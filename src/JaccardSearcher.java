//Name: Purit Phan-udom
//Section: 1
//ID: 5988023

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		
		
		
		
		/***********************************************/
		
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		
		List<String> queryTokens = this.tokenize(queryString);
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
					int intersection = 0;
					int union = queryTokens.size() + doc.getTokens().size();
					
					for(String queryTerm: queryTokens) {

						if(doc.getTokens().contains(queryTerm)) {
							intersection++;			//Increase the size of intersect by 1 for every pair
							union--;				//Decrease the size of union by 1 for every intersecting pair
						}
						
					}
					
					SearchResult jaccardScore = new SearchResult(doc, intersection/union);
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
