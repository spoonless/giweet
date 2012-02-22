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
	
	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{boolean.class, Boolean.class};
	}
	
	@Override
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) {
		String[] patterns = getPatterns(annotations);
		return applyPatterns(patterns, StringUtils.toString(stepTokens));
	}

	@Override
	protected Object convertArray(Class<?> targetClass, Annotation[] annotations, StepToken[] stepTokens) {
		String[] patterns = getPatterns(annotations);
		List<String> meaningfulValues = getMeaningfulValues(stepTokens);
		Object result = Array.newInstance(targetClass, meaningfulValues.size());
		for (int i = 0 ; i < meaningfulValues.size() ; i++) {
			boolean tokenValue = applyPatterns(patterns, meaningfulValues.get(i));
			Array.set(result, i, Boolean.valueOf(tokenValue));
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
