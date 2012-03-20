package org.giweet.tools.javadoc.test;

import org.giweet.annotation.Step;

/**
 * A test step class for javadoc generation.<br/>
 * You can even use HTML tags : <strong>&nbsp;HTML fragment example&nbsp;</strong>
 *
 * @author Spoonless
 * @version 1.0
 * @since 2012
 */
public class TestJavadocStep extends ParentStep {
	
	@Override
	@Step("test step without parameter")
	public void testStepWithoutParameter() {
		
	}

	/**
	 * This is a method step matching several step values
	 */
	@Step({"test step without parameter value 1", "test step without parameter value 2"})
	public void testStepWithoutParameterAndMultipleValues() {
		
	}
		
	/**
	 * This step name is generated from the java method name
	 */
	@Step
	public void testStep_generated_from_the_javaMethodName() {
		
	}

	public void ignorePublicMethodWithoutStepAnnotation() {

	}

	@Step()
	protected void ignorePrivateMethod() {

	}
}
