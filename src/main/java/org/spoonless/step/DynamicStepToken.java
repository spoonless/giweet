package org.spoonless.step;

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
	
	public boolean isMeaningful() {
		return true;
	}

}
