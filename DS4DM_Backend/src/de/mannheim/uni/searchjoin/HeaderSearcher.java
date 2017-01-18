package de.mannheim.uni.searchjoin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mannheim.uni.index.AttributesIndexManager;
import de.mannheim.uni.model.ColumnIndexEntry;
import de.mannheim.uni.pipelines.Pipeline;


public class HeaderSearcher  {
	String conf = "searchJoins.conf";
	Pipeline pipe;
	
	
	public HeaderSearcher(String conf) {
		super();
		this.conf = conf;
		this.pipe = Pipeline.getPipelineFromConfigFile(HeaderSearcher.class.getName(),
				conf);
	}

	public HeaderSearcher() {
		super();
		this.pipe = Pipeline.getPipelineFromConfigFile(HeaderSearcher.class.getName(),
				conf);
	}

	public Set<String> searchTablesByHeaders(String[] args) {
		Set<String> candidateTables = new HashSet<String>();
		String 			query="";

		if (args.length>0)
			query=args[0];
		


		AttributesIndexManager aim = new AttributesIndexManager(pipe);

		List<ColumnIndexEntry> cie = aim.searchIndex(query);
		for (ColumnIndexEntry e : cie){
			candidateTables.add(e.getTableHeader());
			System.out.println(e.getColumnHeader() +" in --> "+e.getTableHeader());}
		return candidateTables;

	}


	public static void main(String[] args) {
		
		HeaderSearcher hs = new HeaderSearcher();
		String[] q = new String[3];
		q[0] = "lakers";
		q[1] = "";
		q[2] = "";

		System.out.println(hs.searchTablesByHeaders(q));
		 
	}

	
}
