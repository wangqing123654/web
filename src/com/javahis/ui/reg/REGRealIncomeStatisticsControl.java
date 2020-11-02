package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: 门急诊实收入统计数据</p>
*
* <p>Description: 门急诊实收入统计数据</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151211
* @version 4.0
*/
public class REGRealIncomeStatisticsControl extends TControl {
	
	TTable table;
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", yesterday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	}
	
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		try {
			
		
		String startTime = getValueString("S_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		String endTime = getValueString("E_DATE");
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		String sql = "SELECT CASE WHEN A.BILL_DATE IS NULL THEN B.BILL_DATE ELSE A.BILL_DATE END AS BILL_DATE," +
				" CASE WHEN A.DEPT_CHN_DESC IS NULL THEN B.DEPT_CHN_DESC ELSE A.DEPT_CHN_DESC END AS DEPT_CHN_DESC," +
				" B.S_COUNT,B.R_COUNT,A.AR_AMT," +
				" A.MEM_PACKAGE_AMT,"+//套餐结转金额 add by huangjw 20160609
				" A.CHARGE01, A.CHARGE02, A.CHARGE03, A.CHARGE04, A.CHARGE05," +
				" A.CHARGE06, A.CHARGE07, A.CHARGE08, A.CHARGE09, A.CHARGE10, A.CHARGE11, A.CHARGE12, A.CHARGE13," +
				" A.CHARGE14, A.CHARGE15, A.CHARGE16, A.CHARGE17, A.CHARGE18, A.CHARGE19, A.CHARGE20, A.REDUCE_AMT" +
				" FROM (  SELECT BILL_DATE, DEPT_CHN_DESC," +
				//" SUM (AR_AMT) AR_AMT, " +
				" (SUM (AR_AMT) - SUM (MEM_PACKAGE_AMT)) AR_AMT, SUM (MEM_PACKAGE_AMT) MEM_PACKAGE_AMT, "+//实收金额不包括套餐结转金额 add by huangjw 20160609
				" SUM (CHARGE01) CHARGE01, SUM (CHARGE02) CHARGE02," +
				" SUM (CHARGE03) CHARGE03, SUM (CHARGE04) CHARGE04, SUM (CHARGE05) CHARGE05, SUM (CHARGE06) CHARGE06," +
				" SUM (CHARGE07) CHARGE07, SUM (CHARGE08) CHARGE08, SUM (CHARGE09) CHARGE09, SUM (CHARGE10) CHARGE10," +
				" SUM (CHARGE11) CHARGE11, SUM (CHARGE12) CHARGE12, SUM (CHARGE13) CHARGE13, SUM (CHARGE14) CHARGE14," +
				" SUM (CHARGE15) CHARGE15, SUM (CHARGE16) CHARGE16, SUM (CHARGE17) CHARGE17, SUM (CHARGE18) CHARGE18," +
				" SUM (CHARGE19) CHARGE19, SUM (CHARGE20) CHARGE20, SUM (REDUCE_AMT) REDUCE_AMT" +
				" FROM (SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM') BILL_DATE, C.DEPT_CHN_DESC," +
//				"(  A.CHARGE01" +
//				" + A.CHARGE02 + A.CHARGE03 + A.CHARGE04 + A.CHARGE05 + A.CHARGE06 + A.CHARGE07 + A.CHARGE08" +
//				" + A.CHARGE09 + A.CHARGE10 + A.CHARGE11 + A.CHARGE12 + A.CHARGE13 + A.CHARGE14 + A.CHARGE15" +
//				" + A.CHARGE16 + A.CHARGE17 + A.CHARGE18 + A.CHARGE19 + A.CHARGE20 + A.CHARGE21 + A.CHARGE22" +
//				" + A.CHARGE23 + A.CHARGE24 + A.CHARGE25 + A.CHARGE26 + A.CHARGE27 + A.CHARGE28 + A.CHARGE29" +
//				" + A.CHARGE30" +
				" (A.TOT_AMT - A.REDUCE_AMT) AR_AMT," +
				" CASE A.MEM_PACK_FLG WHEN 'Y' THEN A.TOT_AMT ELSE 0 END MEM_PACKAGE_AMT,"+//套餐结转列　add by huangjw 20160609
				" A.CHARGE01, A.CHARGE02, A.CHARGE03, A.CHARGE04, A.CHARGE05," +
				" A.CHARGE06, A.CHARGE07, A.CHARGE08, A.CHARGE09, A.CHARGE10, A.CHARGE11, A.CHARGE12, A.CHARGE13," +
				" A.CHARGE14, A.CHARGE15, A.CHARGE16, A.CHARGE17, A.CHARGE18, A.CHARGE19, A.CHARGE20, A.CHARGE21," +
				" A.CHARGE22, A.CHARGE23, A.CHARGE24, A.CHARGE25, A.CHARGE26, A.CHARGE27, A.CHARGE28, A.CHARGE29," +
				" A.CHARGE30, A.REDUCE_AMT FROM BIL_OPB_RECP A, REG_PATADM B, SYS_DEPT C WHERE A.CASE_NO = B.CASE_NO" +
				" AND B.REALDEPT_CODE = C.DEPT_CODE AND B.REGCAN_USER IS NULL" +
				" AND A.BILL_DATE BETWEEN TO_DATE ('"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS'))" +
				" GROUP BY BILL_DATE, DEPT_CHN_DESC) A" +
				" FULL OUTER JOIN" +
				" (SELECT CASE WHEN A.BILL_DATE IS NULL THEN B.BILL_DATE ELSE A.BILL_DATE END AS BILL_DATE," +
				" CASE WHEN A.DEPT_CHN_DESC IS NULL THEN B.DEPT_CHN_DESC ELSE A.DEPT_CHN_DESC END AS DEPT_CHN_DESC," +
				"  A.S_COUNT,  B.R_COUNT  FROM (  SELECT BILL_DATE, DEPT_CHN_DESC, COUNT (CASE_NO) S_COUNT" +
				" FROM (SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM') BILL_DATE, C.DEPT_CHN_DESC, A.CASE_NO" +
				" FROM BIL_OPB_RECP A, REG_PATADM B, SYS_DEPT C WHERE A.CASE_NO = B.CASE_NO" +
				" AND B.REALDEPT_CODE = C.DEPT_CODE AND B.REGCAN_USER IS NULL" +
				" AND A.RESET_RECEIPT_NO IS NULL AND A.TOT_AMT >= 0" +
				" AND A.BILL_DATE BETWEEN TO_DATE ( '"+startTime+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"', 'YYYYMMDDHH24MISS'))" +
				"  GROUP BY BILL_DATE, DEPT_CHN_DESC) A" +
				" FULL OUTER JOIN" +
				" (  SELECT BILL_DATE, DEPT_CHN_DESC, COUNT (CASE_NO) R_COUNT" +
				" FROM (SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM') BILL_DATE, C.DEPT_CHN_DESC,A.CASE_NO" +
				" FROM BIL_OPB_RECP A, REG_PATADM B, SYS_DEPT C WHERE A.CASE_NO = B.CASE_NO" +
				" AND B.REALDEPT_CODE = C.DEPT_CODE AND B.REGCAN_USER IS NULL" +
				" AND A.RESET_RECEIPT_NO IS NULL AND A.TOT_AMT < 0" +
				" AND A.BILL_DATE BETWEEN TO_DATE ( '"+startTime+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS'))" +
				" GROUP BY BILL_DATE, DEPT_CHN_DESC) B  ON A.BILL_DATE = B.BILL_DATE" +
				"  AND A.DEPT_CHN_DESC = B.DEPT_CHN_DESC) B" +
				" ON A.BILL_DATE = B.BILL_DATE AND A.DEPT_CHN_DESC = B.DEPT_CHN_DESC" +
				" ORDER BY A.BILL_DATE, A.DEPT_CHN_DESC DESC";
		//System.out.println("sql::::::::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()< 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		DecimalFormat df = new DecimalFormat("##########0.00");
		String name[] = table.getParmMap().split(";");
		double sumA[] = new double[name.length];
		int countS = 0;
		int countR = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			
			countS += parm.getInt(name[2], i);
			countR += parm.getInt(name[3], i);
			for (int j = 4; j < name.length; j++) {
				parm.setData(name[j], i, df.format(parm.getDouble(name[j], i)));
				sumA[j] += parm.getDouble(name[j], i);
				
			}
		}
		
		parm.addData(name[0], "");
		parm.addData(name[1], "总计");
		parm.addData(name[2], countS);
		parm.addData(name[3], countR);
		for (int i = 4; i < name.length; i++) {
			parm.addData(name[i], df.format( sumA[i]));
		}
		
		table.setParmValue(parm);
		
		} catch (Exception e) {
			// TODO: handle exception
			if(isDedug){  
				System.out.println(" come in class: REGRealIncomeStatisticsControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
		//
		//				
	}
	
	public void onClear(){
		table.removeRowAll();
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", yesterday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	}
	
	public void onExport(){
		table.acceptText();
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊实收入统计数据");
	}
	
}
