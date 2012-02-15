package org.giwit.step.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.giwit.step.StepToken;
import org.giwit.step.StepTokenizer;
import org.giwit.step.tree.MeaningfulStepTokenIterator;
import org.junit.Before;
import org.junit.Test;

public class MeaningfulStepTokenIteratorTest {
	
	private MeaningfulStepTokenIterator underTest;

	@Before
	public void init() {
		StepTokenizer stepTokenizer = new StepTokenizer(false, true);
		StepToken[] stepTokens = stepTokenizer.tokenize("hello the world!");
		assertEquals(6, stepTokens.length);
		underTest = new MeaningfulStepTokenIterator(stepTokens);
	}

	@Test(expected = NoSuchElementException.class)
	public void cannotGoPrevious() {
		underTest.previous();
	}

	@Test(expected = NoSuchElementException.class)
	public void cannotGoNext() {
		underTest = new MeaningfulStepTokenIterator();
		underTest.next();
	}

	@Test
	public void canGoBackAndForth() {
		assertTrue(underTest.hasNext());
		assertFalse(underTest.hasPrevious());

		assertEquals("hello", underTest.next().toString());
		assertTrue(underTest.hasNext());
		assertFalse(underTest.hasPrevious());

		assertEquals("the", underTest.next().toString());
		assertTrue(underTest.hasNext());
		assertTrue(underTest.hasPrevious());

		assertEquals("world", underTest.next().toString());
		assertFalse(underTest.hasNext());
		assertTrue(underTest.hasPrevious());

		assertEquals("the", underTest.previous().toString());
		assertTrue(underTest.hasNext());
		assertTrue(underTest.hasPrevious());

		assertEquals("hello", underTest.previous().toString());
		assertTrue(underTest.hasNext());
		assertFalse(underTest.hasPrevious());
	}
}
