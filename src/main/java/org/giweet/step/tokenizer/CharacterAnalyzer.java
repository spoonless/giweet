package org.giweet.step.tokenizer;

public interface CharacterAnalyzer {
	
	int LETTER = 0x0;
	
	int SEPARATOR = 0x10;
	int WHITESPACE = 0x1 | SEPARATOR;
	int SEPARATOR_IF_LEADING = 0x2 | SEPARATOR;
	int SEPARATOR_IF_TRAILING = 0x4 | SEPARATOR;
	int SEPARATOR_IF_LEADING_OR_TRAILING = SEPARATOR_IF_LEADING | SEPARATOR_IF_TRAILING;

	int HEAD = 0x1000;
	int QUOTE_HEAD = 0x100 | HEAD;
	int DYNAMIC_HEAD = 0x200 | HEAD;

	int getCharacterType(char c);

	char getExpectedQuoteTail(char quoteHead);

	char getExpectedDynamicTail(char dynamicHead);
}
