package org.spoonless;

import java.util.ArrayList;
import java.util.List;

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
	
	public StepDescriptor search(Finder finder) {
		StepDescriptor result = null;
		finder = finder.goDeeper();
		if (finder == null) {
			if (stepDescriptor != null) {
				result = this.stepDescriptor.getTokens().length == depth + 1 ? this.stepDescriptor : null;
			}
		}
		else if (nextNodes != null) {
			StepTokenNode stepTokenNode = finder.findAmongst(nextNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(finder);
			}
			else if (stepToken.isDynamic()) {
				result = this.search(finder);
			}
		}
		else if (nextNodes == null) {
			result = searchOnlyFromStepDescriptor(finder);
		}
		// TODO one case is missing : when descendants are only argument tokens
		return result;
	}

	private StepDescriptor searchOnlyFromStepDescriptor(Finder finder) {
		int currentDepth;
		StepToken previousStepToken = stepToken;
		StepToken[] stepDescriptorTokens = this.stepDescriptor.getTokens();
		for (currentDepth = depth + 1 ; finder != null && currentDepth < stepDescriptorTokens.length ; ) {
			StepToken nextStepToken = stepDescriptorTokens[currentDepth];
			if (finder.match(nextStepToken)) {
				currentDepth++;
				finder = finder.goDeeper();
			}
			else if (previousStepToken.isDynamic()) {
				finder = finder.goDeeper();
			}
			else {
				break;
			}
			previousStepToken = nextStepToken;
		}
		
		StepDescriptor result = null;
		if (finder == null && currentDepth == stepDescriptorTokens.length) {
			result = this.stepDescriptor;
		}
		else if (finder != null && previousStepToken.isDynamic()) {
			result = this.stepDescriptor;
		}
		return result;
	}

	public StepToken getStepToken() {
		return stepToken;
	}
}
