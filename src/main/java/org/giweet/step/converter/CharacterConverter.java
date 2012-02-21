package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.util.List;

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
		return convertChar(baseTargetClass, value);
	}

	private char convertChar(Class<?> baseTargetClass, String value) throws CannotConvertException {
		if (value.length() != 1) {
			throw new CannotConvertException(baseTargetClass, value);
		}
		return value.charAt(0);
	}

	@Override
	protected Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		List<String> values = getMeaningfulValues(stepTokens);
		Object result = null;
		if (char.class.equals(baseTargetClass)) {
			char[] charArray = new char[values.size()];
			int i = 0;
			for (String value : values) {
				charArray[i++] = convertChar(baseTargetClass, value);
			}
			result = charArray;
		}
		else {
			Character[] characterArray = new Character[values.size()];
			int i = 0;
			for (String value : values) {
				characterArray[i++] = convertChar(baseTargetClass, value);
			}
			result = characterArray;
		}
		return result;
	}

}
