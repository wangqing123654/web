package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;
import jdo.ins.INSTool;
/**
 *
 * <p>Title: ҽ��������</p>
 *
 * <p>Description: ҽ��������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSAction
    extends TAction {
    /**
     * ҽ���Һű���
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveREG(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        result = INSTool.getInstance().onSaveREG(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * ҽ���˹ұ���
     * @param parm TParm
     * @return TParm
     */
    public TParm onReturnREG(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        result = INSTool.getInstance().onReturnREG(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
}
