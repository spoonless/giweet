package org.giweet.scenario;

import java.util.ArrayList;
import java.util.List;

public abstract class TextBlock {
	private List<Sentence> meta = new ArrayList<Sentence>();
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

	public List<Sentence> getMeta() {
		return meta;
	}

	public void setMeta(List<Sentence> meta) {
		this.meta = meta;
	}
	
	protected Sentence getFirstSentenceByKeywordType(KeywordType keywordType) {
		for (Sentence sentence : sentences) {
			if (sentence.isProcessable() && sentence.getKeyword().getType() == keywordType) {
				return sentence;
			}
		}
		return null;
	}
}
