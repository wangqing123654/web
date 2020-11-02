package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

public class SYSOperatorNewControl extends TControl {

	private static final String USER_NAME = "USER_NAME";
	private static final String USER_ID = "USER_ID";
	private static final String DEPT_CODE = "DEPT_CODE";
	private static final String REGION_CODE = "REGION_CODE";
	private static final String TABLE = "TABLE";
	private static final String TABLEDEPT = "TABLEDEPT";
	private static final String TABLELISCENSE = "TABLELISCENSE";
	private static final String POS_CODE = "POS_CODE";
	private static final String ROLE_ID = "ROLE_ID";
	private static int selectRow = -1;

	public SYSOperatorNewControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��TABLEDEPT��������¼�
		callFunction("UI|TABLEDEPT|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableDeptCheckBoxClicked");
		addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (getValueString(DEPT_CODE).length() == 0) {
			onFilter();
			return;
		}
		onComboBoxSelected();
	}

	/**
	 * ��������
	 */
	public void onFilter() {
		String value = "";
		String str = getValueString(USER_NAME);
		if (str.length() != 0)
			value += "USER_NAME like '" + str + "%'";
		str = getValueString(USER_ID);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "USER_ID like '" + str + "%'";
		}
		str = getValueString(REGION_CODE);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "REGION_CODE = '" + str + "'";
		}
		str = getValueString(POS_CODE);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "POS_CODE = '" + str + "'";
		}
		str = getValueString(ROLE_ID);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "ROLE_ID = '" + str + "'";
		}
		TTable table1 = getTable(TABLE);
		if (value.length() > 0) {
			table1.setFilter(value);
			table1.filter();
			return;
		}
		table1.setFilter("");
		table1.filter();
	}

	/**
	 * ����ComboBoxѡ���¼�
	 */
	public void onComboBoxSelected() {
		String dept = getValueString(DEPT_CODE);
		TDataStore st = new TDataStore();
		String sql = "SELECT USER_ID,DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE DEPT_CODE = '"
				+ dept + "'";
		st.setSQL(sql);
		st.retrieve();
		int count = st.rowCount();
		String sql2 = "SELECT * FROM SYS_OPERATOR " + "WHERE USER_ID IN ("
				+ "'" + st.getItemData(0, USER_ID) + "'";
		for (int i = 1; i < count; i++) {
			sql2 = sql2 + ",'" + st.getItemData(i, USER_ID) + "'";
		}
		sql2 += ")";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql2);
		dataStroe.retrieve();
		TTable table1 = getTable(TABLE);
		table1.setDataStore(dataStroe);
		table1.setDSValue();
		onFilter();
	}

	/**
	 * �������
	 */
	public void onClear() {
		setValue(USER_NAME, "");
		setValue(USER_ID, "");
		setValue(DEPT_CODE, "");
		setValue(REGION_CODE, "");
		setValue("POS_CODE", "");
		setValue("ROLE_ID", "");
		setValue("ACTIVE_DATE", "");
		setValue("END_DATE", "");
		setValue("RCNT_LOGIN_DATE", "");
		setValue("RCNT_LOGOUT_DATE", "");
		setValue("RCNT_IP", "");
		setValue("PUB_FUNCTION", "");
		setValue("LCS_CLASS_CODE", "");
		setValue("LCS_NO", "");
		setValue("EFF_LCS_DATE", "");
		setValue("END_LCS_DATE", "");
		// ��տ�����Ϣ��
		getTable(TABLEDEPT).removeRowAll();
		// ���֤����Ϣ��
		getTable(TABLELISCENSE).removeRowAll();
		// ��տ����б�
		getTable("TABLE2").removeRowAll();
		onFilter();
	}

	/**
	 * ���淽��
	 * 
	 * @return
	 */
	public boolean onSave() {
		// �������
		TTable table = getTable(TABLEDEPT);
		TTable table1 = getTable(TABLE);
		Timestamp date = StringTool.getTimestamp(new Date());
		if (table.getDataStore().isModified()) {
			String userId = (String) table1.getItemData(selectRow, "USER_ID");
			table.acceptText();
			TDataStore dataStore = table.getDataStore();
			// ���ȫ���Ķ����к�
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "USER_ID", userId);
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table.update()) {
				messageBox("E0001");
				return false;
			}
			messageBox("P0001");
			table.setDSValue();
			return true;
		}
		// ����ʹ����
		if (table1.getDataStore().isModified()) {
			this.messageBox("ʹ����");
			table1.acceptText();
			TDataStore dataStore = table1.getDataStore();
			// ���ȫ���Ķ����к�
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				/////////////////////////////////////////////////////
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table1.update()) {
				messageBox("E0001");
				return false;
			}
			messageBox("P0001");
			table1.setDSValue();
			return true;
		}
		return true;
	}

	/**
	 * Table1�����¼�
	 */
	public void onTable1Clicked() {
		// ���֤����Ϣ
		setValue("LCS_CLASS_CODE", "");
		setValue("LCS_NO", "");
		setValue("EFF_LCS_DATE", "");
		setValue("END_LCS_DATE", "");
		// �õ���ѡ����
		TParm parm = getTable(TABLE).getDataStore().getRowParm(
				getTable(TABLE).getSelectedRow());
		String userId = parm.getValue(USER_ID);
		// ����TABLE��ȷ���Ƿ񱣴�
		if (getTable(TABLEDEPT).getDataStore().isModified()) {
			switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", YES_NO_OPTION)) {
			case 0:
				if (!this.onSave())
					// ���ı�ѡ����
					selectRow = getTable(TABLE).getSelectedRow();
				break;
			case 1:
				break;
			}
		}
		// ���ı�ѡ����
		selectRow = getTable(TABLE).getSelectedRow();
		// ��ѯָ���û�����������
		TDataStore dataStroe1 = new TDataStore();
		String sql1 = "SELECT * FROM SYS_OPERATOR_DEPT WHERE USER_ID = '"
				+ userId + "'";
		dataStroe1.setSQL(sql1);
		dataStroe1.retrieve();
		TTable table = getTable(TABLEDEPT);
		table.setDataStore(dataStroe1);
		table.setDSValue();
		dataStroe1.showDebug();
		// ��ѯָ���û���֤����Ϣ
		TDataStore dataStroe2 = new TDataStore();
		String sql2 = "SELECT * FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
				+ userId + "'";
		dataStroe2.setSQL(sql2);
		dataStroe2.retrieve();
		TTable table2 = getTable(TABLELISCENSE);
		table2.setDataStore(dataStroe2);
		table2.setDSValue();
		// ��ʾ������Ϣ
		setValue("USER_ID", parm.getData("USER_ID"));
		setValue("USER_NAME", parm.getData("USER_NAME"));
		// ��������Ҽ���������
		String main = "";
		String dept = "";
		for (int i = 0; i < dataStroe1.rowCount(); i++) {
			if (dataStroe1.getRowParm(i).getBoolean("MAIN_FLG"))
				main = dataStroe1.getRowParm(i).getValue("DEPT_CODE");
			dept += "'" + dataStroe1.getRowParm(i).getValue("DEPT_CODE") + "',";
		}
		setValue("DEPT_CODE", main);
		setValue("REGION_CODE", parm.getData("REGION_CODE"));
		// ��ʾ�����б�
		String sql3 = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT";
		if (dept.length() > 0) {
			dept = dept.substring(0, dept.length() - 1);
			sql3 += " WHERE DEPT_CODE NOT IN ( " + dept + " )";
		}
		TDataStore dataStroe3 = new TDataStore();
		dataStroe3.setSQL(sql3);
		dataStroe3.retrieve();
		TTable table3 = getTable("TABLE2");
		table3.setDataStore(dataStroe3);
		table3.setDSValue();
		// ��ʾȨ�޷�����Ϣ
		setValue("POS_CODE", parm.getData("POS_CODE"));
		setValue("ROLE_ID", parm.getData("ROLE_ID"));
		setValue("ACTIVE_DATE", parm.getData("ACTIVE_DATE"));
		setValue("END_DATE", parm.getData("END_DATE"));
		setValue("RCNT_LOGIN_DATE", parm.getData("RCNT_LOGIN_DATE"));
		setValue("RCNT_LOGOUT_DATE", parm.getData("RCNT_LOGOUT_DATE"));
		setValue("RCNT_IP", parm.getValue("RCNT_IP"));
		setValue("PUB_FUNCTION", parm.getValue("PUB_FUNCTION"));
	}

	/**
	 * TableLiscense�����¼�
	 */
	public void onTableLiscenseClicked() {
		TParm parm = getTable(TABLELISCENSE).getDataStore().getRowParm(
				getTable(TABLELISCENSE).getSelectedRow());
		setValue("LCS_CLASS_CODE", parm.getData("LCS_CLASS_CODE"));
		setValue("LCS_NO", parm.getData("LCS_NO"));
		setValue("EFF_LCS_DATE", parm.getData("EFF_LCS_DATE"));
		setValue("END_LCS_DATE", parm.getData("END_LCS_DATE"));
	}

	/**
	 * ����INSERT_DEPT��ť
	 */
	public void onInsertDeptClicked() {
		if (getTable("TABLE2").getSelectedRow() == -1)
			return;
		int newRow = getTable(TABLEDEPT).addRow();
		String data = getTable("TABLE2").getDataStore().getItemString(
				getTable("TABLE2").getSelectedRow(), "DEPT_CODE");
		String userId = getTable("TABLE2").getDataStore().getItemString(
				getTable("TABLE2").getSelectedRow(), "USER_ID");
		getTable(TABLEDEPT).setItem(newRow, "MAIN_FLG", "N");
		getTable(TABLEDEPT).setItem(newRow, "DEPT_CODE", data);
		getTable(TABLEDEPT).setItem(newRow, "USER_ID", userId);
		getTable("TABLE2").removeRow(getTable("TABLE2").getSelectedRow());
	}

	/**
	 * ����REMOVE_DEPT��ť
	 */
	public void onRemoveDeptClicked() {
		if (getTable(TABLEDEPT).getSelectedRow() == -1)
			return;
		int newRow = getTable("TABLE2").addRow();
		String data = getTable(TABLEDEPT).getDataStore().getItemString(
				getTable(TABLEDEPT).getSelectedRow(), "DEPT_CODE");
		getTable("TABLE2").setItem(newRow, "DEPT_CODE", data);
		getTable("TABLE2").setItem(newRow, "DEPT_CHN_DESC", data);
		getTable(TABLEDEPT).removeRow(getTable(TABLEDEPT).getSelectedRow());
	}

	/**
	 * TABLEDEPT��ѡ��ı��¼�
	 */
	public void onTableDeptCheckBoxClicked(Object obj) {
		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���
		int row = tableDown.getSelectedRow();
		// ȡ������ѡ��
		for (int i = 0; i < tableDown.getRowCount(); i++) {
			tableDown.setValueAt(false, i, 0);
		}
		// ѡ��ѡ����
		tableDown.setValueAt(true, row, 0);
	}

	/**
	 * ���һ���µ������¼�
	 */
	public void onNew() {
		String userId = getValueString(USER_ID).trim();
		String userName = getValueString(USER_NAME).trim();
		if (userId.length() == 0) {
			this.messageBox("��������ʹ����");
			return;
		}
		TTable table = getTable(TABLE);
		if (table.getDataStore().exist("USER_ID='" + userId + "'")) {
			this.messageBox("ʹ�����Ѵ���");
			return;
		}
		if (userName.length() == 0) {
			this.messageBox("������������");
			return;
		}
		// this.onClear();
		int row = table.addRow();
		table.setItem(row, "USER_ID", userId);
		table.setItem(row, "USER_NAME", userName);
		// ��ƴ��
		String py = TMessage.getPy(userName);
		table.setItem(row, "PY1", py);
		table.setItem(row, "OPT_USER", Operator.getID());
		table.setItem(row, "OPT_DATE", StringTool.getTimestamp(new Date()));
		table.setItem(row, "OPT_TERM", Operator.getIP());
	}

	/**
	 * ����µ�֤��
	 */
	public void onInsertLiscenseClicked() {
		if (getTable(TABLE).getSelectedRow() == -1) {
			this.messageBox("��ѡ��ʹ����");
			return;
		}
		String code = getValueString("LCS_CLASS_CODE").trim();
		TTable table = getTable(TABLELISCENSE);
		if (table.getDataStore().exist("LCS_CLASS_CODE='" + code + "'")) {
			this.messageBox("֤���Ѵ���");
			return;
		}
		if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
			this.messageBox("֤�������Ϊ��");
			return;
		}
		if (getValueString("LCS_NO").trim().length() == 0) {
			this.messageBox("֤�պ��벻��Ϊ��");
			return;
		}
		Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
		if (begin == null) {
			this.messageBox("֤�����ղ���Ϊ��");
			return;
		}
		Timestamp end = (Timestamp) getValue("END_LCS_DATE");
		if (end == null) {
			this.messageBox("֤�����ղ���Ϊ��");
			return;
		}
		if (end.compareTo(begin) <= 0) {
			this.messageBox("֤�����ղ��ܴ���֤������");
			return;
		}
		int row = table.addRow();
		table.setItem(row, "LCS_CLASS_CODE", code);
		table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
		table.setItem(row, "EFF_LCS_DATE", begin);
		table.setItem(row, "END_LCS_DATE", end);
	}

	/**
	 * �Ƴ�֤��
	 */
	public void onRemoveLiscenseClicked() {
		TTable table = getTable(TABLELISCENSE);
		int row = table.getSelectedRow();
		if (row == -1) {
			this.messageBox("��ѡ��֤��");
			return;
		}
		table.removeRow(row);
	}

	/**
	 * ����֤��
	 */
	public void onUpdateLiscenseAction() {
		TTable table = getTable(TABLELISCENSE);
		int row = table.getSelectedRow();
		String oldvalue = (String) table.getItemData(row, "LCS_CLASS_CODE");
		String value = getValueString("LCS_CLASS_CODE").trim();
		if (!oldvalue.equals(value)) {
			if (table.getDataStore().exist("LCS_CLASS_CODE='" + value + "'")) {
				this.messageBox("֤���Ѵ���");
				return;
			}
			table.setItem(row, "LCS_CLASS_CODE", value);
		}
		oldvalue = (String) table.getItemData(row, "LCS_NO");
		value = getValueString("LCS_NO").trim();
		if (!oldvalue.equals(value)) {
			table.setItem(row, "LCS_NO", value);
		}
		Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
		Timestamp end = (Timestamp) getValue("END_LCS_DATE");
		if (end.compareTo(begin) <= 0) {
			this.messageBox("֤�����ղ��ܴ���֤������");
			return;
		}
		table.setItem(row, "EFF_LCS_DATE", begin);
		table.setItem(row, "END_LCS_DATE", end);
	}

	/**
	 * ����֤��
	 */
	public boolean onSaveLiscenseClicked() {
		TTable table1 = getTable(TABLE);
		String userId = (String) table1.getItemData(table1.getSelectedRow(),
				"USER_ID");
		Timestamp date = StringTool.getTimestamp(new Date());
		TTable table = getTable(TABLELISCENSE);
		table.acceptText();
		TDataStore dataStore = table.getDataStore();
		// ���ȫ���Ķ����к�
		int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
		for (int i = 0; i < rows.length; i++) {
			dataStore.setItem(rows[i], "USER_ID", userId);
			dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
			dataStore.setItem(rows[i], "OPT_DATE", date);
			dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
		}

		if (!table.update()) {
			messageBox("E0001");
			return false;
		}
		messageBox("P0001");
		table.setDSValue();
		return true;
	}

	// /**
	// * ��ʾ���п�����Ϣ
	// */
	// public void showDeptAll() {
	// String sql = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT";
	// TDataStore dataStroe = new TDataStore();
	// dataStroe.setSQL(sql);
	// dataStroe.retrieve();
	// TTable table = getTable("TABLE2");
	// table.setDataStore(dataStroe);
	// table.setDSValue();
	// }

	/**
	 * TABLEֵ�ı��¼�
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onTableChangeValue(Object obj) {
		// �õ�tableֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			this.messageBox(node.getValue() + ":" + node.getOldValue());
			return true;
		}
		// �õ�table�ϵ�����
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// �õ���ǰ�ı�������
		String value = "" + node.getValue();
		int row = node.getRow();
		// ���֤��
		if ("ID_NO".equals(columnName)) {
			// ���֤����֤
			if (StringTool.isId(value))
				node.getTable().setItem(row, "SEX_CODE",
						StringTool.isMaleFromID(value));
		}
		return false;
	}

	/**
	 * �õ�ҳ����Table����
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

}
