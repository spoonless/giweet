package org.giweet.step.tokenizer.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="suite")
public class XmlSuiteDescriptor {
	
	private List<XmlTestDescriptor> tests;

	@XmlElement(name="test")
	public List<XmlTestDescriptor> getTests() {
		return tests;
	}

	public void setTests(List<XmlTestDescriptor> testDescriptors) {
		this.tests = testDescriptors;
	}
	
	public void run() {
		run(0);
	}
	
	public void run(int intialBufferSize) {
		for (XmlTestDescriptor test : tests) {
			test.run(intialBufferSize);
		}
	}
}
