package org.giweet.step.converter;

import java.lang.annotation.Annotation;

public class SimpleStringConverter implements Converter {
	
	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{String.class};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		return value;
	}

}
