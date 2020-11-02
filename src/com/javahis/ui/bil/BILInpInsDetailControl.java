package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title:住院患者医保明细表</p>
 *
 * <p>Description:住院患者医保明细表 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author lim
 * @version 1.0
 */
public class BILInpInsDetailControl extends TControl {
	
	/**
	 * 初始化
	 */
	public void onInit(){
		Timestamp date = SystemTool.getInstance().getDate() ;
		String transDate = StringTool.getString(date, "yyyy/MM/dd") ;
		this.setValue("START_DATE", transDate) ;
		this.setValue("END_DATE", transDate) ;
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		String startDate = this.getValueString("START_DATE") ;
		String endDate = this.getValueString("END_DATE") ;
		
		String sql = 
			" SELECT   B.IPD_NO, A.PAT_NAME, A.DEPT_DESC," +
			" (  A.NHI_COMMENT" +
			" + A.NHI_PAY" +
			" + A.OWN_AMT" +
			" + A.ADD_AMT" +
			" + A.RESTART_STANDARD_AMT" +
			" + A.STARTPAY_OWN_AMT" +
			" + A.PERCOPAYMENT_RATE_AMT" +
			" + A.INS_HIGHLIMIT_AMT" +
			" ) AS SUM," +
			" (  CASE" +
			" WHEN ARMYAI_AMT IS NULL" +
			" THEN 0" +
			" ELSE ARMYAI_AMT" +
			" END" +
			" + CASE" +
			" WHEN TOT_PUBMANADD_AMT IS NULL" +
			" THEN 0" +
			" ELSE TOT_PUBMANADD_AMT" +
			" END" +
			" + A.NHI_PAY" +
			" ) AS COORDINATION_PAY," +
			" A.NHI_COMMENT AS HELP_PAY," +
			" (  A.OWN_AMT" +
			" + A.ADD_AMT" +
			" + A.RESTART_STANDARD_AMT" +
			" + A.STARTPAY_OWN_AMT" +
			" + A.PERCOPAYMENT_RATE_AMT" +
			" + A.INS_HIGHLIMIT_AMT" +
			" - CASE" +
			" WHEN A.ACCOUNT_PAY_AMT IS NULL" +
			" THEN 0" +
			" ELSE A.ACCOUNT_PAY_AMT" +
			" END" +
			" - CASE" +
			" WHEN ARMYAI_AMT IS NULL" +
			" THEN 0" +
			" ELSE ARMYAI_AMT" +
			" END" +
			" - CASE" +
			" WHEN TOT_PUBMANADD_AMT IS NULL" +
			" THEN 0" +
			" ELSE TOT_PUBMANADD_AMT" +
			" END" +
			" ) AS OWN_PAY," +
			" (CASE" +
			" WHEN A.ACCOUNT_PAY_AMT IS NULL" +
			" THEN 0" +
			" ELSE A.ACCOUNT_PAY_AMT" +
			" END" +
			" ) AS OWN_ACCOUNT_PAY," +
			" B.MR_NO, B.BILL_DATE CHARGE_DATE, A.STATION_DESC" +
			" FROM INS_IBS A, ADM_INP B" +
			" WHERE A.CASE_NO = B.CASE_NO" +
//			" AND A.CONFIRM_NO NOT LIKE 'KN%'" +
			" AND B.CASE_NO IN (" +
			" SELECT DISTINCT CASE_NO" +
			" FROM BIL_IBS_RECPM" +
			" WHERE (PAY_INS_CARD IS NOT NULL OR PAY_INS IS NOT NULL" +
			" )" +
			" AND RESET_RECEIPT_NO IS NULL" +
			" AND AR_AMT > 0";
		//===zhangp 20120806 end
		              //======modify-end========================================================
		StringBuilder sbuilder = new StringBuilder(sql) ;

		//=====modify-begin (by wanglong 20120615)===============================
//		if(startDate != null && !"".equals(startDate)){
//			String[] startDateArray = startDate.split(" ") ;
//			sbuilder.append(" AND TO_CHAR(A.IN_DATE,'yyyy-MM-dd')>='"+startDateArray[0]+"'") ;
//		}
//		if(endDate != null && !"".equals(endDate)){
//			String[] endDateArray = endDate.split(" ") ;
//			sbuilder.append(" AND TO_CHAR(A.IN_DATE,'yyyy-MM-dd')<='"+endDateArray[0]+"'") ;
//		}
		//AND B.CHARGE_DATE BETWEEN TO_DATE('20120511000000','YYYYMMDDHH24MISS') AND TO_DATE('20120513235959','YYYYMMDDHH24MISS')
		//======pangben 2013-4-17 修改时间查询格式
		if(startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate)){
            sbuilder.append(" AND CHARGE_DATE BETWEEN TO_DATE ('" + SystemTool.getInstance().getDateReplace(startDate, true)+ "'," +
			" 'YYYYMMDDHH24MISS'" +
			" )" +
			" AND TO_DATE ('" +SystemTool.getInstance().getDateReplace(endDate.substring(0,10), false) + "'," +
			" 'YYYYMMDDHH24MISS'");
		}
		//======modify-end========================================================
		sbuilder.append(" ))ORDER BY IPD_NO ") ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
		
