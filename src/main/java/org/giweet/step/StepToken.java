package org.giweet.step;

public interface StepToken extends Comparable<StepToken> {
	
	boolean isDynamic();
	
	@Deprecated
	boolean isMeaningful();
	
	boolean isSeparator();
	
	boolean isWhitespace();
	
	String getValue();

	boolean merge(StepToken stepToken);

}
