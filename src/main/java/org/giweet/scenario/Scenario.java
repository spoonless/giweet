package org.giweet.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Scenario {
	
	private Sentence title;
	private Locale locale;
	private List<Sentence> sentences = new ArrayList<Sentence>();

	public List<Sentence> getSentences() {
		return sentences;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Sentence getTitle() {
		return title;
	}

	public void setTitle(Sentence title) {
		this.title = title;
	}
	
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
}
