package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;

import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
import play.mvc.Controller;
import play.mvc.Result;

public class ExtendTableT2k extends Controller {
	private final String conf = "testConf.conf";

	public Result search() {
	    JsonNode json = request().body().asJson();
	    if(json == null) {
	        return badRequest("Expecting Json data");
	    }
	    
		File response = new File("public/exampleData/response.json");
		File request = new File("public/exampleData/request_temp.json");

		try (FileOutputStream fop = new FileOutputStream(request)) {
			createRequestFile(json, request, fop);
			ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(new JSONRelatedTablesRequest());
			JSONRelatedTablesRequest qts = rwj.fromJson(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File fetchedTablesFolder = new File("public/exampleData/tables");

		if (!fetchedTablesFolder.exists())
			fetchedTablesFolder.mkdirs();
		T2kMatching.match(request, fetchedTablesFolder, response);
		return ok(response);
	}

	private void createRequestFile(JsonNode json, File request, FileOutputStream fop)
			throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String json_str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		
		// if file doesn't exists, then create it
		if (!request.exists()) {
			request.createNewFile();
		}
		
		// get the content in bytes
		byte[] contentInBytes = json_str.getBytes();
		fop.write(contentInBytes);
		
		fop.flush();
		fop.close();
	}
}
