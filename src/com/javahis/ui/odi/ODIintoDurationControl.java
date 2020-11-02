package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.system.textFormat.TextFormatCLPDuration;

/**
 * <p>
 * Title: ����ʱ��
 * </p>
 * 
 * <p>
 * Description: ����ʱ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liling 20140821
 * @version 1.0
 */
public class ODIintoDurationControl extends TControl {
	public ODIintoDurationControl() {

	}

	private TParm sendParm;
	private String case_no = "";
	private String currentDuration;// ��ǰʱ��
	private String nextDuration;// ѡ��ʱ��
	private String clncPathCode;// �ٴ�·��
	String deptFlg;

	/**
	 * ҳ���ʼ������
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		// System.out.println("ҳ���ʼ��");
		sendParm = (TParm) this.getParameter();
		case_no = sendParm.getValue("CASE_NO");
		clncPathCode = sendParm.getValue("CLNCPATH_CODE");
		// ��ջ�������begin
		currentDuration = null;
		nextDuration = null;
		this.setValue("currentDuration", "");
		this.setValue("nextDuration", "");
		deptFlg = sendParm.getValue("DEPT_FLG");
		if (null != deptFlg && deptFlg.length() > 0) {
			this.callFunction("UI|LBL_UPDATE|setVisible", true);
		} else {
			this.callFunction("UI|LBL_UPDATE|setVisible", false);
		}
		// ��ջ�������end
		getCurrentDuration();
		initPageData();
	}

	/**
	 * ��ʼ��ҳ��ֵ
	 */
	private void initPageData() {
		if (clncPathCode != null) {
			this.setValue("CLNC_PATHCODE", clncPathCode);
			callFunction("UI|nextDuration|onQuery");
		}
		if (currentDuration != null) {
			this.setValue("currentDuration", currentDuration);
		}
		if (nextDuration != null) {
			this.setValue("nextDuration", nextDuration);
		}
	}

	/**
	 * �õ���ǰʱ��
	 */
	private void getCurrentDuration() {
		String sql = "SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO= '"
				+ this.case_no + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// �õ���ǰʱ��
		if (result.getCount("SCHD_CODE") > 0) {
			currentDuration = result.getValue("SCHD_CODE", 0);
			return;
		}
	}

	/**
	 * ����ʱ��
	 */
	public void inDuration() {
		nextDuration = this.getValueString("nextDuration");
		if (null == nextDuration || nextDuration.length() <= 0) {
			this.messageBox("��ѡ�����ʱ��");
			return;
		}
		TParm result = new TParm();
		if (null != deptFlg && deptFlg.length() > 0) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", this.case_no);
			parm.setData("SCHD_CODE", this.nextDuration);
			result = TIOM_AppServer.executeAction(
					"action.clp.CLPManagemAction", "onExeInDuration", parm);
		} else {
			String sql = "UPDATE ADM_INP SET SCHD_CODE = '" + nextDuration
					+ "' WHERE CASE_NO= '" + this.case_no + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��!");
		} else {
			this.messageBox("����ɹ�!");
		}
		// ִ�к�ҳ���ʼ��
		initPage();
	}

	/**
	 * �ٴ�·���ؼ������¼�
	 */
	public void onClickClncpathCode() {
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this
				.getComponent("nextDuration");
		if (this.getValue("CLNC_PATHCODE").toString().length() > 0) {
			combo_schd.setClncpathCode(this.getValue("CLNC_PATHCODE")
					.toString());
		}
		combo_schd.onQuery();
	}

	/**
	 * �رշ���
	 */
	public void onWindowClose() {
		if (null != deptFlg && deptFlg.length() > 0) {
			String sql = "UPDATE ADM_TRANS_LOG SET CLP_SCHD_FLG = 'Y' WHERE CASE_NO= '"
	    		+  this.case_no+ "' AND CLP_SCHD_FLG = 'N'";
	    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		}
		this.closeWindow();
	}
}
