package org.giweet.step.tokenizer;

public class StepDeclarationCharAnalyzer implements CharacterAnalyzer {
	
	private final CharacterAnalyzer parent;
	private final char dynamicHead;
	private final char dynamicTail;

	public StepDeclarationCharAnalyzer(CharacterAnalyzer parent) {
		this(parent, '{', '}');
	}

	public StepDeclarationCharAnalyzer(CharacterAnalyzer parent, char dynamicHead, char dynamicTail) {
		this.parent = parent;
		this.dynamicHead = dynamicHead;
		this.dynamicTail = dynamicTail;
	}

	@Override
	public int getCharacterType(char c) {
		int type = parent.getCharacterType(c);
		if (c == dynamicHead) {
			type |= CharacterAnalyzer.DYNAMIC_HEAD;
		}
		return type;
	}

	@Override
	public char getExpectedQuoteTail(char quoteHead) {
		return parent.getExpectedQuoteTail(quoteHead);
	}

	@Override
	public char getExpectedDynamicTail(char c) {
		return c == dynamicHead ? dynamicTail : 0;
	}

}
