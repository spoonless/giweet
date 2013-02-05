package org.giweet.step.tokenizer;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.DynamicStepToken;
import org.giweet.step.SeparatorStepToken;
import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;

public class StepTokenizer {
	private final CharacterAnalyzer characterAnalyzer;

	@Deprecated
	public StepTokenizer(TokenizerStrategy strategy) {
		this(strategy, new DefaultCharacterAnalyzer());
	}

	@Deprecated
	public StepTokenizer(TokenizerStrategy strategy, CharacterAnalyzer characterAnalyzer) {
		this.characterAnalyzer = characterAnalyzer;
	}
	
	public StepTokenizer(CharacterAnalyzer characterAnalyzer) {
		this.characterAnalyzer = characterAnalyzer;
	}

	public StepToken[] tokenize(String value) throws QuoteTailNotFoundException {
		List<StepToken> stepTokens = new ArrayList<StepToken>();
		
		for (int i = 0; i < value.length();) {
			StepToken stepToken = null;
			char c = value.charAt(i);
			int type = characterAnalyzer.getCharacterType(c);
			if (isDynamicHead(type)) {
				stepToken = tokenizeDynamic(value, i);
			}
			else if (isQuoteHead(type)) {
				stepToken = tokenizeQuotation(value, i);
			}
			else if (isSeparator(type)) {
				stepToken = tokenizeSeparator(value, i);
			}
			else {
				stepToken = tokenizeStatic(value, i);
			}
			addStepToken(stepTokens, stepToken);
			i+=stepToken.toString().length();
		}
		
		return stepTokens.toArray(new StepToken[stepTokens.size()]);
	}

	private void addStepToken(List<StepToken> stepTokens, StepToken stepToken) {
		int stepTokensSize = stepTokens.size();
		if (stepTokensSize != 0) {
			StepToken previousToken = stepTokens.get(stepTokensSize - 1);
			if (previousToken.merge(stepToken)) {
				return;
			}
		}
		stepTokens.add(stepToken);
	}

	private StepToken tokenizeStatic(String value, int startIndex) {
		int trailingSeparator = 0;
		int currentIndex = startIndex + 1;
		while (currentIndex < value.length()) {
			char c = value.charAt(currentIndex);
			int type = characterAnalyzer.getCharacterType(c);
			if (isSeparatorIfLeading(type)) {
				trailingSeparator++;
			}
			else if (isSeparator(type)) {
				break;
			}
			else {
				trailingSeparator = 0;
			}
			currentIndex++;
		}
		return new StaticStepToken(value.substring(startIndex, currentIndex - trailingSeparator));
	}
	
	private StepToken tokenizeSeparator(String value, int startIndex) {
		char nonWhitepaceCharacter = 0;
		char startChar = value.charAt(startIndex);
		if (! isWhitespace(characterAnalyzer.getCharacterType(startChar))) {
			nonWhitepaceCharacter = startChar;
		}
		
		int currentIndex = startIndex + 1;
		while (currentIndex < value.length()) {
			char c = value.charAt(currentIndex);
			int type = characterAnalyzer.getCharacterType(c);
			if (! isSeparator(type) || isHead(type)) {
				break;
			}
			if (! isWhitespace(type)) {
				if (nonWhitepaceCharacter == 0) {
					nonWhitepaceCharacter = c;
				}
				else {
					break;
				}
			}
			currentIndex++;
		}

		String extractedValue = "";
		if (nonWhitepaceCharacter != 0) {
			extractedValue = Character.toString(nonWhitepaceCharacter);
		}
		return new SeparatorStepToken(extractedValue, value.substring(startIndex, currentIndex));
	}

	private StepToken tokenizeQuotation(String value, int startIndex) throws QuoteTailNotFoundException {
		char quoteHead = value.charAt(startIndex);
		char quoteTail = characterAnalyzer.getExpectedQuoteTail(quoteHead);
		
		if (quoteTail == 0) {
			return tokenizeStaticOrSeparator(value, startIndex);
		}
		
		int nbQuoteHead = 1;
		int currentIndex = startIndex + 1;
		while (currentIndex < value.length()) {
			char c = value.charAt(currentIndex);
			currentIndex++;
			if (c == quoteTail && --nbQuoteHead == 0) {
				return new StaticStepToken(value.substring(startIndex + 1, currentIndex - 1), value.substring(startIndex, currentIndex));
			}
			else if (c == quoteHead) {
				nbQuoteHead++;
			}
		}
		throw new QuoteTailNotFoundException(quoteTail);
	}
	
	private StepToken tokenizeDynamic(String value, int startIndex) {
		char dynamicTail = characterAnalyzer.getExpectedDynamicTail(value.charAt(startIndex));
		
		if (dynamicTail == 0) {
			return tokenizeStaticOrSeparator(value, startIndex);
		}
		
		int currentIndex = startIndex + 1;
		while (currentIndex < value.length()) {
			char c = value.charAt(currentIndex);
			currentIndex++;
			if (c == dynamicTail) {
				if (startIndex + 1 == currentIndex -1) {
					return tokenizeStaticOrSeparator(value, startIndex);
				}
				else {
					return new DynamicStepToken(value.substring(startIndex + 1, currentIndex - 1), value.substring(startIndex, currentIndex));
				}
			}
		}
		return tokenizeStaticOrSeparator(value, startIndex);
	}

	private StepToken tokenizeStaticOrSeparator(String value, int startIndex) {
		if (isSeparator(characterAnalyzer.getCharacterType(value.charAt(startIndex)))) {
			return tokenizeSeparator(value, startIndex);
		}
		else {
			return tokenizeStatic(value, startIndex);
		}
	}

	private static boolean isSeparator(int type) {
		return (type & CharacterAnalyzer.SEPARATOR) == CharacterAnalyzer.SEPARATOR;
	}

	private static boolean isWhitespace(int type) {
		return (type & CharacterAnalyzer.WHITESPACE) == CharacterAnalyzer.WHITESPACE;
	}

	private static boolean isSeparatorIfLeading(int type) {
		return (type & CharacterAnalyzer.SEPARATOR_IF_TRAILING) == CharacterAnalyzer.SEPARATOR_IF_TRAILING;
	}
	
	private static boolean isHead(int type) {
		return (type & CharacterAnalyzer.HEAD) == CharacterAnalyzer.HEAD;
	}

	private static boolean isQuoteHead(int type) {
		return (type & CharacterAnalyzer.QUOTE_HEAD) == CharacterAnalyzer.QUOTE_HEAD;
	}

	private static boolean isDynamicHead(int type) {
		return (type & CharacterAnalyzer.DYNAMIC_HEAD) == CharacterAnalyzer.DYNAMIC_HEAD;
	}
}
