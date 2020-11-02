package com.javahis.ui.ins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.ins.INSTJAdm;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.ins.INSTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import manager.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.FileTool;

/**
 * 
 * <p>
 * Title:ҽ����ˢ������
 * </p>
 * 
 * <p>
 * Description:ҽ����ˢ������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2011-12-05
 * @version 2.0
 */
public class INSConfirmApplyCardControl extends TControl {
	private TParm readParm = new TParm();// ˢ������
	// private String case_no;// �����
	private String mr_no;// ��������
	int card_Type = 0;// �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ��5�����صǼǣ�
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
		// case_no = parm.getValue("CASE_NO");
		mr_no = parm.getValue("MR_NO");
		card_Type = parm.getInt("CARD_TYPE");
		int insType=parm.getInt("INS_TYPE");//�����շ�ҽ���Һ�������
		callFunction("UI|PASSWORD|setEnabled", false);//���벻�ɱ༭
		onExeEnable(false);
		switch (card_Type) {
		case 3://�����շ�
			callFunction("UI|INS_READ_TYPE|setEnabled", false);
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
			if (insType==1) {
				this.setValue("INS_READ_TYPE", "1");// 1.��ְ2.�Ǿ�
				this.setValue("INS_PAT_TYPE", "1");// 1.��ͨ2.����
			}else if(insType==2){
				this.setValue("INS_READ_TYPE", "1");// 1.��ְ2.�Ǿ�
				this.setValue("INS_PAT_TYPE", "2");// 1.��ͨ2.����
			}else if(insType==3){
				this.setValue("INS_READ_TYPE", "2");// 1.��ְ2.�Ǿ�
				this.setValue("INS_PAT_TYPE", "2");// 1.��ͨ2.����
			}
			break;
		case 4:// סԺ
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
			this.setValue("INS_PAT_TYPE", "");// 1.��ͨ2.����
			break;
		case 5:// ���صǼ�
			this.setValue("INS_READ_TYPE", parm.getValue("INS_CROWD_TYPE"));// 1.��ְ2.�Ǿ�
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);
			this.setValue("INS_PAT_TYPE", 2);// 1.��ͨ2.����
			break;
		}
		this.setValue("PASSWORD", "111111");//Ĭ������
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
	 * ˢ������
	 */
	public void readCard() {	
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			onReadCard();
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			onReadIdNo();
		}	
		// ִ��
		// U
		// ������
		// A
		// ����
		if (readParm.getErrCode() < 0) {
			this.messageBox(readParm.getErrText());
			return;
		}
		this.setValue("NHI_NO", readParm.getValue("CARD_NO"));
		String insReadType = readParm.getValue("CROWD_TYPE");// ��Ⱥ���
		this.setValue("INS_READ_TYPE", insReadType);
		this.grabFocus("tButton_0");//ȷ����ý���
		if (card_Type == 5 || card_Type == 4 || card_Type==3) {// ���صǼ�// סԺ
			return ;
		}
		// 1.��ְ 2.�Ǿ�
		TComboBox com = (TComboBox) this.getComponent("INS_PAT_TYPE");
		if (this.getValueString("INS_READ_TYPE").equals("1")) {
			callFunction("UI|INS_PAT_TYPE|setEnabled", true);
			com.setSelectedIndex(1);// ��ͨĬ��
		} else if (this.getValueString("INS_READ_TYPE").equals("2")) {
			callFunction("UI|INS_PAT_TYPE|setEnabled", false);// �ڶ���combo ������ѡ��
			com.setSelectedIndex(2);// ����Ĭ��
		}
		
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
	 * ҽ��������
	 */
	private void onReadCard(){
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
		readParm = new TParm(INSTJReg.getInstance().readINSCard(parm.getData()));// ��������
	}
	/**
	 * ȷ����ť
	 */
	public void onOK() {

		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			this.messageBox("��ִ�ж�������");
			return;
		}
		readParm.setData("RETURN_TYPE", 1);// ����ִ��״̬
		if (this.getValueInt("INS_PAT_TYPE")==2) {
			if (null==this.getValue("DISEASE_CODE") || this.getValue("DISEASE_CODE").toString().length()<=0) {//���ز��ֲ�����Ϊ��
				this.messageBox("���ز��ֲ�����Ϊ��");
				this.grabFocus("DISEASE_CODE");
				return;
			}
			readParm.setData("DISEASE_CODE",this.getValue("DISEASE_CODE"));
		}
		if (card_Type == 5 || card_Type == 4) {// ���صǼǺ�סԺ
			this.setReturnValue(readParm);
			this.closeWindow();
			return;
		}
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			if (!this.emptyTextCheck("INS_READ_TYPE,INS_PAT_TYPE,PASSWORD")) {
				return;
			}
		}else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			if (!this.emptyTextCheck("IDNO,PASSWORD")) {
				return;
			}
		}
		readParm.setData("PASSWORD",this.getValue("PASSWORD"));//����
		//System.out.println("readParm::::"+readParm);
		//System.out.println("ִ��");
		// ��ְ��ͨ
		int insType = 0;
		TParm testParm = new TParm();
		if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("1")) {
			insType = 1;
			//System.out.println("��ְ��ͨ");
			testParm = INSTJFlow.getInstance().insIdentificationChZPt(readParm);
			String ctzCode=testParm.getValue("CTZ_CODE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));//��Ա���
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// ����ִ��״̬
				return;
			}
			// ��ְ����
		} else if (this.getValue("INS_READ_TYPE").equals("1")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 2;
			//System.out.println("��ְ����");
			readParm.setData("PAY_KIND", 13);
			// ��ְ����ˢ�����ز���,�õ�������Ϣ
			testParm = INSTJFlow.getInstance().insCreditCardChZMt(readParm);
			//System.out.println("testParm:"+testParm);
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// ����ִ��״̬
				return;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
		} else if (this.getValue("INS_READ_TYPE").equals("2")
				&& this.getValue("INS_PAT_TYPE").equals("2")) {
			insType = 3;
			//System.out.println("�Ǿ�����");
			readParm.setData("PAY_KIND", 41);
			// �Ǿ�����ˢ�����ز���,�õ�������Ϣ
			testParm = INSTJFlow.getInstance().insCreditCardChJMt(readParm);
			
			if (testParm.getErrCode() < 0) {
				this.messageBox(testParm.getErrText());
				readParm.setData("RETURN_TYPE", 0);// ����ִ��״̬
				return;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(this.getValueInt("INS_READ_TYPE"),ctzCode);
			if (ctzParm.getErrCode()<0) {
				this.messageBox("E0005");
				return ;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
		}
		//System.out.println("ˢ�����ز���,�õ�������Ϣ::"+testParm);
		if (null == testParm || null == testParm.getValue("CONFIRM_NO")) {
			this.messageBox("ִ��ˢ������ʧ��,û�л������");
			return;
		}
		testParm.setData("BED_FEE",regionParm.getValue("TOP_BEDFEE",0));//��λ��
		testParm.setData("REGION_CODE",regionParm.getValue("NHI_NO", 0));// ҽ���������
		readParm.setData("opbReadCardParm", testParm.getData());// �ʸ�ȷ�������
		readParm.setData("CONFIRM_NO", testParm.getValue("CONFIRM_NO"));// ���ؾ�ҽ˳���
		readParm.setData("INS_TYPE", insType);
		readParm.setData("RETURN_TYPE", 1);// �������� 1.�ɹ� 2.ʧ��
		readParm.setData("INS_PAT_TYPE", this.getValue("INS_PAT_TYPE"));// 1.��ͨ
		readParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));//���ز���
		// 2.����
		readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		this.setReturnValue(readParm);
		this.closeWindow();
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
		if (flg) {
			this.grabFocus("IDNO");
		}else{
			this.grabFocus("READ_TEXT");
		}
		//callFunction("UI|PASSWORD|setEnabled", flg ? false : true);// ����
	}
	/**
	 * ��ѡ��ť�¼�
	 */
	public void onExeType() {
		if (this.getRadioButton("READ_CARD").isSelected()) {// ����
			onExeEnable(false);
		} else if (this.getRadioButton("READ_IDNO").isSelected()) {// ���֤
			onExeEnable(true);
		}
		String[] name = { "IDNO", "PAT_NAME", "READ_TEXT", "PASSWORD","DISEASE_CODE","NHI_NO" };//���ó�ʼֵ
		for (int i = 0; i < name.length; i++) {
			this.setValue(name[i], "");
		}

	}
	private TParm sysCtzParm(int type,String ctzCode){
		String ctzSql="SELECT CTZ_CODE FROM SYS_CTZ WHERE NHI_NO='"+ctzCode+"' AND NHI_CTZ_FLG='Y'";
		if (type==1) {
			ctzSql+=" AND CTZ_CODE IN('11','12','13')";
		}
		if (type==2) {
			ctzSql+=" AND CTZ_CODE IN('21','22','23')";
		}
		TParm ctzParm=new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm.getErrCode()<0) {
			//this.messageBox("E0005");
			return ctzParm;
		}
		return ctzParm;
	}
}
