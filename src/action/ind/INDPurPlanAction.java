package action.ind;

import jdo.ind.INDTool;
import jdo.ind.IndPurPlanMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �ɹ��ƻ�Action
 * </p>
 *
 * <p>
 * Description: �ɹ��ƻ�Action
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

public class INDPurPlanAction
    extends TAction {

    /**
     * ��ѯ�ɹ��ƻ���������������������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryPurPlan(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ɹ��ƻ�������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurPlanMTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ���²ɹ��ƻ�������
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurPlanMTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * ɾ���ɹ��ƻ���
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeletePurPlanM(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * �ɹ��ƻ����ò�ѯ(����ҩƷ��ѯ)
     *
     * @param parm
     * @return TParm
     */
    public TParm onQueryExcerptByOrder(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryExcerptByOrder(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ɹ��ƻ����ɶ�����
     *
     * @param parm
     * @return
     */
    public TParm onCreatePurOrder(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onCreatePurOrder(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

}
