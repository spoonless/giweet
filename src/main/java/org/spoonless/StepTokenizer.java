package org.spoonless;

import java.util.ArrayList;

public class StepTokenizer {
	
	private final boolean allowDynamicToken;
	
	public StepTokenizer(boolean allowDynamicToken) {
		this.allowDynamicToken = allowDynamicToken;
	}

	public StepToken[] tokenize(String value) {
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
			if (c == '$' && i == startPosition && allowDynamicToken) {
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
}
