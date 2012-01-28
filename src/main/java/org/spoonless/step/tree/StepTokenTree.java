package org.spoonless.step.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spoonless.step.StepDescriptor;
import org.spoonless.step.StepToken;

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
			if (currentNode == null || ! currentNode.add(stepDescriptor)) {
				currentNode = new StepTokenNode(stepDescriptor);
				getStepTokenNodes().add(currentNode);
			}
		}
	}
	
	public StepDescriptor find (StepToken... stepTokens) {
		StepDescriptor result = null;
		if (stepTokens.length > 0) {
			Finder finder = new Finder(stepTokens);
			StepTokenNode stepTokenNode = finder.findAmongst(stepTokenNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(finder);
			}
		}
		return result;
	}

	public List<StepTokenNode> getStepTokenNodes() {
		return stepTokenNodes;
	}
}
