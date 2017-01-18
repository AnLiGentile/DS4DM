package de.mannheim.uni.ds4dm.model;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a column of a Table. Holds the references to all the values of the column.
 * 
 * No longer compatible to the original SearchJoins Project, but now uses less than one third of the memory previously needed.
 * @author Oliver
 *
 */
public class TableColumn
        implements java.io.Serializable, Comparable<TableColumn> {

    /**
     *
     */
    private static final long serialVersionUID = 7927975071509939076L;

    /**
     * @return the columnStatistic
     */
    public Statistic getColumnStatistic() {
        return columnStatistic;
    }

    /**
     * @param columnStatistic the columnStatistic to set
     */
    public void setColumnStatistic(Statistic columnStatistic) {
        this.columnStatistic = columnStatistic;
    }

    public static enum ColumnDataType {
        numeric, string, coordinate, date, link, bool, unknown, unit, list
    };

    /*
     * Column meta data
     */
    private String header;
    private ColumnDataType dataType;
    private String dataSource;
    private Table table;
    private String URI;
    private boolean isKey;
    private int numRows;
    private Statistic columnStatistic;
    
    /*
     * Column values
     */
    Map<Integer, Object> values;
    
    
    /**
     * @return the table that contains this column
     */
    public Table getTable() {
        return table;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isKey() {
        return isKey;
    }

    public void setKey(boolean isKey) {
        this.isKey = isKey;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getURI() {
        return URI;
    }
    
    protected void setURI(String URI) {
        this.URI = URI;
    }
    
    public ColumnDataType getDataType() {
        return dataType;
    }

    public void setDataType(ColumnDataType dataType) {
        this.dataType = dataType;
    }

    public Map<Integer, Object> getValues() {
        return values;
    }
    
    public int getNumRows() {
        return numRows;
    }
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public TableColumn(Table table) {
        values = new TreeMap<Integer, Object>();
        header = "";
        dataType = ColumnDataType.unknown;
        this.table = table; 
        this.columnStatistic = new Statistic();
    }

    /**
     * The uniqueness rank of a column is the fraction of unique values minus
     * the fraction of null values.
     *
     * @return
     */
    public double getColumnUniqnessRank() {
        //TODO check if this calculation is still correct!
        int uniqueValues = getNumberOfUniqueValues();
        int totalValues = getValues().size();
        
//        double rank = (double) ((double) (uniqueValues) / (double) totalValues);
//        
//        int numberOfNullValues = totalValues - numRows;
//        
//        rank = rank - ((double) ((double) numberOfNullValues / (double) totalValues));
//
//        return rank;
        double rank1 = (double)uniqueValues / (double)numRows;
        int numNulls = numRows - totalValues;
        double rank2 = (double)numNulls / (double)numRows;
        
        return rank1 - rank2;
    }

    public int getNumberOfUniqueValues() {
        
        HashSet<Object> values = new HashSet<>();
        
        for(Object o : getValues().values()) {
            values.add(o);
        }
        
        return values.size();
    }
    
    @Override
    public String toString() {
        return header;
    }

    @Override
    public int compareTo(TableColumn o) {
//        int comp = getTable().getHeader().compareTo(o.getTable().getHeader());
//        
//        if(comp==0) {
//            comp = Integer.compare(getTable().getColumns().indexOf(this), o.getTable().getColumns().indexOf(o));
//            
//            if(comp==0 && getURI()!=null && o.getURI()!=null) {
//                comp = getURI().compareTo(o.getURI());
//            }
//        }
//        
//        return comp;
        String me = getTable().getHeader() + getTable().getColumns().indexOf(this) + getURI()+ "";
        String other = o.getTable().getHeader() + o.getTable().getColumns().indexOf(o) + o.getURI()+  "";
        return me.compareTo(other);
    }
    
//  too slow...
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(dataSource.hashCode()).append(header.hashCode()).append(table.hashCode()).append(table.getColumns().indexOf(this)).toHashCode();
//    }
//    
//    @Override
//    public boolean equals(Object obj) {
//        return hashCode()==obj.hashCode();
//    }
}
