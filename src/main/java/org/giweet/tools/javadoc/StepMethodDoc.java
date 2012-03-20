package org.giweet.tools.javadoc;

import org.giweet.step.StepDescriptor;

import com.sun.javadoc.MethodDoc;

public class StepMethodDoc extends StepDescriptor {
	
	private final long id;
	private final MethodDoc methodDoc;
	
	public StepMethodDoc(long id, MethodDoc methodDoc, String step) {
		super(step);
		this.id = id;
		this.methodDoc = methodDoc;
	}
	
	public long getId() {
		return id;
	}

	public MethodDoc getMethodDoc() {
		return methodDoc;
	}


}
