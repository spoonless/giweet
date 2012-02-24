package org.giweet.converter;

import java.lang.annotation.Annotation;

public class EnumConverter implements Converter {

	public Class<?>[] getSupportedClasses() {
		return new Class<?>[] {Enum.class};
	}

	@SuppressWarnings("rawtypes")
	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! targetClass.isEnum()) {
			throw new CannotConvertException(targetClass, value);
		}
		Object[] enumConstants = targetClass.getEnumConstants();
		Object result = null;
		for (Object enumConstant : enumConstants) {
			if (enumConstant.toString().equals(value)) {
				result = enumConstant;
				break;
			}
		}
		if (result == null) {
			for (Object enumConstant : enumConstants) {
				if (((Enum)enumConstant).name().equals(value)) {
					result = enumConstant;
					break;
				}
			}
		}
		if (result == null) {
			throw new CannotConvertException(targetClass, value);
		}
		return result;
	}

}
