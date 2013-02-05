package org.giweet.step;

import org.giweet.step.tokenizer.DefaultCharacterAnalyzer;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;
import org.giweet.step.tokenizer.StepDeclarationCharAnalyzer;
import org.giweet.step.tokenizer.StepTokenizer;


public abstract class StepDeclaration implements Comparable<StepDeclaration> {
	
	private final String value;
	private final StepToken[] tokens;
	
	public StepDeclaration(String value) throws QuoteTailNotFoundException {
		this.value = value;
		// FIXME should be passed as argument
		this.tokens = new StepTokenizer(new StepDeclarationCharAnalyzer(new DefaultCharacterAnalyzer())).tokenize(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public abstract boolean isOfType(StepType type);
	
	public StepToken[] getTokens() {
		return tokens;
	}
	
	@Override
	public int compareTo(StepDeclaration stepDeclaration) {
		StepToken [] otherStepTokens = stepDeclaration.getTokens();
		int result = 0;
		int nbTokens = Math.min(tokens.length, otherStepTokens.length);
		for (int i = 0 ; i < nbTokens && result == 0 ; i++) {
			result = tokens[i].compareTo(otherStepTokens[i]);
		}
		if (result == 0) {
			result = this.tokens.length - otherStepTokens.length;
			if (nbTokens > 0 && tokens[nbTokens - 1].isDynamic()) {
				result = -result;				
			}
		}
		
		if (result != 0) {
			result = result < 0 ? -1 : 1;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
