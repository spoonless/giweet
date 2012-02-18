package org.giweet.step.converter;

import java.lang.annotation.Annotation;

public class BooleanConverter implements Converter {

	private final String[] patterns;

	public BooleanConverter(String... patterns) {
		this.patterns = patterns;
	}

	public boolean canConvert(Class<?> targetClass) {
		return Boolean.class.isAssignableFrom(targetClass);
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! canConvert(targetClass)) {
			throw new CannotConvertException(targetClass, value);
		}
		boolean result = Boolean.FALSE;
		String[] patterns = Pattern.getPatterns(annotations);
		if (patterns == null || patterns.length == 0) {
			patterns = this.patterns;
		}
		for (String pattern : patterns) {
			if (pattern.equalsIgnoreCase(value)) {
				result = Boolean.TRUE;
				break;
			}
		}
		return result;
	}

}
