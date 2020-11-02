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
 * Title:��û����б�Control��
 * </p>
 * 
 * <p>
 * Description:��û����б�Control��
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

	/** �����б� */
	private TTable table_pat;
	/** ����ճ� */
	private TTable table_schedule;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		// ��ʼ��TABLE
		table_pat = getTable("TABLE_PAT");
		table_schedule = getTable("TABLE_SCHEDULE");
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
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
	 * ��ѯ
	 */
	public void onQuery() {
		// ��װ��ѯ����
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
	 * �������
	 */
	public void onOver() {
		int row = table_pat.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��Ҫ������õĻ���");
			return;
		}
		TParm selParm = table_pat.getParmValue().getRow(row);
		TParm result = FOLPatListTool.getInstance().updateStatus(selParm);
		if (result.getErrCode() < 0) {
			messageBox("���߽������ʧ��");
			return;
		}
		messageBox("���߽�����óɹ�");
	}

	/**
	 * �����б����¼�
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
	 * ���ýṹ������
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
		parm.setData("RULETYPE", "2");// �޸�Ȩ��
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * ���
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
	 * ��ȡTable�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * ��ȡRadioButton�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * ��ȡTextFormat�ؼ�
	 * 
	 * @param tag
	 * @return
	 */
	private TTextFormat getTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

}
