package org.giweet.scenario.parser;

import java.util.ArrayList;
import java.util.List;
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
	private final List<KeywordResource> keywordResources;
	private final Locale locale;
	
	public KeywordParser(Locale locale) {
		this.locale = locale;
		bundle = ResourceBundle.getBundle("org/giweet/i18n/keywords", locale);
		this.keywordResources = new ArrayList<KeywordParser.KeywordResource>();
		for (KeywordType keywordType : KeywordType.getParseableKeywordTypes()) {
			this.keywordResources.add(createKeywordResource(keywordType));			
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
			patterns[i] = Pattern.compile("^[\\p{Z}\t]*" + patternsAsString[i], Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		}
		return patterns;
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
	
	public Keyword getStartingKeyword(String line) {
		Keyword keyword = null;
		for (KeywordResource keywordResource : keywordResources) {
			keyword = extractKeyword(keywordResource, line);
			if (keyword != null) {
				break;
			}
		}
		return keyword == null ? Keyword.NO_KEYWORD : keyword;
	}

}
