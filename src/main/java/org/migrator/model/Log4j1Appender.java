package org.migrator.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Log4j1Appender extends Log4j1Element {

	@Nonnull public final NumberedValue name;
	@Nullable public NumberedValue typeClass;
	@Nullable public NumberedValue file;
	@Nullable public NumberedValue layout;
	@Nullable public NumberedValue layoutConversionPattern;
	@Nullable public NumberedValue maxFileSize;
	@Nullable public NumberedValue maxBackupIndex;
	@Nullable public NumberedValue threshold;
	@Nullable public NumberedValue datePattern;
	@Nullable public NumberedValue append;
	@Nullable public NumberedValue target;
	@Nullable public NumberedValue encoding;

	public Log4j1Appender(@Nonnull NumberedValue name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Log4j1Appender ("
				+ "name: " + name
				+ ", typeClass: " + typeClass
				+ ", file: " + file
				+ ", layout: " + layout
				+ ", layoutConversionPattern: " + layoutConversionPattern
				+ ", maxFileSize: " + maxFileSize
				+ ", maxBackupIndex: " + maxBackupIndex
				+ ", threshold: " + threshold
				+ ", datePattern: " + datePattern
				+ ", append: " + append
				+ ", target: " + target
				+ ", encoding: " + encoding
				+ ")";
	}

}