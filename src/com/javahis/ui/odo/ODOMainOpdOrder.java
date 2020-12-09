package com.javahis.ui.odo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.cxf.common.util.StringUtils;

import jdo.bil.BIL;
import jdo.bil.BILStrike;
import jdo.ctr.CTRPanelTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.odo.Diagrec;
import jdo.odo.DrugAllergy;
import jdo.odo.MedHistory;
import jdo.odo.OpdOrder;
import jdo.opb.OPB;
import jdo.opb.OPBTool;
import jdo.opd.ODOTool;
import jdo.opd.OPDSysParmTool;
import jdo.opd.Order;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.pha.PhaBaseTool;
import jdo.spc.INDTool;
import jdo.sys.DeptTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * 
 * Title: 门诊医生工作站医嘱对象
 * </p>
 * 
 * <p>
 * Description:门诊医生工作站医嘱对象
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainOpdOrder {
	
	public OdoMainControl odoMainControl;
	public ODOMainPat odoMainPat;
	public ODOMainReg odoMainReg;
	public ODOMainOther odoMainOther;
	public ODOMainReasonbledMed odoMainReasonbledMed;
	
	private static final String RXCIRL = "com.javahis.ui.odo.ODORxCtrl";
	private static final String RXOP = "com.javahis.ui.odo.ODORxOp";
	private static final String RXCHNMED = "com.javahis.ui.odo.ODORxChnMed";
	private static final String RXMED = "com.javahis.ui.odo.ODORxMed";
	private static final String RXEXA = "com.javahis.ui.odo.ODORxExa";
	
	public static final String W = "W";//西医
	public static final String C = "C";//中医
	private static final String NULLSTR = "";
	
	// 界面上的TABLE
	public TTable tblExa, tblOp, tblMed, tblChn, tblCtrl;
	
	public IODORx odoRxExa;
	public IODORx odoRxOp;
	public IODORx odoRxMed;
	public IODORx odoRxChn;
	public IODORx odoRxCtrl;
	
	public String phaCode = "";// 诊间对药房
	public String wc = "";// 中西医标记
	public double dctMediQty = 0.0;// 默认中药用量
	public double dctTakeDays = 0;// 默认中药天数
	public String dctFreqCode = "";// 默认中药频次
	public String dctRouteCode = "";// 默认中药用法
	public String dctAgentCode = "";// 默认中药煎法
	public String rxType;// 处方类型
	public String tableName;// 公用TABLE名
	public String tempRxNo;
	public String rxName;// 处方类型名称
	private int warnFlg;
	public List<String> indOrgIsExinvs;
	public String orderDeptCode;
	public String diagDeptCode;
	
	private static final String RXFILTER = "rxFilter";// rxFilter
	private static final String TAGDCTTAKEQTY = "DCT_TAKE_QTY";// 默认中药用量控件名
	private static final String TAGDCTTAKEDAY = "DCT_TAKE_DAYS";// 默认中药天数控件名
	private static final String TAGDCTFREQCODE = "G_FREQ_CODE";// 默认中药频次控件名
	private static final String TAGDCTROUTECODE = "G_ROUTE_CODE";// 默认中药用法控件名
	private static final String TAGGDCTAGENTCODE = "G_DCTAGENT_CODE";// 默认中药煎法控件名
	private static final String TAGCHNFREQCODE = "CHN_FREQ_CODE";// 频次控件名
	private static final String TAGCHNROUTECODE = "CHN_ROUTE_CODE";// 用法下拉区域控件名
	private static final String TAGDCTAGENTCODE = "DCTAGENT_CODE";// 煎药方式下拉列表控件名
	public static final String TAGTTABPANELORDER = "TTABPANELORDER";// 医嘱切换也签控件名
	
	private static final String URLPHAREDRUGMSG = "%ROOT%\\config\\pha\\PHAREDrugMsg.x";
	private static final String URLOPDEDITALL = "%ROOT%\\config\\opd\\OPDEditAll.x";
	private static final String URLOPDORDERSETSHOW = "%ROOT%\\config\\opd\\OPDOrderSetShow.x";
	private static final String URLSYSFEE_PHA = "%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_PHA.x";
	private static final String URLOPDORDERUSEDESC = "%ROOT%\\config\\opd\\OPDOrderUseDesc.x";
	
	private static final String EXAPOPUPMENUSYNTAX = "显示集合医嘱细项|Show LIS/RIS Detail Items,onOrderSetShow";
	private static final String OPPOPUPMENUSYNTAX = "显示集合医嘱细项|Show Treatment Detail Items,onOpShow";
	private static final String MEDPOPUPMENUSYNTAX = "显示药嘱信息|Show Med Info,onSysFeeShow;显示合理用药信息|Show Rational Drug Use,onQueryRationalDrugUse;使用说明|Show Drug Use Desc,useDrugMenu";
	private static final String CHNPOPUPMENUSYNTAX = "显示合理用药信息|Show Rational Drug Use,onQueryRationalDrugUse";
	
	private static final String OPTBLPARMMAPENG = "FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE";
	private static final String MEDTBLPARMMAPENG = "FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE";
	private static final String OPTBLPARMMAPCHN = "FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE";
	private static final String MEDTBLPARMMAPCHN = "FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE";
	
	public static final String MEGOVERTIME = "已超过看诊时间不可修改";
	public static final String MEGBILLED = "已计费,不能修改或删除医嘱";
	public static final String MEGBILLED2 = "此医嘱已经登记,不能删除";
	public static final String MEGBILLED3 = "此处方签中存在已经登记的医嘱,不能删除";
	
	/**
	 * ODOMainOpdOrder构造
	 * @param odoMainControl
	 */
	public ODOMainOpdOrder(OdoMainControl odoMainControl){
		this.odoMainControl = odoMainControl;
		try {
			odoRxExa = (IODORx) Class.forName(RXEXA).newInstance();
			odoRxOp = (IODORx) Class.forName(RXOP).newInstance();
			odoRxMed = (IODORx) Class.forName(RXMED).newInstance();
			odoRxChn = (IODORx) Class.forName(RXCHNMED).newInstance();
			odoRxCtrl = (IODORx) Class.forName(RXCIRL).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 */
	public void onInit(){
		this.odoMainPat = odoMainControl.odoMainPat;
		this.odoMainReg = odoMainControl.odoMainReg;
		this.odoMainOther = odoMainControl.odoMainOther;
		this.odoMainReasonbledMed = odoMainControl.odoMainReasonbledMed;
		TParm sysparm = OPDSysParmTool.getInstance().getSysParm();
		dctMediQty = sysparm.getDouble(TAGDCTTAKEQTY, 0);
		dctTakeDays = sysparm.getInt(TAGDCTTAKEDAY, 0);
		dctFreqCode = sysparm.getValue(TAGDCTFREQCODE, 0);
		dctRouteCode = sysparm.getValue(TAGDCTROUTECODE, 0);
		dctAgentCode = sysparm.getValue(TAGGDCTAGENTCODE, 0);
		odoMainControl.setValue(TAGDCTTAKEDAY, dctTakeDays);
		odoMainControl.setValue(TAGDCTTAKEQTY, dctMediQty);
		odoMainControl.setValue(TAGCHNFREQCODE, OPDSysParmTool.getInstance()
				.getGfreqCode());
		odoMainControl.setValue(TAGCHNROUTECODE, OPDSysParmTool.getInstance()
				.getGRouteCode());
		odoMainControl.setValue(TAGDCTAGENTCODE, OPDSysParmTool.getInstance()
				.getGdctAgent());
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		indOrgIsExinvs = ODOTool.getInstance().getIndOrgIsExinv();
		orderDeptCode = ODOTool.getInstance().getOrderDeptCodeByDept(Operator.getDept());
		diagDeptCode = ODOTool.getInstance().getDiagDeptCodeByDept(Operator.getDept());  //add by huangtt 20150302 
	}
	
	/**
	 * 注册控件的事件
	 */
	public void onInitEvent() throws Exception{
		odoRxExa.onInitEvent(this);
		odoRxOp.onInitEvent(this);
		odoRxMed.onInitEvent(this);
		odoRxChn.onInitEvent(this);
		odoRxCtrl.onInitEvent(this);
	}
	
	/**
	 * 初始化医嘱panel
	 */
	public void initPanel() throws Exception{
		this.setTableInit(ODORxExa.TABLE_EXA, false);// 检验检查
		this.setTableInit(ODORxOp.TABLE_OP, false);// 处置
		this.setTableInit(ODORxMed.TABLE_MED, false);// 西药
		this.setTableInit(ODORxChnMed.TABLE_CHN, false);// 中药
		this.setTableInit(ODORxCtrl.TABLE_CTRL, false);// 毒药
		TTabbedPane tabP = (TTabbedPane) odoMainControl.getComponent(TAGTTABPANELORDER);
		
		
		//zhangp
		odoRxOp.init();
		odoRxMed.init();
		odoRxChn.init();
		odoRxCtrl.init();
		odoRxExa.init();
		
		if (tabP.getSelectedIndex() != ODORxExa.TABBEDPANE_INDEX) {
			tabP.setSelectedIndex(ODORxExa.TABBEDPANE_INDEX);
			onChangeOrderTab();// 页签切换
		}
		
//		tabP.setSelectedIndex(ODORxOp.TABBEDPANE_INDEX);
//		onChangeOrderTab();
//		tabP.setSelectedIndex(ODORxMed.TABBEDPANE_INDEX);
//		onChangeOrderTab();
//		tabP.setSelectedIndex(ODORxChnMed.TABBEDPANE_INDEX);
//		onChangeOrderTab();
//		tabP.setSelectedIndex(ODORxCtrl.TABBEDPANE_INDEX);
//		onChangeOrderTab();
//		tabP.setSelectedIndex(ODORxExa.TABBEDPANE_INDEX);
//		onChangeOrderTab();
	}
	
	public void onOpdClear() throws Exception{
		//医生站金额控件清空
		for (int i = 0; i < OPBTool.controlNameAmt.length; i++) {//====pangben 2013-5-2
			odoMainControl.setValue(OPBTool.controlNameAmt[i], 0.0);
		}
		String[] nullList = new String[] {};
		//处方签下拉清空
		TComboBox combo=null;
		for (int i = 0; i < OPBTool.controlNameCombo.length; i++) {//====pangben 2013-5-2
			combo = (TComboBox) odoMainControl.getComponent(OPBTool.controlNameCombo[i]);
			combo.setVectorData(nullList);
		}
		tblExa.removeRowAll();
		tblOp.removeRowAll();
		tblMed.removeRowAll();
		tblChn.removeRowAll();
		tblCtrl.removeRowAll();
	}
	
	/**
	 * 初始化非集合医嘱的TABLE
	 * 
	 * @param rxNo
	 *            String
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initNoSetTable(String rxNo, String tableName, boolean isInit) throws Exception{
		if (StringUtil.isNullString(tableName))
			return false;
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		String filter = "RX_NO='" + rxNo + "'";
		if (ODORxOp.TABLE_OP.equalsIgnoreCase(tableName)) {
			filter += " AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='' OR HIDE_FLG='N')";
		}
		if (!isInit) {
			table.setDataStore(odoMainControl.odo.getOpdOrder());
			table.setFilter(filter);
			if (!table.filter()) {
				odoMainControl.messageBox("E0024"); // 初始化参数失败
				return false;
			}
		} else {
			odoMainControl.odo.getOpdOrder().setFilter(filter);
			if (!odoMainControl.odo.getOpdOrder().filter()) {
				odoMainControl.messageBox("E0024"); // 初始化参数失败
				return false;
			}
		}
		Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
		return true;
	}
	
	/**
	 * 初始化显示集合医嘱的TABLE
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initSetTable(String tableName, boolean isInit) throws Exception{
		if (StringUtil.isNullString(tableName)) {
			return false;
		}
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		String rxNo = (String) odoMainControl.getValue(ODORxExa.EXA_RX);
		String filter = "RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')";
		if (!isInit)
			table.setDataStore(odoMainControl.odo.getOpdOrder());
		table.setFilter(filter);
		table.filter();
		tempRxNo = (String) odoMainControl.getValue(ODORxExa.EXA_RX);
		table.getDataStore().filterObject(this, RXFILTER);
		table.setDSValue();
		
		//add by huangtt 20150422 计算折扣金额 start
		 table = (TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA);
		 int columnAr = table.getColumnIndex("AR_AMT_MAIN");
		 int columnOwn = table.getColumnIndex("OWN_AMT_MAIN");
		 int columnPay = table.getColumnIndex("PAYAMOUNT");
		 for (int i = 0; i < table.getRowCount(); i++) {
			double own_amt = (Double) table.getValueAt(i, columnOwn);
			double ar_amt = (Double) table.getValueAt(i, columnAr);
			table.setValueAt(own_amt-ar_amt, i, columnPay);
		  }
		//add by huangtt 20150422 计算折扣金额 end
		
		this.calculateCash(ODORxExa.TABLE_EXA, ODORxExa.AMT_TAG);
		if (odoMainControl.odoMainTjIns.whetherCallInsItf) {
			Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
					odoMainControl.odoMainTjIns.whetherCallInsItf);
			table.setRowTextColorMap(inscolor);
		}
		return true;
	}
	
	public boolean rxFilter(TParm parm, int row) throws Exception {
		String s = parm.getValue("SETMAIN_FLG", row);
		return parm.getValue("RX_NO", row).equalsIgnoreCase(tempRxNo)
				&& (s.equalsIgnoreCase("Y") || s.length() == 0);
	}
	
	/**
	 * 初始化制定COMBO并返回处方号
	 * 
	 * @param rxName
	 *            String
	 * @param rxType
	 *            String
	 * @return String
	 */
	public String initRx(String rxName, String rxType) throws Exception {
		if (StringUtil.isNullString(rxName) || StringUtil.isNullString(rxType)) {
			return NULLSTR;
		}
		TComboBox combo = (TComboBox) odoMainControl.getComponent(rxName);
		String[] data = odoMainControl.odo.getOpdOrder().getRx(rxType);
		if (data == null || data.length < 0) {
			return NULLSTR;
		}
		String rxNo = getRxNo(data, 0);
		if (StringUtil.isNullString(rxNo)) {
			data = new String[1];
			rxNo = odoMainControl.odo.getOpdOrder().newPrsrp(rxType);
			if (StringUtil.isNullString(rxNo)) {
				odoMainControl.messageBox("E0032"); // 生成处方号失败
				return NULLSTR;
			}
			if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				data[0] = rxNo + ",【" + 1 + "】 Rx";
			} else {
				data[0] = rxNo + ",【" + 1 + "】 处方签";
			}
		} else {
			if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)
					&& !ODORxChnMed.CHN.equalsIgnoreCase(rxType)) {
				odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
			}
		}
		combo.getModel().setItems(new Vector());
		TComboNode nodeNull = new TComboNode();
		combo.getModel().getItems().add(nodeNull);
		combo.setData(data, ",");
		combo.setValue(rxNo);
		return rxNo;
	}
	
	/**
	 * 执行收费以后重新初始化界面数据
	 */
	public void onExeFee() throws Exception{
		// 重新加载数据
		if (!odoMainControl.odo.getOpdOrder().onQuery()) {
			return ;
		}
		//=======pangben 2013-7-10 修改界面删除空行问题
		// 删除后，增加一空行
		TTabbedPane tabPanel = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELORDER);
		String rxNo= NULLSTR;
		TTable table=null;
		String rxType= NULLSTR;
		String tableName= NULLSTR;
		switch (tabPanel.getSelectedIndex()) {
		case ODORxExa.TABBEDPANE_INDEX:
			rxNo = odoMainControl.getValueString(ODORxExa.EXA_RX);
			table = tblExa;
			rxType = ODORxExa.EXA;
			tableName = ODORxExa.TABLE_EXA;
			break;
		case ODORxOp.TABBEDPANE_INDEX:
			// 处置
			rxNo = odoMainControl.getValueString(ODORxOp.OP_RX);
			table = tblOp;
			rxType = ODORxOp.OP;
			tableName = ODORxOp.TABLE_OP;
			break;
		case ODORxMed.TABBEDPANE_INDEX:
			// 西药
			rxNo = odoMainControl.getValueString(ODORxMed.MED_RX);
			table = tblMed;
			rxType = ODORxMed.MED;
			tableName = ODORxMed.TABLE_MED;
			break;
		case ODORxChnMed.TABBEDPANE_INDEX:
			// 中药
			rxNo = odoMainControl.getValueString(ODORxChnMed.CHN_RX);
			table = tblChn;
			rxType = ODORxChnMed.CHN;
			tableName = ODORxChnMed.TABLE_CHN;
			break;
		case ODORxCtrl.TABBEDPANE_INDEX:
			rxNo = odoMainControl.getValueString(ODORxCtrl.CTRL_RX);
			table = tblCtrl;
			rxType = ODORxCtrl.CTRL;
			tableName = ODORxCtrl.TABLE_CTRL;
			break;
		}
		String []tableNames={ODORxExa.TABLE_EXA,ODORxOp.TABLE_OP,ODORxMed.TABLE_MED,ODORxChnMed.TABLE_CHN,ODORxCtrl.TABLE_CTRL};
		odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		table.setDSValue();
		getTableInit(tableName, tableNames);
		getChangeOrderTab();
	}
	
	/**
	 * 西药，管制药品，处置的checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) throws Exception{
		TTable table = (TTable) obj;
		
		table.acceptText();
		table.setDSValue();
		return false;
	}
	/**
	 * 西药，管制药品，处置的checkBox事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox_influne(Object obj) throws Exception{
		TTable table = (TTable) obj;
		
		int selectNum = table.getSelectedRow();
		int num = table.getRowCount();
		
		table.acceptText();
		table.setDSValue();
		table.acceptText();
		String linkNo = table.getValueAt(0, 7)+"";
		if(StringUtil.isNullString(linkNo)){//去掉所有滴速
		}else{//增加所有滴速
			if((""+table.getValueAt(selectNum, 1)).equals("Y")){
				TDataStore dst = table.getDataStore();
				
				//odoMainControl.messageBox(""+table.getValueAt(selectNum,7 ));
				for(int i = selectNum;i<num;i++){
					dst.setItem(i, "INFLUTION_RATE", table.getValueAt(selectNum,7 ));
				}
				table.setDSValue();
			}
		}
		return false;
	}
	/**
	 * 新增一张处方签，更新combo，并初始table
	 * 
	 * @param rxType
	 *            处方类型
	 * @param rxName
	 *            combo名
	 * @param tableName
	 *            table名
	 */
	public void onAddOrderList(String rxType, String rxName, String tableName) throws Exception{
		// 通过取号原则取得新处方号
		String rxNo = odoMainControl.odo.getOpdOrder().newPrsrp(rxType);
		if (StringUtil.isNullString(rxNo)) {
			odoMainControl.messageBox("E0033"); // 新增处方失败
			return;
		}
		// 设置combo的新值，并设置显示值
		TComboBox combo = (TComboBox) odoMainControl.getComponent(rxName);
		String[] data = odoMainControl.odo.getOpdOrder().getRx(rxType);
		String newData = NULLSTR;
		if (odoMainControl.isEng) {
			newData = rxNo + ",【" + (data.length) + "】 Rx";
		} else {
			newData = rxNo + ",【" + (data.length) + "】 处方签";
		}
		combo.addData(newData, ",");
		combo.setValue(rxNo);
		if (!odoMainControl.odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odoMainControl.odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (ODORxChnMed.CHN.equalsIgnoreCase(rxType)) {
			odoMainControl.setValue("DCT_TAKE_DAYS", dctTakeDays);
			odoMainControl.setValue("DCT_TAKE_QTY", dctMediQty);
			odoMainControl.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			odoMainControl.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			odoMainControl.setValue("DCTAGENT_CODE", this.dctAgentCode);
			odoMainControl.setValue("DR_NOTE", NULLSTR);
			odoMainControl.setValue("CHN_EXEC_DEPT_CODE", NULLSTR);
			odoMainControl.setValue("URGENT_FLG", NULLSTR);
			odoMainControl.setValue("RELEASE_FLG", NULLSTR);
		}
		onChangeRx(rxType);
	}
	
	/**
	 * 删除整张处方签
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onDeleteOrderList(int rxType) throws Exception{
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		StringBuffer billFlg=new StringBuffer();//判断是否可以删除 ，同一张处方签中的状态不相同不能删除
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		boolean canSave = true;
		switch (rxType) {
		case ODORxMed.MED_INT:// 西药
			odoRxMed.onDeleteOrderList(rxType);
			break;
		case ODORxCtrl.CTRL_INT://管制药品
			odoRxCtrl.onDeleteOrderList(rxType);
			break;
		case ODORxChnMed.CHN_INT: // 中药
			odoRxChn.onDeleteOrderList(rxType);
			break;
		case ODORxOp.OP_INT://处置
			odoRxOp.onDeleteOrderList(rxType);
			break;
		case ODORxExa.EXA_INT://检验
			try {
				odoRxExa.onDeleteOrderList(rxType);
			break;
			} catch (Exception e) {
				// TODO: handle exception
				odoMainControl.callFunction("UI|save|setEnabled", false);
				odoMainControl.callFunction("UI|tempsave|setEnabled", false);
				odoMainControl.messageBox("删除异常,请重新选择该病患");
				canSave = false;
			}
		}
		//收费操作
		if("Y".equals(billFlg.toString()))
			odoMainControl.odoMainEkt.onFee();// 执行删除医嘱
		else
			if(canSave){
				onTempSave(null,0);
			}
		ODOTool.getInstance().updateMemPackageByDelete(odoMainControl.caseNo, odoMainControl.odo.getMrNo());
	}

	/**
	 * combo点选事件,根据处方号初始化table
	 * 
	 * @param rxType
	 *            处方类型
	 */
	public void onChangeRx(String rxType) throws Exception{
		if (StringUtil.isNullString(rxType)) {
			odoMainControl.messageBox("E0035"); // 操作失败
			return;
		}
		TTabbedPane tabbedPane = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELORDER);
		if (!ODORxExa.EXA.equalsIgnoreCase(rxType) && tabbedPane.getSelectedIndex() == 0) {
			return;
		}
		int type = StringTool.getInt(rxType);
		if (odoMainControl.odo == null || odoMainControl.odo.getOpdOrder() == null)
			return;
		switch (type) {
		// 西药
		case ODORxMed.MED_INT:
			odoRxMed.onChangeRx(rxType);
			break;
		// 毒麻药
		case ODORxCtrl.CTRL_INT:
			odoRxCtrl.onChangeRx(rxType);
			break;
		// 中药
		case ODORxChnMed.CHN_INT:
			odoRxChn.onChangeRx(rxType);
			break;
		// 处置
		case ODORxOp.OP_INT:
			odoRxOp.onChangeRx(rxType);
			break;
		// 检验检查
		case ODORxExa.EXA_INT:
			odoRxExa.onChangeRx(rxType);
			break;
		}
	}
	
	/**
	 * 医嘱页签点击事件
	 */
	public void onChangeOrderTab() throws Exception{
		if (odoMainControl.odo == null) {
			return;
		}
//		if (odoMainControl.odo.isModified()) {
//			this.onTempSave(null);
//		}
		OpdOrder opdOrder = odoMainControl.odo.getOpdOrder();
		String lastFilter = opdOrder.getFilter();
		opdOrder.setFilter("");
		opdOrder.filter();
		for (int i = 0; i < opdOrder.rowCount(); i++) {
			if(null==opdOrder.getItemData(i, "ORDER_CODE"))
				continue;
			String orderCode = opdOrder.getItemData(i, "ORDER_CODE").toString();//医嘱码
			if(StringUtil.isNullString(orderCode))//校验是否空行
				continue;
			if(opdOrder.getItemData(i, "FLG").equals("Y")){//勾选状态
				opdOrder.setItem(i, "FLG", "N");
			}
		}
		opdOrder.setFilter(lastFilter);
		opdOrder.filter();
		getChangeOrderTab();
	}
	
	/**
	 * 将切换页签方法分开
	 * -=======pangben 2013-5-15
	 */
	private void getChangeOrderTab() throws Exception{
		
		TTabbedPane tabPanel = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELORDER);
		if(tabPanel.getSelectedIndex() == ODORxMed.TABBEDPANE_INDEX ||
				tabPanel.getSelectedIndex() == ODORxChnMed.TABBEDPANE_INDEX ||
				tabPanel.getSelectedIndex() == ODORxCtrl.TABBEDPANE_INDEX 
				){
			if(odoMainReg.checkWeight(odoMainControl.getValueString("AGE") , odoMainControl.getValueString("WEIGHT"))){
				odoMainControl.messageBox("患者无体重信息，请先补录！");
				tabPanel.setSelectedIndex(ODORxExa.TABBEDPANE_INDEX);
				odoRxExa.init();
				return;
			}
		}
		
		switch (tabPanel.getSelectedIndex()) {
		case ODORxExa.TABBEDPANE_INDEX:
			odoRxExa.init();
			break;
		case ODORxOp.TABBEDPANE_INDEX:
			// 处置
			odoRxOp.init();
			this.calculateCash(ODORxOp.TABLE_OP, ODORxOp.AMT_TAG);
			break;
		case ODORxMed.TABBEDPANE_INDEX:
			// 西药
			odoRxMed.init();
			break;
		case ODORxChnMed.TABBEDPANE_INDEX:
			// 中药
			odoRxChn.init();
			calculateChnCash(odoMainControl.getValueString(ODORxChnMed.CHN_RX));
			break;
		case ODORxCtrl.TABBEDPANE_INDEX:
			// 毒药
			odoRxCtrl.init();
			this.calculateCash(ODORxCtrl.TABLE_CTRL, ODORxCtrl.AMT_TAG);
			break;
		}
		if (!StringUtil.isNullString(tableName)) {
			TTable table = (TTable) odoMainControl.getComponent(tableName);
			table.acceptText();
		}
	}
	
	/**
	 * 
	 * @param rxKind
	 *            String 处方类型
	 * @param tableName
	 *            String TABLE名
	 */
	public void onEditAll(String rxKind, String tableName) throws Exception {
		TParm msg = (TParm) odoMainControl
				.openDialog(URLOPDEDITALL);
		if (msg == null) {
			return;
		}
		// 执行医疗卡操作，判断是否已经可以使用医疗卡
//		if(odoMainControl.odoMainEkt.readEKT())
//			return;
		String freqCode = msg.getValue("FREQ_CODE");
		String routeCode = msg.getValue("ROUTE_CODE");
		double mediQty = msg.getDouble("MEDI_QTY");
		double takeDays = msg.getDouble("TAKE_DAYS");
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		OpdOrder order = (OpdOrder) table.getDataStore();
		if (rxKind.equals(ODORxMed.MED_RX)) {//====pangben 2014-1-15 
			for (int i = 0; i < order.rowCount(); i++) {
				if (odoRxMed.check(order, i, "MED",true))//开立医嘱校验处方签是否可以开立
					return ;
			}
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (EKTpay.PAY_TYPE.equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				odoMainControl.messageBox("E0055"); // 已计费医嘱不能删除
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !EKTpay.PAY_TYPE.equals(order.getItemString(i, "BILL_TYPE"))) {
					odoMainControl.messageBox("E0055"); // 已计费医嘱不能删除
					return;
				}
			}
			if (!StringUtil.isNullString(freqCode)) {
				order.setItem(i, "FREQ_CODE", freqCode);
			}
			if (!StringUtil.isNullString(routeCode)) {
				order.setItem(i, "ROUTE_CODE", routeCode);
			}
			if (mediQty > 0.0) {
				order.setItem(i, "MEDI_QTY", mediQty);
			}
			if (takeDays > 0) {
				order.setItem(i, "TAKE_DAYS", takeDays);
			}
		}
		table.setDSValue();
	}
	
	/**
	 * 右击MENU弹出事件
	 * 
	 * @param tableName
	 *            String
	 */
	public void showPopMenu(String tableName) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(tableName);
        if (ODORxExa.TABLE_EXA.equalsIgnoreCase(tableName)) {
            table.setPopupMenuSyntax(EXAPOPUPMENUSYNTAX);
            return;
        }
        if (ODORxOp.TABLE_OP.equalsIgnoreCase(tableName)) {
            table.setPopupMenuSyntax(OPPOPUPMENUSYNTAX);
            return;
        }
        if (ODORxMed.TABLE_MED.equalsIgnoreCase(tableName) || ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(tableName)) {// modify by wanglong 20130522
            table.setPopupMenuSyntax(MEDPOPUPMENUSYNTAX);
            this.tableName = tableName;
            return;
        }
        if (ODORxChnMed.TABLE_CHN.equalsIgnoreCase(tableName)) {// add by wanglong 20130522
            table.setPopupMenuSyntax(CHNPOPUPMENUSYNTAX);
            return;
        }
	}
	
	/**
	 * 右击MENU显示集合医嘱事件
	 */
	public void onOrderSetShow() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		odoMainControl.openDialog(URLOPDORDERSETSHOW, parm);
	}
	
	/**
	 * 右击MENU显示诊疗项目细项事件
	 */
	public void onOpShow() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		odoMainControl.openDialog(URLOPDORDERSETSHOW, parm);
	}
	
	/**
	 * 右击MENU显示SYS_FEE事件
	 */
	public void onSysFeeShow() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String orderCode = order.getItemString(table.getSelectedRow(),
				"ORDER_CODE");
		TParm parm = new TParm();
		parm.setData("FLG", "OPD");
		parm.setData("ORDER_CODE", orderCode);
		odoMainControl.openDialog(URLSYSFEE_PHA, parm);
	}
	
	/**
	 * 数据对象的值改变事件
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            Object
	 */
	public void onSetItemEvent(String columnName, Object value) throws Exception{
		if (!"MEDI_QTY".equalsIgnoreCase(columnName)
				&& !"TAKE_DAYS".equalsIgnoreCase(columnName)
				&& !"FREQ_CODE".equalsIgnoreCase(columnName))
			return;
		int rxType = odoMainControl.odo.getOpdOrder().getItemInt(0, "RX_TYPE");
		String tableName = NULLSTR, tagName = NULLSTR;
		switch (rxType) {
		case ODORxMed.MED_INT:
			tableName = ODORxMed.TABLE_MED;
			tagName = ODORxMed.AMT_TAG;
			break;
		case ODORxCtrl.CTRL_INT:
			tableName = ODORxCtrl.TABLE_CTRL;
			tagName = ODORxCtrl.AMT_TAG;
			break;
		case ODORxChnMed.CHN_INT:
			tableName = ODORxChnMed.TABLE_CHN;
			tagName = ODORxChnMed.AMT_TAG;
			break;
		case ODORxOp.OP_INT:
			tableName = ODORxOp.TABLE_OP;
			tagName = ODORxOp.AMT_TAG;
			break;
		}
		this.calculateCash(tableName, tagName);
	}
	
	/**
	 * 是否是集合医嘱
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) throws Exception{
		boolean falg = false;
		if (orderParm.getBoolean("SETMAIN_FLG")) {
			falg = true;
		}
		return falg;
	}
	
	/**
	 * 校验是否可以开在同一个处方签上 ===========pangben 2012-7-12 添加管控
	 * 当前处方签管控
	 * @return
	 */
	public boolean getCheckRxNo() throws Exception{
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "已经打票的处方签不可以添加医嘱","此医嘱已经登记,不能删除")) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 各个TABLE中值改变事件用到的后置的库存检核方法
	 * 
	 * @param row
	 *            int
	 * @param execDept
	 *            String
	 * @param orderCode
	 *            String
	 * @param columnName
	 *            String
	 * @return boolean
	 */
	public boolean checkStoreQty(final int row, final String execDept,
			final String orderCode, final String columnName,final double oldDosageQty) throws Exception{
		if (StringUtil.isNullString(columnName)) {
			return true;
		}
		if (!("TAKE_DAYS".equalsIgnoreCase(columnName)
				|| "MEDI_QTY".equalsIgnoreCase(columnName)
				|| "FREQ_CODE".equalsIgnoreCase(columnName) || "EXEC_DEPT_CODE"
				.equalsIgnoreCase(columnName))) {
			return true;
		}
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
			OpdOrder order = odoMainControl.odo.getOpdOrder();
//			order.showDebug();
			double dosageQty = TypeTool.getDouble(order.getItemData(row,
					"DOSAGE_QTY"));
			if(orderCode.length()<=0)//==pangben 2013-5-2 当前没有选择的医嘱不执行校验库存操作
				return true;
			if (!TypeTool.getBoolean(order.getItemData(row, "RELEASE_FLG"))) {////add by huangtt 20150225   先判断是否是自备药
				if (isCheckKC(orderCode)) // 判断是否是“药品备注”
					if (Operator.getLockFlg().equals("Y")) {
						TParm parmQty=new TParm();
						parmQty.setData("ORDER_CODE", orderCode);
						parmQty.setData("ORG_CODE", execDept);
						parmQty.setData("EXEC_DEPT_CODE", execDept);
						parmQty.setData("CAT1_TYPE", "PHA");
						parmQty.setData("CASE_NO", order.getItemString(row, "CASE_NO"));
						parmQty.setData("RX_NO", order.getItemString(row, "RX_NO"));
						TParm orderParm=OrderTool.getInstance().selectLockQtyCheckSumQty(parmQty);
						double oldQty=0.00;
						if (orderParm.getCount("QTY")>0) {//数据库存在数据，扣除库存=当前操作的数量-数据库里的数量
							oldQty=orderParm.getDouble("QTY",0);
						}
						if (dosageQty>oldQty) {//增加
							if (!INDTool.getInstance().inspectIndStockQty(orderCode, execDept,
									dosageQty, oldQty,true)) {
								odoMainControl.messageBox("E0052");// 库存不足
								//order.setActive(row, false);
								return false;
							}
						}else{//减少
							if (!INDTool.getInstance().inspectIndStockQty(orderCode, execDept,
									dosageQty, oldQty,false)) {
								odoMainControl.messageBox("E0052");// 库存不足
								//order.setActive(row, false);
								return false;
							}
						}
					}else{
						// 物联网
						if (!INDTool.getInstance().inspectIndStock(execDept, orderCode,
								dosageQty)) {
							// this.messageBox("E0052"); // 库存不足
							// $$==========add by lx 2012-06-19加入存库不足，替代药提示
							TParm inParm = new TParm();
							inParm.setData("orderCode", orderCode);
							odoMainControl.openDialog(URLPHAREDRUGMSG,
									inParm);
							return false;
						}
					}
			}
			
		}
		return true;
	}
	
	/**
	 * 判断是否检核库存
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isCheckKC(String orderCode) throws Exception {
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0)) // 如果是药品备注那么就不检核库存
			return false;
		else
			// 不是药品备注的 要检核库存
			return true;
	}

	/**
	 * 删除整张处方签医嘱管控
	 * 
	 * @return
	 */
	public boolean deleteSumRxOrder(OpdOrder order, int row,
			StringBuffer billFlg) throws Exception{
		if(!billFlg.toString().equals(order.getItemData(row, "BILL_FLG").toString())){
			odoMainControl.messageBox("此处方签中医嘱状态不同,不能执行删除");
			return false;
		}
		
		return true;
	}

	/**
	 * 暂存操作 校验数据
	 * @return
	 */
	public boolean getTempSaveParm() throws Exception{
		// 新加的数据
		int newRow[] = odoMainControl.odo.getOpdOrder().getNewRows();
		TParm ordParm = new TParm();
		// 合理用药检测
		if (!odoMainReasonbledMed.checkDrugAuto()) {
			TTabbedPane tabPanel = (TTabbedPane) odoMainControl
					.getComponent(TAGTTABPANELORDER);
			switch (tabPanel.getSelectedIndex()) {
			case ODORxMed.TABBEDPANE_INDEX:
				odoRxMed.getTempSaveParm();// 西药
				break;
			case ODORxChnMed.TABBEDPANE_INDEX:
				odoRxChn.getTempSaveParm();//中药
				break;
			case ODORxCtrl.TABBEDPANE_INDEX:
				odoRxCtrl.getTempSaveParm();//管制药品
				break;
			}
			String buff = odoMainControl.odo.getOpdOrder().isFilter() ? OpdOrder.FILTER
					: OpdOrder.PRIMARY;
			int newDRow[] = odoMainControl.odo.getOpdOrder().getNewRows(buff);
			for (int i : newDRow) {
				ordParm = odoMainControl.odo.getOpdOrder().getRowParm(0);
				String order_code = ordParm.getValue("ORDER_CODE");
				if (!order_code.equals(NULLSTR)) {
					this.deleteorderAuto(0);
				}
			}
			return false;
		}
		
		for (int i : newRow) {
			ordParm = odoMainControl.odo.getOpdOrder().getRowParm(i);
			String order_code = ordParm.getValue("ORDER_CODE");
			// 进入医令管控接口
			if (getctrflg(order_code).equals("Y")) {
				TParm crtParm = new TParm();
				crtParm.setData("ADM_TYPE", odoMainReg.admType);
				crtParm.setData("CTZ_CODE", ordParm.getValue("CTZ1_CODE"));
				crtParm.setData("ORDER_CODE", ordParm.getValue("ORDER_CODE"));
				crtParm.setData("CASE_NO", ordParm.getValue("CASE_NO"));
				crtParm.setData("MR_NO", ordParm.getValue("MR_NO"));
				crtParm.setData("ORDER_DATE", ordParm
						.getTimestamp("ORDER_DATE"));
				if (CTRPanelTool.getInstance().selCTRPanel(crtParm)
						.getErrCode() == 100) {
					if (!CTRPanelTool.getInstance().selCTRPanel(crtParm)
							.getValue("FORCE_FLG").equals("Y")) {
						if (odoMainControl.messageBox("提示信息/Tip",
								 CTRPanelTool.getInstance()
												.selCTRPanel(crtParm).getValue(
														"MESSAGE") + ",继续开立?",
								0) != 0) {
							this.deleteorderAuto(i);
							return false;
						} else {
							odoMainControl.messageBox(CTRPanelTool.getInstance()
									.selCTRPanel(crtParm).getValue("MESSAGE"));
						}
					} else {
						odoMainControl.messageBox(CTRPanelTool.getInstance().selCTRPanel(
								crtParm).getValue("MESSAGE"));
						this.deleteorderAuto(i);
						return false;
					}
				}
			}
		}
		

		//=========yanjing  皮试校验   start
		TTabbedPane tabPanel = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELORDER);
		if (tabPanel.getSelectedIndex() == 2) {// 西药
			for (int j : newRow) {
				ordParm = odoMainControl.odo.getOpdOrder().getRowParm(j);
				String order_code = ordParm.getValue("ORDER_CODE");
				String order_desc = ordParm.getValue("ORDER_DESC");
				String routeCode = ordParm.getValue("ROUTE_CODE");
				
				
				if (!routeCode.equals("PS")) {
					// 查询是否为皮试药品
					String isPsSql = "SELECT SKINTEST_FLG,ROUTE_CODE FROM PHA_BASE WHERE ORDER_CODE = '"
							+ order_code + "' AND PHA_TYPE = 'W'";
					TParm psResult = new TParm(TJDODBTool.getInstance().select(
							isPsSql));
					if (psResult.getCount() > 0) {
						if (psResult.getValue("SKINTEST_FLG", 0).equals("Y")) {
							// 判断是否进行过皮试
							String isDoPsSql = "SELECT A.BATCH_NO,A.SKINTEST_FLG FROM OPD_ORDER A,SYS_PHAROUTE B  "
									+ "WHERE A.CASE_NO='"
									+ odoMainControl.caseNo
									+ "' AND A.ORDER_CODE='"
									+ order_code
									+ "'"
									+ "AND A.ROUTE_CODE=B.ROUTE_CODE AND B.PS_FLG='Y' ORDER BY A.ORDER_DATE DESC";
							TParm doPsResult = new TParm(TJDODBTool
									.getInstance().select(isDoPsSql));
							if (doPsResult.getCount() <= 0) {// 没有进行过皮试,给出提示
								if (odoMainControl.messageBox("提示信息/Tip",
										order_desc + "是皮试用药，当前用法不是皮试用法，是否继续开立",
										0) != 0) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		//=========yanjing  皮试校验   end
		
		String filter = odoMainControl.odo.getOpdOrder().getFilter();
		odoMainControl.odo.getOpdOrder().setFilter(
				"RX_TYPE='1' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
		odoMainControl.odo.getOpdOrder().filter();	
		int newR[] = odoMainControl.odo.getOpdOrder().getNewRows();
		String ages = odoMainControl.getValueString("AGE");
		if(ages.indexOf("岁") < 0){
			ages="0";
		}else{
			ages = ages.substring(0, ages.indexOf("岁"));
		}
		
		int age = Integer.parseInt(ages);
		for (int j : newR) {
			 ordParm = odoMainControl.odo.getOpdOrder().getRowParm(j);
			String order_desc = ordParm.getValue("ORDER_DESC");
			String routeCode = ordParm.getValue("ROUTE_CODE");
			String orderCode = ordParm.getValue("ORDER_CODE");
			String linkNo = ordParm.getValue("LINK_NO");
			String influtionRate = ordParm.getValue("INFLUTION_RATE");
			
			double meditQty = ordParm.getDouble("MEDI_QTY");
			
			TParm unitParm = new TParm( TJDODBTool.getInstance().select("SELECT UNIT_CHN_DESC FROM SYS_UNIT WHERE UNIT_CODE = '"+ordParm.getValue("MEDI_UNIT")+"'"));
			String unit =unitParm.getValue("UNIT_CHN_DESC", 0);
				
//			if(routeCode.equals("IVD")){
//				if(ordParm.getDouble("INFLUTION_RATE") == 0){
//					odoMainControl.messageBox(order_desc+"的用法是静脉点滴,请填写速率后在进行医嘱保存！！！！！");
//					return false;
//				}
//			}
			
			String sql = "SELECT MAXIMUMLIMIT_MEDI1,MAXIMUMLIMIT_MEDI2  FROM SYS_FEE WHERE ORDER_CODE = '"+orderCode+"'";
			TParm sysFeeParm = new TParm(TJDODBTool.getInstance().select(sql));
			
			if(age < 18 ){
				if(sysFeeParm.getValue("MAXIMUMLIMIT_MEDI1", 0).length() > 0){
//					odoMainControl.messageBox(meditQty+""+sysFeeParm.getDouble("MAXIMUMLIMIT_MEDI1", 0));

					if(meditQty > sysFeeParm.getDouble("MAXIMUMLIMIT_MEDI1", 0)){
//						odoMainControl.messageBox(order_desc+"药品用量已超极量。极量值: 小于18岁");
						
						if (odoMainControl.messageBox("提示信息/Tip",
								order_desc+"药品用量已超极量。极量值: 小于18岁为"+sysFeeParm.getValue("MAXIMUMLIMIT_MEDI1", 0) +unit+"，是否继续 " ,
								0) != 0) {
							return false;
						}
						
					}
				}
				
			}else{
				if(sysFeeParm.getValue("MAXIMUMLIMIT_MEDI2", 0).length() > 0){
//					odoMainControl.messageBox(meditQty+""+sysFeeParm.getDouble("MAXIMUMLIMIT_MEDI2", 0));
					if(meditQty > sysFeeParm.getDouble("MAXIMUMLIMIT_MEDI2", 0)){
//						odoMainControl.messageBox(order_desc+"已超极量！！！！！");
						if (odoMainControl.messageBox("提示信息/Tip",
								order_desc+"药品用量已超极量。极量值: 大于等于18岁为 "+sysFeeParm.getValue("MAXIMUMLIMIT_MEDI2", 0)+unit+"，是否继续 " ,
								0) != 0) {
							return false;
						}
						
					}
				}
			}
			
			
		}
		
		odoMainControl.odo.getOpdOrder().setFilter(
		 "RX_TYPE='1' AND ORDER_CODE <>'' ");
		odoMainControl.odo.getOpdOrder().filter();
		int newR1[] =odoMainControl.odo.getOpdOrder().getNewRows();
		List<String> linkNos = new ArrayList<String>();
		for (int j : newR1) {
			ordParm = odoMainControl.odo.getOpdOrder().getRowParm(j);
//			System.out.println(ordParm);
			String order_desc = ordParm.getValue("ORDER_DESC");
			String routeCode = ordParm.getValue("ROUTE_CODE");

			
			if(routeCode.equals("IVD")){
				if(ordParm.getDouble("INFLUTION_RATE") == 0){
					odoMainControl.messageBox(order_desc+"的用法是静脉点滴,请填写速率后在进行医嘱保存！！！！！");
					return false;
				}
			}
		}
		
		odoMainControl.odo.getOpdOrder().setFilter(
		 "");
		odoMainControl.odo.getOpdOrder().filter();
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int count = odoMainControl.odo.getOpdOrder().rowCount();
		TParm parmResult = new TParm();
		for (int i = 0; i < count; i++) {
			if ("1".equalsIgnoreCase(order.getItemString(i, "RX_TYPE")) && !StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				String rxNo = order.getItemString(i, "RX_NO");
				String linkNo =order.getItemString(i, "LINK_NO");
				String influtionRate =order.getItemString(i, "INFLUTION_RATE");
				
				if(!StringUtil.isNullString(linkNo)){
					parmResult.addData("RX_NO", rxNo);
					parmResult.addData("LINK_NO", linkNo);
					parmResult.addData("INFLUTION_RATE", influtionRate);
					if(!linkNos.contains(rxNo+"#"+linkNo)){
						linkNos.add(rxNo+"#"+linkNo);
					}
				}
			}
		}
//		System.out.println("parmResult---"+parmResult);
		if(!checkRout(parmResult)){
			odoMainControl.messageBox("相同连组号,请填写相同的速率!");
			return false;
		}
		
		odoMainControl.odo.getOpdOrder().setFilter(filter);
		odoMainControl.odo.getOpdOrder().filter();
		
		return true;
	}
	public boolean checkRout(TParm p){
//		System.out.println("111111:"+p);
		int count = p.getCount("LINK_NO");
		for(int i = 0;i<count;i++){
			String rxNo = p.getValue("RX_NO", i);
			String linkNo = p.getValue("LINK_NO", i);
			String inf = p.getValue("INFLUTION_RATE",i);
			if(i+1 < count){
				String rxNoNext = p.getValue("RX_NO", i+1);
				String linkNoNext = p.getValue("LINK_NO", i+1);
				String infNext = p.getValue("INFLUTION_RATE",i+1);
				if(linkNo.equals(linkNoNext) && rxNo.equals(rxNoNext)){
					if(!inf.equals(infNext)){
						return false;
					}
				}else{
					continue;
				}
			}
		}
		return true;
	}
	/**
	 * isWarn
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean isWarn(TParm parm) throws Exception{
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
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
	
	/**
	 * 校验是否可以开在同一个处方签上 ===========pangben 2012-7-12 添加管控
	 * 所有页签当前的处方签管控
	 * 
	 * @return false 不可以执行  true 可以执行 
	 */
	public String getCheckRxNoSum(TParm parm) throws Exception{
		String[] resultStrings =
				{odoRxExa.getCheckRxNoSum(parm),
				odoRxOp.getCheckRxNoSum(parm),
				odoRxMed.getCheckRxNoSum(parm),
				odoRxCtrl.getCheckRxNoSum(parm)
				};
		for (int i = 0; i < resultStrings.length; i++) {
			if(null != resultStrings[i]){
				return resultStrings[i];
			}
		}
		return null;
	}
	
	/**
	 * 设置给定TABLE已经初始化的标记
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 */
	public void setTableInit(String tableName, boolean isInit) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		table.setData(isInit);
	}

	/**
	 * 西医诊断radio注记
	 */
	public void onWFlg() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(ODOMainOther.TABLEDIAGNOSIS);
		table.acceptText();
		wc = W;
	}

	/**
	 * 中医诊断radio注记
	 */
	public void onCFlg() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(ODOMainOther.TABLEDIAGNOSIS);
		table.acceptText();
		wc = C;
	}
	
	/**
	 * 商品名方法
	 * 
	 * @param tag
	 *            String
	 * @param checkBox
	 *            String
	 */
	public void onGoods(String tag, String checkBox) throws Exception{
		TCheckBox tcb = (TCheckBox) odoMainControl.getComponent(checkBox);
		TTable table = (TTable) odoMainControl.getComponent(tag);
		// ORDER_ENG_DESC
		if (tcb.isSelected()) {
			if (ODORxOp.TABLE_OP.equalsIgnoreCase(tag)) {
				table.setParmMap(OPTBLPARMMAPENG);
				table.setDSValue();
			}
			if (ODORxMed.TABLE_MED.equalsIgnoreCase(tag)) {
				table.setParmMap(MEDTBLPARMMAPENG);
				table.setDSValue();
			}
			if (ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(tag)) {
				table.setParmMap(MEDTBLPARMMAPENG);
				table.setDSValue();
			}
		} else {
			// ORDER_DESC
			if (ODORxOp.TABLE_OP.equalsIgnoreCase(tag)) {
				table.setParmMap(OPTBLPARMMAPCHN);
				table.setDSValue();
			}
			if (ODORxMed.TABLE_MED.equalsIgnoreCase(tag)) {
				table.setParmMap(MEDTBLPARMMAPCHN);
				table.setDSValue();
			}
			if (ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(tag)) {
				table.setParmMap(MEDTBLPARMMAPCHN);
				table.setDSValue();
			}
		}
	}

	/**
	 * 初始化parm
	 * 20130428 yanjing 
	 * @param parm
	 * @param i
	 */
	public TParm initParmBase(TParm parm,int i) {
		TParm parmBase = new TParm();
		parmBase.addData("ROUTE_CODE", parm.getValue("ROUTE_CODE",i));
		parmBase.addData("DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT",i));
		parmBase.addData("RELEASE_FLG", parm.getValue("RELEASE_FLG",i));
		parmBase.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",i));
		parmBase.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT",i));
		parmBase.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE",i));
		parmBase.addData("FREQ_CODE", parm.getValue("FREQ_CODE",i));
		parmBase.addData("ORDER_CODE", parm.getValue("ORDER_CODE",i));
		parmBase.addData("URGENT_FLG", parm.getValue("URGENT_FLG",i));
		parmBase.addData("DR_NOTE", parm.getValue("DR_NOTE",i));
		parmBase.addData("ORDER_DESC", parm.getValue("ORDER_DESC",i));
		parmBase.addData("LINK_NO", parm.getValue("LINK_NO",i));
		parmBase.addData("GIVEBOX_FLG", parm.getValue("GIVEBOX_FLG",i));
		parmBase.addData("DISPENSE_QTY", parm.getValue("DISPENSE_QTY",i));
		parmBase.addData("TAKE_DAYS", parm.getValue("TAKE_DAYS",i));
		parmBase.addData("NS_NOTE", parm.getValue("NS_NOTE",i));
		parmBase.addData("USE", parm.getValue("USE",i));
		parmBase.addData("ACTIVE_FLG", parm.getValue("ACTIVE_FLG",i));
		parmBase.addData("ORDER_CODE_FEE", parm.getValue("ORDER_CODE_FEE",i));
		parmBase.addData("MEDI_QTY", parm.getValue(" MEDI_QTY",i));
		parmBase.addData("LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",i));
		TParm p = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE",i));
		String str = "";
		if(p.getCount() > 0){
			str = p.getValue("DOSE_TYPE", 0);
		}
		parmBase.addData("DOSE_TYPE", str);
		return parmBase;
	}
	
	/**
	 * 锁行
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean 是否为刚刚写入的order，如是，则此行不锁，如不是则此行锁
	 * @return String
	 */
	public String lockRows(String tableName, boolean isInit) throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		StringBuffer sb = new StringBuffer();
		int index;
		if (isInit) {
			index = table.getRowCount() - 1;
		} else {
			index = table.getRowCount() - 2;
		}
		for (int i = 0; i < index; i++) {
			sb.append(i + ",");
		}
		String lockRow = sb.toString();
		if (StringUtil.isNullString(lockRow))
			return NULLSTR;
		return lockRow.substring(0, lockRow.lastIndexOf(","));
	}
	
	/**
	 * 判断是否可以保存
	 * 
	 * @return boolean
	 */
	public boolean canSave() throws Exception{
		// 超出规定看诊时间 不可新增 不可修改
		if (!odoMainReg.canEdit()) {
			OpdOrder order = odoMainControl.odo.getOpdOrder();
			int[] newRows = order.getNewRows();
			if (newRows != null && newRows.length > 0) {
				return false; // 有新增行
			}
			int[] updateRows = order.getModifiedRows();
			if (updateRows != null && updateRows.length > 0) {
				return false; // 有修改行
			}
		}
		return true;
		
	}
	
	/**
	 * 取得RX COMBO的值
	 * 
	 * @param data
	 *            String[]
	 * @param i
	 *            int
	 * @return String
	 */
	private String getRxNo(String[] data, int i) throws Exception{
		if (data == null || data.length < 1 || i < 0)
			return null;
		String rxNo = (data[i].split(","))[0];
		return rxNo;
	}

	/**
	 * 判断给定TABLE是否已经初始化过
	 * 
	 * @param tableName
	 *            String
	 * @return boolean
	 */
	public boolean isTableInit(String tableName) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		return TCM_Transform.getBoolean(table.getData());
	}
	
	/**
	 * getWarn
	 * 
	 * @param flg
	 *            int
	 * @return boolean
	 */
	private boolean getWarn(int flg) throws Exception {
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
	 * 取得成本中心
	 * 
	 * @param dept_code
	 *            String
	 * @return String
	 */
	public String getCostCenter(String dept_code)throws Exception {
		return DeptTool.getInstance().getCostCenter(dept_code, NULLSTR);
	}
	
	/**
	 * 根据给入条件初始化一行order
	 * 
	 * @param order
	 *            OpdOrder
	 * @param row
	 *            int
	 * @param parm
	 *            TParm sysFeeParm
	 * @param parmBase
	 *            TParm phaBaseParm
	 */
	public void initOrder(OpdOrder order, int row, TParm parm, TParm parmBase)throws Exception {
		order.itemNow = true;
		order.setItem(row, "FLG", "N");//=======pangben 2013-3-22 默认选中
//		order.setItem(row, "PRESRT_NO", row + 1);
		order.setItem(row, "REGION_CODE", Operator.getRegion());
		order.setItem(row, "RELEASE_FLG", "N");
		order.setItem(row, "LINKMAIN_FLG", "N");
		order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC")
				.replaceFirst("(" + parm.getValue("SPECIFICATION") + ")", NULLSTR));
		order.setItem(row, "TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC"));
		order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
		order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		order.setItem(row, "IS_PRE_ORDER", parm.getValue("IS_PRE_ORDER"));//caowl 20131117
		order.setItem(row, "PRE_DATE", parm.getValue("PRE_DATE"));//caowl 20131117
		if (null!=parm.getValue("SKINTEST_FLG")
				&&parm.getValue("SKINTEST_FLG").length()>0) {
			order.setItem(row, "EXEC_DATE", parm.getValue("EXEC_DATE"));//add by huangjw 20141031
			order.setItem(row, "SKINTEST_FLG", parm.getValue("SKINTEST_FLG"));//皮试结果
			order.setItem(row, "BATCH_NO", parm.getValue("BATCH_NO"));//批号
		}
		if ("2".equals(odoMainControl.serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE2"));
		} else if ("3".equals(odoMainControl.serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE3"));
		} else
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
		// order.setItem(row, "OWN_PRICE", parm.getData("OWN_PRICE"));
		order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		String REXP_CODE = BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"),
				odoMainReg.admType);
		// ============xueyf modify 20120419 start
		// 追踪REXP_CODE为空的数据信息
		if (StringUtil.isNullString(REXP_CODE)) {
			odoMainControl.messageBox("该医嘱REXP_CODE为空，请通知信息中心。");
			System.err.println("字典没有该医嘱，请通知信息中心。");
			System.err.println("CHARGE_HOSP_CODE="
					+ parm.getValue("CHARGE_HOSP_CODE"));
			System.err.println("admType=" + odoMainReg.admType);
			TParm logParm = new TParm();
			logParm.setErr(-1, "CASE_NO="+odoMainControl.caseNo+" ORDER_CODE="+parm.getValue("ORDER_CODE")+" HEXP_CODE="+parm.getValue("CHARGE_HOSP_CODE"));
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
		            "noRexpCodeLog", logParm);
		}
		order.setItem(row, "REXP_CODE", REXP_CODE);
		// ============xueyf modify 20120419 stop
		order.setItem(row, "SETMAIN_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", 0);
		order.setItem(row, "CTZ1_CODE", odoMainPat.ctz[0]);
		order.setItem(row, "CTZ2_CODE", odoMainPat.ctz[1]);
		order.setItem(row, "CTZ3_CODE", odoMainPat.ctz[2]);
		order.setItem(row, "MR_CODE", parm.getValue("MR_CODE"));
		order.itemNow = false; // 是否调用计算总量方法的开关
		if (TypeTool.getDouble(parm.getData("MEDI_QTY")) > 0) {
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("MEDI_QTY")));
		} else {
			order.setItem(row, "MEDI_QTY", 1.0);
		}
		if (parm.getDouble("TOTQTY") > 0) {
			// 集合医嘱子项的用量 也要带入默认值（子项数量有可能大于1，所以当数量大于一时 拿总量作为用量）
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("TOTQTY")));
			order.itemNow = true; // 必须将setItem事件的开关关掉，否增会调用总量回推用量方法 造成用量为0
			order.setItem(row, "DISPENSE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
			order.itemNow = false; // 总量付初始值后 打开开关 用以计算总量
			order.setItem(row, "DOSAGE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
		} else {
			order.itemNow = true; // 必须将setItem事件的开关关掉，否增会调用总量回推用量方法 造成用量为0
			order.setItem(row, "DISPENSE_QTY", 1.0);
			order.itemNow = false; // 总量付初始值后 打开开关 用以计算总量
			order.setItem(row, "DOSAGE_QTY", 1.0);
		}
		order.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
		// zhangyong20110616
		order.setItem(row, "COST_CENTER_CODE", getCostCenter(parm
				.getValue("EXEC_DEPT_CODE")));
		// 判断是否是药品
	
		
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			String printNo = NULLSTR;
			if (row <= 0) {
				printNo = order.getPrintNo(parm.getValue("EXEC_DEPT_CODE"));
			} else {
				printNo = order.getItemString(row - 1, "PRINT_NO");
			}
			
			if (printNo==null) {
				odoMainControl.messageBox("E0112"); // 取得领药号失败
				String rxNo = order.getItemString(row, "RX_NO");
				order.deleteRow(row);
				order.newOrder(rxType, rxNo);
				return;
			}
			order.setItem(row, "PRINT_NO", printNo);
			int counterNo = order.getItemInt(row - 1, "COUNTER_NO");
			if (counterNo < 1) {
				counterNo = order.getCounterNo(parm.getValue("EXEC_DEPT_CODE"),
						printNo, rxType);
			}
			
			
			if (counterNo < 1) {
				odoMainControl.messageBox("E0112"); // 取得领药号失败
				String rxNo = order.getItemString(row, "RX_NO");
				order.deleteRow(row);
				order.newOrder(rxType, rxNo);
				return;
			}
			order.setItem(row, "COUNTER_NO", counterNo);
		}
		order.setItem(row, "RPTTYPE_CODE", parm.getValue("RPTTYPE_CODE"));
		order.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
		order.setItem(row, "DEV_CODE", parm.getValue("DEV_CODE"));
		order.setItem(row, "MR_CODE", parm.getValue("MR_CODE"));
		order.setItem(row, "DEGREE_CODE", parm.getValue("DEGREE_CODE"));
		if (parmBase != null && parmBase.getCount("FREQ_CODE") > 0) {
			// double takeQty = (parmBase.getDouble("MEDI_UNIT",
			// 0)<1.0)?1.0:parmBase.getDouble("MEDI_UNIT", 0);
			order.setItem(row, "MEDI_QTY",null!=parmBase.getValue("MEDI_QTY", 0)?parmBase.getValue("MEDI_QTY", 0):NULLSTR);
			// System.out.println("开药单位："+parmBase.getValue("MEDI_UNIT", 0));
			order.setItem(row, "MEDI_UNIT", null!=parmBase.getValue("MEDI_UNIT", 0)?parmBase.getValue("MEDI_UNIT", 0):NULLSTR);
			order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
			order.setItem(row, "ROUTE_CODE",  null!=parmBase.getValue("ROUTE_CODE",0)?parmBase.getValue("ROUTE_CODE",0):NULLSTR);
			int takedays = TypeTool.getInt(parm.getData("TAKE_DAYS")) > 0 ? TypeTool
					.getInt(parm.getData("TAKE_DAYS"))
					: TypeTool.getInt(parmBase.getData("TAKE_DAYS", 0));
			if (takedays < 0) {
				takedays = 1;
			}
			order.setItem(row, "TAKE_DAYS", takedays);
			order.setItem(row, "CTRLDRUGCLASS_CODE", parmBase.getValue(
					"CTRLDRUGCLASS_CODE", 0));
			order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
					0));
			order.setItem(row, "DOSE_TYPE", parmBase.getValue("DOSE_TYPE", 0));
			
