package org.giweet.annotation;

import java.lang.annotation.Annotation;

public class SeparatedByDouble implements SeparatedBy {
	
	private final String[] value;

	public SeparatedByDouble(String ... value) {
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