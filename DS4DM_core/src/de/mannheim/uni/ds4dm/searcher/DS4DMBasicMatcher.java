package de.mannheim.uni.ds4dm.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.rapidminer.extension.json.JSONRelatedTablesRequest;
import com.rapidminer.extension.json.JSONRelatedTablesResponse;
import com.rapidminer.extension.json.TableInformation;

import de.mannheim.uni.normalizer.StringNormalizer;
import de.mannheim.uni.types.ColumnTypeGuesser.ColumnDataType;
import de.mannheim.uni.utils.TableColumnTypeGuesser;

public class DS4DMBasicMatcher implements Matcher {

	private List<String> queryTable;
	private List<String> extAtt;
	private List<String> targetSchema;
	private List<String> normalizedTargetSchema;
	private List<String> subjectsFromQueryTable;
	private Map<String, String> q2t_str; // map query schema to target schema
	private Map<String, String> e2t_str; // map extention attribute to target
	
	private Map<Integer, Integer> q2t; // map query schema to target schema
	private Map<Integer, Integer> e2t; // map extention attribute to target

	private List<ColumnDataType> targetSchemaDataTypes = new ArrayList<ColumnDataType>();
	
	private int maximalNumberOfTables;
	
	public int getMaximalNumberOfTables() {
		return maximalNumberOfTables;
	}

	public void setMaximalNumberOfTables(int maximalNumberOfTables) {
		this.maximalNumberOfTables = maximalNumberOfTables;
	}

	// schema
	public DS4DMBasicMatcher(JSONRelatedTablesRequest qts) {
		super();
		// construct target schema
		// ***********************
		// construct query table
		this.queryTable = new ArrayList<String>();
		// get all headers from query table
		List<List<String>> tableData = qts.getQueryTable();
		Iterator<List<String>> it = tableData.iterator();
		while (it.hasNext()) {
			this.queryTable.add(it.next().get(0));
		}

		// extention attribute
		this.extAtt = qts.getExtensionAttributes();

		// concatenate extention attribute
		this.targetSchema = new ArrayList<String>();
		targetSchema.addAll(queryTable);

		for (String e : extAtt)
			targetSchema.add(e);

		this.normalizedTargetSchema = new ArrayList<String>();
		for (String e : this.targetSchema) {
			normalizedTargetSchema.add(StringNormalizer.format(e));
		}

		// TODO use init to make correspondences between indexes
		initWithAttributeNames(qts);

		// ***********************

		// collect all subjects to match
		// ***********************
		this.subjectsFromQueryTable = qts.getQueryTable().get(
				Integer.parseInt(qts.getKeyColumnIndex()));
		// remove header
		subjectsFromQueryTable.remove(0);
		// ***********************
		
		// guess types 
		this.targetSchemaDataTypes = new ArrayList<ColumnDataType>();
		TableColumnTypeGuesser tctg = new TableColumnTypeGuesser();
		for (int i = 0; i < this.targetSchema.size(); i++){
			Integer mapping = q2t.get(i);

			ColumnDataType type;

			if (mapping!=null){

			List<String> valuesFromQueryTable = qts.getQueryTable().get(mapping);

			type = tctg.guessTypeForColumn(valuesFromQueryTable, this.targetSchema.get(i), false, null);
			}else{
				type = ColumnDataType.string;
			}
			this.targetSchemaDataTypes.add(type);
		}
		
		//set maximalNumberOfTables
		this.maximalNumberOfTables = qts.getMaximalNumberOfTables();
		
		System.out.println("target schema: " + targetSchema);
		System.out.println("subject column: " + subjectsFromQueryTable);
		System.out.println("max number of tables: " + maximalNumberOfTables);

	}
	
	public Map<String, String> getE2t_str() {
		return e2t_str;
	}
	
	public List<String> getExtAtt() {
		return extAtt;
	}

	public List<String> getNormalizedTargetSchema() {
		return normalizedTargetSchema;
	}

	public Map<Integer, Integer> getQ2t() {
		return q2t;
	}

	public Map<String, String> getQ2t_str() {
		return q2t_str;
	}

	public List<String> getQueryTable() {
		return queryTable;
	}

	public List<String> getSubjectsFromQueryTable() {
		return subjectsFromQueryTable;
	}

