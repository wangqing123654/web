package jdo.fol;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:选择随访方案Tool类
 * </p>
 * 
 * <p>
 * Description:选择随访方案Tool类
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
public class FOLAddPatToPlanTool extends TJDOTool {

	/** 实例对象 */
	private static FOLAddPatToPlanTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static FOLAddPatToPlanTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new FOLAddPatToPlanTool();
		}
		return instanceObject;
	}

	/**
	 * 选择患者加入随访
	 * 
	 * @return
	 */
	public TParm savePatRecord(TParm parm, TConnection conn) {
		String sql = "INSERT INTO FOL_PAT_RECORD "
				+ "(MR_NO, CASE_NO, PLAN_CODE, START_DATE, END_DATE, STATUS, "
				+ " FIRST_DOCTOR, ADM_TYPE, ADM_DATE, IN_TYPE, OPT_DATE, OPT_USER, "
				+ "OPT_TERM) " + "VALUES " + "('"
				+ parm.getValue("MR_NO")
				+ "', '"
				+ parm.getValue("CASE_NO")
				+ "', '"
				+ parm.getValue("PLAN_CODE")
				+ "', TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "', 'YYYY/MM/DD HH24:MI:SS'), TO_DATE('"
				+ parm.getValue("END_DATE")
				+ "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ parm.getValue("STATUS")
				+ "', '"
				+ parm.getValue("FIRST_DOCTOR")
				+ "', '"
				+ parm.getValue("ADM_TYPE")
				+ "', TO_DATE('"
				+ parm.getValue("ADM_DATE")
				+ "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ parm.getValue("IN_TYPE")
				+ "', TO_DATE('"
				+ parm.getValue("OPT_DATE")
				+ "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ parm.getValue("OPT_USER")
				+ "', '"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 保存患者随访阶段日程
	 */
	public TParm savePatFolSchdule(TParm parm, TConnection conn) {
		String sql = "INSERT INTO FOL_PAT_PHASE "
				+ "(MR_NO, CASE_NO, PHASE_SEQ, PLAN_CODE, EXPECTED_FOL_DATE,  "
				+ " OPT_DATE, OPT_USER, OPT_TERM) " + "VALUES " + "  ('"
				+ parm.getValue("MR_NO") + "', '" + parm.getValue("CASE_NO")
				+ "'," + parm.getValue("PHASE_SEQ") + ", '"
				+ parm.getValue("PLAN_CODE") + "', TO_DATE('"
				+ parm.getValue("EXPECTED_FOL_DATE")
				+ "', 'YYYY/MM/DD HH24:MI:SS'),  TO_DATE('"
				+ parm.getValue("OPT_DATE") + "', 'YYYY/MM/DD HH24:MI:SS'), '"
				+ parm.getValue("OPT_USER") + "', " + " '"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 查询随访方案
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryPlan(TParm parm) {
		String plan_code = parm.getValue("PLAN_CODE");
		String sql = "SELECT SEQ,PLAN_DESC,DEPT_CODE,PLAN_LENGTH,FOL_PURPOSE,"
				+ "PLAN_CODE,START_DATE,END_DATE FROM FOL_PLAN ";
		if (!StringUtil.isNullString(plan_code)) {
			sql += " WHERE PLAN_CODE='" + plan_code + "' ";
		}
		sql += "ORDER BY SEQ ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询随访阶段
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryPlanPhase(TParm parm) {
		String plan_code = parm.getValue("PLAN_CODE");
		String sql = "SELECT PHASE_SEQ, PLAN_CODE, PHASE_DESC, ENG_DESC, PY1, "
				+ "PY2, DISTANCE_DAYS, DESCRIPTION, OPT_DATE, OPT_TERM, OPT_USER "
				+ "FROM FOL_PLAN_PHASE ";
		if (!StringUtil.isNullString(plan_code)) {
			sql += " WHERE PLAN_CODE='" + plan_code + "' ";
		}
		sql += "ORDER BY PHASE_SEQ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
}
