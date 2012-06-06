package org.giweet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.giweet.annotation.Step;
import org.giweet.step.StepUtils;

public class MethodStepScanner {
	
	public List<MethodStepDeclaration> scan (Object instance) throws InvalidMethodStepException {
		List<MethodStepDeclaration> methoStepDeclarations = new ArrayList<MethodStepDeclaration>();
		
		// FIXME Be careful we scan only declared methods (not inherited ones).
		// There is no doubt that getMethods() is much more appropriate. But
		// it means that we cannot detect erroneous usage of @Step annotation.
		for (Method method : instance.getClass().getDeclaredMethods()) {
			Step stepAnnotation = method.getAnnotation(Step.class);
			if (stepAnnotation != null) {
				if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
					throw new InvalidMethodStepException("Found non public method with @Step annotation: " + method.toString());
				}
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
		
		return methoStepDeclarations;
	}
}
