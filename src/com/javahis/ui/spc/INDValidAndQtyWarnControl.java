package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import java.awt.event.KeyEvent;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Calendar;
import java.util.Date;
import com.dongyang.util.TypeTool;

import jdo.spc.INDSQL;
import jdo.spc.IndValidAndQtyWarnTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import jdo.util.Manager;

/**
 * <p>
 * Title: 近效期及库存量提示
 * </p>
 * 
 * <p>
 * Description: 近效期及库存量提示
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2010.10.28
 * @version 1.0
 */
public class INDValidAndQtyWarnControl extends TControl {

	private TPanel panel_0;

	private TTable table_a;

	private TTable table_b;

	public INDValidAndQtyWarnControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 注册激发SYSFeePopup弹出的事件
		callFunction("UI|ORDER_CODE_A|addEventListener", "ORDER_CODE_A->"
				+ TKeyListener.KEY_PRESSED, this, "onCreateEditComoponentUD_A");
		// 注册激发SYSFeePopup弹出的事件
		callFunction("UI|ORDER_CODE_B|addEventListener", "ORDER_CODE_B->"
				+ TKeyListener.KEY_PRESSED, this, "onCreateEditComoponentUD_B");
		panel_0 = getPanel("TPanel_0");
		table_a = getTable("TABLE_A");
		table_b = getTable("TABLE_B");
		String dept_code = Operator.getDept();
		// String station_code = Operator.getStation();
		// TParm resultParm = null;
		// String flg = "Y";//判断主科室在药库部门里有无对应数据，没有就查主病区
		// TParm deptParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(Operator.getRegion())));
		// for (int i = 0; i < deptParm.getCount(); i++) {
		// if (dept_code==deptParm.getValue("ORG_CODE")) {
		// flg = "N";
		// break;
		// }
		// }
		// // 查询部门名称
		// if ("Y".equals(flg)) {
		// resultParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(station_code,
		// Operator.getRegion())));
		// }else {
		// resultParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(dept_code,
		// Operator.getRegion())));
		// }
		// String dept_desc = resultParm.getValue("ORG_CHN_DESC",0);
		this.setValue("ORG_CODE_A", dept_code);
		this.setValue("ORG_CODE_B", dept_code);
		// identify by shendr 20131230 爱育华
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		int deaf_mon = result.getInt("DEAF_MON", 0);
		setValue("DEAF_MON", deaf_mon + "");
		// onQuery(); by liyh 20120810 去掉初始化查询
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		int deaf_mon = this.getValueInt("DEAF_MON");

