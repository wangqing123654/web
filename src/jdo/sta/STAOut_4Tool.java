package jdo.sta;

import java.util.HashMap;
import java.util.Map;

import jdo.ibs.IBSOrderdTool;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Description: ���������ؼ�������ҽԺ���Ѽ���֧���������ͳ2��3��</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_4Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAOut_4Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAOut_4Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOut_4Tool();
        return instanceObject;
    }

    public STAOut_4Tool() {
        setModuleName("sta\\STAOut_4Module.x");
        onInit();
    }

    /**
     * ��ѯSTA_OUT_01����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OUT_01(TParm parm) {
        TParm result = this.query("selectSTA_OUT_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOPB_RECEIPT(TParm parm) {
        TParm result = this.query("selectOPB_RECEIPT", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�Һŷ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectREG_RECEIPT(TParm parm) {
        TParm result = this.query("selectREG_RECEIPT", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
	 * ����IBSϵͳ���ص�סԺ����
	 * 
	 */
	public TParm selectMRO_RECORD(TParm inparm) {
		TParm ibs = this.query("selectMRO_RECORD", inparm);
		if (ibs.getErrCode() < 0) {
			err("ERR:" + ibs.getErrCode() + ibs.getErrText() + ibs.getErrName());
			return ibs;
		}
		// �Լ�ֵ�Ե���ʽ�洢��������
		Map charge = new HashMap();
		double sumTot = 0;
		double ownTot = 0;
		Map MrofeeCode = STATool.getInstance().getIBSChargeName();
		for (int i = 0; i < ibs.getCount(); i++) {
			if (ibs.getValue("IPD_CHARGE_CODE", i).length() > 0) {
				charge.put(ibs.getValue("IPD_CHARGE_CODE", i), ibs.getValue("TOT_AMT", i));
			}
		}
		String seq = "";
		String c_name = "";
		TParm parm = new TParm();
		for (int i = 1; i <= 30; i++) {
			c_name = "CHARGE";
			if (i < 10)// IС��10 ����
				seq = "0" + i;
			else
				seq = "" + i;
			c_name += seq;
			parm.setData(c_name,
					charge.get(MrofeeCode.get(c_name)) == null ? 0 : charge
							.get(MrofeeCode.get(c_name)));
		}
        return parm;
    }
    /**
     * ����Ҫ����STA_OUT_04�������
     * @param sta_date String
     * @return TParm
     */
    public TParm selectData(String sta_date,String regionCode) {
        TParm result = new TParm();
        //��˲���
        if (sta_date.trim().length() <= 0) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        String dateStart = sta_date + "01";
        String dateEnd = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(sta_date),
                                              "yyyyMMdd");
        TParm parm1 = new TParm();
        parm1.setData("STA_DATE", sta_date);
        //=========pangben modify 20110523 start �����������
        parm1.setData("REGION_CODE", regionCode);
        TParm STA_OUT_01 = this.selectSTA_OUT_01(parm1); //STA_OUT_01��Ϣ
        if (STA_OUT_01.getErrCode() < 0) {
            err("ERR:" + STA_OUT_01.getErrCode() + STA_OUT_01.getErrText() +
                STA_OUT_01.getErrName());
            return STA_OUT_01;
        }
        if (STA_OUT_01.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "���·ݵġ���ͳ2��1����δ�ύ");
            return result;
        }
        TParm parm2 = new TParm();
        parm2.setData("CHARGE_DATE_START", dateStart); //����ʼ����
        parm2.setData("CHARGE_DATE_END", dateEnd); //�����һ������
        parm2.setData("ADM_TYPE", "O");
         //=========pangben modify 20110523 start �����������
        parm2.setData("REGION_CODE", regionCode);
        TParm OPB = this.selectOPB_RECEIPT(parm2); //���������Ϣ
        if (OPB.getErrCode() < 0) {
            err("ERR:" + OPB.getErrCode() + OPB.getErrText() +
                OPB.getErrName());
            return OPB;
        }
        TParm REG = this.selectREG_RECEIPT(parm2); //�Һŷ�����Ϣ
        if (REG.getErrCode() < 0) {
            err("ERR:" + REG.getErrCode() + REG.getErrText() +
                REG.getErrName());
            return REG;
        }
        TParm parm3 = new TParm();
        parm3.setData("OUT_DATE_START", dateStart); //����ʼ����
        parm3.setData("OUT_DATE_END", dateEnd); //�����һ������
        //=========pangben modify 20110523 start �����������
        parm3.setData("REGION_CODE", regionCode);
        TParm MRO = this.selectMRO_RECORD(parm3); //סԺ������Ϣ
        if (MRO.getErrCode() < 0) {
            err("ERR:" + MRO.getErrCode() + MRO.getErrText() +
                MRO.getErrName());
            return MRO;
        }
        //���ձ�ṹ�������ݼ�
        result.setData("STA_DATE", sta_date);
        result.setData("DATA_01", "1");
        result.setData("DATA_02", STA_OUT_01.getValue("DATA_18", 0)); //������
        result.setData("DATA_03", ""); //ƽ��ְ������ (¼��)
        result.setData("DATA_04", ""); //ƽ��ְ������-ҽ��(¼��)
        //����������  ��ͳ2��1.<1> + <2>+<9>+<10>
        result.setData("DATA_05",
                       STA_OUT_01.getInt("DATA_01", 0) +
                       STA_OUT_01.getInt("DATA_02", 0) +
                       STA_OUT_01.getInt("DATA_09", 0) +
                       STA_OUT_01.getInt("DATA_10", 0));
        result.setData("DATA_06", STA_OUT_01.getValue("DATA_11", 0)); //��Ժ����   ��ͳ2��1.<11>
        result.setData("DATA_07", STA_OUT_01.getValue("DATA_19", 0)); //ʵ�ʿ����ܴ�����   ��ͳ2��1.<19>
        result.setData("DATA_08", STA_OUT_01.getValue("DATA_20", 0)); //ƽ�����Ų����� ��ͳ2��1.<20>
        result.setData("DATA_09", STA_OUT_01.getValue("DATA_21", 0)); //ʵ��ռ���ܴ�����  ��ͳ2��1.<21>
        result.setData("DATA_10", STA_OUT_01.getValue("DATA_22", 0)); //��Ժ��ռ���ܴ����� ��ͳ2��1.<22>
        result.setData("DATA_11", STA_OUT_01.getValue("DATA_29", 0)); //��Ժ��ƽ��סԺ��  ��ͳ2��1.<29>

        //�����շ�С�� sys_charge�е� 17������ܺ� ������ҩ�� ���ϹҺŷ���
        double opbSum =OPB.getDouble("CHARGE05", 0) + OPB.getDouble("CHARGE06", 0) +
            OPB.getDouble("CHARGE07", 0) + OPB.getDouble("CHARGE08", 0) +
            OPB.getDouble("CHARGE09", 0) + OPB.getDouble("CHARGE10", 0) +
            OPB.getDouble("CHARGE11", 0) + OPB.getDouble("CHARGE12", 0) +
            OPB.getDouble("CHARGE13", 0) + OPB.getDouble("CHARGE14", 0) +
            OPB.getDouble("CHARGE15", 0) + OPB.getDouble("CHARGE17", 0) +
            OPB.getDouble("CHARGE18", 0) + OPB.getDouble("CHARGE19", 0) +
            REG.getDouble("REG_FEE_REAL", 0); //�Һŷ���

        result.setData("DATA_14", opbSum); //�����շ�С��
        result.setData("DATA_15", REG.getValue("REG_FEE_REAL", 0)); //�Һŷ�
        result.setData("DATA_16", OPB.getValue("CHARGE9", 0)); //�������
        result.setData("DATA_17",
                       opbSum - OPB.getDouble("CHARGE9", 0) -
                       REG.getDouble("REG_FEE_REAL", 0)); //������������
        
        //סԺС�� ������ҩ��
        double admSum =MRO.getDouble("CHARGE01") + MRO.getDouble("CHARGE02") +
        MRO.getDouble("CHARGE07") + MRO.getDouble("CHARGE08") +
        MRO.getDouble("CHARGE09") + MRO.getDouble("CHARGE10") +
        MRO.getDouble("CHARGE11") + MRO.getDouble("CHARGE12") +
        MRO.getDouble("CHARGE13") + MRO.getDouble("CHARGE14") +
        MRO.getDouble("CHARGE15") + MRO.getDouble("CHARGE17") +
        MRO.getDouble("CHARGE18") + MRO.getDouble("CHARGE19")+ MRO.getDouble("CHARGE20");
        
        result.setData("DATA_18", admSum); //סԺС��
        result.setData("DATA_19", MRO.getDouble("CHARGE01")); //��λ�� 
        result.setData("DATA_20", MRO.getDouble("CHARGE08")); //סԺ���Ʒ�
        result.setData("DATA_21", MRO.getDouble("CHARGE07")); //סԺ����
        result.setData("DATA_22",admSum-(MRO.getDouble("CHARGE01")+MRO.getDouble("CHARGE07")+MRO.getDouble("CHARGE08"))); //סԺ��������
        //ҩƷסԺ����
        double admDrug = (MRO.getDouble("CHARGE06") + MRO.getDouble("CHARGE03") +
                MRO.getDouble("CHARGE04") + MRO.getDouble("CHARGE05"));
        double opdDrug =  OPB.getDouble("CHARGE01", 0)+OPB.getDouble("CHARGE02", 0) +
            OPB.getDouble("CHARGE03", 0) + OPB.getDouble("CHARGE04", 0);
        result.setData("DATA_23", admDrug + opdDrug);
        result.setData("DATA_24", admDrug); //ҩƷסԺ����
        result.setData("DATA_25", opdDrug); //ҩƷ��������

        result.setData("DATA_12", opbSum + admSum + admDrug + opdDrug); //�����ܼ�<12>
        result.setData("DATA_13", opbSum + admSum + admDrug + opdDrug); //ҵ������ϼ�

        result.setData("DATA_26", ""); //ҵ����(¼��)
        result.setData("DATA_27", ""); //ר���(¼��)
        result.setData("DATA_28", ""); //��������(¼��)
        result.setData("DATA_29", ""); //֧���ܼ�
        result.setData("DATA_30", ""); //ҵ��֧���ܼ�
        result.setData("DATA_31", ""); //��Ա����С��(¼��)
        result.setData("DATA_32", ""); //��������Ա����(¼��)
        result.setData("DATA_33", ""); //ҩƷ��(¼��)
        result.setData("DATA_34", ""); //����ҵ��֧��(¼��)
        result.setData("DATA_35", ""); //����֧��(¼��)
        result.setData("DATA_36", ""); //�̶��ʲ��ܽ��ϼ�(¼��)
        result.setData("DATA_37", ""); //רҵ�豸�ܽ��(¼��)
        result.setData("DATA_38", ""); //ƽ��ÿ��ռ�̶��ʲ� �ϼ�
        result.setData("DATA_39", ""); //ÿ��ռרҵ�豸�ܽ��
        result.setData("DATA_40", ""); //���ڲ���Ƿ���ܶ�(¼��)
        result.setData("DATA_41", ""); //Ƿ����%
        //ƽ��ÿ�������˴�=�������˴�/�·���  DATA_05/������
        int days = STATool.getInstance().getDaysOfMonth(sta_date);
        if (days > 0)
            result.setData("DATA_42", (STA_OUT_01.getInt("DATA_01", 0) +
                                       STA_OUT_01.getInt("DATA_02", 0) +
                                       STA_OUT_01.getInt("DATA_09", 0) +
                                       STA_OUT_01.getInt("DATA_10", 0)) / days);
        else
            result.setData("DATA_42", "");
        result.setData("DATA_43", STA_OUT_01.getValue("DATA_28", 0)); //ÿ��ʹ����  ��ͳ2��1.<28>
        //������ת����  =��Ժ����/ƽ�����Ų�����
        if(STA_OUT_01.getDouble("DATA_20", 0)!=0)
            result.setData("DATA_44", STA_OUT_01.getDouble("DATA_11", 0)/STA_OUT_01.getDouble("DATA_20", 0));
        else
            result.setData("DATA_44", "");
        result.setData("DATA_45", STA_OUT_01.getValue("DATA_29", 0)); //Ŀǰ��ʱ�趨 ����סԺ���� ��Ժ��ƽ��סԺ��  ��ͳ2��1.<29>
        result.setData("DATA_46", ""); //ƽ��ҩƷ�ӳ���(%)(¼��)
        if (result.getDouble("DATA_05") != 0) {
            //ƽ��ÿ�����˴�ҽ�Ʒ� ҩ��
            result.setData("DATA_48",result.getDouble("DATA_25") /result.getDouble("DATA_05"));
            //ƽ��ÿ�����˴�ҽ�Ʒ� ����
            result.setData("DATA_49",result.getDouble("DATA_16") /result.getDouble("DATA_05"));
        }
        else {
            result.setData("DATA_48", "");
            result.setData("DATA_49", "");
        }
        if(result.getDouble("DATA_05")!=0){
            //ƽ��ÿ�����˴�ҽ�Ʒ� ����
            result.setData("DATA_47",
                           (result.getDouble("DATA_14") +
                            result.getDouble("DATA_25")) /
                           result.getDouble("DATA_05"));
        }
        else
            result.setData("DATA_47","");

        if (result.getDouble("DATA_06") != 0) {
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ��λ��
            result.setData("DATA_51",
                           result.getDouble("DATA_19") /
                           result.getDouble("DATA_06"));
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ҩ��
            result.setData("DATA_52",
                           result.getDouble("DATA_24") /
                           result.getDouble("DATA_06"));
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ���Ʒ�
            result.setData("DATA_53",
                           result.getDouble("DATA_20") /
                           result.getDouble("DATA_06"));
            //ƽ��ÿһ��Ժ��ҽ�Ʒ� ����
            result.setData("DATA_54",
                           result.getDouble("DATA_21") /
                           result.getDouble("DATA_06"));
        }
        else {
            result.setData("DATA_51", "");
            result.setData("DATA_52", "");
            result.setData("DATA_53", "");
            result.setData("DATA_54", "");
        }
        //ƽ��ÿһ��Ժ��ҽ�Ʒ� �ϼ�
        result.setData("DATA_50",
                       result.getDouble("DATA_51") + result.getDouble("DATA_52") +
                       result.getDouble("DATA_53") + result.getDouble("DATA_54"));
        if (result.getDouble("DATA_10") != 0) {
            //��Ժ��ƽ��ÿ��סԺҽ�Ʒ� ��סԺ����С�ƣ�DATA_18��+ҩƷ����סԺ���루DATA_24����/��Ժ��ռ���ܴ�������DATA_10��
            result.setData("DATA_55",(result.getDouble("DATA_18")+result.getDouble("DATA_24"))/result.getDouble("DATA_10"));
        }else{
            result.setData("DATA_55","");
        }