//			System.out.println("parmBase----->"+parmBase);
			
			// 判断是否是按总量给药
			if ("Y".equalsIgnoreCase(parmBase.getValue("DSPNSTOTDOSE_FLG", 0))) {
				order.setItem(row, "DISPENSE_QTY", parmBase.getValue(
						"DEFAULT_TOTQTY", 0));
			} else {
				// $$============add by lx 2012/03/03
				// 加入默认总量start=================$$//
				double tMediQty = parmBase.getDouble("MEDI_QTY", 0);// 开药数量
				String tUnitCode = parmBase.getValue("MEDI_UNIT", 0);// 开药单位
				String tFreqCode = parmBase.getValue("FREQ_CODE", 0);// 频次
				int tTakeDays = parmBase.getInt("TAKE_DAYS", 0);// 天数
				TotQtyTool qty = TotQtyTool.getInstance();
				parm.setData("TAKE_DAYS", tTakeDays);
				parm.setData("MEDI_QTY", tMediQty);
				parm.setData("FREQ_CODE", tFreqCode);
				parm.setData("MEDI_UNIT", tUnitCode);
				parm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
				TParm qtyParm = qty.getTotQty(parm);
				order.setItem(row, "DISPENSE_QTY", qtyParm.getDouble("QTY"));
				order.setItem(row, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
				// $$============add by lx 2012/03/03 加入默认总量
				// end=================$$//
			}
			// 判断是否按盒发药
			if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
				order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
						"STOCK_UNIT", 0));
				order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
						"STOCK_UNIT", 0));
				order.itemNow=false;//pangben 2013-6-3  盒装药计算总计金额 总量设置 itemNow=false计算 true 不计算 
				order.setItem(row, "GIVEBOX_FLG", parmBase.getValue("GIVEBOX_FLG",
						0));
				order.itemNow=true;
			} else {
				order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
						"DOSAGE_UNIT", 0));
				order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
						"DOSAGE_UNIT", 0));
			}
		}
		double ownAmt = ODOTool.getInstance().roundAmt(order.getItemDouble(row, "OWN_PRICE")
				* order.getItemDouble(row, "DOSAGE_QTY"));
		String orderCode = parm.getValue("ORDER_CODE");
		// ===============begin===//
		// lzk 2010.6.23 合并 BIL.chargeTotCTZ和BIL.getOwnRate
		double d[] = BILStrike.getInstance().chargeC(odoMainPat.ctz[0], odoMainPat.ctz[1], odoMainPat.ctz[2],
				orderCode, parm.getValue("CHARGE_HOSP_CODE"), odoMainControl.serviceLevel);
		double arAmt = ODOTool.getInstance().roundAmt(d[0] * order.getItemDouble(row, "DOSAGE_QTY"));
		order.setItem(row, "DISCOUNT_RATE", d[1]);
		// ===============   chenxi modify  添加开立时间
