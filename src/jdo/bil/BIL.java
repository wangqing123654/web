package jdo.bil;

import java.util.HashMap;
import java.util.Map;

import jdo.opb.OPBReceiptTool;
import jdo.reg.PanelTypeFeeTool;
import jdo.reg.Reg;
import jdo.sys.BILRecpParmTool;
import jdo.sys.DictionaryTool;
import jdo.sys.Operator;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.StatusDetailsTool;
import jdo.sys.SysFee;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.Log;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: ������</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BIL {
    /**
     * ����SYSFeeTool
     * @return SYSFeeTool
     */
    public static SYSFeeTool SYSFeeTool() {
        return SYSFeeTool.getInstance();
    }

    /**
     * ����PanelTypeFeeTool
     * @return PanelTypeFeeTool
     */
    public static PanelTypeFeeTool PanelTypeFeeTool() {
        return PanelTypeFeeTool.getInstance();
    }

    /**
     * ����StatusDetailsToolTool
     * @return StatusDetailsTool
     */
    public static StatusDetailsTool getStatusDetailsTool() {
        return StatusDetailsTool.getInstance();
    }

    /**
     * ����BILInvoiceTool
     * @return BILInvoiceTool
     */
    public static BILInvoiceTool getBILInvoiceTool() {
        return BILInvoiceTool.getInstance();
    }

    /**
     * ����ݷ����ۿ���
     * @param ctz1 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String chargeHospCode, String orderCode) {
        return getOwnRate(ctz1, null, null, chargeHospCode, orderCode);
    }

    /**
     * ˫����ۿ���
     * @param ctz1 String
     * @param ctz2 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String ctz2,
                                    String chargeHospCode, String orderCode) {
        return getOwnRate(ctz1, ctz2, null, chargeHospCode, orderCode);
    }

    /**
     * ������ۿ���
     * @param ctz1 String
     * @param ctz2 String
     * @param ctz3 String
     * @param chargeHospCode String
     * @return double
     */
    public static double getOwnRate(String ctz1, String ctz2, String ctz3,
                                    String chargeHospCode, String orderCode) {
        return BILStrike.getInstance().getOwnRate(ctz1, ctz2, ctz3,
                                                  chargeHospCode, orderCode);
    }

    /**
     * ����admType��clinicTypeCode,����ȼ������Һŷ���
     * @param admType String
     * @param clinicTypecode String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String level) {
//        System.out.println("  " + admType + " " + clinicTypecode);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCode(admType, clinicTypecode);
        double out = 0.00;
        if (result.getCount() == 0) {
            return -1;
        }
        for (int i = 0; i < result.getCount(); i++) {
            out +=
                SysFee.getFee( (String) result.getData("ORDER_CODE", i), level);
        }
        return out;
    }

    /**
     * ����admType��clinicTypecode��CTZ1��CTZ2��CTZ3������ȼ��õ��ۿۺ�ķ���
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String CTZ2, String CTZ3,
                                   String level) {
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
//        System.out.println("��ʼ" + admType + "  " + clinicTypecode + "  " + CTZ1);
        TParm result = PanelTypeFeeTool().getOrderCode(admType, clinicTypecode);
//        System.out.println("result  :" + result);
        double out = 0.00;
        if (result.getCount() == 0) {
            return -1;
        }
        for (int i = 0; i < result.getCount(); i++) {
//            System.out.println( (String) result.getData("ORDER_CODE", i));
            double fee = SysFee.getFee( (String) result.getData("ORDER_CODE", i),
                                       level);
            double ownrate = getOwnRate(CTZ1, CTZ2, CTZ3,
                                        SysFee.
                                        getChargeHospCode( (String) result.
                getData("ORDER_CODE", i)));
            out += fee * ownrate;
//            System.out.println("����out   " + out);
        }
        return out;
    }

    /**
     * ����admType��clinicTypecode��CTZ1��CTZ2��CTZ3,����ȼ��õ�����
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String CTZ2, String level) {
        return getRegFee(admType, clinicTypecode, CTZ1, CTZ2, null, level);
    }

    /**
     * ����admType��clinicTypecode��CTZ1������ȼ��õ��ۿۺ�ķ���
     * @param admType String
     * @param clinicTypecode String
     * @param CTZ1 String
     * @param level String
     * @return double
     */
    public static double getRegFee(String admType, String clinicTypecode,
                                   String CTZ1, String level) {
        return getRegFee(admType, clinicTypecode, CTZ1, null, null, level);
    }

    /**
     * ����REG����õ�����
     * @param reg Reg
     * @return double
     */
    public static double getRegfee(Reg reg) {

        return 0.00;
//         getRegFee(reg.getAdmType(),reg.getClinicareaNo())
    }

    /**
     * ����admType��clinicTypeCode,receiptType,����ȼ�����REG��ϸ����
     * @param admType String
     * @param clinicTypecode String
     * @param receiptType String
     * @param level String
     * @return double
     */
    public static double getRegDetialFee(String admType, String clinicTypecode,
                                         String receiptType, String level) {
//        System.out.println("  " + admType + " " + clinicTypecode);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        if (receiptType == null || receiptType.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCodeDetial(admType,
            clinicTypecode, receiptType);
        double out = 0.00;
        if (result.getCount() == 0) {
            return 0;
        }
        out += SysFee.getFee( (String) result.getData("ORDER_CODE", 0), level);
        return out;
    }

    /**
     * ��ѯ����ݵ�reg������ϸ
     * @param admType String
     * @param clinicTypecode String
     * @param receiptType String
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param level String
     * @return double
     */
    public static double getRegDetialFee(String admType, String clinicTypecode,
                                         String receiptType, String CTZ1,
                                         String CTZ2, String CTZ3, String level) {
//        System.out.println("����ȼ�" + level);
        if (admType == null || admType.trim().length() == 0)
            return -1;
        if (clinicTypecode == null || clinicTypecode.trim().length() == 0)
            return -1;
        if (receiptType == null || receiptType.trim().length() == 0)
            return -1;
        TParm result = PanelTypeFeeTool().getOrderCodeDetial(admType,
            clinicTypecode, receiptType);
        double out = 0.00;
        if (result.getCount() == 0) {
            return 0;
        }
        //��ѯ�ۿ���
        out = chargeCTZ(CTZ1, CTZ2, CTZ3,
                        (String) result.getData("ORDER_CODE", 0), level);
        return out;
    }

    /**
     * ����REG��ordercode�õ��������
     * @param reg Reg
     * @param orderCode String
     * @return double
     */
    public static double chargeCTZ(Reg reg, String orderCode) {
        if (reg == null || orderCode == null || orderCode.length() == 0)
            return -1;
        return chargeCTZ(reg.getCtz1Code(), reg.getCtz2Code(), reg.getCtz3Code(),
                         orderCode, "");
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
    public static double chargeCTZ(String CTZ1, String CTZ2, String CTZ3,
                                   String orderCode, String level) {
        if (orderCode == null || orderCode.trim().length() == 0)
            return -1;
        return BILStrike.getInstance().chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode,
                                                 level);
    }

    /**
     * ��������ݺ�ҽ�����룬����ȼ��õ��Ը�����
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getRate(String CTZ1, String CTZ2, String CTZ3,
                                 String orderCode, String level) {
		// ҩ�·���Ѳ�����
		String PHA_SERVICE_FEE = TConfig.getSystemValue("PHA_SERVICE_FEE");
		if (orderCode.equals(PHA_SERVICE_FEE)) {
			return 1;
		}
		//
        String chargeHospCode = SysFee.getChargeHospCode(orderCode);
        if (chargeHospCode == null || chargeHospCode.length() == 0)
            return -1;
        double fee = SysFee.getFee(orderCode, level);
        if (fee == -1)
            return fee;
        return getOwnRate(CTZ1, CTZ2, CTZ3, chargeHospCode, orderCode);
    }

    /**
     * ���ݾ������,ҽ������,����,����ȼ����ؼ�Ǯ��Ϣ
     * @param caseNo String
     * @param orderCode String
     * @param qty double
     * @param level String
     * @return TParm
     */
    public static TParm getPriceForODI(String caseNo, String orderCode,
                                       double qty, String level) {
        TParm result = new TParm();
        String ctzSql =
            "SELECT CTZ1_CODE,CTZ2_CODE,CTZ3_CODE FROM ADM_INP WHERE CASE_NO='" +
            caseNo + "'";
        TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
        if (ctzParm == null) {
            return result;
        }
        if (ctzParm.getErrCode() != 0) {
            return result;
        }
        TParm sysFee = new TParm(TJDODBTool.getInstance().select(
            "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"));
        if (sysFee == null) {
            return result;
        }
        if (sysFee.getErrCode() != 0) {
            return result;
        }
        String ctz1Code = ctzParm.getValue("CTZ1_CODE", 0);
        String ctz2Code = ctzParm.getValue("CTZ2_CODE", 0);
        String ctz3Code = ctzParm.getValue("CTZ3_CODE", 0);
        double ownPrice = getFee(orderCode, level);
        double nhiPrice = sysFee.getDouble("NHI_PRICE", 0);
        double rate = getRate(ctz1Code, ctz2Code, ctz3Code, orderCode, level);
        double ownAmtM = StringTool.round(BIL.getFee(orderCode, qty, level), 2);
        double totAmtM = StringTool.round(ownAmtM * rate, 2);
        result.setData("OWN_PRICE", ownPrice);
        result.setData("NHI_PRICE", nhiPrice);
        result.setData("RATE", rate);
        result.setData("OWN_AMT", ownAmtM);
        result.setData("TOT_AMT", totAmtM);
        return result;
    }

    /**
     * ����ҽ������,����ȼ�����ҽ������
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, String level) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return SysFee.getFee(orderCode, level);
    }

    /**
     * ����ҽ���������ҽ������
     * @param orderCode String
     * @return double
     */
    public static double getFee(String orderCode) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return SysFee.getFee(orderCode);
    }

    /**
     * ����ҽ������,����,����ȼ��������
     * @param orderCode String
     * @param qty double
     * @param level String
     * @return double
     */
    public static double getFee(String orderCode, double qty, String level) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return getFee(orderCode, level) * qty;
    }

    /**
     * ����ҽ������,�����������
     * @param orderCode String
     * @param qty double
     * @return double
     */
    public static double getFee(String orderCode, double qty) {
        if (orderCode == null || orderCode.length() == 0)
            return -1;
        return getFee(orderCode) * qty;
    }

    /**
     * �����,ordercode,����ȼ�����
     * @param CTZ1 String
     * @param CTZ2 String
     * @param orderCode String
     * @param level String
     * @return double
     */
    public static double chargeCTZ(String CTZ1, String CTZ2, String orderCode,
                                   String level) {
        return chargeCTZ(CTZ1, CTZ2, null, orderCode, level);
    }

    /**
     * һ�����,ordercode����
     * @param CTZ String
     * @param orderCode String
     * @return double
     */
    public static double chargeCTZ(String CTZ, String orderCode) {
        return chargeCTZ(CTZ, null, null, orderCode);
    }

    /**
     * ����������շ�,����ݷ�����ordercode�����㺬�������ܼ۸�
     * @param CTZ1 String
     * @param CTZ2 String
     * @param CTZ3 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */

    public static double chargeTotCTZ(String CTZ1, String CTZ2, String CTZ3,
                                      String orderCode, double QTY) {
        return chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode) * QTY;
    }

    public static double chargeTotCTZ(String CTZ1, String CTZ2, String CTZ3,
                                      String orderCode, double QTY,
                                      String level) {
//        System.out.println("BIL���" + CTZ1 + ";" + CTZ2 + ";" + CTZ3 + ";" +
//                           orderCode + ";" + level);

        return chargeCTZ(CTZ1, CTZ2, CTZ3, orderCode, level) * QTY;
    }

    /**
     *  ��������ͣ�ordercode,���㺬�������ܼ۸�
     * @param CTZ1 String
     * @param CTZ2 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */

    public static double chargeTotCTZ(String CTZ1, String CTZ2,
                                      String orderCode, double QTY) {
        return chargeTotCTZ(CTZ1, CTZ2, null, orderCode, QTY);
    }

    /**
     * һ���������,ordercode �����㺬�������ܼ۸�
     * @param CTZ1 String
     * @param orderCode String
     * @param QTY double
     * @return double
     */
    public static double chargeTotCTZ(String CTZ1, String orderCode, double QTY) {
        return chargeTotCTZ(CTZ1, null, null, orderCode, QTY);
    }

    /**
     * �õ���ǰƱ�ź���һƱ��
     * @param recpType String
     * @return String[]
     */
    public static String[] getUpdateNo(String recpType) {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "01");
        String[] updateNo = getBILInvoiceTool().getUpdateUpdateNo(parm);
        return updateNo;
    }

    /**
     * ���REG�Ƿ���
     * @return boolean
     */
    public static boolean checkREG() {
        return checkSwitchBil("REG");
    }

    /**
     * ���OPB�Ƿ���
     * @return boolean
     */
    public static boolean checkOPB() {
        return checkSwitchBil("OPB");
    }

    /**
     * ����Ƿ���
     * @param type String
     * @return boolean
     */
    public static boolean checkSwitchBil(String type) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", type);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0"); //01��ʾƱ��ʹ����
        TParm cashiercode = BILCounteTool.getInstance().CheckCounter(parm);
        String result = cashiercode.getValue("CASHIER_CODE");
        if (result.length() > 0)
            return true;
        return false;
    }

    /**
     * ����Ժ�ڷ��ô���,�ż���õ��վ����
     * @param chargeHospCode String
     * @param admType String
     * @return String
     */
    public static String getRexpCode(String chargeHospCode, String admType) {
        TParm result = getCode(chargeHospCode);
        String rexpCode = "";
        if ("I".equals(admType))
            rexpCode = result.getValue("IPD_CHARGE_CODE", 0);
        else
            rexpCode = result.getValue("OPD_CHARGE_CODE", 0);
        return rexpCode;
    }

    /**
     * ����Ժ�ڷ��ô���õ�������ҳ���ô���
     * @param chargeHospCode String
     * @return String
     */
    public static String getMroChargeCode(String chargeHospCode) {
        TParm result = getCode(chargeHospCode);
        String mroChargeCode = result.getValue("MRO_CHARGE_CODE", 0);
        return mroChargeCode;
    }

    /**
     * ����Ժ�ڷ��ô���õ�ͳ�Ʒ���
     * @param chargeHospCode String
     * @return String
     */
    public static String getStaChargeCode(String chargeHospCode) {
        TParm result = getCode(chargeHospCode);
        String staChargeCode = result.getValue("STA_CHARGE_CODE", 0);
        return staChargeCode;
    }

    /**
     * ����Ժ�ڷ��ô�����Ҹ��ַ���
     * @param chargeHospCode String
     * @return TParm
     */
    public static TParm getCode(String chargeHospCode) {
        TParm parm = new TParm();
        parm.setData("CHARGE_HOSP_CODE", chargeHospCode);
        TParm result = SYSChargeHospCodeTool.getInstance().selectChargeCode(
            parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����շ����
     * @param admType String
     * @return Map
     */
    public Map getChargeToRexpCodeMap(String admType) {
        TParm parm = new TParm();
        parm.setData("ADM_TYPE", admType);
        TParm result = BILRecpParmTool.getInstance().selectcharge(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        Map map = new HashMap();
        map.put(result.getValue("CHARGE01", 0), "CHARGE01");
        map.put(result.getValue("CHARGE02", 0), "CHARGE02");
        map.put(result.getValue("CHARGE03", 0), "CHARGE03");
        map.put(result.getValue("CHARGE04", 0), "CHARGE04");
        map.put(result.getValue("CHARGE05", 0), "CHARGE05");
        map.put(result.getValue("CHARGE06", 0), "CHARGE06");
        map.put(result.getValue("CHARGE07", 0), "CHARGE07");
        map.put(result.getValue("CHARGE08", 0), "CHARGE08");
        map.put(result.getValue("CHARGE09", 0), "CHARGE09");
        map.put(result.getValue("CHARGE10", 0), "CHARGE10");
        map.put(result.getValue("CHARGE11", 0), "CHARGE11");
        map.put(result.getValue("CHARGE12", 0), "CHARGE12");
        map.put(result.getValue("CHARGE13", 0), "CHARGE13");
        map.put(result.getValue("CHARGE14", 0), "CHARGE14");
        map.put(result.getValue("CHARGE15", 0), "CHARGE15");
        map.put(result.getValue("CHARGE16", 0), "CHARGE16");
        map.put(result.getValue("CHARGE17", 0), "CHARGE17");
        map.put(result.getValue("CHARGE18", 0), "CHARGE18");
        map.put(result.getValue("CHARGE19", 0), "CHARGE19");
        map.put(result.getValue("CHARGE20", 0), "CHARGE20");
        map.put(result.getValue("CHARGE21", 0), "CHARGE21");
        map.put(result.getValue("CHARGE22", 0), "CHARGE22");
        map.put(result.getValue("CHARGE23", 0), "CHARGE23");
        map.put(result.getValue("CHARGE24", 0), "CHARGE24");
        map.put(result.getValue("CHARGE25", 0), "CHARGE25");
        map.put(result.getValue("CHARGE26", 0), "CHARGE26");
        map.put(result.getValue("CHARGE27", 0), "CHARGE27");
        map.put(result.getValue("CHARGE28", 0), "CHARGE28");
        map.put(result.getValue("CHARGE29", 0), "CHARGE29");
        map.put(result.getValue("CHARGE30", 0), "CHARGE30");
        return map;

    }

    /**
     *  �õ�chargeId���շ����ƶ�Ӧmap
     * @return Map
     */
    public Map getChargeToRexpdDescMap() {
        TParm result = DictionaryTool.getInstance().getSysCharge("SYS_CHARGE");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        //�õ��������
        int count = result.getCount();
        //׼���Ĵ洢map
        Map map = new HashMap();
        //�շѴ���
        String chargeCode;
        for (int i = 0; i < count; i++) {
            //ȡ��chargercode
            chargeCode = result.getValue("ID", i);
            //����code��desc�Ķ���
            map.put(chargeCode, result.getValue("CHN_DESC", i));
        }
        return map;
    }

    /**
     * �õ�chargeId���շ����ƶ�Ӧmap
     * @return Map
     */
    public Map getChargeToChargeDescMap() {
        TParm result = DictionaryTool.getInstance().getSysCharge("SYS_CHARGE");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        //�õ��������
        int count = result.getCount();
        //׼���Ĵ洢map
        Map map = new HashMap();
        //�շѴ���
        String chargeCode;
        for (int i = 0; i < count; i++) {
            //ȡ��chargercode
            chargeCode = result.getValue("ID", i);
            //����chargecode
            chargeCode = dealStringCharge(chargeCode);
            //����code��desc�Ķ���
            map.put(chargeCode, result.getValue("CHN_DESC", i));
        }
        return map;
    }

    /**
     * ����charge����
     * @param chargeCode String
     * @return String
     */
    public String dealStringCharge(String chargeCode) {
        for (int i = chargeCode.length(); i < 2; i++) {
            chargeCode = "0" + chargeCode;
        }
        return "CHARGE" + chargeCode;
    }

    /**
     * ���������շѽ��
     * @return TParm
     */
    public TParm getChargeParm() {
        TParm chargeParm = new TParm();
        chargeParm.setData("CHARGE01", 0);
        chargeParm.setData("CHARGE02", 0);
        chargeParm.setData("CHARGE03", 0);
        chargeParm.setData("CHARGE04", 0);
        chargeParm.setData("CHARGE05", 0);
        chargeParm.setData("CHARGE06", 0);
        chargeParm.setData("CHARGE07", 0);
        chargeParm.setData("CHARGE08", 0);
        chargeParm.setData("CHARGE09", 0);
        chargeParm.setData("CHARGE10", 0);
        chargeParm.setData("CHARGE11", 0);
        chargeParm.setData("CHARGE12", 0);
        chargeParm.setData("CHARGE13", 0);
        chargeParm.setData("CHARGE14", 0);
        chargeParm.setData("CHARGE15", 0);
        chargeParm.setData("CHARGE16", 0);
        chargeParm.setData("CHARGE17", 0);
        chargeParm.setData("CHARGE18", 0);
        chargeParm.setData("CHARGE19", 0);
        chargeParm.setData("CHARGE20", 0);
        chargeParm.setData("CHARGE21", 0);
        chargeParm.setData("CHARGE22", 0);
        chargeParm.setData("CHARGE23", 0);
        chargeParm.setData("CHARGE24", 0);
        chargeParm.setData("CHARGE25", 0);
        chargeParm.setData("CHARGE26", 0);
        chargeParm.setData("CHARGE27", 0);
        chargeParm.setData("CHARGE28", 0);
        chargeParm.setData("CHARGE29", 0);
        chargeParm.setData("CHARGE30", 0);
        return chargeParm;
    }

    /**
     * �õ����ʽ��parm
     * @return TParm
     */
//                      CHARGE30,TOT_AMT,REDUCE_AMT,
//                      AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
//                      PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
//                      PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK

    public TParm getPayParm() {
        TParm payParm = new TParm();
      //�Żݽ��
        payParm.setData("DIS_AMT", 0);
        //Ӧ�����
        payParm.setData("TOT_AMT", 0);
        //�ۿ�/������
        payParm.setData("REDUCE_AMT", 0);
        //Ӧ�ս��
        payParm.setData("AR_AMT", 0);
        //�ֽ�֧��
        payParm.setData("PAY_CASH", 0);
        //ҽ�ƿ�֧��
        payParm.setData("PAY_MEDICAL_CARD", 0);
        //����ˢ��
        payParm.setData("PAY_BANK_CARD", 0);
        //ҽ����
        payParm.setData("PAY_INS_CARD", 0);
        //֧Ʊ֧��
        payParm.setData("PAY_CHECK", 0);
        
      //��Ʒ��֧��  yanjing 20141020
        payParm.setData("PAY_OTHER3", 0);
      //�ֽ��ۿ�ȯ֧��   yanjing 20141020
        payParm.setData("PAY_OTHER4", 0);
//      //ҽԺ�渶    yanjing 20141020
//        payParm.setData("PAY_CHECK", 0);
        
        //����
        payParm.setData("PAY_DEBIT", 0);
        //���
        payParm.setData("PAY_DRAFT", 0);
        //Ԥ�������
        payParm.setData("PAY_BILPAY", 0);
        //ҽ��ͳ�����
        payParm.setData("PAY_INS", 0);
        //����֧��(���Բ���)
        payParm.setData("PAY_OTHER1", 0);
        //����֧��2
        payParm.setData("PAY_OTHER2", 0);
      //�ֽ𣨶���֧����
        payParm.setData("PAY_TYPE01", 0);
        //ˢ��������֧����
        payParm.setData("PAY_TYPE02", 0);
        //֧Ʊ������֧����
        payParm.setData("PAY_TYPE06", 0);
        //ҽԺ�渶������֧����
        payParm.setData("PAY_TYPE04", 0);
        //��Ʒ��������֧����
        payParm.setData("PAY_TYPE05", 0);
        //�ֽ��ۿ�ȯ������֧����
        payParm.setData("PAY_TYPE07", 0);
      //����ֱ��������֧����  add by huangtt 20150519
        payParm.setData("PAY_TYPE08", 0);
        return payParm;
    }

    /**
     *
     * @return Map
     */
    public Map getHrexpCodeToHrexpDesc() {
        Map map = new HashMap();
        return map;
    }

    /**
     * �õ�Ʊ�ݵ���������
     * @param parm TParm
     * @return TParm
     */
    public TParm dealVoteNo(TParm parm) {
        int count = parm.getCount();
        TParm returnParm = new TParm();
        int firstNo = 0;
        int endNo = 0;
        for (int i = 0; i < count; i++) {
            firstNo = parm.getInt("", i);
        }

        return returnParm;
    }

    /**
     * ��־���
     * @param text String ��־����
     */
    public static void out(String text) {
        Log.getInstance().out(text);
    }

    /**
     * ��־���
     * @param text String ��־����
     * @param debug boolean true ǿ����� false ��ǿ�����
     */
    public static void out(String text, boolean debug) {
        Log.getInstance().out(text, debug);
    }

    /**
     * ������־���
     * @param text String ��־����
     */
    public static void err(String text) {
        Log.getInstance().err(text);
    }

    /*
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.initClient();

//        System.out.println("wwwwww"+getRate("99", "", "","1CCA0001"));
//        TIOM_AppServer.resetAction();
//
    }

    /**
     * �����շ���Ŀ�ķ���(ԭ���ܼ�)
     * @param orderParm TParm
     * @return TParm
     */
    public TParm getChargeFeeParm(TParm orderParm) {
//        System.out.println("orderParm�������==="+orderParm);
        Map chargeMap = getChargeToRexpCodeMap("O");
        //ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
        TParm chargeParm = getChargeParm();
        int count = orderParm.getCount();
        for (int i = 0; i < count; i++) {
            //�շ����
            String rexpCode = orderParm.getValue("REXP_CODE", i);
            //ͨ���շ����õ�CHARGE
            String charge = chargeMap.get(rexpCode).toString();
            //�õ�parm�е��Ͻ��
            double oldTotAmt = chargeParm.getDouble(charge);
//            System.out.println("�����½��"+orderParm.getDouble("OWN_AMT", i)+"==="+oldTotAmt);
            //�����½��
            double totAmt = orderParm.getDouble("OWN_AMT", i) + oldTotAmt;
            //�����շѽ��
            chargeParm.setData(charge, totAmt);
        }
        return chargeParm;
    }
    
    /**
     * �����շ���Ŀ�ķ���(�ۿۺ���ܼ�)
     * @param orderParm TParm
     * @return TParm
     */
    public TParm getChargeFeeArAmtParm(TParm orderParm) {
//        System.out.println("orderParm�������==="+orderParm);
        Map chargeMap = getChargeToRexpCodeMap("O");
        //ÿ�μ�����ö�Ҫ���³�ʼ���շ�parm
        TParm chargeParm = getChargeParm();
        int count = orderParm.getCount();
        for (int i = 0; i < count; i++) {
            //�շ����
            String rexpCode = orderParm.getValue("REXP_CODE", i);
            //ͨ���շ����õ�CHARGE
            String charge = chargeMap.get(rexpCode).toString();
            //�õ�parm�е��Ͻ��
            double oldTotAmt = chargeParm.getDouble(charge);
//            System.out.println("�����½��"+orderParm.getDouble("OWN_AMT", i)+"==="+oldTotAmt);
            //�����½��
            double totAmt = orderParm.getDouble("AR_AMT", i) + oldTotAmt;
            //�����շѽ��
            chargeParm.setData(charge, totAmt);
        }
        return chargeParm;
    }

    /**
     * �����շ���Ŀ�ķ���
     * @param rexp TParm
     * @return TParm
     */
    public TParm getopbChargeParm(TParm rexp) {
//        System.out.println("rexp===" + rexp);
        Map chargeMap = getChargeToRexpCodeMap("O");
        TParm AmtParm = new TParm();
        TParm chargeParm = OPBReceiptTool.getInstance().getOneReceipt(rexp.
            getValue("RECEIPT_NO", 0));
//        System.out.println("chargeParm====��======" + chargeParm);
        int count = rexp.getCount();
        for (int i = 0; i < count; i++) {
            //�շ����
            String rexpCode = rexp.getValue("REXP_CODE", i);
            //ͨ���շ����õ�CHARGE
            String charge = chargeMap.get(rexpCode).toString();
//            System.out.println("charge==========" + charge);
            //�õ�parm�еķ���
            double totAmt = chargeParm.getDouble(charge, 0);
//            System.out.println("totAmt==========" + totAmt);
            //�����շѽ��
            AmtParm.addData(rexpCode, totAmt);
        }
//        System.out.println("AmtParm==========" + AmtParm);
        return AmtParm;
    }

}
