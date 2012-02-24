package org.giweet.converter;

import java.lang.annotation.Annotation;

import org.giweet.annotation.Param;

public class Pattern {
	
	private static final String[] EMPTY_ARRAY = new String[0];

	public static String[] getPatterns(Annotation[] annotations) {
		String[] patterns = EMPTY_ARRAY;
		for (Annotation annotation : annotations) {
			if (annotation instanceof Param) {
				Param paramAnnotation = (Param) annotation;
				patterns = paramAnnotation.pattern();
				break;
			}
		}
		return patterns;
	}

}
