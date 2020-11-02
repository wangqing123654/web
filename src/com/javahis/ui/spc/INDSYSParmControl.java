package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.*;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import java.util.Date;
import jdo.ind.INDSQL;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;

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
		// �Զ���������
		if (getRadioButton("AUTO_FILL_TYPE_0").isSelected())
			result.setData("AUTO_FILL_TYPE", "1");
		else
			result.setData("AUTO_FILL_TYPE", "2");

		// <-- ����3���ֶε�ά�� update by shendr date:2013.5.24
//		result.setData("IS_AUTO_DRUG", Integer.valueOf(getCheckBox(
//				"IS_AUTO_DRUG").getValue() != "Y" ? 0 : 1));
//		result.setData("TOXIC_LENGTH", getValueString("TOXIC_LENGTH"));
//		result.setData("TOXBOX_LENGTH", getValueString("TOXBOX_LENGTH"));
		// -->

		// ���뵥���Զ�ά��
		String flg = "Y";
		flg = getCheckBox("REUPRICE").getValue();
		result.setData("REUPRICE_FLG", flg);
		// ���⼴���ע��
		flg = getCheckBox("DISCHECK").getValue();
		result.setData("DISCHECK_FLG", flg);
		// ��ҩ�Զ�ά�����ۼ�
		flg = getCheckBox("GPRICE").getValue();
		result.setData("GPRICE_FLG", flg);
		//fux modify �ֵ����
		// ��ҩ�Զ�ά�����ۼ�
		flg = getCheckBox("IS_SEPARATE_REQ").getValue();
		result.setData("IS_SEPARATE_REQ", flg);    
		double price = 0.0D;
		// ��ҩ�ɱ��ӳɱ���(���ۼ�)
		price = TypeTool.getDouble(getNumberTextField("GOWN_COSTRATE")
				.getText());
		result.setData("GOWN_COSTRATE", Double.valueOf(price));
		// ��ҩ�ɱ��ӳɱ���(ҽ����)
		price = TypeTool.getDouble(getNumberTextField("GNHI_COSTRATE")
				.getText());
		result.setData("GNHI_COSTRATE", Double.valueOf(price));
		// ��ҩ�ɱ��ӳɱ���(������߼�)
		price = TypeTool
				.getDouble(getNumberTextField("GOV_COSTRATE").getText());
		result.setData("GOV_COSTRATE", Double.valueOf(price));
		// ��ҩ���ۼ��Զ�����Ϊ���ԷѼ۶�ע��
		flg = getCheckBox("UPDATE_GRETAIL").getValue();
		result.setData("UPDATE_GRETAIL_FLG", flg);
		// ��ҩ�ɱ��ӳɱ���(���ۼ�)
		price = TypeTool.getDouble(getNumberTextField("WOWN_COSTRATE")
				.getText());
		result.setData("WOWN_COSTRATE", Double.valueOf(price));
		// ��ҩ�ɱ��ӳɱ���(ҽ����)
		price = TypeTool.getDouble(getNumberTextField("WNHI_COSTRATE")
				.getText());
		result.setData("WNHI_COSTRATE", Double.valueOf(price));
		// ��ҩ�ɱ��ӳɱ���(������߼�)
		price = TypeTool.getDouble(getNumberTextField("WGOV_COSTRATE")
				.getText());
		result.setData("WGOV_COSTRATE", Double.valueOf(price));
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
		result.setData("PHA_PRICE_FLG", getValueString("PHA_PRICE_FLG"));
		// �龫��������Ų��� TOXIC_STORAGE_ORG
		result
				.setData("TOXIC_STORAGE_ORG",
						getValueString("TOXIC_STORAGE_ORG"));
		// OPT_USER,OPT_DATE,OPT_TERM
		Timestamp date = StringTool.getTimestamp(new Date());
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_DATE", date);
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("DEAF_MON",this.getValueInt("DEAF_MON"));
		
		//�����½�����������ĩ�ֶ� STAT_DAY
		
			result.setData("STAT_START",getValue("STAT_START"));
			result.setData("STAT_END",getValue("STAT_END"));
		if("".equals(getValue("STAT_START")) || "".equals(getValue("STAT_END"))){
			result.setData("STAT_FLG", "N");
		}else{
			result.setData("STAT_FLG","Y");
		}
		
		// ��������
		TParm parm = new TParm();
		if ("insert".equals(operation))
			parm = IndSysParmTool.getInstance().onInsert(result);
		else
			parm = IndSysParmTool.getInstance().onUpdate(result);
		if (parm.getErrCode() < 0) {
			messageBox("E0001");
			return;
		} else {
			messageBox("P0001");
			operation = "update";
			return;
		}
	}

	/**
	 * ���ν���
	 */
	public void onUnLock() {
		openDialog("%ROOT%\\config\\spc\\INDBatchLock.x");
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
			case 0: // '\0'
				getRadioButton("UNIT_TYPE_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("UNIT_TYPE_1").setSelected(true);
				break;
			}
			// �Զ�������ʽ
			switch (result.getInt("FIXEDAMOUNT_FLG", 0)) {
			case 0: // '\0'
				getRadioButton("FIXEDAMOUNT_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("FIXEDAMOUNT_1").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("FIXEDAMOUNT_2").setSelected(true);
				break;
			}
			// �Զ���������
			switch (result.getInt("AUTO_FILL_TYPE", 0)) {
			case 1: // '\001'
				getRadioButton("AUTO_FILL_TYPE_0").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("AUTO_FILL_TYPE_1").setSelected(true);
				break;
			}
			// <-- ����3���ֶε�ά�� update by shendr date:2013.5.24
//			if ("1".equals(result.getValue("IS_AUTO_DRUG", 0)))
//				getCheckBox("IS_AUTO_DRUG").setSelected(true);
//			else
//				getCheckBox("IS_AUTO_DRUG").setSelected(false);
//			getTTextField("TOXIC_LENGTH").setValue(
//					result.getValue("TOXIC_LENGTH", 0));
//			getTTextField("TOXBOX_LENGTH").setValue(
//					result.getValue("TOXBOX_LENGTH", 0));
			// -->
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
					Double.valueOf(result.getDouble("GOWN_COSTRATE", 0)));
			// ��ҩ�ɱ��ӳɱ���(ҽ����)
			getNumberTextField("GNHI_COSTRATE").setValue(
					Double.valueOf(result.getDouble("GNHI_COSTRATE", 0)));
			// ��ҩ�ɱ��ӳɱ���(������߼�)
			getNumberTextField("GOV_COSTRATE").setValue(
					Double.valueOf(result.getDouble("GOV_COSTRATE", 0)));
			// ��ҩ���ۼ��Զ�����Ϊ���ԷѼ۶�ע��
			if ("Y".equals(result.getValue("UPDATE_GRETAIL_FLG", 0)))
				getCheckBox("UPDATE_GRETAIL").setSelected(true);
			else
				getCheckBox("UPDATE_GRETAIL").setSelected(false);
			// ��ҩ�ɱ��ӳɱ���(���ۼ�)
			getNumberTextField("WOWN_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WOWN_COSTRATE", 0)));
			// ��ҩ�ɱ��ӳɱ���(ҽ����)
			getNumberTextField("WNHI_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WNHI_COSTRATE", 0)));
			// ��ҩ�ɱ��ӳɱ���(������߼�)
			getNumberTextField("WGOV_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WGOV_COSTRATE", 0)));
			// �����ۼ��Զ�����Ϊ���ԷѼ۶�ע��
			if ("Y".equals(result.getValue("UPDATE_WRETAIL_FLG", 0)))
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(true);
			else
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(false);
			// �ս�״̬
			switch (result.getInt("MANUAL_TYPE", 0)) {
			case 0: // '\0'
				getRadioButton("MANUAL_TYPE_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("MANUAL_TYPE_1").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("MANUAL_TYPE_2").setSelected(true);
				break; 
			}     
			//fux modify 20141030           
			//�ֵ����  
			if ("Y".equals(result.getValue("IS_SEPARATE_REQ", 0)))    
				getCheckBox("IS_SEPARATE_REQ").setSelected(true);   
			else               
				getCheckBox("IS_SEPARATE_REQ").setSelected(false);
			
			// ҩƷ����
			setValue("PHA_PRICE_FLG", result.getValue("PHA_PRICE_FLG", 0));
			setValue("TOXIC_STORAGE_ORG", result.getValue("TOXIC_STORAGE_ORG",
					0));
			setValue("DEAF_MON", result.getInt("DEAF_MON",0));
			// ��ҩ�Զ�ά�����ۼ�(CheckBox)�ı��¼�
			onSelectGprice();
			setValue("STAT_START",result.getValue("STAT_START",0));
			setValue("STAT_END",result.getValue("STAT_END",0));
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

	/**
	 * �õ�TTextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

}