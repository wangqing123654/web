package action.inv;

import jdo.inv.INVTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: ��������</p>
 *
 * <p>Description: ��������</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */

public class INVAction
    extends TAction {
//    /**
//     * �����������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveInvVerifyin(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveInvVerifyin(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//
//    }
//
//    /**
//     * ���湩Ӧ�����
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveInvDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveIvndspen(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
    /**
     * ����������������
     * @param parm TParm
     * @return TParm
     */
    public TParm onSavePackAge(TParm parm) {
//    	System.out.println("------INVActionGYS------");
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        result = INVTool.getInstance().onSavePackAgeGYS(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
//
//    /**
//     * ���湩Ӧ�ҳ���
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveOutStock(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveOutStockPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }

    /**
     * ���������ͻ���
     * @param parm TParm
     * @return TParm
     */
    public TParm saveBackPackAndDisnfection(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        result = INVTool.getInstance().saveBackPackAndDisnfection(parm,
            connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

//    /**
//     * ����������������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveRePackAge(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveRePack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * �������������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm saveTearPack(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveTearPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * ���湩Ӧ���˿�
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveBackDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveBackDispense(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * �����������Զ�������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveAutoRePackAge(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveAutoRePack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * zhangyong20091112
//     * ȡ����Ӧ�ҳ���
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onCancelInvDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onCancelInvDispense(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * ���ư���������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm saveCallBackPack(TParm parm){
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveCallBackPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * ���ư����ù黹
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onBorrowBack(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "����Ϊ��");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onBorrowBack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
}
