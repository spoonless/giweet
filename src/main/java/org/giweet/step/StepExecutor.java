package org.giweet.step;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StepExecutor extends StepDescriptor {

	private final Method method;
	private final Object instance;

	public StepExecutor(Method method, Object instance, String stepValue) {
		super(stepValue);
		this.method = method;
		this.instance = instance;
	}
	
	public void execute(ParameterValue... parameterValues) throws Throwable {
		Object[] parameters = new Object[parameterValues.length];
		for (int i = 0 ; i < parameters.length ; i++) {
			parameters[i] = parameterValues[i].getValue();
		}
		try {
			method.invoke(instance, parameters);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}
