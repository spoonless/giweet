package org.giweet.step;

public interface StepTokenValue {

	int getDynamicTokenPosition();
	
	StepToken getDynamicToken();
	
	int getStartPosition();

	int getEndPosition();

	StepToken[] getTokens();
	
	String getValue();
}
