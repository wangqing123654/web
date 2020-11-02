package action.fol;

import jdo.fol.FOLPlanTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title:随访方案维护Action类
 * </p>
 * 
 * <p>
 * Description:随访方案维护Action类
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
 * @author shendr 2014-02-24
 * @version 1.0
 */
public class FOLPlanAction extends TAction {

	/**
	 * 更新随访方案及阶段
	 */
	public TParm onUpdate(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = new TParm();
		// 更新随访方案
		TParm planParm = parm.getParm("PLAN_PARM");
		result = FOLPlanTool.getInstance().updatePlan(planParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// 更新随访阶段
		TParm planPhaseParm = parm.getParm("PLAN_PHASE_PARM");
		for (int i = 0; i < planPhaseParm.getCount(); i++) {
			result = FOLPlanTool.getInstance().updatePlanPhase(
					planPhaseParm.getRow(i), conn);
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
