package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILCounteTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILCounteTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILCounteTool
     */
    public static BILCounteTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILCounteTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILCounteTool() {
        setModuleName("bil\\BILCounterModule.x");
        onInit();
    }

    /**
     * ��ѯȫ������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���˲�������
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ������������
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
     * ������������
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��˿�����
     * @param parm TParm
     * @return TParm
     */
    public TParm CheckCounter(TParm parm) {
        TParm result = query("CheckCounter", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ���ʹ���е�Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public TParm finishData(TParm parm) {
        TParm result = query("finishData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

}
