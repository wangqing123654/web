package jdo.adm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title:������ע�� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author duzhw
 * @version 4.5
 */
public class ADMNewBodyRegisterTool extends TJDOTool {
	
	 /**
     * ʵ��
     */
    public static ADMNewBodyRegisterTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ADMNewBodyRegisterTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMNewBodyRegisterTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public ADMNewBodyRegisterTool() {
        setModuleName("adm\\ADMNewBodyRegisterModule.x");
        onInit();
    }
    
    /**
     * ע��������-����sys_patinfo��
     */
    public TParm insertSysPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertSysPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯĸ��סԺ���
     */
    public TParm queryAdmDiag(TParm parm) {
        TParm result = new TParm();
        result = query("queryDiagForMro", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����סԺ���
     */
    public TParm insertAdmDiag(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertDiag", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯĸ�ײ�����Ϣ
     */
    public TParm queryMroRecrd(TParm parm) {
    	TParm result = new TParm();
        result = query("selectInHospInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���벡����Ϣ
     */
    public TParm insertMroRecrd(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯĸ�ײ������
     */
    public TParm queryMroRecordDiag(TParm parm) {
    	TParm result = new TParm();
        result = query("queryMRODiag", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���벡�������Ϣ
     */
    public TParm insertMroRecordDiag(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertMRODiag", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯת����־
     */
    public TParm queryTransLog(TParm parm) {
    	TParm result = new TParm();
        result = query("selectData", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ����ת����־
     */
    public TParm insertTransLog(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /*
     * ��ѯ��̬��¼��־
     */
    public TParm queryChg(TParm parm){
    	TParm result = new TParm();
        result = query("queryChg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ���붯̬��¼��־
     */
    public TParm insertChg(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("insertChg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��������Ϣ�޸�
     */
    public TParm updateSysPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("updateSysPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * �鿴ռ��ע��
     */
    public TParm checkBedAlloFlg(TParm parm) {
    	TParm result = new TParm();
        result = query("checkBedAlloFlg", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ������������Ժ 
     */
    public TParm newPatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("newPatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����ԭmr_no���ݵ�״̬
     */
    public TParm updatePatInfo(TParm parm, TConnection conn) {
    	TParm result = new TParm();
        result = update("updatePatInfo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
   
    
}
