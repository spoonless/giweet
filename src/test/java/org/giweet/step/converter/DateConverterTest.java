package org.giweet.step.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

public class DateConverterTest {

	private Annotation[] dummyAnnotations = new Annotation[0];

	@Test
	public void canGetSupportedClasses() {
		DateConverter underTest = new DateConverter(Locale.US);
		
		assertArrayEquals(new Class<?>[]{Date.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		DateConverter underTest = new DateConverter(Locale.US, "dd/MM/yyyy");
		
		Date result = (Date) underTest.convert(Date.class, dummyAnnotations, "01/12/1970");

		assertEquals("01/12/1970 00:00:00", dateFormat.format(result));
	}

	@Test
	public void canConvertValueWithTime() throws Exception {
		String datePattern = "dd/MM/yyyy 'at' HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		Date result = (Date) underTest.convert(Date.class, dummyAnnotations, "01/12/1970 at 05:00");
		assertEquals("01/12/1970 at 05:00", dateFormat.format(result));
	}

	@Test
	public void canConvertValueByChoosingRightFormat() throws Exception {
		String datePattern = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		DateConverter underTest = new DateConverter(Locale.US, "dd/MM/yyyy HH:mm:ss", "dd MM yyyy HH mm ss", "dd MMMM yyyy", "dd/MM/yyyy");
		
		Date result = (Date) underTest.convert(Date.class, dummyAnnotations, "01/12/1970 08:30:10, 2 March 1971, 03 04 1972 01 02 03");
		assertEquals("01/12/1970 08:30:10", dateFormat.format(result));

		result = (Date) underTest.convert(Date.class, dummyAnnotations, "2 March 1971");
		assertEquals("02/03/1971 00:00:00", dateFormat.format(result));

		result = (Date) underTest.convert(Date.class, dummyAnnotations, "02/03/1971");
		assertEquals("02/03/1971 00:00:00", dateFormat.format(result));

		result = (Date) underTest.convert(Date.class, dummyAnnotations, "03 04 1972 01 02 03");
		assertEquals("03/04/1972 01:02:03", dateFormat.format(result));
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithWrongFormat() throws Exception {
		String datePattern = "dd/MM/yyyy";
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		underTest.convert(Date.class, dummyAnnotations, "12:12:12");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithUnsupportedClassTarget() throws Exception {
		DateConverter underTest = new DateConverter(Locale.US, "dd/MM/yyyy");
		
		underTest.convert(Integer.class, dummyAnnotations, "01/01/1970");
	}
}
