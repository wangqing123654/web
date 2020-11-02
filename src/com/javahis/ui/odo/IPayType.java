package com.javahis.ui.odo;

import com.dongyang.data.TParm;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站收费方式接口
 * </p>
 * 
 * <p>
 * Description: 门诊医生工作站收费方式接口
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public interface IPayType {
	
	/**
	 * 读卡
	 * @param odoMainControl
	 * @throws Exception
	 */
	public void onReadCard(OdoMainControl odoMainControl) throws Exception;

	/**
	 * 读卡
	 * @return
	 * @throws Exception
	 */
	public TParm readCard() throws Exception;

	/**
	 * 删除
	 * @param order
	 * @param row
	 * @param message
	 * @param medAppMessage
	 * @return
	 * @throws Exception
	 */
	public boolean deleteOrder(OpdOrder order, int row, String message,
			String medAppMessage) throws Exception;

	/**
	 * 收费保存
	 * @param orderParm
	 * @param sumExeParm
	 * @return
	 * @throws Exception
	 */
	public boolean onSave(TParm orderParm, TParm sumExeParm) throws Exception;

	/**
	 * 修改收费医嘱不能执行扣款界面撤销动作
	 * @param ektOrderParmone
	 * @param ektOrderParm
	 * @return
	 * @throws Exception
	 */
	public boolean updateOrderUnConcle(TParm ektOrderParmone, TParm ektOrderParm)
			throws Exception;

	/**
	 * 暂存收费
	 * @param ektOrderParm
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	public void onTempSave(TParm ektOrderParm, TParm ektOrderParmone,
			TParm ektSumExeParm) throws Exception;

	/**
	 * 保存
	 * @throws Exception
	 */
	public void onSave() throws Exception;

	/**
	 * 执行收费
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm)
			throws Exception;
}
