package org.giweet.step.converter;

import java.lang.annotation.Annotation;

import org.giweet.annotation.Param;

public class Pattern {

	public static String[] getPatterns(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Param) {
				Param paramAnnotation = (Param) annotation;
				return paramAnnotation.pattern();
			}
		}
		return null;
	}

}
