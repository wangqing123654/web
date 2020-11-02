package com.javahis.ui.mem;

import java.text.DecimalFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.ExportExcelUtil;

public class MEMPackageSummaryControl extends TControl{

	 public void onInit(){
	     super.onInit();
	     String now = StringTool.getString(SystemTool.getInstance().getDate(),
			"yyyyMMdd");
	    this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
			"yyyyMMddHHmmss"));// 开始时间
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
			"yyyyMMddHHmmss"));// 结束时间
		this.callFunction("UI|TABLE|setParmValue", new TParm()); 
		this.setValue("PACKAGE_CLASS", "");
	 }
	/*
	 *  查询方法
	 */
	 public void onQuery(){
		 String startDate=this.getValue("START_DATE").toString();
		 startDate = startDate.substring(0, startDate.lastIndexOf(".")).replace(":", "")
		 .replace("-", "").replace(" ", "");
		 String endDate=this.getValue("END_DATE").toString();
			endDate = endDate.substring(0, endDate.lastIndexOf(".")).replace(":", "")
			.replace("-", "").replace(" ", "");
			String datesql="SELECT TO_CHAR(TO_DATE('"+startDate+"', 'YYYYMMDDHH24MISS') + ROWNUM-1,'YYYY-MM-DD') TIME " +
					"FROM DUAL CONNECT BY ROWNUM <= (TO_DATE('"+endDate+"', 'YYYYMMDDHH24MISS') -" +
					" TO_DATE('"+startDate+"', 'YYYYMMDDHH24MISS'))+1";
			TParm dateParm= new TParm(TJDODBTool.getInstance().select(datesql));
			String jysql="";
			//前日结余
			for(int i=0;i<dateParm.getCount();i++){
				 /* jysql+="SELECT '"+dateParm.getValue("TIME",i)+"' BILL_DATE,A.* FROM (SELECT A.PACKAGE_CLASS,SUM(A.TOT_AMT+(CASE WHEN B.TOT_AMT1 IS NULL THEN 0 ELSE B.TOT_AMT1 END)) AR_AMT " +
			 		" FROM (SELECT SUM(A.AR_AMT) AS TOT_AMT,P.PACKAGE_CLASS from MEM_PAT_PACKAGE_SECTION A,MEM_PACKAGE_TRADE_M B ,MEM_PACKAGE P" +
			 		" WHERE B.TRADE_NO=A.TRADE_NO  AND A.PACKAGE_CODE=P.PACKAGE_CODE AND A.USED_FLG ='0' AND B.BILL_DATE<TO_DATE('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000','YYYYMMDDHH24MISS') " +
			 		" GROUP BY PACKAGE_CLASS)A,(SELECT -SUM(a.AR_AMT) TOT_AMT1,P.PACKAGE_CLASS FROM MEM_PACKAGE_TRADE_M M,MEM_PAT_PACKAGE_SECTION A,MEM_PACKAGE P " +
			 		" WHERE  M.AR_AMT<0 AND M.BILL_DATE<TO_DATE('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000','YYYYMMDDHH24MISS')" +
			 		" AND A.PACKAGE_CODE=P.PACKAGE_CODE AND M.TRADE_NO=A.REST_TRADE_NO  GROUP BY P.PACKAGE_CLASS) B," +
			 		"  (  SELECT P.PACKAGE_CLASS, SUM (A.AR_AMT) AR_AMT FROM OPD_ORDER A,  MEM_PAT_PACKAGE_SECTION_D D," +
			 		"  MEM_PACKAGE P WHERE     D.PACKAGE_CODE = P.PACKAGE_CODE AND A.MEM_PACKAGE_ID = D.ID" +
			 		"  AND A.MEM_PACKAGE_ID IS NOT NULL  AND A.BILL_DATE < TO_DATE ('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000', 'YYYYMMDDhh24miss')  GROUP BY P.PACKAGE_CLASS) C" +
			 		" WHERE A.PACKAGE_CLASS=B.PACKAGE_CLASS(+)  AND A.PACKAGE_CLASS = C.PACKAGE_CLASS GROUP BY A.PACKAGE_CLASS) A WHERE A.AR_AMT<>0 UNION ALL ";*/
				jysql+="SELECT '"+dateParm.getValue("TIME",i)+"' BILL_DATE,(B.AR_AMT-C.AR_AMT-D.AR_AMT) AR_AMT,B.PACKAGE_CLASS FROM (" +
						" SELECT  B.PACKAGE_CLASS, SUM (A.AR_AMT) AR_AMT FROM (SELECT DISTINCT A.TRADE_NO, B.PACKAGE_CODE,A.AR_AMT" +
						" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B WHERE A.TRADE_NO = B.TRADE_NO AND A.BILL_DATE < TO_DATE " +
						" ('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000','YYYYMMDDHH24MISS') AND A.AR_AMT >= 0) A, MEM_PACKAGE B WHERE A.PACKAGE_CODE = B.PACKAGE_CODE" +
						" GROUP BY B.PACKAGE_CLASS ORDER BY PACKAGE_CLASS) B," +
						" (SELECT  B.PACKAGE_CLASS, -SUM (AR_AMT) AR_AMT FROM (SELECT DISTINCT A.TRADE_NO,B.PACKAGE_CODE,A.AR_AMT " +
						" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B WHERE  A.TRADE_NO = B.REST_TRADE_NO " +
						" AND A.BILL_DATE < TO_DATE ('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000','YYYYMMDDHH24MISS') AND A.AR_AMT < 0) A, MEM_PACKAGE B" +
						" WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY PACKAGE_CLASS ORDER BY PACKAGE_CLASS) C," +
						" (SELECT P.PACKAGE_CLASS,SUM (A.AR_AMT) AR_AMT FROM OPD_ORDER A, BIL_OPB_RECP C,MEM_PAT_PACKAGE_SECTION_D D," +
						" MEM_PACKAGE_SECTION E,MEM_PACKAGE P WHERE A.RECEIPT_NO = C.RECEIPT_NO AND E.PACKAGE_CODE = P.PACKAGE_CODE" +
						" AND A.MEM_PACKAGE_ID = D.ID AND D.PACKAGE_CODE = E.PACKAGE_CODE AND D.SECTION_CODE = E.SECTION_CODE " +
						" AND A.MEM_PACKAGE_ID IS NOT NULL AND (A.SETMAIN_FLG = 'N' OR A.ORDERSET_CODE IS NULL) AND C.BILL_DATE < TO_DATE " +
						"('"+dateParm.getValue("TIME",i).substring(0, dateParm.getValue("TIME",i).length()).replace("-", "")+"000000','YYYYMMDDhh24miss') GROUP BY P.PACKAGE_CLASS ORDER BY  P.PACKAGE_CLASS)  D" +
						" WHERE B.PACKAGE_CLASS=C.PACKAGE_CLASS AND B.PACKAGE_CLASS=D.PACKAGE_CLASS UNION ALL ";
			}
			jysql=jysql.substring(0,jysql.length()-11);
		 
		
		 
		 //套餐销售
		 String xssql=" SELECT  A.BILL_DATE,B.PACKAGE_CLASS,SUM(A.AR_AMT) AR_AMT " +
		 		"FROM (SELECT DISTINCT A.TRADE_NO,B.PACKAGE_CODE,TO_CHAR(A.BILL_DATE,'yyyy-MM-DD') BILL_DATE," +
		 		" A.AR_AMT FROM MEM_PACKAGE_TRADE_M A,MEM_PAT_PACKAGE_SECTION B WHERE A.TRADE_NO = B.TRADE_NO" +
		 		" AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"','YYYYMMDDHH24MISS')" +
		 		" AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS') AND A.AR_AMT >= 0) A,MEM_PACKAGE B" +
		 		" WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY A.BILL_DATE,B.PACKAGE_CLASS ORDER BY PACKAGE_CLASS";

		 //套餐退费
		 String tfsql="SELECT A.BILL_DATE,B.PACKAGE_CLASS,-SUM(AR_AMT) AR_AMT FROM (SELECT DISTINCT A.TRADE_NO," +
		 		" B.PACKAGE_CODE, TO_CHAR(A.BILL_DATE,'yyyy-MM-DD') BILL_DATE, A.AR_AMT " +
		 		" FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B WHERE A.TRADE_NO = B.REST_TRADE_NO" +
		 		" AND A.BILL_DATE BETWEEN TO_DATE ('"+startDate+"','YYYYMMDDHH24MISS') AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS')" +
		 		" AND A.AR_AMT < 0) A,MEM_PACKAGE B WHERE A.PACKAGE_CODE = B.PACKAGE_CODE GROUP BY BILL_DATE,PACKAGE_CLASS " +
		 		" ORDER BY BILL_DATE,PACKAGE_CLASS";

		 //套餐结转
		 String jzsql="SELECT TO_CHAR(C.BILL_DATE,'yyyy-MM-DD') BILL_DATE,P.PACKAGE_CLASS,SUM(A.AR_AMT) AR_AMT " +
		 		" FROM OPD_ORDER A, BIL_OPB_RECP C,MEM_PAT_PACKAGE_SECTION_D D, MEM_PACKAGE_SECTION E,MEM_PACKAGE P " +
		 		" WHERE A.RECEIPT_NO = C.RECEIPT_NO AND E.PACKAGE_CODE = P.PACKAGE_CODE AND A.MEM_PACKAGE_ID = D.ID" +
		 		" AND D.PACKAGE_CODE = E.PACKAGE_CODE AND D.SECTION_CODE = E.SECTION_CODE AND A.MEM_PACKAGE_ID IS NOT NULL " +
		 		" AND (A.SETMAIN_FLG = 'N' OR A.ORDERSET_CODE IS NULL) AND C.BILL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDhh24miss')" +
		 		" AND TO_DATE ('"+endDate+"', 'YYYYMMDDhh24miss') GROUP BY TO_CHAR(C.BILL_DATE,'yyyy-MM-DD'),P.PACKAGE_CLASS " +
		 		" ORDER BY BILL_DATE,P.PACKAGE_CLASS";
		
		 String where ="";
		 if(this.getValueString("PACKAGE_CLASS").length()>0){
			 where=" AND A.PACKAGE_CLASS='"+this.getValue("PACKAGE_CLASS")+"' ";
		 }
		 String sqlddd="SELECT DISTINCT A.BILL_DATE,E.CHN_DESC,A.AR_AMT,NVL(B.AR_AMT,0) XS_AMT,NVL(C.AR_AMT,0) TF_AMT,NVL(D.AR_AMT,0) JZ_AMT," +
		 		"(A.AR_AMT+NVL(B.AR_AMT,0)-NVL(C.AR_AMT,0)-NVL(D.AR_AMT,0)) YE_AMT FROM " +
		 		"("+jysql+") A,("+xssql+") B,("+tfsql+") C,("+jzsql+") D,SYS_DICTIONARY E " +
		 		" WHERE A.BILL_DATE=B.BILL_DATE(+)" +
		 		"	AND A.PACKAGE_CLASS=B.PACKAGE_CLASS(+) " +
		 		 " AND A.BILL_DATE=C.BILL_DATE(+)" +
		 		"	AND A.PACKAGE_CLASS=C.PACKAGE_CLASS(+)" +
		 		" AND A.BILL_DATE=D.BILL_DATE(+)" +
		 		"	AND A.PACKAGE_CLASS=D.PACKAGE_CLASS(+) " +
		 		" AND A.PACKAGE_CLASS=E.ID AND E.GROUP_ID = 'MEM_PACKAGE_CLASS' " +
		 		where+
		 		" ORDER BY A.BILL_DATE,E.CHN_DESC ";
		 //System.out.println("---"+sqlddd);
		 TParm result= new TParm(TJDODBTool.getInstance().select(sqlddd));
		 double xsTotAmt=0.00;
		 double tfTotAmt=0.00;
		 double jzTotAmt=0.00;
		 DecimalFormat df= new DecimalFormat("######0.00");   
		 for(int i=0;i<result.getCount();i++){
			 xsTotAmt+=result.getDouble("XS_AMT",i);
			 tfTotAmt+=result.getDouble("TF_AMT",i);
			 jzTotAmt+=result.getDouble("JZ_AMT",i);
			 result.setData("AR_AMT",i,df.format(result.getDouble("AR_AMT",i)));
			 result.setData("XS_AMT",i,df.format(result.getDouble("XS_AMT",i)));
			 result.setData("TF_AMT",i,df.format(result.getDouble("TF_AMT",i)));
			 result.setData("JZ_AMT",i,df.format(result.getDouble("JZ_AMT",i)));
			 result.setData("YE_AMT",i,df.format(result.getDouble("YE_AMT",i)));
			 
		 }
		 result.addData("BILL_DATE", "合计:");
		 result.addData("CHN_DESC", "");
		 result.addData("AR_AMT", "");
		 result.addData("XS_AMT", df.format(xsTotAmt));
		 result.addData("TF_AMT", df.format(tfTotAmt));
		 result.addData("JZ_AMT", df.format(jzTotAmt));
		 result.addData("YE_AMT", "");
		 callFunction("UI|TABLE|setParmValue", result);
	 }
	 //清空
	 public void onClear(){
		 onInit();
	 }
	 //打印
	 public void onPrint(){
		 TTable table=(TTable) this.getComponent("TABLE");
		 TParm tableParm = table.getParmValue();
		 TParm printParm=new TParm();
		 if(tableParm.getCount()<=0){
			 this.messageBox("无可打印数据");
			 return;
		 }
		 tableParm.setCount(tableParm.getCount()+1);
		 printParm.setData("TABLE",tableParm.getData());
		 printParm.setData("DATE", "TEXT", "日期："
					+ this.getValue("START_DATE").toString().substring(0, 19)
					.replaceAll("-", "/")
			+ " ~ "
			+ this.getValue("END_DATE").toString().substring(0, 19)
					.replaceAll("-", "/"));
		 openPrintWindow("%ROOT%\\config\\prt\\MEM\\MEMPackageSummary.jhw",printParm);
	 }
// 汇出
	    /**
	     * 导出Excel表格
	     */
	    public void onExport() {
	        TTable table = (TTable) callFunction("UI|Table|getThis");
	        if (table.getRowCount() <= 0) {
	            messageBox("无导出资料");
	            return;
	        }
	        ExportExcelUtil.getInstance().exportExcel(table, "预收套餐汇总表");
	    }
}
