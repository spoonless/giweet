package org.giweet.tools.javadoc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.giweet.step.StepUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class GiweetDomBuilder {
	
	private static final String NAMESPACE = "http://www.giweet.org/javadoc/1.0";
	
	private final DocumentBuilder documentBuilder;
	private Document document;

	public GiweetDomBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = builderFactory.newDocumentBuilder();
	}
	
	public Document createDocument(int position, ClassDoc stepClassDoc) {
		document = documentBuilder.newDocument();
		Element stepClassElement = createElement("stepClass");
		document.appendChild(stepClassElement);
		stepClassElement.appendChild(createElementWithText("type", stepClassDoc.qualifiedName()));
		stepClassElement.appendChild(createElementWithCDATASection("doc", stepClassDoc.commentText()));
		Element stepsElement = createElement("steps");
		stepClassElement.appendChild(stepsElement);
		
		int stepCounter = 0;
		for (MethodDoc methodDoc : stepClassDoc.methods()) {
			AnnotationDesc annotationDesc = getAnnotation(methodDoc.annotations(), "org.giweet.annotation.Step");
			if (annotationDesc != null && isCandidateMethodDoc(methodDoc)) {
				for (String stepName : getMethodStepNames(methodDoc, annotationDesc)) {
					stepCounter++;
					Element stepElement = createElement("step");
					stepsElement.appendChild(stepElement);
					stepElement.appendChild(createElementWithText("id", "class" + position + "step" + stepCounter));
					stepElement.appendChild(createElementWithText("name", stepName));
					if (methodDoc.commentText().length() > 0) {
						stepElement.appendChild(createElementWithCDATASection("doc", methodDoc.commentText()));
					}
					stepElement.appendChild(createElementWithText("method", methodDoc.name() + methodDoc.signature()));
				}
			}
		}

		Document result = document;
		document = null;
		return result;
	}

	private Element createElement(String name) {
		return document.createElementNS(NAMESPACE, name);
	}

	private Element createElementWithText(String name, String text) {
		Element element = createElement(name);
		element.appendChild(document.createTextNode(text));
		return element;
	}

	private Element createElementWithCDATASection(String name, String text) {
		Element element = createElement(name);
		element.appendChild(document.createCDATASection(text));
		return element;
	}

	private boolean isCandidateMethodDoc(MethodDoc methodDoc) {
		return methodDoc.isPublic() && methodDoc.isIncluded();
	}
	
	private AnnotationDesc getAnnotation (AnnotationDesc[] annotationDescs, String qName) {
		for (AnnotationDesc annotationDesc : annotationDescs) {
			boolean isStepAnnotation = annotationDesc.annotationType().asClassDoc().qualifiedName().equals(qName);
			if (isStepAnnotation) {
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
}
