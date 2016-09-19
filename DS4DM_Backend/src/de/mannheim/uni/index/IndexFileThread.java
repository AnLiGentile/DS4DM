package de.mannheim.uni.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import de.mannheim.uni.IO.ConvertFileToTable;
import de.mannheim.uni.IO.ConvertFileToTable.ReadTableType;
import de.mannheim.uni.TableProcessor.TableKeyIdentifier;
import de.mannheim.uni.TableProcessor.TableManager;
import de.mannheim.uni.model.ColumnIndexEntry;
import de.mannheim.uni.model.IndexEntry;
import de.mannheim.uni.model.Table;
import de.mannheim.uni.model.TableColumn;
import de.mannheim.uni.model.TableStats;
import de.mannheim.uni.pipelines.Pipeline;
import de.mannheim.uni.pipelines.Pipeline.KeyIdentificationType;
import de.mannheim.uni.utils.FileUtils;
import de.mannheim.uni.utils.PipelineConfig;

/******************************************************************************************
 * @author petar/annalisa
 * IndexFileThread class
 */
public class IndexFileThread implements Runnable {

	private IndexManager indexManager = null;

	private AttributesIndexManager attributeIndexManager = null;

	private Pipeline pipeline;

	private Table table;

	private ConvertFileToTable fileToTable;

	private ReadTableType typeTable;

	public IndexFileThread(Pipeline pipeline, IndexManager indexManager, Table table,
			ConvertFileToTable fileToTable, ReadTableType typeTable, AttributesIndexManager attributeIndexManager) {
		this.pipeline = pipeline;
		this.indexManager = indexManager;
		this.table = table;
		this.fileToTable = fileToTable;
		this.typeTable = typeTable;
		this.attributeIndexManager = attributeIndexManager;
	}

	public AttributesIndexManager getAttributeIndexManager() {
		return attributeIndexManager;
	}

