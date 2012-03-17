package org.giweet.step;

public class StepUtils {
	
	private StepUtils() {
	}

	public static String getStepFromJavaIdentifier(String name) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0 ; i < name.length() ; i++) {
			char c = name.charAt(i);
			if (i > 0) {
				if (c == '_') {
					builder.append(' ');
					continue;
				}
				else if (c == '$' || Character.isUpperCase(c)) {
					c = Character.toLowerCase(c);
					builder.append(' ');
				}
			}
			builder.append(c);
		}
		return builder.toString();
	}
}
