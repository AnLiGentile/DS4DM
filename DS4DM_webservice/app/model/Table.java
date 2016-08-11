package model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

//import org.apache.commons.io.IOUtils;

public class Table {

	
	private String getJSONString() {

		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"queryTable\":" + "[{\"att1\":64,\"att2\":\"yes\",\"city\":\"Dortmund\",\"att4\":\"credit card\"}]," + ",");
		String subjectId = "city";
		if (subjectId != null && !subjectId.isEmpty()) {
			builder.append("\"subjectAttributeID\": \"" + subjectId + "\",");
		}
		builder.append("\"extensionAttribute\": \"" + "population" + "\" }");
		return builder.toString();
	}

	
	
	public void getJson() {

		// get example set as JSON
		// String json = getJSONString();
		// LogService.getRoot().log(Level.INFO, json);

		// connect with the API
		HttpURLConnection connection = null;
		try {
			String urlString = "http://localhost:9000/";
			if (urlString == null || urlString.isEmpty()) {
				// throw new user error
			}
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");

			// get example set as JSON
			String json = getJSONString();

			// send specs JSON to server
			OutputStream outputStream = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
			byte[] buffer = new byte[4096];
			int length;
			InputStream jsonIn = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
			while ((length = jsonIn.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			outputStream.flush();
			writer.close();

			// see if response code indicates it worked, otherwise log it; close connection
			int code = connection.getResponseCode();

//			String content = Tools.readTextFile(connection.getInputStream());
//			String content = IOUtils.toString(connection.getInputStream()); 

			// InputStreamReader inputStreamReader = new
			// InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);

			// convert JSON to example sets
			// return collection of example sets and example sets

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}
	
	
}
