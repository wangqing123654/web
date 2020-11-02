package com.javahis.ui.opd;

import jdo.ekt.EKTGreenPathTool;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTNewTool;
import jdo.odo.OpdRxSheetTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
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
 * @author pangb 2013.03.27
 * @version 4.0
 */
public class OPDOrderPreviewAmtControl extends TControl {
	private String caseNo;
	private double amt;//��ʾ���
	private TParm readCard;//ҽ�ƿ�����
	private TParm result;// ҽ�ƿ���ɫͨ�����
	//private TParm newParm;//����ҽ��վҽ������Ҫ������ҽ��(��ɾ�����ݼ���)
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	private void initPage(){
		TParm parm = (TParm) getParameter();
		int type =parm.getInt("EKT_TYPE_FLG");
		switch (type){//type:1 .��ѯ������ 2.�����޸�ҽ�������Ǵ˴ν���ҽ�ƿ���� ,3.�޸�ҽ�������˻ش���ǩ�еĽ��
		case 1:
			this.setValue("EKT_AMT", parm.getDouble("CURRENT_BALANCE"));//ҽ�ƿ����
			String sql = "SELECT SUM(AR_AMT) AS AR_AMT FROM OPD_ORDER WHERE CASE_NO='"+ parm.getValue("CASE_NO")+ "' AND RELEASE_FLG<>'Y'";
			TParm sumParm = new TParm(TJDODBTool.getInstance().select(sql)); //�ܽ�δ�շѺ����շѣ�
			this.setValue("FEE_Y", sumParm.getDouble("AR_AMT",0));//Ӧ�ս��
			sql = "SELECT SUM(AR_AMT) AS AR_AMT FROM OPD_ORDER WHERE CASE_NO='"+ parm.getValue("CASE_NO")+ "' AND RELEASE_FLG<>'Y' AND BILL_FLG='Y' ";
			TParm billParm = new TParm(TJDODBTool.getInstance().select(sql)); // ���շ�
			this.setValue("BALANCE_AMT", parm.getDouble("CURRENT_BALANCE")-(sumParm.getDouble("AR_AMT",0)-billParm.getDouble("AR_AMT",0)));//���
			break;
		case 2:
			onInitParm(parm);
			break;
		}
		
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
		readCard = parm.getParm("READ_CARD");
		if (readCard.getErrCode() != 0) {
			return ;
		}
		caseNo = parm.getValue("CASE_NO");//�����
		if (null == caseNo) {
			return;
		}
		amt = parm.getDouble("AMT");//��ʾ�Ľ��
		//sumOpdorderAmt = parm.getDouble("SUMOPDORDER_AMT");
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
		// setValue("OLD_AMT",oldAmt);
		setValue("FEE_Y", amt);//��ʾ���
		this.setValue("EKT_AMT",readCard.getDouble("CURRENT_BALANCE"));//ҽ�ƿ����
		this.setValue("BALANCE_AMT",readCard.getDouble("CURRENT_BALANCE")-amt);//���
		// setValue("NEW_AMT",oldAmt - amt);
	}
	public boolean onCheck() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// ��ѯ�˴ξ��ﲡ���Ƿ����ҽ�ƿ���ɫͨ��
		TParm patEktParm = EKTGreenPathTool.getInstance().selPatEktGreen(parm);
		if (patEktParm.getInt("COUNT", 0) > 0) {
			// ��ѯ��ɫͨ���ۿ���ܳ�ֵ���
			result = PatAdmTool.getInstance().selEKTByMrNo(parm);
			callFunction("UI|EKT_GREEN_LBL|setVisible", true);// ��ʾ��ɫͨ�����
			callFunction("UI|GREEN_BALANCE|setVisible", true);
			this.setValue("GREEN_BALANCE", result.getValue("GREEN_BALANCE", 0));
			this.messageBox("�˾��ﲡ������ҽ�ƿ���ɫǮ��");
		}
		return true;// EKTIO.getInstance().createCard(cardNo,mrNo,oldAmt);
	}
}
