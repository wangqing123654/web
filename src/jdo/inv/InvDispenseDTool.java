package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author  fux  
 * @version 1.0
 */
public class InvDispenseDTool
    extends TJDOTool {
    /**
     * ʵ��
     */
    public static InvDispenseDTool instanceObject;

    /**
     * ������
     */
    public InvDispenseDTool() {
        setModuleName("inv\\INVDispenseDModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return InvDispenseDTool
     */
    public static InvDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvDispenseDTool();
        return instanceObject; 
    }

    /**
     * �������ⵥϸ��
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * ��ѯ���ⵥϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseDOut(TParm parm) {
        TParm result = this.query("queryDispenseDOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * ��ѯ��Ҫ���ĳ��ⵥϸ��
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryDispenseDOI(TParm parm) { 
        TParm result = this.query("queryDispenseDOI", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**  
     * ��ѯ���ⵥϸ��
     * @param parm TParm
     * @return TParm 
     */
    public TParm onQueryDispenseDIn(TParm parm) {                
    	System.out.println("��ѯ���ⵥϸ��");    
        TParm result = this.query("queryDispenseDIn", parm);
        System.out.println("��ѯ���ⵥϸ��result"+result);  
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    
    /**
     * ��ѯ���ⵥ��ϸ(ֻ��D��)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseD(TParm parm) { 
        TParm result = this.query("queryDispenseD", parm);
        System.out.println("queryDispenseD--result"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;  
    }
                  

    /**
     * ���³��ⵥϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDispenseD(TParm parm) {  
        TParm result = this.query("onUpdateDispenseD", parm);
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
