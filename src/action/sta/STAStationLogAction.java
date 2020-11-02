package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sta.STAStationLogTool;

/**
 * <p>Title:������־ </p>
 *
 * <p>Description: ������־</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangk 2009-5-18
 * @version 1.0
 */
public class STAStationLogAction
    extends TAction {
    public STAStationLogAction() {
    }
    /**
     * ͬʱ�޸� �ż����м䵵 STA_OPD_DAILY �� ������־�м䵵 STA_STATION_DAILY
     * @param parm TParm
     * @return TParm
     */
//    public TParm updateData(TParm parm){
//        TParm result = new TParm();
//        if (parm == null) {
//            result.setErr( -1, "��������Ϊ�գ�");
//            return result;
//        }
//        TConnection connection = getConnection();
//        result = STAStationLogTool.getInstance().updateDAILY(parm,connection);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            connection.close();
//            return result;
//        }
//        //ֱ��ͨ�� �����ύ
//        connection.commit();
//        connection.close();
//        return result;
//    }
    /**
     * ���벡����־�� STA_DAILY_01 ͬʱ�޸� �ż����м䵵 STA_OPD_DAILY �� ������־�м䵵 STA_STATION_DAILY
     * @return TParm
     */
//    public TParm insertData(TParm parm){
//        TParm result = new TParm();
//        if (parm == null) {
//            result.setErr( -1, "��������Ϊ�գ�");
//            return result;
//        }
//        TConnection connection = getConnection();
//        result = STAStationLogTool.getInstance().insertDate(parm,connection);
//        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//                result.getErrName());
//            connection.close();
//            return result;
//        }
//        //ֱ��ͨ�� �����ύ
//        connection.commit();
//        connection.close();
//        return result;
//    }
    /**
     * ���ɲ����� STA_DAILY_01 ������
     * @param parm TParm
     * @return TParm
     */
    public TParm insertNewData(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();

        result = STAStationLogTool.getInstance().insertNewData(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //ֱ��ͨ�� �����ύ
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ���ɲ����� STA_DAILY_01 ������
     * @param parm TParm
     * @return TParm
     */
    public TParm creatData(TParm parm){
        TParm result = new TParm();
        TConnection conn = this.getConnection();

        result = STAStationLogTool.getInstance().insertNewData(parm,conn);
        if(result.getErrCode()<0){
            conn.rollback();
            conn.close();
            System.out.println("result.getErrText->"+result.getErrText());
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * �޸Ĳ�����־����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_DAILY_01(TParm parm){
        TParm result = new TParm();
        TConnection conn = this.getConnection();
        result = STAStationLogTool.getInstance().updateSTA_DAILY_01(parm,conn);
        if(result.getErrCode()<0){
            conn.close();
            System.out.println("result.getErrText->"+result.getErrText());
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
}
