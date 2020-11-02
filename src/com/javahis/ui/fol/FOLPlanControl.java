package com.javahis.ui.fol;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.fol.FOLPlanTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:随访方案维护Control类
 * </p>
 * 
 * <p>
 * Description:随访方案维护Control类
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
 * @author shendr 2014-02-24
 * @version 1.0
 */
public class FOLPlanControl extends TControl {

	/** 随访方案 */
	private TTable table_plan;
	/** 随访阶段 */
	private TTable table_plan_phase;
	/** 是否是随访阶段保存 */
	private boolean isOnNew = false;
	/** 更新之前的PHASE_SEQ */
	private List<String> list = new ArrayList<String>();

	/**
	 * 界面初始化
	 */
	public void onInit() {
		super.onInit();
		table_plan = getTable("TABLE_PLAN");
		table_plan_phase = getTable("TABLE_PLAN_PHASE");
		setValue("DEPT_CODE", Operator.getDept());
	}

	/**
	 * 保存
	 */
	public void onSave() {
		TParm parm = new TParm();
		// 公共参数
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm result = new TParm();
		if (table_plan.getSelectedRow() >= 0) {
			if (isOnNew) {
				isOnNew = false;
				for (int i = 0; i < table_plan_phase.getRowCount(); i++) {
					// 数据封装-随访阶段-INSERT
					table_plan.acceptText();
					table_plan_phase.acceptText();
					int plan_selected_row = table_plan.getSelectedRow();
					String plan_code = table_plan.getParmValue().getRow(
							plan_selected_row).getValue("PLAN_CODE");
					int phase_seq = Integer.parseInt(table_plan_phase
							.getItemString(i, "PHASE_SEQ"));
					TParm isExistIn = new TParm();
					isExistIn.setData("PLAN_CODE", plan_code);
					isExistIn.setData("PHASE_SEQ", phase_seq);
					TParm isExistOut = FOLPlanTool.getInstance()
							.queryPlanPhaseByIndex(parm);
					if (isExistOut.getInt("COUNTS") != 0)
						continue;
					String phase_desc = table_plan_phase.getItemString(i,
							"PHASE_DESC");
					String dis = "";
					int distance_days = 0;
					if (!("".equals(table_plan_phase.getItemString(i,
							"DISTANCE_DAYS")) || table_plan_phase
							.getItemString(i, "DISTANCE_DAYS") == null)) {
						distance_days = Integer.parseInt(table_plan_phase
								.getItemString(i, "DISTANCE_DAYS"));
						parm.setData("DISTANCE_DAYS", distance_days);
					} else {
						dis = table_plan_phase
								.getItemString(i, "DISTANCE_DAYS");
						parm.setData("DISTANCE_DAYS", dis);
					}
					String description = table_plan_phase.getItemString(i,
							"DESCRIPTION");
					parm.setData("PLAN_CODE", plan_code);
					parm.setData("PHASE_SEQ", phase_seq);
					parm.setData("PHASE_DESC", phase_desc);
					parm.setData("DESCRIPTION", description);
					parm.setData("OPT_USER", Operator.getID());
					parm.setData("OPT_DATE", date.toString().substring(0, 10));
					parm.setData("OPT_TERM", Operator.getIP());
					result = FOLPlanTool.getInstance().savePlanPhase(parm);
				}
			} else {
				// 数据封装-随访阶段/随访方案-UPDATE
				TParm planParm = new TParm();
				TParm planPhaseParm = new TParm();
				String plan_code = table_plan.getParmValue().getRow(
						table_plan.getSelectedRow()).getValue("PLAN_CODE");
				planParm.setData("PLAN_CODE", plan_code);
				planParm.setData("PLAN_DESC", this.getValue("PLAN_DESC"));
				planParm.setData("ENG_DESC", this.getValue("ENG_DESC"));
				planParm.setData("PY1", this.getValue("PY1"));
				planParm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
				planParm.setData("DEPT_DESC", this.getText("DEPT_CODE"));
				planParm.setData("PLAN_LENGTH", this.getValue("PLAN_LENGTH"));
				planParm.setData("SEQ", this.getValue("SEQ"));
				planParm.setData("FOL_PURPOSE", this.getValue("FOL_PURPOSE"));
				planParm.setData("START_DATE", this
						.getValueString("START_DATE").substring(0, 10));
				planParm.setData("END_DATE", this.getValueString("END_DATE")
						.substring(0, 10));
				String status = "";
				// 0表示未启用，1表示启用，2表示终止
				if (getRadioButton("STATUS_0").isSelected()) {
					status = "0";
				} else if (getRadioButton("STATUS_1").isSelected()) {
					status = "1";
				} else {
					status = "2";
				}
				planParm.setData("STATUS", status);
				planParm.setCount(1);
				parm.setData("PLAN_PARM", planParm.getData());
				for (int i = 0; i < list.size(); i++) {
					planPhaseParm.addData("PHASE_SEQ", list.get(i));
				}
				table_plan_phase.acceptText();
				for (int i = 0; i < table_plan_phase.getRowCount(); i++) {
					planPhaseParm.addData("PLAN_CODE", plan_code);
					planPhaseParm.addData("PHASE_SEQ_UPDATE", table_plan_phase
							.getItemString(i, "PHASE_SEQ"));
					planPhaseParm.addData("PHASE_DESC", table_plan_phase
							.getItemString(i, "PHASE_DESC"));
					planPhaseParm.addData("DISTANCE_DAYS", table_plan_phase
							.getItemString(i, "DISTANCE_DAYS"));
					planPhaseParm.addData("DESCRIPTION", table_plan_phase
							.getItemString(i, "DESCRIPTION"));
				}
				planPhaseParm.setCount(table_plan_phase.getRowCount());
				parm.setData("PLAN_PHASE_PARM", planPhaseParm.getData());
				result = TIOM_AppServer.executeAction(
						"action.fol.FOLPlanAction", "onUpdate", parm);
			}
		} else {
			if (!checkCondiction())
				return;
			// 数据封装-随访方案-INSERT
			String plan_code = SystemTool.getInstance().getNo("ALL", "FOL",
					"PLAN_CODE", "PLAN_CODE");
			String plan_desc = this.getValueString("PLAN_DESC");
			String eng_desc = this.getValueString("ENG_DESC");
			String py1 = this.getValueString("PY1");
			String dept_code = this.getValueString("DEPT_CODE");
			String dept_desc = this.getText("DEPT_CODE");
			String fol_purpose = this.getValueString("FOL_PURPOSE");
			String start_date = this.getValueString("START_DATE").substring(0,
					10);
			String end_date = this.getValueString("END_DATE").substring(0, 10);
			int plan_length = this.getValueInt("PLAN_LENGTH");
			int seq = this.getValueInt("SEQ");
			String status = "";
			// 0表示未启用，1表示启用，2表示终止
			if (getRadioButton("STATUS_0").isSelected()) {
				status = "0";
			} else if (getRadioButton("STATUS_1").isSelected()) {
				status = "1";
			} else {
				status = "2";
			}
			parm.setData("PLAN_CODE", plan_code);
			parm.setData("PLAN_DESC", plan_desc);
			parm.setData("ENG_DESC", eng_desc);
			parm.setData("PY1", py1);
			parm.setData("DEPT_CODE", dept_code);
			parm.setData("DEPT_DESC", dept_desc);
			parm.setData("FOL_PURPOSE", fol_purpose);
			parm.setData("START_DATE", start_date);
			parm.setData("END_DATE", end_date);
			parm.setData("STATUS", status);
			parm.setData("PLAN_LENGTH", plan_length);
			parm.setData("SEQ", seq);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", date.toString().substring(0, 10));
			parm.setData("OPT_TERM", Operator.getIP());
			result = FOLPlanTool.getInstance().savePlan(parm);
		}
		if (result.getErrCode() < 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
		onClear();
		onQuery();
	}

	/**
	 * 保存前检查
	 * 
	 * @return
	 */
	private boolean checkCondiction() {
		String plan_desc = this.getValueString("PLAN_DESC");
		if (StringUtil.isNullString(plan_desc)) {
			messageBox("请填写方案名称");
			return false;
		}
		return true;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm result = FOLPlanTool.getInstance().query(parm);
		if (result.getErrCode() < 0) {
			messageBox("E0008");
			return;
		}
		table_plan.setParmValue(result);
	}

	/**
	 * 随访方案点击事件 获取对应随访阶段
	 */
	public void onTablePlanClicked() {
		int select_plan_row = table_plan.getSelectedRow();
		TParm planParm = table_plan.getParmValue().getRow(select_plan_row);
		// 表格数据带入panel内的对应控件中
		int seq = planParm.getInt("SEQ");
		String plan_desc = planParm.getValue("PLAN_DESC");
		String dept_code = planParm.getValue("DEPT_CODE");
		int plan_length = planParm.getInt("PLAN_LENGTH");
		String fol_purpose = planParm.getValue("FOL_PURPOSE");
		String eng_desc = planParm.getValue("ENG_DESC");
		String py1 = planParm.getValue("PY1");
		String status = planParm.getValue("STATUS");
		String start_date = planParm.getValue("START_DATE");
		String end_date = planParm.getValue("END_DATE");
		this.setValue("SEQ", seq + "");
		this.setValue("PLAN_DESC", plan_desc);
		this.setValue("DEPT_CODE", dept_code);
		this.setValue("PLAN_LENGTH", plan_length + "");
		this.setValue("FOL_PURPOSE", fol_purpose);
		this.setValue("ENG_DESC", eng_desc);
		this.setValue("PY1", py1);
		Timestamp date = StringTool.getTimestamp(start_date, "yyyyMMdd");
		this.setValue("START_DATE", date);
		date = StringTool.getTimestamp(end_date, "yyyyMMdd");
		this.setValue("END_DATE", date);
		if ("0".equals(status)) {
			getRadioButton("STATUS_0").setSelected(true);
		} else if ("1".equals(status)) {
			getRadioButton("STATUS_1").setSelected(true);
		} else if ("2".equals(status)) {
			getRadioButton("STATUS_2").setSelected(true);
		}
		// 查询对应随访阶段数据
		String plan_code = planParm.getValue("PLAN_CODE");
		TParm result = FOLPlanTool.getInstance().queryPlanPhaseByPlanCode(
				plan_code);
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getInt("DISTANCE_DAYS", i) == 0)
				result.setData("DISTANCE_DAYS", i, "");
		}
		table_plan_phase.setParmValue(result);
		onInitArray();
	}

	/**
	 * 初始化集合
	 */
	public void onInitArray() {
		list.removeAll(list);
		for (int i = 0; i < table_plan_phase.getRowCount(); i++) {
			list.add(table_plan_phase.getItemString(i, "PHASE_SEQ"));
		}
	}

	/**
	 * 新增一行随访阶段
	 */
	public void onNew() {
		if (table_plan.getSelectedRow() < 0) {
			messageBox("请选中一条随访方案");
			return;
		}
		table_plan_phase.addRow();
		isOnNew = true;
	}

	/**
	 * 恢复页面初始状态
	 */
	public void onClear() {
		table_plan.removeRowAll();
		table_plan_phase.removeRowAll();
		String tags = "PLAN_DESC;ENG_DESC;PY1;PLAN_LENGTH;FOL_PURPOSE;SEQ;START_DATE;END_DATE";
		clearValue(tags);
		setValue("DEPT_CODE", Operator.getDept());
		getRadioButton("STATUS_0").setSelected(true);
	}

	/**
	 * 方案名称回车事件
	 */
	public void onPlanDescAction() {
		String name = getValueString("PLAN_DESC");
		if (name.length() > 0)
			setValue("PY1", TMessage.getPy(name));
		((TTextField) getComponent("ENG_DESC")).grabFocus();
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
	 * @return
	 */
	private TRadioButton getRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

}
