package org.giweet.scenario.parser;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

public class ResetableBufferedReader extends LineNumberReader {
	
	private String lastReadLine;
	private String nextReadLine;
	
	public ResetableBufferedReader(Reader in) {
		super(in);
	}

	@Override
	public String readLine() throws IOException {
		if (nextReadLine != null) {
			lastReadLine = nextReadLine;
			nextReadLine = null;
		}
		else {
			lastReadLine = super.readLine();
		}
		return lastReadLine;
	}

	public void resetLastLine() {
		nextReadLine = lastReadLine;
	}
	
}