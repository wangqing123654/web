package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCCabinetSearchTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:���ܹ������ѯ
 * </p>
 * 
 * <p>
 * Description:���ܹ������ѯ
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
 * @author shendr 2013-10-11
 * @version 1.0
 */
public class SPCCabinetSearchControl extends TControl {

	// ���ؼ�
	private TTable table_m;
	private TTable table_d;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
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
		// ״̬�����б�Ĭ��ѡ�е�һ��
		((TComboBox) getComponent("STATUS")).setValue("1");
		OnInitTime();
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
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String cabinet_id = this.getValueString("CABINET_ID");
		String order_code = this.getValueString("ORDER_CODE");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String drug_flg = "";// �龫����ҩ���
		String all_flg = "";// ���ܣ���ϸ���
		if (getTRadioButton("TYPE_C").isSelected()) {
			drug_flg = "N";
		} else if (getTRadioButton("TYPE_D").isSelected()) {
			drug_flg = "Y";
		}
		if (getTRadioButton("TYPE_A").isSelected()) {
			all_flg = "Y";
		} else if (getTRadioButton("TYPE_B").isSelected()) {
			all_flg = "N";
		}
		parm.setData("CABINET_ID", cabinet_id);
		parm.setData("ORDER_CODE", order_code);
		parm.setData("START_TIME", start_time);
		parm.setData("DRUG_FLG", drug_flg);
		parm.setData("END_TIME", end_time);
		String status = this.getValueString("STATUS");
		TParm result = new TParm();
		if ("1".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoA(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoAA(parm);
			}
		} else if ("2".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoB(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoBB(parm);
			}
		} else if ("3".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoC(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoCC(parm);
			}
		} else if ("4".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoD(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoDD(parm);
			}
		} else if ("5".equals(status)) {
			if ("Y".equals(all_flg)) {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance().queryInfoEDrug(
							parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoENormal(parm);
				}
			} else {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoEEDrug(parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoEENormal(parm);
				}
			}
		} else if ("6".equals(status)) {
			if ("Y".equals(all_flg)) {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance().queryInfoFDrug(
							parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFNormal(parm);
				}
			} else {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFFDrug(parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFFNormal(parm);
				}
			}
		}
		if ("Y".equals(all_flg)) {
			table_m.setParmValue(result);
		} else {
			table_d.setParmValue(result);
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		String tags = "CABINET_ID;START_TIME;END_TIME;ORDER_CODE;ORDER_DESC;STATUS";
		clearValue(tags);
		getTRadioButton("TYPE_A").setSelected(true);
		getTRadioButton("TYPE_C").setSelected(true);
		table_m.removeRowAll();
		table_d.removeRowAll();
		table_m.setVisible(true);
		table_d.setVisible(false);
		OnInitTime();
	}

	/**
	 * ͳ�����ı��¼�
	 */
	public void onTypeChanged() {
		if (getTRadioButton("TYPE_A").isSelected()) {
			table_m.setVisible(true);
			table_d.setVisible(false);
		} else if (getTRadioButton("TYPE_B").isSelected()) {
			table_m.setVisible(false);
			table_d.setVisible(true);
		}
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		int row = 0;
		TParm selParm = new TParm();
		if (table_d.isVisible()) {
			row = table_d.getSelectedRow();
			selParm = table_d.getParmValue().getRow(row);
		} else {
			row = table_m.getSelectedRow();
			selParm = table_m.getParmValue().getRow(row);
		}
		getTextField("ORDER_CODE").setValue(selParm.getValue("ORDER_CODE"));
		getTextField("ORDER_DESC").setValue(selParm.getValue("ORDER_DESC"));
	}

	/**
	 * ��ʼ����ѯʱ��
	 */
	public void OnInitTime() {
		// ��ʼ����ѯʱ��
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// ����ʱ��
		Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date)
				.substring(0, 4)
				+ "/"
				+ TypeTool.getString(date).substring(5, 7)
				+ "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (�ϸ������һ��)
		setValue("END_TIME", StringTool.rollDate(dateTime, -1));
		// ��ʼʱ��(�ϸ��µ�һ��)
		setValue("START_TIME", StringTool.rollDate(dateTime, -1).toString()
				.substring(0, 4)
				+ "/"
				+ StringTool.rollDate(dateTime, -1).toString().substring(5, 7)
				+ "/01 00:00:00");
	}

	/**
	 * ��ȡTABLE�ؼ�
	 */
	public TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡTRadioButton�ؼ�
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * ��ȡTTextField�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

}
