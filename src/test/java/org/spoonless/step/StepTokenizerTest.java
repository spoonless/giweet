package org.spoonless.step;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spoonless.step.DynamicStepToken;
import org.spoonless.step.StaticStepToken;
import org.spoonless.step.StepToken;
import org.spoonless.step.StepTokenizer;

public class StepTokenizerTest {

	@Test
	public void canTokenizeWithoutDynamicToken() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("hello the world");
		testTokenization(stepTokens, "hello", "the", "world");

		stepTokens = underTest.tokenize("100 € 100$");
		testTokenization(stepTokens, "100", "€", "100$");

		stepTokens = underTest.tokenize("$");
		testTokenization(stepTokens, "$");

		stepTokens = underTest.tokenize("$test");
		testTokenization(stepTokens, "$test");

		stepTokens = underTest.tokenize("a b c d e");
		testTokenization(stepTokens, "a", "b", "c", "d", "e");

		stepTokens = underTest.tokenize("1 2 3 4 5 6 7 8 9 10");
		testTokenization(stepTokens, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
	}

	@Test
	public void canTokenizeWithStringWithSpaces() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("           hello         the world      ");
		testTokenization(stepTokens, "hello", "the", "world");
	}

	@Test
	public void canTokenizeWithStringWithNonParsingCharacters() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("@hello/the#world* ");
		testTokenization(stepTokens, "hello", "the", "world");

		stepTokens = underTest.tokenize("&~\"{-)]':,\n\r\t\f-.!?");
		assertEquals(0, stepTokens.length);
	}
	
	@Test
	public void canTokenizeByIngnoringPartsIntoParenthesis() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("(before ignorable block) hello(this must be ignored)the (and that also) world (after ignorable block)");
		testTokenization(stepTokens, "hello", "the", "world");

		stepTokens = underTest.tokenize("(((test) test) test) hello )))))))) the world (ignore even if no end parenthensis is specified");
		testTokenization(stepTokens, "hello", "the", "world");
	}

	@Test
	public void canTokenizeWithDynamicToken() {
		StepTokenizer underTest = new StepTokenizer(true);
		
		StepToken[] stepTokens = underTest.tokenize("hello the $world");
		testTokenization(stepTokens, new StaticStepToken("hello"), new StaticStepToken("the"), new DynamicStepToken("world"));

		stepTokens = underTest.tokenize("hello $the $world");
		testTokenization(stepTokens, new StaticStepToken("hello"), new DynamicStepToken("the"), new DynamicStepToken("world"));

		stepTokens = underTest.tokenize("hello$ $the$ $worl$d");
		testTokenization(stepTokens, new StaticStepToken("hello$"), new DynamicStepToken("the$"), new DynamicStepToken("worl$d"));

		stepTokens = underTest.tokenize("$hello $the $world");
		testTokenization(stepTokens, new DynamicStepToken("hello"), new DynamicStepToken("the"), new DynamicStepToken("world"));

		stepTokens = underTest.tokenize("$hello the $world");
		testTokenization(stepTokens, new DynamicStepToken("hello"), new StaticStepToken("the"), new DynamicStepToken("world"));

		stepTokens = underTest.tokenize("$");
		testTokenization(stepTokens, new StaticStepToken("$"));
	}

	private void testTokenization(StepToken[] stepTokens, String... expectedTokens) {
		StepToken[] expectedStepTokens = new StepToken[expectedTokens.length];
		for (int i = 0 ; i < expectedTokens.length ; i++) {
			expectedStepTokens[i] = new StaticStepToken(expectedTokens[i]);
		}
		testTokenization(stepTokens, expectedStepTokens);
	}

	private void testTokenization(StepToken[] stepTokens, StepToken... expectedTokens) {
		assertEquals(expectedTokens.length, stepTokens.length);
		for (int i = 0 ; i < expectedTokens.length ; i++) {
			assertEquals(expectedTokens[i], stepTokens[i]);
			assertTrue(expectedTokens[i].isDynamic() == stepTokens[i].isDynamic());
			assertEquals(expectedTokens[i].toString(), stepTokens[i].toString());
		}
	}
}
