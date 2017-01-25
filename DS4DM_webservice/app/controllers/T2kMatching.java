package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spark_project.guava.collect.Lists;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;
import com.rapidminer.extension.json.JSONRelatedTablesResponse;
import com.rapidminer.extension.json.TableInformation;

import de.mannheim.uni.ds4dm.searcher.DS4DMBasicMatcher;
import de.mannheim.uni.searchJoin.HeaderSearcher;
import de.uni_mannheim.informatik.dws.ds4dm.match.data.MatchableTableColumn;
import de.uni_mannheim.informatik.dws.ds4dm.match.data.MatchableTableRow;
import de.uni_mannheim.informatik.dws.ds4dm.match.data.WebTables;
import de.uni_mannheim.informatik.dws.t2k.utils.io.ReadWriteGson;
import de.uni_mannheim.informatik.dws.t2k.webtables.Table;
import de.uni_mannheim.informatik.dws.t2k.webtables.TableRow;
import de.uni_mannheim.informatik.wdi.matching.Comparator;
import de.uni_mannheim.informatik.wdi.matching.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.wdi.matching.MatchingEngine;
import de.uni_mannheim.informatik.wdi.matching.SchemaMatchingRule;
import de.uni_mannheim.informatik.wdi.matching.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.matching.blocking.BlockingKeyGenerator;
import de.uni_mannheim.informatik.wdi.matching.blocking.StandardBlocker;
import de.uni_mannheim.informatik.wdi.model.Correspondence;
import de.uni_mannheim.informatik.wdi.model.DataSet;
import de.uni_mannheim.informatik.wdi.model.ResultSet;
import de.uni_mannheim.informatik.wdi.parallel.ParallelMatchingEngine;
import jersey.repackaged.com.google.common.collect.Maps;

public class T2kMatching {

