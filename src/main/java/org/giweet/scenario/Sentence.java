package org.giweet.scenario;


public class Sentence {
	
	private final Keyword keyword;
	private String text;
	
	public Sentence(Keyword keyword, String line) {
		this.keyword = keyword;
		this.text = keyword.extractText(line);
	}
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public String getText() {
		return text;
	}
	
	public void concat(String value) {
		text += value;
	}

	@Override
	public String toString() {
		return keyword + text;
	}
	
}
