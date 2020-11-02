package com.javahis.ui.ins;

import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title:���ҽ�����շ�
 * </p>
 * 
 * <p>
 * Description:ִ����ʾҽ��Ԥ�ָ��ѡ���շѷ�ʽ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangb 2011.12.28
 * @version 1.0
 */
public class INSFeeControl extends TControl {
	private TParm insParm;// ��Ҫ�ָ��ҽ��
	private boolean feeFlg;// �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
	private TParm result;// �������� ������÷ָ��������� �ͷ��ý�������
	private boolean exeError = false;// ִ�в�������
	private boolean exeSplit = false;// �ж��Ƿ�ִ�з��÷ָ�
	private TParm parm;//������Ϣ�������������Ϻͷָ����ݣ�
	private String accountamtforreg="";//�����˻������ڹҺ��������ʾ��


	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ȥ���˵���
//        TFrame F=(TFrame)this.getComponent("UI");
//        F.setUndecorated(true);
		callFunction("UI|tButton_1|setEnabled", false);//����ȡ����ť�ûң�����ʹ�ã�
		callFunction("UI|tButton_0|setEnabled", false);//ִ�а�ť�ûң�����ʹ�ã�
		
		parm = (TParm) getParameter();//������Ϣ�������������Ϻͷָ����ݣ�
		//���޴�����Ϣ����
		if (null == parm) {
			return;
		}
		TComboBox com = (TComboBox) this.getComponent("PAY_WAY");//֧����ʽ
		//PAY_TYPE: Y  ҽ�ƿ�֧��,N �ֽ�֧��
		if (parm.getBoolean("PAY_TYPE")) {
			com.setSelectedIndex(2);// Ĭ�� ҽ�ƿ�֧��
		} else {
			com.setSelectedIndex(1);// Ĭ�� �ֽ�֧��
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));//���˵Ĳ����ţ�������ʾ��
		this.setValue("NAME", parm.getValue("NAME"));//����������������ʾ��
		// �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
		feeFlg = parm.getBoolean("FEE_FLG");
		//System.out.println("PARM:::::"+parm);
		this.setValue("INS_TYPE", parm.getValue("INS_TYPE").toString());// ҽ����𣨽�����ʾ��
		this.setValue("FeeY", parm.getDouble("FeeY"));// Ӧ�ս�������ʾ��
		insParm = parm.getParm("insParm");// ��Ҫ�ָ������
		// TParm opbReadCardParm=insParm.getParm("opbReadCardParm");
		//		
		// opbReadCardParm.setData("CONFIRM_NO", "MT05511202221342");// ����˳��� E
		// // ��
		// // L��������
		// // result.addData("CASE_NO", insParm.getValue("CASE_NO"));// ����� A
		// // �����еĲ���
		// //ssss.addData("HOSP_NHI_NO", insParm.getValue("REGION_CODE"));//
		// ҽԺ����
		// insParm.setData("opbReadCardParm",opbReadCardParm.getData());
		// INSTJFlow.getInstance().cancelBalance(insParm);//ȡ�����ý������
	}

	/**
	 * �ָ���
	 */
	public void onSplit() {
		callFunction("UI|tButton_9|setEnabled", false);//����ظ������ť��ɵ�����
		TParm spParm = splitRun();//ִ�зָ����
		//����ȡ����ť�ָ�ʹ�ã����ڽ���ʧ�����������̣�
		callFunction("UI|tButton_1|setEnabled", true);
		if (spParm == null) {
			this.messageBox("����ʧ��,��ִ�г����������");
			return;
		} else {
			this.messageBox("����ɹ�");	
			callFunction("UI|tButton_0|setEnabled", true);//ִ�а�ť�ָ�ʹ��
		}
		//ҽ����������ʾ��
		this.setValue("INS_FEE", spParm.getValue("ACCOUNT_AMT"));
		// �շѽ�������ʾ��
		this.setValue("FeeZ", spParm.getValue("UACCOUNT_AMT"));
		//�����˻������ڹҺ��������ʾ��
		accountamtforreg = spParm.getValue("ACCOUNT_AMT_FORREG");
	}

	/**
	 * ִ�в���
	 */
	public void onOK() {
		// �շѷ�ʽ������Ϊ��
		if (!this.emptyTextCheck("PAY_WAY")) {
			return;
		}
		//û�л��ҽ�����ݷ���
		if (!exeSplit) {
			this.messageBox("û�л��ҽ������");
			return;
		}
		String type = this.getValue("PAY_WAY").toString();// �շѷ�ʽ
		result.setData("RETURN_TYPE", 1);// ִ�в���
		result.setData("PAY_WAY", type);// ֧����ʽ 1.�ֽ� 2.ҽ�ƿ�
		result.setData("ACCOUNT_AMT", this.getValue("INS_FEE"));//ҽ�����
		result.setData("UACCOUNT_AMT", this.getValue("FeeZ"));//�ָ��Ľ��
		result.setData("ACCOUNT_AMT_FORREG",accountamtforreg);//�����˻�
		this.setReturnValue(result);//���ص��ҺŽ��������
		this.closeWindow();//�رմ���
	}

	/**
	 * ִ�зָ����
	 * 
	 * @return
	 */
	private TParm splitRun() {
		//ִ���շѲ���ʱ�ж��Ƿ�ִ�зָ�
		if (feeFlg) {
			if (null == insParm
					|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
				this.messageBox("û����Ҫִ�е�����");
				return null;
			}
		}
		// �Ǿ����� ������DataDown_cmts, ����ˢ�����ף�E��,�õ�������Ϣ
		// ��ְ��ͨ: �õ�������Ϣ ���ú���DataDown_czys, ����ˢ����L��
		// ��ְ���� : ����DataDown_mts, ����ˢ�����ף�E��,�õ�������Ϣ
		// //System.out.println("ruleParm::::::"+ruleParm);
		// double insSum = 0.00;// ���÷ָ��ۼƽ��
		// if (feeFlg) {// �շ�
		// return exeFee(insParm);
		// }
		return exeFee(insParm);
	}

	/**
	 * ִ���շѲ���
	 * 
	 * @param regParm
	 * @param opbReadCardParm
	 * @return
	 */
	private TParm exeFee(TParm regParm) { 
		// ִ�й��õķ��÷ָ���ins_opd_order ����ϸ�ϴ�����
		// ִ�з��÷ָ� ������DataDown_sp1 ���� B
		// ִ���ϴ���ϸ ����: ����DataUpload,��B������
		regParm.setData("NEW_REGION_CODE", Operator.getRegion());// �������
		regParm.setData("FeeY", parm.getDouble("FeeY"));//�շѽ��
		result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(regParm);// ���÷ָ�
		exeError = true;// �����ۼ�
		// ���÷ָ���ִ���
		if (result.getErrCode() < 0) {
			this.messageBox("�ָ���ִ���:" + result.getErrText());
			exeSplit = false;//��ִ�з��÷ָ�
			this.grabFocus("tButton_1");//ѡ�����ȡ����ť
			return null;
		} else {//���÷ָ�ɹ�
			//��������;���� �����ݿ����INSAMT_FLG=1������
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				exeSplit = false;//��ִ�з��÷ָ�
				this.grabFocus("tButton_1");//ѡ�����ȡ����ť
			} else {
				exeSplit = true;// ִ�з��÷ָ����
				this.grabFocus("tButton_0");//ѡ��ִ�а�ť
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// ���ý������
		//��ô�ӡƱ�ݣ����Һ�����������
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(regParm);
		//���ҽ��ר�����֧�����ֽ�֧�����͸����˻��������
		TParm accountParm = getAmt(regParm.getInt("INS_TYPE"), parm);
		return accountParm;

	}

	public TParm getAmt(int insType, TParm returnParm) {
		// ȡ��ҽ��ר�����֧�����
		double sOTOT_Amt = 0.00;
		// ȡ���ֽ�֧�����
		double sUnaccount_pay_amt = 0.00;
		// ȡ�ø����ʻ�֧�����
		double account_pay_amt = 0.00;
		
		account_pay_amt = returnParm.getDouble("ACCOUNT_PAY_AMT", 0);
		// ��ְ
		//System.out.println("��ְreturnParm:::"+returnParm);
		
		if (insType == 1) {
			//ȡ��ҽ��ר�����֧�����
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // �ܽ��
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // ���˻�֧��
					returnParm.getDouble("UNREIM_AMT", 0);//����δ�������
			// ȡ���ֽ�֧�����
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);//�ֽ�֧�����
		}
		// ��ְ����
		if (insType == 2) {
			//ȡ��ҽ��ר�����֧�����
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)//// �ܽ��
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)// ���˻�֧��
					- returnParm.getDouble("UNREIM_AMT", 0);//����δ�������
			// ȡ���ֽ�֧�����
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)// ���˻�֧��
					+ returnParm.getDouble("UNREIM_AMT", 0);//����δ�������
		}
		// �Ǿ�����
		if (insType == 3) {
			//�л���δ��������
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				//ȡ��ҽ��ר�����֧�����
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)//���ͳ����
						+ returnParm.getDouble("ARMY_AI_AMT", 0)//�������
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)//���������
						- returnParm.getDouble("UNREIM_AMT", 0);//����δ�������

			} else {
				//�ǻ���δ��������
				//ȡ��ҽ��ר�����֧�����
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)//���ͳ����
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)//���������
						+ returnParm.getDouble("ARMY_AI_AMT", 0);//�������

			}

			// ȡ���ֽ�֧�����
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)// �ܽ��
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)//���ͳ����
					- returnParm.getDouble("FLG_AGENT_AMT", 0)//���������
					- returnParm.getDouble("ARMY_AI_AMT", 0)//�������
					+ returnParm.getDouble("UNREIM_AMT", 0);//����δ�������
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);//ҽ��ר�����֧�����
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);//�ֽ�֧�����
		parm.setData("ACCOUNT_AMT_FORREG", account_pay_amt);//�����ʻ�֧�����
