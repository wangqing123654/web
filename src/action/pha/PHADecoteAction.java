package action.pha;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.pha.PhaDecoteTool;

/**
 * <p>Title: ��ҽ��ҩ</p>
 *
 * <p>Description: ��ҽ��ҩ</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.17
 * @version 1.0
 */
public class PHADecoteAction
    extends TAction {
    public PHADecoteAction() {
    }

    /**
     * ����
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhaDecoteTool.getInstance().onSave(parm, conn);
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
     * ȡ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onCancel(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PhaDecoteTool.getInstance().onCancel(parm, conn);
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
