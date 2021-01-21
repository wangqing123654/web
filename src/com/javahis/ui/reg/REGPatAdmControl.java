package com.javahis.ui.reg;

import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BIL;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.mem.MEMSQL;
import jdo.mem.MEMTool;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.opd.OrderTool;
import jdo.reg.PanelRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGCcbReTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.reg.RegMethodTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sid.IdCardO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.EktDriver;
import com.javahis.device.NJCityInwDriver;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.system.combo.TComboSysCtz;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import com.javahis.ui.ekt.EKTReceiptPrintControl;
import com.javahis.ui.opb.Objects;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * 
 * <p>
 * Title:挂号主档控制类
 * </p>
 * 
 * <p>
 * Description:挂号主档控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author wangl 2008.09.22
 * @version 1.0
 */
public class REGPatAdmControl extends TControl {
	// 病患对象
	private Pat pat;
	private Pat crmPat;
	private TParm mem = new TParm(); // 会员参数 huangtt
	private TParm memTrade = new TParm(); // 会员交易表参数 huangtt
	// 挂号对象
	private Reg reg;
	// 门急别
	private String admType = "O";
	// 预约时间
	String startTime;
	// 医疗卡卡号
	String ektCard;
	int selectRow = -1;
	String tredeNo;
	String businessNo; // 挂号出现问题撤销操作
	String tradeNoT;
	String endInvNo;
	private TParm p3; // 医保卡参数
	private boolean feeShow = false; // =====pangben 20110815 医保中心获得费用管控
	private boolean txEKT = false; // 泰心医疗卡管理执行直接写卡操作=====pangben 20110916
	private String ektOldSum; // 医疗卡操作失败回写金额
	private String ektNewSum; // 扣款以后的金额
	// 错误信息标记

	private TParm insParm; // 医保出参，U 方法 A 方法参数

	private boolean tjINS = false; // 天津医保管控，判断是否执行了医疗卡操作

	private boolean insFlg = false; // 医保卡读卡成功管控
	// private String caseNo; // 医保操作刷卡时需要就诊号
	private TParm regionParm; // 获得医保区域代码
	// zhangp 20111227
	private TParm parmSum; // 执行充值操作参数
	private boolean printBil = false; // 打印票据时使用
	private TParm reSetEktParm; // 医疗卡退费使用判断是否执行医疗卡退费操作
	private String confirmNo; // 医保卡就诊号，退挂时时使用
	private String reSetCaseNo; // 退挂使用就诊号码
	private String insType; // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特 退挂使用
	private boolean tableFlg = false; // 第一个页签（全部）表格 获得焦点管控
	private double ins_amt = 0.00; // 医保金额
	private boolean ins_exe = false; // 判断是否医保执行 操作，执行操作表数据时实现在途状态
	private TParm greenParm = null;// //绿色通道使用金额
	private double accountamtforreg = 0.00;// 个人账户
	private String preFlg = "N";// 是否有预开检查 caowl 20131117
	private String preCaseNo;// 预开检查传回的就诊号 caowl 20131117
	private String preAdmDate = "";// 预开检查传回的预开时间 yanjing 20131212
	private boolean isPreFlg = false;// 是否是预开检查传回注记 yanjing 20131212
	private boolean aheadFlg = true;

	private String triageFlg = "N"; // 急诊检伤等级 huangtt 20140310

	private boolean singleFlg = true; // 挂号是否打到诊单 add by huangtt 20140409
	private boolean ticketFlg = true; // 挂号是否打票 add by huangtt 20131231
	private String memCode; // 会员卡类型
	private TParm acceptData = new TParm(); // 接参
	private boolean crmFlg = true; // crm接口开关
	private String regCtz = ""; // 挂号身份
	boolean saveFlg = false;
	private String zfCtz = ""; // 自费身份
	private boolean crmRegFlg = false; // CRM预约标记 add by wangbin 20140807
	private String crmId = ""; // CRM预约号  add by wangbin 20140807
	private SocketLink client; // Socket传送病案室工具  add by wangbin 20140916
	private String mroRegNo;
	private String clinictypeCode = ""; //号别－－CRM接口用到 add by huangtt 20141205
	private String crmTime=""; //CRM预约时间

	// ,出现现金收费时没有在途状态判断
	/**
	 * 初始化参数
	 */
	public void onInitParameter() {
		// add by huangtt 20140211
		// 接收数据

		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			admType = acceptData.getData("ADM_TYPE").toString();
		} else {

			String parmAdmType = (String) this.getParameter();
			if (parmAdmType != null && parmAdmType.length() > 0)
				admType = parmAdmType;
		}
		setValue("ADM_TYPE", admType);
		callFunction("UI|SESSION_CODE|setAdmType", admType);
		callFunction("UI|CLINICTYPE_CODE|setAdmType", admType);
		callFunction("UI|VIP_SESSION_CODE|setAdmType", admType);
		callFunction("UI|setTitle", "O".equals(admType) ? "门诊挂号窗口" : "急诊挂号窗口");
		callFunction("UI|ERD_LEVEL_TITLE|setVisible", false);
		callFunction("UI|ERD_LEVEL|setVisible", false);

		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
			TParm selTriageFlg = REGSysParmTool.getInstance().selectdata();
			triageFlg = selTriageFlg.getValue("TRIAGE_FLG", 0);
			if ("N".equals(triageFlg))
				callFunction("UI|ERD_LEVEL|setEnabled", false);
			setValue("ADM_DATE", SystemTool.getInstance().getDate());
			String sessionCode = initSessionCode();
			Timestamp admDate = TJDODBTool.getInstance().getDBTime();
			// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
			if (!StringUtil.isNullString(sessionCode)
					&& !StringUtil.isNullString(admType)) {
				admDate = SessionTool.getInstance().getDateForSession(admType,
						sessionCode, Operator.getRegion());
				this.setValue("ADM_DATE", admDate);
			}
			// add by huangtt 20140504
			TComboBox ctz1 = new TComboSysCtz();
			callFunction("UI|CTZ1_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ1_CODE|setEmgFitFlg", "Y");
			ctz1.onQuery();
			TComboBox ctz2 = new TComboSysCtz();
			callFunction("UI|CTZ2_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ2_CODE|setEmgFitFlg", "Y");
			ctz2.onQuery();
			TComboBox ctz3 = new TComboSysCtz();
			callFunction("UI|CTZ3_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ3_CODE|setEmgFitFlg", "Y");
			ctz3.onQuery();
			TTextFormat ctzReg1 = new TextFormatSYSCtz();
			callFunction("UI|REG_CTZ1|setOpdFitFlg", "");
			callFunction("UI|REG_CTZ1|setEmgFitFlg", "Y");
			ctzReg1.onQuery();
			TComboBox ctzReg2 = new TComboSysCtz();
			callFunction("UI|REG_CTZ2|setOpdFitFlg", "");
			callFunction("UI|REG_CTZ2|setEmgFitFlg", "Y");
			ctzReg2.onQuery();

		}
		// 初始化科室Combo
		callFunction("UI|DEPT_CODE|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		// 初始化科室(普通诊sort)Combo
		callFunction("UI|DEPT_CODE_SORT|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码

	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();

		// 初始化时段Combo,取得默认时段
		initSession();
		setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		// ============huangtt modify 20131106 start 证件类型默认显示身份证
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE CHN_DESC = '身份证' AND GROUP_ID = 'SYS_IDTYPE'";
		TParm typeParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ID_TYPE", typeParm.getValue("ID", 0));
		sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE CTZ_DESC='自费'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		zfCtz = ctzParm.getValue("CTZ_CODE", 0);
		// ============huangtt modify 20131106 end
		
		// 初始化默认(现场)挂号方式
		setValue("REGMETHOD_CODE", "A");
		// 初始化ID号输入框
		// onClickRadioButton();
		this.onClear();
		initSchDay();
		// 初始化预约信息开始时间
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// 初始化VIP班表Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// 置退挂,报道按钮为灰
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|NHI_NO|setEnabled", false); // 医保卡不可编辑
		// // 初始化初复诊
		// TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		// if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
		// setValue("VISIT_CODE_F", "Y");
		// callFunction("UI|MR_NO|setEnabled", true);
		// }

		// ===zhangp 20120306 modify end
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		// this.onClear();
		// ======zhangp 20120224 modify start
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// ======zhangp 20120224 modify end
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();

		ticketFlg = REGSysParmTool.getInstance().selTicketFlg();
		singleFlg = REGSysParmTool.getInstance().selSingleFlg();
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		// if(ticketFlg){
		// // 初始化下一票号
		// BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// endInvNo = invoice.getEndInvno();
		// // ===zhangp 20120306 modify start
		// if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
		// Operator.getRegion(), invoice.getUpdateNo())) {
		// setValue("NEXT_NO", invoice.getUpdateNo());
		// } else {
		// messageBox("票据已用完");
		// }
		// }

		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			String mrNo = acceptData.getData("MR_NO").toString();
			this.setValue("MR_NO", mrNo);
			this.onQueryNO();
		}
	}

	/**
	 * 初始化班表
	 */
	public void initSchDay() {
		new Thread() {
			// 线程,为节省时间提高打开挂号主界面效率
			public void run() {
				// 初始化默认支付方式
				// ===zhangp 不修改支付方式 20130517
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// 初始化带入医师排班
				onQueryDrTable();

				// 初始化带入VIP班表
				onQueryVipDrTable();
			}
		}.start();

	}

	/**
	 * 增加对Table1的监听
	 */
	public void onTable1Clicked() {
		if(pat == null){
			this.messageBox("无病患信息");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|Table1|getClickedRow");
		if (row < 0)
			return;
		// =====20130507 yanjing 添加查询日班表数据库判断对应信息是否存在
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		String admDate = parm.getValue("ADM_DATE").substring(0, 4)
				+ parm.getValue("ADM_DATE").substring(5, 7)
				+ parm.getValue("ADM_DATE").substring(8, 10);
		parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO", row));
		clinictypeCode = tableParm.getValue("CLINICTYPE_CODE", row); //add by huangtt 20141205
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount() <= 0) {
			callFunction("UI|SAVE_REG|setEnabled", false);// 收费按钮不可编辑=====yanjing
			this.messageBox("日期、医师及诊室信息不一致，请刷新界面！");
			return;
		}
		// =======20130507 yanjing end
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 删除了待诊人数相关代码
		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}
		getCTZ();
		// 不打票操作
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			// zhangp
			if (aheadFlg) {
				onClickClinicType(true);
			} else {
				onClickClinicType(false);
			}
		}
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		// 置退挂按钮不可编辑
		callFunction("UI|unreg|setEnabled", false);
		// 置补印按钮不可编辑
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // 第一个页签管控
		this.grabFocus("FeeS");

	}
	
	/**
	 * 增加对Table1的监听
	 */
	public void onTable1Clicked(int row) {
		if(pat == null){
			this.messageBox("无病患信息");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		
		// =====20130507 yanjing 添加查询日班表数据库判断对应信息是否存在
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		String admDate = parm.getValue("ADM_DATE").substring(0, 4)
				+ parm.getValue("ADM_DATE").substring(5, 7)
				+ parm.getValue("ADM_DATE").substring(8, 10);
		parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO", row));
		clinictypeCode = tableParm.getValue("CLINICTYPE_CODE", row); //add by huangtt 20141205
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount() <= 0) {
			callFunction("UI|SAVE_REG|setEnabled", false);// 收费按钮不可编辑=====yanjing
			this.messageBox("日期、医师及诊室信息不一致，请刷新界面！");
			return;
		}
		// =======20130507 yanjing end
		// add by wangbin 20140807 增加用于判断当前病患是否是CRM预约的标记 START
		// 自动带入的号别如果不是空，则说明是CRM预约的病患
		if (!"".equals(this.getValueString("CLINICTYPE_CODE"))) {
			crmRegFlg = true;
		}
		// add by wangbin 20140807 增加用于判断当前病患是否是CRM预约的标记 END
//		clinictypeCode = parm.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 删除了待诊人数相关代码
		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}
		getCTZ();
		// 不打票操作
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			// zhangp
			if (aheadFlg) {
				onClickClinicType(true);
			} else {
				onClickClinicType(false);
			}
		}
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		// 置退挂按钮不可编辑
		callFunction("UI|unreg|setEnabled", false);
		// 置补印按钮不可编辑
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // 第一个页签管控
		this.grabFocus("FeeS");

	}
	
	

	/**
	 * 增加对Talbe2的监听事件
	 */
	public void onTable2Clicked() {
		if(pat == null){
			this.messageBox("无病患信息");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		int row = (Integer) callFunction("UI|Table2|getClickedRow");
		if (row < 0)
			return;
		// 拿到table控件
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("已占号!");
			callFunction("UI|table2|clearSelection");
			return;
		}
		// =====已过诊pangben 2012-3-26 start

		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// 系统当前时间
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// 系统当前日期
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 当前挂号日期
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("已经过诊不可以挂号");
			callFunction("UI|table2|clearSelection");
			return;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("已经过诊不可以挂号");
				callFunction("UI|table2|clearSelection");
				return;
			}
		}
		// =====已过诊pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);
		clinictypeCode = data.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		getCTZ();
		onClickClinicType(true);

		this.grabFocus("FeeS");
		// //add by huangtt 20140303
		// if(!this.getValueString("MR_NO").equals("")){
		// double clinicFee = this.getValueDouble("FeeY");
		// double currentBalance =
		// EKTpreDebtTool.getInstance().getEkeMaster(this.getValueString("MR_NO"));
		// String sqlCtz =
		// "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"+this.getValueString("REG_CTZ1")+"'";
		// TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
		// double overdraft = 0;
		// if(parmCtz.getCount()>0){
		// overdraft = parmCtz.getDouble("OVERDRAFT", 0);
		// }
		// // double currentBalance =2;
		// if(currentBalance+overdraft<clinicFee){
		// this.messageBox("医疗卡余额小于诊查费");
		// }
		// }
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
	}

	/**
	 * 增加对Talbe2的监听事件
	 */
	public boolean onTable2Clicked(int row) {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		// int row = (Integer) callFunction("UI|Table2|getClickedRow");
		// if (row < 0)
		// return;
		// 拿到table控件
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("该号已预约占号，请报到!");
			callFunction("UI|table2|clearSelection");
			return false;
		}
		// =====已过诊pangben 2012-3-26 start

		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// 系统当前时间
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// 系统当前日期
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 当前挂号日期
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("已经过诊不可以挂号");
			callFunction("UI|table2|clearSelection");
			return true;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("已经过诊不可以挂号");
				callFunction("UI|table2|clearSelection");
				return true;
			}
		}
		
		// =====已过诊pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);
		
		// add by wangbin 20140807 增加用于判断当前病患是否是CRM预约的标记 START
		// 自动带入的号别如果不是空，则说明是CRM预约的病患
		if (!"".equals(this.getValueString("CLINICTYPE_CODE"))) {
			crmRegFlg = true;
		}
		// add by wangbin 20140807 增加用于判断当前病患是否是CRM预约的标记 END
		clinictypeCode = data.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		getCTZ();
		onClickClinicType(true);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		this.grabFocus("FeeS");
		return true;
		// //add by huangtt 20140303
		// if(!this.getValueString("MR_NO").equals("")){
		// double clinicFee = this.getValueDouble("FeeY");
		// double currentBalance =
		// EKTpreDebtTool.getInstance().getEkeMaster(this.getValueString("MR_NO"));
		// String sqlCtz =
		// "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"+this.getValueString("REG_CTZ1")+"'";
		// TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
		// double overdraft = 0;
		// if(parmCtz.getCount()>0){
		// overdraft = parmCtz.getDouble("OVERDRAFT", 0);
		// }
		// // double currentBalance =2;
		// if(currentBalance+overdraft<clinicFee){
		// this.messageBox("医疗卡余额小于诊查费");
		// }
		// }
		
	}

	/**
	 * 增加对Talbe3的监听事件
	 */
	public void onTable3Clicked() {
		int row = (Integer) callFunction("UI|Table3|getSelectedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		TParm parm = table3.getParmValue();
		// System.out.println("退挂信息" + parm);
		// parm.getValue("ARRIVE_FLG",row);隐藏列取法
//		String arriveFlg = (String) table3.getValueAt(row, 7);
		String arriveFlg = parm.getValue("ARRIVE_FLG", row);
		
		String isPreOrder = parm.getValue("IS_PRE_ORDER", row);
		String crmFlg = parm.getValue("CRM_FLG", row);// CRM标记
		if (crmFlg.equals("Y")) {
			this.messageBox("该数据为CRM预约信息");
			callFunction("UI|unreg|setEnabled", false);
			callFunction("UI|arrive|setEnabled", false);
			callFunction("UI|print|setEnabled", false);
			return;
		}
		if (isPreOrder.equals("Y")) {// 预开检查的预约号
			String admdate = "";// yanjing 20131212
			admdate = parm.getValue("ADM_DATE", row);
			if (admdate.equals(null) || "".equals(admdate)) {
				admdate = "";
			} else {
				admdate = admdate.substring(0, 10);
				String date = SystemTool.getInstance().getDate().toString();
				date = date.substring(0, 10);
				if (!admdate.equals(date)) {
					messageBox("非当日，不能报到。");
					return;
				}
			}
			this.preFlg = "Y";
			this.preCaseNo = parm.getValue("CASE_NO", row);
			this.preAdmDate = parm.getValue("ADM_DATE", row);
			setValueForParm("ADM_DATE;CONTRACT_CODE;REGMETHOD_CODE", parm, row);
			// 置报道按钮不可编辑
			callFunction("UI|arrive|setEnabled", false);
			callFunction("UI|unreg|setEnabled", true);// 置退挂按钮可编辑
		} else {
			// 判断是否预约挂号
			if ("N".equals(arriveFlg)) {
				if(parm.getValue("PAT_PACKAGE",row)!=null && !parm.getValue("PAT_PACKAGE",row).equals("")){
					TParm packageParm = new TParm(TJDODBTool.getInstance().select("SELECT PACKAGE_DESC FROM MEM_PACKAGE WHERE PACKAGE_CODE='"+parm.getValue("PAT_PACKAGE",row)+"'"));
					this.setValue("PAT_PACKAGE", packageParm.getValue("PACKAGE_DESC",0).toString());
				}
				setValueForParm(
						"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE;INSURE_INFO;PAT_PACKAGE;REASSURE_FLG",
						parm, row);

				// setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
				setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
				setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
				getCTZ();
				onClickClinicType(true);
				// onDateReg();
				callFunction("UI|CLINICROOM_NO|onQuery");
				// 置报道按钮可编辑
				callFunction("UI|arrive|setEnabled", true);
				// setValue("FeeY", parm.getValue("ARRIVE_FLG", row));
				// setValue("FeeS", parm.getValue("ARRIVE_FLG", row));
				this.messageBox(getValue("PAT_NAME") + "有预约信息");
				// 置收费按钮不可编辑
				// ===zhangp 20120306 modify start
				callFunction("UI|SAVE_REG|setEnabled", false);
				// ===zhangp 20120306 modify end
				// 置退挂按钮为灰
				callFunction("UI|unreg|setEnabled", true);

			} else {
				// System.out.println("已挂信息:::"+parm);
				// this.messageBox_("已挂信息"+parm);
				if(parm.getValue("PAT_PACKAGE",row)!=null && !parm.getValue("PAT_PACKAGE",row).equals("")){
					TParm packageParm = new TParm(TJDODBTool.getInstance().select("SELECT PACKAGE_DESC FROM MEM_PACKAGE WHERE PACKAGE_CODE='"+parm.getValue("PAT_PACKAGE",row)+"'"));
					this.setValue("PAT_PACKAGE", packageParm.getValue("PACKAGE_DESC",0).toString());
				}
				setValueForParm(
						"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE;INSURE_INFO;REASSURE_FLG",
						parm, row);
				
				setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
				setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
				setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
				setValue("REQUIREMENT", parm.getValue("REQUIREMENT", row)); // add
				// by
				// huangtt
				// 20131121
				callFunction("UI|DEPT_CODE|onQuery");
				callFunction("UI|DR_CODE|onQuery");
				callFunction("UI|CLINICROOM_NO|onQuery");
				callFunction("UI|CLINICTYPE_CODE|onQuery");
				// onClickClinicType( -1);
				// ==================pangben modify 20110815 修改获得票据表中的价格显示到界面
				unregFeeShow(parm.getValue("CASE_NO", row));
				setValueForParm(
						"CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
						parm, row);
				// 置报道按钮不可编辑
				callFunction("UI|arrive|setEnabled", false);
				// 置收费按钮不可编辑
				callFunction("UI|SAVE_REG|setEnabled", false);
				// 置退挂按钮可编辑
				callFunction("UI|unreg|setEnabled", true);
				// 置补印按钮可编辑
				callFunction("UI|print|setEnabled", true);
			}
		}
		setControlEnabled(false);
		callFunction("UI|INSURE_INFO|setEnabled", false);
		callFunction("UI|PAT_PACKAGE|setEnabled", false);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
	}

	/**
	 * 无身份注记事件
	 */
	public void onSelForeieignerFlg() {
		if (this.getValue("FOREIGNER_FLG").equals("Y"))
			this.grabFocus("BIRTH_DATE");
		if (this.getValue("FOREIGNER_FLG").equals("N"))
			this.grabFocus("IDNO");
	}

	/**
	 * 初复诊状态
	 */
	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
			callFunction("UI|MR_NO|setEnabled", false);
			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		this.onClear();
	}

	/**
	 * 保存病患信息
	 */
	public void onSavePat() {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		// 不能输入空值
		//if(this.getValueString("MR_NO").equals("")){//更新时，出生日期不能修改moidfy by huangjw 20150721
			if (getValue("BIRTH_DATE") == null) {
				this.messageBox("出生日期不能为空!");
				return;
			}
		//}
		// add by huangtt 20140320
		if (getValueString("PAT_NAME").equals("")) {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
				if (this.messageBox("姓名赋值", "是否将firstName和lastName合并赋值给姓名？", 0) != 0) {
					this.messageBox("姓名不能为空!");
					return;
				}
				this.setValue("PAT_NAME", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));

			} else if (getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				//if(this.getValueString("MR_NO").equals("")){//更新时，姓名不能修改moidfy by huangjw 20150721
					this.messageBox("姓名不能为空!");
					this.grabFocus("PAT_NAME");
					return;
				//}
			}
		} else {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("请输入lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
			}
		}
		//if(this.getValueString("MR_NO").equals("")){//更新时，性别不能修改moidfy by huangjw 20150721
			if (getValue("SEX_CODE") == null
					|| getValue("SEX_CODE").toString().length() <= 0) {
				this.messageBox("性别不能为空!");
				this.grabFocus("SEX_CODE");
				return;
			}
		//}
		if(!getValue("ID_TYPE").toString().equals("99")){
        	if (getValue("IDNO") == null || getValue("IDNO").toString().length()<=0) {
                this.messageBox("证件号不能为空!");
                this.grabFocus("IDNO");
                return ;
            }
        }
		
		if (getValue("CTZ1_CODE") == null
				|| getValue("CTZ1_CODE").toString().length() <= 0) {
			this.messageBox("身份一不能为空!");
			this.grabFocus("CTZ1_CODE");
			return;
		}

