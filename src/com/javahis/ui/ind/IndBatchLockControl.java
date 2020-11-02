package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title:ҩ�����ν��� </p>
 *
 * <p>Description:ҩ�����ν��� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangy 2009.4.22
 * @version 1.0
 */

public class IndBatchLockControl extends TControl {

	public IndBatchLockControl() {
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
	 * ���ν���
	 */
	public void onUnLock() {
		TTable table = getTable("TABLE");
		table.acceptText();
		if (table.getDataStore().rowCount() == 0) {
			this.messageBox("û����Ҫ�����Ĳ���");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		Timestamp date = StringTool.getTimestamp(new Date());
		for (int i = 0; i < table.getRowCount(); i++) {
			if ("Y".equals(table.getItemString(i, 0))) {
				dataStore.setItem(i, "BATCH_FLG", "N");
				dataStore.setItem(i, "OPT_USER", Operator.getID());
				dataStore.setItem(i, "OPT_DATE", date);
				dataStore.setItem(i, "OPT_TERM", Operator.getIP());
			} else {
				dataStore.setItem(i, "BATCH_FLG", "Y");
			}
		}
		if (!table.update()) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("�����ɹ�");
		initPage();
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		TTable table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(INDSQL.getINDBatchLockORG("Y"));
		dataStroe.retrieve();
		if (dataStroe.rowCount() == 0) {
			this.messageBox("û����Ҫ�����Ĳ���");
			return;
		}
		table.setDataStore(dataStroe);
		table.setDSValue();
	}

	/**
	 * ȫѡ(CheckBox)�ı��¼�
	 */
	public void onSelectAll() {
		TTable table = getTable("TABLE");
		if (table.getDataStore().rowCount() == 0)
			return;
		String flg = getCheckBox("SELECT_ALL").getValue();
		if ("Y".equals(flg)) {
			for (int i = 0; i < table.getDataStore().rowCount(); i++) {
				table.getDataStore().setItem(i, "BATCH_FLG", "Y");
			}
		} else {
			for (int i = 0; i < table.getDataStore().rowCount(); i++) {
				table.getDataStore().setItem(i, "BATCH_FLG", "N");
			}
		}
		table.acceptText();
		table.setDSValue();
	}

	/**
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
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
}
