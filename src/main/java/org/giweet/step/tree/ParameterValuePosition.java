package org.giweet.step.tree;

public class ParameterValuePosition {
	
	private final int dynamicTokenPosition;
	private final int startPosition;
	private int endPosition;
	
	public ParameterValuePosition(int dynamicTokenPosition, int startPosition) {
		this.dynamicTokenPosition = dynamicTokenPosition;
		this.startPosition = startPosition;
		this.endPosition = startPosition;
	}

	public int getDynamicTokenPosition() {
		return dynamicTokenPosition;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = Math.max(this.startPosition, endPosition);
	}
}
