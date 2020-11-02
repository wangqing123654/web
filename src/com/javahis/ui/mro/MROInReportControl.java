package com.javahis.ui.mro;

import java.util.HashMap;
import java.util.Map;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:病历待入库查询报表
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis       
 * </p>
 * 
 * @author wangbin
 * @version 1.0
 */
public class MROInReportControl extends TControl {
	private TTable table;
	private Map<String, String> lendAreaMap;// 诊区病区控件Map数据

	/**
	 * 初始化
	 */

	public void onInit() {
		super.onInit();
		this.onInitPage();
	}

	/**
	 * 初始化界面
	 */
	public void onInitPage() {
		this.setValue("REP_S_DATE", SystemTool.getInstance().getDate()); // 开始时间
		this.setValue("REP_E_DATE", SystemTool.getInstance().getDate()); // 截止时间
		table = (TTable) this.getComponent("TABLE");
		
		// 诊区/病区控件设定
		TTextFormat lendArea = ((TTextFormat)this.getComponent("ADM_AREA_CODE"));
		TParm areaParm = MROQueueTool.getInstance().selectMroLendArea();
		lendArea.setPopupMenuData(areaParm);
		lendArea.setComboSelectRow();
		lendArea.popupMenuShowData();
		
		// 组装诊区病区数据Map
		lendAreaMap = new HashMap<String, String>();
		for (int i = 0; i < areaParm.getCount(); i++) {
			lendAreaMap.put(areaParm.getValue("ID", i), areaParm.getValue("NAME", i));
		}
	}
	
	/**
	 * 数据查询
	 */
	public void onQuery() {
		TParm queryParm = getQueryParm();
		
		if (queryParm.getBoolean("ERR")) {
			return;
		}
		
		TParm result = MROBorrowTool.getInstance().queryMROInReadyData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询待入库数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");    
			return;
		}
		
		// 设置主表显示数据(诊区病区)
		for (int i = 0; i < result.getCount(); i++) {
			result.addData("ADM_AREA_DESC", lendAreaMap
					.get(result.getValue("ADM_AREA_CODE", i)));
		}
		
		table.setParmValue(result);
	}

	/**
	 * 获取查询参数
	 * 
	 * @return
	 */
	public TParm getQueryParm() {
		TParm queryParm = new TParm();
		
		if (StringUtils.isEmpty(getValueString("REP_S_DATE"))) {
			this.messageBox("开始日期不能为空");
			queryParm.setData("ERR", true);
			return queryParm;
		}
		
		if (StringUtils.isEmpty(getValueString("REP_E_DATE"))) {
			this.messageBox("截止日期不能为空");
			queryParm.setData("ERR", true);
			return queryParm;
		}
		
		queryParm.setData("REP_S_DATE", getValueString("REP_S_DATE").substring(
				0, 10).replace("-", "")
				+ "000000");
		queryParm.setData("REP_E_DATE", getValueString("REP_E_DATE").substring(
				0, 10).replace("-", "")
				+ "235959");
		queryParm.setData("DEPT_CODE", getValueString("REP_DEPT_CODE"));
		queryParm.setData("OUT_TYPE", getComboBox("REP_OUT_TYPE").getSelectedID());
		queryParm.setData("ISSUE_CODE", "1");
		queryParm.setData("CAN_FLG", "N");
		
		queryParm.setData("ERR", false);
		return queryParm;

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("REP_DEPT_CODE;REP_OUT_TYPE");
		table.removeRowAll();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "病历待入库数据");
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
