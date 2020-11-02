package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: �ɹ��ƻ�����Tool
 * </p>
 *
 * <p>
 * Description: �ɹ��ƻ�����Tool
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class IndPurPlanMTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndPurPlanMTool instanceObject;

    /**
     * ������
     */
    public IndPurPlanMTool() {
        setModuleName("ind\\INDPurPlanMModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndPurPlanMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurPlanMTool();
        return instanceObject;
    }

    /**
     * ��ѯ
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPlanM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewPlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���²ɹ��ƻ������ɶ�����״̬
     *
     * @param parm
     * @return
     */
    public TParm onUpdateCreateFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateCreateFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ɹ��ƻ����ò�ѯ(����ҩƷ��ѯ)
     *
     * @param parm
     * @return
     */
    public TParm onQueryExcerptByOrder(TParm parm) {
        TParm result = this.query("queryExcerptByOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���ڲɹ���
     *
     * @param parm
     * @return
     */
    public TParm onQueryBuyQty(TParm parm) {
        TParm result = this.query("queryBuyQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ����������
     *
     * @param parm
     * @return
     */
    public TParm onQuerySellQty(TParm parm) {
        TParm result = this.query("querySellQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ������
     *
     * @param parm
     * @return
     */
    public TParm onQueryMainQty(TParm parm) {
        TParm result = this.query("queryMainQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�п���
     *
     * @param parm
     * @return
     */
    public TParm onQueryMiddQty(TParm parm) {
        TParm result = this.query("queryMiddQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
