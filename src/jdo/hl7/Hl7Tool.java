package jdo.hl7;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.exception.HL7Exception;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version 1.0
 */
public class Hl7Tool extends TJDODBTool {
	/**
	 * 实例
	 */
	public static Hl7Tool instanceObject;

	/**
	 * 构造器
	 */
	public Hl7Tool() {

	}

	/**
	 * 得到实例
	 * 
	 * @return IBSTool
	 */
	public static Hl7Tool getInstance() {
		if (instanceObject == null)
			instanceObject = new Hl7Tool();
		return instanceObject;
	}

	/**
	 * 得到当前医院的名称 SYS_REGION表 REGION_CODE字段 条件是MAIN_FLG='Y'
	 * 
	 * @return TParm
	 */
	public TParm getRegionCode() {
		TParm result = new TParm(getDBTool().select(
				"SELECT REGION_CODE FROM SYS_REGION WHERE MAIN_FLG='Y'"));
		return result;
	}

	/**
	 * 医嘱医嘱
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getodiOrderData(TParm parm) {
		String sql = "SELECT A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC,B.RX_KIND,B.LINK_NO,A.DOSAGE_UNIT,A.DOSAGE_QTY,A.DOSE_TYPE, CASE A.DOSE_TYPE"
				+ " WHEN NULL "
				+ "    THEN '' "
				+ " ELSE (SELECT c.chn_desc FROM SYS_DICTIONARY C WHERE   A.DOSE_TYPE = c.ID "
				+ "   AND C.GROUP_ID = 'SYS_DOSETYPE') "
				+ " END AS dose_typedesc,"
				+ " A.ORDER_DATE,D.START_DATE,A.NS_EXEC_DATE,A.DC_DATE,B.MED_APPLY_NO ,"
				+ " A.CAT1_TYPE,A.FREQ_CODE,A.ROUTE_CODE,B.ACUMDSPN_QTY, A.MEDI_QTY, A.MEDI_UNIT "
				+ " FROM ODI_DSPNM A,ODI_ORDER B,SYS_FEE_HISTORY D"
				+ " WHERE A.CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "'"
				+ " AND A.ORDER_NO='"
				+ parm.getValue("ORDER_NO")
				+ "'"
				+ " AND A.ORDER_SEQ='"
				+ parm.getValue("ORDER_SEQ")
				+ "'"
				+ " AND A.CASE_NO=B.CASE_NO"
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND A.ORDER_CODE=D.ORDER_CODE"
				+ " AND A.ORDER_DATE>=TO_DATE(D.START_DATE,'YYYYMMDDHH24MISS')"
				+ " AND A.ORDER_DATE<=TO_DATE(D.END_DATE,'YYYYMMDDHH24MISS') ";
		TParm result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 门急诊
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getopdorderParm(TParm parm) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM OPD_ORDER WHERE ADM_TYPE='"
						+ parm.getValue("ADM_TYPE") + "' AND CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND RX_NO='"
						+ parm.getValue("RX_NO") + "' AND SEQ_NO='"
						+ parm.getValue("SEQ_NO") + "'"));
		return result;
	}

	/**
	 * 住院
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getodiorderParm(TParm parm) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM ODI_ORDER WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND ORDER_NO='"
						+ parm.getValue("ORDER_NO") + "' AND ORDER_SEQ='"
						+ parm.getValue("ORDER_SEQ") + "'"));
		return result;
	}

	/**
	 * 健检
	 * 
	 * @param parm
	 * @return
	 */
	public TParm gethrmorderParm(TParm parm) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM HRM_ORDER WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " AND SEQ_NO="
						+ parm.getValue("SEQ_NO")));
		return result;
	}

	/**
	 * 得到MED数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMedData(TParm parm, Map map) {
		TParm result = new TParm();
		Iterator it = map.values().iterator();
		StringBuffer orderNoSeq = new StringBuffer();
		while (it.hasNext()) {
			String orderline = (String) it.next();
			if (orderNoSeq.toString().length() > 0)
				orderNoSeq.append(",");
			orderNoSeq.append("'" + orderline + "'");
		}
		String line = "";
		if (map.size() > 0) {
			line = " AND A.ORDER_NO||A.SEQ_NO IN(" + orderNoSeq.toString()
					+ ")";
		}
		String sql = "";
		if (parm.getValue("FLG").equals("0")) {
			if (parm.getValue("ADM_TYPE").equals("O")
					|| parm.getValue("ADM_TYPE").equals("E")) {
				sql = "SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
						+ parm.getValue("CAT1_TYPE")
						+ "' AND A.APPLICATION_NO='"
						+ parm.getValue("LAB_NO")
						+ "' AND A.ADM_TYPE='"
						+ parm.getValue("ADM_TYPE")
						+ "' AND A.BILL_FLG='Y'" + line;
				result = new TParm(getDBTool().select(sql));
			} else {
				sql = "SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
						+ parm.getValue("CAT1_TYPE")
						+ "' AND A.APPLICATION_NO='"
						+ parm.getValue("LAB_NO")
						+ "' AND A.ADM_TYPE='"
						+ parm.getValue("ADM_TYPE")
						+ "' " + line;
				result = new TParm(getDBTool().select(sql));
			}
		} else {
			sql = "SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND A.CAT1_TYPE='"
					+ parm.getValue("CAT1_TYPE")
					+ "' AND A.APPLICATION_NO='"
					+ parm.getValue("LAB_NO")
					+ "' AND ADM_TYPE='"
					+ parm.getValue("ADM_TYPE") + "'" + line;
			result = new TParm(getDBTool().select(sql));
		}
		// System.out.println("=========================="+sql);
		return result;
	}

	/**
	 * 得到标本送检数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getLisSendData(TParm parm) {
		TParm result = new TParm();
		String sql = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,B.NS_EXEC_CODE_REAL,B.NS_EXEC_DATE_REAL,C.LIS_RE_USER,C.LIS_RE_DATE,C.APPLICATION_NO,A.ORDER_CAT1_CODE FROM ODI_DSPNM A,ODI_DSPND B,MED_APPLY C"
				+ " WHERE A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE||B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
				+ " AND A.CASE_NO=C.CASE_NO "
				+ " AND A.CAT1_TYPE=C.CAT1_TYPE "
				+ " AND A.ORDER_NO=C.ORDER_NO " + " AND A.ORDER_SEQ=C.SEQ_NO";
		if (!parm.getValue("CASE_NO").equals("")) {
			sql += " AND A.CASE_NO='" + parm.getValue("CASE_NO") + "'";
		}
		if (!parm.getValue("ORDER_NO").equals("")) {
			sql += " AND A.ORDER_NO='" + parm.getValue("ORDER_NO") + "'";
		}
		if (!parm.getValue("SEQ_NO").equals("")) {
			sql += " AND A.ORDER_SEQ='" + parm.getValue("SEQ_NO") + "'";
		}
		if (!parm.getValue("LAB_NO").equals("")) {
			sql += " AND C.APPLICATION_NO='" + parm.getValue("LAB_NO") + "'";
		}
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 得到病患基本信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getPatInfo(TParm parm) throws HL7Exception {
		// System.out.println("SQL=="+"SELECT * FROM SYS_PATINFO WHERE MR_NO='"+parm.getValue("MR_NO")+"'");
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM SYS_PATINFO WHERE MR_NO='"
						+ parm.getValue("MR_NO") + "'"));
		return result;
	}

	/**
	 * 得到HL7信息门诊参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getODOParm(TParm parm) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM REG_PATADM A,SYS_CTZ B WHERE CASE_NO = '"
						+ parm.getValue("CASE_NO")
						+ "' AND A.CTZ1_CODE=B.CTZ_CODE(+)"));
		return result;
	}

	/**
	 * 得到HL7信息住院参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getODIParm(TParm parm) {
		/*
		 * TParm result = new TParm(
		 * this.select("SELECT * FROM ADM_INP A,SYS_CTZ B WHERE CASE_NO='" +
		 * parm.getValue("CASE_NO") + "' AND A.CTZ1_CODE=B.CTZ_CODE"));
		 */
		// liuf
		TParm result = new TParm(getDBTool().select(
				" SELECT * FROM ADM_INP A,SYS_CTZ B,  SYS_BED C "
						+ " WHERE A.CASE_NO='" + parm.getValue("CASE_NO")
						+ "' " + " AND A.CTZ1_CODE=B.CTZ_CODE(+)"
						+ " AND A.BED_NO= C.BED_NO(+)"));
		return result;
	}

	/**
	 * 得到HL7健康检查参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getHRMParm(TParm parm) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM HRM_PATADM WHERE CASE_NO='"
						+ parm.getValue("CASE_NO") + "'"));
		return result;
	}

	/**
	 * 返回医令细分类
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getSysOrderCat1(String orderCat1Code) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"
						+ orderCat1Code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}

	/**
	 * 返回报告类别
	 * 
	 * @param code
	 *            String
	 * @return TParm
	 */
	public TParm getEXMRule(String type, String code) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE = '" + type
						+ "' AND CATEGORY_CODE = '" + code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}

	/**
	 * 拿到诊断名称
	 * 
	 * @param code
	 *            String
	 * @return TParm
	 */
	public TParm getICDData(String code) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM SYS_DIAGNOSIS WHERE ICD_CODE='" + code + "'"));
		if (result.getCount() <= 0) {
			return result;
		}
		return result.getRow(0);
	}

	/**
	 * 门急诊诊断
	 * 
	 * @param caseNo
	 *            String
	 * @param icdType
	 *            String
	 * @param mainFlg
	 *            String
	 * @return TParm
	 */
	public TParm opdDiagrec(String caseNo, String admType, String mainFlg) {
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM OPD_DIAGREC WHERE CASE_NO='" + caseNo
						+ "' AND ADM_TYPE='" + admType
						+ "' AND MAIN_DIAG_FLG='" + mainFlg + "'"));
		return result;
	}

	/**
	 * 住院诊断
	 * 
	 * @param caseNo
	 *            String
	 * @param icdType
	 *            String
	 * @param mainFlg
	 *            String
	 * @return TParm
	 */
	public TParm odiDiagrec(String caseNo, String ioType, String mainFlg) {
		String str = "";
		// 20120918 取adm_inp 中最新的诊断码 shibl
		TParm result = new TParm(getDBTool().select(
				"SELECT * FROM ADM_INPDIAG WHERE CASE_NO='" + caseNo
						+ "' AND IO_TYPE='" + ioType + "' AND MAINDIAG_FLG='"
						+ mainFlg + "'"));
		return result;
	}

	/**
	 * 住院最新诊断
	 * 
	 * @param caseNo
	 * @param mainFlg
	 * @return
	 */
	public TParm odiNewDiagrec(String caseNo, String mainFlg) {
		TParm result = new TParm(getDBTool().select(
				"SELECT IO_TYPE,ICD_CODE,DESCRIPTION "
						+ "FROM ADM_INPDIAG WHERE CASE_NO='" + caseNo
						+ "' AND MAINDIAG_FLG='" + mainFlg + "' "
						+ "AND IO_TYPE IN('I','M','O') ORDER BY IO_TYPE DESC"));
		if (result.getCount() <= 0) {
			return result;
		}
		String ioType = result.getValue("IO_TYPE", 0);
		// 只有门急诊诊断直接返回
		if (ioType.equals("I")) {
			return result;
		}
		TParm reParm = new TParm();
		for (int i = 0; i < result.getCount(); i++) {
			TParm parmRow = result.getRow(i);
			if (ioType.equals(parmRow.getValue("IO_TYPE"))) {
				reParm.addData("IO_TYPE", parmRow.getValue("IO_TYPE"));
				reParm.addData("ICD_CODE", parmRow.getValue("ICD_CODE"));
				reParm.addData("DESCRIPTION", parmRow.getValue("DESCRIPTION"));
			}
		}
		reParm.setCount(reParm.getCount("IO_TYPE"));
		return reParm;
	}

	/**
	 * 拿到用户名
	 * 
	 * @param userID
	 *            String
	 * @return String
	 */
	public String getOperatorName(String userID) {
		TParm parm = new TParm(getDBTool().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
						+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 更新状态
	 * 
	 * @param caseNo
	 *            String
	 * @param labNo
	 *            String
	 * @return boolean
	 */
	public boolean updateMedApply(String caseNo, String labNo, String flg,
			String admType, Map map) {
		String status = "1";
		if ("1".equals(flg))
			status = "9";
		Iterator it = map.values().iterator();
		StringBuffer orderNoSeq = new StringBuffer();
		while (it.hasNext()) {
			String orderline = (String) it.next();
			if (orderNoSeq.toString().length() > 0)
				orderNoSeq.append(",");
			orderNoSeq.append("'" + orderline + "'");
		}
		TParm result = new TParm();
		if (admType.equals("O") || admType.equals("E")) {
			if ("1".equals(flg))
				result = new TParm(// ==pangben 2013-7-23 修改sql,门诊检验检查退费出现报错
						getDBTool().update(
								"UPDATE MED_APPLY SET STATUS = '" + status
										+ "',SEND_FLG='1' WHERE CASE_NO='"
										+ caseNo + "' AND APPLICATION_NO='"
										+ labNo + "' AND BILL_FLG='N'"
										+ " AND ORDER_NO||SEQ_NO IN("
										+ orderNoSeq.toString() + ")"));
			else
				result = new TParm(getDBTool().update(
						"UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo
								+ "' AND ORDER_NO||SEQ_NO IN("
								+ orderNoSeq.toString() + ")"));

		} else {
			if ("1".equals(flg))
				result = new TParm(getDBTool().update(
						"UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo
								+ "' AND ORDER_NO||SEQ_NO IN("
								+ orderNoSeq.toString() + ")"));
			else
				result = new TParm(getDBTool().update(
						"UPDATE MED_APPLY SET STATUS = '" + status
								+ "',SEND_FLG='1' WHERE CASE_NO='" + caseNo
								+ "' AND APPLICATION_NO='" + labNo
								+ "' AND ORDER_NO||SEQ_NO IN("
								+ orderNoSeq.toString() + ")"));// shibl
		}
		if (result.getErrCode() < 0) {
			System.out.println("updateMedApply(" + caseNo + "," + labNo
					+ ") ERR:" + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 拿到集合医嘱细项
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderSet(String orderCode) {
		TParm result = new TParm(
				getDBTool()
						.select("SELECT B.OWN_PRICE,A.DOSAGE_QTY,A.ORDER_CODE,B.ORDER_DESC FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDERSET_CODE='"
								+ orderCode + "'"));
		return result;
	}

	/**
	 * 手术数据
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm getOpeCisData(String opbookSeq) {
		String sql = " SELECT E.OP_RECORD_NO,A.OPBOOK_SEQ AS OPBOOK_NO,A.OP_CODE1,B.OPT_CHN_DESC,A.ANA_CODE,D.CHN_DESC AS ANA_DESC,A.ANA_USER1,C1.USER_NAME AS ANA_USER1DESC,A.ANA_USER2,"
				+ "  C2.USER_NAME AS ANA_USER2DESC,A.CIRCULE_USER1,C3.USER_NAME AS CIRCULE_USER1DESC,A.CIRCULE_USER2,C4.USER_NAME AS CIRCULE_USER2DESC,A.CIRCULE_USER3,C5.USER_NAME "
				+ " AS CIRCULE_USER3DESC,A.CIRCULE_USER4,C6.USER_NAME AS CIRCULE_USER4DESC,A.SCRUB_USER1,C7.USER_NAME AS SCRUB_USER1DESC,A.SCRUB_USER2,C8.USER_NAME "
				+ " AS SCRUB_USER2DESC,A.SCRUB_USER3,C9.USER_NAME AS SCRUB_USER3DESC,A.SCRUB_USER4,C10.USER_NAME AS SCRUB_USER4DESC,A.TIME_NEED AS OPE_TIME"
				+ " FROM OPE_OPBOOK A,SYS_OPERATIONICD B,SYS_OPERATOR C1,SYS_OPERATOR C2,SYS_OPERATOR C3,SYS_OPERATOR C4,SYS_OPERATOR C5,SYS_OPERATOR C6,"
				+ " SYS_OPERATOR C7,SYS_OPERATOR C8,SYS_OPERATOR C9,SYS_OPERATOR C10,SYS_DICTIONARY D,OPE_OPDETAIL E "
				+ " WHERE A.OPBOOK_SEQ='"
				+ opbookSeq
				+ "'"
				+ " AND A.OP_CODE1 = B.OPERATION_ICD"
				+ " AND A.ANA_USER1=C1.USER_ID(+) "
				+ " AND A.ANA_USER2=C2.USER_ID(+)"
				+ " AND A.CIRCULE_USER1=C3.USER_ID(+)"
				+ " AND A.CIRCULE_USER2=C4.USER_ID(+)"
				+ " AND A.CIRCULE_USER3=C5.USER_ID(+)"
				+ " AND A.CIRCULE_USER4=C6.USER_ID(+) "
				+ " AND A.SCRUB_USER1=C7.USER_ID(+) "
				+ " AND A.SCRUB_USER2=C8.USER_ID(+) "
				+ " AND A.SCRUB_USER3=C9.USER_ID(+) "
				+ " AND A.SCRUB_USER4=C10.USER_ID(+) "
				+ " AND A.ANA_CODE=D.ID(+) "
				+ " AND D.GROUP_ID='OPE_ANAMETHOD' "
				+ " AND A.OPBOOK_SEQ=E.OPBOOK_NO(+) ";
		// System.out.println("sql------------------------" + sql);
		TParm result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 手术名称
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm getOpeData(String caseNo) {
		TParm result = new TParm(
				getDBTool()
						.select("SELECT B.OPERATION_ICD,B.OPT_CHN_DESC FROM OPE_OPDETAIL A,SYS_OPERATIONICD B WHERE A.OP_RECORD_NO IN (SELECT MAX(OP_RECORD_NO) FROM OPE_OPDETAIL WHERE CASE_NO = '"
								+ caseNo
								+ "') AND A.OP_CODE1 = B.OPERATION_ICD"));
		return result;
	}

	/**
	 * 拿到科室名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm result = new TParm(getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("DEPT_CHN_DESC", 0);
	}

	// 2019-3-25 chenyl 增加拿到医生名称
	/**
	 * 拿到医生名称
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getDrName(String code) {
		TParm result = new TParm(
				getDBTool().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
						+ code + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("USER_NAME", 0);
	}

	/**
	 * 拿到诊间名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getClinicRoomDesc(String clinicCode) {
		TParm result = new TParm(getDBTool().select(
				"SELECT CLINICROOM_DESC FROM REG_CLINICROOM WHERE CLINICROOM_NO='"
						+ clinicCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("CLINICROOM_DESC", 0);
	}

	/**
	 * 拿到病房名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getRoomDesc(String roomCode) {
		TParm result = new TParm(getDBTool().select(
				"SELECT ROOM_DESC FROM SYS_ROOM WHERE ROOM_CODE='" + roomCode
						+ "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("ROOM_DESC", 0);
	}

	/**
	 * 拿到病房名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm result = new TParm(getDBTool().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		if (result.getErrCode() != 0) {
			return "";
		}
		return result.getValue("STATION_DESC", 0);
	}

	/**
	 * 得到过敏史数据
	 * 
	 * @param mrNo
	 *            String
	 * @return TParm
	 */
	public TParm getDrugAllErgy(String mrNo) {
		TParm result = new TParm(
				getDBTool()
						.select(" SELECT ROWNUM AS ID,ADM_DATE,DRUG_TYPE,DRUGORINGRD_CODE,ALLERGY_NOTE,DEPT_CODE,DR_CODE,ADM_TYPE,CASE_NO,MR_NO,OPT_USER,OPT_DATE,OPT_TERM FROM OPD_DRUGALLERGY WHERE MR_NO='"
								+ mrNo + "' ORDER BY ADM_DATE "));
		return result;
	}

	/**
	 * 拿到名称
	 * 
	 * @param type
	 *            String
	 * @param code
	 *            String
	 * @return String
	 */
	public String getDrugTypeName(String type, String code) {
		String name = "";
		if ("A".equals(type)) {
			TParm p = new TParm(getDBTool().select(
					"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT' AND ID='"
							+ code + "' ORDER BY ID"));
			name = p.getValue("CHN_DESC", 0);
		}
		if ("B".equals(type)) {
			TParm p = new TParm(getDBTool().select(
					"SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='" + code
							+ "'"));
			name = p.getValue("ORDER_DESC", 0);
		}
		if ("C".equals(type)) {
			TParm p = new TParm(getDBTool().select(
					"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE' AND ID='"
							+ code + "' ORDER BY ID"));
			name = p.getValue("CHN_DESC", 0);
		}
		return name;
	}

	/**
	 * 根据条码查询医嘱 过滤条件待定
	 * 
	 * @param ApplyNo
	 * @return
	 */
	public TParm getOrder(String ApplyNo, String Cat1Type, String ExecDept)
			throws HL7Exception {
		TParm result = new TParm();
		String execdept = "";
		if (!ExecDept.equals("") && !ExecDept.contains("|")) {
			execdept = " AND A.EXEC_DEPT_CODE='" + ExecDept + "'";
		} else if (ExecDept.contains("|")) {
			String[] ExceDeptStr = StringTool.parseLine(ExecDept, "|");
			String inCon = "";
			for (int i = 0; i < ExceDeptStr.length; i++) {
				if (inCon.length() > 0)
					inCon += ",'" + ExceDeptStr[i] + "'";
				else
					inCon = "'" + ExceDeptStr[i] + "'";
			}
			if (ExecDept.length() > 0)
				execdept = " AND A.EXEC_DEPT_CODE IN(" + inCon + ")";
		}
		String sql = "SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID AND  A.APPLICATION_NO='"
				+ ApplyNo + "' AND A.CAT1_TYPE='" + Cat1Type + "'" + execdept;
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 根据条码查询医嘱 过滤条件待定
	 * 
	 * @param ApplyNo
	 * @return
	 */
	public TParm getOrderList(String MrNo, String Cat1Type, String ExecDept,
			String StartDate, String EndDate) {
		TParm result = new TParm();
		String mr = "";
		String execdept = "";
		if (!MrNo.equals(""))
			mr = " AND  A.MR_NO='" + MrNo + "'";
		if (!ExecDept.equals("") && !ExecDept.contains("|")) {
			execdept = " AND A.EXEC_DEPT_CODE='" + ExecDept + "'";
		} else if (ExecDept.contains("|")) {
			String[] ExceDeptStr = StringTool.parseLine(ExecDept, "|");
			String inCon = "";
			for (int i = 0; i < ExceDeptStr.length; i++) {
				if (inCon.length() > 0)
					inCon += ",'" + ExceDeptStr[i] + "'";
				else
					inCon = "'" + ExceDeptStr[i] + "'";
			}
			if (ExecDept.length() > 0)
				execdept = " AND A.EXEC_DEPT_CODE IN(" + inCon + ")";
		}
		String sql = "SELECT * FROM MED_APPLY A,SYS_OPERATOR B WHERE A.ORDER_DR_CODE = B.USER_ID  AND A.CAT1_TYPE='"
				+ Cat1Type
				+ "' "
				+ mr
				+ execdept
				+ " AND A.ORDER_DATE"
				+ " BETWEEN TO_DATE('"
				+ StartDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ EndDate
				+ "','YYYYMMDDHH24MISS')";
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 根据条码查询住院医嘱
	 * 
	 * @param ApplyNo
	 * @param Cat1Type
	 * @return
	 */
	public TParm getOdiOrder(String ApplyNo, String Cat1Type, String flg)
			throws HL7Exception {
		TParm result = new TParm();
		String billFlg = "";
		if (flg.equals("ADD")) {
			billFlg = " AND (B.BILL_FLG='N' OR B.BILL_FLG IS NULL)";
		} else {
			billFlg = " AND B.BILL_FLG='Y'";
		}
		String sql = "SELECT B.* FROM ODI_ORDER A,ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ AND B.DC_NS_CHECK_DATE IS NULL AND B.NS_EXEC_CODE IS NOT NULL "
				+ billFlg + " AND  A.MED_APPLY_NO='" + ApplyNo + "'";
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 根据条码查询门诊医嘱
	 * 
	 * @param ApplyNo
	 * @param Cat1Type
	 * @return
	 */
	public TParm getOpdOrder(String ApplyNo, String Cat1Type) {
		TParm result = new TParm();
		String sql = "SELECT * FROM OPD_ORDER A WHERE A.MED_APPLY_NO='"
				+ ApplyNo + "'";
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 根据病案号得到医疗卡号
	 * 
	 * @param mrNo
	 * @return
	 */
	public TParm getEktCardNo(String mrNo) {
		TParm result = new TParm();
		String sql = "SELECT * FROM EKT_ISSUELOG A WHERE A.WRITE_FLG='Y' AND A.MR_NO='"
				+ mrNo + "'";
		result = new TParm(getDBTool().select(sql));
		return result;
	}

	/**
	 * 取得MedNodify号
	 * 
	 * @return String
	 */
	static public String getMedNodifyNo() {
		return SystemTool.getInstance().getNo("ALL", "MED", "MEDNODIFY_NO",
				"MEDNODIFY_NO");
	}

	/**
	 * 
	 * @return
	 */
	public TParm onInsertMedNodify(String ApplyNo, String Cat1Type)
			throws HL7Exception {
		TParm parm = new TParm();
		TParm result = getOrder(ApplyNo, Cat1Type, "");
		if (result.getCount() <= 0)
			return result;
		String mnn = getMedNodifyNo();
		int seq = 1;
		TConnection conn = this.getConnection();
		for (int i = 0; i < result.getCount(); i++) {
			TParm RowParm = result.getRow(i);
			if (RowParm.getValue("STATUS").equals("9"))
				continue;
			String abnormal = getAbnormalValue(RowParm,
					RowParm.getValue("CAT1_TYPE"));
			parm.setData("MED_NOTIFY_CODE", mnn);
			parm.setData("SEQ", seq);
			parm.setData("ADM_TYPE", RowParm.getValue("ADM_TYPE"));
			parm.setData("CASE_NO", RowParm.getValue("CASE_NO"));
			parm.setData("MR_NO", RowParm.getValue("MR_NO"));
			parm.setData("PAT_NAME", RowParm.getValue("PAT_NAME"));
			parm.setData("SEX_CODE", RowParm.getValue("SEX_CODE"));
			parm.setData("BED_NO", RowParm.getValue("BED_NO"));
			parm.setData("IPD_NO", RowParm.getValue("IPD_NO"));
			parm.setData("STATION_CODE", RowParm.getValue("STATION_CODE"));
			parm.setData("DEPT_CODE", RowParm.getValue("DEPT_CODE"));
			parm.setData("BILLING_DOCTORS", RowParm.getValue("ORDER_DR_CODE"));
			parm.setData("CAT1_TYPE", RowParm.getValue("CAT1_TYPE"));
			parm.setData("SYSTEM_CODE", RowParm.getValue("RPTTYPE_CODE"));
			parm.setData("APPLICATION_NO", RowParm.getValue("APPLICATION_NO"));
			parm.setData("ORDER_CODE", RowParm.getValue("ORDER_CODE"));
			parm.setData("ORDER_NO", RowParm.getValue("ORDER_NO"));
			parm.setData("ORDER_SEQ", RowParm.getValue("SEQ_NO"));
			parm.setData("SEND_STAT", "1");// 默认为1
			parm.setData("CRTCLLWLMT", abnormal);// A 存在异常值 N为正常
			parm.setData("CLINICAREA_CODE", RowParm.getValue("CLINICAREA_CODE"));
			parm.setData("OPT_USER", "Med");
			parm.setData("OPT_TERM", "127.0.0.1");
			String sql = this.CreateMedNodifySql(parm);
			TParm nodParm = new TParm(TJDODBTool.getInstance()
					.update(sql, conn));
			if (nodParm.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return nodParm;
			}
			seq++;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 异常值
	 * 
	 * @param parm
	 * @param cat1Type
	 * @return
	 */
	public String getAbnormalValue(TParm parm, String cat1Type) {
		String abnormalValue = "N";
		if (cat1Type.equals("LIS")) {
			String applyNo = parm.getValue("APPLICATION_NO");
			String orderNo = parm.getValue("ORDER_NO");
			String seq = parm.getValue("SEQ_NO");
			String sql = "SELECT CRTCLLWLMT FROM MED_LIS_RPT WHERE CAT1_TYPE='LIS' AND"
					+ " APPLICATION_NO='"
					+ applyNo
					+ "' AND ORDER_NO='"
					+ orderNo
					+ "' AND SEQ_NO='"
					+ seq
					+ "'"
					+ " AND  (CRTCLLWLMT='SL' OR CRTCLLWLMT='SH')";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() > 0) {
				abnormalValue = "A";
			}
		} else {
			String applyNo = parm.getValue("APPLICATION_NO");
			String sql = "SELECT OUTCOME_TYPE FROM MED_RPTDTL WHERE CAT1_TYPE='RIS' AND "
					+ " APPLICATION_NO='" + applyNo + "' AND OUTCOME_TYPE='H'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() > 0) {
				abnormalValue = "A";
			}
		}
		return abnormalValue;
	}

	private String CreateMedNodifySql(TParm parm) {
		String sql = "INSERT INTO MED_NODIFY"
				+ "(MED_NOTIFY_CODE, SEQ, ADM_TYPE, CASE_NO, MR_NO, PAT_NAME,"
				+ "SEX_CODE, BED_NO, IPD_NO, STATION_CODE, DEPT_CODE,"
				+ "BILLING_DOCTORS, CAT1_TYPE, SYSTEM_CODE, APPLICATION_NO,"
				+ "ORDER_CODE, ORDER_NO, ORDER_SEQ, SEND_STAT, CRTCLLWLMT,"
				+ "OPT_USER, OPT_DATE,OPT_TERM,CLINICAREA_CODE,SEND_DATE)"
				+ "VALUES ('"
				+ parm.getValue("MED_NOTIFY_CODE")
				+ "',"
				+ parm.getValue("SEQ")
				+ ",'"
				+ parm.getValue("ADM_TYPE")
				+ "','"
				+ parm.getValue("CASE_NO")
				+ "', '"
				+ parm.getValue("MR_NO")
				+ "','"
				+ parm.getValue("PAT_NAME")
				+ "',"
				+ "'"
				+ parm.getValue("SEX_CODE")
				+ "','"
				+ parm.getValue("BED_NO")
				+ "', '"
				+ parm.getValue("IPD_NO")
				+ "', '"
				+ parm.getValue("STATION_CODE")
				+ "', '"
				+ parm.getValue("DEPT_CODE")
				+ "',"
				+ "'"
				+ parm.getValue("BILLING_DOCTORS")
				+ "', '"
				+ parm.getValue("CAT1_TYPE")
				+ "', '"
				+ parm.getValue("SYSTEM_CODE")
				+ "', '"
				+ parm.getValue("APPLICATION_NO")
				+ "',"
				+ "'"
				+ parm.getValue("ORDER_CODE")
				+ "', '"
				+ parm.getValue("ORDER_NO")
				+ "', '"
				+ parm.getValue("ORDER_SEQ")
				+ "', '"
				+ parm.getValue("SEND_STAT")
				+ "', '"
				+ parm.getValue("CRTCLLWLMT")
				+ "',"
				+ "'"
				+ parm.getValue("OPT_USER")
				+ "', SYSDATE,'"
				+ parm.getValue("OPT_TERM")
				+ "','"
				+ parm.getValue("CLINICAREA_CODE") + "', SYSDATE)";
		return sql;
	}

	/**
	 * 计算怀孕周数
	 * 
	 * @param t1
	 *            就诊日期
	 * @param t2
	 *            LMP
	 * @return week int 怀孕周数
	 */
	public static int getPreWeek(Timestamp t1, Timestamp t2) {
		/* 自动计算怀孕周数=强制进位((就诊日期-LMP+1)/7) */
		if (t1 == null || t2 == null) {
			return 0;
		}
		String date = t1.toString().trim().replaceAll("-", "");
		date = date.substring(0, date.indexOf(" "));
		float Medicinecount = (Float.parseFloat(String.valueOf(StringTool
				.getDateDiffer(t1, t2) + 1))) / 7;
		BigDecimal bd = new BigDecimal(Medicinecount);
		if (bd.signum() == -1) {
			bd = bd.negate();
		}
		int IntgerValue = bd.intValue();
		if (bd.scale() > 0)
			IntgerValue++;
		return IntgerValue; /* 怀孕周数 */
	}

	/**
	 * getDBTool 数据库工具实例
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

}
