package org.giwit.step.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.giwit.step.ParameterValue;
import org.giwit.step.StepToken;

public class MeaningfulStepTokenIterator implements Iterator<StepToken> {
	
	private final StepToken [] stepTokens;
	private int cursor;
	private int nextCursor;
	private int previousCursor;
	private List<ParameterValuePosition> parameterValuePositions = new ArrayList<ParameterValuePosition>();
	
	public MeaningfulStepTokenIterator(StepToken... stepTokens) {
		this.stepTokens = stepTokens;
		this.cursor = -1;
		this.nextCursor = cursor;
		this.previousCursor = cursor;
	}

	public boolean hasNext() {
		nextCursor = getNextCursor();
		return nextCursor < stepTokens.length;
	}

	public boolean hasPrevious() {
		previousCursor = getPreviousCursor();
		return previousCursor >= 0;
	}

	private int getNextCursor() {
		for (int i = cursor + 1 ; i < stepTokens.length ; i++) {
			if (stepTokens[i].isMeaningful()) {
				return i;
			}
		}
		return stepTokens.length;
	}
	
	private int getPreviousCursor() {
		for (int i = cursor - 1 ; i >= 0 ; i--) {
			if (stepTokens[i].isMeaningful()) {
				return i;
			}
		}
		return -1;
	}

	public StepToken next() {
		if (nextCursor <= cursor) {
			nextCursor = getNextCursor();
		}
		if (nextCursor == stepTokens.length) {
			throw new NoSuchElementException();
		}
		previousCursor = cursor;
		cursor = nextCursor;
		return stepTokens[cursor];
	}
	
	public StepToken previous() {
		if (previousCursor >= cursor) {
			previousCursor = getPreviousCursor();
		}
		if (previousCursor == -1) {
			throw new NoSuchElementException();
		}
		nextCursor = cursor;
		cursor = previousCursor;
		updateParameterValuePosition();
		return stepTokens[cursor];
	}

	private void updateParameterValuePosition() {
		for (int i = parameterValuePositions.size() - 1 ; i >= 0 ; i--) {
			ParameterValuePosition parameterValuePosition = parameterValuePositions.get(i);
			if (parameterValuePosition.getStartPosition() > cursor) {
				parameterValuePositions.remove(i);
			}
			else if (parameterValuePosition.getEndPosition() > cursor) {
				parameterValuePosition.setEndPosition(cursor);
			}
			else {
				break;
			}
		}
	}

	public void markCurrentAsParameter (int dynamicTokenPosition) {
		ParameterValuePosition parameterValuePosition = getParameterValuePosition(dynamicTokenPosition);
		if (parameterValuePosition == null) {
			parameterValuePosition = new ParameterValuePosition(dynamicTokenPosition, cursor);
			parameterValuePositions.add(parameterValuePosition);
		}
		else {
			parameterValuePosition.setEndPosition(cursor);
		}
	}
	
	public ParameterValue[] getParameterValues() {
		ParameterValue[] parameterValues = new ParameterValue[parameterValuePositions.size()];
		for (int i = 0 ; i < parameterValues.length ; i++) {
			ParameterValuePosition parameterValuePosition = parameterValuePositions.get(i);
			ParameterValue parameterValue = new ParameterValue(parameterValuePosition.getDynamicTokenPosition(), parameterValuePosition.getStartPosition());
			for (int j = parameterValuePosition.getStartPosition() ; j <= parameterValuePosition.getEndPosition() ; j++) {
				parameterValue.add(stepTokens[j]);
			}
			parameterValues[i] = parameterValue;
		}
		return parameterValues;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private ParameterValuePosition getParameterValuePosition(int dynamicTokenPosition) {
		if (! parameterValuePositions.isEmpty()) {
			ParameterValuePosition parameterValue = parameterValuePositions.get(parameterValuePositions.size() - 1);
			return parameterValue.getDynamicTokenPosition() == dynamicTokenPosition ? parameterValue : null;
		}
		return null;
	}
}
