package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: ������־�м䵵</p>
 *
 * <p>Description: ������־�м䵵</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STAStationDailyTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAStationDailyTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAStationDailyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAStationDailyTool();
        return instanceObject;
    }

    public STAStationDailyTool() {
        setModuleName("sta\\STAStationDailyModule.x");
        onInit();
    }

    /**
     * ��ȡҪ��ѯ�ĸ���Ŀ������
     * @param parm TParm
     * @param type String  ����Ҫ��ѯ��SQL�������
     * @return TParm
     */
    public Map getNum(String STADATE, String type,String regionCode) {
        TParm parm = new TParm();
        parm.setData("STADATE",STADATE);
        //=============pangben modify 20110617 start
        if(null!=regionCode&&regionCode.length()>0)
             parm.setData("REGION_CODE",regionCode);
         //=============pangben modify 20110617 stop
        Map map = new HashMap();
        TParm result = this.query(type, parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return map;
        }
        for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
            //�Կ���code�Ͳ���codeͬʱ��Ϊ ��ֵ
            String key = result.getValue("DEPT_CODE", i)+"_"+result.getValue("STATION_CODE", i);
            map.put(key,result.getValue("O_NUM", i));
        }
        return map;
    }
    /**
     * ����Ժ���м��
     * @param parm ActionParm
     * @return ActionParm
     */
    public TParm insertStation_Daily(TParm parms, TConnection conn) {
        TParm result = new TParm();
        if (parms == null) {
            result.setErr( -1,
                          "��������ΪNULL");
            return result;
        }
        //ѭ����ȡ���� ���б���
        for (int i = 0; i < parms.getCount("STA_DATE"); i++) {
            TParm parm = new TParm();
            parm.setData("STA_DATE", parms.getData("STA_DATE", i));
            parm.setData("DEPT_CODE", parms.getData("DEPT_CODE", i));
            parm.setData("STATION_CODE", parms.getData("STATION_CODE", i));
            parm.setData("ORIGINAL_NUM", parms.getData("ORIGINAL_NUM", i));
            parm.setData("ADM_NUM", parms.getData("ADM_NUM", i));
            parm.setData("FROM_OTHER_DEPT", parms.getData("FROM_OTHER_DEPT", i));
            parm.setData("RECOVER_NUM", parms.getData("RECOVER_NUM", i));
            parm.setData("EFFECT_NUM", parms.getData("EFFECT_NUM", i));
            parm.setData("INVALED_NUM", parms.getData("INVALED_NUM", i));
            parm.setData("DIED_NUM", parms.getData("DIED_NUM", i));
            parm.setData("OTHER_NUM", parms.getData("OTHER_NUM", i));
            parm.setData("TRANS_DEPT_NUM", parms.getData("TRANS_DEPT_NUM", i));
            parm.setData("END_BED_NUM", parms.getData("END_BED_NUM", i));
            parm.setData("REAL_OPEN_BEB_NUM",
                         parms.getData("REAL_OPEN_BEB_NUM", i));
            parm.setData("AVG_OPEB_BED_NUM",
                         parms.getData("AVG_OPEB_BED_NUM", i));
            parm.setData("REAL_OCUU_BED_NUM",
                         parms.getData("REAL_OCUU_BED_NUM", i));
            parm.setData("DS_TOTAL_ADM_DAY",
                         parms.getData("DS_TOTAL_ADM_DAY", i));
            parm.setData("DS_ADM_NUM",
                    parms.getData("DS_ADM_NUM", i));
            parm.setData("OUYCHK_OI_NUM", parms.getData("OUYCHK_OI_NUM", i));
            parm.setData("OUYCHK_RAPA_NUM", parms.getData("OUYCHK_RAPA_NUM", i));
            parm.setData("OUYCHK_INOUT", parms.getData("OUYCHK_INOUT", i));
            parm.setData("OUYCHK_OPBFAF", parms.getData("OUYCHK_OPBFAF", i));
            parm.setData("HEAL_LV_I_CASE", parms.getData("HEAL_LV_I_CASE", i));
            parm.setData("HEAL_LV_BAD", parms.getData("HEAL_LV_BAD", i));
            parm.setData("GET_TIMES", parms.getData("GET_TIMES", i));
            parm.setData("SUCCESS_TIMES", parms.getData("SUCCESS_TIMES", i));
            parm.setData("CARE_NUMS", parms.getData("CARE_NUMS", i));
            parm.setData("RECOVER_RATE", parms.getData("RECOVER_RATE", i));
            parm.setData("EFFECT_RATE", parms.getData("EFFECT_RATE", i));
            parm.setData("DIED_RATE", parms.getData("DIED_RATE", i));
            parm.setData("BED_RETUEN", parms.getData("BED_RETUEN", i));
            parm.setData("BED_WORK_DAY", parms.getData("BED_WORK_DAY", i));
            parm.setData("BED_USE_RATE", parms.getData("BED_USE_RATE", i));
            parm.setData("AVG_ADM_DAY", parms.getData("AVG_ADM_DAY", i));
            parm.setData("DIAG_RATE", parms.getData("DIAG_RATE", i));
            parm.setData("HEAL_LV_BAD_RATE",
                         parms.getData("HEAL_LV_BAD_RATE", i));
            parm.setData("SUCCESS_RATE", parms.getData("SUCCESS_RATE", i));
            parm.setData("CARE_RATE", parms.getData("CARE_RATE", i));
            parm.setData("OUYCHK_RAPA_RATE",
                         parms.getData("OUYCHK_RAPA_RATE", i));
            parm.setData("OUYCHK_OPBFAF_RATE",
                         parms.getData("OUYCHK_OPBFAF_RATE", i));
            parm.setData("VIP_NUM", parms.getData("VIP_NUM", i));// �����ù����� add by wanglong 20140304
            parm.setData("BMP_NUM", parms.getData("BMP_NUM", i));// ����������
            parm.setData("LUP_NUM", parms.getData("LUP_NUM", i));// ��������� add end
            parm.setData("OPT_USER", parms.getData("OPT_USER", i));
            parm.setData("OPT_TERM", parms.getData("OPT_TERM", i));
            //================pangben modify 20110520 start
            parm.setData("REGION_CODE", parms.getData("REGION_CODE", i));
            //ɾ��ԭ������
            result = this.delete_Station_Daily(parms.getValue("STA_DATE", i),parms.getValue("DEPT_CODE", i),parms.getValue("STATION_CODE", i),parms.getValue("REGION_CODE", i), conn);
            //================pangben modify 20110520 stop
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            result = this.update("Insert_Station_Daily", parm, conn);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * ���� Ժ���м䵵 ����
     * @param parm TParm SQL������
     * @param dept TParm �м�����б�
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertSTA_STATION_DATA(TParm parmObj, TConnection conn,String regionCode) {
        TParm parm = parmObj.getParm("SQL"); //SQL����
        TParm dept = parmObj.getParm("DEPT"); //���Ų���
        TParm result = new TParm();
        //�����ֶε����ݼ�
        //------------------------------//����ԭ������ start
        //��ȡǰһ������
        String yesterday = getYesterdayString(parm.getValue("STADATE").toString());
        if (yesterday.equals("")) {
            result.setErr( -1,
                "jdo.sta.StaStationDailyTool.insertSTA_OPD_DAILY=>����ǰһ�����ڴ���");
            return result;
        }
        TParm ORIGINAL_NUM_Parm = new TParm();
        ORIGINAL_NUM_Parm.setData("STADATE", yesterday);
        //=====pangben modify 20110617 ����������
        Map ORIGINAL_NUM = this.getNum(yesterday, "ORIGINAL_NUM",regionCode); //����ԭ������
        //-----------------------------//����ԭ������ end
        //=====pangben modify 20110617 ����������
        Map ADM_NUM = this.getNum(parm.getValue("STADATE"), "ADM_NUM",regionCode); //��Ժ����
//        System.out.println("-=-------ADM_NUM------"+ADM_NUM);
//        System.out.println("ADM_NUM:"+ADM_NUM);
        //=====pangben modify 20110617 ����������
        Map FROM_OTHER_DEPT = this.getNum(parm.getValue("STADATE"), "FROM_OTHER_DEPT",regionCode); //����ת��
//        System.out.println("FROM_OTHER_DEPT:"+FROM_OTHER_DEPT);
        //=====pangben modify 20110617 ����������
        Map RECOVER_NUM = this.getNum(parm.getValue("STADATE"), "RECOVER_NUM",regionCode); //��������
//        System.out.println("RECOVER_NUM:"+RECOVER_NUM);
        //=====pangben modify 20110617 ����������
        Map EFFECT_NUM = this.getNum(parm.getValue("STADATE"), "EFFECT_NUM",regionCode); //��ת����
//        System.out.println("EFFECT_NUM:"+EFFECT_NUM);
        //=====pangben modify 20110617 ����������
        Map INVALED_NUM = this.getNum(parm.getValue("STADATE"), "INVALED_NUM",regionCode); //δ������
//        System.out.println("INVALED_NUM:"+INVALED_NUM);
        //=====pangben modify 20110617 ����������
        Map DIED_NUM = this.getNum(parm.getValue("STADATE"), "DIED_NUM",regionCode); //��������
//        System.out.println("DIED_NUM:"+DIED_NUM);
        //=====pangben modify 20110617 ����������
        Map OTHER_NUM = this.getNum(parm.getValue("STADATE"), "OTHER_NUM",regionCode); //��������
//        System.out.println("OTHER_NUM:"+OTHER_NUM);
        //=====pangben modify 20110617 ����������
        Map TRANS_DEPT_NUM = this.getNum(parm.getValue("STADATE"), "TRANS_DEPT_NUM",regionCode); //ת����������
//        System.out.println("TRANS_DEPT_NUM:"+TRANS_DEPT_NUM);
        //=====pangben modify 20110617 ����������
        Map END_BED_NUM = this.getNum(parm.getValue("STADATE"), "END_BED_NUM",""); //��ĩʵ�в�����
        //=====pangben modify 20110617 ����������
        Map REAL_OPEN_BEB_NUM = this.getNum(parm.getValue("STADATE"), "END_BED_NUM",""); //////////////ʵ�ʿ����ܴ�����  //��ʱ���� END_BED_NUM
        //=====pangben modify 20110617 ����������
        Map DS_TOTAL_ADM_DAY = this.getNum(parm.getValue("STADATE"), "DS_TOTAL_ADM_DAY",regionCode); //��Ժ��סԺ����
        
        Map DS_ADM_NUM = this.getNum(parm.getValue("STADATE"), "DS_ADM_NUM",regionCode); //��Ժ����
        //=====pangben modify 20110617 ����������
        Map OUYCHK_OI_NUM = this.getNum(parm.getValue("STADATE"), "OUYCHK_OI_NUM",regionCode); //������Ϸ�����
        //=====pangben modify 20110617 ����������
        Map OUYCHK_RAPA_NUM = this.getNum(parm.getValue("STADATE"), "OUYCHK_RAPA_NUM",regionCode); //������Ϸ�����
        Map OUYCHK_INOUT = this.getNum(parm.getValue("STADATE"), "OUYCHK_INOUT",regionCode); //��Ժ��Ϸ�����
        Map OUYCHK_OPBFAF = this.getNum(parm.getValue("STADATE"), "OUYCHK_OPBFAF",regionCode); //��ǰ������Ϸ�����
        Map HEAL_LV_I_CASE = this.getNum(parm.getValue("STADATE"), "HEAL_LV_I_CASE",regionCode); //�޾��п�������
        Map HEAL_LV_BAD = this.getNum(parm.getValue("STADATE"), "HEAL_LV_BAD",regionCode); //�޾��пڻ�ŧ��
        Map GET_TIMES = this.getNum(parm.getValue("STADATE"), "GET_TIMES",regionCode); //Σ�ز������ȴ���
        Map SUCCESS_TIMES = this.getNum(parm.getValue("STADATE"), "SUCCESS_TIMES",regionCode); //Σ�ز������ȳɹ�
        Map CARE_NUMS = this.getNum(parm.getValue("STADATE"), "CARE_NUMS",regionCode); //Σ�ز������ȳɹ�
        Map AVG_ADM_DAY = this.getNum(parm.getValue("STADATE"), "AVG_ADM_DAY",regionCode); //����ƽ��סԺ��
        Map RAPA_NUM = this.getNum(parm.getValue("STADATE"), "RAPA_NUM",regionCode); //���������
        Map OPBFAF = this.getNum(parm.getValue("STADATE"), "OPBFAF",regionCode); //��ǰ���������
        Map VIP_NUM = this.getNum(parm.getValue("STADATE"), "VIP_NUM",regionCode); //�����ù����� add by wanglong 20140304
        Map BMP_NUM = this.getNum(parm.getValue("STADATE"), "BMP_NUM",regionCode); //���������� 
        Map LUP_NUM = this.getNum(parm.getValue("STADATE"), "LUP_NUM",regionCode); //��������� add end
        int i_ORIGINAL_NUM; //����ԭ������
        int i_ADM_NUM; //��Ժ����
        int i_FROM_OTHER_DEPT; //����ת��
        int i_RECOVER_NUM; //��������
        int i_EFFECT_NUM; //��ת����
        int i_INVALED_NUM; //δ������
        int i_DIED_NUM; //��������
        int i_OTHER_NUM; //��������
        int i_TRANS_DEPT_NUM; //ת����������
        int i_END_BED_NUM; //��ĩʵ�в�����
        int i_REAL_OPEN_BEB_NUM; //ʵ�ʿ����ܴ�����
        int i_AVG_OPEB_BED_NUM; //ƽ�����Ų�����
        int i_REAL_OCUU_BED_NUM; //ʵ��ռ���ܴ���
        int i_DS_TOTAL_ADM_DAY; //��Ժ��סԺ����
        int i_DS_ADM_NUM;//��Ժ����
        int i_OUYCHK_OI_NUM; //������Ϸ�����
        int i_OUYCHK_RAPA_NUM; //������Ϸ�����
        int i_OUYCHK_INOUT; //��Ժ��Ϸ�����
        int i_OUYCHK_OPBFAF; //��ǰ������Ϸ�����
        int i_HEAL_LV_I_CASE; //�޾��п�������
        int i_HEAL_LV_BAD; //�޾��пڻ�ŧ��
        int i_GET_TIMES; //Σ�ز������ȴ���
        int i_SUCCESS_TIMES; //Σ�ز������ȳɹ�
        int i_CARE_NUMS; //������
        double i_RECOVER_RATE; //������
        double i_EFFECT_RATE; //��ת��
        double i_DIED_RATE; //������
        double i_BED_RETUEN; //������ת
        int i_BED_WORK_DAY; //����������
        double i_BED_USE_RATE; //����ʹ����
        double i_AVG_ADM_DAY; //����ƽ��סԺ��
        double i_DIAG_RATE; //��Ϸ�����%
        double i_HEAL_LV_BAD_RATE; //�޾��пڻ�ŧ��
        double i_SUCCESS_RATE; //Σ�ز������ȳɹ���
        double i_CARE_RATE; //������
        double i_OUYCHK_RAPA_RATE; //������Ϸ�����%
        double i_OUYCHK_OPBFAF_RATE; //��ǰ������Ϸ�����
        int i_RAPA_NUM; //���������
        int i_OPBFAF; //��ǰ���������
        int i_VIP_NUM; // �����ù����� add by wanglong 20140304
        int i_BMP_NUM; // ����������
        int i_LUP_NUM;// ��������� add end
        
        TParm data = new TParm(); //�洢��������
        //ѭ������ ����Ӧ���ҵ����ݴ洢
        for (int i = 0; i < dept.getCount("DEPT_CODE"); i++) {
            //�м����Ҵ���
            String dept_code = dept.getValue("DEPT_CODE", i);
            //��סԺ���Ҵ���Ͳ���������Ϊ����ֵ
            String Dc = dept.getValue("IPD_DEPT_CODE", i)+"_"+dept.getValue("STATION_CODE", i);
            //����ԭ�в�����  Ҫ���м��Ŀ��Ҵ��� ע��Ҫ���»���
            i_ORIGINAL_NUM = ORIGINAL_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(ORIGINAL_NUM.get(Dc).toString());
            i_ADM_NUM = ADM_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(ADM_NUM.get(Dc).toString());
            i_FROM_OTHER_DEPT = FROM_OTHER_DEPT.get(Dc) == null ? 0 :
                Integer.valueOf(FROM_OTHER_DEPT.get(Dc).toString());
            //��������
            i_RECOVER_NUM = RECOVER_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(RECOVER_NUM.get(Dc).toString());
            //��ת����
            i_EFFECT_NUM = EFFECT_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(EFFECT_NUM.get(Dc).toString());
             //δ������
            i_INVALED_NUM = INVALED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(INVALED_NUM.get(Dc).toString());
            //��������
            i_DIED_NUM = DIED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(DIED_NUM.get(Dc).toString());
            //��������
            i_OTHER_NUM = OTHER_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OTHER_NUM.get(Dc).toString());
            //ת����������
            i_TRANS_DEPT_NUM = TRANS_DEPT_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(TRANS_DEPT_NUM.get(Dc).toString());
            //��ĩʵ�в�����
            i_END_BED_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //ʵ�ʿ����ܴ�����(����END_BED_NUM)
            i_REAL_OPEN_BEB_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //ƽ�����Ų�����(����END_BED_NUM)
            i_AVG_OPEB_BED_NUM = END_BED_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(END_BED_NUM.get(Dc).toString());
            //ʵ��ռ���ܴ���  ����ԭ������+��Ժ����+����ת��-��RECOVER_NUM + EFFECT_NUM+ INVALED_NUM+ OTHER_NUM+ TRANS_DEPT_NUM��
            i_REAL_OCUU_BED_NUM = i_ORIGINAL_NUM + i_ADM_NUM +
                i_FROM_OTHER_DEPT -
                (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_OTHER_NUM +
                 i_TRANS_DEPT_NUM);
            //��Ժ��סԺ����
            i_DS_TOTAL_ADM_DAY = DS_TOTAL_ADM_DAY.get(Dc) == null ? 0 :
                Integer.valueOf(DS_TOTAL_ADM_DAY.get(Dc).toString());
            //��Ժ����
            i_DS_ADM_NUM=DS_ADM_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(DS_ADM_NUM.get(Dc).toString());
            //������Ϸ�����
            i_OUYCHK_OI_NUM = OUYCHK_OI_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_OI_NUM.get(Dc).toString());
            //������Ϸ�����
            i_OUYCHK_RAPA_NUM = OUYCHK_RAPA_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_RAPA_NUM.get(Dc).toString());
            //��Ժ��Ϸ�����
            i_OUYCHK_INOUT = OUYCHK_INOUT.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_INOUT.get(Dc).toString());
            //��ǰ������Ϸ�����
            i_OUYCHK_OPBFAF = OUYCHK_OPBFAF.get(Dc) == null ? 0 :
                Integer.valueOf(OUYCHK_OPBFAF.get(Dc).toString());
            //�޾��п�������
            i_HEAL_LV_I_CASE = HEAL_LV_I_CASE.get(Dc) == null ? 0 :
                Integer.valueOf(HEAL_LV_I_CASE.get(Dc).toString());
            //�޾��пڻ�ŧ��
            i_HEAL_LV_BAD = HEAL_LV_BAD.get(Dc) == null ? 0 :
                Integer.valueOf(HEAL_LV_BAD.get(Dc).toString());
            //Σ�ز������ȴ���
            i_GET_TIMES = GET_TIMES.get(Dc) == null ? 0 :
                Integer.valueOf(GET_TIMES.get(Dc).toString());
            //Σ�ز������ȳɹ�
            i_SUCCESS_TIMES = SUCCESS_TIMES.get(Dc) == null ? 0 :
                Integer.valueOf(SUCCESS_TIMES.get(Dc).toString());
            //�㻤����
            i_CARE_NUMS = CARE_NUMS.get(Dc) == null ? 0 :
                Integer.valueOf(CARE_NUMS.get(Dc).toString()); 
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0) //������
                i_RECOVER_RATE = (double) i_RECOVER_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_RECOVER_RATE = 0;
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0)//��ת��
                i_EFFECT_RATE = (double) i_EFFECT_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_EFFECT_RATE = 0;
            if ( (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM + i_DIED_NUM +
                  i_OTHER_NUM) != 0)//������
                i_DIED_RATE = (double) i_DIED_NUM /
                    (double) (i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                              i_DIED_NUM + i_OTHER_NUM);
            else
                i_DIED_RATE = 0;
            if (i_AVG_OPEB_BED_NUM != 0)//������ת  ���ü�����ת�εķ���
                i_BED_RETUEN = STAWorkLogTool.getInstance().countBedReturn( (
                    i_RECOVER_NUM + i_EFFECT_NUM + i_INVALED_NUM +
                    i_DIED_NUM + i_OTHER_NUM), i_AVG_OPEB_BED_NUM);
            else
                i_BED_RETUEN = 0;
            i_BED_WORK_DAY = i_END_BED_NUM;//����������  ÿ��ÿ��Ϊ1�������գ�ÿ�쵼��Ĺ�����Ӧ�õ�����ĩʵ�в�����<17>--add by zhangk 2009-7-23
