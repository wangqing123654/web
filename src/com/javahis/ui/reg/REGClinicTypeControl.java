package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import java.util.Date;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import java.util.ArrayList;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;

import jdo.bil.BIL;

/**
 * 
 * <p>
 * Title: ���������ÿ�����
 * </p>
 * 
 * <p>
 * Description: ���������ÿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl 2009.07.16
 * @version 1.0
 */
public class REGClinicTypeControl extends TControl {
	int selectRow = -1;
	boolean flg;
	TParm data;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ������table�����¼�
		callFunction("UI|TYPETABLE|addEventListener", "TYPETABLE->"
				+ TTableEvent.CLICKED, this, "onTYPETABLEClicked");
		// ���������table�����¼�
		callFunction("UI|TYPEFEETABLE|addEventListener", "TYPEFEETABLE->"
				+ TTableEvent.CLICKED, this, "onTYPEFEETABLEClicked");
		// ������tableר�õļ���
		getTTable("TYPEFEETABLE").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		OrderList orderDesc = new OrderList();
		TTable table = (TTable) this.getComponent("TYPEFEETABLE");
		table.addItem("ORDER_LIST", orderDesc);
		// //���������tableֵ�ı��¼�
		// this.addEventListener("TYPEFEETABLE->" + TTableEvent.CHANGE_VALUE,
		// "onTYPEFEETABLEChargeValue");
		initPage();
		onClear();
	}

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		typeFeeTable.setFilter(" CLINICTYPE_CODE = '' ");
		typeFeeTable.filter();

	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * sysFee��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ���õ�ǰѡ�е���

		selectRow = row;
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		// �����ǰѡ�е��в������һ�п�����ʲô������
		if (typeFeeTable.getRowCount() != selectRow + 1)
			return;
		// �����ǰ�к�
		column = typeFeeTable.getColumnModel().getColumnIndex(column);
		// �õ���ǰ����
		String columnName = typeFeeTable.getParmMap(column);
		// ����sysfee�Ի������
		if (!columnName.equals("ORDER_CODE"))
			return;
		// if (column != 1)
		// return;
		//
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("SYSFEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"));
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}

	/**
	 * sysFeeģ����ѯ
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ORDER_CODE");
			Vector d = (Vector) parm.getData("ORDER_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * ����ҽ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// System.out.println("parm"+parm);
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		// typeFeeTable.acceptText();
		String orderCode = parm.getValue("ORDER_CODE");
		String orderDesc = parm.getValue("ORDER_DESC");
		int selRow = typeFeeTable.getSelectedRow();
		typeFeeTable.setItem(selRow, "ORDER_CODE", orderCode);
		// ===========pangben modify 20110608 start �����
		typeFeeTable.setItem(selRow, "ORDER_DESC", orderDesc);
		// ===========pangben modify 20110608 stop
		typeFeeTable.setValueAt(parm.getDouble("OWN_PRICE") < 0 ? 0.00 : parm
				.getDouble("OWN_PRICE"), selRow, 5);
		// �����пɱ���
		typeFeeTable.getDataStore().setActive(selRow, true);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String clinicCode = TypeTool.getString(getValue("CLINICTYPE_CODE"));
		String clinicDesc = TypeTool.getString(getValue("CLINICTYPE_DESC"));
		StringBuffer sb = new StringBuffer();
		if (clinicCode != null && clinicCode.length() > 0)
			sb.append(" CLINICTYPE_CODE like '" + clinicCode + "%' ");
		if (clinicDesc != null && clinicDesc.length() > 0) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append(" CLINICTYPE_DESC like '" + clinicDesc + "%' ");
		}
		String sql1 = "  SELECT CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,SEQ,"
				+ "         DESCRIPTION,ADM_TYPE,PROF_FLG,OPT_USER,OPT_DATE,OPT_TERM "
				+ "    FROM REG_CLINICTYPE ";
		String sql2 = " ORDER BY SEQ,CLINICTYPE_CODE ";
		if (sb.length() > 0)
			sql1 += " WHERE " + sb.toString() + sql2;
		else
			sql1 = sql1 + sql2;
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (sqlParm.getErrCode() < 0) {
			messageBox(sqlParm.getErrText());
			return;
		}
		// �ű�table��ֵ
		this.callFunction("UI|TYPETABLE|setParmValue", sqlParm);
		// �ű����table��ֵ
		TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");
		TDataStore typeFeeDataStore = typeFeeTable.getDataStore();
		typeFeeDataStore.retrieve();

	}

	/**
	 * ����
	 * 
	 * @return boolean
	 */
	public boolean onSave() {
		// ����table����
		if (!this.emptyTextCheck("ADM_TYPE")
				&& !this.emptyTextCheck("CLINICTYPE_CODE"))
			return false;
		String admType = getValue("ADM_TYPE").toString(); // �ż���
		String typeCode = getValue("CLINICTYPE_CODE").toString(); // �ű����
		String typeDesc = getValue("CLINICTYPE_DESC").toString(); // �ű�˵��
		String py1 = getValue("PY1").toString(); // ƴ����
		String py2 = getValue("PY2").toString(); // ע����
		int seq = TypeTool.getInt(getValue("SEQ")); // ˳���
		String description = getValue("DESCRIPTION").toString(); // ��ע
		String profFlg = getValue("PROF_FLG").toString(); // ר����ע��
		String optUser = Operator.getID(); // ������Ա
		Timestamp optDate = SystemTool.getInstance().getDate(); // ��������
		String optTerm = Operator.getIP(); // �����ն�
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CLINICTYPE_CODE", typeCode);
		parm.setData("CLINICTYPE_DESC", typeDesc);
		parm.setData("PY1", py1);
		parm.setData("PY2", py2);
		parm.setData("SEQ", seq);
		parm.setData("DESCRIPTION", description);
		parm.setData("PROF_FLG", profFlg);
		parm.setData("OPT_USER", optUser);
		parm.setData("OPT_DATE", optDate);
		parm.setData("OPT_TERM", optTerm);
		String selSql = " SELECT CLINICTYPE_CODE FROM REG_CLINICTYPE WHERE CLINICTYPE_CODE = '"
				+ typeCode + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(selSql));
		if (selParm.getCount("CLINICTYPE_CODE") > 0) {
			String updateSql = " UPDATE REG_CLINICTYPE "
					+ "    SET CLINICTYPE_DESC='"
					+ typeDesc
					+ "',PY1='"
					+ py1
					+ "',PY2='"
					+ py2
					+ "',SEQ='"
					+ seq
					+ "',DESCRIPTION='"
					+ description
					+ "',"
					+ "        PROF_FLG='"
					+ profFlg
					+ "',OPT_USER='"
					+ optUser
					+ "',OPT_DATE=SYSDATE,OPT_TERM='"
					+ optTerm
					+ "' "
					+ "  WHERE CLINICTYPE_CODE='"
					+ typeCode
					+ "'"
					+ "    AND ADM_TYPE ='" + admType + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(
					updateSql));
			// ˢ�£�����ĩ��ĳ�е�ֵ
			int row = (Integer) callFunction("UI|TYPETABLE|getSelectedRow");
			if (row < 0)
				System.out.println("�ű��޸���");
			else {
				TParm data = (TParm) callFunction("UI|TYPETABLE|getParmValue");
				data.setRowData(row, parm);
				callFunction("UI|TYPETABLE|setRowParmValue", row, data);
			}

		} else {
			String sql = " INSERT INTO REG_CLINICTYPE "
					+ "             (ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,SEQ,"
					+ "             DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,OPT_TERM) "
					+ "      VALUES ('" + admType + "','" + typeCode + "','"
					+ typeDesc + "','" + py1 + "','" + py2 + "','" + seq + "',"
					+ "             '" + description + "','" + profFlg + "','"
					+ optUser + "',SYSDATE,'" + optTerm + "') ";
			TParm insertParm = new TParm(TJDODBTool.getInstance().update(sql));
			if (insertParm.getErrCode() < 0) {
				messageBox(insertParm.getErrText());
				return false;
			}
			// table�ϼ���������������ʾ
			callFunction("UI|TYPETABLE|addRow", parm,
					"ADM_TYPE,CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;"
							+ "DESCRIPTION;PROF_FLG;OPT_USER;OPT_DATE;OPT_TERM");
		}
		// ����table����
		Timestamp date = StringTool.getTimestamp(new Date());
		TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");
		// �����ı�
		typeFeeTable.acceptText();
		TDataStore dataStore = typeFeeTable.getDataStore();
		// ���ȫ���Ķ����к�
		int rows[] = dataStore.getModifiedRows();
		// ���̶�����������
		for (int i = 0; i < rows.length; i++) {
			dataStore.setItem(rows[i], "OPT_USER", optUser);
			dataStore.setItem(rows[i], "OPT_DATE", date);
			dataStore.setItem(rows[i], "OPT_TERM", optTerm);
		}
		if (!typeFeeTable.update()) {
			messageBox("E0001");
			return false;
		}
		messageBox("P0001");
		typeFeeTable.setDSValue();
		this.onClear();
		return true;
	}

	/**
	 * ��������
	 */
	public void onNew() {
		TTable table = (TTable) getComponent("TYPEFEETABLE");
		String maxCode = "";
		// ��������ݵ�˳����
		int maxSeq = getMaxSeq(table.getDataStore(), "SEQ");
		if (getValue("CLINICTYPE_CODE").toString().length() > 0) {
			// ����ӵ��к�
			int row = table.addRow();
			// ��ǰѡ�е���
			table.setSelectedRow(row);
			// Ĭ�Ϻ��ż������
			table.setItem(row, "ADM_TYPE", getValue("ADM_TYPE"));
			// Ĭ�Ϻű���ô���
			table.setItem(row, "CLINICTYPE_CODE", getValue("CLINICTYPE_CODE")); // RECEIPT_TYPE
			// Ĭ��˳����
			table.setItem(row, "SEQ", maxSeq);
			// �����в��ɱ���
			table.getDataStore().setActive(row, false);
		} else {
			this.messageBox("�޹����ű�");
			return;
		}
	}

	/**
	 * ɾ��
	 */
	public void onDeleteC() {
		TTable typeTable = (TTable) this.getComponent("TYPETABLE");
		TParm typeParm = typeTable.getParmValue();
		int typeSelRow = typeTable.getSelectedRow();
		String admType = typeParm.getValue("ADM_TYPE", typeSelRow);
		String typeCode = typeParm.getValue("CLINICTYPE_CODE", typeSelRow);
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		int typeFeeSelRow = typeFeeTable.getSelectedRow();
		String orderCode = typeFeeTable.getItemString(typeFeeSelRow,
				"ORDER_CODE");
		String delTypeSql = "";
		String delTypeAndFeeSql = "";
		String delTypeFeeSql = "";
		ArrayList list = new ArrayList();
		if (false) {
			// if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 0) == 0) {
			if (flg) {
				delTypeSql = " DELETE REG_CLINICTYPE WHERE CLINICTYPE_CODE = '"
						+ typeCode + "' AND ADM_TYPE = '" + admType + "'";
				delTypeAndFeeSql = " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '"
						+ typeCode + "' AND ADM_TYPE = '" + admType + "' ";
				list.add(delTypeSql);
				list.add(delTypeAndFeeSql);
				String[] allSql = (String[]) list.toArray(new String[] {});
				TParm delTypeParm = new TParm(TJDODBTool.getInstance().update(
						allSql));
				if (delTypeParm.getErrCode() < 0) {
					err(delTypeParm.getErrName() + " "
							+ delTypeParm.getErrText());
					return;
				}
				typeTable.retrieve();
			} else {
				delTypeFeeSql = " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '"
						+ typeCode
						+ "' AND ADM_TYPE = '"
						+ admType
						+ "' AND ORDER_CODE = '"
						+ orderCode
						+ "'"
						+ orderCode
						+ "' ";
				TParm delTypeFeeParm = new TParm(TJDODBTool.getInstance()
						.update(delTypeFeeSql));
				if (delTypeFeeParm.getErrCode() < 0) {
					err(delTypeFeeParm.getErrName() + " "
							+ delTypeFeeParm.getErrText());
					return;
				}
				typeFeeTable.retrieve();
				typeFeeTable.setFilter("ADM_TYPE = '"
						+ getValue("ADM_TYPE").toString()
						+ "' AND CLINICTYPE_CODE = '"
						+ getValue("CLINICTYPE_CODE").toString() + "'");
			}
		} else {
			return;
		}
		this.onClear();
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		TTable typeTable = (TTable) this.getComponent("TYPETABLE");
		// TParm typeParm = typeTable.getParmValue();
		// int typeSelRow = typeTable.getSelectedRow();
		String admType = getValue("ADM_TYPE").toString();
		String typeCode = getValue("CLINICTYPE_CODE").toString();
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		int typeFeeSelRow = typeFeeTable.getSelectedRow();
		String orderCode = typeFeeTable.getItemString(typeFeeSelRow,
				"ORDER_CODE");
		String delTypeSql = "";
		String delTypeAndFeeSql = "";
		String delTypeFeeSql = "";
		ArrayList list = new ArrayList();
		if (this.messageBox("ѯ��", "�Ƿ�ɾ��", 0) == 0) {
			if (flg) {
				delTypeSql = " DELETE REG_CLINICTYPE WHERE CLINICTYPE_CODE = '"
						+ typeCode + "' AND ADM_TYPE = '" + admType + "'";
				delTypeAndFeeSql = " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '"
						+ typeCode + "' AND ADM_TYPE = '" + admType + "' ";
				list.add(delTypeSql);
				list.add(delTypeAndFeeSql);
				String[] allSql = (String[]) list.toArray(new String[] {});
				TParm delTypeParm = new TParm(TJDODBTool.getInstance().update(
						allSql));
				if (delTypeParm.getErrCode() < 0) {
					err(delTypeParm.getErrName() + " "
							+ delTypeParm.getErrText());
					return;
				}
				typeTable.retrieve();
			} else {
				delTypeFeeSql = " DELETE REG_CLINICTYPE_FEE WHERE CLINICTYPE_CODE = '"
						+ typeCode
						+ "' AND ADM_TYPE = '"
						+ admType
						+ "' AND ORDER_CODE = '" + orderCode + "' ";
				// System.out.println("delTypeFeeSql" + delTypeFeeSql);
				TParm delTypeFeeParm = new TParm(TJDODBTool.getInstance()
						.update(delTypeFeeSql));
				if (delTypeFeeParm.getErrCode() < 0) {
					err(delTypeFeeParm.getErrName() + " "
							+ delTypeFeeParm.getErrText());
					return;
				}
				typeFeeTable.retrieve();
				typeFeeTable.setFilter("ADM_TYPE = '"
						+ getValue("ADM_TYPE").toString()
						+ "' AND CLINICTYPE_CODE = '"
						+ getValue("CLINICTYPE_CODE").toString() + "'");
			}
		} else {
			return;
		}
		this.onClear();
	}

	/**
	 * ���
	 */
	public void onClear() {
		data = new TParm();
		clearValue("ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG;TOT_FEE");
		this.callFunction("UI|TYPETABLE|removeRowAll");
		this.callFunction("UI|TYPEFEETABLE|removeRowAll");
		TTable typeTable = (TTable) this.getComponent("TYPETABLE");
		TDataStore typeDataStore = typeTable.getDataStore();
		typeDataStore.resetModify();
		TTable typeFeeTable = (TTable) this.getComponent("TYPEFEETABLE");
		TDataStore typeFeeDataStore = typeFeeTable.getDataStore();
		typeFeeDataStore.resetModify();
		selectRow = -1;
		onQuery();
		data = typeTable.getParmValue();
		long seq = 0;
		// ȡSEQ���ֵ
		if (data.existData("SEQ")) {
			Vector vct = data.getVectorValue("SEQ");
			for (int i = 0; i < vct.size(); i++) {
				long a = Long.parseLong((vct.get(i)).toString().trim());
				if (a > seq)
					seq = a;
			}
			this.setValue("SEQ", seq + 1);
		}

	}

	/**
	 * �ű�table�����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTYPETABLEClicked(int row) {
		flg = true;
		if (row < 0)
			return;
		TTable typeFeeTable = (TTable) getComponent("TYPEFEETABLE");

		int typeFeeAllRow = typeFeeTable.getRowCount();
		String orderCode = typeFeeTable.getItemString(typeFeeAllRow - 1,
				"ORDER_CODE");
		if ((orderCode != null && orderCode.length() != 0)
				|| typeFeeAllRow == 0) {
			this.onDeleteC();
		}

		TParm data = (TParm) callFunction("UI|TYPETABLE|getParmValue");
		setValueForParm(
				"ADM_TYPE;CLINICTYPE_CODE;CLINICTYPE_DESC;PY1;PY2;SEQ;DESCRIPTION;PROF_FLG",
				data, row);
		selectRow = row;

		typeFeeTable.setFilter("ADM_TYPE = '" + getValue("ADM_TYPE").toString()
				+ "' AND CLINICTYPE_CODE = '"
				+ getValue("CLINICTYPE_CODE").toString() + "'");
		typeFeeTable.filter();
		// ��ʼ�������
		String selOrderCode = "";
		double ownPrice = 0.00;
		double sumPrice = 0.00;
		int allRow = typeFeeTable.getRowCount();
		for (int i = 0; i < allRow; i++) {
			selOrderCode = typeFeeTable.getValueAt(i, 2).toString();
			ownPrice = BIL.getFee(selOrderCode);
			if (ownPrice < 0)
				ownPrice = 0;
			sumPrice += ownPrice;
			typeFeeTable.setValueAt(ownPrice < 0 ? 0.00 : ownPrice, i, 5);
		}
		// ��ʾ�ܷ���
		this.setValue("TOT_FEE", sumPrice + "");
		// �����������
		typeFeeAllRow = typeFeeTable.getRowCount();
		orderCode = typeFeeTable.getItemString(typeFeeAllRow - 1, "ORDER_CODE");
		if ((orderCode != null && orderCode.length() != 0)
				|| typeFeeAllRow == 0) {
			this.onNew();
		}
	}

	/**
	 * ��������
	 */
	public void onExport() {
		// ���������ѯ�����Ľ����������ҳ����ʾ�����е���������ʱ��ֱ������
		TTable table = (TTable) getComponent("TYPETABLE");
		String Sql = "SELECT *FROM (SELECT CASE WHEN AA.ADM_TYPE ='E' THEN '����' WHEN AA.ADM_TYPE = 'O' THEN '����' END ADM_TYPE,"
				+ "AA.CLINICTYPE_CODE," + "AA.CLINICTYPE_DESC," + "AA.PY1,"
				+ "AA.PY2," + "AA.SEQ," + "AA.DESCRIPTION," + "AA.PROF_FLG,"
				+ "CASE WHEN AA.OPT_USER ='qiandongliang' THEN 'Ǯ����' WHEN AA.OPT_USER = 'yangtianbao' THEN '���챦' END OPT_USER," 
				+ "AA.OPT_DATE," 
				+ "AA.OPT_TERM,"
				+ "CASE WHEN BB.RECEIPT_TYPE ='CLINIC_FEE' THEN '�Һŷ�' WHEN BB.RECEIPT_TYPE = 'REG_FEE' THEN '����' END RECEIPT_TYPE," 
				+ "BB.ORDER_CODE," + "BB.ORDER_DESC,"
				+ "CC.OWN_PRICE"
				+ " FROM REG_CLINICTYPE AA, REG_CLINICTYPE_FEE BB, SYS_FEE CC"
				+ " WHERE AA.CLINICTYPE_CODE = BB.CLINICTYPE_CODE"
				+ " AND BB.ORDER_CODE = CC.ORDER_CODE)"
				+ " ORDER BY CLINICTYPE_CODE";

		TParm parm = new TParm(TJDODBTool.getInstance().select(Sql));

		 System.out.println("������������������������������������������������������������"+parm);
		if (parm.getCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		// ���õ������ķ�������ͷ���ֶ�������������Ҫ����д
		ExportExcelUtil.getInstance().exportExcel(table.getHeader()+";�շ����,70,RECEIPT_TYPE;���ô���,150;��������,200;����,100,double,###0.00",
				table.getParmMap()+";RECEIPT_TYPE;ORDER_CODE;ORDER_DESC;OWN_PRICE", parm, "�ű���ϸ");

	}

	/**
	 * �ű�table�����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTYPEFEETABLEClicked(int row) {
		flg = false;
		TTable typeTable = (TTable) this.getComponent("TYPETABLE");
		typeTable.clearSelection();
		if (row < 0)
			return;
	}

	/**
	 * �õ����ı��
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName) {
		if (dataStore == null)
			return 0;
		// ����������
		int count = dataStore.rowCount();
		// ��������
		int s = 0;
		for (int i = 0; i < count; i++) {
			int value = dataStore.getItemInt(i, columnName);
			// �������ֵ
			if (s < value) {
				s = value;
				continue;
			}
		}
		// ���ż�1
		s++;
		return s;
	}
}
