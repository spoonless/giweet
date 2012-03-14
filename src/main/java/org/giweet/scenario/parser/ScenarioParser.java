package org.giweet.scenario.parser;

import java.io.IOException;

public interface ScenarioParser {
	
	void parse(ScenarioParserHandler handler) throws IOException;

}
