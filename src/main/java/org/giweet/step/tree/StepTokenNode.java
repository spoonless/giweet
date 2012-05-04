package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.giweet.step.StepDeclaration;
import org.giweet.step.StepToken;
import org.giweet.step.StepType;
	
public class StepTokenNode<T extends StepDeclaration> {
	
	private static final int STEP_TYPE_COUNT = StepType.values().length;
	
	private final int depth;
	private final StepToken stepToken;
	private StepDeclaration[] stepDeclarations;
	private List<StepTokenNode<T>> nextNodes;
	
	public StepTokenNode(T stepDeclaration) {
		this(stepDeclaration, 0);
	}

	public StepTokenNode(T stepDeclaration, int depth) {
		this.depth = depth;
		StepToken[] tokens = stepDeclaration.getTokens();
		this.stepToken = tokens[depth];
		this.stepDeclarations = new StepDeclaration[STEP_TYPE_COUNT];
		setStepDeclaration(stepDeclaration);
	}

	private void setStepDeclaration(T stepDeclaration) {
		for (int i = 0; i < STEP_TYPE_COUNT; i++) {
			if (stepDeclaration.isOfType(StepType.values()[i])) {
				if (this.stepDeclarations[i] != null) {
					// FIXME big problem here!
					throw new IllegalArgumentException();
				}
				this.stepDeclarations[i] = stepDeclaration;
			}
		}
	}

	public StepToken getStepToken() {
		return stepToken;
	}

	public boolean matches(T stepDeclaration) {
		StepToken[] tokens = stepDeclaration.getTokens();
		return depth < tokens.length && tokens[depth].equals(stepToken);
	}

	public boolean add(T newStepDeclaration) {
		if (matches(newStepDeclaration)) {
			createNextNodeIfNecessary();
			StepToken[] tokens = newStepDeclaration.getTokens();
			if (depth + 1 == tokens.length) {
				setStepDeclaration(newStepDeclaration);
			}
			else {
				if (nextNodes == null) {
					nextNodes = new ArrayList<StepTokenNode<T>>();
					nextNodes.add(new StepTokenNode<T>(newStepDeclaration, depth + 1));
				}
				else {
					StepTokenNode<T> lastNextNode = nextNodes.get(nextNodes.size() - 1);
					if (! lastNextNode.add(newStepDeclaration)) {
						nextNodes.add(new StepTokenNode<T>(newStepDeclaration, depth + 1));
					}
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void createNextNodeIfNecessary() {
		if (nextNodes != null) {
			return;
		}
		for (int i = 0; i < STEP_TYPE_COUNT; i++) {
			T stepDeclaration = (T) this.stepDeclarations[i];
			if (stepDeclaration != null) {
				if (stepDeclaration.getTokens().length > depth + 1) {
					this.nextNodes = new ArrayList<StepTokenNode<T>>();
					StepTokenNode<T> newStepTokenNode = new StepTokenNode<T>(stepDeclaration, depth+1);
					this.nextNodes.add(newStepTokenNode);
					this.stepDeclarations = new StepDeclaration[STEP_TYPE_COUNT];
				}
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T search(StepType stepType, MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T result = null;
		
		if (this.stepToken.isDynamic()) {
			meaningfulStepTokenIterator.markCurrentAsStepTokenValue(depth);
		}
		
		T stepDeclaration = (T) stepDeclarations[stepType.ordinal()]; 
		if (! meaningfulStepTokenIterator.hasNext()) {
			if (stepDeclaration != null) {
				result = stepDeclaration.getTokens().length == depth + 1 ? stepDeclaration : null;
			}
		}
		else if (nextNodes != null) {
			result = searchThroughNextNodes(stepType, meaningfulStepTokenIterator);
		}
		else if (stepDeclaration != null) {
			result = searchOnlyFromStepDeclaration(stepDeclaration, meaningfulStepTokenIterator);
		}
		return result;
	}

	private T searchThroughNextNodes(StepType stepType, MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T result = null;
		StepToken nextStepToken = meaningfulStepTokenIterator.next();
		StepTokenNode<T> stepTokenNode = StepTokenNode.search(nextStepToken, nextNodes);
		if (stepTokenNode != null) {
			result = stepTokenNode.search(stepType, meaningfulStepTokenIterator);
		}
		if (result == null) {
			StepTokenNode<T> lastStepTokenNode = nextNodes.get(nextNodes.size() - 1);
			if (lastStepTokenNode.stepToken.isDynamic()) {
				result = lastStepTokenNode.search(stepType, meaningfulStepTokenIterator);
			}
		}
		if (result == null && stepToken.isDynamic()) {
			result = this.search(stepType, meaningfulStepTokenIterator);
		}
		if (result == null) {
			meaningfulStepTokenIterator.previous();
		}
		return result;
	}

	private T searchOnlyFromStepDeclaration(T stepDeclaration, MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		int currentDepth;
		StepToken previousStepToken = stepToken;
		StepToken[] stepDeclarationTokens = stepDeclaration.getTokens();
		int nbNextCall = 0 ;
		for (currentDepth = depth + 1 ; meaningfulStepTokenIterator.hasNext() && currentDepth < stepDeclarationTokens.length ; ) {
			StepToken nextStepToken = stepDeclarationTokens[currentDepth];
			StepToken nextStepToken2 = meaningfulStepTokenIterator.next();
			nbNextCall++;
			if (! nextStepToken.equals(nextStepToken2)) {
				if (nextStepToken.isDynamic()) {
					meaningfulStepTokenIterator.markCurrentAsStepTokenValue(currentDepth);
				}
				else if (previousStepToken.isDynamic()) {
					meaningfulStepTokenIterator.markCurrentAsStepTokenValue(currentDepth - 1);
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
			if (currentDepth == stepDeclarationTokens.length) {
				result = stepDeclaration;
			}
		}
		else if (previousStepToken.isDynamic()) {
			while (meaningfulStepTokenIterator.hasNext()) {
				meaningfulStepTokenIterator.next();
				meaningfulStepTokenIterator.markCurrentAsStepTokenValue(currentDepth - 1);
			}
			result = stepDeclaration;
		}
		else {
			for (int i = 0 ; i < nbNextCall ; i++) {
				meaningfulStepTokenIterator.previous();
			}
		}
		return result;
	}

	public static <T extends StepDeclaration> StepTokenNode<T> search(StepToken stepToken, List<StepTokenNode<T>> stepTokenNodes) {
		StepTokenNode<T> result = null;
		if (!stepTokenNodes.isEmpty()) {
			int index = Collections.binarySearch(stepTokenNodes, stepToken, COMPARATOR);
			if (index >= 0) {
				result = stepTokenNodes.get(index);
			}
		}
		return result;
	}
	
	private static final StepTokenNodeStepTokenComparator COMPARATOR = new StepTokenNodeStepTokenComparator();

	private static class StepTokenNodeStepTokenComparator implements Comparator<Object> {
		public int compare(Object node, Object token) {
			StepToken stepToken = ((StepTokenNode<?>)node).getStepToken();
			return stepToken.compareTo((StepToken) token);
		}
	}
}
