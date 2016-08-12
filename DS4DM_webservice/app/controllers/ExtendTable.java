package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;

import de.mannheim.uni.ds4dm.demo1.exploreData.GenerateMatchingExample_withKeywords;
import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;
import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
import play.mvc.Controller;
import play.*;
import play.mvc.*;

import views.html.*;

public class ExtendTable extends Controller {
	

	//TODO change
	File tebleReositoryFolder = new File(
			"public/exampleData/mappings");
	//TODO change
	String conf = "testConf.conf";

	
	
	/**
	 * The matching is performed by the SearJoin Service, via a POST request to http://ds4dm.informatik.uni-mannheim.de/search
where the input is the table specified above.

The SearJoin service returns a composite response, containing:
A mapping object, which specifies:
the "targetSchema", constructed using the header of the query input table plus the extension attribute(s)       
the "dataTypes" of the target schema
the mapping between the initial query table schema and the target schema ("queryTable2targetSchema")
the mapping between the extension attributes in the query table and the target schema (“extensionAttributes2targetSchema")
and an array of "relatedTables".
Each related table has:
a name ("tableName") which is a unique identifier for the table within the SearchJoin Index
the correspondence between instances in the table and those from the query table ("instancesCorrespondences2QueryTable"); these are given as the <row index from current table>-<row index in query table>
 the correspondence between the schema of the current table and the target schema ("tableSchema2TargetSchema"); these are given as the <column index from current table>-<column index in target schema>
	 * @return
	 */
	public Result search() {
		

		   
	    JsonNode json = request().body().asJson();
	    
//	    String json_str = "{\"table\":{\"extentionAttributes\":[\"Population\"],\"headerRowIndex\":0,\"keyColumnIndex\":\"0\",\"relation\":[[\"Country\",\"Afghanistan\",\"Albania\",\"Algeria\",\"American Samoa\",\"Andorra\",\"Angola\",\"Anguilla\",\"Antigua and Barbuda\",\"Argentina\",\"Armenia\"],[\"Region\",\"South Asia\",\"Eastern Europe & Central Asia\",\"Middle East & North Africa\",\"Sub-Saharan Africa\",\"Latin America & Caribbean\",\"Latin America & Caribbean\",\"Eastern Europe & Central Asia\",\"OECD high income\",\"OECD high income\",\"Eastern Europe & Central Asia\"]]}}";
	    if(json == null) {
	        return badRequest("Expecting Json data");
	    } else {
	    	
			File response = new File("public/exampleData/response.json");

			ObjectMapper mapper = new ObjectMapper();
			String json_str= "";
			try {
				json_str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			} catch (JsonProcessingException e2) {
				e2.printStackTrace();
			}
			
			
			File request = new File("public/exampleData/request_temp.json");

			
			try (FileOutputStream fop = new FileOutputStream(request)) {

				// if file doesn't exists, then create it
				if (!request.exists()) {
					request.createNewFile();
				}

				// get the content in bytes
				byte[] contentInBytes = json_str.getBytes();

				fop.write(contentInBytes);
				fop.flush();
				fop.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
//			JSONRelatedTablesRequest qts = mapper.convertValue(json, JSONRelatedTablesRequest.class);
			JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
			try {
				
				
				
//				qts = mapper.readValue(json_str, JSONRelatedTablesRequest.class);
				
				ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
				qts = rwj.fromJson(request);
				
				System.out.println(qts.toString());

			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
//			CandidateBuilder_fromJsonFolder candBuilder = new CandidateBuilder_fromJsonFolder(tebleReositoryFolder);
			//TODO testing with hardcoded conf file
			CandidateBuilder_fromLuceneIndex candBuilder = new CandidateBuilder_fromLuceneIndex(conf);

			File fetchedTablesFolder = new File("public/exampleData/tables");
			if (!fetchedTablesFolder.exists())
				fetchedTablesFolder.mkdirs();
//			GenerateMatchingExample_withKeywords.serchTables_fromFolder(request, fetchedTablesFolder, response, candBuilder);
			GenerateMatchingExample_withKeywords.serchTables_fromLucene(request, fetchedTablesFolder, response, candBuilder);

	    	System.out.println(response);
	    	return ok(response);
	    }
	    
	    
		}
	
	//TODO fill logic
	/**
	 *  in this first iteration we assume: (i) one single attribute and (ii) we perform exact string matching of this attribute on potentially relevant tables. In the future we will design more sofisticated methods for (ii) and provide an attributeSuggestionService as a POST request to http://ds4dm.informatik.uni-mannheim.de/suggestAttributes passing the INPUT table in the same format
	 * @return
	 */
	public Result suggestAttributes() {

		 return ok(new File("public/exampleData/response.json"));
		}
	
	/**
	 *  to retrieve the full content of each of the related table, it is possible to use the service http://ds4dm.informatik.uni-mannheim.de/fetchTable with a GET request, passing the tableName parameter.
This is to avoid returning potentially big results in a single request, as we expect the number and size of tables to grow in future iterations.
	 * @return
	 */
//	curl --header "Content-type: application/json" --request POST --data '{"name": "11688006_0_8123036130090004213.json"}' http://ds4dm.informatik.uni-mannheim.de/fetchTable

	
//    "dataTypes": {...   }, 
//		    "hasHeader": true, 
//		    "hasKeyColumn": true, 
//		    "headerRowIndex": 0, 
//		    "keyColumnIndex": 1, 
//		    "url": "http://www.worldatlas.com/aatlas/populations/ctypopls.htm",
//		    "relation": [ ...]
		                 
		                 public Result fetchTablePOST() {
		
		File tableIndex = new File("public/exampleData/tables");
		File[] t = tableIndex.listFiles();
		List<String> tables = new ArrayList<String>();
		for (File f: t)
			tables.add(f.getName());
		

	    JsonNode json = request().body().asJson();
	    if(json == null) {
	        return badRequest("Expecting Json data");
	    } else {
	        String name = json.findPath("name").textValue();
		    
	        if(name != null) {
	        	if (!name.endsWith(".json"))
	        		name = name+".json";
	        		
	        	if (tables.contains(name)){
	       		 return ok(new File("public/exampleData/tables/"+name));

	        	}else{
//		       		 return ok(new File("public/exampleData/tables/"+name));

	        		return notFound("Table "+name+" not found");
	        	}
			    
	        } else {
	        	
	        	return badRequest("Expecting json request, with table id specified by the attribute name");
	        	}
	    }
		}
	 
		/**
		 * @param name the name of the table
		 * @return return a json response representing the table
		 */
		public Result fetchTable(String name) {
    
		File tableIndex = new File("public/exampleData/tables");
		File[] t = tableIndex.listFiles();
		List<String> tables = new ArrayList<String>();
		for (File f: t)
			tables.add(f.getName());

		    
	        if(name != null) {
//	        	if (!name.endsWith(".json"))
//	        		name = name+".json";
	        		
	        	if (tables.contains(name)){
	       		 return ok(new File("public/exampleData/tables/"+name));

	        	}else{
//		       		 return ok(new File("public/exampleData/tables/"+name));

	        		return notFound("Table "+name+" not found");
	        	}
			    
	        } else {
	        	
	        	return badRequest("Expecting json request, with table id specified by the attribute name");
	        	}
	    
		}
		                 
	public Result ind() {
        return ok(index.render("Your new application is ready."));
    }
		
}
