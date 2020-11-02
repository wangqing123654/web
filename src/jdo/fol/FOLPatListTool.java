package jdo.fol;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:随访患者列表Tool类
 * </p>
 * 
 * <p>
 * Description:随访患者列表Tool类
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
 * @author shendr 2014-03-19
 * @version 1.0
 */
public class FOLPatListTool extends TJDOTool {

	/** 实例对象 */
	private static FOLPatListTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static FOLPatListTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new FOLPatListTool();
		}
		return instanceObject;
	}

	/**
	 * 查询随访方案
	 * 
	 * @return
	 */
	public TParm queryFolPlan() {
		String sql = "SELECT '' AS ID,'' AS NAME FROM DUAL UNION ALL "
				+ "SELECT PLAN_CODE AS ID,PLAN_DESC AS NAME FROM FOL_PLAN";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询患者列表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryPatList(TParm parm) {
		String dept_code = parm.getValue("DEPT_CODE");
		String start_date = parm.getValue("START_DATE");
		String end_date = parm.getValue("END_DATE");
		String plan_code = parm.getValue("PLAN_CODE");
		String mr_no = parm.getValue("MR_NO");
		String status = parm.getValue("STATUS");
		String sql = "SELECT A.MR_NO,C.PAT_NAME,B.DEPT_CODE,A.STATUS,A.CASE_NO,A.ADM_TYPE,A.ADM_DATE, "
				+ "D.USER_NAME AS RECORD_DOCTOR,E.EXPECTED_FOL_DATE AS NEXT_FOL_DATE,A.PLAN_CODE "
				+ "FROM FOL_PAT_RECORD A,FOL_PLAN B,SYS_PATINFO C,SYS_OPERATOR D,FOL_PAT_PHASE E "
				+ "WHERE 1=1 ";
		if (!StringUtil.isNullString(status)) {
			sql += "AND A.STATUS = '" + status + "' ";
		}
		if (!StringUtil.isNullString(plan_code)) {
			sql += "AND A.PLAN_CODE = '" + plan_code + "' ";
		}
		if (!StringUtil.isNullString(mr_no)) {
			sql += "AND A.MR_NO = '" + mr_no + "' ";
		}
		if (!StringUtil.isNullString(dept_code)) {
			sql += "AND B.DEPT_CODE = '" + dept_code + "' ";
		}
		sql += "AND A.PLAN_CODE = B.PLAN_CODE " + "AND A.MR_NO = C.MR_NO "
				+ "AND A.FIRST_DOCTOR = D.USER_ID "
				+ "AND A.PLAN_CODE = E.PLAN_CODE " + "AND A.MR_NO = E.MR_NO "
				+ "AND E.EXPECTED_FOL_DATE = (SELECT MIN(F.EXPECTED_FOL_DATE) "
				+ "                             FROM FOL_PAT_PHASE F "
				+ "                            WHERE F.STATUS <> '2' "
				+ "                          AND F.PLAN_CODE = A.PLAN_CODE "
				+ "                          AND F.MR_NO = A.MR_NO)";
		if (!StringUtil.isNullString(start_date)
				&& !StringUtil.isNullString(end_date)) {
			sql += "AND E.EXPECTED_FOL_DATE BETWEEN TO_DATE('" + start_date
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('" + end_date
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 结束随访
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateStatus(TParm parm) {
		String sql = "UPDATE FOL_PAT_RECORD SET STATUS = '2' "
				+ "WHERE MR_NO = '" + parm.getValue("MR_NO") + "' "
				+ "  AND PLAN_CODE = '" + parm.getValue("PLAN_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().update(sql));
	}

	/**
	 * 查询随访日程
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryPatPhase(TParm parm) {
		String sql = "SELECT CASE WHEN A.FOL_DATE IS NULL THEN EXPECTED_FOL_DATE ELSE FOL_DATE END AS FOL_DATE, "
				+ "A.MR_NO,A.STATUS,C.PAT_NAME,B.PHASE_DESC,A.PLAN_CODE,A.CASE_NO, "
				+ " CASE WHEN A.STATUS = '0' OR A.STATUS = '1' THEN 'N' ELSE 'Y' END AS STATUS_FOR_TABLE "
				+ " FROM FOL_PAT_PHASE A,FOL_PLAN_PHASE B,SYS_PATINFO C "
				+ " WHERE A.MR_NO = '"
				+ parm.getValue("MR_NO")
				+ "' "
				+ "  AND A.PLAN_CODE = '"
				+ parm.getValue("PLAN_CODE")
				+ "' "
				+ "  AND A.PLAN_CODE = B.PLAN_CODE "
				+ " AND A.PHASE_SEQ = B.PHASE_SEQ "
				+ "  AND A.MR_NO = C.MR_NO"
				+ " ORDER BY A.PHASE_SEQ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
