package org.giweet.step.converter;

import java.lang.annotation.Annotation;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;

public class CharacterConverter extends ArraySupportConverter {

	@Override
	protected boolean canConvertSingle(Class<?> targetClass) {
		return char.class.equals(targetClass) || Character.class.equals(targetClass);
	}

	@Override
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		String value = StringUtils.toString(stepTokens);
		if (value.length() != 1) {
			throw new CannotConvertException(baseTargetClass, value);
		}
		return value.charAt(0);
	}

	@Override
	protected Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		String value = StringUtils.toString(stepTokens);
		Object result = null;
		if (char.class.equals(baseTargetClass)) {
			result = value.toCharArray();
		}
		else {
			char[] charArray = value.toCharArray();
			Character[] characterArray = new Character[charArray.length];
			for (int i = 0; i < charArray.length; i++) {
				characterArray[i] = charArray[i];
			}
			result = characterArray;
		}
		return result;
	}

}
