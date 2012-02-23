package org.giweet.step.converter;

import java.lang.annotation.Annotation;

public class BooleanConverter implements Converter {

	private final String[] patterns;

	public BooleanConverter(String... patterns) {
		this.patterns = patterns;
	}
	
	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{boolean.class, Boolean.class};
	}
	
	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! boolean.class.equals(targetClass) && ! Boolean.class.equals(targetClass)) {
			throw new CannotConvertException(targetClass, value);
		}
		String[] patterns = getPatterns(annotations);
		return applyPatterns(patterns, value);
	}

	private String[] getPatterns(Annotation[] annotations) {
		String[] patterns = Pattern.getPatterns(annotations);
		if (patterns == null || patterns.length == 0) {
			patterns = this.patterns;
		}
		return patterns;
	}
	
	private static boolean applyPatterns(String[] patterns, String value) {
		boolean result = false;
		for (String pattern : patterns) {
			if (pattern.equalsIgnoreCase(value)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
