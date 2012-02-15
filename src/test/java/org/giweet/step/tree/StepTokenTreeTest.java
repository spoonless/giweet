package org.giweet.step.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
		this.stepTokenizer = new StepTokenizer(false, true);
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree<StepDescriptor> underTest = new StepTokenTree<StepDescriptor>(stepDescriptors);
		
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello the world", "hello, the world!");
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
		StepDescriptor stepDescriptorFound = tree.search(stepTokenizer.tokenize(actualRawStep));
		assertNotNull(stepDescriptorFound);
		assertEquals(expectedStepDescriptorValue, stepDescriptorFound.getValue());
	}

	private <T extends StepDescriptor> void assertStepDescriptorNotFoundInStepTokenTree(StepTokenTree<T> tree, String actualRawStep) {
		StepDescriptor stepDescriptorFound = tree.search(stepTokenizer.tokenize(actualRawStep));
		assertNull(stepDescriptorFound);
	}
}
