package org.spoonless;

public class DynamicStepToken extends AbstractStepToken {
	
	public DynamicStepToken(String name) {
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
