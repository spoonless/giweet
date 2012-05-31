package org.giweet.tools.javadoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import org.giweet.tools.javadoc.test.JavadocTestStep;
import org.junit.Before;
import org.junit.Test;

import com.sun.javadoc.LanguageVersion;

public class GiweetDocletTest {
	
	private static final String OUPUT_DIR = "target/generated-test-javadoc";
	
	@Before
	public void removeOutputDir () {
		File outputDir = new File(OUPUT_DIR);
		if (outputDir.exists() && outputDir.isDirectory()) {
			for (File file : outputDir.listFiles()) {
				file.delete();
			}
		}
		outputDir.delete();
	}

	@Test
	public void isJava5Doclet() {
		assertEquals(LanguageVersion.JAVA_1_5, GiweetDoclet.languageVersion());
	}

	@Test
	public void canAcceptOptions() {
		assertEquals(2, GiweetDoclet.optionLength(GiweetDoclet.LOCALE_OPTION));

		assertEquals(2, GiweetDoclet.optionLength(GiweetDoclet.OUTPUT_DIR_OPTION));

		assertEquals(2, GiweetDoclet.optionLength(GiweetDoclet.STYLESHEETFILE_OPTION));

		assertEquals(0, GiweetDoclet.optionLength("unknownOption"));
	}
	
	@Test
	public void failIfOutputdirectoryIsFile() {
		assertEquals(1, runJavadoc(Locale.ENGLISH, "pom.xml", "org.giweet.tools.javadoc.test"));
	}
	
	@Test
	public void canGenerateDocumentation() throws IOException {
		assertEquals(0, runJavadoc("org.giweet.tools.javadoc.test"));
		assertFileEquals(new File("src/main/resources/org/giweet/tools/javadoc/giweet.css"), new File(OUPUT_DIR + "/giweet.css"));
		assertFileEquals(new File("src/test/resources/org/giweet/tools/javadoc/expected/steps." + JavadocTestStep.class.getCanonicalName() + ".html"), new File(OUPUT_DIR + "/steps." + JavadocTestStep.class.getCanonicalName() + ".html"));
	}

	@Test
	public void canGenerateDocumentationInFrench() throws IOException {
		assertEquals(0, runJavadoc(Locale.FRENCH, OUPUT_DIR, "org.giweet.tools.javadoc.test"));
		assertFileEquals(new File("src/main/resources/org/giweet/tools/javadoc/giweet.css"), new File(OUPUT_DIR + "/giweet.css"));
		assertFileEquals(new File("src/test/resources/org/giweet/tools/javadoc/expected/steps." + JavadocTestStep.class.getCanonicalName() + "_fr.html"), new File(OUPUT_DIR + "/steps." + JavadocTestStep.class.getCanonicalName() + ".html"));
	}

	@Test
	public void canGenerateEnglishDocumentationForUnknownLocale() throws IOException {
		assertEquals(0, runJavadoc(Locale.PRC, OUPUT_DIR, "org.giweet.tools.javadoc.test"));
		assertFileEquals(new File("src/main/resources/org/giweet/tools/javadoc/giweet.css"), new File(OUPUT_DIR + "/giweet.css"));
		assertFileEquals(new File("src/test/resources/org/giweet/tools/javadoc/expected/steps." + JavadocTestStep.class.getCanonicalName() + ".html"), new File(OUPUT_DIR + "/steps." + JavadocTestStep.class.getCanonicalName() + ".html"));
	}

	private int runJavadoc(String subpackages) {
		return runJavadoc(Locale.ENGLISH, OUPUT_DIR, subpackages);
	}
	
	private void assertFileEquals (File expected, File actual) throws IOException {
		assertEquals("Incorrect file length", expected.length(), actual.length());
		assertFileEquals(new FileInputStream(expected), new FileInputStream(actual));
	}

	
	private void assertFileEquals (InputStream expected, InputStream actual) throws IOException {
		try {
			assertEquals(readFile(expected), readFile(actual));
		}
		finally {
			expected.close();
			actual.close();
		}
	}
	
	private String readFile (InputStream inputStream) throws IOException {
		Reader reader = new InputStreamReader(inputStream, "utf-8");
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char [512];
		int nbRead = 0;
		while ((nbRead = reader.read(buffer)) != -1) {
			stringBuilder.append(buffer, 0, nbRead);
		}
		return stringBuilder.toString().toLowerCase();
	}

	private int runJavadoc(Locale locale, String outputDir, String subpackages) {
		int result = 1;
		try {
			result = com.sun.tools.javadoc.Main.execute(new String[] {"-locale", locale.getLanguage(), "-generationdate", "FAKE DATE", "-d", outputDir, "-sourcepath", "src/test/java", "-doclet", GiweetDoclet.class.getCanonicalName(), "-docletpath", "target/classes/", "-subpackages", subpackages});
		}
		catch (NoClassDefFoundError ncdfe) {
			String message = "This test failed due to the following error: " + ncdfe.toString() + "\n"
					+ "It means that the JDK used to execute the test is not the same as the JDK used to resolve project dependencies.\n"
					+ "It probabely means that you are running this test inside a IDE like Eclipse.\n"
					+ "Please add manually tools.jar to your project dependency as a Java build path library.\n"
					+ "Take care to pick the jar file from the same JDK installation as the one used to run the test.\n"
					+ "Believe me, this problem does not come from Giweet implementation.\" +"
					+ "To fix permanently this problem, use only the command line : \"mvn test\" :)";
			System.err.println(message);
			fail(message);
		}
		return result;
	}

}
