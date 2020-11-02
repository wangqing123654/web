package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INV_AssignSysParmTool extends TJDOTool {
	  /**
     * ʵ��
     */
    public static INV_AssignSysParmTool instanceObject;

    /**
     * ������  
     */
    public INV_AssignSysParmTool() { 
        setModuleName("inv\\INV_AssignSysParmModule.x");
        onInit();
    }   

    /**
     * �õ�ʵ��
     * 
     * @return INV_AssignSysParmTool 
     */
    public static INV_AssignSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INV_AssignSysParmTool();
        return instanceObject;
    }

    /**
     * ����
     * @param parm TParm
     * @param conn TConnection 
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /** 
     * ����Inv_sysParm
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */   
    public TParm onUpdateSysParm(TParm parm, TConnection conn) {
       TParm result = this.update("updatesysparm", parm, conn); 
       System.out.println("TOOl--result"+result);  
       if (result.getErrCode() < 0) {  
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName()); 
           return result;
       } 
       return result; 
   }
   /**  
     * ɾ��
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
       TParm result = this.update("delete", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
}
