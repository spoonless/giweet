package org.giweet.converter;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberConverter implements Converter {
	
	private static final String DEFAULT_DECIMAL_PATTERN = "0,000.0";
	private static final char NON_BREAKABLE_SPACE = '\u00a0';
	private final DecimalFormat[] decimalFormats;
	private final DecimalFormatSymbols decimalFormatSymbols;

	public NumberConverter(Locale locale, DecimalFormat... decimalFormats) {
		this.decimalFormatSymbols = new DecimalFormatSymbols(locale); 
		if (decimalFormats.length == 0) {
			this.decimalFormats = new DecimalFormat[]{new DecimalFormat(DEFAULT_DECIMAL_PATTERN, this.decimalFormatSymbols)};
		}
		else {
			this.decimalFormats = decimalFormats;
		}
	}

	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{
			Number.class,
			Integer.class,
			Long.class,
			Double.class,
			Float.class,
			Short.class,
			Byte.class,
			int.class,
			long.class,
			double.class,
			float.class,
			short.class,
			byte.class,
			AtomicInteger.class,
			AtomicLong.class,
			BigInteger.class,
			BigDecimal.class
		};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		DecimalFormat[] decimalFormats = getDecimalFormats(annotations);
		Number result = null;
		if (decimalFormatSymbols.getGroupingSeparator() == NON_BREAKABLE_SPACE) {
			value = fixNonBreakableSpaceForGroupSeparator(value);
		}
		boolean bigDecimalExpected = BigInteger.class.isAssignableFrom(targetClass) || BigDecimal.class.isAssignableFrom(targetClass);
        for (DecimalFormat decimalFormat : decimalFormats) {
            decimalFormat.setParseBigDecimal(bigDecimalExpected);
            try {
                result = decimalFormat.parse(value);
            } catch (ParseException e) {
            }
        }
		if (result == null) {
			throw new CannotConvertException(targetClass, value);
		}
		return cast(targetClass, result);
	}
	
	private DecimalFormat[] getDecimalFormats(Annotation[] annotations) {
		String[] patterns = PatternUtils.getPatterns(annotations);
		DecimalFormat[] decimalFormats = null;
		if (patterns.length == 0) {
			decimalFormats = this.decimalFormats;
		}
		else {
			decimalFormats = new DecimalFormat[patterns.length];
			for (int i = 0; i < decimalFormats.length; i++) {
				decimalFormats[i] = new DecimalFormat(patterns[i], decimalFormatSymbols);
			}
		}
		return decimalFormats;
	}
	
	/**
	 * Workaround: for some locales (like french and finnish, group separator character 
	 * is a non breakable space.
	 * @see http://bugs.sun.com/view_bug.do?bug_id=4510618
	 */
	private String fixNonBreakableSpaceForGroupSeparator(String value) {
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isWhitespace(chars[i])) {
				chars[i] = NON_BREAKABLE_SPACE;
			}
		}
		return new String(chars);
	}

	private static Object cast(Class<?> targetClass, Number result) throws CannotConvertException {
		if (targetClass.isPrimitive()) {
			Class<?> wrapperClass = getWrapperClass(targetClass);
			if (wrapperClass == null) {
				throw new CannotConvertException(targetClass, String.valueOf(result));
			}
			targetClass = wrapperClass;
		}
		if (targetClass.isAssignableFrom(result.getClass())) {
			return result;
		}
		else if (targetClass.equals(Integer.class)) {
			return result.intValue();
		}
		else if (targetClass.equals(Long.class)) {
			return result.longValue();
		}
		else if (targetClass.equals(Double.class)) {
			return result.doubleValue();
		}
		else if (targetClass.equals(Float.class)) {
			return result.floatValue();
		}
		else if (targetClass.equals(Short.class)) {
			return result.shortValue();
		}
		else if (targetClass.equals(Byte.class)) {
			return result.byteValue();
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

	private static Class<?> getWrapperClass(Class<?> targetClass) {
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
}
