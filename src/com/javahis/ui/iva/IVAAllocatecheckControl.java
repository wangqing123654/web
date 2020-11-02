package com.javahis.ui.iva;

import jdo.iva.IVAAllocatecheckTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>
 * Title: 静配中心成品核对Control
 * </p>
 * 
 * <p>
 * Description: 静配中心成品核对Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2013.07.28
 * @version 1.0
 */
public class IVAAllocatecheckControl extends TControl {
	// 得到table控件
	private TTable table_d;
	private TTable table_m;

	private TTable getTTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/*
	 * 扫描瓶签查询调配药品信息
	 */
	public void onQuery() {
		// 得到TABLE_M控件

		table_m = this.getTTable("TABLE_D");
		table_m.removeRowAll();
		// 封装前台得到的值
		TParm parm = new TParm();
		// 得到前台控件值
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		String barcode = this.getValueString("BAR_CODE");

		if (barcode != null && !"".equals(barcode)) {
			parm.setData("BAR_CODE", barcode);
			resultM = IVAAllocatecheckTool.getInstance().queryInfo(parm);
			if(!resultM.getValue("IVA_CHECK_USER", 0).equals("") 
					&& resultM.getValue("IVA_CHECK_USER", 0) !=null){
				this.messageBox("此成品已核对！");
				this.clearValue("PAT_NAME;STATION_CODE;MR_NO;CASE_NO;BAR_CODE");
				((TTextField)this.getComponent("BAR_CODE")).requestFocus();
				return;
			}
		} else {
			messageBox("请输入查询信息！");
			return;
		}
		this.setValue("PAT_NAME", resultM.getValue("PAT_NAME", 0));
		this.setValue("STATION_CODE", resultM.getValue("STATION_DESC", 0));
		this.setValue("MR_NO", resultM.getValue("MR_NO", 0));
		this.setValue("CASE_NO", resultM.getValue("CASE_NO", 0));
		resultD.setData("CASE_NO", resultM.getValue("CASE_NO", 0));
		resultD.setData("BAR_CODE", parm.getValue("BAR_CODE"));
		TParm result = IVAAllocatecheckTool.getInstance().querydetail(resultD);
		if(result.getCount() <= 0){
			this.messageBox("未查询到数据");
			return;
		}
		for(int i=0; i<result.getCount("ORDER_CODE");i++){
			result.setData("EXEC_DATE", i, 
					result.getValue("EXEC_DATE", i).substring(0, 4)
					+"-"
					+result.getValue("EXEC_DATE", i).substring(4, 6)
					+"-"
					+result.getValue("EXEC_DATE", i).substring(6, 8)
					+" "
					+result.getValue("EXEC_DATE", i).substring(8, 10)
					+":"
					+result.getValue("EXEC_DATE", i).substring(10, 12));
		}
		table_m.setParmValue(result);
	}

