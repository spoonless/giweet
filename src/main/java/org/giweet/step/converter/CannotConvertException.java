package org.giweet.step.converter;

import org.giweet.StringUtils;

public class CannotConvertException extends Exception {

	private static final long serialVersionUID = 6924717640583135899L;
	
	public CannotConvertException() {
	}
	
	public CannotConvertException(Class<?> targetClass, String value) {
		super (createMessage(targetClass, value));
	}

	public CannotConvertException(Class<?> targetClass, Object[] values) {
		super (createMessage(targetClass, StringUtils.toString(values)));
	}

	public CannotConvertException(Class<?> targetClass, Object[] values, Throwable e) {
		this(targetClass, StringUtils.toString(values), e);
	}

	public CannotConvertException(Class<?> targetClass, String value, Throwable e) {
		super (createMessage(targetClass, value), e);
	}

	private static String createMessage(Class<?> targetClass, String value) {
		return "Cannot convert \"" + value + "\" to type " + targetClass.getName();
	}

}
