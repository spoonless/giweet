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
				stepTokenNodes.add(currentNode);
			}
		}
	}
	
	public StepDescriptor find (StepToken... stepTokens) {
		StepDescriptor result = null;
		StepTokenIterator stepTokenIterator = new StepTokenIterator(stepTokens);
		
		if (stepTokens.length > 0 && stepTokenIterator.hasNext()) {
			StepToken nextStepToken = stepTokenIterator.next();
			StepTokenNode stepTokenNode = StepTokenNode.find(nextStepToken, stepTokenNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(stepTokenIterator);
			}
		}
		if (result != null) {
			for (; stepTokenIterator.hasNext() ; stepTokenIterator.next());
			stepTokenIterator.endParameter();
		}
		return result;
	}
}
