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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wnewbury
 */
public class RunReport {

	public RunReport(String directory) {
		this.directory = directory;
	}

	public void generateReport() throws IOException {
		File file = new File(directory + "/runReport");

		int index = directory.lastIndexOf("/");

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					  new FileOutputStream(file), "utf-8"))) {

			printProperties(writer, directory.substring(index + 1));
			printDetails(writer);

			for (EndpointReport endpointReport: endpointReports) {
				endpointReport.printReport(writer);
			}

			writer.flush();
		}
	}

	private void printDetails(BufferedWriter writer) throws IOException {
		double totalAverage = 0.0;

		for (EndpointReport endpointReport: endpointReports) {
			totalAverage += endpointReport.mean();
		}

		writer.write(
			"Total average time from start to end: " + totalAverage + "\n");
		writer.newLine();
		writer.newLine();
	}

	private void printProperties(BufferedWriter writer, String directoryName) throws IOException {
		writer.write(
			"Report for benchmarking run taken at " + directoryName + "\n");
		writer.newLine();
		writer.write(
			"For Information about the configuration of this run," +
				"see testray.jmeter.properties");
		writer.newLine();
	}

	public EndpointReport addEndpointReport(String name, Path path) throws IOException {		
		EndpointReport endpointReport = new EndpointReport(name);

		endpointReport.processPath(path);

		endpointReports.add(endpointReport);

		return endpointReport;
	}

	private String directory;

	private final List<EndpointReport> endpointReports =
		new ArrayList<EndpointReport>();

}