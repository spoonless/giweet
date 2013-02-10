package org.giweet.scenario;

import java.util.ArrayList;
import java.util.List;


public class Scenario {
	
	private final Sentence title;
	private List<Sentence> sentences = new ArrayList<Sentence>();

	public List<Sentence> getSentences() {
		return sentences;
	}
	
	public Scenario(Sentence title) {
		this.title = title;
	}

	public void add(Sentence sentence) {
		sentences.add(sentence);
	}

	public List<Sentence> getMeta() {
		return getSentencesByKeywordType(KeywordType.META);
	}
	
	protected Sentence getFirstSentenceByKeywordType(KeywordType keywordType) {
		for (Sentence sentence : sentences) {
			if (sentence.getKeyword().getType() == keywordType) {
				return sentence;
			}
		}
		return null;
	}

	protected List<Sentence> getSentencesByKeywordType(KeywordType keywordType) {
		List<Sentence> result = new ArrayList<Sentence>();
		for (Sentence sentence : sentences) {
			if (sentence.getKeyword().getType() == keywordType) {
				result.add(sentence);
			}
		}
		return result;
	}
	
	public Sentence getTitle() {
		return title;
	}
}
