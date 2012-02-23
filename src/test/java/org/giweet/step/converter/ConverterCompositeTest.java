package org.giweet.step.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

public class ConverterCompositeTest {

	@Test
	public void canGetSupportedClasses() {
		ConverterComposite underTest = new ConverterComposite(new SimpleStringConverter(), new DateConverter(Locale.US));
		
		Class<?>[] supportedClasses = underTest.getSupportedClasses();

		assertEquals(2, supportedClasses.length);
		assertTrue(Arrays.asList(supportedClasses).contains(Date.class));
		assertTrue(Arrays.asList(supportedClasses).contains(String.class));
	}

	@Test
	public void canConvertValue() throws Exception {
		Annotation[] dummyAnnotations = {};
		ConverterComposite underTest = new ConverterComposite(new SimpleStringConverter(), new BooleanConverter(), new NumberConverter(Locale.US));
		
		Object result = underTest.convert(Integer.class, dummyAnnotations , "1");
		assertEquals(Integer.valueOf(1), result);
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueIfNoConverterAvailable() throws Exception {
		Annotation[] dummyAnnotations = {};
		ConverterComposite underTest = new ConverterComposite();
		
		underTest.convert(Integer.class, dummyAnnotations , "1");
	}
}
