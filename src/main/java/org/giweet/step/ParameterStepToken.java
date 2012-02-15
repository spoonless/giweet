package org.giweet.step;

public class ParameterStepToken extends AbstractStepToken {
	
	public ParameterStepToken(String name) {
		super(name);
	}

	public boolean isParameter() {
		return true;
	}
	@Override
	public String toString() {
		return "$" + super.toString();
	}
	
	public boolean isMeaningful() {
		return true;
	}

}
