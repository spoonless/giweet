package org.giweet.step.tokenizer;


public class DefaultCharacterAnalyzer implements CharacterAnalyzer {

	@Override
	public char getExpectedQuoteTail(char quoteHead) {
		// FIXME many QUOTE_HEAD characters have no tail
		char expectedQuoteTail = 0;
		switch (quoteHead) {
		case '"':
			expectedQuoteTail = '"';
			break;
		case '\u00AB':
			expectedQuoteTail = '\u00BB';
			break;
		case '\u2018':
			expectedQuoteTail = '\u2019';
			break;
		case '\u201C':
			expectedQuoteTail = '\u201D';
			break;
		case '\u2039':
			expectedQuoteTail = '\u203A';
			break;
		}
		return expectedQuoteTail;
	}
	
	@Override
	public char getExpectedDynamicTail(char dynamicHead) {
		return 0;
	}

	@Override
	public int getCharacterType(char c) {
		int characterType = WHITESPACE;
		switch (Character.getType(c)) {
		// character that can be dropped
		// case Character.CONTROL:
		// case Character.FORMAT:
		// case Character.UNASSIGNED:
		// case Character.PRIVATE_USE:
		// case Character.SURROGATE:
		// case Character.LINE_SEPARATOR:
		// case Character.PARAGRAPH_SEPARATOR:
		// case Character.SPACE_SEPARATOR:
		//	break;

		case Character.CONNECTOR_PUNCTUATION:
		case Character.ENCLOSING_MARK:
			characterType = SEPARATOR;
			break;
		
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
			characterType = LETTER;
			break;

		// character that can be dropped under special circumstances
		case Character.START_PUNCTUATION:
			characterType = SEPARATOR_IF_LEADING;
			break;
		case Character.FINAL_QUOTE_PUNCTUATION:
		case Character.END_PUNCTUATION:
			characterType = SEPARATOR_IF_TRAILING;
			break;
		case Character.INITIAL_QUOTE_PUNCTUATION:
			characterType = QUOTE_HEAD | SEPARATOR_IF_LEADING;
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
			characterType = QUOTE_HEAD | SEPARATOR_IF_LEADING;
			break;
		case '\'':
		case '\uFF07': // FULLWIDTH APOSTROPHE
			characterType = SEPARATOR;
			break;
		case '#':
		case '%':
		case '&':
		case '*':
		case '/':
		case '@':
		case '\\':
		// FIXME some characters missing as token letters
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
			characterType = LETTER;
			break;
		default:
			characterType = SEPARATOR_IF_LEADING_OR_TRAILING;
		}
		return characterType;
	}
}
