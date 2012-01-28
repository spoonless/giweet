package org.spoonless;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Finder {
	
	private int depth;
	private final StepToken[] stepTokens;
	private final StepTokenNodeStepTokenComparator comparator = new StepTokenNodeStepTokenComparator();
	
	public Finder(StepToken... stepTokens) {
		this.stepTokens = stepTokens;
	}
	
	public boolean match(StepToken stepToken) {
		boolean equals = stepTokens[depth].equals(stepToken);
		if (! equals && stepToken.isDynamic()) {
			equals = true;
		}
		return equals;
	}
	
	public StepTokenNode findAmongst(List<StepTokenNode> stepTokenNodes) {
		StepTokenNode result = null;
		if (!stepTokenNodes.isEmpty()) {
			int index = Collections.binarySearch(stepTokenNodes, stepTokens[depth], comparator);
			if (index < 0) {
				StepTokenNode lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.getStepToken().isDynamic()) {
					result = lastStepTokenNode;
				}
			} else {
				result = stepTokenNodes.get(index);
			}
		}
		return result;
	}
	
	public Finder goDeeper() {
		depth++;
		return depth < stepTokens.length ? this : null;
	}
	
	private static class StepTokenNodeStepTokenComparator implements Comparator<Object> {
		public int compare(Object node, Object token) {
			StepToken stepToken = ((StepTokenNode)node).getStepToken();
			return stepToken.compareTo((StepToken) token);
		}
	}
}