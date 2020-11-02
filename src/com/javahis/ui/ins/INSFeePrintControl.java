package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.ekt.EKTIO;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.opd.OrderTool;
import jdo.bil.BILInvrcptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * 
 * <p>
 * Title:���ҽ�����շ�
 * </p>
 * 
 * <p>
 * Description:�����շ�ҽ�ƿ��������,��ִ�зָ���ȥ�շ�
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
 * @author pangb 2012.03.29
 * @version 4.0
 */
public class INSFeePrintControl extends TControl {
	private TParm insParm;// ��Ҫ�ָ��ҽ��
	private TParm result;// �������� ������÷ָ��������� �ͷ��ý�������
	private boolean exeError = false;// ִ�в�������
	private boolean exeSplit = false;// �ж��Ƿ�ִ�з��÷ָ�
	private TParm parm;
	private TParm ektParm = null;
	private String caseNo;
	private TParm ektOrderParm = null;//ȥ������ҽ�����������
	//private String idNo;
	private String admType = "";
	//private TParm parmSum;//���е�ҽ��
	private double billAmt;//δ�շѵ�ҽ�����
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		// ȥ���˵���
		// TFrame F=(TFrame)this.getComponent("UI");
		// F.setUndecorated(true);
		callFunction("UI|tButton_1|setEnabled", false);
		callFunction("UI|tButton_10|setEnabled", false);

		parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		//============pangben 2012-7-20 Ʊ�ݺ���У�����
		if (null==parm.getValue("PRINT_NO")||parm.getValue("PRINT_NO").length()<=0) {
			this.messageBox("û�л��Ʊ�ݺ���");
			callFunction("UI|tButton_9|setEnabled", false);
			return;
		}
		TParm parmTemp =new TParm();
		parmTemp.setData("RECP_TYPE","OPB");
		parmTemp.setData("INV_NO",parm.getValue("PRINT_NO"));
		//========��ѯ��Ʊ���Ƿ�ʹ��
		TParm result=BILInvrcptTool.getInstance().selectAllData(parmTemp);
		if (result.getCount()>0) {
			this.messageBox("��Ʊ���Ѿ�ʹ��,����ϵ��Ϣ����");
			callFunction("UI|tButton_9|setEnabled", false);
			return;
		}
		//parmSum=parm.getParm("parmSum");//���е�ҽ�� ��������ҽ�������޸� OPD_ORDER MED_APPLY����
		billAmt=parm.getDouble("billAmt");//δ�շѵ�ҽ�����
		admType = parm.getValue("ADM_TYPE");// ��� �ż���
		ektParm = parm.getParm("ektParm");// ҽ�ƿ���Ϣ
		caseNo = parm.getValue("CASE_NO");// �������
		//idNo = parm.getValue("ID_NO");// ���֤����
		ektOrderParm = parm.getParm("orderParm");//����ҽ�������շ�δ�շ�����
		this.setValue("PRINT_NO", parm.getValue("PRINT_NO"));
		TComboBox com = (TComboBox) this.getComponent("PAY_WAY");
		if (parm.getBoolean("PAY_TYPE")) {
			com.setSelectedIndex(2);// Ĭ�� ҽ�ƿ�֧��
		} else {
			com.setSelectedIndex(1);// Ĭ�� �ֽ�֧��
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("NAME", parm.getValue("NAME"));
		// System.out.println("PARM:::::"+parm);
		this.setValue("INS_TYPE", parm.getValue("INS_TYPE").toString());// ҽ�����

		this.setValue("FeeY", parm.getDouble("FeeY"));// Ӧ�ս��
		this.setValue("INS_FEE", ektParm.getDouble("CURRENT_BALANCE"));// ʵ�ս��
		// =ҽ�����+ҽ�ƿ����
		this.setValue("EKT_SUMAMT", ektParm.getDouble("CURRENT_BALANCE"));// ҽ�ƿ����
		insParm = parm.getParm("insParm");// ��Ҫ�ָ��ҽ��
	}

	/**
	 * �ָ���
	 */
	public void onSplit() {
		callFunction("UI|tButton_9|setEnabled", false);//=======pangben 2013-3-7 ��ӱ����ظ������ť
		TParm spParm = splitRun();
		//=====pangben 2013-3-13 ���У��
		if(null!=result.getValue("NO_EXE_FLG") && result.getValue("NO_EXE_FLG").equals("Y"))
			return;	
		else
			callFunction("UI|tButton_1|setEnabled", true);
		if (spParm == null) {
			this.messageBox("����ʧ��,��ִ�г����������");
			return;
		} else {
			this.messageBox("����ɹ�");
			callFunction("UI|tButton_10|setEnabled", true);
		}
		onNewAmt(spParm);
	}

