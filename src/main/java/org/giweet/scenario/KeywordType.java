package org.giweet.scenario;

public enum KeywordType {
	NONE, STORY, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META;
	
	public boolean isSentenceConcatAllowed() {
		return this != STORY && this != SCENARIO && this != META;
	}
	
	public static KeywordType[] getParseableKeywordTypes() {
		return new KeywordType[]{STORY, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META};
	}
	
}
