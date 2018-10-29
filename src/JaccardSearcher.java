//Name: Purit Phan-udom
//Section: 1
//ID: 5988023

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class JaccardSearcher extends Searcher{
	
	

	public JaccardSearcher(String docFilename) {
		super(docFilename);		
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		
		List<String> queryTokens = Searcher.tokenize(queryString);
		Set<String> queryTokensSet = new HashSet<String>(queryTokens);
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		List<SearchResult> topKresult = new ArrayList<SearchResult>();
		
		//Part 1: Create the search result for all documents first//
		
		if(queryTokens.size() == 0) {			//Case where query is empty;
			for(Document doc: documents) {
				SearchResult jaccardZero = new SearchResult(doc, 0.0);
				searchResults.add(jaccardZero);
			}
		} else {
			
			for(Document doc: documents) {
				
				if(doc.getTokens().size() == 0) {	//Case where document is empty
					SearchResult jaccardZero = new SearchResult(doc, 0.0);
					searchResults.add(jaccardZero);
				} else {							//Normal case
					Set<String> documentTokensSet = new HashSet<String>(doc.getTokens());
					//we suppose to get a copy of docToken which has been put into set aka. no dup
					//so we can ultilize data inside it all we want
					//try not to alter the data in queryTokensSet because we only create it once per query
					
					/*int intersection = 0;
					int union = queryTokens.size() + doc.getTokens().size();
					
					for(String queryTerm: queryTokens) {

						if(doc.getTokens().contains(queryTerm)) {
							intersection++;			//Increase the size of intersect by 1 for every pair
							union--;				//Decrease the size of union by 1 for every intersecting pair
						}
						
					}*/
					
					//union
					documentTokensSet.addAll(queryTokensSet);
					int union = documentTokensSet.size();
					int intersection;
					SearchResult jaccardScore;
					if(union == 0){
						jaccardScore = new SearchResult(doc, 0.0);
					}else{
						//intersect
						documentTokensSet = new HashSet<String>(doc.getTokens());
						documentTokensSet.retainAll(queryTokensSet);
						intersection = documentTokensSet.size();
						jaccardScore = new SearchResult(doc, ((double)intersection)/ ((double)union));
					}
					searchResults.add(jaccardScore);
				}
								
			}
					
		}
		
		Collections.sort(searchResults); //sort using comparable interface
		
		
		for(int i = 0; i < k; i++) {
			topKresult.add(searchResults.get(i));	//Get the top K search results
		}
		
		return topKresult;	//Return the top K results

	}
}
