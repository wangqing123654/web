package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
*
* <p>Title: 门急诊未结算信息查询 </p>
*
* <p>Description: 门急诊未结算信息查询</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151230
* @version 4.0
*/
public class REGRegNotSettledControl extends TControl{
	
	TTable table;

	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		initPage();
	}
	
	public void initPage(){
		this.setValue("STATUS", "1");
		onClickRadioButton();
		
	}
	
	public void onQuery(){
		DecimalFormat df = new DecimalFormat("##########0.00");
		String where = "";
		if(this.getValueString("DEPT_CODE").length() > 0){
			where += " AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
		}
		if(this.getValueString("DR_CODE").length() > 0){
			where += " AND A.DR_CODE='"+this.getValueString("DR_CODE")+"'";
		}
		if(this.getValueString("MR_NO").length() > 0){
			where += " AND A.MR_NO='"+this.getValueString("MR_NO")+"'";
		}
		String dateWhere="";
		if ("Y".equalsIgnoreCase(this.getValueString("ADM_DATE_FLG"))) {
			String startTime = StringTool.getString(
					TypeTool.getTimestamp(getValue("S_DATE1")), "yyyyMMdd");
			String endTime = StringTool.getString(
					TypeTool.getTimestamp(getValue("E_DATE1")), "yyyyMMdd");
			dateWhere =" AND A.ADM_DATE BETWEEN TO_DATE ('"+startTime+"000000', 'yyyyMMddHH24miss') "
            +" AND TO_DATE ('"+endTime+"235959', 'yyyyMMddHH24miss') ";
		}
		if ("Y".equalsIgnoreCase(this.getValueString("REG_DATE_FLG"))) {
			
			String startTime = getValueString("S_DATE");
			startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
			String endTime = getValueString("E_DATE");
			endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
			
			dateWhere =" AND A.REG_DATE BETWEEN TO_DATE ('"+startTime+"', 'yyyyMMddHH24miss') "
            +" AND TO_DATE ('"+endTime+"', 'yyyyMMddHH24miss') ";
		}
		
		int status = this.getValueInt("STATUS");
		
		String sql = "";
		if(status == 1){ //有未结算
			sql = " SELECT TO_CHAR ( A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
					" TO_CHAR (A.REG_DATE, 'YYYY/MM/DD HH24:MI:SS')  REG_DATE," +
					" A.CASE_NO, A.MR_NO, E.PAT_NAME, C.DEPT_CHN_DESC DEPT_DESC," +
					" D.USER_NAME AS DR_DESC, SUM (B.AR_AMT) AR_AMT" +
					" FROM REG_PATADM A, OPD_ORDER B, SYS_DEPT C, SYS_OPERATOR D, SYS_PATINFO E" +
					" WHERE A.CASE_NO = B.CASE_NO" +
					" AND A.DEPT_CODE = C.DEPT_CODE" +
					" AND A.DR_CODE = D.USER_ID" +
					" AND A.REGCAN_USER IS NULL" +
					" AND B.RECEIPT_NO IS NULL" +
					" AND A.MR_NO = E.MR_NO" +
					dateWhere + 
					where +
					" GROUP BY A.ADM_DATE, A.REG_DATE, A.CASE_NO, A.MR_NO," +
					" E.PAT_NAME, C.DEPT_CHN_DESC, D.USER_NAME" +
					" ORDER BY A.ADM_DATE, A.REG_DATE, A.CASE_NO,A.MR_NO," +
					" E.PAT_NAME, C.DEPT_CHN_DESC, D.USER_NAME";
				
		}else if(status == 2){ //全部未结算
			sql = "SELECT A.* FROM (  " +
					" SELECT TO_CHAR ( A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
					" TO_CHAR (A.REG_DATE, 'YYYY/MM/DD HH24:MI:SS')  REG_DATE," +
					" A.CASE_NO, A.MR_NO, E.PAT_NAME, C.DEPT_CHN_DESC DEPT_DESC," +
					" D.USER_NAME AS DR_DESC, SUM (B.AR_AMT) AR_AMT" +
					" FROM REG_PATADM A, OPD_ORDER B, SYS_DEPT C," +
					" SYS_OPERATOR D, SYS_PATINFO E" +
					" WHERE  A.CASE_NO = B.CASE_NO" +
					" AND A.DEPT_CODE = C.DEPT_CODE" +
					" AND A.DR_CODE = D.USER_ID" +
					" AND A.REGCAN_USER IS NULL" +
					" AND B.RECEIPT_NO IS NULL" +
					" AND A.MR_NO = E.MR_NO" +
					dateWhere + 
					where +
					" GROUP BY A.ADM_DATE, A.REG_DATE, A.CASE_NO," +
					" A.MR_NO, E.PAT_NAME, C.DEPT_CHN_DESC, D.USER_NAME" +
					" ORDER BY A.ADM_DATE, A.REG_DATE, A.CASE_NO," +
					" A.MR_NO,  C.DEPT_CHN_DESC, D.USER_NAME) A" +
					" WHERE A.CASE_NO NOT IN (SELECT A.CASE_NO" +
						" FROM (  SELECT A.CASE_NO  FROM REG_PATADM A," +
						" OPD_ORDER B, SYS_DEPT C, SYS_OPERATOR D" +
						" WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = C.DEPT_CODE" +
						" AND A.DR_CODE = D.USER_ID AND A.REGCAN_USER IS NULL " +
						" AND B.RECEIPT_NO IS NULL" +
						dateWhere + 
						 where +
						" ) A, (SELECT DISTINCT A.CASE_NO" +
						" FROM REG_PATADM A, SYS_DEPT B, BIL_OPB_RECP D," +
						" SYS_REGION E, SYS_OPERATOR F" +
						" WHERE A.DEPT_CODE = B.DEPT_CODE(+) AND A.REGION_CODE = E.REGION_CODE" +
						" AND A.REGCAN_USER IS NULL AND A.CASE_NO = D.CASE_NO" +
						" AND A.DR_CODE = F.USER_ID" +
						dateWhere + 
						where +
						" ) B WHERE A.CASE_NO = B.CASE_NO)";
			
		
		}else if(status == 3){ //部分未结算	
			sql = "SELECT A.* FROM (  SELECT TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
					" TO_CHAR (A.REG_DATE, 'YYYY/MM/DD HH24:MI:SS') REG_DATE, A.CASE_NO," +
					" A.MR_NO, E.PAT_NAME, C.DEPT_CHN_DESC DEPT_DESC, D.USER_NAME AS DR_DESC," +
					" SUM (B.AR_AMT) AR_AMT FROM REG_PATADM A, OPD_ORDER B, SYS_DEPT C," +
					" SYS_OPERATOR D, SYS_PATINFO E" +
					" WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = C.DEPT_CODE" +
					" AND A.DR_CODE = D.USER_ID AND A.REGCAN_USER IS NULL" +
					" AND B.RECEIPT_NO IS NULL AND A.MR_NO = E.MR_NO" +
					dateWhere + 
					where +
					" GROUP BY A.ADM_DATE, A.REG_DATE, A.CASE_NO, A.MR_NO," +
					" E.PAT_NAME, C.DEPT_CHN_DESC, D.USER_NAME" +
					" ORDER BY A.ADM_DATE, A.REG_DATE, A.CASE_NO, A.MR_NO," +
					" E.PAT_NAME, C.DEPT_CHN_DESC, D.USER_NAME) A," +
					" (SELECT DISTINCT A.CASE_NO FROM REG_PATADM A," +
					" SYS_DEPT B, BIL_OPB_RECP D, SYS_REGION E, SYS_OPERATOR F" +
					" WHERE A.DEPT_CODE = B.DEPT_CODE(+) AND A.REGION_CODE = E.REGION_CODE" +
					" AND A.REGCAN_USER IS NULL AND A.CASE_NO = D.CASE_NO" +
					" AND A.DR_CODE = F.USER_ID" +
					dateWhere + 
					where +
					") B WHERE A.CASE_NO = B.CASE_NO";
			
			
		}		
		System.out.println(status+"---------"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			this.messageBox("没有查询的数据");
			table.removeRowAll();
			return;
		}
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		if(isDedug){
			if(parm.getErrCode() < 0){
				System.out.println(" come in class: REGRegNotSettledControl ，method ：onQuery");
				System.out.println("err:"+parm);

			}
		}
		
		for (int i = 0; i < parm.getCount(); i++) {
			parm.setData("AR_AMT", i, df.format(parm.getDouble("AR_AMT", i)));
		}
		table.setParmValue(parm);
	}
	
	
	public void onClear(){
		this.clearValue("DEPT_CODE;DR_CODE;MR_NO");
		initPage();
		table.removeRowAll();
		this.setValue("DEPT_CODE", "");
		this.setValue("DR_CODE", "");
		TTextFormat drCode = (TTextFormat) this.getComponent("DR_CODE");
		drCode.onQuery();
	}
	
	public void onExport(){
		table.acceptText();
		TComboBox status = (TComboBox) this.getComponent("STATUS");
		String name = status.getSelectedText();
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊"+name+"数据统计表");
	}
	
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		onQuery();
		
	}
	
	public void onClickRadioButton(){
		
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -30);
		Timestamp date = SystemTool.getInstance().getDate();
		if ("Y".equalsIgnoreCase(this.getValueString("ADM_DATE_FLG"))) {
			callFunction("UI|S_DATE1|setEnabled", true);
			callFunction("UI|E_DATE1|setEnabled", true);
			callFunction("UI|S_DATE|setEnabled", false);
			callFunction("UI|E_DATE|setEnabled", false);	
			setValue("S_DATE1", yesterday );
			setValue("E_DATE1", date );
			setValue("S_DATE", "");
			setValue("E_DATE", "");
			
		}
		if ("Y".equalsIgnoreCase(this.getValueString("REG_DATE_FLG"))) {
			callFunction("UI|S_DATE1|setEnabled", false);
			callFunction("UI|E_DATE1|setEnabled", false);
			callFunction("UI|S_DATE|setEnabled", true);
			callFunction("UI|E_DATE|setEnabled", true);
			setValue("S_DATE",  yesterday.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
			setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
			setValue("S_DATE1", "");
			setValue("E_DATE1", "");
			
		}
	}

}
