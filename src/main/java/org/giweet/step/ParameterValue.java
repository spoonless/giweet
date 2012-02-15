package org.giweet.step;

public class ParameterValue {
	
	private final int dynamicTokenPosition;
	private final int staticTokenPosition;
	private final StringBuilder stringBuilder;
	private String value;

	public ParameterValue(int dynamicTokenPosition, int staticTokenPosition) {
		this.dynamicTokenPosition = dynamicTokenPosition;
		this.staticTokenPosition = staticTokenPosition;
		this.stringBuilder = new StringBuilder();
	}
	
	public void add (StepToken stepToken) {
		stringBuilder.append(stepToken);
	}
	
	public int getDynamicTokenPosition() {
		return dynamicTokenPosition;
	}

	public int getStaticTokenPosition() {
		return staticTokenPosition;
	}

	@Override
	public String toString() {
		if (value == null) {
			value = stringBuilder.toString();
		}
		return value;
	}

}
