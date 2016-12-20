package de.mannheim.uni.ds4dm.searcher.de.mannheim.uni.ds4dm.searcher.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonSyntaxException;
import com.rapidminer.extension.json.Correspondence;
import com.rapidminer.extension.json.JSONRelatedTablesRequest;
import com.rapidminer.extension.json.JSONRelatedTablesResponse;
import com.rapidminer.extension.json.JSONTableResponse;
import com.rapidminer.extension.json.TableInformation;

import de.mannheim.uni.ds4dm.model.TableData;
import de.mannheim.uni.ds4dm.model.TableData2TableDS4DM;
import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromJsonFolder;
import de.mannheim.uni.ds4dm.searcher.CandidateBuilder_fromLuceneIndex;
import de.mannheim.uni.ds4dm.searcher.DS4DMBasicMatcher;
import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
import de.mannheim.uni.normalizer.StringNormalizer;

public class GenerateMatchingExample_withKeywords {

	public static void main(String[] args) {

		// File request = new File(
		// "/Users/annalisa/Documents/DS4DM_local/useful
		// data/USE_CASE_1/requestNewNumber.json");
		// File selectedTableFolder = new File(
		// "/Users/annalisa/Documents/DS4DM_local/useful
		// data/USE_CASE_1/selected_tables_population_simplified");
		// File selectedTableFolder = new File(
		// "/Users/annalisa/Documents/DS4DM_local/useful data/mappings");
		File selectedTableFolder = new File(
				"/Users/annalisa/Documents/AllWorkspaces/ds4dm/T2Kv2/T2K.Match/testJsonRes");

		File responseFolder = new File(
				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/response_material");
		responseFolder.mkdirs();
		File fetchedTablesFolder = new File(responseFolder.getAbsolutePath() + "/fetchedTables");
		fetchedTablesFolder.mkdirs();
		File response = new File(responseFolder.getAbsolutePath() + "/response.json");

		CandidateBuilder_fromJsonFolder candBuilder = new CandidateBuilder_fromJsonFolder(selectedTableFolder);

		File request = new File("request.json");

		JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
		ReadWriteGson<JSONRelatedTablesRequest> rwj = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
		try {
			qts = rwj.fromJson(request);
		} catch (JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DS4DMBasicMatcher matcher = new DS4DMBasicMatcher(qts);

		if (!fetchedTablesFolder.exists())
			fetchedTablesFolder.mkdirs();

		// GenerateMatchingExample_withKeywords.serchTables(request,
		// fetchedTablesFolder, response, candBuilder);

		serchTables(request, fetchedTablesFolder, response, candBuilder);

	}

	public static void serchTables(File request, File fetchedTablesFolder, File response,
			CandidateBuilder_fromJsonFolder candBuilder) {
		try {

			DS4DMBasicMatcher matcher = constructQueryTable(request);

			JSONRelatedTablesResponse responseMappimg = constructResponseObject(matcher);

			// ***********************************

			Map<String, TableData> candidates = candBuilder.finCandidates(matcher);

			System.out.println("relevant tables: " + candidates.keySet());

			{
				// read all relevant tables
				// for each table

				generateMatches(fetchedTablesFolder, response, matcher, responseMappimg, candidates);

			}
		} catch (JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void serchTables_fromLucene(File request, File fetchedTablesFolder, File response,
			CandidateBuilder_fromLuceneIndex candBuilder) {

		// query table
		DS4DMBasicMatcher matcher = null;
		try {
			matcher = constructQueryTable(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (matcher != null) {
			// construct response object
			JSONRelatedTablesResponse responseMappimg = constructResponseObject(matcher);

			// fetch candidate tables
			// TODO check why values are not populated properly
			Map<String, TableData> candidates = candBuilder.finCandidates(matcher);
			System.out.println("relevant tables: " + candidates.keySet());

			{
				try {
					generateMatches(fetchedTablesFolder, response, matcher, responseMappimg, candidates);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	public static void serchTables_fromFolder(File request, File fetchedTablesFolder, File response,
			CandidateBuilder_fromJsonFolder candBuilder) {

		// query table
		DS4DMBasicMatcher matcher = null;
		try {
			matcher = constructQueryTable(request);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (matcher != null) {
			// construct response object
			JSONRelatedTablesResponse responseMappimg = constructResponseObject(matcher);

			// fetch candidate tables
			Map<String, TableData> candidates = candBuilder.finCandidates(matcher);
			System.out.println("relevant tables: " + candidates.keySet());

			{
				try {
					generateMatches(fetchedTablesFolder, response, matcher, responseMappimg, candidates);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private static void generateMatches(File fetchedTablesFolder, File response, DS4DMBasicMatcher matcher,
			JSONRelatedTablesResponse responseMappimg, Map<String, TableData> candidates) throws IOException {

		// read all relevant tables
		// for each table

		List<TableInformation> relevamntTables_ds4dm = new ArrayList<TableInformation>();

		for (Entry<String, TableData> table : candidates.entrySet()) {
			// System.out.println("*** MATCHING TABLE ***"+
			// table.getKey());//add table name to TableData

			if (table.getValue().getRelation() != null) {
				try {
					// create an object for the current table
					TableInformation tm_ds4dm = new TableInformation();
					// tm_ds4dm.setTableName(f.getName());

					// map schema

					Map<String, Correspondence> tableSchema2TargetSchema = new HashMap<String, Correspondence>();

					JSONTableResponse t_sab = TableData2TableDS4DM
							.fromAnnotatedTable2JSONTableResponse(table.getValue(), table.getKey());

					String[] relTableHeadres = table.getValue().getColumnHeaders();
		
					List<String> relTableHeadresList = Arrays.asList(relTableHeadres);

					for (int i = 0; i < relTableHeadres.length; i++) {
						String currentAttribute = StringNormalizer.format(relTableHeadres[i]);
						if (matcher.getNormalizedTargetSchema().contains(currentAttribute)) {
							int matchedIndex = matcher.getNormalizedTargetSchema().indexOf(currentAttribute);

							tableSchema2TargetSchema.put(Integer.toString(i) + "_" + relTableHeadres[i],
									new Correspondence(matcher.getTargetSchema().get(matchedIndex), 0.99));
							// TODO remove the index from the name, use these
							// instead of previous
							// tableSchema2TargetSchema.put(relTableHeadres[i],
							// new
							// Correspondence(matcher.getTargetSchema().get(matchedIndex),
							// 0.99));
							// System.out.println("mapped: "
							// + matchedIndex + " --> " + i);
						}
						// else {
						// System.out.println(matcher.getNormalizedTargetSchema()
						// + " does not contain --> "
						// + relTableHeadres[i]);
						// }
					}

					tm_ds4dm.setTableName(table.getKey());
					tm_ds4dm.setTableSchema2TargetSchema(tableSchema2TargetSchema);

					// map instances

					Map<String, Correspondence> instancesCorrespondences2QueryTable = new HashMap<String, Correspondence>();

					// get the subject column and check if present in query
					// table
					String[] col = table.getValue().getRelation()[table.getValue().getKeyColumnIndex()];
					for (int i = 0; i < col.length; i++) {
						if (i != table.getValue().getHeaderRowIndex()) {

							if (matcher.getSubjectsFromQueryTable().contains(col[i])) {

								// here subjectsFromQueryTable have the first
								// row removed (as it's the header)
								// so need to +1 on the index

								String matched = Integer
										.toString(matcher.getSubjectsFromQueryTable().indexOf(col[i]) + 1);
								instancesCorrespondences2QueryTable.put(Integer.toString(i),
										new Correspondence(matched, 0.99));

								// System.out.println("match: " + table.getKey()
								// + "\t" + Integer.toString(i) + "\t"
								// + col[i] + "\t" + matched);
							}
						}
					}

					if (!instancesCorrespondences2QueryTable.isEmpty()) {

						tm_ds4dm.setInstancesCorrespondences2QueryTable(instancesCorrespondences2QueryTable);
						relevamntTables_ds4dm.add(tm_ds4dm);
						ReadWriteGson<JSONTableResponse> resp = new ReadWriteGson<JSONTableResponse>(t_sab);
						File current_table = new File(fetchedTablesFolder.getAbsolutePath() + "/" + table.getKey());
						resp.writeJson(current_table);
						System.out.println("**Matched table: " + table.getKey() + relTableHeadresList);
						System.out.println("schema correspondences: " + tm_ds4dm.getTableSchema2TargetSchema());
						System.out.println(
								"instances correspondences: " + tm_ds4dm.getInstancesCorrespondences2QueryTable());
					}

				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				System.err.println("Empty relation in table: " + table.getKey());
			}
		}

		responseMappimg.setRelatedTables(relevamntTables_ds4dm);

		ReadWriteGson<JSONRelatedTablesResponse> resp = new ReadWriteGson<JSONRelatedTablesResponse>(responseMappimg);
		resp.writeJson(response);

	}

	private static DS4DMBasicMatcher constructQueryTable(File request) throws IOException {
		// query table

		try {
			JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
			ReadWriteGson<JSONRelatedTablesRequest> rwg = new ReadWriteGson<JSONRelatedTablesRequest>(qts);
			qts = rwg.fromJson(request);

			DS4DMBasicMatcher matcher = new DS4DMBasicMatcher(qts);
			return matcher;

		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static JSONRelatedTablesResponse constructResponseObject(DS4DMBasicMatcher matcher) {
		// construct response object
		// ***********************
		JSONRelatedTablesResponse responseMappimg = new JSONRelatedTablesResponse();
		responseMappimg.setTargetSchema(matcher.getTargetSchema());
		responseMappimg.setExtensionAttributes2TargetSchema(matcher.getE2t_str());
		responseMappimg.setQueryTable2TargetSchema(matcher.getQ2t_str());
		Map<String, String> dataT = new HashMap<String, String>();
		for (int i = 0; i < matcher.getTargetSchema().size(); i++) {
			dataT.put(matcher.getTargetSchema().get(i), matcher.getTargetSchemaDataTypes().get(i).toString());
		}
		responseMappimg.setDataTypes(dataT);
		return responseMappimg;
	}

}
