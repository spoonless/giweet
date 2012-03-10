package org.giweet.converter;

import java.lang.annotation.Annotation;

import org.giweet.annotation.Pattern;

public class PatternUtils {
	
	private static final String[] EMPTY_ARRAY = new String[0];
	
	private PatternUtils() {
	}

	public static String[] getPatterns(Annotation[] annotations) {
		String[] patterns = EMPTY_ARRAY;
		for (Annotation annotation : annotations) {
			if (annotation instanceof Pattern) {
				Pattern patternAnnotation = (Pattern) annotation;
				patterns = patternAnnotation.value();
				break;
			}
		}
		return patterns;
	}

}
