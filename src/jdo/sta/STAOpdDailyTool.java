package jdo.sta;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: �ż����м䵵</p>
 *
 * <p>Description: �ż����м䵵</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STAOpdDailyTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAOpdDailyTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAOpdDailyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAOpdDailyTool();
        return instanceObject;
    }

    public STAOpdDailyTool() {
        setModuleName("sta\\STAOpdDailyModule.x");
        onInit();
    }

    /**
     * ��ȡҪ��ѯ�ĸ���Ŀ������
     * @param parm TParm
     * @param type String  ����Ҫ��ѯ��SQL�������
     * @return TParm
     */
    public Map getNum(String ADMDATE, String type) {
        TParm parm = new TParm();
        parm.setData("ADMDATE",ADMDATE);
        Map map = new HashMap();
        TParm result = this.query(type, parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return map;
        }
        
        for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
            map.put(result.getData("DEPT_CODE", i),
                    result.getData("O_NUM", i));
        }
        return map;
    }

    /**
     * �����м��
     * @param parm ActionParm
     * @return ActionParm
     */
    public TParm insertSTA_OPD_DAILY(TParm parms, TConnection conn) {
        TParm parm;
        TParm result = new TParm();
        if (parms == null) {
            result.setErr( -1, "��������ΪNULL");
            return result;
        }
        //ѭ����ȡ��������SQL��� ���б���
        for (int i = 0; i < parms.getCount("STA_DATE"); i++) {
            parm = new TParm();
            parm.setData("STA_DATE", parms.getData("STA_DATE", i));
            parm.setData("DEPT_CODE", parms.getData("DEPT_CODE", i));
            parm.setData("OUTP_NUM", parms.getData("OUTP_NUM", i));
            parm.setData("ERD_NUM", parms.getData("ERD_NUM", i));
            parm.setData("HRM_NUM", parms.getData("HRM_NUM", i));
            parm.setData("OTHER_NUM", parms.getData("OTHER_NUM", i));
            parm.setData("GET_TIMES", parms.getData("GET_TIMES", i));
            parm.setData("PROF_DR", parms.getData("PROF_DR", i));
            parm.setData("COMM_DR", parms.getData("COMM_DR", i));
            parm.setData("DR_HOURS", parms.getData("DR_HOURS", i));
            parm.setData("SUCCESS_TIMES", parms.getData("SUCCESS_TIMES", i));
            parm.setData("OBS_NUM", parms.getData("OBS_NUM", i));
            parm.setData("ERD_DIED_NUM", parms.getData("ERD_DIED_NUM", i));
            parm.setData("OBS_DIED_NUM", parms.getData("OBS_DIED_NUM", i));
            parm.setData("OPE_NUM", parms.getData("OPE_NUM", i));
            parm.setData("FIRST_NUM", parms.getData("FIRST_NUM", i));
            parm.setData("FURTHER_NUM", parms.getData("FURTHER_NUM", i));
            parm.setData("APPT_NUM", parms.getData("APPT_NUM", i));
            parm.setData("ZR_DR_NUM", parms.getData("ZR_DR_NUM", i));
            parm.setData("ZZ_DR_NUM", parms.getData("ZZ_DR_NUM", i));
            parm.setData("ZY_DR_NUM", parms.getData("ZY_DR_NUM", i));
            parm.setData("ZX_DR_NUM", parms.getData("ZX_DR_NUM", i));
            parm.setData("OPT_USER", parms.getData("OPT_USER", i));
            parm.setData("OPT_TERM", parms.getData("OPT_TERM", i));
            //=============pangben modify 20110519
            parm.setData("REGION_CODE", parms.getData("REGION_CODE", i));
            parm.setData("CONFIRM_FLG","N");//2009-7-7�����ύ״̬�ֶ� Ĭ��Ϊ��N��
            
            //ɾ��ԭ������
            result = this.deleteSTA_OPD_DATA(parms.getValue("STA_DATE", i),parms.getValue("DEPT_CODE", i),parms.getValue("REGION_CODE", i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName()+"������"+i+"  ���ڲ��ţ�"+parms.getData("STA_DATE", i)+" "+parms.getData("DEPT_CODE", i));
                return result;
            }
            
            result = this.update("Insert_STA_OPD_DAILY", parm, conn);
            // �жϴ���ֵ
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName()+"������"+i+"  ���ڲ��ţ�"+parms.getData("STA_DATE", i)+" "+parms.getData("DEPT_CODE", i));
                return result;
            }
        }
        return result;
    }

    /**
     * ���� �ż����м䵵 ����
     * @param parm TParm  sql������
     * @param dept TParm  �����б�
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertSTA_OPD_DATA(TParm parmObj, TConnection conn) {
        TParm parm = parmObj.getParm("SQL");//SQL����
        TParm dept = parmObj.getParm("DEPT");//���Ų���
        Map OUTP_NUM = this.getNum(parm.getValue("ADMDATE"), "OUTP_NUM"); //��������
        Map ERD_NUM = this.getNum(parm.getValue("ADMDATE"), "ERD_NUM"); //��������
        Map HRM_NUM = this.getNum(parm.getValue("ADMDATE"), "HRM_NUM"); //�����������
        Map GET_TIMES = this.getNum(parm.getValue("ADMDATE"), "GET_TIMES"); //�����˴�
        Map PROF_DR = this.getNum(parm.getValue("ADMDATE"), "PROF_DR"); //ר����<���θ�����>�˴�
        Map COMM_DR = this.getNum(parm.getValue("ADMDATE"), "COMM_DR"); //��ͨ��
        Map DR_HOURS_4 = this.getNum(parm.getValue("ADMDATE"), "DR_HOURS_4"); //����Сʱ(������)
        Map DR_HOURS_8 = this.getNum(parm.getValue("ADMDATE"), "DR_HOURS_8"); //����Сʱ(ȫ����)
//        Map DEATH_NUM = this.getNum(parm.getValue("ADMDATE"), "DEATH_NUM"); //�������� ���ڼ������ȳɹ�����
        Map DEATH_NUM = new HashMap();//Ŀǰϵͳ��û�й������������ļ�¼
        Map OBS_NUM = this.getNum(parm.getValue("ADMDATE"), "OBS_NUM"); //��������
//        Map ERD_DIED_NUM = this.getNum(parm.getValue("ADMDATE"), "ERD_DIED_NUM"); //������������
        Map ERD_DIED_NUM = new HashMap();//Ŀǰϵͳ��û�й������������ļ�¼
//        Map OBS_DIED_NUM = this.getNum(parm.getValue("ADMDATE"), "OBS_DIED_NUM"); //������������
        Map FIRST_NUM = this.getNum(parm.getValue("ADMDATE"), "FIRST_NUM"); //��������
        Map FURTHER_NUM = this.getNum(parm.getValue("ADMDATE"), "FURTHER_NUM"); //��������
        Map APPT_NUM = this.getNum(parm.getValue("ADMDATE"), "APPT_NUM"); //ԤԼ����
        Map ZR_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZR_DR_NUM"); //���θ�����ҽʦ����
        Map ZZ_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZZ_DR_NUM"); //����ҽʦ����
        Map ZY_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZY_DR_NUM"); //סԺҽʦ����
        //Map ZX_DR_NUM = this.getNum(parm.getValue("ADMDATE"), "ZX_DR_NUM"); //����ҽʦ����
        Map OPE_NUM = this.getNum(parm.getValue("ADMDATE"), "OPE_NUM"); //��������
        Map OBS_DIED_NUM = new HashMap();
        TParm data = new TParm();
        //ѭ������ ����Ӧ���ҵ����ݴ洢
        for (int i = 0; i < dept.getCount("DEPT_CODE"); i++) {
            String Dc = dept.getValue("OE_DEPT_CODE", i); //�����ż������
            data.addData("STA_DATE", parm.getValue("ADMDATE")); //ͳ������
            data.addData("DEPT_CODE", dept.getValue("DEPT_CODE", i)); //���Ŵ���
            data.addData("OUTP_NUM",
                         OUTP_NUM.get(Dc) == null ? 0 : OUTP_NUM.get(Dc));
            data.addData("ERD_NUM",
                         ERD_NUM.get(Dc) == null ? 0 : ERD_NUM.get(Dc));
            data.addData("HRM_NUM",
                         HRM_NUM.get(Dc) == null ? 0 : HRM_NUM.get(Dc));
            data.addData("OTHER_NUM", 0); //����   --��ʱû�м���
            data.addData("GET_TIMES",
                         GET_TIMES.get(Dc) == null ? 0 : GET_TIMES.get(Dc));
            data.addData("PROF_DR",
                         PROF_DR.get(Dc) == null ? 0 : PROF_DR.get(Dc));
            data.addData("COMM_DR",
                         COMM_DR.get(Dc) == null ? 0 : COMM_DR.get(Dc));
            int n1 = 0; //������ Сʱ��
            int n2 = 0; //ȫ���� Сʱ��
            if (DR_HOURS_4.get(Dc) != null)
                n1 = Integer.valueOf(DR_HOURS_4.get(Dc).toString());
            if (DR_HOURS_8.get(Dc) != null)
                n2 = Integer.valueOf(DR_HOURS_8.get(Dc).toString());
            data.addData("DR_HOURS", n1 + n2); //����Сʱ��
            n1 = 0; //������
            n2 = 0; //������
            if (DEATH_NUM.get(Dc) != null)
                n1 = Integer.valueOf(DEATH_NUM.get(Dc).toString()); //��������ϵͳ�޼�¼
            if (GET_TIMES.get(Dc) != null)
                n2 = Integer.valueOf(GET_TIMES.get(Dc).toString());
            data.addData("SUCCESS_TIMES", n2 - n1); //���ȳɹ��˴� = ���ȴ���-������
            data.addData("OBS_NUM",
                         OBS_NUM.get(Dc) == null ? 0 : OBS_NUM.get(Dc));//��������
            data.addData("ERD_DIED_NUM",
                         ERD_DIED_NUM.get(Dc) == null ? 0 : ERD_DIED_NUM.get(Dc));//������������ ����ϵͳ�޼�¼
            data.addData("OBS_DIED_NUM",
                         OBS_DIED_NUM.get(Dc) == null ? 0 : OBS_DIED_NUM.get(Dc));//������������ ����ϵͳ�޼�¼
           
            data.addData("OPE_NUM", OPE_NUM.get(Dc) == null ? 0 : OPE_NUM.get(Dc));//��������
            //----shibl 20120516 modify 
            data.addData("FIRST_NUM", FIRST_NUM.get(Dc) == null ? 0 : FIRST_NUM.get(Dc));//��������
            data.addData("FURTHER_NUM", FURTHER_NUM.get(Dc) == null ? 0 : FURTHER_NUM.get(Dc));//��������
            data.addData("APPT_NUM", APPT_NUM.get(Dc) == null ? 0 : APPT_NUM.get(Dc));//ԤԼ����
            data.addData("ZR_DR_NUM", ZR_DR_NUM.get(Dc) == null ? 0 : ZR_DR_NUM.get(Dc));//���θ�����
            data.addData("ZZ_DR_NUM", ZZ_DR_NUM.get(Dc) == null ? 0 : ZZ_DR_NUM.get(Dc));//����
            data.addData("ZY_DR_NUM", ZY_DR_NUM.get(Dc) == null ? 0 : ZY_DR_NUM.get(Dc));//סԺ
            //data.addData("ZX_DR_NUM", ZX_DR_NUM.get(Dc) == null ? 0 : ZX_DR_NUM.get(Dc));//����
            data.addData("ZX_DR_NUM",0);
            data.addData("OPT_USER", parm.getValue("OPT_USER")); //��ʱ���� �ֶηǿ�
            data.addData("OPT_TERM", parm.getValue("OPT_TERM")); //��ʱ���� �ֶηǿ�
            //===========pangben modify 20110519
            data.addData("REGION_CODE", parm.getValue("REGION_CODE")); //��ʱ���� �ֶηǿ�
        }
        //��������
        TParm result = this.insertSTA_OPD_DAILY(data, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    public TParm ceshi(TParm parm, String type) {
        TParm result = this.query(type, parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ���������ڵ�����
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteSTA_OPD_DATA(TParm parmObj, TConnection conn){
        TParm result = this.update("delete_STA_OPD_DAILY",parmObj,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ���������ڵ�����
     * @param parmObj TParm
     * @param conn TConnection
     * @param regionCode ����
     * @return TParm
     * =========pangben modify 20110520 ����������
     */
    public TParm deleteSTA_OPD_DATA(String STA_DATE,String Dept_code,String regionCode, TConnection conn){
        TParm parm = new TParm();
        parm.setData("ADMDATE",STA_DATE);
        parm.setData("DEPT_CODE",Dept_code);
        //=========pangben modify 20110520 start
        parm.setData("REGION_CODE",regionCode);
        TParm result = this.update("delete_STA_OPD_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���������м����Ϣ
     * @param parmObj TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertData(TParm parmObj, TConnection conn){
        TParm result = new TParm();
//        //ɾ��ԭ����Ϣ
//        TParm parm = parmObj.getParm("SQL");//ɾ������
//        TParm dept = parmObj.getParm("DEPT"); //���Ų���
//        for(int i=0;i<dept.getCount("DEPT_CODE");i++){
//            result = this.deleteSTA_OPD_DATA(parm.getValue("ADMDATE"),dept.getValue("DEPT_CODE",i), conn);
//            if (result.getErrCode() < 0) {
//                err("ERR:" + result.getErrCode() + result.getErrText() +
//                    result.getErrName());
//                return result;
//            }
//        }
        //����������
        result = this.insertSTA_OPD_DATA(parmObj,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �Զ����ε������ݵ��õķ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * ============pangben modify 20110519 �˷�����ʱû���õ��������Ҫ�����ڵ�һ���������������ֵ
     */
    public TParm batchData(TParm p,TConnection conn){
        //��ȡ���������
        Timestamp time = StringTool.rollDate(SystemTool.getInstance().getDate(),-1);
        String yestodate = StringTool.getString(time,"yyyyMMdd");
        TParm parm = new TParm();//���ڲ���
        parm.setData("ADMDATE",yestodate);
        TParm dept = new TParm();//�����б�  ��ȡ�м���ղ����е�סԺ����
        dept = STADeptListTool.getInstance().selectOE_DEPT(p.getValue("REGION_CODE"));//========pangben modify 20110519 ��Ӳ���
        TParm parmObj = new TParm();//�ܲ���
        parmObj.setData("SQL",parm.getData());
        parmObj.setData("DEPT",dept.getData());

        TParm result = new TParm();
//        //ɾ��ԭ����Ϣ
//        result = this.deleteSTA_OPD_DATA(parm,conn);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            return result;
//        }
        //����������
        result = this.insertSTA_OPD_DATA(parmObj,conn);
        if (result.getErrCode() < 0) {
            err("STA��������ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯSTA_OPD_DAILY �������м��������
     * @param parm TParm
     * @return TParm
     */
    public TParm select_STA_OPD_DAILY(TParm parm){
        TParm result = this.query("select_STA_OPD_DAILY",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   
}
