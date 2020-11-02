package com.javahis.ui.sum;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.time.DurationFormatUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.base.TFrameBase;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.DateUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

import jdo.adm.ADMTool;
import jdo.erd.ERDForSUMTool;
import jdo.sum.SUMVitalSignTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

/**
 * <p>
 * Title: 成人体温单
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: javahis
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * 
 * @version 1.0
 */
public class SUMVitalSignMainControl extends TControl {

	TTable masterTable;// 体温记录table（主）
	TTable detailTable; // 体温明细table（细）
	int masterRow = -1;// 主table选中行号
	int detailRow = -1;// 细table选中行号
	TParm patInfo = new TParm();// 患者信息
	String admType = "I"; // 门急住别
	String caseNo = ""; // 看诊号
	TParm tprDtl = new TParm(); // 体温表数据
	TParm inParm = new TParm(); // 入参
	boolean isMroFlg = false;// 病案首页调用

	private final static String stoolLink = "+";
	/**
	 * P 成人 C 儿童 N 新生儿
	 */
	String sumType = "";
	// 上午 、下午
	private List<Integer> MorningList = new ArrayList<Integer>();
	private List<Integer> AfternoonList = new ArrayList<Integer>();

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		masterTable = (TTable) this.getComponent("masterTable");// 初始化组件
		detailTable = (TTable) this.getComponent("detailTable");
		this.callFunction("UI|masterTable|addEventListener", "masterTable->"
				+ TTableEvent.CLICKED, this, "onMasterTableClicked");// table点击事件
		inParm = this.getInputParm();
		if (inParm != null) {
			admType = inParm.getValue("SUM", "ADM_TYPE");
			caseNo = inParm.getValue("SUM", "CASE_NO");
			sumType = inParm.getValue("SUM", "TYPE");
			onQuery();
			if (inParm.getValue("SUM", "FLG").equals("MRO")) {
				isMroFlg = true;
				hideFrame();// 隐藏界面
			}
		}
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// 记录时间
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温

