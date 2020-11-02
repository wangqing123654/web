package com.javahis.ui.ins;

import jdo.ins.INSTJFlow;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title:ҽ��������Ա��������
 * </p>
 * 
 * <p>
 * Description:ҽ��������Ա��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangben 2012.2.23
 * @version 2.0
 */
public class INSConcelErrorControl extends TControl {
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
	}

	private String[] comm = { "NHI_REGION_CODE", "INS_READ_TYPE",
			"INS_PAT_TYPE", "CONFIRM_NO" };
	private StringBuffer messaGrab;//��ý���
	/**
	 * ������ý��㳷��
	 */
	public void onConcelSettlement() {
		if (!getCheck("������ý��㳷��")) {
			return;
		}
		if (this.messageBox("��ʾ","�Ƿ�ȷ��ִ��",2)!=0) {
			return;
		}
		TParm parm=INSTJFlow.getInstance().cancelBalance(commFunction(),getType());
		if (parm.getErrCode()<0) {
			this.setValue("TEXT", parm.getErrText());
			return;
		}
		this.setValue("TEXT", parm.getValue("PROGRAM_MESSAGE"));
	}

	private TParm commFunction() {
		TParm parm = new TParm();
		parm.setData("INS_TYPE", getType());
		parm.setData("REGION_CODE", this.getValue("NHI_REGION_CODE"));
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		parm.setData("opbReadCardParm", opbReadCardParm.getData());
		return parm;
	}

	/**
	 * �����˷�ȡ��
	 */
	public void onConcelReSetFee() {
		
		if (!getCheck("�����˷�ȡ��")) {
			return;
		}
		if (this.messageBox("��ʾ","�Ƿ�ȷ��ִ��",2)!=0) {
			return;
		}
		TParm parm=INSTJFlow.getInstance().resetConcelFee(commFunction());
		if (parm.getErrCode()<0) {
			this.setValue("TEXT", parm.getErrText());
			return;
		}
		this.setValue("TEXT", parm.getValue("PROGRAM_MESSAGE"));
	}

	private boolean getCheck(String messageName) {
		String message = concelComm();
		if (message.length() > 0) {
			message = message.substring(0, message.lastIndexOf(","));
			this.messageBox(messageName + "��Ҫ" + message + "����");
			message = messaGrab.substring(0, messaGrab.indexOf(","));
			this.grabFocus(message);
			return false;
		}
		return true;
	}

	/**
	 * ����У�鷽��
	 */
	private String concelComm() {
		messaGrab=new StringBuffer();
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < comm.length; i++) {
			if (this.getValue(comm[i]).toString().length() <= 0) {
				message.append(this.getTip(comm[i]) + ",");
				messaGrab.append(comm[i]+",");
			}
		}
		return message.toString();
	}

	/**
	 * ��þ�ҽ���
	 * 
	 * @return
	 */
	private int getType() {
		int insType = 0;
		if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("1")) {
			insType = 1;

			// ��ְ����
		} else if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 2;

		} else if (this.getValue("INS_READ_TYPE").equals("2")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 3;

		}
		return insType;
	}

	/**
	 * INS_CROWD_TYPE combox У��
	 */
	public void onSelType() {
		if (this.getValueInt("INS_READ_TYPE") == 1) {	
			this.setValue("INS_PAT_TYPE", 1);
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
		} else if (this.getValueInt("INS_READ_TYPE") == 2) {
			this.setValue("INS_PAT_TYPE", 2);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
		}
	}
	/**
	 * �˷Ѳ���
	 */
	public void onReSetFee() {
		if (!getCheck("�����˷�")) {
			return;
		}
		if (this.messageBox("��ʾ","�Ƿ�ȷ��ִ��",2)!=0) {
			return;
		}
		int type = getType();
		TParm parm = new TParm();
		parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
		parm.setData("REGION_CODE", this.getValue("NHI_REGION_CODE"));
		parm.setData("OPT_USER", Operator.getID());
		TParm opdParm = INSTJFlow.getInstance().selectResetFee(parm);
		if (opdParm.getErrCode()<0) {
			this.messageBox("E0005");
			return;
		}
		if (null==opdParm.getValue("CONFIRM_NO")) {
			this.messageBox("û�в�ѯ���������");
			return;
		}
		TParm result = new TParm();
		TParm resetInsOPDParm = null;
		StringBuffer message=new StringBuffer();
		int flg = 0;
		switch (type) {
		case 1:
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZPt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_yb_C:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			flg = resetInsOPDParm.getInt("INSAMT_FLG");
			if (flg == 1) {
				// �˷�ȷ�Ͻ���
				result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(
						opdParm, resetInsOPDParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_yb_D:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		case 2:
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZMt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_mts_K:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			flg = resetInsOPDParm.getInt("SOCIAL_FLG");
			if (flg == 1) {
				// �˷�ȷ�Ͻ���
				result = INSTJFlow.getInstance()
						.resetFeeInsSettleChZMt(opdParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_mts_L:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		case 3:
			// �����˷�
			resetInsOPDParm = INSTJFlow.getInstance().resetFeeChJMt(parm,
					opdParm);
			if (!INSTJTool.getInstance().getErrParm(resetInsOPDParm)) {
				this.setValue("TEXT", resetInsOPDParm.getErrText());
				return;
			}
			message.append("DataDown_cmts_K:").append(resetInsOPDParm.getValue("PROGRAM_MESSAGE")+"\n");
			if (flg == 1) {
				// �˷�ȷ�Ͻ���
				result = INSTJFlow.getInstance()
						.resetFeeInsSettleChJMt(opdParm);
				if (!INSTJTool.getInstance().getErrParm(result)) {
					this.setValue("TEXT", result.getErrText());
					return;
				}
			}
			message.append("DataDown_cmts_L:").append(result.getValue("PROGRAM_MESSAGE")+"\n");
			break;
		}
		this.setValue("TEXT", message.toString());
	}
	/**
	 * ��þ������
	 */
	public void onGetConfirm(){
		String confirmNo = (String) this.openDialog(
				"%ROOT%\\config\\ins\\INSChooseVisit.x", new TParm());
		this.setValue("CONFIRM_NO", confirmNo);
		
	}
	/**
	 * DataDown_yb (D)
	 */
	public void getDataDown_yb(){
		// �˷�ȷ�Ͻ���
		TParm opdParm=new TParm();
		opdParm.setData("CONFIRM_NO",this.getValue("CONFIRM_NO"));
		opdParm.setData("NHI_REGION_CODE",this.getValue("NHI_REGION_CODE"));
		opdParm.setData("ACCOUNT_PAY_AMT",this.getValue("ACCOUNT_PAY_AMT"));
		TParm resetInsOPDParm=new TParm();
		resetInsOPDParm.setData("OTOT_AMT",this.getValueDouble("OTOT_AMT"));
		TParm result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(
				opdParm, resetInsOPDParm);
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.setValue("TEXT", "DataDown_yb_D:"+result.getErrText());
			return;
		}
	}
	/**
	 * ��ְ�Ǿ��˷�ȷ�ϲ���
	 */
	public void getDataDown_mtsAndcmts_L(){
		// �˷�ȷ�Ͻ���
		TParm opdParm=new TParm();
		opdParm.setData("CONFIRM_NO",this.getValue("CONFIRM_NO"));
		opdParm.setData("NHI_REGION_CODE",this.getValue("NHI_REGION_CODE"));
		opdParm.setData("ACCOUNT_PAY_AMT",this.getValueDouble("ACCOUNT_PAY_AMT"));
		opdParm.setData("TOTAL_AGENT_AMT",this.getValueDouble("TOTAL_AGENT_AMT"));
		opdParm.setData("FLG_AGENT_AMT",this.getValueDouble("FLG_AGENT_AMT"));
		if (this.getValueInt("INS_READ_TYPE")==1) {
			// �˷�ȷ�Ͻ���
			TParm result = INSTJFlow.getInstance()
					.resetFeeInsSettleChZMt(opdParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				this.setValue("TEXT", "DataDown_mts_L:"+result.getErrText());
				return;
			}
		}else{
			// �˷�ȷ�Ͻ���
			TParm result = INSTJFlow.getInstance()
					.resetFeeInsSettleChJMt(opdParm);
			if (!INSTJTool.getInstance().getErrParm(result)) {
				this.setValue("TEXT", "DataDown_cmts_L:"+result.getErrText());
				return;
			}
		}
		
	} 
	/**
	 * �ʸ�ȷ���鳷������
	 */
	public void onConcelConfirm(){
		if (!getCheck("�ʸ�ȷ���鳷��")) {
			return;
		}
		if (this.messageBox("��ʾ","�Ƿ�ȷ��ִ��",2)!=0) {
			return;
		}
		//int type = getType();
		TParm parm = new TParm();
		//parm.setData("INS_TYPE", getType());
		parm.setData("HOSP_NHI_NO", this.getValue("NHI_REGION_CODE"));
		TParm concelParm = null;
		StringBuffer message=new StringBuffer();
		if (this.getValueInt("INS_READ_TYPE") == 1) {// 1 ��ְ 2.�Ǿ�
			parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
			concelParm = INSTJTool.getInstance().DataDown_sp_C(parm);
			message.append("DataDown_sp_C:").append(concelParm.getValue("PROGRAM_MESSAGE")+"\n");

		} else if (this.getValueInt("INS_READ_TYPE") == 2) {// 1 ��ְ 2.�Ǿ�
			parm.setData("CONFIRM_NO", this.getValue("CONFIRM_NO"));
			concelParm = INSTJTool.getInstance().DataDown_czys_F(parm);
			message.append("DataDown_czys_F:").append(concelParm.getValue("PROGRAM_MESSAGE")+"\n");

		}
		this.setValue("TEXT", message.toString());
				
	}
}
