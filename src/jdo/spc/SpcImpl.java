package jdo.spc;

import javax.jws.WebService;


/**
 * <p>Title: ������ werbservice�ӿ�</p>
 *
 * <p>Description: ������ werbservice�ӿ�</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 *  @author  Yuanxm
 * @version 1.0
 */

@WebService
public class SpcImpl implements SpcInf {

	/**
	 * �ż���ҩ����ˣ�HISִ���ż���ҩ����˺󡢽�������Ϣ��������������
	 * @param list 
	 * @return
	 */
	public String phaExamine(SpcOpdOrderDtos dtos){
		if(null != dtos){
			
		}
		return "";
	}

	/**
	 * �ż���ҩ����ҩ(��HIS�ż��﷢ҩ������д���ʱ����Ӧҩ������˸��ʾ)
	 * @param SpcCommonDto  ���ݴ������
	 * @return
	 */
	public String phaSend(SpcCommonDtos dtos) {
		return null;
	}

	/**
	 * �ż���ҩ����ҩ�ż���ҩ����ҩ(��HISִ���ż���ҩ����ҩ�󡢽���ҩ��Ϣ������������)
	 * @return
	 */
	public String phaSendClicked(SpcCommonDto dto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}