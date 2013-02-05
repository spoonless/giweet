package org.giweet.step;

public class StaticStepToken extends AbstractStepToken {
	
	public StaticStepToken(String value) {
		this(value, value);
	}

	public StaticStepToken(String value, String stringRepresentation) {
		super(value, stringRepresentation);
	}

	@Override
	public boolean isDynamic() {
		return false;
	}
	
	@Override
	public boolean isSeparator() {
		return false;
	}

	@Override
	public boolean isWhitespace() {
		return false;
	}
}
