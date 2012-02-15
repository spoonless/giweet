package org.giweet.step;

import java.util.ArrayList;

public class StepTokenizer {
	
	private static final int TOKEN_SEPARATOR = 0x1;
	private static final int TOKEN_SEPARATOR_IF_LEADING = 0x2;
	private static final int TOKEN_SEPARATOR_IF_TRAILING = 0x4;
	private static final int TOKEN_SEPARATOR_IF_LEADING_OR_TRAILING = TOKEN_SEPARATOR_IF_LEADING | TOKEN_SEPARATOR_IF_TRAILING;
	private static final int START_QUOTE = 0x8 | TOKEN_SEPARATOR;
	private static final int END_QUOTE = 0x10 | TOKEN_SEPARATOR;
	private static final int START_COMMENT = 0x20 | TOKEN_SEPARATOR;
	private static final int END_COMMENT = 0x40 | TOKEN_SEPARATOR;
	private static final int TOKEN_LETTER = 0x80;

	private final boolean allowParameterToken;
	private final boolean withMeaninglessTokens;
	
	public StepTokenizer(boolean allowParameterToken) {
		this (allowParameterToken, false);
	}
	
	public StepTokenizer(boolean allowParameterToken, boolean withMeaninglessTokens) {
		this.allowParameterToken = allowParameterToken;
		this.withMeaninglessTokens = withMeaninglessTokens;
	}

	public StepToken[] tokenize(String value) {
		return tokenize(value.toCharArray(), 0, value.length());
	}

	public StepToken[] tokenize (char[] characters, int offset, int count) {
		ArrayList<StepToken> tokensAsList = new ArrayList<StepToken>();
		int lastIndex = offset + count;
		int letterCount = 0;
		int separatorCount = 0;
		int trailingCount = 0;
		int startPosition = 0;
		int commentBlockCount = 0;
		char expectedEndQuote = 0;
		for (int i = offset ; i <= lastIndex ; i++) {
			char c = (i < lastIndex) ? characters[i] : expectedEndQuote;
			int characterType = 0;
			
			if (expectedEndQuote != 0) {
				if (c != expectedEndQuote) {
					letterCount++;
					continue;
				}
				else {
					expectedEndQuote = 0;
					characterType = END_QUOTE;
				}
			}
			else {
				characterType = getCharacterType(c);
			}
			
			if (commentBlockCount > 0) {
				switch (characterType) {
				case START_COMMENT:
					commentBlockCount++;
					break;
				case END_COMMENT:
					commentBlockCount--;
					break;
				}
				if (! withMeaninglessTokens) {
					continue;
				}
				characterType = TOKEN_SEPARATOR;
			}
			
			if ((characterType & TOKEN_SEPARATOR) != 0) {
				if (letterCount > 0) {
					letterCount -= trailingCount;
					boolean isArgumentToken = false;
					if (characters[startPosition] == '$' && letterCount > 1 && allowParameterToken) {
						startPosition++;
						letterCount--;
						isArgumentToken = true;
					}
					String stringToken = new String (characters, startPosition, letterCount);
					tokensAsList.add(createStepToken(isArgumentToken, stringToken));
					letterCount = 0;
					startPosition = i - trailingCount;
					separatorCount = trailingCount;
					trailingCount = 0;
				}
				separatorCount++;
			}

			switch (characterType) {
			case TOKEN_LETTER:
				if (letterCount == 0) {
					if (withMeaninglessTokens && separatorCount > 0) {
						String stringToken = new String (characters, startPosition, separatorCount);
						tokensAsList.add(new StaticStepToken(stringToken, false));
						separatorCount = 0;
					}
					startPosition = i;
				}
				letterCount++;
				trailingCount = 0;
				break;
			case TOKEN_SEPARATOR_IF_LEADING:
				if (letterCount != 0) {
					letterCount++;
				}
				else {
					separatorCount++;
				}
				break;
			case TOKEN_SEPARATOR_IF_TRAILING:
			case TOKEN_SEPARATOR_IF_LEADING_OR_TRAILING:
				if (letterCount != 0) {
					letterCount++;
					trailingCount++;
				}
				break;
			case START_COMMENT:
				commentBlockCount++;
				break;
			case START_QUOTE:
				expectedEndQuote = getExpectedEndQuote(c);
				startPosition = i+1;
				break;
			}
		}
		
		if (withMeaninglessTokens && separatorCount > 1) {
			String stringToken = new String (characters, startPosition, separatorCount - 1);
			tokensAsList.add(new StaticStepToken(stringToken, false));
		}

		return tokensAsList.toArray(new StepToken[tokensAsList.size()]);
	}
	