//		parm.setData("ACCOUNT_AMT", 1.5);
//		parm.setData("UACCOUNT_AMT", 3.5);
		return parm;
	}

	/**
	 * 
	 * ִ���˷Ѳ���
	 * 
	 * @return
	 */
	public double reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getInt("INS_PAT_TYPE", 0) == 1) {
			return result.getDouble("NHI_AMT");
		} else {
			return result.getDouble("INS_PAY_AMT");
		}

	}

	/**
	 * ȡ������
	 */
	public void onCancel() {
		// System.out.println("EXEERROR::" + exeError);
		// �������ý������
		if (exeError) {
			if (this.messageBox("��ʾ","�Ƿ�ִ�н��㳷������",2)!=0) {
				return;
			}
			//��÷��ý�������
			TParm result = INSTJFlow.getInstance().cancelBalance(insParm);
			//û��ȡ�÷��ý�������
			if (result.getErrCode() < 0) {
				// System.out.println("�������ý������:" + result.getErrText());
				this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			} else {
				INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ��INS_OPD������
				INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);// ɾ��INS_OPD_ORDER������
				TParm parm=new TParm();
				parm.setData("CASE_NO",insParm.getValue("CASE_NO"));//���˾����
				parm.setData("EXE_USER",Operator.getID());//������Ա
				parm.setData("EXE_TERM",Operator.getIP());//������ַ
				parm.setData("EXE_TYPE",insParm.getValue("RECP_TYPE"));//���ͣ�REG �Һ�,REGT �˹�
				INSRunTool.getInstance().deleteInsRun(parm);//ȡ������ɾ����;״̬
				this.messageBox("P0005");
				//ҽ����������ʾ��
				this.setValue("INS_FEE",0.00);
				// �շѽ�������ʾ��
				this.setValue("FeeZ", 0.00);
				//�����˻���������ص�ԭʼ״̬��
				accountamtforreg ="";
				callFunction("UI|tButton_1|setEnabled", false);//����ȡ����ť�ûң�����ʹ�ã�
				callFunction("UI|tButton_0|setEnabled", false);//ִ�а�ť�ûң�����ʹ�ã�
				exeSplit=false;//��ִ�з��÷ָ�
				callFunction("UI|tButton_9|setEnabled", true);//����ظ������ť��ɵ�����
			}
		}
		
	}
}
