package jdo.opd.ws;

import java.util.List;

import javax.jws.WebService;

/**
*
* <p>
* Title: ҽ���ӿ�
* </p>
*
* <p>
* Description:ҽ��վҽ������webService �������������
* </p>
*
* <p>
* Copyright: Copyright (c) 
* </p>
*
* <p>
* Company:bluecore
* </p>
*
* @author pangben 2013-5-20
* @version 1.0
*/
@WebService
public interface IOpdOrderService {
	/**
	 * ���ҽ������
	 * @return
	 */
	public String saveOpdOrder(OpdOrderList opdOrderList);
}