	public static void match(File request, File fetchedTablesFolder, File response) {
		try {
			JSONRelatedTablesRequest jsonRequest = parseRequest(request);
			WebTables queryTable = constructQueryTable(jsonRequest.getQueryTable());
			
			DS4DMBasicMatcher matcher = new DS4DMBasicMatcher(jsonRequest);
			List<String> subjects = Lists.newArrayList();
			for(String s : matcher.getSubjectsFromQueryTable()) {
				subjects.add(s.toLowerCase());
			}
			matcher.setSubjectsFromQueryTable(subjects);
			JSONRelatedTablesResponse responseMapping = constructResponseObject(queryTable,matcher);
			WebTables candidateTables = findCandidates(matcher, fetchedTablesFolder);
			generateMatches(response, matcher, queryTable, responseMapping, candidateTables);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static JSONRelatedTablesRequest parseRequest(File request) throws JsonSyntaxException, IOException {
		JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
		ReadWriteGson<JSONRelatedTablesRequest> rwg = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
		qts = rwg.fromJson(request);
		return qts;
	}

	private static void generateMatches(File response, DS4DMBasicMatcher matcher, WebTables queryTable, JSONRelatedTablesResponse responseMapping, WebTables candidateTables) throws Exception {
		Map<String,Integer> subjectColumnIdxMap = initSubjectColumnIdxMap(candidateTables);
		MatchingEngine<MatchableTableRow, MatchableTableColumn> matchingEngine = new ParallelMatchingEngine<>();

		LinearCombinationMatchingRule<MatchableTableRow, MatchableTableColumn> ruleInstance = new LinearCombinationMatchingRule<>(0.6);
		Comparator<MatchableTableRow, MatchableTableColumn> levensteinSimilarityCompInstance = new Comparator<MatchableTableRow, MatchableTableColumn>() {

			private static final long serialVersionUID = 1L;

			@Override
			public double compare(MatchableTableRow record1, MatchableTableRow record2,
					Correspondence<MatchableTableColumn, MatchableTableRow> schemaCorrespondences) {
				String identifierRecord1 = record1.getIdentifier().substring(0,record1.getIdentifier().indexOf("~"));
				int subjectColumnIdx1 = subjectColumnIdxMap.get(identifierRecord1);
				int wIndex = record2.getIdentifier().indexOf("w")+1;
				if (matcher.getSubjectsFromQueryTable().get(Integer.parseInt(record2.getIdentifier().substring(wIndex))).equals(record1.get(subjectColumnIdx1)))  {
					return 1.0;
				}
				return 0.0;
			}
		};
		ruleInstance.addComparator(levensteinSimilarityCompInstance, 0.8);
		if (candidateTables.getRecords().get().isEmpty()) return;
		Blocker<MatchableTableRow, MatchableTableColumn> blocker;
		BlockingKeyGenerator<MatchableTableRow> blockingFunction;
		blockingFunction = new BlockingKeyGenerator<MatchableTableRow>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String getBlockingKey(MatchableTableRow instance) {
				// TODO return an index key defining a partition
				return "";
			}
		};

		blocker = new StandardBlocker<>(blockingFunction);
		ResultSet<Correspondence<MatchableTableRow,MatchableTableColumn>> results = matchingEngine.runIdentityResolution(candidateTables.getRecords(), queryTable.getRecords(), 
				null, ruleInstance, blocker);
		
		Collection<Correspondence<MatchableTableRow,MatchableTableColumn>> instanceCorrespondences = results.get();
		Iterator<Correspondence<MatchableTableRow,MatchableTableColumn>> iter = instanceCorrespondences.iterator();
		Map<Integer,List<Correspondence<MatchableTableRow,MatchableTableColumn>>> tablesCorrMapping = Maps.newConcurrentMap();
		
		while(iter.hasNext()) {
			Correspondence<MatchableTableRow,MatchableTableColumn> corr = (Correspondence<MatchableTableRow,MatchableTableColumn>)iter.next();
			List<MatchableTableRow> rows = Lists.newArrayList(corr.getFirstRecord(), corr.getSecondRecord());
			for(MatchableTableRow row : rows) {
				if(!tablesCorrMapping.containsKey(row.getTableId())) {
					tablesCorrMapping.put(corr.getFirstRecord().getTableId(), Lists.newArrayList(corr));
				} else {
					tablesCorrMapping.get(row.getTableId()).add(corr);
				}				
			}
		}

		List<TableInformation> tableInformation = Lists.newArrayList();
		for(int key : tablesCorrMapping.keySet()) {
			List<Correspondence<MatchableTableRow,MatchableTableColumn>> groupedCorrespondences = tablesCorrMapping.get(key);
			Map<String, com.rapidminer.extension.json.Correspondence> instanceCorrespondences2QueryTable = new HashMap<>();
			String tablename = "";
			for (Correspondence<MatchableTableRow,MatchableTableColumn> corr : groupedCorrespondences) {
				instanceCorrespondences2QueryTable.put(String.valueOf(corr.getSecondRecord().getRowNumber()), new com.rapidminer.extension.json.Correspondence(String.valueOf(
						corr.getFirstRecord().getRowNumber()), corr.getSimilarityScore()));
				tablename = corr.getFirstRecord().getIdentifier();
			}
			TableInformation ti = new TableInformation();
			ti.setInstancesCorrespondences2QueryTable(instanceCorrespondences2QueryTable);
			ti.setTableName(tablename);
			tableInformation.add(ti);
		}

		responseMapping.setRelatedTables(tableInformation);		
		ReadWriteGson<JSONRelatedTablesResponse> resp = new ReadWriteGson<JSONRelatedTablesResponse>(responseMapping);
		resp.writeJson(response);
	}
	
	private static Map<String,Integer> initSubjectColumnIdxMap(WebTables web) {		
		Map<String,Integer> subjectColumnIdxMap = Maps.newConcurrentMap();
		for(int i : web.getTables().keySet()) {
			Table t = web.getTables().get(i);
			TableRow tableRow = t.getRows().get(0);
			String tableIdentifier = tableRow != null ? tableRow.getIdentifier().substring(0,tableRow.getIdentifier().indexOf("~")) : ""; // avoid null pointer if table is empty for some reason
			subjectColumnIdxMap.put(tableIdentifier, t.getKeyIndex());
		}
		return subjectColumnIdxMap;
	}

	private static WebTables findCandidates(DS4DMBasicMatcher matcher, File tablesFolder) throws FileNotFoundException {
		HeaderSearcher hs = new HeaderSearcher("testConf.conf");
		List<String> att = matcher.getExtAtt();
		String [] headers = att.toArray(new String[att.size()]);
		Set<String> tableNames = hs.searchTablesByHeaders(headers);
		WebTables tables = WebTables.loadPortionOfWebTables(tablesFolder, true, false, tableNames);
		return tables;
	}
	
	private static WebTables constructQueryTable(List<List<String>> tableData) throws IOException {
		File tempFile = new File("public/exampleData/tempfile.json");
		Gson gson = new Gson();
		JsonObject relation = new JsonObject();
		JsonArray jsonTable  = new JsonArray();
		for(List<String> c : tableData) {
			JsonArray jsonColumn  = new JsonArray();
			for(String s : c) {
				jsonColumn.add(s);
			}
			jsonTable.add(jsonColumn);
		}
        relation.add("relation", jsonTable);
        relation.add("tableType", new JsonPrimitive("RELATION"));
        relation.add("hasHeader", new JsonPrimitive(true));
        relation.add("headerPosition", new JsonPrimitive("FIRST_ROW"));
        relation.add("tableOrientation", new JsonPrimitive("HORIZONTAL"));
        relation.add("hasKeyColumn", new JsonPrimitive(true));
        relation.add("keyColumnIndex", new JsonPrimitive(0));
        relation.add("headerRowIndex", new JsonPrimitive(0));
        FileWriter fileWriter = new FileWriter(tempFile);
        gson.toJson(relation, new JsonWriter(fileWriter));
		fileWriter.close();
		return WebTables.loadWebTables(tempFile, true, false);
	}
	
	private static JSONRelatedTablesResponse constructResponseObject(WebTables queryTable, DS4DMBasicMatcher matcher) {
		JSONRelatedTablesResponse responseMapping = new JSONRelatedTablesResponse();
//		Iterator<MatchableTableColumn> iter = queryTable.getSchema().get().iterator();
//		List<String> schema = Lists.newArrayList();
//		while(iter.hasNext()) {
//			schema.add(iter.next().getHeader().toString());
//		}
		responseMapping.setTargetSchema(matcher.getTargetSchema());
		responseMapping.setExtensionAttributes2TargetSchema(matcher.getE2t_str());
		responseMapping.setQueryTable2TargetSchema(matcher.getQ2t_str());
		Map<String, String> dataT = new HashMap<String, String>();
		for (int i = 0; i < matcher.getTargetSchema().size(); i++) {
			dataT.put(matcher.getTargetSchema().get(i), matcher.getTargetSchemaDataTypes().get(i).toString());
		}
		responseMapping.setDataTypes(dataT);
		return responseMapping;
	}
	
}
