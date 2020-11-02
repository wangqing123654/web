package jdo.sys;

import com.dongyang.data.TParm;

public class SysFee {
    /**
     * ����SYSFeeTool
     * @return SYSFeeTool
     */
    public static SYSFeeTool SYSFeeTool() {
        return SYSFeeTool.getInstance();
    }
    /**
     * ����orderCode,����ȼ��õ�����ҽ������
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, String level) {
        TParm result = SYSFeeTool().getFee(orderCode);
        if (result.getErrCode() < 0) {
            return -1;
        }
        if ("2".equals(level)) {
            return result.getDouble("OWN_PRICE2", 0);
        }
        else if ("3".equals(level)) {
            return result.getDouble("OWN_PRICE3", 0);
        }
        else
            return result.getDouble("OWN_PRICE", 0);
    }

    /**
     * ����orderCode�õ��۸�
     * @param orderCode String
     * @return double
     */
    public static double getFee(String orderCode)
       {
           TParm result=SYSFeeTool().getFee(orderCode);
           if (result.getErrCode() < 0){
                 return -1;
             }
           return result.getDouble("OWN_PRICE",0);
     }
    /**
     * ����orderCode�õ�ChargeHospCode
     * @param orderCode String
     * @return String
     */
    public static String getChargeHospCode(String orderCode) {
        TParm chargeHospCode = SYSFeeTool().getChargHospCode(orderCode);
        if (chargeHospCode.getCount() == 0)
            return null;
        return chargeHospCode.getValue("CHARGE_HOSP_CODE", 0);
    }
    /**
     * ��������sysfee�е�����
     * @param orderCode String
     * @return TParm
     */
}
