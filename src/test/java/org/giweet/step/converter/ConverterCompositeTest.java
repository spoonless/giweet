package org.giweet.step.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;
import org.junit.Test;

public class ConverterCompositeTest {

	@Test
	public void canConvert() {
		ConverterComposite underTest = new ConverterComposite(new SimpleStringConverter(), new BooleanConverter(), new NumberConverter(Locale.US));
		
		assertTrue(underTest.canConvert(String.class));
		assertTrue(underTest.canConvert(Boolean.class));
		assertTrue(underTest.canConvert(Long.class));
		assertTrue(underTest.canConvert(int.class));
		assertTrue(underTest.canConvert(long.class));
		assertTrue(underTest.canConvert(double.class));
		assertTrue(underTest.canConvert(float.class));
		assertTrue(underTest.canConvert(short.class));
		assertTrue(underTest.canConvert(byte.class));
		assertTrue(underTest.canConvert(Long[].class));
		assertTrue(underTest.canConvert(long[].class));
		assertFalse(underTest.canConvert(Exception.class));
		
		// FIXME improper result because of type erasure. The converter assumes List<String>!
		List<Double> doubleList = new ArrayList<Double>();
		assertTrue(underTest.canConvert(doubleList.getClass()));
	}

	@Test
	public void canConvertValue() throws Exception {
		Annotation[] dummyAnnotations = {};
		ConverterComposite underTest = new ConverterComposite(new SimpleStringConverter(), new BooleanConverter(), new NumberConverter(Locale.US));
		
		Object result = underTest.convert(Integer.class, dummyAnnotations , toArray(new StaticStepToken("1")));
		assertEquals(Integer.valueOf(1), result);
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertValueIfNoConverterAvailable() throws Exception {
		Annotation[] dummyAnnotations = {};
		ConverterComposite underTest = new ConverterComposite();
		
		underTest.convert(Integer.class, dummyAnnotations , toArray(new StaticStepToken("1")));
	}
	
	private static StepToken[] toArray(StepToken... stepTokens) {
		return stepTokens;
	}
}
