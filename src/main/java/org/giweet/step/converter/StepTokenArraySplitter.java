package org.giweet.step.converter;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.StepToken;
import org.giweet.step.StepTokenizer;

public class StepTokenArraySplitter {
	
	private final StepToken[][] listSeparators;
	
	// FIXME what to do if one separator is "" (means stepToken[] of size 0)
	public StepTokenArraySplitter(String... separators) {
		StepTokenizer stepTokenizer = new StepTokenizer(false, true);
		listSeparators = new StepToken[separators.length][];
		for (int i = 0; i < separators.length; i++) {
			listSeparators[i] = stepTokenizer.tokenize(separators[i]);
		}
	}
	
	public StepToken[][] split(StepToken... stepTokens) {
		int startIndex = 0;
		List<StepToken[]> resultAsList = new ArrayList<StepToken[]>();
		for (int i = 0; i < stepTokens.length;) {
			StepToken[] nextSeparator = isSeparatorNext(stepTokens, i);
			if (nextSeparator == null) {
				i++;
			}
			else {
				int endIndex = i;
				if (startIndex < endIndex) {
					resultAsList.add(subArray(stepTokens, startIndex, endIndex - startIndex));
				}
				i+= nextSeparator.length;
				startIndex = i;
			}
		}
		if (startIndex < stepTokens.length) {
			resultAsList.add(subArray(stepTokens, startIndex, stepTokens.length - startIndex));
		}
		return resultAsList.toArray(new StepToken[resultAsList.size()][]);
	}
	
	private StepToken[] isSeparatorNext(StepToken[] stepTokens, int stepTokenIndex) {
		for (int i = 0; i < listSeparators.length; i++) {
			StepToken[] separator = listSeparators[i];
			if (separator.length > 0 && isSeparatorNext(separator, stepTokens, stepTokenIndex)) {
				return separator;
			}
		}
		return null;
	}

	private static boolean isSeparatorNext(StepToken[] separator, StepToken[] stepTokens, int stepTokenIndex) {
		boolean isSeparatorNext = false;
		if (separator.length <= stepTokens.length - stepTokenIndex) {
			isSeparatorNext = true;
			for (int i = 0; isSeparatorNext && i < separator.length; i++) {
				isSeparatorNext = areEquals(separator[i], stepTokens[stepTokenIndex + i]);
			}
		}
		return isSeparatorNext;
	}
	
	private static boolean areEquals (StepToken separator, StepToken stepToken) {
		boolean areEquals = false;
		if (separator.isMeaningful()) {
			if (stepToken.isMeaningful()) {
				areEquals = separator.equals(stepToken);
			}
		}
		else if (!stepToken.isMeaningful()){
			String stepTokenValue = stepToken.toString();
			String separatorValue = separator.toString();
			int indexOfSeparator = stepTokenValue.indexOf(separatorValue);
			if (indexOfSeparator >= 0) {
				boolean nonWhitespaceFound = false;
				char[] stepTokenValueAsChar = stepTokenValue.toCharArray();
				for (int i = 0; i < indexOfSeparator && !nonWhitespaceFound; i++) {
					nonWhitespaceFound = !Character.isWhitespace(stepTokenValueAsChar[i]);
				}
				if (! nonWhitespaceFound) {
					for (int i = indexOfSeparator + separatorValue.length(); i < stepTokenValueAsChar.length && !nonWhitespaceFound; i++) {
						nonWhitespaceFound = !Character.isWhitespace(stepTokenValueAsChar[i]);
					}
					areEquals = !nonWhitespaceFound;
				}
			}
		}
		return areEquals;
	}

	private static StepToken[] subArray(StepToken[] stepTokens, int offset, int count) {
		StepToken[] subStepTokens = new StepToken[count];
		for (int i = 0; i < subStepTokens.length; i++) {
			subStepTokens[i] = stepTokens[i + offset];
		}
		return subStepTokens;
	}
}
