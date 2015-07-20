package com.lucene;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * apache lucene 建立索引源码
 * @author anonymous
 *
 */

public class IndexFiles {

	private IndexFiles() {
	}

	/** Index all text files under a directory. */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
//		String usage = "java org.apache.lucene.demo.IndexFiles"
//				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
//				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
//				+ "in INDEX_PATH that can be searched with SearchFiles";
		String indexPath = "lucene/indexes/files";
		String docsPath = "F:/Desk/Note";
		boolean create = true;
//		for (int i = 0; i < args.length; i++) {
//			if ("-index".equals(args[i])) {
//				indexPath = args[i + 1];
//				i++;
//			} else if ("-docs".equals(args[i])) {
//				docsPath = args[i + 1];
//				i++;
//			} else if ("-update".equals(args[i])) {
//				create = false;
//			}
//		}
//		if (docsPath == null) {
//			System.err.println("Usage: " + usage);
//			System.exit(1);
//		}

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			docDir.mkdirs();
		}	

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_10_4);
//			Analyzer analyzer = new PaodingAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(
					Version.LUCENE_4_10_4, analyzer);

			if (create) {
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}


			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}
	}

	static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {

				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException fnfe) {
					return;
				}

				try {

					Document doc = new Document();
					Field pathField = new StringField("path", file.getPath(),
							Field.Store.YES);
					doc.add(pathField);
					doc.add(new LongField("modified", file.lastModified(),
							Field.Store.NO));
					doc.add(new TextField("contents", new BufferedReader(
							new InputStreamReader(fis, StandardCharsets.UTF_8))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()),
								doc);
					}

				} finally {
					fis.close();
				}
			}
		}
	}
}
