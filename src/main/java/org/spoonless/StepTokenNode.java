package org.spoonless;

import java.util.ArrayList;
import java.util.List;

public class StepTokenNode {
	
	private final StepToken stepToken;
	private StepDescriptor stepDescriptor;
	private List<StepTokenNode> nextNodes;
	
	public StepTokenNode(StepDescriptor stepDescriptor, int depth) {
		StepToken[] tokens = stepDescriptor.getTokens();
		this.stepToken = tokens[depth];
		if (tokens.length > depth + 1) {
			nextNodes = new ArrayList<StepTokenNode>();
			nextNodes.add(new StepTokenNode(stepDescriptor, depth + 1));
		}
		else {
			this.stepDescriptor = stepDescriptor;
		}
	}

	public boolean matches(StepDescriptor stepDescriptor, int depth) {
		StepToken[] tokens = stepDescriptor.getTokens();
		return depth < tokens.length && tokens[depth].equals(stepToken);
	}

	public boolean add(StepDescriptor stepDescriptor, int depth) {
		if (matches(stepDescriptor, depth)) {
			StepToken[] tokens = stepDescriptor.getTokens();
			if (depth + 1 == tokens.length) {
				if (this.stepDescriptor == null) {
					// FIXME big problem here
				}
				this.stepDescriptor = stepDescriptor;
			}
			else {
				if (nextNodes == null) {
					nextNodes = new ArrayList<StepTokenNode>();
					nextNodes.add(new StepTokenNode(stepDescriptor, depth + 1));
				}
				else {
					StepTokenNode lastNextNode = nextNodes.get(nextNodes.size() - 1);
					if (! lastNextNode.add(stepDescriptor, depth + 1)) {
						nextNodes.add(new StepTokenNode(stepDescriptor, depth + 1));
					}
				}
			}
			return true;
		}
		return false;
	}

	public StepDescriptor search(Finder finder) {
		StepDescriptor result = null;
		finder = finder.goDeeper();
		if (finder == null) {
			result = this.stepDescriptor;
		}
		else if (finder != null && nextNodes == null) {
			if (stepToken.isDynamic()) {
				result = this.stepDescriptor;
			}
		}
		else if (finder != null && nextNodes != null) {
			StepTokenNode stepTokenNode = finder.findAmongst(nextNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(finder);
			}
			else if (stepToken.isDynamic()) {
				result = this.search(finder);
			}
		}
		// TODO one case is missing : when descendants are only argument tokens
		return result;
	}

	public StepDescriptor getStepDescriptor() {
		return stepDescriptor;
	}

	public List<StepTokenNode> getNextNodes() {
		return nextNodes;
	}
	
	public StepToken getStepToken() {
		return stepToken;
	}
}
