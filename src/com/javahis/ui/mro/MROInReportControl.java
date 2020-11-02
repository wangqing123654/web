package com.javahis.ui.mro;

import java.util.HashMap;
import java.util.Map;

import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:����������ѯ����
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis       
 * </p>
 * 
 * @author wangbin
 * @version 1.0
 */
public class MROInReportControl extends TControl {
	private TTable table;
	private Map<String, String> lendAreaMap;// ���������ؼ�Map����

	/**
	 * ��ʼ��
	 */

	public void onInit() {
		super.onInit();
		this.onInitPage();
	}

	/**
	 * ��ʼ������
	 */
	public void onInitPage() {
		this.setValue("REP_S_DATE", SystemTool.getInstance().getDate()); // ��ʼʱ��
		this.setValue("REP_E_DATE", SystemTool.getInstance().getDate()); // ��ֹʱ��
		table = (TTable) this.getComponent("TABLE");
		
		// ����/�����ؼ��趨
		TTextFormat lendArea = ((TTextFormat)this.getComponent("ADM_AREA_CODE"));
		TParm areaParm = MROQueueTool.getInstance().selectMroLendArea();
		lendArea.setPopupMenuData(areaParm);
		lendArea.setComboSelectRow();
		lendArea.popupMenuShowData();
		
		// ��װ������������Map
		lendAreaMap = new HashMap<String, String>();
		for (int i = 0; i < areaParm.getCount(); i++) {
			lendAreaMap.put(areaParm.getValue("ID", i), areaParm.getValue("NAME", i));
		}
	}
	
	/**
	 * ���ݲ�ѯ
	 */
	public void onQuery() {
		TParm queryParm = getQueryParm();
		
		if (queryParm.getBoolean("ERR")) {
			return;
		}
		
		TParm result = MROBorrowTool.getInstance().queryMROInReadyData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ��������ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("��������");    
			return;
		}
		
		// ����������ʾ����(��������)
		for (int i = 0; i < result.getCount(); i++) {
			result.addData("ADM_AREA_DESC", lendAreaMap
					.get(result.getValue("ADM_AREA_CODE", i)));
		}
		
		table.setParmValue(result);
	}

	/**
	 * ��ȡ��ѯ����
	 * 
	 * @return
	 */
	public TParm getQueryParm() {
		TParm queryParm = new TParm();
		
		if (StringUtils.isEmpty(getValueString("REP_S_DATE"))) {
			this.messageBox("��ʼ���ڲ���Ϊ��");
			queryParm.setData("ERR", true);
			return queryParm;
		}
		
		if (StringUtils.isEmpty(getValueString("REP_E_DATE"))) {
			this.messageBox("��ֹ���ڲ���Ϊ��");
			queryParm.setData("ERR", true);
			return queryParm;
		}
		
		queryParm.setData("REP_S_DATE", getValueString("REP_S_DATE").substring(
				0, 10).replace("-", "")
				+ "000000");
		queryParm.setData("REP_E_DATE", getValueString("REP_E_DATE").substring(
				0, 10).replace("-", "")
				+ "235959");
		queryParm.setData("DEPT_CODE", getValueString("REP_DEPT_CODE"));
		queryParm.setData("OUT_TYPE", getComboBox("REP_OUT_TYPE").getSelectedID());
		queryParm.setData("ISSUE_CODE", "1");
		queryParm.setData("CAN_FLG", "N");
		
		queryParm.setData("ERR", false);
		return queryParm;

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("REP_DEPT_CODE;REP_OUT_TYPE");
		table.removeRowAll();
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�������������");
	}
	
	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
