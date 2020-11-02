package com.javahis.ui.ekt;

import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTNewIO;
import jdo.odo.OpdRxSheetTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TDialog;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ҽ�ƿ��ɷѽ���
 * </p>
 * 
 * <p>
 * Description:ҽ�ƿ��ɷѽ���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author sundx
 * @version 1.0
 */
public class EKTChargeControl extends TControl {

	private String caseNo;
	private String mrNo;
	private String cardNo;
	private double oldAmt = 0.00;
	private double amt;//��ʾ���
	private TParm readCard;//ҽ�ƿ�����
	private String ektTradeType;// ��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
	private String businessType;//�˴β��������� ODO OPB REG
	private TParm result;// ҽ�ƿ���ɫͨ�����
	private String insFlg;// ҽ����ע�ǣ�ҽ�ƿ�������Ӳ���
	private double insAmt;// ҽ�����
	//private double sumOpdorderAmt;// δ��Ʊҽ���ܽ��
	private double exeAmt;// �˴β������
	//private TParm newParm;//����ҽ��վҽ������Ҫ������ҽ��(��ɾ�����ݼ���)
	private String tradeSumNo;//�˴β����Ѿ��շѵ��ڲ����׺���
	private String odoEktFlg;//==pangben 2013-7-17����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�
	private TParm reParm;//==pangben 2013-7-17ҽ��վҽ����������UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX д��һ������
	private double payOther3 = 0;
	private double payOther4 = 0;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// ȥ���˵���
		TDialog F = (TDialog) this.getComponent("UI");
		F.setUndecorated(true);
		onInitParm((TParm) getParameter());
	}

	/**
	 * ��ʼ������
	 * 
	 * @param parm
	 *            TParm
	 */
	private void onInitParm(TParm parm) {
		// System.out.println("����====="+parm);
		if (parm == null)
			return;
		payOther3 = parm.getDouble("PAY_OTHER3");
		payOther4 = parm.getDouble("PAY_OTHER4");
		readCard = parm.getParm("READ_CARD");
		if (readCard.getErrCode() != 0) {
			((TButton) getComponent("tButton_0")).setEnabled(false);
			setValue("Message_Text", readCard.getErrText());
		}
		cardNo = readCard.getValue("CARD_NO");//����
		setValue("READ_CARD_NO", cardNo);
		setValue("READ_NAME1", readCard.getValue("PAT_NAME"));//����
		setValue("READ_SEX1", readCard.getValue("SEX"));//�Ա�
		caseNo = parm.getValue("CASE_NO");//�����
		tradeSumNo=parm.getValue("TRADE_SUM_NO");//�˴β����Ѿ��շѵ��ڲ����׺���,��ʽ'xxx','xxx'
		if (null == caseNo) {
			return;
		}
		odoEktFlg=parm.getValue("ODO_EKT_FLG");//==pangben 2013-7-17����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�
		reParm=parm.getParm("RE_PARM");//==pangben 2013-7-17ҽ��վҽ����������UPDATE OPD_ORDER BILL='Y',BUSINESS_NO=XXX д��һ������
		amt = parm.getDouble("AMT");//��ʾ�Ľ��
		mrNo = parm.getValue("MR_NO");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		ektTradeType = parm.getValue("EKT_TRADE_TYPE");// ��ѯ����
		businessType = parm.getValue("BUSINESS_TYPE");// �������ݲ���
		insFlg = parm.getValue("INS_FLG");// ҽ����ע��
		insAmt = parm.getDouble("INS_AMT");// ҽ�����
		//sumOpdorderAmt = parm.getDouble("SUMOPDORDER_AMT");
		exeAmt = parm.getDouble("EXE_AMT");//ҽ��վ�˴β������
		//newParm = parm.getParm("newParm");
		// �����շ�ҽ���ۿ�س�ҽ�ƿ�����ִ��ȡ������
		if (null != parm.getValue("OPBEKTFEE_FLG")
				&& parm.getBoolean("OPBEKTFEE_FLG")) {
			callFunction("UI|tButton_1|setEnabled", false);
		}
		if (!onCheck()) {
			this.messageBox("ҽ�ƿ���ʼ��ʧ��!");
			return;
		}
		if ("en".equals(getLanguage())) {
			setValue("PAT_NAME", OpdRxSheetTool.getInstance().getPatEngName(
					mrNo));
		} else {
			setValue("PAT_NAME", readCard.getValue("PAT_NAME"));
		}
		setValue("SEX_CODE", readCard.getValue("SEX_CODE"));
		setValue("MR_NO", mrNo);
		setValue("CARD_NO", cardNo);
		// setValue("OLD_AMT",oldAmt);
		setValue("AMT", amt);
		// setValue("NEW_AMT",oldAmt - amt);
	}

	public void onOK() {
		// zhangp 20111230 ��֤
		TParm parm = new TParm();
		if (readCard.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			// TParm parm = new TParm();
			parm.setErr(-1, "��ҽ�ƿ���Ч");
			setReturnValue(parm);
			closeWindow();
		}
		parm.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
		if (this.getValueDouble("NEW_AMT") < 0) {
			messageBox_("�û�����,���β���û�пۿ�!");
			parm.setData("OP_TYPE", 2);
		} else {
			TParm cp = new TParm();
			cp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));
			cp.setData("CASE_NO", caseNo);
			cp.setData("MR_NO", mrNo);
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			// cp.setData("BUSINESS_NO",businessNo);
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("EKT_TRADE_TYPE", ektTradeType);//�Һ�ʹ�� REG_PATADM ��û��TRADE_NO �ֶ� 
			cp.setData("OLD_AMT", oldAmt);// ҽ�ƿ����
			cp.setData("NEW_AMT", this.getValue("NEW_AMT"));
			cp.setData("INS_FLG", insFlg);// ҽ����ע��
			// zhangp 20120106 ����seq
			cp.setData("SEQ", readCard.getValue("SEQ"));// ���
			//cp.setData("SUMOPDORDER_AMT", sumOpdorderAmt);
			cp.setData("PAY_OTHER3", payOther3);
			cp.setData("PAY_OTHER4", payOther4);
			// �ۿ�
			if (amt >= 0) {
				onFee(cp);
			} else {
				// �˷�
				onUnFee(cp);
			}
			//parm.setData("TRADE_NO",tradeSumNo);
			//��Ҫ�����Ľ��׽��
			//TParm ektTradeSumParm=EKTNewTool.getInstance().selectEktTradeUnSum(parm);
			// ҽ�ƿ������ڴ˴οۿ���
			if (null != result && result.getCount() > 0) {
				EKTNewIO.getInstance().onExeGreenFee(oldAmt, amt, businessType, cp, exeAmt, caseNo, result);
			}else{
				cp.setData("EKT_USE", exeAmt);// ҽ�ƿ��ۿ���
				cp.setData("GREEN_USE",0.00);//��ɫͨ���ۿ���
				cp.setData("EKT_OLD_AMT",oldAmt+exeAmt-amt);//ҽ�ƿ����ڲ���֮ǰ�Ľ�� �����˴ζ����Ĵ���ǩ�����н�� �س� ��õ�ǰ ҽ�ƿ��Ľ�
				cp.setData("GREEN_BALANCE",0.00);//������ۿ���
			}
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			cp.setData("TRADE_SUM_NO",tradeSumNo);////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
			if (null!=odoEktFlg&&odoEktFlg.equals("Y")) {
				cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬��ӵ�һ��������
				cp.setData("OPB_AMT",amt);// ����ҽ��վ�������=====pangben 2013-7-17
				cp.setData("ODO_EKT_FLG",odoEktFlg);// ����ҽ��վ����,����EKTChageUI.x��������ҽ��վ�������շѽ��湫���߼�=====pangben 2013-7-17
			}
			// ̩��ҽԺ�ۿ����
			TParm p = new TParm(EKTNewIO.getInstance().onNewSaveFee(
					cp.getData()));
			// TParm p = EKTIO.getInstance().consume(cp);
			if (p.getErrCode() < 0)
				parm.setErr(-1, p.getErrText());
			else {
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", p.getValue("TRADE_NO"));
				parm.setData("OLD_AMT", oldAmt);
				parm.setData("NEW_AMT", this.getValue("NEW_AMT"));
				parm.setData("EKTNEW_AMT", cp.getDouble("EKT_AMT"));// ҽ�ƿ��еĽ��
				parm.setData("CANCLE_TREDE", p.getValue("CANCLE_TREDE"));// �˷Ѳ���ִ�е�ҽ�ƿ��ۿ�����TREDE_NO��Ϣ
				if (null != result && result.getCount() > 0) {
					parm.setData("AMT", amt < 0 ? amt * (-1) : amt); // �շѽ��
					parm.setData("EKT_USE", cp.getDouble("EKT_USE")); // ��ҽ�ƿ����
					parm.setData("GREEN_USE", cp.getDouble("GREEN_USE")); // ����ɫͨ�����
					parm.setData("GREEN_FLG", "Y"); // �ж��Ƿ������ɫͨ�������BIL_OPB_RECP
													// ��PAY_MEDICAL_CARD����ʱ��Ҫ�ж�Ϊ0ʱ�Ĳ���
					parm.setData("GREEN_BALANCE", result.getDouble(
							"GREEN_BALANCE", 0)); // ��ɫͨ��δ�ۿ���
					parm.setData("GREEN_PATH_TOTAL", result.getDouble(
							"GREEN_PATH_TOTAL", 0)); // ��ɫͨ���ܽ��
				}
			}
		}
		setReturnValue(parm);
		closeWindow();
	}

	/**
	 * �������շ�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onFee(TParm cp) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			if (oldAmt - amt >= 0) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�������
//				cp.setData("EKT_USE", amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				cp.setData("EKT_AMT", 0.00); // ҽ�ƿ�������
//				cp.setData("EKT_USE", oldAmt);// ҽ�ƿ��ۿ���
			    cp.setData("SHOW_GREEN_USE",StringTool.round(amt - oldAmt,2));//��ɫͨ���ۿ���
			}
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",0));
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", this.getValue("NEW_AMT")); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}
	}

	/**
	 * �������˷�
	 * 
	 * @param cp
	 *            TParm
	 */
	private void onUnFee(TParm cp) {
		// ҽ�ƿ���ɫͨ������
		if (null != result && result.getCount() > 0) {
			// cp.setData("OLD_AMT",oldAmt+result.getDouble("GREEN_BALANCE",0));//ҽ�ƿ����+ҽ�ƿ���ɫǮ���Ľ��
			// �����˷�
			if (result.getDouble("GREEN_BALANCE", 0) >= result.getDouble(
					"GREEN_PATH_TOTAL", 0)) {
				cp.setData("EKT_AMT", oldAmt - amt); // ҽ�ƿ�����˷ѳ�ֵ
//				cp.setData("EKT_USE", -amt);// ҽ�ƿ��ۿ���
				cp.setData("SHOW_GREEN_USE",0.00);//��ɫͨ���ۿ���
			} else {
				// ���ȣ�ҽ�ƿ���ɫǮ���ۿ����ֵ Ȼ�� ��ɫǮ���ۿ�����ڳ�ֵ�Ľ���Ժ���ȥ��ֵҽ�ƿ�
				double tempFee = result.getDouble("GREEN_BALANCE", 0) - amt;// �鿴��ɫǮ���ۿ���+��Ҫ�˷ѽ���Ƿ���ڳ�ֵ���
				// �����ڳ�ֵ������ۿ���
				if (tempFee > result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt + tempFee
							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��н�����ۿ����Ժ�Ľ��+ҽ�ƿ��н��
//					cp.setData("EKT_USE", tempFee
//							- result.getDouble("GREEN_PATH_TOTAL", 0));// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",StringTool.round(result.getDouble("GREEN_BALANCE", 0)-result.getDouble("GREEN_PATH_TOTAL", 0),2));//��ɫͨ���ۿ���
				} else if (tempFee <= result.getDouble("GREEN_PATH_TOTAL", 0)) {
					cp.setData("EKT_AMT", oldAmt);// ҽ�ƿ��еĽ���
//					cp.setData("EKT_USE", 0.00);// ҽ�ƿ��ۿ���
					cp.setData("SHOW_GREEN_USE",amt);//��ɫͨ���ۿ���
				}
			}
			cp.setData("GREEN_PATH_TOTAL", result.getDouble("GREEN_PATH_TOTAL",
					0));
			cp.setData("FLG", "Y"); // ��ɫͨ������ע��
		} else {
			cp.setData("GREEN_PATH_TOTAL", 0.00);
			cp.setData("EKT_AMT", this.getValue("NEW_AMT")); // û��ҽ�ƿ���ɫǮ��
			cp.setData("FLG", "N"); // ��ɫͨ��������ע��
		}

	}

	public boolean onCheck() {

		cardNo = readCard.getValue("CARD_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// ��ѯ�˴ξ��ﲡ���Ƿ����ҽ�ƿ���ɫͨ��
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		double sumAmt = oldAmt - amt;
		double tempAmt = oldAmt;
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// ��ѯ��ɫͨ���ۿ���ܳ�ֵ���
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// ��ʾ��ɫͨ�����
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", result.getValue("GREEN_BALANCE", 0));
			this.messageBox("�˾��ﲡ������ҽ�ƿ���ɫǮ��");
			sumAmt += result.getDouble("GREEN_BALANCE", 0);// �ۿ�֮��Ľ��
			tempAmt += result.getDouble("GREEN_BALANCE", 0);// δ�ۿ���
		}
		if (null != insFlg && insFlg.equals("Y")) {
			callFunction("UI|INS_LBL|setVisible", true);// ��ʾҽ���ۿ���
			callFunction("UI|INS_AMT|setVisible", true);
			setValue("INS_AMT", insAmt);
		}
		// caseNo
		setValue("OLD_AMT", tempAmt);
		setValue("CARD_NO", cardNo);
		setValue("NEW_AMT", sumAmt);
		return true;// EKTIO.getInstance().createCard(cardNo,mrNo,oldAmt);
	}

	public void onK() {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("OP_TYPE", 2);
		setReturnValue(parm);
		closeWindow();
	}

	/**
	 * ȡ����
	 */
	public void onCancel() {
		closeWindow();
	}

//	/**
//	 * ִ������վݲ���
//	 */
//	private void onOpbRect() {
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("MR_NO", mrNo);
//		parm.setData("INV_NO", "");
//		parm.setData("OPT_USER", Operator.getID());
//		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//		parm.setData("OPT_TERM", Operator.getIP());
//		parm.setData("REGION_CODE", Operator.getRegion());
//		parm.setData("TOT_AMT", this.getValueDouble("AMT"));// �ۿ���
//		parm.setData("billFlg", "Y"); // ����: N ������:Y
//
//	}
}
