package org.giweet.step.tree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.StepDeclarationImpl;
import org.giweet.step.StepInstance;
import org.giweet.step.StepTokenValue;
import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Test;

public class StepTokenTreeTest {
	
	private StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);

	@Test
	public void canSearchRawStepsWithOneStepDeclarationAvailable() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello $the world");
		stepDeclarations.add(stepDeclaration);

		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationNotFoundInStepTokenTree(underTest, "hello");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello $the world", "hello le petit world");
	}

	@Test
	public void canSearchRawStepsWithMultipleStepDeclarationAvailable() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello the");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$hello the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$hello $the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$hello $the");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$hello $the $world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello $the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello $the universe");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello the $world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("foo $bar");
		stepDeclarations.add(stepDeclaration);

		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationNotFoundInStepTokenTree(underTest, "NotExisting");

		assertStepDeclarationFoundInStepTokenTree(underTest, "hello", "hello");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello the world", "hello the world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$hello the world", "Bonjour the world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$hello $the world", "Bonjour le world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello $the world", "hello le world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello the $world", "Hello the monde");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$hello $the $world", "this is correct");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$hello $the $world", "this is also correct");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello $the world", "hello le petit world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello the $world", "hello the petit world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$hello $the world", "bonjour le world");
		assertStepDeclarationFoundInStepTokenTree(underTest, "foo $bar", "foo foo and foo bar");
	}
	
	@Test
	public void canSearchStepsBySearchingThroughDifferentBranchesOfTheTree() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("test searching through the wrong branch");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("test $1 through another branch");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("test $1 through the wrong branch again");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$1 through $2 right branch");
		stepDeclarations.add(stepDeclaration);

		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationFoundInStepTokenTree(underTest, "$1 through $2 right branch", "test searching through the right branch");
		assertStepDeclarationFoundInStepTokenTree(underTest, "test $1 through another branch", "test searching through the right branch through another branch");
	}

	@Test
	public void canSearchAllRawStepsWithOnlyOneDynamicToken() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("$any");
		stepDeclarations.add(stepDeclaration);
		
		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);

		assertStepDeclarationNotFoundInStepTokenTree(underTest, "");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$any", "hello");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$any", "hello the");
		assertStepDeclarationFoundInStepTokenTree(underTest, "$any", "hello the world");
	}

	@Test
	public void canSearchEvenIfInputContainsMeaninglessStepTokens() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello the world");
		stepDeclarations.add(stepDeclaration);
		
		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationFoundInStepTokenTree(underTest, "hello the world", "hello, the world!");
	}

	@Test
	public void canSearchAndReturnStepTokenValue() {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello the world from $1");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$1 $2");
		stepDeclarations.add(stepDeclaration);
		
		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);
		
		SearchResult<StepDeclaration> searchResult = underTest.search(new StepInstance(StepType.GIVEN, "hello the world"));

		StepTokenValue[] stepTokenValues = searchResult.getStepTokenValues();
		assertEquals(0, searchResult.getStepTokenValues().length);

		searchResult = underTest.search(new StepInstance(StepType.GIVEN, "hello the world from giweet"));

		stepTokenValues = searchResult.getStepTokenValues();
		assertEquals(1, stepTokenValues.length);
		assertEquals("1", stepTokenValues[0].getDynamicToken().toString());
		assertEquals(4, stepTokenValues[0].getDynamicTokenPosition());
		assertEquals("giweet", stepTokenValues[0].getValue());
		assertEquals(8, stepTokenValues[0].getStartPosition());
		assertEquals(8, stepTokenValues[0].getEndPosition());
		assertEquals(1, stepTokenValues[0].getTokens().length);
		assertArrayEquals(stepTokenizer.tokenize("giweet"), stepTokenValues[0].getTokens());

		searchResult = underTest.search(new StepInstance(StepType.GIVEN, "goodbye giweet and good luck!"));

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
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("");
		stepDeclarations.add(stepDeclaration);
		
		StepTokenTree<StepDeclaration> underTest = new StepTokenTree<StepDeclaration>(stepDeclarations);

		assertStepDeclarationNotFoundInStepTokenTree(underTest, "");
	}

	private <T extends StepDeclaration> void assertStepDeclarationFoundInStepTokenTree(StepTokenTree<T> tree, String expectedStepDeclarationValue, String actualRawStep) {
		SearchResult<T> searchResult = tree.search(new StepInstance(StepType.GIVEN, actualRawStep));
		assertNotNull(searchResult);
		assertEquals(expectedStepDeclarationValue, searchResult.getStepDeclaration().getValue());
	}

	private <T extends StepDeclaration> void assertStepDeclarationNotFoundInStepTokenTree(StepTokenTree<T> tree, String actualRawStep) {
		SearchResult<T> searchResult = tree.search(new StepInstance(StepType.GIVEN, actualRawStep));
		assertNull(searchResult);
	}
}
