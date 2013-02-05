package org.giweet.step;

import static org.junit.Assert.*;

import org.junit.Test;

public class StepDeclarationTest {

	@Test
	public void canCompare() throws Exception {
		assertStepDeclarationComparison(0, "hello the world", "hello the world");
		// TODO support automatic trim on step tokens
		//assertStepDeclarationComparison(0, "hello the world", "    hello the world    ");
		assertStepDeclarationComparison(0, "HELLO THE WORLD", "hello the world");
		assertStepDeclarationComparison(0, "hello the world", "hello   the    world");
		assertStepDeclarationComparison(0, "hello {the} world", "hello {0} world");
		assertStepDeclarationComparison(0, "{1}, {2}, {3}", "{aa} , {bb} ,{c.c}");
		assertStepDeclarationComparison(0, "{1}, {2}, {3}", "{1},{2},{3}");
		assertStepDeclarationComparison(0, "", "");
		assertStepDeclarationComparison(0, "{}", "{}");
		assertStepDeclarationComparison(1, "{0}", "{}");
		assertStepDeclarationComparison(-1, "a", "hello the world");
		assertStepDeclarationComparison(-1, "hello", "hello the world");
		assertStepDeclarationComparison(-1, "hello the world", "hello {the} world");
		assertStepDeclarationComparison(-1, "hello the {world}", "hello {the} world");
		assertStepDeclarationComparison(-1, "hello the {world} {world}", "hello the {world}");
		assertStepDeclarationComparison(-1, "hello the world", "{0}");
		assertStepDeclarationComparison(-1, "", "{0}");
	}

	private void assertStepDeclarationComparison(int comparisonResult, String step1, String step2) throws Exception {
		StepDeclaration stepDeclaration1 = new StepDeclarationImpl(step1);
		StepDeclaration stepDeclaration2 = new StepDeclarationImpl(step2);
		assertEquals(comparisonResult, stepDeclaration1.compareTo(stepDeclaration2));
		assertEquals(-comparisonResult, stepDeclaration2.compareTo(stepDeclaration1));
	}
}
