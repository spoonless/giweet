package org.giweet.step.converter;

import java.lang.annotation.Annotation;

import org.giweet.step.StepToken;

public interface Converter {
	
	boolean canConvert(Class<?> targetClass);
	
	Object convert (Class<?> targetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException;

}
