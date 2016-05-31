/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package JMeterProcessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author wnewbury
 */
public class JMeterResultsProcessor {

	public static void main(String[] args) throws IOException {
		getProperties();

		ResultsFileVisitor jMeterFileVisitor = new ResultsFileVisitor();

		Files.walkFileTree(
			Paths.get(props.getProperty("root.output.dir")), jMeterFileVisitor);

		for (Entry<String, RunReport> entry : runReports.entrySet()) {
			entry.getValue().generateReport();

			System.out.println(
				"Generated Report for directory: " + entry.getKey());
		}
	}

	public static void getProperties()
		throws FileNotFoundException, IOException {

		File propsDir = new File(".");

		FileInputStream is = new FileInputStream(
			new File(propsDir, "testray.processing.properties"));

		props.load(is);
	}

	public static RunReport getRunReport(String directory) {
		RunReport runReport = runReports.get(directory);

		if (runReport == null) {
			runReport = new RunReport(directory);

			runReports.put(directory, new RunReport(directory));
		}

		return runReport;
	}

	private static Properties props = new Properties();
	private static Map<String, RunReport> runReports =
		new HashMap<String, RunReport>();

}