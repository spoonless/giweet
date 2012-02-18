package org.giweet.step.converter;

import java.lang.annotation.Annotation;

public interface Converter {
	
	boolean canConvert(Class<?> targetClass);
	
	Object convert (Class<?> targetClass, Annotation[] annotations, String value) throws Exception;

}
