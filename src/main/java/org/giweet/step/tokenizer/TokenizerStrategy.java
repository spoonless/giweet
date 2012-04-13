package org.giweet.step.tokenizer;

public enum TokenizerStrategy {
	
	TOKENIZE_STEP_DECLARATION, TOKENIZE_STEP_INSTANCE;

	public boolean isDynamicStepTokenAllowed() {
		return this == TOKENIZE_STEP_DECLARATION;
	}
	
	public boolean isMeaninglessStepTokenAllowed() {
		return this == TOKENIZE_STEP_INSTANCE;
	}
}
