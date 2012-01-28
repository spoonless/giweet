package org.spoonless;


public class StepDescriptor implements Comparable<StepDescriptor> {
	
	private final String value;
	private final StepToken[] tokens;
	
	public StepDescriptor(String value) {
		this.value = value;
		this.tokens = new StepTokenizer(true).tokenize(value);
	}
	
	public String getValue() {
		return value;
	}
	
	public StepToken[] getTokens() {
		return tokens;
	}
	
	public int compareTo(StepDescriptor stepDescriptor) {
		StepToken [] otherStepTokens = stepDescriptor.getTokens();
		int result = 0;
		int nbTokens = Math.min(tokens.length, otherStepTokens.length);
		for (int i = 0 ; i < nbTokens && result == 0 ; i++) {
			result = tokens[i].compareTo(otherStepTokens[i]);
		}
		if (result == 0) {
			result = this.tokens.length - otherStepTokens.length;
			if (nbTokens > 0 && tokens[nbTokens - 1].isDynamic()) {
				result = -result;				
			}
		}
		if (result != 0) {
			result = result < 0 ? -1 : 1;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
