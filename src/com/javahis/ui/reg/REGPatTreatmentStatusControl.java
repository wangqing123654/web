package com.javahis.ui.reg;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title: 门诊患者在院就诊状态统计表
 * </p>
 * 
 * <p>
 * Description: 门诊患者在院就诊状态统计表
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author huangtt 20150325
 * @version 1.0
 */
public class REGPatTreatmentStatusControl extends TControl {
	
	TTable table;
	/**
	 * 初始化界面
	 */
	public void onInit() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		table = (TTable) this.getComponent("TABLE");
	}
	
	public void onQuery() {
		if (this.getValueString("S_DATE").length() <= 0
				|| this.getValueString("E_DATE").length() <= 0) {
			this.messageBox_("请选择查询起讫日期");
			return;
		}
		TParm parm = finishData();
		TParm result = this.selectDate(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() == 0) {
			this.messageBox("E0008");
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);

	}
	
	public void onMrNo(){
		String mrNo = this.getValueString("MR_NO");
		this.setValue("MR_NO", PatTool.getInstance().checkMrno(mrNo));
		this.onQuery();
	}
	
	/**
     * 查询配参
     * @return TParm
     */
    public TParm finishData() {
        TParm parm = new TParm();

        //科室
        if(getValueString("DEPT_CODE").length()>0)
            parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
        //病案号
        if(getValueString("MR_NO").length()>0)
            parm.setData("MR_NO", getValueString("MR_NO"));
        //起日
        if(getValueString("S_DATE").length()>0)
            parm.setData("S_DATE",
                         StringTool.getString(TCM_Transform.getTimestamp(
                                              getValue("S_DATE")), "yyyyMMdd"));
        //迄日
        if(getValueString("E_DATE").length()>0)
            parm.setData("E_DATE",
                         StringTool.getString(TCM_Transform.getTimestamp(
                                              getValue("E_DATE")), "yyyyMMdd"));
        return parm;
    }
	
	public void onClear(){
		this.clearValue("MR_NO;DEPT_CODE");
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		setValue("S_DATE", yesterday);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		table.removeRowAll();
	}
	
	 /**
     * 导出Excel表格
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("无导出资料");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "门诊患者在院就诊状态统计表");
    }
    
    public void onPrint(){
    	table.acceptText();
    	TParm tableParm = table.getParmValue();
    	int rowCount = tableParm.getCount("MR_NO");
    	if (rowCount < 1) {
			this.messageBox("请先查询数据!");
			return;
		}
    	TParm printParm = new TParm(); // 给报表传参的TParm
    	TParm T1 = new TParm(); // 表格数据
    	for (int i = 0; i < rowCount; i++) {
			T1.addRowData(tableParm, i);
		}
    	for(int i=0;i<rowCount;i++){
    		T1.setData("BIRTH_DATE", i, T1.getValue("BIRTH_DATE", i).toString().substring(0, 10).replace('-', '/'));
    		T1.setData("ADM_DATE", i, T1.getValue("ADM_DATE", i).toString().substring(0, 10).replace('-', '/'));
    		T1.setData("REG_DATE", i, T1.getValue("REG_DATE", i).toString().substring(0, 19).replace('-', '/'));
    		if(T1.getValue("SEEN_DR_TIME", i).toString().length()>0){
    			T1.setData("SEEN_DR_TIME", i, T1.getValue("SEEN_DR_TIME", i).toString().substring(0, 19).replace('-', '/'));
    		}
    		
    		if(T1.getValue("BILL_DATE", i).toString().length() > 0){
    			T1.setData("BILL_DATE", i, T1.getValue("BILL_DATE", i).toString().substring(0, 19).replace('-', '/'));
    		}
			
		}
		T1.setCount(rowCount);
		String[] chage = table.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyy/MM/dd");
		printParm.setData("START_DATE", "TEXT", sDate);
		printParm.setData("END_DATE", "TEXT", eDate);
		printParm.setData("TABLE", T1.getData());
		printParm.setData("TITLE", "TEXT", "门诊患者在院就诊状态表");
		printParm.setData("OPT_USER", "TEXT", "制表人:" + Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGPatTreatMentStatusPrint.jhw",
				printParm);
    }

	
	public TParm selectDate(TParm parm){
		String sql="SELECT A.CASE_NO, A.MR_NO, E.PAT_NAME, G.CHN_DESC SEX_DESC," +
				" E.BIRTH_DATE,F.CTZ_DESC,C.DEPT_CHN_DESC DEPT_DESC,A.ADM_DATE, " +
				" A.REG_DATE, A.SEEN_DR_TIME,  B.SEEN_DR_DESC," +
				" H.BILL_DATE,H.CASHIER_DESC" +
				" FROM REG_PATADM A,  SYS_DEPT C, " +
				" (SELECT A.CASE_NO, D.USER_NAME SEEN_DR_DESC" +
				" FROM OPD_DIAGREC A, SYS_OPERATOR D" +
				" WHERE A.MAIN_DIAG_FLG = 'Y' AND A.DR_CODE = D.USER_ID) B," +
				" SYS_PATINFO E,SYS_CTZ F,SYS_DICTIONARY G," +
				" (SELECT DISTINCT A.BILL_DATE, A.CASE_NO, C.USER_NAME CASHIER_DESC" +
				"     FROM (  SELECT MAX (B.BILL_DATE) BILL_DATE, B.CASE_NO FROM REG_PATADM A, BIL_OPB_RECP B" +
				"     WHERE A.CASE_NO = B.CASE_NO AND A.ADM_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"000000','YYYYMMDDHH24MISS')" +
				"	  AND TO_DATE ('"+parm.getValue("E_DATE")+"235959','YYYYMMDDHH24MISS')GROUP BY B.CASE_NO) A,BIL_OPB_RECP B,SYS_OPERATOR C" +
				"     WHERE A.CASE_NO = B.CASE_NO AND B.CASHIER_CODE = C.USER_ID) H" +
				" WHERE A.CASE_NO = B.CASE_NO(+)" +
				" AND A.REGCAN_DATE IS NULL" +
				" AND A.DEPT_CODE = C.DEPT_CODE" +
				" AND A.MR_NO = E.MR_NO" +
				" AND A.CTZ1_CODE = F.CTZ_CODE" +
				" AND G.GROUP_ID = 'SYS_SEX'" +
				" AND E.SEX_CODE = G.ID" +
				" AND A.CASE_NO = H.CASE_NO(+)" +
				" AND A.REG_DATE BETWEEN TO_DATE ('"+parm.getValue("S_DATE")+"000000', 'YYYYMMDDHH24MISS')" +
				"     AND TO_DATE ('"+parm.getValue("E_DATE")+"235959', 'YYYYMMDDHH24MISS')";
		
		if(parm.getValue("DEPT_CODE") != null && parm.getValue("DEPT_CODE").length() > 0){
    		sql += " AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"'";
    	}
    	if(parm.getValue("MR_NO") != null && parm.getValue("MR_NO").length() > 0){
    		sql += " AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
    	}
		sql +=" ORDER BY A.CASE_NO";
//		System.out.println(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
