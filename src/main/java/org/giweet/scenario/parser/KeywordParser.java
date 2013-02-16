package org.giweet.scenario.parser;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.giweet.scenario.Keyword;
import org.giweet.scenario.KeywordType;

public class KeywordParser {
	
	private static class KeywordResource {
		public KeywordType type;
		public Pattern[] patterns;
		public String acceptedValue;
	}
	
	private final ResourceBundle bundle;
	private final KeywordResource[] keywordResources;
	private final Locale locale;
	
	public KeywordParser(Locale locale) {
		this.locale = locale;
		bundle = ResourceBundle.getBundle("org/giweet/i18n/keywords", locale);
		KeywordType[] keywordTypeValues = KeywordType.values();
		this.keywordResources = new KeywordResource[keywordTypeValues.length];
		for (KeywordType keywordType : keywordTypeValues) {
			this.keywordResources[keywordType.ordinal()] = createKeywordResource(keywordType);	
		}
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	private KeywordResource createKeywordResource(KeywordType keywordType) {
		KeywordResource keywordResource = new KeywordResource();
		String keywordResourcePrefix = "keyword." + keywordType.name().toLowerCase();
		keywordResource.acceptedValue = bundle.getString(keywordResourcePrefix + ".accepted");
		keywordResource.patterns = createKeywordPatterns(bundle.getString(keywordResourcePrefix + ".pattern").split("\\s*,\\s+"));
		keywordResource.type = keywordType;
		return keywordResource;
	}
	
	private Pattern[] createKeywordPatterns(String...patternsAsString) {
		Pattern[] patterns = new Pattern[patternsAsString.length];
		for (int i = 0; i < patternsAsString.length; i++) {
			patterns[i] = Pattern.compile(completePattern(patternsAsString[i]), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		return patterns;
	}

	private String completePattern(String pattern) {
		return "^[\\p{Z}\t]*" + pattern.replace(" ", "[\\p{Space}\\p{Z}]");
	}

	private Keyword extractKeyword(KeywordResource keywordResource, String line) {
		Keyword keyword = null;
		String extractedKeyword = extractKeywordString(keywordResource.patterns, line);
		if (extractedKeyword != null) {
			keyword = new Keyword(keywordResource.type, extractedKeyword);
		}
		return keyword;
	}

	private String extractKeywordString(Pattern[] givenPatterns, String line) {
		for (Pattern pattern : givenPatterns) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.lookingAt() && matcher.start() == 0) {
				return line.substring(0, matcher.end());
			}
		}
		return null;
	}
	
	public Keyword getKeyword(String line, KeywordType... keywordTypes) {
		Keyword keyword = null;
		for (KeywordType keywordType : keywordTypes) {
			keyword = extractKeyword(keywordResources[keywordType.ordinal()], line);
			if (keyword != null) {
				break;
			}
		}
		return keyword;
	}

}
