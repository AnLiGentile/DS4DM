package de.mannheim.uni.ds4dm.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


/**
 * @author annalisa
 * this is a prototype for testing purposes
 */

public class AnnotatedResponseTable {

    private QueryTableData queryTable;
    private TableMapping mapping;
        
    public QueryTableData getQueryTable() {
        return queryTable;
    }
    public void setQueryTable(QueryTableData table) {
        this.queryTable = table;
    }
    
    public TableMapping getMapping() {
        return mapping;
    }
    public void setMapping(TableMapping mapping) {
        this.mapping = mapping;
    }
    
    public void writeJson(File file) throws IOException {
        Gson gson = new Gson();
        BufferedWriter w = new BufferedWriter(new FileWriter(file));
        w.write(gson.toJson(this));
        w.close();
    }
    
    public static AnnotatedResponseTable fromJson(File file) throws JsonSyntaxException, IOException {
        Gson gson = new Gson();
        return gson.fromJson(FileUtils.readFileToString(file), AnnotatedResponseTable.class);
    }
	public void setRelatedTables(TableMappingToQuery[] array) {
		// TODO Auto-generated method stub
		
	}
	public Object getRelatedTables() {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    
}
