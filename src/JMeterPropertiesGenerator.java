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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author wnewbury
 */
public class JMeterPropertiesGenerator {

	public static void main(String[] args) throws IOException, JSONException {
		Map<String, Long> idValuesMap = new HashMap<String, Long>();

		processTestrayCaseResult(idValuesMap);
		processTestrayRun(idValuesMap);
		processTestrayBuild(idValuesMap);
		processTestrayBuildType(idValuesMap);
		processTestrayTask(idValuesMap);

		generatePropertiesFile(idValuesMap);
	}
	
	private static void doCurl(String url, String[] idNames, Map<String, Long> idValuesMap) throws JSONException, IOException {
		String[] command = {"curl", "-H", "Accept:application/json", "-X", "GET", "-u", _USERNAME + ":" + _PASSWORD , url};

		ProcessBuilder process = new ProcessBuilder(command); 

		Process p = process.start();

		BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));

		StringBuilder sb = new StringBuilder();

		String line = reader.readLine();

		while (line != null) {
			sb.append(line);

			line = reader.readLine();
		}

		String result =  sb.toString();

		if (result != null) {
			JSONObject curlResult = new JSONObject(result);

			JSONObject jsonObject;

			try {
				JSONArray data = curlResult.getJSONArray("data");

				jsonObject = data.getJSONObject(0);
			}
			catch (JSONException e) {
				jsonObject = curlResult.getJSONObject("data");
			}

			for (String idName : idNames) {
				Long idValue = jsonObject.getLong(idName);

				idValuesMap.put(idName, idValue);
			}
		}
	}

	private static void generatePropertiesFile(Map<String, Long> idValuesMap) throws IOException {
		String propertiesOutputDir = System.getProperty("properties.output.dir");
		File original = new File(propertiesOutputDir + "/testray.jmeter.full.depth.properties");
		File generated = new File(propertiesOutputDir + "/testray.jmeter.properties");

		Files.copy(original.toPath(), generated.toPath(), StandardCopyOption.REPLACE_EXISTING);

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					  new FileOutputStream(generated, true), "utf-8"))) {

			printProperties(writer, idValuesMap);

			writer.flush();
		}
	}

	private static void printProperties(BufferedWriter writer, Map<String, Long> idValuesMap) throws IOException {
		writer.newLine();

		writer.write("# Jmeter server\n");
		writer.write("full.depth.server=" + _SERVER + "\n");
		writer.write("full.depth.server.port=" + _PORT + "\n");
		writer.write("full.depth.protocol=" + _PROTOCOL + "\n");
		writer.newLine();

		writer.write("# Testray Object Ids\n");

		for (Map.Entry<String, Long> entry : idValuesMap.entrySet()) {
			writer.write("full.depth." + entry.getKey() + "=" + entry.getValue() + "\n");
		}

		writer.newLine();
		writer.write("# Login Info\n");
		writer.write("full.depth.user=" + _USERNAME + "\n");
		writer.write("full.depth.password=" + _PASSWORD);
	}

	private static void processTestrayBuild(Map<String, Long> idValuesMap) throws IOException, JSONException {
		String url = _URL_BASE + "/web/guest/home/-/testray/builds/" + idValuesMap.get("testrayBuildId") + ".json";
		String[] idNames = new String[] {"testrayBuildTypeId", "testrayProductVersionId"};

		doCurl(url, idNames, idValuesMap);
	}

	private static void processTestrayBuildType(Map<String, Long> idValuesMap) throws IOException, JSONException {
		String url = _URL_BASE + "/web/guest/home/-/testray/build_types/" + idValuesMap.get("testrayBuildTypeId") + ".json";
		String[] idNames = new String[] {"testrayProjectId"};

		doCurl(url, idNames, idValuesMap);
	}

	private static void processTestrayCaseResult(Map<String, Long> idValuesMap) throws IOException, JSONException {
		String url = _URL_BASE + "/web/guest/home/-/testray/case_results.json";
		String[] idNames = new String[] {"testrayCaseResultId", "testrayTeamId", "testrayCaseId", "testrayComponentId", "testrayRunId"};

		doCurl(url, idNames, idValuesMap);
	}

	private static void processTestrayRun(Map<String, Long> idValuesMap) throws IOException, JSONException {
		String url = _URL_BASE + "/web/guest/home/-/testray/runs/" + idValuesMap.get("testrayRunId") + ".json";
		String[] idNames = new String[] {"testrayBuildId"};

		doCurl(url, idNames, idValuesMap);
	}

	private static void processTestrayTask(Map<String, Long> idValuesMap) throws IOException, JSONException {
		String url = _URL_BASE + "/web/guest/home/-/testray/subtasks.json";
		String[] idNames = new String[] {"testraySubtaskId", "testrayTaskId"};

		doCurl(url, idNames, idValuesMap);
	}

	private static final String _PASSWORD = System.getProperty("password");
	private static final String _PORT = System.getProperty("port");
	private static final String _PROTOCOL = System.getProperty("protocol");
	private static final String _SERVER = System.getProperty("server");
	private static final String _URL_BASE = _PROTOCOL + "://" + _SERVER + ":" + _PORT;
	private static final String _USERNAME = System.getProperty("username");

}