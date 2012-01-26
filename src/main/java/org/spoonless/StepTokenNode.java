package org.spoonless;

import java.util.List;

public class StepTokenNode {
	
	private StepToken stepToken;
	
	private StepDescriptor stepDescriptor;
	
	private List<StepTokenNode> nextNodes;

	public StepDescriptor getStepDescriptor() {
		return stepDescriptor;
	}

	public void setStepDescriptor(StepDescriptor stepDescriptor) {
		this.stepDescriptor = stepDescriptor;
	}

}
