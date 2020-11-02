package jdo.ibs;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: סԺ��������������</p>
 *
 * <p>Description: סԺ��������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.03.18
 * @version 1.0
 */
public class IBSOrdermTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static IBSOrdermTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSOrdermTool
     */
    public static IBSOrdermTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSOrdermTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public IBSOrdermTool() {
        setModuleName("ibs\\IBSOrdermModule.x");
        onInit();
    }

    /**
     * ��ѯ��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdate(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ɾ��
     * @param parm TParm
     * @return TParm CASE_NO,CASE_NO_SEQ
     */
    public TParm deletedata(TParm parm) {
        TParm result = new TParm();
        result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * �����˵�����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateBillNO(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updateBillNO", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���в���,��������
     * @param parm TParm
     * @return TParm
     */
    public TParm selRegionStation(TParm parm) {
        TParm result = new TParm();
        result = query("selRegionStation", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    public TParm selRegionStationCaseNo(TParm parm)
    {
        TParm result = new TParm();
        result = query("selRegionStationCaseNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * ��ѯ����������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm selStationPatInfo(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSOrdermTool.selStationPatInfo()�����쳣!");
            return result;
        }
        result = query("selStationPatInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * �жϲ�������(For ADM)
     * @param parm TParm
     * @return boolean true û���� false ����
     */
    public boolean existFee(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSOrdermTool.selStationPatInfo()�����쳣!");
            return false;
        }
        result = query("existFee", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;

        }
//        if (result.getCount("CASE_NO") > 0)
//            return false;
        if (result.getDouble("TOT_AMT",0) != 0)
            return false;
        return true;
    }

    /**
     * ��ѯ����������
     * @param caseNo String
     * @return TParm
     */
    public TParm selMaxCaseNoSeq(String caseNo) {
        TParm result = new TParm();
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", caseNo);
        result = query("selMaxCaseNoSeq", inParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ�������(����)
     * @param caseNo String
     * @param billDate String
     * @return String
     */
    public String selCaseNoSeqForPatch(String caseNo, String billDate) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = query("selCaseNoSeqForPatch", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result.getErrName();
        }
        return result.getValue("CASE_NO_SEQ", 0);
    }

    /**
     * ɾ��������ϸ��(����)
     * @param caseNo String
     * @param billDate String
     * @param conn TConnection
     * @return TParm
     */
    public TParm deleteOrderMPatch(String caseNo, String billDate,
                                   TConnection conn) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = update("deleteOrderMPatch", parm, conn);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�����˵�����(�����˵�)
     * @param parm TParm
     * @return TParm
     */
    public TParm selBillReturnM(TParm parm) {
        TParm result = new TParm();
        result = query("selBillReturnM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 
    * @Title: selCaseNoSeqForPatchSum
    * @Description: TODO(��ѯ��Ҫɾ���������������)����
    * @author pangben 2015-11-9
    * @param caseNo
    * @param billDate
    * @return
    * @throws
     */
    public TParm selCaseNoSeqForPatchSum(String caseNo, String billDate) {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("S_DATE", billDate + "000000");
        parm.setData("E_DATE", billDate + "235959");
        result = query("selCaseNoSeqForPatch", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
