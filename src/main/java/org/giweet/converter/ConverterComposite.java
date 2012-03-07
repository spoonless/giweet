package org.giweet.converter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConverterComposite implements Converter {
	
	private Map<Class<?>, Converter> mapConvertersByClass = new HashMap<Class<?>, Converter>();
	
	public ConverterComposite(Converter... converters) {
		for (Converter converter : converters) {
			for (Class<?> supportedClasses : converter.getSupportedClasses()) {
				mapConvertersByClass.put(supportedClasses, converter);
			}
		}
	}
	
	public Class<?>[] getSupportedClasses() {
		Set<Class<?>> keySet = mapConvertersByClass.keySet();
		return keySet.toArray(new Class<?>[keySet.size()]);
	}

	private Converter getConverter(Class<?> targetClass) {
		return mapConvertersByClass.get(targetClass);
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		Converter converter = getConverter(targetClass);
		if (converter == null) {
			if (Enum.class.isAssignableFrom(targetClass)) {
				converter = getConverter(Enum.class);
			}
			if (converter == null) {
				throw new CannotConvertException(targetClass, value);
			}
		}
		return converter.convert(targetClass, annotations, value);
	}
}
