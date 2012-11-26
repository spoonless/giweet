package org.giweet;

import static org.junit.Assert.*;

import java.util.Collection;

import org.giweet.annotation.Given;
import org.giweet.annotation.Setup;
import org.giweet.annotation.Step;
import org.giweet.annotation.Teardown;
import org.giweet.annotation.Then;
import org.giweet.annotation.When;
import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StepObjectHandlerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public static class DummyStep {
		public int step1;
		public int step2;
		public int step3;

		public void methodNotStep() {
		}

		@Step
		public void methodStep_1() {
			step1++;
		}

		@Step("method step 2")
		public void methodStep2() {
			step2++;
		}

		@Step({ "method step 3", "other method step 3" })
		public void methodStep3() {
			step3++;
		}
	}

	public static class DummyStepWithFilterOnStepType {

		public int givenStep;
		public int whenStep;
		public int thenStep;
		public int givenThenStep;
		public int givenWhenThenStep;

		@Step
		@Given
		public void givenStep() {
			givenStep++;
		}

		@Step
		@When
		public void whenStep() {
			whenStep++;
		}

		@Step
		@Then
		public void thenStep() {
			thenStep++;
		}

		@Step
		@Given
		@Then
		public void givenThenStep() {
			givenThenStep++;
		}

		@Step
		@Given
		@When
		@Then
		public void givenWhenThenStep() {
			givenWhenThenStep++;
		}
	}

	public static class InvalidMethodScopeStep {
		@Step
		private void methodStep() {
		}
	}

	public static class InvalidMethodScopeSetup {
		@Setup
		private void setup() {
		}
	}

	public static class InvalidMethodScopeTeardown {
		@Teardown
		private void teardown() {
		}
	}

	public static class InheritedInvalidMethodScopeStep extends InvalidMethodScopeStep {
	}

	public static class GivenAnnotationUsedWithoutStepAnnotation {
		@Given
		public void methodStep() {
		}
	}

	public static class WhenAnnotationUsedWithoutStepAnnotation {
		@When
		public void methodStep() {
		}
	}

	public static class ThenAnnotationUsedWithoutStepAnnotation {
		@Then
		public void methodStep() {
		}
	}

	@Test
	public void canGetMethodStepDeclarations() throws Exception {
		DummyStep instance = new DummyStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		Collection<MethodStepDeclaration> result = underTest.getMethodStepDeclarations();

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

	@Test
	public void canGetMethodStepDeclarationsByIgnoringNonPublicMethod() throws Exception {
		InvalidMethodScopeStep instance = new InvalidMethodScopeStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		Collection<MethodStepDeclaration> result = underTest.getMethodStepDeclarations();

		assertEquals(0, result.size());
	}

	@Test
	public void canGetMethodStepDeclarationsWithSpecificTypes() throws Exception {
		DummyStepWithFilterOnStepType instance = new DummyStepWithFilterOnStepType();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		Collection<MethodStepDeclaration> result = underTest.getMethodStepDeclarations();

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

	@Test
	public void canCheckANonPublicStep() throws Exception {
		InvalidMethodScopeStep instance = new InvalidMethodScopeStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Step annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidMethodScopeStep.methodStep()");

		underTest.check();
	}

	@Test
	public void canCheckAnInheritedNonPublicStep() throws Exception {
		InheritedInvalidMethodScopeStep instance = new InheritedInvalidMethodScopeStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Step annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidMethodScopeStep.methodStep()");

		underTest.check();
	}
	
	@Test
	public void canCheckGivenAnnotationPresentWithoutStepAnnotation() throws Exception {
		GivenAnnotationUsedWithoutStepAnnotation instance = new GivenAnnotationUsedWithoutStepAnnotation();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("@Given annotation must be used with @Step annotation! Found method without @Step annotation: public void org.giweet.StepObjectHandlerTest$GivenAnnotationUsedWithoutStepAnnotation.methodStep()");

		underTest.check();
	}

	@Test
	public void canCheckWhenAnnotationPresentWithoutStepAnnotation() throws Exception {
		WhenAnnotationUsedWithoutStepAnnotation instance = new WhenAnnotationUsedWithoutStepAnnotation();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("@When annotation must be used with @Step annotation! Found method without @Step annotation: public void org.giweet.StepObjectHandlerTest$WhenAnnotationUsedWithoutStepAnnotation.methodStep()");

		underTest.check();
	}

	@Test
	public void canCheckThenAnnotationPresentWithoutStepAnnotation() throws Exception {
		ThenAnnotationUsedWithoutStepAnnotation instance = new ThenAnnotationUsedWithoutStepAnnotation();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("@Then annotation must be used with @Step annotation! Found method without @Step annotation: public void org.giweet.StepObjectHandlerTest$ThenAnnotationUsedWithoutStepAnnotation.methodStep()");

		underTest.check();
	}

	@Test
	public void canCheckSetupMehodIsNotPublic() throws Exception {
		InvalidMethodScopeSetup instance = new InvalidMethodScopeSetup();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Setup annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidMethodScopeSetup.setup()");

		underTest.check();
	}

	@Test
	public void canCheckTeardownMehodIsNotPublic() throws Exception {
		InvalidMethodScopeTeardown instance = new InvalidMethodScopeTeardown();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Teardown annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidMethodScopeTeardown.teardown()");

		underTest.check();
	}

	private void assertIsOfTypes(StepDeclaration stepDeclaration, StepType... types) {
		for (StepType stepType : types) {
			assertTrue("Expected is of type " + stepType, stepDeclaration.isOfType(stepType));
		}
	}

	private void assertIsNotOfTypes(StepDeclaration stepDeclaration, StepType... types) {
		for (StepType stepType : types) {
			assertFalse("Expected is not of type " + stepType, stepDeclaration.isOfType(stepType));
		}
	}

	private MethodStepDeclaration find(Collection<MethodStepDeclaration> methodStepDeclarations, String name) {
		for (MethodStepDeclaration methodStepDeclaration : methodStepDeclarations) {
			if (name.equals(methodStepDeclaration.getValue())) {
				return methodStepDeclaration;
			}
		}
		return null;
	}
}
