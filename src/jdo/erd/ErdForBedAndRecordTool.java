package jdo.erd;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: ��¼���۴�λ�Ͷ�̬��¼</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdForBedAndRecordTool
    extends TJDOTool {

    /**
     * ʵ��
     */
    private static ErdForBedAndRecordTool instanceObject;

    /**
     * �õ�ʵ��
     * @return PatTool
     */
    public static ErdForBedAndRecordTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdForBedAndRecordTool();
        return instanceObject;
    }

    public ErdForBedAndRecordTool() {

        //����Module�ļ�
        this.setModuleName("erd\\ERDMainModule.x");
        onInit();
    }

    /**
     * ���ݲ�ѯ������ѯREG_PATADM������
     * @param parm TParm
     * @return TParm
     */
    public TParm selPat(TParm parm) {
        TParm result = new TParm();
        result = query("selPat", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ�ѯ������ѯREG_BED������
     * @param parm TParm
     * @return TParm
     */
    public TParm selBed(TParm parm) {
        TParm result = new TParm();
        result = query("selBed", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * ����һ���¼�¼ERD_RECORD
     * @param parm TParm
     * @return TParm
     */
    public TParm insertErdRecord(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("insertErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdBed(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("updateErdBed", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateAdmStatus(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("updateAdmStauts", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ����ERD_BED
     * @param parm TParm
     * @return TParm
     */
    public TParm updateErdRecord(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("updateErdRecord", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݲ�ѯ������ѯ���ﻤʿ��Ҫִ�е�ҽ��
     * @param parm TParm
     * @return TParm
     */
    public TParm selOrderExec(TParm parm) {
        TParm result = new TParm();
        result = query("selOrderExec", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ﻤʿִ�и���OPD_ORDER
     * @param parm TParm
     * @return TParm
     */
    public TParm updateExec(TParm parm, TConnection connection) {

        TParm result = new TParm();
        //ִ��module�ϵ�insert update delete��update
        result = update("updateExec", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ���ݾ�����ŵõ����߼���������������
     * @param parm TParm
     * @return TParm
     */
    public TParm selERDRegionBedByPat(TParm parm){
        TParm result = query("selERDRegionBedByPat",parm);
        return result;
    }
}
