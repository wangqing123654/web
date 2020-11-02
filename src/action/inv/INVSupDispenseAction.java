package action.inv;

import com.dongyang.action.TAction;
import jdo.inv.INVTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class INVSupDispenseAction
    extends TAction {
    public INVSupDispenseAction() {
    }

    /**
     * ����һ�����ʳ��ⵥ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertInvSupDispenseByInv(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onInsertInvSupDispenseByInv(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * �������������ⵥ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertInvSupDispenseByPack(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onInsertInvSupDispenseByPack(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }


}
