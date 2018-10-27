//Name: Poomdharm Benjasirimongkol
//Section: 1
//ID: 5988056

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs
	
	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";
		
		//load queries. Treat each query as a document. 
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results 
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		//So we need to create a list and set of Ground truth and relevant set first
		List<SearchResult> RelevantDocs = searcher.search(query.getRawText(), k);
		Set<Integer> Gq = answers.get(query.getId());		//get set of relevant docs
		Set<Integer> Rq = new HashSet<Integer>();			//get k searcher results
		Set<Integer> IntersectRG = new HashSet<Integer>();	//a temp HashSet for intersection
		double Precision = 0.0, Recall = 0.0, F1 = 0.0;
		
		//Create Rq set
		for(SearchResult SR: RelevantDocs) {
			Rq.add(SR.getDocument().getId());
		}
		
		//Intersection
		IntersectRG = Rq;
		IntersectRG.retainAll(Gq);
		
		//Find Precision
		Precision = IntersectRG.size()/Rq.size();
		//Find Recall
		Recall = IntersectRG.size()/Gq.size();
		//Find F1
		if(Precision + Recall != 0) {						//prevent case of 0 divider
			F1 = (2*Precision*Recall) / (Precision + Recall);
		}
		double[] Result = {Precision, Recall, F1};			//Pack and return the Precision, Recall, F1 into result array
		return Result;
		/****************************************************************/
	}
	
	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1. 
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/
		//method for compute the average of Precision Recall and F1
		//Just like the last method except we do it in every docs
		double totPrecision = 0.0, totRecall = 0.0, totF1 = 0.0;
		double avgPrecision = 0.0, avgRecall = 0.0, avgF1 = 0.0;
		
		for(Document q: queries) {
			List<SearchResult> RelevantDocs = searcher.search(q.getRawText(), k);
			Set<Integer> Gq = answers.get(q.getId());		//get set of relevant docs
			Set<Integer> Rq = new HashSet<Integer>();			//get k searcher results
			Set<Integer> IntersectRG = new HashSet<Integer>();	//a temp HashSet for intersection
			double Precision = 0.0, Recall = 0.0, F1 = 0.0;
			
			//Create Rq set
			for(SearchResult SR: RelevantDocs) {
				Rq.add(SR.getDocument().getId());
			}
			
			//Intersection
			IntersectRG = Rq;
			IntersectRG.retainAll(Gq);
			
			//Find Precision
			Precision = IntersectRG.size()/Rq.size();
			//Find Recall
			Recall = IntersectRG.size()/Gq.size();
			//Find F1
			if(Precision + Recall != 0) {						//prevent case of 0 divider
				F1 = (2*Precision*Recall) / (Precision + Recall);
			}
			//Sum up all Precision Recall and F1
			totPrecision +=  Precision;
			totRecall += Recall;
			totF1 += F1;
		}
		//Calculate the average of Precision Recall and F1
		avgPrecision = totPrecision/queries.size();
		avgRecall = totRecall/queries.size();
		avgF1 = totF1/queries.size();
		
		double[] Result = {avgPrecision, avgRecall, avgF1};		//Pack and return the updated average Precision, Recall, F1 into result array
		return Result;
		/****************************************************************/
	}
}
