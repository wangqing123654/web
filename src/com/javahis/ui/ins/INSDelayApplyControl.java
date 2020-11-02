package com.javahis.ui.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSTJTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;

/**
 * <p>
 * Title: �ӳ��걨
 * </p>
 * 
 * <p>
 * Description: �ʸ�ȷ���� �ӳ��걨
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111214
 * @version 1.0
 */
public class INSDelayApplyControl extends TControl {

	private String ownNo;// ���˱���
	private String inDate;// ��Ժʱ��
	private String regionCode;// ҽԺ����
	private String diagDesc;// ��Ժ���

	public void onInit() {
		super.onInit();
		// �õ�ǰ̨���������ݲ���ʾ�ڽ�����
		TParm parm = (TParm) getParameter();
		ownNo = parm.getValue("OWN_NO");
		inDate = parm.getValue("IN_DATE");
		regionCode = parm.getValue("REGION_CODE");
		diagDesc = parm.getValue("DIAG_DESC");
		((TComboBox) this.getComponent("COM_TYPE")).setSelectedID("1");
	}

	// public void onSave() {
	//
	// TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
	// "comm", new TParm());
	// if (result.getErrCode() < 0) {
	// this.messageBox("���ʧ��");
	// }else{
	// this.messageBox("��ӳɹ�");
	// }
	// }
	/**
	 * ȷ��
	 */
	public void onOK() {
		if (this.getValue("DELAY_APP").toString().length() <= 0) {
			this.messageBox("�������ӳ�������Ϣ");
			return;
		}
		if (this.getValue("DELAY_APP").toString().length() > 400) {
			this.messageBox("ԭ����������200��!");
			return;
		}
		TParm parm = new TParm();
		parm.setData("PERSONAL_NO", ownNo);
		parm.setData("HOSPITAL_SIS", diagDesc);
		parm.setData("HOSPITAL_DATE", inDate.replace("/", ""));
		parm.setData("HOSPITAL_NO", regionCode);
		parm.setData("DELAY_REASON", this.getValue("DELAY_APP"));// �ӳ���Ϣ
		parm.setData("INSURANCE_TYPE", this.getValue("COM_TYPE"));// ����combox
		TParm result=INSTJTool.getInstance().DataDown_yb_I(parm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox("�ӳ��걨ʧ��");
			return;
		}else{
			this.messageBox("P0001");
		}
		//��ӵ��ýӿڷ���
		this.callFunction("UI|onClose");
	}
}
