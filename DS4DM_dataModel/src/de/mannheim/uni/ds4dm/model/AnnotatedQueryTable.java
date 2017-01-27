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

public class AnnotatedQueryTable {

    private QueryTableData table;
    private TableMapping mapping;
    
    public QueryTableData getTable() {
        return table;
    }
    public void setTable(QueryTableData table) {
        this.table = table;
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
    
    public static AnnotatedQueryTable fromJson(File file) throws JsonSyntaxException, IOException {
        Gson gson = new Gson();
        return gson.fromJson(FileUtils.readFileToString(file), AnnotatedQueryTable.class);
    }
    
    
    
}
