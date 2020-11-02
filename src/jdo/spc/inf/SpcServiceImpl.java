package jdo.spc.inf;


import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.spc.inf.dto.SpcCommonDto;
import jdo.spc.inf.dto.SpcCommonDtos;
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
public class SpcServiceImpl implements SpcService {

	/**
	 * HIS��ҩ��ѯҽ��״̬
	 * @param mrNo ������
	 * @param rxNo ������
	 * @param orderCode ҩƷ����
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo) {
		// TODO Auto-generated method stub
		return SpcDaoImpl.getInstance().getPhaStateReturn(rxNo,caseNo,seqNo);
	}
	
	/**
	 * סԺҩ������
	 */
	@Override
	public String onSaveSpcRequest(SPCIndRequestm requestM) {
		// TODO Auto-generated method stub
		return SpcRequestDaoImpl.getInstance().onSaveSpcRequest(requestM);
	}

	/**
	 *  Ԥ��ҩ�ӿ�-�����HIS��������ҽ����Ϣ
	 */
	@Override
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos) {
		// TODO Auto-generated method stub
		return SpcServiceDaoImpl.getInstance().onSavePhaOrderFromHis(dtos);
	}




	
}