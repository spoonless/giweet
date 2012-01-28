package org.spoonless;

public class DynamicToken extends AbstractStepToken {
	
	public DynamicToken(String name) {
		super(name);
	}

	public boolean isDynamic() {
		return true;
	}
	@Override
	public String toString() {
		return "$" + super.toString();
	}

}
