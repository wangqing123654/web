package com.javahis.ui.inv;

import com.dongyang.ui.event.TPopupMenuEvent;

import jdo.inv.INVOrgTool;
import jdo.inv.INVSQL;
import com.dongyang.data.TNull;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import jdo.inv.INVPackMTool;
import com.dongyang.control.TControl;
import jdo.inv.INVPackStockMTool;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.inv.INVPublicTool;
import com.dongyang.ui.TTableNode;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.manager.INVPackOberver;

/**
 * <p>
 * Title:������
 * </p>
 * 
 * <p>
 * Description:������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw
 * @version 1.0
 */
public class INVSterilizationControl extends TControl {
	/**
	 * ������
	 */
	private TTable tableM;
	/**
	 * ������ϸ
	 */
	private TTable tableD;
	/**
	 * ��¼�����ȱ�ѡ�е���
	 */
	private String initStatus = "";
	/**
	 * ����Ч��
	 */
	private int valueDate = 0;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// �����������
		tableM = (TTable) getComponent("TABLEM");
		// ���������ϸ
		tableD = (TTable) getComponent("TABLED");
		// tableDֵ�ı��¼�
		this.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableDChargeValue");
		// ���ʵ�������
		callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
				"%ROOT%\\config\\inv\\INVChoose.x");
		// ���ܻش�ֵ
		callFunction("UI|PACK_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		iniAppOrgCombo();
		initAppOrgCode();
		// ��ʼ��table
		onInitTable();
		// ��ӹ۲���
		observer();
	}
	/**
	 * ��ʼ����Ӧ����
	 */
	public void iniAppOrgCombo() {
		// ��ѯ����
		TParm parm = INVOrgTool.getInstance().getDept();
		TComboBox comboBox = (TComboBox) this.getComponent("ORG_CODE");
		comboBox.setParmValue(parm);
		comboBox.updateUI();
		// ��������
		comboBox = new TComboBox();
		comboBox.setParmMap("id:ORG_CODE;name:DEPT_CHN_DESC");
		comboBox.setParmValue(parm);
		comboBox.setShowID(true);
		comboBox.setShowName(true);
		comboBox.setExpandWidth(30);
		comboBox.setTableShowList("name");
	}
	/**
	 * �õ�Ĭ�Ϲ�Ӧ����
	 * 
	 * @return String
	 */
	public void initAppOrgCode() {
		String appOrgCode = "";
		TParm fromOrgParm = INVPublicTool.getInstance().getOrgCode("B");
		if (fromOrgParm.getErrCode() >= 0) {
			appOrgCode = fromOrgParm.getValue("ORG_CODE", 0);
		}
		this.setValue("ORG_CODE", appOrgCode);
	}

	/**
	 * ��������Ϊ����ˢ���������datasotre
	 */
	public void onInitTable() {
		// ����
		tableM.setSQL(INVSQL.getInitPackStockMSql());
		tableM.retrieve();
		// ϸ��
		tableD.setSQL(INVSQL.getInitPackStockDSql());
		tableD.retrieve();
	}

	/**
	 * ��ӹ۲���
	 */
	public void observer() {
		// ��������ӹ۲���
		TDataStore dataStore = new TDataStore();
		dataStore
				.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
		dataStore.retrieve();
		// ��ϸ����ӹ۲���
		TDS tds = (TDS) tableD.getDataStore();
		tds.addObserver(new INVPackOberver());

		// ��������ӹ۲���
		dataStore = new TDataStore();
		dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
		dataStore.retrieve();
		// ��ϸ����ӹ۲���
		tds = (TDS) tableM.getDataStore();
		tds.addObserver(new INVPackTob(dataStore));

	}

	/**
	 * ������ѡ�񷵻����ݴ���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		// ���ô��������ݵķ���
		// ����
		setValue("PACK_CODE", parm.getValue("PACK_CODE"));
		// ����
		setValue("PACK_DESC", parm.getValue("PACK_DESC"));
		// ���Ч��
		valueDate = parm.getInt("VALUE_DATE");
		onQuery();
	}

	/**
	 * ��ѯ���
	 */
	public void onQuery() {
		// ������ݱ��
		if (!checkValueChange())
			return;
		// �õ���ѯsql
		String packCode = getValueString("PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("��������������!");
			return;
		}
		getDetialValue();
		// �õ�Ĭ�ϻ��պ�������ѡ��
		setCheckBoxValue();
		// ���Ա���
		this.callFunction("UI|save|setEnabled", true);
		// �����Գ���
		this.callFunction("UI|cancel|setEnabled", false);
		// ��¼��ѯ�����ݵ�״̬
		initStatus = tableM.getItemString(0, "STATUS");
	}

	/**
	 * ɨ������
	 */
	public void onScream() {
		String packCode = getValueString("SCREAM");
		if (packCode == null || packCode.length() == 0) {
			return;
		}
		setValue("PACK_CODE", packCode.substring(0, packCode.length() - 4));
		setValue("PACK_SEQ_NO",
				packCode.substring((packCode.length() - 4), packCode.length()));
		this.onQuery();
	}
	/**
	 * ���ѡ���¼�
	 */
	public void onSterilizationSelected() {
		if (tableM.getRowCount() <= 0)
			return;
		// ����ǲ����
		String selected = this.getValueString("STERILIZATION");
		if (selected.equals("N"))
			return;

		Timestamp sterilization_date = tableM.getItemTimestamp(0, "STERILIZATION_DATE");// �õ��������
		// ���Ҫ��ѡ��ϴ��Ҫ����Ƿ��ڿ�
		String status = tableM.getItemString(0, "STATUS");
		// ����״̬ ������ϴ
		if (status.equals("1")) {
			messageBox("�����ʲ��������!");
			setValue("STERILIZATION", "N");
		} else if (null != sterilization_date) {
			if (this.messageBox("��ʾ", "�����������!�Ƿ��ٴ����?", 2) != 0) {
				setValue("STERILIZATION", "N");
				return;
			}
		}
	}
	/**���ѡ���¼�*/
	public void onUnPackSelected(){
		if (tableM.getRowCount() <= 0)
			return;
		// ����ǲ����
		String selected = this.getValueString("UNPACK");
		if (selected.equals("N"))
			return;

		Timestamp pack_date = tableM.getItemTimestamp(0, "PACK_DATE");// �õ��������
		// ���Ҫ��ѡ��ϴ��Ҫ����Ƿ��ڿ�
		String status = tableM.getItemString(0, "STATUS");
		// ����״̬ ������ϴ
		if (status.equals("1")) {
			messageBox("�����ʲ����Դ��!");
			setValue("STERILIZATION", "N");
		} else if (null != pack_date) {
			if (this.messageBox("��ʾ", "�������Ѵ��!�Ƿ��ٴδ��?", 2) != 0) {
				setValue("UNPACK", "N");
				return;
			}
		}
	}

	/**
	 * �õ���ϸ����
	 */
	public void getDetialValue() {
		// �õ���ѯsql
		String packCode = getValueString("PACK_CODE");
		int packSeqNo = getValueInt("PACK_SEQ_NO");
		// �õ���ѯ��������sql
		String sql = INVSQL.getQueryStockMSql(packCode, packSeqNo);
		// ˢ������
		tableRetrive(tableM, sql);
		// ��ϸ��
		sql = INVSQL.getQueryStockDSql(packCode, packSeqNo);
		// ˢ��ϸ��
		tableRetriveD(tableD, sql);
		// �õ�Ĭ�ϻ��պ�������ѡ��
		setCheckBoxValue();
		if (tableM.getRowCount() <= 0)
			return;
		tableM.setSelectedRow(0);
		// �ҵ�Ĭ����������
		setDisnfectionData(packCode);
	}

	/**
	 * ��������Ĭ������
	 * 
	 * @param packCode
	 *            String
	 */
	public void setDisnfectionData(String packCode) {
		String sql = INVSQL.getPackMValueDateSql(packCode);
		if (sql == null || sql.equals(""))
			return;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			System.out.println("" + result.getErrText());
			return;
		}
		// ����Ч��
		int value = result.getInt("VALUE_DATE", 0);
		// ��ǰʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		// ����
		Timestamp valueDate = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), value);
		// ѡ�е���
		int selectRow = tableM.getSelectedRow();
		// ��������
		tableM.setItem(selectRow, "DISINFECTION_DATE", date);
		// Ĭ��Ч��
		tableM.setItem(selectRow, "VALUE_DATE", valueDate);
	}

	/**
	 * ����ѡ�¼�
	 */
	public void setCheckBoxValue() {
		if (tableM.getRowCount() <= 0)
			return;
		String status = tableM.getItemString(0, "STATUS");
		// ״̬ 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ����ϴ 4 ������ 5 ����� 6 �Ѵ�� 7:ά����
		Timestamp sterilization_date = tableM.getItemTimestamp(0,
				"STERILIZATION_DATE");// �õ���ϴ����
		// ���״̬��Ϊ1 ������ϴ���ڲ�Ϊnull
		if (!status.equals("1") && null == sterilization_date)
			setValue("STERILIZATION", "Y");
		else
			setValue("STERILIZATION", "N");

		Timestamp pack_date = tableM.getItemTimestamp(0, "PACK_DATE");// �õ���ϴ����
		// ���״̬��Ϊ1 ������ϴ���ڲ�Ϊnull
		if (!status.equals("1") && null == pack_date)
			setValue("UNPACK", "Y");
		else
			setValue("UNPACK", "N");
	}

	/**
	 * �������¼�
	 */
	public void onTableMClecked() {
		// �������
		int row = tableM.getSelectedRow();
		// �õ�������ʱҪ�����Ĳ�ѯ��ϸ���sql
		String packCode = tableM.getItemString(row, "PACK_CODE");
		int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
		// ��ϸ��
		String sql = INVSQL.getQueryStockDSql(packCode, packSeqNo);
		// ˢ��ϸ��
		tableRetriveD(tableD, sql);
		TParm tableCtzParm = tableM.getDataStore().getRowParm(row);
		setTextValue(tableCtzParm);
	}

	/**
	 * ����ˢ�±�
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetrive(TTable table, String sql) {
		table.setSQL(sql);
		table.retrieve();
		table.setDSValue();
	}
	/**
	 * ����ˢ��ϸ��
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetriveD(TTable table, String sql) {
		TDS tds = new TDS();
		tds.setSQL(sql);
        tds.retrieve();
		INVPackOberver obser = new INVPackOberver();
		tds.addObserver(obser);
		table.setDataStore(tds);
		table.setDSValue();
	}

	/**
	 * ϸ��ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChargeValue(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// �õ�table�ϵ�parmmap������
		String columnName = tableD.getDataStoreColumnName(node.getColumn());
		// �����ı�
		if ("RECOUNT_TIME".equals(columnName)) {
			// �õ���ǰ�ı�������
			int value = TCM_Transform.getInt(node.getValue());
			if (value < 0) {
				messageBox("�����������С��0!");
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * ����ĵ���¼�
	 */
	public void onTableMClicked() {
		int row = tableM.getSelectedRow();
		// �����Ϸ�
		TParm tableCtzParm = tableM.getDataStore().getRowParm(row);
		setTextValue(tableCtzParm);
	}

	/**
	 * �����Ϸ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTextValue(TParm parm) {
		setValueForParm("PACK_CODE;PACK_SEQ_NO", parm);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ������ݱ��
		if (!checkValueChange())
			return;
		// �������
		clearText();
		// �������
		clearTable(tableM);
		// �����ϸ��
		clearTable(tableD);
		// ���Ա���
		this.callFunction("UI|save|setEnabled", true);
		// �����Գ���
		this.callFunction("UI|cancel|setEnabled", false);
	}

	/**
	 * �������
	 */
	public void clearText() {
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM");
	}

	/**
	 * ���datastore
	 * 
	 * @param table
	 *            TTable
	 */
	public void clearTable(TTable table) {
		// �������һ���޸�
		table.acceptText();
		// ���ѡ��
		table.clearSelection();
		// ɾ��������
		table.removeRowAll();
		// ����޸ļ�¼
		table.resetModify();
	}

	/**
	 * ȡ��
	 * 
	 * @return boolean
	 */
	public boolean onCancel() {
		int rowCount = tableM.getRowCount();
		if (rowCount <= 0) {
			messageBox("û��Ҫ���������!");
			return false;
		}
		// ȡ�ñ�������
		TParm saveParm = getSaveParm(false);
		if (saveParm == null)
			return false;
		// ���ñ�������
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"savePackAndSterilization", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("����ʧ��");
			return false;
		}
		messageBox("����ɹ�");
		// �����ˢ�½���
		dealAaterSaveData();
		// ���Ա���
		this.callFunction("UI|save|setEnabled", true);
		// �����Գ���
		this.callFunction("UI|cancel|setEnabled", false);

		return true;
	}

	/**
	 * �������
	 * 
	 * @return boolean
	 */
	public boolean onUpdate() {

		int rowCount = tableM.getRowCount();
		if (rowCount <= 0) {
			messageBox("û��Ҫ���������!");
			return false;
		}
		// ȡ�ñ�������
		TParm saveParm = getSaveParm(true);
		if (saveParm == null)
			return false;
		// ���ñ�������
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"savePackAndSterilization", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("����ʧ��!");
			return false;
		}
		messageBox("����ɹ�!");
		// �����ˢ�½���
		dealAaterSaveData();
		// �����Ա���
		// this.callFunction("UI|save|setEnabled", false);
		// ���Գ���
		this.callFunction("UI|cancel|setEnabled", true);
		return true;
	}

	/**
	 * ������������
	 */
	public void dealAaterSaveData() {
		tableM.setDSValue();
		tableM.resetModify();
		tableD.setDSValue();
		tableD.resetModify();
		// �õ����պ�������ѡ��
		setCheckBoxValue();
	}

	/**
	 * ȡ�ñ�������
	 * 
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getSaveParm(boolean action) {
		TParm saveParm = new TParm();
		// ���
		String sterilization = this.getValueString("STERILIZATION");
		// ���
		String pack = this.getValueString("UNPACK");
		if (sterilization.equals("N") && pack.equals("N")) {
			messageBox("��Ҫ���������!");
			return null;
		}
		// ȡϵͳʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		// 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ����ϴ 4 ������ 5 ����� 6 �Ѵ�� 7:ά����
		if (sterilization.equals("Y")) {
			// ״̬����Ϊ�����
			tableM.setItem(0, "STATUS", "5");
			tableM.setItem(0, "STERILIZATION_DATE", date);
			tableM.setItem(0, "STERILIZATION_VALID_DATE", date);
			tableM.setItem(0, "STERILIZATION_USER", Operator.getID());
		}
		if (pack.equals("Y")) {
			// ״̬����Ϊ�Ѵ��
			tableM.setItem(0, "STATUS", "6");
			tableM.setItem(0, "PACK_DATE", date);
		}

		// �õ���������ݰ�
		TParm sterilizationParm = getSterilization(sterilization, pack, action);
		saveParm.setData("STERILIZATION", sterilizationParm.getData());
		// ȡ��������
		String[] saveSql = getsaveSql();
		saveParm.setData("SAVESQL", saveSql);
		return saveParm;
	}

	/**
	 * ���������¼����..ͬʱ��������������ͻ��յ�����
	 * 
	 * @param disinfection
	 *            String
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getSterilization(String sterilization, String pack,
			boolean action) {
		TParm sterilizationParm = new TParm();
		if (action) { // ������
			sterilizationParm = dealSaveParm(sterilization, pack);
		} else { // ����ȡ��
			sterilizationParm = dealCancelParm();
		}
		return sterilizationParm;
	}

	/**
	 * �����������
	 * 
	 * @param disinfection
	 *            String
	 * @return TParm
	 */
	public TParm dealSaveParm(String sterilization, String pack) {
		tableM.acceptText();
		// ȡϵͳ�¼�
		Timestamp date = SystemTool.getInstance().getDate();
		TParm sterilizationParm = tableM.getDataStore().getRowParm(0);
		sterilizationParm.setData("ORG_CODE", this.getValue("ORG_CODE"));// �õ���Ӧ�Ҳ���
		// 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ����ϴ 4 ������ 5 ����� 6 �Ѵ�� 7:ά����
		if (sterilization.equals("Y")) {
			// �������
			sterilizationParm.setData("DISINFECTION_DATE",
					TCM_Transform.getString(date));
			// ����Ч�� VALID_DATE
			Timestamp sterilization_valid_date = sterilizationParm
					.getTimestamp("STERILIZATION_VALID_DATE");
			sterilizationParm.setData("STERILIZATION_VALID_DATE",
					sterilization_valid_date);

			sterilizationParm.setData("STERILIZATION_USER", Operator.getID());// �����Ա
			sterilizationParm.setData("STERILIZATION_OPERATIONSTAFF",
					Operator.getID());// ���������Ա
			// ����
			sterilizationParm.setData("QTY", 1);
			sterilizationParm.setCount(1);
		}
		if (pack.equals("Y")) {
			sterilizationParm.setData("PACK_DATE", date);
			sterilizationParm.setCount(1);
		}
		// ������
		sterilizationParm.setData("OPT_USER", Operator.getID());
		// ʱ��
		sterilizationParm.setData("OPT_DATE", date);
		// ����IP
		sterilizationParm.setData("OPT_TERM", Operator.getIP());

		sterilizationParm.setData("ACTIONMODE", "SAVE");
		return sterilizationParm;

	}

	/**
	 * ����ȡ���ı���parm
	 * 
	 * @return TParm
	 */
	public TParm dealCancelParm() {
		// ȡϵͳ�¼�
		TParm dispenfectionParm = new TParm();
		// 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ����ϴ 4 ������ 5 ����� 6 �Ѵ�� 7:ά����
		// �����¼
		dispenfectionParm = tableM.getDataStore().getRowParm(0);
		String sterilizationDate = dispenfectionParm
				.getValue("STERILIZATION_DATE");
		sterilizationDate = sterilizationDate.substring(0, 4)
				+ sterilizationDate.substring(5, 7)
				+ sterilizationDate.substring(8, 10)
				+ sterilizationDate.substring(11, 13)
				+ sterilizationDate.substring(14, 16)
				+ sterilizationDate.substring(17, 19);
		// ����ʱ��
		dispenfectionParm.setData("STERILIZATION_DATE", sterilizationDate);
		// ״̬����Ϊ�Ѿ�����
		tableM.setItem(0, "STATUS", initStatus);
		// �������
		tableM.setItem(0, "STERILIZATION_DATE", "");
		// �����Ա
		tableM.setItem(0, "STERILIZATION_USER", "");
		// ���Ч��
		tableM.setItem(0, "STERILIZATION_VALID_DATE", "");
		// �������
		tableM.setItem(0, "PACK_DATE", "");
		dispenfectionParm.setCount(1);
		dispenfectionParm.setData("ACTIONMODE", "CANCEL");
		return dispenfectionParm;

	}

	/**
	 * �õ�Ҫ���������
	 * 
	 * @return String[]
	 */
	public String[] getsaveSql() {
		// ȡ������sql
		String[] sqlM = tableM.getUpdateSQL();
		// ȡ��ϸ��sql
		String[] sqlD = tableD.getUpdateSQL();
		// ������sql�ϲ�
		return StringTool.copyArray(sqlM, sqlD);
	}

	/**
	 * ������ݱ����ʾ��Ϣ�������ϸ��ʱ��ʾ���ݵı����
	 * 
	 * @return boolean
	 */
	public boolean checkValueChange() {
		// ��������ݱ��
		if (checkData())
			switch (this.messageBox("��ʾ��Ϣ", "���ݱ���Ƿ񱣴�",
					this.YES_NO_CANCEL_OPTION)) {
			// ����
			case 0:
				if (!onUpdate())
					return false;
				return true;
				// ������
			case 1:
				return true;
				// ����
			case 2:
				return false;
			}
		return true;
	}

	/**
	 * ������ݱ��
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// ����
		if (tableM.isModified())
			return true;
		// ϸ��
		if (tableD.isModified())
			return true;
		return false;
	}

	/**
	 * �Ƿ�رմ���(�������ݱ��ʱ��ʾ�Ƿ񱣴�)
	 * 
	 * @return boolean true �ر� false ���ر�
	 */
	public boolean onClosing() {
		// ��������ݱ��
		if (checkData())
			switch (this.messageBox("��ʾ��Ϣ", "�Ƿ񱣴�", this.YES_NO_CANCEL_OPTION)) {
			// ����
			case 0:
				if (!onUpdate())
					return false;
				break;
			// ������
			case 1:
				return true;
				// ����
			case 2:
				return false;
			}
		// û�б��������
		return true;
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TParm parm = new TParm();
		int row = tableM.getSelectedRow();
		if (row < 0)
			row = 0;
		String packCode = tableM.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("������ѡ�����!");
			return;
		}
		// ���������:
		int packSeqNo = tableM.getItemInt(row, "PACK_SEQ_NO");
		String packCodeSeq = "" + packSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;
		TParm orgParm = INVPublicTool.getInstance().getOrgDesc();

		String deptDesc = orgParm.getValue("DEPT_CHN_DESC", 0);

		// ��������
		TParm packCodeSeqParm = new TParm();
		TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
		String packDesc = packParm.getValue("PACK_DESC", row);
		String packDept = deptDesc;
		// �������
		String orgCode=this.getValueString("ORG_CODE");
		String sql="SELECT STERILIZATION_DATE,STERILIZATION_VALID_DATE,STERILIZATION_USER,OPT_USER FROM INV_PACKSTOCKM " +
					"WHERE ORG_CODE='"+orgCode+"' AND PACK_CODE='"+packCode+"' AND PACK_SEQ_NO='"+packSeqNo+"'";
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		String sterilization_date = StringTool.getString(
				(Timestamp) result.getData("STERILIZATION_DATE", 0),
				"yyyy/MM/dd");
		String valueDate = StringTool.getString(
				(Timestamp) result.getData("STERILIZATION_VALID_DATE", 0), "yyyy/MM/dd");
		String optUser = result.getValue("OPT_USER", 0);
		String sterilization_user = result.getValue("STERILIZATION_USER", 0);

		packCodeSeqParm.addData("PACK_CODE_SEQ", packCode + packCodeSeq);
		packCodeSeqParm.addData("PACK_DESC", packDesc);
		packCodeSeqParm.addData("PACK_DEPT", "(" + packDept + ")");
		packCodeSeqParm.addData("DISINFECTION_DATE", sterilization_date);
		packCodeSeqParm.addData("VALUE_DATE", valueDate);
		packCodeSeqParm.addData("OPT_USER", optUser);
		packCodeSeqParm.addData("DISINFECTION_USER", sterilization_user);
		packCodeSeqParm.setCount(1);
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_CODE_SEQ");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DEPT");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "VALUE_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_USER");

		parm = new TParm();
		parm.setData("T1", packCodeSeqParm.getData());

		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackSeq.jhw", parm);
	}

}
