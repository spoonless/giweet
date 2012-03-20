package org.giweet.tools.javadoc.test;

import org.giweet.annotation.Step;

public abstract class ParentStep {
	
	/**
	 * A step method declared by a superclass 
	 */
	@Step
	public void parentStep() {
		
	}

	@Step("overriden step")
	public void testStepWithoutParameter() {
		
	}
}
