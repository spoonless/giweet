package org.giweet.step;

import static org.junit.Assert.*;

import org.giweet.StringUtils;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Test;

public class StepTokenArraySplitterTest {
	
	private final StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);

	@Test
	public void canSplit() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter(",");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize(""));
		assertSplitEquals(result);
		
		result = underTest.split(stepTokenizer.tokenize(", , , , , , "));
		assertSplitEquals(result);

		result = underTest.split(stepTokenizer.tokenize("a"));
		assertSplitEquals(result, "a");

		result = underTest.split(stepTokenizer.tokenize("a b"));
		assertSplitEquals(result, "a b");

		result = underTest.split(stepTokenizer.tokenize("a, b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a,"));
		assertSplitEquals(result, "a");

		result = underTest.split(stepTokenizer.tokenize("a, "));
		assertSplitEquals(result, "a");

		result = underTest.split(stepTokenizer.tokenize(", a"));
		assertSplitEquals(result, "a");

		result = underTest.split(stepTokenizer.tokenize(",a"));
		assertSplitEquals(result, "a");

		result = underTest.split(stepTokenizer.tokenize(","));
		assertSplitEquals(result);

		result = underTest.split(stepTokenizer.tokenize("\"\", \"\""));
		assertSplitEquals(result, "\"\"", "\"\"");

		result = underTest.split(stepTokenizer.tokenize("\",\""));
		assertSplitEquals(result, "\",\"");

		result = underTest.split(stepTokenizer.tokenize("a,b"));
		assertSplitEquals(result, "a,b");
	}
	
	@Test
	public void canSplitWithComplexSeparator() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("and also");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a and also b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a and b and also c and also d e f"));
		assertSplitEquals(result, "a and b", "c", "d e f");

		result = underTest.split(stepTokenizer.tokenize("a and, also b"));
		assertSplitEquals(result, "a and, also b");

		result = underTest.split(stepTokenizer.tokenize("a and ,also b"));
		assertSplitEquals(result, "a and ,also b");
	}

	@Test
	public void canSplitWithComplexMixedSeparators() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("and also", "between", "and");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a between b and c and also d"));
		assertSplitEquals(result, "a", "b", "c", "d");

		result = underTest.split(stepTokenizer.tokenize("a between b and c and            also d"));
		assertSplitEquals(result, "a", "b", "c", "d");

		result = underTest.split(stepTokenizer.tokenize("a between b and c \"and also\" d"));
		assertSplitEquals(result, "a", "b", "c \"and also\" d");

		result = underTest.split(stepTokenizer.tokenize("a between b and \"c and also d\""));
		assertSplitEquals(result, "a", "b", "\"c and also d\"");
	}

	@Test
	public void canSplitWithComplexMixedSeparators2() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("$,", "$.");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("1 $, 2 $."));
		assertSplitEquals(result, "1", "2");

		result = underTest.split(stepTokenizer.tokenize("1 $ , 2 $ ."));
		assertSplitEquals(result, "1", "2");

		result = underTest.split(stepTokenizer.tokenize("1 $ !, 2 $."));
		assertSplitEquals(result, "1 $ !, 2");
	}

	@Test
	public void canSplitWithMeaninglessSeparator() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("\t..\t");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a .. b"));
		assertSplitEquals(result, "a .. b");
	}

	@Test
	public void canSplitWithListSeparatorPattern() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("\n- ");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("\n - item1\n  - item2\n- item3"));
		assertSplitEquals(result, "item1", "item2", "item3");

		result = underTest.split(stepTokenizer.tokenize("\n - item1\n -, item2"));
		assertSplitEquals(result, "item1\n -, item2");
	}

	@Test
	public void canSplitWithEmptySeparatorPattern() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter("");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a b"));
		assertSplitEquals(result, "a b");
	}

	@Test
	public void canSplitWithNoSeparatorPattern() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter();
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a b"));
		assertSplitEquals(result, "a b");
	}

	@Test
	public void canSplitWithWhitespaceSeparator() throws Exception {
		StepTokenArraySplitter underTest = new StepTokenArraySplitter(" ");
		
		StepToken[][] result = underTest.split(stepTokenizer.tokenize("a b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a  b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a \t b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a\t b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a \tb"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a, b"));
		assertSplitEquals(result, "a", "b");

		result = underTest.split(stepTokenizer.tokenize("a ,b"));
		assertSplitEquals(result, "a", "b");
	}

	private void assertSplitEquals(StepToken[][] result, String... expected) {
		assertEquals(expected.length, result.length);
		for (int i = 0; i < result.length; i++) {
			assertEquals(expected[i], StringUtils.toString(result[i]));
		}
	}

}