//		if (!this.emptyTextCheck("SEX_CODE,CTZ1_CODE,ID_TYPE"))
//			return;

		pat = new Pat();
		// 病患姓名
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// 英文名
		pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
		// 姓名拼音
		pat.setPy1(TypeTool.getString(getValue("PY1")));
		// 证件类型
		pat.setIdType(TypeTool.getString(getValue("ID_TYPE"))); // add by
		// huangtt
		// 20131106
		// 身份证号
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// 外国人注记
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
		// 出生日期
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// 性别
		pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
		// 电话
		// pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
		pat.setCellPhone(TypeTool.getString(getValue("CELL_PHONE")));
		// 邮编
		pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
		// 地址
		pat.setResidAddress(TypeTool.getString(getValue("RESID_ADDRESS")));
		// 现住址
		pat.setCurrentAddress(TypeTool.getString(getValue("CURRENT_ADDRESS"))); // add
		// by
		// huangtt
		// 20131106
		pat.setAddress(TypeTool.getString(getValue("CURRENT_ADDRESS"))); // add
		// by
		// huangtt
		// 20131106
		// 身份1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// 身份2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// 身份3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// 医保卡市民卡
		pat.setNhiNo(TypeTool.getString("")); // =============pangben
		// 备注
		pat.setRemarks(TypeTool.getString(getValue("REMARKS"))); // add by
		// huangtt
		// 20131106
		pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
		pat.setSpeciesCode(TypeTool.getString(getValue("SPECIES_CODE")));
		pat.setMarriageCode(TypeTool.getString(getValue("MARRIAGE_CODE")));
		pat.setFirstName(TypeTool.getString(getValue("FIRST_NAME")));
		pat.setLastName(TypeTool.getString(getValue("LAST_NAME")));
		// modify
		// 20110808
		if (this.messageBox("病患信息", "是否保存", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
		patParm.setData("LAST_NAME", getValue("LAST_NAME"));
		patParm.setData("FIRST_NAME", getValue("FIRST_NAME"));
		patParm.setData("PY1", getValue("PY1"));
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("CELL_PHONE", getValue("CELL_PHONE"));
		patParm.setData("SEX_CODE", getValue("SEX_CODE"));
		patParm.setData("POST_CODE", getValue("POST_CODE"));
		patParm.setData("RESID_ADDRESS", getValue("RESID_ADDRESS"));
		patParm.setData("ADDRESS", getValue("CURRENT_ADDRESS"));
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO", ""); // =============pangben
		patParm.setData("ID_TYPE", getValue("ID_TYPE")); // add by huangtt
		// 20131106
		patParm.setData("CURRENT_ADDRESS", getValue("CURRENT_ADDRESS")); // add
		// by
		// huangtt
		// 20131106
		patParm.setData("REMARKS", getValue("REMARKS")); // add by huangtt
		// 20131106
		patParm.setData("NATION_CODE", getValueString("NATION_CODE"));
		patParm.setData("SPECIES_CODE", getValueString("SPECIES_CODE"));
		patParm.setData("MARRIAGE_CODE", getValueString("MARRIAGE_CODE"));

		// modify 20110808
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME1").toString())) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("LAST_NAME").toString())) {
			patParm.setData("LAST_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("FIRST_NAME").toString())) {
			patParm.setData("FIRST_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PY1").toString())) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString("" + getValue("CELL_PHONE"))) {
			patParm.setData("CELL_PHONE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX_CODE").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("POST_CODE").toString())) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("RESID_ADDRESS").toString())) {
			patParm.setData("RESID_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		// =============pangben modify 20110808
		// if (StringUtil.isNullString(getValue("NHI_NO").toString())) {
		// patParm.setData("NHI_NO", new TNull(String.class));
		// }
		// ====huangtt 20131106 start
		if (StringUtil.isNullString(getValue("ID_TYPE").toString())) {
			patParm.setData("ID_TYPE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CURRENT_ADDRESS").toString())) {
			patParm.setData("CURRENT_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CURRENT_ADDRESS").toString())) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("REMARKS").toString())) {
			patParm.setData("REMARKS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("NATION_CODE").toString())) {
			patParm.setData("NATION_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SPECIES_CODE").toString())) {
			patParm.setData("SPECIES_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("MARRIAGE_CODE").toString())) {
			patParm.setData("MARRIAGE_CODE", new TNull(String.class));
		}
		// ====huangtt 20131106 end
		TParm result = new TParm();
		// ===zhangp 20120613 start
		// if ("Y".equals(getValue("VISIT_CODE_F"))) {
		if (!"".equals(getValueString("MR_NO"))) {
			// ===zhangp 20120613 end
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("请先检索出病患");
				return;
			}
			// 更新病患
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());

			TParm parmMem = new TParm();
			parmMem.setData("MR_NO", pat.getMrNo());
			parmMem.setData("CUSTOMER_SOURCE", this
					.getValueString("CUSTOMER_SOURCE"));
			parmMem.setData("GUARDIAN1_NAME", this
					.getValueString("GUARDIAN_NAME"));
			parmMem.setData("GUARDIAN1_RELATION", this
					.getValueString("GUARDIAN_RELATION"));
			if (mem.getCount() > 0) {
				result = MEMTool.getInstance()
						.updateMemPatInfoGuardian(parmMem);
			} else {
				result = MEMTool.getInstance().insertMemPatInfo(parmMem);
			}

			if (crmFlg) {
				// System.out.println("patParm==="+patParm);
				// add by huangtt 20140401 CRM----start
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", "<TNULL>".equals(patParm
						.getValue("PAT_NAME")) ? "" : patParm
						.getValue("PAT_NAME"));
				parm.setData("PY1",
						"<TNULL>".equals(patParm.getValue("PY1")) ? ""
								: patParm.getValue("PY1"));
				parm.setData("FIRST_NAME", "<TNULL>".equals(patParm
						.getValue("FIRST_NAME")) ? "" : patParm
						.getValue("FIRST_NAME"));
				parm.setData("LAST_NAME", "<TNULL>".equals(patParm
						.getValue("LAST_NAME")) ? "" : patParm
						.getValue("LAST_NAME"));
				parm.setData("OLDNAME", crmPat.getOldName());
				parm.setData("ID_TYPE", "<TNULL>".equals(patParm
						.getValue("ID_TYPE")) ? "" : patParm
						.getValue("ID_TYPE"));
				parm.setData("IDNO",
						"<TNULL>".equals(patParm.getValue("IDNO")) ? ""
								: patParm.getValue("IDNO"));
				parm.setData("SEX_CODE", "<TNULL>".equals(patParm
						.getValue("SEX_CODE")) ? "" : patParm
						.getValue("SEX_CODE"));
				parm.setData("BIRTH_DATE", "<TNULL>".equals(patParm
						.getValue("BIRTH_DATE")) ? "" : patParm
						.getValue("BIRTH_DATE"));
				parm.setData("NATION_CODE", "<TNULL>".equals(patParm
						.getValue("NATION_CODE")) ? "" : patParm
						.getValue("NATION_CODE"));
				parm.setData("NATION_CODE2", "<TNULL>".equals(patParm
						.getValue("SPECIES_CODE")) ? "" : patParm
						.getValue("SPECIES_CODE"));
				parm.setData("RELIGION", crmPat.getReligionCode());
				parm.setData("MARRIAGE", "<TNULL>".equals(patParm
						.getValue("MARRIAGE_CODE")) ? "" : patParm
						.getValue("MARRIAGE_CODE"));
				parm.setData("RESID_POST_CODE", crmPat.getResidPostCode());
				parm.setData("RESID_ADDRESS", "<TNULL>".equals(patParm
						.getValue("RESID_ADDRESS")) ? "" : patParm
						.getValue("RESID_ADDRESS"));
				parm.setData("POST_CODE", "<TNULL>".equals(patParm
						.getValue("POST_CODE")) ? "" : patParm
						.getValue("POST_CODE"));
				parm.setData("CURRENT_ADDRESS", "<TNULL>".equals(patParm
						.getValue("CURRENT_ADDRESS")) ? "" : patParm
						.getValue("CURRENT_ADDRESS"));
				parm.setData("HOMEPLACE_CODE", crmPat.gethomePlaceCode());
				parm.setData("BIRTH_HOSPITAL", mem
						.getValue("BIRTH_HOSPITAL", 0));
				parm.setData("SCHOOL_NAME", mem.getValue("SCHOOL_NAME", 0));
				parm.setData("SCHOOL_TEL", mem.getValue("SCHOOL_TEL", 0));
				parm.setData("SOURCE", mem.getValue("SOURCE", 0));
				parm.setData("INSURANCE_COMPANY1_CODE", mem.getValue(
						"INSURANCE_COMPANY1_CODE", 0));
				parm.setData("INSURANCE_COMPANY2_CODE", mem.getValue(
						"INSURANCE_COMPANY2_CODE", 0));
				parm.setData("INSURANCE_NUMBER1", mem.getValue(
						"INSURANCE_NUMBER1", 0));
				parm.setData("INSURANCE_NUMBER2", mem.getValue(
						"INSURANCE_NUMBER2", 0));
				parm.setData("GUARDIAN1_NAME", mem
						.getValue("GUARDIAN1_NAME", 0));
				parm.setData("GUARDIAN1_RELATION", mem.getValue(
						"GUARDIAN1_RELATION", 0));
				parm.setData("GUARDIAN1_TEL", mem.getValue("GUARDIAN1_TEL", 0));
				parm.setData("GUARDIAN1_PHONE", mem.getValue("GUARDIAN1_PHONE",
						0));
				parm.setData("GUARDIAN1_COM", mem.getValue("GUARDIAN1_COM", 0));
				parm.setData("GUARDIAN1_ID_TYPE", mem.getValue(
						"GUARDIAN1_ID_TYPE", 0));
				parm.setData("GUARDIAN1_ID_CODE", mem.getValue(
						"GUARDIAN1_ID_CODE", 0));
				parm.setData("GUARDIAN1_EMAIL", mem.getValue("GUARDIAN1_EMAIL",
						0));
				parm.setData("GUARDIAN2_NAME", mem
						.getValue("GUARDIAN2_NAME", 0));
				parm.setData("GUARDIAN2_RELATION", mem.getValue(
						"GUARDIAN2_RELATION", 0));
				parm.setData("GUARDIAN2_TEL", mem.getValue("GUARDIAN2_TEL", 0));
				parm.setData("GUARDIAN2_PHONE", mem.getValue("GUARDIAN2_PHONE",
						0));
				parm.setData("GUARDIAN2_COM", mem.getValue("GUARDIAN2_COM", 0));
				parm.setData("GUARDIAN2_ID_TYPE", mem.getValue(
						"GUARDIAN2_ID_TYPE", 0));
				parm.setData("GUARDIAN2_ID_CODE", mem.getValue(
						"GUARDIAN2_ID_CODE", 0));
				parm.setData("GUARDIAN2_EMAIL", mem.getValue("GUARDIAN2_EMAIL",
						0));
				parm.setData("REG_CTZ1_CODE", mem.getValue("REG_CTZ1_CODE", 0));
				parm.setData("REG_CTZ2_CODE", mem.getValue("REG_CTZ2_CODE", 0));
				parm.setData("FAMILY_DOCTOR", mem.getValue("FAMILY_DOCTOR", 0));
				parm.setData("ACCOUNT_MANAGER_CODE", mem.getValue(
						"ACCOUNT_MANAGER_CODE", 0));
				parm.setData("MEM_TYPE", mem.getValue("MEM_CODE", 0));
				parm.setData("START_DATE", "".equals(mem.getValue("START_DATE",
						0)) ? "" : mem.getValue("START_DATE", 0).substring(0,
						10));
				parm.setData("END_DATE",
						"".equals(mem.getValue("END_DATE", 0)) ? "" : mem
								.getValue("END_DATE", 0).substring(0, 10));
				String sDate = mem.getValue("START_DATE", 0);
				String eDate = mem.getValue("END_DATE", 0);
				Timestamp date = SystemTool.getInstance().getDate();
				if (sDate.length() > 0 && eDate.length() > 0) {
					// 计算购买月龄
					int buyMonthAge = getBuyMonth(sDate.substring(0, 10)
							.replaceAll("-", ""), eDate.substring(0, 10)
							.replaceAll("-", ""));

					// 发生月龄
					int currMonthAge = getBuyMonth(sDate.substring(0, 10)
							.replaceAll("-", ""), date.toString().substring(0,
							10).replaceAll("-", ""));

					parm.setData("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
					parm.setData("HAPPEN_MONTH_AGE", String
							.valueOf(currMonthAge));
				} else {
					parm.setData("BUY_MONTH_AGE", "");
					parm.setData("HAPPEN_MONTH_AGE", "");
				}
				parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
				parm.setData("REASON", memTrade.getValue("REASON", 0));
				parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue(
						"START_DATE", 0)) ? "" : memTrade.getValue(
						"START_DATE", 0).substring(0, 10));
				parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue(
						"END_DATE", 0)) ? "" : memTrade.getValue("END_DATE", 0)
						.substring(0, 10));
				parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
				parm
						.setData("INTRODUCER1", memTrade.getValue(
								"INTRODUCER1", 0));
				parm
						.setData("INTRODUCER2", memTrade.getValue(
								"INTRODUCER2", 0));
				parm
						.setData("INTRODUCER3", memTrade.getValue(
								"INTRODUCER3", 0));
				parm
						.setData("DESCRIPTION", memTrade.getValue(
								"DESCRIPTION", 0));
				parm.setData("CTZ1_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ1_CODE")) ? "" : patParm
						.getValue("CTZ1_CODE"));
				parm.setData("CTZ2_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ2_CODE")) ? "" : patParm
						.getValue("CTZ2_CODE"));
				parm.setData("CTZ3_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ3_CODE")) ? "" : patParm
						.getValue("CTZ3_CODE"));
				parm.setData("SPECIAL_DIET", crmPat.getSpecialDiet().getValue());
				parm.setData("E_MAIL", crmPat.getEmail());
				parm.setData("TEL_HOME", crmPat.getTelHome());
				parm.setData("CELL_PHONE", "<TNULL>".equals(patParm
						.getValue("CELL_PHONE")) ? "" : patParm
						.getValue("CELL_PHONE"));

				System.out.println("CRM信息更新同步===" + parm);
				TParm parmCRM = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "updateMemberByMrNo1", parm);

				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM信息更新同步失败！");
				}
				// add by huangtt 20140401 CRM----end
			}

		} else {
			// 新增病患
			// pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
			saveFlg = true;
			// add by huangtt 20140210
			if (!this.getValueString("GUARDIAN_NAME").equals("")
					|| !this.getValueString("GUARDIAN_RELATION").equals("")
					|| !this.getValueString("CUSTOMER_SOURCE").equals("")) {
				TParm parmMem = new TParm();
				parmMem.setData("MR_NO", pat.getMrNo());
				parmMem.setData("CUSTOMER_SOURCE", this
						.getValueString("CUSTOMER_SOURCE"));
				parmMem.setData("GUARDIAN1_NAME", this
						.getValueString("GUARDIAN_NAME"));
				parmMem.setData("GUARDIAN1_RELATION", this
						.getValueString("GUARDIAN_RELATION"));
				// System.out.println("parmMEM==="+parmMem);
				result = MEMTool.getInstance().insertMemPatInfo(parmMem);

			}
			this.setValue("VISIT_CODE_C", true);

			// add by huangtt 20140401 CRM----start
			if (crmFlg) {
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", pat.getName());
				parm.setData("PY1", pat.getPy1());
				parm.setData("FIRST_NAME", pat.getFirstName());
				parm.setData("LAST_NAME", pat.getLastName());
				parm.setData("OLDNAME", "");
				parm.setData("ID_TYPE", pat.getIdType());
				parm.setData("IDNO", pat.getIdNo());
				parm.setData("SEX_CODE", pat.getSexCode());
				parm.setData("BIRTH_DATE", pat.getBirthday());
				parm.setData("NATION_CODE", pat.getNationCode());
				parm.setData("NATION_CODE2", pat.getSpeciesCode());
				parm.setData("MARRIAGE", pat.getMarriageCode());
				parm.setData("RESID_POST_CODE", "");
				parm.setData("RESID_ADDRESS", pat.getResidAddress());
				parm.setData("POST_CODE", pat.getPostCode());
				parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
				parm.setData("CELL_PHONE", pat.getCellPhone());
				parm.setData("SPECIAL_DIET", "");
				parm.setData("E_MAIL", "");
				parm.setData("TEL_HOME", "");
				parm.setData("CTZ1_CODE", pat.getCtz1Code());
				parm.setData("CTZ2_CODE", pat.getCtz2Code());
				parm.setData("CTZ3_CODE", pat.getCtz3Code());
				parm.setData("HOMEPLACE_CODE", "");
				parm.setData("RELIGION", "");
				parm.setData("BIRTH_HOSPITAL", "");
				parm.setData("SCHOOL_NAME", "");
				parm.setData("SCHOOL_TEL", "");
				parm.setData("SOURCE", "");
				parm.setData("INSURANCE_COMPANY1_CODE", "");
				parm.setData("INSURANCE_COMPANY2_CODE", "");
				parm.setData("INSURANCE_NUMBER1", "");
				parm.setData("INSURANCE_NUMBER2", "");
				parm.setData("GUARDIAN1_NAME", this
						.getValueString("GUARDIAN_NAME"));
				parm.setData("GUARDIAN1_RELATION", this
						.getValueString("GUARDIAN_RELATION"));
				parm.setData("GUARDIAN1_TEL", "");
				parm.setData("GUARDIAN1_PHONE", "");
				parm.setData("GUARDIAN1_COM", "");
				parm.setData("GUARDIAN1_ID_TYPE", "");
				parm.setData("GUARDIAN1_ID_CODE", "");
				parm.setData("GUARDIAN1_EMAIL", "");
				parm.setData("GUARDIAN2_NAME", "");
				parm.setData("GUARDIAN2_RELATION", "");
				parm.setData("GUARDIAN2_TEL", "");
				parm.setData("GUARDIAN2_PHONE", "");
				parm.setData("GUARDIAN2_COM", "");
				parm.setData("GUARDIAN2_ID_TYPE", "");
				parm.setData("GUARDIAN2_ID_CODE", "");
				parm.setData("GUARDIAN2_EMAIL", "");
				parm.setData("REG_CTZ1_CODE", "");
				parm.setData("REG_CTZ2_CODE", "");
				parm.setData("FAMILY_DOCTOR", "");
				parm.setData("ACCOUNT_MANAGER_CODE", "");
				parm.setData("MEM_TYPE", "");
				parm.setData("START_DATE", "");
				parm.setData("END_DATE", "");
				parm.setData("BUY_MONTH_AGE", "");
				parm.setData("HAPPEN_MONTH_AGE", "");
				parm.setData("MEM_CODE", "");
				parm.setData("REASON", "");
				parm.setData("START_DATE_TRADE", "");
				parm.setData("END_DATE_TRADE", "");
				parm.setData("MEM_FEE", "");
				parm.setData("INTRODUCER1", "");
				parm.setData("INTRODUCER2", "");
				parm.setData("INTRODUCER3", "");
				parm.setData("DESCRIPTION", "");
				// System.out.println("CRM信息新增同步==="+parm);
				TParm parmCRM = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "createMember1", parm);
				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM信息新增同步失败！");
				}
			}

			// add by huangtt 20140401 CRM----end

		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 20120112 zhangp 保存之后建卡
		// ===zhangp 20120309 modify start
		// if (getValueBoolean("VISIT_CODE_C")) {
		if (saveFlg) {
			ektCard();
		}
		// ===物联网 start
		if (Operator.getSpcFlg().equals("Y")) {
			// SYSPatinfoClientTool sysPatinfoClientTool = new
			// SYSPatinfoClientTool(
			// this.getValue("MR_NO").toString());
			// SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client
			// serviceSpcPatInfoServiceImplPortClient = new
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
			// String msg = serviceSpcPatInfoServiceImplPortClient
			// .onSaveSpcPatInfo(syspat);
			// if (!msg.equals("OK")) {
			// System.out.println(msg);
			// }
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
					"action.sys.SYSSPCPatAction", "getPatName", spcParm);
		}
		// ===物联网 end
		this.onClear();
	}

	/**
	 * 预开检查查询 yanj 20131216
	 * */
	public void onPreOrder() {
		String mr_no = this.getValueString("MR_NO");
		if (mr_no == null || mr_no.equals("")) {
			this.messageBox("请输入病案号！");
			return;
		}
		String sql = "SELECT MR_NO,PAT_NAME,SEX_CODE,BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO = '"
				+ mr_no + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() < 0) {
			this.messageBox("查无病人信息");
			return;
		}
		String pat_name = selParm.getData("PAT_NAME", 0).toString();
		String sex_code = selParm.getData("SEX_CODE", 0).toString();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String birthDate = selParm.getData("BIRTH_DATE", 0).toString()
				.substring(0, 19).replace("-", "/");
		Timestamp birth_date = new Timestamp(Date.parse(birthDate));
		Timestamp temp = birth_date == null ? sysDate : birth_date;

		// 计算年龄
		String age = "0";
		if (birth_date != null)
			age = OdiUtil.getInstance().showAge(temp, sysDate);
		else
			age = "";
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		parm.setData("PAT_NAME", pat_name);
		parm.setData("SEX_CODE", sex_code);
		parm.setData("AGE", age);
		Object obj = openDialog("%ROOT%\\config\\opd\\OPDPatPreOrderChoose.x",
				parm);
		TParm patParm = new TParm();
		if (obj != null) {
			patParm = (TParm) obj;
			this.preFlg = "Y";
			this.preCaseNo = patParm.getValue("CASE_NO");
			this.preAdmDate = patParm.getValue("PRE_DATE").toString();
			// reg.setCaseNo(case_no);
			return;
		}
	}

	/**
	 * 查询病患信息
	 * 
	 * @throws ParseException
	 */
	public void onQueryNO() {
		onClearRefresh();
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		insFlg = false; // 初始化
		insType = null;// 初始化
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		// add by huangtt 20140410 start
		crmPat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));

		// add by huangtt 20140410 end

		// add by huangtt 20140211
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}

		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName().trim());
		setValue("PAT_NAME1", pat.getName1());
		setValue("FIRST_NAME", pat.getFirstName());
		setValue("LAST_NAME", pat.getLastName());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("ID_TYPE", pat.getIdType()); // add by huangtt 20131106
		setValue("REMARKS", pat.getRemarks()); // add by huangtt 20131106
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress()); // add by huangtt
		// 20131106
		// setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		// onPast();
		setValue("SEX_CODE", pat.getSexCode());
		setValue("CELL_PHONE", pat.getCellPhone());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("RESID_ADDRESS", pat.getResidAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		regCtz = getValueString("CTZ1_CODE");
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		setValue("NATION_CODE", pat.getNationCode());
		setValue("SPECIES_CODE", pat.getSpeciesCode());
		setValue("MARRIAGE_CODE", pat.getMarriageCode());

		// add by huangtt 20140114 start
		TParm memParm = new TParm();
		memParm.setData("MR_NO", getValue("MR_NO"));
		TParm memInfo = MEMTool.getInstance().selectMemPatInfo(memParm);
		mem = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
		memTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
		if (mem.getCount() > 0) {
			setValue("GUARDIAN_NAME", mem.getValue("GUARDIAN1_NAME", 0));
			setValue("GUARDIAN_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
			setValue("CUSTOMER_SOURCE", mem.getValue("CUSTOMER_SOURCE", 0));
		}

		if (memInfo.getCount() > 0) {
			memCode = memInfo.getValue("MEM_CODE", 0);
		} else {
			memCode = "";
		}
		// add by huangtt 20140114 end

//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
//		// 锁病患信息
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
//			// this.messageBox_("加锁成功!");//测试专用
			selPatInfoTable();
		// =======20120216 zhangp modify start
		String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
				+ pat.getMrNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
		}
		if (result.getCount() < 0) { // 如果未查出数据（未制卡）则制卡
			if (messageBox("提示", "该病患未办理医疗卡,是否办理医疗卡", 0) == 0) {
				ektCard(); // 制卡
				// ====zhangp 20120227 modify start
				this.onClear();
			}
		}
		// =======20120216 zhangp modify end
		this.grabFocus("CLINICROOM_NO");

		// add by huangtt 20140401 CRM-------start
		
		
		
		if (crmFlg) {
			if (admType.equals("O")) {
				TParm parmCRM = new TParm();
				parmCRM.setData("MR_NO", this.getValueString("MR_NO"));
				TParm order = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "orderInfo", parmCRM);
//				 System.out.println("CRM预约信息==="+order);
				Timestamp date = SystemTool.getInstance().getDate();
				String today = date.toString().replace("-", "")
						.replace("/", "").substring(0, 8);
				TParm orderToday = new TParm();// 今天的预约信息
				TParm orderTomorrow = new TParm(); // 除今天之外的预约信息
				String startTime = StringTool.getString(TypeTool
						.getTimestamp(getValue("YY_START_DATE")), "yyyy/MM/dd");
				String endTime = StringTool.getString(TypeTool
						.getTimestamp(getValue("YY_END_DATE")), "yyyy/MM/dd");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				for (int i = 0; i < order.getCount(); i++) {
					String admDate1 = order.getValue("ADM_DATE", i).replace(
							"-", "").replace("/", "").substring(0, 8);
					String admDate2 = order.getValue("ADM_DATE", i).replace(
							"-", "/").substring(0, 10);
					if (admDate1.equals(today)) {
						orderToday.addData("CLINICTYPE_CODE", order.getValue("CLINICTYPE_CODE", i));
						orderToday.addData("CLINICTYPE_DESC", order.getValue("CLINICTYPE_DESC", i));
						orderToday.addData("DEPT_CODE", order.getValue("DEPT_CODE", i));
						orderToday.addData("DEPT_DESC", order.getValue("DEPT_DESC", i));
						orderToday.addData("DR_CODE", order.getValue("DR_CODE",i));
						orderToday.addData("DR_DESC", order.getValue("DR_DESC",i));
						orderToday.addData("ADM_DATE", order.getValue("ADM_DATE", i));
						orderToday.addData("START_TIME", order.getValue("START_TIME", i));
						// add by wangbin 2015/1/8 增加CRMID
						orderToday.addData("CRM_ID", order.getValue("CRM_ID", i));
					} else {
						try {
							if (sdf.parse(startTime).before(sdf.parse(admDate2))
									&& sdf.parse(admDate2).before(sdf.parse(endTime))) {
								orderTomorrow.addData("CLINICTYPE_CODE", order.getValue("CLINICTYPE_CODE", i));
								orderTomorrow.addData("REALDEPT_CODE", order.getValue("DEPT_CODE", i));
								orderTomorrow.addData("REALDR_CODE", order.getValue("DR_CODE", i));
								orderTomorrow.addData("ADM_DATE", order.getValue("ADM_DATE", i).replace("-","/"));
								orderTomorrow.addData("CRM_FLG", "Y");
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				orderToday.setCount(orderToday.getCount("DEPT_CODE"));
				orderTomorrow.setCount(orderTomorrow.getCount("REALDEPT_CODE"));
				if (orderTomorrow.getCount() > 0) {
					TTable table3 = (TTable) callFunction("UI|table3|getThis");
					TParm parm = table3.getParmValue();
					for (int i = 0; i < orderTomorrow.getCount(); i++) {
						parm.addData("CASE_NO", "");
						parm.addData("ADM_DATE", orderTomorrow.getValue("ADM_DATE", i));
						parm.addData("SESSION_CODE", "");
						parm.addData("REALDEPT_CODE", orderTomorrow.getValue("REALDEPT_CODE", i));
						parm.addData("REALDR_CODE", orderTomorrow.getValue("REALDR_CODE", i));
						parm.addData("ADM_STATUS", "");
						parm.addData("ARRIVE_FLG", "");
						parm.addData("IS_PRE_ORDER", "");
						parm.addData("CRM_FLG", orderTomorrow.getValue("CRM_FLG", i));
						parm.addData("CONFIRM_NO", "");
						parm.addData("INS_PAT_TYPE", "");
						parm.addData("REGMETHOD_CODE", "");
						parm.addData("OLD_CASE_NO", "");
						parm.addData("TRANHOSP_CODE", "");
						parm.addData("DRG_CODE", "");
						parm.addData("CONTRACT_CODE", "");
						parm.addData("REGCAN_USER", "");
						parm.addData("PREVENT_SCH_CODE", "");
						parm.addData("CTZ2_CODE", "");
						parm.addData("HEIGHT", 0.0);
						parm.addData("REPORT_STATUS", "");
						parm.addData("CLINICROOM_NO", "");
						parm.addData("CTZ3_CODE", "");
						parm.addData("DR_CODE", "");
						parm.addData("REG_DATE", "");
						parm.addData("ADM_REGION", "");
						parm.addData("WEIGHT", "");
						parm.addData("APPT_CODE", "N");
						parm.addData("REG_ADM_TIME", "");
						parm.addData("REQUIREMENT", "");
						parm.addData("ADM_TYPE", "");
						parm.addData("DEPT_CODE", "");
						parm.addData("OPT_TERM", "");
						parm.addData("OPT_DATE", "");
						parm.addData("CLINICAREA_CODE", "");
						parm.addData("QUE_NO", "");
						parm.addData("MR_NO", "");
						parm.addData("TRIAGE_NO", "");
						parm.addData("OPT_USER", "");
						parm.addData("CLINICTYPE_CODE", orderTomorrow.getValue("CLINICTYPE_CODE", i));
						parm.addData("VIP_FLG", "");
						parm.addData("REGION_CODE", "");
						parm.addData("CTZ1_CODE", "");
						parm.addData("HEAT_FLG", "");
						parm.addData("SERVICE_LEVEL", "");
						parm.addData("VISIT_CODE", "");
						parm.addData("REGCAN_DATE", "");
						parm.addData("REASSURE_FLG", "");
					}
					table3.setParmValue(parm);
				}
				if (orderToday.getCount() > 0) {
					//add by huangtt 20141229 start CRM预约挂号之后，排除不让在显示出来
					TTable table3 = (TTable) callFunction("UI|table3|getThis");
					TParm parm3 = table3.getParmValue();
					for (int j = 0; j < orderToday.getCount(); j++) {
						String admDate = orderToday.getValue("ADM_DATE", j).replace("-", "").replace("/", "");
						String deptCode = orderToday.getValue("DEPT_CODE", j);
						String drCode = orderToday.getValue("DR_CODE", j);
						String clinictypeCode = orderToday.getValue("CLINICTYPE_CODE", j);
						String orderTime = orderToday.getValue("START_TIME", j).replace(":", "").substring(0, 4);
						for (int j2 = 0; j2 < parm3.getCount("ADM_DATE") ; j2++) {
							if(admDate.equals(parm3.getValue("ADM_DATE", j2).replace("-", "").replace("/", "").subSequence(0, 8)) &&
									deptCode.equals(parm3.getValue("REALDEPT_CODE", j2)) && 
										drCode.equals(parm3.getValue("REALDR_CODE", j2)) && 
											clinictypeCode.equals(parm3.getValue("CLINICTYPE_CODE", j2)) && 
												orderTime.equals(parm3.getValue("REG_ADM_TIME", j2)) && 
													!parm3.getBoolean("APPT_CODE", j2)){
								orderToday.removeRow(j);
							}
						}
					}

					if(orderToday.getCount("ADM_DATE") <= 0){
						return;
					}
					//add by huangtt 20141229 end
					
					TParm orderParm = (TParm) this.openDialog(
							"%ROOT%\\config\\reg\\REGOrderInfo.x", orderToday,
							false);
					// System.out.println("返回的预约信息：==="+orderParm);
					if (orderParm != null) {
						TTabbedPane tabbedPane = (TTabbedPane) getComponent("tTabbedPane_0");
//						tabbedPane.setSelectedIndex(1);
						tabbedPane.setSelectedIndex(0);
						this.setValue("DEPT_CODE_SORT", orderParm.getValue("deptCode"));
						this.setValue("VIP_DEPT_CODE", orderParm.getValue("deptCode"));
						this.setValue("DR_CODE_SORT", orderParm.getValue("deCode"));
						this.setValue("VIP_DR_CODE", orderParm.getValue("deCode"));
						onQueryVipDrTable();
//						TTable table = (TTable) this.getComponent("Table2");
//						table.acceptText();						
//						TParm tableParm = table.getParmValue();
						
						TTable table1 = (TTable) this.getComponent("Table1");
						table1.acceptText();
						TParm table1Parm = table1.getParmValue();
						crmTime = orderParm.getValue("time");
						
						for (int j = 0; j < parm3.getCount("CASE_NO"); j++) {
							if (parm3.getValue("ADM_DATE", j).length() > 0) {
								String admDate1 = parm3.getValue("ADM_DATE", j).replace("-","").replace("/", "").substring(0, 8);
								String admDate2 = orderParm.getValue("admDate").replace("-", "").replace("/", "").substring(0, 8);
								if (admDate1.equals(admDate2)
										&& parm3.getValue("CLINICTYPE_CODE", j).equals(orderParm.getValue("clinictypeCode"))
										&& parm3.getValue("REALDEPT_CODE", j).equals(orderParm.getValue("deptCode"))
										&& parm3.getValue("REALDR_CODE",j).equals(orderParm.getValue("deCode"))
										&& parm3.getValue("REG_ADM_TIME", j).equals(crmTime)
										&& parm3.getBoolean("APPT_CODE", j)){
									table3.setSelectedRow(j);
									onTable3Clicked();												
									return;
								}
							}
						}
						
						
						for (int i = 0; i < table1Parm.getCount("DEPT_CODE"); i++) {
//							if (tableParm.getValue("START_TIME", i).equals(time) 
//									&& tableParm.getValue("CLINICTYPE_CODE", i).equals(orderParm.getValue("clinictypeCode"))) {
//								table.setSelectedRow(i);
							if (table1Parm.getValue("DEPT_CODE", i).equals(orderParm.getValue("deptCode")) 
									&& table1Parm.getValue("CLINICTYPE_CODE", i).equals(orderParm.getValue("clinictypeCode"))
									&& table1Parm.getValue("DR_CODE", i).equals(orderParm.getValue("deCode"))) {
								table1.setSelectedRow(i);
								
								// add by wangbin 2015/1/8
								// 取得CRM预约号
								this.crmId = orderParm.getValue("crmId");								
								onTable1Clicked(i);
								
								return;
							}
						}
						this.messageBox("该病人的看诊时间为" + crmTime.substring(0, 2)+":"+crmTime.substring(2, 4) + ",该时间不在看诊时间内！");
						
						
					}
				}
			}

		}

		// add by huangtt 20140401 CRM-------end

		// ===zhangp 20120413 start
		// 初始化下一票号
		// ===huangtt 20131212
		// if(ticketFlg){
		// BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// endInvNo = invoice.getEndInvno();
		// if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
		// Operator.getRegion(), invoice.getUpdateNo())) {
		// setValue("NEXT_NO", invoice.getUpdateNo());
		// } else {
		// messageBox("票据已用完");
		// }
		// }

		// ===zhangp 20120413 end
		// yanjing 当急诊时刷新日版表
		// if (admType.equals("E")) {
		// initSchDay();
		// }
		TButton btton=(TButton) this.getComponent("SAVE_PAT");
		btton.setEnabled(false);
		callFunction("UI|INSURE_INFO|setEnabled", true);//add by huangjw 20150731
		callFunction("UI|PAT_PACKAGE|setEnabled", true);//add by huangjw 20150731
		// 
		this.checkInsures();
		this.checkCtz1Code();
	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 * @throws ParseException
	 */
	public void onQueryNO(String mrNo) {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("无此病案号!");
			this.setValue("MR_NO", "");
			return;
		}
		// add by huangtt 20140211
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}

		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		// setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("CELL_PHONE", pat.getCellPhone());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("RESID_ADDRESS", pat.getResidAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		regCtz = getValueString("CTZ1_CODE");
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// add by huangtt 20131106 start
		setValue("ID_TYPE", pat.getIdType());
		setValue("REMARKS", pat.getRemarks());
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
		// add by huangtt 20131106 end
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		setValue("NATION_CODE", pat.getNationCode());
		setValue("SPECIES_CODE", pat.getSpeciesCode());
		setValue("MARRIAGE_CODE", pat.getMarriageCode());

		// add by huangtt 20140114 start
		mem = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
		memTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
		TParm memParm = new TParm();
		memParm.setData("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		TParm memInfo = MEMTool.getInstance().selectMemPatInfo(memParm);
		if (mem.getCount() > 0) {
			setValue("GUARDIAN_NAME", mem.getValue("GUARDIAN1_NAME", 0));
			setValue("GUARDIAN_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
			setValue("CUSTOMER_SOURCE", mem.getValue("CUSTOMER_SOURCE", 0));
		}
		if (memInfo.getCount() > 0) {
			memCode = memInfo.getValue("MEM_CODE", 0);
			// regCtz = getValueString("CTZ1_CODE");
			// this.setValue("REG_CTZ1", getValue("CTZ1_CODE"));
			// this.setValue("REG_CTZ2", getValue("CTZ2_CODE"));

		} else {
			memCode = "";
		}
		// if(this.getValueString("REG_CTZ1").equals("")){
		// setValue("REG_CTZ1", zfCtz);
		// regCtz=zfCtz;
		// }
		// if(this.getValueString("REG_CTZ2").equals("")){
		// setValue("REG_CTZ2", "");
		// }
		// add by huangtt 20140114 end
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
//		// 锁病患信息
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
//			// this.messageBox_("加锁成功!");//测试专用
			selPatInfoTable();
		this.grabFocus("CLINICROOM_NO");
	}

	/**
	 * 应收金额获得焦点
	 * 
	 * @param e
	 *            FocusEvent
	 */
	public void onFocusLostAction(FocusEvent e) {
		onFee();

	}

	/**
	 * 保存REG对象
	 * 
	 * @throws ParseException
	 */
	public void onSaveReg() {
		// =====yanj 20130502 添加时间校验
		if (admType.equals("E")) {
			String admNowTime1 = StringTool.getString(SystemTool.getInstance()
					.getDate(), "HH:mm:ss");// 系统当前时间
			String sessionCode = (String) this.getValue("SESSION_CODE");
			String startTime = SessionTool.getInstance().getStartTime(admType,
					sessionCode);
			String endTime = SessionTool.getInstance().getEndTime(admType,
					sessionCode);
			if (startTime.compareTo(endTime) < 0) {
				if (!(admNowTime1.compareTo(startTime) > 0 && (admNowTime1
						.compareTo(endTime) < 0))) {
					this.messageBox("请刷新界面！");
					return;
				}
			} else {
				if (admNowTime1.compareTo(startTime) < 0
						&& admNowTime1.compareTo(endTime) > 0) {
					this.messageBox("请刷新界面！");
					return;
				}
			}
		}
		
		
		// ====zhangp 20120724 start
		if (!this.emptyTextCheck("CTZ1_CODE")) {
			messageBox("请选择身份");
			return;
		}
		// ====zhangp 20120724 end

		// System.out.println("保存REG对象");
		DecimalFormat df = new DecimalFormat("##########0.00");
		// 现场挂号
		if (this.getValue("REGMETHOD_CODE").equals("A") && aheadFlg) {
			// 输入金额校验
			if (TypeTool.getDouble(df.format(getValue("FeeS"))) < TypeTool
					.getDouble(df.format(getValue("FeeY")))) {
				this.messageBox("金额不足");
				return;
			}

		}
		// 不能输入空值
		// if (!this.emptyTextCheck("DEPT_CODE,CLINICTYPE_CODE,PAY_WAY"))
		// return;
		if (this.getValue("DEPT_CODE") == null
				|| this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox("科室不能为空");
			return;
		}
		if (this.getValue("CLINICTYPE_CODE") == null
				|| this.getValueString("CLINICTYPE_CODE").length() == 0) {
			this.messageBox("号别不能为空");
			return;
		}
		//delete by huangtt 20150310
//		if (admType.endsWith("E") && triageFlg.equals("Y")) {
//			if (!this.emptyTextCheck("ERD_LEVEL"))
//				return;
//		}

		// add by huangtt 20140303 start
		if (!this.getValueString("MR_NO").equals("")) {
			double clinicFee = this.getValueDouble("FeeY");
			double currentBalance = EKTpreDebtTool.getInstance().getEkeMaster(
					this.getValueString("MR_NO"));
			String sqlCtz = "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"
					+ this.getValueString("REG_CTZ1") + "'";
			TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
			double overdraft = 0;
			if (parmCtz.getCount() > 0) {
				overdraft = parmCtz.getDouble("OVERDRAFT", 0);
			}
			if (currentBalance + overdraft < clinicFee) {
				this.messageBox("医疗卡余额小于诊查费");
			}
			
			//add by huangtt 20150225 start 
			TParm memPatParm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(this.getValueString("MR_NO"))));
			if(memPatParm.getCount() > 0){
				String endDate = memPatParm.getValue("END_DATE", 0);
				if(endDate.length() > 0){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							Locale.CHINA);
					endDate = endDate.replace("-", "/").substring(0, 10);
					String today = SystemTool.getInstance().getDate().toString()
							.replace("-", "/").substring(0, 10);
					try {
						if (sdf.parse(endDate).before(sdf.parse(today))) {
							String startDate = memPatParm.getValue("START_DATE", 0);	
							this.messageBox(memPatParm.getValue("MEM_DESC", 0)
									+ "办理日期" + startDate.substring(0, 4) + "年"
									+ startDate.substring(5, 7) + "月"
									+ startDate.substring(8, 10) + "日，失效日期"
									+ endDate.substring(0, 4) + "年"
									+ endDate.substring(5, 7) + "月"
									+ endDate.substring(8, 10) + "日");

						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			//add by huangtt 20150225 end
			
		}
		// add by huangtt 20140303 end
		
		
		reg = new Reg();

		reg.createReceipt();
		// reg.getRegReceipt().createBilInvoice();
		// if(ticketFlg){ //=============== huangtt 20131212
		// if (RegMethodTool.getInstance().selPrintFlg(
		// this.getValueString("REGMETHOD_CODE"))) {
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null) {
		// this.messageBox("尚未开账");
		// return;
		// }
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo().compareTo(
		// reg.getRegReceipt().getBilInvoice().getEndInvno()) > 0) {
		// this.messageBox("票据已用完!");
		// return;
		// }
		// }
		// }

		// 病患
		if (pat == null) {
			this.messageBox("无病患信息");
			return;
		}
		//add by huangtt 20140710
		if(pat.isDeptFlg()){
			if (JOptionPane.showConfirmDialog(null, "该患者有保险欠费，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) == 1){
				return;
			}
		}
		
		if(pat.isOpbArreagrage()){
			if (JOptionPane.showConfirmDialog(null, "有未结算项目，是否继续？", "信息",
					JOptionPane.YES_NO_OPTION) == 1){
				this.openDialog("%ROOT%\\config\\bil\\BILPatOweQuery.x",pat.getMrNo());//弹出欠费患者查询界面add by huangjw 20150129
				return;
			}
			
		}
		// 判断是否为黑名单病患
		if (pat.getBlackFlg())
			this.messageBox("请注意,此为黑名单病患!");
		pat.setNhiNo(this.getValueString("NHI_NO"));
		// System.out.println("pat::" + pat.getNhiNo());
		reg.setPat(pat);
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("病案号不能为空");
			return;
		}
		// 挂号主表,REG对象
		// 2门急别
		if (!onSaveRegParm(true))
			return;
		// 医保医疗操作 共用部分
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别

		reg.setTredeNo(tredeNo);
		String regmethodCode = this.getValueString("REGMETHOD_CODE"); // 挂号方式
		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ regmethodCode + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}

		// ================
		// this.messageBox("preFlg is ::"+preFlg);
		if (this.preFlg.equals("Y")) {// yanjing 20131216
			// 根据就诊号删除之前录入的挂号信息
			// this.messageBox("preCaseNo is ::"+preCaseNo);
			reg.setCaseNo(preCaseNo);// 如果是预开检查
			reg.setPreFlg(preFlg);
			String selectOldCaseNo = "SELECT ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,REG_DATE,"
					+ "IS_PRE_ORDER,OLD_CASE_NO FROM REG_PATADM "
					+ "WHERE CASE_NO = '" + preCaseNo + "'";
			// System.out.println("查询selectOldCaseNo is：："+selectOldCaseNo);
			TParm oldCaseNoResult = new TParm(TJDODBTool.getInstance().select(
					selectOldCaseNo));
			String oldCaseNo = "";
			if (oldCaseNoResult.getCount() > 0) {
				oldCaseNo = oldCaseNoResult.getValue("OLD_CASE_NO", 0);
			}
			reg.setOldCaseNo(oldCaseNo);
			String regSql = "DELETE REG_PATADM WHERE CASE_NO = '" + preCaseNo
					+ "'";
			TParm deleteParm = new TParm(TJDODBTool.getInstance()
					.update(regSql));
		} else {
			reg.setPreFlg(preFlg);
		}
		// 先预开再预约（同一天）合并就诊号 yanjing 20130328
		this.mergePreCaseNo();

		// ==================
		if (null != regMethodParm.getValue("PRINT_FLG", 0)
				&& regMethodParm.getValue("PRINT_FLG", 0).equals("Y")) {
			// 打票
			reg.setApptCode("N");
//			reg.setRegAdmTime("");  //delete by huangtt 20140910
		} else if (null == regMethodParm.getValue("PRINT_FLG", 0)
				|| regMethodParm.getValue("PRINT_FLG", 0).length() <= 0
				|| regMethodParm.getValue("PRINT_FLG", 0).equals("N")) {
			// 不打票操作
			reg.setApptCode("Y");
			// 12预约时间
			reg.setRegAdmTime(startTime);
		}
		// 获得第一个页签数据
		if (tableFlg) {
//			System.out.println("2222222222222");
			// 判断是否VIP就诊
			TTable table1 = (TTable) this.getComponent("Table1");
			TParm parm = table1.getParmValue();
			TParm temp = parm.getRow(selectRow); // 获得第一个页签数据
			String type = temp.getValue("TYPE"); // VIP 和一般
			if (type.equals("VIP")) {
				// UPDATE REG_CLINICQUE &
				temp.setData("ADM_TYPE", admType); // 挂号类型
				temp.setData("SESSION_CODE", reg.getSessionCode()); // 时段
				temp.setData("ADM_DATE", StringTool.getString(
						(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
				temp.setData("START_TIME", StringTool.getString(SystemTool
						.getInstance().getDate(), "HHmm"));// 系统当前时间
				String admNowDate = StringTool.getString(SystemTool
						.getInstance().getDate(), "yyyyMMdd");// 系统当前日期
				// String startTime = "";
				if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
					this.messageBox("已经过诊不可以挂号");
					callFunction("UI|table2|clearSelection");
					return;
				}
				// // 获得vip就诊号  modigy by huangtt 20140926
				if(!queryQueNo(temp)){
					return; 
				}
				
				if(crmTime.length() > 0){
					reg.setRegAdmTime(crmTime);
				}	
				// ===zhangp 20120629 end
				reg.setVipFlg(true); // vip就诊
				TParm regParm = reg.getParm();
				String admDate = StringTool.getString(reg.getAdmDate(),
						"yyyyMMdd");
				regParm.setData("ADM_DATE", admDate);
				// =========pangben 2012-7-1 start重号问题
				if (!onSaveQueNo(regParm)) {
					messageBox("取得就诊号失败");
					return;
				}
				// =========pangben 2012-7-1 stop
				if ("N".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(true); // 报到
				} else if ("Y".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(false); // 不报到
				}
//				startTime = temp.getValue("START_TIME", 0);
//				reg.setRegAdmTime(startTime);
			} else if (getValueString("REGMETHOD_CODE").equals("D")) {
				messageBox("请挂VIP诊");
				return;
			}
		}
		// =====zhangp 20120301 modify start
//		if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
		if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
			if (!onInsEkt(payWay, null)) {
				// ===========pangben 2012-7-1 操作失败回滚VIP就诊号码
				TParm regParm = reg.getParm();
				if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
					this.messageBox("撤销VIP就诊号码失败,请联系信息中心");
				}
				return;
			}
		}
		// =====zhangp 20120301 modify end

		if (!onSaveRegOne(payWay, ins_exe)) {
			// ===========pangben 2012-7-1 操作失败回滚VIP就诊号码
			TParm regParm = reg.getParm();
			if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
				this.messageBox("撤销VIP就诊号码失败,请联系信息中心");
			}
			return;
		}
		if (ins_exe) { // 医保卡操作执行成功 ,删除在途状态数据 修改票据号
			if (!updateINSPrintNo(reg.caseNo(), "REG"))
				return;
		}
		// 打票数据
		TParm result = onPrintParm();
		if ("Y".endsWith(reg.getApptCode())) {
			if (this.preFlg.equals("Y")) {// yanjing 预开检查，直接报到，20131230
				if (!onArrivePre())
					return;
			} else {
				this.messageBox("预约成功!");

			}
			// 调用排队叫号
			/**
			 * if (!"true".equals(callNo("REG", ""))) { this.messageBox("叫号失败");
			 * }
			 **/
			this.onClear();
			return;
		}
		// ================pangben modify 20110817 记账单位不存在执行打票
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			// 判断当诊病患打票
			if ("N".endsWith(reg.getApptCode())) {
				// 医疗卡打票
				if (ticketFlg) {
					onPrint(result);
				}
				// ====huangtt 20131212
				// BilInvoice invoice = new BilInvoice();
				// invoice = invoice.initBilInvoice("REG");
				// // 初始化下一票号
				// // ===zhangp 20120306 modify start
				// if(ticketFlg){
				// if (BILTool.getInstance().compareUpdateNo("REG",
				// Operator.getID(), Operator.getRegion(),
				// invoice.getUpdateNo())) {
				// setValue("NEXT_NO", invoice.getUpdateNo());
				// } else {
				// messageBox("票据已用完");
				// clearValue("NEXT_NO");
				// }
				// }

				// ===zhangp 20120306 modify end
				// 调用排队叫号
				if (!"true".equals(callNo("REG", reg.caseNo()))) {
					this.messageBox("叫号失败");
				}

			}
			// 不打票执行记账操作
		} else {
			// =================pangben 20110817
			TParm parm = new TParm();
			parm.setData("RECEIPT_NO", reg.getRegReceipt().getReceiptNo()); // 收据号
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")); // 记账单位
			parm.setData("ADM_TYPE", reg.getRegReceipt().getAdmType()); // 门急住别
			parm.setData("REGION_CODE", Operator.getRegion()); // 院区
			parm.setData("CASHIER_CODE", Operator.getID()); // 收费人员
			parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // 收费日期时间
			parm.setData("RECEIPT_TYPE", "REG"); // 票据类型：REG 、OPB
			parm.setData("DATA_TYPE", "REG"); // 扣款来源 REG OPB HRM
			parm.setData("CASE_NO", reg.caseNo()); // 就诊号
			parm.setData("MR_NO", reg.getPat().getMrNo());
			parm.setData("AR_AMT", reg.getRegReceipt().getArAmt()); // 应缴金额
			parm.setData("BIL_STATUS", "1"); // 记账状态1 记账 2 结算完成写入 =1
			// caowl 20130307 start
			// String sqls = "SELECT * FROM BIL_CONTRACTD WHERE MR_NO = '"
			// + reg.getPat().getMrNo() + "' AND CONTRACT_CODE = '"
			// + this.getValue("CONTRACT_CODE") + "'";
			// // System.out.println("卡的条件" + sqls);
			// TParm parms = new TParm(TJDODBTool.getInstance().select(sqls));
			// if (parms.getCount() <= 0) {
			// this.messageBox("此病人不属于该合同单位，请确认！");
			// return;
			// }
			// caowl 20130307 end
			// 记账单位缴费时候
			// update =2
			parm.setData("RECEIPT_FLG", "1"); // 状态：1 收费 2 退费
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result1 = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "insertRecode", parm);
			if (result1.getErrCode() < 0) {
				err(result1.getErrCode() + " " + result1.getErrText());
				this.messageBox("挂号失败");
			} else
				this.messageBox("挂号成功,已经记账");
		}
		
		// add by wangbin 20140812 挂号时插入病历册主档表 START
		this.insertMroMrvData();
        // add by wangbin 20140812 挂号时插入病历册主档表 END
		
		// add by wangbin 20140805 门诊非CRM预约挂号数据存入临时表 START
		this.insertMroRegData();
		// add by wangbin 20140805 门诊非CRM预约挂号数据存入临时表 END
		
		// add by wangbin 20140915 挂号后向病案室发送消息 START
		this.onSendMROMessages();
		// add by wangbin 20140915 挂号后向病案室发送消息 END
		
		if (singleFlg) {
			onPrintReg(reg.caseNo(), ""); // add by huangtt 20140331
		}
		
		if (crmFlg && "O".equals(reg.getAdmType())) {
			// System.out.println("时间="+reg.getAdmDate());
			// System.out.println("门急诊="+reg.getAdmType());
			// System.out.println("医生＝＝"+reg.getDrCode());
			// System.out.println("科室＝＝"+reg.getDeptCode());
			// System.out.println("诊间＝＝"+reg.getClinicroomNo());
			// System.out.println("时段＝＝"+reg.getSessionCode());
//			String sqlQueNo = "SELECT QUE_NO-1 QUE_NO FROM REG_SCHDAY"
//					+ " WHERE VIP_FLG = 'Y' "
//					+ " AND DEPT_CODE = '"
//					+ reg.getDeptCode()
//					+ "'"
//					+ " AND ADM_TYPE = '"
//					+ reg.getAdmType()
//					+ "'"
//					+ " AND ADM_DATE = '"
//					+ reg.getAdmDate().toString().replace("/", "").replace("-",
//							"").substring(0, 8) + "'"
//					+ " AND CLINICROOM_NO = '" + reg.getClinicroomNo() + "'";
//					if(reg.getDrCode().trim().length() == 0){
//						sqlQueNo = sqlQueNo +  " AND DR_CODE IS NULL ";
//					}else{
//						sqlQueNo = sqlQueNo + " AND DR_CODE = '" + reg.getDrCode() + "'";
//					}
//
//			sqlQueNo = sqlQueNo+ " AND CLINICTYPE_CODE='" + clinictypeCode + "'";
			String sqlQueNo = "SELECT COUNT(QUE_NO) QUE_NO FROM REG_CLINICQUE " +
					" WHERE ADM_DATE = '"+ reg.getAdmDate().toString().replace("/", "").replace("-",
							"").substring(0, 8)+"'" +
					" AND QUE_STATUS='Y'" +
					" AND CLINICROOM_NO = '" + reg.getClinicroomNo() + "' " +
					"AND ADM_TYPE = 'O'";
//			System.out.println("sqlQueNo="+sqlQueNo);
			TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
					sqlQueNo));
			if(parmQueNo.getCount()>0){
				int queNo=parmQueNo.getInt("QUE_NO", 0);
//				for (int i = 0; i < parmQueNo.getCount(); i++) {
//					queNo += parmQueNo.getInt("QUE_NO", i);
//				}
				TParm orderCount = new TParm();
				orderCount.setData("date", reg.getAdmDate().toString().replace("/",
						"").replace("-", "").substring(0, 8));
				orderCount.setData("deptCode", reg.getDeptCode());
				orderCount.setData("drCode", reg.getDrCode());
				orderCount.setData("quegroup", clinictypeCode);   //add by huangtt 20141106        
				orderCount.setData("count", queNo);
				System.out.println("orderCount=="+orderCount);
				result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
						"orderCount", orderCount);
				if (!result.getBoolean("flg")) {
					this.messageBox("传输挂号总数失败！");
//					return;
				}
			}
			

		}
	//if(this.getValue("INSURE_INFO")!=null && !this.getValue("INSURE_INFO").equals("")){//挂号绑定保险信息
		TParm insureInfo = new TParm(TJDODBTool.getInstance().update("UPDATE REG_PATADM SET INSURE_INFO='"+this.getValue("INSURE_INFO")+"', " +
				" PAT_PACKAGE = '"+this.getValue("PAT_PACKAGE")+"' WHERE CASE_NO='"+reg.caseNo()+"'"));
	//}
		//		// 解锁病患 信息
