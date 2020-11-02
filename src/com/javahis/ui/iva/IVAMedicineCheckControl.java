package com.javahis.ui.iva;

import java.sql.Timestamp;


import jdo.iva.IVAMedicineCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

import com.dongyang.ui.TTextFormat;

import com.dongyang.util.StringTool;

import com.javahis.system.textFormat.TextFormatSYSOperator;

/**
 * <p>
 * Title: ����ͳҩ�˶�Control
 * </p>
 * 
 * <p>
 * Description: ����ͳҩ�˶�Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2013.07.21
 * @version 1.0
 */
public class IVAMedicineCheckControl extends TControl {
	//��ϸ��
	private TTable table_d;
	//����
	private TTable table_m;

	// �õ�table�ؼ�
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}


	// �õ�RadioButton�ؼ�
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	public void onInit() {
		super.onInit();
		
		initPage();
	}

	public void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		Operator.getID();
		this.setValue("IVA_INTGMED_CHECK_USER", Operator.getID());
		// callFunction("UI|TABLE_1|addEventListener",
		// TTableEvent.CHECK_BOX_CLICKED, this,
		// "onTableCheckBoxClicked");
	}

	/*
	 * ��ѯ
	 */
	public void onQuery() {

		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");
		// �����
		String check_user = this.getValueString("IVA_INTGMED_CHECK_USER");
		// �������
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String station_code = this.getValueString("STATION_CODE");
		// ��Ų�ѯ������ֵ
		TParm tparm = new TParm();
		if (station_code != null && !"".equals(station_code)) {
			tparm.setData("STATION_CODE", station_code);
		}
		if (check_user != null && !"".equals(check_user)) {
			tparm.setData("IVA_INTGMED_CHECK_USER", check_user);
		}
		if(getRadioButton("KIND_UD").isSelected()){
			tparm.setData("KIND", "UD");
		}
		if(getRadioButton("KIND_ST").isSelected()){
			tparm.setData("KIND", "ST");
		}
		if(getRadioButton("KIND_F").isSelected()){
			tparm.setData("KIND", "F");
		}
//		System.out.println("tparm>>>>>>>>>"+tparm);
		if (getRadioButton("BUTTON_1").isSelected()) {
			if ((station_code == null || "".equals(station_code.trim()))) {
				this.messageBox("��ѯ��������ͬʱΪ�գ�");
				return;
			}
			if (start_time != null && !"".equals(start_time)) {
				tparm.setData("START_TIME_N", start_time.toString().substring(0, 19));
			}
			if (end_time != null && !"".equals(end_time)) {
				tparm.setData("END_TIME_N", end_time.toString().substring(0, 19));
			}
//			System.out.println("KIND======"+tparm.getValue("KIND"));
			TParm tParm = IVAMedicineCheckTool.getInstance().queryInfo(tparm);
			if(tParm.getCount() <= 0){
				table_d.removeRowAll();
				table_m.removeRowAll();
				this.messageBox("δ��ѯ������");
				return;
			}
			if (tParm.getCount() == 1) {
				tParm.setData("SELECT_FLG", 0, "Y");
			}
			table_d.setParmValue(tParm);
			if (table_d.getRowCount() == 1) {
				onTableinit();
			}
		}
		if (getRadioButton("BUTTON_2").isSelected()) {
			if ((station_code == null || "".equals(station_code.trim()))
					&& (start_time == null || "".equals(start_time.trim()))
					&& (end_time == null || "".equals(end_time.trim()))
					&& (check_user == null || "".equals(check_user.trim()))) {
				this.messageBox("��ѯ��������ͬʱΪ�գ�");
				return;
			}
			if (start_time != null && !"".equals(start_time)) {
				tparm.setData("START_TIME_Y", start_time.toString().substring(0, 19));
			}
			if (end_time != null && !"".equals(end_time)) {
				tparm.setData("END_TIME_Y", end_time.toString().substring(0, 19));
			}
//			TParm result = IVAMedicineCheckTool.getInstance().queryIn(tparm);
			TParm result = IVAMedicineCheckTool.getInstance().queryInfo(tparm);
			if(result.getCount() <= 0){
				table_d.removeRowAll();
				table_m.removeRowAll();
				this.messageBox("δ��ѯ������");
				return;
			}
			table_d.setParmValue(result);
			if (table_d.getRowCount() == 1) {
				onTableinit();
			}
		}

	}

	/*
	 * ��ѡ��ť�����¼�
	 */
	public void onRidoButton() {
		if (getRadioButton("BUTTON_2").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("IVA_INTGMED_CHECK_USER"))
					.setEnabled(true);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
			this.clearValue("IVA_INTGMED_CHECK_USER");
			initPage();
			onClear();

		}
		if (getRadioButton("BUTTON_1").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("IVA_INTGMED_CHECK_USER"))
					.setEnabled(false);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			initPage();
			onClear();
		}
	}

	/*
	 * ����ֻһ��ʱ��Ĭ�ϴ���ϸ����Ϣ
	 */
	public void onTableinit() {
		// �õ�table�ؼ�

		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");

		// �õ�ѡ����������

		TParm parm = new TParm();
		// �ж�table���Ƿ���ֵ

		// �õ�table��ֵ
		TParm tparm = table_d.getParmValue();

		String tNo = tparm.getValue("INTGMED_NO", 0);
		parm.setData("INTGMED_NO", tNo);
		TParm result = IVAMedicineCheckTool.getInstance().queryInfocheck(parm);

		table_m.setParmValue(result);
	}

	/*
	 * �����¼�
	 */
	public void onTableClicked() {
		// �õ�table�ؼ�
		
		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");
		table_m.removeRowAll();
		// �õ�ѡ����������
		int row = table_d.getSelectedRow();
		TParm tParm = new TParm();
		// �ж�table���Ƿ���ֵ
		if (row != -1) {
			// �õ�table��ֵ
			TParm tparm = table_d.getParmValue();
//			this.setValue("INTGMED_NO", tparm.getValue("INTGMED_NO", row));
			if (getRadioButton("BUTTON_2").isSelected()) {
				this.setValue("IVA_INTGMED_CHECK_USER", tparm.getValue(
						"IVA_INTGMED_CHECK_USER", row));
			}
			this.setValue("STATION_CODE", tparm.getValue("STATION_DESC", row));
			tParm.setData("INTGMED_NO", tparm.getValue("INTGMED_NO", row));
		}
		TParm result = IVAMedicineCheckTool.getInstance().queryInfocheck(tParm);
		table_m.setParmValue(result);
		onTableCheckBoxClicked(this);
	}

	/*
	 *  ��տؼ���ֵ
	 */
	public void onClear() {
//		if (getRadioButton("BUTTON_2").isSelected()) {
//			this.clearValue("IVA_INTGMED_CHECK_USER");
//			this.clearValue("START_TIME");
//			this.clearValue("END_TIME");
//		}
		table_d = this.getTable("TABLE_1");
		table_d.removeRowAll();
		table_m = this.getTable("TABLE_2");
		table_m.removeRowAll();
//		this.clearValue("INTGMED_NO");
		this.clearValue("STATION_CODE");

	}

	/*
	 * �������״̬
	 */
	public void onSave() {
		
		table_d = this.getTable("TABLE_1");
		TParm tableParm = table_d.getParmValue();
		TParm result = new TParm();
		String check_user = this.getValueString("IVA_INTGMED_CHECK_USER");
		if(table_d.getRowCount()==1){
			result.setData("INTGMED_NO",tableParm.getValue("INTGMED_NO",0));
			result.setData("IVA_INTGMED_CHECK_USER",check_user);
			TParm parm = IVAMedicineCheckTool.getInstance().updateInfo(result);
			if (parm.getErrCode() < 0) {
				this.messageBox("���ʧ��");
				return;
			}
			messageBox("��˳ɹ�");
			this.getTable("TABLE_2").removeRowAll();
			table_d.removeRow(0);
		}
		int selectrow = table_d.getSelectedRow();
		// for (int i = 0; i < table_d.getRowCount(); i++) {
		if (check_user != null && !"".equals(check_user)) {
			if ("N".equals(table_d.getItemString(selectrow, "SELECT_FLG"))) {
				return;
			}
			result.setData("INTGMED_NO", tableParm.getValue("INTGMED_NO",selectrow));
			result.setData("IVA_INTGMED_CHECK_USER", check_user);
			TParm parm = IVAMedicineCheckTool.getInstance().updateInfo(result);

			if (parm.getErrCode() < 0) {
				this.messageBox("���ʧ��");
				return;
			}
			messageBox("��˳ɹ�");
			this.getTable("TABLE_2").removeRowAll();
			table_d.removeRow(selectrow);
		}else{
			messageBox("����˲���Ϊ��");
		}
	}

	/*
	 * ��ѡ���¼�
	 */
	public void onTableCheckBoxClicked(Object obj) {
		if (getRadioButton("BUTTON_1").isSelected()) {
			table_d = this.getTable("TABLE_1");
			int column = table_d.getSelectedRow();
			if ("N".equals(table_d.getItemString(column, "SELECT_FLG"))) {
				table_d.setItem(column, "SELECT_FLG", "Y");
			} else {
				table_d.setItem(column, "SELECT_FLG", "N");
			}
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if (i != column) {
					table_d.setItem(i, "SELECT_FLG", "N");
				}
			}

		}
	}

	/*
	 * �س��¼�
	 */
	public void onAction1() {
		onQuery();

	}
}
