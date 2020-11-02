package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �����������Tool
 * </p>
 *
 * <p>
 * Description: �����������Tool
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
 * @author zhangy 2009.05.04
 * @version 1.0
 */

public class IndVerifyinMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndVerifyinMTool instanceObject;

    /**
     * ������
     */
    public IndVerifyinMTool() {
        setModuleName("ind\\INDVerifyinMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndVerifyinMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndVerifyinMTool();
        return instanceObject;
    }

    /**
     * ��ѯ��������
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryVerifyinM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������������еĲɹ��ƻ�����
     *
     * @param parm
     * @return
     */
    public TParm onQueryPlanNo(String plan_no) {
        String sql = INDSQL.getPlanNoInVerifyin(plan_no);
        if ("".equals(sql)) {
            return null;
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݹ�Ӧ�̺Ϳ��Ҳ�ѯ��������յ���
     *
     * @param org_code
     * @param sup_code
     * @return
     */
    public TParm onQueryDoneVerByOrgAndSup(String org_code, String sup_code) {
        String sql = INDSQL.getDoneVerifyinByOrgAndSup(org_code, sup_code);
        if ("".equals(sql)) {
            return null;
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������������
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������������
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ�����յ�����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�������յ�����Ϣ
     *
     * @param parm
     * @return
     */
    public TParm onQueryDone(TParm parm) {
        TParm result = this.query("queryDoneVerifyin", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
