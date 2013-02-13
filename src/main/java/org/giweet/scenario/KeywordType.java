package org.giweet.scenario;

public enum KeywordType {
	NONE, SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META;
	
	public static KeywordType[] getParseableKeywordTypes() {
		return new KeywordType[]{SCENARIO, GIVEN, WHEN, THEN, AND, EXAMPLES, META};
	}
	
}
