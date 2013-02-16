package org.giweet.scenario.parser;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Test;

public class ResetableBufferedReaderTest {

	@Test
	public void canRevertLastLine() throws Exception {
		ResetableBufferedReader underTest = new ResetableBufferedReader(new StringReader("line1\nline2"));
		
		assertEquals(0, underTest.getLineNumber());
		assertEquals("line1", underTest.readLine());
		assertEquals(1, underTest.getLineNumber());
		underTest.resetLastLine();
		assertEquals("line1", underTest.readLine());
		assertEquals(1, underTest.getLineNumber());
		assertEquals("line2", underTest.readLine());
		assertEquals(2, underTest.getLineNumber());
		underTest.resetLastLine();
		assertEquals("line2", underTest.readLine());
		assertEquals(2, underTest.getLineNumber());
		assertNull(underTest.readLine());
		assertEquals(2, underTest.getLineNumber());
		
		underTest.close();
	}

}
