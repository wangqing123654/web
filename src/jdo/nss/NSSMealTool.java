package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: �ʹ����õ�</p>
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
public class NSSMealTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSMealTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSMealTool
     */
    public static NSSMealTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSMealTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSMealTool() {
        setModuleName("nss\\NSSMealModule.x");
        onInit();
    }

    /**
     * ��ѯ�ʹ����õ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSMeal(TParm parm) {
        TParm result = this.query("queryNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ʹ����õ�
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²ʹ����õ�
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���ʹ����õ�
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSMeal(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSMeal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
