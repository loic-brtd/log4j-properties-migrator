package org.migrator.model;

import java.util.ArrayList;
import java.util.List;

public class Log4j1Logger extends Log4j1Element {
	public NumberedValue name;
	public NumberedValue level;
	public NumberedValue additivity;
	public List<NumberedValue> appenderNames = new ArrayList<>();

	@Override
	public String toString() {
		return "Log4j1Logger:"
				+ "\n  name: " + name
				+ "\n  level: " + level
				+ "\n  additivity: " + additivity
				+ "\n  appenderNames: " + appenderNames;
	}

}