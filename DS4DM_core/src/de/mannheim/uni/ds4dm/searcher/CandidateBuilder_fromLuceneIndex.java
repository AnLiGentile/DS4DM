package de.mannheim.uni.ds4dm.searcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mannheim.uni.ds4dm.model.TableData;
import de.mannheim.uni.normalizer.StringNormalizer;
import de.mannheim.uni.searchJoin.HeaderSearcher;
import de.mannheim.uni.searchJoin.TableFetcher;

public class CandidateBuilder_fromLuceneIndex implements CandidateBuilder {

	private String indexConfigurationFilePath;
	private File folderFile;
	private	HeaderSearcher hs;
	private TableFetcher tf;

	
	
	public CandidateBuilder_fromLuceneIndex() {
		super();
		this.indexConfigurationFilePath = "searchJoins.conf";
		this.hs = new HeaderSearcher();
		this.tf = new TableFetcher();

	}
	
	public CandidateBuilder_fromLuceneIndex(String indexConfigurationFilePath) {
		super();
		this.indexConfigurationFilePath = indexConfigurationFilePath;
		this.hs = new HeaderSearcher(indexConfigurationFilePath);
		this.tf = new TableFetcher(indexConfigurationFilePath);

	}

	public Map<String, TableData> finCandidates(
			DS4DMBasicMatcher qts) {
					
		
		Map<String,TableData> candidates = new HashMap<String,TableData>();
		List<String> att = qts.getExtAtt();
		String [] headers = att.toArray(new String[att.size()]);
		
		//TODO close things that are open
		Set<String> tableNames = this.hs.searchTablesByHeaders(headers);
		
		for (String t:tableNames){
//			candidates.put(t.getName(),table);
//TODO load a table 
			TableData table = loadTable(this.tf, t);
			candidates.put(t,table);
		}
		
		return candidates;
	}
	
	

	private TableData loadTable(TableFetcher hs, String t) {

		TableData tab = new TableData();
		try {
			tab.setHasHeader(true);
			tab.setHasKeyColumn(true);
			tab.setRelation(hs.getRowValues(t));
		} catch (Exception e) {
			System.err.println("error loading table "+t);
			e.printStackTrace();
			return null;
		}
		return tab;
	}

	private boolean headersMatch(String[] head, List<String> att) {
		List<String> normalizedHeaders = new ArrayList<String>();
		List<String> normalizedTarget = new ArrayList<String>();

		for (String s: head){
			normalizedHeaders.add(StringNormalizer.format(s));
		}
		for (String s: att){
			normalizedTarget.add(StringNormalizer.format(s));
		}
		
		for (String s: normalizedHeaders){
			if (normalizedTarget.contains(s))
				return true;
		}
		
		return false;
	}





	@Override
	public Set<TableData> finCandidates(Matcher matcher) {
		// TODO Auto-generated method stub
		return null;
	}



}