		if (panel_0.isShowing()) {
			// 部门代码
			org_code = getValueString("ORG_CODE_A");
			if (org_code == null || org_code.length() <= 0) {
				this.messageBox("请选择查询部门");
				return;
			}
			parm.setData("ORG_CODE", org_code);
			String date = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyyMMdd");
			// fux modify 20150911
			// 期限限制
			if (getRadioButton("VALID_DATE_B").isSelected()) {
				deaf_mon = 6;
				parm.setData("VALID_DATE", rollMonth(date.substring(0, 6), date
						.substring(6, 8), deaf_mon));
			} else if (getRadioButton("VALID_DATE_A").isSelected()) {
				deaf_mon = 3;
				parm.setData("VALID_DATE", rollMonth(date.substring(0, 6), date
						.substring(6, 8), deaf_mon));
			} else {
				String valid_date = getValueString("VALID_DATE");
				parm.setData("VALID_DATE", valid_date.substring(0, 4)
						+ valid_date.substring(5, 7)
						+ valid_date.substring(8, 10));
			}
			// messageBox(""+rollMonth(date.substring(0, 6), date.substring(6,
			// 8), deaf_mon));
			// 药品代码
			order_code = getValueString("ORDER_CODE_A");
			if (order_code != null && order_code.length() > 0) {
				parm.setData("ORDER_CODE", order_code);
			}
			// 近效期查询
			result = IndValidAndQtyWarnTool.getInstance().onQueryValid(parm);
			if (result == null || result.getCount("ORDER_CODE") <= 0) {
				this.messageBox("没有查询数据");
				table_a.removeRowAll();
				return;
			}
			table_a.setParmValue(result);
		} else {
			// 部门代码
			org_code = getValueString("ORG_CODE_B");
			if (org_code == null || org_code.length() <= 0) {
				this.messageBox("请选择查询部门");
				return;
			}
			parm.setData("ORG_CODE", org_code);
			// 药品代码
			order_code = getValueString("ORDER_CODE_B");
			if (order_code != null && order_code.length() > 0) {
				parm.setData("ORDER_CODE", order_code);
			}
			// 库存量
			if (getRadioButton("STOCK_QTY_A").isSelected()) {
				parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
			} else if (getRadioButton("STOCK_QTY_B").isSelected()) {
				parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
			} else {
				parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
			}
			// 库存量查询
			result = IndValidAndQtyWarnTool.getInstance().onQueryQty(parm);
			if (result == null || result.getCount("ORDER_CODE") <= 0) {
				this.messageBox("没有查询数据");
				table_b.removeRowAll();
				return;
			}
			table_b.setParmValue(result);
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		if (panel_0.isShowing()) {
			getRadioButton("VALID_DATE_B").setSelected(true);
			getTextFormat("VALID_DATE").setEnabled(false);
			getTextField("DEAF_MON").setEnabled(true);
			this.clearValue("VALID_DATE;ORG_CODE_A;ORDER_CODE_A;ORDER_DESC_A");
			TParm parmNulla = new TParm();
			table_a.setParmValue(parmNulla);
			// table_a.removeRowAll();
		} else {
			getRadioButton("STOCK_QTY_C").setSelected(true);
			this.clearValue("ORG_CODE_B;ORDER_CODE_B;ORDER_DESC_B");
			TParm parmNullb = new TParm();
			table_b.setParmValue(parmNullb);
			// table_b.removeRowAll();
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		int deaf_mon = result.getInt("DEAF_MON", 0);
		setValue("DEAF_MON", deaf_mon + "");
	}

	/**
	 * 变更单选框
	 */
	public void onChangeRadioButton() {
		if (getRadioButton("VALID_DATE_C").isSelected()) {
			getTextFormat("VALID_DATE").setEnabled(true);
		} else {
			getTextFormat("VALID_DATE").setEnabled(false);
			this.clearValue("VALID_DATE");
		}
		if (getRadioButton("VALID_DATE_B").isSelected()) {
			getTextField("DEAF_MON").setEnabled(true);
		} else {
			getTextField("DEAF_MON").setEnabled(false);
		}
	}

	/**
	 * 当TextField创建编辑控件时长期
	 * 
	 * @param com
	 */
	public void onCreateEditComoponentUD_A(KeyEvent obj) {
		TTextField textFilter = getTextField("ORDER_CODE_A");
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn_A");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn_A(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE_A").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC_A").setValue(order_desc);
	}

	/**
	 * 当TextField创建编辑控件时长期
	 * 
	 * @param com
	 */
	public void onCreateEditComoponentUD_B(KeyEvent obj) {
		TTextField textFilter = getTextField("ORDER_CODE_B");
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn_B");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn_B(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE_B").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC_B").setValue(order_desc);
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
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 得到TPanel对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TPanel getPanel(String tagName) {
		return (TPanel) getComponent(tagName);
	}

