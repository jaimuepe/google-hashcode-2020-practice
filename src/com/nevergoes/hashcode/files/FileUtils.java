package com.nevergoes.hashcode.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

	public static String readFile(String fileName) throws IOException {

		InputStream is = FileUtils.class.getResourceAsStream(fileName);

		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(
				new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}

		return textBuilder.toString();
	}

	public static void writeFile(Path filePath, String content) throws IOException {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile().getAbsoluteFile()))) {
			writer.write(content);
			writer.close();
		}
	}

	public static void writeFile(String filePath, String content) throws IOException {
		writeFile(Paths.get(filePath), content);
	}
}
