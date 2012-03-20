package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.giweet.step.StepTokenValue;
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
			List<StepTokenValuePosition> stepTokenValuePositions = meaningfulStepTokenIterator.getStepTokenValuePositions();
			StepTokenValue[] stepTokenValues = createStepTokenValueArray(stepTokenValuePositions, stepDescriptorFound, stepTokens);
			searchResult = new SearchResult<T>(stepDescriptorFound, stepTokenValues);
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
				if (lastStepTokenNode.getStepToken().isDynamic()) {
					stepDescriptorFound = lastStepTokenNode.search(meaningfulStepTokenIterator);
				}
			}
		}
		return stepDescriptorFound;
	}

	private StepTokenValue[] createStepTokenValueArray(List<StepTokenValuePosition> stepTokenValuePositions, T stepDescriptorFound, StepToken... stepTokens) {
		StepTokenValue stepTokenValues[] = new StepTokenValue[stepTokenValuePositions.size()];
		for (int i = 0 ; i < stepTokenValues.length ; i++) {
			StepTokenValuePosition stepTokenValuePosition = stepTokenValuePositions.get(i);
			StepTokenValueImpl stepTokenValue = new StepTokenValueImpl(stepTokenValuePosition, stepDescriptorFound.getTokens()[stepTokenValuePosition.getDynamicTokenPosition()], stepTokens);
			stepTokenValues[i] = stepTokenValue;
		}
		return stepTokenValues;
	}
}
