package de.mannheim.uni.ds4dm.searcher.de.mannheim.uni.ds4dm.searcher.test;

import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;

import java.io.File;

/**
 * Created by steffen on 20.12.16.
 */
public class TestMain {
    public static void main(String[] args) {
        CandidateBuilder_fromLuceneIndex candBuilder = new CandidateBuilder_fromLuceneIndex(conf);

        File fetchedTablesFolder = new File("public/exampleData/tables");
        if (!fetchedTablesFolder.exists())
            fetchedTablesFolder.mkdirs();
//			GenerateMatchingExample_withKeywords.serchTables_fromFolder(request, fetchedTablesFolder, response, candBuilder);
        GenerateMatchingExample_withKeywords.serchTables_fromLucene(request, fetchedTablesFolder, response, candBuilder);

        System.out.println(response);
    }
}
