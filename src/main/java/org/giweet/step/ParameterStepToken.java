package org.giweet.step;

public class ParameterStepToken extends AbstractStepToken {
	
	public ParameterStepToken(String name) {
		super(name);
	}

	public boolean isParameter() {
		return true;
	}
	
	public boolean isMeaningful() {
		return true;
	}

}
