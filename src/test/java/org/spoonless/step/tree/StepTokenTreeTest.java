package org.spoonless.step.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.spoonless.step.StepDescriptor;
import org.spoonless.step.StepTokenizer;
import org.spoonless.step.tree.StepTokenTree;

public class StepTokenTreeTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(false);

	@Test
	public void canFindRawStepsWithOneStepDescriptorAvailable() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello $the world");
		stepDescriptors.add(stepDescriptor);

		StepTokenTree underTest = new StepTokenTree(stepDescriptors);
		
		assertStepDescriptorNotFoundInStepTokenTree(underTest, "hello");
		assertStepDescriptorFoundInStepTokenTree(underTest, "hello $the world", "hello le petit world");
	}

	@Test
	public void canFindRawStepsWithMultipleStepDescriptorAvailable() {
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

		StepTokenTree underTest = new StepTokenTree(stepDescriptors);
		
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
	public void canFindAllRawStepsWithOnlyOneDynamicToken() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("$any");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree underTest = new StepTokenTree(stepDescriptors);

		assertStepDescriptorNotFoundInStepTokenTree(underTest, "");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello the");
		assertStepDescriptorFoundInStepTokenTree(underTest, "$any", "hello the world");
	}

	@Test
	public void testEmptyStepNotAllowed() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree underTest = new StepTokenTree(stepDescriptors);

		assertStepDescriptorNotFoundInStepTokenTree(underTest, "");
	}

	private void assertStepDescriptorFoundInStepTokenTree(StepTokenTree tree, String expectedStepDescriptorValue, String actualRawStep) {
		StepDescriptor stepDescriptorFound = tree.find(stepTokenizer.tokenize(actualRawStep));
		assertNotNull(stepDescriptorFound);
		assertEquals(expectedStepDescriptorValue, stepDescriptorFound.getValue());
	}

	private void assertStepDescriptorNotFoundInStepTokenTree(StepTokenTree tree, String actualRawStep) {
		StepDescriptor stepDescriptorFound = tree.find(stepTokenizer.tokenize(actualRawStep));
		assertNull(stepDescriptorFound);
	}
}