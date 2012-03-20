package org.giweet.step.tree;

import org.giweet.step.StepTokenValue;
import org.giweet.step.StepToken;

public class StepTokenValueImpl implements StepTokenValue {
	
	private final int dynamicTokenPosition;
	private final int startPosition;
	private final int endPosition;
	private final StepToken dynamicToken;
	private final StepToken[] tokens;

	public StepTokenValueImpl(StepTokenValuePosition stepTokenValuePosition, StepToken dynamicToken, StepToken... allValueTokens) {
		dynamicTokenPosition = stepTokenValuePosition.getDynamicTokenPosition();
		startPosition = stepTokenValuePosition.getValueTokenStartPosition();
		endPosition = stepTokenValuePosition.getValueTokenEndPosition();
		this.dynamicToken = dynamicToken;
		this.tokens = new StepToken[endPosition - startPosition + 1];
		for (int i = 0 ; i < this.tokens.length ; i++) {
			this.tokens[i] = allValueTokens[i + this.startPosition];
		}
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

	public StepToken getDynamicToken() {
		return dynamicToken;
	}

	public StepToken[] getTokens() {
		return tokens;
	}

	public String getValue() {
		StringBuilder stringBuilder = new StringBuilder();
		for (StepToken value : tokens) {
			stringBuilder.append(value);
		}
		return stringBuilder.toString();
	}
}
