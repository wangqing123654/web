package action.spc;

import jdo.spc.SPCCabinetTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;


/**
 * <p>
 * Title: ���ܹ�Action
 * </p>
 *
 * <p>
 * Description: ���ܹ�Action
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
 * @author fuwj 2012.11.14
 * @version 1.0
 */

public class SPCCabinetAction extends TAction {
	
	/**
	 * �������ܹ������Ϣ
	 * @param parm
	 * @return
	 */
	public TParm onSaveCabinet(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCCabinetTool.getInstance().onSaveCabinet(parm,conn);
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
