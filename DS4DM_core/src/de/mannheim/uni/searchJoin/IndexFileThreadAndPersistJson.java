//package de.mannheim.uni.searchJoin;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.rapidminer.extension.json.JSONTableResponse;
//
//import de.mannheim.uni.IO.ConvertFileToTable;
//import de.mannheim.uni.IO.ConvertFileToTable.ReadTableType;
//import de.mannheim.uni.TableProcessor.TableKeyIdentifier;
//import de.mannheim.uni.TableProcessor.TableManager;
//import de.mannheim.uni.ds4dm.model.TableSearchJoin2TableDS4DM;
//import de.mannheim.uni.ds4dm.utils.ReadWriteGson;
//import de.mannheim.uni.index.AttributesIndexManager;
//import de.mannheim.uni.index.IndexManager;
//import de.mannheim.uni.index.ParallelIndexer.IndexFileThread;
//import de.mannheim.uni.model.ColumnIndexEntry;
//import de.mannheim.uni.model.IndexEntry;
//import de.mannheim.uni.model.Table;
//import de.mannheim.uni.model.TableColumn;
//import de.mannheim.uni.model.TableStats;
//import de.mannheim.uni.pipelines.Pipeline;
//import de.mannheim.uni.pipelines.Pipeline.KeyIdentificationType;
//import de.mannheim.uni.utils.PipelineConfig;
//import de.mannheim.uni.webtables.statistics.TableStatisticsExtractorLocal;
//
//public class IndexFileThreadAndPersistJson extends IndexFileThread{
//
//	public IndexFileThreadAndPersistJson(Pipeline pipeline, IndexManager indexManager,
//			Table table, ConvertFileToTable fileToTable,
//			ReadTableType typeTable,
//			AttributesIndexManager attributeIndexManager) {
//		super(pipeline, indexManager, table, fileToTable, typeTable, attributeIndexManager);
//	}
//	
//	
//	public void run() {
//		indexTable(super.getTable());
//
//	}
//
//	public void indexTable(Table table) {
//		// use if it is a tar
//		List<String> tablesToIndex = new ArrayList<String>();
//		File unzipped = null;
//		tablesToIndex.add(table.getFullPath());
//		if (table.getFullPath().endsWith(".tar")) {
//			try {
//				unzipped = ParallelIndexer.extractTarFile(table.getFullPath());
//				tablesToIndex = new ArrayList<String>();
//				for (File f : unzipped.listFiles()) {
//					tablesToIndex.add(f.getAbsolutePath());
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return;
//			}
//		} else if (table.getFullPath().endsWith(".tar.gz")) {
//			try {
//				unzipped = TableStatisticsExtractorLocal
//						.extractTarFile(table.getFullPath());
//				tablesToIndex = new ArrayList<String>();
//				for (File f : unzipped.listFiles()) {
//					if (f.getName().endsWith(".csv")
//							&& !f.getName().contains("LINK"))
//						tablesToIndex.add(f.getAbsolutePath());
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return;
//			}
//		}
//		for (String tablePath : tablesToIndex) {
//			table = super.getFileToTable().readTable(super.getTypeTable(), tablePath);
//			// something is wrong with the table
//			if (table == null)
//				continue;
//			String tableName = table.getFullPath();
//			List<IndexEntry> indexEntries = null;
//			try {
//				// set the final types of each column
//				for (TableColumn column : table.getColumns())
//					column.setFinalDataType();
//
//				// identify key
//				TableKeyIdentifier keyIdentifier = new TableKeyIdentifier(
//						super.getPipeline());
//				keyIdentifier.identifyKey(table);
//				if (!table.isHasKey())
//					continue;
//
//				TableStats stat = new TableStats();
//				stat.setHeader(table.getFullPath());
//				stat.setNmCols(table.getColumns().size());
//				stat.setNmRows(table.getColumns().get(0).getValues().size());
//
//				super.getPipeline().getLogger().info(
//						"Indexing table:" + table.getFullPath());
//				long start = System.currentTimeMillis();
//				// if the user selected indexing the values
//				if (super.getPipeline().getIndexingMode() == 0
//						|| super.getPipeline().getIndexingMode() == 1) {
//					if (super.getPipeline().getKeyidentificationType() == KeyIdentificationType.single) {
//						indexEntries = TableManager
//								.getKeyIndexEntriesFromTable(table);
//					} else if (super.getPipeline().getKeyidentificationType() == KeyIdentificationType.none
//							|| super.getPipeline().getKeyidentificationType() == KeyIdentificationType.singleWithRefineAttrs) {
//						indexEntries = TableManager
//								.getIndexEntriesFromTable(table);
//					}
//
//					stat.setNmNulls(table.getNmNulls());
//					//table = null;
//					int eCount = 1;
//					// index the entries
//					for (IndexEntry entry : indexEntries) {
//						if (!entry.getValue().equalsIgnoreCase(
//								PipelineConfig.NULL_VALUE)
//								&& !entry.getValue().equals(""))
//							super.getIndexManager().indexValue(entry);
//						// check if it is null
//						if (entry.getValue().equalsIgnoreCase(
//								PipelineConfig.NULL_VALUE)) {
//							stat.setNmNulls(stat.getNmNulls() + 1);
//						}
//						if (eCount % 1000 == 0)
//							System.out.println("Indexing " + eCount + "/"
//									+ indexEntries.size());
//						eCount++;
//					}
//				}
//
//				// index the headers if needed
//				if (super.getPipeline().getIndexingMode() == 0
//						|| super.getPipeline().getIndexingMode() == 2) {
//					List<ColumnIndexEntry> columnEntries = TableManager
//							.getColumnsEntriesFromTable(table);
//					for (ColumnIndexEntry entry : columnEntries) {
//						try {
//							super.getAttributeIndexManager().indexValue(entry);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}
//				//TODO this is a test, change to Sabrina's format
//				//save the table as json
//				File j_pet = new File (super.getPipeline().getJsonFolderLocation()+File.separator+"petar");
//				File j_sab = new File (super.getPipeline().getJsonFolderLocation()+File.separator+"sabrina");
//
//				if (!j_pet.exists())
//					j_pet.mkdirs();
//				if (!j_sab.exists())
//					j_sab.mkdirs();
//
//				
//				String tn = table.getFullPath().substring(table.getFullPath().lastIndexOf(File.separator));
//				if (tn.endsWith(".csv.gz")){
//					tn = tn.substring(0, tn.indexOf(".csv.gz"));
//					}
//				
//				
//				JSONTableResponse t_sab = TableSearchJoin2TableDS4DM.fromAnnotatedTable2JSONTableResponse(table, tn);
//		
//				
//				ReadWriteGson<Table> rt = new ReadWriteGson<Table>(table);
//				ReadWriteGson<JSONTableResponse> rt_sab = new ReadWriteGson<JSONTableResponse>(t_sab);
//
//				
//				File outJ = new File(j_pet.getAbsolutePath()+File.separator+tn+".json");
//				File outJ_sab = new File(j_sab.getAbsolutePath()+File.separator+tn+".json");
//
//				
//				try {
//					rt.writeJson(outJ);
//					rt_sab.writeJson(outJ_sab);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				table = null;
//				// write the stats to file
//				super.getPipeline().getIndexLogger().info(stat.getInfo());
//				long end = System.currentTimeMillis();
//				super.getPipeline().getLogger().info(
//						tableName + "The table was indexed for: "
//								+ +((double) (end - start) / 1000));
//
//			} catch (Exception e) {
//				super.getPipeline().getLogger().info(
//						"Indexing table " + tableName + "failed");
//				super.getPipeline().getLogger().info(e.getMessage());
//			}
//			indexEntries = null;
//		}
//		if (unzipped != null) {
//			try {
//				org.apache.commons.io.FileUtils.deleteDirectory(unzipped);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//}
