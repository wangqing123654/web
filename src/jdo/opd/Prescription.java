package jdo.opd;

import com.dongyang.data.TModifiedData;
/**
*
* <p>
* Title: ����
* </p>
*
* <p>
* Description:����
* </p>
*
* <p>
* Copyright: Copyright (c) 2008
* </p>
*
* <p>
* Company:Javahis
* </p>
*
* @author ehui 200800911
* @version 1.0
*/
public class Prescription extends TModifiedData {

	/**
	 * ҽ��
	 */
	private OrderList orderlist;
	/**
	 * ����ҽ��
	 * @param order
	 */
	public void setOrderList(OrderList orderlist){
		this.orderlist=orderlist;
	}
	/**
	 * �õ�ҽ��
	 * @return
	 */
	public OrderList getOrderList(){
		return orderlist;
	}
	
}
