package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class InvocableSetterMethod implements InvocableMethod {

	private final Object instance;
	private final Method method;
	
	public InvocableSetterMethod(Object instance, String... path) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (int i = 0; i < path.length - 1; i++) {
			Method readerMethod = JavaBeanUtils.getReaderMethod(instance.getClass(), path[i]);
			instance = readerMethod.invoke(instance);
			if (instance == null) {
				throw new NullPointerException("On instance " + instance + " method " + readerMethod.getName() + " returns null");
			}
		}
		this.instance = instance;
		String property = path[path.length - 1];
		this.method = JavaBeanUtils.getWriterMethod(instance.getClass(), property);
	}

	public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(instance, args);
	}

	public Type[] getGenericParameterTypes() {
		return method.getGenericParameterTypes();
	}
	
	public Annotation[][] getParameterAnnotations() {
		return method.getParameterAnnotations();
	}
}
