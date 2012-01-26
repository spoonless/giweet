package org.spoonless;

public class StringToken implements StepToken {
	
	private String value;
	
	public StringToken(String value) {
		this.value = value;
	}
	
	public int compareTo(StepToken o) {
		if (o instanceof StringToken) {
			return this.value.compareToIgnoreCase(((StringToken) o).value);
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringToken) {
			return compareTo((StringToken)obj) == 0;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return value;
	}

}
