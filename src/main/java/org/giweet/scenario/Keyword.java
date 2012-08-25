package org.giweet.scenario;


public class Keyword {
	
	private final KeywordType keywordType;
	private final String keywordAsString;

	public Keyword(KeywordType keywordType, String keywordAsString) {
		this.keywordType = keywordType;
		this.keywordAsString = keywordAsString;
	}

	public KeywordType getType() {
		return keywordType;
	}
	
	public String extractText(String line) {
		return line.substring(this.keywordAsString.length());
	}
	
	@Override
	public String toString() {
		return keywordAsString;
	}
}
