package jdo.phl;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �����ұ���
 * </p>
 *
 * <p>
 * Description: �����ұ���
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PhlRegisterTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static PhlRegisterTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return
     */
    public static PhlRegisterTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlRegisterTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public PhlRegisterTool() {
        setModuleName("phl\\PHLRegisterModule.x");
        onInit();
    }

    /**
     * ��ѯ���˹Һż�¼�б�
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegister(TParm parm) {
        TParm result = this.query("queryRegister", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ȡ�ò����������ĵ��ҩƷ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryOrderDetail(TParm parm) {
        TParm result = this.query("queryOrderDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ȡ���д��ŵĲ����������ĵ��ҩƷ
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryOrderDetailBed(TParm parm) {
        TParm result = this.query("queryOrderDetailBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����ұ�������
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onPhlRegister(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        TParm result = new TParm();
        // ��������ҽ��
        TParm orderParm = parm.getParm("ORDER_PARM");
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            result = PhlOrderTool.getInstance().onInsert(orderParm.getRow(i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // ���´�λ��Ϣ
        TParm bedParm = parm.getParm("BED_PARM");
        result = PhlBedTool.getInstance().onUpdateBed(bedParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�˴α�����ǰ�Ѿ�����phl_order���е����� 
     * add caoyong 20140403
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryoldOrder(TParm parm) {
        TParm result = this.query("onQueryoldOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
