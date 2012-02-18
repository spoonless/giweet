package org.giweet.annotation;

import java.lang.annotation.Annotation;

import org.giweet.annotation.Param;

public class ParamDouble implements Param {
	
	private final String[] pattern;

	public ParamDouble(String ... pattern) {
		this.pattern = pattern;
	}
	
	public Class<? extends Annotation> annotationType() {
		return null;
	}

	public String[] pattern() {
		return pattern;
	}

	public String[] name() {
		return null;
	}
}