package org.giweet.step.tokenizer.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import junit.framework.Assert;

import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;

public class XmlTestDescriptor {
	
	private String actual;

	private TokenizerStrategy strategy;
	
	private List<XmlTokenDescriptor> expectedTokens;

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	@XmlAttribute
	public TokenizerStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(TokenizerStrategy strategy) {
		this.strategy = strategy;
	}

	@XmlElementWrapper(name="expected")
	@XmlElement(name="token")
	public List<XmlTokenDescriptor> getExpectedTokens() {
		return expectedTokens;
	}

	public void setExpectedTokens(List<XmlTokenDescriptor> expectedTokens) {
		this.expectedTokens = expectedTokens;
	}
	
	public void run(int intialBufferSize) {
		StepTokenizer stepTokenizer = null;
		if (intialBufferSize == 0) {
			stepTokenizer = new StepTokenizer(strategy);
		}
		else {
			stepTokenizer = new StepTokenizer(strategy, intialBufferSize);
		}
		StepToken[] stepTokens = stepTokenizer.tokenize(actual);
		
		String message = "For actual \"" + actual + "\". ";
		
		Assert.assertEquals(message + "Number of expected tokens does not match", expectedTokens.size(), stepTokens.length);
		
		for (int i = 0; i < stepTokens.length; i++) {
			StepToken stepToken = stepTokens[i];
			XmlTokenDescriptor expectedToken = expectedTokens.get(i);
			Assert.assertEquals(message + "Token at position " + i + "does not match", expectedToken.getValue(), stepToken.toString());
			if (expectedToken.isDynamic()) {
				Assert.assertTrue(message + "Token " + expectedToken.getValue() + " is not dynamic", stepToken.isDynamic());
			}
			if (expectedToken.isSeparator()) {
				Assert.assertFalse(message + "Token " + expectedToken.getValue() + " is not separator", stepToken.isMeaningful());
			}
		}
	}
}
