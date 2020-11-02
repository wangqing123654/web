package com.javahis.ui.sta;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: STA_IN_12����������ϸ����
 * </p>
 * 
 * <p>
 * Description: ����������ϸ����
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
 * @author shibl 2012.06.21
 * @version 1.0
 */
public class STAIn_12Control extends TControl {
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
		this.setValue("DATE_S", SystemTool.getInstance().getDate());
		this.setValue("DATE_E", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
	}

	/**
	 * ���
	 */
	public void onClear() {
		initUI();
		this.setValue("SUN_TOT", "0");
		this.callFunction("UI|TABLE|removeRowAll");// ��ձ��
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (this.getValueString("DEPT_CODE").equals("")) {
			this.messageBox("���Ҳ���Ϊ��");
			return;
		}
		String startDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_S")), "yyyyMMdd");
		String endDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_E")), "yyyyMMdd");
		if(startDate.compareTo(endDate)>0){
			this.messageBox("��ʼ���ڴ��ڽ�������");
			return;
		}
		String regionCode = "";// ����ID
		if (!this.getValue("REGION_CODE").equals("")) {
			regionCode = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		}
		String dept_code = ""; // ����
		if (!this.getValue("DEPT_CODE").equals("")) {
			dept_code = " AND A.DEPT_CODE= '" + this.getValueString("DEPT_CODE")
					+ "' ";
		}
		String dateStr = "";
		// ȡ����ʱ��
		if (!startDate.equals("") && !endDate.equals("")) {
			dateStr = " AND A.ORDER_DATE BETWEEN TO_DATE('" + startDate
					+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ endDate + "235959','yyyyMMddHH24miss') ";
		}
		String sql = " SELECT A.MR_NO, B.PAT_NAME, A.ORDER_DESC, A.ORDER_DATE,E.DEPT_CHN_DESC AS DEPT_CODE,"
				+ " F.USER_NAME AS DR_CODE, D.ICD_CHN_DESC,'' AS BED_NO,'' AS CODE_STATUS "
				+ " FROM OPD_ORDER A, SYS_PATINFO B, OPD_DIAGREC C, SYS_DIAGNOSIS D,SYS_DEPT E,SYS_OPERATOR F "
				+ " WHERE  A.ORDER_CAT1_CODE = 'SVG' "
				+ " AND A.MR_NO = B.MR_NO "
				+ "  AND C.MAIN_DIAG_FLG ='Y' "
				+ "  AND A.CASE_NO = C.CASE_NO "
				+ " AND C.ICD_CODE = D.ICD_CODE "
				+ "  AND C.ICD_TYPE = D.ICD_TYPE "
				+ "  AND A.DEPT_CODE = E.DEPT_CODE "
				+ "  AND A.DR_CODE = F.USER_ID "
				+ regionCode
				+ dept_code
				+ dateStr + " ORDER BY A.ORDER_DATE";
		tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = 0;
		if (tableParm.getCount() <= 0) {
			this.messageBox("��������");
			this.callFunction("UI|TABLE|removeRowAll");// ��ձ��
			// �ϼƱ���
			this.setValue("SUM_TOT", count + "");
			return;
		}
		count = tableParm.getCount();
		// �ϼƱ���
		this.setValue("SUM_TOT", count + "");
		// ���ֵ
		this.callFunction("UI|TABLE|setParmValue", tableParm);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "����������ϸͳ�Ʊ���");
		}
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TTable table = ((TTable) this.getComponent("TABLE"));
		table.acceptText();
		TParm parm = table.getParmValue();
		int rowCount = parm.getCount();
		if (rowCount < 1) {
			this.messageBox("���Ȳ�ѯ����!");
			return;
		}
		TParm T1 = new TParm(); // �������
		for (int i = 0; i < rowCount; i++) {
			parm.setData("ORDER_DATE", i, StringTool.getString(
					parm.getTimestamp("ORDER_DATE", i), "yyyy/MM/dd HH:mm"));
			T1.addRowData(parm, i);
		}
		T1.setCount(rowCount);
		String[] chage = table.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_S")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("DATE_E")), "yyyy/MM/dd");
		TParm printParm = new TParm();// �������ε�TParm
		printParm.setData("START_DATE", "TEXT", sDate);
		printParm.setData("END_DATE", "TEXT", eDate);
		printParm.setData("OPT_USER", "TEXT", "�Ʊ���:" + Operator.getName());
		printParm.setData("TABLE", T1.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_12.jhw",
				printParm);
	}
}
