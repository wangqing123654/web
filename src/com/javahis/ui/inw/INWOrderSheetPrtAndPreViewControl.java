package com.javahis.ui.inw;

import jdo.sys.Pat;
import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TWord;
import com.dongyang.data.TParm;
import jdo.adm.ADMTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.JavaHisDebug;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.dongyang.tui.text.MStyle;
import com.javahis.ui.emr.EMRTool;

/**
 * <p>
 * Title: 医嘱单打印预览主窗口
 * </p>
 * 
 * <p>
 * Description: 医嘱单处理特效： 1,order_cat1_type:PHA 数量+单位+频次+用法 非PHA （AW1,.,STAT
 * 不显示凭此频次）其余显示 频次 2,连接医嘱 3,ZZZZ医嘱备注 4,转科换页
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>      
 * Company:
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class INWOrderSheetPrtAndPreViewControl extends TControl {

	// 医嘱嘱种类
	TRadioButton ST;
	TRadioButton UD;
	TRadioButton DS;

	TWord word;

	String caseNo = "";
	String name = "";
	String ipdNo = "";
	String mrN0 = "";
	String dept = "";
	String station = "";
	String bed = "";
	Timestamp dsDate = null;
	Timestamp birthday = null;
	String sex = "";
	// 保存来自住院管理界面的参数，传给补充计价
	TParm outsideParm = new TParm();

	public INWOrderSheetPrtAndPreViewControl() {
	}

	public void onInit() {
		super.onInit();
		myInitControler();
		outsideParm = (TParm) this.getParameter();
		// this.messageBox("===外部参数===="+outsideParm);
		if (outsideParm != null)
			initParmFromOutside();
		setDeptList();

	}

	/**
	 * 初始化时得到所有控件对象
	 */
	public void myInitControler() {

		ST = (TRadioButton) this.getComponent("ST");
		UD = (TRadioButton) this.getComponent("UD");
		DS = (TRadioButton) this.getComponent("DS");
		word = (TWord) this.getComponent("WORD");

		//add by yangjj 20150327
		TCheckBox ANDDS = (TCheckBox) this.getComponent("ANDDS");
		ANDDS.setEnabled(false);
	}

	/**
	 * 初始化界面参数caseNo/stationCode
	 */
	public void initParmFromOutside() {

		// 按就诊号查询的caseNo
		this.setCaseNo(outsideParm.getValue("INW", "CASE_NO"));
		TParm parm1 = new TParm();
		parm1.setData("CASE_NO", getCaseNo());
		TParm admInfo = ADMTool.getInstance().getADM_INFO(parm1);
		this.setIpdNo((String) admInfo.getData("IPD_NO", 0));
		this.setMrN0((String) admInfo.getValue("MR_NO", 0));
		String inDeptCode = (String) admInfo.getValue("IN_DEPT_CODE", 0);
		TParm firstData = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC from sys_dept where DEPT_CODE='"
						+ inDeptCode + "'"));
		this.setDept((String) firstData.getData("DEPT_CHN_DESC", 0));

		firstData = new TParm(TJDODBTool.getInstance().select(
				"SELECT STATION_DESC from SYS_STATION where STATION_CODE='"
						+ admInfo.getValue("STATION_CODE", 0) + "'"));
		station = "" + firstData.getData("STATION_DESC", 0);

		firstData = new TParm(TJDODBTool.getInstance().select(
				"SELECT BED_NO_DESC from SYS_BED where BED_NO='"
						+ admInfo.getValue("BED_NO", 0) + "'"));
		bed = "" + firstData.getData("BED_NO_DESC", 0);

		Pat pat = Pat.onQueryByMrNo((String) admInfo.getValue("MR_NO", 0));
		this.setName(pat.getName());
		dsDate = (Timestamp) admInfo.getData("DS_DATE", 0);
		birthday = pat.getBirthday();
		sex = pat.getSexString();
	}

	/**
	 * 打印程序
	 */
	public void onPrint() {
		EMRTool emrTool = new EMRTool(this.caseNo, this.mrN0, this);
		if (ST.isSelected())
			//============   modify   by  chenxi   20120702  true 为医嘱单打印时保存一笔数据
			emrTool.saveEMR(word, getSaveFileName(), "EMR110002", "EMR11000201",true);
		if (UD.isSelected())
			emrTool.saveEMR(word, getSaveFileName(), "EMR110001", "EMR11000101",true);
		if (DS.isSelected())
			emrTool.saveEMR(word, getSaveFileName(), "EMR120001", "EMR12000106",true);
		//===========   modify  by  chenxi     20120702
		word.print();
	}

	/**
	 * 得到保存医嘱单文件的名称
	 * 
	 * @return String
	 */
	private String getSaveFileName() {
		String fileName = "";
		if (this.ST.isSelected()) {
			fileName = "临时医嘱单";
		}
		if (this.UD.isSelected()) {
			fileName = "长期医嘱单";
		}
		if (this.DS.isSelected()) {
			fileName = "出院带药医嘱单";
		}
		return fileName;
	}

	public void onDeptList() {
		if (ST.isSelected())
			onCheck("ST");
		if (UD.isSelected())
			onCheck("UD");
		if (DS.isSelected())
			onCheck("DS");
	}

	/**
	 * 挑勾动作
	 * 
	 * @param flg
	 *            Object
	 */
	public void onCheck(Object flg) {
		//add by yangjj 20150327
		TCheckBox ANDDS = (TCheckBox) this.getComponent("ANDDS");


		word.setWordParameter(null);
		// $$====modified by lx 2012/02/20 报表样式重载 ===========//
		word.getWordText().getPM().setStyleManager(new MStyle(true));
		// $$====modified by lx 2012/02/20 报表样式重载end ===========//
		word.getWordText().getPM().getFileManager().onNewFile();
		TParm prtParm = new TParm();
		// 调用TWord
		word.setWordParameter(prtParm);
		word.setPreview(true);
		TParm orderParm = new TParm();
		Timestamp endDate = (dsDate == null ? SystemTool.getInstance()
				.getDate() : dsDate);
		String age = StringUtil.getInstance().showAge(birthday, endDate);
		if ("ST".equals(flg + "") && ST.isSelected()) {
			//add by yangjj 20150327
			ANDDS.setEnabled(true);

			//System.out.println("========ST1=========="+new Date());
			// 得到打印参数
			orderParm = getSTOrderParm();
			//System.out.println("========ST2=========="+new Date());
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
			orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
			orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
			//System.out.println("========ST3=========="+new Date());
			TParm data = new TParm();		
			data.setData("S.12.002", orderParm.getData());
			data.setData("HR02.01.001.01", "TEXT", this.getName());
			//add by sunqy 20140617 ----start----
			data.setData("filePatName", "TEXT", this.getName());//病案首页姓名
			data.setData("fileSex", "TEXT", sex);//病案首页性别
			data.setData("fileBirthday", "TEXT", birthday.toString().replaceAll("-", "/").substring(0, 10));//病案首页出生日期
			data.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrN0());//病案页眉病案号
			data.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());//病案页眉住院号
			//add by sunqy 20140617 ----end----
			data.setData("MR_NO", "TEXT", this.getMrN0());
			data.setData("IPD_NO", "TEXT", this.getIpdNo());
			data.setData("HR21.01.100.05", "TEXT", getValueString("DEPT_LIST")
					.length() == 0 ? getDept()
							: ((TComboBox) getComponent("DEPT_LIST")).getSelectedName());
			data.setData("STATION", "TEXT", station);
			data.setData("HR01.01.002.02", "TEXT", bed);
			data.setData("HR02.02.001", "TEXT", sex);
			data.setData("HR02.03.001", "TEXT", age);
			//System.out.println("========ST4=========="+new Date());
			word.setWordParameter(data);
			//System.out.println("========ST5=========="+new Date());
			word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_ST.jhw");
			//System.out.println("========ST6=========="+new Date());
			return;
		}
		if ("UD".equals(flg + "") && UD.isSelected()) {
			//add by yangjj 20150327
			ANDDS.setEnabled(false);

			// 得到打印参数

			orderParm = getUDOrderParm();
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
			orderParm.addData("SYSTEM", "COLUMNS", "NS_CHECK_CODE");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_DAY");
			orderParm.addData("SYSTEM", "COLUMNS", "DC_DATE_TIME");
			orderParm.addData("SYSTEM", "COLUMNS", "DC_DR_CODE");
			orderParm.addData("SYSTEM", "COLUMNS", "DC_NS_CHECK_CODE");
			TParm data = new TParm();
			data.setData("TABLE", orderParm.getData());
			data.setData("HR02.01.001.01", "TEXT", this.getName());// name
			//add by sunqy 20140617 ----start----
			data.setData("filePatName", "TEXT", this.getName());//病案首页姓名
			data.setData("fileSex", "TEXT", sex);//病案首页性别
			data.setData("fileBirthday", "TEXT", birthday.toString().replaceAll("-", "/").substring(0, 10));//病案首页出生日期
			data.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrN0());//病案页眉病案号
			data.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());//病案页眉住院号
			//add by sunqy 20140617 ----end----
			data.setData("MR_NO", "TEXT", this.getMrN0());
			data.setData("IPD_NO", "TEXT", this.getIpdNo());
			data.setData("HR21.01.100.05", "TEXT", getValueString("DEPT_LIST")
					.length() == 0 ? getDept()
							: ((TComboBox) getComponent("DEPT_LIST")).getSelectedName());// dept
			data.setData("STATION", "TEXT", station);
			data.setData("HR01.01.002.02", "TEXT", bed);// bed
			data.setData("HR02.02.001", "TEXT", sex);// sex
			data.setData("HR02.03.001", "TEXT", age);// age
			word.setWordParameter(data);
			word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_UD.jhw");
			return;
		}
		if ("DS".equals(flg + "") && DS.isSelected()) {
			//add by yangjj 20150327
			ANDDS.setEnabled(false);

			// 得到打印参数
			orderParm = getDSOrderParm();// 目前临时和出院带药一样
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_DAY");
			orderParm.addData("SYSTEM", "COLUMNS", "EFF_DATE_TIME");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			orderParm.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
			orderParm.addData("SYSTEM", "COLUMNS", "ORDER_DR_CODE");
			orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
			orderParm.addData("SYSTEM", "COLUMNS", "NS_EXEC_CODE");
			TParm data = new TParm();
			data.setData("TABLE", orderParm.getData());
			data.setData("HR02.01.001.01", "TEXT", this.getName());// name
			//add by sunqy 20140617 ----start----
			data.setData("filePatName", "TEXT", this.getName());//病案首页姓名
			data.setData("fileSex", "TEXT", sex);//病案首页性别
			data.setData("fileBirthday", "TEXT", birthday.toString().replaceAll("-", "/").substring(0, 10));//病案首页出生日期
			data.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrN0());//病案页眉病案号
			data.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());//病案页眉住院号
			//add by sunqy 20140617 ----end----
			data.setData("MR_NO", "TEXT", this.getMrN0()); 
			data.setData("IPD_NO", "TEXT", this.getIpdNo());
			data.setData("HR21.01.100.05", "TEXT", getValueString("DEPT_LIST")
					.length() == 0 ? getDept()
							: ((TComboBox) getComponent("DEPT_LIST")).getSelectedName());// dept
			data.setData("STATION", "TEXT", station);
			data.setData("HR01.01.002.02", "TEXT", bed);// bed
			data.setData("HR02.02.001", "TEXT", sex);// sex
			data.setData("HR02.03.001", "TEXT", age);// age
			word.setWordParameter(data);
			word.setFileName("%ROOT%\\config\\prt\\inw\\OrderSheet_DS.jhw");
			return;
		}

	}

	/**
	 * 获得该病人长期医嘱
	 * 
	 * @return TParm
	 */
	private TParm getUDOrderParm() {
		TParm UDparm = new TParm(TJDODBTool.getInstance().select(
				this.getSelectSQL("UD")));
		TParm printData = arrangeData(UDparm, "UD");
		return printData;
	}

	/**
	 * 获得该病人临时医嘱
	 * 
	 * @return TParm
	 */
	private TParm getSTOrderParm() {
		//System.out.println("===========getSTOrderParm 1==========="+new Date());


		//add by yangjj 20150326
		TCheckBox ANDDS = (TCheckBox) this.getComponent("ANDDS");
		TParm STparm = new TParm();
		if(ANDDS.isSelected()){
			STparm = new TParm(TJDODBTool.getInstance().select(
					this.getSelectSQL("ST_AND_DS")));
		}else{
			STparm = new TParm(TJDODBTool.getInstance().select(
					this.getSelectSQL("ST")));
		}

		//System.out.println("===========getSTOrderParm 2==========="+new Date());
		TParm printData = arrangeData(STparm, "ST");
		//System.out.println("===========getSTOrderParm 3==========="+new Date());
		return printData;
	}

	/**
	 * 获得该病人出院带药医嘱
	 * 
	 * @return TParm
	 */
	private TParm getDSOrderParm() {
		TParm STparm = new TParm(TJDODBTool.getInstance().select(
				this.getSelectSQL("DS")));
		TParm printData = arrangeData(STparm, "DS");
		return printData;
	}

	/**
	 * 取得服法中文描述
	 * 
	 * @param dose
	 *            String
	 * @return String
	 */
	private String getRouteDesc(String dose) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT ROUTE_CHN_DESC FROM SYS_PHAROUTE WHERE ROUTE_CODE = '"
						+ dose + "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("ROUTE_CHN_DESC", 0);
	}

	/**
	 * 取得服法中文描述
	 * 
	 * @param dose
	 *            String
	 * @return String
	 */
	private String getRoutePSFlg(String dose) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT PS_FLG FROM SYS_PHAROUTE WHERE ROUTE_CODE = '" + dose
				+ "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("PS_FLG", 0);
	}

	/**
	 * 取得频次中文描述
	 * 
	 * @param dose
	 *            String
	 * @return String
	 */
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
//	public String getFreqDesc(Object o){
//		String sql = "SELECT FREQ_CHN_DESC "+
//					 "FROM SYS_PHAFREQ "+
//					 "WHERE FREQ_CODE = '"+o+"'";
//		String desc = new TParm(TJDODBTool.getInstance().select(sql)).getValue("FREQ_CHN_DESC",0);
//		return desc;
//	}
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
		//System.out.println("=======111count========"+count);
		//System.out.println("=======arrangeData1========"+new Date());
		TParm odiParm = new TParm(TJDODBTool.getInstance().select(
				" SELECT DELAY_TIME,DELAY_SUFFIX "
						+ " FROM ODI_SYSPARM "));
		Long timePeriod = odiParm.getLong("DELAY_TIME", 0);
		String suffix = odiParm.getValue("DELAY_SUFFIX", 0);
		for (int i = 0; i < count; i++) {
			TParm order = parm.getRow(i);
			//System.out.println("=======arrangeData2========"+new Date());
			String orderdep = "";
			// 判断连接医嘱
			if (ifLinkOrder(order)) {
				// 如果为连接医嘱细项则不予处理      (不是子项则)
				if (ifLinkOrderSubItem(order))
					continue;
				// String finalOrder = getLinkOrder(order, parm);
				orderdep = getLinkOrder(order, parm);
			} else { // 普通医嘱
				String desc = (String) order.getData("ORDER_DESC");// 医嘱描述
				String qty = order.getData("MEDI_QTY") + "";// 用量
				String unit = (String) order.getData("UNIT_CHN_DESC");// 单位
				String freq = getFreDesc((String) order.getData("FREQ_CODE"));// 频次
				//String freq = (String) order.getData("FREQ_CODE");// 频次
				String routeChnDesc = (String) order.getData("ROUTE_CHN_DESC");// 用法
				String influtionRate = "";//滴速  machao  20171128
				if("IVD".equals((String) order.getData("ROUTE_CODE"))){
					if(!StringUtil.isNullString(order.getData("INFLUTION_RATE")+"") 
							//&& !(order.getData("INFLUTION_RATE")+"").equals("0.0")
							){
						influtionRate = "滴速:"+(String) order.getValue("INFLUTION_RATE")+"ml/h";
					}
				}
				
				String drNote = (String) order.getData("DR_NOTE");// 医嘱备注
				String nsNote = (String) order.getData("NS_NOTE");// 护士备注
				
				String dose = (String) order.getData("ROUTE_CODE");
				String cat1 = (String) order.getData("CAT1_TYPE");
				String isRemark = order.getValue("IS_REMARK");//wanglong add 20150204
				// 判断是否是医嘱备注
				if (isRemark.equals("Y")) {
					desc = drNote;
					drNote = "";
					qty = "";
					unit = "";
					freq = "";
					dose = "";
				}
				// 如果该医嘱是非PHA
				if ((!checkOrderCat1(cat1)) && chackFreq(freq)) {
					qty = "";
					unit = "";
					freq = "";
					dose = "";
				}
	
				// modiFied by WangQing at 20170222 -start
//				boolean psFlg = order.getValue("PS_FLG").equals("N");
				String secondRow = "";
				secondRow = qty + " " + unit + " " + freq + " " + routeChnDesc +" "+influtionRate;//滴速  machao  20171128
				orderdep = desc
						+ ((secondRow.trim().length() == 0) ? "" : "\r"
								+ secondRow)
						+ ((drNote != null && drNote.length() != 0) ? "\r"
								+ "(" + drNote + ")" : "") 
						+ ((nsNote != null && nsNote.length() != 0) ? "\r"
								+ "(" + nsNote + ")" : "");
				// modiFied by WangQing at 20170222 -end
				
			}
			//System.out.println("=======arrangeData3========"+new Date());
			// 根据医嘱类型设置不同的数据列
			if ("UD".equals(flg)) {
				// order_date 与 EFF_DATE时间差 > 30分钟，医嘱后加上(补）的标示
				// 医嘱启用时间
				long effDateFull = StringTool.getTimestamp(
						(String) order.getData("EFF_DATE_FULL"),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// 开立日期
				long orderDateFull = StringTool.getTimestamp(
						(String) order.getData("ORDER_DATE_FULL"),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// 相差分钟
				long interval = (orderDateFull - effDateFull) / (1000 * 60);
				// order_date 与 ？？？时间差 > 30分钟，医嘱后加上(补）的标示
				/*TParm odiParm = new TParm(TJDODBTool.getInstance().select(
						" SELECT DELAY_TIME,DELAY_SUFFIX "
								+ " FROM ODI_SYSPARM "));
				Long timePeriod = odiParm.getLong("DELAY_TIME", 0);
				String suffix = odiParm.getValue("DELAY_SUFFIX", 0);*/
				// this.messageBox("++interval++"+interval);
				// this.messageBox("++timePeriod++"+timePeriod);
				// this.messageBox("++interval++"+interval);
				if (interval > timePeriod&&timePeriod!=0) {
					// System.out.println("----------------------------"+suffix);
					result.addData("ORDER_DESC", orderdep + suffix);

				} else {
					result.addData("ORDER_DESC", orderdep);
				}
				result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
				result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
				result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
				result.addData("NS_CHECK_CODE", order.getData("NS_CHECK_CODE"));
				result.addData("DC_DATE_DAY", order.getData("DC_DATE_DAY"));
				result.addData("DC_DATE_TIME", order.getData("DC_DATE_TIME"));
				result.addData("DC_DR_CODE", order.getData("DC_DR_CODE"));
				result.addData("DC_NS_CHECK_CODE",
						order.getData("DC_NS_CHECK_CODE"));

				//add by yangjj 20151120
				result.addData("DISPENSE_FLG",order.getData("DISPENSE_FLG"));
			} else if ("ST".equals(flg)) { // 临时
				// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
				// =============$$//
				// order_date 与 EFF_DATE时间差 > 30分钟，医嘱后加上(补）的标示
				// 医嘱启用时间
				long effDateFull = StringTool.getTimestamp(
						(String) order.getData("EFF_DATE_FULL"),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// 开立日期
				long orderDateFull = StringTool.getTimestamp(
						(String) order.getData("ORDER_DATE_FULL"),
						"yyyy/MM/dd HH:mm:ss").getTime();
				// 相差分钟
				long interval = (orderDateFull - effDateFull) / (1000 * 60);
				// order_date 与 ？？？时间差 > 30分钟，医嘱后加上(补）的标示
				//Modified by lx 性能问题移到循环外面
				/*TParm odiParm = new TParm(TJDODBTool.getInstance().select(
						" SELECT DELAY_TIME,DELAY_SUFFIX "
								+ " FROM ODI_SYSPARM "));
				Long timePeriod = odiParm.getLong("DELAY_TIME", 0);
				String suffix = odiParm.getValue("DELAY_SUFFIX", 0);*/

				if (interval > timePeriod&&timePeriod!=0){
					result.addData("ORDER_DESC", orderdep + suffix);
				} else {
					result.addData("ORDER_DESC", orderdep);
				}

				result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
				result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
				result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
				result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
				result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));

				//add by yangjj 20151120
				result.addData("DISPENSE_FLG",order.getData("DISPENSE_FLG"));
			} else if ("DS".equals(flg)) { // 出院带药
				result.addData("EFF_DATE_DAY", order.getData("EFF_DATE_DAY"));
				result.addData("TAKE_DAYS", order.getData("TAKE_DAYS"));
				result.addData("EFF_DATE_TIME", order.getData("EFF_DATE_TIME"));
				result.addData("ORDER_DR_CODE", order.getData("ORDER_DR_CODE"));
				result.addData("NS_EXEC_DATE", order.getData("NS_EXEC_DATE"));
				result.addData("NS_EXEC_CODE", order.getData("NS_EXEC_CODE"));
				result.addData("ORDER_DESC", orderdep);

				//add by yangjj 20151120
				result.addData("DISPENSE_FLG",order.getData("DISPENSE_FLG"));
			}
		}
		result.setCount(result.getCount("EFF_DATE_DAY"));


		//add by yangjj 20151120 增加自备药信息
		for(int i = 0 ; i < result.getCount(); i++){
			TParm order = result.getRow(i);
			String dispenseFlg = order.getValue("DISPENSE_FLG");
			if("Y".equals(dispenseFlg)){
				result.setData("ORDER_DESC", i, order.getValue("ORDER_DESC")+" （备）");
			}
		}

		return result;
	}

	/**
	 * 判断该医嘱是否是PHA类型
	 * 
	 * @param code
	 *            String
	 * @return boolean
	 */
	private boolean checkOrderCat1(String code) {
		return "PHA".equals(code);
	}

	/**
	 * 非PHA医嘱 （AW1,.,STAT 不显示凭此频次）
	 * 
	 * @param freq
	 *            String
	 * @return boolean
	 */
	private boolean chackFreq(String freq) {
		return "AW1".equals(freq) || ".".equals(freq) || "STAT".equals(freq);
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
		// modified by WangQing at 20170222 -start
		String mainOrder = (String) order.getData("ORDER_DESC");// 医嘱描述	
		String mainmediQty = order.getData("MEDI_QTY") + "";// 用量
		String mainUnit = (String) order.getData("UNIT_CHN_DESC");// 单位
		String mainFreq = getFreDesc((String) order.getData("FREQ_CODE"));// 频次
		//String mainFreq = (String) order.getData("FREQ_CODE");// 频次
		String mainRouteChnDesc = (String) order.getData("ROUTE_CHN_DESC");// 用法
		
		String mainInflutionRate = "";//滴速  machao  20171128
		if("IVD".equals((String) order.getData("ROUTE_CODE"))){
			if(!StringUtil.isNullString(order.getData("INFLUTION_RATE")+"") 
					//&& !(order.getData("INFLUTION_RATE")+"").equals("0.0")
					){
				mainInflutionRate = "滴速:"+(String) order.getValue("INFLUTION_RATE")+"ml/h";
			}
		}
		String mainNote = (String) order.getData("DR_NOTE");// 医嘱备注
		String mainNsNote = (String) order.getData("NS_NOTE");// 护士备注
		
		String mainDose = (String) order.getData("ROUTE_CODE");
		String mainLinkNo = (String) order.getData("LINK_NO");
		String mainorderNo=(String) order.getData("ORDER_NO");  //shibl 20130121 add order_no
		String mainRxKind = (String) order.getData("RX_KIND");
		
//		boolean psFlg = order.getValue("PS_FLG").equals("N");	
		
		resultDesc = mainOrder
				+"\r"
				+ mainmediQty
				+ " "
				+ mainUnit
				+ " "
				+ mainFreq
				+ " "
				+ mainRouteChnDesc
				+ " "
				+ mainInflutionRate
				+ (mainNote != null && mainNote.length() != 0 ? "\r" + "("
						+ mainNote + ")" : "") 
				+ (mainNsNote != null && mainNsNote.length() != 0 ? "\r" + "("
						+ mainNsNote + ")" : "")
				+ "\r";	
		// modified by WangQing at 20170222 -end
		
		
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			String linkNo = (String) parm.getData("LINK_NO", i);
			String rxKind = (String) parm.getData("RX_KIND", i);
			String orderNo=(String) parm.getData("ORDER_NO", i);
			if (rxKind.equals(mainRxKind) && mainLinkNo.equals(linkNo)&&mainorderNo.equals(orderNo)//shibl 20130121 add order_no
					&& !TypeTool.getBoolean(parm.getData("LINKMAIN_FLG", i))) {
				
				// modified by WangQing at 20170222 -start
				String subOrder = (String) parm.getData("ORDER_DESC", i);// 医嘱描述
				String submediQty = parm.getData("MEDI_QTY", i) + "";// 用量
				String subUnit = (String) parm.getData("UNIT_CHN_DESC", i);// 单位
				String subFreq = getFreDesc((String) order.getData("FREQ_CODE"));// 频次
				//String subFreq = (String) parm.getData("FREQ_CODE", i);// 频次
				String subRouteChnDesc = (String) parm.getData("ROUTE_CHN_DESC", i);// 用法
				
				String influtionRate = "";//滴速  machao  20171128
				if("IVD".equals((String) order.getData("ROUTE_CODE"))){
					if(!StringUtil.isNullString(order.getData("INFLUTION_RATE")+"") 
							//&& !(order.getData("INFLUTION_RATE")+"").equals("0.0")
							){
						influtionRate = "滴速:"+(String) order.getValue("INFLUTION_RATE")+"ml/h";
					}
				}
				
				
				String subNote = (String) parm.getData("DR_NOTE", i);// 医嘱备注
				String nsNote = (String) parm.getData("NS_NOTE", i);// 护士备注
				
				resultDesc += subOrder
						+"\r"
						+ submediQty
						+ " "
						+ subUnit
						+ " "
						+ subFreq
						+ " "
						+ subRouteChnDesc
						+ " "
						+ influtionRate
						+ (subNote != null && subNote.length() != 0 ? "\r"
								+ "(" + subNote + ")" : "") 
						+ (nsNote != null && nsNote.length() != 0 ? "\r"
								+ "(" + nsNote + ")" : "")
						+ "\r";	
				// modified by WangQing at 20170222 -end
			} else
				continue;
		}
		// modified by WangQing at 20170222
		resultDesc += "第 " + mainLinkNo + " 组";
		return resultDesc;
	}

	private TParm setDeptList() {
		String SQL = " SELECT DISTINCT A.DEPT_CODE,B.DEPT_CHN_DESC "
				+ " FROM ODI_ORDER A , SYS_DEPT B" + " WHERE A.CASE_NO = '"
				+ getCaseNo() + "' " + " AND   A.DEPT_CODE = B.DEPT_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		((TComboBox) getComponent("DEPT_LIST"))
		.setParmMap("id:DEPT_CODE;name:DEPT_CHN_DESC");
		((TComboBox) getComponent("DEPT_LIST")).setParmValue(parm);
		setValue("DEPT_LIST", getValue("DEPT_CODE"));
		return parm;
	}

	private String getSelectSQL(String orderFlg) {
		String sql = "";
		if ("ST".equals(orderFlg) || "DS".equals(orderFlg)) {// 临时医嘱/出院带药
			String unionStr0 = "";
			String unionStr1 = "";

			unionStr0 = " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
					+ " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,A.ORDER_SEQ,"//shibl 20130121 add order_no
					+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
					+
					// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
					// =============$$//
					" TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "

					//add by yangjj 20151120 增加自备药信息
					+ " A.DISPENSE_FLG, "


					// modified by WangQing at 20170215 -start
					+ " D.PS_FLG, "
					+ " D.ROUTE_CHN_DESC, "
					+ " A.INFLUTION_RATE "//增加速率  machao  20171128
					// modified by WangQing at 20170215 -end


					// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
					// =============$$//
					+ " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B ,SYS_PHAROUTE D "
					+ " WHERE  A.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A.CASE_NO=B.CASE_NO (+)"
					+ " AND A.ORDER_NO=B.ORDER_NO (+)"
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
					+ " AND A.RX_KIND='"
					+ orderFlg
					+ "' "
					+ " AND A.HIDE_FLG = 'N' "


					// modified by WangQing at 20170215 -start
					+ " AND A.ROUTE_CODE=D.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end



					+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
					+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'")
					// 过滤术中医嘱wanglong add 20140707
					+ " AND A.OPBOOK_SEQ IS NULL "; 

			unionStr1 = " UNION ALL"
					+ " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
					+ " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
					+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,A1.ORDER_SEQ,"//shibl 20130121 add order_no
					+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
					+ " TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " A1.DISPENSE_FLG, "

						// modified by WangQing at 20170215 -start
						+ " D1.PS_FLG, "
						+ " D1.ROUTE_CHN_DESC, "
						+ " B1.INFLUTION_RATE "//machao 增加速率  20171128
						// modified by WangQing at 20170215 -end
						+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 ,SYS_PHAROUTE D1 "
						+ " WHERE  A1.CASE_NO='"
						+ this.getCaseNo()
						+ "'"
						+ " AND A1.CASE_NO=B1.CASE_NO (+)"
						+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
						+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
						+ " AND A1.RX_KIND='"
						+ orderFlg
						+ "' "

						// modified by WangQing at 20170215 -start
						+ " AND A1.ROUTE_CODE=D1.ROUTE_CODE (+) "
						// modified by WangQing at 20170215 -end

						//add by yangjj 20161214 
						+ " AND A1.TEMPORARY_FLG='Y' "

						+ " AND A1.HIDE_FLG = 'N' "
						+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
						+ (getValueString("DEPT_LIST").length() == 0 ? ""
								: " AND A1.DEPT_CODE = '"
								+ getValueString("DEPT_LIST") + "'") ;
			sql = "SELECT * FROM ("
					+ unionStr0
					+ unionStr1 
					+ ") T "
					+ " ORDER BY T.RX_KIND DESC, T.EFF_DATE ASC ,ORDER_NO,ORDER_SEQ" ;


			//			//modify by wukai on 20160824
			//			String unionStr = "";
			//			
			//			//modify by yangjj 20161214
			//			//if("ST".equals(orderFlg)) {
			//				//unionStr = "";
			//			//} else {
			//				unionStr = " UNION ALL"
			//	                    + " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//						+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
			//	                    + " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//	                    + " A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
			//						+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,A1.ORDER_SEQ,"//shibl 20130121 add order_no
			//						+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
			//						+
			//						// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//						// =============$$//
			//						" TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//						+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//						
			//						//add by yangjj 20151120 增加自备药信息
			//						+ " A1.DISPENSE_FLG "
			//						
			//						// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//						// =============$$//
			//						+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 "
			//						+ " WHERE  A1.CASE_NO='"
			//						+ this.getCaseNo()
			//						+ "'"
			//						+ " AND A1.CASE_NO=B1.CASE_NO (+)"
			//						+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
			//						+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
			//						+ " AND A1.RX_KIND='"
			//						+ orderFlg
			//						+ "' "
			//						
			//						//add by yangjj 20161214 
			//						+ " AND A1.TEMPORARY_FLG='Y' "
			//						
			//						+ " AND A1.HIDE_FLG = 'N' "
			//						+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
			//						+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//								: " AND A1.DEPT_CODE = '"
			//										+ getValueString("DEPT_LIST") + "'") ;
			//			//}
			//			sql = "SELECT * FROM ("
			//					+ " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
			//                    + " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                    + " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
			//					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,A.ORDER_SEQ,"//shibl 20130121 add order_no
			//					+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
			//					+
			//					// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//					// =============$$//
			//					" TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//					
			//					//add by yangjj 20151120 增加自备药信息
			//					+ " A.DISPENSE_FLG"
			//					
			//					// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//					// =============$$//
			//					+ " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B "
			//					+ " WHERE  A.CASE_NO='"
			//					+ this.getCaseNo()
			//					+ "'"
			//					+ " AND A.CASE_NO=B.CASE_NO (+)"
			//					+ " AND A.ORDER_NO=B.ORDER_NO (+)"
			//					+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
			//					+ " AND A.RX_KIND='"
			//					+ orderFlg
			//					+ "' "
			//					+ " AND A.HIDE_FLG = 'N' "
			//					+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
			//					+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
			//					+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//							: " AND A.DEPT_CODE = '"
			//									+ getValueString("DEPT_LIST") + "'")
			//                    // 过滤术中医嘱wanglong add 20140707
			//                    + " AND A.OPBOOK_SEQ IS NULL "
			//                    
			//                    // add by yangjj 20151127 
			//                    + unionStr 
			//                   /* + " UNION ALL"
			//                    + " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
			//                    + " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                    + " A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
			//					+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,"//shibl 20130121 add order_no
			//					+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
			//					+
			//					// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//					// =============$$//
			//					" TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//					
			//					//add by yangjj 20151120 增加自备药信息
			//					+ " A1.DISPENSE_FLG "
			//					
			//					// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//					// =============$$//
			//					+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 "
			//					+ " WHERE  A1.CASE_NO='"
			//					+ this.getCaseNo()
			//					+ "'"
			//					+ " AND A1.CASE_NO=B1.CASE_NO (+)"
			//					+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
			//					+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
			//					+ " AND A1.RX_KIND='"
			//					+ orderFlg
			//					+ "' "
			//					+ " AND A1.HIDE_FLG = 'N' "
			//					+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
			//					+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//							: " AND A1.DEPT_CODE = '"
			//									+ getValueString("DEPT_LIST") + "'")*/ + 
			//									") T "
			//					+ " ORDER BY T.RX_KIND DESC, T.EFF_DATE ASC ,ORDER_NO,ORDER_SEQ" ;
		}else if("ST_AND_DS".equals(orderFlg)){// 临时医嘱(含出院带药勾选)
			String unionStr0 = "";
			String unionStr1 = "";
			String unionStr2 = "";
			String unionStr3 = "";

			unionStr0 = " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
					+ " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,A.ORDER_SEQ,"//shibl 20130121 add order_no
					+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " A.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D.PS_FLG, "
					+ " D.ROUTE_CHN_DESC, "
					+ " A.INFLUTION_RATE "//增加速率  machao  20171128
					// modified by WangQing at 20170215 -end


					+ " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B ,SYS_PHAROUTE D "
					+ " WHERE  A.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A.CASE_NO=B.CASE_NO (+)"
					+ " AND A.ORDER_NO=B.ORDER_NO (+)"
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
					+ " AND A.RX_KIND='"
					+ "ST"
					+ "' "
					+ " AND A.HIDE_FLG = 'N' "

					// modified by WangQing at 20170215 -start
					+ " AND A.ROUTE_CODE=D.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end


					+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
					+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'")
					+ " AND A.OPBOOK_SEQ IS NULL ";

			unionStr1 = " UNION ALL"
					+ " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
					+ " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "
					+ " A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
					+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,A1.ORDER_SEQ,"
					+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
					+ " TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " A1.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D1.PS_FLG, "
					+ " D1.ROUTE_CHN_DESC, "
					+ " B1.INFLUTION_RATE "//增加速率  machao  20171128
					// modified by WangQing at 20170215 -end


					+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 ,SYS_PHAROUTE D1 "		
					+ " WHERE  A1.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A1.CASE_NO=B1.CASE_NO (+)"
					+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
					+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
					+ " AND A1.RX_KIND='"
					+ "ST"
					+ "' "
					+ " AND A1.TEMPORARY_FLG='Y' "

					// modified by WangQing at 20170215 -start
					+ " AND A1.ROUTE_CODE=D1.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end


					+ " AND A1.HIDE_FLG = 'N' "
					+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A1.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'") ;
			unionStr2 = " UNION ALL "
					+ " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
					+ " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "
					+ " '（出） '||A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
					+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,A.ORDER_SEQ, "
					+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " A.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D.PS_FLG, "
					+ " D.ROUTE_CHN_DESC, "
					+ " A.INFLUTION_RATE "//增加速率  machao  20171128
					// modified by WangQing at 20170215 -end

					+ " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B ,SYS_PHAROUTE D "
					+ " WHERE  A.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A.CASE_NO=B.CASE_NO (+)"
					+ " AND A.ORDER_NO=B.ORDER_NO (+)"
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
					+ " AND A.RX_KIND='"
					+ "DS"
					+ "' "
					+ " AND A.HIDE_FLG = 'N' "

					// modified by WangQing at 20170215 -start
					+ " AND A.ROUTE_CODE=D.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end


					+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
					+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'")
					+ " AND A.OPBOOK_SEQ IS NULL " ;

			unionStr3 = " UNION ALL"
					+ " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
					+ " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " '（出） '||A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
					+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,A1.ORDER_SEQ, "//shibl 20130121 add order_no
					+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
					+ " TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " A1.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D1.PS_FLG, "
					+ " D1.ROUTE_CHN_DESC, "
					+ " B1.INFLUTION_RATE "//增加速率  machao  20171128
					// modified by WangQing at 20170215 -end

					+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 ,SYS_PHAROUTE D1 "
					+ " WHERE  A1.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A1.CASE_NO=B1.CASE_NO (+)"
					+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
					+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
					+ " AND A1.RX_KIND='"
					+ "DS"
					+ "' " 
					+ " AND A1.TEMPORARY_FLG='Y' "		
					+ " AND A1.HIDE_FLG = 'N' "

					// modified by WangQing at 20170215 -start
					+ " AND A1.ROUTE_CODE=D1.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end

					+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A1.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'"); 

			sql = "SELECT * FROM ("
					+ unionStr0
					+ unionStr1 
					+ unionStr2 
					+ unionStr3 
					+ ") T "
					+ " ORDER BY T.RX_KIND DESC, T.EFF_DATE ASC ,ORDER_NO,ORDER_SEQ" ;




			//			sql = "SELECT * FROM ("
			//				+ " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//				+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
			//                + " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                + " A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
			//				+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,"//shibl 20130121 add order_no
			//				+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
			//				+
			//				// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				" TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//				+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//				
			//				//add by yangjj 20151120 增加自备药信息
			//				+ " A.DISPENSE_FLG ,A.ORDER_SEQ"
			//				
			//				// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				+" FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B "
			//				+ " WHERE  A.CASE_NO='"
			//				+ this.getCaseNo()
			//				+ "'"
			//				+ " AND A.CASE_NO=B.CASE_NO (+)"
			//				+ " AND A.ORDER_NO=B.ORDER_NO (+)"
			//				+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
			//				+ " AND A.RX_KIND='"
			//				+ "ST"
			//				+ "' "
			//				+ " AND A.HIDE_FLG = 'N' "
			//				+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
			//				+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
			//				+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//						: " AND A.DEPT_CODE = '"
			//								+ getValueString("DEPT_LIST") + "'")
			//                // 过滤术中医嘱wanglong add 20140707
			//                + " AND (A.ORDER_DEPT_CODE NOT IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE OP_FLG='Y') AND A.OPBOOK_SEQ IS NULL) "
			//                + " UNION ALL "
			//                + " SELECT  A.EFF_DATE,A.TAKE_DAYS, TO_CHAR (A.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//				+ " TO_CHAR (A.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A.ORDER_DR_CODE, "
			//                + " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                + " '（出） '||A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,A.FREQ_CODE, "
			//				+ " A.DOSE_TYPE,A.LINKMAIN_FLG,A.LINK_NO,A.DR_NOTE,A.ORDER_CODE,A.CAT1_TYPE,A.ORDER_NO,"//shibl 20130121 add order_no
			//				+ " TO_CHAR (B.NS_EXEC_DATE,'MM/DD HH24:MI') AS NS_EXEC_DATE,B.NS_EXEC_CODE,A.RX_KIND,A.ROUTE_CODE,A.NS_NOTE, "
			//				+
			//				// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				" TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//				+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//
			//				//add by yangjj 20151120 增加自备药信息
			//				+ " A.DISPENSE_FLG,A.ORDER_SEQ "
			//				
			//				// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				+ " FROM   ODI_ORDER A, SYS_UNIT F,ODI_DSPNM B "
			//				+ " WHERE  A.CASE_NO='"
			//				+ this.getCaseNo()
			//				+ "'"
			//				+ " AND A.CASE_NO=B.CASE_NO (+)"
			//				+ " AND A.ORDER_NO=B.ORDER_NO (+)"
			//				+ " AND A.ORDER_SEQ=B.ORDER_SEQ (+)"
			//				+ " AND A.RX_KIND='"
			//				+ "DS"
			//				+ "' "
			//				+ " AND A.HIDE_FLG = 'N' "
			//				+ " AND A.GIVE_FLG = 'N' "//医生站不选“给”字才打印出来 wanglong add 20140423
			//				+ " AND A.MEDI_UNIT = F.UNIT_CODE (+)"
			//				+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//						: " AND A.DEPT_CODE = '"
			//								+ getValueString("DEPT_LIST") + "'")
			//                // 过滤术中医嘱wanglong add 20140707
			//                + " AND A.OPBOOK_SEQ IS NULL " 
			//                
			//                //add by yangjj 20151127
			//                /*+ " UNION ALL"
			//                + " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//				+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
			//                + " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                + " A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
			//				+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,"//shibl 20130121 add order_no
			//				+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
			//				+
			//				// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				" TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//				+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//				
			//				//add by yangjj 20151120 增加自备药信息
			//				+ " A1.DISPENSE_FLG "
			//				
			//				// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 "
			//				+ " WHERE  A1.CASE_NO='"
			//				+ this.getCaseNo()
			//				+ "'"
			//				+ " AND A1.CASE_NO=B1.CASE_NO (+)"
			//				+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
			//				+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
			//				+ " AND A1.RX_KIND='"
			//				+ "ST"
			//				+ "' "
			//				+ " AND A1.HIDE_FLG = 'N' "
			//				+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
			//				+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//						: " AND A1.DEPT_CODE = '"
			//								+ getValueString("DEPT_LIST") + "'") */
			//				+ " UNION ALL"
			//                + " SELECT  A1.EFF_DATE,A1.TAKE_DAYS, TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//				+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, A1.ORDER_DR_CODE, "
			//                + " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                + " '（出） '||A1.ORDER_DESC,A1.MEDI_QTY,F1.UNIT_CHN_DESC,A1.FREQ_CODE, "
			//				+ " A1.DOSE_TYPE,A1.LINKMAIN_FLG,A1.LINK_NO,A1.DR_NOTE,A1.ORDER_CODE,A1.CAT1_TYPE,A1.ORDER_NO,"//shibl 20130121 add order_no
			//				+ " '取消' AS NS_EXEC_DATE,B1.NS_EXEC_CODE,A1.RX_KIND,A1.ROUTE_CODE,A1.NS_NOTE, "
			//				+
			//				// $$==========start add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				" TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//				+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//				
			//				//add by yangjj 20151120 增加自备药信息
			//				+ " A1.DISPENSE_FLG ,A1.ORDER_SEQ"
			//				
			//				// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//				// =============$$//
			//				+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 "
			//				+ " WHERE  A1.CASE_NO='"
			//				+ this.getCaseNo()
			//				+ "'"
			//				+ " AND A1.CASE_NO=B1.CASE_NO (+)"
			//				+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
			//				+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
			//				+ " AND A1.RX_KIND='"
			//				+ "DS"
			//				+ "' "
			//				
			//				//add by yangjj 20161214 
			//				+ " AND A1.TEMPORARY_FLG='Y' "
			//				
			//				+ " AND A1.HIDE_FLG = 'N' "
			//				+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
			//				+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//						: " AND A1.DEPT_CODE = '"
			//								+ getValueString("DEPT_LIST") + "'")
			//                
			//                +") A "
			//                + " ORDER BY A.RX_KIND DESC, A.EFF_DATE ASC,ORDER_NO,ORDER_SEQ " ;
		}else {// 长期医嘱
			String unionStr0 = "";
			String unionStr1 = "";
			
			unionStr0 = " SELECT TO_CHAR(A.EFF_DATE,'MM/DD') AS EFF_DATE_DAY,"
					+ " TO_CHAR(A.EFF_DATE,'HH24:MI') AS EFF_DATE_TIME, "
					+ " A.ORDER_DR_CODE,"
					+ " A.NS_CHECK_CODE,"
					+ " A.ORDER_DESC,"
					+ " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " A.MEDI_QTY,"
					+ " F.UNIT_CHN_DESC,"
					+ " A.FREQ_CODE,"
					+ " A.DOSE_TYPE,"
					+ " A.LINKMAIN_FLG,"
					+ " A.LINK_NO, "
					+ " A.DR_NOTE,"
					+ " A.ORDER_CODE,"
					+ " A.CAT1_TYPE,"
					//==start==modify by kangy 20171009 长期医嘱开立抗生素医嘱未停止之前不显示停止时间
					+"CASE WHEN A.DC_DATE<SYSDATE THEN TO_CHAR(A.DC_DATE,'MM/DD') WHEN A.DC_DATE>=SYSDATE THEN '' END DC_DATE_DAY,"
					+ "CASE WHEN A.DC_DATE<SYSDATE THEN TO_CHAR(A.DC_DATE,'HH24:MI') WHEN A.DC_DATE>=SYSDATE THEN '' END DC_DATE_TIME, "
					+ "CASE WHEN A.DC_DATE<SYSDATE THEN A.DC_DR_CODE WHEN A.DC_DATE>=SYSDATE THEN '' END DC_DR_CODE,"
					//==end==modify by kangy 20171009 长期医嘱开立抗生素医嘱未停止之前不显示停止时间
					+ " A.DC_NS_CHECK_CODE,"
					+ " A.RX_KIND,"
					+ " A.ROUTE_CODE,"
					+ " A.NS_NOTE,"
					+ " A.CASE_NO,"
					+ " A.ORDER_NO,"
					+ " A.ORDER_SEQ, "
					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " '' AS NS_EXEC_DATE, "
					+ " A.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D.PS_FLG, "
					+ " D.ROUTE_CHN_DESC, "
					+ " A.INFLUTION_RATE "//增加滴速  machao  20171128
					// modified by WangQing at 20170215 -end

					// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
					// =============$$//
					+ " FROM   ODI_ORDER A,SYS_UNIT F ,SYS_PHAROUTE D "
					+ " WHERE  A.CASE_NO='"
					+ this.getCaseNo()
					+ "' "
					+ " AND A.RX_KIND='"
					+ orderFlg
					+ "' "
					+ " AND A.HIDE_FLG='N' "

					// modified by WangQing at 20170215 -start
					+ " AND A.ROUTE_CODE=D.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end

					+ " AND A.MEDI_UNIT=F.UNIT_CODE (+)"
					+ " AND (A.DC_DATE IS NULL OR (A.DC_DATE IS NOT NULL AND A.NS_CHECK_DATE IS NOT NULL))"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'")
					// 过滤术中医嘱wanglong add 20140707
					+ " AND (A.ORDER_DEPT_CODE NOT IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE OP_FLG='Y') AND A.OPBOOK_SEQ IS NULL) ";
			
			
			unionStr1 = " UNION ALL"
					+ " SELECT  "
					+ " TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, "
					+ " A1.ORDER_DR_CODE,"
					+ " A1.NS_CHECK_CODE,"
					+ " A1.ORDER_DESC, "
					+ " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
					+ " A1.MEDI_QTY,"
					+ " F1.UNIT_CHN_DESC,"
					+ " A1.FREQ_CODE, "
					+ " A1.DOSE_TYPE,"
					+ " A1.LINKMAIN_FLG,"
					+ " A1.LINK_NO,"
					+ " A1.DR_NOTE,"
					+ " A1.ORDER_CODE,"
					+ " A1.CAT1_TYPE,"
					+ " '取消' AS DC_DATE_DAY,"
					+ " '' AS DC_DATE_TIME,"
					+ " A1.DC_DR_CODE,"
					+ " A1.DC_NS_CHECK_CODE,"
					+ " A1.RX_KIND,"
					+ " A1.ROUTE_CODE,"
					+ " A1.NS_NOTE, "
					+ " A1.CASE_NO,"
					+ " A1.ORDER_NO,"
					+ " A1.ORDER_SEQ,"//shibl 20130121 add order_no
					+ " TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
					+ " '取消' AS NS_EXEC_DATE,"
					+ " A1.DISPENSE_FLG, "

					// modified by WangQing at 20170215 -start
					+ " D1.PS_FLG, "
					+ " D1.ROUTE_CHN_DESC, "
					+ " B1.INFLUTION_RATE "//增加滴速  machao  20171128
					// modified by WangQing at 20170215 -end



					+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 ,SYS_PHAROUTE D1 "
					+ " WHERE  A1.CASE_NO='"
					+ this.getCaseNo()
					+ "'"
					+ " AND A1.CASE_NO=B1.CASE_NO (+)"
					+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
					+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
					+ " AND A1.RX_KIND='"
					+ orderFlg
					+ "' "

					//add by yangjj 20161214 
					+ " AND A1.TEMPORARY_FLG='Y' "

					// modified by WangQing at 20170215 -start
					+ " AND A1.ROUTE_CODE=D1.ROUTE_CODE (+) "
					// modified by WangQing at 20170215 -end




					+ " AND A1.HIDE_FLG = 'N' "
					+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
					+ (getValueString("DEPT_LIST").length() == 0 ? ""
							: " AND A1.DEPT_CODE = '"
							+ getValueString("DEPT_LIST") + "'") ;


			sql = "SELECT * FROM ("
					+ unionStr0
					+ unionStr1
					
					+ " ) T "

                    + " ORDER BY EFF_DATE_FULL ,ORDER_NO,ORDER_SEQ";

			//			
			//			
			//			//add by yangjj 20161214
			//			String unionStr = " UNION ALL"
			//                    + " SELECT  "
			//                    + " TO_CHAR (A1.EFF_DATE, 'MM/DD') AS EFF_DATE_DAY, "
			//					+ " TO_CHAR (A1.EFF_DATE, 'HH24:MI') AS EFF_DATE_TIME, "
			//					+ " A1.ORDER_DR_CODE,"
			//					+ " A1.NS_CHECK_CODE,"
			//					+ " A1.ORDER_DESC, "
			//                    + " (SELECT SF1.IS_REMARK FROM SYS_FEE SF1 WHERE SF1.ORDER_CODE = A1.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//                    + " A1.MEDI_QTY,"
			//                    + " F1.UNIT_CHN_DESC,"
			//                    + " A1.FREQ_CODE, "
			//					+ " A1.DOSE_TYPE,"
			//					+ " A1.LINKMAIN_FLG,"
			//					+ " A1.LINK_NO,"
			//					+ " A1.DR_NOTE,"
			//					+ " A1.ORDER_CODE,"
			//					+ " A1.CAT1_TYPE,"
			//					+ " '取消' AS DC_DATE_DAY,"
			//					+ " '' AS DC_DATE_TIME,"
			//					+ " A1.DC_DR_CODE,"
			//					+ " A1.DC_NS_CHECK_CODE,"
			//					+ " A1.RX_KIND,"
			//					+ " A1.ROUTE_CODE,"
			//					+ " A1.NS_NOTE, "
			//					+ " A1.CASE_NO,"
			//					+ " A1.ORDER_NO,"
			//					+ " A1.ORDER_SEQ,"//shibl 20130121 add order_no
			//					+ " TO_CHAR (A1.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//					+ " TO_CHAR (A1.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//					+ " '取消' AS NS_EXEC_DATE,"
			//					+ " A1.DISPENSE_FLG "
			//					+ " FROM   ODI_ORDER_HISTORY A1, SYS_UNIT F1,ODI_DSPNM B1 "
			//					+ " WHERE  A1.CASE_NO='"
			//					+ this.getCaseNo()
			//					+ "'"
			//					+ " AND A1.CASE_NO=B1.CASE_NO (+)"
			//					+ " AND A1.ORDER_NO=B1.ORDER_NO (+)"
			//					+ " AND A1.ORDER_SEQ=B1.ORDER_SEQ (+)"
			//					+ " AND A1.RX_KIND='"
			//					+ orderFlg
			//					+ "' "
			//					
			//					//add by yangjj 20161214 
			//					+ " AND A1.TEMPORARY_FLG='Y' "
			//					
			//					+ " AND A1.HIDE_FLG = 'N' "
			//					+ " AND A1.MEDI_UNIT = F1.UNIT_CODE (+)"
			//					+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//							: " AND A1.DEPT_CODE = '"
			//									+ getValueString("DEPT_LIST") + "'") ;
			//			
			//			
			//			sql = "SELECT * FROM ("
			//					+ " SELECT TO_CHAR(A.EFF_DATE,'MM/DD') AS EFF_DATE_DAY,"
			//					+ " TO_CHAR(A.EFF_DATE,'HH24:MI') AS EFF_DATE_TIME, "
			//					+ " A.ORDER_DR_CODE,"
			//					+ " A.NS_CHECK_CODE,"
			//					+ " A.ORDER_DESC,"
			//                    + " (SELECT SF.IS_REMARK FROM SYS_FEE SF WHERE SF.ORDER_CODE = A.ORDER_CODE) IS_REMARK, "//wanglong add 20150108
			//					+ " A.MEDI_QTY,"
			//					+ " F.UNIT_CHN_DESC,"
			//					+ " A.FREQ_CODE,"
			//					+ " A.DOSE_TYPE,"
			//					+ " A.LINKMAIN_FLG,"
			//					+ " A.LINK_NO, "
			//					+ " A.DR_NOTE,"
			//					+ " A.ORDER_CODE,"
			//					+ " A.CAT1_TYPE,"
			//					+ " TO_CHAR(A.DC_DATE,'MM/DD') AS DC_DATE_DAY,"
			//					+ " TO_CHAR(A.DC_DATE,'HH24:MI') AS DC_DATE_TIME, "
			//					+ " A.DC_DR_CODE,"
			//					+ " A.DC_NS_CHECK_CODE,"
			//					+ " A.RX_KIND,"
			//					+ " A.ROUTE_CODE,"
			//					+ " A.NS_NOTE,"
			//					+ " A.CASE_NO,"
			//					+ " A.ORDER_NO,"
			//					+ " A.ORDER_SEQ, "
			//					+ " TO_CHAR (A.EFF_DATE, 'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_FULL, "
			//					+ " TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE_FULL, "
			//					+ " '' AS NS_EXEC_DATE, "
			//					+ " A.DISPENSE_FLG "
			//					
			//					// $$==========end add by lx 2011-05-24 加入 提供医嘱补录入功能
			//					// =============$$//
			//					+ " FROM   ODI_ORDER A,SYS_UNIT F "
			//					+ " WHERE  A.CASE_NO='"
			//					+ this.getCaseNo()
			//					+ "' "
			//					+ " AND A.RX_KIND='"
			//					+ orderFlg
			//					+ "' "
			//					+ " AND A.HIDE_FLG='N' "
			//					+ " AND A.MEDI_UNIT=F.UNIT_CODE (+)"
			//					+ " AND (A.DC_DATE IS NULL OR (A.DC_DATE IS NOT NULL AND A.NS_CHECK_DATE IS NOT NULL))"
			//					+ (getValueString("DEPT_LIST").length() == 0 ? ""
			//							: " AND A.DEPT_CODE = '"
			//									+ getValueString("DEPT_LIST") + "'")
			//                    // 过滤术中医嘱wanglong add 20140707
			//                    + " AND (A.ORDER_DEPT_CODE NOT IN (SELECT DEPT_CODE FROM SYS_DEPT WHERE OP_FLG='Y') AND A.OPBOOK_SEQ IS NULL) "
			//					
			//                    //add by yangjj 20161214
			//                    + unionStr
			//                    + " ) T "
			//                    
			//                    + " ORDER BY EFF_DATE_FULL ,ORDER_NO,ORDER_SEQ";
		}
		System.out.println("医嘱===sql====》"+sql);
		return sql;
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
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		return true;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getName() {
		return name;
	}

	public String getMrN0() {
		return mrN0;
	}

	public String getDept() {
		return dept;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMrN0(String mrN0) {
		this.mrN0 = mrN0;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	/**
	 * 续印
	 */
	public void onPrintXDDialog() {
		word.printXDDialog();
	}

	/**
	 * 显示行号开关
	 */
	public void onShowRowIDSwitch() {
		word.setShowRowID(getValueBoolean("ROW_NO"));
		word.update();
	}
	/**
	 * 处理切换的时候跟换TopMenu
	 */
	public void onShowWindowsFunction(){
		//显示UIshowTopMenu
		callFunction("UI|showTopMenu");
	}

	public static void main(String[] args) {

		JavaHisDebug.initClient();
		// JavaHisDebug.initServer();
		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\INWOrderSheetPrtAndPreView.x");
	}

}
