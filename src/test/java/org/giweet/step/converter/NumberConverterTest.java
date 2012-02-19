package org.giweet.step.converter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.giweet.annotation.Param;
import org.giweet.annotation.ParamDouble;
import org.giweet.step.StepTokenizer;
import org.junit.Test;

public class NumberConverterTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(false, true);
	private Annotation[] dummyAnnotations = new Annotation[0];

	@Test
	public void canConvert() {
		NumberConverter underTest = new NumberConverter(Locale.US);
		
		assertTrue(underTest.canConvert(Number.class));
		assertTrue(underTest.canConvert(Integer.class));
		assertTrue(underTest.canConvert(Long.class));
		assertTrue(underTest.canConvert(Double.class));
		assertTrue(underTest.canConvert(Float.class));
		assertTrue(underTest.canConvert(Short.class));
		assertTrue(underTest.canConvert(Byte.class));
		assertTrue(underTest.canConvert(int.class));
		assertTrue(underTest.canConvert(long.class));
		assertTrue(underTest.canConvert(double.class));
		assertTrue(underTest.canConvert(float.class));
		assertTrue(underTest.canConvert(short.class));
		assertTrue(underTest.canConvert(byte.class));
		assertTrue(underTest.canConvert(AtomicInteger.class));
		assertTrue(underTest.canConvert(AtomicLong.class));
		assertTrue(underTest.canConvert(BigInteger.class));
		assertTrue(underTest.canConvert(BigDecimal.class));
		assertFalse(underTest.canConvert(Object.class));
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
	public void canConvertValueAsArray() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		
		Long[] result = (Long[]) underTest.convert(Long[].class, dummyAnnotations, stepTokenizer.tokenize("0 1, -2"));
		assertArrayEquals(new Long[]{0L, 1L, -2L}, result);
		
		long[] primiviteResult = (long[]) underTest.convert(long[].class, dummyAnnotations, stepTokenizer.tokenize("0 1, -2"));
		assertArrayEquals(new long[]{0L, 1L, -2L}, primiviteResult);

		float[] primiviteFloatResult = (float[]) underTest.convert(float[].class, dummyAnnotations, stepTokenizer.tokenize("0 1.3, -2"));
		assertArrayEquals(new float[]{0f, 1.3f, -2f}, primiviteFloatResult, 0);
	}

	@Test
	public void canConvertValueAsArrayWithNonBreakableSpaceFixForFrench() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.FRENCH);
		
		double[] result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("1 000,0, 10000,30 -10 000\u00a0000"));
		assertArrayEquals(new double[]{1000, 10000.3, -10000000}, result, 0);

		result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("1 2 3"));
		assertArrayEquals(new double[]{1, 2, 3}, result, 0);

		result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("99 999\n999"));
		assertArrayEquals(new double[]{99999, 999}, result, 0);

		result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("9 999 999"));
		assertArrayEquals(new double[]{9999999}, result, 0);

		result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("9  999"));
		assertArrayEquals(new double[]{9, 999}, result, 0);

		result = (double[]) underTest.convert(double[].class, dummyAnnotations, stepTokenizer.tokenize("9 999 -999"));
		assertArrayEquals(new double[]{9999, -999}, result, 0);
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenExpectedClassNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(IllegalArgumentException.class, new Annotation[] {}, stepTokenizer.tokenize("0"));
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenValueIsNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(Integer.class, new Annotation[] {}, stepTokenizer.tokenize("a"));
	}

	private void canConvertValue(NumberConverter underTest, Annotation[] annotations) throws CannotConvertException {
		Object result = underTest.convert(Number.class, annotations, stepTokenizer.tokenize("10"));
		assertTrue(result instanceof Number);
		assertEquals(10, ((Number)result).intValue());

		result = underTest.convert(Integer.class, annotations, stepTokenizer.tokenize("10"));
		assertEquals(Integer.valueOf(10), result);

		result = underTest.convert(Long.class, annotations, stepTokenizer.tokenize("1,000"));
		assertEquals(Long.valueOf(1000), result);

		result = underTest.convert(Double.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Double.valueOf(1.1), result);

		result = underTest.convert(Float.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Float.valueOf(1.1f), result);

		result = underTest.convert(Short.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Short.valueOf((short)1), result);

		result = underTest.convert(Byte.class, annotations, stepTokenizer.tokenize("10"));
		assertEquals(Byte.valueOf((byte)10), result);

		result = underTest.convert(int.class, annotations, stepTokenizer.tokenize("10"));
		assertEquals(Integer.valueOf(10), result);

		result = underTest.convert(long.class, annotations, stepTokenizer.tokenize("1,000"));
		assertEquals(Long.valueOf(1000), result);

		result = underTest.convert(double.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Double.valueOf(1.1), result);

		result = underTest.convert(float.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Float.valueOf(1.1f), result);

		result = underTest.convert(short.class, annotations, stepTokenizer.tokenize("1.1"));
		assertEquals(Short.valueOf((short)1), result);

		result = underTest.convert(byte.class, annotations, stepTokenizer.tokenize("10"));
		assertEquals(Byte.valueOf((byte)10), result);

		result = underTest.convert(BigInteger.class, annotations, stepTokenizer.tokenize("10"));
		assertEquals(new BigInteger("10"), result);

		result = underTest.convert(BigDecimal.class, annotations, stepTokenizer.tokenize("10.1"));
		assertEquals(10.1, ((BigDecimal)result).doubleValue(), 0);

		result = underTest.convert(AtomicInteger.class, annotations, stepTokenizer.tokenize("10"));
		assertTrue(result instanceof AtomicInteger);
		assertEquals(10, ((AtomicInteger)result).intValue());

		result = underTest.convert(AtomicLong.class, annotations, stepTokenizer.tokenize("10"));
		assertTrue(result instanceof AtomicLong);
		assertEquals(10L, ((AtomicLong)result).longValue());
	}
	
}