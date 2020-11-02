package com.javahis.ui.pha;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JOptionPane;

import jdo.device.CallNo;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.ODOTool;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.pha.PHACHMTool;
import jdo.pha.PHARxSheetTool;
import jdo.pha.PassTool;
import jdo.pha.Pha;
import jdo.pha.PhaSQL;
import jdo.pha.PhaSysParmTool;
import jdo.pha.TXNewATCTool;
import jdo.reg.PatAdmTool;
import jdo.spc.INDTool;
import jdo.spc.SPCSQL;
import jdo.spc.SPCTool;
import jdo.spc.bsm.ConsisServiceSoap_ConsisServiceSoap_Client;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.util.Medicine;
import jdo.util.Personal;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.bsm.XmlUtils;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.ui.sys.LEDMEDUI;
import com.javahis.util.DateUtil;
import com.javahis.util.OdoUtil;

/**
 * 
 * <p>
 * Title: 药房审配发退主档
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2008.09.28
 * 
 * @version 1.0
 */

public class PhaMainControl extends TControl {

	// 上面的panel
	private static String PANEL_HEAD = "PanelHead";
	// 中间的panel
	private static String PANEL_MIDDLE = "PanelMiddle";

	private static String PANEL_HEAD_NAME = "PHAMainHeader";
	private static String PANEL_MIDDLE_NAME = "PHAMainMiddle";
	// 审配发退
	private String type;
	// 西中成/中药饮片
	private String orderType;
	// pha对象
	private Pha pha;
	// pat对象
	private Pat pat;
	// 上面table（处方签）被点中的行号
	private int selectUPRow = -1;
	// 下面table（医嘱）被点中的行号
	private int selectDownRow = -1;
	// 被选种的当前orderlist对象
	private OrderList nowOrdList;

	// 查询的结果数据（装载OrderList）
	private TParm data;

	// ‘是否需要审核’标记
	private boolean needExamineFlg;
	// ‘配药即发药模式’标记
	private boolean dispEqualSendFlg;

	private String startDateUI, endDateUI;

	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;

	// 电子标签更新成功标志
	private static final String UPDATE_FLAG_TRUE = "1";
	// 电子标签更新失败标志
	private static final String UPDATE_FLAG_FLASE = "-1";

	// 电子标签随机生成标志
	public UUID uuid;

	// 记录 点击 主动时的case_no 为保存电子标签和病人关系时 用
	public String caseNo = "";
	private String WAY = "202";

	/**
	 * 跑马灯==pangben 2013-5-9
	 */
	private LEDMEDUI ledMedUi;
	/**
	 * 跑马灯参数
	 */
	private TParm ledParm;

	TTable tableDown;

	public PhaMainControl() {
	}

	public void onInit() {
		super.onInit();
		// 获得不同界面的初始化参数
		gainInitParm();
		// 根据参数初始化相应的界面
		initUI();
		// System.out.println("==================" + type);
		// 给上下table注册单击事件监听
		this.callFunction("UI|Table_UP|addEventListener", "Table_UP->" + TTableEvent.CLICKED, this, "onTableUPClicked");

		this.callFunction("UI|Table_DOWN|addEventListener", "Table_DOWN->" + TTableEvent.CLICKED, this,
				"onTableDOWNClicked");

		// 给下面的table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|Table_DOWN|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
				"onDownTableCheckBoxChangeValue");

		// 给下面的table注册change点击监听事件
		TTable tableDown = (TTable) getComponent("Table_DOWN");
		tableDown.addEventListener("Table_DOWN->" + TTableEvent.CHANGE_VALUE, this, "onDownTableChangeValue");

		// 给上面的table注册CHECK_BOX_CLICKED点击监听事件
		this.callFunction("UI|Table_UP|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
				"onUpTableCheckBoxChangeValue");

		// 取得药房系统参数
		// 获得‘是否需要审核’，‘配药即发药模式’的标记
		if ("WD".equals(orderType))
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		else if ("DD".equals(orderType))
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		dispEqualSendFlg = PhaSysParmTool.getInstance().dispEqualSend();
		initUIData();

		// 合理用药标识
		passIsReady = SYSNewRegionTool.getInstance().isOREASONABLEMED(Operator.getRegion());
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		// ========pangben modify 20110421 start 权限添加
		this.callFunction("UI|REGION_CODE|setEnabled",
				SYSRegionTool.getInstance().getRegionIsEnabled(this.getValueString("REGION_CODE")));
		// TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
		// cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
		// getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop
		// 初始化合理用药
		if (passIsReady && "Examine".equals(type)) {
			if (!PassTool.getInstance().init()) {
				this.messageBox("合理用药初始化失败！");
			}
		}
		((TTextField) getComponent("RX_PRESRT_NO")).grabFocus();// by liyh
																// 20120914 初始化
		// 处方签得到焦点
		this.grabFocus("RX_PRESRT_NO");

