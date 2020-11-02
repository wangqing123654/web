package jdo.sta;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ������־</p>
 *
 * <p>Description: ������־</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class STAStationLogTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static STAStationLogTool instanceObject;

    /**
     * �õ�ʵ��
     * @return RegMethodTool
     */
    public static STAStationLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAStationLogTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public STAStationLogTool() {
        setModuleName("sta\\STAStationLogModule.x");
        onInit();
    }
    /**
     * ��ѯ���� ������־��Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm){
        TParm result = this.query("selectdata",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ȡ���п���
     * @return TParm
     */
    public TParm selectDept(TParm parm){
        TParm result = this.query("selectDept",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ȡ���в���
     * @return TParm
     * ==============pangben modify 20110525 ��Ӳ���
     */
    public TParm selectStation(String regionCode){
        //=========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //=========pangben modify 20110525 stop
        TParm result = this.query("selectStation",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ȡ�м���е����в���
     * @return TParm
     */
    public TParm selectSTAStation(String regionCode){
        //=========pangben modify 20110525 start
        TParm parm=new TParm();
        if(null!=regionCode && regionCode.length()>0)
            parm.setData("REGION_CODE",regionCode);
        //=========pangben modify 20110525 stop
        TParm result = this.query("selectSTAStation",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���벡����־��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_DAILY_01(TParm parm,TConnection conn2){
        TParm result = this.update("insertData",parm,conn2);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���벡����־��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertSTA_DAILY_01(TParm parm){
        TParm result = this.update("insertData",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �޸� ������־�м䵵 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_STATION_DAILY(TParm parm,TConnection conn){
        TParm result = this.update("updateSTA_STATION_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸� �ż����м䵵 STA_OPD_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateSTA_OPD_DAILY(TParm parm,TConnection conn){
        TParm result = this.update("updateSTA_OPD_DAILY",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ͬʱ�޸� �ż����м䵵 STA_OPD_DAILY �� ������־�м䵵 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateDAILY(TParm parm,TConnection conn){
        TParm result = new TParm();
        if(parm.getData("OPD")==null||parm.getData("STATION")==null){
            result.setErr(-1,"ȱ�ٲ���");
            return result;
        }
        result = this.updateSTA_OPD_DAILY(parm.getParm("OPD"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.updateSTA_STATION_DAILY(parm.getParm("STATION"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ���벡����־�� STA_DAILY_01 ͬʱ�޸� �ż����м䵵 STA_OPD_DAILY �� ������־�м䵵 STA_STATION_DAILY
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDate(TParm parm,TConnection conn){
        TParm result = new TParm();
        if(parm.getData("OPD")==null||parm.getData("STATION")==null||parm.getData("DAILY")==null){
            result.setErr(-1,"ȱ�ٲ���");
            return result;
        }
        result = this.updateSTA_OPD_DAILY(parm.getParm("OPD"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.updateSTA_STATION_DAILY(parm.getParm("STATION"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = this.insertSTA_DAILY_01(parm.getParm("DAILY"),conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ�˴�
     * @return TParm
     */
    public TParm selectInNum(TParm parm){
        TParm result = this.query("selectInNum",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ��Ժ�˴�
     * @return TParm
     */
    public TParm selectOutNum(TParm parm){
        TParm result = this.query("selectOutNum",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ STA_DAILY_01 �����ݣ�������������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_DAILY_01(TParm parm) {
        TParm result = this.query("selectSTA_DAILY_01", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ɾ�� STA_DAILY_01 ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteSTA_DAILY_01(TParm parm,TConnection conn2){
        TParm result = this.update("deleteSTA_DAILY_01",parm,conn2);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����STA_DAILY_01 ������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertNewData(TParm parm,TConnection conn1){
        TParm result = new TParm();
        if(parm.getData("Del")==null||parm.getData("Insert")==null){
            result.setErr(-1,"ȱ�ٲ�����");
            return result;
        }
        TParm parmDel = parm.getParm("Del");
        TParm parmIn = parm.getParm("Insert");
        //��ɾ��ԭ����Ϣ
        result = this.deleteSTA_DAILY_01(parmDel,conn1);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //��������Ϣ
        result = this.insertSTA_DAILY_01(parmIn,conn1);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �޸�STA_DAILY_01���е�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_DAILY_01(TParm parm,TConnection conn){
        TParm parm1 = parm.getParm("sta_daily_01");
        TParm parm2 = parm.getParm("station_daily");
        TParm result = this.update("updateSTA_DAILY_01",parm1,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = STAStationDailyTool.getInstance().updateREAL_OCUU_BED_NUM(parm2,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯSTA_STATION_DAILY���������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_STATION_DAILY(TParm parm){
        TParm result = this.query("selectSTA_STATION_DAILY",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯSTA_OPD_DAILY���������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectSTA_OPD_DAILY(TParm parm){
        TParm result = this.query("selectSTA_OPD_DAILY",parm);
        // �жϴ���ֵ
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ȡת���Ʋ���
     * @param deptCode String ����code
     * @param date String ���ڸ�ʽ��yyyyMMdd
     * @param regionCode String ����
     * @return TParm
     * ==============pangben  modify 20110518 ����������
     */
    public TParm selectOUPR(String deptCode,String  stationCode,String date,String regionCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("DATE",date);
        if(null!=stationCode&&!stationCode.equals(""))
            parm.setData("STATION_CODE",stationCode);
        //========pangben modify 20110518 start
       if(null!=regionCode&&!regionCode.equals(""))
           parm.setData("REGION_CODE",regionCode);
       //========pangben modify 20110518 stop
        TParm result = this.query("selectOUPR",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ȡת��Ʋ���
     * @param deptCode String ����code
     * @param date String ���ڸ�ʽ��yyyyMMdd
     * @param regionCode String ����
     * @return TParm
     * ==============pangben  modify 20110518 ����������
     */
    public TParm selectINPR(String deptCode,String stationCode,String date,String regionCode){
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",deptCode);
        parm.setData("DATE",date);
        if(null!=stationCode&&!stationCode.equals(""))
            parm.setData("STATION_CODE",stationCode);
        //========pangben modify 20110518 start
        if(null!=regionCode&&!regionCode.equals(""))
            parm.setData("REGION_CODE",regionCode);
        //========pangben modify 20110518 stop
        TParm result = this.query("selectINPR",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * �����û�ID��ѯ ��������
     * @param UserID String
     * @return TParm
     */
    public String getDeptByUserID(String UserID){
        TParm parm = new TParm();
        parm.setData("USER_ID",UserID);
        TParm result = this.query("selectDeptByUID",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return "";
        }
        return result.getValue("DEPT_CODE",0);
    }
}
