package org.giweet.step;

import java.util.Arrays;

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
	
	public StepToken[] trimTokens() {
		int startIndex = 0;
		int endIndex = tokens.length;
		if (endIndex > 0 && tokens[0].isWhitespace()) {
			startIndex++;
		}
		if (tokens.length > startIndex && tokens[endIndex - 1].isWhitespace()) {
			endIndex--;
		}
		StepToken[] trimTokens = tokens;
		if (endIndex - startIndex != tokens.length) {
			trimTokens = Arrays.copyOfRange(tokens, startIndex, endIndex);
		}
		return trimTokens;
	}
	
	@Override
	public int compareTo(StepDeclaration stepDeclaration) {
		StepToken [] otherTrimTokens = stepDeclaration.trimTokens();
		StepToken [] trimTokens = trimTokens();
		int result = 0;
		int nbTokens = Math.min(trimTokens.length, otherTrimTokens.length);
		for (int i = 0 ; i < nbTokens && result == 0 ; i++) {
			result = trimTokens[i].compareTo(otherTrimTokens[i]);
		}
		if (result == 0) {
			result = trimTokens.length - otherTrimTokens.length;
			if (nbTokens > 0 && trimTokens[nbTokens - 1].isDynamic()) {
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