//		if (PatTool.getInstance().unLockPat(pat.getMrNo()))
		this.onClear();
		initSession();
		pat = null;
		// 清空CRM预约FLG
		crmRegFlg = false;
	}
	
	/**
	 * 打印到诊单
	 */
	@SuppressWarnings("deprecation")
	public void onPrintReg(String caseNo, String copy) {
		// String sql = "SELECT MAX_QUE," +
		// "MAX_QUE-(SELECT MAX_QUE FROM REG_QUEGROUP WHERE QUEGROUP_CODE = REG_SCHDAY.QUEGROUP_CODE) AS ADD_COUNT "
		// +
		// " FROM REG_SCHDAY " +
		// " WHERE REGION_CODE = '"+reg.getRegion()+"'" +
		// " AND ADM_TYPE = '"+reg.getAdmType()+"'" +
		// " AND ADM_DATE = '"+reg.getAdmDate().toString().replace("-",
		// "").replace("/", "").substring(0, 8)+"'" +
		// " AND SESSION_CODE = '"+reg.getSessionCode()+"'" +
		// " AND REALDEPT_CODE = '"+reg.getRealdeptCode()+"'" +
		// " AND REALDR_CODE = '"+reg.getRealdrCode()+"'" +
		// " AND CLINICROOM_NO='"+reg.getClinicroomNo()+"'" ;
		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// int maxQue = parm.getInt("MAX_QUE", 0);
		// int addCount = parm.getInt("ADD_COUNT", 0);
		String sql = "SELECT * FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm regParm = new TParm(TJDODBTool.getInstance().select(sql));
//		int que = regParm.getInt("QUE_NO", 0);
		// if(que>(maxQue-addCount)){
		// add = " （加号）";
		// }
//		String sql1 = "SELECT B.CHN_DESC NAME"
//				+ " FROM REG_CLINICROOM A, SYS_DICTIONARY B"
//				+ " WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'"
//				+ "  AND A.CLINICROOM_NO='"
//				// + this.getValueString("CLINICROOM_NO") + "'";
//				+ regParm.getValue("CLINICROOM_NO", 0) + "'";
//		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		String dept = "";
//		if (!parm1.getValue("NAME", 0).equals("")) {
//			dept = "(" + parm1.getValue("NAME", 0) + ")";
//		}
		TParm data = new TParm();
		data.setData("REQUIREMENT", "TEXT", regParm.getValue("REQUIREMENT", 0));
		data.setData("COPY", "TEXT", copy);
		data.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("HOSP_EN", "TEXT", Operator.getHospitalENGFullName());
		data.setData("TITLE", "TEXT", "到诊单");
		data.setData("MR_NO", this.getValueString("MR_NO"));
		data.setData("BAR_CODE", "TEXT", this.getValueString("MR_NO"));
		data.setData("NAME", this.getValueString("PAT_NAME"));
		data.setData("REG_CTZ", this.getText("REG_CTZ1"));
		data.setData("DEPT", this.getText("DEPT_CODE"));// ==liling
//		TTextFormat sex = (TTextFormat) this.getComponent("SEX_CODE");
		data.setData("SEX", this.getText("SEX_CODE"));
		String birthday = pat.getBirthday().toString().replace("-", "")
				.replace("/", "").substring(0, 8);

		Timestamp birthDay = (Timestamp) pat.getBirthday();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = birthDay == null ? sysDate : birthDay;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		
		data.setData("BIRTH", birthday.substring(0, 4) + "年"
				+ birthday.substring(4, 6) + "月" + birthday.substring(6, 8)
				+ "日/"+age);// ==liling

		data.setData("DR", this.getText("DR_CODE"));
		DecimalFormat df = new DecimalFormat("0.00");
		data.setData("FEE", df.format(Math.abs(this.getValueDouble("FeeY"))));// ====liling
		
		sql = "SELECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='"+regParm.getValue("CLINICAREA_CODE", 0)+"'";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sql));
		
		data.setData("CLINICROOM_NO", parmC.getValue("CLINIC_DESC", 0));
		data.setData("TIME", regParm.getValue("REG_DATE", 0).replace("-", "/")
				.substring(0, regParm.getValue("REG_DATE", 0).length() - 5));
		data.setData("OPERATOR", Operator.getID());
		String admDate = this.getValueString("ADM_DATE").replace("-", "/")
				.substring(0, 10);
