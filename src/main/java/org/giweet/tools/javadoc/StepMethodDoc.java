package org.giweet.tools.javadoc;

import org.giweet.step.StepDeclaration;
import org.giweet.step.StepType;
import org.giweet.step.tokenizer.QuoteTailNotFoundException;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;

public class StepMethodDoc extends StepDeclaration {
	
	private final long id;
	private final MethodDoc methodDoc;
	private final int typeMask;
	
	public StepMethodDoc(long id, MethodDoc methodDoc, String step) throws QuoteTailNotFoundException {
		super(step);
		this.id = id;
		this.methodDoc = methodDoc;
		this.typeMask = createTypeMask();
	}
	
	public long getId() {
		return id;
	}

	public MethodDoc getMethodDoc() {
		return methodDoc;
	}

	private int createTypeMask() {
		int typeMask = 0;
		for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
			String annotationQualifiedName = annotationDesc.annotationType().asClassDoc().qualifiedName();
			if ("org.giweet.annotation.Given".equals(annotationQualifiedName)) {
				typeMask |= 1 << StepType.GIVEN.ordinal();
			}
			if ("org.giweet.annotation.When".equals(annotationQualifiedName)) {
				typeMask |= 1 << StepType.WHEN.ordinal();
			}
			if ("org.giweet.annotation.Then".equals(annotationQualifiedName)) {
				typeMask |= 1 << StepType.THEN.ordinal();
			}
		}
		
		if (typeMask == 0) {
			typeMask = ~typeMask;
		}
		return typeMask;
	}
	
	@Override
	public boolean isOfType(StepType type) {
		return (typeMask & (1 << type.ordinal())) > 0;
	}

}
