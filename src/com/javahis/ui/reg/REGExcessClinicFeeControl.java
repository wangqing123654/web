package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>
 * Title: 医务部超额诊疗统计表
 * </p>
 * 
 * <p>
 * Description: 医务部超额诊疗统计表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author huangtt 2016.06.21
 * @version 1.0
 */
public class REGExcessClinicFeeControl extends TControl {
	TTable table;
	TParm printParm;
	
	public void onInit(){
		setStartEndDate();
		table = (TTable) this.getComponent("TABLE");
		printParm = new TParm();
		
	}
	
	public void onQuery(){
		DecimalFormat df1 = new DecimalFormat("####.00");
		
		String startTime = getValueString("S_DATE");
		String endTime = getValueString("E_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		
		String whereSql = "";
		String whereSql1 = "";
		if(this.getValueString("DR_CODE").length() > 0){
			whereSql += " AND B.DR_CODE='"+getValueString("DR_CODE")+"'";
		}
		if(this.getValueString("DEPT_CODE").length() > 0){
			whereSql += " AND B.DEPT_CODE='"+getValueString("DEPT_CODE")+"'";
		}
		if(this.getValueString("CLINICTYPE_CODE").length() > 0){
			whereSql += " AND T.CLINICTYPE_CODE='"+getValueString("CLINICTYPE_CODE")+"'";
			whereSql1 += " AND T.CLINICTYPE_CODE='"+getValueString("CLINICTYPE_CODE")+"'";
		}
		
		
		String sql = "SELECT * FROM (SELECT DE.DEPT_CHN_DESC DEPT_DESC,O.USER_NAME DR_DESC," +
				" B.DEPT_CODE,B.DR_CODE," +
				" TO_CHAR(T.ADM_DATE,'YYYY/MM/DD') ADM_DATE , COUNT(T.REG_DATE) REG_COUNT," +
				" DECODE(SIGN((COUNT(T.REG_DATE) - 10)), -1, 0,(COUNT(T.REG_DATE) - 10)) EXCESS_COUNT" +
				" FROM REG_PATADM T, SYS_OPERATOR O, SYS_DEPT DE," +
				" REG_SESSION S, REG_CLINICTYPE C, SYS_CTZ CT," +
				" (SELECT * FROM OPD_ORDER P WHERE P.REXP_CODE = '102' AND P.BILL_FLG = 'Y') B" +
				" WHERE T.CASE_NO = B.CASE_NO" +
				" AND T.SESSION_CODE = S.SESSION_CODE" +
				" AND B.DR_CODE = O.USER_ID(+)" +
				" AND B.DEPT_CODE = DE.DEPT_CODE(+)" +
				whereSql+
				" AND T.CLINICTYPE_CODE IN ('01', '04', '05', '06', '07', '09','10','11', '12', '16', '17', '19','20')" +
				" AND T.CTZ1_CODE<>'23'" +
				" AND T.REGCAN_USER IS NULL" +
				" AND T.SEEN_DR_TIME BETWEEN TO_DATE('"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE('"+endTime+"', 'YYYYMMDDHH24MISS')" +
				" AND C.CLINICTYPE_CODE = T.CLINICTYPE_CODE" +
				" AND T.CTZ1_CODE = CT.CTZ_CODE(+)" +
				" GROUP BY DE.DEPT_CHN_DESC, O.USER_NAME, T.ADM_DATE,B.DEPT_CODE,B.DR_CODE) WHERE EXCESS_COUNT>0" +
				" ORDER BY DEPT_DESC, DR_DESC, ADM_DATE";
		
//		System.out.println(sql);
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println(parm);
//		System.out.println(parm.getCount());
		if(parm.getCount() < 0){			
			table.removeRowAll();
			printParm = new TParm();
			this.messageBox("没有要查询的数据");
			return;
		}
		
		TParm tableParm = new TParm();
		
		for (int i = 0; i < parm.getCount(); i++) {
			String admDate =parm.getValue("ADM_DATE", i).replaceAll("/", "");
			String deptCode = parm.getValue("DEPT_CODE", i);
			String drCode = parm.getValue("DR_CODE", i);
			TParm detailParm = getDetail(admDate, deptCode, drCode, startTime, endTime,whereSql1);
			double sumArAmt =0;
			double sumExcessAmt =0;
			if(detailParm.getCount() > 10){
				for (int j = 10; j < detailParm.getCount(); j++) {
					tableParm.addRowData(detailParm, j);
					sumArAmt += detailParm.getDouble("AR_AMT", j);
					sumExcessAmt += detailParm.getDouble("EXCESS_AMT", j);
				}
			}
			
			printParm.addRowData(parm, i);
			printParm.addData("SUM_AR_AMT", df1.format(sumArAmt));
			printParm.addData("SUM_EXCESS_AMT", df1.format(sumExcessAmt));

		}
//		System.out.println("tableParm=="+tableParm);
//		System.out.println("printParm=="+printParm);
		table.setParmValue(tableParm);
		
		
		
		
		
	}
	
	public TParm getDetail(String admDate,String deptCode,String drCode,String sDate,String eDate,String whereSql1){
		String sql = "SELECT  T.MR_NO, P.PAT_NAME, CT.CTZ_DESC, C.CLINICTYPE_DESC," +
				" DE.DEPT_CHN_DESC DEPT_DESC, O.USER_NAME DR_DESC, RDE.DEPT_CHN_DESC REALDEPT_DESC," +
				" RO.USER_NAME REALDR_DESC, S.SESSION_DESC," +
				" TO_CHAR(T.SEEN_DR_TIME,'YYYY/MM/DD HH24:MI:SS') SEEN_DR_TIME," +
				" TO_CHAR(B.BILL_DATE,'YYYY/MM/DD HH24:MI:SS') BILL_DATE," +
				" B.AR_AMT, B.AR_AMT * 0.4 EXCESS_AMT" +
				" FROM REG_PATADM T, SYS_OPERATOR O, SYS_DEPT DE, REG_SESSION S," +
				" REG_CLINICTYPE C, SYS_CTZ CT, SYS_PATINFO P, SYS_OPERATOR RO, SYS_DEPT RDE," +
				"(SELECT * FROM OPD_ORDER P WHERE P.REXP_CODE = '102' AND P.BILL_FLG = 'Y') B" +
				" WHERE T.CASE_NO = B.CASE_NO AND T.SESSION_CODE = S.SESSION_CODE" +
				" AND T.REALDR_CODE = O.USER_ID(+) AND T.REALDEPT_CODE = DE.DEPT_CODE(+)" +
				" AND T.CLINICTYPE_CODE IN" +
				" ('01', '04', '05', '06', '07', '09','10','11', '12', '16', '17', '19','20')" +
				" AND T.CTZ1_CODE<>'23' AND T.REGCAN_USER IS NULL" +
				" AND T.SEEN_DR_TIME BETWEEN TO_DATE('"+sDate+"', 'YYYYMMDDHH24MISS') AND" +
				" TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS')" +
				" AND C.CLINICTYPE_CODE = T.CLINICTYPE_CODE" +
				" AND T.CTZ1_CODE = CT.CTZ_CODE(+)" +
				" AND T.MR_NO=P.MR_NO" +
				" AND B.DR_CODE = RO.USER_ID(+)" +
				" AND B.DEPT_CODE = RDE.DEPT_CODE(+)" +
				" AND B.DR_CODE='" +drCode+"'"+
				" AND B.DEPT_CODE='" +deptCode+"'" +whereSql1+
				" AND T.ADM_DATE = TO_DATE('"+admDate+"','YYYYMMDD')"+
				" ORDER BY DE.DEPT_CHN_DESC , O.USER_NAME , T.SEEN_DR_TIME";
		
//		System.out.println("sqlD---"+sql);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
		
		
	}
	
	public void onPrint(){
		
		if(printParm.getCount("DEPT_DESC") < 0){
			this.messageBox("没有要打印的数据");
    		return;
			
		}
		
		// 打印数据
		TParm date = new TParm();
		String startDate = getValueString("S_DATE").replaceAll("-", "/").substring(0, 19);
		String endDate = getValueString("E_DATE").replaceAll("-", "/").substring(0, 19);
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		
		date.setData("TITLE", "TEXT", "门急诊超额诊看诊诊疗费分配表");
		date.setData("PRINT_USER", "TEXT", "制表人："+Operator.getID());
		date.setData("PRINT_DATE", "TEXT", "打印日期："+sysDate);
		date.setData("DATE","TEXT","统计时间:"+startDate+"--"+endDate);
		TParm parm = new TParm();
    	for (int i = 0; i < printParm.getCount("DEPT_DESC"); i++) {
    		parm.addData("DEPT_DESC", printParm.getValue("DEPT_DESC", i));
    		parm.addData("DR_DESC", printParm.getValue("DR_DESC", i));
    		parm.addData("ADM_DATE", printParm.getValue("ADM_DATE", i));
    		parm.addData("REG_COUNT", printParm.getValue("REG_COUNT", i));
    		parm.addData("EXCESS_COUNT", printParm.getValue("EXCESS_COUNT", i));
    		parm.addData("SUM_AR_AMT", printParm.getValue("SUM_AR_AMT", i));
			parm.addData("SUM_EXCESS_AMT", printParm.getValue("SUM_EXCESS_AMT", i));
		
		}
    	parm.setCount(parm.getCount("DEPT_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		parm.addData("SYSTEM", "COLUMNS", "DR_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		parm.addData("SYSTEM", "COLUMNS", "REG_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "EXCESS_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "SUM_AR_AMT");
		parm.addData("SYSTEM", "COLUMNS", "SUM_EXCESS_AMT");
		date.setData("TABLE", parm.getData());
		System.out.println(date);
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\REG\\REGExcessClinicFeePrint.jhw", date);
		
		
		
	}
	
	public void onExport(){
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊超额诊疗患者明细表");
	}
	
	public void onClear(){
		table.removeRowAll();
		this.clearValue("DR_CODE;DEPT_CODE;S_DATE;E_DATE");
		printParm = new TParm();
		setStartEndDate();
		
		
	}
	
	/**
    *
    * 设置起始时间和结束时间，上月26-本月25
    */
   private void setStartEndDate(){
   	Timestamp date = TJDODBTool.getInstance().getDBTime();
       // 结束时间(本月的25)
       Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
                                                    substring(0, 4) + "/" +
                                                    TypeTool.getString(date).
                                                    substring(5, 7) +
                                                    "/25 23:59:59",
                                                    "yyyy/MM/dd HH:mm:ss");
       setValue("E_DATE", dateTime);
       // 起始时间(上个月26)
       Calendar cd = Calendar.getInstance();
       cd.setTimeInMillis(date.getTime());
       cd.add(Calendar.MONTH, -1);
       Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());
       setValue("S_DATE",
       		endDateTimestamp.toString().substring(0, 4) +
                "/" +
                endDateTimestamp.toString().substring(5, 7) +
                "/26 00:00:00");
   }
	
	

}
