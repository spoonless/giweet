package org.giweet.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.giweet.annotation.PatternDouble;
import org.junit.Test;

public class CalendarConverterTest {

	private Annotation[] dummyAnnotations = new Annotation[0];

	@Test
	public void canGetSupportedClasses() {
		CalendarConverter underTest = new CalendarConverter(Locale.US);
		
		assertArrayEquals(new Class<?>[]{Calendar.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		CalendarConverter underTest = new CalendarConverter(Locale.US, "dd/MM/yyyy");
		
		Calendar result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "01/12/1970");

		assertEquals("01/12/1970 00:00:00", dateFormat.format(result.getTime()));
	}

	@Test
	public void canConvertValueFromDateConverter() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateConverter dateConverter = new DateConverter(Locale.US, "dd/MM/yyyy");
		CalendarConverter underTest = new CalendarConverter(dateConverter);
		
		Calendar result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "01/12/1970");

		assertEquals("01/12/1970 00:00:00", dateFormat.format(result.getTime()));
	}

	@Test
	public void canConvertValueWithTime() throws Exception {
		String datePattern = "dd/MM/yyyy 'at' HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		CalendarConverter underTest = new CalendarConverter(Locale.US, datePattern);
		
		Calendar result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "01/12/1970 at 05:00");
		assertEquals("01/12/1970 at 05:00", dateFormat.format(result.getTime()));
	}

	@Test
	public void canConvertValueByChoosingRightFormat() throws Exception {
		String datePattern = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		CalendarConverter underTest = new CalendarConverter(Locale.US, "dd/MM/yyyy HH:mm:ss", "dd MM yyyy HH mm ss", "dd MMMM yyyy", "dd/MM/yyyy");
		
		Calendar result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "01/12/1970 08:30:10");
		assertEquals("01/12/1970 08:30:10", dateFormat.format(result.getTime()));

		result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "2 March 1971");
		assertEquals("02/03/1971 00:00:00", dateFormat.format(result.getTime()));

		result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "02/03/1971");
		assertEquals("02/03/1971 00:00:00", dateFormat.format(result.getTime()));

		result = (Calendar) underTest.convert(Calendar.class, dummyAnnotations, "03 04 1972 01 02 03");
		assertEquals("03/04/1972 01:02:03", dateFormat.format(result.getTime()));
	}

	@Test
	public void canConvertValueByParamPattern() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		CalendarConverter underTest = new CalendarConverter(Locale.US, "dd/MM/yyyy");
		Annotation[] annotations = {new PatternDouble("dd MMMM yyyy")};
		
		Calendar result = (Calendar) underTest.convert(Calendar.class, annotations, "1 january 1970");

		assertEquals("01/01/1970 00:00:00", dateFormat.format(result.getTime()));
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithWrongFormat() throws Exception {
		String datePattern = "dd/MM/yyyy";
		CalendarConverter underTest = new CalendarConverter(Locale.US, datePattern);
		
		underTest.convert(Calendar.class, dummyAnnotations, "12:12:12");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithUnsupportedClassTarget() throws Exception {
		CalendarConverter underTest = new CalendarConverter(Locale.US, "dd/MM/yyyy");
		
		underTest.convert(Integer.class, dummyAnnotations, "01/01/1970");
	}
}
