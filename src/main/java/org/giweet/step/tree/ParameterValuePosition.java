package org.giweet.step.tree;

public class ParameterValuePosition {
	
	private final int parameterTokenPosition;
	private final int valueTokenStartPosition;
	private int valueTokenEndPosition;
	
	public ParameterValuePosition(int parameterTokenPosition, int startPosition) {
		this.parameterTokenPosition = parameterTokenPosition;
		this.valueTokenStartPosition = startPosition;
		this.valueTokenEndPosition = startPosition;
	}

	public int getParameterTokenPosition() {
		return parameterTokenPosition;
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
