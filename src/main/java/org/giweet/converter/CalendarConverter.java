package org.giweet.converter;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class CalendarConverter implements Converter {
	
	private final DateConverter dateConverter;

	public CalendarConverter(Locale locale, String... patterns) {
		dateConverter = new DateConverter(locale, patterns);
	}

	public CalendarConverter(DateConverter dateConverter) {
		this.dateConverter = dateConverter;
	}

	public Class<?>[] getSupportedClasses() {
		return new Class<?>[]{Calendar.class};
	}

	public Object convert(Class<?> targetClass, Annotation[] annotations, String value) throws CannotConvertException {
		if (! Calendar.class.equals(targetClass)) {
			throw new CannotConvertException(targetClass, value);
		}
		Date date = (Date) dateConverter.convert(Date.class, annotations, value);
		Calendar calendar = Calendar.getInstance(dateConverter.getLocale());
		calendar.setTime(date);
		return calendar;
	}

}
