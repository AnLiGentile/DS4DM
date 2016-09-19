package de.mannheim.uni.main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.mannheim.uni.IO.ConvertFileToTable.ReadTableType;
import de.mannheim.uni.index.ParallelIndexer;
import de.mannheim.uni.pipelines.Pipeline;

/**
 * @author annalisa
 * 
 */
public class GenerateIndexFromCsv {

	public static void main(String[] args) {

		String configFile = "./searchJoins.conf";
		String pipeName = GenerateIndexFromCsv.class.getName() + "_"
				+ System.currentTimeMillis() + "_";

		if (args == null & args.length < 3) {
			System.err.println("Provide parameters.");
			return;
		}

		if (args[0] != null) {
			configFile = args[0];
		} else {
			System.out.println("Using default config: "
					+ new File(configFile).getAbsolutePath());
		}

		if (args[1] != null) {
			pipeName = args[1]+"_";
		} else {
			System.out.println("Using default process name: " + pipeName);
		}

		// create the pipeline
		Pipeline pipeline = Pipeline.getPipelineFromConfigFile(pipeName,
				new File(configFile).getAbsolutePath());

		Map<String, ReadTableType> repos = new HashMap<String, ReadTableType>();
		for (int i = 2; i < args.length; i += 2) {
			repos.put(args[i], ReadTableType.valueOf("web"));
		}
		ParallelIndexer indexer = new ParallelIndexer(pipeline);
		// Indexer indexer = new Indexer(pipeline);
//		indexer.indexRepos(repos);
		indexer.indexReposAndPersistJson(repos);
	}
}
