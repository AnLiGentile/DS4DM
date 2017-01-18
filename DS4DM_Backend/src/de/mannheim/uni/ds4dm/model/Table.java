package de.mannheim.uni.ds4dm.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * this is the Table from T2K model
 */
public class Table implements Serializable, Comparable<Table> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean hasKey;
    private String fullPath;
    private String source;
    private List<TableColumn> columns;
    private String header;
    public int nmNulls;
    private int numHeaderRows;
    private transient int totalNumOfRows;
    private transient TableColumn keyColumn;
    private transient int keyIndex = -1;
    
    private transient String contextTimestamp;
    private transient String contextBeforeTable;
    private transient String contextAfterTable;

    public Table() {
        columns = new LinkedList<>();
        header = "";
        hasKey = true;
        nmNulls = 0;        
    }

    public void addColumn(TableColumn column) {
        this.columns.add(column);
    }

    //    @Override
//    public int compareTo(Table o) {
//        return Integer.compare(hashCode, o.hashCode);
//    }
    @Override
    public int compareTo(Table o) {
        return getHeader().compareTo(o.getHeader());
    }

    public void deleteColumn(TableColumn column) {
        this.columns.remove(column);
    }

    public TableColumn getColumn(String header) {
        for (TableColumn c : columns) {
            if (c.getHeader().equals(header)) {
                return c;
            }
        }
        return null;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public String getContextAfterTable() {
        return contextAfterTable;
    }

    public String getContextBeforeTable() {
        return contextBeforeTable;
    }

    public String getContextTimestamp() {
        return contextTimestamp;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getHeader() {
        return header;
    }

    public TableColumn getKey() {
        if (!hasKey) {
            return null;
        } else if (keyColumn != null) {
            return keyColumn;
        } else {
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).isKey()) {
                    keyIndex = i;
                    keyColumn = columns.get(i);
                    return keyColumn;
                }
            }
        }
        return null;
    }

    public int getKeyIndex() {
        if (!hasKey) {
            return -1;
        } else if (keyIndex > -1) {
            return keyIndex;
        } else {
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).isKey()) {
                    keyIndex = i;
                    keyColumn = columns.get(i);
                    return i;
                }
            }
        }
        return -1;
    }

    public int getNmNulls() {
        return nmNulls;
    }

    /**
     * @return the numHeaderRows
     */
    public int getNumHeaderRows() {
        return numHeaderRows;
    }

    public String getSource() {
        return source;
    }
    /**
     * @return the totalNumOfRows
     */
    public int getTotalNumOfRows() {
        return totalNumOfRows;
    }
    
    @Override
    public int hashCode() {
        return fullPath.hashCode();
    }
    public boolean isHasKey() {
        return hasKey;
    }
    
    public StringBuilder printHeader() {
        StringBuilder sb = new StringBuilder();
        for (TableColumn c : getColumns()) {
            sb.append(padString(c.getHeader(), 30));
            sb.append(" | ");
        }
        sb.append("\n");
        return sb;
    }
    public StringBuilder printRow(int row) {
        StringBuilder sb = new StringBuilder();
        for (TableColumn c : getColumns()) {
            String value = "";

            if (c.getValues().get(row) != null) {
                value = c.getValues().get(row).toString();
            }

            sb.append(padString(value, 30));
            sb.append(" | ");
        }
        sb.append("\n");
        return sb;
    }
    
    public String printTable() {
        StringBuilder sb = new StringBuilder();

//        for(TableColumn c : getColumns()) {
//            sb.append(padString(c.getHeader(), 30));
//            sb.append(" | ");
//        }
//        sb.append("\n");
        sb.append(printHeader());

//        for(TableColumn c : getColumns()) {
//            sb.append(padString(c.getDataType().toString(), 30));
//            sb.append(" | ");
//        }
//        sb.append("\n");
        sb.append(printTypes());

        for(TableColumn tc : getColumns()) {
            sb.append("---------------------------------");
        }
        sb.append("\n");
        
//        if (!isHasKey() || getKey().getValues() == null) {
//            return sb.toString();
//        }
        //for (int row : getKey().getValues().keySet()) {
        for(int row = 0; row < getTotalNumOfRows(); row++) {
//            for(TableColumn c : getColumns()) {
//                String value = "";
//                
//                if(c.getValues().get(row)!=null) {
//                    value = c.getValues().get(row).toString();
//                }
//                
//                sb.append(padString(value, 30));
//                sb.append(" | ");
//            }
//            sb.append("\n");
            sb.append(printRow(row));
        }

        return sb.toString();
    }

    public StringBuilder printTypes() {
        StringBuilder sb = new StringBuilder();
        for (TableColumn c : getColumns()) {
            sb.append(padString(c.getDataType().toString(), 30));
            sb.append(" | ");
        }
        sb.append("\n");
        return sb;
    }

    public void setColumns(List<TableColumn> columns) {
        this.columns = columns;
    }

    public void setContextAfterTable(String contextAfterTable) {
        this.contextAfterTable = contextAfterTable;
    }

    public void setContextBeforeTable(String contextBeforeTable) {
        this.contextBeforeTable = contextBeforeTable;
    }

    public void setContextTimestamp(String contextTimestamp) {
        this.contextTimestamp = contextTimestamp;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public void setHeader(String header) {
        if (header.contains("\\")) {
            header = header.substring(header.lastIndexOf("\\") + 1,
                    header.length());
        }
        if (header.contains("/")) {
            header = header.substring(header.lastIndexOf("/") + 1,
                    header.length());
        }
        this.header = header;
    }

    public void setNmNulls(int nmNulls) {
	        this.nmNulls = nmNulls;
	    }

/**
 * @param numHeaderRows the numHeaderRows to set
 */
public void setNumHeaderRows(int numHeaderRows) {
    this.numHeaderRows = numHeaderRows;
}

    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @param totalNumOfRows the totalNumOfRows to set
     */
    public void setTotalNumOfRows(int totalNumOfRows) {
        this.totalNumOfRows = totalNumOfRows;
    }

    @Override
    public String toString() {
        return getHeader();
    }

    protected String padString(String s, int n) {
        if (s.length() > n) {
            s = s.substring(0, n);
        }
        return String.format("%1$-" + n + "s", s);
    }
}
