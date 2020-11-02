package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: ��ʿվ����</p>
 *
 * <p>Description: ��ʿվ����</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-09-18
 * @version 1.0
 */
public class SYSStationTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static SYSStationTool instanceObject;
    /**
     * �õ�ʵ��
     * @return DeptTool
     */
    public static SYSStationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSStationTool();
        return instanceObject;
    }

//    /**
//     * ������
//     */
//    public SYSStationTool() {
//        setModuleName("inv\\INVOrgModule.x");
//        onInit();
//    }

    /**
     * ɾ����ʿվ��������ķ���,�Լ������еĴ�λ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteStation(TParm parm,TConnection connection) {
        TParm result = new TParm();
        if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
        TParm inParm=parm.getParm("DELETE");
        String deleteStationSql=inParm.getValue("DELETESTATION");
        //ִ��ɾ����ʿվ
       result = new TParm(TJDODBTool.getInstance().update(deleteStationSql,
           connection));
       if (result.getErrCode() < 0)
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());

        String deleteRoomSql=inParm.getValue("DELETEROOM");
        //ִ��ɾ������
     result = new TParm(TJDODBTool.getInstance().update(deleteRoomSql,
         connection));
     if (result.getErrCode() < 0)
         err("ERR:" + result.getErrCode() + result.getErrText() +
             result.getErrName());

        String deleteBedSql=inParm.getValue("DELETEBED");
        //ִ��ɾ������
        result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());


        return result;
    }
    /**
     * ɾ�����估������Ĵ�λ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteRoom(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TParm inParm = parm.getParm("DELETE");
        String deleteRoomSql = inParm.getValue("DELETEROOM");
        //ִ��ɾ������
        result = new TParm(TJDODBTool.getInstance().update(deleteRoomSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        String deleteBedSql = inParm.getValue("DELETEBED");
        //ִ��ɾ����λ
        result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
            connection));
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        return result;
    }
    /**
     * ɾ����λ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteBed(TParm parm,TConnection connection) {
       TParm result = new TParm();
       if(parm==null)
            return result.newErrParm(-1,"����Ϊ��");
       TParm inParm=parm.getParm("DELETE");
       String deleteBedSql = inParm.getValue("DELETEBED");
       //ִ��ɾ����λ
       result = new TParm(TJDODBTool.getInstance().update(deleteBedSql,
           connection));
       if (result.getErrCode() < 0)
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());

       return result;
   }

   /**
    * ��ѯ������������
    * @param parm TParm
    * @return TParm
    */
   public TParm selStationRegion(String stationCode) {
       TParm result = new TParm();
       String sql =
           " SELECT REGION_CODE FROM SYS_STATION WHERE STATION_CODE = '" + stationCode + "' ";
       result = new TParm(TJDODBTool.getInstance().select(sql));
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }


}
