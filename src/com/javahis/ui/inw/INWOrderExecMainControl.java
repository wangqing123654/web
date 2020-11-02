package com.javahis.ui.inw;
 
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.table.TableModel;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.bil.BIL;
import jdo.hl7.Hl7Communications;
import jdo.ibs.IBSNewTool;
import jdo.ibs.IBSOrderD;
import jdo.ibs.IBSTool;
import jdo.inw.InwForOdiTool;
import jdo.inw.InwOrderExecTool;
import jdo.mem.MEMTool;
import jdo.odi.OdiMainTool;
import jdo.opd.TotQtyTool;
import jdo.pha.TXNewATCTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SysPhaBarTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 护士站执行主窗口
 * </p>
 * table
 * <p>
 * Description: PS：there is interface of quering all station here
 * </p>
 * 
 * <p>
 * 
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * 
 * @version 1.0
 */
public class INWOrderExecMainControl extends TControl {
	private Compare compare = new Compare();

	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	private int orderSeq;

	public int getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(int orderSeq) {
		this.orderSeq = orderSeq;
	}

	private int casenoSeq;
	private String caseNO;
	private int seqNo;
	private int row;

	/**
	 * 床号
	 */
	private String bedNo;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getCaseNO() {
		return caseNO;
	}

	public void setCaseNO(String caseNO) {
		this.caseNO = caseNO;
	}

	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	// 界面上的UI对象
	TComboBox CLP;

	TTextFormat from_Date;
	TTextFormat to_Date;

	TTextField from_Time;
	TTextField to_Time;

	// 查询的时间种类 add by wanglong 20130626
	TRadioButton firstDateRadio;// 默认时间
	TRadioButton secondDateRadio;// 医生开立时间

	/**
	 * 服务等级
	 */
	String serviceLevel;
	// 医嘱类别
	TRadioButton ord1All;
	TRadioButton ord1ST;
	TRadioButton ord1UD;
	TRadioButton ord1DS;
	TRadioButton ord1IG;

	// 医嘱种类
	TRadioButton ord2All;
	TRadioButton ord2PHA;
	TRadioButton ord2PL;
	TRadioButton ord2ENT; // 嘱托
	private IBSOrderD order;
	// 药嘱种类
	TCheckBox typeO;
	TCheckBox typeE;
	TCheckBox typeI;
	TCheckBox typeF;

	// 审核状态
	TRadioButton checkAll;
	TRadioButton checkYES;
	TRadioButton checkNO;

	// 全部执行
	TCheckBox exeAll;
	TCheckBox printAll;

	TTable masterTbl;
	TTable detailTbl;
	TTable replenishTbl;

	TMovePane mp1;
	TMovePane mp2;
	TMovePane mp3;

	// 判断是否点击了补充计价的按钮
	boolean isCharge = false;
	// 判断是否点击了体温单界面
	boolean isVitalSign = false;
	// 某个病人的信息（初始化有外部界面传入）
	String caseNo = "";
	String stationCode = "";
	String mrNo = "";
	String patName = "";
	String ipdNo = "";
	String deptCode = "";
	String ctz1Code = "";
	String ctz2Code = "";
	String ctz3Code = "";
	boolean saveFlg = true;

	// 保存来自住院管理界面的参数，传给补充计价
	TParm outsideParm = new TParm();
	// 打印报表的数据
	TParm forPrtParm = new TParm();
	// 会调用HL7接口的数据
	TParm sendHL7Parm = new TParm();
	// 发送 取消标记
	private int flg;
	// ICU注记
	boolean ICUflg;
	// 调用CISHl7接口
	TParm CISHl7Parm = new TParm();
	private SocketLink client1;

	String clpCode_ = "";

	String orderCodeSki = "";// yanjing 20131107 点击获得医嘱代码
	String orderNoSki = "";// yanjing 20131107 点击获得医嘱
	String orderSeqSki = "";// yanjing 20131107 点击获得医嘱

	String skiResult = "";// yanjing 20131107 存放皮试结果回传的备注信息

	// 紧急医嘱高亮显示（黄色）
	Color yellowColor = new Color(255, 255, 0); // ==================duzhw add
	// 20131101
	private double ownRate; // 自付比例 add caoyong 2014/4/10
	private String clncpathCode;// 临床路径代码 add caoyong 2014/4/10
	private String lumpworkCode;//套餐代码
	private double lumpWorkRate;//套餐折扣
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	
	

	public void onInit() {
		super.onInit();
		myInitControler();
		callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107
		// 皮试结果按钮
		getTable("replenishTable").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");

		addEventListener("replenishTable->" + TTableEvent.CHANGE_VALUE,
				"onTableValueChange");

		// 定义接受返回值方法
		// 从外部调用得到参数(从病案管理界面拿到参数TParm(还有一个作用是传给给IBS))
		outsideParm = (TParm) this.getParameter();
		if (outsideParm != null)
			initParmFromOutside();
		onQuery();
		lockComponet();
		String vsDrCode_ = this.getValueString("VC_CODE");
		parm.setData("VS_DR_CODE", vsDrCode_);
		parm.setData("STATION_CODE", outsideParm
				.getValue("INW", "STATION_CODE"));
		// ---start --服务等级 add caoyong 20131108
		caseNo = outsideParm.getValue("INW", "CASE_NO");// 就诊号
		bedNo = outsideParm.getData("IBS", "BED_NO").toString();
		order = new IBSOrderD(caseNo);
		order.setCaseNo(caseNo);
		TParm admParm = new TParm();
		admParm.setData("CASE_NO", caseNo);
		TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
		serviceLevel = selAdmParm.getValue("SERVICE_LEVEL", 0);
		lumpworkCode= selAdmParm.getValue("LUMPWORK_CODE", 0);//套餐代码
		lumpWorkRate= selAdmParm.getDouble("LUMPWORK_RATE", 0);//套餐折扣
		// ---end --服务等级 add caoyong 20131108
		// newOrderTemp(parm);
		// 排序监听
		addListener(masterTbl);
	}

	public void lockComponet() {
		// ((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
		// ((TTextFormat)getComponent("VC_CODE")).setEnabled(false);
		((TMenuItem) getComponent("save")).setEnabled(saveFlg);
		((TButton) getComponent("butCharge")).setEnabled(saveFlg);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107
		// 皮试结果按钮
		masterTbl.setParmValue(new TParm());
		detailTbl.setParmValue(new TParm());
		// 初始化当前table
		initTable();
		exeAll.setEnabled(true);
		if (checkYES.isSelected()) {
			exeAll.setSelected(true);
		} else if (checkNO.isSelected()) {
			exeAll.setSelected(false);
		}
		onPrtAll();

	}

	public boolean checkPatNum() {
		int rowCount = masterTbl.getRowCount();
		if (rowCount <= 0)
			return true;
		String mrNoThis = masterTbl.getParmValue().getValue("MR_NO", 0);
		for (int i = 0; i < rowCount; i++) {
			if (mrNoThis.equals(masterTbl.getParmValue().getValue("MR_NO", i)))
				continue;
			return true;
		}
		return false;
	}

	/**
	 * 单击上面的table事件
	 * 
	 * @param row
	 *            int 行号
	 */
	public void onMasterTableClicked(int row) {
		// 清空
		detailTbl.setParmValue(new TParm());
		detailTbl.acceptText();
		this.onIsertTable(row);// add caoyong 补充计价
		TParm tableDate = masterTbl.getParmValue();
		String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
		// 通过CASE_NO，ORDER_NO，ORDER_SEQ在ODI_DSPND中定位多条细项
		caseNo = tableDate.getValue("CASE_NO", row);
		orderNo = tableDate.getValue("ORDER_NO", row);
		orderSeq = tableDate.getValue("ORDER_SEQ", row);
		startDttm = tableDate.getValue("START_DTTM", row);
		endDttm = tableDate.getValue("END_DTTM", row);
		// 查询细项的SQL
		String sql = "SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME,"
				+ " A.DC_DATE,A.EXEC_DEPT_CODE,A.NS_EXEC_CODE,A.NS_EXEC_DATE_REAL,A.NS_EXEC_CODE_REAL," +
				  " A.EXEC_NOTE,A.SKIN_RESULT,B.ROUTE_CODE "
				+ " FROM ODI_DSPND A,ODI_ORDER B"
				+ " WHERE A.CASE_NO=B.CASE_NO AND A.ORDER_NO=B.ORDER_NO AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND A.CASE_NO='"
				+ caseNo
				+ "' AND A.ORDER_NO='"
				+ orderNo
				+ "' AND A.ORDER_SEQ='"
				+ orderSeq
				+ "' "
				+ " AND TO_DATE (A.ORDER_DATE||A.ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
				+ startDttm
				+ "','YYYYMMDDHH24MISS') "
				+ " AND TO_DATE (A.ORDER_DATE||A.ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
				+ endDttm
				+ "','YYYYMMDDHH24MISS')"
				+ " ORDER BY A.ORDER_DATE||A.ORDER_DATETIME";
		// 更新细表的TDS,更改其数据
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			result.addData("EXE_FLG", checkYES.isSelected());
			String date = (String) result.getData("ORDER_DATE", i);
			result.setData("ORDER_DATE", i, date.substring(0, 4) + "/"
					+ date.substring(4, 6) + "/" + date.substring(6));
			String time = (String) result.getData("ORDER_DATETIME", i);
			result.setData("ORDER_DATETIME", i, time.substring(0, 2) + ":"
					+ time.substring(2, 4));
			result.setData("EXEC_NOTE", i, result.getData("EXEC_NOTE", i));// yanjing
			result.setData("SKIN_RESULT", i, result.getData("SKIN_RESULT", i));
			result.setData("ROUTE_CODE", i, result.getData("ROUTE_CODE", i));
		}
		detailTbl.setParmValue(result);
		setValue("INW_STATION_CODE", tableDate.getValue("STATION_CODE", row));
		setValue("IPD_NO", tableDate.getValue("IPD_NO", row));
		setValue("MR_NO", tableDate.getValue("MR_NO", row));
		setValue("BED_NO", tableDate.getValue("BED_NO", row));
		setValue("INW_VC_CODE", tableDate.getValue("JZDR", row));
		setValue("PAT_NAME", tableDate.getValue("PAT_NAME", row));
		setValue("INW_DEPT_CODE", tableDate.getValue("DEPT_CODE", row));
		setValue("ADM_DATE", tableDate.getValue("IN_DATE", row) == null ? ""
				: tableDate.getValue("IN_DATE", row).substring(0, 10)
						.replaceAll("-", "/"));
		setValue("PAY_INS", tableDate.getValue("PAY_INS", row));
		setValue("TOTAL_AMT", tableDate.getValue("TOTAL_AMT", row));
		setValue("YJYE_PRICE", tableDate.getValue("CUR_AMT", row));
		setValue("GREED_PRICE", tableDate.getValue("GREENPATH_VALUE", row));
		setValue("YJJ_PRICE", tableDate.getValue("TOTAL_BILPAY", row));
	}

	public void clearTop() {
		if (getValueString("IPD_NO").length() != 0)
			return;
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("BED_NO", "");
	}

	private void filterDrugByDoseCode(TParm parm) {
		if (!ord2PHA.isSelected())
			return;
		if (parm.getCount() <= 0)
			return;
		for (int i = parm.getCount() - 1; i >= 0; i--) {
			if (!parm.getValue("ORDER_CAT1_CODE", i).startsWith("PHA"))
				continue;
			if (!typeO.isSelected()
					&& "O".equals(parm.getValue("CLASSIFY_TYPE", i)))
				parm.removeRow(i);
			if (!typeE.isSelected()
					&& "E".equals(parm.getValue("CLASSIFY_TYPE", i)))
				parm.removeRow(i);
			if (!typeI.isSelected()
					&& "I".equals(parm.getValue("CLASSIFY_TYPE", i)))
				parm.removeRow(i);
			if (!typeF.isSelected()
					&& "F".equals(parm.getValue("CLASSIFY_TYPE", i)))
				parm.removeRow(i);
		}
		parm.setCount(parm.getCount("ORDER_CAT1_CODE"));
	}

	/**
	 * 初始化table 查询条件是： caseNo/病区
	 */
	public void initTable() {
		String fromDate = StringTool.getString(
				(Timestamp) from_Date.getValue(), "yyyyMMdd");
		String fromTime = (String) from_Time.getValue();
		String fromCheckDate = fromDate + fromTime.substring(0, 2)
				+ fromTime.substring(3);
		String toDate = StringTool.getString((Timestamp) to_Date.getValue(),
				"yyyyMMdd");
		String toTime = (String) to_Time.getValue();
		String toCheckDate = toDate + toTime.substring(0, 2)
				+ toTime.substring(3);
		if (fromCheckDate.compareTo(toCheckDate) > 0) {
			messageBox("执行日期不合法");
			return;
		}
		TParm selParm = new TParm();
		selParm = getQueryParm();
		TParm query = new TParm();
		if (firstDateRadio.isSelected() || checkNO.isSelected()) {// add by
			// wanglong
			// 20130626
			query = InwForOdiTool.getInstance().selectOdiDspnm(selParm);
		} else {
			query = InwForOdiTool.getInstance().selectOdiDspnD(selParm);
		}
		filterDrugByDoseCode(query);
		if (query.getCount() <= 0) {
			masterTbl.setParmValue(query);
			// this.messageBox("没有相关数据！");
			return;
		}
		for (int i = 0; i < query.getCount(); i++) {
			masterTbl.removeRowColor(i);// duzhw update 20131126 初始化取消表格颜色
			query.addData("EXE_FLG", checkYES.isSelected());
			query.addData("PRT", false);
			// 护士执行医嘱时间
			Timestamp exeDate = (Timestamp) query.getData("NS_EXEC_DATE", i);
			// 护士站执行DC时间
			Timestamp exeDcDate = (Timestamp) query.getData("NS_EXEC_DC_DATE",
					i);
			// 如果查询已审核
			if (exeDate != null) {
				String day = StringTool.getString(exeDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDate, "HH:mm:ss")
						.substring(0, 5);
				query.addData("NS_EXEC_DATE", exeDate);
				query.addData("NS_EXEC_DATE_TIME", time);
			} else {
				query.addData("NS_EXEC_DATE", null);
				query.addData("NS_EXEC_DATE_TIME", null);
			}
			if (exeDcDate != null) {
				String day = StringTool.getString(exeDcDate, "yyyy/MM/dd");
				String time = StringTool.getString(exeDcDate, "HH:mm:ss");
				query.addData("NS_EXEC_DC_DATE_DAY", day);
				query.addData("NS_EXEC_DC_DATE_TIME", time);
			} else {
				query.addData("NS_EXEC_DC_DATE_DAY", null);
				query.addData("NS_EXEC_DC_DATE_TIME", null);
			}
		}
		masterTbl.setParmValue(query);
		existsDateForTabl(masterTbl);
		if (saveFlg) {
			if (checkPatNum()) {
				String SQL = "";
				String deptSQL = "";
				if (getValueString("INW_DEPT_CODE").length() != 0)
					deptSQL = " AND DEPT_CODE = '"
							+ getValueString("INW_DEPT_CODE") + "'";
				String stationSQL = "";
				if (getValueString("INW_STATION_CODE").length() != 0)
					stationSQL = " AND STATION_CODE = '"
							+ getValueString("INW_STATION_CODE") + "'";
				// String drSQL = "";
				// if (getValueString("VC_CODE").length() != 0)
				// drSQL = " AND VS_DR_CODE = '" + getValueString("VC_CODE")
				// +"'";
				if (getValueString("MR_NO").length() != 0
						&& getValueString("IPD_NO").length() != 0)
					SQL = " SELECT * " + " FROM ADM_INP " + " WHERE MR_NO = '"
							+ getValueString("MR_NO") + "'"
							+ " AND   IPD_NO = '" + getValueString("IPD_NO")
							+ "'" + " AND CANCLE_FLG  != 'Y'"
							+ " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
				// drSQL;
				else if (getValueString("MR_NO").length() != 0)
					SQL = " SELECT * " + " FROM ADM_INP " + " WHERE MR_NO = '"
							+ getValueString("MR_NO") + "'"
							+ " AND CANCLE_FLG  != 'Y'"
							+ " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
				// drSQL;
				else if (getValueString("IPD_NO").length() != 0)
					SQL = " SELECT * " + " FROM ADM_INP " + " WHERE IPD_NO = '"
							+ getValueString("IPD_NO") + "'"
							+ " AND CANCLE_FLG  != 'Y'"
							+ " AND DS_DATE IS NULL" + deptSQL + stationSQL;// +
				// drSQL;
				if (SQL.length() != 0) {
					TParm result = new TParm(TJDODBTool.getInstance().select(
							SQL));
					if (result.getCount() > 0) {
						this.setCaseNo(result.getValue("CASE_NO", 0));
						this.setStationCode(result.getValue("STATION_CODE", 0));
						this.setMrNo(result.getValue("MR_NO", 0));
						TParm patTParm = new TParm(TJDODBTool.getInstance()
								.select(
										"SELECT * FROM SYS_PATINFO WHERE MR_NO = '"
												+ result.getValue("MR_NO", 0)
												+ "'"));
						this.setPatName(patTParm.getValue("PAT_NAME", 0));
						this.setIpdNo(result.getValue("IPD_NO", 0));
						this.setDeptCode(result.getValue("DEPT_CODE", 0));
						ctz1Code = result.getValue("CTZ1_CODE", 0);
						ctz2Code = result.getValue("CTZ2_CODE", 0);
						ctz3Code = result.getValue("CTZ3_CODE", 0);
						((TMenuItem) getComponent("print")).setEnabled(true);
						((TMenuItem) getComponent("paster")).setEnabled(true);
					} else {
						this.setCaseNo("");
						// ((TMenuItem)
						// getComponent("print")).setEnabled(false);
						// ((TMenuItem)
						// getComponent("paster")).setEnabled(false);
					}
				} else {
					this.setCaseNo("");
					// ((TMenuItem) getComponent("print")).setEnabled(false);
					// ((TMenuItem) getComponent("paster")).setEnabled(false);
				}
			} else {
				TParm parmMastrt = masterTbl.getParmValue();
				this.setCaseNo(parmMastrt.getValue("CASE_NO", 0));
				this.setStationCode(parmMastrt.getValue("STATION_CODE", 0));
				this.setMrNo(parmMastrt.getValue("MR_NO", 0));
				this.setPatName(parmMastrt.getValue("PAT_NAME", 0));
				this.setIpdNo(parmMastrt.getValue("IPD_NO", 0));
				this.setDeptCode(parmMastrt.getValue("DEPT_CODE", 0));
				TParm admParm = new TParm();
				admParm.setData("CASE_NO", parmMastrt.getValue("CASE_NO", 0));
				admParm = ADMTool.getInstance().getADM_INFO(admParm);
				ctz1Code = admParm.getValue("CTZ1_CODE", 0);
				ctz2Code = admParm.getValue("CTZ2_CODE", 0);
				ctz3Code = admParm.getValue("CTZ3_CODE", 0);
				((TMenuItem) getComponent("print")).setEnabled(true);
				((TMenuItem) getComponent("pasterBottle")).setEnabled(true);
				((TMenuItem) getComponent("Newprint")).setEnabled(true);
			}
		}
		setColor(); // modify by chenxi 20120703
		return;
	}

	// /**
	// * 过滤掉错误的包床床号
	// * @param query
	// */
	// private void filterBedNo(TParm query) {
	// for (int i = 0; i < query.getCount("MR_NO"); i++) {
	// String bedNo =
	// InwForOdiTool.getInstance().selectBedNoByMrNo(query.getValue("MR_NO",
	// i));
	// query.setData("BED_NO", i, bedNo);
	// }
	// }

	/**
	 * 获得界面上的所有查询参数
	 * 
	 * @return TParm
	 */
	public TParm getQueryParm() {
		// 获得界面上的参数
		TParm result = new TParm();
		// ===============pangben modify 20110512 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			result.setData("REGION_CODE", Operator.getRegion());
		// ===============pangben modify 20110512 stop

		// 取得医嘱类别
		if (ord1All.isSelected()) {
			// 所有
		} else if (ord1ST.isSelected()) {
			// 临时(包括ST/F)
			result.setData("DSPN_KINDSTF", "Y");
		} else if (ord1UD.isSelected()) {
			// 长期
			result.setData("DSPN_KINDUD", "Y");
		} else if (ord1DS.isSelected()) {
			// 出院带药
			result.setData("DSPN_KINDDS", "Y");
		} else if (ord1IG.isSelected()) {
			// 住院草药
			result.setData("DSPN_KINDIG", "Y");
		}

		// 医嘱种类
		if (ord2All.isSelected()) {
			// 所有
		} else if (ord2PHA.isSelected()) {
			// 药嘱
			result.setData("CAT1_TYPEPHA", "Y");
		} else if (ord2PL.isSelected()) {
			// 处置(检验检查)
			result.setData("CAT1_TYPEPL", "Y");
		} else if (ord2ENT.isSelected()) {
			// 嘱托
			result.setData("CAT1_TYPEENT", "Y");
		}

		// 医嘱种类
		/*
		 * if (typeO.isSelected()) { //口服 result.addData("DOSE_TYPEO", "Y"); }
		 * if (typeE.isSelected()) { //外用 result.addData("DOSE_TYPEE", "Y"); }
		 * if (typeI.isSelected()) { //针剂 result.addData("DOSE_TYPEI", "Y"); }
		 * if (typeF.isSelected()) { //点滴 result.addData("DOSE_TYPEF", "Y"); }
		 */

		// 执行状态
		if (checkAll.isSelected()) {
			// 所有
			if (firstDateRadio.isSelected()) {
				result.setData("EXECTYPE_ALL", "Y");
				String fromDate = StringTool.getString((Timestamp) from_Date
						.getValue(), "yyyyMMdd");
				String fromTime = (String) from_Time.getValue();
				String fromCheckDate = fromDate + fromTime.substring(0, 2)
						+ fromTime.substring(3);
				String toDate = StringTool.getString((Timestamp) to_Date
						.getValue(), "yyyyMMdd");
				String toTime = (String) to_Time.getValue();
				String toCheckDate = toDate + toTime.substring(0, 2)
						+ toTime.substring(3);
				result.setData("fromCheckDate", fromCheckDate);
				result.setData("toCheckDate", toCheckDate);
			} else {// add by wanglong 20130626
				result.setData("EXECTYPE_ALL_ORDERDATETIME", "Y");
				result.setData("fromDateTime", (Timestamp) this
						.getValue("ORDER_START_DATETIME"));
				result.setData("toDateTime", (Timestamp) this
						.getValue("ORDER_END_DATETIME"));
			}
		} else if (checkYES.isSelected()) {
			if (firstDateRadio.isSelected()) {
				// 已执行
				result.setData("EXECTYPE_YES", "Y");
				String fromDate = StringTool.getString((Timestamp) from_Date
						.getValue(), "yyyyMMdd");
				String fromTime = (String) from_Time.getValue();
				String fromCheckDate = fromDate + fromTime.substring(0, 2)
						+ fromTime.substring(3);
				String toDate = StringTool.getString((Timestamp) to_Date
						.getValue(), "yyyyMMdd");
				String toTime = (String) to_Time.getValue();
				String toCheckDate = toDate + toTime.substring(0, 2)
						+ toTime.substring(3);
				result.setData("fromCheckDate", fromCheckDate);
				result.setData("toCheckDate", toCheckDate);
				result.setData("NS_EXEC_DATE", fromCheckDate);
			} else {// add by wanglong 20130626
				result.setData("EXECTYPE_YES", "Y");
				result.setData("fromDateTime", (Timestamp) this
						.getValue("ORDER_START_DATETIME"));
				result.setData("toDateTime", (Timestamp) this
						.getValue("ORDER_END_DATETIME"));
				result.setData("ORDER_DATETIME", "Y");
			}
		} else if (checkNO.isSelected()) {
			// 未审核
			result.setData("EXECTYPE_NO", "Y");
		}
		if (setQueryParm(result)) {
			clearTop();
			return result;
		}
		// 加如看诊号
		if (caseNo != null && !"".equals(caseNo.trim())
				&& !"null".equals(caseNo)) {
			result.setData("CASE_NO", caseNo);
		} else {
			// 为空的时候
		}
		// 加入病区号
		if (!this.getValueString("INW_STATION_CODE").equals("")) {
			result.setData("STATION_CODE", getValueString("INW_STATION_CODE"));
		} else {
			// 为空的时候
		}
		// 加入科室
		if (!this.getValueString("INW_DEPT_CODE").equals("")) {
			result.setData("DEPT_CODE", getValueString("INW_DEPT_CODE"));
		} else {
			// 为空的时候
		}
		// 加入经治医师
		if (!this.getValueString("INW_VC_CODE").equals("")) {
			result.setData("VS_DR_CODE", getValueString("INW_VC_CODE"));
		} else {
			// 为空的时候
		}
		
		return result;
	}

	public boolean setQueryParm(TParm parm) {
		if (!saveFlg)
			return false;
		if (getValueString("INW_STATION_CODE").length() != 0)
			parm.setData("STATION_CODE", getValue("INW_STATION_CODE"));
		if (getValueString("IPD_NO").length() != 0)
			parm.setData("IPD_NO", getValue("IPD_NO"));
		if (getValueString("MR_NO").length() != 0)
			parm.setData("MR_NO", getValue("MR_NO"));
		// if(getValueString("VC_CODE").length() != 0)
		// parm.setData("VC_CODE",getValue("VC_CODE"));
		if (getValueString("INW_DEPT_CODE").length() != 0)
			parm.setData("DEPT_CODE", getValue("INW_DEPT_CODE"));
		parm.setData("DS_DATE_FLG", false);
		return true;
	}

	// 全部执行
	public void onCheck() {

		boolean nowFlag = exeAll.isSelected();
		// 当全部执行的时候设置一次时间
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		String optName = Operator.getName();

		// 得到行数
		int ordCount = masterTbl.getRowCount();
		for (int i = 0; i < ordCount; i++) {
			// 循环取消对勾
			if (nowFlag) {
				selection(i, optName, chackTime);
			} else { // 取消审核
				unselection(i);
			}
		}

	}

	/**
	 * 保存动作
	 */
	public boolean onSave() {
		// 保存完之后清空选勾
		exeAll.setSelected(false);
		printAll.setSelected(false);

		// 立刻接受值的改变
		masterTbl.acceptText();
		detailTbl.acceptText();
		
		//add by yangjj 20151211 取消执行权限判断
		if(!checkNO.isSelected()){
			String sql1 = " SELECT " +
								" ROLE_CODE,GROUP_CODE,CODE " +
						  " FROM " +
						  		" SYS_ROLE_POPEDOM " +
						  " WHERE " +
						  		" GROUP_CODE = 'INW02' AND CODE = 'inwCancelExec' AND ROLE_CODE='"+Operator.getRole()+"'";
			String sql2 = " SELECT " +
								" USER_ID,AUTH_CODE,GROUP_CODE " +
						  " FROM " +
						  		" SYS_USER_AUTH " +
						  " WHERE " +
						  		" USER_ID = '"+Operator.getID()+"' AND AUTH_CODE = 'inwCancelExec' AND GROUP_CODE = 'INW02'";
			
			TParm r1 = new TParm(TJDODBTool.getInstance().select(sql1));
			TParm r2 = new TParm(TJDODBTool.getInstance().select(sql2));
			
			if((r1.getCount() < 1) && (r2.getCount() < 1)){
				this.messageBox("您没有取消执行医嘱的权限！");
				return false;
			}
		}

		
		
		boolean existOption = false;
		if (checkAll.isSelected()) {
			this.messageBox("全部状态下\n不可保存！");
			return false;
		}
		// 检查是否有选中的数据
		for (int i = 0; i < masterTbl.getRowCount(); i++) {
			boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 3)); //列位置调整13改3
			// modify
			// 12改为13
			// 20131122
			if ((checkNO.isSelected() && selFlg)
					|| (checkYES.isSelected() && !selFlg)) {
				existOption = true;
				break;
			}
		}

		// 如果没有存在选择的数据
		if (!existOption) {
			this.messageBox("没选中保存数据！");
			return false;
		}

		// 检验皮试药品是否做皮试 yanjing 20140514
		boolean psResult = false;// 是否存在未做皮试的药品
		for (int i = 0; i < masterTbl.getRowCount(); i++) {
			boolean selFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 3)); //列位置调整13改3
			// 校验是否是皮试用品
			TParm tablValue = masterTbl.getParmValue();
			String orderCode = (String) tablValue.getData("ORDER_CODE", i);
			String selectSql = "SELECT BATCH_NO,SKINTEST_NOTE FROM PHA_ANTI WHERE CASE_NO= '"
					+ caseNo
					+ "' AND ORDER_CODE='"
					+ orderCode
					+ "' "
					+ " ORDER BY ORDER_DATE DESC";
			TParm selectparm = new TParm(TJDODBTool.getInstance().select(
					selectSql));
			if (selectparm.getCount() > 0) {
				if (checkNO.isSelected()
						&& selFlg
//						&& selectparm.getData("BATCH_NO", 0).toString().equals(
//								"")
						&& selectparm.getData("SKINTEST_NOTE", 0).toString()
								.equals("")) {// 没有做皮试（有批号未皮试结果时也要给出提示，可能是药房传过来的批号，护士站并未做过皮试）
					psResult = true;
				}
			}
		}

		// 如果有皮试的药品未填写皮试结果
		if (psResult) {
			if (this.messageBox("提示", "存在未做皮试的药嘱,是否继续保存?", this.YES_NO_OPTION) != 0) {// 否，不保存
				return false;
			}
		}

		// 密码判断
		if (!checkPW()) {
			return false;
		}

		// 调用保存
		if (checkNO.isSelected()) {
			if (!onExec()) {
				this.messageBox("E0001");
				onQuery();
				return false;
			}
		} // 如果审核被选择（说明保存时是--取消审核），需要验证是否有执行的
		else {
			if (!onUndoExec()) {
				this.messageBox("E0001");
				onQuery();
				return false;
			}

		}

		this.messageBox("P0001");
		// 保存后再执行一边查询
		onQuery();
		// 保存成功后校验是否该停止划价
		checkStopFee();
		// 发送HL7信息
		if (sendHL7Parm.getCount("CASE_NO") > 0) {
			sendHL7Mes();
		}
		// 发送ICU消息
		// if (this.ICUflg && this.CISHl7Parm.getCount("CASE_NO") > 0) {
		// sendICUHl7Mes();
		// }
		return true;
	}

	private boolean CheckData() {
		boolean falg = false;

		return falg;
	}

	/**
	 * 设置特殊药品颜色
	 */
	// ================= add by chenxi 20120703
	private void setColor() {
		TParm tableParm = masterTbl.getParmValue();
		Color WhiteColor = new Color(255, 255, 255);
		Color normalColor = new Color(0, 0, 0);
		Color blueColor = new Color(0, 0, 255);
		Color antibioticColor = new Color(255, 0, 0);
		for (int i = 0; i < tableParm.getCount(); i++) {
			// =========药品提示信息 modify by chenxi
			String orderCode = tableParm.getValue("ORDER_CODE", i);
			// 紧急医嘱高亮显示 duzhw add 20131101 start-----------------
			TParm action = tableParm.getRow(i);
			String sql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
					+ orderCode + "'";
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
			sqlparm = sqlparm.getRow(0);
			if (tableParm.getValue("ANTIBIOTIC_CODE", i).length() > 0) {
				masterTbl.setRowTextColor(i, antibioticColor);
				//fux modify 20150820
			}  else if (this.getColorByHighRiskOrderCode(orderCode)) {
				//messageBox("高危药品，显示为红色");
				masterTbl.setRowTextColor(i, Color.RED);
			}//高危药品，显示为红色
			   else  {
				masterTbl.setRowTextColor(i, normalColor);
			}
			if ("Y".equals(action.getValue("URGENT_FLG"))) {
				masterTbl.setRowColor(i, yellowColor);
			} else {
				masterTbl.setRowColor(i, WhiteColor);
			}  
			// 紧急医嘱高亮显示 duzhw add 20131101 end-----------------
//			if (sqlparm.getValue("DRUG_NOTES_DR").length() > 0) {
//				masterTbl.setRowTextColor(i, blueColor);
//			} else {
//				masterTbl.setRowTextColor(i, normalColor);
//			}
		}
	}

	/**
	 * 判断是否为高危药品
	 * @param orderCode
	 * @return
	 * 20150731 wangjc add
	 */
	public boolean getColorByHighRiskOrderCode(String orderCode){
		boolean a=false;
		String sql = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='"
				+orderCode
				+ "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getValue("HIGH_RISK_FLG", 0).equals("Y")){
			a=true;
		}
		return a;
	}
	
	/**
	 * 单击某条医嘱在状态栏上显示信息
	 */
	public void onClick() {
		int row = masterTbl.getSelectedRow();
		TParm parm = masterTbl.getParmValue();
		// System.out.println("++++===+++++parm parm is ::"+parm);
		// TParm action1 = mainTable.getDataStore().getRowParm(
		// mainTable.getSelectedRow());
		// =================== modify by chenxi 20120703 start
		orderCodeSki = parm.getValue("ORDER_CODE", row);// yanjing 20131107
		// 医嘱代码（皮试）
		orderNoSki = parm.getValue("ORDER_NO", row);// yanjing 20131107 医嘱代码（皮试）
		orderSeqSki = parm.getValue("ORDER_SEQ", row);// yanjing 20131107
		// 医嘱代码（皮试）
		String orderCode = parm.getValue("ORDER_CODE", row);
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// System.out.println("==========="+sql);
		// 状态栏显示医嘱提示
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR"));
		// ================= add by chenxi 20120703

	}

	/**
	 * 发送HL7消息
	 * 
	 * @param admType
	 *            String 门急住别
	 * @param catType
	 *            医令分类
	 * @param patName
	 *            病患姓名
	 * @param caseNo
	 *            String 就诊号
	 * @param applictionNo
	 *            String 条码号
	 * @param flg
	 *            String 状态(0,发送1,取消)
	 */
	private void sendHL7Mes() {
		int count = ((Vector) sendHL7Parm.getData("CASE_NO")).size();
		if (count <= 0) {
			return;
		}
		List list = new ArrayList();
		for (int i = 0; i < count; i++) {
			String sql = " SELECT * FROM ODI_ORDER WHERE CASE_NO ='"
					+ sendHL7Parm.getValue("CASE_NO", i) + "' AND ORDER_NO='"
					+ sendHL7Parm.getValue("ORDER_NO", i) + "' AND ORDER_SEQ="
					+ sendHL7Parm.getInt("ORDER_SEQ", i) + "";

			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", "I");
			parm.setData("FLG", flg);
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
			parm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
			parm.setData("SEQ_NO", result.getInt("ORDER_SEQ", 0));
			list.add(parm);
		}
		// 清空parm
		while (sendHL7Parm.getCount("CASE_NO") > 0) {
			sendHL7Parm.removeRow(0);
		}
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
	}

	// /**
	// * CIS发送HL7消息
	// */
	// private void sendICUHl7Mes() {
	// // 调用CISHl7接口
	// List list = new ArrayList();
	// list.add(CISHl7Parm);
	// TParm CISparm = Hl7Communications.getInstance().Hl7MessageCIS(list,
	// "NBW");
	// if (CISparm.getErrCode() < 0) {
	// this.messageBox(CISparm.getErrText());
	// } else {
	// this.messageBox("发送CIS消息成功！");
	// }
	// // 清空parm
	// while (CISHl7Parm.getCount("CASE_NO") > 0) {
	// CISHl7Parm.removeRow(0);
	// }
	// }

	/**
	 * 取消执行主方法
	 * 
	 * @return boolean
	 */
	public boolean onUndoExec() {
		// 拿到所有挑勾--展开人的caseNo
		TParm execData = new TParm();
		TParm tablValue = masterTbl.getParmValue();
		// TParm detailTblValue = detailTbl.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = masterTbl.getRowCount();
		boolean sendFlg = true;
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) tablValue.getData("CASE_NO", i);
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			String cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
			String orderDateD = tablValue.getData("ORDER_DATE", i).toString();
			// String orderDateTime = (String) tablValue.getData(
			// "ORDER_DATETIME", i);
			if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 3))) { //列位置调整13改3
				// yanjing 20140514 取消审核时清空PHA_ANTI表中相应的皮试结果的值 start
				String orderCode = (String) tablValue.getData("ORDER_CODE", i);
				String psSql = "SELECT ORDER_DATE FROM PHA_ANTI WHERE CASE_NO = '"
						+ caseNo
						+ "' AND ORDER_CODE = '"
						+ orderCode
						+ "' AND ROUTE_CODE = 'PS'  "
						+ "ORDER BY ORDER_DATE DESC";
				TParm selectparm = new TParm(TJDODBTool.getInstance().select(
						psSql));
				if (selectparm.getCount() > 0) {// 清空皮试注记
					String optDate = selectparm.getValue("ORDER_DATE", 0)
							.substring(0, 19).replace("-", "").replace("/", "")
							.replace(" ", "").replace(":", "");
					String updatePhaAnti = " UPDATE PHA_ANTI SET BATCH_NO = '',SKINTEST_NOTE= '' "
							+ "WHERE CASE_NO= '"
							+ caseNo
							+ "' AND ORDER_CODE='"
							+ orderCode
							+ "'AND OPT_DATE = TO_DATE('"
							+ optDate
							+ "','YYYYMMDDHH24MISS') ";
					TParm parm = new TParm(TJDODBTool.getInstance().update(
							updatePhaAnti));

				}
				execData.addData("CASE_NO", caseNo);
				execData.addData("ORDER_NO", orderNo);
				execData.addData("ORDER_SEQ", orderSeq);
				execData.addData("DC_DATE", tablValue.getData("DC_DATE", i));// 根据DC_DATE后台判断是取消执行/取消执行DC
				execData.addData("NS_EXEC_CODE", tablValue.getData(
						"NS_EXEC_CODE", i));
				execData.addData("NS_EXEC_DATE", tablValue.getData(
						"NS_EXEC_DATE", i));
				execData.addData("NS_EXEC_DC_CODE", tablValue.getData(
						"NS_EXEC_DC_CODE", i));
				execData.addData("NS_EXEC_DC_DATE", tablValue.getData(
						"NS_EXEC_DC_DATE", i));
				execData.addData("OPT_USER", Operator.getID());
				// =============yanjing 20140515 清空护士备注 start
				execData.addData("EXEC_NOTE", "");
				
				
				execData.addData("ORDER_DATE", orderDateD.substring(0, 4)
						+ orderDateD.substring(5, 7)
						+ orderDateD.substring(8, 10));
				execData.addData("ORDER_DATETIME", orderDateD.substring(11, 13)
						+ orderDateD.substring(15));
				// ==============yanjing 20140515 清空护士备注 end
				execData.addData("OPT_DATE", now);
				execData.addData("OPT_TERM", Operator.getIP());
				execData.addData("CAT1_TYPE", tablValue.getData("CAT1_TYPE", i));
				// 处理集合医嘱
				String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
				String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
						i)
						+ "";
				execData.addData("SETMAIN_FLG", setMainFlg);
				execData.addData("ORDERSET_GROUP_NO", orderSetGroupNo);

				Timestamp falseParm = new Timestamp(0);
				execData.addData("START_DTTM", tablValue.getData("START_DTTM",
						i));
				execData.addData("END_DTTM", tablValue.getData("END_DTTM", i));
				// ======add by wanglong 20130527
				execData.addData("MR_NO", tablValue.getValue("MR_NO", i));
				execData.addData("ORDER_CODE", tablValue.getValue("ORDER_CODE",
						i));
				execData.addData("ORDERSET_CODE", tablValue.getValue(
						"ORDER_CODE", i));
				execData.addData("ORDER_DESC", tablValue.getValue(
						"ORDER_DESC_AND_SPECIFICATION", i));
				execData.addData("DOSAGE_QTY", tablValue.getValue("DOSAGE_QTY",
						i));
				// ======add end
				// 如果CAT1_TYPE为LIS或者RIS的将会发送给HL7接口
				if ("LIS".equals(cat1Type) || "RIS".equals(cat1Type)) {
					if (Hl7Communications.getInstance().IsExeOrder(
							tablValue.getRow(i), "I")) {
						String orderDate = tablValue.getValue("ORDER_DATE", i)
								.substring(0, 19).replaceAll("-", "/");
						this.messageBox(tablValue.getValue("ORDER_DESC", i)
								+ "已经执行，不能取消！\n 下达时间:" + orderDate);
						sendFlg = false;
						break;
					}
					sendHL7Parm.addData("CASE_NO", caseNo);
					sendHL7Parm.addData("ORDER_NO", orderNo);
					sendHL7Parm
							.addData("ORDER_SEQ", Integer.parseInt(orderSeq));
					flg = 1;
				}
			}
		}
		if (!sendFlg) {
			return false;
		}
		execData = checkDCQtyIsLess(execData);// add by wanglong 20130527
		if (execData == null) {// add by wanglong 20130527
			return false;
		}
		// // 20120228--------------------------------
		// execData.setData("ADM_TYPE", "I");
		// // System.out.println("-=-=-------------------------"+execData);
		// execData = SysPhaBarTool.getInstance().getaddBarParm(execData,
		// "INW");
		// 拿到数量
		int count = ((Vector) execData.getData("OPT_USER")).size();
		execData.setCount(count);
		// 调用action执行事务
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.InwOrderExecAction", "onUndoSave", execData);
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		}

		return true;

	}

	/**
	 * 执行主方法
	 * 
	 * @return boolean
	 */
	public boolean onExec() {
		// 拿到所有挑勾--展开人的caseNo
		TParm inParm = new TParm();// 入参
		TParm execData = new TParm();
		TParm tablValue = masterTbl.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = masterTbl.getRowCount();
		// 主表的数据
		for (int i = 0; i < rowCount; i++) {
			String caseNo = (String) tablValue.getData("CASE_NO", i);
			String orderNo = (String) tablValue.getData("ORDER_NO", i);
			String orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			String cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
			String orderCode = (String) tablValue.getData("ORDER_CODE", i);
			String order_dr = (String) tablValue.getData("ORDER_DR_CODE", i);
			// 判断是否写执行/DC的人员
			String dcFlg = (String) tablValue.getData("DC_DR_CODE", i);
			if (TypeTool.getBoolean(masterTbl.getValueAt(i, 3))) { //列位置调整13改3
				execData.addData("CASE_NO", caseNo);
				execData.addData("ORDER_NO", orderNo);
				execData.addData("ORDER_SEQ", orderSeq);
				execData.addData("ORDER_CODE", orderCode);
				// 该医嘱为DC医嘱
				if (dcFlg != null) {
					execData.addData("DC_ORDER", true);
					// =============add by wanglong 20130619
					if (!tablValue.getRow(i).getValue("CAT1_TYPE")
							.equals("PHA")) {
						TParm dCQty = InwOrderExecTool.getInstance()
								.getDCOrder(tablValue.getRow(i));
						int dcQty = dCQty.getInt("DC_QYT", 0);
						String sumCountSql = "SELECT SUM(DOSAGE_QTY) COUNT FROM IBS_ORDD WHERE CASE_NO = '#' AND ORDER_CODE = '#' GROUP BY CASE_NO, ORDER_CODE";
						String sql = sumCountSql.replaceFirst("#", caseNo)
								.replaceFirst("#", orderCode);
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getErrCode() != 0) {
							this.messageBox("查询待DC医嘱被病患使用过的总数量失败");
							return false;
						}
						if (result.getInt("COUNT", 0) < dcQty) {
							this.messageBox(tablValue.getRow(i).getValue(
									"ORDER_DESC")
									+ "（"
									+ orderCode
									+ "）取消执行的数量大于病患（"
									+ tablValue.getRow(i).getValue("PAT_NAME")
									+ "）执行过的总数量，当前操作将被取消");
							return false;
						}
					}
					// =============add end
				} else {
					execData.addData("DC_ORDER", false);
				}
				execData.addData("OPT_USER", Operator.getID());
				execData.addData("OPT_DATE", now);
				execData.addData("OPT_TERM", Operator.getIP());
				// 医嘱类别：后台区分是否送给IBS计费
				execData.addData("CAT1_TYPE", cat1Type);

				String execDate = "" + masterTbl.getValueAt(i, 19); // duzhw
				// modify
				// 18改为19
				// 20131122
				String execTime = ("" + masterTbl.getValueAt(i, 20)).replace( // duzhw
						// modify
						// 19改为20
						// 20131122
						":", "");
				// String execDate = "" + masterTbl.getValueAt(i, 18);
				Timestamp checkDateTime = StringTool.getTimestamp(execDate
						.substring(0, 10).replaceAll("/", "").replaceAll("-",
								"")
						+ " " + execTime.substring(0, 4) + "00",
						"yyyyMMdd HHmmss");

				// 允许人工修改
				execData.addData("NS_EXEC_DATE", checkDateTime);
				// 处理集合医嘱
				String setMainFlg = tablValue.getData("SETMAIN_FLG", i) + "";
				String orderSetGroupNo = tablValue.getData("ORDERSET_GROUP_NO",
						i)
						+ "";
				execData.addData("START_DTTM", tablValue.getData("START_DTTM",
						i));
				execData.addData("END_DTTM", tablValue.getData("END_DTTM", i));

				// 给IBS用的参数
				// execData.addData("CTZ1_CODE", tablValue.getData("CTZ1_CODE",
				// i));
				// execData.addData("CTZ2_CODE", tablValue.getData("CTZ2_CODE",
				// i));
				// execData.addData("CTZ3_CODE", tablValue.getData("CTZ3_CODE",
				// i));

				// // CISHl7数据医嘱
				// CISHl7Parm.addData("CASE_NO", caseNo);
				// CISHl7Parm.addData("ORDER_NO", orderNo);
				// CISHl7Parm.addData("ORDER_SEQ", orderSeq);
				// CISHl7Parm.addData("ORDER_DR_CODE", order_dr);
				// CISHl7Parm.addData("START_DTTM",
				// tablValue.getData("START_DTTM", i));
				execData.addData("SETMAIN_FLG", setMainFlg);
				execData.addData("ORDERSET_GROUP_NO", orderSetGroupNo);
				// System.out.println("cat1Type =----" + cat1Type);
				// 如果CAT1_TYPE为LIS或者RIS的将会发送给HL7接口
				if ("LIS".equals(cat1Type) || "RIS".equals(cat1Type)) {
					sendHL7Parm.addData("CASE_NO", caseNo);
					sendHL7Parm.addData("ORDER_NO", orderNo);
					sendHL7Parm
							.addData("ORDER_SEQ", Integer.parseInt(orderSeq));
					flg = 0;
				}
			}
		}
		// -------------------------SHIBLadd
		// // 20120228--------------------------------
		// execData.setData("ADM_TYPE", "I");
		// // System.out.println("-=-=-------------------------"+execData);
		// execData = SysPhaBarTool.getInstance().getaddBarParm(execData,
		// "INW");
