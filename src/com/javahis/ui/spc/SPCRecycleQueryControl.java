package com.javahis.ui.spc;

import jdo.spc.INDSQL;
import jdo.spc.SPCRecycleQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:�龫��ƿ���ռ�¼��ѯControl
 * </p>
 * 
 * <p>
 * Description:�龫��ƿ���ռ�¼��ѯControl
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-09-26
 * @version 1.0
 */
public class SPCRecycleQueryControl extends TControl {

	/**
	 * ҩƷ
	 */
	private TTextField ORDER_CODE;
	private TTextField ORDER_DESC;

	/** ��� */
	private TTable table;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		ORDER_CODE = (TTextField) getComponent("ORDER_CODE");
		ORDER_DESC = (TTextField) getComponent("ORDER_DESC");
		table = (TTable) getComponent("TABLE");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		ORDER_CODE.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		ORDER_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
		// ���ݿⷿ�������ҩ���б�(ORG_CODE)
		onSetOrgCodeByOrgType();
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_code)
				&& !StringUtil.isNullString(order_desc)) {
			ORDER_CODE.setValue(order_code);
			ORDER_DESC.setValue(order_desc);
		}
	}

	/**
	 * ���ݿⷿ�������ҩ���б�(ORG_CODE)
	 * 
	 * @param org_type
	 */
	private void onSetOrgCodeByOrgType() {
		getTextFormat("EXE_DEPT_CODE").setPopupMenuSQL(
				INDSQL.getIndOrgComobo("C", "", Operator.getRegion()));
		getTextFormat("EXE_DEPT_CODE").onQuery();
		getTextFormat("EXE_DEPT_CODE").setText("");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		// ��ѯ���ң��ж�Ҫ��ѯ��һ�ű�
		String exe_dept_code = this.getValueString("EXE_DEPT_CODE");
		String order_code = this.getValueString("ORDER_CODE");
		String reclaim_date = this.getValueString("RECLAIM_DATE");
		TParm parm = new TParm();
		parm.setData("EXE_DEPT_CODE", exe_dept_code);
		parm.setData("ORDER_CODE", order_code);
		parm.setData("RECLAIM_DATE", reclaim_date);
		if (!StringUtil.isNullString(reclaim_date)) {
			reclaim_date = reclaim_date.substring(0, 19);
		}
		TParm result1 = SPCRecycleQueryTool.getInstance().queryOrg(
				exe_dept_code);
		TParm result2 = SPCRecycleQueryTool.getInstance().queryDept(
				exe_dept_code);
		String station_flg = result1.getValue("STATION_FLG", 0);
		String exinv_flg = result1.getValue("EXINV_FLG", 0);
		String emg_fit_flg = result2.getValue("EMG_FIT_FLG", 0);
		TParm result = new TParm();
		if ("Y".equals(station_flg)) {
			if ("Y".equals(emg_fit_flg)) {
				// ������
				result = SPCRecycleQueryTool.getInstance().queryOpd(parm);
			} else if ("N".equals(emg_fit_flg) || "".equals(emg_fit_flg)) {
				// ����
				result = SPCRecycleQueryTool.getInstance().queryInd(parm);
			}
		} else {
			if ("Y".equals(exinv_flg)) {
				// ����/����
				result = SPCRecycleQueryTool.getInstance().querySpc(parm);
			}
		}
		table.setParmValue(result);
	}

	/**
	 * ��񵥻��¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue().getRow(row);
		setValue("ORDER_CODE", tableParm.getValue("ORDER_CODE"));
		setValue("ORDER_DESC", tableParm.getValue("ORDER_DESC"));
		setValue("RECLAIM_DATE", tableParm.getValue("RECLAIM_DATE").substring(
				0, 19));
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "�龫��ƿ���ռ�¼��ѯͳ�Ʊ�");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// �������
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		// ��������е�Ԫ��
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm
					.getValue("SPECIFICATION", i));
			parm.addData("BAR_CODE", tableParm.getValue("BAR_CODE", i));
			parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.addData("EXE_DEPT_CODE", tableParm
					.getValue("EXE_DEPT_CODE", i));
			parm.addData("BED_NO", tableParm.getValue("BED_NO", i));
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("RECLAIM_USER", tableParm.getValue("RECLAIM_USER", i));
			parm.addData("RECLAIM_DATE", tableParm.getValue("RECLAIM_DATE", i)
					.subSequence(0, 19));
		}
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "BAR_CODE");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "EXE_DEPT_CODE");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "RECLAIM_USER");
		parm.addData("SYSTEM", "COLUMNS", "RECLAIM_DATE");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\SPCRecycleQuery.jhw",
				data);
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�龫��ƿ���ռ�¼��ѯ");
	}

	/**
	 * ��ȡTTextFormat�ؼ�
	 * 
	 * @return
	 */
	private TTextFormat getTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

}
