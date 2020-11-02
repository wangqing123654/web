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
 * Title:�ն˻�ά�� 
 * </p>
 * 
 * <p>
 * Description:�ն˻�ά��
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
public class SYSTerminalControl extends TControl {

	private String action = "save";
	// ������
	private TTable table;

	public SYSTerminalControl() {
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
			TTextField combo = getTextField("TERM_NO");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				if (!CheckData())
					return;
				row = table.getSelectedRow();
			}
			table.setItem(row, "TERM_NO", getValueString("TERM_NO"));
			String desc = getValueString("TERM_CHN_DESC");
			table.setItem(row, "TERM_CHN_DESC", desc);
			desc = getValueString("TERM_ENG_DESC");
			table.setItem(row, "TERM_ENG_DESC", desc);
			table.setItem(row, "PY1", getValueString("PY1"));
			table.setItem(row, "PY2", getValueString("PY2"));
			table.setItem(row, "SEQ", getValueInt("SEQ"));
			table.setItem(row, "TERM_IP", getValueString("TERM_IP"));
			table.setItem(row, "TEL_EXT", getValueString("TEL_EXT"));
			table.setItem(row, "LOC_CODE", getValueString("LOC_CODE"));
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
		dataStore.setSQL(SYSSQL.getSYSTerminal());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String code = getValueString("TERM_NO");
		String desc = getValueString("TERM_CHN_DESC");
		String filterString = "";
		if (code.length() > 0 && desc.length() > 0)
		{
			filterString += "TERM_NO like '" + code
					+ "%' AND TERM_CHN_DESC like '" + desc + "%'";
		}
		else if (code.length() > 0)  
		{
			filterString += "TERM_NO like '" + code + "%'";
		}
		else if (desc.length() > 0)
		{
			filterString += "TERM_CHN_DESC like '" + desc + "%'";
		}  
		filterString += " ORDER BY TERM_NO ,SEQ ";     
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "TERM_NO;TERM_CHN_DESC;TERM_ENG_DESC;PY1;PY2;"
				+ "SEQ;TERM_IP;TEL_EXT;LOC_CODE;DESCRIPTION";
		clearValue(clearString);
		// ���
		TDataStore dataStroe = table.getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		table.setSelectionMode(0);
		getTextField("TERM_NO").setEnabled(true);
		getTextField("TERM_CHN_DESC").setEnabled(true);
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
			String likeNames = "TERM_NO;TERM_CHN_DESC;TERM_ENG_DESC;PY1;PY2;"
					+ "SEQ;TERM_IP;TEL_EXT;LOC_CODE;DESCRIPTION";
			this.setValueForParm(likeNames, parm);
			getTextField("TERM_NO").setEnabled(false);
			getTextField("TERM_CHN_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * TermDesc�س��¼�
	 */
	public void onTermDescAction() {
		String py = TMessage.getPy(this.getValueString("TERM_CHN_DESC"));
		setValue("PY1", py);
		getTextField("TERM_ENG_DESC").grabFocus();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSTerminal());
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
		if ("".equals(getValueString("TERM_NO"))) {
			this.messageBox("��ĩ���벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("TERM_CHN_DESC"))) {
			this.messageBox("��ĩ���Ʋ���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("TERM_IP"))) {
			this.messageBox("IP��ַ����Ϊ��");
			return false;
		} else {
			if (!StringTool.isIP(getValueString("TERM_IP"))) {
				this.messageBox("IP��ַ��ʽ����ȷ");
				return false;
			}
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
