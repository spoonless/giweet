package org.giweet.step;

public interface StepToken extends Comparable<StepToken> {
	
	boolean isParameter();
	
	boolean isMeaningful();

}
