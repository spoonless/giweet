package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.giweet.converter.CannotConvertException;
import org.giweet.step.StepTokenValue;

public class MethodStepInvoker {
	
	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");
	
	private final ParamStepConverter paramStepConverter;

	public MethodStepInvoker(ParamStepConverter paramStepConverter) {
		this.paramStepConverter = paramStepConverter;
	}
	
	public Object invoke(MethodStepDescriptor methodStepDescriptor, StepTokenValue[] stepTokenValues) throws CannotConvertException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Type[] genericParameterTypes = methodStepDescriptor.getGenericParameterTypes();
		Annotation[][] parameterAnnotations = methodStepDescriptor.getParameterAnnotations();
		Object[] params = new Object[genericParameterTypes.length];
		
		for(int i = 0; i < stepTokenValues.length; i++) {
			StepTokenValue stepTokenValue = stepTokenValues[i];
			try {
				int paramPosition = Integer.parseInt(stepTokenValue.getDynamicToken().toString());
				Object paramValue = paramStepConverter.convert(genericParameterTypes[paramPosition], parameterAnnotations[paramPosition], stepTokenValue.getTokens());
				params[i] = paramValue;
			} catch (NumberFormatException e) {
				String[] path = SPLIT_PATTERN.split(stepTokenValue.getDynamicToken().toString());
				InvocableMethod setterMethod = methodStepDescriptor.getInvocableSetterMethod(path);
				Object paramValue = paramStepConverter.convert(setterMethod.getGenericParameterTypes()[0], setterMethod.getParameterAnnotations()[0], stepTokenValue.getTokens());
				setterMethod.invoke(paramValue);
			}
		}
		return methodStepDescriptor.invoke(params);
	}
	

}
