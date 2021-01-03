package com.javahis.ui.opb;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import jdo.bil.BIL;
import jdo.bil.BilInvoice;
import jdo.bil.PaymentTool;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJReg;
import jdo.mem.MEMPatRegisterTool;
import jdo.odi.OdiMainTool;
import jdo.opb.OPB;
import jdo.opb.OPBReceipt;
import jdo.opb.OPBReceiptTool;
import jdo.opb.OPBTool;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.sys.DeptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.NJCityInwDriver;
import com.javahis.system.root.RootClientListener;
import com.javahis.ui.odo.ODOMainOpdOrder;
import com.javahis.ui.odo.ODOMainOther;
import com.javahis.ui.odo.ODOMainPat;
import com.javahis.ui.odo.ODOMainReg;
import com.javahis.ui.odo.ODORxMed;
import com.javahis.ui.odo.OdoMainControl;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;
import com.tiis.util.TiMath;

/**
 * <p>
 * Title:门诊收费系统
 * </p>
 * 
 * <p>
 * Description:门诊收费系统
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author fudw
 * @version 1.0
 */
public class OPBChargesMControl extends TControl {
	// 传入数据
	Object paraObject;
	String systemCode = "";
	String onwType="";
	// opb对象
	OPB opb;
	// pat对象
	Pat pat;
	// reg对象
	Reg reg;
	// BIL 对象
	BIL opbbil;
	// 传入界面传参caseNo
	String caseNoPost;
	// 界面唯一caseNo
	String onlyCaseNo;   
	// 传入界面传参mrNo
	String mrNoPost;
	// HL7接口数据
	TParm sendHL7Parm;
	// 当前选中的行
	int selectRow = -1;
	// 是否新增过处方签
	String pFlg = "N";
	// 记录医生开立医嘱的数量
	int drOrderCount = -1;
	// 新建orderlist
	OrderList orderList = null;
	// 收费权限
	boolean bilRight = true;
	// 补充计价权限
	boolean addOrder = true;
	// 全选
	TCheckBox checkBoxChargeAll;
	// 未收费
	TRadioButton checkBoxNotCharge;
	// EKT退费
	TRadioButton ektTCharge;

	// 全部
	TRadioButton checkAll;
	// 给处方签赋值
	TComboBox comboPrescription;
	// table
	TTable table;
	// 调用系统名
	String systemName;
	// 服务等级
	String serviceLevel;
	// 交易号
	String tredeNo;
	// 手术室医嘱套餐
	TParm operationParm;
	// 删除医嘱操作：没有就诊直接开立医嘱后频次修改也可以实现删除医嘱操作
	boolean deleteFun = false;
	// 显示删除按钮
	// ==================pangben modify 20110804 删除按钮操作
	int drOrderCountTemp = 0;
	boolean drOrderCountFalse = false;
	boolean feeShow = false; // 金额调用是否执行显示金额的数据保存
	boolean isbill = false; // 是否记账
	private TParm parmEKT; // 读取医疗卡信息
	private boolean EKTmessage = false; // 医疗卡退费输出消息
	private boolean isEKT = false; // 医疗卡操作
	private TParm tredeParm; // 获得当前扣款是否是医疗卡操作
	private TParm insParm = new TParm(); // 医保出参，U 方法 A 方法参数
	private boolean insFlg = false; // 医保卡读取操作
	private TParm resultBill; // 记账数据
	private TParm regSysParm;//pangben 2013-4-28 挂号有效天数
	private double reduceAmt = 0.00;//减免金额
	// private TParm insMZconfirmParm;// 判断此次就诊是否执行医保操作
	/**
	 * Socket传送门诊药房工具
	 */
	private SocketLink client1;
	private String phaRxNo;//===pangben 2013-5-17 药品审核界面添加跑马灯处方签数据 

	private TButton charge;

	private PaymentTool paymentTool;
	private TParm oldOpdOrderParm; //未收费的全部医嘱  huangtt 2141126
	private String MESSAGE = "该病人医嘱已变，请重新查询！";
	private String insureInfo;
	private static final String URLOPDCASEHISTORY = "%ROOT%\\config\\opd\\OPDCaseHistory.x";
	public OdoMainControl odoMainControl;
	public ODOMainOther odoMainOther;
	public ODOMainPat odoMainPat;
	public ODOMainOpdOrder odoMainOpdOrder;
	public ODOMainReg odoMainReg;
	TParm reduceResult = new TParm();
	private DecimalFormat df = new DecimalFormat("##########0.00");

	
	/**
	 * 初始化界面
	 */
	
