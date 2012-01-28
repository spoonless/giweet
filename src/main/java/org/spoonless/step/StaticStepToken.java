package org.spoonless.step;

public class StaticStepToken extends AbstractStepToken {
	
	public StaticStepToken(String value) {
		super(value);
	}

	public boolean isDynamic() {
		return false;
	}
}
