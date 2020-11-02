package com.javahis.ui.ins;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJReg;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;

public class INSConfirmApplyCardOneControl extends TControl {
	private TParm readParm = new TParm();// ˢ������
	// private String case_no;// �����
	private String mr_no;// ��������
	TParm regionParm = null;// ���ҽ���������

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		regionParm  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//���ҽ���������
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		mr_no=parm.getValue("MR_NO");
		callFunction("UI|INS_PAT_TYPE|setEnabled", false);// ������������
		onExeEnable(false);
		this.setValue("INS_PAT_TYPE", "");// 1.��ͨ2.����
	}

	/**
	 * ˢ������
	 */
	public void readCard() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			onReadCard();
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			onReadIdNo();
		}
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// ��Ⱥ���
		this.setValue("INS_READ_TYPE", insReadType);//1.��ְ2.�Ǿ�
		this.grabFocus("PASSWORD");
	}

	/**
	 * ����
	 */
	private void onReadCard() {
		TParm parm = new TParm();
		if (this.getValue("READ_TEXT").toString().length() <= 0) {
			this.messageBox("��ˢ��");
			this.grabFocus("READ_TEXT");
			return;
		}
		
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		parm.setData("TEXT", this.getValue("READ_TEXT"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("PASSWORD", this.getValueString("PASSWORD"));
		parm.setData("MR_NO", mr_no);

		// ���ز���
		readParm = new TParm(INSTJReg.getInstance().readINSCard(parm.getData()));// ��������
	}

	/**
	 * �������֤�Ż����Ϣ
	 */
	private void onReadIdNo() {
		if (!this.emptyTextCheck("IDNO")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("IDNO", this.getValue("IDNO"));// ���֤��
		parm.setData("PAT_NAME", this.getValue("PAT_NAME"));// ��������
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",
				0));// ҽ���������
		readParm = new TParm(INSTJAdm.getInstance().getOwnInfo(parm.getData()));
	}

	/**
	 * 
	 * ȷ����ť
	 */
	public void onOK() {

		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			this.messageBox("��ִ�ж�������");
			return;
		}
		if (!this.emptyTextCheck("PASSWORD")) {
			return;
		}
		readParm.setData("PASSWORD",this.getValue("PASSWORD"));//����
		readParm.setData("RETURN_TYPE", 1);// ����ִ��״̬
		this.setReturnValue(readParm);
		this.closeWindow();
	}

	/**
	 * ��ѡ��ť�¼�
	 */
	public void onExeType() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			onExeEnable(false);
			this.grabFocus("READ_TEXT");
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			onExeEnable(true);
			this.grabFocus("IDNO");
		}
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT", "PASSWORD" };//���ó�ʼֵ
		for (int i = 0; i < name.length; i++) {
			this.setValue(name[i], "");
		}

	}

	/**
	 * ��ִ�в�������
	 * 
	 * @param flg
	 */
	private void onExeEnable(boolean flg) {
		callFunction("UI|IDNO|setEnabled", flg);// ���֤����
		callFunction("UI|PAT_NAME|setEnabled", flg);// ��������
		callFunction("UI|READ_TEXT|setEnabled", flg ? false : true);// ����
		//callFunction("UI|PASSWORD|setEnabled", flg ? false : true);// ����
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ͨ�����֤�����ò�����Ϣ
	 */
	public void onGetInfo() {
		TParm parm = new TParm();
		parm.setData("IDNO", this.getValue("IDNO"));// ���֤����
		TParm result = PatTool.getInstance().getInfoForIdNo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			this.grabFocus("IDNO1");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("�����ڲ�����Ϣ");
			this.grabFocus("IDNO1");
			return;
		}
		// ���û�����ͨ��ѡ���ò�����Ϣ
		if (result.getCount() > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSPatInfo.x", parm);
			if (null == result || null == result.getValue("MR_NO")
					|| result.getValue("MR_NO").length() <= 0) {
				this.setValue("IDNO1", "");
				return;
			}
			this.setValue("MR_NO", result.getValue("MR_NO"));
			this.setValue("PAT_NAME1", result.getValue("PAT_NAME"));
			return;
		}
		this.setValue("MR_NO", result.getValue("MR_NO", 0));
		this.setValue("PAT_NAME1", result.getValue("PAT_NAME", 0));
	}
}
