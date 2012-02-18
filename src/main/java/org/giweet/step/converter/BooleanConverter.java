package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;

public class BooleanConverter extends ArraySupportConverter {

	private final String[] patterns;

	public BooleanConverter(String... patterns) {
		this.patterns = patterns;
	}
	
	@Override
	protected boolean canConvertSingle(Class<?> targetClass) {
		return boolean.class.isAssignableFrom(targetClass) || Boolean.class.isAssignableFrom(targetClass);
	}
	
	@Override
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] values) {
		String[] patterns = getPatterns(annotations);
		return applyPatterns(patterns, StringUtils.toString(values));
	}

	@Override
	protected Object convertArray(Class<?> targetClass, Annotation[] annotations, StepToken[] values) {
		String[] patterns = getPatterns(annotations);
		List<StepToken> meaningfulStepTokens = getMeaningfulTokens(values);
		Object result = Array.newInstance(targetClass, meaningfulStepTokens.size());
		for (int i = 0 ; i < meaningfulStepTokens.size() ; i++) {
			boolean tokenValue = applyPatterns(patterns, String.valueOf(meaningfulStepTokens.get(i)));
			if (targetClass.isPrimitive()) {
				Array.setBoolean(result, i, tokenValue);
			}
			else {
				Array.set(result, i, Boolean.valueOf(tokenValue));
			}
		}
		return result;
	}

	private String[] getPatterns(Annotation[] annotations) {
		String[] patterns = Pattern.getPatterns(annotations);
		if (patterns == null || patterns.length == 0) {
			patterns = this.patterns;
		}
		return patterns;
	}
	
	private static boolean applyPatterns(String[] patterns, String value) {
		boolean result = false;
		for (String pattern : patterns) {
			if (pattern.equalsIgnoreCase(value)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
