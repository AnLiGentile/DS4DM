package de.mannheim.uni.ds4dm.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

/**
 * this class provides the mapping to json - the Model is Table
 * use Sabrina's class instead
 *
 */
@Deprecated 
public class TableData {

    public enum HeaderPosition {
        FIRST_ROW
    }

    public enum TableOrientation {
        HORIZONTAL, VERTICAL
    }

    public enum TableType {
        RELATION
    }

    private String[][] relation;
    private String pageTitle;
    private String title;
    private String url;
    private boolean hasHeader;
    private HeaderPosition headerPosition;
    private TableType tableType;
    private int tableNum;
    private String s3Link;
    private int recordEndOffset;
    private int recordOffset;
    private TableOrientation tableOrientation;
    private String TableContextTimeStampBeforeTable;
    private String textBeforeTable;
    private String textAfterTable;
    private boolean hasKeyColumn;//this is what is called subject column
    private int keyColumnIndex;//this is what is called subject column
    private int headerRowIndex;

    public static TableData fromJson(File file) throws IOException {
        Gson gson = new Gson();
        
        FileReader reader = new FileReader(file);
        // get the data from the JSON source
        TableData data = gson.fromJson(reader, TableData.class);
        
        reader.close();
        
        return data;
    }

    public String[] getColumnHeaders() {
        String[] headers = null;
        headers = new String[relation.length];
    
        for (int col = 0; col < relation.length; col++) {
            headers[col] = relation[col][0];
        }
        return headers;
        
//        if (tableType != TableType.RELATION
//                || tableOrientation != TableOrientation.HORIZONTAL
//                || !hasHeader) {
//            return null;
//        }
//
//        headers = null;
//
//        if(headerPosition!=null) {
//            switch (headerPosition) {
//            case FIRST_ROW:
//                headers = new String[relation.length];
//    
//                for (int col = 0; col < relation.length; col++) {
//                    headers[col] = relation[col][0];
//                }
//                break;
//            default:
//    
//            }
//        }
//
//        return headers;
    }

    public HeaderPosition getHeaderPosition() {
        return headerPosition;
    }

    public int getHeaderRowIndex() {
        return headerRowIndex;
    }

    public int getKeyColumnIndex() {
        return keyColumnIndex;
    }

    public int getNumberOfHeaderRows() {
        if (tableType != TableType.RELATION
                || tableOrientation != TableOrientation.HORIZONTAL
                || !hasHeader || headerPosition != HeaderPosition.FIRST_ROW) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public int getRecordEndOffset() {
        return recordEndOffset;
    }

    public int getRecordOffset() {
        return recordOffset;
    }

    public String[][] getRelation() {
        return relation;
    }

    public String getS3Link() {
        return s3Link;
    }

    public String getTableContextTimeStampBeforeTable() {
        return TableContextTimeStampBeforeTable;
    }

    public int getTableNum() {
        return tableNum;
    }

    public TableOrientation getTableOrientation() {
        return tableOrientation;
    }

    public TableType getTableType() {
        return tableType;
    }

    public String getTextAfterTable() {
        return textAfterTable;
    }

    public String getTextBeforeTable() {
        return textBeforeTable;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public boolean isHasKeyColumn() {
        return hasKeyColumn;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public void setHasKeyColumn(boolean hasKeyColumn) {
        this.hasKeyColumn = hasKeyColumn;
    }

    public void setHeaderPosition(HeaderPosition headerPosition) {
        this.headerPosition = headerPosition;
    }

    public void setHeaderRowIndex(int headerRowIndex) {
        this.headerRowIndex = headerRowIndex;
    }

    public void setKeyColumnIndex(int keyColumnIndex) {
        this.keyColumnIndex = keyColumnIndex;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setRecordEndOffset(int recordEndOffset) {
        this.recordEndOffset = recordEndOffset;
    }

    public void setRecordOffset(int recordOffset) {
        this.recordOffset = recordOffset;
    }

    public void setRelation(String[][] relation) {
        this.relation = relation;
    }

    public void setS3Link(String s3Link) {
        this.s3Link = s3Link;
    }

    public void setTableContextTimeStampBeforeTable(
            String tableContextTimeStampBeforeTable) {
        TableContextTimeStampBeforeTable = tableContextTimeStampBeforeTable;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }

    public void setTableOrientation(TableOrientation tableOrientation) {
        this.tableOrientation = tableOrientation;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public void setTextAfterTable(String textAfterTable) {
        this.textAfterTable = textAfterTable;
    }

    public void setTextBeforeTable(String textBeforeTable) {
        this.textBeforeTable = textBeforeTable;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void transposeRelation() {
        int colNum = 0;
        
        for(int i = 0; i < relation.length; i++) {
            colNum = Math.max(colNum, relation[i].length);
        }
        
        String[][] newRelation = new String[colNum][relation.length];
        
        for(int i = 0; i < relation.length; i++) {
            for(int j = 0; j < colNum; j++) {
                
                if(j < relation[i].length) {
                    newRelation[j][i] = relation[i][j];
                }
                
            }
        }
        
        relation = newRelation;
    }
    

}
