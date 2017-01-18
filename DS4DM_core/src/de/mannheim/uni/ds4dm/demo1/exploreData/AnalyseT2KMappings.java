package de.mannheim.uni.ds4dm.demo1.exploreData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonSyntaxException;

import de.mannheim.uni.ds4dm.model.AnnotatedTable;
import de.mannheim.uni.ds4dm.model.TableData;
import de.mannheim.uni.ds4dm.model.TableMapping;
import de.mannheim.uni.utils.MapUtil;
import de.mannheim.uni.utils.data.Pair;

public class AnalyseT2KMappings {

	public static void main(String[] args) {

//		String targetType = "http://dbpedia.org/ontology/Country";
//		String extentionAttribute = "population";
	
		String targetType = "http://dbpedia.org/ontology/Film";
		String extentionAttribute = "releasedate";
		
		
		File tableFolder = new File("/Users/annalisa/Documents/DS4DM_local/useful data/mappings");
		File selectedTableFolder = new File(
				"/Users/annalisa/Documents/DS4DM_local/useful data/selected_tables_population_june");
		
		AnalyseT2KMappings cat = new AnalyseT2KMappings(tableFolder,selectedTableFolder);
		
		Set<String> targetTypes = new HashSet<String>();

		cat.populateTypesAndProperties();
		
		// TODO uncomment next to limit the number of types - example for
		// testing with one type only
		targetTypes.add(targetType);
//		targetTypes.addAll(types.keySet());

		
		cat.selectMatchingTables(targetTypes, extentionAttribute);

	}
	
	
	private SortedMap<String, Integer> types;
	private Map<String, Map<String, Integer>> properties;
	private Map<String, Map<String, Set<String>>> propertiesLexicalizations;
	private File inputTableFolder;
	private File selectedTableFolder;
	 
	public AnalyseT2KMappings(File tableFolder, File selectedTableFolder) {
		super();
		this.inputTableFolder = tableFolder;
		this.selectedTableFolder = selectedTableFolder;
		this.types = new TreeMap<String, Integer>();
		this.properties = new HashMap<String, Map<String,Integer>>();
		this.propertiesLexicalizations = new HashMap<String, Map<String,Set<String>>>();

	}

	private void populateTypesAndProperties() {


		if (inputTableFolder.isDirectory()) {
			int files = 0;
			for (File f : inputTableFolder.listFiles()) {
				try {
					files++;
					AnnotatedTable fromJson = AnnotatedTable.fromJson(f);
					TableData t = fromJson.getTable();
					TableMapping m = fromJson.getMapping();
					int counter = 1;
					if (types.containsKey(m.getMappedClass().getFirst())) {
						counter = types.get(m.getMappedClass().getFirst()) + 1;
					}
					types.put(m.getMappedClass().getFirst(), counter);

					if (!properties.containsKey(m.getMappedClass().getFirst())) {
						properties.put(m.getMappedClass().getFirst(), new HashMap<String,Integer>());
						propertiesLexicalizations.put(m.getMappedClass().getFirst(), new HashMap<String,Set<String>>());
					}
					for (Entry<Integer, Pair<String, Double>> p : m.getMappedProperties().entrySet()) {
						int count =0;
						if (properties.get(m.getMappedClass().getFirst()).get(p.getValue().getFirst())!=null){
							count = properties.get(m.getMappedClass().getFirst()).get(p.getValue().getFirst());
						}
						properties.get(m.getMappedClass().getFirst()).put(p.getValue().getFirst(), count+1);
						
						if (propertiesLexicalizations.get(m.getMappedClass().getFirst()).get(p.getValue().getFirst())==null)
							propertiesLexicalizations.get(m.getMappedClass().getFirst()).put(p.getValue().getFirst(), new HashSet<String>());
						
						propertiesLexicalizations.get(m.getMappedClass().getFirst()).get(p.getValue().getFirst()).add(t.getColumnHeaders()[p.getKey()]);
						
 
					}

				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			Map<String, Integer> sorted = MapUtil.sortByValue(types);

			int check = 0;
			for (Entry<String, Integer> s : sorted.entrySet()) {
				System.out.println(s.getValue()+"\t"+s.getKey());
					for (Entry<String,Integer> p:properties.get(s.getKey()).entrySet())
				System.out.println("\t\t"+p.getKey()+"\t"+p.getValue()+"\t"+this.propertiesLexicalizations.get(s.getKey()).get(p.getKey()));

				check = check + s.getValue();
			}
			System.out.println("********* Counted "+check + " types in " + files+" json tables **********");

		}
	}

	private void selectMatchingTables(Set<String> targetTypes, String extentionAttribute) {
		if (inputTableFolder.isDirectory()) {
			int i = 0;
			int matching = 0;

			for (File f : inputTableFolder.listFiles()) {
				i++;
				try {
					AnnotatedTable fromJson = AnnotatedTable.fromJson(f);
					TableData t = fromJson.getTable();
					TableMapping m = fromJson.getMapping();
					if (targetTypes.contains(m.getMappedClass().getFirst())) {
						matching++;
						System.out.print(matching + " (" + i + ") " + f.getName() + "\t");
						System.out.println();

						boolean headerMatch = false;
						
						for (int col =0; col< t.getColumnHeaders().length; col++){
							String head = t.getColumnHeaders()[col];
							String mapped = "";
							try {
								mapped = m.getMappedProperties().get(col).getFirst();
							} catch (Exception e) {
							}
							if (head.toLowerCase().contains(extentionAttribute)||mapped.toLowerCase().contains(extentionAttribute)) {
								headerMatch = true;
								System.out.println("col. "+col+ "\t" + mapped
										 + "\t" + t.getColumnHeaders()[col]);
							}
						}

						System.out.println();
						if (headerMatch) {
							try {
								String newF = selectedTableFolder.getAbsolutePath() + File.separator + f.getName();
								FileUtils.copyFile(f, new File(newF));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
