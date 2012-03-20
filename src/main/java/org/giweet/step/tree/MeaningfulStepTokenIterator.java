package org.giweet.step.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.giweet.step.StepToken;

public class MeaningfulStepTokenIterator implements Iterator<StepToken> {
	
	private final StepToken [] stepTokens;
	private int cursor;
	private int nextCursor;
	private int previousCursor;
	private List<StepTokenValuePosition> stepTokenValuePositions = new ArrayList<StepTokenValuePosition>();
	
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
		removeStepTokenValuePosition();
		return stepTokens[cursor];
	}

	private void removeStepTokenValuePosition() {
		for (int i = stepTokenValuePositions.size() - 1 ; i >= 0 ; i--) {
			StepTokenValuePosition stepTokenValuePosition = stepTokenValuePositions.get(i);
			if (stepTokenValuePosition.getValueTokenStartPosition() > cursor) {
				stepTokenValuePositions.remove(i);
			}
			else if (stepTokenValuePosition.getValueTokenEndPosition() > cursor) {
				stepTokenValuePosition.setValueTokenEndPosition(cursor);
			}
			else {
				break;
			}
		}
	}

	public void markCurrentAsStepTokenValue (int dynamicStepTokenPosition) {
		StepTokenValuePosition stepTokenValuePosition = getStepTokenValuePosition(dynamicStepTokenPosition);
		if (stepTokenValuePosition == null) {
			stepTokenValuePosition = new StepTokenValuePosition(dynamicStepTokenPosition, cursor);
			stepTokenValuePositions.add(stepTokenValuePosition);
		}
		else {
			stepTokenValuePosition.setValueTokenEndPosition(cursor);
		}
	}
	
	public List<StepTokenValuePosition> getStepTokenValuePositions() {
		return stepTokenValuePositions;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	private StepTokenValuePosition getStepTokenValuePosition(int dynamicStepTokenPosition) {
		if (! stepTokenValuePositions.isEmpty()) {
			StepTokenValuePosition stepTokenValuePosition = stepTokenValuePositions.get(stepTokenValuePositions.size() - 1);
			return stepTokenValuePosition.getDynamicTokenPosition() == dynamicStepTokenPosition ? stepTokenValuePosition : null;
		}
		return null;
	}
}
