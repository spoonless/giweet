package org.spoonless;

public class StringToken extends AbstractStepToken {
	
	public StringToken(String value) {
		super(value);
	}

	public boolean isDynamic() {
		return false;
	}
}
