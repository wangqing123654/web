package jdo.spc.uddinfo;

import java.util.List;

import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;


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
public interface SpcUddService {

	/**
	 * ��ʿ����ִ��-�����龫�����ѯ��ҩ��Ϣ����
	 * @param caseNo
	 * @param barCode �龫����
	 * @return
	 */
	public List<OdiDspndPkVo> getOdiDspndInfo(String caseNo,String barCode);
	

}
