package com.javahis.ui.opb;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: �����ײͷ����嵥�ײ���ϸ��Ϣ
 * </p>
 * 
 * <p>
 * Description: �����ײͷ����嵥�ײ���ϸ��Ϣ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author sunqy 20140630
 * @version 1.0
 */
public class OPBPackageListControl {
	
	private OPBPackageListControl(){
	}
	
	private static OPBPackageListControl packageList;
	
	public static OPBPackageListControl getInstance(){
		if(packageList == null){
			packageList = new OPBPackageListControl();
		}
		return packageList;
	}
	
	public TParm listParm(TParm parmIn){
		
		String code = "";//���ڴ����ײ����
		int count = parmIn.getCount();//���ݿ⹺���ײ����ݵ�����
		boolean engFlag = parmIn.getBoolean("engFlag");
		TParm result = new TParm();
		
		//�Ƿ���ͬһ���ײ����
		for (int i = 0; i < count; i++) {
			if(!code.equals(parmIn.getValue("PACKAGE_CODE",i))){
				code = parmIn.getValue("PACKAGE_CODE",i);
				
				if(engFlag){//Ӣ�Ĵ�ӡ��Ӣ������
					result.addData("RECEIPTTYPE", parmIn.getValue("PACKAGE_ENG_DESC",i));//Ӣ���վ����
				}else{
					result.addData("RECEIPTTYPE", parmIn.getValue("PACKAGE_DESC",i));//�վ����
				}
				
			}else{
				result.addData("RECEIPTTYPE", "");
			}
			if(engFlag){
				result.addData("PROJECTNAME", parmIn.getValue("ORDER_ENG_DESC",i));//��Ŀ����
				result.addData("UNIT", parmIn.getValue("UNIT_ENG_DESC",i));//��λ
			}else{
				result.addData("PROJECTNAME", parmIn.getValue("ORDER_DESC",i));//��Ŀ����
				result.addData("UNIT", parmIn.getValue("UNIT_CHN_DESC",i));//��λ
			}
			
			result.addData("QUANTITY", parmIn.getValue("ORDER_NUM", i));//����
			result.addData("EMPTY1", "");//�հ���
			result.addData("EMPTY2", "");//�հ���
		}
		result.setCount(count);
		result.addData("SYSTEM", "COLUMNS", "RECEIPTTYPE");
		result.addData("SYSTEM", "COLUMNS", "PROJECTNAME");
		result.addData("SYSTEM", "COLUMNS", "UNIT");
		result.addData("SYSTEM", "COLUMNS", "QUANTITY");
		result.addData("SYSTEM", "COLUMNS", "EMPTY1");
		result.addData("SYSTEM", "COLUMNS", "EMPTY2");
		return result;
	}
	
	
	
	
}
