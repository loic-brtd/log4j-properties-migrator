package org.migrator.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Log4j1RootLogger extends Log4j1Element {

	@Nonnull public final List<NumberedValue> appenderNames = new ArrayList<>();
	@Nullable public NumberedValue level;

	@Override
	public String toString() {
		return "Log4j1RootLogger ("
				+ "level: " + level
				+ ", appenderNames: " + appenderNames
				+ ")";
	}

}