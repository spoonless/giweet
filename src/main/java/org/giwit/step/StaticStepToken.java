package org.giwit.step;

public class StaticStepToken extends AbstractStepToken {
	
	private final boolean isMeaningful;

	public StaticStepToken(String value) {
		this(value, true);
	}

	public StaticStepToken(String value, boolean isMeaningful) {
		super(value);
		this.isMeaningful = isMeaningful;
	}

	public boolean isDynamic() {
		return false;
	}
	
	public boolean isMeaningful() {
		return isMeaningful;
	}
}
