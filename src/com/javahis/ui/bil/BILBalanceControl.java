package com.javahis.ui.bil;

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

public class BILBalanceControl extends TControl {
	private Pat pat;
	/**
	 * 初始化界面
	 */
	public void onInit() {
		super.onInit();
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		/*this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// 开始时间
*/		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// 结束时间
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		this.setValue("MR_NO", "");
		this.setValue("DEPT_CODE", "");
	}
	/**
	 * 查询方法
	 */
	public void onQuery() {	
		String endDate=this.getValue("END_DATE").toString();
		endDate = endDate.substring(0, endDate.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		String where=" ";
		if(this.getValueString("MR_NO").length()>0){
			where +=" AND D.MR_NO='"+this.getValue("MR_NO").toString()+"' ";
		}
		if(this.getValue("DEPT_CODE")!=null&&this.getValue("DEPT_CODE").toString().length()>0){
			where+=" AND D.DEPT_CODE ='"+this.getValue("DEPT_CODE").toString()+"' ";
		}
		//明细
		String  sql="SELECT A.TOT_AMT ,B.* FROM ( SELECT SUM(TOT_AMT) TOT_AMT,CASE_NO FROM (" +
				" SELECT SUM (A.TOT_AMT) AS TOT_AMT,D.CASE_NO " +
				"FROM IBS_ORDD A, ADM_INP D WHERE A.CASE_NO = D.CASE_NO " +
				"AND D.REGION_CODE = 'H01' AND A.BILL_DATE BETWEEN D.IN_DATE " +
				"AND TO_DATE ('"+endDate+"','yyyyMMddhh24miss')"+where+" AND A.CASE_NO NOT IN(  " +
				"SELECT D.CASE_NO FROM ADM_INP D,BIL_IBS_RECPM H WHERE " +
				"D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01'" +
				" AND H.ACCOUNT_SEQ IS NOT NULL AND H.ACCOUNT_DATE < TO_DATE ('20151222000000','yyyyMMddhh24miss')" +
				" GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO FROM ADM_INP  WHERE M_CASE_NO IN( " +
				"SELECT D.CASE_NO FROM ADM_INP D,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO" +
				" AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL AND H.ACCOUNT_DATE < TO_DATE ('20151222000000','yyyyMMddhh24miss')" +
				" GROUP BY D.CASE_NO)) GROUP BY D.CASE_NO  UNION ALL SELECT -SUM (WRT_OFF_AMT) TOT_AMT,D.CASE_NO " +
				"FROM ADM_INP D, BIL_IBS_RECPM H, BIL_IBS_RECPD S WHERE D.CASE_NO = H.CASE_NO" +
				" AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL AND H.RECEIPT_NO = S.RECEIPT_NO" +
				" AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000','yyyyMMddhh24miss') AND TO_DATE ('"+endDate+"'," +
				" 'yyyyMMddhh24miss')"+where+" GROUP BY D.CASE_NO) GROUP BY CASE_NO) A," +
				"(SELECT * FROM(SELECT SUM(TOT_AMT) TOT_AMT,REXP_CODE,CASE_NO,MR_NO,PAT_NAME,DEPT_CHN_DESC FROM (" +
				"SELECT SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE,D.CASE_NO,D.MR_NO,P.PAT_NAME,B.DEPT_CHN_DESC" +
				" FROM IBS_ORDD A, ADM_INP D,SYS_CTZ Z,SYS_DEPT B,SYS_STATION C,SYS_PATINFO P" +
				" WHERE A.CASE_NO = D.CASE_NO AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND D.DEPT_CODE = B.DEPT_CODE" +
				" AND D.STATION_CODE = C.STATION_CODE(+) AND D.REGION_CODE = 'H01'" +
				" AND D.MR_NO=P.MR_NO AND A.BILL_DATE BETWEEN D.IN_DATE  AND TO_DATE ('"+endDate+"','yyyyMMddhh24miss')" +where+
				" AND A.CASE_NO NOT IN( SELECT D.CASE_NO FROM SYS_DEPT B, SYS_STATION C,ADM_INP D,SYS_CTZ Z,BIL_IBS_RECPM H " +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL " +
				" AND H.ACCOUNT_DATE <TO_DATE ('20151222000000','yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE" +
				" AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+) GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO" +
				" FROM ADM_INP WHERE M_CASE_NO IN(SELECT D.CASE_NO FROM SYS_DEPT B," +
				"SYS_STATION C,ADM_INP D, SYS_CTZ Z,BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO" +
				" AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL AND H.ACCOUNT_DATE <TO_DATE ('20151222000000', " +
				"'yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+)" +
				" GROUP BY D.CASE_NO)) GROUP BY A.REXP_CODE,D.CASE_NO,D.MR_NO,P.PAT_NAME,B.DEPT_CHN_DESC" +
				" UNION ALL SELECT -SUM (WRT_OFF_AMT) TOT_AMT, S.REXP_CODE,D.CASE_NO,D.MR_NO,P.PAT_NAME,B.DEPT_CHN_DESC FROM SYS_DEPT B," +
				" SYS_STATION C,ADM_INP D, SYS_CTZ Z, BIL_IBS_RECPM H,BIL_IBS_RECPD S,SYS_PATINFO P" +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL " +
				"AND H.RECEIPT_NO = S.RECEIPT_NO AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000', 'yyyyMMddhh24miss')" +
				" AND TO_DATE ('"+endDate+"','yyyyMMddhh24miss') AND D.DEPT_CODE = B.DEPT_CODE" +where+
				" AND D.STATION_CODE = C.STATION_CODE(+) AND D.CTZ1_CODE = Z.CTZ_CODE(+) AND D.MR_NO=P.MR_NO " +
				" GROUP BY S.REXP_CODE,D.MR_NO,D.CASE_NO,P.PAT_NAME,B.DEPT_CHN_DESC) GROUP BY CASE_NO,REXP_CODE,MR_NO,PAT_NAME,DEPT_CHN_DESC )" +
				" pivot (max(tot_amt) for rexp_code in(201,202,203,204,205,206,207,208,209,210,211,212,213,214,215.1,215.2," +
				"216,217,218,219,220))) B WHERE A.CASE_NO= B.CASE_NO AND A.TOT_AMT <>0 ";
		
		//System.out.println("ddddd"+sql);
		TParm tableParm= new TParm(TJDODBTool.getInstance().select(sql));
		//汇总
		String totsql="SELECT A.*,B.SUM_AMT FROM (  SELECT SUM (TOT_AMT) TOT_AMT, REXP_CODE FROM (SELECT SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE" +
				" FROM IBS_ORDD A, ADM_INP D WHERE A.CASE_NO = D.CASE_NO AND D.REGION_CODE = 'H01'" +
				"AND A.BILL_DATE BETWEEN D.IN_DATE AND TO_DATE ('"+endDate+"', 'yyyyMMddhh24miss')" +where+
				" AND A.CASE_NO NOT IN (SELECT D.CASE_NO FROM ADM_INP D, BIL_IBS_RECPM H " +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL" +
				" AND H.ACCOUNT_DATE <TO_DATE ('20151222000000','yyyyMMddhh24miss') " +
				"GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO IN" +
				" (SELECT D.CASE_NO FROM ADM_INP D, BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO" +
				" AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL AND H.ACCOUNT_DATE <" +
				"TO_DATE ('20151222000000','yyyyMMddhh24miss') GROUP BY D.CASE_NO)) GROUP BY A.REXP_CODE" +
				" UNION ALL SELECT -SUM (WRT_OFF_AMT) TOT_AMT, S.REXP_CODE FROM ADM_INP D, BIL_IBS_RECPM H, BIL_IBS_RECPD S" +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL" +
				" AND H.RECEIPT_NO = S.RECEIPT_NO AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000','yyyyMMddhh24miss')" +
				" AND TO_DATE ('"+endDate+"','yyyyMMddhh24miss')"+where+" GROUP BY S.REXP_CODE) GROUP BY REXP_CODE)" +
				" PIVOT (MAX (TOT_AMT)FOR REXP_CODE IN (201, 202,203,204,205,206, 207,208,209," +
				"210,211, 212, 213, 214,215.1, 215.2, 216,217,218,219,220)) A," +
				"" +
				"(SELECT SUM(TOT_AMT) SUM_AMT FROM( SELECT SUM (TOT_AMT) TOT_AMT, REXP_CODE FROM (SELECT SUM (A.TOT_AMT) AS TOT_AMT, A.REXP_CODE" +
				" FROM IBS_ORDD A, ADM_INP D WHERE A.CASE_NO = D.CASE_NO AND D.REGION_CODE = 'H01'" +
				"AND A.BILL_DATE BETWEEN D.IN_DATE AND TO_DATE ('"+endDate+"', 'yyyyMMddhh24miss')" +where+
				" AND A.CASE_NO NOT IN (SELECT D.CASE_NO FROM ADM_INP D, BIL_IBS_RECPM H " +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL" +
				" AND H.ACCOUNT_DATE <TO_DATE ('20151222000000','yyyyMMddhh24miss') " +
				"GROUP BY D.CASE_NO UNION ALL SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO IN" +
				" (SELECT D.CASE_NO FROM ADM_INP D, BIL_IBS_RECPM H WHERE D.CASE_NO = H.CASE_NO" +
				" AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL AND H.ACCOUNT_DATE <" +
				"TO_DATE ('20151222000000','yyyyMMddhh24miss') GROUP BY D.CASE_NO)) GROUP BY A.REXP_CODE" +
				" UNION ALL SELECT -SUM (WRT_OFF_AMT) TOT_AMT, S.REXP_CODE FROM ADM_INP D, BIL_IBS_RECPM H, BIL_IBS_RECPD S" +
				" WHERE D.CASE_NO = H.CASE_NO AND D.REGION_CODE = 'H01' AND H.ACCOUNT_SEQ IS NOT NULL" +
				" AND H.RECEIPT_NO = S.RECEIPT_NO AND H.ACCOUNT_DATE BETWEEN TO_DATE ('20151222000000','yyyyMMddhh24miss')" +
				" AND TO_DATE ('"+endDate+"','yyyyMMddhh24miss')"+where+" GROUP BY S.REXP_CODE) GROUP BY REXP_CODE)) B";
		//System.out.println("111111"+totsql);
		TParm sumParm=new TParm(TJDODBTool.getInstance().select(totsql));
		tableParm.addData("PAT_NAME", "");
		tableParm.addData("MR_NO", "");
		tableParm.addData("DEPT_CHN_DESC", "汇总:");
		tableParm.addData("TOT_AMT", sumParm.getDouble("SUM_AMT",0));
		tableParm.addData("201", sumParm.getDouble("201",0));
		tableParm.addData("202", sumParm.getDouble("202",0));
		tableParm.addData("203", sumParm.getDouble("203",0));
		tableParm.addData("204", sumParm.getDouble("204",0));
		tableParm.addData("205", sumParm.getDouble("205",0));
		tableParm.addData("206", sumParm.getDouble("206",0));
		tableParm.addData("207", sumParm.getDouble("207",0));
		tableParm.addData("208", sumParm.getDouble("208",0));
		tableParm.addData("209", sumParm.getDouble("209",0));
		tableParm.addData("210", sumParm.getDouble("210",0));
		tableParm.addData("211", sumParm.getDouble("211",0));
		tableParm.addData("212", sumParm.getDouble("212",0));
		tableParm.addData("213", sumParm.getDouble("213",0));
		tableParm.addData("214", sumParm.getDouble("214",0));
		tableParm.addData("215.1", sumParm.getDouble("215.1",0));
		tableParm.addData("215.2", sumParm.getDouble("215.2",0));
		tableParm.addData("216", sumParm.getDouble("216",0));
		tableParm.addData("217", sumParm.getDouble("217",0));
		tableParm.addData("218", sumParm.getDouble("218",0));
		tableParm.addData("219", sumParm.getDouble("219",0));
		tableParm.addData("220", sumParm.getDouble("220",0));
		this.callFunction("UI|TABLE|setParmValue", tableParm);
	}
	/**
	 * 病案号回车事件
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO", mrNo);
		onQuery();
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		onInit();
		}
	/**
	 * 导出
	 */
	public void onExport() {
        TTable table = (TTable) callFunction("UI|Table|getThis");
        if (table.getRowCount() > 0)
            ExportExcelUtil.getInstance().exportExcel(table, "应收在院病人余额明细表");
        else
        	this.messageBox("没有可导出的数据");
	}
	
}
