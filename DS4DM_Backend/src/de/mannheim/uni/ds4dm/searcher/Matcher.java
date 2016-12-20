/**
 * 
 */
package de.mannheim.uni.ds4dm.searcher;

import com.rapidminer.extension.json.JSONRelatedTablesRequest;
import com.rapidminer.extension.json.JSONRelatedTablesResponse;
import com.rapidminer.extension.json.TableInformation;

import java.util.Set;

/**
 * @author annalisa
 *
 */
interface Matcher {
	
	JSONRelatedTablesResponse match(JSONRelatedTablesRequest queryTable, Set<TableInformation> candidates);

}
