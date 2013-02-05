package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.giweet.annotation.SeparatedBy;
import org.giweet.converter.CannotConvertException;
import org.giweet.converter.Converter;
import org.giweet.step.StepToken;
import org.giweet.step.StepTokenArraySplitter;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;

public class ParamStepConverter {
	
	private final Converter converter;
	private final StepTokenArraySplitter stepTokenArraySplitter;
	
	public ParamStepConverter(Converter converter, String... listSeparators) throws QuoteTailNotFoundException {
		this.converter = converter;
		this.stepTokenArraySplitter = new StepTokenArraySplitter(listSeparators);
	}
	
	public Object convert(Type type, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException, QuoteTailNotFoundException {
		Object result;
		if (type instanceof ParameterizedType) {
			result = convertToList(type, annotations, stepTokens);
		}
		else if (type instanceof Class) {
			result = convertToClass((Class<?>) type, annotations, stepTokens);
		}
		else {
			throw new CannotConvertException(type.getClass(), StringUtils.toString(stepTokens));
		}
		return result;
	}

	private Object convertToClass(Class<?> targetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException, QuoteTailNotFoundException {
		Object result;
		if (targetClass.isAssignableFrom(List.class)) {
			// TODO warning here
			Object[] array = Object[].class.cast(convertToArray(String.class, annotations, stepTokens));
			result = Arrays.asList(array);
		}
		else if (targetClass.isArray()) {
			result = convertToArray(targetClass.getComponentType(), annotations, stepTokens);
		}
		else {
			result = converter.convert(targetClass, annotations, StringUtils.toString(stepTokens));
		}
		return result;
	}

	private Object convertToList(Type type, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException, QuoteTailNotFoundException {
		Object result;
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Class<?> collectionType = (Class<?>)parameterizedType.getRawType();
		if (!collectionType.isAssignableFrom(List.class)) {
			throw new CannotConvertException(collectionType, StringUtils.toString(stepTokens));
		}
		Type collectionElementType = parameterizedType.getActualTypeArguments()[0];
		if (!(collectionElementType instanceof Class)) {
			throw new CannotConvertException(collectionElementType.getClass(), StringUtils.toString(stepTokens));
		}
		Object[] array = Object[].class.cast(convertToArray((Class<?>) collectionElementType, annotations, stepTokens));
		result = Arrays.asList(array);
		return result;
	}

	private Object convertToArray(Class<?> realType, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException, QuoteTailNotFoundException {
		StepToken[][] splittedStepTokens = getStepTokenArraySplitter(annotations).split(stepTokens);
		Object array = Array.newInstance(realType, splittedStepTokens.length);
		int index = 0;
		for (StepToken[] splittedStepToken : splittedStepTokens) {
			Object o = converter.convert(realType, annotations, StringUtils.toString(splittedStepToken));
			Array.set(array, index++, o);
		}
		return array;
	}
	
	private StepTokenArraySplitter getStepTokenArraySplitter(Annotation[] annotations) throws QuoteTailNotFoundException {
		for (Annotation annotation : annotations) {
			if (annotation instanceof SeparatedBy) {
				return new StepTokenArraySplitter(((SeparatedBy)annotation).value());
			}
		}
		return this.stepTokenArraySplitter;
	}

}
