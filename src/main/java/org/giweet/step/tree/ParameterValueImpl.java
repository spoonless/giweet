package org.giweet.step.tree;

import org.giweet.step.ParameterValue;
import org.giweet.step.StepToken;

public class ParameterValueImpl implements ParameterValue {
	
	private final int dynamicTokenPosition;
	private final int valueTokenStartPosition;
	private final int valueTokenEndPosition;
	private final StepToken dynamicToken;
	private final StepToken[] valueTokens;

	public ParameterValueImpl(ParameterValuePosition parameterValuePosition, StepToken dynamicToken, StepToken... allValueTokens) {
		dynamicTokenPosition = parameterValuePosition.getDynamicTokenPosition();
		valueTokenStartPosition = parameterValuePosition.getValueTokenStartPosition();
		valueTokenEndPosition = parameterValuePosition.getValueTokenEndPosition();
		this.dynamicToken = dynamicToken;
		this.valueTokens = new StepToken[valueTokenEndPosition - valueTokenStartPosition + 1];
		for (int i = 0 ; i < this.valueTokens.length ; i++) {
			this.valueTokens[i] = allValueTokens[i + this.valueTokenStartPosition];
		}
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

	public StepToken getDynamicToken() {
		return dynamicToken;
	}

	public StepToken[] getValueTokens() {
		return valueTokens;
	}

	public String getValue() {
		StringBuilder stringBuilder = new StringBuilder();
		for (StepToken value : valueTokens) {
			stringBuilder.append(value);
		}
		return stringBuilder.toString();
	}
}
