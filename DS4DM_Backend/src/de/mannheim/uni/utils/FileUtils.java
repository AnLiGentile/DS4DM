package de.mannheim.uni.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

/**
 * @author petar
 * 
 */
public class FileUtils {

	public static double getFileSize(File file) {

		double bytes = file.length();
		double kilobytes = (bytes / 1024);
		double megabytes = (kilobytes / 1024);
		return megabytes;

	}
	
	public static List<String> readFilesFromFolder(String folderPath,
			double maxSize) {
		List<String> files = new ArrayList<String>();
		File folder = new File(folderPath);

		for (File fileEntry : folder.listFiles()) {
			// System.out.println("added" + fileEntry.getAbsolutePath());
			if (FileUtils.getFileSize(fileEntry) <= maxSize
					&& (fileEntry.getName().endsWith(".gz") || fileEntry
							.getName().endsWith(".tar")))

				files.add(fileEntry.getAbsolutePath());
		}
		return files;
	}

	/**
	 * reads the data files paths for InfoGather
	 * 
	 * @param folderPath
	 * @return
	 */
	public static Map<String, List<String>> getFilePaths(String folderPath) {
		Map<String, List<String>> paths = new HashMap<String, List<String>>();

		File folder = new File(folderPath);
		for (File fileEntry : folder.listFiles()) {
			String fileName = fileEntry.getName();
			if (fileName.endsWith("CSV")) {
				String filePath = fileEntry.getAbsolutePath();
				String prexis = fileName.replace("_CSV", "");
				List<String> innerPaths = new LinkedList<String>();
				if (paths.containsKey(prexis)) {
					innerPaths = paths.get(prexis);
				}
				innerPaths.add(0, filePath);
				paths.put(prexis, innerPaths);
			} else if (fileName.endsWith("HTML")) {
				String filePath = fileEntry.getAbsolutePath();
				String prexis = fileName.replace("_HTML", "");
				List<String> innerPaths = new LinkedList<String>();
				if (paths.containsKey(prexis)) {
					innerPaths = paths.get(prexis);
				}
				if (innerPaths.size() < 1)
					innerPaths.add(0, "dummy");
				innerPaths.add(1, filePath);
				paths.put(prexis, innerPaths);
			} else if (fileName.endsWith("META")) {
				String filePath = fileEntry.getAbsolutePath();
				String prexis = fileName.replace("_META", "");
				List<String> innerPaths = new LinkedList<String>();
				if (paths.containsKey(prexis)) {
					innerPaths = paths.get(prexis);
				}
				if (innerPaths.size() < 2) {
					innerPaths.add(0, "dummy");
					innerPaths.add(1, "dummy");
				}
				innerPaths.add(2, filePath);
				paths.put(prexis, innerPaths);
			}

		}

		return paths;
	}
	
	/**
	 * returns the list of .gz or .tar files conatined in a folder
	 * @param folderPath
	 * @param maxSize
	 * @return
	 */
	public static List<String> readGzTarFilesFromFolder(String folderPath, double maxSize) {
		List<String> files = new ArrayList<String>();
		File folder = new File(folderPath);

		for (File fileEntry : folder.listFiles()) {
			// System.out.println("added" + fileEntry.getAbsolutePath());
			if (FileUtils.getFileSize(fileEntry) <= maxSize
					&& (fileEntry.getName().endsWith(".gz") || fileEntry.getName().endsWith(".tar")))

				files.add(fileEntry.getAbsolutePath());
		}
		return files;
	}
	
	
    public void gunzipIt(File INPUT_GZIP_FILE, File OUTPUT_FILE){
    	 
        byte[] buffer = new byte[1024];
    
        try{
    
       	 GZIPInputStream gzis = 
       		new GZIPInputStream(new FileInputStream(INPUT_GZIP_FILE));
    
       	 FileOutputStream out = 
               new FileOutputStream(OUTPUT_FILE);
    
           int len;
           while ((len = gzis.read(buffer)) > 0) {
           	out.write(buffer, 0, len);
           }
    
           gzis.close();
       	out.close();
           	
       }catch(IOException ex){
          ex.printStackTrace();   
       }
      } 
   


	/**
	 * returns the list of .json files contained in a folder
	 * @param folderPath
	 * @param maxSize
	 * @return
	 */
	public static List<String> readJsonFilesFromFolder(String folderPath, double maxSize) {
		List<String> files = new ArrayList<String>();
		File folder = new File(folderPath);

		for (File fileEntry : folder.listFiles()) {
			// System.out.println("added" + fileEntry.getAbsolutePath());
			if (FileUtils.getFileSize(fileEntry) <= maxSize
					&& (fileEntry.getName().endsWith(".json") || fileEntry.isDirectory()))

				files.add(fileEntry.getAbsolutePath());
		}
		return files;
	}
	
	/**
	 * returns the list of .json files contained in a folder
	 * @param folderPath
	 * @param maxSize
	 * @return
	 */
	public static List<String> readJsonFilesFromMultilineJson(String folderPath, double maxSize) {
		List<String> files = new ArrayList<String>();
		File folder = new File(folderPath);

		for (File fileEntry : folder.listFiles()) {
			// System.out.println("added" + fileEntry.getAbsolutePath());
//			if (FileUtils.getFileSize(fileEntry) <= maxSize
							if ( (fileEntry.getName().endsWith(".json.gz") || fileEntry.isDirectory()))

				files.add(fileEntry.getAbsolutePath());
		}
		return files;
	}
	

	/**
	 * reads the allowed table when searching by column header
	 * 
	 * @return
	 */
	public static TreeSet<String> readHeaderTablesFromFile(String filePath) {
		TreeSet<String> tablesList = new TreeSet<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String line = "";

			while ((line = br.readLine()) != null) {

				try {
					if (line.contains("|")) {
						String fileName = line.split("\"\\|\"")[2].replace("\"", "");

						tablesList.add(fileName);

					}

				} catch (Exception e) {

				}
			}
		} catch (Exception e) {

		}
		return tablesList;

	}

}
