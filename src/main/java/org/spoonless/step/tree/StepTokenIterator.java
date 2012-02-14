package org.spoonless.step.tree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.spoonless.step.StepToken;

public class StepTokenIterator implements Iterator<StepToken> {
	
	private final StepToken [] stepTokens;
	private int cursor;
	private int nextCursor;
	private int startParameter;
	
	public StepTokenIterator(StepToken[] stepTokens) {
		this.stepTokens = stepTokens;
		this.cursor = -1;
		this.nextCursor = cursor;
		this.startParameter = Integer.MAX_VALUE;
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
	
	public void startParameter() {
		if (startParameter > cursor) {
			startParameter = cursor;
		}
	}
	
	public void endParameter() {
		StepToken[] arrayFromMarkToCurrent = subArrayFromMarkToCurrent();
		if (arrayFromMarkToCurrent.length > 0) {
			System.out.println(Arrays.toString(arrayFromMarkToCurrent));
		}
		startParameter = Integer.MAX_VALUE;
	}
	
	public StepToken[] subArrayFromMarkToCurrent() {
		int size = Math.max(0, cursor - startParameter);
		StepToken[] result = new StepToken[size];
		
		for (int i = 0 ; i < size ; i++) {
			result[i] = stepTokens[i + startParameter];
		}
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
