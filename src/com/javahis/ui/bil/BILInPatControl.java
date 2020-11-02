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
import com.dongyang.util.TypeTool;
import com.javahis.ui.sys.SYSOpdComOrderControl;
import com.javahis.util.ExportExcelUtil;

public class BILInPatControl extends TControl {
	TParm parm;
	String DATE;
	double sum = 0;
	
	//zhanglei 20170110 修改住院预交金报表昨日余
	double yesterday;  //昨日

	public void onInit() {
		super.onInit();
		this.initPage();
	}

	/**
	 * 初始化
	 */
	private void initPage() {
		// table = (TTable) this.getComponent("TABLE");
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("DATE_S", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// 开始时间
		this.setValue("DATE_E", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// 结束时间

	}

	/**
	 * 查询
	 */
	public void OnQuery() {
		//===zhangp 20120618 start
		String sql = 
				" SELECT   A.IPD_NO, C.MR_NO, E.STATION_DESC, B.PAT_NAME, C.DEPT_CODE," +
				" D.DEPT_ABS_DESC, SUM (A.PRE_AMT) AS PRE_AMT" +
				" FROM BIL_PAY A, SYS_PATINFO B, ADM_INP C, SYS_DEPT D, SYS_STATION E" +
				" WHERE A.CASE_NO = C.CASE_NO" +		
				" AND C.STATION_CODE = E.STATION_CODE" +
				" AND A.MR_NO = C.MR_NO" +
				" AND B.MR_NO = C.MR_NO" +
				" AND C.DEPT_CODE = D.DEPT_CODE" +
				//===ZHANGP 20120625 START
//				" AND C.DS_DATE IS NULL" +
				" AND (C.BILL_STATUS <> 4 OR C.BILL_STATUS IS NULL)" +
				" and a.pre_amt<>0 " +
				//===ZHANGP 20120625 END
				" GROUP BY A.IPD_NO," +
				" B.PAT_NAME," +
				" C.DEPT_CODE," +
				" D.DEPT_ABS_DESC," +
				" C.MR_NO," +
				" E.STATION_DESC" +
				" ORDER BY C.DEPT_CODE";
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
			return;
		}
		// table赋值IPD_NO;PAT_NAME;DEPT_CODE;TOTAL_BILPAY
		// this.callFunction("UI|TABLE|setParmValue", parm);
		double money1 = 0.00;
		TParm printData = new TParm();
		String deptcode = parm.getData("DEPT_ABS_DESC", 0).toString();
		DecimalFormat df = new DecimalFormat("########0.00");
		for (int i = 0; i < parm.getCount("IPD_NO"); i++) {
		//zhanglei 20170111只显示余额的用户
		  if(parm.getDouble("PRE_AMT", i)!=0){
			if (parm.getData("DEPT_ABS_DESC", i).equals(deptcode)) {
				printData.addData("IPD_NO", parm.getValue("IPD_NO", i));
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				printData.addData("DEPT_ABS_DESC", parm.getValue(
						"DEPT_ABS_DESC", i));
				printData.addData("PRE_AMT", df.format(StringTool.round(parm.getDouble("PRE_AMT", i),2)));
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				printData.addData("STATION_CODE", parm.getValue("STATION_DESC", i));
				money1 += parm.getDouble("PRE_AMT", i);
//				if (i == parm.getCount("IPD_NO") - 1) {
//					printData.addData("IPD_NO", "");
//					printData.addData("PAT_NAME", "");
//					printData.addData("DEPT_ABS_DESC", "预交金小计");
//					printData.addData("PRE_AMT", df.format(money1));
//					printData.addData("MR_NO", "");
//					printData.addData("STATION_CODE", "");
//					sum += money1;
//				}
			} else {
				if(money1>0){
				printData.addData("IPD_NO", "");
				printData.addData("PAT_NAME", "");
				printData.addData("DEPT_ABS_DESC", "预交金小计");
				printData.addData("PRE_AMT", df.format(money1));
				printData.addData("MR_NO", "");
				printData.addData("STATION_CODE", "");
				sum += money1;
				money1 = 0;
				printData.addData("IPD_NO", parm.getValue("IPD_NO", i));
				printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
				printData.addData("DEPT_ABS_DESC", parm.getValue(
						"DEPT_ABS_DESC", i));
				printData.addData("PRE_AMT", df.format(StringTool.round(parm.getDouble("PRE_AMT", i),2)));
				printData.addData("MR_NO", parm.getValue("MR_NO", i));
				printData.addData("STATION_CODE", parm.getValue("STATION_DESC", i));
				money1 += parm.getDouble("PRE_AMT", i);
			}
			}
			deptcode = parm.getData("DEPT_ABS_DESC", i).toString();
		//zhanglei 20170111只显示余额的用户
		  }
		  
		  
		}
		printData.addData("IPD_NO", "");
		printData.addData("PAT_NAME", "");
		printData.addData("DEPT_ABS_DESC", "预交金小计");
		printData.addData("PRE_AMT", df.format(money1));
		printData.addData("MR_NO", "");
		printData.addData("STATION_CODE", "");
		sum += money1;
		
		//zhanglei 20170111
		yesterday=sum;
		
		printData.addData("IPD_NO", "");
		printData.addData("PAT_NAME", "");
		printData.addData("DEPT_ABS_DESC", "总计");
		printData.addData("PRE_AMT", df.format(sum));
		printData.addData("MR_NO", "");
		printData.addData("STATION_CODE", "");
		//====zhangp 20120618 end
		printData.setCount(printData.getCount("IPD_NO"));
		this.callFunction("UI|TABLE|setParmValue", printData);
		DATE = StringTool.getString((Timestamp) this.getValue("DATE_S"),
				"yyyy/MM/dd ")
				+ " 至 "
				+ StringTool.getString((Timestamp) this.getValue("DATE_E"),
						"yyyy/MM/dd ");
		
		

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("DEPT_ABS_DESC");
		TTable table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		//====zhangp 20120618 start
		parm = new TParm();
		DATE = "";
	    sum = 0;
		//====zhangp 20120618 end
	}

	/**
	 * 打印
	 */
	public void OnPrint() {
		
		
		DecimalFormat df = new DecimalFormat("########0.00");
		TTable table = (TTable) getComponent("TABLE");
		TParm tableParm = table.getParmValue();

		
		//zhanglei 20170110 修改住院预交金报表


		//zhanglei 20170110 修改住院预交金报表获得今日预交金得到日期
		
        String start_date = this.getValueString("DATE_E").substring(0, 19);
        start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
                     start_date.substring(8, 10) + "000000";
        String end_date = this.getValueString("DATE_E").substring(0, 19);
        end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
                   end_date.substring(8, 10) + end_date.substring(11, 13) +
                   end_date.substring(14, 16) + end_date.substring(17, 19);

		
      //zhanglei 20170110 修改住院预交金报表获得今日预交金获得今日预交金总额
        String sqlJR = "SELECT sum(A.PRE_AMT) AS PRE_AMT " +
        " FROM BIL_PAY A " +
        " WHERE A.CHARGE_DATE BETWEEN TO_DATE(" + start_date +
        ", 'YYYYMMDDHH24MISS') AND TO_DATE(" + end_date +
        " ,'YYYYMMDDHH24MISS') AND A.TRANSACT_TYPE <>'03'" + 
        " ORDER BY RECEIPT_NO";
        TParm parmJR = new TParm(TJDODBTool.getInstance().select(sqlJR));
        double today = parmJR.getDouble("PRE_AMT",0);
        
        
        //zhanglei 20170110 修改住院预交金报表获得今日预交金获得今日预交金冲销 今日出
        String start_dateJRC = start_date.substring(0, 8);
        String sqlJRC = " SELECT SUM(B.PRE_AMT) AS AMT "
        + " FROM BIL_PAY B, SYS_PATINFO S, SYS_OPERATOR O, ADM_INP I, "
        + " SYS_BED D, SYS_STATION T, SYS_DICTIONARY Y, SYS_DEPT P "
        + " WHERE TO_CHAR (B.CHARGE_DATE, 'YYYYMMDD') = '"
        + start_dateJRC + "' "
        + " AND B.TRANSACT_TYPE = '03' "
        + " AND B.MR_NO = S.MR_NO "
        + " AND B.CASHIER_CODE = O.USER_ID "
        + " AND B.CASE_NO = I.CASE_NO "
        + " AND I.BED_NO = D.BED_NO "
        + " AND D.STATION_CODE = T.STATION_CODE "
        + " AND T.DEPT_CODE = P.DEPT_CODE "
        + " AND B.PAY_TYPE = Y.ID "
        + " AND Y.GROUP_ID = 'GATHER_TYPE' "
        + " ORDER BY B.RECEIPT_NO ";
        TParm parmJRC = new TParm(TJDODBTool.getInstance().select(sqlJRC));
        double todayOut = parmJRC.getDouble("AMT",0);
        
        
        double he = yesterday+today;
        double ji = todayOut*-1;
        double ca = he-ji;
        
        
        
//        System.out.println("起始时间::::::"+start_date+"/n"+"结束时间::::::"+end_date);
//        System.out.println("今日::::::"+today);
//        System.out.println("昨日::::::"+df.format(yesterday));
//        System.out.println("今日出::::::"+todayOut*(-1));
//        System.out.println("今日余::::::"+df.format(ca));
        TParm taParm = new TParm();
        
//        taParm.addData("SYSTEM", "COLUMNS", df.format(yesterday));
//        taParm.addData("SYSTEM", "COLUMNS", today);
//        taParm.addData("SYSTEM", "COLUMNS", ji);
//        taParm.addData("SYSTEM", "COLUMNS", df.format(ca));
       
        String mo = this.getValueString("DATE_E").substring(0,19);
        String qi = this.getValueString("DATE_E").substring(0, 10)+ " 00:00:00";
        
        
        TParm result=new TParm(); 
        result.setData("qi","TEXT",qi);
        result.setData("mo","TEXT",mo);
        result.setData("yesterday","TEXT",df.format(yesterday));
        result.setData("today","TEXT",df.format(today));
        result.setData("todayOut","TEXT",df.format(ji));
        result.setData("ca","TEXT",df.format(ca));
        //System.out.println("bb:::::::"+result);
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILInPat1.jhw",
        		result);
		
		
		
		
		
		
		
		//==========zhangp 20120618 start
//		DecimalFormat df = new DecimalFormat("########0.00");
//		TTable table = (TTable) getComponent("TABLE");
//		TParm tableParm = table.getParmValue();
//		tableParm.setCount(tableParm.getCount("IPD_NO"));
//		tableParm.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
//		tableParm.addData("SYSTEM", "COLUMNS", "STATION_CODE");
//		tableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
//		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
//		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
//		tableParm.addData("SYSTEM", "COLUMNS", "PRE_AMT");
//		TParm printParm = new TParm();
//		printParm.setData("TABLE", tableParm.getData());
////		printParm.setData("ALLMONEY", "TEXT", df.format(StringTool.round(sum,2)));
//		String date = SystemTool.getInstance().getDate().toString();
//		printParm.setData("P_DATE", "TEXT", "制表时间: " + date);
//		printParm.setData("P_USER", "TEXT", "制表人: " + Operator.getName());
//		//==========zhangp 20120618 end
//		this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILInPat.jhw",
//				printParm);
	}
	
    /**
     * 汇出Excel
     */
    public void onExport() {
    	TTable table = (TTable) getComponent("TABLE");
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount() <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "预交金明细表");
    }
}
