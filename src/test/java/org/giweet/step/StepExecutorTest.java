package org.giweet.step;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.giweet.step.tree.SearchResult;
import org.giweet.step.tree.StepTokenTree;
import org.junit.Test;

public class StepExecutorTest {
	
	public static class DummySteps {
		public String value;

		public void testStepWithParameter(String value) {
			this.value = value;
		}

		public void testStepThrowingNullPointerException()  {
			throw new NullPointerException();
		}
	}

	@Test
	public void canExecute() throws Throwable {
		DummySteps dummySteps = new DummySteps();
		List<StepExecutor> stepExecutors = new ArrayList<StepExecutor>();
		stepExecutors.add(new StepExecutor(DummySteps.class.getDeclaredMethod("testStepWithParameter", String.class), dummySteps, "test with one $1"));
		StepTokenTree<StepExecutor> stepTokenTree = new StepTokenTree<StepExecutor>(stepExecutors);
		
		SearchResult<StepExecutor> searchResult = stepTokenTree.search(new StepTokenizer(false, true).tokenize("test with one nice :) parameter!"));
		
		searchResult.getStepDescriptor().execute(searchResult.getParameterValues());
		
		assertEquals("nice :) parameter", dummySteps.value);
	}

	@Test(expected=NullPointerException.class)
	public void canExecuteAndThrowCorrectException() throws Throwable {
		DummySteps dummySteps = new DummySteps();
		List<StepExecutor> stepExecutors = new ArrayList<StepExecutor>();
		stepExecutors.add(new StepExecutor(DummySteps.class.getDeclaredMethod("testStepThrowingNullPointerException"), dummySteps, "test with exception"));
		StepTokenTree<StepExecutor> stepTokenTree = new StepTokenTree<StepExecutor>(stepExecutors);
		
		SearchResult<StepExecutor> searchResult = stepTokenTree.search(new StepTokenizer(false, true).tokenize("test with exception"));
		
		searchResult.getStepDescriptor().execute(searchResult.getParameterValues());
	}
}
