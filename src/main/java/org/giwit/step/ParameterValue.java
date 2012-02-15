package org.giwit.step;

public class ParameterValue {
	
	private final int dynamicTokenPosition;
	private final StringBuilder stringBuilder;
	private String value;

	public ParameterValue(int dynamicTokenPosition) {
		this.dynamicTokenPosition = dynamicTokenPosition;
		this.stringBuilder = new StringBuilder();
	}
	
	public void add (StepToken stepToken) {
		stringBuilder.append(stepToken);
	}
	
	public int getDynamicTokenPosition() {
		return dynamicTokenPosition;
	}

	@Override
	public String toString() {
		if (value == null) {
			value = stringBuilder.toString();
		}
		return value;
	}
}
