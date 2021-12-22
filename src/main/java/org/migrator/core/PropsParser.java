package org.migrator.core;

import java.util.ArrayList;
import java.util.List;

import org.migrator.model.Log4j1Appender;
import org.migrator.model.Log4j1Logger;
import org.migrator.model.Log4j1Properties;
import org.migrator.model.NumberedValue;

public class PropsParser {

	public static Log4j1Properties parseLog4j1Properties(List<String> lines) throws Exception {
		Log4j1Properties properties = new Log4j1Properties();
		List<NumberedValue> comments = new ArrayList<>();
		int lineNumber = 1;

		for (String line : lines) {
			line = line.trim();

			if (Util.isEmptyOrComment(line)) {
				comments.add(new NumberedValue(line, lineNumber));

			} else {
				if (!line.contains("=")) {
					System.err.println("Property doesn't have '=' sign at line " + lineNumber + " : " + line);
					continue;
				}

				int equalsIndex = line.indexOf("=");
				String key = line.substring(0, equalsIndex).trim();
				String value = line.substring(equalsIndex + 1).trim();

				if (key.equals("log4j.rootLogger")) {
					String[] listOfValues = Util.splitCSV(value, line, lineNumber);
					if (listOfValues == null) {
						continue;
					}
					properties.rootLogger.level = new NumberedValue(listOfValues[0], lineNumber);
					for (int i = 1; i < listOfValues.length; i++) {
						properties.rootLogger.appenderNames.add(new NumberedValue(listOfValues[i], lineNumber));
					}
					properties.rootLogger.comments.addAll(comments);
					comments.clear();

				} else if (key.startsWith("log4j.logger.")) {
					String loggerName = key.substring("log4j.logger.".length());
					Log4j1Logger logger = properties.getOrCreateLogger(loggerName, lineNumber);

					String[] listOfValues = Util.splitCSV(value, line, lineNumber);
					if (listOfValues == null) {
						continue;
					}
					logger.level = new NumberedValue(listOfValues[0], lineNumber);
					for (int i = 1; i < listOfValues.length; i++) {
						logger.appenderNames.add(new NumberedValue(listOfValues[i], lineNumber));
					}
					logger.comments.addAll(comments);
					comments.clear();

				} else if (key.startsWith("log4j.appender.")) {
					// Exemple key : "log4j.appender.asip.layout.ConversionPattern"
					String restOfKey = key.substring("log4j.appender.".length()); // "asip.layout.ConversionPattern"
					String appenderName = restOfKey.contains(".")
							? restOfKey.substring(0, restOfKey.indexOf("."))
							: restOfKey; // "asip"
					String lowerCaseAttribute = restOfKey.contains(".")
							? restOfKey.substring(appenderName.length() + 1).toLowerCase()
							: ""; // "layout.conversionpattern"
					Log4j1Appender appender = properties.getOrCreateAppender(appenderName, lineNumber);

					if (lowerCaseAttribute.equals("")) {
						appender.classType = new NumberedValue(value, lineNumber);
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
					} else {
						System.err.println("Unknown property at line " + lineNumber + " : " + line);
					}
					appender.comments.addAll(comments);
					comments.clear();

				} else if (key.startsWith("log4j.additivity.")) {
					String loggerName = key.substring("log4j.additivity.".length());
					Log4j1Logger logger = properties.getOrCreateLogger(loggerName, lineNumber);
					logger.additivity = new NumberedValue(value, lineNumber);

				} else {
					System.err.println("Unknown property at line " + lineNumber + " : " + line);
				}

			}

			lineNumber++;
		}

		properties.lastComments.addAll(comments);
		comments.clear();

		return properties;
	}

}
