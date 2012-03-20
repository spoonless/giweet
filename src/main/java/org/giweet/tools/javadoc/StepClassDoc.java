package org.giweet.tools.javadoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.giweet.step.StepUtils;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class StepClassDoc {
	
	private final long id;
	private final ClassDoc classDoc;
	private final List<StepMethodDoc> stepMethodDocs;
	
	public StepClassDoc(long id, ClassDoc classDoc) {
		this.id = id;
		this.classDoc = classDoc;
		this.stepMethodDocs = new ArrayList<StepMethodDoc>();
		addStepMethodDocs();
		Collections.sort(this.stepMethodDocs);
	}

	private void addStepMethodDocs() {
		List<MethodDoc> overriddenMethodDocs = new ArrayList<MethodDoc>();
		ClassDoc currentClassDoc = this.classDoc;
		do {
			extractStepMethodDoc(currentClassDoc.methods(), overriddenMethodDocs);
			currentClassDoc = currentClassDoc.superclass();
		} while (currentClassDoc != null);
	}
	
	private void extractStepMethodDoc(MethodDoc[] methodDocs, List<MethodDoc> overriddenMethodDocs) {
		// TODO check that no step method is overridden by none step method
		// TODO search for step method from parent classes
		// TODO check there is no collision on declared steps (two equivalent steps)
		for (MethodDoc methodDoc : methodDocs) {
			MethodDoc overriddenMethodDoc = methodDoc.overriddenMethod();
			if (overriddenMethodDocs != null) {
				overriddenMethodDocs.add(overriddenMethodDoc);
			}
			if (! overriddenMethodDocs.contains(methodDoc)) {
				AnnotationDesc annotationDesc = getAnnotation(methodDoc.annotations(), "org.giweet.annotation.Step");
				if (annotationDesc != null && isCandidateMethodDoc(methodDoc)) {
					for (String stepName : getMethodStepNames(methodDoc, annotationDesc)) {
						stepMethodDocs.add(new StepMethodDoc(this.id * 1000 + stepMethodDocs.size()+1, methodDoc, stepName));												
					}
				}
			}
		}
	}

	private boolean isCandidateMethodDoc(MethodDoc methodDoc) {
		return methodDoc.isPublic();
	}
	
	private AnnotationDesc getAnnotation (AnnotationDesc[] annotationDescs, String annotationQualifiedName) {
		for (AnnotationDesc annotationDesc : annotationDescs) {
			boolean isAnnotation = annotationDesc.annotationType().asClassDoc().qualifiedName().equals(annotationQualifiedName);
			if (isAnnotation) {
				return annotationDesc;
			}
		}
		return null;
	}
	
	private List<String> getMethodStepNames(MethodDoc methodDoc, AnnotationDesc annotationDesc) {
		List<String> methodStepNames = new ArrayList<String>();
		for (ElementValuePair elementValuePair : annotationDesc.elementValues()) {
			if (elementValuePair.element().name().equals("value")) {
				for (AnnotationValue annotationValue : (AnnotationValue[]) elementValuePair.value().value()) {
					methodStepNames.add((String) annotationValue.value());
				}
			}
		}
		if (methodStepNames.isEmpty()) {
			methodStepNames.add(StepUtils.getStepFromJavaIdentifier(methodDoc.name()));
		}
		return methodStepNames;
	}
	
	public ClassDoc getClassDoc() {
		return classDoc;
	}
	
	public List<StepMethodDoc> getStepMethodDocs() {
		return stepMethodDocs;
	}
	
	public boolean isStepClassDoc() {
		return ! this.stepMethodDocs.isEmpty();
	}

}
