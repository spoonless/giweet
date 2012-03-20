package org.giweet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.giweet.annotation.Step;
import org.giweet.step.StepUtils;

public class MethodStepScanner {
	
	public List<MethodStepDescriptor> scan (Object instance) throws InvalidMethodStepException {
		List<MethodStepDescriptor> methoStepDescriptors = new ArrayList<MethodStepDescriptor>();
		
		for (Method method : instance.getClass().getDeclaredMethods()) {
			Step stepAnnotation = method.getAnnotation(Step.class);
			if (stepAnnotation != null) {
				if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
					throw new InvalidMethodStepException("Found non public method with @Step annotation: " + method.toString());
				}
				if (stepAnnotation.value().length != 0) {
					for (String value : stepAnnotation.value()) {
						methoStepDescriptors.add(new MethodStepDescriptor(method, instance, value));
					}
				}
				else {
					methoStepDescriptors.add(new MethodStepDescriptor(method, instance, StepUtils.getStepFromJavaIdentifier(method.getName())));
				}
			}
		}
		
		return methoStepDescriptors;
	}
}
