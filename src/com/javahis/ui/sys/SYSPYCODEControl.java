package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;

public class SYSPYCODEControl extends TControl {

	public String[] tableNames;
	public String[] columnnames;
	public String[] columntypes;
	public TParm parm;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��TABLENAME�б�
		onClear();
		TComboBox table = getComboBox("TABLENAME");
		tableNames = TJDODBTool.getInstance().getTables();
		table.setVectorData(tableNames);
	}

	/**
	 * ��������
	 */
	public void onFilter() {
		clearValue("DESC;PY");
		TComboBox table = getComboBox("TABLENAME");
		TComboBox desc = getComboBox("DESC");
		TComboBox py = getComboBox("PY");
		String tablename = table.getValue();
		columnnames = TJDODBTool.getInstance().getColumns(tablename);
		columntypes = TJDODBTool.getInstance().getColumnType(tablename,
				columnnames);
		parm = new TParm();
		for (int i = 0; i < columnnames.length; i++) {
			if ("VARCHAR2".equals(columntypes[i])) {
				parm.addData(columnnames[i], columntypes[i]);
			}
		}
		TParm pm = new TParm();
		if (parm.existData("PY1")) {
			pm.addData("PY1", "PY1");
			parm.removeData("PY1");
		}
		if (parm.existData("PY2")) {
			pm.addData("PY2", "PY2");
			parm.removeData("PY2");
		}
		py.setVectorData(pm.getNames());
		desc.setVectorData(parm.getNames());
	}

	/**
	 * ���淽��
	 */
	public boolean onSave() {
		// ������ݷǿ�
		if (!Check_Data())
			return false;
		String sql = "UPDATE " + getValueString("TABLENAME") + " SET "
				+ getValueString("PY") + " = py(" + getValueString("DESC")
				+ ")";
		TParm update = new TParm();
		update.setData("UPDATE_PY", sql);
		TParm result = TIOM_AppServer.executeAction(
				"action.sys.SYSPYCODEAction", "onUpdate", update);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ�ܣ�" + result.getErrName()
					+ result.getErrText());
			return false;
		}
		this.messageBox("���³ɹ�");
		return true;
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		String clearObj = "TABLENAME;DESC;PY";
		clearValue(clearObj);
		getComboBox("DESC").removeAllItems();
		getComboBox("PY").removeAllItems();
	}

	/**
	 * ������ݷǿ�
	 * 
	 * @return
	 */
	public boolean Check_Data() {
		if (this.getValueString("TABLENAME").equals("")) {
			this.messageBox("��������Ϊ��");
			return false;
		}
		if (this.getValueString("DESC").equals("")) {
			this.messageBox("DESC�в���Ϊ��");
			return false;
		}
		if (this.getValueString("PY").equals("")) {
			this.messageBox("PY�в���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * ��������б����
	 * 
	 * @param tag
	 * @return
	 */
	public TComboBox getComboBox(String tag) {
		return (TComboBox) callFunction("UI|" + tag + "|getThis");
	}

}
