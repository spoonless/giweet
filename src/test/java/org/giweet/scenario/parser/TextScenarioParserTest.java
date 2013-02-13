package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.io.IOException;
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

	@Test
	public void cannotParseScenarioWithoutScenarioKeyword() throws Exception {
		Scenario scenario = readScenario("scenario.without.title.txt", 1);
		assertNull(scenario);

		scenario = readScenario("scenario.without.title.txt", 2);
		assertNull(scenario);
	}
	
	@Test
	public void canParseSimpleScenarioButCompleteScenario() throws Exception {
		Scenario scenario = readScenario("scenario.wellformed.txt", 1);
		
		assertNotNull(scenario);
		assertEquals("a simple but complete scenario", scenario.getTitle().getText());
		assertEquals(1, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "Given ", "a given statement", scenario.getSentences().get(0));
	}
	
	@Test
	public void canParseMultilineScenario() throws Exception {
		Scenario scenario = readScenario("scenario.wellformed.txt", 2);
		
		assertNotNull(scenario);
		assertEquals("a complete multi line scenario", scenario.getTitle().getText());
		assertEquals(3, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "Given ", "a given statement", scenario.getSentences().get(0));
		assertSentenceIs(KeywordType.WHEN, "When ", "a when statement\nover two lines", scenario.getSentences().get(1));
		assertSentenceIs(KeywordType.THEN, "Then ", "a then statement", scenario.getSentences().get(2));
	}

	@Test
	public void canParseScenarioWithTextBlock() throws Exception {
		Scenario scenario = readScenario("scenario.wellformed.txt", 3);
		
		assertNotNull(scenario);
		assertEquals("a scenario with text blocks", scenario.getTitle().getText());
		assertEquals(3, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "	Given ", "a given statement", scenario.getSentences().get(0));
		assertSentenceIs(KeywordType.WHEN, "	When ", "a when statement", scenario.getSentences().get(1));
		assertSentenceIs(KeywordType.THEN, "	Then ", "a then statement", scenario.getSentences().get(2));
	}
	
	@Test
	public void canParseScenarioWithAndStatement() throws Exception {
		Scenario scenario = readScenario("scenario.wellformed.txt", 4);
		
		assertNotNull(scenario);
		assertEquals("a scenario with \"and\" statements", scenario.getTitle().getText());
		assertEquals(3, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "Given ", "a given statement", scenario.getSentences().get(0));
		assertSentenceIs(KeywordType.AND, "And ", "an and statement", scenario.getSentences().get(1));
		assertSentenceIs(KeywordType.AND, "And ", "another and statement", scenario.getSentences().get(2));
	}
	

	@Test
	public void canParseScenarioWithExamples() throws Exception {
		Scenario scenario = readScenario("scenario.wellformed.txt", 5);
		
		assertNotNull(scenario);
		assertEquals("a scenario with examples", scenario.getTitle().getText());
		assertEquals(4, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "	given ", "the number <number>", scenario.getSentences().get(0));
		assertSentenceIs(KeywordType.WHEN, "	when ", "1 is added to this number", scenario.getSentences().get(1));
		assertSentenceIs(KeywordType.THEN, "	then ", "the result is <result>", scenario.getSentences().get(2));
		assertSentenceIs(KeywordType.EXAMPLES, "Examples:", "\n|number|result|\n|1     |2     |", scenario.getSentences().get(3));
	}

	@Test
	public void cannotParseScenarioWithoutEmptyLineBefore() throws Exception {
		Scenario scenario = readScenario("scenario.malformed.txt", 1);
		
		assertNotNull(scenario);
		assertEquals(3, scenario.getSentences().size());
		assertSentenceIs(KeywordType.GIVEN, "Given ", "a given statement\nScenario: an invalid scenario declaration", scenario.getSentences().get(0));
		assertSentenceIs(KeywordType.WHEN, "When ", "a when statement", scenario.getSentences().get(1));
		assertSentenceIs(KeywordType.THEN, "Then ", "a then statement", scenario.getSentences().get(2));
	}

	private Scenario readScenario(String filename, int scenarioNumber) throws IOException {
		Reader reader = createScenarioReader(filename);
		TextScenarioParser textScenarioParser = new TextScenarioParser(keywordParser, reader);
		Scenario scenario = null;
		for (int i = 0; i < scenarioNumber; i++) {
			scenario = textScenarioParser.nextScenario();
		}
		reader.close();
		return scenario;
	}

	private Reader createScenarioReader(String scenarioResource) {
		InputStream resource = this.getClass().getResourceAsStream(scenarioResource);
		return new InputStreamReader(resource, Charset.forName("utf-8"));
	}
	
	private void assertSentenceIs (KeywordType expectedKeywordType, String expectedKeyword, String expectedText, Sentence sentence) {
		assertTrue("sentence '" + sentence + "' is not processable!", sentence.isProcessable());
		assertEquals(expectedKeywordType, sentence.getKeyword().getType());
		assertEquals(expectedKeyword, sentence.getKeyword().toString());
		assertEquals(expectedText, sentence.getText());
		assertEquals(expectedKeyword + expectedText, sentence.toString());
	}

}
