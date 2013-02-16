package org.giweet.scenario;


public class Keyword {
	
	private final KeywordType type;
	private final String label;

	public Keyword(KeywordType type, String label) {
		this.type = type;
		this.label = label;
	}

	public KeywordType getType() {
		return type;
	}
	
	public String extractText(String line) {
		return label.length() < line.length() ? line.substring(this.label.length()) : "";
	}
	
	@Override
	public String toString() {
		return label;
	}
}
