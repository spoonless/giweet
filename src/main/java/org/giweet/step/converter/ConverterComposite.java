package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;

public class ConverterComposite implements Converter {
	
	private final List<Converter> converters = new ArrayList<Converter>();
	
	public ConverterComposite(Converter... converters) {
		this.converters.addAll(Arrays.asList(converters));
	}

	public boolean canConvert(Class<?> targetClass) {
		targetClass = getRealTargetClass(targetClass);
		return getConverter(targetClass) != null;
	}
	
	private Converter getConverter(Class<?> targetClass) {
		for (Converter converter : converters) {
			if (converter.canConvert(targetClass)) {
				return converter;
			}
		}
		return null;
	}

	private Class<?> getRealTargetClass(Class<?> targetClass) {
		if (Collection.class.isAssignableFrom(targetClass)) {
			// FIXME type eraser : by default we assume strings
			// FIXME should we provide more complex implementation (handling case with annotations or partially implements solution for generic type discovery)
			// TODO maybe we should throw an exception to clearly indicates that array is suitable but not Collection
			targetClass = String.class;
		}
		return targetClass;
	}
	
	public Object convert(Class<?> targetClass, Annotation[] annotations, StepToken[] value) throws CannotConvertException {
		Class<?> realTargetClass = getRealTargetClass(targetClass);
		Converter converter = getConverter(realTargetClass);
		if (converter == null) {
			throw new CannotConvertException(targetClass, StringUtils.toString(value));
		}
		return converter.convert(targetClass, annotations, value);
	}
}
