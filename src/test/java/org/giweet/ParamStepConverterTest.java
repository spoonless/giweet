package org.giweet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.giweet.annotation.SeparatedByDouble;
import org.giweet.converter.CannotConvertException;
import org.giweet.converter.Converter;
import org.giweet.converter.ConverterComposite;
import org.giweet.converter.NumberConverter;
import org.giweet.converter.SimpleStringConverter;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Before;
import org.junit.Test;

public class ParamStepConverterTest {
	
	private StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);
	private Converter converter;
	private final Annotation[] dummyAnnotations = new Annotation[0];
	
	public static class FakeMethodClass {
		public void methodWithParameterizedList(List<Integer> list){}

		public void methodWithParameterizedCollection(Collection<Integer> collection){}

		public <T> void methodWithGenericCollection(Collection<T> collection){}

		public void methodWithParameterizedArrayList(ArrayList<Integer> collection){}

		@SuppressWarnings("rawtypes")
		public void methodWithNonParameterizedList(List list){}
	}
	
	@Before
	public void createConverter() {
		converter = new ConverterComposite(new NumberConverter(Locale.US), new SimpleStringConverter());
	}
	
	@Test
	public void testCanConvertPrimitive() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter);
		
		Object result = underTest.convert(int.class, dummyAnnotations, stepTokenizer.tokenize("20"));
		
		assertEquals(Integer.valueOf(20), result);
	}

	@Test
	public void testCanConvertPrimiviteArray() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		
		Object result = underTest.convert(int[].class, dummyAnnotations, stepTokenizer.tokenize("20, 10, 0"));
		
		assertArrayEquals(new int[]{20,10,0}, (int[])result);
	}

	@Test
	public void testCanConvertNonParameterizedList() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Method method = FakeMethodClass.class.getMethod("methodWithNonParameterizedList", List.class);

		Object result = underTest.convert(method.getGenericParameterTypes()[0], dummyAnnotations, stepTokenizer.tokenize("20, 10, 0"));
		
		String[] resultAsArray = new String[]{"20", "10", "0"};
		assertEquals(Arrays.asList(resultAsArray), result);
	}

	@Test
	public void testCanConvertParameterizedList() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Method method = FakeMethodClass.class.getMethod("methodWithParameterizedList", List.class);

		Object result = underTest.convert(method.getGenericParameterTypes()[0], dummyAnnotations, stepTokenizer.tokenize("20, 10, 0"));
		
		Integer[] resultAsArray = new Integer[]{20, 10, 0};
		assertEquals(Arrays.asList(resultAsArray), result);
	}

	@Test
	public void testCanConvertParameterizedCollection() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Method method = FakeMethodClass.class.getMethod("methodWithParameterizedCollection", Collection.class);

		Object result = underTest.convert(method.getGenericParameterTypes()[0], dummyAnnotations, stepTokenizer.tokenize("20, 10, 0"));
		
		Integer[] resultAsArray = new Integer[]{20, 10, 0};
		assertEquals(Arrays.asList(resultAsArray), result);
	}

	@Test
	public void testCanConvertArrayWithSpecifiedSeparator() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Annotation[] annotations = new Annotation[]{new SeparatedByDouble("or")};

		Object result = underTest.convert(int[].class, annotations, stepTokenizer.tokenize("20 or 10 or 0"));
		
		assertArrayEquals(new int[]{20,10,0}, (int[])result);
	}

	@Test(expected=CannotConvertException.class)
	public void testCannotConvertParameterizedArrayList() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Method method = FakeMethodClass.class.getMethod("methodWithParameterizedArrayList", ArrayList.class);

		underTest.convert(method.getGenericParameterTypes()[0], dummyAnnotations, stepTokenizer.tokenize(""));
	}

	@Test(expected=CannotConvertException.class)
	public void testCannotConvertGenericCollection() throws Exception {
		ParamStepConverter underTest = new ParamStepConverter(converter, ",");
		Method method = FakeMethodClass.class.getMethod("methodWithGenericCollection", Collection.class);

		underTest.convert(method.getGenericParameterTypes()[0], dummyAnnotations, stepTokenizer.tokenize(""));
	}
}
