package junit.test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class IndexUtil {
	private String[] idArr = {"1","2","3","4","5","6"};
	private String[] emailArr = {"abc@us.ibm.com","ert@cn.ibm.com","lucy@us.ibm.com",
	"rock@cn.ibm.com","test@126.com","deploy@163.com"};
	private String[] contentArr = {
	"welcome to Lucene,I am abc","This is ert,I am from China",
	"I'm Lucy,I am english","I work in IBM",
	"I am a tester","I like Lucene in action"
	};
	
	private String[] nameArr = {"abc","ert","lucy","rock","test","deploy"};
	private Directory directory = null;
	public void index() {
	IndexWriter writer = null;
	try {
	directory = FSDirectory.open(new File("/lucene/indexes"));
	IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_47,
	new StandardAnalyzer(Version.LUCENE_47));
	conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
	LogMergePolicy mergePolicy = new LogDocMergePolicy();
	mergePolicy.setMergeFactor(10);
	mergePolicy.setMaxMergeDocs(10);
	conf.setMaxBufferedDocs(10);
	writer = new IndexWriter(directory, conf);
	Document doc = null;
	int date = 1;
	for(int i=0;i<idArr.length;i++) {
	doc = new Document();
	doc.add(new StringField("id",idArr[i],Field.Store.YES));
	doc.add(new StringField("email",emailArr[i],Field.Store.YES));
	doc.add(new StringField("content",contentArr[i],Field.Store.YES));
	doc.add(new StringField("name",nameArr[i],Field.Store.YES));
	doc.add(new StringField("date","2014120"+date+"222222",Field.Store.YES));
	writer.addDocument(doc);
	date++;
	}
	
	//新的版本对 Field 进行了更改，StringField 索引但是不分词、StoreField 至存储不索引、TextField 索引并分词
	} catch (CorruptIndexException e) {
	e.printStackTrace();
	} catch (LockObtainFailedException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} finally {
	try {
	if(writer!=null)writer.close();
	} catch (CorruptIndexException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
	}
	}
	
	public static void main(String args[]){
	IndexUtil indexUtil = new IndexUtil();
	indexUtil.index();
	}
}