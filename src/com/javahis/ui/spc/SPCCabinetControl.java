package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:���ܹ������Ϣά��
 * </p>
 * 
 * <p>
 * Description:���ܹ������Ϣά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */

public class SPCCabinetControl extends TControl {

	private String action = "insertM";

	private TTable TABLE_Z;
	private TTable TABLE_M;
	private TTable TABLE_L;
	private TPanel tPanel_Z;
	private TPanel tPanel_L;

	public SPCCabinetControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		TABLE_Z = getTable("TABLE_Z");
		TABLE_M = getTable("TABLE_M");
		TABLE_L = getTable("TABLE_L");
		this.addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
				"onROOMTABLEChargeValue");
		initPage();
	}

	/**
	 * ��ʼ������
	 */
	private void initPage() {
		TABLE_Z.removeRowAll();
		TParm parm = new TParm();
		TParm result = SPCCabinetTool.getInstance().queryInfo(parm);
		TABLE_Z.setParmValue(result);
		callFunction("UI|DELETE|setEnabled", false);
		setValue("REGION_CODE", Operator.getRegion());
		action = "insertM";
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		setValue("CABINET_ID", "");
		setValue("CABINET_IP", "");
		setValue("CABINET_DESC", "");
		setValue("DESCRIPTION", "");
		setValue("START_DATE", "");
		setValue("END_DATE", "");
		setValue("ORG_CODE", "");
		setValue("GUARD_IP", "");
		setValue("RFID_IP", "");
		// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
		setValue("ZKRFID_URL", "");
		setValue("MQ_DESC", "");
		setValue("CABINET_TYPE", "");
		getTCheckBox("ACTIVE_FLG").setSelected(false);
		// -->
		TABLE_Z.removeRowAll();
		TABLE_M.removeRowAll();
		callFunction("UI|DELETE|setEnabled", false);
		getTextField("CABINET_CODE").setEnabled(true);
		action = "insertM";
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		tPanel_L = (TPanel) getComponent("tPanel_L");
		TParm parm = new TParm();
		String cabId = this.getValueString("CABINET_ID");
		String cabName = this.getValueString("CABINET_DESC");
		parm.setData("CABINET_ID", cabId);
		parm.setData("CABINET_DESC", cabName);
		TParm result;
		if (tPanel_Z.isShowing()) {
			TParm tparm = new TParm();
			if (!"".equals(this.getValueString("CABINET_ID"))) {
				tparm.setData("CABINET_ID", this.getValueString("CABINET_ID"));
			}
			if (!"".equals(this.getValueString("CABINET_DESC"))) {
				tparm.setData("CABINET_DESC", this
						.getValueString("CABINET_DESC"));
			}
			result = SPCCabinetTool.getInstance().queryInfo(tparm);
			TABLE_Z.setParmValue(result);
			if (result.getCount() <= 0) {
				this.messageBox("û�в�ѯ������");
				return;
			}
		} else if (tPanel_L.isShowing()) {
			TParm tparm = new TParm();
			if ((this.getValue("START_DATE") != null)
					&& (this.getValue("END_DATE") != null)) {
				String startDate = this.getValue("START_DATE").toString()
						.substring(0, 10).replace('-', '/')
						+ " 00:00:00";
				String endDate = this.getValue("END_DATE").toString()
						.substring(0, 10).replace('-', '/')
						+ " 23:59:59";
				tparm.setData("START_DATE", startDate);
				tparm.setData("END_DATE", endDate);
			}
			if (!"".equals(this.getValueString("TASK_TYPE"))) {
				tparm.setData("TASK_TYPE", this.getValueString("TASK_TYPE"));
			}
			if (!"".equals(this.getValueString("EVENT_TYPE"))) {
				tparm.setData("EVENT_TYPE", this.getValueString("EVENT_TYPE"));
			}
			result = SPCCabinetTool.getInstance().queryLog(tparm);
			TABLE_L.setParmValue(result);
			if (result.getCount() <= 0) {
				this.messageBox("û�в�ѯ������");
				return;
			}
		}
		action = "insertM";
	}

	/**
	 * TABLE�����¼�
	 */
	// public void onTableClicked() {
	// int row = TABLE_Z.getSelectedRow();
	// if (row != -1) {
	// TParm parm = TABLE_Z.getParmValue();
	// this.setValue("CABINET_CODE", parm.getData("CABINET_CODE", row));
	// this.setValue("CABINET_IP", parm.getData("CABINET_IP", row));
	// this.setValue("CABINET_DESC", parm.getData("CABINET_DESC", row));
	// this.setValue("DESCRIPTION", parm.getData("DESCRIPTION", row));
	// callFunction("UI|DELETE|setEnabled", true);
	// getTextField("CABINET_CODE").setEnabled(false);
	// action = "save";
	// }
	// }

	/**
	 * ���淽��
	 */
	public void onSave() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		if (tPanel_Z.isShowing()) {
			TParm result;
			if ("insertM".equals(action)) {
				if (!CheckData()) {
					return;
				}
				TParm parm = new TParm();
				Timestamp date = StringTool.getTimestamp(new Date());
				parm.setData("CABINET_ID", getValueString("CABINET_ID"));
				parm.setData("CABINET_DESC", getValueString("CABINET_DESC"));
				parm.setData("CABINET_IP", getValueString("CABINET_IP"));
				parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
				parm.setData("ORG_CODE", getValueString("ORG_CODE"));
				parm.setData("GUARD_IP", getValueString("GUARD_IP"));
				parm.setData("RFID_IP", getValueString("RFID_IP"));
				parm.setData("REGION_CODE", "H01");
				parm.setData("OPT_USER", Operator.getID());
				parm.setData("OPT_DATE", date);
				parm.setData("OPT_TERM", Operator.getIP());
				// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
				parm.setData("ZKRFID_URL", getValueString("ZKRFID_URL"));
				parm.setData("MQ_DESC", getValueString("MQ_DESC"));
				parm.setData("CABINET_TYPE", getValueString("CABINET_TYPE"));
				parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
				// -->
				result = SPCCabinetTool.getInstance().insertInfo(parm);
				this.messageBox("����ɹ�");
				setValue("DESCRIPTION", "");
				setValue("CABINET_ID", "");
				setValue("CABINET_CODE", "");
				setValue("CABINET_IP", "");
				setValue("CABINET_DESC", "");
				setValue("ORG_CODE", "");
				setValue("GUARD_IP", "");
				setValue("RFID_IP", "");
				// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
				setValue("ZKRFID_URL", "");
				setValue("MQ_DESC", "");
				setValue("CABINET_TYPE", "");
				getTCheckBox("ACTIVE_FLG").setSelected(false);
				// -->
				onQuery();
				return;
			} else if ("updateM".equals(action)) {
				if (!CheckData()) {
					return;
				}
				TParm parm = new TParm();
				Timestamp date = StringTool.getTimestamp(new Date());
				parm.setData("CABINET_ID", getValueString("CABINET_ID"));
				parm.setData("CABINET_DESC", getValueString("CABINET_DESC"));
				parm.setData("CABINET_IP", getValueString("CABINET_IP"));
				parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
				parm.setData("ORG_CODE", getValueString("ORG_CODE"));
				parm.setData("GUARD_IP", getValueString("GUARD_IP"));
				parm.setData("RFID_IP", getValueString("RFID_IP"));
				parm.setData("OPT_USER", Operator.getID());
				parm.setData("OPT_DATE", date);
				parm.setData("OPT_TERM", Operator.getIP());
				// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
				parm.setData("ZKRFID_URL", getValueString("ZKRFID_URL"));
				parm.setData("MQ_DESC", getValueString("MQ_DESC"));
				parm.setData("CABINET_TYPE", getValueString("CABINET_TYPE"));
				parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
				// -->
				result = SPCCabinetTool.getInstance().updateInfo(parm);
				this.messageBox("���³ɹ�");
				TABLE_M.removeRowAll();
				setValue("DESCRIPTION", "");
				setValue("CABINET_ID", "");
				setValue("CABINET_CODE", "");
				setValue("CABINET_IP", "");
				setValue("CABINET_DESC", "");
				setValue("ORG_CODE", "");
				setValue("GUARD_IP", "");
				setValue("RFID_IP", "");
				// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
				setValue("ZKRFID_URL", "");
				setValue("MQ_DESC", "");
				setValue("CABINET_TYPE", "");
				getTCheckBox("ACTIVE_FLG").setSelected(false);
				// -->
				onQuery();
				return;
			} else {
				if (!CheckDataM()) {
					return;
				}
				TTable table_M = getTable("TABLE_M");
				table_M.acceptText();
				TDataStore dataStore = table_M.getDataStore();
				// ���ȫ���Ķ����к�
				int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
				// ���ȫ����������
				int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
				// ���̶�����������
				Timestamp date = StringTool.getTimestamp(new Date());
				for (int i = 0; i < newrows.length; i++) {
					dataStore.setItem(newrows[i], "CABINET_ID",
							getValueString("CABINET_ID"));
					dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
					dataStore.setItem(newrows[i], "OPT_DATE", date);
					dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
				}
				TParm inParm = new TParm();
				String guardId = "";
				String guardDesc = "";
				String type = "";
				int j = 0;
				for (int i = 0; i < rows.length - newrows.length; i++) {
					j++;
					guardId = dataStore.getItemString(rows[i], "GUARD_ID");
					guardDesc = dataStore.getItemString(rows[i], "GUARD_DESC");
					type = dataStore.getItemString(rows[i], "IS_TOXIC_GUARD");
					inParm.setData("GUARD_ID", i, guardId);
					inParm.setData("GUARD_DESC", i, guardDesc);
					inParm.setData("IS_TOXIC_GUARD", i, type);
					inParm.setData("OPT_USER", i, Operator.getID());
					inParm.setData("OPT_TERM", i, Operator.getIP());
					inParm.setData("NUM", j);
				}
				inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
				result = TIOM_AppServer.executeAction(
						"action.spc.SPCCabinetAction", "onSaveCabinet", inParm);
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("E0001");
					return;
				}
				this.messageBox("����ɹ�");
				return;
			}
		}
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		if (tPanel_Z.isShowing()) {
			if (TABLE_M.getSelectedRow() == -1
					&& TABLE_Z.getSelectedRow() == -1) {
				this.messageBox("��ѡ��Ҫɾ������");
				return;
			}
			if (TABLE_M.getSelectedRow() == -1
					&& TABLE_Z.getSelectedRow() != -1) {
				int row = TABLE_Z.getSelectedRow();
				String code = TABLE_Z.getItemString(row, "CABINET_ID");
				TParm parm = new TParm();
				parm.setData("CABINET_ID", code);
				TParm result = SPCCabinetTool.getInstance().queryChild(parm);
				if (result.getCount() > 0) {
					this.messageBox("����ɾ���Ž���Ϣ");
					return;
				}
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����ܹ�", 2) == 0) {
					result = SPCCabinetTool.getInstance().deleteInfo(parm);
					this.messageBox("ɾ���ɹ�");
					TABLE_M.removeRowAll();
					setValue("DESCRIPTION", "");
					setValue("CABINET_ID", "");
					setValue("CABINET_CODE", "");
					setValue("CABINET_IP", "");
					setValue("CABINET_DESC", "");
					setValue("ORG_CODE", "");
					setValue("GUARD_IP", "");
					setValue("RFID_IP", "");
					// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
					setValue("ZKRFID_URL", "");
					setValue("MQ_DESC", "");
					setValue("CABINET_TYPE", "");
					getTCheckBox("ACTIVE_FLG").setSelected(false);
					// -->
					onQuery();
					return;
				}
			}
			if (TABLE_M.getSelectedRow() != -1
					&& TABLE_Z.getSelectedRow() == -1) {
				TParm parm = new TParm();
				int row = TABLE_M.getSelectedRow();
				String code = TABLE_M.getItemString(row, "GUARD_ID");
				parm.setData("GUARD_ID", code);
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ���Ž�", 2) == 0) {
					TParm result = SPCCabinetTool.getInstance().deleteChild(
							parm);
					this.messageBox("ɾ���ɹ�");
					TABLE_M = getTable("TABLE_M");
					TABLE_M.removeRowAll();
					TABLE_M.setSelectionMode(0);
					TDS tds = new TDS();
					tds.setSQL(SPCSQL
							.getCabinetGuard(getValueString("CABINET_ID")));
					tds.retrieve();
					TABLE_M.setDataStore(tds);
					TABLE_M.setDSValue();
					onAddRow();
				}
			}
		}
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
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getTCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("CABINET_ID"))) {
			this.messageBox("���ܹ��Ų���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("CABINET_IP"))) {
			this.messageBox("���ܹ�IP����Ϊ��");
			return false;
		}
		if ("".equals(getValueString("CABINET_DESC"))) {
			this.messageBox("���ܹ����Ʋ���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("�������Ų���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * �������¼�
	 */
	public void onTableMClicked() {
		int row = TABLE_Z.getSelectedRow();
		if (row != -1) {
			setValue("CABINET_ID", TABLE_Z.getItemString(row, "CABINET_ID"));
			setValue("CABINET_DESC", TABLE_Z.getItemString(row, "CABINET_DESC"));
			setValue("CABINET_IP", TABLE_Z.getItemString(row, "CABINET_IP"));
			setValue("DESCRIPTION", TABLE_Z.getItemString(row, "DESCRIPTION"));
			setValue("ORG_CODE", TABLE_Z.getItemString(row, "ORG_CODE"));
			setValue("RFID_IP", TABLE_Z.getItemString(row, "RFID_IP"));
			setValue("GUARD_IP", TABLE_Z.getItemString(row, "GUARD_IP"));
			// <-- �����ĸ��ֶε�ά�� update by shendr date:2013.5.24
			this.setValue("ZKRFID_URL", TABLE_Z
					.getItemString(row, "ZKRFID_URL"));
			this.setValue("MQ_DESC", TABLE_Z.getItemString(row, "MQ_DESC"));
			this.setValue("CABINET_TYPE", TABLE_Z.getItemString(row,
					"CABINET_TYPE"));
			if ("Y".equals(TABLE_Z.getItemString(row, "ACTIVE_FLG"))) {
				getTCheckBox("ACTIVE_FLG").setSelected(true);
			} else if ("N".equals(TABLE_Z.getItemString(row, "ACTIVE_FLG"))) {
				getTCheckBox("ACTIVE_FLG").setSelected(false);
			}
			// -->
			callFunction("UI|DELETE|setEnabled", true);
			action = "updateM";
			// �Ž���Ϣ
			TABLE_M = getTable("TABLE_M");
			TABLE_M.removeRowAll();
			TABLE_M.setSelectionMode(0);
			TDS tds = new TDS();
			tds.setSQL(SPCSQL.getCabinetGuard(getValueString("CABINET_ID")));
			tds.retrieve();
			TABLE_M.setDataStore(tds);
			TABLE_M.setDSValue();
			onAddRow();
		}
	}

	/**
	 * ���һ��ϸ��
	 */
	private void onAddRow() {
		// ��δ�༭��ʱ����
		if (!this.isNewRow())
			return;
		TTable table_M = getTable("TABLE_M");
		int row = table_M.addRow();
		table_M.getDataStore().setActive(row, false);
	}

	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean falg = false;
		TParm parmBuff = getTable("TABLE_M").getDataStore().getBuffer(
				getTable("TABLE_M").getDataStore().PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			falg = true;
		}
		return falg;
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		TTable table_M = getTable("TABLE_M");
		table_M.acceptText();
		for (int i = 0; i < table_M.getRowCount(); i++) {
			if (!table_M.getDataStore().isActive(i)) {
				continue;
			}
			if ("".equals(table_M.getItemString(i, "GUARD_ID"))) {
				this.messageBox("�Ž���Ų���Ϊ��");
				return false;
			}
			if ("".equals(table_M.getItemString(i, "GUARD_DESC"))) {
				this.messageBox("�Ž����Ʋ���Ϊ��");
				return false;
			}
		}
		return true;
	}

	public void onTableDClicked() {
		TTable table = getTable("TABLE_M");
		int row = table.getSelectedRow();
		if (row != -1) {
			action = "updateD";
			// ������Ϣ
			TTable table_Z = getTable("TABLE_Z");
			table_Z.setSelectionMode(0);
		}
	}

	/**
	 * ����tableֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onROOMTABLEChargeValue(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// �õ�table�ϵ�parmmap������
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �õ�table
		TTable table = node.getTable();
		// �õ��ı����
		int row = node.getRow();
		// �õ���ǰ�ı�������
		String value = "" + node.getValue();
		// table.setItem(row, "CABINET_ID", "111");
		// ��������Ƹı���ƴ��1�Զ�����,���ҿ������Ʋ���Ϊ��SD
		TParm parm = new TParm();
		if ("GUARD_ID".equals(columnName)) {
			parm.setData("GUARD_ID", (String) node.getOldValue());
			parm.setData("CABINET_ID", this.getValueString("CABINET_ID"));
			TParm result = SPCCabinetTool.getInstance().queryCode(parm);
			if (result.getCount() > 0) {
				this.messageBox("�Ž���Ų����ظ�");
				return true;
			}
			table.getDataStore().setActive(row, true);
		}
		return false;
	}

	/**
	 * ��table˫���¼�
	 */
	public void onTableMDClicked() {
		tPanel_L = (TPanel) getComponent("tPanel_L");
		TTabbedPane aPane = (TTabbedPane) getComponent("tTabbedPane_0");
		aPane.setSelectedIndex(1);
		if (tPanel_L.isShowing()) {
			TParm parm = new TParm();
			TParm result = SPCCabinetTool.getInstance().queryLog(parm);
			if (result.getCount() <= 0) {
				this.messageBox("û�в�ѯ������");
				return;
			}
			TABLE_L.setParmValue(result);
		}
	}

}
