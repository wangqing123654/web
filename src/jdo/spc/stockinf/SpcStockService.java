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
public interface SpcStockService {
	/**
	 * ��ѯIND_STOCK�龫ҩƷ
	 * @param requestM ���ݴ������
	 * @return
	 */
	public List<IndStock> onSearchIndStock();
	
	
	/**
	 * ͬ��HIS��IND_AGENT
	 * @param indAgent
	 * @return
	 */
	public String onSaveIndAgent(IndAgent indAgent);
	
	
	}
