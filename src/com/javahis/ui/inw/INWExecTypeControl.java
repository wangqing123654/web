package com.javahis.ui.inw;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inw.INWCareNumAndExecTypeTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: 护士工作量统计Control
 * </p>
 * 
 * <p>
 * Description: 护士工作量统计Control
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
 * @author zhangh 2013.12.9
 * @version 1.0
 */

public class INWExecTypeControl extends TControl {

	public INWExecTypeControl() {
	}

	private TTable tableExec;

	/*
	 * 界面初始化
	 * 
	 * @see com.dongyang.control.TControl#onInit()
	 */
	public void onInit() {
		initUI();
	}

	/*
	 * 初始化界面
	 */
	private void initUI() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME_EXEC", new Timestamp(date.getTime() + -7
				* 24L * 60L * 60L * 1000L).toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_TIME_EXEC", date.toString().substring(0, 10)
				.replace("-", "/")
				+ " 23:59:59");
		tableExec = (TTable) this.getComponent("TABLE_EXEC");
	}

	/*
	 * 查询方法
	 */
	public void onQuery() {
		query();
	}

	private void query() {
		String sqlWhere = "";
		TParm parm = new TParm();
		TParm result = new TParm();
		if (this.getValueString("START_TIME_EXEC").length() <= 0
				|| this.getValueString("END_TIME_EXEC").length() <= 0) {
			this.messageBox("请填写查询时间！");
			return;
		}

		// 开始时间
		String startTime = this.getValueString("START_TIME_EXEC").substring(0,
				this.getValueString("START_TIME_EXEC").lastIndexOf("."))
				.replace("-", "").replace(":", "").replace(" ", "");
		parm.setData("START_TIME", startTime);
		// 结束时间
		String endTime = this.getValueString("END_TIME_EXEC").substring(0,
				this.getValueString("END_TIME_EXEC").lastIndexOf(".")).replace(
				"-", "").replace(":", "").replace(" ", "");
		parm.setData("END_TIME", endTime);
		// 验收部门
		if (this.getValueString("DEPT_CODE_EXEC").length() > 0) {
			String deptCode = this.getValueString("DEPT_CODE_EXEC");
			parm.setData("DEPT_CODE", deptCode);
			sqlWhere += " AND B.DEPT_CODE ='" + deptCode + "'";
		}
		// 病区
		if (this.getValueString("STATION_CODE_EXEC").length() > 0) {
			String stationCode = this.getValueString("STATION_CODE_EXEC");
			parm.setData("STATION_CODE", stationCode);
			sqlWhere += " AND B.STATION_CODE ='" + stationCode + "'";
		}
		// 护士
		if (this.getValueString("USER").length() > 0) {
			String user = this.getValueString("USER");
			parm.setData("USER_ID", user);
			sqlWhere += " AND A.NS_EXEC_CODE ='" + user + "'";
		}
		result = INWCareNumAndExecTypeTool.getInstance().onQueryExecType(parm);

		if (result.getCount() <= 0) {
			this.messageBox("无查询结果");
			tableExec.removeRowAll();
			return;
		}

		TParm temp = new TParm();
		String sql = " SELECT DEPT_CHN_DESC," + " STATION_DESC,"
				+ " E.USER_NAME," + " A.NS_EXEC_CODE" + " FROM ODI_DSPND A,"
				+ " ODI_ORDER B," + " SYS_DEPT C," + " SYS_STATION D,"
				+ " SYS_OPERATOR E" + " WHERE A.DC_DATE IS NULL"
				+ " AND A.NS_EXEC_DATE >= TO_DATE ('" + startTime
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND A.NS_EXEC_DATE <= TO_DATE ('" + endTime
				+ "', 'YYYYMMDDHH24MISS')" + " AND A.CASE_NO = B.CASE_NO"
				+ " AND A.ORDER_NO = B.ORDER_NO"
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ"
				+ " AND A.NS_EXEC_CODE = E.USER_ID"
				+ " AND B.DEPT_CODE = C.DEPT_CODE"
				+ " AND B.STATION_CODE = D.STATION_CODE";
		String group = " GROUP BY B.DEPT_CODE," + " B.STATION_CODE,"
				+ " C.DEPT_CHN_DESC," + " D.STATION_DESC," + " E.USER_NAME,"
				+ " NS_EXEC_CODE" + " ORDER BY B.DEPT_CODE, B.STATION_CODE";
		sql = sql + sqlWhere + group;
		temp = new TParm(TJDODBTool.getInstance().select(sql));

		if (temp.getCount() <= 0) {
			this.messageBox("查询错误！");
			tableExec.removeRowAll();
			return;
		}
		int eCount = 0, fCount = 0, iCount = 0, oCount = 0;
		for (int i = 0; i < temp.getCount("NS_EXEC_CODE"); i++) {
			temp.setData("O_COUNT", i, 0);
			temp.setData("E_COUNT", i, 0);
			temp.setData("I_COUNT", i, 0);
			temp.setData("F_COUNT", i, 0);
		}
		for (int i = 0; i < temp.getCount(); i++) {
			for (int j = 0; j < result.getCount("DEPT_CHN_DESC"); j++) {
				if (temp.getValue("NS_EXEC_CODE", i).equals(
						result.getValue("NS_EXEC_CODE", j))) {
					temp.setData(result.getValue("CLASSIFY_TYPE", j)
							+ "_COUNT",i, result.getInt("NUM", j));
				}
			}
		}
//		System.out.println("temp"+temp);
		
		for (int i = 0; i < temp.getCount("DEPT_CHN_DESC"); i++) {
			TParm  parmRow=temp.getRow(i);
			eCount+=parmRow.getInt("E_COUNT");
			fCount+=parmRow.getInt("F_COUNT");
			iCount+=parmRow.getInt("I_COUNT");
			oCount+=parmRow.getInt("O_COUNT");
		}

		temp.addData("DEPT_CHN_DESC", "总计：");
		temp.addData("STATION_DESC", "");
		temp.addData("USER_NAME", "");
		temp.addData("O_COUNT", oCount);
		temp.addData("I_COUNT", iCount);
		temp.addData("F_COUNT", fCount);
		temp.addData("E_COUNT", eCount);

		tableExec.setParmValue(temp);
	}

	/*
	 * 清空方法
	 */
	public void onClear() {
		Timestamp date = new Timestamp(new Date().getTime());
		this
				.clearValue("DEPT_CODE_EXEC;STATION_CODE_EXEC;START_TIME_EXEC;END_TIME_EXEC");
		tableExec.removeRowAll();
		this.setValue("START_TIME_EXEC", new Timestamp(date.getTime() + -7
				* 24L * 60L * 60L * 1000L).toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_TIME_EXEC", date.toString().substring(0, 10)
				.replace("-", "/")
				+ " 23:59:59");
	}

	/*
	 * 打印方法
	 */
	public void onPrint() {
		print();
	}

	private void print() {
		TParm tableData = new TParm();

		if (tableExec.getRowCount() <= 0) {
			this.messageBox("无打印数据！");
			return;
		}

		tableData = tableExec.getShowParmValue();

		tableData.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		tableData.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		tableData.addData("SYSTEM", "COLUMNS", "USER_NAME");
		tableData.addData("SYSTEM", "COLUMNS", "O_COUNT");
		tableData.addData("SYSTEM", "COLUMNS", "I_COUNT");
		tableData.addData("SYSTEM", "COLUMNS", "F_COUNT");
		tableData.addData("SYSTEM", "COLUMNS", "E_COUNT");

		TParm printParm = new TParm();

		printParm.setData("TABLE", tableData.getData());
		printParm.setData("TITLE", "TEXT", "护士工作统计表");
		printParm.setData("DATE", "TEXT", "制表时间："
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date()));
		printParm.setData("USER", "TEXT", "制表人：" + Operator.getName());

		this.openPrintWindow("%ROOT%\\config\\prt\\INW\\INWExecTypeReport.jhw",
				printParm);
	}
}
