package org.spoonless;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StepDescriptorTest {

	@Test
	public void canTokenizeWithString() {
		StepDescriptor underTest = new StepDescriptor("hello the world");
		testTokenization(underTest, "hello", "the", "world");

		underTest = new StepDescriptor("100 € 100$");
		testTokenization(underTest, "100", "€", "100$");

		underTest = new StepDescriptor("$");
		testTokenization(underTest, "$");

		underTest = new StepDescriptor("a b c d e");
		testTokenization(underTest, "a", "b", "c", "d", "e");

		underTest = new StepDescriptor("1 2 3 4 5 6 7 8 9 10");
		testTokenization(underTest, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
	}

	@Test
	public void canTokenizeWithStringWithSpaces() {
		StepDescriptor underTest = new StepDescriptor("           hello         the world      ");
		testTokenization(underTest, "hello", "the", "world");
	}

	@Test
	public void canTokenizeWithStringWithNonParsingCharacters() {
		StepDescriptor underTest = new StepDescriptor("@hello/the#world* ");
		testTokenization(underTest, "hello", "the", "world");

		underTest = new StepDescriptor("&~\"{-)]':,\n\r\t\f-.!?");
		testTokenization(underTest);
	}
	
	@Test
	public void canTokenizeByIngnoringPartsIntoParenthesis() {
		StepDescriptor underTest = new StepDescriptor("(before ignorable block) hello(this must be ignored)the (and that also) world (after ignorable block)");
		testTokenization(underTest, "hello", "the", "world");

		underTest = new StepDescriptor("(((test) test) test) hello )))))))) the world (ignore even if no end parenthensis is specified");
		testTokenization(underTest, "hello", "the", "world");
	}

	@Test
	public void canTokenizeWithArguments() {
		StepDescriptor underTest = new StepDescriptor("hello the $world");
		testTokenization(underTest, new StringToken("hello"), new StringToken("the"), new ArgumentToken("world"));

		underTest = new StepDescriptor("hello $the $world");
		testTokenization(underTest, new StringToken("hello"), new ArgumentToken("the"), new ArgumentToken("world"));

		underTest = new StepDescriptor("hello$ $the$ $worl$d");
		testTokenization(underTest, new StringToken("hello$"), new ArgumentToken("the$"), new ArgumentToken("worl$d"));

		underTest = new StepDescriptor("$hello $the $world");
		testTokenization(underTest, new ArgumentToken("hello"), new ArgumentToken("the"), new ArgumentToken("world"));

		underTest = new StepDescriptor("$hello the $world");
		testTokenization(underTest, new ArgumentToken("hello"), new StringToken("the"), new ArgumentToken("world"));
	}

	@Test
	public void canCompare() {
		assertStepDescriptorComparison(0, "hello the world", "hello the world");
		assertStepDescriptorComparison(0, "HELLO THE WORLD", "hello the world");
		assertStepDescriptorComparison(0, "hello the world", "    hello   the    world   ");
		assertStepDescriptorComparison(0, "hello $the world", "hello $a world");
		assertStepDescriptorComparison(0, "$1, $2, $3", "$a $b $c (don't forget we ignore some characters)");
		assertStepDescriptorComparison(0, "", "");
		assertStepDescriptorComparison(0, "$", "$ ($ sign is regarded as an argument if alone)");
		assertStepDescriptorComparison(-1, "a", "hello the world");
		assertStepDescriptorComparison(-1, "hello", "hello the world");
		assertStepDescriptorComparison(-1, "hello the world", "hello $the world");
		assertStepDescriptorComparison(-1, "hello the $world", "hello $the world");
		assertStepDescriptorComparison(-1, "hello the $world $world", "hello the $world");
		assertStepDescriptorComparison(-1, "hello the world", "$i");
		assertStepDescriptorComparison(-1, "", "$i");
	}

	private void assertStepDescriptorComparison(int comparisonResult, String step1, String step2) {
		StepDescriptor stepDescriptor1 = new StepDescriptor(step1);
		StepDescriptor stepDescriptor2 = new StepDescriptor(step2);
		assertEquals(comparisonResult, stepDescriptor1.compareTo(stepDescriptor2));
		assertEquals(-comparisonResult, stepDescriptor2.compareTo(stepDescriptor1));
	}

	private void testTokenization(StepDescriptor underTest, String... expectedTokens) {
		StepToken[] tokens = new StepToken[expectedTokens.length];
		for (int i = 0 ; i < expectedTokens.length ; i++) {
			tokens[i] = new StringToken(expectedTokens[i]);
		}
		testTokenization(underTest, tokens);
	}

	private void testTokenization(StepDescriptor underTest) {
		StepToken[] tokens = underTest.getTokens();
		assertEquals(0, tokens.length);
	}

	private void testTokenization(StepDescriptor underTest, StepToken... expectedTokens) {
		StepToken[] tokens = underTest.getTokens();
		assertEquals(expectedTokens.length, tokens.length);
		for (int i = 0 ; i < expectedTokens.length ; i++) {
			assertEquals(expectedTokens[i], tokens[i]);
			assertEquals(expectedTokens[i].toString(), tokens[i].toString());
		}
	}
}
