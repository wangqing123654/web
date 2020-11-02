package com.javahis.ui.fol;

import java.sql.Timestamp;

import jdo.fol.FOLPatListTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:随访患者列表Control类
 * </p>
 * 
 * <p>
 * Description:随访患者列表Control类
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
public class FOLPatListControl extends TControl {

	/** 患者列表 */
	private TTable table_pat;
	/** 随访日程 */
	private TTable table_schedule;

	/**
	 * 初始化
	 */
	public void onInit() {
		// 初始化TABLE
		table_pat = getTable("TABLE_PAT");
		table_schedule = getTable("TABLE_SCHEDULE");
		initPage();
	}

	/**
	 * 初始化页面
	 */
	private void initPage() {
		Timestamp start_date = SystemTool.getInstance().getDate();
		Timestamp end_date = StringTool.rollDate(start_date, 7);
		this.setValue("START_DATE", start_date);
		this.setValue("END_DATE", end_date);
		this.setValue("DEPT_CODE", Operator.getDept());
		TParm planParm = FOLPatListTool.getInstance().queryFolPlan();
		getTextFormat("PLAN_CODE").setPopupMenuData(planParm);
		getTextFormat("PLAN_CODE").onQuery();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 封装查询条件
		TParm parm = new TParm();
		String dept_code = this.getValueString("DEPT_CODE");
		String start_date = this.getValueString("START_DATE");
		if (!StringUtil.isNullString(start_date)) {
			start_date = start_date.substring(0, 19);
		}
		String end_date = this.getValueString("END_DATE");
		if (!StringUtil.isNullString(end_date)) {
			end_date = end_date.substring(0, 19);
		}
		String plan_code = this.getValueString("PLAN_CODE");
		String mr_no = this.getValueString("MR_NO");
		String status = "";
		if (getRadioButton("STATUS_0").isSelected()) {
			status = "0";
		} else if (getRadioButton("STATUS_1").isSelected()) {
			status = "1";
		} else {
			status = "2";
		}
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("START_DATE", start_date);
		parm.setData("END_DATE", end_date);
		parm.setData("PLAN_CODE", plan_code);
		parm.setData("MR_NO", mr_no);
		parm.setData("STATUS", status);
		TParm result = FOLPatListTool.getInstance().queryPatList(parm);
		if (result.getErrCode() < 0) {
			messageBox("E0008");
			return;
		}
		table_pat.setParmValue(result);
	}

	/**
	 * 结束随访
	 */
	public void onOver() {
		int row = table_pat.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要结束随访的患者");
			return;
		}
		TParm selParm = table_pat.getParmValue().getRow(row);
		TParm result = FOLPatListTool.getInstance().updateStatus(selParm);
		if (result.getErrCode() < 0) {
			messageBox("患者结束随访失败");
			return;
		}
		messageBox("患者结束随访成功");
	}

	/**
	 * 患者列表点击事件
	 */
	public void onTablePatClicked() {
		int row = table_pat.getSelectedRow();
		TParm selParm = table_pat.getParmValue().getRow(row);
		TParm patPhaseParm = FOLPatListTool.getInstance()
				.queryPatPhase(selParm);
		table_schedule.setParmValue(patPhaseParm);
		this.setValue("DEPT_CODE", selParm.getValue("DEPT_CODE"));
		this.setValue("PAT_NAME", selParm.getValue("PAT_NAME"));
		this.setValue("MR_NO", selParm.getValue("MR_NO"));
		this.setValue("PLAN_CODE", selParm.getValue("PLAN_CODE"));
	}

	/**
	 * 调用结构化病例
	 */
	public void onTableScheduleDoubleClicked() {
		int patRow = table_pat.getSelectedRow();
		TParm selPatParm = table_pat.getParmValue().getRow(patRow);
		int row = table_schedule.getSelectedRow();
		TParm selParm = table_schedule.getParmValue().getRow(row);
		TParm parm = new TParm();
		TParm emrParm = new TParm();
		emrParm.setData("MR_CODE", TConfig.getSystemValue("FOLEmrMRCODE"));
		emrParm.setData("CASE_NO", selParm.getValue("CASE_NO"));
		emrParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		emrParm.setData("ADM_DATE", selPatParm.getTimestamp("ADM_DATE"));
		emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
		parm.setData("SYSTEM_TYPE", "FOL");
		parm.setData("ADM_TYPE", selPatParm.getValue("ADM_TYPE"));
		parm.setData("CASE_NO", selParm.getValue("CASE_NO"));
		parm.setData("PAT_NAME", selParm.getValue("PAT_NAME"));
		parm.setData("MR_NO", selParm.getValue("MR_NO"));
		parm.setData("ADM_DATE", selPatParm.getTimestamp("ADM_DATE"));
		parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		parm.setData("EMR_FILE_DATA", emrParm);
		parm.setData("RULETYPE", "2");// 修改权限
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		table_pat.removeRowAll();
		table_schedule.removeRowAll();
		String tags = "PLAN_CODE;MR_NO;PAT_NAME";
		this.clearValue(tags);
		getRadioButton("STATUS_1").setSelected(true);
		initPage();
	}

	/**
	 * 获取Table控件
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获取RadioButton控件
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * 获取TextFormat控件
	 * 
	 * @param tag
	 * @return
	 */
	private TTextFormat getTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

}
