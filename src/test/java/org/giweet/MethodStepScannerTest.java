package org.giweet;

import static org.junit.Assert.*;

import java.util.List;

import org.giweet.annotation.Given;
import org.giweet.annotation.Step;
import org.giweet.annotation.Then;
import org.giweet.annotation.When;
import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;
import org.junit.Test;

public class MethodStepScannerTest {
	
	public static class DummyStep {
		
		public int step1;
		public int step2;
		public int step3;
		
		public void methodNotStep(){}

		@Step
		public void methodStep_1(){
			step1++;
		}
		
		@Step("method step 2")
		public void methodStep2(){
			step2++;
		}

		@Step({"method step 3", "other method step 3"})
		public void methodStep3(){
			step3++;
		}
	}

	@Test
	public void canScan() throws Exception {
		MethodStepScanner underTest = new MethodStepScanner();
		DummyStep instance = new DummyStep();
		
		List<MethodStepDeclaration> result = underTest.scan(instance);
		
		assertEquals(4, result.size());
		
		MethodStepDeclaration methodStepDeclaration = find(result, "method step 1");
		assertNotNull(methodStepDeclaration);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN, StepType.THEN);
		methodStepDeclaration.invoke();
		assertEquals(1, instance.step1);

		methodStepDeclaration = find(result, "method step 2");
		assertNotNull(methodStepDeclaration);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN, StepType.THEN);
		methodStepDeclaration.invoke();
		assertEquals(1, instance.step2);

		methodStepDeclaration = find(result, "method step 3");
		assertNotNull(methodStepDeclaration);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN, StepType.THEN);
		methodStepDeclaration.invoke();
		assertEquals(1, instance.step3);

		methodStepDeclaration = find(result, "other method step 3");
		assertNotNull(methodStepDeclaration);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN, StepType.THEN);
		methodStepDeclaration.invoke();
		assertEquals(2, instance.step3);
	}

	public static class InvalidStep {
		@Step
		protected void methodStep(){
		}
	}
	
	@Test(expected=InvalidMethodStepException.class)
	public void cannotScanIfOneStepMethodIsNotPublic() throws Exception {
		MethodStepScanner underTest = new MethodStepScanner();
		
		underTest.scan(new InvalidStep());
		
	}

	public static class DummyStepWithFilterOnStepType {
		
		public int givenStep;
		public int whenStep;
		public int thenStep;
		public int givenThenStep;
		public int givenWhenThenStep;
		
		@Step
		@Given
		public void givenStep(){
			givenStep++;
		}
		
		@Step
		@When
		public void whenStep(){
			whenStep++;
		}

		@Step
		@Then
		public void thenStep(){
			thenStep++;
		}

		@Step
		@Given
		@Then
		public void givenThenStep(){
			givenThenStep++;
		}

		@Step
		@Given
		@When
		@Then
		public void givenWhenThenStep(){
			givenWhenThenStep++;
		}
	}

	@Test
	public void canScanWithSpecificTypes() throws Exception {
		MethodStepScanner underTest = new MethodStepScanner();
		DummyStepWithFilterOnStepType instance = new DummyStepWithFilterOnStepType();
		
		List<MethodStepDeclaration> result = underTest.scan(instance);
		
		assertEquals(5, result.size());
		
		MethodStepDeclaration methodStepDeclaration = find(result, "given step");
		methodStepDeclaration.invoke();
		assertEquals(1, instance.givenStep);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN);
		assertIsNotOfTypes(methodStepDeclaration, StepType.WHEN, StepType.THEN);

		methodStepDeclaration = find(result, "when step");
		methodStepDeclaration.invoke();
		assertEquals(1, instance.whenStep);
		assertIsOfTypes(methodStepDeclaration, StepType.WHEN);
		assertIsNotOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.THEN);

		methodStepDeclaration = find(result, "then step");
		methodStepDeclaration.invoke();
		assertEquals(1, instance.thenStep);
		assertIsOfTypes(methodStepDeclaration, StepType.THEN);
		assertIsNotOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN);

		methodStepDeclaration = find(result, "given then step");
		methodStepDeclaration.invoke();
		assertEquals(1, instance.givenThenStep);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.THEN);
		assertIsNotOfTypes(methodStepDeclaration, StepType.WHEN);

		methodStepDeclaration = find(result, "given when then step");
		methodStepDeclaration.invoke();
		assertEquals(1, instance.givenWhenThenStep);
		assertIsOfTypes(methodStepDeclaration, StepType.GIVEN, StepType.WHEN, StepType.THEN);
	}
	
	private void assertIsOfTypes (StepDeclaration stepDeclaration, StepType...types) {
		for (StepType stepType : types) {
			assertTrue("Expected is of type " + stepType, stepDeclaration.isOfType(stepType));
		}
	}

	private void assertIsNotOfTypes (StepDeclaration stepDeclaration, StepType...types) {
		for (StepType stepType : types) {
			assertFalse("Expected is not of type " + stepType, stepDeclaration.isOfType(stepType));
		}
	}
	
	private MethodStepDeclaration find(List<MethodStepDeclaration> methodStepDeclarations, String name) {
		for (MethodStepDeclaration methodStepDeclaration : methodStepDeclarations) {
			if (name.equals(methodStepDeclaration.getValue())) {
				return methodStepDeclaration;
			}
		}
		return null;
	}
}
