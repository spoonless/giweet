package org.giweet.scenario.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;
import org.giweet.scenario.Story;

public class TextScenarioParser {

	private final KeywordParser keywordParser;
	private final BufferedReader reader;
	private Scenario nextPartiallyParsedScenario;
	private Story currentStory;
	private final List<Sentence> meta;

	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new BufferedReader(reader);
		currentStory = new Story();
		meta = new ArrayList<Sentence>();
	}

	public Scenario nextScenario() throws IOException {
		Scenario scenario = nextPartiallyParsedScenario;
		nextPartiallyParsedScenario = null;
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			line += '\n';
			Sentence sentence = new Sentence(keywordParser.getStartingKeyword(line), line);
			
			if (sentence.isProcessable() && sentence.getKeyword().getType() == KeywordType.META) {
				meta.add(sentence);
			}
			else if (sentence.isProcessable() && sentence.getKeyword().getType() == KeywordType.STORY) {
				currentStory = new Story();
				currentStory.add(sentence);
				currentStory.getMeta().addAll(meta);
				meta.clear();
				if (scenario != null) {
					break;
				}
			}
			else if (sentence.isProcessable() && sentence.getKeyword().getType() == KeywordType.SCENARIO) {
				if (scenario != null) {
					nextPartiallyParsedScenario = createScenario();
					nextPartiallyParsedScenario.add(sentence);
					break;
				}
				else {
					scenario = createScenario();
					scenario.add(sentence);
				}
			}
			else if (scenario == null) {
				if (sentence.isProcessable()) {
					scenario = createScenario();
					scenario.add(sentence);
				}
				else {
					meta.clear();
					currentStory.add(sentence);
				}
			}
			else {
				meta.clear();
				scenario.add(sentence);
			}
		}
		return scenario;
	}

	private Scenario createScenario() {
		Scenario scenario = new Scenario();
		scenario.setStory(currentStory);
		scenario.getMeta().addAll(meta);
		meta.clear();
		return scenario;
	}

}
