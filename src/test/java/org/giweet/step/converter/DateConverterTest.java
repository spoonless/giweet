package org.giweet.step.converter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.giweet.step.StepTokenizer;
import org.junit.Test;

public class DateConverterTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(false, true);
	private Annotation[] dummyAnnotations = new Annotation[0];

	@Test
	public void canGetSupportedClasses() {
		DateConverter underTest = new DateConverter(Locale.US);
		
		assertArrayEquals(new Class<?>[]{Date.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		String datePattern = "dd/MM/yyyy";
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		canConvertWithDatePattern(underTest, datePattern, "01/12/1970");
	}

	@Test
	public void canConvertValues() throws Exception {
		String datePattern = "dd/MM/yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		Date[] result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970"));
		assertEquals("01/12/1970", dateFormat.format(result[0]));

		result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970 02/12/1970"));
		assertEquals("01/12/1970", dateFormat.format(result[0]));
		assertEquals("02/12/1970", dateFormat.format(result[1]));
	}

	@Test
	public void canConvertValuesWithTime() throws Exception {
		String datePattern = "dd/MM/yyyy 'at' HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		Date[] result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970 at 05:00 "));
		assertEquals("01/12/1970 at 05:00", dateFormat.format(result[0]));

		result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970 at 00:00 02/12/1970 at 08:30"));
		assertEquals(2, result.length);
		assertEquals("01/12/1970 at 00:00", dateFormat.format(result[0]));
		assertEquals("02/12/1970 at 08:30", dateFormat.format(result[1]));

		result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970 at 00:00, 02/12/1970 at 08:30 03/12/1970 at 10:30"));
		assertEquals(3, result.length);
		assertEquals("01/12/1970 at 00:00", dateFormat.format(result[0]));
		assertEquals("02/12/1970 at 08:30", dateFormat.format(result[1]));
		assertEquals("03/12/1970 at 10:30", dateFormat.format(result[2]));
	}

	@Test
	public void canConvertValuesWithMixedFormat() throws Exception {
		String datePattern = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		DateConverter underTest = new DateConverter(Locale.US, "dd/MM/yyyy HH:mm:ss", "dd MM yyyy HH mm ss", "dd MMMM yyyy");
		
		Date[] result = (Date[]) underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("01/12/1970 08:30:10, 2 March 1971, 03 04 1972 01 02 03"));
		assertEquals(3, result.length);
		assertEquals("01/12/1970 08:30:10", dateFormat.format(result[0]));
		assertEquals("02/03/1971 00:00:00", dateFormat.format(result[1]));
		assertEquals("03/04/1972 01:02:03", dateFormat.format(result[2]));
	}

	@Test
	public void canConvertValueByChoosingRightFormat() throws Exception {
		String datePattern = "dd/MM/yyyy";
		String datePatternWithTime = "dd/MM/yyyy HH:mm:ss";
		String datePatternWithTimeFirst = "HH:mm:ss dd/MM/yyyy";
		DateConverter underTest = new DateConverter(Locale.US, datePatternWithTime, datePatternWithTimeFirst, datePattern);
		
		canConvertWithDatePattern(underTest, datePattern, "01/12/1970");
		canConvertWithDatePattern(underTest, datePatternWithTime, "01/12/1970 13:55:59");
		canConvertWithDatePattern(underTest, datePatternWithTimeFirst, "01:13:13 01/12/1970");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithWrongFormat() throws Exception {
		String datePattern = "dd/MM/yyyy";
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		underTest.convert(Date.class, dummyAnnotations, stepTokenizer.tokenize("12:12:12"));
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertArrayValuesWithWrongFormat() throws Exception {
		String datePattern = "dd/MM/yyyy";
		DateConverter underTest = new DateConverter(Locale.US, datePattern);
		
		underTest.convert(Date[].class, dummyAnnotations, stepTokenizer.tokenize("12:12:12"));
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueWithUnsupportedClassTarget() throws Exception {
		DateConverter underTest = new DateConverter(Locale.US, "dd/MM/yyyy");
		
		underTest.convert(Integer.class, dummyAnnotations, stepTokenizer.tokenize("01/01/1970"));
	}

	private void canConvertWithDatePattern(DateConverter underTest, String datePattern, String dateValue) throws Exception {
		Date result = (Date) underTest.convert(Date.class, dummyAnnotations, stepTokenizer.tokenize(dateValue));

		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		assertEquals(dateValue, dateFormat.format(result));
	}
}