		//=====modify-begin (by wanglong 20120615)===============================
		double sumPaySum=0;
		double coordinationPaySum=0;
		double helpPaySum=0;
		double ownPaySum=0;
		double ownAccountPaySum=0;
		for (int i = 0; i < result.getCount(); i++) {
			sumPaySum+=	 StringTool.round(result.getDouble("SUM", i), 2)    ;
			coordinationPaySum+=	 StringTool.round(result.getDouble("COORDINATION_PAY", i), 2);
			helpPaySum+=	 StringTool.round(result.getDouble("HELP_PAY", i), 2);
			ownPaySum+= StringTool.round(	result.getDouble("OWN_PAY", i), 2);
			ownAccountPaySum+=	 StringTool.round(result.getDouble("OWN_ACCOUNT_PAY", i), 2);
		}
		result.addData( "SUM"  ,    StringTool.round( sumPaySum, 2) );
		result.addData(  "COORDINATION_PAY" , StringTool.round( coordinationPaySum  , 2)  );
		result.addData(  "HELP_PAY" , StringTool.round( helpPaySum  , 2)  );
		result.addData(   "OWN_PAY", StringTool.round( ownPaySum  , 2)  );
		result.addData(  "OWN_ACCOUNT_PAY" , StringTool.round( ownAccountPaySum   , 2) );
		result.addData("IPD_NO", "") ;
		result.addData("PAT_NAME", "") ;
		result.addData("DEPT_DESC", "合计") ;
		result.addData("MR_NO","");	
		result.addData("CHARGE_DATE","");
		result.addData("STATION_DESC", "") ;	
		//======modify-end========================================================
    	//   获得错误信息消息
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}
        if (result.getCount() <= 0) {
            messageBox("查无数据");
            this.callFunction("UI|TTABLE|setParmValue", new TParm());
            return;
        }		
        this.callFunction("UI|TTABLE|setParmValue", result);
	}

	/**
	 * 清空
	 */
	public void onClear(){
		//this.clearValue("START_DATE;END_DATE") ;
		Timestamp date = SystemTool.getInstance().getDate() ;
		String transDate = StringTool.getString(date, "yyyy/MM/dd") ;
		this.setValue("START_DATE", transDate) ;
		this.setValue("END_DATE", transDate) ;
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TTable dataTable = (TTable)this.getComponent("TTABLE") ;
		TParm parm = dataTable.getShowParmValue() ;
		
		if(parm.getCount()<=0){
			messageBox("没有打印数据!") ;
			return ;
		}
		
		TParm tableParm = new TParm() ;
		DecimalFormat df = new DecimalFormat("##########0.00");
		//=====modify-begin (by wanglong 20120615)===============================
		for (int i = 0; i < parm.getCount(); i++) {
		
//		double sumPaySum=0;
//		double coordinationPaySum=0;
//		double helpPaySum=0;
//		double ownPaySum=0;
//		double ownAccountPaySum=0;
//		int i = 0;
//		for (; i < parm.getCount(); i++) {
	    //======modify-end========================================================
			tableParm.addData("IPD_NO", parm.getValue("IPD_NO", i)) ;
			tableParm.addData("PAT_NAME", parm.getValue("PAT_NAME", i)) ;
			tableParm.addData("DEPT_DESC", parm.getValue("DEPT_DESC", i)) ;
			tableParm.addData("SUM", df.format(parm.getDouble("SUM", i))) ;
			tableParm.addData("COORDINATION_PAY", df.format(parm.getDouble("COORDINATION_PAY", i))) ;
			tableParm.addData("HELP_PAY", df.format(parm.getDouble("HELP_PAY", i))) ;
			tableParm.addData("OWN_PAY", df.format(parm.getDouble("OWN_PAY", i))) ;
			tableParm.addData("OWN_ACCOUNT_PAY", df.format(parm.getDouble("OWN_ACCOUNT_PAY", i))) ;
			//=====modify-begin (by wanglong 20120615)===============================
			
//			 sumPaySum+=parm.getDouble("SUM", i);
//			 coordinationPaySum+=parm.getDouble("COORDINATION_PAY", i);
//			 helpPaySum+=parm.getDouble("HELP_PAY", i);
//			 ownPaySum+=parm.getDouble("OWN_PAY", i);
//			 ownAccountPaySum+=parm.getDouble("OWN_ACCOUNT_PAY", i);

			
			tableParm.addData("MR_NO",parm.getValue("MR_NO", i));	
			tableParm.addData("CHARGE_DATE",parm.getValue("CHARGE_DATE", i));
			tableParm.addData("STATION_DESC", parm.getValue("STATION_DESC", i)) ;
			//======modify-end========================================================
		}
		//=====modify-begin (by wanglong 20120615)===============================

//		tableParm.addData("SUM", df.format(sumPaySum)) ;
//		tableParm.addData("COORDINATION_PAY", df.format(coordinationPaySum)) ;
//		tableParm.addData("HELP_PAY", df.format(helpPaySum)) ;
//		tableParm.addData("OWN_PAY", df.format(ownPaySum)) ;
//		tableParm.addData("OWN_ACCOUNT_PAY", df.format(ownAccountPaySum)) ;
//		
//		tableParm.addData("IPD_NO", "") ;
//		tableParm.addData("PAT_NAME", "") ;
//		tableParm.addData("DEPT_DESC", "合计") ;
//		tableParm.addData("MR_NO","");	
//		tableParm.addData("CHARGE_DATE","");
//		tableParm.addData("STATION_DESC", "") ;	
		
		
		//======modify-end========================================================
		tableParm.setCount(tableParm.getCount("IPD_NO")) ;
		//=====modify-begin (by wanglong 20120615)===============================
		tableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");	
		tableParm.addData("SYSTEM", "COLUMNS", "SUM");
		tableParm.addData("SYSTEM", "COLUMNS", "COORDINATION_PAY");
		tableParm.addData("SYSTEM", "COLUMNS", "HELP_PAY");
		tableParm.addData("SYSTEM", "COLUMNS", "OWN_PAY");	
		tableParm.addData("SYSTEM", "COLUMNS", "OWN_ACCOUNT_PAY");	
		tableParm.addData("SYSTEM", "COLUMNS", "CHARGE_DATE");
		//======modify-end========================================================
		TParm data = new TParm() ;
		String startDate = this.getValueString("START_DATE") ;
		String endDate = this.getValueString("END_DATE") ;		
		data.setData("START_DATE","TEXT", startDate.replace("-", "/").substring(0,10)) ;
		data.setData("END_DATE","TEXT",endDate.replace("-", "/").substring(0,10)) ;
		data.setData("USER","TEXT",Operator.getName());//修改 添加收费员签章 20130722 caoyong
		data.setData("TABLE", tableParm.getData()) ;
		this.openPrintWindow("%ROOT%\\config\\prt\\bil\\BILInpInsDetail.jhw",data);
	}
}
