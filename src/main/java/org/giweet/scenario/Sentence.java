package org.giweet.scenario;

public class Sentence {
	
	private Keyword keyword;
	private String step;
	
	public Keyword getKeyword() {
		return keyword;
	}
	
	public void setKeyword(Keyword keyword) {
		this.keyword = keyword;
	}
	
	public String getStep() {
		return step;
	}
	
	public void setStep(String step) {
		this.step = step;
	}
	
	@Override
	public String toString() {
		return getKeyword() + getStep();
	}

}
