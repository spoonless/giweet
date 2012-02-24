package org.giweet.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;

import org.giweet.annotation.ParamDouble;
import org.junit.Test;

public class BooleanConverterTest {
	
	@Test
	public void canGetSupportedClasses() {
		BooleanConverter underTest = new BooleanConverter();
		
		assertArrayEquals(new Class<?>[]{boolean.class, Boolean.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct");
		
		Boolean result = (Boolean) underTest.convert(Boolean.class, new Annotation[]{}, "correct");
		assertTrue(result);

		result = (Boolean) underTest.convert(boolean.class, new Annotation[]{}, "correct");
		assertTrue(result);
		
		result = (Boolean) underTest.convert(Boolean.class, new Annotation[]{}, "incorrect");
		assertFalse(result);
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenExpectedClassIsNotBoolean() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct");
		
		underTest.convert(Integer.class, new Annotation[]{}, "correct");
	}

	@Test
	public void canConvertValueFromPatternInAnnotation() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct but not used");
		Annotation[] annotations = {new ParamDouble("correct")};
		
		Boolean result = (Boolean) underTest.convert(Boolean.class, annotations, "correct");
		assertTrue(result);

		result = (Boolean) underTest.convert(Boolean.class, annotations, "correct but not used");
		assertFalse(result);
	}
}
