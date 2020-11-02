package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ��������ͳ��
 * </p>
 * 
 * <p>
 * Description: ��������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author caowl 2012.07.05
 * @version 1.0
 */
public class OPBReceiptOfOutPatientControl extends TControl {

	/**
	 * ��ʼ��
	 * */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ������
	 * 
	 */
	private void initPage() {

		// ��ʼ����ѯ����
		this.setValue("S_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("E_DATE", rollDay);
		// ��ʼ��Ժ��Ϊ��
		this.setValue("REALDEPT_CODE", "");
		// �Ƴ�������������
		this.callFunction("UI|Table|removeRowAll");
		// ��ʼ����
		TTable table = (TTable) this.getComponent("Table");

	}

	/**
	 * ��ѯ
	 * */
	public void onQuery() {
		// ��ò�ѯ����
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd HH:mm:ss");
		System.out.println("startTime--->" + startTime);
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd HH:mm:ss");
		TParm selParm = new TParm();
		String deptWhere = "";
		System.out.println();
		// �����ż���
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("1"))
				deptWhere = " AND (A.ADM_TYPE = 'O' OR A.ADM_TYPE = 'E') AND A.REALDEPT_CODE = '020103'";
		}
		// Ժ������
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("2"))
				deptWhere = " and a.adm_type = 'O'  and a.REALDEPT_CODE <> '020103'";
		}

		// Ժ�ڼ���
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("3"))
				deptWhere = " and a.adm_type = 'E'  and a.REALDEPT_CODE <> '020103'";
		}

		String sql = "SELECT   A.CASE_NO, A.MR_NO, A.�Һŷ� + A.���� + B.TOT AS �ϼ�, A.�Һŷ�,"
				+ " A.����, ������, �ǿ�����, �г�ҩ, �в�ҩ, ����, ���Ʒ�, �����,"
				+ " ������, ��Ѫ��, �����, ����, ����ҽ��, �۲촲��, CT, MR, �ԷѲ���,"
				+ " ���Ϸ�, ������"
				+ "  FROM (SELECT   B.CASE_NO, B.MR_NO, SUM (REG_FEE_REAL) AS �Һŷ�,"
				+ "  SUM (CLINIC_FEE_REAL) AS ����"
				+ "  FROM REG_PATADM A, BIL_REG_RECP B"
				+ " WHERE A.CASE_NO = B.CASE_NO"
				+ deptWhere
				+ "  AND A.ADM_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "',"
				+ " 'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ "  AND TO_DATE ('"
				+ endTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " GROUP BY B.CASE_NO, B.MR_NO) A,"
				+ "(SELECT   A.CASE_NO, SUM (TOT_AMT) AS TOT, SUM (CHARGE01) AS ������,"
				+ "  SUM (CHARGE02) AS �ǿ�����, SUM (CHARGE03) AS �г�ҩ,"
				+ "  SUM (CHARGE04) AS �в�ҩ, SUM (CHARGE05) AS ����,"
				+ "  SUM (CHARGE06) AS ���Ʒ�, SUM (CHARGE07) AS �����,"
				+ "  SUM (CHARGE08) AS ������, SUM (CHARGE09) AS ��Ѫ��,"
				+ "  SUM (CHARGE10) AS �����, SUM (CHARGE11) AS ����,"
				+ "  SUM (CHARGE12) AS ����ҽ��, SUM (CHARGE13) AS �۲촲��,"
				+ "  SUM (CHARGE14) AS CT, SUM (CHARGE15) AS MR,"
				+ "  SUM (CHARGE16) AS �ԷѲ���, SUM (CHARGE17) AS ���Ϸ�,"
				+ "  SUM (CHARGE18) AS ������"
				+ "  FROM REG_PATADM A, BIL_OPB_RECP B"
				+ " WHERE A.CASE_NO = B.CASE_NO"
				+ deptWhere
				+ "  AND CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " AND TO_DATE ('"
				+ endTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " GROUP BY A.CASE_NO) B"
				+ "  WHERE A.CASE_NO = B.CASE_NO"
				+ "  ORDER BY CASE_NO";

		System.out.println("sql---->" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (selParm.getCount("CASE_NO") < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();
			return;
		}
		this.callFunction("UI|Table|setParmValue", selParm);

	}

	/**
	 * ���
	 * */
	public void onClear() {
		initPage();
		this.clearValue("REALDEPT_CODE");
	}

	/**
	 * ����Excel
	 * */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		int row = table.getRowCount();
		if (row < 1) {
			this.messageBox_("�Ȳ�ѯ����!");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "��������ͳ�Ʊ�");
	}

	/**
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
                
	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}
}
