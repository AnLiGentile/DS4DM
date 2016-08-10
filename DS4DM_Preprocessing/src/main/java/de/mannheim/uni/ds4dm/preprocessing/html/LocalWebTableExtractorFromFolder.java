package de.mannheim.uni.ds4dm.preprocessing.html;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.webdatacommons.webtables.extraction.BasicExtractionAlgorithm;
import org.webdatacommons.webtables.extraction.ExtractionAlgorithm;
import org.webdatacommons.webtables.extraction.model.DocumentMetadata;
import org.webdatacommons.webtables.extraction.stats.HashMapStatsData;
import org.webdatacommons.webtables.tools.data.Dataset;



/**
 * @author annalisa
 *
 */
public class LocalWebTableExtractorFromFolder {
	
	public static void main(String args[]) throws IOException, InterruptedException {
		
		ExtractionAlgorithm ea = new BasicExtractionAlgorithm(new HashMapStatsData(), true);
		File inputFile = new File(args[0]);
		File outputFolder = new File(args[1]);
		if (!outputFolder.exists())
			outputFolder.mkdirs();
		
		
			
		processHtmlFolder(ea, inputFile, outputFolder);
        System.out.println("Extraction completed.");
	}

	/**
	 * this function scans a folder for HTML files.
	 * For each file extracts tables using a specific ExtractionAlgorithm.
	 * Extracted tables are saved in the output folder in json format, as one table per file.
	 * @param ea - an ExtractionAlgorithm 
	 * @param inputFolder - folder containing HTML files 
	 * @param outputFolder - folder where extraction results will be saved 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void processHtmlFolder(ExtractionAlgorithm ea,
			File inputFolder, File outputFolder) throws FileNotFoundException,
			IOException, InterruptedException {
		
		
		
		
		if(inputFolder.isDirectory()){
			for (File f: inputFolder.listFiles()){
				processHtmlFolder(ea, f, outputFolder);
			}
		}else{
			
			
        	String type = "";
        	String fN = inputFolder.getName();
        	if (fN.contains("~")){
        		type = fN.split("~")[0];
        		fN = fN.split("~")[1];
        		System.out.println("for now skipping files of type: "+type);
        	}else{
        	
        	
		InputStream in = new BufferedInputStream(new FileInputStream(inputFolder));
        Document doc = Jsoup.parse(in, null, "");
        DocumentMetadata dm = new DocumentMetadata(0, 0, "", "", "");
        List<Dataset> result = ea.extract(doc, dm);    
		System.out.println("Processing: "+inputFolder+" --- EMPTY:"+ result.isEmpty());
        for (Dataset er : result) {

        	String outputF = outputFolder+File.separator+fN.toLowerCase().charAt(0) +File.separator+fN.toLowerCase().charAt(1)+File.separator+fN.toLowerCase().charAt(2);
        	File out = new File(outputF);
        	if (!out.exists())
        		out.mkdirs();
        	BufferedWriter write = new BufferedWriter(new FileWriter(new File(outputF, fN+ "_" + er.getTableNum()+".json")));
            write.write(er.toJson());
            write.flush();
            write.close();
        	
        }
        	}
		}
	}
	
}
