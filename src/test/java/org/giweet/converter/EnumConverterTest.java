package org.giweet.converter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import org.junit.Test;

public class EnumConverterTest {
	
	private Annotation[] dummyAnnotations = {};
	
	private static enum TestEnum {
		TEST1, TEST2;
		
		@Override
		public String toString() {
			return "test enum with ordinal " + this.ordinal();		
		}
	}

	@Test
	public void canGetSupportedClasses() {
		EnumConverter underTest = new EnumConverter();
		
		Class<?>[] result = underTest.getSupportedClasses();
		
		assertArrayEquals(new Class<?>[]{Enum.class} , result);
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertWhenTargetClassIsNotAnEnum() throws Exception {
		EnumConverter underTest = new EnumConverter();
		
		underTest.convert(Integer.class, dummyAnnotations, "");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertWhenValueIsInvalid() throws Exception {
		EnumConverter underTest = new EnumConverter();
		
		underTest.convert(TestEnum.class, dummyAnnotations, "INVALID VALUE");
	}

	@Test(expected=CannotConvertException.class)
	public void cannotConvertWhenExpectedClassIsEnum() throws Exception {
		EnumConverter underTest = new EnumConverter();
		
		underTest.convert(Enum.class, dummyAnnotations, "TEST1");
	}

	@Test
	public void canConvert() throws Exception {
		EnumConverter underTest = new EnumConverter();
		
		Object result = underTest.convert(TestEnum.class, dummyAnnotations, "test enum with ordinal 0");
		assertEquals(TestEnum.TEST1, result);

		result = underTest.convert(TestEnum.class, dummyAnnotations, "TEST2");
		assertEquals(TestEnum.TEST2, result);
	}
}
