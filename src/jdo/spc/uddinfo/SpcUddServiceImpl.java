package jdo.spc.uddinfo;


import java.util.List;

import javax.jws.WebService;




/**
 * <p>Title: ������-��ʿ����ִ��- werbservice�ӿ�</p>
 *
 * <p>Description: ������-��ʿ����ִ��- werbservice�ӿ�</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: bluecore</p>
 *
 *  @author  liyh
 * @version 1.0
 */

@WebService
public class SpcUddServiceImpl implements SpcUddService {

	/**
	 * ��ʿ����ִ��-�����龫�����ѯ��ҩ��Ϣ����
	 * @param caseNo
	 * @param barCode �龫����
	 * @return
	 */
	@Override
	public List<OdiDspndPkVo> getOdiDspndInfo(String caseNo, String barCode) {
		// TODO Auto-generated method stu
		return SpcUddServiceDaoImpl.getInstance().getOdiDspndInfo(caseNo, barCode); 
	}
	




	
}