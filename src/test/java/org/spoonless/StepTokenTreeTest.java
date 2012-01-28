package org.spoonless;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StepTokenTreeTest {

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
		
		StepDescriptor stepDescriptorFound = null;
		stepDescriptorFound = underTest.find(new StringToken("NotExisting"));
		assertNull(stepDescriptorFound);

		stepDescriptorFound = underTest.find(new StringToken("hello"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("hello"), new StringToken("the"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello the world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("Bonjour"), new StringToken("the"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("$hello the world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("Bonjour"), new StringToken("le"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("$hello $the world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("hello"), new StringToken("le"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello $the world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("Hello"), new StringToken("the"), new StringToken("monde"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello the $world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("this"), new StringToken("is"), new StringToken("correct"));
		assertNotNull(stepDescriptorFound);
		assertEquals("$hello $the $world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("this"), new StringToken("is"), new StringToken("also"), new StringToken("correct"));
		assertNotNull(stepDescriptorFound);
		assertEquals("$hello $the $world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("hello"), new StringToken("le"), new StringToken("petit"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello $the world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("hello"), new StringToken("the"), new StringToken("petit"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("hello the $world", stepDescriptorFound.getValue());

		stepDescriptorFound = underTest.find(new StringToken("bonjour"), new StringToken("le"), new StringToken("world"));
		assertNotNull(stepDescriptorFound);
		assertEquals("$hello $the world", stepDescriptorFound.getValue());
	}
}
