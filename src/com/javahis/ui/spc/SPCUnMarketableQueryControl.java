package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.SPCMarketQueryTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����ҩƷ��ѯ
 * </p>
 * 
 * <p>
 * Description: ����ҩƷ��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author shendr 2013.10.18
 * @version 1.0
 */
public class SPCUnMarketableQueryControl extends TControl {

	/**
	 * TABLE�ؼ�
	 */
	private TTable tableA;// ����
	private TTable tableB;// δ����
	/** ���� */
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		tableA = getTTable("TABLE_A");
		tableB = getTTable("TABLE_B");
		addListener(getTTable("TABLE_A"));
		addListener(getTTable("TABLE_B"));
		initTime("A");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.setValue("ORG_CODE", Operator.getDept());
	}

	/**
	 * ��ʼ��ʱ��
	 * 
	 * @param type
	 */
	private void initTime(String type) {
		// ��ʼ��ͳ������(Ĭ�ϴ�2����֮ǰ�����յ�)
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		if ("A".equals(type)) {
			// ����ʱ��
			setValue("END_TIME", StringTool.getTimestamp(date.toString()
					.substring(0, 10)
					+ " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
			// ��ʼʱ��
			setValue("START_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
			// ���ʱ��
			setValue("IN_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		} else {
			// ���ʱ��
			setValue("START_TIME", StringTool.getTimestamp(StringTool.rollDate(
					date, -91).toString().substring(0, 10)
					+ " 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (!queryCheck())
			return;
		String start_time = "";
		String end_time = "";
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		int day = this.getValueInt("DAY");
		String org_code = this.getValueString("ORG_CODE");
		if (getRadioButton("TYPEA").isSelected()) {
			start_time = this.getValueString("START_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
			end_time = this.getValueString("END_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
		} else {
			start_time = (StringTool.getTimestamp(
					StringTool.rollDate(date, -day)).toString().replaceAll("-",
					"").trim().substring(0, 8));
			end_time = this.getValueString("END_TIME").replaceAll("-", "")
					.trim().substring(0, 8);
		}
		String in_time = this.getValueString("IN_TIME").replaceAll("-", "")
				.trim().substring(0, 8);
		String order_code = this.getValueString("ORDER_CODE");
		TParm parm = new TParm();
		parm.setData("ORG_CODE", org_code);
		parm.setData("START_TIME", start_time);
		parm.setData("END_TIME", end_time);
		parm.setData("IN_TIME", in_time);
		parm.setData("ORDER_CODE", order_code);
		TTable table = new TTable();
		TParm result = new TParm();
		if (tableA.isShowing()) {
			table = tableA;
			result = SPCMarketQueryTool.getInstance().queryByTypeA(parm);
		} else {
			table = tableB;
			result = SPCMarketQueryTool.getInstance().queryByTypeB(parm);
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("E0008");
			onClear();
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * ��ѯǰ���
	 */
	public boolean queryCheck() {
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String in_time = this.getValueString("IN_TIME");
		String day = this.getValueString("DAY");
		int iDay = this.getValueInt("DAY");
		if (tableA.isShowing()) {
			if (getRadioButton("TYPEA").isSelected()) {
				if (StringUtil.isNullString(start_time)
						|| StringUtil.isNullString(end_time)) {
					messageBox("��ѡ���ѯ����");
					return false;
				}
				if (StringUtil.isNullString(in_time)) {
					messageBox("��ѡ���������");
					return false;
				}
			} else {
				if (StringUtil.isNullString(day) || iDay <= 0) {
					messageBox("��ѡ���ѯ����");
					return false;
				}
			}
		} else {
			if (StringUtil.isNullString(start_time)) {
				messageBox("��ѡ���������");
				return false;
			}
		}
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		String tags = "ORG_CODE;ORDER_CODE;ORDER_DESC;DAY";
		clearValue(tags);
		if (tableA.isShowing()) {
			initTime("A");
		} else {
			initTime("B");
		}
		getRadioButton("TYPEA").setSelected(true);
		tableA.removeRowAll();
		tableB.removeRowAll();
		onTableChanged();
		onRadioChanged();
	}

	/**
	 * ҳǩ�л��¼�
	 */
	public void onTableChanged() {
		if (tableA.isShowing()) {
			getTLabel("LABEL_TIME").setText("��ѯ����:");
			getTLabel("LABEL").setVisible(true);
			getTLabel("LABEL_IN_TIME").setVisible(true);
			getTLabel("LABEL_DAY").setVisible(true);
			getTTextFormat("IN_TIME").setVisible(true);
			getTTextFormat("END_TIME").setVisible(true);
			getTextField("DAY").setVisible(true);
			getRadioButton("TYPEA").setVisible(true);
			getRadioButton("TYPEB").setVisible(true);
			initTime("A");
		} else {
			getTLabel("LABEL_TIME").setText("������� <=");
			getTLabel("LABEL").setVisible(false);
			getTLabel("LABEL_IN_TIME").setVisible(false);
			getTLabel("LABEL_DAY").setVisible(false);
			getTTextFormat("IN_TIME").setVisible(false);
			getTTextFormat("END_TIME").setVisible(false);
			getTextField("DAY").setVisible(false);
			getRadioButton("TYPEA").setVisible(false);
			getRadioButton("TYPEB").setVisible(false);
			initTime("B");
		}
	}

	/**
	 * ��ѯ����ѡ���л��¼�
	 */
	public void onRadioChanged() {
		if (getRadioButton("TYPEA").isSelected()) {
			getTextField("DAY").setEnabled(false);
			setValue("DAY", "");
		} else {
			getTextField("DAY").setEnabled(true);
		}
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (tableA.isShowing()) {
			if (tableA.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			} else {
				ExportExcelUtil.getInstance().exportExcel(tableA, "����ҩƷ��ѯ");
			}
		} else {
			if (tableB.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			} else {
				ExportExcelUtil.getInstance().exportExcel(tableB, "δ����ҩƷ��ѯ");
			}
		}
	}

	/**
	 * ��ȡTABLE�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡTTextField�ؼ�
	 * 
	 * @param string
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * ��ȡTLabel�ؼ�
	 * 
	 * @param string
	 * @return
	 */
	public TLabel getTLabel(String tag) {
		return (TLabel) getComponent(tag);
	}

	/**
	 * ��ȡTTextFormat�ؼ�
	 * 
	 * @param string
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

	/**
	 * ��ȡTRadioButton�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	// =================================�����ܿ�ʼ==================================
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж�
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = table.getParmValue();
				TParm totAmtRow = tableData.getRow(tableData
						.getCount("ORDER_DESC") - 1);// add by wanglong 20130108
				tableData.removeRow(tableData.getCount("ORDER_DESC") - 1);// add by
				// wanglong
				// 20130108
				// System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = table.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				TParm lastResultParm = new TParm();// ��¼���ս��
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����
				for (int k = 0; k < columnName.length; k++) {// add by wanglong
					// 20130108
					lastResultParm.addData(columnName[k], totAmtRow
							.getData(columnName[k]));
				}
				lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add
				// by
				// wanglong
				// 20130108
				table.setParmValue(lastResultParm);
			}
		});
	}

	/**
	 * ����ת������ֵ
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectoryת��param
	 */
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		return parmTable;
	}
	// ================================�����ܽ���==================================

}