//		System.out.println("----------------------------"+execData);
		
		// 细表数据 拿到执行备注更新数据
		detailTbl.acceptText();
		int TblDRow = detailTbl.getRowCount();
		TParm detailTblValue = detailTbl.getParmValue();
		TParm nuserNote = new TParm();
		int noteCount = 0;
		for (int i = 0; i < TblDRow; i++) {
			String execNote = detailTblValue.getValue("EXEC_NOTE", i);
			String skinResult= detailTblValue.getValue("SKIN_RESULT", i);
			String caseNo = (String) detailTblValue.getData("CASE_NO", i);
			String orderNo = (String) detailTblValue.getData("ORDER_NO", i);
			String orderSeq = detailTblValue.getData("ORDER_SEQ", i) + "";
			String orderDate = (String) detailTblValue.getData("ORDER_DATE", i);
			String orderDateTime = (String) detailTblValue.getData(
					"ORDER_DATETIME", i);
			// 需要保存的数据
			nuserNote.addData("CASE_NO", caseNo);
			nuserNote.addData("ORDER_NO", orderNo);
			nuserNote.addData("ORDER_SEQ", orderSeq);
			nuserNote.addData("ORDER_DATE", orderDate.substring(0, 4)
					+ orderDate.substring(5, 7) + orderDate.substring(8));
			nuserNote.addData("ORDER_DATETIME", orderDateTime.substring(0, 2)
					+ orderDateTime.substring(3));
			nuserNote.addData("SKIN_RESULT", skinResult);
			nuserNote.addData("EXEC_NOTE", execNote);
			nuserNote.addData("OPT_USER", Operator.getID());
			nuserNote.addData("OPT_TERM", Operator.getIP());
			
			noteCount++;
		}
		nuserNote.setCount(noteCount);
		// 当护士注释不为零
		if (noteCount != 0) {
			// 把护士备注压入后台数据
			inParm.setData("EXECNOTE", nuserNote.getData());
		}
		// 检测是否有需要存入的数据
		if ((Vector) execData.getData("OPT_USER") != null) {
			// 拿到数量
			int count = ((Vector) execData.getData("OPT_USER")).size();
			execData.setCount(count);
			inParm.setData("EXECDATA", execData.getData());
			// 标记为告知IBS接口是加正或者冲负
			if (checkYES.isSelected())
				inParm.setData("FLG", "SUB");
			else
				inParm.setData("FLG", "ADD");

		}
