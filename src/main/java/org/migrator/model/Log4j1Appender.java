package org.migrator.model;

public class Log4j1Appender extends Log4j1Element {
	public NumberedValue name;
	public NumberedValue typeClass;
	public NumberedValue file;
	public NumberedValue layout;
	public NumberedValue layoutConversionPattern;
	public NumberedValue maxFileSize;
	public NumberedValue maxBackupIndex;
	public NumberedValue threshold;
	public NumberedValue datePattern;
	public NumberedValue append;
	public NumberedValue target;
	public NumberedValue encoding;

	@Override
	public String toString() {
		return "Log4j1Appender:"
				+ "\n  name: " + name
				+ "\n  typeClass: " + typeClass
				+ "\n  file: " + file
				+ "\n  layout: " + layout
				+ "\n  layoutConversionPattern: " + layoutConversionPattern
				+ "\n  maxFileSize: " + maxFileSize
				+ "\n  maxBackupIndex: " + maxBackupIndex
				+ "\n  threshold: " + threshold
				+ "\n  datePattern: " + datePattern
				+ "\n  append: " + append
				+ "\n  target: " + target
				+ "\n  encoding: " + encoding;
	}

}