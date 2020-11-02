package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ibs.IBSTool;

/**
 * <p>Title: ��ʳϵͳ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class NSSTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static NSSTool instanceObject;

    /**
     * �õ�ʵ��
     *
     * @return NSSTool
     */
    public static NSSTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public NSSTool() {
        onInit();
    }

    /**
     * ɾ���ײͣ����������ϸ�����ϸ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPack(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        // ɾ���ײͲ���
        result = NSSPackDDTool.getInstance().onDeleteNSSPackDD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // ɾ���ײͲ͵�
        result = NSSPackDTool.getInstance().onDeleteNSSPackD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // ɾ���ײ��ֵ�
        result = NSSPackMTool.getInstance().onDeleteNSSPackM(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * ɾ���͵㣨������ϸ�����ϸ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        // ɾ���ײͲ���
        result = NSSPackDDTool.getInstance().onDeleteNSSPackDD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        // ɾ���ײͲ͵�
        result = NSSPackDTool.getInstance().onDeleteNSSPackD(parm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertNSSOrder(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            // ����
            result = NSSOrderTool.getInstance().onInsertNSSOrder(parm.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * �շ�(ͬʱд��IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateNSSChagre(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
            // �շ�
            result = NSSOrderTool.getInstance().onUpdateNSSChagre(parm.getRow(i),
                conn);
            if (result.getErrCode() < 0) {
                return result;
            }
        }

        //�Ƽ۽ӿ�
        TParm inParmIbs = new TParm();
        inParmIbs.setData("M", parm.getData());
        inParmIbs.setData("FLG", "ADD");
        //zhangyong20110516 �������REGION_CODE
        inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));

        inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
        inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
        inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
        TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
        if (resultIbs.getErrCode() < 0) {
            return resultIbs;
        }
        TParm inIbs = new TParm();
        inIbs.setData("M", resultIbs.getData());
        inIbs.setData("DATA_TYPE", "4");
        inIbs.setData("FLG", "ADD");
        TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs, conn);
        if (resultIbsM2.getErrCode() != 0) {
            return resultIbsM2;
        }
        return result;
    }

    /**
     * �˷�(ͬʱд��IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateNSSUnChagre(TParm parm, TConnection conn) {
        // ���ݼ��
        if (parm == null)
            return null;
        // �����
        TParm result = new TParm();
        // �˷�
        result = NSSOrderTool.getInstance().onUpdateNSSChagre(parm.getRow(0), conn);
        if (result.getErrCode() < 0) {
            return result;
        }

        //�Ƽ۽ӿ�
        TParm inParmIbs = new TParm();
        inParmIbs.setData("M", parm.getData());
        inParmIbs.setData("FLG", "ADD");
        //zhangyong20110516 �������REGION_CODE
        inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));

        inParmIbs.setData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));
        inParmIbs.setData("CTZ2_CODE", parm.getValue("CTZ2_CODE", 0));
        inParmIbs.setData("CTZ3_CODE", parm.getValue("CTZ3_CODE", 0));
        TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
        if (resultIbs.getErrCode() < 0) {
            return resultIbs;
        }
        TParm inIbs = new TParm();
        inIbs.setData("M", resultIbs.getData());
        inIbs.setData("DATA_TYPE", "4");
        inIbs.setData("FLG", "DIX");
        TParm resultIbsM2 = IBSTool.getInstance().insertIBSOrder(inIbs, conn);
        if (resultIbsM2.getErrCode() != 0) {
            return resultIbsM2;
        }
        return result;
    }

}
