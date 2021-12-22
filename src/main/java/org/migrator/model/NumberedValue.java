package org.migrator.model;

public class NumberedValue {

	public final String value;
	public final int lineNumber;

	public NumberedValue(String value, int lineNumber) {
		this.value = value;
		this.lineNumber = lineNumber;
	}

	public String getValue() {
		return value;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String toString() {
		return "(" + lineNumber + ": " + value + ")";
	}

}
