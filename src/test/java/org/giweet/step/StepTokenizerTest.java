package org.giweet.step;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StepTokenizerTest {

	@Test
	public void canTokenizeWithoutParameterToken() {
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
	public void canTokenizeWithStringWithQuotes() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("\"hello the world\"");
		testTokenization(stepTokens, "hello the world");

		stepTokens = underTest.tokenize("hello \" the world \"");
		testTokenization(stepTokens, "hello", " the world ");

		stepTokens = underTest.tokenize("\"hello the world\" \"   ");
		testTokenization(stepTokens, "hello the world", "   ");

		stepTokens = underTest.tokenize("\"hello the world\" \"   ");
		testTokenization(stepTokens, "hello the world", "   ");
	}

	@Test
	public void canTokenizeWithStringWithNonParsingCharacters() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("@hello/the#world*");
		testTokenization(stepTokens, "@hello/the#world*");

		stepTokens = underTest.tokenize(" [hel{lo] ");
		testTokenization(stepTokens, "hel{lo");

		stepTokens = underTest.tokenize(" hello? ");
		testTokenization(stepTokens, "hello");

		stepTokens = underTest.tokenize("it's ok");
		testTokenization(stepTokens, "it", "s", "ok");

		stepTokens = underTest.tokenize("http://localhost:8080/mywebsite/");
		testTokenization(stepTokens, "http://localhost:8080/mywebsite/");
	}
	
	@Test
	public void canTokenizeWithMeaninglessTokens() {
		StepTokenizer underTest = new StepTokenizer(false, true);

		StepToken[] stepTokens = underTest.tokenize("\thello, the world  ");
		testTokenization(stepTokens, "\t", "hello", ", ", "the", " ", "world", "  ");
		assertFalse(stepTokens[0].isMeaningful());
		assertTrue(stepTokens[1].isMeaningful());
		assertFalse(stepTokens[2].isMeaningful());
		assertTrue(stepTokens[3].isMeaningful());
		assertFalse(stepTokens[4].isMeaningful());
		assertTrue(stepTokens[5].isMeaningful());
		assertFalse(stepTokens[6].isMeaningful());
		
		stepTokens = underTest.tokenize("\thello, the world( ");
		testTokenization(stepTokens, "\t", "hello", ", ", "the", " ", "world", "( ");

		stepTokens = underTest.tokenize("hello (the) world");
		testTokenization(stepTokens, "hello", " (the) ", "world");

		stepTokens = underTest.tokenize("hello(the)world");
		testTokenization(stepTokens, "hello", "(the)", "world");

		stepTokens = underTest.tokenize("hello : world");
		testTokenization(stepTokens, "hello", " : ", "world");
	}

	@Test
	public void canTokenizeByIgnoringPartsIntoParenthesis() {
		StepTokenizer underTest = new StepTokenizer(false);

		StepToken[] stepTokens = underTest.tokenize("(before ignorable block) hello(this must be ignored)the (and that also) world (after ignorable block)");
		testTokenization(stepTokens, "hello", "the", "world");

		stepTokens = underTest.tokenize("(((test) test) test) hello )))))))) the world (ignore even if no end parenthensis is specified");
		testTokenization(stepTokens, "hello", "the", "world");
	}

	@Test
	public void canTokenizeWithParameterToken() {
		StepTokenizer underTest = new StepTokenizer(true);
		
		StepToken[] stepTokens = underTest.tokenize("hello the $world");
		testTokenization(stepTokens, new StaticStepToken("hello"), new StaticStepToken("the"), new ParameterStepToken("world"));

		stepTokens = underTest.tokenize("hello $the $world");
		testTokenization(stepTokens, new StaticStepToken("hello"), new ParameterStepToken("the"), new ParameterStepToken("world"));

		stepTokens = underTest.tokenize("hello$ $the$ $worl$d");
		testTokenization(stepTokens, new StaticStepToken("hello$"), new ParameterStepToken("the$"), new ParameterStepToken("worl$d"));

		stepTokens = underTest.tokenize("$hello $the $world");
		testTokenization(stepTokens, new ParameterStepToken("hello"), new ParameterStepToken("the"), new ParameterStepToken("world"));

		stepTokens = underTest.tokenize("$hello the $world");
		testTokenization(stepTokens, new ParameterStepToken("hello"), new StaticStepToken("the"), new ParameterStepToken("world"));

		stepTokens = underTest.tokenize("$");
		testTokenization(stepTokens, new StaticStepToken("$"));
	}
	
	@Test
	public void canTokenizeAsStringFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("/org/giweet/step/StepTokenizerTest.xml", 0);
	}

	@Test
	public void canTokenizeAsStreamWithTinyBufferFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("/org/giweet/step/StepTokenizerTest.xml", 1);
	}

	@Test
	public void canTokenizeAsStreamFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("/org/giweet/step/StepTokenizerTest.xml", 10);
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
			assertTrue(expectedTokens[i].isParameter() == stepTokens[i].isParameter());
			assertEquals(expectedTokens[i].toString(), stepTokens[i].toString());
		}
	}
}
