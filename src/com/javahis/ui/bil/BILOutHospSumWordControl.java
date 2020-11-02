package com.javahis.ui.bil;

import com.javahis.util.ExportExcelUtil;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>
 * Title: ��Ժ����ҽ�Ʒ����ܱ�
 * </p>
 * 
 * <p>
 * Description: ��Ժ����ҽ�Ʒ����ܱ�
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
 * @author caowl 2012.07.09
 * @version 1.0
 */
public class BILOutHospSumWordControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}

	String[] chargName = { "CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
			"CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08", "CHARGE09",
			"CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13", "CHARGE14",
			"CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18", "CHARGE19",
			"CHARGE20", "CHARGE21" };
	private TParm parmName; // ��������
	private TParm parmCode; // ���ô���
	// ��������
	Timestamp startDate;
	Timestamp bill_date;

	/**
	 * ��ʼ������
	 */
	public void initPage() {
		// ��ʼ��ʱ��
		startDate = SystemTool.getInstance().getDate();
		bill_date = getDateForInit(queryFirstDayOfLastMonth(StringTool.
                getString(SystemTool.getInstance().getDate(), "yyyyMMdd")));
		setValue("S_DATE", startDate);
		setValue("BILL_DATE", bill_date);
		String sql = SYSSQL.getBillRecpparm(); // ��÷��ô���
		sql += " WHERE ADM_TYPE='I'";
		parmCode = new TParm(TJDODBTool.getInstance().select(sql));
		TTable table = (TTable) this.getComponent("Table");
//		table.removeRowAll();
		if (parmCode.getErrCode() < 0 || parmCode.getCount() <= 0) {
			this.messageBox("���÷����ֵ�������");
			return;
		}
		// ��÷�������
		sql = "SELECT ID ,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE'";
		parmName = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(getHeard());
	}

	/**
	 * ��ӱ�ͷ
	 * 
	 * @return TParm
	 */
	private TParm getHeard() {
		TParm heardParm = new TParm();
		heardParm.addData("DEPT_CHN_DESC", "��������");
		heardParm.addData("STATION_DESC", "����");
		heardParm.addData("TOT_AMT", "�ϼƽ��");
		for (int i = 0; i < chargName.length; i++) {
			heardParm.addData(chargName[i], getChargeName(parmName, parmCode
					.getValue(chargName[i], 0)));
		}
		heardParm.setCount(1);
		return heardParm;
	}

	/**
	 * ��÷�������
	 * 
	 * @param parmName
	 *            TParm
	 * @param chargeCode
	 *            String
	 * @return String
	 */
	private String getChargeName(TParm parmName, String chargeCode) {
		for (int i = 0; i < parmName.getCount(); i++) {
			if (parmName.getValue("ID", i).equals(chargeCode)) {
				return parmName.getValue("CHN_DESC", i);
			}
		}
		return "";
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		print();
	}

	/**
	 * ���ñ����ӡԤ������
	 */
	private void print() {
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 2) {
			this.messageBox_("�Ȳ�ѯ����!");
			return;
		}
		

		
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");

		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd HH:mm:ss");
		String billTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("BILL_DATE")), "yyyyMMdd");
		TParm selAllParm = new TParm();// ȫ��
		TParm selCheckedParm = new TParm();// �ѽ�ת
		TParm selParm = new TParm();// δ��ת
		String billDay = billTime.substring(0, 8); // �½�ʱ���
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND E.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		
		if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
			// �ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_6"))) {
				if (this.getValue("LUMPWORK_CODE") != null
						&& this.getValueString("LUMPWORK_CODE").length() > 0) {
					regionWhere += " AND D.LUMPWORK_CODE ='"
							+ this.getValue("LUMPWORK_CODE") + "'";
				} else {
					regionWhere += " AND D.LUMPWORK_CODE IS NOT NULL ";
				}
			}
			// ���ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_7"))) {
				regionWhere += " AND D.LUMPWORK_CODE IS NULL  ";
			}
		}
		
		// ȫ�����ϼƣ�
		String sqlAll = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selAllParm = new TParm(TJDODBTool.getInstance().select(sqlAll));
		BILRecpChargeForPrint endData = new BILRecpChargeForPrint();
		TParm endAllParm = endData.getValue(selAllParm);
//		System.out.println("ȫ����" + endAllParm);
		// �ѽ�ת
		String sqlChecked = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ " AND B.BILL_DATE < TO_DATE('"
				+ billDay
				+ "000000','yyyyMMddhh24miss')"
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selCheckedParm = new TParm(TJDODBTool.getInstance().select(sqlChecked));
		BILRecpChargeForPrint endData1 = new BILRecpChargeForPrint();
		TParm endCheckedParm = endData1.getValue(selCheckedParm);
