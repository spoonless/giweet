package org.giweet.step;

public abstract class AbstractStepToken implements StepToken {
	
	protected String value;
	protected String stringRepresentation;
	
	public AbstractStepToken(String value, String stringRepresentation) {
		this.value = value;
		this.stringRepresentation = stringRepresentation;
	}

	@Override
	@Deprecated
	public final boolean isMeaningful() {
		return !isSeparator();
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public boolean merge(StepToken stepToken) {
		return false;
	}
	
	@Override
	public int compareTo(StepToken stepToken) {
		if (! this.isDynamic() && ! stepToken.isDynamic()) {
			return this.value.compareToIgnoreCase(stepToken.getValue());
		}
		else if (this.isDynamic() && stepToken.isDynamic()) {
			return 0;
		}
		else if (this.isDynamic()) {
			return 1;
		}
		else {
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StepToken) {
			return this.compareTo((StepToken) obj) == 0;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}
}
