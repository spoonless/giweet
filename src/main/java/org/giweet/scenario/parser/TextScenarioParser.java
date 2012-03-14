package org.giweet.scenario.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;

public class TextScenarioParser implements ScenarioParser {
	
	private final BufferedReader bufferedReader;
	private final KeywordParser keywordParser;

	private boolean scenarioStarted;
	private KeywordType previousKeywordType;
	private StringBuilder stepBuilder;

	public TextScenarioParser(Reader reader) {
		this(reader, Locale.getDefault());
	}

	public TextScenarioParser(Reader reader, Locale locale) {
		this.bufferedReader = new BufferedReader(reader);
		this.keywordParser = new KeywordParser(locale);
	}

	public void parse(ScenarioParserHandler handler) throws IOException {
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			Keyword keyword = keywordParser.getStartingKeyword(line);
			
			if (keyword == null) {
				handleLineWithNoKeyword(handler, line);
			}
			else if (keyword.getType() == KeywordType.SCENARIO) {
				endPreviousScenario(handler);
				handler.startScenario(keywordParser.getLocale(), keyword.toString(), line.substring(keyword.toString().length()));
				scenarioStarted = true;
			}
			else if (keyword.getType() != KeywordType.AND || previousKeywordType != null) {
				endPreviousStep(handler);
				if (keyword.getType() != KeywordType.AND) {
					previousKeywordType = keyword.getType();
				}
				handler.startStep(previousKeywordType, keyword.toString());
				stepBuilder = new StringBuilder();
				stepBuilder.append(line.substring(keyword.toString().length()));
			}
		}
		
		endPreviousScenario(handler);
	}

	private void handleLineWithNoKeyword(ScenarioParserHandler handler, String line) {
		if (stepBuilder != null){
			if (line.equals("")) {
				handler.step(stepBuilder.toString());
				handler.endStep(previousKeywordType);
				stepBuilder = null;
				// TODO improve reset for and
				previousKeywordType = null;
			}
			else {
				stepBuilder.append('\n').append(line);
			}
		}
	}

	private void endPreviousScenario(ScenarioParserHandler handler) {
		endPreviousStep(handler);
		if (scenarioStarted) {
			handler.endScenario();
			scenarioStarted = false;
			previousKeywordType = null;
		}
	}

	private void endPreviousStep(ScenarioParserHandler handler) {
		if (stepBuilder != null) {
			handler.step(stepBuilder.toString());
			handler.endStep(previousKeywordType);
			stepBuilder = null;
		}
	}
}
