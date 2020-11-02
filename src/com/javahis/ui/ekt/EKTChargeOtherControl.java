package com.javahis.ui.ekt;

import jdo.ekt.EKTNewIO;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TDialog;
import com.dongyang.util.StringTool;

public class EKTChargeOtherControl extends TControl{
	private double payOther3 = 0;
	private double payOther4 = 0;
	private TParm readCard;//ҽ�ƿ�����
	private String mrNo;
	private String caseNo;
	private String cardNo;
	private String resetTradeNo="";
	private double oldAmt = 0.00;
	private String businessType;//�˴β��������� ODO OPB REG
	private double exeAmt;// �˴β������
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
		if(parm.getValue("RESET_TRADE_NO") != null){
			resetTradeNo = parm.getValue("RESET_TRADE_NO");
		}
		payOther3 = parm.getDouble("PAY_OTHER3");
		payOther4 = parm.getDouble("PAY_OTHER4");
		readCard = parm.getParm("READ_CARD");
		if (readCard.getErrCode() != 0) {
			((TButton) getComponent("tButton_0")).setEnabled(false);
			setValue("Message_Text", readCard.getErrText());
		}
		cardNo = readCard.getValue("CARD_NO");//����
		mrNo = parm.getValue("MR_NO");
		caseNo = parm.getValue("CASE_NO");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		businessType = parm.getValue("BUSINESS_TYPE");// �������ݲ���
		exeAmt = parm.getDouble("EXE_AMT");//�˴β������

		// �����շ�ҽ���ۿ�س�ҽ�ƿ�����ִ��ȡ������
//		callFunction("UI|tButton_1|setEnabled", false);
		
		if ("en".equals(getLanguage())) {
			setValue("PAT_NAME", OpdRxSheetTool.getInstance().getPatEngName(
					mrNo));
		} else {
			setValue("PAT_NAME", readCard.getValue("PAT_NAME"));
		}
		setValue("SEX_CODE", readCard.getValue("SEX_CODE"));
		setValue("MR_NO", mrNo);
		setValue("CARD_NO", cardNo);
		setValue("OLD_AMT",oldAmt);
		setValue("AMT",exeAmt);
		setValue("NEW_AMT",oldAmt - exeAmt);
	}
	
	public void onOK() {
		TParm parm = new TParm();
		if (readCard.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");;
			parm.setData("OP_TYPE", 2);
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
			cp.setData("MR_NO", mrNo);
			cp.setData("CASE_NO", caseNo);
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));
			cp.setData("IDNO", readCard.getValue("IDNO"));
			if (businessType == null || businessType.length() == 0)
				businessType = "none";
			cp.setData("BUSINESS_TYPE", businessType);
			cp.setData("OLD_AMT", oldAmt);// ҽ�ƿ����
			cp.setData("NEW_AMT", this.getValue("NEW_AMT"));//ҽ�ƿ�ʣ���� 
			cp.setData("EXE_AMT", exeAmt);// ҽ�ƿ��ۿ���
			cp.setData("SEQ", readCard.getValue("SEQ"));// ���
			cp.setData("PAY_OTHER3", payOther3);
			cp.setData("PAY_OTHER4", payOther4);
			cp.setData("RESET_TRADE_NO", resetTradeNo);
			cp.setData("OPT_USER", Operator.getID());
			cp.setData("OPT_TERM", Operator.getIP());
			TParm r = EKTNewIO.getInstance().onNewSaveOtherFee(cp);
			if(r.getErrCode() < 0){
				parm.setErr(-1, r.getErrText());
			}else{
				parm.setData("OP_TYPE", 1);
				parm.setData("TRADE_NO", r.getValue("TRADE_NO"));
				parm.setData("ektSql", r.getData("sql"));
			}
			
		}		
		setReturnValue(parm);
		closeWindow();
	}
	/**
	 * ȡ����
	 */
	public void onCancel() {
		closeWindow();
	}
	


}
