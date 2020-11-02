package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDSQL;
import jdo.sys.Operator;

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
 * Title: ҩ��ԭ����
 * </p>
 * 
 * <p>
 * Description: ҩ��ԭ����
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
 * @author zhangy 2009.04.23
 * @version 1.0
 */

public class INDReasonControl extends TControl {

	private String action = "save";

	public INDReasonControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��������
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		TTable table = getTable("TABLE");
		int row = 0;
		if ("save".equals(action)) {
			TTextField combo = (TTextField) getComponent("REASON_CODE");
			boolean flg = combo.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				row = table.getSelectedRow();
			}
			table.setItem(row, "REASON_CODE", getValueString("REASON_CODE"));
			table.setItem(row, "REASON_TYPE", getValueString("REASON_TYPE"));
			table.setItem(row, "REASON_CHN_DESC",
					getValueString("REASON_CHN_DESC"));
			table.setItem(row, "REASON_ENG_DESC",
					getValueString("REASON_ENG_DESC"));
			table.setItem(row, "PY1", getValueString("PY1"));
			table.setItem(row, "PY2", getValueString("PY2"));
			table.setItem(row, "SEQ", getValueString("SEQ"));
			table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
			table.setItem(row, "OPT_USER", Operator.getID());
			Timestamp date = StringTool.getTimestamp(new Date());
			table.setItem(row, "OPT_DATE", date);
			table.setItem(row, "OPT_TERM", Operator.getIP());
		}
		TDataStore dataStore = table.getDataStore();
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0001");
				return;
			}
			table.setDSValue();
		}
		messageBox("P0001");
		table.setDSValue();
		onClear();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String code = getValueString("REASON_CODE");
		String type = getValueString("REASON_TYPE");
		String filterString = "";
		if (code.length() > 0 && type.length() > 0) {
			filterString = "REASON_TYPE = '" + type
					+ "' AND REASON_CODE like '" + code + "%'";
		} else if (code.length() > 0) {
			filterString = "REASON_CODE like '" + code + "%'";
		} else if (type.length() > 0) {
			filterString = "REASON_TYPE = '" + type + "'";
		} else {
			filterString = "";
		}
		TTable table = getTable("TABLE");
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		TTable table = getTable("TABLE");
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		table.removeRow(row);
		table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		getTextField("REASON_CODE").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		String tags = "REASON_CODE;REASON_TYPE;REASON_CHN_DESC;REASON_ENG_DESC;"
				+ "PY1;PY2;DESCRIPTION";
		clearValue(tags);
		// ����+1(SEQ)
		TDataStore dataStroe = getTable("TABLE").getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		TTable table = getTable("TABLE");
		table.setSelectionMode(0);
		action = "save";
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		TTable table = getTable("TABLE");
		int row = table.getSelectedRow();
		if (row != -1) {
			((TMenuItem) getComponent("delete")).setEnabled(true);
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "REASON_CODE;REASON_TYPE;REASON_CHN_DESC;REASON_ENG_DESC;"
					+ "PY1;PY2;SEQ;DESCRIPTION";
			this.setValueForParm(likeNames, parm);
			getTextField("REASON_CODE").setEnabled(false);
			action = "save";
		}
	}

	/**
	 * ԭ��˵���س��¼�
	 */
	public void onReasonDescAction() {
		String name = getValueString("REASON_CHN_DESC");
		if (name.length() > 0)
			setValue("PY1", TMessage.getPy(name));
		((TTextField) getComponent("REASON_ENG_DESC")).grabFocus();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		TTable table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(INDSQL.getReason());
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
		table.setDSValue();

		// ����+1(SEQ)
		int seq = getMaxSeq(dataStroe, "SEQ",
				dataStroe.isFilter() ? dataStroe.FILTER : dataStroe.PRIMARY);
		setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		String reason_code = getValueString("REASON_CODE");
		if ("".equals(reason_code)) {
			this.messageBox("ԭ����벻��Ϊ��");
			return false;
		}
		String reason_type = getValueString("REASON_TYPE");
		if ("".equals(reason_type)) {
			this.messageBox("ԭ�������Ϊ��");
			return false;
		}
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(INDSQL.getReason(reason_code));
		dataStore.retrieve();
		if (dataStore.rowCount() > 0) {
			this.messageBox("ԭ������ظ����޷�����");
			return false;
		}
		return true;
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
	 * �õ����ı�� +1
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	private int getMaxSeq(TDataStore dataStore, String columnName,
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
