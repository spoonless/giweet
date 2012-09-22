package org.giweet.scenario;


public class Sentence {
	
	private final Keyword keyword;
	private String text;
	
	public Sentence(Keyword keyword, String line) {
		this.keyword = keyword;
		this.text = keyword != null ? keyword.extractText(line) : line;
	}
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean isProcessable() {
		return keyword != null;
	}
	
	public boolean concat(Sentence sentence) {
		if (sentence.isProcessable()) {
			return false;
		}
		if (this.isProcessable() && ! this.keyword.getType().isSentenceConcatAllowed()) {
			return false;
		}
		text += sentence.toString();
		return true;
	}

	@Override
	public String toString() {
		return isProcessable() ? keyword + text : text;
	}
	
}
