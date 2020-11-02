package com.javahis.ui.dev;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdo.dev.DEVChildAppointInfoTool;
import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:新生儿信息查询
 * </p>
 * 
 * <p>
 * Description: 新生儿信息查询
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
 * @author wukai
 * @version 4.5
 */
public class DEVChildAppointInfoControl extends TControl {
	/**
	 * 信息表格
	 */
	private TTable tableChildInfo;
	/**
	 * 数据库操作类
	 */
	private DEVChildAppointInfoTool mTool;
	/**
	 * 显示查询结果的条数
	 */
	private TLabel tResNums;
	/**
	 * 选择新生儿出生天数42天、3月、6月、9月、1周岁的孩子
	 */
	private TComboBox tAgeDays;
	/**
	 * 未来几天
	 */
	private TNumberTextField tFetureDays;
	/**
	 * 查询所有的基本sql语句
	 */
	private String baseSQL;
	/**
	 * 全局出生时间
	 */
	private String birthDay = "0";
	/**
	 * 全局未来几天
	 */
	private String fetureDay = "7";

	@Override
	public void onInit() {
		super.onInit();
		mTool = DEVChildAppointInfoTool.getNewInstance();
		tableChildInfo = (TTable) this.getComponent("ChildTable");
		tResNums = (TLabel) this.getComponent("SearchNums");
		tAgeDays = (TComboBox) this.getComponent("ComboxAge");
		tFetureDays = (TNumberTextField) this.getComponent("NumberFutDays");
		initTable();
		baseSQL = "SELECT b.MR_NO,b.PAT_NAME, "
				+ "(CASE WHEN b.SEX_CODE = '1' THEN '男' WHEN b.SEX_CODE='2' THEN '女' WHEN b.SEX_CODE='0' THEN '未知' END) AS SEX_CODE, "
				+ "b.BIRTH_DATE,m.MOTHER_NAME,m.CELL_PHONE FROM "
				+ "(SELECT a.CASE_NO,A.IPD_NO, s.PAT_NAME , s.MR_NO, s.BIRTH_DATE ,s.SEX_CODE from SYS_PATINFO s, ADM_INP a WHERE s.MR_NO in (SELECT MR_NO FROM sys_patinfo WHERE new_born_flg='Y') and s.MR_NO = a.MR_NO ORDER BY S.MR_NO)"
				+ "b, "
				+ "(SELECT s.PAT_NAME AS MOTHER_NAME,s.BIRTH_DATE AS MOTHER_DATE, s.CELL_PHONE, s.MR_NO FROM SYS_PATINFO s WHERE MR_NO in (SELECT IPD_NO FROM adm_inp WHERE new_born_flg='Y' ) ORDER BY s.MR_NO ) "
				+ "m WHERE b.IPD_NO = m.MR_NO";
	}

	/**
	 * 查出所有的新生儿数据
	 */
	private void initTable() {
		TParm t = mTool.onQueryAll();
		if (t != null) {
			tableChildInfo.setParmValue(mTool.onQueryAll());
			tResNums.setText(t.getCount() + " 条");
		}

	}

