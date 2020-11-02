package jdo.spc.phaBaseSyncInfo;

import java.sql.Timestamp;

import javax.jws.WebService;

/**
 * <p>
 * Title: ��������HIS PHA_BASE��PHA_TRANSUNIT ��ͬ�� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: ��������HIS PHA_BASE��PHA_TRANSUNIT��ͬ�� werbservice�ӿ�
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
 * @author shendr 2013-08-12
 * @version 1.0
 */
@WebService
public interface SPCPhaBaseSynchWsTool {

	// ����HIS��PHA_BASE
	public boolean updatePhaBase(String orderCode, String purchUnit,
			String stockUnit, String dosageUnit, String mediUnit);

	// ����HIS��PHA_TRANSUNIT
	public boolean updatePhaTransUnit(String flg, String orderCode,
			String purchUnit, String stockUnit, String dosageUnit,
			String mediUnit, double purchQty, double stockQty,
			double dosageQty, double mediQty, String optUser,
			Timestamp optDate, String optTerm, String regionCode);

}
