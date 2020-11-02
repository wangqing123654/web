package com.javahis.ui.fol;

import jdo.fol.FOLParmaSettingTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:��ò����趨Control��
 * </p>
 * 
 * <p>
 * Description:��ò����趨Control��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-03-19
 * @version 1.0
 */
public class FOLParmaSettingControl extends TControl {

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ����
	 */
	public void onSave() {
		// ��װ���µĲ���
		String auto_remind_flg = this.getValueString("AUTO_REMIND_FLG");
		String before_remind_day = this.getValueString("BEFORE_REMIND_DAY");
		String followup_deline = this.getValueString("FOLLOWUP_DELINE");
		TParm parm = new TParm();
		// �Ƿ��Զ����ѣ�N��Y��
		parm.setData("AUTO_REMIND_FLG", auto_remind_flg);
		// ��ǰ��������
		parm.setData("BEFORE_REMIND_DAY", before_remind_day);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// ��ʱ���������������ڼ�ȥԤ��������ڣ�������ڸ��������ǲ���ʱ���
		parm.setData("FOLLOWUP_DELINE", followup_deline);
		TParm result = FOLParmaSettingTool.getInstance().updateFolParm(parm);
		if (result.getErrCode() < 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
	}

	/**
	 * �����ʼ��
	 */
	private void initPage() {
		// ��ʼ�����������
		TParm result = FOLParmaSettingTool.getInstance().queryFolParm();
		this.setValue("AUTO_REMIND_FLG", result.getValue("AUTO_REMIND_FLG", 0));
		this.setValue("BEFORE_REMIND_DAY", result.getValue("BEFORE_REMIND_DAY",0));
		this.setValue("FOLLOWUP_DELINE", result.getValue("FOLLOWUP_DELINE", 0));
	}

}
