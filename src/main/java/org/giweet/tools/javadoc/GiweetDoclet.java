package org.giweet.tools.javadoc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

public class GiweetDoclet {
	
	private static final String GENERATION_DATE = "-generationdate";
	public static final String LOCALE_OPTION = "-locale";
	public static final String OUTPUT_DIR_OPTION = "-d";
	public static final String STYLESHEETFILE_OPTION = "-stylesheetfile";

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
		if (! file.isDirectory()) {
			throw new IOException("Invalid output directory path: \"" + outputDirPath + "\" already exists and is not a directory");
		}
		return file;
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
		List<StepClassDoc> stepClassDocs = this.filterStepClassDocs();
		GiweetDocumentGenerator giweetDocumentGenerator = new GiweetDocumentGenerator(rootDoc, locale);
		giweetDocumentGenerator.setGenerationDate(getGenerationDate());
		giweetDocumentGenerator.setStylesheet(getOptionValue(STYLESHEETFILE_OPTION, "giweet.css"));

		for (StepClassDoc stepClassDoc : stepClassDocs) {
			giweetDocumentGenerator.createDoc(outputDir, stepClassDoc);
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

	public List<StepClassDoc> filterStepClassDocs() {
		List<StepClassDoc> stepClassDocs = new ArrayList<StepClassDoc>();
		int classId = 1;
		for (ClassDoc classDoc : rootDoc.classes()) {
			if (isCandidateClassDoc(classDoc)) {
				StepClassDoc stepClassDoc = new StepClassDoc(classId, classDoc);
				if (stepClassDoc.isStepClassDoc()) {
					rootDoc.printNotice("Step class found: " + classDoc);
					stepClassDocs.add(stepClassDoc);
					classId++;
				}
			}
		}
		return stepClassDocs;
		
	}

	private boolean isCandidateClassDoc(ClassDoc classDoc) {
		return classDoc.isOrdinaryClass() && classDoc.isIncluded() && (classDoc.modifierSpecifier() & Modifier.ABSTRACT) == 0;
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
