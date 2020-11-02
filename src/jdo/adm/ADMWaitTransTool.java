package jdo.adm;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:���ת���� </p>
 *
 * <p>Description:���ת���� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author JiaoY 
 * @version 1.0 
 */
public class ADMWaitTransTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static ADMWaitTransTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMWaitTransTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMWaitTransTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMWaitTransTool() {
        setModuleName("adm\\ADMWaitTransModule.x");
        onInit();
    }

    /**
     * ��ѯadm_Wait_Transȫ�ֶ�
     * @param parm TParm
     * @return TParm
     */
    public TParm queryDate(TParm parm) {
        TParm result = new TParm();
        result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * סԺ�Ǽ�ʱ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm saveForInp(TParm parm) {
        TParm result = new TParm();
        result = update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ת��ʱ����
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForInOutDept(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insertForInOutDept", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��ת�봫��������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm selpatInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selpatInfo",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ��Ժ������Ϣ
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm selAdmPat(TParm parm) {
        TParm result = new TParm();
        result = query("selAdmPat",parm);
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
     * @return TParm
     */
    public TParm deleteIn(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ȡ��ת��ʱ����ɾ��,�����.
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForCancelTrans(TParm parm, TConnection conn) { 
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }     

}
