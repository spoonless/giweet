package org.giweet.step;

public class SeparatorStepToken extends AbstractStepToken {
	
	public SeparatorStepToken(String value, String stringRepresentation) {
		super(value, stringRepresentation);
	}

	@Override
	public boolean isDynamic() {
		return false;
	}
	
	@Override
	public boolean isSeparator() {
		return true;
	}

	@Override
	public boolean isWhitespace() {
		return value.length() == 0;
	}
	
	@Override
	public boolean merge(StepToken stepToken) {
		if (stepToken.isSeparator() && (this.isWhitespace() || stepToken.isWhitespace())) {
			value += stepToken.getValue();
			stringRepresentation += stepToken.toString();
			return true;
		}
		return false;
	}
}
