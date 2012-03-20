package org.giweet.step.tree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.StepTokenValue;
import org.giweet.step.StepDescriptor;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Test;

public class StepTokenTreeTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_SCENARIO);

	@Test
	public void canSearchRawStepsWithOneStepDescriptorAvailable() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello $the world");
		stepDescriptors.add(stepDescriptor);

		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorNotFoundInStepTokenTree(underTest, "hello");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello $the world", "hello le petit world");
	}

	@Test
	public void canSearchRawStepsWithMultipleStepDescriptorAvailable() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello the");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$hello the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$hello $the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$hello $the");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$hello $the $world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello $the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello $the universe");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello the $world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("foo $bar");
		stepDescriptors.add(stepDescriptor);

		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorNotFoundInStepTokenTree(underTest, "NotExisting");

		assertStepDescriptorFoundInStepTokenTree(underTest, "hello", "hello");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the world", "hello the world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$hello the world", "Bonjour the world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$hello $the world", "Bonjour le world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello $the world", "hello le world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the $world", "Hello the monde");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$hello $the $world", "this is correct");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$hello $the $world", "this is also correct");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello $the world", "hello le petit world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the $world", "hello the petit world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$hello $the world", "bonjour le world");
		assertStepDescriptorFoundInStepTokenTree(underTest, "foo $bar", "foo foo and foo bar");
	}
	
	@Test
	public void canSearchStepsBySearchingThroughDifferentBranchesOfTheTree() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("test searching through the wrong branch");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("test $1 through another branch");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("test $1 through the wrong branch again");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$1 through $2 right branch");
		stepDescriptors.add(stepDescriptor);

		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorFoundInStepTokenTree(underTest, "$1 through $2 right branch", "test searching through the right branch");
		assertStepDescriptorFoundInStepTokenTree(underTest, "test $1 through another branch", "test searching through the right branch through another branch");
	}

	@Test
	public void canSearchAllRawStepsWithOnlyOneDynamicToken() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("$any");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);

		assertStepDescriptorNotFoundInStepTokenTree(underTest, "");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello the");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello the world");
	}

	@Test
	public void canSearchEvenIfInputContainsMeaninglessStepTokens() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the world", "hello, the world!");
	}

	@Test
	public void canSearchAndReturnStepTokenValue() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello the world from $1");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$1 $2");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		SearchResult<StepDescriptor> searchResult = underTest.search(stepTokenizer.tokenize("hello the world"));

		StepTokenValue[] stepTokenValues = searchResult.getStepTokenValues();
		assertEquals(0, searchResult.getStepTokenValues().length);

		searchResult = underTest.search(stepTokenizer.tokenize("hello the world from giweet"));

		stepTokenValues = searchResult.getStepTokenValues();
		assertEquals(1, stepTokenValues.length);
		assertEquals("1", stepTokenValues[0].getDynamicToken().toString());
		assertEquals(4, stepTokenValues[0].getDynamicTokenPosition());
		assertEquals("giweet", stepTokenValues[0].getValue());
		assertEquals(8, stepTokenValues[0].getStartPosition());
		assertEquals(8, stepTokenValues[0].getEndPosition());
		assertEquals(1, stepTokenValues[0].getTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("giweet"), stepTokenValues[0].getTokens());

		searchResult = underTest.search(stepTokenizer.tokenize("goodbye giweet and good luck!"));

		stepTokenValues = searchResult.getStepTokenValues();
		assertEquals(2, stepTokenValues.length);
		assertEquals("1", stepTokenValues[0].getDynamicToken().toString());
		assertEquals(0, stepTokenValues[0].getDynamicTokenPosition());
		assertEquals("goodbye", stepTokenValues[0].getValue());
		assertEquals(0, stepTokenValues[0].getStartPosition());
		assertEquals(0, stepTokenValues[0].getEndPosition());
		assertEquals(1, stepTokenValues[0].getTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("goodbye"), stepTokenValues[0].getTokens());

		assertEquals("2", stepTokenValues[1].getDynamicToken().toString());
		assertEquals(1, stepTokenValues[1].getDynamicTokenPosition());
		assertEquals("giweet and good luck", stepTokenValues[1].getValue());
		assertEquals(2, stepTokenValues[1].getStartPosition());
		assertEquals(8, stepTokenValues[1].getEndPosition());
		assertEquals(7, stepTokenValues[1].getTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("giweet and good luck"), stepTokenValues[1].getTokens());
	}

	@Test
	public void testEmptyStepNotAllowed() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);

		assertStepDescriptorNotFoundInStepTokenTree(underTest, "");
	}

	private <T extends StepDescriptor> void assertStepDescriptorFoundInStepTokenTree(StepTokenTree<T> tree, String expectedStepDescriptorValue, String actualRawStep) {
		SearchResult<T> searchResult = tree.search(stepTokenizer.tokenize(actualRawStep));
		assertNotNull(searchResult);
		assertEquals(expectedStepDescriptorValue, searchResult.getStepDescriptor().getValue());
	}

	private <T extends StepDescriptor> void assertStepDescriptorNotFoundInStepTokenTree(StepTokenTree<T> tree, String actualRawStep) {
		SearchResult<T> searchResult = tree.search(stepTokenizer.tokenize(actualRawStep));
		assertNull(searchResult);
	}
}
