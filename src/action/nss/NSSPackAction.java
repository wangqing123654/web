package action.nss;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.nss.NSSTool;

/**
 * <p>Title: �ײ��ֵ�</p>
 *
 * <p>Description: �ײ��ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackAction
    extends TAction {
    public NSSPackAction() {
    }

    /**
     * ɾ���ײͣ����������ϸ�����ϸ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteNSSPack(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onDeleteNSSPack(parm, conn);
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
     * ɾ���͵㣨������ϸ�����ϸ��
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onDeleteNSSPackD(parm, conn);
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