	/**
	 * 工具栏查询按钮 用来查询当前孩子的信息
	 */
	public void onSearch() {
		// 处理孩子年龄天数
		String ageDays = tAgeDays.getText();
		// String unit = this.getText("TimeUnit");
		this.birthDay = ageDays;
		int num = 0;
		if (StringUtils.isEmpty(ageDays)) {
			num = 0;
		} else {
			String numStr = getNum(ageDays);
			if (StringUtils.isEmpty(numStr)) {
				this.messageBox("请输入合法的时间!");
				return;
			}
			num = Integer.parseInt(numStr);
			if (ageDays.contains("月"))
				num = num * 30;
			else if (ageDays.contains("年") || ageDays.contains("周岁"))
				num = num * 365;
			// if(unit.equals("天") || unit.equals("")) {
			// num = num * 1;
			// } else if(unit.equals("周")) {
			// num = num * 7;
			// } else if(unit.equals("周岁")) {
			// num = num * 365;
			// }
		}
		ageDays = String.valueOf(num);
		// 处理未来几天的天数
		String fetureDays = tFetureDays.getText();
		if (StringUtils.isEmpty(fetureDays)) {
			fetureDays = "0";
		}
		this.fetureDay = fetureDays;
		// this.messageBox("ageDays:" + ageDays + ",fetureDays:" + fetureDays);
		StringBuffer sb = new StringBuffer();
		if (!ageDays.equals("0") || !fetureDays.equals("0")) {
			sb.append(" AND (SELECT trunc(sysdate - b.birth_date) FROM dual) > ");
			sb.append(ageDays);
			sb.append(" AND (SELECT trunc(sysdate - b.birth_date) FROM dual) < ");
			sb.append(Integer.parseInt(ageDays) + Integer.parseInt(fetureDays));
		}
		String sql = baseSQL + sb.toString();
		TParm res = new TParm(TJDODBTool.getInstance().select(sql));
		tableChildInfo.setParmValue(res);
		if (res.getCount() <= 0) {
			tResNums.setText("0 条");
		} else {
			tResNums.setText(res.getCount() + " 条");
		}

	}

	/**
	 * 将一个字符串中的数字提取出来
	 * 
	 * @param str
	 * @return
	 */
	private String getNum(String str) {
		// int num = 0;
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * add by wukai on 20160512 打印报表
	 */
	public void onPrint() {
		// this.messageBox("print");
		int rowCount = tableChildInfo.getRowCount();
		if (rowCount <= 0) {
			this.messageBox("没有要打印的数据");
			return;
		}
		TParm data = new TParm();
		// 报表表头标题
		data.setData("TABLENAME", "TEXT", "儿童保健科随访儿童信息表");
		// 打印时间范围
		//this.messageBox(this.birthDay + "," + this.fetureDay);
		if ("0".equals(this.birthDay) || StringUtils.isEmpty(birthDay)
				&& "0".equals(this.fetureDay)) {                      
			data.setData("TimeRange", "TEXT", "出生天数：" + "所有时间");
		} else {
			StringBuilder sb = new StringBuilder();
			if (StringUtils.isEmpty(this.birthDay)) {
				sb.append("0  至  ");
				sb.append(this.fetureDay + "天");
			} else {
				sb.append(this.birthDay);
				sb.append("  至  ");
				if(this.birthDay.contains("天")) {
					int days = Integer.parseInt(getNum(this.birthDay)) + Integer.parseInt(this.fetureDay);
					sb.append(days + "天");
				} else {
					sb.append(this.birthDay + this.fetureDay + "天");
				}
				
			}
			data.setData("TimeRange", "TEXT", "出生天数：" + sb.toString()); // 时间范围
		}
		// 打印当前时间和打印人
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(new Date());
		data.setData("PrintTime", "TEXT", "打印时间：" + time);
		data.setData("PrintPeople", "TEXT", "制表人：" + Operator.getName());

		// 表格数据处理CASE_NO, PAT_NAME, SEX_CODE, BIRTH_DATE, MOTHER_NAME,
		// CELL_PHONE
		TParm parm = new TParm();
		TParm tableParm = tableChildInfo.getParmValue();
		for (int i = 0; i < rowCount; i++) {
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.addData("SEX_CODE", tableParm.getValue("SEX_CODE", i));
			parm.addData("BIRTH_DATE", tableParm.getValue("BIRTH_DATE", i));
			parm.addData("MOTHER_NAME", tableParm.getValue("MOTHER_NAME", i));
			parm.addData("CELL_PHONE", tableParm.getValue("CELL_PHONE", i));
		}
		parm.setCount(rowCount);
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
		parm.addData("SYSTEM", "COLUMNS", "BIRTH_DATE");
		parm.addData("SYSTEM", "COLUMNS", "MOTHER_NAME");
		parm.addData("SYSTEM", "COLUMNS", "CELL_PHONE");

		data.setData("TABLE", parm.getData());
		// TODO 调用方法打印报表
		this.openPrintWindow("%ROOT%\\config\\prt\\ChildAppointInfo.jhw", data,
				true);
	}
}
