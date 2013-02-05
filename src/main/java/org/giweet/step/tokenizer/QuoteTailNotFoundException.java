package org.giweet.step.tokenizer;

public class QuoteTailNotFoundException extends Exception {

	private static final long serialVersionUID = -8275541543396180740L;
	
	private char expectedQuoteTail;
	
	public QuoteTailNotFoundException() {
	}
	
	public QuoteTailNotFoundException(char expectedQuoteTail) {
		super("Expected quote tail character " + expectedQuoteTail + " cannot be found!");
		this.expectedQuoteTail = expectedQuoteTail;
	}
	
	public char getExpectedQuoteTail() {
		return expectedQuoteTail;
	}

}
