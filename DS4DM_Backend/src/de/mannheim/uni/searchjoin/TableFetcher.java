package de.mannheim.uni.searchjoin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;

import de.mannheim.uni.ds4dm.demo1.exploreData.GenerateMatchingExample_withKeywords;
import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;
import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
import de.mannheim.uni.index.IndexManager;
import de.mannheim.uni.model.IndexEntry;
import de.mannheim.uni.pipelines.Pipeline;


public class TableFetcher  {
	String conf = "searchJoins.conf";
	Pipeline pipe;
	IndexManager aim;
	
	public TableFetcher(String conf) {
		super();
		this.conf = conf;
		this.pipe = Pipeline.getPipelineFromConfigFile(TableFetcher.class.getName(),
				conf);
		this.aim = new IndexManager(pipe);

	}

	public TableFetcher() {
		super();
		this.pipe = Pipeline.getPipelineFromConfigFile(TableFetcher.class.getName(),
				conf);
		this.aim = new IndexManager(pipe);

	}
	

	public String [][] getRowValues(String tableName) {
		
		List<IndexEntry> v = aim.fetchTablebRelationValues(tableName);
		
		//byColumn relation (the key is the column header)
		Map<String,Map<Integer,String>> entries = new HashMap<String,Map<Integer,String>>();

		int maxRow = 0;
		for (IndexEntry vv : v){
			if (!entries.containsKey(vv.getColumnHeader()))
				entries.put(vv.getColumnHeader(), new HashMap<Integer,String>() );
			
			entries.get(vv.getColumnHeader()).put(vv.getEntryID(), vv.getOriginalValue());
			
			if (vv.getEntryID()>maxRow){
				maxRow = vv.getEntryID();
			}
					}
		
		//TODO fix size
		String [][] relation = new String [entries.size()][maxRow+2];

		int c =0;
		for (Entry<String, Map<Integer, String>> e : entries.entrySet()){
			relation [c][0]= e.getKey();
			for (Entry<Integer, String> val : e.getValue().entrySet()){
				int rowindex = val.getKey();
				rowindex = rowindex+1;
				//TODO check
			try {
				relation [c][rowindex]= val.getValue();
			} catch (Exception e1) {
				System.err.println("error at column = "+c+" row = "+rowindex);
				System.err.println("size "+relation.length+" row = "+relation[c].length);
				e1.printStackTrace();
			}
					}
			c++;
		}		
		
//		for (String [] r : relation){
//			for (String rr : r){
//				System.out.print(rr+"\t");
//			}
//			System.out.println();
//		}
		return relation;
		
	}
	
	
	
	public Set<String> getTableFullPath(String tableName) {

		Set<String> candidateTables = new HashSet<String>();
		Set<Document> candidateDocs = new HashSet<Document>();


		IndexManager aim = new IndexManager(pipe);
		
		
		candidateDocs = aim.fetchTableFullPath(tableName);
		System.out.println(candidateDocs.size());
		for (Document d : candidateDocs){
			candidateTables.add(d.get(IndexManager.FULL_TABLE_PATH));
		}
		return candidateTables;

	}


	public static void main	(String[] args) {
		
//		TableFetcher hs = new TableFetcher();
		
		
//		int[][][] threeDimArr = { { { 1, 2 }, { 3, 4 } }, { { 5, 6 }, { 7, 8 } } };
//		String[][] twoDimArr = {{ "1", "2", "", "extra" }, 
//								{ "3", "4", "5", "6" } };
//
//		for (String[] ss:twoDimArr){
//			for (String sss:ss){
//				System.out.print(sss+"\t");
//			}
//			System.out.println();
//		}
//		
//		for (int i=0; i<twoDimArr.length; i++){
//			for (int j=0; j<twoDimArr[i].length; j++){
//				System.out.print(twoDimArr[i][j]+"\t");
//			}
//			System.out.println();
//		}
		

//		System.out.println(hs.searchTableByName("1950_Drivers_Championship_final_standings_72023_154109.csv.gz"));
//		System.out.println(hs.getRowValues("1950_Drivers_Championship_final_standings_72023_154109.csv.gz"));

/*		System.out.println(hs.getTableFullPath("1_November_1982_-_Second_Quarter_1983_615795_1332688.csv.gz"));
		String[][] s = hs.getRowValues("1_November_1982_-_Second_Quarter_1983_615795_1332688.csv.gz");
		for (String[] ss:s){
			for (String sss:ss){
				System.out.print(sss+"\t");
			}
			System.out.println();
		}*/
		
		
		
		File request = new File("temp/request_test_population.json");
		File response = new File("temp/response_test_population.json");

		JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
		try {
			
						
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

		CandidateBuilder_fromLuceneIndex candBuilder = new CandidateBuilder_fromLuceneIndex(args[0]);

		File fetchedTablesFolder = new File("public/exampleData/tables");
		//erase existing
		if (fetchedTablesFolder.exists())
			try {
				FileUtils.deleteDirectory(fetchedTablesFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			fetchedTablesFolder.mkdirs();
			
			long start = System.currentTimeMillis();
		GenerateMatchingExample_withKeywords.serchTables_fromLucene(request, fetchedTablesFolder, response, candBuilder);
		long end = System.currentTimeMillis();
		long elapsed = (end-start)/1000;
		

    	System.out.println(response);
    	System.out.println("elapsed seconds: "+ elapsed);

		
//		System.out.println(hs.searchTableByName("/Users/annalisa/Documents/DS4DM_local/useful data/datasets/wikitables/part1-1000/-48kg_Women_396049_802730.csv.gz"));
//		System.out.println(hs.searchTableByName("/Users/annalisa/Documents/DS4DM_local/useful data/datasets/wikitables/part1-1000/07_14_2007_Pepsi_Racing_100__Thompson_International_Speedway_400648_815711.csv.gz"));

//		fullTablePath:/Users/annalisa/Documents/DS4DM_local/useful data/datasets/wikitables/part1-1000/-48kg_Women_396049_802730.csv.gz
//		fullTablePath:\/Users\/annalisa\/Documents\/DS4DM_local\/useful data\/datasets\/wikitables\/part1\-1000\/\-48kg_Women_396049_802730.csv.gz
	}

	
}
