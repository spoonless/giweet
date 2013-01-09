package org.giweet.scenario;

public enum KeywordType {
	STORY, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META;
	
	public boolean isSentenceConcatAllowed() {
		return this != STORY && this != SCENARIO && this != META;
	}
	
}
