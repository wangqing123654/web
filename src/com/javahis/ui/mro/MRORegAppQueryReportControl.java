package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.mro.MROBorrowTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:预约挂号数据查询报表
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
public class MRORegAppQueryReportControl extends TControl {
	private TTable table;

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
		// 默认显示第二天日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("REP_S_DATE", SystemTool.getInstance().getDate()); // 开始时间
		this.setValue("REP_E_DATE", tommorwDate); // 截止时间
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * 数据查询
	 */
	public void onQuery() {
		TParm queryParm = getQueryParm();
		
		if (queryParm.getBoolean("ERR")) {
			return;
		}
		
		TParm result = MROBorrowTool.getInstance().queryMRORegAppInfoByDate(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询预约挂号数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("查无数据");    
			return;
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
		queryParm.setData("DR_CODE", getValueString("REP_DR_CODE"));
		// 来源类型(0_预约挂号)
		queryParm.setData("ORIGIN_TYPE", "0");
		queryParm.setData("ADM_TYPE", "O");
		queryParm.setData("CANCEL_FLG", "N");
		
		queryParm.setData("ERR", false);
		return queryParm;

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("REP_DEPT_CODE;REP_DR_CODE");
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
		ExportExcelUtil.getInstance().exportExcel(table, "预约挂号数据");
	}
}
