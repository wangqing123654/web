package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �˻���������Tool
 * </p>
 *
 * <p>
 * Description: �˻���������Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.05.21
 * @version 1.0
 */

public class IndRegressgoodsMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndRegressgoodsMTool instanceObject;

    /**
     * ������
     */
    public IndRegressgoodsMTool() {
        setModuleName("ind\\INDRegressgoodsMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndRegressgoodsMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndRegressgoodsMTool();
        return instanceObject;
    }

    /**
     * ��ѯ�˻�����
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryRegressgoodsM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����˻�����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����˻�����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ���˻�����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�˻�����ͳ��(δ����)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoods(TParm parm) {
        TParm result = this.query("getQueryRegressgoods", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ҩƷ�˻�����ͳ��(�ѳ���)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoodsCheck(TParm parm) {
        TParm result = this.query("getQueryRegressgoodsCheck", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
