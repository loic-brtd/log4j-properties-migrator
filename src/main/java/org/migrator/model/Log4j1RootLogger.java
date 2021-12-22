package org.migrator.model;

import java.util.ArrayList;
import java.util.List;

public class Log4j1RootLogger extends Log4j1Element {
	public NumberedValue level;
	public List<NumberedValue> appenderNames = new ArrayList<>();

	@Override
	public String toString() {
		return "Log4j1RootLogger:"
				+ "\n  level: " + level
				+ "\n  appenderNames: " + appenderNames;
	}

}