//        TParm DrNum = this.selectDrNum(); //��ѯȫԺҽ������
//        TParm OpNum = this.selectOperatorNum(); //��ѯȫԺְ������
//        double DrN = DrNum.getDouble("NUM", 0);
//        double OpN = OpNum.getDouble("NUM", 0);
        double OpN = 0;//ȫԺְ������ (�ֶ�¼��)
        double DrN = 0;//ȫԺҽ������ (�ֶ�¼��)
        if (OpN != 0) {
            result.setData("DATA_56", result.getDouble("DATA_05") / OpN); //ƽ��ÿһְ�����ڸ����������˴�
            result.setData("DATA_57", result.getDouble("DATA_10") / OpN); //���ڸ�����סԺ������
            result.setData("DATA_58", result.getDouble("DATA_13") / OpN); //����ҵ������
        }
        else {
            result.setData("DATA_56", "");
            result.setData("DATA_57", "");
            result.setData("DATA_58", "");
        }
        if (DrN != 0) {
            result.setData("DATA_59", result.getDouble("DATA_05") / DrN); //ƽ��ÿһҽ�����ڸ����������˴�
            result.setData("DATA_60", result.getDouble("DATA_10") / DrN); //���ڸ�����סԺ������
            result.setData("DATA_61", result.getDouble("DATA_13") / DrN); //����ҵ������
        }
        else {
            result.setData("DATA_59", "");
            result.setData("DATA_60", "");
            result.setData("DATA_61", "");
        }
        result.setData("CONFIRM_FLG", "");
        result.setData("CONFIRM_USER", "");
        result.setData("CONFIRM_DATE", "");
        result.setData("OPT_USER", "");
        result.setData("OPT_TERM", "");
        return result;
    }

    /**
     * ɾ��STA_OUT_04������
     * @param sta_date String
     * @return TParm
     */
    public TParm deleteSTA_OUT_04(String sta_date,String regionCode, TConnection conn) {
        TParm result = new TParm();
        if (sta_date.trim().length() <= 0) {
            result.setErr( -1, "��������Ϊ��");
        }
        TParm parm = new TParm();
        parm.setData("STA_DATE", sta_date);
        //======pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //======pangben modify 20110523 stop
        result = this.update("deleteSTA_OUT_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���STA_OUT_04������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_OUT_04(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_OUT_04", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_OUT_04������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        String sta_date = parm.getValue("STA_DATE");
        //=============pangben modify 20110523 start
        String regionCode=parm.getValue("REGION_CODE");
        //=============pangben modify 20110523 stop
        TParm result = new TParm();
        result = this.deleteSTA_OUT_04(sta_date,regionCode, conn);

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_OUT_04(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�STA_OUT_04����Ϣ ����SQL����
     * @param sql String[]
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_04bySQL(String[] sql, TConnection conn) {
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸�STA_OUT_04�е�����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OUT_04(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getData("SQL1") == null || parm.getData("SQL2") == null ||
            parm.getData("SQL3") == null || parm.getData("SQL4") == null) {
            result.setErr( -1, "ȱ�ٲ���");
            return result;
        }
        String[] sql1 = (String[]) parm.getData("SQL1");
        String[] sql2 = (String[]) parm.getData("SQL2");
        String[] sql3 = (String[]) parm.getData("SQL3");
        String[] sql4 = (String[]) parm.getData("SQL4");
        result = updateSTA_OUT_04bySQL(sql1, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql2, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql3, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = updateSTA_OUT_04bySQL(sql4, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��ӡ����
     * @param STA_DATE String
     * @return TParm
     * ===========pangben modify 20110523 ��Ӳ���
     */
    public TParm selectPrint(String STA_DATE,String regionCode){
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //===========pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //===========pangben modify 20110523 stop
        TParm result = this.query("selectPrint",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ȫԺҽ������
     * @return TParm
     * ===========pangben modify 20110525 ����������
     */
    public TParm selectDrNum(String regionCode){
        //===========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //===========pangben modify 20110525 stop
        TParm result = this.query("selectDrNum",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ȫԺְ������
     * @return TParm
     * ==========pangben modify 20110525
     */
    public TParm selectOperatorNum(String regionCode){
        // ==========pangben modify 20110525 start
        TParm parm = new TParm();
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        // ==========pangben modify 20110525 stop
        TParm result = this.query("selectOperatorNum", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
