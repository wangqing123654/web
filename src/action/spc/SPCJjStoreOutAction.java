package action.spc;

import java.util.List;                         
import java.util.Set;

import jdo.spc.INDTool;
import jdo.spc.SPCJjStoreOutTool;                                        
  
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: ��������Action
 * </p>
 *
 * <p>
 * Description: ��������Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fuwj 2013.5.31
 * @version 1.0
 */
public class SPCJjStoreOutAction extends TAction {
   
	/**
	 * �龫��������Action
	 * @param parm
	 * @return
	 */
	public TParm JjStockOut(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCJjStoreOutTool.getInstance().JjStockOut(parm, conn);
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
     * �������
     *
     * @param parm 
     *            TParm
     * @return TParm
     */
    public TParm onInsertIn(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onInsertDispenseIn(parm, conn);
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
