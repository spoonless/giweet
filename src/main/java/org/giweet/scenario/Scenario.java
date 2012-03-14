package org.giweet.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Scenario {
	
	private String title;
	private Locale locale;
	private List<Sentence> sentences = new ArrayList<Sentence>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
