package com.javahis.ui.odo;

import com.dongyang.data.TParm;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站银行卡收费方式实现类
 * </p>
 * 
 * <p>
 * Description: 门诊医生工作站银行卡收费方式实现类
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class BANKPay implements IPayType {

	@Override
	public TParm readCard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteOrder(OpdOrder order, int row, String message,
			String medAppMessage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSave(TParm orderParm, TParm sumExeParm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onReadCard(OdoMainControl odoMainControl) throws Exception {
		// TODO Auto-generated method stub
		
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
		
	}

	@Override
	public void onSave() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) throws Exception {
		
	}


}
