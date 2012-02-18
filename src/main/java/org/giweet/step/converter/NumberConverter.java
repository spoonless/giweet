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
		targetClass = convertFromPrimitive(targetClass);
		return Number.class.isAssignableFrom(targetClass);
	}

	private Class<?> convertFromPrimitive(Class<?> targetClass) {
		if (targetClass.isPrimitive()) {
			if (int.class.equals(targetClass)) {
				targetClass = Integer.class;
			} 
			else if (long.class.equals(targetClass)) {
				targetClass = Long.class;
			} 
			else if (double.class.equals(targetClass)) {
				targetClass = Double.class;
			} 
			else if (float.class.equals(targetClass)) {
				targetClass = Float.class;
			} 
			else if (byte.class.equals(targetClass)) {
				targetClass = Byte.class;
			}
			else if (short.class.equals(targetClass)) {
				targetClass = Short.class;
			} 
		}
		return targetClass;
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		targetClass = convertFromPrimitive(targetClass);
		String[] patterns = Pattern.getPatterns(annotations);
		Number result = null;
		try {
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
		}
		catch (ParseException e) {
			throw new CannotConvertException(targetClass, value, e);
		}
		return convert(targetClass, result);
	}
	
	private Object convert(Class<?> targetClass, Number result) throws CannotConvertException {
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
		throw new CannotConvertException(targetClass, result.toString());
	}
}
