package org.migrator.core;

import org.migrator.model.Log4j1Appender;
import org.migrator.model.Log4j1Properties;
import org.migrator.model.Log4j1RootLogger;
import org.migrator.model.NumberedValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class PropsWriter {

	private static final String FILE_APPENDER = "org.apache.log4j.FileAppender";
	private static final String DAILY_ROLLING_FILE_APPENDER = "org.apache.log4j.DailyRollingFileAppender";
	private static final String ROLLING_FILE_APPENDER = "org.apache.log4j.RollingFileAppender";
	private static final String CONSOLE_APPENDER = "org.apache.log4j.ConsoleAppender";

	private static final String PATTERN_LAYOUT = "org.apache.log4j.PatternLayout";

	/**
	 * Transforms a Log41 properties object into a Log4j2 properties file. Orders the output lines so that they appear in the same order as
	 * their related lines in the original properties file.
	 *
	 * @param properties Log4j1 properties object
	 * @return Lines of the Log4j2 properties file
	 */
	public static List<String> writeLog4j2Properties(Log4j1Properties properties) {
		List<NumberedValue> output = new ArrayList<>();

		// Root logger
		Log4j1RootLogger rootLogger = properties.rootLogger;
		if (rootLogger.level != null) {
			output.add(new NumberedValue("rootLogger.level = " + rootLogger.level.value, rootLogger.level.lineNumber));
		}
		for (NumberedValue appenderName : properties.rootLogger.appenderNames) {
			output.add(new NumberedValue("rootLogger.appenderRef." + nameToIdf(appenderName.value) + ".ref = "
					+ appenderName.value, appenderName.lineNumber));
		}

		// Other loggers
		properties.getLoggers().forEach((loggerName, logger) -> {
			String prefix = "logger." + nameToIdf(loggerName);

			output.add(new NumberedValue(prefix + ".name = " + logger.name.value, logger.name.lineNumber));
			if (logger.level != null) {
				output.add(new NumberedValue(prefix + ".level = " + logger.level.value, logger.level.lineNumber));
			}
			if (logger.additivity != null) {
				output.add(new NumberedValue(prefix + ".additivity = " + logger.additivity.value, logger.additivity.lineNumber));
			}
			for (NumberedValue appenderName : logger.appenderNames) {
				output.add(new NumberedValue(prefix + ".appenderRef." + nameToIdf(appenderName.value)
						+ ".ref = " + appenderName.value, appenderName.lineNumber));
			}
		});

		// Appenders
		properties.getAppenders().forEach((appenderName, appender) -> {
			String prefix = "appender." + nameToIdf(appenderName);

			// Type
			if (appender.typeClass == null) {
				reportMissingAppenderProperty("type", appender, properties);

			} else {
				String appenderType = convertAppenderType(appender.typeClass.value, appender.typeClass.lineNumber);
				output.add(new NumberedValue(prefix + ".type = " + appenderType, appender.typeClass.lineNumber));
				output.add(new NumberedValue(prefix + ".name = " + appenderName, appender.name.lineNumber));

				// Info related to appender type
				switch (appender.typeClass.value) {
				case ROLLING_FILE_APPENDER:
					if (appender.file == null) {
						reportMissingAppenderProperty("file", appender, properties);
					} else {
						output.add(new NumberedValue(prefix + ".fileName = " + appender.file.value, appender.file.lineNumber));
						output.add(new NumberedValue(prefix + ".filePattern = " + appender.file.value + ".%i",
								appender.file.lineNumber));
					}
					break;
				case DAILY_ROLLING_FILE_APPENDER:
					if (appender.file == null) {
						reportMissingAppenderProperty("file", appender, properties);
					} else if (appender.datePattern == null) {
						reportMissingAppenderProperty("datePattern", appender, properties);
					} else {
						output.add(new NumberedValue(prefix + ".fileName = " + appender.file.value, appender.file.lineNumber));
						String dateSuffix = ".%d{" + appender.datePattern.value.replace("'.'", "") + "}";
						output.add(new NumberedValue(prefix + ".filePattern = " + appender.file.value + dateSuffix,
								appender.file.lineNumber));
						output.add(new NumberedValue(prefix + ".policies.type = Policies", appender.datePattern.lineNumber));
						output.add(new NumberedValue(prefix + ".policies.time.type = TimeBasedTriggeringPolicy",
								appender.datePattern.lineNumber));
						output.add(new NumberedValue(prefix + ".policies.time.interval = 1", appender.datePattern.lineNumber));
					}
					break;
				case FILE_APPENDER:
					if (appender.file == null) {
						reportMissingAppenderProperty("file", appender, properties);
					} else {
						output.add(new NumberedValue(prefix + ".fileName = " + appender.file.value, appender.file.lineNumber));
					}
					break;
				}
			}

			// Append
			if (appender.append != null) {
				output.add(new NumberedValue(prefix + ".append = " + appender.append.value, appender.append.lineNumber));
			}

			// Encoding
			if (appender.encoding != null) {
				output.add(new NumberedValue(prefix + ".encoding = " + appender.encoding.value, appender.encoding.lineNumber));
			}

			// Layout
			if (appender.layout != null) {
				String appenderLayoutType = convertAppenderLayoutType(appender.layout.value, appender.layout.lineNumber);
				output.add(new NumberedValue(prefix + ".layout.type = " + appenderLayoutType, appender.layout.lineNumber));

				if (appender.layout.value.equals(PATTERN_LAYOUT)) {
					if (appender.layoutConversionPattern != null) {
						output.add(new NumberedValue(prefix + ".layout.pattern = " + appender.layoutConversionPattern.value,
								appender.layoutConversionPattern.lineNumber));
					} else {
						reportMissingAppenderProperty("layout.conversionPattern", appender, properties);
					}
				}
			}

			// Max file size
			if (appender.maxFileSize != null) {
				output.add(new NumberedValue(prefix + ".policies.type = Policies", appender.maxFileSize.lineNumber));
				output.add(new NumberedValue(prefix + ".policies.size.type = SizeBasedTriggeringPolicy",
						appender.maxFileSize.lineNumber));
				output.add(new NumberedValue(prefix + ".policies.size.size = " + appender.maxFileSize.value,
						appender.maxFileSize.lineNumber));
			}

			// Max backup index
			if (appender.maxBackupIndex != null) {
				output.add(new NumberedValue(prefix + ".strategy.type = DefaultRolloverStrategy",
						appender.maxBackupIndex.lineNumber));
				output.add(new NumberedValue(prefix + ".strategy.max = " + appender.maxBackupIndex.value,
						appender.maxBackupIndex.lineNumber));
			}

			// Threshold
			if (appender.threshold != null) {
				output.add(new NumberedValue(prefix + ".filter.threshold.type = ThresholdFilter", appender.threshold.lineNumber));
				output.add(new NumberedValue(prefix + ".filter.threshold.level = " + appender.threshold.value,
						appender.threshold.lineNumber));
			}
		});

		// Comments
		output.addAll(properties.comments);

		// Parsing errors
		output.addAll(properties.errors);

		return output.stream()
				.sorted(Comparator.comparingInt(NumberedValue::getLineNumber))
				.map(NumberedValue::getValue)
				.collect(Collectors.toList());
	}

	/**
	 * Convert an appender/logger name to a valid identifier. This identifier is only used inside the
	 * properties file to identify which appender/logger the properties are related to.
	 *
	 * @param name Name of an appender/logger (logger names are used inside the calls to Logger.getLogger(loggerName))
	 * @return Identifier for the appender/logger (used in properties like "logger.loggerIdentifier.level = ..."
	 * or "appender.appenderIdentifier.type = ...")
	 */
	private static String nameToIdf(String name) {
		return name.replace(".", "_").toLowerCase();
	}

	private static String convertAppenderLayoutType(@Nullable String layout, int lineNumber) {
		if (layout == null) {
			Util.logError("Missing layout type", lineNumber);
			return format("[%s] Missing layout type: ", Util.ERROR_NAME);
		}
		switch (layout) {
		case PATTERN_LAYOUT:
			return "PatternLayout";
		default:
			Util.logError("Unknown layout type: " + layout, lineNumber);
			return format("[%s] Unknown layout type: %s", Util.ERROR_NAME, layout);
		}
	}

	private static String convertAppenderType(String classType, int lineNumber) {
		if (classType == null) {
			Util.logError("Missing appender type", lineNumber);
			return format("[%s] Missing appender type", Util.ERROR_NAME);
		}
		switch (classType) {
		case CONSOLE_APPENDER:
			return "Console";
		case ROLLING_FILE_APPENDER:
		case DAILY_ROLLING_FILE_APPENDER:
			return "RollingFile";
		case FILE_APPENDER:
			return "File";
		default:
			Util.logError("Unknown appender type: " + classType, lineNumber);
			return format("[%s] Unknown appender type: %s", Util.ERROR_NAME, classType);
		}
	}

	/*
	 * Print error message to stderr and collect error into properties object.
	 */
	private static void reportMissingAppenderProperty(String property, Log4j1Appender appender, Log4j1Properties properties) {
		String message = "Missing property: log4j.appender." + appender.name.value + "." + property;
		Util.logError(message, appender.name.lineNumber);
		properties.errors.add(new NumberedValue(format("# [%s] %s", Util.ERROR_NAME, message), appender.name.lineNumber));
	}

}