	private char getExpectedEndQuote(char startQuoteCharacter) {
		char expectedEndQuote = startQuoteCharacter;
		switch (startQuoteCharacter) {
		case '\u00AB':
			expectedEndQuote = '\u00BB';
			break;
		case '\u2018':
			expectedEndQuote = '\u2019';
			break;
		case '\u201C':
			expectedEndQuote = '\u201D';
			break;
		case '\u2039':
			expectedEndQuote = '\u203A';
			break;
		}
		return expectedEndQuote;
	}

	private int getCharacterType(char c) {
		int characterType = TOKEN_SEPARATOR;
		switch (Character.getType(c)) {
		// character that can be dropped
		// case Character.CONNECTOR_PUNCTUATION:
		// case Character.CONTROL:
		// case Character.ENCLOSING_MARK:
		// case Character.FORMAT:
		// case Character.LINE_SEPARATOR:
		// case Character.PARAGRAPH_SEPARATOR:
		// case Character.PRIVATE_USE:
		// case Character.SPACE_SEPARATOR:
		// case Character.SURROGATE:
		// case Character.UNASSIGNED:
		//	break;
		
		// character to preserve
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
		case Character.TITLECASE_LETTER:
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.CURRENCY_SYMBOL:
		case Character.MATH_SYMBOL:
		case Character.LETTER_NUMBER:
		case Character.DASH_PUNCTUATION:
		case Character.COMBINING_SPACING_MARK:
		case Character.MODIFIER_LETTER:
		case Character.MODIFIER_SYMBOL:
		case Character.NON_SPACING_MARK:
		case Character.OTHER_LETTER:
		case Character.OTHER_NUMBER:
		case Character.OTHER_SYMBOL:
			characterType = TOKEN_LETTER;
			break;

		// character that can be dropped under special circumstances
		case Character.START_PUNCTUATION:
			characterType = c == '(' ? START_COMMENT : TOKEN_SEPARATOR_IF_LEADING;
			break;
		case Character.END_PUNCTUATION:
			characterType = c == ')' ? END_COMMENT : TOKEN_SEPARATOR_IF_TRAILING;
			break;
		case Character.INITIAL_QUOTE_PUNCTUATION:
			characterType = START_QUOTE;
			break;
		case Character.FINAL_QUOTE_PUNCTUATION:
			characterType = END_QUOTE;
			break;
		case Character.OTHER_PUNCTUATION:
			characterType = getCharacterTypeForOtherPunctuation(c);
			break;
		}
		return characterType;
	}

	private int getCharacterTypeForOtherPunctuation(char c) {
		int characterType;
		switch (c) {
		case '"':
		case '\uFF02': // FULLWIDTH QUOTATION MARK
			characterType = START_QUOTE;
			break;
		case '\'':
		case '\uFF07': // FULLWIDTH APOSTROPHE
			characterType = TOKEN_SEPARATOR;
			break;
		case '#':
		case '%':
		case '&':
		case '*':
		case '/':
		case '@':
		case '\\':
		// TODO some characters missing as token letters
		case '\u2030': // PER MILLE SIGN
		case '\u2031': // PER TEN THOUSAND SIGN
		case '\uFE5F' : // SMALL NUMBER SIGN
		case '\uFE60': // SMALL AMPERSAND
		case '\uFE61': // SMALL ASTERISK
		case '\uFE68': // SMALL REVERSE SOLIDUS
		case '\uFE6A': // SMALL PERCENT SIGN
		case '\uFE6B': // SMALL COMMERCIAL AT
		case '\uFF03': // FULLWIDTH NUMBER SIGN
		case '\uFF05': // FULLWIDTH PERCENT SIGN
		case '\uFF06': // FULLWIDTH AMPERSAND
		case '\uFF0A': // FULLWIDTH ASTERISK
		case '\uFF0F': // FULLWIDTH SOLIDUS
		case '\uFF20': // FULLWIDTH COMMERCIAL AT
		case '\uFF3C': // FULLWIDTH REVERSE SOLIDUS
			characterType = TOKEN_LETTER;
			break;
		default:
			characterType = TOKEN_SEPARATOR_IF_LEADING_OR_TRAILING;
		}
		return characterType;
	}

	private static StepToken createStepToken(boolean isArgumentNext, String tokenValue) {
		StepToken result = null;
		if (isArgumentNext) {
			result = new ParameterStepToken(tokenValue);
		}
		else {
			result = new StaticStepToken(tokenValue);
		}
		return result;
	}
}
