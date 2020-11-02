package com.javahis.ui.odo;

import com.dongyang.data.TParm;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站现金收费方式实现类
 * </p>
 * 
 * <p>
 * Description: 门诊医生工作站现金收费方式实现类
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
		// 已收费医嘱没有主诊断不能删除
		int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {
			odoMainControl.messageBox("请开立主诊断");
			return false;
		}
		
		if (!canDelete(order, row)) {// 校验是否可以删除医嘱
			odoMainControl.messageBox(message); // 已计费医嘱不能删除
			return false;
		}
		//=========pangben 2013-1-29
		if(!odoMainControl.odoMainOpdOrder.odoRxExa.medAppyCheckDate(order, row)){
			odoMainControl.messageBox(medAppMessage); // 校验 检验检查已经登记的数据不能删除操作
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
	 * 校验医疗卡删除操作 rxFlg false : 删除整张处方签操作使用 true :删除单个处方qi
	 */
	private boolean canDelete(OpdOrder order, int row) {
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
		if (!order.isRemovable(row, false)) {// FALSE : 已经收费 要执行 onFee() 方法
			// TRUE : 未收费 不执行onFee() 方法
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}


}
