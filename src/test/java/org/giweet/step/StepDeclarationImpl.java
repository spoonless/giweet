package org.giweet.step;

import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;

public class StepDeclarationImpl extends StepDeclaration {
	public StepDeclarationImpl(String value) {
		super(value);
	}

	@Override
	public boolean isOfType(StepType type) {
		return true;
	}
}