package org.giweet.step;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.Assert;

import org.giweet.step.tokenizer.StepTokenizer;
import org.giweet.step.tokenizer.TokenizerStrategy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class StepTokenizerTestFromXml {

	public static class SaxHandler extends DefaultHandler2 {
		
		private StringBuilder stringBuilder;
		private StepTokenizer stepTokenizer;
		private StepToken[] actualStepTokens;
		private boolean expectedMeaningful;
		private boolean expectedParameter;
		private int expectedTokenIndex;
		private String actual;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if ("test".equals(localName)) {
				stepTokenizer = new StepTokenizer(TokenizerStrategy.valueOf(attributes.getValue("strategy")));
			}
			else if ("actual".equals(localName)) {
				stringBuilder = new StringBuilder();
			}
			else if ("expected".equals(localName)) {
				expectedTokenIndex = 0;
			}
			else if ("token".equals(localName)) {
				expectedMeaningful = Boolean.valueOf(attributes.getValue("meaningful"));
				expectedParameter = Boolean.valueOf(attributes.getValue("parameter"));
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
				this.actualStepTokens = stepTokenizer.tokenize(actual);
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
				Assert.assertEquals(message + "as parameter", expectedParameter, stepToken.isParameter());
				Assert.assertEquals(message + "as meaningful", expectedMeaningful, stepToken.isMeaningful());
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
	
	public static void testFromFile(String xmlResourcePath) throws IOException, SAXException, ParserConfigurationException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new StreamSource(StepTokenizerTestFromXml.class.getResourceAsStream("/org/giweet/step/StepTokenizerTest.xsd")));
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setValidating(true);
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setSchema(schema);
		SAXParser saxParser = saxParserFactory.newSAXParser();
		saxParser.parse(StepTokenizerTestFromXml.class.getResourceAsStream(xmlResourcePath), new SaxHandler());
	}
}