//		System.out.println("�ѽ�ת��" + endCheckedParm);
		// δ��ת
		String sql = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ " AND B.BILL_DATE > TO_DATE('"
				+ billDay
				+ "000000','yyyyMMddhh24miss')"
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
		selParm = new TParm(TJDODBTool.getInstance().select(sql));		
		BILRecpChargeForPrint endData2 = new BILRecpChargeForPrint();
		TParm endParm = endData2.getValue(selParm);
//		System.out.println("δ��ת��" + endParm);
		String sDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd");

		// ��ֵ��������
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "��Ժ����ҽ�Ʒ����ܱ�");
		parm.setData("DATE", "TEXT", sDate);
		parm.setData("OPT_USER", "TEXT", Operator.getName());
		parm.setData("printDate", "TEXT", sysDate);
		// �ѽ�ת
		String[] checkedName = { "CHARGE01", "CHARGE02", "CHARGE03", "CHARGE04",
				"CHARGE05", "CHARGE06", "CHARGE07", "CHARGE08", "CHARGE09",
				"CHARGE10", "CHARGE11", "CHARGE12", "CHARGE13", "CHARGE14",
				"CHARGE15", "CHARGE16", "CHARGE17", "CHARGE18", "CHARGE19",
				"CHARGE20", "CHARGE21" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(checkedName[i], "TEXT", endCheckedParm.getData(
					chargName[i], endCheckedParm.getCount() - 1));
		}		
		parm.setData("TOT_AMT", "TEXT", endCheckedParm.getData("TOT_AMT",
				endCheckedParm.getCount()-1));
		// δ��ת
		String[] checkName = { "CHECKCHARGE01", "CHECKCHARGE02",
				"CHECKCHARGE03", "CHECKCHARGE04", "CHECKCHARGE05",
				"CHECKCHARGE06", "CHECKCHARGE07", "CHECKCHARGE08",
				"CHECKCHARGE09", "CHECKCHARGE10", "CHECKCHARGE11",
				"CHECKCHARGE12", "CHECKCHARGE13", "CHECKCHARGE14",
				"CHECKCHARGE15", "CHECKCHARGE16", "CHECKCHARGE17",
				"CHECKCHARGE18", "CHECKCHARGE19", "CHECKCHARGE20", "CHECKCHARGE21" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(checkName[i], "TEXT", endParm.getData(chargName[i],
					endParm.getCount() - 1));
		}
		parm.setData("CHECKTOT_AMT", "TEXT", endParm.getData("TOT_AMT", endParm.getCount() - 1 ));
//		System.out.println("δ��ת�ܼƣ�"+endParm.getData("TOT_AMT", endParm.getCount() - 1 ));
		//ȫ��
		String[] allChargName = { "ALLCHARGE01", "ALLCHARGE02", "ALLCHARGE03",
				"ALLCHARGE04", "ALLCHARGE05", "ALLCHARGE06", "ALLCHARGE07",
				"ALLCHARGE08", "ALLCHARGE09", "ALLCHARGE10", "ALLCHARGE11",
				"ALLCHARGE12", "ALLCHARGE13", "ALLCHARGE14", "ALLCHARGE15",
				"ALLCHARGE16", "ALLCHARGE17", "ALLCHARGE18", "ALLCHARGE19",
				"ALLCHARGE20", "ALLCHARGE21" };
		for (int i = 0; i < chargName.length; i++) {
			parm.setData(allChargName[i], "TEXT", endAllParm.getData(
					chargName[i], endAllParm.getCount() - 1));
		}
		parm.setData("ALLTOT_AMT", "TEXT", endAllParm.getData("TOT_AMT",
				endAllParm.getCount()-1));

		this.openPrintWindow(
				"%ROOT%\\config\\prt\\IBS\\BILOutHospPatSumFee.jhw", parm);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String billTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("BILL_DATE")), "yyyyMMdd");
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String deptWhere = "";
		String billDay = billTime.substring(0, 8); // �½�ʱ���
		// �ѽ�ת
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_4"))) {
			deptWhere += " AND B.BILL_DATE < TO_DATE('" + billDay
					+ "000000','yyyyMMddhh24miss')";
		}
		// δ��ת
		if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_5"))) {
			deptWhere += " AND B.BILL_DATE > TO_DATE('" + billDay
					+ "000000','yyyyMMddhh24miss')";
		}

		if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
			// �ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_6"))) {
				if (this.getValue("LUMPWORK_CODE") != null
						&& this.getValueString("LUMPWORK_CODE").length() > 0) {
					deptWhere += " AND D.LUMPWORK_CODE ='"
							+ this.getValue("LUMPWORK_CODE") + "'";
				} else {
					deptWhere += " AND D.LUMPWORK_CODE IS NOT NULL ";
				}
			}
			// ���ײ�
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioButton_7"))) {
				deptWhere += " AND D.LUMPWORK_CODE IS NULL  ";
			}
		}
        
		String regionWhere = "";
		if (!"".equals(Operator.getRegion()))
			regionWhere = " AND E.REGION_CODE = '" + Operator.getRegion()
					+ "' ";
		String sql = " SELECT B.DEPT_CODE AS EXE_DEPT_CODE, SUM (C.AR_AMT)  AS TOT_AMT, C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " FROM BIL_IBS_RECPM A,IBS_BILLM B,IBS_BILLD C,ADM_INP D,SYS_DEPT E,SYS_STATION F,SYS_CTZ G "
				+ " WHERE A.RECEIPT_NO=B.RECEIPT_NO "
				+ " AND A.CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000', 'yyyyMMddhh24miss') AND TO_DATE('"
				+ startTime
				+ "235959', 'yyyyMMddhh24miss')"
				+ " AND B.BILL_NO=C.BILL_NO "
				+ " AND B.BILL_SEQ=C.BILL_SEQ "
				+ " AND A.CASE_NO=D.CASE_NO "
				+ " AND B.DEPT_CODE=E.DEPT_CODE(+) "
				+ " AND B.STATION_CODE=F.STATION_CODE(+) "
				+ " AND B.CTZ1_CODE=G.CTZ_CODE "
				+ deptWhere
				+ regionWhere
				+ " GROUP BY B.DEPT_CODE,B.STATION_CODE,C.REXP_CODE, E.DEPT_CHN_DESC ,F.STATION_DESC "
				+ " ORDER BY B.DEPT_CODE";
