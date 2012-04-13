package org.giweet.step.tree;

import org.giweet.step.StepTokenValue;
import org.giweet.step.StepDeclaration;

public class SearchResult<T extends StepDeclaration> {
	
	private final T stepDeclaration;
	private final StepTokenValue[] stepTokenValues;
	
	public SearchResult(T stepDeclaration, StepTokenValue... stepTokenValues) {
		this.stepDeclaration = stepDeclaration;
		this.stepTokenValues = stepTokenValues;
	}

	public T getStepDeclaration() {
		return stepDeclaration;
	}

	public StepTokenValue[] getStepTokenValues() {
		return stepTokenValues;
	}

}
