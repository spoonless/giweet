package org.giweet.step.tree;

import org.giweet.step.StepTokenValue;
import org.giweet.step.StepDescriptor;

public class SearchResult<T extends StepDescriptor> {
	
	private final T stepDescriptor;
	private final StepTokenValue[] stepTokenValues;
	
	public SearchResult(T stepDescriptor, StepTokenValue... stepTokenValues) {
		this.stepDescriptor = stepDescriptor;
		this.stepTokenValues = stepTokenValues;
	}

	public T getStepDescriptor() {
		return stepDescriptor;
	}

	public StepTokenValue[] getStepTokenValues() {
		return stepTokenValues;
	}

}
