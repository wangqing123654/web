package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:ҩ������趨
 * </p>
 *
 * <p>
 * Description:ҩ������趨
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.4.22
 * @version 1.0
 */

public class INDSYSParmControl extends TControl {

	private String operation = "insert";

	public INDSYSParmControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// Ȩ���趨

		// ��ʼ��������
		initPage();
	}

	/**
	 * ���淽��
	 *
	 * @return boolean
	 */
	public void onSave() {
		TParm result = new TParm();

		// ����ʹ�õ�λ
		if (getRadioButton("UNIT_TYPE_0").isSelected())
			result.setData("UNIT_TYPE", "0");
		else
			result.setData("UNIT_TYPE", "1");

		// �Զ�������ʽ
		if (getRadioButton("FIXEDAMOUNT_0").isSelected())
			result.setData("FIXEDAMOUNT_FLG", "0");
		else if (getRadioButton("FIXEDAMOUNT_1").isSelected())
			result.setData("FIXEDAMOUNT_FLG", "1");
		else
			result.setData("FIXEDAMOUNT_FLG", "2");

		String flg = "Y";
		// ���뵥���Զ�ά��
		flg = getCheckBox("REUPRICE").getValue();
		result.setData("REUPRICE_FLG", flg);

		// ���⼴���ע��
		flg = getCheckBox("DISCHECK").getValue();
		result.setData("DISCHECK_FLG", flg);

		// ��ҩ�Զ�ά�����ۼ�
		flg = getCheckBox("GPRICE").getValue();
		result.setData("GPRICE_FLG", flg);

		double price = 0.00;
		// ��ҩ�ɱ��ӳɱ���(���ۼ�)
		price = TypeTool.getDouble(getNumberTextField("GOWN_COSTRATE")
				.getText());
		result.setData("GOWN_COSTRATE", price);

		// ��ҩ�ɱ��ӳɱ���(ҽ����)
		price = TypeTool.getDouble(getNumberTextField("GNHI_COSTRATE")
				.getText());
		result.setData("GNHI_COSTRATE", price);

		// ��ҩ�ɱ��ӳɱ���(������߼�)
		price = TypeTool
				.getDouble(getNumberTextField("GOV_COSTRATE").getText());
		result.setData("GOV_COSTRATE", price);

		// ��ҩ���ۼ��Զ�����Ϊ���ԷѼ۶�ע��
		flg = getCheckBox("UPDATE_GRETAIL").getValue();
		result.setData("UPDATE_GRETAIL_FLG", flg);

		// ��ҩ�ɱ��ӳɱ���(���ۼ�)
		price = TypeTool.getDouble(getNumberTextField("WOWN_COSTRATE")
				.getText());
		result.setData("WOWN_COSTRATE", price);

		// ��ҩ�ɱ��ӳɱ���(ҽ����)
		price = TypeTool.getDouble(getNumberTextField("WNHI_COSTRATE")
				.getText());
		result.setData("WNHI_COSTRATE", price);

		// ��ҩ�ɱ��ӳɱ���(������߼�)
		price = TypeTool.getDouble(getNumberTextField("WGOV_COSTRATE")
				.getText());
		result.setData("WGOV_COSTRATE", price);

		// �����ۼ��Զ�����Ϊ���ԷѼ۶�ע��
		flg = getCheckBox("UPDATE_WRETAIL_FLG").getValue();
		result.setData("UPDATE_WRETAIL_FLG", flg);

		// �ս�״̬
		if (getRadioButton("MANUAL_TYPE_0").isSelected())
			result.setData("MANUAL_TYPE", "0");
		else if (getRadioButton("MANUAL_TYPE_1").isSelected())
			result.setData("MANUAL_TYPE", "1");
		else
			result.setData("MANUAL_TYPE", "2");

                // ҩƷ����
                result.setData("PHA_PRICE_FLG", this.getValueString("PHA_PRICE_FLG"));
                            

		// OPT_USER,OPT_DATE,OPT_TERM
		Timestamp date = StringTool.getTimestamp(new Date());
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_DATE", date);
		result.setData("OPT_TERM", Operator.getIP());

		// ��������
		TParm parm = new TParm();
		if ("insert".equals(operation))
			parm = IndSysParmTool.getInstance().onInsert(result);
		else
			parm = IndSysParmTool.getInstance().onUpdate(result);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
                operation = "update";
	}

	/**
	 * ���ν���
	 */
	public void onUnLock() {
		this.openDialog("%ROOT%\\config\\ind\\INDBatchLock.x");
	}

	/**
	 * ��ҩ�Զ�ά�����ۼ�(CheckBox)�ı��¼�
	 */
	public void onSelectGprice() {
		if (getCheckBox("GPRICE").isSelected()) {
			getNumberTextField("GOWN_COSTRATE").setEnabled(true);
			getNumberTextField("GNHI_COSTRATE").setEnabled(true);
			getNumberTextField("GOV_COSTRATE").setEnabled(true);
		} else {
			getNumberTextField("GOWN_COSTRATE").setEnabled(false);
			getNumberTextField("GNHI_COSTRATE").setEnabled(false);
			getNumberTextField("GOV_COSTRATE").setEnabled(false);
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		if (result.getCount() > 0) {
			
			// ����ʹ�õ�λ
			switch (result.getInt("UNIT_TYPE", 0)) {
			case 0:
				getRadioButton("UNIT_TYPE_0").setSelected(true);
				break;
			case 1:
				getRadioButton("UNIT_TYPE_1").setSelected(true);
				break;
			}

			// �Զ�������ʽ
			switch (result.getInt("FIXEDAMOUNT_FLG", 0)) {
			case 0:
				getRadioButton("FIXEDAMOUNT_0").setSelected(true);
				break;
			case 1:
				getRadioButton("FIXEDAMOUNT_1").setSelected(true);
				break;
			case 2:
				getRadioButton("FIXEDAMOUNT_2").setSelected(true);
				break;
			}

			// ���뵥���Զ�ά��
			if ("Y".equals(result.getValue("REUPRICE_FLG", 0)))
				getCheckBox("REUPRICE").setSelected(true);
			else
				getCheckBox("REUPRICE").setSelected(false);

			// ���⼴���ע��
			if ("Y".equals(result.getValue("DISCHECK_FLG", 0)))
				getCheckBox("DISCHECK").setSelected(true);
			else
				getCheckBox("DISCHECK").setSelected(false);

			// ��ҩ�Զ�ά�����ۼ�
			if ("Y".equals(result.getValue("GPRICE_FLG", 0)))
				getCheckBox("GPRICE").setSelected(true);
			else
				getCheckBox("GPRICE").setSelected(false);

			// ��ҩ�ɱ��ӳɱ���(���ۼ�)
			getNumberTextField("GOWN_COSTRATE").setValue(
					result.getDouble("GOWN_COSTRATE", 0));

			// ��ҩ�ɱ��ӳɱ���(ҽ����)
			getNumberTextField("GNHI_COSTRATE").setValue(
					result.getDouble("GNHI_COSTRATE", 0));

			// ��ҩ�ɱ��ӳɱ���(������߼�)
			getNumberTextField("GOV_COSTRATE").setValue(
					result.getDouble("GOV_COSTRATE", 0));

			// ��ҩ���ۼ��Զ�����Ϊ���ԷѼ۶�ע��
			if ("Y".equals(result.getValue("UPDATE_GRETAIL_FLG", 0)))
				getCheckBox("UPDATE_GRETAIL").setSelected(true);
			else
				getCheckBox("UPDATE_GRETAIL").setSelected(false);

			// ��ҩ�ɱ��ӳɱ���(���ۼ�)
			getNumberTextField("WOWN_COSTRATE").setValue(
					result.getDouble("WOWN_COSTRATE", 0));

			// ��ҩ�ɱ��ӳɱ���(ҽ����)
			getNumberTextField("WNHI_COSTRATE").setValue(
					result.getDouble("WNHI_COSTRATE", 0));

			// ��ҩ�ɱ��ӳɱ���(������߼�)
			getNumberTextField("WGOV_COSTRATE").setValue(
					result.getDouble("WGOV_COSTRATE", 0));

			// �����ۼ��Զ�����Ϊ���ԷѼ۶�ע��
			if ("Y".equals(result.getValue("UPDATE_WRETAIL_FLG", 0)))
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(true);
			else
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(false);

			// �ս�״̬
			switch (result.getInt("MANUAL_TYPE", 0)) {
			case 0:
				getRadioButton("MANUAL_TYPE_0").setSelected(true);
				break;
			case 1:
				getRadioButton("MANUAL_TYPE_1").setSelected(true);
				break;
			case 2:
				getRadioButton("MANUAL_TYPE_2").setSelected(true);
				break;
			}
                        // ҩƷ����
                        this.setValue("PHA_PRICE_FLG",
                                      result.getValue("PHA_PRICE_FLG", 0));

                        // ��ҩ�Զ�ά�����ۼ�(CheckBox)�ı��¼�
                        onSelectGprice();
                        operation = "update";
		}
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
	 * �õ�NumberTextField����
	 *
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TNumberTextField getNumberTextField(String tagName) {
		return (TNumberTextField) getComponent(tagName);
	}
}
