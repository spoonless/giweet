package org.giweet.annotation;

import java.lang.annotation.Annotation;

public class PatternDouble implements Pattern {
	
	private final String[] value;

	public PatternDouble(String ... value) {
		this.value = value;
	}
	
	public Class<? extends Annotation> annotationType() {
		return null;
	}

	public String[] value() {
		return value;
	}

	public String[] name() {
		return null;
	}
}