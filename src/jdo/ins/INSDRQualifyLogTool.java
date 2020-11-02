package jdo.ins;


import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
 * 
 * <p>
 * Title: ְҵҽʦ֤��Ź���
 * </p>
 * 
 * <p>
 * Description:ְҵҽʦ֤��Ź���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2012.2.2
 * @version 1.0
 */
public class INSDRQualifyLogTool extends TJDOTool{
	   /**
     * ʵ��
     */
    public static INSDRQualifyLogTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelTypeTool
     */
    public static INSDRQualifyLogTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSDRQualifyLogTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSDRQualifyLogTool() {
        setModuleName("ins\\INSDRQualifyLogModule.x");
        onInit();
    }
 
    /**
     * ����ְҵҽʦ֤���
     * @param parm TParm
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        parm.setData("OPT_DATE",StringTool.getString(SystemTool.getInstance().getDate(),
		"yyyy-MM-dd HH:mm:ss"));
        result = update("insertdata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result = update("updateOperatordata", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        }
        return result;
    }
    /**
     * ����ְҵҽʦ֤���
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOperatorData(TParm parm) {
        TParm result = update("updateOperatordata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�û�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOperator(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectOperatordata", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯ�û�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdatabyuser(TParm parm) {
    	TParm result = new TParm();
    	result = query("selectdatabyuser", parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
    /**
     * ��ѯְҵҽʦ֤��Ź�����Ϣ�б�
     * @param parm TParm
     * @return TParm
     */
    public TParm selectdata(TParm parm) {
        TParm result = new TParm();
        result = query("selectdata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
  
}
