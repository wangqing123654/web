package jdo.udd;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaAntiTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: סԺҩ�����ڰ�ҩTOOL
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui 20090526
 * @version 1.0
 */
public class UddDispatchTool extends TJDOTool {

	// ODI_ORDER TDS��SQL
	private static final String OTDS_SQL = "SELECT * FROM ODI_ORDER";
	// ODI_DSPNM TDS��SQL
	private static final String MTDS_SQL = "SELECT * FROM ODI_DSPNM";
	// ODI_DSPND TDS��SQL
	private static final String DTDS_SQL = "SELECT * FROM ODI_DSPND";

	private static final String INSERT_SOURCE_SQL = " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.REGION_CODE,B.STATION_CODE,"
			+ "				A.DEPT_CODE,A.VS_DR_CODE,A.BED_NO,A.IPD_NO,A.MR_NO,"
			+ "				A.RX_KIND,A.TEMPORARY_FLG,A.ORDER_STATE,A.LINKMAIN_FLG,"
			+ "				A.LINK_NO,A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,"
			+ "				A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,"
			+ "				A.DOSAGE_QTY,A.DOSAGE_UNIT,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,"
			+ "				A.CONTINUOUS_FLG,A.ACUMDSPN_QTY,A.LASTDSPN_QTY,A.ORDER_DATE,A.EFF_DATE,"
			+ "				A.ORDER_DEPT_CODE,A.ORDER_DR_CODE,A.EXEC_DEPT_CODE,A.EXEC_DR_CODE,A.DC_DEPT_CODE,"
			+ "				A.DC_DR_CODE,A.DC_DATE,A.DC_RSN_CODE,A.DR_NOTE,A.NS_NOTE,"
			+ "				A.INSPAY_TYPE,A.CTRLDRUGCLASS_CODE,A.RX_NO,A.PRESRT_NO,A.PHA_TYPE,"
			+ "				A.DOSE_TYPE,A.DCT_TAKE_QTY,A.DCTAGENT_CODE,A.PACKAGE_AMT,A.SETMAIN_FLG,"
			+ "				A.NS_CHECK_CODE,A.INDV_FLG,A.HIDE_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
			+ "				A.ORDER_CAT1_CODE,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.MR_CODE,A.FILE_NO,"
			+ "				A.DEGREE_CODE,A.NS_CHECK_DATE,A.DC_NS_CHECK_CODE,A.DC_NS_CHECK_DATE,A.START_DTTM,"
			+ "				A.LAST_DSPN_DATE,A.FRST_QTY,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,A.INJ_ORG_CODE,"
			+ "				A.URGENT_FLG,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DCTEXCEP_CODE ,A.CAT1_TYPE ,A.DISPENSE_FLG,A.SKIN_RESULT,A.INFLUTION_RATE "//���ӵ���
			+ "	FROM ODI_ORDER A ,ADM_INP B ";
 
	private static final String LEFT_DRUG_SQL = "SELECT COUNT(CASE_NO) COUNT FROM ODI_DSPNM WHERE CASE_NO='#' AND to_date(END_DTTM,'YYYYMMDDHH24MISS') >=SYSDATE AND  PHA_DISPENSE_CODE IS NULL";

	private String today, fromDate, endDate, effDate;
	private Timestamp now;
	private TParm sysparm;
	private String[] arrangeTime;
	private List dspnTimelist;
	private String[] dspnTimes;
	private String yesterday;
	private String id, ip;
	// **************************************************************
	// luhai 2012-04-09 ��ò�����case_no ʱɾ��ռ��ע�� begin
	// **************************************************************
	// //ȡ�ò�����CASE_NO
	// private static final String GET_CASE_NO_SQL =
	// "	SELECT DISTINCT A.CASE_NO FROM ODI_ORDER A, SYS_BED B " +
	// "	WHERE A.BED_NO=B.BED_NO " +
	// "	AND (B.ALLO_FLG IS NOT NULL AND B.ALLO_FLG ='Y') " +
	// "	AND (B.BED_OCCU_FLG IS NULL OR B.BED_OCCU_FLG='N')" +
	// "	AND (A.DC_DATE IS NULL OR A.DC_DATE >=#)" +
	// "	AND A.ORDER_CAT1_CODE IN ('PHA_W','PHA_C')";
	// ȡ�ò�����CASE_NO
	private static final String GET_CASE_NO_SQL = "	SELECT DISTINCT A.CASE_NO FROM ODI_ORDER A, ADM_INP B "
			+ "	WHERE A.CASE_NO=B.CASE_NO "
			+ " AND B.CANCEL_FLG='N' AND B.DS_DATE IS NULL "
			+ "	AND (A.DC_DATE IS NULL OR A.DC_DATE >=#)"
			+ "	AND A.ORDER_CAT1_CODE IN ('PHA_W','PHA_C')";
	// **************************************************************
	// luhai 2012-04-09 ��ò�����case_no ʱɾ��ռ��ע�� end
	// **************************************************************
	// �ж��Ƿ�δ���
	private static final String IS_UNCHECK_SQL = "SELECT COUNT(ORDER_CODE) AS COUNT "
			+ "	FROM ODI_ORDER";
	// ��ѯ��������ʾ��Ϣ
	private static final String TBL_DIS_SQL_FIELD = "SELECT A.STATION_CODE,A.BED_NO,B.PAT_NAME,MAX(A.PHA_CHECK_DATE) AS PHA_CHECK_DATE,MAX(A.LAST_DSPN_DATE) AS PHA_DOSAGE_DATE,"
			+ "	A.MR_NO,A.IPD_NO,A.CASE_NO "
			+ "	FROM ODI_ORDER A,SYS_PATINFO B ,SYS_BED C ";
	// ����SQL�õ�GROUP BY ���
	private static final String TBL_DIS_SQL_ORDER_GROUP = " GROUP BY A.CASE_NO, A.STATION_CODE,A.BED_NO,A.MR_NO,A.IPD_NO,B.PAT_NAME  ORDER BY A.STATION_CODE,A.CASE_NO ";

