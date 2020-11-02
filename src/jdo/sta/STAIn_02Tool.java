package jdo.sta;

import java.util.HashMap;
import java.util.Map;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: STA_IN_02ҽԺ�š����﹤��ͳ�Ʊ���</p>
 *
 * <p>Description: STA_IN_02ҽԺ�š����﹤��ͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-16
 * @version 1.0
 */
public class STAIn_02Tool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAIn_02Tool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAIn_02Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_02Tool();
        return instanceObject;
    }

    public STAIn_02Tool() {
        setModuleName("sta\\STAIn_02Module.x");
        onInit();
    }

    /**
     * ��ѯSTA_DAILY_02����Ϣ
     * @param parm TParm
     * @return TParm
     * ===========pangben modify 20110523 ����������
     */
    public TParm selectSTA_DAILY_02(String sta_date,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", sta_date);
        //=======pangben modify 20110523 start
        
        if (null != regionCode && regionCode.length() > 0)
            parm.setData("REGION_CODE", regionCode);
        //=======pangben modify 20110523 stop
        TParm result = this.query("selectSTA_DAILY_02", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯSTA_OPD_DAILY����Ϣ
     * @param sta_date String
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY(String sta_date,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", sta_date);
        //=======pangben modify 20110523 start
         if (null != regionCode && regionCode.length() > 0)
             parm.setData("REGION_CODE", regionCode);
         //=======pangben modify 20110523 stop
        TParm result = this.query("selectSTA_OPD_DAILY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯSTA_DAILY_02����Ϣ�����ڶ�ģʽ��ѯ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_DAILY_02(TParm parm) {
        TParm result = this.query("selectSTA_DAILY_02_ByDay", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯSTA_OPD_DAILY����Ϣ�����ڶ�ģʽ��ѯ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY(TParm parm) {
        TParm result = this.query("selectSTA_OPD_DAILY_ByDay", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������(��4������Ϊ��λ)
     * @param sta_date String
     * @return TParm
     * =============pangben modify 20110523 ����������
     */
    public TParm selectData(String sta_date,String regionCode) {
        TParm result = new TParm();
        if (sta_date.trim().length() <= 0) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        //===========pangben modify 20110523 ��Ӳ���
        TParm Daily02 = this.selectSTA_DAILY_02(sta_date,regionCode); //��ѯSTA_DAILY_02����Ϣ
        if (Daily02.getErrCode() < 0) {
            err("ERR:" + Daily02.getErrCode() + Daily02.getErrText() +
                Daily02.getErrName());
            return Daily02;
        }
        
         //===========pangben modify 20110523 ��Ӳ���
        TParm opb = this.selectSTA_OPD_DAILY(sta_date,regionCode); //��ѯSTA_OPD_DAILY����Ϣ
        if (opb.getErrCode() < 0) {
            err("ERR:" + opb.getErrCode() + opb.getErrText() +
                opb.getErrName());
            return opb;
        }
        TParm inparm=new TParm();
        inparm.setData("STA_DATE", sta_date);
        Map maxMap=this.getMaxNum(inparm, "selectDayNum");
        Map minMap=this.getMinNum(inparm, "selectDayNum");
        //��ѯ�м������Ϣ ������ ����ҽʦ����������λ���ȣ�
        //===========pangben modify 20110523 ��Ӳ���
        TParm deptParm=new TParm();
        deptParm.setData("REGION_CODE",regionCode);
//        System.out.println("drNum:"+drNum);
//        System.out.println("opb:"+opb);
        //��ѯ�����ļ�����  �ż��ﲿ��
        TParm deptList = STATool.getInstance().getDeptByLevel(new String[] {"4"},
            "OE",regionCode);//===========pangben modify 20110523
        String deptCode = ""; //��¼����CODE
        String stationCodeTemp="";//��¼����CODE
        
        for (int i = 0; i < deptList.getCount(); i++) {
            deptCode = deptList.getValue("DEPT_CODE", i);
            stationCodeTemp= deptList.getValue("STATION_CODE", i);//===========pangben modify 20110523
            //��������ֶεı���
            int DATA_02 = 0;
            int DATA_03 = 0;
            int DATA_04 = 0;
            int DATA_05 = 0;
            int DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            int DATA_10 = 0;
            int DATA_11 = 0;
            int DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            double DATA_19 = 0;
            double DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int DATA_25 = 0;
            int DATA_26 = 0;
            double DATA_27 = 0;
            double DATA_28 = 0;
            double DATA_29 = 0;
            int DATA_30 = 0;
            double DATA_31 = 0;
            int DATA_32 = 0;
            double DATA_33 = 0;
            double DATA_34 = 0;
            
            //add by yangjj 20150512���������˴�
            double DATA_35 = 0;
            
            String stationCode = "";
            int P_num = 0; //��¼ʵ�в�����
            int inP_num = 0; //��¼��Ժ�����ܼ�
            //ѭ��ͳ��STA_DAILY_02������ֶε�����
            for (int j = 0; j < Daily02.getCount(); j++) {
                if (Daily02.getValue("DEPT_CODE", j).equals(deptCode)) {
                    stationCode = Daily02.getValue("STATION_CODE", j); //����CODE
                    DATA_03 = Daily02.getInt("DATA_02", j) +
                        Daily02.getInt("DATA_03", j); //�ż����˴μ�
                    DATA_04 = Daily02.getInt("DATA_02", j); //�����˴�
                    DATA_05 = Daily02.getInt("DATA_03", j); //�����
                    DATA_06 = Daily02.getInt("DATA_04", j); //��������
                    DATA_08 = Daily02.getInt("DATA_01", j); //�Һ���
                    DATA_09 = Daily02.getInt("DATA_05", j); //�۲�������
                    DATA_10 = Daily02.getInt("DATA_06", j); //�۲�������
                    P_num = Daily02.getInt("DATA_16", j); //ʵ���в������ܼ�
                    inP_num = Daily02.getInt("DATA_08", j); //��Ժ�����ܼ�
                }
            }
            if (DATA_09 != 0) {
                DATA_29 = DATA_10 / DATA_09; //�۲���������
            }
            //ѭ��ͳ�������м�������ֶε����� �����ۼ�
            for (int j = 0; j < opb.getCount(); j++) {
                if (opb.getValue("DEPT_CODE", j).equals(deptCode)) {
                    DATA_15 = opb.getInt("DR_HOURS", j); //����Сʱ
                    DATA_16 = opb.getInt("WORKDAYS", j); //����ʵ�ʹ�����
//                    DATA_17 = opb.getInt("MAXNUM", j); //������˴�
//                    DATA_18 = opb.getInt("MINNUM", j); //������˴�
                    DATA_19 = opb.getDouble("OUTP_NUM_AVG", j); //ƽ��ÿ�������˴�
                    DATA_20 = opb.getDouble("ERD_NUM_AVG", j); //ƽ��ÿ�ռ����˴�
                    DATA_07 = opb.getInt("OTHER_NUM", j); //�����������
                    DATA_25 = opb.getInt("GET_TIMES", j); //������������
                    DATA_26 = opb.getInt("SUCCESS_TIMES", j); //�������ȳɹ�����
                    DATA_11 = opb.getInt("ZR_DR_NUM",j);//��������ҽʦ
                    DATA_12 = opb.getInt("ZZ_DR_NUM",j);//����ҽʦ
                    DATA_13 = opb.getInt("ZY_DR_NUM",j);//סԺҽʦ
                    DATA_14 = opb.getInt("ZX_DR_NUM",j);//����ҽʦ
                    
                    //add by yangjj 20150512 ���������˴�
                    DATA_35 = opb.getInt("OPE_NUM", j);//�����˴�
                }
            }
            DATA_17=maxMap.get(deptCode)==null?0:Integer.valueOf(maxMap.get(deptCode).toString());
            DATA_18=minMap.get(deptCode)==null?0:Integer.valueOf(minMap.get(deptCode).toString());
            if (DATA_25 != 0) {
                DATA_27 = (DATA_25 - DATA_26) / DATA_25*100; //����������
                DATA_28 = DATA_26 / DATA_25*100; //�������ȳɹ���
            }
            //�ܼ�
            DATA_02 = DATA_03 + DATA_07;
            //ҽʦÿСʱ�����˴�
            if (DATA_15 != 0) {
                DATA_21 = (double) DATA_02 / (double) DATA_15;
            }
            //ÿ����ÿ���ż�����α�
            if (P_num != 0) {
                DATA_22 = (double) DATA_03 / (double) P_num;
            }
            //ÿ���ż�����Ժ�˴�
            if (DATA_03 != 0) {
                DATA_23 = (double) inP_num / (double) ( (double) DATA_03 / 100);
            }
            //�ż������ռ����ΰٷֱ�
            if (DATA_02 != 0) {
                DATA_24 = (double) DATA_03 / (double) DATA_02 * 100;
            }
            if(DATA_13 != 0){
                DATA_34 = (double)DATA_11/(double)DATA_13;
            }
            //�������
            result.addData("STA_DATE", sta_date);
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", null==stationCode ||stationCode.length()==0? stationCodeTemp :stationCode);
            result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
            result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
            result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
            result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
            result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
            result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
            result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
            result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
            result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
            result.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
            result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
            result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
            result.addData("DATA_28", DATA_28 == 0 ? "" : DATA_28);
            result.addData("DATA_29", DATA_29 == 0 ? "" : DATA_29);
            result.addData("DATA_30", DATA_30 == 0 ? "" : DATA_30);
            result.addData("DATA_31", DATA_31 == 0 ? "" : DATA_31);
            result.addData("DATA_32", DATA_32 == 0 ? "" : DATA_32);
            result.addData("DATA_33", DATA_33 == 0 ? "" : DATA_33);
            result.addData("DATA_34", DATA_34 == 0 ? "" : DATA_34);
            
            //add by yangjj 20150512���������˴�
            result.addData("DATA_35", DATA_35 == 0 ? "" : DATA_35);
            
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
        }
        return result;
    }

    /**
     * ��������(��4������Ϊ��λ)  ���ڶ�ͳ������
     * @param parm TParm ������DATE_S:��ʼ���ڣ����룩; DATE_E:��ֹ���ڣ����룩�� DEPT������CODE���Ǳ��룩
     * @return TParm
     */
    public TParm selectData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ��");
            return result;
        }
        TParm Daily02 = this.selectSTA_DAILY_02(parm); //��ѯSTA_DAILY_02����Ϣ
        if (Daily02.getErrCode() < 0) {
            err("ERR:" + Daily02.getErrCode() + Daily02.getErrText() +
                Daily02.getErrName());
            return Daily02;
        }
        TParm opb = this.selectSTA_OPD_DAILY(parm); //��ѯSTA_OPD_DAILY����Ϣ
        if (opb.getErrCode() < 0) {
            err("ERR:" + opb.getErrCode() + opb.getErrText() +
                opb.getErrName());
            return opb;
        }
        Map maxMap=this.getMaxNum(parm, "selectDayNumByDay");
        Map minMap=this.getMinNum(parm, "selectDayNumByDay");
        //�жϲ�ѯ�������Ƿ�������ţ����ָ���˲�����ôֻͳ�Ƹò��ŵ���Ϣ �������ź���
        TParm deptList = new TParm();
        if (parm.getValue("DEPT_CODE").trim().length() <= 0) {
            //��ѯ�����ļ�����
            deptList = STATool.getInstance().getDeptByLevel(new String[] {"4"},
                "OE",parm.getValue("REGION_CODE"));//=======pangben modify 20110523
        }
        else {
            //��ѯָ���Ĳ���
            deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(parm.
                getValue("DEPT_CODE"),parm.getValue("REGION_CODE"));//=======pangben modify 20110523
        }
        int TDATA_02 = 0;
		int TDATA_03 = 0;
		int TDATA_04 = 0;
		int TDATA_05 = 0;
		int TDATA_06 = 0;
		int TDATA_07 = 0;
		int TDATA_08 = 0;
		int TDATA_09 = 0;
		int TDATA_10 = 0;
		int TDATA_11 = 0;
		int TDATA_12 = 0;
		int TDATA_13 = 0;
		int TDATA_14 = 0;
		int TDATA_15 = 0;
		int TDATA_16 = 0;
		int TDATA_17 = 0;
		int TDATA_18 = 0;
		double TDATA_19 = 0;
		double TDATA_20 = 0;
		double TDATA_21 = 0;
		double TDATA_22 = 0;
		double TDATA_23 = 0;
		double TDATA_24 = 0;
		int TDATA_25 = 0;
		int TDATA_26 = 0;
		double TDATA_27 = 0;
		double TDATA_28 = 0;
		double TDATA_29 = 0;
		int TDATA_30 = 0;
		double TDATA_31 = 0;
		int TDATA_32 = 0;
		double TDATA_33 = 0;
		double TDATA_34 = 0;
		
		//add by yangjj 20150512���������˴�
		int TDATA_35 = 0;
		
		int TP_num = 0; //��¼ʵ�в�����
        int TinP_num = 0; //��¼��Ժ�����ܼ�
        String deptCode = ""; //��¼����CODE
        for (int i = 0; i < deptList.getCount(); i++) {
            deptCode = deptList.getValue("DEPT_CODE", i);
            //��������ֶεı���
            int DATA_02 = 0;
            int DATA_03 = 0;
            int DATA_04 = 0;
            int DATA_05 = 0;
            int DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            int DATA_10 = 0;
            int DATA_11 = 0;
            int DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            int DATA_17 = 0;
            int DATA_18 = 0;
            double DATA_19 = 0;
            double DATA_20 = 0;
            double DATA_21 = 0;
            double DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int DATA_25 = 0;
            int DATA_26 = 0;
            double DATA_27 = 0;
            double DATA_28 = 0;
            double DATA_29 = 0;
            int DATA_30 = 0;
            double DATA_31 = 0;
            int DATA_32 = 0;
            double DATA_33 = 0;
            double DATA_34 = 0;
            
            //add by yangjj 20150512 ���������˴�
            int DATA_35 = 0;
            String stationCode = "";
            int P_num = 0; //��¼ʵ�в�����
            int inP_num = 0; //��¼��Ժ�����ܼ�

            //ѭ��ͳ��STA_DAILY_02������ֶε�����
            for (int j = 0; j < Daily02.getCount(); j++) {
                if (Daily02.getValue("DEPT_CODE", j).equals(deptCode)) {
                    stationCode = Daily02.getValue("STATION_CODE", j); //����CODE
                    DATA_03 = Daily02.getInt("DATA_01", j); //�ż����˴μ�
                    DATA_04 = Daily02.getInt("DATA_02", j); //�����˴�
                    DATA_05 = Daily02.getInt("DATA_03", j); //�����
                    DATA_06 = Daily02.getInt("DATA_04", j); //��������
                    DATA_08 = Daily02.getInt("DATA_01", j); //�Һ���
                    DATA_09 = Daily02.getInt("DATA_05", j); //�۲�������
                    DATA_10 = Daily02.getInt("DATA_06", j); //�۲�������
                    P_num = Daily02.getInt("DATA_16", j); //ʵ���в������ܼ�
                    inP_num = Daily02.getInt("DATA_08", j); //��Ժ�����ܼ�
                }
            }
            TP_num+=P_num;
            TinP_num+=inP_num;
            if (DATA_09 != 0) {
                DATA_29 = (double) DATA_10 / (double) DATA_09 * 100; //�۲���������
            }
            //ѭ��ͳ�������м�������ֶε����� �����ۼ�
            for (int j = 0; j < opb.getCount(); j++) {
                if (opb.getValue("DEPT_CODE", j).equals(deptCode)) {
                    DATA_15 = opb.getInt("DR_HOURS", j); //����Сʱ
                    DATA_16 = opb.getInt("WORKDAYS", j); //����ʵ�ʹ�����
//                    DATA_17 = opb.getInt("MAXNUM", j); //������˴�
//                    DATA_18 = opb.getInt("MINNUM", j); //������˴�
                    DATA_19 = opb.getDouble("OUTP_NUM_AVG", j); //ƽ��ÿ�������˴�
                    DATA_20 = opb.getDouble("ERD_NUM_AVG", j); //ƽ��ÿ�ռ����˴�
                    DATA_07 = opb.getInt("OTHER_NUM", j); //�����������
                    DATA_25 = opb.getInt("GET_TIMES", j); //������������
                    DATA_26 = opb.getInt("SUCCESS_TIMES", j); //�������ȳɹ�����
                    DATA_11 = opb.getInt("ZR_DR_NUM",j);//��������ҽʦ
                    DATA_12 = opb.getInt("ZZ_DR_NUM",j);//����ҽʦ
                    DATA_13 = opb.getInt("ZY_DR_NUM",j);//סԺҽʦ
                    DATA_14 = opb.getInt("ZX_DR_NUM",j);//����ҽʦ
                    
                    //add by yangjj 20150512 ���������˴�
                    DATA_35 = opb.getInt("OPE_NUM", j);//�����˴�
                }
            }
            DATA_17=maxMap.get(deptCode)==null?0:Integer.valueOf(maxMap.get(deptCode).toString());
            DATA_18=minMap.get(deptCode)==null?0:Integer.valueOf(minMap.get(deptCode).toString());
            if (DATA_25 != 0) {
                DATA_27 = (double) (DATA_25 - DATA_26) / (double) DATA_25 * 100; //����������
                DATA_28 = (double) DATA_26 / (double) DATA_25 * 100; //�������ȳɹ���
            }
            //�ܼ�
            DATA_02 = DATA_03 + DATA_07;
            //ҽʦÿСʱ�����˴�
            if (DATA_15 != 0) {
                DATA_21 = (double) DATA_02 / (double) DATA_15;
            }
            //ÿ����ÿ���ż�����α�
            if (P_num != 0) {
                DATA_22 = (double) DATA_03 / (double) P_num;
            }
            //ÿ���ż�����Ժ�˴�
            if (DATA_03 != 0) {
                DATA_23 = (double) inP_num / (double) ( (double) DATA_03 / 100);
            }
            //�ż������ռ����ΰٷֱ�
            if (DATA_02 != 0) {
                DATA_24 = (double) DATA_03 / (double) DATA_02 * 100;
            }
            //�������
            result.addData("STA_DATE",
                           parm.getValue("DEPT_S") + "-" +
                           parm.getValue("DEPT_E"));
            result.addData("DEPT_CODE", deptCode);
            result.addData("STATION_CODE", stationCode);
            result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
            result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
            result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
            result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
            result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
            result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
            result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
            result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
            result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
            result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
            result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
            result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
            result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
            result.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
            result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
            result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
            result.addData("DATA_28", DATA_28 == 0 ? "" : DATA_28);
            result.addData("DATA_29", DATA_29 == 0 ? "" : DATA_29);
            result.addData("DATA_30", DATA_30 == 0 ? "" : DATA_30);
            result.addData("DATA_31", DATA_31 == 0 ? "" : DATA_31);
            result.addData("DATA_32", DATA_32 == 0 ? "" : DATA_32);
            result.addData("DATA_33", DATA_33 == 0 ? "" : DATA_33);
            result.addData("DATA_34", DATA_34 == 0 ? "" : DATA_34);
            
            //add by yangjj 20150512���������˴�
            result.addData("DATA_35", DATA_35 == 0 ? "" : DATA_35);
            
            result.addData("CONFIRM_FLG", "N");
            result.addData("CONFIRM_USER", "");
            result.addData("CONFIRM_DATE", "");
            result.addData("OPT_USER", "");
            result.addData("OPT_TERM", "");
            TDATA_02 += DATA_02;
			TDATA_03 += DATA_03;
			TDATA_04 += DATA_04;
			TDATA_05 += DATA_05;
			TDATA_06 += DATA_06;
			TDATA_07 += DATA_07;
			TDATA_08 += DATA_08;
			TDATA_09 += DATA_09;
			TDATA_10 += DATA_10;
			TDATA_11 += DATA_11;
			TDATA_12 += DATA_12;
			TDATA_13 += DATA_13;
			TDATA_14 += DATA_14;
			TDATA_15 += DATA_15;
			TDATA_16 = DATA_16;
			TDATA_17 += DATA_17;
			TDATA_18 += DATA_18;
			TDATA_19 += DATA_19;
			TDATA_20 += DATA_20;
			TDATA_25 += DATA_25;
			TDATA_26 += DATA_26;
			TDATA_30 += DATA_30;
			TDATA_31 += DATA_31;
			TDATA_32 += DATA_32;
			TDATA_33 += DATA_33;
			
			//add by yangjj 20150512���������˴�
			TDATA_35 += DATA_35;
        }
        if (TDATA_25 != 0) {
            TDATA_27 = (double) (TDATA_25 - TDATA_26) / (double) TDATA_25 * 100; //����������
            TDATA_28 = (double) TDATA_26 / (double) TDATA_25 * 100; //�������ȳɹ���
        }
        if (TDATA_09 != 0) {
            TDATA_29 = (double) TDATA_10 / (double) TDATA_09 * 100; //�۲���������
        }
      //�ܼ�
        TDATA_02 = TDATA_03 + TDATA_07;
        //ҽʦÿСʱ�����˴�
        if (TDATA_15 != 0) {
            TDATA_21 = (double) TDATA_02 / (double) TDATA_15;
        }
        //ÿ����ÿ���ż�����α�
        if (TP_num != 0) {
            TDATA_22 = (double) TDATA_03 / (double) TP_num;
        }
        //ÿ���ż�����Ժ�˴�
        if (TDATA_03 != 0) {
            TDATA_23 = (double) TinP_num / (double) ( (double) TDATA_03 / 100);
        }
        //�ż������ռ����ΰٷֱ�
        if (TDATA_02 != 0) {
            TDATA_24 = (double) TDATA_03 / (double) TDATA_02 * 100;
        }
        result.addData("DEPT_CODE", "�ϼ�:");
        result.addData("DATA_02", TDATA_02 == 0 ? "" : TDATA_02);
        result.addData("DATA_03", TDATA_03 == 0 ? "" : TDATA_03);
        result.addData("DATA_04", TDATA_04 == 0 ? "" : TDATA_04);
        result.addData("DATA_05", TDATA_05 == 0 ? "" : TDATA_05);
        result.addData("DATA_06", TDATA_06 == 0 ? "" : TDATA_06);
        result.addData("DATA_07", TDATA_07 == 0 ? "" : TDATA_07);
        result.addData("DATA_08", TDATA_08 == 0 ? "" : TDATA_08);
        result.addData("DATA_09", TDATA_09 == 0 ? "" : TDATA_09);
        result.addData("DATA_10", TDATA_10 == 0 ? "" : TDATA_10);
        result.addData("DATA_11", TDATA_11 == 0 ? "" : TDATA_11);
        result.addData("DATA_12", TDATA_12 == 0 ? "" : TDATA_12);
        result.addData("DATA_13", TDATA_13 == 0 ? "" : TDATA_13);
        result.addData("DATA_14", TDATA_14 == 0 ? "" : TDATA_14);
        result.addData("DATA_15", TDATA_15 == 0 ? "" : TDATA_15);
        result.addData("DATA_16", TDATA_16 == 0 ? "" : TDATA_16);
        result.addData("DATA_17", TDATA_17 == 0 ? "" : TDATA_17);
        result.addData("DATA_18", TDATA_18 == 0 ? "" : TDATA_18);
        result.addData("DATA_19", TDATA_19 == 0 ? "" : TDATA_19);
        result.addData("DATA_20", TDATA_20 == 0 ? "" : TDATA_20);
        result.addData("DATA_21", TDATA_21 == 0 ? "" : TDATA_21);
        result.addData("DATA_22", TDATA_22 == 0 ? "" : TDATA_22);
        result.addData("DATA_23", TDATA_23 == 0 ? "" : TDATA_23);
        result.addData("DATA_24", TDATA_24 == 0 ? "" : TDATA_24);
        result.addData("DATA_25", TDATA_25 == 0 ? "" : TDATA_25);
        result.addData("DATA_26", TDATA_26 == 0 ? "" : TDATA_26);
        result.addData("DATA_27", TDATA_27 == 0 ? "" : TDATA_27);
        result.addData("DATA_28", TDATA_28 == 0 ? "" : TDATA_28);
        result.addData("DATA_29", TDATA_29 == 0 ? "" : TDATA_29);
        result.addData("DATA_30", TDATA_30 == 0 ? "" : TDATA_30);
        result.addData("DATA_31", TDATA_31 == 0 ? "" : TDATA_31);
        result.addData("DATA_32", TDATA_32 == 0 ? "" : TDATA_32);
        result.addData("DATA_33", TDATA_33 == 0 ? "" : TDATA_33);
        result.addData("DATA_34", TDATA_34 == 0 ? "" : TDATA_34);
        
        //add by yangjj 20150512 ���������˴�
        result.addData("DATA_35", TDATA_35 == 0 ? "" : TDATA_35);
        
        result.addData("CONFIRM_FLG", "N");
        result.addData("CONFIRM_USER", "");
        result.addData("CONFIRM_DATE", "");
        result.addData("OPT_USER", "");
        result.addData("OPT_TERM", "");
        result.setCount(deptList.getCount()+1);
        return result;
    }

    /**
     * ɾ����STA_IN_02����
     * @param STA_DATE String
     * @return TParm
     */
    public TParm deleteSTA_IN_02(String STA_DATE,String regionCode, TConnection conn) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //============pangben modify 20110523 start
        if(null!=regionCode&&regionCode.length()>0)
            parm.setData("REGION_CODE", regionCode);
        //============pangben modify 20110523 stop
        TParm result = this.update("deleteSTA_IN_02", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_IN_02����
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_IN_02(TParm parm, TConnection conn) {
        TParm result = this.update("insertSTA_IN_02", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����STA_IN_02����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "û�пɲ��������");
            return result;
        }
        String STA_DATE = parm.getValue("STA_DATE", 0);
        //=============pangben modify 20110523 start
        String regionCode = parm.getValue("REGION_CODE", 0);
        //=============pangben modify 20110523 stop
        if (STA_DATE.trim().length() <= 0) {
            result.setErr( -1, "STA_DATE����Ϊ��");
            return result;
        }
        //=============pangben modify 20110523 ����������
        result = this.deleteSTA_IN_02(STA_DATE,regionCode, conn); //ɾ�������ڵ�����
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
//        System.out.println("parm::::"+parm);
        TParm insert = null;
        for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
            insert = new TParm();
            insert.setData("STA_DATE", parm.getValue("STA_DATE", i));
            insert.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
            insert.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
            insert.setData("DATA_02", parm.getValue("DATA_02", i));
            insert.setData("DATA_03", parm.getValue("DATA_03", i));
            insert.setData("DATA_04", parm.getValue("DATA_04", i));
            insert.setData("DATA_05", parm.getValue("DATA_05", i));
            insert.setData("DATA_06", parm.getValue("DATA_06", i));
            insert.setData("DATA_07", parm.getValue("DATA_07", i));
            insert.setData("DATA_08", parm.getValue("DATA_08", i));
            insert.setData("DATA_09", parm.getValue("DATA_09", i));
            insert.setData("DATA_10", parm.getValue("DATA_10", i));
            insert.setData("DATA_11", parm.getValue("DATA_11", i));
            insert.setData("DATA_12", parm.getValue("DATA_12", i));
            insert.setData("DATA_13", parm.getValue("DATA_13", i));
            insert.setData("DATA_14", parm.getValue("DATA_14", i));
            insert.setData("DATA_15", parm.getValue("DATA_15", i));
            insert.setData("DATA_16", parm.getValue("DATA_16", i));
            insert.setData("DATA_17", parm.getValue("DATA_17", i));
            insert.setData("DATA_18", parm.getValue("DATA_18", i));
            insert.setData("DATA_19", parm.getValue("DATA_19", i));
            insert.setData("DATA_20", parm.getValue("DATA_20", i));
            insert.setData("DATA_21", parm.getValue("DATA_21", i));
            insert.setData("DATA_22", parm.getValue("DATA_22", i));
            insert.setData("DATA_23", parm.getValue("DATA_23", i));
            insert.setData("DATA_24", parm.getValue("DATA_24", i));
            insert.setData("DATA_25", parm.getValue("DATA_25", i));
            insert.setData("DATA_26", parm.getValue("DATA_26", i));
            insert.setData("DATA_27", parm.getValue("DATA_27", i));
            insert.setData("DATA_28", parm.getValue("DATA_28", i));
            insert.setData("DATA_29", parm.getValue("DATA_29", i));
            insert.setData("DATA_30", parm.getValue("DATA_30", i));
            insert.setData("DATA_31", parm.getValue("DATA_31", i));
            insert.setData("DATA_32", parm.getValue("DATA_32", i));
            insert.setData("DATA_33", parm.getValue("DATA_33", i));
            insert.setData("DATA_34", parm.getValue("DATA_34", i));
            
            //add by yangjj 20150512���������˴�
            insert.setData("DATA_35", parm.getValue("DATA_35", i));
            
            insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
            insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
            insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
            insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
            insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
            //=========pangben modify 20110523 start
            insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
            //=========pangben modify 20110523 stop
            
            result = this.insertSTA_IN_02(insert, conn); //����������
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ��ѯSTA_IN_02������
     * @param STA_DATE String
     * @return TParm
     * ==============pangben modify 20110523 ����������
     */
    public TParm selectSTA_IN_02(String STA_DATE,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STA_DATE", STA_DATE);
        //==============pangben modify 20110523 start
        parm.setData("REGION_CODE", regionCode);
        //==============pangben modify 20110523 stop
        TParm result = this.query("selectSTA_IN_02", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�STA_IN_02����
     * @param parm TParm
     * @return TParm
     * ============pangben modify 20110526
     */
    public TParm updateSTA_IN_02(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_02", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
    * �޸�sta_in_02���е�����
    * @param parm TParm
    * @param conn TConnection
    * @return TParm
    * =============pangben modify 20110526
    */
   public TParm updateData(TParm parm, TConnection conn) {
       TParm result = new TParm();
       result = updateSTA_IN_02(parm, conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * @param parm TParm
    * @param type String  ����Ҫ��ѯ��SQL�������
    * @return TParm
    */
   public Map getMaxNum(TParm parm, String type) {
       Map map = new HashMap();
       TParm result = this.query(type, parm);
       // �жϴ���ֵ
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return map;
       }
       int maxnum=0;
       String stadate="";
       for(int i=0;i<result.getCount();i++){
    	   int num=result.getInt("NUM", i);
    	   if(maxnum<=num){
    		   maxnum=num;
    		   stadate=result.getValue("STA_DATE", i);
    	   }
       }
       TParm inparm=new TParm();
       inparm.setData("STA_DATE", stadate);
       inparm=STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(inparm);
       for (int i = 0; i < inparm.getCount("DEPT_CODE"); i++) {
           map.put(inparm.getData("DEPT_CODE", i),
        		   inparm.getInt("OUTP_NUM", i)+inparm.getInt("ERD_NUM", i));
       }
       return map;
   }
   /**
    * @param parm TParm
    * @param type String  ����Ҫ��ѯ��SQL�������
    * @return TParm
    */
   public Map getMinNum(TParm parm, String type) {
	   Map map = new HashMap();
       TParm result = this.query(type, parm);
       // �жϴ���ֵ
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return map;
       }
       int mixnum=result.getInt("NUM", 0);
       String stadate="";
       for(int i=0;i<result.getCount();i++){
    	   int num=result.getInt("NUM", i);
    	   if(mixnum>num){
    		   mixnum=num;
    		   stadate=result.getValue("STA_DATE", i);
    	   }
       }
       TParm inparm=new TParm();
       inparm.setData("STA_DATE", stadate);
       inparm=STAOpdDailyTool.getInstance().select_STA_OPD_DAILY(inparm);
       for (int i = 0; i < inparm.getCount("DEPT_CODE"); i++) {
           map.put(inparm.getData("DEPT_CODE", i),
        		   inparm.getInt("OUTP_NUM", i)+inparm.getInt("ERD_NUM", i));
       }
//       System.out.println("getMinNum=========="+map);
       return map;
   }
}
