package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.giweet.converter.CannotConvertException;
import org.giweet.step.ParameterValue;

public class MethodStepInvoker {
	
	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");
	
	private final ParamStepConverter paramStepConverter;

	public MethodStepInvoker(ParamStepConverter paramStepConverter) {
		this.paramStepConverter = paramStepConverter;
	}
	
	public Object invoke(MethodStepDescriptor methodStepDescriptor, ParameterValue[] parameterValues) throws CannotConvertException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Type[] genericParameterTypes = methodStepDescriptor.getGenericParameterTypes();
		Annotation[][] parameterAnnotations = methodStepDescriptor.getParameterAnnotations();
		Object[] params = new Object[genericParameterTypes.length];
		
		for(int i = 0; i < parameterValues.length; i++) {
			ParameterValue parameterValue = parameterValues[i];
			try {
				int paramPosition = Integer.parseInt(parameterValue.getParameterToken().toString());
				Object paramValue = paramStepConverter.convert(genericParameterTypes[paramPosition], parameterAnnotations[paramPosition], parameterValue.getValueTokens());
				params[i] = paramValue;
			} catch (NumberFormatException e) {
				String[] path = SPLIT_PATTERN.split(parameterValue.getParameterToken().toString());
				InvocableMethod setterMethod = methodStepDescriptor.getInvocableSetterMethod(path);
				Object paramValue = paramStepConverter.convert(setterMethod.getGenericParameterTypes()[0], setterMethod.getParameterAnnotations()[0], parameterValue.getValueTokens());
				setterMethod.invoke(paramValue);
			}
		}
		return methodStepDescriptor.invoke(params);
	}
	

}
