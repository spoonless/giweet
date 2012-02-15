package org.giweet.step;

public interface StepToken extends Comparable<StepToken> {
	
	boolean isDynamic();
	
	boolean isMeaningful();

}
