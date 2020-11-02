package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ����ϸ��Tool
 * </p>
 *
 * <p>
 * Description: ����ϸ��Tool
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class IndPurorderDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IndPurorderDTool instanceObject;

    /**
     * ������
     */
    public IndPurorderDTool() {
        setModuleName("spc\\INDPurorderDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static IndPurorderDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurorderDTool();
        return instanceObject;
    }

    /**
     * ��ѯ��������
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPurOrderD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯδ��ɶ�������
     *
     * @param sup_code
     * @return
     */
    public TParm onQueryUnDonePurorderNo(String org_code, String sup_code) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getUnDonePurorderNo(org_code, sup_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �ۼ����������ۼ�Ʒ�ʿۿ����
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateVer(TParm parm, TConnection conn) {
        TParm result = this.update("updatePlanDVer", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�ۼ�������
     *
     * @param parm
     * @return
     */
    public TParm onQueryPurOrderQTY(TParm parm) {
        TParm result = this.query("queryPurOrderQTY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��������ϸ��
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createPurorderD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
