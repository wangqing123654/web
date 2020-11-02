package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �ײͲ���</p>
 *
 * <p>Description: �ײͲ���</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackDDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSPackDDTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSPackddTool
     */
    public static NSSPackDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSPackDDTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSPackDDTool() {
        setModuleName("nss\\NSSPackDDModule.x");
        onInit();
    }

    /**
     * ��ѯ�ײͲ���
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSPackDD(TParm parm) {
        TParm result = this.query("queryNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ײͲ���
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ײͲ���
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���ײͲ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���ײͲ���
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackDD(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
