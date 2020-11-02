package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.Date;
import java.util.Vector;

import com.javahis.util.ExportExcelUtil;

public class CLPDifTotWordControl extends TControl {
	public CLPDifTotWordControl() {

	}

	// ��ʼʱ��
	private TTextFormat start_date;
	// ����ʱ��
	private TTextFormat end_date;
	// ���
	private TTable table;

	public void onInit() {
		super.onInit();
		initControl();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void initControl() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ʱ����Ϊ1��
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table = (TTable) this.getComponent("CLPTABLE");
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		if (this.table.getRowCount() <= 0) {
			this.messageBox("û��Ҫ��ӡ������");
			return;
		}
		TParm prtParm = new TParm();
		// ��ͷ
		prtParm.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "����Ժ��")
				+ "�ٴ�·�������ܱ�");
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		prtParm.setData("START_DATE", "TEXT", startDate); // �Ʊ�ʱ��start
		prtParm.setData("END_DATE", "TEXT", endDate); // �Ʊ�ʱ��end
		TParm prtTableParm = this.getSelectTParm();
		prtTableParm.addData("SYSTEM", "COLUMNS", "REGION_CODE");// wangzhilei 20120724 ���
		prtTableParm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "MONCAT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "VARIANCE_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		prtTableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		prtTableParm.addData("SYSTEM", "COLUMNS", "VAR_COUNT");
		prtParm.setData("CLPTABLE", prtTableParm.getData());
		// ��β
		prtParm.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPDifTotWordPrt.jhw",
				prtParm);
	}

	public void onQuery() {
		TParm parm = this.getSelectTParm();
		table.setParmValue(parm);
	}

	public TParm getSelectTParm() {
		TParm tableparm = new TParm();
		// �õ�ʱ���ѯ����
		String startdate = this.getValueString("START_DATE");
		startdate = startdate.substring(0, 4) + startdate.substring(5, 7)
				+ startdate.substring(8, 10) + startdate.substring(11, 13)
				+ startdate.substring(14, 16) + startdate.substring(17, 19);
		String enddate = this.getValueString("END_DATE");
		enddate = enddate.substring(0, 4) + enddate.substring(5, 7)
				+ enddate.substring(8, 10) + enddate.substring(11, 13)
				+ enddate.substring(14, 16) + enddate.substring(17, 19);
		// �õ������ܱ���Ϣ
		String Sql = "SELECT D.REGION_CODE AS REGION_CODE,B.CLNCPATH_CHN_DESC,T.MONCAT_CHN_DESC,V.VARIANCE_CHN_DESC,DE.DEPT_CHN_DESC,R.USER_NAME,COUNT(D.CASE_NO) VAR_COUNT FROM "
				+ "CLP_MANAGED D, CLP_VARMONCAT T,CLP_VARIANCE V,CLP_BSCINFO B ,SYS_DEPT DE ,SYS_OPERATOR R ,ADM_INP P,MRO_RECORD MR  "
				+ "WHERE MR.CASE_NO=D.CASE_NO AND D.MEDICAL_MONCAT IS NOT NULL AND  T.MONCAT_CODE(+) = D.MEDICAL_MONCAT  AND D.CASE_NO=P.CASE_NO(+) AND D.REGION_CODE='"
				+ Operator.getRegion()
				+ "' AND MR.OUT_DATE>TO_DATE('"
				+ startdate
				+ "','YYYYMMDDHH24MISS') AND MR.OUT_DATE<TO_DATE('"
				+ enddate
				+ "','YYYYMMDDHH24MISS') "
				+ "AND V.VARIANCE_CODE(+) = D.MEDICAL_VARIANCE "
				+ "AND  B.CLNCPATH_CODE(+)=D.CLNCPATH_CODE "
				+ "AND  D.R_DEPT_CODE = DE.DEPT_CODE(+) "
				+ "AND  D.R_USER = R.USER_ID(+) "
				+ "GROUP BY B.CLNCPATH_CHN_DESC, T.MONCAT_CHN_DESC, V.VARIANCE_CHN_DESC, DE.DEPT_CHN_DESC, R.USER_NAME,D.REGION_CODE ";
		//System.out.println("ִ��sql:" + Sql);
		tableparm = new TParm(TJDODBTool.getInstance().select(Sql));
		TParm prtTableParm = new TParm();
		// �����ѯ������parm
		if (tableparm == null || tableparm.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return tableparm;
		}
		// �ܼ���Ϣ����
		// ������
		int totalCount = 0;
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");// wangzhilei 20120724 ���
		// ͳ����Ϣend
		for (int i = 0; i < tableparm.getCount(); i++) {
			TParm rowParm = tableparm.getRow(i);
			// wangzhilei 20120724 ���
			String rn = tableparm.getValue("REGION_CODE", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			// wangzhilei 20120724 ���
			prtTableParm.addData("REGION_CODE", rn);// wangzhilei 20120724 ���
			prtTableParm.addData("CLNCPATH_CHN_DESC", rowParm
					.getValue("CLNCPATH_CHN_DESC"));
			prtTableParm.addData("MONCAT_CHN_DESC", rowParm
					.getValue("MONCAT_CHN_DESC"));
			prtTableParm.addData("VARIANCE_CHN_DESC", rowParm
					.getValue("VARIANCE_CHN_DESC"));
			prtTableParm.addData("DEPT_CHN_DESC", rowParm
					.getValue("DEPT_CHN_DESC"));
			prtTableParm.addData("USER_NAME", rowParm.getValue("USER_NAME"));
			prtTableParm.addData("VAR_COUNT", rowParm.getValue("VAR_COUNT"));
			// ͳ�Ʊ�����
			totalCount += rowParm.getInt("VAR_COUNT");
		}
		// ����ͳ����Ϣ
		prtTableParm.addData("REGION_CODE", "�ܼ�:");// wangzhilei 20120724 ���
		prtTableParm.addData("MONCAT_CHN_DESC", "");
		prtTableParm.addData("VARIANCE_CHN_DESC", "");
		prtTableParm.addData("DEPT_CHN_DESC", "");
		prtTableParm.addData("USER_NAME", "");
		prtTableParm.addData("VAR_COUNT", totalCount);
		prtTableParm.setCount(prtTableParm.getCount("CLNCPATH_CHN_DESC"));
		return prtTableParm;
	}

	/**
	 * ���
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
	}

	/**
	 * ����Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·�������ܱ�");
		}
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

}
