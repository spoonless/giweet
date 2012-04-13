package org.giweet.step;

import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;

public class StepInstance {

	private final String value;
	private final StepToken[] tokens;
	private final StepType type;

	public StepInstance(StepType stepType, String value) {
		this.type = stepType;
		this.value = value;
		// FIXME should be passed as argument
		this.tokens = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE).tokenize(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public StepToken[] getTokens() {
		return tokens;
	}
	
	public StepType getType() {
		return type;
	}
}
