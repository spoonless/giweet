package org.giweet;

import java.util.regex.Pattern;

public class StringUtils {

	private static final Pattern whitespacePattern = Pattern.compile("^[\\p{Space}\\p{Z}\\p{C}]*$");

	private StringUtils() {
	}

	public static String toString(Object[] values) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Object object : values) {
			stringBuilder.append(object);
		}
		return stringBuilder.toString();
	}
	
	public static String toStringWithFirstUpperLetter(String value) {
		String result = value;
		if (value != null && value.length() > 0) {
			char[] charArray = value.toCharArray();
			charArray[0] = Character.toUpperCase(charArray[0]);
			result = new String (charArray); 
		}
		return result;
	}
	
	// TODO to test adding Unicode C category
	public static boolean isWhitespace(String string) {
		return whitespacePattern.matcher(string).matches();
	}
}
