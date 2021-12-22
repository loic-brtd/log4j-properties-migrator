package org.migrator.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Log4j1Properties {

	public Log4j1RootLogger rootLogger = new Log4j1RootLogger();
	private Map<String, Log4j1Logger> loggers = new LinkedHashMap<>();
	private Map<String, Log4j1Appender> appenders = new LinkedHashMap<>();
	public List<NumberedValue> lastComments = new ArrayList<>();

	public Map<String, Log4j1Logger> getLoggers() {
		return loggers;
	}

	public Map<String, Log4j1Appender> getAppenders() {
		return appenders;
	}

	public Log4j1Logger getOrCreateLogger(String name, int lineNumber) {
		if (!loggers.containsKey(name)) {
			Log4j1Logger logger = new Log4j1Logger();
			logger.name = new NumberedValue(name, lineNumber);
			loggers.put(name, logger);
		}
		return loggers.get(name);
	}

	public Log4j1Appender getOrCreateAppender(String name, int lineNumber) {
		if (!appenders.containsKey(name)) {
			Log4j1Appender appender = new Log4j1Appender();
			appender.name = new NumberedValue(name, lineNumber);
			appenders.put(name, appender);
		}
		return appenders.get(name);
	}
}