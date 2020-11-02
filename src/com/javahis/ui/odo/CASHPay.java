package com.javahis.ui.odo;

import com.dongyang.data.TParm;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ�ֽ��շѷ�ʽʵ����
 * </p>
 * 
 * <p>
 * Description: ����ҽ������վ�ֽ��շѷ�ʽʵ����
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class CASHPay implements IPayType {
	
	OdoMainControl odoMainControl;
	
	public static final String PAY_TYPE	= "E";
	
	private void onInit(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
	}

	@Override
	public TParm readCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteOrder(OpdOrder order, int row, String message,
			String medAppMessage) throws Exception {
		// TODO Auto-generated method stub
		// ���շ�ҽ��û������ϲ���ɾ��
		int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {
			odoMainControl.messageBox("�뿪�������");
			return false;
		}
		
		if (!canDelete(order, row)) {// У���Ƿ����ɾ��ҽ��
			odoMainControl.messageBox(message); // �ѼƷ�ҽ������ɾ��
			return false;
		}
		//=========pangben 2013-1-29
		if(!odoMainControl.odoMainOpdOrder.odoRxExa.medAppyCheckDate(order, row)){
			odoMainControl.messageBox(medAppMessage); // У�� �������Ѿ��Ǽǵ����ݲ���ɾ������
			return false;
		}
		return true;
	}

	@Override
	public boolean onSave(TParm orderParm, TParm sumExeParm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onReadCard(OdoMainControl odoMainControl) throws Exception {
		// TODO Auto-generated method stub
		onInit(odoMainControl);
	}

	@Override
	public boolean updateOrderUnConcle(TParm ektOrderParmone, TParm ektOrderParm)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTempSave(TParm ektOrderParm, TParm ektOrderParmone,
			TParm ektSumExeParm) throws Exception {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public void onSave() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) throws Exception {
		
	}
	
	/**
	 * У��ҽ�ƿ�ɾ������ rxFlg false : ɾ�����Ŵ���ǩ����ʹ�� true :ɾ����������qi
	 */
	private boolean canDelete(OpdOrder order, int row) {
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		if (!order.isRemovable(row, false)) {// FALSE : �Ѿ��շ� Ҫִ�� onFee() ����
			// TRUE : δ�շ� ��ִ��onFee() ����
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}


}
