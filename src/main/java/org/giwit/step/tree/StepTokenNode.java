package org.giwit.step.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.giwit.step.StepDescriptor;
import org.giwit.step.StepToken;
	
public class StepTokenNode {
	private final int depth;
	private final StepToken stepToken;
	private StepDescriptor stepDescriptor;
	private List<StepTokenNode> nextNodes;
	
	public StepTokenNode(StepDescriptor stepDescriptor) {
		this(stepDescriptor, 0);
	}

	public StepTokenNode(StepDescriptor stepDescriptor, int depth) {
		this.depth = depth;
		StepToken[] tokens = stepDescriptor.getTokens();
		this.stepToken = tokens[depth];
		this.stepDescriptor = stepDescriptor;
	}

	public StepToken getStepToken() {
		return stepToken;
	}

	public boolean matches(StepDescriptor stepDescriptor) {
		StepToken[] tokens = stepDescriptor.getTokens();
		return depth < tokens.length && tokens[depth].equals(stepToken);
	}

	public boolean add(StepDescriptor newStepDescriptor) {
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
					nextNodes = new ArrayList<StepTokenNode>();
					nextNodes.add(new StepTokenNode(newStepDescriptor, depth + 1));
				}
				else {
					StepTokenNode lastNextNode = nextNodes.get(nextNodes.size() - 1);
					if (! lastNextNode.add(newStepDescriptor)) {
						nextNodes.add(new StepTokenNode(newStepDescriptor, depth + 1));
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
				this.nextNodes = new ArrayList<StepTokenNode>();
				this.nextNodes.add(new StepTokenNode(this.stepDescriptor, depth+1));
				this.stepDescriptor = null;
			}
		}
	}
	
	public StepDescriptor search(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		StepDescriptor result = null;
		
		if (this.stepToken.isDynamic()) {
			meaningfulStepTokenIterator.markCurrentAsParameter(depth);
		}
		
		if (! meaningfulStepTokenIterator.hasNext()) {
			if (stepDescriptor != null) {
				result = this.stepDescriptor.getTokens().length == depth + 1 ? this.stepDescriptor : null;
			}
		}
		else if (nextNodes != null) {
			StepToken nextStepToken = meaningfulStepTokenIterator.next();
			StepTokenNode stepTokenNode = StepTokenNode.find(nextStepToken, nextNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(meaningfulStepTokenIterator);
			}
			if (result == null) {
				StepTokenNode lastStepTokenNode = nextNodes.get(nextNodes.size() - 1);
				if (lastStepTokenNode.stepToken.isDynamic()) {
					result = lastStepTokenNode.search(meaningfulStepTokenIterator);
				}
			}
			if (result == null && stepToken.isDynamic()) {
				result = this.search(meaningfulStepTokenIterator);
			}
			if (result == null) {
				meaningfulStepTokenIterator.previous();
			}
		}
		else if (nextNodes == null) {
			result = searchOnlyFromStepDescriptor(meaningfulStepTokenIterator);
		}
		return result;
	}

	private StepDescriptor searchOnlyFromStepDescriptor(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		int currentDepth;
		StepToken previousStepToken = stepToken;
		StepToken[] stepDescriptorTokens = this.stepDescriptor.getTokens();
		int nbNextCall = 0 ;
		for (currentDepth = depth + 1 ; meaningfulStepTokenIterator.hasNext() && currentDepth < stepDescriptorTokens.length ; ) {
			StepToken nextStepToken = stepDescriptorTokens[currentDepth];
			StepToken nextStepToken2 = meaningfulStepTokenIterator.next();
			nbNextCall++;
			if (! nextStepToken.equals(nextStepToken2)) {
				if (nextStepToken.isDynamic()) {
					meaningfulStepTokenIterator.markCurrentAsParameter(currentDepth);
				}
				else if (previousStepToken.isDynamic()) {
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
		
		StepDescriptor result = null;
		if (! meaningfulStepTokenIterator.hasNext()) {
			if (currentDepth == stepDescriptorTokens.length) {
				result = this.stepDescriptor;
			}
		}
		else if (previousStepToken.isDynamic()) {
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

	public static StepTokenNode find(StepToken stepToken, List<StepTokenNode> stepTokenNodes) {
		StepTokenNode result = null;
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
			StepToken stepToken = ((StepTokenNode)node).getStepToken();
			return stepToken.compareTo((StepToken) token);
		}
	}
}