//		if (regParm.getBoolean("VIP_FLG", 0)
//				&& !"".equals(regParm.getValue("REG_ADM_TIME", 0))  ) {
//			String vipSql = "SELECT START_TIME FROM REG_CLINICQUE "
//					+ "WHERE ADM_TYPE='"
//					+ regParm.getValue("ADM_TYPE", 0)
//					+ "' AND ADM_DATE='"
//					+ regParm.getValue("ADM_DATE", 0).replaceAll("-", "")
//							.substring(0, 8) + "'" + " AND SESSION_CODE='"
//					+ regParm.getValue("SESSION_CODE", 0)
//					+ "' AND CLINICROOM_NO='"
//					+ regParm.getValue("CLINICROOM_NO", 0) 
//					+ "' ORDER BY START_TIME";
//			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
//			SimpleDateFormat sdf = new SimpleDateFormat("mmss");
//			String t1 = result.getValue("START_TIME", 0);
//			String t2 = result.getValue("START_TIME", 1);
//			long minute = 0;
//			try {
//				minute = (sdf.parse(t2).getTime() - sdf.parse(t1).getTime()) / 1000;
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			StringBuffer time = new StringBuffer("");
//			time.append(regParm.getValue("REG_ADM_TIME", 0).substring(0, 2));
//			time.append(":");
//			time.append(regParm.getValue("REG_ADM_TIME", 0).substring(2, 4));
//			Calendar cal = Calendar.getInstance();
//			cal.set(Integer.parseInt(admDate.substring(0, 4)), Integer
//					.parseInt(admDate.substring(5, 7)), Integer
//					.parseInt(admDate.substring(8, 10)),
//					Integer.parseInt(regParm.getValue("REG_ADM_TIME", 0)
//							.substring(0, 2)), Integer.parseInt(regParm
//							.getValue("REG_ADM_TIME", 0).substring(2, 4)));
//		
//		
//			cal.add(Calendar.MINUTE, Integer.parseInt(minute+""));
//
//			time.append(" - ");
//			time.append(cal.getTime().getHours());
//			time.append(":");
//			if(cal.getTime().getMinutes() < 10){
//				time.append("0");
//			}
//			time.append(cal.getTime().getMinutes());
//			
//			data.setData("REG_ADM_TIME", admDate + " " + time.toString());
//		} else {
//			data.setData("REG_ADM_TIME", "");
//		}
		data.setData("REG_ADM_TIME", "");
		if(regParm.getValue("REG_ADM_TIME", 0).length()==4){
			data.setData("REG_ADM_TIME", regParm.getValue("REG_ADM_TIME", 0).substring(0, 2)+":"+regParm.getValue("REG_ADM_TIME", 0).substring(2, 4));
		}
		
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REG.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
			this.openPrintDialog(IReportTool.getInstance().getReportPath(
					"REG.jhw"), IReportTool.getInstance().getReportParm("REG.class",data), true);
		}
