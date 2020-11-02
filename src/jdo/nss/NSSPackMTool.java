package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class NSSPackMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSPackMTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSPackMTool
     */
    public static NSSPackMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSPackMTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSPackMTool() {
        setModuleName("nss\\NSSPackMModule.x");
        onInit();
    }

    /**
     * ��ѯ�ײ��ֵ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSPackM(TParm parm) {
        TParm result = this.query("queryNSSPackM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ײ��ֵ�
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSPackM(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSPackM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ײ��ֵ�
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSPackM(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSPackM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���ײ��ֵ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackM(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���ײ��ֵ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackM(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
