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
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author wnewbury
 */
public class ResultsFileVisitor extends SimpleFileVisitor<Path> {

	@Override
	public FileVisitResult preVisitDirectory(
		Path dirPath, BasicFileAttributes attrs) {

		String dirName = dirPath.toString();

		if (dirName.endsWith("/Testray")) {
			return FileVisitResult.SKIP_SUBTREE;
		}

		if (dirName.endsWith("/Full-Depth")
			|| dirName.endsWith("/JMeter")
			|| dirName.endsWith("/Output")) {

			return FileVisitResult.CONTINUE;
		}

		File runReport = new File(dirName  + "/runReport");

		if (runReport.exists()) {
			return FileVisitResult.SKIP_SUBTREE;
		}

		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(
		Path filePath, BasicFileAttributes attrs)
		throws IOException {

		String path = filePath.toString();

		if (!path.endsWith("-Elapsed-Times")) {
			return FileVisitResult.CONTINUE;
		}

		int index = path.lastIndexOf("/");

		String directory = path.substring(0, index);
		String fileName = path.substring(index + 1);

		RunReport runReport = JMeterResultsProcessor.getRunReport(directory);

		runReport.addEndpointReport(fileName, filePath);

		return FileVisitResult.CONTINUE;
	}

}