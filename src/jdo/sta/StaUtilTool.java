package jdo.sta;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * Title: ҽ�ƹ�����
 * </p>
 * 
 * <p>
 * Description: ҽ�ƹ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 * 
 * 
 */
public class StaUtilTool {

	public StaUtilTool() {

	}

	/**
	 * �������sourceFilePathĿ¼�µ�Դ�ļ�,�����fileName���Ƶ�ZIP�ļ�,����ŵ�zipFilePath��
	 * 
	 * @param sourceFilePath
	 *            ��ѹ�����ļ�·��
	 * @param zipFilePath
	 *            ѹ������·��
	 * @param fileName
	 *            ѹ�����ļ�������
	 * @return flag
	 */
	public static boolean fileToZip(String sourceFilePath, String GfileName,
			String zipFilePath, String fileName) {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		if (sourceFile.exists() == false) {
			// System.out.println(">>>>>> ��ѹ�����ļ�Ŀ¼��" + sourceFilePath
			// + " ������. <<<<<<");
		} else {
			try {
				File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
				if (zipFile.exists()) {
					// System.out.println(">>>>>> " + zipFilePath + " Ŀ¼�´�������Ϊ��"
					// + fileName + ".zip" + " ����ļ�. <<<<<<");
				} else {
					File[] sourceFiles = sourceFile.listFiles();
					if (null == sourceFiles || sourceFiles.length < 1) {
						// System.out.println(">>>>>> ��ѹ�����ļ�Ŀ¼��" +
						// sourceFilePath
						// + " ���治�����ļ�,����ѹ��. <<<<<<");
					} else {
						fos = new FileOutputStream(zipFile);
						zos = new ZipOutputStream(new BufferedOutputStream(fos));
						byte[] bufs = new byte[1024 * 10];
						for (int i = 0; i < sourceFiles.length; i++) {
							if (GfileName.equals(sourceFiles[i].getName())) {
								// ����ZIPʵ��,����ӽ�ѹ����
								ZipEntry zipEntry = new ZipEntry(sourceFiles[i]
										.getName());
								zos.putNextEntry(zipEntry);
								// ��ȡ��ѹ�����ļ���д��ѹ������
								fis = new FileInputStream(sourceFiles[i]);
								bis = new BufferedInputStream(fis, 1024 * 10);
								int read = 0;
								while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
									zos.write(bufs, 0, read);
								}
							}
						}
						flag = true;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				// �ر���
				try {
					if (null != bis)
						bis.close();
					if (null != zos)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}

		return flag;
	}

	/**
	 * ��ѹ�ļ�
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 * @throws Exception
	 */
	public static boolean ZipTofile(String zipFileName, String outputDirectory) {
		boolean flag = false;
		ZipInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new ZipInputStream(new FileInputStream(zipFileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		ZipEntry z;
		try {
			while ((z = in.getNextEntry()) != null) {
				// System.out.println("���ڽ�ѹ " + z.getName());
				if (z.isDirectory()) {
					String name = z.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
					// System.out.println("����Ŀ¼ " + outputDirectory
					// + File.separator + name);
				} else {
					File f = new File(outputDirectory + File.separator
							+ z.getName());
					f.createNewFile();
					out = new FileOutputStream(f);
					int b;
					while ((b = in.read()) != -1)
						out.write(b);
					out.close();
				}
			}
			flag = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			// �ر���
			try {
				if (null != out)
					out.close();
				if (null != in)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return flag;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // ɾ�����ļ���
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// ɾ��ָ���ļ����������ļ�
	// param path �ļ�����������·��
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * ���ļ������ZIPѹ���ļ�,main��������
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// String sourceFilePath = "C:\\JavaHis\\StaGenData";
		// String zipFilePath = "C:\\JavaHis\\StaGenData";
		// String fileName = "hqms_20121207095829.dbf";
		// boolean flag = StaUtilTool.fileToZip(sourceFilePath, fileName,
		// zipFilePath, fileName);
		try {
			boolean flag =delAllFile("D:\\360Downloads\\");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
