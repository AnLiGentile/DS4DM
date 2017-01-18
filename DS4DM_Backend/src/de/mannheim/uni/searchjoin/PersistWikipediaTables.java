//package de.mannheim.uni.searchJoin;
//
//import java.util.List;
//
//import de.mannheim.uni.index.IndexManager;
//import de.mannheim.uni.model.IndexEntry;
//import de.mannheim.uni.pipelines.Pipeline;
//
//
//public class PersistWikipediaTables  {
//	
//
//	public static void main(String[] args) {
//		
//		String conf = "searchJoins.conf";
//		if (args.length>0)
//			conf=args[0];
//		
//		System.out.println(conf);
//		Pipeline pipe = Pipeline.getPipelineFromConfigFile("ss",
//				conf);
//		IndexManager ma = new IndexManager(pipe);
//		IndexEntry en = new IndexEntry();
////		en.setValue("eacute");
//		en.setValue("population");
//		
//		List<IndexEntry> entries = ma.searchIndex(en);
//		for (IndexEntry e : entries){
//			System.out.println(e.getFullTablePath());
//			
//		}
//		
//		// ma.retrieveRow("Place.csv", 44);
//
//	}
//	
//}
