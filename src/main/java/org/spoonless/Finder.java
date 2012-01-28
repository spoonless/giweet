package org.spoonless;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Finder {
	
	private int depth;
	private final StringToken[] stringTokens;
	private final StringTokenStepTokenNodeComparator comparator = new StringTokenStepTokenNodeComparator();
	
	public Finder(StringToken... stringTokens) {
		this.stringTokens = stringTokens;
	}
	
	public StepTokenNode findAmongst(List<StepTokenNode> stepTokenNodes) {
		StepTokenNode result = null;
		if (!stepTokenNodes.isEmpty()) {
			int index = Collections.binarySearch(stepTokenNodes, stringTokens[depth], comparator);
			if (index < 0) {
				StepTokenNode lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.getStepToken() instanceof ArgumentToken) {
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
		return depth < stringTokens.length ? this : null;
	}
	
	private static class StringTokenStepTokenNodeComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			StepTokenNode stepTokenNode = (StepTokenNode) o1;
			StepToken stepToken = stepTokenNode.getStepToken();
			return ((StepToken) o2).compareTo(stepToken);
		}
	}
}