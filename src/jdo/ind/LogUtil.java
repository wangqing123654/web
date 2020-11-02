package jdo.ind;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

	 
	public static void writerLabelLog(String msg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String name = "LABEL_LOG";
	    name +=format.format(new Date());
		File f = new File("C:\\JavaHis\\logs\\"+name+".log");
		BufferedWriter out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();// ���SPC.log�����ڣ��򴴽�һ�����ļ�
			}
			out = new BufferedWriter(new FileWriter(f, true));// ����true��ʾ�����׷�ӵ��ļ����ݵ�ĩβ��������ԭ��������
			out.write(msg);
			out.newLine(); // ����
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		writerLabelLog("���������1111");
	}

}
