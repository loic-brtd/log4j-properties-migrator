package org.migrator.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Log4j1Logger extends Log4j1Element {

	@Nonnull public final NumberedValue name;
	@Nonnull public final List<NumberedValue> appenderNames = new ArrayList<>();
	@Nullable public NumberedValue level;
	@Nullable public NumberedValue additivity;

	public Log4j1Logger(@Nonnull NumberedValue name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Log4j1Logger ("
				+ "name: " + name
				+ ", level: " + level
				+ ", additivity: " + additivity
				+ ", appenderNames: " + appenderNames
				+ ")";
	}

}