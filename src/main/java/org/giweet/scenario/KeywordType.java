package org.giweet.scenario;

public enum KeywordType {
	NONE, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META;
	
	public boolean isSentenceConcatAllowed() {
		return this != SCENARIO && this != META;
	}
	
	public static KeywordType[] getParseableKeywordTypes() {
		return new KeywordType[]{SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META};
	}
	
}