		// 屏蔽功能
		setOdoAutEnabled(false);
	}

	/**
	 * 得到初始化参数 WD西药/中成药 DD中药饮片
	 */
	public void gainInitParm() {
		String s = (String) this.getParameter();
		if (s == null)
			s = this.getConfigString("DEFAULT_PARAMETER"); // 控制西中成/饮片 和
		// 审配发退的MENU
		// eg:Examine|DD
		String s1[] = StringTool.parseLine(s, "|");
		type = s1[0]; // 控制改变：审配发退的MENU和上/下面的table头
		orderType = s1[1]; // 西中成/饮片
		// orderType = "WD";
		// orderType = "DD";
		// type = "Examine";
		// type = "Dispense";
		// type = "Send";
		// type = "Return";
		if ("WD".equals(orderType)) {
			startDateUI = "from_ORDER_DATE";
			endDateUI = "to_ORDER_DATE";
		} else {
			startDateUI = "start_ORDER_DATE";
			endDateUI = "end_ORDER_DATE";
		}
	}

	/**
	 * 根据初始化参数初始化相应界面 设置默认状态
	 */
	public void initUI() {
		// messageBox(type);
		if (type.equals("History")) {
			onInitUIHistory();
			if (orderType.equals("WD") && !needExamineFlg) {
				callFunction("UI|CHECKBUTTON|setVisible", false);
				setValue("DOSAGEBUTTON", "Y");
			} else {
				callFunction("UI|CHECKBUTTON|setVisible", true);
				setValue("CHECKBUTTON", "Y");
			}
			callFunction("UI|DOSAGEBUTTON|setVisible", true);
			callFunction("UI|DISPENSEBUTTON|setVisible", true);
			callFunction("UI|RETURNBUTTON|setVisible", true);
			callFunction("UI|tLabel_9|setVisible", false);
			callFunction("UI|FINISH|setVisible", false);
			callFunction("UI|UNFINISH|setVisible", false);

			// 历史查询界面不显示诊断信息
			callFunction("UI|MAIN_DIAG_LABEL|setVisible", false);
			callFunction("UI|MAIN_DIAG|setVisible", false);
			callFunction("UI|SECONDARY_DIAG_LABEL|setVisible", false);
			callFunction("UI|SECONDARY_DIAG|setVisible", false);
			return;
		}
		if ("DD".equals(orderType) && ("Dispense".equals(type) || "Examine".equals(type)))
			this.callFunction("UI|setMenuConfig", getConfigString("MENU." + type + "." + orderType)); // 菜单加载
		else
			this.callFunction("UI|setMenuConfig", getConfigString("MENU." + type)); // 菜单加载

		// 动态加载菜单后必须使用的初始化一步
		this.callFunction("UI|onInitMenu");
		// 根据审配发退设置不同的窗口title
		if (type.equals("Examine")) {
			this.callFunction("UI|setTitle", "药房审核界面"); // 审
			if (orderType.equals("WD")) {// &&"Y".equals(Operator.getSpcFlg()))
											// {//
											// 西药审核界面显示跑马灯，区分中药审核操作=====pangben
				// 2013-7-18
				// openLEDMEDUI();//modify by wangjc 20180103爱育华反应系统慢，注掉跑马灯功能
			}
		} else if (type.equals("Dispense")) {
			this.callFunction("UI|setTitle", "药房调配界面"); // 配
		} else if (type.equals("Send")) {
			this.callFunction("UI|setTitle", "药房发药界面"); // 发
		} else {
			this.callFunction("UI|setTitle", "药房退药界面"); // 退
		}

		// 加载两个PANEL
		this.callFunction("UI|" + PANEL_HEAD + "|addItem", PANEL_HEAD_NAME, getConfigString("PANEL_HEAD." + orderType),
				null, false);
		this.callFunction("UI|" + PANEL_MIDDLE + "|addItem", PANEL_MIDDLE_NAME,
				getConfigString("PANEL_MIDDLE." + orderType), null, false);

		if ("WD".equalsIgnoreCase(orderType)) { // 加载两个table
			this.callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));

			// 根据审配发退锁住不同数量的列
			if (type.equals("Examine")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13"); // 审
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left"); // 设置列的左右对应位置

			} else if (type.equals("Dispense")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14"); // 配
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // 设置列的左右对应位置
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // 发（不锁住第一列）
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // 设置列的左右对应位置
			} else {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14"); // 退
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // 设置列的左右对应位置
			}

			// 下面的table
			this.callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			/**
			 * PS：只有‘配药’的时候预留单笔操作功能
			 */
			// 当在配药界面的时候可不以处方签为单位，可以单独配某条药嘱（以order为单位确认）
			if (type.equals("Dispense")) { // 放开第一列
				this.callFunction("UI|Table_DOWN|setLockColumns", "2,3,4,5,6,7,8,9,10,11,12,13,14,16");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"4,left;5,right;6,left;7,right;8,right;9,right;10,left;11,left;12,left;13,right;14,left;15,left;16,left");
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"4,left;5,right;6,left;7,right;8,right;9,right;10,left;11,left;12,left;13,right;14,left;15,left;16,left");
			}
			// fux modify 20150825
			else if (type.equals("Examine")) { // 审
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"6,left;7,right;8,left;9,right;10,right;11,right;12,left;13,left;14,left;15,right;16,left;17,left;18,left");

			} else { // 审，发，退都以处方签为单位确认
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"5,left;6,right;7,left;8,right;9,right;10,right;11,left;12,left;13,left;14,right;15,left;16,left;17,left");

			}

			// 初始化相应的
			myInitWD();
		} else { // 当是DD：饮片时，需要改变审发/配退的table头

			this.callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));

			// 根据审配发退锁住不同数量的列
			if (type.equals("Examine")) {
				this.callFunction("UI|Table_UP|setLockColumns", "1,2,3,4,5,6,7,8,9,10,11,12,13,14");
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // 设置列的左右对应位置
			} else if (type.equals("Dispense")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // 配
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // 设置列的左右对应位置
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16"); // 发
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left"); // 设置列的左右对应位置
			} else {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // 退
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;4,right;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // 设置列的左右对应位置
			}
			// 下面的table
			this.callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			if (type.equals("Examine")) {// 20190228 add by wangjc 增加自备药标记显示
				this.callFunction("UI|Table_DOWN|setLockColumns", "ALL");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"0,left;1,right;2,left;4,right;5,left;6,left;8,left;9,left;10,right;12,left;13,right;14,left"); // 设置列的左右对应位置
			} else {
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left"); // 设置列的左右对应位置
			}

			// 初始化相应的
			myInitDD();
			// 设置送包药机按钮进入中医界面时不可点击
			if ("DD".equalsIgnoreCase(orderType) && "Dispense".equalsIgnoreCase(type)) {
				((TMenuItem) getComponent("ATC")).setEnabled(false);
			}
		}
		if (!type.equals("History")) {
			callFunction("UI|CHECKBUTTON|setVisible", false);
			callFunction("UI|DOSAGEBUTTON|setVisible", false);
			callFunction("UI|DISPENSEBUTTON|setVisible", false);
			callFunction("UI|RETURNBUTTON|setVisible", false);
		}
		// 测试根据人员初始化部分
		// System.out.println("=>" + Operator.getDept());
		// System.out.println("=>" + Operator.getName());
		// System.out.println("=>" + Operator.getID());
		// initByOptUser();

		// 退药界面不显示诊断信息
		if (type.equals("Return")) {
			this.callFunction("UI|MAIN_DIAG_LABEL|setVisible", false);
			this.callFunction("UI|MAIN_DIAG|setVisible", false);
			this.callFunction("UI|SECONDARY_DIAG_LABEL|setVisible", false);
			this.callFunction("UI|SECONDARY_DIAG|setVisible", false);
		}

		// 在程序中强制次诊断Textarea设置成不可编辑
		((TTextArea) this.getComponent("SECONDARY_DIAG")).getTextArea().setEnabled(false);
	}

	/**
	 * 初始化 根据登陆人员的(权限)信息初始化 （拿到登陆人员的信息）
	 */
	public void initByOptUser() {
		String dept = Operator.getDept();
		// this.messageBox(dept);
		this.callFunction("UI|EXEC_DEPT_CODE|setValue", dept);
		// 判断该登陆人员是否是最高权限
		if (this.getPopedem("maxAUT")) {
			// System.out.println("最高权限登陆");
			// 是最高权限,执行科室可以下拉
			this.callFunction("UI|EXEC_DEPT_CODE|setEnabled", true);
		} else {
			// System.out.println("其他权限登陆");
			// 不是最高权限的人只能显示登陆人员的主科室
			this.callFunction("UI|EXEC_DEPT_CODE|setEnabled", false);
		}

	}

	/**
	 * WD(西药/中成药) 自定义初始化状态 清空时候也调用
	 */
	public void myInitWD() {

		selectUPRow = -1;

		if (type.equals("Send")) {// 发药 情况执行科室EXEC_DEPT_CODE
			// 清空上部所有数据
			this.clearValue(
					"EXEC_DEPT_CODE;DRUGTYPE;RX_NO;RX_PRESRT_NO;PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
							+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;ATC_MACHINENO;ATC_TYPE;COUNTER_NO;MAIN_DIAG;SECONDARY_DIAG");
		} else {
			// 清空上部所有数据
			this.clearValue("DRUGTYPE;RX_NO;PRESRT_NO;RX_PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
					+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;COUNTER_NO;ATC_MACHINENO;ATC_TYPE;COUNTER_NO;MAIN_DIAG;SECONDARY_DIAG;DRUG");

		}
		callFunction("UI|SAVE|setEnabled", false);
		// 设置默认的按钮状态
		this.callFunction("UI|UNFINISH|setEnabled", true);
		// 当是配药页面的时候显示‘是否送包药机’控件
		if (type.equals("Dispense")) {
			this.callFunction("UI|ATC_NO_L|setVisible", true);

			this.callFunction("UI|ATC_MACHINENO|setVisible", true);

			this.callFunction("UI|ATC_TYPE_L|setVisible", true);

			this.callFunction("UI|ATC_TYPE|setVisible", true);

			this.callFunction("UI|PACKAGEDRUG|setVisible", true);
			// 不选中
			this.callFunction("UI|PACKAGEDRUG|setSelected", false);
			// 不可用
			this.callFunction("UI|PACKAGEDRUG|setEnabled", false);

		}
		this.callFunction("UI|ALLEXECUTE|setEnabled", true);
		this.callFunction("UI|ALLEXECUTE|setSelected", true);
		// 删除上/下table的值
		this.callFunction("UI|Table_UP|removeRowAll");
		this.callFunction("UI|Table_DOWN|removeRowAll");
	}

	/**
	 * DD(中药) 自定义初始化状态 清空时候也调用
	 */
	public void myInitDD() {

		selectUPRow = -1;
		// 清空所有数据
		this.clearValue("RX_NO;PRESRT_NO;RX_PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
				+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;DECOCT_CODE;"
				+ "TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE;COUNTER_NO");
		this.callFunction("UI|SAVE|setEnabled", false);
		// 设置默认的按钮状态
		this.callFunction("UI|UNFINISH|setEnabled", true);

		// 删除上/下table的值
		this.callFunction("UI|Table_UP|removeRowAll");
		this.callFunction("UI|Table_DOWN|removeRowAll");
	}

	/**
	 * 通过病案好查询功能
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
		this.setText("MR_NO", pat.getMrNo());
		((TTextField) getComponent("BASKET_ID")).grabFocus();
		onQuery();

	}

	/**
	 * 清空动作
	 */
	public void onClear() {
		// 回到初始化状态
		if (orderType.equals("WD")) {
			myInitWD();
		} else {
			myInitDD();
		}
		// ========pangben modify 20110517 start
		this.setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110517 stop
		this.setValue("BASKET_ID", "");// by liyh 20120914
		((TTextField) getComponent("RX_PRESRT_NO")).grabFocus();// by liyh
																// 20120914 初始化
		// 处方签得到焦点

		setOdoAutEnabled(false);
	}

	/**
	 * 点击上面的table的某一行激发的事件 在下面的table中列出细项
	 * 
	 * @param row
	 *            int
	 */
	public void onTableUPClicked(int row) {
		if (type.equals("History")) {
			onUpTableClick();
			return;
		}
		// 选中行号
		selectUPRow = row;
		// 从UI上面拿单独的一个值，如果status为“Y”为查询完成的，为“N”为查询未完成的
		String status = this.getValueString("FINISH");
		// 激活保存按扭type.equals("Examine") || type.equals("Dispense") ||
		// type.equals("Send") || type.equals("Return")
		if ("Y".equals(status) && (type.equals("Dispense") || type.equals("Send") || type.equals("Return"))) {
			this.callFunction("UI|SAVE|setEnabled", false);
		} else {
			this.callFunction("UI|SAVE|setEnabled", true);
		}

		setOdoAutEnabled(false);

		// 存储点击OrderList包含的orders（装载到下面的table上）

		tableDown = getTable("Table_DOWN");

		pat = Pat.onQueryByMrNo(data.getValue("MR_NO", selectUPRow).trim());

		// add by wangbin 20140731 查询诊断信息
		String rxNo = data.getValue("RX_NO", selectUPRow).trim();
		String sql = "SELECT B.ICD_CODE, C.ICD_CHN_DESC,B.MAIN_DIAG_FLG,B.DIAG_NOTE "
				+ " FROM OPD_ORDER A, OPD_DIAGREC B, SYS_DIAGNOSIS C "
				+ " WHERE A.CASE_NO = B.CASE_NO AND B.ICD_CODE = C.ICD_CODE AND A.RX_NO = '" + rxNo
				+ "' ORDER BY B.MAIN_DIAG_FLG DESC, A.ORDER_DATE DESC";

		TParm diagParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (diagParm.getErrCode() < 0) {
			err("ERR:" + diagParm.getErrCode() + diagParm.getErrText() + diagParm.getErrName());
			this.messageBox("查询诊断信息出现错误");
			return;
		}

		// 过敏史信息
		if ("Examine".equals(type)) {
			// 设置初始过敏史信息框为不可见
			this.callFunction("UI|GMS|setVisible", false);
			this.callFunction("UI|DRUGORINGRD_CODE|setVisible", false);
			String mrNo = getTableSelectRowData("MR_NO", "Table_UP");
			String drugoringdSql = "SELECT ORDER_DESC FROM ( "
					+ "SELECT (CASE WHEN OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN '' ELSE (SELECT CHN_DESC"
					+ " FROM SYS_DICTIONARY" + " WHERE     GROUP_ID = 'SYS_ALLERGY'"
					+ " AND ID = OPD_DRUGALLERGY.DRUG_TYPE) END)" + " AS DRUG_TYPE,(SELECT ORDER_DESC FROM SYS_FEE"
					+ " WHERE     ORDER_CODE = OPD_DRUGALLERGY.DRUGORINGRD_CODE"
					+ " AND OPD_DRUGALLERGY.drug_type = 'B')"
					+ " || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'PHA_INGREDIENT'"
					+ " AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'A')"
					+ " || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_ALLERGYTYPE'"
					+ " AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'C')"
					+ " || (CASE WHEN  OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN DRUGORINGRD_CODE END )"
					+ " AS ORDER_DESC, OPD_DRUGALLERGY.ALLERGY_NOTE FROM OPD_DRUGALLERGY, SYS_DEPT, SYS_OPERATOR"
					+ " WHERE OPD_DRUGALLERGY.MR_NO = '" + mrNo + "'"
					+ " AND OPD_DRUGALLERGY.DEPT_CODE = SYS_DEPT.DEPT_CODE"
					+ " AND OPD_DRUGALLERGY.DR_CODE = SYS_OPERATOR.USER_ID" + " ) WHERE TRIM(ORDER_DESC) <> '-'";
			TParm drugorTparm = new TParm(TJDODBTool.getInstance().select(drugoringdSql));
			// 判断是否有获取到数据,无的话，直接设置过敏史不显示
			if (drugorTparm.getCount() <= 0) {
				this.callFunction("UI|GMS|setVisible", false);
				this.callFunction("UI|DRUGORINGRD_CODE|setVisible", false);
			} else {
				// 定义一个存放字符串的临时器皿
				String temp = "";
				for (int i = 0; i < drugorTparm.getCount(); i++) {
					// 判断字段信息为无字时，赋值为空
					if (drugorTparm.getValue("ORDER_DESC", i).equals("无")) {
						temp = temp + "";
					} else {
						temp = temp + drugorTparm.getValue("ORDER_DESC", i) + ",";
					}
				}
				if (temp.length() > 0) {
					// 去掉末尾的逗号
					temp = temp.substring(0, temp.length() - 1);
					this.setValue("DRUGORINGRD_CODE", temp);
					this.callFunction("UI|GMS|setVisible", true);
					this.callFunction("UI|DRUGORINGRD_CODE|setVisible", true);
				}
			}

		}

		// 主诊断
		String mainDiag = diagParm.getValue("ICD_CHN_DESC", 0).trim().length() > 0 ? diagParm.getValue("ICD_CHN_DESC", 0):diagParm.getValue("DIAG_NOTE", 0);
		int parmLen = diagParm.getCount();
		// 次诊断
		String secondaryDiag = "";
		List<String> secondaryDiagCodeList = new ArrayList<String>();

		for (int i = 1; i < parmLen; i++) {
			if (!StringUtils.equals(diagParm.getValue("MAIN_DIAG_FLG", i), "Y")) {
				// 次诊断去重
				if (!secondaryDiagCodeList.contains(diagParm.getValue("ICD_CODE", i))) {
					secondaryDiagCodeList.add(diagParm.getValue("ICD_CODE", i));
					secondaryDiag = secondaryDiag + (diagParm.getValue("ICD_CHN_DESC", i).trim().length() > 0 ? diagParm.getValue("ICD_CHN_DESC", i):diagParm.getValue("DIAG_NOTE", i));
					if (i < parmLen - 1) {
						secondaryDiag = secondaryDiag + ";\n";
					}
				}
			}
		}

		// 构造向页面上赋值的诊断TParm
		TParm showDiagParm = new TParm();
		showDiagParm.setData("MAIN_DIAG", mainDiag);
		showDiagParm.setData("SECONDARY_DIAG", secondaryDiag);
		showDiagParm.setData("DRUG", this.getAllerg(pat.getMrNo())); // add by
																		// huangtt
																		// 20180205
																		// 过敏史
		TParm orders = new TParm();

		String deptSql = " SELECT ORG_CODE,ORG_CHN_DESC FROM IND_ORG ";
		TParm deptParn = new TParm(TJDODBTool.getInstance().select(deptSql));
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < deptParn.getCount("ORG_CODE"); i++) {
			map.put(deptParn.getValue("ORG_CODE", i), deptParn.getValue("ORG_CHN_DESC", i));
		}

		// 根据不同的药品类型
		if (orderType.equals("WD")) { // 西药/中成药
			// 选中table中的某行
			if (selectUPRow >= 0) {
				// 向上部的控件翻值
				setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO", data, selectUPRow);
				this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add by
																					// huangtt
																					// 20150120
																					// PRESRT_NO
				this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																							// huangtt
																							// 20150120
																							// PRESRT_NO
				setValueForParm("MAIN_DIAG;SECONDARY_DIAG;DRUG", showDiagParm);
				String counterNo = getTableSelectRowData("COUNTER_NO", "Table_UP");
				String orgCode = getTableSelectRowData("EXEC_DEPT_CODE", "Table_UP");
				TParm atcParm = this.getPHAcounterNoData(orgCode, counterNo);
				String atctype = atcParm.getValue("ATC_TYPE", 0);
				String machineNo = atcParm.getValue("MACHINENO", 0);
				this.setValue("ATC_MACHINENO", machineNo);
				this.setValue("ATC_TYPE", atctype);
				// 设置当前选中的某个orderList行号
				pha.setOrdListRow(selectUPRow);

				// 取得选中行的OrderList
				nowOrdList = pha.getCertainOrdListByRow(row);

				// RX_TYPE为‘2’的时候是管制药品
				if ("2".equals(nowOrdList.getRxType())) {

					// 如果为为真有权限，如果为假没有权限则使‘保存’按钮不可用
					if (!isCtlDrugPopedom()) {
						this.callFunction("UI|SAVE|setEnabled", false);
					}
				}

				// 取得该OrderList中含有的orders(类型是TParm)--PRIMARY主区域中的值
				orders = nowOrdList.getParm(nowOrdList.PRIMARY);

				// fux modify 20180928 修改你单价显示 id：6121
				System.out.println("COUNT:" + orders.getCount("ORDER_CODE"));
				for (int k = 0; k < orders.getCount("ORDER_CODE"); k++) {
					String sqlPhatransunit = " SELECT ORDER_CODE,DOSAGE_QTY,STOCK_QTY,DOSAGE_UNIT,STOCK_UNIT "
							+ " FROM PHA_TRANSUNIT WHERE ORDER_CODE = '" + orders.getValue("ORDER_CODE", k) + "' ";
					// System.out.println("sqlPhatransunit::"+sqlPhatransunit);
					TParm parmPhatransunit = new TParm(TJDODBTool.getInstance().select(sqlPhatransunit));
					String stockUnit = parmPhatransunit.getValue("STOCK_UNIT", 0);
					String unitCode = orders.getValue("DISPENSE_UNIT", k);
					if (stockUnit.equals(unitCode)) {
						double dosageQty = parmPhatransunit.getDouble("DOSAGE_QTY", 0);
						double stockQty = parmPhatransunit.getDouble("STOCK_QTY", 0);
						double ownPriceOld = orders.getDouble("OWN_PRICE", k);
						BigDecimal ownPriceNewDec = new BigDecimal(Double.toString(ownPriceOld * dosageQty / stockQty));
						// 保留2位小数
						double ownPriceNew = ownPriceNewDec.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						// System.out.println("ownPriceNew:::"+ownPriceNew);
						orders.setData("OWN_PRICE", k, ownPriceNew);
					}
				}

				// 取得case_no 为保存电子标签关系使用
				caseNo = orders.getValue("CASE_NO", 0);
				// 取得TParm的行数
				int count = orders.getCount();
				// 因为框架中用TParm的addData的第一参数eg:EXE_FLG的个数来判断放到table中的行数，所以应该循环插入第一次参

				for (int i = 0; i < count; i++) {
					orders.addData("EXE_FLG", "Y"); // 默认每条ORDER上划钩
					// 并且设置执行默认为真
					nowOrdList.getOrder(i).setExeFlg(true);

				}

				/**
				 * 审核时
				 */
				if ("Dispense".equals(type)) {
					for (int i = 0; i < count; i++) {
						String orderCode = orders.getValue("ORDER_CODE", i);
						String execDeptCode = orders.getValue("EXEC_DEPT_CODE", i);
						double dosageQty = orders.getDouble("DOSAGE_QTY", i);

						TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);
						String routeCode = orders.getValue("ROUTE_CODE", i);
						String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);

						String batchNo = orders.getValue("BATCH_NO", i);
						if ("<TNULL>".equals(batchNo)) {
							batchNo = "";
						}

						if ("Y".equals(skintestFlg)) {
							if ("PS".equals(routeCode)) {
								if (status.equals("Y")) {

								} else {
									if (batchNo == null || batchNo.equals("")) {

										TParm result = getIndStockBatchNoBySkintest(orderCode, execDeptCode, dosageQty);

										batchNo = result.getValue("BATCH_NO", 0);
									}
									tableDown.setLockCell(i, 15, false);
								}
							} else {

								if (batchNo == null || batchNo.equals("")) {
									TParm result = getIndStockBatchNoBySkintest(orderCode, execDeptCode, dosageQty);
									batchNo = result.getValue("BATCH_NO", 0);
								}
								tableDown.setLockCell(i, 15, true);
							}
						} else {
							tableDown.setLockCell(i, 15, true);
						}

						orders.setData("BATCH_NO", i, batchNo);
						nowOrdList.getOrder(i).setBatchNo(batchNo);
					}
				}

				// 不选中
				this.callFunction("UI|PACKAGEDRUG|setSelected", true);
				// 不可用
				this.callFunction("UI|PACKAGEDRUG|setEnabled", true);

				for (int j = 0; j < orders.getCount("EXE_FLG"); j++) {
					orders.setData("EXEC_DEPT", j, map.get(orders.getValue("EXEC_DEPT_CODE", j)));
				}

				// 把order放入下面的table
				// fux modify 20150825 加入bill_flg(如何判断退费。。。) 并且 将连和组 放在一起
				if ("Examine".equals(type)) {
					for (int k = 0; k < count; k++) {
						if (!"".equals(orders.getValue("PHA_RETN_CODE", k))) {
							orders.setData("BILL_FLG", k, "N");
						}
					}
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;BILL_FLG;RELEASE_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");// 20150803
																																																											// wangjc
																																																											// modify
																																																											// 增加RELEASE_FLG
				} else if ("Dispense".equals(type)) {// add by wangjc
					for (int k = 0; k < count; k++) {
						if (!"".equals(orders.getValue("PHA_RETN_CODE", k))) {
							orders.setData("BILL_FLG", k, "N");
						}
					}
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;BILL_FLG;LINKMAIN_FLG;RELEASE_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");
				} else {
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;LINKMAIN_FLG;RELEASE_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");// 20150803
																																																									// wangjc
																																																									// modify
																																																									// 增加RELEASE_FLG
				}
				// "EXE_FLG;ATC_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;REMARK;BATCH_NO"
				// 置包药机状态
				this.onDoATC();

				// messageBox("orders:"+orders);
				// fux modify 20150821
				// 退药完成状态变红
				if ("Y".equals(status) && type.equals("Return")) {

					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						/** 红色 */
						this.getTable("Table_DOWN").setRowTextColor(i, new Color(255, 0, 0));
					}
				}
				// 非退药
				else if (!type.equals("Return")) {
					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						String orderCode = orders.getValue("ORDER_CODE", i);
						// 高危药品变红
						if (this.getColorByHighRiskOrderCode(orderCode)) {
							this.getTable("Table_DOWN").setRowTextColor(i, Color.RED);// 高危药品，显示为红色
							// 非高危不变
						} else {
							this.getTable("Table_DOWN").setRowTextColor(i, new Color(0, 0, 0));
						}
					}
				}
				// 退药 未完成 不变
				else if ("N".equals(status) && type.equals("Return")) {
					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						this.getTable("Table_DOWN").setRowTextColor(i, new Color(0, 0, 0));
					}
				}

				((TTextField) getComponent("BASKET_ID")).grabFocus();

				return;
			}
		} else { // 中药饮片
			if (selectUPRow >= 0) { // 选中table中的某行
				// 向上部的控件翻值
				setValueForParm(
						"EXEC_DEPT_CODE;RX_NO;RX_PRESRT_NO;PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO", data,
						selectUPRow);
				this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));
				this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));
				setValueForParm("MAIN_DIAG;SECONDARY_DIAG;DRUG", showDiagParm);
				// 向中部的控件翻值
				setValueForParm("TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE",
						data, selectUPRow);

				// 设置当前选中的某个orderList行号
				pha.setOrdListRow(selectUPRow);

				// 取得选中行的OrderList
				nowOrdList = pha.getCertainOrdListByRow(row);
				// 取得该OrderList中含有的orders(类型是TParm)
				orders = nowOrdList.getParm(nowOrdList.PRIMARY);

				// 中药----重新整理后的中药orders(OPD提供的方法)
				OdoUtil odrUtil = new OdoUtil();
				TParm reOrders = odrUtil.chnMedicReArrange(orders);
				// 取得case_no 为保存电子标签关系使用
				caseNo = reOrders.getValue("CASE_NO", 0);
				// 根据审核、发药-用量（TAKE_QTY）/配药、退药-总量（TOT_QTY）
				// 20190228 modify by wangjc 增加备药标记显示
				if (type.equals("Examine")) {
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TAKE_QTY1;DCTEXCEP_CODE1;RELEASE_FLG1;ORDER_CODE2;TAKE_QTY2;DCTEXCEP_CODE2;RELEASE_FLG2;ORDER_CODE3;TAKE_QTY3;DCTEXCEP_CODE3;RELEASE_FLG3;ORDER_CODE4;TAKE_QTY4;DCTEXCEP_CODE4;RELEASE_FLG4");
				} else if (type.equals("Send")) {
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TAKE_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TAKE_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TAKE_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TAKE_QTY4;DCTEXCEP_CODE4");
				} else { // Dispense,Return
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4");
				}
				setTotTakeDays();

				return;
			}

		}
		// 判断该部门是否正在部门批次过账
		if ("Dispense".equalsIgnoreCase(type) && !checkOrgBatch()) {
			this.messageBox("该药房正在批次过账\n不可以执行扣库！");
			this.callFunction("UI|SAVE|setEnabled", false);
		}

	}

	/**
	 * 判断是否为高危药品
	 * 
	 * @param orderCode
	 * @return 20150731 wangjc add
	 */
	public boolean getColorByHighRiskOrderCode(String orderCode) {
		boolean a = false;
		String sql = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='" + orderCode + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getValue("HIGH_RISK_FLG", 0).equals("Y")) {
			a = true;
		}
		return a;
	}

	/**
	 * 点击下面的table的某一行激发的事件
	 * 
	 * @param row
	 *            int 行号
	 */
	public void onTableDOWNClicked(int row) {
		// 得到当前的选中行
		selectDownRow = row;
		if (orderType.equals("WD")) {
			// 激活送包药机选项
			// callFunction("UI|PACKAGEDRUG|setEnabled", true);
		} else { // DD
			// 中药只可以整张处方签
		}
		return;
	}

	/**
	 * 触发下面table的修改属性事件
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDownTableCheckBoxChangeValue(Object obj) {

		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的列/行
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();

		// 如果选中的是第一列就激发执行动作--执行
		if (col == 0) {
			boolean exeFlg;
			// 获得点击时的值
			exeFlg = TCM_Transform.getBoolean(tableDown.getValueAt(row, col));
			// 设置执行标记
			onExeFlg(exeFlg, row);
		}
		// 不允许在table上点击
		// 如果选中的是第二列就激发执行动作--送包药机
		if (col == 1) {
			String ATCFlg = getATCFlgFromSYSFee(nowOrdList.getOrder(row).getOrderCode());
			String boxFlg = nowOrdList.getOrder(row).getGiveboxFlg();
			if (tableDown.getValueAt(row, col).equals("Y") && (ATCFlg.length() == 0 || ATCFlg.equals("N"))) {
				callFunction("UI|Table_DOWN|setValueAt", "N", row, 1);
				tableDown.acceptText();
				onATCFlg("N", row);
				messageBox("此药品无法送包药机");
				return;
			}
			if (tableDown.getValueAt(row, col).equals("Y") && boxFlg.equals("Y")) {
				callFunction("UI|Table_DOWN|setValueAt", "N", row, 1);
				tableDown.acceptText();
				onATCFlg("N", row);
				messageBox("盒计药品无法送包药机");
				return;
			}
			// 获得点击时的值
			ATCFlg = TCM_Transform.getString(tableDown.getValueAt(row, col));
			// 设置执行标记
			onATCFlg(ATCFlg, row);
		}
	}

	public void onExeFlg(boolean exeFlg, int row) {
		// this.messageBox_("执行。。。。。。");
		// 取消画钩的拿出来
		Order nowOrder = nowOrdList.getOrder(row);
		// 如果勾选的该checkBox，就把order的ExeFlg属性设为真，以备在pha对象中onExecute方法中使用
		nowOrder.setExeFlg(exeFlg);
	}

	public void onATCFlg(String ATCFlg, int row) {
		// 取消画钩的拿出来
		Order nowOrder = nowOrdList.getOrder(row);
		// 如果勾选的该checkBox，就把order的ATCFlg属性设为真
		nowOrder.setAtcFlg(ATCFlg);
	}

	public void onUpTableCheckBoxChangeValue(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的列/行
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		if (col != 0)
			return;
		for (int i = 0; i < nowOrdList.size(); i++) {
			nowOrdList.getOrder(i).modifyDctagentFlg("" + tableDown.getValueAt(row, col));
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("1======================="+sdf.format(SystemTool.getInstance().getDate()));
		String rxNoString = getValueString("RX_NO");
		String rxPresrtNoString = this.getValueString("RX_PRESRT_NO");
		// System.out.println("rxNoString:"+rxNoString);
		// System.out.println("rxPresrtNoString:"+rxPresrtNoString);
		// System.out.println("PresrtNoString:"+rxPresrtNoString.replace(rxNoString,
		// ""));
		String presrtNoString = rxPresrtNoString.replace(rxNoString, "");
		// zhangp
		if (EKTIO.getInstance().ektAyhSwitch() && type.equals("Dispense")) {
			TTable downTable = (TTable) getComponent("Table_DOWN");
			downTable.acceptText();
			TParm downParm = downTable.getParmValue();
			TParm inParm = new TParm();
			for (int i = 0; i < downParm.getCount("EXE_FLG"); i++) {
				// EXE_FLG
				if (downParm.getBoolean("EXE_FLG", i)) {
					inParm.addData("SEQ_NO", downParm.getValue("SEQ_NO", i));
					inParm.addData("RX_NO", downParm.getValue("RX_NO", i));
				}
			}
			inParm.setData("CASE_NO", downParm.getValue("CASE_NO", 0));
			inParm.setData("MR_NO", downParm.getValue("MR_NO", 0));
			TParm preParm = EKTpreDebtTool.getInstance().checkMasterForExe(inParm);
			if (preParm.getErrCode() < 0) {
				messageBox(preParm.getErrText());
				return;
			}
		}
		// System.out.println("2======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Examine") && StringUtils.isNotEmpty(rxNoString) && rxNoString.length() > 5
				&& "Y".equals(Operator.getSpcFlg())) {
			Timestamp startDate = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
			Timestamp endDate = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();

			synOPdOrderSave(startDate.toString(), endDate.toString(), getValueString("RX_NO"), getValueString("MR_NO"));
		}
		if (type.equals("Examine") && !checkDrugAuto())
			return;
		TParm result = new TParm();
		// 判断执行药房不能与代理药房相同
		if (this.getValueString("EXEC_DEPT_CODE").length() > 0 && this.getValueString("AGENCY_ORG_CODE").length() > 0
				&& this.getValue("EXEC_DEPT_CODE").equals(this.getValue("AGENCY_ORG_CODE"))) {
			this.messageBox("执行药房与代理不可相同！");
			return;
		}

		// System.out.println("3======================="+sdf.format(SystemTool.getInstance().getDate()));

		// 病案号 发药电子标签闪烁用
		String mr_No = this.getValueString("MR_NO");

		// 处方签号
		String rxNo = this.getValueString("RX_NO");
		// 电子标签
		String basketId = this.getValueString("BASKET_ID");

		String rxPresertNo = this.getValueString("RX_PRESRT_NO"); // add by
																	// huangtt
																	// 20150120

		// 检测该选中药品是否可以做逆流成操作
		if ("Y".equals((String) this.getValueString("FINISH")))// 只有逆流成才检查可操作性
			if (!checkIfRve())
				return;

		// 设置pha的页面参数类型
		pha.setType(type);
		// 设置pha的页面状态属性
		pha.setFinishFlag((String) this.getValueString("FINISH"));

		pha.setLockFlg(Operator.getLockFlg());
		// ================================================================
		// 202与203方法的先后错误判断
		// MODIFY BY CEHNXI
		if (type.equals("Send")) {
			WAY = "203";
			boolean check = this.onSendBoxMachine();// 203方法，发药结束
			WAY = "202";
			if (!check)
				return;
		}
		// System.out.println("4======================="+sdf.format(SystemTool.getInstance().getDate()));
		// ==pangben 2013-7-19 添加物联网校验操作的医嘱和数据库是否同步
		if ((this.orderType.equals("WD")) && (this.type.equals("Examine"))) {// &&"Y".equals(Operator.getSpcFlg()))
																				// {//
																				// 西药审核界面操作

			nowOrdList = pha.getCertainOrdListByRow(selectUPRow);

			// 取得该OrderList中含有的orders(类型是TParm)--PRIMARY主区域中的值
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);

			System.out.println("ordersordersordersorders:" + orders);

			;// 获得需要操作的医嘱界面数据
			String sql = "SELECT A.SEQ_NO,A.DISPENSE_QTY FROM OPD_ORDER A WHERE A.CASE_NO='" + caseNo
			// + "' AND A.RX_NO||A.PRESRT_NO='" + rxPresertNo+ "' "
			// ;//add by huangtt 20150120
					+ "' AND A.RX_NO = '" + rxNoString + "' AND A.PRESRT_NO='" + presrtNoString + "' ";// modify by
																										// wangjc
																										// 20190109
																										// 爱育华反应药房保存较慢

			if (EKTIO.getInstance().ektAyhSwitch()) {
				// sql += "AND BILL_FLG='N'";
			} else {
				sql += "AND A.BILL_FLG='Y'";
			}
			// System.out.println(sql);
			TParm opdOrderParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (opdOrderParm.getCount() <= 0) {
				this.messageBox("查询此处方签医嘱出现问题");
				return;
			}
			if (orders.getCount("SEQ_NO") != opdOrderParm.getCount()) {
				this.messageBox("此处方签数据出现问题,请执行查询操作");
				return;
			}
			for (int i = 0; i < orders.getCount("SEQ_NO"); i++) {
				for (int j = 0; j < opdOrderParm.getCount(); j++) {
					if (orders.getDouble("SEQ_NO", i) == opdOrderParm.getDouble("SEQ_NO", j)) {// 比较顺序号
						if (orders.getDouble("DISPENSE_QTY", i) == opdOrderParm.getDouble("DISPENSE_QTY", j)) {// 比较总量是否相同
							break;
						} else {
							this.messageBox("此处方签数据出现问题,请执行查询操作");
							return;
						}
					}
				}
			}
		}
		// =========================== 保存之前验证界面的数据是否保存过

		// System.out.println("5======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Examine") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 校验同步问题
			String checkSqlBySynchronizedExamine = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_CHECK_CODE IS NOT NULL " + "  AND PHA_CHECK_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// 爱育华反应药房保存较慢
			TParm checkParmBySynchronizedExamine = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedExamine));
			if (checkParmBySynchronizedExamine.getCount() > 0) {
				this.messageBox("处方已审核 请重新查询!");
				onArrangeAfterSave();
				return;
			}
		}

		if (type.equals("Send") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 校验同步问题
			String checkSqlBySynchronizedSend = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_DISPENSE_CODE IS NOT NULL " + "  AND PHA_DISPENSE_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// 爱育华反应药房保存较慢
			TParm checkParmBySynchronizedSend = new TParm(TJDODBTool.getInstance().select(checkSqlBySynchronizedSend));
			if (checkParmBySynchronizedSend.getCount() > 0) {
				this.messageBox("处方已发药 请重新查询!");
				onArrangeAfterSave();
				return;
			}
		}

		if (type.equals("Return") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 校验同步问题
			String checkSqlBySynchronizedReturn = " SELECT RX_NO FROM OPD_ORDER " + " WHERE PHA_RETN_CODE IS NOT NULL "
					+ "  AND PHA_RETN_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// 爱育华反应药房保存较慢
			TParm checkParmBySynchronizedReturn = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedReturn));
			if (checkParmBySynchronizedReturn.getCount() > 0) {
				this.messageBox("处方已退药 请重新查询!");
				onArrangeAfterSave();
				return;
			}
		}

		// System.out.println("6======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Dispense") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// String checkSql = " SELECT RX_NO FROM OPD_ORDER "
			// + " WHERE BILL_FLG = 'Y' "
			// + " AND PHA_DOSAGE_DATE IS NOT NULL"
			// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;//add by huangtt
			// 20150120
			// TParm checkParm = new TParm(TJDODBTool.getInstance().select(
			// checkSql));
			// if (checkParm.getCount() > 0) {
			// this.messageBox("此数据已被操作");
			// onArrangeAfterSave();
			// return;
			// }

			// fux modify 20160804 校验同步问题
			String checkSqlBySynchronizedDispense = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_DOSAGE_CODE IS NOT NULL " + "  AND PHA_DOSAGE_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// 爱育华反应药房保存较慢
			TParm checkParmBySynchronizedDispense = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedDispense));
			if (checkParmBySynchronizedDispense.getCount() > 0) {
				this.messageBox("处方已配药 请重新查询!");
				onArrangeAfterSave();
				return;
			}

			// System.out.println("7======================="+sdf.format(SystemTool.getInstance().getDate()));
			nowOrdList = pha.getCertainOrdListByRow(selectUPRow);
			// 取得该OrderList中含有的orders(类型是TParm)--PRIMARY主区域中的值
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);

			String msg = "";
			String skingMsg = "";
			for (int i = 0; i < orders.getCount("ORDER_CODE"); i++) {

				String orderCode = orders.getValue("ORDER_CODE", i);
				String orderDesc = orders.getValue("ORDER_DESC", i);
				TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);
				String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);
				String batchNo = orders.getValue("BATCH_NO", i);
				String orgCode = orders.getValue("EXEC_DEPT_CODE", i);
				double dosageQty = orders.getDouble("DOSAGE_QTY", i);
				if ("Y".equals(skintestFlg)) {
					if (batchNo == null || batchNo.equals("")) {
						msg += orderDesc + "批号不能为空\n";
					} else {// 校验同批号是否有库存
						boolean checkResult = false;
						/** 根据批号检测 */
						checkResult = INDTool.getInstance().inspectIndStock(orgCode, orderCode, batchNo, dosageQty);
						if (!checkResult) {
							if (this.messageBox("提示", "该批号库存不足，是否继续?", 2) == 0) {

							} else {
								return;
							}
						}
					}
				} else {
					if ("<TNULL>".equals(batchNo) || "null".equals(batchNo)) {
						batchNo = "";
					}
					if (batchNo != null && !batchNo.equals("")) {
						msg += orderDesc + "不能填写批号" + batchNo + "\n";
					}
				}

				String skingFlg = orders.getValue("SKINTEST_FLG", i);

				if ("1".equals(skingFlg)) {
					skingMsg += orderDesc + " 批号：" + batchNo + "　皮试结果为：阳性+　\n";
				}
			}

			if (msg.length() > 0) {
				this.messageBox(msg);
				return;
			}

			if (skingMsg.length() > 0) {
				if (this.messageBox("提示", skingMsg + "\n是否确认配药吗?", 2) == 0) {

				} else {
					return;
				}
			}
		}

		// 结算不能退药
		if ("Return".equals(type)) {
			String checkSql = " SELECT RX_NO FROM OPD_ORDER " + " WHERE  BILL_FLG = 'Y' "
			// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;//add by
			// huangtt 20150120
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// 爱育华反应药房保存较慢
			TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
			if (checkParm.getCount() > 0) {
				this.messageBox("已经结算完成不能退药");
				return;
			}
		}

		// System.out.println("8======================="+sdf.format(SystemTool.getInstance().getDate()));
		// 调用pha对象的保存方法
		result = pha.onSave();
		// System.out.println("9======================="+sdf.format(SystemTool.getInstance().getDate()));
		// 验证保存
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			this.messageBox("保存失败！");
			return;
		} else {
			this.messageBox("保存成功！");
			if ("WD".equals(orderType)) {
				if (type.equals("Examine")) {// 审核操作跑马灯移除数据
					// getLedMedRemoveRxNo();// ====pangben 2013-5-27//modify by
					// wangjc 20180103爱育华反应系统慢，注掉跑马灯功能
				} else if (type.equals("Dispense") && !"Y".equals((String) this.getValueString("FINISH"))) {
					// onPrint(nowOrdList.getRxNo());
				}
			} else {
				if (type.equals("Examine") && !"Y".equals((String) this.getValueString("FINISH"))) {
					onPrintTCM();
				}
			}
			// ==========================取消叫号 ======chenxi modify
			// 20130618======================
			if (type.equals("Send") && "N".equals(getValueString("FINISH")) && "WD".equals(orderType)) {
				this.onArrive();
			}
			// ============================================= chenxi modify
			// 盒装包药机重送
			if (type.equals("Dispense") && "N".equals(getValueString("FINISH")) && "WD".equals(orderType)) {
				// lx test
				// this.onDispenseBoxMachine();
			}
			// //============================================= chenxi modify
			// 盒装包药机重送
			// luhai modify 2012-2-22 删除领药号的打印功能 begin
			// onCode();
			// luhai modify 2012-2-22 删除领药号的打印功能 end
		}
		// 2019-3-25 chenyl中草药调配时调用中草药接口 START
		// System.out.println(orderType.equals("DD"));
		// System.out.println(type.equals("Dispense"));
		// System.out.println(!"Y".equals(this.getValueString("FINISH")));
		if (orderType.equals("DD") && type.equals("Dispense") && !"Y".equals(this.getValueString("FINISH"))) {
			// 中草药开关
			if (StringUtils.equals("Y", TConfig.getSystemValue("CHM.Switch"))) {
				// 取得该OrderList中含有的orders(类型是TParm)--PRIMARY主区域中的值
				TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);
				String seqNo = orders.getValue("SEQ_NO").replaceAll("\\[", "").replaceAll("\\]", "");
				TParm parm = new TParm();
				parm.setData("CASE_NO", caseNo);
				parm.setData("RX_NO", this.getValueString("RX_NO"));
				parm.setData("SEQ_NO", seqNo);
				// 查询医嘱处方数据
				TParm orderInfoResult = PHACHMTool.getInstance().queryOrderInfo(parm);
				if (orderInfoResult.getErrCode() < 0) {
					this.messageBox("发送中药颗粒机失败");
					err("查询医嘱处方错误ERR:" + orderInfoResult.getErrCode() + orderInfoResult.getErrText()
							+ orderInfoResult.getErrName());
					return;
				}
				// 组装中草药插入数据
				TParm insertResult = PHACHMTool.getInstance().getChmInsertData(orderInfoResult);
				insertResult = TIOM_AppServer.executeAction("action.pha.PHACHMAction", "insertIntoDataPrescription",
						insertResult);
				if (insertResult.getErrCode() < 0) {
					this.messageBox("发送中药颗粒机失败");
					err("调用中草药接口失败ERR:" + insertResult.getErrCode() + insertResult.getErrText()
							+ insertResult.getErrName());
					return;
				}
			}
		}

		// ********************************************************************************************
		// luhai modify 2012-3-12 begin //若列表中的选中行不是一行，则不清空，删除选中的行并将剩下的行选中 begin
		// ********************************************************************************************
		// delete begin
		// // 保存整理的动作
		// onArrangeAfterSave();
		// delete end
		TTable table = (TTable) this.getComponent("Table_UP");
		// 判断改选中病患是否仅有一张处方签，若有多张下次刷新还显示该病患的信息
		/*
		 * String mrNo = table.getParmValue().getValue("MR_NO", selectUPRow); int
		 * lastCount = 0; for (int j = 0; j < table.getParmValue().getCount("MR_NO");
		 * j++) { if (table.getParmValue().getValue("MR_NO", j).equals(mrNo)) {
		 * lastCount++; } }
		 */
		// 得到该病患的处方签后，减去已经审核的那个处方签
		// lastCount--;
		// 表格剩余数量>0 且该病患的未处理处方签数>0时，刷新是保留mr_no 根据该病患继续查找
		/*
		 * if (table.getParmValue().getCount("MR_NO") > 1 && lastCount > 0) { //
		 * 清除上面选中的行 this.callFunction("UI|Table_UP|removeRow", selectUPRow); // 清除下面所有的行
		 * this.callFunction("UI|Table_DOWN|removeRowAll"); // this.onClear(); //
		 * COUNTER_NO RX_NO this.setValue("COUNTER_NO", ""); this.setValue("RX_NO", "");
		 * // 清空luhai 2012-03-28 begin // 清空上部数据 this
		 * .clearValue("DRUGTYPE;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;" +
		 * "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;COUNTER_NO;ATC_MACHINENO;ATC_TYPE;COUNTER_NO"
		 * ); // 清空luhai 2012-03-28 end this.onQuery(); } else {
		 */
		// 保存整理的动作
		onArrangeAfterSave();
		// System.out.println("10======================="+sdf.format(SystemTool.getInstance().getDate()));
		// }
		// luhai modify 2012-3-12 end
		// ********************************************************************************************
		// luhai modify 2012-3-12 begin //若列表中的选中行不是一行，则不清空，删除选中的行并将剩下的行选中 end
		// ********************************************************************************************
		// cancelCallNoList();

		// 只有配药时打印调用要签打印 by yhb 20120914
		if (type.equals("Dispense")) {
			// 更改执行科室
			updateExecDeptCode();
			// System.out.println("11======================="+sdf.format(SystemTool.getInstance().getDate()));
			// 配药单打印
			onPrintAuto(rxNoString, rxPresrtNoString);
			// System.out.println("16======================="+sdf.format(SystemTool.getInstance().getDate()));
		}
	}

	/**
	 * 更改执行科室
	 */
	public void updateExecDeptCode() {
		TParm parm = nowOrdList.getParm();
		System.out.println("boolean: " + (null != nowOrdList && nowOrdList.size() > 0));
		if (null != nowOrdList && nowOrdList.size() > 0) {
			// System.out.println("---------ATC_FLG:"+nowOrdList.getOrder(0).getAtcFlg());
			int count = nowOrdList.size();
			for (int i = 0; i < count; i++) {
				String atcFlg = nowOrdList.getOrder(i).getAtcFlg();
				if (StringUtils.equals("Y", atcFlg)) {// Y表示送包药机
					// 查询到对应的包药机
					TParm atcParm = SPCTool.getInstance().getOrgCodeOfAtc(nowOrdList.getOrder(i).getExecDeptCode());
					String caseNo = nowOrdList.getOrder(i).getCaseNo();
					String rxNo = nowOrdList.getOrder(i).getRxNo();
					String seqNo = nowOrdList.getOrder(i).getSeqNo() + "";
					String orgCode = atcParm.getValue("ORG_CODE", 0);
					if (orgCode.length() <= 0)
						orgCode = nowOrdList.getOrder(i).getExecDeptCode();
					// System.out.println("caseNo:"+caseNo+",rxNo="+rxNo+",seqNo:"+seqNo);
					// 修改执行科室
					TParm result = SPCTool.getInstance().updateExecDeptCode(caseNo, rxNo, seqNo, orgCode);
					// System.out.println(i+",result:"+result);
				}
			}
		}
	}

	/**
	 * 取消叫号列表
	 */
	public void cancelCallNoList() {
		if (!"Send".equals(type))
			return;
		if (nowOrdList.size() <= 0)
			return;
		CallNo call = new CallNo();
		if (!call.init())
			return;
		call.SyncDrug("", nowOrdList.getOrder(0).getCaseNo(), "", "", "", "", "", "", "", "", "", "1");
	}

	/**
	 * 检查该处方签是否可以执行逆操作
	 * 
	 * @return boolean
	 */
	private boolean checkIfRve() {

		String rxNo = this.getValueString("RX_NO");
		String presrtNo = this.getValueString("PRESRT_NO"); // add by huangtt
															// 20150121
		// String mrNo = this.getValueString("MR_NO");
		// 通过该处方签号查询
		String checkSql = getChechSql(rxNo, presrtNo);

//		System.out.println("---检查逆流是否可执行--->" + checkSql);

		// 执行sql语句
		TJDODBTool tool = TJDODBTool.getInstance();
		TParm result = new TParm();
		result = new TParm(tool.select(checkSql));
		// 拿到返回的行数(PS:用TJDODBTool返回后包装的TParm取行数的特殊方法)
		String flg = "";
		if ("Examine".equalsIgnoreCase(type))
			flg = result.getValue("PHA_DOSAGE_CODE", 0).toString();
		if ("Dispense".equalsIgnoreCase(type))
			flg = result.getValue("PHA_DISPENSE_CODE", 0).toString();
		if ("Send".equalsIgnoreCase(type))
			flg = result.getValue("PHA_RETN_CODE", 0).toString();
		// 如果返回的标记为空则可以逆操作
		if (flg == null || flg.length() <= 0 || flg.equalsIgnoreCase("null")) {
			return true;
		}

		// 弹出提示消息
		String msg = "";
		if ("Examine".equalsIgnoreCase(type))
			msg = "已配药不可取消审核";
		if ("Dispense".equalsIgnoreCase(type))
			msg = "已发药不可取消配药";
		if ("Send".equalsIgnoreCase(type))
			msg = "已退药不可取消发药";

		this.messageBox("该处方签\n" + msg);
		return false;
	}

	private String getChechSql(String rxNo, String presrtNo) {

		String checkSql = "";
		// 审核-配药；配药-发药；发药-退药
		if ("Examine".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_DOSAGE_CODE FROM opd_order WHERE " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo + "' ";// add
																														// by
																														// huangtt
																														// 20150120
		if ("Dispense".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_DISPENSE_CODE FROM opd_order WHERE " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo
					+ "' ";// add by
							// huangtt
							// 20150120
		if ("Send".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_RETN_CODE FROM opd_order WHERE  " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo + "' ";// add
																														// by
																														// huangtt
																														// 20150120

		return checkSql;
	}

	/**
	 * 保存之后重新整理界面
	 */
	public void onArrangeAfterSave() {
		// 清除上面选中的行
		this.callFunction("UI|Table_UP|removeRow", selectUPRow);
		// 清除下面所有的行
		this.callFunction("UI|Table_DOWN|removeRowAll");
		this.onClear();
		this.onQuery();

	}

	/**
	 * 查询 根据条件查询orderList 装载到上面的table
	 */
	public void onQuery() {
		if (type.equals("History")) {
			onQueryHistory();
			return;
		}
		TParm parm = new TParm();
		// 从UI上面拿单独的一个值，如果status为“Y”为查询完成的，为“N”为查询未完成的
		String status = this.getValueString("FINISH");
		// 拿到起讫时间
		Timestamp startDate = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
		Timestamp endDate = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();
		// 根据不同的界面取得该UI上的所有查询orderLiset条件--西成药/饮片
		if (orderType.equals("WD")) { // WD:西成药
			// 设置全部执行为真
			this.setValue("ALLEXECUTE", "Y");
			parm = this.getParmForTag("MR_NO;EXEC_DEPT_CODE;RX_NO;DEPT_CODE;"
					+ "DR_CODE;from_PRESCRIPT_NO;to_PRESCRIPT_NO;" + "AGENCY_ORG_CODE;COUNTER_NO", true);
			// 当是西成药界面的时候传入类型参,挂查询条件用
			parm.setData("MEDIC", "Y");

		} else { // DD:饮片
			parm = this.getParmForTag("MR_NO;EXEC_DEPT_CODE;ORDER_DATE;MR_NO;"
					+ "DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;to_PRESCRIPT_NO;" + "AGENCY_ORG_CODE;DECOCT_CODE;COUNTER_NO",
					true);
			// 当是饮片界面的时候传入类型参,挂查询条件用
			parm.setData("CHNMEDIC", "Y");
		}
		parm.setData("from_ORDER_DATE", startDate);
		parm.setData("to_ORDER_DATE", endDate);
		String rxNoString = getValueString("RX_NO");
		if (type.equals("Examine") && StringUtils.isNotEmpty(rxNoString) && rxNoString.length() > 5
				&& "Y".equals(Operator.getSpcFlg())) {
			synOPdOrder(startDate.toString(), endDate.toString(), getValueString("RX_NO"), getValueString("MR_NO"));
		}
		// --------------------特别管控------start--------------------------------
		/**
		 * 根据挂不同审配发退流程进行不同的管控 后台module8个标记isCheck(ed) isDGT(ed) isDlvry(ed) isReturn(ed)
		 */
		// 如果是"审"的界面
		// 并且选中“完成”，则加入“审核时间”in not null后台查询条件
		if (type.equals("Examine")) {
			if (status.equals("Y")) {
				parm.setData("isCheckedForExamine", "Y"); // 完成
			} else
				parm.setData("isCheck", "Y"); // 未完成

		}

		// 判断在“配”的页面时
		// 如果有审核流程，后台查询时的条件应该是“审核时间不为空”
		if (type.equals("Dispense")) {
			// 并且需要审核后配药
			if (needExamineFlg) {
				parm.setData("isChecked", "Y"); // 审核完成
			}
			// 如果是配药界面并且选中“完成”，则加入“配药时间”in not null后台查询条件
			if (status.equals("Y"))
				parm.setData("isDGTed", "Y"); // 完成
			else
				parm.setData("isDGT", "Y"); // 未完成
		}

		// 判断在“发”的页面时
		// 如果有配药流程，后台查询时的条件应该是“配药时间不为空”
		if (type.equals("Send")) {
			parm.setData("isDGTed", "Y"); // 配药完成
			if (this.onBoxFlg(this.getValueString("EXEC_DEPT_CODE"))) {
				parm.setData("isPicked", "Y");// 盒装包药机配药完成 chenx
			}

			// 如果是发药界面并且选中“完成”，则加入“发药时间”in not null后台查询条件
			if (status.equals("Y"))
				parm.setData("isDlvryed", "Y"); // 完成
			else
				parm.setData("isDlvry", "Y"); // 未完成
		}

		// 注意：在退药流程中，如果有发药流程则只管“发药时间”，如果没有发药流程--‘配药即发药模式’就只管“配药时间”（特殊流程）
		// 判断在“退”的页面时
		// 如果不是‘配药即发药模式’--有发药流程，后台查询时的条件应该是“发药时间”in not null
		if (type.equals("Return") && !dispEqualSendFlg) {
			// 如果是退药界面并且选中“完成”，则加入“退药时间”in not null后台查询条件
			if (status.equals("Y")) {
				parm.setData("isReturned", "Y"); // 完成
			} else {
				// PS：完成发药(当退药的时候只查询退药时间不为空)
				parm.setData("isDlvryed", "Y");
				parm.setData("isReturn", "Y"); // 未完成
			}

		}

		// 当在‘配药即发药模式’--没有发药时间，所以卡“配药时间”in not null
		if (type.equals("Return") && dispEqualSendFlg) {
			// 如果是发药界面并且选中“完成”，则加入“退药时间”in not null后台查询条件
			if (status.equals("Y"))
				parm.setData("isReturned", "Y"); // 完成
			else {
				// PS：配药完成(当退药的时候只查询退药时间不为空)
				parm.setData("isDGTed", "Y");
				parm.setData("isReturn", "Y"); // 未完成
			}
		}
		// --------------------特别管控------end----------------------------------

		// 增加查询<OPD_ORDER表>条件--只能显示已交费的（BILL_FLG）
		if (EKTIO.getInstance().ektAyhSwitch()) {
			// parm.setData("BILL_FLG", "N");
		} else {
			parm.setData("BILL_FLG", "Y");
		}
		parm.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify 20110628
		// 返回的u询PHA结果
		pha = Pha.onQueryByTParm(parm); // 返回的查询PHA结果
		if (pha == null) {
			// 清空所有数据
			onClear();
			messageBox("没有符合查询条件的数据");
			return;
		}
		// 调用PHA对象的方法返回该病人的所有处方签（是一个TParm类型变量）
		data = pha.getAllOrderListParm();
		// System.out.println("data==="+data);
		// 验证查询的结果
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
		}

		// 得到查询结果的行数
		int count = data.getCount();

		// 因为框架中用TParm的addData方法第一值（eg:EXE_FLG）的个数来判断放到table中的行数，所以应该循环插入第一次参
		for (int i = 0; i < count; i++) {
			// 如果是管制药品第一个参数为‘Y’-->管制药品单独一个
			// 处方签医嘱分类(0：补充计价 1：西成药 2：管制药品 3：中药饮片 4：诊疗项目)
			if ("2".equals(data.getData("RX_TYPE", i))) {
				data.addData("CTL_FLG", "Y");
				/**
				 * J-K:将来如果判断没有发放管制药品的人员就不能看到管制药品， 那么在此可以剔除该行(判断该操作者对管制药品有无权限)
				 * data.removeRow(i);
				 */
			} else {
				data.addData("CTL_FLG", "N");
			}

		}

		// 判断是西成药/中饮片,加载table值
		if (orderType.equals("WD")) { // 西成药
			// 根据不同的功能页面当中的table的列的不同会动态加载数据
			if (type.equals("Examine")) { // 审
				this.setColor(data, this.getTable("Table_UP"));
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Dispense")) { // 配
				this.setColor(data, this.getTable("Table_UP"));
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Send")) { // 发
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else { // Return-退
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			}
		} else { // 中饮片

			// 根据不同的功能页面当中的table的列的不同会动态加载数据
			if (type.equals("Examine")) { // 审
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Dispense")) { // 配
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Send")) { // 发
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else { // Return-退
				// 向table里面放值
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			}

		}

		if ("Y".equals(status) && type.equals("Return")) {

			for (int i = 0; i < data.getCount("RX_NO"); i++) {
				this.getTable("Table_UP").setRowTextColor(i, new Color(255, 0, 0));
			}
		} else {
			for (int i = 0; i < data.getCount("RX_NO"); i++) {
				this.getTable("Table_UP").setRowTextColor(i, new Color(0, 0, 0));
			}
		}
	}

	/**
	 * 已收费的显示为黄色
	 * 
	 * @param parm
	 * @param table
	 * 
	 *            add by wangjc 20181211
	 */
	private void setColor(TParm parm, TTable table) {
		String sql = "";
		TParm result = null;
		for (int i = 0; i < parm.getCount(); i++) {
			sql = "SELECT COUNT(*) AS COUNT FROM OPD_ORDER WHERE RX_NO = '" + parm.getValue("RX_NO", i)
					+ "' AND PRESRT_NO = '" + parm.getValue("PRESRT_NO", i) + "' AND BILL_FLG = 'Y'";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getInt("COUNT", 0) > 0) {
				table.setRowColor(i, Color.YELLOW);
			} else {
				table.setRowColor(i, null);
			}
		}
	}

	// 备用方法，查询该用户对管制药品是否有权限
	public boolean isCtlDrugPopedom() {
		// 操作者ID
		String optId = Operator.getID();
		Personal checkCtlFlg = new Personal();
		TParm result = new TParm(checkCtlFlg.getSYSOperator(optId));
		String CtlFlg = (String) result.getValue("CTRL_FLG", 0);
		// this.messageBox(CtlFlg);
		if (!"Y".equalsIgnoreCase(CtlFlg)) {
			this.messageBox("您没有对管制药品操作权限！");
			return false;
		}
		return true;
	}

	// 全部执行
	public void onDoEXE() {
		// 得到当前执行与否的状态
		boolean nowFlag = (Boolean) this.callFunction("UI|ALLEXECUTE|isSelected");
		// 得到行数
		int ordCount = (Integer) this.callFunction("UI|Table_DOWN|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			// 循环取消对勾（现象）
			this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, i, 0);
			// 循环设置每一行数据的第一列的值（本质）
			onExeFlg(nowFlag, i);
		}
	}

	// 执行送包药机动作
	public void onDoATC() {
		/*
		 * //this.messageBox_("送包药机。。。。。。"); //得到当前‘送包药机’与否的状态 boolean nowFlag =
		 * (Boolean)this.callFunction( "UI|PACKAGEDRUG|isSelected"); //循环取消对勾（现象）
		 * this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, selectDownRow, 1);
		 * //取消画钩的拿出来 Order nowOrder = nowOrdList.getOrder(selectDownRow);
		 * //如果勾选的该checkBox，就把order的ExeFlg属性设为真，以备在pha对象中onExecute方法中使用
		 * nowOrder.setAtcFlg(TCM_Transform.getString(nowFlag));
		 */

		((TTextField) getComponent("BASKET_ID")).grabFocus();// by liyh 20120914
		// 初始化 处方签得到焦点
		// 得到当前执行与否的状态
		boolean nowFlag = (Boolean) this.callFunction("UI|PACKAGEDRUG|isSelected");
		// 得到行数
		int ordCount = (Integer) this.callFunction("UI|Table_DOWN|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			String ATCFlg = getATCFlgFromSYSFee(nowOrdList.getOrder(i).getOrderCode());
			String boxFlg = nowOrdList.getOrder(i).getGiveboxFlg();
			if (ATCFlg.length() == 0 || ATCFlg.equals("N") || boxFlg.equals("Y"))
				continue;
			// 循环取消对勾（现象）
			this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, i, 1);
			// 循环设置每一行数据的第一列的值（本质）
			onATCFlg(TCM_Transform.getString(nowFlag), i);
		}
	}

	private String getATCFlgFromSYSFee(String orderCode) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT ATC_FLG " + " FROM SYS_FEE" + " WHERE ORDER_CODE='" + orderCode + "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("ATC_FLG", 0);
	}

	/**
	 * @return boolean true 可以执行保存 false 部门批次过账不可以保存
	 */
	public boolean checkOrgBatch() {

		// 给IND传参执行扣库的科室代码
		String orgCode = (String) this.getValue("EXEC_DEPT_CODE");
		TParm orgCodeForInd = new TParm();
		orgCodeForInd.setData("org_code", orgCode);
		// IND接口
		Medicine subInd = new Medicine();

		return subInd.checkIndOrgBatch(orgCodeForInd);

	}

	/**
	 * 打印领药号
	 */
	public void onCode() {
		// this.messageBox("打印领药号领药号");
		if (!"Examine".equals(type))
			return;
		TTable table = (TTable) this.getComponent("Table_UP");
		if (table.getRowCount() <= 0 || table.getSelectedRow() < 0) {
			this.messageBox("无打印数据");
			return;
		}

		TParm printData = new TParm();
		printData.setData("NUMBER", "TEXT", SystemTool.getInstance().getNo("ALL", "PHA", "CLAIM_NO", "No"));
		String name = (String) table.getValueAt(table.getSelectedRow(), 2);
		printData.setData("NAME", "TEXT", name);
		this.openPrintDialog("%ROOT%\\config\\prt\\pha\\PHAGetMedicineNum.jhw", printData);

	}

	/**
	 * 打印
	 */
	public void onPrint() {
		// this.messageBox("打印主程序");
		String status = this.getValueString("FINISH");
		// 只有退药&&完成状态才可以执行打印
		if ("Return".equals(type) && !"Y".equals(status)) {
			this.messageBox("退药以后才可以打印！");
			return;
		}

		if (((TTable) this.getComponent("Table_UP")).getRowCount() <= 0
				|| ((TTable) this.getComponent("Table_DOWN")).getRowCount() <= 0) {
			this.messageBox("没有退药数据！");
			return;
		}

		// 需要打印的数据
		TParm printData = new TParm();
		printData = getReturnDrug();

		// this.messageBox_(printData);

		// 退药界面--退药单
		if ("Return".equals(type))
			this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHARetnMedSta.jhw", printData);

	}

	/**
	 * 获得退药打印的数据
	 * 
	 * @return TParm
	 */
	public TParm getReturnDrug() {

		String prtTime = StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd HH:mm:ss");
		TParm data = new TParm();
		data.setData("proName", "TEXT", "【PhaMainControl】");
		data.setData("prtTime", "TEXT", prtTime);
		data.setData("HospName", "TEXT", Manager.getOrganization().getHospitalCHNShortName(Operator.getPosition()));
		data.setData("staName", "TEXT", "退 药 确 认 单");
		// 统计时间
		Timestamp startDateT = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
		Timestamp endDateT = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();
		String startDate = ("" + startDateT).substring(0, 10);
		String endDate = ("" + endDateT).substring(0, 10);
		// String
		// startDate=this.getValueString("from_ORDER_DATE").substring(0,10);
		// String endDate=this.getValueString("to_ORDER_DATE").substring(0,10);
		data.setData("staSection", "TEXT", "统计区间: " + startDate + " ～ " + endDate);
		// 制表时间
		data.setData("prtDate", "TEXT", "制表时间: " + prtTime);
		// 执行药房
		String exeDept = ((TComboBox) this.getComponent("EXEC_DEPT_CODE")).getSelectedName();
		data.setData("durgDept", "TEXT", exeDept);
		// 姓名
		Pat prtPat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
		String name = prtPat.getName();
		data.setData("name", "TEXT", name);
		// 性别
		String sex = prtPat.getSexString();
		data.setData("sex", "TEXT", sex);
		// 年龄
		String age = "43";
		data.setData("age", "TEXT", age);
		// 病案号
		String mrNo = prtPat.getMrNo();
		data.setData("mrNo", "TEXT", mrNo);

		TTable downTable = (TTable) getComponent("Table_DOWN");
		downTable.acceptText();
		TParm downParm = downTable.getParmValue();

		String caseNo = downParm.getValue("CASE_NO", 0);

		// 主诊断
		// String caseNo = getPatCaseNo(mrNo);
		// 拿到实际看病的科室
		String deptSql = " SELECT   A.REALDEPT_CODE, B.DEPT_CHN_DESC " + " FROM   REG_PATADM A, SYS_DEPT B "
				+ " WHERE   A.CASE_NO = '" + caseNo + "' AND A.REALDEPT_CODE = B.DEPT_CODE";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
		String deptDesc = (String) deptParm.getData("DEPT_CHN_DESC", 0);
		data.setData("dept", "TEXT", deptDesc);

		/**
		 * // 通过CASE_NO拿到主诊断 String icdSql = " SELECT A.ICD_CODE, B.ICD_CHN_DESC " + "
		 * FROM OPD_DIAGREC A, SYS_DIAGNOSIS B " + " WHERE A.CASE_NO = '" + caseNo + "'
		 * AND A.ICD_CODE = B.ICD_CODE AND A.MAIN_DIAG_FLG = 'Y'"; TParm icdParm = new
		 * TParm(TJDODBTool.getInstance().select(icdSql));
		 * 
		 * String icdCode = "ICD"; String icdDesc = "测试ICD"; // 当有主诊断的时候 if
		 * (icdParm.getCount() > 0) { icdCode = (String) icdParm.getData("ICD_CODE", 0);
		 * icdDesc = (String) icdParm.getData("ICD_CHN_DESC", 0); }
		 * data.setData("icdCode", "TEXT", icdCode); data.setData("icdValue", "TEXT",
		 * icdDesc);
		 */
		data.setData("OEIType", "TEXT", "门");

		TParm mainData = getMainData();
		data.setData("TABLE", mainData.getData());

		return data;
	}

	/**
	 * 获得主数据从table上
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public TParm getMainData() {
		TParm result = new TParm();

		String rx_No = this.getValueString("RX_NO");
		String presrt_No = this.getValueString("PRESRT_NO"); // add by huangtt
																// 20150121

		String prtSql = " SELECT  A.LINK_NO, A.ORDER_DESC || CASE WHEN TRIM (A.GOODS_DESC) IS NOT NULL OR TRIM (A.GOODS_DESC) <> '' THEN '(' || A.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC,"
				+ " A.SPECIFICATION,D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,A.MEDI_QTY || ' ' || B.UNIT_CHN_DESC   DOSAGE_QTY, "
				+ " A.TAKE_DAYS,A.DISPENSE_QTY,A.OWN_AMT "
				+ " FROM OPD_ORDER A ,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D  "
				+ " WHERE   A.MEDI_UNIT = B.UNIT_CODE  " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.RX_NO||A.PRESRT_NO = '" + rx_No + presrt_No
				+ "'" + " AND A.PHA_RETN_CODE IS NOT NULL ";

		// System.out.println("prtSql-----:"+prtSql);
		result = new TParm(TJDODBTool.getInstance().select(prtSql));
		result.setCount(result.getCount());
		result.addData("SYSTEM", "COLUMNS", "LINK_NO");
		result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		result.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		result.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		result.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		result.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
		result.addData("SYSTEM", "COLUMNS", "OWN_AMT");

		return result;
	}

	/**
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public String getPatCaseNo(String mrNo) {

		return "";
	}

	/**
	 * 打印电子病历
	 */
	public void onElecCaseHistory() {
		// modify by wangbin 20140730 门急诊药房审配发页面电子病历浏览按时间倒序
		TParm opdParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM REG_PATADM WHERE MR_NO='"
				+ pat.getMrNo() + "' AND ADM_TYPE='O' ORDER BY REG_DATE DESC"));
		if (opdParm.getCount() < 0) {
			this.messageBox("此病患没有门诊病历！");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);

	}

	/**
	 * 包药机重送
	 */
	public void onGenATCFile() {
		String type = this.getValueString("ATC_TYPE");
		String machineNo = this.getValueString("ATC_MACHINENO");
		if (type.equals("1")) {
			this.onOldATCFile();
		} else if (type.equals("2")) {
			if (machineNo.equals("")) {
				this.messageBox("包药机台号不能为空");
				return;
			}
			this.onNewATCInsert(machineNo);
		}
	}

	/**
	 * 产生送包药机的txt文件
	 */
	public void onOldATCFile() {
		if (getTable("Table_UP").getSelectedRow() < 0) {
			messageBox("请选择处方信息");
			return;
		}
		TParm parm = new TParm();
		// 门急别
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		parm.setData("ADM_TYPE", regInfo.getValue("ADM_TYPE", 0));
		// 药品列表
		TParm drugListParm = new TParm();
		int count = 0;
		for (int i = 0; i < nowOrdList.size(); i++) {
			Order nowOrder = nowOrdList.getOrder(i);
			// 送包药机注记
			if (nowOrder.getAtcFlg().equals("N"))
				continue;
			TParm desc = getOrderData(nowOrder.getOrderCode());
			TParm ransRate = getPHAOrderTransRate(nowOrder.getOrderCode());
			// 领药号
			drugListParm.addData("PRESCRIPT_NO", nowOrder.getPrescriptNo());
			// 姓名
			drugListParm.addData("PAT_NAME", getTableSelectRowData("PAT_NAME", "Table_UP"));
			// 病案号
			drugListParm.addData("MR_NO", getTableSelectRowData("MR_NO", "Table_UP"));
			// 送包药机时间
			drugListParm.addData("DATE", ("" + SystemTool.getInstance().getDate()).substring(0, 19));
			// 药品列表序号
			drugListParm.addData("SEQ", i + 1);
			// 药品编码
			/*************** 物联网 发药转HIS编码 by liyh 20130104 staert *************/
			// drugListParm.addData("ORDER_CODE", nowOrder.getOrderCode());
			String hisOrderCode = SPCTool.getInstance().getHisOrderCodeBySpcOrderCode(nowOrder.getOrderCode(),
					Operator.getRegion());
			// System.out.println(nowOrder.getOrderCode()+"-------spc to his
			// ordercode-------"+hisOrderCode);
			drugListParm.addData("ORDER_CODE", hisOrderCode);
			/*************** 物联网 发药转HIS编码 by liyh 20130104 end *************/
			// 药品商品名
			drugListParm.addData("ORDER_GOODS_DESC", desc.getData("TRADE_ENG_DESC", 0));
			int time = getFreqData(nowOrder.getFreqCode()).getInt("FREQ_TIMES", 0);
			double qty = nowOrder.getDispenseQty() / nowOrder.getTakeDays();
			double Minqty = (double) (qty) / time;
			// 药品总量
			drugListParm.addData("QTY", Minqty);
			// 药品频次
			drugListParm.addData("FREQ", nowOrder.getFreqCode());
			// 用药天数
			drugListParm.addData("DAY", nowOrder.getTakeDays());
			// 首餐时间传入空值
			drugListParm.addData("START_DTTM", "000000000000");
			// 餐包注记传入空值
			drugListParm.addData("FLG", "N");
			count++;
		}
		if (count > 0) {
			if (drugListParm.getCount("SEQ") <= 0)
				return;
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "1");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction", "onATCO", parm);
			if (parm.getErrCode() < 0) {
				messageBox("送包药机失败");
				return;
			}
			messageBox("送包药机成功");
		}
	}

	/**
	 * 包药机数据插入
	 */
	public void onNewATCInsert(String machineNo) {
		if (getTable("Table_UP").getSelectedRow() < 0) {
			messageBox("请选择处方信息");
			return;
		}
		TParm parm = new TParm();
		pat = new Pat();
		// 门急别
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		parm.setData("ADM_TYPE", regInfo.getValue("ADM_TYPE", 0));
		int count = 0;
		Map<String, String> map = new HashMap<String, String>();
		int seq = 0;
		String preNo = "";
		String preStr1 = "";
		String preStr2 = "";
		// 药品列表
		TParm drugListParm = new TParm();
		for (int i = 0; i < nowOrdList.size(); i++) {
			Order nowOrder = nowOrdList.getOrder(i);
			// 送包药机注记
			if (nowOrder.getAtcFlg().equals("N"))
				continue;
			count++;
			TParm desc = getOrderData(nowOrder.getOrderCode());
			if (map.get(nowOrder.getCaseNo()) == null) {
				preNo = nowOrder.getRxNo();
			}
			map.put(nowOrder.getCaseNo(), nowOrder.getCaseNo());
			// 处方号 1
			drugListParm.addData("PRESCRIPTIONNO", preNo);
			preStr1 = preNo;
			// 不同处方签号取顺序号
			if (!preStr1.equals(preStr2)) {
				seq = 1;
				// 顺序号 2
				drugListParm.addData("SEQNO", seq);
				preStr2 = preStr1;
			} else {
				seq++;
				// 顺序号 2
				drugListParm.addData("SEQNO", seq);
			}
			// 组号（默认）3
			drugListParm.addData("GROUP_NO", 2);
			// 机器号（药房柜台设置） 4
			drugListParm.addData("MACHINENO", TypeTool.getInt(machineNo));
			// 处理状态（默认） 5
			drugListParm.addData("PROCFLG", 0);
			pat = Pat.onQueryByMrNo(nowOrder.getMrNo());
			// 病患ID 6
			drugListParm.addData("PATIENTID", pat.getMrNo());
			// 病患姓名 7
			drugListParm.addData("PATIENTNAME", pat.getName());
			// 病患姓名拼音8
			drugListParm.addData("ENGLISHNAME", "");
			// 出生日期 9
			drugListParm.addData("BIRTHDAY", pat.getBirthday());
			// 性别 10
			drugListParm.addData("SEX", pat.getSexCode());
			// 类别 区分 （ 1:门诊 2:住院[长期] 3:住院[临时] ） 11
			drugListParm.addData("IOFLG", "1");
			// 挂号科室编码 12
			drugListParm.addData("WARDCD", nowOrder.getExecDeptCode());
			// 挂号科室名称 13
			drugListParm.addData("WARDNAME", getDeptDesc(nowOrder.getExecDeptCode()));
			// 病房号（门诊无） 14
			drugListParm.addData("ROOMNO", "");
			// 病床号 (门诊无) 15
			drugListParm.addData("BEDNO", "");
			// 医师编码 16
			drugListParm.addData("DOCTORCD",
					nowOrder.getDrCode().getBytes().length > 7 ? new String(nowOrder.getDrCode().getBytes(), 0, 7)
							: nowOrder.getDrCode());
			// 医师名称 17
			drugListParm.addData("DOCTORNAME", getDrDesc(nowOrder.getDrCode()));
			// 处方时间 18
			drugListParm.addData("PRESCRIPTIONDATE", nowOrder.getOrderDate());
			String today = ("" + SystemTool.getInstance().getDate()).substring(0, 10).replaceAll("-", "");
			// 第一次用药时间(门诊为空) 19
			drugListParm.addData("TAKEDATE", SystemTool.getInstance().getDate());
			// 开始服用的时间编码(门诊为空) 20
			drugListParm.addData("TAKETIME", "");
			// 最后服用的时间编码(门诊为空) 21
			drugListParm.addData("LASTTIME", "");
			// 紧急类别（默认为1） 22
			drugListParm.addData("PRESC_CLASS", 0);
			// 药品编码 23
			String hisOrderCode = SPCTool.getInstance().getHisOrderCodeBySpcOrderCode(nowOrder.getOrderCode(),
					Operator.getRegion());
			drugListParm.addData("DRUGCD", hisOrderCode);
			// 药品名 24
			drugListParm.addData("DRUGNAME", desc.getData("ORDER_DESC", 0) + "" + desc.getData("SPECIFICATION", 0));
			// 药品类型(默认为空) 25
			drugListParm.addData("DRUGSHAPE", "");
			// 开药数量 26
			drugListParm.addData("PRESCRIPTIONDOSE", nowOrder.getMediQty());
			// 开药单位 27
			drugListParm.addData("PRESCRIPTIONUNIT", getUnitDesc(nowOrder.getMediUnit()));
			double dispenQty = nowOrder.getDispenseQty();
			int time = getFreqData(nowOrder.getFreqCode()).getInt("FREQ_TIMES", 0);
			int day = nowOrder.getTakeDays();
			double qty = (double) (dispenQty / day) / time;
			BigDecimal sf = new BigDecimal(String.valueOf(qty));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			// 发药数量 28
			drugListParm.addData("DISPENSEDDOSE", data.doubleValue());
			// 发药总数量 29
			drugListParm.addData("DISPENSEDTOTALDOSE", dispenQty);
			// 发药单位 30
			drugListParm.addData("DISPENSEDUNIT", getUnitDesc(nowOrder.getDosageUnit()));
			// 单片或单粒的剂量 31
			drugListParm.addData("AMOUNT_PER_PACKAGE",
					this.getPHAOrderTransRate(nowOrder.getOrderCode()).getDouble("MEDI_QTY", 0));
			String manDesc = this.getManDesc(desc.getValue("MAN_CODE", 0));
			// 厂家名 32
			drugListParm.addData("FIRM_ID",
					manDesc.getBytes().length > 20 ? new String(manDesc.getBytes(), 0, 20) : manDesc);
			// 服用天数 33
			drugListParm.addData("DISPENSE_DAYS", day);
			// 频次 34
			drugListParm.addData("FREQ_DESC_CODE", "");
			// 频次名称 35
			drugListParm.addData("FREQ_DESC", getFreqData(nowOrder.getFreqCode()).getValue("FREQ_CHN_DESC", 0));
			// 一天服用次数（空） 36
			drugListParm.addData("FREQ_COUNTER", "");
			String timeCode = TXNewATCTool.getTimeLine(nowOrder.getFreqCode());
			// 服用时间编码 37
			drugListParm.addData("FREQ_DESC_DETAIL_CODE", timeCode);
			String timeDetail = TXNewATCTool.getTimeDetail(nowOrder.getFreqCode());
			// 服用时间详细 38
			drugListParm.addData("FREQ_DESC_DETAIL", timeDetail);
			// 用药说明编码 39
			drugListParm.addData("EXPLANATION_CODE", "");
			// 用药说明 40
			drugListParm.addData("EXPLANATION", "");
			// 用药途径 41
			drugListParm.addData("ADMINISTRATION_NAME", this.getRouteDesc(nowOrder.getRouteCode()));
			// 备注 42
			drugListParm.addData("DOCTORCOMMENT", "");
			// 摆药顺序 43
			drugListParm.addData("BAGORDERBY", "");
			// 操作时间 44
			drugListParm.addData("MAKERECTIME", ("" + SystemTool.getInstance().getDate()).substring(0, 19));
			// 对方更新时间 45
			drugListParm.addData("UPDATERECTIME", "");
			// 预备 46
			drugListParm.addData("FILLER", "");
			// 医嘱号 47
			drugListParm.addData("ORDER_NO", Long.parseLong(nowOrder.getRxNo()));
			// 顺序号 48
			drugListParm.addData("ORDER_SUB_NO", nowOrder.getSeqNo());
			// 以下暂时不用
			// 外袋打印格式 49
			drugListParm.addData("BAGPRINTFMT", "");
			// 尺寸 50
			drugListParm.addData("BAGLEN", "");
			// 领药号 51
			drugListParm.addData("TICKETNO", "");
			// 药袋打印用病人名 52
			drugListParm.addData("BAGPRINTPATIENTNM", "");
			// 预备用打印内容（处方１） 53
			drugListParm.addData("FREEPRINTITEM_PRESC1", "");
			// 预备用打印内容（处方2） 54
			drugListParm.addData("FREEPRINTITEM_PRESC2", "");
			// 预备用打印内容（处方3） 55
			drugListParm.addData("FREEPRINTITEM_PRESC3", "");
			// 预备用打印内容（处方4） 56
			drugListParm.addData("FREEPRINTITEM_PRESC4", "");
			// 预备用打印内容（处方5） 57
			drugListParm.addData("FREEPRINTITEM_PRESC5", "");
			// 预备用打印内容（药品1） 58
			drugListParm.addData("FREEPRINTITEM_DRUG1", "");
			// 预备用打印内容（药品2） 59
			drugListParm.addData("FREEPRINTITEM_DRUG2", "");
			// 预备用打印内容（药品3） 60
			drugListParm.addData("FREEPRINTITEM_DRUG3", "");
			// 预备用打印内容（药品4） 61
			drugListParm.addData("FREEPRINTITEM_DRUG4", "");
			// 预备用打印内容（药品5） 62
			drugListParm.addData("FREEPRINTITEM_DRUG5", "");
			// 综合摆药用标志位(在中国不使用 63
			drugListParm.addData("SYNTHETICFLG", "");
			// 0:不切纸、1:在此处方的追后一包上切纸 64
			drugListParm.addData("CUTFLG", "");
			// 调剂时间（number型、无法输入汉字） 65
			drugListParm.addData("PHARMACYTIME", "");
			// 药品上的刻印 66
			drugListParm.addData("CARVEDSEAL", "");
			// 药品刻印简称 67
			drugListParm.addData("CARVEDSEALABB", "");
			// 处方信息条形码１ 68
			drugListParm.addData("PREBARCODE1", "");
			// 处方信息条形码２ 69
			drugListParm.addData("PREBARCODE2", "");
			// 药品信息条形码 70
			drugListParm.addData("PREDRUGBARCODE", "");
			// 条形码格式 71
			drugListParm.addData("PREBARCODEFMT", "");
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "2");
		}
		parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction", "onATCO", parm);
		if (parm.getErrCode() < 0) {
			messageBox("送包药机失败");
			return;
		}
		if (count > 0)
			messageBox("送包药机成功");
	}

	/**
	 * 包药机数据
	 * 
	 * @param orgCode
	 * @param counterNo
	 * @return
	 */
	public TParm getPHAcounterNoData(String orgCode, String counterNo) {
		return new TParm(getDBTool().select(" SELECT MACHINENO,ATC_TYPE " + " FROM PHA_COUNTERNO" + " WHERE ORG_CODE='"
				+ orgCode + "' AND COUNTER_NO='" + counterNo + "'"));
	}

	/**
	 * 取得药品发药单位和库存单位转换率
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getPHAOrderTransRate(String orderCode) {
		return new TParm(getDBTool().select(" SELECT DOSAGE_QTY/STOCK_QTY TRANS_RATE,MEDI_QTY " + " FROM PHA_TRANSUNIT "
				+ " WHERE ORDER_CODE='" + orderCode + "'"));
	}

	/**
	 * 取得药品数据
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderData(String orderCode) {
		return new TParm(getDBTool()
				.select(" SELECT ORDER_DESC,GOODS_DESC,ALIAS_DESC,TRADE_ENG_DESC,DESCRIPTION,MAN_CODE,SPECIFICATION"
						+ " FROM SYS_FEE" + " WHERE ORDER_CODE='" + orderCode + "'"));
	}

	/**
	 * 取得科室名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT DEPT_CHN_DESC" + " FROM SYS_DEPT " + " WHERE DEPT_CODE='" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * 取得人员姓名
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getDrDesc(String userId) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT USER_NAME " + " FROM SYS_OPERATOR " + " WHERE USER_ID='" + userId + "'"));
		String userName = "";
		if (parm.getCount() > 0)
			userName = parm.getValue("USER_NAME", 0);
		return userName;
	}

	/**
	 * 取得单位名称
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getUnitDesc(String unitCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT " + " WHERE UNIT_CODE='" + unitCode + "'"));
		String unitDesc = "";
		if (parm.getCount() > 0)
			unitDesc = parm.getValue("UNIT_CHN_DESC", 0);
		return unitDesc;
	}

	/**
	 * 取得用药途径名称
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getRouteDesc(String routeCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE " + " WHERE ROUTE_CODE='" + routeCode + "'"));
		String routeDesc = "";
		if (parm.getCount() > 0)
			routeDesc = parm.getValue("ROUTE_CHN_DESC", 0);
		return routeDesc;
	}

	/**
	 * 取得频次数据
	 * 
	 * @param freqCode
	 * @return
	 */
	public TParm getFreqData(String freqCode) {
		TParm parm = new TParm(getDBTool().select(" SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION " + " FROM SYS_PHAFREQ "
				+ " WHERE FREQ_CODE='" + freqCode + "'"));
		return parm;
	}

	/**
	 * 取得生产厂商数据
	 * 
	 * @param freqCode
	 * @return
	 */
	public String getManDesc(String manCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT MAN_CHN_DESC " + " FROM SYS_MANUFACTURER " + " WHERE MAN_CODE='" + manCode + "'"));
		String manDesc = "";
		if (parm.getCount() > 0)
			manDesc = parm.getValue("MAN_CHN_DESC", 0);
		return manDesc;
	}

	// =============================== chenxi modify 20130520
	/**
	 * 取得身份名称
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getCtzDesc(String ctzCode) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT CTZ_DESC " + " FROM SYS_CTZ " + " WHERE CTZ_CODE='" + ctzCode + "'"));
		String ctzDesc = "";
		if (parm.getCount() > 0)
			ctzDesc = parm.getValue("CTZ_DESC", 0);
		return ctzDesc;
	}

	// ========================= chenxi modify 20130603
	/**
	 * 取得药品一片的剂量
	 */
	public String getMediQty(String orderCode) {
		TParm parm = new TParm(
				getDBTool().select("SELECT MEDI_QTY FROM PHA_TRANSUNIT " + " WHERE ORDER_CODE = '" + orderCode + "'"));
		String qty = "";
		if (parm.getCount() > 0)
			qty = parm.getValue("MEDI_QTY", 0);
		return qty;
	}

	/**
	 * 得到his编码
	 * 
	 * @param ctzCode
	 * @return
	 */
	public String getBarCode(String order_code) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT HIS_ORDER_CODE " + " FROM SYS_FEE_SPC " + " WHERE ORDER_CODE='" + order_code + "'"));
		String hisCode = "";
		if (parm.getCount() > 0)
			hisCode = parm.getValue("HIS_ORDER_CODE", 0);
		return hisCode;
	}

	/**
	 * 得到数据库访问Tool
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 取得表格选中行数据
	 * 
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableSelectRowData(String rowName, String tableName) {
		return getTableRowData(getTable(tableName).getSelectedRow(), rowName, tableName);
	}

	/**
	 * 按行号取得该行数据
	 * 
	 * @param row
	 *            int
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableRowData(int row, String rowName, String tableName) {
		return getTable(tableName).getParmValue().getValue(rowName, row);
	}

	/**
	 * 取得表格控件
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	/**
	 * 药房叫号
	 */
	public void onCall() {
		/**
		 * if (getValueString("EXEC_DEPT_CODE").length() == 0 ||
		 * getValueString("COUNTER_NO").length() == 0) { messageBox("请选中一个药房和领药窗口");
		 * return; } if (this.getValueString("MR_NO").length() == 0) {
		 * messageBox("请输入病案号"); return; }
		 * 
		 * TParm parm = new TParm(); parm.setData("COUNTER_NO",
		 * getValueString("COUNTER_NO")); parm.setData("EXEC_DEPT_CODE",
		 * getDeptDesc(getValueString("EXEC_DEPT_CODE")));
		 * 
		 * parm.setData("MR_NO", this.getValueString("MR_NO"));
		 * openDialog("%ROOT%\\config\\pha\\PHACallNoDiag.x", parm);
		 **/
		// $$================add by lx 2012/02/23 start==================$$//
		if (this.getValueString("MR_NO").length() == 0) {
			messageBox("请输入病案号");
			return;
		}
		// 查询对应参数;
		String sql = "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
		sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "'";
		sql += " AND a.SEX_CODE=b.ID";
		TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
		String patName = patParm.getValue("PAT_NAME", 0);
		if (patName.equals("")) {
			this.messageBox("无此病患");
			return;
		}
		String strSend = this.getValueString("MR_NO") + "|";
		strSend += patParm.getValue("PAT_NAME", 0) + "|";
		strSend += patParm.getValue("SEX", 0) + "|";
		strSend += patParm.getValue("BIRTH_DATE", 0) + "|";
		strSend += Operator.getIP();
		// System.out.println("========sendString=======" + strSend);
		TParm inParm = new TParm();
		inParm.setData("msg", strSend);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doPHACallNo", inParm);

		// $$================add by lx 2012/02/23 end====================$$//

	}

	/**
	 * 已领药
	 */
	public void onArrive() {
		if (this.getValueString("MR_NO").length() == 0) {
			messageBox("请输入病案号");
			return;
		}
		// 查询对应参数;
		String sql = "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
		sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "'";
		sql += " AND a.SEX_CODE=b.ID";
		TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
		String patName = patParm.getValue("PAT_NAME", 0);
		if (patName.equals("")) {
			this.messageBox("无此病患");
			return;
		}
		String strSend = this.getValueString("MR_NO") + "|";
		strSend += patParm.getValue("PAT_NAME", 0) + "|";
		strSend += patParm.getValue("SEX", 0) + "|";
		strSend += patParm.getValue("BIRTH_DATE", 0) + "|";
		strSend += Operator.getIP();
		// System.out.println("========sendString=======" + strSend);
		TParm inParm = new TParm();
		inParm.setData("msg", strSend);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doPHAArriveCallNo", inParm);

	}

	/**
	 * 取得科室名称
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getCounterNoDesc(String orgCode, String counterNo) {
		TParm parm = new TParm(getDBTool().select(" SELECT COUNTER_DESC" + " FROM PHA_COUNTERNO" + " WHERE ORG_CODE='"
				+ orgCode + "'" + " AND   COUNTER_NO = '" + counterNo + "'"));
		return parm.getValue("COUNTER_DESC", 0);
	}

	/**
	 * 初始化界面数据
	 */
	private void initUIData() {
		setValue("EXEC_DEPT_CODE", Operator.getDept());
		setValue("COUNTER_NO", getCounterNoByUser());
	}

	/**
	 * 得到领药窗口号
	 * 
	 * @return String
	 */
	public String getCounterNoByUser() {
		String user = "";
		if (type.equals("Dispense"))
			user = "A.DOSAGE_USER";
		else if (type.equals("Send") || type.equals("Return"))
			user = "A.DISPENSE_USER";
		else
			return "";
		String sql = "  SELECT A.COUNTER_NO" + "    FROM PHA_COUNTERNO A ,IND_ORG B "
				+ "   WHERE B.ORG_CODE=A.ORG_CODE " + "     AND A.CHOSEN_FLG = 'Y' " + "     AND B.REGION_CODE ='"
				+ Operator.getRegion() + "' " + "     AND A.ORG_CODE='" + Operator.getDept() + "'" + "     AND   "
				+ user + " = '" + Operator.getID() + "'";
		TParm parm = new TParm(getDBTool().select(sql));
		if (parm.getCount("COUNTER_NO") <= 0)
			return "";
		return parm.getValue("COUNTER_NO", 0);
	}

	public void onPaster() {
		// fux modify 20180906 自备药不显示
		/*
		 * if (((TTable) this.getComponent("Table_UP")).getSelectedRow() < 0) {
		 * messageBox("未选中处方"); return; }
		 */
		if ("WD".equals(orderType))
			// onPrint(nowOrdList.getRxNo(),(nowOrdList.getPresrtNo()==0?"":nowOrdList.getPresrtNo()+""));
			// //modify by huangtt 20150121 nowOrdList.getPresrtNo()
			onPrint(nowOrdList.getRxNo(), nowOrdList.getRxPresertNo()); // modify
																		// by
																		// huangtt
																		// 20150121
																		// nowOrdList.getPresrtNo()
		else
			onPrintTCM();
	}

	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		// 修改读医疗卡功能 begin luhai 2012-2-27
		// TParm patParm = EKTIO.getInstance().getPat();
		// if (patParm.getErrCode() < 0) {
		// this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
		// return;
		// }
		// setValue("MR_NO", patParm.getValue("MR_NO"));
		// onMrNo();
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("请查看医疗卡是否正确使用");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("MR_NO", parm.getValue("MR_NO"));
		onMrNo();
		// 修改读医疗卡功能 end luhai 2012-2-27
	}

	/**
	 * 处方签回车事件
	 * 
	 * @author liyh
	 * @date 20120914
	 */
	public void onRxNo() {
		onQuery();
	}

	/**
	 * 保存病案号和药框电子标签ID关系
	 * 
	 * @author liyh
	 * @date 20120830
	 */
	public boolean onSaveMedBasket(String bastkId) {

		// 查询电子标签状态
		String flag = "1";// getEleTagStatus(bastkId);
		if (UPDATE_FLAG_TRUE.equals(flag)) {// 更新药框成功，保存病人和药框电子标签关系
			String ip = Operator.getIP();
			String opdUser = Operator.getName();

			TParm parm = new TParm();
			parm.setData("MR_NO", getValueString("MR_NO"));
			TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
			TParm medParm = new TParm();
			medParm.setData("MR_NO", getValueString("MR_NO"));
			medParm.setData("BASKET_ID", getValueString("BASKET_ID"));
			medParm.setData("RX_NO", getValueString("RX_NO"));
			medParm.setData("CASE_NO", caseNo == null ? "-1" : caseNo);
			medParm.setData("PAT_NAME", pationParm.getValue("PAT_NAME", 0));
			medParm.setData("SEX_TYPE", pationParm.getValue("SEX_NAME", 0));
			medParm.setData("AGE", pationParm.getValue("AGE", 0));
			medParm.setData("OPT_USER", opdUser);
			medParm.setData("OPT_TERM", ip);

			// 保存病案号和药框ID关系
			// System.out.println("----update medbasktet
			// sql:"+PhaSQL.savBasketInfo(medParm));
			TParm result = new TParm(TJDODBTool.getInstance().update(PhaSQL.savBasketInfo(medParm)));
			if (result.getErrCode() < 0) {
				return false;
			}
		} else {
			return false;
		}
		return true;

	}

	/**
	 * 查看电子标签状态
	 * 
	 * @param basketId
	 * @return 1:成功；-1不成功
	 */
	public String getEleTagStatus(String basketId) {
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", basketId + uuid.toString());
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		try {
			Map<String, Object> map = eti.getLable(mp);
			if (null != map) {
				String resultStatus = (String) map.get("Status");
				if (!"10000".equals(resultStatus)) {// 成功状态 为10000
					return UPDATE_FLAG_TRUE;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return UPDATE_FLAG_TRUE;
		}

		return UPDATE_FLAG_TRUE;// UPDATE_FLAG_FLASE;
	}

	/**
	 * 调用发药框电子标签更新接口
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120903
	 */
	public void onBasketId() {
		if ("Dispense".equalsIgnoreCase(type)) {// 只有配药 才触发事件
			// 病案号
			String mrNo = getValueString("MR_NO");
			// 药框的电子标签id
			String bastkId = getValueString("BASKET_ID");
			// 处方签好
			String rxNoStr = getValueString("RX_NO");
			// System.out.println("parm==="+parm);
			if (null == mrNo || mrNo.length() <= 0) {
				this.messageBox("病案号不能为空,请先选择一条处方签记录");
				return;
			}

			if (null != bastkId && bastkId.length() > 0) {
				bastkId = bastkId.toUpperCase();
				TParm parm = new TParm();
				parm.setData("MR_NO", mrNo);
				// 通过病案号查询病人信息
				// System.out.println("------------通过病案号查询病人信息--sql:"+
				// PhaSQL.getPationInfo(parm));
				TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
				/************** 查询电子标签所在区域 by liyh 20130520 start ******************/
				TParm orgParm = new TParm();
				/*
				 * orgParm.setData("ORG_CODE", this.getValue("EXEC_DEPT_CODE")); TParm
				 * orgResultParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode
				 * (orgParm);
				 */
				/************** 查询电子标签所在区域 by liyh 20130520 end ******************/
				if (null != pationParm && pationParm.getCount() > 0) {// 如果不为空
					// 更细电子标签
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					// 登陆电子标签
					// login();
					String nameInfo = "";
					// 年龄
					String age = pationParm.getValue("AGE", 0);
					// 姓名
					String patName = pationParm.getValue("PAT_NAME", 0);
					// 性别
					String sex = pationParm.getValue("SEX_NAME", 0);
					// 拼第一行显示 7个字
					if (age.indexOf("月") != -1) {// <1岁 显示n月，长度为2-3个
						// 姓名长度不能大于4位
						patName = patName.length() > 4 ? patName.substring(0, 4) : patName;
						nameInfo = patName + age;
					} else {// >1岁，长度为1-2
						// 姓名长度不能大于5位
						patName = patName.length() > 5 ? patName.substring(0, 4) : patName;
						nameInfo = patName + age;
					}
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					uuid = UUID.randomUUID();

					m.put("ObjectId", uuid.toString());
					m.put("ObjectType", 3);
					m.put("ObjectName", "medBasket");
					// m.put("LabelNo", "01048A");
					// 电子标签id
					m.put("LabelNo", bastkId);
					// 基站id
					m.put("StationID", "2");
					// 第一行 显示用户名和年龄
					m.put("ProductName", nameInfo);
					// 第二回 显示病案号 和性别
					m.put("Spec", mrNo + " " + sex);
					// 第三行 电子标签二维条码含义
					m.put("ShelfNo", "SN1000");
					// 闪烁次数
					m.put("Light", 20);
					// 是否亮灯：true:亮
					m.put("Enabled", true);

					Iterator it = m.entrySet().iterator();
					// System.out.println("-------------绑定-电子标签更新内容---------start----------");
					ElectronicTagsInf eti = new ElectronicTagsImpl();
					// 调用电子标签接口
					Map<String, Object> map = eti.cargoUpdate(m);
					it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
						// System.out.println(entry.getKey() + "==============="
						// + entry.getValue());
					}
					// System.out.println("------------绑定--电子标签更新内容---------end----------");
					if (null != map) {
						String status = (String) map.get("Status");
						if (null != status && "10000".equals(status)) {// 更新电子标签状态成功
							/*
							 * boolean flag = onSaveMedBasketNew(bastkId,patName,age,sex); if(!flag){
							 * this.messageBox("保存病人和电子标签关系失败"); return; }
							 */

							return;
						} else {// 返回值 状态 不正确
							if (this.messageBox("提示", "更新药框电子标签失败，是否继续更新", 2) == 0) {// 是
								this.messageBox("请扫描电子标签");
								((TTextField) getComponent("BASKET_ID")).grabFocus();
								return;
							}
						}
					} else {// 没有返回值
						if (this.messageBox("提示", "更新药框电子标签失败，是否继续更新", 2) == 0) {// 是
							this.messageBox("请扫描电子标签");
							((TTextField) getComponent("BASKET_ID")).grabFocus();
							return;
						}
					}
					/*
					 * Map<String, Object> map = new LinkedHashMap<String, Object> ();
					 * map.put("ProductName", nameInfo); map.put("SPECIFICATION", mrNo +" "+sex );
					 * map.put("TagNo", bastkId); map.put("Light", 50); map.put("APRegion"
					 * ,orgResultParm.getValue("onQueryLabelByOrgCode", 0)); list.add(map); try{
					 * String url = Constant.LABELDATA_URL ;
					 * EleTagControl.getInstance().sendNewEleTag(list, url); }catch (Exception e) {
					 * // TODO: handle exception e.printStackTrace();
					 * System.out.println("调用电子标签服务失败"); }
					 */
				} else {
					this.messageBox("查询病人信息失败");
					return;
				}
			}
		}

	}

	/**
	 * 更新电子标签
	 * 
	 * @param bastkId
	 * @param nameInfo
	 * @param sex
	 * @param mrNo
	 * @return
	 * @author liyh
	 * @date 20120919
	 */
	public boolean sendEleTag(String bastkId, String nameInfo, String age, String mrNo, String sex) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		if (null == uuid)
			uuid = UUID.randomUUID();
		int lightCount = 20;
		if (null == nameInfo || "".equals(nameInfo) || nameInfo.trim().length() < 1) {// name 为空时 发药 清空电子标签 闪烁1次，
			lightCount = 1;
		}
		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// 电子标签id
		m.put("LabelNo", bastkId);
		// 基站id
		m.put("StationID", "2");
		// 第一行 显示用户名和年龄
		m.put("ProductName", nameInfo + age);
		// 第二回 显示病案号 和性别
		m.put("Spec", mrNo + " " + sex);
		// 第三行 电子标签二维条码含义
		if (null == nameInfo || "".equals(nameInfo) || nameInfo.trim().length() < 1) {// name 为空时 发药 清空电子标签 闪烁1次，
			m.put("ShelfNo", "");
		} else {
			m.put("ShelfNo", bastkId);
		}
		// 闪烁次数
		m.put("Light", 20);
		// 是否亮灯：true:亮
		m.put("Enabled", true);

		Iterator it = m.entrySet().iterator();
		// System.out.println("------------发药--电子标签更新内容---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// 调用电子标签接口
		Map<String, Object> map = eti.cargoUpdate(m);
		if (null != map) {
			it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
				// System.out.println(entry.getKey() + "===============" +
				// entry.getValue());
			}
			String status = (String) map.get("Status");
			if (null != status && "10000".equals(status)) {// 更新电子标签状态成功
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * 查询病人信息
	 */
	public boolean queryPationInfo(String bastkId, String mrNo, String rxNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// 通过病案号查询病人信息
		// System.out.println("------------通过病案号查询病人信息--sql:"+
		// PhaSQL.getPationInfo(parm));
		TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
		if (null != pationParm && pationParm.getCount() > 0) {// 如果不为空 更细电子标签
			String nameInfo = "";
			// 年龄
			String age = pationParm.getValue("AGE", 0);
			// 姓名
			String patName = pationParm.getValue("PAT_NAME", 0);
			// 性别
			String sex = pationParm.getValue("SEX_NAME", 0);
			// 拼第一行显示 7个字
			if (age.indexOf("月") != -1) {// <1岁 显示n月，长度为2-3个
				// 姓名长度不能大于4位
				patName = patName.length() > 4 ? patName.substring(0, 4) : patName;
				nameInfo = patName + age;
			} else {// >1岁，长度为1-2
				// 姓名长度不能大于5位
				patName = patName.length() > 5 ? patName.substring(0, 4) : patName;
				nameInfo = patName + age;
			}
			// 保存保存病案号和药框电子标签ID关系
			onSaveMedBasketNew(bastkId, patName, age, sex, mrNo, rxNo);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 保存病案号和药框电子标签ID关系
	 * 
	 * @author liyh
	 * @date 20120830
	 */
	public boolean onSaveMedBasketNew(String bastkId, String name, String age, String sex, String mrNO, String rxNO) {

		String ip = Operator.getIP();
		String opdUser = Operator.getName();
		TParm medParm = new TParm();
		medParm.setData("MR_NO", mrNO);
		medParm.setData("BASKET_ID", bastkId);
		medParm.setData("RX_NO", rxNO);
		medParm.setData("CASE_NO", caseNo == null ? "-1" : caseNo);
		medParm.setData("PAT_NAME", name);
		medParm.setData("SEX_TYPE", sex);
		medParm.setData("AGE", age);
		medParm.setData("OPT_USER", opdUser);
		medParm.setData("OPT_TERM", ip);

		// 保存病案号和药框ID关系
		// System.out.println("----update medbasktet
		// sql:"+PhaSQL.savBasketInfo(medParm));
		TParm result = new TParm(TJDODBTool.getInstance().update(PhaSQL.savBasketInfo(medParm)));
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;

	}

	public void onPrint(String rxNo, String rxPresrtNo) {
		TParm parm = onQuery(rxNo, rxPresrtNo);
		String Date = StringTool.getString(parm.getTimestamp("ORDER_DATE", 0), "yyyy/MM/dd"); // parm.getValue("ORDER_DATE",0);
		String INDT = parm.getValue("COUNTER_NO", 0);
		String INDNO = parm.getValue("PRINT_NO", 0);
		String Name = parm.getValue("PAT_NAME", 0);
		String Sex = parm.getValue("CHN_DESC", 0);
		// String Age = parm.getValue("AGE", 0) + "岁";
		Timestamp sysDate = TJDODBTool.getInstance().getDBTime();// add by sunqy
																	// 20140701
																	// 设置年龄
		String Age = com.javahis.util.DateUtil.showAge((Timestamp) parm.getData("BIRTH_AGE", 0), sysDate);// add by
																											// sunqy
																											// 20140701
																											// 设置年龄
		String kb = parm.getValue("DEPT_CHN_DESC", 0);
		String Room = parm.getValue("USER_NAME", 0);
		String MR_NO = parm.getValue("MR_NO", 0);
		String checkuUser = parm.getValue("CHECK_USER", 0);
		String orderCat1 = "";
		if (orderType.equals("WD")) {
			orderCat1 = "'PHA_W','PHA_C'";
		} else
			orderCat1 = "'PHA_G'";
		TParm tparmPHA = getPHAParm(rxNo, orderCat1, rxPresrtNo);
		if (tparmPHA.getCount() <= 0) {
			messageBox("配药确认单无打印数据");
			return;
		}

		// 大类 E 外用
		// F 大量点滴
		// I 针剂
		// O 口服

		TParm oParm = new TParm();
		// ==liling 20140825 modify start ======
		/*
		 * //==liling 20140825 屏蔽start ====== TParm eParm = new TParm(); TParm fiParm =
		 * new TParm(); int ei = 0 ; int fii = 0 ; int oi = 0 ; for(int i = 0 ; i <
		 * tparmPHA.getCount("GOODS_DESC");i++){ TParm rowParm = tparmPHA.getRow(i);
		 * String doseType = tparmPHA.getValue("DOSE_TYPE",i); if("F".equals(doseType)||
		 * "I".equals(doseType)){//大量点滴/针剂 fiParm.addData("MIAN_FLG",
		 * rowParm.getValue("MIAN_FLG")); fiParm.addData("LINK_NO",
		 * rowParm.getValue("LINK_NO")); fiParm.addData("GOODS_DESC",
		 * rowParm.getValue("GOODS_DESC")); fiParm.addData("SPECIFICATION",
		 * rowParm.getValue("SPECIFICATION")); fiParm.addData("ROUTE_CODE",
		 * rowParm.getValue("ROUTE_CODE")); fiParm.addData("FREQ_CODE",
		 * rowParm.getValue("FREQ_CODE")); fiParm.addData("BATCH_NO",
		 * rowParm.getValue("BATCH_NO")); fiParm.addData("QTY",
		 * rowParm.getValue("QTY")); fii = rowParm.getInt("DOSAGE_PRN_TOT"); }else if
		 * ("E".equals(doseType)){//外用 eParm.addData("MIAN_FLG",
		 * rowParm.getValue("MIAN_FLG")); eParm.addData("LINK_NO",
		 * rowParm.getValue("LINK_NO")); eParm.addData("GOODS_DESC",
		 * rowParm.getValue("GOODS_DESC")); eParm.addData("SPECIFICATION",
		 * rowParm.getValue("SPECIFICATION")); eParm.addData("ROUTE_CODE",
		 * rowParm.getValue("ROUTE_CODE")); eParm.addData("FREQ_CODE",
		 * rowParm.getValue("FREQ_CODE")); eParm.addData("BATCH_NO",
		 * rowParm.getValue("BATCH_NO")); eParm.addData("QTY", rowParm.getValue("QTY"));
		 * ei = rowParm.getInt("DOSAGE_PRN_TOT"); }else if("O".equals(doseType)){//口服
		 * oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
		 * oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
		 * oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
		 * oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
		 * oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
		 * oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
		 * oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO")); oParm.addData("QTY",
		 * rowParm.getValue("QTY")); oi = rowParm.getInt("DOSAGE_PRN_TOT"); } if (parm
		 * != null) { TParm date = null;
		 * 
		 * if(fiParm.getCount("GOODS_DESC")> 0){ date = getPringData(Date, INDT, INDNO,
		 * Name, Sex, Age, kb, Room, MR_NO, checkuUser, fiParm); // 调用打印方法 //
		 * this.messageBox("date" + date); date.setData("Title", "TEXT",
		 * "门(急)诊配药确认单");//==liling 20140825 add this.openPrintWindow(IReportTool
		 * .getInstance().getReportPath("phaInd_V45.jhw"), IReportTool.getInstance
		 * ().getReportParm("phaInd_V45.class",date),false); }
		 * if(eParm.getCount("GOODS_DESC")> 0){ date = getPringData(Date, INDT, INDNO,
		 * Name, Sex, Age, kb, Room, MR_NO, checkuUser, eParm); // 调用打印方法 //
		 * this.messageBox("date" + date); date.setData("Title", "TEXT",
		 * "门(急)诊配药确认单");//==liling 20140825 add this.openPrintWindow(IReportTool
		 * .getInstance().getReportPath("phaInd_V45.jhw"), IReportTool.getInstance
		 * ().getReportParm("phaInd_V45.class",date),false); }
		 */// ==liling 20140825 屏蔽 end ======
		for (int i = 0; i < tparmPHA.getCount("GOODS_DESC"); i++) {
			TParm rowParm = tparmPHA.getRow(i);
			oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
			oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
			oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
			oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
			oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
			oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
			oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
			oParm.addData("QTY", rowParm.getValue("QTY"));
		}
		if (parm != null) {
			TParm date = null;
			if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "门(急)诊配药确认单");
				String previewSwitch = IReportTool.getInstance().getPrintSwitch("phaInd_V45.previewSwitch");
				if (previewSwitch.equals(IReportTool.OFF)) {// 预览开关关上 直接打印
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
				} else {// 预览开关开或没设预览开关 ；默认预览
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), false);
				}
				date = null;
			}
			if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "门(急)诊配药确认单【底】");// ==liling
																// 20140825 add
				this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
						IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
				date = null;
			}
			// ==liling 20140825 modify end ======
		} else {
			this.messageBox("E0010"); // 弹出提示对话框（“没打印有数据”）
			return;
		}
	}

	public void onPrintAuto(String rxNo, String rxPresrtNo) {
		// this.messageBox("666666");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("12======================="+sdf.format(SystemTool.getInstance().getDate()));
		TParm parm = onQuery(rxNo, rxPresrtNo);
		String Date = StringTool.getString(parm.getTimestamp("ORDER_DATE", 0), "yyyy/MM/dd"); // parm.getValue("ORDER_DATE",0);
		String INDT = parm.getValue("COUNTER_NO", 0);
		String INDNO = parm.getValue("PRINT_NO", 0);
		String Name = parm.getValue("PAT_NAME", 0);
		String Sex = parm.getValue("CHN_DESC", 0);
		String Age = parm.getValue("AGE", 0) + "岁";
		String kb = parm.getValue("DEPT_CHN_DESC", 0);
		String Room = parm.getValue("USER_NAME", 0);
		String MR_NO = parm.getValue("MR_NO", 0);
		String checkuUser = parm.getValue("CHECK_USER", 0);
		String orderCat1 = "";
		if (orderType.equals("WD")) {
			orderCat1 = "'PHA_W','PHA_C'";
		} else
			orderCat1 = "'PHA_G'";

		// System.out.println("13======================="+sdf.format(SystemTool.getInstance().getDate()));
		TParm tparmPHA = getPHAParm(rxNo, orderCat1, rxPresrtNo);
		if (tparmPHA.getCount() <= 0) {
			messageBox("配药确认单无打印数据");
			return;
		}

		// System.out.println("14======================="+sdf.format(SystemTool.getInstance().getDate()));
		// 大类 E 外用
		// F 大量点滴
		// I 针剂
		// O 口服
		TParm eParm = new TParm();
		TParm fiParm = new TParm();
		TParm oParm = new TParm();
		int ei = 0;
		int fii = 0;
		int oi = 0;
		for (int i = 0; i < tparmPHA.getCount("GOODS_DESC"); i++) {
			TParm rowParm = tparmPHA.getRow(i);
			String doseType = tparmPHA.getValue("DOSE_TYPE", i);
			if ("F".equals(doseType) || "I".equals(doseType)) {
				fiParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				fiParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				fiParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				fiParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				fiParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				fiParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				fiParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				fiParm.addData("QTY", rowParm.getValue("QTY"));
				if (fii == 0) {
					fii = rowParm.getInt("DOSAGE_PRN_TOT");
				}

			} else if ("E".equals(doseType)) {
				eParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				eParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				eParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				eParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				eParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				eParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				eParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				eParm.addData("QTY", rowParm.getValue("QTY"));
				if (ei == 0) {
					ei = rowParm.getInt("DOSAGE_PRN_TOT");
				}
			} else if ("O".equals(doseType)) {
				oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				oParm.addData("QTY", rowParm.getValue("QTY"));
				if (oi == 0) {
					oi = rowParm.getInt("DOSAGE_PRN_TOT");
				}
			}
		}
		// System.out.println("15======================="+sdf.format(SystemTool.getInstance().getDate()));

		if (parm != null) {
			TParm date = null;
			if (fiParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, fiParm);
				date.setData("Title", "TEXT", "门(急)诊配药确认单");
				// 调用打印方法
				// this.messageBox("date" + date);
				for (int i = 0; i < fii; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, fiParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, fiParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单【底】");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			} else if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "门(急)诊配药确认单");
				// 调用打印方法
				// this.messageBox("date" + date);
				for (int i = 0; i < oi; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, oParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, oParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单【底】");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			} else if (eParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, eParm);
				date.setData("Title", "TEXT", "门(急)诊配药确认单");
				for (int i = 0; i < ei; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, eParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, eParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "门(急)诊配药确认单【底】");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			}

		} else {
			this.messageBox("E0010"); // 弹出提示对话框（“没打印有数据”）
			return;
		}
	}

	private TParm getPringData(String Date, String INDT, String INDNO, String Name, String Sex, String Age, String kb,
			String Room, String MR_NO, String checkuUser, TParm tparmPHA) {
		TParm date = new TParm();
		tparmPHA.addData("SYSTEM", "COLUMNS", "MIAN_FLG");
		tparmPHA.addData("SYSTEM", "COLUMNS", "LINK_NO");
		tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
		tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
		tparmPHA.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		tparmPHA.setCount(tparmPHA.getCount("GOODS_DESC"));
		// 表头数据 MR_NO
		// date.setData("Title", "TEXT", "门(急)诊配药确认单");//==liling 20140825 add
		date.setData("INDT", "TEXT", "领药台：" + INDT);
		date.setData("INDNO", "TEXT", "领药号：" + INDNO);
		date.setData("Name", "TEXT", Name);
		date.setData("Sex", "TEXT", Sex);
		date.setData("Age", "TEXT", Age);
		date.setData("kb", "TEXT", kb);
		date.setData("Room", "TEXT", Room);
		date.setData("Date", "TEXT", Date);
		date.setData("MR_NO", "TEXT", MR_NO);
		date.setData("CHECK_USER", "TEXT", checkuUser);
		date.setData("table", tparmPHA.getData());
		return date;
	}

	public TParm onQuery(String rxNo, String rxPresrtNo) {
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = " SELECT A.CASE_NO AS CASE_NO,  A.COUNTER_NO AS COUNTER_NO,A.PRINT_NO AS PRINT_NO,B.Pat_Name AS Pat_Name,C.CHN_DESC  ,"
				+ "   FLOOR (MONTHS_BETWEEN (SYSDATE, B.BIRTH_DATE) / 12) AS AGE, E.DEPT_CHN_DESC AS  DEPT_CHN_DESC,D.USER_NAME AS USER_NAME, "
				+ "   A.ORDER_DATE AS ORDER_DATE,A.MR_NO AS MR_NO,F.USER_NAME AS CHECK_USER, "
				+ "   B.BIRTH_DATE AS BIRTH_AGE "// add by sunqy20140701 取得出生日期
				+ "  FROM  OPD_ORDER A ,SYS_PATINFO B,SYS_DICTIONARY C,SYS_OPERATOR D,SYS_DEPT E,SYS_OPERATOR F"
				// + " WHERE A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'" //add by
				// huangtt 20150121
				+ "  WHERE A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' " // modify by wangjc 20190109
																							// 爱育华反应药房保存较慢
				+ "  AND B.MR_NO = A.MR_NO  AND C.ID = B.SEX_CODE" + "  AND C.GROUP_ID = 'SYS_SEX'"
				+ "  AND D.USER_ID = A.DR_CODE" + "  AND E.DEPT_CODE = A.DEPT_CODE"
				+ "  AND A.PHA_CHECK_CODE=F.USER_ID(+) " + "  AND A.ORDER_CAT1_CODE LIKE 'PHA%' "
				+ "  GROUP BY   A.COUNTER_NO, A.PRINT_NO, B.Pat_Name, C.CHN_DESC, B.BIRTH_DATE,"
				+ "  A.DEPT_CODE,E.DEPT_CHN_DESC,D.USER_NAME,A.ORDER_DATE,A.CASE_NO,A.MR_NO,F.USER_NAME ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("配药单:"+sql);
		return parm;
	}

	public TParm getPHAParm(String rxNo, String orderCat1, String rxPresrtNo) {
		String releaseFlg = "";
		if ("Dispense".equals(type)) {
			releaseFlg = " AND RELEASE_FLG = 'N' ";
		}
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = "SELECT CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '主' ELSE '' END MIAN_FLG, "
				+ "       A.LINK_NO, A.ORDER_DESC AS GOODS_DESC   , A.SPECIFICATION, "
				+ "       D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,REGEXP_REPLACE(A.DISPENSE_QTY,'^\\D','0.') || ' ' || B.UNIT_CHN_DESC QTY, "
				+ "       A.BATCH_NO  , CASE WHEN A.ATC_FLG = 'Y' THEN '√' ELSE ' ' END ATC_FLG ,A.DOSE_TYPE,D.DOSAGE_PRN_TOT  "
				+ "       ,A.RELEASE_FLG "// add by wangjc 20171107
				+ "FROM OPD_ORDER A,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D "
				+ "WHERE     A.DISPENSE_UNIT = B.UNIT_CODE " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.ORDER_CAT1_CODE IN (" + orderCat1 + ") "
				// + " AND A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'" //add by
				// huangtt 20150121
				+ "       AND A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' " // modify by wangjc 20190109
																							// 爱育华反应药房保存较慢
				+ releaseFlg // add by wangjc 20171107 门诊配药单不打印自备药
				+ "       ORDER BY LINK_NO, SEQ_NO";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm getPHAInfusion(String rxNo, String rxPresrtNo) {
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = "SELECT CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '主' ELSE '' END MIAN_FLG, "
				+ "       A.LINK_NO, A.ORDER_DESC AS GOODS_DESC   , A.SPECIFICATION, "
				// modify by wangbin 20140806 解决bug开药为0.6g的口服药打印药签显示.6
				+ "       D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,REGEXP_REPLACE(A.MEDI_QTY,'^\\D','0.') || ' ' || B.UNIT_CHN_DESC QTY,"
				+ " A.DISPENSE_QTY || ' ' || N.UNIT_CHN_DESC AS  DISPENSE_QTY, "
				+ "       A.BATCH_NO  , A.DOSE_TYPE,D.DOSAGE_PRN_TOT,A.MR_NO,E.USER_NAME DOSAGE_NAME ,F.USER_NAME DR_NAME ,"
				// ==liling 20140703 屏蔽 年龄调用公用方法计算 start==
				// + " A.MEDI_QTY || B.UNIT_CHN_DESC QQ,"//添加开药量add by
				// huangjw 20150309
				// +
				// " FLOOR(MONTHS_BETWEEN (SYSDATE, BIRTH_DATE)/12)||'岁'|| "
				// +
				// " ABS(TO_NUMBER(EXTRACT(MONTH FROM SYSDATE)-EXTRACT(MONTH FROM
				// BIRTH_DATE)))||'月'|| "
				// +
				// " ABS(TO_NUMBER(EXTRACT(DAY FROM SYSDATE)-EXTRACT(DAY FROM BIRTH_DATE)))||'日'
				// AS AGE,"
				// ==liling 20140703 屏蔽 年龄调用公用方法计算 end==
				+ "		  G.PAT_NAME AS PAT_NAME ,H.CHN_DESC , I.DEPT_CHN_DESC,  "
				// +
				// " A.DOSAGE_QTY || ' ' || N.UNIT_CHN_DESC AS
				// DOSAGE_QTY,A.TAKE_DAYS,K.MATERIAL_LOC_CODE,M.ADM_DATE "
				+ "     A.DOSAGE_QTY/P.DOSAGE_QTY || ' ' || O.UNIT_CHN_DESC "
				+ " AS DOSAGE_QTY,A.TAKE_DAYS,K.MATERIAL_LOC_CODE,M.ADM_DATE,"
				+ " REGEXP_REPLACE(A.MEDI_QTY,'^\\D','0.')/REGEXP_REPLACE(P.MEDI_QTY,'^\\D','0.') AS DOSAGE_QTYNEW,"
				+ " N.UNIT_CHN_DESC  AS UNIT_CHN_DESCNEW ,MOD(A.DOSAGE_QTY,P.DOSAGE_QTY) AS MOD_DOSAGE"
				+ " ,MOD(A.MEDI_QTY,P.MEDI_QTY) AS MOD_MEDI,A.DOSAGE_QTY AS DOSAGE1,P.DOSAGE_QTY AS DOSAGE2,"
				+ " A.MEDI_QTY AS MEDI1,P.MEDI_QTY AS MEDI2,O.UNIT_CHN_DESC AS UNIT_STOCK,A.LINKMAIN_FLG ,S.DRUG_NOTES_PATIENT,A.INFLUTION_RATE "
				+ "FROM OPD_ORDER A,SYS_UNIT B,SYS_UNIT N,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_OPERATOR E,SYS_OPERATOR F ,SYS_PATINFO G,"
				+ " SYS_DICTIONARY H ,SYS_DEPT I, IND_STOCKM K ,REG_PATADM M,SYS_UNIT O,PHA_TRANSUNIT P,PHA_BASE S "
				+ "WHERE     A.MEDI_UNIT = B.UNIT_CODE " + "       AND A.DOSAGE_UNIT = N.UNIT_CODE "
				// fux modify 20151208
				+ "       AND P.STOCK_UNIT = O.UNIT_CODE " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.PHA_DOSAGE_CODE=E.USER_ID(+)"// cyl 2019.3.5
				+ "       AND A.DR_CODE=F.USER_ID " + "       AND A.CAT1_TYPE='PHA' "
				+ "       AND  G.MR_NO = A.MR_NO  " + "       AND H.ID = G.SEX_CODE" +
				// fux modify 20151008 加入料位
				" AND A.ORDER_CODE = K.ORDER_CODE(+)" + " AND A.EXEC_DEPT_CODE = K.ORG_CODE(+)"
				+ " AND A.CASE_NO = M.CASE_NO(+)  " + "       AND H.GROUP_ID = 'SYS_SEX'" +
				// yanmm 20170825 加入药品字典病人用药注意备注
				"       AND A.ORDER_CODE = S.ORDER_CODE " + "      AND I.DEPT_CODE = A.DEPT_CODE"
				// fux modify 20151208
				+ "     AND A.ORDER_CODE = P.ORDER_CODE "
				// + " AND A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'"; //add
				// by huangtt 20150121
				+ " AND A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' ";// modify by wangjc 20190109
																						// 爱育华反应药房保存较慢
		System.out.println("查询SQL————————————————————————————————" + sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public void onPrintTCM() {
		String SQL = " SELECT CASE_NO,ADM_TYPE,MR_NO" + " FROM   OPD_ORDER" + " WHERE RX_NO = '" + nowOrdList.getRxNo()
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		if (parm.getCount() < 0) {
			messageBox("配药确认单无打印数据");
			return;
		}
		// RegPatAdm regPatAdm = new RegPatAdm();
		// regPatAdm.setCaseNo(parm.getValue("CASE_NO",0));
		// regPatAdm.onQuery();
		// OpdOrder opdOrder = new OpdOrder();
		// opdOrder.setCaseNo(regPatAdm.getCaseNo());
		// opdOrder.setMrNo(regPatAdm.getMrNo());
		// opdOrder.setDeptCode(getTableSelectRowData("DEPT_CODE","Table_UP"));
		// opdOrder.setDrCode(getTableSelectRowData("DR_CODE","Table_UP"));
		// opdOrder.setAdmType(parm.getValue("ADM_TYPE",0));
		// opdOrder.onQuery();
		//
		// PatInfo patInfo = new PatInfo();
		// patInfo.setMrNo(regPatAdm.getMrNo());
		// patInfo.onQuery();
		// ---------------

		ODO odo = new ODO(parm.getValue("CASE_NO", 0), parm.getValue("MR_NO", 0),
				getTableSelectRowData("DEPT_CODE", "Table_UP"), getTableSelectRowData("DR_CODE", "Table_UP"),
				parm.getValue("ADM_TYPE", 0));

		odo.getOpdOrder().addEventListener(odo.getOpdOrder().ACTION_SET_ITEM, this, "onSetItemEvent");
		odo.onQuery();

		// ---------------
		/*
		 * TParm inParam=PHARxSheetTool.getInstance().getOrderPrintParm(
		 * getTableSelectRowData("DEPT_CODE","Table_UP"), "3", odo.getOpdOrder(),
		 * nowOrdList.getRxNo(),"",odo.getRegPatAdm(),odo.getPatInfo());
		 */
		TParm inParam = PHARxSheetTool.getInstance().getOrderPrintParm(getTableSelectRowData("DEPT_CODE", "Table_UP"),
				"3", odo, nowOrdList.getRxNo(), "");
		openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHAChnOrderSheet.jhw", inParam, true);
	}

	public void onInitUIHistory() {
		callFunction("UI|setMenuConfig", getConfigString("MENU." + type));
		callFunction("UI|onInitMenu");
		callFunction("UI|setTitle", "药房历史信息查询");
		callFunction("UI|" + PANEL_HEAD + "|addItem", PANEL_HEAD_NAME, getConfigString("PANEL_HEAD." + orderType), null,
				false);
		if (orderType.equals("DD"))
			callFunction("UI|" + PANEL_MIDDLE + "|addItem", PANEL_MIDDLE_NAME,
					getConfigString("PANEL_MIDDLE." + orderType), null, false);

		if ("WD".equalsIgnoreCase(orderType)) {
			TTable ta = ((TTable) this.getComponent("Table_UP"));
			callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));
			// ====================pangben modify 20110417 start 添加区域列
			callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
			callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
					"1,left;2,left;3,left;4,right;5,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left");
			// ====================pangben modify 20110417 stop
			callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));

			callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14");
			callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
					"3,left;4,right;5,left;6,right;7,right;8,right;9,left;10,left;11,left;12,left;13,right;14,left");
		} else {
			callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));
			// ====================pangben modify 20110417 start 添加区域列
			callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
			callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
					"1,left;2,left;4,right;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left");
			// ====================pangben modify 20110417 stop
			callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11");
			callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
					"0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left");
		}
		callFunction("UI|Table_UP|removeRowAll");
		callFunction("UI|Table_DOWN|removeRowAll");
	}

	public void onQueryHistory() {
		getTable("Table_UP").removeRowAll();
		getTable("Table_DOWN").removeRowAll();
		if (orderType.equals("WD"))
			onQueryHistoryWD();
		else
			onQueryHistoryDD();
	}

	public void onQueryHistoryWD() {
		String dateSQL = "";
		if (getValue("from_ORDER_DATE") != null && getValue("to_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQL = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQL = " AND TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                                               AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String dateSQLOPD = "";
		if (getValue("from_ORDER_DATE") != null && getValue("to_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_RETN_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                           AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String exeDeptSQL = "";
		if (getValueString("EXEC_DEPT_CODE").length() != 0)
			exeDeptSQL = " AND EXEC_DEPT_CODE = '" + getValueString("EXEC_DEPT_CODE") + "'";
		String rxNoSQL = "";
		if (getValueString("RX_NO").length() != 0)
			rxNoSQL = " AND RX_NO = '" + getValueString("RX_NO") + "'";
		String mrNoSQL = "";
		if (getValueString("MR_NO").length() != 0)
			mrNoSQL = " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		String patNameSQL = "";
		if (getValueString("PAT_NAME").length() != 0)
			patNameSQL = " AND PAT_NAME LIKE '%" + getValueString("PAT_NAME") + "%'";
		String deptSQL = "";
		if (getValueString("DEPT_CODE").length() != 0)
			deptSQL = " AND DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		String drSQL = "";
		if (getValueString("DR_CODE").length() != 0)
			drSQL = " AND DR_CODE = '" + getValueString("DR_CODE") + "'";
		String agencyOrgCodeSQL = "";
		if (getValueString("AGENCY_ORG_CODE").length() != 0)
			agencyOrgCodeSQL = " AND AGENCY_ORG_CODE = '" + getValueString("AGENCY_ORG_CODE") + "'";
		String prescriptNoSQL = "";
		if (getValueString("from_PRESCRIPT_NO").length() != 0 && getValueString("to_PRESCRIPT_NO").length() != 0)
			prescriptNoSQL = " AND PRINT_NO BETWEEN TO_NUMBER('" + getValueString("from_PRESCRIPT_NO") + "')"
					+ "              AND     TO_NUMBER('" + getValueString("to_PRESCRIPT_NO") + "')";
		String countNoSQL = "";
		if (getValueString("COUNTER_NO").length() != 0)
			countNoSQL = " AND COUNTER_NO = '" + getValueString("COUNTER_NO") + "'";
		String checkSQL = "";
		if (getValueString("CHECKBUTTON").equals("Y"))
			checkSQL = " AND PHA_CHECK_DATE IS NOT NULL";
		String dosgeSQL = "";
		if (getValueString("DOSAGEBUTTON").equals("Y"))
			dosgeSQL = " AND PHA_DOSAGE_DATE IS NOT NULL";
		String dispenseSQL = "";
		if (getValueString("DISPENSEBUTTON").equals("Y"))
			dispenseSQL = " AND PHA_DISPENSE_DATE IS NOT NULL";
		String returnSQL = "";
		if (getValueString("RETURNBUTTON").equals("Y"))
			returnSQL = " AND PHA_RETN_DATE IS NOT NULL";
		// ==================pangben modify 20110517 start
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (A.REGION_CODE= '" + this.getValueString("REGION_CODE")
					+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE='' ) ";
		// =======================pangben modify 20110517 stop
		String SQL = " SELECT CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END CTL_FLG," +
		// ======pangben modify 20110418
				"    REGION_CHN_DESC, RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ "        PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " FROM  PHA_ORDER_HISTORY A,SYS_PATINFO B, SYS_REGION C"
				+ " WHERE A.REGION_CODE = C.REGION_CODE AND A.MR_NO = B.MR_NO" + // ======pangben modify 20110418
																					// =======================pangben
																					// modify 20110405 start
																					// 添加区域查询条件
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE IN ('W','C')" + dateSQL + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + prescriptNoSQL + countNoSQL
				+ " GROUP BY CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END,"
				+ "        REGION_CHN_DESC,  RX_NO,PAT_NAME,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,"
				+ "          PHA_RETN_DATE,PHA_DISPENSE_DATE,PHA_DOSAGE_DATE,"
				+ "          PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE" + " UNION"
				+ " SELECT CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END CTL_FLG,"
				+ "     REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE," + // ==pangben
																											// modify
																											// 20110418
				"        PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " FROM  OPD_ORDER A,SYS_PATINFO B ,SYS_REGION C"
				+ " WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // =====pangben modify 20110418
																				// =======================pangben modify
																				// 2011040 start
																				// 添加区域查询条件
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE IN ('W','C')" + dateSQLOPD + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL
				+ drSQL + agencyOrgCodeSQL + prescriptNoSQL + countNoSQL + checkSQL + dosgeSQL + dispenseSQL + returnSQL
				+ " GROUP BY REGION_CHN_DESC,CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END,"
				+ "          RX_NO,PAT_NAME,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,"
				+ "          PHA_RETN_DATE,PHA_DISPENSE_DATE,PHA_DOSAGE_DATE,"
				+ "          PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " ORDER BY REGION_CHN_DESC,RX_NO";
		// System.out.println("SQL:"+SQL);
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		clearNull(parm);
		// ==========pangben modify 20110418
		callFunction("UI|Table_UP|setParmValue", parm,
				"CTL_FLG;REGION_CHN_DESC;RX_NO;PAT_NAME;VARIETY;" + "SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;"
						+ "DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;" + "PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;"
						+ "COUNTER_NO;REGION_CODE");
	}

	public void onQueryHistoryDD() {
		String dateSQL = "";
		if (getValue("start_ORDER_DATE") != null && getValue("end_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQL = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQL = " AND TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                                               AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String dateSQLOPD = "";
		if (getValue("start_ORDER_DATE") != null && getValue("end_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_RETN_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                           AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String exeDeptSQL = "";
		if (getValueString("EXEC_DEPT_CODE").length() != 0)
			exeDeptSQL = " AND EXEC_DEPT_CODE = '" + getValueString("EXEC_DEPT_CODE") + "'";
		String rxNoSQL = "";
		if (getValueString("RX_NO").length() != 0)
			rxNoSQL = " AND RX_NO = '" + getValueString("RX_NO") + "'";
		String mrNoSQL = "";
		if (getValueString("MR_NO").length() != 0)
			mrNoSQL = " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		String patNameSQL = "";
		if (getValueString("PAT_NAME").length() != 0)
			patNameSQL = " AND PAT_NAME LIKE '%" + getValueString("PAT_NAME") + "%'";
		String deptSQL = "";
		if (getValueString("DEPT_CODE").length() != 0)
			deptSQL = " AND DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		String drSQL = "";
		if (getValueString("DR_CODE").length() != 0)
			drSQL = " AND DR_CODE = '" + getValueString("DR_CODE") + "'";
		String agencyOrgCodeSQL = "";
		if (getValueString("AGENCY_ORG_CODE").length() != 0)
			agencyOrgCodeSQL = " AND AGENCY_ORG_CODE = '" + getValueString("AGENCY_ORG_CODE") + "'";
		String decoctCodeSQL = "";
		if (getValueString("DECOCT_CODE").length() != 0)
			decoctCodeSQL = " AND DECOCT_CODE = '" + getValueString("DECOCT_CODE") + "'";
		String prescriptNoSQL = "";
		if (getValueString("from_PRESCRIPT_NO").length() != 0 && getValueString("to_PRESCRIPT_NO").length() != 0)
			prescriptNoSQL = " AND PRINT_NO BETWEEN TO_NUMBER('" + getValueString("from_PRESCRIPT_NO") + "')"
					+ "              AND     TO_NUMBER('" + getValueString("to_PRESCRIPT_NO") + "')";
		String countNoSQL = "";
		if (getValueString("COUNTER_NO").length() != 0)
			countNoSQL = " AND COUNTER_NO = '" + getValueString("COUNTER_NO") + "'";
		String checkSQL = "";
		if (getValueString("CHECKBUTTON").equals("Y"))
			checkSQL = " AND PHA_CHECK_DATE IS NOT NULL";
		String dosgeSQL = "";
		if (getValueString("DOSAGEBUTTON").equals("Y"))
			dosgeSQL = " AND PHA_DOSAGE_DATE IS NOT NULL";
		String dispenseSQL = "";
		if (getValueString("DISPENSEBUTTON").equals("Y"))
			dispenseSQL = " AND PHA_DISPENSE_DATE IS NOT NULL";
		String returnSQL = "";
		if (getValueString("RETURNBUTTON").equals("Y"))
			returnSQL = " AND PHA_RETN_DATE IS NOT NULL";
		// =======================pangben modify 20110517 start 添加区域查询条件
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (A.REGION_CODE= '" + this.getValueString("REGION_CODE")
					+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE='' )";
		String SQL = " SELECT DCTAGENT_FLG,REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ // =======================pangben modify 20110418
				"        DECOCT_CODE,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "        TAKE_DAYS,SUM(MEDI_QTY) TOT_GRAM,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE REMARK,A.REGION_CODE"
				+ " FROM  PHA_ORDER_HISTORY A,SYS_PATINFO B,SYS_REGION C" + // =======================pangben
				// modify
				// 20110418
				" WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // =======================pangben modify
																				// 20110418
																				// =======================pangben modify
																				// 20110405 start
																				// 添加区域查询条件
				region
				// =======================pangben modify 20110405 stop
				+ " AND PHA_TYPE = 'G'" + dateSQL + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + decoctCodeSQL + prescriptNoSQL + countNoSQL
				+ " GROUP BY REGION_CHN_DESC,DCTAGENT_FLG,RX_NO,PAT_NAME,DECOCT_CODE,PRINT_NO," + // ==========pangben
																									// modify 20110418
				"          A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "          PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "          TAKE_DAYS,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE,A.REGION_CODE" + " UNION"
				+ " SELECT DCTAGENT_FLG,REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ "        DECOCT_CODE,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "        TAKE_DAYS,SUM(MEDI_QTY) TOT_GRAM,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE REMARK,A.REGION_CODE"
				+ " FROM  OPD_ORDER A,SYS_PATINFO B,SYS_REGION C" + // ==========pangben modify 20110418
				" WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // ==========pangben modify 20110418
																				// =======================pangben modify
																				// 20110405 start
																				// 添加区域查询条件
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE = 'G'" + dateSQLOPD + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + decoctCodeSQL + prescriptNoSQL + countNoSQL + checkSQL + dosgeSQL + dispenseSQL
				+ returnSQL + " GROUP BY REGION_CHN_DESC,DCTAGENT_FLG,RX_NO,PAT_NAME,DECOCT_CODE,PRINT_NO,"
				+ "          A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "          PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "          TAKE_DAYS,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE,A.REGION_CODE"
				+ " ORDER BY REGION_CHN_DESC,RX_NO";
		// System.out.println("SQL?::::::::::::::::::::::::"+SQL);
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		clearNull(parm);
		// =======================pangben modify 20110418
		callFunction("UI|Table_UP|setParmValue", parm,
				"DCTAGENT_FLG;REGION_CHN_DESC;RX_NO;PAT_NAME;" + "VARIETY;SUM_FEE;DECOCT_CODE;"
						+ "PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;" + "PHA_RETN_DATE;PHA_DISPENSE_DATE;"
						+ "PHA_DOSAGE_DATE;PHA_CHECK_DATE;" + "EXEC_DEPT_CODE;COUNTER_NO;REGION_CODE");
	}

	private void clearNull(TParm parm) {
		String names[] = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < parm.getCount(names[i]); j++) {
				if (parm.getData(names[i], j) == null || parm.getValue(names[i], j).equalsIgnoreCase("null"))
					parm.setData(names[i], j, "");
			}
		}
	}

	public TParm getDownTableData() {
		String rxNo = getTable("Table_UP").getParmValue().getValue("RX_NO", getTable("Table_UP").getSelectedRow());
		// =======pangben modify 20110418 start 添加下边表查找条件，通过点击上边表的信息动态条件查找
		String regionCode = getTable("Table_UP").getParmValue().getValue("REGION_CODE",
				getTable("Table_UP").getSelectedRow());
		TParm parm = new TParm();
		String SQL = "";
		// =======================pangben modify 20110517 start 添加区域查询条件
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (REGION_CODE= '" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE ='') ";
		// =======================pangben modify 20110517 stop

		if (orderType.equals("WD")) {
			SQL = " SELECT ATC_FLG,LINKMAIN_FLG,''||LINK_NO LINK_NO,ORDER_DESC,"
					+ "        DISPENSE_QTY,DISPENSE_UNIT,OWN_PRICE,OWN_AMT,MEDI_QTY,"
					+ "        MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DR_NOTE REMARK,SEQ_NO"
					+ " FROM   PHA_ORDER_HISTORY" + " WHERE  RX_NO = '" + rxNo + "' " +
					// =======================pangben modify 20110405 start
					// 添加区域查询条件
					region +
					// =======================pangben modify 20110405 stop
					" UNION" + " SELECT ATC_FLG,LINKMAIN_FLG,LINK_NO,ORDER_DESC ||"
					+ "                                     CASE WHEN TRIM(GOODS_DESC) IS NOT NULL OR TRIM(GOODS_DESC) <>''"
					+ "                                     THEN '(' || GOODS_DESC || ')'"
					+ "                                     ELSE '' END || "
					+ "                                     CASE WHEN TRIM(SPECIFICATION) IS NOT NULL OR TRIM(SPECIFICATION) <>''"
					+ "                                     THEN '(' || SPECIFICATION || ')'"
					+ "                                     ELSE ''"
					+ "                                     END ORDER_DESC,"
					+ "        DISPENSE_QTY,DISPENSE_UNIT,OWN_PRICE,OWN_AMT,MEDI_QTY,"
					+ "        MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DR_NOTE REMARK,SEQ_NO" + " FROM   OPD_ORDER"
					+ " WHERE  RX_NO = '" + rxNo + "'" +
					// =======================pangben modify 20110405 start
					// 添加区域查询条件
					region +
					// =======================pangben modify 20110405 stop
					" ORDER BY SEQ_NO";
		} else {
			SQL = " SELECT ORDER_DESC ORDER_CODE," + "        DOSAGE_QTY TOT_QTY,DCTEXCEP_CODE,SEQ_NO"
					+ (type.equals("Examine") ? ",RELEASE_FLG" : "")// add by
																	// wangjc
																	// 增加备药标记显示
					+ " FROM   PHA_ORDER_HISTORY" + " WHERE  RX_NO = '" + rxNo + "' " +
					// =======================pangben modify 20110405 start
					// 添加区域查询条件
					region +
					// =======================pangben modify 20110405 stop
					" UNION" + " SELECT ORDER_DESC ||"
					+ "        CASE WHEN TRIM(GOODS_DESC) IS NOT NULL OR TRIM(GOODS_DESC) <>''"
					+ "        THEN '(' || GOODS_DESC || ')'" + "        ELSE '' END || "
					+ "        CASE WHEN TRIM(SPECIFICATION) IS NOT NULL OR TRIM(SPECIFICATION) <>''"
					+ "        THEN '(' || SPECIFICATION || ')'" + "        ELSE ''" + "        END ORDER_CODE,"
					+ "        DOSAGE_QTY TOT_QTY,DCTEXCEP_CODE,SEQ_NO" + (type.equals("Examine") ? ",RELEASE_FLG" : "")// add
																														// by
																														// wangjc
																														// 增加备药标记显示
					+ " FROM   OPD_ORDER" + " WHERE  RX_NO = '" + rxNo + "'" +
					// =======================pangben modify 20110405 start
					// 添加区域查询条件
					region +
					// =======================pangben modify 20110405 stop
					" ORDER BY SEQ_NO";
		}
		System.out.println("SQL?:11111:::::::::::::::::::::::" + SQL);
		parm = new TParm(TJDODBTool.getInstance().select(SQL));
		if (orderType.equals("DD")) {
			TParm parmFormat = new TParm();
			for (int i = 0; i < parm.getCount(); i = i + 4) {
				if (i < parm.getCount()) {
					parmFormat.addData("ORDER_CODE1", parm.getValue("ORDER_CODE", i));
					parmFormat.addData("TOT_QTY1", parm.getValue("TOT_QTY", i));
					parmFormat.addData("DCTEXCEP_CODE1", parm.getValue("DCTEXCEP_CODE", i));
					parmFormat.addData("RELEASE_FLG1", parm.getValue("RELEASE_FLG", i));// add by wangjc
																						// 增加备药标记显示
				} else {
					parmFormat.addData("ORDER_CODE1", "");
					parmFormat.addData("TOT_QTY1", "");
					parmFormat.addData("DCTEXCEP_CODE1", "");
					parmFormat.addData("RELEASE_FLG1", "");// add by wangjc
															// 增加备药标记显示
				}
				if (i + 1 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE2", parm.getValue("ORDER_CODE", i + 1));
					parmFormat.addData("TOT_QTY2", parm.getValue("TOT_QTY", i + 1));
					parmFormat.addData("DCTEXCEP_CODE2", parm.getValue("DCTEXCEP_CODE", i + 1));
					parmFormat.addData("RELEASE_FLG2", parm.getValue("RELEASE_FLG", i + 1));// add by
																							// wangjc
																							// 增加备药标记显示
				} else {
					parmFormat.addData("ORDER_CODE2", "");
					parmFormat.addData("TOT_QTY2", "");
					parmFormat.addData("DCTEXCEP_CODE2", "");
					parmFormat.addData("RELEASE_FLG2", "");// add by wangjc
															// 增加备药标记显示
				}
				if (i + 2 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE3", parm.getValue("ORDER_CODE", i + 2));
					parmFormat.addData("TOT_QTY3", parm.getValue("TOT_QTY", i + 2));
					parmFormat.addData("DCTEXCEP_CODE3", parm.getValue("DCTEXCEP_CODE", i + 2));
					parmFormat.addData("RELEASE_FLG3", parm.getValue("RELEASE_FLG", i + 2));// add by
																							// wangjc
																							// 增加备药标记显示
				} else {
					parmFormat.addData("ORDER_CODE3", "");
					parmFormat.addData("TOT_QTY3", "");
					parmFormat.addData("DCTEXCEP_CODE3", "");
					parmFormat.addData("RELEASE_FLG3", "");// add by wangjc
															// 增加备药标记显示
				}
				if (i + 3 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE4", parm.getValue("ORDER_CODE", i + 3));
					parmFormat.addData("TOT_QTY4", parm.getValue("TOT_QTY", i + 3));
					parmFormat.addData("DCTEXCEP_CODE4", parm.getValue("DCTEXCEP_CODE", i + 3));
					parmFormat.addData("RELEASE_FLG4", parm.getValue("RELEASE_FLG", i + 3));// add by
																							// wangjc
																							// 增加备药标记显示
				} else {
					parmFormat.addData("ORDER_CODE4", "");
					parmFormat.addData("TOT_QTY4", "");
					parmFormat.addData("DCTEXCEP_CODE4", "");
					parmFormat.addData("RELEASE_FLG4", "");// add by wangjc
															// 增加备药标记显示
				}
			}
			parm = parmFormat;
		}
		System.out.println(parm);
		return parm;
	}

	public void onUpTableClick() {
		if (getTable("Table_UP").getSelectedRow() < 0)
			return;
		TParm orders = getDownTableData();
		TParm order = getTable("Table_UP").getParmValue();
		if (orderType.equals("WD")) {
			setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO;REGION_CODE", // =====pangben
					// modify
					// 20110418
					order, getTable("Table_UP").getSelectedRow());
			this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add
																				// by
																				// huangtt
																				// 20150120
																				// PRESRT_NO
			this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																						// huangtt
																						// 20150120
																						// PRESRT_NO
			callFunction("UI|Table_DOWN|setParmValue", orders,
					"ATC_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;REMARK");
		} else {
			setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO;REGION_CODE", // =====pangben
					// modify
					// 20110418
					order, getTable("Table_UP").getSelectedRow());
			this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add
																				// by
																				// huangtt
																				// 20150120
																				// PRESRT_NO
			this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																						// huangtt
																						// 20150120
																						// PRESRT_NO
			setValueForParm("TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE", order,
					getTable("Table_UP").getSelectedRow());
			callFunction("UI|Table_DOWN|setParmValue", orders,
					// "ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4");
					"ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;RELEASE_FLG1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;RELEASE_FLG2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;RELEASE_FLG3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4;RELEASE_FLG4");
		}
	}

	private void setTotTakeDays() {
		if (getTable("Table_UP").getSelectedRow() < 0)
			return;
		TParm order = getTable("Table_UP").getParmValue();
		TParm orderRow = order.getRow(getTable("Table_UP").getSelectedRow());
		TParm takeDaysTPatm = new TParm(TJDODBTool.getInstance()
				.select("SELECT TAKE_DAYS FROM OPD_ORDER WHERE RX_NO = '" + orderRow.getValue("RX_NO") + "'"));
		setValue("TAKE_DAYS", takeDaysTPatm.getValue("TAKE_DAYS", 0));
	}

	/**
	 * 合理用药自动检测
	 * 
	 * @return boolean
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!PassTool.getInstance().init()) {
			return true;
		}
		String opdOrdercat = "";
		if (orderType.equals("WD")) {
			opdOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		} else {
			opdOrdercat = "A.ORDER_CAT1_CODE='PHA_G'";
			;
		}
		PassTool.getInstance().setPatientInfo(nowOrdList.getOrder(0).getCaseNo());
		PassTool.getInstance().setAllergenInfo(nowOrdList.getMrNo());
		PassTool.getInstance().setMedCond(nowOrdList.getOrder(0).getCaseNo());
		TParm parm = PassTool.getInstance().setRecipeInfoAuto(nowOrdList.getOrder(0).getCaseNo(), opdOrdercat);
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "有药品使用不合理,是否存档?", "信息", JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * 警告级别
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("RX_NO"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * 查询药品信息
	 */
	public void queryDrug() {
		if (!passIsReady) {
			messageBox("合理用药未启用");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		if (orderType.equals("WD")) {
			int row = getTable("Table_DOWN").getSelectedRow();
			if (row < 0) {
				return;
			}
			String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
			if (value == null || value.length() == 0) {
				return;
			}
			int conmmand = Integer.parseInt(value);
			if (conmmand != 6) {
				PassTool.getInstance().setQueryDrug(nowOrdList.getOrder(row).getOrderCode(), conmmand);

			} else {
				PassTool.getInstance().setWarnDrug1(nowOrdList.getRxNo(), "" + nowOrdList.getOrder(row).getSeqNo());
			}
		} else {
			int column = getTable("Table_DOWN").getSelectedColumn();
			int number = 0;
			if (column < 0) {
				return;
			}
			TParm parm = getTable("Table_DOWN").getParmValue();
			String ordercode;
			switch (column / 3) {
			case 0:
				ordercode = parm.getValue("ORDER_CODE1", 0);
				if (!ordercode.equals("")) {
					number = 0;
				}
				break;
			case 1:
				ordercode = parm.getValue("ORDER_CODE2", 0);
				if (!ordercode.equals("")) {
					number = 1;
				}
				break;
			case 2:
				ordercode = parm.getValue("ORDER_CODE3", 0);
				if (!ordercode.equals("")) {
					number = 2;
				}
				break;
			case 3:
				ordercode = parm.getValue("ORDER_CODE4", 0);
				if (!ordercode.equals("")) {
					number = 3;
				}
				break;
			default:
				break;
			}
			String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
			if (value == null || value.length() == 0) {
				return;
			}
			int conmmand = Integer.parseInt(value);
			if (conmmand != 6) {
				PassTool.getInstance().setQueryDrug(nowOrdList.getOrder(number).getOrderCode(), conmmand);
			} else {
				PassTool.getInstance().setWarnDrug1(nowOrdList.getRxNo(), "" + nowOrdList.getOrder(number).getSeqNo());
			}

		}
	}

	/**
	 * 手动检测合理用药
	 */
	public void checkDrugHand() {
		if (!passIsReady) {
			messageBox("合理用药未启用");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("合理用药初始化失败，此功能不能使用！");
			return;
		}
		String opdOrdercat = "";
		if (orderType.equals("WD")) {
			opdOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		} else {
			opdOrdercat = "A.ORDER_CAT1_CODE='PHA_G'";
		}
		if (nowOrdList == null) {
			return;
		}
		PassTool.getInstance().setPatientInfo(nowOrdList.getOrder(0).getCaseNo());
		PassTool.getInstance().setAllergenInfo(nowOrdList.getMrNo());
		PassTool.getInstance().setMedCond(nowOrdList.getOrder(0).getCaseNo());
		TParm parm = PassTool.getInstance().setRecipeInfoHand(nowOrdList.getOrder(0).getCaseNo(), opdOrdercat);
		isWarn(parm);
	}

	public void onPasterSwab() {
		onPasterSwab("");
	}

	/**
	 * 打印药签-物联网
	 * 
	 * @author yuhaibao
	 */
	public void onPasterSwab(String save) {
		if (((TTable) this.getComponent("Table_UP")).getSelectedRow() < 0) {
			messageBox("未选中处方!");
			return;
		}
		// 取行数据，保存到一个P里面

		// 如果是西药
		if ("WD".equals(orderType))
			// onPrintSwab(nowOrdList.getRxNo(),
			// save,(nowOrdList.getPresrtNo()==0?"":nowOrdList.getPresrtNo()+""));
			onPrintSwab(nowOrdList.getRxNo(), save, nowOrdList.getRxPresertNo());
		// else
		// onPrintTCM();
	}

	/**
	 * 打印西药药签-物联网
	 * 
	 * @author yuhaibao
	 * @param rxNo
	 */
	public void onPrintSwab(String rxNo, String save, String rxPresrtNo) {
		/*
		 * r TParm parm = onQuery(rxNo);
		 * 
		 * String orderCat1 = ""; if (orderType.equals("WD")) { orderCat1 =
		 * "'PHA_W','PHA_C'"; }
		 */
		if (caseNo == null) {
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);
			caseNo = orders.getValue("CASE_NO", 0);
		}
		// 查询数据，进行校验， 如果有值 才能打印，没值 弹出框
		TParm tparmPHA = getPHAInfusion(rxNo, rxPresrtNo);

		if (tparmPHA.getCount() <= 0) {
			messageBox("药签无打印数据");
			return;
		}
		// 校验，如果tparmPHA.getCount() <= 0并且 界面上是未完成的状态
		// 把之前行取值的P数据，拿过来，按照以下需要攒值数据 一一对应，
		String linkNo = tparmPHA.getValue("LINK_NO", 0);
		String mrNo = tparmPHA.getValue("MR_NO", 0);
		String dosageName = tparmPHA.getValue("DOSAGE_NAME", 0);
		String drName = tparmPHA.getValue("DR_NAME", 0);
		String sexCode = tparmPHA.getValue("CHN_DESC", 0);
		String patName = getValueString("PAT_NAME");
		// String age = tparmPHA.getValue("AGE",0);
		// Timestamp day = SystemTool.getInstance().getDate();
		// String age = DateUtil.showAge(pat.getBirthday(), day);//==liling
		// 20140703 modify
		String birthdate = pat.getBirthday().toString();
		if (null != birthdate && birthdate.length() >= 10) {
			birthdate = birthdate.substring(0, 4) + "年" + birthdate.substring(5, 7) + "月" + birthdate.substring(8, 10)
					+ "日";
		}
		System.out.println("shengri=============" + birthdate);
		String deptName = tparmPHA.getValue("DEPT_CHN_DESC", 0);
		// fux modify 20151008

		boolean flg = false;
		for (int i = 0; i < tparmPHA.getCount(); i++) {
			String doseType = tparmPHA.getValue("DOSE_TYPE", i);
			if ("O".equals(doseType) || "E".equals(doseType)) {
				flg = true;
				break;
			}

		}
		if (flg) {

			for (int i = 0; i < tparmPHA.getCount(); i++) {
				TParm date = new TParm();

				String doseType = tparmPHA.getValue("DOSE_TYPE", i);
				String doseTypeChn = "";
				String doseChn = "门诊";
				// ROUTE_CODE
				doseType = tparmPHA.getValue("ROUTE_CODE", i);
				// if("O".equals(doseType)){
				// doseTypeChn = "口服";
				//
				// }else{
				// doseTypeChn = "外用";
				// }
				doseTypeChn = doseType;
				// TParm newTParm = new TParm();
				// fux modify 20151009
				// 料位 GOODS_DESC
				// 规格
				// 数量 （配药量）
				// 用法 口服（外用） 药

				// date.addData("GOODS_DESC", tparmPHA.getData("GOODS_DESC",
				// i)+" "+tparmPHA.getData("SPECIFICATION",i));
				// date.addData("QTY", "");
				// date.addData("GOODS_DESC",
				// "用法: "+tparmPHA.getData("FREQ_CODE",
				// i)+" "+"每次"+tparmPHA.getData("ROUTE_CODE",
				// i)+" "+tparmPHA.getData("QTY", i));
				// date.addData("QTY","");
				// newTParm.setCount(2);
				// newTParm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
				// newTParm.addData("SYSTEM", "COLUMNS", "QTY");
				// date.setData("TABLE", newTParm.getData());

				// 表头数据 MR_NO
				date.setData("LINK_NO", "TEXT", linkNo);
				date.setData("DEPT_TYPE", "TEXT", "【" + doseTypeChn + "】");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);
				date.setData("BED_CODE", "TEXT", "");
				date.setData("DOSECHN", "TEXT", doseChn);
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT", sexCode);
				// date.setData("AGE", "TEXT",age);
				// fux modify 20151008
				date.setData("REGION", "TEXT", Operator.getHospitalCHNShortName());
				// yanmm 20170824
				// date.setData("REMARK","TEXT","用药前请阅读药品说明书");
				date.setData("BIRTHDATE", "TEXT", birthdate);
				date.setData("ADM_DATE", "TEXT", tparmPHA.getValue("ADM_DATE", i).substring(0, 10));
				date.setData("SEND_CODE", "TEXT", dosageName);
				date.setData("DOCTOR_CODE", "TEXT", drName);
				date.setData("MATERIAL_LOC_CODE", "TEXT", tparmPHA.getValue("MATERIAL_LOC_CODE", i).toString());
				date.setData("GOODS_DESC", "TEXT", tparmPHA.getData("GOODS_DESC", i).toString());
				date.setData("SPECIFICATION", "TEXT", "规格: " + tparmPHA.getData("SPECIFICATION", i).toString());
				date.setData("DRUG_NOTES_PATIENT", "TEXT",
						"备注: " + tparmPHA.getData("DRUG_NOTES_PATIENT", i).toString() + "");
				// System.out.println("数量:"+tparmPHA.getData("MOD_DOSAGE",i).toString());
				// System.out.println("用法:"+tparmPHA.getData("MOD_MEDI",i).toString());
				if (tparmPHA.getData("MOD_DOSAGE", i).toString().equals("0")) {

					date.setData("DISPENSE_QTY", "TEXT", "数量: " + tparmPHA.getData("DOSAGE_QTY", i).toString());
				} else {
					date.setData("DISPENSE_QTY", "TEXT", "数量: " + tparmPHA.getData("DOSAGE1", i).toString() + "/"
							+ tparmPHA.getData("DOSAGE2", i).toString() + tparmPHA.getData("UNIT_STOCK", i).toString());
				}

				if (tparmPHA.getData("MOD_MEDI", i).toString().equals("0")) {
					date.setData("FREQ_CODE", "TEXT",
							"用法: " + tparmPHA.getData("FREQ_CODE", i).toString() + "  " + "" + "每次 "
									+ tparmPHA.getData("QTY", i).toString() + " "
									+ tparmPHA.getData("TAKE_DAYS", i).toString() + "天");
				} else {
					date.setData("FREQ_CODE", "TEXT",
							"用法: " + tparmPHA.getData("FREQ_CODE", i).toString() + "  " + "" + "每次 "
									+ tparmPHA.getData("QTY", i).toString() + " "
									+ tparmPHA.getData("TAKE_DAYS", i).toString() + "天");

				}

				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPersralnew.jhw", date, true);
			}
		} else {
			this.messageBox("非口服/外用药不能打印药签!");
		}

	}

	/**
	 * 
	 */
	public void inflution(String rxNo) {
		String sql = "SELECT INFLUTION_RATE from opd_oder where  MIAN_FLG ='Y' and RX_NO='" + rxNo + "' and  ";

	}

	/**
	 * 输液签
	 */
	public void onPrintInfusion() {
		// messageBox("tttt");
		// System.out.println("======================== print");
		String rxNo = getValueString("RX_NO");
		String rxPresrtNo = this.getValueString("RX_PRESRT_NO"); // add by
																	// huangtt
																	// 20150121
		TParm parm = getPHAInfusion(rxNo, rxPresrtNo);
		String linkNo = parm.getValue("LINK_NO", 0);
		String mrNo = parm.getValue("MR_NO", 0);
		String dosageName = parm.getValue("DOSAGE_NAME", 0);
		String drName = parm.getValue("DR_NAME", 0);
		String sexCode = parm.getValue("CHN_DESC", 0);
		String patName = getValueString("PAT_NAME");
		// String age = parm.getValue("AGE",0);
		Timestamp day = SystemTool.getInstance().getDate();
		String age = DateUtil.showAge(pat.getBirthday(), day);// ==liling
																// 20140703
																// modify
		String deptName = parm.getValue("DEPT_CHN_DESC", 0);
		// 出生日期
		String birthdate = pat.getBirthday().toString();
		if (null != birthdate && birthdate.length() >= 10) {
			birthdate = birthdate.substring(0, 4) + "年" + birthdate.substring(5, 7) + "月" + birthdate.substring(8, 10)
					+ "日";
		}

		boolean isInfusion = false;
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < parm.getCount(); i++) {
			linkNo = parm.getValue("LINK_NO", i);
			set.add(linkNo);
		}

		for (int i = 0; i < parm.getCount(); i++) {
			String doseType = parm.getValue("DOSE_TYPE", 0);
			if ("F".equals(doseType) || "I".equals(doseType)) {
				isInfusion = true;
				break;
			}
		}
		if (isInfusion) {
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				linkNo = (String) it.next();
				TParm tparmPHA = new TParm();
				TParm date = new TParm();
				int count = 0;
				// DecimalFormat df = new DecimalFormat("#####0.000");
				double rate = 0;
				boolean flg = true;
				for (int i = 0; i < parm.getCount(); i++) {
					String linkNoNew = parm.getValue("LINK_NO", i);
					if ("".equals(linkNoNew) && flg) {// 非连组
						tparmPHA.addData("GOODS_DESC",
								parm.getData("GOODS_DESC", i) + " " + parm.getData("SPECIFICATION", i));
						// tparmPHA.addData("SPECIFICATION",
						// parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("QTY", i));
						// tparmPHA.addData("INFLUTION_RATE",
						// parm.getDouble("INFLUTION_RATE", i));
						// tparmPHA.addData("ROUTE_CODE",
						// parm.getData("ROUTE_CODE", i));
						// tparmPHA.addData("FREQ_CODE",
						// parm.getData("FREQ_CODE", i));
						rate = parm.getDouble("INFLUTION_RATE", i);
						count++;
					} else if (linkNo.equals(linkNoNew)) {// 连组
						tparmPHA.addData("GOODS_DESC",
								parm.getData("GOODS_DESC", i) + " " + parm.getData("SPECIFICATION", i));
						// tparmPHA.addData("SPECIFICATION",
						// parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("QTY", i));
						// if(parm.getBoolean("LINKMAIN_FLG", i)){
						// tparmPHA.addData("INFLUTION_RATE",
						// df.format(parm.getDouble("INFLUTION_RATE", i)));
						rate = parm.getDouble("INFLUTION_RATE", i);
						// }
						// tparmPHA.addData("ROUTE_CODE",
						// parm.getData("ROUTE_CODE", i));
						// tparmPHA.addData("FREQ_CODE",
						// parm.getData("FREQ_CODE", i));
						flg = false;
						count++;
					}
				}
				tparmPHA.addData("GOODS_DESC", "");
				tparmPHA.addData("QTY", "");
				// tparmPHA.addData("INFLUTION_RATE", "");

				tparmPHA.addData("GOODS_DESC", "输液速率：" + rate + "ml/h");
				tparmPHA.addData("QTY", "");
				// tparmPHA.addData("INFLUTION_RATE", "");
				count += 2;
				tparmPHA.setCount(count);
				tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
				tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");

				date.setData("TABLE", tparmPHA.getData());
				// 表头数据 MR_NO
				date.setData("LINK_NO", "TEXT", linkNo);
				date.setData("DEPT_TYPE", "TEXT", "门诊【注射】药");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);
				date.setData("BED_CODE", "TEXT", "");
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT", sexCode);
				date.setData("AGE", "TEXT", age);
				date.setData("ROUTE_CODE", "TEXT", parm.getData("ROUTE_CODE", 0));
				date.setData("FREQ_CODE", "TEXT", parm.getData("FREQ_CODE", 0));
				date.setData("BAR_CODE", "TEXT", getBarCode(rxNo, linkNo));

				date.setData("SEND_CODE", "TEXT", dosageName);
				date.setData("DOCTOR_CODE", "TEXT", drName);
				// add by wangb 2017/08/11 输液瓶签增加出生日期显示
				date.setData("BIRTHDATE", "TEXT", birthdate);

				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date, true);
			}
		} else {
			this.messageBox("非输液药品不能打印输液标签!");
		}

	}

	/**
	 * 获取条码
	 * 
	 * @param orderNo
	 * @param linkNo
	 * @return
	 */
	private String getBarCode(String orderNo, String linkNo) {
		if ("".equals(linkNo)) {
			return "";
		}
		linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
		return orderNo + linkNo;
	}

	// =============== chenxi modify 20121009添加打印病例功能
	/**
	 * 打印病历
	 * 
	 * @return Object
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
			return;
		}
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		TParm tableParm = table.getParmValue();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
		parm.setData("MR", "TEXT", "病案号：" + tableParm.getValue("MR_NO", selRow));
		// if (isEng) {
		// parm
		// .setData("HOSP_NAME", "TEXT", Operator
		// .getHospitalENGFullName());
		// } else {
		parm.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		// }
		parm.setData("DR_NAME", "TEXT", "医师签字:" + OpdRxSheetTool.getInstance().GetRealRegDr(caseNo));
		parm.setData("REALDEPT_CODE", tableParm.getValue("DEPT_CODE", selRow));

		// 20171114 add by wangjc
		// 家族史
		if (OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)).equals("")) {// 判断家族史是否为空
			parm.setData("familyHistory", "");
		} else {
			parm.setData("familyHistory",
					OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)));
		}
		// 既往史
		if (OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)).equals("")) {// 判断既往史是否为空
			parm.setData("pastHistory", "");
		} else {
			parm.setData("pastHistory",
					OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)));
		}
		// 手术输血史
		String opBloodHistory = OpdRxSheetTool.getInstance().getOpBloodHistory(tableParm.getValue("MR_NO", selRow));
		if (StringUtils.isEmpty(opBloodHistory)) {
			parm.setData("opBloodHistory", "");
		} else {
			parm.setData("opBloodHistory", opBloodHistory);
		}
		// 用药情况
		String medication = OpdRxSheetTool.getInstance().getMedication(caseNo);
		if (StringUtils.isEmpty(medication)) {
			parm.setData("medication", "");
		} else {
			parm.setData("medication", medication);
		}

		Object obj = new Object();
		if ("O".equals(regInfo.getValue("ADM_TYPE", 0))) {
			// obj = this.openPrintDialog(
			// "%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010.jhw", parm,
			// false);
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHACaseSheet.jhw", parm, false);
			// 加入EMR保存 beign
			// this.saveEMR(obj, "门诊病历记录", "EMR020001", "EMR02000106");
			// 加入EMR保存 end
		} else if ("E".equals(regInfo.getValue("ADM_TYPE", 0))) {
			// obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG.jhw",
			// parm, false);
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHAEMG.jhw", parm, false);

		}

	}

	/**
	 * 药签-物联网
	 * 
	 * @author yuhaibao
	 * @param rxNo
	 * @param orderCat1
	 * @return
	 */
	public TParm getPHAParmSwab(String rxNo, String caseNo, String save) {
		boolean nowFlag = (Boolean) this.callFunction("UI|PACKAGEDRUG|isSelected");
		String atc_flg = "";
		if (nowFlag) {
			atc_flg = "N";
		} else {
			atc_flg = "Y";
		}
		if (save.equals("SAVE")) {
			atc_flg = "N";
		}
		String SQL = " SELECT A.ORDER_DESC  ||'\r\n'||  " + " 			B.FREQ_CHN_DESC  " + " 	    || ' ' || '每次' ||  "
				+ " CASE" + " WHEN SUBSTR (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99'),"
				+ " LENGTH (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99'))," + " 1" + " ) = '.'"
				+ " THEN SUBSTR (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')," + " 1,"
				+ " LENGTH (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')) - 1" + " )"
				+ " ELSE TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')" + " END" + " || D.UNIT_CHN_DESC "
				+ "        AS SWABDATA " + " FROM OPD_ORDER A,SYS_PHAFREQ B,SYS_UNIT C ,SYS_UNIT D ,PHA_TRANSUNIT E "
				+ " WHERE A.CASE_NO='" + caseNo + "'  AND A.RX_NO ='" + rxNo + "' "
				+ " AND A.CAT1_TYPE='PHA'  AND A.FREQ_CODE=B.FREQ_CODE(+) AND A.MEDI_UNIT=C.UNIT_CODE(+)  "
				+ " AND A.ORDER_CODE=E.ORDER_CODE AND E.DOSAGE_UNIT=D.UNIT_CODE ";
		// System.out.println("---------药签-物联网:---sql: "+SQL);
		return new TParm(TJDODBTool.getInstance().select(SQL));
	}

	/**
	 * 查询病患信息是否从HIS同步过来
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param rxNo
	 *            处方号
	 * @param mrNo
	 *            病案号
	 * @return
	 */
	private boolean isHaveInfoFromHis(String startDate, String endDate, String rxNo, String mrNo) {
		// 标志位：false 没有数据同步过来，需要调用接口，TRUE查询到数据直接
		boolean isHave = false;
		// System.out.println("3928----------------sql:
		// "+SPCSQL.getCountOPdOrder(startDate,
		// endDate, rxNo, mrNo));
		TParm parm = new TParm(
				TJDODBTool.getInstance().select(SPCSQL.getCountOPdOrder(startDate, endDate, rxNo, mrNo)));
		if (null != parm || parm.getCount() > 0) {
			if (parm.getInt("COUNT", 0) > 0) {
				isHave = true;
			}
		}
		return isHave;
	}

	/**
	 * 同步HIS发药信息
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param rxNo
	 *            处方号
	 * @param mrNo
	 *            病案号
	 * @return
	 */
	private void synOPdOrder(String startDate, String endDate, String rxNo, String mrNo) {
		startDate = startDate.length() > 19 ? startDate.substring(0, 19) : startDate;
		endDate = endDate.length() > 10 ? endDate.substring(0, 10) + " 23:59:59" : endDate;
		// 查询病患信息是否从HIS同步过来 false 没有数据同步过来，需要调用接口，TRUE查询到数据直接
		boolean flag = isHaveInfoFromHis(startDate, endDate, rxNo, mrNo);
		if (flag) {
			return;
		} else {
			// 调用后台的action
			TParm rxParm = new TParm();
			rxParm.setData("RX_NO", rxNo);
			rxParm.setData("START_DATE", startDate);
			rxParm.setData("END_DATE", endDate);
			TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction", "onSaveSpcRequest", rxParm);
			// SpcOpdOrderDtos dtos =
			// PHADosageWsImplService_Client.onSaveSpcRequest(rxNo,
			// startDate,endDate);
			// System.out.println("size:=" + dtos.getSpcOpdOrderDtos().size());
			/*
			 * System.out.println("getRxNo:=" + dtos.getSpcOpdOrderDtos().get(0).getRxNo());
			 * System.out.println("tSpcOpdOrderDtos:=" +
			 * dtos.getSpcOpdOrderDtos().get(0).getPatName());
			 * System.out.println("st__retur:=" +
			 * dtos.getSpcOpdOrderDtos().get(0).getSeqNo());
			 * System.out.println("onSaveSpcRequest.result=" + dtos);
			 */
			// SpcDaoImpl.getInstance().onSavePhaOrder(dtos);
		}
	}

	/**
	 * 更新电子标签库存
	 */
	private void updateStockQtyOfEleTag() {
		if (null != nowOrdList && nowOrdList.size() > 0) {
			for (int i = 0; i < nowOrdList.size(); i++) {
				Order nowOrder = nowOrdList.getOrder(i);
				String orderCode = nowOrder.getOrderCode();
				String orgCode = nowOrder.getExecDeptCode();
				String atcFlg = nowOrder.getAtcFlg();
				// System.out.println("-----更新电子标签库存-----orgCode:"+orgCode+",orderCode:"+orderCode+",atcFlg;"+atcFlg);
				if (true) {// if(StringUtils.equals("N", atcFlg)){//当非包药时
					// 才更新药品的库存,
					SPCTool.getInstance().sendEleTag(orgCode, orderCode, 1);
				}
			}
		}
	}

	// ======================================== chenxi 20130515 送盒装包药机 呼叫201 方法
	/**
	 * chenxi 20130515 送盒装包药机 呼叫201 方法
	 */
	public void onDispenseBoxMachine() {
		TParm result = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0) {
			this.messageBox("选择处方");
			return;
		}
		// ============================== EXEC_DEPT_CODE 盒装包药机开关
		if (onBoxFlg(table.getItemString(selectRow, "EXEC_DEPT_CODE")) && "Y".equals(Operator.getSpcFlg())) {

			// ============================= 盒装包药机开关
			TParm parmMain = data.getRow(selectRow); // 选中行的处方主表
			parmMain.setData("DR_NAME", this.getDrDesc(parmMain.getValue("DR_CODE")));
			parmMain.setData("DEPT_DESC", this.getDeptDesc(parmMain.getValue("DEPT_CODE")));
			nowOrdList = pha.getCertainOrdListByRow(selectRow);
			TParm parmDetail = nowOrdList.getParm(nowOrdList.PRIMARY); // 处方主表对应的处方明细
			for (int i = 0; i < parmDetail.getCount(); i++) {
				TParm desc = getOrderData(parmDetail.getValue("ORDER_CODE", i)); // 取得药品数据
				parmDetail.setData("ORDER_DESC", i,
						desc.getData("ORDER_DESC", 0) + "" + desc.getData("SPECIFICATION", 0));
				parmDetail.setData("MEDI_UNIT", i, this.getUnitDesc(parmDetail.getValue("MEDI_UNIT", i)));
				parmDetail.setData("DISPENSE_UNIT", i, this.getUnitDesc(parmDetail.getValue("DISPENSE_UNIT", i)));
				parmDetail.setData("ROUTE_CODE", i, this.getRouteDesc(parmDetail.getValue("ROUTE_CODE", i)));
				parmDetail.addData("FREQ_DESC",
						this.getFreqData(parmDetail.getValue("FREQ_CODE", i)).getValue("FREQ_CHN_DESC", 0));
				parmDetail.addData("FIRM_ID", this.getManDesc(desc.getValue("MAN_CODE", 0)));
				parmDetail.setData("CTZ1_CODE", i, this.getCtzDesc(parmDetail.getValue("CTZ1_CODE", i)));
				String boxFlg = nowOrdList.getOrder(i).getGiveboxFlg();
				parmDetail.addData("FLG", boxFlg);
				String timeCode = TXNewATCTool.getTimeLine(parmDetail.getValue("FREQ_CODE", i)); // 服用时间编码 37
				parmDetail.addData("FREQ_DESC_DETAIL_CODE", timeCode);
				String timeDetail = TXNewATCTool.getTimeDetail(parmDetail.getValue("FREQ_CODE", i));// 服用时间详细 38
				parmDetail.addData("FREQ_DESC_DETAIL", timeDetail);
				parmDetail.addData("QTY_BUONE", getMediQty(parmDetail.getValue("ORDER_CODE", i)));// 一片的剂量
																									// (数值)
				parmDetail.addData("BAR_CODE", getBarCode(parmDetail.getValue("ORDER_CODE", i)));// 得到物联网编码
			}
			// 设置表头参数
			TParm parmTitle = new TParm();
			parmTitle.setData("OPT_USER", Operator.getName());
			parmTitle.setData("OPT_CODE", Operator.getID());
			parmTitle.setData("OPT_TERM", Operator.getIP());
			parmTitle.setData("OPWINID", table.getItemString(selectRow, "COUNTER_NO")); // 窗口号
			parmTitle.setData("WAY", "201"); // 201 方法
			result.setData("TITLE", parmTitle);
			result.setData("DETAIL", parmDetail);
			result.setData("MAIN", parmMain);
			String inxml = XmlUtils.onCreateXmlDispense(result).toString();
			// System.out.println("xml就是我呼叫201方法传的参数========"+inxml);
			ConsisServiceSoap_ConsisServiceSoap_Client client = new ConsisServiceSoap_ConsisServiceSoap_Client();
			String outxml = client.onTransConsisData(inxml); // 返回的xml
			// System.out.println("out====返回的xml】==201===="+outxml);
			if (outxml.equals("err")) {
				this.messageBox("webservices 链接错误");
				return;
			}
			TParm returnParm = XmlUtils.createXmltoParm(outxml); // 将返回的xml转换为parm，0代表失败，1代表成功
			if (returnParm.getErrCode() == 1) {
				String sql = "UPDATE OPD_ORDER SET COUNTER_NO = '" + returnParm.getValue("MESSAGE") + "' "
						+ "     WHERE RX_NO  ='" + table.getItemString(selectRow, "RX_NO") + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					this.messageBox("分配窗口号失败");
					return;
				}
				this.messageBox("送包药机成功");
			} else {
				this.messageBox("送包药机失败");
				return;
			}
		}
	}

	/**
	 * 通知发药机发药，呼叫202方法(发药完成，呼叫203方法)
	 */
	public boolean onSendBoxMachine() {
		TParm result = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selectRow = table.getSelectedRow();
		if (onBoxFlg(table.getItemString(selectRow, "EXEC_DEPT_CODE")) && "Y".equals(Operator.getSpcFlg())) {
			// 设置表头参数
			TParm parmTitle = new TParm();
			parmTitle.setData("OPT_USER", Operator.getName());
			parmTitle.setData("OPT_CODE", Operator.getID());
			parmTitle.setData("OPT_TERM", Operator.getIP());
			parmTitle.setData("OPWINID", table.getItemString(selectRow, "COUNTER_NO")); // 窗口号
			parmTitle.setData("WAY", WAY); // 202,或是203 方法，暂时这样，等填逻辑的时候在传个变量
			result.setData("TITLE", parmTitle);
			result.setData("MAIN", data.getRow(selectRow));
			String inxml = XmlUtils.onCreateXmlSend(result).toString();
			// System.out.println("xml就是我呼叫202.203方法传的参数===11222====="+inxml);
			ConsisServiceSoap_ConsisServiceSoap_Client client = new ConsisServiceSoap_ConsisServiceSoap_Client();
			String outxml = client.onTransConsisData(inxml); // 返回的xml
			// System.out.println("out====返回的xml】=="+WAY+"===="+outxml);
			if (outxml.equals("err")) {
				this.messageBox("webservices 链接错误");
				return false;
			}
			TParm returnParm = XmlUtils.createXmltoParm(outxml); // 将返回的xml转换为parm，0代表失败，1代表成功
			if (returnParm.getErrCode() == 1) {
				if (WAY.equals("202")) {
					// this.onPasterSwab("SAVE") ;
					this.messageBox("执行成功");
					return true;
				}

				return true;
			} else {
				this.messageBox("送包药机失败,确认是否发药");
				return false;
			}
		} else if ("Y".equals(Operator.getSpcFlg()))
			this.messageBox("该药房不允许送盒装发药机"); // shibl modify 2013/12/18 注释
		return true;
	}

	// 盒装包药机开关
	public boolean onBoxFlg(String deptCode) {
		String sql = "SELECT BOX_FLG FROM IND_ORG WHERE ORG_CODE = '" + deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return false;
		}
		if (result.getValue("BOX_FLG", 0).equals("N"))
			return false;
		return true;
	}

	/**
	 * 审核药品界面跑马灯双击操作
	 * 
	 * @param rxNo
	 *            ===pangben 2013-5-15
	 */
	public void openInwCheckWindow(String rxNo) {
		// this.setValue("RX_NO", rxNo);
		onQuery();
		TTable table = (TTable) this.getComponent("Table_UP");// 获得上面表格数据，如果没有数据直接移除跑马灯当前处方签
		if (table.getParmValue().getCount() <= 0) {
			this.messageBox("没有获得数据");
			// getLedMedRemoveRxNo();//modify by wangjc 20180103爱育华反应系统慢，注掉跑马灯功能
		}

	}

	/**
	 * 跑马灯显示
	 */
	public void openLEDMEDUI() {
		Component com = (Component) getComponent();
		TParm parm = new TParm();
		// "PHAMAIN"
		//
		// System.out.println("----Operator.getDept()-----"+Operator.getDept());
		// 按操作者的，登录部门接消息
		parm.setData("PHA_CODE", Operator.getDept());
		//
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while ((com != null) && (!(com instanceof Frame)))
			com = com.getParent();
		this.ledMedUi = new LEDMEDUI((Frame) com, this, parm);
		this.ledMedUi.openWindow();
	}

	public boolean onClosing() {
		if ((this.orderType.equals("WD")) && (this.type.equals("Examine"))) {// &&"Y".equals(Operator.getSpcFlg()))
																				// {
																				// this.ledMedUi.close();//modify
																				// by
																				// wangjc
																				// 20180103爱育华反应系统慢，注掉跑马灯功能
		}
		return true;
	}

	public void onSelStationListenerLed(TParm parm) {
		this.ledParm = parm;
	}

	public void onSel() {
		this.ledParm.runListener("onListenerLed", new Object[] { "PHAMAIN" });
	}

	/**
	 * ==pangben 2013-5-27
	 */
	public void getLedMedRemoveRxNo() {
		// if("Y".equals(Operator.getSpcFlg())) {
		TParm caseNoParm = new TParm();// =====pangben 2013-5-15 跑马灯移除数据
		// caseNoParm.setData("RX_NO", this.getValue("RX_NO"));
		if (ledMedUi != null) {
			ledMedUi.removeMessage(caseNoParm);
		}
		// }
	}

	// ====================== chenxi 保存同步信息
	private void synOPdOrderSave(String startDate, String endDate, String rxNo, String mrNo) {
		startDate = startDate.length() > 19 ? startDate.substring(0, 19) : startDate;
		endDate = endDate.length() > 10 ? endDate.substring(0, 10) + " 23:59:59" : endDate;
		// 调用后台的action
		TParm rxParm = new TParm();
		rxParm.setData("RX_NO", rxNo);
		rxParm.setData("START_DATE", startDate);
		rxParm.setData("END_DATE", endDate);
		TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction", "onSaveSpcRequest", rxParm);

	}

	private TParm getIndStockBatchNoBySkintest(String orderCode, String execDeptCode, double dosageQty) {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", execDeptCode);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("DOSAGE_QTY", dosageQty);
		TParm result = INDTool.getInstance().onQueryStockQtyBySkintest(parm);
		return result;
	}

	/**
	 * 门诊医生站挂界面，药品状态查询界面 ，将保存、打印、叫号、电子病例功能屏掉不能使用 ==========pangben 2014-3-13
	 * 
	 * @param flg
	 */
	private void setOdoAutEnabled(boolean flg) {
		if (type.equals("Return")) {
			if (this.getPopedem("odoAUT")) {// 医生权限
				this.callFunction("UI|save|setEnabled", flg);
				this.callFunction("UI|call|setEnabled", flg);
				this.callFunction("UI|elecCaseHistory|setEnabled", flg);
				this.callFunction("UI|returnDrugList|setEnabled", flg);
				this.setValue("DEPT_CODE", Operator.getDept());
				this.setValue("DR_CODE", Operator.getID());
				this.callFunction("UI|DEPT_CODE|setEnabled", flg);
				this.callFunction("UI|DR_CODE|setEnabled", flg);
				this.setTitle("药品状态查询");
			}
		}

	}

	public boolean onDownTableChangeValue(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();

		String colName = tNode.getTable().getParmMap(column);

		TParm parm = tNode.getTable().getParmValue();
		TTable tableDown = getTable("Table_DOWN");
		String orderCode = parm.getValue("ORDER_CODE", row);

		if (colName.equals("BATCH_NO")) {
			TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);

			String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);

			if ("Y".equals(skintestFlg)) {
				nowOrdList = pha.getCertainOrdListByRow(selectUPRow);
				String value = (String) tNode.getValue();

				// this.messageBox(nowOrdList.getOrder(row).getOrderDesc()+"");
				nowOrdList.getOrder(row).setBatchNo(value);

			} else {
				this.messageBox("不是皮试药品，不能填写批号！");
				tableDown.setItem(row, "BATCH_NO", "");
				return true;
			}

		}
		return false;
	}

	public String getAllerg(String mrNo) {
		boolean aflag = false;
		TParm Aresult = ODOTool.getInstance().getAllergyData(mrNo);// 过敏药品
		StringBuffer buf = new StringBuffer();
		String allerg = "";
		if (Aresult.getCount() > 0) {
			for (int j = 0; j < Aresult.getCount(); j++) {
				buf.append(",").append(Aresult.getValue("ORDER_DESC", j)).append(" ")
						.append(Aresult.getValue("ALLERGY_NOTE", j));
			}
			aflag = true;
		}
		if (aflag) {
			allerg = buf.toString();
			allerg = "过敏史:" + allerg.substring(1, allerg.length());
		}
		return allerg;
	}

	public static void main(String[] args) {

		String prtSql = " SELECT  A.LINK_NO, A.ORDER_DESC || CASE WHEN TRIM (A.GOODS_DESC) IS NOT NULL OR TRIM (A.GOODS_DESC) <> '' THEN '(' || A.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC,"
				+ " A.SPECIFICATION,D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,A.DOSAGE_QTY || ' ' || B.UNIT_CHN_DESC DOSAGE_QTY, "
				+ " A.TAKE_DAYS,A.DISPENSE_QTY,A.OWN_AMT "
				+ " FROM OPD_ORDER A ,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D  "
				+ " WHERE   A.DISPENSE_UNIT = B.UNIT_CODE  " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.RX_NO = '1'"
				+ " AND A.PHA_RETN_CODE IS NOT NULL ";
		// System.out.println("-------:"+prtSql);
	}

}
