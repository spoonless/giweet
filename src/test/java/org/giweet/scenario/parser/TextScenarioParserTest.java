package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Locale;

import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;
import org.junit.Before;
import org.junit.Test;

public class TextScenarioParserTest {
	
	private KeywordParser keywordParser;
	
	@Before
	public void createKeywordParser() {
		keywordParser = new KeywordParser(Locale.ENGLISH);
	}

	private Reader createScenarioReader(String scenarioResource) {
		InputStream resource = this.getClass().getResourceAsStream(scenarioResource);
		return new InputStreamReader(resource, Charset.forName("utf-8"));
	}
	
	@Test
	public void canParseScenarioWithoutScenarioKeyword() throws Exception {
		Reader reader = createScenarioReader("simple.scenario.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "", "", scenario.getTitle());
		assertEquals(1, scenario.getSentences().size());
		assertEquals(KeywordType.GIVEN, scenario.getSentences().get(0).getKeyword().getType());
		assertEquals("given just a given step", scenario.getSentences().get(0).toString());
	}

	@Test
	public void canParseAllScenarios() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "Scenario: ", "a simple scenario", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "ScEnArIo: ", "a scenario mixing cases", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "     scenario : ", "a scenario mixing indentation and formatting", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "   Scenario: ", "a simple pretty scenario", scenario.getTitle());
		
		assertNull(underTest.nextScenario());
	}

	@Test
	public void canParseFirstScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;

		assertEquals(5, scenario.getSentences().size());
		assertSentenceIsNotProcessable("", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.GIVEN, "given ", "a given statement", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.WHEN, "when ", "a when statement", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.THEN, "then ", "a then statement", scenario.getSentences().get(3));
		assertSentenceIsNotProcessable("", scenario.getSentences().get(4));
	}

	@Test
	public void canParseSecondScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		underTest.nextScenario();
		Scenario scenario = underTest.nextScenario();

		assertEquals(8, scenario.getSentences().size());

		assertSentenceIsNotProcessable("", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.GIVEN, "GIVEN ", "a given statement", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.AND, "aNd ", "a second given statement", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.AND, "And ", "a third given statement", scenario.getSentences().get(3));

		assertSentenceIsProcessable(KeywordType.WHEN, "wheN ", "a when statement", scenario.getSentences().get(4));
		assertSentenceIsProcessable(KeywordType.AND, "aNd ", "a second when statement", scenario.getSentences().get(5));

		assertSentenceIsProcessable(KeywordType.THEN, "Then ", "a then statement", scenario.getSentences().get(6));
		assertSentenceIsNotProcessable("", scenario.getSentences().get(7));
	}
	
	@Test
	public void canParseThirdScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		underTest.nextScenario();
		underTest.nextScenario();
		Scenario scenario = underTest.nextScenario();

		assertEquals(9, scenario.getSentences().size());

		assertSentenceIsProcessable(KeywordType.GIVEN, "Given ", "a given statement", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.AND, "     And ", "an incremented given statement", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.AND, "and ", "an non incremented given statement", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.WHEN, "when ", "a when statement", scenario.getSentences().get(3));
		assertSentenceIsProcessable(KeywordType.THEN, "then\t\t\t", "a then statement", scenario.getSentences().get(4));
		assertSentenceIsProcessable(KeywordType.AND, "and ", "a multiline\nstatement\nwithout any\nempty line", scenario.getSentences().get(5));
		assertSentenceIsNotProcessable(
				"\nthis line is ignored because it is preceding by an empty line (even with the words given when then written\n" +
				"And this line is also ignored even if And is a valid keyword\n" +
				"then the final line but also ignored even if then is a valid keyword\n", scenario.getSentences().get(6));

		assertSentenceIsProcessable(KeywordType.THEN, "Then ", "the final then statement", scenario.getSentences().get(7));
		assertSentenceIsNotProcessable("\n+=======================================+", scenario.getSentences().get(8));
	}

	private void assertSentenceIsProcessable (KeywordType expectedKeywordType, String expectedKeyword, String expectedText, Sentence sentence) {
		assertTrue(sentence.isProcessable());
		assertEquals(expectedKeywordType, sentence.getKeyword().getType());
		assertEquals(expectedKeyword, sentence.getKeyword().toString());
		assertEquals(expectedText, sentence.getText());
		assertEquals(expectedKeyword + expectedText, sentence.toString());
	}

	private void assertSentenceIsNotProcessable (String expectedText, Sentence sentence) {
		assertFalse(sentence.isProcessable());
		assertNull(sentence.getKeyword());
		assertEquals(expectedText, sentence.getText());
		assertEquals(expectedText, sentence.toString());
	}
}
