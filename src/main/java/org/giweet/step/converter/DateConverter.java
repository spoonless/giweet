package org.giweet.step.converter;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;
import org.giweet.step.StepTokenizer;

public class DateConverter extends ArraySupportConverter {

	private final DateFormat[] dateFormats;

	public DateConverter(Locale locale, String... datePatterns) {
		dateFormats = new DateFormat[datePatterns.length];
		int i = 0;
		for (String datePattern : datePatterns) {
			dateFormats[i++] = new SimpleDateFormat(datePattern, locale);
		}
	}
	
	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{Date.class};
	}

	@Override
	protected Object convertSingle(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		return convert(baseTargetClass, StringUtils.toString(stepTokens));
	}

	private Object convert(Class<?> baseTargetClass, String value) throws CannotConvertException {
		Date result = null;
		Exception exceptionCaught = null;
		for (DateFormat dateFormat : dateFormats) {
			try {
				result = dateFormat.parse(value);
				exceptionCaught = null;
				break;
			} catch (ParseException e) {
				exceptionCaught = e;
			}
		}
		if (exceptionCaught != null) {
			throw new CannotConvertException(baseTargetClass, value, exceptionCaught);
		}
		return result;
	}

	@Override
	// FIXME add support for pattern from annotations
	protected Object convertArray(Class<?> baseTargetClass, Annotation[] annotations, StepToken[] stepTokens) throws CannotConvertException {
		List<Date> result = new ArrayList<Date>(stepTokens.length);
		Date dateToComputeSteps = new Date(0);
		StepTokenizer stepTokenizer = new StepTokenizer(false, true);
		int[] nbStepTokensPerDatePattern = new int[dateFormats.length];
		int currentStepTokenIndex = 0;
		for (int i = 0; i < dateFormats.length && currentStepTokenIndex < stepTokens.length; i++) {
			DateFormat dateFormat = dateFormats[i];
			if (nbStepTokensPerDatePattern[i] == 0) {
				StepToken[] computedStepTokens = stepTokenizer.tokenize(dateFormat.format(dateToComputeSteps));
				nbStepTokensPerDatePattern[i] = computedStepTokens.length;
			}
			int nbExpectedStepTokens = nbStepTokensPerDatePattern[i];
			int endStepTokenIndex = currentStepTokenIndex + nbExpectedStepTokens;
			if (isValidEndIndex(stepTokens, endStepTokenIndex)) {
				String value = extractTokens(stepTokens, currentStepTokenIndex, endStepTokenIndex);
				try {
					result.add(dateFormat.parse(value));
					i = -1;
					currentStepTokenIndex = endStepTokenIndex + 1;
				} catch (ParseException e) {
				}
			}
		}
		if (currentStepTokenIndex < stepTokens.length) {
			throw new CannotConvertException(baseTargetClass, extractTokens(stepTokens, currentStepTokenIndex, stepTokens.length));
		}
		return result.toArray(new Date[result.size()]);
	}

	private boolean isValidEndIndex(StepToken[] stepTokens, int endStepTokenIndex) {
		return endStepTokenIndex == stepTokens.length || (endStepTokenIndex < stepTokens.length && !stepTokens[endStepTokenIndex].isMeaningful());
	}

	private String extractTokens(StepToken[] stepTokens, int currentStepTokenIndex, int endStepTokenIndex) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int j = currentStepTokenIndex; j < endStepTokenIndex; j++){
			stringBuilder.append(stepTokens[j]);
		}
		return stringBuilder.toString();
	}
}