//		if (parmBase != null && parmBase.getCount("FREQ_CODE") > 0){
			order.setItem(row, "ORDER_DATE", SystemTool.getInstance().getDate()) ;
//		}
//		else  order.setItem(row, "ORDER_DATE", NULLSTR) ;
		//==============  chenxi modify 添加开立时间
		order.setItem(row, "OWN_AMT", ownAmt);
		order.setItem(row, "AR_AMT", arAmt);
		order.setItem(row, "PAYAMOUNT", ownAmt - arAmt); 
		order.itemNow = false;
	}
	
	/**
	 * 删除一行西成药或管制药品公用
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	public boolean deleteRowMedCtrlComm(OpdOrder order ,int row,TTable table) throws Exception{
		// 是否可以删
		if (!deleteOrder(order, row, "已打票,不可以修改或删除医嘱","此医嘱已经登记,不能删除")) {
			return false;
		} 
		int linktemp = order.getItemInt(row, "LINK_NO");
		String rxNo = order.getItemString(row, "RX_NO");
		if (linktemp > 0
				&& TCM_Transform.getBoolean(order.getItemData(row,
						"LINKMAIN_FLG"))) {
			boolean flg = false;
			for (int i = order.rowCount(); i > -1; i--) {
				if (linktemp == order.getItemInt(i, "LINK_NO")
						) {
					if(StringUtil.isNullString(order.getItemString(i,
							"ORDER_CODE"))){
						flg = true;
					}
					order.deleteRow(i);
				}
			}
			if(flg){
				order.newOrder(rxType, rxNo);
			}
		} else {
			order.deleteRow(row);
		}
		return true;
	}
	
	/**
	 * 删除一行西成药或管制药品公用,for循环执行以后操作
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	private boolean deleteRowMedCtrlComm(TTable table,String rxNo,String rxType,OpdOrder order) throws Exception{
		if (table.getRowCount() - 1 < 0) {
			order.newOrder(rxType, rxNo);
			return false;
		}
		if (!StringUtil.isNullString(TCM_Transform.getString(table.getItemData(
				table.getRowCount() - 1, "ORDER_DESC")))) {
			order.newOrder(rxType, rxNo);
		}
		table.setDSValue();
		Map inscolor = OdoUtil.getInsColor(odoMainPat.ctz, odoMainControl.odo.getOpdOrder(),
				odoMainControl.odoMainTjIns.whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odoMainControl.odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
		return true;
	}
	
	/**
	 * 删除指定表格的一行数据
	 */
	public void deleteRow() throws Exception{
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		int deleteRow = table.getSelectedRow();
		int colIndex = -1;
		try {
			colIndex = table.getSelectedColumn();
		} catch (Exception e) {
			// TODO: handle exception
			colIndex = -1;
		}
		table.acceptText();
		table.setDSValue();
        if(deleteExe(table, deleteRow)){//=========pangben 2013-4-24 删除主诉现病逝、既往史、家族史
        	return ;
        }
		String tag = NULLSTR;//状态选择用来计算金额
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		boolean deleteRowFlg=false;//校验是否删除操作
		String billFlg = NULLSTR;//操作暂存还是收费
		String rxNo = NULLSTR;
		String rxType = NULLSTR;
		boolean canSave = true;
		if(ODORxChnMed.TABLE_CHN.equalsIgnoreCase(tableName)){//中药
			if (deleteRow < 0) {
				return;
			}
			billFlg=order.getItemData(deleteRow, "BILL_FLG").toString();
			table.setSelectedColumn(colIndex);
			if(!odoRxChn.deleteRow(order, deleteRow, table))
				return;
			//delete by huangtt 20141027
//			if("Y".equals(billFlg))
//				odoMainControl.odoMainEkt.onFee();// 执行删除医嘱
//			else
//				onTempSave(null);
			
			return;		
		}
		for (int i = order.rowCount()-1; i>=0; i--) {
			if(null==order.getItemData(i, "ORDER_CODE"))
				continue;
			String orderCode = order.getItemData(i, "ORDER_CODE").toString();//医嘱码
			if(StringUtil.isNullString(orderCode))//校验是否空行
				continue;
			if(order.getItemData(i, "FLG").equals("Y")){//勾选状态
				deleteRowFlg=true;
				if(billFlg.length()<=0||billFlg.equals("N"))//判断操作收费还是暂存
					billFlg=order.getItemData(i, "BILL_FLG").toString();
				if (ODORxExa.TABLE_EXA.equalsIgnoreCase(tableName)) {// 检验检查表
					try {
						if(!odoRxExa.deleteRow(order, i, table)){
							return;
						}
					} catch (Exception e) {
						// TODO: handle exception
						odoMainControl.callFunction("UI|save|setEnabled", false);
						odoMainControl.callFunction("UI|tempsave|setEnabled", false);
						odoMainControl.messageBox("删除异常,请重新选择该病患");
						canSave = false;
					}
					tag = ODORxExa.AMT_TAG;
				}
				if (ODORxOp.TABLE_OP.equalsIgnoreCase(tableName)) {// 诊疗项目表
					try {
						if (!odoRxOp.deleteRow(order, i, table)) {
							return;
						}
					} catch (Exception e) {
						// TODO: handle exception
						odoMainControl.callFunction("UI|save|setEnabled", false);
						odoMainControl.callFunction("UI|tempsave|setEnabled", false);
						odoMainControl.messageBox("删除异常,请重新选择该病患");
						canSave = false;
					}
					tag = ODORxOp.AMT_TAG;
				}
				if (ODORxMed.TABLE_MED.equalsIgnoreCase(tableName)) {//西成药
					if (!odoRxMed.deleteRow(order, i, table)) {
						return;
					}
					rxNo = odoMainControl.getValueString("MED_RX");
					tag = ODORxMed.AMT_TAG;
					rxType = ODORxMed.MED;
				}
				if (ODORxCtrl.TABLE_CTRL.equalsIgnoreCase(tableName)) {//管制药品
					if (!odoRxCtrl.deleteRow(order, i, table)) {
						return;
					}
					rxNo = odoMainControl.getValueString("CTRL_RX");
					tag = ODORxCtrl.AMT_TAG;
					rxType = ODORxCtrl.CTRL;
				}
			}
		}
		if (!deleteRowFlg) {//没有操作的数据
			table.acceptText();
			table.setItem(0, 0, NULLSTR);
			table.getTable().grabFocus();
//			System.out.println(order.getFilter());
			table.setSelectedRow(order.rowCount() - 1);
			table.setSelectedColumn(0);
			odoMainControl.messageBox("请选择要删除的医嘱");
			return;
		}
		if(rxType.equals(ODORxMed.MED)||rxType.equals(ODORxCtrl.CTRL)){//删除一行西成药或管制药品公用,for循环执行以后操作
			if (!deleteRowMedCtrlComm(table, rxNo, rxType, order)) {
				return ;
			}
		}
		this.calculateCash(tableName, tag);
		if("Y".equals(billFlg)){
			odoMainControl.odoMainEkt.onFee();// 执行删除医嘱
		}else{
			if(canSave){
				onTempSave(null,0);
			}
		}
		
		ODOTool.getInstance().updateMemPackageByDelete(odoMainControl.caseNo, odoMainControl.odo.getMrNo());
		if(rxType.equals(ODORxMed.MED)){//如果是西药，删除医嘱后要弹出打印界面
			odoMainOther.onCaseSheet(ODORxMed.MED);
		}
	}
	
	/**
	 * 集合医嘱过滤细项
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm tableShow(TParm parm) throws Exception{
		// 医嘱代码
		String orderCode = NULLSTR;
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		// 医嘱数量
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 删除按钮显示
		// 需要删除的细项列表
		int[] removeRow = new int[count < 0 ? 0 : count]; // =====pangben modify
		// 20110801
		int removeRowCount = 0;
		// 循环医嘱
		for (int i = 0; i < count; i++) {
			Order order = (Order) parm.getData("OBJECT", i);
			// 如果不是集合医嘱主项
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			// 医嘱代码
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// 组
			groupNo = order.getOrderSetGroupNo();
			// 如果是主项循环所有医嘱清理细项
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) parm.getData("OBJECT", j);
				// 如果是这个主项的细项
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// 计算费用
					fee += orderNew.getArAmt();
					// 保存要删除的行
					removeRow[removeRowCount] = j;
					// 自加
					removeRowCount++;
				}
			}
			// 细项费用绑定主项
			parm.setData("AR_AMT", i, fee);
		}
		// 删除集合医嘱细项=====pangben modify 20110801 不用去医生站就诊直接可以开立医嘱计费
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// 调用table赋值方法
		return parm;
	}
	
	/**
	 * 删除行数据
	 * 
	 * @param row
	 */
	private void deleteorderAuto(int row) throws Exception {
		TTabbedPane tabPanel = (TTabbedPane) odoMainControl
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		case ODORxExa.TABBEDPANE_INDEX:// 检验检查
			odoRxExa.deleteorderAuto(row);
		case ODORxOp.TABBEDPANE_INDEX:// 处置
			odoRxOp.deleteorderAuto(row);
		case ODORxMed.TABBEDPANE_INDEX:// 西药
			odoRxMed.deleteorderAuto(row);
			break;
		case ODORxChnMed.TABBEDPANE_INDEX:// 中药
			odoRxChn.deleteorderAuto(row);
			break;
		case ODORxCtrl.TABBEDPANE_INDEX:// 管制药
			odoRxCtrl.deleteorderAuto(row);
			break;
		default:
			break;
		}
		return;
	}
	
	/**
	 * 常用医嘱，组套插入医嘱
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 * @param orderCat1Type
	 *            String
	 * @param orderParm
	 *            TParm
	 * @param rxTag
	 *            String
	 */
	public void insertOrder(String tableName, String tag,
			String orderCat1Type, TParm orderParm, String rxTag) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String rxType = order.getItemString(0, "RX_TYPE");
		String rxNo = order.getItemString(0, "RX_NO");
		insertOrderData(order, orderParm, orderCat1Type, rxTag);		
		if (ODORxChnMed.TABLE_CHN.equalsIgnoreCase(tableName)) {
			odoRxChn.initTable(rxNo);
			return;
		}
		int row = -1;
		if (order.rowCount() >= 1
				&& !StringUtil.isNullString(order.getItemString(order
						.rowCount() - 1, "ORDER_CODE"))) {
			row = order.newOrder(rxType, rxNo);
		} else if (order.rowCount() <= 0) {
			row = order.newOrder(rxType, rxNo);
		} else {
			row = order.rowCount() - 1;
		}
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		int column = table.getColumnIndex("ORDER_DESC_SPECIFICATION");
		table.setSelectedColumn(column);
		order.itemNow = false;
		this.calculateCash(tableName, tag);
		
	}
	
	
	/**
	 * 循环插入数据
	 * 
	 * @param order
	 *            OpdOrder
	 * @param orderParm
	 *            TParm
	 * @param orderCat1Type
	 *            String
	 * @param rxTag
	 *            String
	 */
	public void insertOrderData(OpdOrder order, TParm orderParm,
			String orderCat1Type, String rxTag) throws Exception {
		int count = orderParm.getCount("ORDER_CODE");
		String rxNo = order.getItemString(0, "RX_NO");
		String rxType = order.getItemString(0, "RX_TYPE");
		this.rxType = rxType;
		String deptCode = NULLSTR;
		if (ODORxMed.MED.equalsIgnoreCase(rxType)) {
			deptCode = odoMainControl.getValueString("MED_RBORDER_DEPT_CODE");
		} else if (ODORxCtrl.CTRL.equalsIgnoreCase(rxType)) {
			deptCode = odoMainControl.getValueString("CTRL_RBORDER_DEPT_CODE");
		} else if (ODORxChnMed.CHN.equalsIgnoreCase(rxType)) {
			deptCode = odoMainControl.getValueString("CHN_EXEC_DEPT_CODE");
		} else if (ODORxOp.OP.equalsIgnoreCase(rxType)) {
			deptCode = odoMainControl.getValueString("OP_EXEC_DEPT");
		}
		int row = order.rowCount() - 1;
		if (ODORxExa.EXA.equalsIgnoreCase(rxType)) {
			return;
		}
		TParm price;
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				row = order.newOrder(rxType, rxNo);
			}
			order.setActive(row, true);
			order.itemNow = true;
			if (order.isSameOrder(orderParm.getValue("ORDER_CODE", i))) {
				if (odoMainControl.messageBox(
								"提示信息/Tip",
								"该医嘱已经开立，是否继续？\r\n/This order exist,Do you give it again?",
								0) == 1) {
					continue;
				}
			}
			String orderCode = orderParm.getValue("ORDER_CODE", i);
			TParm orderRowParm = orderParm.getRow(i);
			if ("PHA_W".equalsIgnoreCase(orderCat1Type)) {
				orderRowParm.setData("CAT1_TYPE", "PHA");
			} else if (orderCat1Type.contains("TRT")
					|| orderCat1Type.contains("PLN")) {
				orderRowParm.setData("CAT1_TYPE", orderCat1Type);
			}
			orderRowParm.setData("EXEC_DEPT_CODE", deptCode);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			if (phaBase.getCount() != 1) {
				this.initOrder(order, row, orderRowParm, null);
			} else {
				this.initOrder(order, row, orderRowParm, phaBase);
			}
			order.setItem(row, "DR_NOTE", orderParm.getValue("DESCRIPTION", i));
