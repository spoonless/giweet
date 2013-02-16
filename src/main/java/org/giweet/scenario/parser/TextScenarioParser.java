package org.giweet.scenario.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.giweet.StringUtils;
import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;

public class TextScenarioParser {

	private final KeywordParser keywordParser;
	private final ResetableBufferedReader reader;
	private boolean isNewTextBlock = true;
	private final List<Sentence> meta;
	
	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new ResetableBufferedReader(reader);
		this.meta = new ArrayList<Sentence>();
	}
	
	public Scenario nextScenario() throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (isNewTextBlock) {
				Keyword keyword = keywordParser.getKeyword(line, KeywordType.SCENARIO);
				if (keyword != null) {
					Scenario scenario = new Scenario(new Sentence(keyword, line));
					scenario.getMeta().addAll(meta);
					parseScenario(scenario);
					return scenario;
				}
			}
			isNewTextBlock = StringUtils.isWhitespace(line);
		}
		return null;
	}
	
	// TODO to refactor
	private void parseScenario(Scenario scenario) throws IOException {
		String line;
		Sentence sentence = null;
		isNewTextBlock = true;
		while ((line = reader.readLine()) != null) {
			Keyword keyword;
			if (isNewTextBlock) {
				keyword = keywordParser.getKeyword(line, KeywordType.META);
				if (keyword != null) {
					meta.add(new Sentence(keyword, line));
					continue;
				}
				keyword = keywordParser.getKeyword(line, KeywordType.SCENARIO);
				if (keyword != null) {
					reader.resetLastLine();
					break;
				}
			}
			meta.clear();
			if (isNewTextBlock) {
				keyword = keywordParser.getKeyword(line, KeywordType.EXAMPLES);
				if (keyword != null) {
					sentence = new Sentence(keyword, line);
					scenario.add(sentence);
					isNewTextBlock = false;
					continue;
				}
			}
			
			keyword = keywordParser.getKeyword(line, KeywordType.GIVEN, KeywordType.WHEN, KeywordType.THEN);
			if (keyword != null) {
				sentence = new Sentence(keyword, line);
				scenario.add(sentence);
				isNewTextBlock = false;
				continue;
			}
			
			if (!scenario.getSentences().isEmpty()) {
				keyword = keywordParser.getKeyword(line, KeywordType.AND);
				if (keyword != null) {
					sentence = new Sentence(keyword, line);
					scenario.add(sentence);
					isNewTextBlock = false;
					continue;
				}
			}
			
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
