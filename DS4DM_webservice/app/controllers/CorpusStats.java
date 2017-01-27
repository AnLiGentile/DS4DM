//package controllers;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rapidminer.extension.json.JSONRelatedTablesRequest;
//
//import de.mannheim.uni.ds4dm.demo1.exploreData.GenerateMatchingExample_withKeywords;
//import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;
//import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
//import play.mvc.Controller;
//import play.*;
//import play.mvc.*;
//
//import views.html.*;
//
//public class CorpusStats extends Controller {
//	
//		public Result stats() {
//		    	
//				File response = new File("public/exampleData/stats.json");
//
//				//TODO change Config.conf
//				CandidateBuilder_fromLuceneIndex candBuilder = new CandidateBuilder_fromLuceneIndex(Config.conf);
//
//				GenerateMatchingExample_withKeywords.serchTables_fromLucene(request, fetchedTablesFolder, response, candBuilder);
//
//		    	System.out.println(response);
//		    	return ok(response);
//		    
//		    
//		    
//			}
//		                 
//	public Result ind() {
//        return ok(index.render("Your new application is ready."));
//    }
//		
//}
