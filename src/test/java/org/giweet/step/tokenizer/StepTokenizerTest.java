package org.giweet.step.tokenizer;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.xml.XmlSuiteDescriptor;
import org.junit.Test;

public class StepTokenizerTest {
	
	private final JAXBContext jaxbContext;

	public StepTokenizerTest() throws Exception{
		jaxbContext = JAXBContext.newInstance(XmlSuiteDescriptor.class);
	}
	
	public static class WeirdCharacterAnalyzer implements CharacterAnalyzer {

		@Override
		public int getCharacterType(char c) {
			if (c == 'a') {
				return CharacterAnalyzer.SEPARATOR;
			}
			else {
				return CharacterAnalyzer.LETTER;
			}
		}

		@Override
		public char getExpectedQuoteTail(char quoteTail) {
			return 0;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void cannotCreateTokenizerWithBufferSize0() throws Exception {
		new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE, 0);
	}

	@Test
	public void canProvideAlternateCharacterAnalyzer() throws Exception {
		StepTokenizer underTest = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE, new WeirdCharacterAnalyzer());
		
		StepToken[] stepTokens = underTest.tokenize("abaaabab b");
		
		testTokenization(stepTokens, "a", "b", "aaa", "b", "a", "b b");
	}

	@Test
	public void canTokenizeAsStringFromXmlFile() throws Exception {
		loadSuite().run();
	}

	private XmlSuiteDescriptor loadSuite() throws JAXBException {
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		XmlSuiteDescriptor suite = (XmlSuiteDescriptor) unmarshaller.unmarshal(StepTokenizerTest.class.getResourceAsStream("StepTokenizerTest.xml"));
		return suite;
	}

	@Test
	public void canTokenizeAsStreamWithTinyBufferFromXmlFile() throws Exception {
		loadSuite().run(1);
	}

	@Test
	public void canTokenizeAsStreamFromXmlFile() throws Exception {
		loadSuite().run(10);
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
