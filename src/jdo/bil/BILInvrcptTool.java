package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILInvrcptTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILInvrcptTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILInvrcptTool
     */
    public static BILInvrcptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvrcptTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILInvrcptTool() {
        setModuleName("bil\\BILInvrcptModule.x");
        onInit();
    }

    /**
     * ��ѯȫ��������ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectData", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ��ӡƱ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        parm.setData("PRINT_USER", parm.getData("OPT_USER"));
        TParm result = update("insertData", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ����,���ϵ�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ������������
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * Ʊ�ݹ���
     * @param parm TParm
     * @return TParm
     */
    public TParm selectByInvNo(TParm parm) {
        TParm result = query("selectByInvNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * ����һ��Ʊ��(�˷�)
     * @param parm TParm
     * @return TParm
     */
    public TParm getOneInv(TParm parm) {
        TParm result = query("getOneInv", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �ս�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm account(TParm parm, TConnection connection) {
        TParm result = update("account", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * �ս�o e h
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm accountAll(TParm parm, TConnection connection) {
    	TParm result = update("accountAll", parm, connection);
    	if (result.getErrCode() < 0)
    		err(result.getErrCode() + " " + result.getErrText());
    	return result;
    }

    /**
     * �õ��ս��˷�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvalidCount(TParm parm) {
        TParm result = query("getInvalidCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }   
    
    /**
     * �õ��ս��˷�����(O,E,H)
     * ====zhangp 20120310
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvalidCountAll(TParm parm) {
        TParm result = query("getInvalidCountAll", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �����ս�Ʊ������
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvrcpCount(TParm parm) {
        TParm result = query("getInvrcpCount", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * �����ս�Ʊ������(O,E,H)
     * @param parm TParm
     * @return TParm
     */
    public TParm getInvrcpCountAll(TParm parm) {
    	TParm result = query("getInvrcpCountAll", parm);
    	if (result.getErrCode() < 0)
    		err(result.getErrCode() + " " + result.getErrText());
    	return result;
    }

    /**
     * �����ս�Ʊ�ݺ�
     * @param parm TParm
     * @return TParm
     */
    public TParm getPrintNo(TParm parm) {
        TParm result = query("getPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �õ��˷�Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public TParm getBackPrintNo(TParm parm) {
        TParm result = query("getBackPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * �õ�����Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public TParm getTearPrintNo(TParm parm) {
        TParm result = query("getTearPrintNo", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
}
