package com.javahis.ui.pha;

import java.sql.Timestamp;
import java.util.Date;

import jdo.pha.PHABasePreserveTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �а�װת��ά�������� 
 * </p>
 * 
 * <p>
 * Description: �а�װת��ά��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Date: 2013.4.29
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author shendongri
 * @version 1.0
 */
public class PHABasePreserveControl extends TControl {

	// ����ȫ��table�ؼ�
	private TTable table;

	/**
	 * ���table�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��� TextField�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.table = this.getTable("PHA_BASE");
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
	}

	/**
	 * ��ձ�����ı���
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "ORDER_CODE;ORDER_DESC;CONVERSION_RATIO;DOSAGE_UNIT;MEDI_UNIT;PURCH_UNIT;STOCK_UNIT";
		clearValue(tags);
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
		onQueryUnitByOrderCode();
	}

	/**
	 * ����ҩƷ�����ѯ��ҩ��λ����ҩ��λ���ɹ���λ����浥λ������
	 */
	public void onQueryUnitByOrderCode() {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		TParm result = PHABasePreserveTool.getInstance()
				.queryUnitByOrderCodeInfo(parm);
		// Ϊ�ı���ֵ
		setValue("DOSAGE_UNIT", result.getData("UNIT_CHN_DESC", 0));
		setValue("MEDI_UNIT", result.getData("UNIT_CHN_DESC", 1));
		setValue("PURCH_UNIT", result.getData("UNIT_CHN_DESC", 2));
		setValue("STOCK_UNIT", result.getData("UNIT_CHN_DESC", 3));
	}

	/**
	 * ��񵥻�,���һ�����ݣ���ʾ����Ӧ�ı�����
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		setValue("ORDER_CODE", table.getItemString(row, "ORDER_CODE"));
		setValue("ORDER_DESC", table.getItemString(row, "ORDER_DESC"));
		setValue("CONVERSION_RATIO", table.getItemString(row,
				"CONVERSION_RATIO"));
		setValue("DOSAGE_UNIT", table.getItemString(row, "DOSAGE_UNIT"));
		setValue("MEDI_UNIT", table.getItemString(row, "MEDI_UNIT"));
		setValue("PURCH_UNIT", table.getItemString(row, "PURCH_UNIT"));
		setValue("STOCK_UNIT", table.getItemString(row, "STOCK_UNIT"));
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (table.getSelectedRow() < 0) {
			messageBox("��ѡ��һ�����ݽ��и���!");
			return;
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("ORDER_DESC", getValueString("ORDER_DESC"));
		parm.setData("CONVERSION_RATIO", getValueString("CONVERSION_RATIO"));
		parm.setData("SPECIFICATION", getValueString("SPECIFICATION"));
		parm.setData("DOSAGE_UNIT", getValueString("DOSAGE_UNIT"));
		parm.setData("MEDI_UNIT", getValueString("MEDI_UNIT"));
		parm.setData("PURCH_UNIT", getValueString("PURCH_UNIT"));
		parm.setData("STOCK_UNIT", getValueString("STOCK_UNIT"));
		// ������λ���ƣ���ѯUNIT_CODE
		TParm unitParm = PHABasePreserveTool.getInstance().queryUnitDescByCode(
				parm);
		parm.setData("DOSAGE_UNIT", unitParm.getData("UNIT_CODE", 0));
		parm.setData("MEDI_UNIT", unitParm.getData("UNIT_CODE", 1));
		parm.setData("PURCH_UNIT", unitParm.getData("UNIT_CODE", 2));
		parm.setData("STOCK_UNIT", unitParm.getData("UNIT_CODE", 3));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = new TParm();
		result = PHABasePreserveTool.getInstance().updateInfo(parm);
		if (result.getErrCode() < 0) {
			return;
		}
		messageBox("���³ɹ���");
		onClear();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String order_Code = getValueString("ORDER_CODE");
		parm.setData("ORDER_CODE", order_Code);
		TParm result = new TParm();
		if (!StringUtil.isNullString(order_Code)) {
			result = PHABasePreserveTool.getInstance().queryByOrderCodeInfo(
					parm);
		} else {
			result = PHABasePreserveTool.getInstance().queryInfo();
		}
		table.setParmValue(result);
	}

}
