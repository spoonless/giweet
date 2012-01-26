package org.spoonless;

public class ArgumentToken implements StepToken {
	
	private final String name;
	
	public ArgumentToken(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int compareTo(StepToken o) {
		if (o instanceof ArgumentToken) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ArgumentToken;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
