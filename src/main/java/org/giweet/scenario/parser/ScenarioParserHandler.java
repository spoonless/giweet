package org.giweet.scenario.parser;

import java.util.Locale;

import org.giweet.scenario.KeywordType;

public interface ScenarioParserHandler {
	
	enum ParseAction {CONTINUE, BREAK, SKIP};
	
	void setLocale(Locale locale);
	
	ParseAction startScenario(Locale locale, String keyword, String title);
	
	ParseAction startStep (KeywordType keywordType, String keyword);
	
	ParseAction step (String stepValue);

	ParseAction endStep(KeywordType keywordType);
	
	ParseAction endScenario();

}
