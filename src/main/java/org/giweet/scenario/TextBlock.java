package org.giweet.scenario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TextBlock {
	private List<Sentence> sentences = new ArrayList<Sentence>();

	public List<Sentence> getSentences() {
		return sentences;
	}

	public abstract Sentence getTitle();

	public void add(Sentence sentence) {
		if (sentences.isEmpty()) {
			sentences.add(sentence);
		}
		else {
			Sentence previousSentence = sentences.get(sentences.size() - 1);
			if (! previousSentence.concat(sentence)) {
				sentences.add(sentence);
			}
		}
	}
	
	public void addAll(Collection<Sentence> sentences) {
		for (Sentence sentence : sentences) {
			this.add(sentence);
		}
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
}
