package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

public class MEMPreColPackSalesSummaryControl extends TControl{
	
	private TTable table;
	private String accountUser="";
	private TParm printParm;
	private DecimalFormat df = new DecimalFormat("##########0.00");
	
	public void onInit(){
		
		table = (TTable) this.getComponent("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		
	}
	
	public void onQuery(){
		String startTime = getValueString("S_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		String endTime = getValueString("E_DATE");
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		
		String user = this.getValueString("USER");
		String whereSql = "";
		if(user.length() > 0){
			whereSql = " AND A.ACCOUNT_USER = '"+user+"'";
		}
		//收款
		String sqlS = "  SELECT C.CHN_DESC PACKAGE_CLASS, B.PACKAGE_DESC, A.COUNT COUNT_S, A.AR_AMT AR_AMT_S" +
				" FROM (  SELECT COUNT (PACKAGE_CODE) COUNT, PACKAGE_CODE, SUM (AR_AMT) AR_AMT" +
				" FROM (SELECT PACKAGE_CODE, AR_AMT  FROM (SELECT DISTINCT A.TRADE_NO, B.PACKAGE_CODE," +
				" B.PACKAGE_DESC, A.AR_AMT FROM MEM_PACKAGE_TRADE_M A,MEM_PAT_PACKAGE_SECTION B" +
				" WHERE A.TRADE_NO = B.TRADE_NO" +
				" AND A.ACCOUNT_DATE BETWEEN TO_DATE ('"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS')" + whereSql +
				" AND A.AR_AMT >= 0)) GROUP BY PACKAGE_CODE) A, MEM_PACKAGE B, SYS_DICTIONARY C" +
				" WHERE A.PACKAGE_CODE = B.PACKAGE_CODE AND B.PACKAGE_CLASS = C.ID" +
				" AND C.GROUP_ID = 'MEM_PACKAGE_CLASS' ORDER BY C.ID";
		TParm parmS = new TParm(TJDODBTool.getInstance().select(sqlS));
		
		//退款
		String sqlT = "SELECT C.CHN_DESC PACKAGE_CLASS,B.PACKAGE_DESC,A.COUNT COUNT_T, -A.AR_AMT AR_AMT_T" +
				" FROM(SELECT COUNT(PACKAGE_CODE) COUNT,PACKAGE_CODE,SUM(AR_AMT) AR_AMT " +
				" FROM (SELECT PACKAGE_CODE,AR_AMT  " +
				" FROM (SELECT DISTINCT A.TRADE_NO, B.PACKAGE_CODE,B.PACKAGE_DESC,A.AR_AMT  " +
				" FROM MEM_PACKAGE_TRADE_M A,MEM_PAT_PACKAGE_SECTION B" +
				" WHERE A.TRADE_NO = B.REST_TRADE_NO" +
				" AND A.ACCOUNT_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS')" + whereSql +
				" AND A.AR_AMT < 0)) GROUP BY PACKAGE_CODE) A,MEM_PACKAGE B,SYS_DICTIONARY C" +
				" WHERE A.PACKAGE_CODE = B.PACKAGE_CODE AND B.PACKAGE_CLASS = C.ID" +
				" AND C.GROUP_ID = 'MEM_PACKAGE_CLASS' ORDER BY C.ID";
		TParm parmT = new TParm(TJDODBTool.getInstance().select(sqlT));
		
		TParm parm = new TParm();
		for (int i = 0; i < parmS.getCount(); i++) {
			parm.addRowData(parmS, i);
			parm.addData("COUNT_T", 0);
			parm.addData("AR_AMT_T", 0.00);
			parm.addData("AR_AMT", parmS.getDouble("AR_AMT_S", i));
			
		}
		parm.setCount(parm.getCount("PACKAGE_CLASS"));
		
		if(parm.getCount() > 0){
			TParm parm1 = new TParm(); //有退费信息没有收费信息数据
			for (int i = 0; i < parmT.getCount(); i++) {
				boolean flg = true;
				for (int j = 0; j < parm.getCount(); j++) {
					if(parmT.getValue("PACKAGE_CLASS", i).equals(parm.getValue("PACKAGE_CLASS", j)) &&
							parmT.getValue("PACKAGE_DESC", i).equals(parm.getValue("PACKAGE_DESC", j))
							){
						parm.setData("COUNT_T", j, parmT.getInt("COUNT_T", i));
						parm.setData("AR_AMT_T", j, parmT.getDouble("AR_AMT_T", i));
						parm.setData("AR_AMT", j, parm.getDouble("AR_AMT_S", j)- parmT.getDouble("AR_AMT_T", i));
						flg = false;
						break;
					}
	
				}
				
				if(flg){
					parm1.addRowData(parmT, i);
					parm1.addData("COUNT_S", "0");
					parm1.addData("AR_AMT_S", "0.00");
					parm1.addData("AR_AMT", -parmT.getDouble("AR_AMT_T", i));
				}
				
			}
			
			for (int i = 0; i < parm1.getCount("PACKAGE_CLASS"); i++) {
				parm.addRowData(parm1, i);
			}
			
			parm.setCount(parm.getCount("PACKAGE_CLASS"));
			
		}else{
			for (int i = 0; i < parmT.getCount(); i++) {
				parm.addRowData(parmT, i);
				parm.addData("COUNT_S", "0");
				parm.addData("AR_AMT_S", "0.00");
				parm.addData("AR_AMT", -parmT.getDouble("AR_AMT_T", i));
				
			}
			parm.setCount(parm.getCount("PACKAGE_CLASS"));
		}
		
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			printParm = new TParm();
			return;
		}
		
		printParm = new TParm();
		printParm.addRowData(parm, 0);
		int countS = parm.getInt("COUNT_S", 0);
		int countT = parm.getInt("COUNT_T", 0);
		double arAmtS = parm.getDouble("AR_AMT_S", 0);
		double arAmtT = parm.getDouble("AR_AMT_T", 0);
		double arAmt = parm.getDouble("AR_AMT", 0);
		
		int countSSum = parm.getInt("COUNT_S", 0);
		int countTSum = parm.getInt("COUNT_T", 0);
		double arAmtSSum = parm.getDouble("AR_AMT_S", 0);
		double arAmtTSum = parm.getDouble("AR_AMT_T", 0);
		double arAmtSum = parm.getDouble("AR_AMT", 0);
		
		for (int i = 1; i < parm.getCount(); i++) {
			//总计
			countSSum += parm.getInt("COUNT_S", i);
			countTSum += parm.getInt("COUNT_T", i);
			arAmtSSum += parm.getDouble("AR_AMT_S", i);
			arAmtTSum += parm.getDouble("AR_AMT_T", i);
			arAmtSum += parm.getDouble("AR_AMT", i);
			
			if(!parm.getValue("PACKAGE_CLASS", i-1).equals(parm.getValue("PACKAGE_CLASS", i))){
				printParm.addData("PACKAGE_CLASS", "小计:");
				printParm.addData("PACKAGE_DESC", "");
				printParm.addData("COUNT_S", countS);
				printParm.addData("AR_AMT_S", arAmtS);
				printParm.addData("COUNT_T", countT);
				printParm.addData("AR_AMT_T", arAmtT);
				printParm.addData("AR_AMT", arAmt);
				
				countS = 0;
				countT = 0;
				arAmtS = 0;
			    arAmtT = 0;
				arAmt = 0;
				
			}
			
			 printParm.addRowData(parm, i);
			 countS += parm.getInt("COUNT_S", i);
			 countT += parm.getInt("COUNT_T", i);
			 arAmtS += parm.getDouble("AR_AMT_S", i);
			 arAmtT += parm.getDouble("AR_AMT_T", i);
			 arAmt += parm.getDouble("AR_AMT", i);
			
		}
		printParm.addData("PACKAGE_CLASS", "小　　计:");
		printParm.addData("PACKAGE_DESC", "");
		printParm.addData("COUNT_S", countS);
		printParm.addData("AR_AMT_S", arAmtS);
		printParm.addData("COUNT_T", countT);
		printParm.addData("AR_AMT_T", arAmtT);
		printParm.addData("AR_AMT", arAmt);
		printParm.addData("PACKAGE_CLASS", "总　　计:");
		printParm.addData("PACKAGE_DESC", "");
		printParm.addData("COUNT_S", countSSum);
		printParm.addData("AR_AMT_S", df.format(arAmtSSum));
		printParm.addData("COUNT_T", countTSum);
		printParm.addData("AR_AMT_T", df.format(arAmtTSum));
		printParm.addData("AR_AMT", df.format(arAmtSum));
		
		printParm.setCount(printParm.getCount("PACKAGE_CLASS"));	
		
		for (int i = 0; i < printParm.getCount(); i++) {
			printParm.setData("AR_AMT_S", i, df.format(printParm.getDouble("AR_AMT_S", i)));
			printParm.setData("AR_AMT_T", i, df.format(printParm.getDouble("AR_AMT_T", i)));
			printParm.setData("AR_AMT", i, df.format(printParm.getDouble("AR_AMT", i)));

		}

		table.setParmValue(printParm);
		
		
		
	}
	
	public void onPrint(){
		if(printParm.getCount("PACKAGE_CLASS") < 0){
			this.messageBox("没有要打印的数据");
			return;
		}
		String userList = "";
		if(this.getValueString("USER").length() > 0){
			userList = this.getText("USER");
		}else{
			String startTime = getValueString("S_DATE");
			startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
			String endTime = getValueString("E_DATE");
			endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
			
			String sql = "SELECT DISTINCT B.USER_NAME FROM MEM_PACKAGE_TRADE_M A,SYS_OPERATOR B " +
					" WHERE A.ACCOUNT_DATE BETWEEN TO_DATE('"+startTime+"','YYYYMMDDHH24MISS')" +
					" AND TO_DATE('"+endTime+"','YYYYMMDDHH24MISS') AND A.ACCOUNT_USER = B.USER_ID";
			TParm userParm = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < userParm.getCount(); i++) {
				userList += userParm.getValue("USER_NAME", i)+",";
			}
			
			if(userList.length() > 0){
				userList = userList.substring(0, userList.length()-1);
			}
		}
		
		// 打印数据
		TParm date = new TParm();
		String startDate = getValueString("S_DATE").replaceAll("-", "/").substring(0, 19);
		String endDate = getValueString("E_DATE").replaceAll("-", "/").substring(0, 19);
		Timestamp d = SystemTool.getInstance().getDate();
		date.setData("TITLE", "TEXT", "门诊预收款套餐销售汇总表");
		date.setData("DATE","TEXT","结算日期："+startDate+"--"+endDate);
		date.setData("USER",  "收费员："+userList);
		date.setData("PRINT_DATE", "TEXT", "打印日期："+d.toString().replace("-", "/").substring(0, 19));
		
		printParm.addData("SYSTEM", "COLUMNS", "PACKAGE_CLASS");
		printParm.addData("SYSTEM", "COLUMNS", "PACKAGE_DESC");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT_S");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT_S");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT_T");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT_T");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
		date.setData("TABLE", printParm.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\MEM\\MEMPreColPackSalesSummaryPrint.jhw", date);
		
		
	}
	
	public void onClear(){
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		table.removeRowAll();
		
	}
	
	

}
