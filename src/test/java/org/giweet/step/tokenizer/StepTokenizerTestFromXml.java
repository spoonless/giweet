package org.giweet.step.tokenizer;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.Assert;

import org.giweet.step.StepToken;
import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class StepTokenizerTestFromXml {

	private SAXParserFactory saxParserFactory;

	private static class SaxHandler extends DefaultHandler2 {
		
		private final int tokenizerBufferSize;
		private StringBuilder stringBuilder;
		private StepTokenizer stepTokenizer;
		private StepToken[] actualStepTokens;
		private boolean expectedMeaningful;
		private boolean expectedDynamicToken;
		private int expectedTokenIndex;
		private String actual;
		
		public SaxHandler(int tokenizerBufferSize) {
			this.tokenizerBufferSize = tokenizerBufferSize;
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ("test".equals(localName)) {
				if (this.tokenizerBufferSize == 0) {
					stepTokenizer = new StepTokenizer(TokenizerStrategy.valueOf(attributes.getValue("strategy")));
				}
				else {
					stepTokenizer = new StepTokenizer(TokenizerStrategy.valueOf(attributes.getValue("strategy")), this.tokenizerBufferSize);
				}
			}
			else if ("actual".equals(localName)) {
				stringBuilder = new StringBuilder();
			}
			else if ("expected".equals(localName)) {
				expectedTokenIndex = 0;
			}
			else if ("token".equals(localName)) {
				expectedMeaningful = Boolean.valueOf(attributes.getValue("meaningful"));
				expectedDynamicToken = Boolean.valueOf(attributes.getValue("dynamic"));
				stringBuilder = new StringBuilder();
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("test".equals(localName)) {
				actual = null;
				actualStepTokens = null;
				stepTokenizer = null;
			}
			else if ("actual".equals(localName)) {
				actual = stringBuilder.toString();
				if (tokenizerBufferSize == 0) {
					this.actualStepTokens = stepTokenizer.tokenize(actual);
				}
				else {
					try {
						this.actualStepTokens = stepTokenizer.tokenize(new StringReader(actual));
					} catch (IOException e) {
						throw new SAXException(e);
					}
				}
				stringBuilder = null;
			}
			else if ("expected".equals(localName)) {
				Assert.assertEquals("For actual \"" + actual + "\", invalid number of tokens", expectedTokenIndex, actualStepTokens.length);
			}
			else if ("token".equals(localName)) {
				String expectedContent = stringBuilder.toString();
				String message = "For actual \"" + actual + "\", stepToken " + expectedTokenIndex + " ";
				Assert.assertTrue(message + "unexpected, only " + actualStepTokens.length + " tokens were parsed" , expectedTokenIndex < actualStepTokens.length);
				StepToken stepToken = actualStepTokens[expectedTokenIndex];
				Assert.assertEquals(message + "content", expectedContent, stepToken.toString());
				Assert.assertEquals(message + "as dynamic step token", expectedDynamicToken, stepToken.isDynamic());
				Assert.assertEquals(message + "as meaningful step token", expectedMeaningful, stepToken.isMeaningful());
				expectedTokenIndex++;
				stringBuilder = null;
			}
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (stringBuilder != null) {
				stringBuilder.append(ch, start, length);
			}
		}
	}
	
	public StepTokenizerTestFromXml() throws SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new StreamSource(StepTokenizerTestFromXml.class.getResourceAsStream("StepTokenizerTest.xsd")));
		saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setSchema(schema);
	}
	
	public void testFromFile(String xmlResourcePath, int tokenizerBufferSize) throws IOException, SAXException, ParserConfigurationException {
		SAXParser saxParser = saxParserFactory.newSAXParser();
		saxParser.parse(StepTokenizerTestFromXml.class.getResourceAsStream(xmlResourcePath), new SaxHandler(tokenizerBufferSize));
	}
}
