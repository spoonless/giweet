package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import org.giweet.converter.CannotConvertException;
import org.giweet.step.StepTokenValue;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;

public class MethodStepInvoker {
	
	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\.");
	
	private final ParamStepConverter paramStepConverter;

	public MethodStepInvoker(ParamStepConverter paramStepConverter) {
		this.paramStepConverter = paramStepConverter;
	}
	
	public Object invoke(MethodStepDeclaration methodStepDeclaration, StepTokenValue[] stepTokenValues) throws CannotConvertException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, QuoteTailNotFoundException {
		Type[] genericParameterTypes = methodStepDeclaration.getGenericParameterTypes();
		Annotation[][] parameterAnnotations = methodStepDeclaration.getParameterAnnotations();
		Object[] params = new Object[genericParameterTypes.length];
		
		for(int i = 0; i < stepTokenValues.length; i++) {
			StepTokenValue stepTokenValue = stepTokenValues[i];
			try {
				int paramPosition = Integer.parseInt(stepTokenValue.getDynamicToken().toString());
				Object paramValue = paramStepConverter.convert(genericParameterTypes[paramPosition], parameterAnnotations[paramPosition], stepTokenValue.getTokens());
				// FIXME checks i < params.length && i > 0
				params[paramPosition] = paramValue;
			} catch (NumberFormatException e) {
				String[] path = SPLIT_PATTERN.split(stepTokenValue.getDynamicToken().toString());
				InvocableMethod setterMethod = null;
				try {
					int paramPosition = Integer.parseInt(path[0]);
					// FIXME checks i < params.length && i > 0
					if (params[paramPosition] == null) {
						Type paramType = genericParameterTypes[paramPosition];
						Class<?> paramClass = getClassFromType(paramType);
						params[i] = paramClass.newInstance();
					}
					String[] truncatedPath = new String[path.length - 1];
					for(int j = 0 ; j < truncatedPath.length ; j++) {
						truncatedPath[j] = path[j+1];
					}
					setterMethod = new InvocableSetterMethod(params[paramPosition], truncatedPath);
				} catch (NumberFormatException e2) {
					setterMethod = methodStepDeclaration.getInvocableSetterMethod(path);
				}
				Object paramValue = paramStepConverter.convert(setterMethod.getGenericParameterTypes()[0], setterMethod.getParameterAnnotations()[0], stepTokenValue.getTokens());
				setterMethod.invoke(paramValue);
			}
		}
		return methodStepDeclaration.invoke(params);
	}

	private Class<?> getClassFromType(Type paramType) {
		Class<?> paramClass = null;
		if (paramType instanceof ParameterizedType) {
			paramType = ((ParameterizedType) paramType).getRawType();
		}
		if (paramType instanceof Class) {
			paramClass = (Class<?>) paramType;
		}
		if (paramClass == null) {
			// FIXME throw exception
		}
		return paramClass;
	}
	

}
