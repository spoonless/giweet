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
	
	public final Object convert(Class<?> targetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		if (! canConvert(targetClass)) {
			throw new CannotConvertException(targetClass, stepTokens);
		}
		Object result = null;
		if (! targetClass.isArray()) {
			result = convertSingle(targetClass, annotations, stepTokens);
		}
		else {
			result = convertArray(getSimpleType(targetClass), annotations, stepTokens);
		}
		return result;
	}

	protected abstract Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException;

	protected abstract Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException;

	protected Class<?> getSimpleType(Class<?> targetClass) {
		if (targetClass.isArray()) {
			targetClass = targetClass.getComponentType();
		}
		return targetClass;
	}

	protected static List<String> getMeaningfulValues(StepToken[] stepTokens) {
		List<String> result = new ArrayList<String>(stepTokens.length);
		for (StepToken stepToken : stepTokens) {
			if (stepToken.isMeaningful()) {
				result.add(String.valueOf(stepToken));
			}
		}
		return result;
	}
}
