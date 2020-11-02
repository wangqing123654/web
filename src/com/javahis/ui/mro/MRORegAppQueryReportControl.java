package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.mro.MROBorrowTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:ԤԼ�Һ����ݲ�ѯ����
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
public class MRORegAppQueryReportControl extends TControl {
	private TTable table;

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
		// Ĭ����ʾ�ڶ�������
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.DATE, 1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String tommorwDate = sdf.format(calendar.getTime());
		
		this.setValue("REP_S_DATE", SystemTool.getInstance().getDate()); // ��ʼʱ��
		this.setValue("REP_E_DATE", tommorwDate); // ��ֹʱ��
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * ���ݲ�ѯ
	 */
	public void onQuery() {
		TParm queryParm = getQueryParm();
		
		if (queryParm.getBoolean("ERR")) {
			return;
		}
		
		TParm result = MROBorrowTool.getInstance().queryMRORegAppInfoByDate(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯԤԼ�Һ����ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("��������");    
			return;
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
		queryParm.setData("DR_CODE", getValueString("REP_DR_CODE"));
		// ��Դ����(0_ԤԼ�Һ�)
		queryParm.setData("ORIGIN_TYPE", "0");
		queryParm.setData("ADM_TYPE", "O");
		queryParm.setData("CANCEL_FLG", "N");
		
		queryParm.setData("ERR", false);
		return queryParm;

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("REP_DEPT_CODE;REP_DR_CODE");
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
		ExportExcelUtil.getInstance().exportExcel(table, "ԤԼ�Һ�����");
	}
}
