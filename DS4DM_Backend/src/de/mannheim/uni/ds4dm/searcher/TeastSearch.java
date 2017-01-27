//package de.mannheim.uni.ds4dm.searcher;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.apache.commons.io.FileUtils;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.rapidminer.extension.json.JSONRelatedTablesRequest;
//
//import de.mannheim.uni.ds4dm.demo1.exploreData.GenerateMatchingExample_withKeywords;
//import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
//
//public class TeastSearch {
//	//TODO change
//	static File tebleReositoryFolder = new File(
//			"/Users/annalisa/Documents/AllWorkspaces/ds4dm/ds4dw_WebService/public/exampleData/mappings");
//	
//
//	
//	public static void search(){
//		
//		File request = new File("/Users/annalisa/Documents/AllWorkspaces/ds4dm/ds4dw_WebService/public/exampleData/request_temp.json");
//		try {
//			String json_str = FileUtils.readFileToString(request);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		   	    	
//			File response = new File("/Users/annalisa/Documents/AllWorkspaces/ds4dm/ds4dw_WebService/public/exampleData/response.json");
//
//		
//		JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
//			try {
//				
//				
//				
////				qts = mapper.readValue(json_str, JSONRelatedTablesRequest.class);
//				
//				ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
//				qts = rwj.fromJson(request);
//				
//				System.out.println(qts.toString());
//
//			} catch (JsonParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
///*			try {
//
//			JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
//				
//			ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
//			qts = rwj.fromJson(request);
//			
//			System.out.println(qts.toString());
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}*/
////			   mapping = objectMapper.readValue(json , JSONTableResponse.class);
//
//			
////			JsonFactory jsonFactory = new JsonFactory(); 
////			File request = new File("public/exampleData/request_temp.json");
////			
////			JsonGenerator jsonGen;
////			try {
////				jsonGen = jsonFactory.createGenerator(request, JsonEncoding.UTF8);
////				jsonGen.writeObject(qts);
////
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//
////			List<String> att = qts.getExtensionAttributes();
////			DS4DMBasicMatcher matcher = new DS4DMBasicMatcher(qts);
//			
//			
//			CandidateBuilder_fromJsonFolder candBuilder = new CandidateBuilder_fromJsonFolder(tebleReositoryFolder);
//			File fetchedTablesFolder = new File("/Users/annalisa/Documents/AllWorkspaces/ds4dm/ds4dw_WebService/public/exampleData/tables");
//			if (!fetchedTablesFolder.exists())
//				fetchedTablesFolder.mkdirs();
//			GenerateMatchingExample_withKeywords.serchTables(request, fetchedTablesFolder, response, candBuilder);
//	    	
//	
//	}
//	
//	public static void main(String[] args) {
//		search();
//
//	}
//}
