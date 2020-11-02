package com.javahis.ui.inf;

import jdo.sys.OperatorTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ����ҩ����������
 * </p>
 * 
 * <p>
 * Description: ����ҩ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class InfAntibiotestControl extends TControl {
	private String caseNo = "";
	private String mrNo = "";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		TParm parm = (TParm) this.getParameter();
		caseNo = parm.getValue("CASE_NO");
		mrNo = parm.getValue("MR_NO");
		callFunction("UI|TABLE|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTableCheckBoxClicked" });
		initTable();
	}

	public void initTable() {
		TParm parm = new TParm(this.getDBTool()
				.select(this.getInfAntiTestSql()));
		if (parm.getCount() <= 0) {
			this.messageBox("û��ʵ������");
			return;
		}
		if (parm.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		}
		this.getTable("TABLE").setParmValue(parm);
	}

	private String getInfAntiTestSql() {
		String sql = "";
		sql = "SELECT 'N' AS FLG,A.CASE_NO,B.CULTURE_CODE,B.CULTURE_CHN_DESC,B.ANTI_CODE,B.ANTI_CHN_DESC,B.SENS_LEVEL,A.MR_NO,"
				+ " A.PAT_NAME,A.APPLICATION_NO "
				+ " FROM MED_APPLY A,MED_LIS_ANTITEST B "
				+ " WHERE A.CAT1_TYPE=B.CAT1_TYPE "
				+ " AND A.APPLICATION_NO=B.APPLICATION_NO "
				+ " AND A.CASE_NO='"
				+ caseNo + "'" + " AND A.MR_NO='" + this.mrNo + "'";
		System.out.println("sql==����ʵ����====="+sql);
		return sql;
	}

	public void onCANCLE() {
		this.closeWindow();
	}

	public void onOK() {
		TParm tableParm = this.getTable("TABLE").getParmValue();
		getTable("TABLE").acceptText();
		TParm parm = new TParm();
		int count = 0;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (tableParm.getValue("FLG", i).equals("Y")) {
				parm.addData("CULTURE_CODE",
						tableParm.getValue("CULTURE_CODE", i));
				parm.addData("ANTI_CODE", tableParm.getValue("ANTI_CODE", i));
				parm.addData("SENS_LEVEL", tableParm.getValue("SENS_LEVEL", i));
				count++;
			}
		}
		parm.setCount(count);
		this.setReturnValue(parm);
		this.closeWindow();
	}

	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		TParm parm = this.getTable("TABLE").getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("ALLCHECK").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		this.getTable("TABLE").setParmValue(parm);
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
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
	 * �õ�TTable����
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
}
