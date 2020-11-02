package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:��Ⱦ�����
 * </p>
 *
 * <p>
 * Description:��Ⱦ�����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.04
 * @version 1.0
 */
public class SYSDiseaseTypeControl extends TControl {

	private String action = "save";
	// ������
	private TTable table;

	public SYSDiseaseTypeControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		if ("save".equals(action)) {
			TTextField combo = getTextField("DISEASETYPE_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				row = table.getSelectedRow();
			}
                        table.setItem(row, "DISEASETYPE_CODE",
                                      getValueString("DISEASETYPE_CODE"));
                        table.setItem(row, "DISEASETYPE_DESC",
                                      getValueString("DISEASETYPE_DESC"));
                        table.setItem(row, "ENG_DESC", getValueString("ENG_DESC"));
			table.setItem(row, "PY1", getValueString("PY1"));
			table.setItem(row, "PY2", getValueString("PY2"));
			table.setItem(row, "SEQ", getValueInt("SEQ"));
			table.setItem(row, "DEADLINE", getValueInt("DEADLINE"));
			String desc = getValueString("DEADLINE_DESC");
			table.setItem(row, "DEADLINE_DESC", desc);
			String leg_flg = getValueString("LEGALINFECT_FLG");
			table.setItem(row, "LEGALINFECT_FLG", leg_flg);
			table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
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
		dataStore.setSQL(SYSSQL.getSYSDiseaseType());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String type_code = getValueString("DISEASETYPE_CODE");
		String filterString = "";
		if (type_code.length() > 0)
			filterString += "DISEASETYPE_CODE like '" + type_code + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "DISEASETYPE_CODE;DISEASETYPE_DESC;ENG_DESC;PY1;PY2;SEQ;"
				+ "DEADLINE;DEADLINE_DESC;LEGALINFECT_FLG;DESCRIPTION";
		clearValue(clearString);
		// ���
		TDataStore dataStroe = table.getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		table.setSelectionMode(0);
		getTextField("DISEASETYPE_CODE").setEnabled(true);
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
			String likeNames = "DISEASETYPE_CODE;DISEASETYPE_DESC;ENG_DESC;PY1;PY2;SEQ;"
					+ "DEADLINE;DEADLINE_DESC;LEGALINFECT_FLG;DESCRIPTION";
			this.setValueForParm(likeNames, parm);
			getTextField("DISEASETYPE_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * TypeDesc�س��¼�
	 */
	public void onTypeDescAction() {
		String py = TMessage.getPy(this.getValueString("DISEASETYPE_DESC"));
		setValue("PY1", py);
		getTextField("ENG_DESC").grabFocus();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSDiseaseType());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		// ����+1(SEQ)
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
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
		if ("".equals(getValueString("DISEASETYPE_CODE"))) {
			this.messageBox("�����벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("DISEASETYPE_DESC"))) {
			this.messageBox("������Ʋ���Ϊ��");
			return false;
		}
		if (getValueDouble("DEADLINE") <= 0) {
			this.messageBox("����ʱ�޲���С�ڻ����O");
			return false;
		}
		return true;
	}

	/**
	 * �õ����ı�� +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// ����������
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// ��������
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// �������ֵ
			if (max < value) {
				max = value;
				continue;
			}
		}
		// ���ż�1
		max++;
		return max;
	}
}