	public List<String> getTargetSchema() {
		return targetSchema;
	}

	public List<ColumnDataType> getTargetSchemaDataTypes() {
		return targetSchemaDataTypes;
	}

	@Override
	public JSONRelatedTablesResponse match(JSONRelatedTablesRequest queryTable,
			Set<TableInformation> candidates) {

		return null;
	}

	public void setE2t_str(Map<String, String> e2t_str) {
		this.e2t_str = e2t_str;
	}

	public void setExtAtt(List<String> extAtt) {
		this.extAtt = extAtt;
	}

	public void setNormalizedTargetSchema(List<String> normalizedTargetSchema) {
		this.normalizedTargetSchema = normalizedTargetSchema;
	}

	public void setQ2t(Map<Integer, Integer> q2t) {
		this.q2t = q2t;
	}

	public void setQ2t_str(Map<String, String> q2t_str) {
		this.q2t_str = q2t_str;
	}

	public void setQueryTable(List<String> queryTable) {
		this.queryTable = queryTable;
	}

	public void setSubjectsFromQueryTable(List<String> subjectsFromQueryTable) {
		this.subjectsFromQueryTable = subjectsFromQueryTable;
	}

	public void setTargetSchema(List<String> targetSchema) {
		this.targetSchema = targetSchema;
	}

	public void setTargetSchemaDataTypes(List<ColumnDataType> targetSchemaDataTypes) {
		this.targetSchemaDataTypes = targetSchemaDataTypes;
	}

	@Deprecated
	private void init(JSONRelatedTablesRequest qts) {

		// map query schema to target schema
		HashMap<Integer, Integer> q2t = new HashMap<Integer, Integer>();
		this.q2t_str = new HashMap<String, String>();
		List<String> queryTable = new ArrayList<String>();
		for (String e : this.queryTable) {
			queryTable.add(StringNormalizer.format(e));
		}

		for (int i = 0; i < queryTable.size(); i++) {
			if (targetSchema.contains(queryTable.get(i))) {
				q2t.put(i, targetSchema.indexOf(queryTable.get(i)));
			}
		}
		for (Entry<Integer, Integer> e : q2t.entrySet()) {
			q2t_str.put(e.getKey().toString(), e.getValue().toString());
		}

		// map extention attribute to target schema
		HashMap<Integer, Integer> e2t = new HashMap<Integer, Integer>();
		this.e2t_str = new HashMap<String, String>();

		for (int j = 0; j < extAtt.size(); j++) {
			if (targetSchema.contains(extAtt.get(j))) {
				e2t.put(j, targetSchema.indexOf(extAtt.get(j)));
			}
		}
		for (Entry<Integer, Integer> e : e2t.entrySet()) {
			e2t_str.put(e.getKey().toString(), e.getValue().toString());
		}
	}

	private void initWithAttributeNames(JSONRelatedTablesRequest qts) {

		// map query schema to target schema
		this.q2t = new HashMap<Integer, Integer>();
		this.q2t_str = new HashMap<String, String>();

		List<String> queryTable = new ArrayList<String>();
		for (String e : this.queryTable) {
			queryTable.add(StringNormalizer.format(e));
		}
		List<String> extAtt = new ArrayList<String>();
		for (String e : this.extAtt) {
			extAtt.add(StringNormalizer.format(e));
		}

		for (int i = 0; i < queryTable.size(); i++) {
			if (this.normalizedTargetSchema.contains(queryTable.get(i))) {
				q2t_str.put(this.queryTable.get(i), this.targetSchema
						.get(this.normalizedTargetSchema.indexOf(queryTable.get(i))));
				q2t.put(i, this.normalizedTargetSchema.indexOf(queryTable.get(i)));
			}
		}

		// map extention attribute to target schema
		this.e2t = new HashMap<Integer, Integer>();
		this.e2t_str = new HashMap<String, String>();

		for (int j = 0; j < extAtt.size(); j++) {
			if (this.normalizedTargetSchema.contains(extAtt.get(j))) {
				e2t_str.put(this.extAtt.get(j), this.targetSchema
						.get(this.normalizedTargetSchema.indexOf(extAtt.get(j))));
				e2t.put(j, this.normalizedTargetSchema.indexOf(extAtt.get(j)));
			}
		}

	}

}