	/**
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 根据指定的月份和天数加减计算需要的月份和天数
	 * 
	 * @param Month
	 *            String 制定月份 格式:yyyyMM
	 * @param Day
	 *            String 制定月份 格式:dd
	 * @param num
	 *            String 加减的数量 以月为单位
	 * @return String
	 */
	public String rollMonth(String Month, String Day, int num) {
		if (Month.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(Month, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// 当前月＋num
		cal.add(cal.MONTH, num);
		// 将下个月1号作为日期初始值
		cal.set(cal.DATE, 1);
		Timestamp month = new Timestamp(cal.getTimeInMillis());
		String result = StringTool.getString(month, "yyyyMM");
		String lastDayOfMonth = getLastDayOfMonth(result);
		if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
			result += lastDayOfMonth;
		} else {
			result += Day;
		}
		return result;
	}

	/**
	 * 获取指定月份的最后一天的日期
	 * 
	 * @param date
	 *            String 格式 YYYYMM
	 * @return Timestamp
	 */
	public String getLastDayOfMonth(String date) {
		if (date.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// 当前月＋1，即下个月
		cal.add(cal.MONTH, 1);
		// 将下个月1号作为日期初始值
		cal.set(cal.DATE, 1);
		// 下个月1号减去一天，即得到当前月最后一天
		cal.add(cal.DATE, -1);
		Timestamp result = new Timestamp(cal.getTimeInMillis());
		return StringTool.getString(result, "dd");
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = new TTable();
		if (panel_0.isShowing())
			table = table_a;
		else
			table = table_b;
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "近效期及库存量提示");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		TTable table = new TTable();
		if (panel_0.isShowing()) {
			table = table_a;
			if (table.getRowCount() <= 0) { 
				this.messageBox("没有打印数据");
				return;
			}
			// 打印数据  
			TParm date = new TParm();
			// 表头数据  
			if(getRadioButton("VALID_DATE_A").isSelected()){
				date.setData("TITLE", "TEXT",  "药品近效期提示报表(三个月)");
			}else if(getRadioButton("VALID_DATE_B").isSelected()){
				date.setData("TITLE", "TEXT",  "药品近效期提示报表(六个月)");  
			}else{
				date.setData("TITLE", "TEXT",  "药品近效期提示报表(截止至" + ""+this.getValueString("VALID_DATE").substring(0, 10)+")");
			}
			    
//			String start_date = getValueString("START_DATE");
//			String end_date = getValueString("END_DATE");
//			date.setData("DATE_AREA", "TEXT", "统计区间: "
//					+ start_date.substring(0, 4) + "/"
//					+ start_date.substring(5, 7) + "/"
//					+ start_date.substring(8, 10) + " "
//					+ start_date.substring(11, 13) + ":"
//					+ start_date.substring(14, 16) + ":"
//					+ start_date.substring(17, 19) + " ~ "
//					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
//					+ "/" + end_date.substring(8, 10) + " "
//					+ end_date.substring(11, 13) + ":"
//					+ end_date.substring(14, 16) + ":"    
//					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "制表日期: "      
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE_A"));
			date.setData("ORG_CODE", "TEXT", "部门: " +orgDesc );
			
			// 表格数据
			TParm parm = new TParm();
			TParm tableParm = table.getParmValue();
			// ORDER_CODE;ORDER_DESC;SPECIFICATION;DOSE_CHN_DESC;STOCK_QTY;UNIT_CHN_DESC;BATCH_NO;VALID_DATE;SUP_CHN_DESC
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));  
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("DOSE_CHN_DESC", tableParm.getValue(
						"DOSE_CHN_DESC", i));
				parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(
						"UNIT_CHN_DESC", i));
				parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", i));
				parm.addData("VALID_DATE", tableParm.getValue("VALID_DATE", i).subSequence(0, 10));
				parm.addData("SUP_CHN_DESC", tableParm.getValue("SUP_CHN_DESC",
						i));

			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
			parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
			parm.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
			date.setData("TABLE", parm.getData());  
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\SPC\\INDVaildWarn.jhw",
					date);

		} else {
			table = table_b;
			if (table.getRowCount() <= 0) {
				this.messageBox("没有打印数据");
				return;
			}
			// 打印数据
			TParm date = new TParm();
			// 表头数据
			if(getRadioButton("STOCK_QTY_A").isSelected()){
				date.setData("TITLE", "TEXT",  "药品库存量提示报表(最高库存量)");
			}else if(getRadioButton("STOCK_QTY_B").isSelected()){
				date.setData("TITLE", "TEXT",  "药品库存量提示报表(最低库存量)");  
			}else{  
				date.setData("TITLE", "TEXT",  "药品库存量提示报表(安全库存量)");
			}
			//date.setData("TITLE", "TEXT", "药品库存量提示报表");
//			String start_date = getValueString("START_DATE");
//			String end_date = getValueString("END_DATE");
//			date.setData("DATE_AREA", "TEXT", "统计区间: "  
//					+ start_date.substring(0, 4) + "/"  
//					+ start_date.substring(5, 7) + "/"
//					+ start_date.substring(8, 10) + " "
//					+ start_date.substring(11, 13) + ":"
//					+ start_date.substring(14, 16) + ":"
//					+ start_date.substring(17, 19) + " ~ "
//					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
//					+ "/" + end_date.substring(8, 10) + " "
//					+ end_date.substring(11, 13) + ":"
//					+ end_date.substring(14, 16) + ":"  
//					+ end_date.substring(17, 19));  
			date.setData("DATE", "TEXT", "制表日期: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE_A"));
			date.setData("ORG_CODE", "TEXT", "部门: " +orgDesc );
			// 表格数据  
			TParm parm = new TParm();  
			TParm tableParm = table.getParmValue();
			// ORDER_CODE;ORDER_DESC;SPECIFICATION;STOCK_QTY;UNIT_CHN_DESC;MAX_QTY;MIN_QTY;SAFE_QTY
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("STOCK_QTY", tableParm.getValue("STOCK_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(  
						"UNIT_CHN_DESC", i));
				parm.addData("MAX_QTY", tableParm.getValue("MAX_QTY", i));
				parm.addData("MIN_QTY", tableParm.getValue("MIN_QTY", i));
				parm.addData("SAFE_QTY", tableParm.getValue("SAFE_QTY", i));
			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_QTY");  
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MAX_QTY");
			parm.addData("SYSTEM", "COLUMNS", "MIN_QTY");
			parm.addData("SYSTEM", "COLUMNS", "SAFE_QTY");
			date.setData("TABLE", parm.getData());
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\SPC\\INDVaildQty.jhw",
					date);
		}

	}

	private String getOrg(String valueString) {
		String sql = " SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"+valueString+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ORG_CHN_DESC",0);
	}

}
