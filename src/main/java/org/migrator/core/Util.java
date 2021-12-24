package org.migrator.core;

import javax.annotation.Nonnull;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

	public static final String ERROR_NAME = "MigrationError";

	public static boolean isEmptyOrComment(@Nonnull String line) {
		line = line.trim();
		return line.isEmpty() || line.charAt(0) == '#' || line.charAt(0) == '!';
	}

	public static String[] splitCSV(@Nonnull String csv) {
		if (csv.trim().isEmpty()) {
			return new String[0];
		}
		return csv.split("\\s*,\\s*");
	}

	public static List<String> readLines(Path path) throws Exception {
		try {
			// First try as ISO_8859_1
			return Files.lines(path, StandardCharsets.ISO_8859_1)
					.collect(Collectors.toList());
		} catch (Exception e) {
			// Second try as UTF_8
			return Files.lines(path, StandardCharsets.UTF_8)
					.collect(Collectors.toList());
		}
	}

	public static void writeLines(List<String> lines, Path path) throws IOException {
		FileWriter fw = new FileWriter(path.toAbsolutePath().toString());

		for (String line : lines) {
			fw.write(line);
			fw.write("\n");
		}

		fw.close();
	}

	public static void logError(String message, int lineNumber) {
		System.err.printf("[%s:%2d] %s%n", ERROR_NAME, lineNumber, message);
	}

}
