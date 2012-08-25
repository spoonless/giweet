package org.giweet.scenario.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;

public class TextScenarioParser {

	private final KeywordParser keywordParser;
	private final BufferedReader reader;
	private Scenario nextPartiallyParsedScenario;

	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new BufferedReader(reader);
	}

	public Scenario nextScenario() throws IOException {
		Scenario scenario = nextPartiallyParsedScenario;
		nextPartiallyParsedScenario = null;
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			line += '\n';
			Sentence sentence = new Sentence(keywordParser.getStartingKeyword(line), line);
			
			if (sentence.isProcessable() && sentence.getKeyword().getType() == KeywordType.SCENARIO) {
				if (scenario != null) {
					nextPartiallyParsedScenario = new Scenario();
					nextPartiallyParsedScenario.setTitle(sentence);
					break;
				}
				else {
					scenario = new Scenario();
					scenario.setTitle(sentence);
				}
			}
			else if (scenario == null) {
				if (sentence.isProcessable()) {
					scenario = new Scenario();
					Sentence anonymousTitle = new Sentence(new Keyword(KeywordType.SCENARIO, ""), "");
					scenario.setTitle(anonymousTitle);
					scenario.add(sentence);
				}
			}
			else {
				scenario.add(sentence);
			}
		}
		return scenario;
	}

}
