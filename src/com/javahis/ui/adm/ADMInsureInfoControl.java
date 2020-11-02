package com.javahis.ui.adm;

import java.text.DecimalFormat;

import jdo.adm.ADMResvTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * 查询保险信息
 * @author Administrator
 *
 */
public class ADMInsureInfoControl extends TControl{

	int selectRow = -1;
	TParm acceptData = new TParm(); // 接参
	TTable table;
	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
		}
		table=(TTable)this.getComponent("TABLE");
		initUI();
	}

	/**
	 * 初始化界面
	 */
	public void initUI() {
		this.setValue("MR_NO", acceptData.getValue("MR_NO"));
		this.setValue("PAT_NAME", acceptData.getValue("PAT_NAME"));
		this.setValue("SEX_CODE1", acceptData.getValue("SEX_CODE"));
		TParm parm=new TParm();
		parm.setData("MR_NO",acceptData.getValue("MR_NO"));
		String sql = " SELECT  A.CASE_NO,A.MR_NO,B.BIRTH_DATE,B.PAT_NAME,B.SEX_CODE,A.IN_DATE," +
				" A.DS_DATE,A.IN_DEPT_CODE,A.VS_DR_CODE,A.INSURE_INFO,A.OLD_INSURE_INFO FROM ADM_INP A, SYS_PATINFO B" +
				" WHERE A.MR_NO = B.MR_NO AND A.MR_NO = '"+acceptData.getValue("MR_NO")+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		
		table.setParmValue(result);
	}
	
	/**
	 * 传回MR_NO;IPD_NO;PAT_NAME;IDNO;TEL_HOME;BIRTH_DATE
	 */
	public void onSave() {
		TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选中一行数据");
			return;
		}
		TParm backData = table.getParmValue().getRow(row);
		this.setReturnValue(backData);
		this.closeWindow();
	}

}
