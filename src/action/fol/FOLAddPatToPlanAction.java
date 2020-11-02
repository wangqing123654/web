package action.fol;

import jdo.fol.FOLAddPatToPlanTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:选择随访方案Action类
 * </p>
 * 
 * <p>
 * Description:选择随访方案Action类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-02-28
 * @version 1.0
 */
public class FOLAddPatToPlanAction extends TAction {

	/**
	 * 保存患者随访记录和阶段日程
	 */
	public TParm onSavePatFolRecord(TParm parm) {
		TParm recordParm = parm.getParm("RECORDPARM");
		TParm phaseParm = parm.getParm("PHASEPARM");
		TConnection conn = this.getConnection();
		TParm result = FOLAddPatToPlanTool.getInstance().savePatRecord(
				recordParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		for (int i = 0; i < phaseParm.getCount(); i++) {
			result = FOLAddPatToPlanTool.getInstance().savePatFolSchdule(
					phaseParm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

}
