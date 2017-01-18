//package de.mannheim.uni.searchJoin;
//
//import java.io.IOException;
//import java.util.List;
//
//import de.mannheim.uni.index.AttributesIndexManager;
//import de.mannheim.uni.index.IndexManager;
//import de.mannheim.uni.model.ColumnIndexEntry;
//import de.mannheim.uni.model.IndexEntry;
//import de.mannheim.uni.pipelines.Pipeline;
//
//
//public class DummySearcher  {
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
//		en.setValue("hutton");
////		en.setValue("eacute");
////		en.setValue("é");
//		
//		
//		
//		List<IndexEntry> entries = ma.searchIndex(en);
//		for (IndexEntry e : entries){
//			System.out.println(e.getOriginalValue() +" in --> "+e.getFullTablePath());
//		}
//		// ma.retrieveRow("Place.csv", 44);
//
//		AttributesIndexManager aim = new AttributesIndexManager(pipe);
//
//		List<ColumnIndexEntry> cie = aim.searchIndex("paris");
//		for (ColumnIndexEntry e : cie)
//			System.out.println(e.getColumnHeader() +" in --> "+e.getTableFullPath());
//		
//		 try {
//			aim.generateHeadersList("test.txt", ".");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		 
//	}
//	
//}
