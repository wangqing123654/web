package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title:�Һ��վݵ� </p>
 *
 * <p>Description:�Һ��վݵ� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class REGReceiptTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static REGReceiptTool instanceObject;
    /**
     * �õ�ʵ��
     * @return REGReceiptTool
     */
    public static REGReceiptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new REGReceiptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public REGReceiptTool() {
        setModuleName("reg\\REGReceiptModule.x");
        onInit();
    }

    /**
     * ��ӡд�����վ�
     * @return TParm
     */
    public TParm insertBill(TParm parm, TConnection connection) {
        TParm result = update("insertBill", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�Ƿ�����Һ��վ�(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm selCaseCountForREG(TParm parm) {
        TParm result = query("selCaseCountForREG", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �����˹��վ�(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateRecpForUnReg(TParm parm,TConnection connection) {
        TParm result = update("updateRecpForUnReg", parm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �˹�д��һ�ʸ�������(FOR REG)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertDataForUnReg(TParm parm,TConnection connection) {
        TParm result = update("insertDataForUnReg", parm,connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
