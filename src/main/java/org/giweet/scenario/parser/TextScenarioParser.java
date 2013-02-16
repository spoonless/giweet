package org.giweet.scenario.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.giweet.StringUtils;
import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;

public class TextScenarioParser {

	private final KeywordParser keywordParser;
	private final BufferedReader reader;
	private String bufferedLine;
	private boolean isNewTextBlock = true;

	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new BufferedReader(reader);
	}
	
	public Scenario nextScenario() throws IOException {
		String line;
		while ((line = readLine()) != null) {
			if (isNewTextBlock) {
				Keyword keyword = keywordParser.getStartingKeyword(line);
				if (keyword.getType() == KeywordType.SCENARIO) {
					Scenario scenario = new Scenario(new Sentence(keyword, line));
					parseScenario(scenario);
					return scenario;
				}
			}
			isNewTextBlock = StringUtils.isWhitespace(line);
		}
		return null;
	}

	private void parseScenario(Scenario scenario) throws IOException {
		String line;
		Sentence sentence = null;
		isNewTextBlock = true;
		while ((line = readLine()) != null) {
			Keyword keyword = keywordParser.getStartingKeyword(line);
			KeywordType keywordType = keyword.getType();
			
			if (isNewTextBlock && keywordType == KeywordType.SCENARIO) {
				bufferedLine = line;
				break;
			}
			else if (keywordType == KeywordType.GIVEN || 
					 keywordType == KeywordType.WHEN || 
					 keywordType == KeywordType.THEN ||
					 (keywordType == KeywordType.AND && !scenario.getSentences().isEmpty())) {
				sentence = new Sentence(keyword, line);
				scenario.add(sentence);
				isNewTextBlock = false;
			}
			else if (keywordType == KeywordType.EXAMPLES && isNewTextBlock) {
				sentence = new Sentence(keyword, line);
				scenario.add(sentence);
				isNewTextBlock = false;
			}
			else {
				isNewTextBlock = StringUtils.isWhitespace(line);
				if (isNewTextBlock) {
					sentence = null;
				}
				else if (sentence != null){
					sentence.concat("\n" + line);
				}
			}
		}
	}

	private String readLine() throws IOException {
		String nextLine;
		if (bufferedLine != null) {
			nextLine = bufferedLine;
			bufferedLine = null;
		}
		else {
			nextLine = reader.readLine();
		}
		return nextLine;
	}

}
