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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author wnewbury
 */
public class EndpointReport {

	public EndpointReport(String name) {
		int index = name.indexOf("-Elapsed-Time");

		this.name = name.substring(0, index);
	}

	public void printReport(BufferedWriter writer) throws IOException {
		writer.write("For page: " + name + "\n");
		writer.write(
			"Hit the page " + elapsedTimes.size() + " times, with times:\n");
		writer.write(elapsedTimes.toString());
		writer.newLine();
		writer.write("Mean: " + mean());
		writer.write(" Median: " + median());
		writer.write(" Standard Deviation: " + sd());
		writer.newLine();
		writer.newLine();
	}

	public void processPath(Path path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(
			path.toFile()));

		String line = reader.readLine();

		while (line != null) {
			int value = Integer.parseInt(line);

			elapsedTimes.add(value);

			line = reader.readLine();
		}
	}

	public int sum() {
		int sum = 0;

		for (int time: elapsedTimes) {
			sum += time;
		}

		return sum;
    }

	public double mean() {
        return sum() / (elapsedTimes.size() * 1.0);

    }

	public double median() {
		int middle = elapsedTimes.size()/2;
 
		if (elapsedTimes.size() % 2 == 1) {
			return elapsedTimes.get(middle);
		}

		return (elapsedTimes.get(middle-1) + elapsedTimes.get(middle)) / 2.0;
    }

    public double sd() {
		int sum = 0;

        double mean = mean();
 
        for (Integer time : elapsedTimes) {
			sum += Math.pow((time - mean), 2);
		}

        return Math.sqrt(sum / ( elapsedTimes.size() - 1 ));
    }

	private String name;
	private final List<Integer> elapsedTimes = new ArrayList<Integer>();

}