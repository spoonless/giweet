package org.giweet.step.converter;

import java.lang.annotation.Annotation;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;

public class SimpleStringConverter implements Converter {
	
	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{String.class};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException {
		return StringUtils.toString(values);
	}

}
