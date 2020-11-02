package action.sys;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.CTZTool;
import jdo.sys.StatusDetailsTool;
import com.dongyang.action.TAction;
import jdo.sys.SYSStationTool;

/**
 * <p>Title: ��ʿվ����action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis </p>
 *
 * @author fudw 2009-09-17
 * @version 1.0
 */
public class SYSStationAction extends TAction {
    /**
     * ɾ����ʿվ��������ķ��仹�з����еĴ�λ
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteStation(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSStationTool.getInstance().deleteStation(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * ɾ�������Լ������еĴ�λ
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteRoom(TParm parm) {
       TConnection connection = getConnection();
       TParm result = new TParm();
       result = SYSStationTool.getInstance().deleteRoom(parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }
   /**
    * ɾ����λ
    * @param parm TParm
    * @return TParm
    */
   public TParm deleteBed(TParm parm) {
       TConnection connection = getConnection();
       TParm result = new TParm();
       result = SYSStationTool.getInstance().deleteBed(parm, connection);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           connection.close();
           return result;
       }
       connection.commit();
       connection.close();
       return result;
   }


}
