package org.spoonless;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepTokenTree {
	
	private final List<StepTokenNode> stepTokenNodes = new ArrayList<StepTokenNode>();
	
	public StepTokenTree(List<StepDescriptor> stepDescriptors) {
		StepDescriptor[] array = stepDescriptors.toArray(new StepDescriptor[stepDescriptors.size()]);
		Arrays.sort(array);
		
		StepTokenNode currentNode = null;
		for (StepDescriptor stepDescriptor : array) {
			if (stepDescriptor.getTokens().length == 0) {
				// TODO maybe we should filter such descriptor before
				continue;
			}
			if (currentNode == null || ! currentNode.add(stepDescriptor, 0)) {
				currentNode = new StepTokenNode(stepDescriptor, 0);
				getStepTokenNodes().add(currentNode);
			}
		}
	}
	
	public StepDescriptor find (StringToken... stringTokens) {
		StepDescriptor result = null;
		Finder finder = new Finder(stringTokens);
		StepTokenNode stepTokenNode = finder.findAmongst(stepTokenNodes);
		if (stepTokenNode != null) {
			result = stepTokenNode.search(finder);
		}
		return result;
	}

	public List<StepTokenNode> getStepTokenNodes() {
		return stepTokenNodes;
	}
}
