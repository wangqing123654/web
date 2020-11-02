/*
 * CopyRright (c) 2009-2015 www.fdauto.com
 */
package com.javahis.web.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.index.IndexWriter;

import com.javahis.web.lucene.doc.IDocHandler;
import com.javahis.web.lucene.doc.DocHandlerFactory;

/**
 * ���������ṩ��������
 */
public class DocIndexHelper {

	private static final Log log = LogFactory.getLog(DocIndexHelper.class);
	private static final String INDEX_ROOT_DIR = "";

	/**
	 * ���������Ĵ���
	 *
	 * @param createDir
	 *            ָ��Ҫ�����������ļ�Ŀ¼
	 * @param indexDir
	 *            ָ������������Ĵ��Ŀ¼
	 * @return
	 */
	public static boolean buildIndexDir(String createDir, String indexDir) {
		long startIndexTime = System.currentTimeMillis();

		IndexWriter indexWriter = null;
		try {
			File createFiles = new File(createDir);
			if (!createFiles.exists()) {
				throw new RuntimeException(createDir + "---->ָ��Ҫ����������Ŀ¼������!!!");
			}
			File indexFile = new File(INDEX_ROOT_DIR + indexDir);
			if (!indexFile.exists()) {
				indexFile.mkdir();
			}

			indexWriter = LuceneDocIndexFactory.getDefaultDocIndex()
					.getIndexWriter(indexDir, true);
			indexDirs(createFiles, indexWriter);
			indexWriter.optimize();
			if (log.isDebugEnabled()) {
				log.debug("�����������õ�ʱ��Ϊ---->"
						+ (System.currentTimeMillis() - startIndexTime));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (indexWriter != null)
				try {
					indexWriter.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}

	/** �ݹ�����Ҫ�����������ļ� */
	private static void indexDirs(File createFiles, IndexWriter indexWriter) {

		if (createFiles.isDirectory()) {
			String[] files = createFiles.list();
			Arrays.sort(files);
			for (String file : files) {

				indexDirs(new File(createFiles, file), indexWriter);
			}
		}

		if (createFiles.isFile()) {

			try {
				IDocHandler docHandler = DocHandlerFactory
						.getKidDocHandler(createFiles.getPath());
                                // ��û�л�ȡ����.ppt�Ĵ�����ʱ,���Բ�����
				if (docHandler != null)
				{
					indexWriter.addDocument(docHandler.getDocument());
                                        //indexWriter.addIndexes();
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void indexDirectory(IndexWriter writer,File dir) throws Exception  {
		File [] files = dir.listFiles();
		for(int i = 0 ; i < files.length ; i++){
			File f = files[i];
			if(f.isDirectory()){
				indexDirectory(writer, f);
			}else if(f.getName().indexOf("CDA") != -1){
				
			}
		}
	}
	
	private static void indexFile(IndexWriter writer,File f)throws Exception {
		if(f.isHidden() || !f.exists() || !f.canRead()){
			return ;
		}
		
		Document doc = new Document();
		 
		
	}
	
}
