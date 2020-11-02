package com.javahis.ui.reg;

import java.sql.Timestamp;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
*
* <p>Title: 门急诊挂号未看诊查询</p>
*
* <p>Description: 急诊挂号未看诊查询</p>
*
*
* <p>Company: bluecore</p>
*
* @author huangtt 20151229
* @version 4.0
*/
public class REGRegNoSeeQueryControl extends TControl{
	
	TTable table;
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
	}
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		onQuery();
		
	}
	
	public void onQuery(){
		boolean isDedug=true; //add by huangtt 20160505 日志输出
		
		String startTime = getValueString("S_DATE");
		startTime = startTime.substring(0, 4)+startTime.substring(5, 7)+startTime.substring(8, 10)+startTime.substring(11, 13)+startTime.substring(14, 16)+startTime.substring(17, 19);
		String endTime = getValueString("E_DATE");
		endTime = endTime.substring(0, 4)+endTime.substring(5, 7)+endTime.substring(8, 10)+endTime.substring(11, 13)+endTime.substring(14, 16)+endTime.substring(17, 19);
		
		String where = "";
		if(this.getValueString("SESSION_CODE").length() > 0){
			where += " AND A.SESSION_CODE='"+this.getValueString("SESSION_CODE")+"'";
		}
		if(this.getValueString("CLINICTYPE_CODE").length() > 0){
			where += " AND A.CLINICTYPE_CODE='"+this.getValueString("CLINICTYPE_CODE")+"'";
		}
		if(this.getValueString("DEPT_CODE").length() > 0){
			where += " AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
		}
		if(this.getValueString("DR_CODE").length() > 0){
			where += " AND A.DR_CODE='"+this.getValueString("DR_CODE")+"'";
		}
		if(this.getValueString("MR_NO").length() > 0){
			where += " AND A.MR_NO='"+this.getValueString("MR_NO")+"'";
		}
		
		String sql = "  SELECT TO_CHAR (ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
				" TO_CHAR (REG_DATE, 'YYYY/MM/DD HH24:MI:SS') REG_DATE," +
				" SESSION_DESC, CLINICTYPE_DESC, CASE_NO, MR_NO, PAT_NAME," +
				" DEPT_CHN_DESC DEPT_DESC, DR_DESC" +
				" FROM (SELECT A.ADM_DATE, A.REG_DATE, F.SESSION_DESC, G.CLINICTYPE_DESC," +
				" A.CASE_NO, B.CASE_NO CASE_NO1, A.MR_NO, E.PAT_NAME, C.DEPT_CHN_DESC," +
				" D.USER_NAME DR_DESC FROM REG_PATADM A, OPD_ORDER B, SYS_DEPT C," +
				" SYS_OPERATOR D, SYS_PATINFO E, REG_SESSION F, REG_CLINICTYPE G" +
				" WHERE A.CASE_NO = B.CASE_NO(+)" +
				" AND A.REGCAN_USER IS NULL" +
				" AND A.MR_NO = E.MR_NO" +
				" AND A.DEPT_CODE = C.DEPT_CODE" +
				" AND A.DR_CODE = D.USER_ID" +
				" AND A.SESSION_CODE = F.SESSION_CODE" +
				" AND A.CLINICTYPE_CODE = G.CLINICTYPE_CODE" +
				where +
				" AND A.ADM_DATE BETWEEN TO_DATE ('"+startTime+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+endTime+"','YYYYMMDDHH24MISS'))" +
				" WHERE CASE_NO1 IS NULL" +
				" ORDER BY ADM_DATE, REG_DATE, CASE_NO, SESSION_DESC, CLINICTYPE_DESC";
		
		TParm parm =new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("挂号未就诊－－－－"+sql);
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		
		if(isDedug){
			if(parm.getErrCode() < 0){
				System.out.println(" come in class: REGRegNoSeeQueryControl ，method ：onQuery");
				System.out.println("err:"+parm);

			}
		}
		
		table.setParmValue(parm);
		
	}
	
	public void onClear(){
		this.clearValue("SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;MR_NO");
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("S_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		table.removeRowAll();
	}
	
	public void onExport(){
		table.acceptText();
		ExportExcelUtil.getInstance().exportExcel(table, "门急诊挂号未看诊统计表");
	}


}
