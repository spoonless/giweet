package org.giweet;

public class InvalidStepException extends Exception {

	private static final long serialVersionUID = 5791348029717959117L;
	
	public InvalidStepException() {
	}

	public InvalidStepException(String message) {
		super(message);
	}
}
