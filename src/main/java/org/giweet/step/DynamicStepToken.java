package org.giweet.step;

public class DynamicStepToken extends AbstractStepToken {
	
	public DynamicStepToken(String name) {
		super(name);
	}

	public boolean isDynamic() {
		return true;
	}
	
	public boolean isMeaningful() {
		return true;
	}

}
