package org.giweet.converter;

import java.lang.annotation.Annotation;

public class EnumConverter implements Converter {

	public Class<?>[] getSupportedClasses() {
		return new Class<?>[] {Enum.class};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! targetClass.isEnum()) {
			throw new CannotConvertException(targetClass, value);
		}
		Object result = convertEnumFromToString(targetClass, value);
		if (result == null) {
			result = convertEnumFromName(targetClass, value);
		}
		if (result == null) {
			throw new CannotConvertException(targetClass, value);
		}
		return result;
	}
	
	private Object convertEnumFromToString(Class<?> targetClass, String value) {
		Object[] enumConstants = targetClass.getEnumConstants();
		Object result = null;
		for (Object enumConstant : enumConstants) {
			if (enumConstant.toString().equals(value)) {
				result = enumConstant;
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private Object convertEnumFromName(Class<?> targetClass, String value) {
		Object[] enumConstants = targetClass.getEnumConstants();
		Object result = null;
		for (Object enumConstant : enumConstants) {
			if (((Enum)enumConstant).name().equals(value)) {
				result = enumConstant;
				break;
			}
		}
		return result;
	}
}
