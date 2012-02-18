package org.giweet;

public class StringUtils {

	public static String toString(Object[] values) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Object object : values) {
			stringBuilder.append(object);
		}
		return stringBuilder.toString();
	}
}
