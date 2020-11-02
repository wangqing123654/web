package com.javahis.ui.mro;

import java.sql.Timestamp;
import java.util.Date;

import jdo.mro.MROInfectMagTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 传染病报告卡管理界面控制
 * </p>
 * 
 * <p>
 * Description: 传染病报告卡管理界面控制
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
 * @author zhangh 2013-11-04
 * @version 1.0
 */
public class MROInfectMagControl extends TControl {

	/**
	 * 初始化方法
	 */
	public void onInit() {
		onInitComponent();
		// 设置弹出菜单
		getTextField("DIAG")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// 定义接受返回值方法
		getTextField("DIAG").addEventListener(TPopupMenuEvent.RETURN_VALUE,
				this, "getDiagReturn");
		this.setValue("ADM_TYPE", "O");
		onChangeAdmType();
	}

	/**
	 * 取得界面TTextField
	 * 
	 * @param tag
	 *            String
	 * @return TTextField
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 初始化数据
	 */
	public void onInitComponent() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_DATE_4", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_DATE_4", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 23:59:59");
	}

	/**
	 * 查询动作
	 */
	public void onQuery() {
		String mrNo = getValueString("MR_NO_4");
		String admType = this.getValueString("ADM_TYPE");
		if(null == admType || "".equals(admType)){
			this.messageBox("门急住别不能为空！");
			return;
		}
		TParm parm = new TParm();
		if (getValueString("START_DATE_4").length() != 0)
			parm.setData("START_DATE", getValueString("START_DATE_4")
					.substring(
							0,
							this.getValueString("START_DATE_4")
									.lastIndexOf(".")).replace("-", "")
					.replace(":", "").replace(" ", ""));
		if (getValueString("END_DATE_4").length() != 0)
			parm.setData("END_DATE", getValueString("END_DATE_4").substring(0,
					this.getValueString("END_DATE_4").lastIndexOf("."))
					.replace("-", "").replace(":", "").replace(" ", ""));
		if (getValueString("MR_NO_4").length() != 0)
			parm.setData("MR_NO", mrNo);
		if (getValueString("REPORT_START_DATE").length() != 0)
			parm.setData("REPORT_START_DATE", getValueString(
					"REPORT_START_DATE").substring(0,
					this.getValueString("REPORT_START_DATE").lastIndexOf("."))
					.replace("-", "").replace(":", "").replace(" ", ""));
		if (getValueString("REPORT_END_DATE").length() != 0)
			parm.setData("REPORT_END_DATE", getValueString("REPORT_END_DATE")
					.substring(
							0,
							this.getValueString("REPORT_END_DATE").lastIndexOf(
									".")).replace("-", "").replace(":", "")
					.replace(" ", ""));
		if (getValueString("START_DATE_4").length() != 0
				&& getValueString("END_DATE_4").length() != 0
				&& (StringTool.getDateDiffer(
						(Timestamp) getValue("START_DATE_4"),
						(Timestamp) getValue("END_DATE_4")) > 0)) {
			messageBox("录入的日期不合法");
			return;
		}

		if (getValue("REP_Y_4").equals("Y"))
			parm.setData("REPORT_DATE_NOT_NULL", "Y");
		else if (getValue("REP_N_4").equals("Y"))
			parm.setData("REPORT_DATE_NULL", "Y");
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		if (null != getValue("DIAG") && getValueString("DIAG").length() > 0)
			parm.setData("ICD_CODE", getValueString("DIAG"));
		if (null != getValue("DEPT_4") && getValueString("DEPT_4").length() > 0)
			parm.setData("DEPT_CODE", getValueString("DEPT_4"));
		if(admType.equals("I")){
			if (null != getValue("STATION_4")
					&& getValueString("STATION_4").length() > 0)
				parm.setData("STATION_CODE", getValueString("STATION_4"));
		}else{
			if (null != getValue("CLINICAREA_CODE")
					&& getValueString("CLINICAREA_CODE").length() > 0)
				parm.setData("CLINICAREA_CODE", getValueString("CLINICAREA_CODE"));
		}
		parm.setData("ADM_TYPE", admType);

		parm = MROInfectMagTool.getInstance().selectInfectReport(parm);
		getTable("TABLE_1_4").removeRowAll();
		if (parm.getErrCode() < 0)
			return;
		if (parm.getCount() <= 0)
			return;
		getTable("TABLE_1_4").setParmValue(parm);
	}

	/**
	 * 取得Table控件
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}

	/**
	 * 取得数据库访问类
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 保存动作
	 */
	public void onSave() {
		boolean flg = false;
		TTable table = getTable("TABLE_1_4");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (tableParm.getValue("SELECT_FLG", i).equals("Y")) {
				if (tableParm.getValue("REPORT_FLG", i).equals("Y")) {
					this.messageBox("所选记录中有已上传记录！");
					return;
				}
				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("CARD_SEQ_NO", tableParm
						.getValue("CARD_SEQ_NO", i));
				parm.addData("UPLOAD_DATE", SystemTool.getInstance().getDate()
						.toString().substring(
								0,
								SystemTool.getInstance().getDate().toString()
										.lastIndexOf(".")).replace("-", "")
						.replace(":", "").replace(" ", ""));
				parm.addData("UPLOAD_CODE", Operator.getID());
				parm.addData("PAD_DEPT", Operator.getDept());
				flg = true;
			}
			if (i == table.getRowCount() - 1 && !flg) {
				messageBox("请选择需要保存的记录！");
				return;
			}
		}
		System.out.println(parm);
		parm = MROInfectMagTool.getInstance().updateInfectReport(parm);
		if (parm.getErrCode() < 0) {
			messageBox("保存失败");
			return;
		}
		messageBox("保存成功");
		onQuery();
	}


	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void getDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			getTextField("DIAG").setValue(icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc))
			getTextField("DIAG_DESC").setValue(icdDesc);
	}

	/**
	 * 更新取消上传动作
	 */
	public void onDelete() {
		boolean flg = false;
		TTable table = getTable("TABLE_1_4");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (tableParm.getValue("SELECT_FLG", i).equals("Y")) {
				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("CARD_SEQ_NO", tableParm
						.getValue("CARD_SEQ_NO", i));
				parm.addData("UPLOAD_DATE", new TNull(Date.class));
				parm.addData("UPLOAD_CODE", "");
				parm.addData("PAD_DEPT", "");
				flg = true;
			}
			if (i == table.getRowCount() - 1 && !flg) {
				messageBox("请选择需要删除的记录!");
				return;
			}
		}
		parm = MROInfectMagTool.getInstance().updateInfectReportCancel(parm);
		if (parm.getErrCode() < 0) {
			messageBox("取消上传失败");
			return;
		}
		messageBox("取消上传成功");
		// onClear();
		onQuery();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		setValue("ADM_TYPE", "O");
		onChangeAdmType();
		setValue("REP_ALL_4", "Y");
		setValue("REP_Y_4", "N");
		setValue("REP_N_4", "N");
		setValue("DEPT_4", "");
		setValue("STATION_4", "");
		setValue("MR_NO_4", "");
		setValue("DIAG", "");
		setValue("DIAG_DESC", "");
		setValue("REPORT_START_DATE", "");
		setValue("REPORT_END_DATE", "");
		setValue("CLINICAREA_CODE", "");
		onInitComponent();
		getTable("TABLE_1_4").removeRowAll();
	}

	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 门急住类别值改变方法
	 */
	public void onChangeAdmType() {
		if (this.getValueString("ADM_TYPE").equals("I")) {
			this.getTTable("TABLE_1_4").setHeader(
				"选,30,boolean;已上传,60,boolean;科室,120,DEPT_4;病区,120,STATION_4;" +
				"床号,120;住院号,120;病案号,120;姓名,120;入院日期,120;" +
				"经治医生,120,USER_ID;上传日期,120;填报科室,120,DEPT_4");
			this.getTTable("TABLE_1_4").setParmMap(
				"SELECT_FLG;REPORT_FLG;DEPT_CODE;STATION_CODE;BED_NO;IPD_NO;MR_NO;" +
				"PAT_NAME;IN_DATE;VS_DR_CODE;UPLOAD_DATE;PAD_DEPT;CARD_SEQ_NO");
			this.getTTable("TABLE_1_4").setColumnHorizontalAlignmentData(
				"2,left;3,left;4,left;5,left ;6,left;7,left;8,left;9,left;10,left;11,left");
			((TLabel)this.getComponent("tLabel_1")).setVisible(false);
			((TComboBox)this.getComponent("CLINICAREA_CODE")).setVisible(false);
			((TLabel)this.getComponent("tLabel_65")).setVisible(true);
			((TTextFormat)this.getComponent("STATION_4")).setVisible(true);
				
		} else {
			this.getTTable("TABLE_1_4").setHeader(
				"选,30,boolean;已上传,60,boolean;科室,120,DEPT_4;" +
				"诊区,120,CLINICAREA_CODE;病案号,120;姓名,120;挂号日期,120;" +
				"看诊医生,120,USER_ID;上传日期,120;填报科室,120,DEPT_4");
			this.getTTable("TABLE_1_4").setParmMap(
				"SELECT_FLG;REPORT_FLG;DEPT_CODE;CLINICAREA_CODE;MR_NO;" +
				"PAT_NAME;REG_DATE;REALDR_CODE;UPLOAD_DATE;PAD_DEPT;CARD_SEQ_NO");
			this.getTTable("TABLE_1_4").setColumnHorizontalAlignmentData(
				"2,left;3,left;4,left;5,left ;6,left;7,left;8,left;9,left");
			((TLabel)this.getComponent("tLabel_1")).setVisible(true);
			((TComboBox)this.getComponent("CLINICAREA_CODE")).setVisible(true);
			((TLabel)this.getComponent("tLabel_65")).setVisible(false);
			((TTextFormat)this.getComponent("STATION_4")).setVisible(false);
		}
	}
	
	/**
	 * 病案号回车方法
	 */
	public void onQueryByMrNo(){
		String mrNo = getValueString("MR_NO_4");
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO_4", mrNo);
		onQuery();
	}
}
