package org.giweet.scenario;

import java.util.ArrayList;
import java.util.List;


public class Scenario {
	
	private final Sentence title;
	private final List<Sentence> sentences = new ArrayList<Sentence>();
	private final List<Sentence> meta = new ArrayList<Sentence>();

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
		return meta;
	}
	
	public Sentence getTitle() {
		return title;
	}
}
