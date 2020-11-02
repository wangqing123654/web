package com.javahis.ui.ind;

import jdo.ind.INDSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: ѡ����λ
 * </p>
 * 
 * <p>
 * Description: ѡ����λ
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class INDMacValidControl extends TControl {

	private String org_code;

	public INDMacValidControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ȡ�ô������
		Object obj = getParameter();
		if (obj != null) {
			org_code = (String) obj;
		}
		// ��ʼ��������
		initPage();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getMaterialloc(org_code)));
		TTable table = (TTable) this.getComponent("TABLE");
		table.setParmValue(result);
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		TTable table = (TTable) this.getComponent("TABLE");
		int row = table.getSelectedRow();
		if (row != -1) {
			this.setValue("CODE", (String) table.getValueAt(row, 0));
			this.setValue("DESC", (String) table.getValueAt(row, 1));
		}
	}

	/**
	 * �����¼�
	 */
	public void onReturn() {
		setReturnValue(getValueString("CODE"));
		this.closeWindow();
	}
}
