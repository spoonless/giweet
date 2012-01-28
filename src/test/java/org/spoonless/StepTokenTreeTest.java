package org.spoonless;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StepTokenTreeTest {

	private StepTokenizer stepTokenizer = new StepTokenizer(false);

	@Test
	public void canCreateStepTokenTreeFromOneStepDescriptor() {
		List<StepDescriptor> stepDescriptors = new ArrayList<StepDescriptor>();
		StepDescriptor stepDescriptor = new StepDescriptor("hello the world");
		stepDescriptors.add(stepDescriptor);
		
		StepTokenTree underTest = new StepTokenTree(stepDescriptors);
		
		assertEquals(1, underTest.getStepTokenNodes().size());
		assertNull(underTest.getStepTokenNodes().get(0).getStepDescriptor());

		assertEquals(1, underTest.getStepTokenNodes().get(0).getNextNodes().size());
		assertNull(underTest.getStepTokenNodes().get(0).getNextNodes().get(0).getStepDescriptor());

		assertEquals(1, underTest.getStepTokenNodes().get(0).getNextNodes().get(0).getNextNodes().size());
		assertSame(stepDescriptor, underTest.getStepTokenNodes().get(0).getNextNodes().get(0).getNextNodes().get(0).getStepDescriptor());

		assertNull(underTest.getStepTokenNodes().get(0).getNextNodes().get(0).getNextNodes().get(0).getNextNodes());
	}

	@Test
	public void canFindRawSteps() {
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
		stepDescriptor = new StepDescriptor("hello the $world");
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
