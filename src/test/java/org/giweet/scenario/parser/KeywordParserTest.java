package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.util.Locale;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.junit.Test;

public class KeywordParserTest {

	@Test
	public void canGetStartingKeywordInEnglish() {
		KeywordParser keywordParser = new KeywordParser(Locale.ENGLISH);
		
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword(""));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword("givengiven"));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword("no keyword"));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword(", given"));

		Keyword keyword = keywordParser.getStartingKeyword("Story: ");
		assertKeyword(KeywordType.STORY, "Story: ", keyword);

		keyword = keywordParser.getStartingKeyword("\tStory: ");
		assertKeyword(KeywordType.STORY, "\tStory: ", keyword);

		keyword = keywordParser.getStartingKeyword("Scenario: ");
		assertKeyword(KeywordType.SCENARIO, "Scenario: ", keyword);

		keyword = keywordParser.getStartingKeyword("   scenario   : ");
		assertKeyword(KeywordType.SCENARIO, "   scenario   : ", keyword);

		keyword = keywordParser.getStartingKeyword("given given");
		assertKeyword(KeywordType.GIVEN, "given ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tgiven test");
		assertKeyword(KeywordType.GIVEN, "\t\t\t\tgiven ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0given test");
		assertKeyword(KeywordType.GIVEN, "\u00a0given ", keyword);

		keyword = keywordParser.getStartingKeyword("when when");
		assertKeyword(KeywordType.WHEN, "when ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\twhen test");
		assertKeyword(KeywordType.WHEN, "\t\t\t\twhen ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0when test");
		assertKeyword(KeywordType.WHEN, "\u00a0when ", keyword);

		keyword = keywordParser.getStartingKeyword("then then");
		assertKeyword(KeywordType.THEN, "then ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tthen test");
		assertKeyword(KeywordType.THEN, "\t\t\t\tthen ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0then test");
		assertKeyword(KeywordType.THEN, "\u00a0then ", keyword);

		keyword = keywordParser.getStartingKeyword("and then");
		assertKeyword(KeywordType.AND, "and ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tand test");
		assertKeyword(KeywordType.AND, "\t\t\t\tand ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0and test");
		assertKeyword(KeywordType.AND, "\u00a0and ", keyword);

		keyword = keywordParser.getStartingKeyword("examples:\nexample here");
		assertKeyword(KeywordType.EXAMPLES, "examples:\n", keyword);

		keyword = keywordParser.getStartingKeyword("@test");
		assertKeyword(KeywordType.META, "@", keyword);

		keyword = keywordParser.getStartingKeyword("meta: test");
		assertKeyword(KeywordType.META, "meta: ", keyword);
	}
	
	@Test
	public void canGetStartingKeywordInFrench() {
		KeywordParser keywordParser = new KeywordParser(Locale.FRENCH);
		
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword(""));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword("etet"));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword("aucun mot-clef"));
		assertEquals(Keyword.NO_KEYWORD, keywordParser.getStartingKeyword("given"));

		Keyword keyword = keywordParser.getStartingKeyword("Histoire: ");
		assertKeyword(KeywordType.STORY, "Histoire: ", keyword);

		keyword = keywordParser.getStartingKeyword("\tHistoire  : ");
		assertKeyword(KeywordType.STORY, "\tHistoire  : ", keyword);

		keyword = keywordParser.getStartingKeyword("Scenario: ");
		assertKeyword(KeywordType.SCENARIO, "Scenario: ", keyword);

		keyword = keywordParser.getStartingKeyword("Scénario: ");
		assertKeyword(KeywordType.SCENARIO, "Scénario: ", keyword);

		keyword = keywordParser.getStartingKeyword("   scenario   : ");
		assertKeyword(KeywordType.SCENARIO, "   scenario   : ", keyword);

		keyword = keywordParser.getStartingKeyword("étant donné ");
		assertKeyword(KeywordType.GIVEN, "étant donné ", keyword);

		keyword = keywordParser.getStartingKeyword("etant donne ");
		assertKeyword(KeywordType.GIVEN, "etant donne ", keyword);

		keyword = keywordParser.getStartingKeyword("étant donné que ");
		assertKeyword(KeywordType.GIVEN, "étant donné que ", keyword);

		keyword = keywordParser.getStartingKeyword("étant donné qu'il");
		assertKeyword(KeywordType.GIVEN, "étant donné qu'", keyword);

		keyword = keywordParser.getStartingKeyword("Etant donné ");
		assertKeyword(KeywordType.GIVEN, "Etant donné ", keyword);

		keyword = keywordParser.getStartingKeyword("Etant donnés ");
		assertKeyword(KeywordType.GIVEN, "Etant donnés ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tétant  donné  que\t");
		assertKeyword(KeywordType.GIVEN, "\t\t\t\tétant  donné  que\t", keyword);

		keyword = keywordParser.getStartingKeyword("quand quand");
		assertKeyword(KeywordType.WHEN, "quand ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tquand test");
		assertKeyword(KeywordType.WHEN, "\t\t\t\tquand ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0quand test");
		assertKeyword(KeywordType.WHEN, "\u00a0quand ", keyword);

		keyword = keywordParser.getStartingKeyword("alors alors");
		assertKeyword(KeywordType.THEN, "alors ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\talors test");
		assertKeyword(KeywordType.THEN, "\t\t\t\talors ", keyword);

		keyword = keywordParser.getStartingKeyword("\u00a0alors test");
		assertKeyword(KeywordType.THEN, "\u00a0alors ", keyword);

		keyword = keywordParser.getStartingKeyword("et il");
		assertKeyword(KeywordType.AND, "et ", keyword);

		keyword = keywordParser.getStartingKeyword("\t\t\t\tet test");
		assertKeyword(KeywordType.AND, "\t\t\t\tet ", keyword);

		keyword = keywordParser.getStartingKeyword("Et que ceci");
		assertKeyword(KeywordType.AND, "Et que ", keyword);

		keyword = keywordParser.getStartingKeyword("et qu'il");
		assertKeyword(KeywordType.AND, "et qu'", keyword);

		keyword = keywordParser.getStartingKeyword("exemples:\nmettre les exemples ici");
		assertKeyword(KeywordType.EXAMPLES, "exemples:\n", keyword);

		keyword = keywordParser.getStartingKeyword("@test");
		assertKeyword(KeywordType.META, "@", keyword);

		keyword = keywordParser.getStartingKeyword("meta: test");
		assertKeyword(KeywordType.META, "meta: ", keyword);
	}

	private void assertKeyword(KeywordType expectedType, String expectedStringValue, Keyword keyword) {
		assertNotNull("keyword must not be null", keyword);
		assertEquals(expectedType, keyword.getType());
		assertEquals(expectedStringValue, keyword.toString());
	}

}
