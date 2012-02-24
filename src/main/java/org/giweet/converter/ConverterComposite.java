package org.giweet.converter;

import java.lang.annotation.Annotation;
import java.util.Collection;
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

	private Class<?> getRealTargetClass(Class<?> targetClass) {
		if (Collection.class.isAssignableFrom(targetClass)) {
			// FIXME type eraser : by default we assume strings
			// FIXME should we provide more complex implementation (handling case with annotations or partially implements solution for generic type discovery)
			// TODO maybe we should throw an exception to clearly indicates that array is suitable but not Collection
			targetClass = String.class;
		}
		return targetClass;
	}
	
	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		Class<?> realTargetClass = getRealTargetClass(targetClass);
		Converter converter = getConverter(realTargetClass);
		if (converter == null) {
			throw new CannotConvertException(targetClass, value);
		}
		return converter.convert(targetClass, annotations, value);
	}
}
