package com.javahis.ui.mro;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import jdo.mro.MROBorrowTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:
 * 
 * <p>
 * Description:ԤԼ�Һ�������ȡ
 * 
 * <p>
 * Copyright:
 * 
 * <p>
 * Company: JavaHis       
 * </p>
 * 
 * @author chenx
 * @version 4.0
 */
public class MRORegAppQueryControl extends TControl {
	
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
		
		this.setValue("S_DATE", tommorwDate); // ��ʼʱ��
		table = (TTable) this.getComponent("TABLE");
	}
	
	/**
	 * ������ȡ
	 */
	public void onQuery() {
		TParm data = getQueryParm();
		// webservice ��ȡcrm����
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
				"getOrder", data);
		if (parm.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("��������");
			return;
		}
		
		// ��crm���ݲ��뵽�м��MRO_REG
		this.getCrmParm(parm);
		
		TParm result = TIOM_AppServer.executeAction(
				"action.mro.MROBorrowAction", "insertMroReg", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("CRM���ݲ�����ʱ��ʱ�����쳣");
			err("ERR:" + result.getErrCode() + result.getErrName()
					+ result.getErrText());
			return;
		}
		
		// ɸ����ԤԼ����
		// modify by wangb 2017/11/24 ����CRM�����Ļظ�����CRM�ӿ���ȡ��������ȫ��Ϊ��ЧԤԼ���������������
//		this.dealNotAppointmentCrmData(parm);
		
		if (parm.getCount() <= 0) {
			table.setParmValue(new TParm());
			this.messageBox("��������");    
			return;
		}
		
		table.setParmValue(parm);
		this.messageBox("������ȡ���");
	}

	/**
	 * ���õ���crm��������
	 * 
	 * @param parm
	 * @return
	 */
	public void getCrmParm(TParm parm) {
		
		// ȡ��VIP�Һ���Ϣ
		TParm vipInfoParm = new TParm();
		
		for (int i = parm.getCount() - 1; i > -1; i--) {
			// modify by wangbin 2015/1/7 ���˵����Ｔ�޲����ŵ�ԤԼ����
			if (StringUtils.isEmpty(parm.getValue("MR_NO", i))) {
				parm.removeRow(i);
				continue;
			}
		}
		
		for (int i = parm.getCount() - 1; i > -1; i--) {
			parm.addData("MRO_REGNO", "");
			parm.addData("SEQ", "");
			parm.addData("BOOK_ID", parm.getValue("CRM_ID", i));
			// 0_ԤԼ�Һ�(APP), 1_�ֳ��Һ�(LOC), 2_סԺ�Ǽ�
			parm.addData("ORIGIN_TYPE", "0");
			// �ż�ס��ʶ
			parm.addData("ADM_TYPE", "O");
			// �����
			parm.addData("CASE_NO", "");
			// ������ȷ��״̬
			parm.addData("CONFIRM_STATUS", "0");
			
			// ȡ��CRMԤԼ�Һ���Ϣ
			vipInfoParm = MROBorrowTool.getInstance().queryVipRegInfo(parm.getRow(i));
			
			if (vipInfoParm.getErrCode() < 0) {
				err(vipInfoParm.getErrCode() + " " + vipInfoParm.getErrText());
				this.messageBox("ȡ��ԤԼ�Һ���Ϣ�쳣");
				return;
			}
			
			// ����
			parm.addData("ADM_AREA_CODE", vipInfoParm.getValue("REG_CLINICAREA", 0));
			// �Һ�ʱ��
			parm.addData("SESSION_CODE", vipInfoParm.getValue("SESSION_CODE", 0));
			// ���
			parm.addData("QUE_NO", vipInfoParm.getValue("QUE_NO", 0));
			// ȡ��ע��(Y_ȡ��,N_δȡ��)
			parm.addData("CANCEL_FLG", "N");
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
		}
	}
	
	/**
	 * ��ȡ��ѯ����
	 * 
	 * @return
	 */
	public TParm getQueryParm() {
		TParm queryParm = new TParm();
		queryParm.setData("admDate", getValueString("S_DATE"));
		queryParm.setData("session", "");
		queryParm.setData("deptCode", getValueString("DEPT_CODE"));
		queryParm.setData("drCode", getValueString("DR_CODE"));
		return queryParm;

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.onInit();
		this.clearValue("DEPT_CODE;DR_CODE");
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
		ExportExcelUtil.getInstance().exportExcel(table, "�Һ�ԤԼ����");
	}
	
	/**
	 * ����crm����ɾ����ԤԼ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm dealNotAppointmentCrmData(TParm parm) {
		for (int i = parm.getCount() - 1; i > -1; i--) {
			if (!"1,4,5".contains(parm.getValue("STATUS", i))) {
				parm.removeRow(i);
			}
		}
		
		return parm;
	}
}
