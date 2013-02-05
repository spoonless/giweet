package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.giweet.annotation.Given;
import org.giweet.annotation.Then;
import org.giweet.annotation.When;
import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;

public class MethodStepDeclaration extends StepDeclaration implements InvocableMethod {

	private final Method method;
	private final Object instance;
	private final int typeMask;

	public MethodStepDeclaration(Method method, Object instance, String stepValue) throws QuoteTailNotFoundException {
		super(stepValue);
		this.method = method;
		this.instance = instance;
		this.typeMask = createTypeMask();
	}

	private int createTypeMask() {
		int typeMask = 0;
		if (method.getAnnotation(Given.class) != null) {
			typeMask |= 1 << StepType.GIVEN.ordinal();
		}
		if (method.getAnnotation(When.class) != null) {
			typeMask |= 1 << StepType.WHEN.ordinal();
		}
		if (method.getAnnotation(Then.class) != null) {
			typeMask |= 1 << StepType.THEN.ordinal();
		}
		
		if (typeMask == 0) {
			typeMask = ~typeMask;
		}
		return typeMask;
	}
	
	@Override
	public boolean isOfType(StepType type) {
		return (typeMask & (1 << type.ordinal())) > 0;
	}
	
	@Override
	public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return method.invoke(instance, args);
	}

	@Override
	public Type[] getGenericParameterTypes() {
		return method.getGenericParameterTypes();
	}
	
	@Override
	public Annotation[][] getParameterAnnotations() {
		return method.getParameterAnnotations();
	}
	
	public InvocableMethod getInvocableSetterMethod(String[] path) throws IllegalAccessException, InvocationTargetException, IllegalArgumentException, NoSuchMethodException {
		return new InvocableSetterMethod(instance, path);
	}
}