//            System.out.println("-=------i_END_BED_NUM----------------"+i_END_BED_NUM);
//            System.out.println("-=------i_REAL_OCUU_BED_NUM----------------"+i_REAL_OCUU_BED_NUM);
            if(i_END_BED_NUM!=0)
                i_BED_USE_RATE = (double)i_REAL_OCUU_BED_NUM/(double)i_END_BED_NUM*100; //����ʹ����=ʵ��ռ�ò�����/���Ų�����
            else
                i_BED_USE_RATE = 0;
            i_AVG_ADM_DAY = AVG_ADM_DAY.get(Dc) == null ? 0 :
                StringTool.round(Double.valueOf(AVG_ADM_DAY.get(Dc).toString()), 2); //����ƽ��סԺ��
            i_DIAG_RATE = 0; //��Ϸ�����%  ���������Ҫѯ��
            if (i_HEAL_LV_I_CASE != 0) //�޾��пڻ�ŧ��  <27>�޾��пڻ�ŧ��/<26>�޾��п�������
                i_HEAL_LV_BAD_RATE = (double) i_HEAL_LV_BAD /
                    (double) i_HEAL_LV_I_CASE;
            else
                i_HEAL_LV_BAD_RATE = 0;
            if (i_GET_TIMES != 0) //Σ�ز������ȳɹ���  <29>Σ�ز������ȳɹ�/<28>Σ�ز���������
                i_SUCCESS_RATE = (double) i_SUCCESS_TIMES /
                    (double) i_GET_TIMES;
            else
                i_SUCCESS_RATE = 0;
            if (i_ADM_NUM != 0) //������
            	i_CARE_RATE = (double) i_CARE_NUMS /
                    (double) i_ADM_NUM;
            else
            	i_CARE_RATE = 0;//������   ���������Ҫѯ��
            i_RAPA_NUM = RAPA_NUM.get(Dc) == null ? 0 :
                Integer.valueOf(RAPA_NUM.get(Dc).toString());
            i_OPBFAF = OPBFAF.get(Dc) == null ? 0 :
                Integer.valueOf(OPBFAF.get(Dc).toString());
            if (i_RAPA_NUM != 0) //������Ϸ�����%
                i_OUYCHK_RAPA_RATE = (double) i_OUYCHK_RAPA_NUM /
                    (double) i_RAPA_NUM;
            else
                i_OUYCHK_RAPA_RATE = 0;
            if (i_OPBFAF != 0)
                i_OUYCHK_OPBFAF_RATE = (double) i_OUYCHK_OPBFAF /
                    (double) i_OPBFAF; //��ǰ������Ϸ�����
            else
                i_OUYCHK_OPBFAF_RATE = 0;
            i_VIP_NUM = VIP_NUM.get(Dc) == null ? 0 : Integer.valueOf(VIP_NUM.get(Dc).toString()); // �����ù����� add by wanglong 20140304
            i_BMP_NUM = BMP_NUM.get(Dc) == null ? 0 : Integer.valueOf(BMP_NUM.get(Dc).toString()); // ����������
            i_LUP_NUM = LUP_NUM.get(Dc) == null ? 0 : Integer.valueOf(LUP_NUM.get(Dc).toString()); // ��������� add end
            data.addData("STA_DATE", parm.getValue("STADATE").toString());
            data.addData("DEPT_CODE", dept.getValue("DEPT_CODE", i));
            data.addData("STATION_CODE", dept.getValue("STATION_CODE", i));//����CODE
            data.addData("ORIGINAL_NUM", i_ORIGINAL_NUM); //����ʵ�в�����
            data.addData("ADM_NUM", i_ADM_NUM);
            data.addData("FROM_OTHER_DEPT", i_FROM_OTHER_DEPT);
            data.addData("RECOVER_NUM", i_RECOVER_NUM);
            data.addData("EFFECT_NUM", i_EFFECT_NUM);
            data.addData("INVALED_NUM", i_INVALED_NUM);
            data.addData("DIED_NUM", i_DIED_NUM);
            data.addData("OTHER_NUM", i_OTHER_NUM);
            data.addData("TRANS_DEPT_NUM", i_TRANS_DEPT_NUM);
            data.addData("END_BED_NUM", i_END_BED_NUM);
            data.addData("REAL_OPEN_BEB_NUM", i_REAL_OPEN_BEB_NUM); //��ʱ���� END_BED_NUM
            data.addData("AVG_OPEB_BED_NUM", i_AVG_OPEB_BED_NUM); //��ʱ���� END_BED_NUM
            data.addData("REAL_OCUU_BED_NUM", i_REAL_OCUU_BED_NUM);
            data.addData("DS_TOTAL_ADM_DAY", i_DS_TOTAL_ADM_DAY);
            data.addData("DS_ADM_NUM", i_DS_ADM_NUM);
            data.addData("OUYCHK_OI_NUM", i_OUYCHK_OI_NUM);
            data.addData("OUYCHK_RAPA_NUM", i_OUYCHK_RAPA_NUM);
            data.addData("OUYCHK_INOUT", i_OUYCHK_INOUT);
            data.addData("OUYCHK_OPBFAF", i_OUYCHK_OPBFAF);
            data.addData("HEAL_LV_I_CASE", i_HEAL_LV_I_CASE);
            data.addData("HEAL_LV_BAD", i_HEAL_LV_BAD);
            data.addData("GET_TIMES", i_GET_TIMES);
            data.addData("SUCCESS_TIMES", i_SUCCESS_TIMES);
            data.addData("CARE_NUMS", i_CARE_NUMS); //��ʿ�����ձ�������¼��
            data.addData("RECOVER_RATE", StringTool.round(i_RECOVER_RATE,2)); //RECOVER_NUM/RECOVER_NUM + EFFECT_NUM +INVALED_NUM + DIED_NUM + OTHER_NUM
            data.addData("EFFECT_RATE", StringTool.round(i_EFFECT_RATE,2));
            data.addData("DIED_RATE", StringTool.round(i_DIED_RATE,2));
            data.addData("BED_RETUEN",  StringTool.round(i_BED_RETUEN,2));
            data.addData("BED_WORK_DAY", i_BED_WORK_DAY);
            data.addData("BED_USE_RATE",  StringTool.round(i_BED_USE_RATE,2));
            data.addData("AVG_ADM_DAY", StringTool.round(i_AVG_ADM_DAY,2));
            data.addData("DIAG_RATE", StringTool.round(i_DIAG_RATE,2));
            data.addData("HEAL_LV_BAD_RATE", StringTool.round(i_HEAL_LV_BAD_RATE,2));
            data.addData("SUCCESS_RATE", StringTool.round(i_SUCCESS_RATE,2));
            data.addData("CARE_RATE", StringTool.round(i_CARE_RATE,2));
            data.addData("OUYCHK_RAPA_RATE", StringTool.round(i_OUYCHK_RAPA_RATE,2));
            data.addData("OUYCHK_OPBFAF_RATE", i_OUYCHK_OPBFAF_RATE);
            data.addData("VIP_NUM", i_VIP_NUM); // �����ù����� add by wanglong 20140304
            data.addData("BMP_NUM", i_BMP_NUM); // ����������
            data.addData("LUP_NUM", i_LUP_NUM); // ��������� add end
            data.addData("OPT_USER", parm.getValue("OPT_USER"));
            data.addData("OPT_TERM", parm.getValue("OPT_TERM"));
            //===========pangben modify 20110520 start
            data.addData("REGION_CODE", parm.getValue("REGION_CODE"));
            //===========pangben modify 20110520 stop
        }
