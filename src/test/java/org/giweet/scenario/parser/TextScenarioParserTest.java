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
import org.giweet.scenario.Story;
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
		assertSentenceIsProcessable(KeywordType.GIVEN, "given ", "just a given step\n", scenario.getSentences().get(0));
	}

	@Test
	public void canParseAllScenarios() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "Scenario: ", "a simple scenario\n", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "ScEnArIo: ", "a scenario mixing cases\n", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "     scenario : ", "a scenario mixing indentation and formatting\n", scenario.getTitle());

		scenario = underTest.nextScenario() ;
		assertSentenceIsProcessable(KeywordType.SCENARIO, "   Scenario: ", "a simple pretty scenario\n", scenario.getTitle());
		
		assertNull(underTest.nextScenario());
	}

	@Test
	public void canParseFirstScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;

		assertEquals(5, scenario.getSentences().size());
		assertSentenceIsNotProcessable("\n", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.GIVEN, "given ", "a given statement\n", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.WHEN, "when ", "a when statement\n", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.THEN, "then ", "a then statement\n", scenario.getSentences().get(3));
		assertSentenceIsNotProcessable("\n", scenario.getSentences().get(4));
	}

	@Test
	public void canParseSecondScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		underTest.nextScenario();
		Scenario scenario = underTest.nextScenario();

		assertEquals(8, scenario.getSentences().size());

		assertSentenceIsNotProcessable("\n", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.GIVEN, "GIVEN ", "a given statement\n", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.AND, "aNd ", "a second given statement\n", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.AND, "And ", "a third given statement\n", scenario.getSentences().get(3));

		assertSentenceIsProcessable(KeywordType.WHEN, "wheN ", "a when statement\n", scenario.getSentences().get(4));
		assertSentenceIsProcessable(KeywordType.AND, "aNd ", "a second when statement\n", scenario.getSentences().get(5));

		assertSentenceIsProcessable(KeywordType.THEN, "Then ", "a then statement\n", scenario.getSentences().get(6));
		assertSentenceIsNotProcessable("\n", scenario.getSentences().get(7));
	}
	
	@Test
	public void canParseThirdScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		underTest.nextScenario();
		underTest.nextScenario();
		Scenario scenario = underTest.nextScenario();

		assertEquals(9, scenario.getSentences().size());

		assertSentenceIsProcessable(KeywordType.GIVEN, "Given ", "a given statement\n", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.AND, "     And ", "an incremented given statement\n", scenario.getSentences().get(1));
		assertSentenceIsProcessable(KeywordType.AND, "and ", "an non incremented given statement\n", scenario.getSentences().get(2));
		assertSentenceIsProcessable(KeywordType.WHEN, "when\n", "a when statement\n", scenario.getSentences().get(3));
		assertSentenceIsProcessable(KeywordType.THEN, "then\t\t\t", "a then statement\n", scenario.getSentences().get(4));
		assertSentenceIsProcessable(KeywordType.AND, "and ", "a multiline\nstatement\nwithout any\nempty line\n", scenario.getSentences().get(5));
		assertSentenceIsNotProcessable(
				"\nthis line is ignored because it is preceding by an empty line (even with the words given when then written\n" +
				"And this line is also ignored even if And is a valid keyword\n" +
				"then the final line but also ignored even if then is a valid keyword\n\n", scenario.getSentences().get(6));

		assertSentenceIsProcessable(KeywordType.THEN, "Then ", "the final then statement\n", scenario.getSentences().get(7));
		assertSentenceIsNotProcessable("\n+=======================================+\n", scenario.getSentences().get(8));
	}

	@Test
	public void canGetStoryAssociatedWithAScenario() throws Exception {
		Reader reader = createScenarioReader("scenarios.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;
		Story story = scenario.getStory();

		assertNotNull(story);
		assertSentenceIsProcessable(KeywordType.STORY, "Story: ", "a simple test story\n", story.getTitle());
		assertSentenceIsNotProcessable("\nAs a tester\nI test the text parser\nIn order to check that sentences are correctly parsed\n\n", story.getSentences().get(0));
	}
	
	@Test
	public void canGetEmptyStoryWhenNoStoryDeclaredInFile() throws Exception {
		Reader reader = createScenarioReader("simple.scenario.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;
		Story story = scenario.getStory();

		assertNotNull(story);
		assertSentenceIsProcessable(KeywordType.STORY, "", "", story.getTitle());
	}

	@Test
	public void canParseScenarioFileContainingSeveralStories() throws Exception {
		Reader reader = createScenarioReader("multi.stories.txt");
		TextScenarioParser underTest = new TextScenarioParser(keywordParser, reader);
		
		Scenario scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "scenario: ", "first scenario of first story\n", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "first story\n", scenario.getStory().getTitle());

		scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "scenario: ", "second scenario of first story\n", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "first story\n", scenario.getStory().getTitle());

		scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "scenario: ", "first scenario of second story\n", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "second story\n", scenario.getStory().getTitle());

		scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "scenario: ", "first scenario of third story\n", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "third story\n", scenario.getStory().getTitle());

		scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "scenario: ", "second scenario of third story\n", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "third story\n", scenario.getStory().getTitle());

		scenario = underTest.nextScenario() ;

		assertSentenceIsProcessable(KeywordType.SCENARIO, "", "", scenario.getTitle());
		assertSentenceIsProcessable(KeywordType.GIVEN, "given ", "an empty scenario\n", scenario.getSentences().get(0));
		assertSentenceIsProcessable(KeywordType.STORY, "story: ", "fourth story\n", scenario.getStory().getTitle());
		
		scenario = underTest.nextScenario() ;
		assertNull(scenario);
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