	public ConvertFileToTable getFileToTable() {
		return fileToTable;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public Table getTable() {
		return table;
	}

	public ReadTableType getTypeTable() {
		return typeTable;
	}

	public void indexJsonTable(Table table) {

		List<String> tablesToIndex = new ArrayList<String>();
		File tti = new File(table.getFullPath());
		scanDir(tti, tablesToIndex);

		indexListOfJsonTables(tablesToIndex);

	}
	
	private void indexListOfJsonTables(List<String> tablesToIndex) {
		Table table;
		pipeline.getLogger().info("Tables to index:" + tablesToIndex.size());
		for (String tablePath : tablesToIndex) {
			pipeline.getLogger().info("Reading table:" + tablePath);
			table = fileToTable.readTable(typeTable, tablePath);
			// something is wrong with the table
			if (table == null) {
				pipeline.getLogger().info("NULL table:" + tablePath);
				continue;
			}
			String tableName = table.getFullPath();
			List<IndexEntry> indexEntries = null;
			try {
				// set the final types of each column
				for (TableColumn column : table.getColumns())
					column.setFinalDataType();

				// identify key
				TableKeyIdentifier keyIdentifier = new TableKeyIdentifier(pipeline);
				keyIdentifier.identifyKey(table);
				if (!table.isHasKey())
					continue;

				TableStats stat = new TableStats();
				stat.setHeader(table.getFullPath());
				stat.setNmCols(table.getColumns().size());
				stat.setNmRows(table.getColumns().get(0).getValues().size());

				pipeline.getLogger().info("Indexing table:" + table.getFullPath());
				long start = System.currentTimeMillis();
				// if the user selected indexing the values
				if (pipeline.getIndexingMode() == 0 || pipeline.getIndexingMode() == 1) {
					if (pipeline.getKeyidentificationType() == KeyIdentificationType.single) {
						indexEntries = TableManager.getKeyIndexEntriesFromTable(table);
					} else if (pipeline.getKeyidentificationType() == KeyIdentificationType.none
							|| pipeline.getKeyidentificationType() == KeyIdentificationType.singleWithRefineAttrs) {
						indexEntries = TableManager.getIndexEntriesFromTable(table);
					}

					stat.setNmNulls(table.getNmNulls());
					// table = null;
					int eCount = 1;
					// index the entries
					for (IndexEntry entry : indexEntries) {
						if (!entry.getValue().equalsIgnoreCase(PipelineConfig.NULL_VALUE)
								&& !entry.getValue().equals(""))
							indexManager.indexValue(entry);
						// check if it is null
						if (entry.getValue().equalsIgnoreCase(PipelineConfig.NULL_VALUE)) {
							stat.setNmNulls(stat.getNmNulls() + 1);
						}
						if (eCount % 1000 == 0)
							System.out.println("Indexing " + eCount + "/" + indexEntries.size());
						eCount++;
					}
				}

				// index the headers if needed
				if (pipeline.getIndexingMode() == 0 || pipeline.getIndexingMode() == 2) {
					List<ColumnIndexEntry> columnEntries = TableManager.getColumnsEntriesFromTable(table);
					for (ColumnIndexEntry entry : columnEntries) {
						try {
							attributeIndexManager.indexValue(entry);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				table = null;
				// write the stats to file
				pipeline.getIndexLogger().info(stat.getInfo());
				long end = System.currentTimeMillis();
				pipeline.getLogger()
						.info(tableName + "The table was indexed for: " + +((double) (end - start) / 1000));

			} catch (Exception e) {
				pipeline.getLogger().info("Indexing table " + tableName + "failed");
				pipeline.getLogger().info(e.getMessage());
			}
			indexEntries = null;
		}
	}
	
	
	/**
	 * this method handles json files from the Dresden corpus
	 * @param table
	 */
	public void indexGzJsonCondensedTables(Table table) {
		// use if it is a tar
		List<String> tablesToIndex = new ArrayList<String>();
		File unzipped = null;
		File tempFolder = null;
		tablesToIndex.add(table.getFullPath());
		if (table.getFullPath().endsWith(".gz")) {
			try {
				unzipped = ParallelIndexer.extractGzFile(table.getFullPath());
				tablesToIndex = new ArrayList<String>();
				if (unzipped.getName().endsWith(".json"))
				{
					String outputDirName = unzipped.getName().replaceAll(".json$", "");
					//TODO create temp folder
					tempFolder = new File(unzipped.getParentFile().getAbsolutePath()+File.separator+outputDirName);
					tempFolder.mkdirs();
					
					//splitjson and create a single json file for each of them
					try (BufferedReader br = new BufferedReader(new FileReader(unzipped))) {
						int i=0;
					    String line;
					    while ((line = br.readLine()) != null) {
					    	String fileName=tempFolder+File.separator+unzipped.getName()+"_"+i+".json";
					    	try(  PrintWriter out = new PrintWriter(fileName)  ){
					    	    out.println( line );
					    	    out.close();
								tablesToIndex.add(fileName);
					    	    i++;
					    	}
					    }
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
		indexListOfJsonTables(tablesToIndex);
		

		if (tempFolder != null) {
			try {
				org.apache.commons.io.FileUtils.deleteDirectory(tempFolder);
				unzipped.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void indexTarGzCsvTable(Table table) {
		// use if it is a tar
		List<String> tablesToIndex = new ArrayList<String>();
		File unzipped = null;
		tablesToIndex.add(table.getFullPath());
		if (table.getFullPath().endsWith(".tar")) {
			try {
				unzipped = ParallelIndexer.extractTarFile(table.getFullPath());
				tablesToIndex = new ArrayList<String>();
				for (File f : unzipped.listFiles()) {
					tablesToIndex.add(f.getAbsolutePath());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else if (table.getFullPath().endsWith(".tar.gz")) {
			try {
				unzipped = ParallelIndexer.extractTarFile(table.getFullPath());
				tablesToIndex = new ArrayList<String>();
				for (File f : unzipped.listFiles()) {
					if (f.getName().endsWith(".csv") && !f.getName().contains("LINK"))
						tablesToIndex.add(f.getAbsolutePath());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		indexListOfJsonTables(tablesToIndex);
		if (unzipped != null) {
			try {
				org.apache.commons.io.FileUtils.deleteDirectory(unzipped);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		
		
		
		if (this.typeTable.equals(ReadTableType.json))
			indexJsonTable(table);
		else if (this.typeTable.equals(ReadTableType.compressedjson))
		indexGzJsonCondensedTables(table);
		else		
			indexTarGzCsvTable(table);

	}
	
	public void scanDir(File tti, List<String> tablesToIndex) {
		if (tablesToIndex == null)
			tablesToIndex = new ArrayList<String>();

		if (tti.isDirectory()) {
			try {
				File ltti[] = tti.listFiles();
				for (File f : ltti) {
					scanDir(f, tablesToIndex);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (tti.getAbsolutePath().endsWith(".json"))
			try {
				tablesToIndex.add(tti.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
	}

	public void setAttributeIndexManager(AttributesIndexManager attributeIndexManager) {
		this.attributeIndexManager = attributeIndexManager;
	}

	public void setFileToTable(ConvertFileToTable fileToTable) {
		this.fileToTable = fileToTable;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void setTypeTable(ReadTableType typeTable) {
		this.typeTable = typeTable;
	}
}