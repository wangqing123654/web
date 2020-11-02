package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDSQL;
import jdo.spc.SPCOpiDrugApplicationTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 手术介入麻精备药申请Control
 * </p>
 * 
 * <p>
 * Description: 手术介入麻精备药申请Control
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
 * @author shendr 2013.07.29
 * @version 1.0
 */
public class SPCOpiDrugApplicationControl extends TControl {

	// 主项表格
	private TTable tableM;

	// 细项表格
	private TTable tableD;

	// 申请单号
	private String request_no;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		/**
		 * 权限控制 权限1:只显示自已所属科室 权限9:最大权限,显示全院药库部门
		 */
		// 判断是否显示全院药库部门
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrg()));
			getComboBox("APP_ORG_CODE").setParmValue(parm);
			if (parm.getCount("NAME") > 0) {
				getComboBox("APP_ORG_CODE").setSelectedIndex(1);
			}
			// 预设归属库房getINDORG
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// 初始化TABLE
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String START_DATE = this.getValueString("START_DATE");
		String END_DATE = this.getValueString("END_DATE");
		if (!StringUtil.isNullString(START_DATE)) {
			START_DATE = START_DATE.substring(0, 19);
			parm.setData("START_DATE", START_DATE);
		}
		if (!StringUtil.isNullString(END_DATE)) {
			END_DATE = END_DATE.substring(0, 19);
			parm.setData("END_DATE", END_DATE);
		}
		parm.setData("APP_ORG_CODE", this.getValue("APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", this.getValue("TO_ORG_CODE"));
		parm.setData("REQUEST_NO", this.getValue("REQUEST_NO"));
		// 判断是麻精还是普药
		if (getTRadioButton("DRUG").isSelected()) {
			parm.setData("CTRL_FLG", "Y");
		} else {
			parm.setData("CTRL_FLG", "N");
		}
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// 查询判断
		if (this.getTRadioButton("REQUEST_FLG_A").isSelected()) {// 已申请
			parm.setData("REQUEST_FLG", "Y");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// 明细
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecord(parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecordM(parm);
			}
		} else {// 未申请
			parm.setData("REQUEST_FLG", "N");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// 明细
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecord(parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecordM(parm);
			}
		}
		if ("M".equals(parm.getData("REQUEST_TYPE"))) {
			if (resultM.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableM.setParmValue(resultM);
				tableM.setVisible(true);
				tableD.setVisible(false);
			}
		} else {
			if (resultD.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableD.setParmValue(resultD);
				tableM.setVisible(false);
				tableD.setVisible(true);
			}
		}
		((TCheckBox) getComponent("SELECT_ALL")).setSelected(true);
	}

	/**
	 * 自动生成申请单号
	 */
	public void onSave() {
		TParm parm = new TParm();
		// 整理数据，申请单主项
		getRequestExmParmM(parm);
		// 整理数据，申请单细项
		getRequestExmParmD(parm);
		TParm result = new TParm();
		// 调用物联网接口方法
		result = TIOM_AppServer.executeAction("action.spc.INDRequestAction",
				"onCreateDeptOpiRequestSpc", parm);
		String msg = "";
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			// this.messageBox(result.getErrText());
			String errText = result.getErrText();
			String[] errCode = errText.split(";");
			for (int i = 0; i < errCode.length; i++) {
				String orderCode = errCode[i];
				TParm returnParm = SYSFeeTool.getInstance().getFeeAllData(
						orderCode);
				if (returnParm != null && returnParm.getCount() > 0) {
					returnParm = returnParm.getRow(0);
					msg += orderCode + " " + returnParm.getValue("ORDER_DESC")
							+ "  " + returnParm.getValue("SPECIFICATION")
							+ "\n";
					if (i == errCode.length - 1) {
						msg += "不存在物联网药品对照编码";
					}
				} else {
					msg += orderCode + "\n";
				}
			}
			this.messageBox(msg);
			return;
		}
		this.messageBox("P0001");
		onPrint();
		onClear();
		onQuery();
	}

	/**
	 * 整理数据，申请单主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "TEC");
		inparm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
		inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", Operator.getID());
		inparm.setData("REASON_CHN_DESC", this
				.getValueString("REASON_CHN_DESC"));
		inparm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("DRUG_CATEGORY", "2");
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * 整理数据，申请单细项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		int count = 0;
		for (int i = 0; i < tableM.getRowCount(); i++) {
			if ("N".equals(tableM.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", tableM.getParmValue().getValue(
					"ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("QTY", tableM.getItemDouble(i, "DOSAGE_QTY"));
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", user_id);
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", user_ip);
			inparm.addData("EXEC_DEPT_CODE", getValueString("APP_ORG_CODE"));
			inparm.addData("START_DATE", formatString(this
					.getValueString("START_DATE")));
			inparm.addData("END_DATE", formatString(this
					.getValueString("END_DATE")));
			count++;
		}
		inparm.setCount(count);
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * 打印申请单
	 */
	public void onPrint() {
		if (tableM.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("TITLE", "TEXT", "手术介入麻精备药申请统计表");
		data.setData("DATE", "TEXT", "统计时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// 申请单号
		data.setData("REQUEST_NO", "TEXT", "申请单号:" + request_no);
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = tableM.getParmValue();
		// 遍历表格中的元素
		for (int i = 0; i < tableM.getRowCount(); i++) {
			if ("N".equals(tableM.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm
					.getValue("SPECIFICATION", i));
			parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
			parm.addData("UNIT_CHN_DESC", tableParm
					.getValue("UNIT_CHN_DESC", i));
		}
		// 总行数
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		// 将表格放到容器中
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
		// 调用打印方法
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\spc\\SPCOpiDrugApplication.jhw", data);
	}

	/**
	 * 格式化字符串(时间格式)
	 * 
	 * @param arg
	 *            String
	 * @return String YYYYMMDDHHMMSS
	 */
	private String formatString(String arg) {
		arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10)
				+ arg.substring(11, 13) + arg.substring(14, 16)
				+ arg.substring(17, 19);
		return arg;
	}

	/**
	 * 全选
	 */
	public void onSelectAll() {
		String flg = "N";
		if (getTCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "SELECT_FLG", flg);
		}
		for (int i = 0; i < tableD.getRowCount(); i++) {
			tableD.setItem(i, "SELECT_FLG", flg);
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		tableM.setVisible(true);
		tableM.removeRowAll();
		tableD.setVisible(false);
		tableD.removeRowAll();
		// 清空画面内容
		String clearString = "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
				+ "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
				+ "PRICE_DIFFERENCE";
		clearValue(clearString);
		getTRadioButton("REQUEST_FLG_B").setSelected(true);
		getTRadioButton("REQUEST_TYPE_A").setSelected(true);
	}

	/**
	 * 得到TRadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getTCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

}
