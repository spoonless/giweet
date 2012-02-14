package org.spoonless.step.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.spoonless.step.StepDescriptor;
import org.spoonless.step.StepToken;
	
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
	
	public StepDescriptor search(StepTokenIterator stepTokenIterator) {
		StepDescriptor result = null;
		
		if (this.stepToken.isDynamic()) {
			stepTokenIterator.startParameter();
		}
		
		if (! stepTokenIterator.hasNext()) {
			if (stepDescriptor != null) {
				result = this.stepDescriptor.getTokens().length == depth + 1 ? this.stepDescriptor : null;
			}
		}
		else  if (nextNodes != null) {
			StepToken nextStepToken = stepTokenIterator.next();
			StepTokenNode stepTokenNode = StepTokenNode.find(nextStepToken, nextNodes);
			if (stepTokenNode != null) {
				if (this.stepToken.isDynamic()) {
					stepTokenIterator.endParameter();
				}
				result = stepTokenNode.search(stepTokenIterator);
			}
			else if (stepToken.isDynamic()) {
				result = this.search(stepTokenIterator);
			}
		}
		else if (nextNodes == null) {
			result = searchOnlyFromStepDescriptor(stepTokenIterator);
		}
		// TODO one case is missing : when descendants are only argument tokens
		return result;
	}

	private StepDescriptor searchOnlyFromStepDescriptor(StepTokenIterator stepTokenIterator) {
		int currentDepth;
		StepToken previousStepToken = stepToken;
		StepToken[] stepDescriptorTokens = this.stepDescriptor.getTokens();
		for (currentDepth = depth + 1 ; stepTokenIterator.hasNext() && currentDepth < stepDescriptorTokens.length ; ) {
			StepToken nextStepToken = stepDescriptorTokens[currentDepth];
			StepToken nextStepToken2 = stepTokenIterator.next();
			if (nextStepToken.equals(nextStepToken2)) {
				currentDepth++;
				stepTokenIterator.endParameter();
			}
			else if (nextStepToken.isDynamic()) {
				currentDepth++;
				stepTokenIterator.startParameter();
			}
			else if (! previousStepToken.isDynamic()) {
				break;
			}
			previousStepToken = nextStepToken;
		}
		
		StepDescriptor result = null;
		if (! stepTokenIterator.hasNext()) {
			if (currentDepth == stepDescriptorTokens.length) {
				result = this.stepDescriptor;
			}
		}
		else if (previousStepToken.isDynamic()) {
			result = this.stepDescriptor;
		}
		return result;
	}

	public static StepTokenNode find(StepToken stepToken, List<StepTokenNode> stepTokenNodes) {
		StepTokenNode result = null;
		if (!stepTokenNodes.isEmpty()) {
			int index = Collections.binarySearch(stepTokenNodes, stepToken, comparator);
			if (index < 0) {
				StepTokenNode lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.stepToken.isDynamic()) {
					result = lastStepTokenNode;
				}
			} else {
				result = stepTokenNodes.get(index);
			}
		}
		return result;
	}
	
	private static final StepTokenNodeStepTokenComparator comparator = new StepTokenNodeStepTokenComparator();

	private static class StepTokenNodeStepTokenComparator implements Comparator<Object> {
		public int compare(Object node, Object token) {
			StepToken stepToken = ((StepTokenNode)node).stepToken;
			return stepToken.compareTo((StepToken) token);
		}
	}
}