//			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", orderParm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", orderParm.getValue("MEDI_UNIT", i));
			order.setItem(row, "FREQ_CODE", orderParm.getValue("FREQ_CODE", i));
			order.setItem(row, "ROUTE_CODE", orderParm
					.getValue("ROUTE_CODE", i));
			order.setItem(row, "TAKE_DAYS", orderParm.getValue("TAKE_DAYS", i));
//			order.setItem(row, "PRESRT_NO", orderParm.getValue("PRESRT_NO", i));
			price = OdoUtil.getPrice(order.getItemString(row, "ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
//			order.itemNow = false;
			order.setItem(row, "GIVEBOX_FLG", orderParm.getValue("GIVEBOX_FLG",
					i));
			double ownAmt = ODOTool.getInstance().roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = ODOTool.getInstance().roundAmt(BIL.chargeTotCTZ(odoMainPat.ctz[0], odoMainPat.ctz[1], odoMainPat.ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					odoMainControl.serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(odoMainPat.ctz[0], odoMainPat.ctz[1],
					odoMainPat.ctz[2], price.getValue("CHARGE_HOSP_CODE"), orderCode));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			//add by huangtt 20150421 添加PAYAMOUNT  start
//			String[] columns = order.getColumns();
//			List<String> cList = new ArrayList<String>();
//			
//			for (String string : columns) {
//				cList.add(string);
//			}
//			if(!cList.contains("PAYAMOUNT")){
//				cList.add("PAYAMOUNT");
//			}
//			columns = cList.toArray(columns);
//			order.setColumns(columns);
			//add by huangtt 20150421 添加PAYAMOUNT  end
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", orderCat1Type);
		}
	}
	
	/**
	 * 取得模板后的向医生站主界面上插入数据
	 * 
	 * @param result
	 *            TParm
	 */
	public void insertPack(TParm result) throws Exception {
//		 System.out.println("==result=="+result);
		if (result == null)
			return;
		// =======pangben 2012-6-15
		//deleteLisPosc = false;// 检验检查删除管控 HL7退费操作修改注记
		String subjText = odoMainControl.getValueString("SUBJ_TEXT")
				+ result.getValue("SUBJ_TEXT");
		String objText = odoMainControl.getValueString("OBJ_TEXT")
				+ result.getValue("OBJ_TEXT");
		String psyText = odoMainControl.getValueString("PHYSEXAM_REC")
				+ result.getValue("PHYSEXAM_REC");
		// zhangyong20110311

		String proText = odoMainControl.getValueString("PROPOSAL")
		+ result.getValue("PROPOSAL");  //add by huangtt 20150226  添加建议
		
		String filter1 = ((TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA)).getDataStore()
				.getFilter();
		String filter2 = ((TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP)).getDataStore()
				.getFilter();
		String filter3 = ((TTable) odoMainControl.getComponent(ODORxMed.TABLE_MED)).getDataStore()
				.getFilter();
		String filter4 = ((TTable) odoMainControl.getComponent(ODORxChnMed.TABLE_CHN)).getDataStore()
				.getFilter();
		String filter5 = ((TTable) odoMainControl.getComponent(ODORxCtrl.TABLE_CTRL))
				.getDataStore().getFilter();
		if (odoMainOther.word != null) {
			if (subjText != null && !subjText.equals(NULLSTR)) {
				subjText = odoMainOther.word.getCaptureValue("SUB")+subjText; //add by huangtt 20150610
				odoMainOther.word.clearCapture("SUB");
				odoMainOther.word.pasteString(subjText);
			}
			if (objText != null && !objText.equals(NULLSTR)) {
				objText = odoMainOther.word.getCaptureValue("OBJ")+objText; //add by huangtt 20150610
				odoMainOther.word.clearCapture("OBJ");
				odoMainOther.word.pasteString(objText);
			}
			if (psyText != null && !psyText.equals(NULLSTR)) {
//				psyText = odoMainOther.word.getCaptureValue("PHY");  
				psyText = "\r\n"+"         "+psyText;  //add by huangtt 20140928  使用科模板时传回的体征追加到原来的体征上
//				odoMainOther.word.clearCapture("PHY");
				ECapture capture = odoMainOther.word.findCapture("PHY");			
				ECapture endCapture = capture.getEndCapture();
				endCapture.setFocus();
				odoMainOther.word.pasteString(psyText);
			}
			
			if (proText != null && !proText.equals(NULLSTR)) {
				proText = odoMainOther.word.getCaptureValue("PROPOSAL")+proText; //add by huangtt 20150610
				odoMainOther.word.clearCapture("PROPOSAL");
				odoMainOther.word.pasteString(proText);
			}
		}
		// $$=======Modified by lx 2012-06-10 科模板传回时主诉现病史被清空 end======$$//
		// 将模板中的主诉客诉现病史赋值到结构化病例中
		// 取得诊断
		TParm diagResult = ((TParm) result.getData("DIAG"));
		// ============xueyf modify 20120312
		boolean isHasDiagFlg = false;
		int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
		if (rowMainDiag >= 0) {
			isHasDiagFlg = true;
		}
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				odoMainOther.tblDiag.setSelectedRow(odoMainOther.tblDiag.getRowCount() - 1);
				odoMainOther.popDiagReturn(NULLSTR, diagResult.getRow(i));
				Diagrec diagRec = odoMainControl.odo.getDiagrec();
				int row = odoMainOther.tblDiag.getSelectedRow() - 1;
				// ============xueyf modify 20120312 
				String MAIN_DIAG_FLG = diagResult.getData("MAIN_DIAG_FLG", i)
						.toString();
				if (MAIN_DIAG_FLG.equals("Y") && isHasDiagFlg) {
					odoMainControl.messageBox("已有主诊断!\n如需要，请您手动选择主诊断。");
					MAIN_DIAG_FLG = "N";
				}
				diagRec.setItem(row, "MAIN_DIAG_FLG", MAIN_DIAG_FLG);
				odoMainOther.tblDiag.setDSValue();
			}
		}
		// 检验检查
		TParm exaResult = ((TParm) result.getData("EXA"));
		
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0 ) {
			tableName = ODORxExa.TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				odoRxExa.insertPack(exaResult.getRow(i), true);
			}
		}
		// // 处置
		TParm opResult = ((TParm) result.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxOp.TABLE_OP;
			// orderCat1Type = "TRT";
			// tag = "OP_AMT";
			odoRxOp.insertPack(opResult, true);
		}
		// 药品
		TParm orderResult = ((TParm) result.getData("ORDER"));
		
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxMed.TABLE_MED;
			// orderCat1Type = "PHA_W";
			// tag = "MED_AMT";
			odoRxMed.insertPack(orderResult,false);
		}
		// 管制药品
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxCtrl.TABLE_CTRL;
			// orderCat1Type = "PHA_W";
			// tag = "CTRL_AMT";
			odoRxCtrl.insertPack(orderResult, true);
		}
		// 中药
		// System.out.println("chn======"+result.getData("CHN"));
		Map chnMap = (Map) result.getData("CHN");
		if (chnMap == null || chnMap.size() <= 0) {
			return;
		}
		Iterator it = chnMap.values().iterator();
		while (it.hasNext()) {
			tableName = ODORxChnMed.TABLE_CHN;
			// orderCat1Type = "PHA_G";
			orderResult = (TParm) it.next();
//			System.out.println("orderResult(TParm) it.next()--->"+orderResult);
			odoRxChn.insertPack(orderResult, true);
				
		}
		
		((TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA)).setFilter(filter1);
		((TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA)).filter();
		((TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA)).setDSValue();
		((TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP)).setFilter(filter2);
		((TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP)).filter();
		((TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP)).setDSValue();
		((TTable) odoMainControl.getComponent(ODORxMed.TABLE_MED)).setFilter(filter3);
		((TTable) odoMainControl.getComponent(ODORxMed.TABLE_MED)).filter();
		((TTable) odoMainControl.getComponent(ODORxMed.TABLE_MED)).setDSValue();
		((TTable) odoMainControl.getComponent(ODORxCtrl.TABLE_CTRL)).setFilter(filter5);
		((TTable) odoMainControl.getComponent(ODORxCtrl.TABLE_CTRL)).filter();
		((TTable) odoMainControl.getComponent(ODORxCtrl.TABLE_CTRL)).setDSValue();
