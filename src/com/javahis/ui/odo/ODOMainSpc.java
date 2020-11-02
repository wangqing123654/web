package com.javahis.ui.odo;

import jdo.odo.OpdOrder;
import jdo.opd.OrderTool;
import jdo.pha.PhaSysParmTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ����������
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����������
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainSpc {
	public OdoMainControl odoMainControl;
	private SocketLink client1;//Socket��������ҩ������
	private String phaRxNo;//===pangben 2013-5-17 ҩƷ��˽����������ƴ���ǩ���� 
	
	private static final String NULLSTR = "";
	
	public ODOMainSpc(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}
	
	public void onInit() throws Exception{
		
	}
	/**
	 * У���������Ƿ��Ѿ����
	 * @param order
	 * @return
	 */
	public boolean checkSpcPha(OpdOrder order) throws Exception{
		for (int i = 0; i < order.rowCount(); i++) {
			if (i - 1 >= 0) {// �Ѿ����䷢�Ĵ���ǩ�����������ҽ��
				//===pangben 2013-7-23 �޸���������ʾ��Ϣ
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(i - 1).getValue("RX_NO");
				String seqNo = order.getRowParm(i - 1).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
						"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
				if (!this.checkDrugCanUpdate(order, "MED", i - 1, true,spcReturn)) { // �ж��Ƿ��������ҽ��
					if(spcReturn.getValue("PhaRetnCode").length()>0)
						odoMainControl.messageBox("�Ѿ���ҩ,��ɾ������ǩ����");
					else
						odoMainControl.messageBox("�˴����Ѿ����,�����޸Ĳ���");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * ���ҩƷ�Ƿ��Ѿ����䷢ �Ƿ������ҩ
	 * 
	 * @param type
	 *            String "EXA":��ҩ "CHN":��ҩ
	 * @param row
	 *            int
	 *            flg =true���鵱ǰ�������޸�ҽ������ɾ��ҽ�����޸�ҽ��������ҩ�������޸ĵ��ǿ���ɾ��
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) throws Exception{
		boolean needExamineFlg = false;
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("MED".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("CHN".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		TParm spcReturn=new TParm();
		if (null==spcParm) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
		}else{
			spcReturn=spcParm;
		}
		if(spcReturn.getErrCode()==-2){
			return true;
		}
		// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
		if (needExamineFlg) {
			// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
			if(flg){//============pangben 2013-4-17 ����޸�ҽ������
//				if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0) {
//					return false;
//				}
				if (spcReturn.getValue("PhaCheckCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;
				}
			}
		} else {// û��������� ֱ����ҩ
			// �ж��Ƿ�����ҩҩʦ
			if (flg) {// ============pangben 2013-4-17 ����޸�ҽ������
				if (spcReturn.getValue("PhaDosageCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;// �Ѿ���ҩ���������޸�
				}
			}
		}
		return true;
	}
	
	/**
	 * ����������ҽ��
	 * @param pha_rx_no
	 * @throws Exception
	 */
	public void saveSpcOpdOrder(String pha_rx_no) throws Exception{
		TParm spcParm = new TParm();
		spcParm.setData("RX_NO", pha_rx_no);
		spcParm.setData("CASE_NO", odoMainControl.odoMainReg.reg.caseNo());
		spcParm.setData("CAT1_TYPE", "PHA");
		spcParm.setData("RX_TYPE", "7");
		// ��������ô˴β�����ҽ����ͨ������ǩ���
		TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
				spcParm);
		if (spcResult.getErrCode() < 0) {
			odoMainControl.messageBox("������������ҽ����ѯ���ִ���");
		} else {
			spcResult.setData("SUM_RX_NO", pha_rx_no);
			spcResult = TIOM_AppServer.executeAction(
					"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
					spcResult);
			if (spcResult.getErrCode() < 0) {
				System.out.println("����������:" + spcResult.getErrText());
				odoMainControl.messageBox("������������ҽ����ӳ��ִ���,"
						+ spcResult.getErrText());
			} else {
				phaRxNo = pha_rx_no;// =pangben2013-5-15���ҩ����ҩ��ʾ���������
				sendMedMessages();
			}
		}
	}
	
	/**
	 * ���Ӧ������ҩ��������Ϣ
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages() throws Exception{
		//System.out.println("------sendMedMessages come in-----");
		client1 = SocketLink.running(NULLSTR, "ODO", "ODO");
		if (client1.isClose()) {
			odoMainControl.out(client1.getErrText());
			return;
		}
		String[] phaArray = new String[0];
		if (phaRxNo.length() > 0) {// ������в����Ĵ���ǩ���� ��������
			phaArray = phaRxNo.split(",");
		}
		String sql="";
		TParm result=null;
		String sktUser="";
		for (int i = 0; i < phaArray.length; i++) { 
			sql="SELECT CASE_NO,EXEC_DEPT_CODE FROM OPD_ORDER WHERE CASE_NO='"+odoMainControl.caseNo+"' AND RX_NO='"+phaArray[i]+"'";			
			//System.out.println("----sql----"+sql);			
			result=new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount()<=0) {
				continue;
			}
			//ҩƷ ��ִ�п��ң�������Ϣ
			sktUser=result.getValue("EXEC_DEPT_CODE", 0);
			//
			//"PHAMAIN"
			client1.sendMessage(sktUser, "RX_NO:"// PHAMAIN :SKT_USER ���������
					+ phaArray[i] + "|MR_NO:" + odoMainControl.odoMainPat.pat.getMrNo()
					+ "|PAT_NAME:"
					+ odoMainControl.odoMainPat.pat.getName());
		}
		if (client1 == null)
			return;
		client1.close();
	}
	
	/**
	 * У���ѷ�ҩƷ���ѵ���
	 * 
	 * @param order
	 * @param row
	 */
	public boolean checkSendPah(OpdOrder order, int row) throws Exception{
		if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			TParm spcReturn = TIOM_AppServer.executeAction(
	                "action.opb.OPBSPCAction",
	                "getPhaStateReturn", spcParm);
			if(spcReturn.getErrCode()==-2){
				return true;
			}
			boolean needExamineFlg = false;
			// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
			if ("W".equals(order.getRowParm(row).getValue("PHA_TYPE"))
					|| "C".equals(order.getRowParm(row)
							.getValue("PHA_TYPE"))) {
				// �ж��Ƿ����
				needExamineFlg = PhaSysParmTool.getInstance().needExamine();
			}
			// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
			if (needExamineFlg) {
				// System.out.println("�����");
				// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					odoMainControl.messageBox("ҩƷ�����,�����˷�!");
					return false;
				}
			} else {// û��������� ֱ����ҩ
				// �ж��Ƿ�����ҩҩʦ
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					odoMainControl.messageBox("ҩƷ�ѷ�ҩ,�����˷�!");
					return false;// �Ѿ���ҩ���������޸�
				}
			}
		}
		return true;
	}
	
	/**
	 * ������Ϣ
	 * =====pangben 2013-12-18 �������汾 ҩ��������Ϣ
	 * @param ektSumExeParm
	 * @throws Exception 
	 */
	public void onSendInw(TParm ektSumExeParm, boolean flg) throws Exception {
		if (ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
			// ==pangben 2013-5-21 ���Ԥ����
			if (flg) {
				saveSpcOpdOrder(ektSumExeParm.getValue("PHA_RX_NO"));
			} else {
				phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15���ҩ����ҩ��ʾ���������
				sendMedMessages();
			}
		}
	}
}
