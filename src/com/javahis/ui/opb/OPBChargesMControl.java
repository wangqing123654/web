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
 * Title:�����շ�ϵͳ
 * </p>
 * 
 * <p>
 * Description:�����շ�ϵͳ
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
	// ��������
	Object paraObject;
	String systemCode = "";
	String onwType="";
	// opb����
	OPB opb;
	// pat����
	Pat pat;
	// reg����
	Reg reg;
	// BIL ����
	BIL opbbil;
	// ������洫��caseNo
	String caseNoPost;
	// ����ΨһcaseNo
	String onlyCaseNo;   
	// ������洫��mrNo
	String mrNoPost;
	// HL7�ӿ�����
	TParm sendHL7Parm;
	// ��ǰѡ�е���
	int selectRow = -1;
	// �Ƿ�����������ǩ
	String pFlg = "N";
	// ��¼ҽ������ҽ��������
	int drOrderCount = -1;
	// �½�orderlist
	OrderList orderList = null;
	// �շ�Ȩ��
	boolean bilRight = true;
	// ����Ƽ�Ȩ��
	boolean addOrder = true;
	// ȫѡ
	TCheckBox checkBoxChargeAll;
	// δ�շ�
	TRadioButton checkBoxNotCharge;
	// EKT�˷�
	TRadioButton ektTCharge;

	// ȫ��
	TRadioButton checkAll;
	// ������ǩ��ֵ
	TComboBox comboPrescription;
	// table
	TTable table;
	// ����ϵͳ��
	String systemName;
	// ����ȼ�
	String serviceLevel;
	// ���׺�
	String tredeNo;
	// ������ҽ���ײ�
	TParm operationParm;
	// ɾ��ҽ��������û�о���ֱ�ӿ���ҽ����Ƶ���޸�Ҳ����ʵ��ɾ��ҽ������
	boolean deleteFun = false;
	// ��ʾɾ����ť
	// ==================pangben modify 20110804 ɾ����ť����
	int drOrderCountTemp = 0;
	boolean drOrderCountFalse = false;
	boolean feeShow = false; // �������Ƿ�ִ����ʾ�������ݱ���
	boolean isbill = false; // �Ƿ����
	private TParm parmEKT; // ��ȡҽ�ƿ���Ϣ
	private boolean EKTmessage = false; // ҽ�ƿ��˷������Ϣ
	private boolean isEKT = false; // ҽ�ƿ�����
	private TParm tredeParm; // ��õ�ǰ�ۿ��Ƿ���ҽ�ƿ�����
	private TParm insParm = new TParm(); // ҽ�����Σ�U ���� A ��������
	private boolean insFlg = false; // ҽ������ȡ����
	private TParm resultBill; // ��������
	private TParm regSysParm;//pangben 2013-4-28 �Һ���Ч����
	private double reduceAmt = 0.00;//������
	// private TParm insMZconfirmParm;// �жϴ˴ξ����Ƿ�ִ��ҽ������
	/**
	 * Socket��������ҩ������
	 */
	private SocketLink client1;
	private String phaRxNo;//===pangben 2013-5-17 ҩƷ��˽����������ƴ���ǩ���� 

	private TButton charge;

	private PaymentTool paymentTool;
	private TParm oldOpdOrderParm; //δ�շѵ�ȫ��ҽ��  huangtt 2141126
	private String MESSAGE = "�ò���ҽ���ѱ䣬�����²�ѯ��";
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
	 * ��ʼ������
	 */
	
	public void onInit() {
		// this.messageBox("Ȩ��"+this.getPopedem("NOBILL"));
		super.onInit();
		charge = (TButton) getComponent("CHARGE");
		regSysParm=REGTool.getInstance().getRegParm();//pangben 2013-4-28 �Һ���Ч����
		paraObject = null;
		paraObject = this.getParameter();
		if (paraObject != null && paraObject.toString().length() > 0) {
			// System.out.println("this.getParameter()+"+this.getParameter());
			TParm paraParm = (TParm) this.getParameter();
			if (paraParm != null && paraParm.getData("SYSTEM") != null) {
				systemCode = paraParm.getValue("SYSTEM");
			}
			if (paraParm != null && paraParm.getData("ONW_TYPE") != null) {
				onwType = paraParm.getValue("ONW_TYPE");//=====pangben 2013-5-15 �ż��ﻤʿվ����ʹ�ã�������ͬ�Ľ���
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
		//====��Ӽ���������Ȩ��  yanjing 20141204  start
		if(this.getPopedem("REDUCE")){
			this.callFunction("UI|reduceCheck|setEnabled", true);
		}else{
			this.callFunction("UI|reduceCheck|setEnabled", false);
		}
		//====��Ӽ���������Ȩ��  yanjing 20141204  start
		//caowl 20140211 end
		checkBoxChargeAll = (TCheckBox) getComponent("CHARGEALL");
		// table1�������¼�
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");

		// table1ֵ�ı��¼�
		this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		// tableר�õļ���
		callFunction("UI|TABLE|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// �˵�tableר�õļ���
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
		// ��ʼ�������ϵ�����
		iniTextValue();
		// ��ʼ��Ȩ��
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
		// ��ʼ��Ʊ��
		// initBilInvoice(bilInvoice.initBilInvoice("OPB"));

		onGatherChange(1);

	}
	/**
	 * �����¼
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
		if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
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
	 * �õ�table
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * ����������У�飬��δִ�е� ��ʾ��Ϣ
	 * @param chargeTable
	 * @param row
	 * @return
	 * pangben 2014-3-18
	 */
	@SuppressWarnings("unused")
	private boolean onChangeTableOnExe(TTable chargeTable ,int row){
		if (checkBoxNotCharge.isSelected()||checkAll.isSelected()) {//=======ȫ����δ�շѵ�ѡ��ť
			if(chargeTable.getParmValue().getValue("CHARGE",row).equals("Y")){
				if (chargeTable.getParmValue().getBoolean("EXEC_FLG", row) || chargeTable.getParmValue().getValue("PHA_CHECK_CODE",row).length()>0) {
				}else{//�����δִ�е�ҽ�������ʾ
					this.messageBox("δִ����Ŀ�����Խ���");
					chargeTable.getParmValue().setData("CHARGE",row,"N");
					table.setParmValue(chargeTable.getParmValue());
					//table.acceptText();
					for (int i = 0; i < orderList.size(); i++) {
						// ȡһ��order
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
	 * table checkbox�����¼�
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
			//======pangben 2013-3-7 �޸Ļ�ʿ����Ʒ� �������ҽ�� �ڶ���Ϊ����ҽ�������ʱ��
			//��ǰ��Ĺ�ѡ���� ��û����ȡ�ڶ���ҽ�����ͻ���� �ڶ���ҽ����ϸ�� �� OPD_ORDER����״̬Ϊ�շ�״̬
			if (chargeTable.getParmValue().getValue("SETMAIN_FLG", row).equals("Y")) {
				List list = opb.getPrescriptionList().getOrder();
				for (int i = 0; i < list.size(); i++) {
					// ȡһ��order
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
	 * ��ʼ�������ϵ�����
	 */
	public void iniTextValue() {
		TParm parm = new TParm();
		// Ԥ���������
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());
		// ��ǰҽ��
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// ��ǰ����
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
				if (getValue("BILL_TYPE").equals("E")) {//pangben 2013-9-18��ʿ���ò���Ʒѣ����治��ʾ
					txReadEKT();
				}
			}
		}

	}

	/**
	 * ����޸�
	 * caowl 
	 * */
	public void onCtzModify(){
		//����Ψһcase_no
		TParm parm = new TParm();
		parm.setData("CASE_NO",opb.getReg().caseNo());
		if(onlyCaseNo==null || onlyCaseNo.equals("")){
			this.messageBox("�����벡����");
			return;

		}		
		openDialog("%ROOT%\\config\\opb\\OPBCTZModify.x", parm);
	}
	/**
	 * �����ӡ
	 * caowl  20140425
	 * */
	public TParm onReduce(boolean flg,TParm orderParm){
		//����Ψһcase_no
		TParm parm = new TParm();
		TParm result = new TParm();
		if(onlyCaseNo==null || onlyCaseNo.equals("")){
			this.messageBox("�����벡����");
			return result;			
		}	
		parm.setData("CASE_NO",onlyCaseNo);
		parm.setData("MR_NO",this.getValue("MR_NO"));
		parm.setData("PAT_NAME",this.getValue("PAT_NAME"));
		parm.setData("SEX_CODE",this.getValue("SEX_CODE"));
		parm.setData("CTZ1_CODE",this.getValue("CTZ1_CODE"));
		parm.setData("RECEIPT_NO",this.getValue("UPDATE_NO"));
		parm.setData("ADM_DATE",this.getValue("STARTTIME"));
		parm.setData("OPB_FLG",flg);//FLG TRUE:ҽ�ƿ���Ʊ   FALSE �ֽ��Ʊ
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
		//			parm.setData("ORDER_PARM",orderParm.getData());//ҽ������
		//			result = (TParm) openDialog("%ROOT%\\config\\bil\\BILOPDReduceCash.x", parm);
		//		}
		return result;
	}
	/**
	 * �ײͲ�ѯ
	 * caowl
	 * */
	public void onQueryPack(){
		TParm parm = new TParm();
		parm.setData("MR_NO",this.getValue("MR_NO"));
		if(this.getValue("MR_NO")==null || this.getValue("MR_NO").equals("")){
			this.messageBox("�����벡����");
			return;			
		}
		openDialog(
				"%ROOT%\\config\\mem\\MEMPackageSalesInfo.x", parm);
	}
	/**
	 * ��ʼ��Ȩ��
	 */
	public void initPopedem() {
		// �շ�Ȩ��
		if (!getPopedem("BILCharge")) {
			// �շ�Ȩ��Ϊtrue
			bilRight = false;
			// ȫѡΪδѡ��
			callFunction("UI|CHARGEALL|setValue", "N");
			// ȫѡcheckbox��
			callFunction("UI|CHARGEALL|setEnabled", false);
			// table��һ���շ�����
			callFunction("UI|TABLE|setLockColumns", "0");
			// ���շѰ�ťΪ���ɱ༭
			callFunction("UI|CHARGE|setEnabled", false);

		}
		// ����Ƽ�Ȩ��
		if (!getPopedem("ADDORDER")) {
			// Ȩ��Ϊfalse
			addOrder = false;
		}
	}

	/**
	 * table ����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		// �����ҽ����ҽ��������,�շ�Ա��Ȩɾ���ͱ༭
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
		// RxType::ҽ������0 OR 7������Ƽ�1������ҩ2������ҩƷ3����ҩ��Ƭ4��������Ŀ5����������Ŀ
		if (null != order
				&& (order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			callFunction("UI|delete|setEnabled", true);
		}
		// ===zhangp 20120414 end
	}

	/**
	 * ���ݲ����Ŵ���������Ϣ
	 */
	public void onQuery() {
		this.setValue("TAX_FLG", "N");
		//delete by huangtt 20141126
		//		if (pat != null) {
		//			this.unLockPat();
		//		}
		// ����Ĭ��û���½�����
		pFlg = "N";
		// ��ʼ��pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			callFunction("UI|record|setEnabled", false);
			return;
		}
		if(pat.isOpbArreagrage()){
			if(JOptionPane.showConfirmDialog(null, "��δ������Ŀ���Ƿ����", "�Ƿ����", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
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
		//		// ���������ʧ�����������
		//		if (!this.lockPat()) {
		//			return;
		//		}
		callFunction("UI|record|setEnabled", true);
		// ���渳ֵ
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());

		//		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
		//				SystemTool.getInstance().getDate()); // showAge(Timestamp birth,
		//		// Timestamp sysdate);
		String age = DateUtil.showAge(pat.getBirthday(), SystemTool.getInstance().getDate());//��������޸ģ�modify by huangjw 20150114
		setValue("AGE", age);
		setValue("BIRTH_DATE",pat.getBirthday());
		
		
		setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(pat.getMrNo()));
		
		// ���Ҿ����¼
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
		// ���Ҵ���
		if (parm.getCount() < 0) {
			messageBox_("�������ѡ�����!");
			return;
		}

		// ���Һ���ϢΪ0
		if (parm.getCount() == 0) {
			//			this.messageBox("�޽��չҺ���Ϣ!");
			// �������ѡ�����
			onRecord();
			return;
		}
		// �������ֻ��һ�ιҺ���Ϣ
		if (parm.getCount() == 1) {
			// ��ʼ��reg
			String caseNo = parm.getValue("CASE_NO", 0);
			reg = Reg.onQueryByCaseNo(pat, caseNo);
			// �жϹҺ���Ϣ
			if (reg == null) {
				return;
			}
			// reg�õ������ݷ������
			afterRegSetValue();
			// ͨ��reg��caseNo�õ�pat
			opb = OPB.onQueryByCaseNo(reg);
			serviceLevel = opb.getReg().getServiceLevel();
			this.setValue("SERVICE_LEVEL", serviceLevel);
			this.setValue("REASSURE_FLG", reg.getReassureFlg());
			// onlyCaseNo = "";
			onlyCaseNo = opb.getReg().caseNo();
			//��Ӿ�����Ƿ񱨵���У��   20130814 yanjing
			checkArrive();//�Ƿ񱨵�У��
			//��Ӿ�����Ƿ񱨵���У��   20130814 yanjing  end
			// �������ϲ��ֵط���ֵ
			if (opb == null ) {  
				// this.messageBox_(11111111);
				this.messageBox("�˲�����δ����!");
				return;
			}
			oldOpdOrderParm = getOrder(); //add by huangtt 20141204
			// ��ʼ��opb�����ݴ���
			afterInitOpb();

			//add by huangtt 20140826 start
			// �ó�tableҪ��ʾ����������
			TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
			TParm tableShow = opb.getOrderParmNotCharge(true, comboPrescription
					.getValue(), this.getValueString("CAT1_TYPE"), true, un_exec.isSelected(),this.getValueString("MEM_TRADE"));
			if(tableShow.getCount()>0){
				this.messageBox("�˻��ߴ����ײ�����Ŀ");
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
		// �ó�tableҪ��ʾ����������
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		TParm tableShow = opb.getOrderParmNotCharge(true, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), true, un_exec.isSelected(),this.getValue("MEM_TRADE").toString());
		if(tableShow.getCount()>0){
			this.messageBox("�˻��ߴ����ײ�����Ŀ");
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
	 * ����XML�ļ� ================================pangben modify 20110806
	 */
	public void createOrderXML() {
		// 1.��������
		TParm inparm = new TParm();
		TParm parm = table.getParmValue();
		if (parm.getCount() - 1 <= 0) {
			this.messageBox("û����Ҫ���ɵ�����");
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
		// 2.�����ļ�
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzgzxx.xml");
		createFeeXML(parm);
	}

	private void createFeeXML(TParm parm) {
		// 1.��������
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
		// 2.�����ļ�
		NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/mzcfsj.xml");
		this.messageBox("���ɳɹ�");

	}

	/**
	 * ���ν���Ӳκ��ѯ
	 */
	public void onQueryByCaseNo() {
		setValue("MR_NO", mrNoPost);
		//delete by huangtt 20141126
		// �����ǰ�б����Ĳ���
		//		unLockPat();
		// ����Ĭ��û���½�����
		pFlg = "N";
		// ��ʼ��pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			callFunction("UI|record|setEnabled", false);
			return;
		}
		//delete by huangtt 20141126
		//		// ���������ʧ�����������
		//		if (!this.lockPat()) {
		//			return;
		//		}
		callFunction("UI|record|setEnabled", true);
		// ���渳ֵ
		setValueForParm("MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC", pat
				.getParm());
		
		setValue("CUSTOMER_SOURCE",MEMPatRegisterTool.getInstance().getMemCustomerSource(pat.getMrNo()));
		
		String age = OdiUtil.getInstance().showAge(pat.getBirthday(),
				SystemTool.getInstance().getDate());
		setValue("AGE", age);
		setValue("BIRTH_DATE",pat.getBirthday());
		// ���ý��洫��
		reg = Reg.onQueryByCaseNo(pat, caseNoPost);
		// �жϹҺ���Ϣ
		if (reg == null) {
			return;
		}
		// reg�õ������ݷ������
		afterRegSetValue();
		// ͨ��reg��caseNo�õ�pat
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// �������ϲ��ֵط���ֵ
		if (opb == null) {
			// this.messageBox_(22222222);
			this.messageBox("�˲�����δ����!");
			return;
		}
		// ��ʼ��opb�����ݴ���
		afterInitOpbParameter();
		return;
	}

	/**
	 * reg ��ʼ���ɹ����������
	 */
	public void afterRegSetValue() {
		// �����
		callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
		callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
		callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
		//-----------modified bywangqing at 2016.11.16 start------------------
		//�޸ķ�������������   �����Һŷ�ʽ

		//�Һŷ�ʽ
		callFunction("UI|REGMETHOD_CODE|setValue",reg.getRegmethodCode());
		//-----------modified bywangqing at 2016.11.16 end------------------
		// �������
		callFunction("UI|DEPT_CODE|setValue", reg.getRealdeptCode());
		// ����ҽ��
		callFunction("UI|DR_CODE|setValue", reg.getRealdrCode());
		//		String deptCode = Operator.getDept();
		String deptCode = Operator.getCostCenter();
		//		TParm deptParm = DeptTool.getInstance().selUserDept(deptCode);
		// ִ�п���
		//		if (deptParm.getCount("DEPT_CODE") > 0) {
		callFunction("UI|REALDEPT_CODE|setValue", deptCode);
		//		} else {
		//			callFunction("UI|REALDEPT_CODE|setValue", "");
		//		}
		// ִ��ҽ��
		callFunction("UI|REALDR_CODE|setValue", Operator.getID());
		// Ԥ���������
		this.callFunction("UI|STARTTIME|setValue", reg.getAdmDate());
		
		this.callFunction("UI|REASSURE_FLG|setValue", reg.getReassureFlg());
		
	}

	/**
	 * ��ʼ��opb������ݴ���
	 */
	public void afterInitOpb() {
		String view = checkAll.getValue().toString();
		// ��ѯ�˴ξ����Ƿ���˲���
		String sql = "SELECT CASE_NO,CONTRACT_CODE FROM BIL_CONTRACT_RECODE WHERE CASE_NO='"
				+ onlyCaseNo + "'";
		resultBill = new TParm(TJDODBTool.getInstance().select(sql));
		if (resultBill.getCount() > 0) {
			setValue("BILL_TYPE", "P");
		}
		// �����ȫ��
		if (view.equals("Y")) {
			onAll();
		} else {
			// ������ʾδ�շѷ���
			checkBoxNotCharge.setSelected(true);
			onNotCharge();
		}
		if (ektSave().getErrCode() < 0) { // =========pangb 2011-11-29
			// ���ҽ�ƿ���Ϣ�鿴�˴οۿ��Ƿ���ҽ�ƿ�����
			this.messageBox("��ȡҽ�ƿ���Ϣ����");
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

		// ҽ�ƿ��������û�п��ʿ���ִ���շѲ�����ִ�д�Ʊ,���ǲ�����ִ���ֽ��շ�
		// =====zhangp 20120302 modify start
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			//			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", false);
			// ��ʼ��Ʊ��
			// BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// =====origion code start
				// ��ʼ��Ʊ��
				if (!initBilInvoice(opb.getBilInvoice())) {
					// return;
				}
				// =====origion code end
			}
		}
		// =====zhangp 20120302 modify end
		// ��ʾ��һƱ��

		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
		this.setValue("INSURE_INFO", insureInfo);//������Ϣ
	}

	/**
	 * ��ʼ��Ʊ��
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			}
			// this.onClear();
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
		}
		callFunction("UI|UPDATE_NO|setValue", bilInvoice.getUpdateNo());
		return true;
	}

	/**
	 * ��ѯ��ǰ�����Ƿ�ҽ�ƿ��ۿ�
	 * 
	 * @return TParm
	 */
	private TParm ektSave() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("BUSINESS_TYPE", "REG"); // ����
		parm.setData("STATE", "1"); // ״̬�� 0 �ۿ� 1 �ۿ��Ʊ 2�˹� 3 ����
		tredeParm = EKTTool.getInstance().selectTradeNo(parm);
		return tredeParm;
	}

	/**
	 * �жϴ˴ξ����Ƿ�ִ��ҽ������
	 */
	public void afterInitOpbParameter() {
		String view = checkAll.getValue().toString();
		// �����ȫ��
		if (view.equals("Y")) {
			onAll();
		} else {
			// ������ʾδ�շѷ���
			onNotCharge();
		}
		TNumberTextField numberText = (TNumberTextField) this
				.getComponent("PAY");
		numberText.grabFocus();
	}

	/**
	 * �����¼ѡ��
	 */
	public void onRecord() {
		// ��ʼ��pat
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null) {
			messageBox_("���޴˲�����!");
			// ���޴˲��������ܲ��ҹҺ���Ϣ
			callFunction("UI|record|setEnabled", false);
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// �ж��Ƿ����ϸ�㿪�ľ����ѡ��
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
			return;
		}
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		if (reg == null) {
			messageBox("�Һ���Ϣ����!");
			return;
		}
		// reg�õ������ݷ������
		afterRegSetValue();
		// ͨ��reg��caseNo�õ�pat
		opb = OPB.onQueryByCaseNo(reg);
		serviceLevel = opb.getReg().getServiceLevel();
		this.setValue("SERVICE_LEVEL", serviceLevel);
		onlyCaseNo = opb.getReg().caseNo();
		checkArrive();//�Ƿ񱨵�У��
		if (opb == null) {
			// this.messageBox_(33333333);
			this.messageBox_("�˲�����δ����!");
			return;
		}
		oldOpdOrderParm = getOrder(); //add by huangtt 20141204
		// ��ʼ��opb�����ݴ���
		afterInitOpb();
	}

	/**
	 * ���鲡���Ƿ񱨵�
	 * yanjing 20131231 
	 */
	private void checkArrive(){
		//��Ӿ�����Ƿ񱨵���У��   20130814 yanjing
		String sql = "SELECT ARRIVE_FLG,IS_PRE_ORDER,INSURE_INFO FROM REG_PATADM WHERE CASE_NO = '"+onlyCaseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		insureInfo = result.getValue("INSURE_INFO",0);//������Ϣ
		//		System.out.println("��ѯsql  is����"+sql);
		if(result.getValue("ARRIVE_FLG",0).equals("N")&&!(result.getValue("IS_PRE_ORDER",0).equals("Y"))){
			this.messageBox("�˲�����δ����!");
			this.onClear();
			return;
		}
	}
	/**
	 * ��˲����Ƿ����ѿ�ҽ��
	 * 
	 * @return boolean
	 */
	public boolean checkOrderCount() {
		// ���opb
		if (opb == null) {
			return true;
		}
		if (opb.checkOrderCount()) {
			// this.messageBox_(44444444);
			this.messageBox("�˲�����δ����!");
			// table.addRow(setParm());
			// return true;//=====pangben modify 20110801
		}
		return false;
	}

	/**
	 * ���õõ���odo��table��ֵ
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTableValue(TParm parm) {
		// ���table����
		table.removeRowAll();
		// ����ҽ������ҽ������
		drOrderCount = parm.getCount();
		// ִ��ɾ���������жϵ�һ�μ�¼���������ҽ��
		// ==================pangben modify 20110804 ɾ����ť��ʾ
		if (!drOrderCountFalse && drOrderCount > 0) {
			drOrderCountTemp = drOrderCount; // ���ҽ��������ҽ������
			drOrderCountFalse = true; // ������ʾɾ����ť
		}
		// //û�п����κ��շ���Ŀ
		// if (drOrderCount <= 0) {
		// //��һ����
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
		// //�����ݷ���tabl
		//		TParm exeParm=onExeParmCompar(parm);
		TParm exeParm=parm;
		//table.setParmValue(exeParm);
		table.setLockColumns("1");// =========pangben 2012-4-16 ���������Ʊע��
		//zhangp
		for (int i = 0; i < exeParm.getCount("EXEC_FLG"); i++) {
			// PHA_DOSAGE_CODE ��ΪPHA_CHECK_CODE modify by huangtt 20180823
			if(exeParm.getBoolean("EXEC_FLG", i) || (exeParm.getValue("PHA_CHECK_CODE",i).length()>0 && exeParm.getValue("PHA_RETN_CODE",i).length()==0)){
				table.setRowColor(i, Color.YELLOW);	
			}else{
				table.setRowColor(i, Color.white);	
			}
		}
		table.setParmValue(exeParm);
		// table.setLockRows(lockRow);
		// table.setUI()
		// �ŷ���
		// ���¼������
		double fee = getFee();
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
		// ��һ����
		if (addOrder) {
			callFunction("UI|TABLE|addRow", setParm());
		}
	}
	public void setTableValue_R(TParm parm) {
		// ���table����
		table.removeRowAll();
		// ����ҽ������ҽ������
		drOrderCount = parm.getCount();
		//zhangp
		for (int i = 0; i < parm.getCount("EXEC_FLG"); i++) {
			// PHA_DOSAGE_CODE ��ΪPHA_CHECK_CODE modify by huangtt 20180823
			if(parm.getBoolean("EXEC_FLG", i) || (parm.getValue("PHA_CHECK_CODE",i).length()>0 && parm.getValue("PHA_RETN_CODE",i).length()==0)){
				table.setRowColor(i, Color.YELLOW);	
			}else{
				table.setRowColor(i, Color.white);	
			}
		}
		// //�����ݷ���tabl
		table.setParmValue(parm);
		table.setLockColumns("1,2,3,4,5,,6,7,8,9,10,11,12,13,14");
		// �ŷ���
		// ���¼������
		double fee = opb.getFee(!ektTCharge.isSelected());
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
	}

	/**
	 * ���������Ĭ������
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
	 * ѭ��ɾ��table����
	 */
	public void removeTableAllRow() {
		int row = table.getRowCount();
		for (int i = 0; i < row; i++) {
			table.removeRow();
		}
	}

	/**
	 * ���������Ĭ������
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
	 * ȫѡ�¼�
	 */
	public void onSelectAll() {
		// ���opb
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// �鿴��ʾ��ʽ
		String allR = (String) callFunction("UI|ALL|getValue");
		String notChargeR = (String) callFunction("UI|NOTCHARGE|getValue");
		String ektR = (String) callFunction("UI|EKT_R|getValue");
		// �����ȫ��
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
		// ��ʾδ�շ�
		// onNotCharge();
	}
	/**
	 * ����У����ʾ��Ϣ
	 * ===========pangben 2014-3-18 ����������У�飬��δִ�е� ��ʾ��Ϣ
	 */
	private boolean onSelectAllonExe(){
		if (getValue("BILL_TYPE").equals("E")) {
			if (this.getValueString("ALL").equals("Y")) {
				this.messageBox("�벻Ҫ��ѡȫ��");
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
				}else{//�����δִ�е�ҽ�������ʾ
					chargList.add(tableParm.getValue("CHARGE", i));
					if (tableParm.getBoolean("CHARGE", i)) {  
						if(this.messageBox("��ʾ", "��δִ����Ŀ���н��㣬�Ƿ����", 2)!=0){
							return false;
						}else{
							chargList.remove(chargList.size() -1);
							break;
						}
					}else{ //add by huangtt 20140826
						if(this.messageBox("��ʾ", "�˻�����δִ��ҽ�����Ƿ����", 2)!=0){
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
				}else{//�����δִ�е�ҽ�������ʾ
					if(chargList.contains("Y")){
						if (this.messageBox("��ʾ", "�˻�����δִ��ҽ�����Ƿ����", 2)!=0) {
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
					this.messageBox("���ɽ���");
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
//						messageBox("��ȫѡ");
//						return false;
//					}
//				}
//			}
//		}
		return true;
	}
	/**
	 * ��ʾδ�շ�ҽ��
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
			// //��һ����
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// ������շ�Ȩ��
		if (bilRight) {
			// �����շѷ���
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// �ó�tableҪ��ʾ����������
		TParm tableShow = opb.getOrderParmNotCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), mem_package.isSelected(), un_exec.isSelected(),this.getValueString("MEM_TRADE"));
		// ���ù��˷���,��table��ֵ
		tableShow(tableShow);
		// setTableValue(tableShow);
		// �õ�����ǩ
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// ��combo������
		comboPrescription.setVectorData(prescriptionCombopb);
		// ˢ��combo
		comboPrescription.updateUI();
		EKTmessage = false; // �����Ϣ
	}

	/**
	 * ���շ�ridiobutton�¼�
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
			// //��һ����
			// if (addOrder)
			// callFunction("UI|TABLE|addRow", setParm());
			return;
		}
		//
		boolean bo = checkBoxChargeAll.getValue().equals("Y");
		// ������շ�Ȩ��
		if (bilRight) {
			// �����շѷ���
			opb.chargeTAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// �ó�tableҪ��ʾ����������
		TParm tableShow = opb.getOrderParmEKTTCharge(bo, comboPrescription
				.getValue(), this.getValueString("CAT1_TYPE"), mem_package.isSelected(), un_exec.isSelected());
		// ���ù��˷���,��table��ֵ
		tableShow_R(tableShow);
		// setTableValue(tableShow);
		// �õ�����ǩ
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// ��combo������
		comboPrescription.setVectorData(prescriptionCombopb);
		// ˢ��combo
		comboPrescription.updateUI();
		EKTmessage = true; // �����Ϣ
	}

	/**
	 * ��ʾȫ��ҽ��
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
		// ���ѡ��ȫѡcheckbox
		if (checkBoxChargeAll.isSelected()) {
			bo = true;
		}
		// bo = false;
		// ������շ�Ȩ��
		if (bilRight) {
			// �����շѷ���
			opb.chargeAll(bo, comboPrescription.getValue());
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		TCheckBox un_exec = (TCheckBox) getComponent("UN_EXEC");
		// �ó�tableҪ��ʾ����������
		TParm tableShow = opb.getOrderParm(bo, comboPrescription.getValue(),
				true, mem_package.isSelected(), un_exec.isSelected(),this.getValueString("MEM_TRADE"));
		// ���ù��˷���,��table��ֵ
		tableShow(tableShow);
		// setTableValue(tableShow);
		// �õ�����ǩ
		Vector prescriptionCombopb = opb.getPrescriptionList()
				.getPrescriptionComb(this.getValueString("CAT1_TYPE"));
		// ��combo��ֵ
		comboPrescription.setVectorData(prescriptionCombopb);
		comboPrescription.updateUI();
		EKTmessage = false; // �����Ϣ
	}

	/**
	 * ������ǩ����
	 */
	public void onPrescription() {
		if (opb == null) {
			return;
		}
		if (checkOrderCount()) {
			return;
		}
		// �鿴��ʾ��ʽ
		String view = callFunction("UI|ALL|getValue").toString();
		// �����ȫ��
		if (view.equals("Y")) {
			onAll();
			return;
		}
		// ��ʾδ�շ�
		onNotCharge();
	}
	/**
	 * ����ҽ������ϸ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public void tableShow(TParm parm) {
		// ҽ������
		String orderCode = "";
		// ҽ�����
		int groupNo = -1;
		// ���㼯��ҽ�����ܷ���
		double fee = 0.0;
		double ownPrice = 0;
		// ҽ������
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 ɾ����ť��ʾ
		if (count < 0) {
			deleteFun = true;
		}
		// ��Ҫɾ����ϸ���б�
		int[] removeRow = new int[count < 0 ? 0 : count]; // =====pangben modify
		// 20110801
		int removeRowCount = 0;
		// ѭ��ҽ��
		for (int i = 0; i < count; i++) {
			Order order = (Order) parm.getData("OBJECT", i);
			// ������Ǽ���ҽ������
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			ownPrice = 0;
			// ҽ������
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// ��
			groupNo = order.getOrderSetGroupNo();
			// ���������ѭ������ҽ������ϸ��
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) parm.getData("OBJECT", j);
				// �������������ϸ��
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// �������
					fee += orderNew.getArAmt();
					ownPrice += orderNew.getOwnPrice() * orderNew.getDosageQty();
					// ����Ҫɾ������
					removeRow[removeRowCount] = j;
					// �Լ�
					removeRowCount++;
				}
			}
			// ϸ����ð�����
			parm.setData("AR_AMT", i, fee);
			parm.setData("OWN_PRICE", i, ownPrice);
		}
		// ɾ������ҽ��ϸ��=====pangben modify 20110801 ����ȥҽ��վ����ֱ�ӿ��Կ���ҽ���Ʒ�
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// ==================pangben modify 20110804 ɾ����ť��ʾ
		if (removeRowCount < 0) {
			deleteFun = true;
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// ����table��ֵ����
		setTableValue(parm);
	}

	public void tableShow_R(TParm parm) {
		TParm tableParm = parm;
		// ҽ������
		String orderCode = "";
		// ҽ�����
		int groupNo = -1;
		// ���㼯��ҽ�����ܷ���
		double fee = 0.0;
		// ҽ������
		int count = tableParm.getCount("ORDER_CODE");
		// ��Ҫɾ����ϸ���б�
		int[] removeRow = new int[count];
		int removeRowCount = 0;
		// ѭ��ҽ��
		for (int i = 0; i < count; i++) {
			Order order = (Order) tableParm.getData("OBJECT", i);
			// ������Ǽ���ҽ������
			if (order.getSetmainFlg() != null
					&& !order.getSetmainFlg().equals("Y")) {
				continue;
			}
			groupNo = -1;
			fee = 0.0;
			// ҽ������
			orderCode = order.getOrderCode();
			String rxNo = order.getRxNo();
			// ��
			groupNo = order.getOrderSetGroupNo();
			// ���������ѭ������ҽ������ϸ��
			for (int j = i; j < count; j++) {
				Order orderNew = (Order) tableParm.getData("OBJECT", j);
				// �������������ϸ��
				if (orderCode.equals(orderNew.getOrdersetCode())
						&& orderNew.getOrderSetGroupNo() == groupNo
						&& !orderNew.getOrderCode().equals(
								orderNew.getOrdersetCode())
						&& rxNo.equals(orderNew.getRxNo())) {
					// �������
					fee += orderNew.getArAmt();
					// ����Ҫɾ������
					removeRow[removeRowCount] = j;
					// �Լ�
					removeRowCount++;
				}
			}
			// ϸ����ð�����
			tableParm.setData("OWN_PRICE", i, fee);
			tableParm.setData("AR_AMT", i, fee);
		}
		// ==============pangben 2012-05-23 start ҽ����ʾ�޸�
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
		// ɾ������ҽ��ϸ��
		for (int i = removeRowCount - 1; i >= 0; i--) {
			tableParm.removeRow(removeRow[i]);
		}
		tableParm.setCount(tableParm.getCount() - removeRowCount);
		// ����table��ֵ����
		setTableValue_R(tableParm);

	}

	/**
	 * sysFee��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// ���õ�ǰѡ�е���
		selectRow = row;
		// �����ǰѡ�е��в������һ�п�����ʲô������
		if (table.getRowCount() != selectRow + 1) {
			return;
		}
		TTextField textfield = (TTextField) com;
		//���ݹҺŲ�������Ч����У���Ƿ���Ծ�����ҽ������===========pangben 2013-4-28
		if(!OPBTool.getInstance().canEdit(reg, regSysParm)){
			this.messageBox("������ǰ����ʱ��");
			this.onClear();
			return;
		}
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		// �õ���ǰ����
		String columnName = table.getParmMap(column);
		// ����sysfee�Ի������
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
		//add by lx 2014/03/17  ��ʿר��
		parm.setData("USE_CAT", "2");

		if (!"".equals(Operator.getRegion())) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("SYSFEE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newOrder");
	}

	/**
	 * �¿�ҽ��
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newOrder(String tag, Object obj) {
		if (systemCode != null && "ONW".equals(systemCode)) {
			TParm patParm = PatTool.getInstance().getLockPat(pat.getMrNo());
			// �ж��Ƿ����
			if (patParm != null && patParm.getCount() > 0) {
				if (!isMyPat(patParm)) {// �û��������������ҽ��===pangben 2013-5-15
					this.messageBox("�˲����ѱ�" + patParm.getValue("OPT_USER", 0)
					+ "����");
					this.closeWindow();
					return;
				}
			}
		}
		// sysfee���ص����ݰ�
		TParm parm = (TParm) obj;
		String freq_code=this.getFreqCode(parm);// add caoyong 20130108 �õ�Ƶ��
		newReturnOrder(parm,selectRow,0.00,parm.getValue("UNIT_CODE"),freq_code,true);
		//newReturnOrder(parm,selectRow,0.00,parm.getValue("UNIT_CODE"),true);
	}
	/**
	 * add caoyong �õ�Ƶ��
	 * @param parm
	 * @return
	 */
	public String getFreqCode(TParm parm){
		//$$$$$$--------add caoyong 20131218 ����Ƶ�� start ----------------///
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "RIS".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "OTH".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				) {
			///order.setItem(row, "FREQ_CODE", "STAT");
			return "STAT";
			//orderOne.modifyFreqCode("STAT");//Ĭ������ʹ��
			//parm.setData("FREQ_CODE","STAT");//Ĭ������ʹ��
		}else{
			TParm action = new TParm();
			action.setData("ORDER_CODE",parm.getValue("ORDER_CODE") );
			TParm result = OdiMainTool.getInstance().queryPhaBase(
					action);
			return result.getValue("FREQ_CODE", 0);

		}
	}
	/**
	 * ����ҽ������
	 * ========pangben 2013-8-30
	 * @param parm
	 * flg :true ��������  false:�����ײʹ���
	 * 
	 */
	private void newReturnOrder(TParm parm,int selectRow, double dosage_qty,String dosage_unit,String freq_code,boolean flg){
		// ��������ǩ(����Ƽ�),���һ��order
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
		// �õ���ǰ(����Ƽ�)����ǩ�е����ҽ����
		if (orderList == null) {
			orderList = opb.getPrescriptionList().getOrderList(maxName,
					opb.getPrescriptionList().getGroup(maxName).size());
		}

		// �½�ҽ��
		Order orderOne = orderList.newOrder();

		// �Ƿ��շ�
		boolean bo = false;
		// �շѱ��
		String chargeFlg = "N";
		// ���ѡ��ȫѡcheckbox
		if (checkBoxChargeAll.isSelected()) {
			chargeFlg = "Y";
			bo = true;
		}
		// ����ע��
		orderOne.modifyChargeFlg(bo);
		// ����Ǽ���ҽ��
		if (parm.getValue("ORDERSET_FLG").equals("Y")) {
			Order o=OdoUtil.initExaOrder(reg, parm, orderOne, false, serviceLevel);
			// Ҫ����order��λ��
			int count = orderList.size() - 1;
			// ��������ҽ��
			OdoUtil.addOrder(reg, orderList, count, false, serviceLevel);
			// �ɱ�����
			//			orderOne.modifyCostCenterCode(DeptTool.getInstance().getCostCenter(
			//					orderOne.getExecDeptCode(), ""));
			orderOne.modifyCostCenterCode(getValueString("REALDEPT_CODE"));
			orderOne.modifyExecDeptCode(getValueString("REALDEPT_CODE"));
			// ������ɼ���ҽ�����ҽ������
			int orderListCount = orderList.size();
			// �õ��¿�����ҽ��
			orderOne = orderList.getOrder(count);
			// ���㼯��ҽ���ܷ���
			double allFee = 0.0;
			// �����¿�����ҽ��
			for (int i = count; i < orderListCount; i++) {
				// �õ��¿�ҽ��
				Order newOrder = orderList.getOrder(i);
				//				newOrder.modifyCostCenterCode(DeptTool.getInstance()
				//						.getCostCenter(newOrder.getExecDeptCode(), ""));
				orderOne.modifyCostCenterCode(getValueString("REALDEPT_CODE"));
				orderOne.modifyExecDeptCode(getValueString("REALDEPT_CODE"));
				newOrder.modifyChargeFlg(bo);
				newOrder.modifyDiscountRate(o.getDiscountRate());//======pangben 2013-8-30 �ۿ����
				newOrder.modifyTakeDays(o.getTakeDays());
				//				if (!flg) {
				//					newOrder.modifyDosageQty(dosage_qty);
				//				}
				if (newOrder.getOrderCode().equals(newOrder.getOrdersetCode())) {
					continue;
				}
				// �������
				allFee += newOrder.getArAmt();
			}
			// �õ�ҽ����Ŀ��ʾ����
			parm = orderOne.getParm();
			// �����������ʾ����
			parm.setData("AR_AMT", allFee);
		} else {
			// ��װ�����
			String[] ctz = new String[3];
			ctz[0] = opb.getReg().getCtz1Code();
			ctz[1] = opb.getReg().getCtz2Code();
			ctz[2] = opb.getReg().getCtz3Code();
			// ���ù��÷�����װorder
			orderOne = OdoUtil.fillOrder(orderOne, parm, ctz,dosage_qty, dosage_unit ,serviceLevel,flg);
			TotQtyTool t = new TotQtyTool();
			// �õ�ҽ����Ŀ��ʾ����
			parm = orderOne.getParm();
			//==========pangben 2013-1-28��ʿ ������Ʒ�Ĭ��Ƶ��
			//orderOne.modifyFreqCode("STAT");//Ĭ������ʹ��
			//parm.setData("FREQ_CODE","STAT");//Ĭ������ʹ��

			//$-------------- modify caoyong 20131228 start------------------// 
			orderOne.modifyFreqCode(freq_code);//Ƶ��
			parm.setData("FREQ_CODE",freq_code);//Ƶ��
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
			}else{//====pangben 2013-10-29 ��ӿ���ҽ����ʼֵ��ֵ����
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
				//orderOne.modifyMediQty(orderOne.getMediQty()*orderOne.getDosageQty());//===pangben 2013-12-10 ���ص�ҽ������Ҫƥ��// modify caoyong 20131218
				orderOne.modifyOwnAmt(StringTool.round(orderOne.getOwnPrice() * orderOne.getDosageQty(), 2));
				orderOne.modifyArAmt(BIL.chargeTotCTZ(orderOne.getCtz1Code(), orderOne.getCtz2Code(),
						orderOne.getCtz3Code(), orderOne.getOrderCode(), orderOne
						.getDosageQty(), serviceLevel));
			}
			parm.setData("DISPENSE_QTY", orderOne.getDosageQty());// ��ҩ��
			parm.setData("DOSAGE_QTY", orderOne.getDosageQty());// ��ҩ��
			parm.setData("AR_AMT", orderOne.getArAmt());
			parm.setData("OWN_AMT",orderOne.getOwnAmt()); 
			//parm.setData("DISPENSE_QTY",orderOne.getDosageQty());//��ҩ��
			//orderOne.modifyDispenseQty(orderOne.getDosageQty());//��ҩ��
			//orderOne.modifyOwnAmt(orderOne.getOwnPrice()*orderOne.getDosageQty());//�Էѽ��
			//orderOne.modifyArAmt(orderOne.getTakeDays()*orderOne.getOwnPrice());//�ܽ��====pangben 2013-8-29 ע�ͣ��ۿ۽��
			//parm.setData("DOSAGE_QTY",orderOne.getDosageQty());//��ҩ��
			//parm.setData("AR_AMT",orderOne.getTakeDays()*orderOne.getOwnPrice());//�ܽ��====pangben 2013-8-29 ע�ͣ��ۿ۽��
			//parm.setData("OWN_AMT",orderOne.getOwnPrice()*orderOne.getDosageQty());//�Էѽ��

		}
		// �ɱ�����
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
		// ���۱��
		parm.setData("CHARGE", chargeFlg);
		parm.setData("OBJECT", orderOne);
		// ���¿���ҽ������table
		table.setRowParmValue(selectRow, parm);
		table.getParmValue().setRowData(selectRow, parm);
		double fee = getFee();
		// ����һ��
		table.addRow(setParm());
		callFunction("UI|TOT_AMT|setValue", fee);

		paymentTool.setAmt(fee);

		setFeeReview();
		table.getTable().grabFocus();
		table.setSelectedColumn(2);
		table.setSelectedRow(selectRow);
	}
	/**
	 * ���Ӷ�Tableֵ�ı�ļ���
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableChangeValue(Object obj) {
		// ��ǰ�༭�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return false;
		}
		callFunction("UI|delete|setEnabled", false);
		// ��ǰ�༭������
		String colunHeader = table.getParmMap(node.getColumn());
		// ���Ļ���ע��
		if (colunHeader.equals("CHARGE")) {
			return chargeChange(node);
		}
		// ���û�п�ҽ����Ȩ�޲��ܸ���ҽ������
		if (!addOrder) {
			return true;
		}
		if (node.getRow() != table.getRowCount() - 1) {
			// �õ��������е�order
			Order order = (Order) table.getParmValue().getData("OBJECT",
					node.getRow());
			boolean dcOrder = order.getDcOrder();
			// ===zhangp 20120316 start
			// ===zhangp 20120414 start
			// if (dcOrder&&!order.getRxType().equals("0")) {
			if (dcOrder && !order.getRxType().equals("7")) {
				// ===zhangp 20120414 end
				messageBox_("ҽ��������ҽ��,���ܸ���");
				return true;
			}
			// ===zhangp 20120316 end
			// //����Ǽ���ҽ��������
			// if ("Y".equals(order.getSetmainFlg())) {
			// this.messageBox("��ҽ���Ǽ���ҽ��!");
			// return true;
			// }
		}
		// �����ҽ����ҽ��������,�շ�Ա��Ȩɾ���ͱ༭
		// ===zhangp 20120316 start
		// // �õ��������е�order
		// Order order = (Order) table.getParmValue().getData("OBJECT",
		// node.getRow());
		if (node.getRow() >= drOrderCount) {
			// ===zhangp 20120316 end
			callFunction("UI|delete|setEnabled", true);
		}
		// ҽ������
		if (colunHeader.equals("ORDER_DESC")) {
			return orderDescChange(node);
		}
		// ����
		if (colunHeader.equals("MEDI_QTY")) {
			return mediQtyChange(node);

		}
		// Ƶ��
		if (colunHeader.equals("FREQ_CODE")) {
			return freqCodeChange(node);
		}
		// �÷�
		if (colunHeader.equals("ROUTE_CODE")) {
			return routeCodeChange(node);
		}
		// ����
		if (colunHeader.equals("TAKE_DAYS")) {
			return takeDateChange(node);
		}
		// ����
		if (colunHeader.equals("DOSAGE_QTY")) {
			return dosageQtyChange(node);
		}
		// �������
		if (colunHeader.equals("DEPT_CODE")) {
			return deptCodeChange(node);
		}
		// ִ�п���
		if (colunHeader.equals("EXEC_DEPT_CODE")) {
			return execDeptChange(node);
		}
		// ִ��ҽ��
		if (colunHeader.equals("EXEC_DR_CODE")) {
			return execDrCodeChange(node);
		}

		// С�� add by huangtt 20150129
		if (colunHeader.equals("AR_AMT")) {
			return arAmtChange(node);
		}
		return false;
	}

	/**
	 * �޸�������
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean chargeChange(TTableNode node) {
		table.acceptText();
		// �ж��շ�Ȩ��
		if (!bilRight) {
			return true;
		}
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1
				&& (!ektTCharge.isSelected())) {
			return true;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ��ǰnode��ֵ
		boolean b = TCM_Transform.getBoolean(node.getValue());
		// ��order�����շѱ��
		order.modifyChargeFlg(b);
		// //���order�Ѿ��շ�,������
		// if (order.getChargeFlg() != b)
		// return true;
		// ����Ǽ���ҽ��������
		if ("Y".equals(order.getSetmainFlg())) {
			// �õ�ҽ������
			String ordeCode = order.getOrderCode();
			// �õ�����ҽ�������
			int orderGroupNo = order.getOrderSetGroupNo();
			// ����ǩ��
			String rxNo = order.getRxNo();
			// �����շ�
			opb.congregation(ordeCode, orderGroupNo, rxNo, b);
		}
		// ���¼������
		// double fee = opb.getFee(!ektTCharge.isSelected());
		return false;
	}

	/**
	 * �ı�ҽ������
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean orderDescChange(TTableNode node) {
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// ����������һ�еĿ���
		if (node.getRow() != table.getRowCount() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * ����ı�����
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean mediQtyChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue() == null) {
			node.setValue(0);
		}
		//����ڵ�������Ǹ�ֵ����ʾ����,yanjing,20130702
		if (Double.valueOf(node.getValue().toString())<0) {
			this.messageBox("��������Ϊ��ֵ��");
			return true;
		}
		//=====20130702 yanjing end
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// ���������ı��¼�
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ��order�Ķ�Ӧ�з�������
		order.modifyMediQty(TCM_Transform.getDouble(node.getValue()));
		// ͨ�����������Ӧ����
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
		// ����ժȡ����
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(4);
		table.setSelectedRow(selectRow);

		// //���¼������
		// //���¼������
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * Ƶ�θ���
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean freqCodeChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����

		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// ����������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ����order��Ƶ��
		order.modifyFreqCode(TCM_Transform.getString(node.getValue()));
		// ����Ƶ�θı���ʾ�¼�
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
		// ����ժȡ����
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(5);
		table.setSelectedRow(selectRow);

		// //����table����ʾ
		// setTableValueAt(order,node);
		// //���¼������
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * �÷�����
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean routeCodeChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ����order���÷�
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
		// ����table����ʾ
		setTableValueAt(order, node);
		return false;
	}

	/**
	 * ��������
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean takeDateChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ��order��������
		order.modifyTakeDays(TCM_Transform.getInt(node.getValue()));
		// ���������ı����
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
		// ����ժȡ����
		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(7);
		table.setSelectedRow(selectRow);

		// //����table����ʾ
		// setTableValueAt(order,node);
		// //���¼������
		// //���¼������
		// double fee=opb.getFee();
		// callFunction("UI|TOT_AMT|setValue", fee);
		// callFunction("UI|PAY_CASH|setValue",fee);
		return false;
	}

	/**
	 * �����޸�
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean dosageQtyChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ��order��������
		order.modifyDosageQty(TCM_Transform.getDouble(node.getValue()));
		// ���������ı����
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
		// ����ժȡ����
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
	 * У����������Ϊ0
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
				this.messageBox(parm.getValue("ORDER_DESC", i) + "����������Ϊ0");
				return false;
			}
		}
		return true;
	}

	/**
	 * EXEC_DEPT_CODE ִ�п��Ҹ���
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDeptChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ����order��ִ�п���
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
	 * ҽ�����Ըı��table��ֵ�Ըı�
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
		// ����
		table.setValueAt(order.getMediQty(), row, 2);
		// Ƶ��
		table.setValueAt(order.getFreqCode(), row, 4);
		// ����
		table.setValueAt(order.getTakeDays(), row, 6);
		// ����DOSAGE_QTY
		table.setValueAt(order.getDosageQty(), row, 7);
		// С�Ʒ�
		table.setValueAt(order.getArAmt(), row, 10);
	}

	/**
	 * EXEC_DR_CODE ����ҽʦ
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean execDrCodeChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ����order��ҽʦ
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
	 * DEPT_CODE ����Ʊ�ı�
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean deptCodeChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue())) {
			return false;
		}
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ����order��ҽʦ
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
	 * AR_AMT ���Ľ��
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	public boolean arAmtChange(TTableNode node) {
		// ��������һ�еĿ���
		if (node.getRow() == table.getRowCount() - 1) {
			return true;
		}
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue() == null) {
			node.setValue(0);
		}
		if (TCM_Transform.getDouble(node.getValue()) == TCM_Transform
				.getDouble(node.getOldValue())) {
			return false;
		}

		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				node.getRow());
		// ��order�Ķ�Ӧ�з���С��
		order.modifyArAmt(TCM_Transform.getDouble(node.getValue()));

		if (checkBoxNotCharge.isSelected()) {
			this.onNotCharge();
		} else {
			this.onAll();
		}
		table.getTable().grabFocus();
		table.setSelectedColumn(12);
		table.setSelectedRow(selectRow);
		//���¼������
		double fee=getFee();
		callFunction("UI|TOT_AMT|setValue", fee);
		return false;
	}


	/**
	 * ��ҽ�ƿ�
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm modifyOrderParm) {
		TParm orderParm = opb.getEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		setEktExeParm(orderParm, modifyOrderParm, null);
		if (orderParm.getParm("newParm").getCount("RX_NO") <= 0) {
			this.messageBox("û��Ҫִ�е�����");
			return null;
		}
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("CASE_NO", reg.caseNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("ektParm", parmEKT.getData());
		//zhangp
		orderParm.setData("PAY_OTHER3", modifyOrderParm.getDouble("PAY_OTHER3"));
		orderParm.setData("PAY_OTHER4", modifyOrderParm.getDouble("PAY_OTHER4"));
		TParm parm = new TParm();
		// ��Ҫ���ҽ���շ���ϸ���EKT_ACCNTDETAIL���������
		parm = EKTIO.getInstance()
				.onOPDAccntClient(orderParm, onlyCaseNo, this);
		parm.setData("orderParm", orderParm.getData());
		return parm;
	}

	/**
	 * �շѲ����ռ����� String exeTrade��UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
	 * 
	 * @param parm
	 */
	private void setEktExeParm(TParm parm, TParm modifyOrderParm,
			String exeTrade) {
		// ������δ�շ��޸ġ�δ�շ�ɾ��������
		// TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		TParm newParm = new TParm();// �˴β�����ҽ������
		TParm hl7Parm = new TParm();// hl7����
		String bill_flg = "";
		double sum = 0.00;// ִ�н��
		StringBuffer phaRxNo=new StringBuffer();//pangben 2013-5-17������в����Ĵ���ǩ���� ��������
		if (!ektTCharge.isSelected()) {
			bill_flg = "Y";
		} else {
			bill_flg = "N";
		}
		parm.setData("BUSINESS_TYPE", "OPB");
		for (int i = 0; i < modifyOrderParm.getParm(OrderList.NEW).getCount(//��������,���ݿ⻹û�б����
				"RX_NO"); i++) {
			for (int j = 0; j < parm.getCount("ORDER_CODE"); j++) {
				// ��ʾ��ѡ�շ�״̬
				if (parm.getValue("RX_NO", j).equals(
						modifyOrderParm.getParm(OrderList.NEW).getValue("RX_NO", i))&& 
						parm.getValue("SEQ_NO", j).equals(modifyOrderParm.getParm(OrderList.NEW).getValue("SEQ_NO", i))) {
					OPBTool.getInstance().setNewParm(newParm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j), "E");// ������ҽ��
					// HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
					OPBTool.getInstance().setHl7TParm(hl7Parm,
							modifyOrderParm.getParm(OrderList.NEW), i,
							parm.getValue("CHARGE_FLG", j));
					if (parm.getValue("CHARGE_FLG", j).equals("Y")) {
						sum += modifyOrderParm.getParm(OrderList.NEW)
								.getDouble("AR_AMT", i);
					}
					if (null != modifyOrderParm.getParm(OrderList.NEW).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
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
			if (!ektTCharge.isSelected()){// �շѲ���ֱ��������� �˷Ѳ��� ��Ҫ���ҵ�������ҽ���������ڲ����׺�
				// �����ڴ��ڲ����׺� ������ҽ�����¸�ֵ
				OPBTool.getInstance().setNewParm(newParm,
						modifyOrderParm.getParm(OrderList.MODIFIED), i, "Y",
						"E");// ���ݿ���δ�շѵ�ҽ��
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("0")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
			}
			// HL7���ݼ��� ������� �ļ���ҽ������ ���ͽӿ�ʹ��
			OPBTool.getInstance().setHl7TParm(hl7Parm,
					modifyOrderParm.getParm(OrderList.MODIFIED), i, bill_flg);
		}
		StringBuffer tradeNo = new StringBuffer();
		if (ektTCharge.isSelected()) {// �˷Ѳ���
			StringBuffer tempTradeNo = new StringBuffer();
			// ���Ҵ˴β����������ڲ����׺�
			for (int i = 0; i < modifyOrderParm.getParm(OrderList.MODIFIED)
					.getCount("RX_NO"); i++) {
				if (null != modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i) && // ==pangben2013-5-15���ҩ����ҩ��ʾ���������
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue("CAT1_TYPE", i).equals("PHA")&&
						!modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_TYPE", i).equals("7")) {
					if (!phaRxNo.toString().contains(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i))) {
						phaRxNo.append(modifyOrderParm.getParm(OrderList.MODIFIED).getValue("RX_NO", i)).append(",");
					}
				}
				if (!tempTradeNo.toString().contains(
						modifyOrderParm.getParm(OrderList.MODIFIED).getValue(
								"BUSINESS_NO", i))) {
					// ������β�����ҽ��ʹ��
					tempTradeNo.append(
							modifyOrderParm.getParm(OrderList.MODIFIED)
							.getValue("BUSINESS_NO", i)).append(",");
					// UPDATE EKT_TRADE ��ʹ�� �޸��Ѿ��ۿ������ �帺ʹ��
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
					// ѡ���ҽ��
					// EKT_TRADE ���ڲ����׺�
					if (parm.getValue("BUSINESS_NO", i).equals(
							tempTradeNames[j])) {
						newFlg = false;
						for (int z = 0; z < modifyOrderParm.getParm(
								OrderList.MODIFIED).getCount("RX_NO"); z++) {
							// ���˴��˷ѵ�ҽ���Ƴ�
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
							sum += parm.getDouble("AMT", i);// ִ�н��
						} else {
							OPBTool.getInstance().setNewParm(newParm, parm, i,
									"N", "C");
						}
					}
				}
			}
		}
		String exeTradeNo = "";
		// ����ڲ����׺��� ���˴β�����ҽ���ۿ�������Ҫ�˻���ҽ��
		if (tradeNo.length() > 0) {
			exeTradeNo = tradeNo.toString().substring(0,
					tradeNo.toString().lastIndexOf(","));
		}
		parm.setData("newParm", newParm.getData());// ����ҽ������ɾ��
		// parm.setData("unBillParm",
		// modifyOrderParm.getParm(OrderList.MODIFIED).getData());//ת��δ�շ�״̬����
		parm.setData("hl7Parm", hl7Parm.getData());// HL7���ͽӿڼ���
		parm.setData("EXE_AMT", !ektTCharge.isSelected() ? this
				.getValueDouble("TOT_AMT") : sum);// EKT_TRADE �д˴� �����Ľ��
		// ��ô˴ξ���ҽ���ܽ������Ѿ��շѡ��¿���(������β�����ҽ��)
		parm.setData("SHOW_AMT", this.getValueDouble("TOT_AMT"));// ��ʾ�Ľ��
		parm.setData("ORDER", modifyOrderParm.getData());// ����OPD_ORDER ʹ��
		parm.setData("TRADE_SUM_NO", null == exeTrade ? exeTradeNo : exeTrade);// UPDATE
		parm.setData("PHA_RX_NO", phaRxNo.length()>0? phaRxNo.toString().substring(0,
				phaRxNo.toString().lastIndexOf(",")):"");//=pangben2013-5-15���ҩ����ҩ��ʾ���������
		// EKT_TRADE
		// �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
	}

	/**
	 * �����շ�ҽ�ƿ��������,ִ���շ�ҽ���ָ��ӡ����
	 */
	public void exeInsPrint() {
		TParm exeParm = new TParm();
		TParm orderParm = opb.getInsEKTParm(ektTCharge.isSelected(), this
				.getValueString("CAT1_TYPE"));
		if (orderParm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("û��Ҫִ�е�����");
			return;
		}
		if (null == parmEKT || parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox("���ȡҽ�ƿ���Ϣ");
			return;
		}
		if (null == insParm) {
			this.messageBox("���ȡҽ������Ϣ");
			return;
		}
		if (null == this.getValue("UPDATE_NO")
				|| this.getValue("UPDATE_NO").toString().length() <= 0) {
			this.messageBox("û�п�ִ�е�Ʊ�ݺ���");
			return;
		}
		TParm result = null;
		TParm parm = null;
		// ���ҽ��
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
				this.messageBox("���ҽ������ʧ��");
				return;
			}
			orderParm.setData("YF", i, result.getValue("YF", 0));// �÷�
			orderParm.setData("ZFBL1", i, result.getValue("ZFBL1", 0));// �Ը�����
			orderParm.setData("PZWH", i, result.getValue("PZWH", 0));// ��׼�ĺ�
		}
		insExeParm(orderParm);
		exeParm.setData("NAME", pat.getName());
		exeParm.setData("MR_NO", pat.getMrNo()); // ������
		exeParm.setData("PAY_TYPE", isEKT); // ֧����ʽ
		exeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		// exeParm.setData("orderParm", orderParm.getData());// ��Ҫ�շѵ�ҽ��
		// exeParm.setData("parmSum", orderParm.getParm("parmSum").getData());//
		// ���е�ҽ��
		// ��������ҽ�������޸�
		// OPD_ORDER
		// MED_APPLY����
		exeParm.setData("billAmt", orderParm.getDouble("billAmt"));// ����ҽ�����
		// exeParm.setData("parmBill",
		// orderParm.getParm("parmBill").getData());// δ�շ�ҽ������
		// �����շ�δ�շ�
		exeParm.setData("ektParm", parmEKT.getData());// ҽ�ƿ�����
		exeParm.setData("insParm", insParm.getData());// ҽ������
		exeParm.setData("FeeY", orderParm.getDouble("sumAmt"));// Ӧ�ս��
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("OPT_USER", Operator.getID());
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("CASE_NO", reg.caseNo());
		exeParm.setData("ADM_TYPE", reg.getAdmType());
		exeParm.setData("ID_NO", pat.getIdNo());
		exeParm.setData("PRINT_NO", this.getValue("UPDATE_NO"));// Ʊ��
		exeParm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());// ��ʼƱ��
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		setEktExeParm(orderParm, modifyOrderParm, orderParm
				.getValue("TRADE_SUM_NO"));
		exeParm.setData("orderParm", orderParm.getData());// ����������
		TParm r = (TParm) openDialog("%ROOT%\\config\\ins\\INSFeePrint.x",
				exeParm);
		//======pangben 2013-3-13 ���У��Ϊ��
		if(null==r){
			return;
		}
		opdOrderSpc(orderParm);//===pangben 2013-5-22 ���������Ԥ����
		String re = EKTIO.getInstance().check(r.getValue("TRADE_NO"),
				reg.caseNo(),reduceAmt);
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("����������Ϣ������ϵ");
		}
		this.onClear();
	}
	/**
	 * =====pangben 2013-5-22 ���������Ԥ����
	 * OPD_ORDER��������� 
	 * @param orderParm
	 */
	private void opdOrderSpc(TParm orderParm){
		if (Operator.getSpcFlg().equals("Y")&&orderParm.getValue("PHA_RX_NO").length()>0) {
			// ==========pangben 2013-5-22 ���Ԥ����
			TParm spcParm = new TParm();
			spcParm.setData("RX_NO", orderParm.getValue("PHA_RX_NO"));
			spcParm.setData("CASE_NO", reg.caseNo());
			spcParm.setData("CAT1_TYPE", "PHA");
			spcParm.setData("RX_TYPE", "7");
			// ��������ô˴β�����ҽ����ͨ������ǩ���
			TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
					spcParm);
			if (spcResult.getErrCode() < 0) {
				this.messageBox("������������ҽ����ѯ���ִ���");
			} else {
				spcResult.setData("SUM_RX_NO", orderParm.getValue("PHA_RX_NO"));
				spcResult = TIOM_AppServer.executeAction(
						"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
						spcResult);
				if (spcResult.getErrCode() < 0) {
					System.out.println("����������:" + spcResult.getErrText());
					this.messageBox("������������ҽ����ӳ��ִ���," + spcResult.getErrText());
				} else {
					phaRxNo = orderParm.getValue("PHA_RX_NO");// =pangben2013-5-15���ҩ����ҩ��ʾ���������
					sendMedMessages();
				}
			}
		}
	}
	/**
	 * ִ��ҽ�������� ������� ����ÿ۳��Ժ��ҽ������============pangb 2011-11-29
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
			this.messageBox(result.getValue("MESSAGE")); // �ֽ�֧���� ����ҽ������ ��
			// �ϴ�������ϸʧ��
			// ��������ϸ����ʧ�ܡ��˻�֧��ʧ��
			if (null != result.getValue("FLG")
					&& result.getValue("FLG").length() > 0) {
				result.setErr(-1, "ҽ����ִ�г���");
				return result;
			}
			return result;
		}
		return result;

	}

	/**
	 * ����ҽ��
	 * 
	 * @return boolean
	 */
	public boolean onSave() {		
		//add by huangtt 20141126    ����ǰ��һ��ҽ�������бȶԣ��鿴�Ƿ��в�һ����
		if(!orderComparison(getOrder())){
			return false;
		}

		//add by huangtt 20141009 start
		if(onGreenBalance()){
			if(this.getValueDouble("GIFT_CARD2") != 0 || this.getValueDouble("GIFT_CARD") != 0){
				if(this.getValueDouble("PAY_OTHER4") == 0 && this.getValueDouble("PAY_OTHER3") == 0){
					this.messageBox("���������ȯ����Ʒ����Ǯ����");
					return false;
				}
			}

		}
		//add by huangtt 20141009 end

		// table����
		table.acceptText();
		//delete by huangtt 20141126
		//		if (!PatTool.getInstance().isLockPat(pat.getMrNo())) {
		//			this.messageBox("�����Ѿ��������û�ռ��!");
		//			return false;
		//		}
		//		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		//		if (("ODO".equals(parm.getValue("PRG_ID", 0))||"ODE".equals(parm.getValue("PRG_ID", 0)))//����������У��===pangben 2013-7-18
		//				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
		//				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
		//			this.messageBox(PatTool.getInstance().getLockParmString(
		//					pat.getMrNo()));
		//			return false;
		//		}

		if (getValue("BILL_TYPE").equals("")) {
			this.messageBox("֧����ʽ����Ϊ��");
			return false;
		}
		// ��ѯ��ǰ���ﲡ���Ƿ�ִ�м���
		// String sql =
		// "SELECT A.PRINT_NO,B.CONTRACT_CODE FROM BIL_REG_RECP A,REG_PATADM B WHERE A.CASE_NO=B.CASE_NO(+)  AND  A.CASE_NO='"
		// + onlyCaseNo +
		// "'";
		// CASE_NO!=null || CASE_NO!="" ����: ������==========pangben 20110818
		String CONTRACT_CODE = "";
		if (resultBill != null && resultBill.getCount("CASE_NO") > 0) {
			if (null != resultBill && null != resultBill.getValue("CASE_NO", 0)
					&& !"".equals(resultBill.getValue("CASE_NO", 0))) {
				isbill = true; // ����
				CONTRACT_CODE = resultBill.getValue("CONTRACT_CODE", 0); // =====pangben
			}
		}
		if (!dosageQtyCheck()) {
			return false;
		}
		if (!onSelectAllonExe()) {//=======pangben 2014-3-18 ����������У�飬��δִ�е� ��ʾ��Ϣ
			return false;
		}
		// 20110818
		// ���˵�λ
		// �ж�֧����ʽ�Ƿ�΢��֧����

		// �ж�֧����ʽ�Ƿ�Ϊҽ�ƿ�
		if (getValue("BILL_TYPE").equals("E")) {
			TRadioButton notCharge = (TRadioButton) getComponent("NOTCHARGE");
			if(notCharge.isSelected() && this.onPayOther()){//δ�շ�
				return false;
			}
			// ����ҽ�ƿ�
			return onEktSave();
		}
		if (getValue("BILL_TYPE").equals("C")
				|| getValue("BILL_TYPE").equals("P")) {
			if (systemCode != null && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				// // ��ʼ��Ʊ��
				// if (!initBilInvoice(opb.getBilInvoice())) {
				// return false;
				// }
			}
			// �����ֽ�
			return onCashSave(CONTRACT_CODE);
		}
		// ҽ��������
		if (getValue("BILL_TYPE").equals("I")) {
			if (isEKT) {
				return onEktSave();
			} else {
				return onCashSave(CONTRACT_CODE);
			}
		}
		// ��˿�����
		if (checkOpenBill()) {
			return false;
		}
		// ��װƱ��
		setOpbReceipt();
		// �õ��վݽ��
		double totAmt = getValueDouble("TOT_AMT");
		if (totAmt == 0) {
			this.messageBox("���տ���");
			return false;
		}
		double pay = getValueDouble("PAY");
		if (pay - totAmt < 0 || pay == 0) {
			this.messageBox("����!");
			this.grabFocus("PAY");
			return false;
		}
		// ����շѽ�����0�����շ�
		String charge = "N";
		if (bilRight) {
			charge = "Y";
		}

		// //�õ��շ���Ŀ
		// sendHL7Parm = table.getParmValue();
		// ����opb�ı��淽��
		TParm result = opb.onSave(charge);
		if (result.getErrCode() < 0) {
			this.messageBox("�ɷ�ʧ��!");
			return false;
		}
		this.messageBox("�����ɹ�");
		// //����HL7
		// sendHL7Mes();
		// �õ���̨���淵�ص�Ʊ�ݺ�
		String[] receiptNo = (String[]) result.getData("RECEIPTNO");

		// ���ô����ӡ�ķ���
		dealPrintData(receiptNo);
		// �շѳɹ�����ˢ�µ�ǰ����
		String mrNo = this.getValueString("MR_NO");
		onClear();
		//add by huangtt 20160530 "20160508-�����������������շѽ�������ˢ�¹���		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}
	/**
	 * ��ѯͬһ������������еļ�����֮��
	 * yanjing 20141203
	 */
	private double selectReduceAmt(String caseNo){
		String sql = "SELECT SUM(REDUCE_AMT) AS REDUCE_AMT FROM BIL_REDUCEM WHERE CASE_NO = '"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		double reduceAmt = parm.getDouble("REDUCE_AMT",0);
		return reduceAmt;
	}

	/**
	 * У�鱣����������Ƿ�ִ��
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
					this.messageBox("¼���ҽ��ִ�п��Ҳ���Ϊ��!");
					return false;
				}
				if (checkParm.getBoolean("PRINT_FLG", i)) {
					this.messageBox("�Ѵ�Ʊ,�����˷�!");
					return false;
				}
				checkParmEnd.addData("DOSAGE_QTY", checkParm.getData(
						"DOSAGE_QTY", i));

			}
		}
		// ȫԺ
		if (!this.getPopedem("ALL")) {
			if (!chekeRolo(checkParmEnd)) {
				return false;
			}
		}
		int count = orderParm.getCount("CASE_NO");
		//		for (int i = 0; i < count; i++) {
		//			//===zhangp �������޸� start
		//			if (!Operator.getSpcFlg().equals("Y")) {
		//				if ("PHA".equals(orderParm.getValue("CAT1_TYPE", i))
		//						&& !opb.checkDrugCanUpdate(orderParm.getRow(i), i) && !EKTIO.getInstance().ektAyhSwitch()) {
		//					this.messageBox("ҩƷ����˻�ҩ,�����˷�!");
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
		//					// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		//					if ("W".equals(orderParm.getValue("PHA_TYPE"))
		//							|| "C".equals(orderParm.getValue("PHA_TYPE"))) {
		//						// �ж��Ƿ����
		//						needExamineFlg = PhaSysParmTool.getInstance()
		//								.needExamine();
		//					}
		//					// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
		//					if (needExamineFlg) {
		//						// System.out.println("�����"); 
		//						// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
		////						if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0
		////								&& spcOpdOrderReturnDto.getPhaRetnCode()
		////										.length() == 0) {
		////							this.messageBox("ҩƷ�����,�����˷�!");
		////							return false;
		////						}
		//						if (spcReturn.getValue("PhaCheckCode").length() > 0
		//								&& spcReturn.getValue("PhaRetnCode")
		//										.length() == 0 && !EKTIO.getInstance().ektAyhSwitch()) {
		//							this.messageBox("ҩƷ�����,�����˷�!");
		//							return false;
		//						}
		//					} else {// û��������� ֱ����ҩ
		//						// �ж��Ƿ�����ҩҩʦ
		//						// System.out.println("�����");
		//						if (spcReturn.getValue("PhaDosageCode").length() > 0
		//								&& spcReturn.getValue("PhaRetnCode")
		//										.length() == 0 && !EKTIO.getInstance().ektAyhSwitch()) {
		//							this.messageBox("ҩƷ�ѷ�ҩ,�����˷�!");
		//							return false;// �Ѿ���ҩ���������޸�
		//						}
		//					}
		//				}
		//			}
		//			//===zhangp �������޸� end	
		//			//====zhangp 20140113
		//			if (ektTCharge.isSelected()) {
		//				if (!"PHA".equals(orderParm.getValue("CAT1_TYPE", i))
		//						&& "Y".equals(orderParm.getValue("EXEC_FLG", i))) {
		//					this.messageBox("�ѵ���,�����˷�!");
		//					return false;
		//				}
		//			}
		//			// ===zhangp 20120421 end
		//		}
		// ���ͽӿڼ���
		if (!flg) {
			getCashHl7Parm(checkParm, hl7ParmEnd);
		}
		return true;
	}

	/**
	 * �ֽ�������ͽӿ�
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
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean onEktSave() {
		String mrNo = this.getValueString("MR_NO");
		// ======zhangp 20120227 modify start
		this.callFunction("UI|save|setEnabled", false);
		this.callFunction("UI|CHARGE|setEnabled", false);
		if (!isEKT) {
			this.messageBox("���ȡҽ�ƿ���Ϣ");
			return false;
		}
		int type = 0;
		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("�벻Ҫ��ѡȫ��");
			return false;
		}
		TParm modifyOrderParm = opb.getPrescriptionList().getParm();
		if (!checkOnEktSave(true, null, modifyOrderParm)) {
			return false;
		}
		// ���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
		if (!EKTIO.getInstance().ektSwitch()) {
			messageBox_("ҽ�ƿ�����û������!");
			return false;
		}
		//zhangp 20131208
		boolean feeFlg = false;
		if(EKTIO.getInstance().ektAyhSwitch()){
			TParm preParm = EKTpreDebtTool.getInstance().checkMasterForCharge(modifyOrderParm, pat.getMrNo(), reg.caseNo());
			if(preParm.getErrCode()<0){
				if(messageBox("�Ƿ��������", preParm.getErrText()+",��������?", 0) != 0){
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

		// �õ��շ���Ŀ
		// sendHL7Parm = hl7ParmEnd;
		// ����opb�ı��淽��
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		// ==================pangben ��Ҫ�޸ĵ�����
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
				// ��дҽ�ƿ����
				if (writeParm.getErrCode() < 0)
					System.out.println("err:" + writeParm.getErrText());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("err:ҽ�ƿ�д������ʧ��");
				e.printStackTrace();
			} 
			if (EKTmessage) {
				this.messageBox("ҽ�ƿ��˷�ʧ��!");
			} else {
				this.messageBox("�ɷ�ʧ��!");
			}
			return false;
		} else {
			if (EKTmessage) {
				this.messageBox("ҽ�ƿ��˷ѳɹ�");
			} else {
				this.messageBox("�����ɹ�");
			}
			opdOrderSpc(parm.getParm("orderParm"));//===pangben 2013-5-22 ���������Ԥ����
			// ����HL7
			// ����HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(
					parm.getParm("orderParm").getParm("hl7Parm"),
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// ��ʿ����ƷѲ���ʾ��Ϣ
		String re = EKTIO.getInstance().check(tredeNo, reg.caseNo(),StringTool.round(reduceAmt+selectReduceAmt(reg.caseNo()), 2));
		if (re != null && re.length() > 0) {
			this.messageBox_(re);
			this.messageBox_("����������Ϣ������ϵ");
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
		// �շѳɹ�����ˢ�µ�ǰ����
		//zhangp
		if(prtFlg){
			//			onEKT();
			onEKTPrint("", true, null, null,null);
		}


		onClear();
		//add by huangtt 20160530 "20160508-�����������������շѽ�������ˢ�¹���		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}

	/**
	 * �ֽ��շѱ���
	 * 
	 * @param CONTRACT_CODE
	 *            String
	 * @return boolean
	 */
	public boolean onCashSave(String CONTRACT_CODE) {
		String mrNo = this.getValueString("MR_NO");
		opb.initBilInvoice();
		// ��˿�����
		if (opb.getBilInvoice() == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		
		//���鵱ǰ�Ƿ����Ѿ�ʹ�ù�������Ѿ�ʹ�ù�������Ʊ������ȡ��  add by huangtt 20170223 start
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
					this.messageBox("��ǰƱ�Ŵ�����ȥƱ�����ý������ţ�����");
					return false;
				}
				opb.initBilInvoice();
			}
		}
		
		
		//add by huangtt 20170223 end
		
		
		
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("Ʊ��������!");
			return false;
		}
		// ��˵�ǰƱ��
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| opb.getBilInvoice().getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			}
			return false;
		}
		// ��˵�ǰƱ��
		if (opb.getBilInvoice().getUpdateNo().equals(
				opb.getBilInvoice().getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
			// return;
		}

		TParm parm = new TParm();
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("�벻Ҫ��ѡȫ��");
			return false;
		}

		//==========================================================����Ǳ���֧��ҪУ�鲡���ı����Ƿ�����Ч����add by huangjw 20150907 start
		TParm tableParm = paymentTool.table.getParmValue();
		for(int i = 0; i < tableParm.getCount("PAY_TYPE"); i++){
			if(tableParm.getValue("PAY_TYPE",i).equals("BXZF")){
				String sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " +
						" WHERE MR_NO = '"+pat.getMrNo()+"' AND VALID_FLG = 'Y'" +
						" AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
				TParm dataParm = new TParm(TJDODBTool.getInstance().select(sql));
				if(dataParm.getCount() < 0){
					if(JOptionPane.showConfirmDialog(null, "�ò������ղ�����Ч���ڣ��Ƿ����", "��Ϣ",
							JOptionPane.YES_NO_OPTION) == 0){
						break;//����ѭ��
					}else{
						return false;//������һ������
					}
				}
			}

		}
		//===========================================================����Ǳ���֧��ҪУ�鲡���ı����Ƿ�����Ч����add by huangjw 20150907 end 
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
			this.messageBox("�����������ͬ��֧����ʽ��");
			return flg2;
		}
		//add by huangtt 20160107 start ����֧����ʽ����ʱ�ж�Ǯ����Ӧ�ս���Ƿ�һ��
		double sum = 0;
		for (int i = 0; i < payTypeParm.getCount("AMT"); i++) {
			sum += payTypeParm.getDouble("AMT", i);
		}
		BigDecimal ar_amt_bd = new BigDecimal(this.getValueDouble("TOT_AMT")).setScale(2, RoundingMode.HALF_UP); 
		BigDecimal sumPay_bd = new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP);

		if(ar_amt_bd.compareTo(sumPay_bd) != 0){
			this.messageBox("����֧����ʽ���Ϊ"+sumPay_bd+"��Ӧ�ս��Ϊ"+ar_amt_bd+"������Ǯ�����ȣ�����������֧����ʽǮ����");
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
		// �õ��շ���Ŀ
		// sendHL7Parm = hl7ParmEnd;
		// ����opb�ı��淽��
		if (systemCode != null && "ONW".equals(systemCode)
				|| this.getPopedem("NOBILL")) {
			this.callFunction("UI|ektPrint|setEnabled", false);
			this.messageBox("��ʿ���տ�Ȩ��");
			return false;
		}
		// ҽ������
		TParm insResult = null;
		if ("I".equals(this.getValueString("BILL_TYPE"))) { // ҽ��������
			insResult = insCashSave();
			if (null == insResult) {
				return false;
			}
		}else{
			//			TParm orderParm = opb.getReduceCashParm(ektTCharge.isSelected());
			//			TParm parmReduce = onReduce(false,orderParm);//���ü������
			TParm parmReduce = reduceResult;
			if(parmReduce==null)
				return false;
			payTypeParm.setData("parmReduce",parmReduce.getData());//�ֽ�������====pangben 2014-8-21
		}
		//�ֽ��Ʊ������У���Ƿ����֧������΢�Ž��
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
			this.messageBox("�ɷ�ʧ��!");
			return false;
		} else {
			this.messageBox("�����ɹ�");
			// ����HL7
			TParm resultParm = OPBTool.getInstance().sendHL7Mes(hl7ParmEnd,
					getValue("PAT_NAME").toString(), EKTmessage, reg.caseNo());
			if (resultParm.getErrCode() < 0) {
				this.messageBox(resultParm.getErrText());
			}
		}
		// �շѳɹ�����ˢ�µ�ǰ����
		onEKTPrint(CONTRACT_CODE, false, insResult, payTypeParm,payCashParm); // ============pangben
		// 20110818 ��Ӳ���

		onClear();
		//add by huangtt 20160530 "20160508-�����������������շѽ�������ˢ�¹���		
		if(mrNo.length() > 0){
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
		return true;
	}

	/**
	 * ҽ���ֽ��շѲ���
	 * 
	 * @return TParm
	 */
	private TParm insCashSave() {
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", reg.caseNo());
		selOpdParm.setData("REGION_CODE", Operator.getRegion());
		TParm opdParm = OrderTool.getInstance()
				.selDataForOPBCashIns(selOpdParm);
		TParm insReturnParm = insExeFee(opdParm, true); // ҽ���շѲ���
		if (null == insReturnParm) {
			this.messageBox("ҽ������ʧ��");
			return null;
		}
		insParm.setData("ACCOUNT_AMT", insReturnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
		insParm
		.setData("UACCOUNT_AMT", insReturnParm
				.getDouble("UACCOUNT_AMT")); // �ֽ���
		insParm.setData("comminuteFeeParm", insReturnParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�����
		insParm.setData("settlementDetailsParm", insReturnParm.getParm(
				"settlementDetailsParm").getData()); // ���ý���
		TParm ins_result = new TParm();
		// ҽ������
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			ins_result = onINSAccntClient(opdParm); // �ֽ�������ز����ݻ�ÿ۳�ҽ������Ժ��ҽ����Ϣ
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("ҽ���շ�ʧ��");
				return null;
			}
			if (null != ins_result.getValue("MESSAGE")
					&& ins_result.getValue("MESSAGE").length() > 0) {
				// �ֽ�֧��
				return null;
			} else { // �ֽ�ʹ�� ҽ�ƿ��Ѿ���ӡ�վݲ���Ҫ

				insFlg = true; // �ж�ҽ����;״ִ̬��
			}
		}
		return insReturnParm;
	}

	/**
	 * �����ӡ����
	 * 
	 * @param receiptNo
	 *            String[]
	 */
	public void dealPrintData(String[] receiptNo) {
		int size = receiptNo.length;
		for (int i = 0; i < size; i++) {
			// ȡ��һ��Ʊ�ݺ�
			String recpNo = receiptNo[i];
			if (recpNo == null || recpNo.length() == 0) {
				return;
			}
			// ���ô�ӡһ��Ʊ�ݵķ���
			onPrint(new OPBReceipt().getOneReceipt(recpNo));
		}
	}

	/**
	 * ��ӡƱ��
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
	 * ��װƱ������
	 */
	public void setOpbReceipt() {
		// �õ�һ��Ʊ�ݶ���
		OPBReceipt receiptOne = opb.getReceiptList().newReceipt();
		// �ֽ�֧��
		receiptOne.setPayCash(getValueDouble("PAY_CASH"));
		// ����֧��
		receiptOne.setPayDepit(getValueDouble("PAY_DEBIT"));
		// ˢ��
		receiptOne.setPayBankCard(getValueDouble("PAY_BANK_CARD"));
		// ֧Ʊ
		receiptOne.setPayCheck(getValueDouble("PAY_CHECK"));
		// ҽ����
		receiptOne.setPayInsCard(getValueDouble("PAY_INS_CARD"));
		// ҽ�ƿ�
		receiptOne.setPayMedicalCard(getValueDouble("PAY_MEDICAL_CARD"));
		// ����֧��(���Բ�)
		receiptOne.setPayOther1(getValueDouble("PAY_OTHER1"));
		// ֧Ʊ��ע
		receiptOne.setPayRemark(getValueString("PAY_REMARK"));
		// Ѻ��
		receiptOne.setPayBilPay(getValueDouble("PAY_BILPAY"));
		// ҽ��
		receiptOne.setPayIns(getValueDouble("PAY_INS"));
		// �ܽ��
		receiptOne.setTotAmt(getValueDouble("TOT_AMT"));
		// �Էѽ��
		receiptOne.setArAmt(getValueDouble("TOT_AMT")
				- getValueDouble("PAY_INS"));
		// �ۿ۽��
		receiptOne.setReduceAmt(getValueDouble("REDUCE_AMT"));
		// �������
		receiptOne.setReduceDeptCode(getValueString("REDUCE_DEPT_CODE"));
		// ����ԭ��
		receiptOne.setReduceReason(getValueString("REDUCE_REASON"));
		// ������Ա
		receiptOne.setReduceRespond(getValueString("REDUCE_RESPOND"));
		// �վݷ��õ����(charge01~charge30)
		receiptOne.initCharge(opb.getChargeParm());
		// ��Ʊ�ݶ�������ܽ��
		opb.getBilinvrcpt().setArAmt(getValueDouble("TOT_AMT"));

	}

	/**
	 * ɾ������ҽ��
	 */
	public void onDelete() {
		// ���û���¿�����ҽ���޷�ɾ��
		// if (orderList == null) {
		// return;
		// }
		// �õ�Ҫɾ����table��
		if (this.getValueString("ALL").equals("Y")) {
			this.messageBox("�벻Ҫ��ѡȫ��");
			return;
		}
		// ===zhangp 20120424 start
		if (ektTCharge.isSelected()) {
			this.messageBox("���շ�ҽ������ɾ��");
			return;
		}
		// ===zhangp 20120424 end
		int removeRow = table.getSelectedRow();
		// ����Ƿ���Ȩ��ɾ��ѡ�е�ҽ��
		// ===zhangp 20120414 start
		Order order = (Order) table.getParmValue().getData("OBJECT", removeRow);
		// if (removeRow < drOrderCount && !deleteFun) {
		if (removeRow < drOrderCount
				&& !deleteFun
				&& !(order.getRxType().equals("7") || order.getRxType().equals(
						"0"))) {
			// ===zhangp 20120414 end
			this.messageBox("��ҽ����ҽ������!");
			return;
		}
		if (orderList == null) {
			if (!deleteSetCodeOrder(order, removeRow, true))
				return;
		}
		if (orderList != null) {
			// ============pangben 2013-1-7 ��������ɾ������ҽ��ϸ��
			Order orderTemp = null;// ɾ������ҽ��ϸ��
			orderList.removeData(order);
			table.removeRow(removeRow);
			if (order.getOrderSetGroupNo() > 0) {// �Ƴ�����ҽ��
				for (int i = orderList.getTableParm().getCount("ORDER_CODE") - 1; i >= 0; i--) {
					orderTemp = orderList.getOrder(i);
					if (null == orderTemp)
						continue;
					if (null != orderTemp.getRxNo()
							&& orderTemp.getRxNo().equals(order.getRxNo())
							&& orderTemp.getOrderSetGroupNo() == order
							.getOrderSetGroupNo()) {
						orderList.removeData(orderTemp);// �Ƴ�ϸ��
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
	 * ɾ������ ɾ�������ϼ���ҽ��ϸ������ =======pangben 2013-1-10
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
				messageBox("ɾ��ʧ��");
				return false;
			}
			if (flg) {
				OdoUtil.deleteOrderSet(orderList, order);
				// ��ɾ����ҽ����table��һ��
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
				messageBox("ɾ��ʧ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * ��շ���
	 */
	public void onClear() {

		// ====zhangp 20120227 modify start
		checkBoxChargeAll.setEnabled(true);
		checkBoxChargeAll.setSelected(true);
		// Ԥ���������
		this.callFunction("UI|STARTTIME|setValue", SystemTool.getInstance()
				.getDate());//=======yanjing 20131231 ���
		this.callFunction("UI|save|setEnabled", true);
		this.callFunction("UI|CHARGE|setEnabled", true);
		// =====zhangp 20120227 modify end
		clearValue("REDUCE_REASON;REDUCE_DEPT_CODE;REDUCE_RESPOND;REDUCE_AMT;CUSTOMER_SOURCE;REASSURE_FLG");
		clearValue("PAY_CASH;PAY_MEDICAL_CARD;PAY_BILPAY;PAY_INS;PAY_BANK_CARD;PAY_CHECK;");
		clearValue("PAY_DEBIT;PAY_OTHER1;TOT_AMT;PAY;PAY_RETURN;PAY_INS_CARD;PAY_OTHER2");
		clearValue("PAY_REMARK;MR_NO;PAT_NAME;IDNO;AGE;SEX_CODE;CTZ1_CODE;BIRTH_DATE;REGMETHOD_CODE");
		clearValue("CTZ2_CODE;CTZ3_CODE;PRESCRIPTION;DEPT_CODE;DR_CODE;REALDEPT_CODE;REALDR_CODE;CAT1_TYPE;EKT_CURRENT_BALANCE;AMT;PAY_OTHER3;PAY_OTHER4;GIFT_CARD;GIFT_CARD2;NO_PAY_OTHER;NO_PAY_OTHER_ALL");
		clearValue("INSURE_INFO");//������Ϣ
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
		// ���table����
		table.removeRowAll();
		setViewModou(false);
		opb = null;

		//delete by huangtt 20141126
		//		// �����������������
		//		unLockPat();

		reg = null;
		pat = null;
		reduceAmt = 0.00;
		// ��ǰѡ�е���
		selectRow = -1;
		// �Ƿ�����������ǩ
		pFlg = "N";
		// ��¼ҽ������ҽ��������
		drOrderCount = -1;
		// �½�orderlist
		orderList = null;
		setViewModou(false);
		setValue("BILL_TYPE", "C");
		/**
		 * �ж��Ƿ�ΪȫԺ���鳤Ȩ��
		 */
		if (this.getPopedem("LEADER") || this.getPopedem("ALL")) {
			//			callFunction("UI|BILL_TYPE|setEnabled", true);
		} else {
			callFunction("UI|BILL_TYPE|setEnabled", false);
			// ======zhangp 20120227 modify start
			// ��ʼ��Ʊ��
			BilInvoice bilInvoice = new BilInvoice();
			if (!systemCode.equals("") && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {
				this.callFunction("UI|ektPrint|setEnabled", false);
			} else {
				initBilInvoice(bilInvoice.initBilInvoice("OPB"));
			}
			// ===============pangben 2012-3-30 �ܿ�
			//			this.callFunction("UI|MR_NO|setEnabled", false);
			// =======zhang 20120227 modify end
		}
		/**
		 * ���ݽ���Ĭ��ҽ������
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
		// ==================pangben modify 20110804 ɾ����ť����
		deleteFun = false; // �趨ҽ��ɾ��
		drOrderCountFalse = false; // ��һ�μ�¼���ﲡ����ҽ����Ϣ
		drOrderCountTemp = 0; // ��һ�μ�¼���ﲡ����ҽ����Ϣ
		feeShow = false; // �ܿأ���ʾ���ʹ�� pangben modify 20110804
		isbill = false; // �ܿأ��Ƿ���� pangben modify 20110818
		EKTmessage = false; // �ܿ� ҽ�ƿ��˷Ѳ���
		isEKT = false; // ҽ�ƿ���Ϣ��ȡ����
		TParm reValueParm = new TParm();
		reduceResult = reValueParm;
		tredeParm = null; // �ж�֧����ʽ
		insParm = null; // ҽ�� ����
		insFlg = false; // ҽ������ȡ����
		resultBill = null; // ��������
		// ===zhangp 20120309 modify start
		ektTCharge.setEnabled(false);
		// ==zhangp 20120309 modify end
		// ҽ�������ӡ
		callFunction("UI|insPrint|setEnabled", false);
		// ҽ�ƿ���ӡ
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
	 * ������ʾ�Ƿ��շ�
	 * 
	 * @param view
	 *            boolean
	 */
	public void setViewModou(boolean view) {
		checkBoxNotCharge.setSelected(!view);
		checkAll.setSelected(view);
	}

	/**
	 * �һ�MENU�����¼�
	 */
	public void onTableRightClicked() {
		int tableSelectRow = table.getSelectedRow();
		// ���ҽ����Ϣ...����Ǽ���ҽ��....�����pha
		// �õ��������е�order
		Order order = (Order) table.getParmValue().getData("OBJECT",
				tableSelectRow);
		if (order.getOrdersetCode() != null
				&& order.getOrdersetCode().length() > 0) {
			table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,onOrderSetShow");
			return;
		}
		if (order.getOrderCat1Code().contains("PHA")) {
			table.setPopupMenuSyntax("��ʾҩ����Ϣ,onSysFeeShow");
			return;
		}
		table.setPopupMenuSyntax("");
	}

	/**
	 * �һ�MENU��ʾ����ҽ���¼�
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
	 * �һ�MENU��ʾSYS_FEE�¼�
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
	 * ���ֲ���ǰ�����ݼ��
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// �����
		if (opb == null) {
			return true;
		}
		// ��˿��س�
		if (checkOpenBill()) {
			return true;
		}
		// ���ҽ��
		if (opb.checkOrder()) {
			this.messageBox("û��Ҫ�շѵ�ҽ��");
			return true;
		}
		return true;
	}

	/**
	 * ��˿�����
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
			this.messageBox("û�п���!");
			return true;
		}

		return false;
	}

	/**
	 * ���ҽ��
	 * 
	 * @return boolean
	 */
	public boolean checkOrder() {
		if (table.getRowCount() <= 1) {
			this.messageBox("���κ���Ҫ�����ҽ��!");
			return true;
		}
		return false;
	}

	/**
	 * ��������
	 * 
	 * @return boolean true �ɹ� false ʧ��
	 */
	public boolean lockPat() {
		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// �ж��Ƿ����
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
				} else if ("ONW".equals(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿ�:����
					parm.setData("WINDOW_ID", "ONW01");
				else if ("ENW".equals(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿ�:����
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
									+ Operator.getName() + " ǿ�ƽ���[" + aa
									+ " �����ţ�" + pat.getMrNo() + "]");
				} else {
					this.onClear();
					return false;
				}
			} else {
				if (this.messageBox("�Ƿ����", PatTool.getInstance()
						.getLockParmString(pat.getMrNo()), 0) == 0) {
					PatTool.getInstance().unLockPat(pat.getMrNo());
					PATLockTool.getInstance().log(
							"ODO->" + SystemTool.getInstance().getDate() + " "
									+ Operator.getID() + " "
									+ Operator.getName() + " ǿ�ƽ���[" + aa
									+ " �����ţ�" + pat.getMrNo() + "]");
				} else {
					onClear();
					return false;
				}
			}
		}
		// ��������Ϣ
		if (!PatTool.getInstance().lockPat(pat.getMrNo(), checklockPat())) {
			onClear();
			return false;
		}
		return true;
	}
	/**
	 * pangben 2013-5-15
	 * ��ʿվ��������
	 * @return
	 */
	private String checklockPat(){
		String type = "OPB";// ��ӻ�ʿվ�ܾ��������ܣ������ż��ﻤʿվ����
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
	 * �Ƿ����ڱ���������ס����
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
	 * ��������
	 */
	public void unLockPat() {
		if (pat == null) {
			return;
		}
		// �ж��Ƿ����
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
	 * �ֽ�س�ʵ�� PAY_CASH
	 */
	public void onPayCash() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// //ˢ��
		// double payBankCard = getValueDouble("PAY_BANK_CARD");
		// ֧Ʊ
		double payCheck = getValueDouble("PAY_CHECK");
		// ҽ����
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// ҽ�ƿ�
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// ����֧��
		double payOther2 = getValueDouble("PAY_OTHER1");
		// Ѻ��
		double payBilPay = getValueDouble("PAY_BILPAY");
		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payBankCard = arAmt - payCash - payDebit - payCheck
				- payMedicalCard - payOther2 - payBilPay - payInsCard;
		// ��ʽ�����
		payBankCard = StringTool.round(payBankCard, 2);
		if (payBankCard < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_BANK_CARD|setValue", payBankCard);
		// ���п��õ�����
		callFunction("UI|PAY_BANK_CARD|grabFocus");
	}

	/**
	 * ���п��س��¼� PAY_BANK_CARD
	 */
	public void onPayBankCard() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// ˢ��
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// ֧Ʊ
		double payCheck = getValueDouble("PAY_CHECK");
		// ҽ����
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// //ҽ�ƿ�
		// double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// ����֧��
		double payOther2 = getValueDouble("PAY_OTHER1");
		// Ѻ��
		double payBilPay = getValueDouble("PAY_BILPAY");
		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payMoidCard = arAmt - payCash - payDebit - payCheck
				- payBankCard - payOther2 - payBilPay - payInsCard;
		// ��ʽ�����
		payMoidCard = StringTool.round(payMoidCard, 2);
		if (payMoidCard < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_MEDICAL_CARD|setValue", payMoidCard);
		// ҽ�ƿ��õ�����
		callFunction("UI|PAY_MEDICAL_CARD|grabFocus");

	}

	/**
	 * ҽ�ƿ��س��¼� PAY_MEDICAL_CARD
	 */
	public void onPayMediCard() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// ˢ��
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// ֧Ʊ
		double payCheck = getValueDouble("PAY_CHECK");
		// //ҽ����
		// double payInsCard = getValueDouble("PAY_INS_CARD");
		// ҽ�ƿ�
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// ����֧��
		double payOther2 = getValueDouble("PAY_OTHER1");
		// Ѻ��
		double payBilPay = getValueDouble("PAY_BILPAY");
		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payInsCard = arAmt - payCash - payDebit - payCheck - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// ��ʽ�����
		payInsCard = StringTool.round(payInsCard, 2);
		if (payInsCard < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_INS_CARD|setValue", payInsCard);
		// ҽ�ƿ��õ�����
		callFunction("UI|PAY_INS_CARD|grabFocus");
	}

	/**
	 * ҽ�����س��¼� PAY_INS_CARD
	 */
	public void onPayInsCard() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// ˢ��
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// //֧Ʊ
		// double payCheck = getValueDouble("PAY_CHECK");
		// ҽ����
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// ҽ�ƿ�
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// /����֧��
		double payOther2 = getValueDouble("PAY_OTHER1");
		// Ѻ��
		double payBilPay = getValueDouble("PAY_BILPAY");
		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payCheck = arAmt - payCash - payDebit - payInsCard - payBankCard
				- payOther2 - payBilPay - payMedicalCard;
		// ��ʽ�����
		payCheck = StringTool.round(payCheck, 2);
		if (payCheck < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}

		// ��ֵ
		callFunction("UI|PAY_CHECK|setValue", payCheck);
		// ֧Ʊ֧���õ�����
		callFunction("UI|PAY_CHECK|grabFocus");

	}

	/**
	 * ֧Ʊ֧���س��¼� PAY_CHECK
	 */
	public void onPayCheck() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// ˢ��
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// ֧Ʊ
		double payCheck = getValueDouble("PAY_CHECK");
		// ҽ����
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// ҽ�ƿ�
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// ����֧��
		double payOther2 = getValueDouble("PAY_OTHER2");
		// //Ѻ��
		// double payBilPay = getValueDouble("PAY_BILPAY");

		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payBilPay = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payOther2 - payCheck - payMedicalCard;
		// ��ʽ�����
		payBilPay = StringTool.round(payBilPay, 2);
		if (payBilPay < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_BILPAY|setValue", payBilPay);
		// Ѻ��֧���õ�����
		callFunction("UI|PAY_BILPAY|grabFocus");

	}

	/**
	 * Ѻ��֧�� PAY_BILPAY
	 */
	public void onPayBilPay() {
		// �ֽ�֧��
		double payCash = getValueDouble("PAY_CASH");
		// ˢ��
		double payBankCard = getValueDouble("PAY_BANK_CARD");
		// ֧Ʊ
		double payCheck = getValueDouble("PAY_CHECK");
		// ҽ����
		double payInsCard = getValueDouble("PAY_INS_CARD");
		// ҽ�ƿ�
		double payMedicalCard = getValueDouble("PAY_MEDICAL_CARD");
		// //����֧��
		// double payOther2 = getValueDouble("PAY_OTHER2");
		// Ѻ��
		double payBilPay = getValueDouble("PAY_BILPAY");

		// ҽ��
		double payIns = getValueDouble("PAY_INS");
		// �ܽ��
		double totAmt = getValueDouble("TOT_AMT");
		// ����֧��
		double payDebit = getValueDouble("PAY_DEBIT");
		// �ۿ۽��
		double reduceAmt = getValueDouble("REDUCE_AMT");
		// �Էѽ��ܽ��-ҽ��֧��-����-���⣩
		double arAmt = totAmt - payIns - payDebit - reduceAmt;
		// �������п�֧�����
		double payOther2 = arAmt - payCash - payDebit - payInsCard
				- payBankCard - payCheck - payMedicalCard - payBilPay;
		// ��ʽ�����
		payOther2 = StringTool.round(payOther2, 2);
		if (payOther2 < 0) {
			this.messageBox("¼�����ȷ!���������!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_OTHER2|setValue", payOther2);
		// ����֧���õ�����
		callFunction("UI|PAY_OTHER2|grabFocus");
	}

	/**
	 * ����֧�� PAY_OTHER2
	 */
	public void onPayOther2() {
		// ��˷����¼�
		// �տ���õ�����
		callFunction("UI|PAY|grabFocus");
	}

	/**
	 * ������س��¼� PAY
	 */
	public void onPay() {
		// �ۿ۽��
		double pay = getValueDouble("PAY");
		// �ۿ۽��
		double arAmt = getValueDouble("TOT_AMT");
		if (pay - arAmt < 0 || pay == 0) {
			this.messageBox("����!");
			return;
		}
		// ��ֵ
		callFunction("UI|PAY_RETURN|setValue", StringTool.round((pay - arAmt),
				2));
		// this.grabFocus("CHARGE");
	}

	/**
	 * ������ϸ��ѯ���˷�
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
		// ͨ��reg��caseNo���³�ʼ��opb
		//		opb = OPB.onQueryByCaseNo(reg);
		//		onlyCaseNo = "";
		//		onlyCaseNo = opb.getReg().caseNo();
		// �������ϲ��ֵط���ֵ
		//		if (opb == null) {
		// this.messageBox_(555555555);
		//			this.messageBox("�˲�����δ����!");
		//			 return;=====pangben modify 20110801
		//		}
		// ��ʼ��opb�����ݴ���
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
	 * ���˷�����ϸ��ѯ���˷� =========================pangben 20110823
	 */
	public void onBackContract() {
		if (opb == null) {
			return;
		}
		TParm opbParm = new TParm();
		opbParm.setData("MR_NO", opb.getPat().getMrNo());
		opbParm.setData("CASE_NO", opb.getReg().caseNo());
		this.openDialog("%ROOT%\\config\\opb\\OPBBackContract.x", opbParm);
		// ͨ��reg��caseNo���³�ʼ��opb
		opb = OPB.onQueryByCaseNo(reg);
		onlyCaseNo = "";
		onlyCaseNo = opb.getReg().caseNo();
		// �������ϲ��ֵط���ֵ
		if (opb == null) {
			// this.messageBox_(555555555);
			this.messageBox("�˲�����δ����!");
			// return;=====pangben modify 20110801
		}
		// ��ʼ��opb�����ݴ���
		afterInitOpb();
		this.onClear();

	}

	/**
	 * ���¼������
	 */
	public void setFeeReview() {
		double fee = TypeTool.getDouble(getValue("TOT_AMT"));
		if ("E".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // ҽ�ƿ�
			callFunction("UI|PAY_MEDICAL_CARD|setValue", fee);
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(true);

		} else if ("C".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // �ֽ�
			callFunction("UI|PAY_CASH|setValue", fee);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", 0.00);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("P".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // ����
			callFunction("UI|PAY_CASH|setValue", 0.00);
			callFunction("UI|PAY_MEDICAL_CARD|setValue", 0.00);
			callFunction("UI|PAY_DEBIT|setValue", fee);
			callFunction("UI|PAY_INS|setValue", 0.00);
			ektTCharge.setEnabled(false);
		} else if ("I".equals(TypeTool.getString(getValue("BILL_TYPE")))) { // ҽ��
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
		// System.out.println("�ɱ����Ĵ���"
		// + DeptTool.getInstance().getCostCenter("01020101", "0003"));
	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		txReadEKT();
		// ����ʱ��֧����ʽ�л�Ϊ��ҽ�ƿ���
		TRadioButton ektPay = (TRadioButton) getComponent("tRadioButton_1");
		ektPay.setSelected(true);
		onGatherChange(1);
	}

	public void onEKTPrint() {
		if (this.messageBox("��ʾ", "�Ƿ��ӡ", 2) != 0) {
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
	 * ҽ�ƿ����ֽ��Ʊ(���˵�λ���ֽ�ʹ��)
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
			this.messageBox("��ѡ�񲡻�");
		}
		// ��˿�����
		if (opb.getBilInvoice() == null) {
			this.messageBox_("����δ����!");
			return null;
		}

		
		if (opb.getBilInvoice().getUpdateNo().compareTo(
				opb.getBilInvoice().getEndInvno()) > 0) {
			this.messageBox("Ʊ��������!");
			return null;
		}
		// ��˵�ǰƱ��
		if (opb.getBilInvoice().getUpdateNo().length() == 0
				|| opb.getBilInvoice().getUpdateNo() == null) {
			if (systemCode != "" && "ONW".equals(systemCode)
					|| this.getPopedem("NOBILL")) {

			}else{
				this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			}
			return null;
		}
		TCheckBox mem_package = (TCheckBox) getComponent("MEM_PACKAGE");
		//pangben 2019-4-25 �ײʹ�Ʊ �жϲ���ҽ���Ƿ���ͬһ���ײ��У�����ͬһ���ײ���Ҫ��Ʊ��ӡ
		boolean memPartFlg = false;//��Ʊע��
		if(mem_package.isSelected()){
			//У��Ʊ���Ƿ����
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
				//����ײ�
				String updateNo = opb.getBilInvoice().getUpdateNo();
				for(int i= 0; i< memTradeParm.getCount()-1; i++){
					updateNo = StringTool.addString(updateNo);
				}
				if (updateNo.compareTo(
						opb.getBilInvoice().getEndInvno()) > 0) {
					this.messageBox("Ʊ��������!");
					return null;
				}
				memPartFlg = true;
			}
		}
		// ��˵�ǰƱ��
		if (opb.getBilInvoice().getUpdateNo().equals(
				opb.getBilInvoice().getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
			// return;
		}

		// ��ʾ��һƱ��
		callFunction("UI|UPDATE_NO|setValue", opb.getBilInvoice().getUpdateNo());
		String updateNo = this.getValueString("UPDATE_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", onlyCaseNo);
		parm.setData("MR_NO", pat.getMrNo());	 //modify by huangtt 20170605 ֱ��ȡpat�����mr_No	
		parm.setData("INV_NO", updateNo);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("START_INVNO", opb.getBilInvoice().getStartInvno());
		// =============pangben modify 201110817 start
		parm.setData("feeShow", feeShow); // ���ı���ʹ�ùܿ�
		parm.setData("TOT_AMT", this.getValueDouble("TOT_AMT"));
		parm.setData("billFlg", isbill ? "N" : "Y"); // ����: N ������:Y
		parm.setData("CONTRACT_CODE", contractCode); // ���˵�λ
		parm.setData("ADM_TYPE", reg.getAdmType()); // �Һŷ�ʽ :0 \E
		parm.setData("TAX_FLG", getValueString("TAX_FLG"));
		// =============pangben modify 201110817 stop
		TParm selOpdParm = new TParm();
		selOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
		
		TParm opdParm = new TParm();
		TParm result = new TParm();
		boolean flg = true; // ����ҽ�ƿ���Ʊ
		TParm bilExeParm = null;
		TParm opbReceiptParm = new TParm(); // ����վݺź�ҽ�ƿ����
		if (("E".equals(this.getValueString("BILL_TYPE")) && isEKT) || mem_package.isSelected()) { // ҽ�ƿ���Ʊ����
			if(!mem_package.isSelected()){
				opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
			}else{
				if(!memPartFlg){
					opdParm = OrderTool.getInstance().selDataForOPBEKTMem(selOpdParm);
				}else{
					//�ײͷ�Ʊ��ӡ
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
			System.out.println("�ֽ��Ʊ");
		} else if ("I".equals(this.getValueString("BILL_TYPE"))) { // ҽ��������
			// ִ��ҽ������
			if (ektPrintFlg && isEKT) {
				TParm readCard = EKTIO.getInstance().TXreadEKT();// ̩��ҽ�ƿ���������
				if (!this.getValue("MR_NO").equals(readCard.getValue("MR_NO"))) {
					this.messageBox("ҽ�ƿ�������Ϣ����,������ִ�д�Ʊ����");
					return null;
				}

				opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
				TParm insReturnParm = insExeFee(opdParm, true); // ҽ���շѲ���
				if (null == insReturnParm) {
					this.messageBox("ҽ������ʧ��");
					return null;
				}
				insParm.setData("ACCOUNT_AMT", insReturnParm
						.getDouble("ACCOUNT_AMT")); // ҽ�����
				insParm.setData("UACCOUNT_AMT", insReturnParm
						.getDouble("UACCOUNT_AMT")); // �ֽ���

				insParm.setData("comminuteFeeParm", insReturnParm.getParm(
						"comminuteFeeParm").getData()); // ���÷ָ�����
				insParm.setData("settlementDetailsParm", insReturnParm.getParm(
						"settlementDetailsParm").getData()); // ���ý���
			} else {
				opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
			}

			if (ektPrintFlg && isEKT) {
				parm.setData("ACCOUNT_AMT", insParm.getDouble("ACCOUNT_AMT")); // ҽ�����
				result = ektSavePrint(opdParm, opbReceiptParm, parm);
				isbill = false;
				flg = false; // ҽ�ƿ���Ʊ����
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
			this.messageBox("�Ѿ�����,����ӡƱ��");
			return null;
		}
		TParm recpParm = null;

		// �����վݵ�����:ҽ�ƿ��շѴ�Ʊ|�ֽ��շѴ�Ʊ||ҽ����Ʊ
		// shibl  RECEIPT_NO�غ�����  
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
			// ҽ����;״̬ɾ��
			if (!updateINSPrintNo(reg.caseNo(), "OPB")) {
				updateINSPrintNo(reg.caseNo(), "OPB");
			}
		}
		this.onClear();
		return new TParm();
	}

	/**
	 * ɾ��ҽ����;״̬
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
			result.setErr(-1, "ҽ����ִ�в���ʧ��");
			return false;
		}
		return true;
	}

	/**
	 * �޸�ҽ��Ʊ�ݺ�
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
			result.setErr(-1, "ҽ����ִ�в���ʧ��");
			return false;
		}
		return true;
	}
	/**
	 * ҽ�ƿ�ִ�д�Ʊ����
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
				this.messageBox("��ҽ�ƿ���Ч");
				return null;
			}
		}
		TParm result = new TParm();
		// opdParm = OrderTool.getInstance().selDataForOPBEKTC(selOpdParm);
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("�޿��շ�ҽ��");
			return null;
		}
		if (!insFee(opdParm, opbReceiptParm, EKTTemp))
			return null;
		parm.setData("opdParm", opdParm.getData()); // ���һ�����ܽ��
		parm.setData("REGION_CODE", Operator.getRegion()); // ����
		parm.setData("TAX_FLG", getValueString("TAX_FLG"));
		//caowl 20140425  startr
		//TParm oneReceiptParm = new TParm();
		//		TParm parmReduce = onReduce(true,null);//���ü������
		TParm parmReduce = reduceResult;
		if(parmReduce==null)
			return null;
		parm.setData("parmReduce",parmReduce.getData());
		//caowl 20140425 end
		result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBEktprint", parm);//���λ������������������������caowl����������������
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * �ֽ��Ʊ
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
		// �ֽ� ���� ҽ����ִ�в���
		int opdCount = opdParm.getCount("CASE_NO");
		if (opdCount <= 0) {
			this.messageBox("�޿��շ�ҽ��");
			return null;
		}
		// ҽ������
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			parm.setData("INS_RESULT", insResult.getData()); // ҽ������
			parm.setData("INS_FLG", "Y");
		}

		parm.setData("TAX_FLG", getValueString("TAX_FLG"));

		if(payTypeParm != null){
			TParm paymentParm=null;
			TParm parmReduce=payTypeParm.getParm("parmReduce");//���ⷵ������
			if (parmReduce.getValue("REDUCEFLG").equals("N")) {	
			}else{
				paymentParm=parmReduce.getParm("PAYMENT_PARM");//�ֽ����֧����ʽ
				parm.setData("REDUCE_AMT_SUM",parmReduce.getDouble("REDUCE_AMT"));//�����ܽ��===pangben 2014-8-21
			}
			parm.setData("PAY_TYPE_PARM", payTypeParm.getData());
			parm.setData("REDUCEFLG",parmReduce.getValue("REDUCEFLG"));//����ע�ǣ����������Ʊ
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
								||"PAY_TYPE10".equals(payTypeParm.getValue("PAY_TYPE", j))){//modify by kangy 20171018 ΢��֧������ӿ����ͱ�ע���׺�
							remark = payTypeParm.getValue("CARD_TYPE",j)+ "#" +payTypeParm.getValue("REMARKS",j);//ˢ���շѣ���ӿ����ͼ�����add by huangjw 20141230
						}else{
							remark=payTypeParm.getValue("REMARKS",j);//����֧����ʽ��ӱ�עpangben 2016-6-21
						}
						if (null!=paymentParm) {//�ֽ����===pangben 2014-8-21
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
				parm.setData(remarkKey,remark);//ˢ���շѣ���ӿ����ͼ�����add by huangjw 20141230 
			}
		}
		if(null!=payCashParm){//====pangben 2016-8-8 ���΢��֧�������׺�����ʾ
			parm.setData("payCashParm",payCashParm.getData());
		}
		TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
				"onOPBCashprint", parm);//���λ����������������������caowl����������������������
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ҽ���շ�
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
		// ҽ������
		if (null != insParm && null != insParm.getValue("CONFIRM_NO")
				&& insParm.getValue("CONFIRM_NO").length() > 0) {
			// ���ҽ������
			TParm ins_result = onINSAccntClient(opdParm); // ҽ�����������ز����ݻ�ÿ۳�ҽ������Ժ��ҽ����Ϣ
			if (ins_result.getErrCode() < 0) {
				err(ins_result.getErrCode() + " " + ins_result.getErrText());
				this.messageBox("E0005");
				return false;
			}
			insFlg = true; // �ж��Ƿ�ִ��ҽ����;״̬ɾ��
			// ҽ���˷ѻس�ҽ�ƿ�������
			// orderParm.setData("INS_FLG", "N");// ��ҽ��������
			opdParm.setData("AMT", -insParm.getDouble("ACCOUNT_AMT"));
			opdParm.setData("NAME", pat.getName());
			opdParm.setData("SEX", pat.getSexCode() != null
					&& pat.getSexCode().equals("1") ? "��" : "Ů");
			opdParm.setData("INS_FLG", "Y"); // ҽ��ʹ��
			// ��Ҫ�޸ĵĵط�
			opdParm.setData("MR_NO", pat.getMrNo());
			opdParm.setData("RECP_TYPE", "OPB"); // �շ�����

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
	 * ��ӡƱ�ݷ�װ===================pangben 20111014
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
		// ������Ա������
		String spPatType = "";
		// ������Ա���
		String spcPerson = "";
		double startStandard = 0.00; // �𸶱�׼
		double accountPay = 0.00; // ����ʵ���ʻ�֧��
		double gbNhiPay = 0.00; // ҽ��֧��
		String reimType = ""; // �������
		double gbCashPay = 0.00; // �ֽ�֧��
		double agentAmt = 0.00; // �������
		double unreimAmt = 0.00;// ����δ�������
		double difference = 0.00;//�Ż�
		String reduceReason = "";
		// ҽ����Ʊ����
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
			unreimAmt = insOpdParm.getDouble("UNREIM_AMT", 0);// ����δ����
			insCrowdType = insOpdParm.getValue("INS_CROWD_TYPE", 0); // 1.��ְ
			// 2.�Ǿ�
			insPatType = insOpdParm.getValue("INS_PAT_TYPE", 0); // 1.��ͨ
			// ������Ա������
			spPatType = insPatparm.getValue("SPECIAL_PAT", 0);
			// ������Ա���
			spcPerson = getSpPatDesc(spPatType);
			// ��ְ��ͨ
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
			// ��ְ����
			if (insCrowdType.equals("1") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOT_AMT", 0)
					- insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0)
					- insOpdParm.getDouble("ARMY_AI_AMT", 0);
				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("UNACCOUNT_PAY_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
			// �Ǿ�����
			if (insCrowdType.equals("2") && insPatType.equals("2")) {

				startStandard = insOpdParm.getDouble("INS_STD_AMT", 0);
				// ����ʵ���ʻ�֧��
				accountPay = insOpdParm.getDouble("ACCOUNT_PAY_AMT", 0);
				if (reimType.equals("1"))
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
					+ insOpdParm.getDouble("ARMY_AI_AMT", 0)
					+ insOpdParm.getDouble("FLG_AGENT_AMT", 0);
				else
					// ҽ��֧��
					gbNhiPay = insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
					+ insOpdParm.getDouble("FLG_AGENT_AMT", 0);

				gbNhiPay = TiMath.round(gbNhiPay, 2);
				// �ֽ�֧��
				gbCashPay = insOpdParm.getDouble("TOT_AMT", 0)
						- insOpdParm.getDouble("TOTAL_AGENT_AMT", 0)
						- insOpdParm.getDouble("FLG_AGENT_AMT", 0)
						- insOpdParm.getDouble("ARMY_AI_AMT", 0)
						+ insOpdParm.getDouble("UNREIM_AMT", 0);
				gbCashPay = TiMath.round(gbCashPay, 2);
				// �������
				agentAmt = insOpdParm.getDouble("ARMY_AI_AMT", 0);
			}
		}
		// INS_CROWD_TYPE, INS_PAT_TYPE
		// Ʊ����Ϣ
		// ����
		//		oneReceiptParm.setData("PAT_NAME", "TEXT", opb.getPat().getName());
		oneReceiptParm.setData("PAT_NAME",  opb.getPat().getName());
		//�Ա�
		oneReceiptParm.setData("SEX_CODE",  opb.getPat().getSexString());

		// ������Ա���
		oneReceiptParm.setData("SPC_PERSON", "TEXT",
				spcPerson.length() == 0 ? "" : spcPerson);
		// ��ᱣ�Ϻ�
		oneReceiptParm.setData("Social_NO", "TEXT", cardNo);
		// ��Ա���
		oneReceiptParm.setData("CTZ_DESC", "TEXT", "ְ��ҽ��");
		// �������
		// ======zhangp 20120228 modify start
		if ("1".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			if (reg.getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "��ͳ");
		} else if ("2".equals(insPatType) || "3".equals(insPatType)) {
			oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			if (reg.getAdmType().equals("E")) {
				oneReceiptParm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
			// oneReceiptParm.setData("Cost_class", "TEXT", "����");
		}
		// =====zhangp 20120228 modify end
		// ҽ�ƻ�������
		oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(opb.getReg().getRegion()));
		// �𸶽��
		oneReceiptParm.setData("START_AMT", "TEXT", StringTool.round(
				startStandard, 2));
		// ����޶����
		oneReceiptParm.setData("MAX_AMT", "TEXT", unreimAmt == 0 ? "--" : df
				.format(unreimAmt));
		// �˻�֧��
		oneReceiptParm.setData("DA_AMT", "TEXT", df.format(accountPay));


		/*----add by lich 20140928 ��ӱ�����Ϣ----start*/

		//add by huangtt 20140930  start
		String ownSql = "SELECT SUM(OWN_AMT) OWN_AMT ,SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"
				+ recpParm.getValue("CASE_NO")
				+ "' AND RECEIPT_NO='"
				+ recpParm.getValue("RECEIPT_NO") + "' AND (BILL_FLG='Y' OR MEM_PACKAGE_ID IS NOT NULL)";
		//		System.out.println("ownSql=="+ownSql);
		TParm ownParm = new TParm(TJDODBTool.getInstance().select(ownSql));
		double ownAmt = ownParm.getDouble("OWN_AMT", 0); //�ۿ�֮ǰ�ļ�Ǯ
		//		double totAmt = recpParm.getDouble("TOT_AMT", 0); //�ۿ�֮��ļ�Ǯ
		double arAmt = recpParm.getDouble("AR_AMT");  //ʵ��
		//		double reduceAmt=recpParm.getDouble("REDUCE_AMT", 0); //����
		//add by huangtt 20140930  start

		// ����Ӧ��
		oneReceiptParm.setData("TOT_AMT", df.format(ownAmt));
		//�Żݽ��
		difference = ownAmt-arAmt;


		//ʵ�ս��
		oneReceiptParm.setData("AR_AMT",df.format(recpParm.getDouble("AR_AMT")));

		//���⡢�ۿ�
		//		reduceReason = recpParm.getValue("REDUCE_REASON", 0);
		reduceReason = recpParm.getValue("REDUCE_NOTE");
		//		System.out.println("reduceReason-----------"+reduceReason);
		if(reduceReason.trim().length() == 0){
			reduceReason = "�ۿ�";
		}else if(reduceReason.trim().length() > 0){
			if (reduceReason.length()>55) {
				reduceReason=reduceReason.substring(0,55);
			}
			reduceReason = "����ԭ��"+reduceReason;
		}
		oneReceiptParm.setData("REDUCE_REASON",reduceReason);
		/*----add by lich 20140928 ��ӱ�����Ϣ----end*/
		// ������
		oneReceiptParm.setData("REDUCE_AMT", "TEXT", df.format(recpParm.getDouble(
				"REDUCE_AMT")));
		// ������ʾ��д���
		//		oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
		//				.numberToWord(recpParm.getDouble("AR_AMT", 0)));//==pangben 20140611 TOT_AMT ��ΪAR_AMT
		oneReceiptParm.setData("TOTAL_AW", StringUtil.getInstance().numberToWord(recpParm.getDouble("AR_AMT")));//==liling

		// ͳ��֧��
		oneReceiptParm.setData("Overall_pay", "TEXT", StringTool.round(recpParm
				.getDouble("Overall_pay"), 2));
		// ����֧��
		oneReceiptParm.setData("Individual_pay", "TEXT", df.format(recpParm
				.getDouble("TOT_AMT")));
		// �ֽ�֧��= ҽ�ƿ����+�ֽ�+��ɫͨ��
		double payCash = StringTool.round(recpParm.getDouble("PAY_CASH"), 2)
				+ StringTool
				.round(recpParm.getDouble("PAY_MEDICAL_CARD"), 2)
				+ StringTool.round(recpParm.getDouble("PAY_OTHER1"), 2);
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", gbCashPay == 0 ? payCash : df
				.format(gbCashPay));

		// �˻�֧��---ҽ�ƿ�֧��
		oneReceiptParm.setData("Recharge", "TEXT", 0.00);

		// =====zhangp 20120229 modify start
		if (agentAmt != 0) {
			oneReceiptParm.setData("AGENT_NAME", "TEXT", "ҽ�ƾ���֧��");
			// ҽ�ƾ������
			oneReceiptParm.setData("AGENT_AMT", "TEXT", df.format(agentAmt));
		}
		//		oneReceiptParm.setData("MR_NO", "TEXT", pat.getMrNo());
		oneReceiptParm.setData("MR_NO",  pat.getMrNo());
		// =====zhangp 20120229 modify end
		// ��ӡ����
		oneReceiptParm.setData("OPT_DATE", StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));//==liling add HH:MM:ss

		// ҽ�����====pangben 2012-6-7 ��ʾҽ�����
		// String pay_debit = gbNhiPay == 0 ?
		// StringTool.round(recpParm.getDouble(
		// "PAY_INS_CARD", 0), 2)
		// + "" : df.format(gbNhiPay);
		// ҽ�����
		// ===zhangp 20120702 start
		// oneReceiptParm.setData("PAY_DEBIT", "TEXT",
		// pay_debit.equals(df.format(accountPay))?"0.0":pay_debit);
		oneReceiptParm.setData("PAY_DEBIT", "TEXT", df.format(gbNhiPay));

		/*----add by lich 20140928  ��ͷ���ӿ�����Ϣ----start*/
		TTextFormat deptCode = (TTextFormat) getComponent("DEPT_CODE");
		oneReceiptParm.setData("REALDEPTCODE","TEXT",deptCode.getText());
		/*----add by lich 20140928  ��ͷ���Ӿ������----start*/
		TComboBox ctzCode = (TComboBox) getComponent("CTZ1_CODE");
		String s="SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"+ctzCode.getText()+"'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(s));
		oneReceiptParm.setData("CTZ_DESC","TEXT",parm.getValue("CTZ_DESC"));

		//		System.out.println("REALDEPTCODE::::::::::"+reg.getRealdeptCode());
		/*----add by lich 20140928  ��ͷ���ӿ�����Ϣ----end*/

		// ===zhangp 20120702 end
		if (recpParm.getDouble("PAY_OTHER1") > 0) {
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_PATH", "TEXT", "��ɫͨ��֧��");
			// ��ɫͨ�����
			oneReceiptParm.setData("GREEN_AMT", "TEXT", StringTool.round(
					recpParm.getDouble("PAY_OTHER1"), 2));

		}
		// ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", recpParm.getValue(
				"CASHIER_CODE"));

		// ��ӡ��
		oneReceiptParm.setData("OPT_USER", "TEXT", Operator.getName());
		oneReceiptParm.setData("USER_NAME", Operator.getID());
		TParm EKTTemp = null;
		if (!flg) {
			EKTTemp = EKTIO.getInstance().TXreadEKT();
			if (EKTTemp.getErrCode() < 0) {
				this.messageBox("��ҽ�ƿ���Ч");
				return;
			}
			if (null == EKTTemp || EKTTemp.getValue("MR_NO").length() <= 0) {
				this.messageBox("��ҽ�ƿ���Ч");
				// ��ӳ������⳷��
				return;
			}
		}
		if (recpParm.getDouble("REDUCE_AMT")>0) {//===pangben 2014-9-18 ���������Ӽ�������ʾ
			this.messageBox("�շѽ��:"+df.format(recpParm.getDouble("AR_AMT"))+"Ԫ,������:"+df.format(recpParm.getDouble("REDUCE_AMT"))+"Ԫ");
		}
		// oneReceiptParm.setData("TEXT_TITLE1", "TEXT", "(��������嵥)");
		// =====20120229 zhangp modify start
		if (cardNo.equals("")) {
			oneReceiptParm.setData("CARD_CODE", "TEXT", pat.getIdNo());// �������ҽ��
			// ��ʾ���֤��
		} else {
			oneReceiptParm.setData("CARD_CODE", "TEXT", cardNo);// ���� ��ʾҽ������
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
		//====================20140609 liling  modify start ��������շѶ�Ӧ������=====
		String sql="SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY ID ";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String sql2= "SELECT ADM_TYPE,CHARGE01,CHARGE02,CHARGE03,CHARGE04, CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09, "+
				" CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14, CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20 "+
				" FROM BIL_RECPPARM   WHERE  ADM_TYPE='O'  " ;
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		String sql3 = "SELECT MEM_PACKAGE_ID FROM OPD_ORDER WHERE MR_NO = '"+
				opb.getPat().getMrNo()+"' AND CASE_NO = '"+opb.getReg().caseNo()+"' AND MEM_PACKAGE_ID IS NULL";//add by sunqy 20140827 �Ƿ�ҽ��վ�����ײ�
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
		oneReceiptParm.setData("CHARGE001_D", "��ҩ��");
		//		oneReceiptParm.setData("BILL_DATE", "TEXT",StringTool.getString(
		//				recpParm.getTimestamp("BILL_DATE", 0), "yyyy/MM/dd HH:mm:ss"));//��������
		oneReceiptParm.setData("BILL_DATE", StringTool.getString(
				recpParm.getTimestamp("BILL_DATE"), "yyyy/MM/dd HH:mm:ss"));//��������
//		oneReceiptParm.setData("AR_AMT", df.format(recpParm.getDouble("AR_AMT", 0)));//�ϼ�
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

		//add by sunqy 20140827 Ϊ�վ����֧��ϸ�� ----start----
		String mapSql = "SELECT A.PAYTYPE,A.GATHER_TYPE, B.CHN_DESC FROM BIL_GATHERTYPE_PAYTYPE A,SYS_DICTIONARY B " +
				"WHERE B.GROUP_ID='GATHER_TYPE' AND A.GATHER_TYPE=B.ID";//����PAY_TYPE�����Ӧ������
		TParm mapParm = new TParm(TJDODBTool.getInstance().select(mapSql));
		String sql4 = "SELECT PAY_CASH,PAY_MEDICAL_CARD,PAY_TYPE01,PAY_TYPE02,PAY_TYPE03,PAY_TYPE04," +
				"PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08,PAY_TYPE09,PAY_TYPE10,PAY_OTHER3,PAY_OTHER4,REMARK02,REMARK09,REMARK10,WX_BUSINESS_NO,ZFB_BUSINESS_NO,PAY_TYPE11,REMARK11 " +//modify by kangy 20171018 ���΢��֧�������׺�
				"FROM BIL_OPB_RECP WHERE CASE_NO = '" + caseNo + "' " +
				"AND RECEIPT_NO = '" + recpParm.getData("RECEIPT_NO").toString() + "'";
		TParm parm4 = new TParm(TJDODBTool.getInstance().select(sql4));
		String payDetail = "";
		String card_type = "";
		if(!"".equals(parm4.getValue("PAY_MEDICAL_CARD",0)) && parm4.getValue("PAY_MEDICAL_CARD",0)!=null && parm4.getDouble("PAY_MEDICAL_CARD",0)!=0){
			payDetail += ";ҽ�ƿ�"+parm4.getValue("PAY_MEDICAL_CARD",0)+"Ԫ";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER3",0)) && parm4.getValue("PAY_OTHER3",0)!=null && parm4.getDouble("PAY_OTHER3",0)!=0){
			payDetail += ";��Ʒ��"+parm4.getValue("PAY_OTHER3",0)+"Ԫ";
		}
		if(!"".equals(parm4.getValue("PAY_OTHER4",0)) && parm4.getValue("PAY_OTHER4",0)!=null && parm4.getDouble("PAY_OTHER4",0)!=0){
			payDetail += ";�ֽ��ۿ�ȯ"+parm4.getValue("PAY_OTHER4",0)+"Ԫ";
		}
		if(!"".equals(parm4.getValue("PAY_CASH",0)) && parm4.getValue("PAY_CASH",0)!=null && parm4.getDouble("PAY_CASH",0)!=0){
			for (int i = 0; i < mapParm.getCount(); i++) {
				if("".equals(parm4.getValue(mapParm.getValue("PAYTYPE", i),0)) || parm4.getValue(mapParm.getValue("PAYTYPE", i),0)==null 
						|| parm4.getDouble(mapParm.getValue("PAYTYPE", i),0)==0){
					continue;
				}
				payDetail += ";"+mapParm.getValue("CHN_DESC", i)+parm4.getValue(mapParm.getValue("PAYTYPE", i),0)+"Ԫ";
				//==start==modify by kangy20171019 ˢ��΢��֧������ӿ����ͱ�ע���׺�
				/*if(mapParm.getValue("PAYTYPE", i).equals("PAY_TYPE02")){
					card_type=parm4.getValue("REMARK02", 0);//�շ�Ϊˢ��ʱ���վ���ӿ��źͿ����� add by huangjw 20141230 
				}*/
				String remark02 = parm4.getValue("REMARK02",0);
				String remark09= parm4.getValue("REMARK09",0);
				String remark10= parm4.getValue("REMARK10",0);
				String cardtypeString = "";
				String wxCardtypeString = " ";
				String zfbCardtypeString = " ";
				String wxBusinessNo="";//΢�Ž��׺�
				String zfbBusinessNo="";//֧�������׺�
				String x = ";";
				if("PAY_TYPE02".equals(mapParm.getValue("PAYTYPE", i))){
				if (!"".equals(remark02) && !"#".equals(remark02)) {// ���ڿ����ͺͿ���
					String[] strArray = remark02.split("#");
					String card_Type[] = strArray[0].split(";");// ������
					String reMark[] = strArray[1].split(";");// ����
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
				if (!"".equals(remark09) && !"#".equals(remark09)) {// ���ڿ����ͺͿ���
					String[] strArray = remark09.split("#");
					String card_Type[] = strArray[0].split(";");// ������
					String reMark[] = strArray[1].split(";");// ��ע
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
										+ cardParm.getValue("CHN_DESC", 0) + " ��ע:"
										+ reMark[m];
							} else {
								wxCardtypeString = wxCardtypeString
										+ cardParm.getValue("CHN_DESC", 0);
							}
							if(wxBusinessNo.length()>0){
								wxCardtypeString=wxCardtypeString+" ���׺�:"+wxBusinessNo;
							}
						}else{
							continue;
						}
					}
					payDetail+=wxCardtypeString;
				}
				}
				if("PAY_TYPE10".equals(mapParm.getValue("PAYTYPE", i))){
				if (!"".equals(remark10) && !"#".equals(remark10)) {// ���ڿ����ͺͿ���
					String[] strArray = remark10.split("#");
					String card_Type[] = strArray[0].split(";");// ������
					String reMark[] = strArray[1].split(";");// ��ע
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
										+ cardParm.getValue("CHN_DESC", 0) + " ��ע:"
										+ reMark[m];
							} else {
								zfbCardtypeString = zfbCardtypeString
										+ cardParm.getValue("CHN_DESC", 0);
							}
							if(zfbCardtypeString.length()>0){
								zfbCardtypeString=zfbCardtypeString+" ���׺�:"+zfbBusinessNo;
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
		//==end==modify by kangy20171019 ˢ��΢��֧������ӿ����ͱ�ע���׺�

/*		if(!"".equals(payDetail)){
			payDetail = payDetail.substring(1, payDetail.length());
		}
		oneReceiptParm.setData("PAY_DETAIL", payDetail);*/
		//add by sunqy 20140827 Ϊ�վ����֧��ϸ�� ----end----
		//�շ�Ϊˢ��ʱ���վ���ӿ��źͿ����� add by huangjw 20141230  start
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
		//�շ�Ϊˢ��ʱ���վ���ӿ��źͿ����� add by huangjw 20141230  end
		//		if(parm3.getCount() > 0){//add by sunqy 20140827 �����ײ͵���������Ϊ"��������վݣ��ײͣ�"
		String dept_package_session = "���ң�"+deptCode.getText()+"   ";
		if(!memPackageFlg){
			if(recpParm.getValue("ADM_TYPE").equals("E")){
				oneReceiptParm.setData("TITLE", "TEXT", "��������վ�");
			}else {
				oneReceiptParm.setData("TITLE", "TEXT", "��������վ�");
			}
			
			oneReceiptParm.setData("CHN1","�� �� �� ��");
			oneReceiptParm.setData("DIFFERENCE",df.format(difference));

		}else{
			oneReceiptParm.setData("TITLE", "TEXT", "��������վݡ��ײͽ�ת��");
			oneReceiptParm.setData("CHN1","�� ת �� ��");
			oneReceiptParm.setData("DIFFERENCE",ownParm.getDouble("AR_AMT", 0));

			//add by huangtt 20150714 ����ײ�������ʱ������ -----start
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

			dept_package_session += "�ײ�����:";
			for (int i = 0; i < packageCodeList.size(); i++) {
				dept_package_session = dept_package_session+ packageCodeList.get(i)+"��";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);
			dept_package_session += "  ʱ������:";
			for (int i = 0; i < sessionCodeList.size(); i++) {
				dept_package_session = dept_package_session + sessionCodeList.get(i)+"��";
			}
			dept_package_session = dept_package_session.substring(0, dept_package_session.length()-1);

			//add by huangtt 20150714 ����ײ�������ʱ������ -----end
		}
		dept_package_session=dept_package_session+" �������:"+parm.getValue("CTZ_DESC");
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
						IReportTool.getInstance().getReportParm("OPBRECTPrint.class", oneReceiptParm));//����ϲ�modify by wanglong 20130730
			}
			//20141223 wangjingchun add end 854
		}
		return;

	}

	/**
	 * �ֽ��Ʊ��ϸ���
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
			oneReceiptParm.setData("DETAIL", "TEXT", "(���������ϸ��)");
		}
		oneReceiptParm.setData("TABLE", tableresultparm.getData());
	}

	/**
	 * Ȩ�޼��
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
					this.messageBox("��ͬ���Ҳ������븺ֵ!");
					return false;
				}
			} else {
				if (orderParm.getDouble("DOSAGE_QTY", i) < 0) {
					this.messageBox("�������븺ֵ!");
					return false;
				}
			}
		}
		return true;

	}

	/**
	 *ҽ�����ı�
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
	 * ���¼������
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
	 * ������������
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
	 * ��ȡ���� ================pangben modify 20110815 ��õļ۸���Ա��浽������
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
		feeShow = true; // �ܿ�
		this.setValue("TOT_AMT", sum); // �շ�

		paymentTool.setAmt(sum);
	}

	/**
	 * ̩�Ķ�ȡҽ�ƿ�����
	 */
	public void txReadEKT() {
		// parmEKT= TXreadEKT("MR_NO");
		parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		// ҽ�ƿ���ӡ
		callFunction("UI|ektPrint|setEnabled", true);
		if (insParm != null) {
			// ҽ�������ӡ
			callFunction("UI|insPrint|setEnabled", true);
		}
		isEKT = true; // ҽ�ƿ�����
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
	 * ��ҽ����======pangben 2011-11-29
	 */
	public void readINSCard() {
		TJreadINSCard();
	}

	/**
	 * ̩��ҽԺҽ������������ =============pangben 20111129
	 */
	private void TJreadINSCard() {
		// String mrNo = "000000001116";
		// if (!isEKT) {
		//
		// this.messageBox("���Ȼ��ҽ�ƿ���Ϣ");
		// return;
		// }
		if (null == pat || null == pat.getMrNo() || pat.getMrNo().length() <= 0
				|| null == reg) {
			this.messageBox("���ò�����Ϣ");
			return;
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		parm.setData("INS_TYPE", reg.getInsPatType()); // ��ҽ��� 1.��ְ��ͨ2.��ְ����
		// �ҺŲ���ҽ������,���շ�ʱ������ִ��ҽ���շ� ��ʾ����ִ��ҽ������
		if (null == reg.getConfirmNo() || reg.getConfirmNo().length() <= 0) {
			this.messageBox("�˴ξ��ﲡ������ҽ���Һ�,����ִ��ҽ���շѲ���");
			return;
		}
		// ��ѯ�Ƿ�������������
		TParm greenParm = new TParm();
		greenParm.setData("CASE_NO", reg.caseNo());
		greenParm = PatAdmTool.getInstance().selEKTByMrNo(greenParm);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (greenParm.getDouble("GREEN_BALANCE", 0) > 0) {
			this.messageBox("�˾��ﲡ��ʹ��������,������ʹ��ҽ������");
			return;
		}
		// 3.�Ǿ�����
		parm.setData("CARD_TYPE", 3); // �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ,5 :���صǼǣ�
		parm.setData("INS_TYPE", reg.getInsPatType());// �Һ�ҽ���������
		insParm = null;
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null==insParm || null==insParm.getValue("RETURN_TYPE")) {
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType != 1) {
			this.messageBox("��ȡҽ����ʧ��");
			insParm = null;
			return;
		}
		// ===zhangp 20120408 start
		String insType = insParm.getValue("INS_TYPE"); // ҽ����������: 1.��ְ��ͨ 2.��ְ����
		// 3.�Ǿ�����
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		// ============pangben ��ѯ�����Ƿ����
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
			this.messageBox("��ò�����Ϣʧ��");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("��ҽ������������ҽ�ƿ���Ϣ,\nҽ����Ϣ:���֤����:"
					+ opbReadCardParm.getValue("SID") + "\nҽ����������:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
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
					this.messageBox("ҽ����Ϣ�벡����Ϣ����,ҽ����������:" + name);
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
			// ҽ�������ӡ
			callFunction("UI|insPrint|setEnabled", true);
			this.callFunction("UI|ektPrint|setEnabled", false);
		}
		// ��ҪУ�����֤�����Ƿ���ͬ ����ͬ˵�����Ǳ��˵�ҽ���� ���ܲ����Һ�
		// �ж���Ⱥ���
		// insFlg = true;// ҽ������ȡ�ɹ�
		this.setValue("BILL_TYPE", "I"); // ֧����ʽ�޸�
	}

	/**
	 * ҽ����ִ�з�����ʾ���� flg �Ƿ�ִ���˹� false�� ִ���˹� true�� �����̲���
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
		double totAmt = 0.00; // ���Ҫ�շѵ�ҽ��------����ҽ������ҽ��������ʾ
		TParm newOpbParm = new TParm();
		for (int i = 0; i < opbParm.getCount("ORDER_CODE"); i++) {
			if (null == opbParm.getValue("ORDERSET_CODE", i)
					|| !opbParm.getValue("ORDERSET_CODE", i).equals(
							opbParm.getValue("ORDER_CODE", i))) {
				totAmt += opbParm.getDouble("OWN_AMT", i);
				newOpbParm.addRowData(opbParm, i);
			}
		}
		insFeeParm.setData("CASE_NO", reg.caseNo()); // �˹�ʹ��
		insFeeParm.setData("RECP_TYPE", "OPB"); // �շ�ʹ��
		insFeeParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // ҽ�������
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // ������
		insFeeParm.setData("FeeY", totAmt); // Ӧ�ս��
		insFeeParm.setData("PAY_TYPE", isEKT); // ֧����ʽ
		insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		insExeParm(newOpbParm);
		insFeeParm.setData("REGION_CODE", insParm.getValue("REGION_CODE")); // ҽ������
		insFeeParm.setData("insParm", insParm.getData());
		insFeeParm.setData("FEE_FLG", flg); // �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
		TParm returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
				insFeeParm);
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.ʧ�� 1. �ɹ�
		if (returnType == 1) {
			return returnParm;
		} else {
			return null;
		}
	}

	/**
	 * ҽ���ۿ�����
	 * 
	 * @param newOpbParm
	 *            TParm
	 */
	private void insExeParm(TParm newOpbParm) {
		insParm.setData("REG_PARM", newOpbParm.getData()); // ����Ҫ�ָ��ҽ��
		insParm.setData("MR_NO", pat.getMrNo()); // ������
		insParm.setData("PAY_KIND", "11"); // 4 ֧�����:11���ҩ��21סԺ//֧�����12
		insParm.setData("CASE_NO", reg.caseNo()); // �����
		insParm.setData("RECP_TYPE", "OPB"); // �������
		insParm.setData("OPT_USER", Operator.getID()); // �������
		// insParm.setData("REG_PARM", parm.getData());
		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // ���Ҵ���
		insParm.setData("REG_TYPE", "0"); // �Һű�־:1 �Һ�0 �ǹҺ�
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("OPBEKTFEE_FLG", "Y"); // ����ҽ�ƿ��շ�ע��----�ۿ��Ʊʱʹ���������� �վݱ�
		// BIL_OPB_RECP ҽ������޸�
		insParm.setData("PRINT_NO", this.getValue("UPDATE_NO")); // Ʊ��
		insParm.setData("DR_CODE", this.getValue("DR_CODE")); // ҽ������
		if (reg.getAdmType().equals("E")) {
			insParm.setData("EREG_FLG", "1"); // ����
		} else {
			insParm.setData("EREG_FLG", "0"); // ��ͨ
		}
	}

	/**
	 * ������Ա���
	 * 
	 * @param type
	 *            String
	 * @return String
	 */
	private String getSpPatDesc(String type) {
		if (type == null || type.length() == 0 || type.equals("null"))
			return "";
		if ("04".equals(type))
			return "�˲о���";
		if ("06".equals(type))
			return "����Ա";
		if ("07".equals(type))
			return "����������Ա";
		if ("08".equals(type))
			return "�Ÿ�����";
		return "";
	}

	/**
	 * ҽ�ƿ���������˴�ҽ�����ۿ���
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
		// ���:AMT:���β������ BUSINESS_TYPE :���β������� CASE_NO:�������
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
	 * �����Ҳ���Ʒ�
	 */
	public void onOperation() {
		if (this.getValueString("ALL").equals("Y")||this.getValueString("EKT_R").equals("Y")) {
			this.messageBox("�벻Ҫ��ѡȫ�����˷�");
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
			//ҽ����      1ҽʦר�ã� ���������ܼ���
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
	 * �����ײͻش�����
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
	 * ҽ�ƿ���ֵ
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
	 * ���Ӧ������ҩ��������Ϣ
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
		if(phaRxNo.length()>0){//������в����Ĵ���ǩ���� ��������
			phaArray=phaRxNo.split(",");
		}
		for (int i = 0; i < phaArray.length; i++) {
			client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER ���������
					+ phaArray[i] + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		}
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000001" + "|MR_NO:" + pat.getMrNo()+ "|PAT_NAME:" + pat.getName());
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000002" + "|MR_NO:000022222222|PAT_NAME:����");
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000003" + "|MR_NO:000022222222|PAT_NAME:����");
		//		client1.sendMessage("PHAMAIN", "RX_NO:"
		//				+ "00000000004" + "|MR_NO:000033333333|PAT_NAME:����");
		if (client1 == null)
			return;
		client1.close();
	}

	/**
	 * �ײ͵���¼�
	 * ====zhangp
	 */
	public void onChangeMemPackageFlg(){
		if(null== reg || null == reg.caseNo() || reg.caseNo().length()<=0){
			this.messageBox("δ��þ�����Ϣ");
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
			memCombo.setPopupMenuHeader("����,100;����,100");
			memCombo.setPopupMenuWidth(300);
			memCombo.setPopupMenuHeight(300);
			memCombo.setFormatType("combo");
			memCombo.setShowColumnList("NAME");
			memCombo.setValueColumn("ID");
			memCombo.setPopupMenuData(parm);
			
			memTrade.setHorizontalAlignment(2);
			memTrade.setPopupMenuHeader("����,100;����,200");
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
		table.setLockCellColumn(17, true);//lockColumns������������Ч��ֻ����ôд
	}

	/**
	 * �ر��¼�
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
				messageBox("�ֽ���");
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
	 * �����ӡ
	 * @deprecated
	 */
	public void onWrist111() {
		if (this.getValueString("MR_NO").length() == 0 || 
				this.getValueString("PAT_NAME").length() == 0 ||
				this.getValueString("SEX_CODE").length() == 0) {
			this.messageBox("������ѡ��Ϊ��,��������д!");
			return;
		}
		String a_number = getValueString("MR_NO");
		String a_name = getValueString("PAT_NAME");
		String a_gender = getValueString("SEX_CODE");
		String sql = "SELECT BIRTH_DATE,SEX_CODE FROM SYS_PATINFO WHERE MR_NO = '"+a_number+"'";
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
		String time = parm1.getValue("BIRTH_DATE");
		if(a_gender.equals("1")){
			a_gender="��";
		}
		if(a_gender.equals("2")){
			a_gender="Ů";
		}
		if(a_gender.equals("9")){
			a_gender="δ˵��";
		}
		if(a_gender.equals("0")){
			a_gender="δ֪";
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
		//                             IReportTool.getInstance().getReportParm("REGPatAdm.class", print));//����ϲ�modify by wanglong 20130730
	}
	
	/**
	 * �����ӡ
	 */
	public void onWrist() {
		TParm print = new TParm();
		// �õ�����
		print.setData("PatName", "TEXT", "����:" + pat.getName());
		// �õ�������
		print.setData("Barcode1", "TEXT", pat.getMrNo());
		// �õ��Ա�
		print.setData("Sex", "TEXT", "�Ա�:" + pat.getSexString());
		// �õ���������
		print.setData("BirthDay", "TEXT", "��������:" + StringTool.getString(pat.getBirthday(), "yyyy/MM/dd HH:mm"));
		// �õ�����
		print.setData("Dept", "TEXT",
				"����:" + StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + reg.getRealdeptCode() + "'"));
		this.openPrintDialog("%ROOT%\\config\\prt\\ADM\\ADMWristAdult.jhw", print, true);
	}

	public void changeChargeText(){
		if(ektTCharge.isSelected()){
			charge.setText("�˷�");
		}else{
			charge.setText("�շ�");
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
	 * ���߱�����Ϣ add by huangtt 20140812
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
	 * ��ѯ�Ƿ���������  add by huangtt 20141009
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
	 * ȡ������ҽ��   add by huangtt 20141126
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
	 * ����ǰ�����¾�ҽ���ȶ�  add by huangtt 20141126
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
	 * ��������
	 * 20141128 yanjing
	 */
	public void onReduceCheck(){
		//�õ���������
		TParm parm = table.getParmValue();
		if(this.getValueDouble("TOT_AMT")==0){
			this.messageBox("��ѡ��Ҫ���������");
			return;
		}else if(this.getValueDouble("TOT_AMT")<0){
			this.messageBox("�˷Ѳ��ɲ�������");
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