//		System.out.println("-------------------------"+inParm);
		
		// 调用action执行事务
		TParm result = TIOM_AppServer.executeAction(
				"action.inw.InwOrderExecAction", "onSave", inParm);

		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			return false;
		} else {
//			onCheckData(inParm);// shibl add 20130507
			this.onPrint();
		}
		return true;
	}

	/**
	 * 校验护士执行后有主项执行而细项未执行
	 * 
	 * @param inParm
	 */
	public void onCheckData(TParm inParm) {
		TParm Execparm = inParm.getParm("EXECDATA");// 执行数据
		StringBuffer str = new StringBuffer();
		Map execpat = InwOrderExecTool.getInstance().groupByPatParm(Execparm);
		Iterator it = execpat.values().iterator();
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
			String caseNo = patParm.getValue("CASE_NO", 0);
			String sql = " SELECT DISTINCT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM,A.MR_NO "
					+ " FROM ODI_DSPNM A,"
					+ " (SELECT CASE_NO,ORDER_NO,ORDERSET_CODE,ORDERSET_GROUP_NO "
					+ " FROM ODI_DSPNM WHERE CASE_NO='"
					+ caseNo
					+ "' AND NS_EXEC_CODE IS NOT NULL AND ORDERSET_CODE IS NOT NULL ) B "
					+ " WHERE A.CASE_NO=B.CASE_NO  AND A.ORDER_NO=B.ORDER_NO"
					+ " AND A.ORDERSET_CODE=B.ORDERSET_CODE "
					+ " AND A.ORDERSET_GROUP_NO=B.ORDERSET_GROUP_NO "
					+ " AND A.NS_EXEC_CODE IS  NULL";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				if (str.toString().length() > 0)
					str.append("\r\n");
				str.append("病案号:" + parm.getValue("MR_NO", 0));
				str.append("\r\n");
				str.append("未执行细项PARM:" + parm);
				str.append("\r\n");
				str.append("入参PARM:" + patParm);
			}
		}
		if (str.toString().length() > 0) {
			String sysDateS = StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyyMMddHHmm");
			String root = TIOM_FileServer.getRoot();
			boolean flg = TIOM_FileServer.writeFile(
					TIOM_FileServer.getSocket(), root + "\\NsExeclog\\log"
							+ sysDateS + ".txt", str.toString().getBytes());
		}
	}

	/**
	 * 调用密码验证
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwExe = "inwExe";
		String value = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", inwExe);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}

	/**
	 * 筛选时间医嘱
	 */
	public void SelectTime() {
		masterTbl.acceptText();
		TParm parm = masterTbl.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有可筛选的数据");
			return;
		}
		String time = this.getValueString("TIME");
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\INWPhaFreqUI.x", time);
		if (value == null) {
			this.clearValue("TIME");
			return;
		}
		if (value.getCount() <= 0) {
			this.clearValue("TIME");
			return;
		}
		StringBuffer line = new StringBuffer();
		for (int i = 0; i < value.getCount(); i++) {
			line.append(value.getValue("TIME", i).substring(0, 2) + ":"
					+ value.getValue("TIME", i).substring(2, 4));
			line.append(";");
		}
		this.setValue("TIME", line.toString());
		// 主表的数据
		for (int j = parm.getCount("CASE_NO") - 1; j >= 0; j--) {
			String caseNo = parm.getValue("CASE_NO", j);
			String orderNo = parm.getValue("ORDER_NO", j);
			String orderSeq = parm.getValue("ORDER_SEQ", j);
			String startDttm = parm.getValue("START_DTTM", j);
			String endDttm = parm.getValue("END_DTTM", j);
			String cat1Type = parm.getValue("CAT1_TYPE", j);
			if (!ExeDTableData(caseNo, orderNo, orderSeq, startDttm, endDttm,
					value, cat1Type))
				parm.removeRow(j);
		}
		masterTbl.setParmValue(parm);
		return;
	}

	/**
	 * 处理数据
	 * 
	 * @param caseNo
	 * @param orderNo
	 * @param orderSeq
	 * @param startDttm
	 * @param endDttm
	 * @param parm
	 * @return
	 */
	private boolean ExeDTableData(String caseNo, String orderNo,
			String orderSeq, String startDttm, String endDttm, TParm parm,
			String cat1Type) {
		String sql = "SELECT * FROM ODI_DSPND WHERE CASE_NO='" + caseNo
				+ "' AND ORDER_NO='" + orderNo + "' AND " + " ORDER_SEQ='"
				+ orderSeq + "' AND ORDER_DATE||ORDER_DATETIME BETWEEN '"
				+ startDttm + "' AND '" + endDttm + "'";
		// System.out.println("========sql=============="+sql);
		TParm orderDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("========orderDParm=============="+orderDParm);
		if (orderDParm.getCount() <= 0)
			return false;
		for (int i = 0; i < orderDParm.getCount(); i++) {
			TParm parmRow = orderDParm.getRow(i);
			String orderDataTime = parmRow.getValue("ORDER_DATETIME");
			for (int j = 0; j <= parm.getCount(); j++) {
				String MapTime = parm.getValue("TIME", j);
				if (!cat1Type.equals("PHA") && MapTime.equals("0000")) {
					MapTime = "2359";
				}
				if (MapTime.equals(orderDataTime)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 选择
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void selection(int i, String optName, Timestamp chackTime) {
		masterTbl.setItem(i, "EXE_FLG", "Y");
		// masterTbl.setValueAt(true, i, 12);
		TParm parm = masterTbl.getParmValue().getRow(i);
		// DC时间，检验是否有DC
		Timestamp dcDate = (Timestamp) masterTbl.getValueAt(i, 16); // duzhw
		// modify
		// 15改为16
		// 20131122
		if (dcDate != null) {
			// 如果目前还没有执行时间就把执行时间一并写进去
			String execDate = "" + masterTbl.getValueAt(i, 19); // duzhw modify
			// 18改为19
			// 20131122
			if ("".equals(execDate.trim()) || "null".equals(execDate.trim())) {
				// 临时医嘱完成时间默认为启用时间+10分钟 shibl 20120723 add
				// if (parm.getValue("DSPN_KIND").equals("ST")
				// && parm.getValue("DR_NOTE").equals("")) {
				// Timestamp effdate = parm.getTimestamp("ORDER_DATE");
				// Timestamp STnsexeTime = new Timestamp(
				// effdate.getTime() + 10L * 60L * 1000L);
				// masterTbl.setItem(i, "NS_EXEC_CODE", optName);
				// masterTbl.setItem(i, "NS_EXEC_DATE",
				// StringTool.getString(STnsexeTime, "yyyy/MM/dd"));
				// masterTbl.setItem(i, "NS_EXEC_DATE_TIME",
				// StringTool.getString(STnsexeTime, "HH:mm:ss").substring(0,
				// 5));
				// // masterTbl.setValueAt(optName, i, 17);
				// // masterTbl.setValueAt(
				// // StringTool.getString(STnsexeTime, "yyyy/MM/dd"), i,
				// // 18);
				// // masterTbl.setValueAt(
				// // StringTool.getString(STnsexeTime, "HH:mm:ss"), i,
				// // 19);
				// } else {
				masterTbl.setItem(i, "NS_EXEC_CODE", optName);
				masterTbl.setItem(i, "NS_EXEC_DATE", StringTool.getString(
						chackTime, "yyyy/MM/dd"));
				masterTbl.setItem(i, "NS_EXEC_DATE_TIME", StringTool.getString(
						chackTime, "HH:mm:ss").substring(0, 5));
				// masterTbl.setValueAt(optName, i, 17);
				// masterTbl.setValueAt(
				// StringTool.getString(chackTime, "yyyy/MM/dd"), i,
				// 18);
				// masterTbl.setValueAt(
				// StringTool.getString(chackTime, "HH:mm:ss"), i, 19);
				// }
			}
			masterTbl.setItem(i, "NS_EXEC_DC_CODE", optName);
			masterTbl.setItem(i, "NS_EXEC_DC_DATE_DAY", StringTool.getString(
					chackTime, "yyyy/MM/dd"));
			masterTbl.setItem(i, "NS_EXEC_DC_DATE_TIME", StringTool.getString(
					chackTime, "HH:mm:ss"));
			// masterTbl.setValueAt(optName, i, 22);
			// masterTbl.setValueAt(StringTool.getString(chackTime,
			// "yyyy/MM/dd"),
			// i, 23);
			// masterTbl.setValueAt(StringTool.getString(chackTime, "HH:mm:ss"),
			// i, 24);
		} else {
			// 临时医嘱完成时间默认为启用时间+10分钟 shibl 20120723 add
			// if (parm.getValue("DSPN_KIND").equals("ST")
			// && parm.getValue("DR_NOTE").equals("")) {
			// Timestamp effdate = parm.getTimestamp("ORDER_DATE");
			// Timestamp STnsexeTime = new Timestamp(
			// effdate.getTime() + 10L * 60L * 1000L);
			// masterTbl.setItem(i, "NS_EXEC_CODE", optName);
			// masterTbl.setItem(i, "NS_EXEC_DATE",
			// StringTool.getString(STnsexeTime, "yyyy/MM/dd"));
			// masterTbl.setItem(i, "NS_EXEC_DATE_TIME",
			// StringTool.getString(STnsexeTime, "HH:mm:ss").substring(0, 5));
			// // masterTbl.setValueAt(optName, i, 17);
			// // masterTbl.setValueAt(
			// // StringTool.getString(STnsexeTime, "yyyy/MM/dd"), i, 18);
			// // masterTbl.setValueAt(
			// // StringTool.getString(STnsexeTime, "HH:mm:ss"), i, 19);
			// } else {
			masterTbl.setItem(i, "NS_EXEC_CODE", optName);
			masterTbl.setItem(i, "NS_EXEC_DATE", StringTool.getString(
					chackTime, "yyyy/MM/dd"));
			masterTbl.setItem(i, "NS_EXEC_DATE_TIME", StringTool.getString(
					chackTime, "HH:mm:ss").substring(0, 5));
			// masterTbl.setValueAt(optName, i, 17);
			// masterTbl.setValueAt(
			// StringTool.getString(chackTime, "yyyy/MM/dd"), i, 18);
			// masterTbl.setValueAt(
			// StringTool.getString(chackTime, "HH:mm:ss"), i, 19);
			// }
		}

	}

	/**
	 * table上的checkBox注册监听
	 * 
	 * @param obj
	 *            Object
	 */
	public void onMasterTableCheckBoxChangeValue(Object obj) {
		// 当全部执行的时候设置一次时间
		Timestamp chackTime = TJDODBTool.getInstance().getDBTime();
		// 获得点击的table对象
		TTable table = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		table.acceptText();
		TParm tblParm = table.getParmValue();

		// 获得选中的列/行
		int col = table.getSelectedColumn();
		int row = table.getSelectedRow();
		// 获得table上的行数
		int rowcount = table.getRowCount();
		// 如果选中的是第16列就激发执行动作--执行
		String columnName = table.getParmMap(col);
		if (columnName.equals("EXE_FLG")) {
			boolean exeFlg;
			// 获得点击时的值
			exeFlg = TypeTool.getBoolean(table.getValueAt(row, col));
			// 勾选时
			if (exeFlg) {

				// 勾选行动作
				selection(row, Operator.getName(), chackTime);
				// 得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
				String rxKind = (String) tblParm.getValue("DSPN_KIND", row);
				// -----------------------处理连结医嘱start----------------------------
				// 找到相同的连接号
				String linkNo = (String) table.getValueAt(row, 7); //列位置调整5改7
				if (TCM_Transform.getInt(linkNo) > 0) {
					for (int i = 0; i < rowcount; i++) {
						// 除了当前点击的行号以外的
						if (i != row
								&& linkNo.equals((String) table
										.getValueAt(i, 7)) //列位置调整5改7
								&& rxKind.equals((String) tblParm.getValue(
										"DSPN_KIND", i))) {

							selection(i, Operator.getName(), chackTime);
						}
					}
				}
				// -------------------------处理中草药start-----------------------
				if ("IG".equals(rxKind)) {
					// 中草药的时候以RX_NO(处方签)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// 记录其他的RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// 除了当前点击的行号以外的
						if (!"".equals(rxNoTemp) && rxNoTemp.equals(rxNo)) {
							selection(i, Operator.getName(), chackTime);
						}
					}
				}
			} else { // 取消时 勾选行动作
				unselection(row);
				// 得到选中数据的医嘱类型（由于现在是不同医嘱类型各自计算连接号，所以为了避免在“全部”的状况下会出现重复连接号情况）
				String rxKind = (String) tblParm.getValue("DSPN_KIND", row);
				// -----------------------处理连结医嘱start----------------------------
				// 找到相同的连接号
				String linkNo = (String) table.getValueAt(row, 7); //列位置调整5改7
				if (TCM_Transform.getInt(linkNo) > 0) {
					for (int i = 0; i < rowcount; i++) {
						// 除了当前点击的行号以外的
						if (i != row
								&& linkNo.equals((String) table
										.getValueAt(i, 7)) //列位置调整5改7
								&& rxKind.equals((String) tblParm.getValue(
										"DSPN_KIND", i))) {
							unselection(i);
						}
					}
				}
				// -------------------------处理中草药start-----------------------
				if ("IG".equals(rxKind)) {
					// 中草药的时候以RX_NO(处方签)
					String rxNo = (String) tblParm.getValue("RX_NO", row);
					for (int i = 0; i < rowcount; i++) {
						// 记录其他的RX_NO
						String rxNoTemp = (String) tblParm.getValue("RX_NO", i);
						// 除了当前点击的行号以外的
						if (!"".equals(rxNoTemp) && rxNoTemp.equals(rxNo)) {
							unselection(i);
						}
					}
				}
			}
			// ---------------------处理相同条码号的医嘱start-------------------add by
			// wanglong 20130809
			String boolFlg = table.getValueAt(row, col).toString();
			if (tblParm.getValue("ORDER_CAT1_CODE", row).equals("LIS")
					|| tblParm.getValue("ORDER_CAT1_CODE", row).equals("RIS")) {// 检验检查才有条码
				// 找到相同的条码号
				TParm medNoParm = InwForOdiTool.getInstance().queryMedNo(
						tblParm.getRow(row));
				if (medNoParm.getErrCode() >= 0 && medNoParm.getCount() > 0) {
					String caseNo = tblParm.getValue("CASE_NO", row);
					String orderNo = tblParm.getValue("ORDER_NO", row);
					String orderCat1Code = tblParm.getValue("ORDER_CAT1_CODE",
							row);
					String optItemCode = tblParm.getValue("OPTITEM_CODE", row);
					String medNo = medNoParm.getValue("MED_APPLY_NO", 0);
					for (int i = 0; i < rowcount; i++) {// 遍历masterTable每一行数据，查相同项
						if (i != row
								&& tblParm.getValue("CASE_NO", i)
										.equals(caseNo)
								&& tblParm.getValue("ORDER_NO", i).equals(
										orderNo)
								&& tblParm.getValue("ORDER_CAT1_CODE", i)
										.equals(orderCat1Code)
								&& tblParm.getValue("OPTITEM_CODE", i).equals(
										optItemCode)) {
							TParm rowMedNoParm = InwForOdiTool.getInstance()
									.queryMedNo(tblParm.getRow(i));
							if (rowMedNoParm.getErrCode() < 0) {
								this.messageBox(rowMedNoParm.getErrText());
								break;
							}
							if (rowMedNoParm.getValue("MED_APPLY_NO", 0)
									.equals(medNo)) {
								if (boolFlg.equals("Y")) {// modify by wanglong
									// 20130820
									selection(i, Operator.getName(), chackTime);
								} else {
									unselection(i);
								}
							}
						}
					}
				} else if (medNoParm.getErrCode() < 0) {
					this.messageBox(medNoParm.getErrText());
				}
			}
			// -----------------------end----------------------------------------
		}
	}

	/**
	 * 取消选择
	 * 
	 * @param i
	 *            int
	 * @param nowFlag
	 *            boolean
	 * @param optName
	 *            String
	 * @param chackTime
	 *            Timestamp
	 */
	private void unselection(int i) {
		masterTbl.setItem(i, "EXE_FLG", "N");
		// masterTbl.setValueAt(false, i, 12);
		// DC时间，检验是否有DC
		Timestamp dcDate = (Timestamp) masterTbl.getValueAt(i, 16); // duzhw
		// modify
		// 15改为16
		// 20131122
		if (dcDate != null) {
			masterTbl.setItem(i, "NS_EXEC_DC_CODE", "");
			masterTbl.setItem(i, "NS_EXEC_DC_DATE_DAY", "");
			masterTbl.setItem(i, "NS_EXEC_DC_DATE_TIME", "");
			// masterTbl.setValueAt("", i, 22);
			// masterTbl.setValueAt("", i, 23);
			// masterTbl.setValueAt("", i, 24);

		} else {
			masterTbl.setItem(i, "NS_EXEC_CODE", "");
			masterTbl.setItem(i, "NS_EXEC_DATE", "");
			masterTbl.setItem(i, "NS_EXEC_DATE_TIME", "");
			// masterTbl.setValueAt("", i, 17);
			// masterTbl.setValueAt("", i, 18);
			// masterTbl.setValueAt("", i, 19);
		}
	}

	/**
	 * 初始化补充计价的相应动作
	 */
	public void initPanel() {
		// 初始化界面（与补充计价相关的部分）,模拟MOVEPANEL被双击动作
		mp1.onDoubleClicked(true);

	}

	/**
	 * 初始化界面参数caseNo/stationCode
	 */
	public void initParmFromOutside() {

		// 按就诊号查询的caseNo
		this.setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
		// 按病区查询的stationCode
		this.setStationCode(outsideParm.getValue("INW", "STATION_CODE"));
		this.setMrNo(outsideParm.getValue("INW", "MR_NO"));
		this.setPatName(outsideParm.getValue("INW", "PAT_NAME"));
		this.setIpdNo(outsideParm.getValue("INW", "IPD_NO"));
		this.setDeptCode(outsideParm.getValue("INW", "DEPT_CODE"));
		// 传个IBS的3个身份参数
		ctz1Code = outsideParm.getValue("INW", "CTZ1_CODE");
		ctz2Code = outsideParm.getValue("INW", "CTZ2_CODE");
		ctz3Code = outsideParm.getValue("INW", "CTZ3_CODE");
		saveFlg = outsideParm.getBoolean("INW", "SAVE_FLG");
		// 传ICU注记
		ICUflg = outsideParm.getBoolean("INW", "ICU_FLG");
		clpCode_ = outsideParm.getValue("INW", "CLNCPATH_CODE");

	}

	/**
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 */
	public void showPopMenu(String tableName) {
		// 拿到table对象
		TTable table = (TTable) this.getComponent(tableName);
		// 获得选中行的TParm
		TParm action = masterTbl.getParmValue().getRow(
				masterTbl.getSelectedRow());
		if ("LIS".equals(action.getValue("CAT1_TYPE"))
				|| "RIS".equals(action.getValue("CAT1_TYPE"))) {
			table.setPopupMenuSyntax("显示集合医嘱细项,openRigthPopMenu");
			return;
		} else {
			table.setPopupMenuSyntax("");
			return;
		}
	}

	/**
	 * 打开集合医嘱细项查询
	 */
	public void openRigthPopMenu() {
		// 获得选中行的TParm
		TParm action = masterTbl.getParmValue().getRow(
				masterTbl.getSelectedRow());
		int groupNo = action.getInt("ORDERSET_GROUP_NO");
		String orderCode = action.getValue("ORDER_CODE");
		String caseNo = action.getValue("CASE_NO");
		TParm parm = getOrderSetDetails(caseNo, groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String caseNo, int groupNo,
			String orderSetCode) {
		TParm result = new TParm();
		if (groupNo < 0) {
			return result;
		}
		if (StringUtil.isNullString(orderSetCode)) {
			return result;
		}
		// ===========pangben modify 20110516 start 区域添加
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		String selSetOrder = "SELECT * FROM ODI_DSPNM WHERE CASE_NO='" + caseNo
				+ "' AND ORDERSET_GROUP_NO<>0" + region;
		TParm parm = new TParm(TJDODBTool.getInstance().select(selSetOrder));
		int count = parm.getCount();
		if (count < 0) {
			return result;
		}
		String tempCode;
		int tempNo;
		for (int i = 0; i < count; i++) {
			tempCode = parm.getValue("ORDERSET_CODE", i);
			tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo
					&& !parm.getBoolean("SETMAIN_FLG", i)) {
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("SPECIFICATION", parm.getValue("SPECIFICATION",
						i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				// 查询单价
				TParm ownPriceParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
								+ parm.getValue("ORDER_CODE", i) + "'"));
				// 计算总价格
				double ownPrice = ownPriceParm.getDouble("OWN_PRICE", 0)
						* parm.getDouble("MEDI_QTY", i);
				result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",
						0));
				result.addData("OWN_AMT", ownPrice);
				result.addData("EXEC_DEPT_CODE", parm.getValue(
						"EXEC_DEPT_CODE", i));
				result
						.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE",
								i));
				result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
			}

		}
		return result;
	}

	/**
	 * 根据列数得到列名
	 * 
	 * @param i
	 *            int 行数
	 * @return String
	 */
	public String getColName(int i, TTable tbl) {
		String colName = "";
		colName = tbl.getParmMap(i);
		return colName;
	}

	/**
	 * 修改执行日期和时间（为了抢救医嘱）
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int (NS_EXEC_DATE;NS_EXEC_DATE_TIME)
	 */
	public void onDateTime(TTableNode node) {
		int col = node.getColumn();
		String colName = getColName(col, masterTbl);
		int row = masterTbl.getSelectedRow();
		TParm rowParm = masterTbl.getParmValue().getRow(row);
		Map map = new HashMap();
		if ("NS_EXEC_DATE".equals(colName)) {
			Timestamp temp = (Timestamp) node.getValue();
			Timestamp date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				Timestamp tempT = (Timestamp) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO")
						+ rowParm.getValue("DSPN_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = masterTbl.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = masterTbl.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO")
							+ parm.getValue("DSPN_KIND")
							+ parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						masterTbl.setItem(i, "NS_EXEC_DATE", tempT);
					}
				}
			}
		}
		if ("NS_EXEC_DATE_TIME".equals(colName)) {
			String temp = (String) node.getValue();
			if (temp.length() > 5 || temp.length() < 4) {
				this.messageBox("时间长度错误！");
				node.setValue(node.getOldValue());
				return;
			} else {
				String execDate = "" + masterTbl.getValueAt(row, 19); // duzhw
				// modify
				// 18改为19
				// 20131122
				String execTime = "" + temp;
				if (temp.length() == 5) {
					Timestamp checkDateTime = StringTool.getTimestamp(execDate
							.substring(0, 10).replaceAll("/", "").replaceAll(
									"-", "")
							+ " " + execTime.substring(0, 5) + ":00",
							"yyyyMMdd HH:mm:ss");
					if (checkDateTime == null) {
						this.messageBox("时间格式错误！");
						node.setValue(node.getOldValue());
						return;
					}
					Pattern pattern = Pattern
							.compile("((0[0-9])|(1[0-9])|(2[0-3])):([0-5][0-9])");
					if (!pattern.matcher(execTime).matches()) {
						this.messageBox("时间数值错误！");
						node.setValue(node.getOldValue());
						return;
					}
				} else if (temp.length() == 4) {
					Timestamp checkDateTime = StringTool.getTimestamp(execDate
							.substring(0, 10).replaceAll("/", "").replaceAll(
									"-", "")
							+ " " + execTime.substring(0, 4) + "00",
							"yyyyMMdd HHmmss");
					if (checkDateTime == null) {
						this.messageBox("时间格式错误！");
						node.setValue(node.getOldValue());
						return;
					}
					Pattern pattern = Pattern
							.compile("((0[0-9])|(1[0-9])|(2[0-3]))([0-5][0-9])");
					if (!pattern.matcher(execTime).matches()) {
						this.messageBox("时间数值错误！");
						node.setValue(node.getOldValue());
						return;
					}
				}
			}
			String date = temp;
			node.setValue(date);
			if (isLinkOrder(rowParm)) {
				String tempT = (String) node.getValue();
				String linkstr = rowParm.getValue("CASE_NO")
						+ rowParm.getValue("DSPN_KIND")
						+ rowParm.getValue("LINK_NO");
				map.put(linkstr, linkstr);
				int count = masterTbl.getParmValue().getCount();
				for (int i = 0; i < count; i++) {
					TParm parm = masterTbl.getParmValue().getRow(i);
					String str = parm.getValue("CASE_NO")
							+ parm.getValue("DSPN_KIND")
							+ parm.getValue("LINK_NO");
					if (map.get(str) != null) {
						masterTbl.setItem(i, "NS_EXEC_DATE_TIME", tempT);
					}
				}
			}
		}
	}

	/**
	 * 是否是连结医嘱
	 * 
	 * @param linkOrder
	 *            TParm
	 * @return boolean
	 */
	public boolean isLinkOrder(TParm linkOrder) {
		boolean falg = false;
		if (linkOrder.getInt("LINK_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * 初始化时得到所有控件对象
	 */
	public void myInitControler() {
		CLP = (TComboBox) this.getComponent("CLP");
		from_Date = (TTextFormat) this.getComponent("from_Date");
		to_Date = (TTextFormat) this.getComponent("to_Date");
		from_Time = (TTextField) this.getComponent("from_Time");
		to_Time = (TTextField) this.getComponent("to_Time");

		// 得到table控件
		masterTbl = (TTable) this.getComponent("masterTable");

		detailTbl = (TTable) this.getComponent("detailTable");
		replenishTbl = (TTable) this.getComponent("replenishTable");
		firstDateRadio = (TRadioButton) this.getComponent("firstDateRadio");// add
		// by
		// wanglong
		// 20130626
		secondDateRadio = (TRadioButton) this.getComponent("secondDateRadio");

		// 得到查询条件UI的对象
		ord1All = (TRadioButton) this.getComponent("ord1All");
		ord1ST = (TRadioButton) this.getComponent("ord1ST");
		ord1UD = (TRadioButton) this.getComponent("ord1UD");
		ord1DS = (TRadioButton) this.getComponent("ord1DS");
		ord1IG = (TRadioButton) this.getComponent("ord1IG");

		ord2All = (TRadioButton) this.getComponent("ord2All");
		ord2PHA = (TRadioButton) this.getComponent("ord2PHA");
		ord2PL = (TRadioButton) this.getComponent("ord2PL");
		ord2ENT = (TRadioButton) this.getComponent("ord2ENT");

		typeO = (TCheckBox) this.getComponent("typeO");
		typeE = (TCheckBox) this.getComponent("typeE");
		typeI = (TCheckBox) this.getComponent("typeI");
		typeF = (TCheckBox) this.getComponent("typeF");

		checkAll = (TRadioButton) this.getComponent("checkAll");
		checkYES = (TRadioButton) this.getComponent("checkYES");
		checkNO = (TRadioButton) this.getComponent("checkNO");
		// 得到全全部执行控件
		exeAll = (TCheckBox) this.getComponent("exeALL");
		printAll = (TCheckBox) this.getComponent("printAll");

		mp1 = (TMovePane) callFunction("UI|MovePane_1|getThis");
		mp2 = (TMovePane) callFunction("UI|MovePane_2|getThis");
		mp3 = (TMovePane) callFunction("UI|MovePane_3|getThis");

		// 得到时间控件对象

		// 给上下table注册单击事件监听
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");

		this.callFunction("UI|masterTable|addEventListener", "masterTable->"// add
				// caoyong
				// 20131101
				+ TTableEvent.CLICKED, this, "onTABLEClicked");

		// 给上面table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|masterTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onMasterTableCheckBoxChangeValue");
		// 给下面table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|detailTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onDetailTableCheckBoxChangeValue");
		masterTbl.addEventListener(masterTbl.getTag() + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onDateTime");
		// 相应的的初始化动作
		initDateTime();
		initPanel();
	}

	/**
	 * 初始化时间控件
	 */
	public void initDateTime() {
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 用今天的00：00初始化起始时间
		from_Date.setValue(date);
		from_Time.setValue("00:00");
		String dateStr = StringTool.getString(date, "yyyy/MM/dd");
		this.setValue("ORDER_START_DATETIME", dateStr + " 00:00");// add by
		// wanglong
		// 20130626
		// 用下次摆药时间初始化
		List dispenseDttm = new ArrayList();
		try {
			dispenseDttm = TotQtyTool.getInstance().getNextDispenseDttm(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 根据当前时间拿到最近摆药时间点（最近摆药时间，下次摆药时间）
		// this.messageBox_(dispenseDttm);
		if (dispenseDttm.size() != 0) {
			String disDateTime = (String) dispenseDttm.get(0);
			to_Date.setValue(disDateTime.substring(0, 4) + "/"
					+ disDateTime.substring(4, 6) + "/"
					+ disDateTime.substring(6, 8));
			to_Time.setValue(disDateTime.substring(8, 10) + ":"
					+ disDateTime.substring(10));
			this.setValue("ORDER_END_DATETIME",// add by wanglong 20130626
					disDateTime.substring(0, 4) + "/"
							+ disDateTime.substring(4, 6) + "/"
							+ disDateTime.substring(6, 8) + " "
							+ disDateTime.substring(8, 10) + ":"
							+ disDateTime.substring(10));
		} else {
			// 如果没有返
			to_Date.setValue(date);
			to_Time.setValue("00:00");
			this.setValue("ORDER_END_DATETIME", dateStr + " 00:00");// add by
			// wanglong
			// 20130626
		}
	}

	/**
	 * 清除table上的的数据行
	 */
	public void onRemoveTbl() {
		masterTbl.setParmValue(new TParm());
		detailTbl.setParmValue(new TParm());
		replenishTbl.setParmValue(new TParm());
		exeAll.setSelected(false);
		// onQuery();
	}

	/**
	 * 清空动作
	 */
	public void onClear() {
		// 恢复状态
		callFunction("UI|skiResult|setEnabled", false);// yanjing 20131107
		// 皮试结果按钮
		firstDateRadio.setSelected(true); // 时间类别 add by wanglong 20130626
		ord1All.setSelected(true); // 医嘱类别
		ord2All.setSelected(true); // 医嘱种类
		checkNO.setSelected(true); // 审核状态
		typeO.setSelected(false);
		typeE.setSelected(false);
		typeI.setSelected(false);
		typeF.setSelected(false);
		// 编辑状态
		typeO.setEnabled(false);
		typeE.setEnabled(false);
		typeI.setEnabled(false);
		typeF.setEnabled(false);
		onRemoveTbl();
		clearQueryCondition();
		if (saveFlg)
			this.setCaseNo("");
	}

	public void clearQueryCondition() {
		setValue("INW_DEPT_CODE", "");
		setValue("BED_NO", "");
		setValue("IPD_NO", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		setValue("SEX", "");
		setValue("SERVICE_LEVELIN", "");
		setValue("WEIGHT", "");
		setValue("INW_VC_CODE", "");
		setValue("ADM_DATE", "");
		setValue("TOTAL_AMT", "");
		setValue("PAY_INS", "");
		setValue("YJJ_PRICE", "");
		setValue("GREED_PRICE", "");
		setValue("YJYE_PRICE", "");
		setValue("PRESON_NUM", "");
	}

	/**
	 * 激发补充计价窗口
	 */
	public void onCharge(Object isDbClick) {
		// this.messageBox("come in.");
		boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
		// 得到选中的行数
		/**
		 * int selRow = masterTbl.getSelectedRow(); if (selRow < 0) { //
		 * messageBox("请选择病患医嘱"); // return; outsideParm.setData("INWEXE",
		 * "CASE_NO", getCaseNo()); outsideParm.setData("INWEXE", "ORDER_NO",
		 * ""); outsideParm.setData("INWEXE", "ORDER_SEQ", ""); } else { TParm
		 * tableParm = masterTbl.getParmValue(); // 取得需要传给IBS的参数 String caseNo =
		 * tableParm.getValue("CASE_NO", selRow); String orderNo =
		 * tableParm.getValue("ORDER_NO", selRow); String orderSeq =
		 * tableParm.getValue("ORDER_SEQ", selRow); // 传给IBS的数据
		 * outsideParm.setData("INWEXE", "CASE_NO", caseNo);
		 * outsideParm.setData("INWEXE", "ORDER_NO", orderNo);
		 * outsideParm.setData("INWEXE", "ORDER_SEQ", orderSeq); }
		 **/
		TParm ibsParm = new TParm();
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		String execDeptCode_ = "";
		String vsDrCode_ = "";

		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			// System.out.println("==tableParm=="+tableParm);
			// 取得需要传给SUM的参数
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			station_ = tableParm.getValue("STATION_CODE", selRow);
			bedNo_ = tableParm.getValue("BED_NO", selRow);
			execDeptCode_ = tableParm.getValue("ORDER_DEPT_CODE", selRow);// zhangp
			// 成本中心修改为科室，补充计费不会再错
			// System.out.println("===execDeptCode_==="+execDeptCode_);
			vsDrCode_ = tableParm.getValue("VS_DR_CODE", selRow);
			// System.out.println("===vsDrCode_==="+vsDrCode_);

		} else {
			caseNo_ = this.getCaseNo();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
			station_ = this.getStationCode();
			execDeptCode_ = Operator.getDept();
			// this.getValue("");
			vsDrCode_ = this.getValueString("VC_CODE");
			// System.out.println("===vsDrCode_==="+vsDrCode_);
			// outsideParm.getValue("")
			// this.getValue("");
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo_);
			parm = ADMTool.getInstance().getADM_INFO(parm);
			bedNo_ = parm.getValue("BED_NO", 0);
			// System.out.println("===bedNo_==="+bedNo_);
			// outsideParm.getValue(name)

		}

		// 先选中再调用界面
		if (!dbClickFlg) {
			// outsideParm.setData("IBS", "TYPE", "INW");
            ibsParm.setData("IBS", "INWLEAD_FLG", false);
			ibsParm.setData("IBS", "CASE_NO", caseNo_);
			ibsParm.setData("IBS", "IPD_NO", ipdNo_);
			ibsParm.setData("IBS", "MR_NO", mrNo_);
			ibsParm.setData("IBS", "BED_NO", bedNo_);
			ibsParm.setData("IBS", "DEPT_CODE", execDeptCode_);
			ibsParm.setData("IBS", "STATION_CODE", station_);
			ibsParm.setData("IBS", "VS_DR_CODE", vsDrCode_);
			ibsParm.setData("IBS", "TYPE", "INW");
			ibsParm.setData("IBS", "CLNCPATH_CODE", clpCode_);

			openWindow("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
			/*
			 * //激发MovePane的双击效果 mp1.onDoubleClicked(isCharge); if (!isCharge) {
			 * 
			 * getTPanel("ChargePanel").addItem("ChargePanel_",
			 * "%ROOT%\\config\\ibs\\IBSOrderm.x", outsideParm, false); }
			 * isCharge = isCharge ? false : true; //隐藏右边的按钮
			 * mp2.onDoubleClicked(true); mp3.onDoubleClicked(true);
			 */
		} else { // 界面打开着，双击调用 outsideParm
			this.callFunction("UI|ChargePanel_|getINWData", ibsParm);
		}
	}

	// public boolean onMasterTableChangeValue(Object obj){
	// // 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
	// this.messageBox("dasd");
	// TTableNode node = (TTableNode) obj;
	// if (node == null)
	// return true;
	// this.messageBox("dasd1");
	// // 如果改变的节点数据和原来的数据相同就不改任何数据
	// if (node.getValue().equals(node.getOldValue()))
	// return true;
	// this.messageBox("dasd2");
	// TParm tableParm=masterTbl.getParmValue();
	// // 拿到table上的parmmap的列名
	// String columnName = node.getTable().getDataStoreColumnName(
	// node.getColumn());
	// int row=masterTbl.getSelectedRow();
	// this.messageBox("1");
	// if(columnName.equals("NS_EXEC_DATE_TIME")){
	// this.messageBox("2");
	// String NsexeTime=(String)masterTbl.getValueAt(row, 19);
	// if(NsexeTime.length()<8){
	// this.messageBox("完成时间长度不足");
	// return false;
	// }else{this.messageBox("3");
	// Timestamp
	// datetime=StringTool.getTimestamp(tableParm.getValue("NS_EXEC_DATE",
	// row)+" "+NsexeTime, "yyyy/MM/dd HH:mm:ss");
	// System.out.println("---------------"+datetime);
	// if(datetime==null){
	// this.messageBox("完成时间格式有误");
	// return false;
	// }
	// }
	//
	// }
	// return true;
	// }
	/**
	 * 呼叫体温单接口
	 */
	public void onVitalSign() {
		isVitalSign = !isVitalSign;
		TParm sumParm = new TParm();
		// 得到选中的行数
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			// 取得需要传给SUM的参数
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			station_ = tableParm.getValue("STATION_CODE", selRow);
			bedNo_ = tableParm.getValue("BED_NO", selRow);

		} else {
			caseNo_ = this.getCaseNo();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
			station_ = this.getStationCode();
		}
		if (caseNo_.equals("")) {
			this.messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo_);
		parm = ADMTool.getInstance().getADM_INFO(parm);
		bedNo_ = parm.getValue("BED_NO", 0);
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		sumParm.setData("SUM", "TYPE", "P");
		getTPanel("second").setVisible(false);
		getTPanel("first").addItem("first_",
				"%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm, false);
	}
	/**
	 * 呼叫儿童体温单接口
	 */
	public void onVitalSignChild() {
		isVitalSign = !isVitalSign;
		TParm sumParm = new TParm();
		// 得到选中的行数
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			// 取得需要传给SUM的参数
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			station_ = tableParm.getValue("STATION_CODE", selRow);
			bedNo_ = tableParm.getValue("BED_NO", selRow);

		} else {
			caseNo_ = this.getCaseNo();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
			station_ = this.getStationCode();
		}
		if (caseNo_.equals("")) {
			this.messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo_);
		parm = ADMTool.getInstance().getADM_INFO(parm);
		bedNo_ = parm.getValue("BED_NO", 0);
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		sumParm.setData("SUM", "TYPE", "C");
		getTPanel("second").setVisible(false);
		getTPanel("first").addItem("first_",
				"%ROOT%\\config\\sum\\SUMVitalSignChild.x", sumParm, false);
	}
	/**
	 * 呼叫新生儿体温单
	 * 
	 * @param name
	 *            String
	 * @return TPanel
	 */
	public void onNewArrival() {
		isVitalSign = !isVitalSign;
		TParm sumParm = new TParm();
		// 得到选中的行数
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String station_ = "";
		String bedNo_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			// 取得需要传给SUM的参数
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			station_ = tableParm.getValue("STATION_CODE", selRow);
			bedNo_ = tableParm.getValue("BED_NO", selRow);
		} else {
			caseNo_ = this.getCaseNo();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
			station_ = this.getStationCode();
		}
		if (caseNo_.equals("")) {
			this.messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo_);
		parm = ADMTool.getInstance().getADM_INFO(parm);
		bedNo_ = parm.getValue("BED_NO", 0);
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		getTPanel("second").setVisible(false);
		getTPanel("first").addItem("first_",
				"%ROOT%\\config\\sum\\SUMNewArrival.x", sumParm, false);
	}

	/**
	 * table上的checkBox注册监听
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDetailTableCheckBoxChangeValue(Object obj) {
		// 获得点击的table对象
		TTable detailTable = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		detailTable.acceptText();

	}

	public void selDOSE(Object flg) {
		boolean temp = TypeTool.getBoolean(flg);
		// 清空选择
		typeO.setSelected(temp);
		typeE.setSelected(temp);
		typeI.setSelected(temp);
		typeF.setSelected(temp);
		// 编辑状态
		typeO.setEnabled(temp);
		typeE.setEnabled(temp);
		typeI.setEnabled(temp);
		typeF.setEnabled(temp);
		// 清除table
		onRemoveTbl();
	}

	/**
	 * 调用结构化病历
	 */
	public void onEmr() {
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String patName_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			patName_ = tableParm.getValue("PAT_NAME", selRow);

		} else {
			caseNo_ = this.getCaseNo();
			patName_ = this.getPatName();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
		}
		if (caseNo_.length() == 0) {
			messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", caseNo_);// this.getCaseNo());
		parm.setData("PAT_NAME", patName_);// this.getPatName());
		parm.setData("MR_NO", mrNo_);// this.getMrNo());
		parm.setData("IPD_NO", ipdNo_);// this.getIpdNo());
		parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("EMR_DATA_LIST", new TParm());
		this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * 护理记录
	 */
	public void onNursingRec() {
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		String ipdNo_ = "";
		String patName_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);
			ipdNo_ = tableParm.getValue("IPD_NO", selRow);
			patName_ = tableParm.getValue("PAT_NAME", selRow);

		} else {
			caseNo_ = this.getCaseNo();
			patName_ = this.getPatName();
			mrNo_ = this.getMrNo();
			ipdNo_ = this.getIpdNo();
		}
		if (caseNo_.length() == 0) {
			messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", caseNo_);
		parm.setData("PAT_NAME", patName_);
		parm.setData("MR_NO", mrNo_);
		parm.setData("IPD_NO", ipdNo_);
		// parm.setData("CASE_NO",this.getCaseNo());
		// parm.setData("PAT_NAME",this.getPatName());
		// parm.setData("MR_NO",this.getMrNo());
		// parm.setData("IPD_NO",this.getIpdNo());
		parm.setData("ADM_DATE", outsideParm.getData("INW", "ADM_DATE"));
		parm.setData("DEPT_CODE", this.getDeptCode());
		parm.setData("RULETYPE", "2");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);

	}

	/**
	 * 根据table上的数据更新全部执行标记是否选中
	 */
	public boolean updateAllExe() {
		int rowCount = masterTbl.getRowCount();
		if (rowCount != 0) {
			for (int i = 0; i < rowCount; i++) {
				if (!TypeTool.getBoolean(masterTbl.getValueAt(i, 3))) //列位置调整13改3
					// modify
					// 12改为13
					// 20131122
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 检查对应table上是否存在数据 （每次查询）
	 * 
	 * @param tab
	 *            TTable
	 */
	public void existsDateForTabl(TTable tab) {
		if (tab.getRowCount() == 0)
			this.messageBox("没有相关数据！");
		// 跟新全部执行控件
		exeAll.setSelected(updateAllExe());
	}

	/**
	 * 逆流程(取消审核)保存的时候检核是否已经执行
	 * 
	 * @return boolean
	 */
	public boolean isDisp() {
		// 得到验证是否发药的参数
		TParm ordParm = masterTbl.getParmValue();
		Vector caseNo = ordParm.getVector("CASE_NO");
		Vector orderNo = ordParm.getVector("ORDER_NO");
		Vector orderSeq = ordParm.getVector("ORDER_SEQ");
		String inCaseNo = "";
		String inOrderNo = "";
		String inOrderSeq = "";
		boolean flg1 = true;
		boolean flg2 = true;
		boolean flg3 = true;
		for (int i = 0; i < ordParm.getCount(); i++) {
			// 当有数据的时候(都是为拼IN)
			if (caseNo.size() != 0) {
				// 当前面已经有数据并且和新的一个不相同的时候需要在前面的数据后加逗号(利用短路不会越界)
				if (!inCaseNo.equals("")
						&& !((Vector) caseNo.get(i - 1)).get(0).equals(
								((Vector) caseNo.get(i)).get(0))) {
					inCaseNo += ",";
					flg1 = true;
				}
				if (!inOrderNo.equals("")
						&& !((Vector) orderNo.get(i - 1)).get(0).equals(
								((Vector) orderNo.get(i)).get(0))) {
					inOrderNo += ",";
					flg2 = true;
				}
				if (!inOrderSeq.equals("")
						&& !((Vector) orderSeq.get(i - 1)).get(0).equals(
								((Vector) orderSeq.get(i)).get(0))) {
					inOrderSeq += ",";
					flg3 = true;
				}
				inCaseNo += flg1 ? "'" + ((Vector) caseNo.get(i)).get(0) + "'"
						: "";
				flg1 = false;
				inOrderNo += flg2 ? "'" + ((Vector) orderNo.get(i)).get(0)
						+ "'" : "";
				flg2 = false;
				inOrderSeq += flg3 ? "'" + ((Vector) orderSeq.get(i)).get(0)
						+ "'" : "";
				flg3 = false;
			}
		}
		// ===========pangben modify 20110516 start 区域添加
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		String checkSql = "SELECT  ORDER_DESC" + " FROM ODI_DSPNM"
				+ " WHERE CASE_NO IN(" + inCaseNo + ")" + " AND ORDER_NO IN("
				+ inOrderNo + ")" + " AND ORDER_SEQ IN(" + inOrderSeq + ")"
				+ " AND PHA_DISPENSE_CODE IS NOT NULL" + region;// ===========pangben
		// modify
		// 20110516
		// 执行sql语句
		TJDODBTool tool = TJDODBTool.getInstance();
		TParm result = new TParm();
		result = new TParm(tool.select(checkSql));
		// 拿到返回的行数(PS:用TJDODBTool返回后包装的TParm取行数的特殊方法)
		int count = result.getInt("SYSTEM", "COUNT");
		if (count == 0) {
			return false;
		}
		String hadExecName = "";
		for (int i = 0; i < count; i++) {
			hadExecName += (result.getValue("ORDER_DESC", i) + "\n");

		}
		this.messageBox(hadExecName + "已经发药不可取消");
		return true;
	}

	/**
	 * 勾选所有选项打印
	 */
	public void onPrtAll() {
		boolean prt = printAll.isSelected();
		int count = masterTbl.getRowCount();
		for (int i = 0; i < count; i++) {
			masterTbl.setValueAt(prt, i, 4); //列位置调整14改4
		}
	}

	/**
	 * 医嘱执行单打印
	 */
	public void onPrintOrderExeSheet() {
		if (ord2All.isSelected() || ord1All.isSelected()) {
			messageBox("全部状态下不可打印医嘱执行单");
			return;
		}
		if ((ord2PHA.isSelected())
				&& ((typeO.isSelected() || typeE.isSelected()) && (typeI
						.isSelected() || typeF.isSelected()))) {
			messageBox("请确认药品是属于静脉点滴类还是口服外用类");
			return;
		}
		getPrintPrderExeSheetParm(getCasePrintList());
	}

	/**
	 * 医嘱执行单打印人员列表
	 * 
	 * @return TParm
	 */
	public TParm getCasePrintList() {
		TParm tableParm = masterTbl.getParmValue();
		int count = masterTbl.getRowCount();
		Map map = new HashMap();
		TParm result = new TParm();
		int scout = 0;
		for (int i = 0; i < count; i++) {
			boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 4)); //列位置调整14改4
			// modify
			// 13改为14
			// 20131122
			if (!prtFlg)
				continue;
			if (map.get(tableParm.getValue("CASE_NO", i)) != null)
				continue;
			map.put(tableParm.getValue("CASE_NO", i), tableParm.getValue(
					"CASE_NO", i));
			TParm patParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT * " + " FROM SYS_PATINFO A " + " WHERE MR_NO ='"
							+ tableParm.getData("MR_NO", i) + "'"));
			TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT CHN_DESC " + " FROM SYS_DICTIONARY A "
							+ " WHERE GROUP_ID = 'SYS_SEX' " + " AND  ID = '"
							+ patParm.getData("SEX_CODE", 0) + "'"));
			result.setData("CASE_NO", "TEXT", tableParm.getValue("CASE_NO", i));
			result.setData("DEPT", "TEXT", getDeptDesc(tableParm.getValue(
					"DEPT_CODE", i)));
			result.setData("STATION", "TEXT", getStationDesc(tableParm
					.getValue("STATION_CODE", i)));
			result.setData("DATE", "TEXT", from_Date.getText() + " "
					+ from_Time.getText() + "～" + to_Date.getText() + " "
					+ to_Time.getText());
			result.setData("BED", "TEXT", tableParm.getData("BED_NO", i));
			result.setData("MR_NO", "TEXT", tableParm.getData("MR_NO", i));
			result.setData("IPD_NO", "TEXT", tableParm.getData("IPD_NO", i));
			result.setData("NAME", "TEXT", tableParm.getData("PAT_NAME", i));
			result.setData("SEX", "TEXT", sexDescParm.getData("CHN_DESC", 0));
			result.setData("AGE", "TEXT", StringUtil.getInstance().showAge(
					(Timestamp) patParm.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate()));
			// 给页眉传入数据
			result.setData("filePatName", "TEXT", tableParm.getData("PAT_NAME",
					i));
			result.setData("fileSex", "TEXT", sexDescParm
					.getData("CHN_DESC", 0));
			String birth_date = "";
			if (!"".equals(patParm.getData("BIRTH_DATE", 0)))
				birth_date = patParm.getData("BIRTH_DATE", 0).toString()
						.substring(0, 10).replace('-', '/');
			result.setData("fileBirthday", "TEXT", birth_date);
			result.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", tableParm.getData(
					"MR_NO", i));
			result.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", tableParm.getData(
					"IPD_NO", i));
			if (ord2PL.isSelected() || ord2ENT.isSelected())
				result.setData("TITLE", "TEXT", "检验检查治疗医嘱执行单");
			if (ord2PHA.isSelected()) {
				if (typeO.isSelected() || typeE.isSelected())
					result.setData("TITLE", "TEXT", "口服外用医嘱执行单");
				if (typeI.isSelected() || typeF.isSelected())
					result.setData("TITLE", "TEXT", "静脉注射医嘱执行单");
			}
			scout++;
		}
		result.setCount(scout);
		int a = result.getCount();
		if (a <= 0) {
			return null;
		}
		return result;
	}

	/**
	 * 打印医嘱执行单
	 * 
	 * @param casePrintList
	 *            TParm
	 * @return TParm
	 */
	public TParm getPrintPrderExeSheetParm(TParm casePrintList) {
		if (casePrintList == null) {
			messageBox("无需打印数据");
			return null;
		}
		TParm result = new TParm();
		TParm tableParm = TsetOrderDescLength();
		int count = tableParm.getCount();
		for (int i = 0; i < casePrintList.getCount(); i++) {
			// String caseNo = casePrintList.getValue("CASE_NO", i);
			// int index = 1;
			// int page = 1;
			// int pageRow = 15;
			for (int j = 0; j < count; j++) {
				// if (!caseNo.equals(tableParm.getValue("CASE_NO", j)))
				// continue;
				// if ((index - 1) % pageRow == 0)
				// index = 1;
				// if (index == 1) {
				// cloneParmVector(casePrintList, result, i);
				// result.addData("PAGE", page);
				// page++;
				// }
				//20150226 wangjingchun add start
				//组号
				result.addData("LINK_NO", tableParm.getData("LINK_NO",j));
				//20150226 wangjingchun add end
				result.addData("ORDER", tableParm.getData(
						"ORDER_DESC_AND_SPECIFICATION", j));
				result.addData("QTY",
						numDot(tableParm.getDouble("MEDI_QTY", j)) + " "
								+ getUnit(tableParm.getValue("MEDI_UNIT", j)));
				result.addData("ROUTE", getRouteDesc(tableParm.getValue(
						"ROUTE_CODE", j)));
				 result.addData("FREQ" , tableParm.getValue("FREQ_CODE",
				 j));//20150202 wangjingchun modify
//				result.addData("FREQ", this.getFreq(tableParm.getValue(
//						"FREQ_CODE", j)));
				result.addData("TIME", getTimes(tableParm, j));
				String orderDate = tableParm.getValue("ORDER_DATE", j)
						.substring(0, 19);

				result.addData("ORDER_DATE", orderDate);
				//显示执行人员--xiongwg20150311 start
				String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" +
						tableParm.getValue("NS_EXEC_CODE", j)+ "'";
				TParm dataIn = new TParm(TJDODBTool.getInstance().select(sql));
				result.addData("EXEC_USER", dataIn.getValue("USER_NAME",0));
				//显示执行人员--xiongwg20150311 end
				result.addData("R_EXEC_USER", "");
				// index++;
			}
			// for (int j = index; j <= pageRow; j++) {
			// result.addData("ORDER" + j, "");
			// result.addData("QTY" + j, "");
			// result.addData("ROUTE" + j, "");
			// result.addData("FREQ" + j, "");
			// result.addData("TIME" + j, "");
			// }

		}
		result.setCount(result.getCount("ORDER"));
		//20150226 wangjingchun add start
		result.addData("SYSTEM", "COLUMNS", "LINK_NO");
		//20150226 wangjingchun add end
		result.addData("SYSTEM", "COLUMNS", "ORDER");
		result.addData("SYSTEM", "COLUMNS", "QTY");
		result.addData("SYSTEM", "COLUMNS", "ROUTE");
		result.addData("SYSTEM", "COLUMNS", "FREQ");
		result.addData("SYSTEM", "COLUMNS", "TIME");

		result.addData("SYSTEM", "COLUMNS", "ORDER_DATE");
		result.addData("SYSTEM", "COLUMNS", "EXEC_USER");
		result.addData("SYSTEM", "COLUMNS", "R_EXEC_USER");

		casePrintList.setData("TABLE", result.getData());
		// result.setCount(result.getCount("CASE_NO"));
		// TParm printParm = new TParm();
		// printParm.setData("TABLE", result.getData());
		openPrintWindow("%ROOT%\\config\\prt\\inw\\INWOrderExeSheet_V45.jhw",
				casePrintList, true);
		// transportParmReport(result);
		return casePrintList;
	}

	private String numDot(double medQty) {
		if (medQty == 0)
			return "";
		if ((int) medQty == medQty)
			return "" + (int) medQty;
		else
			return "" + medQty;
	}

	private String getTimes(TParm parm, int index) {
		if (parm.getValue("FREQ_CODE", index).length() == 0)
			return "";
		TParm result = new TParm(TJDODBTool.getInstance().select(
				" SELECT ORDER_DATETIME,DC_DATE " + " FROM ODI_DSPND "
						+ " WHERE CASE_NO = '" + parm.getData("CASE_NO", index)
						+ "'" + " AND   ORDER_NO = '"
						+ parm.getData("ORDER_NO", index) + "'"
						+ " AND   ORDER_SEQ = '"
						+ parm.getData("ORDER_SEQ", index) + "'"
						+ " AND   ORDER_DATE||ORDER_DATETIME "
						+ "       BETWEEN '"
						+ parm.getData("START_DTTM", index) + "' "
						+ "       AND '" + parm.getData("END_DTTM", index)
						+ "'"));
		String times = "";
		for (int i = 0; i < result.getCount() && i <= 8; i++) {
			if (!result.getValue("DC_DATE", i).equals(""))
				continue;
			times += result.getValue("ORDER_DATETIME", i).substring(0, 2) + ":"
					+ result.getValue("ORDER_DATETIME", i).substring(2, 4)
					+ " ";
		}
		return times;
	}

	/**
	 * 拷贝TParm中Vector数据
	 * 
	 * @param fromParm
	 *            TParm
	 * @param toParm
	 *            TParm
	 * @param index
	 *            int
	 */
	private void cloneParmVector(TParm fromParm, TParm toParm, int index) {
		String[] names = fromParm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (fromParm.getData(names[i]) instanceof String)
				continue;
			toParm.addData(names[i], fromParm.getData(names[i], index));
		}
	}

	private String getDesc(TParm parm) {
		// if(parm.getValue("ORDER_CODE").startsWith("ZZZ")){
		// ===========pangben modify 20110516 start 区域添加
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		TParm result = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								" SELECT ORDER_DESC || "
										+ "        CASE WHEN GOODS_DESC IS NULL THEN '' ElSE ('(' || GOODS_DESC || ')') END ||"
										+ "        CASE WHEN SPECIFICATION IS NULL THEN '' ELSE ('(' || SPECIFICATION || ')') END||"
										+ "        CASE WHEN DR_NOTE IS NULL THEN '' ELSE ('(' || DR_NOTE || ')') END DESCALL"
										+ " FROM ODI_ORDER"
										+ " WHERE  CASE_NO = '"
										+ parm.getValue("CASE_NO") + "'"
										+ " AND    ORDER_NO = '"
										+ parm.getValue("ORDER_NO") + "'"
										+ " AND    ORDER_SEQ = '"
										+ parm.getValue("ORDER_SEQ") + "'"
										+ region));// ===========pangben
		// modify
		// 20110516
		return result.getValue("DESCALL", 0);
		// }
		// else
		// return parm.getValue("ORDER_DESC_AND_SPECIFICATION");
	}

	/**
	 * 医嘱名称换行
	 * 
	 * @return TParm
	 */
	private TParm setOrderDescLength() {
		TParm result = new TParm();
		TParm tableParm = masterTbl.getParmValue();
		int count = masterTbl.getRowCount();
		for (int i = 0; i < count; i++) {
			boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 4)); //列位置调整14改4
			// modify
			// 13改为14
			// 20131122
			if (!prtFlg)
				continue;
			// String orderDesc =
			// tableParm.getValue("ORDER_DESC_AND_SPECIFICATION",i);
			String orderDesc = getDesc(tableParm.getRow(i));
			String desc[] = breakNFixRow(orderDesc, 30, 1);
			for (int k = 0; k < desc.length; k++) {
				if (k == 0) {
					result.addData("ORDER_DESC_AND_SPECIFICATION", desc[k]);
					cloneParm(result, tableParm, i);
					continue;
				}
				result.addData("ORDER_DESC_AND_SPECIFICATION", desc[k]);
				cloneParm(result, tableParm, i);
				setNull(result, result.getCount("CASE_NO") - 1);
			}
		}
		result.setCount(result.getCount("CASE_NO"));
		return result;
	}

	/**
	 * 医嘱名称换行
	 * 
	 * @return TParm
	 */
	private TParm TsetOrderDescLength() {
		TParm result = new TParm();
		TParm tableParm = masterTbl.getParmValue();
		int count = masterTbl.getRowCount();
		for (int i = 0; i < count; i++) {
			boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 4)); //列位置调整14改4
			// modify
			// 13改为14
			// 20131122
			if (!prtFlg)
				continue;
			result.addRowData(tableParm, i);
		}
		result.setCount(result.getCount("CASE_NO"));
		return result;
	}

	/**
	 * 拷贝TParm医嘱信息出医嘱名称外
	 * 
	 * @param result
	 *            TParm
	 * @param parm
	 *            TParm
	 * @param index
	 *            int
	 * @return TParm
	 */
	private TParm cloneParm(TParm result, TParm parm, int index) {
		String[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals("ORDER_DESC_AND_SPECIFICATION"))
				continue;
			result.addData(names[i], parm.getData(names[i], index));
		}
		return result;
	}

	/**
	 * 换行置空
	 * 
	 * @param result
	 *            TParm
	 * @param index
	 *            int
	 */
	private void setNull(TParm result, int index) {
		result.setData("MEDI_QTY", index, "");
		result.setData("ROUTE_CODE", index, "");
		result.setData("FREQ_CODE", index, "");
		result.setData("MEDI_UNIT", index, "");
	}

	/**
	 * 发送打印数据
	 * 
	 * @param parm
	 *            TParm
	 */
	private void transportParmReport(TParm parm) {
		String[] names = parm.getNames();
		for (int i = 0; i < names.length; i++)
			parm.addData("SYSTEM", "COLUMNS", names[i]);
		for (int index = 0; index < parm.getCount(); index++) {
			String[] Pnames = parm.getNames();
			TParm printParm = new TParm();
			TParm tParm = new TParm();
			for (String col : names) {
				tParm.addData(col, parm.getValue(col, index));
			}
			tParm.setCount(1);
			printParm.setData("TABLE", tParm.getData());
			openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWOrderExeSheet_V45_1.jhw",
					printParm, true);
		}
	}

	/**
	 * 取得科室中文
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT DEPT_CHN_DESC " + " FROM SYS_DEPT "
						+ " WHERE DEPT_CODE = '" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 取得频次时间点
	 * 
	 * @param freqCode
	 *            String
	 * @return String
	 */
	public String getDescription(String freqCode) {
		if (freqCode.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT DESCRIPTION " + " FROM SYS_PHAFREQ "
						+ " WHERE FREQ_CODE = '" + freqCode + "'"));
		return parm.getValue("DESCRIPTION", 0);
	}

	/**
	 * 取得药品单位中文
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getUnit(String code) {
		if (code.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT "
						+ " WHERE UNIT_CODE = '" + code + "'"));
		return parm.getValue("UNIT_CHN_DESC", 0);
	}

	/**
	 * 取得药品频次中文 yanjing 20140627
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	public String getFreq(String code) {
		if (code.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT FREQ_CHN_DESC " + " FROM SYS_PHAFREQ "
						+ " WHERE FREQ_CODE = '" + code + "'"));
		return parm.getValue("FREQ_CHN_DESC", 0);
	}

	/**
	 * 打印程序/分类执行单
	 */
	/*
	public void onPrint() {
		if (true) {
			onPrintOrderExeSheet();
			return;
		}
		if (ord2All.isSelected()) {
			messageBox("全部状态下不可打印医嘱执行单");
			return;
		}
		TParm selectData = getPrintParm();
		TParm printParm = new TParm();
		// 判断执行那种打印单
		if (typeO.isSelected()) { // 口服单
			printParm = arrangeData(selectData, "O");
			printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
			TParm data = new TParm();
			data.setData("NAME", "TEXT", this.getPatName());
			TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "'"));
			data.setData("STATION", "TEXT", (String) stationDesc.getData(
					"STATION_DESC", 0));
			data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
					+ from_Time.getText() + "～" + to_Date.getText() + " "
					+ to_Time.getText());
			data.setData("TABLE", printParm.getData());
			setNewDate(data);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_O.jhw", data);
		} else if (typeI.isSelected()) { // 注射
			printParm = arrangeData(selectData, "I");
			printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
			TParm data = new TParm();
			data.setData("NAME", "TEXT", this.getPatName());
			TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "'"));
			data.setData("STATION", "TEXT", (String) stationDesc.getData(
					"STATION_DESC", 0));
			data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
					+ from_Time.getText() + "～" + to_Date.getText() + " "
					+ to_Time.getText());
			data.setData("TABLE", printParm.getData());
			setNewDate(data);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_I.jhw", data);
		} else if (typeF.isSelected()) { // 输液
			printParm = arrangeData(selectData, "F");
			printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
			TParm data = new TParm();
			data.setData("NAME", "TEXT", this.getPatName());
			TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "'"));
			// 病区
			data.setData("STATION", "TEXT", (String) stationDesc.getData(
					"STATION_DESC", 0));
			// 统计时间
			data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
					+ from_Time.getText() + "～" + to_Date.getText() + " "
					+ to_Time.getText());
			data.setData("TABLE", printParm.getData());
			setNewDate(data);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWExecOrderPrt_F.jhw", data);
		} else { // 普通执行单
			printParm = arrangeData(selectData, "COM");
			printParm.addData("SYSTEM", "COLUMNS", "CASE_NO");
			printParm.addData("SYSTEM", "COLUMNS", "MR_NO");
			printParm.addData("SYSTEM", "COLUMNS", "BED_NO");
			printParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			printParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
			printParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
			printParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
			printParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
			TParm stationDesc = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ this.getValueString("STATION_CODE") + "'"));
			TParm data = new TParm();
			// 病区
			data.setData("STATION", "TEXT", (String) stationDesc.getData(
					"STATION_DESC", 0));
			data.setData("RX_TYPE", "TEXT", "普通");
			// 统计时间
			data.setData("FROMTODATE", "TEXT", from_Date.getText() + " "
					+ from_Time.getText() + "～" + to_Date.getText() + " "
					+ to_Time.getText());
			data.setData("TABLE", printParm.getData());
			setNewDate(data);
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWExecSheetPrt.jhw", data);
		}
	}
	*/
	
	/*
	 * 分类执行单
	 * add by yangjj 20150603
	 * */
	public void onPrint(){
		execPrint(false);
	}
	
	/**
	 * 汇总执行单打印 add by yangjj 20150603
	 */
	public void onTemporaryPrint(){
		execPrint(true);
	}
	
	public void execPrint(boolean b){
		//b为true则为汇总，false为普通
		
		//医嘱类别不能为全部
		if(((TRadioButton)getComponent("ord1All")).isSelected()){
			this.messageBox("请选择医嘱类别！");
			return;
		}
		
		masterTbl.acceptText();
		TParm p = masterTbl.getParmValue();
		//选择打印复选款的数据
		TParm parm = new TParm();
		for(int i = 0 ; i < p.getCount() ; i++){
			if(TypeTool.getBoolean(masterTbl.getValueAt(i, 4))){
				parm.addRowData(p, i);
			}
		}
		
		if(parm.getCount() <= 0){
			this.messageBox("请选择打印内容！");
			return;
		}
		
		//打印的表格数据
		TParm tableParm = new TParm();
		
		//打印内容
		TParm data = new TParm();
		//表头内容
		data.setData("filePatName","TEXT",this.getPatName());
		data.setData("fileSex","TEXT",((TComboBox)this.getComponent("SEX")).getSelectedName());
		TParm birthDay = new TParm(TJDODBTool.getInstance().select("SELECT BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO = '"+((TTextField)this.getComponent("MR_NO")).getText()+"'"));
		data.setData("fileBirthday","TEXT",birthDay.getValue("BIRTH_DATE", 0).replace("-", "/").substring(0, 10));
		data.setData("FILE_HEAD_TITLE_MR_NO","TEXT",((TTextField)this.getComponent("MR_NO")).getText());
		data.setData("FILE_HEAD_TITLE_IPD_NO","TEXT",((TTextField)this.getComponent("IPD_NO")).getText());
		data.setData("NAME", "TEXT", this.getPatName());
		data.setData("SEX", "TEXT", ((TComboBox)this.getComponent("SEX")).getSelectedName());
		data.setData("AGE", "TEXT", ((TTextField)this.getComponent("AGE")).getText());
		data.setData("MRNO", "TEXT", ((TTextField)this.getComponent("MR_NO")).getText());
		data.setData("BEDNO", "TEXT", ((TTextField)this.getComponent("BED_NO")).getText());
		System.out.println(parm+"");
		//临时医嘱执行单
		if(((TRadioButton)getComponent("ord1ST")).isSelected() && (!b)){
			data.setData("TITLE", "TEXT", "临时医嘱执行单");
			data.setData("TIMES", "TEXT", "用法时点");
			data.setData("TIMES1", "TEXT", "开立时间");
			for(int i = 0 ; i < parm.getCount() ; i++){
				tableParm.addData("LINK_NO", parm.getData("LINK_NO",i));
				tableParm.addData("ORDER_DESC",parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				tableParm.addData("MEDI_QTY",parm.getData("MEDI_QTY", i));
				TParm routeParm = new TParm(TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+parm.getData("ROUTE_CODE", i)+"'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ",parm.getData("FREQ_CODE", i));
				tableParm.addData("TIMES",parm.getData("ORDER_DATE", i).toString().subSequence(11, 16));
				TParm orderDrParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getData("ORDER_DR_CODE", i)+"'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));
				tableParm.addData("TIMES1", parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				tableParm.addData("EXEC_DATE", "");
				tableParm.addData("EXEC_DR", "");
			}
		}
		
		//临时医嘱执行单（汇总）
		else if(((TRadioButton)getComponent("ord1ST")).isSelected() && b){
			data.setData("TITLE", "TEXT", "临时医嘱执行单（汇总）");
			data.setData("TIMES", "TEXT", "开立时间");
			data.setData("TIMES1", "TEXT", "");
			for(int i = 0 ; i < parm.getCount() ; i++){
				tableParm.addData("LINK_NO", parm.getData("LINK_NO",i));
				tableParm.addData("ORDER_DESC",parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				tableParm.addData("MEDI_QTY",parm.getData("MEDI_QTY", i));
				TParm routeParm = new TParm(TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+parm.getData("ROUTE_CODE", i)+"'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ",parm.getData("FREQ_CODE", i));
				tableParm.addData("TIMES",parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				TParm orderDrParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getData("ORDER_DR_CODE", i)+"'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));
				tableParm.addData("TIMES1", "");
				tableParm.addData("EXEC_DATE", "");
				tableParm.addData("EXEC_DR", "");
			}
		}
		
		//长期医嘱执行单
		else if(((TRadioButton)getComponent("ord1UD")).isSelected() && (!b)){
			data.setData("TITLE", "TEXT", "长期医嘱执行单");
			data.setData("VALIDITY","TEXT","有效期:"
					+getValueString("from_Date").replaceFirst("-", "年").replaceFirst("-", "月").replace(" ", "日").substring(0, 11)
					+getValueString("from_Time").replaceFirst(":", "时").substring(0, 5)+"分"
					+"至"+getValueString("to_Date").replaceFirst("-", "年").replaceFirst("-", "月").replace(" ", "日").substring(0, 11)
					+getValueString("to_Time").replaceFirst(":", "时").substring(0, 5)+"分");
			
			//口服
			TParm oParm = new TParm();
			TParm otParm = new TParm();
			int iO = 0;
			//静脉输液
			TParm fParm = new TParm();
			TParm ftParm = new TParm();
			int iF = 0;
			//注射
			TParm iParm = new TParm();
			TParm itParm = new TParm();
			int iI = 0;
			//治疗
			TParm eParm = new TParm();
			TParm etParm = new TParm();
			int iE = 0;
			
			for(int i = 0 ; i < parm.getCount() ; i++){
				TParm routeParm = new TParm(TJDODBTool.getInstance().select("SELECT CLASSIFY_TYPE FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+parm.getData("ROUTE_CODE", i)+"'"));
				parm.addData("CLASSIFY", routeParm.getValue("CLASSIFY_TYPE", 0));
			}
			for(int i = 0 ; i < parm.getCount() ; i++){
				String classify = parm.getData("CLASSIFY", i)+"";
				if("O".equals(classify)){
					oParm.addRowData(parm, i);
					oParm.setCount(++iO);
				}else if("F".equals(classify)){
					fParm.addRowData(parm, i);
					fParm.setCount(++iF);
				}else if("I".equals(classify)){
					iParm.addRowData(parm, i);
					iParm.setCount(++iI);
				}else {
					eParm.addRowData(parm, i);
					eParm.setCount(++iE);
				}
			}
			otParm = getUDPrintTableParm(oParm);
			ftParm = getUDPrintTableParm(fParm);
			itParm = getUDPrintTableParm(iParm);
			etParm = getUDPrintTableParm(eParm);
			
			if(otParm.getCount()<=0){
				otParm = new TParm();
				otParm.setData("Visible", false);
				data.setData("O", "TEXT", "");
			}else{
				data.setData("O", "TEXT", "口服药");
			}
			if(ftParm.getCount()<=0){
				ftParm = new TParm();
				ftParm.setData("Visible", false);
				data.setData("F", "TEXT", "");
			}else{
				data.setData("F", "TEXT", "静脉输液");
			}
			if(itParm.getCount()<=0){
				itParm = new TParm();
				itParm.setData("Visible", false);
				data.setData("I", "TEXT", "");
			}else{
				data.setData("I", "TEXT", "注射");
			}
			if(etParm.getCount()<=0){
				etParm = new TParm();
				etParm.setData("Visible", false);
				data.setData("E", "TEXT", "");
			}else{
				data.setData("E", "TEXT", "治疗");
			}
			data.setData("OTABLE", otParm.getData());
			data.setData("FTABLE", ftParm.getData());
			data.setData("ITABLE", itParm.getData());
			data.setData("ETABLE", etParm.getData());
			
			TParm drParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"'"));
			data.setData("printer","TEXT", drParm.getValue("USER_NAME", 0));
			data.setData("printTime", "TEXT",SystemTool.getInstance().getDate().toString()
						.substring(0, 17)
						.replace(" ", "日")
						.replaceFirst("-", "年")
						.replaceFirst("-", "月")
						.replaceFirst(":", "时")
						.replaceFirst(":", "分"));
			
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\inw\\INWExecUDSheetPrt.jhw", data);
			
			return;
		}
		
		//长期医嘱执行单（汇总）
		else if(((TRadioButton)getComponent("ord1UD")).isSelected() && b){
			data.setData("TITLE", "TEXT", "长期医嘱执行单（汇总）");
			data.setData("TIMES", "TEXT", "开立时间");
			data.setData("TIMES1", "TEXT", "停止时间");
			for(int i = 0 ; i < parm.getCount() ; i++){
				tableParm.addData("LINK_NO", parm.getData("LINK_NO",i));
				tableParm.addData("ORDER_DESC",parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
				tableParm.addData("MEDI_QTY",parm.getData("MEDI_QTY", i));
				TParm routeParm = new TParm(TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+parm.getData("ROUTE_CODE", i)+"'"));
				tableParm.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
				tableParm.addData("FREQ",parm.getData("FREQ_CODE", i));
				tableParm.addData("TIMES", parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
				TParm orderDrParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getData("ORDER_DR_CODE", i)+"'"));
				tableParm.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
				tableParm.addData("DR_NOTE", parm.getData("DR_NOTE", i));
				tableParm.addData("TIMES1", "");
				tableParm.addData("EXEC_DATE", "");
				tableParm.addData("EXEC_DR", "");
			}
		}
		
		else{
			this.messageBox("非临时和长期医嘱不能打印执行单！");
			return;
		}
		
		tableParm.setCount(parm.getCount());
		tableParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		tableParm.addData("SYSTEM", "COLUMNS", "ROUTE");
		tableParm.addData("SYSTEM", "COLUMNS", "FREQ");
		tableParm.addData("SYSTEM", "COLUMNS", "TIMES");
		tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DR");
		tableParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		tableParm.addData("SYSTEM", "COLUMNS", "TIMES1");
		tableParm.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
		tableParm.addData("SYSTEM", "COLUMNS", "EXEC_DR");
		
		TParm drParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"'"));
		data.setData("printer","TEXT", drParm.getValue("USER_NAME", 0));
		data.setData("printTime", "TEXT",SystemTool.getInstance().getDate().toString()
					.substring(0, 17)
					.replace(" ", "日")
					.replaceFirst("-", "年")
					.replaceFirst("-", "月")
					.replaceFirst(":", "时")
					.replaceFirst(":", "分"));
		data.setData("TABLE", tableParm.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\inw\\INWExecSheetPrt.jhw", data);
	}

	private void setNewDate(TParm parm) {
		parm.setData("DEPT", "TEXT", getDeptDesc(getDeptCode()));
		TParm admParm = new TParm();
		admParm.setData("CASE_NO", getCaseNo());
		admParm = ADMTool.getInstance().getADM_INFO(admParm);
		parm.setData("IPD_NO", "TEXT", admParm.getData("IPD_NO", 0));
		parm.setData("MR_NO", "TEXT", admParm.getData("MR_NO", 0));
		parm.setData("BED_NO", "TEXT", admParm.getData("BED_NO", 0));
		TParm sexParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_PATINFO A WHERE MR_NO ='" + getMrNo() + "'"));
		parm.setData("PAT_NAME", "TEXT", sexParm.getData("PAT_NAME", 0));
		TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY A WHERE GROUP_ID = 'SYS_SEX' AND  ID = '"
						+ sexParm.getData("SEX_CODE", 0) + "'"));
		parm.setData("SEX", "TEXT", sexDescParm.getData("CHN_DESC", 0));
		parm.setData("AGE", "TEXT", StringUtil.getInstance().showAge(
				(Timestamp) sexParm.getData("BIRTH_DATE", 0),
				SystemTool.getInstance().getDate()));
		parm.setData("PRT_DATE", "TEXT", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss"));
		parm.setData("PRT_USER", "TEXT", Operator.getName());
	}
	
	public TParm getUDPrintTableParm(TParm parm){
		TParm p = new TParm();
		p.setCount(parm.getCount());
		for(int i = 0 ; i < parm.getCount() ; i++){
			p.addData("LINK_NO", parm.getData("LINK_NO",i));
			p.addData("ORDER_DESC",parm.getData("ORDER_DESC_AND_SPECIFICATION", i));
			p.addData("MEDI_QTY",parm.getData("MEDI_QTY", i));
			TParm routeParm = new TParm(TJDODBTool.getInstance().select("SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"+parm.getData("ROUTE_CODE", i)+"'"));
			p.addData("ROUTE", routeParm.getValue("ROUTE_CHN_DESC", 0));
			p.addData("FREQ",parm.getData("FREQ_CODE", i));
			p.addData("TIMES", TXNewATCTool.getTimeDetail(parm.getData("FREQ_CODE", i)+"").replace("-", "  "));
			TParm orderDrParm = new TParm(TJDODBTool.getInstance().select("SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+parm.getData("ORDER_DR_CODE", i)+"'"));
			p.addData("ORDER_DR", orderDrParm.getValue("USER_NAME", 0));
			p.addData("DR_NOTE", parm.getData("DR_NOTE", i));
			p.addData("TIMES1", parm.getData("ORDER_DATE", i).toString().replace("-", "/").subSequence(0, 16));
			p.addData("EXEC_DATE", "");
			p.addData("EXEC_DR", "");
		}
		p.addData("SYSTEM", "COLUMNS", "LINK_NO");
		p.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		p.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		p.addData("SYSTEM", "COLUMNS", "ROUTE");
		p.addData("SYSTEM", "COLUMNS", "FREQ");
		p.addData("SYSTEM", "COLUMNS", "TIMES");
		p.addData("SYSTEM", "COLUMNS", "ORDER_DR");
		p.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		p.addData("SYSTEM", "COLUMNS", "TIMES1");
		p.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
		p.addData("SYSTEM", "COLUMNS", "EXEC_DR");
		
		return p;
	}

	/**
	 * 判断是否是连接医嘱
	 * 
	 * @return boolean
	 */
	private boolean ifLinkOrder(TParm oneOrder) {
		String LinkNo = (String) oneOrder.getData("LINK_NO");
		if (LinkNo == null || LinkNo.length() == 0)
			return false;
		return true;
	}

	/**
	 * 判断是否是链接医嘱子项
	 * 
	 * @return boolean
	 */
	private boolean ifLinkOrderSubItem(TParm oneOrder) {
		return !TypeTool.getBoolean(oneOrder.getData("LINKMAIN_FLG"));

	}

	/**
	 * 整理数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm arrangeData(TParm parm, String flg) {
		TParm result = new TParm();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			TParm order = parm.getRow(i);
			// 判断连接医嘱
			if (ifLinkOrder(order)) {
				// 如果为连接医嘱细项则不予处理
				if (ifLinkOrderSubItem(order))
					continue;
				String finalOrder = getLinkOrder(order, parm);
				String medi = getLinkQty(order, parm);
				result.addData("ORDER_DESC", finalOrder);
				result.addData("MEDI_QTY", medi);
			} else { // 普通医嘱
				String drNote = (String) order.getData("DR_NOTE");
				String desc = (String) order.getData("ORDER_DESC");
				// 判断是否是医嘱备注
				if (ifZ00Order(order)) {
					desc = drNote;
					drNote = "";
				}
				String finalDesc = "" + desc;
				// 主要参数--医嘱
				result.addData("ORDER_DESC", finalDesc);
				result.addData("MEDI_QTY", "" + order.getData("MEDI_QTY"));
			}
			// 根据医嘱类型设置不同的数据列
			if ("O".equals(flg)) {
				result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
				result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
				result.addData("DISPENSE_QTY", order.getData("DISPENSE_QTY"));
			} else if ("I".equals(flg)) { // 注射
				result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
				result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
			} else if ("F".equals(flg)) { // 输液
				result.addData("ROUTE_CODE", order.getData("ROUTE_CODE"));
				result.addData("FREQ_CODE", order.getData("FREQ_CODE"));
				result.addData("DR_NOTE", order.getData("DR_NOTE"));
			} else if ("COM".equals(flg)) {
				result.addData("CASE_NO", "");// order.getData("CASE_NO")==null?"":order.getData("CASE_NO"));
				result.addData("MR_NO", "");// order.getData("MR_NO")==null?"":order.getData("MR_NO"));
				result.addData("BED_NO", "");// order.getData("BED_NO")==null?"":order.getData("BED_NO"));
				// result.addData("MEDI_QTY", order.getData("MEDI_QTY"));
				result.addData("ROUTE_CODE",
						order.getData("ROUTE_CODE") == null ? "" : order
								.getData("ROUTE_CODE"));
				result.addData("FREQ_CODE",
						order.getData("FREQ_CODE") == null ? "" : order
								.getData("FREQ_CODE"));
				result.addData("NS_EXEC_DATE",
						order.getData("NS_EXEC_DATE") == null ? "" : order
								.getData("NS_EXEC_DATE").toString().substring(
										5, 16));
				result.addData("DR_NOTE", order.getData("DR_NOTE") == null ? ""
						: order.getData("DR_NOTE"));
			}
		}
		result.setCount(result.getCount("ORDER_DESC"));
		return result;
	}

	/**
	 * 判断数否是医嘱备注
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean ifZ00Order(TParm parm) {
		String orderCode = (String) parm.getData("ORDER_CODE");
		return orderCode.startsWith("Z");
	}

	/**
	 * 整理连接医嘱ORDER_DESC
	 * 
	 * @param order
	 *            TParm
	 * @param parm
	 *            TParm
	 * @return String
	 */
	private String getLinkOrder(TParm order, TParm parm) {
		String resultDesc = "";
		String mainOrder = (String) order.getData("ORDER_DESC");
		String mainLinkNo = (String) order.getData("LINK_NO");
		String mainDspnKind = (String) order.getData("DSPN_KIND");
		resultDesc = mainOrder;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			String linkNo = (String) parm.getData("LINK_NO", i);
			String dspnKind = (String) parm.getData("DSPN_KIND", i);
			if (dspnKind.equals(mainDspnKind) && mainLinkNo.equals(linkNo)
					&& !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
				String subOrder = (String) parm.getData("ORDER_DESC", i);
				resultDesc += "\r" + subOrder;
			} else
				continue;
		}
		return resultDesc;
	}

	/**
	 * 
	 * @param order
	 *            TParm
	 * @param parm
	 *            TParm
	 * @return String
	 */
	private String getLinkQty(TParm order, TParm parm) {
		String resultString = "";
		String mainMediQty = (String) order.getData("MEDI_QTY");
		String mainLinkNo = (String) order.getData("LINK_NO");
		String mainDspnKind = (String) order.getData("DSPN_KIND");
		resultString = mainMediQty;
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			String linkNo = (String) parm.getData("LINK_NO", i);
			String dspnKind = (String) parm.getData("DSPN_KIND", i);
			if (dspnKind.equals(mainDspnKind) && mainLinkNo.equals(linkNo)
					&& !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
				String subMediQty = (String) parm.getData("MEDI_QTY", i);
				resultString += "\r" + subMediQty;
			} else
				continue;
		}
		return resultString;
	}

	/**
	 * 得到打印的数据支持4种报表：普通执行单 静脉输液单
	 * 
	 * @return TParm
	 */
	private TParm getPrintParm() {
		TParm result = new TParm();
		TParm getShowParm = masterTbl.getShowParmValue();
		TParm hideParm = masterTbl.getParmValue();
		int count = masterTbl.getRowCount();
		for (int i = 0; i < count; i++) {
			boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 4)); //列位置调整14改4
			// modify
			// 13改为14
			// 20131122
			if (prtFlg) {
				result.addData("CASE_NO", hideParm.getData("CASE_NO", i));
				result.addData("MR_NO", hideParm.getData("MR_NO", i));
				result.addData("BED_NO", hideParm.getData("BED_NO", i));

				result.addData("ORDER_CODE", hideParm.getData("ORDER_CODE", i));
				result.addData("ORDER_DESC", hideParm.getData("ORDER_DESC", i));
				result.addData("DSPN_KIND", hideParm.getData("DSPN_KIND", i));
				result.addData("MEDI_QTY", getShowParm.getData("MEDI_QTY", i)
						+ " " + getShowParm.getData("MEDI_UNIT", i));
				result.addData("ROUTE_CODE", getShowParm.getData("ROUTE_CODE",
						i));
				result
						.addData("FREQ_CODE", getShowParm.getData("FREQ_CODE",
								i));
				result.addData("DR_NOTE", getShowParm.getData("DR_NOTE", i));
				result.addData("DISPENSE_QTY", getShowParm.getData(
						"DISPENSE_QTY", i)
						+ " " + getShowParm.getData("DISPENSE_UNIT", i));
				result.addData("LINKMAIN_FLG", getShowParm.getData(
						"LINKMAIN_FLG", i));
				result.addData("LINK_NO", getShowParm.getData("LINK_NO", i));
				result.addData("NS_EXEC_DATE", hideParm.getData("NS_EXEC_DATE",
						i));
				// result.addData("DR_NOTE", hideParm.getData("DR_NOTE", i));
			}
		}
		// 设置count
		result.setCount(result.getCount("ORDER_DESC"));
		return result;
	}

	/**
	 * 费用保存后校验是否停止计划（for ADM）
	 */
	private void checkStopFee() {
		String caseNo = this.getCaseNo();
		ADMTool.getInstance().checkStopFee(caseNo);
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// TParm a=new TParm();
		// a.setData("a","aaaaaaaaa");
		// TParm result=
		// TJDODBTool.getInstance().exeIOAction("jdo.inw.testINW",a);
		// System.out.println("=>"+result);

		// switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
		// case 0:
		// if (!onSave())
		// return false;
		// break;
		// case 1:
		// if (!restoreUI())
		// return false;
		// break;
		// case 2:
		// return false;
		// }
		if (!restoreUI())
			return false;
		return true;
	}

	/**
	 * 回复UI界面
	 * 
	 * @return boolean
	 */
	private boolean restoreUI() {
		// 关闭补充计价界面
		if (isCharge) {
			// 移除补充计价面板
			getTPanel("ChargePanel").removeAll();
			mp1.onDoubleClicked(isCharge);
			isCharge = !isCharge;
			mp2.onDoubleClicked(false);
			mp3.onDoubleClicked(false);
			callFunction("UI|showTopMenu");
			return false;
		}
		if (isVitalSign) {
			getTPanel("first").remove(getTPanel("first_"));
			getTPanel("second").setVisible(true);
			isVitalSign = !isVitalSign;
			// 移除子UIMenuBar
			callFunction("UI|removeChildMenuBar");
			// 移除子UIToolBar
			callFunction("UI|removeChildToolBar");
			// 显示UIshowTopMenu
			callFunction("UI|showTopMenu");
			return false;
		}
		return true;
	}

	/**
	 * 处理切换的时候跟换TopMenu
	 */
	public void onShowWindowsFunction() {
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	// 动态调出相应的panel
	public TPanel getTPanel(String name) {

		TPanel panel = (TPanel) this.getComponent(name);
		return panel;
	}

	/**
	 * 条码打印
	 */
	public void onBarCode() {
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
		} else {
			caseNo_ = this.getCaseNo();
		}
		if (caseNo_.length() == 0) {
			messageBox("请选择病人！");
			return;
		}
		// String sql1 = "SELECT * FROM ADM_INP WHERE CASE_NO='" +
		// this.getCaseNo() + "'";
		// ===========pangben modify 20110516 start 区域添加
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		String sql1 = "SELECT * FROM ADM_INP WHERE CASE_NO='" + caseNo_ + "'"
				+ region;
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
		String mrNo = result1.getValue("MR_NO", 0);
		String sql2 = "SELECT * FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		TParm parm = new TParm();
		// 参数
		parm.setData("DEPT_CODE", result1.getData("DEPT_CODE", 0));
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", caseNo_);
		// parm.setData("CASE_NO", this.getCaseNo());
		parm.setData("MR_NO", result1.getData("MR_NO", 0));
		parm.setData("PAT_NAME", result2.getData("PAT_NAME", 0));
		parm.setData("ADM_DATE", result1.getData("ADM_DATE", 0));
		parm.setData("IPD_NO", result2.getData("IPD_NO", 0));
		parm.setData("BED_NO", result1.getData("BED_NO", 0));
		parm.setData("POPEDEM", "1");
		String value = (String) this.openDialog(
				"%ROOT%\\config\\med\\MEDApply.x", parm);
	}

	public String getCaseNo() {
		return caseNo;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	/**
	 * 费用查询
	 */
	public void onSelIbs() {
		int selRow = masterTbl.getSelectedRow();
		String caseNo_ = "";
		String mrNo_ = "";
		if (selRow != -1) {
			TParm tableParm = masterTbl.getParmValue();
			caseNo_ = tableParm.getValue("CASE_NO", selRow);
			mrNo_ = tableParm.getValue("MR_NO", selRow);

		} else {
			caseNo_ = this.getCaseNo();
			mrNo_ = this.getMrNo();
		}
		if (caseNo_.length() == 0) {
			messageBox("请选择病人！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("IBS", "CASE_NO", caseNo_);
		parm.setData("IBS", "MR_NO", mrNo_);
		// parm.setData("IBS","CASE_NO",this.getCaseNo());
		// parm.setData("IBS","MR_NO",this.getMrNo());
		parm.setData("IBS", "TYPE", "INWSTATION");
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
	}

	/**
	 * 瓶签打印
	 */
	public void onPrintPaster() {
		Vector vct = new Vector();
		TParm parm = masterTbl.getParmValue();
		for (int i = 0; i < 21; i++) {
			vct.add(new Vector());
		}
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
			((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
			((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
			((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
			((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
			((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i) + ""
					+ parm.getData("SPECIFICATION", i));
			((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
			((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
			((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
			((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
			((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
			((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
			((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));
			((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
					"STATION_CODE", i)));
			TParm patParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
							+ parm.getData("MR_NO", i) + "'"));
			TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
							+ " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
							+ patParm.getData("SEX_CODE", 0) + "'"));
			((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
			((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
					(Timestamp) patParm.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate()));
			String udSt = "";
			if (parm.getData("DSPN_KIND", i).equals("UD")
					|| parm.getData("DSPN_KIND", i).equals("F"))
				udSt = "长期医嘱";
			else if (parm.getData("DSPN_KIND", i).equals("ST"))
				udSt = "临时医嘱";
			((Vector) vct.get(16)).add(udSt);
			((Vector) vct.get(17)).add(parm.getValue("ROUTE_CODE", i));
			((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
					"ORDER_DR_CODE", i)));
			((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
			((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
		}
		vct.add(getUnitMap());
		openWindow("%ROOT%\\config\\inw\\INWPrintPQUI.x", vct);
	}

	/**
	 * 
	 * 瓶签打印 luhai 2012-2-24
	 */
	public void onPrintPasterBottle() {
		//this.messageBox("1");
		Vector vct = new Vector();
		TParm parm = masterTbl.getParmValue();
		//System.out.println("ppppppppp"+parm);
		// modify by wangb 2017/08/11 增加出生日期
		for (int i = 0; i < 26; i++) {//  24变更25  增加一个变量  machao
			vct.add(new Vector());
		}
		String cat1Type = "";
		String orderCode = "";
		String orderDesc = "";
		String Dosetype = "";
		String routeCode = "";
		String birthdate = "";
		// System.out.println("=====数据："+parm);
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			cat1Type = parm.getData("CAT1_TYPE", i) + "";
			orderCode = (String) parm.getData("ORDER_CODE", i);
			orderDesc = (String) parm.getData("ORDER_DESC", i);
			routeCode = (String) parm.getData("ROUTE_CODE", i);
			if (TypeTool.getBoolean(masterTbl.getValueAt(i, 4))) { //列位置调整14改4
				// modify
				// 13改为14
				// 20131122
				if (cat1Type.equals("PHA")) {
					/*
					 * Dosetype = SysPhaBarTool.getInstance().getDoseType(
					 * orderCode);
					 */
					Dosetype = SysPhaBarTool.getInstance().getClassifyType(
							routeCode);

					if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
						this.messageBox(orderDesc + "不是针剂或点滴，不能打印！");
						return;
					}
				}
			}
			if (!("true".equals(masterTbl.getValueAt(i, 4) + "") || ("Y" //列位置调整14改4
			// modify
					// 13改为14
					// 20131122
					.equals(masterTbl.getValueAt(i, 4) + "")))) { //列位置调整14改4
				// modify
				// 13改为14 20131122
				continue;
			}
			//添加药品条码已打印确认xiongwg20150128 start
//			 String orderDate = parm.getValue("ORDER_DATE",i).substring(0,4)+
//			 parm.getValue("ORDER_DATE",i).substring(5,7)+
//			 parm.getValue("ORDER_DATE",i).substring(8,10);
			if(parm.getValue("PRT", i).equals("Y")){
				String sqlbar = "SELECT BAR_CODE FROM ODI_DSPND " +
				"WHERE CASE_NO='"+parm.getValue("CASE_NO",i)	+
				"' AND ORDER_NO='"+parm.getValue("ORDER_NO",i)	+
				"' AND ORDER_SEQ='"+parm.getValue("ORDER_SEQ",i)	+
//				"' AND ORDER_DATE='"+orderDate	+
				"' AND ORDER_DATE||ORDER_DATETIME BETWEEN '"	+
				parm.getValue("START_DTTM",i)+"' AND '"	+
				parm.getValue("END_DTTM",i)	+	"'";
				TParm bar = new TParm(TJDODBTool.getInstance().select(sqlbar));
				if(bar.getCount()<0 || bar.getValue("BAR_CODE",0).equals("")){
					this.messageBox("请先生成药品条码");
					return;
				}
			}
			//添加药品条码已打印确认xiongwg20150128 end
			((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
			((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
			((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
			((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
			((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
			((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i) + ""
					+ parm.getData("SPECIFICATION", i));
			((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
			((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
			((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
			((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
			((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
			((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
			// modify by huangjw 20140731 获取FREQ_CHN_DESC
//			TParm freqParm = new TParm(TJDODBTool.getInstance().select(
//					"SELECT * FROM SYS_PHAFREQ WHERE FREQ_CODE='"
//							+ parm.getData("FREQ_CODE", i) + "'"));
//			((Vector) vct.get(12)).add(freqParm.getValue("FREQ_CHN_DESC", 0));
			((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));//20150202 wangjingchun add
			((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
					"STATION_CODE", i)));
			TParm patParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
							+ parm.getData("MR_NO", i) + "'"));
			TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
							+ " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
							+ patParm.getData("SEX_CODE", 0) + "'"));
			((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
			((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
					(Timestamp) patParm.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate()));
			String udSt = "";
			if (parm.getData("DSPN_KIND", i).equals("UD")
					|| parm.getData("DSPN_KIND", i).equals("F"))
				udSt = "长期医嘱";
			else if (parm.getData("DSPN_KIND", i).equals("ST"))
				udSt = "临时医嘱";
			((Vector) vct.get(16)).add(udSt);
			// modify by huangjw 20140731 获取ROUTE_CHN_DESC
			TParm routeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT * FROM SYS_PHAROUTE WHERE ROUTE_CODE='"
							+ parm.getData("ROUTE_CODE", i) + "'"));
			((Vector) vct.get(17)).add(routeParm.getValue("ROUTE_CHN_DESC", 0));
			((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
					"ORDER_DR_CODE", i)));
			((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
			((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
			// 加入剂型
			((Vector) vct.get(21)).add(parm.getData("CLASSIFY_TYPE", i));
			// 加入CASE_NO
			((Vector) vct.get(22)).add(parm.getData("CASE_NO", i));
			// 加入 END_DTTM
			((Vector) vct.get(23)).add(parm.getData("END_DTTM", i));
			// 加入速率  machao
			((Vector) vct.get(24)).add(parm.getData("INFLUTION_RATE", i));
			// 出生日期
			birthdate = patParm.getValue("BIRTH_DATE", 0);
			if (null != birthdate && birthdate.length() >= 10) {
				birthdate = birthdate.substring(0, 4) + "年"
						+ birthdate.substring(5, 7) + "月"
						+ birthdate.substring(8, 10) + "日";
			}
			((Vector) vct.get(25)).add(birthdate);
		}
		vct.add(getUnitMap());
		//this.messageBox(vct+"");
		// openWindow("%ROOT%\\config\\inw\\INWPrintBottonUI.x", vct);
		// 打印瓶签（直接打印）
		printBottle(vct);
	}

	/**
	 * 
	 * 打印瓶签方法 luhai 2012-2-28
	 * 
	 * @param buttonVct
	 */
	public void printBottle(Vector buttonVct) {
		//this.messageBox("2");
		parm = initPageData((Vector) buttonVct);
		//this.messageBox(parm+"");
		Object objPha = (buttonVct).get((buttonVct).size() - 1);
		if (objPha != null) {
			phaMap = (Map) (buttonVct).get((buttonVct).size() - 1);
		}
		// 打印瓶签
		// 选中行
		int row = 0;
		// 选中列
		int column = 0;

		int count = parm.getCount("BED_NO");
		if (count <= 0) {
			this.messageBox_("没有要打印的医嘱！");
			return;
		}
		TParm actionParm = creatPrintData();
		int rowCount = actionParm.getCount("PRINT_DATAPQ");
		if (rowCount <= 0) {
			this.messageBox_("打印数据错误！");
			return;
		}
		// TParm printDataPQParm = new TParm();
		// int pRow = row;
		// int pColumn = column;
		// int cnt=0;
		// int rowNull = 0;
		// for(int i = 0; i < 15; i++){
		// if (i % 3 == 0&&i!=0){
		// cnt = 0 ;
		// rowNull++;
		// }
		// if (i < pRow * 3 + pColumn) {
		// printDataPQParm.addData("ORDER_DATE_"+(cnt+1),"");
		// printDataPQParm.addData("BED_"+(cnt+1),"");
		// printDataPQParm.addData("PAT_NAME_"+(cnt+1),"");
		// printDataPQParm.addData("ORDER_1_"+(cnt+1),"");
		// printDataPQParm.addData("QTY_1_"+(cnt+1),"");
		// printDataPQParm.addData("TOT_QTY_1_"+(cnt+1),"");
		// printDataPQParm.addData("ORDER_2_"+(cnt+1),"");
		// printDataPQParm.addData("QTY_2_"+(cnt+1),"");
		// printDataPQParm.addData("TOT_QTY_2_"+(cnt+1),"");
		// printDataPQParm.addData("ORDER_3_"+(cnt+1),"");
		// printDataPQParm.addData("QTY_3_"+(cnt+1),"");
		// printDataPQParm.addData("TOT_QTY_3_"+(cnt+1),"");
		// printDataPQParm.addData("ORDER_4_"+(cnt+1),"");
		// printDataPQParm.addData("QTY_4_"+(cnt+1),"");
		// printDataPQParm.addData("TOT_QTY_4_"+(cnt+1),"");
		// printDataPQParm.addData("ORDER_5_"+(cnt+1),"");
		// printDataPQParm.addData("QTY_5_"+(cnt+1),"");
		// printDataPQParm.addData("TOT_QTY_5_"+(cnt+1),"");
		// printDataPQParm.addData("STATION_DESC_"+(cnt+1),"");
		// printDataPQParm.addData("MR_NO_"+(cnt+1),"");
		// printDataPQParm.addData("SEX_"+(cnt+1),"");
		// printDataPQParm.addData("AGE_"+(cnt+1),"");
		// printDataPQParm.addData("RX_TYPE_"+(cnt+1),"");
		// printDataPQParm.addData("FREQ_CODE_"+(cnt+1),"");
		// printDataPQParm.addData("DOCTOR_"+(cnt+1),"");
		// printDataPQParm.addData("ROUT_"+(cnt+1),"");
		// printDataPQParm.addData("PAGE_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_NAME_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_QTY_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_TOT_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_DR_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_CHECK_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_EXE_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_PAGEF_"+(cnt+1),"");
		// printDataPQParm.addData("TITLE_PAGEB_"+(cnt+1),"");
		// cnt++;
		// continue;
		// }else{
		// break;
		// }
		// }
		// for (int i = 0; i < rowCount; i++) {
		// TParm temp = (TParm)actionParm.getData("PRINT_DATAPQ",i);
		// printDataPQParm.addData("ORDER_DATE_"+(pColumn+1),temp.getData("DATETIME"));
		// printDataPQParm.addData("BED_" + (pColumn + 1),
		// temp.getData("BED_NO"));
		// printDataPQParm.addData("PAT_NAME_" + (pColumn + 1),
		// temp.getData("PAT_NAME"));
		// printDataPQParm.addData("STATION_DESC_" + (pColumn + 1),
		// temp.getData("STATION_DESC"));
		// printDataPQParm.addData("MR_NO_" + (pColumn + 1),
		// temp.getData("MR_NO"));
		// printDataPQParm.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
		// printDataPQParm.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
		// printDataPQParm.addData("RX_TYPE_" + (pColumn + 1),
		// temp.getData("RX_TYPE"));
		// printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1),
		// temp.getData("FREQ"));
		// printDataPQParm.addData("DOCTOR_" + (pColumn + 1),
		// temp.getData("DOCTOR"));
		// printDataPQParm.addData("ROUT_" + (pColumn + 1),
		// temp.getData("ROUTE"));
		// printDataPQParm.addData("PAGE_" + (pColumn + 1),
		// temp.getData("PAGE"));
		//
		// printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1),"药名");
		// printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1),"用量");
		// printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1),"数量");
		// printDataPQParm.addData("TITLE_DR_" + (pColumn + 1),"医生:");
		// printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1),"审核:");
		// printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1),"执行:");
		// printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1),"第");
		// printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1),"页");
		// int rowOrderCount = temp.getCount("ORDER_DESC");
		// for(int j=0;j<5;j++){
		// if(j>rowOrderCount-1){
		// printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),"");
		// printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),"");
		// printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),"");
		// continue;
		// }
		// printDataPQParm.addData("ORDER_"+(j+1)+"_"+(pColumn+1),temp.getData("ORDER_DESC",j));
		// printDataPQParm.addData("QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("QTY",j))+""+temp.getData("UNIT_CODE",j));
		// printDataPQParm.addData("TOT_QTY_"+(j+1)+"_"+(pColumn+1),numDot(temp.getDouble("DOSAGE_QTY",j))+""+temp.getData("DOSAGE_UNIT",j));
		// }
		// pColumn++;
		// if (pColumn == 3) {
		// pColumn = 0;
		// pRow++;
		// }
		// }
		// printDataPQParm.setCount(pRow+rowNull+1);
		// printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
		// printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
		// printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
		// TParm parmForPrint = new TParm();
		// parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
		// System.out.println("======="+parmForPrint);
		// // TParm parm = new TParm();
		// // TParm prtTableParm = new TParm();
		// // prtTableParm.addData("TEST", "222222222");
		// // prtTableParm.addData("TEST", "333333333333");
		// // prtTableParm.addData("TEST", "122222");
		// // prtTableParm.addData("TEST2", "---------");
		// // prtTableParm.addData("TEST2", "333333333333");
		// // prtTableParm.addData("TEST2", "122222");
		// // prtTableParm.setCount(prtTableParm.getCount("TEST"));
		// // prtTableParm.addData("SYSTEM", "COLUMNS", "A1");
		// // prtTableParm.addData("SYSTEM", "COLUMNS", "A2");
		// // prtTableParm.addData("SYSTEM", "COLUMNS", "A3");
		// // parm.setData("PRINT_PQ", prtTableParm.getData());
		// // System.out.println("****************************"+parm);
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		// ***************************************************
		// 重新处理打印瓶签方法，将数据整合到一列中进行打印 luhai 2012-2-29 begin
		// ***************************************************
		TParm printDataPQParm = new TParm();
		int pRow = row;
		int pColumn = column;
		int cnt = 0;
		int rowNull = 0;
		for (int i = 0; i < 15; i++) {
			if (i % 3 == 0 && i != 0) {
				// cnt = 0 ;
				rowNull++;
			}
			if (i < pRow * 3 + pColumn) {
				printDataPQParm.addData("ORDER_DATE_" + (cnt + 1), "");
				printDataPQParm.addData("BED_" + (cnt + 1), "");
				printDataPQParm.addData("PAT_NAME_" + (cnt + 1), "");
				printDataPQParm.addData("BAR_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_1_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_2_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_3_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_4_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_5_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("STATION_DESC_" + (cnt + 1), "");
				printDataPQParm.addData("MR_NO_" + (cnt + 1), "");
				printDataPQParm.addData("SEX_" + (cnt + 1), "");
				printDataPQParm.addData("AGE_" + (cnt + 1), "");
				printDataPQParm.addData("RX_TYPE_" + (cnt + 1), "");
				printDataPQParm.addData("FREQ_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("DOCTOR_" + (cnt + 1), "");
				printDataPQParm.addData("ROUT_" + (cnt + 1), "");
				printDataPQParm.addData("PAGE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_NAME_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_QTY_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_TOT_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_DR_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_CHECK_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_EXE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEF_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEB_" + (cnt + 1), "");
				printDataPQParm.addData("LINK_NO_"+ (cnt + 1),"" );
				printDataPQParm.addData("RUNTE_1_" + (cnt + 1), "");//增加速率  machao
				printDataPQParm.addData("RUNTE_2_" + (cnt + 1), "");//增加速率  machao
				printDataPQParm.addData("RUNTE_3_" + (cnt + 1), "");//增加速率  machao
				printDataPQParm.addData("RUNTE_4_" + (cnt + 1), "");//增加速率  machao
				printDataPQParm.addData("RUNTE_5_" + (cnt + 1), "");//增加速率  machao
				printDataPQParm.addData("BIRTHDATE", "");// 增加出生日期
				// cnt++;
				continue;
			} else {
				break;
			}
		}
		// 设置变量计算执行时的实际数据数目
		int realtotCount = 0;
		for (int i = 0; i < rowCount; i++) {
			TParm temp = (TParm) actionParm.getData("PRINT_DATAPQ", i);
			// TJDODBTool.getInstance().select("  ");
			printDataPQParm.addData("ORDER_DATE_" + (pColumn + 1), temp
					.getData("DATETIME"));
			printDataPQParm.addData("BED_" + (pColumn + 1), temp
					.getData("BED_NO"));
			printDataPQParm.addData("PAT_NAME_" + (pColumn + 1), temp
					.getData("PAT_NAME"));
//			 this.messageBox( temp.getData("BAR_CODE")+"");
			printDataPQParm.addData("BAR_CODE_" + (pColumn + 1), temp
					.getData("BAR_CODE"));
			printDataPQParm.addData("STATION_DESC_" + (pColumn + 1), temp
					.getData("STATION_DESC"));
			printDataPQParm.addData("MR_NO_" + (pColumn + 1), temp
					.getData("MR_NO"));
			printDataPQParm
					.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
			printDataPQParm
					.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
			printDataPQParm.addData("BIRTHDATE", temp.getData("BIRTHDATE"));
			printDataPQParm.addData("RX_TYPE_" + (pColumn + 1), temp
					.getData("RX_TYPE"));
			printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), 
					getFreDesc((String)temp.getData("FREQ"))
					);
//			printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), temp
//					.getData("FREQ"));
			printDataPQParm.addData("DOCTOR_" + (pColumn + 1), temp
					.getData("DOCTOR"));
			printDataPQParm.addData("ROUT_" + (pColumn + 1), temp
					.getData("ROUTE"));
			printDataPQParm.addData("PAGE_" + (pColumn + 1), temp
					.getData("PAGE"));
			printDataPQParm.addData("LINK_NO_"+ (pColumn + 1),temp.getValue("LINK_NO"));
			printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1), "药名");
			printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1), "用量");
			printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1), "数量");
			printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "医生:");
			printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "审核:");
			printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1), "执行:");
			printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1), "第");
			printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1), "页");
			int rowOrderCount = temp.getCount("ORDER_DESC");
			for (int j = 0; j < 5; j++) {
				if (j > rowOrderCount - 1) {
					printDataPQParm.addData("ORDER_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					//  增加速率  machao
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					continue;
				}
				printDataPQParm.addData("ORDER_" + (j + 1) + "_"
						+ (pColumn + 1), temp.getData("ORDER_DESC", j));
				printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn + 1),
						numDot(temp.getDouble("QTY", j)) + ""
								+ temp.getData("UNIT_CODE", j));
				printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
						+ (pColumn + 1),
						numDot(temp.getDouble("DOSAGE_QTY", j)) + ""
								+ temp.getData("DOSAGE_UNIT", j));
				
				//  增加速率  machao
				if(numDot(temp.getDouble("RUNTE", j)).length()>0){
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_" + (pColumn + 1),
							numDot(temp.getDouble("RUNTE", j))+"ml/h");
				}else{
					printDataPQParm.addData("RUNTE_" + (j + 1) + "_" + (pColumn + 1),
							numDot(temp.getDouble("RUNTE", j)));
				}
				
			}
			// pColumn++;
			if (pColumn == 3) {
				// pColumn = 0;
				pRow++;
			}
		}
		//System.out.println("111111"+printDataPQParm);
		// printDataPQParm.setCount(pRow+rowNull+1);
		printDataPQParm.setCount(rowCount);
		// --------modify
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
		TParm parmForPrint = new TParm();
		parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
		// System.out.println("======="+parmForPrint);
		// TParm parm = new TParm();
		// TParm prtTableParm = new TParm();
		// prtTableParm.addData("TEST", "222222222");
		// prtTableParm.addData("TEST", "333333333333");
		// prtTableParm.addData("TEST", "122222");
		// prtTableParm.addData("TEST2", "---------");
		// prtTableParm.addData("TEST2", "333333333333");
		// prtTableParm.addData("TEST2", "122222");
		// prtTableParm.setCount(prtTableParm.getCount("TEST"));
		// prtTableParm.addData("SYSTEM", "COLUMNS", "A1");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "A2");
		// prtTableParm.addData("SYSTEM", "COLUMNS", "A3");
		// parm.setData("PRINT_PQ", prtTableParm.getData());
		// System.out.println("****************************"+parm);
		// System.out.println("-=========:"+parmForPrint.getRow(0));
		// luhai 2012-2-13 modify 将瓶签数据拆开，每页都进行打印
		// （宏图层方式存在问题，故取消打印预览，改用反复调用每页数据方式实现）
		TParm pqParm = new TParm((Map) parmForPrint.getData("PRINT_PQ"));
		for (int i = 0; i < pqParm.getCount(); i++) {//连组  输液签与非连组输液签 machao
//			if(!StringUtil.isNullString(pqParm.getValue("LINK_NO_1",i))){
//				//连组
//				printBottleForEach2(pqParm, i);
//			}else{
//				//非连组
//				printBottleForEach(pqParm, i);
//			}
			printBottleForEach2(pqParm, i);
		}
		// luhai 2012-2-13 modify modify 将瓶签数据拆开，每页都进行打印
		// ***************************************************
		// 重新处理打印瓶签方法，将数据整合到一列中进行打印 luhai 2012-2-29 end
		// ***************************************************
	}

	/**
	 * 
	 * 根据瓶签Tparm 打印数据
	 * 
	 * @param pqTParm 
	 *            瓶签TParm
	 * @param index
	 *            打印索引 luhai 2012-3-13
	 */
	private void printBottleForEach(TParm pqTParm, int index) {
		//this.messageBox("3");
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			newPqTParm.addData(key, pqTParm.getValue(key, index));
		}
		//this.messageBox(newPqTParm+""); 
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		openPrintWindow("%ROOT%\\config\\prt\\INW\\INWPrintBottle.jhw",
				printTParm, true);
	}
	
	private void printBottleForEach2(TParm pqTParm, int index) { 
		//this.messageBox("3");
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			if(key.startsWith("RUNTE_")){
				if(!StringUtil.isNullString(pqTParm.getValue(key, index))){
					newPqTParm.addData("RUNTE", pqTParm.getValue(key, index));
				}
			}else{
				newPqTParm.addData(key, pqTParm.getValue(key, index));
			}
		}
		System.out.println("55555555"+newPqTParm);
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
		openPrintWindow("%ROOT%\\config\\prt\\INW\\INWPrintBottle.jhw",
				printTParm, true);
	}
	
	
	
	private String getFreDesc(String code) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT FREQ_CHN_DESC,FREQ_ENG_DESC " + " FROM SYS_PHAFREQ "
						+ " WHERE FREQ_CODE = '" + code + "'"));
		if (parm.getCount() <= 0)
			return "";
		return (parm.getValue("FREQ_CHN_DESC", 0) == null
				|| parm.getValue("FREQ_CHN_DESC", 0).equalsIgnoreCase("null") || parm
				.getValue("FREQ_CHN_DESC", 0).length() == 0) ? code : parm
						.getValue("FREQ_CHN_DESC", 0);
	}
	// 全局数据
	TParm parm = new TParm();
	// 全局单位
	Map phaMap;

	/**
	 * 整理打印数据
	 * 
	 * @return TParm
	 */
	public TParm creatPrintData() {
		TParm result = new TParm();
		Set linkSet = new HashSet();
		Map linkMap = new HashMap();
		int rowCount = parm.getCount("PAT_NAME");
		// 打印多少个瓶签
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (!"".equals(temp.getValue("LINK_NO"))) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM")
						+ temp.getValue("BAR_CODE") + temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		Iterator linkIterator = linkSet.iterator();
		// 每个瓶签的基本信息
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				// if(tempLinkStr.equals(tempParm.getValue("MR_NO")+tempParm.getValue("LINK_NO")+tempParm.getValue("START_DTTM")+tempParm.getValue("SEQ_NO")+
				// tempParm.getValue("ORDER_NO"))){
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& !"".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("LINK_NO", tempParm.getValue("LINK_NO"));
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					temp.setData("BIRTHDATE", tempParm.getValue("BIRTHDATE"));
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					String desc[] = breakNFixRow(orderDesc, 35, 1);
					for (int k = 0; k < desc.length; k++) {
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// 用量
							temp.addData("QTY", tempParm.getValue("QTY"));
							// 单位
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// 总量
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// 总量单位
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// 连接主
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							// 速率   machao
							temp.addData("RUNTE", tempParm
									.getValue("RUNTE"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// 用量
						temp.addData("QTY", "");
						// 单位
						temp.addData("UNIT_CODE", "");
						// 总量
						temp.addData("DOSAGE_QTY", "");
						// 总量单位
						temp.addData("DOSAGE_UNIT", "");
						// 连接主
						temp.addData("LINK_MAIN_FLG", "");
						// 速率   machao
						temp.addData("RUNTE", "");
					}
				}
			}
			linkMap.put(tempLinkStr, temp);
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		Set onlySet = new HashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		TParm resultTemp = new TParm();
		Iterator iter = onlySet.iterator();
		while (iter.hasNext()) {
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// 处理非连接医嘱的针剂也显示出来begin
		// **************************************************************************************************
		result = new TParm();
		linkSet = new HashSet();
		linkMap = new HashMap();
		rowCount = parm.getCount("PAT_NAME");
		// 打印多少个瓶签
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			boolean classifyFlg = "F".equals(temp.getValue("CLASSIFY_TYPE"))
					|| "I".equals(temp.getValue("CLASSIFY_TYPE"));
			if ("".equals(temp.getValue("LINK_NO")) && (classifyFlg)) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM")
						+ temp.getValue("BAR_CODE") + temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		linkIterator = linkSet.iterator();
		// 每个瓶签的基本信息
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& "".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("LINK_NO", tempParm.getValue("LINK_NO"));
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					temp.setData("BIRTHDATE", tempParm.getValue("BIRTHDATE"));
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					String desc[] = breakNFixRow(orderDesc, 28, 1);
					for (int k = 0; k < desc.length; k++) {
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// 用量
							temp.addData("QTY", tempParm.getValue("QTY"));
							// 单位
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// 总量
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// 总量单位
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// 连接主
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							// 速率   machao
							temp.addData("RUNTE", tempParm
									.getValue("RUNTE"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// 用量
						temp.addData("QTY", "");
						// 单位
						temp.addData("UNIT_CODE", "");
						// 总量
						temp.addData("DOSAGE_QTY", "");
						// 总量单位
						temp.addData("DOSAGE_UNIT", "");
						// 连接主
						temp.addData("LINK_MAIN_FLG", "");
						// 速率  machao
						temp.addData("RUNTE", "");
					}
				}
			}
			linkMap.put(tempLinkStr, temp);
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		onlySet = new HashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		// TParm resultTemp = new TParm();
		iter = onlySet.iterator();
		while (iter.hasNext()) {
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// 处理非连接医嘱的针剂也显示出来end
		// **************************************************************************************************

		return configParm(resultTemp);
	}

	/**
	 * 初始化页面打印数据 luhai 2012-2-28
	 * 
	 * @param parm
	 *            Vector
	 * @return TParm
	 */
	public TParm initPageData(Vector parm) {
		TParm result = new TParm();
		int rowCount = ((Vector) parm.get(0)).size();
		for (int i = 0; i < rowCount; i++) {
			// if(((Vector)parm.get(4)).get(i)!=null&&((Vector)parm.get(4)).get(i).toString().trim().length()!=0&&!"null".equals(((Vector)parm.get(4)).get(i))){
			// 频次
			// luhai 2012-2-29 modify 处理频次为空的情况 begin
			String freqCode = "";
			if (((Vector) (parm.get(12))).get(i) == null) {
				freqCode = "STAT";
			} else {
				freqCode = ((Vector) parm.get(12)).get(i).toString();
			}
			// luhai 2012-2-29 modify 处理频次为空的情况 end
			// add
			// modify
			TParm freqParm = new TParm(this.getDBTool().select(
					"SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
							+ freqCode + "'"));
			int countFreq = freqParm.getInt("FREQ_TIMES", 0);
			// luhai 2012-3-1 加入执行次数的计算，之前逻辑根据频次计算数量，不计算执行天数，现在需要每次执行都打印瓶签 begin
			String caseNo = ((Vector) parm.get(22)).get(i) + "";
			String orderNo = ((Vector) parm.get(9)).get(i) + "";
			String orderSeq = ((Vector) parm.get(10)).get(i) + "";
			String startDttm = ((Vector) parm.get(11)).get(i) + "";
			String endDttm = ((Vector) parm.get(23)).get(i) + "";
			// 查询细项的SQL
			String sql = " SELECT "
					+ " BAR_CODE,ORDER_DATE,ORDER_DATETIME,DOSAGE_QTY,DOSAGE_UNIT "// modify
					// by
					// wanglong
					// 20140402
					+ " FROM ODI_DSPND  WHERE CASE_NO='"
					+ caseNo
					+ "' AND ORDER_NO='"
					+ orderNo
					+ "' AND ORDER_SEQ='"
					+ orderSeq
					+ "' "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
					+ startDttm
					+ "','YYYYMMDDHH24MISS') "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
					+ endDttm + "','YYYYMMDDHH24MISS')"
					+ " AND DC_DATE IS NULL  "// SHIBL 20130301 MODIFY
					+ " ORDER BY ORDER_DATE||ORDER_DATETIME ";
			// System.out.println("sql=========="+sql);
			TParm resultDspnCnt = new TParm(TJDODBTool.getInstance()
					.select(sql));
			int totCount = resultDspnCnt.getCount();
			String barCode = "";
			// System.out.println("BAR_CODE-------"+barCode);
			// if (countFreq > 1) {
			// // totCount = totCount * countFreq;
			// // luhai 2012-4-1 瓶签打印次数调整 begin
			// totCount = totCount;
			// // luhai 2012-4-1 瓶签打印次数调整 begin
			// }
			// this.messageBox(totCount+"");
			// int tot_time=
			// luhai 2012-3-1 加入执行次数的计算，之前逻辑根据频次计算数量，不计算执行天数，现在需要每次执行都打印瓶签 end
			// 大于1次
			int seqNo = 1;
			// // if(countFreq<=1){
			// if (totCount <= 1) {
			// result.addData("BED_NO", ((Vector) parm.get(0)).get(i));
			// result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
			// result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
			// result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
			// result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
			// result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
			// result.addData("QTY", ((Vector) parm.get(6)).get(i));
			// result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
			// result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
			// result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
			// result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
			// result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
			// result.addData("SEQ_NO", 0);
			// result.addData("FREQ", ((Vector) parm.get(12)).get(i));
			// result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
			// result.addData("SEX", ((Vector) parm.get(14)).get(i));
			// result.addData("AGE", ((Vector) parm.get(15)).get(i));
			// result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
			// result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
			// result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
			// result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
			// result.addData("DOSAGE_UNIT", ((Vector) parm.get(20)).get(i));
			// // 加入剂型
			// result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
			// // 加入case_no
			// result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
			// // 加入END_DTTM
			// result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
			// // barCode
			// result.addData("BAR_CODE", barCode);
			//
			// } else {
			// for(int j=0;j<countFreq;j++){
			for (int j = 0; j < totCount; j++) {
				result.addData("BED_NO", ((Vector) parm.get(0)).get(i));
				result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
				result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
				result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
				result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
				result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
				result.addData("QTY", ((Vector) parm.get(6)).get(i));
				result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
				result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
				result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
				result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
				result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
				result.addData("SEQ_NO", seqNo);

				result.addData("FREQ", ((Vector) parm.get(12)).get(i));
				result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
				result.addData("SEX", ((Vector) parm.get(14)).get(i));
				result.addData("AGE", ((Vector) parm.get(15)).get(i));
				result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
				result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
				result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
				// result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
				// result.addData("DOSAGE_UNIT", ((Vector)
				// parm.get(20)).get(i));
				result.addData("DOSAGE_QTY", resultDspnCnt.getDouble(
						"DOSAGE_QTY", j));// modify by wanglong 20140402
				result.addData("DOSAGE_UNIT", resultDspnCnt.getValue(
						"DOSAGE_UNIT", j));
				// 加入剂型
				result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
				// 加入case_no
				result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
				// 加入END_DTTM
				result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
				// 加入速率   machao
				result.addData("RUNTE", ((Vector) parm.get(24)).get(i));
				// 出生日期
				result.addData("BIRTHDATE", ((Vector) parm.get(25)).get(i));
				// shibl 20120415 add
				barCode = resultDspnCnt.getValue("BAR_CODE", j);
				// barCode
				result.addData("BAR_CODE", barCode);
				result.addData("ORDER_DATE", resultDspnCnt.getValue(
						"ORDER_DATE", j));
				result.addData("ORDER_DATETIME", resultDspnCnt.getValue(
						"ORDER_DATETIME", j));
				seqNo++;
			}
		}
		return result;
	}

	public String getbedDesc(String bedNo) {
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT A.BED_NO,A.BED_NO_DESC FROM SYS_BED A,ADM_INP B WHERE A.BED_NO=B.BED_NO AND BED_NO = '"
										+ bedNo + "'"));
		return parm.getValue("BED_NO_DESC", 0);
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	private TParm configParm(TParm parm) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("PRINT_DATAPQ"); i++) {
			TParm parmI = (TParm) parm.getData("PRINT_DATAPQ", i);
			;
			int rowCount = parmI.getCount("ORDER_DESC");
			int pageCount = 1;
			if (rowCount % 5 == 0)
				pageCount = rowCount / 5;
			else
				pageCount = rowCount / 5 + 1;
			int page = 1;
			for (int j = 0; j < rowCount; j++) {
				if ((j + 1) % 5 == 0) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, j - 4, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				} else if ((j + 1) == rowCount) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, rowCount
							- rowCount % 5, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				}
			}
		}
		return result;
	}

	private TParm cloneParm(TParm parm, int startIndex, int endIndex) {
		TParm result = new TParm();
		String[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (parm.getData(names[i]) instanceof String)
				result.setData(names[i], parm.getData(names[i]));
			else if (parm.getData(names[i]) instanceof Vector) {
				for (int j = startIndex; j <= endIndex; j++)
					result.addData(names[i], parm.getData(names[i], j));
			}
		}
		return result;
	}

	// ====================luahi modify 2012-2-28 end
	// ================================
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE = '" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	public String getRouteDesc(String routeCode) {
		if (routeCode.length() == 0)
			return "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE "
						+ " WHERE ROUTE_CODE = '" + routeCode + "'"));
		return parm.getValue("ROUTE_CHN_DESC", 0);
	}

	public String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID = '" + userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * 取得单位字典
	 * 
	 * @return Map
	 */
	public Map getUnitMap() {
		Map map = new HashMap();
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT"));
		for (int i = 0; i < parm.getCount(); i++) {
			map.put(parm.getData("UNIT_CODE", i), parm.getData("UNIT_CHN_DESC",
					i));
		}
		return map;
	}

	public String[] breakNFixRow(String src, int bre, int fix) {
		return fixRow(breakRow(src, bre), fix);
	}

	public String[] fixRow(String string, int size) {
		Vector splitVector = new Vector();
		int index = 0;
		int separatorCount = 0;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ("\n".equals(String.valueOf(c))) {
				if (++separatorCount >= size) {
					splitVector.add(string.substring(index, i));
					index = i + 1;
					separatorCount = 0;
				}
			}
		}

		splitVector.add(string.substring(index, string.length()));
		String splitArray[] = new String[splitVector.size()];
		for (int j = 0; j < splitVector.size(); j++)
			splitArray[j] = (String) splitVector.get(j);

		return splitArray;
	}

	public String breakRow(String src, int size) {
		return breakRow(src, size, 0);
	}

	public String breakRow(String src, int size, int shift) {
		StringBuffer tmp = new StringBuffer("");
		tmp.append(space(shift));
		int i = 0;
		int len = 0;
		for (; i < src.length(); i++) {
			char c = src.charAt(i);
			len += getCharSize(c);
			if ("\n".equals(String.valueOf(c))) {
				tmp.append(c);
				tmp.append(space(shift));
				len = 0;
			} else if (size >= len) {
				tmp.append(c);
			} else {
				tmp.append("\n");
				tmp.append(space(shift));
				tmp.append(c);
				len = getCharSize(c);
			}
		}

		return tmp.toString();
	}

	public int getCharSize(char c) {
		return (new String(new char[] { c })).getBytes().length;
	}

	public String space(int n) {
		StringBuffer tmp = new StringBuffer("");
		for (int i = 0; i < n; i++)
			tmp.append(' ');

		return tmp.toString();
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\INWOrderExecMain.x");
	}

	/**
	 * 护士站执行查看医嘱 ===========pangben 2011-11-14
	 */
	public void OrderExecStuts() {
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT A.CASE_NO,B.MR_NO,C.PAT_NAME,B.IPD_NO, A.NS_EXEC_DATE,ADM_DATE  FROM ODI_DSPND A ,ODI_ORDER B,SYS_PATINFO C,ADM_INP D WHERE    A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO=D.CASE_NO(+) AND B.MR_NO=C.MR_NO AND (NS_EXEC_DATE IS NULL OR NS_EXEC_DATE='') ");
		if (null != caseNo && caseNo.length() > 0)
			sql.append(" AND A.CASE_NO ='").append(caseNo).append("'")
					.toString();
		sql
				.append(
						" GROUP BY B.MR_NO, C.PAT_NAME, B.IPD_NO, A.NS_EXEC_DATE, ADM_DATE,A.CASE_NO")
				.toString();
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount() <= 0) {
			messageBox("没有需要执行的医嘱信息");
			return;
		}
		TParm parmValue = new TParm();
		String stationSQL = null;
		for (int i = 0; i < result.getCount(); i++) {
			stationSQL = "SELECT STATION_CODE FROM ODI_ORDER WHERE CASE_NO='"
					+ result.getValue("CASE_NO", 0) + "'";
			parmValue = new TParm(TJDODBTool.getInstance().select(stationSQL));
			result.addData("STATION_CODE", parmValue
					.getValue("STATION_CODE", 0));
		}

		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// START==================$$//
		/**
		 * client1 = SocketLink.running("", "INWSTATION", "inw"); if
		 * (client1.isClose()) { out(client1.getErrText()); return; }
		 **/
		StringBuffer message = new StringBuffer();
		String mess = null;
		for (int i = 0; i < result.getCount(); i++) {
			// add by lx
			client1 = SocketLink.running("",
					result.getValue("STATION_CODE", i), result.getValue(
							"STATION_CODE", i));

			if (client1.isClose()) {
				out(client1.getErrText());
				return;
			}
			String admDate = StringTool.getString(result.getTimestamp(
					"ADM_DATE", i), "yyyy/MM/dd HH:mm:ss");
			if (result.getCount() > 1) {
				message.append("CASE_NO:")
						.append(result.getValue("CASE_NO", i)).append(
								"|STATION_CODE:").append(
								result.getValue("STATION_CODE", i)).append(
								"|MR_NO:").append(result.getValue("MR_NO", i))
						.append("|PAT_NAME:").append(
								result.getValue("PAT_NAME", i)).append(
								"|IPD_NO:")
						.append(result.getValue("IPD_NO", i)).append(
								"|ADM_DATE:").append(admDate).append("|")
						.toString();
				if (result.getCount() - 1 == i)
					mess = message.toString().substring(0, message.length())
							.toString();
			} else {
				mess = message.append(message).append("CASE_NO:").append(
						result.getValue("CASE_NO", i)).append("|STATION_CODE:")
						.append(result.getValue("STATION_CODE", i)).append(
								"|MR_NO:").append(result.getValue("MR_NO", i))
						.append("|PAT_NAME:").append(
								result.getValue("PAT_NAME", i)).append(
								"|IPD_NO:")
						.append(result.getValue("IPD_NO", i)).append(
								"|ADM_DATE:").append(admDate).toString();
			}

			client1.sendMessage(result.getValue("STATION_CODE", i), mess);
			if (client1 == null) {
				return;
			} else {
				client1.close();
				return;
			}
		}
		/**
		 * client1.sendMessage("INWSTATION", mess); if (client1 == null) {
		 * return; } else { client1.close(); return; }
		 **/
		// $$ ============ Modified by lx 医生按部门,护士按病区收发消息2012/02/27
		// END==================$$//
	}

	/**
	 * 打印多病人医嘱执行单
	 */
	public void onPrintExe() {
		if (this.ord1All.isSelected() || this.ord2All.isSelected()
				|| checkAll.isSelected()) {
			this.messageBox("不能在全部状态下打印！");
			return;
		}
		String orderKind = "";
		if (ord1ST.isSelected()) {
			orderKind = "临时";
		} else if (ord1UD.isSelected()) {
			orderKind = "长期";
		} else if (ord1DS.isSelected()) {
			orderKind = "出院带药";
		} else if (ord1IG.isSelected()) {
			orderKind = "中药饮片";
		}
		String orderType = "";
		if (ord2PHA.isSelected()) {
			orderType = "药嘱";
		} else if (ord2PL.isSelected()) {
			orderType = "检验检查";
		} else if (ord2ENT.isSelected()) {
			orderType = "治疗";
		}
		String orderExe = "";
		if (this.checkYES.isSelected()) {
			orderExe = "已执行";
		} else if (checkNO.isSelected()) {
			orderExe = "未执行";
		}
		TParm tableDate = masterTbl.getParmValue();
		String caseNo = "", orderNo = "", orderSeq = "", startDttm = "", endDttm = "";
		// 通过CASE_NO，ORDER_NO，ORDER_SEQ在ODI_DSPND中定位多条细项
		Map map = new HashMap();
		TParm printData = new TParm();
		int count = 0;
		for (int i = 0; i < tableDate.getCount(); i++) {
			boolean prtFlg = TypeTool.getBoolean(masterTbl.getValueAt(i, 4)); //列位置调整14改4
			// modify
			// 13改为14
			// 20131122
			if (!prtFlg)
				continue;
			if (map.get(tableDate.getValue("CASE_NO", i)) != null) {
				printData.addData("BED_NO", "");
				// printData.addData("MR_NO", "");
				printData.addData("PAT_NAME", "");
				printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
			} else if (map.get(tableDate.getValue("CASE_NO", i)) == null
					&& i == 0) {
				printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
				printData.addData("BED_NO", tableDate.getValue("BED_NO", i)); // =========
				// chenxi
				// modify
				// 20130408
				printData
						.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			} else {
				printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
				printData.addData("BED_NO", " ");
				// printData.addData("MR_NO", "病案号");
				printData.addData("PAT_NAME", " ");
				printData.addData("LINK_NO", " ");
				printData.addData("ORDER_DESC", " ");
				// printData.addData("ORDER_TIME", " ");
				printData.addData("MEDI_QTY", " ");
				printData.addData("MEDI_UNIT", " ");
				printData.addData("FREQ_CODE", "");
				printData.addData("ROUTE_CODE", " ");
				printData.addData("INFLUTION_RATE", " ");//加入速率 machao
				printData.addData("DC_DATE", " ");
				printData.addData("NS_EXEC_DATE", " ");
				printData.addData("DR_NOTE", " ");
				printData.addData("NS_EXEC_CODE", " ");
				count++;
				printData.addData("CASE_NO", tableDate.getValue("CASE_NO", i));
				printData.addData("BED_NO", this.getNewbedDesc(tableDate
						.getValue("CASE_NO", i)));
				// printData.addData("MR_NO", tableDate.getValue("MR_NO", i));
				printData
						.addData("PAT_NAME", tableDate.getValue("PAT_NAME", i));
			}
			map.put(tableDate.getValue("CASE_NO", i), tableDate.getValue(
					"CASE_NO", i));
			printData.addData("LINK_NO", tableDate.getValue("LINK_NO", i));
			printData.addData("ORDER_DESC", tableDate.getValue(
					"ORDER_DESC_AND_SPECIFICATION", i));
			// printData.addData("ORDER_TIME", tableDate.getValue("START_DTTM",
			// i)
			// .substring(4, 6)
			// + "/"
			// + tableDate.getValue("START_DTTM", i).substring(6, 8)
			// + " "
			// + tableDate.getValue("START_DTTM", i).substring(8, 10)
			// + ":"
			// + tableDate.getValue("START_DTTM", i).substring(10, 12)
			// + " "
			// + tableDate.getValue("END_DTTM", i).substring(4, 6)
			// + "/"
			// + tableDate.getValue("END_DTTM", i).substring(6, 8)
			// + " "
			// + tableDate.getValue("END_DTTM", i).substring(8, 10)
			// + ":"
			// + tableDate.getValue("END_DTTM", i).substring(10, 12));
			printData.addData("MEDI_QTY", tableDate.getValue("MEDI_QTY", i));
			printData.addData("MEDI_UNIT", getUnit(tableDate.getValue(
					"MEDI_UNIT", i)));
			printData.addData("FREQ_CODE", getFreqData(
					tableDate.getValue("FREQ_CODE", i)).getValue(
					"FREQ_CHN_DESC", 0));
//			printData.addData("FREQ_CODE", 
//					tableDate.getValue("FREQ_CODE", i));//20150202 wangjingchun add
			printData.addData("ROUTE_CODE", OrderUtil.getInstance().getRoute(
					tableDate.getValue("ROUTE_CODE", i)));
			//加入速率  machao
			printData.addData("INFLUTION_RATE", tableDate.getValue("INFLUTION_RATE", i));
			printData.addData("DC_DATE", tableDate.getValue("DC_DATE", i)
					.equals("") ? " " : tableDate.getValue("DC_DATE", i)
					.replaceAll("-", "/").substring(5, 16));
			printData.addData("NS_EXEC_DATE", tableDate.getValue(
					"NS_EXEC_DATE", i).equals("") ? " "
					: tableDate.getValue("NS_EXEC_DATE", i)
							.replaceAll("-", "/").substring(5, 10)
							+ " "
							+ (tableDate.getValue("NS_EXEC_DATE_TIME", i)
									.equals("") ? " " : tableDate.getValue(
									"NS_EXEC_DATE_TIME", i).substring(0, 5)));
			printData.addData("DR_NOTE", tableDate.getValue("DR_NOTE", i));
			printData.addData("NS_EXEC_CODE", getOperatorName(tableDate
					.getValue("NS_EXEC_CODE", i)));
			count++;
		}
		printData.setCount(count);
		TParm GprintParm = new TParm();
		if (count <= 0) {
			this.messageBox("无打印数据！");
			return;
		} else {
			Map patMap = new HashMap();
			Map pat = groupByPatParm(printData);
			// Iterator it = pat.values().iterator();
			for (int j = 0; j < tableDate.getCount(); j++) {
				boolean prtFlg = TypeTool.getBoolean(masterTbl
						.getValueAt(j, 3)); //列位置调整13改3
				if (!prtFlg)
					continue;
				if (patMap.get(tableDate.getValue("CASE_NO", j)) == null) {
					if (pat.get(tableDate.getValue("CASE_NO", j)) != null) {
						TParm patParm = (TParm) pat.get(tableDate.getValue(
								"CASE_NO", j));
						int rows = patParm.getCount();
						for (int i = 0; i < rows; i++) {
							GprintParm.addData("BED_NO", patParm.getValue(
									"BED_NO", i));
							GprintParm.addData("PAT_NAME", patParm.getValue(
									"PAT_NAME", i));
							GprintParm.addData("LINK_NO", patParm.getValue(
									"LINK_NO", i));
							GprintParm.addData("ORDER_DESC", patParm.getValue(
									"ORDER_DESC", i));
							// GprintParm.addData("ORDER_TIME",
							// patParm.getValue(
							// "ORDER_TIME", i));
							GprintParm.addData("MEDI_QTY", patParm.getValue(
									"MEDI_QTY", i));
							GprintParm.addData("MEDI_UNIT", patParm.getValue(
									"MEDI_UNIT", i));
							GprintParm.addData("FREQ_CODE", patParm.getValue(
									"FREQ_CODE", i));
							GprintParm.addData("ROUTE_CODE", patParm.getValue(
									"ROUTE_CODE", i));
							//增加速率  machao
							GprintParm.addData("INFLUTION_RATE", patParm
									.getValue("INFLUTION_RATE", i));
							GprintParm.addData("NS_EXEC_DATE", patParm
									.getValue("NS_EXEC_DATE", i));
							GprintParm.addData("DC_DATE", patParm.getValue(
									"DC_DATE", i));
							GprintParm.addData("DR_NOTE", patParm.getValue(
									"DR_NOTE", i));
							GprintParm.addData("NS_EXEC_CODE", patParm
									.getValue("NS_EXEC_CODE", i));
//							GprintParm.addData("NS_EXEC_CODE", printData
//									.getValue("NS_EXEC_CODE", i));
						}
					}
				}
				patMap.put(tableDate.getValue("CASE_NO", j), tableDate
						.getValue("CASE_NO", j));
			}
		}
		GprintParm.setCount(count);
		GprintParm.addData("SYSTEM", "COLUMNS", "BED_NO");
		// printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		GprintParm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		// GprintParm.addData("SYSTEM", "COLUMNS", "ORDER_TIME");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		GprintParm.addData("SYSTEM", "COLUMNS", "MEDI_UNIT");
		GprintParm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		GprintParm.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");//速率  machao
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
		GprintParm.addData("SYSTEM", "COLUMNS", "DC_DATE");
		GprintParm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		GprintParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
		
		TParm printParm = new TParm();
		printParm.setData("TITLE", "TEXT", "医嘱执行单");
		printParm.setData("ORDER_KIND", "TEXT", "医嘱类别:" + orderKind);
		printParm.setData("ORDER_TYPE", "TEXT", "医嘱种类:" + orderType);
		printParm.setData("ORDER_EXE", "TEXT", "执行确认:" + orderExe);
		printParm.setData("STATION_CODE", "TEXT", "病区："
				+ getStationDesc(this.getValueString("INW_STATION_CODE")));
		printParm.setData("DATE", "TEXT", "执行时间:"
				+ this.getValueString("from_Date").replaceAll("-", "/")
						.substring(0, 10)
				+ " "
				+ this.getValueString("from_Time")
				+ "至"
				+ this.getValueString("to_Date").replaceAll("-", "/")
						.substring(0, 10) + " "
				+ this.getValueString("to_Time"));
		printParm.setData("PRINT_DATE", "TEXT", "打印时间:"
				+ StringTool.getString(SystemTool.getInstance().getDate(),
						"yyyy/MM/dd HH:mm:ss"));
		System.out.println("aaaaaa"+GprintParm.getData());
		printParm.setData("TABLE", GprintParm.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\inw\\inwExeNewPrint.jhw",
				printParm);
	}

	/**
	 * 将按病患分组
	 * 
	 * @param parm
	 * @return
	 */
	public Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("CASE_NO", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * 取得频次数据
	 * 
	 * @param freqCode
	 * @return
	 */
	public TParm getFreqData(String freqCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION "
						+ " FROM SYS_PHAFREQ " + " WHERE FREQ_CODE='"
						+ freqCode + "'"));
		return parm;
	}

	/**
	 * 检验报告
	 */
	public void onCheckrep() {
		String mrNo = "";
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			mrNo = this.getValueString("MR_NO");
		} else if (selType == 1) {
			mrNo = this.getValueString("MR_NOOUT");
		}
		if (mrNo.equals(""))
			return;
		SystemTool.getInstance().OpenLisWeb(mrNo);
	}

	/**
	 * 检查报告
	 */
	public void onTestrep() {
		String mrNo = "";
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			mrNo = this.getValueString("MR_NO");
		} else if (selType == 1) {
			mrNo = this.getValueString("MR_NOOUT");
		}
		if (mrNo.equals(""))
			return;
		SystemTool.getInstance().OpenRisWeb(mrNo);
	}

	boolean sortClicked = false;

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = masterTbl.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = masterTbl.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);
				setColor();
				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
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
		masterTbl.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 生成配液条码
	 */
	public void GeneratPhaBarcode() {
		TParm dspndParm = new TParm();
		TParm tablValue = masterTbl.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = masterTbl.getRowCount();
		int count = 0;
		TParm linkparm = new TParm();
		Map mapBarCode = new HashMap();
		Map linkmap = new HashMap();
		String caseNo = "";
		String orderNo = "";
		String orderSeq = "";
		String cat1Type = "";
		String orderCode = "";
		String startDttm = "";
		String endDttm = "";
		String orderDesc = "";
		String Dosetype = "";
		String barCode = "";
		String linkNo = "";
		String dspnKind = "";
		String linkStr = "";
		String dcFlg = "";
		String routeCode = "";
		// 主表的数据
		for (int i = 0; i < rowCount; i++) {
			caseNo = (String) tablValue.getData("CASE_NO", i);
			orderNo = (String) tablValue.getData("ORDER_NO", i);
			orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
			orderCode = (String) tablValue.getData("ORDER_CODE", i);
			startDttm = (String) tablValue.getData("START_DTTM", i);
			endDttm = (String) tablValue.getData("END_DTTM", i);
			orderDesc = (String) tablValue.getData(
					"ORDER_DESC_AND_SPECIFICATION", i);
			Dosetype = "";
			linkNo = tablValue.getValue("LINK_NO", i);
			dspnKind = (String) tablValue.getData("DSPN_KIND", i);
			routeCode = (String) tablValue.getData("ROUTE_CODE", i);
			if (TypeTool.getBoolean(masterTbl.getValueAt(i, 3))) { //列位置调整13改3
				// modify
				// 12改为13
				// 20131122
				if (cat1Type.equals("PHA")) {
					if (routeCode.equals("")) {
						this.messageBox(orderDesc + "用法为空，不能生成条码！");
						return;
					}
					Dosetype = SysPhaBarTool.getInstance().getClassifyType(
							routeCode);
					if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
						this.messageBox(orderDesc + "不是针剂或点滴，不能生成条码！");
						return;
					}
					// String checksql = "SELECT BAR_CODE FROM ODI_DSPND "
					// + "WHERE CASE_NO='"
					// + caseNo
					// + "' AND ORDER_NO='"
					// + orderNo
					// + "' AND ORDER_SEQ='"
					// + orderSeq
					// + "' "
					// +
					// " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
					// + startDttm
					// + "','YYYYMMDDHH24MISS') "
					// +
					// " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
					// + endDttm
					// + "','YYYYMMDDHH24MISS') AND BAR_CODE IS NOT NULL ";
					// TParm check = new TParm(TJDODBTool.getInstance()
					// .select(checksql));
					// //如果数据存在，询问是否导入
					// if (check.getCount("BAR_CODE") > 0) {
					// continue;
					// // switch (this.messageBox("提示信息",
					// // orderDesc+"数据条码已存在，是否重新生成？", this.YES_NO_OPTION)) {
					// // case 0: //生成
					// // break;
					// // case 1: //不生成
					// // continue;
					// // }
					// }
					// 判断连接医嘱（一组一码）
					if (!linkNo.equals("")) {
						linkStr = caseNo + orderNo + dspnKind + startDttm
								+ linkNo;
						if (linkmap.get(linkStr) == null) {
							// 取号
							barCode = SysPhaBarTool.getInstance().getBarCode();
							mapBarCode.put(linkStr, barCode);
						}
						linkmap.put(linkStr, linkStr);
						// 查询细项的SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";
						// 更新细表的TDS,更改其数据
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("DC_DATE", j).equals(""))
								continue;
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", (String) mapBarCode
									.get(linkStr)
									+ j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					} else {
						// 取号
						barCode = SysPhaBarTool.getInstance().getBarCode();
						// 查询细项的SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";

						// 更新细表的TDS,更改其数据
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("DC_DATE", j).equals(""))
								continue;
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", barCode + j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					}
				}
			}
		}
		dspndParm.setCount(count);
		if (count > 0) {
			TParm result = InwOrderExecTool.getInstance().GeneratIFBarcode(
					dspndParm);
			if (result.getErrCode() < 0) {
				this.messageBox("生成条码失败！");
				return;
			}
			this.messageBox("生成条码成功！");
		} else {
			this.messageBox("无需生成条码的针剂或点滴数据");
		}
	}

	/**
     * 
     * 
     */
	public String getNewbedDesc(String caseNo) {
		String bed = "";
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE CASE_NO = '"
						+ caseNo + "'"));
		if (parm.getCount() > 0) {
			bed = parm.getValue("BED_NO_DESC", 0);
		}
		return bed;
	}

	/**
	 * 频次筛选
	 */
	public void FreqCodeSelect() {
		masterTbl.acceptText();
		TParm parm = masterTbl.getParmValue();
		String freqCode = this.getValueString("FREQ_CODETAG");
		// 主表的数据
		for (int i = parm.getCount("CASE_NO") - 1; i >= 0; i--)
			if (!freqCode.equals(parm.getValue("FREQ_CODE", i)))
				parm.removeRow(i);
		masterTbl.setParmValue(parm);
	}

	/**
	 * 检查待DC医嘱的数量不能超过用户开立它的数量
	 * 
	 * @param parm
	 * @return
	 */
	public TParm checkDCQtyIsLess(TParm parm) {// add by wanglong 20130527
		String bilPoint = (String) OdiMainTool.getInstance().getOdiSysParmData(
				"BIL_POINT");// add by wanglong 20140123
		String sumCountSql = "SELECT SUM(DOSAGE_QTY) COUNT FROM IBS_ORDD WHERE CASE_NO = '#' AND ORDER_CODE = '#' GROUP BY CASE_NO, ORDER_CODE";
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			if (!parm.getValue("ORDER_CODE", i).equals(
					parm.getValue("ORDERSET_CODE", i))) {
				continue;
			}
			if (parm.getValue("CAT1_TYPE", i).equals("PHA")) {// add by wanglong
				// 20130619
				continue;
			}
			if (bilPoint.equals("2")
					&& (parm.getValue("CAT1_TYPE", i).equals("RIS") || parm
							.getValue("CAT1_TYPE", i).equals("LIS"))) {// add by
				// wanglong
				// 20140123
				continue;
			}
			String caseNo = parm.getValue("CASE_NO", i);
			String orderCode = parm.getValue("ORDER_CODE", i);
			String orderNo = parm.getValue("ORDER_NO", i);
			String startDttm = parm.getValue("START_DTTM", i);
			String sql = sumCountSql.replaceFirst("#", caseNo).replaceFirst(
					"#", orderCode);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getErrCode() != 0) {
				this.messageBox("查询待DC医嘱被病患使用过的总数量失败");
				return null;
			}
			if (result.getInt("COUNT", 0) < parm.getInt("DOSAGE_QTY", i)) {
				String patName = PatTool.getInstance().getNameForMrno(
						parm.getValue("MR_NO", i));
				this.messageBox(parm.getValue("ORDER_DESC", i) + "（"
						+ parm.getValue("ORDER_CODE", i) + "）取消执行的数量大于病患（"
						+ patName + "）执行过的总数量，当前操作将被取消");
				return null;
			}
		}
		return parm;
	}

	/**
	 * 保存改变的医嘱 add caoyong 20131108
	 */
	public void onSaveT() {
		boolean flag = false;
		if(null!=lumpworkCode && lumpworkCode.length()>0){
			//套餐患者校验是否修改过套餐
			TParm result= ADMInpTool.getInstance().checkLumpWorkisUpdate(caseNo, lumpworkCode);
			if(result.getErrCode()<0){
				this.messageBox(result.getErrText());
				return ;
			}
		}
		if (replenishTbl.getRowCount() > 0) {
			for (int i = 0; i < replenishTbl.getRowCount(); i++) {

				if (replenishTbl.getItemString(i, "ORDER_CODE").length() > 0) {
					flag = true;
					break;
				}
			}
		}
		if (flag) {

			// System.out.println("===="+addParm());
			/*
			 * TParm checkParm = checkOrderSave(); if (checkParm.getErrCode() <
			 * 0) { this.messageBox(checkParm.getErrText()); return ; }
			 */
			getInsetDetail(addParm());// 保存新增医嘱

			// onTABLEClicked(this.getRow());
			// replenishTbl.removeRowAll();
		} else {
			this.messageBox("没有要保存的数据");
		}

	}

	/**
	 * 护士套餐 add caoyong 20131108
	 */
	public void onNuese() {
		// 护士套餐
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 4);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);
	}

	/**
	 * 科室套餐 add caoyong 20131108
	 */
	public void onDept() {
		// 科室套餐
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "IBS");
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("USER_ID", Operator.getID());
		parm.setData("DEPT_OR_DR", 3);
		parm.setData("RULE_TYPE", 4);
		parm.addListener("INSERT_TABLE", this, "onQuoteSheetList");
		TWindow window = (TWindow) this.openDialog(
				"%ROOT%\\config\\odi\\ODIPACKOrderUI.x", parm, true);
		window.setVisible(true);

	}

	/**
	 * 套餐赋值
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheetList(Object obj) {
		boolean falg = true;
		if (obj != null) {
			List orderList = (ArrayList) obj;
			Iterator iter = orderList.iterator();
			while (iter.hasNext()) {
				TParm temp = (TParm) iter.next();
				// System.out.println("套餐参数"+temp);
				//IBSOrderControl order = new IBSOrderControl();
				insertNewOperationOrder(temp, temp.getDouble("MEDI_QTY"));
			}
		}
		return falg;
	}

	/**
	 * add caoyong 20131108 监听replenishTable
	 * 
	 * @param row
	 */
	public void onIsertTable(int row) {

		callFunction("UI|skiResult|setEnabled", true);// yanjing 20131107 皮试结果按钮

		// replenishTbl.setParmValue(new TParm());
		// this.setRow(row);

		TParm tparm = masterTbl.getParmValue().getRow(row);
		// clpCode_ = outsideParm.getValue("INW", "CLNCPATH_CODE");
		// String vsDrCode= tparm.getValue("VS_DR_CODE");
		// String station= tparm.getValue("STATION_CODE");
		String orderNo = tparm.getValue("ORDER_NO");
		String caseNo = tparm.getValue("CASE_NO");
		String orderCode = tparm.getValue("ORDER_CODE");
		int seq = tparm.getInt("ORDER_SEQ");
		setOrderNo(orderNo);
		setCaseNO(caseNo);
		this.setOrderSeq(seq);

		replenishTbl.removeRowAll();
		/*
		 * String
		 * sql="SELECT ORDER_CHN_DESC,MEDI_QTY,MEDI_UNIT,FREQ_CODE,DOSE_CODE, "
		 * + "TAKE_DAYS,DOSAGE_QTY,DOSAGE_UNIT,OWN_PRICE,TOT_AMT, " +
		 * "BILL_DATE " + "FROM IBS_ORDD WHERE CASE_NO='"+caseNo+"' " + ///
		 * "AND ORDER_CODE='"+orderCode+"' " + "AND ORDER_NO='"+orderNo+"'";
		 */

		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		/*
		 * if(parm.getCount()>0){
		 * replenishTbl.setLockCellRow(parm.getCount()-1,false); }else{
		 * replenishTbl.setLockCellRow(0,false); }
		 */

		/*
		 * for(int i=0;i<parm.getCount();i++){
		 * parm.addData("EXE_DEPT_CODE",Operator.getCostCenter());
		 * parm.addData("EXE_STATION_CODE", station);
		 * parm.addData("EXE_DR_CODE", Operator.getID());
		 * parm.addData("DEPT_CODE", Operator.getDept());
		 * parm.addData("STATION_CODE", station); parm.addData("DR_CODE",
		 * vsDrCode); parm.addData("SCHD_CODE", clpCode_);
		 * 
		 * replenishTbl.setLockCellRow(i,true); }
		 * replenishTbl.setParmValue(parm);
		 */

		newOrderTemp(tparm);
	}

	/**
	 * 列改变值 add caoyong 2013116
	 * 
	 * @param obj
	 */
	public void onTableValueChange(Object obj) {
		TTableNode node = (TTableNode) obj;
		onTableValueChange0(node);

	}

	/**
	 * 列改变值add caoyong 20131106
	 */
	public void onTableValueChange0(TTableNode node) {
		if (node.getColumn() != 5 && node.getColumn() != 6
				&& node.getColumn() != 1) {
			return;
		}
		DecimalFormat df = new DecimalFormat("0.00");
		boolean flag = false;
		replenishTbl.acceptText();
		int col = replenishTbl.getSelectedColumn();
		String columnName = getFactColumnName("replenishTable", col);
		int row = node.getRow();
		String tackDays = "" + node.getValue();// 取得改变值天数
		double Tdays = 0;// 天数
		double ownMat = 0;// 单价\
		double medQty = 0;
		String Tmat = replenishTbl.getItemString(row, "OWN_PRICE");// 单价
		String Mqty = replenishTbl.getItemString(row, "MEDI_QTY");// 用量

		if ("TAKE_DAYS".equals(columnName)) {
			ownMat = Double.parseDouble(Tmat);
			Tdays = Double.parseDouble(tackDays);
			medQty = Double.parseDouble(Mqty);
			replenishTbl.setItem(row, "TOT_AMT", df.format(ownMat * Tdays
					* medQty * ownRate));// 改变总价
			replenishTbl.setItem(row, "DOSAGE_QTY", (int) (Tdays * medQty));// 改变总量

		}

		double dqyt = 0;
		if ("DOSAGE_QTY".equals(columnName)) {
			String qty = "" + node.getValue();// 取得改变用量的值
			ownMat = Double.parseDouble(Tmat);
			dqyt = Double.parseDouble(qty);
			replenishTbl.setItem(row, "TOT_AMT", df.format(ownMat * dqyt
					* ownRate));// 改变总价

		}

		if ("MEDI_QTY".equals(columnName)) {

			String tdays = replenishTbl.getItemString(row, "TAKE_DAYS");
			String mqty = "" + node.getValue();
			ownMat = Double.parseDouble(Tmat);
			dqyt = Double.parseDouble(mqty);
			int days = Integer.parseInt(tdays);
			replenishTbl.setItem(row, "DOSAGE_QTY", (int) (days * dqyt));// 改变总量
			replenishTbl.setItem(row, "TOT_AMT", df.format(ownMat * dqyt * days
					* ownRate));// 改变总价

		}
	}

	/**
	 * add caoyong 20131108
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// 拿到列名
		String columnName = this.getFactColumnName("replenishTable", column);
		if (!columnName.contains("ORDER_CHN_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");

	}

	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * 拿到更变之前的列号 add caoyong 20131108
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTable(table).getColumnModel().getColumnIndex(column);
	}

	public void newOrder(String tag, Object obj) {

		TParm parm = (TParm) obj;
		replenishTbl.acceptText();
		int selRow = replenishTbl.getSelectedRow();
		insertPackOrder(selRow, parm);

	}

	private void newOrderTemp(TParm parm) {
		replenishTbl.acceptText();
		int selRow = replenishTbl.addRow();
		clpCode_ = outsideParm.getValue("INW", "CLNCPATH_CODE");
		String vsDrCode = this.getValueString("VC_CODE");
		String station = parm.getValue("STATION_CODE");
		replenishTbl.setItem(selRow, "ORDER_CODE", "");
		replenishTbl.setItem(selRow, "ORDER_CHN_DESC", "");
		replenishTbl.setItem(selRow, "MEDI_QTY", "");
		replenishTbl.setItem(selRow, "MEDI_UNIT", "");
		replenishTbl.setItem(selRow, "FREQ_CODE", "");
		replenishTbl.setItem(selRow, "DOSE_CODE", "");
		replenishTbl.setItem(selRow, "TAKE_DAYS", "");
		replenishTbl.setItem(selRow, "DOSAGE_QTY", "");
		replenishTbl.setItem(selRow, "DOSAGE_UNIT", "");
		replenishTbl.setItem(selRow, "OWN_PRICE", "");
		replenishTbl.setItem(selRow, "TOT_AMT", "");
		replenishTbl.setItem(selRow, "EXE_DEPT_CODE", Operator.getCostCenter());
		replenishTbl.setItem(selRow, "EXE_STATION_CODE", station);
		replenishTbl.setItem(selRow, "EXE_DR_CODE", Operator.getID());
		replenishTbl.setItem(selRow, "DEPT_CODE", Operator.getDept());
		replenishTbl.setItem(selRow, "STATION_CODE", station);
		replenishTbl.setItem(selRow, "DR_CODE", vsDrCode);
		replenishTbl.setItem(selRow, "SCHD_CODE", clpCode_);
		replenishTbl.setItem(selRow, "BILL_DATE", SystemTool.getInstance()
				.getDate());
		replenishTbl.setItem(selRow, "INV_CODE", "");
		replenishTbl.setItem(selRow, "SCHD_CODE", "");
		replenishTbl.setItem(selRow, "CLNCPATH_CODE", "");
		replenishTbl.setItem(selRow, "ORDER_NO", this.getOrderNo());
		replenishTbl.setItem(selRow, "ORDER_SEQ", this.getOrderSeq());
		replenishTbl.setItem(selRow, "HEXP_CODE", "");
		replenishTbl.setItem(selRow, "CAT1_TYPE", "");
		replenishTbl.setItem(selRow, "OWN_RATE", "");
		replenishTbl.setItem(selRow, "COST_CENTER_CODE", "");
		replenishTbl.setItem(selRow, "ORDER_CAT1_CODE", "");
	}

	/**
	 * 接受传回值
	 * 
	 * @param selRow
	 * @param parm
	 */
	private void insertPackOrder(int selRow, TParm parm) {
		TParm hexpParm = SYSChargeHospCodeTool.getInstance()
				.selectalldata(parm);
		TParm xparm = new TParm();
		double ownPriceSingle = 0.00;
		if(null!=lumpworkCode&&lumpworkCode.length()>0){
			if(lumpWorkRate<=0){
				this.messageBox("套餐病患折扣存在问题,不可以操作");
				return;
			}
			IBSNewTool tool=new IBSNewTool();
			TParm queryInfoParm=tool.onCheckLumWorkCaseNo("",caseNo);
			String caseNoNew=queryInfoParm.getValue("CASE_NO");
			//药品、血费不执行套餐折扣流程，套餐外费用
			if(null!=parm.getValue("CAT1_TYPE") &&parm.getValue("CAT1_TYPE").equals("PHA")||
					null!=parm.getValue("CHARGE_HOSP_CODE") && parm.getValue("CHARGE_HOSP_CODE").equals("RA")){
				ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, parm
						.getValue("ORDER_CODE"), serviceLevel);	
				if ("2".equals(serviceLevel)) {
    				ownPriceSingle = parm.getDouble("OWN_PRICE2");
    			} else if ("3".equals(serviceLevel)) {
    				ownPriceSingle = parm.getDouble("OWN_PRICE3");
    			} else {
    				ownPriceSingle = parm.getDouble("OWN_PRICE");
    			}
			}else{
				ownRate=lumpWorkRate;//套餐内费用，根据套餐折扣计算
				ownPriceSingle =IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpworkCode, 
						parm.getValue("ORDER_CODE"), serviceLevel);
			}
		}else{
			if ("2".equals(serviceLevel)) {
				ownPriceSingle = parm.getDouble("OWN_PRICE2");
			} else if ("3".equals(serviceLevel)) {
				ownPriceSingle = parm.getDouble("OWN_PRICE3");
			} else {
				ownPriceSingle = parm.getDouble("OWN_PRICE");
			}
			ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code, parm
					.getValue("ORDER_CODE"), serviceLevel);//正常病患
		}
		clncpathCode = this.getValueString("CLP");
		// TParm parmDetail = SYSOrderSetDetailTool.getInstance()
		// .selectByOrderSetCode(parm.getValue("ORDER_CODE"));

		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {// 联合医嘱

			replenishTbl.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));
			replenishTbl.setItem(selRow, "ORDERSET_CODE", parm
					.getValue("ORDER_CODE"));

		} else {// 非联合医嘱
			replenishTbl.setItem(selRow, "ORDERSET_GROUP_NO", "");
			replenishTbl.setItem(selRow, "ORDERSET_CODE", "");
		}
		// this.messageBox("1"+parm.getValue("ORDER_CAT1_CODE"));
		// this.messageBox("2"+parm.getValue("EXEC_DEPT_CODE"));
		// this.messageBox("3"+parm.getValue("EXEC_DEPT_CODE"));
		replenishTbl.setItem(selRow, "ORDER_CHN_DESC", parm
				.getValue("ORDER_DESC"));
		replenishTbl.setItem(selRow, "MEDI_QTY", 1);
		replenishTbl.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		replenishTbl.setItem(selRow, "FREQ_CODE", "STAT");
		replenishTbl.setItem(selRow, "DOSE_CODE", "");
		replenishTbl.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		replenishTbl.setItem(selRow, "TAKE_DAYS", 1);
		replenishTbl.setItem(selRow, "DOSAGE_QTY", 1);// 总量
		replenishTbl.setItem(selRow, "OWN_PRICE", ownPriceSingle);
		replenishTbl.setItem(selRow, "TOT_AMT", ownPriceSingle * ownRate);
		replenishTbl.setItem(selRow, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		replenishTbl.setItem(selRow, "REXP_CODE", hexpParm.getValue(
				"IPD_CHARGE_CODE", 0));
		replenishTbl.setItem(selRow, "HEXP_CODE", parm
				.getValue("CHARGE_HOSP_CODE"));
		replenishTbl.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		replenishTbl.setItem(selRow, "OWN_RATE", ownRate);
		replenishTbl.setItem(selRow, "ORDER_CAT1_CODE", parm
				.getValue("ORDER_CAT1_CODE"));
		replenishTbl.setItem(selRow, "COST_CENTER_CODE", parm
				.getValue("EXEC_DEPT_CODE"));
		replenishTbl.setItem(selRow, "CLNCPATH_CODE", clncpathCode);

		String vsDrCode_ = this.getValueString("VC_CODE");
		xparm.setData("VS_DR_CODE", vsDrCode_);
		xparm.setData("STATION_CODE", outsideParm.getValue("INW",
				"STATION_CODE"));
		if (selRow == replenishTbl.getRowCount() - 1) {

			newOrderTemp(xparm);
		}

	}

	/**
	 * 查询最大医嘱顺序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	/*
	 * public TParm selMaxOrderSeq(String caseNo) {
	 * 
	 * String sql = " SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM IBS_ORDD " +
	 * " WHERE CASE_NO = '" + caseNo + "' " + " AND ORDER_NO = '" +
	 * this.getOrderNo() + "' "; TParm result = new
	 * TParm(TJDODBTool.getInstance().select(sql)); if (result.getErrCode() < 0)
	 * { this.messageBox(result.getErrText()); return result; } return result; }
	 */

	/**
	 * 查询最大账务序号
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 皮试结果
	 */
	public void onSkiResult() {
		TParm parm = new TParm();
		TParm result = new TParm();
		// 查询选中药嘱是否为皮试药品
		String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE),"
				+ "B.BATCH_NO,B.SKINTEST_NOTE"
				+ " FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = '"
				+ orderCodeSki
				+ "' AND B.CASE_NO = '"
				+ caseNo
				+ "' "
				+ "GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE "
				+ "ORDER BY B.OPT_DATE DESC";
		// System.out.println("皮试药品 sql is：："+sql);
		TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result1.getCount() <= 0) {
			this.messageBox("该药品不存在。");
			return;
		} else if (result1.getValue("SKINTEST_FLG", 0).equals("N")) {
			this.messageBox("非皮试药品。");
			return;
		} else if (result1.getValue("BATCH_NO", 0).equals(null)
				|| "".equals(result1.getValue("BATCH_NO", 0))) {
			parm.setData("BATCH_NO", "");// 批号
			parm.setData("SKINTEST_NOTE", "");// 皮试结果
		} else {
			parm.setData("BATCH_NO", result1.getValue("BATCH_NO", 0));// 批号
			parm.setData("SKINTEST_FLG", result1.getValue("SKINTEST_NOTE", 0));// 皮试结果
		}
		// //查询最近的皮试结果和皮试批号
		// String skiNoSql = "SELECT MAX(OPT_DATE),BATCH_NO,SKINTEST_NOTE " +
		// "FROM PHA_ANTI WHERE CASE_NO= '"+caseNo+"' AND ORDER_CODE= '"+orderCodeSki+"' "
		// +
		// "AND  BATCH_NO IS NOT NULL " +
		// "GROUP BY BATCH_NO ,SKINTEST_NOTE,OPT_DATE " +
		// "ORDER BY OPT_DATE";
		// // System.out.println("皮试查询语句："+skiNoSql);
		// TParm parm = new TParm(TJDODBTool.getInstance().select(skiNoSql));
		// if(parm.getCount()<=0){
		// parm.setData("BATCH_NO", "11");//批号
		// parm.setData("SKINTEST_NOTE", "22");//皮试结果
		// }
		// parm.setData("BATCH_NO", parm.getValue("BATCH_NO", 0));//批号
		// parm.setData("SKINTEST_NOTE", parm.getValue("SKINTEST_NOTE",
		// 0));//皮试结果
		parm.setData("CASE_NO", caseNo);// 就诊号
		parm.setData("ORDER_CODE", orderCodeSki);// 医嘱代码
		parm.setData("ORDER_NO", orderNoSki);//
		parm.setData("SEQ_NO", orderSeqSki);//
		parm.setData("OPT_USER", Operator.getID());//
		parm.setData("OPT_TERM", Operator.getIP());//
		result = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWSkiResult.x",
				parm, true);
		// System.out.println("皮试结果回传："+result);
		if (null != result) {
			String psResult = "";
			if (result.getValue("SKINTEST_NOTE", 0).equals("0")) {
				psResult = "阴性";
			} else if (result.getValue("SKINTEST_NOTE", 0).equals("1")) {
				psResult = "阳性";
			}
			skiResult = "皮试结果：" + psResult + ";  批号:"
					+ result.getValue("BATCH_NO", 0);
		}
		TTable table2 = this.getTable("detailTable");
		for (int j = 0; j < table2.getRowCount(); j++) {
			table2.setItem(j, "SKIN_RESULT", skiResult);
		}
	}

	/**
	 * 结合医嘱主项和细项 以及非结合医嘱合并
	 */
	public TParm addParm() {
		TParm tparm = new TParm();
		TParm tresult = new TParm();
		DecimalFormat df = new DecimalFormat();
		String ordersetCode = "";
		// double dosageQty=0;//用量
		// int days =0;//天数
		// double ownPrice=0;//单价
		// double totAmt=0;//总价
		for (int i = 0; i < replenishTbl.getRowCount(); i++) {
			if (replenishTbl.getItemString(i, "ORDER_CODE").length() > 0) {
				ordersetCode = replenishTbl.getItemString(i, "ORDERSET_CODE");
				// days=replenishTbl.getItemInt(i,"TAKE_DAYS");

				if (ordersetCode.length() > 0) {

					tresult = getOrderSetList(ordersetCode);
					// System.out.println("联合医嘱"+tresult);

					for (int j = 0; j < tresult.getCount(); j++) {
						// dosageQty=tresult.getInt("DOSAGE_QTY",j);

						// ownPrice=tresult.getDouble("OWN_PRICE",j);

						// dosageQty=dosageQty*days;
						// totAmt=dosageQty*ownPrice;

						// this.messageBox("总金额"+totAmt);
						tparm.addData("ORDER_CHN_DESC", tresult.getValue(
								"ORDER_DESC", j));
						tparm.addData("MEDI_QTY", tresult.getInt("DOSAGE_QTY",
								j));
						tparm.addData("MEDI_UNIT", tresult.getValue(
								"UNIT_CODE", j));
						tparm.addData("FREQ_CODE", "");
						tparm.addData("DOSE_CODE", "");
						tparm.addData("TAKE_DAYS", replenishTbl.getItemString(
								i, "TAKE_DAYS"));
						tparm.addData("DOSAGE_QTY", tresult.getInt(
								"DOSAGE_QTY", j));
						tparm.addData("DOSAGE_UNIT", tresult
								.getValue("DOSAGE_UNIT,j"));
						tparm.addData("OWN_PRICE", tresult.getValue(
								"OWN_PRICE", j));
						tparm.addData("TOT_AMT", tresult.getInt("TOT_AMT", j));
						tparm.addData("EXE_DEPT_CODE", replenishTbl
								.getItemString(i, "EXE_DEPT_CODE"));
						tparm.addData("EXE_STATION_CODE", replenishTbl
								.getItemString(i, "EXE_STATION_CODE"));
						tparm.addData("EXE_DR_CODE", replenishTbl
								.getItemString(i, "EXE_DR_CODE"));
						tparm.addData("DEPT_CODE", replenishTbl.getItemString(
								i, "DEPT_CODE"));
						tparm.addData("ORDERSET_CODE", replenishTbl
								.getItemString(i, "ORDERSET_CODE"));
						tparm.addData("ORDERSET_GROUP_NO", replenishTbl
								.getItemString(i, "ORDERSET_GROUP_NO"));
						tparm.addData("STATION_CODE", replenishTbl
								.getItemString(i, "STATION_CODE"));
						tparm.addData("DR_CODE", replenishTbl.getItemString(i,
								"DR_CODE"));
						tparm.addData("SCHD_CODE", replenishTbl.getItemString(
								i, "SCHD_CODE"));
						tparm.addData("INV_CODE", replenishTbl.getItemString(i,
								"INV_CODE"));
						tparm.addData("CLNCPATH_CODE", replenishTbl
								.getItemString(i, "CLNCPATH_CODE"));
						tparm.addData("ORDER_CODE", tresult.getValue(
								"ORDER_CODE", j));
						tparm.addData("ORDER_NO", replenishTbl.getItemString(i,
								"ORDER_NO"));
						tparm.addData("ORDER_SEQ", replenishTbl.getItemString(
								i, "ORDER_SEQ"));
						tparm.addData("REXP_CODE", replenishTbl.getItemString(
								i, "REXP_CODE"));
						tparm.addData("HEXP_CODE", replenishTbl.getItemString(
								i, "HEXP_CODE"));
						tparm.addData("CAT1_TYPE", replenishTbl.getItemString(
								i, "CAT1_TYPE"));
						tparm.addData("OWN_RATE", replenishTbl.getItemString(i,
								"OWN_RATE"));
						tparm.addData("ORDER_CAT1_CODE", replenishTbl
								.getItemString(i, "ORDER_CAT1_CODE"));
						tparm.addData("COST_CENTER_CODE", replenishTbl
								.getItemString(i, "COST_CENTER_CODE"));
						// tparm.addData(
						// "CLNCPATH_CODE",replenishTbl.getItemString(i,"CLNCPATH_CODE"));
					}
				}
				tparm.addData("ORDER_CHN_DESC", replenishTbl.getItemString(i,
						"ORDER_CHN_DESC"));
				tparm.addData("MEDI_QTY", replenishTbl.getItemString(i,
						"MEDI_QTY"));
				tparm.addData("MEDI_UNIT", replenishTbl.getItemString(i,
						"MEDI_UNIT"));
				tparm.addData("FREQ_CODE", replenishTbl.getItemString(i,
						"FREQ_CODE"));
				tparm.addData("DOSE_CODE", replenishTbl.getItemString(i,
						"DOSE_CODE"));
				tparm.addData("TAKE_DAYS", replenishTbl.getItemString(i,
						"TAKE_DAYS"));
				tparm.addData("DOSAGE_QTY", replenishTbl.getItemString(i,
						"DOSAGE_QTY"));
				tparm.addData("DOSAGE_UNIT", replenishTbl.getItemString(i,
						"DOSAGE_UNIT"));
				tparm.addData("OWN_PRICE", replenishTbl.getItemString(i,
						"OWN_PRICE"));
				tparm.addData("TOT_AMT", replenishTbl.getItemString(i,
						"TOT_AMT"));
				tparm.addData("EXE_DEPT_CODE", replenishTbl.getItemString(i,
						"EXE_DEPT_CODE"));
				tparm.addData("EXE_STATION_CODE", replenishTbl.getItemString(i,
						"EXE_STATION_CODE"));
				tparm.addData("EXE_DR_CODE", replenishTbl.getItemString(i,
						"EXE_DR_CODE"));
				tparm.addData("DEPT_CODE", replenishTbl.getItemString(i,
						"DEPT_CODE"));
				tparm.addData("ORDERSET_CODE", replenishTbl.getItemString(i,
						"ORDERSET_CODE"));
				tparm.addData("ORDERSET_GROUP_NO", replenishTbl.getItemString(
						i, "ORDERSET_GROUP_NO"));
				tparm.addData("STATION_CODE", replenishTbl.getItemString(i,
						"STATION_CODE"));
				tparm.addData("DR_CODE", replenishTbl.getItemString(i,
						"DR_CODE"));
				tparm.addData("SCHD_CODE", replenishTbl.getItemString(i,
						"SCHD_CODE"));
				tparm.addData("INV_CODE", replenishTbl.getItemString(i,
						"INV_CODE"));
				tparm.addData("CLNCPATH_CODE", replenishTbl.getItemString(i,
						"CLNCPATH_CODE"));
				tparm.addData("ORDER_CODE", replenishTbl.getItemString(i,
						"ORDER_CODE"));
				tparm.addData("ORDER_NO", replenishTbl.getItemString(i,
						"ORDER_NO"));
				tparm.addData("ORDER_SEQ", replenishTbl.getItemString(i,
						"ORDER_SEQ"));
				tparm.addData("REXP_CODE", replenishTbl.getItemString(i,
						"REXP_CODE"));
				tparm.addData("HEXP_CODE", replenishTbl.getItemString(i,
						"HEXP_CODE"));
				tparm.addData("CAT1_TYPE", replenishTbl.getItemString(i,
						"CAT1_TYPE"));
				tparm.addData("OWN_RATE", replenishTbl.getItemString(i,
						"OWN_RATE"));
				tparm.addData("ORDER_CAT1_CODE", replenishTbl.getItemString(i,
						"ORDER_CAT1_CODE"));
				tparm.addData("COST_CENTER_CODE", replenishTbl.getItemString(i,
						"COST_CENTER_CODE"));
				// tparm.addData(
				// "CLNCPATH_CODE",replenishTbl.getItemString(i,"CLNCPATH_CODE"));
			}

		}

		return tparm;

	}

	/**
	 * add caoyong 20131108 添加新添加的医嘱
	 */
	public void getInsetDetail(TParm parm) {

		DecimalFormat df = new DecimalFormat("0.00");
		// replenishTbl.acceptText();
		TParm tparm = new TParm();
		TParm result = new TParm();
		double newTotamt = 0;
		String ordersetCode = "";
		// TParm orderSeqParm = this.selMaxOrderSeq(caseNo);
		// orderSeq = orderSeqParm.getInt("ORDER_SEQ", 0);
		TParm groupNoParm = this.selMaxCaseNoSeq(caseNo);
		casenoSeq = groupNoParm.getInt("CASE_NO_SEQ", 0);
		// this.messageBox(""+casenoSeq);
		insertIbsOrdmlist(casenoSeq);

		TParm checkParm = checkOrderSave(parm);// 校验
		if (checkParm.getErrCode() < 0) {
			this.messageBox(checkParm.getErrText());

			return;
		}

		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {

			// this.messageBox(parm.getValue("ORDER_CHN_DESC",i));
			if (parm.getValue("ORDER_CODE", i).length() > 0) {

				newTotamt += TypeTool.getDouble(parm.getDouble("TOT_AMT", i));
				// Timestamp billDate =
				// StringTool.getTimestamp(replenishTbl.getItemString(i,"BILL_DATE"),
				// "yyyyMMddHHmm");

				tparm.setData("CASE_NO", this.getCaseNo());
				tparm.setData("CASE_NO_SEQ", casenoSeq + 1);
				tparm.setData("SEQ_NO", seqNo++);
				tparm.setData("OWN_FLG", "Y");
				tparm.setData("BILL_FLG", "Y");

				tparm.setData("ORDER_CHN_DESC", parm.getValue("ORDER_CHN_DESC",
						i));
				tparm.setData("MEDI_QTY", parm.getValue("MEDI_QTY", i));
				tparm.setData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
				tparm.setData("FREQ_CODE", parm.getValue("FREQ_CODE", i));
				tparm.setData("DOSE_CODE", parm.getValue("DOSE_CODE", i));
				tparm.setData("TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
				tparm.setData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				tparm.setData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
				tparm.setData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
				tparm.setData("TOT_AMT", parm.getValue("TOT_AMT", i));
				tparm.setData("OWN_AMT", parm.getValue("TOT_AMT", i));
				tparm.setData("EXE_DEPT_CODE", parm
						.getValue("EXE_DEPT_CODE", i));
				tparm.setData("EXE_STATION_CODE", parm.getValue(
						"EXE_STATION_CODE", i));
				tparm.setData("EXE_DR_CODE", parm.getValue("EXE_DR_CODE", i));
				tparm.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
				tparm.setData("ORDERSET_CODE", parm
						.getValue("ORDERSET_CODE", i));
				tparm.setData("ORDERSET_GROUP_NO", parm.getValue(
						"ORDERSET_GROUP_NO", i));

				tparm.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
				tparm.setData("DR_CODE", parm.getValue("DR_CODE", i));
				tparm.setData("SCHD_CODE", parm.getValue("SCHD_CODE", i));
				tparm.setData("BILL_DATE", SystemTool.getInstance().getDate());
				tparm.setData("INV_CODE", parm.getValue("INV_CODE", i));
				tparm.setData("CLNCPATH_CODE", parm
						.getValue("CLNCPATH_CODE", i));
				tparm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ", i));
				tparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				tparm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
				tparm.setData("REXP_CODE", parm.getValue("REXP_CODE", i));
				tparm.setData("HEXP_CODE", parm.getValue("HEXP_CODE", i));
				tparm.setData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
				tparm.setData("BEGIN_DATE", SystemTool.getInstance().getDate());
				tparm.setData("END_DATE", SystemTool.getInstance().getDate());
				tparm.setData("OWN_RATE", parm.getValue("OWN_RATE", i));
				tparm.setData("ORDER_CAT1_CODE", parm.getValue(
						"ORDER_CAT1_CODE", i));
				tparm.setData("COST_CENTER_CODE", parm.getValue(
						"COST_CENTER_CODE", i));
				tparm.setData("OPT_USER", Operator.getID());
				tparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
				tparm.setData("OPT_TERM", Operator.getIP());
				result = InwOrderExecTool.getInstance().getInsetDetailed(tparm);
			}
		}

		String selADMSql = " SELECT TOTAL_AMT,CUR_AMT,DEPT_CODE "
				+ "   FROM ADM_INP " + "  WHERE CASE_NO = '" + this.getCaseNo()
				+ "'";
		TParm selADMParm = new TParm(TJDODBTool.getInstance().select(selADMSql));

		double oleTotalAmt = selADMParm.getDouble("TOTAL_AMT", 0);
		// System.out.println("老总价"+oleTotalAmt);
		double oleCurAmt = selADMParm.getDouble("CUR_AMT", 0);
		// System.out.println("老剩余金额"+oleTotalAmt);
		double newTotalAmt = oleTotalAmt + newTotamt;
		// System.out.println("新金额产生"+newTotamt);
		double newCurAmt = oleCurAmt - newTotamt;
		String sql = " UPDATE ADM_INP SET TOTAL_AMT = '"
				+ df.format(newTotalAmt) + "',CUR_AMT = '"
				+ df.format(newCurAmt) + "' " + "  WHERE CASE_NO = '"
				+ this.getCaseNo() + "'";
		TParm sresult = new TParm(TJDODBTool.getInstance().update(sql));

		if (result.getErrCode() < 0 && sresult.getErrCode() < 0) {
			this.messageBox("添加失败");
			return;
		}
		this.messageBox("添加成功");
		replenishTbl.removeRowAll();
	}

	/**
	 * 接收科套餐和护士套餐的传回值 add caoyong 20131108
	 * 
	 * @param parm
	 * @param dosage_qty
	 */
	public void insertNewOperationOrder(TParm parm, double dosage_qty) {
		replenishTbl.acceptText();
		TParm xparm = new TParm();
		// 收据费用代码
		TParm hexpParm = SYSChargeHospCodeTool.getInstance()
				.selectalldata(parm);

		double ownPriceSingle = 0.00;
		if(null!=lumpworkCode&&lumpworkCode.length()>0){
			TParm lumpParm=new TParm();
    		lumpParm.setData("CASE_NO",caseNo);
    		lumpParm.setData("PACKAGE_CODE",lumpworkCode);
    		lumpParm.setData("ORDER_CODE",parm.getValue("ORDER_CODE"));
    		TParm feeParm= MEMTool.getInstance().selectMemPackageSectionDByCaseNo(lumpParm); 
    		ownPriceSingle = feeParm.getDouble("OWN_PRICE", 0);
    		if(feeParm.getErrCode()<0||feeParm.getCount()<=0){//套餐外医嘱，正常计费
    			if ("2".equals(serviceLevel)) {
    				ownPriceSingle = parm.getDouble("OWN_PRICE2");
    			} else if ("3".equals(serviceLevel)) {
    				ownPriceSingle = parm.getDouble("OWN_PRICE3");
    			} else {
    				ownPriceSingle = parm.getDouble("OWN_PRICE");
    			}
    		}
		}else{
			if ("2".equals(serviceLevel)) {
				ownPriceSingle = parm.getDouble("OWN_PRICE2");
			} else if ("3".equals(serviceLevel)) {
				ownPriceSingle = parm.getDouble("OWN_PRICE3");
			} else {
				ownPriceSingle = parm.getDouble("OWN_PRICE");
			}
		}
		
		if(null!=lumpworkCode&&lumpworkCode.length()>0){//套餐患者计费根据入院登记计算的折扣操作
			 if(lumpWorkRate<=0){
				 this.messageBox("套餐病患折扣存在问题,不可以操作");
				 return;
			 }
			TParm sysFeeParm=SYSFeeTool.getInstance().getFeeAllData(parm.getValue("ORDER_CODE"));
			if(sysFeeParm.getCount()<=0){
				 this.messageBox("未找到当前操作医嘱");
				 return;
			}
			//药品、血费根据身份折扣计算(套餐外)
			if(null!=sysFeeParm.getValue("CAT1_TYPE",0) &&sysFeeParm.getValue("CAT1_TYPE",0).equals("PHA")||
					null!=sysFeeParm.getValue("CHARGE_HOSP_CODE",0) && sysFeeParm.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
				 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,parm.getValue("ORDER_CODE"),serviceLevel);
			}else{
				 ownRate=lumpWorkRate;//套餐患者根据套餐折扣计算(套餐内)
			}
		 }else{
			 ownRate = BIL.getRate(ctz1Code, ctz2Code, ctz3Code,parm.getValue("ORDER_CODE"),serviceLevel);  //add by huangtt 20130922
		 }
		// this.messageBox("传回"+parm.getCount());
		int selRow = replenishTbl.getRowCount() - 1;
		if ("Y".equals(parm.getValue("ORDERSET_FLG"))) {
			replenishTbl.setItem(selRow, "ORDERSET_GROUP_NO", parm
					.getInt("ORDERSET_GROUP_NO"));
			replenishTbl.setItem(selRow, "ORDERSET_CODE", parm
					.getValue("ORDER_CODE"));
		} else {
			replenishTbl.setItem(selRow, "ORDERSET_GROUP_NO", "");
			replenishTbl.setItem(selRow, "ORDERSET_CODE", "");
		}
		replenishTbl.setItem(selRow, "ORDER_CHN_DESC", parm
				.getValue("ORDER_DESC"));
		replenishTbl.setItem(selRow, "MEDI_QTY", 1);
		replenishTbl.setItem(selRow, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		replenishTbl.setItem(selRow, "FREQ_CODE", "STAT");
		replenishTbl.setItem(selRow, "DOSE_CODE", parm.getValue("DOSE_CODE"));
		replenishTbl.setItem(selRow, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		replenishTbl.setItem(selRow, "TAKE_DAYS", 1);
		replenishTbl.setItem(selRow, "DOSAGE_QTY", 1);// 总量
		replenishTbl.setItem(selRow, "OWN_PRICE", ownPriceSingle);
		replenishTbl.setItem(selRow, "TOT_AMT", ownPriceSingle);
		replenishTbl.setItem(selRow, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		replenishTbl.setItem(selRow, "REXP_CODE", hexpParm.getValue(
				"IPD_CHARGE_CODE", 0));
		replenishTbl.setItem(selRow, "HEXP_CODE", parm
				.getValue("CHARGE_HOSP_CODE"));
		replenishTbl.setItem(selRow, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		String vsDrCode_ = this.getValueString("VC_CODE");
		xparm.setData("VS_DR_CODE", vsDrCode_);
		xparm.setData("STATION_CODE", outsideParm.getValue("INW",
				"STATION_CODE"));
		newOrderTemp(xparm);

	}

	public TParm checkOrderSave(TParm parm) {
		TParm result = new TParm();
		// 新加的数据
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			/*
			 * if (!order.isActive(i, buff)) continue; //
			 * 增加处方标记位管控-------start-------- wangl modify String orderCode =
			 * order.getRowParm(i, buff).getValue("ORDER_CODE"); String
			 * orderCodeSql =
			 * " SELECT ORDER_CODE,ORDER_DESC,SPECIFICATION,DR_ORDER_FLG " +
			 * "   FROM SYS_FEE " + "  WHERE ORDER_CODE = '" + orderCode + "'";
			 * // System.out.println("处方权限查询sql" + orderCodeSql); TParm
			 * orderCodeParm = new TParm(TJDODBTool.getInstance().select(
			 * orderCodeSql)); boolean drOrderFlg =
			 * orderCodeParm.getBoolean("DR_ORDER_FLG", 0); if (drOrderFlg) {
			 * this.messageBox(orderCodeParm.getValue("ORDER_DESC", 0) + "||" +
			 * orderCodeParm.getValue("SPECIFICATION", 0) + "为医师处方药,不得开立");
			 * 
			 * 
			 * }
			 */
			// 增加处方标记位管控-------end-------- wangl modify

			// 增加退费数量管控
			String orderCode = parm.getValue("ORDER_CODE", i);
			double dosageQty = parm.getDouble("DOSAGE_QTY", i);
			if (dosageQty < 0) {
				String selQtySql = " SELECT SUM(DOSAGE_QTY) AS DOSAGE_QTY,ORDER_CODE "
						+ "   FROM IBS_ORDD "
						+ "  WHERE ORDER_CODE = '"
						+ orderCode
						+ "' "
						+ "    AND CASE_NO = '"
						+ caseNo
						+ "' " + "  GROUP BY ORDER_CODE ";
				// System.out.println("selQtyParmsql" + orderCodeSql);
				TParm selQtyParm = new TParm(TJDODBTool.getInstance().select(
						selQtySql));
				double dosageQtyTot = selQtyParm.getDouble("DOSAGE_QTY", 0);
				if (Math.abs(dosageQty) > dosageQtyTot) {
					result.setErr(-1, "退费数量超过合计数量,不能保存");
					return result;
				}
			}
			/*
			 * if (order.getRowParm(i, buff).getValue("ORDERSET_CODE") == null
			 * || order.getRowParm(i, buff).getValue("ORDERSET_CODE") .length()
			 * == 0) { // 用量 if (order.getRowParm(i, buff).getDouble("MEDI_QTY")
			 * == 0) { //
			 * this.messageBox_(ds.getRowParm(i,buff).getDouble("MEDI_QTY"
			 * )+"==="+i); result.setErrCode(-1);
			 * result.setErrText(order.getRowParm(i, buff).getValue(
			 * "ORDER_DESC") + "用量不能为:0"); result.setData("ERR", "ORDER_CODE",
			 * order.getRowParm(i, buff).getValue("ORDER_CODE")); return result;
			 * } //caowl 20130130 start //频次 if (order.getRowParm(i,
			 * buff).getValue("FREQ_CODE").length() == 0) { result.setErrCode(
			 * -2); result.setErrText(order.getRowParm(i,
			 * buff).getValue("ORDER_DESC") + "医嘱频次不可以为空");
			 * result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
			 * buff).getValue("ORDER_CODE")); return result; } //caowl 20130130
			 * end // 天数 if (order.getRowParm(i, buff).getInt("TAKE_DAYS") == 0)
			 * { result.setErrCode(-3); result.setErrText(order.getRowParm(i,
			 * buff).getValue( "ORDER_DESC") + "医嘱天数不可以为0");
			 * result.setData("ERR", "ORDER_CODE", order.getRowParm(i,
			 * buff).getValue("ORDER_CODE")); return result; }
			 * 
			 * } // 执行科室 if (order.getRowParm(i,
			 * buff).getValue("EXE_DEPT_CODE").length() == 0) {
			 * result.setErrCode(-4); result.setErrText(order.getRowParm(i,
			 * buff).getValue( "ORDER_DESC") + "执行科室不能为空");
			 * result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
			 * .getValue("ORDER_CODE")); return result; } // 开单科室 if
			 * (order.getRowParm(i, buff).getValue("DEPT_CODE").length() == 0) {
			 * result.setErrCode(-4); result.setErrText(order.getRowParm(i,
			 * buff).getValue( "ORDER_DESC") + "开单科室不能为空");
			 * result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
			 * .getValue("ORDER_CODE")); return result; } // 医嘱代码 if
			 * (order.getRowParm(i, buff).getValue("ORDER_CODE").length() == 0)
			 * { result.setErrCode(-4); result.setErrText(order.getRowParm(i,
			 * buff).getValue( "ORDER_DESC") + "医嘱代码不能为空");
			 * result.setData("ERR", "ORDER_CODE", order.getRowParm(i, buff)
			 * .getValue("ORDER_CODE")); return result; } //
			 * System.out.println("总量"+order.getRowParm(i, //
			 * buff).getDouble("DOSAGE_QTY")); //caowl 20130130 start 总量不能为零
			 * //总量 if (order.getRowParm(i, buff).getDouble("DOSAGE_QTY") == 0)
			 * { result.setErrCode( -6); result.setErrText(order.getRowParm(i,
			 * buff).getValue("ORDER_DESC") + "总量不能为0"); result.setData("ERR",
			 * "ORDER_CODE", order.getRowParm(i, buff).getValue("ORDER_CODE"));
			 * return result; } //caowl 20130130 end // 检核库存 //
			 * if("PHA".equals(ds.getRowParm(i,buff).getValue("CAT1_TYPE"))){ //
			 * if(!INDTool.getInstance().inspectIndStock(ds.getRowParm(i,buff).
			 * getValue
			 * ("EXEC_DEPT_CODE"),ds.getRowParm(i,buff).getValue("ORDER_CODE"),
			 * // ds.getRowParm(i,buff).getDouble("DOSAGE_QTY"))){ //
			 * result.setErrCode(-5); //
			 * result.setErrText(ds.getRowParm(i,buff).
			 * getValue("ORDER_DESC")+"库存不足！"); //
			 * result.setData("ERR","INDEX",index
			 * .get(ds.getRowParm(i,buff).getValue("RX_KIND"))); //
			 * result.setData
			 * ("ERR","ORDER_CODE",ds.getRowParm(i,buff).getValue("ORDER_CODE"
			 * )); // return result; // } // } }
			 */
		}
		return result;
	}

	/**
	 * add caoyong 20131121 查询联合医嘱的细项
	 * 
	 * @param ordersetCode
	 * @return
	 */
	public TParm getOrderSetList(String ordersetCode) {
		TParm result = new TParm();
		// String orderCode = parm.getValue("ORDER_CODE");

		String sql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.DESCRIPTION,B.UNIT_CODE,B.INSPAY_TYPE,"
				+ " B.ORDERSET_FLG,B.INDV_FLG,ROWNUM+1 AS ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
				+ " B.RPTTYPE_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.SPECIFICATION,"
				+ " 'Y' AS HIDE_FLG,B.DEV_CODE,B.IPD_FIT_FLG,A.DOSAGE_QTY, "
				+ " B.OWN_PRICE,(A.DOSAGE_QTY*B.OWN_PRICE) AS TOT_AMT"
				+ " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDERSET_CODE='"
				+ ordersetCode + "' AND A.ORDER_CODE=B.ORDER_CODE";

		// System.out.println("sql:::::::"+sql);
		result = new TParm(this.getDBTool().select(sql));
		// System.out.println("得到集合医嘱细项:"+result);
		return result;
	}

	/**
	 * add caoyong 查找ibs_ordm case_No_seq
	 * 
	 * @param caseNo
	 * @return
	 */
	/*
	 * public TParm getIbsOrdmlist(String caseNo){ String
	 * sql="SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO='"
	 * +caseNo+"'";
	 * 
	 * TParm seleCaseNoSeq = new TParm(TJDODBTool.getInstance().select(sql));
	 * 
	 * return seleCaseNoSeq;
	 * 
	 * }
	 */
	/**
        * 
        */
	public void insertIbsOrdmlist(int caseNoSeq) {
		// TParm xparm=new TParm();
		TParm sparm = new TParm();
		// sparm=this.getIbsOrdmlist(this.getCaseNo());//找到CASE_NO对应CASE_NO_SEQ对应最大值
		// int coseNOseq=sparm.getInt("CASE_NO_SEQ",0);

		// TParm groupNoParm = this.selMaxCaseNoSeq(this.getCaseNo());
		// casenoSeq = groupNoParm.getInt("CASE_NO_SEQ", 0);
		/*
		 * xparm.setData("CASE_NO",this.getCaseNo());
		 * xparm.setData("CASE_NO_SEQ",coseNOseq);
		 * xparm.setData("BILL_DATE",SystemTool.getInstance().getDate());
		 * xparm.setData("IPD_NO",ipdNo); xparm.setData("MR_NO",mrNo);
		 * xparm.setData("DEPT_CODE",deptCode);
		 * xparm.setData("STATION_CODE",this.getStationCode());
		 * xparm.setData("BED_NO",bedNo); xparm.setData("DATA_TYPE","1");
		 * xparm.setData("BILL_NO","");
		 * xparm.setData("OPT_USER",Operator.getID());
		 * xparm.setData("OPT_DATE",SystemTool.getInstance().getDate());
		 * xparm.setData("OPT_TERM",Operator.getIP() );
		 * xparm.setData("REGION_CODE",Operator.getRegion());
		 * xparm.setData("COST_CENTER_CODE","");
		 */
		int casenoseq = caseNoSeq + 1;
		String sql = " INSERT INTO IBS_ORDM (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,"
				+ " DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,"
				+ " OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,COST_CENTER_CODE)"
				+ " VALUES  " + " ('"
				+ this.getCaseNo()
				+ "','"
				+ casenoseq
				+ "',SYSDATE, "
				+ " '"
				+ ipdNo
				+ "','"
				+ mrNo
				+ "','"
				+ deptCode
				+ "','"
				+ this.getStationCode()
				+ "',"
				+ " '"
				+ bedNo
				+ "','1','','"
				+ Operator.getID()
				+ "',SYSDATE,"
				+ " '"
				+ Operator.getIP()
				+ "','" + Operator.getRegion() + "','' )";

		// System.out.println("===sql===="+sql);

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		// System.out.println("===result===="+result);
		if (result.getErrCode() < 0) {
			// / this.messageBox("取消失败");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		// this.messageBox("取消成功");

	}

	/**
	 * 检验报告
	 */
	public void onCheckPrint() {
		String mrNo = "";
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			mrNo = this.getValueString("MR_NO");
		} else if (selType == 1) {
			mrNo = this.getValueString("MR_NOOUT");
		}
		if (mrNo.equals(""))
			return;
		Timestamp now = SystemTool.getInstance().getDate();
		String patName = PatTool.getInstance().getNameForMrno(mrNo);
		String sql = "SELECT IN_DATE FROM ADM_INP WHERE CASE_NO='"
				+ this.caseNo + "'";
		TParm inparm = new TParm(TJDODBTool.getInstance().select(sql));
		String indate = StringTool.getString(inparm.getTimestamp("IN_DATE", 0),
				"yyyy-MM-dd");
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("PAT_NAME", patName);
		parm.setData("START_DATE", indate);
		parm.setData("END_DATE", StringTool.getString(now, "yyyy-MM-dd"));
		SystemTool.getInstance().OpenLisReportWeb(parm);
	}

	/**
	 * 临时医嘱执行单打印 add by lich 20140818
	 */
	
	private boolean checkComponent() {

		/** 医嘱类别 */
		boolean flag = true;
		// 判断全部按钮
		TRadioButton ord1All = (TRadioButton) this.getComponent("ord1All");
		if (ord1All.isSelected()) {
			this.messageBox("不可选择全部");
			flag = false;
			return flag;
		}
		// 判断出院带药按钮
		TRadioButton ord1DS = (TRadioButton) this.getComponent("ord1DS");
		if (ord1DS.isSelected()) {
			this.messageBox("不可选择出院带药按钮");
			flag = false;
			return flag;
		}
		// 判断住院草药按钮
		TRadioButton ord1IG = (TRadioButton) this.getComponent("ord1IG");
		if (ord1IG.isSelected()) {
			this.messageBox("不可选择住院草药按钮");
			flag = false;
			return flag;
		}
//		/** 执行确认 */
//		// 判断非未执行按钮
//		TRadioButton checkAll = (TRadioButton) this.getComponent("checkAll");
//		TRadioButton checkYes = (TRadioButton) this.getComponent("checkYes");
//		if (checkAll.isSelected() || checkYes.isSelected()) {
//			this.messageBox("您选择的不是未执行按钮");
//			flag = false;
//			return flag;
//		}
		TRadioButton ord2All = (TRadioButton) this.getComponent("ord2All");
		if (!ord2All.isSelected()) {
			this.messageBox("医嘱种类请选择全部按钮");
			flag = false;
			return flag;
		}
		return flag;
	}

	/**
	 * 汇总执行单打印 add by lich 20140818
	 * @throws ParseException 
	 */
	/*
	public void onTemporaryPrint() throws ParseException {
		Map unitMap = returnUnitMap();//储存用药单位中文对照名称
		Map personMap = returnPersonMap();//储存人员中文对照名称
		Map routeMap = returnRouteMap();//储存用法中文对照名称
		
		if (checkComponent()) {
			TRadioButton ord1ST = (TRadioButton) this.getComponent("ord1ST");// 临时
			TRadioButton ord1UD = (TRadioButton) this.getComponent("ord1UD");// 长期
			
			以下为临时医嘱打操作
			if (ord1ST.isSelected()) {

				TParm parm = masterTbl.getParmValue();
//				TParm parm = getParmData();
//				System.out.println("parm======:"+parm);
				TParm printParm = new TParm();// 打印parm

				TParm tableParm = new TParm();// 表格parm

				TParm infoParm = new TParm();// 病患性别、出生日期parm
				
				if (parm.getCount() > 0) {

					String bedNo = parm.getValue("BED_NO", 0);
					
					for (int j = 0; j < parm.getCount(); j++) {
						
						//add by yangjj 20150522打印执行单时只打印勾选了“打印”的医嘱
						if(!"Y".equals(parm.getValue("PRT", j))){
							continue;
						}
						
						
						if (bedNo.equals(parm.getValue("BED_NO", j))) {
							
							

								// 项目名称（包括名称、规格、备注）

								if (parm.getValue("SPECIFICATION", j) == null
										|| "".equals(parm.getValue(
												"SPECIFICATION", j).trim())) {
									tableParm.addData("Program_Name", parm
											.getValue("ORDER_DESC", j));
									// + "	"
									// + parm.getValue("SPECIFICATION", j));
									// + "	" + parm.getValue("DR_NOTE", j));

								} else {
									tableParm.addData("Program_Name", parm
											.getValue("ORDER_DESC", j));
									// + "("
									// + parm.getValue("SPECIFICATION", j)
									// + ")" + parm.getValue("DR_NOTE", j));

								}

								tableParm.addData("LINK_NO", parm.getValue(
										"LINK_NO", j));
								tableParm.addData("DR_NOTE", parm.getValue(
										"DR_NOTE", j));

								// 用量
								tableParm.addData("MEDI_QTY", parm.getValue(
										"MEDI_QTY", j)
										+ "	 "
										+ unitMap.get(parm.getValue(
												"MEDI_UNIT", j)));
								// 用法
								tableParm.addData("ROUTE_CODE", routeMap
										.get(parm.getValue("ROUTE_CODE", j)));

								// 开药医生
								tableParm
										.addData("ORDER_DR_CODE", personMap
												.get(parm.getValue(
														"ORDER_DR_CODE", j)));

								// //频次
								// printParm.addData("FREQ_CODE", parm.getValue(
								// "FREQ_CODE", j));

								// 执行护士
								// personMap.get(parm.getValue("NS_EXEC_CODE",
								// j))
								tableParm.addData("NS_EXEC_CODE", "");

								// 护士执行时间 parm.getValue("NS_EXEC_DATE", j)
								tableParm.addData("NS_EXEC_DATE", "");
							
							
							
//							System.out.println("tableParm========"+parm);
							infoParm = getPatInfo(parm.getValue("MR_NO", j));
							String birthday = "";
							if(infoParm.getValue("BIRTH_DATE",0).length()>0)
								birthday = infoParm.getValue("BIRTH_DATE",0).replaceAll("-", "/").substring(0, 10);
//							System.out.println("姓名 "+parm.getValue("PAT_NAME", j)+"  性别"+infoParm.getValue("SEX")+"   出生日期"+birthday+ "   病案号"+
//									parm.getValue("MR_NO", j)+"    住院号"+parm.getValue("IPD_NO", j)+"    床号"+parm.getValue("BED_NO", j)+"");
							printParm.setData("filePatName", "TEXT", parm.getValue("PAT_NAME", j));//病患姓名
							printParm.setData("fileSex", "TEXT", infoParm.getValue("SEX",0));//病患性别
							printParm.setData("fileBirthday", "TEXT", birthday);//病患出生年月
							printParm.setData("AGE", "TEXT", com.javahis.util.DateUtil.showAge(
									stringToTimestamp(infoParm.getValue("BIRTH_DATE",0)), SystemTool.getInstance().getDate()));//病患年龄
							printParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO", j));//病患病案号
							printParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", parm.getValue("IPD_NO", j));//病患住院号
							printParm.setData("BED_NO", "TEXT", parm.getValue("BED_NO", j));//病患床号
							// DISPENSE_FLG;BED_NO;PAT_NAME;LINKMAIN_FLG;URGENT_FLG;LINK_NO;ORDER_DESC_AND_SPECIFICATION;MEDI_QTY;
							// MEDI_UNIT;DISPENSE_QTY;DISPENSE_UNIT;FREQ_CODE;ROUTE_CODE;EXE_FLG;PRT;ORDER_DATE;DC_DATE;DR_NOTE;NS_EXEC_CODE;
							// NS_EXEC_DATE;NS_EXEC_DATE_TIME;ORDER_DR_CODE;DC_DR_CODE;NS_EXEC_DC_CODE;NS_EXEC_DC_DATE_DAY;NS_EXEC_DC_DATE_TIME;
							// MR_NO;IPD_NO;CASE_NO;ORDER_NO;ORDER_CODE
						} else {
							getPrintParm(tableParm);
//							System.out.println("tableparm=--------------"+ tableParm);
							printParm.setData("TABLE", tableParm.getData());
//							System.out.println("printparm=--------------"+ printParm);
							
							
							String previewSwitch=IReportTool.getInstance().getPrintSwitch("INWOrderExecMainPrtL.previewSwitch");
							if(previewSwitch.equals(IReportTool.ON)){
								this.openPrintDialog(IReportTool.getInstance()
										.getReportPath("INWOrderExecMainPrtL.jhw"),
										IReportTool.getInstance().getReportParm(
												"INWOrderExecMainPrtL.class",
												printParm));

							}else{
								this.openPrintDialog(IReportTool.getInstance()
										.getReportPath("INWOrderExecMainPrtL.jhw"),
										IReportTool.getInstance().getReportParm(
												"INWOrderExecMainPrtL.class",
												printParm),true);
							}
							
							tableParm = new TParm();
							printParm = new TParm();
							//打印上一个不同床号的病患信息后，取得当前床号的病患信息第一条
							
																				// start
								if (parm.getValue("SPECIFICATION", j) == null
										|| "".equals(parm.getValue(
												"SPECIFICATION", j).trim())) {
									tableParm
											.addData("Program_Name", parm
													.getValue("ORDER_DESC", j)
													+ "	"
													+ parm.getValue(
															"SPECIFICATION", j)
													+ "	"
													+ parm.getValue("DR_NOTE",
															j));

								} else {
									tableParm
											.addData("Program_Name", parm
													.getValue("ORDER_DESC", j)
													+ "("
													+ parm.getValue(
															"SPECIFICATION", j)
													+ ")"
													+ parm.getValue("DR_NOTE",
															j));

								}

								tableParm.addData("MEDI_QTY", parm.getValue(
										"MEDI_QTY", j)
										+ "	 "
										+ unitMap.get(parm.getValue(
												"MEDI_UNIT", j)));
								tableParm.addData("ROUTE_CODE", routeMap
										.get(parm.getValue("ROUTE_CODE", j)));
								tableParm
										.addData("ORDER_DR_CODE", personMap
												.get(parm.getValue(
														"ORDER_DR_CODE", j)));
								tableParm.addData("NS_EXEC_CODE", "");
								tableParm.addData("NS_EXEC_DATE", "");
							
							infoParm = getPatInfo(parm.getValue("MR_NO", j));
							String birthday = "";
							if(infoParm.getValue("BIRTH_DATE",0).length()>0)
								birthday = infoParm.getValue("BIRTH_DATE",0).replaceAll("-", "/").substring(0, 10);
							printParm.setData("filePatName", "TEXT", parm.getValue("PAT_NAME", j));//病患姓名
							printParm.setData("fileSex", "TEXT", infoParm.getValue("SEX",0));//病患性别
							printParm.setData("fileBirthday", "TEXT", birthday);//病患出生年月
							printParm.setData("AGE", "TEXT", com.javahis.util.DateUtil.showAge(
									stringToTimestamp(infoParm.getValue("BIRTH_DATE",0)), SystemTool.getInstance().getDate()));//病患年龄
							printParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO", j));//病患病案号
							printParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", parm.getValue("IPD_NO", j));//病患住院号
							printParm.setData("BED_NO", "TEXT", parm.getValue("BED_NO", j));//病患床号
						}

						bedNo = parm.getValue("BED_NO", j);
						
						if (j == parm.getCount() - 1) {
							getPrintParm(tableParm);
							
							printParm.setData("TABLE", tableParm.getData());
							String previewSwitch=IReportTool.getInstance().getPrintSwitch("INWOrderExecMainPrtL.previewSwitch");
							if(previewSwitch.equals(IReportTool.ON)){
								this.openPrintDialog(IReportTool.getInstance()
										.getReportPath("INWOrderExecMainPrtL.jhw"),
										IReportTool.getInstance().getReportParm(
												"INWOrderExecMainPrtL.class",
												printParm));
							}else{
								this.openPrintDialog(IReportTool.getInstance()
										.getReportPath("INWOrderExecMainPrtL.jhw"),
										IReportTool.getInstance().getReportParm(
												"INWOrderExecMainPrtL.class",
												printParm),true);
							}
							
						}

					}

				}

			}
			以下为长期医嘱打操作
			if (ord1UD.isSelected()) {// 长期医嘱执行单打印   分四类：口服、膳食、输液、治疗（非口服输液膳食的都算做治疗）
				
				TParm empty = new TParm();//用于置空表格
				empty.setData("Visible", false);
				
				TParm parm =  masterTbl.getParmValue();//界面表格数据parm
				
				TParm printParm = new TParm();//打印parm
				
				TParm tableParm = new TParm();//储存打印表格数据
				
				TParm infoParm = new TParm();//病患性别、出生日期parm
				
				TParm parmKF = new TParm();
				TParm parmSY = new TParm();
				TParm parmWY = new TParm();
				TParm parmZL = new TParm();
				TParm parmXZL = new TParm();
				boolean kf=false;
				boolean sy=false;
				boolean wy=false;
				boolean zl=false;
				boolean xzl=false;
				if(parm.getCount()>0){
					
					String bedNo = parm.getValue("BED_NO", 0);
					
//					for (int i = 0; i < parm.getCount(); i++) {
//						if(bedNo.equals(parm.getValue("BED_NO", i))){
//							if(!"PHA".equals(parm.getValue("CAT1_TYPE", i)) && 
//									parm.getValue("ORDER_CODE", i).toUpperCase().startsWith("SDK")){//是长期膳食单的先拿出打印
//								getTableParm(parm, tableParm, i);
//								getPrintCParm(tableParm);
//							}else{
//								if(tableParm.getCount()<=0){
//									empty.setData("Visible", false);
//									printParm.setData("ZL", "TEXT", "");
//									printParm.setData("TABLE_ZL", empty.getData());
//								}
//							}
//						}
//					}
					
					for (int i = 0; i < parm.getCount(); i++) {
						
						//add by yangjj 20150522打印执行单时只打印勾选了“打印”的医嘱
						this.messageBox(""+parm.getValue("PRT", i));
						if(!"Y".equals(parm.getValue("PRT", i))){
							continue;
						}
						
						
						if (bedNo.equals(parm.getValue("BED_NO", i))){
//							//分为药品(CAT1_TYPE=='PHA')：分为口服(DOSE_TYPE=='O')和输液(DOSE_TYPE=='I')、膳食(ORDER_CODE前3位为SDK)、其他
//							if(parm.getValue("ORDER_CODE", i) != null || !"".equals(parm.getValue("ORDER_CODE", i))){
//								//膳食：医院暂时没有项目分类CAT1_TYPE的值、先暂时用ORDER_CODE前3位为SDK的条件判断
//								if(parm.getValue("ORDER_CODE", i).toUpperCase().startsWith("SDK")){
//									TParm parmKF = new TParm();
//									getTableParm(parm, parmKF, i);
//								}else{
//									printParm.setData("SS", "TEXT", "");
//									printParm.setData("TABLE_SS", empty.getData());//置空膳食表格
//								}
//							}
							if("PHA".equals(parm.getValue("CAT1_TYPE", i))){
								
								//modify by yangjj 20150506
								if("O".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期口服单
								//if("O".equals(parm.getValue("DOSE_TYPE", i))){//长期口服单
  								    printParm.setData("KF", "TEXT", "长期口服单");
									
								        kf=true;
									
									getTableParm(parm, parmKF, i);
									
//									printParm.setData("TABLE_KF", parmKF.getData());
							}//	else{
//									if(parmKF.getCount()<=0){
//										printParm.setData("KF", "TEXT", "");
//										printParm.setData("TABLE_KF", empty.getData());//置空口服表格
//									}
//								}
								
								//modify by yangjj 20150506
								if("I".equals(parm.getValue("CLASSIFY_TYPE", i)) || "F".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期输液单
								//if("I".equals(parm.getValue("DOSE_TYPE", i)) || "F".equals(parm.getValue("DOSE_TYPE", i))){//长期输液单
									printParm.setData("SY", "TEXT", "长期输液单");
										
								        sy=true;
									
									getTableParm(parm, parmSY, i);
//									getPrintCParm(parmSY);
//									printParm.setData("TABLE_SY", parmSY.getData());
								}	
//								}else{
//									if(parmSY.getCount()<=0){
//										printParm.setData("SY", "TEXT", "");
//										printParm.setData("TABLE_SY", empty.getData());//置空输液表格
//									}
//								}
								
								//modify by yangjj 20150506
								if("E".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期外用单
								//if("E".equals(parm.getValue("DOSE_TYPE", i))){//长期外用单
										
								        wy=true;
									
									printParm.setData("WY", "TEXT", "长期外用单");
									getTableParm(parm, parmWY, i);
//									getPrintCParm(parmWY);
//									printParm.setData("TABLE_WY", parmWY.getData());
								}
//								}else{
//									if(parmWY.getCount()<=0){
//										printParm.setData("WY", "TEXT", "");
//										printParm.setData("TABLE_WY", empty.getData());//置空外用表格
//									}
//								}
//								if(parmZL.getCount()<=0){
//									printParm.setData("ZL", "TEXT", "");
//									printParm.setData("TABLE_ZL", empty.getData());//置空治疗表格
//								}
							}	
//							}else{
//								if(parmKF.getCount()<=0){
//									printParm.setData("TABLE_KF", empty.getData());
//								}
//								if(parmSY.getCount()<=0){
//									printParm.setData("TABLE_SY", empty.getData());
//								}
//								if(parmWY.getCount()<=0){
//									printParm.setData("TABLE_WY", empty.getData());
//								}
								
								
								 新增小治疗
								 
								if("TRT".equals(parm.getValue("CAT1_TYPE", i))){
											
								        zl=true;
									
									printParm.setData("ZL", "TEXT", "长期治疗单");
									getTableParm(parm,  parmZL, i);
//									getPrintCParm(parmZL);
//									printParm.setData("TABLE_ZL", parmZL.getData());
								}else{
									if(!"PHA".equals(parm.getValue("CAT1_TYPE", i))){//非药嘱
//										if(!"PHA".equals(parm.getValue("CAT1_TYPE", i)) && 
//												!parm.getValue("ORDER_CODE", i).toUpperCase().startsWith("SDK")){//非药嘱的非膳食医嘱
											
										
									        xzl=true;
										
										    printParm.setData("XZL", "TEXT", "其它类");
											getTableParm(parm,  parmXZL, i);
//											getPrintCParm(parmZL);
//											printParm.setData("TABLE_ZL", parmZL.getData());
//										}else{
//											if(parmZL.getCount()<=0){
//												printParm.setData("ZL", "TEXT", "");
//												printParm.setData("TABLE_ZL", empty.getData());//置空治疗表格
//											}
										}
									
								}	
								
//							}
							
							
							
							infoParm = getPatInfo(parm.getValue("MR_NO", i));
							String birthday = "";
							if(infoParm.getValue("BIRTH_DATE",0).length()>0)
								birthday = infoParm.getValue("BIRTH_DATE",0).replaceAll("-", "/").substring(0, 10);
//							System.out.println("姓名 "+parm.getValue("PAT_NAME", j)+"  性别"+infoParm.getValue("SEX")+"   出生日期"+birthday+ "   病案号"+
//									parm.getValue("MR_NO", j)+"    住院号"+parm.getValue("IPD_NO", j)+"    床号"+parm.getValue("BED_NO", j)+"");
							printParm.setData("filePatName", "TEXT", parm.getValue("PAT_NAME", i));//病患姓名
							printParm.setData("fileSex", "TEXT", infoParm.getValue("SEX",0));//病患性别
							printParm.setData("fileBirthday", "TEXT", birthday);//病患出生年月
							printParm.setData("AGE", "TEXT", com.javahis.util.DateUtil.showAge(
									stringToTimestamp(infoParm.getValue("BIRTH_DATE",0)), SystemTool.getInstance().getDate()));//病患年龄
							printParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO", i));//病患病案号
							printParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", parm.getValue("IPD_NO", i));//病患住院号
							printParm.setData("BED_NO", "TEXT", parm.getValue("BED_NO", i));//病患床号
						}else{
//							System.out.println("printParm -------- : "+printParm);
						String previewSwitch=IReportTool.getInstance().getPrintSwitch("INWOrderExecMainPrtC.previewSwitch");
							
							if(previewSwitch.equals(IReportTool.ON)){
								this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
										IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm));
							}else{
								this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
										IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm),true);
							}
						
							tableParm = new TParm();
							printParm = new TParm();
							parmKF = new TParm();
							parmSY = new TParm();
							parmWY = new TParm();
							parmZL = new TParm();
							
							if("PHA".equals(parm.getValue("CAT1_TYPE", i))){
								//modify by yangjj 20150506
								if("O".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期口服单
								//if("O".equals(parm.getValue("DOSE_TYPE", i))){//长期口服单
									printParm.setData("KF", "TEXT", "长期口服单");
									getTableParm(parm, parmKF, i);
									getPrintCParm(parmKF);
									printParm.setData("TABLE_KF", parmKF.getData());
								}else{
									if(parmKF.getCount()<=0){
										printParm.setData("KF", "TEXT", "");
										printParm.setData("TABLE_KF", empty.getData());//置空口服表格
									}
								}
								
								//modify by yangjj 20150506
								if("I".equals(parm.getValue("CLASSIFY_TYPE", i)) || "F".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期输液单
								//if("I".equals(parm.getValue("DOSE_TYPE", i)) || "F".equals(parm.getValue("DOSE_TYPE", i))){//长期输液单
									printParm.setData("SY", "TEXT", "长期输液单");
									getTableParm(parm, parmSY, i);
									getPrintCParm(parmSY);
									printParm.setData("TABLE_SY", parmSY.getData());
								}else{
									if(parmSY.getCount()<=0){
										printParm.setData("SY", "TEXT", "");
										printParm.setData("TABLE_SY", empty.getData());//置空输液表格
									}
								}
								
								//modify by yangjj 20150506
								if("E".equals(parm.getValue("CLASSIFY_TYPE", i))){//长期外用单
								//if("E".equals(parm.getValue("DOSE_TYPE", i))){//长期外用单
									printParm.setData("WY", "TEXT", "长期外用单");
									getTableParm(parm, parmWY, i);
									getPrintCParm(parmWY);
									printParm.setData("TABLE_WY", parmWY.getData());
								}else{
									if(parmWY.getCount()<=0){
										printParm.setData("WY", "TEXT", "");
										printParm.setData("TABLE_WY", empty.getData());//置空外用表格
									}
								}
							}else{
								if(parmKF.getCount()<=0){
									printParm.setData("TABLE_KF", empty.getData());
								}
								if(parmSY.getCount()<=0){
									printParm.setData("TABLE_SY", empty.getData());
								}
								if(parmWY.getCount()<=0){
									printParm.setData("TABLE_WY", empty.getData());
								}
								if(!"PHA".equals(parm.getValue("CAT1_TYPE", i))){//非药嘱
//								if(!"PHA".equals(parm.getValue("CAT1_TYPE", i)) && 
//										!parm.getValue("ORDER_CODE", i).toUpperCase().startsWith("SDK")){//非药嘱的非膳食医嘱
									printParm.setData("ZL", "TEXT", "长期治疗单");
									getTableParm(parm, parmZL, i);
									getPrintCParm(parmZL);
									printParm.setData("TABLE_ZL", parmZL.getData());
								}else{
									if(parmZL.getCount()<=0){
										printParm.setData("ZL", "TEXT", "");
										printParm.setData("TABLE_ZL", empty.getData());//置治疗服表格
									}
								}
							}
							
							
							infoParm = getPatInfo(parm.getValue("MR_NO", i));
							String birthday = "";
							if(infoParm.getValue("BIRTH_DATE",0).length()>0)
								birthday = infoParm.getValue("BIRTH_DATE",0).replaceAll("-", "/").substring(0, 10);
//							System.out.println("姓名 "+parm.getValue("PAT_NAME", j)+"  性别"+infoParm.getValue("SEX")+"   出生日期"+birthday+ "   病案号"+
//									parm.getValue("MR_NO", j)+"    住院号"+parm.getValue("IPD_NO", j)+"    床号"+parm.getValue("BED_NO", j)+"");
							printParm.setData("filePatName", "TEXT", parm.getValue("PAT_NAME", i));//病患姓名
							printParm.setData("fileSex", "TEXT", infoParm.getValue("SEX",0));//病患性别
							printParm.setData("fileBirthday", "TEXT", birthday);//病患出生年月
							printParm.setData("AGE", "TEXT", com.javahis.util.DateUtil.showAge(
									stringToTimestamp(infoParm.getValue("BIRTH_DATE",0)), SystemTool.getInstance().getDate()));//病患年龄
							printParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO", i));//病患病案号
							printParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", parm.getValue("IPD_NO", i));//病患住院号
							printParm.setData("BED_NO", "TEXT", parm.getValue("BED_NO", i));//病患床号
							
						}
						
						
						bedNo = parm.getValue("BED_NO", i);

						if (i == parm.getCount() - 1) {
//							System.out.println("printParm -------- : "+printParm);
							
//							String previewSwitch=IReportTool.getInstance().getPrintSwitch("INWOrderExecMainPrtC.previewSwitch");
//							
//							if(previewSwitch.equals(IReportTool.ON)){
//								this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
//										IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm));
//							}else{
//								this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
//										IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm),true);
//							}
							
						}
					}

			     
					
//					if(parmKF.getCount()!=0){
//						System.out.println("进入了汇总方法");
//						
//						tableTitle=setTableTitle(tableTitle,"长期口服单");
//						System.out.println("tableTitle的值：====="+tableTitle);
//						allParm.addParm(tableTitle);
//						allParm.addParm(parmKF);
//					}
					
					getPrintCParm(parmKF);
					getPrintCParm(parmSY);
					getPrintCParm(parmWY);
					getPrintCParm(parmXZL);
					getPrintCParm(parmZL);
					
			/*		if(!kf){
						for(int i=0;i<parmKF.getCount();i++){
						   allParm.addData("Title",parmKF.getValue("Title",i));
						   allParm.addData("Program_Name",parmKF.getValue("Program_Name",i));
						   allParm.addData("MEDI_QTY",parmKF.getValue("MEDI_QTY",i));
						   allParm.addData("ROUTE_CODE",parmKF.getValue("ROUTE_CODE",i));
						   allParm.addData("ORDER_DR_CODE",parmKF.getValue("ORDER_DR_CODE",i));
						   allParm.addData("FREQ_CODE",parmKF.getValue("FREQ_CODE",i));
						   allParm.addData("NS_EXEC_CODE",parmKF.getValue("NS_EXEC_CODE",i));
						   allParm.addData("NS_EXEC_DATE",parmKF.getValue("NS_EXEC_DATE",i));
						   System.out.println("========================进入赋值循环=====================================");
						}  
						System.out.println("========================出赋值循环=====================================");
						   allParm.addData("Title","");
						   allParm.addData("Program_Name","");
						   allParm.addData("MEDI_QTY","");
						   allParm.addData("ROUTE_CODE","");
						   allParm.addData("ORDER_DR_CODE","");
						   allParm.addData("FREQ_CODE","");
						   allParm.addData("NS_EXEC_CODE","");
						   allParm.addData("NS_EXEC_DATE","");
					}
					if(!sy){
						for(int i=0;i<parmSY.getCount();i++){
						   allParm.addData("Title",parmSY.getValue("Title",i));
						   allParm.addData("Program_Name",parmSY.getValue("Program_Name",i));
						   allParm.addData("MEDI_QTY",parmSY.getValue("MEDI_QTY",i));
						   allParm.addData("ROUTE_CODE",parmSY.getValue("ROUTE_CODE",i));
						   allParm.addData("ORDER_DR_CODE",parmSY.getValue("ORDER_DR_CODE",i));
						   allParm.addData("FREQ_CODE",parmSY.getValue("FREQ_CODE",i));
						   allParm.addData("NS_EXEC_CODE",parmSY.getValue("NS_EXEC_CODE",i));
						   allParm.addData("NS_EXEC_DATE",parmSY.getValue("NS_EXEC_DATE",i));
						}  
						   allParm.addData("Title","");
						   allParm.addData("Program_Name","");
						   allParm.addData("MEDI_QTY","");
						   allParm.addData("ROUTE_CODE","");
						   allParm.addData("ORDER_DR_CODE","");
						   allParm.addData("FREQ_CODE","");
						   allParm.addData("NS_EXEC_CODE","");
						   allParm.addData("NS_EXEC_DATE","");
					}
					if(!wy){
						for(int i=0;i<parmWY.getCount();i++){
						   allParm.addData("Title",parmWY.getValue("Title",i));
						   allParm.addData("Program_Name",parmWY.getValue("Program_Name",i));
						   allParm.addData("MEDI_QTY",parmWY.getValue("MEDI_QTY",i));
						   allParm.addData("ROUTE_CODE",parmWY.getValue("ROUTE_CODE",i));
						   allParm.addData("ORDER_DR_CODE",parmWY.getValue("ORDER_DR_CODE",i));
						   allParm.addData("FREQ_CODE",parmWY.getValue("FREQ_CODE",i));
						   allParm.addData("NS_EXEC_CODE",parmWY.getValue("NS_EXEC_CODE",i));
						   allParm.addData("NS_EXEC_DATE",parmWY.getValue("NS_EXEC_DATE",i));
						}  
						   allParm.addData("Title","");
						   allParm.addData("Program_Name","");
						   allParm.addData("MEDI_QTY","");
						   allParm.addData("ROUTE_CODE","");
						   allParm.addData("ORDER_DR_CODE","");
						   allParm.addData("FREQ_CODE","");
						   allParm.addData("NS_EXEC_CODE","");
						   allParm.addData("NS_EXEC_DATE","");
					}
					if(!xzl){
						for(int i=0;i<parmXZL.getCount();i++){
						   allParm.addData("Title",parmXZL.getValue("Title",i));
						   allParm.addData("Program_Name",parmXZL.getValue("Program_Name",i));
						   allParm.addData("MEDI_QTY",parmXZL.getValue("MEDI_QTY",i));
						   allParm.addData("ROUTE_CODE",parmXZL.getValue("ROUTE_CODE",i));
						   allParm.addData("ORDER_DR_CODE",parmXZL.getValue("ORDER_DR_CODE",i));
						   allParm.addData("FREQ_CODE",parmXZL.getValue("FREQ_CODE",i));
						   allParm.addData("NS_EXEC_CODE",parmXZL.getValue("NS_EXEC_CODE",i));
						   allParm.addData("NS_EXEC_DATE",parmXZL.getValue("NS_EXEC_DATE",i));
						}  
						   allParm.addData("Title","");
						   allParm.addData("Program_Name","");
						   allParm.addData("MEDI_QTY","");
						   allParm.addData("ROUTE_CODE","");
						   allParm.addData("ORDER_DR_CODE","");
						   allParm.addData("FREQ_CODE","");
						   allParm.addData("NS_EXEC_CODE","");
						   allParm.addData("NS_EXEC_DATE","");
					}
					if(!zl){
						for(int i=0;i<parmZL.getCount();i++){
						   allParm.addData("Title",parmZL.getValue("Title",i));
						   allParm.addData("Program_Name",parmZL.getValue("Program_Name",i));
						   allParm.addData("MEDI_QTY",parmZL.getValue("MEDI_QTY",i));
						   allParm.addData("ROUTE_CODE",parmZL.getValue("ROUTE_CODE",i));
						   allParm.addData("ORDER_DR_CODE",parmZL.getValue("ORDER_DR_CODE",i));
						   allParm.addData("FREQ_CODE",parmZL.getValue("FREQ_CODE",i));
						   allParm.addData("NS_EXEC_CODE",parmZL.getValue("NS_EXEC_CODE",i));
						   allParm.addData("NS_EXEC_DATE",parmZL.getValue("NS_EXEC_DATE",i));
						}  
						 allParm.addData("Title","");
						   allParm.addData("Program_Name","");
						   allParm.addData("MEDI_QTY","");
						   allParm.addData("ROUTE_CODE","");
						   allParm.addData("ORDER_DR_CODE","");
						   allParm.addData("FREQ_CODE","");
						   allParm.addData("NS_EXEC_CODE","");
						   allParm.addData("NS_EXEC_DATE","");
					}
					*/
					
//					getPrintCParm(parmKF);
//					getPrintCParm(parmSY);
//					getPrintCParm(parmWY);
//					getPrintCParm(parmXZL);
//					getPrintCParm(parmZL);
//					getPrintCParm(allParm);
//					printParm.setData("TABLE_KF",allParm.getData());
//					if(kf){
//						printParm.setData("TABLE_KF", parmKF.getData());
//					}
//					if(!kf&&sy){
//						printParm.setData("TABLE_KF", parmSY.getData());
//					}else if(kf&&sy){
//						printParm.setData("TABLE_SY", parmSY.getData());
//					}
//					if()
					
				
					
//					if(kf&&sy&&wy&&xzl&&zl){
//						printParm.setData("TABLE_KF", parmKF.getData());
//						printParm.setData("TABLE_SY", parmSY.getData());
//						printParm.setData("TABLE_WY", parmWY.getData());
//						printParm.setData("TABLE_ZL", parmZL.getData());
//						printParm.setData("TABLE_XZL", parmXZL.getData());
//					}else if(sy&&wy&&xzl&&zl){
//						printParm.setData("TABLE_KF", parmSY.getData());
//						printParm.setData("TABLE_SY", parmWY.getData());
//						printParm.setData("TABLE_WY", parmZL.getData());
//						printParm.setData("TABLE_ZL", parmXZL.getData());
//					}else if(wy&&xzl&&zl){
//						printParm.setData("TABLE_KF", parmWY.getData());
//						printParm.setData("TABLE_SY", parmZL.getData());
//						printParm.setData("TABLE_WY", parmXZL.getData());
//					}else if(xzl&&zl){
//						printParm.setData("TABLE_KF", parmZL.getData());
//						printParm.setData("TABLE_SY", parmXZL.getData());
//					}else if(zl){
//						printParm.setData("TABLE_KF", parmZL.getData());
//					}
					
					
					
	//				  判断各表是否有数据，按顺序打印 addBy ZhangZe 20150130
					 
					/*int tables=0;
					if(kf){
						printParm.setData("KF", "TEXT", "长期口服单");
						printParm.setData("TABLE_KF", parmKF.getData());
						tables=1;
						kf=false;
					}else if(sy){
						printParm.setData("KF", "TEXT", "长期输液单");
						printParm.setData("TABLE_KF", parmSY.getData());
						tables=1;
						sy=false; 
					}else if(zl){
						printParm.setData("KF", "TEXT", "长期治疗单");
						printParm.setData("TABLE_KF", parmZL.getData());
						tables=1;
						zl=false;
					}else if(wy){
						printParm.setData("KF", "TEXT", "长期外用单");
						printParm.setData("TABLE_KF", parmWY.getData());
						tables=1;
						wy=false;
					}else if(xzl){
						printParm.setData("KF", "TEXT", "其他类");
						printParm.setData("TABLE_KF", parmXZL.getData());
						tables=1;
						xzl=false;
					}
					
					if(sy){
						printParm.setData("SY", "TEXT", "长期输液单");
						printParm.setData("TABLE_SY", parmSY.getData());
                        tables=2;
						sy=false; 
					}else if(zl){
						printParm.setData("SY", "TEXT", "长期治疗单");
						printParm.setData("TABLE_SY", parmZL.getData());
						tables=2;
						zl=false;
					}else if(wy){
						printParm.setData("SY", "TEXT", "长期外用单");
						printParm.setData("TABLE_SY", parmWY.getData());
						tables=2;
						wy=false;
					}else if(xzl){
						printParm.setData("SY", "TEXT", "其他类");
						printParm.setData("TABLE_SY", parmXZL.getData());
						tables=2;
						xzl=false;
					}
					
					if(zl){
						printParm.setData("WY", "TEXT", "长期治疗单");
						printParm.setData("TABLE_WY", parmZL.getData());
						tables=3;
						zl=false;
					}else if(wy){
						printParm.setData("WY", "TEXT", "长期外用单");
						printParm.setData("TABLE_WY", parmWY.getData());
						tables=3;
						wy=false;
					}else if(xzl){
						printParm.setData("WY", "TEXT", "其他类");
						printParm.setData("TABLE_WY", parmXZL.getData());
						tables=3;
						xzl=false;
					}
					
				    if(wy){
				    	printParm.setData("ZL", "TEXT", "长期外用单");
						printParm.setData("TABLE_ZL", parmWY.getData());
						tables=4;
						wy=false;
					}else if(xzl){
						printParm.setData("ZL", "TEXT", "其他类");
						printParm.setData("TABLE_ZL", parmXZL.getData());
						tables=4;
						xzl=false;
					}
					
					if(xzl){
						printParm.setData("XZL", "TEXT", "其他类");
						printParm.setData("TABLE_XZL", parmXZL.getData());
						tables=5;
						xzl=false;
					}
					if(tables==4){
						printParm.setData("XZL", "TEXT", "");
					    printParm.setData("TABLE_XZL", empty.getData());
					}
					if(tables==3){
						 printParm.setData("ZL", "TEXT", "");
						 printParm.setData("XZL", "TEXT", "");
						 printParm.setData("TABLE_XZL", empty.getData());
						 printParm.setData("TABLE_ZL", empty.getData());
					}
					if(tables==2){
						 printParm.setData("XZL", "TEXT", "");
						 printParm.setData("ZL", "TEXT", "");
						 printParm.setData("WY", "TEXT", "");
						 printParm.setData("TABLE_XZL", empty.getData());
						 printParm.setData("TABLE_ZL", empty.getData());
						 printParm.setData("TABLE_WY", empty.getData());
					}
					if(tables==1){
						 printParm.setData("XZL", "TEXT", "");
						 printParm.setData("ZL", "TEXT", "");
						 printParm.setData("WY", "TEXT", "");
						 printParm.setData("SY", "TEXT", "");
						 printParm.setData("TABLE_XZL", empty.getData());
						 printParm.setData("TABLE_ZL", empty.getData());
						 printParm.setData("TABLE_WY", empty.getData());
						 printParm.setData("TABLE_SY", empty.getData());
					}
					if(tables==0){
						 printParm.setData("XZL", "TEXT", "");
						 printParm.setData("ZL", "TEXT", "");
						 printParm.setData("WY", "TEXT", "");
						 printParm.setData("SY", "TEXT", "");
						 printParm.setData("KF", "TEXT", "");
						 printParm.setData("TABLE_XZL", empty.getData());
						 printParm.setData("TABLE_ZL", empty.getData());
						 printParm.setData("TABLE_WY", empty.getData());
						 printParm.setData("TABLE_SY", empty.getData());
						 printParm.setData("TABLE_KF", empty.getData());
					}
					
					
					
//					printParm.setData("TABLE_KF", parmKF.getData());
//					printParm.setData("TABLE_KF", parmSY.getData());
				
					String previewSwitch=IReportTool.getInstance().getPrintSwitch("INWOrderExecMainPrtC.previewSwitch");
					
					if(previewSwitch.equals(IReportTool.ON)){
						this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
								IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm));
					}else{
						this.openPrintDialog(IReportTool.getInstance().getReportPath("INWOrderExecMainPrtC.jhw"),
								IReportTool.getInstance().getReportParm("INWOrderExecMainPrtC.class",printParm),true);
					}
//					openPrintWindow("%ROOT%\\config\\prt\\inw\\INWOrderExecMainPrtC.jhw",printParm);
				}
			}
		} else {
//			messageBox("选择不正确");
			return;
		}
	}*/
	

	/**
	 * 临时医嘱执行单打印 add by lich 20140818
	 */
	private void getPrintParm(TParm parm) {

		parm.setCount(parm.getCount("MEDI_QTY"));
		
		parm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		
		parm.addData("SYSTEM", "COLUMNS", "Program_Name");

		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");

		parm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");

		parm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
		
		parm.addData("SYSTEM", "COLUMNS", "DR_NOTE");

		// parm.setData("SYSTEM", "COLUMNS", "FREQ_CODE");

		parm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");

		parm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");

	}
	/**
	 * 长期医嘱执行单打印 add by lich 20140818
	 */
	private void getPrintCParm(TParm parm) {
		
		parm.setCount(parm.getCount("MEDI_QTY"));
		
//		parm.addData("SYSTEM", "COLUMNS", "Title");
		
		parm.addData("SYSTEM", "COLUMNS", "LINK_NO");
		
		parm.addData("SYSTEM", "COLUMNS", "Program_Name");
		
		parm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		
		parm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
		
		parm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		
		parm.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		
		parm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
		
		parm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
		
	}

	
	/**
	 * 储存用药单位中文对照名称add by lich 20140818
	 */
	private Map returnUnitMap(){
		Map map = new HashMap();
		String sql = "SELECT UNIT_CODE, UNIT_CHN_DESC FROM SYS_UNIT ";
		TParm unitResult = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < unitResult.getCount(); i++) {
			map.put(unitResult.getValue("UNIT_CODE", i), unitResult.getValue("UNIT_CHN_DESC", i));
		}
		return map;
	}
	/**
	 * 储存人员中文对照名称add by lich 20140818
	 */
	private Map returnPersonMap(){
		Map map = new HashMap();
		String sql = "SELECT USER_ID, USER_NAME FROM SYS_OPERATOR";
		TParm personResult = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < personResult.getCount(); i++) {
			map.put(personResult.getValue("USER_ID", i), personResult.getValue("USER_NAME", i));
		}
		return map;
	}
	/**
	 * 储存用法中文对照名称add by lich 20140818
	 */
	private Map returnRouteMap(){
		Map map = new HashMap();
		String sql = "SELECT ROUTE_CODE, ROUTE_CHN_DESC FROM SYS_PHAROUTE";
		TParm routeResult = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < routeResult.getCount(); i++) {
			map.put(routeResult.getValue("ROUTE_CODE", i), routeResult.getValue("ROUTE_CHN_DESC", i));
		}
		return map;
	}
	/**
	 * 储存频次中文对照名称add by lich 20140818
	 */
	private Map returnFreqMap(){
		Map map = new HashMap();
		String sql = "SELECT FREQ_CODE, FREQ_CHN_DESC FROM SYS_PHAFREQ";
		TParm routeResult = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < routeResult.getCount(); i++) {
			map.put(routeResult.getValue("FREQ_CODE", i), routeResult.getValue("FREQ_CHN_DESC", i));
		}
		return map;
	}
	/**
	 * 病患性别、出生日期add by lich 20140818
	 * @return parm
	 */
	private TParm getPatInfo(String mrNo) {
		String sql = "SELECT B.CHN_DESC SEX, A.BIRTH_DATE FROM SYS_PATINFO A, SYS_DICTIONARY B " +
				"WHERE B.GROUP_ID ='SYS_SEX' AND A.SEX_CODE = B.ID AND A.MR_NO ='"+mrNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	private Timestamp stringToTimestamp(String tsStr){
	    Timestamp ts = new Timestamp(System.currentTimeMillis());  
        try {  
        	tsStr = tsStr.substring(0, 19);
            ts = Timestamp.valueOf(tsStr);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ts;
	}
	private TParm getTableParm(TParm tableParm, TParm parmIn, int j){
		
		Map unitMap = returnUnitMap();//储存用药单位中文对照名称
		Map personMap = returnPersonMap();//储存人员中文对照名称
		Map routeMap = returnRouteMap();//储存用法中文对照名称
		Map freqMap = returnFreqMap();//储存频次中文对照名称
		
		// 项目名称（包括名称、规格、备注）
		
		if(tableParm.getValue("SPECIFICATION", j) == null ||"".equals(tableParm.getValue("SPECIFICATION", j).trim())){
			parmIn.addData("Program_Name", tableParm.getValue("ORDER_DESC", j));
//					+ "	" + 
//			tableParm.getValue("SPECIFICATION", j) + "	" + tableParm.getValue("DR_NOTE", j));
		}else{
			parmIn.addData("Program_Name", tableParm.getValue("ORDER_DESC", j) );
//					+ "(" + 
//			tableParm.getValue("SPECIFICATION", j) + ")" + tableParm.getValue("DR_NOTE", j));
		}
		//连打分组
		parmIn.addData("LINK_NO",tableParm.getValue("LINK_NO", j));
				
		
		// 用量
		parmIn.addData("MEDI_QTY", tableParm.getValue("MEDI_QTY", j) + "	 "+unitMap.get(tableParm.getValue("MEDI_UNIT", j)));
		
		// 用法
		parmIn.addData("ROUTE_CODE", 
				routeMap.get(tableParm.getValue("ROUTE_CODE", j)));

		// 开药医生
		parmIn.addData("ORDER_DR_CODE", personMap.get(tableParm.getValue("ORDER_DR_CODE", j)));

		//频次
//		parmIn.addData("FREQ_CODE", freqMap.get(tableParm.getValue("FREQ_CODE", j)));
		parmIn.addData("FREQ_CODE", tableParm.getValue("FREQ_CODE", j));//20150202 wangjingchun add
		
		parmIn.addData("DR_NOTE",tableParm.getValue("DR_NOTE", j));//医生备注zhangz20150130

		// 护士执行时间 parm.getValue("NS_EXEC_DATE", j)
		parmIn.addData("NS_EXEC_DATE", "");
		
		// 执行护士 personMap.get(parm.getValue("NS_EXEC_CODE", j))
		parmIn.addData("NS_EXEC_CODE", "");
		
		return parmIn;
	}
    private TParm getTableParm(TParm tableParm, TParm parmIn, int j,String title,String exe_code,String exe_date){
		
		Map unitMap = returnUnitMap();//储存用药单位中文对照名称
		Map personMap = returnPersonMap();//储存人员中文对照名称
		Map routeMap = returnRouteMap();//储存用法中文对照名称
		Map freqMap = returnFreqMap();//储存频次中文对照名称
		
		// 项目名称（包括名称、规格、备注）
		
		if(tableParm.getValue("SPECIFICATION", j) == null ||"".equals(tableParm.getValue("SPECIFICATION", j).trim())){
			parmIn.addData("Program_Name", tableParm.getValue("ORDER_DESC", j) + "	" + 
			tableParm.getValue("SPECIFICATION", j) + "	" + tableParm.getValue("DR_NOTE", j));
		}else{
			parmIn.addData("Program_Name", tableParm.getValue("ORDER_DESC", j) + "(" + 
			tableParm.getValue("SPECIFICATION", j) + ")" + tableParm.getValue("DR_NOTE", j));
		}
		
		parmIn.addData("Title", title);
		
		// 用量
		parmIn.addData("MEDI_QTY", tableParm.getValue("MEDI_QTY", j) + "	 "+unitMap.get(tableParm.getValue("MEDI_UNIT", j)));
		
		// 用法
		parmIn.addData("ROUTE_CODE", routeMap.get(tableParm.getValue("ROUTE_CODE", j)));

		// 开药医生
		parmIn.addData("ORDER_DR_CODE", personMap.get(tableParm.getValue("ORDER_DR_CODE", j)));

		//频次
//		parmIn.addData("FREQ_CODE", freqMap.get(tableParm.getValue("FREQ_CODE", j)));
		parmIn.addData("FREQ_CODE", tableParm.getValue("FREQ_CODE", j));//20150202 wangjingchun add
		
		//备注
		
		parmIn.addData("DR_NOTE",tableParm.getValue("DR_NOTE", j));

		// 护士执行时间 parm.getValue("NS_EXEC_DATE", j)
		parmIn.addData("NS_EXEC_DATE", exe_date);
		
		// 执行护士 personMap.get(parm.getValue("NS_EXEC_CODE", j))
		parmIn.addData("NS_EXEC_CODE", exe_code);
		
		return parmIn;
	}
    private TParm setTableTitle(TParm tableTitle,String str){
        tableTitle.setData("Titie", str);
		tableTitle.setData("Program_Name", "项目名称");
        tableTitle.setData("MEDI_QTY", "用量");
        tableTitle.setData("ROUTE_CODE", "用法");
        tableTitle.setData("ORDER_DR_CODE", "医生");
        tableTitle.setData("FREQ_CODE", "频次");
        tableTitle.setData("NS_EXEC_CODE", "执行时间");
        tableTitle.setData("NS_EXEC_DATE", "执行人");
        return tableTitle;					    
    }
    
    /**
	 * 
	 * 添加打印输血单 2017-11-08
	 * zhanglei add 添加打印输血单
	 */
	public void onPrintBloodTransfusion(){
		int n = 0;
//		int row1 = masterTbl.getSelectedRow();
//		this.messageBox("第" + row1);
//		if (row1 < 0) {
//            this.messageBox("E0134");
//            return;
//        }
		//获得当前日期
    	Timestamp sysDate = SystemTool.getInstance().getDate();
//    	TParm parmRow = masterTbl.getParmValue().getRow(row1);
    	TParm parmRow = masterTbl.getParmValue();
//    	if(!getChargeHospCode(parmRow.getValue("ORDER_CODE")).equals("RA")){
//    		this.messageBox("非血类医嘱" + parmRow.getValue("PRT"));
//    		return;
//    	}
//		System.out.println("parmRow6666666666" + masterTbl.getParmValue());
		 String sex = "";
    	 if(outsideParm.getValue("ODI","SEX_CODE").equals("1")){
    		 sex="男";
    	 }else if(outsideParm.getValue("ODI","SEX_CODE").equals("2")){
    		 sex="女";
    	 }else{
    		 sex="未知";
    	 }
    	 String a = parmRow.getValue("ORDER_DATE",0);//得到执行日期
    	 String riqi = a.substring(0,4) + "年" + a.substring(5,7) + "月" + a.substring(8,10) + "日";
		 TParm printTParm = new TParm();
		 printTParm.setData("bingqu", "TEXT",getStationDesc(parmRow.getValue("STATION_CODE",0)));//病区
		 printTParm.setData("chuanghao", "TEXT",outsideParm.getValue("ODI","BED_NO"));//床号
		 printTParm.setData("tiaoma", "TEXT",this.getValueString("MR_NO"));//条码
		 printTParm.setData("xingming", "TEXT",outsideParm.getValue("ODI","PAT_NAME"));//姓名
		 printTParm.setData("xingbie", "TEXT",sex);//性别
		 printTParm.setData("nianling", "TEXT",OdiUtil.getInstance().showAge(getgetBirthDay(),sysDate));//年龄
		 printTParm.setData("zhuyuanhao", "TEXT",outsideParm.getValue("ODI","MR_NO"));//病案号
		 printTParm.setData("riqi", "TEXT",riqi);//输血日期
		 printTParm.setData("xuexing", "TEXT",getDoctor().getValue("BLOOD_TYPE",0));//血型
		 printTParm.setData("RH", "TEXT",getDoctor().getValue("BLOOD_RH_TYPE",0));//RH
//		 printTParm.setData("yisheng", "TEXT",getDoctor(parmRow.getValue("ORDER_DR_CODE",0)));//医生
////		 printTParm.setData("pinci", "TEXT",parmRow.getValue("FREQ_CODE"));//频次 getFreq
////		 printTParm.setData("pinci", "TEXT",getFreq(parmRow.getValue("FREQ_CODE",0)));//频次
//		 printTParm.setData("yizhu", "TEXT",parmRow.getValue("ORDER_DESC",0));//医嘱
//		 printTParm.setData("xuexing", "TEXT",getDoctor().getValue("BLOOD_TYPE", 0));//血型
//		 printTParm.setData("RH", "TEXT",getDoctor().getValue("BLOOD_RH_TYPE", 0));//RH
////		 printTParm.setData("xueliang", "TEXT",parmRow.getValue("MEDI_QTY"));//血量
//		 printTParm.setData("xueliang", "TEXT",parmRow.getInt("MEDI_QTY"));//血量
//		 printTParm.setData("danwei", "TEXT",OrderUtil.getInstance().getUnit(parmRow.getValue("DISPENSE_UNIT"))) ;//单位
////		 printTParm.setData("sulv", "TEXT",parmRow.getValue("INFLUTION_RATE"));//速率
//		 printTParm.setData("sulv", "TEXT",parmRow.getInt("INFLUTION_RATE"));//速率
//		 printTParm.setData("yongfa", "TEXT",OrderUtil.getInstance().getRoute(parmRow.getValue("ROUTE_CODE")));//用法
//		 printTParm.setData("zu", "TEXT",parmRow.getValue("LINK_NO"));//组
//		 printTParm.setData("zhixing", "TEXT",getDoctor(parmRow.getValue("NS_EXEC_CODE",0)));//执行
//		 printTParm.setData("shenhe", "TEXT", getDoctor(parmRow.getValue("OPT_USER",0)));//审核
//		 this.messageBox_(printTParm);
//		 this.messageBox("" + parmRow.getCount());
		 for(int i = 0 ; i < parmRow.getCount() ; i++){
			 if(parmRow.getValue("PRT",i).equals("Y")){
				    if(!getChargeHospCode(parmRow.getValue("ORDER_CODE",i)).equals("RA")){
			    		this.messageBox(parmRow.getValue("ORDER_DESC",i) + "是非血类医嘱");
			    		return;
			    	}
//				    this.messageBox(i + parmRow.getValue("ORDER_DESC",i));
//				    System.out.println(i + parmRow.getValue("ORDER_DESC",i));
				     printTParm.setData("yisheng", "TEXT",getDoctor(parmRow.getValue("ORDER_DR_CODE",i)));//医生
		//			 printTParm.setData("pinci", "TEXT",parmRow.getValue("FREQ_CODE"));//频次 getFreq
		//			 printTParm.setData("pinci", "TEXT",getFreq(parmRow.getValue("FREQ_CODE",0)));//频次
					 printTParm.setData("yizhu", "TEXT",parmRow.getValue("ORDER_DESC",i));//医嘱
		//			 printTParm.setData("xueliang", "TEXT",parmRow.getValue("MEDI_QTY"));//血量
					 printTParm.setData("xueliang", "TEXT",parmRow.getInt("MEDI_QTY",i));//血量
					 printTParm.setData("danwei", "TEXT",OrderUtil.getInstance().getUnit(parmRow.getValue("DISPENSE_UNIT",i))) ;//单位
		//			 printTParm.setData("sulv", "TEXT",parmRow.getValue("INFLUTION_RATE"));//速率
					 printTParm.setData("sulv", "TEXT",parmRow.getInt("INFLUTION_RATE",i));//速率
    				 printTParm.setData("pinci", "TEXT",getFreq(parmRow.getValue("FREQ_CODE",i)));//频次
					 printTParm.setData("yongfa", "TEXT",OrderUtil.getInstance().getRoute(parmRow.getValue("ROUTE_CODE",i)));//用法
					 printTParm.setData("zu", "TEXT",parmRow.getValue("LINK_NO",i));//组
					 printTParm.setData("zhixing", "TEXT",getDoctor(parmRow.getValue("NS_EXEC_CODE",i)));//执行
					 printTParm.setData("shenhe", "TEXT", getDoctor(parmRow.getValue("OPT_USER",i)));//审核
//					 System.out.println("==" + i + printTParm.getValue("yizhu","TEXT"));
				     openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSprintBloodTransfusion.jhw", printTParm,true);
				     n++;//计数查看是否n大于0
			 }
			 if(i+1 == parmRow.getCount()){
			     if(n <= 0){
			    	 this.messageBox("没有要打印的医嘱！");
			    	 return;
			     }
		    }
		 }
		
//	   	 printTParm.setData("TEXT1", "TEXT",("病区" + getStationDesc(parmRow.getValue("STATION_CODE"))));//病区
//		 printTParm.setData("TEXT2", "TEXT",("床号" + outsideParm.getValue("ODI","BED_NO")));//床号
//		 printTParm.setData("TEXT3", "TEXT",("条码" + this.getValueString("MR_NO"))) ;//条码
//		 printTParm.setData("TEXT4", "TEXT",("姓名" + outsideParm.getValue("ODI","PAT_NAME")));//姓名
//		 printTParm.setData("TEXT5", "TEXT",("性别" + sex));//性别
//		 printTParm.setData("TEXT6", "TEXT",("年龄" + OdiUtil.getInstance().showAge(getgetBirthDay(),sysDate))) ;//年龄
//		 printTParm.setData("TEXT7", "TEXT",("病案号" + outsideParm.getValue("ODI","MR_NO")));//病案号
//		 printTParm.setData("TEXT8", "TEXT",("输血日期" + parmRow.getValue("ORDER_DATE")));//输血日期
//		 printTParm.setData("TEXT9", "TEXT",("医生" + getDoctor(parmRow.getValue("ORDER_DR_CODE"))));//医生
//		 printTParm.setData("TEXT10", "TEXT",("频次" + parmRow.getValue("FREQ_CODE")));//频次
//		 printTParm.setData("TEXT11", "TEXT",("医嘱" + parmRow.getValue("ORDER_DESC")));//医嘱
//		 printTParm.setData("TEXT12", "TEXT",("血型" + getDoctor()));//血型
////		 printTParm.setData("TEXT12", "TEXT",("血量" + parmRow.getValue("MEDI_QTY")));//血量
//		 printTParm.setData("TEXT13", "TEXT",("单位" + OrderUtil.getInstance().getUnit(parmRow.getValue("DISPENSE_UNIT")))) ;//单位
//		 printTParm.setData("TEXT14", "TEXT",("速率" + parmRow.getValue("INFLUTION_RATE"))) ;//速率
//		 printTParm.setData("TEXT15", "TEXT",("用法" + OrderUtil.getInstance().getRoute(parmRow.getValue("ROUTE_CODE"))));//用法
//		 printTParm.setData("TEXT16", "TEXT",("组" + parmRow.getValue("LINK_NO")));//组
//		 printTParm.setData("TEXT17", "TEXT",("执行" + parmRow.getValue("NS_EXEC_CODE")));//执行
//		 printTParm.setData("TEXT18", "TEXT",("审核" + getDoctor(parmRow.getValue("OPT_USER"))));//审核
//		 this.messageBox_(printTParm);
	//	 this.messageBox("11" + printTParm);
	}
	
	 /**
     * 得到生日
     * zhanglei
     */
    public Timestamp getgetBirthDay(){
//    	String sql = " SELECT BIRTH_DATE FROM SYS_PATINFO "
//			+ " WHERE MR_NO = '" + mr_no + "'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT BIRTH_DATE FROM SYS_PATINFO "
						+ " WHERE MR_NO = '" + this.getValueString("MR_NO") + "'"));
//    	System.out.println("生日11111" + sql);
//    	this.messageBox("生日" + parm);
		return parm.getTimestamp("BIRTH_DATE", 0);
    }
    
    /**
     * 得到医生名
     * zhanglei 查表SYS_OPERATOR
     */
    public String getDoctor(String userId){
    	String sql = " SELECT USER_NAME " + " FROM SYS_OPERATOR " + " WHERE USER_ID = '" + userId + "'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(
				sql));
//    	System.out.println("得到医生名" + sql);
		return parm.getValue("USER_NAME", 0);
    }
    
    /**
     * 得到血型
     * zhanglei 查表SYS_OPERATOR
     */
    public TParm getDoctor(){
    	String sql = " SELECT BLOOD_TYPE,BLOOD_RH_TYPE " + " FROM SYS_PATINFO " + " WHERE MR_NO = '" + mrNo + "'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(
				sql));
//    	System.out.println("得到医生名" + sql);
//		return parm.getValue("BLOOD_TYPE", 0);
    	return parm;
    }
    
    /**
     * 得到医嘱类型
     * zhanglei 查表SYS_FEE CHARGE_HOSP_CODE
     */
    public String getChargeHospCode(String orderCode){
    	String sql = " SELECT CHARGE_HOSP_CODE " + " FROM SYS_FEE " + " WHERE ORDER_CODE = '" + orderCode + "'";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(
				sql));
//    	System.out.println("得到医生名" + sql);
//		return parm.getValue("BLOOD_TYPE", 0);
    	return parm.getValue("CHARGE_HOSP_CODE",0);
    }
    
    
    

}

