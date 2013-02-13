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

	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new BufferedReader(reader);
	}
	
	public Scenario nextScenario() throws IOException {
		String line;
		while ((line = readLine()) != null) {
			Keyword keyword = keywordParser.getStartingKeyword(line);
			if (keyword.getType() == KeywordType.SCENARIO) {
				Scenario scenario = new Scenario(new Sentence(keyword, line));
				parseScenario(scenario);
				return scenario;
			}
		}
		return null;
	}

	private void parseScenario(Scenario scenario) throws IOException {
		String line;
		Sentence sentence = null;
		
		while ((line = readLine()) != null) {
			Keyword keyword = keywordParser.getStartingKeyword(line);
			if (sentence == null && keyword.getType() == KeywordType.SCENARIO) {
				bufferedLine = line;
				break;
			}
			if (keyword.getType() != KeywordType.NONE && keyword.getType() != KeywordType.SCENARIO) {
				if (keyword.getType() != KeywordType.AND || scenario.getSentences().size() > 0){
					sentence = new Sentence(keyword, line);
					scenario.add(sentence);
				}
			}
			else if (sentence != null) {
				if (StringUtils.isWhitespace(line)) {
					sentence = null;
				}
				else {
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
