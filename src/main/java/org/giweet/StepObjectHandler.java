package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.giweet.annotation.Given;
import org.giweet.annotation.Setup;
import org.giweet.annotation.Step;
import org.giweet.annotation.Teardown;
import org.giweet.annotation.Then;
import org.giweet.annotation.When;
import org.giweet.step.StepUtils;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;

public class StepObjectHandler {
	
	private final Object instance;
	
	public StepObjectHandler(Object instance) {
		this.instance = instance;
	}
	
	public void check() throws InvalidStepException {
		int nbStepMethods = 0;
		Class<?> classToCheck = instance.getClass();
		while (classToCheck != Object.class) {
			nbStepMethods += check(classToCheck);
			classToCheck = classToCheck.getSuperclass();
		}

		if (nbStepMethods == 0) {
			throw new InvalidStepException("Class must declare or inherit at least one public method with @Step annotation ! Found class instance without step of type: " + instance.getClass().getName());
		}
	}
	
	private int check(Class<?> classToCheck) throws InvalidStepException {
		int nbStepMethods = 0;
		for (Method method : classToCheck.getDeclaredMethods()) {
			if (check(method)) {
				nbStepMethods++;
			}
		}
		return nbStepMethods;
	}

	private boolean check(Method method) throws InvalidStepException {
		boolean isValidStep = false;
		if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
			checkAnnotationOnNonPublicMethod(Step.class, method);
			checkAnnotationOnNonPublicMethod(Setup.class, method);
			checkAnnotationOnNonPublicMethod(Teardown.class, method);
			checkAnnotationOnNonPublicMethod(Given.class, method);
			checkAnnotationOnNonPublicMethod(When.class, method);
			checkAnnotationOnNonPublicMethod(Then.class, method);
		}
		else {
			checkAnnotatedMethodHasNoParameter(Setup.class, method);
			checkAnnotatedMethodHasNoParameter(Teardown.class, method);
			if (method.isAnnotationPresent(Step.class)) {
				isValidStep = true;
			}
			else {
				checkGivenWhenThenNotUse(Given.class, method);
				checkGivenWhenThenNotUse(When.class, method);
				checkGivenWhenThenNotUse(Then.class, method);
			}
		}
		return isValidStep;
	}
	
	private void checkAnnotationOnNonPublicMethod(Class<? extends Annotation> annotation, Method method) throws InvalidStepException {
		if (method.isAnnotationPresent(annotation)) {
			throw new InvalidStepException("Method with @" + annotation.getSimpleName() + " annotation must be declared public! Found non public method: " + method.toString());
		}
	}
	
	private void checkGivenWhenThenNotUse(Class<? extends Annotation> annotation, Method method) throws InvalidStepException {
		if (method.isAnnotationPresent(annotation)) {
			throw new InvalidStepException("@" + annotation.getSimpleName() + " annotation must be used with @Step annotation! Found method without @Step annotation: " + method.toString());
		}
	}

	private void checkAnnotatedMethodHasNoParameter(Class<? extends Annotation> annotation, Method method) throws InvalidStepException {
		if (method.isAnnotationPresent(annotation) && method.getParameterTypes().length > 0) {
			throw new InvalidStepException("Method with @" + annotation.getSimpleName() + " annotation must have no argument! Found method with arguments: " + method.toString());
		}
	}
	
	public Collection<MethodStepDeclaration> getMethodStepDeclarations() throws QuoteTailNotFoundException {
		List<MethodStepDeclaration> methodStepDeclarations = new ArrayList<MethodStepDeclaration>();
		for (Method method : instance.getClass().getMethods()) {
			addMethodStepDeclaration(methodStepDeclarations, method);
		}
		
		return methodStepDeclarations;
	}

	private void addMethodStepDeclaration(List<MethodStepDeclaration> methoStepDeclarations, Method method) throws QuoteTailNotFoundException {
		Step stepAnnotation = method.getAnnotation(Step.class);
		if (stepAnnotation != null) {
			if (stepAnnotation.value().length != 0) {
				for (String value : stepAnnotation.value()) {
					methoStepDeclarations.add(new MethodStepDeclaration(method, instance, value));
				}
			}
			else {
				methoStepDeclarations.add(new MethodStepDeclaration(method, instance, StepUtils.getStepFromJavaIdentifier(method.getName())));
			}
		}
	}
	
	public void setup() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		invokeAnnotatedMethods(Setup.class, instance.getClass().getMethods(), instance.getClass(), true);
	}
	
	public void teardown() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		invokeAnnotatedMethods(Teardown.class, instance.getClass().getMethods(), instance.getClass(), false);
	}

	private void invokeAnnotatedMethods(Class<? extends Annotation> annotation, Method[] methods, Class<?> currentClass, boolean isSuperClassFirst) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (isSuperClassFirst && currentClass.getSuperclass() != Object.class) {
			invokeAnnotatedMethods(annotation, methods, currentClass.getSuperclass(), isSuperClassFirst);
		}
		for (Method method : methods) {
			if (method.getDeclaringClass() == currentClass && method.isAnnotationPresent(annotation)) {
				method.invoke(instance);
			}
		}
		if (! isSuperClassFirst && currentClass.getSuperclass() != Object.class) {
			invokeAnnotatedMethods(annotation, methods, currentClass.getSuperclass(), isSuperClassFirst);
		}
	}
}
