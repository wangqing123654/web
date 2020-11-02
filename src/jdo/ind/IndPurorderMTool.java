package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ��������Tool
 * </p>
 *
 * <p>
 * Description: ��������Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.04
 * @version 1.0
 */

public class IndPurorderMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndPurorderMTool instanceObject;

    /**
     * ������
     */
    public IndPurorderMTool() {
        setModuleName("ind\\INDPurorderMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndPurorderMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurorderMTool();
        return instanceObject;
    }

    /**
     * ��ѯ���������еĲɹ��ƻ�����
     *
     * @param parm
     * @return
     */
    public TParm onQueryPlanNo(String plan_no) {
        String sql = INDSQL.getPlanNoInPurorder(plan_no);
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
     * ��ѯ��������
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPurOrderM", parm);
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
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createPurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¶�������
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯδ��ɶ�������Ϣ
     *
     * @param parm
     * @return
     */
    public TParm onQueryUnDone(TParm parm) {
        TParm result = this.query("queryUnDonePurOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���ж�������Ϣ
     *
     * @param parm
     * @return
     */
    public TParm onQueryDone(TParm parm) {
        TParm result = this.query("queryDonePurOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ����������
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���¶��������ɹ��ƻ�����
     *
     * @param parm
     * @return
     */
    public TParm onUpdatePlanNo(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderMPlanNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
