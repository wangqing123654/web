package com.javahis.ui.spc;

import jdo.spc.SPCCodeMapTool;
import jdo.spc.SPCDrugSearchTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.system.textFormat.TextFormatSYSUnit;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:�龫׷�ٱ���
 * </p>
 * 
 * <p>
 * Description:�龫׷�ٱ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-11-27
 * @version 1.0
 */
public class SPCDrugSearchControl extends TControl {

	private TTable table;

	public void onInit() {
		table = getTable("TABLE");
		// ���õ����˵�
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
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
		String order_code = this.getValueString("ORDER_CODE");
		String bar_code = this.getValueString("BAR_CODE");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		if (!StringUtil.isNullString(start_time)) {
			start_time = start_time.substring(0, 19);
		}
		if (!StringUtil.isNullString(end_time)) {
			end_time = end_time.substring(0, 19);
		}
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		parm.setData("BAR_CODE", bar_code);
		parm.setData("START_TIME", start_time);
		parm.setData("END_TIME", end_time);
		TParm result = SPCDrugSearchTool.getInstance().query(parm);
		if (result.getErrCode() < 0) {
			messageBox("��ѯʧ��");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			if ("A".equals(result.getValue("TYPE", i))
					|| "C".equals(result.getValue("TYPE", i))
					|| "E".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "���");
			} else if ("B".equals(result.getValue("TYPE", i))
					|| "D".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "����");
			} else if ("F".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "��ҩ");
			} else if ("G".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "��ҩ");
			} else if ("H".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "����");
			}
		}
		table.setParmValue(result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		String tags = "ORDER_CODE;ORDER_DESC;BAR_CODE;START_TIME;END_TIME";
		this.clearValue(tags);
		table.removeRowAll();
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�龫׷�ٱ���");
	}

	/**
	 * ��ȡTABLE�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ��ȡTEXTFIELD�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

}
