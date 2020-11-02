package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �����ֵ�</p>
 *
 * <p>Description: �����ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSTabooTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSTabooTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSTabooTool
     */
    public static NSSTabooTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSTabooTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSTabooTool() {
        setModuleName("nss\\NSSTabooModule.x");
        onInit();
    }

    /**
     * ���������ֵ�
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSTaboo(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSTaboo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���½����ֵ�
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSTaboo(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSTaboo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�������ֵ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSTaboo(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSTaboo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
