package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.giweet.StringUtils;
import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;

public class NumberConverter extends ArraySupportConverter {
	
	private static final char NON_BREAKABLE_SPACE = '\u00a0';
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
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		String value = StringUtils.toString(stepTokens);
		NumberFormat numberFormat = getNumberFormat(baseTargetClass, annotations);
		Number result = null;
		try {
			result = numberFormat.parse(value);
		}
		catch (ParseException e) {
			throw new CannotConvertException(baseTargetClass, value, e);
		}
		return cast(baseTargetClass, result);
	}
	
	private NumberFormat getNumberFormat(Class<?> baseTargetClass, Annotation[] annotations) {
		String[] patterns = Pattern.getPatterns(annotations);
		NumberFormat numberFormat = null;
		if (patterns == null || patterns.length == 0) {
			numberFormat = this.numberFormat;
		}
		else {
			DecimalFormat decimalFormat = new DecimalFormat(patterns[0], decimalFormatSymbols);
			if (BigInteger.class.isAssignableFrom(baseTargetClass) || BigDecimal.class.isAssignableFrom(baseTargetClass)) {
				decimalFormat.setParseBigDecimal(true);
			}
			numberFormat = decimalFormat;
		}
		return numberFormat;
	}

	private static Object cast(Class<?> targetClass, Number result) throws CannotConvertException {
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

	@Override
	protected Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		List<String> meaningfulValues = null;
		NumberFormat numberFormat = getNumberFormat(baseTargetClass, annotations);
		if (decimalFormatSymbols.getGroupingSeparator() != NON_BREAKABLE_SPACE) {
			meaningfulValues = getMeaningfulValues(stepTokens);
		}
		else {
			meaningfulValues = getMeaningfulValueWithNonBreakableSpaceFix(stepTokens);
		}
		
		Object resultArray = Array.newInstance(baseTargetClass, meaningfulValues.size());
		for (int i = 0; i < meaningfulValues.size(); i++) {
			String value = meaningfulValues.get(i);
			try {
				Number result = numberFormat.parse(value);
				Array.set(resultArray, i, cast(baseTargetClass, result));
			} catch (ParseException e) {
				throw new CannotConvertException(baseTargetClass, value, e);
			}
		}
		return resultArray;
	}
	
	/**
	 * Workaround: for some locales (like french and finnish, group separator character 
	 * is a non breakable space. If such character is present then we assume that simple space
	 * must also be present.
	 * @see http://bugs.sun.com/view_bug.do?bug_id=4510618
	 */
	private List<String> getMeaningfulValueWithNonBreakableSpaceFix(StepToken[] stepTokens) {
		List<String> reformattedList = new ArrayList<String>(stepTokens.length);
		stepTokens = stepTokens.clone();
		StepToken groupSeparator = new StaticStepToken(String.valueOf(NON_BREAKABLE_SPACE), false);
		
		replaceByGroupSeparator(stepTokens, groupSeparator);
		
		String currentListItem = null;
		for (StepToken stepToken : stepTokens) {
			if (stepToken.isMeaningful()) {
				if (currentListItem == null) {
					currentListItem = String.valueOf(stepToken);
				}
				else {
					currentListItem += String.valueOf(stepToken);
				}
			}
			else if (currentListItem != null){
				if (stepToken == groupSeparator) {
					currentListItem += String.valueOf(stepToken);
				}
				else {
					reformattedList.add(currentListItem);
					currentListItem = null;
				}
			}
		}
		if (currentListItem != null) {
			reformattedList.add(currentListItem);
		}
		return reformattedList;
	}

	private void replaceByGroupSeparator(StepToken[] stepTokens, StepToken groupSeparator) {
		boolean maybeGroupSeparator = false;
		for (int i = stepTokens.length - 1; i >= 0; i--) {
			StepToken stepToken = stepTokens[i];
			String value = String.valueOf(stepToken);
			boolean signIsFirst = false;
			if (stepToken.isMeaningful()) {
				int nbDigit = 0;
				int j = 0;
				for (; j < value.length(); j++) {
					char currentCharacter = value.charAt(j);
					if (Character.isDigit(currentCharacter)) {
						nbDigit++;
					}
					else if (j == 0 && currentCharacter == decimalFormatSymbols.getMinusSign()){
						signIsFirst = true;
					}
					else {
						break;
					}
				}
				if (maybeGroupSeparator && j == value.length() && nbDigit <= 3) {
					stepTokens[i+1] = groupSeparator;
				}
				maybeGroupSeparator = nbDigit == 3 && !signIsFirst;
			}
			else if (maybeGroupSeparator) {
				if (value.length() != 1) {
					maybeGroupSeparator = false;
				}
				else if (! stepTokens[i+1].isMeaningful()) {
					maybeGroupSeparator = false;
				}
				else {
					char separatorChar = value.charAt(0);
					if (separatorChar != ' ' && separatorChar != NON_BREAKABLE_SPACE) {
						maybeGroupSeparator = false;
					}
				}
			}
		}
	}
}
