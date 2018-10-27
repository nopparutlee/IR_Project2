//Name: Nopparut Li
//Section: 1
//ID: 5988015

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TFIDFSearcher extends Searcher
{	
	//idea:   <term,<doc,freq>>
	Map<String,TreeMap<Integer,Integer>> termDocumentFreq = new TreeMap<String,TreeMap<Integer,Integer>>();

	//idea:   <term,<doc,weight>>
	Map<String,TreeMap<Integer,Double>> termDocumentWeight = new TreeMap<String,TreeMap<Integer,Double>>();
	
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
						//if the term is new for this document
						termDocumentFreq.get(token).put(doc.getId(), 1);
					}
				}
				else{
					//new term
					termDocumentFreq.put(token, new TreeMap<Integer,Integer>());
					//well it is new so
					termDocumentFreq.get(token).put(doc.getId(), 1);
					//also we can just instantiate for the weight
					//just leave the docid and weight value as null becuase reasons
					termDocumentWeight.put(token, new TreeMap<Integer,Double>());
				}
			}
		}
		//we suppose to get all TF and DF for now
		
		//time to calculate for TF-IDF weight
		
		for(String term:termDocumentFreq.keySet()){
			for(Integer docId:termDocumentFreq.get(term).keySet()){
				//well it is more likely that there would not be any disturbing variable yes?
				termDocumentWeight.get(term).put(docId, tf(termDocumentFreq.get(term).get(docId)) * idf(documents.size(),termDocumentFreq.get(term).size()));
			}
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
		return Math.log10(n/df);
	}
	
	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		
		
		
		return null;
		/***********************************************/
	}
}
