package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.giweet.step.ParameterValue;
import org.giweet.step.StepDescriptor;
import org.giweet.step.StepToken;

public class StepTokenTree<T extends StepDescriptor> {
	
	private final List<StepTokenNode<T>> stepTokenNodes = new ArrayList<StepTokenNode<T>>();
	
	public StepTokenTree(List<T> stepDescriptors) {
		Object[] array = stepDescriptors.toArray();
		Arrays.sort(array);
		
		StepTokenNode<T> currentNode = null;
		for (Object currentElement : array) {
			@SuppressWarnings("unchecked")
			T stepDescriptor = (T) currentElement;
			if (stepDescriptor.getTokens().length == 0) {
				// TODO maybe we should filter such descriptor before
				continue;
			}
			if (currentNode == null || ! currentNode.add((T)stepDescriptor)) {
				currentNode = new StepTokenNode<T>((T)stepDescriptor);
				stepTokenNodes.add(currentNode);
			}
		}
	}
	
	public T search (StepToken... stepTokens) {
		T result = null;
		MeaningfulStepTokenIterator meaningfulStepTokenIterator = new MeaningfulStepTokenIterator(stepTokens);
		
		if (stepTokens.length > 0 && meaningfulStepTokenIterator.hasNext()) {
			StepToken nextStepToken = meaningfulStepTokenIterator.next();
			StepTokenNode<T> stepTokenNode = StepTokenNode.search(nextStepToken, stepTokenNodes);
			if (stepTokenNode != null) {
				result = stepTokenNode.search(meaningfulStepTokenIterator);
			}
			if (result == null) {
				StepTokenNode<T> lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.getStepToken().isDynamic()) {
					result = lastStepTokenNode.search(meaningfulStepTokenIterator);
				}
			}
		}
		
		if (result != null) {
			for (ParameterValue parameterValue : meaningfulStepTokenIterator.getParameterValues()) {
				System.out.println("* " + parameterValue.getDynamicTokenPosition() + " = \"" + parameterValue.toString() + "\"");
			}
		}
		
		return result;
	}
}
