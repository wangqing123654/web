package com.javahis.ui.odo;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewIO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.odo.OpdOrder;
import jdo.opb.OPBTool;
import jdo.opd.OrderTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վҽ�ƿ��շѷ�ʽʵ����
 * </p>
 * 
 * <p>
 * Description: ����ҽ������վҽ�ƿ��շѷ�ʽʵ����
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class EKTpay implements IPayType {
	
	private OdoMainControl odoMainControl;
	private ODOMainEkt odoMainEkt;
	private ODOMainOpdOrder odoMainOpdOrder;
	
	private String tredeNo;
	
	private static final String NULLSTR = "";
	private static final String URLEKTINFOUI = "%ROOT%\\config\\ekt\\EKTInfoUI.x";
	private static final String URLOPDREGCHOOSE = "%ROOT%\\config\\opd\\OPDRegChoose.x";
	
	public static final String PAY_TYPE	= "E";
	
	private void onInit(OdoMainControl odoMainControl) throws Exception{
		this.odoMainControl = odoMainControl;
		this.odoMainEkt = odoMainControl.odoMainEkt;
		this.odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
	}
	
	/**
	 * ����
	 * @return
	 */
	private boolean readEKT() {
		odoMainEkt.ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (odoMainEkt.ektReadParm.getErrCode() < 0) {
			odoMainControl.messageBox("ҽ�ƿ���������");
			return false;
		}
		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
			odoMainControl.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
			odoMainEkt.ektReadParm = null;
			return false;
		}
		if (null == odoMainEkt.ektReadParm) {
			odoMainControl.messageBox("δȷ����ݣ����ҽ�ƿ�");
			return false;
		}
		return true;
	}
	
	/**
	 * ��������
	 */
	@Override
	public TParm readCard() {
		return EKTIO.getInstance().TXreadEKT();
	}
	
	/**
	 * �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
	 * ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
	 */
	private boolean updateOrderParm(TParm orderParm ,TParm orderOldParm,TParm unParm){
		boolean unFlg = false;
		int unCount = 0;
		// System.out.println("orderParm��������������" + orderParm);
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			if (orderParm.getValue("CAT1_TYPE",i).equals("RIS")
					|| orderParm.getValue("CAT1_TYPE",i).equals("LIS")
					|| orderParm.getValue("ORDER_CODE",i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
				if (orderParm.getValue("RX_NO",i).equals(
						orderOldParm.getValue("RX_NO", j))
						&& orderParm.getValue("SEQ_NO",i).equals(
								orderOldParm.getValue("SEQ_NO", j))) {
					if (orderParm.getDouble("AMT",i) == orderOldParm
							.getDouble("AR_AMT", j)) {
						break;
					}
					unParm.setRowData(unCount, orderOldParm, j);// ���ִ���޸ĵ�ҽ��
					unCount++;
					unFlg = true;// �ж��Ƿ����ҽ��
					break;
				}
			}
		}
		unParm.setCount(unCount);
		return unFlg;
	}
	
	/**
	 * У��ҽ�ƿ� ɾ��ҽ�� ��ӻس� OPD_ORDER ����
	 * 
	 * @param parm
	 */
	private void ektDeleteChackOut(TParm parm) {
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < OPBTool.orderName.length; j++) {
				if (null == parm.getValue(OPBTool.orderName[j], i)
						|| parm.getValue(OPBTool.orderName[j], i).length() <= 0) {
					parm.setData(OPBTool.orderName[j], i, NULLSTR);
				}
			}
		}
	}
	
	/**
	 * У���Ƿ����ɾ��ҽ�� ҽ�ƿ�ɾ��ҽ������ֱ��ɾ��,����Ѿ��ۿ��ҽ��ɾ�� ��ֱ��ִ�пۿ����
	 * 
	 * @param order
	 * @param row
	 * boolean flg true ɾ��һ��ҽ�� 
	 * @return
	 * =======pangben 2013-1-29 ��Ӳ��� У�������ҽ���Ƿ��Ѿ��Ǽ�
	 * @throws Exception 
	 */
	@Override
	public boolean deleteOrder(OpdOrder order, int row, String message,String medAppMessage) throws Exception {
		// ҽ�ƿ���������ɾ��ҽ��====pangben 2011-12-16
		if (null == odoMainEkt.ektReadParm || null == odoMainEkt.ektReadParm.getValue("MR_NO")
				|| odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
			if (!order.isRemovable(row, false)) {
				odoMainControl.messageBox("�ѼƷ�,��ִ���˷Ѳ���"); // �ѼƷ�ҽ������ɾ��
				return false;
			} 
			return true;
		} else {
			// ���շ�ҽ��û������ϲ���ɾ��
			//modify by huangtt 20141126
			int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
			if (rowMainDiag < 0) {
				odoMainControl.messageBox("�뿪�������");
				return false;
			}
			
			
			if (!ektDelete(order, row)) {// У���Ƿ����ɾ��ҽ��
				odoMainControl.messageBox(message); // �ѼƷ�ҽ������ɾ��
				return false;
			}
			//=========pangben 2013-1-29
			if(!odoMainControl.odoMainOpdOrder.odoRxExa.medAppyCheckDate(order, row)){
				odoMainControl.messageBox(medAppMessage); // У�� �������Ѿ��Ǽǵ����ݲ���ɾ������
				return false;
			}
			boolean flg = false;
			if(odoMainEkt.preDebtFlg){
				flg = true;
			}else{
				flg = readEKT();
			}
			return flg;
		}
	}
	
	/**
	 * У��ҽ�ƿ�ɾ������ rxFlg false : ɾ�����Ŵ���ǩ����ʹ�� true :ɾ����������qi
	 */
	private boolean ektDelete(OpdOrder order, int row) {
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		if (!order.isRemovable(row, false)) {// FALSE : �Ѿ��շ� Ҫִ�� onFee() ����
			// TRUE : δ�շ� ��ִ��onFee() ����
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}
	
	/**
	 * �޸�ҽ������ʱ�������������ݴ����ִ��ֱ���շѲ���
	 * @throws Exception 
	 */
	@Override
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) throws Exception {
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
		} else {
			// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
			// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
			updateOrderUnConcle(ektOrderParmone, ektSumExeParm);	
		}
		// ɾ��\�޸�ҽ���շ�
		if (!onEktSave(ektOrderParmone, ektSumExeParm)) {
		}
	}
	
	/**
	 * �޸��շ�ҽ������ִ�пۿ���泷������
	 * @return
	 */
	@Override
	public boolean updateOrderUnConcle(TParm ektOrderParmone,TParm ektOrderParm) throws Exception{
		//����ݴ水ť ������շѵ�ҽ���޸Ľ�� ִ�� �޸�ҽ���ۿ���˿����
		boolean unFlg = false;
		TParm updateParm=ektOrderParm.getParm("updateParm");
		for (int i = 0; i < updateParm.getCount("ORDER_CODE"); i++) {
			if (updateParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| updateParm.getValue("CAT1_TYPE", i).equals(
							"LIS")
					|| updateParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < ektOrderParmone.getCount("ORDER_CODE"); j++) {//====pangb 2013-2-28 �޸�ҽ�������ݴ�������
				if (updateParm.getValue("RX_NO", i).equals(
						ektOrderParmone.getValue("RX_NO", j))
						&& updateParm.getValue("SEQ_NO", i).equals(
								ektOrderParmone.getValue("SEQ_NO", j))) {
					if (updateParm.getDouble("AMT", i) != ektOrderParmone
							.getDouble("AR_AMT", j)) {
						unFlg = true;// �ж��Ƿ��޸�ҽ��
						break;
					}
				}
			}
			if (unFlg) {
				ektOrderParmone.setData("OPBEKTFEE_FLG", "Y");
				//ektOnFee = true;
				break;
			}
		}
		return unFlg;
	}
	
	/**
	 * ҽ�ƿ���������ɾ����ҽ�� =====pangben 2012-01-06
	 */
	private void concelDeleteOrder(TParm orderParm, TParm oldOrderParm,
			boolean exeDelOrder) {
		TParm tempParm = new TParm();
		int count = 0;
		// if (!exeDelOrder) {// ҽ�����ݲ�ͬ ����ȫ��ɾ����orderParmҽ�� û����ô������
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < oldOrderParm.getCount("ORDER_CODE"); j++) {
				if (oldOrderParm.getValue("SEQ_NO", j).equals(
						orderParm.getValue("SEQ_NO", i))
						&& oldOrderParm.getValue("RX_NO", j).equals(
								orderParm.getValue("RX_NO", i))
						&& !orderParm.getBoolean("BILL_FLG", i)) {
					tempParm.setRowData(count, oldOrderParm, j);
					count++;
				}
			}
		}
		orderParm = tempParm;
		if (orderParm.getCount("ORDER_CODE") > 0) {
			// �ж��Ƿ�����޸ĵ�ҽ����Ϣ
			for (int i = orderParm.getCount("ORDER_CODE") - 1; i >= 0; i--) {
				if (!orderParm.getBoolean("BILL_FLG", i)) {
					orderParm.setData("BILL_FLG", i, "Y");
					orderParm.setData("BILL_DATE", i, SystemTool.getInstance()
							.getDate());
					orderParm.setData("BILL_USER", i, Operator.getID());
				}
			}
			ektDeleteChackOut(orderParm);
			// System.out.println("concleDeleteorder�������Parm:::::"+orderParm);
			orderParm.setData("MED_FLG", "Y");// ҽ�ƿ�����������ִ�����MED_APPLY ������
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
					"concleDeleteOrder", orderParm);
			if (result.getErrCode() < 0) {
				System.out.println("ҽ�ƿ�����ҽ������ʧ��");
			}
		} else {
			System.out.println("û��Ҫִ�е�ҽ������");
		}
	}
	
	/**
	 * ҽ�ƿ�����
	 * 
	 * @param FLG
	 *            String ִ�пۿ�
	 * @param FLG
	 *            double �ܷ���
	 * @return boolean ====================pangben 20110915 flg ��false û�����ִ�е�ҽ��
	 *         true ����ӻ�ɾ����ҽ�� boolean flg ҽ�������޸�ҽ��ʹ�� true ɾ��ҽ���շѲ��� false ����޸�ҽ��
	 *         �շ�
	 * @throws Exception 
	 */
	private boolean onEktSave(TParm orderParm, TParm ektSumExeParm) throws Exception {
		int type = 0;
		// ��ִ���ݴ�ҽ���� ���ҽ�ƿ�����ʷ��¼�жϴ˴β���ҽ���Ƿ��޸�ҽ��
		TParm parm = new TParm();
		// TParm detailParm = null;
		// ���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
		if (EKTIO.getInstance().ektSwitch()) { // ҽ�ƿ����أ���¼�ں�̨config�ļ���
			parm = onOpenCard(orderParm, ektSumExeParm);
			if (parm == null) {
				odoMainControl.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			if (type == 3) {
				odoMainControl.messageBox("E0115");
				return false;
			}
			if (type == -1) {
				odoMainControl.messageBox("��������!");
				return false;
			}
			if (type == 5) {
				return false;
			}
			odoMainControl.odo.setTredeNo(parm.getValue("TRADE_NO"));
			tredeNo = parm.getValue("TRADE_NO");
			// System.out.println("ҽ�ƿ��������" + parm);
			if (parm.getErrCode() < 0) {
				odoMainControl.messageBox("E0005");
				return false;
			}
			if (Operator.getSpcFlg().equals("Y")
					&& ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
//				odoMainControl.odoMainSpc.saveSpcOpdOrder(ektSumExeParm.getValue("PHA_RX_NO"));//����������ҽ��
				odoMainControl.odoMainSpc.onSendInw(ektSumExeParm,true);//==pangben 2013-12-18
			}
			if (null != parm.getValue("OPD_UN_FLG")
					&& parm.getValue("OPD_UN_FLG").equals("Y")) {
				TParm tempParm = new TParm();
				tempParm.setData("CASE_NO", odoMainControl.caseNo);
				// parm.setData("MR_NO", getValue("MR_NO"));
				TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(
						tempParm);
				odoMainOpdOrder.wc = ODOMainOpdOrder.W; // Ĭ��Ϊ��ҽ
				odoMainControl.odoMainReg.initOpd(reg, 0);
			} else {
				// ����HL7
				odoMainControl.odoMainOther.sendHL7Mes();
			}
			double reduceAmt = 0.00;
			String re = EKTIO.getInstance().check(tredeNo, odoMainControl.odoMainReg.reg.caseNo(),reduceAmt);
			if (re != null && re.length() > 0) {
				odoMainControl.messageBox_(re);
				odoMainControl.messageBox_("����������Ϣ������ϵ");
				// deleteLisPosc = false;
				odoMainOpdOrder.onExeFee();
				return false;
			}
		} else {
			odoMainControl.messageBox_("ҽ�ƿ��ӿ�δ����");
			// deleteLisPosc = false;
			return false;
		}
		// �շѳɹ�����ˢ�µ�ǰ����
		// onClear();
		//ektDeleteOrder = false;// ɾ������ִ��
		//isFee = false;// ִ���շ��Ժ󲻿����ٴ�ִ���շ�
		//deleteLisPosc = false;
		odoMainOpdOrder.onExeFee();
		return true;
	}

	@Override
	public boolean onSave(TParm orderParm, TParm sumExeParm) throws Exception {
		// TODO Auto-generated method stub
		return onEktSave(orderParm, sumExeParm);
	}
	
	/**
	 * ��ҽ�ƿ� =====pangben 2011-12-16 flg ��false û�����ִ�е�ҽ�� true ����ӻ�ɾ����ҽ��
	 * orderParm ��Ҫ������ҽ�� ��ɾ�� ����ӡ� �޸�
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm orderOldParm, TParm orderParm) {
		if (odoMainControl.odo == null) {
			return null;
		}
		TParm unParm = new TParm();
		if (orderOldParm == null) {
			odoMainControl.messageBox("û����Ҫ������ҽ��");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		if(orderParm.getValue("OP_FLG").length()>0 && orderParm.getInt("OP_FLG")==5){
			odoMainControl.messageBox("û����Ҫ������ҽ��");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		// ׼������ҽ�ƿ��ӿڵ�����
		// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
		// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
		TParm updateParm=orderParm.getParm("updateParm");
		boolean unFlg = updateOrderParm(updateParm, orderOldParm, unParm);
		TParm parm = new TParm();
		boolean isDelOrder = false;// ִ��ɾ��ҽ��
		//boolean exeDelOrder = false;// ִ��ɾ��ҽ��
		String delFlg=orderParm.getValue("DEL_FLG");
		// �����������ҽ��ɾ��Ҳ�����IS_NEW = false ״̬ ������Ҫ��ִ�з���ʱ�Ȳ�ѯ��ǰ����ҽ��
		// У���Ƿ���ɾ��������ӿ�
		if(delFlg.equals("Y")){
			isDelOrder = true;
		}
		orderParm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("CASE_NO",odoMainControl.odoMainReg.reg.caseNo());
		orderParm.setData("REGION_CODE", Operator.getRegion());
		orderParm.setData("MR_NO", odoMainControl.odoMainPat.pat.getMrNo());
		orderParm.setData("NAME", odoMainControl.odoMainPat.pat.getName());
		orderParm.setData("IDNO", odoMainControl.odoMainPat.pat.getIdNo());
		orderParm.setData("SEX", odoMainControl.odoMainPat.pat.getSexCode() != null
				&& odoMainControl.odoMainPat.pat.getSexCode().equals("1") ? "��" : "Ů");
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		orderParm.setData("INS_FLG", "N");// ҽ��������
		orderParm.setData("UN_FLG", unFlg ? "Y" : "N");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		orderParm.setData("unParm", unParm.getData());// ���ִ���޸ĵ�ҽ��
		if (null != orderOldParm.getValue("OPBEKTFEE_FLG")
				&& orderOldParm.getValue("OPBEKTFEE_FLG").equals("Y")) {
			orderParm.setData("OPBEKTFEE_FLG", "Y");
		}
		//ֱ���շѲ���������޸ĵ��շ�ҽ�� ����ִ��ȡ������
		if(null == orderOldParm.getValue("OPBEKTFEE_FLG")
				|| orderOldParm.getValue("OPBEKTFEE_FLG").length()<=0){
			if(unFlg)
				orderParm.setData("OPBEKTFEE_FLG", "Y");
		}

		odoMainEkt.ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getErrCode() < 0
				|| null == odoMainEkt.ektReadParm.getValue("MR_NO")) {
			parm.setData("OP_TYPE", 5);
			odoMainControl.messageBox("ҽ�ƿ���������");
			odoMainControl.setValue("LBL_EKT_MESSAGE", "δ����");//====pangben 2013-5-3��Ӷ���
			odoMainEkt.ekt_lable.setForeground(OdoMainControl.RED);//======yanjing 2013-06-14���ö�����ɫ
			return parm;
		}else{
			odoMainControl.setValue("LBL_EKT_MESSAGE", "�Ѷ���");//====pangben 2013-5-3��Ӷ���
			odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14���ö�����ɫ
		}
		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
			odoMainControl.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
			odoMainEkt.ektReadParm = null;
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		int type=0;
		//parm.setData("BILL_FLG", "Y");
		orderParm.setData("ektParm", odoMainEkt.ektReadParm.getData()); // ҽ�ƿ�����
		try {
			parm = EKTNewIO.getInstance().onOPDAccntClient(orderParm, odoMainControl.odo.getCaseNo(),
					odoMainControl);
		} catch (Exception e) {
			System.out.println("ҽ��վ�շѳ�������::::"+e.getMessage());
		}finally{
			type = parm.getInt("OP_TYPE");
			TParm delExeParm=orderParm.getParm("delExeParm");//�����շ�ɾ����ҽ��
			if (parm == null || type == 3 || type == -1 || type == 5) {
				if(delExeParm.getCount("ORDER_CODE")>0){
					concelDeleteOrder(delExeParm, orderOldParm, isDelOrder);// ɾ��ҽ��ѡ��ȡ������
					odoMainEkt.ektExeConcel=true;//ɾ����������
				}
			}
		}
		if (type == 6) {
			odoMainControl.messageBox("û����Ҫ������ҽ��");
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		if(null!=parm.getValue("OPD_UN_FLG") && parm.getValue("OPD_UN_FLG").equals("Y")){//�޸�ҽ������,���� �����շѽ��׺ŵ�����ҽ���˻���ȥ�����δ�շ�״̬
			orderParm.setData("newParm",parm.getParm("unParm").getData());
		}
		// �õ��շ���Ŀ
		odoMainControl.odoMainOther.sendHL7Parm = orderParm.getParm("hl7Parm");
		//hl7Temp(checkParm);
		// ɾ�����ݲ���ʱ��ʹ��ֻ���޸�ҽ������ʱʹ��
		parm.setData("orderParm", orderParm.getData());// ��Ҫ������ҽ��
		return parm;
	}

	/**
	 * 
	 */
	@Override
	public void onReadCard(OdoMainControl odoMainControl) throws Exception {
		// TODO Auto-generated method stub
		onInit(odoMainControl);
		onReadCard();
	}
	
	/**
	 * ҽ�ƿ���������
	 * @throws Exception
	 */
	private void onReadCard() throws Exception{
		odoMainEkt.isReadEKT = true;
		odoMainEkt.ektReadParm = readCard();
		if (odoMainEkt.ektReadParm.getErrCode() < 0) {
			odoMainControl.messageBox("ҽ�ƿ���������");
			return;
		}
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		boolean isMrNoNull = StringUtil
				.isNullString((String) odoMainControl.getValue("MR_NO"));
		if (null == odoMainControl.caseNo || isMrNoNull) {
//			odoMainControl.messageBox("��ѡ��һ������");
			return;
		}		
//		if (!odoMainEkt.ektReadParm.getValue("MR_NO").equals(odoMainControl.getValue("MR_NO"))) {
//			odoMainControl.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
//					+ odoMainEkt.ektReadParm.getValue("PAT_NAME"));
//			// �������в��˵�ʱ�� ��ҽ�ƿ�ֻ�������Ա� ��Ƭ�Ƿ����ڸò���
//			odoMainEkt.ektReadParm.setData("SEX",
//					odoMainEkt.ektReadParm.getValue("SEX_CODE").equals("1") ? "��" : "Ů");
//			odoMainControl.openDialog(URLEKTINFOUI, odoMainEkt.ektReadParm);
//			odoMainEkt.ektReadParm = null;
//			return;
//		}
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", odoMainControl.caseNo);
		// parm.setData("MR_NO", getValue("MR_NO"));
		TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
		if (reg.getCount("CASE_NO") > 1) {
			TParm re = (TParm) odoMainControl.openDialog(
					URLOPDREGCHOOSE, reg);
			if (re == null)
				return;
			TParm result = new TParm();
			result.setRowData(0, re);
			odoMainOpdOrder.wc = ODOMainOpdOrder.W; // Ĭ��Ϊ��ҽ
			// ============xueyf modify 20120227 start
			if (isMrNoNull) {
				odoMainControl.odoMainReg.initOpd(result, 0);
			}
		} else if (reg.getCount("CASE_NO") == 1 && isMrNoNull) {
					odoMainOpdOrder.wc = ODOMainOpdOrder.W; // Ĭ��Ϊ��ҽ
			odoMainControl.odoMainReg.initOpd(reg, 0);
			// ============xueyf modify 20120227 stop
		}
		odoMainControl.setValue("LBL_EKT_MESSAGE", "�Ѷ���");//====pangben 2013-3-19 ��Ӷ���
		odoMainEkt.ekt_lable.setForeground(OdoMainControl.GREEN);//======yanjing 2013-06-14���ö�����ɫ
	}
	
	/**
	 * �ݴ�
	 * @param ektOrderParm
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	@Override
	//˹�ʹ�
	public void onTempSave(TParm ektOrderParm, TParm ektOrderParmone, TParm ektSumExeParm) throws Exception{
		if (null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0) {
		} else {
			// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
			// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
			if (null == ektOrderParm) {
				//����ݴ水ť ������շѵ�ҽ���޸Ľ�� ִ�� �޸�ҽ���ۿ���˿����
				if(updateOrderUnConcle(ektOrderParmone, ektSumExeParm))
					ektOrderParm=ektOrderParmone;
			}
		}
		// ɾ��\�޸�ҽ���շ�
		if (null!=ektOrderParm){
			//˹�ʹ�
			//====zhangp 20131202
			if(odoMainEkt.preDebtFlg && !(null == odoMainEkt.ektReadParm || odoMainEkt.ektReadParm.getValue("MR_NO").length() <= 0)){
				
			}else{
				onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 �޸Ľ���Ҳ��ӡ����ǩ
			}
			onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 �޸Ľ���Ҳ��ӡ����ǩ
		}
	}

	@Override
	public void onSave() throws Exception {
		// TODO Auto-generated method stub
			odoMainEkt.ektOrderParmone = new TParm();
			odoMainEkt.ektSumExeParm = new TParm();
			// parm.setData("REGION_CODE", Operator.getRegion());
			odoMainEkt.ektOrderParmone.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
			// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
			odoMainEkt.ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(
					odoMainEkt.ektOrderParmone);
			if (odoMainEkt.ektOrderParmone.getErrCode() < 0) {
				return;
			}
			// ��ô˴�ҽ�ƿ�����������Ҫִ�е�ҽ��=====pangben 2012-4-14
			odoMainEkt.ektSumExeParm = odoMainControl.odo.getOpdOrder().getEktParam(odoMainEkt.ektOrderParmone);
	}
}
