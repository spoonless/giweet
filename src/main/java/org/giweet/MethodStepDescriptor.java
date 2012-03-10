package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.giweet.step.StepDescriptor;

public class MethodStepDescriptor extends StepDescriptor implements InvocableMethod {

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
	
	public InvocableMethod getInvocableSetterMethod(String[] path) throws IllegalAccessException, InvocationTargetException, IllegalArgumentException, NoSuchMethodException {
		return new InvocableSetterMethod(instance, path);
	}
}
