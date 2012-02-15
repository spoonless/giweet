package org.giweet.step;

public interface ParameterValue {
	int getParameterTokenPosition();
	
	StepToken getParameterToken();
	
	int getValueTokenStartPosition();

	int getValueTokenEndPosition();

	StepToken[] getValueTokens();
	
	String getValue();
}
