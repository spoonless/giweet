package org.giweet.converter;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter implements Converter {

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

	// FIXME add support for pattern from annotations
	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! Date.class.equals(targetClass)) {
			throw new CannotConvertException(targetClass, value);
		}
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
			throw new CannotConvertException(targetClass, value, exceptionCaught);
		}
		return result;
	}
}
