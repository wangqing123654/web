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
 * Title:ҽ����λά��
 * </p>
 * 
 * <p>
 * Description:ҽ����λά��
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

public class SYSNhiCompanyControl extends TControl {

	private String action = "save";
	// ������
	private TTable table;

	public SYSNhiCompanyControl() {
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
			TTextField combo = getTextField("NHI_COMPANY_CODE");
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
			table.setItem(row, "NHI_COMPANY_CODE",
					getValueString("NHI_COMPANY_CODE"));
			table.setItem(row, "NHI_COMPANY_DESC",
					getValueString("NHI_COMPANY_DESC"));
			table.setItem(row, "PY1", getValueString("PY1"));
			table.setItem(row, "PY2", getValueString("PY2"));
			table.setItem(row, "SEQ", getValueInt("SEQ"));
			table.setItem(row, "CONTACT_PERSON",
					getValueString("CONTACT_PERSON"));
			table.setItem(row, "TEL", getValueString("TEL"));
			table.setItem(row, "E_MAIL", getValueString("E_MAIL"));
			table.setItem(row, "ADDRESS", getValueString("ADDRESS"));
			table.setItem(row, "INS_VIEW", getValueString("INS_VIEW"));
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
		dataStore.setSQL(SYSSQL.getSYSNhiCpmpany());
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();

		String code = getValueString("NHI_COMPANY_CODE");
		String desc = getValueString("NHI_COMPANY_DESC");
		String filterString = "";
		if (code.length() > 0 && desc.length() > 0)
			filterString += "NHI_COMPANY_CODE like '" + code
					+ "%' AND NHI_COMPANY_DESC like '" + desc + "%'";
		else if (code.length() > 0)
			filterString += "NHI_COMPANY_CODE like '" + code + "%'";
		else if (desc.length() > 0)
			filterString += "NHI_COMPANY_DESC like '" + desc + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "NHI_COMPANY_CODE;NHI_COMPANY_DESC;PY1;PY2;SEQ;"
				+ "CONTACT_PERSON;TEL;E_MAIL;ADDRESS;INS_VIEW;DESCRIPTION";
		clearValue(clearString);
		// ���
		TDataStore dataStroe = table.getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		table.setSelectionMode(0);
		getTextField("NHI_COMPANY_CODE").setEnabled(true);
		getTextField("NHI_COMPANY_DESC").setEnabled(true);
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
			String likeNames = "NHI_COMPANY_CODE;NHI_COMPANY_DESC;PY1;PY2;SEQ;"
					+ "CONTACT_PERSON;TEL;E_MAIL;ADDRESS;INS_VIEW;DESCRIPTION";
			this.setValueForParm(likeNames, parm);
			getTextField("NHI_COMPANY_CODE").setEnabled(false);
			getTextField("NHI_COMPANY_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * NhiCompanyDesc�س��¼�
	 */
	public void onNhiCompanyDescAction() {
		String py = TMessage.getPy(this.getValueString("NHI_COMPANY_DESC"));
		setValue("PY1", py);
		getTextField("PY1").grabFocus();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSNhiCpmpany());
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
		if ("".equals(getValueString("NHI_COMPANY_CODE"))) {
			this.messageBox("��λ���벻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("NHI_COMPANY_DESC"))) {
			this.messageBox("��λ���Ʋ���Ϊ��");
			return false;
		}
		if (!"".equals(getValueString("E_MAIL"))) {
			if (!StringTool.isEmail(getValueString("E_MAIL"))) {
				this.messageBox("�����ʽ����ȷ");
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
