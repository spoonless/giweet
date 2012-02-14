package org.spoonless.step.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.spoonless.step.ParameterValue;
import org.spoonless.step.StepToken;

public class StepTokenIterator implements Iterator<StepToken> {
	
	private final StepToken [] stepTokens;
	private int cursor;
	private int nextCursor;
	private List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
	private int lastTokenAsParameterValueCursor = -1;
	
	public StepTokenIterator(StepToken[] stepTokens) {
		this.stepTokens = stepTokens;
		this.cursor = -1;
		this.nextCursor = cursor;
	}

	public boolean hasNext() {
		nextCursor = getNextCursor();
		return nextCursor < stepTokens.length;
	}

	private int getNextCursor() {
		for (int i = cursor + 1 ; i < stepTokens.length ; i++) {
			if (stepTokens[i].isMeaningful()) {
				return i;
			}
		}
		return stepTokens.length;
	}
	
	public StepToken next() {
		if (nextCursor <= cursor) {
			nextCursor = getNextCursor();
		}
		if (nextCursor == stepTokens.length) {
			throw new NoSuchElementException();
		}
		cursor = nextCursor;
		return stepTokens[cursor];
	}
	
	public void markCurrentAsParameter (int dynamicTokenPosition) {
		ParameterValue parameterValue = getParameterValue(dynamicTokenPosition);
		if (parameterValue == null) {
			parameterValue = new ParameterValue(dynamicTokenPosition);
			parameterValues.add(parameterValue);
			parameterValue.add(stepTokens[cursor]);
			lastTokenAsParameterValueCursor = cursor;
		}
		else {
			for (int i = lastTokenAsParameterValueCursor + 1 ; i <= cursor ; i++) {
				parameterValue.add(stepTokens[i]);
			}
			lastTokenAsParameterValueCursor = cursor;
		}
	}
	
	public ParameterValue[] getParameterValues() {
		return parameterValues.toArray(new ParameterValue[parameterValues.size()]);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private ParameterValue getParameterValue(int dynamicTokenPosition) {
		if (! parameterValues.isEmpty()) {
			ParameterValue parameterValue = parameterValues.get(parameterValues.size() - 1);
			return parameterValue.getDynamicTokenPosition() == dynamicTokenPosition ? parameterValue : null;
		}
		return null;
	}

}
