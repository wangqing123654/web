package action.mro;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MRORecordActionTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MRORecordAction
    extends TAction {
    /**
     * �޸���ҳ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDate(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = MRORecordActionTool.getInstance().updateRecordDate(parm,connection);
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
//    /**
//     * �����÷���
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm test(TParm parm){
//        TParm result = new TParm();
//        TConnection connection = getConnection();
//        result = MROInterfaceTool.getInstance().updateTransDept(parm,connection);
//        connection.commit();
//        connection.close();
//        return result;
//    }
}
