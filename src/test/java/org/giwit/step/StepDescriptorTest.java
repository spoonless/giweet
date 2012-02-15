package org.giwit.step;

import static org.junit.Assert.assertEquals;

import org.giwit.step.StepDescriptor;
import org.junit.Test;

public class StepDescriptorTest {

	@Test
	public void canCompare() {
		assertStepDescriptorComparison(0, "hello the world", "hello the world");
		assertStepDescriptorComparison(0, "HELLO THE WORLD", "hello the world");
		assertStepDescriptorComparison(0, "hello the world", "    hello   the    world   ");
		assertStepDescriptorComparison(0, "hello $the world", "hello $a world");
		assertStepDescriptorComparison(0, "$1, $2, $3", "$a $b $c (don't forget we ignore some characters)");
		assertStepDescriptorComparison(0, "", "");
		assertStepDescriptorComparison(0, "$", "$ ($ sign is regarded as an argument if alone)");
		assertStepDescriptorComparison(-1, "a", "hello the world");
		assertStepDescriptorComparison(-1, "hello", "hello the world");
		assertStepDescriptorComparison(-1, "hello the world", "hello $the world");
		assertStepDescriptorComparison(-1, "hello the $world", "hello $the world");
		assertStepDescriptorComparison(-1, "hello the $world $world", "hello the $world");
		assertStepDescriptorComparison(-1, "hello the world", "$i");
		assertStepDescriptorComparison(-1, "", "$i");
	}

	private void assertStepDescriptorComparison(int comparisonResult, String step1, String step2) {
		StepDescriptor stepDescriptor1 = new StepDescriptor(step1);
		StepDescriptor stepDescriptor2 = new StepDescriptor(step2);
		assertEquals(comparisonResult, stepDescriptor1.compareTo(stepDescriptor2));
		assertEquals(-comparisonResult, stepDescriptor2.compareTo(stepDescriptor1));
	}
}
