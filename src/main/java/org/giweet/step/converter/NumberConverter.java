package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.giweet.annotation.Param;

public class NumberConverter implements Converter {
	
	private final NumberFormat numberFormat;
	private final DecimalFormatSymbols decimalFormatSymbols;

	public NumberConverter(Locale locale) {
		this.numberFormat = NumberFormat.getInstance(locale);
		this.decimalFormatSymbols = new DecimalFormatSymbols(locale);
	}

	public NumberConverter(DecimalFormat decimalFormat) {
		this.numberFormat = decimalFormat;
		this.decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
	}

	public boolean canConvert(Class<?> targetClass) {
		return Number.class.isAssignableFrom(targetClass);
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws ParseException {
		String[] patterns = getPatterns(annotations);
		Number result = null;
		if (patterns == null || patterns.length == 0) {
			result = numberFormat.parse(value);
		}
		else {
			DecimalFormat decimalFormat = new DecimalFormat(patterns[0], decimalFormatSymbols);
			if (BigInteger.class.isAssignableFrom(targetClass) || BigDecimal.class.isAssignableFrom(targetClass)) {
				decimalFormat.setParseBigDecimal(true);
			}
			result = decimalFormat.parse(value);
		}
		return convert(targetClass, result);
	}
	
	private Object convert(Class<?> targetClass, Number result) {
		if (targetClass.isAssignableFrom(result.getClass())) {
			return result;
		}
		else if (targetClass.equals(Integer.class)) {
			return Integer.valueOf(result.intValue());
		}
		else if (targetClass.equals(Long.class)) {
			return Long.valueOf(result.longValue());
		}
		else if (targetClass.equals(Double.class)) {
			return Double.valueOf(result.doubleValue());
		}
		else if (targetClass.equals(Float.class)) {
			return Float.valueOf(result.floatValue());
		}
		else if (targetClass.equals(Short.class)) {
			return Short.valueOf(result.shortValue());
		}
		else if (targetClass.equals(Byte.class)) {
			return Byte.valueOf(result.byteValue());
		}
		else if (targetClass.equals(AtomicInteger.class)) {
			return new AtomicInteger (result.intValue());
		}
		else if (targetClass.equals(AtomicLong.class)) {
			return new AtomicLong(result.longValue());
		}
		else if (targetClass.equals(BigInteger.class)) {
			if (result instanceof BigDecimal) {
				return ((BigDecimal)result).toBigInteger();
			}
			return new BigInteger(result.toString());
		}
		else if (targetClass.equals(BigDecimal.class)) {
			return new BigDecimal(result.doubleValue());
		}
		// FIXME should we really return null
		return null;
	}

	private String[] getPatterns(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Param) {
				Param paramAnnotation = (Param) annotation;
				return paramAnnotation.pattern();
			}
		}
		return null;
	}

}
