package org.giwit.step;

public interface StepToken extends Comparable<StepToken> {
	
	boolean isDynamic();
	
	boolean isMeaningful();

}
