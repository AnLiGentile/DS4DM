package de.mannheim.uni.ds4dm.demo1.exploreData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.gson.JsonSyntaxException;

import au.com.bytecode.opencsv.CSVReader;
import de.mannheim.uni.ds4dm.model.AnnotatedTable;
import de.mannheim.uni.ds4dm.model.TableData;
import de.mannheim.uni.ds4dm.model.TableMapping;
import de.mannheim.uni.utils.MapUtil;
import de.mannheim.uni.utils.MapUtils;

/**
 * @author annalisa
 * 
 */
public class AnalyzeGS {

	public static void main(String[] args) {

//		String targetType = "http://dbpedia.org/ontology/Country";
//		String extentionAttribute = "population";
	
//		String targetType = "http://dbpedia.org/ontology/Film";
//		String extentionAttribute = "releasedate";
		
		
		File tableFolder = new File("/Users/annalisa/Documents/DS4DM_local/useful data/goldstandard/context_json_url");
		String propertiesMatchFolder = "/Users/annalisa/Documents/DS4DM_local/useful data/goldstandard/gs_properties_num_new";
		File instancesMatchFolder = new File("/Users/annalisa/Documents/DS4DM_local/useful data/goldstandard/gs_instances_new");
		String conceptMatchFile = "/Users/annalisa/Documents/DS4DM_local/useful data/goldstandard/gs_classes_proper.csv";
		
		AnalyzeGS cat = new AnalyzeGS(tableFolder, propertiesMatchFolder, instancesMatchFolder, conceptMatchFile);
		
//		Set<String> targetTypes = new HashSet<String>();

		cat.populateTypesPropertiesAndInstances();
		
		// TODO uncomment next to limit the number of types - example for
		// testing with one type only
//		targetTypes.add(targetType);
//		targetTypes.addAll(types.keySet());

//		cat.printInstancesLex();
//		cat.printInstances();

		cat.printQuerySeeds();
//		cat.selectMatchingTables(targetTypes, extentionAttribute);

	}
	
	
	private SortedMap<String, Integer> types;
	private Map<String, Map<String, Integer>> properties;
	private Map<String, Map<String, Set<String>>> propertiesLexicalizations;
	private Map<String, Map<String, Set<String>>> propertiesInTables;
	
	private Map<String, Map<String, Integer>> instances;
	private Map<String, Map<String, Set<String>>> instancesLexicalizations;
	private Map<String, Map<String, Set<String>>> instancesInTables;
	
	private Map<String, Set<String>> lexicalizationOccurrences;

	
	
	private File inputTableFolder;
	private String inputPropertiesMatchFolder;
	private File inputInstancesMatchFolder;
	private String inputConceptMatchFile;
	
	public AnalyzeGS(File tableFolder, String propertiesMatchFolder, File instancesMatchFolder, String conceptMatchFile) {
		super();
		this.inputTableFolder = tableFolder;
		this.inputPropertiesMatchFolder = propertiesMatchFolder;
		this.inputInstancesMatchFolder = instancesMatchFolder;
		this.inputConceptMatchFile = conceptMatchFile;

		this.types = new TreeMap<String, Integer>();
		this.properties = new HashMap<String, Map<String,Integer>>();
		this.propertiesLexicalizations = new HashMap<String, Map<String,Set<String>>>();
		this.propertiesInTables = new HashMap<String, Map<String,Set<String>>>();
		
		this.instances = new HashMap<String, Map<String,Integer>>();
		this.instancesLexicalizations = new HashMap<String, Map<String,Set<String>>>();
		this.instancesInTables = new HashMap<String, Map<String,Set<String>>>();
		this.lexicalizationOccurrences = new HashMap<String,Set<String>>();
	}

