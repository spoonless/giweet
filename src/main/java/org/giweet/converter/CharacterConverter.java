package org.giweet.converter;

import java.lang.annotation.Annotation;

public class CharacterConverter implements Converter {

	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{char.class, Character.class};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! char.class.equals(targetClass) && ! Character.class.equals(targetClass)) {
			throw new CannotConvertException(targetClass, value);
		}
		if (value.length() != 1) {
			throw new CannotConvertException(targetClass, value);
		}
		return value.charAt(0);
	}
}
