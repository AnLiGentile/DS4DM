//package de.mannheim.uni.ds4dm.searcher;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import com.google.gson.JsonSyntaxException;
//
//import de.mannheim.uni.ds4dm.model.AnnotatedTable;
//import de.mannheim.uni.ds4dm.model.TableData;
//import de.mannheim.uni.ds4dm.normalizer.StringNormalizer;
//
//public class CandidateBuilder_fromJsonFolder implements CandidateBuilder {
//	
//	public CandidateBuilder_fromJsonFolder(String folder) {
//		super();
//		this.folder = folder;
//		init();
//		
//	}
//
//	public CandidateBuilder_fromJsonFolder(File folderFile) {
//		super();
//		this.folder = folderFile.getAbsolutePath();
//		this.folderFile = folderFile;
//		
//	}
//	
//	
//	private void init() {
//	this.folderFile = new File(this.folder);
//		if (!folderFile.exists()){
//			System.err.println(this.folder +" is not a folder!");
//		}
//	}
//
//
//	String folder;
//	private File folderFile;
//
//	public String getFolder() {
//		return folder;
//	}
//
//
//	public void setFolder(String folder) {
//		this.folder = folder;
//	}
//
//
//	public Map<String, TableData> finCandidates(
//			DS4DMBasicMatcher qts) {
//					
//					
//		Map<String,TableData> candidates = new HashMap<String,TableData>();
//		List<String> att = qts.getExtAtt();
//		for (File t : this.folderFile.listFiles()){
//			if (t.getName().endsWith(".json")){
//			AnnotatedTable at;
//			try {
//				at = AnnotatedTable.fromJson(t);
//				TableData table = at.getTable();
//				if (table==null){
//					//try and read as TableData directly
//					System.err.println(t + " does not contain object of type: "+AnnotatedTable.class);
//					table = TableData.fromJson(t);
//					System.err.println("Reading as: "+TableData.class);
//					if(table!=null){
//						System.err.println(t + " contains table: "+table.getTitle()+" num of columns: "+table.getRelation().length );
//					}
//
//				}
//				String[] head = table.getColumnHeaders();
////				JSONTableResponse table = Table2TableDS4DM.fromAnnotatedTable2JSONTableResponse(at.getTable(), t.getName());
////				for(String h : head){				
////					if (att.contains(h.toLowerCase())){
////						candidates.put(t.getName(),table);
////						break; //if at least one match exists
////					}
////				}
//				if (headersMatch(head, att)){
//					candidates.put(t.getName(),table);
//				}
//			
//			} catch (JsonSyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				System.err.println("Error with file "+t);
//				e.printStackTrace();
//			}
//		}
//		}
//		
//		
//		return candidates;
//	}
//
//	private boolean headersMatch(String[] head, List<String> att) {
//		List<String> normalizedHeaders = new ArrayList<String>();
//		List<String> normalizedTarget = new ArrayList<String>();
//
//		for (String s: head){
//			normalizedHeaders.add(StringNormalizer.format(s));
//		}
//		for (String s: att){
//			normalizedTarget.add(StringNormalizer.format(s));
//		}
//		
//		for (String s: normalizedHeaders){
//			if (normalizedTarget.contains(s))
//				return true;
//		}
//		
//		return false;
//	}
//
//	@Override
//	public Set<TableData> finCandidates(Matcher matcher) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
