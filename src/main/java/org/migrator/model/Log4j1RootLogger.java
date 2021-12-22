package org.migrator.model;

import java.util.ArrayList;
import java.util.List;

public class Log4j1RootLogger extends Log4j1Element {
	public NumberedValue name;
	public NumberedValue level;
	public List<NumberedValue> appenderNames = new ArrayList<>();
}