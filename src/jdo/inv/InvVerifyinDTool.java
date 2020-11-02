package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

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
public class InvVerifyinDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvVerifyinDTool instanceObject;

    /**
     * ������
     */
    public InvVerifyinDTool() {
        setModuleName("inv\\INVVerifyinDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvVerifyinDTool
     */
    public static InvVerifyinDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvVerifyinDTool();
        return instanceObject;
    }

    /**
     * ��ѯ���յ�ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryVerifyinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createVerifyinD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������ϸ��
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateVerifyinD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("deleteVerifyinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������ϸ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteVerifyinD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ������ϸ��(ȫ��)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteVerifyinDAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
