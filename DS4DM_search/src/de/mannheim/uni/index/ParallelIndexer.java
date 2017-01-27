package de.mannheim.uni.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import de.mannheim.uni.IO.ConvertFileToTable;
import de.mannheim.uni.IO.ConvertFileToTable.ReadTableType;
import de.mannheim.uni.model.Table;
import de.mannheim.uni.pipelines.Pipeline;
import de.mannheim.uni.utils.FileUtils;

//TODO change the name of the class
/**
 * @author petar (Annalisa made changes to the original version)
 * 
 */
public class ParallelIndexer {

	/**
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static File extractTarFile(String path) throws Exception {
		/* Read TAR File into TarArchiveInputStream */
		TarArchiveInputStream myTarFile = new TarArchiveInputStream(new FileInputStream(new File(path)));

		String outputDirName = path.replaceAll(".tar$", "");
		File outputDir = new File(outputDirName);
		outputDir.mkdir();
		/* To read individual TAR file */
		TarArchiveEntry entry = null;
		String individualFiles;
		int offset;
		FileOutputStream outputFile = null;
		/* Create a loop to read every single entry in TAR file */
		while ((entry = myTarFile.getNextTarEntry()) != null) {
			/* Get the name of the file */
			individualFiles = entry.getName();
			/* Get Size of the file and create a byte array for the size */
			byte[] content = new byte[(int) entry.getSize()];
			offset = 0;
			/* Some SOP statements to check progress */
			// System.out.println("File Name in TAR File is: "
			// + individualFiles);
			// System.out.println("Size of the File is: " +
			// entry.getSize());
			// System.out.println("Byte Array length: " + content.length);
			/* Read file from the archive into byte array */
			myTarFile.read(content, offset, content.length - offset);
			/* Define OutputStream for writing the file */
			outputFile = new FileOutputStream(new File(outputDirName + "/" + individualFiles));
			/* Use IOUtiles to write content of byte array to physical file */
			IOUtils.write(content, outputFile);
			/* Close Output Stream */
			outputFile.close();
		}
		/* Close TarAchiveInputStream */
		myTarFile.close();
		return outputDir;
	}

	
	/**
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static File extractGzFile(String path) throws Exception {
		/* Read TAR File into TarArchiveInputStream */
		GZIPInputStream zipin = new GZIPInputStream(new FileInputStream(new File(path)));

		int sChunk = 1024;
		
		String outputDirName = path.replaceAll(".gz$", "");
		
	    byte[] buffer = new byte[sChunk];
	    File outputDir = new File (outputDirName);
	    FileOutputStream out = new FileOutputStream(outputDirName);
	    int length;
	    while ((length = zipin.read(buffer, 0, sChunk)) != -1)
	      out.write(buffer, 0, length);
	    out.close();
	    zipin.close();
	    		return outputDir;
	}
	
	private IndexManager indexManager = null;

	private AttributesIndexManager attributeIndexManager = null;

	private Pipeline pipeline;

	/**
	 * @param pipeline
	 */
	public ParallelIndexer(Pipeline pipeline) {
		this.pipeline = pipeline;
		this.indexManager = pipeline.getIndexManager();
		this.attributeIndexManager = pipeline.getAttributesIndexManager();
	}

	/**
	 * @param path
	 *            path of a folder containing tables
	 * @param tableType
	 *            type of contained tables
	 * @throws InterruptedException
	 */
	public void indexFilesFromFolder(String path, ReadTableType tableType) throws InterruptedException {
		List<String> files = FileUtils.readGzTarFilesFromFolder(path, pipeline.getMaxFileSize());
		processListOfTables(tableType, files);
	}

	/**
	 * @param path
	 *            path of a folder containing tables
	 * @param tableType
	 *            type of contained tables
	 * @throws InterruptedException
	 */
	public void indexFilesFromJsonCondensedFiles(String path, ReadTableType tableType) throws InterruptedException {
		List<String> files = FileUtils.readJsonFilesFromMultilineJson(path, pipeline.getMaxFileSize());
		processListOfTables(tableType, files);
	}
	
	
	public void indexFilesFromFolderAndPersistJson(String path, ReadTableType tableType) throws InterruptedException {

		List<String> files = FileUtils.readGzTarFilesFromFolder(path, pipeline.getMaxFileSize());

		processListOfTables(tableType, files);

	}

	/**
	 * @param path path of a folder containing json tables
	 * @param tableType
	 * @throws InterruptedException
	 */
	public void indexFilesFromJsonFolder(String path, ReadTableType tableType) throws InterruptedException {
		List<String> files = FileUtils.readJsonFilesFromFolder(path, pipeline.getMaxFileSize());
		processListOfTables(tableType, files);
	}

	/**
	 * @param tableType
	 * @param files
	 * @throws InterruptedException
	 */
	private void processListOfTables(ReadTableType tableType, List<String> files) throws InterruptedException {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
				Runtime.getRuntime().availableProcessors(), 0, TimeUnit.SECONDS,
				new java.util.concurrent.ArrayBlockingQueue<Runnable>(files.size()));
		pipeline.getLogger().info("TOTAL DOCUMENTS TO INDEX :" + files.size());
		// read the table sequentally
		ConvertFileToTable fileToTable = new ConvertFileToTable(pipeline);
		int tCount = 1;
		for (String file : files) {

			Table table = new Table();
			table.setFullPath(file);
			// table = fileToTable.readTable(tableType, file);
			// something is wrong with the table
			// if (table == null)
			// continue;
			IndexFileThread th = new IndexFileThread(pipeline, indexManager, table, fileToTable, tableType,
					attributeIndexManager);
			pipeline.getLogger().info("Indexing " + tCount + " table");
			tCount++;
			pool.execute(th);
		}
		pool.shutdown();
		pool.awaitTermination(10, TimeUnit.DAYS);
		
	}

	
	public void indexJson(Map<String, ReadTableType> repos) {

		long start = System.currentTimeMillis();
		// TODO Annalisa: added this, but maybe wrong
		attributeIndexManager.getIndexWriter(true);
		indexManager.getIndexWriter(true);
		for (Entry entry : repos.entrySet()) {
			try {
				indexFilesFromJsonFolder((String) entry.getKey(), (ReadTableType) entry.getValue());
				// indexFilesFromFolderByValues((String) entry.getKey(),
				// (ReadTableType) entry.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			attributeIndexManager.closeIndexWriter();
			indexManager.closeIndexWriter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipeline.getLogger().info("#Docs after indexing " + repos.keySet().iterator().next() + ": "
				+ Integer.toString(indexManager.getNmDocs()));

		long end = System.currentTimeMillis();
		pipeline.getLogger().info("Time for indexing all tables: " + ((double) (end - start) / 1000));
	}
	
	
	public void indexJsonDresden(Map<String, ReadTableType> repos) {

		long start = System.currentTimeMillis();
		// TODO Annalisa: added this, but maybe wrong
		attributeIndexManager.getIndexWriter(true);
		indexManager.getIndexWriter(true);
		for (Entry entry : repos.entrySet()) {
			try {
				indexFilesFromJsonCondensedFiles((String) entry.getKey(), (ReadTableType) entry.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			attributeIndexManager.closeIndexWriter();
			indexManager.closeIndexWriter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipeline.getLogger().info("#Docs after indexing " + repos.keySet().iterator().next() + ": "
				+ Integer.toString(indexManager.getNmDocs()));

		long end = System.currentTimeMillis();
		pipeline.getLogger().info("Time for indexing all tables: " + ((double) (end - start) / 1000));
	}
	
	
	public void indexRepos(Map<String, ReadTableType> repos) {

		long start = System.currentTimeMillis();
		for (Entry entry : repos.entrySet()) {

			try {
				indexFilesFromFolder((String) entry.getKey(), (ReadTableType) entry.getValue());
				// indexFilesFromFolderByValues((String) entry.getKey(),
				// (ReadTableType) entry.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		try {
			attributeIndexManager.closeIndexWriter();
			indexManager.closeIndexWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		pipeline.getLogger().info("#Docs after indexing " + repos.keySet().iterator().next() + ": "
				+ Integer.toString(indexManager.getNmDocs()));

		long end = System.currentTimeMillis();

		pipeline.getLogger().info("Time for indexing all tables: " + ((double) (end - start) / 1000));

	}
	
	
	/**
	 * @param repos
	 */
	public void indexReposAndPersistJson(Map<String, ReadTableType> repos) {

		long start = System.currentTimeMillis();
		for (Entry entry : repos.entrySet()) {
			try {
				pipeline.getLogger().info(
						"#Docs before indexing " + entry.getKey() + ": " + Integer.toString(indexManager.getNmDocs()));
			} catch (Exception e) {

			}

			try {

				indexFilesFromFolderAndPersistJson((String) entry.getKey(), (ReadTableType) entry.getValue());
				// indexFilesFromFolderByValues((String) entry.getKey(),
				// (ReadTableType) entry.getValue());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}

		}
		try {
			attributeIndexManager.closeIndexWriter();
			indexManager.closeIndexWriter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pipeline.getLogger().info("#Docs after indexing " + repos.keySet().iterator().next() + ": "
				+ Integer.toString(indexManager.getNmDocs()));

		long end = System.currentTimeMillis();

		pipeline.getLogger().info("Time for indexing all tables: " + ((double) (end - start) / 1000));

	}

}
