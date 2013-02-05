package org.giweet.step;

public class DynamicStepToken extends StaticStepToken {
	
	public DynamicStepToken(String name, String stringRepresentation) {
		super(name, stringRepresentation);
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
