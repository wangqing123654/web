package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: Ԥ���𹤾���</p>
 *
 * <p>Description: Ԥ���𹤾���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILPayTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILPayTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILPayTool
     */
    public static BILPayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILPayTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILPayTool() {
        setModuleName("bil\\BILPayModule.x");
        onInit();
    }
    /**
     * ��ѯȫ������(���ݾ�����Ż򲡰���)
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
     * ��ѯȫ������(����Ԥ�����վݺ�)
     * @param receiptNo String
     * @return TParm
     */
    public TParm selAllDataByRecpNo(String receiptNo) {
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO", receiptNo);
        TParm result = query("selAllDataByRecpNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �����嵥
     * @param parm TParm
     * @return TParm
     */
    public TParm selAllDataByRecpNo(TParm parm) {
        TParm result = query("selAllDataByRecpNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����ID�Ų�ѯ���������
     * @param parm TParm
     * @return TParm mrNo
     */
    public TParm selectPatCaseNo(TParm parm) {
        TParm result = query("selectPatCaseNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * (���ݾ������)��ѯ����������Ϣ(��ԺҲ���˷�)
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataByCaseNo(TParm parm) {
        TParm result = query("seldataByCaseNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * (���ݲ�����)��ѯ����������Ϣ(��ԺҲ���˷�)
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataByIpdNo(TParm parm) {
        TParm result = query("seldataByIpdNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ����Ԥ��������(��Ԥ����)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
        TParm result = update("insertData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ����Ԥ��������(��Ԥ����)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
    	if (parm.getData("CARD_TYPE") == null || "".equals(parm.getData("CARD_TYPE")))
    		parm.setData("CARD_TYPE", new TNull(String.class));
    	if (parm.getData("BUSINESS_NO") == null || "".equals(parm.getData("BUSINESS_NO")))
    		parm.setData("BUSINESS_NO", new TNull(String.class));
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
     * ��ѯԤ��������(�ɷ���ҵ)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForCharge(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.selDataForCharge()�����쳣!");
            return result;
        }
        result = query("selDataForCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * ���³����վݺ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataOffBilPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.updataOffBilPay()�����쳣!");
            return result;
        }
        result = update("updataOffBilPay", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * ����Ԥ����ӡ�վ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upRecpForRePrint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.updataOffBilPay()�����쳣!");
            return result;
        }
        result = update("upRecpForRePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * Ԥ�������
     * @param caseNo String
     * @return TParm
     */
    public TParm selBilPayLeft(String caseNo){
        String sql =
            "SELECT SUM (PRE_AMT) AS PRE_AMT FROM BIL_PAY "+
            " WHERE CASE_NO='"+caseNo+"' "+
            "   AND TRANSACT_TYPE IN ('01', '02') ";
        TParm  result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

//    /**
//     * ��Ԥ�������
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onReturnBILPay(TParm parm) {
//        TParm result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
//            "onReturnFee", parm);
//        if (result.getErrCode() < 0) {
//            err(result.getErrCode() + " " + result.getErrText());
//            return result;
//        }
//        return result;
//    }
    /**
     * ����Ԥ�����IPD_NO
     * �����ײ�ԤԼ��ֵ����ʹ�ã�����������Ժʱͬʱ����BIL_PAY��
     * ==========pangben 2014-7-31
     */
	public TParm updateBilPayIpdNo(TParm parm, TConnection connection) {
		TParm result = update("updateBilPayIpdNo", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
