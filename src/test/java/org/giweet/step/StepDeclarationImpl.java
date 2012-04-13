package org.giweet.step;

import java.util.Arrays;
import java.util.List;

import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;

public class StepDeclarationImpl extends StepDeclaration {
	
	private final List<StepType> typeList;

	public StepDeclarationImpl(String value, StepType... types) {
		super(value);
		typeList = Arrays.asList(types);
	}

	@Override
	public boolean isOfType(StepType type) {
		return typeList.isEmpty() || typeList.contains(type);
	}
}