package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import java.net.URLEncoder;
import java.sql.Timestamp;
import com.dongyang.util.TDebug;
import com.dongyang.util.StringTool;
import com.dongyang.config.TConfig;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.db.TConnection;

/**
 * 
 * <p>
 * Title: 系统数据对象
 * </p>
 * 
 * <p>
 * Description: 处理有关系统的全部数据处理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author lzk 2008-08-11
 * @version 1.0
 */
public class SystemTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static SystemTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return SystemTool
	 */
	public static SystemTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SystemTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SystemTool() {
		setModuleName("sys\\SYSSystemModule.x");
		onInit();
	}

	/**
	 * 得到系统时间
	 * 
	 * @return Timestamp 生效日期
	 */
	public Timestamp getDate() {
		TParm parm = new TParm();
		return getResultTimestamp(query("getDate", parm), "SYSDATE");
	}
	
	public String getUpdateTime(){
		TParm parm = new TParm();
		return getResultString(query("getUpdateTime", parm), "UPDATETIME");
	}

	/**
	 * 得到病案号
	 * 
	 * @return String
	 */
	public String getMrNo() {
		return getResultString(call("getMrNo"), "MR_NO");
	}

	/**
	 * 得到住院号
	 * 
	 * @return String
	 */
	public String getIpdNo() {
		return getResultString(call("getIpdNo"), "IPD_NO");
	}

	/**
	 * 得到取号原则
	 * 
	 * @param regionCode
	 *            String
	 * @param systemCode
	 *            String
	 * @param operation
	 *            String
	 * @param section
	 *            String
	 * @return String
	 */
	public synchronized String getNo(String regionCode, String systemCode,
			String operation, String section) {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", regionCode);
		parm.setData("SYSTEM_CODE", systemCode);
		parm.setData("OPERATION", operation);
		parm.setData("SECTION", section);
		return getResultString(call("getNo", parm), "NO");
	}

	/**
	 * 拼音码
	 * 
	 * @param CNS_STR
	 *            String
	 * @return String
	 */
	public String charToCode(String CNS_STR) {
		SYSHzpyTool syshzpytool = new SYSHzpyTool();
		return syshzpytool.charToCode(CNS_STR);

	}

	/**
	 * 获得主院区的简称
	 * 
	 * @return String
	 */
	public String getRegionAbs() {
		SYSRegionTool sysregiontool = new SYSRegionTool();
		return sysregiontool.selRegionABN();
	}

	/**
	 * 为3.0做的取号原则
	 * 
	 * @param hospArea
	 *            String
	 * @param regionCode
	 *            String
	 * @param systemCode
	 *            String
	 * @param operation
	 *            String
	 * @param section
	 *            String
	 * @return String
	 */
	public synchronized String getNo(String hospArea, String regionCode,
			String systemCode, String operation, String section) {
		TParm parm = new TParm();
		parm.setData("HOSP_AREA", hospArea);
		parm.setData("REGION_CODE", regionCode);
		parm.setData("SYSTEM_CODE", systemCode);
		parm.setData("OPERATION", operation);
		parm.setData("SECTION", section);
		TParm parm1 = call("get3No", parm);
		// System.out.println(""+parm1);
		return getResultString(parm1, "NO");
	}

	/**
	 * 得到物资验收入库入库单号
	 * 
	 * @return String
	 */
	public String getVerifyinNo() {
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 4;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String verifyinNo = getNo("HIS", "ALL", "INV", "VERIFYIN", "NO");
		for (int i = verifyinNo.length(); i < length; i++) {
			verifyinNo = "0" + verifyinNo;
		}
		return time.substring(2, time.length()) + verifyinNo;
	}

	/**
	 * 得到物资出库单号
	 * 
	 * @return String
	 */
	public String getDispense() {
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 4;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String verifyinNo = getNo("HIS", "ALL", "INV", "DISPENSE", "NO");
		for (int i = verifyinNo.length(); i < length; i++) {
			verifyinNo = "0" + verifyinNo;
		}
		return time.substring(2, time.length()) + verifyinNo;
	}

	/**
	 * 得到物资出库单号
	 * 
	 * @return String
	 */
	public String getSUDispense() {
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 4;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String verifyinNo = getNo("HIS", "ALL", "INV", "SUDISPENSE", "NO");
		for (int i = verifyinNo.length(); i < length; i++) {
			verifyinNo = "0" + verifyinNo;
		}
		return time.substring(2, time.length()) + verifyinNo;
	}

	/**
	 * 得到住院医生站中医的处方签号
	 * 
	 * @return String
	 */
	public String getChnRxNo() {
		// System.out.println("start herer");
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 6;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String verifyinNo = getNo("HIS", "ALL", "ODI", "CHNRX", "CHNRX");
		// System.out.println("veryin===="+verifyinNo);
		for (int i = verifyinNo.length(); i < length; i++) {
			verifyinNo = "0" + verifyinNo;
		}
		return time.substring(2, time.length()) + verifyinNo;
	}

	/**
	 * 得到物资出库单号
	 * 
	 * @return String SUP_DETAIL_NO
	 */
	public String getSupDetailNo() {
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 4;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String supDetailNo = getNo("HIS", "ALL", "INV", "SUPDETAIL", "NO");
		for (int i = supDetailNo.length(); i < length; i++) {
			supDetailNo = "0" + supDetailNo;
		}
		return time.substring(2, time.length()) + supDetailNo;
	}

	/**
	 * 得到科室请领单号
	 * 
	 * @return String REQUEST_NO
	 */
	public String getRequestNo() {
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 4;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String supDetailNo = getNo("HIS", "ALL", "INV", "REQUEST", "NO");
		for (int i = supDetailNo.length(); i < length; i++) {
			supDetailNo = "0" + supDetailNo;
		}
		return time.substring(2, time.length()) + supDetailNo;
	}

	/**
	 * 得到DSPNM的SheetNo
	 * 
	 * @return String
	 */
	public String getChnSheetNo() {
		/*
		 * String[] Data={"HIS","UDD","UDDDspn","UDDDspn","5","ALL"}; String
		 * Hosp_Area = All_Data[0]; String SYSTEM_CODE = All_Data[1]; String
		 * OPERATION = All_Data[2]; String SUB1_OPERATION = All_Data[3]; String
		 * CaseNo_length = All_Data[4]; String Region_Code = All_Data[5];
		 * 
		 * vctCaseno1.add(Hosp_Area); vctCaseno1.add(Region_Code);
		 * vctCaseno1.add(SYSTEM_CODE); vctCaseno1.add(OPERATION);
		 * vctCaseno1.add(SUB1_OPERATION);
		 */
		Timestamp date = SystemTool.getInstance().getDate();
		String time = StringTool.getString(date, "yyyyMMdd");
		int length = 5;
		// "HIS","ALL", "INV","VERIFYIN","VERIFYIN"
		String sheetNo = getNo("HIS", "ALL", "UDD", "UDDDspn", "UDDDspn");
		// System.out.println("veryin===="+sheetNo);
		for (int i = sheetNo.length(); i < length; i++) {
			sheetNo = "0" + sheetNo;
		}
		return time.substring(2, time.length()) + sheetNo;
	}

	public static void main(String args[]) {
		TDebug.initClient();
		SystemTool tool = new SystemTool();
		// System.out.println(tool.getDate());

		// System.out.println("tool.getMrNo()=" + tool.getMrNo());
		// System.out.println("tool.getIpdNo()=" + tool.getIpdNo());
		// System.out.println("getChnRxNo()" + tool.getRequestNo());
		// System.out.println(tool.getRegionAbs());
	}

	/**
	 * 指定URL 打开IE
	 * 
	 * @param urlString
	 *            String
	 */
	public void OpenIE(String urlString) {
		try {
			Runtime.getRuntime().exec(
					String.valueOf(String.valueOf((new StringBuffer(
							"cmd.exe /c start iexplore \"")).append(urlString)
							.append("\""))));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 
     * @param url
     */
	public static void openExe(String url) {
		Runtime rn = Runtime.getRuntime();
		Process p = null;
		try {
			p = rn.exec(url);
		} catch (Exception e) {
			System.out.println("Error exec!");
		}
	}

	/**
	 * 打开检验报告
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @param startDate
	 *            String 开始时间(门急诊就诊时间，住院入院时间，健康检查报道时间 2010-04-15)
	 */
	public void OpenLisWeb(String mrNo, Timestamp startDate) {
		String date = StringTool.getString(startDate, "yyyy-MM-dd");
		String url = "http://" + TConfig.getSystemValue("LABIP")
				+ "/yd/index.aspx?patient_id=" + mrNo;
		OpenIE(url);
	}

	/**
	 * 打开检验报告
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @param startDate
	 *            Timestamp 开始时间(门急诊就诊时间，住院入院时间，健康检查报道时间 2010-04-15)
	 * @param labNo
	 *            String
	 */
	public void OpenLisWeb(String mrNo, Timestamp startDate, String labNo) {
		String date = StringTool.getString(startDate, "yyyy-MM-dd");
		String url = "http://" + TConfig.getSystemValue("LABIP")
				+ "/yd/index.aspx?patient_id=" + mrNo + "&requisition_id="
				+ labNo;
		OpenIE(url);
	}

	/**
	 * 打开检验报告
	 * 
	 * @param mrNo
	 *            String
	 * @param startDate
	 *            Timestamp
	 * @param labNo
	 *            String
	 * @param orderCode
	 *            String
	 * @param deptCode
	 *            String
	 * @param stationCode
	 *            String
	 */
	public void OpenLisWeb(String mrNo, Timestamp startDate, String labNo,
			String orderCode, String deptCode, String stationCode) {
		String date = StringTool.getString(startDate, "yyyy-MM-dd");
		// String url =
		// "http://"+TConfig.getSystemValue("LABIP")+"/新和报告查询/YanDaLogin.aspx?infos="+labNo+"!"+orderCode+"!"+mrNo+"!"+deptCode+"!"+stationCode;
		String url = "http://" + TConfig.getSystemValue("LABIP")
				+ "/BlueCore/jsp/lis/report.jsp?MR_NO=" + mrNo;
		OpenIE(url);
	}

	// /**
	// * 打开泰心RIS报告
	// * @param mrNo String
	// */
	// public void OpenRisWeb(String mrNo) {
	// String url = "http://" + TConfig.getSystemValue("RISIP") +
	// "/ami/html/webviewer.html?showlist&un=his&pw=hishis&ris_pat_id=" +
	// mrNo;
	// OpenIE(url);
	// }
	/**
	 * 打开爱育华RIS报告
	 * 
	 * @param mrNo
	 *            String
	 */
	public void OpenRisWeb(String mrNo) {
//		String sql = " SELECT   IMAGE_URL  FROM MED_RPTDTL A,MED_APPLY B "
//				+ " WHERE A.APPLICATION_NO=B.APPLICATION_NO "
//				+ " AND  A.CAT1_TYPE=B.CAT1_TYPE " + " AND B.MR_NO='" + mrNo
//				+ "'" + " AND B.cat1_type='RIS' ORDER BY A.OPT_DATE DESC";
//		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String url = "http://" + TConfig.getSystemValue("RISIP")
				+ "/DicomWeb/DicomWeb.dll/login?PTNID="+mrNo+"&User=user&Password=user";
		OpenIE(url);
	}

	/**
	 * 打开泰心糖尿病报告
	 * 
	 * @param mrNo
	 *            String
	 */
	public void OpenTnbWeb(String mrNo) {
		String url = "http://" + TConfig.getSystemValue("TNBIP")
				+ "/chart/chart_main.php?id=" + mrNo;
		OpenIE(url);
	}

	/**
	 * 得到二代身份证路劲
	 * 
	 * @param mrNo
	 *            String
	 */
	public String Getdir() {
		String dir = TConfig.getSystemValue("sid.path");
		return dir;
	}

	/**
	 * 打开泰心LIS报告
	 * 
	 * @param mrNo
	 *            String
	 */
	public void OpenLisWeb(String mrNo) {
		String url = "http://" + TConfig.getSystemValue("LABIP")
				+ "/ReportForm/ui/ReportSearch/?patno=" + mrNo;
		OpenIE(url);
	}

	/**
	 * 打开爱育华LIS打印报告
	 * 
	 * @param mrNo
	 *            String
	 */
	public void OpenLisReportWeb(TParm parm) {
		TConfig config = TConfig.getConfig("WEB-INF/config/system/THl7.x");
		String line="Result_Client.exe cname:"+parm.getValue("PAT_NAME")+";patno:"+parm.getValue("MR_NO")+
		            ";StartDate:"+parm.getValue("START_DATE")+";EndDate:"+parm.getValue("END_DATE");
		String url = config.getString("LABreportPath")
				+ "/"+line;
		openExe(url);
	}

	/**
	 * 日期格式整理
	 * 
	 * @param name
	 *            String
	 * @param flg
	 *            boolean
	 * @return Object
	 */
	public Object getDateReplace(String name, boolean flg) {
		if (null != name && name.trim().length() > 0) {
			name = name.replace(":", "").replace("/", "").replace(" ", "")
					.replace("-", "");
			if (name.length() > 8) {
				if (name.contains(".")) {
					return name.substring(0, name.lastIndexOf("."));
				}
				return name;

			} else {
				if (flg) {
					return name + "000000";
				} else {
					return name + "235959";
				}

			}
		}
		return new TNull(String.class);
	}

	/**
	 * 批次日志记录
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertBatchLog(TParm parm, TConnection conn) {
		String sql = " INSERT INTO SYS_FALSE_BATCH_LOG "
				+ "             (POST_DATE, SYSTEM_CODE,SEQ, CASE_NO, MR_NO, IPD_NO, DEPT_CODE,"
				+ "             STATION_CODE, STATUS, OPT_USER, OPT_DATE, OPT_TERM) "
				+ "      VALUES ('"
				+ parm.getValue("POST_DATE")
				+ "', '"
				+ parm.getValue("SYSTEM_CODE")
				+ "',"
				+ "              '"
				+ parm.getValue("SEQ")
				+ "', '"
				+ parm.getValue("CASE_NO")
				+ "',"
				+ "              '"
				+ parm.getValue("MR_NO")
				+ "', '"
				+ parm.getValue("IPD_NO")
				+ "',"
				+ "              '"
				+ parm.getValue("DEPT_CODE")
				+ "', '"
				+ parm.getValue("STATION_CODE")
				+ "',"
				+ "              '"
				+ parm.getValue("STATUS")
				+ "', '"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ "              SYSDATE,'"
				+ parm.getValue("OPT_TERM")
				+ "') ";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询最大顺序号
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMaxBatchSeq(TParm parm) {
		String sql = "SELECT MAX(SEQ) AS SEQ " + "  FROM SYS_FALSE_BATCH_LOG "
				+ " WHERE POST_DATE = '" + parm.getValue("POST_DATE") + "' "
				+ "   AND SYSTEM_CODE = '" + parm.getValue("SYSTEM_CODE")
				+ "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
