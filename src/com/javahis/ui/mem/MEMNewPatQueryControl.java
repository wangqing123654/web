package com.javahis.ui.mem;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title:新注册患者查询
 * </p>
 * 
 * <p>
 * Description: 新注册患者查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) /p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author huangtt 2013140220
 * @version 1.0
 */
public class MEMNewPatQueryControl extends TControl {
	private  TTable table;
	private  String mrNo;

	/**
	 * 初始化
	 */
	public void onInit() {

		table = (TTable) getComponent("TABLE");

		// 得到当前时间
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().replace("-", "/")
				.substring(0, 10)
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().replace("-", "/").substring(
				0, 19));

		onQuery();

	}

	public void onQuery() {
		String sDate = this.getValueString("START_DATE").replace("-", "").replace("/", "").substring(0, 8)+"000000";
		String eDate = this.getValueString("END_DATE").replace("-", "").replace("/", "").substring(0, 8)+"235959";
		String sql = "SELECT PAT_NAME, SEX_CODE, MR_NO, BIRTH_DATE, ID_TYPE, IDNO, '' MEM_FLG" +
				" FROM SYS_PATINFO" +
				" WHERE OPT_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')";
		String patName = this.getValueString("PAT_NAME");
		if(patName.length()>0){
			sql += " AND PAT_NAME='"+patName+"'";
		}
		String py1 = this.getValueString("PY1").toUpperCase();
		if(py1.length()>0){
			sql += " AND PY1='"+py1+"'";
		}
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm tableParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			String mr_no = parm.getValue("MR_NO", i);
			if (getCountEkt(mr_no) < 0) {
				tableParm.addData("PAT_NAME", parm.getData("PAT_NAME", i));
				tableParm.addData("SEX_CODE", parm.getData("SEX_CODE", i));
				tableParm.addData("MR_NO", parm.getData("MR_NO", i));
				tableParm.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", i));
				tableParm.addData("ID_TYPE", parm.getData("ID_TYPE", i));
				tableParm.addData("IDNO", parm.getData("IDNO", i));
				if(getCountMem(mr_no) > 0){
					tableParm.addData("MEM_FLG", "Y");
				}else{
					tableParm.addData("MEM_FLG", "N");
				}	
			}
		}
		
		table.setParmValue(tableParm);

	}
	/**
	 *判断是否制过卡
	 * @param mrNo
	 * @return
	 */
	public int getCountEkt(String mrNo){
		String sql = "SELECT CARD_NO FROM EKT_ISSUELOG WHERE MR_NO = '"+mrNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getCount();
	}
	
	public int getCountMem(String mrNo){
		String sql = "SELECT MEM_CARD_NO FROM MEM_TRADE WHERE MR_NO = '"+mrNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getCount();
	}
	
	public void onClear() {
		this.clearValue("START_DATE;END_DATE;PAT_NAME;PY1");
		table.removeRowAll();
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().replace("-", "/")
				.substring(0, 10)
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().replace("-", "/").substring(
				0, 19));

	}
	
	public void onTableClicked(){
		table.acceptText();
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		mrNo = parm.getValue("MR_NO", row);
		
	}
	
	public void onTableDoubleClicked(){
		table.acceptText();
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		mrNo = parm.getValue("MR_NO", row);
		this.onOk();
	}
	
	public void onC(){
		this.closeWindow();
	}
	
	public void onOk(){
		if("".equals(mrNo) || mrNo == null){
			this.messageBox("请选择要传回的数据!");
			return;
		}
		TParm retDate = new TParm();
		retDate.setData("MR_NO", mrNo);
		this.setReturnValue(retDate);
		this.closeWindow();
	}
}
