package org.giweet.step.tree;

import org.giweet.step.ParameterValue;
import org.giweet.step.StepToken;

public class ParameterValueImpl implements ParameterValue {
	
	private final int parameterTokenPosition;
	private final int valueTokenStartPosition;
	private final int valueTokenEndPosition;
	private final StepToken parameterToken;
	private final StepToken[] valueTokens;

	public ParameterValueImpl(ParameterValuePosition parameterValuePosition, StepToken parameterToken, StepToken... allValueTokens) {
		parameterTokenPosition = parameterValuePosition.getParameterTokenPosition();
		valueTokenStartPosition = parameterValuePosition.getValueTokenStartPosition();
		valueTokenEndPosition = parameterValuePosition.getValueTokenEndPosition();
		this.parameterToken = parameterToken;
		this.valueTokens = new StepToken[valueTokenEndPosition - valueTokenStartPosition + 1];
		for (int i = 0 ; i < this.valueTokens.length ; i++) {
			this.valueTokens[i] = allValueTokens[i + this.valueTokenStartPosition];
		}
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

	public StepToken getParameterToken() {
		return parameterToken;
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
