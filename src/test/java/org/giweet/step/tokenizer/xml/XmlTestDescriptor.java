package org.giweet.step.tokenizer.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import junit.framework.Assert;

import org.giweet.StringUtils;
import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.StepTokenizer;

public class XmlTestDescriptor {
	
	private String actual;

	private List<XmlTokenDescriptor> expectedTokens;

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	@XmlElementWrapper(name="expected")
	@XmlElement(name="token")
	public List<XmlTokenDescriptor> getExpectedTokens() {
		return expectedTokens;
	}

	public void setExpectedTokens(List<XmlTokenDescriptor> expectedTokens) {
		this.expectedTokens = expectedTokens;
	}
	
	public void run(StepTokenizer stepTokenizer) throws Exception {
		StepToken[] stepTokens = stepTokenizer.tokenize(actual);
		
		String message = "For actual \"" + actual + "\". ";
		
		Assert.assertEquals(message + "Number of expected tokens does not match", expectedTokens.size(), stepTokens.length);
		
		for (int i = 0; i < stepTokens.length; i++) {
			StepToken stepToken = stepTokens[i];
			XmlTokenDescriptor expectedToken = expectedTokens.get(i);
			Assert.assertEquals(message + "Token value at position " + i + " does not match", expectedToken.getValue(), stepToken.getValue());
			if (expectedToken.isDynamic()) {
				Assert.assertTrue(message + "Token " + expectedToken.getValue() + " is not dynamic", stepToken.isDynamic());
			}
			if (expectedToken.isSeparator()) {
				Assert.assertTrue(message + "Token " + expectedToken.getValue() + " is no separator", stepToken.isSeparator());
			}
		}

		Assert.assertEquals("Rebuilding steps do not match original string", actual, StringUtils.toString(stepTokens));
	}
}
