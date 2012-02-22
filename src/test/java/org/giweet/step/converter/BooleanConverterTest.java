package org.giweet.step.converter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import org.giweet.annotation.ParamDouble;
import org.giweet.step.StepToken;
import org.giweet.step.StepTokenizer;
import org.junit.Test;

public class BooleanConverterTest {
	
	private StepTokenizer stepTokenizer = new StepTokenizer(false, true);
	
	@Test
	public void canGetSupportedClasses() {
		BooleanConverter underTest = new BooleanConverter();
		
		assertArrayEquals(new Class<?>[]{boolean.class, Boolean.class}, underTest.getSupportedClasses());
	}

	@Test
	public void canConvertValue() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct");
		
		Boolean result = (Boolean) underTest.convert(Boolean.class, new Annotation[]{}, stepTokenizer.tokenize("correct"));
		assertTrue(result);

		result = (Boolean) underTest.convert(boolean.class, new Annotation[]{}, stepTokenizer.tokenize("correct"));
		assertTrue(result);
		
		result = (Boolean) underTest.convert(Boolean.class, new Annotation[]{}, stepTokenizer.tokenize("incorrect"));
		assertFalse(result);
	}

	@Test
	public void canConvertValueForArrays() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct");
		
		Boolean[] result = (Boolean[]) underTest.convert(Boolean[].class, new Annotation[]{}, new StepToken[0]);
		assertEquals(0, result.length);

		result = (Boolean[]) underTest.convert(Boolean[].class, new Annotation[]{}, stepTokenizer.tokenize("correct, correct incorrect"));
		assertArrayEquals(new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.FALSE}, result);

		boolean[] primitiveArrayResult = (boolean[]) underTest.convert(boolean[].class, new Annotation[]{}, new StepToken[0]);
		assertEquals(0, primitiveArrayResult.length);

		primitiveArrayResult = (boolean[]) underTest.convert(boolean[].class, new Annotation[]{}, stepTokenizer.tokenize("correct, incorrect"));
		assertTrue(primitiveArrayResult[0]);
		assertFalse(primitiveArrayResult[1]);
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenExpectedClassIsNotBoolean() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct");
		
		underTest.convert(Integer.class, new Annotation[]{}, stepTokenizer.tokenize("correct"));
	}

	@Test
	public void canConvertValueFromPatternInAnnotation() throws Exception {
		BooleanConverter underTest = new BooleanConverter("correct but not used");
		Annotation[] annotations = {new ParamDouble("correct")};
		
		Boolean result = (Boolean) underTest.convert(Boolean.class, annotations, stepTokenizer.tokenize("correct"));
		assertTrue(result);

		result = (Boolean) underTest.convert(Boolean.class, annotations, stepTokenizer.tokenize("correct but not used"));
		assertFalse(result);
	}
}
