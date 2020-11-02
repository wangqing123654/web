package jdo.odi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jdo.sys.PatTool;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

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
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OdiOrderTool extends TJDODBTool {
	// 查询住院信息中的入院时间和经治医师
	private static final String GET_ADM_INP = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO='#'";
	/**
	 * 实例
	 */
	public static OdiOrderTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RuleTool
	 */
	public static OdiOrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OdiOrderTool();
		return instanceObject;
	}

	public OdiOrderTool() {
	}

	/**
	 * 保存住院医嘱
	 * 
	 * @param parm
	 *            TParm
	 * @param con
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveOrder(TParm parm, TConnection con) {
		String sqlStr[] = (String[]) parm.getData("ARRAY");
		List<String> list =new ArrayList<String>();
		// ================SHIBL add增加校验（护士已审核医生删医嘱）==========
		for (int i =0; i<sqlStr.length; i++) {
			if (sqlStr[i].startsWith("DELETE FROM ODI_ORDER ")) {
				String sql = sqlStr[i].replaceFirst("DELETE",
						"SELECT CASE_NO,ORDER_NO,ORDER_SEQ")
						+ " AND NS_CHECK_CODE IS NOT NULL";
				TParm queryParm = new TParm(this.select(sql));
				if (queryParm.getCount() > 0) {
					System.out.println("校验医嘱已审核不能删除：" + queryParm);
					continue;
				}
			}
			list.add(sqlStr[i]);
		}
		// ================SHIBL add增加校验（医生删检验检查医嘱）==========
		for (int i = list.size() - 1; i > 0; i--) {
			if (list.get(i).startsWith("DELETE FROM  MED_APPLY")) {
				String sql = list.get(i).replaceFirst("DELETE",
						"SELECT CASE_NO,ORDER_NO,SEQ_NO");
				TParm queryParm = new TParm(this.select(sql));
				// 存在med删除语句不存在odiOrder的删除语句过滤掉
				if (queryParm.getCount() > 0
						&& !list.contains(onCreateDelOrderSql(queryParm
								.getRow(0)))) {
					list.remove(i);
					System.out.println("校验检验检查删除医嘱未删除的情况：" + queryParm);
					continue;
				}
			}
		}
		String[] arr = (String[]) list.toArray(new String[list.size()]);
		TParm result = new TParm(this.update(arr, con));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 构造odiOrder删除sql
	 * 
	 * @param parm
	 * @return
	 */
	public String onCreateDelOrderSql(TParm parm) {
		String sql = "DELETE FROM ODI_ORDER WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND ORDER_NO='"+parm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ=" + parm.getInt("SEQ_NO");
		return sql;
	}

	/**
	 * 检查出院是否有没有停用的长期医嘱
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean getUDOrder(String caseNo) {
		boolean falg = false;
		TParm parm = new TParm(
				this.select("SELECT ORDER_NO FROM ODI_ORDER WHERE CASE_NO='"
						+ caseNo + "' AND RX_KIND='UD' AND DC_DATE IS NULL"
						+ " AND OPBOOK_SEQ IS NULL "));// wanglong add 20141114
														// 过滤掉术中医嘱，不检查
		// System.out.println("长期医嘱sql---->"+"SELECT ORDER_NO FROM ODI_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_KIND='UD' AND DC_DATE IS NULL");
		if (parm.getCount("ORDER_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 拿到最新诊断
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getICDCode(String caseNo) {
		TParm parm = new TParm(
				this.select("SELECT B.ICD_CHN_DESC FROM ADM_INP A,SYS_DIAGNOSIS B WHERE CASE_NO='"
						+ caseNo + "' AND A.MAINDIAG=B.ICD_CODE"));
		return parm.getValue("ICD_CHN_DESC", 0);
	}

	/**
	 * 拿到最新诊断
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getICDCodeEng(String caseNo) {
		TParm parm = new TParm(
				this.select("SELECT B.ICD_ENG_DESC FROM ADM_INP A,SYS_DIAGNOSIS B WHERE CASE_NO='"
						+ caseNo + "' AND A.MAINDIAG=B.ICD_CODE"));
		return parm.getValue("ICD_ENG_DESC", 0);
	}

	/**
	 * 拿到身份名称
	 * 
	 * @param ctzCode
	 *            String
	 * @return String
	 */
	public String getCTZDesc(String ctzCode) {
		TParm parm = new TParm(
				this.select("SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"
						+ ctzCode + "'"));
		return parm.getValue("CTZ_DESC", 0);
	}

	/**
	 * 检查出院时退药流程是否完成 duzhw add 20130917
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean getRtnCfmM(String caseNo) {
		boolean falg = false;
		TParm parm = new TParm(
				this.select("SELECT CASE_NO FROM ODI_DSPNM WHERE CASE_NO = '"
						+ caseNo
						+ "' AND  (PHA_RETN_CODE IS NULL OR PHA_RETN_CODE = '') AND (PHA_RETN_DATE IS NULL OR PHA_RETN_DATE = '') AND DSPN_KIND = 'RT'"));
		if (parm.getCount("CASE_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * add caoy 出院带药处方
	 * 
	 * @param caseNo
	 * @param rxNo
	 * @return
	 */
	public TParm getOuthosResult(String caseNo, String rxNo) {
		TParm result = new TParm();
		String sql = "SELECT  '  '||A.LINK_NO AS LINK_NO,CASE WHEN F.IS_REMARK = 'Y' "
				+ "THEN A.DR_NOTE ELSE A.ORDER_DESC END || ' ' || A.SPECIFICATION AS ORDER_DESC,"
				+ "C.ROUTE_CHN_DESC AS ROUTE_CODE, TO_CHAR(A.MEDI_QTY,'fm9999999990.000') "
				+ "||D.UNIT_CHN_DESC AS MEDI_UNIT "//modify by guoy 20151105
				+ ",B.FREQ_CODE,A.TAKE_DAYS,TO_CHAR(A.DISPENSE_QTY,'fm9999999990.000')|| "//modify by guoy 20151105
				+ " E.UNIT_CHN_DESC AS DISPENSE_UNIT, A.DR_NOTE "
				+ "FROM ODI_ORDER A,SYS_PHAFREQ B,SYS_PHAROUTE C,SYS_UNIT D,SYS_UNIT E,SYS_FEE F,PHA_TRANSUNIT W,SYS_UNIT N "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND RX_KIND='DS' "
				+ "AND RX_NO='"
				+ rxNo
				+ "' "
				+ "AND A.FREQ_CODE = B.FREQ_CODE "
				+ "AND A.ROUTE_CODE = C.ROUTE_CODE "
				+ "AND A.MEDI_UNIT = D.UNIT_CODE "
				+ "AND A.DISPENSE_UNIT=N.UNIT_CODE "
				+ "AND A.DISPENSE_UNIT = E.UNIT_CODE "
				+ "AND A.ORDER_CODE=F.ORDER_CODE "
				+ "AND A.ORDER_CODE=W.ORDER_CODE "
				+ "AND F.ACTIVE_FLG='Y' "
				+ "ORDER BY ORDER_SEQ ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * add caoy 检查申请单 病房来源传参数
	 * 
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
	public String getRexv(String mrNo) {
		String sql = "SELECT RESV_NO FROM ADM_RESV WHERE MR_NO='"
				+ mrNo
				+ "' "
				+ "AND APP_DATE =( SELECT MAX(APP_DATE) FROM ADM_RESV WHERE MR_NO='"
				+ mrNo + "')";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("RESV_NO", 0);
	}

	/**
	 * huangtt 查询检验数据
	 * 
	 * @param caseNo
	 * @param flg
	 * @return
	 */
	public TParm getExaDataSum(String caseNo) {
		String sql = "SELECT   A.MED_APPLY_NO, F.OWN_PRICE, A.ORDER_CODE, C.DEPT_CHN_DESC, F.AR_AMT,"
				+ " A.ORDER_DESC, A.MEDI_QTY, C.DESCRIPTION,"
				+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS OPT_DATE,"
				+ " A.ORDER_CAT1_CODE, A.SETMAIN_FLG,G.CHN_DESC, H.CHN_DESC AS BF,A.ORDER_DR_CODE"
				+ " ,A.URGENT_FLG,A.DEPT_CODE,A.STATION_CODE "//20151211 wangjc add 急作
				+ " FROM ODI_ORDER A,"
				+ " SYS_FEE B,"
				+ " SYS_DEPT C,"
				+ " (SELECT   A.RX_NO, A.ORDERSET_GROUP_NO, SUM (B.OWN_PRICE) OWN_PRICE,"
				+ " SUM (A.MEDI_QTY * B.OWN_PRICE) AR_AMT, A.CASE_NO"
				+ " FROM ODI_ORDER A, SYS_FEE B, SYS_DEPT C"
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "'"
				+ " AND A.ORDER_CODE = B.ORDER_CODE"
				+ " AND C.DEPT_CODE = A.EXEC_DEPT_CODE"
				+ " AND A.CAT1_TYPE IN ('LIS', 'RIS')"
				+ " GROUP BY A.RX_NO, A.ORDERSET_GROUP_NO, A.CASE_NO"
				+ " ORDER BY A.ORDERSET_GROUP_NO) F,"
				+ " (SELECT ID, CHN_DESC, COST_CENTER_CODE"
				+ " FROM SYS_COST_CENTER A, SYS_DICTIONARY B"
				+ " WHERE B.GROUP_ID = 'EXAADDRESS' AND A.EXE_PLACE = B.ID) G,"
				+ " SYS_DICTIONARY H "
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "'"
				+ " AND A.SETMAIN_FLG = 'Y'"
				+ " AND A.ORDER_CODE = B.ORDER_CODE"
				+ " AND A.EXEC_DEPT_CODE = G.COST_CENTER_CODE(+)"
				+ " AND B.OPTITEM_CODE = H.ID(+)"
				+ " AND H.GROUP_ID = 'SYS_OPTITEM'"
				+ " AND A.CASE_NO = F.CASE_NO"
				+ " AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO"
				+ " AND C.DEPT_CODE = A.EXEC_DEPT_CODE"
				+ " AND A.CAT1_TYPE IN ('LIS', 'RIS')"
				+ " ORDER BY A.MED_APPLY_NO";
//		System.out.println("sql----"+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 设置显示年龄
	 * 
	 * @param caseNo
	 * @param mrNo
	 * @return
	 */
	public String getAgeName(String caseNo, String mrNo) {
		TParm tParm = new TParm(TJDODBTool.getInstance().select(
				GET_ADM_INP.replace("#", caseNo)));
		return OdoUtil.showAge(PatTool.getInstance().getInfoForMrno(mrNo)
				.getTimestamp("BIRTH_DATE", 0),
				tParm.getTimestamp("IN_DATE", 0));
	}

	/**
	 * 设置显示年龄(英文)
	 * 
	 * @param caseNo
	 * @param mrNo
	 * @return
	 */
	public String getAgeEngName(String caseNo, String mrNo) {
		TParm tParm = new TParm(TJDODBTool.getInstance().select(
				GET_ADM_INP.replace("#", caseNo)));
		return OdoUtil.showEngAge(PatTool.getInstance().getInfoForMrno(mrNo)
				.getTimestamp("BIRTH_DATE", 0),
				tParm.getTimestamp("IN_DATE", 0));
	}

	/**
	 * 设置显示在处方签上的诊断
	 */
	public String getIcdName(String caseNo) {
		/*
		 * String
		 * sql="SELECT A.ICD_CODE,B.ICD_CHN_DESC,A.DESCRIPTION,B.NOTE_FLG " +
		 * " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B " +
		 * " WHERE A.ICD_CODE = B.ICD_CODE AND A.CASE_NO='"+caseNo+"'" +
		 * " ORDER BY A.SEQ_NO"; TParm parm = new
		 * TParm(TJDODBTool.getInstance().select(sql)); String icdName="";
		 * 
		 * //ADM_INPDIAG.IO_TYPE I为门急诊诊断，O为出院诊断，M为入院诊断
		 * //ADM_INPDIAG.MAINDIAG_FLG Y为主诊断
		 * 
		 * for (int i = 0; i < parm.getCount(); i++) {
		 * 
		 * //add by yangjj 20150619 String diagNote=
		 * parm.getValue("DESCRIPTION", i); String
		 * noteFlg=parm.getValue("NOTE_FLG", i);
		 * 
		 * 
		 * //add by yangjj 20150619 if (noteFlg.equals("Y")) { icdName
		 * +=diagNote; }else{ icdName = icdName + parm.getValue("ICD_CHN_DESC",
		 * i);
		 * 
		 * if(!diagNote.equals("")){ icdName+="("+diagNote+")"; }
		 * 
		 * }
		 * 
		 * 
		 * icdName += "#"; } if(icdName.length()>0){
		 * icdName=icdName.substring(0,icdName.length()-1); }
		 */

		String icdName = "";

		icdName = getDiag(caseNo, "O");

		if (!"".equals(icdName)) {
			return icdName;
		}

		icdName = getDiag(caseNo, "M");

		if (!"".equals(icdName)) {
			return icdName;
		}

		icdName = getDiag(caseNo, "I");

		return icdName;
	}

	public String getDiag(String caseNo, String type) {
		String diag = "";

		String sql = " SELECT " + " A.ICD_CODE, " + " B.ICD_CHN_DESC, "
				+ " A.DESCRIPTION, " + " B.NOTE_FLG " + " FROM "
				+ " ADM_INPDIAG A, " + " SYS_DIAGNOSIS B " + " WHERE "
				+ " A.ICD_CODE = B.ICD_CODE " + " AND A.CASE_NO = '" + caseNo
				+ "'" + " AND A.IO_TYPE = '" + type + "'"
				+ " ORDER BY A.MAINDIAG_FLG DESC";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		for (int i = 0; i < result.getCount(); i++) {

			String diagNote = result.getValue("DESCRIPTION", i);
			String noteFlg = result.getValue("NOTE_FLG", i);

			if (noteFlg.equals("Y")) {
				diag += diagNote;
			} else {
				diag = diag + result.getValue("ICD_CHN_DESC", i);
				if (!diagNote.equals("")) {
					diag += "(" + diagNote + ")";
				}
			}
			diag += "#";
		}
		if (diag.length() > 0) {
			diag = diag.substring(0, diag.length() - 1);
		}

		return diag;
	}

	public String GetAdmDr(String drCode) {

		return StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='"
				+ drCode + "'");

	}
}
