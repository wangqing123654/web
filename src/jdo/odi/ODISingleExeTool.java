package jdo.odi;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class ODISingleExeTool extends TJDOTool {
	private static ODISingleExeTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RuleTool
	 */
	public static ODISingleExeTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ODISingleExeTool();
		return instanceObject;
	}

	/**
	 * ������
	 */

	public static String Action;

	public ODISingleExeTool() {
		setModuleName("odi\\ODISingleExeModule.x");
		onInit();
	}

	public String sql_caseNo(String mr_no) {
		String sq1 = "  SELECT   CASE_NO" + "  FROM   ODI_DSPNM"
				+ "   WHERE   MR_NO = '" + mr_no
				+ "' AND ROWNUM <= 1ORDER BY   OPT_DATE DESC";
		return sq1;
	}

	public String sql_TableMessage(String Case_no, String REGION_CODE,
			String STATION_CODE, String EXEC_DEPT_CODE, String Star_time,
			String End_date, String order_date) {
		// String
		// sql="SELECT CASE WHEN A.NS_EXEC_CODE IS NULL THEN 'N' ELSE 'Y' END AS EXEC_FLG,A.CASE_NO AS CASE_NO,"+
		// "  B.LINKMAIN_FLG AS LINKMAIN_FLG,B.LINK_NO AS LINK_NO,"+
		// "  B.MEDI_QTY AS MEDI_QTY,B.MEDI_UNIT AS MEDI_UNIT,B.DISPENSE_QTY AS DISPENSE_QTY, B.DISPENSE_UNIT AS DISPENSE_UNIT,B.FREQ_CODE AS FREQ_CODE,B.ROUTE_CODE AS ROUTE_CODE,"+
		// "C.ORDER_CAT1_DESC AS ORDER_CAT1_DESC,      TO_DATE (A.ORDER_DATE||' '||A.ORDER_DATETIME,'YYYYMMDDHH24MISS') AS NS_EXEC_DATE,B.ORDER_DESC AS ORDER_DESC,  D.CANCELRSN_DESC AS CANCELRSN_DESC,"+
		// "   A.ORDER_NO AS��ORDER_NO��,A.ORDER_SEQ��AS ORDER_SEQ,A.ORDER_DATE AS ORDER_DATE,A.ORDER_DATETIME AS ORDER_DATETIME"+
		// "  FROM   ODI_DSPND A, ODI_DSPNM B,SYS_ORDER_CAT1 C,ODI_CANCEL_RSN D"+
		// "   WHERE       A.CASE_NO = B.CASE_NO  AND  A.ORDER_NO = B.ORDER_NO  AND A.ORDER_SEQ = B.ORDER_SEQ"+
		// "    AND B.ORDER_CAT1_CODE = C.ORDER_CAT1_CODE  AND B.CANCELRSN_CODE = D.CANCELRSN_CODE(+) "+
		// "   AND TO_DATE (A.ORDER_DATE, 'YYYYMMDD') BETWEEN TO_DATE (B.START_DTTM,'YYYYMMDDHH24MISS' )"+
		// "   AND  TO_DATE ( B.END_DTTM, 'YYYYMMDDHH24MISS'  ) AND A.ORDER_DATE = '"+order_date+"' AND A.ORDER_DATETIME BETWEEN '"+Star_time+"' AND '"+End_date+"'"+
		// "    AND A.CASE_NO = '" + Case_no + "' AND B.REGION_CODE = '" +
		// REGION_CODE + "'  AND B.STATION_CODE = '" + STATION_CODE +
		// "'  AND B.EXEC_DEPT_CODE = '" + EXEC_DEPT_CODE +
		// "' AND A.NS_EXEC_CODE IS NULL";

		String sql = "SELECT 'N' AS FLG, CASE WHEN A.NS_EXEC_CODE IS NULL THEN 'N' ELSE 'Y' END AS EXEC_FLG,A.CASE_NO AS CASE_NO,B.LINKMAIN_FLG AS LINKMAIN_FLG,B.LINK_NO AS LINK_NO,"
				+ "  B.MEDI_QTY AS MEDI_QTY,B.MEDI_UNIT AS MEDI_UNIT,B.DISPENSE_QTY AS DISPENSE_QTY, B.DISPENSE_UNIT AS DISPENSE_UNIT,B.FREQ_CODE AS FREQ_CODE,B.ROUTE_CODE AS ROUTE_CODE,"
				+ "  TO_DATE (A.ORDER_DATE||' '||A.ORDER_DATETIME,'YYYYMMDDHH24MISS') AS NS_EXEC_DATE,B.ORDER_DESC AS ORDER_DESC,B.DR_NOTE AS DR_NOTE,B.ORDER_DR_CODE AS ORDER_DR_CODE,B.DC_DATE AS DC_DATE,B.DC_DR_CODE AS DC_DR_CODE, D.CANCELRSN_DESC AS CANCELRSN_DESC,"
				+ "   A.ORDER_NO AS��ORDER_NO��,A.ORDER_SEQ��AS ORDER_SEQ,A.ORDER_DATE AS ORDER_DATE,A.ORDER_DATETIME AS ORDER_DATETIME"
				+ "   FROM   ODI_DSPND A, ODI_DSPNM B,SYS_ORDER_CAT1 C,ODI_CANCEL_RSN D"
				+ "   WHERE       A.CASE_NO = B.CASE_NO  AND  A.ORDER_NO = B.ORDER_NO  AND A.ORDER_SEQ = B.ORDER_SEQ"
				+ "    AND B.ORDER_CAT1_CODE = C.ORDER_CAT1_CODE  AND B.CANCELRSN_CODE = D.CANCELRSN_CODE(+) "
				+ "   AND TO_DATE (A.ORDER_DATE, 'YYYYMMDD') BETWEEN TO_DATE (B.START_DTTM,'YYYYMMDDHH24MISS' )"
				+ "   AND  TO_DATE ( B.END_DTTM, 'YYYYMMDDHH24MISS'  ) AND A.ORDER_DATE = '"
				+ order_date
				+ "' AND A.ORDER_DATETIME BETWEEN '"
				+ Star_time
				+ "' AND '"
				+ End_date
				+ "'"
				+ "    AND A.CASE_NO = '"
				+ Case_no
				+ "' AND B.REGION_CODE = '"
				+ REGION_CODE
				+ "'  AND B.STATION_CODE = '"
				+ STATION_CODE
				+ "'  AND B.EXEC_DEPT_CODE = '"
				+ EXEC_DEPT_CODE
				+ "' AND A.NS_EXEC_CODE IS NULL";

		// System.out.print(sql);

		return sql;
	}

	public String sql_Patinfo(String MR_NO) {
		String sql = "SELECT   SYS_PATINFO.PAT_NAME, SYS_DICTIONARY.CHN_DESC"
				+ "  FROM   SYS_PATINFO, SYS_DICTIONARY"
				+ "  WHERE       MR_NO ='"
				+ MR_NO
				+ "'"
				+ "    AND SYS_DICTIONARY.ID = SYS_PATINFO.SEX_CODE AND SYS_DICTIONARY.GROUP_ID = 'SYS_SEX'";
		return sql;

	}

	/**
	 * ����ָ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onUpdate(TParm parm) {
		TParm result = new TParm();
		result = this.update("update", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public String queryPatInfo(String mrNo, String deptCode, String stationCode) {
		String deptstr = "";
		String Stationstr = "";
		if (!deptCode.equals("") || deptCode.length() > 0)
			deptstr = " AND A.DEPT_CODE = '" + deptCode + "'";
		if (!stationCode.equals("") || stationCode.length() > 0)
			Stationstr = " AND A.STATION_CODE = '" + stationCode + "'";
		String SQL = " SELECT D.PAT_NAME,B.CHN_DESC,C.BED_NO_DESC,E.STATION_DESC,"
				+ "        A.CASE_NO,A.MR_NO"
				+ " FROM ADM_INP A,SYS_DICTIONARY B,SYS_BED C,SYS_PATINFO D,SYS_STATION E"
				+ " WHERE A.MR_NO = '" + mrNo + "'" + deptstr + Stationstr
				+ " AND   A.CANCEL_FLG != 'Y'" + " AND   A.DS_DATE IS NULL AND A.STATION_CODE=E.STATION_CODE "
				+ " AND   D.SEX_CODE = B.ID" + " AND   B.GROUP_ID = 'SYS_SEX'"
				+ " AND   A.BED_NO = C.BED_NO" + " AND   A.MR_NO = D.MR_NO";
		return SQL;
	}
    /**
     * ����ҽ����Ϣ
     * @param caseNo
     * @param barCode
     * @param startDate
     * @param endDate
     * @param cat1Type
     * @param isExe
     * @param doseType
     * @return
     */
	public String queryPatOrder(String caseNo, String barCode,
			String startDate, String endDate, String cat1Type, String isExe,
			String doseType) {
		String isExeSQL = "";
		String Stationstr = "";
		StringBuffer barStr = new StringBuffer();
		StringBuffer doseStr = new StringBuffer();
		if (isExe.equals("N"))
			isExeSQL = " AND   A.NS_EXEC_DATE_REAL IS NULL AND A.NS_EXEC_CODE_REAL IS NULL";
		else if (isExe.equals("Y"))
			isExeSQL = " AND   A.NS_EXEC_DATE_REAL IS NOT NULL AND A.NS_EXEC_CODE_REAL IS NOT NULL";
		if (cat1Type.equals("PHA")) {
			if (barCode.length() == 0)
				barStr.append("");
			else
				barStr.append(" AND A.BAR_CODE = '" + barCode + "'");
			if (doseType.equals(""))
				doseStr.append("");
			else
				doseStr.append("AND D.CLASSIFY_TYPE = '" + doseType + "'");
		} else if (cat1Type.equals("LIS','RIS")) {
			if (barCode.length() == 0)
				barStr.append("");
			else
				barStr.append(" AND C.MED_APPLY_NO = '" + barCode + "'");
		} else if (cat1Type.equals("")) {
			if (barCode.length() == 0)
				barStr.append("");
			else
				barStr.append(" AND ( C.MED_APPLY_NO = '" + barCode
						+ "' OR  A.BAR_CODE = '" + barCode + "')");
		}
		String SQL = " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
				+ "        B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
				+ "        B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
				+ "        B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,CASE  WHEN B.CAT1_TYPE='PHA' THEN A.BAR_CODE ELSE C.MED_APPLY_NO END AS BAR_CODE,"//modify by wanglong 20130609
				+ "        A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE, "
				+ "        A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,A.BARCODE_1,A.BARCODE_2,A.BARCODE_3 "//modify by wanglong 20130605
				+ " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D"
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "'"
				+ isExeSQL
				+ " AND   A.ORDER_DATE || A.ORDER_DATETIME BETWEEN '"
				+ startDate
				+ "' AND '"
				+ endDate
				+ "'"
				+ " AND   A.CASE_NO = B.CASE_NO"
				+ " AND   A.ORDER_NO = B.ORDER_NO"
				+ " AND   A.ORDER_SEQ = B.ORDER_SEQ"
				+ " AND   A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM"
				+ " AND   (B.ORDERSET_CODE IS NULL OR B.ORDER_CODE = B.ORDERSET_CODE)"
				+ (cat1Type.length() == 0 ? "" : " AND B.CAT1_TYPE IN ('"
						+ cat1Type + "')")
				+ " AND   A.CASE_NO = C.CASE_NO"
				+ " AND   A.ORDER_NO = C.ORDER_NO"
				+ " AND   A.ORDER_SEQ = C.ORDER_SEQ"
				+ " AND   B.ROUTE_CODE=D.ROUTE_CODE(+) "
				+
				// " AND   A.DC_DATE IS NULL " +
				doseStr.toString()
				+ barStr.toString()
				+ " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
		return SQL;
	}
    /**
     * �����ڷ�ҩƷ��Ϣ
     * @param caseNo
     * @param orderNo
     * @param orderSeq
     * @param orderDate
     * @param orderDateTime
     * @param isExe
     * @return
     */
	public String queryPatOrderPhaO(String caseNo,String startDate, String endDate, String cat1Type, String orderNo,
			String orderSeq, String orderDate, String orderDateTime,
			String isExe,boolean isUD) {
		String isExeSQL = "";
		if (isExe.equals("N"))
			isExeSQL = " AND   A.NS_EXEC_DATE_REAL IS NULL AND A.NS_EXEC_CODE_REAL IS NULL";
		else if (isExe.equals("Y"))
			isExeSQL = " AND   A.NS_EXEC_DATE_REAL IS NOT NULL AND A.NS_EXEC_CODE_REAL IS NOT NULL";
		String isUDSQL="";
		if(isUD){
			isUDSQL=" AND A.ORDER_DATE='"
			+ orderDate
			+ "'"
			+ " AND A.ORDER_DATETIME='"
			+ orderDateTime
			+ "'";
		}
		String SQL = " SELECT CASE WHEN A.NS_EXEC_DATE_REAL IS  NULL THEN 'Y' ELSE 'N' END SEL_FLG,CASE WHEN A.NS_EXEC_DATE_REAL IS NOT NULL THEN 'Y' ELSE 'N' END EXE_FLG,"
				+ "        B.LINKMAIN_FLG,B.LINK_NO,TO_CHAR(TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME,'YYYYMMDDHH24MISS'),'YYYY/MM/DD HH24:MI:SS')NS_EXEC_DATE,"
				+ "        B.ORDER_DESC,A.MEDI_QTY,A.MEDI_UNIT,A.DOSAGE_QTY,A.DOSAGE_UNIT,B.FREQ_CODE,B.ROUTE_CODE,"
				+ "        B.DR_NOTE,B.ORDER_DR_CODE,A.DC_DATE,B.DC_DR_CODE,A.CANCELRSN_CODE,A.INV_CODE,A.ORDER_NO||A.ORDER_SEQ  AS BAR_CODE,"//modify by wanglong 20130609
				+ "        A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,B.ORDERSET_GROUP_NO,B.CAT1_TYPE,"
				+ "        A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL,B.START_DTTM,B.END_DTTM,A.ORDER_CODE,A.BOX_ESL_ID,A.BARCODE_1,A.BARCODE_2,A.BARCODE_3 "//modify by wanglong 20130605
				+ " FROM ODI_DSPND A,ODI_DSPNM B,ODI_ORDER C,SYS_PHAROUTE D"
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "'"
				+ isExeSQL
				+ " AND   A.ORDER_NO='"
				+ orderNo
				+ "'"
				+ " AND A.ORDER_SEQ='"
				+ orderSeq
				+ "'"
				+isUDSQL
				+ " AND   A.ORDER_DATE || A.ORDER_DATETIME BETWEEN '"
				+ startDate
				+ "' AND '"
				+ endDate
				+ "'"
				+ " AND   A.CASE_NO = B.CASE_NO"
				+ " AND   A.ORDER_NO = B.ORDER_NO"
				+ " AND   A.ORDER_SEQ = B.ORDER_SEQ"
				+ (cat1Type.length() == 0 ? "" : " AND B.CAT1_TYPE IN ('"
					+ cat1Type + "')")
				+ " AND   A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM"
				+ " AND   A.CASE_NO = C.CASE_NO"
				+ " AND   A.ORDER_NO = C.ORDER_NO"
				+ " AND   A.ORDER_SEQ = C.ORDER_SEQ"
				+ " AND   B.ROUTE_CODE=D.ROUTE_CODE(+) "
				+ " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
		return SQL;
	}

	public String queryPatOrderSetDetail(String caseNo, String orderNo,
			String orderDate, String orderDateTime, String orderSetgroupNo) {
		String SQL = " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME"
				+ " FROM ODI_DSPND A,ODI_DSPNM B"
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "'"
				+ " AND   A.ORDER_NO = '"
				+ orderNo
				+ "'"
				+ " AND   A.ORDER_DATE = '"
				+ orderDate
				+ "'"
				+ " AND   A.ORDER_DATETIME = '"
				+ orderDateTime
				+ "'"
				+ " AND   A.CASE_NO = B.CASE_NO"
				+ " AND   A.ORDER_NO = B.ORDER_NO"
				+ " AND   A.ORDER_SEQ = B.ORDER_SEQ"
				+ " AND   A.ORDER_DATE || A.ORDER_DATETIME BETWEEN B.START_DTTM AND B.END_DTTM"
				+ " AND   B.ORDERSET_CODE != B.ORDER_CODE "
				+ " AND   B.ORDERSET_CODE IS NOT NULL "
				+ " AND   B.ORDERSET_GROUP_NO = '"
				+ orderSetgroupNo
				+ "'"
				+ " ORDER BY A.ORDER_NO,A.ORDER_SEQ";
		return SQL;
	}

	public TParm updateDspnd(TParm parm, TConnection conn) {
		TParm result = update("updateDspnd", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateDspnm(TParm parm, TConnection conn) {
		TParm result = update("updateDspnm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
