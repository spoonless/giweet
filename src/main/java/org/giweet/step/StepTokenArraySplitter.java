package org.giweet.step;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;

public class StepTokenArraySplitter {
	
	private static final int FIRST_SEPARATOR_TOKEN = 0x1;
	private static final int LAST_SEPARATOR_TOKEN = 0x2;
	private final StepToken[][] listSeparators;
	
	public StepTokenArraySplitter(String... separators) {
		// FIXME should be passed as argument
		StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);
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
					StepToken[] subArray = subArray(stepTokens, startIndex, endIndex - startIndex);
					if (subArray != null) {
						resultAsList.add(subArray);
					}
				}
				i+= nextSeparator.length;
				startIndex = i;
			}
		}
		if (startIndex < stepTokens.length) {
			StepToken[] subArray = subArray(stepTokens, startIndex, stepTokens.length - startIndex);
			if (subArray != null) {
				resultAsList.add(subArray);
			}
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
				int tokenRange = 0;
				if (i == 0) {
					tokenRange = FIRST_SEPARATOR_TOKEN;
				}
				if (i == separator.length - 1) {
					tokenRange |= LAST_SEPARATOR_TOKEN;
				}
				isSeparatorNext = areEquals(separator[i], stepTokens[stepTokenIndex + i], tokenRange);
			}
		}
		return isSeparatorNext;
	}
	
	private static boolean areEquals (StepToken separator, StepToken stepToken, int tokenRange) {
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
				if ((tokenRange & FIRST_SEPARATOR_TOKEN) == 0) {
					for (int i = 0; i < indexOfSeparator && !nonWhitespaceFound; i++) {
						nonWhitespaceFound = !Character.isWhitespace(stepTokenValueAsChar[i]);
					}
				}
				if (! nonWhitespaceFound && (tokenRange & LAST_SEPARATOR_TOKEN) == 0) {
					for (int i = indexOfSeparator + separatorValue.length(); i < stepTokenValueAsChar.length && !nonWhitespaceFound; i++) {
						nonWhitespaceFound = !Character.isWhitespace(stepTokenValueAsChar[i]);
					}
				}
				areEquals = !nonWhitespaceFound;
			}
		}
		return areEquals;
	}

	private static StepToken[] subArray(StepToken[] stepTokens, int offset, int count) {
		int lastIndex = offset + count;

		for (; offset < lastIndex; offset++) {
			if (stepTokens[offset].isMeaningful()) {
				break;
			}
		}
		
		for (; lastIndex > offset; lastIndex--) {
			if (stepTokens[lastIndex - 1].isMeaningful()) {
				break;
			}
		}
		
		StepToken[] subStepTokens = null;
		if (offset < lastIndex) {
			subStepTokens = new StepToken[lastIndex - offset];
			for (int i = 0; i < subStepTokens.length; i++) {
				subStepTokens[i] = stepTokens[i + offset];
			}
		}
		
		return subStepTokens;
	}
}
