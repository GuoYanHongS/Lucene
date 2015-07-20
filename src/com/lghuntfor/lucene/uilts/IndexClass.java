package com.lghuntfor.lucene.uilts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.lghuntfor.lucene.annotation.Searchable;
import com.lghuntfor.lucene.annotation.SearchableId;
import com.lghuntfor.lucene.annotation.SearchableProperty;
import com.lghuntfor.lucene.enums.Index;
import com.lghuntfor.lucene.enums.Store;

public class IndexClass {
	
	private static String INDEXPATH = null;
	static {
		InputStream inStream = null;
		Properties props = null;
		try {
			inStream = IndexClass.class.getClassLoader().getResourceAsStream("/search.properties");
			props = new Properties();
			props.load(inStream);
			INDEXPATH = props.getProperty("indexPath");
		} catch(FileNotFoundException e) {
			throw new RuntimeException("未找到search.properties配置文件",e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static <T> void index(Class<T> clazz, Collection<T> coll) {
		Searchable searchable = clazz.getAnnotation(Searchable.class);
		if(searchable==null) {
			throw new RuntimeException(clazz.getName()+ ",此类无法创建索引, 请添加@Searchable注解");
		}
		String alias = searchable.alias();
		INDEXPATH += alias;
		
		try {
			Directory dir = FSDirectory.open(new File(INDEXPATH));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_4, analyzer);
			boolean create = true;
			if (create ) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}


			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, clazz, coll);

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static<T> void indexDocs(IndexWriter writer, Class<T> clazz, Collection<T> coll) throws Exception {
		try {
			for(T t : coll) {
				Document doc = new Document();
				java.lang.reflect.Field[] fields = clazz.getFields();
				for(java.lang.reflect.Field field : fields) {
					SearchableId searchableId = field.getAnnotation(SearchableId.class);
					if(searchableId!=null) {
						String name = searchableId.name();
						Store store = searchableId.store();
						Index index = searchableId.index();
						
						Object propertyValue = getField(t, field.getName());
						
						Class<?> type = field.getType();
						
						if(type.equals(Integer.class)) {
							
						}
						
						
						continue;
					}
					SearchableProperty searchableProperty = field.getAnnotation(SearchableProperty.class);
					if(searchableProperty!=null) {
						
					}
					writer.addDocument(doc);
				}
			}
		} finally {
			
		}
	}
	
	
	@SuppressWarnings("unused")
	private static Object getField(Object obj, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		java.lang.reflect.Field field = obj.getClass().getField(fieldName);
		Class<?> type = field.getType();
		Object object = field.get(obj);
		
		return object;
	}
}
