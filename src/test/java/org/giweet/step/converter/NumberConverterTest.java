package org.giweet.step.converter;

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
import org.junit.Test;

public class NumberConverterTest {

	@Test
	public void canConvert() {
		NumberConverter underTest = new NumberConverter(Locale.US);
		
		assertTrue(underTest.canConvert(Number.class));
		assertTrue(underTest.canConvert(int.class));
		assertTrue(underTest.canConvert(long.class));
		assertTrue(underTest.canConvert(double.class));
		assertTrue(underTest.canConvert(float.class));
		assertTrue(underTest.canConvert(short.class));
		assertTrue(underTest.canConvert(byte.class));
		assertTrue(underTest.canConvert(Integer.class));
		assertTrue(underTest.canConvert(Long.class));
		assertTrue(underTest.canConvert(Double.class));
		assertTrue(underTest.canConvert(Float.class));
		assertTrue(underTest.canConvert(Short.class));
		assertTrue(underTest.canConvert(Byte.class));
		assertTrue(underTest.canConvert(AtomicInteger.class));
		assertTrue(underTest.canConvert(AtomicLong.class));
		assertTrue(underTest.canConvert(BigInteger.class));
		assertTrue(underTest.canConvert(BigDecimal.class));
		assertFalse(underTest.canConvert(Object.class));
	}

	@Test
	public void canConvertValue() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		Annotation[] dummyAnnotations = new Annotation[0];
		canConvertValue(underTest, dummyAnnotations);
	}

	@Test
	public void canConvertValueWithPattern() throws Exception {
		Param param = new ParamDouble("#,##0.0#");

		NumberConverter underTest = new NumberConverter(new DecimalFormat("0", new DecimalFormatSymbols(Locale.US)));
		canConvertValue(underTest, new Annotation[] {param});
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenExpectedClassNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(IllegalArgumentException.class, new Annotation[] {}, "0");
	}

	@Test(expected = CannotConvertException.class)
	public void cannotConvertValueWhenValueIsNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		underTest.convert(Integer.class, new Annotation[] {}, "a");
	}

	private void canConvertValue(NumberConverter underTest, Annotation[] annotations) throws CannotConvertException {
		Object result = underTest.convert(Number.class, annotations, "10");
		assertTrue(result instanceof Number);
		assertEquals(10, ((Number)result).intValue());

		result = underTest.convert(Integer.class, annotations, "10");
		assertEquals(Integer.valueOf(10), result);

		result = underTest.convert(int.class, annotations, "10");
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

		result = underTest.convert(Byte.class, annotations, "10");
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