package com.javahis.ui.fol;

import java.sql.Timestamp;

import jdo.fol.FOLAddPatToPlanTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:ѡ����÷���Control��
 * </p>
 * 
 * <p>
 * Description:ѡ����÷���Control��
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
 * @author shendr 2014-02-28
 * @version 1.0
 */
public class FOLAddPatToPlanControl extends TControl {

	/** ��÷��� */
	private TTable table_plan;

	/**
	 * ҳ���ʼ��
	 */
	public void onInit() {
		super.onInit();
		table_plan = getTable("TABLE_PLAN");
		setValue("DEPT_CODE", Operator.getDept());
		onQuery();
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (!saveCheck())
			return;
		// ��ȡҽ��վ������������
		TParm getParm = (TParm) this.getParameter();
		String mr_no = getParm.getValue("MR_NO");
		String in_type = getParm.getValue("IN_TYPE");
		String adm_type = getParm.getValue("ADM_TYPE");
		String adm_date = getParm.getValue("ADM_DATE").substring(0,19);
		String case_no = getParm.getValue("CASE_NO");
		// ��ȡPLAN_CODE
		int selRow = table_plan.getSelectedRow();
		TParm selParm = table_plan.getParmValue().getRow(selRow);
		String plan_code = selParm.getValue("PLAN_CODE");
		Timestamp date = SystemTool.getInstance().getDate();
		// ��һ�����ʱ��
		String expected_fol_date_String = this
				.getValueString("EXPECTED_FOL_DATE");
		Timestamp expected_fol_date_Date = StringTool.getTimestamp(
				expected_fol_date_String, "yyyy-MM-dd hh:mm:ss");
		String xpected_fol_date = "";
		TParm parm = new TParm();
		TParm recordParm = new TParm();
		TParm phaseParm = new TParm();
		recordParm.setData("PLAN_CODE", plan_code);
		// ��װrecordParm
		TParm planParm = FOLAddPatToPlanTool.getInstance()
				.queryPlan(recordParm);
		TParm planPhaseParm = FOLAddPatToPlanTool.getInstance().queryPlanPhase(
				recordParm);
		String start_date = planParm.getValue("START_DATE", 0);
		String end_date = planParm.getValue("END_DATE", 0);
		recordParm.setData("MR_NO", mr_no);
		recordParm.setData("FIRST_DOCTOR", Operator.getID());
		recordParm.setData("STATUS", "0");
		recordParm.setData("START_DATE", start_date.substring(0, 19));
		recordParm.setData("END_DATE", end_date.substring(0, 19));
		recordParm.setData("IN_TYPE", in_type);
		recordParm.setData("ADM_TYPE", adm_type);
		recordParm.setData("ADM_DATE", adm_date);
		recordParm.setData("CASE_NO", case_no);
		recordParm.setData("OPT_USER", Operator.getID());
		recordParm.setData("OPT_DATE", date.toString().substring(0, 19));
		recordParm.setData("OPT_TERM", Operator.getIP());
		recordParm.setCount(1);
		// ��װphaseParm
		for (int i = 0; i < planPhaseParm.getCount(); i++) {
			phaseParm.addData("PLAN_CODE", plan_code);
			phaseParm.addData("MR_NO", mr_no);
			phaseParm.addData("CASE_NO", case_no);
			expected_fol_date_Date = StringTool.rollDate(
					expected_fol_date_Date, planPhaseParm.getInt(
							"DISTANCE_DAYS", i));
			xpected_fol_date = expected_fol_date_Date.toString().substring(0,
					19);
			phaseParm.addData("EXPECTED_FOL_DATE", xpected_fol_date);
			phaseParm
					.addData("PHASE_SEQ", planPhaseParm.getInt("PHASE_SEQ", i));
			phaseParm.addData("OPT_USER", Operator.getID());
			phaseParm.addData("OPT_DATE", date.toString().substring(0, 19));
			phaseParm.addData("OPT_TERM", Operator.getIP());
		}
		phaseParm.setCount(planPhaseParm.getCount());
		// ��װparm
		parm.setData("RECORDPARM", recordParm.getData());
		parm.setData("PHASEPARM", phaseParm.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.fol.FOLAddPatToPlanAction", "onSavePatFolRecord", parm);
		if (result.getErrCode() < 0) {
			messageBox("E0001");
			return;
		}
		messageBox("�û��߼��뷽���ɹ�");
	}

	/**
	 * ����ǰ���
	 * 
	 * @return
	 */
	private boolean saveCheck() {
		if (table_plan.getSelectedRow() < 0) {
			messageBox("��ѡ��Ҫ�������÷���");
			return false;
		}
		if (StringUtil.isNullString(this.getValueString("EXPECTED_FOL_DATE"))) {
			messageBox("��ѡ���һ�����ʱ��");
			return false;
		}
		return true;
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTablePlanClicked() {
		int row = table_plan.getSelectedRow();
		TParm parmValue = table_plan.getParmValue().getRow(row);
		this.setValue("DEPT_CODE", parmValue.getValue("DEPT_CODE"));
		this.setValue("PLAN_DESC", parmValue.getValue("PLAN_DESC"));
		this.setValue("PLAN_LENGTH", parmValue.getValue("PLAN_LENGTH"));
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm result = FOLAddPatToPlanTool.getInstance().queryPlan(parm);
		if (result.getErrCode() < 0) {
			messageBox("E0008");
		}
		table_plan.setParmValue(result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		table_plan.removeRowAll();
		String tags = "PLAN_DESC;ENG_DESC;PLAN_LENGTH;IN_WAY;EXPECTED_FOL_DATE";
		clearValue(tags);
		setValue("DEPT_CODE", Operator.getDept());
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

}
