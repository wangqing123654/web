package com.javahis.ui.opd;

import jdo.ekt.EKTNewIO;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;

/**
 * 
 * <p>
 * Title:�������ѯ
 * </p>
 * 
 * <p>
 * Description:�ż���ҽ��վ��ѯ���ﲡ���������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author pangb 2013.04.27
 * @version 4.0
 */
public class OPDOrderPreviewReAmtControl extends TControl {
	private double oldAmt = 0.00;
	private TParm returnParm = new TParm();
	private TParm readCard;//ҽ�ƿ�����
	private String caseNo;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TDialog F=(TDialog)this.getComponent("UI");
        F.setUndecorated(true);
		initPage();
	}
	private void initPage(){
		TParm parm = (TParm) getParameter();
		int type =parm.getInt("EKT_TYPE_FLG");
		switch (type){//type:1 .��ѯ������ 2.�����޸�ҽ�������Ǵ˴ν���ҽ�ƿ���� ,3.�޸�ҽ�������˻ش���ǩ�еĽ��
		case 3:
			onInitOpdParm(parm);
			break;
		}
	}
	/**
	 * ��ʼ������
	 * �޸Ĳ������㣬�˻���������ǩ
	 * @param parm
	 *            TParm
	 */
	private void onInitOpdParm(TParm parm) {
		//System.out.println("����OPD====="+parm);
		if (parm == null)
			return;
		String insFlg=parm.getValue("INS_FLG");
		readCard = parm.getParm("READ_CARD");
		oldAmt = readCard.getDouble("CURRENT_BALANCE");
		if (null!=insFlg && insFlg.equals("Y")) {
		}else{
			this.messageBox("����,�˻�ҽ�ƿ����");
			opdExe(parm);
		}
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
		TParm reParm=parm.getParm("RE_PARM");//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬��ӵ�һ��������
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
		setValue("FEE_Y", -amt);//��ʾ���
		setValue("EKT_AMT", oldAmt + greeBalance);//ҽ�ƿ����
		//ҽ�ƿ��еĽ��=ҽ�ƿ�ԭ���Ľ��+�Ѿ��ۿ��ҽ�ƿ����+��ɫͨ��ʹ�ý��+��ɫͨ��ʣ��-ҽ���ܽ��
		setValue("BALANCE_AMT",oldAmt+ greeBalance+amt);
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
			cp.setData("EKT_AMT",oldAmt+ greeBalance+amt); // ҽ�ƿ���ǰ���
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
		reParm.setData("newParm",returnParm.getData());//��Ҫ������ҽ��==pangben 2013-7-17
		cp.setData("RE_PARM",reParm.getData());//==pangben 2013-7-17 �������ݣ����޸�OPD_ORDER�շ�״̬��ӵ�һ��������
		cp.setData("OPB_AMT",parm.getDouble("OPB_AMT"));// ����ҽ��վ�������=====pangben 2013-7-17
		cp.setData("ODO_EKT_FLG",  parm.getValue("ODO_EKT_FLG"));//�����޸�OPD_ORDER�� ===pangben 2013-7-17
		TParm showParm  = new TParm(EKTNewIO.getInstance().onNewSaveFee(cp.getData()));
		if (showParm.getErrCode()<0) {
			returnParm=new TParm();
			returnParm.setErr(-1, returnParm.getErrText());
			this.messageBox("ִ�л���ҽ�����ִ���");
			return;
		}
		if (greenPathTotal > 0) {
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// ��ʾ��ɫͨ�����
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", greeBalance);
		}
	}
}
