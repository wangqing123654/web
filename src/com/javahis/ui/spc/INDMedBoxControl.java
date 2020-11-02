package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDMedBoxTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:����ҩ�ж���ά��
 * </p>
 * 
 * <p>
 * Description:����ҩ�ж���ά��
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
 * @author shendr 2013-05-21
 * @version 1.0
 */
public class INDMedBoxControl extends TControl {

	// ����ȫ��TTable�ؼ�
	private TTable table;

	public void onInit() {
		table = this.getTTable("TABLE");
	}

	/**
	 * ���TTable�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ���TTextField�ؼ�
	 * 
	 * @param tag
	 *            �ؼ�TAG
	 * @return
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * ��ձ����ؼ�
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "ELETAG_CODE;AP_REGION";
		clearValue(tags);
		getTTextField("ELETAG_CODE").setEnabled(true);
	}

	/**
	 * ��񵥻�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		TParm selParm = table.getParmValue().getRow(row);
		setValue("ELETAG_CODE", selParm.getData("ELETAG_CODE"));
		setValue("AP_REGION", selParm.getData("AP_REGION"));
		// �������޸ĵ��ӱ�ǩID
		getTTextField("ELETAG_CODE").setEnabled(false);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String ELETAG_CODE = getValueString("ELETAG_CODE");
		if (!(ELETAG_CODE.length() == 0 || ELETAG_CODE == ""))
			parm.setData("ELETAG_CODE", ELETAG_CODE);
		TParm result = INDMedBoxTool.getInstance().queryInfo(parm);
		table.setParmValue(result);
	}

	/**
	 * ���������
	 */
	public void onSave() {
		if (!checkData())
			return;
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parm = new TParm();
		parm.setData("ELETAG_CODE", getValueString("ELETAG_CODE"));
		parm.setData("AP_REGION", getValueString("AP_REGION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		if (table.getSelectedRow() < 0) {
			TParm result = INDMedBoxTool.getInstance().insertInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
			messageBox("����ɹ�");
		} else {
			parm.setData("ELETAG_CODE", table.getParmValue().getRow(
					table.getSelectedRow()).getData("ELETAG_CODE"));
			TParm result = INDMedBoxTool.getInstance().updateInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("����ʧ��");
				return;
			}
			messageBox("���³ɹ�");
		}
		onClear();
		onQuery();
	}

	/**
	 * ����У��
	 * 
	 * @return
	 */
	public boolean checkData() {
		if (getValueString("ELETAG_CODE").length() == 0
				|| getValueString("ELETAG_CODE") == "")
			return false;
		if (getValueString("AP_REGION").length() == 0
				|| getValueString("AP_REGION") == "")
			return false;
		return true;
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫɾ��������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ELETAG_CODE", (table.getParmValue().getRow(table
				.getSelectedRow())).getData("ELETAG_CODE"));
		TParm result = INDMedBoxTool.getInstance().deleteInfo(parm);
		if (result.getErrCode() < 0) {
			messageBox("ɾ��ʧ��");
			return;
		}
		messageBox("ɾ���ɹ�");
		onClear();
		onQuery();
	}

}