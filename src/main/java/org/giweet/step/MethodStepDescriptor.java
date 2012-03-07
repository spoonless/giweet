package org.giweet.step;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodStepDescriptor extends StepDescriptor {

	private final Method method;
	private final Object instance;

	public MethodStepDescriptor(Method method, Object instance, String stepValue) {
		super(stepValue);
		this.method = method;
		this.instance = instance;
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
