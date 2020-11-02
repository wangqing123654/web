package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: 活动套餐使用明细查询</p>
 *
 * <p>Description: 活动套餐使用明细查询</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author huangtt 20150929
 * @version 1.0
 */
public class MEMPackageActivityDetialControl extends TControl{
	private TTable packTable;
	TParm tableParm;
	public void onInit(){
		initPage();
   	 	packTable = (TTable) this.getComponent("TABLE");
	}
	
	
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {

		String sDate = this.getValueString("S_DATE").substring(0, 19).replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
    	String eDate = this.getValueString("E_DATE").substring(0, 19).replace("-", "").replace("/", "").replace(" ", "").replace(":", "");
    	String mrNo = this.getValueString("MR_NO");
    	String packageCode = this.getValueString("PACKAGE_CODE");
    	String where1 = "";
    	String where2 = "";
    	if(mrNo.length()>0){
    		where1=" AND K.MR_NO='"+mrNo+"'";
    	}
    	if(packageCode.length()>0){
    		where2=" AND K.PACKAGE_CODE='"+packageCode+"'";
    	}
    	
		String sql = "SELECT J.PAT_NAME,K.MR_NO,K.COUNT,K.AR_AMT,K.PAY_TYPE01,K.PAY_TYPE02," +
				" K.BILL_DATE,'' USER1_DATE, '' USER2_DATE,K.PACKAGE_CODE,K.PACKAGE_DESC  FROM (  SELECT A.MR_NO," +
				" COUNT (A.MR_NO) COUNT,SUM (A.AR_AMT) AR_AMT,SUM (A.PAY_TYPE01) PAY_TYPE01," +
				" SUM (A.PAY_TYPE02) PAY_TYPE02, A.PACKAGE_CODE, A.PACKAGE_DESC, A.BILL_DATE" +
				" FROM (SELECT DISTINCT A.TRADE_NO, A.MR_NO, A.AR_AMT, A.PAY_TYPE01, A.PAY_TYPE02," +
				" B.PACKAGE_CODE,B.PACKAGE_DESC,TO_CHAR (A.BILL_DATE, 'YYYY.MM.DD') BILL_DATE" +
				" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B" +
				" WHERE A.TRADE_NO = B.TRADE_NO AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"'," +
				" 'YYYYMMDDHH24MISS') AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" +
				" AND B.PACKAGE_CODE IN (SELECT PACKAGE_CODE FROM MEM_PACKAGE" +
				" WHERE PARENT_PACKAGE_CODE IS NOT NULL AND PACKAGE_PRICE IS NOT NULL" +
				" AND    ACTIVE_FLG = 'Y' )) A" +
				" GROUP BY A.MR_NO, A.PACKAGE_CODE,A.PACKAGE_DESC,A.BILL_DATE) K," +
				"(  SELECT DISTINCT A.MR_NO, C.PAT_NAME, B.PACKAGE_CODE, B.PACKAGE_DESC," +
				" TO_CHAR (A.BILL_DATE, 'YYYY.MM.DD') BILL_DATE  FROM MEM_PACKAGE_TRADE_M A," +
				" MEM_PAT_PACKAGE_SECTION B, SYS_PATINFO C WHERE A.TRADE_NO = B.TRADE_NO" +
				" AND A.MR_NO = C.MR_NO  AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"'," +
				" 'YYYYMMDDHH24MISS') AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND B.PACKAGE_CODE IN (SELECT PACKAGE_CODE FROM MEM_PACKAGE  WHERE  " +
				" PARENT_PACKAGE_CODE IS NOT NULL AND PACKAGE_PRICE IS NOT NULL" +
				//" AND (   PARENT_PACKAGE_CODE = '0202' OR PACKAGE_CODE LIKE '04%'))" +
				" AND ACTIVE_FLG = 'Y' ) "+//活动套餐标记add by huangjw 20160608
				" ORDER BY B.PACKAGE_CODE) J WHERE K.MR_NO = J.MR_NO AND K.PACKAGE_CODE = J.PACKAGE_CODE" +
				" AND K.PACKAGE_DESC = J.PACKAGE_DESC AND K.BILL_DATE = J.BILL_DATE";
		sql = sql + where1 +where2 +" ORDER BY K.PACKAGE_CODE";
		//System.out.println("sql:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			packTable.removeRowAll();
			return;
		}
		String packSql = "";
		List<String> userDate; //使用时间
		List<String> packageDesc = new ArrayList<String>(); //套餐名称
		for (int i = 0; i < parm.getCount(); i++) {
			String mrNo1 = parm.getValue("MR_NO", i);
			String packageCode1 = parm.getValue("PACKAGE_CODE", i);
			String packageDesc1 = parm.getValue("PACKAGE_DESC", i);
			String billDage = parm.getValue("BILL_DATE", i);
			if(! packageDesc.contains(packageDesc1)){
				packageDesc.add(packageDesc1);
			}
			
			packSql="SELECT B.TRADE_NO,B.CASE_NO,B.MR_NO,B.PACKAGE_CODE," +
					" B.PACKAGE_DESC, B.USED_FLG, B.REST_TRADE_NO" +
					" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B" +
					" WHERE A.TRADE_NO = B.TRADE_NO" +
					" AND A.MR_NO = '"+mrNo1+"'" +
					" AND B.PACKAGE_CODE = '"+packageCode1+"'" +
					" AND TO_CHAR (A.BILL_DATE, 'YYYY.MM.DD') = '"+billDage+"'";
			TParm packParm = new TParm(TJDODBTool.getInstance().select(packSql));
			userDate = new ArrayList<String>();//使用日期
			String re = ""; //是否退费
			for (int j = 0; j < packParm.getCount(); j++) {
				if("1".equals(packParm.getValue("USED_FLG", j))){
					String dateSql="SELECT DISTINCT TO_CHAR (ORDER_DATE, 'YYYY.MM.DD') ORDER_DATE " +
							" FROM OPD_ORDER WHERE  MEM_PACKAGE_ID IN " +
							" (SELECT ID FROM MEM_PAT_PACKAGE_SECTION_D " +
							" WHERE TRADE_NO='"+packParm.getValue("TRADE_NO", j)+"'  AND USED_FLG='1')" +
							" ORDER BY ORDER_DATE DESC";
					TParm dateParm = new TParm(TJDODBTool.getInstance().select(dateSql));
//					System.out.println(j+"=====dateParm==="+dateParm);
					if(!userDate.contains(dateParm.getValue("ORDER_DATE", 0))){
						userDate.add(dateParm.getValue("ORDER_DATE", 0));
					}
					if(dateParm.getCount() > 1){
						if(!userDate.contains(dateParm.getValue("ORDER_DATE", 1))){
							userDate.add(dateParm.getValue("ORDER_DATE", 1));
						}
					}
				}else{
					if(packParm.getValue("REST_TRADE_NO", j).length() > 0){
						re="未使用已退";
					}
				}
			}
			
			if(userDate.size() > 0){
				Collections.sort(userDate);
				parm.setData("USER1_DATE", i, userDate.get(0));
				if(userDate.size() > 1){
					parm.setData("USER2_DATE", i, userDate.get(1));
				}
				
				
			}else{
				parm.setData("USER1_DATE", i, re);
			}
			
		}
		tableParm = new TParm();
		String[] name = parm.getNames();
		for (int j = 0; j < packageDesc.size(); j++) {
			int countSum =0; //份数
			double arAmtSum=0;//总金额
			double payType01Sum = 0;//现金
			double payType02Sum = 0;//刷卡
			TParm packParm = new TParm();
			for (int k = 0; k < parm.getCount(); k++) {
				
				if(packageDesc.get(j).equals(parm.getValue("PACKAGE_DESC", k))){
					countSum += parm.getInt("COUNT", k);
					arAmtSum += parm.getDouble("AR_AMT", k);
					payType01Sum += parm.getDouble("PAY_TYPE01", k);
					payType02Sum += parm.getDouble("PAY_TYPE02", k);
					packParm.addRowData(parm, k);
				}
				
			}
			for (int k = 0; k < name.length; k++) {
				tableParm.addData(name[k], "");
			}
			int row = tableParm.getCount("PAT_NAME")-1;
			//PAT_NAME;MR_NO;COUNT;AR_AMT;PAY_TYPE01;PAY_TYPE02;BILL_DATE;USER1_DATE;USER2_DATE;PACKAGE_DESC
			tableParm.setData("PAT_NAME", row, packageDesc.get(j));
			tableParm.setData("COUNT", row, countSum);
			tableParm.setData("AR_AMT", row, arAmtSum);
			tableParm.setData("PAY_TYPE01", row, payType01Sum);
			tableParm.setData("PAY_TYPE02", row, payType02Sum);
			for (int k = 0; k < packParm.getCount("PAT_NAME"); k++) {
				tableParm.addRowData(packParm, k);
			}
			
		}
		DecimalFormat df = new DecimalFormat("##########0.00");
		for (int j = 0; j < tableParm.getCount("MR_NO"); j++) {
			tableParm.setData("AR_AMT", j, df.format(tableParm.getDouble("AR_AMT", j)));
			tableParm.setData("PAY_TYPE01", j, df.format(tableParm.getDouble("PAY_TYPE01", j)));
			tableParm.setData("PAY_TYPE02", j, df.format(tableParm.getDouble("PAY_TYPE02", j)));
		}
		
