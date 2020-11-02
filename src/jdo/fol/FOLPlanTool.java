package jdo.fol;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:随访方案维护Tool类
 * </p>
 * 
 * <p>
 * Description:随访方案维护Tool类
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
public class FOLPlanTool extends TJDOTool {

	/** 实例对象 */
	private static FOLPlanTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static FOLPlanTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new FOLPlanTool();
		}
		return instanceObject;
	}

	/**
	 * 保存随访计划
	 * 
	 * @param parm
	 * @return
	 */
	public TParm savePlan(TParm parm) {
		String sql = "INSERT INTO FOL_PLAN(PLAN_CODE,PLAN_DESC,ENG_DESC,DEPT_CODE,DEPT_DESC, "
				+ "PY1,SEQ,PLAN_LENGTH,FOL_PURPOSE,START_DATE, "
				+ "END_DATE,STATUS,OPT_USER,OPT_DATE,OPT_TERM) " + "VALUES('"
				+ parm.getValue("PLAN_CODE")
				+ "','"
				+ parm.getValue("PLAN_DESC")
				+ "','"
				+ parm.getValue("ENG_DESC")
				+ "','"
				+ parm.getValue("DEPT_CODE")
				+ "','"
				+ parm.getValue("DEPT_DESC")
				+ "','"
				+ parm.getValue("PY1")
				+ "',"
				+ parm.getInt("SEQ")
				+ ","
				+ parm.getInt("PLAN_LENGTH")
				+ ",'"
				+ parm.getValue("FOL_PURPOSE")
				+ "',TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "','YYYY/MM/DD'),TO_DATE('"
				+ parm.getValue("END_DATE")
				+ "','YYYY/MM/DD'),'"
				+ parm.getValue("STATUS")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',TO_DATE('"
				+ parm.getValue("OPT_DATE")
				+ "','YYYY/MM/DD'),'" + parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 保存随访阶段
	 * 
	 * @param parm
	 * @return
	 */
	public TParm savePlanPhase(TParm parm) {
		String sql = "INSERT INTO FOL_PLAN_PHASE(PHASE_SEQ,PLAN_CODE,PHASE_DESC,DISTANCE_DAYS,DESCRIPTION, "
				+ "OPT_USER,OPT_DATE,OPT_TERM) "
				+ "VALUES ("
				+ parm.getInt("PHASE_SEQ")
				+ ",'"
				+ parm.getValue("PLAN_CODE")
				+ "','"
				+ parm.getValue("PHASE_DESC")
				+ "',"
				+ parm.getInt("DISTANCE_DAYS")
				+ ",'"
				+ parm.getValue("DESCRIPTION")
				+ "','"
				+ parm.getValue("OPT_USER")
				+ "',TO_DATE('"
				+ parm.getValue("OPT_DATE")
				+ "','YYYY/MM/DD'),'"
				+ parm.getValue("OPT_TERM") + "')";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 查询随访方案
	 * 
	 * @param parm
	 * @return
	 */
	public TParm query(TParm parm) {
		String sql = "SELECT SEQ,PLAN_DESC,DEPT_CODE,PLAN_LENGTH,FOL_PURPOSE,"
				+ "PLAN_CODE,ENG_DESC,PY1,STATUS,START_DATE,END_DATE FROM FOL_PLAN ORDER BY SEQ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询随访方案最大SEQ
	 * 
	 * @return
	 */
	public TParm querySeqForPlan() {
		String sql = "SELECT NVL(MAX(SEQ),0) AS SEQ FROM FOL_PLAN";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 根据PLAN_CODE查询对应随访阶段
	 * 
	 * @return
	 */
	public TParm queryPlanPhaseByPlanCode(String planCode) {
		String sql = "SELECT PHASE_SEQ, PLAN_CODE, PHASE_DESC, ENG_DESC, PY1, "
				+ "PY2, DISTANCE_DAYS, DESCRIPTION, OPT_DATE, OPT_TERM,  "
				+ "OPT_USER " + "FROM FOL_PLAN_PHASE " + "WHERE PLAN_CODE = '"
				+ planCode + "' ORDER BY PHASE_SEQ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 更新随访方案
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePlan(TParm parm, TConnection conn) {
		String sql = "UPDATE FOL_PLAN SET PLAN_DESC = '"
				+ parm.getValue("PLAN_DESC") + "', " + "ENG_DESC = '"
				+ parm.getValue("ENG_DESC") + "', " + "PY1 = '"
				+ parm.getValue("PY1") + "', " + "DEPT_CODE = '"
				+ parm.getValue("DEPT_CODE") + "', " + "DEPT_DESC = '"
				+ parm.getValue("DEPT_DESC") + "', " + "PLAN_LENGTH = "
				+ parm.getInt("PLAN_LENGTH") + ", " + "SEQ = "
				+ parm.getInt("SEQ") + ", " + "FOL_PURPOSE = '"
				+ parm.getValue("FOL_PURPOSE") + "', "
				+ "START_DATE = TO_DATE('" + parm.getValue("START_DATE")
				+ "','YYYY/MM/DD'), " + "END_DATE = TO_DATE('"
				+ parm.getValue("END_DATE") + "','YYYY/MM/DD'), "
				+ "STATUS = '" + parm.getValue("STATUS") + "' "
				+ "WHERE PLAN_CODE = '" + parm.getValue("PLAN_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 更新随访阶段
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePlanPhase(TParm parm, TConnection conn) {
		String sql = "UPDATE FOL_PLAN_PHASE SET PHASE_SEQ = "
				+ parm.getInt("PHASE_SEQ_UPDATE") + ", " + "PHASE_DESC = '"
				+ parm.getValue("PHASE_DESC") + "', " + "DISTANCE_DAYS = "
				+ parm.getInt("DISTANCE_DAYS") + ", " + "DESCRIPTION = '"
				+ parm.getValue("DESCRIPTION") + "' " + "WHERE PLAN_CODE = '"
				+ parm.getValue("PLAN_CODE") + "' " + "AND PHASE_SEQ = "
				+ parm.getValue("PHASE_SEQ") + "";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	/**
	 * 判断数据存在与否
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryPlanPhaseByIndex(TParm parm) {
		String sql = "SELECT COUNT(PLAN_CODE) AS COUNTS FROM FOL_PLAN_PHASE WHERE PLAN_CODE = '"
				+ parm.getValue("PLAN_CODE")
				+ "' AND PHASE_SEQ = '"
				+ parm.getValue("PHASE_SEQ") + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
}
