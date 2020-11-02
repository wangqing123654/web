package action.inv;

import com.dongyang.action.TAction;
import jdo.inv.INVTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable    
 * @version 1.0
 */
public class INVDispenseAction
    extends TAction {
    public INVDispenseAction() {
    }

    /**
     * ��ѯ���ⵥ
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryMOut(TParm parm) {
        TParm result = new TParm(); 
        result = INVTool.getInstance().onQueryDispenseMOut(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result; 
    }
 
    /**
     * ��ѯ��ⵥ
     * @param parm TParm 
     * @return TParm 
     */
    public TParm onQueryMIn(TParm parm) { 
        TParm result = new TParm();
        result = INVTool.getInstance().onQueryDispenseMIn(parm);
        System.out.println("��ѯ��ⵥ=="+result); 
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * �������룩���ⵥ
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();  
        TParm result = new TParm(); 
        result = INVTool.getInstance().onCreateDispenseOutorIn(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }   
    
    /**
     * ɾ����ⵥ(����ɾ��)
     * @param parm TParm 
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm(); 
        result = INVTool.getInstance().onDeleteDispense(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
    
    /**
     * ���£��룩���ⵥ
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();  
        TParm result = new TParm(); 
        result = INVTool.getInstance().onUpdateDispenseOut(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }   
    
    
    
    /**
     * ����INV_DISPENSEDD 
     * @param parm TParm  
     * @return TParm 
     */
    public TParm onInsertDD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onCreateDispenseOutorIn(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result; 
    }
     

}

 
