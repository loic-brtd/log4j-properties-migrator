package org.migrator.core;

import java.util.List;

import org.migrator.model.Log4j1Appender;
import org.migrator.model.Log4j1Logger;
import org.migrator.model.Log4j1Properties;
import org.migrator.model.NumberedValue;

import static java.lang.String.format;
import static org.migrator.core.Util.ERROR_NAME;

public class PropsParser {

	/**
	 * Parse a Log4j1 properties file, collecting information about the rootLogger, loggers and appenders. This retains the line numbers of
	 * the original file as well as the empty and commented lines. Parse errors are printed to stderr and collected in the output object.
	 *
	 * @param lines Lines of the properties file as a list of String
	 * @return Object representing the content of the input properties file
	 */
	public static Log4j1Properties parseLog4j1Properties(List<String> lines) {
		Log4j1Properties properties = new Log4j1Properties();

		int lineNumber = 0;
		for (String line : lines) {
			lineNumber++; // Starts at line 1

			if (Util.isEmptyOrComment(line)) {
				properties.comments.add(new NumberedValue(line, lineNumber));

			} else if (!line.contains("=")) {
				handleParseError("Missing '=' sign: " + line, lineNumber, properties);

			} else {
				int equalsIndex = line.indexOf("=");
				String key = line.substring(0, equalsIndex).trim();
				String value = line.substring(equalsIndex + 1).trim();

				if (key.equals("log4j.rootLogger") || key.equals("log4j.rootCategory")) {
					// "rootCategory" is a deprecated equivalent to "rootLogger" in Log4j1
					String[] listOfValues = Util.splitCSV(value);
					if (listOfValues.length == 0) {
						handleParseError("Empty list of values: " + line, lineNumber, properties);
						continue;
					}
					properties.rootLogger.level = new NumberedValue(listOfValues[0], lineNumber);
					for (int i = 1; i < listOfValues.length; i++) {
						properties.rootLogger.appenderNames.add(new NumberedValue(listOfValues[i], lineNumber));
					}

				} else if (key.startsWith("log4j.logger.")) {
					String loggerName = key.substring("log4j.logger.".length());
					Log4j1Logger logger = properties.getOrCreateLogger(loggerName, lineNumber);

					String[] listOfValues = Util.splitCSV(value);
					if (listOfValues.length == 0) {
						handleParseError("Empty list of values: " + line, lineNumber, properties);
						continue;
					}
					logger.level = new NumberedValue(listOfValues[0], lineNumber);
					for (int i = 1; i < listOfValues.length; i++) {
						logger.appenderNames.add(new NumberedValue(listOfValues[i], lineNumber));
					}

				} else if (key.startsWith("log4j.appender.")) {
					// Example key : "log4j.appender.appenderName.layout.ConversionPattern"
					String restOfKey = key.substring("log4j.appender.".length()); // "asip.layout.ConversionPattern"
					String appenderName = restOfKey.contains(".")
							? restOfKey.substring(0, restOfKey.indexOf("."))
							: restOfKey; // "appenderName"
					String lowerCaseAttribute = restOfKey.contains(".")
							? restOfKey.substring(appenderName.length() + 1).toLowerCase()
							: ""; // "layout.conversionpattern"
					Log4j1Appender appender = properties.getOrCreateAppender(appenderName, lineNumber);

					if (lowerCaseAttribute.equals("")) {
						appender.typeClass = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("layout")) {
						appender.layout = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("layout.conversionpattern")) {
						appender.layoutConversionPattern = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("datepattern")) {
						appender.datePattern = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("file")) {
						appender.file = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("maxfilesize")) {
						appender.maxFileSize = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("maxbackupindex")) {
						appender.maxBackupIndex = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("threshold")) {
						appender.threshold = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("append")) {
						appender.append = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("target")) {
						appender.target = new NumberedValue(value, lineNumber);
					} else if (lowerCaseAttribute.equals("encoding")) {
						appender.encoding = new NumberedValue(value, lineNumber);
					} else {
						handleParseError("Unknown property: " + key, lineNumber, properties);
					}

				} else if (key.startsWith("log4j.additivity.")) {
					String loggerName = key.substring("log4j.additivity.".length());
					Log4j1Logger logger = properties.getOrCreateLogger(loggerName, lineNumber);
					logger.additivity = new NumberedValue(value, lineNumber);

				} else {
					handleParseError("Unknown property: " + key, lineNumber, properties);
				}
			}
		}

		return properties;
	}

	/*
	 * Print error message to stderr and collect error into properties object.
	 */
	private static void handleParseError(String message, int lineNumber, Log4j1Properties properties) {
		Util.logError(message, lineNumber);
		properties.errors.add(new NumberedValue(format("# [%s] %s", ERROR_NAME, message), lineNumber));
	}

}
