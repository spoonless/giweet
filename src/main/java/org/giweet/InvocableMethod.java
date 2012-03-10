package org.giweet;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface InvocableMethod {

	public abstract Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	public abstract Type[] getGenericParameterTypes();

	public abstract Annotation[][] getParameterAnnotations();

}