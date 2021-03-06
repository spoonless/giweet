package org.giweet.step.tree;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Before;
import org.junit.Test;

public class MeaningfulStepTokenIteratorTest {
	
	private MeaningfulStepTokenIterator underTest;

	@Before
	public void init() throws Exception {
		StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);
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