	public void onInit() {
		// this.messageBox("权限"+this.getPopedem("NOBILL"));
		super.onInit();
		charge = (TButton) getComponent("CHARGE");
		regSysParm=REGTool.getInstance().getRegParm();//pangben 2013-4-28 挂号有效天数
		paraObject = null;
		paraObject = this.getParameter();
		if (paraObject != null && paraObject.toString().length() > 0) {
			// System.out.println("this.getParameter()+"+this.getParameter());
			TParm paraParm = (TParm) this.getParameter();
			if (paraParm != null && paraParm.getData("SYSTEM") != null) {
				systemCode = paraParm.getValue("SYSTEM");
			}
			if (paraParm != null && paraParm.getData("ONW_TYPE") != null) {
				onwType = paraParm.getValue("ONW_TYPE");//=====pangben 2013-5-15 门急诊护士站解锁使用，监听不同的界面
			}			
		}
		table = (TTable) getComponent("TABLE");
		comboPrescription = (TComboBox) getComponent("PRESCRIPTION");
		checkBoxNotCharge = (TRadioButton) getComponent("NOTCHARGE");
		ektTCharge = (TRadioButton) getComponent("EKT_R");
		checkAll = (TRadioButton) getComponent("ALL");
		//caowl 20140211 start
		if (!systemCode.equals("") && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			checkAll.setValue("N");
		} else {		
			checkAll.setValue("Y");
		}
		//====添加减免审批的权限  yanjing 20141204  start
		if(this.getPopedem("REDUCE")){
			this.callFunction("UI|reduceCheck|setEnabled", true);
		}else{
			this.callFunction("UI|reduceCheck|setEnabled", false);
		}
		//====添加减免审批的权限  yanjing 20141204  start
		//caowl 20140211 end
		checkBoxChargeAll = (TCheckBox) getComponent("CHARGEALL");
		// table1的侦听事件
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// table1值改变事件
		this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		// table专用的监听
		callFunction("UI|TABLE|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// 账单table专用的监听
		table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onChangeTableComponent");
		//addListener(table);

		TPanel p = (TPanel) getComponent("tPanel_1");
		try {
			paymentTool = new PaymentTool(p, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		callFunction("UI|REASSURE_FLG|setEnabled", false);

		onClear();
		// 初始化界面上的数据
		iniTextValue();
		// 初始化权限
		// initPopedem();
		// BilInvoice bilInvoice = new BilInvoice();
		if (systemCode != "" && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			// ===zhangp 20120306 modify start
			this.callFunction("UI|EKT_R|setEnabled", false);
			table.setLockCellColumn(0, true);
			// ===zhangp 20120306 modify end
			return;
		}
		// 初始化票据
		// initBilInvoice(bilInvoice.initBilInvoice("OPB"));

		onGatherChange(1);

	}
	/**
	 * 就诊记录
	 */
	public void onCaseHistory() throws Exception{

		//---
		

		Object obj = this.openDialog(URLOPDCASEHISTORY,this.getValue("MR_NO"));
		
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//添加物联网校验是否当前处方签已经审核
			if (!odoMainControl.odoMainSpc.checkSpcPha(odoMainControl.odo.getOpdOrder())) {
				return;
			}
		}else{//=======pangben 2014-1-15
			if (!odoMainOpdOrder.checkPhaIsSave(ODORxMed.MED_RX,"MED")) {
				return;
			}
		}
	}

	/**
	 * 拿到table
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 点击保存添加校验，有未执行的 提示信息
	 * @param chargeTable
	 * @param row
	 * @return
	 * pangben 2014-3-18
	 */
	@SuppressWarnings("unused")
	private boolean onChangeTableOnExe(TTable chargeTable ,int row){
		if (checkBoxNotCharge.isSelected()||checkAll.isSelected()) {//=======全部、未收费单选按钮
			if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y")){
				if (chargeTable.getParmValue().getBoolean("EXEC_FLG", row) || chargeTable.getParmValue().getValue("PHA_CHECK_CODE",row).length()>0) {
				}else{//如果有未执行的医嘱添加提示
					this.messageBox("未执行项目不可以结算");
					chargeTable.getParmValue().setData("CHARGE",row,"N");
					table.setParmValue(chargeTable.getParmValue());
					//table.acceptText();
					for (int i = 0; i < orderList.size(); i++) {
						// 取一条order
						Order order=(Order) orderList.get(i);;
						//						System.out.println("CAT1_TYPE:::::::"+chargeTable.getParmValue().getValue("CAT1_TYPE",row));
						//						System.out.println("SEQ_NO:::::"+chargeTable.getParmValue().getInt("SEQ_NO",row));
						//						System.out.println("CHARGE::::::"+chargeTable.getParmValue().getValue("CHARGE",row));
						if (chargeTable.getParmValue().getValue("CAT1_TYPE",row).equals("PHA")) {
							if(chargeTable.getParmValue().getValue("RX_NO",row).equals(order.getRxNo())
									&&chargeTable.getParmValue().getInt("SEQ_NO",row)==order.getSeqNo()){
								if(chargeTable.getParmValue().getValue("CHARGE",row).equals("N"))
									order.modifyChargeFlg(false);
								else if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y"))
									order.modifyChargeFlg(true);
							}
						}else{
							if(chargeTable.getParmValue().getValue("RX_NO",row).equals(order.getRxNo())
									&&chargeTable.getParmValue().getInt("ORDERSET_GROUP_NO",row)==order.getOrderSetGroupNo()){
								if(chargeTable.getParmValue().getValue("CHARGE",row).equals("N"))
									((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(false);
								else if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y"))
									((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(true);
							}
						}
					}
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * table checkbox监听事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableComponent(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable("TABLE").getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		if ("CHARGE".equals(columnName)) {
			//			if (!onChangeTableOnExe(chargeTable, row)) {//===pangben 2014-3-18
			//				return false;
			//			}
			double fee = getFee();
			if (ektTCharge.isSelected()) {
				callFunction("UI|TOT_AMT|setValue", -fee);

				paymentTool.setAmt(-fee);

				callFunction("UI|AMT|setValue", parmEKT.getDouble("CURRENT_BALANCE")+fee);
			} else {
				callFunction("UI|TOT_AMT|setValue", fee);

				paymentTool.setAmt(fee);

				callFunction("UI|AMT|setValue", parmEKT.getDouble("CURRENT_BALANCE")-fee);
			}
			setFeeReview();
			//======pangben 2013-3-7 修改护士补充计费 添加两条医嘱 第二条为集合医嘱的情况时，
			//将前面的勾选挑掉 ，没有收取第二条医嘱，就会出现 第二条医嘱的细项 在 OPD_ORDER表中状态为收费状态
			if (chargeTable.getParmValue().getValue("SETMAIN_FLG", row).equals("Y")) {
				List list = opb.getPrescriptionList().getOrder();
				for (int i = 0; i < list.size(); i++) {
					// 取一条order
					Order order=(Order)list.get(i);
					if(chargeTable.getParmValue().getValue("RX_NO",row).equals(order.getRxNo())
							&&chargeTable.getParmValue().getInt("ORDERSET_GROUP_NO",row)==order.getOrderSetGroupNo()){
						if(chargeTable.getParmValue().getValue("CHARGE",row).equals("N"))
							((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(false);
						else if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y"))
							((Order)opb.getPrescriptionList().getOrder().get(i)).setChargeFlg(true);
					}
				}
			}
		}
		return true;
	}

	/**
	 * 初始化界面上的数据
	 */
	public void iniTextValue() {
		TParm parm = new TParm();
		// 预设就诊日期
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());
		// 当前医生
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// 当前科室
		callFunction("UI|REALDEPT_CODE|setValue", Operator.getDept());
		callFunction("UI|delete|setEnabled", false);
		Object obj = this.getParameter();
		if (obj == null || obj.toString().length() == 0) {
			return;
		}
		if (obj.toString().length() > 1) {
			parm = (TParm) obj;
			if (parm.getValue("CASE_NO") != null) {
				caseNoPost = parm.getData("CASE_NO").toString();
				mrNoPost = parm.getData("MR_NO").toString();
				// systemName = parm.getData("SYSTEM").toString();
				onQueryByCaseNo();
				if (getValue("BILL_TYPE").equals("E")) {//pangben 2013-9-18护士调用补充计费，保存不提示
					txReadEKT();
				}
			}
		}

	}

	/**
	 * 身份修改
	 * caowl 
	 * */
	public void onCtzModify(){
		//界面唯一case_no
		TParm parm = new TParm();
		parm.setData("CASE_NO",opb.getReg().caseNo());
		if(onlyCaseNo==null || onlyCaseNo.equals("")){
			this.messageBox("请输入病案号");
			return;

		}		
		openDialog("%ROOT%\\config\\opb\\OPBCTZModify.x", parm);
	}
	/**
	 * 减免打印
	 * caowl  20140425
	 * */
	public TParm onReduce(boolean flg,TParm orderParm){
		//界面唯一case_no
		TParm parm = new TParm();
		TParm result = new TParm();
		if(onlyCaseNo==null || onlyCaseNo.equals("")){
			this.messageBox("请输入病案号");
			return result;			
		}	
		parm.setData("CASE_NO",onlyCaseNo);
		parm.setData("MR_NO",this.getValue("MR_NO"));
		parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
		parm.setData("SEX_CODE",this.getValue("SEX_CODE"));
		parm.setData("CTZ1_CODE",this.getValue("CTZ1_CODE"));
		parm.setData("RECEIPT_NO",this.getValue("UPDATE_NO"));
		parm.setData("ADM_DATE",this.getValue("STARTTIME"));
		parm.setData("OPB_FLG",flg);//FLG TRUE:医疗卡打票   FALSE 现金打票
		for(int i = 0;i<orderParm.getCount();i++){
			parm.setData("CHARGE", i, orderParm.getValue("CHARGE", i)) ;
			parm.setData("ORDER_CODE", i, orderParm.getValue("ORDER_CODE", i)) ;
			parm.setData("ORDER_DESC", i, orderParm.getValue("ORDER_DESC", i)) ;
			parm.setData("SPECIFICATION", i, orderParm.getValue("SPECIFICATION", i)) ;
			parm.setData("OWN_PRICE", i, orderParm.getValue("OWN_PRICE", i)) ;
			parm.setData("AR_AMT", i, orderParm.getValue("AR_AMT", i)) ;
			parm.setData("DOSAGE_QTY", i, orderParm.getValue("DOSAGE_QTY", i)) ;
			parm.setData("DOSAGE_UNIT", i, orderParm.getValue("DOSAGE_UNIT", i)) ;
			parm.setData("REXP_CODE", i, orderParm.getValue("REXP_CODE", i)) ;
			parm.setData("SETMAIN_FLG", i, orderParm.getValue("SETMAIN_FLG", i)) ;
		}
		//	   parm.setData("TableParm",orderParm.getData());
		//parm.setData("ReceiptParm",oneReceiptParm.getData());
		parm.setData("TYPE_PARM",null!=paymentTool.table.getParmValue().getData()?paymentTool.table.getParmValue().getData():null);
		result = (TParm) openDialog("%ROOT%\\config\\bil\\BILOPDReduce.x", parm);	
		//		if (flg) {
		//			result = (TParm) openDialog("%ROOT%\\config\\bil\\BILOPDReduce.x", parm);	
		//		}else{
		//			parm.setData("ORDER_PARM",orderParm.getData());//医嘱集合
		//			result = (TParm) openDialog("%ROOT%\\config\\bil\\BILOPDReduceCash.x", parm);
		//		}
		return result;
	}
	/**
	 * 套餐查询
	 * caowl
	 * */
	public void onQueryPack(){
		TParm parm = new TParm();
		parm.setData("MR_NO",this.getValue("MR_NO"));
		if(this.getValue("MR_NO")==null || this.getValue("MR_NO").equals("")){
			this.messageBox("请输入病案号");
			return;			
		}
		openDialog(
				"%ROOT%\\config\\mem\\MEMPackageSalesInfo.x", parm);
	}
	/**
	 * 初始化权限
	 */
	public void initPopedem() {
		// 收费权限
		if (!getPopedem("BILCharge")) {
			// 收费权限为true
			bilRight = false;
			// 全选为未选中
			callFunction("UI|CHARGEALL|setValue", "N");
			// 全选checkbox空
			callFunction("UI|CHARGEALL|setEnabled", false);
			// table第一列收费锁定
			callFunction("UI|TABLE|setLockColumns", "0");
			// 置收费按钮为不可编辑
			callFunction("UI|CHARGE|setEnabled", false);

		}
		// 补充计价权限
		if (!getPopedem("ADDORDER")) {
			// 权限为false
			addOrder = false;
		}
	}

	/**
	 * table 点击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// 如果此医嘱是医生开立的,收费员无权删除和编辑
		callFunction("UI|delete|setEnabled", false);
		// int temp=0;
		if (drOrderCountTemp != 0) {
			drOrderCount = drOrderCountTemp;
		}
		if (table.getSelectedRow() >= drOrderCount || deleteFun) {
			callFunction("UI|delete|setEnabled", true);
		}
		// ===zhangp 20120414 start
		Order order = null;
		if (table.getSelectedRow() > -1) {
			if (table.getParmValue().getData("OBJECT", table.getSelectedRow()) instanceof Order)
				order = (Order) table.getParmValue().getData("OBJECT",
						table.getSelectedRow());
		}
		// RxType::医嘱分类0 OR 7：补充计价1：西成药2：管制药品3：中药饮片4：诊疗项目5：检验检查项目
		if (null != order
				&& (order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			callFunction("UI|delete|setEnabled", true);
		}
		// ===zhangp 20120414 end
	}

	/**
	 * 根据病案号带出所有信息
	 */
	public void onQuery() {
		this.setValue("TAX_FLG", "N");
		//delete by huangtt 20141126
		//		if (pat != null) {
		//			this.unLockPat();
		//		}
		// 设置默认没有新建处方
		pFlg = "N";
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		if(pat.isOpbArreagrage()){
			if(JOptionPane.showConfirmDialog(null, "有未结算项目，是否继续", "是否继续", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
				return;
			}
		}
		//zhangp
		double payOther3 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER3);
		double payOther4 = EKTpreDebtTool.getInstance().getPayOther(pat.getMrNo(), EKTpreDebtTool.PAY_TOHER4);
		setValue("GIFT_CARD", payOther3);
		setValue("GIFT_CARD2", payOther4);
		BigDecimal pay3 = new BigDecimal(payOther3);
		BigDecimal pay4 = new BigDecimal(payOther4);
		BigDecimal ektSum = new BigDecimal(getValueDouble("EKT_CURRENT_BALANCE"));
		BigDecimal nPay = ektSum.subtract(pay3).subtract(pay4).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		setValue("NO_PAY_OTHER_ALL", nPay.doubleValue());

		//delete by huangtt 20141126
		//		// 如果锁病患失败则清空数据
		//		if (!this.lockPat()) {
		//			return;
		//		}
		callFunction("UI|record|setEnabled", true);
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());

		//		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
		//				SystemTool.getInstance().getDate()); // showAge(Timestamp birth,
		//		// Timestamp sysdate);
		String age = DateUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate());//年龄计算修改，modify by huangjw 20150114
		setValue("AGE", age);
		setValue("BIRTH_DATE",pat.getBirthday());
		
		
		setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(pat.getMrNo()));
		
		// 查找就诊记录
		// ==========pangben modify 20110421 start
		String regionCode = Operator.getRegion();
		Timestamp startTime = SystemTool.getInstance().getDate();
		if(!"void".equals(getValueString("STARTTIME"))){
			startTime = (Timestamp) getValue("STARTTIME");
		}
		TParm parm = PatAdmTool.getInstance().selDateByMrNo(pat.getMrNo(),
				startTime,
				startTime, regionCode);
		// ==========pangben modify 20110421 start
		// 查找错误
		if (parm.getCount() < 0) {
			messageBox_("就诊序号选择错误!");
			return;
		}

		// 若挂号信息为0
		if (parm.getCount() == 0) {
			//			this.messageBox("无今日挂号信息!");
			// 就诊序号选择界面
			onRecord();
			return;
		}
		// 如果今天只有一次挂号信息
		if (parm.getCount() == 1) {
			// 初始化reg
			String caseNo = parm.getValue("CASE_NO", 0);
			reg = Reg.onQueryByCaseNo(pat, caseNo);
			// 判断挂号信息
			if (reg == null) {
				return;
			}
			// reg得到的数据放入界面
			afterRegSetValue();
			// 通过reg和caseNo得到pat
			opb = OPB.onQueryByCaseNo(reg);
			serviceLevel = opb.getReg().getServiceLevel();
			this.setValue("SERVICE_LEVEL", serviceLevel);
			this.setValue("REASSURE_FLG", reg.getReassureFlg());
			// onlyCaseNo = "";
			onlyCaseNo = opb.getReg().caseNo();
			//添加就诊号是否报道的校验   20130814 yanjing
			checkArrive();//是否报到校验
			//添加就诊号是否报道的校验   20130814 yanjing  end
			// 给界面上部分地方赋值
			if (opb == null ) {  
				// this.messageBox_(11111111);
				this.messageBox("此病人尚未就诊!");
				return;
			}
			oldOpdOrderParm = getOrder(); //add by huangtt 20141204
			// 初始化opb后数据处理
			afterInitOpb();

			//add by huangtt 20140826 start
			// 拿出table要显示的所有数据
			TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
			TParm tableShow = opb.getOrderParmNotCharge(true, comboPrescription
					.getValue(), this.getValueString("CAT1_TYPE"), true, un_exec.isSelected(),this.getValueString("MEM_TRADE"));
			if(tableShow.getCount()>0){
				this.messageBox("此患者存在套餐内项目");
			}
			//add by huangtt 20140826 end
			if(opb.checkOrderCount()){
				return;
			}
			checkBoxChargeAll.setSelected(true);
			this.onSelectAll();
			this.onAll();
			this.onNotCharge();
			//add by huangtt 20141126
			return;
		}
		onRecord();
		if(opb.checkOrderCount()){
			return;
		}
		//add by huangtt 20140826
		// 拿出table要显示的所有数据
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		TParm tableShow = opb.getOrderParmNotCharge(true, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), true, un_exec.isSelected(),this.getValue("MEM_TRADE").toString());
		if(tableShow.getCount()>0){
			this.messageBox("此患者存在套餐内项目");
		}

		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		mem_package.setSelected(false);
		this.onChangeMemPackageFlg();

		BilInvoice bilInvoice = new BilInvoice();
		if (!systemCode.equals("") && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			//checkAll.setValue("N");//caowl 20140211 
			this.callFunction("UI|ektPrint|setEnabled", false);
		} else {
			//checkAll.setValue("Y");//caowl 20140211
			initBilInvoice(bilInvoice.initBilInvoice("OPB"));
		}
		// ===zhangp 20120724 end
		checkBoxChargeAll.setSelected(true);
		this.onSelectAll();
		this.onAll();
		this.onNotCharge();
		//add by huangtt 20141126


	}

	/**
	 * 创建XML文件 ================================pangben modify 20110806
	 */
	public void createOrderXML() {
		// 1.构造数据
		TParm inparm = new TParm();
		TParm parm = table.getParmValue();
		if (parm.getCount() - 1 <= 0) {
			this.messageBox("没有需要生成的数据");
			return;
		}
		int count = 0;
		for (int i = 0; i < parm.getCount() - 1; i++) {

			inparm.insertData("TBR", i, this.getValue("MR_NO"));
			inparm.insertData("XM", i, this.getValue("PAT_NAME"));
			inparm.insertData("YSM", i, parm.getValue("DR_CODE", i));
			inparm.insertData("BZBM", i, parm.getValue("ORDER_CODE", i));
			inparm.insertData("KSM", i, parm.getValue("DEPT_CODE", i));
			String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"
					+ parm.getValue("DR_CODE", i) + "'";
			TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
			inparm.insertData("YSXM", i, sqlParm.getValue("USER_NAME", 0));
			sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
					+ parm.getValue("DEPT_CODE", i) + "'";
			sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
			inparm.insertData("KSMC", i, sqlParm.getValue("DEPT_CHN_DESC", 0));
			count++;
		}
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "YSM");
		inparm.addData("SYSTEM", "COLUMNS", "BZBM");
		inparm.addData("SYSTEM", "COLUMNS", "KSM");
		inparm.addData("SYSTEM", "COLUMNS", "YSXM");
		inparm.addData("SYSTEM", "COLUMNS", "KSMC");
		inparm.setCount(count);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzgzxx.xml");
		createFeeXML(parm);
	}

	private void createFeeXML(TParm parm) {
		// 1.构造数据
		TParm inparm = new TParm();
		int count = 0;
		for (int i = 0; i < parm.getCount() - 1; i++) {
			inparm.insertData("TBR", i, this.getValue("MR_NO"));
			inparm.insertData("XM", i, this.getValue("PAT_NAME"));
			inparm.insertData("ZBM", i, parm.getValue("ORDER_CODE", i));
			inparm.insertData("SL", i, parm.getValue("DOSAGE_QTY", i));
			inparm.insertData("DJ", i, parm.getValue("OWN_PRICE", i));
			inparm.insertData("YHJ", i, parm.getValue("OWN_PRICE", i));
			if ("PHA".equals(parm.getValue("CAT1_TYPE", i))) {
				inparm.insertData("BZ", i, 0);
			} else {
				inparm.insertData("BZ", i, 1);
			}
			inparm.insertData("YHLB", i, 0);
			count++;
		}
		inparm.addData("SYSTEM", "COLUMNS", "TBR");
		inparm.addData("SYSTEM", "COLUMNS", "XM");
		inparm.addData("SYSTEM", "COLUMNS", "ZBM");
		inparm.addData("SYSTEM", "COLUMNS", "SL");
		inparm.addData("SYSTEM", "COLUMNS", "DJ");
		inparm.addData("SYSTEM", "COLUMNS", "YHJ");
		inparm.addData("SYSTEM", "COLUMNS", "BZ");
		inparm.addData("SYSTEM", "COLUMNS", "YHLB");
		inparm.setCount(count);
		// 2.生成文件
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzcfsj.xml");
		this.messageBox("生成成功");

	}

	/**
	 * 传参界面接参后查询
	 */
	public void onQueryByCaseNo() {
		setValue("MR_NO", mrNoPost);
		//delete by huangtt 20141126
		// 如果当前有被锁的病患
		//		unLockPat();
		// 设置默认没有新建处方
		pFlg = "N";
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		//delete by huangtt 20141126
		//		// 如果锁病患失败则清空数据
		//		if (!this.lockPat()) {
		//			return;
		//		}
		callFunction("UI|record|setEnabled", true);
		// 界面赋值
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		
		setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(pat.getMrNo()));
		
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());
		setValue("AGE", age);
		setValue("BIRTH_DATE",pat.getBirthday());
		// 调用界面传参
		reg = Reg.onQueryByCaseNo(pat, caseNoPost);
		// 判断挂号信息
		if (reg == null) {
			return;
		}
		// reg得到的数据放入界面
		afterRegSetValue();
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		if (opb == null) {
			// this.messageBox_(22222222);
			this.messageBox("此病人尚未就诊!");
			return;
		}
		// 初始化opb后数据处理
		afterInitOpbParameter();
		return;
	}

	/**
	 * reg 初始化成功后放入数据
	 */
	public void afterRegSetValue() {
		// 三身份
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		//-----------modified bywangqing at 2016.11.16 start------------------
		//修改方法：新增代码   新增挂号方式

		//挂号方式
		callFunction("UI|REGMETHOD_CODE|setValue",reg.getRegmethodCode());
		//-----------modified bywangqing at 2016.11.16 end------------------
		// 就诊科室
		callFunction("UI|DEPT_CODE|setValue", reg.getRealdeptCode());
		// 经治医生
		callFunction("UI|DR_CODE|setValue", reg.getRealdrCode());
		//		String deptCode = Operator.getDept();
		String deptCode = Operator.getCostCenter();
		//		TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// 执行科室
		//		if (deptParm.getCount("DEPT_CODE") > 0) {
		callFunction("UI|REALDEPT_CODE|setValue", deptCode);
		//		} else {
		//			callFunction("UI|REALDEPT_CODE|setValue", "");
		//		}
		// 执行医生
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// 预设就诊日期
		this.callFunction("UI|STARTTIME|setValue", reg.getAdmDate());
		
		this.callFunction("UI|REASSURE_FLG|setValue", reg.getReassureFlg());
		
	}

	/**
	 * 初始化opb后的数据处理
	 */
	public void afterInitOpb() {
		String view = checkAll.getValue().toString();
		// 查询此次就诊是否记账操作
		String sql = "SELECT CASE_NO,CONTRACT_CODE FROM BIL_CONTRACT_RECODE WHERE CASE_NO='"
				+ onlyCaseNo + "'";
		resultBill = new TParm(TJDODBTool.getInstance().select(sql));
		if (resultBill.getCount() > 0) {
			setValue("BILL_TYPE", "P");
		}
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
		} else {
			// 调用显示未收费方法
			checkBoxNotCharge.setSelected(true);
			onNotCharge();
		}
		if (ektSave().getErrCode() < 0) { // =========pangb 2011-11-29
			// 获得医疗卡信息查看此次扣款是否是医疗卡操作
			this.messageBox("读取医疗卡信息有误");
			tredeParm = null;
			return;
		}
		if (paraObject != null && systemCode != null
				&& "ONW".equals(systemCode) || this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return;
		}
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return;
		}

		// 医疗卡操作如果没有开帐可以执行收费不可以执行打票,但是不可以执行现金收费
		// =====zhangp 20120302 modify start
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			//			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", false);
			// 初始化票据
			// BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// =====origion code start
				// 初始化票号
				if (!initBilInvoice(opb.getBilInvoice())) {
					// return;
				}
				// =====origion code end
			}
		}
		// =====zhangp 20120302 modify end
		// 显示下一票号

		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
		this.setValue("INSURE_INFO", insureInfo);//保险信息
	}

	/**
	 * 初始化票据
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("无可打印的票据!");
			}
			// this.onClear();
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张票据!");
		}
		callFunction("UI|UPDATE_NO|setValue", bilInvoice.getUpdateNo());
		return true;
	}

	/**
	 * 查询当前操作是否医疗卡扣款
	 * 
	 * @return TParm
	 */
	private TParm ektSave() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("BUSINESS_TYPE", "REG"); // 类型
		parm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
		tredeParm = EKTTool.getInstance().selectTradeNo(parm);
		return tredeParm;
	}

	/**
	 * 判断此次就诊是否执行医保操作
	 */
	public void afterInitOpbParameter() {
		String view = checkAll.getValue().toString();
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
		} else {
			// 调用显示未收费方法
			onNotCharge();
		}
		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
	}

	/**
	 * 就诊记录选择
	 */
	public void onRecord() {
		// 初始化pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("查无此病案号!");
			// 若无此病案号则不能查找挂号信息
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("挂号信息错误!");
			return;
		}
		// reg得到的数据放入界面
		afterRegSetValue();
		// 通过reg和caseNo得到pat
		opb = OPB.onQueryByCaseNo(reg);
		serviceLevel = opb.getReg().getServiceLevel();
		this.setValue("SERVICE_LEVEL", serviceLevel);
		onlyCaseNo = opb.getReg().caseNo();
		checkArrive();//是否报到校验
		if (opb == null) {
			// this.messageBox_(33333333);
			this.messageBox_("此病人尚未就诊!");
			return;
		}
		oldOpdOrderParm = getOrder(); //add by huangtt 20141204
		// 初始化opb后数据处理
		afterInitOpb();
	}

	/**
	 * 检验病人是否报到
	 * yanjing 20131231 
	 */
	private void checkArrive(){
		//添加就诊号是否报道的校验   20130814 yanjing
		String sql = "SELECT ARRIVE_FLG,IS_PRE_ORDER,INSURE_INFO FROM REG_PATADM WHERE CASE_NO = '"+onlyCaseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		insureInfo = result.getValue("INSURE_INFO",0);//保险信息
		//		System.out.println("查询sql  is：："+sql);
		if(result.getValue("ARRIVE_FLG",0).equals("N")&&!(result.getValue("IS_PRE_ORDER",0).equals("Y"))){
			this.messageBox("此病人尚未报到!");
			this.onClear();
			return;
		}
	}
	/**
	 * 检核病人是否有已开医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrderCount() {
		// 检核opb
		if (opb == null) {
			return true;
		}
		if (opb.checkOrderCount()) {
			// this.messageBox_(44444444);
			this.messageBox("此病人尚未就诊!");
			// table.addRow(setParm());
			// return true;//=====pangben modify 20110801
		}
		return false;
	}

	/**
	 * 利用得到的odo给table赋值
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTableValue(TParm parm) {
		// 清空table数据
		table.removeRowAll();
		// 保存医生开的医嘱数量
		drOrderCount = parm.getCount();
		// 执行删除方法：判断第一次记录病患就诊的医嘱
		// ==================pangben modify 20110804 删除按钮显示
		if (!drOrderCountFalse && drOrderCount > 0) {
			drOrderCountTemp = drOrderCount; // 获得医生开立的医嘱个数
			drOrderCountFalse = true; // 可以显示删除按钮
		}
		// //没有开立任何收费项目
		// if (drOrderCount <= 0) {
		// //加一空行
		// if (addOrder){
		// table.setParmValue(setAddParm());
		// callFunction("UI|TABLE|addRow", setParm());
		// }
		// return;
		// }
		// String lockRow="";
		// for(int i=0;i<drOrderCount;i++){
		// lockRow+=""+i+",";
		// }
		// //把数据放入tabl
		//		TParm exeParm=onExeParmCompar(parm);
		TParm exeParm=parm;
		//table.setParmValue(exeParm);
		table.setLockColumns("1");// =========pangben 2012-4-16 添加锁死打票注记
		//zhangp
		for (int i = 0; i < exeParm.getCount("EXEC_FLG"); i++) {
			// PHA_DOSAGE_CODE 改为PHA_CHECK_CODE modify by huangtt 20180823
			if(exeParm.getBoolean("EXEC_FLG", i) || (exeParm.getValue("PHA_CHECK_CODE",i).length()>0 && exeParm.getValue("PHA_RETN_CODE",i).length()==0)){
				table.setRowColor(i, Color.YELLOW);	
			}else{
				table.setRowColor(i, Color.white);	
			}
		}
		table.setParmValue(exeParm);
		// table.setLockRows(lockRow);
		// table.setUI()
		// 放费用
		// 重新计算费用
		double fee = getFee();
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
		// 加一空行
		if (addOrder) {
			callFunction("UI|TABLE|addRow", setParm());
		}
	}
	public void setTableValue_R(TParm parm) {
		// 清空table数据
		table.removeRowAll();
		// 保存医生开的医嘱数量
		drOrderCount = parm.getCount();
		//zhangp
		for (int i = 0; i < parm.getCount("EXEC_FLG"); i++) {
			// PHA_DOSAGE_CODE 改为PHA_CHECK_CODE modify by huangtt 20180823
			if(parm.getBoolean("EXEC_FLG", i) || (parm.getValue("PHA_CHECK_CODE",i).length()>0 && parm.getValue("PHA_RETN_CODE",i).length()==0)){
				table.setRowColor(i, Color.YELLOW);	
			}else{
				table.setRowColor(i, Color.white);	
			}
		}
		// //把数据放入tabl
		table.setParmValue(parm);
		table.setLockColumns("1,2,3,4,5,,6,7,8,9,10,11,12,13,14");
		// 放费用
		// 重新计算费用
		double fee = opb.getFee(!ektTCharge.isSelected());
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
	}

	/**
	 * 新增行添加默认数据
	 * 
	 * @return TParm
	 */
	public TParm setParm() {
		TParm parm = new TParm();
		Order order = new Order();
		parm = order.getParm();
		parm.setData("OBJECT", order);
		return parm;
	}

	/**
	 * 循环删除table的行
	 */
	public void removeTableAllRow() {
		int row = table.getRowCount();
		for (int i = 0; i < row; i++) {
			table.removeRow();
		}
	}

	/**
	 * 新增行添加默认数据
	 * 
	 * @return TParm
	 */
	public TParm setParmData() {
		TParm parm = new TParm();
		parm.addData("CHARGE", "N");
		parm.addData("MEDI_QTY", 0);
		parm.addData("TAKE_DAYS", 0);
		parm.addData("DOSAGE_QTY", 0);
		parm.addData("OWN_PRICE", 0);
		parm.addData("AR_AMT", 0);
		parm.addData("OBJECT", null);
		return parm;
	}

	/**
	 * 全选事件
	 */
	public void onSelectAll() {
		// 检核opb
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// 查看显示方式
		String allR = (String) callFunction("UI|ALL|getValue");
		String notChargeR = (String) callFunction("UI|NOTCHARGE|getValue");
		String ektR = (String) callFunction("UI|EKT_R|getValue");
		// 如果是全部
		if (allR.equals("Y")) {
			onAll();
			return;
		}
		if (notChargeR.equals("Y")) {
			onNotCharge();
			return;
		}
		if (ektR.equals("Y")) {
			onEKTCharge(1);
			return;
		}
		// checkBoxNotCharge.setSelected(true);
		// 显示未收费
		// onNotCharge();
	}
	/**
	 * 保存校验提示信息
	 * ===========pangben 2014-3-18 点击保存添加校验，有未执行的 提示信息
	 */
	private boolean onSelectAllonExe(){
		if (getValue("BILL_TYPE").equals("E")) {
			if (this.getValueString("ALL").equals("Y")) {
				this.messageBox("请不要点选全部");
				return false;
			}
		}
		if (checkBoxNotCharge.isSelected()||checkAll.isSelected()) {
			TParm tableParm=table.getParmValue();
			List<String> chargList = new ArrayList<String>();
			for (int i = 0; i < tableParm.getCount(); i++) {
				//add by huangtt 20150115 start
				if (null==tableParm.getValue("ORDER_CODE",i)||tableParm.getValue("ORDER_CODE",i).length()<=0) {
					continue;
				}
				//add by huangtt 20150115 end
				if (tableParm.getBoolean("EXEC_FLG", i) || (tableParm.getValue("PHA_CHECK_CODE",i).length()>0 && tableParm.getValue("PHA_RETN_CODE",i).length()==0)) {
				}else{//如果有未执行的医嘱添加提示
					chargList.add(tableParm.getValue("CHARGE", i));
					if (tableParm.getBoolean("CHARGE", i)) {  
						if(this.messageBox("提示", "对未执行项目进行结算，是否继续", 2)!=0){
							return false;
						}else{
							chargList.remove(chargList.size() -1);
							break;
						}
					}else{ //add by huangtt 20140826
						if(this.messageBox("提示", "此患者有未执行医嘱，是否继续", 2)!=0){
							return false;
						}else{
							chargList.remove(chargList.size() -1);
							break;
						}
					}	

				}
			}
			for (int i = 0; i < tableParm.getCount(); i++) {
				if (null==tableParm.getValue("ORDER_CODE",i)||tableParm.getValue("ORDER_CODE",i).length()<=0) {
					continue;
				}
				if (tableParm.getBoolean("EXEC_FLG", i) || (tableParm.getValue("PHA_CHECK_CODE",i).length()>0 && tableParm.getValue("PHA_RETN_CODE",i).length()==0)) {
				}else{//如果有未执行的医嘱添加提示
					if(chargList.contains("Y")){
						if (this.messageBox("提示", "此患者有未执行医嘱，是否继续", 2)!=0) {
							return false;
						}else{
							break;
						}
					}else{
						return true;
					}
				}
			}
		}
		if (!systemCode.equals("") && "ONW".equals(systemCode)) {
			TParm tableParm=table.getParmValue();
			for (int i = 0; i < tableParm.getCount(); i++) {
				if (tableParm.getBoolean("CHARGE", i)) {
					this.messageBox("不可结算");
					return false;
				}
			}
		}
		//zhangp
//		if(ektTCharge.isSelected()){
//			TParm tableParm=table.getParmValue();
//			List<String> list = new ArrayList<String>();
//			List<String> chargList = new ArrayList<String>();
//			for (int i = 0; i < tableParm.getCount("CHARGE"); i++) {
//				chargList.add(tableParm.getValue("CHARGE", i));
//				String businessNo = tableParm.getValue("BUSINESS_NO", i);
//				if(businessNo.length() > 0 && !list.contains(businessNo)){
//					list.add(businessNo);
//				}
//			}
//			for (int i = 0; i < list.size(); i++) {
//				String sql =
//						"SELECT TRADE_NO,PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE WHERE TRADE_NO = '" + list.get(i) + "'";
//				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//				if(parm.getDouble("PAY_OTHER3", 0) > 0 || parm.getDouble("PAY_OTHER4", 0) > 0){
//					if (chargList.contains("N")) {
//						messageBox("请全选");
//						return false;
//					}
//				}
//			}
//		}
		return true;
	}
	/**
	 * 显示未收费医嘱
	 */
	public void onNotCharge() {
		// ====zhangp 20120227 modify start
		if (!systemCode.equals("") && "ONW".equals(systemCode)) {
			checkBoxChargeAll.setEnabled(false);
			checkBoxChargeAll.setSelected(false);
		}else{
			checkBoxChargeAll.setEnabled(true);
		}
		//checkBox.setSelected(true);
		// =======zhangp 20120227 modify end

		TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
		cashPay.setEnabled(true);

		changeChargeText();

		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			// //加一空行
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParmNotCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), mem_package.isSelected(), un_exec.isSelected(),this.getValueString("MEM_TRADE"));
		// 调用过滤方法,给table赋值
		tableShow(tableShow);
		// setTableValue(tableShow);
		// 得到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo放数据
		comboPrescription.setVectorData(prescriptionCombopb);
		// 刷新combo
		comboPrescription.updateUI();
		EKTmessage = false; // 输出消息
	}

	/**
	 * 卡收费ridiobutton事件
	 */
	public void onEKTCharge(int i) {
		// ====zhangp 20120227 modify start
		checkBoxChargeAll.setEnabled(true);
		//		checkBoxChargeAll.setSelected(false);
		// =======zhangp 20120227 modify end

		TRadioButton ektPay = (TRadioButton) getComponent("tRadioButton_1");
		TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
		ektPay.setSelected(true);
		cashPay.setEnabled(false);
		onGatherChange(1);

		switch (i) {
		case 0:
			checkBoxChargeAll.setSelected(false);
			break;
		}

		changeChargeText();

		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			// //加一空行
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		//
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeTAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParmEKTTCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), mem_package.isSelected(), un_exec.isSelected());
		// 调用过滤方法,给table赋值
		tableShow_R(tableShow);
		// setTableValue(tableShow);
		// 得到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo放数据
		comboPrescription.setVectorData(prescriptionCombopb);
		// 刷新combo
		comboPrescription.updateUI();
		EKTmessage = true; // 输出消息
	}

	/**
	 * 显示全部医嘱
	 */
	public void onAll() {
		// ====zhangp 20120227 modify start
		checkBoxChargeAll.setEnabled(false);
		checkBoxChargeAll.setSelected(true);
		// =======zhangp 20120227 modify end

		TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
		cashPay.setEnabled(true);

		changeChargeText();

		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		boolean bo = false;
		// 如果选中全选checkbox
		if (checkBoxChargeAll.isSelected()) {
			bo = true;
		}
		// bo = false;
		// 如果有收费权限
		if (bilRight) {
			// 调用收费方法
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// 拿出table要显示的所有数据
		TParm tableShow = opb.getOrderParm(bo, comboPrescription.getValue(),
				true, mem_package.isSelected(), un_exec.isSelected(),this.getValueString("MEM_TRADE"));
		// 调用过滤方法,给table赋值
		tableShow(tableShow);
		// setTableValue(tableShow);
		// 拿到处方签
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// 给combo赋值
		comboPrescription.setVectorData(prescriptionCombopb);
		comboPrescription.updateUI();
		EKTmessage = false; // 输出消息
	}

	/**
	 * 按处方签过滤
	 */
	public void onPrescription() {
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// 查看显示方式
		String view = callFunction("UI|ALL|getValue").toString();
		// 如果是全部
		if (view.equals("Y")) {
			onAll();
			return;
		}
		// 显示未收费
		onNotCharge();
	}
	/**
	 * 集合医嘱过滤细项
	 * 
	 * @param parm
	 *            TParm
	 */
	public void tableShow(TParm parm) {
		// 医嘱代码
		String orderCode = "";
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		double ownPrice = 0;
		// 医嘱数量
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 删除按钮显示
		if (count < 0) {
			deleteFun = true;
		}
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
			ownPrice = 0;
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
					ownPrice += orderNew.getOwnPrice() * orderNew.getDosageQty();
					// 保存要删除的行
					removeRow[removeRowCount] = j;
					// 自加
					removeRowCount++;
				}
			}
			// 细项费用绑定主项
			parm.setData("AR_AMT", i, fee);
			parm.setData("OWN_PRICE", i, ownPrice);
		}
		// 删除集合医嘱细项=====pangben modify 20110801 不用去医生站就诊直接可以开立医嘱计费
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// ==================pangben modify 20110804 删除按钮显示
		if (removeRowCount < 0) {
			deleteFun = true;
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// 调用table赋值方法
		setTableValue(parm);
	}

	public void tableShow_R(TParm parm) {
		TParm tableParm = parm;
		// 医嘱代码
		String orderCode = "";
		// 医嘱组号
		int groupNo = -1;
		// 计算集合医嘱的总费用
		double fee = 0.0;
		// 医嘱数量
		int count = tableParm.getCount("ORDER_CODE");
		// 需要删除的细项列表
		int[] removeRow = new int[count];
		int removeRowCount = 0;
		// 循环医嘱
		for (int i = 0; i < count; i++) {
			Order order = (Order) tableParm.getData("OBJECT", i);
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
				Order orderNew = (Order) tableParm.getData("OBJECT", j);
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
			tableParm.setData("OWN_PRICE", i, fee);
			tableParm.setData("AR_AMT", i, fee);
		}
		// ==============pangben 2012-05-23 start 医嘱显示修改
		int temp = -1;
		for (int i = 0; i < removeRowCount; i++) {
			for (int j = 0; j < removeRowCount - 1; j++) {
				temp = removeRow[i];
				if (temp < removeRow[j]) {
					removeRow[i] = removeRow[j];
					removeRow[j] = temp;
				}
			}
		}
		// ==============pangben 2012-05-23 stop
		// 删除集合医嘱细项
		for (int i = removeRowCount - 1; i >= 0; i--) {
			tableParm.removeRow(removeRow[i]);
		}
		tableParm.setCount(tableParm.getCount() - removeRowCount);
		// 调用table赋值方法
		setTableValue_R(tableParm);

	}

	/**
	 * sysFee弹出界面
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 设置当前选中的行
		selectRow = row;
		// 如果当前选中的行不是最后一行空行则什么都不做
		if (table.getRowCount() != selectRow + 1) {
			return;
		}
		TTextField textfield = (TTextField) com;
		//根据挂号参数中有效天数校验是否可以就诊，添加医嘱操作===========pangben 2013-4-28
		if(!OPBTool.getInstance().canEdit(reg, regSysParm)){
			this.messageBox("超过当前就诊时间");
			this.onClear();
			return;
		}
		// 求出当前列号
		column = table.getColumnModel().getColumnIndex(column);
		// 得到当前列名
		String columnName = table.getParmMap(column);
		// 弹出sysfee对话框的列
		if (!columnName.equals("ORDER_DESC")) {
			return;
		}
		// if (column != 1)
		// return;
		//
		if (!(com instanceof TTextField)) {
			return;
		}
		textfield.onInit();	
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", this.getValueString("CAT1_TYPE"));
		//add by lx 2014/03/17  护士专用
		parm.setData("USE_CAT", "2");

		if (!"".equals(Operator.getRegion())) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("SYSFEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}

	/**
	 * 新开医嘱
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		if (systemCode != null && "ONW".equals(systemCode)) {
			TParm patParm = PatTool.getInstance().getLockPat(pat.getMrNo());
			// 判断是否加锁
			if (patParm != null && patParm.getCount() > 0) {
				if (!isMyPat(patParm)) {// 用户被锁定不能添加医嘱===pangben 2013-5-15
					this.messageBox("此病患已被" + patParm.getValue("OPT_USER", 0)
					+ "锁定");
					this.closeWindow();
					return;
				}
			}
		}
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		String freq_code=this.getFreqCode(parm);// add caoyong 20130108 得到频次
		newReturnOrder(parm,selectRow,0.00,parm.getValue("UNIT_CODE"),freq_code,true);
		//newReturnOrder(parm,selectRow,0.00,parm.getValue("UNIT_CODE"),true);
	}
	/**
	 * add caoyong 得到频次
	 * @param parm
	 * @return
	 */
	public String getFreqCode(TParm parm){
		//$$$$$$--------add caoyong 20131218 增加频次 start ----------------///
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "RIS".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "OTH".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				) {
			///order.setItem(row, "FREQ_CODE", "STAT");
			return "STAT";
			//orderOne.modifyFreqCode("STAT");//默认立即使用
			//parm.setData("FREQ_CODE","STAT");//默认立即使用
		}else{
			TParm action = new TParm();
			action.setData("ORDER_CODE",parm.getValue("ORDER_CODE") );
			TParm result = OdiMainTool.getInstance().queryPhaBase(
					action);
			return result.getValue("FREQ_CODE", 0);

		}
	}
	/**
	 * 传回医嘱公用
	 * ========pangben 2013-8-30
	 * @param parm
	 * flg :true 正常传回  false:手术套餐传回
	 * 
	 */
	private void newReturnOrder(TParm parm,int selectRow, double dosage_qty,String dosage_unit,String freq_code,boolean flg){
		// 新增处方签(补充计价),添加一个order
		String s[] = opb.getPrescriptionList().getGroupNames();
		String maxName = "0";
		if (s.length > 0) {
			maxName = s[s.length - 1];
		}
		if (pFlg.equals("N")) {
			orderList = opb.getPrescriptionList().newOrderList(maxName, "O",
					opb.getPat());
			pFlg = "Y";
		}
		// 拿到当前(补充计价)处方签中的最大医嘱号
		if (orderList == null) {
			orderList = opb.getPrescriptionList().getOrderList(maxName,
					opb.getPrescriptionList().getGroup(maxName).size());
		}

		// 新建医嘱
		Order orderOne = orderList.newOrder();

		// 是否收费
		boolean bo = false;
		// 收费标记
		String chargeFlg = "N";
		// 如果选中全选checkbox
		if (checkBoxChargeAll.isSelected()) {
			chargeFlg = "Y";
			bo = true;
		}
		// 划价注记
		orderOne.modifyChargeFlg(bo);
		// 如果是集合医嘱
		if (parm.getValue("ORDERSET_FLG").equals("Y")) {
			Order o=OdoUtil.initExaOrder(reg, parm, orderOne, false, serviceLevel);
			// 要新增order的位置
			int count = orderList.size() - 1;
			// 新增集合医嘱
			OdoUtil.addOrder(reg, orderList, count, false, serviceLevel);
			// 成本中心
			//			orderOne.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
			//					orderOne.getExecDeptCode(), ""));
			orderOne.modifyCostCenterCode(getValueString("REALDEPT_CODE"));
			orderOne.modifyExecDeptCode(getValueString("REALDEPT_CODE"));
			// 开立完成集合医嘱后的医嘱数量
			int orderListCount = orderList.size();
			// 拿到新开立的医嘱
			orderOne = orderList.getOrder(count);
			// 计算集合医嘱总费用
			double allFee = 0.0;
			// 整理新开立的医嘱
			for (int i = count; i < orderListCount; i++) {
				// 得到新开医嘱
				Order newOrder = orderList.getOrder(i);
				//				newOrder.modifyCostCenterCode(DeptTool.getInstance()
				//						.getCostCenter(newOrder.getExecDeptCode(), ""));
				orderOne.modifyCostCenterCode(getValueString("REALDEPT_CODE"));
				orderOne.modifyExecDeptCode(getValueString("REALDEPT_CODE"));
				newOrder.modifyChargeFlg(bo);
				newOrder.modifyDiscountRate(o.getDiscountRate());//======pangben 2013-8-30 折扣添加
				newOrder.modifyTakeDays(o.getTakeDays());
				//				if (!flg) {
				//					newOrder.modifyDosageQty(dosage_qty);
				//				}
				if (newOrder.getOrderCode().equals(newOrder.getOrdersetCode())) {
					continue;
				}
				// 计算费用
				allFee += newOrder.getArAmt();
			}
			// 拿到医嘱项目显示数据
			parm = orderOne.getParm();
			// 给主项添加显示费用
			parm.setData("AR_AMT", allFee);
		} else {
			// 组装三身份
			String[] ctz = new String[3];
			ctz[0] = opb.getReg().getCtz1Code();
			ctz[1] = opb.getReg().getCtz2Code();
			ctz[2] = opb.getReg().getCtz3Code();
			// 调用公用方法组装order
			orderOne = OdoUtil.fillOrder(orderOne, parm, ctz,dosage_qty, dosage_unit ,serviceLevel,flg);
			TotQtyTool t = new TotQtyTool();
			// 拿到医嘱项目显示数据
			parm = orderOne.getParm();
			//==========pangben 2013-1-28护士 、补充计费默认频次
			//orderOne.modifyFreqCode("STAT");//默认立即使用
			//parm.setData("FREQ_CODE","STAT");//默认立即使用

			//$-------------- modify caoyong 20131228 start------------------// 
			orderOne.modifyFreqCode(freq_code);//频次
			parm.setData("FREQ_CODE",freq_code);//频次
			//$-------------- modify caoyong 20131228 end------------------// 
			orderOne.modifyExecFlg("Y");
			if (!flg) {
				orderOne.modifyDosageQty(dosage_qty);
				orderOne.modifyDispenseQty(dosage_qty);
				orderOne.modifyDosageUnit(dosage_unit);
				orderOne.modifyTakeDays(1);
				parm.setData("TAKE_DAYS",1);
				orderOne.modifyOwnAmt(StringTool.round(orderOne.getOwnPrice() *
						orderOne.getDosageQty(), 2));
				double ctzRate=BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
						orderOne.getOrderCode(), orderOne.getDosageQty(), serviceLevel);
				orderOne.modifyArAmt(ctzRate<= 0 ?0.00 : ctzRate);
			}else{//====pangben 2013-10-29 添加开立医嘱初始值赋值操作
				TParm qty = t.getTotQty(parm);
				if ("Y".equalsIgnoreCase(orderOne.getGiveboxFlg())) {
					orderOne.modifyDispenseQty(StringTool.getDouble(TCM_Transform.getString(
							qty.getData("QTY_FOR_STOCK_UNIT"))));
					orderOne.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
							getData("TOT_QTY"))));
					orderOne.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
							"STOCK_UNIT")));
				}else {
					orderOne.modifyDosageQty(StringTool.getDouble(TCM_Transform.getString(qty.
							getData("QTY"))));
					orderOne.modifyDispenseUnit(TCM_Transform.getString(qty.getData(
							"DOSAGE_UNIT")));
					orderOne.modifyDispenseQty(orderOne.getDosageQty());
				}
				//orderOne.modifyMediQty(orderOne.getMediQty()*orderOne.getDosageQty());//===pangben 2013-12-10 传回的医嘱用量要匹配// modify caoyong 20131218
				orderOne.modifyOwnAmt(StringTool.round(orderOne.getOwnPrice() * orderOne.getDosageQty(), 2));
				orderOne.modifyArAmt(BIL.chargeTotCTZ(orderOne.getCtz1Code(), orderOne.getCtz2Code(),
						orderOne.getCtz3Code(), orderOne.getOrderCode(), orderOne
						.getDosageQty(), serviceLevel));
			}
			parm.setData("DISPENSE_QTY", orderOne.getDosageQty());// 配药量
			parm.setData("DOSAGE_QTY", orderOne.getDosageQty());// 发药量
			parm.setData("AR_AMT", orderOne.getArAmt());
			parm.setData("OWN_AMT",orderOne.getOwnAmt()); 
			//parm.setData("DISPENSE_QTY",orderOne.getDosageQty());//配药量
			//orderOne.modifyDispenseQty(orderOne.getDosageQty());//配药量
			//orderOne.modifyOwnAmt(orderOne.getOwnPrice()*orderOne.getDosageQty());//自费金额
			//orderOne.modifyArAmt(orderOne.getTakeDays()*orderOne.getOwnPrice());//总金额====pangben 2013-8-29 注释，折扣金额
			//parm.setData("DOSAGE_QTY",orderOne.getDosageQty());//发药量
			//parm.setData("AR_AMT",orderOne.getTakeDays()*orderOne.getOwnPrice());//总金额====pangben 2013-8-29 注释，折扣金额
			//parm.setData("OWN_AMT",orderOne.getOwnPrice()*orderOne.getDosageQty());//自费金额

		}
		// 成本中心
		//		orderOne.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
		//				orderOne.getExecDeptCode(), ""));
		orderOne.modifyCostCenterCode(getValueString("REALDEPT_CODE"));
		orderOne.modifyExecDeptCode(getValueString("REALDEPT_CODE"));
		parm.setData("EXEC_DEPT_CODE", getValueString("REALDEPT_CODE"));
		orderOne.modifyRexpCode(BIL.getRexpCode(orderOne.getHexpCode(), "O"));
		orderOne.modifyCaseNo(reg.caseNo());
		//		if (!flg) {
		//			orderOne.modifyDosageUnit(dosage_unit);
		//			orderOne.modifyDispenseUnit(parm.getValue("STOCK_UNIT", 0));
		//		}
		//parm.setData("FREQ_CODE","QD");
		// 划价标记
		parm.setData("CHARGE", chargeFlg);
		parm.setData("OBJECT", orderOne);
		// 把新开的医嘱放入table
		table.setRowParmValue(selectRow, parm);
		table.getParmValue().setRowData(selectRow, parm);
		double fee = getFee();
		// 新增一行
		table.addRow(setParm());
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
		table.getTable().grabFocus();
		table.setSelectedColumn(2);
		table.setSelectedRow(selectRow);
	}
	/**
	 * 增加对Table值改变的监听
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableChangeValue(Object obj) {
		// 当前编辑的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return false;
		}
		callFunction("UI|delete|setEnabled", false);
		// 当前编辑的列明
		String colunHeader = table.getParmMap(node.getColumn());
		// 更改划价注记
		if (colunHeader.equals("CHARGE")) {
			return chargeChange(node);
		}
		// 如果没有开医嘱的权限不能更改医嘱属性
		if (!addOrder) {
			return true;
		}
		if (node.getRow() != table.getRowCount() - 1) {
			// 拿到隐含列中的order
			Order order = (Order) table.getParmValue().getData("OBJECT",
					node.getRow());
			boolean dcOrder = order.getDcOrder();
			// ===zhangp 20120316 start
			// ===zhangp 20120414 start
			// if (dcOrder&&!order.getRxType().equals("0")) {
			if (dcOrder && !order.getRxType().equals("7")) {
				// ===zhangp 20120414 end
				messageBox_("医生开立的医嘱,不能更改");
				return true;
			}
			// ===zhangp 20120316 end
			// //如果是集合医嘱的主项
			// if ("Y".equals(order.getSetmainFlg())) {
			// this.messageBox("此医嘱是集合医嘱!");
			// return true;
			// }
		}
		// 如果此医嘱是医生开立的,收费员无权删除和编辑
		// ===zhangp 20120316 start
		// // 拿到隐含列中的order
		// Order order = (Order) table.getParmValue().getData("OBJECT",
		// node.getRow());
		if (node.getRow() >= drOrderCount) {
			// ===zhangp 20120316 end
			callFunction("UI|delete|setEnabled", true);
		}
		// 医嘱名称
		if (colunHeader.equals("ORDER_DESC")) {
			return orderDescChange(node);
		}
		// 用量
		if (colunHeader.equals("MEDI_QTY")) {
			return mediQtyChange(node);

		}
		// 频次
		if (colunHeader.equals("FREQ_CODE")) {
			return freqCodeChange(node);
		}
		// 用法
		if (colunHeader.equals("ROUTE_CODE")) {
			return routeCodeChange(node);
		}
		// 天数
		if (colunHeader.equals("TAKE_DAYS")) {
			return takeDateChange(node);
		}
		// 总量
		if (colunHeader.equals("DOSAGE_QTY")) {
			return dosageQtyChange(node);
		}
		// 就诊科室
		if (colunHeader.equals("DEPT_CODE")) {
			return deptCodeChange(node);
		}
		// 执行科室
		if (colunHeader.equals("EXEC_DEPT_CODE")) {
			return execDeptChange(node);
		}
		// 执行医生
		if (colunHeader.equals("EXEC_DR_CODE")) {
			return execDrCodeChange(node);
		}

		// 小计 add by huangtt 20150129
		if (colunHeader.equals("AR_AMT")) {
			return arAmtChange(node);
		}
		return false;
	}

	/**
	 * 修改批价列
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean chargeChange(TTableNode node) {
		table.acceptText();
		// 判断收费权限
		if (!bilRight) {
			return true;
		}
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1
				&& (!ektTCharge.isSelected())) {
			return true;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 当前node的值
		boolean b = TCM_Transform.getBoolean(node.getValue());
		// 给order设置收费标记
		order.modifyChargeFlg(b);
		// //如果order已经收费,不操作
		// if (order.getChargeFlg() != b)
		// return true;
		// 如果是集合医嘱的主项
		if ("Y".equals(order.getSetmainFlg())) {
			// 拿到医嘱代码
			String ordeCode = order.getOrderCode();
			// 拿到集合医嘱组序号
			int orderGroupNo = order.getOrderSetGroupNo();
			// 处方签号
			String rxNo = order.getRxNo();
			// 设置收费
			opb.congregation(ordeCode, orderGroupNo, rxNo, b);
		}
		// 重新计算费用
		// double fee = opb.getFee(!ektTCharge.isSelected());
		return false;
	}

	/**
	 * 改变医嘱名称
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean orderDescChange(TTableNode node) {
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 如果不是最后一行的空行
		if (node.getRow() != table.getRowCount() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * 如果改变用量
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean mediQtyChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		//如果节点的数据是负值，提示错误,yanjing,20130702
		if (Double.valueOf(node.getValue().toString())<0) {
			this.messageBox("用量不能为负值！");
			return true;
		}
		//=====20130702 yanjing end
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 调用用量改变事件
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order的对应列放入用量
		order.modifyMediQty(TCM_Transform.getDouble(node.getValue()));
		// 通过用量计算对应数据
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyMediQty(TCM_Transform.getDouble(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(4);
		table.setSelectedRow(selectRow);

		// //重新计算费用
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 频次更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean freqCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据

		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 如果不是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 设置order的频次
		order.modifyFreqCode(TCM_Transform.getString(node.getValue()));
		// 调用频次改变显示事件
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyFreqCode(TCM_Transform.getString(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(5);
		table.setSelectedRow(selectRow);

		// //设置table的显示
		// setTableValueAt(order,node);
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 用法更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean routeCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更改order的用法
		order.modifyRouteCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyRouteCode(TCM_Transform.getString(node.getValue()));
				}
			}

		}
		// 设置table的显示
		setTableValueAt(order, node);
		return false;
	}

	/**
	 * 天数更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean takeDateChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order设置天数
		order.modifyTakeDays(TCM_Transform.getInt(node.getValue()));
		// 调用天数改变计算
		order = OdoUtil.calcuQty(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyTakeDays(TCM_Transform.getInt(node.getValue()));
					o = OdoUtil.calcuQty(o, serviceLevel);
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(7);
		table.setSelectedRow(selectRow);

		// //设置table的显示
		// setTableValueAt(order,node);
		// //重新计算费用
		// //重新计算费用
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * 总量修改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean dosageQtyChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order设置总量
		order.modifyDosageQty(TCM_Transform.getDouble(node.getValue()));
		// 调用总量改变计算
		order = OdoUtil.calcuQtyAll(order, serviceLevel);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					TParm parm = new TParm();
					parm.setData("ORDER_CODE", o.getOrderCode());
					TParm sysFeeParm = SYSOrderSetDetailTool.getInstance()
							.selSyeFeeData(parm);
					o.modifyDosageQty(sysFeeParm.getDouble("TOTQTY", 0)
							* Double.parseDouble((String) node.getValue()));
					o = OdoUtil.calcuQtyAll(o, serviceLevel);
				}
			}

		}
		// 重新摘取费用
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(2);
		table.setSelectedRow(selectRow);
		return false;
	}

	/**
	 * 校验总量不能为0
	 * 
	 * @return
	 */
	private boolean dosageQtyCheck() {
		TParm parm = table.getParmValue();
		for (int i = 0; i < parm.getCount("CHARGE"); i++) {
			if (null == parm.getValue("ORDER_DESC", i)
					|| parm.getValue("ORDER_DESC", i).equals("<TNULL>")
					|| parm.getValue("ORDER_DESC", i).length() <= 0) {
				continue;
			}
			if (parm.getDouble("DOSAGE_QTY", i) == 0) {
				this.messageBox(parm.getValue("ORDER_DESC", i) + "总量不可以为0");
				return false;
			}
		}
		return true;
	}

	/**
	 * EXEC_DEPT_CODE 执行科室更改
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDeptChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的执行科室
		order.modifyExecDeptCode(TCM_Transform.getString(node.getValue()));
		order.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
				order.getExecDeptCode(), ""));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyExecDeptCode(TCM_Transform.getString(node
							.getValue()));
					o.modifyCostCenterCode(DeptTool.getInstance()
							.getCostCenter(o.getExecDeptCode(), ""));
				}
			}

		}
		return false;
	}

	/**
	 * 医嘱属性改变后table的值显改变
	 * 
	 * @param order
	 *            Order
	 * @param node
	 *            TTableNode
	 */
	public void setTableValueAt(Order order, TTableNode node) {
		int row = node.getRow();
		// TParm parm=order.getParm();
		// table.setRowParmValue(row,parm);
		// 用量
		table.setValueAt(order.getMediQty(), row, 2);
		// 频次
		table.setValueAt(order.getFreqCode(), row, 4);
		// 天数
		table.setValueAt(order.getTakeDays(), row, 6);
		// 总量DOSAGE_QTY
		table.setValueAt(order.getDosageQty(), row, 7);
		// 小计费
		table.setValueAt(order.getArAmt(), row, 10);
	}

	/**
	 * EXEC_DR_CODE 更改医师
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDrCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的医师
		order.modifyExecDrCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o
					.modifyExecDrCode(TCM_Transform.getString(node
							.getValue()));
				}
			}

		}
		return false;
	}

	/**
	 * DEPT_CODE 就诊科别改变
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean deptCodeChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 更新order的医师
		order.modifyDeptCode(TCM_Transform.getString(node.getValue()));
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().equals(order.getOrderCode())) {
			int count = orderList.size();
			String orderCode = order.getOrdersetCode();
			for (int i = 0; i < count; i++) {
				Order o = (Order) orderList.get(i);
				if (o.getOrdersetCode() != null
						&& o.getOrdersetCode().equals(orderCode)
						&& (!o.getOrderCode().equals(orderCode))) {
					o.modifyDeptCode(TCM_Transform.getString(node.getValue()));
				}
			}

		}
		return false;
	}

	/**
	 * AR_AMT 更改金额
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean arAmtChange(TTableNode node) {
		// 如果是最后一行的空行
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}

		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// 给order的对应列放入小计
		order.modifyArAmt(TCM_Transform.getDouble(node.getValue()));

		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(12);
		table.setSelectedRow(selectRow);
		//重新计算费用
		double fee=getFee();
		callFunction("UI|TOT_AMT|setValue", fee);
		return false;
	}


	/**
	 * 打开医疗卡
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm modifyOrderParm) {
		TParm orderParm = opb.getEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		setEktExeParm(orderParm, modifyOrderParm, null);
		if (orderParm.getParm("newParm").getCount("RX_NO") <= 0) {
			this.messageBox("没有要执行的数据");
			return null;
		}
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("CASE_NO", reg.caseNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("ektParm", parmEKT.getData());
		//zhangp
		orderParm.setData("PAY_OTHER3", modifyOrderParm.getDouble("PAY_OTHER3"));
		orderParm.setData("PAY_OTHER4", modifyOrderParm.getDouble("PAY_OTHER4"));
		TParm parm = new TParm();
		// 需要添加医保收费明细添加EKT_ACCNTDETAIL表数据添加
		parm = EKTIO.getInstance()
				.onOPDAccntClient(orderParm, onlyCaseNo, this);
		parm.setData("orderParm", orderParm.getData());
		return parm;
	}

	/**
	 * 收费操作收集数据 String exeTrade：UPDATE EKT_TRADE 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
	 * 
	 * @param parm
	 */
	private void setEktExeParm(TParm parm, TParm modifyOrderParm,
			String exeTrade) {
		// 新增、未收费修改、未收费删除的数据
		// TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		TParm newParm = new TParm();// 此次操作的医嘱集合
		TParm hl7Parm = new TParm();// hl7集合
		String bill_flg = "";
		double sum = 0.00;// 执行金额
		StringBuffer phaRxNo=new StringBuffer();//pangben 2013-5-17获得所有操作的处方签号码 发送数据
		if (!ektTCharge.isSelected()) {
			bill_flg = "Y";
		} else {
			bill_flg = "N";
		}
		parm.setData("BUSINESS_TYPE", "OPB");
		for (int i = 0; i < modifyOrderParm.getParm(OrderList.NEW).getCount(//新增数据,数据库还没有保存的
				"RX_NO"); i++) {
			for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
				// 提示勾选收费状态
				if (parm.getValue("RX_NO", j).equals(
						modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i))&& 
						parm.getValue("SEQ_NO", j).equals(modifyOrderParm.getParm(OrderList.NEW).getValue("SEQ_NO", i))) {
					OPBTool.getInstance().setNewParm(newParm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j), "E");// 新增的医嘱
					// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
					OPBTool.getInstance().setHl7TParm(hl7Parm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j));
					if (parm.getValue("CHARGE_FLG", j).equals("Y")) {
						sum += modifyOrderParm.getParm(OrderList.NEW)
								.getDouble("AR_AMT", i);
					}
					if (null != modifyOrderParm.getParm(OrderList.NEW).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
							modifyOrderParm.getParm(OrderList.NEW).getValue("CAT1_TYPE", i).equals("PHA")&&
							!modifyOrderParm.getParm(OrderList.NEW).getValue("RX_TYPE", i).equals("7")&&
							!modifyOrderParm.getParm(OrderList.NEW).getValue("RX_TYPE", i).equals("0")) {
						if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i))) {
							phaRxNo.append(modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i)).append(",");
						}
					}
				}
			}
		}
		for (int i = 0; i < modifyOrderParm.getParm(OrderList.MODIFIED)
				.getCount("RX_NO"); i++) {
			if (!ektTCharge.isSelected()){// 收费操作直接添加数据 退费操作 需要查找到所操纵医嘱的所有内部交易号
				// 将存在此内部交易号 的所有医嘱重新赋值
				OPBTool.getInstance().setNewParm(newParm,
						modifyOrderParm.getParm(OrderList.MODIFIED), i, "Y",
						"E");// 数据库中未收费的医嘱
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("0")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
			}
			// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
			OPBTool.getInstance().setHl7TParm(hl7Parm,
					modifyOrderParm.getParm(OrderList.MODIFIED), i, bill_flg);
		}
		StringBuffer tradeNo = new StringBuffer();
		if (ektTCharge.isSelected()) {// 退费操作
			StringBuffer tempTradeNo = new StringBuffer();
			// 查找此次操作的所有内部交易号
			for (int i = 0; i < modifyOrderParm.getParm(OrderList.MODIFIED)
					.getCount("RX_NO"); i++) {
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15添加药房审药显示跑马灯数据
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
				if (!tempTradeNo.toString().contains(
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue(
								"BUSINESS_NO", i))) {
					// 汇总这次操作的医嘱使用
					tempTradeNo.append(
							modifyOrderParm.getParm(OrderList.MODIFIED)
							.getValue("BUSINESS_NO", i)).append(",");
					// UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
					tradeNo.append("'").append(
							modifyOrderParm.getParm(OrderList.MODIFIED)
							.getValue("BUSINESS_NO", i)).append("',");
				}
			}
			String[] tempTradeNames = new String[0];
			if (tempTradeNo.length() > 0) {
				tempTradeNames = tempTradeNo.toString().substring(0,
						tempTradeNo.lastIndexOf(",")).split(",");
			}
			boolean newFlg = false;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				for (int j = 0; j < tempTradeNames.length; j++) {
					// 选择的医嘱
					// EKT_TRADE 表内部交易号
					if (parm.getValue("BUSINESS_NO", i).equals(
							tempTradeNames[j])) {
						newFlg = false;
						for (int z = 0; z < modifyOrderParm.getParm(
								OrderList.MODIFIED).getCount("RX_NO"); z++) {
							// 将此次退费的医嘱移除
							if (parm.getValue("RX_NO", i).equals(
									modifyOrderParm.getParm(OrderList.MODIFIED)
									.getValue("RX_NO", z))
									&& parm.getValue("SEQ_NO", i).equals(
											modifyOrderParm.getParm(
													OrderList.MODIFIED)
											.getValue("SEQ_NO", z))) {
								newFlg = true;
								break;
							}
						}
						if (!newFlg) {
							OPBTool.getInstance().setNewParm(newParm, parm, i,
									"Y", "E");
							sum += parm.getDouble("AMT", i);// 执行金额
						} else {
							OPBTool.getInstance().setNewParm(newParm, parm, i,
									"N", "C");
						}
					}
				}
			}
		}
		String exeTradeNo = "";
		// 获得内部交易号码 ：此次操作的医嘱扣款所有需要退还的医嘱
		if (tradeNo.length() > 0) {
			exeTradeNo = tradeNo.toString().substring(0,
					tradeNo.toString().lastIndexOf(","));
		}
		parm.setData("newParm", newParm.getData());// 操作医嘱，增删改
		// parm.setData("unBillParm",
		// modifyOrderParm.getParm(OrderList.MODIFIED).getData());//转回未收费状态数据
		parm.setData("hl7Parm", hl7Parm.getData());// HL7发送接口集合
		parm.setData("EXE_AMT", !ektTCharge.isSelected() ? this
				.getValueDouble("TOT_AMT") : sum);// EKT_TRADE 中此次 操作的金额
		// 获得此次就诊医嘱总金额包括已经收费、新开立(包括这次操作的医嘱)
		parm.setData("SHOW_AMT", this.getValueDouble("TOT_AMT"));// 显示的金额
		parm.setData("ORDER", modifyOrderParm.getData());// 更改OPD_ORDER 使用
		parm.setData("TRADE_SUM_NO", null == exeTrade ? exeTradeNo : exeTrade);// UPDATE
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15添加药房审药显示跑马灯数据
		// EKT_TRADE
		// 冲负数据,医疗卡扣款内部交易号码,格式'xxx','xxx'
	}

	/**
	 * 门诊收费医疗卡金额不足情况,执行收费医保分割打印操作
	 */
	public void exeInsPrint() {
		TParm exeParm = new TParm();
		TParm orderParm = opb.getInsEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		if (orderParm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("没有要执行的数据");
			return;
		}
		if (null == parmEKT || parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox("请读取医疗卡信息");
			return;
		}
		if (null == insParm) {
			this.messageBox("请读取医保卡信息");
			return;
		}
		if (null == this.getValue("UPDATE_NO")
				|| this.getValue("UPDATE_NO").toString().length() <= 0) {
			this.messageBox("没有可执行的票据号码");
			return;
		}
		TParm result = null;
		TParm parm = null;
		// 获得医保
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			parm = new TParm();
			parm.setData("BILL_D", SystemTool.getInstance().getDate());
			if (null != reg && null != reg.getAdmType()
					&& reg.getAdmType().equals("O")) {
				parm.setData("INS_CODE", orderParm.getValue("NHI_CODE_O", i));
			} else {
				parm.setData("INS_CODE", orderParm.getValue("NHI_CODE_E", i));
			}
			result = INSIbsTool.getInstance().queryInsIbsOrderByInsRule(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("获得医保数据失败");
				return;
			}
			orderParm.setData("YF", i, result.getValue("YF", 0));// 用法
			orderParm.setData("ZFBL1", i, result.getValue("ZFBL1", 0));// 自负比例
			orderParm.setData("PZWH", i, result.getValue("PZWH", 0));// 批准文号
		}
		insExeParm(orderParm);
		exeParm.setData("NAME", pat.getName());
		exeParm.setData("MR_NO", pat.getMrNo()); // 病患号
		exeParm.setData("PAY_TYPE", isEKT); // 支付方式
		exeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		// exeParm.setData("orderParm", orderParm.getData());// 需要收费的医嘱
		// exeParm.setData("parmSum", orderParm.getParm("parmSum").getData());//
		// 所有的医嘱
		// 包括集合医嘱用来修改
		// OPD_ORDER
		// MED_APPLY数据
		exeParm.setData("billAmt", orderParm.getDouble("billAmt"));// 所有医嘱金额
		// exeParm.setData("parmBill",
		// orderParm.getParm("parmBill").getData());// 未收费医嘱集合
		// 包括收费未收费
		exeParm.setData("ektParm", parmEKT.getData());// 医疗卡数据
		exeParm.setData("insParm", insParm.getData());// 医保数据
		exeParm.setData("FeeY", orderParm.getDouble("sumAmt"));// 应收金额
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("OPT_USER", Operator.getID());
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("ADM_TYPE", reg.getAdmType());
		exeParm.setData("ID_NO", pat.getIdNo());
		exeParm.setData("PRINT_NO", this.getValue("UPDATE_NO"));// 票号
		exeParm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());// 开始票号
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		setEktExeParm(orderParm, modifyOrderParm, orderParm
				.getValue("TRADE_SUM_NO"));
		exeParm.setData("orderParm", orderParm.getData());// 操作的数据
		TParm r = (TParm) openDialog("%ROOT%\\config\\ins\\INSFeePrint.x",
				exeParm);
		//======pangben 2013-3-13 添加校验为空
		if(null==r){
			return;
		}
		opdOrderSpc(orderParm);//===pangben 2013-5-22 添加物联网预审功能
		String re = EKTIO.getInstance().check(r.getValue("TRADE_NO"),
				reg.caseNo(),reduceAmt);
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("请马上与信息中心联系");
		}
		this.onClear();
	}
	/**
	 * =====pangben 2013-5-22 添加物联网预审功能
	 * OPD_ORDER表添加数据 
	 * @param orderParm
	 */
	private void opdOrderSpc(TParm orderParm){
		if (Operator.getSpcFlg().equals("Y")&&orderParm.getValue("PHA_RX_NO").length()>0) {
			// ==========pangben 2013-5-22 添加预审功能
			TParm spcParm = new TParm();
			spcParm.setData("RX_NO", orderParm.getValue("PHA_RX_NO"));
			spcParm.setData("CASE_NO", reg.caseNo());
			spcParm.setData("CAT1_TYPE", "PHA");
			spcParm.setData("RX_TYPE", "7");
			// 物联网获得此次操作的医嘱，通过处方签获得
			TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
					spcParm);
			if (spcResult.getErrCode() < 0) {
				this.messageBox("物联网操作：医嘱查询出现错误");
			} else {
				spcResult.setData("SUM_RX_NO", orderParm.getValue("PHA_RX_NO"));
				spcResult = TIOM_AppServer.executeAction(
						"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
						spcResult);
				if (spcResult.getErrCode() < 0) {
					System.out.println("物联网操作:" + spcResult.getErrText());
					this.messageBox("物联网操作：医嘱添加出现错误," + spcResult.getErrText());
				} else {
					phaRxNo = orderParm.getValue("PHA_RX_NO");// =pangben2013-5-15添加药房审药显示跑马灯数据
					sendMedMessages();
				}
			}
		}
	}
	/**
	 * 执行医保卡操作 添加数据 ，获得扣除以后的医嘱金额集合============pangb 2011-11-29
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm onINSAccntClient(TParm parm) {

		TParm result = INSTJReg.getInstance()
				.insCommFunction(insParm.getData());
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE")); // 现金支付： 享受医保救助 、
			// 上传费用明细失败
			// 、结算明细生成失败、账户支付失败
			if (null != result.getValue("FLG")
					&& result.getValue("FLG").length() > 0) {
				result.setErr(-1, "医保卡执行出错");
				return result;
			}
			return result;
		}
		return result;

	}

	/**
	 * 保存医嘱
	 * 
	 * @return boolean
	 */
	public boolean onSave() {		
		//add by huangtt 20141126    保存前查一下医嘱，进行比对，查看是否有不一样的
		if(!orderComparison(getOrder())){
			return false;
		}

		//add by huangtt 20141009 start
		if(onGreenBalance()){
			if(this.getValueDouble("GIFT_CARD2") != 0 || this.getValueDouble("GIFT_CARD") != 0){
				if(this.getValueDouble("PAY_OTHER4") == 0 && this.getValueDouble("PAY_OTHER3") == 0){
					this.messageBox("请输入代金券或礼品卡的钱数！");
					return false;
				}
			}

		}
		//add by huangtt 20141009 end

		// table接收
		table.acceptText();
		//delete by huangtt 20141126
		//		if (!PatTool.getInstance().isLockPat(pat.getMrNo())) {
		//			this.messageBox("病患已经被其他用户占用!");
		//			return false;
		//		}
		//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		//		if (("ODO".equals(parm.getValue("PRG_ID", 0))||"ODE".equals(parm.getValue("PRG_ID", 0)))//锁病患操作校验===pangben 2013-7-18
		//				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
		//				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
		//			this.messageBox(PatTool.getInstance().getLockParmString(
		//					pat.getMrNo()));
		//			return false;
		//		}

		if (getValue("BILL_TYPE").equals("")) {
			this.messageBox("支付方式不能为空");
			return false;
		}
		// 查询当前就诊病患是否执行记账
		// String sql =
		// "SELECT A.PRINT_NO,B.CONTRACT_CODE FROM BIL_REG_RECP A,REG_PATADM B WHERE A.CASE_NO=B.CASE_NO(+)  AND  A.CASE_NO='"
		// + onlyCaseNo +
		// "'";
		// CASE_NO!=null || CASE_NO!="" 记账: 不记账==========pangben 20110818
		String CONTRACT_CODE = "";
		if (resultBill != null && resultBill.getCount("CASE_NO") > 0) {
			if (null != resultBill && null != resultBill.getValue("CASE_NO", 0)
					&& !"".equals(resultBill.getValue("CASE_NO", 0))) {
				isbill = true; // 记账
				CONTRACT_CODE = resultBill.getValue("CONTRACT_CODE", 0); // =====pangben
			}
		}
		if (!dosageQtyCheck()) {
			return false;
		}
		if (!onSelectAllonExe()) {//=======pangben 2014-3-18 点击保存添加校验，有未执行的 提示信息
			return false;
		}
		// 20110818
		// 记账单位
		// 判断支付方式是否微信支付宝

		// 判断支付方式是否为医疗卡
		if (getValue("BILL_TYPE").equals("E")) {
			TRadioButton notCharge = (TRadioButton) getComponent("NOTCHARGE");
			if(notCharge.isSelected() && this.onPayOther()){//未收费
				return false;
			}
			// 保存医疗卡
			return onEktSave();
		}
		if (getValue("BILL_TYPE").equals("C")
				|| getValue("BILL_TYPE").equals("P")) {
			if (systemCode != null && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// // 初始化票号
				// if (!initBilInvoice(opb.getBilInvoice())) {
				// return false;
				// }
			}
			// 保存现金
			return onCashSave(CONTRACT_CODE);
		}
		// 医保卡操作
		if (getValue("BILL_TYPE").equals("I")) {
			if (isEKT) {
				return onEktSave();
			} else {
				return onCashSave(CONTRACT_CODE);
			}
		}
		// 检核开关帐
		if (checkOpenBill()) {
			return false;
		}
		// 组装票据
		setOpbReceipt();
		// 拿到收据金额
		double totAmt = getValueDouble("TOT_AMT");
		if (totAmt == 0) {
			this.messageBox("无收款金额");
			return false;
		}
		double pay = getValueDouble("PAY");
		if (pay - totAmt < 0 || pay == 0) {
			this.messageBox("金额不足!");
			this.grabFocus("PAY");
			return false;
		}
		// 如果收费金额大于0传入收费
		String charge = "N";
		if (bilRight) {
			charge = "Y";
		}

		// //得到收费项目
		// sendHL7Parm = table.getParmValue();
		// 调用opb的保存方法
		TParm result = opb.onSave(charge);
		if (result.getErrCode() < 0) {
			this.messageBox("缴费失败!");
			return false;
		}
		this.messageBox("操作成功");
		// //调用HL7
		// sendHL7Mes();
		// 得到后台保存返回的票据号
		String[] receiptNo = (String[]) result.getData("RECEIPTNO");

		// 调用处理打印的方法
		dealPrintData(receiptNo);
		// 收费成功重新刷新当前病患
		String mrNo = this.getValueString("MR_NO");
		onClear();
		//add by huangtt 20160530 "20160508-【急】（需求）门诊收费界面增加刷新功能		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}
	/**
	 * 查询同一个就诊号下所有的减免金额之和
	 * yanjing 20141203
	 */
	private double selectReduceAmt(String caseNo){
		String sql = "SELECT SUM(REDUCE_AMT) AS REDUCE_AMT FROM BIL_REDUCEM WHERE CASE_NO = '"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		double reduceAmt = parm.getDouble("REDUCE_AMT",0);
		return reduceAmt;
	}

	/**
	 * 校验保存操作数据是否执行
	 * 
	 * @return
	 */
	private boolean checkOnEktSave(boolean flg, TParm hl7ParmEnd,
			TParm modifyOrderParm) {
		TParm orderParm = modifyOrderParm.getParm(OrderList.MODIFIED);
		TParm checkParm = table.getParmValue();
		TParm checkParmEnd = new TParm();
		int checkCount = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < checkCount; i++) {
			if (checkParm.getBoolean("CHARGE", i)) {
				checkParmEnd.addData("ORDER_CODE", checkParm.getData(
						"ORDER_CODE", i));
				checkParmEnd.addData("EXEC_DEPT_CODE", checkParm.getData(
						"EXEC_DEPT_CODE", i));
				if (checkParm.getData("EXEC_DEPT_CODE", i) == null
						|| checkParm.getData("EXEC_DEPT_CODE", i).toString()
						.length() == 0) {
					this.messageBox("录入的医嘱执行科室不能为空!");
					return false;
				}
				if (checkParm.getBoolean("PRINT_FLG", i)) {
					this.messageBox("已打票,不能退费!");
					return false;
				}
				checkParmEnd.addData("DOSAGE_QTY", checkParm.getData(
						"DOSAGE_QTY", i));

			}
		}
		// 全院
		if (!this.getPopedem("ALL")) {
			if (!chekeRolo(checkParmEnd)) {
				return false;
			}
		}
		int count = orderParm.getCount("CASE_NO");
		//		for (int i = 0; i < count; i++) {
		//			//===zhangp 物联网修改 start
		//			if (!Operator.getSpcFlg().equals("Y")) {
		//				if ("PHA".equals(orderParm.getValue("CAT1_TYPE", i))
		//						&& !opb.checkDrugCanUpdate(orderParm.getRow(i), i) && !EKTIO.getInstance().ektAyhSwitch()) {
		//					this.messageBox("药品已审核或发药,不能退费!");
		//					return false;
		//				}
		//			}else{
		//				if ("PHA".equals(orderParm.getValue("CAT1_TYPE", i))) {
		//					String caseNo = orderParm.getValue("CASE_NO", i);
		//					String rxNo = orderParm.getValue("RX_NO", i);
		//					String seqNo = orderParm.getValue("SEQ_NO", i);
		//					TParm spcParm = new TParm();
		//					spcParm.setData("CASE_NO", caseNo);
		//					spcParm.setData("RX_NO", rxNo);
		//					spcParm.setData("SEQ_NO", seqNo);
		//					TParm spcReturn = TIOM_AppServer.executeAction(
		//			                "action.opb.OPBSPCAction",
		//			                "getPhaStateReturn", spcParm);
		////					PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
		////					SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
		////					if(spcOpdOrderReturnDto == null){
		////						return true;
		////					}
		//					if(spcReturn.getErrCode()==-2){
		//						return true;
		//					}
		//					boolean needExamineFlg = false;
		//					// 如果是西药 审核或配药后就不可以再进行修改或者删除
		//					if ("W".equals(orderParm.getValue("PHA_TYPE"))
		//							|| "C".equals(orderParm.getValue("PHA_TYPE"))) {
		//						// 判断是否审核
		//						needExamineFlg = PhaSysParmTool.getInstance()
		//								.needExamine();
		//					}
		//					// 如果有审核流程 那么判断审核医师是否为空
		//					if (needExamineFlg) {
		//						// System.out.println("有审核"); 
		//						// 如果审核人员存在 不存在退药人员 那么表示药品已审核 不能再做修改
		////						if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0
		////								&& spcOpdOrderReturnDto.getPhaRetnCode()
		////										.length() == 0) {
		////							this.messageBox("药品已审核,不能退费!");
		////							return false;
		////						}
		//						if (spcReturn.getValue("PhaCheckCode").length() > 0
		//								&& spcReturn.getValue("PhaRetnCode")
		//										.length() == 0 && !EKTIO.getInstance().ektAyhSwitch()) {
		//							this.messageBox("药品已审核,不能退费!");
		//							return false;
		//						}
		//					} else {// 没有审核流程 直接配药
		//						// 判断是否有配药药师
		//						// System.out.println("无审核");
		//						if (spcReturn.getValue("PhaDosageCode").length() > 0
		//								&& spcReturn.getValue("PhaRetnCode")
		//										.length() == 0 && !EKTIO.getInstance().ektAyhSwitch()) {
		//							this.messageBox("药品已发药,不能退费!");
		//							return false;// 已经配药不可以做修改
		//						}
		//					}
		//				}
		//			}
		//			//===zhangp 物联网修改 end	
		//			//====zhangp 20140113
		//			if (ektTCharge.isSelected()) {
		//				if (!"PHA".equals(orderParm.getValue("CAT1_TYPE", i))
		//						&& "Y".equals(orderParm.getValue("EXEC_FLG", i))) {
		//					this.messageBox("已到检,不能退费!");
		//					return false;
		//				}
		//			}
		//			// ===zhangp 20120421 end
		//		}
		// 发送接口集合
		if (!flg) {
			getCashHl7Parm(checkParm, hl7ParmEnd);
		}
		return true;
	}

	/**
	 * 现金操作发送接口
	 * 
	 * @param checkParm
	 * @param hl7ParmEnd
	 */
	private void getCashHl7Parm(TParm checkParm, TParm hl7ParmEnd) {
		int hl7Count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			if (checkParm.getBoolean("CHARGE", i)) {
				if ((checkParm.getValue("CAT1_TYPE", i).equals("RIS") || checkParm
						.getValue("CAT1_TYPE", i).equals("LIS"))
						&& checkParm.getBoolean("SETMAIN_FLG", i)
						&& checkParm.getValue("ORDERSET_CODE", i).equals(
								checkParm.getValue("ORDER_CODE", i))) {
					hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
							"TEMPORARY_FLG", i));
					hl7ParmEnd.addData("ADM_TYPE", checkParm.getData(
							"ADM_TYPE", i));
					hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData(
							"CAT1_TYPE", i));
					hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
					hl7ParmEnd
					.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
					// hl7ParmEnd.addData("BILL_FLG",
					// checkParm.getData("BILL_FLG", i));
					hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
							"MED_APPLY_NO", i));
				}
			}
		}
	}

	/**
	 * 医疗卡保存
	 * 
	 * @return boolean
	 */
	public boolean onEktSave() {
		String mrNo = this.getValueString("MR_NO");
		// ======zhangp 20120227 modify start
		this.callFunction("UI|save|setEnabled", false);
		this.callFunction("UI|CHARGE|setEnabled", false);
		if (!isEKT) {
			this.messageBox("请读取医疗卡信息");
			return false;
		}
		int type = 0;
		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return false;
		}
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		if (!checkOnEktSave(true, null, modifyOrderParm)) {
			return false;
		}
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (!EKTIO.getInstance().ektSwitch()) {
			messageBox_("医疗卡流程没有启动!");
			return false;
		}
		//zhangp 20131208
		boolean feeFlg = false;
		if(EKTIO.getInstance().ektAyhSwitch()){
			TParm preParm = EKTpreDebtTool.getInstance().checkMasterForCharge(modifyOrderParm, pat.getMrNo(), reg.caseNo());
			if(preParm.getErrCode()<0){
				if(messageBox("是否继续保存", preParm.getErrText()+",继续保存?", 0) != 0){
					return false;
				}
			}
		}
		//zhangp 20140317
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = getValueDouble("PAY_OTHER4");
		modifyOrderParm.setData("PAY_OTHER3", payOther3);
		modifyOrderParm.setData("PAY_OTHER4", payOther4);
		parm = onOpenCard(modifyOrderParm);
		if (parm == null) {
			this.messageBox("E0115");
			return false;
		}
		type = parm.getInt("OP_TYPE");
		if (type == 3 || type == -1) {
			this.messageBox("E0115");
			return false;
		}
		if (type == 2) {
			return false;
		}
		tredeNo = parm.getValue("TRADE_NO");

		// 得到收费项目
		// sendHL7Parm = hl7ParmEnd;
		// 调用opb的保存方法
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		// ==================pangben 需要修改的数据
		TParm result = opb.onSaveEKT(parm, ektTCharge.isSelected());
		if (result.getErrCode() < 0) {
			// EKTIO.getInstance().unConsume(tredeNo, this);
			TParm writeParm = new TParm();
			writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
			writeParm.setData("MR_NO", pat.getMrNo());
			writeParm.setData("SEQ", parmEKT.getValue("SEQ"));
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
			if (EKTmessage) {
				this.messageBox("医疗卡退费失败!");
			} else {
				this.messageBox("缴费失败!");
			}
			return false;
		} else {
			if (EKTmessage) {
				this.messageBox("医疗卡退费成功");
			} else {
				this.messageBox("操作成功");
			}
			opdOrderSpc(parm.getParm("orderParm"));//===pangben 2013-5-22 添加物联网预审功能
			// 调用HL7
			// 调用HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(
					parm.getParm("orderParm").getParm("hl7Parm"),
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// 护士补充计费不提示信息
		String re = EKTIO.getInstance().check(tredeNo, reg.caseNo(),StringTool.round(reduceAmt+selectReduceAmt(reg.caseNo()), 2));
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("请马上与信息中心联系");
			return false;
		}
		//zhangp
		boolean prtFlg = false;
		if (systemCode != "" && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {

		}else{
			if(checkBoxNotCharge.isSelected()){
				table.acceptText();
				TParm pa = table.getParmValue();
				for (int i = 0; i < pa.getCount(); i++) {
					if(pa.getBoolean("CHARGE", i)){
						prtFlg = true;
						break;
					}
				}
			}
		}
		// 收费成功重新刷新当前病患
		//zhangp
		if(prtFlg){
			//			onEKT();
			onEKTPrint("", true, null, null,null);
		}


		onClear();
		//add by huangtt 20160530 "20160508-【急】（需求）门诊收费界面增加刷新功能		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}

	/**
	 * 现金收费保存
	 * 
	 * @param CONTRACT_CODE
	 *            String
	 * @return boolean
	 */
	public boolean onCashSave(String CONTRACT_CODE) {
		String mrNo = this.getValueString("MR_NO");
		opb.initBilInvoice();
		// 检核开关帐
		if (opb.getBilInvoice() == null) {
			this.messageBox_("你尚未开账!");
			return false;
		}
		
		//检验当前是否是已经使用过，如果已经使用过将更新票号重新取号  add by huangtt 20170223 start
		String sqlInv = "SELECT MAX(INV_NO) INV_NO FROM BIL_INVRCP " +
		" WHERE CASHIER_CODE = '"+opb.getBilInvoice().getCashierCode()+"'" +
		" AND RECP_TYPE = '"+opb.getBilInvoice().getRecpType()+"' " +
		" AND INV_NO BETWEEN '"+opb.getBilInvoice().getStartInvno()+"' AND '"+opb.getBilInvoice().getEndInvno()+"'";
		TParm parmBil = new TParm(TJDODBTool.getInstance().select(sqlInv));
		if(parmBil.getCount() > 0){
			String invNoInvrcp = StringTool.addString(parmBil.getValue("INV_NO", 0));
			if(!invNoInvrcp.equals(opb.getBilInvoice().getUpdateNo())){
				String updateSql = "UPDATE BIL_INVOICE SET OPT_DATE=SYSDATE,UPDATE_NO='"+invNoInvrcp+"'" +
				" WHERE RECP_TYPE='"+opb.getBilInvoice().getRecpType()+"' " +
				" AND CASHIER_CODE='"+opb.getBilInvoice().getCashierCode()+"'" +
				" AND STATUS='"+opb.getBilInvoice().getStatus()+"'" +
				" AND START_INVNO='"+opb.getBilInvoice().getStartInvno()+"'";
				TParm rParm = new TParm(TJDODBTool.getInstance().update(updateSql));
				if(rParm.getErrCode() < 0){
					this.messageBox("当前票号错误，请去票据领用界面调标号！！！");
					return false;
				}
				opb.initBilInvoice();
			}
		}
		
		
		//add by huangtt 20170223 end
		
		
		
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return false;
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| opb.getBilInvoice().getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("无可打印的票据!");
			}
			return false;
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().equals(
				opb.getBilInvoice().getEndInvno())) {
			this.messageBox_("最后一张票据!");
			// return;
		}

		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return false;
		}

		//==========================================================如果是保险支付要校验病患的保险是否在有效期内add by huangjw 20150907 start
		TParm tableParm = paymentTool.table.getParmValue();
		for(int i = 0; i < tableParm.getCount("PAY_TYPE"); i++){
			if(tableParm.getValue("PAY_TYPE",i).equals("BXZF")){
				String sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " +
						" WHERE MR_NO = '"+pat.getMrNo()+"' AND VALID_FLG = 'Y'" +
						" AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
				TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(dataParm.getCount() < 0){
					if(JOptionPane.showConfirmDialog(null, "该病患保险不在有效期内，是否继续", "信息",
							JOptionPane.YES_NO_OPTION) == 0){
						break;//跳出循环
					}else{
						return false;//返回上一级方法
					}
				}
			}

		}
		//===========================================================如果是保险支付要校验病患的保险是否在有效期内add by huangjw 20150907 end 
		TParm payTypeParm = null;
		try {
			payTypeParm = paymentTool.getAmts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			messageBox(e.getMessage());
			return false;
		}
		TParm typeParm=paymentTool.table.getParmValue();
		boolean flg2=paymentTool.onCheckPayType(typeParm);
		if (flg2) {
		} else {
			this.messageBox("不允许出现相同的支付方式！");
			return flg2;
		}
		//add by huangtt 20160107 start 多种支付方式保存时判断钱数与应收金额是否一致
		double sum = 0;
		for (int i = 0; i < payTypeParm.getCount("AMT"); i++) {
			sum += payTypeParm.getDouble("AMT", i);
		}
		BigDecimal ar_amt_bd = new BigDecimal(this.getValueDouble("TOT_AMT")).setScale(2, RoundingMode.HALF_UP); 
		BigDecimal sumPay_bd = new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP);

		if(ar_amt_bd.compareTo(sumPay_bd) != 0){
			this.messageBox("多种支付方式金额为"+sumPay_bd+"，应收金额为"+ar_amt_bd+"，两者钱数不等，请重新输入支付方式钱数！");
			this.onQuery();
			return false;
		}
		//add by huangtt 20160107 end

		TParm hl7ParmEnd = new TParm();
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		if (!checkOnEktSave(false, hl7ParmEnd, modifyOrderParm)) {
			return false;
		}
		parm = opb.getCashParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		if (parm == null) {
			this.messageBox("E0115");
			return false;
		}
		// 得到收费项目
		// sendHL7Parm = hl7ParmEnd;
		// 调用opb的保存方法
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			this.messageBox("护士无收款权限");
			return false;
		}
		// 医保操作
		TParm insResult = null;
		if ("I".equals(this.getValueString("BILL_TYPE"))) { // 医保卡操作
			insResult = insCashSave();
			if (null == insResult) {
				return false;
			}
		}else{
			//			TParm orderParm = opb.getReduceCashParm(ektTCharge.isSelected());
			//			TParm parmReduce = onReduce(false,orderParm);//调用减免界面
			TParm parmReduce = reduceResult;
			if(parmReduce==null)
				return false;
			payTypeParm.setData("parmReduce",parmReduce.getData());//现金减免操作====pangben 2014-8-21
		}
		//现金打票操作，校验是否存在支付宝或微信金额
		TParm checkCashTypeParm=OPBTool.getInstance().checkCashType(payTypeParm);
		TParm payCashParm=null;
		if(null!=checkCashTypeParm.getValue("WX_FLG")&&
				checkCashTypeParm.getValue("WX_FLG").equals("Y")||null!=checkCashTypeParm.getValue("ZFB_FLG")&&
				checkCashTypeParm.getValue("ZFB_FLG").equals("Y")){
			Object result = this.openDialog(
					"%ROOT%\\config\\bil\\BILPayTypeOnFeeTransactionNo.x", checkCashTypeParm, false);
			if(null==result){
				return false;
			}
			payCashParm=(TParm)result;
		}
		TParm result = opb.onSaveCash(parm, ektTCharge.isSelected());
		if (result.getErrCode() < 0) {
			if(result.getErrCode() == -2){
				this.messageBox(result.getErrText());
				return false;
			}
			this.messageBox("缴费失败!");
			return false;
		} else {
			this.messageBox("操作成功");
			// 调用HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(hl7ParmEnd,
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// 收费成功重新刷新当前病患
		onEKTPrint(CONTRACT_CODE, false, insResult, payTypeParm,payCashParm); // ============pangben
		// 20110818 添加参数

		onClear();
		//add by huangtt 20160530 "20160508-【急】（需求）门诊收费界面增加刷新功能		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}

	/**
	 * 医保现金收费操作
	 * 
	 * @return TParm
	 */
	private TParm insCashSave() {
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", reg.caseNo());
		selOpdParm.setData("REGION_CODE", Operator.getRegion());
		TParm opdParm = OrderTool.getInstance()
				.selDataForOPBCashIns(selOpdParm);
		TParm insReturnParm = insExeFee(opdParm, true); // 医保收费操作
		if (null == insReturnParm) {
			this.messageBox("医保操作失败");
			return null;
		}
		insParm.setData("ACCOUNT_AMT", insReturnParm.getDouble("ACCOUNT_AMT")); // 医保金额
		insParm
		.setData("UACCOUNT_AMT", insReturnParm
				.getDouble("UACCOUNT_AMT")); // 现金金额
		insParm.setData("comminuteFeeParm", insReturnParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割数据
		insParm.setData("settlementDetailsParm", insReturnParm.getParm(
				"settlementDetailsParm").getData()); // 费用结算
		TParm ins_result = new TParm();
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			ins_result = onINSAccntClient(opdParm); // 现金操作，回参数据获得扣除医保金额以后的医嘱信息
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("医保收费失败");
				return null;
			}
			if (null != ins_result.getValue("MESSAGE")
					&& ins_result.getValue("MESSAGE").length() > 0) {
				// 现金支付
				return null;
			} else { // 现金使用 医疗卡已经打印收据不需要

				insFlg = true; // 判断医保在途状态执行
			}
		}
		return insReturnParm;
	}

	/**
	 * 处理打印数据
	 * 
	 * @param receiptNo
	 *            String[]
	 */
	public void dealPrintData(String[] receiptNo) {
		int size = receiptNo.length;
		for (int i = 0; i < size; i++) {
			// 取出一张票据号
			String recpNo = receiptNo[i];
			if (recpNo == null || recpNo.length() == 0) {
				return;
			}
			// 调用打印一张票据的方法
			onPrint(new OPBReceipt().getOneReceipt(recpNo));
		}
	}

	/**
	 * 打印票据
	 * 
	 * @param receiptOne
	 *            OPBReceipt
	 */
	public void onPrint(OPBReceipt receiptOne) {
		if (receiptOne == null) {
			return;
		} 
		TParm oneReceiptParm = receiptOne.getParm();
		oneReceiptParm.setData("PAT_NAME", opb.getPat().getName());
		oneReceiptParm.setData("DEPT_CODE", opb.getReg().getDeptCode());
		oneReceiptParm.setData("SEX_CODE", opb.getPat().getSexString());
		//oneReceiptParm.setData("CTZ_DESC", receiptOne.getCtzDesc());
		oneReceiptParm.setData("OPT_USER", Operator.getName());
		oneReceiptParm.setData("OPT_ID", Operator.getID());
		oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw",
				oneReceiptParm);
	}

	/**
	 * 组装票据数据
	 */
	public void setOpbReceipt() {
		// 拿到一个票据对象
		OPBReceipt receiptOne = opb.getReceiptList().newReceipt();
		// 现金支付
		receiptOne.setPayCash(getValueDouble("PAY_CASH"));
		// 记账支付
		receiptOne.setPayDepit(getValueDouble("PAY_DEBIT"));
		// 刷卡
		receiptOne.setPayBankCard(getValueDouble("PAY_BANK_CARD"));
		// 支票
		receiptOne.setPayCheck(getValueDouble("PAY_CHECK"));
		// 医保卡
		receiptOne.setPayInsCard(getValueDouble("PAY_INS_CARD"));
		// 医疗卡
		receiptOne.setPayMedicalCard(getValueDouble("PAY_MEDICAL_CARD"));
		// 其它支付(慢性病)
		receiptOne.setPayOther1(getValueDouble("PAY_OTHER1"));
		// 支票备注
		receiptOne.setPayRemark(getValueString("PAY_REMARK"));
		// 押金
		receiptOne.setPayBilPay(getValueDouble("PAY_BILPAY"));
		// 医保
		receiptOne.setPayIns(getValueDouble("PAY_INS"));
		// 总金额
		receiptOne.setTotAmt(getValueDouble("TOT_AMT"));
		// 自费金额
		receiptOne.setArAmt(getValueDouble("TOT_AMT")
				- getValueDouble("PAY_INS"));
		// 折扣金额
		receiptOne.setReduceAmt(getValueDouble("REDUCE_AMT"));
		// 减免科室
		receiptOne.setReduceDeptCode(getValueString("REDUCE_DEPT_CODE"));
		// 减免原因
		receiptOne.setReduceReason(getValueString("REDUCE_REASON"));
		// 减免人员
		receiptOne.setReduceRespond(getValueString("REDUCE_RESPOND"));
		// 收据费用的添加(charge01~charge30)
		receiptOne.initCharge(opb.getChargeParm());
		// 给票据对象添加总金额
		opb.getBilinvrcpt().setArAmt(getValueDouble("TOT_AMT"));

	}

	/**
	 * 删除新增医嘱
	 */
	public void onDelete() {
		// 如果没有新开立的医嘱无法删除
		// if (orderList == null) {
		// return;
		// }
		// 得到要删除的table行
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("请不要点选全部");
			return;
		}
		// ===zhangp 20120424 start
		if (ektTCharge.isSelected()) {
			this.messageBox("已收费医嘱不能删除");
			return;
		}
		// ===zhangp 20120424 end
		int removeRow = table.getSelectedRow();
		// 检核是否有权限删除选中的医嘱
		// ===zhangp 20120414 start
		Order order = (Order) table.getParmValue().getData("OBJECT", removeRow);
		// if (removeRow < drOrderCount && !deleteFun) {
		if (removeRow < drOrderCount
				&& !deleteFun
				&& !(order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			// ===zhangp 20120414 end
			this.messageBox("此医嘱是医生开立!");
			return;
		}
		if (orderList == null) {
			if (!deleteSetCodeOrder(order, removeRow, true))
				return;
		}
		if (orderList != null) {
			// ============pangben 2013-1-7 操作错误删除集合医嘱细项
			Order orderTemp = null;// 删除集合医嘱细项
			orderList.removeData(order);
			table.removeRow(removeRow);
			if (order.getOrderSetGroupNo() > 0) {// 移除集合医嘱
				for (int i = orderList.getTableParm().getCount("ORDER_CODE") - 1; i >= 0; i--) {
					orderTemp = orderList.getOrder(i);
					if (null == orderTemp)
						continue;
					if (null != orderTemp.getRxNo()
							&& orderTemp.getRxNo().equals(order.getRxNo())
							&& orderTemp.getOrderSetGroupNo() == order
							.getOrderSetGroupNo()) {
						orderList.removeData(orderTemp);// 移除细项
					}
				}
			}
			if (!deleteSetCodeOrder(order, removeRow, false))
				return;
		} else {
			onClear();
			onEKT();
		}
		double fee = getFee();
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
	}

	/**
	 * 删除操作 删除界面上集合医嘱细项问题 =======pangben 2013-1-10
	 * 
	 * @param order
	 * @param removeRow
	 * @return
	 */
	private boolean deleteSetCodeOrder(Order order, int removeRow, boolean flg) {
		TParm result = new TParm();
		if (order.getOrderSetGroupNo() > 0) {
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"deleteOPBChargeSet", order.getParm());
			if (result.getErrCode() < 0) {
				messageBox("删除失败");
				return false;
			}
			if (flg) {
				OdoUtil.deleteOrderSet(orderList, order);
				// 把删除的医嘱从table上一处
				table.removeRow(removeRow);
				if (orderList == null) {
					onClear();
					onEKT();
				}
				double fee = getFee();
				callFunction("UI|TOT_AMT|setValue", fee);

				paymentTool.setAmt(fee);

				setFeeReview();
				return false;
			}
		} else {
			result = TIOM_AppServer.executeAction("action.opb.OPBAction",
					"deleteOPBCharge", order.getParm());
			if (result.getErrCode() < 0) {
				messageBox("删除失败");
				return false;
			}
		}
		return true;
	}

	/**
	 * 清空方法
	 */
	public void onClear() {

		// ====zhangp 20120227 modify start
		checkBoxChargeAll.setEnabled(true);
		checkBoxChargeAll.setSelected(true);
		// 预设就诊日期
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());//=======yanjing 20131231 添加
		this.callFunction("UI|save|setEnabled", true);
		this.callFunction("UI|CHARGE|setEnabled", true);
		// =====zhangp 20120227 modify end
		clearValue("REDUCE_REASON;REDUCE_DEPT_CODE;REDUCE_RESPOND;REDUCE_AMT;CUSTOMER_SOURCE;REASSURE_FLG");
		clearValue("PAY_CASH;PAY_MEDICAL_CARD;PAY_BILPAY;PAY_INS;PAY_BANK_CARD;PAY_CHECK;");
		clearValue("PAY_DEBIT;PAY_OTHER1;TOT_AMT;PAY;PAY_RETURN;PAY_INS_CARD;PAY_OTHER2");
		clearValue("PAY_REMARK;MR_NO;PAT_NAME;IDNO;AGE;SEX_CODE;CTZ1_CODE;BIRTH_DATE;REGMETHOD_CODE");
		clearValue("CTZ2_CODE;CTZ3_CODE;PRESCRIPTION;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;CAT1_TYPE;EKT_CURRENT_BALANCE;AMT;PAY_OTHER3;PAY_OTHER4;GIFT_CARD;GIFT_CARD2;NO_PAY_OTHER;NO_PAY_OTHER_ALL");
		clearValue("INSURE_INFO");//保险信息
		callFunction("UI|record|setEnabled", false);
		this.setValue("SERVICE_LEVEL", "");
		this.setValue("TAX_FLG", "N");
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		mem_package.setSelected(false);
		un_exec.setSelected(false);
		callFunction("UI|MEM_TRADE|setEnabled", false);
		TTextFormat memTrade = (TTextFormat) getComponent("MEM_TRADE");
		memTrade.setValue("");
		// 清空table数据
		table.removeRowAll();
		setViewModou(false);
		opb = null;

		//delete by huangtt 20141126
		//		// 如果病患被锁定解锁
		//		unLockPat();

		reg = null;
		pat = null;
		reduceAmt = 0.00;
		// 当前选中的行
		selectRow = -1;
		// 是否新增过处方签
		pFlg = "N";
		// 记录医生开立医嘱的数量
		drOrderCount = -1;
		// 新建orderlist
		orderList = null;
		setViewModou(false);
		setValue("BILL_TYPE", "C");
		/**
		 * 判断是否为全院或组长权限
		 */
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			//			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", false);
			// ======zhangp 20120227 modify start
			// 初始化票据
			BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				initBilInvoice(bilInvoice.initBilInvoice("OPB"));
			}
			// ===============pangben 2012-3-30 管控
			//			this.callFunction("UI|MR_NO|setEnabled", false);
			// =======zhang 20120227 modify end
		}
		/**
		 * 根据进参默认医嘱类型
		 */
		if (this.getPopedem("opbPHA")) {
			setValue("CAT1_TYPE", "PHA");
		}
		if (this.getPopedem("LIS")) {
			setValue("CAT1_TYPE", "LIS");
		}
		if (this.getPopedem("RIS")) {
			setValue("CAT1_TYPE", "RIS");
		}
		if (this.getPopedem("TRT")) {
			setValue("CAT1_TYPE", "TRT");
		}
		if (this.getPopedem("PLN")) {
			setValue("CAT1_TYPE", "PLN");
		}
		if (this.getPopedem("OTH")) {
			setValue("CAT1_TYPE", "OTH");
		}
		// ==================pangben modify 20110804 删除按钮隐藏
		deleteFun = false; // 设定医嘱删除
		drOrderCountFalse = false; // 第一次记录就诊病患的医嘱信息
		drOrderCountTemp = 0; // 第一次记录就诊病患的医嘱信息
		feeShow = false; // 管控，显示金额使用 pangben modify 20110804
		isbill = false; // 管控，是否记账 pangben modify 20110818
		EKTmessage = false; // 管控 医疗卡退费操作
		isEKT = false; // 医疗卡信息读取操作
		TParm reValueParm = new TParm();
		reduceResult = reValueParm;
		tredeParm = null; // 判断支付方式
		insParm = null; // 医保 参数
		insFlg = false; // 医保卡读取操作
		resultBill = null; // 记账数据
		// ===zhangp 20120309 modify start
		ektTCharge.setEnabled(false);
		// ==zhangp 20120309 modify end
		// 医保结算打印
		callFunction("UI|insPrint|setEnabled", false);
		// 医疗卡打印
		callFunction("UI|ektPrint|setEnabled", false);
		// ===zhangp 20120331 start
		setValue("BILL_TYPE", "E");
		// ===zhangp 20120331 end
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setRowColor(i, null);
		}
		changeChargeText();

		TRadioButton r1 = (TRadioButton) getComponent("tRadioButton_1");

		r1.setSelected(true);

		TRadioButton cashPay = (TRadioButton) getComponent("tRadioButton_0");
		cashPay.setEnabled(true);

		this.onGatherChange(1);




	}

	/**
	 * 设置显示是否收费
	 * 
	 * @param view
	 *            boolean
	 */
	public void setViewModou(boolean view) {
		checkBoxNotCharge.setSelected(!view);
		checkAll.setSelected(view);
	}

	/**
	 * 右击MENU弹出事件
	 */
	public void onTableRightClicked() {
		int tableSelectRow = table.getSelectedRow();
		// 检核医嘱信息...如果是集合医嘱....如果是pha
		// 拿到隐含列中的order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				tableSelectRow);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().length() > 0) {
			table.setPopupMenuSyntax("显示集合医嘱细项,onOrderSetShow");
			return;
		}
		if (order.getOrderCat1Code().contains("PHA")) {
			table.setPopupMenuSyntax("显示药嘱信息,onSysFeeShow");
			return;
		}
		table.setPopupMenuSyntax("");
	}

	/**
	 * 右击MENU显示集合医嘱事件
	 */
	public void onOrderSetShow() {
		Order order = (Order) table.getParmValue().getData("OBJECT",
				table.getSelectedRow());
		String orderCode = order.getOrderCode();
		int groupNo = order.getOrderSetGroupNo();
		TParm parm = opb.getOrderSetParm(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);

	}

	/**
	 * 右击MENU显示SYS_FEE事件
	 */
	public void onSysFeeShow() {
		Order order = (Order) table.getParmValue().getData("OBJECT",
				table.getSelectedRow());
		String orderCode = order.getOrderCode();
		TParm parm = new TParm();
		parm.setData("FLG", "OPD");
		parm.setData("ORDER_CODE", orderCode);
		this.openWindow("%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_PHA.x", parm);

	}

	/**
	 * 各种操作前的数据检核
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// 检核人
		if (opb == null) {
			return true;
		}
		// 检核开关长
		if (checkOpenBill()) {
			return true;
		}
		// 检核医嘱
		if (opb.checkOrder()) {
			this.messageBox("没有要收费的医嘱");
			return true;
		}
		return true;
	}

	/**
	 * 检核开关账
	 * 
	 * @return boolean
	 */
	public boolean checkOpenBill() {
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			return false;
		}
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| !opb.initBilInvoice()) {
			this.messageBox("没有开账!");
			return true;
		}

		return false;
	}

	/**
	 * 检核医嘱
	 * 
	 * @return boolean
	 */
	public boolean checkOrder() {
		if (table.getRowCount() <= 1) {
			this.messageBox("无任何需要保存的医嘱!");
			return true;
		}
		return false;
	}

	/**
	 * 病患加锁
	 * 
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat() {
		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// 判断是否加锁
		if (parm != null && parm.getCount() > 0) {
			if (isMyPat(parm)) {
				return true;
			}
			if (RootClientListener.getInstance().isClient()) {
				parm.setData("PRGID_U", "OPB");
				parm.setData("MR_NO", pat.getMrNo());
				String prgId = parm.getValue("PRG_ID", 0);
				if ("ODO".equals(prgId)) {
					parm.setData("WINDOW_ID", "OPD01");
				} else if ("ODE".equals(prgId)) {
					parm.setData("WINDOW_ID", "ERD01");
				} else if ("OPB".equals(prgId)) {
					parm.setData("WINDOW_ID", "OPB0101");
				} else if ("ONW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:门诊
					parm.setData("WINDOW_ID", "ONW01");
				else if ("ENW".equals(prgId))//====pangben 2013-5-14 添加护士站解锁管控:急诊
					parm.setData("WINDOW_ID", "ONWE");
				String flg = (String) openDialog(
						"%ROOT%\\config\\sys\\SYSPatLcokMessage.x", parm);
				if ("UNLOCKING".equals(flg)) {
					this.onQuery();
					return false;
				}
				if ("LOCKING".equals(flg)) {
					this.onClear();
					return false;
				}
				if ("OK".equals(flg)) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
					PATLockTool.getInstance().log(
							"ODO->" + SystemTool.getInstance().getDate() + " "
									+ Operator.getID() + " "
									+ Operator.getName() + " 强制解锁[" + aa
									+ " 病案号：" + pat.getMrNo() + "]");
				} else {
					this.onClear();
					return false;
				}
			} else {
				if (this.messageBox("是否解锁", PatTool.getInstance()
						.getLockParmString(pat.getMrNo()), 0) == 0) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
					PATLockTool.getInstance().log(
							"ODO->" + SystemTool.getInstance().getDate() + " "
									+ Operator.getID() + " "
									+ Operator.getName() + " 强制解锁[" + aa
									+ " 病案号：" + pat.getMrNo() + "]");
				} else {
					onClear();
					return false;
				}
			}
		}
		// 锁病患信息
		if (!PatTool.getInstance().lockPat(pat.getMrNo(), checklockPat())) {
			onClear();
			return false;
		}
		return true;
	}
	/**
	 * pangben 2013-5-15
	 * 护士站解锁功能
	 * @return
	 */
	private String checklockPat(){
		String type = "OPB";// 添加护士站拒绝解锁功能，区分门急诊护士站操作
		if (systemCode!=null && "ONW".equals(systemCode)) {
			if(null!=onwType && onwType.equals("O"))
				type = "ONW";
			else{
				type = "ENW";
			}
		}
		return type;
	}
	/**
	 * 是否正在本人手中锁住病患
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	public boolean isMyPat(TParm parm) {
		if (!checklockPat().equals(parm.getValue("PRG_ID", 0))
				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
			return false;
		}
		return true;
	}

	/**
	 * 病患解锁
	 */
	public void unLockPat() {
		if (pat == null) {
			return;
		}
		// 判断是否加锁
		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
			if (Operator.getIP().equals(parm.getValue("OPT_TERM", 0))
					&&Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
				if ("OPB".equals(parm.getValue("PRG_ID", 0))
						||"ONW".equals(parm.getValue("PRG_ID", 0))
						||"ENW".equals(parm.getValue("PRG_ID", 0))) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
				}
			}
		}
		pat = null;
	}

	/**
	 * 现金回车实践 PAY_CASH
	 */
	public void onPayCash() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// //刷卡
		// double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payBankCard = arAmt - payCash - payDebit - payCheck
				- payMedicalCard - payOther2 - payBilPay - payInsCard;
		// 格式化金额
		payBankCard = StringTool.round(payBankCard, 2);
		if (payBankCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_BANK_CARD|setValue", payBankCard);
		// 银行卡得到焦点
		callFunction("UI|PAY_BANK_CARD|grabFocus");
	}

	/**
	 * 银行卡回车事件 PAY_BANK_CARD
	 */
	public void onPayBankCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// //医疗卡
		// double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payMoidCard = arAmt - payCash - payDebit - payCheck
				- payBankCard - payOther2 - payBilPay - payInsCard;
		// 格式化金额
		payMoidCard = StringTool.round(payMoidCard, 2);
		if (payMoidCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_MEDICAL_CARD|setValue", payMoidCard);
		// 医疗卡得到焦点
		callFunction("UI|PAY_MEDICAL_CARD|grabFocus");

	}

	/**
	 * 医疗卡回车事件 PAY_MEDICAL_CARD
	 */
	public void onPayMediCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// //医保卡
		// double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payInsCard = arAmt - payCash - payDebit - payCheck - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// 格式化金额
		payInsCard = StringTool.round(payInsCard, 2);
		if (payInsCard < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_INS_CARD|setValue", payInsCard);
		// 医疗卡得到焦点
		callFunction("UI|PAY_INS_CARD|grabFocus");
	}

	/**
	 * 医保卡回车事件 PAY_INS_CARD
	 */
	public void onPayInsCard() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// //支票
		// double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// /其它支付
		double payOther2 = getValueDouble("PAY_OTHER1");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");
		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payCheck = arAmt - payCash - payDebit - payInsCard - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// 格式化金额
		payCheck = StringTool.round(payCheck, 2);
		if (payCheck < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}

		// 赋值
		callFunction("UI|PAY_CHECK|setValue", payCheck);
		// 支票支付得到焦点
		callFunction("UI|PAY_CHECK|grabFocus");

	}

	/**
	 * 支票支付回车事件 PAY_CHECK
	 */
	public void onPayCheck() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// 其它支付
		double payOther2 = getValueDouble("PAY_OTHER2");
		// //押金
		// double payBilPay = getValueDouble("PAY_BILPAY");

		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payBilPay = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payOther2 - payCheck - payMedicalCard;
		// 格式化金额
		payBilPay = StringTool.round(payBilPay, 2);
		if (payBilPay < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_BILPAY|setValue", payBilPay);
		// 押金支付得到焦点
		callFunction("UI|PAY_BILPAY|grabFocus");

	}

	/**
	 * 押金支付 PAY_BILPAY
	 */
	public void onPayBilPay() {
		// 现金支付
		double payCash = getValueDouble("PAY_CASH");
		// 刷卡
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// 支票
		double payCheck = getValueDouble("PAY_CHECK");
		// 医保卡
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// 医疗卡
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// //其它支付
		// double payOther2 = getValueDouble("PAY_OTHER2");
		// 押金
		double payBilPay = getValueDouble("PAY_BILPAY");

		// 医保
		double payIns = getValueDouble("PAY_INS");
		// 总金额
		double totAmt = getValueDouble("TOT_AMT");
		// 记账支付
		double payDebit = getValueDouble("PAY_DEBIT");
		// 折扣金额
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// 自费金额（总金额-医保支付-记账-减免）
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// 计算银行卡支付金额
		double payOther2 = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payCheck - payMedicalCard - payBilPay;
		// 格式化金额
		payOther2 = StringTool.round(payOther2, 2);
		if (payOther2 < 0) {
			this.messageBox("录入金额不正确!请重新审核!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_OTHER2|setValue", payOther2);
		// 其它支付得到焦点
		callFunction("UI|PAY_OTHER2|grabFocus");
	}

	/**
	 * 其它支付 PAY_OTHER2
	 */
	public void onPayOther2() {
		// 检核费用事件
		// 收款金额得到焦点
		callFunction("UI|PAY|grabFocus");
	}

	/**
	 * 交款金额回车事件 PAY
	 */
	public void onPay() {
		// 折扣金额
		double pay = getValueDouble("PAY");
		// 折扣金额
		double arAmt = getValueDouble("TOT_AMT");
		if (pay - arAmt < 0 || pay == 0) {
			this.messageBox("金额不足!");
			return;
		}
		// 赋值
		callFunction("UI|PAY_RETURN|setValue", StringTool.round((pay - arAmt),
				2));
		// this.grabFocus("CHARGE");
	}

	/**
	 * 费用明细查询和退费
	 */
	public void onBackReceipt() {
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		if(mem_package.isSelected()){
			opbParm.setData("MEM_PACKAGE_FLG", "Y");
		}else{
			opbParm.setData("MEM_PACKAGE_FLG", "N");
		}
		this.openDialog("%ROOT%\\config\\opb\\OPBBackReceipt.x", opbParm);
		// 通过reg和caseNo重新初始化opb
		//		opb = OPB.onQueryByCaseNo(reg);
		//		onlyCaseNo = "";
		//		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		//		if (opb == null) {
		// this.messageBox_(555555555);
		//			this.messageBox("此病人尚未就诊!");
		//			 return;=====pangben modify 20110801
		//		}
		// 初始化opb后数据处理
		//		afterInitOpb();
		//		this.onClear();
		//		txReadEKT();   //delete by huangtt 20150127
		//		onQuery();
		//add by huangtt 20150127 start
		this.onClear();
		this.setValue("MR_NO", opbParm.getValue("MR_NO"));
		this.onQuery();
		//add by huangtt 20150127 end
		//		ektTCharge.setSelected(true); //delete by huangtt 20150127
		//		checkBoxChargeAll.setSelected(false); //delete by huangtt 20150127
		//		onEKTCharge(0); //delete by huangtt 20150127
	}

	/**
	 * 记账费用明细查询和退费 =========================pangben 20110823
	 */
	public void onBackContract() {
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		this.openDialog("%ROOT%\\config\\opb\\OPBBackContract.x", opbParm);
		// 通过reg和caseNo重新初始化opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// 给界面上部分地方赋值
		if (opb == null) {
			// this.messageBox_(555555555);
			this.messageBox("此病人尚未就诊!");
			// return;=====pangben modify 20110801
		}
		// 初始化opb后数据处理
		afterInitOpb();
		this.onClear();

	}

	/**
	 * 重新计算费用
	 */
	public void setFeeReview() {
		double fee = TypeTool.getDouble(getValue("TOT_AMT"));
		if ("E".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 医疗卡
			callFunction("UI|PAY_MEDICAL_CARD|setValue", fee);
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(true);

		} else if ("C".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 现金
			callFunction("UI|PAY_CASH|setValue", fee);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("P".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 记账
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", fee);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("I".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // 医保
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", fee);
			if (null != tredeParm) {
				ektTCharge.setEnabled(true);
			} else {
				ektTCharge.setEnabled(false);
			}
		}

	}

	/**
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();
		// Operator.setData("admin","HIS","127.0.0.1","C00101");
		// System.out.println("成本中心代码"
		// + DeptTool.getInstance().getCostCenter("01020101", "0003"));
	}

	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		txReadEKT();
		// 读卡时，支付方式切换为“医疗卡”
		TRadioButton ektPay = (TRadioButton) getComponent("tRadioButton_1");
		ektPay.setSelected(true);
		onGatherChange(1);
	}

	public void onEKTPrint() {
		if (this.messageBox("提示", "是否打印", 2) != 0) {
			return;
		}
		String mrNo = this.getValueString("MR_NO");
		TParm result = onEKTPrint("", true, null, null,null);
		if(null == result){
			return;
		}
		//add by huangtt 20160603
		this.setValue("MR_NO", mrNo);
		this.onQuery();
	}

	/**
	 * 医疗卡和现金打票(记账单位：现金使用)
	 * 
	 * @param contractCode
	 *            String
	 * @param ektPrintFlg
	 *            boolean
	 * @param insResult
	 *            TParm
	 */
	public TParm onEKTPrint(String contractCode, boolean ektPrintFlg,
			TParm insResult, TParm payTypeParm,TParm payCashParm) {
		
		if (opb == null || onlyCaseNo.length() == 0) {
			this.messageBox("先选择病患");
		}
		// 检核开关帐
		if (opb.getBilInvoice() == null) {
			this.messageBox_("你尚未开账!");
			return null;
		}

		
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("票据已用完!");
			return null;
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| opb.getBilInvoice().getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("无可打印的票据!");
			}
			return null;
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		//pangben 2019-4-25 套餐打票 判断操作医嘱是否在同一个套餐中，不在同一个套餐需要分票打印
		boolean memPartFlg = false;//分票注记
		if(mem_package.isSelected()){
			//校验票号是否充足
			String sql =
					" SELECT D.TRADE_NO " + 
					" FROM OPD_ORDER A,PHA_BASE B,SYS_FEE C,MEM_PAT_PACKAGE_SECTION_D D  " +
					" WHERE A.ORDER_CODE = B.ORDER_CODE(+) " +
					" AND A.ORDER_CODE = C.ORDER_CODE(+)  AND A.CASE_NO=D.CASE_NO AND A.MEM_PACKAGE_ID = D.ID " +
					" AND A.RELEASE_FLG <> 'Y' " +
					" AND A.RECEIPT_NO IS NULL " +
					" AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG ='N' OR A.PRINT_FLG ='') " +
					" AND A.MEM_PACKAGE_ID IS NOT NULL " +
					" AND A.CASE_NO = '" +onlyCaseNo + "'" +
					" GROUP BY D.TRADE_NO";
			TParm memTradeParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(memTradeParm.getCount()>1){
				//多个套餐
				String updateNo = opb.getBilInvoice().getUpdateNo();
				for(int i= 0; i< memTradeParm.getCount()-1; i++){
					updateNo = StringTool.addString(updateNo);
				}
				if (updateNo.compareTo(
						opb.getBilInvoice().getEndInvno()) > 0) {
					this.messageBox("票据已用完!");
					return null;
				}
				memPartFlg = true;
			}
		}
		// 检核当前票号
		if (opb.getBilInvoice().getUpdateNo().equals(
				opb.getBilInvoice().getEndInvno())) {
			this.messageBox_("最后一张票据!");
			// return;
		}

		// 显示下一票号
		callFunction("UI|UPDATE_NO|setValue", opb.getBilInvoice().getUpdateNo());
		String updateNo = this.getValueString("UPDATE_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", onlyCaseNo);
		parm.setData("MR_NO", pat.getMrNo());	 //modify by huangtt 20170605 直接取pat里面的mr_No	
		parm.setData("INV_NO", updateNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());
		// =============pangben modify 201110817 start
		parm.setData("feeShow", feeShow); // 金额的保存使用管控
		parm.setData("TOT_AMT", this.getValueDouble("TOT_AMT"));
		parm.setData("billFlg", isbill ? "N" : "Y"); // 记账: N 不记账:Y
		parm.setData("CONTRACT_CODE", contractCode); // 记账单位
		parm.setData("ADM_TYPE", reg.getAdmType()); // 挂号方式 :0 \E
		parm.setData("TAX_FLG", getValueString("TAX_FLG"));
		// =============pangben modify 201110817 stop
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
		
		TParm opdParm = new TParm();
		TParm result = new TParm();
		boolean flg = true; // 控制医疗卡打票
		TParm bilExeParm = null;
		TParm opbReceiptParm = new TParm(); // 获得收据号和医疗卡金额
		if (("E".equals(this.getValueString("BILL_TYPE")) && isEKT) || mem_package.isSelected()) { // 医疗卡打票操作
			if(!mem_package.isSelected()){
				opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
			}else{
				if(!memPartFlg){
					opdParm = OrderTool.getInstance().selDataForOPBEKTMem(selOpdParm);
				}else{
					//套餐分票打印
					bilExeParm = OrderTool.getInstance().selDataForMemPrint(parm,opb.getBilInvoice());
				}
				
			}
			if(!memPartFlg){
				result = ektSavePrint(opdParm, opbReceiptParm, parm);
			}else{
				result = TIOM_AppServer.executeAction("action.opb.OPBAction",
						"onOPBMemPrint", bilExeParm);
			}
			isbill = false;
			if(!mem_package.isSelected()){
				flg = false;
			}
		} else if ("C".equals(this.getValueString("BILL_TYPE"))
				|| "P".equals(this.getValueString("BILL_TYPE")) && !isEKT) {
			opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
			result = cashSavePrint(opdParm, parm, null, payTypeParm,payCashParm);
			System.out.println("现金打票");
		} else if ("I".equals(this.getValueString("BILL_TYPE"))) { // 医保卡操作
			// 执行医保操作
			if (ektPrintFlg && isEKT) {
				TParm readCard = EKTIO.getInstance().TXreadEKT();// 泰心医疗卡读卡操作
				if (!this.getValue("MR_NO").equals(readCard.getValue("MR_NO"))) {
					this.messageBox("医疗卡病患信息不符,不可以执行打票操作");
					return null;
				}

				opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
				TParm insReturnParm = insExeFee(opdParm, true); // 医保收费操作
				if (null == insReturnParm) {
					this.messageBox("医保操作失败");
					return null;
				}
				insParm.setData("ACCOUNT_AMT", insReturnParm
						.getDouble("ACCOUNT_AMT")); // 医保金额
				insParm.setData("UACCOUNT_AMT", insReturnParm
						.getDouble("UACCOUNT_AMT")); // 现金金额

				insParm.setData("comminuteFeeParm", insReturnParm.getParm(
						"comminuteFeeParm").getData()); // 费用分割数据
				insParm.setData("settlementDetailsParm", insReturnParm.getParm(
						"settlementDetailsParm").getData()); // 费用结算
			} else {
				opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
			}

			if (ektPrintFlg && isEKT) {
				parm.setData("ACCOUNT_AMT", insParm.getDouble("ACCOUNT_AMT")); // 医保金额
				result = ektSavePrint(opdParm, opbReceiptParm, parm);
				isbill = false;
				flg = false; // 医疗卡打票操作
			} else {
				result = cashSavePrint(opdParm, parm, insResult, payTypeParm,null);
			}
		}
		if (null == result || result.getErrCode() < 0) {
			this.messageBox("E0005");
			return null;
		}
		
		// =================pangben modify20110818
		if (isbill) {
			this.messageBox("已经记帐,不打印票据");
			return null;
		}
		TParm recpParm = null;

		// 门诊收据档数据:医疗卡收费打票|现金收费打票||医保打票
		// shibl  RECEIPT_NO重号问题  
		if(!memPartFlg){
			String receiptNo = result.getValue("RECEIPT_NO", 0);
			String printNoFee = "N";
			if (result.getData("PRINT_NOFEE", 0) != null) {
				printNoFee = "Y";
			}
			if ("Y".equals(printNoFee)) {
				this.onClear();			
				return null;
			}
			TParm Reparm=new TParm();
			Reparm.setData("RECEIPT_NO", receiptNo);
			Reparm.setData("CASE_NO", reg.caseNo());
			recpParm = OPBReceiptTool.getInstance().getOneReceipt(Reparm);
			recpParm = recpParm.getRow(0);
			onPrint(recpParm, flg);
		}else{
			TParm bilRecpParm =null;
			TParm bilOpbRecpList =  bilExeParm.getParm("bilOpbRecpList");
			for(int i= 0; i< bilOpbRecpList.getCount("bilRecpParm");i++){
				bilRecpParm = bilOpbRecpList.getParm("bilRecpParm",i);
				onPrint(bilRecpParm, flg);
			}
		}
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0 && insFlg) {
			// 医保在途状态删除
			if (!updateINSPrintNo(reg.caseNo(), "OPB")) {
				updateINSPrintNo(reg.caseNo(), "OPB");
			}
		}
		this.onClear();
		return new TParm();
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", "OPB");
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
	 * 医疗卡执行打票操作
	 * 
	 * @param opdParm
	 *            TParm
	 * @param opbReceiptParm
	 *            TParm
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm ektSavePrint(TParm opdParm, TParm opbReceiptParm, TParm parm) {
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TParm EKTTemp = new TParm();
		if(!mem_package.isSelected()){
			EKTTemp = EKTIO.getInstance().TXreadEKT();
			if (null == EKTTemp || EKTTemp.getErrCode() < 0
					|| EKTTemp.getValue("MR_NO").length() <= 0) {
				this.messageBox("此医疗卡无效");
				return null;
			}
		}
		TParm result = new TParm();
		// opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("无可收费医嘱");
			return null;
		}
		if (!insFee(opdParm, opbReceiptParm, EKTTemp))
			return null;
		parm.setData("opdParm", opdParm.getData()); // 获得一条汇总金额
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域
		parm.setData("TAX_FLG", getValueString("TAX_FLG"));
		//caowl 20140425  startr
		//TParm oneReceiptParm = new TParm();
		//		TParm parmReduce = onReduce(true,null);//调用减免界面
		TParm parmReduce = reduceResult;
		if(parmReduce==null)
			return null;
		parm.setData("parmReduce",parmReduce.getData());
		//caowl 20140425 end
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBEktprint", parm);//标记位。。。。。。。。。。。。caowl。。。。。。。。
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 现金打票
	 * 
	 * @param opdParm
	 *            TParm
	 * @param parm
	 *            TParm
	 * @param insResult
	 *            TParm
	 * @return TParm
	 */
	private TParm cashSavePrint(TParm opdParm, TParm parm, TParm insResult,
			TParm payTypeParm,TParm payCashParm) {
		// 现金 记账 医保卡执行操作
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("无可收费医嘱");
			return null;
		}
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			parm.setData("INS_RESULT", insResult.getData()); // 医保出参
			parm.setData("INS_FLG", "Y");
		}

		parm.setData("TAX_FLG", getValueString("TAX_FLG"));

		if(payTypeParm != null){
			TParm paymentParm=null;
			TParm parmReduce=payTypeParm.getParm("parmReduce");//减免返回数据
			if (parmReduce.getValue("REDUCEFLG").equals("N")) {	
			}else{
				paymentParm=parmReduce.getParm("PAYMENT_PARM");//现金减免支付方式
				parm.setData("REDUCE_AMT_SUM",parmReduce.getDouble("REDUCE_AMT"));//减免总金额===pangben 2014-8-21
			}
			parm.setData("PAY_TYPE_PARM", payTypeParm.getData());
			parm.setData("REDUCEFLG",parmReduce.getValue("REDUCEFLG"));//减免注记，操作减免打票
			parm.setData("parmReduce",parmReduce.getData());
			String key;
			double v;
			String remarkKey;
			String remark;
			for (int i = 1; i < 12; i++) {
				if(i < 10){
					key = "PAY_TYPE0" + i;
					remarkKey="REMARK0"+i;
				}else{
					key = "PAY_TYPE" + i;
					remarkKey="REMARK"+i;
				}
				v = 0;
				remark="";
				for (int j = 0; j < payTypeParm.getCount("PAY_TYPE"); j++) {
					if(key.equals(payTypeParm.getValue("PAY_TYPE", j))){
						v = payTypeParm.getDouble("AMT", j);
						if("PAY_TYPE02".equals(payTypeParm.getValue("PAY_TYPE", j))
								||"PAY_TYPE09".equals(payTypeParm.getValue("PAY_TYPE", j))
								||"PAY_TYPE10".equals(payTypeParm.getValue("PAY_TYPE", j))){//modify by kangy 20171018 微信支付宝添加卡类型备注交易号
							remark = payTypeParm.getValue("CARD_TYPE",j)+ "#" +payTypeParm.getValue("REMARKS",j);//刷卡收费，添加卡类型及卡号add by huangjw 20141230
						}else{
							remark=payTypeParm.getValue("REMARKS",j);//其他支付方式添加备注pangben 2016-6-21
						}
						if (null!=paymentParm) {//现金减免===pangben 2014-8-21
							for (int j2 = 0; j2 < paymentParm.getCount("PAY_TYPE"); j2++) {
								if (payTypeParm.getValue("PAY_TYPE", j)
										.equals(paymentParm.getValue("PAY_TYPE", j2))) {
									v-=paymentParm.getDouble("REDUCE_AMT", j2);
									break;
								}
							}
						}
						break;
					}

				}
				parm.setData(key, v);
				parm.setData(remarkKey,remark);//刷卡收费，添加卡类型及卡号add by huangjw 20141230 
			}
		}
		if(null!=payCashParm){//====pangben 2016-8-8 添加微信支付宝交易号码显示
			parm.setData("payCashParm",payCashParm.getData());
		}
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBCashprint", parm);//标记位。。。。。。。。。。。caowl。。。。。。。。。。。
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 医保收费
	 * 
	 * @param opdParm
	 *            TParm
	 * @param opbReceiptParm
	 *            TParm
	 * @param EKTTemp
	 *            TParm
	 * @return boolean
	 */
	private boolean insFee(TParm opdParm, TParm opbReceiptParm, TParm EKTTemp) {
		// TParm result = new TParm();
		// 医保操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			// 添加医保操作
			TParm ins_result = onINSAccntClient(opdParm); // 医保卡操作，回参数据获得扣除医保金额以后的医嘱信息
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("E0005");
				return false;
			}
			insFlg = true; // 判断是否执行医保在途状态删除
			// 医保退费回冲医疗卡金额操作
			// orderParm.setData("INS_FLG", "N");// 非医保卡操作
			opdParm.setData("AMT", -insParm.getDouble("ACCOUNT_AMT"));
			opdParm.setData("NAME", pat.getName());
			opdParm.setData("SEX", pat.getSexCode() != null
					&& pat.getSexCode().equals("1") ? "男" : "女");
			opdParm.setData("INS_FLG", "Y"); // 医保使用
			// 需要修改的地方
			opdParm.setData("MR_NO", pat.getMrNo());
			opdParm.setData("RECP_TYPE", "OPB"); // 收费类型

			// readCard.setData("CARD_NO", cardNo);
			// =================pangben 20110919 stop
			TParm result = insExeUpdate(insParm.getDouble("ACCOUNT_AMT"),
					EKTTemp, reg.caseNo(), "OPB", 9);
			// TParm result=EKTIO.getInstance().insUnFee(opdParm,this);
			if (result.getErrCode() < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 打印票据封装===================pangben 20111014
	 * 
	 * @param recpParm
	 *            TParm
	 * @param flg
	 *            boolean
	 */
	private void onPrint(TParm recpParm, boolean flg) {
		//		System.out.println();
		DecimalFormat df = new DecimalFormat("0.00");
		TParm oneReceiptParm = new TParm();
		TParm insOpdInParm = new TParm();
		String confirmNo = "";
		String cardNo = "";
		String insCrowdType = "";
		String insPatType = "";
		// 特殊人员类别代码
		String spPatType = "";
		// 特殊人员类别
		String spcPerson = "";
		double startStandard = 0.00; // 起付标准
		double accountPay = 0.00; // 个人实际帐户支付
		double gbNhiPay = 0.00; // 医保支付
		String reimType = ""; // 报销类别
		double gbCashPay = 0.00; // 现金支付
		double agentAmt = 0.00; // 补助金额
		double unreimAmt = 0.00;// 基金未报销金额
		double difference = 0.00;//优惠
		String reduceReason = "";
		// 医保打票操作
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			confirmNo = insParm.getValue("CONFIRM_NO");
			insOpdInParm.setData("CASE_NO", reg.caseNo());
			insOpdInParm.setData("CONFIRM_NO", confirmNo);
			cardNo = insParm.getValue("CARD_NO");

			TParm insOpdParm = INSOpdTJTool.getInstance().queryForPrint(
					insOpdInParm);
			TParm insPatparm = INSOpdTJTool.getInstance().selPatDataForPrint(
					insOpdInParm);
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.城职
			// 2.城居
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.普通
			// 特殊人员类别代码
			spPatType = insPatparm.getValue("SPECIAL_PAT", 0);
			// 特殊人员类别
			spcPerson = getSpPatDesc(spPatType);
			// 城职普通
			if (insCrowdType.equals("1") && insPatType.equals("1")) {
				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);

				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				else

					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ARMY_AI_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);

				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);

				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// 城职门特
			if (insCrowdType.equals("1") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ARMY_AI_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// 城居门特
			if (insCrowdType.equals("2") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				// 个人实际帐户支付
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
					+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
					+ insOpdParm.getDouble("FLG_AGENT_AMT", 0);
				else
					// 医保支付
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
					+ insOpdParm.getDouble("FLG_AGENT_AMT", 0);

				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// 现金支付
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// 补助金额
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
		}
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// 票据信息
		// 姓名
		//		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());
		oneReceiptParm.setData("PAT_NAME",  opb.getPat().getName());
		//性别
		oneReceiptParm.setData("SEX_CODE",  opb.getPat().getSexString());

		// 特殊人员类别
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				spcPerson.length() == 0 ? "" : spcPerson);
		// 社会保障号
		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// 人员类别
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "职工医保");
		// 费用类别
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			if (reg.getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "门统");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			if (reg.getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "门特");
		}
		// =====zhangp 20120228 modify end
		// 医疗机构名称
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
		// 起付金额
		oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
				startStandard, 2));
		// 最高限额余额
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "--" : df
				.format(unreimAmt));
		// 账户支付
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));


		/*----add by lich 20140928 添加报表信息----start*/

		//add by huangtt 20140930  start
		String ownSql = "SELECT SUM(OWN_AMT) OWN_AMT ,SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
				+ recpParm.getValue("CASE_NO")
				+ "' AND RECEIPT_NO='"
				+ recpParm.getValue("RECEIPT_NO") + "' AND (BILL_FLG='Y' OR MEM_PACKAGE_ID IS NOT NULL)";
		//		System.out.println("ownSql=="+ownSql);
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double ownAmt = ownParm.getDouble("OWN_AMT", 0); //折扣之前的价钱
		//		double totAmt = recpParm.getDouble("TOT_AMT", 0); //折扣之后的价钱
		double arAmt = recpParm.getDouble("AR_AMT");  //实收
		//		double reduceAmt=recpParm.getDouble("REDUCE_AMT", 0); //减免
		//add by huangtt 20140930  start

		// 费用应收
		oneReceiptParm.setData("TOT_AMT", df.format(ownAmt));
		//优惠金额
		difference = ownAmt-arAmt;


		//实收金额
		oneReceiptParm.setData("AR_AMT",df.format(recpParm.getDouble("AR_AMT")));

		//减免、折扣
		//		reduceReason = recpParm.getValue("REDUCE_REASON", 0);
		reduceReason = recpParm.getValue("REDUCE_NOTE");
		//		System.out.println("reduceReason-----------"+reduceReason);
		if(reduceReason.trim().length() == 0){
			reduceReason = "折扣";
		}else if(reduceReason.trim().length() > 0){
			if (reduceReason.length()>55) {
				reduceReason=reduceReason.substring(0,55);
			}
			reduceReason = "减免原因："+reduceReason;
		}
		oneReceiptParm.setData("REDUCE_REASON",reduceReason);
		/*----add by lich 20140928 添加报表信息----end*/
		// 减免金额
		oneReceiptParm.setData("REDUCE_AMT", "TEXT", df.format(recpParm.getDouble(
				"REDUCE_AMT")));
		// 费用显示大写金额
		//		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
		//				.numberToWord(recpParm.getDouble("AR_AMT", 0)));//==pangben 20140611 TOT_AMT 改为AR_AMT
		oneReceiptParm.setData("TOTAL_AW", StringUtil.getInstance().numberToWord(recpParm.getDouble("AR_AMT")));//==liling

		// 统筹支付
		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
				.getDouble("Overall_pay"), 2));
		// 个人支付
		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
				.getDouble("TOT_AMT")));
		// 现金支付= 医疗卡金额+现金+绿色通道
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH"), 2)
				+ StringTool
				.round(recpParm.getDouble("PAY_MEDICAL_CARD"), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1"), 2);
		// 现金支付
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// 账户支付---医疗卡支付
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);

		// =====zhangp 20120229 modify start
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "医疗救助支付");
			// 医疗救助金额
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
		//		oneReceiptParm.setData("MR_NO", "TEXT", pat.getMrNo());
		oneReceiptParm.setData("MR_NO",  pat.getMrNo());
		// =====zhangp 20120229 modify end
		// 打印日期
		oneReceiptParm.setData("OPT_DATE", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));//==liling add HH:MM:ss

		// 医保金额====pangben 2012-6-7 显示医保金额
		// String pay_debit = gbNhiPay == 0 ?
		// StringTool.round(recpParm.getDouble(
		// "PAY_INS_CARD", 0), 2)
		// + "" : df.format(gbNhiPay);
		// 医保金额
		// ===zhangp 20120702 start
		// oneReceiptParm.setData("PAY_DEBIT", "TEXT",
		// pay_debit.equals(df.format(accountPay))?"0.0":pay_debit);
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df.format(gbNhiPay));

		/*----add by lich 20140928  表头增加科室信息----start*/
		TTextFormat deptCode = (TTextFormat) getComponent("DEPT_CODE");
		oneReceiptParm.setData("REALDEPTCODE","TEXT",deptCode.getText());
		/*----add by lich 20140928  表头增加就诊身份----start*/
		TComboBox ctzCode = (TComboBox) getComponent("CTZ1_CODE");
		String s="SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"+ctzCode.getText()+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(s));
		oneReceiptParm.setData("CTZ_DESC","TEXT",parm.getValue("CTZ_DESC"));

		//		System.out.println("REALDEPTCODE::::::::::"+reg.getRealdeptCode());
		/*----add by lich 20140928  表头增加科室信息----end*/

		// ===zhangp 20120702 end
		if (recpParm.getDouble("PAY_OTHER1") > 0) {
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "绿色通道支付");
			// 绿色通道金额
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1"), 2));

		}
		// 医生名称
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE"));

		// 打印人
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", Operator.getID());
		TParm EKTTemp = null;
		if (!flg) {
			EKTTemp = EKTIO.getInstance().TXreadEKT();
			if (EKTTemp.getErrCode() < 0) {
				this.messageBox("此医疗卡无效");
				return;
			}
			if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
				this.messageBox("此医疗卡无效");
				// 添加出现问题撤销
				return;
			}
		}
		if (recpParm.getDouble("REDUCE_AMT")>0) {//===pangben 2014-9-18 减免操作添加减免金额提示
			this.messageBox("收费金额:"+df.format(recpParm.getDouble("AR_AMT"))+"元,减免金额:"+df.format(recpParm.getDouble("REDUCE_AMT"))+"元");
		}
		// oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(详见费用清单)");
		// =====20120229 zhangp modify start
		if (cardNo.equals("")) {
			oneReceiptParm.setData("CARD_CODE", "TEXT", pat.getIdNo());// 如果不是医保
			// 显示身份证号
		} else {
			oneReceiptParm.setData("CARD_CODE", "TEXT", cardNo);// 否则 显示医保卡号
		}
		// =====20120229 zhangp modify end
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				//				oneReceiptParm.setData("CHARGE0" + i, "TEXT", recpParm
				//						.getDouble("CHARGE0" + i, 0) == 0 ? "" : recpParm
				//						.getData("CHARGE0" + i, 0));
				oneReceiptParm.setData("CHARGE0" + i,df.format( recpParm.getDouble("CHARGE0" + i)).equals("0.00") ? "" : df.format( recpParm.getDouble("CHARGE0" + i)));//==liling
			} else {
				//				oneReceiptParm.setData("CHARGE" + i, "TEXT", recpParm
				//						.getDouble("CHARGE" + i, 0) == 0 ? "" : recpParm
				//						.getData("CHARGE" + i, 0));
				oneReceiptParm.setData("CHARGE" + i, df.format( recpParm.getDouble("CHARGE" + i)).equals("0.00") ? "" : df.format( recpParm.getDouble("CHARGE" + i)));//==liling
			}
		}
		//====================20140609 liling  modify start 查出各项收费对应的中文=====
		String sql="SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY ID ";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String sql2= "SELECT ADM_TYPE,CHARGE01,CHARGE02,CHARGE03,CHARGE04, CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09, "+
				" CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14, CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20 "+
				" FROM BIL_RECPPARM   WHERE  ADM_TYPE='O'  " ;
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		String sql3 = "SELECT MEM_PACKAGE_ID FROM OPD_ORDER WHERE MR_NO = '"+
				opb.getPat().getMrNo()+"' AND CASE_NO = '"+opb.getReg().caseNo()+"' AND MEM_PACKAGE_ID IS NULL";//add by sunqy 20140827 是否医生站引用套餐
		//TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		//		System.out.println("sql3=  =  =  =  ="+sql3);
		//		System.out.println("parm3====="+parm3);
		String	id="";
		String	charge ="";
		for(int i=1;i<=20;i++){
			if (i < 10) {
				charge =parm2.getValue("CHARGE0"+i, 0);
				for(int j=0;j<parm1.getCount();j++){
					id=parm1.getValue("ID", j);	
					if(id.equals(charge)){
						oneReceiptParm.setData("CHARGE0" + i+"_D", parm1.getData("CHN_DESC", j));
						//					System.out.println(i+"========="+parm1.getData("CHN_DESC", j));
					}
				}
			} else {
				charge =parm2.getValue("CHARGE"+i, 0);
				for(int j=0;j<parm1.getCount();j++){
					id=parm1.getValue("ID", j);	
					if(!id.equals(charge))continue;
					else{
						oneReceiptParm.setData("CHARGE" + i+"_D", parm1.getData("CHN_DESC", j));
						//					System.out.println(i+"========="+parm1.getData("CHN_DESC", j));
					}
				}
			}
		}
		oneReceiptParm.setData("CHARGE001_D", "西药费");
		//		oneReceiptParm.setData("BILL_DATE", "TEXT",StringTool.getString(
		//				recpParm.getTimestamp("BILL_DATE", 0), "yyyy/MM/dd HH:mm:ss"));//结算日期
		oneReceiptParm.setData("BILL_DATE", StringTool.getString(
				recpParm.getTimestamp("BILL_DATE"), "yyyy/MM/dd HH:mm:ss"));//结算日期
