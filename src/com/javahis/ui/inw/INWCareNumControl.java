package com.javahis.ui.inw;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inw.INWCareNumAndExecTypeTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: ��ʿ������ͳ��Control
 * </p>
 * 
 * <p>
 * Description: ��ʿ������ͳ��Control
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

public class INWCareNumControl extends TControl {

	public INWCareNumControl() {
	}

	private TTable tableCare;

	/*
	 * �����ʼ��
	 * 
	 * @see com.dongyang.control.TControl#onInit()
	 */
	public void onInit() {
		initUI();
	}

	/*
	 * ��ʼ������
	 */
	private void initUI() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME_CARE", new Timestamp(date.getTime() + -7
				* 24L * 60L * 60L * 1000L).toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_TIME_CARE", date.toString().substring(0, 10)
				.replace("-", "/")
				+ " 23:59:59");
		tableCare = (TTable) this.getComponent("TABLE_CARE");
	}

	/*
	 * ��ѯ����
	 */
	public void onQuery() {
		query();
	}

	private void query() {
		TParm parm = new TParm();
		TParm result = new TParm();
		if (this.getValueString("START_TIME_CARE").length() <= 0
				|| this.getValueString("END_TIME_CARE").length() <= 0) {
			this.messageBox("����д��ѯʱ�䣡");
			return;
		}

		// ��ʼʱ��
		String startTime = this.getValueString("START_TIME_CARE").substring(0,
				this.getValueString("START_TIME_CARE").lastIndexOf("."));
		parm.setData("START_TIME", startTime);
		// ����ʱ��
		String endTime = this.getValueString("END_TIME_CARE").substring(0,
				this.getValueString("END_TIME_CARE").lastIndexOf("."));
		parm.setData("END_TIME", endTime);
		// ���ղ���
		if (this.getValueString("DEPT_CODE_CARE").length() > 0) {
			String deptCode = this.getValueString("DEPT_CODE_CARE");
			parm.setData("DEPT_CODE", deptCode);
		}
		// ����
		if (this.getValueString("STATION_CODE_CARE").length() > 0) {
			String stationCode = this.getValueString("STATION_CODE_CARE");
			parm.setData("STATION_CODE", stationCode);
		}
		// ����ȼ�
		if (this.getValueString("NURSING_CLASS").length() > 0) {
			String nurClass = this.getValueString("NURSING_CLASS");
			parm.setData("NURSING_CLASS", nurClass);
		}
		result = INWCareNumAndExecTypeTool.getInstance().onQueryCareNum(parm);

		if (result.getCount() <= 0) {
			this.messageBox("�޲�ѯ���");
			tableCare.removeRowAll();
			return;
		}
		int count = 0;
		for (int i = 0; i < result.getCount("DEPT_CHN_DESC"); i++) {
			count += result.getInt("PERSON_COUNT", i);
		}
		
		for (int i = 0; i < result.getCount("DEPT_CHN_DESC"); i++) {
			result.setData("PERSENT", i, (((double)result.getInt("PERSON_COUNT", i)
					/(double)count) * 100 + "").substring(0, 4) + "%");
		}
		
		result.addData("DEPT_CHN_DESC", "�ܼƣ�");
		result.addData("STATION_DESC", "");
		result.addData("NURSING_CLASS_DESC", "");
		result.addData("PERSON_COUNT", count);
		result.addData("PERSENT", "");
		
		tableCare.setParmValue(result);
	}

	/*
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = new Timestamp(new Date().getTime());
		this
				.clearValue("DEPT_CODE_CARE;STATION_CODE_CARE;START_TIME_CARE;END_TIME_CARE;NURSING_CLASS");
		tableCare.removeRowAll();
		this.setValue("START_TIME_CARE", new Timestamp(date.getTime() + -7
				* 24L * 60L * 60L * 1000L).toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_TIME_CARE", date.toString().substring(0, 10)
				.replace("-", "/")
				+ " 23:59:59");
	}

	/*
	 * ��ӡ����
	 */
	public void onPrint() {
		print();
	}

	private void print() {
		TParm tableData = new TParm();

		if (tableCare.getRowCount() <= 0) {
			this.messageBox("�޴�ӡ���ݣ�");
			return;
		}

		tableData = tableCare.getShowParmValue();
		tableData.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		tableData.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		tableData.addData("SYSTEM", "COLUMNS", "NURSING_CLASS_DESC");
		tableData.addData("SYSTEM", "COLUMNS", "PERSON_COUNT");
		tableData.addData("SYSTEM", "COLUMNS", "PERSENT");

		TParm printParm = new TParm();

		printParm.setData("TABLE", tableData.getData());
		printParm.setData("TITLE", "TEXT", "��ʿ����ͳ�Ʊ�");
		printParm.setData("DATE", "TEXT", "�Ʊ�ʱ�䣺"
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date()));
		printParm.setData("USER", "TEXT", "�Ʊ��ˣ�" + Operator.getName());

		this.openPrintWindow("%ROOT%\\config\\prt\\INW\\INWCareNumReport.jhw",
				printParm);
	}
}
