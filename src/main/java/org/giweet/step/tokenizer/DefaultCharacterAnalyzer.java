package org.giweet.step.tokenizer;


public class DefaultCharacterAnalyzer implements CharacterAnalyzer {

	public char getExpectedEndQuote(char startQuoteCharacter) {
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

	public int getCharacterType(char c) {
		int characterType = SEPARATOR;
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
		case Character.FINAL_QUOTE_PUNCTUATION:
			characterType = LETTER;
			break;

		// character that can be dropped under special circumstances
		case Character.START_PUNCTUATION:
			characterType = c == '(' ? COMMENT_HEAD : SEPARATOR_IF_LEADING;
			break;
		case Character.END_PUNCTUATION:
			characterType = c == ')' ? COMMENT_TAIL : SEPARATOR_IF_TRAILING;
			break;
		case Character.INITIAL_QUOTE_PUNCTUATION:
			characterType = QUOTE_HEAD;
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
			characterType = QUOTE_HEAD;
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
