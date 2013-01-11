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
		public int setup1;
		public int setup2;
		public int teardown1;
		public int teardown2;
		public int step1;
		public int step2;
		public int step3;

		public void methodNotStep() {
		}

		@Setup
		public void setup1() {
			setup1 = 1;
		}

		@Setup
		public void setup2() {
			setup2 = 1;
		}

		@Teardown
		public void teardown1() {
			teardown1 = 1;
		}

		@Teardown
		public void teardown2() {
			teardown2 = 1;
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

		@Given @Step
		public void givenStep() {
			givenStep++;
		}

		@When @Step
		public void whenStep() {
			whenStep++;
		}

		@Then @Step
		public void thenStep() {
			thenStep++;
		}

		@Given @Then @Step
		public void givenThenStep() {
			givenThenStep++;
		}

		@Given @When @Then @Step
		public void givenWhenThenStep() {
			givenWhenThenStep++;
		}
	}
	
	public class InvalidNoStep {
		public void notStep(){
		}
	}

	public static class InvalidStepMethodScope {
		@Step
		private void methodStep() {
		}
	}

	public static class InvalidSetupMethodScope {
		@Setup
		private void setup() {
		}
	}

	public static class InvalidSetupMethodWithParameter {
		@Setup
		public void setup(int param) {
		}
	}

	public static class InvalidTeardownMethodScope {
		@Teardown
		private void teardown() {
		}
	}

	public static class InvalidTeardownMethodWithParameter {
		@Teardown
		public void teardown(int param) {
		}
	}
	
	public static class InheritedInvalidMethodScopeStep extends InvalidStepMethodScope {
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
		InvalidStepMethodScope instance = new InvalidStepMethodScope();
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
	public void canCheckNoStep() throws Exception {
		InvalidNoStep instance = new InvalidNoStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Class must declare or inherit at least one public method with @Step annotation ! Found class instance without step of type: org.giweet.StepObjectHandlerTest$InvalidNoStep");

		underTest.check();
	}

	@Test
	public void canCheckANonPublicStep() throws Exception {
		InvalidStepMethodScope instance = new InvalidStepMethodScope();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Step annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidStepMethodScope.methodStep()");

		underTest.check();
	}

	@Test
	public void canCheckAnInheritedNonPublicStep() throws Exception {
		InheritedInvalidMethodScopeStep instance = new InheritedInvalidMethodScopeStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Step annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidStepMethodScope.methodStep()");

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
		InvalidSetupMethodScope instance = new InvalidSetupMethodScope();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Setup annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidSetupMethodScope.setup()");

		underTest.check();
	}

	@Test
	public void canCheckSetupMehodHasParameter() throws Exception {
		InvalidSetupMethodWithParameter instance = new InvalidSetupMethodWithParameter();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Setup annotation must have no argument! Found method with arguments: public void org.giweet.StepObjectHandlerTest$InvalidSetupMethodWithParameter.setup(int)");

		underTest.check();
	}

	@Test
	public void canCheckTeardownMehodIsNotPublic() throws Exception {
		InvalidTeardownMethodScope instance = new InvalidTeardownMethodScope();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Teardown annotation must be declared public! Found non public method: private void org.giweet.StepObjectHandlerTest$InvalidTeardownMethodScope.teardown()");

		underTest.check();
	}

	@Test
	public void canCheckTeardownMehodHasParameter() throws Exception {
		InvalidTeardownMethodWithParameter instance = new InvalidTeardownMethodWithParameter();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		thrown.expect(InvalidStepException.class);
		thrown.expectMessage("Method with @Teardown annotation must have no argument! Found method with arguments: public void org.giweet.StepObjectHandlerTest$InvalidTeardownMethodWithParameter.teardown(int)");

		underTest.check();
	}

	@Test
	public void canExecuteSetup() throws Exception {
		DummyStep instance = new DummyStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		underTest.setup();
		
		assertEquals(1, instance.setup1);
		assertEquals(1, instance.setup2);
	}

	@Test
	public void canExecuteInheritedSetupMethodFirst() throws Exception {
		DummyStep instance = new DummyStep() {
			@Setup
			public void setup1FromChildClass() {
				this.setup1 = 2;
			}
		};
		
		StepObjectHandler underTest = new StepObjectHandler(instance);

		underTest.setup();
		assertEquals(2, instance.setup1);
		assertEquals(1, instance.setup2);
	}

	@Test
	public void canExecuteTeardown() throws Exception {
		DummyStep instance = new DummyStep();
		StepObjectHandler underTest = new StepObjectHandler(instance);

		underTest.teardown();
		
		assertEquals(1, instance.teardown1);
		assertEquals(1, instance.teardown2);
	}

	@Test
	public void canExecuteInheritedTeardownMethodLast() throws Exception {
		DummyStep instance = new DummyStep() {
			@Teardown
			public void teardown1FromChildClass() {
				this.teardown1 = 2;
			}
		};
		
		StepObjectHandler underTest = new StepObjectHandler(instance);

		underTest.teardown();
		assertEquals(1, instance.teardown1);
		assertEquals(1, instance.teardown2);
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
