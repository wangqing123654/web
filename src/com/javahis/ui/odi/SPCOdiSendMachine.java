package com.javahis.ui.odi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 * ����סԺ��ҩ��
 * @author Administrator
 *
 */
public class SPCOdiSendMachine {
	
	/**
	 * ����txt�ļ�
	 * @param parm
	 * @throws IOException 
	 */
	public void createTxt(TParm parm) throws IOException{
		
		TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			result.addData("1", parm.getValue("", i)) ;//��������
			result.addData("2", parm.getValue("", i)) ;//������
			result.addData("3", parm.getValue("", i)) ;//����
			result.addData("4", parm.getValue("", i)) ;//����ҽ������
			result.addData("5", "") ;//Ĭ�ϲ���
			result.addData("6", "99") ;//Ĭ��
			result.addData("7", parm.getDouble("", i)) ;//���� Ƭ
			result.addData("8", parm.getValue("", i)) ;//ҽ������
			result.addData("9", parm.getValue("", i)) ;//Ƶ��
			result.addData("10", parm.getValue("", i)) ;//��ʼʱ��
			result.addData("11", parm.getValue("", i)) ;//����ʱ�䣨��ʼʱ�������ʱ����һ��ֵ��
			result.addData("12", "") ;//Ĭ��
			result.addData("13", parm.getValue("", i)) ;//ҽ����ע
			result.addData("14", parm.getValue("", i)) ;//סԺ��
			result.addData("15", "") ;//Ĭ��
			result.addData("16", parm.getValue("", i)) ;//��������
			result.addData("17", parm.getValue("", i)) ;//�Ա�
			result.addData("18", parm.getValue("", i)) ;//������
			result.addData("19", parm.getValue("", i)) ;//������
			result.addData("20", "0") ;//Ĭ��ֵ0
			result.addData("21", parm.getValue("", i)) ;//ҽԺ����
			result.addData("22", "") ;//Ĭ�ϲ���
			result.addData("23", "") ;//Ĭ�ϲ���
			result.addData("24", "") ;//Ĭ�ϲ���
			result.addData("25", "") ;//Ĭ�ϲ���
			result.addData("26", "") ;//Ĭ�ϲ���
			result.addData("27", "") ;//Ĭ�ϲ���
			result.addData("28", "") ;//Ĭ�ϲ���
			result.addData("29", "") ;//Ĭ�ϲ���
			result.addData("30", "") ;//Ĭ�ϲ���
			result.addData("31", "") ;//Ĭ�ϲ���
			result.addData("32", "") ;//Ĭ�ϲ���
			result.addData("33", "") ;//Ĭ�ϲ���
			result.addData("34", "") ;//Ĭ�ϲ���
			result.addData("35", "") ;//Ĭ�ϲ���
			result.addData("36", "") ;//Ĭ�ϲ���
		}
	
		result.setCount(parm.getCount()) ;
			//·��
			   String src = "D:\\text.txt";
			   if (parm != null) {
			    File f = new File(src);
			    BufferedWriter bw = null;
			    bw = new BufferedWriter(new FileWriter(f));
			    for (int i = 0; i < result.getCount(); i++) {
			    	String str = txtFormat(result.getRow(i)) ;
			//д���ļ�
			   bw.write(str + "\r\n");
			    }
			    bw.flush();
			    bw.close();
			   } 
		
	}
		
		


	/**
	 * ת��txt�ļ���ʽ
	 * @param parm
	 * @return
	 */
	public String txtFormat(TParm parm){
		
		int[] size = {20, 30, 50, 26, 3,2,5,50,20,6,6,8,50,50,50,10,6,20,20,1,30,30,30,
				      30,30,30,30,30,30,30,30,30,30,30,30,30} ;
		String[] column = {"1","2","3","4","5","6","7","8","9","10","11","12",
				"13","14","15","16","17","18","19","20","21","22","23","24",
				"25","26","27","28","29","30","31","32","33","34","35","36"} ;
		String str = "";
		for(int i=0;i<column.length;i++){
			if(i==6){
				String strQty= StringTool.round(parm.getDouble(column[i]),2)+"" ;
				str += strQty +String.format("%1$0"+(5-strQty.length())+"d",0) ;
			}
			else	{
				int n = BinaryString(parm.getValue(column[i])) ;
				int stringSize = size[i]-n ;
				str += String.format("%"+"-"+stringSize+"s", parm.getValue(column[i]));
			}
		}
		return str ;
	}
	/**
	 *���㺺�ָ���
	 * @param args
	 */
	public  int BinaryString(String str) {
		String len="";
		int j=0;
		char[] c=str.toCharArray();
		for(int i=0;i<c.length;i++){
			len=Integer.toBinaryString(c[i]);
			if(len.length()>8)
				j++;
		}
		System.out.println("���������ַ���"+j);
		return j ;
	}

}
