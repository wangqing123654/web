package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;

import jdo.spc.INDSQL;
import jdo.spc.IndDispenseMTool;
import java.util.Map;
import java.util.HashMap;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҩƷ����������
 * </p>
 * 
 * <p>
 * Description: ҩƷ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) �����к� 2011
 * </p>  
 * 
 * <p>
 * Company: �����к�
 * </p>
 * 
 * @author zhangy ���� ZhenQin
 * @version 4.0
 */
public class INDDispenseQueryControl extends TControl {

	TTable table;

	public INDDispenseQueryControl() {

	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table = this.getTable("TABLE");

		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

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
		//fux modify ��ʼ����� 20150918
		this.setValue("APP_ORG_CODE","");    
	}

	/**
	 * ��ѯ����  
	 */
	public void onQuery() {
		table.removeRowAll();
		this.setValue("SUM_TOT", 0);
		this.setValue("SUM_COUNT", 0);
		this.setValue("SUM_AMT", 0);

		TParm parm = new TParm();
		// YYYY/MM/DD HH:mm:ss
		parm.setData("START_DATE", this.getValueString("START_DATE").substring(
				0, 19).replaceAll("-", "/"));
		parm.setData("END_DATE", this.getValueString("END_DATE").substring(0,
				19).replaceAll("-", "/"));
		if (!"".equals(this.getValueString("REQUEST_TYPE"))) {
			parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
		}
		if (!"".equals(this.getValueString("ORDER_CODE"))) {
			parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		}
		if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
			parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		}
		if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
			parm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
		}
		if (this.getRadioButton("RadioButton1").isSelected()) {
			parm.setData("DISPENSE_OUT", "Y");
		} else {
			parm.setData("DISPENSE_IN", "Y");
		}    
		if ("Y".equals(this.getValueString("STOP_FLG"))) {
			parm.setData("STOP_FLG", this.getValueString("STOP_FLG"));
		}
		parm.setData("REGION_CODE", Operator.getRegion());
		// System.out.println("param: " + parm);
		TParm result = new TParm();
		result = IndDispenseMTool.getInstance().onQueryDispense(parm);
		if (result == null || result.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}

		double sum_qty = 0;
		double sum_amt = 0;
		Map map = new HashMap();
		map.put("DEP", "��������");
		map.put("TEC", "��ҩ����");
		map.put("EXM", "����Ʒ�");
		map.put("GIF", "ҩ������");
		map.put("RET", "�˿�");
		map.put("WAS", "���");
		map.put("THO", "��������");
		map.put("COS", "���Ĳ�����");
		map.put("THI", "�������");
		for (int i = 0; i < result.getCount(); i++) {
			sum_amt += result.getDouble("OWN_AMT", i);
			sum_qty += result.getDouble("QTY", i);
			result.setData("REQTYPE_CODE", i, map.get(result.getValue(
					"REQTYPE_CODE", i)));
		}

		table.setParmValue(result);
		this.setValue("SUM_TOT", StringTool.round(sum_qty, 3));
		this.setValue("SUM_COUNT", result.getCount());
		this.setValue("SUM_AMT", StringTool.round(sum_amt, 2));

	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		String clearStr = "REQUEST_TYPE;ORDER_CODE;ORDER_DESC;STOP_FLG;TO_ORG_CODE;"
				+ "APP_ORG_CODE;SUM_TOT;SUM_COUNT;SUM_AMT";
		this.clearValue(clearStr);

		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		getRadioButton("RadioButton1").setSelected(true);
		table.removeRowAll();
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TTable table = getTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		// ��ӡ����
		TParm date = new TParm();
		// ��ͷ����
		date.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "ҩƷ�����");
		date.setData("STATUS", "TEXT", "״̬: "
				+ (getRadioButton("RadioButton1").isSelected() ? "����" : "���"));
		String start_date = getValueString("START_DATE");
		String end_date = getValueString("END_DATE");
		date.setData("DATE_AREA", "TEXT", "ͳ������: " + start_date.substring(0, 4)
				+ "/" + start_date.substring(5, 7) + "/"
				+ start_date.substring(8, 10) + " "
				+ start_date.substring(11, 13) + ":"
				+ start_date.substring(14, 16) + ":"
				+ start_date.substring(17, 19) + " ~ "
				+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
				+ "/" + end_date.substring(8, 10) + " "
				+ end_date.substring(11, 13) + ":" + end_date.substring(14, 16)
				+ ":" + end_date.substring(17, 19));
		date.setData("DATE", "TEXT", "�Ʊ�ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
		// �������
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		String dispense_date = "";
		String warehousing_date = "";
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("STOP_FLG", "Y".equals(tableParm.getValue("STOP_FLG",
					i)) ? "��" : "��");
			parm.addData("REQTYPE_CODE", tableParm.getValue("REQTYPE_CODE", i));
			parm.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i));
			parm.addData("DEPT_CHN_DESC", tableParm
					.getValue("DEPT_CHN_DESC", i));
			parm.addData("ORG_CHN_DESC", tableParm.getValue("ORG_CHN_DESC", i));
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm
					.getValue("SPECIFICATION", i));
			parm.addData("UNIT_CHN_DESC", tableParm
					.getValue("UNIT_CHN_DESC", i));
			parm.addData("QTY", tableParm.getValue("QTY", i));
			parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
			parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
			parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", i));
			parm.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i)
					.substring(0, 10));
			dispense_date = tableParm.getValue("DISPENSE_DATE", i);
			parm.addData("DISPENSE_DATE",
					dispense_date.length() > 10 ? dispense_date
							.substring(0, 10) : dispense_date);
			warehousing_date = tableParm.getValue("WAREHOUSING_DATE", i);
			parm.addData("WAREHOUSING_DATE",
					warehousing_date.length() > 10 ? warehousing_date
							.substring(0, 10) : warehousing_date);
		}
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "STOP_FLG");
		parm.addData("SYSTEM", "COLUMNS", "REQTYPE_CODE");
		parm.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
		parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
		parm.addData("SYSTEM", "COLUMNS", "DISPENSE_DATE");
		parm.addData("SYSTEM", "COLUMNS", "WAREHOUSING_DATE");
		date.setData("TABLE", parm.getData());
		// ��β����
		date.setData("SUM_AMT", "TEXT", "�ܽ� " + getValueDouble("SUM_AMT"));
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDDispenseQuery.jhw",
				date);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�������ͳ��");
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
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
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
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
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
}
