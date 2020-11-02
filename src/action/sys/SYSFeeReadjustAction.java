package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.sys.SYSFeeReadjustMTool;
import com.dongyang.db.TConnection;
import jdo.sys.SYSFeeReadjustDTool;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title:���ۼƻ�Action
 * </p>
 *
 * <p>
 * Description:���ۼƻ�Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustAction
    extends TAction {

    public SYSFeeReadjustAction() {
    }

    /**
     * �������ۼƻ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onInsert(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���µ��ۼƻ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onUpdateM(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���µ��ۼƻ�ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateD(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustDTool.getInstance().onUpdateSYSFeeReadjustD(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ɾ�����ۼƻ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onDeleteM(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ִ�е��ۼƻ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onCreateSYSFeeReadjust(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onCreateSYSFeeReadjust(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE���õ��ۼƻ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onSYSFeeReadjustPatch(TParm parm) {
        //System.out.println("SYS_FEE���õ��ۼƻ�����");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onSYSFeeReadjustPatch(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE���õ��ۼƻ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateSYSFeeReadjustPatch(TParm parm) {
        //System.out.println("SYS_FEE���õ��ۼƻ�����");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onUpdateSYSFeeReadjustPatch(
            parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE���õ��ۼƻ�����ִ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertSYSFeeReadjust(TParm parm) {
        //System.out.println("SYS_FEE���õ��ۼƻ�����ִ��");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onInsertSYSFeeReadjust(
            parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        TIOM_Database.logTableAction("SYS_FEE");

        return result;
    }
}
