package com.javahis.ui.opd;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * 
 * <p>
 * Title: �ͻ���Դͳ�Ʊ���Panel
 * </p>
 * 
 * <p>
 * Description:�ͻ���Դͳ�Ʊ���Panel
 * </p>
 * 
 * <p>
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author huangjw 20150508
 * @version 1.0
 */
public class OPDClientResourceControl extends TControl{
	TTable table;
	@Override
	public void onInit() {
		super.onInit();
		table=(TTable) this.getComponent("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE",
	             date.toString().substring(0, 10).replace('-', '/'));
		this.setValue("START_DATE",
	             StringTool.rollDate(date, -1).toString().substring(0, 10).
	             replace('-', '/'));
		
	}
	/**
	 * ��ѯ
	 */
	public void onQuery(){
		if(!checkDate()){
			return;
		}
		String sDate=this.getValueString("START_DATE").toString().replaceAll("-", "/").substring(0,10);
		String eDate=this.getValueString("END_DATE").toString().replaceAll("-", "/").substring(0,10);
		String sql=this.getSQl(sDate,eDate);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<=0){
			this.messageBox("û������");
			table.removeRowAll();
			return;
		}
		table.setParmValue(parm);
	}
	
	/**
	 * У��ʱ��
	 */
	public boolean checkDate(){
		if(this.getValueString("START_DATE").equals("")||this.getValueString("END_DATE").equals("")){
			this.messageBox("���ڲ���Ϊ��");
			return false;
		}
		return true;
	}
	/**
	 * ��ѯsql
	 * @return
	 */
	public String getSQl(String sDate,String eDate){
		String sql="SELECT B.DEPT_CHN_DESC DEPT_CODE,"
	         +"��COUNT (DEPT_CHN_DESC) COUNT"
	         +"��FROM REG_PATADM A, SYS_DEPT B"
	         +"��WHERE     A.VISIT_CODE = '0'"
	         +"��AND A.ADM_DATE BETWEEN TO_DATE ('"+sDate+"','YYYY/MM/DD')"
	         +"�� AND TO_DATE ('"+eDate+"','YYYY/MM/DD')"
	         +"��AND A.DEPT_CODE = B.DEPT_CODE(+)"
	         +"  AND A.REGCAN_USER IS NULL"
	         +"  # "
	         +"��GROUP BY A.DEPT_CODE,B.DEPT_CHN_DESC ORDER BY A.DEPT_CODE,B.DEPT_CHN_DESC";
		String deptCode=this.getValueString("DEPT_CODE");
		if(deptCode!=null && !"".equals(deptCode)){
			sql=sql.replaceAll("#", "AND A.DEPT_CODE='"+deptCode+"'");
		}else{
			sql=sql.replaceAll("#", "");
		}
		System.out.println("sql::"+sql);
		return sql;
	}
	/**
     * ���Excel
     */
    public void onExcel() {
    	if(table.getRowCount()<=0){
    		this.messageBox("û�л������");
    		return;
    	}
        ExportExcelUtil.getInstance().exportExcel(table, "�ͻ���Դͳ�Ʊ���");
    }
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("DEPT_CODE");
		table.removeRowAll();
		onInit();
	}
}
