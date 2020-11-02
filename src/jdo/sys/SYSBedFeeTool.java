package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ��λ�����趨Tool
 * </p>
 *
 * <p>
 * Description: ��λ�����趨Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.06.16
 * @version 1.0
 */
public class SYSBedFeeTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static SYSBedFeeTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return SYSDiagnosisTool
	 */
	public static SYSBedFeeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SYSBedFeeTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public SYSBedFeeTool() {
		setModuleName("sys\\SYSBedFeeModule.x");
		onInit();
	}

	/**
	 * ȡ�ô�λ������Ϣ
	 *
	 * @param bed_class_code
	 *            �����ȼ�
	 * @return
	 */
	public TParm getSysBedFee(String bed_class_code) {
		String sql = SYSSQL.getSYSBedFee(bed_class_code);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
        /**
         * ȡ�ô�λ������Ϣ
         * @param bedClassCode String ��λ�ȼ�
         * @param bedOccuFlg String ռ��ע��
         * @return TParm
         */
        public TParm getSYSBedFeeOccu(String bedClassCode,
                                      String bedOccuFlg) {
            String sql = SYSSQL.getSYSBedFeeOccu(bedClassCode, bedOccuFlg);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            return result;
        }
	/**
	 * ȡ�õ���Ĵ�λ����
	 *
	 * @param bed_class_code
	 *            �����ȼ�
	 * @return
	 */
	public double getSysBdFeeToday(String bed_class_code) {
		String sql = SYSSQL.getSYSBedFee(bed_class_code);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return -1;
		}
		double bed_fee = 0.00;
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			bed_fee += result.getDouble("OWN_PRICE", i);
		}
		return bed_fee;
	}
}
