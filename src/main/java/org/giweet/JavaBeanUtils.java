package org.giweet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JavaBeanUtils {
	
	private JavaBeanUtils() {
	}

	public static Method getWriterMethod(Class<?> clazz, String property) throws NoSuchMethodException {
		String normalizedProperty = StringUtils.toStringWithFirstUpperLetter(property);
		Method expectedMethod = null;
		for (Method method : clazz.getMethods()) {
			if (isCandidateMethod(method, normalizedProperty, 1)) {
				String methodPrefix = getMethodNamePrefix(method, normalizedProperty);
				if ("set".equals(methodPrefix)) {
					expectedMethod = method;
					break;
				}
			}
		}
		if (expectedMethod == null) {
			throw new NoSuchMethodException("Cannot find public setter method for property \"" + property + "\" in class " + clazz);
		}
		return expectedMethod;
	}

	public static Method getReaderMethod(Class<?> clazz, String property) throws NoSuchMethodException {
		String normalizedProperty = StringUtils.toStringWithFirstUpperLetter(property);
		Method expectedMethod = null;
		for (Method method : clazz.getMethods()) {
			if (isCandidateMethod(method, normalizedProperty, 0) && ! Void.TYPE.equals(method.getReturnType())) {
				String methodPrefix = getMethodNamePrefix(method, normalizedProperty);
				if ("get".equals(methodPrefix)) {
					expectedMethod = method;
					break;
				}
				else if ("is".equals(methodPrefix) && isBooleanType(method.getReturnType())) {
					expectedMethod = method;
					break;
				}
			}
		}
		if (expectedMethod == null) {
			throw new NoSuchMethodException("Cannot find public getter method for property \"" + property + "\" in class " + clazz);
		}
		return expectedMethod;
	}

	private static boolean isBooleanType(Class<?> type) {
		return boolean.class.equals(type) || Boolean.class.equals(type);
	}

	private static String getMethodNamePrefix(Method method, String property) {
		return method.getName().substring(0, method.getName().length() - property.length());
	}

	private static boolean isCandidateMethod(Method method, String property, int nbParam) {
		return (method.getModifiers() & Modifier.PUBLIC) > 0
				&& method.getParameterTypes().length == nbParam
				&& method.getName().endsWith(property); 
	}
}
