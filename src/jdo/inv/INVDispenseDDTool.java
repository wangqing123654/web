package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 * 
 * @author fux
 * @version 1.0
 */
public class INVDispenseDDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static INVDispenseDDTool instanceObject;

    /**
     * ������
     */
    public INVDispenseDDTool() {
        setModuleName("inv\\INVDispenseDDModule.x");
        onInit();
    } 

    /**
     * �õ�ʵ��
     *
     * @return InvVerifyinDTool
     */
    public static INVDispenseDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVDispenseDDTool();
        return instanceObject; 
    }

    /**
     * ��ѯ������Ź���ϸ������
     * @param parm TParm 
     * @return TParm
     */
    public TParm onQuery(TParm parm) {  
        TParm result = this.query("queryDispenseDD", parm); 
        System.out.println("queryDispenseDD =---result"+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;     
        }    
        return result;
    }

    /**
     * ����������Ź���ϸ������
     *
     * @param parm 
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {  
        TParm result = this.update("createDispenseDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /** 
     * ɾ�����ϸ��
     * @param parm TParm 
     * @return TParm
     */
    public TParm onDelete(TParm parm) {  
    	//moduleδ���       
        TParm result = this.update("deleteDispenseinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    

}
