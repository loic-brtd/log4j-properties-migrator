package org.migrator.model;

import javax.annotation.Nonnull;

public class NumberedValue {

	@Nonnull
	public final String value;
	public final int lineNumber;

	public NumberedValue(@Nonnull String value, int lineNumber) {
		this.value = value;
		this.lineNumber = lineNumber;
	}

	@Nonnull
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
