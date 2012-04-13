package org.giweet.step;

import static org.junit.Assert.assertEquals;

import org.giweet.step.StepDeclaration;
import org.junit.Test;

public class StepDeclarationTest {

	@Test
	public void canCompare() {
		assertStepDeclarationComparison(0, "hello the world", "hello the world");
		assertStepDeclarationComparison(0, "HELLO THE WORLD", "hello the world");
		assertStepDeclarationComparison(0, "hello the world", "    hello   the    world   ");
		assertStepDeclarationComparison(0, "hello $the world", "hello $a world");
		assertStepDeclarationComparison(0, "$1, $2, $3", "$a $b $c (don't forget we ignore some characters)");
		assertStepDeclarationComparison(0, "", "");
		assertStepDeclarationComparison(0, "$", "$ ($ sign is regarded as an argument if alone)");
		assertStepDeclarationComparison(-1, "a", "hello the world");
		assertStepDeclarationComparison(-1, "hello", "hello the world");
		assertStepDeclarationComparison(-1, "hello the world", "hello $the world");
		assertStepDeclarationComparison(-1, "hello the $world", "hello $the world");
		assertStepDeclarationComparison(-1, "hello the $world $world", "hello the $world");
		assertStepDeclarationComparison(-1, "hello the world", "$i");
		assertStepDeclarationComparison(-1, "", "$i");
	}

	private void assertStepDeclarationComparison(int comparisonResult, String step1, String step2) {
		StepDeclaration stepDeclaration1 = new StepDeclarationImpl(step1);
		StepDeclaration stepDeclaration2 = new StepDeclarationImpl(step2);
		assertEquals(comparisonResult, stepDeclaration1.compareTo(stepDeclaration2));
		assertEquals(-comparisonResult, stepDeclaration2.compareTo(stepDeclaration1));
	}
}
