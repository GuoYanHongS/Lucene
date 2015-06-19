package junit.test;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.FSDirectory;

public class TxtIndexSearcher { 
	 @SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception{ 
	    String queryStr = "lucene"; 
	    //This is the directory that hosts the Lucene index 
       File indexDir = new File("D:\\luceneIndex"); 
       FSDirectory directory = FSDirectory.open(indexDir); 
       DirectoryReader open = IndexReader.open(directory);
       IndexSearcher searcher = new IndexSearcher(open); 
       if(!indexDir.exists()){ 
       	 System.out.println("The Lucene index is not exist"); 
       	 return; 
       } 
       Term term = new Term("contents",queryStr.toLowerCase()); 
       TermQuery luceneQuery = new TermQuery(term); 
//       searcher.search
//       for(int i = 0; i < hits.length(); i++){ 
//       	 Document document = hits.doc(i); 
//       	 System.out.println("File: " + document.get("path")); 
//       } 
	 } 
}