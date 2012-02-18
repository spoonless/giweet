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

import org.giweet.StringUtils;
import org.giweet.step.StepToken;

public class NumberConverter extends ArraySupportConverter {
	
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

	@Override
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException {
		String[] patterns = Pattern.getPatterns(annotations);
		Number result = null;
		try {
			if (patterns == null || patterns.length == 0) {
				result = numberFormat.parse(StringUtils.toString(values));
			}
			else {
				DecimalFormat decimalFormat = new DecimalFormat(patterns[0], decimalFormatSymbols);
				if (BigInteger.class.isAssignableFrom(baseTargetClass) || BigDecimal.class.isAssignableFrom(baseTargetClass)) {
					decimalFormat.setParseBigDecimal(true);
				}
				result = decimalFormat.parse(StringUtils.toString(values));
			}
		}
		catch (ParseException e) {
			throw new CannotConvertException(baseTargetClass, values, e);
		}
		return convert(baseTargetClass, result);
	}
	
	private Object convert(Class<?> targetClass, Number result) throws CannotConvertException {
		if (targetClass.isPrimitive()) {
			targetClass = getWrapperClass(targetClass);
		}
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
		throw new CannotConvertException(targetClass, String.valueOf(result));
	}

	@Override
	protected boolean canConvertSingle(Class<?> targetClass) {
		boolean canConvert = Number.class.isAssignableFrom(targetClass);
		if (! canConvert && targetClass.isPrimitive()) {
			Class<?> wrapperClass = getWrapperClass(targetClass);
			canConvert = wrapperClass != null;
		}
		return canConvert;
	}

	private Class<?> getWrapperClass(Class<?> targetClass) {
		Class<?> wrapperClass = null;
		if (int.class.equals(targetClass)) {
			wrapperClass = Integer.class;
		}
		else if (long.class.equals(targetClass)) {
			wrapperClass = Long.class;
		} 
		else if (double.class.equals(targetClass)) {
			wrapperClass = Double.class;
		} 
		else if (float.class.equals(targetClass)) {
			wrapperClass = Float.class;
		} 
		else if (short.class.equals(targetClass)) {
			wrapperClass = Short.class;
		} 
		else if (byte.class.equals(targetClass)) {
			wrapperClass = Byte.class;
		} 
		return wrapperClass;
	}

	@Override
	protected Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] values) throws CannotConvertException {
		// TODO Auto-generated method stub
		return null;
	}
}
