package com.javahis.ui.opb;

import com.dongyang.data.TParm;

/**
 * <p>
 * Title: 门诊套餐费用清单套餐详细信息
 * </p>
 * 
 * <p>
 * Description: 门诊套餐费用清单套餐详细信息
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
		
		String code = "";//用于储存套餐类别
		int count = parmIn.getCount();//数据库购买套餐数据的行数
		boolean engFlag = parmIn.getBoolean("engFlag");
		TParm result = new TParm();
		
		//是否是同一个套餐类别
		for (int i = 0; i < count; i++) {
			if(!code.equals(parmIn.getValue("PACKAGE_CODE",i))){
				code = parmIn.getValue("PACKAGE_CODE",i);
				
				if(engFlag){//英文打印传英文数据
					result.addData("RECEIPTTYPE", parmIn.getValue("PACKAGE_ENG_DESC",i));//英文收据类别
				}else{
					result.addData("RECEIPTTYPE", parmIn.getValue("PACKAGE_DESC",i));//收据类别
				}
				
			}else{
				result.addData("RECEIPTTYPE", "");
			}
			if(engFlag){
				result.addData("PROJECTNAME", parmIn.getValue("ORDER_ENG_DESC",i));//项目名称
				result.addData("UNIT", parmIn.getValue("UNIT_ENG_DESC",i));//单位
			}else{
				result.addData("PROJECTNAME", parmIn.getValue("ORDER_DESC",i));//项目名称
				result.addData("UNIT", parmIn.getValue("UNIT_CHN_DESC",i));//单位
			}
			
			result.addData("QUANTITY", parmIn.getValue("ORDER_NUM", i));//数量
			result.addData("EMPTY1", "");//空白列
			result.addData("EMPTY2", "");//空白列
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
