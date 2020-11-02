package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.event.TTableEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.Operator;
import jdo.inv.INVSQL;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TDS;
import com.javahis.manager.INVPackOberver;

import jdo.inv.INVOrgTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPackStockMTool;
import jdo.inv.INVPublicTool;

/**
 * 
 * <p>
 * Title:��ϴ,����,����
 * </p>
 * 
 * <p>
 * Description: ��ϴ,����,����
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
 * @author fudo 2009-5-4
 * @version 1.0
 */
public class INVBackPackAndDisnfectionNewControl extends TControl {
	/**
	 * ������
	 */
	private TTable table_packm;

	/** ���ռ�¼table */
	private TTable table_dis;
	/**
	 * ������ϸ
	 */
	private TTable tableD;
	/**
	 * ��¼�����ȱ�ѡ�е���
	 */
	private String initStatus = "";
	/** ��ʼ�����յ��� */
	private String recycleNo;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		/** ���ռ�¼table */
		table_dis = (TTable) getComponent("TABLE_DIS");
		// �����������
		table_packm = (TTable) getComponent("TABLE_PACKM");
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
		recycleNo = getRecycleNo();
		onInitTable();
		iniAppOrgCombo();// 20130425 wangzl ���
		initAppOrgCode();// 20130425 wangzl ���
		onIntiData();
		observer();
	}

	/**
	 * ��������Ϊ����ˢ���������datasotre
	 */
	public void onInitTable() {
		// ����������
		table_packm.setSQL(INVSQL.getInitPackStockMSql());
		table_packm.retrieve();
		// ������ϸ��
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
		tds = (TDS) table_packm.getDataStore();
		tds.addObserver(new INVPackTob(dataStore));
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

	/** ��ʼ�����ڿؼ����� */
	public void onIntiData() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", date);
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
	}

	/**
	 * ��ѯ���
	 */
	public void onQuery() {
		// �õ���ѯsql
		String packCode = getValueString("PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("��������������!");
			return;
		}
		getDetialValue();
		// �õ�Ĭ�ϻ��� ��ϴ ������ѡ��
		setCheckBoxValue();
		// ���Ա���
		this.callFunction("UI|save|setEnabled", true);
		// �����Գ���
		this.callFunction("UI|cancel|setEnabled", false);
		// ��¼��ѯ�����ݵ�״̬
		initStatus = table_packm.getItemString(0, "STATUS");
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
	 * �õ���ϸ����
	 */
	public void getDetialValue() {
		// �õ���������
		String disSql = "SELECT * FROM INV_DISINFECTION WHERE 1=1";
		// �õ���ѯ����
		String selRecycleNo = this.getValueString("SEL_RECYCLE_NO");
		String startRecyleDate = this.getValueString("START_RECYCLE_DATE");
		startRecyleDate = startRecyleDate.substring(0, 10);

		String endRecyleDate = this.getValueString("END_RECYCLE_DATE");
		endRecyleDate = endRecyleDate.substring(0, 10);
		if (selRecycleNo.length() != 0)
			disSql += " AND RECYCLE_NO='" + selRecycleNo + "'";
		disSql += " AND RECYCLE_DATE BETWEEN TO_DATE('" + startRecyleDate
				+ "','YYYY-MM-DD') AND TO_DATE('" + endRecyleDate
				+ "','YYYY-MM-DD')";
		TParm result=new TParm(TJDODBTool.getInstance().select(disSql));
		table_dis.setParmValue(result);
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
	 * ����ѡ�¼�
	 */
	public void setCheckBoxValue() {
		if (table_packm.getRowCount() <= 0)
			return;
		int selRow = table_packm.getSelectedRow();
		TParm selRowParm = table_packm.getDataStore().getRowParm(selRow);
		String status = selRowParm.getValue("STATUS");
		// ״̬ 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ����ϴ 4 ������ 5 ����� 6 �Ѵ�� 7:ά����
		// �����Ի��չ�ѡ����
		if (status.equals("1") || status.equals("5"))
			setValue("BACK", "Y");
		else
			setValue("BACK", "N");

		Timestamp washDate = selRowParm.getTimestamp("WASH_DATE");// �õ���ϴ����
		// ���״̬��Ϊ1 ������ϴ���ڲ�Ϊnull
		if (!status.equals("1") && null == washDate)
			setValue("WASH", "Y");
		else
			setValue("WASH", "N"); // 20130424 wangzl ���

		Timestamp disinfectionDate = selRowParm
				.getTimestamp("DISINFECTION_DATE");// �õ���������
		// ���״̬Ϊ1 ������ϴ����Ϊnull
		if (!status.equals("1") && null == disinfectionDate)
			setValue("DISINFECTION", "Y");
		else
			setValue("DISINFECTION", "N");
	}

	/**
	 * ���չ�ѡ�¼�
	 */
	public void onBackSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// ����ǲ�����
		String selected = this.getValueString("BACK");
		int row = table_packm.getSelectedRow();
		// ���Ҫ��ѡ���վ�Ҫ����Ƿ��ڿ�
		String status = table_packm.getItemString(row, "STATUS");
		if (selected.equals("N") && status.equals("1")) {// 20130424 wangzl ���
			this.setValue("WASH", "N");
			this.setValue("DISINFECTION", "N");
			return;
		}

		// �����ѻ��� ��ѡ����
		if (!status.equals("1") && !status.equals("7")) {
			messageBox("�������Ѿ��ڿ�!");
			setValue("BACK", "N");
		}
	}

	/**
	 * ����ѡ���¼�
	 */
	public void onDisinfectionSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// ����ǲ�����
		String selected = this.getValueString("DISINFECTION");
		if (selected.equals("N"))
			return;
		int row = table_packm.getSelectedRow();
		Timestamp disinfection_date = table_packm.getItemTimestamp(row,
				"DISINFECTION_DATE");// �õ���ϴ����
		// ���Ҫ��ѡ���վ�Ҫ����Ƿ��ڿ�
		String status = table_packm.getItemString(row, "STATUS");
		// ����״̬ ��������
		if (status.equals("1")) {
			messageBox("�����ʲ���������!");
			setValue("DISINFECTION", "N");
		} else if (null != disinfection_date) {
			if (this.messageBox("��ʾ", "������������!�Ƿ��ٴ�����?", 2) != 0) {
				setValue("WASH", "N");
				return;
			}
		}
	}

	/**
	 * ��ϴѡ���¼�
	 */
	public void onWashSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// ����ǲ���ϴ
		String selected = this.getValueString("WASH");
		if (selected.equals("N"))
			return;
		int row = table_packm.getSelectedRow();
		Timestamp washDate = table_packm.getItemTimestamp(row, "WASH_DATE");// �õ���ϴ����
		// ���Ҫ��ѡ��ϴ��Ҫ����Ƿ��ڿ�
		String status = table_packm.getItemString(row, "STATUS");
		// ����״̬ ������ϴ
		if (status.equals("1")) {
			messageBox("�����ʲ�������ϴ!");
			setValue("WASH", "N");
		} else if (null != washDate) {
			if (this.messageBox("��ʾ", "����������ϴ!�Ƿ��ٴ���ϴ?", 2) != 0) {
				setValue("WASH", "N");
				return;
			}
		}
	}

	/**
	 * �������¼�
	 */
	public void onTableMClecked() {
		table_packm.acceptText();
		// �������
		int row = table_packm.getSelectedRow();
		// �õ�������ʱҪ�����Ĳ�ѯ��ϸ���sql
		TParm tableMParm = table_packm.getDataStore().getRowParm(row);
		setTextValue(tableMParm);
		// ��ϸ��
		String sql = INVSQL.getQueryStockDSql(tableMParm.getValue("PACK_CODE"),
				tableMParm.getInt("PACK_SEQ_NO"));
		// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// tableD.setParmValue(result);
		// ˢ��ϸ��
		tableRetriveD(tableD, sql);
		setCheckBoxValue();
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
		// �������
		clearText();
		// �������
		clearTable(table_dis);
		// ���������
		clearTable(table_packm);
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
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;PACK_STATUS;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM");
	}

	/**
	 * ���
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
		int rowCount = table_packm.getRowCount();
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
				"saveBackPackAndDisnfection", saveParm);
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

	/** �������յ����� */
	public void onNew() {
		if (table_packm.getSelectedRow() < 0) {
			// �����ϸ
			clearTable(tableD);
			// ���������
			clearTable(table_packm);
		}
		if (table_dis.getSelectedRow() >= 0
				|| table_packm.getSelectedRow() >= 0) {
			if (this.getValueString("ORG_CODE").length() == 0) {
				messageBox("��Ӧ�Ҳ��Ų���Ϊ��");
				return;
			}
			if (this.getValueString("PACK_CODE").length() == 0) {
				messageBox("���Ų���Ϊ��");
				return;
			}
			// ����֮ǰ ��ѯ �Ƿ���ڴ�������
			String sql = "SELECT * FROM INV_PACKSTOCKM WHERE ORG_CODE='"
					+ getValueString("ORG_CODE") + "' " + "AND PACK_CODE='"
					+ getValueString("PACK_CODE") + "' " + "AND PACK_SEQ_NO='"
					+ getValueInt("PACK_SEQ_NO") + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() < 0) {
				messageBox("��������������");
				return;
			}
			String status = result.getValue("STATUS", 0);// �õ�����������״̬
			this.setValue("PACK_STATUS", status);
			// ����������
			newTablePack();
			return;
		}
		// �������յ�
		newTableDis();
	}

	/** �������g�� */
	public void newTablePack() {
		// ��˳��ⵥ
		if (!checkNewDetial())
			return;
		// �������
		String sql = "SELECT * FROM INV_PACKSTOCKM " + "	 WHERE ORG_CODE='"
				+ this.getValue("ORG_CODE") + "' " + "AND PACK_CODE='"
				+ this.getValue("PACK_CODE") + "' " + "AND PACK_SEQ_NO="
				+ this.getValue("PACK_SEQ_NO") + "";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int row = table_packm.addRow();
		// ����
		table_packm.setItem(row, "PACK_DESC", getValue("PACK_DESC"));
		// ����
		table_packm.setItem(row, "PACK_CODE", getValue("PACK_CODE"));
		// ���
		table_packm.setItem(row, "PACK_SEQ_NO", getValue("PACK_SEQ_NO"));
		// ����
		table_packm.setItem(row, "QTY", 1);
		// ״̬
		table_packm.setItem(row, "STATUS", result.getData("STATUS", 0));
		// ��������
		table_packm.setItem(row, "RECYCLE_DATE",
				result.getData("RECYCLE_DATE", 0));
		// ������Ա
		table_packm.setItem(row, "RECYCLE_USER",
				result.getData("RECYCLE_USER", 0));
		// ��ϴ����
		table_packm.setItem(row, "WASH_DATE", result.getData("WASH_DATE", 0));
		// ��ϴ��Ա
		table_packm.setItem(row, "WASH_USER", result.getData("WASH_USER", 0));
		// ��������
		table_packm.setItem(row, "DISINFECTION_DATE",
				result.getData("DISINFECTION_DATE", 0));
		// ������Ա
		table_packm.setItem(row, "DISINFECTION_USER",
				result.getData("DISINFECTION_USER", 0));
		// ����
		table_packm.setItem(row, "DISINFECTION_POTSEQ",
				result.getData("DISINFECTION_POTSEQ", 0));
		// ����
		table_packm.setItem(row, "DISINFECTION_PROGRAM",
				result.getData("DISINFECTION_PROGRAM", 0));
		// ʹ�óɱ�
		table_packm.setItem(row, "USE_COST", result.getDouble("USE_COST", 0));
		// һ���Բ��ϳɱ�
		table_packm.setItem(row, "ONCE_USE_COST",
				result.getDouble("ONCE_USE_COST", 0));
		table_packm.setSelectedRow(row);
		table_packm.setDSValue();
		// ������������
		installTableDisData();
		setCheckBoxValue();
	}

	/** ����ϸ��ʱ ��������յ��� */
	public void installTableDisData() {
		int disSleRow = table_dis.getSelectedRow();// �õ�����ѡ����
		int packmSleRow = table_packm.getSelectedRow();// �õ��ӱ�ѡ����

		table_dis.setItem(disSleRow, "PACK_CODE",
				table_packm.getItemString(packmSleRow, "PACK_CODE"));
		table_dis.setItem(disSleRow, "PACK_SEQ_NO",
				table_packm.getItemInt(packmSleRow, "PACK_SEQ_NO"));
		table_dis.setItem(disSleRow, "QTY",
				table_packm.getItemInt(packmSleRow, "QTY"));

		table_dis.setItem(disSleRow, "RECYCLE_DATE",
				table_packm.getItemTimestamp(packmSleRow, "RECYCLE_DATE"));
		table_dis.setItem(disSleRow, "RECYCLE_USER",
				table_packm.getItemString(packmSleRow, "RECYCLE_USER"));

		table_dis.setItem(disSleRow, "WASH_DATE",
				table_packm.getItemTimestamp(packmSleRow, "WASH_DATE"));
		table_dis.setItem(disSleRow, "WASH_USER",
				table_packm.getItemString(packmSleRow, "WASH_USER"));

		table_dis.setItem(disSleRow, "DISINFECTION_DATE",
				table_packm.getItemTimestamp(packmSleRow, "DISINFECTION_DATE"));
		table_dis.setItem(disSleRow, "DISINFECTION_USER",
				table_packm.getItemString(packmSleRow, "DISINFECTION_USER"));

		table_dis.setItem(disSleRow, "DISINFECTION_POTSEQ",
				table_packm.getItemString(packmSleRow, "DISINFECTION_POTSEQ"));
		table_dis.setItem(disSleRow, "DISINFECTION_PROGRAM",
				table_packm.getItemString(packmSleRow, "DISINFECTION_PROGRAM"));

	}

	/**
	 * ������ϸʱ���
	 * 
	 * @return boolean
	 */
	public boolean checkNewDetial() {
		// ���ⵥ(�����ǲ��Ǳ�ѡ��)
		int row = table_dis.getSelectedRow();
		if (row < 0) {
			messageBox_("��ѡ����յ�!");
			return false;
		}
		// ͨ�ü�˷���
		return true;
	}

	/**
	 * �������յ�
	 */
	public void newTableDis() {
		int row = table_dis.addRow();
		TDataStore ds=table_dis.getDataStore();
		Timestamp date = SystemTool.getInstance().getDate();
//		table_dis.setItem(row, "RECYCLE_NO", recycleNo);
//		table_dis.setItem(row, "SEQ_NO", row + 1);
//		table_dis.setItem(row, "RECYCLE_DATE", date);
//		table_dis.setItem(row, "RECYCLE_USER", Operator.getID());
//		table_dis.setItem(row, "OPT_USER", Operator.getID());
//		table_dis.setItem(row, "OPT_DATE", date);
//		table_dis.setItem(row, "OPT_TERM", Operator.getIP());
		
		ds.setItem(row, "RECYCLE_NO", recycleNo);
		ds.setItem(row, "SEQ_NO", row + 1);
		ds.setItem(row, "RECYCLE_DATE", date);
		ds.setItem(row, "RECYCLE_USER", Operator.getID());
		ds.setItem(row, "OPT_USER", Operator.getID());
		ds.setItem(row, "OPT_DATE", date);
		ds.setItem(row, "OPT_TERM", Operator.getIP());
		table_dis.setSelectedRow(row);
		table_dis.setDSValue(row);
	}

	/**
	 * �������
	 * 
	 * @return boolean
	 */
	public boolean onUpdate() {

		int rowCount = table_packm.getRowCount();
		if (rowCount <= 0) {
			messageBox("û��Ҫ���������!");
			return false;
		}

		// ȡ�ñ���ϸ������
		TParm saveParm = getSaveParm(true);
		if (saveParm == null)
			return false;
		// �õ�������������
		table_dis.acceptText();
		TParm conditions = new TParm();
		int disCount = table_dis.getDataStore().rowCount();
		for (int i = 0; i < disCount; i++) {
			conditions = table_dis.getDataStore().getRowParm(i);
		}
		saveParm.setData("disParm", conditions.getData());

		// ���ñ�������
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"saveBackPackAndDisnfection", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("����ʧ��!");
			onClear();
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

	/** ���յ��� */
	private String getRecycleNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_SUPREQUEST", "No");
		return recyleNo;
	}

	/**
	 * �������쵥��
	 * 
	 * @param table
	 *            TTable
	 * @param requestNo
	 *            String
	 */
	public void dealRequestNo(TTable table, String recycle_no) {
		int row = table.getRowCount();
		if (row <= 0)
			return;
		for (int i = 0; i < row; i++) {
			table.setItem(i, "RECYCLE_NO", recycle_no);
		}
	}

	/**
	 * ������������
	 */
	public void dealAaterSaveData() {
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
		TParm packmOneParm = new TParm();
		// ����
		String backPack = this.getValueString("BACK");
		// ��ϴ
		String wash = this.getValueString("WASH");
		// ����
		String disinfection = this.getValueString("DISINFECTION");
		if (backPack.equals("N") && wash.equals("N")
				&& disinfection.equals("N")) {
			messageBox("��Ҫ���������!");
			return null;
		}
		// ȡϵͳʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		int count = table_packm.getDataStore().rowCount();
		for (int i = 0; i < count; i++) {
			if (backPack.equals("Y")) {
				// ״̬����Ϊ�Ѿ����
				table_packm.setItem(i, "STATUS", "2");
				table_packm.setItem(i, "RECYCLE_DATE", date);
				table_packm.setItem(i, "RECYCLE_USER", Operator.getID());
			}
			if (wash.equals("Y")) {
				table_packm.setItem(i, "STATUS", "3");
				// ��ϴ����
				table_packm.setItem(i, "WASH_DATE", date);
				table_packm.setItem(i, "WASH_USER", Operator.getID());
			}
			String disPotSeq = table_packm.getItemString(i,
					"DISINFECTION_POTSEQ");// �õ�����
			String disProgram = table_packm.getItemString(i,
					"DISINFECTION_PROGRAM");// �õ�����

			// ����
			if (disinfection.equals("Y")) {
				if (disPotSeq.length() == 0 && disProgram.length() == 0) {
					messageBox("���� ������Ϊ��");
					return null;
				} else {
					// ״̬����Ϊ�Ѿ�����
					table_packm.setItem(i, "STATUS", "4");
					// ��������
					table_packm.setItem(i, "DISINFECTION_DATE", date);
					// ������Ա
					table_packm.setItem(i, "DISINFECTION_USER",
							Operator.getID());
					if (table_packm.getItemTimestamp(i,
							"DISINFECTION_VALID_DATE") == null) {
						// ����Ч�� VALID_DATE
						table_packm.setItem(i, "DISINFECTION_VALID_DATE",
								StringTool.rollDate(date, 14));
					}
				}

			}
			// ������
			table_packm.setItem(i, "OPT_USER", Operator.getID());
			// ʱ��
			table_packm.setItem(i, "OPT_DATE", date);
			// ����IP
			table_packm.setItem(i, "OPT_TERM", Operator.getIP());
			// ����
			table_packm.setItem(i, "ORG_CODE", this.getValue("ORG_CODE"));

			table_packm.acceptText();// �õ���������
			packmOneParm = table_packm.getDataStore().getRowParm(i);
		}
		saveParm.setData("TABLE_PACKM", packmOneParm.getData());
		// ȡ��������
		String[] saveSql = getsaveSql();
		saveParm.setData("SAVESQL", saveSql);
		return saveParm;
	}

	/**
	 * �õ�Ҫ���������
	 * 
	 * @return String[]
	 */
	public String[] getsaveSql() {
		// ȡ������sql
		String[] sqlM = table_packm.getUpdateSQL();
		String[] sqlD = tableD.getUpdateSQL();
		// ������sql�ϲ�
		return StringTool.copyArray(sqlM, sqlD);
	}

	/**
	 * ���������¼����..ͬʱ�����������ϴ�����ͻ��յ�����
	 * 
	 * @param backPack
	 *            String
	 * @param disinfection
	 *            String
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getDisinfectionParm(String backPack, String wash,
			String disinfection, boolean action) {
		TParm dispenfectionParm = new TParm();
		// ����ȡ��
		dispenfectionParm = dealCancelParm();

		return dispenfectionParm;
	}

	/**
	 * ����ȡ���ı���parm
	 * 
	 * @return TParm
	 */
	public TParm dealCancelParm() {
		// ȡϵͳ�¼�
		TParm dispenfectionParm = new TParm();
		// ״̬ 0 �ڿ⣬1 ����, 2 �ѻ��� 3 ������ 4:ά����
		// ȡϵͳ�¼�
		Timestamp date = SystemTool.getInstance().getDate();
		// ������¼
		dispenfectionParm = table_packm.getDataStore().getRowParm(0);
		String disinfectionDate = dispenfectionParm
				.getValue("DISINFECTION_DATE");
		disinfectionDate = disinfectionDate.substring(0, 4)
				+ disinfectionDate.substring(5, 7)
				+ disinfectionDate.substring(8, 10)
				+ disinfectionDate.substring(11, 13)
				+ disinfectionDate.substring(14, 16)
				+ disinfectionDate.substring(17, 19);
		// ����ʱ��
		dispenfectionParm.setData("DISINFECTION_DATE", disinfectionDate);
		// ����ʱ��
		dispenfectionParm.setData("WASH_DATE",
				dispenfectionParm.getValue("WASH_DATE"));

		// ��ϴ����
		table_packm.setItem(0, "WASH_DATE", "");
		// ����״̬
		table_packm.setItem(0, "STATUS", initStatus);
		// ��������
		table_packm.setItem(0, "DISINFECTION_DATE", "");
		// ������Ա
		table_packm.setItem(0, "DISINFECTION_USER", "");
		// ����Ч��
		table_packm.setItem(0, "DISINFECTION_VALID_DATE", "");
		dispenfectionParm.setCount(1);
		dispenfectionParm.setData("ACTIONMODE", "CANCEL");
		return dispenfectionParm;

	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TParm parm = new TParm();
		int row = table_packm.getSelectedRow();
		if (row < 0)
			row = 0;
		String packCode = table_packm.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("������ѡ�����!");
			return;
		}
		// ���������:
		int packSeqNo = table_packm.getItemInt(row, "PACK_SEQ_NO");
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
		// ��������
		TParm result = INVPackStockMTool.getInstance().getPackDate(packCode,
				packSeqNo);
		String disinfectionDate = StringTool.getString(
				(Timestamp) result.getData("DISINFECTION_DATE", 0),
				"yyyy/MM/dd");
		String valueDate = StringTool.getString(
				(Timestamp) result.getData("DISINFECTION_VALID_DATE", 0),
				"yyyy/MM/dd");
		String optUser = result.getValue("OPT_USER", 0);
		String disinfectionUser = result.getValue("DISINFECTION_USER", 0);

		packCodeSeqParm.addData("PACK_CODE_SEQ", packCode + packCodeSeq);
		packCodeSeqParm.addData("PACK_DESC", packDesc);
		packCodeSeqParm.addData("PACK_DEPT", "(" + packDept + ")");
		packCodeSeqParm.addData("DISINFECTION_DATE", disinfectionDate);
		packCodeSeqParm.addData("VALUE_DATE", valueDate);
		packCodeSeqParm.addData("OPT_USER", optUser);
		packCodeSeqParm.addData("DISINFECTION_USER", disinfectionUser);
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
}
