package org.giweet.step.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.giweet.annotation.Param;
import org.giweet.annotation.ParamDouble;
import org.junit.Test;

public class NumberConverterTest {

	private Annotation[] dummyAnnotations = new Annotation[0];

	@Test
	public void canGetSupportedClasses() {
		NumberConverter underTest = new NumberConverter(Locale.US);
		
		Class<?>[] result = underTest.getSupportedClasses();

		List<Class<?>> convertibleClasses = Arrays.asList(result);
		assertEquals(17, convertibleClasses.size());
		assertTrue(convertibleClasses.contains(Number.class));
		assertTrue(convertibleClasses.contains(Integer.class));
		assertTrue(convertibleClasses.contains(Long.class));
		assertTrue(convertibleClasses.contains(Double.class));
		assertTrue(convertibleClasses.contains(Float.class));
		assertTrue(convertibleClasses.contains(Short.class));
		assertTrue(convertibleClasses.contains(Byte.class));
		assertTrue(convertibleClasses.contains(int.class));
		assertTrue(convertibleClasses.contains(long.class));
		assertTrue(convertibleClasses.contains(double.class));
		assertTrue(convertibleClasses.contains(float.class));
		assertTrue(convertibleClasses.contains(short.class));
		assertTrue(convertibleClasses.contains(byte.class));
		assertTrue(convertibleClasses.contains(AtomicInteger.class));
		assertTrue(convertibleClasses.contains(AtomicLong.class));
		assertTrue(convertibleClasses.contains(BigInteger.class));
		assertTrue(convertibleClasses.contains(BigDecimal.class));
		assertFalse(convertibleClasses.contains(Object.class));
		assertFalse(convertibleClasses.contains(boolean.class));
	}

	@Test
	public void canConvertValue() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		canConvertValue(underTest, dummyAnnotations);
	}

	@Test
	public void canConvertValueWithPattern() throws Exception {
		Param param = new ParamDouble("#,##0.0#");

		NumberConverter underTest = new NumberConverter(new DecimalFormat("0", new DecimalFormatSymbols(Locale.US)));
		canConvertValue(underTest, new Annotation[] {param});
	}

	@Test
	public void canConvertValueWithNonBreakableSpaceFixForFrench() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.FRENCH);
		
		Double result = (Double) underTest.convert(double.class, dummyAnnotations, "1 000,0");
		assertEquals(1000, result.doubleValue(), 0);

		result = (Double) underTest.convert(double.class, dummyAnnotations, "1\u00a0000,0");
		assertEquals(1000, result.doubleValue(), 0);
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenExpectedClassNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(Boolean.class, dummyAnnotations, "0");
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenValueIsNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(Integer.class, dummyAnnotations, "a");
	}

	private void canConvertValue(NumberConverter underTest, Annotation[] annotations) throws CannotConvertException {
		Object result = underTest.convert(Number.class, annotations, "10");
		assertTrue(result instanceof Number);
		assertEquals(10, ((Number)result).intValue());

		result = underTest.convert(Integer.class, annotations, "10");
		assertEquals(Integer.valueOf(10), result);

		result = underTest.convert(Long.class, annotations, "1,000");
		assertEquals(Long.valueOf(1000), result);

		result = underTest.convert(Double.class, annotations, "1.1");
		assertEquals(Double.valueOf(1.1), result);

		result = underTest.convert(Float.class, annotations, "1.1");
		assertEquals(Float.valueOf(1.1f), result);

		result = underTest.convert(Short.class, annotations, "1.1");
		assertEquals(Short.valueOf((short)1), result);

		result = underTest.convert(Byte.class, annotations, "10");
		assertEquals(Byte.valueOf((byte)10), result);

		result = underTest.convert(int.class, annotations, "10");
		assertEquals(Integer.valueOf(10), result);

		result = underTest.convert(long.class, annotations, "1,000");
		assertEquals(Long.valueOf(1000), result);

		result = underTest.convert(double.class, annotations, "1.1");
		assertEquals(Double.valueOf(1.1), result);

		result = underTest.convert(float.class, annotations, "1.1");
		assertEquals(Float.valueOf(1.1f), result);

		result = underTest.convert(short.class, annotations, "1.1");
		assertEquals(Short.valueOf((short)1), result);

		result = underTest.convert(byte.class, annotations, "10");
		assertEquals(Byte.valueOf((byte)10), result);

		result = underTest.convert(BigInteger.class, annotations, "10");
		assertEquals(new BigInteger("10"), result);

		result = underTest.convert(BigDecimal.class, annotations, "10.1");
		assertEquals(10.1, ((BigDecimal)result).doubleValue(), 0);

		result = underTest.convert(AtomicInteger.class, annotations, "10");
		assertTrue(result instanceof AtomicInteger);
		assertEquals(10, ((AtomicInteger)result).intValue());

		result = underTest.convert(AtomicLong.class, annotations, "10");
		assertTrue(result instanceof AtomicLong);
		assertEquals(10L, ((AtomicLong)result).longValue());
	}
}