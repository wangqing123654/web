package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
/**
 * 
 * @author Administrator
 *
 */
public class SYSAgeControl extends TControl {
	
	private String action = "save";
	// ������
	private TTable table;
	
	private static String   sql="SELECT AGE_CODE,AGE_DESC,AGE_ENG_DESC,PY,START_AGE,START_MONTH,"+
            "START_DAY,START_HOUR,END_AGE,END_MONTH,END_DAY,END_HOUR,SHOW_TYPE,SUM_JHW,"+
            "OPT_USER, OPT_DATE, OPT_TERM "
                    + "FROM SYS_AGE ORDER BY AGE_CODE ";

	public SYSAgeControl() {

	}

	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		if ("save".equals(action)) {
			TTextField combo = getTextField("AGE_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				row = table.getSelectedRow();
			}
			table.setItem(row, "AGE_CODE",
					getValueString("AGE_CODE"));
			table.setItem(row, "AGE_DESC",
					getValueString("AGE_DESC"));
			table.setItem(row, "AGE_ENG_DESC", getValueString("AGE_ENG_DESC"));
			table.setItem(row, "PY", getValueString("PY"));
			table.setItem(row, "START_AGE", getValueString("START_AGE"));
			table.setItem(row, "START_MONTH", getValueString("START_MONTH"));
			table.setItem(row, "START_DAY", getValueString("START_DAY"));
			table.setItem(row, "START_HOUR", getValueString("START_HOUR"));
			table.setItem(row, "END_AGE", getValueString("END_AGE"));
			table.setItem(row, "END_MONTH", getValueString("END_MONTH"));
			table.setItem(row, "END_DAY", getValueString("END_DAY"));
			table.setItem(row, "END_HOUR", getValueString("END_HOUR"));
			table.setItem(row, "SHOW_TYPE", getValueString("SHOW_TYPE"));
			table.setItem(row, "SUM_JHW", getValueString("SUM_JHW"));
			table.setItem(row, "OPT_USER", Operator.getID());
			table.setItem(row, "OPT_DATE", date);
			table.setItem(row, "OPT_TERM", Operator.getIP());
		}
		TDataStore dataStore = table.getDataStore();
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0001");
				table.removeRow(row);
				table.setDSValue();
				onClear();
				return;
			}
			table.setDSValue();
		}
		messageBox("P0001");
		table.setDSValue();
		onClear();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		table.removeRow(row);
		table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String code = getValueString("AGE_CODE");
		String desc = getValueString("AGE_DESC");
		String filterString = "";
		if (code.length() > 0 && desc.length() > 0)
			filterString += "AGE_CODE like '" + code
					+ "%' AND AGE_DESC like '" + desc + "%'";
		else if (code.length() > 0)
			filterString += "AGE_CODE like '" + code + "%'";
		else if (desc.length() > 0)
			filterString += "AGE_DESC like '" + desc + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "AGE_CODE;AGE_DESC;AGE_ENG_DESC;PY;START_AGE;START_MONTH;START_HOUR;"
				+ "START_DAY;END_AGE;END_MONTH;END_DAY;END_HOUR;SHOW_TYPE;SUM_JHW";
		clearValue(clearString);

		table.setSelectionMode(0);
		getTextField("AGE_CODE").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "AGE_CODE;AGE_DESC;AGE_ENG_DESC;PY;START_AGE;START_MONTH;START_HOUR;"
					+ "START_DAY;END_AGE;END_MONTH;END_DAY;END_HOUR;SHOW_TYPE;SUM_JHW";
			this.setValueForParm(likeNames, parm);
			getTextField("AGE_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	
	public void onDescAction() {
		String py = TMessage.getPy(this.getValueString("AGE_DESC"));
		setValue("PY", py);
		getTextField("AGE_ENG_DESC").grabFocus();
	}
	/**
	 * �õ�Table����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("AGE_CODE"))) {
			this.messageBox("������벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("AGE_DESC"))) {
			this.messageBox("�������Ʋ���Ϊ��");
			return false;
		}
		return true;
	}
}
