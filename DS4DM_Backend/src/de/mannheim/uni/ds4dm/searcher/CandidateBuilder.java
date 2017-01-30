/**
 * 
 */
package de.mannheim.uni.ds4dm.searcher;

import java.util.Map;
import java.util.Set;

import de.mannheim.uni.ds4dm.model.TableData;


/**
 * @author annalisa
 *
 */
interface CandidateBuilder {
	
	Set<TableData> finCandidates(Matcher matcher);

	Map<String,TableData> finCandidates(DS4DMBasicMatcher qts);

}