	private void populateInstances(String tableName, String type) {
		//populate properties
		
		if (!instances.containsKey(type)) {
			instances.put(type, new HashMap<String,Integer>());
			instancesLexicalizations.put(type, new HashMap<String,Set<String>>());
			instancesInTables.put(type, new HashMap<String,Set<String>>());
}
		 CSVReader InstReader;
			try {
				InstReader = new CSVReader(new FileReader(this.inputInstancesMatchFolder+File.separator+tableName));
			     String [] nextInst;
			     while ((nextInst = InstReader.readNext()) != null) {
											int count =0;
											String inst = nextInst[0];
											String lexicalization = nextInst[1].replaceAll("\\s+", " ").trim();

						if (instances.get(type).get(inst)!=null){
							count = instances.get(type).get(inst);
						}
						instances.get(type).put(inst, count+1);
						
						if (instancesLexicalizations.get(type).get(inst)==null){
							instancesLexicalizations.get(type).put(inst, new HashSet<String>());
							instancesInTables.get(type).put(inst, new HashSet<String>());

						}
						instancesLexicalizations.get(type).get(inst).add(lexicalization);
						instancesInTables.get(type).get(inst).add(tableName);

						
			     }     

			     
			     for (Entry<String, Map<String, Set<String>>> l : instancesLexicalizations.entrySet()){
			    	 for ( Entry<String, Set<String>> s : l.getValue().entrySet()){
			    		 for(String lex : s.getValue()){
			    	 if (!this.lexicalizationOccurrences.containsKey(lex))
			    		 this.lexicalizationOccurrences.put(lex, new HashSet<String> ());
			    	 this.lexicalizationOccurrences.get(lex).add(s.getKey());
			    	 }}
			     }
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void populateProperties(String tableName, String type) {
		//populate properties
		
		if (!properties.containsKey(type)) {
		properties.put(type, new HashMap<String,Integer>());
		propertiesLexicalizations.put(type, new HashMap<String,Set<String>>());
		propertiesInTables.put(type, new HashMap<String,Set<String>>());
}
		 CSVReader propReader;
			try {
				propReader = new CSVReader(new FileReader(this.inputPropertiesMatchFolder+File.separator+tableName));
			     String [] nextProp;
			     while ((nextProp = propReader.readNext()) != null) {
											int count =0;
											String property = nextProp[0];
											String header = nextProp[1].replaceAll("\\s+", " ").trim();

						if (properties.get(type).get(property)!=null){
							count = properties.get(type).get(property);
						}
						properties.get(type).put(property, count+1);
						
						if (propertiesLexicalizations.get(type).get(property)==null){
							propertiesLexicalizations.get(type).put(property, new HashSet<String>());
							propertiesInTables.get(type).put(property, new HashSet<String>());

						}
						propertiesLexicalizations.get(type).get(property).add(header);
						propertiesInTables.get(type).get(property).add(tableName);

			     }     

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void populateTypesPropertiesAndInstances() {


		if (inputTableFolder.isDirectory()) {
			int files = 0;
			

		     CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(this.inputConceptMatchFile));
			     String [] nextLine;
			     while ((nextLine = reader.readNext()) != null) {
						files++;
			        // nextLine[] is an array of values from the line
			        int counter = 1;
			        
			        String tableName = nextLine[0].replace(".tar.gz", ".csv");
			        String type = nextLine[2];
			        
					if (types.containsKey(type)){
						counter = types.get(type) + 1;
					}
					types.put(type, counter);
					
					
					populateProperties(tableName, type);
					populateInstances(tableName, type);
					
			     }
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			     

	}
	}
	
	private int printInstances() {
		Map<String, Integer> sorted = MapUtil.sortByValue(types);

		int check = 0;
		for (Entry<String, Integer> s : sorted.entrySet()) {
			System.out.println(s.getValue()+"\t"+s.getKey());
			
			Map<String, Integer> sortedInstances = MapUtil.sortByValue(instances.get(s.getKey()));
				for (Entry<String,Integer> p:sortedInstances.entrySet())
			System.out.println("\t\t"+p.getKey()+"\t"+p.getValue()+"\t"+this.instancesLexicalizations.get(s.getKey()).get(p.getKey())+"\t"+this.instancesInTables.get(s.getKey()).get(p.getKey()).size()+"\t"+this.instancesInTables.get(s.getKey()).get(p.getKey()));
				
			check = check + s.getValue();
		}
		return check;
	}
	
	
	private int printQuerySeeds() {
		Map<String, Integer> sorted = MapUtil.sortByValue(types);

		int check = 0;
		for (Entry<String, Integer> s : sorted.entrySet()) {
			System.out.println(s.getValue()+"\t"+s.getKey());
			
			Map<String, Integer> sortedInstances = MapUtil.sortByValue(instances.get(s.getKey()));
			int maxInstance = 0;
			int maxProp = 0;

				for (Entry<String,Integer> p:sortedInstances.entrySet()){
					Map<String, Integer> sortedproperties = MapUtil.sortByValue(properties.get(s.getKey()));

					for (String tables : this.instancesInTables.get(s.getKey()).get(p.getKey())){
						Iterator<Entry<String, Integer>> it = sortedproperties.entrySet().iterator();
						String prop = it.next().getKey();
						
//						while(it.hasNext()&maxProp<10){
						System.out.println("\t\t"+p.getKey()+"\t"+p.getValue()+"\t"+this.instancesLexicalizations.get(s.getKey()).get(p.getKey())+"\t"+this.instancesInTables.get(s.getKey()).get(p.getKey()).size()+"\t"+this.instancesInTables.get(s.getKey()).get(p.getKey())+"\t"+prop+ this.propertiesInTables.get(s.getKey()).get(prop));

							
//							maxProp++;
//
//							   }
					}
						
			maxInstance++;
			if(maxInstance>10){
			       break;
			   }
				}
		}
		return check;
	}
	
	private void printInstancesLex() {
		Map <String,Integer> size = new HashMap<String,Integer>();
		
		for (Entry<String, Set<String>> s : this.lexicalizationOccurrences.entrySet()) {
			size.put(s.getKey(), s.getValue().size());
	}
		Map<String, Integer> sorted = MapUtil.sortByValue(size);
		for (Entry<String, Integer> s :sorted.entrySet()) {
			System.out.println(s.getKey()+"\t"+ s.getValue()+"\t"+this.lexicalizationOccurrences.get(s.getKey()));
		}
		
	}
	
	

	private void SelectQueryInstances() {
		Map <String,Integer> size = new HashMap<String,Integer>();
		
		for (Entry<String, Set<String>> s : this.lexicalizationOccurrences.entrySet()) {
			size.put(s.getKey(), s.getValue().size());
	}
		Map<String, Integer> sorted = MapUtil.sortByValue(size);
		for (Entry<String, Integer> s :sorted.entrySet()) {
			System.out.println(s.getKey()+"\t"+ s.getValue()+"\t"+this.lexicalizationOccurrences.get(s.getKey()));
		}
		
	}

	
	
	private int printProperties() {
		Map<String, Integer> sorted = MapUtil.sortByValue(types);

		int check = 0;
		for (Entry<String, Integer> s : sorted.entrySet()) {
			System.out.println(s.getValue()+"\t"+s.getKey());
			
				Map<String, Integer> sortedproperties = MapUtil.sortByValue(properties.get(s.getKey()));
					for (Entry<String,Integer> p:sortedproperties.entrySet())
				System.out.println("\t\t"+p.getKey()+"\t"+p.getValue()+"\t"+this.propertiesLexicalizations.get(s.getKey()).get(p.getKey()));
				
			check = check + s.getValue();
		}
		return check;
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
