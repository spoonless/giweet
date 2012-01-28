package org.spoonless;

import java.util.ArrayList;

public class StepDescriptor implements Comparable<StepDescriptor> {
	
	private final String value;
	private final StepToken[] tokens;
	
	public StepDescriptor(String value) {
		this.value = value;
		this.tokens = tokenize(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public StepToken[] getTokens() {
		return tokens;
	}
	
	private static StepToken[] tokenize(String value) {
		ArrayList<StepToken> tokensAsList = new ArrayList<StepToken>();
		char[] characters = value.toCharArray();
		int startPosition = 0;
		int ignorableBlocks = 0;
		boolean isArgumentNext = false;
		for (int i = 0 ; i < characters.length ; i++) {
			char c = characters[i];
			if (! Character.isJavaIdentifierPart(c)) {
				if (ignorableBlocks == 0 && i > startPosition) {
					String tokenValue = new String(characters, startPosition, i - startPosition);
					tokensAsList.add(createStepToken(isArgumentNext, tokenValue));
					isArgumentNext = false;
				}
				startPosition = i+1;
				// TODO parameterized the character for i18n
				if (c == '(') {
					ignorableBlocks++;
					startPosition = i+1;
				}
				// TODO parameterized the character for i18n
				else if (c == ')' && ignorableBlocks > 0) {
					ignorableBlocks--;
					startPosition = i+1;
				}
			}
			if (c == '$' && i == startPosition) {
				isArgumentNext = true;
				startPosition = i+1;
			}
		}
		if (ignorableBlocks == 0 && characters.length > startPosition || isArgumentNext) {
			String tokenValue = new String(characters, startPosition, characters.length - startPosition);
			tokensAsList.add(createStepToken(isArgumentNext, tokenValue));
		}
		return tokensAsList.toArray(new StepToken[tokensAsList.size()]);
	}

	private static StepToken createStepToken(boolean isArgumentNext, String tokenValue) {
		StepToken result = null;
		if (isArgumentNext && tokenValue.length() == 0) {
			isArgumentNext = false;
			tokenValue = "$";
		}
		if (isArgumentNext) {
			result = new DynamicToken(tokenValue);
		}
		else {
			result = new StringToken(tokenValue);
		}
		return result;
	}

	public int compareTo(StepDescriptor stepDescriptor) {
		StepToken [] otherStepTokens = stepDescriptor.getTokens();
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
