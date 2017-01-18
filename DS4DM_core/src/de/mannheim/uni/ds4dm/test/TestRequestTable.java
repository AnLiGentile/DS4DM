package de.mannheim.uni.ds4dm.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonSyntaxException;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;

import de.mannheim.uni.ds4dm.utils.ReadWriteGson;

/**
 * @author annalisa
 *
 */
public class TestRequestTable {

	public static void main(String[] args) {
//		File request = new File(
//				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/requestNew.json");
//		File request = new File(
//				"/Users/annalisa/Documents/AllWorkspaces/ds4dm/ds4dw_WebService/public/exampleData/request.json");
		
		File request = new File(
				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/requestNew.json");
		String json_str="";
		try {
			
			json_str = FileUtils.readFileToString(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
//			ObjectMapper mapper = new ObjectMapper();
//
//			JSONRelatedTablesRequest qts =null;
//			try {
//				qts = mapper.readValue(json_str, JSONRelatedTablesRequest.class);
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
			


		JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
			ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
			qts = rwj.fromJson(request);
			System.out.println(json_str);

			System.out.println("***extention attributes***");
			System.out.println(qts.getExtensionAttributes());

			System.out.println("***subjects to match***");

			List<String> sujSab = qts.getQueryTable().get(
					Integer.parseInt(qts.getKeyColumnIndex()));
			System.out.println(sujSab);

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String readTextFile(Reader r) throws IOException {
		  StringBuilder contents = new StringBuilder();
		  BufferedReader reader = new BufferedReader(r);
		  String line = "";
		  try {
		   while ((line = reader.readLine()) != null) {
		    contents.append(line + getLineSeparator());
		   }
		  } finally {
		   reader.close();
		  }
		  return contents.toString();
		 }
	
	
	private static String getLineSeparator() {
		String r = System.getProperty("line.separator");
		return r;
	}
	  

	
}
