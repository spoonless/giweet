package org.giweet.step.tree;

import org.giweet.step.ParameterValue;
import org.giweet.step.StepDescriptor;

public class SearchResult<T extends StepDescriptor> {
	
	private final T stepDescriptor;
	private final ParameterValue[] parameterValues;
	
	public SearchResult(T stepDescriptor, ParameterValue... parameterValues) {
		this.stepDescriptor = stepDescriptor;
		this.parameterValues = parameterValues;
	}

	public T getStepDescriptor() {
		return stepDescriptor;
	}

	public ParameterValue[] getParameterValues() {
		return parameterValues;
	}

}
