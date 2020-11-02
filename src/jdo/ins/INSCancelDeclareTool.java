package jdo.ins;


import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * <p>
 * Title: ȡ���걨
 * </p>
 * 
 * <p>
 * Description:ȡ���걨
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
public class INSCancelDeclareTool extends TJDOTool{
	   /**
     * ʵ��
     */
    public static INSCancelDeclareTool instanceObject;
    /**
     * �õ�ʵ��
     * @return PanelTypeTool
     */
    public static INSCancelDeclareTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSCancelDeclareTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public INSCancelDeclareTool() {
        setModuleName("ins\\INSCancelDeclareModule.x");
        onInit();
    }

 
    /**
     * ��ѯȡ���걨��Ϣ�б�
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
    /**
     * ����INSStatus
     * @param parm TParm
     * @return TParm
     */
    public TParm updateINSStatus(TParm parm) {
        TParm result = new TParm();
        result = update("updateINSStatus", parm);
        //System.out.println(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * ����UploadFlg
     * @param parm TParm
     * @return TParm
     */
    public TParm updateUploadFlg(TParm parm) {
    	TParm result = new TParm();
    	result = update("updateUploadFlg", parm);
    	//System.out.println(parm);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText() +
    				result.getErrName());
    		return result;
    	}
    	return result;
    }
}
