package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.util.Locale;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;
import org.junit.Test;

public class KeywordParserTest {
	
	private KeywordParser keywordParser;

	@Test
	public void canGetKeywordInEnglish() {
		keywordParser = new KeywordParser(Locale.ENGLISH);

		assertNoKeyword("");
		assertNoKeyword("givengiven");
		assertNoKeyword("no given");
		assertNoKeyword(", given");
		assertKeyword(KeywordType.SCENARIO, "Scenario: ", "Scenario: ");
		assertKeyword(KeywordType.SCENARIO, "   scenario   : ", "   scenario   : ");
		assertKeyword(KeywordType.GIVEN, "given ", "given given");
		assertKeyword(KeywordType.GIVEN, "given", "given");
		assertKeyword(KeywordType.GIVEN, "\t\t\t\tgiven ", "\t\t\t\tgiven test");
		assertKeyword(KeywordType.GIVEN, "\u00a0given ", "\u00a0given test");
		assertKeyword(KeywordType.WHEN, "when ", "when when");
		assertKeyword(KeywordType.WHEN, "when", "when");
		assertKeyword(KeywordType.WHEN, "\t\t\t\twhen ", "\t\t\t\twhen test");
		assertKeyword(KeywordType.WHEN, "\u00a0when ", "\u00a0when test");
		assertKeyword(KeywordType.THEN, "then ", "then then");
		assertKeyword(KeywordType.THEN, "then", "then");
		assertKeyword(KeywordType.THEN, "\t\t\t\tthen ", "\t\t\t\tthen test");
		assertKeyword(KeywordType.THEN, "\u00a0then ", "\u00a0then test");
		assertKeyword(KeywordType.AND, "and ", "and then");
		assertKeyword(KeywordType.AND, "and", "and");
		assertKeyword(KeywordType.AND, "\t\t\t\tand ", "\t\t\t\tand test");
		assertKeyword(KeywordType.AND, "\u00a0and ", "\u00a0and test");
		assertKeyword(KeywordType.EXAMPLES, "examples:\n", "examples:\nexample here");
		assertKeyword(KeywordType.EXAMPLES, "examples:", "examples:");
		assertKeyword(KeywordType.META, "@", "@test");
		assertKeyword(KeywordType.META, "meta: ", "meta: test");
	}
	
	@Test
	public void canGetKeywordInFrench() {
		keywordParser = new KeywordParser(Locale.FRENCH);
		
		assertNoKeyword("");
		assertNoKeyword("etet");
		assertNoKeyword("aucun alors");
		assertNoKeyword(", quand");
		assertKeyword(KeywordType.SCENARIO, "Scenario: ", "Scenario: ");
		assertKeyword(KeywordType.SCENARIO, "Scénario: ", "Scénario: ");
		assertKeyword(KeywordType.SCENARIO, "   scenario   : ", "   scenario   : ");
		assertKeyword(KeywordType.GIVEN, "étant donné que ", "étant donné que ");
		assertKeyword(KeywordType.GIVEN, "étant donné que", "étant donné que");
		assertKeyword(KeywordType.GIVEN, "étant donné qu'", "étant donné qu'il");
		assertKeyword(KeywordType.GIVEN, "Etant donnés que", "Etant donnés que");
		assertKeyword(KeywordType.GIVEN, "\t\t\t\tétant  donné  que\t", "\t\t\t\tétant  donné  que\t");
		assertKeyword(KeywordType.WHEN, "quand ", "quand quand");
		assertKeyword(KeywordType.WHEN, "quand", "quand");
		assertKeyword(KeywordType.WHEN, "\t\t\t\tquand ", "\t\t\t\tquand test");
		assertKeyword(KeywordType.WHEN, "\u00a0quand ", "\u00a0quand test");
		assertKeyword(KeywordType.THEN, "alors ", "alors alors");
		assertKeyword(KeywordType.THEN, "alors", "alors");
		assertKeyword(KeywordType.THEN, "\t\t\t\talors ", "\t\t\t\talors test");
		assertKeyword(KeywordType.THEN, "\u00a0alors ", "\u00a0alors test");
		assertKeyword(KeywordType.AND, "et ", "et il");
		assertKeyword(KeywordType.AND, "et", "et");
		assertKeyword(KeywordType.AND, "\t\t\t\tet ", "\t\t\t\tet test");
		assertKeyword(KeywordType.AND, "Et que ", "Et que ceci");
		assertKeyword(KeywordType.AND, "Et que", "Et que");
		assertKeyword(KeywordType.AND, "et qu'", "et qu'il");
		assertKeyword(KeywordType.EXAMPLES, "exemples:\n", "exemples:\nmettre les exemples ici");
		assertKeyword(KeywordType.META, "@", "@test");
		assertKeyword(KeywordType.META, "meta: ", "meta: test");
	}

	
	private void assertKeyword(KeywordType expectedKeywordType, String expectedStringValue, String string) {
		for (KeywordType keywordType : KeywordType.values()) {
			Keyword keyword = keywordParser.getKeyword(string, keywordType);
			if (keywordType != expectedKeywordType) {
				assertNull(keyword);
			}
			else {
				assertNotNull("keyword must not be null", keyword);
				assertEquals(expectedKeywordType, keyword.getType());
				assertEquals(expectedStringValue, keyword.toString());
				
			}
		}
	}

	private void assertNoKeyword(String string) {
		for (KeywordType keywordType : KeywordType.values()) {
			assertNull(keywordParser.getKeyword(string, keywordType));
		}
	}
}
