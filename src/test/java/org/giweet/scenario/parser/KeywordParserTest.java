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
		
		assertNull(keywordParser.getStartingKeyword(""));
		assertNull(keywordParser.getStartingKeyword("givengiven"));
		assertNull(keywordParser.getStartingKeyword("no keyword"));
		assertNull(keywordParser.getStartingKeyword(", given"));

		Keyword keyword = keywordParser.getStartingKeyword("Scenario: ");
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
	}
	
	@Test
	public void canGetStartingKeywordInFrench() {
		KeywordParser keywordParser = new KeywordParser(Locale.FRENCH);
		
		assertNull(keywordParser.getStartingKeyword(""));
		assertNull(keywordParser.getStartingKeyword("etet"));
		assertNull(keywordParser.getStartingKeyword("aucun mot-clef"));
		assertNull(keywordParser.getStartingKeyword("given"));

		Keyword keyword = keywordParser.getStartingKeyword("Scenario: ");
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
	}

	private void assertKeyword(KeywordType expectedType, String expectedStringValue, Keyword keyword) {
		assertNotNull("keyword must not be null", keyword);
		assertEquals(expectedType, keyword.getType());
		assertEquals(expectedStringValue, keyword.toString());
	}

}
