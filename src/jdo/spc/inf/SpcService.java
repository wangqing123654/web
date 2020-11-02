package jdo.spc.inf;

import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;


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
public interface SpcService {

	/**
	 * HIS��ҩ��ѯҽ��״̬
	 * @param dto 
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo);
	
	
	/**
	 * HIS������ҵ
	 * @param requestM ���ݴ������
	 * @return
	 */
	public String onSaveSpcRequest (SPCIndRequestm requestM);

	/**
	 * Ԥ��ҩ�ӿ�-�����HIS��������ҽ����Ϣ
	 * @return
	 */
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos);	
}
