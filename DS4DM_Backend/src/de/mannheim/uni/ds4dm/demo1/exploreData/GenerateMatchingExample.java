//package de.mannheim.uni.ds4dm.demo1.exploreData;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.google.gson.JsonSyntaxException;
//import com.rapidminer.extension.json.Correspondence;
//import com.rapidminer.extension.json.JSONRelatedTablesRequest;
//import com.rapidminer.extension.json.JSONRelatedTablesResponse;
//import com.rapidminer.extension.json.JSONTableResponse;
//import com.rapidminer.extension.json.TableInformation;
//
//import de.mannheim.uni.ds4dm.model.AnnotatedTable;
//import de.mannheim.uni.ds4dm.model.TableData;
//import de.mannheim.uni.ds4dm.model.TableData2TableDS4DM;
//import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
//
//public class GenerateMatchingExample {
//
//	public static void main(String[] args) {
//
//		File request = new File(
//				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/requestNew.json");
//		File selectedTableFolder = new File(
//				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/selected_tables_population_simplified");
//		
//		File responseFolder = new File(
//				"/Users/annalisa/Documents/DS4DM_local/useful data/USE_CASE_1/response_material");
//		responseFolder.mkdirs();
//		File fetchedTablesFolder = new File(responseFolder.getAbsolutePath()+"/fetchedTables");
//		fetchedTablesFolder.mkdirs();		
//		File response = new File(responseFolder.getAbsolutePath()+"/response.json");
//
//		try {
//
//			// query table
//			JSONRelatedTablesRequest qts = new JSONRelatedTablesRequest();
//			ReadWriteGson<JSONRelatedTablesRequest> rwg = new ReadWriteGson<JSONRelatedTablesRequest>(
//					qts);
//			qts = rwg.fromJson(request);
//
//			// construct target schema
//			// ***********************
//			// construct query table
//			List<String> queryTable = new ArrayList<String>();
//			// get all headers from query table
//			List<List<String>> tableData = qts.getQueryTable();
//			Iterator<List<String>> it = tableData.iterator();
//			while (it.hasNext()) {
//				queryTable.add(it.next().get(0).toLowerCase());
//			}
//
//			// extention attribute
//			List<String> extAtt = qts.getExtensionAttributes();
//
//			// concatenate extention attribute
//			List<String> targetSchema = new ArrayList<String>();
//			targetSchema.addAll(queryTable);
//
//			for (String e : extAtt)
//				targetSchema.add(e.toLowerCase());
//
//			// map query schema to target schema
//			HashMap<Integer, Integer> q2t = new HashMap<Integer, Integer>();
//			Map<String, String> q2t_str = new HashMap<String, String>();
//
//			for (int i = 0; i < queryTable.size(); i++) {
//				if (targetSchema.contains(queryTable.get(i))) {
//					q2t.put(i, targetSchema.indexOf(queryTable.get(i)));
//				}
//			}
//			for (Entry<Integer, Integer> e : q2t.entrySet()) {
//				q2t_str.put(e.getKey().toString(), e.getValue().toString());
//			}
//
//			// map extention attribute to target schema
//			HashMap<Integer, Integer> e2t = new HashMap<Integer, Integer>();
//			Map<String, String> e2t_str = new HashMap<String, String>();
//
//			for (int j = 0; j < extAtt.size(); j++) {
//				if (targetSchema.contains(extAtt.get(j))) {
//					e2t.put(j, targetSchema.indexOf(extAtt.get(j)));
//				}
//			}
//			for (Entry<Integer, Integer> e : e2t.entrySet()) {
//				e2t_str.put(e.getKey().toString(), e.getValue().toString());
//			}
//			// ***********************
//
//			// collect all subjects to match
//			// ***********************
//			List<String> subjectsFromQueryTable = qts.getQueryTable().get(
//					Integer.parseInt(qts.getKeyColumnIndex()));
//			// remove header
//			subjectsFromQueryTable.remove(0);
//			// ***********************
//
//			System.out.println("target schema: " + targetSchema);
//			System.out.println("subject column: " + subjectsFromQueryTable);
//
//			// construct response object
//			// ***********************
//			JSONRelatedTablesResponse responseMappimg = new JSONRelatedTablesResponse();
//			responseMappimg.setTargetSchema(targetSchema);
//			responseMappimg.setExtensionAttributes2TargetSchema(e2t_str);
//			responseMappimg.setQueryTable2TargetSchema(q2t_str);
//
//			// ***********************************
//
//			if (selectedTableFolder.isDirectory()) {
//				// read all relevant tables
//				// for each table
//
//				List<TableInformation> relevamntTables_ds4dm = new ArrayList<TableInformation>();
//
//				for (File f : selectedTableFolder.listFiles()) {
//					System.out.println("*** MATCHING TABLE ***" + f.getName());
//
//					try {
//						// create an object for the current table
//						TableInformation tm_ds4dm = new TableInformation();
//						tm_ds4dm.setTableName(f.getName());
//
//						// map schema
//
//						// TODO fill
//						Map<String, Correspondence> tableSchema2TargetSchema = new HashMap<String, Correspondence>();
//
//						AnnotatedTable relevantTable = AnnotatedTable
//								.fromJson(f);
//						TableData t = relevantTable.getTable();
//						
//						
//						JSONTableResponse t_sab = TableData2TableDS4DM.fromAnnotatedTable2JSONTableResponse(t, f.getName());
//						
//
//						String[] relTableHeadres = t.getColumnHeaders();
//						List<String> relTableHeadresList = Arrays
//								.asList(relTableHeadres);
//
//						for (int i = 0; i < relTableHeadres.length; i++) {
//							if (queryTable.contains(relTableHeadres[i]
//									.toLowerCase())) {
//								tableSchema2TargetSchema.put(Integer
//										.toString(queryTable
//												.indexOf(relTableHeadres[i]
//														.toLowerCase())),
//										new Correspondence(Integer.toString(i),
//												0.99));
//								System.out.println("mapped: "
//										+ queryTable.indexOf(relTableHeadres[i]
//												.toLowerCase()) + " --> " + i);
//							} else {
//								System.out.println(queryTable
//										+ " does not contain --> "
//										+ relTableHeadres[i].toLowerCase());
//
//							}
//						}
//						System.out.println("table: " + f.getName()
//								+ relTableHeadresList);
//
//						tm_ds4dm.setTableSchema2TargetSchema(tableSchema2TargetSchema);
//						System.out.println("scema correspondences: "
//								+ tm_ds4dm.getTableSchema2TargetSchema());
//
//						// map instances
//
//						Map<String, Correspondence> instancesCorrespondences2QueryTable = new HashMap<String, Correspondence>();
//
//						// get the subject column and check if present in query
//						// table
//						String[] col = t.getRelation()[t.getKeyColumnIndex()];
//						for (int i = 0; i < col.length; i++) {
//							if (i != t.getHeaderRowIndex()) {
//
//								if (subjectsFromQueryTable.contains(col[i])) {
//
//									// here subjectsFromQueryTable have the first row removed (as it's the header)
//									// so need to +1 on the index
//
//									String matched = Integer
//											.toString(subjectsFromQueryTable
//													.indexOf(col[i]) + 1);
//									instancesCorrespondences2QueryTable.put(
//											Integer.toString(i),
//											new Correspondence(matched, 0.99));
//
//									System.out.println("match: " + f.getName()
//											+ "\t" + Integer.toString(i) + "\t"
//											+ col[i] + "\t" + matched);
//								}
//							}
//						}
//
//						tm_ds4dm.setInstancesCorrespondences2QueryTable(instancesCorrespondences2QueryTable);
//						System.out
//								.println("instances correspondences: "
//										+ tm_ds4dm
//												.getInstancesCorrespondences2QueryTable());
//
//						relevamntTables_ds4dm.add(tm_ds4dm);
//						
//						ReadWriteGson<JSONTableResponse> resp = new ReadWriteGson<JSONTableResponse>(t_sab);
//						File current_table = new File(fetchedTablesFolder.getAbsolutePath()+"/"+f.getName());
//						resp.writeJson(current_table);
//						
//						
//					} catch (JsonSyntaxException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//
//				responseMappimg.setRelatedTables(relevamntTables_ds4dm);
//
//				ReadWriteGson<JSONRelatedTablesResponse> resp = new ReadWriteGson<JSONRelatedTablesResponse>(
//						responseMappimg);
//				resp.writeJson(response);
//
//			}
//		} catch (JsonSyntaxException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//}
