package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: סԺ�վ���ϸ��������</p>
 *
 * <p>Description: סԺ�վ���ϸ��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpdTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILIBSRecpdTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILIBSRecpdTool
     */
    public static BILIBSRecpdTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILIBSRecpdTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILIBSRecpdTool() {
        setModuleName("bil\\BILIBSRecpdModule.x");
        onInit();
    }

    /**
     * ��ѯ�վ���ϸ����������
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
     * �����վ���ϸ��
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
     * ��ѯ�ٻ���ϸ������
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
     * �����վ���ϸ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
