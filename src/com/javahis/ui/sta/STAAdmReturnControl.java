package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: סԺ�����ٻ�ͳ��
 * </p>
 * 
 * <p>
 * Description: סԺ�����ٻ�ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl 20130722
 * @version 1.0
 */
public class STAAdmReturnControl extends TControl {
	TTable table;

	public void onInit() {
		super.onInit();
		table=this.getTTable("TABLE");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String tDate = StringTool.getString(sysDate, "yyyyMMdd");
		// Ĭ��������ʼ����
		this.setValue("S_DATE",
				StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss"));
		// Ĭ��������ֹ����
		this.setValue("E_DATE",
				StringTool.getTimestamp(tDate + "235959", "yyyyMMddHHmmss"));
	}
	/**
	 * ��ѯ����
	 */
    public  void onQuery(){
    	//��ʼʱ��
    	Timestamp start=(Timestamp)this.getValue("S_DATE");
    	//����ʱ��
    	Timestamp end=(Timestamp)this.getValue("E_DATE");
    	String  sStr=StringTool.getString(start, "yyyyMMddHHmmss");
    	String  eStr=StringTool.getString(end, "yyyyMMddHHmmss");
    	String  sql="SELECT  A.MR_NO,B.PAT_NAME,TO_CHAR(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS') AS IN_DATE,A.IN_DEPT_CODE,A.IN_STATION_CODE,"
    		+" TO_CHAR(A.DS_DATE,'YYYY/MM/DD HH24:MI:SS') AS DS_DATE,A.DS_DEPT_CODE,A.DS_STATION_CODE,TO_CHAR(A.RETURN_DATE,'YYYY/MM/DD HH24:MI:SS') AS RETURN_DATE"
    		+" FROM ADM_INP A,SYS_PATINFO B"
    		+" WHERE A.MR_NO=B.MR_NO "
    		+" AND A.IN_DATE BETWEEN TO_DATE('"+sStr+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eStr+"','YYYYMMDDHH24MISS')"
    		+" AND A.CANCEL_FLG='N' AND A.RETURN_DATE IS NOT NULL";
    	TParm parm=new TParm(this.getDBTool().select(sql));
    	if(parm.getCount()<0){
    		this.messageBox("û������");
    		table.removeRowAll();
    		return;
    	}
    	table.setParmValue(parm); 	
    }
    /**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "סԺ�����ٻ�ͳ�Ʊ���");
		}
	}
	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