	/**
	 * ʵ��
	 */
	public static UddDispatchTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return UddDispatchTool
	 */
	public static UddDispatchTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UddDispatchTool();
		}
		return instanceObject;
	}
	/**
	 * ����ͳҩ����,ͳҩ�������ɹ���Ϊһ��ͳҩ����ֻ�ܰ���һ������,
	 * ͳҩ���Ź�9λ,��ŷ�ʽΪ��Ԫ��2λ+��2λ+��2λ+��ˮ��3λ,
	 * @return String
	 */
	static public String getIntgMedNo() {
		return SystemTool.getInstance().getNo("ALL", "UDD", "INTGMED_NO",
				"INTGMED_NO");
	}
	/**
	 * ����ǰ̨�����ѡ���Ĳ�������ѯ�ò��������ڴ��Ĳ���
	 * 
	 * @param statonCode
	 * @return
	 */
	public TParm getCaseNoParm(TParm stationParm, String orgCode, String date) {
		TParm result = new TParm();
		result.setData("CASE_NO", new Vector());
		if (stationParm == null || stationParm.getCount("STATION_CODE") < 1) {
			return result;
		}
		if (orgCode == null) {
			return result;
		}
		if (orgCode.trim().length() < 1) {
			return result;
		}
		int count = stationParm.getCount("STATION_CODE");
		for (int i = 0; i < count; i++) {
			String stationCode = stationParm.getValue("STATION_CODE", i);
			if (isNullString(stationCode)) {
				// System.out.println("stationCode is null");
				continue;
			}
			String regionCode = "";
			if (null != stationParm.getValue("REGION_CODE", i)
					&& stationParm.getValue("REGION_CODE", i).length() > 0)
				regionCode = " AND A.REGION_CODE='"
						+ stationParm.getValue("REGION_CODE", i) + "'";
			String sql = GET_CASE_NO_SQL.replaceFirst("#", date)
					+ " AND A.STATION_CODE='" + stationCode
					+ "' AND A.EXEC_DEPT_CODE='" + orgCode + "'" + regionCode;
			// System.out.println("getCaseNoParm.sql="+sql);
			// luhai 2012-04-07 add
			// ��ҩ����Ա�б��ѯ������������˳���ҽ�������� begin
			sql += " AND a.rx_kind = 'UD'";
			// ��ҩ����Ա�б��ѯ������������˳���ҽ�������� end
			// luhai 2012-04-07 end "
			// System.out.println("��ѯ����sql:"+sql);
			TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
			result.addParm(temp);
		}
		// getUnDisCaseNoParm(result);
		return result;
	}

	/**
	 * ����caseNO��TParm��У��ÿ��CASE_NO�Ƿ��ҩ������û��δ��˵�ҩ���Ѿ��ڹ�ҩ�ˡ���
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getUnDisCaseNoParm(TParm parm) {
		if (parm == null || parm.getCount("CASE_NO") < 1)
			return new TParm();
		int count = parm.getCount("CASE_NO");
		for (int i = count - 1; i > -1; i--) {
			String tempCase = parm.getValue("CASE_NO", i);
			if (isCaseUnCheck(tempCase))
				parm.removeRow(i);
		}
		return parm;
	}

	/**
	 * �жϸ�CASE_NO�Ĳ����Ƿ���Ϊ��˹���ҩ
	 * 
	 * @param caseNo
	 * @return boolean true:�У�false:û��
	 */
	public boolean isCaseUnCheck(String caseNo) {
		String sql = IS_UNCHECK_SQL + " WHERE CASE_NO='" + caseNo
				+ "' AND PHA_CHECK_CODE IS NULL ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getInt("COUNT", 0) > 0;
	}

	/**
	 * 
	 * @param caseParm
	 *            TParm
	 * @param date
	 *            String
	 * @return TParm
	 */
	public TParm getDispensePat(TParm stationParm, String orgCode, String date) {
		TParm result = new TParm();
		if (stationParm == null || stationParm.getCount("STATION_CODE") < 1) {
			return result;
		}
		if (orgCode == null) {
			return result;
		}
		if (orgCode.trim().length() < 1) {
			return result;
		}
		result.setData("SELECT_FLG", new Vector());
		result.setData("STATION_CODE", new Vector());
		result.setData("BED_NO", new Vector());
		result.setData("PAT_NAME", new Vector());
		result.setData("PHA_CHECK_DATE", new Vector());
		result.setData("PHA_DOSAGE_DATE", new Vector());
		result.setData("MR_NO", new Vector());
		result.setData("IPD_NO", new Vector());
		result.setData("CASE_NO", new Vector());
		int count = stationParm.getCount("STATION_CODE");
		for (int i = 0; i < count; i++) {
			String stationCode = stationParm.getValue("STATION_CODE", i);
			if (isNullString(stationCode)) {
				// System.out.println("stationCode is null");
				continue;
			}
			String regionCode = "";
			if (null != stationParm.getValue("REGION_CODE", i)
					&& stationParm.getValue("REGION_CODE", i).length() > 0)
				regionCode = " AND A.REGION_CODE='"
						+ stationParm.getValue("REGION_CODE", i) + "'";
			String sql = " SELECT 'Y' AS SELECT_FLG, A.STATION_CODE,C.BED_NO,"
					+ " B.PAT_NAME,MAX(A.PHA_CHECK_DATE) AS PHA_CHECK_DATE,"
					+ " MAX(A.LAST_DSPN_DATE) AS PHA_DOSAGE_DATE, A.MR_NO,"
					+ " A.IPD_NO,A.CASE_NO "
					+ " FROM ODI_ORDER A,SYS_PATINFO B ,ADM_INP C "
					+ " WHERE  B.MR_NO=A.MR_NO "
					+ " AND A.CASE_NO=C.CASE_NO"
					+ " AND A.STATION_CODE='"
					+ stationCode
					+ "' AND A.EXEC_DEPT_CODE='"
					+ orgCode
					+ "'"
					+ regionCode
					+ " AND  A.RX_KIND = 'UD' "
					+ " AND C.CANCEL_FLG='N' AND C.DS_DATE IS NULL "
					//fux modify 20160201 ���ڰ�ҩ ��ҩ��Ϣ���Բ�ѯ����ҩ
					//+ " AND A.DISPENSE_FLG = 'N' "
					+ " AND A.PHA_CHECK_DATE IS NOT NULL "
					+ " AND (A.ORDER_CAT1_CODE='PHA_W' "
					+ " OR A.ORDER_CAT1_CODE='PHA_C') "
					+ " AND (A.DC_DATE IS NULL OR A.DC_DATE >= "
					+ date
					+ ") "
					+ " GROUP BY A.CASE_NO, A.STATION_CODE,C.BED_NO,A.MR_NO, "
					+ " A.IPD_NO,B.PAT_NAME  ORDER BY A.STATION_CODE,A.CASE_NO ";
			TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
			result.addParm(temp);
		}
		return result;
	}

	/**
	 * 
	 * @param caseParm
	 *            TParm
	 * @param date
	 *            String
	 * @return TParm
	 */
	public TParm getDispenseSinglePat(TParm caseParm, String date) {
		TParm result = new TParm();
		if (caseParm == null || caseParm.getCount("CASE_NO") < 1)
			return result;
		int count = caseParm.getCount("CASE_NO");
		result.setData("SELECT_FLG", new Vector());
		result.setData("STATION_CODE", new Vector());
		result.setData("BED_NO", new Vector());
		result.setData("PAT_NAME", new Vector());
		result.setData("PHA_CHECK_DATE", new Vector());
		result.setData("PHA_DOSAGE_DATE", new Vector());
		result.setData("MR_NO", new Vector());
		result.setData("IPD_NO", new Vector());
		result.setData("CASE_NO", new Vector());
		for (int i = 0; i < count; i++) {
			String caseNo = caseParm.getValue("CASE_NO", i);
			String sql = " SELECT 'Y' AS SELECT_FLG, A.STATION_CODE,C.BED_NO,"
					+ " B.PAT_NAME,MAX(A.PHA_CHECK_DATE) AS PHA_CHECK_DATE,"
					+ " MAX(A.LAST_DSPN_DATE) AS PHA_DOSAGE_DATE, A.MR_NO,"
					+ " A.IPD_NO,A.CASE_NO "
					+ " FROM ODI_ORDER A,SYS_PATINFO B ,ADM_INP C "
					+ " WHERE A.CASE_NO='"
					+ caseNo
					+ "' AND B.MR_NO=A.MR_NO "
					+ " AND A.CASE_NO=C.CASE_NO AND A.RX_KIND = 'UD' "
					+ " AND C.CANCEL_FLG='N' AND C.DS_DATE IS NULL "
//					+ " AND A.DISPENSE_FLG = 'N' "
					+ " AND A.PHA_CHECK_DATE IS NOT NULL "
					+ " AND (A.ORDER_CAT1_CODE='PHA_W' "
					+ " OR A.ORDER_CAT1_CODE='PHA_C') "
					+ " AND (A.DC_DATE IS NULL OR A.DC_DATE >= "
					+ date
					+ ") "
					+ " GROUP BY A.CASE_NO, A.STATION_CODE,C.BED_NO,A.MR_NO, "
					+ " A.IPD_NO,B.PAT_NAME  ORDER BY A.STATION_CODE,A.CASE_NO ";
			// System.out.println("----"+sql);
			TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
			result.addParm(temp);
		}
		return result;
	}
	/**
	 * �õ�ͳҩ���ż���
	 * @return
	 */
	public Map getIntgMedNoMap(){
		Map map=new HashMap();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String timeStr=String.valueOf(now).substring(0, 10).replace("-", "");
		String sql=" SELECT DISTINCT INTGMED_NO,STATION_CODE FROM ODI_DSPNM WHERE DSPN_DATE " +
				" BETWEEN TO_DATE('"+timeStr+"','YYYYMMDD') "+
				" AND TO_DATE('"+timeStr+"235959','YYYYMMDDHH24MISS') AND DSPN_KIND='UD'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		for(int i=0;i<parm.getCount();i++){
			String stationCode=parm.getValue("STATION_CODE", i);
			if(map.get(stationCode)==null){
				map.put(stationCode, parm.getValue("INTGMED_NO", i));
			}else{
				continue;
			}
		}
		return map;
	}
	/**
	 * ��ҩ������δ�ɹ���CASE_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveDispatch(TParm parm, TConnection conn) throws Exception {
		TParm failed = new TParm();
		if (parm == null)
			return failed;
		TParm caseParm = parm.getParm("CASE_NO");
		sysparm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM ODI_SYSPARM"));
		if (caseParm == null || caseParm.getCount("CASE_NO") < 1)
			return failed;
		today = parm.getValue("TODAY");
		fromDate = parm.getValue("FROM_DATE");
		endDate = parm.getValue("END_DATE");
		effDate = parm.getValue("EFF_DATE");
		id = parm.getValue("ID");
		ip = parm.getValue("IP");
		now = TJDODBTool.getInstance().getDBTime();
		arrangeTime = TotQtyTool.getInstance().assignMedTime(
				effDate.substring(0, 12), fromDate, endDate);
		dspnTimelist = TotQtyTool.getInstance().getNextDispenseDttm(now);
		dspnTimes = TotQtyTool.getInstance().arrageTime();// סԺҩ����ҩʱ���
		yesterday = this.getYesterday();// ȡ����Ե�ǰʱ�����һ����ҩʱ��
		int count = caseParm.getCount("CASE_NO");
		Map MedoNmap=getIntgMedNoMap();
		for (int i = 0; i < count; i++) {
			String caseNo = caseParm.getValue("CASE_NO", i);
			String stationCode=caseParm.getValue("STATION_CODE", i);
			String MedNo="";
			if(MedoNmap.get(stationCode)==null){
				MedoNmap.put(stationCode, this.getIntgMedNo());
			}
			MedNo=String.valueOf(MedoNmap.get(stationCode));
			String msg = saveByCaseNo(caseNo,MedNo,conn);
			if (!"OK".equalsIgnoreCase(msg) && !"".equalsIgnoreCase(msg)) {
				failed.addData("CASE_NO", caseNo + ": " + msg);
				conn.rollback();
				continue;// luhai add 2012-04-14
			}
			conn.commit();
		}
		return failed;
	}
	/**
	 * ����ODI_ORDER��ODI_DSPNM,ODI_DSPND�粻�ɹ��򷵻�false
	 * 
	 * @param caseNo 
	 * @return
	 */
	public String saveByCaseNo(String caseNo,String medNo,TConnection conn)
			throws Exception {
		// luhai 2012-04-14 begin if else �ж� begin
		if (isNullString(caseNo)) {
			return "UddDispatch.saveByCaseNo->caseNo is null";
		} else if (effDate == null || effDate.length() < 12) {
			return "UddDispatch.saveByCaseNo->effDate is null";
		} else if (fromDate == null || fromDate.length() < 12) {
			return "UddDispatch.saveByCaseNo->fromDate is null";
		} else if (endDate == null || endDate.length() < 12) {
			return "UddDispatch.saveByCaseNo->endDate is null";
		}
		// luhai 2012-04-14 begin if else �ж� end
		TotQtyTool qtyTool = TotQtyTool.getInstance();
		TParm parm = new TParm(TJDODBTool.getInstance().select( 
				this.getSql(caseNo)));
		System.out.println("���Ҳ�����ҩ����sql��11111========" + this.getSql(caseNo));
		if (parm.getErrCode() != 0) {
			System.out.println("err:���Ҳ�����ҩ����sql��========" + this.getSql(caseNo));
		}
		List qtyParm = new ArrayList();
		TParm qtyInParm = new TParm();
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		String nowTime = now + sysparm.getValue("DSPN_TIME", 0); 
		int count = parm.getCount("ORDER_SEQ");
		for (int i = 0; i < count; i++) {
			TParm order = parm.getRow(i);
			// �жϻ�ʿ���ʱ���Ƿ����ڰ�ҩʱ��
			String check_date = parm.getValue("NS_CHECK_DATE", 0);
			check_date = check_date.substring(0, 4)
					+ check_date.substring(5, 7) + check_date.substring(8, 10)
					+ check_date.substring(11, 13)
					+ check_date.substring(14, 16);
			if (Long.parseLong(check_date) >= Long.parseLong(nowTime)) {
				continue;
			}
			qtyParm = qtyTool.getOdiUdQty(order, effDate, fromDate, endDate); // 20121015
																				// shibl
																				// modidfy
																				// �޸ĳ���ҩ���㷨
			System.out.println("111111111111111111:"+qtyParm);
			// luhai modify 2012-04-14 begin if else �жϵ���
			if (qtyParm == null || qtyParm.size() < 1) {
				continue;
			}
			List time = (ArrayList) qtyParm.get(0);
			if (time == null || time.size() < 1) {
				continue;
			} else if (!getSaveDspnDDate(qtyParm,order,medNo,conn)) {
				return "insert into odi_dspnd error";
			}
			// luhai modify 2012-04-14 end
			if (dspnTimes == null) {
				return "dspnTimes  is null";
			} else if (yesterday == null || yesterday.trim().length() < 1) {
				return "yesterday is null";
			} else if (!getSaveDspnMDate(qtyParm, order,medNo,conn, yesterday,
					dspnTimes)) {
				return "insert into odi_dspnm error";
			}
			// luhai modify 2012-04-14 end if else �жϵ���
			if (!getSaveODate(order, qtyParm, conn)) {
				return "update odi_dspnm error";
			}
		}
		//========pangben 2013-9-4 ��ӿ���ҩƷ���ڰ�ҩ����
		TParm result=onUDDPhaAntibacterial(caseNo, conn);
		if (result.getErrCode()<0) {
			return "insert into phaAnti error";
		}
		// luhai 2012-04-16 modify ��sql���鴫�� end
		return "OK";
	}

	/**
	 * ȡ��
	 * 
	 * @param caseNo
	 * @return
	 */
	private String getSql(String caseNo) {
		// luhai 2012-04-13 begin
		return this.INSERT_SOURCE_SQL
				+ "	WHERE B.CASE_NO=A.CASE_NO AND A.CASE_NO='"
				+ caseNo+ "'"
				+ "		  AND B.DS_DATE IS NULL "
				+ "	 	  AND (B.CANCEL_FLG IS NULL OR B.CANCEL_FLG ='N') "
				+ "		  AND A.PHA_CHECK_CODE IS NOT NULL "
				+ "		  AND (A.DC_DATE IS NULL OR A.DC_DATE > TO_DATE('"
				+ fromDate
				+ "','YYYYMMDDHH24MI') )"
				+ "		  AND  A.NS_CHECK_DATE < TO_DATE('"
				+ fromDate
				+ "','YYYYMMDDHH24MI') "
				+ "		  AND  A.EFF_DATE<=TO_DATE('"
				+ endDate
				+ "','YYYYMMDDHH24MI') "
				+ "		  AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C') "
//				+ "         AND A.DISPENSE_FLG='N' " 
				+" AND A.RX_KIND = 'UD'";
		// luhai modify 20120424 end
		// luhai 2012-04-13 end
	}
	/**
	 * ȡ�ÿ���ҩƷ����ҽ������
	 * 
	 * @param caseNo
	 * @return
	 * ===========pangben 2013-9-4 
	 */
	private String getSql(String caseNo,String tableName) {
		return this.INSERT_SOURCE_SQL+tableName
				+ "	WHERE  B.CASE_NO=A.CASE_NO AND A.ORDER_CODE=C.ORDER_CODE AND A.CASE_NO='"
				+ caseNo+ "'"
				+ "		  AND B.DS_DATE IS NULL "
				+ "	 	  AND (B.CANCEL_FLG IS NULL OR B.CANCEL_FLG ='N') "
				+ "		  AND A.PHA_CHECK_CODE IS NOT NULL "
				+ "		  AND (A.DC_DATE IS NULL OR A.DC_DATE > TO_DATE('"
				+ fromDate
				+ "','YYYYMMDDHH24MI') )"
				+ "		  AND  A.NS_CHECK_DATE < TO_DATE('"
				+ fromDate
				+ "','YYYYMMDDHH24MI') "
				+ "		  AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C') "
//				+ "         AND A.DISPENSE_FLG='N' "
				+ " AND A.RX_KIND = 'UD' AND C.ANTIBIOTIC_CODE IS NOT NULL";
	}
	/**
	 * Ϊ����ODI_DSPND��
	 * 
	 * @param qty
	 *            ��������ֵ
	 * @param order
	 *            OrderParm
	 * @return D���TParm
	 */
	public boolean getSaveDspnDDate(List qty, TParm order,String medNo, TConnection conn) {
		if (qty == null || qty.size() < 1) {
			// System.out.println("1");
			return false;
		}
		List times = (List) qty.get(0);
		if (times == null || times.size() < 1) {
			// System.out.println("2");
			return false;
		}

		Map qtyMap = (Map) qty.get(1);
		if (qtyMap == null) {
			// System.out.println("3");
			return false;
		}
		int time = times.size();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		// System.out.println("-----" + order);
		for (int i = 0; i < time; i++) {
			TParm dParm = new TParm();
			// CASE_NO
			dParm.setData("CASE_NO", order.getValue("CASE_NO"));
			// ORDER_NO
			dParm.setData("ORDER_NO", order.getValue("ORDER_NO"));
			// ORDER_SEQ
			dParm.setData("ORDER_SEQ", order.getValue("ORDER_SEQ"));
			// System.out.println("times.get(i)============="+times.get(i));
			String orderDttm = times.get(i) + "";
			if (!orderDttm.equalsIgnoreCase(StringTool.getString(
					StringTool.getTimestamp(orderDttm, "yyyyMMddHHmm"),
					"yyyyMMddHHmm"))) {
				// System.out.println("4");
				return false;
			}

			// ORDER_DATE
			dParm.setData("ORDER_DATE", orderDttm.substring(0, 8));
			dParm.setData("ORDER_DATETIME", orderDttm.substring(8));
			// STATION_CODE
			dParm.setData("STATION_CODE", order.getValue("STATION_CODE"));
			// BATCH_CODE
			// TREAT_START_TIME
			// TREAT_END_TIME
			// NURSE_DISPENSE_FLG
			// BAR_CODE
			// ORDER_CODE
			dParm.setData("ORDER_CODE", order.getValue("ORDER_CODE"));
			// DOSAGE_QTY
			// order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
			// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
			// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
			// M���dispenseQty M_DISPENSE_QTY
			// M���dispenseUnit M_DISPENSE_UNIT
			// M���dosageQty M_DOSAGE_QTY
			// M���dosageUnit M_DOSAGE_UNIT
			// D���MediQty D_MEDI_QTY
			// D���MediUnit D_MEDI_UNIT
			// D���dosageQty D_DOSAGE_QTY
			// D���dosageUnit D_DOSAGE_UNIT
			// zhangyong20101214 begin
			String order_code = order.getValue("ORDER_CODE");
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			dParm.setData(
					"MEDI_QTY",
					order.getDouble("MEDI_QTY"));
			dParm.setData(
					"MEDI_UNIT",
					order.getValue("MEDI_UNIT"));
			
			dParm.setData(
					"DOSAGE_QTY",
					TCM_Transform.getDouble(qtyMap.get("D_DOSAGE_QTY")));
			// DOSAGE_UNIT
			dParm.setData("DOSAGE_UNIT", qtyMap.get("D_DOSAGE_UNIT"));
			// zhangyong20101214 begin
			// TOT_AMT
			dParm.setData("TOT_AMT", order.getDouble("TOT_AMT"));
			// DC_DATE
			// PHA_DISPENSE_NO
			// PHA_DOSAGE_CODE
			// PHA_DOSAGE_DATE
			// PHA_DISPENSE_CODE
			// PHA_DISPENSE_DATE
			dParm.setData("NS_EXEC_CODE", order.getValue("NS_EXEC_CODE"));
			dParm.setData(
					"NS_EXEC_DATE",
					order.getData("NS_EXEC_DATE") == null ? new TNull(
							Timestamp.class) : orderParm
							.getData("NS_EXEC_DATE"));
			dParm.setData("TOT_AMT",
					qtyMap.get("TOT_AMTD") == null ? new TNull(Double.class)
							: qtyMap.get("TOT_AMTD"));

			// NS_EXEC_DC_CODE
			// NS_EXEC_DC_DATE
			// NS_USER
			// EXEC_NOTE
			// EXEC_DEPT_CODE	
			dParm.setData("EXEC_DEPT_CODE", order.getValue("EXEC_DEPT_CODE"));
			// BILL_FLG
			// CASHIER_CODE
			// CASHIER_DATE
			// PHA_RETN_CODE
			// PHA_RETN_DATE
			// TRANSMIT_RSN_CODE
			// STOPCHECK_USER
			// STOPCHECK_DATE
			// IBS_CASE_NO
			// IBS_CASE_NO_SEQ
			// OPT_USER
			dParm.setData("OPT_USER", id);
			// OPT_DATE
			dParm.setData("OPT_DATE", now);
			// OPT_TERM
			dParm.setData("OPT_TERM", ip);
			//INTGMED_NO
			dParm.setData("INTGMED_NO", medNo);
			
			dParm.setData("SKIN_RESULT", order.getValue("SKIN_RESULT"));
			
			//20150428 wukai add start ������Һ
			String stationSql = "SELECT NPIVAS_STATION FROM ODI_SYSPARM";
			TParm stationParm = new TParm(TJDODBTool.getInstance().select(stationSql));
			boolean stationFlg = true;
			if(stationParm != null && stationParm.getCount("NPIVAS_STATION")>0){
				String[] station = stationParm.getValue("NPIVAS_STATION", 0).split(";");
				for(int j=0;j<station.length;j++){
					if(station[j].equals(order.getValue("STATION_CODE"))){
						stationFlg = false;
						break;
					}
				}
			}
			
			String regionSql = "SELECT PIVAS_FLG FROM SYS_REGION";
			String sysparmSql = "SELECT IVA_UD FROM ODI_SYSPARM";
			String pharouteSql = "SELECT IVA_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
									+ order.getValue("ROUTE_CODE") + "'";
			String batchtimeSql = "SELECT BATCH_CODE FROM ODI_BATCHTIME WHERE '"
									+ orderDttm.substring(8)
									+"' BETWEEN TREAT_START_TIME AND TREAT_END_TIME "
									+ " AND PN_FLG='N'";
			TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
			TParm sysparmParm = new TParm(TJDODBTool.getInstance().select(sysparmSql));
			TParm pharouteParm = new TParm(TJDODBTool.getInstance().select(pharouteSql));
			TParm batchtimeParm = new TParm(TJDODBTool.getInstance().select(batchtimeSql));
			if(regionParm.getValue("PIVAS_FLG", 0).equals("Y") 
					&& sysparmParm.getValue("IVA_UD", 0).equals("Y") 
					&& pharouteParm.getValue("IVA_FLG", 0).equals("Y") 
					&& !batchtimeParm.getValue("BATCH_CODE", 0).equals("") 
					&& !order.getValue("LINK_NO").equals("") 
					&& order.getValue("LINK_NO") != null
					&& stationFlg){
				dParm.setData("IVA_FLG", "Y");
				dParm.setData("BATCH_CODE", batchtimeParm.getValue("BATCH_CODE", 0));
//				tmpParm.addData("CASE_NO", order.getValue("CASE_NO"));
//				tmpParm.addData("ORDER_NO", order.getValue("ORDER_NO"));
//				tmpParm.addData("ORDER_SEQ", order.getValue("ORDER_SEQ"));
//				tmpParm.addData("ORDER_DATE", orderDttm.substring(0, 8));
//				tmpParm.addData("ORDER_DATETIME", orderDttm.substring(8));
//				tmpParm.addData("ORDER_CODE", order.getValue("ORDER_CODE"));
			}else{
				dParm.setData("IVA_FLG", "N");
				dParm.setData("BATCH_CODE", "");
			}
			
			
			
			TParm Parm = UddChnCheckTool.getInstance().insertOdiDspnd(dParm,
					conn);
			if (Parm.getErrCode() < 0) {
				System.out.println("��ҩ����ODI_DSPND��ʧ��:" + dParm.getData());
				return false;
			}
		}
		return true;
	}

	/**
	 * Ϊ����ODI_DSPNM��
	 * 
	 * @param qty
	 *            ��������ֵ
	 * @param order
	 *            OrderParm
	 * @return D���TParm
	 */
	public boolean getSaveDspnMDate(List qty, TParm order, String medNo,TConnection conn,
			String yesterday, String[] dspnTimes) {
		List times = (List) qty.get(0);
		if (times == null || times.size() < 1)
			return false;
		Map qtyMap = (Map) qty.get(1);
		int time = times.size();
		if (qtyMap == null)
			return false;
		TParm mParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		// CASE_NO
		mParm.setData("CASE_NO", order.getValue("CASE_NO"));
		// ORDER_NO
		mParm.setData("ORDER_NO", order.getValue("ORDER_NO"));
		// ORDER_SEQ
		mParm.setData("ORDER_SEQ", order.getValue("ORDER_SEQ"));
		String endMMdd = ((String) times.get(time - 1)).substring(8);
		String yesDttm = yesterday + dspnTimes[0];
		String today = dspnTimelist.get(0) + "";// shibl modify 20121015
												// �޸�Ϊȡȫ�ֱ��� dspnTimelist
		// START_DTTM
		mParm.setData("START_DTTM", yesDttm);
		// END_DTTM
		mParm.setData("END_DTTM", today);
		// REGION_CODE
		mParm.setData("REGION_CODE", order.getValue("REGION_CODE"));
		// STATION_CODE
		mParm.setData("STATION_CODE", order.getValue("STATION_CODE"));
		// DEPT_CODE
		mParm.setData("DEPT_CODE", order.getValue("DEPT_CODE"));
		// VS_DR_CODE
		mParm.setData("VS_DR_CODE", order.getValue("VS_DR_CODE"));
		// BED_NO
		mParm.setData("BED_NO", order.getValue("BED_NO"));
		// IPD_NO
		mParm.setData("IPD_NO", order.getValue("IPD_NO"));
		// MR_NO
		mParm.setData("MR_NO", order.getValue("MR_NO"));
		// RX_KIND
		mParm.setData("DSPN_KIND", order.getValue("RX_KIND"));

		// DSPN_DATE
		mParm.setData("DSPN_DATE", now);
		// DSPN_USER
		mParm.setData("DSPN_USER", id);
		// DISPENSE_EFF_DATE
		mParm.setData("DISPENSE_EFF_DATE",
				StringTool.getTimestamp(fromDate, "yyyyMMddHHmm"));
		// DISPENSE_END_DATE
		mParm.setData("DISPENSE_END_DATE",
				StringTool.getTimestamp(endDate, "yyyyMMddHHmm"));
		// EXEC_DEPT_CODE
		mParm.setData("EXEC_DEPT_CODE", order.getValue("EXEC_DEPT_CODE"));
		// AGENCY_ORG_CODE
		mParm.setData("AGENCY_ORG_CODE", order.getValue("AGENCY_ORG_CODE"));
		// PRESCRIPT_NO
		mParm.setData("PRESCRIPT_NO", order.getValue("PRESCRIPT_NO"));
		// CAT1_TYPE
		mParm.setData("CAT1_TYPE", order.getValue("CAT1_TYPE"));
		// LINKMAIN_FLG
		mParm.setData("LINKMAIN_FLG", order.getValue("LINKMAIN_FLG"));
		// LINK_NO
		mParm.setData("LINK_NO", order.getValue("LINK_NO"));
		// ORDER_CODE
		mParm.setData("ORDER_CODE", order.getValue("ORDER_CODE"));
		// ORDER_DESC
		mParm.setData("ORDER_DESC", order.getValue("ORDER_DESC"));
		// GOODS_DESC
		mParm.setData("GOODS_DESC", order.getValue("GOODS_DESC"));
		// SPECIFICATION
		mParm.setData("SPECIFICATION", order.getValue("SPECIFICATION"));
		// MEDI_QTY
		mParm.setData("MEDI_QTY", order.getDouble("MEDI_QTY"));
		// MEDI_UNIT
		mParm.setData("MEDI_UNIT", order.getValue("MEDI_UNIT"));
		// FREQ_CODE
		mParm.setData("FREQ_CODE", order.getValue("FREQ_CODE"));
		// ROUTE_CODE
		mParm.setData("ROUTE_CODE", order.getValue("ROUTE_CODE"));
		// TAKE_DAYS
		mParm.setData("TAKE_DAYS", order.getInt("TAKE_DAYS"));
		// OWN_PRICE
		mParm.setData("OWN_PRICE", qtyMap.get("OWN_PRICE"));
		// NHI_PRICE
		mParm.setData("NHI_PRICE", qtyMap.get("NHI_PRICE"));
		// OWN_AMT
		mParm.setData("OWN_AMT", qtyMap.get("OWN_AMT") == null ? new TNull(
				Double.class) : qtyMap.get("TOT_AMTM"));
		// TOT_AMT
		mParm.setData("TOT_AMT", qtyMap.get("TOT_AMTM") == null ? new TNull(
				Double.class) : qtyMap.get("TOT_AMTM"));
		// order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		// M���dispenseQty M_DISPENSE_QTY
		// M���dispenseUnit M_DISPENSE_UNIT
		// M���dosageQty M_DOSAGE_QTY
		// M���dosageUnit M_DOSAGE_UNIT
		// D���MediQty D_MEDI_QTY
		// D���MediUnit D_MEDI_UNIT
		// D���dosageQty D_DOSAGE_QTY
		// D���dosageUnit D_DOSAGE_UNIT
		// DOSAGE_QTY
		mParm.setData("DOSAGE_QTY",
				TCM_Transform.getDouble(qtyMap.get("M_DOSAGE_QTY")));
		// DOSAGE_UNIT
		mParm.setData("DOSAGE_UNIT", qtyMap.get("M_DOSAGE_UNIT"));
		// DISPENSE_QTY
		mParm.setData("DISPENSE_QTY",
				TCM_Transform.getDouble(qtyMap.get("M_DISPENSE_QTY")));
		// DISPENSE_UNIT
		mParm.setData("DISPENSE_UNIT", qtyMap.get("M_DISPENSE_UNIT"));
		// DISPENSE_FLG
		mParm.setData("DISPENSE_FLG", order.getValue("DISPENSE_FLG"));
		// GIVEBOX_FLG
		mParm.setData("GIVEBOX_FLG", order.getValue("GIVEBOX_FLG"));
		// PHA_CHECK_CODE
		mParm.setData("PHA_CHECK_CODE", order.getValue("PHA_CHECK_CODE"));
		// PHA_CHECK_DATE
		mParm.setData("PHA_CHECK_DATE", order.getData("PHA_CHECK_DATE"));
		// OWN_PRICE
		mParm.setData("OWN_PRICE", order.getDouble("OWN_PRICE"));
		// NHI_PRICE
		mParm.setData("NHI_PRICE", order.getDouble("NHI_PRICE"));
		// DISCOUNT_RATE
		mParm.setData("DISCOUNT_RATE", order.getDouble("DISCOUNT_RATE"));
		// //OWN_AMT
		// m.setItem(row, "CASE_NO", order.getValue("CASE_NO"));
		// //TOT_AMT
		// m.setItem(row, "CASE_NO", order.getValue("CASE_NO"));
		// ORDER_DATE
		mParm.setData("ORDER_DATE", order.getTimestamp("EFF_DATE"));
		// ORDER_DEPT_CODE
		mParm.setData("ORDER_DEPT_CODE", order.getValue("ORDER_DEPT_CODE"));
		// ORDER_DR_CODE
		mParm.setData("ORDER_DR_CODE", order.getValue("ORDER_DR_CODE"));
		// DR_NOTE
		mParm.setData("DR_NOTE", order.getValue("DR_NOTE"));
		// ATC_FLG
		mParm.setData("ATC_FLG", order.getValue("ATC_FLG"));
		// SENDATC_FLG
		mParm.setData("SENDATC_FLG", order.getValue("SENDATC_FLG"));
		// SENDATC_DTTM
		if (isNullString(order.getValue("SENDATC_DTTM") + "")) {
			// //System.out.println("sendatcDttm is null");
			mParm.setData("SENDATC_DTTM", new TNull(String.class));
		} else {
			mParm.setData("SENDATC_DTTM", order.getValue("SENDATC_DTTM"));
		}
		// m.setItem(row, "SENDATC_DTTM", order.getValue("SENDATC_DTTM"));
		// INJPRAC_GROUP
		mParm.setData("INJPRAC_GROUP", 0);
		// OPT_USER
		mParm.setData("OPT_USER", id);
		// OPT_DATE
		mParm.setData("OPT_DATE", now);
		// OPT_TERM
		mParm.setData("OPT_TERM", ip);
		// ORDER_CAT1_CODE
		mParm.setData("ORDER_CAT1_CODE", order.getValue("ORDER_CAT1_CODE"));
		// RX_NO
		mParm.setData("RX_NO", order.getValue("RX_NO"));
		// PRESRT_NO
		mParm.setData("PRESRT_NO", order.getValue("PRESRT_NO"));
		// HIDE_FLG
		mParm.setData("HIDE_FLG", order.getValue("HIDE_FLG"));
		// luhai modify 2012-04-14 �޸�DOSE_TYPE ��ҩû�и�ֵ���� begin
		mParm.setData("DOSE_TYPE", order.getValue("DOSE_TYPE"));
		// luhai modify 2012-04-14 �޸�DOSE_TYPE ��ҩû�и�ֵ���� end
		//ͳҩ����   INTGMED_NO
		mParm.setData("INTGMED_NO", medNo);
		//����
		mParm.setData("INFLUTION_RATE", order.getDouble("INFLUTION_RATE"));
		//add by wukai on 20170323   ������Һ  start
		boolean batchFlg = false;
		for (int i = 0; i < time; i++) {
			String orderDttm = times.get(i) + "";
			if (!orderDttm.equalsIgnoreCase(StringTool.getString(
					StringTool.getTimestamp(orderDttm, "yyyyMMddHHmm"),
					"yyyyMMddHHmm"))) {
				// System.out.println("4");
				return false;
			}
			String batchtimeSql = "SELECT BATCH_CODE FROM ODI_BATCHTIME WHERE '"
					+orderDttm.substring(8)
					+"' BETWEEN TREAT_START_TIME AND TREAT_END_TIME "
					+ " AND PN_FLG='N'";
			TParm batchtimeParm = new TParm(TJDODBTool.getInstance().select(batchtimeSql));
			if(!batchtimeParm.getValue("BATCH_CODE", 0).equals("")){
				batchFlg = true;
				break;
			}
			// ORDER_DATE
//			dParm.setData("ORDER_DATE", orderDttm.substring(0, 8));
//			dParm.setData("ORDER_DATETIME", orderDttm.substring(8));
		}
		
		String stationSql = "SELECT NPIVAS_STATION FROM ODI_SYSPARM";
		TParm stationParm = new TParm(TJDODBTool.getInstance().select(stationSql));
		boolean stationFlg = true;
		if(stationParm != null && stationParm.getCount("NPIVAS_STATION")>0){
			String[] station = stationParm.getValue("NPIVAS_STATION", 0).split(";");
			for(int j=0;j<station.length;j++){
				if(station[j].equals(order.getValue("STATION_CODE"))){
					stationFlg = false;
					break;
				}
			}
		}
		String regionSql = "SELECT PIVAS_FLG FROM SYS_REGION";
		String sysparmSql = "SELECT IVA_UD FROM ODI_SYSPARM";
		String pharouteSql = "SELECT IVA_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
								+order.getValue("ROUTE_CODE")+"'";
		
		TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
		TParm sysparmParm = new TParm(TJDODBTool.getInstance().select(sysparmSql));
		TParm pharouteParm = new TParm(TJDODBTool.getInstance().select(pharouteSql));
		
		if(regionParm.getValue("PIVAS_FLG", 0).equals("Y") 
				&& sysparmParm.getValue("IVA_UD", 0).equals("Y") 
				&& pharouteParm.getValue("IVA_FLG", 0).equals("Y") 
				&& batchFlg 
//				&& !batchtimeParm.getValue("BATCH_CODE", 0).equals("") 
				&& !order.getValue("LINK_NO").equals("") 
				&& order.getValue("LINK_NO") != null
				&& stationFlg){
			mParm.setData("IVA_FLG", "Y");
		}else{
			mParm.setData("IVA_FLG", "N");
		}
		//add by wukai on 20170323   ������Һ  end
		System.out.println("����ǰ11111111111��"+mParm);
		
		TParm Parm = UddChnCheckTool.getInstance().insertOdiDspnm(mParm, conn);
		if (Parm.getErrCode() < 0) {
			System.out.println("��ҩ����ODI_DSPNM��ʧ��:" + mParm.getData());
			return false;
		}
		return true;
	}
	/**
	 * ����ODI_ORDER��
	 * 
	 * @param orderS
	 *            OrderParm
	 * @return O���TParm
	 */
	public boolean getSaveODate(TParm order, List qty, TConnection conn) {
		Map qtyMap = (Map) qty.get(1);
		if (qtyMap == null) {
			// System.out.println("qtyMap is null");
			return false;
		}
		// System.out.println("orderParm==="+order);
		String lastDspn = fromDate;
		TParm oParm = new TParm();
		oParm.setData("CASE_NO", order.getValue("CASE_NO"));
		oParm.setData("ORDER_NO", order.getValue("ORDER_NO"));
		oParm.setData("ORDER_SEQ", order.getData("ORDER_SEQ"));
		// System.out.println("TDS.show");
		// o.showDebug();
		// order���LASTDSPN_QTY ORDER_LASTDSPN_QTY
		// order���ACUMDSPN_QTY ORDER_ACUMDSPN_QTY
		// order���ACUMMEDI_QTY ORDER_ACUMMEDI_QTY
		oParm.setData("ACUMDSPN_QTY", qtyMap.get("ORDER_ACUMDSPN_QTY"));
		oParm.setData("LASTDSPN_QTY", qtyMap.get("ORDER_LASTDSPN_QTY"));
		oParm.setData("ACUMMEDI_QTY", qtyMap.get("ORDER_ACUMMEDI_QTY"));
		// LAST_DSPN_DATE
		oParm.setData("LAST_DSPN_DATE",
				StringTool.getTimestamp(lastDspn, "yyyyMMddHHmm"));
		// OPT_USER

		oParm.setData("OPT_USER", id);
		// OPT_DATE
		oParm.setData("OPT_DATE", now);
		// OPT_TERM
		oParm.setData("OPT_TERM", ip);
		TParm Parm = UddChnCheckTool.getInstance().updateOdiOrder(oParm, conn);
		if (Parm.getErrCode() < 0) {
			System.out.println("��ҩ����ODI_ORDER��ʧ��:" + oParm.getData());
			return false;
		}
		return true;
	}
	/**
	 * �ж��ǲ��ǿ��ַ���
	 * 
	 * @param s
	 * @return boolean Y:�գ�N:����
	 */
	public static boolean isNullString(String s) {
		return (s == null || s.length() < 1);
	}

	/**
	 * ����CASE_NO����boolean��true����δ���ҩ,false:û��δ���ҩ
	 * 
	 * @param caseNo
	 * @return
	 */
	public boolean checkLeftDrugForAdm(String caseNo) {
		if (this.isNullString(caseNo)) {
			// System.out.println("caseNo is null");
			return true;
		}
		String sql = this.LEFT_DRUG_SQL.replaceFirst("#", caseNo);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = result.getInt("COUNT", 0);
		return count > 0;
	}

	/**
	 * ȡ����Ե�ǰʱ�����һ����ҩʱ��
	 * 
	 * @return
	 */
	private String getYesterday() {
		String yesterday = "";
		String today = StringTool.getString(TJDODBTool.getInstance()
				.getDBTime(), "yyyy-MM-dd");
		String sql = " SELECT MAX(SCH_DATE) SCH_DATE FROM ODI_DISTDATE WHERE SCH_DATE <=TO_DATE('"
				+ today + "','YYYY-MM-DD') AND DIST_FLG='Y'";
		TParm yesterdayParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (yesterdayParm.getErrCode() != 0) {
			return "";
		}
		yesterday = StringTool.getString(
				yesterdayParm.getTimestamp("SCH_DATE", 0), "yyyyMMdd");
		return yesterday;
	}
	 /**
     * ����ҩƷ����ҽ����ҩ����
     * @return
     * ==========pangben 2013-9-4
     */
    private TParm onUDDPhaAntibacterial(String caseNo, TConnection conn){
    	TParm odiOrderParm = new TParm(TJDODBTool.getInstance().select(
				this.getSql(caseNo,",PHA_BASE C")));
    	if (odiOrderParm.getErrCode() != 0) {
			System.out
					.println("err:����ҩƷ����ҽ����ҩ����sql��===" +this.getSql(caseNo,",PHA_BASE C"));
		}
    	if (odiOrderParm.getCount()<=0) {
			return new TParm();
		}
    	TParm tempParm=null;
    	TParm result=new TParm();
    	//yanjing�����������PHA_SEQ��SEQ_NO
		String phaSeq = SystemTool.getInstance().getDate().toString()
	      .substring(0, 10).replace("/", "").replace("-", "");
//		tempParm.setData("PHA_SEQ", phaSeq);
		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM PHA_ANTI WHERE CASE_NO = '" + 
		caseNo+ "' " + 
	      "AND PHA_SEQ = '" + phaSeq + "' ";
	    TParm seqResult = new TParm(TJDODBTool.getInstance().select(sql));
	       if (seqResult.getErrCode() < 0) {
	  	      return seqResult;
	  	        }
    	for (int i = 0; i < odiOrderParm.getCount(); i++) {
    		tempParm=odiOrderParm.getRow(i);
    		tempParm.setData("PHA_SEQ", phaSeq);
  	    
  	    int seq_no = seqResult.getInt("SEQ_NO", 0) + i+1;
  	  tempParm.setData("SEQ_NO", Integer.valueOf(seq_no));
  	    tempParm.setData("LINKMAIN_FLG", "");//yanjing 20140630
  	    tempParm.setData("LINK_NO", "");//yanjing 20140630
  	    tempParm.setData("NODE_FLG", "N");
  	    tempParm.setData("APPROVE_FLG", "N");
  	    tempParm.setData("USE_FLG", "Y");
  	    tempParm.setData("ANTI_REASON", "");
  	    tempParm.setData("ANTI_MAX_DAYS", 0);
  	    tempParm.setData("OVERRIDE_FLG", "N");
  	    tempParm.setData("CHECK_FLG", "N");
//  	    System.out.println("���ڰ�ҩ���tempParm is��"+tempParm);
		result=PhaAntiTool.getInstance().insertPhaAnti(tempParm, conn);
		if (result.getErrCode()<0) {
			return result;
		}
		}
    	return result;
    }
    
    
    /**
     * д���ӡ������ԱID��ʱ��
     * @param BAR_CODE
     * @return
     * 20150319 wangjingchun add
     */
    public TParm updatePrintBottleUser(String BAR_CODE){
    	SimpleDateFormat logFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String sqlD = "UPDATE ODI_DSPND SET PRT_BARCODE_USER='"
				+Operator.getID()+"',PRT_BARCODE_TIME=to_date('"
				+logFormater.format(new Date())+"','yyyy-mm-dd hh24:mi:ss') WHERE BAR_CODE='"
				+BAR_CODE+"'";
    	TParm resultD = new TParm(TJDODBTool.getInstance().update(sqlD));
    	return resultD;
    }
    
    
    
    
    
    
    
}
