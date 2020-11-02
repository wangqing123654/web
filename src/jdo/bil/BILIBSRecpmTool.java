package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: סԺ�վ�����������</p>
 *
 * <p>Description: סԺ�վ�����������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpmTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILIBSRecpmTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILIBSRecpmTool
     */
    public static BILIBSRecpmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILIBSRecpmTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILIBSRecpmTool() {
        setModuleName("bil\\BILIBSRecpmModule.x");
        onInit();
    }

    /**
     * ��ѯ�վ�������������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = new TParm();
        result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����վ���������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�ٻ���������
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForReturn(TParm parm) {
        TParm result = new TParm();
        result = query("selDateForReturn", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * �����վ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = this.update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯסԺ�ս�����
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForAccount(TParm parm) {
        TParm result = query("selDateForAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����סԺδ���˵��շ�Ա
     * @param parm TParm
     * @return TParm
     */
    public TParm selCashier(TParm parm)
    {
        TParm result = query("selCashier", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �����ս���,�ս��,�ս���Ա,�ս�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateAccount(TParm parm, TConnection connection) {
        TParm result = update("updateAccount", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * סԺƱ�ݲ�ӡ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upRecpForRePrint(TParm parm, TConnection connection) {
        TParm result = update("upRecpForRePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


}
