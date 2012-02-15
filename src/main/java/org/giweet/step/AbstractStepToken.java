package org.giweet.step;

public abstract class AbstractStepToken implements StepToken {
	
	private String value;
	
	public AbstractStepToken(String value) {
		this.value = value;
	}

	public int compareTo(StepToken stepToken) {
		if (! this.isDynamic() && ! stepToken.isDynamic()) {
			return this.toString().compareToIgnoreCase(stepToken.toString());
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
		return value;
	}
}
