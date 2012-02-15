package org.giweet.step.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.ParameterValue;
import org.giweet.step.StepDescriptor;
import org.giweet.step.StepTokenizer;
import org.giweet.step.tree.StepTokenTree;
import org.junit.Test;

public class StepTokenTreeTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(false, true);

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
	}

	@Test
	public void canSearchAllRawStepsWithOnlyOneParameterToken() {
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
		this.stepTokenizer = new StepTokenizer(false, true);
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the world", "hello, the world!");
	}

	@Test
	public void canSearchAndReturnParameterValue() {
		this.stepTokenizer = new StepTokenizer(false, true);
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("hello the world from $1");
		stepDescriptors.add(stepDescriptor);
		stepDescriptor = new StepDescriptor("$1 $2");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		SearchResult<StepDescriptor> searchResult = underTest.search(stepTokenizer.tokenize("hello the world"));

		ParameterValue[] parameterValues = searchResult.getParameterValues();
		assertEquals(0, searchResult.getParameterValues().length);

		searchResult = underTest.search(stepTokenizer.tokenize("hello the world from giweet"));

		parameterValues = searchResult.getParameterValues();
		assertEquals(1, parameterValues.length);
		assertEquals("$1", parameterValues[0].getParameterToken().toString());
		assertEquals(4, parameterValues[0].getParameterTokenPosition());
		assertEquals("giweet", parameterValues[0].getValue());
		assertEquals(8, parameterValues[0].getValueTokenStartPosition());
		assertEquals(8, parameterValues[0].getValueTokenEndPosition());
		assertEquals(1, parameterValues[0].getValueTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("giweet"), parameterValues[0].getValueTokens());

		searchResult = underTest.search(stepTokenizer.tokenize("goodbye giweet and good luck!"));

		parameterValues = searchResult.getParameterValues();
		assertEquals(2, parameterValues.length);
		assertEquals("$1", parameterValues[0].getParameterToken().toString());
		assertEquals(0, parameterValues[0].getParameterTokenPosition());
		assertEquals("goodbye", parameterValues[0].getValue());
		assertEquals(0, parameterValues[0].getValueTokenStartPosition());
		assertEquals(0, parameterValues[0].getValueTokenEndPosition());
		assertEquals(1, parameterValues[0].getValueTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("goodbye"), parameterValues[0].getValueTokens());

		assertEquals("$2", parameterValues[1].getParameterToken().toString());
		assertEquals(1, parameterValues[1].getParameterTokenPosition());
		assertEquals("giweet and good luck", parameterValues[1].getValue());
		assertEquals(2, parameterValues[1].getValueTokenStartPosition());
		assertEquals(8, parameterValues[1].getValueTokenEndPosition());
		assertEquals(7, parameterValues[1].getValueTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("giweet and good luck"), parameterValues[1].getValueTokens());
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
