package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.giweet.step.StepInstance;
import org.giweet.step.StepTokenValue;
import org.giweet.step.StepDeclaration;
import org.giweet.step.StepToken;
import org.giweet.step.StepType;

public class StepDeclarationTree<T extends StepDeclaration> {
	
	private final List<StepTokenNode<T>> stepTokenNodes = new ArrayList<StepTokenNode<T>>();
	
	public StepDeclarationTree(Collection<T> stepDeclarations) {
		Object[] array = stepDeclarations.toArray();
		Arrays.sort(array);
		
		StepTokenNode<T> currentNode = null;
		for (Object currentElement : array) {
			@SuppressWarnings("unchecked")
			T stepDeclaration = (T) currentElement;
			if (stepDeclaration.getTokens().length == 0) {
				// TODO maybe we should filter such descriptor before
				continue;
			}
			if (currentNode == null || ! currentNode.add((T)stepDeclaration)) {
				currentNode = new StepTokenNode<T>((T)stepDeclaration);
				stepTokenNodes.add(currentNode);
			}
		}
	}
	
	public SearchResult<T> search (StepInstance stepInstance) {
		MeaningfulStepTokenIterator meaningfulStepTokenIterator = new MeaningfulStepTokenIterator(stepInstance.getTokens());
		T stepDeclarationFound = search(stepInstance.getType(), meaningfulStepTokenIterator);
		
		SearchResult<T> searchResult = null;
		if (stepDeclarationFound != null) {
			List<StepTokenValuePosition> stepTokenValuePositions = meaningfulStepTokenIterator.getStepTokenValuePositions();
			StepTokenValue[] stepTokenValues = createStepTokenValueArray(stepTokenValuePositions, stepDeclarationFound, stepInstance.getTokens());
			searchResult = new SearchResult<T>(stepDeclarationFound, stepTokenValues);
		}
		return searchResult;
	}

	private T search(StepType stepType, MeaningfulStepTokenIterator meaningfulStepTokenIterator) {
		T stepDeclarationFound = null;
		if (meaningfulStepTokenIterator.hasNext()) {
			StepToken nextStepToken = meaningfulStepTokenIterator.next();
			StepTokenNode<T> stepTokenNode = StepTokenNode.search(nextStepToken, stepTokenNodes);
			if (stepTokenNode != null) {
				stepDeclarationFound = stepTokenNode.search(stepType, meaningfulStepTokenIterator);
			}
			if (stepDeclarationFound == null && ! stepTokenNodes.isEmpty()) {
				StepTokenNode<T> lastStepTokenNode = stepTokenNodes.get(stepTokenNodes.size() - 1);
				if (lastStepTokenNode.getStepToken().isDynamic()) {
					stepDeclarationFound = lastStepTokenNode.search(stepType, meaningfulStepTokenIterator);
				}
			}
		}
		return stepDeclarationFound;
	}

	private StepTokenValue[] createStepTokenValueArray(List<StepTokenValuePosition> stepTokenValuePositions, T stepDeclarationFound, StepToken... stepTokens) {
		StepTokenValue stepTokenValues[] = new StepTokenValue[stepTokenValuePositions.size()];
		for (int i = 0 ; i < stepTokenValues.length ; i++) {
			StepTokenValuePosition stepTokenValuePosition = stepTokenValuePositions.get(i);
			StepTokenValueImpl stepTokenValue = new StepTokenValueImpl(stepTokenValuePosition, stepDeclarationFound.getTokens()[stepTokenValuePosition.getDynamicTokenPosition()], stepTokens);
			stepTokenValues[i] = stepTokenValue;
		}
		return stepTokenValues;
	}
}
