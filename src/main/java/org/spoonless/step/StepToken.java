package org.spoonless.step;

public interface StepToken extends Comparable<StepToken> {
	
	boolean isDynamic();
	
	boolean isMeaningful();

}