//		this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REG.jhw", data, true);

	}

	/**
	 * 打票数据
	 * 
	 * @return TParm
	 */
	private TParm onPrintParm() {
		// 门诊打票操作
		TParm result = PatAdmTool.getInstance().getRegPringDate(reg.caseNo(),
				"");
		// zhangp 20120206
		result.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		result.setData("PAY_WAY", this.getValue("PAY_WAY")); // 支付方式
		result.setData("INS_SUMAMT", ins_amt);
		result.setData("ACCOUNT_AMT_FORREG", accountamtforreg);// 个人账户
		return result;
	}

	/**
	 * 医保医疗操作 共用部分
	 * 
	 * @param payWay
	 *            String
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	private boolean onInsEkt(String payWay, String caseNo) {

		// 医疗卡支付
		if (payWay.equals("EKT")) {
			// 生成CASE_NO 因为医疗卡需要CASE_NO 所以在用医疗卡支付的时候先生成CASE_NO
			if ("N".endsWith(reg.getApptCode())) {
				// System.out.println("222222222222222222");
				if (null != caseNo && caseNo.length() > 0) {
					reg.setCaseNo(caseNo);
				} else {
					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
							"CASE_NO", "CASE_NO"));
				}
				// 保存医疗卡
				if (!this.onEktSave("Y")) {
					System.out.println("!!!!!!!!!!!医疗卡保存错误");
					return false;
				}
				if (null != greenParm
						&& null != greenParm.getValue("GREEN_FLG")
						&& greenParm.getValue("GREEN_FLG").equals("Y")) {
					// 使用绿色通道金额
					reg.getRegReceipt().setPayMedicalCard(
							TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
					reg.getRegReceipt().setOtherFee1(
							greenParm.getDouble("GREEN_USE"));
				}
			}
		}
		if (payWay.equals("INS")) {
			TParm result = null;
			// 医保卡支付
			result = onSaveRegTwo(payWay, ins_exe, caseNo);
			if (null == result) {
				return false;
			}
			ins_exe = result.getBoolean("INS_EXE");
			ins_amt = result.getDouble("INS_AMT");
			accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
		}

		if (ins_exe) {
			// 执行医保 判断在途状态
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", reg.caseNo());
			runParm.setData("EXE_USER", Operator.getID());
			runParm.setData("EXE_TERM", Operator.getIP());
			runParm.setData("EXE_TYPE", "REG");
			runParm = INSRunTool.getInstance().queryInsRun(runParm);
			if (runParm.getErrCode() < 0) {
				return false;
			}
			if (runParm.getCount("CASE_NO") <= 0) {
				// 没有查询到数据，说明在途状态有问题
				return false;
			} else {
				if (runParm.getInt("STUTS", 0) != 1) { // STUTS :1.在途 2.成功
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 执行保存REG_PATADM BIL_REG_RECP BIL_INVRCP 表操作(医保执行操作)
	 * 
	 * @param payWay
	 *            String
	 * @param ins_exe
	 *            boolean
	 * @return boolean
	 * 
	 */
	private boolean onSaveRegOne(String payWay, boolean ins_exe) {
		TParm result = new TParm();
		if (!reg.onNew()) {
			this.messageBox("挂号失败");
			if (payWay.equals("EKT")) { // 医疗卡支付
				result = new TParm();
				result.setData("CURRENT_BALANCE", ektOldSum);
				result.setData("MR_NO", p3.getValue("MR_NO"));
				result.setData("SEQ", p3.getValue("SEQ"));
				try {
					result = EKTIO.getInstance().TXwriteEKTATM(result,
							reg.getPat().getMrNo());
					// 回写医疗卡金额
					if (result.getErrCode() < 0)
						System.out.println("err:" + result.getErrText());
					// 医疗卡挂号出现问题撤销操作
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("err:医疗卡写卡动作失败");
				}

				cancleEKTData();
			}
			if (payWay.equals("INS")) { // 医保卡支付
				if (!ins_exe) { // 医保卡操作 ,删除在途状态数据
					return false;
				}
				result = new TParm();
				insParm.setData("EXE_TYPE", "REG");
				// 执行撤销操作----需要实现
				if (tjINS) { // 医疗卡操作
					result.setData("CURRENT_BALANCE", ektOldSum);
					result.setData("MR_NO", p3.getValue("MR_NO"));
					result.setData("SEQ", p3.getValue("SEQ"));
					try {
						result = EKTIO.getInstance().TXwriteEKTATM(result,
								p3.getValue("MR_NO"));
						// 回写医疗卡金额
						if (result.getErrCode() < 0)
							System.out.println("err:" + result.getErrText());
						// 医疗卡挂号出现问题撤销操作
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("err:医疗卡写卡动作失败");
						e.printStackTrace();
					}
					cancleEKTData();
				}

			}
			// EKTIO.getInstance().unConsume(tredeNo, this);
			return false;
		}
		return true;
	}

	/**
	 * 执行保存 医保表数据操作
	 * 
	 * @param payWay
	 *            String
	 * @param ins_amt
	 *            double
	 * @param ins_exe
	 *            boolean
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
		double ins_amtTemp = 0.00;// 医保金额
		TParm result = new TParm();
		if (payWay.equals("INS")) {
			// 查询是否存在特批款操作
			if (null == caseNo || caseNo.length() <= 0) {
				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
						"CASE_NO", "CASE_NO"); // 获得就诊号
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0005");
				return null;
			}

			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
				this.messageBox("此就诊病患使用特批款,不可以使用医保操作");
				return null;
			}
			if (this.getValue("REG_CTZ1").toString().length() <= 0) {
				this.messageBox("请选择医保卡就诊类型");
				return null;
			}
			// 需要保存到REG_PATADM数据库表中1.城职普通
			// 2.城职门特 3.城居门特
			// 医保卡挂号
			// 获得挂号费用代码，费用金额，费用
			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
					+ admType
					+ "'"
					+ " AND A.CLINICTYPE_CODE='"
					+ getValue("CLINICTYPE_CODE") + "'";

			// 挂号费
			double reg_fee = BIL.getRegDetialFee(admType, TypeTool
					.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
							.getString(getValue("REG_CTZ2")), TypeTool
							.getString(getValue("CTZ3_CODE")), this
							.getValueString("SERVICE_LEVEL") == null ? ""
							: this.getValueString("SERVICE_LEVEL"));
			// 诊查费 计算折扣
			double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
					.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
							.getString(getValue("REG_CTZ2")), TypeTool
							.getString(getValue("CTZ3_CODE")), this
							.getValueString("SERVICE_LEVEL") == null ? ""
							: this.getValueString("SERVICE_LEVEL"));

			// System.out.println("regFeesql:::::" + regFeesql);
			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
					regFeesql));
			if (regFeeParm.getErrCode() < 0) {
				err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
				this.messageBox("医保执行操作失败");
				return null;
			}
			for (int i = 0; i < regFeeParm.getCount(); i++) {
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
					regFeeParm.setData("AR_AMT", i, reg_fee);
				}
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
					regFeeParm.setData("AR_AMT", i, clinic_fee);
				}
			}
			// System.out.println("regFeesql::" + regFeesql);
			result = TXsaveINSCard(regFeeParm, caseNo); // 执行操作
			// System.out.println("RESULT::::" + result);
			if (null == result)
				return null;
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("医保执行操作失败");
				return null;
			}
			// 24医保卡支付(REG_RECEIPT)
			if (null != result.getValue("MESSAGE_FLG")
					&& result.getValue("MESSAGE_FLG").equals("Y")) {
				System.out.println("医保卡出现错误现金收取");
			} else {
				// 医保支付
				ins_amtTemp = tjInsPay(result, regFeeParm);
				ins_exe = true; // 医保执行操作 需要判断在途状态
				reg.setInsPatType(insParm.getValue("INS_TYPE")); // 就诊医保类型
				reg.setConfirmNo(insParm.getValue("CONFIRM_NO")); // 医保就诊号
				// CONFIRM_NO
			}

		}
		result.setData("INS_AMT", ins_amtTemp);
		result.setData("INS_EXE", ins_exe);

		return result;
	}

	/**
	 * 保存操作 汇总数据
	 * 
	 * @param payWay
	 *            支付类别
	 */
	private void onSaveRegParm(String payWay) {
		// 20现金支付(REG_RECEIPT)
		if (payWay.equals("C0")) {
			reg.getRegReceipt()
					.setPayCash(TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);

		}
		// 21银行卡支付(REG_RECEIPT)
		if (payWay.equals("C1")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
		}
		// 22支票支付(REG_RECEIPT)
		if (payWay.equals("T0")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			// 24备注(写支票号)(REG_RECEIPT)
			reg.getRegReceipt().setRemark(
					TypeTool.getString(getValue("REMARK")));

		}
		// 22记账支付(REG_RECEIPT)
		if (payWay.equals("C4")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			reg.getRegReceipt().setPayDebit(
					TypeTool.getDouble(getValue("FeeY")));
		}
		// 23医疗卡支付(REG_RECEIPT)
		if (payWay.equals("EKT")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY")));
		}
	}

	/**
	 * 保存操作 汇总数据
	 * 
	 * @return boolean =======pangben 2012-7-1添加参数 区分报到操作 flg=false 报到 执行 UPDATE
	 *         QUE_NO 操作
	 */
	private boolean onSaveRegParm(boolean flg) {
		String regionCode = TypeTool.getString(getValue("REGION_CODE")); // 区域
		String ctz1Code = TypeTool.getString(getValue("REG_CTZ1")); // 身份1
		String ctz2Code = TypeTool.getString(getValue("REG_CTZ2")); // 身份2
//		String ctz3Code = TypeTool.getString(getValue("CTZ3_CODE")); // 身份3
		String ctz3Code = ""; // 身份3
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别

		reg.setReassureFlg(this.getValueString("REASSURE_FLG"));//安心价
		
		reg.setRequirement(this.getValueString("REQUIREMENT")); // add by
		// huangtt
		// 20131106
		// 添加备注字段
		reg.setAdmType(admType);
		// 4区域
		reg.setRegion(regionCode);
		// 5看诊日期
		reg.setAdmDate(TypeTool.getTimestamp(getValue("ADM_DATE")));
		// 6挂号操作日期
		reg.setRegDate(SystemTool.getInstance().getDate());
		// 7时段
		reg.setSessionCode(TypeTool.getString(getValue("SESSION_CODE")));
		// 8诊区
		reg.setClinicareaCode((PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(getValue("CLINICROOM_NO"))))
				.getValue("CLINICAREA_CODE", 0));
		// 9诊室
		reg.setClinicroomNo(TypeTool.getString(getValue("CLINICROOM_NO")));
		// 10号别
		reg.setClinictypeCode(TypeTool.getString(getValue("CLINICTYPE_CODE")));
		// System.out.println("就诊日期"+reg.getAdmDate());
		// System.out.println("当前日期"+SystemTool.getInstance().getDate());
		// 19挂号方式
		reg.setRegmethodCode(TypeTool.getString(getValue("REGMETHOD_CODE")));
		String admDate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		// 17预约当诊
		// =========pangben 2012-7-1 区分报到 和当日挂号逻辑
		if (flg) {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("预约");
//				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
				if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
					this.messageBox("你选择的是现场挂号,所以日期必须为今天!");
					return false;
				}
				reg.setApptCode("Y");
				// 12预约时间
				reg.setRegAdmTime(startTime);
			} else {
				// System.out.println("当诊");
				reg.setApptCode("N");
			}
			// 18初复诊
			if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C")))
				// 初诊
				reg.setVisitCode("0");
			else {
				// 复诊
				reg.setVisitCode("1");
			}
			// 11根据VIP取值，得到就诊号
			if (!tableFlg) {
				if (!onSaveParm(admDate))
					return false;
			} else {
				// ===========pangben 2012-7-1 修改 UPDATE 班表que_no 只操作一次增加就诊号码
				TTable table1 = (TTable) this.getComponent("Table1");
				TParm parm = table1.getParmValue();
				TParm temp = parm.getRow(selectRow); // 获得第一个页签数据
				String type = temp.getValue("TYPE"); // VIP 和一般
				if (type.equals("VIP")) {
					// VIP诊挂号
				} else {
					// 普通诊挂号
					int queNo = SchDayTool.getInstance().selectqueno(
							reg.getRegion(),
							reg.getAdmType(),
							TypeTool.getString(reg.getAdmDate()).replaceAll(
									"-", "").substring(0, 8),
							reg.getSessionCode(), reg.getClinicroomNo());
					if (queNo == 0) {
						this.messageBox("已无就诊号!");
						return false;
					}
					reg.setQueNo(queNo);
					if (reg.getQueNo() == -1) {
						// 已无号不能挂号
						this.messageBox("E0017");
						return false;
					}
					// ==========pangben 2012-6-18 修改重号问题
					TParm regParm = reg.getParm();
					regParm.setData("ADM_DATE", admDate);
					if (onSaveQueNo(regParm)) {
						// return true;
					} else {
						return false;
					}
				}
			}
		} else {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("预约");
//				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
				if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
					this.messageBox("你选择的是现场挂号,所以日期必须为今天!");
					return false;
				}
			} else {
				reg.setApptCode("N");
			}
		}

		// 13科室
		reg.setDeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 14医师
		reg.setDrCode(TypeTool.getString(getValue("DR_CODE")));
		// 15实看科别(默认科室)
		reg.setRealdeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 16实看医师(默认医师)
		reg.setRealdrCode(TypeTool.getString(getValue("DR_CODE")));
		// 20身份折扣1
		reg.setCtz1Code(ctz1Code);
		// 21身份折扣2
		reg.setCtz2Code(ctz2Code);
		// 22身份折扣3
		reg.setCtz3Code(ctz3Code);

		// 23转诊院所
		reg.setTranhospCode("");
		// 24检伤号
		reg.setTriageNo("");
		// 25记账单位
		reg.setContractCode(TCM_Transform.getString(getValue("CONTRACT_CODE")));
		// 26报到注记    getValue("REGMETHOD_CODE").equals("O") add by huangtt 20160407  添加114挂号方式
		
		if(RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE")))
		
//		if (getValue("REGMETHOD_CODE").equals("A") || getValue("REGMETHOD_CODE").equals("O"))
			reg.setArriveFlg(true);
		else
			reg.setArriveFlg(false);
		// 27退挂人员
		// reg.setRegcanUser();
		// 28退挂日期
		// reg.setRegcanDate();
		// 29挂号院区
		reg.setAdmRegion(regionCode);
		// 30预防保健时程(计划免疫)
		// reg.setPreventSchCode();
		// 31DRG码
		// reg.setDrgCode();
		// 32发热注记
		// reg.setHeatFlg();
		// 33就诊进度
		reg.setAdmStatus("1");
		// 34报告状态
		reg.setReportStatus("1");
		// 35体重
		// reg.setWeight();
		// 36身高
		// reg.setHeight();
		if (admType.equals("E"))
			reg.setErdLevel(Objects.toString(getValue("ERD_LEVEL"),""));

		// 门急诊收据(For bill),REG_RECEIPT对象
		// reg.createReceipt();
		// 3门急住别(REG_RECEIPT)
		reg.getRegReceipt().setAdmType(admType);
		// 4区域(REG_RECEIPT)
		reg.getRegReceipt().setRegion(regionCode);
		// 5ID号(REG_RECEIPT)
		reg.getRegReceipt().setMrNo(TypeTool.getString(getValue("MR_NO")));
		// 6冲销收据号(REG_RECEIPT)
		// reg.getRegReceipt().setResetReceiptNo("");
		// 8记账日期(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9收费日期(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10收据打印日期(REG_RECEIPT)
		// ===================pangben modify 20110818 记账标记，PRINT_DATE 栏位为空时，进行记账
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			reg.getRegReceipt()
					.setPrintDate(SystemTool.getInstance().getDate());
			// 7收据印刷号(REG_RECEIPT)
			// reg.getRegReceipt().setPrintNo(
			// reg.getRegReceipt().getBilInvoice().getUpdateNo()); //modify by
			// huangtt

		}

		// 11挂号费(REG_RECEIPT)
		// ======================pangben modify 20110815
		onSaveParm(ctz1Code, ctz2Code, ctz3Code);
		// 12折扣前挂号费(REG_RECEIPT)
		reg.getRegReceipt().setRegFeeReal(
				TypeTool.getDouble(getValue("REG_FEE")));

		// 14折扣前诊查费(REG_RECEIPT)
		reg.getRegReceipt().setClinicFeeReal(
				TypeTool.getDouble(getValue("CLINIC_FEE")));
		// 15附加费(REG_RECEIPT)
		// reg.getRegReceipt().setSpcFee(0.00);
		// 16其它费用1(REG_RECEIPT)
		// reg.getRegReceipt().setOtherFee1(0.00);
		// 17其它费用2(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee2(0.00);
		// 18其它费用3(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee3(0.00);
		// 19应收金额(REG_RECEIPT)
		reg.getRegReceipt().setArAmt(TypeTool.getDouble(getValue("FeeY")));
		onSaveRegParm(payWay);
		// 24医保卡支付(REG_RECEIPT)
		// reg.getRegReceipt().setPayInsCard(0.00);
		// 26门急诊财政记账(REG_RECEIPT)
		// reg.getRegReceipt().setPayIns(0.00);
		// 28收款员编码(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		// 29结帐标志(REG_RECEIPT)
		// reg.getRegReceipt().setAccountFlg("");
		// 30日结报表号(REG_RECEIPT)
		// reg.getRegReceipt().setAccountSeq("");
		// 31日结人员(REG_RECEIPT)
		// reg.getRegReceipt().setAccountUser(Operator.getName());
		// 32结账日期(REG_RECEIPT)
		// reg.getRegReceipt().setAccountDate(SystemTool.getInstance().getDate());
		// 服务等级
		reg.setServiceLevel(this.getValueString("SERVICE_LEVEL"));
		// 票据主档BilInvoice(For bil),BIL_INVOICE对象
		// reg.getRegReceipt().createBilInvoice();
		// reg.getRegReceipt().getBilInvoice().getParm(); //modify by huangtt
		// reg.getRegReceipt().getBilInvoice().setCashierCode(Operator.getID());
		// //操作人员
		// reg.getRegReceipt().getBilInvoice().setStartValidDate();
		// reg.getRegReceipt().getBilInvoice().setEndValidDate();
		// reg.getRegReceipt().getBilInvoice().setStatus("1");

		// 票据明细档BILInvrcpt(For bil),BIL_INVRCP对象
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1票据类型(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setInvNo(
		// reg.getRegReceipt().getBilInvoice().getUpdateNo()); //
		// //2发票号码(BIL_INVRCP) modify by huangtt

		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // 操作人员(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // 总金额(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setCancelFlg();
		// reg.getRegReceipt().getBilInvrcpt().setCancelUser();
		// reg.getRegReceipt().getBilInvrcpt().setCancelDate();
		// 判断初始化票据
		// ===huangtt 20131212
		// if(ticketFlg){
		// reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// if (RegMethodTool.getInstance().selPrintFlg(
		// this.getValueString("REGMETHOD_CODE"))) {
		// // 显示下一票号
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo()
		// .length() == 0) {
		// this.messageBox("尚未开账");
		// return false;
		// }
		// }
		// }

		if ("Y".equals(reg.getApptCode())) {
			if (this.getPopedem("LEADER")) {
				this.messageBox("非组长不能预约!");
				return false;
			}
		}
		return true;
	}

	/**
	 * 保存操作统计数据
	 * 
	 * @param admDate
	 *            String
	 * @return boolean flg =false 报到操作不执行 UPDATE QUE_NO 操作
	 */
	private boolean onSaveParm(String admDate) {
		if (SchDayTool.getInstance().isVipflg(reg.getRegion(),
				reg.getAdmType(), admDate, reg.getSessionCode(),
				reg.getClinicroomNo())) {
			int row = (Integer) callFunction("UI|Table2|getSelectedRow");
			if (row < 0)
				return false;
			// 拿到table控件
			TTable table2 = (TTable) callFunction("UI|table2|getThis");
			TParm data = table2.getParmValue();
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					data, row);
			// 20090217 新方法 -------end---------
			// =======pangben 2012-7-31 修改查询是否已经vip占号
			String regAdmTime =TypeTool.getString(table2.getValueAt(row, table2.getColumnIndex("START_TIME"))); //add by huangtt 20140910
			int queNoVIP = TypeTool.getInt(table2.getValueAt(row, table2
					.getColumnIndex("QUE_NO")));
			String vipSql = "SELECT QUE_NO,QUE_STATUS FROM REG_CLINICQUE "
					+ "WHERE ADM_TYPE='"
					+ reg.getAdmType()
					+ "' AND ADM_DATE='"
					+ TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8) + "'" + " AND SESSION_CODE='"
					+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
					+ reg.getClinicroomNo() + "' AND  QUE_NO='" + queNoVIP
					+ "' AND QUE_STATUS='N'";
			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				this.messageBox("已占号!");
				// 初始化带入VIP班表
				onQueryVipDrTable();
				return false;
			}
			if (queNoVIP == 0) {
				this.messageBox("已无VIP就诊号!");
				return false;
			}
			reg.setQueNo(queNoVIP);
			reg.setRegAdmTime(regAdmTime);  //add by huangtt 20140910
			reg.setVipFlg(true);
			if (reg.getQueNo() == -1) {
				this.messageBox("E0017");
				return false;
			}

		} else {
			int queNo = SchDayTool.getInstance().selectqueno(
					reg.getRegion(),
					reg.getAdmType(),
					TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8), reg.getSessionCode(),
					reg.getClinicroomNo());
			if (queNo == 0) {
				this.messageBox("已无就诊号!");
				return false;
			}
			reg.setQueNo(queNo);
			if (reg.getQueNo() == -1) {
				// 已无号不能挂号
				this.messageBox("E0017");
				return false;
			}
		}
		// =========pangben 2012-6-18
		TParm regParm = reg.getParm();
		regParm.setData("ADM_DATE", admDate);
		if (onSaveQueNo(regParm)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存操作统计数据
	 * 
	 * @param ctz1Code
	 *            String
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 */
	private void onSaveParm(String ctz1Code, String ctz2Code, String ctz3Code) {
		if (!feeShow) { // 判断是否是医保中心获得的费用====南京医保使用，现在feeShow=false 不会执行
			// feeShow=true
			reg.getRegReceipt().setRegFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
							ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));
			// 13诊查费(REG_RECEIPT)
			reg.getRegReceipt().setClinicFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")),
							"CLINIC_FEE", ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));

		} else {
			reg.getRegReceipt().setRegFee(
					TypeTool.getDouble(getValue("REG_FEE")));
			reg.getRegReceipt().setClinicFee(
					TypeTool.getDouble(getValue("CLINIC_FEE")));
		}
	}

	/**
	 * 医疗卡挂号出现问题撤销操作
	 */
	private void cancleEKTData() {
		// 医疗卡挂号出现问题撤销操作
		TParm oldParm = new TParm();
		oldParm.setData("BUSINESS_NO", businessNo);
		oldParm.setData("TREDE_NO", tredeNo);
		TParm result = TIOM_AppServer.executeAction("action.ins.EKTAction",
				"deleteRegOldData", oldParm);
		// if (result.getErrCode() < 0)
		// System.out.println("err:" + result.getErrText());
	}

	/**
	 * 泰心挂号排队叫号
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		// System.out.println("========caseNo=========="+caseNo);
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// 挂号日期
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// 看诊科室
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// 医师代码
		sendString += result.getValue("DR_CODE", 0) + "|";
		// 医师姓名
		sendString += result.getValue("USER_NAME", 0) + "|";
		// 号别
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// 诊间
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// 患者病案号
		sendString += result.getValue("MR_NO", 0) + "|";

		// 患者姓名
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// 患者性别

		sendString += result.getValue("SEX", 0) + "|";
		// 患者生日
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// 看诊序号
		sendString += result.getValue("QUE_NO", 0) + "|";

		// System.out.println("==adm date=="+result.getValue("ADM_DATE",0));

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// System.out.println("===noSql=="+noSql);
		// 限挂人数
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// 已挂人数 noParm.getValue("QUE_NO", 0)+ "|";
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";
		// this.messageBox("SESSION_CODE"+((TComboBox)
		// this.getComponent("SESSION_CODE")).getSelectedText());
		// 时间段
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));
		// System.out.println("===timeSql==="+timeSql);

		// 退挂叫号
		if ("UNREG".equals(type)) {
			// 预约处理

			inParm.setData("msg", sendString);
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + Que_No.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 **/
			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);
			// this.messageBox("退挂叫号!");

		} else if ("REG".equals(type)) {
			// System.out.println("adm time===="+this.reg.getRegAdmTime());
			sendString += "|";
			// 预约处理
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				// sendString += result.getValue("ADM_DATE", 0).replaceAll("-",
				// "").substring(
				// 0, 8)+startTimeParm.getValue("START_TIME", 0) + "00";
				// System.out.println("========预约sendString=========="+sendString);
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|内分泌代谢科|000875|葛焕琦|主任医师|06诊室|000000001009|谷绍明|女|1936-01-05|2|60|2|上午|
			inParm.setData("msg", sendString);
			// this.messageBox("挂号叫号!");
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + QueNo.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 * System.out.println("Reg_sendString--->" + sendString);
			 **/

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction", "doReg",
					inParm);

		}

		return "true";

	}

	/**
	 * 号别Combo值改变事件
	 * 
	 * @param flg
	 *            boolean
	 */
	public void onClickClinicType(boolean flg) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 挂号费
		this.setValue("REG_FEE", reg_fee1);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		// 打折前的
		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 诊查费
		this.setValue("CLINIC_FEE", celinic_fee1);
		// //应收费用
		// if (pat != null) {
		setValue("FeeY", reg_fee + clinic_fee);
		// add by huangtt 20140115
		if (aheadFlg) {
			if (flg) { // 预约挂号不显示应收金额
				setValue("FeeS", reg_fee + clinic_fee);
			}
		}

		// }
	}

	/**
	 * 号别Combo值改变事件
	 */
	public void onClickClinicType() {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 挂号费
		this.setValue("REG_FEE", reg_fee1);

		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 诊查费
		this.setValue("CLINIC_FEE", celinic_fee1);
		// //应收费用
		setValue("FeeY", reg_fee + clinic_fee);

		if (aheadFlg) {

			setValue("FeeS", reg_fee + clinic_fee);

		}
	}

	/**
	 * 号别Combo值改变事件
	 * 
	 * @param fee
	 *            int
	 */
	public void onClickClinicType(int fee) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 挂号费
		this.setValue("REG_FEE", reg_fee1);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 诊查费
		this.setValue("CLINIC_FEE", celinic_fee1);
		// 应收费用
		double feeY = reg_fee + clinic_fee;

		// if (pat != null)
		setValue("FeeY", feeY * fee);
		if (aheadFlg) {
			setValue("FeeS", feeY * fee);
		}

	}

	/**
	 * 支付方式改变事件
	 */
	public void onSelPayWay() {
		if (getValue("PAY_WAY").equals("PAY_CHECK"))
			callFunction("UI|REMARK|setEnabled", true);
		else
			callFunction("UI|REMARK|setEnabled", false);
		if (getValue("PAY_WAY").equals("PAY_DEBIT"))
			callFunction("UI|CONTRACT_CODE|setEnabled", true);
		else
			callFunction("UI|CONTRACT_CODE|setEnabled", false);

	}

	/**
	 * 查询医师排班(一般)
	 * 
	 */
	public void onQueryDrTable() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// 筛选数据专家诊，普通诊
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		// 可是过滤权限
		if (this.getPopedem("deptFilter"))
			parm.setData("DEPT_CODE_SORT", "1101020101");
		TParm data = SchDayTool.getInstance().selectDrTable(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
		TTable table = (TTable) this.getComponent("Table1");
		int selRow = table.getSelectedRow();
		if (selRow < 0)
			return;
		String drCode = table.getItemString(selRow, 4);
		String clinicroomNo = table.getItemString(selRow, 3);
		String sql = "SELECT SEE_DR_FLG FROM REG_PATADM" + "  WHERE DR_CODE='"
				+ drCode + "' " + "AND  CLINICROOM_NO ='" + clinicroomNo + "'"
				+ "AND SEE_DR_FLG='N'";
		// System.out.println("sql===="+sql);
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = selparm.getCount();
		this.setValue("COUNT", count + "");
	}

	/**
	 * 查询医师排班(VIP)
	 */
	public void onQueryVipDrTable() {
		TTable table2 = new TTable();
		table2.removeAll();
		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
				true);
		parm.setData("ADM_TYPE", admType);
		parm.setData("VIP_ADM_DATE", StringTool.getString(
				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
		if (data2.getErrCode() < 0) {
			messageBox(data2.getErrText());
			return;
		}
		this.callFunction("UI|Table2|setParmValue", data2);
	}

	/**
	 * 查询病患挂号信息
	 */
	// CASE_NO;ADM_DATE;SESSION_CODE;DEPT_CODE;DR_CODE;QUE_NO;ADM_STATUS;ARRIVE_FLG;CONFIRM_NO;INS_PAT_TYPE;REGMETHOD_CODE
	public void selPatInfoTable() {
		TParm parm = new TParm();
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_START_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_END_DATE")), "yyyyMMdd");
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("YY_START_DATE", startTime);
		parm.setData("YY_END_DATE", endTime);
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		TParm data = PatAdmTool.getInstance().selPatInfoForREG(parm);
		// System.out.println("table3显示数据" + data);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table3|setParmValue", data);

	}

	/**
	 * 根据科室下拉列表，查询医师排班(一般)
	 */
	public void onQueryDrTableByDrCombo() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		parm.setDataN("DEPT_CODE_SORT", TypeTool
				.getString(getValue("DEPT_CODE_SORT")));
		parm.setDataN("DR_CODE_SORT", TypeTool
				.getString(getValue("DR_CODE_SORT"))); // add by huangtt
		// 20140227
		// 筛选数据专家诊，普通诊
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}

			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		TParm data = SchDayTool.getInstance().selectDrTable(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
	}

	/**
	 * 算找零金额
	 */
	public void onFee() {
		DecimalFormat df = new DecimalFormat("##########0.00");
		// 找零金额
		setValue("FeeZ", TypeTool.getDouble(df.format(getValue("FeeS")))
				- TypeTool.getDouble(df.format(getValue("FeeY"))));
		// 得到焦点
		this.grabFocus("SAVE_REG");
	}

	/**
	 * 补印
	 */
	public void onPrint() {
		// TParm forPrtParm = new TParm();
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		String caseNo = (String) table3.getValueAt(row, 0);

		if (ticketFlg) {
			String confirmNo = (String) table3.getParmValue().getValue(
					"CONFIRM_NO", row);
			// if (this.getValueString("NEXT_NO").length() <= 0
			// || this.getValueString("NEXT_NO").compareTo(endInvNo) > 0) {
			// this.messageBox("票据已用完!");
			// return;
			// }
			TParm temp = new TParm();
			temp.setData("RECEIPT_TYPE", "REG");
			temp.setData("CASE_NO", caseNo);
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				temp.setData("REGION_CODE", Operator.getRegion());
			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					temp);
			if (null != result && result.getValue("BIL_STATUS", 0).equals("1")) {
				this.messageBox("记账挂号费用没有执行结算操作,不可以打票");
				return;
			}

			TParm onREGReprintParm = new TParm();
			onREGReprintParm.setData("CASE_NO", caseNo);
			onREGReprintParm.setData("OPT_USER", Operator.getID());
			onREGReprintParm.setData("OPT_TERM", Operator.getIP());
			onREGReprintParm.setData("ADM_TYPE", admType);
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onREGReprint", onREGReprintParm);
			if (result.getErrCode() < 0) {
				this.messageBox("补印操作失败");
				return;
			}
			result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
			result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("CONFIRM_NO", confirmNo);
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm); // 判断此次操作是否是医保操作
			if (mzConfirmParm.getErrCode() < 0) {
				return;
			}
			TParm printParm = null;
			if (mzConfirmParm.getCount() > 0) {
				printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
				insFlg = true;
			}
			onRePrint(result, mzConfirmParm, printParm);
		}
		if (singleFlg) {

			// " WHERE REGION_CODE = '"+reg.getRegion()+"'" +
			// " AND ADM_TYPE = '"+reg.getAdmType()+"'" +
			// " AND ADM_DATE = '"+reg.getAdmDate().toString().replace("-",
			// "").replace("/", "").substring(0, 8)+"'" +
			// " AND SESSION_CODE = '"+reg.getSessionCode()+"'" +
			// " AND REALDEPT_CODE = '"+reg.getRealdeptCode()+"'" +
			// " AND REALDR_CODE = '"+reg.getRealdrCode()+"'" +
			// " AND CLINICROOM_NO='"+reg.getClinicroomNo()+"'" ;

			TParm parm = table3.getParmValue();
			onPrintReg(caseNo, "(copy)"); // add by huangtt 20140331

		}

		this.onInit();
	}

	/**
	 * 补印
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
		// 显示方式:科室(诊室)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "号)"); // 号别
		// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		parm.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		DecimalFormat df = new DecimalFormat("########0.00");
		if (tjINS) {
			ektNewSum = df.format(p3.getDouble("CURRENT_BALANCE"));
		}
		parm.setData("CURRENT_BALANCE", "TEXT", "￥ "
				+ df.format(Double.parseDouble(ektNewSum == null
						|| "".equals(ektNewSum) ? "0.00" : ektNewSum))); // 医疗卡剩余金额

		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_CASH", "TEXT", "现金:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // 现金
			// 个人账户
			String sqlamt = " SELECT ACCOUNT_PAY_AMT  FROM INS_OPD "
					+ " WHERE CASE_NO ='"
					+ mzConfirmParm.getValue("CASE_NO", 0) + "'"
					+ " AND CONFIRM_NO ='"
					+ mzConfirmParm.getValue("CONFIRM_NO", 0) + "'";
			TParm insaccountamtParm = new TParm(TJDODBTool.getInstance()
					.select(sqlamt));
			if (insaccountamtParm.getErrCode() < 0) {

			} else {
				parm.setData("PAY_ACCOUNT", "TEXT", "账户:"
						+ StringTool.round(insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0), 2));
				parm.setData("PAY_DEBIT", "TEXT", "医保:"
						+ StringTool.round((printParm.getDouble("PAY_INS_CARD",
								0) - insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0)), 2)); // 医保支付
			}
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// 医保特殊人员身份显示
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		// ===zhangp 20120313 start
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			// parm.setData("Cost_class", "TEXT", "门统");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			// parm.setData("Cost_class", "TEXT", "门特");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
		// ===zhangp 20120313 end
		String caseNo = parm.getValue("CASE_NO", "TEXT");// add by wanglong
		// 20121217
		TParm oldDataRecpParm = BILREGRecpTool.getInstance().selForRePrint(
				caseNo);// add by wanglong 20121217
		parm.setData("RECEIPT_NO", "TEXT", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));// add by wanglong 20121217
		// this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
		// parm, true);
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REGRECPPrint.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
		this.openPrintDialog(IReportTool.getInstance().getReportPath(
				"REGRECPPrint.jhw"), IReportTool.getInstance().getReportParm(
				"REGRECPPrint.class", parm), true);// 报表合并modify by wanglong
		}
		// 20130730
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.initReg();
		clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;RESID_ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;NHI_NO;EKT_CURRENT_BALANCE;COUNT"
				+ " ;REMARKS;CURRENT_ADDRESS;REQUIREMENT"
				+ " ;MEM_CODE;START_DATE;END_DATE;GUARDIAN_NAME;GUARDIAN_RELATION;GUARDIAN_TEL;GUARDIAN_PHONE;NATION_CODE;SPECIES_CODE;MARRIAGE_CODE"
				+ ";REG_CTZ2;DEPT_CODE_SORT;DR_CODE_SORT;INSURE_INFO;PAT_PACKAGE;REASSURE_FLG"); // ====add by
		// huangtt 20131106
		clearValue("FIRST_NAME;LAST_NAME;CELL_PHONE;CUSTOMER_SOURCE");
		// ============huangtt modify 20131106 start 证件类型默认显示身份证
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE CHN_DESC = '身份证' AND GROUP_ID = 'SYS_IDTYPE'";
		TParm typeParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ID_TYPE", typeParm.getValue("ID", 0));
		// ============huangtt modify 20131106 end
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		callFunction("UI|FOREIGNER_FLG|setEnabled", true); // 其他证件可编辑======pangben
		// modify 20110808
		// if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
		// callFunction("UI|MR_NO|setEnabled", false);
		// this.grabFocus("PAT_NAME");
		// }
		// if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
		// callFunction("UI|MR_NO|setEnabled", true);
		// this.grabFocus("MR_NO");
		// }
		// callFunction("UI|MR_NO|setEnabled", true); //其他证件可编辑======pangben
		// modify 20110808
		callFunction("UI|CONTRACT_CODE|setEnabled", true); // 记账单位可编辑
		callFunction("UI|SAVE_REG|setEnabled", true);// 收费按钮可编辑
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// feeIstrue = false;
		ins_amt = 0.00; // 医保金额
		accountamtforreg = 0.00;// 个人账户
		feeShow = false; // 南京医保中心获得费用显示
		txEKT = false; // 泰心医疗卡写卡管控
		p3 = null; // 医疗卡读卡parm
		insFlg = false; // 医保卡读卡操作
		tjINS = false; // 医疗卡读卡完成操作
		reSetEktParm = null; // 医疗卡退费使用判断是否执行医疗卡退费操作
		confirmNo = null; // 医保卡就诊号，退挂时时使用
		reSetCaseNo = null; // 退挂使用就诊号码
		insType = null;
		tableFlg = false; // 判断选中第一个页签表格数据
		ektNewSum = "0.00"; // 医疗卡扣款后金额
		// ===zhangp 20120427 start
		greenParm = null;// //绿色通道使用金额
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();
		ticketFlg = REGSysParmTool.getInstance().selTicketFlg();
		singleFlg = REGSysParmTool.getInstance().selSingleFlg();
		
//		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		initSchDay();
		callFunction("UI|SAVE_REG|setEnabled", true);
		pat = null;
		// reg=null;
		ins_exe = false;
		preFlg = "N";// yanjing 20131212
		
		// add by huangtt 20140211
		// 初始化初复诊
		TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
			setValue("VISIT_CODE_F", "Y");
		}

		TTextFormat dept_code_sort = (TTextFormat) this
				.getComponent("DEPT_CODE_SORT");
		dept_code_sort.onQuery();
		TTextFormat dr_code_sort = (TTextFormat) this
				.getComponent("DR_CODE_SORT");
		dr_code_sort.onQuery();
		//add by huangtt 20140915 start
		TTextFormat dept_code = (TTextFormat) this.getComponent("DEPT_CODE");
		dept_code.onQuery();
		TTextFormat dr_code = (TTextFormat) this.getComponent("DR_CODE");
		dr_code.onQuery();
		TTextFormat clinicroom_no = (TTextFormat) this.getComponent("CLINICROOM_NO");
		clinicroom_no.onQuery();
		//add by huangtt 20140915 end
		saveFlg = false;
		clinictypeCode="";
		
		TButton btton=(TButton) this.getComponent("SAVE_PAT");
		btton.setEnabled(true);
		callFunction("UI|INSURE_INFO|setEnabled", false);//add by huangjw 20150731
		callFunction("UI|PAT_PACKAGE|setEnabled", false);//add by huangjw 20150731
	}

	/**
	 * 是否关闭窗口
	 * 
	 * @return boolean true 关闭 false 不关闭
	 */
	public boolean onClosing() {
		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		return true;
	}

	/**
	 * 初始化时段
	 */
	public void initSession() {
		// 初始化时段Combo,取得默认时段
		String defSession = SessionTool.getInstance().getDefSessionNow_New(admType,
				Operator.getRegion());
		setValue("SESSION_CODE", defSession);
		setValue("VIP_SESSION_CODE", defSession);
	}

	/**
	 * 为清空后初始化
	 */
	public void initReg() {
		// 设置默认身份
		setValue("CTZ1_CODE", "99");
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// 过滤数据
		combo_ctz.setNhiFlg("");
		combo_ctz.onQuery();
		setValue("REG_CTZ1", "99");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("ADM_DATE", SystemTool.getInstance().getDate());
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
			this.setValue("ADM_DATE", admDate);
		}
		// 初始化默认(现场)挂号方式
		setValue("REGMETHOD_CODE", "A");

		// 初始化预约信息开始时间
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// 初始化VIP班表Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// 置退挂,报道,补印按钮为灰
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// 置收费按钮可编辑
		callFunction("UI|SAVE_REG|setEnabled", true);
		// 置挂号信息界面控件可编辑
		setControlEnabled(true);
		setRegion();
	}

	/**
	 * 通信邮编的得到省市
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
		if (parm.getErrCode() != 0 || parm.getCount() == 0) {
			return;
		}
		setValue("STATE", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("CITY", parm.getData("POST_CODE", 0).toString());
		this.grabFocus("MARRIAGE_CODE");
	}

	/**
	 * 设置区域是否可以下拉
	 */
	public void setRegion() {
		if (!REGSysParmTool.getInstance().selOthHospRegFlg())
			callFunction("UI|REGION_CODE|setEnabled", false);
	}

	/**
	 * 通过城市带出邮政编码
	 */
	public void selectCode() {
		this.setValue("POST_CODE", this.getValue("CITY"));
	}

	/**
	 * 检测病患相同姓名
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		// setPatName1();
		if (StringUtil.isNullString(patName)) {
			return;
		}
		// add by huangtt 20131126
		String sexCode = this.getValueString("SEX_CODE");
		if (StringUtil.isNullString(sexCode)) {
			this.grabFocus("PY1");
			return;
		}
		try {
			
			String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS "
					+ " , ID_TYPE, CURRENT_ADDRESS" // add by huangtt 20131106
					+ " FROM SYS_PATINFO A " // del ,EKT_ISSUELOG B
					+ " WHERE PAT_NAME = '"
					+ patName
					+ "' AND  SEX_CODE='"
					+ sexCode // add by huangtt 20131126
					+ "' "
					// + " AND A.MR_NO = B.MR_NO (+) "
					+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}

			// 选择病患信息
			if (same.getCount("MR_NO") > 0) {
				int sameCount = this.messageBox("提示信息",
						"已有相同姓名病患信息,是否继续保存此人信息", 0);
				if (sameCount != 1) {
					this.grabFocus("ID_TYPE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.grabFocus("ID_TYPE");

	}

	/**
	 * 检测病患相同身份证号
	 */
	public void onIDNo() {
		try {
			String idNo = this.getValueString("IDNO");
			String idType = this.getValueString("ID_TYPE"); // add by huangtt
			// 20131106

			// if (StringUtil.isNullString(idType)) { //add by huangtt 20131106
			// return;
			// }
			if (StringUtil.isNullString(idNo)) {
				return;
			}

			if (idType.equals("01")) {// 证件类型为身份证
				if (!isCard(idNo)) {
					this.messageBox("录入的身份证号不符合要求！");
					this.grabFocus("IDNO");
					return;
				} else {
					if (idNo.length() > 14) {
						String time = idNo.substring(6, 14);
						String time2 = time.substring(0, 4) + "/"
								+ time.substring(4, 6) + "/"
								+ time.substring(6, 8);
						// System.out.println("time2="+time2);
						// 显示出生日期
						this.setValue("BIRTH_DATE", time2);
						String sexCode = idNo.substring(idNo.length() - 2);
						sexCode = sexCode.substring(0, 1);
						// System.out.println("性别："+sexCode);
						// 设置性别
						setSexCode(sexCode);
						// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS

					}
				}
			}
			String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS, A.MR_NO"
					// +",B.EKT_CARD_NO "
					+ " , ID_TYPE, CURRENT_ADDRESS" // add by huangtt
					// 20131106
					+ " FROM SYS_PATINFO A"
					// +",EKT_ISSUELOG B "
					+ " WHERE A.IDNO = '" + idNo + "'  "
					// + " AND A.ID_TYPE = '"+idType+"'" //add by
					// huangtt 20131106
					// + " AND A.MR_NO = B.MR_NO (+) "
					+ " ORDER BY A.OPT_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			// 选择病患信息
			if (same.getCount("MR_NO") > 0) {
				int sameCount = this.messageBox("提示信息", "已有相同病患信息,是否继续保存此人信息",
						0);
				if (sameCount != 1) {
					this.grabFocus("BIRTH_DATE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
//			this.grabFocus("BIRTH_DATE");
		} catch (Exception e) {
			// TODO: handle exception
		} 
		this.grabFocus("BIRTH_DATE");

	}

	// =====huangtt 20131106 start
	/**
	 * 检测病患相同出生日期
	 */
	public void onBirthDate() {
		String birthDate = this.getValueString("BIRTH_DATE");
		if (StringUtil.isNullString(birthDate)) {
			return;
		}
		birthDate = birthDate.substring(0, 10).replace(":", "")
				.replace("-", "").replace(" ", "");
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " , ID_TYPE, CURRENT_ADDRESS"
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE BIRTH_DATE = "
				+ "TO_DATE ('"
				+ birthDate
				+ "', 'YYYYMMDD')"
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
			if (sameCount != 1) {
				this.grabFocus("SEX_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("SEX_CODE");
	}

	/**
	 * 检测病患相同现住址
	 */
	public void onCurrentAddress() {
		String currentAddress = this.getValueString("CURRENT_ADDRESS");
		if (StringUtil.isNullString(currentAddress)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " , ID_TYPE, CURRENT_ADDRESS"
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE CURRENT_ADDRESS = '"
				+ currentAddress
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";

		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}

		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
			if (sameCount != 1) {
				this.grabFocus("REMARKS");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("NATION_CODE");
	}

	// =====huangtt 20131106 end

	/**
	 * 检测病患相同身份证号
	 */
	public void onTelHome() {
		String telHome = this.getValueString("TEL_HOME");
		if (StringUtil.isNullString(telHome)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat =
		// ===zhangp 20120319 start
		"SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.TEL_HOME = '" + telHome + "'  "
				+ " AND A.MR_NO = B.MR_NO (+) " + " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同电话号码病患信息,是否继续保存此人信息",
					0);
			if (sameCount != 1) {
				this.grabFocus("POST_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("POST_CODE");
	}

	/**
	 * 报到
	 */
	public void onArrive() {
		String caseNo = "";// yanjing 20131212
		String admdate = "";// yanjing 20131212
		// if(preFlg.equals("N")){
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		admdate = table3Parm.getValue("ADM_DATE", row);
		if (admdate.equals(null) || "".equals(admdate)) {
			admdate = "";
		} else {
			// admdate = table3Parm.getData("ADM_DATE", row).toString();
			admdate = admdate.substring(0, 10);
			String date = SystemTool.getInstance().getDate().toString();
			date = date.substring(0, 10);
			if (!admdate.equals(date)) {
				messageBox("非当日，不能报到。");
				return;
			}
		}
		// ====zhangp 20120306 modify end
		caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);

		// // 保存医疗卡
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("病案号不能为空");
			return;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// 挂号主表,REG对象
		// 2门急别
		if (!onSaveRegParm(false))
			return;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8记账日期(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9收费日期(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10收据打印日期(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28收款员编码(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // 挂号收据(REG_RECEIPT)
		// 票据主档BilInvoice(For bil),BIL_INVOICE对象
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// 票据明细档BILInvrcpt(For bil),BIL_INVRCP对象
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // 票据明细档收据号(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1票据类型(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2发票号码(BIL_INVRCP)
		// 7收据印刷号(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // 操作人员(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // 总金额(BIL_INVRCP)
		// 判断初始化票据
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// // 显示下一票号
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
		// this.messageBox("尚未开账");
		// return;
		// }
		reg.getRegReceipt().getBilInvoice().getParm();
		// 门急诊主档
		TParm saveParm = new TParm();

		// 票据主档
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// 票据明细档
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// 医保医疗操作 共用部分
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别
		if (!onInsEkt(payWay, caseNo)) {
			return;
		}
		saveParm.setData("REG", regParm.getData());
		// 门诊收据
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// 保存医保数据执行修改REG_PADADM
			// 表中INS_PAT_TYPE 和
			// COMFIRM_NO 字段
		}
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("报道失败");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// 医疗卡操作回写金额
			if (payWay.equals("EKT")) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", ektOldSum);
				try {
					writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
							pat.getMrNo());
					// 回写医疗卡金额
					if (writeParm.getErrCode() < 0)
						System.out.println("err:" + writeParm.getErrText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("err:医疗卡写卡动作失败");
					e.printStackTrace();
				}
			}
			return;
		}

		// 门诊打票操作
		result = onPrintParm();
		if (ticketFlg) {
			onPrint(result);
		}
		if (singleFlg) {
			onPrintReg(caseNo, ""); // add by huangtt 20140331
		}
		this.onClear();
		// 报到调用排队叫号
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("叫号失败");
		}
		BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// // 初始化下一票号
		// setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
	}

	/**
	 * 报到 yanjing 预开检查报到
	 */
	public boolean onArrivePre() {
		String caseNo = "";// yanjing 20131212
		String admdate = "";// yanjing 20131212
		// if(preFlg.equals("N")){
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		admdate = table3Parm.getValue("ADM_DATE", row);
		if (admdate.equals(null) || "".equals(admdate)) {
			admdate = "";
		} else {
			// admdate = table3Parm.getData("ADM_DATE", row).toString();
			admdate = admdate.substring(0, 10);
			String date = SystemTool.getInstance().getDate().toString();
			date = date.substring(0, 10);
			if (!admdate.equals(date)) {
				messageBox("非当日，不能报到。");
				return false;
			}
		}
		// ====zhangp 20120306 modify end
		caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);

		// // 保存医疗卡
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("病案号不能为空");
			return false;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// 挂号主表,REG对象
		// 2门急别
		if (!onSaveRegParm(false))
			return false;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8记账日期(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9收费日期(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10收据打印日期(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28收款员编码(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // 挂号收据(REG_RECEIPT)
		// 票据主档BilInvoice(For bil),BIL_INVOICE对象
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// 票据明细档BILInvrcpt(For bil),BIL_INVRCP对象
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // 票据明细档收据号(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1票据类型(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2发票号码(BIL_INVRCP)
		// 7收据印刷号(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // 操作人员(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // 总金额(BIL_INVRCP)
		// 判断初始化票据
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// 显示下一票号
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
		// this.messageBox("尚未开账");
		// return false;
		// }
		reg.getRegReceipt().getBilInvoice().getParm();
		// 门急诊主档
		TParm saveParm = new TParm();

		// 票据主档
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// 票据明细档
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// 医保医疗操作 共用部分
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别
		if (!onInsEkt(payWay, caseNo)) {
			return false;
		}
		saveParm.setData("REG", regParm.getData());
		// 门诊收据
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// 保存医保数据执行修改REG_PADADM
			// 表中INS_PAT_TYPE 和
			// COMFIRM_NO 字段
		}
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("报道失败");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// 医疗卡操作回写金额
			if (payWay.equals("EKT")) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", ektOldSum);
				try {
					writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
							pat.getMrNo());
					// 回写医疗卡金额
					if (writeParm.getErrCode() < 0)
						System.out.println("err:" + writeParm.getErrText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("err:医疗卡写卡动作失败");
					e.printStackTrace();
				}
			}
			return false;
		}

		// 门诊打票操作
		result = onPrintParm();
		if (ticketFlg) {
			onPrint(result);
		}
		if (singleFlg) {
			onPrintReg(caseNo, ""); // add by huangtt 20140331
		}

		this.onClear();
		// 报到调用排队叫号
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("叫号失败");
		}
		BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// // 初始化下一票号
		// setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
		return true;
	}

	/**
	 * 设置英文名
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * 退挂操作
	 */
	public void onUnReg() {
		// =====zhangp 20120301 modify start
		if (this.messageBox("询问", "是否退挂", 2) == 0) {
			this.callFunction("UI|unreg|setEnabled", false);
			if (!this.getPopedem("LEADER")) {
				this.messageBox("非组长不能退挂!");
				return;
			}
			TTable table3 = (TTable) callFunction("UI|table3|getThis");
			int row = table3.getSelectedRow();
			if (row < 0) {
				this.messageBox("请选择要退挂的数据");
				return;
			}
			
			//判断是否产生诊疗费
			String caseNoUnreg = table3.getParmValue().getValue(
							"CASE_NO", row);
			String sql2 = "SELECT RX_NO,AR_AMT  FROM OPD_ORDER " + 
					"		     WHERE CASE_NO = '"+caseNoUnreg+"' " + 
					"		       AND RX_NO = 'CLINIC_FEE' ";
			
			TParm updateParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
			System.out.println("updateParm2::::::"+updateParm2);
			if (updateParm2.getData("RX_NO")!=null || updateParm2.getDouble("AR_AMT")>0) {
				messageBox("已看诊无法退号！");
				return;
			}
			
			// ===zhangp 20120316 start
			String arriveFlg = (String) table3.getValueAt(row, 7);
			// 判断是否预约挂号
			if ("N".equals(arriveFlg)) {
				table3.getParmValue().getRow(row);
				String sql = "UPDATE REG_PATADM SET REGCAN_USER = '"
						+ Operator.getID()
						+ "',REGCAN_DATE = SYSDATE,OPT_USER = '"
						+ Operator.getID() + "',"
						+ "OPT_DATE = SYSDATE,OPT_TERM = '" + Operator.getIP()
						+ "' " + "WHERE CASE_NO = '"
						+ table3.getParmValue().getValue("CASE_NO", row) + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("退挂失败");
					return;
				}
				String admDate = table3.getParmValue()
						.getValue("ADM_DATE", row);
				admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
						+ admDate.substring(8, 10);
				sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
						+ table3.getParmValue().getValue("ADM_TYPE", row)
						+ "'AND ADM_DATE = '"
						+ admDate
						+ "' AND "
						+ "SESSION_CODE = '"
						+ table3.getParmValue().getValue("SESSION_CODE", row)
						+ "' AND "
						+ "CLINICROOM_NO = '"
						+ table3.getParmValue().getValue("CLINICROOM_NO", row)
						+ "' AND "
						+ "QUE_NO = '"
						+ table3.getParmValue().getValue("QUE_NO", row) + "'";
				updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("退挂失败");
					return;
				}
				// 得到预开检查标记
				String isPre = table3.getParmValue().getValue("IS_PRE_ORDER",
						row);
				if (isPre.equals("Y")) {
					// 预开检查，退挂的同时删除opd_order中的预开检查药嘱
					String preCaseNo = table3.getParmValue().getValue(
							"CASE_NO", row);
					// 根据case_no查opd_order表得到rx_no
					String selRxNo = "SELECT RX_NO FROM OPD_ORDER WHERE CASE_NO = '"
							+ preCaseNo + "'";
					TParm selRxNoParm = new TParm(TJDODBTool.getInstance()
							.select(selRxNo));
					if (selRxNoParm.getCount() > 0) {// 删除opd_order中的医嘱
						String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '"
								+ preCaseNo + "' AND IS_PRE_ORDER = 'Y'";
						TParm delPreParm = new TParm(TJDODBTool.getInstance()
								.update(delPreSql));
						if (delPreParm.getErrCode() < 0) {
							messageBox("预约信息退挂失败");
							return;
						}
					}
				}

				messageBox("预约取消成功");
				// 调用排队叫号
				if (!"true".equals(callNo("UNREG", table3.getParmValue()
						.getValue("CASE_NO", row)))) {
					this.messageBox("叫号失败");
				}
				
				// add by wangbin 20150806 退挂成功后更新临时表的取消状态 START
				this.cancelMroRegAppointment(table3.getParmValue().getValue(
						"CASE_NO", row));
				// add by wangbin 20150806 退挂成功后更新临时表的取消状态 END
				
				this.onClear();
				return;
			}
			// ===zhangp 20120316 end

			// 得到预开检查标记
			String isPre = table3.getParmValue().getValue("IS_PRE_ORDER", row);
			if (isPre.equals("Y")) {
				// 预开检查，退挂的同时删除opd_order中的预开检查药嘱
				String preCaseNo = table3.getParmValue().getValue("CASE_NO",
						row);
				// 根据case_no查opd_order表得到rx_no
				String selRxNo = "SELECT RX_NO FROM OPD_ORDER WHERE CASE_NO = '"
						+ preCaseNo + "'";
				TParm selRxNoParm = new TParm(TJDODBTool.getInstance().select(
						selRxNo));
				if (selRxNoParm.getCount() > 0) {// 删除opd_order中的医嘱
					String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '"
							+ preCaseNo + "' AND IS_PRE_ORDER = 'Y'";
					TParm delPreParm = new TParm(TJDODBTool.getInstance()
							.update(delPreSql));
					if (delPreParm.getErrCode() < 0) {
						messageBox("预约信息退挂失败");
						return;
					}
				}
			}

			String caseNo = (String) table3.getValueAt(row, 0);
			TParm tredeParm = new TParm(); // 查询此次退挂操作是否是医疗卡退挂
			tredeParm.setData("CASE_NO", caseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // 类型
			tredeParm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
			confirmNo = table3.getParmValue().getValue("CONFIRM_NO", row); // 医保就诊号
			reSetCaseNo = table3.getParmValue().getValue("CASE_NO", row); // 医保退挂使用
			insType = table3.getParmValue().getValue("INS_PAT_TYPE", row); // 医保就诊类型1.城职普通2.城职门特
			// 3.城居门特
			if (null != confirmNo && confirmNo.length() > 0) {
				// 执行医保操作
				// System.out.println("医保卡退费");
			} else {
				reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // 医疗卡退费查询
				if (reSetEktParm.getErrCode() < 0) {
					this.messageBox("退挂执行有误");
					return;
				}
				if (reSetEktParm.getCount() > 0) { // 如果存在但是没有获得医疗卡信息，提示==pangb
					// 2011-11-29
					String payWay = this.getValueString("PAY_WAY");
					if (!"EKT".equals(payWay)) {
						this.messageBox("请读取医疗卡信息");
						return;
					}
				}
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("RECEIPT_TYPE", "REG");
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				parm.setData("REGION_CODE", Operator.getRegion());

			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					parm);
			// 查询是否有记账信息
			if (null != result && result.getCount() > 0) {
				// 已经结算完成的挂号费
				if ("2".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes2(caseNo, true);
				} else if ("1".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes1(caseNo);
				}
				// 正常退挂
			} else {
				onUnRegNo(caseNo, false);
			}
			
			// add by wangbin 20180206 退挂成功后更新临时表的取消状态 START
			this.cancelMroRegAppointment(caseNo);
			// add by wangbin 20180206 退挂成功后更新临时表的取消状态 END
			
			this.onClear();
		} else
			return;

	}

	/**
	 * 二代身份证
	 */
	public void idnoInfo() {
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfoFromID.x");
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();

	}

	/**
	 * 泰心医疗卡扣款操作
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	private boolean onTXEktSave(String FLG, TParm insParm) {
		int type = 0;
		TParm parm = new TParm();
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (EKTIO.getInstance().ektSwitch()) { // 医疗卡开关，记录在后台config文件中
			if (null == insParm)
				parm = onOpenCard(FLG);
			else
				parm = onOpenCard(FLG, insParm);
			// System.out.println("打开医疗卡parm=" + parm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			// System.out.println("type===" + type);
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == 2) {
				return false;
			}
			if (type == -1) {
				this.messageBox("读卡错误!");
				return false;
			}
			tredeNo = parm.getValue("TREDE_NO");
			businessNo = parm.getValue("BUSINESS_NO"); // //出现医疗卡扣款操作问题使用
			ektOldSum = parm.getValue("OLD_AMT"); // 执行失败撤销的金额
			ektNewSum = parm.getValue("EKTNEW_AMT"); // 扣款以后的金额
			// 判断是否操作绿色通道
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				greenParm = parm;
			}
			// System.out.println("ektNewSum======"+ektNewSum);
		} else {
			this.messageBox_("医疗卡接口未开启");
			return false;
		}
		return true;

	}

	/**
	 * 医疗卡保存
	 * 
	 * @param FLG
	 *            String
	 * @return boolean
	 */
	public boolean onEktSave(String FLG) {
		if (aheadFlg) {
			return onTXEktSave(FLG, null);
		}
		return true;
	}

	/**
	 * 打开医疗卡
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG) {
		if (reg == null) {
			return null;
		}
		// 准备送入医疗卡接口的数据
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("INS_FLG", "N");
		// 医保出现问题现金收取
		reg.setInsPatType(""); // 就诊医保类型 需要保存到REG_PATADM数据库表中1.城职普通 2.城职门特
		// 3.城居门特
		// 送医疗卡，返回医疗卡的回传值
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))); // 医疗卡已经收费的数据
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);

		return parm;
	}

	/**
	 * 天津医保
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG, TParm insParm) {
		// 准备送入医疗卡接口的数据
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
		orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
		orderParm.setData("INS_FLG", "Y"); // 医保卡注记
		orderParm.setData("OPBEKTFEE_FLG", true);// 取消按钮
		orderParm.setData("RECP_TYPE", "REG"); // 添加EKT_ACCNTDETAIL 表数据使用
		orderParm.setData("comminuteFeeParm", insParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割返回参数
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 此病患所有收费医嘱包括已经打票的
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 显示金额
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// 送医疗卡，返回医疗卡的回传值
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);
		return parm;
	}

	/**
	 * 医疗卡入参
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // 写固定值
		orderParm.addData("ORDER_CODE", "REG"); // 写固定值
		orderParm.addData("SEQ_NO", "1"); // 写固定值
		orderParm.addData("EXEC_FLG", "N"); // 写固定值
		orderParm.addData("RECEIPT_FLG", "N"); // 写固定值
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}

	/**
	 * 退挂操作医疗卡退费操作
	 * 
	 * @param caseNo
	 * @param type
	 *            1.正常医疗卡退费 2.医保卡退费
	 * @return
	 */
	public TParm onOpenCardR(String caseNo) {
		// 准备送入医疗卡接口的数据
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // 写固定值
		orderParm.addData("ORDER_CODE", "REG"); // 写固定值
		orderParm.addData("SEQ_NO", "1"); // 写固定值
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.addData("EXEC_FLG", "N"); // 写固定值
		orderParm.addData("RECEIPT_FLG", "N"); // 写固定值
		orderParm.addData("BILL_FLG", "N");
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("BUSINESS_TYPE", "REGT");
		orderParm.setData("TYPE_FLG", "Y");
		if (null != confirmNo && confirmNo.length() > 0) {
			orderParm.setData("OPBEKTFEE_FLG", true);
		}
		orderParm.setData("ektParm", p3.getData());
		// 查询此病患已收费未打票的所有数据汇总金额
		TParm parm = new TParm();
		parm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		parm.setData("CASE_NO", caseNo);
		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(parm);
		orderParm.setData("EXE_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0)); // 医疗卡已经收费的数据
		orderParm.setData("SHOW_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0));
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// System.out.println("MR_NO" + pat.getMrNo());
		// System.out.println("退挂传入金额"+TypeTool.getDouble(getValue("FeeY")));
		// 送医疗卡，返回医疗卡的回传值
		parm = EKTIO.getInstance().onOPDAccntClient(orderParm, caseNo, this);
		return parm;
	}

	/**
	 * 泰心医疗卡读卡 =========================pangben modify 20110808
	 * 
	 * @throws ParseException
	 */
	public void onEKT() throws ParseException {
		// 南京医保卡读卡操作
		// 泰心医疗卡操作
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.释放读卡设备
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.注销TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // 医疗卡类型
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // 南京医保卡
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // 泰心医疗卡
			this.setValue("PAY_WAY", "EKT"); // 支付方式修改
			this.setValue("CONTRACT_CODE", "");
			// callFunction("UI|CONTRACT_CODE|setEnabled", false); // 记账单位不可编辑
		}
		// 通过身份证号查找是否存在此病患信息
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//其他证件不可编辑
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("此医疗卡无效");
			return;
		}
		// 南京医保卡操作
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// add by huangtt 20140115 start
		this.setValue("EKTMR_NO", p3.getValue("MR_NO"));
		String EKTCARD_CODE = p3.getData("CARD_NO").toString();
		this.setValue("EKTCARD_CODE", EKTCARD_CODE);
		this.setValue("CURRENT_BALANCE", p3.getValue("CURRENT_BALANCE"));
		// add by huangtt 20140115 end

		// ===zhangp 20120318 end

		// //add by huangtt 20140109 设置会员卡类型 =======start=======
		// String cardTypeSql =
		// "SELECT CTZ_CODE FROM SYS_CTZ WHERE MEM_CODE = '"+p3.getValue("CARD_TYPE")+"' AND DEPT_CODE IS NULL";
		// TParm cardParm = new
		// TParm(TJDODBTool.getInstance().select(cardTypeSql));
		// if(cardParm.getCount()>0){
		// setValue("REG_CTZ1", cardParm.getValue("CTZ_CODE", 0));
		// }
		// //add by huangtt 20140109 设置会员卡类型 ========end========
		//		
	}
	
	/**
     * 身份证读卡回传值add by huangjw 20141110
     * @param parm
     */
    public void setValueByQueryIdNo(TParm parm){
    	if(this.getValueString("MR_NO").equals(parm.getValue("MR_NO"))){
    		//TParm p = this.getParmForTag("MR_NO;PY1");
    		clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
    		this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
    	}else{
    		//this.setValue("MR_NO",parm.getValue("MR_NO"));
    		onQueryNO(parm.getValue("MR_NO", 0));
    		clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
    		this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
    	}
		
	}
	
	/**
	 * 身份正读卡操作 ==============pangben 2013-3-18
	 */
	public void onReadIdCard() {
		if (JOptionPane.showConfirmDialog(null, "是否覆盖？", "信息",
				JOptionPane.YES_NO_OPTION) == 0) {// 读身份证时，加提示信息，add by huangjw 20141119
			TParm idParm = IdCardO.getInstance().readIdCard();
			if (idParm.getErrCode() < 0) {
				this.messageBox(idParm.getErrText());
				return;
			}
			if (idParm.getCount() > 0) {// 多行数据显示
				if (idParm.getCount() == 1) {// pangben 2013-8-8 只存在一条数据
					// onQueryNO(idParm.getValue("MR_NO", 0));
					setValueByQueryIdNo(idParm);// modify by huangjw 20141110
					onAddress(); // add by huangtt 20131122
									// 判断现住址是否为空，若为空，则同步详细地址
				} else {
					Object obj = openDialog(
							"%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
					TParm patParm = new TParm();
					if (obj != null) {
						patParm = (TParm) obj;
						setValueByQueryIdNo(patParm);// modify by huangjw 20141110
						// onQueryNO(patParm.getValue("MR_NO"));
					} else {
						return;
					}
				}
				setValue("VISIT_CODE_F", "Y"); // 复诊
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// 简拼
				setPatName1();// 设置英文
			} else {
				String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
						+ idParm.getValue("PAT_NAME") + "%'";
				TParm infoParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (infoParm.getCount() <= 0) {
					this.messageBox(idParm.getValue("MESSAGE"));
					setValue("VISIT_CODE_C", "Y"); // 默认初诊
					callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑--初诊操作
				} else {
					this.messageBox("存在相同姓名的病患信息");
					this.grabFocus("PAT_NAME");// 默认选中
				}
				this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
				this.setValue("IDNO", idParm.getValue("IDNO"));
				this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
				this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
				this
						.setValue("RESID_ADDRESS", idParm
								.getValue("RESID_ADDRESS"));// 地址
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// 简拼
				setPatName1();// 设置英文
			}
		}
	}

	/**
	 * 医疗卡读卡操作
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 * @throws ParseException
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) throws ParseException {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// 通过身份证号查找是否存在次病患
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // 存在将病案号显示
			onQueryNO(); // 执行赋值方法
			setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			// modify
			// 20110808
			tjINS = true; // 天津医保使用，判断是否执行了医疗卡操作
			callFunction("UI|PAY_WAY|setEnabled", false); // 支付类别
		} else {
			this.messageBox("此医疗卡无效"); // 不存在显示市民卡上的信息：身份证号、名称、医保号
			switch (typeEKT) {
			// 南京医保卡 没有此病患信息时执行赋值操作
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // 身份证号
				this.setValue("NHI_NO", p3.getValue("siNO")); // 医保号
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // 姓名
				break;
			// 泰心医疗卡没有此病患信息时执行赋值操作
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // 泰心医疗卡写卡操作管控
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑
			this.grabFocus("PAT_NAME");
			setValue("VISIT_CODE_C", "Y"); // 默认初诊
		}
	}

	/**
	 * 医疗卡读卡
	 */
	public void TXonEKTR() {
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("此医疗卡无效");
			return;
		}
		if (null != p && p.getValue("MR_NO").length() > 0) {
			// zhangp 20111231 修改医疗卡号
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		// zhangp 20111227
		clearEKTValue();
	}

	/**
	 * 充值操作
	 * 
	 * @throws ParseException
	 */
	public void TXonEKTW() throws ParseException {
		if (this.getValueDouble("TOP_UPFEE") <= 0) {
			this.messageBox("充值金额不正确");
			return;
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
			this.messageBox("支付方式不可以为空值");
			return;
		}
		if("WX".equals(this.getValue("GATHER_TYPE"))||"ZFB".equals(this.getValue("GATHER_TYPE"))){
			if(this.getValueString("EKT_PRINT_NO").trim().length()<=0){
				this.messageBox("微信或支付宝需要在票据号添加交易号码！");
				return;
			}
		}
		
		
		// add by huangtt 20140228
		if (this.messageBox("充值", "是否充值" + this.getValueDouble("TOP_UPFEE")
				+ "元", 0) != 0) {
			return;
		}
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("此医疗卡无效");
			return;
		}
		// zhangp 20111227
		pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		TParm parm = new TParm();
		parm.setData("SEQ", p.getValue("SEQ")); // 编号
		parm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2)); // 金额
		parm.setData("MR_NO", p.getValue("MR_NO")); // 病案号

		if (null != p && p.getValue("MR_NO").length() > 0) {
			// result.setData("CURRENT_BALANCE",
			// this.getValue("CURRENT_BALANCE"));
			// yanjing 注
			// TParm result = EKTIO.getInstance().TXwriteEKTATM(parm,
			// p.getValue("MR_NO"));
			// if (result.getErrCode() < 0) {
			// this.messageBox_("医疗卡充值操作失败");
			// return;
			// }
			insbilPay(parm, p);
		} else {
			this.messageBox("此医疗卡无效");
		}
		clearEKTValue();
		// =====zhangp 20120403 start
		onEKT();
	}

	/**
	 * 医疗卡充值操作
	 * 
	 * @param parm
	 *            TParm
	 * @param p
	 *            TParm
	 */
	private void insbilPay(TParm parm, TParm p) {
		// zhangp 20111227
		TParm result = new TParm();
		parmSum = new TParm();
		parmSum.setData("CARD_NO", pat.getMrNo() + p.getValue("SEQ"));
		parmSum.setData("CURRENT_BALANCE", parm.getValue("CURRENT_BALANCE"));
		parmSum.setData("CASE_NO", "none");
		parmSum.setData("NAME", pat.getName());
		parmSum.setData("MR_NO", pat.getMrNo());
		parmSum.setData("ID_NO", null != pat.getIdNo()
				&& pat.getIdNo().length() > 0 ? pat.getIdNo() : "none");
		parmSum.setData("OPT_USER", Operator.getID());
		parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmSum.setData("OPT_TERM", Operator.getIP());
		parmSum.setData("FLG", false);
		parmSum.setData("ISSUERSN_CODE", "充值"); // 发卡原因
		parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); // 支付方式
		parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); // 支付方式名称
		parmSum.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); // 性别
		parmSum.setData("CARD_TYPE", this.getValue("CARD_TYPE")); // 备注
		parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); // 备注
		parmSum.setData("PRINT_NO", this.getValue("EKT_PRINT_NO")); // 医疗卡票据号
		parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); // 票据号
		parmSum.setData("CREAT_USER", Operator.getID()); // 执行人员//=====yanjing
		// 明细表参数
		TParm feeParm = new TParm();
		feeParm.setData("ORIGINAL_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)); // 原金额
		feeParm.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		feeParm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		parmSum.setData("TRADE_NO",this.getValue("DESCRIPTION"));
		// EKT_ACCNTDETAIL 数据
		parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm)
				.getData());
		// zhangp 20120112 EKT_BIL_PAY 加字段
		parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime()); // 售卡操作时间
		parmSum.setData("PROCEDURE_AMT", 0.00); // PROCEDURE_AMT
		// bil_pay 充值表数据
		parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
		// 更新余额
		result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"TXEKTonFee", parmSum); //
		callFunction("UI|tButton_5|setEnabled", false);// 充值按钮不可以连续点击操作===pangben
		// 2013-7-1
		if (result.getErrCode() < 0) {
			this.messageBox("医疗卡充值失败");
			callFunction("UI|tButton_5|setEnabled", true);// 充值按钮不可以连续点击操作===pangben
			// 2013-7-1
			// parm = EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
			// if (parm.getErrCode() < 0) {
			// System.out.println("回冲医疗卡金额失败");
			// }
		} else {
			printBil = true;
			this.messageBox("医疗卡充值成功");
			callFunction("UI|tButton_5|setEnabled", true);// 充值按钮不可以连续点击操作===pangben
			// 2013-7-1
			String bil_business_no = result.getValue("BIL_BUSINESS_NO"); // 收据号
			try {
				onPrint(bil_business_no, "");
				this.clearValue("EKT_PRINT_NO;EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;DESCRIPTION");
				this.setValue("TOP_UPFEE", 0.00);
				this.setValue("CURRENT_BALANCE", 0.00);
				this.setValue("SUM_EKTFEE", 0.00);
			} catch (Exception e) {
				this.messageBox("打印出现问题,请执行补印操作");
				// TODO: handle exception
			}
		}
	}

	/**
	 * 写医疗卡
	 */
	public void writeCard() {
	}

	/**
	 * 指定交易信息
	 */
	public void queryConusmeByID() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("无卡");

			return;
		}
		result = EktDriver.queryConusmeByID("1008250000000021");
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * 冲证
	 */
	public void unConsume() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("无卡");

			return;
		}
		result = EktDriver.unConsume(1000, "sys", "1008250000000021",
				StringTool.getString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * 医疗卡条码
	 */
	public void onEKTBarcode() {
		TParm printParm = new TParm();
		if ((ektCard != null || ektCard.length() != 0)
				&& this.getValueString("MR_NO") != null) {
			printParm.setData("mrNo", "TEXT", this.getValueString("MR_NO")); // 病案号
			printParm.setData("patName", "TEXT", this
					.getValueString("PAT_NAME")); // 病患姓名
			printParm.setData("barCode", "TEXT", ektCard); // 条码号
			this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGEktCard.jhw",
					printParm);
		} else {
			this.messageBox("请先读医疗卡");
		}

	}

	/**
	 * 设置SESSION combo的门急属性，并返回当前的SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// 为了界面的SESSION_CODE显示门急诊区别，放置一个不显示的TEXTFIELD。
		String sessionCode = SessionTool.getInstance().getDefSessionNow_New(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * 清卡 ===================pangben modify 20110808
	 */
	public void clearCard() {
		// EKTIO.getInstance().saveMRNO1(parm, this,true);
		if (null == p3) {
			this.messageBox("没有需要清卡的数据");
			return;
		}

		p3.setData("identifyNO", this.getValue("IDNO"));
		p3.setData("siNO", this.getValue("NHI_NO"));
		p3.setData("patientName", this.getValue("PAT_NAME"));
		boolean temp = EKTIO.getInstance().writeEKT(p3, true);
		if (temp) {
			// 修改将此病患医保卡号清空
			StringBuffer sql = new StringBuffer();
			sql
					.append("UPDATE SYS_PATINFO SET NHI_NO='',OPT_DATE=SYSDATE WHERE MR_NO='"
							+ this.getValueString("MR_NO").trim() + "'");
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sql.toString()));
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				this.messageBox("清卡失败");
				return;
			}
			this.messageBox("清卡成功");
		}
	}

	public void onClearRefresh() {
		this.initReg();
		clearValue(" PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;RESID_ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;INSURE_INFO;PAT_PACKAGE");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		crmTime="";
		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
	}

	/**
	 * 控件可编辑设置
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setControlEnabled(boolean flg) {
		// callFunction("UI|REGMETHOD_CODE|setEnabled", flg);
		callFunction("UI|ADM_DATE|setEnabled", flg);
		callFunction("UI|SESSION_CODE|setEnabled", flg);
		callFunction("UI|DEPT_CODE|setEnabled", flg);
		callFunction("UI|DR_CODE|setEnabled", flg);
		callFunction("UI|CLINICROOM_NO|setEnabled", flg);
		if(flg){
			callFunction("UI|CLINICTYPE_CODE|setEnabled", flg);
		}
		callFunction("UI|REG_FEE|setEnabled", flg);
		callFunction("UI|CLINIC_FEE|setEnabled", flg);
		
	}

	/**
	 * 获得费用
	 */
	public void showXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		feeShow = true;
		// String
		// mr_no=parm.getValue("TBR").trim().substring(1,parm.getValue("TBR").trim().indexOf("]"));

		// System.out.println("parm:::"+parm);
		// if(this.getValueString("MR_NO").trim().equals(mr_no)){
		if (null == parm)
			return;
		// feeIstrue = true;
		this.setValue("FeeY", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]"))); // 收费
		this.setValue("REG_FEE", parm.getValue("GHF").substring(1,
				parm.getValue("GHF").indexOf("]"))); // 挂号费
		this.setValue("CLINIC_FEE", parm.getValue("ZLF").substring(1,
				parm.getValue("ZLF").indexOf("]"))); // 诊查费
		this.setValue("FeeS", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]")));
		// }
	}

	/**
	 * 退挂金额显示 医保中心获得的价格显示 =====================pangben modify 20110815
	 * 
	 * @param caseNo
	 *            String
	 */
	private void unregFeeShow(String caseNo) {
		int feeunred = -1;
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT REG_FEE,CLINIC_FEE,AR_AMT FROM BIL_REG_RECP WHERE CASE_NO='"
						+ caseNo + "'"); // 获得退挂的金额
		// System.out.println("sql:::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		this.setValue("FeeY", result.getDouble("AR_AMT", 0) * feeunred); // 总费用
		this.setValue("REG_FEE", result.getDouble("REG_FEE", 0) * feeunred); // 挂号
		this.setValue("CLINIC_FEE", result.getDouble("CLINIC_FEE", 0)
				* feeunred); // 诊疗
		// add by huangtt 20140115
		if (aheadFlg) {
			this.setValue("FeeS", result.getDouble("AR_AMT", 0) * feeunred); // 收取费用
		}

	}

	/**
	 * 正常流程没有记账的操作 flg 判断是否是记账数据
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegNo(String caseNo, boolean flg) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm unRegParm = new TParm();

		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", optUser);

		// 查询当前病患是否产生费用
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		inInvRcpParm.setData("RECP_TYPE", "REG");// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			//查询该挂号数据是否已经退挂了 add by huangtt 20151224 start
			boolean isDedug=true; //add by huangtt 20160505 日志输出
			String sql = "SELECT REGCAN_USER FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
			TParm unParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(unParm.getValue("REGCAN_USER", 0).length() > 0){
				this.messageBox("该就诊记录已退挂！");
				return;
			}
			if(isDedug){
				if(unParm.getErrCode() < 0){
					System.out.println(" come in class: REGPatAdmControl.class ，method ：onUnRegNo");
					System.out.println("err:"+unParm);

				}
			}
			//add by huangtt 20151224 end  
			reSetReg(unRegParm, caseNo, flg, "onUnRegForEKT", "onUnReg", "Y");
		} else {
			this.messageBox("已产生费用,不能退挂!");
			return;
		}
	}

	/**
	 * 记账退挂操作:BIL_STATUS=2 已经结算退挂操作
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegYes2(String caseNo, boolean flg) {
		onUnRegNo(caseNo, flg);
	}

	/**
	 * 记账退挂操作:BIL_STATUS=1 判断是否产生费用，如果没有产出费用直接添加、修改操作BIL_REG_RECP 如果已经产生费用不可以退挂
	 * 
	 * @param caseNo
	 *            String
	 */
	private void onUnRegYes1(String caseNo) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", Operator.getID());
		TParm unRegParm = new TParm();
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("RECP_TYPE", "REG");
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		unRegParm.setData("RECEIPT_NO", recpNo);
		unRegParm.setData("OPT_NAME", Operator.getName());
		// 查询当前病患是否产生费用
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			// 没有执行结算的费用不用退挂
			this.messageBox("没有执行结算,不用退费");
			// 直接添加、修改操作BIL_REG_RECP
			// 现金退挂动作
			reSetReg(unRegParm, caseNo, false, "onUnRegForStatusEKT",
					"onUnRegStatus", "Y");
		} else {
			// 已产生费用
			this.messageBox("已产生费用,不能退挂!");
		}

	}

	/**
	 * 读卡
	 */
	public void readINSCard() {
		String payWay = this.getValueString("PAY_WAY");// 支付方式
		// 天津医保卡操作
		tjReadINSCard(payWay);
	}

	/**
	 * 医疗卡保存
	 * 
	 * @return boolean
	 */
	public boolean onSaveINSData() {
		boolean result = false;
		return result;
	}

	/**
	 * 清空医疗卡信息
	 */
	public void ektOnClear() {
		clearValue("EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;TOP_UPFEE;SUM_EKTFEE;DESCRIPTION;CARD_TYPE");
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
	}

	/**
	 * 门诊挂号收据打印
	 * 
	 * @param parm
	 *            TParm
	 * 
	 */
	private void onPrint(TParm parm) {
		// //处理小数
		// sOTOT_Amt = ""+ TiMath.round( Double.parseDouble(sOTOT_Amt),2);

		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
		// 显示方式:科室(诊室)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "号)"); // 号别
		// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		parm.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		DecimalFormat df = new DecimalFormat("########0.00");
		// parm.setData("CURRENT_BALANCE", "TEXT", "￥ "
		// + df.format(Double.parseDouble(ektNewSum == null
		// || "".equals(ektNewSum) ? "0.00" : ektNewSum))); // 医疗卡剩余金额
		parm
				.setData(
						"CURRENT_BALANCE",
						"TEXT",
						"￥ "
								+ df
										.format(Double
												.parseDouble(ektNewSum == null
														|| "".equals(ektNewSum) ? ""
														+ df
																.format((Double
																		.parseDouble(getValueString(
																				"EKT_CURRENT_BALANCE")
																				.equals(
																						"") ? "0"
																				: getValueString("EKT_CURRENT_BALANCE"))
																		- parm
																				.getDouble(
																						"TEXT",
																						"REGFEE") - parm
																		.getDouble(
																				"TEXT",
																				"CLINICFEE")))
														: ektNewSum))); // 医疗卡剩余金额
		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_DEBIT", "TEXT", "医保:"
					+ StringTool.round((parm.getDouble("INS_SUMAMT") - parm
							.getDouble("ACCOUNT_AMT_FORREG")), 2)); // 医保支付
			parm.setData("PAY_CASH", "TEXT", "现金:"
					+ StringTool.round((parm.getDouble("TOTAL", "TEXT") - parm
							.getDouble("INS_SUMAMT")), 2)); // 现金
			parm
					.setData("PAY_ACCOUNT", "TEXT", "账户:"
							+ StringTool.round(parm
									.getDouble("ACCOUNT_AMT_FORREG"), 2)); // 账户
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ insParm.getParm("opbReadCardParm").getValue(
							"SP_PRESON_TYPE") + "'";// 医保特殊人员身份显示
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		// ===zhangp 20120313 start
		if ("1".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			// parm.setData("Cost_class", "TEXT", "门统");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(insType) || "3".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			// parm.setData("Cost_class", "TEXT", "门特");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
		// ===zhangp 20120313 end
		parm.setData("RECEIPT_NO", "TEXT", reg.getRegReceipt().getReceiptNo());// add
		// by
		// wanglong
		// 20121217
		// this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
		// parm, true);
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REGRECPPrint.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
		this.openPrintDialog(IReportTool.getInstance().getReportPath(
				"REGRECPPrint.jhw"), IReportTool.getInstance().getReportParm(
				"REGRECPPrint.class", parm), true);// 报表合并modify by wanglong
		}
		// 20130730
	}

	/**
	 * 天津医保卡读卡操作
	 * 
	 * @param payWay
	 *            String
	 */
	private void tjReadINSCard(String payWay) {
		// yanjing 删除对SERVICE_LEVEL的清空 20130807
		clearValue("REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ ");
		initSchDay();
		if (null == pat && !this.getValueBoolean("VISIT_CODE_C")) {
			this.messageBox("请先获得病患信息");
			return;
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院,5 :门特登记）
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm) {
			this.setValue("PAY_WAY", payWay); // 支付方式修改
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			this.setValue("PAY_WAY", payWay); // 支付方式修改
			return;
		}

		int crowdType = insParm.getInt("CROWD_TYPE"); // 医保就医类别 1.城职 2.城居
		insType = insParm.getValue("INS_TYPE"); // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特
		// ============pangben 2012-4-8 查询数据是否存在医保校验
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (this.getValueBoolean("VISIT_CODE_C")
				&& this.getValue("MR_NO").toString().trim().length() <= 0) {// 初诊获得医保数据
			this.setValue("PAT_NAME", name);
			this.setValue("IDNO", opbReadCardParm.getValue("SID").trim());
			this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // 医保卡号
			// ========pangben 2013-3-5 添加初诊病人病患信息带入
			setPatName1();
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// 简拼
			// 每次刷卡，要求门特联网系统根据“门特登记结束时间”与当前时间进行比较
			if (!insType.equals("1")) {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTH_DATE") ? opbReadCardParm.getValue(
						"BIRTH_DATE").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(6, 8) : "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX_CODE"));
			} else {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTHDAY") ? opbReadCardParm.getValue(
						"BIRTHDAY").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(6,
								opbReadCardParm.getValue("BIRTHDAY").length())
						: "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX"));
			}
			return;
		}
		if (insPresonParm.getErrCode() < 0) {
			this.messageBox("获得病患信息失败");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("此医保病患不存在医疗卡信息,\n医保信息:身份证号码:"
					+ opbReadCardParm.getValue("SID") + "\n医保病患名称:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		} else if (insPresonParm.getCount("MR_NO") > 1) {
			int flg = -1;
			if (this.getValue("MR_NO").toString().length() > 0) {
				for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
					if (insPresonParm.getValue("MR_NO", i).equals(
							this.getValue("MR_NO"))) {
						flg = i;
						break;
					}
				}
				if (flg == -1) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
			// onPatName();
		}
		// ===================pangben 2012-04-09医保管控添加
		// 每次刷卡，要求门特联网系统根据“门特登记结束时间”与当前时间进行比较
		if (!insType.equals("1")) {

			// 您的门特登记有效期至X年X月X日，请在此时间前2个月内到糖尿病鉴定中心办理复查认定
			String mtEndDate = opbReadCardParm.getValue("MT_END_DATE");// 门特登记结束时间
			this.messageBox("您的门特登记有效期至" + mtEndDate
					+ "，请在此时间前2个月内到糖尿病鉴定中心办理复查认定");
		}
		// ============pangben 2012-4-9 stop
		// 判断人群类别
		// 与身份折扣对照赋值
		// 11：城职普通 ,11:医保号\ 12：城职退休,21:医保号 \13：城职离休,51:医保号
		// 21:城居新生儿 ,11:医保号\22:城居学生儿童 12:医保号 \23：城居成年居民,13:医保号
		this.setValue("REG_CTZ1", insParm.getValue("CTZ_CODE"));
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// 过滤数据
		combo_ctz.setNhiFlg(crowdType + "");
		combo_ctz.onQuery();
		insFlg = true; // 医保卡读取成功
		callFunction("UI|REG_CTZ1|setEnabled", true); // 身份类别
		callFunction("UI|PAY_WAY|setEnabled", false); // 支付类别
		this.setValue("PAY_WAY", "INS"); // 支付方式修改
		this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // 医保卡号
		this.grabFocus("FeeS");
	}

	/**
	 * 泰心医院医保卡保存操作
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm TXsaveINSCard(TParm parm, String caseNo) {
		// 没有获得医疗卡信息 判断是否执行现金收费
		if (!tjINS && !insFlg) {
			if (this.messageBox("提示", "没有获得医疗卡信息,执行现金收费是否继续", 2) != 0) {
				return null;
			}
		}
		if (tjINS) { // 医疗卡操作
			if (p3.getDouble("CURRENT_BALANCE") < this.getValueDouble("FeeY")) {
				this.messageBox("医疗卡金额不足,请充值");
				return null;
			}
		}
		TParm result = new TParm();
		insParm.setData("REG_PARM", parm.getData()); // 医嘱信息
		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // 科室代码
		insParm.setData("MR_NO", pat.getMrNo()); // 病患号

		reg.setCaseNo(caseNo);
		insParm.setData("RECP_TYPE", "REG"); // 类型：REG / OPB
		insParm.setData("CASE_NO", reg.caseNo());
		insParm.setData("REG_TYPE", "1"); // 挂号标志:1 挂号0 非挂号
		insParm.setData("OPT_USER", Operator.getID());
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("DR_CODE", this.getValue("DR_CODE"));// 医生代码
		// insParm.setData("PAY_KIND", "11");// 4 支付类别:11门诊、药店21住院//支付类别12、
		if (this.getValueString("ERD_LEVEL").length() > 0) {
			insParm.setData("EREG_FLG", "1"); // 急诊
		} else {
			insParm.setData("EREG_FLG", "0"); // 普通
		}

		insParm.setData("PRINT_NO", this.getValue("NEXT_NO")); // 票号
		insParm.setData("QUE_NO", reg.getQueNo());

		TParm returnParm = insExeFee(true);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
		if (returnType == 0 || returnType == -1) { // 取消操作
			return null;
		}

		insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // 费用结算

		// System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(insParm.getData());

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// this.messageBox("医保执行操作失败");
			return result;
		}
		// System.out.println("医保操作出参:" + insParm);
		// boolean messageFlg = false; // 医保金额问题 执行现金收款
		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		result.setData("ACCOUNT_AMT_FORREG", returnParm
				.getDouble("ACCOUNT_AMT_FORREG")); // 账户金额
		insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		if (tjINS) { // 医疗卡操作
			// TParm insExeParm = insExe(returnParm.getDouble("ACCOUNT_AMT"),
			// p3,
			// reg.caseNo(), "REG", 9);
			// if (insExeParm.getErrCode() < 0) {
			// return insExeParm;
			// }
			// 执行医疗卡扣款操作：需要判断医保金额与医疗卡金额
			if (!onTXEktSave("Y", result)) {
				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
						"deleteOldData", insParm);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					result.setErr(-1, "医保卡执行操作失败");
					// return result;
				}
				result.setErr(-1, "医疗卡执行操作失败");
				return result;
			}
			// result = new TParm();// 执行添加数据REG_PATADM
		}
		return result;
	}

	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(boolean flg) {
		TParm insFeeParm = new TParm();
		if (flg) {
			insFeeParm.setData("insParm", insParm.getData()); // 医嘱信息
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		} else {
			insFeeParm.setData("CASE_NO", reSetCaseNo); // 退挂使用
			insFeeParm.setData("INS_TYPE", insType); // 退挂使用
			insFeeParm.setData("RECP_TYPE", "REG"); // 退挂使用
			insFeeParm.setData("CONFIRM_NO", confirmNo); // 退挂使用
		}
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // 病患号

		insFeeParm.setData("FeeY", this.getValueDouble("FeeY")); // 应收金额
		insFeeParm.setData("PAY_TYPE", tjINS); // 支付方式
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // 区域代码
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = new TParm();
		if (flg) { // 正流程
			// returnParm=INSTJReg.getInstance().onInsFee(insFeeParm, this);
			returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
					insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")
					|| returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// 退费流程
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// 医保金额
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// 城职
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - returnIns
							.getDouble("UNACCOUNT_PAY_AMT")), 2);
					this.messageBox("医保退费金额:"
							+ accountAmt
							+ " 现金退费金额:"
							+ StringTool.round(returnIns
									.getDouble("UNACCOUNT_PAY_AMT"), 2));

				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// 城居
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ARMY_AI_AMT");// 现金金额
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);

					this.messageBox("医保退费金额:" + accountAmt + " 现金退费金额:"
							+ StringTool.round(payAmt, 2));
				}

				returnParm.setData("RETURN_TYPE", 1); // 执行成功
				returnParm.setData("ACCOUNT_AMT", accountAmt);// 医保金额
			}

		}
		return returnParm;
	}

	/**
	 * 医保执行退费操作
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * 记账操作：支付方式设置记账
	 */
	public void contractSelect() {

		if (this.getValue("CONTRACT_CODE").toString().length() > 0) {
			this.setValue("PAY_WAY", "C4"); // 记账

		} else {
			this.setValue("PAY_WAY", "C0"); // 现金
		}
	}

	/**
	 * 点击办医疗卡 zhangp 20121216
	 */
	public void ektCard() {
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", sendParm);
	}

	/**
	 * 医疗卡明细表插入数据==============zhangp 20111227
	 * 
	 * @param p
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBusinessParm(TParm p, TParm feeParm) {
		// 明细档数据
		TParm bilParm = new TParm();
		bilParm.setData("BUSINESS_SEQ", 0);
		bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
		bilParm.setData("MR_NO", pat.getMrNo());
		bilParm.setData("CASE_NO", "none");
		bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("SEQ_NO", 0);
		bilParm.setData("CHARGE_FLG", "3"); // 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
		bilParm.setData("ORIGINAL_BALANCE", feeParm
				.getValue("ORIGINAL_BALANCE")); // 收费前余额
		bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
		bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
		bilParm.setData("CASHIER_CODE", Operator.getID());
		bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
		// 1：交易执行完成
		// 2：双方确认完成
		bilParm.setData("BUSINESS_STATUS", "1");
		// 1：未对帐
		// 2：对账成功
		// 3：对账失败
		bilParm.setData("ACCNT_STATUS", "1");
		bilParm.setData("ACCNT_USER", new TNull(String.class));
		bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
		bilParm.setData("OPT_USER", Operator.getID());
		bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		bilParm.setData("OPT_TERM", Operator.getIP());
		// p.setData("bilParm",bilParm.getData());
		return bilParm;
	}

	/**
	 * 充值档添加数据参数==============zhangp 20111227
	 * 
	 * @param parm
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBillParm(TParm parm, TParm feeParm) {
		TParm billParm = new TParm();
		billParm.setData("CARD_NO", parm.getValue("CARD_NO")); // 卡号
		billParm.setData("CURT_CARDSEQ", 0); // 序号
		billParm.setData("ACCNT_TYPE", "4"); // 明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
		billParm.setData("MR_NO", parm.getValue("MR_NO")); // 病案号
		billParm.setData("ID_NO", parm.getValue("ID_NO")); // 身份证号
		billParm.setData("NAME", parm.getValue("NAME")); // 病患名称
		billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); // 充值金额
		billParm.setData("CREAT_USER", Operator.getID()); // 执行人员
		billParm.setData("OPT_USER", Operator.getID()); // 操作人员
		billParm.setData("OPT_TERM", Operator.getIP()); // 执行ip
		billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); // 支付方式
		// 20120112 zhangp 加字段
		billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
		billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
		return billParm;
	}

	/**
	 * 充值打印==============zhangp 20111227
	 * 
	 * @param bil_business_no
	 *            String
	 * @param copy
	 *            String
	 */
	private void onPrint(String bil_business_no, String copy) {
		if (!printBil) {
			this.messageBox("进行医疗卡充值操作才可以打印");
			return;
		}
		boolean flg = false;
		TTable table = new TTable();
		parmSum.setData("TITLE", "门诊充值收据");
		parmSum.setData("UnFeeFLG", "N");
		parmSum.setData("ACOUNT_NO", "");
		parmSum.setData("BIL_BUSINESS_NO", bil_business_no);
		EKTReceiptPrintControl.getInstance().onPrint(table, parmSum, copy, -2,
				pat, flg, this);
		// TParm parm = new TParm();
		// parm.setData("TITLE", "TEXT", (Operator.getRegion() != null
		// && Operator.getRegion().length() > 0 ? Operator
		// .getHospitalCHNFullName() : "所有医院"));
		// parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // 病案号
		// parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // 姓名
		// parm.setData("GATHER_TYPE", "TEXT", parmSum
		// .getValue("GATHER_TYPE_NAME")); // 收款方式
		// parm.setData("AMT", "TEXT", StringTool.round(parmSum
		// .getDouble("BUSINESS_AMT"), 2)); // 金额
		// // ====zhangp 20120525 start
		// // parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
		// parm.setData("GATHER_NAME", "TEXT", ""); // 收款方式
		// // ====zhangp 20120525 end
		// parm.setData("TYPE", "TEXT", "预 收"); // 文本预收金额
		// parm.setData("SEX_TYPE", "TEXT", pat.getSexCode().equals("1") ? "男"
		// : "女"); // 性别
		// parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(
		// parmSum.getDouble("BUSINESS_AMT"))); // 大写金额
		// parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //
		// 台头一
		// String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "yyyyMMdd"); // 年月日
		// String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "hhmmss"); // 时分秒
		// parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); // 台头二
		// yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		// hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "HH:mm"); // 时分秒
		// parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION"));
		// // 备注
		// parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); // 票据号
		// if (null == bil_business_no)
		// bil_business_no = EKTTool.getInstance().getBillBusinessNo(); // 补印操作
		// parm.setData("ONFEE_NO", "TEXT", bil_business_no); // 收据号
		// parm.setData("PRINT_DATE", "TEXT", yMd); // 打印时间
		// parm.setData("DATE", "TEXT", yMd + "    " + hms); // 日期
		// parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		// parm.setData("COPY", "TEXT", copy); // 收款人
		// // ===zhangp 20120525 start
		// parm.setData("O", "TEXT", "");
		// // this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw",
		// // parm,true);
		// this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm,
		// true);
		// ===zhangp 20120525 end

	}

	/**
	 * 充值文本框回车事件======zhangp 20111227
	 */
	public void addFee() {
		if (this.getValueDouble("TOP_UPFEE") < 0) {
			this.messageBox("充值金额不可以为负值");
			return;
		}
		this.setValue("SUM_EKTFEE", this.getValueDouble("TOP_UPFEE")
				+ this.getValueDouble("CURRENT_BALANCE"));
	}

	/**
	 * 清空医疗卡页签============zhangp 20111227
	 */
	public void clearEKTValue() {
		ektOnClear();
		// clearValue("DESCRIPTION;TOP_UPFEE;SUM_EKTFEE");
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo, String exeType) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 修改医保票据号
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean updateINSPrintNo(String caseNo, String exeType) {
		TParm parm = new TParm();
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		parm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		parm.setData("PRINT_NO", insParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE"));
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
				"updateINSPrintNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 医保支付赋值
	 * 
	 * @param result
	 *            医保返回的参数
	 * @param regFeeParm
	 *            医保分割后医嘱的金额
	 * @return 返回医保支付总金额
	 */
	private double tjInsPay(TParm result, TParm regFeeParm) {
		reg.getRegReceipt().setPayBankCard(0.00);
		reg.getRegReceipt().setPayCheck(0.00);
		reg.getRegReceipt().setPayDebit(0.00);
		reg.getRegReceipt().setPayInsCard(result.getDouble("INS_SUMAMT")); // 医保金额
		double ins_amt = result.getDouble("INS_SUMAMT");
		if (!tjINS) { // 现金收费
			reg.getRegReceipt().setPayCash(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT"));
			reg.getRegReceipt().setPayMedicalCard(0.00); // 医疗卡金额
		} else { // 医疗卡收费
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT")); // 医疗卡金额
		}
		TParm comminuteFeeParm = result.getParm("comminuteFeeParm"); // 费用分割
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			for (int j = 0; j < comminuteFeeParm.getCount("ORDER_CODE"); j++) {
				if (regFeeParm.getValue("ORDER_CODE", i).equals(
						comminuteFeeParm.getValue("ORDER_CODE", j))) {
					if (comminuteFeeParm.getValue("RECEIPT_TYPE", j).equals(
							"REG_FEE")) {
						reg.getRegReceipt().setRegFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 12折扣前挂号费(REG_RECEIPT)
						reg.getRegReceipt().setRegFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					} else {
						reg.getRegReceipt().setClinicFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 14折扣前诊查费(REG_RECEIPT)
						reg.getRegReceipt().setClinicFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					}
					break;
				}
			}
		}
		return ins_amt;
	}

	/**
	 * 退挂操作使用
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 * @param ektName
	 *            String
	 * @param cashName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetReg(TParm unRegParm, String caseNo, boolean flg,
			String ektName, String cashName, String stutsFlg) {
		// TParm reSetInsParm=new TParm();
		if (!reSetInsSave(unRegParm.getValue("INV_NO")))
			return;
		if ("EKT".equals(this.getValueString("PAY_WAY"))) {
			// 添加建行卡退挂分支====pangben 2012-12-07
			TParm ccbParm = checkCcbReSet(caseNo);// 判断是否执行建行卡操作
			if (null == ccbParm || ccbParm.getCount() <= 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				// 建行操作
				// TParm ccbp=checkCcbReSet(caseNo);
				unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));// 建行金额
				reSetCcbSave(unRegParm, caseNo, stutsFlg);
			}
		} else if ("C0".equals(this.getValueString("PAY_WAY"))) { // 现金
			reSetCashSave(unRegParm, stutsFlg, flg, cashName);
		} else if ("INS".equals(this.getValueString("PAY_WAY"))) { // 医保卡
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				TParm ccbParm = checkCcbReSet(caseNo);// 判断是否执行建行卡操作
				if (null == ccbParm || ccbParm.getCount() <= 0)
					reSetCashSave(unRegParm, stutsFlg, flg, cashName);
				else {
					// 建行操作
					unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));
					reSetCcbSave(unRegParm, caseNo, stutsFlg);
				}
			}
		}
		// 医保删除在途状态
		if (null != confirmNo && confirmNo.length() > 0) {
			if (!deleteInsRun(reSetCaseNo, "REGT"))
				return;
		}
	}

	/**
	 * 医保退挂操作
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo) {
		TParm reSetInsParm = new TParm();
		if (null != confirmNo && confirmNo.length() > 0) {
			// 医保卡退费 需要修改医疗卡参数
			if (null == reSetCaseNo && reSetCaseNo.length() <= 0) {
				return false;
			}
			TParm tredeParm = new TParm(); // 查询此次退挂操作是否是医疗卡退挂
			tredeParm.setData("CASE_NO", reSetCaseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // 类型
			tredeParm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
			TParm reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // 医疗卡退费查询
			if (reSetEktParm.getErrCode() < 0) {
				return false;
			}
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {// 医疗卡退挂操作
				if (p3 == null || null == p3.getValue("MR_NO")
						|| p3.getValue("MR_NO").length() <= 0) {
					this.messageBox("医疗卡退费,请执行读卡操作");
					return false;
				}
			}
			TParm parm = insExeFee(false);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // 取消
				return false;
			}
			reSetInsParm.setData("CASE_NO", reSetCaseNo); // 就诊号
			reSetInsParm.setData("CONFIRM_NO", confirmNo); // 医保就诊号
			reSetInsParm.setData("INS_TYPE", insType); // 医保就诊号
			reSetInsParm.setData("RECP_TYPE", "REG"); // 收费类型
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // 退费类型
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // 医保区域代码
			reSetInsParm.setData("PAT_TYPE", this.getValue("REG_CTZ1")); // 身份
			reSetInsParm.setData("INV_NO", invNo); // 票据号
			// System.out.println("reSetInsParm::::::" + reSetInsParm);
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				this.messageBox("医保退挂失败");
				return false;
			}
		}
		return true;
	}

	/**
	 * 校验是否建行卡退挂操作
	 * 
	 * @return
	 */
	private TParm checkCcbReSet(String reSetCaseNo) {
		String sql = "SELECT CASE_NO,SUM(AMT) AS AMT FROM EKT_CCB_TRADE WHERE CASE_NO='"
				+ reSetCaseNo + "' AND BUSINESS_TYPE='REG' group by case_no";
		TParm reSetParm = new TParm(TJDODBTool.getInstance().select(sql));
		return reSetParm;
	}

	/**
	 * 建行卡退费操作 =====pangben 2012-12-07
	 */
	private void reSetCcbSave(TParm unRegParm, String caseNo, String stutsFlg) {
		// 调用建行接口退费流程
		unRegParm.setData("NHI_NO", regionParm.getValue("NHI_NO", 0));
		unRegParm.setData("RECEIPT_NO", unRegParm.getParm("RECP_PARM")
				.getValue("RECEIPT_NO", 0));
		// 建行接口操作
		// TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ccb.CCBServerAction", "getCcbRe", unRegParm);
		if (result.getErrCode() < 0) {
			this.messageBox("建行接口调用出现问题,请联系信息中心");
			return;
		}
		unRegParm.setData("FLG", "N");
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("OPT_USER", Operator.getID());
		result.setData("BUSINESS_TYPE", "REGT");
		result = REGCcbReTool.getInstance().saveEktCcbTrede(result);
		if (result.getErrCode() < 0) {
			this.messageBox("建行退挂失败");
			return;
		}
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onUnReg", unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// 调用排队叫号
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("叫号失败");
		}
		if (stutsFlg.equals("Y")) {
			// add by huangtt 20131231 start
			String message = "建行卡退挂成功!";
			// if(ticketFlg){
			// message = message + "票据号:"+unRegParm.getValue("INV_NO");
			// }
			this.messageBox(message);
			// this.messageBox("建行卡退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
			// add by huangtt 20131231 end

			// add by wangbin 20150806 退挂成功后更新临时表的取消状态 START
			this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
			// add by wangbin 20150806 退挂成功后更新临时表的取消状态 END
		}
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param ektName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetEktSave(TParm unRegParm, String caseNo, String ektName,
			String stutsFlg) {
		// 医疗卡
		TParm result = new TParm();
		if (EKTIO.getInstance().ektSwitch()) {
			if (aheadFlg) {
				result = onOpenCardR(caseNo);
				int type = 0;
				if (result == null) {
					this.messageBox("E0115");
					return;
				}
				type = result.getInt("OP_TYPE");
				if (type == 3 || type == -1) {
					this.messageBox("E0115");
					return;
				}
				if (type == 2) {
					return;
				}
				tradeNoT = result.getValue("TRADE_NO");
				unRegParm.setData("TRADE_NO", tradeNoT);
			} else {
				result = EKTpreDebtTool.getInstance().unRegForPre(caseNo);
				if (result.getErrCode() < 0) {
					messageBox(result.getErrText());
					return;
				}
			}
			// 医疗卡退挂
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					ektName, unRegParm);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				EKTIO.getInstance().unConsume(tradeNoT, this);
				return;
			}
			if (stutsFlg.equals("Y")) {
				// add by huangtt 20131231 start
				String message = "退挂成功!";
				// if(ticketFlg){
				// message = message + "票据号:"+unRegParm.getValue("INV_NO");
				// }
				this.messageBox(message);
				// this.messageBox("退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
				// add by huangtt 20131231 end
				
				// add by wangbin 20150806 退挂成功后更新临时表的取消状态 START
				this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
				// add by wangbin 20150806 退挂成功后更新临时表的取消状态 END
			}
			// 调用排队叫号
			if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
				this.messageBox("叫号失败");
			}

		}
	}

	/**
	 * 现金退费操作
	 * 
	 * @param unRegParm
	 *            退挂数据
	 * @param flg
	 *            现金退挂管控
	 * @param cashName
	 *            现金调用ACTION类接口方法名称
	 * @param stutsFlg
	 *            判断是否执行提示消息框
	 */
	private void reSetCashSave(TParm unRegParm, String stutsFlg, boolean flg,
			String cashName) {
		TParm result = new TParm();
		//add by huangtt 20141110  退挂时判断是否是有医嘱
		result = EKTpreDebtTool.getInstance().unRegForPre(unRegParm.getValue("CASE_NO"));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		
		
		if (stutsFlg.equals("Y")) {
			unRegParm.setData("FLG", flg);
		}
		// 现金退挂动作
		result = TIOM_AppServer.executeAction("action.reg.REGAction", cashName,
				unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// 调用排队叫号
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("叫号失败");
		}
		if (stutsFlg.equals("Y")) {
			// add by huangtt 20131231 start
			String message = "退挂成功!";
			// if(ticketFlg){
			// message = message + "票据号:"+unRegParm.getValue("INV_NO");
			// }
			this.messageBox(message);
			// this.messageBox("退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
			// add by huangtt 20131231 end

			// add by wangbin 20150806 退挂成功后更新临时表的取消状态 START
			this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
			// add by wangbin 20150806 退挂成功后更新临时表的取消状态 END
		}
	}

	/**
	 * VIP 日期与挂号日期相同
	 */
	public void onDateReg() {
		this.setValue("VIP_ADM_DATE", this.getValue("ADM_DATE"));
		onQueryVipDrTable();
	}

	public void onPast() {
		if (this.getValueString("BIRTH_DATE").length() > 0
				&& this.getValueString("BIRTH_DATE") != null)
			this.grabFocus("POST_CODE");
	}

	/**
	 * 保存QUE_NO 就诊号 解决重号问题 将原来程序的一个事物拆分出来先保存就诊号逻辑 ===============pangben
	 * 2012-6-18
	 */
	private boolean onSaveQueNo(TParm regParm) {
		// 处理号表
		TParm result = null;
		if (regParm.getBoolean("VIP_FLG")) {
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onSaveQueNo", regParm);
		} else {
			// 普通诊
			result = SchDayTool.getInstance().updatequeno(regParm);
		}
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 查就诊号有无占号 ====zhangp 20120629
	 * 
	 * @param temp
	 */
	private boolean queryQueNo(TParm temp) {
		String vipSql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE "
				+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
				+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
				+ temp.getValue("CLINICROOM_NO") + "' AND  QUE_STATUS='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (result.getErrCode() < 0) {
			messageBox("查号失败");
			return false;
		}
//		if (result.getCount() <= 0) {
//			messageBox("无就诊号");
//			return;
//		}
		int queNo = result.getInt("QUE_NO", 0);
		//add by huangtt 20140926 start
		if(queNo == 0){
			messageBox("无就诊号");
			return false;
		}
		//add by huangtt 20140926 end
		reg.setQueNo(queNo);
		
//		String sql = "SELECT START_TIME FROM REG_CLINICQUE "
//			+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
//			+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
//			+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
//			+ temp.getValue("CLINICROOM_NO") + "' AND QUE_NO = "+queNo;
//		TParm parmT = new TParm(TJDODBTool.getInstance().select(sql));
//		if(parmT.getCount() > 0)
//			reg.setRegAdmTime(parmT.getValue("START_TIME", 0));
		
		return true;
	}

	/**
	 * 判断现住址是否填写，如没填写，与详细地址一样 ====add by huangtt 20131121
	 */
	public void onAddress() {
		String currentAddress = this.getValueString("CURRENT_ADDRESS");
		if (currentAddress.equals("")) {
			this.setValue("CURRENT_ADDRESS", this
					.getValueString("RESID_ADDRESS"));
		}
		this.grabFocus("SPECIES_CODE");
	}

	public void getCTZ() {
		String dept = this.getValueString("DEPT_CODE");
		// String ctz=p3.getValue("CARD_TYPE");
		String ctz = memCode;
		if (!ctz.equals("")) {
			String sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE MAIN_CTZ_FLG='Y' AND CTZ_DEPT_FLG='Y' AND MEM_CODE = '"
					+ ctz + "' AND DEPT_CODE ='" + dept + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				this.setValue("REG_CTZ1", parm.getValue("CTZ_CODE", 0));
			} else {
				this.setValue("REG_CTZ1", this.getValue("CTZ1_CODE"));
			}
		}
		getCtzValid();
		getCtzValid2();

	}

	public void getCtzValid() {
		String ctz = this.getValueString("REG_CTZ1");
		String sql = "SELECT START_DATE,END_DATE,MEM_CODE,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			if (parm.getBoolean("USE_FLG", 0)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				String endDate = parm.getValue("END_DATE", 0).replace("-", "/")
						.substring(0, 10);
				String today = SystemTool.getInstance().getDate().toString()
						.replace("-", "/").substring(0, 10);
				try {
					if (sdf.parse(endDate).before(sdf.parse(today))) {
						this.messageBox("该挂号身份一已过期，请重新选择！");
						setValue("REG_CTZ1", zfCtz);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!parm.getValue("MEM_CODE", 0).equals("")) {
				sql = "SELECT MR_NO FROM MEM_PATINFO  WHERE END_DATE > SYSDATE AND MEM_CODE='"
						+ parm.getValue("MEM_CODE", 0)
						+ "' AND MR_NO='"
						+ this.getValueString("MR_NO") + "'";
				// System.out.println(sql);
				TParm parmMem = new TParm(TJDODBTool.getInstance().select(sql));
				if (parmMem.getCount() < 0) {
					this.messageBox("该挂号身份一不应用于该患者，请重新选择！");
					setValue("REG_CTZ1", zfCtz);
				}
			}
			onClickClinicType(true);
		}
	}

	public void getCtzValid2() {
		String ctz = this.getValueString("REG_CTZ2");
		String sql = "SELECT START_DATE,END_DATE,MEM_CODE,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			if (parm.getBoolean("USE_FLG", 0)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				String endDate = parm.getValue("END_DATE", 0).replace("-", "/")
						.substring(0, 10);
				String today = SystemTool.getInstance().getDate().toString()
						.replace("-", "/").substring(0, 10);
				try {
					if (sdf.parse(endDate).before(sdf.parse(today))) {
						this.messageBox("该挂号身份二已过期，请重新选择！");
						setValue("REG_CTZ2", "");

					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!parm.getValue("MEM_CODE", 0).equals("")) {
				sql = "SELECT MR_NO FROM MEM_PATINFO WHERE END_DATE > SYSDATE AND MEM_CODE='"
						+ parm.getValue("MEM_CODE", 0)
						+ "' AND MR_NO='"
						+ this.getValueString("MR_NO") + "'";
				TParm parmMem = new TParm(TJDODBTool.getInstance().select(sql));
				if (parmMem.getCount() < 0) {
					this.messageBox("该挂号身份二不应用于该患者，请重新选择！");

					setValue("REG_CTZ2", "");
				}
			}

			onClickClinicType(true);
		}
	}

	public void mergePreCaseNo() {
		// 先预开后预约时，预开检查和预约挂号合并就诊号
		String admDatePre = this.getValue("ADM_DATE").toString().substring(0,
				10).replace("/", "").replace("-", "").replace(" ", "");
		String preSelectSql = "SELECT CASE_NO,OLD_CASE_NO FROM REG_PATADM WHERE ADM_DATE = TO_DATE('"
				+ admDatePre
				+ "','YYYYMMDD') AND IS_PRE_ORDER ='Y' AND MR_NO = '"
				+ this.getValue("MR_NO").toString() + "' ";
		TParm preSelectResult = new TParm(TJDODBTool.getInstance().select(
				preSelectSql));
		if (preSelectResult.getCount() > 0) {
			reg.setCaseNo(preSelectResult.getValue("CASE_NO", 0));
			reg.setOldCaseNo(preSelectResult.getValue("OLD_CASE_NO", 0));
			reg.setPreFlg("Y");
			// 删除挂号表的预开挂号信息
			String delPreSql = "DELETE REG_PATADM WHERE CASE_NO = '"
					+ reg.caseNo() + "'";
			TParm delPreResult = new TParm(TJDODBTool.getInstance().update(
					delPreSql));
		}

	}

	/**
	 * 获取购买月龄
	 */
	public int getBuyMonth(String s, String s1) {
		// Date m=new Date();
		Date d = null;
		Date d1 = null;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			d = df.parse(s);
			d1 = df.parse(s1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		c.setTime(d1);
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);
		int result;
		if (year == year1) {
			result = month1 - month;// 两个日期相差几个月，即月份差
		} else {
			result = 12 * (year1 - year) + month1 - month;// 两个日期相差几个月，即月份差
		}
		return result;
	}

	public void onWrist() {
		if (getValueString("MR_NO").length() == 0
				|| getValueString("PAT_NAME").length() == 0
				|| getValueString("SEX_CODE").length() == 0
				|| getValueString("BIRTH_DATE").length() == 0) {
			messageBox("不能有选项为空,请重新填写!");
			return;
		}
		String a_number = getValueString("MR_NO");
		String a_name = getValueString("PAT_NAME");
		String a_gender = getValueString("SEX_CODE");
		if (a_gender.equals("1"))
			a_gender = "男";
		if (a_gender.equals("2"))
			a_gender = "女";
		if (a_gender.equals("9"))
			a_gender = "未说明";
		if (a_gender.equals("0"))
			a_gender = "未知";
		String a_time = getValueString("BIRTH_DATE");
		TParm print = new TParm();
		print.setData("MR_NO", "TEXT", a_number);
		print.setData("PAT_NAME", "TEXT", a_name);
		print.setData("SEX_CODE", "TEXT", a_gender);
		print.setData("BIRTH_DATE", "TEXT", a_time.substring(0, 10).replace(
				"-", "/"));
		openPrintDialog("%ROOT%\\config\\prt\\REG\\REGPatAdm.jhw", print);
	}

	/**
	 * 身份证号校验
	 */
	public boolean isCard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches(
				"(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}

	/**
	 * 设置性别
	 */
	public void setSexCode(String sexCode) {
		try {
			int a = Integer.parseInt(sexCode);
			int b = a % 2;
			if (b == 0) {// 女
				this.setValue("SEX_CODE", 2);
			} else if (b == 1) {// 男
				this.setValue("SEX_CODE", 1);
			}
			this.grabFocus("NATION_CODE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 插入临时数据
	 * 
	 * @author wangbin
	 */
	private void insertMroRegData() {
		// modify by wangbin 2016/1/18 为避免用户忘记提取预约数据，现场挂号不分预约与否只要临时表无数据都插入
		// modify by wangbin 2015/1/8 预约挂号的初诊病人以及现场挂号的病人，挂号时插入临时表
		// modify by wangbin 2015/1/15 去掉门诊判断，门急诊都按照门诊处理
		TParm mroRegParm = new TParm();

		// 预约挂号的病人
		if (crmRegFlg && StringUtils.isNotEmpty(crmId)) {
			TParm queryParm = new TParm();
			queryParm.setData("BOOK_ID", crmId);
			// 查询挂号数据是否已经存在
			queryParm = MROBorrowTool.getInstance().queryMroRegAppointment(
					queryParm);

			if (queryParm.getErrCode() < 0) {
				err("ERR:" + queryParm.getErrCode() + queryParm.getErrName()
						+ queryParm.getErrText());
				return;
			}

			// 预约数据如果已经存在，说明是非初诊的预约数据，提取时已插入临时表
			if (queryParm.getCount() > 0) {
				// modify by wangb 2017/11/24 CRM相同预约号存在重复利用的情况
				if (queryParm.getValue("MR_NO", 0).equals(pat.getMrNo())) {
					if (StringUtils.isEmpty(queryParm.getValue("CASE_NO", 0))) {
						TParm updateParm = new TParm();
						updateParm.setData("MRO_REGNO", queryParm.getValue("MRO_REGNO", 0));
						updateParm.setData("BOOK_ID", crmId);
						updateParm.setData("MR_NO", queryParm.getValue("MR_NO", 0));
						updateParm.setData("ADM_TYPE", "O");
						updateParm.setData("CASE_NO", reg.caseNo());
						// 回更临时表就诊号
						updateParm = MROBorrowTool.getInstance().updateCaseNoForMroReg(updateParm);
						
						if (updateParm.getErrCode() < 0) {
							err("ERR:" + updateParm.getErrCode() + updateParm.getErrName()
									+ updateParm.getErrText());
							return;
						}
					}
					return;
				} else {
					// 如果库里现有的数据的病案号与当前挂号的不一致，则将库里数据的取消
					TParm parm = new TParm();
					parm.setData("BOOK_ID", queryParm.getValue("BOOK_ID", 0));
					parm.setData("MR_NO", queryParm.getValue("MR_NO", 0));
					parm.setData("ADM_TYPE", "O");
					parm.setData("OPT_USER", Operator.getID());
					parm.setData("OPT_TERM", Operator.getIP());

					TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(parm, null);

					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
					}
				}
			}
		}

		// 取号原则
		mroRegParm.setData("MRO_REGNO", SystemTool.getInstance().getNo("ALL",
				"MRO", "MRO_REGNO", "MRO_REGNO"));

		this.mroRegNo = mroRegParm.getValue("MRO_REGNO");

		mroRegParm.setData("SEQ", 1);
		// 预约号
		mroRegParm.setData("BOOK_ID", crmId == null ? "" : crmId);
		// 0_预约挂号(APP), 1_现场挂号(LOC), 2_住院登记
		mroRegParm.setData("ORIGIN_TYPE", "1");
		// 门急住标识
		mroRegParm.setData("ADM_TYPE", "O");
		// 病案号
		mroRegParm.setData("MR_NO", pat.getMrNo());
		// 就诊号
		mroRegParm.setData("CASE_NO", reg.caseNo());
		// 诊区
		mroRegParm.setData("ADM_AREA_CODE", reg.getClinicareaCode());
		// 挂号时间
		mroRegParm.setData("ADM_DATE", StringTool.getString(
				(Timestamp) getValue("ADM_DATE"), "yyyy/MM/dd"));
		// 挂号时段
		mroRegParm.setData("SESSION_CODE", reg.getSessionCode());
		// 就诊序号
		mroRegParm.setData("QUE_NO", reg.getQueNo());
		mroRegParm.setData("PAT_NAME", pat.getName());
		mroRegParm.setData("SEX_CODE", pat.getSexCode());
		mroRegParm.setData("BIRTH_DATE", StringTool.getString(
				pat.getBirthday(), "yyyy/MM/dd"));
		mroRegParm.setData("CELL_PHONE", pat.getCellPhone());
		mroRegParm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		mroRegParm.setData("DR_CODE", this.getValue("DR_CODE"));
		// 待出库确认状态
		mroRegParm.setData("CONFIRM_STATUS", "0");
		// 取消注记(Y_取消,N_未取消)
		mroRegParm.setData("CANCEL_FLG", "N");
		mroRegParm.setData("OPT_DATE", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		mroRegParm.setData("OPT_USER", Operator.getID());
		mroRegParm.setData("OPT_TERM", Operator.getIP());

		TParm mroRegResult = MROBorrowTool.getInstance().insertMroRegByLoc(
				mroRegParm);

		if (mroRegResult.getErrCode() < 0) {
			System.out.println("插入临时表失败：" + mroRegResult.getErrText());
			err("ERR:" + mroRegResult.getErrCode() + " "
					+ mroRegResult.getErrText());
		}
		
		// 清空预约号
		crmId = "";
	}
	
	/**
	 * 将数据插入病历册主档表
	 * 
	 * @author wangbin
	 */
	private void insertMroMrvData() {
		// modify by wangbin 2015/1/15 去掉门诊判断，门急诊都按照门诊处理
		TParm queryParm = new TParm();
		// 病案号
		queryParm.setData("MR_NO", pat.getMrNo());
		// 门急住标识
		queryParm.setData("ADM_TYPE", "O");
		// 查询病历册主档表中是否存在该病患的数据
		queryParm = MROQueueTool.getInstance().selectMRO_MRV(queryParm);

		if (queryParm.getCount() <= 0) {
			TParm mroMrv = new TParm();
			String region = Operator.getRegion();
			mroMrv.setData("MR_NO", pat.getMrNo());
			mroMrv.setData("SEQ", 1);
			mroMrv.setData("ADM_TYPE", "O");
			mroMrv.setData("IPD_NO", "");
			mroMrv.setData("CREATE_HOSP", region);
			mroMrv.setData("IN_FLG", "0");
			mroMrv.setData("CURT_HOSP", region);
			mroMrv.setData("CURT_LOCATION", "");
			mroMrv.setData("TRAN_HOSP", region);
			mroMrv.setData("BOX_CODE", "");
			mroMrv.setData("BOOK_NO", "");
			mroMrv.setData("OPT_USER", Operator.getID());
			mroMrv.setData("OPT_TERM", Operator.getIP());
			// 增加当前案卷借阅科室和借阅人
			mroMrv.setData("CURT_LEND_DEPT_CODE", "");
			mroMrv.setData("CURT_LEND_DR_CODE", "");

			TParm result = MROQueueTool.getInstance().insertMRO_MRV(mroMrv);
			if (result.getErrCode() < 0) {
				System.out.println("插入病历册主档表:" + result.getErrText());
				err("ERR:" + result.getErrCode() + " " + result.getErrText());
			}
		}
	}
	
	/**
	 * 病历待出库取消
	 * 
	 * @param caseNo 就诊号
	 * @author wangbin 2014/09/05
	 */
	private void cancelMroRegAppointment(String caseNo) {
		TParm parm = new TParm();
		if (StringUtils.isNotEmpty(crmId)) {
			parm.setData("BOOK_ID", crmId);
		}
		if (StringUtils.isNotEmpty(pat.getMrNo())) {
			parm.setData("MR_NO", pat.getMrNo());
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", "O");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		// 如果病患取消预约，则更新临时表(MRO_REG)取消注记
		TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(parm, null);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		// 如果病患取消挂号，则更新借阅表的取消状态
		result = MROBorrowTool.getInstance().updateMroQueueCanFlg(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
	
	/**
	 * 向病案室发送消息
	 * 
	 * @author wangbin 20140915
	 */
	private void onSendMROMessages() {
		client = SocketLink
				.running("","ODO", "ODO");
		if (client.isClose()) {
			out(client.getErrText());
			return;
		}
		
		client.sendMessage("ODO", "MRO_REGNO:" + this.mroRegNo
				+ "|MR_NO:" + pat.getMrNo() + "|PAT_NAME:" + pat.getName());
		
		if (client == null) {
			return;
		}
		client.close();
	}
	/**
	 * 
	 */
	public void checkInsure(){
		if(this.getValue("INSURE_INFO") == null || this.getValue("INSURE_INFO").toString().length() <= 0){
			return;
		}
		String sql = "SELECT VALID_FLG FROM MEM_INSURE_INFO " +
				" WHERE MR_NO = '"+this.getValueString("MR_NO")+"' AND CONTRACTOR_CODE = '"+this.getValue("INSURE_INFO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(!"Y".equals(parm.getValue("VALID_FLG",0))){
			this.messageBox("该保险信息无效，请重新选择");
			this.setValue("INSURE_INFO", "");
			return;
		}
		sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " +
		" WHERE MR_NO = '"+pat.getMrNo()+"' AND VALID_FLG = 'Y'" +
		" AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
		System.out.println(""+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			if(JOptionPane.showConfirmDialog(null, "该病患保险不在有效期内，是否继续", "信息",
    				JOptionPane.YES_NO_OPTION) == 0){
				
			}else{
				this.setValue("INSURE_INFO", "");
			}
		}
	}
	
	public void getServiceLevel(){
		String ctz = this.getValueString("REG_CTZ1");
		String sql = "SELECT SERVICE_LEVEL FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", 0));
	}
	
	/**
	 * 根据保险有效期在门急诊挂号页面增加相应提示
	 * 当挂号日期大于任意一个保险有效期的截止日期时，门急诊挂号界面添加相应保险公司的到期提醒
	 */
	private void checkInsures() {
		String sql = "SELECT A.MR_NO, A.CONTRACTOR_CODE, A.INSURANCE_NUMBER, A.VALID_FLG, A.START_DATE, A.END_DATE, B.CONTRACTOR_DESC FROM MEM_INSURE_INFO A LEFT JOIN MEM_CONTRACTOR B ON A.CONTRACTOR_CODE = B.CONTRACTOR_CODE "
				+ " WHERE MR_NO = '" + this.getValueString("MR_NO") + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount("MR_NO");
		if (count > 0) {
			Date endDate;
			Date now = SystemTool.instanceObject.getDate();
			String contractorDesc;
			String insuranceNumber;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < count; i++) {
				contractorDesc = parm.getValue("CONTRACTOR_DESC", i);
				insuranceNumber = parm.getValue("INSURANCE_NUMBER", i);
				endDate = parm.getTimestamp("END_DATE", i);
				if (endDate.before(now)) {
					sb.append("保险公司<"+contractorDesc + ">，保险单号<"+insuranceNumber+">，合同到期；" + "\n\r");
				}
			}
			if (sb.toString().length() > 0) {
				this.messageBox(sb.toString());
			}
		}
	}
	
	/**
	 * 门急诊挂号时，读医疗卡或手输病案号后，如果患者身份已停用，
	 * 
	 * 1、添加提示“该患者原身份【99 门诊[自费]】已停用，请在【患者注册页面】重新设定”
	 * 
	 * 2、清空门急诊挂号界面中的“身份一”和“挂号身份一”中的值
	 */
	private void checkCtz1Code() {
		String ctz1Code = this.getValueString("CTZ1_CODE");
		String sql = "SELECT CTZ_CODE, CTZ_DESC, USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '" + ctz1Code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (!parm.getBoolean("USE_FLG", 0)) {
			String ctzDesc = parm.getValue("CTZ_DESC", 0);
			this.messageBox("该患者原身份【" + ctzDesc + "】已停用，请在【患者注册页面】重新设定");
			this.setValue("CTZ1_CODE", "");
			this.setValue("REG_CTZ1", "");
		}
	}
}
