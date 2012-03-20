package org.giweet.tools.javadoc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.sun.javadoc.DocErrorReporter;

public class GiweetDocumentGenerator {
	
	private final class ClasspathURIResolver implements URIResolver {
		public Source resolve(String href, String base) throws TransformerException {
			String resourcePath = "/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + href;
			return new StreamSource(this.getClass().getResourceAsStream(resourcePath));
		}
	}
	
	private final DocErrorReporter docErrorReporter;
	private final Transformer transformerHtml;
	private final GiweetDomBuilder giweetDomBuilder;

	public GiweetDocumentGenerator(DocErrorReporter docErrorReporter, Locale locale) throws IOException, TransformerException, ParserConfigurationException {
		this.docErrorReporter = docErrorReporter;
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setURIResolver(new ClasspathURIResolver());
		InputStream xslstylsheetStream = getStylesheetStream(locale);
		transformerHtml = transformerFactory.newTransformer(new StreamSource(xslstylsheetStream));
		giweetDomBuilder = new GiweetDomBuilder();
	}
	
	public void setStylesheet(String stylesheet) {
		transformerHtml.setParameter("stylesheetfile", stylesheet);
	}
	
	public void setGenerationDate(String generationDate) {
		transformerHtml.setParameter("generationDate", generationDate);
	}
	
	public void createDoc (File outputDir, StepClassDoc stepClassDoc) throws TransformerException {
		Document document = giweetDomBuilder.createDocument(stepClassDoc);
		transformerHtml.transform(new DOMSource(document), new StreamResult(new File(outputDir, "steps." + stepClassDoc.getClassDoc().qualifiedName() + ".html")));
	}

	private InputStream getStylesheetStream(Locale locale) throws IOException {
		InputStream xslstylsheetStream = this.getClass().getResourceAsStream("giweet2html_" + locale.getLanguage() + ".xsl");
		if (xslstylsheetStream == null) {
			docErrorReporter.printWarning("Unable to generate output for locale \"" + locale.getLanguage() + "\". Using default language...");			
			xslstylsheetStream = this.getClass().getResourceAsStream("giweet2html.xsl");
		}
		return xslstylsheetStream;
	}
}
