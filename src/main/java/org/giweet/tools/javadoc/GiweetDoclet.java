package org.giweet.tools.javadoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

public class GiweetDoclet {
	
	private static final String GENERATION_DATE = "-generationdate";
	public static final String LOCALE_OPTION = "-locale";
	public static final String OUTPUT_DIR_OPTION = "-d";
	public static final String STYLESHEETFILE_OPTION = "-stylesheetfile";

	private final class ClasspathURIResolver implements URIResolver {
		public Source resolve(String href, String base) throws TransformerException {
			String resourcePath = "/" + this.getClass().getPackage().getName().replace('.', '/') + "/" + href;
			return new StreamSource(this.getClass().getResourceAsStream(resourcePath));
		}
	}

	private final RootDoc rootDoc;
	private final File outputDir;
	private final Locale locale;

	public GiweetDoclet(RootDoc rootDoc) throws IOException {
		this.rootDoc = rootDoc;
		this.outputDir = getOutputDir();
		this.locale = new Locale(getOptionValue(LOCALE_OPTION, Locale.getDefault().getLanguage()));
	}
	
	private File getOutputDir() throws IOException {
		String outputDirPath = getOptionValue(OUTPUT_DIR_OPTION, ".");
		File file = new File(outputDirPath);
		if (! file.exists()) {
			if (! file.mkdirs()) {
				throw new IOException("Cannot create output directory: \"" + outputDirPath + "\"");
			}
		}
		else if (! file.isDirectory()) {
			throw new IOException("Invalid output directory path: \"" + outputDirPath + "\" already exists and is not a directory");
		}
		return file;
	}
	
	private String getOptionValue(String option) {
		return getOptionValue(option, null);
	}

	private String getOptionValue(String option, String defaultValue) {
		String[] optionValues = getOptionValues(option);
		if (optionValues != null && optionValues.length > 0) {
			return optionValues[1];
		}
		return defaultValue;
	}

	
	private String[] getOptionValues(String option) {
		for (String[] optionValue : rootDoc.options()) {
			if (option.equals(optionValue[0])) {
				return optionValue;
			}
		}
		return null;
	}
	
	public void generate() throws Exception {
		List<ClassDoc> classDocs = this.filterClassDocs();
		rootDoc.printNotice("Step classes found: " + classDocs);

		GiweetDomBuilder giweetDomBuilder = new GiweetDomBuilder();
		
		InputStream xslstylsheetStream = getStylesheetStream();
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setURIResolver(new ClasspathURIResolver());
		Transformer transformerHtml = transformerFactory.newTransformer(new StreamSource(xslstylsheetStream));
		
		transformerHtml.setParameter("stylesheetfile", getOptionValue(STYLESHEETFILE_OPTION, "giweet.css"));
		transformerHtml.setParameter("generationDate", getGenerationDate());
		int classCounter = 0;
		for (ClassDoc classDoc : classDocs) {
			classCounter++;
			Document document = giweetDomBuilder.createDocument(classCounter, classDoc);
			transformerHtml.transform(new DOMSource(document), new StreamResult(new File(outputDir, "steps." + classDoc.qualifiedName() + ".html")));
		}
		extractFile("giweet.css");
	}

	private String getGenerationDate() {
		String generationDate = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale).format(new Date());
		return getOptionValue(GENERATION_DATE, generationDate);
	}
	
	private void extractFile (String relativeClasspath) throws IOException {
		InputStream resourceStream = this.getClass().getResourceAsStream(relativeClasspath);
		if (resourceStream == null) {
			rootDoc.printWarning("Cannot found internal resource \"" + relativeClasspath + "\"");
		}
		rootDoc.printNotice("Extracting resource \"" + relativeClasspath + "\"...");
		File outputFile = new File(this.outputDir, relativeClasspath);
		File directory = outputFile.getParentFile();
		if (! directory.isDirectory()) {
			rootDoc.printWarning("Cannot write file \"" + outputFile.getAbsolutePath() + "\"");
			return;
		}
		if (! directory.exists() && ! directory.mkdirs()) {
			rootDoc.printWarning("Cannot create directory \"" + directory.getAbsolutePath() + "\n");
			return;
		}
		
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		try {
			byte[] buffer = new byte[512];
			int nbRead = 0;
			while ((nbRead = resourceStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, nbRead);
			}
		}
		finally {
			fileOutputStream.close();
		}
	}

	private InputStream getStylesheetStream() throws IOException {
		String stylesheetPath = getOptionValue(STYLESHEETFILE_OPTION);
		if (stylesheetPath != null) {
			File stylesheetFile = new File(stylesheetPath);
			if (! stylesheetFile.exists() || ! stylesheetFile.isFile()) {
				rootDoc.printWarning("Unable to find stylsheet file at \"" + stylesheetFile.getAbsolutePath() + "\"");
			}
			else {
				return new FileInputStream(stylesheetFile);
			}
		}
		InputStream xslstylsheetStream = this.getClass().getResourceAsStream("giweet2html_" + locale.getLanguage() + ".xsl");
		if (xslstylsheetStream == null) {
			rootDoc.printWarning("Unable to generate output for locale \"" + locale.getLanguage() + "\". Using default language...");			
			xslstylsheetStream = this.getClass().getResourceAsStream("giweet2html.xsl");
		}
		return xslstylsheetStream;
	}

	
	public List<ClassDoc> filterClassDocs() {
		List<ClassDoc> stepClasses = new ArrayList<ClassDoc>();
		for (ClassDoc classDoc : rootDoc.classes()) {
			if (isStepClassDoc(classDoc)) {
				stepClasses.add(classDoc);
			}
		}
		return stepClasses;
		
	}

	private boolean isStepClassDoc(ClassDoc classDoc) {
		if (isCandidateClassDoc(classDoc)) {
			for (MethodDoc methodDoc : classDoc.methods()) {
				if (isCandidateMethodDoc(methodDoc) && containAnnotation(methodDoc.annotations(), "org.giweet.annotation.Step")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isCandidateClassDoc(ClassDoc classDoc) {
		return classDoc.isOrdinaryClass() && classDoc.isIncluded() && (classDoc.modifierSpecifier() & Modifier.ABSTRACT) == 0;
	}
	
	private boolean isCandidateMethodDoc(MethodDoc methodDoc) {
		return methodDoc.isPublic() && methodDoc.isIncluded();
	}

	private boolean containAnnotation (AnnotationDesc[] annotationDescs, String qName) {
		for (AnnotationDesc annotationDesc : annotationDescs) {
			boolean isStepAnnotation = annotationDesc.annotationType().asClassDoc().qualifiedName().equals(qName);
			if (isStepAnnotation) {
				return true;
			}
		}
		return false;
	}

	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
	
	public static int optionLength(String option) {
		if (option.equals(OUTPUT_DIR_OPTION)) {
			return 2;
		}
		if (option.equals(STYLESHEETFILE_OPTION)) {
			return 2;
		}
		if (option.equals(LOCALE_OPTION)) {
			return 2;
		}
		if (option.equals(GENERATION_DATE)) {
			return 2;
		}
		return 0;
	}

	public static boolean start(RootDoc rootDoc) {
		try {
			GiweetDoclet giweetDoclet = new GiweetDoclet(rootDoc);
			giweetDoclet.generate();
			return true;
		} catch (Exception e) {
			rootDoc.printError(e.getMessage());
		}
		return false;
	}
}
