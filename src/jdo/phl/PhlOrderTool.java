package jdo.phl;

import com.dongyang.jdo.TJDOTool;
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
public class PhlOrderTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhlOrderTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return IndAgentTool
     */
    public static PhlOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlOrderTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhlOrderTool() {
        setModuleName("phl\\PHLOrderModule.x");
        onInit();
    }

    /**
     * ��������ҽ��
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertOrderDetail", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��������ҽ��
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insertOrderDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ִ��ҽ��
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateOrderDetail", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
