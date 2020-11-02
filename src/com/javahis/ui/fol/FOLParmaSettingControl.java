package com.javahis.ui.fol;

import jdo.fol.FOLParmaSettingTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:随访参数设定Control类
 * </p>
 * 
 * <p>
 * Description:随访参数设定Control类
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
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// 封装更新的参数
		String auto_remind_flg = this.getValueString("AUTO_REMIND_FLG");
		String before_remind_day = this.getValueString("BEFORE_REMIND_DAY");
		String followup_deline = this.getValueString("FOLLOWUP_DELINE");
		TParm parm = new TParm();
		// 是否自动提醒，N否，Y是
		parm.setData("AUTO_REMIND_FLG", auto_remind_flg);
		// 提前几天提醒
		parm.setData("BEFORE_REMIND_DAY", before_remind_day);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		// 及时随访天数，随访日期减去预定随访日期，如果大于该天数则是不及时随访
		parm.setData("FOLLOWUP_DELINE", followup_deline);
		TParm result = FOLParmaSettingTool.getInstance().updateFolParm(parm);
		if (result.getErrCode() < 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
	}

	/**
	 * 界面初始化
	 */
	private void initPage() {
		// 初始化界面的数据
		TParm result = FOLParmaSettingTool.getInstance().queryFolParm();
		this.setValue("AUTO_REMIND_FLG", result.getValue("AUTO_REMIND_FLG", 0));
		this.setValue("BEFORE_REMIND_DAY", result.getValue("BEFORE_REMIND_DAY",0));
		this.setValue("FOLLOWUP_DELINE", result.getValue("FOLLOWUP_DELINE", 0));
	}

}
