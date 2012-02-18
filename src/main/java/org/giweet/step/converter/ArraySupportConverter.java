package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.giweet.step.StepToken;

public abstract class ArraySupportConverter implements Converter {

	public final boolean canConvert(Class<?> targetClass) {
		targetClass = getSimpleType(targetClass);
		return canConvertSingle(targetClass);
	}

	protected abstract boolean canConvertSingle(Class<?> targetClass) ;
	
	public final Object convert(Class<?> targetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException {
		if (! canConvert(targetClass)) {
			throw new CannotConvertException(targetClass, values);
		}
		Object result = null;
		if (! targetClass.isArray()) {
			result = convertSingle(targetClass, annotations, values);
		}
		else {
			result = convertArray(getSimpleType(targetClass), annotations, values);
		}
		return result;
	}

	protected abstract Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException;

	protected abstract Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException;

	protected Class<?> getSimpleType(Class<?> targetClass) {
		if (targetClass.isArray()) {
			targetClass = targetClass.getComponentType();
		}
		return targetClass;
	}

	protected static List<StepToken> getMeaningfulTokens(StepToken[] values) {
		List<StepToken> result = new ArrayList<StepToken>(values.length);
		for (StepToken stepToken : values) {
			if (stepToken.isMeaningful()) {
				result.add(stepToken);
			}
		}
		return result;
	}
}
