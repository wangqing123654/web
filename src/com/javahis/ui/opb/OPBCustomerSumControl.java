package com.javahis.ui.opb;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class OPBCustomerSumControl extends TControl{
	TTable table;
	String sDate = "";
	String eDate = "";
	Date date1;
	Date date2;
	String admType = "";
	boolean isDebug = true;
	@Override
	public void onInit() {
		String date = StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyy/MM/dd");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", date);
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		try {
			admType = this.getValueString("ADM_TYPE");
			sDate = this.getValue("START_DATE").toString().substring(0,19).replaceAll("-", "/");
			eDate = this.getValue("END_DATE").toString().substring(0,19).replaceAll("-", "/");
			
			TParm parm = getData();
			TParm bilResult = parm.getParm("bilResult");
			TParm memResult = parm.getParm("memResult");
			TParm inResult = parm.getParm("inResult");
			TParm outResult = parm.getParm("outResult");
			TParm result = new TParm();
			//以bilResult为准，将memResult、inResult、outResult 放到bilResult里面去
			int count = 0;
			//TIME;COUNT1;AR_AMT1;COUNT2;AR_AMT2;COUNT3;AR_AMT3;COUNT4;AR_AMT4;OPB_TOT;IBS_TOT;TOT
			int sumCount1 = 0;
			int sumCount2 = 0;
			int sumCount3 = 0;
			int sumCount4 = 0;
			double sumArAmt1 = 0.00;
			double sumArAmt2 = 0.00;
			double sumArAmt3 = 0.00;
			double sumArAmt4 = 0.00;
			double sumOpbTot = 0.00;
			double sumIbsTot = 0.00;
			double sumTot = 0.00;
			if(admType.equals("O")){
				for(int i = 0; i < bilResult.getCount("TIME"); i++){
					result.setData("TIME", i, bilResult.getValue("TIME",i));
					result.setData("COUNT1", i, bilResult.getInt("COUNT1",i));
					result.setData("AR_AMT1", i, bilResult.getDouble("AR_AMT1",i));
					result.setData("COUNT2", i, memResult.getInt("COUNT2",i));
					result.setData("AR_AMT2", i, memResult.getDouble("AR_AMT2",i));
					result.setData("COUNT3", i, 0);
					result.setData("AR_AMT3", i,0.00);
					result.setData("COUNT4", i, 0);
					result.setData("AR_AMT4", i, 0.00);
					double opbAmt = 0.00;
					double ibsAmt = 0.00;
					opbAmt += bilResult.getDouble("AR_AMT1",i) + memResult.getDouble("AR_AMT2",i);
					ibsAmt += inResult.getDouble("AR_AMT3",i) + (outResult.getDouble("AR_AMT4",i));
					result.setData("OPB_TOT",i,opbAmt);
					result.setData("IBS_TOT",i,ibsAmt);
					result.setData("TOT",i,opbAmt + ibsAmt);
					
					//总合计
					sumCount1 += bilResult.getInt("COUNT1",i);
					sumCount2 += memResult.getInt("COUNT2",i);
					sumArAmt1 += bilResult.getDouble("AR_AMT1",i);
					sumArAmt2 += memResult.getDouble("AR_AMT2",i);
					sumOpbTot += opbAmt;
					sumIbsTot += ibsAmt;
					sumTot += (opbAmt + ibsAmt);
					count++;
				}
			}else if(admType.equals("I")){
				for(int i = 0; i < inResult.getCount("TIME"); i++){
					result.setData("TIME", i, inResult.getValue("TIME",i));
					result.setData("COUNT1", i, 0);
					result.setData("AR_AMT1", i, 0.00);
					result.setData("COUNT2", i, 0);
					result.setData("AR_AMT2", i, 0.00);
					result.setData("COUNT3", i, inResult.getInt("COUNT3",i));
					result.setData("AR_AMT3", i, inResult.getDouble("AR_AMT3",i));
					result.setData("COUNT4", i, outResult.getInt("COUNT4",i));
					result.setData("AR_AMT4", i, (outResult.getDouble("AR_AMT4",i)));
					double opbAmt = 0.00;
					double ibsAmt = 0.00;
					opbAmt += bilResult.getDouble("AR_AMT1",i) + memResult.getDouble("AR_AMT2",i);
					ibsAmt += inResult.getDouble("AR_AMT3",i) + (outResult.getDouble("AR_AMT4",i));
					result.setData("OPB_TOT",i,opbAmt);
					result.setData("IBS_TOT",i,ibsAmt);
					result.setData("TOT",i,opbAmt + ibsAmt);
					
					//总合计
					sumCount3 += inResult.getInt("COUNT3",i);
					sumCount4 += outResult.getInt("COUNT4",i);
					sumArAmt3 += inResult.getDouble("AR_AMT3",i);
					sumArAmt4 += outResult.getDouble("AR_AMT4",i);
					sumOpbTot += opbAmt;
					sumIbsTot += ibsAmt;
					sumTot += (opbAmt + ibsAmt);
					count++;
				}
			}else{
				for(int i = 0; i < bilResult.getCount("TIME"); i++){
					result.setData("TIME", i, bilResult.getValue("TIME",i));
					result.setData("COUNT1", i, bilResult.getInt("COUNT1",i));
					result.setData("AR_AMT1", i, bilResult.getDouble("AR_AMT1",i));
					result.setData("COUNT2", i, memResult.getInt("COUNT2",i));
					result.setData("AR_AMT2", i, memResult.getDouble("AR_AMT2",i));
					result.setData("COUNT3", i, inResult.getInt("COUNT3",i));
					result.setData("AR_AMT3", i, inResult.getDouble("AR_AMT3",i));
					result.setData("COUNT4", i, outResult.getInt("COUNT4",i));
					result.setData("AR_AMT4", i, outResult.getDouble("AR_AMT4",i));
					double opbAmt = 0.00;
					double ibsAmt = 0.00;
					opbAmt += bilResult.getDouble("AR_AMT1",i) + memResult.getDouble("AR_AMT2",i);
					ibsAmt += inResult.getDouble("AR_AMT3",i) + outResult.getDouble("AR_AMT4",i);
					result.setData("OPB_TOT",i,opbAmt);
					result.setData("IBS_TOT",i,ibsAmt);
					result.setData("TOT",i,opbAmt + ibsAmt);
					//总合计
					sumCount1 += bilResult.getInt("COUNT1",i);
					sumCount2 += memResult.getInt("COUNT2",i);
					sumCount3 += inResult.getInt("COUNT3",i);
					sumCount4 += outResult.getInt("COUNT4",i);
					sumArAmt1 += bilResult.getDouble("AR_AMT1",i);
					sumArAmt2 += memResult.getDouble("AR_AMT2",i);
					sumArAmt3 += inResult.getDouble("AR_AMT3",i);
					sumArAmt4 += outResult.getDouble("AR_AMT4",i);
					sumOpbTot += opbAmt;
					sumIbsTot += ibsAmt;
					sumTot += (opbAmt + ibsAmt);
					count++;
				}
			}
			//TIME;COUNT1;AR_AMT1;COUNT2;AR_AMT2;COUNT3;AR_AMT3;COUNT4;AR_AMT4;OPB_TOT;IBS_TOT;TOT
			result.setData("TIME", count, "");
			result.setData("COUNT1", count, "总计："+sumCount1);
			result.setData("AR_AMT1", count, sumArAmt1);
			result.setData("COUNT2", count, sumCount2);
			result.setData("AR_AMT2", count, sumArAmt2);
			result.setData("COUNT3", count, sumCount3);
			result.setData("AR_AMT3", count, sumArAmt3);
			result.setData("COUNT4", count, sumCount4);
			result.setData("AR_AMT4", count, sumArAmt4);
			result.setData("OPB_TOT",count,sumOpbTot);
			result.setData("IBS_TOT",count,sumIbsTot);
			result.setData("TOT",count,sumTot);
			result.setCount(count);
			//printParm = result;
			table.setParmValue(result);
			
		} catch (Exception e) {
			if(isDebug){
				System.out.println("come in class: OPBCustomerSumControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取 bil_opb_recp表数据
	 */
	public TParm getData(){
		TParm result = new TParm();
		TParm bilResult =  new TParm();
		TParm memResult =  new TParm();
		TParm inResult =  new TParm();
		TParm outResult =  new TParm();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		try {
			date1 = sdf.parse(sDate);
			//System.out.println(sdf.format(date1));
			date2 = sdf.parse(eDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int count = 0;
		while(date1.before(date2) || date1.equals(date2)){
			String bilOpbSql = "SELECT '"+sdf.format(date1).toString()+"' TIME,COUNT(*) COUNT1,SUM(AR_AMT) AR_AMT1 FROM " +
					"(SELECT MR_NO,SUM(AR_AMT) AR_AMT FROM BIL_OPB_RECP WHERE ADM_TYPE <> 'H' " +
					" AND BILL_DATE BETWEEN TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss') " +
					" AND TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss') AND MEM_PACK_FLG = 'N'  GROUP BY CASE_NO,RECEIPT_NO,MR_NO)";
			String memPackageSql = "SELECT '"+sdf.format(date1).toString()+"' TIME, COUNT(*) COUNT2,SUM(AR_AMT) AR_AMT2 FROM " +
					" (SELECT MR_NO,SUM(AR_AMT) AR_AMT FROM MEM_PACKAGE_TRADE_M " +
					" WHERE BILL_DATE BETWEEN TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss') " +
					" AND TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss')   GROUP BY TRADE_NO,MR_NO)";
			/*String Sql = "SELECT '"
					+ sdf.format(date1).toString()
					+ "' TIME, COUNT(*) COUNT3,SUM(AR_AMT) AR_AMT3 FROM "
					+ " (" +
					" SELECT A.CASE_NO,SUM(B.PRE_AMT) AR_AMT FROM ADM_INP A, BIL_PAY B "
					+ " WHERE A.CASE_NO = B.CASE_NO(+) AND A.IN_DATE BETWEEN TO_DATE('"
					+ sdf.format(date1).toString().substring(0, 10).replaceAll(
							"-", "/")
					+ " 00:00:00"
					+ "','yyyy/MM/dd HH24:mi:ss') "
					+ " AND TO_DATE('"
					+ sdf.format(date1).toString().substring(0, 10).replaceAll(
							"-", "/") + " 23:59:59"
					+ "','yyyy/MM/dd HH24:mi:ss') "
					+ " AND A.BILL_STATUS <> '4'  GROUP BY A.CASE_NO" 
					+ " UNION "
					+ " SELECT A.CASE_NO,SUM(B.PAY_BILPAY) AR_AMT FROM ADM_INP A, BIL_IBS_RECPM B "
					+ " WHERE A.CASE_NO = B.CASE_NO AND A.IN_DATE BETWEEN TO_DATE('"
					+ sdf.format(date1).toString().substring(0, 10).replaceAll(
							"-", "/")
					+ " 00:00:00"
					+ "','yyyy/MM/dd HH24:mi:ss') "
					+ " AND TO_DATE('"
					+ sdf.format(date1).toString().substring(0, 10).replaceAll(
							"-", "/") + " 23:59:59"
					+ "','yyyy/MM/dd HH24:mi:ss') "
					+ " AND A.BILL_STATUS = '4'  GROUP BY A.CASE_NO"
					+" )";*/
			String inSql = 
				" WITH AA "
				+ " AS (SELECT COUNT (*) COUNT3,    '"
				+ sdf.format(date1).toString()
				+ "' TIME FROM ADM_INP A "
				+ " WHERE A.IN_DATE BETWEEN TO_DATE ('"+sdf.format(date1).toString()+" 00:00:00"+"', "
				+ " 'yyyy/MM/dd HH24:mi:ss') "
                + " AND TO_DATE ('"+sdf.format(date1).toString()+" 23:59:59"+"', "
                + " 'yyyy/MM/dd HH24:mi:ss')), "
                + " BB "
                + " AS ( SELECT AR_AMT AR_AMT3, '"+sdf.format(date1).toString()+"' TIME FROM ( SELECT SUM (A.PRE_AMT) AR_AMT "
                + " FROM BIL_PAY A "
                + " WHERE     A.CHARGE_DATE BETWEEN TO_DATE ('"+sdf.format(date1).toString()+" 00:00:00"+"', "
                + " 'yyyy/MM/dd HH24:mi:ss') "
                + " AND TO_DATE ('"+sdf.format(date1).toString()+" 23:59:59"+"', "
                + " 'yyyy/MM/dd HH24:mi:ss') "
                + " AND TRANSACT_TYPE IN ('01', '02', '04') AND A.PAY_TYPE <> 'TCJZ' "
                + " )) "
                + " SELECT AA.*,BB.AR_AMT3 FROM AA,BB WHERE AA.TIME = BB.TIME(+) ";
			/*String outSql1 = "SELECT '"+sdf.format(date1).toString()+"' TIME, COUNT(*) COUNT4,SUM(AR_AMT) AR_AMT4,SUM(REDUCE_AMT) REDUCE_AMT FROM " +
					" (SELECT A.CASE_NO,(SUM(AR_AMT)-SUM(PAY_BILPAY)) AR_AMT,SUM(REDUCE_AMT) REDUCE_AMT  FROM ADM_INP A,BIL_IBS_RECPM B " +
					" WHERE A.CASE_NO = B.CASE_NO AND  A.BILL_DATE BETWEEN TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss') " +
					" AND TO_DATE('"+sdf.format(date1).toString().substring(0,10).replaceAll("-", "/")+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss') " +
					"  AND A.BILL_STATUS = '4'  GROUP BY A.CASE_NO)";*/
			/*String outSql = "SELECT COUNT (*) COUNT4, '"+sdf.format(date1).toString()+"' TIME," +
					" (SUM (AR_AMT) - SUM (PAY_BILPAY)) AR_AMT4,SUM (REDUCE_AMT) REDUCE_AMT " +
					" FROM BIL_IBS_RECPM " +
					" WHERE CHARGE_DATE BETWEEN " +
					" TO_DATE ('"+sdf.format(date1).toString()+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss')" +
					" AND TO_DATE('"+sdf.format(date1).toString()+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss')";*/
			String outSql = " SELECT SUM (COUNT) COUNT4, '"+sdf.format(date1).toString()+"' TIME,SUM(AR_AMT4) AR_AMT4 FROM (SELECT " +
			" COUNT (*) COUNT,"+
			" SUM (CASE WHEN PAY_CASH IS NULL THEN 0 ELSE PAY_CASH END)+" +
			" SUM (CASE WHEN PAY_CHECK IS NULL THEN 0 ELSE PAY_CHECK END)+" +
			" SUM (CASE WHEN PAY_BANK_CARD IS NULL THEN 0 ELSE PAY_BANK_CARD END)+" +
			" SUM (CASE WHEN PAY_GIFT_CARD IS NULL THEN 0 ELSE PAY_GIFT_CARD END)+" +
			" SUM (CASE WHEN PAY_DISCNT_CARD IS NULL THEN 0 ELSE PAY_DISCNT_CARD END)+" +
			" SUM (CASE WHEN PAY_DEBIT IS NULL THEN 0 ELSE PAY_DEBIT END)+" +
			" SUM (CASE WHEN PAY_BXZF IS NULL THEN 0 ELSE PAY_BXZF END) AR_AMT4" +
			" FROM BIL_IBS_RECPM " +
			" WHERE CHARGE_DATE BETWEEN " +
			" TO_DATE ('"+sdf.format(date1).toString()+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss')" +
			" AND TO_DATE('"+sdf.format(date1).toString()+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss')" +
			" " +
			" UNION ALL " +
			" SELECT " +
			" COUNT (*) COUNT,"+
			" SUM (CASE WHEN PAY_CASH IS NULL THEN 0 ELSE PAY_CASH END)+" +
			" SUM (CASE WHEN PAY_CHECK IS NULL THEN 0 ELSE PAY_CHECK END)+" +
			" SUM (CASE WHEN PAY_BANK_CARD IS NULL THEN 0 ELSE PAY_BANK_CARD END)+" +
			" SUM (CASE WHEN PAY_GIFT_CARD IS NULL THEN 0 ELSE PAY_GIFT_CARD END)+" +
			" SUM (CASE WHEN PAY_DISCNT_CARD IS NULL THEN 0 ELSE PAY_DISCNT_CARD END)+" +
			" SUM (CASE WHEN PAY_DEBIT IS NULL THEN 0 ELSE PAY_DEBIT END)+" +
			" SUM (CASE WHEN PAY_BXZF IS NULL THEN 0 ELSE PAY_BXZF END) AR_AMT4" +
			" FROM BIL_IBS_RECPM " +
			" WHERE REFUND_DATE BETWEEN " +
			" TO_DATE ('"+sdf.format(date1).toString()+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss')" +
			" AND TO_DATE('"+sdf.format(date1).toString()+" 23:59:59"+"','yyyy/MM/dd HH24:mi:ss')" +
			" AND RESET_RECEIPT_NO IS NOT NULL" +
			" AND CHARGE_DATE < TO_DATE('"+sdf.format(date1).toString()+" 00:00:00"+"','yyyy/MM/dd HH24:mi:ss'))";
			/*System.out.println("bilOpbSql:::"+bilOpbSql);
			System.out.println("memPackageSql:::"+memPackageSql);
			System.out.println("inSql:::"+inSql);*/
			//System.out.println("outSql:::"+outSql);
			//System.out.println("ss:::"+sql);
			if(!admType.equals("") && admType.equals("O")){
				TParm  bilOpbParm = new TParm(TJDODBTool.getInstance().select(bilOpbSql));
				TParm  memPackageParm = new TParm(TJDODBTool.getInstance().select(memPackageSql));
				bilResult.setRowData(count,bilOpbParm.getRow(0));
				memResult.setRowData(count,memPackageParm.getRow(0));
			}else if(admType.equals("I")){
				TParm  inParm = new TParm(TJDODBTool.getInstance().select(inSql));
				TParm  outParm = new TParm(TJDODBTool.getInstance().select(outSql));
				inResult.setRowData(count,inParm.getRow(0));
				outResult.setRowData(count,outParm.getRow(0));
			}else{
				TParm  bilOpbParm = new TParm(TJDODBTool.getInstance().select(bilOpbSql));
				TParm  memPackageParm = new TParm(TJDODBTool.getInstance().select(memPackageSql));
				TParm  inParm = new TParm(TJDODBTool.getInstance().select(inSql));
				TParm  outParm = new TParm(TJDODBTool.getInstance().select(outSql));
				bilResult.setRowData(count,bilOpbParm.getRow(0));
				memResult.setRowData(count,memPackageParm.getRow(0));
				inResult.setRowData(count,inParm.getRow(0));
				outResult.setRowData(count,outParm.getRow(0));
			}
			count++;
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date1);
			calendar.add(calendar.DATE, 1);// 把日期往后增加一天.整数往后推,负数往前移动
			date1 = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		}
		result.setData("bilResult",bilResult.getData());
		result.setData("memResult",memResult.getData());
		result.setData("inResult",inResult.getData());
		result.setData("outResult",outResult.getData());
		return result;
	}
	
	
	/**
	 * 打印
	 */
	public void onPrint(){
		DecimalFormat df = new DecimalFormat("########0.00");
		TParm  data = new TParm();
		TParm printParm = table.getShowParmValue();
		if(printParm.getCount() <= 0){
			this.messageBox("没有打印数据");
		}
		for(int i = 0; i < printParm.getCount(); i++){
			printParm.setData("AR_AMT1", i, df.format(printParm.getDouble("AR_AMT1", i)));
			printParm.setData("AR_AMT2", i, df.format(printParm.getDouble("AR_AMT2", i)));
			printParm.setData("AR_AMT3", i, df.format(printParm.getDouble("AR_AMT3", i)));
			printParm.setData("AR_AMT4", i, df.format(printParm.getDouble("AR_AMT4", i)));
			printParm.setData("OPB_TOT", i, df.format(printParm.getDouble("OPB_TOT", i)));
			printParm.setData("IBS_TOT", i, df.format(printParm.getDouble("IBS_TOT", i)));
			printParm.setData("TOT", i, df.format(printParm.getDouble("TOT", i)));
		}
		printParm.addData("SYSTEM", "COLUMNS", "TIME");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT1");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT1");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT2");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT2");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT3");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT3");
		printParm.addData("SYSTEM", "COLUMNS", "COUNT4");
		printParm.addData("SYSTEM", "COLUMNS", "AR_AMT4");
		printParm.addData("SYSTEM", "COLUMNS", "OPB_TOT");
		printParm.addData("SYSTEM", "COLUMNS", "IBS_TOT");
		printParm.addData("SYSTEM", "COLUMNS", "TOT");
		data.setData("TABLE",printParm.getData());
		data.setData("time", "TEXT","打印日期："+TJDODBTool.getInstance().getDBTime().toString().substring(0,19).replaceAll("-", "/"));
		data.setData("printUser", "TEXT","打印人员："+getUserName(Operator.getID()));
		data.setData("date", "TEXT","查询起讫："+sDate.substring(0,10)+"~"+eDate.substring(0,10));
		this
		.openPrintWindow(
				"%ROOT%\\config\\prt\\opb\\OPBCustomerSum.jhw",
				data);
	}
	
	/**
	 * 获取名称
	 * @param userId
	 * @return
	 */
	public String getUserName(String userId){
		TParm parm =new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+userId+"' "));
		return parm.getValue("USER_NAME",0);
	}
	
	/**
	 * 汇出
	 */
	public void onExport(){
		if(table.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "客户明细表");
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		this.onInit();
		this.clearValue("ADM_TYPE");
		String date = StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyy/MM/dd");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", date);
		 sDate = "";
		 eDate = "";
		 date1 = null;
		 date2 = null;
		 admType = "";
		table.removeRowAll();
	}
}
