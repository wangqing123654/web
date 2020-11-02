package jdo.spc.stockinf;

import java.util.List;

import javax.jws.WebService;

import jdo.spc.stockinf.dto.IndAgent;
import jdo.spc.stockinf.dto.IndStock;

/**
 * <p>
 * Title: ������ werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: ������ werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author fuwj
 * @version 1.0
 */
@WebService
public class SpcStockServiceImpl implements SpcStockService {
	
	/**
	 * ��ѯ�������龫ҩƷ���
	 */
	@Override
	public List<IndStock> onSearchIndStock() {
		return SpcStockDaoImpl.getInstance().onSearchIndStock();
	}
	
	/**
	 * ͬ��HIS��IND_AGNET����
	 */
	@Override
	public String onSaveIndAgent(IndAgent indAgent) {
		// TODO Auto-generated method stub
		return SpcStockDaoImpl.getInstance().onSaveIndAgent(indAgent);
		
	}

}
