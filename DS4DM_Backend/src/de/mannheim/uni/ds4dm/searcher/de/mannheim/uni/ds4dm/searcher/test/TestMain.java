package de.mannheim.uni.ds4dm.searcher.de.mannheim.uni.ds4dm.searcher.test;

import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;

import java.io.File;

/**
 * Created by steffen on 20.12.16.
 */
public class TestMain {
    public static void main(String[] args) {
        String conf = "DS4DM_Backend/indexing.conf";
        CandidateBuilder_fromLuceneIndex candBuilder = new CandidateBuilder_fromLuceneIndex(conf);
//        JSONRelatedTablesRequest qts1 = new JSONRelatedTablesRequest();
//        qts1.setQueryTable(new DataSearchExampleSet());
//        DS4DMBasicMatcher qts = new DS4DMBasicMatcher(qts1);
//
//        candBuilder.finCandidates(qts);
        GenerateMatchingExample_withKeywords.serchTables_fromLucene(new File("/home/steffen/workspace/DS4DM_gs/resources/GS_domi/context_json_url/1146722_1_7558140036342906956.json"), new File("/home/steffen/workspace/DS4DM_matching/luceneindex"), new File("testtable"), candBuilder);
        //File fetchedTablesFolder = new File("public/exampleData/tables");
        //if (!fetchedTablesFolder.exists())
        //    fetchedTablesFolder.mkdirs();
//			GenerateMatchingExample_withKeywords.serchTables_fromFolder(request, fetchedTablesFolder, response, candBuilder);
        //GenerateMatchingExample_withKeywords.serchTables_fromLucene(request, fetchedTablesFolder, response, candBuilder);

        //System.out.println(response);
    }
}
