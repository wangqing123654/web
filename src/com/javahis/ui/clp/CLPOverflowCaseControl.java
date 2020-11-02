package com.javahis.ui.clp;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.javahis.util.ExportExcelUtil;
import jdo.clp.CLPOverPersonManagerTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.base.TComboBoxModel;

import java.util.Date;
import java.util.Vector;

import jdo.sys.SYSRegionTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.control.TControl;

/**
 * <p>
 * Title: �ٴ�·�����ԭ���
 * </p>
 * 
 * <p>
 * Description: �ٴ�·�����ԭ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20110707
 * @version 1.0
 */
public class CLPOverflowCaseControl extends TControl {
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
		// Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	private TTable table;

	public CLPOverflowCaseControl() {
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String date_s = getValueString("DATE_S");
		String date_e = getValueString("DATE_E");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		if (null != this.getValueString("REGION_CODE")
				&& this.getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
		parm.setData("DATE_S", date_s);
		parm.setData("DATE_E", date_e);
		TParm result = CLPOverPersonManagerTool.getInstance().selectData(
				"selectOverflowCase", parm);
		if (result.getCount() <= 0) {
			this.messageBox("��������");
		}
		table.setParmValue(result);
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
		// ��ʼ����ѯ����
		this.setValue("DATE_E", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("DATE_S", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {

		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ӡ������");
			return;
		}
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		TParm parmValue = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			// wangzhilei 20120724 ���
			String rn = parm.getValue("REGION_CODE", i);
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
			parmValue.addData("REGION_CODE", rn);// wangzhilei 20120724 ���
			parmValue.addData("CLNCPATH_CHN_DESC", parm.getValue(
					"CLNCPATH_CHN_DESC", i));
			parmValue.addData("MR_NO", parm.getValue("MR_NO", i));
			parmValue.addData("IPD_NO", parm.getValue("IPD_NO", i));
			parmValue.addData("CTZ_DESC", parm.getValue("CTZ_DESC", i));
			parmValue.addData("DEPT_CHN_DESC", parm
					.getValue("DEPT_CHN_DESC", i));
			parmValue.addData("CHN_DESC", parm.getValue("CHN_DESC", i));
			parmValue.addData("DESCRIPTION", null != parm.getValue(
					"DESCRIPTION", i) ? parm.getValue("DESCRIPTION", i) : "");
			parmValue.addData("STATION_DESC", parm.getValue("STATION_DESC", i));
		}
		parmValue.setCount(parm.getCount());
		TParm result = new TParm();
		parmValue.addData("SYSTEM", "COLUMNS", "REGION_CODE");// wangzhilei 20120724 ���
		parmValue.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
		parmValue.addData("SYSTEM", "COLUMNS", "IPD_NO");
		parmValue.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		parmValue.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		parmValue.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parmValue.addData("SYSTEM", "COLUMNS", "CHN_DESC");
		parmValue.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		result.setData("S_DATE", "TEXT", getValueString("DATE_S").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("E_DATE", "TEXT", getValueString("DATE_E").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("OPT_USER", Operator.getName());
		result.setData("TABLE", parmValue.getData());
		result.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "����Ժ��")
				+ "�ٴ�·�����ԭ���");
		// ¬�������Ʊ���
		// ��β
		result.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewOverflowCase.jhw",
				result);

	}

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�ٴ�·�����ԭ���");
	}

}
