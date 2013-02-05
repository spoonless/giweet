package org.giweet.step.tokenizer;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.giweet.step.SeparatorStepToken;
import org.giweet.step.StaticStepToken;
import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.xml.XmlSuiteDescriptor;
import org.junit.Test;

public class StepTokenizerTest {
	
	private final JAXBContext jaxbContext;

	public StepTokenizerTest() throws Exception{
		jaxbContext = JAXBContext.newInstance(XmlSuiteDescriptor.class);
	}
	
	public static class WeirdCharacterAnalyzer extends DefaultCharacterAnalyzer {

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
	
	@Test
	public void canProvideAlternateCharacterAnalyzer() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new WeirdCharacterAnalyzer());
		
		StepToken[] stepTokens = underTest.tokenize("abaaabab b");
		
		StepToken separatorToken = new SeparatorStepToken("a", "a");
		
		testTokenization(stepTokens, 
				separatorToken, new StaticStepToken("b"), 
				separatorToken, separatorToken, separatorToken, 
				new StaticStepToken("b"), separatorToken, new StaticStepToken("b b"));
	}

	@Test
	public void canIgnoreQuoteHeadWhenNoQuoteTailGivenForSeparatorQuoteHead() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new DefaultCharacterAnalyzer() {
			@Override
			public char getExpectedQuoteTail(char quoteHead) {
				return 0;
			}
		});
		
		StepToken[] stepTokens = underTest.tokenize("«hello");
		
		testTokenization(stepTokens, "«", "hello");
	}

	@Test
	public void canIgnoreQuoteHeadWhenNoQuoteTailGivenForLetterQuoteHead() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new DefaultCharacterAnalyzer() {
			@Override
			public char getExpectedQuoteTail(char quoteHead) {
				return 0;
			}
			
			@Override
			public int getCharacterType(char c) {
				return c == 'x' ? CharacterAnalyzer.QUOTE_HEAD | CharacterAnalyzer.LETTER : CharacterAnalyzer.LETTER;
			}
		});
		
		StepToken[] stepTokens = underTest.tokenize("xbbbb");
		testTokenization(stepTokens, "xbbbb");
	}

	@Test
	public void canIgnoreSeparatorDynamicHeadWhenEmptyCharReturnedForDynamicTail() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new StepDeclarationCharAnalyzer(new DefaultCharacterAnalyzer(), '{', (char) 0));
		
		StepToken[] stepTokens = underTest.tokenize("{0");
		
		testTokenization(stepTokens, "{", "0");
	}

	@Test
	public void canIgnoreLetterDynamicHeadWhenEmptyCharReturnedForDynamicTail() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new StepDeclarationCharAnalyzer(new DefaultCharacterAnalyzer(), 'a', (char) 0));
		
		StepToken[] stepTokens = underTest.tokenize("ab");
		
		testTokenization(stepTokens, "ab");
	}

	@Test
	public void canThrowQuoteTailNotFoundException() throws Exception {
		StepTokenizer underTest = new StepTokenizer(new DefaultCharacterAnalyzer());
		
		try {
			underTest.tokenize("«hello");
			Assert.fail("QuoteTailNotFoundException expected!");
		}
		catch (QuoteTailNotFoundException e) {
			Assert.assertEquals('»', e.getExpectedQuoteTail());
		}
	}

	@Test
	public void canTokenizeAsStringFromXmlFile() throws Exception {
		StepTokenizer stepTokenizer = new StepTokenizer(new StepDeclarationCharAnalyzer(new DefaultCharacterAnalyzer()));
		loadSuite().run(stepTokenizer);
	}

	private XmlSuiteDescriptor loadSuite() throws JAXBException {
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		XmlSuiteDescriptor suite = (XmlSuiteDescriptor) unmarshaller.unmarshal(StepTokenizerTest.class.getResourceAsStream("StepTokenizerTest.xml"));
		return suite;
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
