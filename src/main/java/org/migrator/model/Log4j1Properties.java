package org.migrator.model;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Log4j1Properties {

	@Nonnull private final Map<String, Log4j1Logger> loggers = new LinkedHashMap<>();
	@Nonnull private final Map<String, Log4j1Appender> appenders = new LinkedHashMap<>();
	@Nonnull public final Log4j1RootLogger rootLogger = new Log4j1RootLogger();
	@Nonnull public final List<NumberedValue> comments = new ArrayList<>();
	@Nonnull public final List<NumberedValue> errors = new ArrayList<>();

	@Nonnull
	public Map<String, Log4j1Logger> getLoggers() {
		return loggers;
	}

	@Nonnull
	public Map<String, Log4j1Appender> getAppenders() {
		return appenders;
	}

	public Log4j1Logger getOrCreateLogger(String name, int lineNumber) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, new Log4j1Logger(new NumberedValue(name, lineNumber)));
		}
		return loggers.get(name);
	}

	public Log4j1Appender getOrCreateAppender(String name, int lineNumber) {
		if (!appenders.containsKey(name)) {
			appenders.put(name, new Log4j1Appender(new NumberedValue(name, lineNumber)));
		}
		return appenders.get(name);
	}

	@Override
	public String toString() {
		return "Log4j1Properties ("
				+ "rootLogger: " + rootLogger
				+ ", loggers: " + loggers
				+ ", appenders: " + appenders
				+ ", comments: " + comments
				+ ", errors: " + errors
				+ ")";
	}
}