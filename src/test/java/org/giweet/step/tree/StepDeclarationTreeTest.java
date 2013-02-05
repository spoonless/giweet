package org.giweet.step.tree;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.StepDeclaration;
import org.giweet.step.StepDeclarationImpl;
import org.giweet.step.StepInstance;
import org.giweet.step.StepTokenValue;
import org.giweet.step.StepType;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("StepTokenizer has been reimplemented. Hence StepDeclarationTree must also be reimplemented")
public class StepDeclarationTreeTest {
	
	private final StepTokenizer stepTokenizer = new StepTokenizer(TokenizerStrategy.TOKENIZE_STEP_INSTANCE);

	@Test
	public void canSearchRawStepsWithOneStepDeclarationAvailable() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello $the world");
		stepDeclarations.add(stepDeclaration);

		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationNotFoundInStepDeclarationTree(underTest, "hello");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello $the world", "hello le petit world");
	}

	@Test
	public void canSearchRawStepsWithMultipleStepDeclarationAvailable() throws Exception {
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

		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationNotFoundInStepDeclarationTree(underTest, "NotExisting");

		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello", "hello");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello the world", "hello the world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$hello the world", "Bonjour the world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$hello $the world", "Bonjour le world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello $the world", "hello le world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello the $world", "Hello the monde");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$hello $the $world", "this is correct");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$hello $the $world", "this is also correct");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello $the world", "hello le petit world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello the $world", "hello the petit world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$hello $the world", "bonjour le world");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "foo $bar", "foo foo and foo bar");
	}
	
	@Test
	public void canSearchStepsBySearchingThroughDifferentBranchesOfTheTree() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("test searching through the wrong branch");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("test $1 through another branch");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("test $1 through the wrong branch again");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$1 through $2 right branch");
		stepDeclarations.add(stepDeclaration);

		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$1 through $2 right branch", "test searching through the right branch");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "test $1 through another branch", "test searching through the right branch through another branch");
	}

	@Test
	public void canSearchAllRawStepsWithOnlyOneDynamicToken() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("$any");
		stepDeclarations.add(stepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);

		assertStepDeclarationNotFoundInStepDeclarationTree(underTest, "");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$any", "hello");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$any", "hello the");
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "$any", "hello the world");
	}

	@Test
	public void canSearchEvenIfInputContainsMeaninglessStepTokens() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello the world");
		stepDeclarations.add(stepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		assertStepDeclarationFoundInStepDeclarationTree(underTest, "hello the world", "hello, the world!");
	}

	@Test
	public void canSearchStepDeclarationWithSpecificType() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration givenStepDeclaration = new StepDeclarationImpl("test", StepType.GIVEN);
		stepDeclarations.add(givenStepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		SearchResult<StepDeclaration> searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test"));
		assertEquals(givenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.WHEN, "test"));
		assertNull(searchResult);

		searchResult = underTest.search(new StepInstance(StepType.THEN, "test"));
		assertNull(searchResult);
	}

	@Test
	public void canSearchStepDeclarationWithMultipleTypes() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration givenThenStepDeclaration = new StepDeclarationImpl("test", StepType.GIVEN, StepType.THEN);
		stepDeclarations.add(givenThenStepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		SearchResult<StepDeclaration> searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test"));
		assertEquals(givenThenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.THEN, "test"));
		assertEquals(givenThenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.WHEN, "test"));
		assertNull(searchResult);
	}

	@Test
	public void canSearchStepDeclarationsWithSameValueButDifferentTypes() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration givenStepDeclaration = new StepDeclarationImpl("test test", StepType.GIVEN);
		stepDeclarations.add(givenStepDeclaration);
		StepDeclaration whenStepDeclaration = new StepDeclarationImpl("test test", StepType.WHEN);
		stepDeclarations.add(whenStepDeclaration);
		StepDeclaration thenStepDeclaration = new StepDeclarationImpl("test test", StepType.THEN);
		stepDeclarations.add(thenStepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		SearchResult<StepDeclaration> searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test test"));
		assertEquals(givenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.WHEN, "test test"));
		assertEquals(whenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.THEN, "test test"));
		assertEquals(thenStepDeclaration, searchResult.getStepDeclaration());
	}

	@Test
	public void canSearchStepDeclarationsWithDifferentValuesAndDifferentTypes() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration givenStepDeclaration = new StepDeclarationImpl("test test", StepType.GIVEN);
		stepDeclarations.add(givenStepDeclaration);
		StepDeclaration whenStepDeclaration = new StepDeclarationImpl("test test test", StepType.WHEN);
		stepDeclarations.add(whenStepDeclaration);
		StepDeclaration thenStepDeclaration = new StepDeclarationImpl("test test test test", StepType.THEN);
		stepDeclarations.add(thenStepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
		SearchResult<StepDeclaration> searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test test"));
		assertEquals(givenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.WHEN, "test test"));
		assertNull(searchResult);

		searchResult = underTest.search(new StepInstance(StepType.WHEN, "test test test"));
		assertEquals(whenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.THEN, "test test test"));
		assertNull(searchResult);

		searchResult = underTest.search(new StepInstance(StepType.THEN, "test test test test"));
		assertEquals(thenStepDeclaration, searchResult.getStepDeclaration());

		searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test test test test"));
		assertNull(searchResult);

		searchResult = underTest.search(new StepInstance(StepType.GIVEN, "test test test test test"));
		assertNull(searchResult);
	}

	@Test
	public void canSearchAndReturnStepTokenValue() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("hello the world");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("hello the world from $1");
		stepDeclarations.add(stepDeclaration);
		stepDeclaration = new StepDeclarationImpl("$1 $2");
		stepDeclarations.add(stepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);
		
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
	public void testEmptyStepNotAllowed() throws Exception {
		List<StepDeclaration> stepDeclarations = new ArrayList<StepDeclaration>();
		StepDeclaration stepDeclaration = new StepDeclarationImpl("");
		stepDeclarations.add(stepDeclaration);
		
		StepDeclarationTree<StepDeclaration> underTest = new StepDeclarationTree<StepDeclaration>(stepDeclarations);

		assertStepDeclarationNotFoundInStepDeclarationTree(underTest, "");
	}

	private <T extends StepDeclaration> void assertStepDeclarationFoundInStepDeclarationTree(StepDeclarationTree<T> tree, String expectedStepDeclarationValue, String actualRawStep) throws Exception {
		SearchResult<T> searchResult = tree.search(new StepInstance(StepType.GIVEN, actualRawStep));
		assertNotNull(searchResult);
		assertEquals(expectedStepDeclarationValue, searchResult.getStepDeclaration().getValue());
	}

	private <T extends StepDeclaration> void assertStepDeclarationNotFoundInStepDeclarationTree(StepDeclarationTree<T> tree, String actualRawStep) throws Exception {
		SearchResult<T> searchResult = tree.search(new StepInstance(StepType.GIVEN, actualRawStep));
		assertNull(searchResult);
	}
}
