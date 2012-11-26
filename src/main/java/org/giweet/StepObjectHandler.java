package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.giweet.annotation.Given;
import org.giweet.annotation.Setup;
import org.giweet.annotation.Step;
import org.giweet.annotation.Teardown;
import org.giweet.annotation.Then;
import org.giweet.annotation.When;
import org.giweet.step.StepUtils;

public class StepObjectHandler {
	
	private final Object instance;
	
	public StepObjectHandler(Object instance) {
		this.instance = instance;
	}
	
	public void check() throws InvalidStepException {
		Class<?> classToCheck = instance.getClass();
		do {
			check(classToCheck);
			classToCheck = classToCheck.getSuperclass();
		} while (classToCheck != null);
	}
	
	private void check(Class<?> classToCheck) throws InvalidStepException {
		for (Method method : classToCheck.getDeclaredMethods()) {
			if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
				checkAnnotationOnNonPublicMethod(Step.class, method);
				checkAnnotationOnNonPublicMethod(Setup.class, method);
				checkAnnotationOnNonPublicMethod(Teardown.class, method);
			}

			if (! method.isAnnotationPresent(Step.class)) {
				checkGivenWhenThenNotUse(Given.class, method);
				checkGivenWhenThenNotUse(When.class, method);
				checkGivenWhenThenNotUse(Then.class, method);
			}
		}
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

	public Collection<MethodStepDeclaration> getMethodStepDeclarations() {
		List<MethodStepDeclaration> methoStepDeclarations = new ArrayList<MethodStepDeclaration>();
		for (Method method : instance.getClass().getMethods()) {
			addMethodStepDeclaration(methoStepDeclarations, method);
		}
		
		return methoStepDeclarations;
	}

	private void addMethodStepDeclaration(List<MethodStepDeclaration> methoStepDeclarations, Method method) {
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
	
	public void injectProperties(Properties properties, ParamStepConverter paramStepConverter) {
		throw new UnsupportedOperationException("not yet implemented!");
	}

	public void setup() {
		throw new UnsupportedOperationException("not yet implemented!");
	}

	public void teardown() {
		throw new UnsupportedOperationException("not yet implemented!");
	}
}
