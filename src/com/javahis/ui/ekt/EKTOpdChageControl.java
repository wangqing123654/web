package com.javahis.ui.ekt;

import jdo.ekt.EKTNewIO;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;

/**
 * <p>
 * Title: ҽ��վҽ�ƿ������˻�ҽ�ƿ�������
 * </p>
 * 
 * <p>
 * Description:ҽ���޸�ҽ����ִ���շѲ��� ҽ�ƿ���,�������ִ�н��˴���ǩ����Ϊû���շ�״̬���˻�ҽ�ƿ��н��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-3-7
 * @version 4.1
 */
public class EKTOpdChageControl extends TControl {
	private String caseNo;
	private String mrNo;
	private double oldAmt = 0.00;
	private TParm readCard;
	private TParm returnParm = new TParm();
//	private String ektTradeType;//��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
	String cardNo = "";

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// ȥ���˵���
		TDialog F=(TDialog)this.getComponent("UI");
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
		//System.out.println("����OPD====="+parm);
		if (parm == null)
			return;
		String insFlg=parm.getValue("INS_FLG");
		readCard = parm.getParm("READ_CARD");
		cardNo = readCard.getValue("CARD_NO");
		mrNo = parm.getValue("MR_NO");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		if (null!=insFlg && insFlg.equals("Y")) {
		}else{
			this.messageBox("����,�˻�ҽ�ƿ����");
			opdExe(parm);
		}
		setValue("READ_CARD_NO", cardNo);
		// ZHANGP 20120106
//		setValue("CARD_NO",cardNo);
		setValue("READ_NAME1", readCard.getValue("NAME"));
		setValue("READ_SEX1", readCard.getValue("SEX"));
		if ("en".equals(getLanguage())) {
			setValue("PAT_NAME", OpdRxSheetTool.getInstance().getPatEngName(
					mrNo));
		} else {
			setValue("PAT_NAME", readCard.getValue("PAT_NAME"));
		}
		setValue("SEX_CODE", readCard.getValue("SEX_CODE"));
		setValue("MR_NO", mrNo);
		setValue("CARD_NO", cardNo);
	}

	public void onConcle() {
		//System.out.println("sdfsdfsdfreturnParm:::::"+returnParm);
		setReturnValue(returnParm);
		closeWindow();
	}
	/**
	 * ����ҽ��վ������˶���
	 * @param parm
	 */
	private void opdExe(TParm parm){
		double greeBalance = parm.getDouble("GREEN_BALANCE");// ��ɫͨ��ʣ����
		double greenPathTotal = parm.getDouble("GREEN_PATH_TOTAL");// ��ɫͨ���������
		TParm unParm = parm.getParm("unParm");// ��Ҫ�˻���ҽ��
		double amt = 0.00;
		StringBuffer trade_no = new StringBuffer();
		caseNo = parm.getValue("CASE_NO");
		if (null == caseNo) {
			return;
		}
		for (int i = 0; i < unParm.getCount(); i++) {
			// ���ҽ���շ��ڲ����׺���
			if (!trade_no.toString().contains(unParm.getValue("BUSINESS_NO", i))) {
				trade_no.append("'" + unParm.getValue("BUSINESS_NO", i) + "',");
			}
		}

		String tradeNo = "''";
		if (trade_no.toString().length() > 0) {
			tradeNo = trade_no.substring(0, trade_no.lastIndexOf(","));
		}
		// ��ѯ��Ҫ�˻��Ľ��
		// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ��� ��ѯ
		String sql = "SELECT CASE_NO,RX_NO,SEQ_NO,MR_NO,AR_AMT AS AMT ,'N' BILL_FLG,'C' BILL_TYPE FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo+ "' AND BILL_FLG='Y' AND BILL_TYPE='E'"
				+ " AND BUSINESS_NO IN ("
				+ tradeNo + ") ";
		returnParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (returnParm.getErrCode() < 0) {
			returnParm.setErr(-1, returnParm.getErrText());
			return;
		}
		//�˷�ʱ�����޸ĵĽ���Ҫ,���� δ�޸�֮ǰ��ҽ�����
		boolean flg=false;
		for (int i = 0; i < returnParm.getCount(); i++) {
			flg=false;
			for (int j = 0; j < unParm.getCount(); j++) {
				if (unParm.getValue("RX_NO", j).equals(returnParm.getValue("RX_NO",i))
						&& unParm.getValue("SEQ_NO", j).equals(returnParm.getValue("SEQ_NO",i))) {
					returnParm.setData("AMT",i,unParm.getDouble("AR_AMT", j));
					amt += unParm.getDouble("AR_AMT", j);// ���δ�޸�֮ǰ�Ľ��
					flg=true;
					break;
				}
			}
			if(!flg)
			amt += returnParm.getDouble("AMT", i);// ��ý��
		}
		setValue("AMT", amt);
		setValue("OLD_AMT", oldAmt + greeBalance);
		//ҽ�ƿ��еĽ��=ҽ�ƿ�ԭ���Ľ��+�Ѿ��ۿ��ҽ�ƿ����+��ɫͨ��ʹ�ý��+��ɫͨ��ʣ��-ҽ���ܽ��
		setValue("NEW_AMT",oldAmt+ greeBalance+amt);
		TParm cp = new TParm();
		//�����Ѿ�ʹ��������
		if(greeBalance>0 && greenPathTotal>greeBalance){			
			//�������ܽ��>�˴λ��˽��+������ۿ��� ��ҽ�ƿ��н��=0
			if(greeBalance+amt<greenPathTotal){
				cp.setData("SHOW_GREEN_USE",-amt);//�˴�������ۿ���
				cp.setData("EKT_AMT", 0); // ҽ�ƿ���ǰ���
			}else{
//				//�����������ܽ��Ĳ��ֻس嵽ҽ�ƿ���
//				cp.setData("AMT",orderSumParm.getDouble("TOT_AMT",0));//ҽ�ƿ��ۿ���
//				cp.setData("GREEN_USE",0);//�˴�������ۿ���
				cp.setData("SHOW_GREEN_USE",greeBalance-greenPathTotal);//�˴�������ۿ���
				cp.setData("EKT_AMT", greeBalance+amt-greenPathTotal); // ҽ�ƿ���ǰ���
			}
			cp.setData("GREEN_BALANCE",greeBalance);//��������
			cp.setData("FLG","Y");//�����ǰ����޸� REG_PATADM ���� �����ǰʣ����
		}else{
			cp.setData("EKT_AMT",this.getValueDouble("NEW_AMT")); // ҽ�ƿ���ǰ���
			cp.setData("GREEN_BALANCE",0.00);//��������
			cp.setData("SHOW_GREEN_USE",0.00);//�˴�������ۿ���
		}
		cp.setData("MR_NO", parm.getValue("MR_NO"));//������
		cp.setData("CASE_NO", caseNo);//�����
		//cp.setData("BUSINESS_NO", parm.getValue("BUSINESS_NO"));
		cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));//��������
		cp.setData("OLD_AMT", oldAmt);//ҽ�ƿ�ԭ�����
		cp.setData("BUSINESS_TYPE", parm.getValue("BUSINESS_TYPE"));//�շ�����
		//��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
		cp.setData("EKT_TRADE_TYPE", parm.getValue("EKT_TRADE_TYPE"));//��ѯ����
		cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));//����
		cp.setData("SEQ", readCard.getValue("SEQ"));//���
		cp.setData("IDNO", readCard.getValue("IDNO"));//���
		cp.setData("GREEN_PATH_TOTAL",greenPathTotal);//��ɫͨ���������
		cp.setData("OPT_USER", Operator.getID());//id
		cp.setData("OPT_TERM", Operator.getIP());//IP
		cp.setData("TRADE_SUM_NO", tradeNo);//�ڲ����׺���
		TParm returnParm  = new TParm(EKTNewIO.getInstance().onNewSaveFee(cp.getData()));
		if (greenPathTotal > 0) {
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// ��ʾ��ɫͨ�����
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", greeBalance);
		}
	}
}
