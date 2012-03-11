package org.giweet.step.tokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;
import org.junit.Test;

public class StepTokenizerTest {
	
	public static class WeirdCharacterAnalyzer implements CharacterAnalyzer {

		public int getCharacterType(char c) {
			if (c == 'a') {
				return CharacterAnalyzer.SEPARATOR;
			}
			else {
				return CharacterAnalyzer.LETTER;
			}
		}

		public char getExpectedEndQuote(char startQuoteCharacter) {
			return 0;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotCreateTokenizerWithBufferSize0() throws Exception {
		new StepTokenizer(TokenizerStrategy.TOKENIZE_SCENARIO, 0);
	}

	@Test
	public void canProvideAlternateCharacterAnalyzer() throws Exception {
		StepTokenizer underTest = new StepTokenizer(TokenizerStrategy.TOKENIZE_SCENARIO, new WeirdCharacterAnalyzer());
		
		StepToken[] stepTokens = underTest.tokenize("abaaabab b");
		
		testTokenization(stepTokens, "a", "b", "aaa", "b", "a", "b b");
	}

	@Test
	public void canTokenizeAsStringFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("StepTokenizerTest.xml", 0);
	}

	@Test
	public void canTokenizeAsStreamWithTinyBufferFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("StepTokenizerTest.xml", 1);
	}

	@Test
	public void canTokenizeAsStreamFromXmlFile() throws Exception {
		StepTokenizerTestFromXml stepTokenizerTestFromXml = new StepTokenizerTestFromXml();
		stepTokenizerTestFromXml.testFromFile("StepTokenizerTest.xml", 10);
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
