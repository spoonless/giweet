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
	
	public SearchResult<T> search (StepToken... stepTokens) {
		MeaningfulStepTokenIterator meaningfulStepTokenIterator = new MeaningfulStepTokenIterator(stepTokens);
		T stepDescriptorFound = search(meaningfulStepTokenIterator);
		
		SearchResult<T> searchResult = null;
		if (stepDescriptorFound != null) {
			List<ParameterValuePosition> parameterValuePositions = meaningfulStepTokenIterator.getParameterValuePositions();
			ParameterValue[] parameterValues = createParameterValueArray(parameterValuePositions, stepDescriptorFound, stepTokens);
			searchResult = new SearchResult<T>(stepDescriptorFound, parameterValues);
		}
		return searchResult;
	}

	private T search(MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T stepDescriptorFound = null;
		if (meaningfulStepTokenIterator.hasNext()) {
			StepToken nextStepToken = meaningfulStepTokenIterator.next();
			StepTokenNode<T> stepTokenNode = StepTokenNode.search(nextStepToken, stepTokenNodes);
			if (stepTokenNode != null) {
				stepDescriptorFound = stepTokenNode.search(meaningfulStepTokenIterator);
			}
			if (stepDescriptorFound == null) {
				StepTokenNode<T> lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.getStepToken().isParameter()) {
					stepDescriptorFound = lastStepTokenNode.search(meaningfulStepTokenIterator);
				}
			}
		}
		return stepDescriptorFound;
	}

	private ParameterValue[] createParameterValueArray(List<ParameterValuePosition> parameterValuePositions, T stepDescriptorFound, StepToken... stepTokens) {
		ParameterValue parameterValues[] = new ParameterValue[parameterValuePositions.size()];
		for (int i = 0 ; i < parameterValues.length ; i++) {
			ParameterValuePosition parameterValuePosition = parameterValuePositions.get(i);
			ParameterValueImpl parameterValue = new ParameterValueImpl(parameterValuePosition, stepDescriptorFound.getTokens()[parameterValuePosition.getParameterTokenPosition()], stepTokens);
			parameterValues[i] = parameterValue;
		}
		return parameterValues;
	}
}
