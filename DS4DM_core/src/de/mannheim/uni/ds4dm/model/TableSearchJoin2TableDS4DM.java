//package de.mannheim.uni.ds4dm.model;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.rapidminer.extension.json.JSONTableResponse;
//import com.rapidminer.extension.json.MetaDataTable;
//
//import de.mannheim.uni.model.Table;
//import de.mannheim.uni.model.TableColumn;
//import de.mannheim.uni.types.ColumnTypeGuesser.ColumnDataType;
//import de.mannheim.uni.utils.TableColumnTypeGuesser;
//
//
///**
// * @author annalisa
// *
// */
//public class TableSearchJoin2TableDS4DM {
//	
//	public static JSONTableResponse fromAnnotatedTable2JSONTableResponse(Table t, String tableName) {
//
//		JSONTableResponse t_sab = new JSONTableResponse();
//
//		//TODO fill metadata properly
//		double tableScore = 0;
//		Date lastModified = new Date(System.currentTimeMillis());
//		String URL = "";
//		String textBeforeTable = "";
//		String textAfterTable = "";
//		String title = "";
//		double coverage = 0;
//		double ratio = 0;
//		double trust = 0;
//		double emptyValues = t.getNmNulls();
//		
//
//				
//				
//
//		t_sab.setHasHeader(true);
//		t_sab.setHasKeyColumn(true);
//		//TODO ad-hoc, but it is always 0
//		t_sab.setHeaderRowIndex(Integer.toString(0));
//		List<List<String>> relation = new ArrayList<List<String>>();
//
//		int count = 0;
//		List<TableColumn> cols = t.getColumns();
//		Map <String, String> dataT = new HashMap<String, String>();
//
//		for (TableColumn e : cols){
////			List<String> val = Arrays.asList(e);
//			List<String> val_WithRenamedHeader = new ArrayList<String>();
//			if (e.isKey()){
//				t_sab.setKeyColumnIndex(count +"_"+e.getHeader());
//			}
//				
//			val_WithRenamedHeader.add(count +"_"+e.getHeader());
//			dataT.put(count +"_"+e.getHeader(), e.getDataType().toString());
//
//			//TODO check with Petar if this is the logic he implemented
//			//this might miss null values at the end of the column
//			int size = e.getValues().size();
//			for (int i = 2; i <size; i++){
//			val_WithRenamedHeader.add(e.getValues().get(i));}
//			
//			relation.add(val_WithRenamedHeader);
//			count++;
//		}
//		t_sab.setRelation(relation);
//		t_sab.setDataTypes(dataT);
//
//		//TODO using types from Petar class
////		dataT = guessTypes(relation);
////		t_sab.setDataTypes(dataT);
//
//		t_sab.setTableName(tableName);
//		URL = t.getFullPath();
//		
//		MetaDataTable meta = new MetaDataTable(tableScore, lastModified, URL, textBeforeTable, textAfterTable,
//				title, coverage, ratio, trust, emptyValues);
//		t_sab.setMetaData(meta);
//
//		return t_sab;
//	}
//	
//	
//
//	
//	
//	public static Map<String, String> guessTypes (List<List<String>> relation){
//		Map<String, String> dataTypes = new HashMap<String, String>();
//		TableColumnTypeGuesser tctg = new TableColumnTypeGuesser();
//		for (List<String> e: relation){
//			ColumnDataType type = ColumnDataType.string;;
//			String columnName = e.get(0);
//			List<String> columnValues = new ArrayList<String>();
//			columnValues.addAll(e);
//			columnValues.remove(0);
//			type = tctg.guessTypeForColumn(columnValues, columnName, false, null);
//			dataTypes.put(columnName, type.toString());
//		}
//		return dataTypes;
//	}
//	
//	
//	
//	
//
//}
