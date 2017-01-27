package de.mannheim.uni.tableprocessor.IO;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import de.mannheim.uni.ds4dm.model.Table;
import de.mannheim.uni.ds4dm.model.TableColumnBuilder;
import de.mannheim.uni.ds4dm.model.TableData;
import de.mannheim.uni.ds4dm.model.TableData.TableOrientation;
import de.mannheim.uni.ds4dm.model.TableData.TableType;
import de.mannheim.uni.ds4dm.tableprocessor.ColumnType;
import de.mannheim.uni.ds4dm.tableprocessor.ColumnTypeGuesser;
import de.mannheim.uni.units.UnitParserDomi;
import de.mannheim.uni.units.Unit_domi;
import de.mannheim.uni.utils.Variables;
import de.mannheim.uni.utils.concurrent.Consumer;
import de.mannheim.uni.utils.concurrent.Parallel;
import de.mannheim.uni.utils.data.Pair;

/**
 * Parses a table from the WDCv2 JSON data format
 *
 * @author Oliver
 *
 */
public class JsonTableParser {

    private boolean cleanHeader = true;

    public boolean isCleanHeader() {
        return cleanHeader;
    }

    public void setCleanHeader(boolean cleanHeader) {
        this.cleanHeader = cleanHeader;
    }

    private ColumnTypeGuesser typeGuesser = new ColumnTypeGuesser();

    public static void main(String[] args) {
        TableReader tr = new TableReader();
        
        Table t = tr.readWebTableFromJson(args[0]);

        System.out.println(t.getHeader());
        System.out.println(t.printTable());
    }

    public Table parseJson(File file) {
        FileReader fr;
        Table t = null;
        try {
            fr = new FileReader(file);

            t = parseJson(fr, file.getName());

            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return t;
    }

    public Table parseJson(Reader reader, String fileName) {
        Gson gson = new Gson();

        // get the data from the JSON source
        TableData data = gson.fromJson(reader, TableData.class);

        return parseJson(data, fileName);
    }

    public Table parseJson(TableData data, String fileName) {
        UnitParserDomi.readInUnits();

        if (data.getTableType() != TableType.RELATION) {
            return null;
        }

        if (data.getTableOrientation() == TableOrientation.VERTICAL) {
            // flip table
            data.transposeRelation();
            //return null;
        }

        // create a new table
        Table t = new Table();

        t.setSource(data.getUrl());
        t.setContextTimestamp(data.getTableContextTimeStampBeforeTable());
        t.setContextBeforeTable(data.getTextBeforeTable());
        t.setContextAfterTable(data.getTextAfterTable());
        t.setHeader(fileName);

        // determine the total number of rows
        int ttlNumRows = 0;
        for (int col = 0; col < data.getRelation().length; col++) {
            ttlNumRows = Math.max(ttlNumRows, data.getRelation()[col].length);
        }
        t.setTotalNumOfRows(ttlNumRows);

        // create the table columns
        List<Pair<TableColumnBuilder, String[]>> columns = getColumnData(data, t);

        final int numRowsToSkip = data.getNumberOfHeaderRows();
        //final int numRowsToSkip = 1;
        t.setNumHeaderRows(numRowsToSkip);

        // fill the columns with values
        new Parallel<Pair<TableColumnBuilder, String[]>>(1).tryForeach(columns, new Consumer<Pair<TableColumnBuilder, String[]>>() {

            @Override
            public void execute(Pair<TableColumnBuilder, String[]> parameter) {

                for (int row = numRowsToSkip; row < parameter.getSecond().length; row++) {

                    String value = parameter.getSecond()[row];

                    processValue(parameter.getFirst(), value, row);

                }

            }
        });

        // complete the column construction (decide final data type, etc.)
        for (Pair<TableColumnBuilder, String[]> p : columns) {
            p.getFirst().buildColumn();
        }
//        TableKeyIdentifier keyIdentifier = new TableKeyIdentifier();
//        keyIdentifier.identifyKeys(t);
        if(data.getKeyColumnIndex()!=-1) {
            t.getColumns().get(data.getKeyColumnIndex()).setKey(true);
            t.setHasKey(true);
        } 

        return t;
    }

    protected List<Pair<TableColumnBuilder, String[]>> getColumnData(TableData data, Table table) {
        String[] columnNames = data.getColumnHeaders();

        List<Pair<TableColumnBuilder, String[]>> columns = new LinkedList<>();

        for (int colIdx = 0; colIdx < data.getRelation().length; colIdx++) {
            String columnName = null;

            if (columnNames != null && columnNames.length > colIdx) {
                columnName = columnNames[colIdx];
            } else {
                columnName = "";
            }

            TableColumnBuilder b = new TableColumnBuilder(table);

            // set the header
            String header = columnName;
            if (isCleanHeader()) {
                header = StringNormalizer.cleanWebHeader(StringNormalizer.simpleStringNormalization(columnName, false));
            }
            b.setHeader(header);

            // parse units from headers
            Unit_domi unit = UnitParserDomi.parseUnitFromHeader(columnName);
            b.setUnitFromHeader(unit);

            columns.add(new Pair<TableColumnBuilder, String[]>(b, data.getRelation()[colIdx]));
            table.getColumns().add(b.getColumn());
        }
        //table.setNumHeaderRows(1);

        return columns;
    }

    protected void processValue(TableColumnBuilder b, String value, int row) {
        if (value.contains("&mdash")) {
            value = value.replace("&mdash", "-");
        }

        // handle lists
        if (ListHandler.checkIfList(value)) {
            //valueType = ColumnDataType.list;
            // split values, normalise, determine type, ...
        } else {
            // normalise the value
            if (Variables.normalizeValues) {
                value = StringNormalizer.webStringNormalization(value);
            }
        }

        // guess the type 
        ColumnType valueType = typeGuesser.guessTypeForValue(value, b.getUnitFromHeader());
        
        if (ListHandler.checkIfList(value)) {
            List<String> columnValues = Arrays.asList(ListHandler.splitList(value));
            b.addValue(row, columnValues, valueType);
        } else {            
            b.addValue(row, value, valueType);
        }
    }
}