		packTable.setParmValue(tableParm);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: MEMPackageActivityDetialControl.class ，method ：onQuery");
				e.printStackTrace();

			}
		}
		
	}
	/**
	 * 初始化界面
	 */
	public void initPage() {
		Timestamp now = StringTool.getTimestamp(new Date());
   	 	this.setValue("S_DATE",
   	 		StringTool.rollDate(now, -30).toString().substring(0, 10).replace('-', '/')+" "+"00:00:00");
   	 	this.setValue("E_DATE", 
		 		now.toString().substring(0, 10).replace('-', '/')+" "+"23:59:59");
	}
	
	public void onMrNo(){
		this.setValue("MR_NO", PatTool.getInstance().getMergeMrno(getValueString("MR_NO")));
		this.onQuery();
	}
	
	public void onClear(){
		initPage();
   	 	this.clearValue("MR_NO;PACKAGE_CODE");
   	 	packTable.removeRowAll();
 	
	}
	
	public void onPrint(){
		int rowCount = tableParm.getCount("MR_NO");
		if (rowCount < 1) {
			this.messageBox("先查询数据!");
			return;
		}
		TParm T1 = new TParm(); // 表格数据

		for (int i = 0; i < rowCount; i++) {
			T1.addRowData(tableParm, i);
		}
		T1.setCount(rowCount);
		String[] chage = packTable.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}

		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd  HH:mm:ss");
		String eDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd HH:mm:ss");
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", Operator.getHospitalCHNShortName()+ "活动套餐使用明细表");

		parm.setData("S_DATE", "TEXT", sDate);
		parm.setData("E_DATE", "TEXT", eDate);
		parm.setData("OPT_USER", "TEXT", Operator.getName());
		parm.setData("OPT_DATE", "TEXT", sysDate);
		parm.setData("T1", T1.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\MEM\\MEMPackageActivityDetial.jhw", parm);

	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "活动套餐使用明细报表");
	}

}
