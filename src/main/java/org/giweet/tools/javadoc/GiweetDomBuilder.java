package org.giweet.tools.javadoc;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GiweetDomBuilder {
	
	private static final String NAMESPACE = "http://www.giweet.org/javadoc/1.0";
	
	private final DocumentBuilder documentBuilder;
	private Document document;

	public GiweetDomBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		documentBuilder = builderFactory.newDocumentBuilder();
	}
	
	public Document createDocument(StepClassDoc stepClassDoc) {
		document = documentBuilder.newDocument();
		Element stepClassElement = createElement("stepClass");
		document.appendChild(stepClassElement);
		stepClassElement.appendChild(createElementWithText("type", stepClassDoc.getClassDoc().qualifiedName()));
		stepClassElement.appendChild(createElementWithCDATASection("doc", stepClassDoc.getClassDoc().commentText()));
		Element stepsElement = createElement("steps");
		stepClassElement.appendChild(stepsElement);
		
		for (StepMethodDoc stepMethodDoc : stepClassDoc.getStepMethodDocs()) {
			// TODO check that no step method is overridden by none step method
			// TODO search for step method from parent classes
			// TODO check there is no collision on declared steps (two equivalent steps)
			Element stepElement = createElement("step");
			stepsElement.appendChild(stepElement);
			stepElement.appendChild(createElementWithText("id", Long.toString(stepMethodDoc.getId())));
			stepElement.appendChild(createElementWithText("name", stepMethodDoc.getValue()));
			if (stepMethodDoc.getMethodDoc().commentText().length() > 0) {
				stepElement.appendChild(createElementWithCDATASection("doc", stepMethodDoc.getMethodDoc().commentText()));
			}
			stepElement.appendChild(createElementWithText("class", stepMethodDoc.getMethodDoc().containingClass().qualifiedName()));
			stepElement.appendChild(createElementWithText("method", stepMethodDoc.getMethodDoc().name() + stepMethodDoc.getMethodDoc().signature()));
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
}
