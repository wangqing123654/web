package com.javahis.ui.odo;

import com.dongyang.data.TParm;

import jdo.odo.OpdOrder;

/**
 * 
 * <p>
 * 
 * Title: ����ҽ������վ�շѷ�ʽ�ӿ�
 * </p>
 * 
 * <p>
 * Description: ����ҽ������վ�շѷ�ʽ�ӿ�
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
	 * ����
	 * @param odoMainControl
	 * @throws Exception
	 */
	public void onReadCard(OdoMainControl odoMainControl) throws Exception;

	/**
	 * ����
	 * @return
	 * @throws Exception
	 */
	public TParm readCard() throws Exception;

	/**
	 * ɾ��
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
	 * �շѱ���
	 * @param orderParm
	 * @param sumExeParm
	 * @return
	 * @throws Exception
	 */
	public boolean onSave(TParm orderParm, TParm sumExeParm) throws Exception;

	/**
	 * �޸��շ�ҽ������ִ�пۿ���泷������
	 * @param ektOrderParmone
	 * @param ektOrderParm
	 * @return
	 * @throws Exception
	 */
	public boolean updateOrderUnConcle(TParm ektOrderParmone, TParm ektOrderParm)
			throws Exception;

	/**
	 * �ݴ��շ�
	 * @param ektOrderParm
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	public void onTempSave(TParm ektOrderParm, TParm ektOrderParmone,
			TParm ektSumExeParm) throws Exception;

	/**
	 * ����
	 * @throws Exception
	 */
	public void onSave() throws Exception;

	/**
	 * ִ���շ�
	 * @param ektOrderParmone
	 * @param ektSumExeParm
	 * @throws Exception
	 */
	public void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm)
			throws Exception;
}
