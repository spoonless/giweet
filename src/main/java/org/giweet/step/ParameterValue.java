package org.giweet.step;

public interface ParameterValue {
	int getDynamicTokenPosition();
	
	StepToken getDynamicToken();
	
	int getValueTokenStartPosition();

	int getValueTokenEndPosition();

	StepToken[] getValueTokens();
	
	String getValue();
}