//		((TTable) odoMainControl.getComponent(ODORxChnMed.TABLE_CHN)).setFilter(filter4);
//		((TTable) odoMainControl.getComponent(ODORxChnMed.TABLE_CHN)).filter();
//		((TTable) odoMainControl.getComponent(ODORxChnMed.TABLE_CHN)).setDSValue();
	}
	
	
	/**
	 * 保存前使每个TABLE都没有编辑状态
	 */
	public void acceptOpdOderForSave() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(ODORxChnMed.TABLE_CHN);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(ODORxCtrl.TABLE_CTRL);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(ODORxMed.TABLE_MED);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(ODORxOp.TABLE_OP);
		table.acceptText();
	}
	
	/**
	 * 初始化空行
	 * @param tableName
	 */
	private void getTableInit(String tableName,String [] tableNames) throws Exception{
		for (int i = 0; i < tableNames.length; i++) {
			if (tableName.equals(tableNames[i])) {
				continue;
			}
			this.setTableInit(tableNames[i], false);
		}
	}
	
	/**
	 * 保存
	 * @throws Exception
	 */
	public void onSave(TParm exaParm) throws Exception{
		TParm preDateParm = new TParm();
		if ("5".equals(rxType)) {
			if(exaParm.getCount()>0){//存在修改的检验检查处方签
				preDateParm = ODOTool.getInstance().getPreDateByRxNo(exaParm);
			}
//		if(!preOrder()){//预开检查校验yanjing  20140101
			if (preOrder().equals("NO")) {// 预开检查校验yanjing 20140101
				odoMainControl.messageBox("同一张处方签的预执行时间必须相同。");
				return;
			} else if (preOrder().equals("OVER")) {
				odoMainControl.messageBox("预开时间不可小于当前时间。");
				return;
			}
		}
		//========修改重复发送消息功能
		TParm sendParm=odoMainControl.odo.getOpdOrder().getSendParam(odoMainControl.odoMainEkt.ektOrderParmone);//====pangben 2014-3-19
		// ====zhangp 20131202
		if (odoMainControl.odoMainEkt.preDebtFlg) {
			TParm ayhParm = EKTpreDebtTool.getInstance().checkMasterForOdo(
					odoMainControl.odo);
			if (ayhParm.getErrCode() < 0) {
				if (odoMainControl.messageBox("是否继续保存", ayhParm.getErrText()
						+ ",继续保存?", 0) != 0) {
					return;
				}
			}
		}
		boolean isChanged = false;
		try {
			if (!odoMainControl.odo.onSave()) {
				//add by huangtt 20150215 start
				if(odoMainReg.canSave()){
					odoMainControl.messageBox_(ODOMainReg.MEGOVERTIME);
					return;
				}
				//add by huangtt 20150215 end
				odoMainControl.messageBox("E0005");
				odoMainControl.messageBox_(odoMainControl.odo.getErrText());
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			odoMainControl.messageBox(e.getMessage());
			isChanged = true;
		}
		if(ODORxExa.EXA.equals(rxType)){
//			TParm caseNoParm = new TParm();
			//查询预开时间是否全部修改
			if(preDateParm.getCount()>0){
				ODOTool.getInstance().deleteRegByMrnoAndPreDate(preDateParm, odoMainControl.odo.getMrNo());
			}
		}
		odoMainControl.odoMainSpc.onSendInw(sendParm,false);
		if(isChanged){
			odoMainReg.onTablePatDoubleClick();
		}
	}
	
	/**
	 * 暂存操作
	 * 
	 * @param ektOrderParm
	 */
	public void onTempSave(TParm ektOrderParm,int isKeyIn) throws Exception {
//		if(odoMainControl.odoMainEkt.isReadedCard()){
//			return;
//		}
		
		
		if (!canSave()) {
			odoMainControl.messageBox_(MEGOVERTIME);
			return;
		}
		odoMainControl.acceptForSave();
		
		if(odoMainReg.canSave()){
			odoMainControl.messageBox_(ODOMainReg.MEGOVERTIME);
			return;
		}
		
		if(odoMainOther.onSaveSubjrec())
			return;
		
		if(odoMainOther.diagCanSave()){
			if(isKeyIn == 0){
				odoMainReg.onTablePatDoubleClick();
			}
			
			return;
		}
		
		// 获得此次医疗卡操作所有需要执行的医嘱
		// 需要操作的医嘱 ：删除 、添加、 修改
		TParm ektSumExeParm = odoMainControl.odo.getOpdOrder().getEktParam(ektOrderParm);
		//========修改重复发送消息功能
		TParm sendParm=odoMainControl.odo.getOpdOrder().getSendParam(ektOrderParm);//========pangben 2014-3-19
		if(!getTempSaveParm())
			return;
		setTempSaveOdo();
		odoMainReg.saveRegInfo();
		// 查看此就诊病患是否是医疗卡操作
		// 添加医嘱 收费
		//====zhangp 20131202
		if(odoMainControl.odoMainEkt.preDebtFlg){
			TParm ayhParm = EKTpreDebtTool.getInstance().checkMasterForOdo(odoMainControl.odo);
			if(ayhParm.getErrCode()<0){
				if(odoMainControl.messageBox("是否继续保存", ayhParm.getErrText()+",继续保存?", 0) != 0){
					return;
				}
			}
		}else{
			if (!odoMainControl.odo.isModified()) {
				odoMainControl.pay.onSave(ektOrderParm, ektSumExeParm); // 参数作用：医疗卡操作失败回冲金额
				return;
			}
		}
		if(!checkSave()){
			return;
		}
		if(isKeyIn == 1){
			odoRxMed.onSortRx(false);
		}
		// //拿到申请单结果集
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
		TParm ctrlParm = new TParm();
		TParm chnParm = new TParm();// add liling 中药处方 2014、7，18
		TParm orderParm = new TParm();
		if (odoMainControl.odo.getOpdOrder().isModified()) {
			// add caoy 西药处方 2014、7，2
			orderParm = odoMainControl.odo.getOpdOrder().getModifiedOrderRx();
			 chnParm = odoMainControl.odo.getOpdOrder().getModifiedChnRx();//add liling 中药处方 2014、7，18
			exaParm = odoMainControl.odo.getOpdOrder().getModifiedExaRx();
			opParm = odoMainControl.odo.getOpdOrder().getModifiedOpRx();
			ctrlParm = odoMainControl.odo.getOpdOrder().getModifiedCtrlRx();//管制药品
		}
		TParm ektOrderParmone = new TParm();
		if (null == ektOrderParm) {//点击暂存按钮ektOrderParm 入参为null
			TParm parmOne = new TParm();
			parmOne.setData("CASE_NO", odoMainReg.reg.caseNo());
			// 获得此次操作医疗卡所有的医嘱 在执行删除所有医嘱时使用
			ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(parmOne);
			if (ektOrderParmone.getErrCode() < 0) {
				return;
			}
		}
		//=======yanjing 预开检查校验 start
		TParm preDateParm = new TParm();
		if (ODORxExa.EXA.equals(rxType)) {
				//根据修改的处方签号查询预执行时间
				if (odoMainControl.odo.getOpdOrder().isModified()) {
					exaParm = odoMainControl.odo.getOpdOrder().getModifiedExaRx();
				}
				if(exaParm.getCount()>0){//存在修改的检验检查处方签
					preDateParm = ODOTool.getInstance().getPreDateByRxNo(exaParm);
				}
				if (preOrder().equals("NO")) {// 预开检查校验yanjing 20140101
					odoMainControl.messageBox("同一张处方签的预执行时间必须相同。");
				return;
			}else if(preOrder().equals("OVER")){
				odoMainControl.messageBox("预开时间不可小于当前时间。");
				return;
			}
		}
		   //=======yanjing 预开检查校验 end
		//保存数据
//		odoMainControl.odo.getOpdOrder().isModified();
		boolean isChanged = false;
		try {
			if (!odoMainControl.odo.onSave()) {
				
				if(odoMainReg.canSave()){
					odoMainControl.messageBox_(ODOMainReg.MEGOVERTIME);
					return;
				}
				
				odoMainControl.messageBox(odoMainControl.odo.getErrText());
				odoMainControl.messageBox("E0005");
				//isEKTFee = false;// 医疗卡收费使用
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			odoMainControl.messageBox(e.getMessage());
			isChanged = true;
		}
		odoMainControl.odoMainTmplt.onSaveMemPackage();
		
		if(ODORxExa.EXA.equals(rxType)){
			//查询预开时间是否全部修改
			if(preDateParm.getCount()>0){
				ODOTool.getInstance().deleteRegByMrnoAndPreDateForTmpSave(preDateParm, odoMainControl.odo.getMrNo());
			}
		}
		odoMainControl.odoMainSpc.onSendInw(sendParm,false);
		//this.messageBox("P0005");
		odoMainControl.opb = OPB.onQueryByCaseNo(odoMainReg.reg);// 执行医疗卡收费时使用===============pangben
		// 20111010
		//isFee = true;// 执行成功以后可以收费===============pangben 20111010
		// //医疗卡保存操作
		odoMainControl.pay.onTempSave(ektOrderParm, ektOrderParmone, ektSumExeParm);
//		onExeFee();//暫存預開檢查消失   yanjing 注 20140331
		
		// 保存成功提示
		odoMainControl.messageBox("P0001"); 
		
		//modify by huangtt 20141118
		switch (isKeyIn) {
		case 1:
			odoMainOther.onTempSave(orderParm, exaParm, opParm, ctrlParm,
					chnParm);
		case 0:
		}

		odoMainControl.odoMainEkt.unTmpSave();//医疗卡 删除医嘱操作 点选取消按钮 执行撤销删除医嘱操作
		if(isChanged){
			odoMainReg.onTablePatDoubleClick();
		}
	}
			
	
	/**
	 * 暂存数据 
	 * 赋值操作
	 */
	private void setTempSaveOdo() throws Exception{
		String admStatus = odoMainControl.odo.getRegPatAdm().getItemString(0, "ADM_STATUS");
		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}
		String SEE_DR_FLG = (String) odoMainControl.odo.getRegPatAdm().getItemData(0,
				"SEE_DR_FLG");
		if (!StringUtil.isNullString(SEE_DR_FLG) && !SEE_DR_FLG.equals("Y")) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "T");
		}
		// 判断reg主档的SEEN_DR_TIME是否有数据 如果为空记录当前时间
		if (odoMainControl.odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odoMainControl.odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
	}
	
	/**
	 * 计算并设置金额
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 */
	public void calculateCash(String tableName, String tag) throws Exception {
		//TODO 公用
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		double arAmt = 0.0;
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		int count = order.rowCount();
		if (order.rowCount() < 1)
			return;
		if (ODORxExa.TABLE_EXA.equalsIgnoreCase(tableName)) {
			String field = "AR_AMT_MAIN";
			int column = table.getColumnIndex(field);
			int countTable = table.getRowCount();
			for (int i = 0; i < countTable; i++) {
				if (StringUtil.isNullString(order
						.getItemString(i, "ORDER_DESC")))
					continue;
				if (StringTool
						.getBoolean(order.getItemString(i, "RELEASE_FLG"))) {
					continue;
				}
				if (!StringUtil.isNullString(order
						.getItemString(i, "BILL_USER"))) {
					continue;
				}
				arAmt += (Double) table.getValueAt(i, column);
			}
			odoMainControl.setValue(tag, arAmt);
			return;
		}
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC")))
				continue;
			if (StringTool.getBoolean(order.getItemString(i, "RELEASE_FLG"))) {
				continue;
			}
			if (!StringUtil.isNullString(order.getItemString(i, "BILL_USER"))) {
				continue;
			}
			arAmt += order.getItemDouble(i, "AR_AMT");
		}
		odoMainControl.setValue(tag, arAmt);
	}
	
	/**
	 * 校验是否可以删除医嘱 医疗卡删除医嘱可以直接删除,如果已经扣款的医嘱删除 将直接执行扣款操作
	 * 
	 * @param order
	 * @param row
	 * boolean flg true 删除一条医嘱 
	 * @return
	 * =======pangben 2013-1-29 添加参数 校验检验检查医嘱是否已经登记
	 * @throws Exception 
	 */
	public boolean deleteOrder(OpdOrder order, int row, String message,String medAppMessage) throws Exception {
		//斯巴达
		return odoMainControl.pay.deleteOrder(order, row, message, medAppMessage);
	}
	

	/**
	 * 就诊记录插入医嘱
	 * @param result
	 */
	public void onCaseHistoryInsertPack(TParm result) throws Exception{
		// 检验检查
		TParm exaResult = ((TParm) result.getData("EXA"));
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxExa.TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				odoRxExa.insertPack(exaResult.getRow(i), true);
			}
		}
		// 处置
		TParm opResult = ((TParm) result.getData("OP"));
		// System.out.println("opResult="+opResult);
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxOp.TABLE_OP;
			odoRxOp.insertPack(opResult, true);
		}
		// 药品
		TParm orderResult = ((TParm) result.getData("MED"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxMed.TABLE_MED;
			odoRxMed.insertPack(orderResult,true);
		}
		// 管制药品
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = ODORxCtrl.TABLE_CTRL;
			odoRxCtrl.insertPack(orderResult, true);
		}
		Map chnResult = ((Map) result.getData("CHN"));
		if (chnResult != null) {
			Iterator it = chnResult.values().iterator();
			while (it.hasNext()) {
				TParm chn = (TParm) it.next();
				odoRxChn.insertPack(chn, true);
			}
		}
	}
	
	/**
	 * 检测医令管控标识
	 */
	public String getctrflg(String order_code) throws Exception {
		String ctr_flg;
		TParm flg = SYSFeeTool.getInstance().getCtrFlg(order_code);
		ctr_flg = flg.getValue("CRT_FLG", 0);
		return ctr_flg;
	}
	
	/**
	 * 中药计算总金额
	 * 
	 * @param rxNo
	 *            处方号
	 */
	public void calculateChnCash(String rxNo) throws Exception {
		//TODO 公用
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int count = order.rowCount();
		double arAmt = 0.0;
		for (int i = 0; i < count; i++) {
			arAmt += order.getItemDouble(i, "AR_AMT");
		}
		odoMainControl.setValue("CHN_AMT", arAmt);
	}
	
	/**
	 * 校验已发药品，已到检
	 * 
	 * @param order
	 * @param row
	 */
	public boolean checkSendPah(OpdOrder order, int row) throws Exception{
		//===zhangp 物联网修改 start
		if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
				&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
			odoMainControl.messageBox("已到检,不能退费!");
			return false;
		}
		TParm parm =new TParm();
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 校验物联网注记
			if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))
					&& !odoMainControl.opb.checkDrugCanUpdate(order.getRowParm(row), row, parm, true)) {
				odoMainControl.messageBox(parm.getValue("MESSAGE"));
				return false;
			}
		} else {
			return odoMainControl.odoMainSpc.checkSendPah(order, row);
			// ===zhangp 物联网修改 end
		}
		return true;
	}
	
	/**
	 * 检验检查校验
	 * yanjing 20140101
	 */
	private String preOrder() throws Exception{
		//yanj 如果有预开检查，添加校验(以处方签为单位)
		String isPreDateSameFlg = "YES";// 是否存在同一张处方签上时间不同的情况
		TParm parmRegPre = new TParm();
		String[] data = odoMainControl.odo.getOpdOrder().getRx(rxType);// yanjing 20131212
		for (int m = 0; m < data.length; m++) {
			String pre_date = checkDateforRxNo(m);//校验同一张处方签上的预执行时间是否相同
			if (pre_date.equals("no")) {
				isPreDateSameFlg = "NO";
				return isPreDateSameFlg;
			}else{//与当前时间比较
				//校验预开检查的时间不超过今天
				Timestamp rollDay = SystemTool.getInstance().getDate();
				String date = StringTool.getString(rollDay, "yyyy-MM-dd 00:00:00.0");
//				System.out.println("pre_date  pre_date  pre_date is ::"+pre_date);
				if(date.compareTo(pre_date)>0&&!(pre_date.equals(null))&&!("".equals(pre_date))){
					isPreDateSameFlg = "OVER";
					return isPreDateSameFlg;
				}
			}
		}
		
		Map rxCaseData = new HashMap<String, String>();// 存储处方号及对应的就诊号
		OpdOrder preOrder = odoMainControl.odo.getOpdOrder();
		String lastFilter = preOrder.getFilter();
		preOrder.setFilter("");
		preOrder.filter();

		TParm preDateParm = new TParm();
		for (int i = 0; i < preOrder.rowCount(); i++) {
			if (preOrder.getItemString(i, "IS_PRE_ORDER").equals("Y")
					&& preOrder.getItemString(i, "SETMAIN_FLG").equals("Y")) {
				String preRxNo = preOrder.getItemString(i, "RX_NO");
				String sb = preOrder.getItemString(i, "ORDER_CODE");
				String preDate = "nullPreDte";
				if (preOrder.getItemString(i, "PRE_DATE").length() > 0) {
					preDate = preOrder.getItemString(i, "PRE_DATE").substring(
							0, 10);
				}
				preDateParm.addData("RX_NO", preRxNo);
				preDateParm.addData("PRE_DATE", preDate);
			}
		}

		for (int i = 0; i < preDateParm.getCount("RX_NO"); i++) {
			TParm selectRegParm = ODOTool.getInstance().getCaseNoByOldCaseNo(
					preDateParm.getValue("PRE_DATE", i), odoMainControl.caseNo,
					odoMainControl.odo.getMrNo());
			if (selectRegParm.getCount() <= 0) {
				if ("nullPreDte".equals(preDateParm.getValue("PRE_DATE", i))) {
					parmRegPre.setData("ADM_DATE", "");
				} else {
					parmRegPre.setData("ADM_DATE", StringTool.getTimestamp(
							preDateParm.getValue("PRE_DATE", i)
									.substring(0, 10), "yyyy-MM-dd"));
				}
				parmRegPre.setData("MR_NO", odoMainReg.reg.getPat().getMrNo());
				parmRegPre.setData("CTZ1_CODE", odoMainReg.reg.getPat()
						.getCtz1Code());
				parmRegPre.setData("DEPT_CODE", odoMainReg.reg.getDeptCode());
				String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
						"CASE_NO", "CASE_NO");// 获得新的就诊
				parmRegPre.setData("CASE_NO", newCaseNo);
				odoMainReg.onInsertReg(parmRegPre);// 向挂号主表写数据
				if (!rxCaseData.containsKey(preDateParm.getValue("RX_NO", i))) {
					rxCaseData.put(preDateParm.getValue("RX_NO", i), newCaseNo);
				}
			} else {
				if (!rxCaseData.containsKey(preDateParm.getValue("RX_NO", i))) {
					rxCaseData.put(preDateParm.getValue("RX_NO", i),
							selectRegParm.getValue("CASE_NO", 0));
				}
			}
		}
		preOrder.setFilter(lastFilter);
		preOrder.filter();
		odoMainControl.odo.setPreCaseNos(rxCaseData);
		odoMainControl.odo.preChangeCaceNo();
		return isPreDateSameFlg;
	}
	//斯巴达
	/**
	 * 校验同一张处方签上的预执行时间是否相同
	 * yanjing 20140101
	 */
    private String checkDateforRxNo(int m) throws Exception{
    	String[] data = odoMainControl.odo.getOpdOrder().getRx(rxType);//yanjing 20131212 检验检查处方签个数
    	String pre_date = "";
    	OpdOrder preOrder = odoMainControl.odo.getOpdOrder();
    		preOrder.setFilter("RX_NO = '" + data[m].split(",")[0] + "'");
    		preOrder.filter();
    		for(int i = 0;i<preOrder.rowCount();i++){//循环特定处方签的医嘱
    			String orderCode = preOrder.getItemString(i, "ORDER_CODE");
    			if(!("".equals(orderCode)&&orderCode.equals(null))
    					&&preOrder.getItemString(i, "IS_PRE_ORDER").equals("Y")){//预开检查主项
    				pre_date = preOrder.getItemString(i, "PRE_DATE");
    				String preAdmDate1 = "";
    				if(!"".equals(pre_date)){
    					preAdmDate1 = preOrder.getItemString(i, "PRE_DATE").substring(0, 10);
    				}
    				for(int j = 0;j<preOrder.rowCount()-1;j++){
    					String preAdmDate2 = "";
    					if(!"".equals(preOrder.getItemString(j, "PRE_DATE"))){
    					preAdmDate2 = preOrder.getItemString(j, "PRE_DATE").substring(0, 10);
    					}
    					String preIsPreOrder2 = preOrder.getItemString(j, "IS_PRE_ORDER");
    					if((!(preAdmDate1.equals(preAdmDate2))
    							||("".equals(preAdmDate2)&&"".equals(preIsPreOrder2)))){
    						pre_date = "no";
    						return pre_date;
    					}
    				}
    			}
    			
    		}
//		}
    	return pre_date;	
    }
    /**
	 * 右击MENU显示使用说明-duzhw 20131209
	 */
	public void useDrugMenu() throws Exception{

		TTable table = (TTable) odoMainControl.getComponent(tableName);
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String caseNo = odoMainControl.odo.getCaseNo();
		String mrNo = odoMainControl.odo.getMrNo();
		
		String orderCode = order.getItemString(table.getSelectedRow(),
				"ORDER_CODE");
		String seqNo = order.getItemString(table.getSelectedRow(),
				"SEQ_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("SEQ_NO", seqNo);
		TParm reParm = (TParm)odoMainControl.openDialog(
				URLOPDORDERUSEDESC, parm);
		if(reParm.getValue("OPER").equals("CONFIRM")){
			String pasdrCode = reParm.getValue("PASDR_CODE");
			String pasdrNote = reParm.getValue("PASDR_NOTE");
			table.setItem(table.getSelectedRow(), "PASDR_CODE", pasdrCode);
			table.setItem(table.getSelectedRow(), "PASDR_NOTE", pasdrNote);
		}
		
	}
	
    /**
     * 显示医嘱是否预开检查
     * yanjing 20131226
     */
    public void onPreOrder() throws Exception{
    	if(odoMainControl.getValueString("PREORDER").equals("Y")){
    		//设置是否含有预开检查的状态
    		odoMainControl.odo.getOpdOrder().setPreOrder(true);
    	}else if(odoMainControl.getValueString("PREORDER").equals("N")){
    		odoMainControl.odo.getOpdOrder().setPreOrder(false);
    	}
    	if (!odoMainControl.odo.onQuery()) {
    		odoMainControl.messageBox("E0024"); // 初始化参数失败
			//deleteLisPosc = false;
			return;
		}
    	if (!initSetTable(ODORxExa.TABLE_EXA, true))
    		odoMainControl.messageBox("E0026"); // 初始化检验检查失败
    	  initPanel();
    }
    
    /**
     * 检验医嘱
     */
    public boolean checkSave(){
		if (!odoMainControl.odo.checkSave()) {
			// $$ modified by lx 加入提示替代药
			if (odoMainControl.odo.getErrText().indexOf("库存不足") != -1) {
				if (!Operator.getLockFlg().equals("Y")) {
					String orderCode = odoMainControl.odo.getErrText().split(";")[1];
					TParm inParm = new TParm();
					inParm.setData("orderCode", orderCode);
					odoMainControl.openDialog(URLPHAREDRUGMSG, inParm);
				}
			} else {
//				this.messageBox(odo.getErrText());
			}
			odoMainControl.messageBox(odoMainControl.odo.getErrText()+", 保存失败");
			//isEKTFee = false;// 医疗卡收费使用
			return false;
		}else{
			return true;
		}
    }
    
	/**
	 * 校验药品是否可以保存操作，实现实时审配发功能
	 * @return
	 * @throws Exception 
	 */
	public boolean checkPhaIsSave(String rxNo,String type) throws Exception{
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		order.setFilter("RX_NO='" + odoMainControl.getValueString(rxNo) + "'");
		if (!order.filter()) {//当前页签数据
			return false;
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (odoRxMed.check(order, i,type,true))//开立医嘱校验处方签是否可以开立
				return false;
		}
		return true;
	}
	
	/**
	 * 删除一行数据执行操作
	 * ===============pangben 2013-4-24
	 * @return
	 */
	public boolean deleteExe(TTable table, int row)throws Exception{
		if (ODOMainOther.TABLEDIAGNOSIS.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			Diagrec dRec = odoMainControl.odo.getDiagrec();
			if (dRec.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			dRec.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (ODOMainOther.TABLEALLERGY.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			DrugAllergy allergy = odoMainControl.odo.getDrugAllergy();
			if (allergy.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			allergy.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (ODOMainOther.TABLEMEDHISTORY.equalsIgnoreCase(tableName)) {//既往史TABLE删除
			if (row < 0) {
				return true;
			}
			MedHistory md = odoMainControl.odo.getMedHistory();
			if (md.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			md.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(2);
			return true;
		}
		return false;
	}
	
	/**
	 * 删除一行数据操作修改表格当前状态
	 * ===============pangben 2013-4-24
	 * @param table
	 */
	private void deleteExeTemp(TTable table, int type) throws Exception{
		switch (type) {
		case 1:
			table.setDSValue();
			table.acceptText();
			table.getTable().grabFocus();
			table.setSelectedRow(0);
			break;
		case 2:
			table.acceptText();
			table.getTable().grabFocus();
			table.setSelectedRow(0);
			table.setSelectedColumn(1);
			break;
		}
	}
	
	/**
	 * 诊断、既往史、过敏史TABLE行点击事件
	 * 
	 * @param tag
	 *            String
	 */
	public void onTableClick(String tag) throws Exception{
		tableName = tag;
		TTable table = (TTable) odoMainControl.getComponent(tableName);
		// table.acceptText();
		table.getTable().grabFocus();
		if (ODOMainOther.TABLEDIAGNOSIS.equalsIgnoreCase(tag)
				|| ODOMainOther.TABLEALLERGY.equalsIgnoreCase(tag)
				|| ODOMainOther.TABLEMEDHISTORY.equalsIgnoreCase(tag)
				|| ODOMainReg.TABLEPAT.equalsIgnoreCase(tag)) {
			return;
		}
		int row = table.getSelectedRow();
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		// zhangyong20110311
		String filter = ((TTable) odoMainControl.getComponent(tableName)).getFilter();
		if (filter == null || "null".equals(filter)) {
			filter = NULLSTR;
		}
		if (!tag.equals(ODORxChnMed.TABLE_CHN)) {
			order.setFilter(filter);
			order.filter();
		}
		// 状态条
		 //  ===============chenxi  医嘱提示问题 过敏药物的提示
		String orderCode =order.getItemString(row, "ORDER_CODE");
		TParm sqlparm = ODOTool.getInstance().getAllergy(orderCode);
		odoMainControl.callFunction(
				"UI|setSysStatus",
				sqlparm.getValue("ORDER_CODE") + " " + sqlparm.getValue("ORDER_DESC")
						+ " " + sqlparm.getValue("GOODS_DESC") + " "
						+ sqlparm.getValue("DESCRIPTION") + " "
						+ sqlparm.getValue("SPECIFICATION") + " "
						+ sqlparm.getValue("REMARK_1") + " "
						+ sqlparm.getValue("REMARK_2") + " "
						+ sqlparm.getValue("DRUG_NOTES_DR"));
	}
	
	/**
	 * 调用引用表单界面被该界面调用的增加检验检查的方法
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheet(Object obj) throws Exception {
		//=======pangben 2013-1-11 添加校验
		TTabbedPane orderPane = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELORDER);
		if (orderPane.getSelectedIndex() != 0) {
			odoMainControl.messageBox("E0072");
			return false;
		}
		String rxNo = odoMainControl.getValueString(ODORxExa.EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			return false;
		}
		if (!(obj instanceof TParm))
			return false;
		TParm sysFee = (TParm) obj;
		TTable table = (TTable) odoMainControl.getComponent(ODORxExa.TABLE_EXA);
		// ============pangben 2012-7-12 添加管控
		if (!getCheckRxNo()) {
			return false;
		}
		odoRxExa.insertExa(sysFee, table.getRowCount() - 1, 0);
		return true;
	}
}
