package jdo.bil;

import com.dongyang.jdo.TStrike;
import com.dongyang.util.StringTool;
import jdo.sys.SysFee;
import jdo.sys.StatusDetailsTool;

public class BILStrike extends TStrike{
    /**
     * Ψһʵ��
     */
    private static BILStrike instance;
    /**
     * �õ�ʵ��
     * @return TJDODBTool
     */
    public static BILStrike getInstance()
    {
        if(instance == null)
            instance = new BILStrike();
        return instance;
    }
    /**
     * �������,ordercode���շ�,�����,����ȼ�����
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public double chargeCTZ(String CTZ1, String CTZ2, String CTZ3,
                            String orderCode,String level)
    {
//        System.out.println("BILStrike���"+CTZ1+";"+CTZ2+";"+CTZ3+";"+orderCode+";"+level);
        if (orderCode == null || orderCode.trim().length() == 0)
            return -1;
        if(isClientlink())
            return (Double)callServerMethod(CTZ1,CTZ2,CTZ3,orderCode,level);
        String chargeHospCode = SysFee.getChargeHospCode(orderCode);
        if (chargeHospCode == null || chargeHospCode.length() == 0)
            return -1;
        double fee = SysFee.getFee(orderCode,level);
//        System.out.println("����ȼ����Է���"+fee);
        if (fee == -1)
            return fee;
        double chargeFee = getOwnRate(CTZ1, CTZ2, CTZ3, chargeHospCode, orderCode);
        return StringTool.round(fee * chargeFee, 4);
    }
    /**
     * �ϲ�chargeCTZ��getOwnRate�Ĺ���
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param chargeHospCode String
     * @param level String
     * @return double[]
     */
    public double[] chargeC(String CTZ1, String CTZ2, String CTZ3,
                            String orderCode,String chargeHospCode,String level)
    {
//        System.out.println("BILStrike=CTZ1:"+CTZ1);
//        System.out.println("BILStrike=CTZ2:"+CTZ2);
//        System.out.println("BILStrike=CTZ3:"+CTZ3);
//        System.out.println("BILStrike=orderCode:"+orderCode);
//        System.out.println("BILStrike=chargeHospCode:"+chargeHospCode);
//        System.out.println("BILStrike=level:"+level);
        if (orderCode == null || orderCode.trim().length() == 0)
            return new double[]{0,0};
        if (chargeHospCode == null || chargeHospCode.length() == 0)
            return new double[]{0,0};
        if(isClientlink())
            return (double[])callServerMethod(CTZ1,CTZ2,CTZ3,orderCode,chargeHospCode,level);
        double fee = SysFee.getFee(orderCode,level);
//        System.out.println("����"+fee);
        if (fee == -1)
            return new double[]{0,0};
        double chargeFee = getOwnRate(CTZ1, CTZ2, CTZ3, chargeHospCode, orderCode);
//        System.out.println("����ֵ1��"+StringTool.round(fee * chargeFee, 4)+"����ֵ2��"+chargeFee);
        return new double[]{StringTool.round(fee * chargeFee, 4),chargeFee};
    }
    /**
     * ������ۿ���
     * @param ctz1 String
     * @param ctz2 String
     * @param ctz3 String
     * @param chargeHospCode String
     * @return double
     */
    public double getOwnRate(String ctz1, String ctz2, String ctz3,
                                    String chargeHospCode, String orderCode) {
        if(isClientlink())
            return (Double)callServerMethod(ctz1,ctz2,ctz3,chargeHospCode,orderCode);
        return StatusDetailsTool.getInstance().getOwnRate(ctz1, ctz2, ctz3,
            chargeHospCode,orderCode);
    }
}
