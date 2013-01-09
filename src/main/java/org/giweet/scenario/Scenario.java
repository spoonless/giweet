package org.giweet.scenario;


public class Scenario extends TextBlock {
	
	private Story story;

	public Story getStory() {
		return story;
	}

	public void setStory(Story story) {
		this.story = story;
	}

	@Override
	public Sentence getTitle() {
		return getFirstSentenceByKeywordType(KeywordType.SCENARIO);
	}
}
