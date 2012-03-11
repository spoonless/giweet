package org.giweet.step.tokenizer;

public interface CharacterAnalyzer {
	
	int SEPARATOR = 0x1;
	int SEPARATOR_IF_LEADING = 0x2;
	int SEPARATOR_IF_TRAILING = 0x4;
	int SEPARATOR_IF_LEADING_OR_TRAILING = SEPARATOR_IF_LEADING | SEPARATOR_IF_TRAILING;
	int QUOTE_HEAD = 0x8;
	int QUOTE_TAIL = 0x10 | SEPARATOR;
	int COMMENT_HEAD = 0x20 | SEPARATOR;
	int COMMENT_TAIL = 0x40 | SEPARATOR;
	int LETTER = 0x80;

	int getCharacterType(char c);
	
	char getExpectedEndQuote(char startQuoteCharacter);

}