//		System.out.println("sql:" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("selParm" + selParm);
		if (selParm.getCount("EXE_DEPT_CODE") < 1) {
			// ��������
			this.messageBox("E0008");
			this.initPage();
			return;
		}
		BILRecpChargeForPrint endData = new BILRecpChargeForPrint();

		TParm endParm = endData.getValue(selParm);
//		System.out.println("endParm:"+endParm);
		TParm resultParm = getHeard(); // ��ͷ
		int count = resultParm.getCount();
		for (int i = 0; i < endParm.getCount(); i++) {
			resultParm.setRowData(count, endParm, i);
			count++;
		}
		resultParm.setCount(count);
		resultParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		resultParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		for (int i = 0; i < chargName.length; i++) {
			resultParm.addData("SYSTEM", "COLUMNS", chargName[i]);
		}
		this.callFunction("UI|Table|setParmValue", resultParm);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "��Ժ����ҽ�Ʒ����ܱ�");
	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		this.clearValue("STATION_CODE;DEPT_CODE;LUMPWORK_CODE");
        this.callFunction("UI|tCheckBox_0|setSelected",false);
        this.callFunction("UI|tRadioButton_6|setEnabled", false);
        this.callFunction("UI|tRadioButton_7|setEnabled", false);
        this.callFunction("UI|tRadioButton_7|setSelected",true);
        this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
	}

	/**
	 * ��ѡ��ť�¼�
	 * 
	 * @param type
	 *            String
	 */
	public void onRodio(String type) {
		if ("CTZ".equalsIgnoreCase(type)) {
			this.callFunction("UI|CTZ_CODE|setEnabled", true);
		} else {
			this.callFunction("UI|CTZ_CODE|setEnabled", false);
		}
	} 

	/**
	 * �����ײ͸�ѡ��ť�¼�
	 * 
	 * @param type
	 *            String
	 */
	public void onCheckBox(String type) {
		if ("Y".equalsIgnoreCase(this.getValueString("tCheckBox_0"))) {
			this.callFunction("UI|tRadioButton_6|setEnabled", true);
			this.callFunction("UI|tRadioButton_7|setEnabled", true);
			if ("LUMPWORK".equalsIgnoreCase(type)) {
				this.callFunction("UI|LUMPWORK_CODE|setEnabled", true);
			} else {
				this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
				this.clearValue("LUMPWORK_CODE");
			}
		} else {
			this.callFunction("UI|tRadioButton_6|setEnabled", false);
			this.callFunction("UI|tRadioButton_7|setEnabled", false);
			//this.callFunction("UI|tRadioButton_8|setSelected", true);
			this.callFunction("UI|LUMPWORK_CODE|setEnabled", false);
			this.clearValue("LUMPWORK_CODE");
		}
	}

	 /**
     * �õ���ǰ��
     * @param dateStr String
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
//        cal.add(Calendar.MONTH, -1);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
        return StringTool.getTimestamp(cal.getTime());
    }

    /**
     * ��ʼ��ʱ������
     * @param date Timestamp
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
