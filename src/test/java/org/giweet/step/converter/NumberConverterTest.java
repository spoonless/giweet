package org.giweet.step.converter;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.giweet.annotation.Param;
import org.junit.Test;

public class NumberConverterTest {

	private final class ParamDouble implements Param {
		
		private final String[] pattern;

		public ParamDouble(String ... pattern) {
			this.pattern = pattern;
		}
		
		public Class<? extends Annotation> annotationType() {
			return null;
		}

		public String[] pattern() {
			return pattern;
		}

		public String[] name() {
			return null;
		}
	}

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

	@Test
	public void cannotConvertValueWhenExpectedNotANumber() throws Exception {
		NumberConverter underTest = new NumberConverter(Locale.US);
		Object result = underTest.convert(IllegalArgumentException.class, new Annotation[] {}, "0");
		
		assertNull(result);
	}

	private void canConvertValue(NumberConverter underTest, Annotation[] annotations) throws ParseException {
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