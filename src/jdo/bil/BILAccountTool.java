package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:�����սύ�� </p>
 *
 * <p>Description:�����սύ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class BILAccountTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILAccountTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILAccountTool
     */
    public static BILAccountTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILAccountTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILAccountTool() {
        setModuleName("bil\\BILAccountModule.x");
        onInit();
    }

    /**
     * ��ѯȫ��������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
        TParm result = query("selectData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �սύ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertAccount(TParm parm, TConnection connection) {
        TParm result = update("insertAccount", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ȡ���ս�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����Ƿ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selectCheckAccount(TParm parm) {
        TParm result = query("selectCheckAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�ս�����
     * @param parm TParm
     * @return TParm
     */
    public TParm accountQuery(TParm parm) {
        TParm result = new TParm();
        result = this.query("accountQuery", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

}
