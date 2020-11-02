package action.ins;

import jdo.ins.INSUpLoadTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: ҽ���걨������</p>
 *
 * <p>Description: ҽ���걨������</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.10
 * @version 1.0
 */
public class INSUpLoadAction extends TAction {
    /**
     * ���¶�Ӧ���ݿ���λ
     * @param parm TParm
     * @return TParm
     */
    public TParm saveUpLoadData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        //9.ҽ���걨�����//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbs(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }

        //10.���·�����ϸ��//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsOrder(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //11.�����ʸ�ȷ����״̬//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsAdmConfirm(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //12.�����ϴ���ϸ��//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsUpload(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * �����걨����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdAppData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        result = INSUpLoadTool.getInstance().onUpInsIbsForCJ(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INSUpLoadTool.getInstance().onUpInsInsAdmConfirmForCJ(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    public TParm checkConfirmData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();
        //�õ�ҽ��״̬
        result = INSUpLoadTool.getInstance().getInsStatusForCJ(parm);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
       // System.out.println("�õ�ҽ��״̬");
        //����ʸ�ȷ����
        result = INSUpLoadTool.getInstance().onUpInsInsAdmConfirmForCheckCJ(
                parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //System.out.println("����ʸ�ȷ����");
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * �����µľ���˳���
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdAdmSeqData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();

        result = INSUpLoadTool.getInstance().onUpInsIbsAdmSeq(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }

        result = INSUpLoadTool.getInstance().onUpInsIbsOrderAdmSeq(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ������ϸ�ϴ���д,������ϸ�ϴ���дINS_ADM_CONFIRM
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdInsIbsBackData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "����Ϊ��");
        TConnection connection = getConnection();

        //������ϸ�ϴ���д
        result = INSUpLoadTool.getInstance().onUpInsIbsBack(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //������ϸ�ϴ���дINS_ADM_CONFIRM
        result = INSUpLoadTool.getInstance().onUpInsAdmConfirmBack(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

}
