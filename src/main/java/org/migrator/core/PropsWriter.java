package org.migrator.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.migrator.model.Log4j1Properties;
import org.migrator.model.Log4j1RootLogger;
import org.migrator.model.NumberedValue;

public class PropsWriter {

	private static final String FILE_APPENDER = "org.apache.log4j.FileAppender";
	private static final String DAILY_ROLLING_FILE_APPENDER = "org.apache.log4j.DailyRollingFileAppender";
	private static final String ROLLING_FILE_APPENDER = "org.apache.log4j.RollingFileAppender";
	private static final String CONSOLE_APPENDER = "org.apache.log4j.ConsoleAppender";

	private static final String PATTERN_LAYOUT = "org.apache.log4j.PatternLayout";

	public static List<String> writeLog4j2Properties(Log4j1Properties properties) throws Exception {
		List<NumberedValue> output = new ArrayList<>();

		// Root logger
		Log4j1RootLogger rootLogger = properties.rootLogger;
		output.add(new NumberedValue("rootLogger.level = " + rootLogger.level.value, rootLogger.level.lineNumber));
		for (NumberedValue appenderName : properties.rootLogger.appenderNames) {
			output.add(new NumberedValue("rootLogger.appenderRef." + nameToProp(appenderName.value) + ".ref = " + appenderName.value,
					appenderName.lineNumber));
		}

		// Other loggers
		properties.getLoggers().forEach((loggerName, logger) -> {
			String loggerProp = nameToProp(loggerName);
			String loggerPrefix = "logger." + loggerProp;

			output.add(new NumberedValue(loggerPrefix + ".name = " + logger.name.value, logger.name.lineNumber));
			output.add(new NumberedValue(loggerPrefix + ".level = " + logger.level.value, logger.level.lineNumber));
			if (logger.additivity != null) {
				output.add(new NumberedValue(loggerPrefix + ".additivity = " + logger.additivity.value, logger.additivity.lineNumber));
			}
			for (NumberedValue appenderName : logger.appenderNames) {
				output.add(
						new NumberedValue(loggerPrefix + ".appenderRef." + nameToProp(appenderName.value) + ".ref = " + appenderName.value,
								appenderName.lineNumber));
			}
		});

		// Appender
		properties.getAppenders().forEach((appenderName, appender) -> {
			String appenderProp = nameToProp(appenderName);
			String appenderPrefix = "appender." + appenderProp;
			String appenderType = convertAppenderType(appender.classType.value);

			// Basic info
			output.add(new NumberedValue(appenderPrefix + ".type = " + appenderType, appender.classType.lineNumber));
			output.add(new NumberedValue(appenderPrefix + ".name = " + appenderName, appender.name.lineNumber));

			// Info related to appender type
			if (CONSOLE_APPENDER.equals(appender.classType.value)) {
				// Nothing more

			} else if (ROLLING_FILE_APPENDER.equals(appender.classType.value)) {
				if (appender.file != null) {
					output.add(new NumberedValue(appenderPrefix + ".fileName = " + appender.file.value, appender.file.lineNumber));
					output.add(
							new NumberedValue(appenderPrefix + ".filePattern = " + appender.file.value + ".%i", appender.file.lineNumber));
				}

			} else if (DAILY_ROLLING_FILE_APPENDER.equals(appender.classType.value)) {
				output.add(new NumberedValue(appenderPrefix + ".fileName = " + appender.file.value, appender.file.lineNumber));
				if (appender.datePattern == null) {
					System.err.println("Appender named '" + appenderName + "' should have a datePattern");
				} else {
					String dateSuffix = ".%d{" + appender.datePattern.value.replaceAll("'\\.'", "") + "}";
					output.add(
							new NumberedValue(appenderPrefix + ".filePattern = " + appender.file.value + dateSuffix,
									appender.file.lineNumber));
				}
				output.add(new NumberedValue(appenderPrefix + ".policies.type = Policies", appender.datePattern.lineNumber));
				output.add(
						new NumberedValue(appenderPrefix + ".policies.time.type = TimeBasedTriggeringPolicy",
								appender.datePattern.lineNumber));
				output.add(new NumberedValue(appenderPrefix + ".policies.time.interval = 1", appender.datePattern.lineNumber));

			} else if (FILE_APPENDER.equals(appender.classType.value)) {
				output.add(new NumberedValue(appenderPrefix + ".fileName = " + appender.file.value, appender.file.lineNumber));

			}

			// Layout
			if (appender.layout != null) {
				String appenderLayoutType = convertAppenderLayoutType(appender.layout.value);
				output.add(new NumberedValue(appenderPrefix + ".layout.type = " + appenderLayoutType, appender.layout.lineNumber));
				if (PATTERN_LAYOUT.equals(appender.layout.value)) {
					output.add(new NumberedValue(appenderPrefix + ".layout.pattern = " + appender.layoutConversionPattern.value,
							appender.layoutConversionPattern.lineNumber));
				}
			}

			// Policy and strategy
			if (appender.maxFileSize != null) {
				output.add(new NumberedValue(appenderPrefix + ".policies.type = Policies", appender.maxFileSize.lineNumber));
				output.add(
						new NumberedValue(appenderPrefix + ".policies.size.type = SizeBasedTriggeringPolicy",
								appender.maxFileSize.lineNumber));
				output.add(new NumberedValue(appenderPrefix + ".policies.size.size = " + appender.maxFileSize.value,
						appender.maxFileSize.lineNumber));
			}
			if (appender.maxBackupIndex != null) {
				output.add(
						new NumberedValue(appenderPrefix + ".strategy.type = DefaultRolloverStrategy", appender.maxBackupIndex.lineNumber));
				output.add(new NumberedValue(appenderPrefix + ".strategy.max = " + appender.maxBackupIndex.value,
						appender.maxBackupIndex.lineNumber));
			}

			// Threshold
			if (appender.threshold != null) {
				output.add(new NumberedValue(appenderPrefix + ".filter.threshold.type = ThresholdFilter", appender.threshold.lineNumber));
				output.add(
						new NumberedValue(appenderPrefix + ".filter.threshold.level = " + appender.threshold.value,
								appender.threshold.lineNumber));
			}
		});

		// Comments
		for (NumberedValue comment : properties.comments) {
			output.add(comment);
		}

		return output.stream()
				.sorted(Comparator.comparingInt(NumberedValue::getLineNumber))
				.map(NumberedValue::getValue)
				.collect(Collectors.toList());
	}

	private static String nameToProp(String name) {
		return name.replaceAll("\\.", "_").toLowerCase();
	}

	private static String convertAppenderLayoutType(String layout) {
		if (layout == null) {
			return "<unknown> : " + layout;
		}
		switch (layout) {
		case PATTERN_LAYOUT:
			return "PatternLayout";
		default:
			return "<unknown> : " + layout;
		}
	}

	private static String convertAppenderType(String classType) {
		if (classType == null) {
			return "<unknown> : " + classType;
		}
		switch (classType) {
		case CONSOLE_APPENDER:
			return "Console";
		case ROLLING_FILE_APPENDER:
			return "RollingFile";
		case DAILY_ROLLING_FILE_APPENDER:
			return "RollingFile";
		case FILE_APPENDER:
			return "File";
		default:
			return "<unknown> : " + classType;
		}
	}

}
