package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.giweet.step.StepDescriptor;
import org.giweet.step.StepToken;
	
public class StepTokenNode<T extends StepDescriptor> {
	private final int depth;
	private final StepToken stepToken;
	private T stepDescriptor;
	private List<StepTokenNode<T>> nextNodes;
	
	public StepTokenNode(T stepDescriptor) {
		this(stepDescriptor, 0);
	}

	public StepTokenNode(T stepDescriptor, int depth) {
		this.depth = depth;
		StepToken[] tokens = stepDescriptor.getTokens();
		this.stepToken = tokens[depth];
		this.stepDescriptor = stepDescriptor;
	}

	public StepToken getStepToken() {
		return stepToken;
	}

	public boolean matches(T stepDescriptor) {
		StepToken[] tokens = stepDescriptor.getTokens();
		return depth < tokens.length && tokens[depth].equals(stepToken);
	}

	public boolean add(T newStepDescriptor) {
		if (matches(newStepDescriptor)) {
			createNextNodeIfNecessary();
			StepToken[] tokens = newStepDescriptor.getTokens();
			if (depth + 1 == tokens.length) {
				if (this.stepDescriptor == null) {
					// FIXME big problem here
				}
				this.stepDescriptor = newStepDescriptor;
			}
			else {
				if (nextNodes == null) {
					nextNodes = new ArrayList<StepTokenNode<T>>();
					nextNodes.add(new StepTokenNode<T>(newStepDescriptor, depth + 1));
				}
				else {
					StepTokenNode<T> lastNextNode = nextNodes.get(nextNodes.size() - 1);
					if (! lastNextNode.add(newStepDescriptor)) {
						nextNodes.add(new StepTokenNode<T>(newStepDescriptor, depth + 1));
					}
				}
			}
			return true;
		}
		return false;
	}

	private void createNextNodeIfNecessary() {
		if (this.stepDescriptor != null) {
			if (this.stepDescriptor.getTokens().length > depth + 1) {
				this.nextNodes = new ArrayList<StepTokenNode<T>>();
				this.nextNodes.add(new StepTokenNode<T>(this.stepDescriptor, depth+1));
				this.stepDescriptor = null;
			}
		}
	}
	
	public T search(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T result = null;
		
		if (this.stepToken.isParameter()) {
			meaningfulStepTokenIterator.markCurrentAsParameter(depth);
		}
		
		if (! meaningfulStepTokenIterator.hasNext()) {
			if (stepDescriptor != null) {
				result = this.stepDescriptor.getTokens().length == depth + 1 ? this.stepDescriptor : null;
			}
		}
		else if (nextNodes != null) {
			result = searchThroughNextNodes(meaningfulStepTokenIterator);
		}
		else {
			result = searchOnlyFromStepDescriptor(meaningfulStepTokenIterator);
		}
		return result;
	}

	private T searchThroughNextNodes(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T result = null;
		StepToken nextStepToken = meaningfulStepTokenIterator.next();
		StepTokenNode<T> stepTokenNode = StepTokenNode.search(nextStepToken, nextNodes);
		if (stepTokenNode != null) {
			result = stepTokenNode.search(meaningfulStepTokenIterator);
		}
		if (result == null) {
			StepTokenNode<T> lastStepTokenNode = nextNodes.get(nextNodes.size() - 1);
			if (lastStepTokenNode.stepToken.isParameter()) {
				result = lastStepTokenNode.search(meaningfulStepTokenIterator);
			}
		}
		if (result == null && stepToken.isParameter()) {
			result = this.search(meaningfulStepTokenIterator);
		}
		if (result == null) {
			meaningfulStepTokenIterator.previous();
		}
		return result;
	}

	private T searchOnlyFromStepDescriptor(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		int currentDepth;
		StepToken previousStepToken = stepToken;
		StepToken[] stepDescriptorTokens = this.stepDescriptor.getTokens();
		int nbNextCall = 0 ;
		for (currentDepth = depth + 1 ; meaningfulStepTokenIterator.hasNext() && currentDepth < stepDescriptorTokens.length ; ) {
			StepToken nextStepToken = stepDescriptorTokens[currentDepth];
			StepToken nextStepToken2 = meaningfulStepTokenIterator.next();
			nbNextCall++;
			if (! nextStepToken.equals(nextStepToken2)) {
				if (nextStepToken.isParameter()) {
					meaningfulStepTokenIterator.markCurrentAsParameter(currentDepth);
				}
				else if (previousStepToken.isParameter()) {
					meaningfulStepTokenIterator.markCurrentAsParameter(currentDepth - 1);
					continue;
				}
				else {
					break;
				}
			}
			currentDepth++;
			previousStepToken = nextStepToken;
		}
		
		T result = null;
		if (! meaningfulStepTokenIterator.hasNext()) {
			if (currentDepth == stepDescriptorTokens.length) {
				result = this.stepDescriptor;
			}
		}
		else if (previousStepToken.isParameter()) {
			while (meaningfulStepTokenIterator.hasNext()) {
				meaningfulStepTokenIterator.next();
				meaningfulStepTokenIterator.markCurrentAsParameter(currentDepth - 1);
			}
			result = this.stepDescriptor;
		}
		else {
			for (int i = 0 ; i < nbNextCall ; i++) {
				meaningfulStepTokenIterator.previous();
			}
		}
		return result;
	}

	public static <T extends StepDescriptor> StepTokenNode<T> search(StepToken stepToken, List<StepTokenNode<T>> stepTokenNodes) {
		StepTokenNode<T> result = null;
		if (!stepTokenNodes.isEmpty()) {
			int index = Collections.binarySearch(stepTokenNodes, stepToken, comparator);
			if (index >= 0) {
				result = stepTokenNodes.get(index);
			}
		}
		return result;
	}
	
	private static final StepTokenNodeStepTokenComparator comparator = new StepTokenNodeStepTokenComparator();

	private static class StepTokenNodeStepTokenComparator implements Comparator<Object> {
		public int compare(Object node, Object token) {
			StepToken stepToken = ((StepTokenNode<?>)node).getStepToken();
			return stepToken.compareTo((StepToken) token);
		}
	}
}
