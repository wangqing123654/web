package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �˵��趨</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSMenuTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSMenuTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSMenuTool
     */
    public static NSSMenuTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSMenuTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSMenuTool() {
        setModuleName("nss\\NSSMenuModule.x");
        onInit();
    }

    /**
     * ��ѯ�˵�
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSMenu(TParm parm) {
        TParm result = this.query("queryNSSMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����˵�
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²˵�
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���˵�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSMenu(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSMenu", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
