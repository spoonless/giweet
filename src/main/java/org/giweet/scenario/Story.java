package org.giweet.scenario;

public class Story extends TextBlock {

	@Override
	public Sentence getTitle() {
		return getFirstSentenceByKeywordType(KeywordType.STORY);
	}

}
