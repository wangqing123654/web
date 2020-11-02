package jdo.util;

import java.util.Map;

import jdo.bil.BIL;
import jdo.ibs.IBSTool;
import jdo.sys.SYSBedFeeTool;
import jdo.sys.SYSChargeDetailTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TStrike;

/**
 * 
 * <p>
 * Title: ���ù���
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Charge extends TStrike {
	/**
	 * ����,ordercode���շ�,����ݷ���
	 * 
	 * @param ctz1Code
	 *            String���
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 * @param orderCode
	 *            String
	 * @return double
	 */
	public double chargeCTZ(String ctz1Code, String ctz2Code, String ctz3Code,
			String orderCode) {
		return BIL.chargeCTZ(ctz1Code, ctz2Code, ctz3Code, orderCode);
	}

	/**
	 * �õ��������,���������,�۸���Ϣ(INW,UDD����)
	 * 
	 * @param map
	 *            Map
	 * @return Map
	 */
	public Map getIBSOrderData(Map map) {
		TParm parm = new TParm(map);
		return IBSTool.getInstance().getIBSOrderData(parm).getData();
	}

	/**
	 * ��������(For INW , UDD)
	 * 
	 * @param map
	 *            Map
	 * @param connection
	 *            TConnection
	 * @return Map (dataType=3 ��ʿվ,dataType=2 ҩ��)
	 */
	public Map insertIBSOrder(Map map, TConnection connection) {
		TParm parm = new TParm(map);
		return IBSTool.getInstance().insertIBSOrder(parm, connection).getData();
	}

	/**
	 * ȡ������ۿ�
	 * 
	 * @param ctz_code
	 *            ��ݴ���
	 * @param charge_hosp_code
	 *            Ժ�ڷ��ô���
	 * @return
	 */
	public double getSYSChargeDetail(String ctz_code, String charge_hosp_code) {
		if (isClientlink())
			return (Double) callServerMethod(ctz_code, charge_hosp_code);
		return SYSChargeDetailTool.getInstance().selectdata(ctz_code,
				charge_hosp_code).getDouble("DISCOUNT_RATE", 0);
	}

	/**
	 * ���ݴ�λ�ȼ�ȡ�õ���Ĵ�λ����
	 * 
	 * @param bed_class_code
	 *            �����ȼ�
	 * @return
	 */
	public double getSYSBedFeeToday(String bed_class_code) {
		if (isClientlink())
			return (Double) callServerMethod(bed_class_code);
		return SYSBedFeeTool.getInstance().getSysBdFeeToday(bed_class_code);
	}

	/**
	 * ���ݴ�λ�ȼ�ȡ�ô�λ������Ϣ
	 * 
	 * @param bed_class_code
	 *            �����ȼ�
	 * @return Map TParm{BED_CLASS_CODE, ORDER_CODE, OWN_PRICE, CHK_OUT_TIME,
	 *         CODE_CALC_KIND}
	 */
	public Map getSYSBedFee(String bed_class_code) {
		if (isClientlink())
			return (Map) callServerMethod(bed_class_code);
		return SYSBedFeeTool.getInstance().getSysBedFee(bed_class_code)
				.getData();
	}
}
