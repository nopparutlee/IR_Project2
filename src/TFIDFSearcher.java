//Name: Nopparut Li
//Section: 1
//ID: 5988015

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TFIDFSearcher extends Searcher
{	
	//structure:   <term,<doc,freq>>
	Map<String,TreeMap<Integer,Integer>> termDocumentFreq = new TreeMap<String,TreeMap<Integer,Integer>>();

	//structure:   <term,<doc,weight>>
	Map<String,TreeMap<Integer,Double>> termDocumentWeight = new TreeMap<String,TreeMap<Integer,Double>>();
	
	//structure:   <docId, docNorm>
	//purely a workaround, supposed to cuase some overhead but I don't have any idea how to fix it better
	Map<Integer,Double> docsNorm = new TreeMap<Integer, Double>();
	
	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		//so, the document are already parse, aka. all are ready to be used
		//veriable we have now is List<Document> documents, containing all document that is parsed
		//each document have properties:
		//Integer id: doc id
		//String rawtext: rawtext of the document
		//List<String> tokens: list of token retrieved from raw text, ready to be used
		
		//TDIDF Searcher first job is to make index 
		//well we gotta works with term-document frequency matrix
		long startTime = System.currentTimeMillis();
		for(Document doc:documents){
			for(String token:doc.getTokens()){
				if(termDocumentFreq.containsKey(token)){
					//if term is already present
					if(termDocumentFreq.get(token).containsKey(doc.getId())){
						//if term is already present for this document 
						//get the old frequency and increase it by 1
						termDocumentFreq.get(token).put(doc.getId(), termDocumentFreq.get(token).get(doc.getId())+1);
					}
					else{
						//if the term is new only for this document
						termDocumentFreq.get(token).put(doc.getId(), 1);
					}
				}
				else{
					//very new term
					termDocumentFreq.put(token, new TreeMap<Integer,Integer>());
					//well it is new so
					termDocumentFreq.get(token).put(doc.getId(), 1);
					//also we can just instantiate for the weight
					//just leave the docid and weight value as null becuase reasons
					termDocumentWeight.put(token, new TreeMap<Integer,Double>());
				}
			}
		}
		//we supposed to get all TF and DF for now
		
		//time to calculate for TF-IDF weight
		
		for(String term:termDocumentFreq.keySet()){
			for(Integer docId:termDocumentFreq.get(term).keySet()){
				//well it is more likely that there would not be any disturbing variable yes?
				double weight = tf(termDocumentFreq.get(term).get(docId)) * idf(documents.size(),termDocumentFreq.get(term).size());
				termDocumentWeight.get(term).put(docId, weight);
				if(docsNorm.containsKey(docId)){
					docsNorm.put(docId, docsNorm.get(docId) + weight * weight);
				}
				else{
					docsNorm.put(docId, weight * weight);
				}
			}
		}
		
		for(Integer docId:docsNorm.keySet()){
			docsNorm.put(docId, Math.sqrt(docsNorm.get(docId)));
		}
		
		//ok if I didn't fuck anything up then we should already have weight-calculated matrix
		//in case that we want to free some memory the uncomment these lines
		//termDocumentFreq.clear();
		//System.gc();
		long endTime = System.currentTimeMillis();
		System.out.println("the matrix is now ready for search's calculation");
		System.out.println("@@@ Total time used for first preparation: "+(endTime-startTime)+" milliseconds.");
		
		/***********************************************/
	}
	
	private double tf(int tf){
		return (tf == 0 ? 0.0 : 1 + Math.log10(tf));
	}
	
	private double idf(int n, int df){
		return Math.log10(1+ (((double)n)/((double)df)));
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		/* well I was so lazy to use multiple line comment before but whatever
		 * comment consistency is not really need to be consider for good programming practice right?(jk)
		 * 
		 * @@@param
		 * String 	queryString	: string of the query, will needed to be tokenized before using
		 * int		k			: number of results needed to be returned, must be descending sorted based of relevence
		 * 
		 * @@@property needed to be used
		 * 		<term	,		<doc	,weight>>
		 * Map	<String	,TreeMap<Integer,Double>> termDocumentWeight
		 * 						: weight of each term in each document
		 * 
		 * List<Document> documents
		 * 						: list of documents
		 * 
		 * 		Integer id: doc id                                                          
		 * 		String rawtext: rawtext of the document                                     
		 * 		List<String> tokens: list of token retrieved from raw text, ready to be used
		 * 
		 * @@@ Result object structure
		 * Document document	: the reference to 'that document'
		 * double 	score		: score for each result
		 */
		
		//prepare result list
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		//tokenize the query
		List<String> queryTokens = tokenize(queryString);
		
		//now we change the query into the tfidf weight
		/*Map<String, Double> queryWeight = new TreeMap<String, Double>();
		for(String token:queryTokens){
			if(queryWeight.containsKey(token)){
				queryWeight.put(token, queryWeight.get(token)+idf(documents.size(),termDocumentWeight.get(token).size()));
			}
			else{
				queryWeight.put(token, idf(documents.size(),termDocumentWeight.get(token).size()));
			}
		}*/
		//Map it to make term frequency
		Map<String, Integer> queryTermFreq = new TreeMap<String, Integer>();
		for(String token:queryTokens){
			if(queryTermFreq.containsKey(token)){
				queryTermFreq.put(token, queryTermFreq.get(token)+1);
			}
			else{
				queryTermFreq.put(token, 1);
			}
		}
		
		//make those frequency into weight
		Map<String, Double> queryWeight = new TreeMap<String, Double>();
		for(String token:queryTermFreq.keySet()){
			if(!termDocumentWeight.containsKey(token)){
				continue;
			}
			double weight = tf(queryTermFreq.get(token)) * idf(documents.size(),termDocumentWeight.get(token).size());
			queryWeight.put(token, weight);
		}
		
		//fun time
		//inner product of hell
		
		//just calculate norm of query for efficientcy
		//of course, we don't write efficient code everywhere so we do what we could
		double queryNorm = 0.0;
		for(String token:queryWeight.keySet()){
			queryNorm += queryWeight.get(token)*queryWeight.get(token);
		}
		queryNorm = Math.sqrt(queryNorm);
		
		for(Document doc:documents){
			Set<String> tokensToCalculate = new HashSet<String>(doc.getTokens());
			tokensToCalculate.retainAll(queryWeight.keySet());
			double score = 0.0;
			double docNorm = docsNorm.get(doc.getId());
			for(String token:tokensToCalculate){
				score += queryWeight.get(token) * termDocumentWeight.get(token).get(doc.getId());
			}
			score = score / (queryNorm * docNorm);
			results.add(new SearchResult(doc, score));
		}

		Collections.sort(results);
		
		results = results.subList(0, k);
		
		return results;
		/***********************************************/
	}
}
