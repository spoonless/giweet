package org.giweet.scenario.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;
import org.giweet.scenario.Story;
import org.giweet.scenario.TextBlock;

public class TextScenarioParser {

	private final KeywordParser keywordParser;
	private final BufferedReader reader;
	private TextBlock nextTextBlock;
	private Story currentStory;
	private final List<Sentence> meta;

	public TextScenarioParser(KeywordParser keywordParser, Reader reader) {
		this.keywordParser = keywordParser;
		this.reader = new BufferedReader(reader);
		currentStory = new Story();
		meta = new ArrayList<Sentence>();
	}
	
	public Scenario nextScenario() throws IOException {
		Scenario scenario = null;
		TextBlock textBlock;
		do {
			textBlock = nextTextBlock();
			if (textBlock instanceof Story) {
				currentStory = (Story) textBlock;
			}
			else if (textBlock instanceof Scenario) {
				scenario = (Scenario) textBlock;
				scenario.setStory(currentStory);
			}
		} while (textBlock != null && scenario == null);
		return scenario;
	}

	private TextBlock nextTextBlock() throws IOException {
		TextBlock textBlock = nextTextBlock;
		nextTextBlock = null;
		String line;
		while ((line = reader.readLine()) != null) {
			line += '\n';
			Sentence sentence = new Sentence(keywordParser.getStartingKeyword(line), line);
			TextBlock textBlockWithSentence = addSentenceIntoTextBlock(textBlock, sentence);
			if (textBlock == null) {
				textBlock = textBlockWithSentence;
			}
			else if (textBlock != textBlockWithSentence) {
				nextTextBlock = textBlockWithSentence;
				break;
			}
		}
		return textBlock;
	}

	private TextBlock addSentenceIntoTextBlock(TextBlock textBlock, Sentence sentence) {
		if (sentence.isProcessable()) {
			switch (sentence.getKeyword().getType()) {
			case META:
				meta.add(sentence);
				break;
			case STORY:
				textBlock = new Story();
				textBlock.addAll(meta);
				meta.clear();
				textBlock.add(sentence);
				break;
			case SCENARIO:
				textBlock = new Scenario();
				textBlock.addAll(meta);
				meta.clear();
				textBlock.add(sentence);
				break;
			default:
				if (textBlock == null) {
					textBlock = new Scenario();
					textBlock.addAll(meta);
					meta.clear();
				} else {
					// FIXME to refactor
					for (Sentence invalidMeta : meta) {
						textBlock.add(new Sentence(invalidMeta.toString()));
					}
					meta.clear();
				}
				if (textBlock instanceof Story) {
					textBlock = new Scenario();
				}
				textBlock.add(sentence);
				break;
			}
		}
		else if (textBlock != null) {
			// FIXME to refactor
			for (Sentence invalidMeta : meta) {
				textBlock.add(new Sentence(invalidMeta.toString()));
			}
			meta.clear();
			textBlock.add(sentence);
		}
		return textBlock;
	}
}