		MorningList.add(0);
		MorningList.add(1);
		MorningList.add(2);
		AfternoonList.add(3);
		AfternoonList.add(4);
		AfternoonList.add(5);
	}

	/**
	 * 如果是MRO调用隐藏主面板
	 */
	public void hideFrame() {
		if (!(getComponent() instanceof TFrameBase))
			return;
		TFrame frame = (TFrame) getComponent();
		frame.setOpenShow(false);
		onPrint();
		frame.onClosed();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", admType);
		if ("E".equals(admType)) {
			patInfo = ERDForSUMTool.getInstance().selERDPatInfo(parm); // 病患信息
			patInfo.setData("ADM_DAYS", 0, this.getInHospDays(
					patInfo.getTimestamp("IN_DATE", 0),
					patInfo.getTimestamp("OUT_DATE", 0)));
		} else if ("I".equals(admType)) {
			patInfo = ADMTool.getInstance().getADM_INFO(parm);
			patInfo.setData("ADM_DAYS", 0,
					ADMTool.getInstance().getAdmDays(caseNo) + 1);
		}
		TParm result = SUMVitalSignTool.getInstance().selectExmDateUser(parm);// 体温记录
		// 初始化体温记录table
		for (int row = 0; row < result.getCount(); row++) {
			result.setData("EXAMINE_DATE", row, StringTool.getString(StringTool
					.getDate(result.getValue("EXAMINE_DATE", row), "yyyyMMdd"),
					"yyyy/MM/dd"));
			if ((row + 1) % 7 == 0) {// 每七天设置一个黄颜色
				masterTable.setRowColor(row, new Color(255, 255, 132));
			}
		}
		masterTable.getTable().repaint();
		masterTable.setParmValue(result);
		if (masterTable.getRowCount() > 0) {
			masterTable.setSelectedRow(masterTable.getRowCount() - 1);// 默认选中最后一行
			onMasterTableClicked(masterTable.getRowCount() - 1); // 手动执行点击事件
		}
	}

	/**
	 * 获得住院天数
	 * 
	 * @param inDate
	 * @param outDate
	 * @return
	 */
	private int getInHospDays(Timestamp inDate, Timestamp outDate) {
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		Timestamp endDate = null;
		if (outDate == null)
			endDate = now;
		else
			endDate = outDate;
		int days = StringTool.getDateDiffer(endDate, inDate);
		if (days == 0)
			return 1;
		return days;
	}

	/**
	 * 行转列
	 * 
	 * @param tprDtl
	 *            TParm
	 * @return TParm
	 */
	public TParm rowToColumn(TParm tprDtl) {
		TParm result = new TParm();
		for (int i = 0; i < tprDtl.getCount(); i++) {
			result.addData("" + i, clearZero(tprDtl.getData("TEMPERATURE", i)));// 体温
			result.addData("" + i, clearZero(tprDtl.getData("PLUSE", i)));// 脉搏
			result.addData("" + i, clearZero(tprDtl.getData("RESPIRE", i)));// 呼吸
			/****************** shibl 20120330 modify ***************************/
			result.addData("" + i,
					clearZero(tprDtl.getData("SYSTOLICPRESSURE", i))); // 收缩压
			result.addData("" + i,
					clearZero(tprDtl.getData("DIASTOLICPRESSURE", i))); // 舒张压
			result.addData("" + i, clearZero(tprDtl.getData("HEART_RATE", i))); // 心率
		}
		return result;
	}

	/**
	 * 新建
	 */
	public void onNew() {
		// 拿到服务器/数据库当前时间
		// String today =
		// StringTool.getString(TJDODBTool.getInstance().getDBTime(),
		// "yyyyMMdd");
		String today = (String) openDialog("%ROOT%\\config\\sum\\SUMTemperatureDateChoose.x");
		if (today.length() == 0) {
			messageBox("未选择测量时间");
			return;
		}
		for (int i = 0; i < masterTable.getRowCount(); i++) {
			if (today.equals(StringTool.getString(
					StringTool.getDate(
							masterTable.getItemString(i, "EXAMINE_DATE"),
							"yyyy/MM/dd"), "yyyyMMdd"))) {
				this.messageBox("已存在今天数据\n不可以新建");
				return;
			}
		}
		// 插入一条数据
		TParm MData = new TParm();
		MData.setData("EXAMINE_DATE",
				today.substring(0, 4) + "/" + today.substring(4, 6) + "/"
						+ today.substring(6));
		MData.setData("USER_ID", Operator.getName());
		// 默认选中新增行
		int newRow = masterTable.addRow(MData);
		masterTable.setSelectedRow(newRow);
		onMasterTableClicked(newRow); // 手动执行单击事件
		// 新建的时候写入当前时间
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// 住院天数
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);// 记录时间
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
	}

	/**
	 * 体温记录table单击事件
	 */
	public void onMasterTableClicked(int row) {
		masterRow = row; // 主table选中行号
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", admType);
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXAMINE_DATE", StringTool.getString(StringTool.getDate(
				masterTable.getItemString(row, "EXAMINE_DATE"), "yyyy/MM/dd"),
				"yyyyMMdd"));
		// =========================主表数据
		TParm master = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		this.clearComponent();// 清空组件
		// 向控件翻值
		this.setValue("INHOSPITALDAYS", patInfo.getData("ADM_DAYS", 0));// 住院天数
		this.setValue("OPE_DAYS", master.getValue("OPE_DAYS", 0));// 手术第几天
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		this.setValue("RECTIME", now);
		this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
		// =========================细表数据
		tprDtl = SUMVitalSignTool.getInstance().selectOneDateDtl(parm);
		// 如果没有该天的数据插入空白行
		if (tprDtl.getCount() == 0) {
			detailTable.removeRowAll();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			detailTable.addRow();
			// return;
		} else {
			detailTable.setParmValue(rowToColumn(tprDtl));
		}
		// =========================细表下面的组件
		/**
		 * 成人
		 */
		if (this.sumType.equals("P")) {
			setPDownPanel(master);
		} else {
			setCorNDownPanel(master);
		}
	}

	/**
	 * 儿童和新生儿赋值
	 */
	public void setCorNDownPanel(TParm master) {
		this.setValue("STOOL", master.getValue("STOOL", 0));// 大便
		// ======================shibl add
		// 20150727================================================
		this.setValue("STOOLX", master.getValue("STOOLX", 0));
		this.setValue("STOOLX_NUM", master.getValue("STOOLX_NUM", 0));
		this.setValue("STOOLW", master.getValue("STOOLW", 0));
		this.setValue("STOOLW_NUM", master.getValue("STOOLW_NUM", 0));
		this.setValue("STOOLB", master.getValue("STOOLB", 0));
		this.setValue("E_STOOL", master.getValue("E_STOOL", 0));
		this.setValue("E_STOOLX", master.getValue("E_STOOLX", 0));
		this.setValue("E_STOOLX_NUM", master.getValue("E_STOOLX_NUM", 0));
		this.setValue("E_STOOLW", master.getValue("E_STOOLW", 0));
		this.setValue("E_STOOLW_NUM", master.getValue("E_STOOLW_NUM", 0));
		this.setValue("E_STOOLB", master.getValue("E_STOOLB", 0));
		this.setValue("URINETIMES_NUM", master.getValue("URINETIMES_NUM", 0));
		this.setValue("BM_FLG", master.getValue("BM_FLG", 0));
		// ============================================================================
		this.setValue("ENEMA", master.getValue("ENEMA", 0));// 灌肠
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// 引流
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// 入量
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// 出量
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// 身高
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// 自定义一
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// 自定义二
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// 自定义三
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
		this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// 小便
		this.setValue("VOMIT", master.getValue("VOMIT", 0));// 呕吐
		this.setValue("HEAD_CIRCUM", master.getValue("HEAD_CIRCUM", 0));// 头围
		this.setValue("ABDOMEN_CIRCUM", master.getValue("ABDOMEN_CIRCUM", 0));// 腹围
		if (!master.getValue("WEIGHT", 0).equals("")) {
			this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// 体重
			getTRadioButton("W_KG").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
		} else {
			this.setValue("WEIGHT_G", master.getValue("WEIGHT_G", 0));// 体重克
			getTRadioButton("W_G").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.getTextField("WEIGHT").setEnabled(false);
		}
		this.setValue("WEIGHT_REASON", master.getValue("WEIGHT_REASON", 0));// 体重未测原因
	}

	/**
	 * 成人赋值
	 * 
	 * @param parm
	 */
	public void setPDownPanel(TParm master) {
		this.setValue("STOOL", master.getValue("STOOL", 0));// 大便
		this.setValue("AUTO_STOOL", master.getValue("AUTO_STOOL", 0));// 自行排便
		this.setValue("ENEMA", master.getValue("ENEMA", 0));// 灌肠
		this.setValue("DRAINAGE", master.getValue("DRAINAGE", 0));// 引流
		this.setValue("INTAKEFLUIDQTY", master.getValue("INTAKEFLUIDQTY", 0));// 入量
		this.setValue("OUTPUTURINEQTY", master.getValue("OUTPUTURINEQTY", 0));// 出量
		this.setValue("HEIGHT", master.getValue("HEIGHT", 0));// 身高
		this.setValue("USER_DEFINE_1", master.getValue("USER_DEFINE_1", 0));// 自定义一
		this.setValue("USER_DEFINE_1_VALUE",
				master.getValue("USER_DEFINE_1_VALUE", 0));
		this.setValue("USER_DEFINE_2", master.getValue("USER_DEFINE_2", 0));// 自定义二
		this.setValue("USER_DEFINE_2_VALUE",
				master.getValue("USER_DEFINE_2_VALUE", 0));
		this.setValue("USER_DEFINE_3", master.getValue("USER_DEFINE_3", 0));// 自定义三
		this.setValue("USER_DEFINE_3_VALUE",
				master.getValue("USER_DEFINE_3_VALUE", 0));
		this.setValue("URINETIMES", master.getValue("URINETIMES", 0));// 小便
		this.setValue("VOMIT", master.getValue("VOMIT", 0));// 呕吐
		this.setValue("HEAD_CIRCUM", master.getValue("HEAD_CIRCUM", 0));// 头围
		this.setValue("ABDOMEN_CIRCUM", master.getValue("ABDOMEN_CIRCUM", 0));// 腹围
		if (!master.getValue("WEIGHT", 0).equals("")) {
			this.setValue("WEIGHT", master.getValue("WEIGHT", 0));// 体重
			getTRadioButton("W_KG").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
		} else {
			this.setValue("WEIGHT_G", master.getValue("WEIGHT_G", 0));// 体重克
			getTRadioButton("W_G").setSelected(true);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.getTextField("WEIGHT").setEnabled(false);
		}
		this.setValue("WEIGHT_REASON", master.getValue("WEIGHT_REASON", 0));// 体重未测原因
	}

	/**
	 * 体温明细table单击事件(通过界面注册法)
	 */
	public void onDTableFocusChange() {
		// PS:由于存在矩阵转换的问题，所以选中的列为数据PARM的行
		// 初始化细table被选中的行（即是table的列号）
		detailRow = detailTable.getSelectedColumn();
		((TComboBox) this.getComponent("EXAMINESESSION"))
				.setSelectedIndex(detailRow + 1);
		this.setValue("RECTIME", tprDtl.getValue("RECTIME", detailRow));// 记录时间
		if (tprDtl.getValue("RECTIME", detailRow).equals("")) {
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			this.setValue("RECTIME", now);// 记录时间
		}
		this.setValue("SPCCONDCODE", tprDtl.getValue("SPCCONDCODE", detailRow));// 体温变化特殊情况
		this.setValue("PHYSIATRICS", tprDtl.getValue("PHYSIATRICS", detailRow));// 物理降温
		this.setValue("TMPTRKINDCODE",
				tprDtl.getValue("TMPTRKINDCODE", detailRow));// 体温种类
		if (tprDtl.getValue("TMPTRKINDCODE", detailRow).equals("")) {
			this.setValue("TMPTRKINDCODE", "4");// 体温种类：腋温
		}
		this.setValue("NOTPRREASONCODE",
				tprDtl.getValue("NOTPRREASONCODE", detailRow));// 未量原因
		this.setValue("PTMOVECATECODE",
				tprDtl.getValue("PTMOVECATECODE", detailRow));// 病人动态
		this.setValue("PTMOVECATEDESC",
				tprDtl.getValue("PTMOVECATEDESC", detailRow));// 病人动态附注
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		masterTable.acceptText();
		detailTable.acceptText();
		if (masterRow < 0) {
			this.messageBox("请选择一条记录！");
			return false;
		}
		TParm saveParm = new TParm();
		if (sumType.equals("P")) {
			saveParm = this.getPValueFromUI();
		} else {
			saveParm = this.getCorNValueFromUI();
		}
		if (saveParm.getErrCode() < 0) {
			this.messageBox(saveParm.getErrText());
			return false;
		}
		String sql = " SELECT HEIGHT,WEIGHT FROM ADM_INP WHERE CASE_NO='"
				+ caseNo + "'";
		TParm HWeightParmadm = new TParm(TJDODBTool.getInstance().select(sql));
		// 单独得到身高体重回写ADM_INP
		TParm HWeightParm = new TParm();
		if (!this.getValueString("WEIGHT").equals("")
				&& this.getValueDouble("HEIGHT") > 0) {
			HWeightParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));
		} else {
			HWeightParm
					.setData("HEIGHT", HWeightParmadm.getDouble("HEIGHT", 0));
		}
		try {
			if (!this.getValueString("WEIGHT").equals("")) {
				HWeightParm.setData("WEIGHT", this.getValueString("WEIGHT"));
			} else if (!this.getValueString("WEIGHT_G").equals("")) {
				HWeightParm.setData("WEIGHT",
						this.getValueDouble("WEIGHT_G") / 1000);
			} else {
				HWeightParm.setData("WEIGHT",
						HWeightParmadm.getDouble("WEIGHT", 0));
			}
		} catch (NumberFormatException e) {
			HWeightParm
					.setData("WEIGHT", HWeightParmadm.getDouble("WEIGHT", 0));
		}
		HWeightParm.setData("CASE_NO", caseNo);
		saveParm.setData("HW", HWeightParm.getData());
		// 判断是否已有该数据插入/更新
		String saveDate = saveParm.getParm("MASET").getValue("EXAMINE_DATE");
		// 得到左边table的数据
		TParm checkDate = new TParm();
		checkDate.setData("CASE_NO", caseNo);
		checkDate.setData("ADM_TYPE", admType);
		checkDate.setData("EXAMINE_DATE", saveDate);
		TParm existParm = SUMVitalSignTool.getInstance()
				.checkIsExist(checkDate);
		// 没有该天数据，直接保存
		if (existParm.getCount() == 0) {// 不存在记录，新建
			saveParm.setData("I", true);
			// 调用action执行事务
			TParm result = TIOM_AppServer.executeAction(
					"action.sum.SUMVitalSignAction",
					sumType.equals("P") ? "onSave" : "onCorNSave", saveParm);
			// 调用保存
			if (result.getErrCode() < 0) {
				this.messageBox_(result);
				this.messageBox("E0001");
				return false;
			}
			this.messageBox("P0001");
			onClear();
			return true;
		}
		// 不是插入--update
		saveParm.setData("I", false);
		// 不等于0说明已经有存在的改天数据了--作废、没作废--更新动作
		if (existParm.getData("DISPOSAL_FLG", 0) != null
				&& existParm.getData("DISPOSAL_FLG", 0).equals("Y")) {
			if (0 == this.messageBox("", "该数据已经作废过，\n是否再确定保存？",
					this.YES_NO_OPTION)) {
				// 调用action执行事务
				TParm result = TIOM_AppServer
						.executeAction("action.sum.SUMVitalSignAction",
								sumType.equals("P") ? "onSave" : "onCorNSave",
								saveParm);
				// 调用保存
				if (result.getErrCode() < 0) {
					this.messageBox_(result);
					this.messageBox("E0001");
					return false;
				}
				this.messageBox("P0001");
				return true;
			} else {
				this.messageBox("没有更新数据！");
				return true;
			}
		}
		// 直接诶更新--DISPOSAL_FLG==null或者N
		TParm result = TIOM_AppServer.executeAction(
				"action.sum.SUMVitalSignAction", sumType.equals("P") ? "onSave"
						: "onCorNSave", saveParm);
		// 调用保存
		if (result.getErrCode() < 0) {
			this.messageBox_(result);
			this.messageBox("保存失败！");
			return false;
		}
		this.messageBox("保存成功！");
		onClear();
		return true;
	}

	/**
	 * 成人 保存：从控件上面获得值，放入大对象，以被两个TDS使用
	 */
	public TParm getPValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// 检查日期
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// 住院天数
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// 术后天数
		masterParm.setData("ECTTIMES", "");// 目前ECT次数（暂时没用）
		masterParm.setData("MCFLG", "");// 月经（暂时没用）
		masterParm.setData("HOURSOFSLEEP", "");// 睡眠时数-小时（暂时没用）
		masterParm.setData("MINUTESOFSLEEP", "");// 睡眠时数-分（暂时没用）
		masterParm.setData("SPECIALSTOOLNOTE", "");// 特殊排便情况（暂时没用）
		masterParm.setData("INTAKEDIETQTY", "");// 输入-饮食量（暂时没用）
		masterParm.setData("OUTPUTDRAINQTY", "");// 排出-引流量（暂时没用）
		masterParm.setData("OUTPUTOTHERQTY", "");// 排出-其它（暂时没用）
		masterParm.setData("BATH", "");// 洗澡（暂时没用）
		masterParm.setData("GUESTKIND", "");// 会客（暂时没用）
		masterParm.setData("STAYOUTSIDE", "");// 外宿（暂时没用）
		masterParm.setData("LEAVE", "");// 外出（暂时没用）
		masterParm.setData("LEAVEREASONCODE", "");// 外出原因代码（暂时没用）
		masterParm.setData("NOTE", "");// 备注（暂时没用）
		masterParm.setData("STATUS_CODE", "");// 病历表单状态（暂时没用）
		masterParm.setData("DISPOSAL_FLG", "");// 作废注记
		masterParm.setData("DISPOSAL_REASON", "");// 作废理由
		masterParm.setData("STOOL", this.getValueString("STOOL"));// 大便
		masterParm.setData("AUTO_STOOL", this.getValue("AUTO_STOOL"));// 自行排便
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// 灌肠
		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// 引流
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueString("INTAKEFLUIDQTY"));// 入量-注射
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// 出量-小便量
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// 体重
		masterParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));// 身高
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// 自定义一
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// 自定义二
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// 自定义三
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// 记录人员

		masterParm.setData("URINETIMES", this.getValueString("URINETIMES"));// 小便
		masterParm.setData("VOMIT", this.getValueString("VOMIT"));// 呕吐
		masterParm.setData("HEAD_CIRCUM", this.getValueString("HEAD_CIRCUM"));// 头围
		masterParm.setData("ABDOMEN_CIRCUM",
				this.getValueString("ABDOMEN_CIRCUM"));// 腹围
		masterParm.setData("WEIGHT_G", this.getValueString("WEIGHT_G"));// 体重g
		masterParm.setData("WEIGHT_REASON",
				this.getValueString("WEIGHT_REASON"));// 体重未测原因

		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// 时段有6个
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// 记录时间
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// 物理降温
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// 体温种类
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// 病人动态
				if (!StringUtil.isNullString(this
						.getValueString("PTMOVECATECODE"))
						&& StringUtil.isNullString(this
								.getValueString("PTMOVECATEDESC"))) {
					TParm errParm = new TParm();
					errParm.setErr(-1, "请填写病人动态附注");
					return errParm;
				}
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// 病人动态附注
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// 记录时间
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// 物理降温
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// 病人动态
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// 病人动态附注
			}
			// 得到table上的主数据
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// 体温
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// 脉搏
			oneParm.setData("RESPIRE",
					TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// 呼吸
			/****************** shibl 20120330 modify ***************************/
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// 收缩压
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// 舒张压
			oneParm.setData("HEART_RATE",
					TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// 心率
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * 新生儿与儿童参数
	 * 
	 * @return
	 */
	public TParm getCorNValueFromUI() {
		TParm saveData = new TParm();
		TParm masterParm = new TParm();
		TParm detailParm = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String examineDate = StringTool.getString(StringTool.getDate(
				masterTable.getItemString(masterRow, "EXAMINE_DATE"),
				"yyyy/MM/dd"), "yyyyMMdd");
		masterParm.setData("ADM_TYPE", admType);
		masterParm.setData("CASE_NO", caseNo);
		masterParm.setData("EXAMINE_DATE", examineDate);// 检查日期
		masterParm.setData("IPD_NO", patInfo.getValue("IPD_NO", 0));
		masterParm.setData("MR_NO", patInfo.getValue("MR_NO", 0));
		masterParm
				.setData("INHOSPITALDAYS", this.getValueInt("INHOSPITALDAYS"));// 住院天数
		masterParm.setData("OPE_DAYS", this.getValue("OPE_DAYS"));// 术后天数
		masterParm.setData("ECTTIMES", "");// 目前ECT次数（暂时没用）
		masterParm.setData("MCFLG", "");// 月经（暂时没用）
		masterParm.setData("HOURSOFSLEEP", "");// 睡眠时数-小时（暂时没用）
		masterParm.setData("MINUTESOFSLEEP", "");// 睡眠时数-分（暂时没用）
		masterParm.setData("SPECIALSTOOLNOTE", "");// 特殊排便情况（暂时没用）
		masterParm.setData("INTAKEDIETQTY", "");// 输入-饮食量（暂时没用）
		masterParm.setData("OUTPUTDRAINQTY", "");// 排出-引流量（暂时没用）
		masterParm.setData("OUTPUTOTHERQTY", "");// 排出-其它（暂时没用）
		masterParm.setData("BATH", "");// 洗澡（暂时没用）
		masterParm.setData("GUESTKIND", "");// 会客（暂时没用）
		masterParm.setData("STAYOUTSIDE", "");// 外宿（暂时没用）
		masterParm.setData("LEAVE", "");// 外出（暂时没用）
		masterParm.setData("LEAVEREASONCODE", "");// 外出原因代码（暂时没用）
		masterParm.setData("NOTE", "");// 备注（暂时没用）
		masterParm.setData("STATUS_CODE", "");// 病历表单状态（暂时没用）
		masterParm.setData("DISPOSAL_FLG", "");// 作废注记
		masterParm.setData("DISPOSAL_REASON", "");// 作废理由
		masterParm.setData("STOOL", this.getValueString("STOOL"));// 大便
		// ===========================20150727 add
		// shibl=========================================
		masterParm.setData("STOOLX", this.getValueString("STOOLX"));// 稀糊便次
		masterParm.setData("STOOLX_NUM", this.getValueString("STOOLX_NUM"));// 稀糊便数量
		masterParm.setData("STOOLW", this.getValueString("STOOLW"));// 稀水便次
		masterParm.setData("STOOLW_NUM", this.getValueString("STOOLW_NUM"));// 稀水便数量
		masterParm.setData("STOOLB", this.getValueString("STOOLB"));// 血便次
		masterParm.setData("E_STOOL", this.getValueString("E_STOOL"));// 灌肠后正常次
		masterParm.setData("E_STOOLX", this.getValueString("E_STOOLX"));// 灌肠后稀糊便次
		masterParm.setData("E_STOOLX_NUM", this.getValueString("E_STOOLX_NUM"));// 灌肠后稀糊便数量
		masterParm.setData("E_STOOLW", this.getValueString("E_STOOLW"));// 灌肠后稀水便次
		masterParm.setData("E_STOOLW_NUM", this.getValueString("E_STOOLW_NUM"));// 灌肠后稀水便数量
		masterParm.setData("E_STOOLB", this.getValueString("E_STOOLB"));// 灌肠后血便
		masterParm.setData("URINETIMES_NUM",
				this.getValueString("URINETIMES_NUM"));// 小便数量
		masterParm.setData("BM_FLG", this.getValueString("BM_FLG"));// 含母乳注记
		// =======================================================================================
		masterParm.setData("ENEMA", this.getValue("ENEMA"));// 灌肠
		masterParm.setData("DRAINAGE", this.getValue("DRAINAGE"));// 引流
		masterParm.setData("INTAKEFLUIDQTY",
				this.getValueString("INTAKEFLUIDQTY"));// 入量-注射
		masterParm.setData("OUTPUTURINEQTY",
				this.getValueDouble("OUTPUTURINEQTY"));// 出量-小便量
		masterParm.setData("WEIGHT", this.getValue("WEIGHT"));// 体重
		masterParm.setData("HEIGHT", this.getValueDouble("HEIGHT"));// 身高
		masterParm.setData("USER_DEFINE_1", this.getValue("USER_DEFINE_1"));// 自定义一
		masterParm.setData("USER_DEFINE_1_VALUE",
				this.getValue("USER_DEFINE_1_VALUE"));
		masterParm.setData("USER_DEFINE_2", this.getValue("USER_DEFINE_2"));// 自定义二
		masterParm.setData("USER_DEFINE_2_VALUE",
				this.getValue("USER_DEFINE_2_VALUE"));
		masterParm.setData("USER_DEFINE_3", this.getValue("USER_DEFINE_3"));// 自定义三
		masterParm.setData("USER_DEFINE_3_VALUE",
				this.getValue("USER_DEFINE_3_VALUE"));
		masterParm.setData("USER_ID", Operator.getID());// 记录人员

		masterParm.setData("URINETIMES", this.getValueString("URINETIMES"));// 小便
		masterParm.setData("VOMIT", this.getValueString("VOMIT"));// 呕吐
		masterParm.setData("HEAD_CIRCUM", this.getValueString("HEAD_CIRCUM"));// 头围
		masterParm.setData("ABDOMEN_CIRCUM",
				this.getValueString("ABDOMEN_CIRCUM"));// 腹围
		masterParm.setData("WEIGHT_G", this.getValueString("WEIGHT_G"));// 体重g
		masterParm.setData("WEIGHT_REASON",
				this.getValueString("WEIGHT_REASON"));// 体重未测原因

		masterParm.setData("OPT_USER", Operator.getID());
		masterParm.setData("OPT_DATE", now);
		masterParm.setData("OPT_TERM", Operator.getIP());
		String columnIndex = this.getValueString("EXAMINESESSION");
		for (int i = 0; i < 6; i++) {// 时段有6个
			TParm oneParm = new TParm();
			oneParm.setData("ADM_TYPE", admType);
			oneParm.setData("CASE_NO", caseNo);
			oneParm.setData("EXAMINE_DATE", examineDate);
			oneParm.setData("EXAMINESESSION", i);
			if (("" + i).equals(columnIndex)) {
				oneParm.setData("RECTIME", this.getText("RECTIME"));// 记录时间
				oneParm.setData("SPCCONDCODE", this.getValue("SPCCONDCODE"));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS", this.getValue("PHYSIATRICS"));// 物理降温
				oneParm.setData("TMPTRKINDCODE", this.getValue("TMPTRKINDCODE"));// 体温种类
				oneParm.setData("NOTPRREASONCODE",
						this.getValue("NOTPRREASONCODE"));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						this.getValue("PTMOVECATECODE"));// 病人动态
				if (!StringUtil.isNullString(this
						.getValueString("PTMOVECATECODE"))
						&& StringUtil.isNullString(this
								.getValueString("PTMOVECATEDESC"))) {
					TParm errParm = new TParm();
					errParm.setErr(-1, "请填写病人动态附注");
					return errParm;
				}
				oneParm.setData("PTMOVECATEDESC",
						this.getValue("PTMOVECATEDESC"));// 病人动态附注
			} else {
				oneParm.setData("RECTIME", tprDtl.getValue("RECTIME", i));// 记录时间
				oneParm.setData("SPCCONDCODE",
						tprDtl.getValue("SPCCONDCODE", i));// 体温变化特殊情况
				oneParm.setData("PHYSIATRICS",
						tprDtl.getValue("PHYSIATRICS", i));// 物理降温
				// wanglong modify 20140428
				oneParm.setData(
						"TMPTRKINDCODE",
						tprDtl.getValue("TMPTRKINDCODE", i).equals("") ? this
								.getValue("TMPTRKINDCODE") : tprDtl.getValue(
								"TMPTRKINDCODE", i));
				oneParm.setData("NOTPRREASONCODE",
						tprDtl.getValue("NOTPRREASONCODE", i));// 未量原因
				oneParm.setData("PTMOVECATECODE",
						tprDtl.getValue("PTMOVECATECODE", i));// 病人动态
				oneParm.setData("PTMOVECATEDESC",
						tprDtl.getValue("PTMOVECATEDESC", i));// 病人动态附注
			}
			// 得到table上的主数据
			oneParm.setData("TEMPERATURE",
					TCM_Transform.getDouble(detailTable.getValueAt(0, i)));// 体温
			oneParm.setData("PLUSE",
					TCM_Transform.getDouble(detailTable.getValueAt(1, i)));// 脉搏
			oneParm.setData("RESPIRE",
					TCM_Transform.getDouble(detailTable.getValueAt(2, i)));// 呼吸
			/****************** shibl 20120330 modify ***************************/
			oneParm.setData("SYSTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(3, i)));// 收缩压
			oneParm.setData("DIASTOLICPRESSURE",
					TCM_Transform.getDouble(detailTable.getValueAt(4, i)));// 舒张压
			oneParm.setData("HEART_RATE",
					TCM_Transform.getDouble(detailTable.getValueAt(5, i)));// 心率
			oneParm.setData("USER_ID", Operator.getID());
			oneParm.setData("OPT_USER", Operator.getID());
			oneParm.setData("OPT_DATE", now);
			oneParm.setData("OPT_TERM", Operator.getIP());
			detailParm.setData(i + "PARM", oneParm.getData());
			detailParm.setCount(i + 1);
		}
		saveData.setData("MASET", masterParm.getData());
		saveData.setData("DETAIL", detailParm.getData());
		return saveData;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// 重新设置全局变量
		masterRow = -1;
		detailRow = -1;
		this.clearComponent();
		detailTable.removeRowAll();
		onQuery();// 执行查询
	}

	/**
	 * 清空组件
	 */
	public void clearComponent() {
		// 清理上半部分
		this.clearValue("EXAMINESESSION;RECTIME;" // INHOSPITALDAYS住院天数不清空;OPE_DAYS术后天数不清空
				+ "SPCCONDCODE;PHYSIATRICS;"// TMPTRKINDCODE体温种类不清空
				+ "NOTPRREASONCODE;PTMOVECATECODE;PTMOVECATEDESC");
		// 清理下半部分
		if (sumType.equals("P")) {
			clearPDownPanel();
		} else {
			clearCorNDownPanel();
		}
	}

	/**
	 * 清空成人
	 */
	public void clearPDownPanel() {
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;URINETIMES;VOMIT;HEAD_CIRCUM;ABDOMEN_CIRCUM;"
				+ "WEIGHT_G;WEIGHT_REASON;USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE");
	}

	/**
	 * 清空新生儿或儿童
	 */
	public void clearCorNDownPanel() {
		this.clearValue("STOOL;INTAKEFLUIDQTY;OUTPUTURINEQTY;WEIGHT;HEIGHT;URINETIMES;VOMIT;HEAD_CIRCUM;ABDOMEN_CIRCUM;"
				+ "WEIGHT_G;WEIGHT_REASON;USER_DEFINE_1;USER_DEFINE_2;USER_DEFINE_3;USER_DEFINE_1_VALUE;USER_DEFINE_2_VALUE;USER_DEFINE_3_VALUE;"
				+ "STOOLX;STOOLX_NUM;STOOLW;STOOLW_NUM;STOOLB;E_STOOL;E_STOOLX;E_STOOLX_NUM;E_STOOLW;E_STOOLW_NUM;E_STOOLB;URINETIMES_NUM;"
				+ "BM_FLG");
	}

	/**
	 * 作废
	 */
	public void onDefeasance() {
		int selRow = masterTable.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中作废数据！");
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\sum\\SUMDefeasance.x");
		if (value == null)
			return;
		// 得到选中行的EXAMINE_DATE
		String examineDate = StringTool.getString(
				StringTool.getDate(
						masterTable.getItemString(selRow, "EXAMINE_DATE"),
						"yyyy/MM/dd"), "yyyyMMdd");
		String defSel = "SELECT * FROM SUM_VITALSIGN WHERE ADM_TYPE = '"
				+ admType + "' AND CASE_NO = '" + caseNo
				+ "' AND EXAMINE_DATE = '" + examineDate + "'";
		TDS defData = new TDS();
		defData.setSQL(defSel);
		defData.retrieve();
		defData.setItem(0, "DISPOSAL_REASON", value);
		defData.setItem(0, "DISPOSAL_FLG", "Y");
		if (!defData.update()) {
			this.messageBox("作废失败！");
			return;
		}
		this.messageBox("作废成功！");
		onClear();
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (masterTable.getRowCount() <= 0) {
			this.messageBox("没有打印数据！");
			return;
		}
		TParm prtForSheetParm = new TParm();
		// 获得打印的区间
		TParm parmDate = new TParm();
		// 入院日期时间
		Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
		String sumKind = onAgeCode(this.caseNo, this.admType);
		parmDate.setData("IN_DATE", inDate);
		parmDate.setData("SUM_JHW", sumType.equals("P")?sumType:sumKind);
		TParm value = (TParm) this.openDialog(
				"%ROOT%\\config\\sum\\SUMChoiceDate.x", parmDate);
		if (value == null) {
			prtForSheetParm.setData("STOP", "取消打印！");
			return;
		}
		// 得到选择时间之间的‘天数差’+1===>打印的天数
		int differCount = StringTool.getDateDiffer(
				value.getTimestamp("END_DATE"),
				value.getTimestamp("START_DATE")) + 1;
		if (differCount <= 0) {
			prtForSheetParm.setData("STOP", "查询区域错误！");
			return;
		}
		String jhwName = "";
		String sumJhw = "";
		if ("I".equals(admType)) {
			sumJhw = value.getValue("SUM_JHW");
			jhwName = sumJhw.equals("") ? "SUMVitalSign_PrtSheet"
					: "SUMVitalSign_PrtSheet_" + sumJhw;
			if (jhwName.equals("")) {
				this.messageBox("种类名称未绑定相应模板文件");
				return;
			}
		} else if ("E".equals(admType)) {
			jhwName = "SUMVitalSign_PrtSheetE.jhw";
		}
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		Timestamp forDate = null;
		int pageNo = 1;
		for (int i = 0; i < pageCount; i++) {
			TParm parm = new TParm();
			Timestamp startDate = null;
			Timestamp endDate = null;
			if (i == 0)
				startDate = value.getTimestamp("START_DATE");
			else
				startDate = StringTool.rollDate(forDate, 1);
			int dif = 6;
			// if(i % 7 == 0)
			// dif = 6;
			// else
			// dif = 7 - (i * 7 - differCount) - 1;
			endDate = StringTool.rollDate(startDate, dif);
			forDate = endDate;
			parm.setData("START_DATE", startDate);
			parm.setData("END_DATE", endDate);
			TParm printData = getValueForPrt(parm, dif + 1, i + 1, sumJhw);
			if (printData == null)
				continue;
			printData.setData("PAGENO", "TEXT", pageNo++);
			if (printData.getData("STOP") != null) {
				this.messageBox(printData.getValue("STOP"));
				return;
			}
			// 加入体温单上传EMR
			Object returnObj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\sum\\" + jhwName, printData);
			if (returnObj != null) {
				String mr_no = patInfo.getValue("MR_NO", 0);
				EMRTool emrTool = new EMRTool(this.caseNo, mr_no, this);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String fileName = "体温单" + format.format(startDate) + "-"
						+ format.format(endDate);
				// ======== modify by chenxi 20120702 false表示体温单打印时只保存一笔
				emrTool.saveEMR(returnObj, fileName, "EMR100002",
						"EMR10000202", false);
			}
		}
	}

	/**
	 * 得到UI上的参数给打印程序
	 * 
	 * @return TPram
	 */
	private TParm getValueForPrt(TParm value, int differCount, int pageNo,
			String type) {
		TParm prtForSheetParm = new TParm();
		// 获得生命标记数据
		Vector tprSign = getVitalSignDate(value);
		if (((TParm) tprSign.get(0)).getCount() <= 0)
			return null;
		// 打印核心算法，将数据转化成坐标
		prtForSheetParm = dataToCoordinate(tprSign, differCount, type);
		String stationString = "";
		if ("E".equals(admType))
			stationString = patInfo.getValue("ERD_REGION_DESC", 0);
		else if ("I".equals(admType)) {
			String stationCode = patInfo.getValue("STATION_CODE", 0);
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
							+ stationCode + "'"));
			stationString = parm.getValue("STATION_DESC", 0);
		}
		String mrNo = patInfo.getValue("MR_NO", 0);
		// 通过MR_NO拿到性别
		Pat pat = Pat.onQueryByMrNo(mrNo);
		String sex = pat.getSexString();
		String ipdNo = patInfo.getValue("IPD_NO", 0);
		String bedNo = patInfo.getValue("BED_NO", 0);
		String bedString = "";
		if ("E".equals(admType))
			bedString = patInfo.getValue("BED_DESC", 0);
		else if ("I".equals(admType)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO='" + bedNo
							+ "'"));
			bedString = bedParm.getValue("BED_NO_DESC", 0);
		}
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ patInfo.getValue("IN_DEPT_CODE", 0) + "'"));
		String Name = pat.getName();
		String age = DateUtil.showAge(pat.getBirthday(),
				patInfo.getTimestamp("IN_DATE", 0));
		prtForSheetParm.setData("MR_NO", "TEXT", mrNo);
		prtForSheetParm.setData("IPD_NO", "TEXT", ipdNo);
		prtForSheetParm.setData("DEPT", "TEXT",
				deptParm.getValue("DEPT_CHN_DESC", 0));
		prtForSheetParm.setData("BED_NO", "TEXT", bedString);
		prtForSheetParm.setData("STATION", "TEXT", stationString);
		prtForSheetParm.setData("NAME", "TEXT", Name);
		prtForSheetParm.setData("SEX", "TEXT", sex);
		prtForSheetParm.setData("AGE", "TEXT", age);
		prtForSheetParm.setData("IN_DATE", "TEXT", StringTool.getString(
				patInfo.getTimestamp("IN_DATE", 0), "yyyy年MM月dd日"));
		prtForSheetParm.setData("BIRTH", "TEXT",
				StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		return prtForSheetParm;
	}

	/**
	 * 打印核心算法，将数据转化成坐标
	 * 
	 * @param tprSign
	 *            Vector 主要数据
	 * @param differCount
	 *            int 要打印的天数（endDate-startDate）
	 */
	public TParm dataToCoordinate(Vector tprSign, int differCount, String type) {
		TParm mainPrtData = new TParm();
		// 主细表数据
		TParm master = (TParm) tprSign.get(0); // 下面的数据
		TParm detail = (TParm) tprSign.get(1); // (点线)
		int countDays = detail.getCount("SYSTOLICPRESSURE") / 6;
		Vector MorningfinalSystol = new Vector();
		Vector MorningfinalDiastol = new Vector();
		Vector AfternoonfinalSystol = new Vector();
		Vector AfternoonfinalDiastol = new Vector();
		for (int i = 0; i < countDays; i++) {
			String Morningsystol = "";
			String Morningdiastol = "";
			String Afternoonsystol = "";
			String Afternoondiastol = "";
			for (int j = 0; j < 6; j++) {
				if (!StringUtil.isNullString(detail.getValue(
						"SYSTOLICPRESSURE", i * 6 + j))
						&& detail.getInt("SYSTOLICPRESSURE", i * 6 + j) > 0) {
					if (MorningList.contains(j)) {
						Morningsystol = detail.getValue("SYSTOLICPRESSURE", i
								* 6 + j);
					} else if (AfternoonList.contains(j)) {
						Afternoonsystol = detail.getValue("SYSTOLICPRESSURE", i
								* 6 + j);
					}
				}
				if (!StringUtil.isNullString(detail.getValue(
						"DIASTOLICPRESSURE", i * 6 + j))
						&& detail.getInt("DIASTOLICPRESSURE", i * 6 + j) > 0) {
					if (MorningList.contains(j)) {
						Morningdiastol = detail.getValue("DIASTOLICPRESSURE", i
								* 6 + j);
					} else if (AfternoonList.contains(j)) {
						Afternoondiastol = detail.getValue("DIASTOLICPRESSURE",
								i * 6 + j);
					}
				}
			}
			MorningfinalSystol.add(Morningsystol);
			MorningfinalDiastol.add(Morningdiastol);
			AfternoonfinalSystol.add(Afternoonsystol);
			AfternoonfinalDiastol.add(Afternoondiastol);
		}
		// 得到所有日期
		Vector examineDate = (Vector) master.getData("EXAMINE_DATE");
		// 得到报表下面的数据
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0,c99 = 0,c98 = 0,c5 = 0, c6 = 0, c7 = 0, c3S = 0, c3D = 0, c8 = 0, c9 = 0, c10 = 0, c11 = 0, opeDaysVC = 0, cDrainage = 0, cEnema = 0, cAutoStool = 0;
		int userDefine = 0;
		int Sh1 = 0;
		// 计数依次拿出常量
		int countWord = 0;
		// 如果选择区间的天数>数据/6说明有作废数据，以数据/6为“新”天数--体重的有效数据/6
		int newDates = detail.getCount("TEMPERATURE") / 6;
		if (differCount > newDates)
			differCount = newDates;
		// 根据（天数/7）得到需要花的总页数
		int pageCount = differCount / 7 + 1;
		if (differCount % 7 == 0)
			pageCount = differCount / 7;
		TParm controlPage = new TParm();
		// 外层控制页
		for (int i = 1; i <= pageCount; i++) {
			ArrayList dotList_T = new ArrayList();// 体温
			ArrayList dotList_PL = new ArrayList();// 脉搏
			ArrayList dotList_R = new ArrayList();// 呼吸
			ArrayList dotList_P = new ArrayList();// 物理降温
			ArrayList dotList_H = new ArrayList();// 心率
			ArrayList lineList_T = new ArrayList();
			ArrayList lineList_PL = new ArrayList();
			ArrayList lineList_R = new ArrayList();
			ArrayList lineList_P = new ArrayList();
			ArrayList lineList_H = new ArrayList();
			// 设置页数
			controlPage.addData("PAGE", "" + i);
			// 嵌套子循环控制天----------------------start-------------------------
			// int date = differCount - (i * 7) % 7;
			int date;
			if (i * 7 <= differCount)
				date = 7;
			else
				date = 7 - (i * 7 - differCount);
			int xT = -1;
			int yT = -1;
			int xPL = -1;
			int yPL = -1;
			int xR = -1;
			int yR = -1;
			int xH = -1;
			int yH = -1;
			String notForward = "";
			String notForward2 = "";
			for (int j = 1; j <= date; j++) {
				// 最内层控制时段
				Vector respireValue = new Vector();
				// double temForward = (Double) ( (Vector) temperatureV.get( (1
				// + (j - 1) * 6) - 1)).get(0);
				double temper1 = detail.getDouble("TEMPERATURE",
						(1 + (j - 1) * 6) - 1);
				String not1 = nullToEmptyStr(detail.getValue("NOTPRREASONCODE",
						(1 + (j - 1) * 6) - 1));
				if (temper1 != 0
						|| (temper1 == 0 && !StringUtil.isNullString(not1))) {
					notForward = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
					notForward2 = detail.getValue("NOTPRREASONCODE",
							(1 + (j - 1) * 6) - 1) + "";
				}
				int temperHorizontal = 0;
				int temperVertical = 0;
				for (int exa = 1; exa <= 6; exa++) {
					// 得到体温-------------------start---------------------------
					double temper = detail.getDouble("TEMPERATURE", exa
							+ (j - 1) * 6 - 1);
					double temperBak = temper;
					String tempKindCode = nullToEmptyStr(detail.getValue(
							"TMPTRKINDCODE", exa + (j - 1) * 6 - 1));
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// continue;
						// 当温度<=35的时候写“体温不升”
						if (temper <= 35) {
							// 最低边界35度
							temper = 35;
							controlPage.addData(
									"NORAISE" + (exa + (j - 1) * 6), "体温不升");
						}
						// 得到体温的横纵坐标（点）
						temperHorizontal = countHorizontal(j, exa);
						temperVertical = (int) (getVertical(temper, "T") + 0.5); // 取整
						int dataTemper[] = new int[] {};
						if (temperBak >= 35) {// shibl modify 温度种类不能为空
							if (tempKindCode.equals("4")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7 };
							} else if (tempKindCode.equals("3")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6 };
							} else {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4 };
							}
						} else if (temperBak < 35) {
							if (tempKindCode.equals("4")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, temperHorizontal + 6,
										temperVertical + 6, 7, 1 };
							} else if (tempKindCode.equals("3")) {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 6, 1 };
							} else {
								// 得到一个点的坐标
								dataTemper = new int[] { temperHorizontal,
										temperVertical, 6, 6, 4, 1 };
							}
						}
						// 存入所有点
						dotList_T.add(dataTemper);
					}
					// --------------------------end-----------------------------
					// 得到脉搏的点----------------start--------------------------
					double pluse = detail.getDouble("PLUSE", exa + (j - 1) * 6
							- 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					int pluseHorizontal = 0;
					int pluseVertical = 0;
					if (pluse != 0) {
						// continue;
						// 得到脉搏的横纵坐标（点）
						pluseHorizontal = countHorizontal(j, exa);
						if (type.equals("01"))// 新生儿
							pluse = pluse - 40;
						//start machao 新生儿体温单脉搏坐标位置错误，原因  纵坐标变更
						if(type.equals("N")){
							pluse = pluse - 40;
						}
						//end machao
						pluseVertical = (int) (getVertical(pluse, "PL") + 0.5); // 取整
						int dataPluse[] = new int[] {};
						// 得到一个点的坐标
						dataPluse = new int[] { pluseHorizontal, pluseVertical,
								6, 6, 4 };
						for (int k = 0; k < dotList_T.size(); k++) {
							if (pluseHorizontal == ((int[]) dotList_T.get(k))[0]
									&& pluseVertical == ((int[]) dotList_T
											.get(k))[1]) {
								// 得到一个点的坐标
								dataPluse = new int[] { pluseHorizontal,
										pluseVertical, 6, 6, 6 };
								break;
							}
						}
						// 存入所有点
						dotList_PL.add(dataPluse);
					}
					// ---------------------------end----------------------------
					// 得到呼吸--------------------start--------------------------
					respireValue.add(detail.getValue("RESPIRE", exa + (j - 1)
							* 6 - 1));
					/*
					 * Double respire = Double.parseDouble( ( (Vector)
					 * respireV.get( (exa + (j - 1) * 6) - 1)).get(0) + "");
					 * //当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点 if (respire == 0)
					 * continue; //得到呼吸的横纵坐标（点） int respireHorizontal =
					 * countHorizontal(j, exa); int respireVertical = (int)
					 * (getVertical(respire,"R") + 0.5); //取整 //得到一个点的坐标 int
					 * dataRespire[] = new int[] { respireHorizontal,
					 * respireVertical, 6, 6, 4}; //存入所有点
					 * dotList_R.add(dataRespire);
					 */
					// ----------------------------end---------------------------
					// 得到心率的点----------------start--------------------------
					double heartRate = detail.getDouble("HEART_RATE",
							(exa + (j - 1) * 6) - 1);
					// 当为NULL的时候为测量，但框架自动转换成0，那么当为0的时候不花点
					int heartRateHorizontal = 0;
					int heartRateVertical = 0;
					if (heartRate != 0) {
						// continue;
						// 得到心率的横纵坐标（点）
						heartRateHorizontal = countHorizontal(j, exa);
						heartRateVertical = (int) (getVertical(heartRate, "H") + 0.5); // 取整
						// 得到一个点的坐标
						int dataHeartRate[] = new int[] { heartRateHorizontal,
								heartRateVertical, 6, 6, 6 };
						// 存入所有点
						dotList_H.add(dataHeartRate);
					}
					// ---------------------------end----------------------------
					// 得到物理降温-----------------start--------------------------
					String tempPhsi = detail.getValue("PHYSIATRICS", exa
							+ (j - 1) * 6 - 1);
					if (!StringUtil.isNullString(tempPhsi)) {
						// 得到数字类型的
						double phsiatrics = TCM_Transform.getDouble(tempPhsi);
						if (phsiatrics <= 35) {
							// 最低边界35度
							phsiatrics = 35;
						}
						// 得到体温的横纵坐标（点）
						int phsiHorizontal = countHorizontal(j, exa);
						int phsiVertical = (int) (getVertical(phsiatrics, "P") + 0.5); // 取整
						// 得到一个点的坐标
						int dataPhsi[] = new int[] { phsiHorizontal,
								phsiVertical, 6, 6, 6 };
						// 存入所有点
						dotList_P.add(dataPhsi);
						// 得到物理降温线-----------start--------------------------
						int dataTempLine[] = new int[] { temperHorizontal + 3,
								temperVertical + 3, phsiHorizontal + 3,
								phsiVertical + 3, 1 };
						lineList_P.add(dataTempLine);
					}
					// ----------------------------end---------------------------
					// 得到为测量原因
					String not = nullToEmptyStr(detail.getValue(
							"NOTPRREASONCODE", (exa + (j - 1) * 6) - 1));
					if (!StringUtil.isNullString(not)) {
						String sql1 = " SELECT CHN_DESC "
								+ " FROM SYS_DICTIONARY"
								+ " WHERE GROUP_ID='SUM_NOTMPREASON'"
								+ " AND   ID = '" + not + "'";
						TParm result1 = new TParm(TJDODBTool.getInstance()
								.select(sql1));
						controlPage.addData("REASON" + (exa + (j - 1) * 6),
								result1.getValue("CHN_DESC", 0));
					}
					// 得到体温的线----------------start--------------------------
					if (temper != 0.0 && !StringUtil.isNullString(tempKindCode)) {
						// if(countWord ==0 ||
						// StringTool.getDateDiffer(StringTool.getTimestamp(""+examineDate.get(countWord),"yyyyMMdd"),
						// StringTool.getTimestamp(""+examineDate.get(countWord
						// - 1),"yyyyMMdd")) <= 1 ||
						// exa != 1)
						if (xT != -1 && yT != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward)) {
							int dataTempLine[] = new int[] { xT + 3, yT + 3,
									temperHorizontal + 3, temperVertical + 3, 1 };
							lineList_T.add(dataTempLine);
						}
						xT = temperHorizontal;
						yT = temperVertical;
					}
					// temForward = temper;
					if (temper != 0
							|| (temper == 0 && !StringUtil.isNullString(not))) {
						notForward = not;
					}
					// --------------------------end----------------------------
					// 得到脉搏的线----------------start--------------------------
					if (pluse != 0) {
						if (xPL != -1 && yPL != -1
								&& StringUtil.isNullString(not)
								&& StringUtil.isNullString(notForward2)) {
							int dataPluseLine[] = new int[] { xPL + 3, yPL + 3,
									pluseHorizontal + 3, pluseVertical + 3, 1 };
							lineList_PL.add(dataPluseLine);
						}
						xPL = pluseHorizontal;
						yPL = pluseVertical;
					}
					// temForward = temper;
					if (pluse != 0
							|| (pluse == 0 && !StringUtil.isNullString(not))) {
						notForward2 = not;
					}
					// --------------------------end----------------------------
					// 得到呼吸的线----------------start--------------------------
					/*
					 * if (xR != -1 && yR != -1 && "null".equals(not)) { int
					 * dataRespireLine[] = new int[] { xR + 3, yR + 3,
					 * respireHorizontal + 3, respireVertical + 3, 1};
					 * lineList_R.add(dataRespireLine); } xR =
					 * respireHorizontal; yR = respireVertical;
					 */
					// --------------------------end----------------------------
					// 得到心率的线----------------start--------------------------
					if (heartRate != 0) {
						if (xH != -1 && yH != -1
								&& StringUtil.isNullString(not)) {
							int dataHeartRateLine[] = new int[] { xH + 3,
									yH + 3, heartRateHorizontal + 3,
									heartRateVertical + 3, 1 };
							lineList_H.add(dataHeartRateLine);
						}
						xH = heartRateHorizontal;
						yH = heartRateVertical;
					}
					// --------------------------end----------------------------
					// 病人动态信息----------------start--------------------------
					String ptMoveCode = nullToEmptyStr(detail.getValue(
							"PTMOVECATECODE", exa + (j - 1) * 6 - 1));
					if (!StringUtil.isNullString(ptMoveCode)) {
						String ptMoveDesc = nullToEmptyStr(detail.getValue(
								"PTMOVECATEDESC", exa + (j - 1) * 6 - 1));
						controlPage.addData("MOVE" + (exa + (j - 1) * 6),
								ptMoveCode + "||" + ptMoveDesc);
					}
				}
				// 得到日期-------------------------start-------------------------
				String tenmpDate = examineDate.get(countWord++).toString();
				String fomatDate = "";
				if (countWord - 1 == 0) {
					fomatDate = tenmpDate.substring(0, 4) + "."
							+ tenmpDate.substring(4, 6) + "."
							+ tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				} else {
					String tenmpDateForward = examineDate.get(countWord - 2)
							.toString();
					if (!tenmpDateForward.substring(2, 4).equals(
							tenmpDate.substring(2, 4)))
						fomatDate = tenmpDate.substring(0, 4) + "."
								+ tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else if (!tenmpDateForward.substring(4, 6).equals(
							tenmpDate.substring(4, 6)))
						fomatDate = tenmpDate.substring(4, 6) + "."
								+ tenmpDate.substring(6);
					else
						fomatDate = tenmpDate.substring(6);
					controlPage.addData("DATE" + j, fomatDate);
				}
				controlPage.addData("OPEDAY" + j,
						master.getData("OPE_DAYS", opeDaysVC++));
				// 入院日期时间
				Timestamp inDate = patInfo.getTimestamp("IN_DATE", 0);
				// 得到该出生天数（该日子-出生日子）-------------------------------
				int dates = getBornDateDiffer(tenmpDate,
						StringTool.getTimestampDate(inDate)) + 1;
				// dates = getInHospDaysE();
				controlPage.addData("INDATE" + j, dates == 0 ? "" : dates);
				// 手术后期OPEDAYn
				// -----------------------------------
				// 得到报表下面的数据----------------------start------------------------
				for (int k = 0; k < respireValue.size(); k++) {
					try {
						controlPage.addData(
								"L1_" + j + (k + 1),
								(int) Double.parseDouble(""
										+ clearZero(respireValue.get(k))));
					} catch (Exception e) {
						controlPage.addData("L1_" + j + (k + 1),
								clearZero(respireValue.get(k)));
					}
				}
				if (sumType.equals("P")) {
					if (StringUtil.isNullString(master.getValue("STOOL",
							cAutoStool))
							&& StringUtil.isNullString(master.getValue("ENEMA",
									cEnema))) {
						controlPage.addData("L2" + j, "");
						controlPage.addData("L12" + j, "");
						controlPage.addData("L13" + j, "");
						controlPage.addData("L14" + j, "");
					} else {
						controlPage.addData("L2" + j,
								master.getData("STOOL", cAutoStool));
						if (StringUtil.isNullString(master.getValue("ENEMA",
								cEnema))) {
							controlPage.addData("L12" + j, "");
							controlPage.addData("L13" + j, "");
							controlPage.addData("L14" + j, "");
						} else {
							controlPage.addData("L12" + j,
									master.getValue("ENEMA", cEnema));
							controlPage.addData("L13" + j, "/");
							controlPage.addData("L14" + j, "E");
						}
					}
				}else{
					//增加大便显示
					onAddCorNStool(controlPage,master.getRow(cAutoStool),j, type);
				}
				cAutoStool++;
				cEnema++;

				if ((clearZero(MorningfinalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(MorningfinalDiastol.get(c3D)) + "")
								.length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L3" + j, MorningfinalSystol.get(c3S)
							+ "/" + MorningfinalDiastol.get(c3D));

				if ((clearZero(AfternoonfinalSystol.get(c3S)) + "").length() == 0
						&& (clearZero(AfternoonfinalDiastol.get(c3D)) + "")
								.length() == 0)
					controlPage.addData("L3", "");
				else
					controlPage.addData("L31" + j,
							AfternoonfinalSystol.get(c3S) + "/"
									+ AfternoonfinalDiastol.get(c3D));
				c3S++;
				c3D++;

				// 儿童
				if (type.equals("N")) {
					controlPage.addData(
							"L8" + j,
							clearZero(StringTool.round(
									master.getDouble("WEIGHT_G", c6++), 1)));
				} else if (type.equals("C")) {
					controlPage.addData(
							"L8" + j,
							clearZero(StringTool.round(
									master.getDouble("WEIGHT", c6++), 1)));
				} else {
					controlPage.addData("L7" + j,
							clearZero(master.getData("WEIGHT", c6++)));
				}
				// 新生儿 、儿童
				if (type.equals("N") || type.equals("C")) {
					controlPage.addData("L7" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					//母乳标记
					String bmflg=master.getValue("BM_FLG", c4++);
					String bmstr=bmflg.equals("Y")?"母乳":"";
					Object objin=clearZero(master.getData("INTAKEFLUIDQTY", c99++));
					// modify by wangb 2017/08/04 不勾选母乳，只显示数量即可，无需显示单位
					if (type.equals("C")) {
						if (bmflg.equals("Y")) {
							if (String.valueOf(objin).length() == 0) {
								controlPage.addData("L4" + j, bmstr);
							} else {
								controlPage.addData("L4" + j, bmstr + "+"
										+ objin);
							}
						} else {
							controlPage.addData("L4" + j, objin.toString());
						}
					} else {
						controlPage.addData("L4" + j,
								objin.toString().length()>0?bmstr+objin+"ml":"");
					}
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					String obj8 = master.getValue("URINETIMES", c8++);// 小便
					Object obj8num=clearZero(master.getValue("URINETIMES_NUM", c98++));
					// modify by wangb 2017/08/04 小便次数为0或空但尿量有数值时，针对插尿管患者，显示时只需显示尿量
					if (obj8 == null || obj8.equals("")) {
						if ("C".equals(type)
								&& String.valueOf(obj8num).length() > 0) {
							controlPage.addData("L9" + j, obj8num + "ml");
						} else {
							controlPage.addData("L9" + j, "");
						}
					} else {
						if ("C".equals(type) && "0".equals(obj8)
								&& String.valueOf(obj8num).length() > 0) {
							controlPage.addData("L9" + j, obj8num + "ml");
						} else {
							controlPage.addData("L9" + j, obj8
									+ "次"
									+ (obj8num.equals("") ? "" : "+" + obj8num
											+ "ml"));
						}
					}
					Object obj9 = master.getValue("VOMIT", c9++);// 呕吐
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L2" + j, "");
					else
						controlPage.addData("L2" + j, obj9);
					Object obj10 = master.getData("HEAD_CIRCUM", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L11" + j, "");
					else
						controlPage.addData("L11" + j,
								StringTool.round(TypeTool.getDouble(obj10), 1));
					Object obj11 = master.getData("ABDOMEN_CIRCUM", c11++);
					if (obj11 == null || obj11.equals(""))
						controlPage.addData("L6" + j, "");
					else
						controlPage.addData("L6" + j,
								StringTool.round(TypeTool.getDouble(obj11), 1));
					// add by wangb 2017/08/07 对于自定义测量项目，需要显示在体温单中 START
					if (type.equals("C")) {
						for (int n = 1; n <= 3; n++) {
							// 腹腔引流
							if ("2".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L15" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
							// T型管引流
							if ("3".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L16" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
							// 胃肠减压
							if ("4".equals(master.getValue("USER_DEFINE_CODE_" + n,
									userDefine))) {
								controlPage.addData("L17" + j, master.getData(
										"USER_DEFINE_" + n + "_VALUE",
										userDefine));
							}
						}
						userDefine++;
					}
					// add by wangb 2017/08/07 对于自定义测量项目，需要显示在体温单中 END
				} else if (type.equals("P")) {// 成人
					controlPage.addData("L4" + j,
							clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					controlPage.addData("L6" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					controlPage.addData("L11" + j,
							clearZero(master.getData("DRAINAGE", cDrainage++)));
					controlPage.addData("L80", "体重未测");
					String obj8 = master.getValue("WEIGHT_REASON", c8++);
					if (obj8 == null || obj8.equals(""))
						controlPage.addData("L8" + j, "");
					else
						controlPage.addData("L8" + j, getWeightReason(obj8));

					for (int l = 0; l < master.getCount("USER_DEFINE_1")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_1", l)))
							continue;
						controlPage.addData("L90",
								master.getData("USER_DEFINE_1", l));
						break;
					}
					Object obj9 = master.getData("USER_DEFINE_1_VALUE", c9++);
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L9" + j, "");
					else
						controlPage.addData("L9" + j, obj9);
					for (int l = 0; l < master.getCount("USER_DEFINE_2")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_2", l)))
							continue;
						controlPage.addData("L100",
								master.getData("USER_DEFINE_2", l));
						break;
					}
					Object obj10 = master.getData("USER_DEFINE_2_VALUE", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L10" + j, "");
					else
						controlPage.addData("L10" + j, obj10);
				} else if (type.equals("")) {
					controlPage.addData("L4" + j,
							clearZero(master.getData("INTAKEFLUIDQTY", c4++)));
					controlPage.addData("L5" + j,
							clearZero(master.getData("OUTPUTURINEQTY", c5++)));
					controlPage.addData("L6" + j,
							clearZero(master.getData("HEIGHT", c7++)));
					controlPage.addData("L11" + j,
							clearZero(master.getData("DRAINAGE", cDrainage++)));
					for (int l = 0; l < master.getCount("USER_DEFINE_1")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_1", l)))
							continue;
						controlPage.addData("L80",
								master.getData("USER_DEFINE_1", l));
						break;
					}
					Object obj8 = master.getData("USER_DEFINE_1_VALUE", c8++);
					if (obj8 == null || obj8.equals(""))
						controlPage.addData("L8" + j, "");
					else
						controlPage.addData("L8" + j, obj8);
					for (int l = 0; l < master.getCount("USER_DEFINE_2")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_2", l)))
							continue;
						controlPage.addData("L90",
								master.getData("USER_DEFINE_2", l));
						break;
					}
					Object obj9 = master.getData("USER_DEFINE_2_VALUE", c9++);
					if (obj9 == null || obj9.equals(""))
						controlPage.addData("L9" + j, "");
					else
						controlPage.addData("L9" + j, obj9);
					for (int l = 0; l < master.getCount("USER_DEFINE_3")
							&& j == 1; l++) {
						if (StringUtil.isNullString(master.getValue(
								"USER_DEFINE_3", l)))
							continue;
						controlPage.addData("L100",
								master.getData("USER_DEFINE_3", l));
						break;
					}
					Object obj10 = master.getData("USER_DEFINE_3_VALUE", c10++);
					if (obj10 == null || obj10.equals(""))
						controlPage.addData("L10" + j, "");
					else
						controlPage.addData("L10" + j, obj10);
				}
			}
			// 体温点
			int pageDataForT[][] = new int[dotList_T.size()][5];
			for (int j = 0; j < dotList_T.size(); j++)
				pageDataForT[j] = (int[]) dotList_T.get(j);
			// 脉搏点
			int pageDataForPL[][] = new int[dotList_PL.size()][5];
			for (int j = 0; j < dotList_PL.size(); j++)
				pageDataForPL[j] = (int[]) dotList_PL.get(j);
			// 呼吸
			int pageDataForR[][] = new int[dotList_R.size()][5];
			for (int j = 0; j < dotList_R.size(); j++)
				pageDataForR[j] = (int[]) dotList_R.get(j);
			// 心率
			int pageDataForH[][] = new int[dotList_H.size()][5];
			for (int j = 0; j < dotList_H.size(); j++)
				pageDataForH[j] = (int[]) dotList_H.get(j);
			// 物理降温点
			int pageDataForP[][] = new int[dotList_P.size()][5];
			for (int j = 0; j < dotList_P.size(); j++)
				pageDataForP[j] = (int[]) dotList_P.get(j);
			// 体温线
			int pageDataForTLine[][] = new int[lineList_T.size()][5];
			for (int j = 0; j < lineList_T.size(); j++) {
				pageDataForTLine[j] = (int[]) lineList_T.get(j);
			}
			// 脉搏线
			int pageDataForPLLine[][] = new int[lineList_PL.size()][5];
			for (int j = 0; j < lineList_PL.size(); j++)
				pageDataForPLLine[j] = (int[]) lineList_PL.get(j);
			// 呼吸线
			int pageDataForRLine[][] = new int[lineList_R.size()][5];
			for (int j = 0; j < lineList_R.size(); j++)
				pageDataForRLine[j] = (int[]) lineList_R.get(j);
			// 体重线
			int pageDataForPLine[][] = new int[lineList_P.size()][5];
			for (int j = 0; j < lineList_P.size(); j++)
				pageDataForPLine[j] = (int[]) lineList_P.get(j);
			// 心率线
			int pageDataForHLine[][] = new int[lineList_H.size()][5];
			for (int j = 0; j < lineList_H.size(); j++) {
				pageDataForHLine[j] = (int[]) lineList_H.get(j);
			}
			controlPage.addData("TEMPDOT", pageDataForT);
			controlPage.addData("PLUSEDOT", pageDataForPL);
			// controlPage.addData("RESPIREDOT", pageDataForR);
			controlPage.addData("PHSIDOT", pageDataForP);
			controlPage.addData("RESPIREDOT", pageDataForH);
			controlPage.addData("TEMPLINE", pageDataForTLine);
			controlPage.addData("PLUSELINE", pageDataForPLLine);
			// controlPage.addData("RESPIRELINE", pageDataForRLine);
			controlPage.addData("PHSILINE", pageDataForPLine);
			controlPage.addData("RESPIRELINE", pageDataForHLine);
			// ----------------------------end----------------------------------
		}
		// 设置页数
		controlPage.setCount(pageCount);
		controlPage.addData("SYSTEM", "COLUMNS", "PAGE");
		mainPrtData.setData("TABLE", controlPage.getData());
		return mainPrtData;
	}

	/**
	 * 添加大便数据
	 * 
	 * @param parm
	 */
	public void onAddCorNStool(TParm controlPage, TParm parm, int num, String type) {
		StringBuffer Stool = new StringBuffer();
		StringBuffer Enema = new StringBuffer();
		// modify by wangb 2017/08/04 儿童体温单调整，无大便时，默认为0，应显示为“0”
		if ("C".equals(type)) {
			// 正常
			if (!"".equals(parm.getValue("STOOL"))) {
				Stool.append(parm.getValue("STOOL"));
			}
		} else {
			// 正常
			if (!clearZero(parm.getValue("STOOL")).equals("")) {
				Stool.append(parm.getValue("STOOL"));
			}
		}
		// 稀糊便
		if (!clearZero(parm.getValue("STOOLX_NUM")).equals("")
				&& !parm.getValue("STOOLX").equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			
			// modify by wangb 2017/08/04 有大便量时，需要显示量的单位“ml”
			if ("C".equals(type)) {
				Stool.append(parm.getValue("STOOLX_NUM")
						+ "ml"
						+ "/"
						+ (parm.getValue("STOOLX").equals("1") ? "" : parm
								.getValue("STOOLX")) + "X");
			} else {
				Stool.append(parm.getValue("STOOLX_NUM")
						+ "/"
						+ (parm.getValue("STOOLX").equals("1") ? "" : parm
								.getValue("STOOLX")) + "X");
			}
		} else if (clearZero(parm.getValue("STOOLX_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLX")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append((parm.getValue("STOOLX").equals("1") ? "" : parm
					.getValue("STOOLX")) + "X");
		}
		// 稀水便
		if (!clearZero(parm.getValue("STOOLW_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLW")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			// modify by wangb 2017/08/04 有大便量时，需要显示量的单位“ml”
			if ("C".equals(type)) {
				Stool.append(parm.getValue("STOOLW_NUM")
						+ "ml"
						+ "/"
						+ (parm.getValue("STOOLW").equals("1") ? "" : parm
								.getValue("STOOLW")) + "※");
			} else {
				Stool.append(parm.getValue("STOOLW_NUM")
						+ "/"
						+ (parm.getValue("STOOLW").equals("1") ? "" : parm
								.getValue("STOOLW")) + "※");
			}
		} else if (clearZero(parm.getValue("STOOLW_NUM")).equals("")
				&& !clearZero(parm.getValue("STOOLW")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append((parm.getValue("STOOLW").equals("1") ? "" : parm
					.getValue("STOOLW")) + "※");
		}
		// 血便
		if (!clearZero(parm.getValue("STOOLB")).equals("")) {
			if (Stool.length() > 0) {
				Stool.append(stoolLink);
			}
			Stool.append(parm.getValue("STOOLB") + "血便");
		}
		StringBuffer EStool = new StringBuffer();
		if (!clearZero(parm.getValue("ENEMA")).equals("")) {
			Enema.append(parm.getValue("ENEMA").equals("1") ? "/E" : "/"+parm
							.getValue("ENEMA") + "E");
			EStool.append("(");
			// 正常
			if (!clearZero(parm.getValue("E_STOOL")).equals("")) {
				EStool.append(parm.getValue("E_STOOL"));
			}
			// 稀糊便
			if (!clearZero(parm.getValue("E_STOOLX_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLX")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLX_NUM")
						+ "/"
						+ (parm.getValue("E_STOOLX").equals("1") ? "" : parm
								.getValue("E_STOOLX")) + "X");
			} else if (clearZero(parm.getValue("E_STOOLX_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLX")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append((parm.getValue("E_STOOLX").equals("1") ? "" : parm
						.getValue("E_STOOLX")) + "X");
			}
			// 稀水便
			if (!clearZero(parm.getValue("E_STOOLW_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLW")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLW_NUM")
						+ "/"
						+ (parm.getValue("E_STOOLW").equals("1") ? "" : parm
								.getValue("E_STOOLW")) + "※");
			} else if (clearZero(parm.getValue("E_STOOLW_NUM")).equals("")
					&& !clearZero(parm.getValue("E_STOOLW")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append((parm.getValue("E_STOOLW").equals("1") ? "" : parm
						.getValue("E_STOOLW")) + "※");
			}

			// 血便
			if (!clearZero(parm.getValue("E_STOOLB")).equals("")) {
				if (EStool.length() > 0) {
					EStool.append(stoolLink);
				}
				EStool.append(parm.getValue("E_STOOLB") + "血便");
			}
			EStool.append(")");
		}
		
		//MODIFY BY YANGJJ 20150922
		//if(Enema.toString().length()>0){
		// modify by wangb 2017/08/04 儿童体温单调整，大便只填写正常次数时，只显示数字即可，无需显示“+”号
		if ("C".equals(type)) {
			if (Stool.toString().length() > 0
					&& (!"0".equals(parm.getValue("STOOLX")) && !"0"
							.equals(parm.getValue("STOOLW")))) {
				Stool.append("+");
			}
		} else {
			if (Stool.toString().length() > 0) {
				Stool.append("+");
			}
		}
		
		controlPage.addData("L10" + num, Stool.toString());
		controlPage.addData("L12" + num, EStool.toString());
		controlPage.addData("L14" + num, Enema.toString());
	}

	public Object clearZero(Object obj) {
		try {
			if (obj == null)
				return "";
			double mun = Double.parseDouble("" + obj);
			if (mun == 0)
				return "";
			else
				return obj;
		} catch (NumberFormatException e) {
			return obj;
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	private String getWeightReason(String id) {
		String sql = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_WEIGHT_REASON' AND ID='"
				+ id + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CHN_DESC", 0);
	}

	/**
	 * 得到与出生天数的差
	 * 
	 * @param nowDate
	 *            String
	 * @return int
	 */
	public int getBornDateDiffer(String date, Timestamp bornDate) {
		Timestamp nowDate = StringTool.getTimestamp(date, "yyyyMMdd");
		return StringTool.getDateDiffer(nowDate, bornDate);
	}

	/**
	 * 计算横向坐标位置
	 * 
	 * @param date
	 *            int
	 * @param examineSession
	 *            int
	 * @return int
	 */
	private int countHorizontal(int date, int examineSession) {
		// int adaptX = 7;
		// return 10 * ( (date - 1) * 6 + examineSession) - adaptX;
		int adaptX = 7;
		return 8 * ((date - 1) * 6 + examineSession) - adaptX;
	}

	/**
	 * 结算体温单的纵向坐标
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	private double getVertical(double value, String flag) {
		int adaptY = 8;
		// 体温或者物理降温
		if ("T".equals(flag) || "P".equals(flag))
			return (445 - countVertical(value, 42, 34, 40) * 10) - adaptY;
		// 脉搏
		if ("PL".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		// 心率
		if ("H".equals(flag))
			return (445 - countVertical(value, 180, 20, 40) * 10) - adaptY;
		return -1;
	}

	/**
	 * 计算纵坐标的位置--体温
	 * 
	 * @param value
	 *            int 数据库中记录的数据
	 * @param topValue
	 *            int 表格中最大的值--顶
	 * @param butValue
	 *            int 表格中最小的值--底
	 * @param level
	 *            int 最大与最小之间的有多少等级-行数
	 * @return int
	 */
	private double countVertical(double value, double topValue,
			double butValue, int level) {
		return (value - butValue) / ((topValue - butValue) / level) - 1;
	}

	/**
	 * 得到需要打印的主数据
	 * 
	 * @param date
	 *            TParm
	 */
	public Vector getVitalSignDate(TParm date) {
		Vector tprSign = new Vector();
		date.setData("ADM_TYPE", admType);
		date.setData("CASE_NO", caseNo);
		// 体温，脉搏，呼吸数据
		TParm vitalSignMstParm = SUMVitalSignTool.getInstance().selectdataMst(
				date);
		TParm vitalSignDtlParm = SUMVitalSignTool.getInstance().selectdataDtl(
				date);
		// 生命标记结果：0-主表信息 1-细表信息
		tprSign.add(vitalSignMstParm);
		tprSign.add(vitalSignDtlParm);
		return tprSign;
	}

	/**
	 * 得到年龄编码
	 * 
	 * @return
	 */
	public String onAgeCode(String CaseNo, String AdmType) {
		String sumKind = "";
		String age = "";
		if (AdmType.equals("I")) {
			Timestamp indate = patInfo.getTimestamp("IN_DATE", 0);
			Pat pat = Pat.onQueryByMrNo(patInfo.getValue("MR_NO", 0));
			Timestamp birth = pat.getBirthday();
			age = DurationFormatUtils.formatPeriod(birth.getTime(),
					indate.getTime(), "y-M-d-H-m-s");
		}
		if (age.equals(""))
			return sumKind;
		String sql = "SELECT  AGE_CODE,START_AGE,START_MONTH,START_DAY,START_HOUR,"
				+ " END_AGE,END_MONTH,END_DAY,END_HOUR,SHOW_TYPE,SUM_JHW "
				+ " FROM SYS_AGE";
		// System.out.println("=====age===="+age);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0)
			return sumKind;
		for (int i = 0; i < parm.getCount(); i++) {
			String SUM_JHW = parm.getValue("SUM_JHW", i);
			if (checkAge(parm.getRow(i), age)) {
				sumKind = SUM_JHW;
				break;
			}
		}
		return sumKind;
	}

	/**
	 * 核验数据行是否符合
	 * 
	 * @param parm
	 * @return
	 */
	public boolean checkAge(TParm parm, String age) {
		String[] t = age.split("-");
		int nYear = Integer.valueOf(t[0]);
		int nMonth = Integer.valueOf(t[1]);
		int nDay = Integer.valueOf(t[2]);
		int nHour = Integer.valueOf(t[3]);
		int count = 0;
		String startAge = parm.getValue("START_AGE");
		String startMonth = parm.getValue("START_MONTH");
		String startDay = parm.getValue("START_DAY");
		String startHour = parm.getValue("START_HOUR");
		String endAge = parm.getValue("END_AGE");
		String endMonth = parm.getValue("END_MONTH");
		String endDay = parm.getValue("END_DAY");
		String endHour = parm.getValue("END_HOUR");
		String showType = parm.getValue("SHOW_TYPE");
		String startNum = "";
		String endNum = "";
		if (showType.contains("岁")) {
			if (!startAge.equals(""))
				startNum = startAge;
			if (!endAge.equals(""))
				endNum = endAge;

			count = nYear;
		}
		if (showType.contains("月")) {
			if (!startMonth.equals(""))
				startNum = (TypeTool.getInt(startNum) * 12 + TypeTool
						.getInt(startMonth)) + "";
			if (!endMonth.equals(""))
				endNum = (TypeTool.getInt(endNum) * 12 + TypeTool
						.getInt(endMonth)) + "";

			count = (nYear * 12 + nMonth);
		}
		if (showType.contains("天")) {
			if (!startDay.equals(""))
				startNum = (TypeTool.getInt(startNum) * 30 + TypeTool
						.getInt(startDay)) + "";
			if (!endDay.equals(""))
				endNum = (TypeTool.getInt(endNum) * 30 + TypeTool
						.getInt(endDay)) + "";

			count = (nYear * 12 + nMonth) * 30 + nDay;
		}
		if (showType.contains("小时")) {
			if (!startHour.equals(""))
				startNum = (TypeTool.getInt(startNum) * 24 + TypeTool
						.getInt(startHour)) + "";
			if (!endHour.equals(""))
				endNum = (TypeTool.getInt(endNum) * 24 + TypeTool
						.getInt(endHour)) + "";

			count = ((nYear * 12 + nMonth) * 30 + nDay) * 24 + nHour;
		}
		if (count >= TypeTool.getInt(startNum) && endNum.equals("")) {
			return true;
		}
		if (count >= TypeTool.getInt(startNum)
				&& count <= TypeTool.getInt(endNum)) {
			return true;
		}
		return false;
	}

	/**
	 * 重量单位选择事件、
	 */
	public void onWeightSel() {
		if (this.getTRadioButton("W_KG").isSelected()) {
			double wg = TypeTool.getDouble(getValueString("WEIGHT_G")) / 1000;
			this.getTextField("WEIGHT_G").setEnabled(false);
			this.getTextField("WEIGHT").setEnabled(true);
			this.setValue("WEIGHT", wg + "");
			this.setValue("WEIGHT_G", "");
		}
		if (this.getTRadioButton("W_G").isSelected()) {
			double wg = TypeTool.getDouble(getValueString("WEIGHT")) * 1000;
			this.getTextField("WEIGHT").setEnabled(false);
			this.getTextField("WEIGHT_G").setEnabled(true);
			this.setValue("WEIGHT_G", wg + "");
			this.setValue("WEIGHT", "");
		}
	}

	/**
	 * 得到TRadioButton对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getTRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 *
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * TParm中“null”串转为空字符串
	 * 
	 * @param str
	 * @return
	 */
	public String nullToEmptyStr(String str) {
		if (str == null || str.equalsIgnoreCase("null")) {
			return "";
		}
		return str;
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (isMroFlg)
			return true;
		return true;
	}

	public static void main(String[] args) {
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sum\\SUMVitalSign.x");
	}
}
