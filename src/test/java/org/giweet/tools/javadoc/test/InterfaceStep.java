package org.giweet.tools.javadoc.test;

import org.giweet.annotation.Step;

public interface InterfaceStep {
	
	@Step("step on interface are ignored")
	public void step();

}