//    System.out.println("data====>"+data);
        //��������
        result = this.insertStation_Daily(data, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ȡǰһ��������ַ���
     * @param date String  ��ʽ YYYYMMDD
     * @return String
     */
    private String getYesterdayString(String date) {
        String yesterday = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date d;
        try {
            d = df.parse(date);
            Calendar ctest = Calendar.getInstance();
            ctest.setTime(d);
            ctest.add(Calendar.DATE, -1);
            d = ctest.getTime();
            yesterday = df.format(d);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return yesterday;
    }
    /**
     * ��ѯ��¼��Ա��������
     * @param userID String
     * @return String
     */
    private String getOPTDeptCode(String userID){
        String DeptCode="";
        return DeptCode;
    }
    /**
     * ɾ�������м䵵��Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delete_Station_Daily(TParm parm,TConnection conn){
        TParm result = this.update("delete_Station_Daily",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ�������м䵵��Ϣ
     * @param parm TParm
     * @param regionCode ����
     * @param conn TConnection
     * @return TParm
     * ========pangben modify 20110520 ����������
     */
    public TParm delete_Station_Daily(String STA_DATE,String Dept_code,String Station_code,String regionCode,TConnection conn){
        TParm parm = new TParm();
        parm.setData("STA_DATE",STA_DATE);
        parm.setData("DEPT_CODE",Dept_code);
        parm.setData("STATION_CODE",Station_code);
        //========pangben modify 20110520 start
        parm.setData("REGION_CODE",regionCode);
        //========pangben modify 20110520 stop
        TParm result = this.update("delete_Station_Daily",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���벡���м����Ϣ
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL"); //SQL����
//        TParm dept = parmObj.getParm("DEPT"); //���Ų���
        TParm result = new TParm();
//        //��ɾ��ԭ������
//        for(int i=0;i<dept.getCount("DEPT_CODE");i++){
//            result = this.delete_Station_Daily(parm.getValue("STADATE"),dept.getValue("DEPT_CODE",i), conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        result = insertSTA_STATION_DATA(parmObj,conn,parm.getValue("REGION_CODE"));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �Զ����ε�����Ϣ��
     * @param p TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm batchData(TParm p,TConnection conn) {
        //��ȡ���������
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),-1);
        String yestodate = StringTool.getString(time,"yyyyMMdd");
        TParm parm = new TParm();//���ڲ���
        parm.setData("STADATE",yestodate);
        TParm dept = new TParm();//�����б�  ��ȡ�м���ղ����е����ﲿ��
        dept = STADeptListTool.getInstance().selectIPD_DEPT(p);
        TParm parmObj = new TParm();//�ܲ���
        parmObj.setData("SQL",parm.getData());
        parmObj.setData("DEPT",dept.getData());
        TParm result = new TParm();
//        //��ɾ��ԭ������
//        TParm delParm = new TParm();
//        delParm.setData("STA_DATE",parm.getValue("STADATE"));
//        result = this.delete_Station_Daily(delParm,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        result = insertSTA_STATION_DATA(parmObj,conn,"");
        if (result.getErrCode() < 0) {
            err("STAסԺ����ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ STA_STATION_DAILY �����м䵵����
     * @param parm TParm
     * @return TParm
     */
    public TParm select_Station_Daily(TParm parm){
        TParm result = this.query("select_Station_Daily",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�ʵ�в�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateREAL_OCUU_BED_NUM(TParm parm,TConnection conn){
        TParm result = this.update("updateREAL_OCUU_BED_NUM",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
