package org.giweet.step.tokenizer;

public interface StepTokenizerListener {
	
	void newToken(String token, boolean isMeaningful);

}
