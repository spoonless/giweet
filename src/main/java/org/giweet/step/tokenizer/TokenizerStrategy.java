package org.giweet.step.tokenizer;

public enum TokenizerStrategy {
	
	TOKENIZE_STEP_DESCRIPTOR, TOKENIZE_SCENARIO;

	public boolean isDynamicStepTokenAllowed() {
		return this == TOKENIZE_STEP_DESCRIPTOR;
	}
	
	public boolean isMeaninglessStepTokenAllowed() {
		return this == TOKENIZE_SCENARIO;
	}
}
