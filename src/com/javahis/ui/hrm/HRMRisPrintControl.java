package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import jdo.device.CallNo;
import jdo.device.LAB_Service;
import jdo.device.LAB_Service.DynamecMassage;
import jdo.hrm.HRMContractD;
import jdo.opd.OPDSysParmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.javahis.xml.Item;
import com.javahis.xml.Job;

/**
 * <p>
 * Title: 检查单打印Control
 * </p>
 * 
 * <p>
 * Description: 检查单打印Control
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
 * @author wangm
 * @version 1.0
 */
public class HRMRisPrintControl extends TControl {
	/**
	 * 动作类名称
	 */
	private String actionName = "action.hrm.HRMRisPrintAction";

	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * 门急住别
	 */
	private String admType; // 本类只有H类型
	/**
	 * 门诊看诊日期住院为当前日期
	 */
	private Timestamp admDate;
	/**
	 * 就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 病患姓名
	 */
	private String patName;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	/**
	 * 团体代码、合同代码
	 */
	private String companyCode, contractCode;
	/**
	 * 合同对象
	 */
	private HRMContractD contractD;
	/**
	 * 合同TTextFormat
	 */
	private TTextFormat contract;

	public void onInit() {
		super.onInit();
		contractD = new HRMContractD();
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		/**
		 * REG_CLINICAREA诊区 REG_CLINICROOM诊室 (住院COMBO权限)(门诊权限)(门急住别权限)
		 */
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));

		this.setAdmType("H");
		
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				this.setAdmType("H"); // 2013-3-6	this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setCaseNo(parm.getValue("CASE_NO"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setPatName(parm.getValue("PAT_NAME"));
				this.setAdmDate(parm.getTimestamp("ADM_DATE"));
				if (parm.getValue("POPEDEM").length() != 0) {
					// 一般权限
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// 角色权限
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// 最高权限
					if ("3".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSDBA", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSOPERATOR", false);
					}
				}
			} else {
				this.setAdmType("H"); // 2013-3-6 this.setAdmType("" + obj);
				String date = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyyMMdd")
						+ "000000";
				this
						.setAdmDate(StringTool.getTimestamp(date,
								"yyyyMMddHHmmss"));
			}
		}
		/**
		 * 初始化权限
		 */
		onInitPopeDem();
		/**
		 * 初始化页面
		 */
		initPage();
		/**
		 * 初始化事件
		 */
		initEvent();
	}

	/**
	 * 初始化事件
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		// 排序监听
		addListener(getTTable(TABLE));
	}

	//table中“选中”复选框监听事件执行的方法
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("APPLICATION_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * 初始化权限
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			if ("H".equals(this.getAdmType())) {
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);
			}
		}
		if (this.getPopedem("SYSOPERATOR")) {
			if ("H".equals(this.getAdmType())) {
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(true);
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);
			}
		}
		if (this.getPopedem("SYSDBA")) {
			if ("H".equals(this.getAdmType())) {
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(true);
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);
			}
		}
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", this.getAdmDate());
		this.setValue("END_DATE", sysDate);
		if ("H".equals(this.getAdmType())) {
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth() + 1)
					+ "/" + d.getDate() + " 00:00:00";
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
					+ d.getDate() + " 23:59:59";

			// 初始化时间
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
//			this
//					.getTTable(TABLE)
//					.setHeader(
//							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100");
//			this
//					.getTTable(TABLE)
//					.setParmMap(
//							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO");
		}
		this.onQuery();
	}

	public static Timestamp getTimestamp(String time) {
		Date date = new Date();
		// 注意format的格式要与日期String的格式相匹配
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			date = sdf.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	/**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 拿到TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 返回TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 拿到TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (this.getValueString("MR_NO").trim().length() != 0) {
			// 病案号
			this.setValue("MR_NO", PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO")));
			// 患者姓名
			this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
					PatTool.getInstance().checkMrno(
							this.getValueString("MR_NO"))));
			if ("H".equals("H")) { 
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = ""; 
//			if ("H".equals("H")) {
			sql = getHRMQuerySQL();
//			}
			TParm action = new TParm(this.getDBTool().select(sql));
			this.getTTable(TABLE).setParmValue(action);
			return;
		}
		String sql = ""; 
//		if ("H".equals("H")) { 
		sql = getHRMQuerySQL();
//		}
		TParm action = new TParm(this.getDBTool().select(sql));
		this.getTTable(TABLE).setParmValue(action);
		// 每次查询清空全选
		this.setValue("ALLCHECK", "N");
	}

	/**
	 * 全选
	 */
	public void onSelAll() {
		TParm parm = this.getTTable(TABLE).getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("ALLCHECK").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		this.getTTable(TABLE).setParmValue(parm);
	}

	/**
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * 返回查询病患结果
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            String
	 * @return TParm
	 */
	public TParm getPatInfo(String columnName, String value, String admType) {
		TParm result = new TParm();

		if ("H".equals(admType)) {
			// caseNo  就诊号
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm hParm = new TParm(
						this
								.getDBTool()
								.select(
										"SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE CASE_NO='"
												+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", hParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", hParm.getData("DEPT_CODE", 0));
				TParm hIparm = new TParm(this.getDBTool().select(
						"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
								+ value + "'"));
				result.setData("PAT_NAME", hIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE "
							+ columnName + "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "H");
				Object obj = this.openDialog(
						"%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
			}
		}
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value
						+ "'"));
		result.setData("PAT_NAME", parm.getData("PAT_NAME", 0));
		return result;
	}

	/**
	 * 得到查询参数
	 * 
	 * @return TParm
	 */
	public TParm getParmQuery() {
		TParm result = new TParm();
		result.setData("ADM_TYPE", "H"); // 2013-3-6
		result.setData("START_DATE", StringTool.getString((Timestamp) this
				.getValue("START_DATE"), "yyyyMMddHHmmss"));
		result.setData("END_DATE", StringTool.getString((Timestamp) this
				.getValue("END_DATE"), "yyyyMMddHHmmss"));
		if (getPrintStatus().length() != 0) {
			result.setData("PRINT_FLG", getPrintStatus());
		}
		if (this.getValueString("MR_NO").length() != 0) {
			result.setData("MR_NO", this.getValueString("MR_NO"));
		}
		if (this.getValueString("REGION_CODE").length() != 0) {
			result.setData("REGION_CODE", this.getValueString("REGION_CODE"));
		}
		return result;
	}

	/**
	 * 得到健检的查询语句
	 * 
	 * @return String
	 */
	public String getHRMQuerySQL() {
		String sql = "SELECT 'N' AS FLG, A.PRINT_FLG,  A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, A.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC, A.CAT1_TYPE,A.OPTITEM_CHN_DESC, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, A.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE, C.SEQ_NO AS CSEQ_NO "
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C"
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO "
				+ "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO "
				+ "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
		sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// 门级别
		String srartDate = StringTool.getString((Timestamp) this
				.getValue("START_DATE"), "yyyyMMddHHmmss");// 开始时间
		String endDate = StringTool.getString((Timestamp) this
				.getValue("END_DATE"), "yyyyMMddHHmmss");// 结束时间
		sql += " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate
				+ "','YYYYMMDDHH24MISS') "
				+ "                     AND TO_DATE('" + endDate
				+ "','YYYYMMDDHH24MISS') ";
		if (this.getValueString("REGION_CODE").length() != 0) {// 区域
			sql += " AND A.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
		}
		if (getPrintStatus().length() != 0) {// 打印状态（未打印、已打印、全部）
			sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
		}
		if (this.getValueString("MR_NO").length() != 0) {// 病案号
			sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
		}
		if (this.getValueString("COMPANY_CODE").length() != 0) {// 团体号
			sql += " AND C.COMPANY_CODE = '"
					+ this.getValueString("COMPANY_CODE") + "'";
		}
		if (this.getValueString("CONTRACT_CODE").length() != 0) {// 合同号
			sql += " AND C.CONTRACT_CODE = '"
					+ this.getValueString("CONTRACT_CODE") + "'";
		}
		if (this.getValueString("START_SEQ_NO").length() != 0) {// 员工序号开始
			sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO")
					+ "'";
		}
		if (this.getValueString("END_SEQ_NO").length() != 0) {// 员工序号结束
			sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO")
					+ "'";
		}
		// 2013-3-6 sql +=
		// " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO,A.CAT1_TYPE,A.CASE_NO";
		sql += " AND A.CAT1_TYPE='RIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO,A.CAT1_TYPE,A.CASE_NO";
		return sql;
	}
	/**
	 * 得到打印状态
	 * 
	 * @return String
	 */
	public String getPrintStatus() {
		if (this.getTRadioButton("ALL").isSelected())
			return "";
		if (this.getTRadioButton("ONPRINT").isSelected())
			return "N";
		if (this.getTRadioButton("YESPRINT").isSelected())
			return "Y";
		return "";
	}
	/**
	 * 条码打印
	 */
	public void onPrint() {
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		Set applicationNo = new LinkedHashSet();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			applicationNo.add(temp.getValue("APPLICATION_NO"));
		}
		if (applicationNo.size() > 0) {
			List printSize = new ArrayList();
			Iterator appNo = applicationNo.iterator();
			while (appNo.hasNext()) {
				String appNoStr = "" + appNo.next();
				StringBuffer orderDesc = new StringBuffer();
				String patName = "";
				String deptExCode = "";
				String orderDate = "";
				String stationCode = "";
				String optItemDesc = "";
				String deptCode = "";
				String urgentFlg = "";
				String mrNo = "";
				String sexDesc = "";
				String age = "";
				String devdesc = "";
				String applyNo = "";
				String drNote = "";
				for (int i = 0; i < rowCount; i++) {
					TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
					if (!appNoStr.equals(temp.getValue("APPLICATION_NO")))
						continue;
					patName = temp.getValue("PAT_NAME");
					deptExCode = temp.getValue("EXEC_DEPT_CODE");
					deptCode = temp.getValue("DEPT_CODE");
					stationCode = temp.getValue("STATION_CODE");
					applyNo = temp.getValue("APPLICATION_NO");
					drNote = temp.getValue("DR_NOTE");
					String sql = "SELECT B.BED_NO_DESC FROM MED_APPLY A,SYS_BED B WHERE A.APPLICATION_NO ='"
							+ applyNo + "'" + "AND A.BED_NO=B.BED_NO ";
					TParm selParm = new TParm(TJDODBTool.getInstance().select(
							sql));
					urgentFlg = geturGentFlg(appNoStr).equals("Y") ? "(急)" : "";
					orderDate = String.valueOf(sysDate).substring(0, 19)
							.replaceAll("-", "/");
					optItemDesc = temp.getValue("OPTITEM_CHN_DESC");
					mrNo = temp.getValue("MR_NO");
					sexDesc = this.getDictionary("SYS_SEX", temp
							.getValue("SEX_CODE"));
					age = StringTool.CountAgeByTimestamp(temp
							.getTimestamp("BIRTH_DATE"), sysDate)[0];
					if (appNoStr.equals(temp.getValue("APPLICATION_NO"))) {
						orderDesc.append(temp.getValue("ORDER_DESC"));
					}
				}
				TParm printParm = new TParm();
				printParm.setData("APPLICATION_NO", "TEXT", appNoStr);
				printParm.setData("PAT_NAME", "TEXT", patName);
				printParm.setData("DEPT_CODE", "TEXT", deptCode);
				printParm.setData("STATION_CODE", "TEXT", stationCode);
				printParm.setData("URGENT_FLG", "TEXT", urgentFlg);
				printParm.setData("EXEC_DEPT_CODE", "TEXT", deptExCode);
				printParm.setData("ORDER_DATE", "TEXT", orderDate);
				printParm.setData("OPTITEM_CHN_DESC", "TEXT", optItemDesc);
				printParm.setData("ORDER_DESC", "TEXT", orderDesc.toString());
				printParm.setData("MR_NO", "TEXT", mrNo);
				printParm.setData("SEX_DESC", "TEXT", sexDesc);
				printParm.setData("AGE", "TEXT", age);
				printParm.setData("DR_NOTE", "TEXT", drNote);
				printSize.add(printParm);
			}
			int listRowCount = printSize.size();
			for (int i = 0; i < listRowCount; i++) {
				TParm pR = (TParm) printSize.get(i);
				pR.setData("EXEC_DEPT_CODE", "TEXT", getDeptDesc(pR.getValue(
						"EXEC_DEPT_CODE", "TEXT")));
				pR.setData("DEPT_CODE", "TEXT", getDeptDesc(pR.getValue(
						"DEPT_CODE", "TEXT"))
						+ "("
						+ getStationDesc(pR.getValue("STATION_CODE", "TEXT"))
						+ ")");
				this.openPrintDialog(
						"%ROOT%\\config\\prt\\HRM\\HRMRisPrint.jhw", pR,
						true);

				String[] sqlMedApply = new String[] { "UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE,OPT_DATE=SYSDATE,OPT_USER='"
						+ Operator.getID()
						+ "',OPT_TERM='"
						+ Operator.getIP()
						+ "' WHERE APPLICATION_NO='"
						+ pR.getValue("APPLICATION_NO", "TEXT") + "'" };
				TParm sqlParm = new TParm();
				sqlParm.setData("SQL", sqlMedApply);
				TParm actionParm = TIOM_AppServer.executeAction(actionName,
						"saveHRMRisPrintStat", sqlParm);
				if (actionParm.getErrCode() < 0) {
					this.messageBox("更新" + pR.getValue("PAT_NAME")
							+ "医嘱打印状态失败！");
					return;
				}
			}
		} else {
			this.messageBox("没有需要打印的项目！");
			return;
		}
	}

	/**
	 * 得到检验条码急做标记
	 */
	public String geturGentFlg(String appNoStr) {
		String flg = "";
		if (appNoStr.equals(""))
			return flg;
		String medsql = "SELECT CASE_NO,ORDER_NO,SEQ_NO,ADM_TYPE FROM MED_APPLY "
				+ " WHERE APPLICATION_NO='"
				+ appNoStr
				+ "' AND CAT1_TYPE='LIS'";
		TParm parm = new TParm(this.getDBTool().select(medsql));
		if (parm.getErrCode() < 0)
			return flg;
		if (parm.getCount() <= 0)
			return flg;
		String admType = "";
		String caseNo = "";
		String orderNo = "";
		String seqNo = "";
		if (parm.getCount() > 0) {
			String orderSql = "";
			admType = parm.getValue("ADM_TYPE", 0);
			caseNo = parm.getValue("CASE_NO", 0);
			orderNo = parm.getValue("ORDER_NO", 0);
			seqNo = String.valueOf(parm.getInt("SEQ_NO", 0));
			if (admType.equals("O") || admType.equals("E")) {
				orderSql = " SELECT URGENT_FLG FROM OPD_ORDER WHERE CASE_NO='"
						+ caseNo + "'" + " AND RX_NO='" + orderNo
						+ "' AND SEQ_NO='" + seqNo + "'";
			}
			if (admType.equals("I")) {
				orderSql = " SELECT URGENT_FLG FROM ODI_ORDER WHERE CASE_NO='"
						+ caseNo + "'" + " AND ORDER_NO='" + orderNo
						+ "' AND ORDER_SEQ='" + seqNo + "'";
			}
			if (admType.equals("H")) {
				orderSql = " SELECT URGENT_FLG FROM HRM_ORDER WHERE CASE_NO='"
						+ caseNo + "'" + " AND SEQ_NO='" + seqNo + "'";
			}
			TParm result = new TParm(this.getDBTool().select(orderSql));
			flg = result.getValue("URGENT_FLG", 0);
		}
		return flg;
	}

	/**
	 * 拿到字典信息
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * 拿到科室
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * 拿到科室
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		String date = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd")
				+ "000000";
		Timestamp sysDate = StringTool.getTimestamp(date, "yyyyMMddHHmmss");
		if (this.getPopedem("NORMAL")) {
			this.onInit();
			// clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK");
			clearValue("MR_NO;PAT_NAME;ALLCHECK");
			this.setValue("START_DATE", sysDate);
			this.setValue("END_DATE", SystemTool.getInstance().getDate());
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
		}
		if (this.getPopedem("SYSOPERATOR")) {
			this.onInit();
			// clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED");
			this.setValue("START_DATE", sysDate);
			this.setValue("END_DATE", SystemTool.getInstance().getDate());
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
			
			this.setValue("COMPANY_CODE", "");
			this.setValue("CONTRACT_CODE", "");
			this.setValue("START_SEQ_NO", "");
			this.setValue("END_SEQ_NO", "");
			
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
		}
		if (this.getPopedem("SYSDBA")) {
			// clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED");
			clearValue("ALLCHECK");
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.setValue("START_DATE", sysDate);
			this.setValue("END_DATE", SystemTool.getInstance().getDate());
			this.getTTable(TABLE).removeRowAll();
			
			this.setValue("COMPANY_CODE", "");
			this.setValue("CONTRACT_CODE", "");
			this.setValue("START_SEQ_NO", "");
			this.setValue("END_SEQ_NO", "");
			
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
		}
	}

	public String getAdmType() {
		return admType;
	}
	public String getCaseNo() {
		return caseNo;
	}
	public Timestamp getAdmDate() {
		return admDate;
	}
	public String getMrNo() {
		return mrNo;
	}
	public String getPatName() {
		return patName;
	}
	public void setAdmType(String admType) {
		this.admType = admType;
	}
	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	
	/**
	 * 右键
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table
				.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu|TABLE");
	}
	/**
	 * 细项
	 */
	public void openRigthPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}
	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String orderCode) {
		TParm result = new TParm();
		String sql = "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B  WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
			result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
			result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			// 计算总价格
			double ownPrice = parm.getDouble("OWN_PRICE", i)
					* parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result
					.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
							i));
			result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
			result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
		}
		return result;
	}
	/**
	 * 团体代码选择事件
	 */
	public void onCompanyChoose() {
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			this.setValue("CONTRACT_CODE", "");   //3-6
			return;
		}
		// 根据团体代码查得该团体的合同主项
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm.getErrCode() != 0) {
			this.messageBox_("没有数据");
		}
		// 构造一个TTextFormat,将合同主项赋值给这个控件，取得最后一个合同代码赋值给这个控件初始值
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("查询失败");
			return;
		}
		contract.setValue(contractCode);
	}
	/**
	 * 合同代码选择事件
	 */
	public void onContractChoose() {
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			this.messageBox_(companyCode);
			return;
		}
		contractCode = this.getValueString("CONTRACT_CODE");

	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// 3.根据点击的列,对vector排序
				// 表格排序的列名;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);

	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				return index;
			}
			index++;
		}
		return index;
	}
}
