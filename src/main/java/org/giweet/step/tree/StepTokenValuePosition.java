package org.giweet.step.tree;

public class StepTokenValuePosition {
	
	private final int dynamicTokenPosition;
	private final int valueTokenStartPosition;
	private int valueTokenEndPosition;
	
	public StepTokenValuePosition(int dynamicTokenPosition, int startPosition) {
		this.dynamicTokenPosition = dynamicTokenPosition;
		this.valueTokenStartPosition = startPosition;
		this.valueTokenEndPosition = startPosition;
	}

	public int getDynamicTokenPosition() {
		return dynamicTokenPosition;
	}

	public int getValueTokenStartPosition() {
		return valueTokenStartPosition;
	}

	public int getValueTokenEndPosition() {
		return valueTokenEndPosition;
	}

	public void setValueTokenEndPosition(int endPosition) {
		this.valueTokenEndPosition = Math.max(this.valueTokenStartPosition, endPosition);
	}
}
