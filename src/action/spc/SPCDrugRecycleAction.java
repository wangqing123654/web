package action.spc;

import jdo.spc.SPCDrugRecycleTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: �����龫��ƿ����Action
 * </p>
 * 
 * <p>
 * Description: �����龫��ƿ����Action
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
 * @author shendr 2013.07.18
 * @version 1.0
 */
public class SPCDrugRecycleAction extends TAction {

	/**
	 * ��ƿ���շ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onRecover(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCDrugRecycleTool.getInstance().updateRecover(parm,
				conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		result = SPCDrugRecycleTool.getInstance().deleteToxic(parm, conn);
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