	/*
	 * 调配审核完之后保存
	 */
	public void onSave() {
		String check_user = this.getValueString("IVA_CHECK_USER");
		if(check_user.equals("")){
			String type = "singleExe";
			TParm inParm = (TParm) this.openDialog(
					"%ROOT%\\config\\inw\\passWordCheck.x", type);
			String OK = inParm.getValue("RESULT");
			if (!OK.equals("OK")) {
				return;
			}
			this.setValue("IVA_CHECK_USER", inParm.getValue("USER_ID"));
			check_user = this.getValueString("IVA_CHECK_USER");
		}
		String case_no = this.getValueString("CASE_NO");
		String bar_code = this.getValueString("BAR_CODE");
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("IVA_CHECK_USER", check_user);
		parm.setData("BAR_CODE", bar_code);
		String sqlM = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
				+ " A.START_DTTM,A.END_DTTM,B.BAR_CODE,B.BATCH_CODE, "
				+ " B.ORDER_DATE,B.ORDER_DATETIME "
				+ " FROM ODI_DSPNM A,ODI_DSPND B "
				+ " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")
				+ "' AND A.IVA_FLG='Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN "
				+ " A.START_DTTM AND  A.END_DTTM "
				+ " AND B.BAR_CODE='"+parm.getValue("BAR_CODE")
				+ "' AND B.IVA_FLG='Y' AND B.IVA_DEPLOY_USER IS NOT NULL "
				+ " AND B.IVA_DEPLOY_USER IS NOT NULL "
				+ " AND B.IVA_FLG = 'Y'"
				+ " AND B.IVA_CHECK_USER IS NULL ";
//		System.out.println("sqlM===="+sqlM);
		TParm updateParm = new TParm(TJDODBTool.getInstance().select(sqlM));
		for(int j=0;j<updateParm.getCount("CASE_NO");j++){
			updateParm.setData("IVA_CHECK_USER",j, parm.getValue("IVA_CHECK_USER"));
		}
//		System.out.println("updateParm======="+updateParm);
		for(int i=0;i<updateParm.getCount("CASE_NO");i++){
//			System.out.println("parmRow>>>>>>>>>>>>>>>"+updateParm.getRow(i));
			TParm result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
					"onUpdateDepCheck", updateParm.getRow(i));
			if (result.getErrCode() < 0) {
				messageBox("审核失败！");
				return;
			}
		}
//		TParm result = TIOM_AppServer.executeAction("action.iva.IVADsAciton",
//				"onUpdateDepCheck", parm);
		this.messageBox("审核成功！");
		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this.clearValue("PAT_NAME;STATION_CODE;MR_NO;CASE_NO;BAR_CODE");
		((TTextField)this.getComponent("BAR_CODE")).requestFocus();
	}

	/*
	 * 清空
	 */

	public void onClear() {

		table_d = this.getTTable("TABLE_D");
		table_d.removeRowAll();
		this.clearValue("PAT_NAME;IVA_CHECK_USER;STATION_CODE;MR_NO;CASE_NO;BAR_CODE");
		((TTextField)this.getComponent("BAR_CODE")).requestFocus();
//		this.clearValue("STATION_CODE");
//		this.clearValue("MR_NO");
//		this.clearValue("CASE_NO");
//		this.clearValue("BAR_CODE");

	}

	public void onActionquery() {
		onQuery();
	}

	// /*
	// * 全选
	// */
	// public void onSelectAll() {
	// table_m = getTTable("TABLE_M");
	// table_m.acceptText();
	// if (table_m.getRowCount() < 0) {
	// getCheckBox("SELECT_ALL").setSelected(false);
	// return;
	// }
	// for (int i = 0; i < table_m.getRowCount(); i++) {
	// table_m.setItem(i, "SELECT_AVG", getValueString("SELECT_ALL"));
	// }
	// }
	//
	// /*
	// * 复选框事件
	// */
	// public void onTableCheckBoxClicked(Object obj) {
	//
	// table_m = this.getTTable("TABLE_M");
	// int column = table_m.getSelectedRow();
	// if ("N".equals(table_m.getItemString(column, "SELECT_AVG"))) {
	// table_m.setItem(column, "SELECT_AVG", "Y");
	// } else {
	// table_m.setItem(column, "SELECT_AVG", "N");
	// }
	// /*
	// * 单击事件
	// */
	// public void onClick() {
	// table_m = this.getTTable("TABLE_M");
	// table_d = this.getTTable("TABLE_D");
	// int row = table_m.getSelectedRow();
	// TParm tparm = table_m.getParmValue();
	// TParm parm = new TParm();
	// if (row != -1) {
	// this.setValue("STATION_DESC", tparm.getValue("STATION_DESC", row));
	// parm.setData("MR_NO", tparm.getValue("MR_NO", row));
	// parm.setData("BED_NO", tparm.getValue("BED_NO", row));
	// parm.setData("IPD_NO", tparm.getValue("IPD_NO", row));
	// TParm result = IVAAllocatecheckTool.getInstance().querydetail(parm);
	// table_d.setParmValue(result);
	// }
	// onTableCheckBoxClicked(this);
	//
	// }
}
