package com.javahis.ui.sta;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: STA_IN_11�ż���Һ���ϸ��(���)
 * </p>
 * 
 * <p>
 * Description: �ż���Һ���ϸ��(���)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author wanglong 2012.06.21
 * @version 1.0
 */
public class STAIn_11Control extends TControl {

	private StringUtil util;// �ַ�������
	private TParm tableParm; // ��������TParm

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initUI();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * ��ʼ������
	 */
	public void initUI() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		this.setValue("S_DATE", SystemTool.getInstance().getDate());// ��������
		this.setValue("E_DATE", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("ADM_TYPE", "");// ���������
		this.setValue("DEPT_CODE", "");
		this.callFunction("UI|Table|removeRowAll");// ��ձ��
	}

	/**
	 * ���
	 */
	public void onClear() {
		initUI();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// �ж��ż�������
		if (this.getValueString("ADM_TYPE").equals("")) {
			this.messageBox("��ѡ���ż�������");
			return;
		}
		String startDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyyMMdd");
		String endDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyyMMdd");
		String admType = "";// ���Ｖ��ID
		if (!this.getValue("ADM_TYPE").equals("")) {
			admType = " AND A.ADM_TYPE = '" + this.getValue("ADM_TYPE") + "'  ";
		}
		String deptCode = "";// ����ID
		if (!this.getValue("DEPT_CODE").equals("")) {
			deptCode = " AND A.DEPT_CODE = '" + this.getValue("DEPT_CODE")
					+ "'  ";
		}
		String regionCode = "";// ����ID
		if (!this.getValue("REGION_CODE").equals("")) {
			regionCode = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		}
		String dateStr = "";
		// ����ȡ�Һ�ʱ��
		if (this.getValue("ADM_TYPE").equals("E")) {
			dateStr = " AND A.REG_DATE BETWEEN TO_DATE('" + startDate
					+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ endDate + "235959','yyyyMMddHH24miss') ";
		} else {
			dateStr = " AND A.ADM_DATE BETWEEN TO_DATE('" + startDate
					+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ endDate + "235959','yyyyMMddHH24miss') ";
		}
		String sql = "SELECT *��FROM ( SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME,"
				+ " F.CHN_DESC, B.BIRTH_DATE, A.ADM_DATE,B.CURRENT_ADDRESS,"
				+ " D.DEPT_CHN_DESC, E.USER_NAME,"
				+ " CASE C.MAIN_DIAG_FLG WHEN 'Y' THEN  H.ICD_CHN_DESC WHEN 'N' THEN  'N' "
				+ " ELSE '' END AS ICD_CHN_DESC , G.CTZ_DESC,A.REG_DATE"
				+ " FROM REG_PATADM A," + " SYS_PATINFO B," + " OPD_DIAGREC C,"
				+ " SYS_DEPT D," + " SYS_OPERATOR E," + " SYS_DICTIONARY F,"
				+ " SYS_CTZ G," + " SYS_DIAGNOSIS H"
				+ " WHERE A.MR_NO = B.MR_NO" + " AND A.CASE_NO= C.CASE_NO(+)"
				+ " AND A.DEPT_CODE = D.DEPT_CODE"
				+ " AND A.DR_CODE = E.USER_ID" + " AND B.SEX_CODE = F.ID"
				+ " AND F.GROUP_ID = 'SYS_SEX'"
				+ " AND A.CTZ1_CODE = G.CTZ_CODE"
				+ " AND C.ICD_TYPE = H.ICD_TYPE(+)"
				+ " AND C.ICD_CODE = H.ICD_CODE(+)"
				+ "  AND A.REGCAN_USER IS NULL " + admType + deptCode
				+ regionCode + dateStr;
		sql += " ) TAB WHERE TAB.ICD_CHN_DESC IS NULL OR TAB.ICD_CHN_DESC<>'N' " + " ORDER BY TAB.REG_DATE";
//		System.out.println("-------------sql" + sql);
		tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm nowTimeParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT SYSDATE FROM DUAL"));
		int count = 0;
		
		if (tableParm.getCount("CASE_NO") <= 0) {
			this.messageBox("��������");
			this.callFunction("UI|Table|removeRowAll");// ��ձ��
			// �ϼƱ���
			this.setValue("SUM_TOT", count + "");
			return;
		}
		count = tableParm.getCount("CASE_NO");
		// �ϼƱ���
		this.setValue("SUM_TOT", count + "");
		for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
			tableParm.setData("BIRTH_DATE", i, StringUtil.showAge(
					tableParm.getTimestamp("BIRTH_DATE", i),
					nowTimeParm.getTimestamp("SYSDATE", 0)));
		}
		// ���ֵ
		this.callFunction("UI|Table|setParmValue", tableParm);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|Table|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "�ż���Һ���ϸ��(���)");
		}
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TTable table = ((TTable) this.getComponent("Table"));
		int rowCount = tableParm.getCount("CASE_NO");
		if (rowCount < 1) {
			this.messageBox("���Ȳ�ѯ����!");
			return;
		}
		TParm T1 = new TParm(); // �������
		for (int i = 0; i < rowCount; i++) {
			
			T1.addRowData(tableParm, i);
		}
		for(int i=0;i<rowCount;i++){
			T1.setData("ADM_DATE", i, T1.getValue("ADM_DATE", i).toString().substring(0, 10).replace('-', '/'));
		}
		T1.setCount(rowCount);
		String[] chage = table.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyy/MM/dd");
		String admName = new TParm(TJDODBTool.getInstance().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ADMTYPE' AND ID='"
						+ this.getValue("ADM_TYPE") + "'")).getValue(
				"CHN_DESC", 0);// ���Ｖ��ID
		String deptName = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ this.getValue("DEPT_CODE") + "'")).getValue(
				"DEPT_CHN_DESC", 0);// ����ID
		TParm printParm = new TParm();// �������ε�TParm
		printParm.setData("START_DATE", "TEXT", sDate);
		printParm.setData("END_DATE", "TEXT", eDate);
		printParm.setData("ADM_NAME", "TEXT", admName);
		printParm.setData("DEPT_NAME", "TEXT", deptName);
		printParm.setData("OPT_USER", "TEXT", "�Ʊ���:" + Operator.getName());
		printParm.setData("TABLE", T1.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_11.jhw",
				printParm);
	}
}
