package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.Locale;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.giweet.scenario.Scenario;
import org.giweet.scenario.Sentence;
import org.junit.Test;

public class TextScenarioParserTest {

	public class TestScenarioParserHandler implements ScenarioParserHandler {
		private Scenario currentScenario;
		private Scenario scenario;
		private Sentence sentence;

		public void setLocale(Locale locale) {
		}

		public ParseAction startScenario(Locale locale, String keyword, String title) {
			currentScenario = new Scenario();
			currentScenario.setLocale(locale);
			currentScenario.setTitle(title);
			return ParseAction.CONTINUE;
		}

		public ParseAction startStep(KeywordType keywordType, String keyword) {
			sentence = new Sentence();
			sentence.setKeyword(new Keyword(keywordType, keyword));
			return ParseAction.CONTINUE;
		}

		public ParseAction step(String stepValue) {
			sentence.setStep(stepValue);
			return ParseAction.CONTINUE;
		}

		public ParseAction endStep(KeywordType keywordType) {
			currentScenario.getSentences().add(sentence);
			sentence = null;
			return ParseAction.CONTINUE;
		}

		public ParseAction endScenario() {
			scenario = currentScenario;
			return ParseAction.CONTINUE;
		}
		
		public Scenario getScenario() {
			return scenario;
		}
	}

	@Test
	public void canParseStream() throws Exception {
		
		String scenarioAsString = "Scenario : test scenario\n"
				+"Given a given statement\n"
				+"     And an incremented given statement\n"
				+"and an non incremented given statement\n"
				+"when a when statement\n"
				+"Then               a then statement\n"
				+"And a multiline\ntstatement\nwithout any\nempty line\n"
				+"\n"
				+"this line is ignored because it is preceding by an empty line (even with the words given when then written)\n"
				+"And this line is also ignored because the keyword and is forget after an unparsable line\n"
				+"then the final statement";
		
		TextScenarioParser underTest = new TextScenarioParser(new StringReader(scenarioAsString), Locale.UK);
		TestScenarioParserHandler handler = new TestScenarioParserHandler();

		underTest.parse(handler);
		
		Scenario scenario = handler.getScenario();
		assertNotNull(scenario);
		assertEquals("test scenario", scenario.getTitle());
		assertEquals(Locale.UK, scenario.getLocale());

		assertEquals(KeywordType.GIVEN, scenario.getSentences().get(0).getKeyword().getType());
		assertEquals("Given ", scenario.getSentences().get(0).getKeyword().toString());
		assertEquals("a given statement", scenario.getSentences().get(0).getStep());

		assertEquals(KeywordType.GIVEN, scenario.getSentences().get(1).getKeyword().getType());
		assertEquals("     And ", scenario.getSentences().get(1).getKeyword().toString());
		assertEquals("an incremented given statement", scenario.getSentences().get(1).getStep());

		assertEquals(KeywordType.GIVEN, scenario.getSentences().get(2).getKeyword().getType());
		assertEquals("and ", scenario.getSentences().get(2).getKeyword().toString());
		assertEquals("an non incremented given statement", scenario.getSentences().get(2).getStep());
		
		assertEquals(KeywordType.WHEN, scenario.getSentences().get(3).getKeyword().getType());
		assertEquals("when ", scenario.getSentences().get(3).getKeyword().toString());
		assertEquals("a when statement", scenario.getSentences().get(3).getStep());

		assertEquals(KeywordType.THEN, scenario.getSentences().get(4).getKeyword().getType());
		assertEquals("Then               ", scenario.getSentences().get(4).getKeyword().toString());
		assertEquals("a then statement", scenario.getSentences().get(4).getStep());

		assertEquals(KeywordType.THEN, scenario.getSentences().get(5).getKeyword().getType());
		assertEquals("And ", scenario.getSentences().get(5).getKeyword().toString());
		assertEquals("a multiline\ntstatement\nwithout any\nempty line", scenario.getSentences().get(5).getStep());

		assertEquals(KeywordType.THEN, scenario.getSentences().get(6).getKeyword().getType());
		assertEquals("then ", scenario.getSentences().get(6).getKeyword().toString());
		assertEquals("the final statement", scenario.getSentences().get(6).getStep());
	}

}