	/**
	 * ��� ��ʾ
	 * 
	 * @param spParm
	 */
	private void onNewAmt(TParm spParm) {
		// ҽ��֧�����
		this.setValue("INS_AMT", spParm.getDouble("ACCOUNT_AMT"));
		// ҽ�ƿ�֧�����
		this.setValue("EKT_AMT", spParm.getDouble("UACCOUNT_AMT"));
		// ʵ�ս��
		this.setValue("INS_FEE", spParm.getDouble("ACCOUNT_AMT")
				+ this.getValueDouble("EKT_SUMAMT")+this.getValueDouble("FeeY")-billAmt);
		double cashAmt = parm.getDouble("FeeY")
				- this.getValueDouble("INS_FEE");
		// ����
		this.setValue("FeeZ", cashAmt <= 0 ? Math.abs(cashAmt) : -cashAmt);
		// �ֽ�֧�����
		this.setValue("CASH_AMT", cashAmt <= 0 ? 0 : cashAmt);
	}

	/**
	 * ִ�в���
	 */
	public void onOK() {
		// �շѷ�ʽ������Ϊ��
		if (!this.emptyTextCheck("PAY_WAY")) {
			return;
		}
		if (!exeSplit) {
			this.messageBox("û�л��ҽ������");
			return;
		}
		if (this.getValueDouble("INS_FEE") < this.getValueDouble("FeeY")) {
			this.messageBox("����,��ִ��ҽ�������ҽ�ƿ���ֵ����");
			return;
		}
		insParm.setData("comminuteFeeParm", result.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�����
		insParm.setData("settlementDetailsParm", result.getParm(
				"settlementDetailsParm").getData()); // ���ý���
		TParm exeParm = INSTJReg.getInstance()
				.insCommFunction(insParm.getData());
		if (exeParm.getErrCode() < 0) {
			err(exeParm.getErrCode() + " " + exeParm.getErrText());
			this.messageBox(exeParm.getErrText());
			return;
		}
		// opdParm.setData("INS_FLG", "Y"); // ҽ��ʹ��
		// // ��Ҫ�޸ĵĵط�
		// opdParm.setData("MR_NO", pat.getMrNo());
		// opdParm.setData("RECP_TYPE", "OPB"); // �շ�����
		ektParm.setData("NEW_CURRENT_BALANCE", this
				.getValueDouble("EKT_SUMAMT")
				+ this.getValueDouble("INS_AMT"));// ҽ�������ҽ�ƿ��еĽ���ܺ�
		TParm opdParm = insExeUpdate(this.getValueDouble("INS_AMT"), ektParm,
				caseNo, "OPBT");
		if (opdParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		// ����HL7
		TParm resultParm=OPBTool.getInstance().sendHL7Mes(ektOrderParm.getParm("hl7Parm"),parm.getValue("NAME"),false,caseNo);
		if (resultParm.getErrCode() < 0) {
			this.messageBox(resultParm.getErrText());
		}
		exeParm = new TParm();
		exeParm.setData("CASE_NO", caseNo);
		exeParm.setData("MR_NO", this.getValueString("MR_NO"));
		exeParm.setData("INV_NO", this.getValue("PRINT_NO"));
		exeParm.setData("OPT_USER", Operator.getID());
		exeParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("REGION_CODE", Operator.getRegion());
		exeParm.setData("START_INVNO", parm.getValue("START_INVNO"));
		exeParm.setData("TOT_AMT", this.getValueDouble("FeeY"));//Ӧ�ս��
		exeParm.setData("ADM_TYPE", admType); // �Һŷ�ʽ :0 \E
		exeParm.setData("ACCOUNT_AMT", this.getValueDouble("INS_AMT")); //ҽ�����
		
//		TParm selOpdParm = new TParm();
//		selOpdParm.setData("CASE_NO", caseNo);
		TParm opdOrderParm = insParm.getParm("REG_PARM");
		opdOrderParm.setData("INS_AMT", -this.getValueDouble("INS_AMT"));
		opdOrderParm.setData("NAME", this.getValue("NAME"));
		opdOrderParm.setData("INS_FLG", "Y"); // ҽ��ʹ��
		// ��Ҫ�޸ĵĵط�
		opdOrderParm.setData("MR_NO", this.getValue("MR_NO"));
		opdOrderParm.setData("RECP_TYPE", "OPB"); // �շ�����
		exeParm.setData("opdParm", opdOrderParm.getData()); // ���һ�����ܽ��
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBEktprint", exeParm);
		if(result.getErrCode()<0){
			this.messageBox("E0005");
			return ;
		}
		this.messageBox("P0005");
		
		// String type = this.getValue("PAY_WAY").toString();// �շѷ�ʽ
		// result.setData("RETURN_TYPE", 1);// ִ�в���
		// result.setData("PAY_WAY", type);// ֧����ʽ 1.�ֽ� 2.ҽ�ƿ�
		// result.setData("ACCOUNT_AMT", this.getValue("INS_FEE"));
		// result.setData("UACCOUNT_AMT", this.getValue("FeeZ"));
		// �����վݵ�����:ҽ�ƿ��շѴ�Ʊ|�ֽ��շѴ�Ʊ||ҽ����Ʊ
		TParm recpParm = OPBReceiptTool.getInstance().getOneReceipt(
				result.getValue("RECEIPT_NO", 0));
		onPrint(recpParm);
		this.setReturnValue(opdParm);
		this.closeWindow();
	}

	/**
	 * ��ӡƱ�ݷ�װ===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// ������Ա������
		String spPatType = "";
		// ������Ա���
		String spcPerson = "";
		double startStandard = 0.00; // �𸶱�׼
		double accountPay = 0.00; // ����ʵ���ʻ�֧��
		double gbNhiPay = 0.00; // ҽ��֧��
		String reimType = ""; // �������
		double gbCashPay = 0.00; // �ֽ�֧��
		double agentAmt = 0.00; // �������
		double unreimAmt = 0.00;// ����δ�������
		// ҽ����Ʊ����
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			confirmNo = insParm.getValue("CONFIRM_NO");
			insOpdInParm.setData("CASE_NO", caseNo);
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			cardNo = insParm.getValue("CARD_NO");

			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// ����δ����
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.��ְ
			// 2.�Ǿ�
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.��ͨ
			// ������Ա������
			spPatType = insPatparm.getValue("SPECIAL_PAT", 0);
			// ������Ա���
			spcPerson = getSpPatDesc(spPatType);
			//�������
			reimType=insOpdParm.getValue("REIM_TYPE", 0);
			// ��ְ��ͨ
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);

				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// ��ְ����
			if (insCrowdType.equals("1") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
							- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
							- insOpdParm.getDouble("ARMY_AI_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// �Ǿ�����
			if (insCrowdType.equals("2") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
							+ insOpdParm.getDouble("FLG_AGENT_AMT", 0)
							- insOpdParm.getDouble("UNREIM_AMT", 0);

				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
		}
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// Ʊ����Ϣ
		// ����
		oneReceiptParm.setData("PAT_NAME", "TEXT", this.getValue("NAME"));
		// ������Ա���
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				spcPerson.length() == 0 ? "" : spcPerson);
		// ��ᱣ�Ϻ�
		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// ��Ա���
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
		// �������
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "��ͳ");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			if (admType.equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "����");
		}
		// =====zhangp 20120228 modify end
		// ҽ�ƻ�������
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()));
		// �𸶽��
		oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
				startStandard, 2));
		// ����޶����
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "--" : df
				.format(unreimAmt));
		//����δ������ʾ����======pangben 2012-7-12
		oneReceiptParm.setData("MAX_DESC", "TEXT", unreimAmt == 0 ? "" : "����δ�������:");
		//====zhangp 20120925 start
		//�����渶�������걨
		oneReceiptParm.setData("MAX_DESC2", "TEXT", unreimAmt == 0 ? "" : "�����渶�������걨");
		//====zhangp 20120925 end
		// �˻�֧��
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));

		// ���úϼ�
		oneReceiptParm.setData("TOT_AMT", "TEXT", df.format(recpParm.getDouble(
				"TOT_AMT", 0)));
		// ������ʾ��д���
		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
				.numberToWord(recpParm.getDouble("TOT_AMT", 0)));

		// ͳ��֧��
		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
				.getDouble("Overall_pay", 0), 2));
		// ����֧��
		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
				.getDouble("TOT_AMT", 0)));
		// �ֽ�֧��= ҽ�ƿ����+�ֽ�+��ɫͨ��
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH", 0), 2)
				+ StringTool
						.round(recpParm.getDouble("PAY_MEDICAL_CARD", 0), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1", 0), 2);
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// �˻�֧��---ҽ�ƿ�֧��
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);

		// =====zhangp 20120229 modify start
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "ҽ�ƾ���֧��");
			// ҽ�ƾ������
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
		oneReceiptParm.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		// =====zhangp 20120229 modify end
		// ��ӡ����
		oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd"));
		// ҽ�����
		//=============pangben 2012-7-12 ҽ�������ʾ����
//		oneReceiptParm.setData("PAY_DEBIT", "TEXT", gbNhiPay == 0 ? StringTool
//				.round(recpParm.getDouble("PAY_INS_CARD", 0), 2) : df
//				.format(gbNhiPay));
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", gbNhiPay);
		if (recpParm.getDouble("PAY_OTHER1", 0) > 0) {
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "��ɫͨ��֧��");
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1", 0), 2));

		}
		// ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE", 0));

		// ��ӡ��
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", "TEXT", Operator.getID());
		// oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(��������嵥)");
		oneReceiptParm.setData("CARD_CODE", "TEXT", "");//=====pangben 2012-8-15 ����ʾ����
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
						.getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE0" + i, 0));
			} else {
				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
						.getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
						.getData("CHARGE" + i, 0));
			}
		}
		// =================20120219 zhangp modify start
		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
				.getDouble("CHARGE01", 0)
				+ recpParm.getDouble("CHARGE02", 0)));
		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", admType);
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
	    oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("RECEIPT_NO", 0));//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
//				oneReceiptParm, true);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
	                         IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm), true);//����ϲ�modify by wanglong 20130730

	}

	/**
	 * ҽ�ƿ���Ʊ��ϸ���
	 * 
	 * @param oneReceiptParm
	 *            TParm
	 * @param recpParm
	 *            TParm
	 * @param dparm
	 *            TParm
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO", 0).toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (oneReceiptParm.getCount() > 10) {
			oneReceiptParm.setData("DETAIL", "TEXT", "(���������ϸ��)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * ������Ա���
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "�˲о���";
		if ("06".equals(type))
			return "����Ա";
		if ("07".equals(type))
			return "����������Ա";
		if ("08".equals(type))
			return "�Ÿ�����";
		return "";
	}

	/**
	 * ҽ�ƿ���������˴�ҽ�����ۿ���
	 * 
	 * @param returnParm
	 * @return
	 */
	private TParm insExeUpdate(double accountAmt, TParm readCardParm,
			String caseNo, String business_type) {
		// ���:AMT:���β������ BUSINESS_TYPE :���β������� CASE_NO:�������
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);//ҽ��֧�����
		orderParm.setData("INS_AMT", accountAmt);//ҽ��֧�����
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		//orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("readCard", readCardParm.getData());
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("FeeY", this.getValueDouble("FeeY"));// Ӧ�ս��
		orderParm.setData("OPT_TERM", Operator.getIP());
		orderParm.setData("orderParm", ektOrderParm.getData());// ҽ�ƿ�ִ��ҽ��
		orderParm.setData("billAmt", billAmt);// δִ�е�ҽ�����
		orderParm.setData("INS_EXE_FLG", "Y");// �����շ�ҽ�ƿ�����,ҽ���ָ��� ����
		orderParm.setData("PRINT_NO", this.getValue("PRINT_NO"));
		orderParm.setData("CONFIRM_NO",insParm.getValue("CONFIRM_NO"));
		orderParm.setData("RECP_TYPE",insParm.getValue("RECP_TYPE"));
		//orderParm.setData("parmSum",parmSum.getData());
		//orderParm.setData("ORDER", parm.getParm("ORDER").getData());
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}

	/**
	 * ִ�зָ����
	 * 
	 * @return
	 */
	private TParm splitRun() {
		if (null == insParm
				|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
			this.messageBox("û����Ҫִ�е�����");
			return null;
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
		regParm.setData("FeeY", parm.getDouble("FeeY"));
		regParm.setData("CASE_NO_SUM", caseNo);
		regParm.setData("OPB_RECP_TYPE", "Y");//�����շѲ���
		result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(regParm);// ���÷ָ�
		exeError = true;// �����ۼ�
		// ����ҽ��
		if (result.getErrCode() < 0) {
			this.messageBox("�ָ���ִ���:" + result.getErrText());
			exeSplit = false;
			this.grabFocus("tButton_1");
			return null;
		} else {
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				this.messageBox(result.getValue("MESSAGE"));
				exeSplit = false;
				this.grabFocus("tButton_1");
			    return null;
			} else {
				exeSplit = true;// ִ�з��÷ָ����
				this.grabFocus("tButton_10");
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// ���ý������
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(regParm);
		TParm accountParm = getAmt(regParm.getInt("INS_TYPE"), parm);
		return accountParm;

	}

	public TParm getAmt(int insType, TParm returnParm) {
		// ȡ��ҽ��ר�����֧�����
		double sOTOT_Amt = 0.00;
		// ȡ���ֽ�֧�����
		double sUnaccount_pay_amt = 0.00;

		// ��ְ
		// System.out.println("��ְreturnParm:::"+returnParm);

		if (insType == 1) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // �ܽ��
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // ���˻�֧��
					returnParm.getDouble("UNREIM_AMT", 0);// ����δ����
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);// �ֽ�֧�����
		}
		// ��ְ����
		if (insType == 2) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- returnParm.getDouble("UNREIM_AMT", 0);
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		// �Ǿ�����
		if (insType == 3) {
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						- returnParm.getDouble("UNREIM_AMT", 0);

			} else {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0);

			}

			// �ֽ�֧��
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)
					- returnParm.getDouble("FLG_AGENT_AMT", 0)
					- returnParm.getDouble("ARMY_AI_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);
		// parm.setData("ACCOUNT_AMT", 1.5);
		// parm.setData("UACCOUNT_AMT", 3.5);
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
			if (this.messageBox("��ʾ", "�Ƿ�ִ�н��㳷������", 2) != 0) {
				return;
			}
			TParm result = INSTJFlow.getInstance().cancelBalance(insParm);// ȡ�����ý������
			if (result.getErrCode() < 0) {
				// System.out.println("�������ý������:" + result.getErrText());
				this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			} else {
				INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
				INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
				TParm parm=new TParm();
				parm.setData("CASE_NO",insParm.getValue("CASE_NO"));
				parm.setData("EXE_USER",Operator.getID());
				parm.setData("EXE_TERM",Operator.getIP());
				parm.setData("EXE_TYPE",insParm.getValue("RECP_TYPE"));//����
				INSRunTool.getInstance().deleteInsRun(parm);//ȡ������ɾ����;״̬
				this.messageBox("P0005");
				this.setValue("INS_FEE", 0.00);
				this.setValue("INS_AMT", 0.00);
				this.setValue("EKT_AMT", 0.00);
				this.setValue("CASH_AMT", 0.00);
				// �ָ��Ľ��
				this.setValue("FeeZ", 0.00);
				callFunction("UI|tButton_1|setEnabled", false);
				callFunction("UI|tButton_10|setEnabled", false);
				exeSplit = false;
				callFunction("UI|tButton_9|setEnabled", true);
			}
		}

	}

	/**
	 * ��������
	 */
	public void onReadEKT() {
		// ��ȡҽ�ƿ�
		ektParm = EKTIO.getInstance().TXreadEKT();
		if (null == ektParm || ektParm.getErrCode() < 0
				|| ektParm.getValue("MR_NO").length() <= 0) {
			this.messageBox(ektParm.getErrText());
			ektParm = null;
			return;
		}
		// �жϲ����Ƿ���ͬ
		if (!ektParm.getValue("MR_NO").equals(this.getValue("MR_NO"))) {
			this.messageBox("��ҽ�ƿ�������Ϣ�뵱ǰ��������");
			return;
		}
		this.setValue("EKT_SUMAMT", ektParm.getDouble("CURRENT_BALANCE"));// ҽ�ƿ����
		
		double cashAmt = billAmt
				- this.getValueDouble("INS_AMT")
				- this.getValueDouble("EKT_SUMAMT");
		// ʵ�ս��
		this.setValue("INS_FEE", this.getValueDouble("FeeY")-cashAmt);//=====pangben 2013-3-1�޸���ʾ�������
		// ����
		this.setValue("FeeZ", cashAmt <= 0 ? Math.abs(cashAmt) : -cashAmt);
		// �ֽ�֧�����
		this.setValue("CASH_AMT", cashAmt <= 0 ? 0 : cashAmt);
	}

	/**
	 * ��ֵ
	 */
	public void onFee() {
		TParm parm =new TParm();
		parm.setData("FLG","Y");
		parm = (TParm) openDialog("%ROOT%\\config\\ekt\\EKTTopUp.x",
				parm);
		onReadEKT();
	}
}