//		oneReceiptParm.setData("AR_AMT", df.format(recpParm.getDouble("AR_AMT", 0)));//合计
		//====================20140609 liling  modify end  ===========================
		// =================20120219 zhangp modify start
		//		oneReceiptParm.setData("CHARGE01", "TEXT", df.format(recpParm
		//				.getDouble("CHARGE01", 0)
		//				+ recpParm.getDouble("CHARGE02", 0)));
		oneReceiptParm.setData("CHARGE01", df.format(recpParm.getDouble("CHARGE01")+ 
				recpParm.getDouble("CHARGE02")).equals("0.00")?
						"":df.format(recpParm.getDouble("CHARGE01")+ recpParm.getDouble("CHARGE02")));//==liling
		String caseNo = opb.getReg().caseNo();

		TParm dparm = new TParm();
		dparm.setData("CASE_NO", caseNo);
		dparm.setData("ADM_TYPE", reg.getAdmType());
		onPrintCashParm(oneReceiptParm, recpParm, dparm);
		//	    oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("RECEIPT_NO", 0));//add by wanglong 20121217  PRINT_NO
		oneReceiptParm.setData("RECEIPT_NO", "TEXT", recpParm.getValue("PRINT_NO"));//===modify by liling 20140703 
		//		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw",
		//				oneReceiptParm, true);

		//add by sunqy 20140827 为收据添加支付细项 ----start----
		String mapSql = "SELECT A.PAYTYPE,A.GATHER_TYPE, B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B " +
				"WHERE B.GROUP_ID='GATHER_TYPE' AND A.GATHER_TYPE=B.ID";//储存PAY_TYPE极其对应的中文
		TParm mapParm = new TParm(TJDODBTool.getInstance().select(mapSql));
		String sql4 = "SELECT PAY_CASH,PAY_MEDICAL_CARD,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04," +
				"PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_OTHER3,PAY_OTHER4,REMARK02,REMARK09,REMARK10,WX_BUSINESS_NO,ZFB_BUSINESS_NO,PAY_TYPE11,REMARK11 " +//modify by kangy 20171018 添加微信支付宝交易号
				"FROM BIL_OPB_RECP WHERE CASE_NO = '" + caseNo + "' " +
				"AND RECEIPT_NO = '" + recpParm.getData("RECEIPT_NO").toString() + "'";
		TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
		String payDetail = "";
		String card_type = "";
		if(!"".equals(parm4.getValue("PAY_MEDICAL_CARD",0)) && parm4.getValue("PAY_MEDICAL_CARD",0)!=null && parm4.getDouble("PAY_MEDICAL_CARD",0)!=0){
			payDetail += ";医疗卡"+parm4.getValue("PAY_MEDICAL_CARD",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER3",0)) && parm4.getValue("PAY_OTHER3",0)!=null && parm4.getDouble("PAY_OTHER3",0)!=0){
			payDetail += ";礼品卡"+parm4.getValue("PAY_OTHER3",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER4",0)) && parm4.getValue("PAY_OTHER4",0)!=null && parm4.getDouble("PAY_OTHER4",0)!=0){
			payDetail += ";现金折扣券"+parm4.getValue("PAY_OTHER4",0)+"元";
		}
		if(!"".equals(parm4.getValue("PAY_CASH",0)) && parm4.getValue("PAY_CASH",0)!=null && parm4.getDouble("PAY_CASH",0)!=0){
			for (int i = 0; i < mapParm.getCount(); i++) {
				if("".equals(parm4.getValue(mapParm.getValue("PAYTYPE", i),0)) || parm4.getValue(mapParm.getValue("PAYTYPE", i),0)==null 
						|| parm4.getDouble(mapParm.getValue("PAYTYPE", i),0)==0){
					continue;
				}
				payDetail += ";"+mapParm.getValue("CHN_DESC", i)+parm4.getValue(mapParm.getValue("PAYTYPE", i),0)+"元";
				//==start==modify by kangy20171019 刷卡微信支付宝添加卡类型备注交易号
				/*if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE02")){
					card_type=parm4.getValue("REMARK02", 0);//收费为刷卡时，收据添加卡号和卡类型 add by huangjw 20141230 
				}*/
				String remark02 = parm4.getValue("REMARK02",0);
				String remark09= parm4.getValue("REMARK09",0);
				String remark10= parm4.getValue("REMARK10",0);
				String cardtypeString = "";
				String wxCardtypeString = " ";
				String zfbCardtypeString = " ";
				String wxBusinessNo="";//微信交易号
				String zfbBusinessNo="";//支付宝交易号
				String x = ";";
				if("PAY_TYPE02".equals(mapParm.getValue("PAYTYPE", i))){
				if (!"".equals(remark02) && !"#".equals(remark02)) {// 存在卡类型和卡号
					String[] strArray = remark02.split("#");
					String card_Type[] = strArray[0].split(";");// 卡类型
					String reMark[] = strArray[1].split(";");// 卡号
					for (int m = 0; m < card_Type.length; m++) {
						if (null != card_Type[m] && !"".equals(card_Type[m])) {
							String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
									+ card_Type[m]
									+ "' AND GROUP_ID='SYS_CARDTYPE'";
							TParm cardParm = new TParm(TJDODBTool.getInstance()
									.select(cardsql));
							if (null != reMark[m] && !"".equals(reMark[m])) {
								cardtypeString = cardtypeString
										+ cardParm.getValue("CHN_DESC", 0) + ":"
										+ reMark[m];
							} else {
								cardtypeString = cardtypeString
										+ cardParm.getValue("CHN_DESC", 0) + ":"
										+ "";
							}
						}else{
							continue;
						}
					}
					payDetail+=cardtypeString;
				}
				}
				if("PAY_TYPE09".equals(mapParm.getValue("PAYTYPE", i))){
				if (!"".equals(remark09) && !"#".equals(remark09)) {// 存在卡类型和卡号
					String[] strArray = remark09.split("#");
					String card_Type[] = strArray[0].split(";");// 卡类型
					String reMark[] = strArray[1].split(";");// 备注
					wxBusinessNo=parm4.getValue("WX_BUSINESS_NO",0);
					for (int m = 0; m < card_Type.length; m++) {
						if (null != card_Type[m] && !"".equals(card_Type[m])) {
							String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
									+ card_Type[m]
									+ "' AND GROUP_ID='SYS_CARDTYPE'";
							TParm cardParm = new TParm(TJDODBTool.getInstance()
									.select(cardsql));
							if (null != reMark[m] && !"".equals(reMark[m])) {
								wxCardtypeString = wxCardtypeString
										+ cardParm.getValue("CHN_DESC", 0) + " 备注:"
										+ reMark[m];
							} else {
								wxCardtypeString = wxCardtypeString
										+ cardParm.getValue("CHN_DESC", 0);
							}
							if(wxBusinessNo.length()>0){
								wxCardtypeString=wxCardtypeString+" 交易号:"+wxBusinessNo;
							}
						}else{
							continue;
						}
					}
					payDetail+=wxCardtypeString;
				}
				}
				if("PAY_TYPE10".equals(mapParm.getValue("PAYTYPE", i))){
				if (!"".equals(remark10) && !"#".equals(remark10)) {// 存在卡类型和卡号
					String[] strArray = remark10.split("#");
					String card_Type[] = strArray[0].split(";");// 卡类型
					String reMark[] = strArray[1].split(";");// 备注
					zfbBusinessNo=parm4.getValue("ZFB_BUSINESS_NO",0);
					for (int m = 0; m < card_Type.length; m++) {
						if (null != card_Type[m] && !"".equals(card_Type[m])) {
							String cardsql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE ID='"
									+ card_Type[m]
									+ "' AND GROUP_ID='SYS_CARDTYPE'";
							TParm cardParm = new TParm(TJDODBTool.getInstance()
									.select(cardsql));
							if (null != reMark[m] && !"".equals(reMark[m])) {
								zfbCardtypeString = zfbCardtypeString
										+ cardParm.getValue("CHN_DESC", 0) + " 备注:"
										+ reMark[m];
							} else {
								zfbCardtypeString = zfbCardtypeString
										+ cardParm.getValue("CHN_DESC", 0);
							}
							if(zfbCardtypeString.length()>0){
								zfbCardtypeString=zfbCardtypeString+" 交易号:"+zfbBusinessNo;
							}
						}else{
							continue;
						}
					}
					payDetail+=zfbCardtypeString;
				}
				}
			}
		}
		//==end==modify by kangy20171019 刷卡微信支付宝添加卡类型备注交易号

/*		if(!"".equals(payDetail)){
			payDetail = payDetail.substring(1, payDetail.length());
		}
		oneReceiptParm.setData("PAY_DETAIL", payDetail);*/
		//add by sunqy 20140827 为收据添加支付细项 ----end----
		//收费为刷卡时，收据添加卡号和卡类型 add by huangjw 20141230  start
/*		if(!"".equals(card_type)&&!"#".equals(card_type)){
			String [] str=card_type.split("#");
			String [] str1=str[0].split(";");
//			String [] str2=str[1].split(";");
			
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			
			String cardtypeString="";
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
				if(!"".equals(parm4.getValue("PAY_TYPE02",0)) && parm4.getValue("PAY_TYPE02",0)!=null && parm4.getDouble("PAY_TYPE02",0)!=0){
				cardtypeString=cardtypeString+cardParm.getValue("CHN_DESC",0)+" ";
				
				if(str2 != null){
					if(m < str2.length ){
						cardtypeString=cardtypeString+str2[m]+" ";
					}
				}
			}
			}
			oneReceiptParm.setData("CARD_TYPE",cardtypeString); //modify by kangy 20171019 
		}*/
		oneReceiptParm.setData("CARD_TYPE",""); //modify by kangy 20171019 
		if(!"".equals(payDetail)){
			payDetail = payDetail.substring(1, payDetail.length());
		}
		oneReceiptParm.setData("PAY_DETAIL", payDetail);
		boolean memPackageFlg = this.getValueBoolean("MEM_PACKAGE");
		//收费为刷卡时，收据添加卡号和卡类型 add by huangjw 20141230  end
		//		if(parm3.getCount() > 0){//add by sunqy 20140827 引用套餐的情况标题改为"门诊结算收据（套餐）"
		String dept_package_session = "科室："+deptCode.getText()+"   ";
		if(!memPackageFlg){
			if(recpParm.getValue("ADM_TYPE").equals("E")){
				oneReceiptParm.setData("TITLE", "TEXT", "急诊结算收据");
			}else {
				oneReceiptParm.setData("TITLE", "TEXT", "门诊结算收据");
			}
			
			oneReceiptParm.setData("CHN1","优 惠 金 额");
			oneReceiptParm.setData("DIFFERENCE",df.format(difference));

		}else{
			oneReceiptParm.setData("TITLE", "TEXT", "门诊结算收据【套餐结转】");
			oneReceiptParm.setData("CHN1","结 转 金 额");
			oneReceiptParm.setData("DIFFERENCE",ownParm.getDouble("AR_AMT", 0));

			//add by huangtt 20150714 添加套餐名称与时程名称 -----start
			String memSql = "SELECT B.PACKAGE_DESC, B.SECTION_DESC,D.CTZ_DESC" +
					" FROM OPD_ORDER A, MEM_PAT_PACKAGE_SECTION_D B,SYS_CTZ D" +
					" WHERE A.CASE_NO = '"+opb.getReg().caseNo()+"'" +
					" AND A.MEM_PACKAGE_FLG = 'Y'" +
					" AND A.RECEIPT_NO ='" +recpParm.getData("RECEIPT_NO").toString()+"'"+
					" AND A.MEM_PACKAGE_ID = B.ID" +
					" AND A.CTZ1_CODE=D.CTZ_CODE" +
					" GROUP BY B.PACKAGE_DESC, B.SECTION_DESC,D.CTZ_DESC " +
					" ORDER BY B.PACKAGE_DESC, B.SECTION_DESC";
			TParm memParm = new TParm(TJDODBTool.getInstance().select(memSql));
			List<String> packageCodeList = new ArrayList<String>();
			List<String> sessionCodeList = new ArrayList<String>();
			List<String> ctzCodeList = new ArrayList<String>();
			for (int i = 0; i < memParm.getCount(); i++) {

				if(!packageCodeList.contains(memParm.getValue("PACKAGE_DESC", i))){
					packageCodeList.add(memParm.getValue("PACKAGE_DESC", i));
				}
				if(!sessionCodeList.contains(memParm.getValue("SECTION_DESC", i))){
					sessionCodeList.add(memParm.getValue("SECTION_DESC", i));
				}
			}

			dept_package_session += "套餐名称:";
			for (int i = 0; i < packageCodeList.size(); i++) {
				dept_package_session = dept_package_session+ packageCodeList.get(i)+"、";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);
			dept_package_session += "  时程名称:";
			for (int i = 0; i < sessionCodeList.size(); i++) {
				dept_package_session = dept_package_session + sessionCodeList.get(i)+"、";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);

			//add by huangtt 20150714 添加套餐名称与时程名称 -----end
		}
		dept_package_session=dept_package_session+" 就诊身份:"+parm.getValue("CTZ_DESC");
		TParm T1 = new TParm();
		T1.addData("MEM_CODE", dept_package_session);
		T1.setCount(1);
		T1.addData("SYSTEM", "COLUMNS","MEM_CODE");
		oneReceiptParm.setData("MEMTABLE",T1.getData());
		String ptrSwitch = new IReportTool().getPrintSwitch("OPBRECTPrint.prtSwitch");
		//		System.out.println("oneReceiptParm========="+oneReceiptParm);
		if(ptrSwitch.equals(IReportTool.ON)){
			//20141223 wangjingchun add start 854
			String patinfo_sql = "SELECT VALID_FLG, DEPT_FLG FROM MEM_INSURE_INFO WHERE MR_NO = '"+pat.getMrNo()+"'";
			TParm patinfo_parm = new TParm(TJDODBTool.getInstance().select(patinfo_sql));
			int i = 1;
			if(patinfo_parm.getValue("DEPT_FLG",0).equals("N")&&patinfo_parm.getValue("VALID_FLG",0).equals("Y")){
				i=2;
			}
			for(int j=0;j<i;j++){
				//				oneReceiptParm.setData("sssss", "ddddd");
				this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
						IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm));//报表合并modify by wanglong 20130730
			}
			//20141223 wangjingchun add end 854
		}
		return;

	}

	/**
	 * 现金打票明细入参
	 * 
	 * @param oneReceiptParm
	 *            TParm
	 * @param recpParm
	 *            TParm
	 * @param dparm
	 *            TParm
	 */
	private void onPrintCashParm(TParm oneReceiptParm, TParm recpParm,
			TParm dparm) {
		String receptNo = recpParm.getData("RECEIPT_NO").toString();
		dparm.setData("NO", receptNo);
		TParm tableresultparm = OPBTool.getInstance().getReceiptDetail(dparm);
		if (tableresultparm.getCount() > 10) {
			oneReceiptParm.setData("DETAIL", "TEXT", "(详见费用明细表)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * 权限检核
	 * 
	 * @param orderParm
	 *            TParm
	 * @return boolean
	 */
	public boolean chekeRolo(TParm orderParm) {
		int count = orderParm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (this.getPopedem("LEADER")) {
				if (!Operator.getDept().equals(
						orderParm.getValue("EXEC_DEPT_CODE", i))
						&& orderParm.getDouble("DOSAGE_QTY", i) < 0) {
					this.messageBox("不同科室不可输入负值!");
					return false;
				}
			} else {
				if (orderParm.getDouble("DOSAGE_QTY", i) < 0) {
					this.messageBox("不可输入负值!");
					return false;
				}
			}
		}
		return true;

	}

	/**
	 *医嘱类别改变
	 */
	public void onChangeCat1Type() {
		this.setValue("PRESCRIPTION", "");
		if (table != null) {
			table.acceptText();
		}
		checkBoxNotCharge.setSelected(true);
		onNotCharge();
	}

	/**
	 * 重新计算费用
	 * 
	 * @return double
	 */
	public double getFee() {
		double fee = 0.00;
		double allFee = 0.00;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int tableCount = tableParm.getCount("AR_AMT");
		for (int i = 0; i < tableCount; i++) {
			if ("Y".equals(tableParm.getValue("CHARGE", i))
					&& tableParm.getValue("CHARGE", i).length() != 0) {
				fee = tableParm.getDouble("AR_AMT", i);
				allFee = allFee + fee;
			}
		}
		return Double.parseDouble(df.format(allFee));
	}

	/**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) {
		if (!"OPB".equalsIgnoreCase(prgId)) {
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x", parm);
		if ("OK".equals(flg)) {
			this.onClear();
			return "OK";
		}
		return "";
	}

	/**
	 * 读取数据 ================pangben modify 20110815 获得的价格可以保存到数据中
	 */
	public void readXML() {
		// TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzcfsj.xml");
		TParm parm = NJCityInwDriver.getPame("c:/mzcfsj.xml");
		double sum = 0.0;
		for (int i = 0; i < parm.getCount(); i++) {
			String temp = parm.getValue("DJ").substring(1,
					parm.getValue("DJ").indexOf("]"));
			Double price = new Double(temp);
			temp = parm.getValue("SL").substring(1,
					parm.getValue("SL").indexOf("]"));
			int count = new Integer(temp);
			sum += price * count;

		}
		feeShow = true; // 管控
		this.setValue("TOT_AMT", sum); // 收费

		paymentTool.setAmt(sum);
	}

	/**
	 * 泰心读取医疗卡操作
	 */
	public void txReadEKT() {
		// parmEKT= TXreadEKT("MR_NO");
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		// 医疗卡打印
		callFunction("UI|ektPrint|setEnabled", true);
		if (insParm != null) {
			// 医保结算打印
			callFunction("UI|insPrint|setEnabled", true);
		}
		isEKT = true; // 医疗卡操作
		this.setValue("PAY_MEDICAL_CARD", parmEKT.getDouble("CURRENT_BALANCE"));
		if(!this.getValueBoolean("tRadioButton_0")){
			this.setValue("BILL_TYPE", "E");
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		setValue("EKT_CURRENT_BALANCE", parmEKT
				.getDouble("CURRENT_BALANCE"));
		onQuery();
		// ===zhangp 20120309 modify start
		if (!systemCode.equals("") && "ONW".equals(systemCode)) {
			ektTCharge.setEnabled(false);
		} else {
			ektTCharge.setEnabled(true);
			// ===ZHANGP 20120319 start
			// ================chenxi modify start 2012.05.21
			double surrentBalance = parmEKT.getDouble("CURRENT_BALANCE");
			double totAmt = getValueDouble("TOT_AMT");
			setValue("AMT", StringTool.round((surrentBalance - totAmt), 2));
			// callFunction("UI|AMT|setValue", StringTool.round((surrentBalance
			// - totAmt),
			// 2));
			// ========================chenxi modify stop 2012.05.21
			// ===ZHANGP 20120319 start
		}
		// ===zhangp 20120309 modify end
		checkBoxChargeAll.setSelected(true);
		//add by huangtt 20141226
		if(opb.checkOrderCount()){
			return;
		}
		this.onSelectAll();
	}

	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 读医保卡======pangben 2011-11-29
	 */
	public void readINSCard() {
		TJreadINSCard();
	}

	/**
	 * 泰心医院医保卡读卡操作 =============pangben 20111129
	 */
	private void TJreadINSCard() {
		// String mrNo = "000000001116";
		// if (!isEKT) {
		//
		// this.messageBox("请先获得医疗卡信息");
		// return;
		// }
		if (null == pat || null == pat.getMrNo() || pat.getMrNo().length() <= 0
				|| null == reg) {
			this.messageBox("请获得病患信息");
			return;
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("INS_TYPE", reg.getInsPatType()); // 就医类别 1.城职普通2.城职门特
		// 挂号不是医保操作,在收费时不可以执行医保收费 提示不能执行医保操作
		if (null == reg.getConfirmNo() || reg.getConfirmNo().length() <= 0) {
			this.messageBox("此次就诊病患不是医保挂号,不能执行医保收费操作");
			return;
		}
		// 查询是否存在特批款操作
		TParm greenParm = new TParm();
		greenParm.setData("CASE_NO", reg.caseNo());
		greenParm = PatAdmTool.getInstance().selEKTByMrNo(greenParm);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (greenParm.getDouble("GREEN_BALANCE", 0) > 0) {
			this.messageBox("此就诊病患使用特批款,不可以使用医保操作");
			return;
		}
		// 3.城居门特
		parm.setData("CARD_TYPE", 3); // 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院,5 :门特登记）
		parm.setData("INS_TYPE", reg.getInsPatType());// 挂号医保就诊类别
		insParm = null;
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null==insParm || null==insParm.getValue("RETURN_TYPE")) {
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType != 1) {
			this.messageBox("读取医保卡失败");
			insParm = null;
			return;
		}
		// ===zhangp 20120408 start
		String insType = insParm.getValue("INS_TYPE"); // 医保就诊类型: 1.城职普通 2.城职门特
		// 3.城居门特
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		// ============pangben 查询数据是否存在
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID") + "' AND PAT_NAME='"
					+ name + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID") + "' AND PAT_NAME='"
					+ name + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
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
		// ===zhangp 20120408 end
		if (parmEKT != null && null != parmEKT.getValue("MR_NO")
				&& parmEKT.getValue("MR_NO").length() > 0) {
			// 医保结算打印
			callFunction("UI|insPrint|setEnabled", true);
			this.callFunction("UI|ektPrint|setEnabled", false);
		}
		// 需要校验身份证号码是否相同 不相同说明不是本人的医保卡 不能操作挂号
		// 判断人群类别
		// insFlg = true;// 医保卡读取成功
		this.setValue("BILL_TYPE", "I"); // 支付方式修改
	}

	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param opbParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(TParm opbParm, boolean flg) {
		// TParm insParm = new TParm();
		TParm insFeeParm = new TParm();
		if (null == reg.caseNo()) {
			return null;
		}
		double totAmt = 0.00; // 获得要收费的医嘱------集合医嘱所有医嘱都会显示
		TParm newOpbParm = new TParm();
		for (int i = 0; i < opbParm.getCount("ORDER_CODE"); i++) {
			if (null == opbParm.getValue("ORDERSET_CODE", i)
					|| !opbParm.getValue("ORDERSET_CODE", i).equals(
							opbParm.getValue("ORDER_CODE", i))) {
				totAmt += opbParm.getDouble("OWN_AMT", i);
				newOpbParm.addRowData(opbParm, i);
			}
		}
		insFeeParm.setData("CASE_NO", reg.caseNo()); // 退挂使用
		insFeeParm.setData("RECP_TYPE", "OPB"); // 收费使用
		insFeeParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 医保就诊号
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // 病患号
		insFeeParm.setData("FeeY", totAmt); // 应收金额
		insFeeParm.setData("PAY_TYPE", isEKT); // 支付方式
		insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		insExeParm(newOpbParm);
		insFeeParm.setData("REGION_CODE", insParm.getValue("REGION_CODE")); // 医保区域
		insFeeParm.setData("insParm", insParm.getData());
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
				insFeeParm);
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
		if (returnType == 1) {
			return returnParm;
		} else {
			return null;
		}
	}

	/**
	 * 医保扣款数据
	 * 
	 * @param newOpbParm
	 *            TParm
	 */
	private void insExeParm(TParm newOpbParm) {
		insParm.setData("REG_PARM", newOpbParm.getData()); // 所有要分割的医嘱
		insParm.setData("MR_NO", pat.getMrNo()); // 病患号
		insParm.setData("PAY_KIND", "11"); // 4 支付类别:11门诊、药店21住院//支付类别12
		insParm.setData("CASE_NO", reg.caseNo()); // 就诊号
		insParm.setData("RECP_TYPE", "OPB"); // 就诊类别
		insParm.setData("OPT_USER", Operator.getID()); // 区域代码
		// insParm.setData("REG_PARM", parm.getData());
		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // 科室代码
		insParm.setData("REG_TYPE", "0"); // 挂号标志:1 挂号0 非挂号
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("OPBEKTFEE_FLG", "Y"); // 门诊医疗卡收费注记----扣款打票时使用用来操作 收据表
		// BIL_OPB_RECP 医保金额修改
		insParm.setData("PRINT_NO", this.getValue("UPDATE_NO")); // 票号
		insParm.setData("DR_CODE", this.getValue("DR_CODE")); // 医生代码
		if (reg.getAdmType().equals("E")) {
			insParm.setData("EREG_FLG", "1"); // 急诊
		} else {
			insParm.setData("EREG_FLG", "0"); // 普通
		}
	}

	/**
	 * 特殊人员类别
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "伤残军人";
		if ("06".equals(type))
			return "公务员";
		if ("07".equals(type))
			return "民政救助人员";
		if ("08".equals(type))
			return "优抚对象";
		return "";
	}

	/**
	 * 医疗卡操作保存此次医保卡扣款金额
	 * 
	 * @param accountAmt
	 *            double
	 * @param readCardParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param business_type
	 *            String
	 * @param type
	 *            int
	 * @return TParm
	 */
	private TParm insExeUpdate(double accountAmt, TParm readCardParm,
			String caseNo, String business_type, int type) {
		// 入参:AMT:本次操作金额 BUSINESS_TYPE :本次操作类型 CASE_NO:就诊号码
		TParm orderParm = new TParm();
		orderParm.setData("AMT", -accountAmt);
		orderParm.setData("BUSINESS_TYPE", business_type);
		orderParm.setData("CASE_NO", caseNo);
		orderParm.setData("EXE_FLG", "Y");
		orderParm.setData("TYPE", type);
		orderParm.setData("readCard", readCardParm.getData());
		orderParm.setData("OPT_USER", Operator.getID());
		orderParm.setData("OPT_TERM", Operator.getIP());
		TParm insExeParm = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"exeInsSave", orderParm);
		return insExeParm;

	}

	/**
	 * 手术室补充计费
	 */
	public void onOperation() {
		if (this.getValueString("ALL").equals("Y")||this.getValueString("EKT_R").equals("Y")) {
			this.messageBox("请不要点选全部或卡退费");
			return ;
		}
		operationParm = new TParm();
		TParm parm = new TParm();
		parm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", parm,
				false);
		if (null==operationParm) {//==pangben  2013-08-05
			return;
		}
		TParm parm_obj = new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
					+ operationParm.getValue("ORDER_CODE", i) + "' ";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm_obj == null || parm_obj.getCount() <= 0) {
				continue;
			}
			//医嘱是      1医师专用， 跳过，不能加入
			if(parm_obj.getRow(0).getValue("USE_CAT").equals("1")){
				continue;
			}
			// TParm rowParm = parm_obj.getRow(0);
			// rowParm.setData("",operationParm.getDouble("DOSAGE_QTY", i));
			insertNewOperationOrder(parm_obj.getRow(0), operationParm
					.getDouble("DOSAGE_QTY", i),operationParm.getValue("DOSAGE_UNIT", i));
		}
	}

	/**
	 * 手术套餐回传新增
	 * 
	 * @param parm
	 *            TParm
	 * @param dosage_qty
	 *            double
	 */
	private void insertNewOperationOrder(TParm parm, double dosage_qty,String dosage_unit) {
		int selectRow = table.getRowCount() - 1;
		newReturnOrder(parm,selectRow,dosage_qty,dosage_unit,"STAT",false);//add this.getFreqCode(parm) caoyong 20131218

	}
	/**
	 * 医疗卡充值
	 * yanjing
	 * 20130510
	 */
	public void onFee() {
		TParm parm =new TParm();
		parm.setData("FLG","Y");
		parm = (TParm) openDialog("%ROOT%\\config\\ekt\\EKTTopUp.x",
				parm);
		this.onClear();
	}
	/**
	 * 向对应的门诊药房发送消息
	 * =======pangben 2013-5-13 
	 */
	public void sendMedMessages() {
		client1 = SocketLink
				.running("","ODO", "ODO");
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		String [] phaArray=new String [0];
		if(phaRxNo.length()>0){//获得所有操作的处方签号码 发送数据
			phaArray=phaRxNo.split(",");
		}
		for (int i = 0; i < phaArray.length; i++) {
			client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER 表添加数据
					+ phaArray[i] + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		}
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000002" + "|MR_NO:000022222222|PAT_NAME:李磊");
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000003" + "|MR_NO:000022222222|PAT_NAME:王浩");
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000004" + "|MR_NO:000033333333|PAT_NAME:张涛");
		if (client1 == null)
			return;
		client1.close();
	}

	/**
	 * 套餐点击事件
	 * ====zhangp
	 */
	public void onChangeMemPackageFlg(){
		if(null== reg || null == reg.caseNo() || reg.caseNo().length()<=0){
			this.messageBox("未获得就诊信息");
			return;
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		TParm parm = new TParm();
		TParm memTradeParm = new TParm();
		reduceResult = new TParm();
		TTextFormat memCombo = (TTextFormat) getComponent("MEM_COMBO");
		TTextFormat memTrade = (TTextFormat) getComponent("MEM_TRADE");
		memTrade.setValue("");
		if(mem_package.isSelected()){
			this.callFunction("UI|save|setEnabled", false);
			this.callFunction("UI|CHARGE|setEnabled", false);
			callFunction("UI|ektPrint|setEnabled", true);
			String sql = 
					" SELECT ID, PACKAGE_DESC NAME" +
							" FROM MEM_PAT_PACKAGE_SECTION_D" +
							" WHERE CASE_NO = '" + reg.caseNo() + "' ";
			parm = new TParm(TJDODBTool.getInstance().select(sql));
			sql = " SELECT TRADE_NO ID , PACKAGE_DESC NAME" +
					" FROM MEM_PAT_PACKAGE_SECTION_D" +
					" WHERE CASE_NO = '" + reg.caseNo() + "' GROUP BY TRADE_NO,PACKAGE_DESC";
			memTradeParm = new TParm(TJDODBTool.getInstance().select(sql));
			this.callFunction("UI|MEM_TRADE|setEnabled", true);
		}else{
			this.callFunction("UI|save|setEnabled", true);
			this.callFunction("UI|CHARGE|setEnabled", true);
			callFunction("UI|ektPrint|setEnabled", false);
			parm = new TParm();
			parm.setCount(-1);
			parm.addData("SYSTEM", "COLUMNS", "ID");
			parm.addData("SYSTEM", "COLUMNS", "NAME");
			memTradeParm = new TParm();
			memTradeParm.setCount(-1);
			memTradeParm.addData("SYSTEM", "COLUMNS", "ID");
			memTradeParm.addData("SYSTEM", "COLUMNS", "NAME");
			this.callFunction("UI|MEM_TRADE|setEnabled", false);
		}

		
		try {
			memCombo.setHorizontalAlignment(2);
			memCombo.setPopupMenuHeader("代码,100;名称,100");
			memCombo.setPopupMenuWidth(300);
			memCombo.setPopupMenuHeight(300);
			memCombo.setFormatType("combo");
			memCombo.setShowColumnList("NAME");
			memCombo.setValueColumn("ID");
			memCombo.setPopupMenuData(parm);
			
			memTrade.setHorizontalAlignment(2);
			memTrade.setPopupMenuHeader("代码,100;名称,200");
			memTrade.setPopupMenuWidth(300);
			memTrade.setPopupMenuHeight(300);
			memTrade.setFormatType("combo");
			memTrade.setShowColumnList("NAME");
			memTrade.setValueColumn("ID");
			memTrade.setPopupMenuData(memTradeParm);
			memTrade.onQuery();
		} catch (Exception e) {
			// TODO: handle exception
		}

		table.addItem("MEM_COMBO", memCombo);

		TRadioButton all = (TRadioButton) getComponent("ALL");
		TRadioButton notcharge = (TRadioButton) getComponent("NOTCHARGE");
		TRadioButton ekt_r = (TRadioButton) getComponent("EKT_R");
		if(all.isSelected()){
			onAll();
		}
		if(notcharge.isSelected()){
			onNotCharge();
		}
		if(ekt_r.isSelected()){
			onEKTCharge(0);
		}
		if(un_exec.isSelected()){
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setRowColor(i, null);
			}
		}
		table.setLockCellColumn(17, true);//lockColumns对下拉根本无效！只能这么写
	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {

		//delete by huangtt 20141126
		//		unLockPat();		
		return true;
	}

	public void onPayOther3(){	
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = 0;
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("TOT_AMT");
		if(getValueDouble("PAY_OTHER4") == 0){
			payOther4 = arAmt - payOther3;
			setValue("PAY_OTHER4", df.format(payOther4) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -3){
			setValue("PAY_OTHER4", df.format(payOtherTop4) );
		}
		onPayOther();
	}

	public void onPayOther4(){		
		double payOther3 = 0;
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double arAmt = getValueDouble("TOT_AMT");
		if(getValueDouble("PAY_OTHER3") == 0){
			payOther3 = arAmt - payOther4;
			setValue("PAY_OTHER3", df.format(payOther3) );
		}
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		if(result.getErrCode() == -2){
			setValue("PAY_OTHER3", df.format(payOtherTop3));
		}
		onPayOther();
	}

	public boolean onPayOther(){
		double payOther3 = getValueDouble("PAY_OTHER3");
		double payOther4 = getValueDouble("PAY_OTHER4");
		double payOtherTop3 = getValueDouble("GIFT_CARD");
		double payOtherTop4 = getValueDouble("GIFT_CARD2");
		double payCashTop = getValueDouble("NO_PAY_OTHER_ALL");
		double arAmt = getValueDouble("TOT_AMT");
		double payCash = Double.parseDouble(df.format(arAmt - payOther3 - payOther4));
		TParm result = EKTpreDebtTool.getInstance().checkPayOther(payOther3, payOther4, arAmt, payOtherTop3, payOtherTop4);
		System.out.println(reg.caseNo()+"-----onPayOther==payOther3=="+payOther3+"-----payOtherTop3==="+payOtherTop3);
		System.out.println(reg.caseNo()+"-----onPayOther==payOther4=="+payOther4+"-----payOtherTop4==="+payOtherTop4);
		System.out.println(reg.caseNo()+"-----onPayOther==payCashTop=="+payCashTop+"-----arAmt==="+arAmt);
		System.out.println(reg.caseNo()+"-----onPayOther==payCash=="+payCash);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			setValue("PAY_OTHER3", 0);
			setValue("PAY_OTHER4", 0);
			return true;
		}
		if(payCash > payCashTop){
			String sql =
					" SELECT GREEN_PATH_TOTAL FROM REG_PATADM WHERE CASE_NO = '" + reg.caseNo() + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm.getDouble("GREEN_PATH_TOTAL", 0) == 0){
				messageBox("现金不足");
				System.out.println(reg.caseNo()+"-----payCash===="+payCash+"-----payCashTop==="+payCashTop);
				setValue("PAY_OTHER3", 0);
				setValue("PAY_OTHER4", 0);
				return true;
			}
		}
		setValue("NO_PAY_OTHER", payCash);
		return false;
	}
	/**
	 * 腕带打印
	 * @deprecated
	 */
	public void onWrist111() {
		if (this.getValueString("MR_NO").length() == 0 || 
				this.getValueString("PAT_NAME").length() == 0 ||
				this.getValueString("SEX_CODE").length() == 0) {
			this.messageBox("不能有选项为空,请重新填写!");
			return;
		}
		String a_number = getValueString("MR_NO");
		String a_name = getValueString("PAT_NAME");
		String a_gender = getValueString("SEX_CODE");
		String sql = "SELECT BIRTH_DATE,SEX_CODE FROM SYS_PATINFO WHERE MR_NO = '"+a_number+"'";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String time = parm1.getValue("BIRTH_DATE");
		if(a_gender.equals("1")){
			a_gender="男";
		}
		if(a_gender.equals("2")){
			a_gender="女";
		}
		if(a_gender.equals("9")){
			a_gender="未说明";
		}
		if(a_gender.equals("0")){
			a_gender="未知";
		}

		TParm print = new TParm();
		print.setData("MR_NO", "TEXT", a_number);
		print.setData("PAT_NAME", "TEXT", a_name);
		print.setData("SEX_CODE", "TEXT", a_gender);
		//print.setData("BIRTH_DATE", "TEXT", StringTool.getString(a_time,"yyyy/MM/dd"));
		print.setData("BIRTH_DATE", "TEXT", time.substring(1, 11).replace("-", "/"));
		//        System.out.println("print=="+print);
		//        System.out.println("---00000----");
		this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBChargesM.jhw", print);
		//        this.openPrintDialog(IReportTool.getInstance().getReportPath("REGPatAdm.jhw"),
		//                             IReportTool.getInstance().getReportParm("REGPatAdm.class", print));//报表合并modify by wanglong 20130730
	}
	
	/**
	 * 腕带打印
	 */
	public void onWrist() {
		TParm print = new TParm();
		// 得到姓名
		print.setData("PatName", "TEXT", "姓名:" + pat.getName());
		// 得到病案号
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		// 得到性别
		print.setData("Sex", "TEXT", "性别:" + pat.getSexString());
		// 得到出生日期
		print.setData("BirthDay", "TEXT", "出生日期:" + StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		// 得到科室
		print.setData("Dept", "TEXT",
				"科室:" + StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + reg.getRealdeptCode() + "'"));
		this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristAdult.jhw", print, true);
	}

	public void changeChargeText(){
		if(ektTCharge.isSelected()){
			charge.setText("退费");
		}else{
			charge.setText("收费");
		}
	}

	public void onGatherChange(int t){

		boolean b = false;
		String lockRow = "";


		switch (t) {
		case 0:
			clearValue("PAY_OTHER4;PAY_OTHER3;PAY;");
			setValue("BILL_TYPE", "C");
			double amt = getValueDouble("TOT_AMT");
			paymentTool.setAmt(amt);
			break;
		case 1:
			b = true;
			lockRow = "0";
			setValue("BILL_TYPE", "E");
			paymentTool.onClear();
			break;
		}

		TNumberTextField payOther4 = (TNumberTextField) getComponent("PAY_OTHER4");
		TNumberTextField payOther3 = (TNumberTextField) getComponent("PAY_OTHER3");
		TNumberTextField pay = (TNumberTextField) getComponent("PAY");

		payOther4.setEnabled(b);
		payOther3.setEnabled(b);
		pay.setEnabled(b);

		paymentTool.table.setLockRows(lockRow);
	}

	/**
	 * 患者保险信息 add by huangtt 20140812
	 */
	public void onInsureinfo(){
		String mrNo = this.getValueString("MR_NO");
		if(mrNo == null || mrNo.length()<=0){
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("EDIT", "N");
		this.openDialog("%ROOT%\\config\\mem\\MEMInsureInfo.x",parm);
	}
	/**
	 * 查询是否有特批款  add by huangtt 20141009
	 * @return
	 */
	public boolean onGreenBalance(){
		String caseNo = reg.caseNo();
		String sql = "SELECT GREEN_PATH_TOTAL,GREEN_BALANCE FROM REG_PATADM WHERE CASE_NO = '"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		double greenTotal = parm.getDouble("GREEN_PATH_TOTAL", 0);
		if(greenTotal > 0){
			return true;
		}else{
			return false;
		}
	}

	public void onOrderPackComparison(){
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		this.openDialog("%ROOT%\\config\\opb\\OPBOrderPackageComparison.x",opbParm);
		this.onClear();
		//    	txReadEKT();
		//add by huangtt 20150127
		this.setValue("MR_NO", opbParm.getValue("MR_NO"));
		this.onQuery();
	}

	/**
	 * 取得所有医嘱   add by huangtt 20141126
	 * 
	 * @return
	 */
	public TParm getOrder(){
		String sql = "SELECT CASE_NO,ORDER_CODE,DOSAGE_QTY,OWN_AMT,BILL_FLG,CTZ1_CODE," +
				" PHA_CHECK_DATE,PHA_DOSAGE_DATE,PHA_DISPENSE_DATE,RX_NO,SEQ_NO,REQUEST_FLG,REQUEST_NO,BUSINESS_NO " +
				" FROM OPD_ORDER WHERE CASE_NO='"
				+ opb.getReg().caseNo() + "' ORDER BY ORDER_CODE";
		// System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

	/**
	 * 保存前进行新旧医嘱比对  add by huangtt 20141126
	 * 
	 * @param newOpdOrderParm
	 * @return
	 */
	public boolean orderComparison(TParm newOpdOrderParm){
		String [] valueName = newOpdOrderParm.getNames();
		if(oldOpdOrderParm.getCount() != newOpdOrderParm.getCount()){
			this.messageBox(MESSAGE);
			return false;
		}else{
			List<Integer> temp = new ArrayList<Integer>();
			for (int i = 0; i < oldOpdOrderParm.getCount(); i++) {
				for (int j = 0; j < newOpdOrderParm.getCount(); j++) {
					if(oldOpdOrderParm.getValue("RX_NO", i).equalsIgnoreCase(newOpdOrderParm.getValue("RX_NO", j))
							&& oldOpdOrderParm.getValue("SEQ_NO", i).equalsIgnoreCase(newOpdOrderParm.getValue("SEQ_NO", j))){
						if(!temp.contains(i)){
							temp.add(i);
						}								
					}
				}
			}
			for (int i = 0; i < oldOpdOrderParm.getCount(); i++) {
				if(!temp.contains(i)){
					this.messageBox(MESSAGE);
					return false;
				}
			}
			for (int i = 0; i < newOpdOrderParm.getCount(); i++) {
				String orderCode = newOpdOrderParm.getValue("ORDER_CODE", i);
				String rxNo = newOpdOrderParm.getValue("RX_NO", i);
				String seqNo = newOpdOrderParm.getValue("SEQ_NO", i);
				int row = -1;
				for (int j = 0; j < oldOpdOrderParm.getCount(); j++) {
					if(orderCode.equals(oldOpdOrderParm.getValue("ORDER_CODE", j))
							&& rxNo.equals(oldOpdOrderParm.getValue("RX_NO", j))
							&& seqNo.equals(oldOpdOrderParm.getValue("SEQ_NO", j))){
						row =j;
						break;
					}
				}

				if(row == -1){
					this.messageBox(MESSAGE);
					return false;
				}

				for (int k = 0; k < valueName.length; k++) {
					if(!newOpdOrderParm.getValue(valueName[k], i).equalsIgnoreCase(oldOpdOrderParm.getValue(valueName[k], row))){
						this.messageBox(MESSAGE);
						return false;
					}
				}

				oldOpdOrderParm.removeRow(row);				
			}
		}
		return true;
	}
	/**
	 * 减免审批
	 * 20141128 yanjing
	 */
	public void onReduceCheck(){
		//得到表格的数据
		TParm parm = table.getParmValue();
		if(this.getValueDouble("TOT_AMT")==0){
			this.messageBox("请选择要减免的数据");
			return;
		}else if(this.getValueDouble("TOT_AMT")<0){
			this.messageBox("退费不可操作减免");
			return;
		}else{
			reduceResult =  this.onReduce(true, parm);
			if(reduceResult!=null){
				if(reduceResult.getValue("REDUCEFLG").equals("Y")){
					reduceAmt = reduceResult.getDouble("REDUCE_AMT");
					this.setValue("TOT_AMT", reduceResult.getDouble("TOT_AMT", 0));
					if(this.getValueDouble("EKT_CURRENT_BALANCE")!=0.00){
						this.setValue("AMT", StringTool.round(this.getValueDouble("EKT_CURRENT_BALANCE")-this.getValueDouble("TOT_AMT"), 2));
						TRadioButton tb0 = (TRadioButton) getComponent("tRadioButton_0");
						if(tb0.isSelected()){
							paymentTool.onClear();
							paymentTool.setAmt(StringTool.round(reduceResult.getDouble("TOT_AMT", 0),2));

						}
					}
				} 
				TRadioButton tb0 = (TRadioButton) getComponent("tRadioButton_0");
				if(tb0.isSelected()){
					paymentTool.onClear();
					paymentTool.setAmt(StringTool.round(reduceResult.getDouble("TOT_AMT", 0),2));
				}
			}
		}
	}
}
