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
public class BILREGRecpTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILREGRecpTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILREGRecpTool
     */
    public static BILREGRecpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILREGRecpTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILREGRecpTool() {
        setModuleName("bil\\BILREGRecpModule.x");
        onInit();
    }

    /**
     * ��ӡд�����վ�
     * @param parm TParm
     * @param connection TConnection
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
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRecpForUnReg(TParm parm, TConnection connection) {
        TParm result = update("updateRecpForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �˹�д��һ�ʸ�������(FOR REG)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDataForUnReg(TParm parm, TConnection connection) {
        TParm result = update("insertDataForUnReg", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �˹Ҳ�ѯ�վ�����
     * @param caseNo String
     * @return TParm
     */
    public TParm selDataForUnReg(String caseNo) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = query("selDataForUnReg", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�Һ��ս�����
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
     * ��ѯ��֧ͬ����ʽ������(�ս�)
     * @param parm TParm
     * @return TParm
     */
    public TParm selPayTypeFee(TParm parm) {
        TParm result = query("selPayTypeFee", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ӡ��ѯȫ�ֶ�
     * @param caseNo String
     * @return TParm
     */
    public TParm selForRePrint(String caseNo) {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        TParm result = query("selForRePrint", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ���²�ӡ�վ�(FOR REG)
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
    /**
     * ��ѯ����Ʊ�ݺ��루�սᣩ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm getRegResetCount(TParm parm) {
    	TParm result = query("getRegResetCount", parm);
    	if (result.getErrCode() < 0) {
    		err(result.getErrCode() + " " + result.getErrText());
    		return result;
    	}
    	return result;
    }
}
