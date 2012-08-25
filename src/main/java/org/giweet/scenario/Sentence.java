package org.giweet.scenario;

import org.giweet.StringUtils;

public class Sentence {
	
	private final Keyword keyword;
	private String text;
	private boolean isParagraphSeparator;
	
	public Sentence(Keyword keyword, String line) {
		this.keyword = keyword;
		this.text = keyword != null ? keyword.extractText(line) : line;
		this.isParagraphSeparator = keyword == null && StringUtils.isWhitespace(text);
	}
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return isProcessable() ? keyword + text : text;
	}

	public boolean isProcessable() {
		return keyword != null;
	}
	
	public boolean concat(Sentence sentence) {
		if (this.isProcessable() && ! sentence.isProcessable() && ! sentence.isParagraphSeparator) {
			text += sentence.toString();
			isParagraphSeparator = false;
			return true;
		}
		if (! this.isProcessable() && (! this.isParagraphSeparator || ! sentence.isProcessable())) {
			text += sentence.toString();
			isParagraphSeparator = sentence.isParagraphSeparator;
			return true;
		}
		return false;
	}

}
