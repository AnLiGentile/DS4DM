package de.mannheim.uni.ds4dm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rapidminer.extension.json.JSONTableResponse;
import com.rapidminer.extension.json.MetaDataTable;

import de.mannheim.uni.types.ColumnTypeGuesser.ColumnDataType;
import de.mannheim.uni.utils.TableColumnTypeGuesser;


/**
 * @author annalisa
 *
 */
public class TableData2TableDS4DM {
	
	public static JSONTableResponse fromAnnotatedTable2JSONTableResponse(TableData t, String tableName, MetaDataTable meta) {

		
		//TODO fill metadata
//		double tableScore = 0;
//		Date lastModified = new Date(System.currentTimeMillis());
//		String URL = "";
//		String textBeforeTable = "";
//		String textAfterTable = "";
//		String title = "";
//		double coverage = 0;
//		double ratio = 0;
//		double trust = 0;
//		double emptyValues = 0;
		
		JSONTableResponse t_sab = new JSONTableResponse();

		//TODO fill metadata
		t_sab.setMetaData(meta);

		t_sab.setHasHeader(true);
		t_sab.setHasKeyColumn(true);
		//TODO ad-hoc, but it is always 0
		t_sab.setHeaderRowIndex(Integer.toString(0));
		String indexCol = t.getKeyColumnIndex() +"_"+t.getColumnHeaders()[t.getKeyColumnIndex()];
		t_sab.setKeyColumnIndex(indexCol);
		List<List<String>> relation = new ArrayList<List<String>>();

		int count = 0;
		for (String e[]: t.getRelation()){
//			List<String> val = Arrays.asList(e);
			List<String> val_WithRenamedHeader = new ArrayList<String>();
			val_WithRenamedHeader.add(count +"_"+e[0]);

			for (int c =1; c<e.length; c++){
			val_WithRenamedHeader.add(e[c]);}
			
			relation.add(val_WithRenamedHeader);
			count++;
		}
		t_sab.setRelation(relation);
		
		//TODO fill setDataTypes
		Map <String, String> dataT = guessTypes(relation);
		t_sab.setDataTypes(dataT);

		t_sab.setTableName(tableName);
//		URL = t.getUrl();
		meta.setURL(t.getUrl());

//		MetaDataTable meta = new MetaDataTable(tableScore, lastModified.toString(), URL, textBeforeTable, textAfterTable,
//				title, coverage, ratio, trust, emptyValues);
		t_sab.setMetaData(meta);
		return t_sab;
	}
	
	

	
	
	public static Map<String, String> guessTypes (List<List<String>> relation){
		Map<String, String> dataTypes = new HashMap<String, String>();
		TableColumnTypeGuesser tctg = new TableColumnTypeGuesser();
		for (List<String> e: relation){
			ColumnDataType type = ColumnDataType.string;;
			String columnName = e.get(0);
			List<String> columnValues = new ArrayList<String>();
			columnValues.addAll(e);
			columnValues.remove(0);
			type = tctg.guessTypeForColumn(columnValues, columnName, false, null);
			dataTypes.put(columnName, type.toString());
		}
		return dataTypes;
	}
	
	
	
	

}
