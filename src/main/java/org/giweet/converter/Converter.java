package org.giweet.converter;

import java.lang.annotation.Annotation;

public interface Converter {
	
	Class<?>[] getSupportedClasses();
	
	Object convert (Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException;

}
