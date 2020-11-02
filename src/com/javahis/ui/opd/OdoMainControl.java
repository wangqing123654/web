package com.javahis.ui.opd;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


import jdo.bil.BIL;
import jdo.bil.BILStrike;
import jdo.ctr.CTRPanelTool;
import jdo.device.CallNo;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.hl7.Hl7Communications;
import jdo.spc.INDTool;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSTJTool;
import jdo.odo.Diagrec;
import jdo.odo.DrugAllergy;
import jdo.odo.MedApply;
import jdo.odo.MedHistory;
import jdo.odo.ODO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.odo.OpdOrder;
import jdo.odo.OpdRxSheetTool;
import jdo.odo.PatInfo;
import jdo.odo.RegPatAdm;
import jdo.opb.OPB;
import jdo.opb.OPBTool;
import jdo.opd.OPDSysParmTool;
import jdo.opd.Order;
import jdo.opd.OrderTool;
import jdo.opd.TotQtyTool;
import jdo.pha.PassTool;
import jdo.pha.PhaBaseTool;
//import jdo.pha.client.PHADosageWsImplService_Client;
import jdo.pha.PhaSysParmTool;
//import jdo.pha.client.SpcOpdOrderReturnDto;
import jdo.reg.ClinicRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGAdmForDRTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.reg.SessionTool;
import jdo.sys.DeptTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSAntibioticTool;
import jdo.sys.SYSCtrlDrugClassTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.system.combo.TComboSession;
import com.javahis.system.root.RootClientListener;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.ReasonableMedUtil;
import com.javahis.util.StringUtil;

import device.PassDriver;

/**
 * 
 * 
 * <p>
 * 
 * Title: ����ҽ������վ����
 * </p>
 * 
 * <p>
 * Description:����ҽ������վ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui 2008.09.01
 * @version 1.0
 */
public class OdoMainControl extends OdoMainBaseControl {
	
	//��ɫ����yanjing 20130614
	Color green = new Color(0,125, 0);
	
	// �ż�ס��
	private String admType; // liudy
	// ������Ϣ
	private TParm parmpat;    
	// ���ҵ�MOVEPANEL
	private TMovePane mp;	
	// ���ȫ�ֱ���
	private String[] ctz = new String[3]; // liudy
	// ��������
	private String rxType;
	// ����TABLE��
	private String tableName;
	// ������������
	private String rxName;
	// �Ƿ��Ѷ�ȡ��ҽ�ƿ�
	private boolean isReadEKT = false;
	// ������
	// private String rxNo;
	// ��������
	private String realDeptCode; // ��������
	public final static String EXA_RX = "EXA_RX";
	public final static String OP_RX = "OP_RX";
	public final static String MED_RX = "MED_RX";
	public final static String CHN_RX = "CHN_RX";
	public final static String CTRL_RX = "CTRL_RX";
	public final static String TABLEPAT = "TABLEPAT";
	public final static String TABLEDIAGNOSIS = "TABLEDIAGNOSIS";
	public final static String TABLEMEDHISTORY = "TABLEMEDHISTORY";
	private static final String TABLEALLERGY = "TABLEALLERGY";
	private static final String TABLE_EXA = "TABLEEXA"; // �������
	private static final String TABLE_OP = "TABLEOP"; // ������Ŀ��
	private static final String TABLE_MED = "TABLEMED"; // ����ҩ��
	private static final String TABLE_CHN = "TABLECHN"; // ��ҩ��Ƭ��
	private static final String TABLE_CTRL = "TABLECTRL"; // ����ҩƷ��
	private static final String MED = "1";
	private static final String CTRL = "2";
	private static final String CHN = "3";
	private static final String OP = "4";
	private static final String EXA = "5";
	private static final String NULLSTR = "";
	// �滻TABLE�����е���ϴ����ԭֵ
	//private String tempIcd = "";
	// ��ʼĬ�ϵĹ�������
	private String allergyType = "B";
	// ��һ�����˵�mr_no
	private String lastMrNo = "";
	// ����ҩ��
	private String phaCode = "";
	// Ĭ����ҩ����
	private double dctMediQty = 0.0;
	// Ĭ����ҩ����
	private double dctTakeDays = 0;
	// Ĭ����ҩƵ��
	private String dctFreqCode = "";
	// Ĭ����ҩ�÷�
	private String dctRouteCode = "";
	// Ĭ����ҩ�巨
	private String dctAgentCode = "";
	// �Ƿ�����ҽ��
	private boolean whetherCallInsItf = false;
	// opb����
	OPB opb;
	// tableʹ�õ��е���ɫ����
	Map map; // liudy
	// icd��Ϣ��ȫ�ֱ���
	//private TDataStore icd = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	// ����ҽ���
	private String wc = "";
	// ���ò��Ҳ��˷����Ŀ��أ���ĳЩ�����ֵʱ����Щ�����ĵ�������е���selectPat�������ͻ�ݹ�
	boolean selectPat = false;
	// ������ҩ����
	// private boolean rsnMed;
	// ������ҩ����
	private boolean passIsReady = false;
	private boolean enforcementFlg = false;
	private int warnFlg;
	// ����ҽ����
	//private int linkNo;
	// ���ң�ҽʦ�����ﲿ��
	private TComboBox clinicroom, dr, insteadDept;
	// �����ϵ�TABLE
	private TTable tblPat, tblMedHistory, tblAllergy, tblDiag, tblExa, tblOp,
			tblMed, tblChn, tblCtrl;
	private TLabel ekt_lable;//yanjing 20130614 ҽ�ƿ�״̬��ǩ
	// �����ϵĽṹ������
	private TWord word, familyWord;
	// ����ʱ��combo
	TComboSession t;
	// ���ﲡ���б��ɫ���߳�
	private Thread erdThread;
	// ����ʷ������ʷ�任Ϊ��ɫ
	Color red = new Color(255, 0, 0); // liudy
	// �������߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	private String focusTag;
	// ���Ѿ�����Ĳ����Ľṹ����������Ҫ�Ĵ洢·��
	private String[] saveFiles, familyHisFiles;
	// �Ƿ�Ӣ��
	private boolean isEng;
	// ����ȼ�
	private String serviceLevel = "";
	// �����====pangben 20110914
	private String caseNo;
	TParm sendHL7Parm;
	// ҽ�ƿ�����===pangben 20110914
	private String tredeNo;
	private TParm ektReadParm;// ҽ�ƿ���������
	private boolean ektExeConcel = false;// ҽ�ƿ���������ȡ����ť�Ժ����
	private TParm regSysEFFParm;//pangben 2013-4-28 �Һ���Ч����
	/**
	 * Socket��������ҩ������
	 */
	private SocketLink client1;
	private String phaRxNo;//===pangben 2013-5-17 ҩƷ��˽����������ƴ���ǩ���� 
	private int PatSelectRow; //===huangtt 20131128 ˫�������б�ʱѡ�еĵ�ǰ��
	private String newCaseNo = "";//====yanjԤ����������µľ����
	private boolean aheadFlg = true;
	/** 
	 * ����Ӧҩ��
	 */
	public void onInitParameter() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
//		TTable tblExa1 = (TTable)this.getComponent(TABLE_EXA);//yanjing 20140121
		ekt_lable = (TLabel) this.getComponent("LBL_EKT_MESSAGE");//��ȡ��ʾҽ�ƿ�״̬��ǩ
		ekt_lable.setForeground(red);//======yanjing 2013-06-14���ö�����ɫ
		tblPat = (TTable) this.getComponent("TABLEPAT");
		t = (TComboSession) this.getComponent("SESSION_CODE");
		clinicroom = (TComboBox) this.getComponent("CLINICROOM");
		dr = (TComboBox) this.getComponent("INSTEAD_DR");
		insteadDept = (TComboBox) this.getComponent("INSTEAD_DEPT");
		admType = (String) this.getParameter();
		if(admType.equals("O")){
			this.callFunction("UI|singledise|setEnabled", false);//add by wanglong 20121119
//			tblExa1.setLockColumns("17");
		}
		t.setAdmType(admType);
		t.onQuery();
		super.onInit();
		// ע���¼�
		onInitEvent();
		// ��ʼ�������б�
		initOPD();
		// ��ʼ���ż���
		initOE();
		// �жϿ���Ȩ��
		if (this.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			// ��TABLE������
			onSelectPat("");
		// if (rsnMed) {
		// initReasonbledMed();
		// }
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		if (passIsReady) {
			if (!initReasonbledMed()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
		initInstradCombo();
		SynLogin("1"); // �кŵ�½
		regSysEFFParm=REGTool.getInstance().getRegParm();//pangben 2013-4-28 �Һ���Ч����
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();
	}

	/**
	 * ��ʼ��������ҩ
	 */
	public boolean initReasonbledMed() {
		try {
			if (PassDriver.init() == 0) {
				return false;
			}
			// ������ҩ��ʼ��
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) == 0) {
				return false;
			}
			// ������ҩ���Ʋ���
			if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) == 0) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			return false;
		} catch (NoClassDefFoundError e2) {
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * ���
	 */
	public void onClear() {
		unLockPat(); // ����
		pat = null;
		reg = null;
		odo = null;
		this.setValue("CHN_DSNAME", false);
		this.setValue("W_FLG", true);
		// this.setValue("FAMILY_HISTORY", NULLSTR);
		this.setValue("PREMATURE_FLG", false);
		this.setValue("HANDICAP_FLG", false);
		this.setValue("ORDER_ALLERGY", true);
		for (int i = 0; i < OPBTool.getInstance().controlNameAmt.length; i++) {//====pangben 2013-5-2
			this.setValue(OPBTool.getInstance().controlNameAmt[i], 0.0);
		}
		for (int i = 0; i < OPBTool.getInstance().controlName.length; i++) {//====pangben 2013-5-2
			this.setValue(OPBTool.getInstance().controlName[i], NULLSTR);
		}
		String[] nullList = new String[] {};
		TComboBox combo=null;
		for (int i = 0; i < OPBTool.getInstance().controlNameCombo.length; i++) {//====pangben 2013-5-2
			combo = (TComboBox) this.getComponent(OPBTool.getInstance().controlNameCombo[i]);
			combo.setVectorData(nullList);
		}
		TTable table =null;
		for (int i = 0; i <  OPBTool.getInstance().controlNameTable.length; i++) {//====pangben 2013-5-2
			table = (TTable) this.getComponent(OPBTool.getInstance().controlNameTable[i]);
			table.removeRowAll();
		}
		word.onNewFile();
		word.update();
		ektReadParm=null;// =======pangben 20120802 
		familyWord.onNewFile();
		familyWord.update();
		ektExeConcel = false;// ҽ�ƿ���������ȡ����ť�Ժ����
		this.setValue("LBL_EKT_MESSAGE", "δ����");//====pangben 2013-3-19 ��ʼ������״̬
		ekt_lable.setForeground(red);//======yanjing 2013-06-14���ö�����ɫ
	}

	/**
	 * ע��ؼ����¼�
	 */
	public void onInitEvent() {
		// �����б����¼�
		// ((TTable)getComponent(TABLEPAT)).addEventListener(TTableEvent.DOUBLE_CLICKED,
		// this, "onTablePatDoubleClick");
		mp = (TMovePane) callFunction("UI|MOV_MAIN|getThis");
		// ��ϵ��¼�
		tblDiag = (TTable) this.getComponent(TABLEDIAGNOSIS);
		tblDiag.addEventListener(TABLEDIAGNOSIS + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onDiagTableChangeValue");
		tblDiag.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tblDiag.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");
		// ҽ�����¼�
		tblExa = (TTable) this.getComponent(TABLE_EXA);
		tblExa.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		tblExa.addEventListener(TABLE_EXA + "->" + TTableEvent.CHANGE_VALUE,
				this, "onExaValueChange");
		tblExa.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// ������ĿTABLE
		tblOp = (TTable) this.getComponent(TABLE_OP);
		tblOp.addEventListener(TABLE_OP + "->" + TTableEvent.CHANGE_VALUE,
				this, "onOpValueChange");
		tblOp.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onOpCreateEditComponent");
		tblOp.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// ��ҩTABLE
		tblMed = (TTable) this.getComponent(TABLE_MED);
		tblMed.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedCreateEditComponent");
		tblMed.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tblMed.addEventListener(TABLE_MED + "->" + TTableEvent.CHANGE_VALUE,
				this, "onMedValueChange");
		// ��ҩTABLE
		tblChn = (TTable) this.getComponent(TABLE_CHN);
		tblChn.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onChnCreateEditComponent");
		tblChn.addEventListener(TABLE_CHN + "->" + TTableEvent.CHANGE_VALUE,
				this, "onChnValueChange");
		// ����ҩƷTABLE
		tblCtrl = (TTable) this.getComponent(TABLE_CTRL);
		tblCtrl.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCtrlCreateEditComponent");
		tblCtrl.addEventListener(TABLE_CTRL + "->" + TTableEvent.CHANGE_VALUE,
				this, "onCtrlValueChange");
		tblCtrl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		// ����ʷTABLE
		tblMedHistory = (TTable) this.getComponent(TABLEMEDHISTORY);
		tblMedHistory.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedHistoryCreateEditComponent");
		tblMedHistory.addEventListener(TABLEMEDHISTORY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onMedHistoryChangeValue");
		// ����ʷTABLE
		tblAllergy = (TTable) this.getComponent(TABLEALLERGY);
		tblAllergy.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onAllergyCreateEditComponent");
		tblAllergy.addEventListener(TABLEALLERGY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onAllergyChangeValue");

		word = (TWord) this.getComponent("TWORD");
		familyWord = (TWord) this.getComponent("FAMILY_WORD");
	}

	/**
	 * ����ϵ�ѡ�¼����ж��Ƿ���������
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onDiagMain(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		return false;
	}

	/**
	 * ��ϡ�����ʷ������ʷTABLE�е���¼�
	 * 
	 * @param tag
	 *            String
	 */
	public void onTableClick(String tag) {

		tableName = tag;

		TTable table = (TTable) this.getComponent(tableName);
		// table.acceptText();
		table.getTable().grabFocus();
		if (this.TABLEDIAGNOSIS.equalsIgnoreCase(tag)
				|| this.TABLEALLERGY.equalsIgnoreCase(tag)
				|| this.TABLEMEDHISTORY.equalsIgnoreCase(tag)
				|| this.TABLEPAT.equalsIgnoreCase(tag)) {
			return;
		}
		int row = table.getSelectedRow();

		OpdOrder order = odo.getOpdOrder();

		// zhangyong20110311
		String filter = ((TTable) this.getComponent(tableName)).getFilter();
		if (filter == null || "null".equals(filter)) {
			filter = "";
		}
		if (!tag.equals(TABLE_CHN)) {
			order.setFilter(filter);
			order.filter();

		}
		// ״̬��
		 //  ===============chenxi  ҽ����ʾ���� ����ҩ�����ʾ
		String orderCode =order.getItemString(row, "ORDER_CODE");
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC," +
				"DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE" +
				" WHERE ORDER_CODE = '" +orderCode+ "'" ;
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql)) ;
		sqlparm = sqlparm.getRow(0);
		callFunction(
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
	 * ��ҩ������ҩƷ�����õ�checkBox�¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) {
		
		TTable table = (TTable) obj;
		table.acceptText();
		table.setDSValue();
		return false;

	}

	/**
	 * ����ʷ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onMedHistoryChangeValue(TTableNode tNode) {
		TTable table = (TTable) this.getComponent(TABLEMEDHISTORY);
		int row = table.getSelectedRow();
		if (row < 0)
			return true;
		int column = tNode.getColumn();
		String columnName = table.getParmMap(column);
		if ("ICD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}
		return false;
	}
	/**
	 * ��ҩֵ�ı��¼� ����
	 * @param inRow
	 * @param execDept
	 * @param orderCodeFinal
	 * @param columnNameFinal
	 */
	
	private void setOnMedValueChange(final int inRow,final String execDept,
			final String orderCodeFinal,final String columnNameFinal,final double oldDosageQty){
		
		SwingUtilities.invokeLater(new Runnable() {
			//boolean flg=true;
			public void run() {
				try {
					if (!checkStoreQty(inRow, execDept, orderCodeFinal,
							columnNameFinal,oldDosageQty)) {
						return;
				   }
				} catch (Exception e) {
				}
			}
		});
	}

	/**
	 * ��ҩֵ�ı��¼������Ǯ
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onMedValueChange(TTableNode tNode) {
		TTable table = (TTable) this.getComponent("TABLEMED");
		int column = tNode.getColumn();
		int row = table.getSelectedRow();
		String columnName = table.getParmMap(column);
		OpdOrder order = odo.getOpdOrder();
		// for (int i = 0; i < array.length; i++) {
		//
		// }
		//�������޸�
		TParm spcReturn=null;
		boolean flg=true;//�ܿ�������ɾ�����޸�ҽ������
		if (Operator.getSpcFlg().equals("Y")) {
			TParm spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm.setData("RX_NO",rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(//��ô�ҽ����ҩƷ״̬
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
//			if (spcReturn.getValue("PhaDosageCode").length() > 0
//					&& spcReturn.getValue("PhaRetnCode").length() == 0) {//û����ҩ���Ѿ���ˣ�������ɾ���޸Ĳ���
//				  flg=true;// �Ѿ���ҩ��ҽ��ɾ������ִ��
//			}else{
//				// ==pangben 2013-5-14// ���������Ѿ���ҩ��ҽ������ѡ����flg=true��������flg=false
//				if ("FLG".equalsIgnoreCase(columnName)) {
//					flg = false;
//				} else {
//					flg = true;
//				}
//			}
			if (!this.checkDrugCanUpdate(order, "MED", row, flg,spcReturn)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
				if(spcReturn.getValue("PhaRetnCode").length()>0)
					this.messageBox("�Ѿ���ҩ,��ɾ������ǩ����");//===pangben 2013-7-17 �޸���������ʾ��Ϣ
				else
					this.messageBox("E0189");
				return true;
			}
		}
		//�������޸�
		if (checkPha(order, row, "MED",flg))
			return true;
		if (!deleteOrder(order, row, "���շ�,�����޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {// pangben ====2012
			// 2-28//pangben2012 2-28
			return true;
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return true;
		}
		//========pangben 2012-7-18 ����ҩƷ�Ѿ��շѲ������޸�
		if ("RELEASE_FLG".equalsIgnoreCase(columnName)) {
			TParm parm=order.getRowParm(row);
			if (odo.getOpdOrder().getOrderCodeIsBillFlg(parm)) {
				this.messageBox("���շ�,�������޸ı�ҩ����");
				return true;	
			}
		}
		order.getRowParm(row);
		String orderCode = order.getItemString(row, "ORDER_CODE");
		double mediQty = TypeTool.getDouble(tNode.getValue());
		final int inRow = row;
		final String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		final String orderCodeFinal = order.getItemString(row, "ORDER_CODE");
		final String columnNameFinal = columnName;
		// �շ� ��
		if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			
			int day = SYSAntibioticTool.getInstance().getAntibioticTakeDays(
					orderCode);
			if (day > 0 && TypeTool.getInt(tNode.getValue()) > day) {
				if (messageBox(
						"��ʾ��Ϣ/Tip",
						"��������������������,�Ƿ��������?\r\nThe days you ordered is more than the standard days of antiVirus.Do you proceed anyway?",
						this.YES_NO_OPTION) == 1) {
					table.setDSValue(row);
					return true;
				}
			}
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("EXEC_DEPT_CODE".equalsIgnoreCase(columnName)) { // ִ�п��� ��
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				return true;
			}
			final String execDeptFinal = TypeTool.getString(tNode.getValue());
			setOnMedValueChange(inRow, execDeptFinal, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("MEDI_QTY".equalsIgnoreCase(columnName)) { // ����
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (messageBox(
							"��ʾ��Ϣ/Tip",
							"��������ҩƷĬ������,�Ƿ��������?\r\nQty of this order is over-gived.Do you proceed anyway?",
							this.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("FREQ_CODE".equalsIgnoreCase(columnName)) { // Ƶ��
//			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
//				return true;
//			}
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) { // ����ҽ��
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) { // �����
			int value = TypeTool.getInt(tNode.getValue());
			String link_main_flg = order.getItemString(row, "LINKMAIN_FLG");
			// if(oldValue==0&&value>0){
			// return true;
			// }
			if ("0".equals(tNode.getValue().toString())) {
				return true;
			}
			if ("Y".equals(link_main_flg) || value < 0) {
				return true;
			}

			if (!StringUtil
					.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				int linkNo = order.getMaxLinkNo();
				if (value >= 0 && value <= linkNo) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) { // �÷�
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			String routeCode = tNode.getValue() + "";
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				this.messageBox("E0019"); // ȡ���÷�ʧ��
				return true;
			} else if (result != 1) {
				this.messageBox("E0020"); // ����ҩ����ʹ�ø��÷�
				return true;
			}
			// ����ҽ����������������
			if ("Y".equals(order.getItemString(row, "LINKMAIN_FLG"))) {
				String linkNO = order.getItemString(row, "LINK_NO"); // �����
				String rxNo = order.getItemString(row, "RX_NO"); // ����ǩ��
				// ѭ����������
				for (int i = 0; i < order.rowCount(); i++) {
					if (linkNO.equals(order.getItemString(i, "LINK_NO"))
							&& rxNo.equals(order.getItemString(i, "RX_NO"))
							&& !"".equals(order.getItemString(i, "ORDER_CODE"))
							&& !routeCode.equals(order.getItemString(i,
									"ROUTE_CODE"))) {
						order.setItem(i, "ROUTE_CODE", routeCode);
					}
				}
				table.setDSValue();
			}
			return false;
		} else {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		this.calculateCash(TABLE_MED, "MED_AMT");
		return false;
	}

	/**
	 * ����TABLE��ֵ�ı��¼��õ��ĺ��õĿ���˷���
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
			final String orderCode, final String columnName,final double oldDosageQty) {
		if (StringUtil.isNullString(columnName)) {
			return true;
		}
		if (!("TAKE_DAYS".equalsIgnoreCase(columnName)
				|| "MEDI_QTY".equalsIgnoreCase(columnName)
				|| "FREQ_CODE".equalsIgnoreCase(columnName) || "EXEC_DEPT_CODE"
				.equalsIgnoreCase(columnName))) {
			return true;
		}
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
			OpdOrder order = odo.getOpdOrder();
			order.showDebug();
			double dosageQty = TypeTool.getDouble(order.getItemData(row,
					"DOSAGE_QTY"));
			if(orderCode.length()<=0)//==pangben 2013-5-2 ��ǰû��ѡ���ҽ����ִ��У�������
				return true;
			if (isCheckKC(orderCode)){ // �ж��Ƿ��ǡ�ҩƷ��ע��
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
					if (orderParm.getCount("QTY")>0) {//���ݿ�������ݣ��۳����=��ǰ����������-���ݿ��������
						oldQty=orderParm.getDouble("QTY",0);
					}
					if (dosageQty>oldQty) {//����
						if (!INDTool.getInstance().inspectIndStockQty(orderCode, execDept,
								dosageQty, oldQty,true)) {
							this.messageBox("E0052");// ��治��
							//order.setActive(row, false);
							return false;
						}
					}else{//����
						if (!INDTool.getInstance().inspectIndStockQty(orderCode, execDept,
								dosageQty, oldQty,false)) {
							this.messageBox("E0052");// ��治��
							//order.setActive(row, false);
							return false;
						}
					}
				}else{
					// ������
					if (!INDTool.getInstance().inspectIndStock(execDept, orderCode,
							dosageQty)) {
						// this.messageBox("E0052"); // ��治��
						// $$==========add by lx 2012-06-19�����ⲻ�㣬���ҩ��ʾ
						TParm inParm = new TParm();
						inParm.setData("orderCode", orderCode);
						this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x",
								inParm);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * ������ֵ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onExaValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
//		tblExa.setLockCell(row, "PRE_DATE", true);
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(colName)) {
			tNode.setValue("");
		}
		OpdOrder order = odo.getOpdOrder();
		if (order == null) {
			return true;
		}
		if (!deleteOrder(order, row, "���շ�,�����޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {// pangben ====2012 2-28
			return true;
		}
		if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			return true;
		}
		if(!checkExa(order, row)){//===pangben 2013-4-28ɾ����ѡ������У���Ƿ񵽼�
			return true;
		}
		
		
		//Ԥ������ж� yanjing 20131211
		if ("IS_PRE_ORDER".equalsIgnoreCase(colName)) {//�Ƿ�ΪԤ�����
	   //�޸�ϸ���Ԥ�����״̬��Ԥ��ʱ��
			OpdOrder orderPre = odo.getOpdOrder();
			String lastFilter = orderPre.getFilter();
			tblExa = (TTable) this.getComponent(TABLE_EXA);
			String rxNo = "";
				//�޸�ϸ���Ԥ�����״̬��Ԥ��ʱ��
				rxNo = tblExa.getItemString(row, "RX_NO");
//				orderPre.setFilter("RX_NO = '" + rxNo + "'");
//				orderPre.filter();
				String orderSetCode = tblExa.getItemString(row, "ORDERSET_CODE");//�����ҽ������
				String groupNo = tblExa.getItemString(row, "ORDERSET_GROUP_NO");//�����ҽ������
				// ѭ������ϸ��
			if(tNode.getValue().equals("Y")){
				tblExa.setLockCell(row, "PRE_DATE", false);
				for (int i = 0; i < orderPre.rowCount(); i++) {
					orderPre.setFilter("RX_NO = '" + rxNo + "'");
					orderPre.filter();
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "IS_PRE_ORDER", "Y");
					}
				}
				tblExa.setLockCell(row, "PRE_DATE", false);
				orderPre.setFilter(lastFilter);
				orderPre.filter();
				if (!initSetTable(TABLE_EXA, true))
					this.messageBox("E0026"); // ��ʼ��������ʧ��
			}else{//����Ԥ�����״̬��Ϊ��Ԥ�����״̬ʱ
				for (int i = 0; i < orderPre.rowCount(); i++) {
					orderPre.setFilter("RX_NO = '" + rxNo + "'");
					orderPre.filter();
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "IS_PRE_ORDER", "N");
						orderPre.setItem(i, "PRE_DATE", "");
					}
				}
				tblExa.setLockCell(row, "PRE_DATE", true);
				if (!initSetTable(TABLE_EXA, true))
					this.messageBox("E0026"); // ��ʼ��������ʧ��
				tblExa.setItem(row, "PRE_DATE", null);
			}

		}
		if ("PRE_DATE".equalsIgnoreCase(colName)) {//Ԥִ��ʱ��
			//�޸�ϸ���Ԥ�����״Ԥ��ʱ��
			tblExa = (TTable) this.getComponent(TABLE_EXA);
			OpdOrder orderPre = odo.getOpdOrder();
			String lastFilter = orderPre.getFilter();
			String rxNo = "";	
//			System.out.println("Ԥ��ʱ��Ľ���ֵ tNode.getValue() is������"+tNode.getValue());
//			if(!(tNode.getValue().equals(null)&&"".equals(tNode.getValue()))){
				//�޸�ϸ���Ԥ�����״̬��Ԥ��ʱ��
				rxNo = tblExa.getItemString(row, "RX_NO");
				TTable tblExa1 = (TTable) this.getComponent(TABLE_EXA);
				String orderSetCode = tblExa1.getItemString(row, "ORDERSET_CODE");//�����ҽ������
				String groupNo = tblExa1.getItemString(row, "ORDERSET_GROUP_NO");//�����ҽ������
				orderPre.setFilter("RX_NO = '" + rxNo + "'");
				orderPre.filter();
				// ѭ������ϸ
				for (int i = 0; i < orderPre.rowCount(); i++) {
					if (orderPre.getItemString(i, "ORDERSET_CODE").equals(orderSetCode)
						 && orderPre.getItemString(i, "ORDERSET_GROUP_NO").equals(groupNo)
						 &&orderPre.getItemString(i, "RX_NO").equals(rxNo)) {
						orderPre.setItem(i, "PRE_DATE", tNode.getValue());
					}
				}
//			}
			orderPre.setFilter(lastFilter);
			orderPre.filter();
//			//У��Ԥ������ʱ�䲻��������
//			Timestamp rollDay = SystemTool.getInstance().getDate();
//			String date = StringTool.getString(rollDay, "yyyy-MM-dd 00:00:00.0");
//			if(date.compareTo(tNode.getValue().toString())>0){
//				this.messageBox("Ԥ��ʱ�䲻��С�ڽ��졣");
//				return false;
//			}
			if (!initSetTable(TABLE_EXA, true))
				this.messageBox("E0026"); // ��ʼ��������ʧ��
		}
		//Ԥ������ж� yanjing 20131211  end 
		
		
		
		//-----start 20130111 caoyong �ı� MED_APPLY���� URGENT_FLG��ֵ
		if("URGENT_FLG".equalsIgnoreCase(colName)){
		
			OpdOrder opdO= odo.getOpdOrder();
			opdO.getMedApply().setItem(row, "URGENT_FLG", tNode.getValue());
			
		}
		
		//--end--start 20130111 caoyong 
		return false;
	}

	/**
	 * ����ֵ�ı��¼��������Ǯ
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onOpValueChange(TTableNode tNode) {
		int row = tNode.getRow();
		TTable table = (TTable) this.getComponent("TABLEOP");
		String columnName = table.getParmMap(tNode.getColumn());
		OpdOrder order = odo.getOpdOrder();
		if (!deleteOrder(order, row, "���շ�,�����޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {// pangben ====2012 2-28
			return true;
		}
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		} else {
			String orderCode = odo.getOpdOrder().getItemString(row,
					"ORDER_CODE");

			if ("LINK_NO".equalsIgnoreCase(columnName)) {
				int value = TypeTool.getInt(tNode.getValue());
				int oldValue = TypeTool.getInt(tNode.getOldValue());
				if (oldValue == 0 && value > 0) {
					return true;
				}
			}
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		}
		// ����ҽ�� �����޸�ϸ��Ҫ��֮�޸�
		if (("MEDI_QTY".equalsIgnoreCase(columnName) || // ����
				"FREQ_CODE".equalsIgnoreCase(columnName) || // Ƶ��
		"TAKE_DAYS".equalsIgnoreCase(columnName))) { // �շ�
			// �жϸ��������Ƿ��Ǽ���ҽ������ ��������� ��ôѭ���޸�ϸ��
			if ("Y".equalsIgnoreCase(order.getItemString(row, "SETMAIN_FLG"))) {
				String rxNo = order.getItemString(row, "RX_NO");
				String ordersetCode = order.getItemString(row, "ORDER_CODE");
				String orderSetGroup = order.getItemString(row,
						"ORDERSET_GROUP_NO");
				for (int i = 0; i < order.rowCountFilter(); i++) {
					// �ж��Ƿ������ݸ������ϸ��
					if (rxNo.equals(order.getItemData(i, "RX_NO", order.FILTER))
							&& ordersetCode.equals(order.getItemData(i,
									"ORDERSET_CODE", order.FILTER))
							&& orderSetGroup.equals(TypeTool.getString(order
									.getItemData(i, "ORDERSET_GROUP_NO",
											order.FILTER)))) {
						if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
							order.setItem(i, "MEDI_QTY", tNode.getValue(),
									order.FILTER);
						}
						if ("FREQ_CODE".equalsIgnoreCase(columnName))
							order.setItem(i, "FREQ_CODE", tNode.getValue(),
									order.FILTER);

						if ("TAKE_DAYS".equalsIgnoreCase(columnName)) {
							// this.messageBox_(tNode.getValue());
							order.setItem(i, "TAKE_DAYS", tNode.getValue(),
									order.FILTER);
						}
					}
				}
			}
		}
		this.calculateCash(TABLE_OP, "OP_AMT");
		return false;
	}

	/**
	 * ����ҩƷֵ�ı��¼��������Ǯ
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onCtrlValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		TTable table = (TTable) this.getComponent("TABLECTRL");
		int row = tNode.getRow();
		OpdOrder order = odo.getOpdOrder();
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("EXA", row,parm,true)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
			this.messageBox(parm.getValue("MESSAGE"));
			return true;
		}
		if (checkPha(order, row, "EXA",true)) {
			return true;
		}
		if (!deleteOrder(order, row, "���շ�,�����޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {// pangben ====2012 2-28
			return true;
		}
		String columnName = table.getParmMap(column);
		if ("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}
		// TParm parm = odo.getOpdOrder().getRowParm(table.getSelectedRow());
		String orderCode = odo.getOpdOrder().getItemString(row, "ORDER_CODE");
		double mediQty = StringTool.getDouble(tNode.getValue() + "");
		//final int inRow = row;
		final String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		final String orderCodeFinal = odo.getOpdOrder().getItemString(row,
				"ORDER_CODE");
		final String columnNameFinal = columnName;
		if ("MEDI_QTY".equalsIgnoreCase(columnName)) {
			if (SYSCtrlDrugClassTool.getInstance().getOrderCtrFlg(orderCode)) {
				if (!SYSCtrlDrugClassTool.getInstance().getCtrOrderMaxDosage(
						orderCode, mediQty)) {
					if (messageBox(
							"��ʾ��Ϣ/Tip",
							"��������ҩƷĬ������,�Ƿ��������?\r\nQty of this order is over-gived.Do you proceed anyway?",
							this.YES_NO_OPTION) == 1) {
						table.setDSValue(row);
						return true;
					}
				}
			}
			//setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal,table);
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("TAKE_DAYS".equalsIgnoreCase(columnName) || "FREQ_CODE".equalsIgnoreCase(columnName)) {
			//setOnMedValueChange(inRow, execDept, orderCodeFinal, columnNameFinal,table);
			setOnMedValueChange(row, execDept, orderCodeFinal, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"));
		} else if ("LINKMAIN_FLG".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
		} else if ("LINK_NO".equalsIgnoreCase(columnName)) {
			int value = TypeTool.getInt(tNode.getValue());
			int oldValue = TypeTool.getInt(tNode.getOldValue());
			if (oldValue == 0 && value > 0) {
				return true;
			}
			if (StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
				if (value > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else if ("ROUTE_CODE".equalsIgnoreCase(columnName)) {
			if (StringUtil.isNullString(orderCode)) {
				return true;
			}
			String routeCode = tNode.getValue() + "";
			int result = order.isMedRoute(routeCode);
			if (result == -1) {
				this.messageBox("E0019"); // ȡ���÷�ʧ��
				return true;
			} else if (result != 1) {
				this.messageBox("E0022"); // ����ҩƷ����ʹ�ø��÷�
				return true;
			}
			return false;
		}
		// table.setItem(row,columnName,tNode.getValue());
		// 4,8,12,13,16,17,18,21,22
		this.calculateCash(TABLE_CTRL, "CTRL_AMT");
		// table.setDSValue(table.getSelectedRow());
		return false;
	}

	/**
	 * У���������ǩ��ȫ��ҩƷ�Ƿ��Ѿ���ҩ
	 * 
	 * @param order
	 * @param row
	 * @return
	 */
	private boolean checkPha(OpdOrder order, int row, String name,
			boolean spcFlg) {
		// if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <=
		// 0) {
		// // this.messageBox("δȷ�����,���ҽ�ƿ�");
		// // return true;
		// } else {
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if (order.getItemData(row, "RX_NO").equals(
					order.getItemData(i, "RX_NO"))) {
				// �������޸�
				if (Operator.getSpcFlg().equals("Y")) {// ====pangben 2013-4-17
														// У��������ע��
					if (!this.checkDrugCanUpdate(order, name, i, spcFlg, null)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
						this.messageBox("�˴���ǩ��ҩƷ����˻�ҩ,����ɾ�����޸�ҽ��!");
						return true;
					}
					// �������޸�
				} else {
					TParm parm = new TParm();
					if (!order.checkDrugCanUpdate(name, i, parm,true)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
						if (null != parm.getValue("MESSAGE_FLG")
								&& parm.getValue("MESSAGE_FLG").equals("Y")) {
							if (parm.getValue("MESSAGE_INDEX").equals("Y")) {
								this.messageBox(parm.getValue("MESSAGE"));
							}else{
								this.messageBox("�˴���ǩ��ҩƷ�ѼƷ�,�������޸Ļ�ɾ������!");
							}
						} else {
							this.messageBox("�˴���ǩ��ҩƷ����˻�ҩ,����ɾ�����޸�ҽ��!");
						}
						return true;
					}
				}
			}
		}
		// if (order.getItemData(row, "BILL_FLG").equals("Y")) {
		//				
		//
		// }
		// }
		return false;
	}
	/**
	 * ������У��
	 * @param order
	 * @param row
	 * @param name
	 * @return
	 */
	private boolean checkExa(OpdOrder order, int row){
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("δȷ�����,���ҽ�ƿ�");
			// return;
		} else {
			if (order.getItemData(row, "BILL_FLG").equals("Y")) {
				for (int i = 0; i < order.rowCount(); i++) {
					if (StringUtil.isNullString(order.getItemString(i,
							"ORDER_CODE"))) {
						continue;
					}
					if (order.getItemData(row, "RX_NO").equals(
							order.getItemData(i, "RX_NO"))) {
						if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
								&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
							this.messageBox("�ѵ���,�����޸Ļ�ɾ������!");
							return false;
						}
					}
				}

			}
		}
		return true;
	}
	/**
	 * ��ҽTABLEֵ��ֵ����
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnChnValueChange(TTableNode tNode,OpdOrder order,int realrow,int row,
			final String execDept,final String columnNameFinal,final double oldDosageQty){
		if (chaCheck(order, realrow, row)) {
			return true;
		}
		final int inRow = realrow;
		final String orderCodeFinal = order.getItemString(realrow,
				"ORDER_CODE");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//modify by huangtt 20131122	
					String sql = "SELECT PHA_STOCK_FLG FROM IND_ORG WHERE ORG_CODE = '040102'";
					TParm pha = new TParm(TJDODBTool.getInstance().select(sql));
					if(pha.getBoolean("PHA_STOCK_FLG", 0)){
						if (!checkStoreQty(inRow, execDept, orderCodeFinal,
								columnNameFinal,oldDosageQty)) {
							return;
						}
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		order.setItem(realrow, "MEDI_QTY", TCM_Transform.getDouble(tNode
				.getValue()));
		return false;
	}
	/**
	 * ��ҽTABLEֵ��ֵ����
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	private boolean setOnchnValueChange(TTableNode tNode,OpdOrder order,int realrow,int row){
		if (chaCheck(order, realrow, row)) {
			return true;
		}
		String orderCode = order.getItemString(realrow, "ORDER_CODE");
		if (StringUtil.isNullString(orderCode)) {
			return true;
		}
		order.setItem(realrow, "DCTEXCEP_CODE", TCM_Transform
				.getString(tNode.getValue()));
		return false;
	}
	/**
	 * ��ҽTABLEֵ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onChnValueChange(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		String colName = tNode.getTable().getParmMap(column);
		TTable table = (TTable) this.getComponent("TABLECHN");
		// ORDER_DESC1;MEDI_QTY1;DCTEXCEP_CODE1;ORDER_DESC2;MEDI_QTY2;DCTEXCEP_CODE2;ORDER_DESC3;MEDI_QTY3;DCTEXCEP_CODE3;ORDER_DESC4;MEDI_QTY4;DCTEXCEP_CODE4
		if ("ORDER_DESC1".equalsIgnoreCase(colName)
				|| "ORDER_DESC2".equalsIgnoreCase(colName)
				|| "ORDER_DESC3".equalsIgnoreCase(colName)
				|| "ORDER_DESC4".equalsIgnoreCase(colName)) {
			tNode.setValue("");
			return true;
		}

		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CHN_RX);
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int realrow;
		final String execDept = this.getValueString("CHN_EXEC_DEPT_CODE");
		final String columnNameFinal = "MEDI_QTY";

		switch (column) {
		case 1:
			realrow = row * 4 + 0;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 4:
			realrow = row * 4 + 1;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 7:
			realrow = row * 4 + 2;
			if (chaCheck(order, realrow, row)) {
				return true;
			}
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 10:
			realrow = row * 4 + 3;
			if(setOnChnValueChange(tNode, order, realrow, realrow, execDept, columnNameFinal,order.getItemDouble(row, "DOSAGE_QTY"))){
				return true;
			}
			break;
		case 2:
			realrow = row * 4 + 0;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 5:
			realrow = row * 4 + 1;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 8:
			realrow = row * 4 + 2;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;
		case 11:
			realrow = row * 4 + 3;
			if(setOnchnValueChange(tNode, order, realrow, realrow)){
				return true;
			}
			break;

		}

		setChnPckTot();
		this.calculateChnCash(rxNo);
		this.initChnTable(rxNo);
		//TTable table = (TTable) this.getComponent("TABLECHN");
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		int nextColumn = -1;
		if (column < 3 && column > -1) {
			nextColumn = 2;
		} else if (column < 6 && column > 2) {
			nextColumn = 5;
		} else if (column < 9 && column > 5) {
			nextColumn = 8;
		} else {
			nextColumn = 11;
		}
		table.setSelectedColumn(nextColumn);
		return false;
	}

	/**
	 * ��ҩҽ��У��
	 * 
	 * @param order
	 * @param realrow
	 * @param row
	 * @return
	 */
	private boolean chaCheck(OpdOrder order, int realrow, int row) {
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("CHN", realrow,parm,true)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
			this.messageBox(parm.getValue("MESSAGE"));
			return true;
		}
		if (checkPha(order, row, "CHN",true)) {
			return true;
		}
		if (!deleteOrder(order, row, "�ѼƷ�,�����޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {// pangben ====2012
			// 2-28//pangben
			// ====2012 2-28
			return true;
		}
		return false;
	}

	/**
	 * ������ҩ��ʾ���ܿ���
	 */
	public void setChnPckTot() {
		long amt = 0;
		OpdOrder order = odo.getOpdOrder();
		for (int i = 0; i < order.rowCount(); i++) {
			amt += order.getItemDouble(i, "MEDI_QTY");
		}
		for (int i = 0; i < order.rowCount(); i++) {
			order.setItem(i, "PACKAGE_TOT", amt);
		}
		this.setValue("PACKAGE_TOT", amt);
	}

	/**
	 * ����ʷֵ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onAllergyChangeValue(TTableNode tNode) {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		TTable table = (TTable) this.getComponent(this.TABLEALLERGY);
		String columnName = table.getParmMap(column);
		if ("DRUGORINGRD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue("");
			return false;
		}
		if (!"ALLERGY_NOTE".equalsIgnoreCase(columnName)) {
			return true;
		}
		if (StringUtil.isNullString(odo.getDrugAllergy().getItemString(row,
				"DRUGORINGRD_CODE"))) {
			return true;
		}
		return false;
	}

	/**
	 * ���TABLEֵ�ı��¼�
	 * 
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onDiagTableChangeValue(TTableNode tNode) {
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);

		if ("ICD_DESC".equalsIgnoreCase(colName)) {
			tNode.setValue("");
		}
		int row = tNode.getRow();
		if ("MAIN_DIAG_FLG".equalsIgnoreCase(colName)) {
			if (StringUtil.isNullString(odo.getDiagrec().getItemString(row,
					"ICD_CODE"))) {
				odo.getDiagrec().setItem(row, "MAIN_DIAG_FLG", "N");
				return true;
			}
			int[] oldMain = new int[1];
			boolean isHavingMain = odo.getDiagrec().haveMainDiag(oldMain);
			if (!isHavingMain) {
				if ("C".equalsIgnoreCase(wc)
						&& odo.getDiagrec().isSyndromFlg(
										odo.getDiagrec().getItemString(row,
												"ICD_CODE"))) {
					this.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ��UP

					return true;
				} else if ("W".equalsIgnoreCase(wc)) {
					String icdCode = odo.getDiagrec().getItemString(row,
							"ICD_CODE");
					if (!odo.getDiagrec().isMainFlg(icdCode)) {
						this.messageBox("E0132"); // ����ϲ�����Ϊ�����
						return true;
					}
				}

			} else {
				if ("C".equalsIgnoreCase(wc)
						&& odo.getDiagrec().isSyndromFlg(
										odo.getDiagrec().getItemString(row,
												"ICD_CODE"))) {
					this.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ��UP
					return true;
				} else if ("W".equalsIgnoreCase(wc)) {
					String icdCode = odo.getDiagrec().getItemString(row,
							"ICD_CODE");
					if (!odo.getDiagrec().isMainFlg(icdCode)) {
						return true;
					}
					odo.getDiagrec().setItem(oldMain[0], "MAIN_DIAG_FLG", "N");
					tNode.getTable().setDSValue(oldMain[0]);
				}
			}
		}
		return false;
	}

	/**
	 * ��ʼ�������б�
	 */
	public void initOPD() {
		if (this.getParameter() == null) {
			this.messageBox("E0024"); // ��ʼ������ʧ��
			return;
		}
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
		}
		this.setValue("ADM_DATE", admDate);
		TParm sysparm = OPDSysParmTool.getInstance().getSysParm();
		dctMediQty = sysparm.getDouble("DCT_TAKE_QTY", 0);
		dctTakeDays = sysparm.getInt("DCT_TAKE_DAYS", 0);
		dctFreqCode = sysparm.getValue("G_FREQ_CODE", 0);
		dctRouteCode = sysparm.getValue("G_ROUTE_CODE", 0);
		dctAgentCode = sysparm.getValue("G_DCTAGENT_CODE", 0);
		this.setValue("DCT_TAKE_DAYS", dctTakeDays);
		this.setValue("DCT_TAKE_QTY", dctMediQty);
		this.setValue("CHN_FREQ_CODE", OPDSysParmTool.getInstance()
				.getGfreqCode());
		this.setValue("CHN_ROUTE_CODE", OPDSysParmTool.getInstance()
				.getGRouteCode());
		this.setValue("DCTAGENT_CODE", OPDSysParmTool.getInstance()
				.getGdctAgent());
		passIsReady = SYSNewRegionTool.getInstance().isOREASONABLEMED(
				Operator.getRegion());
		mp.onDoubleClicked(false);
		initClinicRoomCombo();
		String roomNo = PatAdmTool.getInstance().getClinicRoomByRealDept(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getID(), Operator.getDept())
				.getValue("ID", 0);
		clinicroom.setValue(roomNo);

		selectPat = true;

		isEng = "en".equalsIgnoreCase(Operator.getLanguage());
		initORadio();
	}

	/**
	 * �ż������𣬼��ﲡ���б������˵ȼ�����Ժʱ����ֶ�
	 */
	public void initOE() {
		TButton erd = (TButton) this.getComponent("ERD");
		TButton bodyTemp = (TButton) this.getComponent("BODY_TEMP");
		TButton orderSheet = (TButton) this.getComponent("ORDER_SHEET");
		TButton erdSheet = (TButton) this.getComponent("ERD_SHEET");
		TTable table = (TTable) this.getComponent("TABLEPAT");
		// ==========pangben 2012-6-28 ��ӳ�������ֵVISIT_CODE
		if ("O".equalsIgnoreCase(admType)) {
			table.setHeader("���,40;������,100;����,80;������,50,VISIT_CODE;������,50;����״̬,80,ADM_STATUS;����״̬,80,REPORT_STATUS");  //add by huangtt 20121128 ������,50
			table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;FIRST_FLG;ADM_STATUS;REPORT_STATUS"); //add by huangtt 20131128 FIRST_FLG
			table.setEnHeader("QueNo;PatNo;Name;visitName;Status;ReportStatus;FirstNext");
			table.setLanguageMap("PAT_NAME|PAT_NAME1");
			table.setLockColumns("0,1,2,3,5");
			// table.setItem("ADM_STATUS;REPORT_STATUS;ERD_LEVEL;VISIT_CODE");
			table.setColumnHorizontalAlignmentData("0,right;1,right;2,left;3,left;4,left;5,left;6,left"); //add by huangtt 20131128 6,left
			erd.setEnabled(false);
			bodyTemp.setEnabled(false);
			orderSheet.setEnabled(false);
			erdSheet.setEnabled(true);
			return;
		}
		//===================== modify by wanglong 20121024 =====================================
//		table.setHeader("���,40;������,100;����,80;������,50,VISIT_CODE;���˵ȼ�,80,ERD_LEVEL;��Ժʱ��,120,timestamp,yyyy-MM-dd HH:mm;����ҽ��,120,timestamp,yyyy-MM-dd HH:mm;����״̬,80,ADM_STATUS;����״̬,80,REPORT_STATUS");
//		table.setEnHeader("QueNo;PatNo;PatName;visitName;TriageLevel;Arrived Date;Latest OrderDate;Status;ReportStatus");
//		table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;ERD_LEVEL;REG_DATE;ORDER_DATE;ADM_STATUS;REPORT_STATUS;ADM_DATE");
		table.setHeader("���,40;������,100;����,80;������,50,VISIT_CODE;���˵ȼ�,80,ERD_LEVEL;������,120,DISE_CODE;��Ժʱ��,120,Timestamp,yyyy-MM-dd HH:mm;����ҽ��,120,Timestamp,yyyy-MM-dd HH:mm;����״̬,80,ADM_STATUS;����״̬,80,REPORT_STATUS");
		table.setEnHeader("QueNo;PatNo;PatName;visitName;TriageLevel;Single Disease;Arrived Date;Latest OrderDate;Status;ReportStatus");
		table.setParmMap("QUE_NO;MR_NO;PAT_NAME;VISIT_CODE;ERD_LEVEL;DISE_CODE;REG_DATE;ORDER_DATE;ADM_STATUS;REPORT_STATUS;ADM_DATE");
		table.setLanguageMap("PAT_NAME|PAT_NAME1");
//		table.setLockColumns("0,1,2,3,4,5,6,7,8");
//		table.setColumnHorizontalAlignmentData("0,right;1,right;2,left;3,left;4,left;5,left;6,left;7,right;8,right");
		table.setLockColumns("all");//modify by wanglong 20121119
		table.setColumnHorizontalAlignmentData("0,right;2,left;3,left;4,left;5,left;7,left;8,left;9,left");
		//===================== modify end ======================================================
		erd.setEnabled(true);
		bodyTemp.setEnabled(true);
		orderSheet.setEnabled(true);
		erdSheet.setEnabled(true);
	}

	/**
	 * ����SESSION combo���ż����ԣ������ص�ǰ��SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// Ϊ�˽����SESSION_CODE��ʾ�ż������𣬷���һ������ʾ��TEXTFIELD��
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * ��ʼ�����combo
	 */
	public void initClinicRoomCombo() {
		Timestamp admDate = (Timestamp) this.getValue("ADM_DATE");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm comboParm = PatAdmTool.getInstance().getClinicRoomForODO(
				StringTool.getString(admDate, "yyyy-MM-dd"), sessionCode,
				admType, Operator.getDept(), Operator.getID());
		if ("en".equals(this.getLanguage()))
			clinicroom.setParmMap("id:ID;name:ENG_DESC");
		else
			clinicroom.setParmMap("id:ID;name:NAME;");
		clinicroom.setParmValue(comboParm);
	}

	/**
	 * ��ʼ�������ݴ���ɵ�radioButton
	 */
	public void initORadio() {
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sql = "SELECT CASE_NO,SEE_DR_FLG " + "	FROM REG_PATADM "
				+ "	WHERE  ADM_TYPE='" + admType + "' AND ADM_DATE=TO_DATE('"
				+ date + "','YYYY-MM-DD') " + "  AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE") + "' AND REALDR_CODE='"
				+ Operator.getID() + "' AND CLINICROOM_NO='"
				+ this.getValueString("CLINICROOM") + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'" + "  AND REGCAN_USER IS NULL ";
		TParm regSysParm = REGSysParmTool.getInstance().selectdata();
		if (regSysParm.getBoolean("CHECKIN_FLG", 0)) {
			sql += "AND ARRIVE_FLG='Y'";
		}
		TDataStore radioNo = new TDataStore();
		radioNo.setSQL(sql);
		if (radioNo.retrieve() == -1) {
			this.messageBox("E0024"); // ��ʼ������ʧ��
			return;
		}
		radioNo.setFilter(" SEE_DR_FLG='N'");
		radioNo.filter();
		TLabel label = (TLabel) this.getComponent("WAIT_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter(" SEE_DR_FLG='Y'");
		radioNo.filter();
		label = (TLabel) this.getComponent("DONE_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
		radioNo.setFilter("SEE_DR_FLG='T'");
		radioNo.filter();
		label = (TLabel) this.getComponent("TEMP_NO");
		label.setZhText(radioNo.rowCount() + " ��");
		label.setEnText(radioNo.rowCount() + " P(s)");
		label.changeLanguage(Operator.getLanguage());
	}
	/**
	 * Ϊ������Ϣ����������
	 * add by huangtt 20131128
	 */
	public void FirstNext(){
		for(int i=0;i<parmpat.getCount("MR_NO");i++){
			if(parmpat.getValue("FIRST_FLG", i).equals("")){
				String mr_no = parmpat.getValue("MR_NO", i);
				String dept_code = parmpat.getValue("REALDEPT_CODE",i);
				String sql = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
				TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
				//û�����ݣ���Ϊ����
				if(selParm.getInt("SUM", 0)<=1){
//				    this.setValue("FIRST_FLG", "Y");
					parmpat.setData("FIRST_FLG", i, "����");
			    //����
				}else{
					parmpat.setData("FIRST_FLG", i, "����");
				}
			}else{
				if(parmpat.getValue("FIRST_FLG", i).equals("Y")){
					parmpat.setData("FIRST_FLG", i, "����");
				}else{
					parmpat.setData("FIRST_FLG", i, "����");
				}
			}
			
		}

		
	}

	/**
	 * ɸѡ����
	 * 
	 * @param type
	 *            String ��ʾ �Ǹ��ؼ����ø÷���
	 */
	public void onSelectPat(String type) {
		if (!selectPat) {
			selectPat = true;
			return;
		}
		//TTable table = (TTable) this.getComponent("TABLEPAT");
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String insteadDr = this.getValueString("INSTEAD_DR");
		if (!"INSTEAD_DR".equals(type) && !"INSTEAD_DEPT".equals(type)) {
			if ("E".equalsIgnoreCase(admType)) {
				onEPatQuery();
			} else {
				onPatQuery();
			}
			initORadio();
			// ��������ѡ���¼� ��ô���ø������
			if (!"CLINICROOM".equals(type)) {
				initClinicRoomCombo();
			}
			// ����ǡ�ʱ�Ρ�combo���û����� ���������¼�����ô��� ���combo
			if ("SESSION_CODE".equals(type) || "ADM_DATE".equals(type)) {
				this.clearValue("CLINICROOM");
			}
			if (insteadDept != null) {
				initInsteadDept();
			}
			FirstNext(); //add by huangtt 20131128
			tblPat.removeRowAll();
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
		} else if ("INSTEAD_DR".equals(type) && !"".equals(insteadDr)) {
			parmpat = PatAdmTool.getInstance()
					.getInsteadPatList(date,
							this.getValueString("SESSION_CODE"), insteadDr,
							admType, "");
			tblPat.removeRowAll();
			FirstNext(); //add by huangtt 20131128
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
		} else if ("INSTEAD_DEPT".equals(type)
				&& !"".equals(this.getValueString("INSTEAD_DEPT"))) {
			parmpat = PatAdmTool.getInstance().getInsteadPatList(date,
					this.getValueString("SESSION_CODE"), "", admType,
					this.getValueString("INSTEAD_DEPT"));
			tblPat.removeRowAll();
			FirstNext(); //add by huangtt 20131128
			tblPat.setParmValue(parmpat);
			tblPat.changeLanguage(Operator.getLanguage());
			selectPat = true;
		}
	}

	/**
	 * ���ﲡ����ѯ
	 */
	public void onPatQuery() {
		TParm parm = new TParm();
		parm.setData("DR_CODE", Operator.getID());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_TYPE", admType);
		parm.setData("CLINICROOM_NO", this.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);

		parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		// Date patd1 = new Date();
		parmpat = PatAdmTool.getInstance().selDateForODOByWait(parm);
		// Date patd2 = new Date();
		if (parmpat.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		jsEnd();
	}

	/**
	 * ���ﲡ����ѯ
	 */
	public void onEPatQuery() {
		TParm parm = new TParm();
		// parm.setData("DR_CODE", Operator.getID());//����Ҫ���ܿ��������ҵ����в���
		// ���ԾͲ���ҽʦΪ������
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		if (this.getValueString("CLINICROOM").length() > 0)
			parm.setData("CLINICROOM_NO", this.getValueString("CLINICROOM"));
		String date = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		parm.setData("ADM_DATE", date);
		if (this.getValueString("SESSION_CODE").length() > 0)
			parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		if ("Y".equalsIgnoreCase(getValueString("REG_WAIT"))) {
			parm.setData("WAIT_DR", "N");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_DONE"))) {
			parm.setData("SEE_DR", "Y");
		} else if ("Y".equalsIgnoreCase(this.getValueString("REG_TEMP"))) {
			parm.setData("TEMP_DR", "T");
		}
		parm.setData("REALDEPT_CODE", Operator.getDept());
		parmpat = PatAdmTool.getInstance().selDateForODOEmgc(parm);
		// QUE_NO;MR_NO;PAT_NAME;ADM_STATUS;REPORT_STATUS
		if (parmpat.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		// liudy
		jsStart();
	}

	/**
	 * �����ɫ�߳̿���
	 */
	public void jsStart() {
		if (erdThread != null)
			return;
		erdThread = new Thread() {
			public void run() {
				try {
					erdThread.sleep(500);
				} catch (Exception e) {

				}
				while (erdThread != null) {
					jsRun();
					try {
						erdThread.sleep(100);
					} catch (Exception e) {

					}
				}
			}
		};
		erdThread.start();
	}

	/**
	 * �����ɫ�߳̽���
	 */
	public void jsEnd() {
		erdThread = null;
	}

	/**
	 * �����ɫ�߳�����
	 */
	public void jsRun() {
		map = new HashMap();
		if (parmpat == null || parmpat.getCount("TIME_LIMIT") < 1)
			return;
		for (int i = 0; i < parmpat.getCount(); i++) {
			int limit = parmpat.getInt("TIME_LIMIT", i);
			Timestamp admDate = parmpat.getTimestamp("ADM_DATE", i);
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			int minute = new Long(
					(now.getTime() - admDate.getTime()) / 1000 / 60).intValue();
			if (minute >= limit) {
				map.put(i, red);
			}
		}
		if (map.size() > 0) {
			tblPat.setRowTextColorMap(map);
		}
	}

	/**
	 * ������Żس���ѯ�¼�
	 */
	public void onQueNo() {
		if (parmpat == null || parmpat.getCount() < 1) {
			this.messageBox("E0025"); // û�в���
			return;
		}
		String queNo = this.getValueString("QUE_NO");
		this.onClear();
		for (int i = 0; i < parmpat.getCount(); i++) {
			String tempQue = parmpat.getValue("QUE_NO", i);
			if (StringUtil.isNullString(tempQue)) {
				continue;
			}
			if (queNo.equalsIgnoreCase(tempQue)) {
				odo = new ODO(parmpat.getValue("CASE_NO", i), parmpat.getValue(
						"MR_NO", i), Operator.getDept(), Operator.getID(),
						parmpat.getValue("ADM_TYPE", i));
				String odo_type = "ODO";
				if ("E".equals(admType)) {
					odo_type = "ODE";
				}
				if (!onLockPat(odo_type)) {
					odo = null;
					return;
				}
				mp.onDoubleClicked(true);
				realDeptCode = parmpat.getValue("REALDEPT_CODE", i);
				wc = PatAdmTool.getInstance().getWestMediFlg(
						Operator.getRegion(),
						parmpat.getValue("ADM_TYPE", i),
						StringTool.getString(parmpat
								.getTimestamp("ADM_DATE", i), "yyyyMMdd"),
						getValueString("SESSION_CODE"),
						parmpat.getValue("CLINICROOM_NO", i)).getValue(
						"WEST_MEDI_FLG", 0);
				if (StringUtil.isNullString(wc)) {
					this.messageBox("E0119"); // �ż����Ǵ���
					this.closeWindow();
				}
				caseNo=parmpat.getValue("CASE_NO", i);//=====��������¸�ֵ pangben 2014-1-20
				initReg(parmpat.getValue("MR_NO", i), parmpat.getValue(
						"CASE_NO", i));
				if (!odo.onQuery()) {
					this.messageBox("E0024"); // ��ʼ������ʧ��
					return;
				}
				odo.getOpdOrder().addEventListener(
						odo.getOpdOrder().ACTION_SET_ITEM, this,
						"onSetItemEvent");
				initPatInfo(parmpat, i);
				initPanel();
				return;
			}
		}

		this.messageBox("E0025"); // û�в���
	}

	/**
	 * �����ڵĲ�������
	 * 
	 * @param odo_type
	 *            String
	 * @return boolean
	 */
	public boolean onLockPat(String odo_type) {
		// System.out.println("odo----"+odo);
		if (odo == null) {
			return false;
		}
		// ������һ�����˽���
		if (!StringUtil.isNullString(lastMrNo)) {
			// �ж��Ƿ�ͬһ�����ˣ��粻�ǣ���Ϊ��һ�����˽���
			if (lastMrNo.equalsIgnoreCase(pat.getMrNo())) {
				// return true;
			} else {
				PatTool.getInstance().unLockPat(lastMrNo);
			}
		}

		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// System.out.println("parm----" + parm);
		// ���������û����
		if (parm == null || parm.getCount() <= 0) {
			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
					Operator.getID(), Operator.getIP());

			lastMrNo = pat.getMrNo();
			return true;
		}
		// ���������Լ��ӵ���
		if (isMyPat()) {
			lastMrNo = pat.getMrNo();
			return true;
		}
		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
		// ���û���������߼�������
		if (!RootClientListener.getInstance().isClient()) {
			if (this.messageBox("�Ƿ����\r\nUnlock this pat?", PatTool
					.getInstance().getLockParmString(pat.getMrNo()), 0) != 0) {
				pat = null;
				return false;
			}

			if (!PatTool.getInstance().unLockPat(pat.getMrNo())) {
				pat = null;
				return false;
			}
			PATLockTool.getInstance().log(
					odo_type + "->" + SystemTool.getInstance().getDate() + " "
							+ Operator.getID() + " " + Operator.getName()
							+ " ǿ�ƽ���[" + aa + " �����ţ�" + pat.getMrNo() + "]");
			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
					Operator.getID(), Operator.getIP());
			lastMrNo = pat.getMrNo();
			return true;
		}

		// ���߽���
		parm.setData("PRGID_U", odo_type);
		parm.setData("MR_NO", pat.getMrNo());
		String prgId = parm.getValue("PRG_ID", 0);
		if ("ODO".equals(prgId))
			parm.setData("WINDOW_ID", "OPD01");
		else if ("ODE".equals(prgId))
			parm.setData("WINDOW_ID", "ERD01");
		else if ("OPB".equals(prgId))
			parm.setData("WINDOW_ID", "OPB0101");
		else if ("ONW".equals(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿأ�����
			parm.setData("WINDOW_ID", "ONW01");
		else if ("ENW".equals(prgId))//====pangben 2013-5-14 ��ӻ�ʿվ�����ܿ�:����
			parm.setData("WINDOW_ID", "ONWE");
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatLcokMessage.x", parm);
		// �ܾ�
		if ("LOCKING".equals(flg)) {

			pat = null;
			return false;
		}
		// ͬ��
		if ("UNLOCKING".equals(flg)) {
			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
					Operator.getID(), Operator.getIP());
			lastMrNo = pat.getMrNo();
			return true;
		}
		// ǿ����
		if ("OK".equals(flg)) {
			PatTool.getInstance().unLockPat(pat.getMrNo());
			PATLockTool.getInstance().log(
					odo_type + "->" + SystemTool.getInstance().getDate() + " "
							+ Operator.getID() + " " + Operator.getName()
							+ " ǿ�ƽ���[" + aa + " �����ţ�" + pat.getMrNo() + "]");
			PatTool.getInstance().lockPat(pat.getMrNo(), odo_type,
					Operator.getID(), Operator.getIP());
			lastMrNo = pat.getMrNo();
			return true;
		}
		pat = null;
		return false;
	}

	/**
	 * ��ѡ���ˣ���ʼ��ҽ��վ
	 */
	public void onTablePatDoubleClick() {
		// System.out.println("onTablePatDoubleClick");
		//Date d1 = new Date();
		TTable table = (TTable) this.getComponent(TABLEPAT);
		int row = table.getSelectedRow();
		PatSelectRow=row; //add by huangtt 20131128
		if(!aheadFlg){
			TParm clinicFeeParm = EKTpreDebtTool.getInstance().checkClinicFee(parmpat.getValue("CASE_NO", row));
			if(clinicFeeParm.getErrCode()<0){
				messageBox(clinicFeeParm.getErrText());
				return;
			}
		}
		wc = PatAdmTool.getInstance().getWestMediFlg(
				Operator.getRegion(),
				parmpat.getValue("ADM_TYPE", row),
				StringTool.getString(parmpat.getTimestamp("ADM_DATE", row),
						"yyyyMMdd"), this.getValueString("SESSION_CODE"),
				parmpat.getValue("CLINICROOM_NO", row)).getValue(
				"WEST_MEDI_FLG", 0);
		if (StringUtil.isNullString(wc)) {
			this.messageBox("E0119"); // �ż����Ǵ���
			this.closeWindow();
		}
		this.onClear();
		initOpd(parmpat, row);
		initINS();
		viewClinicFee();
		//caowl 20131106 start  ��ʼ������������ֵ
//		String mr_no = parmpat.getValue("MR_NO", row);
//		String dept_code = parmpat.getValue("DEPT_CODE",row);
//		String sql = "SELECT COUNT(MR_NO) FROM SYS_EMR_INDEX WHERE MR_NO = '"+mr_no+"' AND DEPT_CODE = '"+dept_code+"'";
//		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
//		//û�����ݣ���Ϊ����
//		if(selParm.getCount()<=1){
//		    this.setValue("FIRST_FLG", "Y");
//	    //����
//		}else{
//			this.setValue("NEXT_FLG", "Y");
//		}
		//caowl 20131106 end
		
	}

	/**
	 * ��ʼ��ҽ��վ������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	private void initOpd(TParm parm, int row) {
		// this.onClear();
		// ��ʼ��reg����
		//deleteLisPosc = false;
		caseNo = parm.getValue("CASE_NO", row);// ==========pangben modify
		// 20110914
		initReg(parm.getValue("MR_NO", row), parm.getValue("CASE_NO", row));
		// ��ʼ��odo����
		odo = new ODO(parm.getValue("CASE_NO", row), parm
				.getValue("MR_NO", row), Operator.getDept(), Operator.getID(),
				parm.getValue("ADM_TYPE", row));
		String odo_type = "ODO";
		if ("E".equals(admType)) {
			odo_type = "ODE";
		}
		if (!onLockPat(odo_type)) {
			odo = null;
			return;
		}
		realDeptCode = parm.getValue("REALDEPT_CODE", row);
		odo.getOpdOrder().addEventListener(odo.getOpdOrder().ACTION_SET_ITEM,
				this, "onSetItemEvent");
		mp.onDoubleClicked(true);
		if (!odo.onQuery()) {
			this.messageBox("E0024"); // ��ʼ������ʧ��
			//deleteLisPosc = false;
			return;
		}
		// ��ʼ��pat����
		initPatInfo(parm, row);
		initPanel();
		setRootPanelWidth();
		phaCode = ClinicRoomTool.getInstance().getOrgByOdo(
				Operator.getRegion(),
				StringTool.getString((Timestamp) this.getValue("ADM_DATE"),
						"yyyyMMdd"), this.getValueString("SESSION_CODE"),
				admType, parm.getValue("REALDR_CODE", row),
				parm.getValue("REALDEPT_CODE", row),
				parm.getValue("CASE_NO", row)).getValue("ORG_CODE", 0);
		if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
			this.messageBox("E0117"); // ҩ��û�п������ڣ����ܿ���
			this.closeWindow();
		}
		this.setValue("MED_RBORDER_DEPT_CODE", phaCode);
		this.setValue("CTRL_RBORDER_DEPT_CODE", phaCode);
		this.setValue("OP_EXEC_DEPT", parm.getValue("REALDEPT_CODE", row));
		if ("C".equalsIgnoreCase(wc)) {
			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
		}
		lastMrNo = pat.getMrNo();
	}

	/**
	 * ����ROOT_PANEL�Ŀ��
	 */
	public void setRootPanelWidth() {
		TPanel diag = (TPanel) this.getComponent("DIAGNOSISPANEL");
		diag.setWidth(418);
		TMovePane upDown = (TMovePane) this.getComponent("tMovePane_1");
		upDown.setWidth(1088);
		TTabbedPane order = (TTabbedPane) this.getComponent("TTABPANELORDER");
		order.setWidth(1088);
		TMovePane leftRight = (TMovePane) this.getComponent("tMovePane_0");
		leftRight.setX(1100);
		TRootPanel menu = (TRootPanel) this.getComponent("COMM_MENU");
		menu.setX(1120);
		menu.setWidth(120);

	}

	/**
	 * ��ʼ��REG����
	 * 
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 */
	public void initReg(String mrNo, String caseNo) {
		pat = Pat.onQueryByMrNo(mrNo);
		reg = Reg.onQueryByCaseNo(pat, caseNo);
		opb = OPB.onQueryByCaseNo(reg);// ====pangben 2012-2-28
	}

	/**
	 * ��ʼ���������
	 */
	public void initInsteadDept() {
		this.clearValue("INSTEAD_DEPT;INSTEAD_DR");
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDept(admType, admDate,
				sessionCode, Operator.getRegion());
		insteadDept.setParmValue(parm);
		this.callFunction("UI|INSTEAD_DR|setEnabled", false);
	}

	/**
	 * ����Ʊ���ʱ�䣬������ҽʦcombo��Ϊ���ã�����ʼ������ҽʦcombo
	 */
	public void onInsteadDept() {
		String dept = this.getValueString("INSTEAD_DEPT");
		this.clearValue("INSTEAD_DR");
		if (StringUtil.isNullString(dept)) {
			dr.setEnabled(false);
			return;
		}
		dr.setEnabled(true);
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyy-MM-dd");
		String sessionCode = this.getValueString("SESSION_CODE");
		TParm parm = PatAdmTool.getInstance().getInsteadDrByDept(admType,
				sessionCode, admDate, dept);
		if ("en".equals(this.getLanguage()))
			dr.setParmMap("id:ID;name:USER_ENG_NAME;Py1:PY1");
		else
			dr.setParmMap("id:ID;name:NAME;Py1:PY1");
		dr.setParmValue(parm);
		if ("E".equals(admType)) { // ����ʱ ���������ʾ�ÿ��ҵ����в�����Ϣ
			this.onSelectPat("INSTEAD_DEPT");
		}
	}

	/**
	 * ��ʼ������������Ϣ������������
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	public void initPatInfo(TParm parm, int row) {
		this.setValue("QUE_NO", parm.getValue("QUE_NO", row));
		if (isEng) {
			this.setValue("PAT_NAME", parm.getValue("PAT_NAME1", row));
		} else {
			this.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
		}
		serviceLevel = reg.getServiceLevel();
		this.setValue("SERVICE_LEVEL", reg.getServiceLevel());
		this.setValue("CTZ_CODE", reg.getCtz1Code());
		this.setValue("INT_PAT_TYPE", reg.getInsPatType());
		if (!StringUtil.isNullString(reg.getInsPatType())
				&& !reg.getInsPatType().equals("1")) {
			TTextFormat INS_DISEAS_CODE = (TTextFormat) this
					.getComponent("INS_DISEAS_CODE");
			TParm cat1TypeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT DISEASE_CODE FROM INS_MZ_CONFIRM WHERE CASE_NO='"
							+ caseNo + "' AND MR_NO='"
							+ parm.getValue("MR_NO", row) + "'"));
			INS_DISEAS_CODE.setValue(cat1TypeParm.getValue("DISEASE_CODE", 0));
		}
		odo.getOpdOrder().setServiceLevel(serviceLevel); // opdorder�������÷���ȼ�
		this.setValue("WEIGHT", odo.getRegPatAdm().getItemString(0, "WEIGHT"));
		this.setValue("MR_NO", parm.getValue("MR_NO", row));
		this.setValue("SEX_CODE", odo.getPatInfo().getItemString(0,
						"SEX_CODE"));
		String age = OdoUtil.showAge(parm.getTimestamp("BIRTH_DATE", row),
				SystemTool.getInstance().getDate());
		this.setValue("AGE", age);
		if (!"2".equalsIgnoreCase(pat.getSexCode())) {
			TTextFormat tFormat = (TTextFormat) this.getComponent("LMP_DATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_STARTDATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_ENDDATE");
			tFormat.setValue("");
			tFormat.setEnabled(false);
		} else {
			TTextFormat tFormat = (TTextFormat) this.getComponent("LMP_DATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_STARTDATE");
			tFormat.setEnabled(true);
			tFormat = (TTextFormat) this.getComponent("BREASTFEED_ENDDATE");
			tFormat.setEnabled(true);
		}

		this.setValue("PAT1_CODE", parm.getValue("PAT1_CODE", row));
		this.setValue("PAT2_CODE", parm.getValue("PAT2_CODE", row));
		this.setValue("PAT3_CODE", parm.getValue("PAT3_CODE", row));

		this.setValue("HANDICAP_FLG", parm.getValue("HANDICAP_FLG", row));
		this.setValue("PREMATURE_FLG", parm.getValue("PREMATURE_FLG", row));
		this.setValue("LMP_DATE", parm.getTimestamp("LMP_DATE", row));

		// �����ݿ��е�ֵ��ֵ���ṹ��������
		this.setValue("PRE_WEEK", OdoUtil.getPreWeek(parm.getTimestamp(
				"LMP_DATE", row), parm.getTimestamp("ADM_DATE", row))
				+ "");
		this.setValue("BREASTFEED_STARTDATE", parm.getTimestamp(
				"BREASTFEED_STARTDATE", row));
		this.setValue("BREASTFEED_ENDDATE", parm.getTimestamp(
				"BREASTFEED_ENDDATE", row));
		String ctz1 = parm.getValue("CTZ1_CODE", row);
		String ctz2 = parm.getValue("CTZ2_CODE", row);
		String ctz3 = parm.getValue("CTZ3_CODE", row);
		this.setValue("CTZ1_CODE", ctz1);
		this.setValue("CTZ2_CODE", ctz2);
		this.setValue("CTZ3_CODE", ctz3);
		ctz = new String[3];
		ctz[0] = ctz1;
		ctz[1] = ctz2;
		ctz[2] = ctz3;
	}

	/**
	 * ��ʼ������panel
	 */
	public void initPanel() {
		// System.out.println("in initPanel");
		if (odo == null) {
			return;
		}
		// ���߿���
		initSubject();
		// ���
		initDiag();
		// ����ʷ
		initMedHistory();
		// ����ʷ
		initAllergy();
		// ������
		this.setTableInit(TABLE_EXA, false);
		// ����
		this.setTableInit(TABLE_OP, false);
		// ��ҩ
		this.setTableInit(TABLE_MED, false);
		// ��ҩ
		this.setTableInit(TABLE_CHN, false);
		// ��ҩ

		this.setTableInit(TABLE_CTRL, false);
		// System.out.println("before tabP");
		// initCtrl();
		TTabbedPane tabP = (TTabbedPane) this.getComponent("TTABPANELORDER");
		if (tabP.getSelectedIndex() != 0) {
			tabP.setSelectedIndex(0);
		}
		initExa();
		onDiagPnChange();
	}

	/**
	 * ��ʼ������ҩƷ
	 */
	private void initCtrl() {
		boolean isInit = isTableInit(TABLE_CTRL);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(CTRL_RX, CTRL);
			setTableInit(TABLE_CTRL, true);
		} else {
			rxNo = this.getValueString(CTRL_RX);
		}
		if (!initNoSetTable(rxNo, TABLE_CTRL, isInit))
			this.messageBox("E0026"); // ��ʼ��������ʧ��
		onChangeRx("2");
	}

	/**
	 * ��ʼ������
	 */
	private void initOp() {
		boolean isInit = isTableInit(TABLE_OP);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(OP_RX, OP);
			setTableInit(TABLE_OP, true);
		} else {
			rxNo = this.getValueString(OP_RX);
		}
		// System.out.println("rxNo================="+rxNo);
		if (!initNoSetTable(rxNo, TABLE_OP, isInit))
			this.messageBox("E0027"); // ��ʼ������ʧ��
		onChangeRx("4");
	}

	/**
	 * ȡ��RX COMBO��ֵ
	 * 
	 * @param data
	 *            String[]
	 * @param i
	 *            int
	 * @return String
	 */
	private String getRxNo(String[] data, int i) {
		if (data == null || data.length < 1 || i < 0)
			return null;
		String rxNo = (data[i].split(","))[0];
		return rxNo;
	}

	/**
	 * ��ʼ����ҩ
	 */
	private void initChnMed() {
		boolean isInit = isTableInit(TABLE_CHN);
		// System.out.println("isInit-----"+isInit);
		rxType = CHN;
		String rxNo = this.getValueString(CHN_RX);
		// System.out.println("CHN_RX-----"+CHN_RX);
		if (!isInit) {
			rxNo = initRx(CHN_RX, CHN);
			setTableInit(TABLE_CHN, true);

		} else {
			rxNo = this.getValueString(CHN_RX);

		}
		if (StringUtil.isNullString(odo.getOpdOrder().getItemString(0,
				"ORDER_CODE"))) {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		} else {
			this.setValue("DCT_TAKE_DAYS", odo.getOpdOrder().getItemInt(0,
					"TAKE_DAYS"));
			this.setValue("DCT_TAKE_QTY", odo.getOpdOrder().getItemDouble(0,
					"DCT_TAKE_QTY"));
			this.setValue("CHN_FREQ_CODE", odo.getOpdOrder().getItemString(0,
					"FREQ_CODE"));
			this.setValue("CHN_ROUTE_CODE", odo.getOpdOrder().getItemString(0,
					"ROUTE_CODE"));
			this.setValue("DCTAGENT_CODE", odo.getOpdOrder().getItemString(0,
					"DCTAGENT_CODE"));
			this.setValue("DR_NOTE", odo.getOpdOrder().getItemString(0,
					"DR_NOTE"));
			this.setValue("CHN_EXEC_DEPT_CODE", odo.getOpdOrder()
					.getItemString(0, "EXEC_DEPT_CODE"));
			this.setValue("URGENT_FLG", odo.getOpdOrder().getItemString(0,
					"URGENT_FLG"));
			this.setValue("RELEASE_FLG", odo.getOpdOrder().getItemString(0,
					"RELEASE_FLG"));
		}
		this.initChnTable(rxNo);
		onChangeRx("3");
	}

	/**
	 * ��ʼ����ҩ
	 */
	public void initMed() {
		boolean isInit = isTableInit(TABLE_MED);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(MED_RX, MED);
			setTableInit(TABLE_MED, true);
		} else {
			rxNo = this.getValueString(MED_RX);
		}

		if (!initNoSetTable(rxNo, TABLE_MED, isInit))
			this.messageBox("E0028"); // ��ʼ������ҩʧ��

		onChangeRx("1");

	}

	/**
	 * ��ҽһ�Ŵ���ǩ������ҽ���ĸ����ֶε�ֵ�ı��¼�
	 * 
	 * @param fieldName
	 *            String Ҫ�ı���ֶ���
	 * @param type
	 *            String
	 */
	public void onChnChange(String fieldName, String type) {
		if (odo == null)
			return;
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CHN_RX);

		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		String value = this.getValueString(fieldName);
		if ("CHN_FREQ_CODE".equalsIgnoreCase(fieldName)) {
			fieldName = "FREQ_CODE";
		}
		if ("CHN_ROUTE_CODE".equalsIgnoreCase(fieldName)) {
			TComboBox t = (TComboBox) this.getComponent(fieldName);
			value = t.getSelectedID();
			fieldName = "ROUTE_CODE";
		}
		if ("DCT_TAKE_DAYS".equalsIgnoreCase(fieldName)) {
			fieldName = "TAKE_DAYS";
		}
		// �жϼ�ҩ��ʽ�Ƿ���д ����Ϊ��
		if ("DCTAGENT_CODE".equals(fieldName)) {
			if (this.getValueString("DCTAGENT_CODE").length() == 0) {
				this.messageBox("E0190");
				this.setValue("DCTAGENT_CODE", dctAgentCode);
				return;
			}
		}
		int count = order.rowCount();
		for (int i = 0; i < count; i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			if ("E".equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				this.messageBox("E0055"); // �ѼƷ�ҽ������ɾ��
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !"E".equals(order.getItemString(i, "BILL_TYPE"))) {
					this.messageBox("E0055"); // �ѼƷ�ҽ������ɾ��
					return;
				}
			}

			if ("string".equalsIgnoreCase(type)
					|| StringUtil.isNullString(type)) {
				order.setItem(i, fieldName, value);
			} else {
				order.setItem(i, fieldName, value);
			}
		}
	}

	/**
	 * ��ʼ����ҩ����ǩ
	 * 
	 * @param rxNo
	 *            String
	 * @return boolean
	 */
	public boolean initChnTable(String rxNo) {
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0029"); // û�д���ǩ
			return false;
		}
		TTable table = (TTable) this.getComponent(TABLE_CHN);
		String filter = "RX_NO='" + rxNo + "'";
		OpdOrder order = odo.getOpdOrder();
		order.setFilter(filter);

		if (!order.filter()) {
			this.messageBox("E0030"); // �޴�ҩƷ
			return false;
		}
		int totRow = order.rowCount();

		if (!StringUtil.isNullString(order.getItemString(totRow - 1,
				"ORDER_CODE"))
				|| totRow % 4 != 0 || totRow < 1) {
			for (int i = 0; i < 4 - totRow % 4; i++) {
				if (order.newOrder("3", rxNo) == -1) {
					this.messageBox("E0031"); // ��ʾ��ҩʧ��
					return false;
				}
				order.setItem(i, "PHA_TYPE", "G");
			}
		}

		if (!order.filter()) {
			this.messageBox("E0031"); // ��ʾ��ҩʧ��
			return false;
		}

		TParm parm = odo.getOpdOrder().getBuffer(order.PRIMARY);
		TParm tableParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			int idx = i % 4 + 1;
			tableParm.addData("ORDER_DESC" + idx, parm
					.getValue("ORDER_DESC", i));
			tableParm.addData("MEDI_QTY" + idx, parm.getDouble("MEDI_QTY", i));
			tableParm.addData("DCTEXCEP_CODE" + idx, parm.getValue(
					"DCTEXCEP_CODE", i));
		}
		table.setParmValue(tableParm);
		if (!StringUtil.isNullString(parm.getValue("ORDER_CODE", 0))) {
			this.setValue("DCT_TAKE_DAYS", parm.getDouble("TAKE_DAYS", 0));
			this.setValue("DCT_TAKE_QTY", parm.getDouble("DCT_TAKE_QTY", 0));
			this.setValue("CHN_FREQ_CODE", parm.getValue("FREQ_CODE", 0));
			this.setValue("CHN_ROUTE_CODE", parm.getValue("ROUTE_CODE", 0));
			this.setValue("DCTAGENT_CODE", parm.getValue("DCTAGENT_CODE", 0));
			this.setValue("DR_NOTE", parm.getValue("DR_NOTE", 0));
			this.setValue("CHN_EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
					0));
			this.setValue("URGENT_FLG", parm.getValue("URGENT_FLG", 0));
			this.setValue("RELEASE_FLG", parm.getValue("RELEASE_FLG", 0));
		} else {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", phaCode);
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		}
		setChnPckTot();
		this.calculateChnCash(rxNo);
		return true;
	}

	/**
	 * ��ʼ���Ǽ���ҽ����TABLE
	 * 
	 * @param rxNo
	 *            String
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initNoSetTable(String rxNo, String tableName, boolean isInit) {
		if (StringUtil.isNullString(tableName))
			return false;
		TTable table = (TTable) this.getComponent(tableName);
		String filter = "RX_NO='" + rxNo + "'";
		if (TABLE_OP.equalsIgnoreCase(tableName)) {
			filter += " AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='' OR HIDE_FLG='N')";
		}
		if (!isInit) {
			table.setDataStore(odo.getOpdOrder());
			table.setFilter(filter);
			if (!table.filter()) {
				this.messageBox("E0024"); // ��ʼ������ʧ��
				return false;
			}
		} else {
			odo.getOpdOrder().setFilter(filter);
			if (!odo.getOpdOrder().filter()) {
				this.messageBox("E0024"); // ��ʼ������ʧ��
				return false;
			}
		}
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);

		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
		return true;
	}

	/**
	 * ����
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean �Ƿ�Ϊ�ո�д���order�����ǣ�����в������粻���������
	 * @return String
	 */
	public String lockRows(String tableName, boolean isInit) {
		TTable table = (TTable) this.getComponent(tableName);
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
			return "";
		return lockRow.substring(0, lockRow.lastIndexOf(","));
	}

	private String tempRxNo;

	public boolean rxFilter(TParm parm, int row) {
		String s = parm.getValue("SETMAIN_FLG", row);
		return parm.getValue("RX_NO", row).equalsIgnoreCase(tempRxNo)
				&& (s.equalsIgnoreCase("Y") || s.length() == 0);
	}

	/**
	 * ��ʼ����ʾ����ҽ����TABLE
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 * @return boolean
	 */
	public boolean initSetTable(String tableName, boolean isInit) {
		if (StringUtil.isNullString(tableName)) {
			return false;
		}
		TTable table = (TTable) this.getComponent(tableName);
		String rxNo = (String) this.getValue("EXA_RX");
		String filter = "RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')";
		if (!isInit)
			table.setDataStore(odo.getOpdOrder());
		table.setFilter(filter);
		table.filter();
		tempRxNo = (String) this.getValue("EXA_RX");
		table.getDataStore().filterObject(this, "rxFilter");
		table.setDSValue();
		this.calculateCash(TABLE_EXA, "EXA_AMT");
		if (whetherCallInsItf) {
			Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
					whetherCallInsItf);
			table.setRowTextColorMap(inscolor);
		}
		return true;
	}

	/**
	 * ��ʼ���ƶ�COMBO�����ش�����
	 * 
	 * @param rxName
	 *            String
	 * @param rxType
	 *            String
	 * @return String
	 */
	private String initRx(String rxName, String rxType) {
		if (StringUtil.isNullString(rxName) || StringUtil.isNullString(rxType)) {
			return "";
		}
		TComboBox combo = (TComboBox) this.getComponent(rxName);
		String[] data = odo.getOpdOrder().getRx(rxType);
		if (data == null || data.length < 0) {
			return "";
		}
		String rxNo = getRxNo(data, 0);
		if (StringUtil.isNullString(rxNo)) {
			data = new String[1];
			rxNo = odo.getOpdOrder().newPrsrp(rxType);
			if (StringUtil.isNullString(rxNo)) {
				this.messageBox("E0032"); // ���ɴ�����ʧ��
				return "";
			}
			if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				data[0] = rxNo + ",��" + 1 + "�� Rx";
			} else {
				data[0] = rxNo + ",��" + 1 + "�� ����ǩ";
			}

		} else {
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)
					&& !CHN.equalsIgnoreCase(rxType)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
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
	 * ��ʼ��������
	 */
	private void initExa() {
		boolean isInit = isTableInit(TABLE_EXA);
		String rxNo = "";
		if (!isInit) {
			rxNo = initRx(EXA_RX, EXA);
			if (StringUtil.isNullString(rxNo)) {
				this.messageBox("E0026"); // ��ʼ��������ʧ��
				return;
			}
			setTableInit(TABLE_EXA, true);
		} else {
			rxNo = this.getValueString(this.EXA_RX);
		}
		if (!initSetTable(TABLE_EXA, isInit))
			this.messageBox("E0026"); // ��ʼ��������ʧ��
		onChangeRx("5");

	}

	/**
	 * �жϸ���TABLE�Ƿ��Ѿ���ʼ����
	 * 
	 * @param tableName
	 *            String
	 * @return boolean
	 */
	private boolean isTableInit(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		return TCM_Transform.getBoolean(table.getData());
	}

	/**
	 * ���ø���TABLE�Ѿ���ʼ���ı��
	 * 
	 * @param tableName
	 *            String
	 * @param isInit
	 *            boolean
	 */
	private void setTableInit(String tableName, boolean isInit) {
		TTable table = (TTable) this.getComponent(tableName);
		table.setData(isInit);
	}

	/**
	 * ��ʼ�����߿���
	 */
	public void initSubject() {
		// System.out.println("in initSubject");
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("TYPE", "ZS");
		// TParm microParm=new TParm();
		TParm allParm = new TParm();
		if ("N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			// zhangyong20110427 
			saveFiles = EmrUtil.getInstance().getGSTemplet(realDeptCode,
					Operator.getID(), admType);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			word.setWordParameter(allParm);
			word.setCanEdit(true);
		} else {
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			word.setWordParameter(allParm);
			word.setCanEdit(true);
		}
		// $$=========== add by lx 2012/02/24/ ����
		word.fixedTryReset(odo.getMrNo(), odo.getCaseNo());
		// $$=========== add by lx 2012/02/24/ ����  ˢ��ץȡ����

		familyHisFiles = EmrUtil.getInstance().getFamilyHistoryPath(
				odo.getMrNo(), realDeptCode, admType);
		familyWord.onOpen(familyHisFiles[0], familyHisFiles[1], Integer
				.parseInt(familyHisFiles[2]), false);
		TParm familyParm = new TParm();
		familyParm
				.addListener("onDoubleClicked", this, "onFamilyDoubleClicked");
		familyWord.setWordParameter(familyParm);
		// System.out.println("after initSubject");
	}

	/**
	 * �ṹ����������˫���¼�,����Ƭ�����.
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onDoubleClicked(int pageIndex, int x, int y) {
		// �������û�������ߡ����ߡ���������ʲô������.
		String str = "";
		if (word.focusInCaptue("SUB")) {
			str = "SUB";
		} else if (word.focusInCaptue("OBJ")) {
			str = "OBJ";
		} else if (word.focusInCaptue("PHY")) {
			str = "PHY";
		} else if (word.focusInCaptue("EXA_RESULT")) {
			str = "EXA_RESULT";
		} else if (word.focusInCaptue("PROPOSAL")) {
			str = "PROPOSAL";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if ("EXA_RESULT".equalsIgnoreCase(str)) {
			onInsertResult();
		} else {
			onInsertPY();
		}
	}

	/**
	 * ����ʷ����˫���¼�������Ƭ�����
	 * 
	 * @param pageIndex
	 *            int
	 * @param x
	 *            int
	 * @param y
	 *            int
	 */
	public void onFamilyDoubleClicked(int pageIndex, int x, int y) {

		// �������û�������ߡ����ߡ���������ʲô������.
		String str = "";
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}

		onInsertFamilyPY();

	}

	/**
	 * ��ʼ�����
	 */
	public void initDiag() {
		TTable table = (TTable) getComponent(TABLEDIAGNOSIS);
		odo.setIcdType(wc);

		TRadioButton w = (TRadioButton) this.getComponent("W_FLG");
		TRadioButton c = (TRadioButton) this.getComponent("C_FLG");
		if ("W".equalsIgnoreCase(wc)) {
			w.setValue("Y");
		} else {
			c.setValue("Y");
		}
	//	int[] tempInt = new int[] { 0 };
//		mainFlg = odo.getDiagrec().haveMainDiag(tempInt);
//		if (mainFlg) {
//			mainDiag = tempInt[0];
//		}
		w = null;
		c = null;
		table.setDataStore(odo.getDiagrec());
		table.setDSValue();
	}

	/**
	 * ��ҽ���radioע��
	 */
	public void onWFlg() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		table.acceptText();
		wc = "W";
	}

	/**
	 * ��ҽ���radioע��
	 */
	public void onCFlg() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		table.acceptText();
		wc = "C";
	}

	/**
	 * ��ʼ������ʷ
	 */
	public void initMedHistory() {
		TTable table = (TTable) getComponent(TABLEMEDHISTORY);
		table.setDataStore(odo.getMedHistory());
		table.setDSValue();
	}

	/**
	 * ��ʼ������ʷ
	 */
	public void initAllergy() {
		TTable table = (TTable) getComponent(TABLEALLERGY);
		DrugAllergy all = odo.getDrugAllergy();
		table.setDataStore(all);
		all.setFilter("DRUG_TYPE='B'");
		all.filter();
		table.setDSValue();
		// table.setLockRows(lockRows(TABLEALLERGY,true));
	}

	/**
	 * ����һ�Ŵ���ǩ������combo������ʼtable
	 * 
	 * @param rxType
	 *            ��������
	 * @param rxName
	 *            combo��
	 * @param tableName
	 *            table��
	 */
	public void onAddOrderList(String rxType, String rxName, String tableName) {
		// ͨ��ȡ��ԭ��ȡ���´�����
		String rxNo = odo.getOpdOrder().newPrsrp(rxType);
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0033"); // ��������ʧ��
			return;
		}
		// ����combo����ֵ����������ʾֵ
		TComboBox combo = (TComboBox) this.getComponent(rxName);
		String[] data = odo.getOpdOrder().getRx(rxType);
		String newData = "";
		if (isEng) {
			newData = rxNo + ",��" + (data.length) + "�� Rx";
		} else {
			newData = rxNo + ",��" + (data.length) + "�� ����ǩ";
		}

		combo.addData(newData, ",");
		combo.setValue(rxNo);
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		if (this.CHN.equalsIgnoreCase(rxType)) {
			this.setValue("DCT_TAKE_DAYS", dctTakeDays);
			this.setValue("DCT_TAKE_QTY", dctMediQty);
			this.setValue("CHN_FREQ_CODE", this.dctFreqCode);
			this.setValue("CHN_ROUTE_CODE", this.dctRouteCode);
			this.setValue("DCTAGENT_CODE", this.dctAgentCode);
			this.setValue("DR_NOTE", "");
			this.setValue("CHN_EXEC_DEPT_CODE", "");
			this.setValue("URGENT_FLG", "");
			this.setValue("RELEASE_FLG", "");
		}
		onChangeRx(rxType);
	}

	/**
	 * ɾ�����Ŵ���ǩ
	 * 
	 * @param rxType
	 *            ��������
	 */
	public void onDeleteOrderList(int rxType) {
		String rxNo = "";
		String tableName = "";
		OpdOrder order = odo.getOpdOrder();
		String oldfilter = order.getFilter();
		int count = -1;
		TTable table;
		StringBuffer billFlg=new StringBuffer();//�ж��Ƿ����ɾ�� ��ͬһ�Ŵ���ǩ�е�״̬����ͬ����ɾ��
		billFlg.append(order.getItemData(0, "BILL_FLG"));
		TParm parm=new TParm();
		switch (rxType) {
		case 1:// ��ҩ
			rxNo = (String) this.getValue("MED_RX");
			tableName = TABLE_MED;
			this.setValue("MED_AMT", "");
			if (StringUtil.isNullString(tableName)) {
				this.messageBox("E0034"); // ȡ�����ݴ���
				return;
			}
			table = (TTable) this.getComponent(tableName);
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				if (rxType == 1 || rxType == 2) {
					//������start
					if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
						if (!order.checkDrugCanUpdate("MED", i,parm,false)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
							this.messageBox(parm.getValue("MESSAGE"));
							return;
						}
					} else {
						if (i - 1 >= 0) {
							if (!this.checkDrugCanUpdate(order, "MED", i - 1,
									false,null)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
								this.messageBox("E0189");
								return;
							}
						}
					}
					//������end
				}
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "�ѼƷ�,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			if (this.messageBox("��ʾ","�Ƿ�ɾ������ǩ",2)!=0) {//=========pangben 2014-1-15
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			order.newOrder(rxType + "", rxNo);
			order.setFilter(oldfilter);
			order.filter();
			table.setDSValue();
			break;
		case 2://����ҩƷ

			rxNo = (String) this.getValue("CTRL_RX");
			tableName = TABLE_CTRL;
			this.setValue("CTRL_AMT", "");
			if (StringUtil.isNullString(tableName)) {
				this.messageBox("E0034"); // ȡ�����ݴ���
				return;
			}
			table = (TTable) this.getComponent(tableName);
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				if (rxType == 1 || rxType == 2) {

					if (!order.checkDrugCanUpdate("MED", i,parm,false)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
						this.messageBox(parm.getValue("MESSAGE"));
						return;
					}
				}
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			order.newOrder(rxType + "", rxNo);
			order.setFilter(oldfilter);
			order.filter();
			table.setDSValue();
			break;
		case 3: // ��ҩ
			rxNo = (String) this.getValue("CHN_RX");
			tableName = TABLE_CHN;
			table = (TTable) this.getComponent(TABLE_CHN);
			this.setValue("CHN_AMT", "");
			this.setValue("PACKAGE_TOT", "");
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!order.checkDrugCanUpdate("CHN", i,parm,false)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
					this.messageBox(parm.getValue("MESSAGE"));
					return;
				}
				if (!deleteOrder(order, i, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				order.deleteRow(i);
			}
			order.setFilter(oldfilter);
			order.filter();
			table.setDSValue();
			this.initChnTable(rxNo);
			break;
		case 4:
			rxNo = (String) this.getValue("OP_RX");
			tableName = TABLE_OP;
			this.setValue("OP_AMT", "");
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			table = (TTable) this.getComponent(TABLE_OP);
			if (count <= 0) {
				return;
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				if (!deleteOrder(order, i, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
					order.setFilter(oldfilter);
					order.filter();
					table.setDSValue();
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				order.deleteRow(i);
			}
			order.setFilter(oldfilter);
			order.filter();

			table.setDSValue();
			break;
		case 5:
			
			rxNo = (String) this.getValue("EXA_RX");
			tableName = TABLE_EXA;
			this.setValue("EXA_AMT", "");
			//���ݴ���ǩ�Ų�ѯcase_no
			String selectCaseNo = "SELECT CASE_NO FROM OPD_ORDER WHERE RX_NO = '"+rxNo+"'";
			TParm selectCaseNoParm = new TParm(TJDODBTool.getInstance().select(selectCaseNo));
			
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			count = order.rowCount();
			table = (TTable) this.getComponent(TABLE_EXA);
			if (count <= 0) {
				return;
			}
		
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				//======pangben 2012-8-10 ����ѵ���
				if (!checkSendPah(order, i))// ����ѷ�ҩ�ѵ���
					return;
				//=========pangben 2013-1-29
				if (!deleteOrder(order, i, "���շ�,�������޸Ļ�ɾ��ҽ��","�˴���ǩ�д����Ѿ��Ǽǵ�ҽ��,����ɾ��")) {
					order.setFilter(oldfilter);
					order.filter();
					return;
				} 
				if(!deleteSumRxOrder(order, i, billFlg)){
					return;
				}
			}
			// ��ȡ�����е����� med_apply��Ϣ�� APPLICATION_NO
			MedApply med = odo.getOpdOrder().getMedApply();
			Map appNo = new HashMap();
			for (int i = 0; i < med.rowCount(); i++) {
				String key = med.getItemString(i, "ORDER_NO")
						+ med.getItemString(i, "SEQ_NO")
						+ med.getItemString(i, "CAT1_TYPE");
				appNo.put(key, med.getItemString(i, "APPLICATION_NO"));
			}
			for (int i = count - 1; i > -1; i--) {
				String tempCode = order.getItemString(i, "ORDER_CODE");
				if (StringUtil.isNullString(tempCode))
					continue;
				// ɾ��med_apply�Ķ�Ӧ��Ϣ
				// �ж��Ǽ�����ҽ�������� med_apply��¼�������� ɾ������
				if (("LIS".equals(order.getItemString(i, "ORDER_CAT1_CODE")) || "RIS"
						.equals(order.getItemString(i, "ORDER_CAT1_CODE")))
						&& "Y".equals(order.getItemString(i, "SETMAIN_FLG"))) {
					String labMapKey = order.getItemString(i, "RX_NO")
							+ order.getItemString(i, "SEQ_NO")
							+ order.getItemString(i, "CAT1_TYPE");
					med.deleteRowBy((String) appNo.get(labMapKey), order
							.getItemString(i, "RX_NO"), order.getItemInt(i,
							"SEQ_NO"), order.getItemString(i, "CAT1_TYPE"));
				}
				order.deleteRow(i);
			}
			order.setFilter(oldfilter);
			order.filter();
			table.setDSValue();
			String preCaseNo = selectCaseNoParm.getValue("CASE_NO",0);
			this.deleteReg(preCaseNo, rxNo);//Ԥ�����ɾ���Һ���ϢУ��
			break;
		}
		//�շѲ���
		if("Y".equals(billFlg.toString()))
			onFee();// ִ��ɾ��ҽ��
		else
			onTempSave();
		//onFee();
		//======yanjing 20140107 Ԥ����鵱ɾ���Ĵ���ǩ��ĳһ��Ԥִ��ʱ������һ������ǩʱɾ��reg_patadm�еĹҺ���Ϣ
//		order.setFilter("RX_NO='" + rxNo + "'");
//		order.filter();
		
		
	}
	/**
	 * ɾ������ǩʱɾ���Һ���Ϣ
	 * yanjing 20140108
	 */
	private void deleteReg(String preCaseNo,String rxNo){
		String[] data = odo.getOpdOrder().getRx(this.rxType);//yanjing 20131212 �����鴦��ǩ����
		boolean delFlg = true;
		for (int m = 0; m < data.length; m++) {
			if(!data[m].split(",")[0].equals(rxNo)){
			String opdOrderSql = "SELECT CASE_NO FROM OPD_ORDER WHERE RX_NO = '"+data[m].split(",")[0]+"'";
			TParm opdOrderParm = new TParm(TJDODBTool.getInstance().select(opdOrderSql));
			if(opdOrderParm.getCount()>0){//OPD_OEDER����û�и�case_no��ҽ��ɾ��reg_patadm�ĹҺ���Ϣ
				String nextCaseNo = opdOrderParm.getValue("CASE_NO", 0);
				if(preCaseNo.equals(nextCaseNo)){
					delFlg = false;
					break;
				 }
			   }
			 }
			}
		if(delFlg){
			String regSql = "SELECT MR_NO FROM REG_PATADM WHERE CASE_NO = '"+preCaseNo+"'";
			TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
			if(regParm.getCount()>0){//ɾ���Һ���Ϣ
				String delSql = "DELETE REG_PATADM WHERE CASE_NO = '"+preCaseNo+"'";
				TParm delRegParm = new TParm(TJDODBTool.getInstance().update(delSql));
		}
		}
	}
	/**
	 * ɾ�����Ŵ���ǩҽ���ܿ�
	 * 
	 * @return
	 */
	private boolean deleteSumRxOrder(OpdOrder order,int row,StringBuffer billFlg) {
		
		if(!billFlg.toString().equals(order.getItemData(row, "BILL_FLG").toString())){
			this.messageBox("�˴���ǩ��ҽ��״̬��ͬ,����ִ��ɾ��");
			return false;
		}
		return true;
	}

	/**
	 * combo��ѡ�¼�,���ݴ����ų�ʼ��table
	 * 
	 * @param rxType
	 *            ��������
	 */
	public void onChangeRx(String rxType) {
		if (StringUtil.isNullString(rxType)) {
			this.messageBox("E0035"); // ����ʧ��
			return;
		}
		TTabbedPane tabbedPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (!EXA.equalsIgnoreCase(rxType) && tabbedPane.getSelectedIndex() == 0) {
			return;
		}
		int type = StringTool.getInt(rxType);
		String rxNo;
		if (odo == null || odo.getOpdOrder() == null)
			return;
		switch (type) {
		// ��ҩ
		case 1:
			rxNo = this.getValueString(MED_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + MED + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblMed.setDSValue();
				calculateCash(TABLE_MED, "MED_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_MED, false))
				this.messageBox("E0036"); // ��ʾ��ҩʧ��
			this.setValue("MED_RBORDER_DEPT_CODE", phaCode);
			calculateCash(TABLE_MED, "MED_AMT");
			break;
		// ����ҩ
		case 2:

			rxNo = this.getValueString(CTRL_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + CTRL + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblCtrl.setDSValue();
				calculateCash(TABLE_CTRL, "CTRL_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_CTRL, false))
				this.messageBox("E0037"); // ��ʾ����ҩʧ��
			this.setValue("CTRL_RBORDER_DEPT_CODE", phaCode);
			calculateCash(TABLE_CTRL, "CTRL_AMT");
			break;
		// ��ҩ
		case 3:
			rxNo = this.getValueString(CHN_RX);
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			initChnTable(rxNo);
			calculateChnCash(rxNo);
			calculatePackageTot(rxNo);
			break;
		// ����
		case 4:
			rxNo = this.getValueString(OP_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + OP + "' AND ORDER_CODE <>''");
				odo.getOpdOrder().filter();
				tblOp.setDSValue();
				calculateCash(TABLE_OP, "OP_AMT");
				return;
			}
			if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}
			if (!initNoSetTable(rxNo, TABLE_OP, false))
				this.messageBox("E0038"); // ��ʾ����ʧ��
			calculateCash(TABLE_OP, "OP_AMT");
			break;
		// ������
		case 5:
			rxNo = this.getValueString(EXA_RX);
			if (StringUtil.isNullString(rxNo)) {
				odo.getOpdOrder().setFilter(
						"RX_TYPE='" + EXA + "' AND SETMAIN_FLG='Y'");
				odo.getOpdOrder().filter();
				tblExa.setDSValue();
				calculateCash(TABLE_EXA, "EXA_AMT");
				return;
			}
			odo.getOpdOrder().setFilter(
					"RX_NO='" + rxNo
							+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
			odo.getOpdOrder().filter();
			if (!StringUtil.isNullString(odo.getOpdOrder().getItemString(
					odo.getOpdOrder().rowCount() - 1, "ORDER_CODE"))) {
//				if (!initSetTable(TABLE_EXA, false))
//					this.messageBox("E0039"); // ��ʾ������ʧ��
				odo.getOpdOrder().newOrder(rxType, rxNo);
			}

			if (!initSetTable(TABLE_EXA, false))
				this.messageBox("E0039"); // ��ʾ������ʧ��

			break;
		}
	}

	/**
	 * ���㲢���ý��
	 * 
	 * @param tableName
	 *            String
	 * @param tag
	 *            String
	 */
	public void calculateCash(String tableName, String tag) {
		TTable table = (TTable) this.getComponent(tableName);

		double arAmt = 0.0;
		OpdOrder order = odo.getOpdOrder();
		int count = order.rowCount();
		if (order.rowCount() < 1)
			return;
		if (TABLE_EXA.equalsIgnoreCase(tableName)) {
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
			this.setValue(tag, arAmt);

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

		this.setValue(tag, arAmt);
	}

	/**
	 * ��ҩ�����ܽ��
	 * 
	 * @param rxNo
	 *            ������
	 */
	public void calculateChnCash(String rxNo) {
		OpdOrder order = odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int count = order.rowCount();
		double arAmt = 0.0;
		for (int i = 0; i < count; i++) {
			arAmt += order.getItemDouble(i, "AR_AMT");
		}
		this.setValue("CHN_AMT", arAmt);
	}

	/**
	 * ���㲢����OpdOrder������
	 * 
	 * @param rxNo
	 *            String
	 */
	public void calculatePackageTot(String rxNo) {
		OpdOrder order = odo.getOpdOrder();
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		long tot = 0;
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			tot += order.getItemDouble(i, "MEDI_QTY");
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			order.setItem(i, "PACKAGE_TOT", tot);
		}
		this.setValue("PACKAGE_TOT", tot);
	}

	/**
	 * �����ϵ�������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		//selectDiagRow = row;
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		//tempIcd = odo.getDiagrec().getItemString(row, "ICD_CODE");
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wc);
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popDiagReturn");
	}

	/**
	 * �����ϵ�������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedHistoryCreateEditComponent(Component com, int row,
			int column) {
		if (column != 2) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		String wcMed = odo.getMedHistory().getItemString(row, "ICD_TYPE");
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wcMed);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popMedHistoryReturn");
	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onMedCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		
		TTable table = (TTable) this.getComponent(TABLE_MED);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		// ����ҽ�����ɸ���
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_MED;
		rxName = MED_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 1);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onAllergyCreateEditComponent(Component com, int row, int column) {
		if (column != 2)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		if (TCM_Transform.getBoolean(this.getValue("ORDER_ALLERGY"))) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "allergyType");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

		} else if (TCM_Transform.getBoolean(this.getValue("INGDT_ALLERGY"))) {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "A");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("INGDT", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);

			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

		} else {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "C");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("OTHER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SysAllergy.x"), parm);

			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");

		}
	}

	/**
	 * ����ʷ���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popAllergyReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (StringUtil.isNullString(TABLEALLERGY)) {
			this.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		TTable table = (TTable) this.getComponent(TABLEALLERGY);
		table.acceptText();
		int row = table.getSelectedRow();
		String desc;
		String oldCode = odo.getDrugAllergy().getItemString(row,
				"DRUGORINGRD_CODE");
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (!StringUtil.isNullString(oldCode)) {
			this.messageBox("E0040"); // ������������ݣ�������������ɾ��������
			table.setDSValue(row);
			return;
		}
		if (StringTool.getBoolean(this.getValueString("ORDER_ALLERGY"))) {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ORDER_CODE"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "B";
		} else if (StringTool.getBoolean(this.getValueString("INGDT_ALLERGY"))) {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ID"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "A";
		} else {
			odo.getDrugAllergy().setItem(row, "DRUGORINGRD_CODE",
					parm.getValue("ID"));
			odo.getDrugAllergy().setItem(row, "DRUG_TYPE", allergyType);
			desc = "C";
		}
		odo.getDrugAllergy().setActive(table.getSelectedRow(), true);
		int newRow = 0;
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			newRow = odo.getDrugAllergy().insertRow();
			odo.getDrugAllergy().setItem(newRow, "DRUG_TYPE", desc);
		}
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(newRow);
		table.setSelectedColumn(2);
	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCtrlCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_CTRL);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_CTRL;
		rxName = CTRL_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 2);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOrderReturn");

	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onChnCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		if (column != 0 && column != 3 && column != 6 && column != 9) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		tableName = TABLE_CHN;
		rxName = CHN_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 3);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popChnOrderReturn");

	}

	/**
	 * ���SYS_FEE��������
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onOpCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_OP);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		tableName = TABLE_OP;
		rxName = OP_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 4);
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popOpReturn");

	}

	/**
	 * ���SYS_FEE��������(�����鴰��)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		//selectOrderRow = row;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		// �����ǰ�к�
		column = table.getColumnModel().getColumnIndex(column);
		String columnName = table.getParmMap(column);
		// ============xueyf modify 20120309 
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName))) {
			return;
		}
		int selRow = table.getSelectedRow();
		TParm existParm = table.getDataStore().getRowParm(selRow);
		if (this.isOrderSet(existParm)) {
			TTextField textFilter = (TTextField) com;
			textFilter.setEnabled(false);
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		tableName = TABLE_EXA;
		rxName = EXA_RX;
		TParm parm = new TParm();
		parm.setData("RX_TYPE", 5); // ������ CAT1_TYPE = LIS/RIS
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popExaReturn");
	}

	/**
	 * �������
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(TABLEDIAGNOSIS);
		tableDiag.acceptText();
		Diagrec diagRec = odo.getDiagrec();
		int rowNo = tableDiag.getSelectedRow();
		String icdTemp = parm.getValue("ICD_CODE");
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			tableDiag.setDSValue(rowNo);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		tableDiag.acceptText();
		if (diagRec.isHaveSameDiag(icdTemp)) {
			diagRec.deleteRow(rowNo);
			tableDiag.acceptText();
			tableDiag.getTable().grabFocus();
			tableDiag.setSelectedRow(0);
			tableDiag.setSelectedColumn(1);
			tableDiag.addRow();
			tableDiag.setDSValue();
			this.messageBox("E0041"); // ������ѿ���
			return;
		}
		if (!OdoUtil.isAllowDiag(parm, Operator.getDept(), pat.getSexCode(),
				pat.getBirthday(), (Timestamp) this.getValue("ADM_DATE"))) {
			this.messageBox("E0042"); // ��ϲ������ڸò��ˣ������¿���
			diagRec.deleteRow(rowNo);
			tableDiag.acceptText();
			tableDiag.getTable().grabFocus();
			tableDiag.setSelectedRow(0);
			tableDiag.setSelectedColumn(1);
			tableDiag.addRow();
			tableDiag.setDSValue();
			return;
		}

		boolean isHavingMain = diagRec.haveMainDiag(new int[1]);
		if (!isHavingMain) {
			if ("C".equalsIgnoreCase(wc) && !OdoUtil.isAllowChnDiag(parm)) {
				this.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ���
				diagRec.deleteRow(rowNo);
				// odo.getDiagrec().insertRow();
				tableDiag.acceptText();
				tableDiag.getTable().grabFocus();
				tableDiag.setSelectedRow(0);
				tableDiag.setSelectedColumn(1);
				tableDiag.addRow();
				tableDiag.setDSValue();
				return;
			}
			String mainDiagFlg = diagRec.isMainFlg(parm.getValue("ICD_CODE")) ? "Y"
					: "N";

			diagRec.setItem(rowNo, "MAIN_DIAG_FLG", mainDiagFlg);
			//mainFlg = true;
		} else {
			diagRec.setItem(rowNo, "MAIN_DIAG_FLG", "N");
			// mainFlg=false;
		}
		diagRec.setActive(rowNo, true);
		diagRec.setItem(rowNo, "ICD_TYPE", wc);
		if (parm.getValue("DIAG_NOTE").length() > 0) { // ���״��ر�ע�ֶ����� ���ֵ��еĲ�ͬ
			// ��Ҫ�ж�һ���ǲ������׻ش���
			diagRec.setItem(rowNo, "DIAG_NOTE", parm.getValue("DIAG_NOTE"));
		} else {
			diagRec.setItem(rowNo, "DIAG_NOTE", parm.getValue("DESCRIPTION"));
		}
		diagRec.setItem(rowNo, "ORDER_DATE", diagRec.getDBTime());
		String fileNo = parm.getValue("MR_CODE");
		if (!StringUtil.isNullString(fileNo)) {
			diagRec.setItem(rowNo, "MR_CODE", fileNo);
		}
		tableDiag.setItem(rowNo, "ICD_CODE", parm.getValue("ICD_CODE"));
		tableDiag.setDSValue();
		if (rowNo == tableDiag.getRowCount() - 1) {
			rowNo = tableDiag.addRow();
		}
		tableDiag.getTable().grabFocus();
		tableDiag.setSelectedRow(rowNo);
		tableDiag.setSelectedColumn(1);
	}

	/**
	 * �����Ӣ����ʾ
	 */
	public void onChnEng() {
		TCheckBox chnEng = (TCheckBox) this.getComponent("CHN_DSNAME");
		TTable tableDiag = (TTable) this.getComponent("TABLEDIAGNOSIS");
		if (chnEng.isSelected()) {
			chnEng.setText("Chinese version");
			// ============xueyf modify 20120220 
			tableDiag.setHeader("Main,30,boolean;Code,150;Notes,130;Order Dr,100,DR_CODE;Order Time,120,timestamp,yyyy/MM/dd HH:mm");
			tableDiag.setParmMap("MAIN_DIAG_FLG;ICD_ENG_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE");
			tableDiag.setDSValue();
		} else {
			chnEng.setText("Ӣ�Ĳ���");
			// ============xueyf modify 20120220 
			tableDiag.setHeader("��,30,boolean;����,150;��ע,130;����ҽ��,100,DR_CODE;����ʱ��,120,timestamp,yyyy/MM/dd HH:mm");
			tableDiag.setParmMap("MAIN_DIAG_FLG;ICD_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE");
			tableDiag.setDSValue();
		}
	}

	/**
	 * ��Ʒ������
	 * 
	 * @param tag
	 *            String
	 * @param checkBox
	 *            String
	 */
	public void onGoods(String tag, String checkBox) {
		TCheckBox tcb = (TCheckBox) this.getComponent(checkBox);
		TTable table = (TTable) this.getComponent(tag);
		// ORDER_ENG_DESC
		if (tcb.isSelected()) {
			if (TABLE_OP.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
				table.setDSValue();
			}
			if (TABLE_MED.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
			if (TABLE_CTRL.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_ENG_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
		} else {
			// ORDER_DESC
			if (TABLE_OP.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;NS_EXEC_DATE");
				table.setDSValue();
			}
			if (TABLE_MED.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
			if (TABLE_CTRL.equalsIgnoreCase(tag)) {
				table.setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC_SPECIFICATION;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;OWN_PRICE;DISPENSE_QTY;RELEASE_FLG;GIVEBOX_FLG;DISPENSE_UNIT;OWN_AMT;EXEC_DEPT_CODE;DR_NOTE;PAYAMOUNT;AR_AMT;NS_NOTE;URGENT_FLG;INSPAY_TYPE;BILL_DATE;PHA_DOSAGE_DATE");
				table.setDSValue();
			}
		}
	}

	/**
	 * ��������ʷ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popMedHistoryReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableMedHistory = (TTable) this.getComponent(TABLEMEDHISTORY);
		tableMedHistory.acceptText();
		int rowNo = tableMedHistory.getSelectedRow();
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			tableMedHistory.setDSValue(rowNo);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (odo.getMedHistory().isSameICD(parm.getValue("ICD_CODE"))) {
			this.messageBox("E0043"); // ���������ظ����
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		String oldCode = odo.getMedHistory().getItemString(rowNo, "ICD_CODE");
		if (!StringUtil.isNullString(oldCode)) {
			this.messageBox("E0040"); // ������������ݣ�������������ɾ��������`
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		odo.getMedHistory().setActive(rowNo, true);

		odo.getMedHistory().setItem(rowNo, "ICD_CODE",
				parm.getValue("ICD_CODE"));
		odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
				parm.getValue("ICD_TYPE"));
		odo.getMedHistory().setItem(rowNo, "SEQ_NO",
				odo.getMedHistory().getMaxSEQ(odo.getMrNo()));
		if (rowNo == tableMedHistory.getRowCount() - 1)
			odo.getMedHistory().insertRow();
		tableMedHistory.setDSValue();
		tableMedHistory.getTable().grabFocus();
		tableMedHistory.setSelectedRow(odo.getMedHistory().rowCount() - 1);
		tableMedHistory.setSelectedColumn(2);
	}

	/**
	 * �ж��Ƿ��ǹ���ʷ�е�ҩƷ
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	public boolean isAllergy(String orderCode) {
		for (int i = 0; i < odo.getDrugAllergy().rowCount(); i++) {
			if (orderCode.equalsIgnoreCase(odo.getDrugAllergy().getItemString(
					i, "DRUGORINGRD_CODE"))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �������͸ı�
	 * 
	 * @param type
	 *            String
	 */
	public void onAllg(String type) {
		TTable table = (TTable) this.getComponent(TABLEALLERGY);
		table.acceptText();
		if (TCM_Transform.getBoolean(this.getValue("OTHER_ALLERGY"))) {
			allergyType = "C";
			table.setFilter(" DRUG_TYPE='C' ");
			table.filter();
			table.setDSValue();
		} else if (TCM_Transform.getBoolean(this.getValue("INGDT_ALLERGY"))) {
			allergyType = "A";
			table.setFilter(" DRUG_TYPE='A' ");
			table.filter();
			table.setDSValue();
		} else {
			allergyType = "B";
			table.setFilter(" DRUG_TYPE='B' ");
			table.filter();
			table.setDSValue();
		}
	}

	/**
	 * ����������
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable table = (TTable) this.getComponent(TABLE_EXA);

		int column = table.getSelectedColumn();
		table.acceptText();

		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		if ("N".equalsIgnoreCase(parm.getValue("ORDERSET_FLG"))) {
			this.messageBox("E0044"); // ��ҽ�����Ǽ���ҽ������
			return;
		}

		OpdOrder order = (OpdOrder) table.getDataStore();
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "�Ѿ���Ʊ�Ĵ���ǩ���������ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
				return;

			}
		}
		// ============pangben 2012-2-29 stop
		int row = order.rowCount() - 1;
		String orderCode = order.getItemString(row, "ORDER_CODE");
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (orderCode != null && orderCode.trim().length() > 0) {
			this.messageBox("E0045"); // �ѿ���ҽ�����ɱ������ɾ����ҽ�����¿���
			table.setDSValue(row);
			return;
		}
//		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
//			if (this.messageBox(
//							"��ʾ��Ϣ/Tip",
//							"��ҽ���Ѿ��������Ƿ������\r\n/This order exists,Do you proceed it again?",
//							0) == 1) {
//				table.setDSValue(row);
//				return;
//			}
//
//		}
		insertExa(parm, row, column);
	}

	/**
	 * ����������
	 * 
	 * @param parm
	 *            SysFee
	 * @param row
	 *            TABLE ѡ����
	 * @param column
	 *            TABLE ѡ����
	 */
	private void insertExa(TParm parm, int row, int column) {
		int oldRow = row;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		OpdOrder order = (OpdOrder) table.getDataStore();
		// add by yanj 2013/07/17 �ż�������У��
		// ����
		if ("O".equalsIgnoreCase(admType)) {
			// �ж��Ƿ�סԺ����ҽ��
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// ������������ҽ����
				this.messageBox("�����������ü����顣");
				return;
			}
		}
		// ����
		if ("E".equalsIgnoreCase(admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// ������������ҽ����
				this.messageBox("���Ǽ������ü����顣");
				return;
			}
		}
		// $$===========add by yanj 2013/07/17 �ż�������У��
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox(
							"��ʾ��Ϣ/Tip",
							"��ҽ���Ѿ��������Ƿ������\r\n/This order exists,Do you proceed it again?",
							0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		String[] rxNos = order.getRx(EXA);
		if (rxNos == null || rxNos.length < 1) {
			return;
		}

		String rxNo = this.getValueString(this.EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0029"); // û�д���ǩ
			return;
		}

		int groupNo = order.getMaxGroupNo();

		String execDept = parm.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		// Date d1=new Date();
		initOrder(order, row, parm, null);
		String orderCode = parm.getValue("ORDER_CODE");
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		order.setItem(row, "DR_NOTE", parm.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odo);
		if (StringUtil.isNullString(labNo)) {
			this.messageBox("E0049"); // ȡ�ü����ʧ��
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(parm.getValue("ORDER_CODE"));
		// System.out.println("parmDetail==="+parmDetail);
		if (parmDetail.getErrCode() != 0) {
			System.out.println(parmDetail.getErrText());
		}
		if (parmDetail.getErrCode() != 0) {
			this.messageBox("E0050"); // ȡ��ϸ�����ݴ���
			return;
		}
		rxType = "5";
		int count = parmDetail.getCount();
		for (int i = 0; i < count; i++) {
			row = order.newOrder(rxType, rxNo);
			initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			// zhangyong20110616
			order.setItem(row, "COST_CENTER_CODE", getCostCenter(execDept));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double qty = TypeTool.getDouble(parmDetail.getData("TOTQTY", i));
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
							i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));
			order.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(ctz[0],
					ctz[1], ctz[2], order.getItemString(row, "ORDER_CODE"),
					order.getItemDouble(row, "DOSAGE_QTY"), serviceLevel)));
			order.setItem(row, "IS_PRE_ORDER", "N");//caowl 20131118
			order.setItem(row, "PRE_DATE", "");//caowl 20131118
			order.setActive(row, true);
			table.setLockCell(row-1, "PRE_DATE", true);//yanjing 20140121
		}

		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		initSetTable(TABLE_EXA, true);
		order.itemNow = false;
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(table.getColumnIndex("EXEC_DEPT_CODE"));
//		tblExa.setLockCell(row-1, "PRE_DATE", true);//yanjing 20140121
		table.setLockCell(row-1, "PRE_DATE", true);//yanjing 20140121
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
	}
	public boolean oPdSaveCountCheck() {
//		}
		return false;
	}

	/**
	 * ����������ҩ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOrderReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		// ============xueyf modify 20120331
		if (parm.getValue("CAT1_TYPE") != null
				&& !parm.getValue("CAT1_TYPE").equals("PHA")) {
			this.messageBox("��������������ҩҽ����");
			return;
		}
		// add by lx 2012/05/07 �ż�������У��
		// ����
		if ("O".equalsIgnoreCase(admType)) {
			// �ж��Ƿ�סԺ����ҽ��
			if (!("Y".equals(parm.getValue("OPD_FIT_FLG")))) {
				// ������������ҽ����
				this.messageBox("������������ҽ����");
				return;
			}
		}
		// ����
		if ("E".equalsIgnoreCase(admType)) {
			if (!("Y".equals(parm.getValue("EMG_FIT_FLG")))) {
				// ������������ҽ����
				this.messageBox("���Ǽ�������ҽ����");
				return;
			}
		}
		// $$===========add by lx 2012/05/07 �ż�������У��
		String rxNo;
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		int column = table.getSelectedColumn();
		table.acceptText();
		OpdOrder order = odo.getOpdOrder();
		// ===========pangben 2012-6-15 start ע�� ȡ��5��ҽ����ʾ��Ϣ
//		 if (oPdCountCheck()) {
//		 return;
//		 }
		int row = order.rowCount() - 1;
		int oldRow = row;
		String amtTag;
		if (TABLE_MED.equalsIgnoreCase(tableName)) {
			rxType = MED;
			amtTag = "MED_AMT";
		} else if (TABLE_CTRL.equalsIgnoreCase(tableName)) {
			rxType = CTRL;
			amtTag = "CTRL_AMT";
		} else {
			rxType = OP;
			amtTag = "OP_AMT";
		}
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
			this.messageBox("E0040"); // ������������ݣ�������������ɾ��������

			table.setDSValue(row);
			return;
		}
		if (!OdoUtil.isHavingLiciense(parm.getValue("ORDER_CODE"), Operator
				.getID())) {
			this.messageBox("E0051"); // û��֤��Ȩ��
			table.setValueAt("", row, column);
			return;
		}
		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("��ʾ��Ϣ/Tip",
					"��ҽ���Ѿ��������Ƿ������\r\nThis order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt("", oldRow, column);
				return;
			}
		}
		if (!isAllergy(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox(
							"��ʾ��Ϣ/Tip",
							"���˶Ը�ҩƷ��ɷֹ������Ƿ����������\r\nThe Pat is allergic to this order.Do you proceed anyway?",
							0) == 1) {
				table.setDSValue(row);
				return;
			}
		}
		//���ҽ������У��
		//yanjing ���ҽ���Է�У�� 20130719
		TParm insCheckParm=INSTJTool.getInstance().orderCheck(parm.getValue("ORDER_CODE"), reg.getCtz1Code(),admType,reg.getInsPatType());
		if (insCheckParm.getErrCode() < 0&&!(insCheckParm.getErrCode()==-6) &&!(insCheckParm.getErrCode()==-7)) {
			if(this.messageBox("��ʾ","��ҩƷ" + insCheckParm.getErrText()+",�Ƿ����",0)== 1){
				table.setValueAt("", row, column);
				return;
			}
			}else if(insCheckParm.getErrCode()==-6||insCheckParm.getErrCode()==-7){//-6:ҽ���Էѱ�� -7��ҽ���Է��������
				this.messageBox("��ҩƷΪ"+insCheckParm.getErrText());		
		}
		if (!OP.equalsIgnoreCase(rxType)
				&& !order.isDrug(parm.getValue("ORDER_CODE"), rxType)) {
			this.messageBox("E0113");
			return;
		}
		if (CTRL.equalsIgnoreCase(order.getItemString(row, "RX_TYPE"))) {
			if (!order.isSameDrug(parm.getValue("ORDER_CODE"), row)) {
				this.messageBox("E0114");
				return;
			}
		}
		// �ж�Ҫ���Ƿ��ܿ����ô���ǩ�ϣ��Ƿ���ר�ô���ҩƷ��
		int re = order.isPrnRx(order.getItemString(row, "RX_NO"), parm
				.getValue("ORDER_CODE"));
		if (re == 1) {
			this.messageBox("E0190");
			return;
		} else if (re == 2) {
			this.messageBox("E0191");
			return;
		} else if (re == 3) {
			this.messageBox("E0192");
			return;
		}
		rxNo = (String) this.getValue(rxName);
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			this.messageBox("E0034");
			return;
		}
		if (!checkDOSE_TYPE(order, parmBase.getValue("DOSE_TYPE", 0))) {
			return;
		}
		String execDept = parm.getValue("EXEC_DEPT_CODE");
		int tabbedIndex = ((TTabbedPane) this.getComponent("TTABPANELORDER"))
				.getSelectedIndex();
		switch (tabbedIndex) {
		case 1:
			if (StringUtil.isNullString(execDept)) {
				execDept = this.getValueString("OP_EXEC_DEPT");
			}
			break;
		case 2:
			execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
			break;
		case 3:
			execDept = this.getValueString("CHN_EXEC_DEPT_CODE");
			break;
		case 4:
			execDept = this.getValueString("CTRL_RBORDER_DEPT_CODE");
			break;
		}

		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
				if (checkPha(order, row, "MED",true))//����ҽ��У�鴦��ǩ�Ƿ���Կ���
					return ;
				//double dosageQty =OrderTool.getInstance().getDosAgeQty(parmBase, parm);
				//�������湦��==pangben 2013-11-6
//				if (isCheckKC(parm.getValue("ORDER_CODE"))) { // �ж��Ƿ��ǡ�ҩƷ��ע��
//					if (Operator.getLockFlg().equals("Y")) {
//						if (!INDTool.getInstance().inspectIndStock(caseNo,
//								parm.getValue("ORDER_CODE"), execDept,
//								0.00, true)) {
//							this.messageBox("E0052");// ��治��
//							order.setActive(row, false);
//							return;
//						}
//					} else {// ������
//						if (!checkPhaQty(parm, execDept, 0.00, order, row,true)) {
//							return ;
//						}
//					}
//				}
			}else{
				if (!checkSpcPha(order)) {
					return;
				}
			}
			//this.checkDrugCanUpdate(order, "MED", oldRow);
		}
		order.setActive(row, true);
		parm.setData("EXEC_DEPT_CODE", execDept);
		// ���ݸ���������ʼ��һ��order
//		System.out.println("parmBase is :"+parmBase);
		initOrder(order, row, parm, parmBase);
		if ("TRT".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))
				|| "PLN".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			order.setItem(row, "FREQ_CODE", "STAT");
		}
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			int newRow = order.newOrder(rxType, rxNo);
			if (order.getItemInt(row, "LINK_NO") > 0) {
				// //����ҽ�� �鿴�Ƿ��� ��ҽ����ע�� ����� �򲻼�������
				order.itemNow = true;
				order.setItem(newRow, "LINK_NO", order.getItemInt(row,
						"LINK_NO"));
				TParm linkMainParm = order.getLinkMainParm(order.getItemInt(
						row, "LINK_NO"));
				order.itemNow = false;
				order.setItem(row, "TAKE_DAYS", linkMainParm
						.getData("TAKE_DAYS"));
				order.itemNow = false;
				order.setItem(row, "FREQ_CODE", linkMainParm
						.getData("FREQ_CODE"));
				order.setItem(row, "EXEC_DEPT_CODE", linkMainParm
						.getData("EXEC_DEPT_CODE"));
				order.setItem(row, "ROUTE_CODE", linkMainParm
						.getData("ROUTE_CODE"));
				order.itemNow = true;
			}
		}
		initNoSetTable(rxNo, tableName, false);
		this.calculateCash(tableName, amtTag);
		table.getTable().grabFocus();
		table.setSelectedRow(oldRow);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
	}
	/**
	 * ������ҽ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popChnOrderReturn(String tag, Object obj) {
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = (TParm) obj;

		String rxNo;
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034");
			return;
		}
		if (StringUtil.isNullString(this.getValueString("CHN_EXEC_DEPT_CODE"))) {
			this.messageBox("E0053");
			TTextFormat t = (TTextFormat) this
					.getComponent("CHN_EXEC_DEPT_CODE");
			t.grabFocus();
			return;
		}
		if (StringUtil.isNullString(this.getValueString("CHN_FREQ_CODE"))) {
			this.messageBox("E0054");
			return;
		}
		OpdOrder order = odo.getOpdOrder();
//			//������
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
//			if (isCheckKC(parm.getValue("ORDER_CODE"))){ // �ж��Ƿ��ǡ�ҩƷ��ע��
//				TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
//						parm.getValue("ORDER_CODE"));
//				double dosageQty = OrderTool.getInstance().getDosAgeQty(
//						parmBase, parm);
//				// �������湦��==pangben 2013-11-6
//				if (Operator.getLockFlg().equals("Y")) {
//					if (!INDTool.getInstance().inspectIndStock(caseNo,"","",parm.getValue("ORDER_CODE"),
//							this.getValueString("CHN_EXEC_DEPT_CODE"),
//							dosageQty, true)) {
//						this.messageBox("E0052");// ��治��
//						return;
//					}
//				} else {// ������
//					if (!checkPhaQty(parm, this
//							.getValueString("CHN_EXEC_DEPT_CODE"), dosageQty,order, 0, false)) {
//						return;
//					}
//				}
//			}
		}
		
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "�Ѿ���Ʊ�Ĵ���ǩ���������ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
				return;

			}
		}
		// ============pangben 2012-2-29 stop
		int row = table.getSelectedRow();
		// int row = order.rowCount() / 4 - 1;
		int oldRow = row;
		int column = table.getSelectedColumn();
		String code = (String) table.getValueAt(row, column);

		if (order.isSameOrder(parm.getValue("ORDER_CODE"))) {
			if (this.messageBox("��ʾ��Ϣ/Tip",
					"��ҽ���Ѿ��������Ƿ������\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt(code, oldRow, column);
				return;
			}

		}
		table.acceptText();
		int realColumn = 0;
		switch (column) {
		case 0:
			realColumn = 0;
			break;
		case 3:
			realColumn = 1;
			break;
		case 6:
			realColumn = 2;
			break;
		case 9:
			realColumn = 3;
			break;
		}
		int realrow = row * 4 + realColumn;
		rxNo = (String) this.getValue(rxName);
		// System.out.println("-111111---"+order.getItemString(realrow,
		// "ORDER_CODE"));
		if (!StringUtil
				.isNullString(order.getItemString(realrow, "ORDER_CODE"))) {
			this.messageBox("E0040");
			return;
		}
		order.setItem(realrow, "PHA_TYPE", "G");
		TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
				parm.getValue("ORDER_CODE"));
		if (parmBase.getErrCode() < 0) {
			this.messageBox("E0034");
			return;
		}
		// System.out.println("realrow----"+realrow);
		order.setActive(realrow, true);
		order.setItem(realrow, "EXEC_DEPT_CODE", this
				.getValue("CHN_EXEC_DEPT_CODE"));
		parm.setData("EXEC_DEPT_CODE", this.getValue("CHN_EXEC_DEPT_CODE"));
		initOrder(order, realrow, parm, parmBase);
		setChnPckTot();
		order.setItem(realrow, "DCT_TAKE_QTY", this
				.getValueDouble("DCT_TAKE_QTY"));
		order.setItem(realrow, "TAKE_DAYS", this
				.getValueDouble("DCT_TAKE_DAYS"));
		order.setItem(realrow, "FREQ_CODE", this.getValue("CHN_FREQ_CODE"));
		order.setItem(realrow, "ROUTE_CODE", this.getValue("CHN_ROUTE_CODE"));
		order.setItem(realrow, "DCTAGENT_CODE", this.getValue("DCTAGENT_CODE"));

		order.setItem(realrow, "DR_NOTE", this.getValue("DR_NOTE"));
		calculateChnCash(rxNo);
		table.setValueAt(parm.getValue("ORDER_DESC"), table.getSelectedRow(),
				table.getSelectedColumn());
		table.setValueAt(order.getItemDouble(realrow, "MEDI_QTY"), row,
				column + 1);

		table.getTable().grabFocus();
		if (column == 9) {
			String oldFilter = order.getFilter();
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			String laseOrderCode = order.getItemString(order.rowCount() - 1,
					"ORDER_CODE");
			order.setFilter(oldFilter);
			order.filter();
			if (order.getItemString(realrow, "ORDER_CODE").length() > 0
					&& laseOrderCode.length() > 0)
				addChnRow(rxNo, row);
		}
		initChnTable(rxNo);
		table.setSelectedRow(row);
		table.setSelectedColumn(column + 1);
	}

	/**
	 * Ϊ��ҩTABLE����һ��
	 * 
	 * @param rxNo
	 *            ������
	 * @param row
	 *            �к�
	 */
	public void addChnRow(String rxNo, int row) {
		if (StringUtil.isNullString(rxNo))
			return;
		OpdOrder order = odo.getOpdOrder();
		int realrow = row * 4;
		for (int i = realrow; i < realrow + 4; i++) {
			order.newOrder(CHN, rxNo);
			order.setItem(i, "PHA_TYPE", "G");
		}
		// initChnTable(rxNo);
	}

	/**
	 * ��������
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popOpReturn(String tag, Object obj) {
		TParm sysFee = (TParm) obj;
		String rxNo;
		if (StringUtil.isNullString(tableName)) {
			this.messageBox("E0034");
			return;
		}
		TTable table = (TTable) this.getComponent(tableName);

		int row = table.getSelectedRow();
		int oldRow = row;
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!canEdit()) {
			table.setDSValue(row);
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE") != null
				&& odo.getOpdOrder().getItemString(oldRow, "ORDER_CODE").trim()
						.length() > 0) {
			this.messageBox("E0045"); // �ѿ���ҽ�����ɱ������ɾ����ҽ�����¿���
			table.setDSValue(row);
			return;
		}
		int column = table.getSelectedColumn();
		String code = sysFee.getValue("ORDER_CODE");
		table.acceptText();
		rxNo = (String) this.getValue(rxName);
		rxType = "4";
		OpdOrder order = odo.getOpdOrder();
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "�Ѿ���Ʊ�Ĵ���ǩ���������ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
				return;

			}
		}
		// ============pangben 2012-2-29 stop
		if (order.isSameOrder(code)) {
			if (this.messageBox("��ʾ��Ϣ/Tip",
					"��ҽ���Ѿ��������Ƿ������\r\n/This order exist,Do you give it again?",
					0) == 1) {
				table.setValueAt("", oldRow, column);
				return;
			}

		}
		odo.getOpdOrder().newOpOrder(rxNo, code, ctz, row);
		// odo.getOpdOrder().showDebug();
		if (!odo.getOpdOrder().isNullOrder(rxType, rxNo)) {
			odo.getOpdOrder().newOrder(rxType, rxNo);
		}
		this.calculateCash(TABLE_OP, "OP_AMT");
		initNoSetTable(rxNo, tableName, false);
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(3);
		order.itemNow = false;
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		// table.setLockRows(lockRows(tableName,false));
	}

	/**
	 * ���ݸ���������ʼ��һ��order
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
	public void initOrder(OpdOrder order, int row, TParm parm, TParm parmBase) {
		order.itemNow = true;
		order.setItem(row, "FLG", "N");//=======pangben 2013-3-22 Ĭ��ѡ��
		order.setItem(row, "PRESRT_NO", row + 1);
		order.setItem(row, "REGION_CODE", Operator.getRegion());
		order.setItem(row, "RELEASE_FLG", "N");
		order.setItem(row, "LINKMAIN_FLG", "N");
		order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
		order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
		order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC")
				.replaceFirst("(" + parm.getValue("SPECIFICATION") + ")", ""));
		order.setItem(row, "TRADE_ENG_DESC", parm.getValue("TRADE_ENG_DESC"));
		order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
		order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
		order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
		order.setItem(row, "IS_PRE_ORDER", parm.getValue("IS_PRE_ORDER"));//caowl 20131117
		order.setItem(row, "PRE_DATE", parm.getValue("PRE_DATE"));//caowl 20131117
		//double xxQty = 1.0; // ��¼������ϸ������� Ĭ��Ϊ1
		// TOTQTY�Ǽ�����ϸ������� ���ϸ�������� ��ô��Ҫ����������������Ǯ
//		if (parm.getDouble("TOTQTY") > 0) {
//			xxQty = parm.getDouble("TOTQTY");
//		}
		if ("2".equals(serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE2"));
		} else if ("3".equals(serviceLevel)) {
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE3"));
		} else
			order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
		// order.setItem(row, "OWN_PRICE", parm.getData("OWN_PRICE"));
		order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
		String REXP_CODE = BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"),
				admType);
		// ============xueyf modify 20120419 start
		// ׷��REXP_CODEΪ�յ�������Ϣ
		if (StringUtil.isNullString(REXP_CODE)) {
			this.messageBox("��ҽ��REXP_CODEΪ�գ���֪ͨ��Ϣ���ġ�");
			System.err.println("��ҽ��REXP_CODEΪ�գ���֪ͨ��Ϣ���ġ�");
			System.err.println("CHARGE_HOSP_CODE="
					+ parm.getValue("CHARGE_HOSP_CODE"));
			System.err.println("admType=" + admType);
			TParm logParm = new TParm();
			logParm.setErr(-1, "CASE_NO="+caseNo+" ORDER_CODE="+parm.getValue("ORDER_CODE")+" HEXP_CODE="+parm.getValue("CHARGE_HOSP_CODE"));
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
		            "noRexpCodeLog", logParm);
		}
		order.setItem(row, "REXP_CODE", REXP_CODE);
		// ============xueyf modify 20120419 stop
		order.setItem(row, "SETMAIN_FLG", "N");
		order.setItem(row, "ORDERSET_GROUP_NO", 0);
		order.setItem(row, "CTZ1_CODE", ctz[0]);
		order.setItem(row, "CTZ2_CODE", ctz[1]);
		order.setItem(row, "CTZ3_CODE", ctz[2]);
		order.setItem(row, "MR_CODE", parm.getValue("MR_CODE"));
		order.itemNow = false; // �Ƿ���ü������������Ŀ���
		if (TypeTool.getDouble(parm.getData("MEDI_QTY")) > 0) {
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("MEDI_QTY")));
		} else {
			order.setItem(row, "MEDI_QTY", 1.0);
		}
		if (parm.getDouble("TOTQTY") > 0) {
			// ����ҽ����������� ҲҪ����Ĭ��ֵ�����������п��ܴ���1�����Ե���������һʱ ��������Ϊ������
			order.setItem(row, "MEDI_QTY", TypeTool.getDouble(parm
					.getData("TOTQTY")));
			order.itemNow = true; // ���뽫setItem�¼��Ŀ��عص���������������������������� �������Ϊ0
			order.setItem(row, "DISPENSE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
			order.itemNow = false; // ��������ʼֵ�� �򿪿��� ���Լ�������
			order.setItem(row, "DOSAGE_QTY", TypeTool.getDouble(parm
					.getDouble("TOTQTY")));
		} else {
			order.itemNow = true; // ���뽫setItem�¼��Ŀ��عص���������������������������� �������Ϊ0
			order.setItem(row, "DISPENSE_QTY", 1.0);
			order.itemNow = false; // ��������ʼֵ�� �򿪿��� ���Լ�������
			order.setItem(row, "DOSAGE_QTY", 1.0);
		}
		order.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
		order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
		// zhangyong20110616
		order.setItem(row, "COST_CENTER_CODE", getCostCenter(parm
				.getValue("EXEC_DEPT_CODE")));
		// �ж��Ƿ���ҩƷ
		if ("PHA".equalsIgnoreCase(parm.getValue("CAT1_TYPE"))) {
			String printNo = "";
			if (row <= 0) {
				printNo = order.getPrintNo(parm.getValue("EXEC_DEPT_CODE"));
			} else {
				printNo = order.getItemString(row - 1, "PRINT_NO");
			}
			if (StringUtil.isNullString(printNo)) {
				this.messageBox("E0112"); // ȡ����ҩ��ʧ��
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
				this.messageBox("E0112"); // ȡ����ҩ��ʧ��
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
			order.setItem(row, "MEDI_QTY",null!=parmBase.getValue("MEDI_QTY", 0)?parmBase.getValue("MEDI_QTY", 0):"");
			// System.out.println("��ҩ��λ��"+parmBase.getValue("MEDI_UNIT", 0));
			order.setItem(row, "MEDI_UNIT", null!=parmBase.getValue("MEDI_UNIT", 0)?parmBase.getValue("MEDI_UNIT", 0):"");
			order.setItem(row, "FREQ_CODE", parmBase.getValue("FREQ_CODE", 0));
			order.setItem(row, "ROUTE_CODE",  null!=parmBase.getValue("ROUTE_CODE",0)?parmBase.getValue("ROUTE_CODE",0):"");
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
			// �ж��Ƿ��ǰ�������ҩ
			if ("Y".equalsIgnoreCase(parmBase.getValue("DSPNSTOTDOSE_FLG", 0))) {
				order.setItem(row, "DISPENSE_QTY", parmBase.getValue(
						"DEFAULT_TOTQTY", 0));
			} else {
				// $$============add by lx 2012/03/03
				// ����Ĭ������start=================$$//
				double tMediQty = parmBase.getDouble("MEDI_QTY", 0);// ��ҩ����
				String tUnitCode = parmBase.getValue("MEDI_UNIT", 0);// ��ҩ��λ
				String tFreqCode = parmBase.getValue("FREQ_CODE", 0);// Ƶ��
				int tTakeDays = parmBase.getInt("TAKE_DAYS", 0);// ����
				TotQtyTool qty = TotQtyTool.getInstance();
				parm.setData("TAKE_DAYS", tTakeDays);
				parm.setData("MEDI_QTY", tMediQty);
				parm.setData("FREQ_CODE", tFreqCode);
				parm.setData("MEDI_UNIT", tUnitCode);
				parm.setData("ORDER_DATE", SystemTool.getInstance().getDate());
				TParm qtyParm = qty.getTotQty(parm);
				order.setItem(row, "DISPENSE_QTY", qtyParm.getDouble("QTY"));
				order.setItem(row, "DOSAGE_QTY", qtyParm.getDouble("QTY"));
				// $$============add by lx 2012/03/03 ����Ĭ������
				// end=================$$//
			}
			// �ж��Ƿ񰴺з�ҩ
			if ("Y".equalsIgnoreCase(parmBase.getValue("GIVEBOX_FLG", 0))) {
				order.setItem(row, "DOSAGE_UNIT", parmBase.getValue(
						"STOCK_UNIT", 0));
				order.setItem(row, "DISPENSE_UNIT", parmBase.getValue(
						"STOCK_UNIT", 0));
				order.itemNow=false;//pangben 2013-6-3  ��װҩ�����ܼƽ�� �������� itemNow=false���� true ������ 
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
		double ownAmt = roundAmt(order.getItemDouble(row, "OWN_PRICE")
				* order.getItemDouble(row, "DOSAGE_QTY"));
		String orderCode = parm.getValue("ORDER_CODE");
		// ===============begin===//
		// lzk 2010.6.23 �ϲ� BIL.chargeTotCTZ��BIL.getOwnRate
		double d[] = BILStrike.getInstance().chargeC(ctz[0], ctz[1], ctz[2],
				orderCode, parm.getValue("CHARGE_HOSP_CODE"), serviceLevel);
		double arAmt = roundAmt(d[0] * order.getItemDouble(row, "DOSAGE_QTY"));
		order.setItem(row, "DISCOUNT_RATE", d[1]);
		// ===============   chenxi modify  ��ӿ���ʱ��
		if (parmBase != null && parmBase.getCount("FREQ_CODE") > 0){
			order.setItem(row, "ORDER_DATE", SystemTool.getInstance().getDate()) ;
		}
		else  order.setItem(row, "ORDER_DATE", "") ;
		
		//==============  chenxi modify ��ӿ���ʱ��
		order.setItem(row, "OWN_AMT", ownAmt);
		order.setItem(row, "AR_AMT", arAmt);
		order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
		order.itemNow = false;
	}
	/**
	 * ɾ��һ�����ݲ����޸ı��ǰ״̬
	 * ===============pangben 2013-4-24
	 * @param table
	 */
	private void deleteExeTemp(TTable table, int type) {
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
	 * ɾ��һ������ִ�в���
	 * ===============pangben 2013-4-24
	 * @return
	 */
	private boolean deleteExe(TTable table){
		int row = table.getSelectedRow();
		if (TABLEDIAGNOSIS.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			Diagrec dRec = odo.getDiagrec();
			if (dRec.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			dRec.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (TABLEALLERGY.equalsIgnoreCase(tableName)) {
			if (row < 0) {
				return true;
			}
			DrugAllergy allergy = odo.getDrugAllergy();
			if (allergy.rowCount() - 1 <= row || row == -1) {
				deleteExeTemp(table, 2);
				return true;
			}
			allergy.deleteRow(row);
			deleteExeTemp(table,1);
			table.setSelectedColumn(1);
			return true;
		}else if (TABLEMEDHISTORY.equalsIgnoreCase(tableName)) {//����ʷTABLEɾ��
			if (row < 0) {
				return true;
			}
			MedHistory md = odo.getMedHistory();
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
	 * ɾ��һ�м�����
	 * ==========pangben 2013-4-24
	 */
	private boolean deleteRowExa(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
			return false;
		String rxNo = this.getValueString(EXA_RX);
		if (!deleteOrder(order, row, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
			return false;
		} 
		String orderSetCode = order.getItemString(row, "ORDERSET_CODE");//����ҽ������
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");//����ҽ�����
		String orderCode = order.getItemString(row, "ORDER_CODE");//����
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, order
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		//exaFLg = true;// ɾ����ʾ�����鵥
		this.calculateCash(tableName, "EXA_AMT");
		//����RX_NO��ѯcase_no ��is_pre_order
		String selCaseNo = "SELECT CASE_NO,IS_PRE_ORDER FROM OPD_ORDER WHERE RX_NO = '"+rxNo+"'";
		TParm selectCaseNoParm = new TParm(TJDODBTool.getInstance().select(selCaseNo));
		String isPreOrder = selectCaseNoParm.getValue("IS_PRE_ORDER",0);//Ԥ�������
		String preCaseNo = selectCaseNoParm.getValue("CASE_NO",0);;//�����
		//��ѯopd_order���е�����
		if(isPreOrder.equals("Y")){
		String slctOpdSql = "SELECT ORDER_CODE FROM OPD_ORDER WHERE " +
				"CASE_NO = '"+preCaseNo+"' AND IS_PRE_ORDER = 'Y' AND SETMAIN_FLG = 'Y'";
		TParm delOpdParm = new TParm(TJDODBTool.getInstance().select(slctOpdSql));
		if(delOpdParm.getCount()==1 && delOpdParm.getValue("ORDER_CODE", 0).equals(orderCode)){//ɾ��reg_patadm�е�����
			String delRegSql = "DELETE REG_PATADM WHERE CASE_NO = '"+preCaseNo+"'";
			TParm delRegParm = new TParm(TJDODBTool.getInstance().update(delRegSql));
		}
		}
		return true;
	}
	/**
	 * ɾ��һ����ҩҽ��
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowChn(OpdOrder order ,int row,TTable table){
		rxType = CHN;
		int column = table.getSelectedColumn();
		int realColumn = 0;
		if (column >= 0 && column <= 2) {
			realColumn = 0;
		} else if (column >= 3 && column <= 5) {
			realColumn = 1;
		} else if (column >= 6 && column <= 8) {
			realColumn = 2;
		} else {
			realColumn = 3;
		}
		String rxNo = this.getValueString(CHN_RX);

		int realRow = row * 4 + realColumn;
		if (!deleteOrder(order, row, "���շ�,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
			return false;
		} 
		TParm parm=new TParm();
		if (!order.checkDrugCanUpdate("CHN", realRow,parm,true)) { // �ж��Ƿ�����޸ģ���û�н�����,��,�����շ�
			this.messageBox(parm.getValue("MESSAGE"));
			return false;
		}
		if (realRow < order.rowCount()) {
			order.deleteRow(realRow);
		} else {
			return false;
		}
		this.initChnTable(rxNo);	
		return true;
	}
	/**
	 * ɾ��һ�д���ҽ��
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowOp(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
			return false;
		if (!deleteOrder(order, row, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
			return false;
		}
		String rxNo = this.getValueString("OP_RX");
		String orderSetCode = table.getItemString(row, "ORDERSET_CODE");
		int groupNo = table.getItemInt(row, "ORDERSET_GROUP_NO");
		String orderCode = table.getItemString(row, "ORDER_CODE");
		order.deleteOrderSet(rxNo, orderSetCode, groupNo, orderCode, table
				.getItemString(row, "SEQ_NO"));
		table.setDSValue();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		this.calculateCash(tableName, "OP_AMT");
		return true;
	}
	/**
	 * ɾ��һ������ҩҽ��
	 * ==========pangben 2013-4-24
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 */
	private boolean deleteRowMed(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
			return false;
//		if (!order.checkDrugCanUpdate("EXA", row)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
//			this.messageBox("E0189");
//			return false;
//		}
		table.acceptText();//====pangben 2013-1-6 ɾ����ҩ��꽹������������,��ӡ�Ĵ���ǩ�����ҽ����������ɾ����ҽ������
		if(!deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	/**
	 * ɾ��һ������ҩ�����ҩƷ����
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	private boolean deleteRowMedCtrlComm(OpdOrder order ,int row,TTable table){
		// �Ƿ����ɾ
		if (!deleteOrder(order, row, "�Ѵ�Ʊ,�������޸Ļ�ɾ��ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
			return false;
		} 
		int linktemp = order.getItemInt(row, "LINK_NO");
		if (linktemp > 0
				&& TCM_Transform.getBoolean(order.getItemData(row,
						"LINKMAIN_FLG"))) {
			for (int i = order.rowCount(); i > -1; i--) {
				if (linktemp == order.getItemInt(i, "LINK_NO")
						&& !StringUtil.isNullString(order.getItemString(i,
								"ORDER_CODE"))) {
					order.deleteRow(i);
				}
			}
		} else {
			order.deleteRow(row);
		}
		
		return true;
	}
	/**
	 * ɾ��һ������ҩ�����ҩƷ����,forѭ��ִ���Ժ����
	 * @param order
	 * @param row
	 * @param table
	 * @return
	 * =====pangben 2013-4-25
	 */
	private boolean deleteRowMedCtrlComm(TTable table,String rxNo,String rxType,OpdOrder order){
		if (table.getRowCount() - 1 < 0) {
			order.newOrder(rxType, rxNo);
			return false;
		}
		if (!StringUtil.isNullString(TCM_Transform.getString(table.getItemData(
				table.getRowCount() - 1, "ORDER_DESC")))) {
			order.newOrder(rxType, rxNo);
		}
		table.setDSValue();
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);

		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		table.setRowTextColorMap(ctrlcolor);
		return true;
	}
	/**
	 * ɾ��һ�й���ҩƷ
	 * @param order
	 * @param row
	 * @param table
	 * =====pangben 2013-4-24
	 */
	private boolean deleteRowCtrl(OpdOrder order ,int row,TTable table){
		if (!checkSendPah(order, row))// ����ѷ�ҩ�ѵ���
			return false;
//		if (!order.checkDrugCanUpdate("EXA", row)) { // �ж��Ƿ�����޸ģ���û�н�����,��,����
//			this.messageBox("E0189");
//			return false;
//		}
		if(!deleteRowMedCtrlComm(order, row, table)){
			return false;
		}
		return true;
	}
	/**
	 * ɾ��ָ������һ������
	 */
	public void deleteRow() {
		TTable table = (TTable) this.getComponent(tableName);
        if(deleteExe(table)){//=========pangben 2013-4-24 ɾ�������ֲ��š�����ʷ������ʷ
        	return ;
        }
		String tag ="";//״̬ѡ������������
		OpdOrder order = odo.getOpdOrder();
		boolean deleteRowFlg=false;//У���Ƿ�ɾ������
		String billFlg="";//�����ݴ滹���շ�
		String rxNo="";
		String rxType="";
		if(TABLE_CHN.equalsIgnoreCase(tableName)){//��ҩ
			int row = table.getSelectedRow();
			if (row < 0) {
				return;
			}
			billFlg=order.getItemData(row, "BILL_FLG").toString();
			if(!deleteRowChn(order, row, table))
				return;
			if("Y".equals(billFlg))
				onFee();// ִ��ɾ��ҽ��
			else
				onTempSave();
			return;		
		}
		for (int i = order.rowCount()-1; i>=0; i--) {
			if(null==order.getItemData(i, "ORDER_CODE"))
				continue;
			String orderCode = order.getItemData(i, "ORDER_CODE").toString();//ҽ����
			if(StringUtil.isNullString(orderCode))//У���Ƿ����
				continue;
			if(order.getItemData(i, "FLG").equals("Y")){//��ѡ״̬
				deleteRowFlg=true;
				if(billFlg.length()<=0||billFlg.equals("N"))//�жϲ����շѻ����ݴ�
					billFlg=order.getItemData(i, "BILL_FLG").toString();
				if (TABLE_EXA.equalsIgnoreCase(tableName)) {// �������
					if(!deleteRowExa(order, i, table)){
						return;
					}
					tag = "EXA_AMT";
				}
				if (TABLE_OP.equalsIgnoreCase(tableName)) {// ������Ŀ��
					if (!deleteRowOp(order, i, table)) {
						return;
					}
					tag = "OP_AMT";
				}
				if (TABLE_MED.equalsIgnoreCase(tableName)) {//����ҩ
					if (!deleteRowMed(order, i, table)) {
						return;
					}
					rxNo = this.getValueString("MED_RX");
					tag = "MED_AMT";
					rxType=MED;
				}
				if (TABLE_CTRL.equalsIgnoreCase(tableName)) {//����ҩƷ
					if (!deleteRowCtrl(order, i, table)) {
						return;
					}
					rxNo = this.getValueString("CTRL_RX");
					tag = "CTRL_AMT";
					rxType =CTRL;
				}
			}
		}
		if (!deleteRowFlg) {//û�в���������
			table.acceptText();
			table.setItem(0, 0, "");
			table.getTable().grabFocus();
			table.setSelectedRow(order.rowCount() - 1);
			table.setSelectedColumn(0);
			this.messageBox("��ѡ��Ҫɾ����ҽ��");
			return;
		}
		if(rxType.equals(MED)||rxType.equals(CTRL)){//ɾ��һ������ҩ�����ҩƷ����,forѭ��ִ���Ժ����
			if (!deleteRowMedCtrlComm(table, rxNo, rxType, order)) {
				return ;
			}
		}
		this.calculateCash(tableName, tag);
		if("Y".equals(billFlg)){
			onFee();// ִ��ɾ��ҽ��
		}else{
			onTempSave();
		}
	}
	/**
	 * У���ѷ�ҩƷ���ѵ���
	 * 
	 * @param order
	 * @param row
	 */
	private boolean checkSendPah(OpdOrder order, int row) {
//		 System.out.println("opb::::::"+opb);
//===zhangp �������޸� start
		if (!"PHA".equals(order.getItemData(row, "CAT1_TYPE"))
				&& "Y".equals(order.getItemData(row, "EXEC_FLG"))) {
			this.messageBox("�ѵ���,�����˷�!");
			return false;
		}
		TParm parm =new TParm();
		if (!Operator.getSpcFlg().equals("Y")) {//====pangben 2013-4-17 У��������ע��
			if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))
					&& !opb.checkDrugCanUpdate(order.getRowParm(row), row,parm,true)) {
				this.messageBox(parm.getValue("MESSAGE"));
				return false;
			}
		} else {
			if ("PHA".equals(order.getItemData(row, "CAT1_TYPE"))) {
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(row).getValue("RX_NO");
				String seqNo = order.getRowParm(row).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
		                "action.opb.OPBSPCAction",
		                "getPhaStateReturn", spcParm);
//				PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
//				SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
//				if(spcOpdOrderReturnDto == null){
//					return true;
//				}
				if(spcReturn.getErrCode()==-2){
					return true;
				}
				boolean needExamineFlg = false;
				// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
				if ("W".equals(order.getRowParm(row).getValue("PHA_TYPE"))
						|| "C".equals(order.getRowParm(row)
								.getValue("PHA_TYPE"))) {
					// �ж��Ƿ����
					needExamineFlg = PhaSysParmTool.getInstance().needExamine();
				}
				// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
				if (needExamineFlg) {
					// System.out.println("�����");
					// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
//					if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0
//							&& spcOpdOrderReturnDto.getPhaRetnCode().length() == 0) {
//						this.messageBox("ҩƷ�����,�����˷�!");
//						return false;
//					}
					if (spcReturn.getValue("PhaCheckCode").length() > 0
							&& spcReturn.getValue("PhaRetnCode").length() == 0) {
						this.messageBox("ҩƷ�����,�����˷�!");
						return false;
					}
				} else {// û��������� ֱ����ҩ
					// �ж��Ƿ�����ҩҩʦ
					// System.out.println("�����");
//					if (spcOpdOrderReturnDto.getPhaDosageCode().length() > 0
//							&& spcOpdOrderReturnDto.getPhaRetnCode().length() == 0) {
//						this.messageBox("ҩƷ�ѷ�ҩ,�����˷�!");
//						return false;// �Ѿ���ҩ���������޸�
//					}
					if (spcReturn.getValue("PhaDosageCode").length() > 0
							&& spcReturn.getValue("PhaRetnCode").length() == 0) {
						this.messageBox("ҩƷ�ѷ�ҩ,�����˷�!");
						return false;// �Ѿ���ҩ���������޸�
					}
				}
			}
			// ===zhangp �������޸� end
		}
		return true;
	}

	/**
	 * У���Ƿ����ɾ��ҽ�� ҽ�ƿ�ɾ��ҽ������ֱ��ɾ��,����Ѿ��ۿ��ҽ��ɾ�� ��ֱ��ִ�пۿ����
	 * 
	 * @param order
	 * @param row
	 * boolean flg true ɾ��һ��ҽ�� 
	 * @return
	 * =======pangben 2013-1-29 ��Ӳ��� У�������ҽ���Ƿ��Ѿ��Ǽ�
	 */
	private boolean deleteOrder(OpdOrder order, int row, String message,String medAppMessage) {
		// ҽ�ƿ���������ɾ��ҽ��====pangben 2011-12-16
		if (null == ektReadParm || null == ektReadParm.getValue("MR_NO")
				|| ektReadParm.getValue("MR_NO").length() <= 0) {
			if (!order.isRemovable(row, false)) {
				this.messageBox("�ѼƷ�,��ִ���˷Ѳ���"); // �ѼƷ�ҽ������ɾ��
				return false;
			} 
			return true;
		} else {
			// ���շ�ҽ��û������ϲ���ɾ��
			int rowMainDiag = odo.getDiagrec().getMainDiag();
			if (rowMainDiag < 0) {
				this.messageBox("�뿪�������");
				return false;
			}
			if (!ektDelete(order, row)) {// У���Ƿ����ɾ��ҽ��
				this.messageBox(message); // �ѼƷ�ҽ������ɾ��
				return false;
			}
			//=========pangben 2013-1-29
			if(!medAppyCheckDate(order, row)){
				this.messageBox(medAppMessage); // У�� �������Ѿ��Ǽǵ����ݲ���ɾ������
				return false;
			}
			boolean flg = false;
			if(EKTIO.getInstance().ektAyhSwitch()){
				flg = true;
			}else{
				flg = readEKT();
			}
			return flg;

		}
	}

	/**
	 * ҽ��ҳǩ����¼�
	 */
	public void onChangeOrderTab() {
		if (odo == null) {
			return;
		}
		if (odo.isModified()) {
			this.onTempSave();
		}
		getChangeOrderTab();
	}
	/**
	 * ���л�ҳǩ�����ֿ�
	 * -=======pangben 2013-5-15
	 */
	private void getChangeOrderTab() {
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		case 0:
			initExa();
			break;
		case 1:
			// ����
			initOp();
			this.calculateCash(TABLE_OP, "OP_AMT");
			break;
		case 2:
			// ��ҩ
			initMed();
			break;
		case 3:
			// ��ҩ
			initChnMed();
			this.calculateChnCash(this.getValueString("CHN_RX"));
			break;
		case 4:
			// ��ҩ
			initCtrl();
			this.calculateCash(TABLE_CTRL, "CTRL_AMT");
			break;
		}
		if (!StringUtil.isNullString(tableName)) {
			TTable table = (TTable) this.getComponent(tableName);
			table.acceptText();
		}
	}
	/**
	 * ���ҳǩ��ѡ���������м���ʷ�����ʷҳǩͷ�ͱ�ɫ
	 */
	public void onDiagPnChange() {
		if (odo == null)
			return;
		// if(odo.isModified()){
		// this.onTempSave();
		// }

		MedHistory medHistory = odo.getMedHistory();

		// Color orginal=new Color(255,255,255);
		TTabbedPane p = (TTabbedPane) this.getComponent("TTABPANELDIAG");
		if (medHistory.rowCount() > 1) {
			p.setTabColor(1, red);
		} else {
			p.setTabColor(1, null);
		}
		if (odo.getDrugAllergy().rowCount() > 1) {
			p.setTabColor(4, red);
		} else {
			p.setTabColor(4, null);
		}
	}

	/**
	 * xueyf 2012-02-28 ҽ�����ش�����ѯ
	 */
	public void onINSDrQuery() {
		String CASE_NO = odo.getCaseNo();
		if (StringUtil.isNullString(CASE_NO)) {
			this.messageBox("��ѡ�񲡻���");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("INS_TYPE", reg.getInsPatType());
		this.openDialog("%ROOT%\\config\\ins\\INSDrQueryList.x", parm);

	}

	/**
	 * ���ҳǩ˫���¼����ṹ������ѡ��ģ��
	 */
	public void onChangeTemplate() {
		TTabbedPane panel = (TTabbedPane) this.getComponent("TTABPANELDIAG");
		if (panel.getSelectedIndex() != 0) {
			return;
		}
		if (odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODO");
		parm.setData("ADM_TYPE", this.admType);
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		// zhangyong20110427 begin
		Object obj = this.openDialog("%ROOT%\\config\\opd\\ODOEmrTemplet.x",
				parm);
		// zhangyong20110427 end
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");

		word.onOpen(templetPath, templetName, 2, false);

		if (!"N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
		}
		word.setCanEdit(true);
	}

	/**
	 * �����б�չ���¼�
	 */
	public void onPat() {
		// �жϿ���Ȩ��
		if (this.getPopedem("DEPT_POPEDEM"))
			onSelectPat("INSTEAD_DEPT");
		else
			onSelectPat("");
		mp.onDoubleClicked(false);
	}

	/**
	 * ���1����¼��������2�����3���
	 */
	public void onCtz1() {
		TComboBox t = (TComboBox) this.getComponent("CTZ2_CODE");
		t.setValue("");
		t = (TComboBox) this.getComponent("CTZ3_CODE");
		t.setValue("");
	}

	/**
	 * ���2����¼��������1�����3�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onCtz2() {

		TComboBox t = (TComboBox) this.getComponent("CTZ1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("CTZ2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0057");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("CTZ3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0058");
			t2.setValue("");
			return;
		}
	}

	/**
	 * ���3����¼��������1�����2�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onCtz3() {
		TComboBox t = (TComboBox) this.getComponent("CTZ1_CODE");
		TComboBox t3 = (TComboBox) this.getComponent("CTZ3_CODE");
		if (StringUtil.isNullString(t3.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t3.setValue("");
			return;
		}
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0057");
			t3.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("CTZ2_CODE");
		if (t3.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0059");
			t3.setValue("");
			return;
		}
	}

	/**
	 * ������1����¼�����ղ�����2��3
	 */
	public void onPat1() {
		TComboBox t = (TComboBox) this.getComponent("PAT2_CODE");
		t.setValue("");
		t = (TComboBox) this.getComponent("PAT3_CODE");
		t.setValue("");
	}

	/**
	 * ������2����¼����벡����1��3�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onPat2() {

		TComboBox t = (TComboBox) this.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("PAT2_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0060");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("PAT3_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0061");
			t2.setValue("");
			return;
		}

	}

	/**
	 * ������3����¼����벡����1��2�Ƚϣ����ܺ����ǵ�ֵ��ͬ
	 */
	public void onPat3() {
		TComboBox t = (TComboBox) this.getComponent("PAT1_CODE");
		TComboBox t2 = (TComboBox) this.getComponent("PAT3_CODE");
		if (StringUtil.isNullString(t2.getValue()))
			return;
		if (StringUtil.isNullString(t.getValue())) {
			t2.setValue("");
			return;
		}
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0060");
			t2.setValue("");
			return;
		}
		t = (TComboBox) this.getComponent("PAT2_CODE");
		if (t2.getValue().equalsIgnoreCase(t.getValue())) {
			this.messageBox("E0062");
			t2.setValue("");
			return;
		}

	}

	/**
	 * LMP����¼������㻳������
	 */
	public void onLmp() {

		Timestamp LMP = (Timestamp) this.getValue("LMP_DATE");
		int week = OdoUtil
				.getPreWeek(TJDODBTool.getInstance().getDBTime(), LMP);
		this.setValue("PRE_WEEK", week + "");
	}

	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastStartDate() {
		Timestamp t2 = (Timestamp) this.getValue("BREASTFEED_ENDDATE");
		if (t2 == null)
			return;
		Timestamp t1 = (Timestamp) this.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			this.messageBox("E0063");
			this.setValue("BREASTFEED_STARTDATE", "");
			return;
		}
	}

	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastEndDate() {
		Timestamp t2 = (Timestamp) this.getValue("BREASTFEED_ENDDATE");
		Timestamp t1 = (Timestamp) this.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			this.messageBox("E0064");
			this.setValue("BREASTFEED_ENDDATE", "");
			return;
		}
		if (t2 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			this.messageBox("E0063");
			this.setValue("BREASTFEED_ENDDATE", "");
			return;
		}
	}

	/**
	 * ���ߵ���¼�
	 * 
	 * @param tag
	 *            String
	 */
	public void onSubjText(String tag) {
		TParm inParm = new TParm();
		inParm.setData("subject", this);
		inParm.setData("TAG", tag);
		inParm.setData("DEPT_OR_DR", "1");
		inParm.setData("DEPTORDR_CODE", "2");
		this.openWindow("%ROOT%\\config\\opd\\OPDComPhraseQuote.x", inParm,
				true);
	}

	/**
	 * �ж��Ƿ��Ѷ��� true �Ѷ���false δ��
	 */
	private boolean isReadEKT() {
		return isReadEKT;

	}
	
	/**
	 * ѡ��������ﵥѡ��ť�л�
	 * caowl
	 * */
	public void onFirstFlg(){
		
		if(((TRadioButton)this.getComponent("FIRST_FLG")).isSelected()){
			this.setValue("NEXT_FLG", "N");
		}
		if(((TRadioButton)this.getComponent("NEXT_FLG")).isSelected()){
			this.setValue("FIRST_FLG", "N");
		}
		
	}
	
	
	/**
	 * �����Ԥ�����   дһ��ԤԼ�Һ���Ϣ
	 * caowl 20131116
	 * ������һ��case_no
	 * */
	public void onInsertReg(TParm parm){
		String newCaseNo = parm.getValue("CASE_NO");
        String mr_no =parm.getValue("MR_NO");
        String APPT_CODE = "Y";     
        String ARRIVE_FLG = "N";
        String OPT_USER = Operator.getID();
        Timestamp OPT_DATE = SystemTool.getInstance().getDate();
        String ADM_DATE ="";
        String pre_adm_date1 = "";
        String OPT_TERM = Operator.getIP();
        TParm tp = new TParm();
//        if(parm.getData("ADM_DATE")!=null && !parm.getData("ADM_DATE").equals("")){
//        	 ADM_DATE =parm.getData("ADM_DATE").toString().substring(0,
//        	            10).replace("-", "") + "000000"; //������ؼ��ϵ����ݽ�ȡ�Ժ󸳸�����
//        	 tp.setData("ADM_DATE",
//                     StringTool.getTimestamp(ADM_DATE, "yyyyMMddHHmmss"));
//        	 pre_adm_date1 = " AND ADM_DATE = '"+parm.getData("ADM_DATE").toString().substring(0,
//     	            10).replace("-", "")+"'";
//        }else{
//        	ADM_DATE = "";
//        	tp.setData("ADM_DATE","");
//        }
        
        tp.setData("REGMETHOD_CODE", "T");//�Һŷ�ʽ,�绰�˹�
        tp.setData("SERVICE_LEVEL", reg.getServiceLevel());//����ȼ�
        tp.setData("ADM_TYPE", admType);//�ż�ס��ԤԼ��Ϣ��������ʾ
        tp.setData("REGION_CODE", Operator.getRegion());
        tp.setData("CTZ1_CODE", parm.getData("CTZ1_CODE"));//���
        tp.setData("OLD_CASE_NO", caseNo);
        tp.setData("REG_DATE", OPT_DATE);
        tp.setData("ADM_DATE", parm.getData("ADM_DATE"));
        tp.setData("CASE_NO", newCaseNo);
        tp.setData("MR_NO", mr_no);
        tp.setData("APPT_CODE", APPT_CODE);
        tp.setData("ARRIVE_FLG", ARRIVE_FLG);
        tp.setData("OPT_USER", OPT_USER);
        tp.setData("OPT_DATE", OPT_DATE);
        tp.setData("OPT_TERM", OPT_TERM);
        tp.setData("IS_PRE_ORDER","Y");//�Ƿ���Ԥ�����
			TParm onSaveP = REGAdmForDRTool.getInstance().onSavePreOrder(tp);
			if(onSaveP.getErrCode()<0){
	            this.messageBox("Ԥ����鱣��ʧ�ܣ�");
	            return;
		}
        
        
        
//        this.messageBox("Ԥ����鱣��ɹ���");           		
	}
	

	/**
	 * ����
	 * 
	 * caowl  20131116  maybe
	 */
	public void onSave() {
		if (!canSave()) {
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (!isMyPat()) {
			saveSubjrec();
			this.messageBox("E0193");
			return;
		}
		// ============xueyf modify 20120220 start
//		if (!isReadEKT()) {
//			this.messageBox("δ��ȡҽ�ƿ���");
//			// return;
//		}
		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {//û������ϣ����ܱ���
			this.messageBox("E0065");
			return;
		}
		// xueyf add ins check
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		// ============xueyf modify 20120220 stop
		acceptForSave();
		saveSubjrec();
		saveRegInfo();
		odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "Y");
		//caowl 20131106 start
//		if(this.getValue("FIRST_FLG").equals("Y")){
//			odo.getRegPatAdm().setItem(0, "FIRST_FLG", "Y");
//		}
//		if(this.getValue("NEXT_FLG").equals("Y")){
//			odo.getRegPatAdm().setItem(0, "FIRST_FLG", "N");
//		}		
		//caowl 20131106 end
		//huangtt 20131128 start
		tblPat.acceptText();
		String firstFlg=tblPat.getParmValue().getValue("FIRST_FLG", PatSelectRow);
		if(firstFlg.equals("����")){
			odo.getRegPatAdm().setItem(0, "FIRST_FLG", "Y");
		}
		if(firstFlg.equals("����")){
			odo.getRegPatAdm().setItem(0, "FIRST_FLG", "N");
		}
		//huangtt 20131128 end
		// �ж�reg������SEEN_DR_TIME�Ƿ������� ���Ϊ�ռ�¼��ǰʱ��
		if (odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
//		if (!odo.isModified()) {//===pangben 2013-5-3 ע�Ͳ�У���Ƿ��в���ҽ�����������ִ���շ�
//			return;
//		}
		// if (oPdSaveCountCheck()) {
		// return;
		// }
		if (!odo.checkSave()) {
			// $$ modified by lx ������ʾ���ҩ
			if (odo.getErrText().indexOf("��治��") != -1) {
				if (Operator.getLockFlg().equals("Y")) {
//					this.messageBox(odo.getErrText());
				}else{
         String orderCode = odo.getErrText().split(";")[1];
					TParm inParm = new TParm();
					inParm.setData("orderCode", orderCode);
					this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
				}
			} else {
//				this.messageBox(odo.getErrText());
			}
			// $$ modified by lx ������ʾ���ҩ
			this.messageBox(odo.getErrText()+", ����ʧ��");
			return;
		}
		String admStatus = odo.getRegPatAdm().getItemString(0, "ADM_STATUS");

		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}
		TParm orderParm = new TParm();
		TParm ctrlParm = new TParm();
		TParm chnParm = new TParm();
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
		if (odo.getOpdOrder().isModified()) {
			orderParm = odo.getOpdOrder().getModifiedOrderRx();
			ctrlParm = odo.getOpdOrder().getModifiedCtrlRx();
			chnParm = odo.getOpdOrder().getModifiedChnRx();
			exaParm = odo.getOpdOrder().getModifiedExaRx();
			odo.getOpdOrder().updateMED(odo);
			opParm = odo.getOpdOrder().getModifiedOpRx();
			if (orderParm.getCount() > 0) {
				if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
					this.messageBox("E0117");
				}
			}
		}
		//У������
		if(!getTempSaveParm())
			return;
		TParm ektOrderParmone = new TParm();
		// parm.setData("REGION_CODE", Operator.getRegion());
		ektOrderParmone.setData("CASE_NO", reg.caseNo());
		// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
		ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(
				ektOrderParmone);
		if (ektOrderParmone.getErrCode() < 0) {
			return;
		}
		// ��ô˴�ҽ�ƿ�����������Ҫִ�е�ҽ��=====pangben 2012-4-14
		TParm ektSumExeParm = odo.getOpdOrder().getEktParam(ektOrderParmone);
		TParm sendParm=odo.getOpdOrder().getSendParam(ektOrderParmone);//====pangben 2014-3-19
		TParm preDateParm = new TParm();
		if(rxType.equals("5")){
			//�����޸ĵĴ���ǩ�Ų�ѯԤִ��ʱ��
			String rxNo = "";
			if(exaParm.getCount()>0){//�����޸ĵļ����鴦��ǩ
				for(int i = 0;i < exaParm.getCount();i++){
					//����ȡ������ǩ��
				    rxNo += "'"+exaParm.getValue("RX_NO", i)+"'"+",";
				    if(i == exaParm.getCount()-1){
				    	rxNo += "'"+exaParm.getValue("RX_NO", i)+"'";
				    }
				}
				String selectSql = "SELECT DISTINCT PRE_DATE,CASE_NO FROM OPD_ORDER WHERE " +
						"RX_NO IN("+rxNo+") ORDER BY PRE_DATE";
				preDateParm = new TParm(TJDODBTool.getInstance().select(selectSql));
			}
			
//		if(!preOrder()){//Ԥ�����У��yanjing  20140101
		if(preOrder().equals("NO")){//Ԥ�����У��yanjing  20140101
			this.messageBox("ͬһ�Ŵ���ǩ��Ԥִ��ʱ�������ͬ��");
			return;
		}else if(preOrder().equals("OVER")){
			this.messageBox("Ԥ��ʱ�䲻��С�ڵ�ǰʱ�䡣");
			return;
		}
		}
		
		//====zhangp 20131202
		if(EKTIO.getInstance().ektAyhSwitch() && !(null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0)){
			TParm ayhParm = EKTpreDebtTool.getInstance().checkMasterForOdo(odo);
			if(ayhParm.getErrCode()<0){
				if(messageBox("�Ƿ��������", ayhParm.getErrText()+",��������?", 0) != 0){
					return;
				}
			}
		}
		if (!odo.onSave()) {
			// EKTIO.getInstance().unConsume(odo.getTredeNo(),this);
			this.messageBox("E0005");
			this.messageBox_(odo.getErrText());
			return;
		}
		if(rxType.equals("5")){
			String preDate = "";
			TParm caseNoParm = new TParm();
			//��ѯԤ��ʱ���Ƿ�ȫ���޸�
			if(preDateParm.getCount()>0){
				for(int j = 0 ;j<preDateParm.getCount();j++){
					preDate = preDateParm.getValue("PRE_DATE", j).toString()
					             .substring(0, 10).replace("/", "").replace("-", "");
					String selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER " +
					"WHERE MR_NO = '"+odo.getMrNo()+"' AND PRE_DATE = TO_DATE("+preDate+",'YYYYMMDD') ORDER BY CASE_NO";
			caseNoParm = new TParm(TJDODBTool.getInstance().select(selectCaseNo));
			if(caseNoParm.getCount() <= 0){//ɾ����  ��ǰ�ĹҺ���Ϣ
//					for(int m = 0;m<preDateParm.getCount();m++){
						String caseNoReg = preDateParm.getValue("CASE_NO", j);
						String deleteReg = "DELETE REG_PATADM WHERE CASE_NO = '"+caseNoReg+"'";
						TJDODBTool.getInstance().update(deleteReg);
//					}
			} 
				}
			}
			
		}
		onSendInw(sendParm,false);
		//this.messageBox("P0005");
		if(EKTIO.getInstance().ektAyhSwitch()){
			
		}else{
			isExeFee(ektOrderParmone, ektSumExeParm);
		}
		// ͨ��reg��caseNo�õ�pat
		opb = OPB.onQueryByCaseNo(reg);// ===============pangben 20110914
		//isFee = true;// ִ�гɹ��Ժ�����շ�===============pangben 20110914
		if (orderParm.getCount("RX_NO") > 0) {
			onPrintOrder(orderParm, MED);
		}
		if (ctrlParm.getCount("RX_NO") > 0) {
			onPrintOrder(ctrlParm, CTRL);
		}
		if (chnParm.getCount("RX_NO") > 0) {
			onPrintOrder(chnParm, CHN);
		}
		// ����Ӵ�ӡ������ҽ���� shibl add20120320
		// �ż���
		// =======pangben 2012-06-28��ӡ����ǩ�������ż��� ����Ҫ��ӡ
		// if (this.admType.equals("E")) {
		if (exaParm.getCount("RX_NO") > 0) {
			// ��ӡ������ҽ����
			onPrintExa(exaParm);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						onEmr();
					} catch (Exception e) {
					}
				}
			});
		}
		if (opParm.getCount("RX_NO") > 0) {
			onPrintOp(opParm);
		}
		int countPreOrder = odo.getDiagrec().rowCount();
		for (int i = 0; i < countPreOrder; i++) {
			if (odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odo.getMrNo());
				can.setData("CASE_NO", odo.getCaseNo());
				can.setData("ICD_CODE", odo.getDiagrec().getItemString(i,
						"ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				can.setData("ADM_TYPE", odo.getAdmType());//add by wanglong 20140307
				this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
			}
		}
		// }
		// =============pangben 2012-6-28 �����������ӡ����
		// if (this.admType.equals("O"))
		// onPrintCase();
		onContagionReport();// ��Ⱦ����������

	}
	/**
	 * ������У��
	 * yanjing 20140101
	 */
	private String preOrder(){
		//yanj �����Ԥ����飬���У��(�Դ���ǩΪ��λ)
//		boolean isPreDateSameFlg = true;// �Ƿ����ͬһ�Ŵ���ǩ��ʱ�䲻ͬ�����
		String isPreDateSameFlg = "YES";// �Ƿ����ͬһ�Ŵ���ǩ��ʱ�䲻ͬ�����
		TParm parmRegPre = new TParm();
		String[] data = odo.getOpdOrder().getRx(rxType);// yanjing 20131212
		for (int m = 0; m < data.length; m++) {
			String pre_date = checkDateforRxNo(m);//У��ͬһ�Ŵ���ǩ�ϵ�Ԥִ��ʱ���Ƿ���ͬ
		if(pre_date.equals("no")){
			isPreDateSameFlg = "NO";
//			isPreDateSameFlg = false;
//			this.messageBox("ͬһ�Ŵ���ǩ��Ԥִ��ʱ�������ͬ��");
			return isPreDateSameFlg;
		}else{//�뵱ǰʱ��Ƚ�
			//У��Ԥ������ʱ�䲻��������
			Timestamp rollDay = SystemTool.getInstance().getDate();
			String date = StringTool.getString(rollDay, "yyyy-MM-dd 00:00:00.0");
			System.out.println("pre_date  pre_date  pre_date is ::"+pre_date);
			if(date.compareTo(pre_date)>0&&!(pre_date.equals(null))&&!("".equals(pre_date))){
				isPreDateSameFlg = "OVER";
//				this.messageBox("Ԥ��ʱ�䲻��С�ڽ��졣");
				return isPreDateSameFlg;
			}
		}
		}
		
		Map rxCaseData = new HashMap<String, String>();// �洢�����ż���Ӧ�ľ����
			OpdOrder preOrder = odo.getOpdOrder();
			String lastFilter = preOrder.getFilter();
			preOrder.setFilter("");
			preOrder.filter();
			
			TParm preDateParm = new TParm();
			for (int i = 0; i < preOrder.rowCount(); i++) {
				if(preOrder.getItemString(i, "IS_PRE_ORDER").equals("Y")
						&& preOrder.getItemString(i, "SETMAIN_FLG").equals("Y")){
					String preRxNo = preOrder.getItemString(i, "RX_NO");
					String sb = preOrder.getItemString(i, "ORDER_CODE");
					String preDate = "nullPreDte";
					if(preOrder.getItemString(i, "PRE_DATE").length()>0){
						preDate = preOrder.getItemString(i, "PRE_DATE").substring(0, 10);
					}
					preDateParm.addData("RX_NO", preRxNo);
					preDateParm.addData("PRE_DATE", preDate);
					}
				}
			for (int i = 0; i < preDateParm.getCount("RX_NO"); i++) {
				String selectRegSql = "";
				if("nullPreDte".equals(preDateParm.getValue("PRE_DATE", i))){
					selectRegSql = "SELECT CASE_NO FROM REG_PATADM " +
					"WHERE OLD_CASE_NO = '"+this.caseNo+"' " +
							"AND ADM_DATE IS NULL " +
							" AND IS_PRE_ORDER ='Y' " +
							" AND REGCAN_USER IS NULL ";
				}else{
					String admDate = preDateParm.getValue("PRE_DATE", i).substring(0, 10).replace("-", "").replace("/", "");
					String admDateS = admDate+"0000";
					String admDateE = admDate+"2359";
					//���ݲ����ź;���ʱ���ѯreg_patadm���鿴�Һ���Ϣ�Ƿ����
					selectRegSql = "SELECT CASE_NO FROM REG_PATADM " +
							"WHERE MR_NO = '"+odo.getMrNo()+"' " +
									"AND (APPT_CODE = 'Y' OR APPT_CODE = '' OR APPT_CODE IS NULL) " +
//							        " OLD_CASE_NO = '"+this.caseNo+"' " +
									"AND ADM_DATE BETWEEN TO_DATE('"+admDateS+"','YYYYMMDDHH24MI') " +
									" AND TO_DATE ('"+admDateE+"','YYYYMMDDHH24MI')" +
//									" AND IS_PRE_ORDER ='Y' " +
									" AND REGCAN_USER IS NULL ";
//					System.out.println("��ѯ������Ƿ���� selectRegSql is����"+selectRegSql);
				}
				TParm selectRegParm = new TParm(TJDODBTool.getInstance().select(selectRegSql));
				if(selectRegParm.getCount()<=0){
					if("nullPreDte".equals(preDateParm.getValue("PRE_DATE", i))){
			        	parmRegPre.setData("ADM_DATE","");
					}else{
						parmRegPre.setData("ADM_DATE",StringTool.getTimestamp
								(preDateParm.getValue("PRE_DATE", i).substring(0, 10), "yyyy-MM-dd"));
					}
					parmRegPre.setData("MR_NO",reg.getPat().getMrNo());
					parmRegPre.setData("CTZ1_CODE",reg.getPat().getCtz1Code());
					parmRegPre.setData("DEPT_CODE",reg.getDeptCode());
					String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
				            "CASE_NO", "CASE_NO");//����µľ���
					parmRegPre.setData("CASE_NO",newCaseNo);
					onInsertReg(parmRegPre);//��Һ�����д����
					if(!rxCaseData.containsKey(preDateParm.getValue("RX_NO", i))){
					rxCaseData.put(preDateParm.getValue("RX_NO", i), newCaseNo);
					}
				}else{
					if(!rxCaseData.containsKey(preDateParm.getValue("RX_NO", i))){
					rxCaseData.put(preDateParm.getValue("RX_NO", i), selectRegParm.getValue("CASE_NO", 0));
					}
				}
			}
			preOrder.setFilter(lastFilter);
			preOrder.filter();
			odo.setPreCaseNos(rxCaseData);
			odo.preChangeCaceNo();
			return isPreDateSameFlg;
		}
//	/**
//	 * �޸�Ԥ������ҽ��
//	 * yanjing
//	 * 20140211
//	 */
//	private boolean isOpdOrderModified(){
//		System.out.println("========================");
//		TParm exaParm = new TParm();
//		exaParm = odo.getOpdOrder().getModifiedExaRx();
//	System.out.println("�޸ĵļ����� exaParm is����"+exaParm);
//		OpdOrder opdOrder = odo.getOpdOrder();
//		if (opdOrder.isModified()) {
//			int[] mRows = opdOrder.getModifiedRows();
//			for (int i = 0; i < mRows.length; i++) {
//				
//			}
//		}
//		return false;
//	}
	/**
	 * У��ͬһ�Ŵ���ǩ�ϵ�Ԥִ��ʱ���Ƿ���ͬ
	 * yanjing 20140101
	 */
    private String checkDateforRxNo(int m){
    	String[] data = odo.getOpdOrder().getRx(rxType);//yanjing 20131212 �����鴦��ǩ����
    	String pre_date = "";
    	OpdOrder preOrder = odo.getOpdOrder();
    		preOrder.setFilter("RX_NO = '" + data[m].split(",")[0] + "'");
    		preOrder.filter();
    		for(int i = 0;i<preOrder.rowCount();i++){//ѭ���ض�����ǩ��ҽ��
    			String orderCode = preOrder.getItemString(i, "ORDER_CODE");
    			if(!("".equals(orderCode)&&orderCode.equals(null))
    					&&preOrder.getItemString(i, "IS_PRE_ORDER").equals("Y")){//Ԥ���������
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
	 * �޸�ҽ������ʱ�������������ݴ����ִ��ֱ���շѲ���
	 */
	private void isExeFee(TParm ektOrderParmone, TParm ektSumExeParm) {
		//boolean ektOnFee = false;
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
			// this.messageBox("δȷ�����,���ҽ�ƿ�");
			// return;

		} else {
			// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
			// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
			updateOrderUnConcle(ektOrderParmone, ektSumExeParm);	
		}
		// ɾ��\�޸�ҽ���շ�
		if (!onEktSave(ektOrderParmone, ektSumExeParm)) {
		}
	}
	/**
	 * �޸��շ�ҽ������ִ�пۿ���泷������
	 * @return
	 */
	private boolean updateOrderUnConcle(TParm ektOrderParmone,TParm ektOrderParm){
		//����ݴ水ť ������շѵ�ҽ���޸Ľ�� ִ�� �޸�ҽ���ۿ���˿����
		boolean unFlg = false;
		TParm updateParm=ektOrderParm.getParm("updateParm");
		for (int i = 0; i < updateParm.getCount("ORDER_CODE"); i++) {
			if (updateParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| updateParm.getValue("CAT1_TYPE", i).equals(
							"LIS")
					|| updateParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < ektOrderParmone.getCount("ORDER_CODE"); j++) {//====pangb 2013-2-28 �޸�ҽ�������ݴ�������
				if (updateParm.getValue("RX_NO", i).equals(
						ektOrderParmone.getValue("RX_NO", j))
						&& updateParm.getValue("SEQ_NO", i).equals(
								ektOrderParmone.getValue("SEQ_NO", j))) {
					if (updateParm.getDouble("AMT", i) != ektOrderParmone
							.getDouble("AR_AMT", j)) {
						unFlg = true;// �ж��Ƿ��޸�ҽ��
						break;
					}
				}
			}
			if (unFlg) {
				ektOrderParmone.setData("OPBEKTFEE_FLG", "Y");
				//ektOnFee = true;
				break;
			}
		}
		return unFlg;
	}
	/**
	 * ̩������ҽ�ƿ��շ� ֻ�ܲ���ҽ�ƿ��շѲ��� 
	 */
	public void onFee() {
		if (!PatTool.getInstance().isLockPat(pat.getMrNo())) {
			this.messageBox("�����Ѿ��������û�ռ��!");
			return;
		}
		if (null == caseNo || caseNo.length() <= 0)
			return;
//		// �鿴�˾��ﲡ���Ƿ���ҽ�ƿ�����
//		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
//			this.messageBox("δȷ�����,���ҽ�ƿ�");
//			return;
//
//		}
//		if (!ektReadParm.getValue("MR_NO").equals(this.getValue("MR_NO"))) {
//			this.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
//					+ ektReadParm.getValue("PAT_NAME"));
//			return;
//		}
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
		
		TParm ektOrderParm = OrderTool.getInstance().selDataForOPBEKT(parm);
		onTempSave(ektOrderParm);
		//odo.getOpdOrder().onQuery();
		//onExeFee();
	}
	/**
	 * ִ���շ��Ժ����³�ʼ����������
	 */
	private void onExeFee(){
		// ���¼�������
		if (!odo.getOpdOrder().onQuery()) {
			return ;
		}
		//=======pangben 2013-7-10 �޸Ľ���ɾ����������
		// ɾ��������һ����
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		String rxNo="";
		TTable table=null;
		String rxType="";
		String tableName="";
		switch (tabPanel.getSelectedIndex()) {
		case 0:
			rxNo = this.getValueString(this.EXA_RX);
			table=tblExa;
			rxType=EXA;
			tableName=TABLE_EXA;
			break;
		case 1:
			// ����
			rxNo = this.getValueString(OP_RX);
			table=tblOp;
			rxType=OP;
			tableName=TABLE_OP;
			break;
		case 2:
			// ��ҩ
			rxNo = this.getValueString(MED_RX);
			table=tblMed;
			rxType=MED;
			tableName=TABLE_MED;
			break;
		case 3:
			// ��ҩ
			rxNo = this.getValueString(CHN_RX);
			table=tblChn;
			rxType=CHN;
			tableName=TABLE_CHN;
			break;
		case 4:
			rxNo = this.getValueString(CTRL_RX);
			table=tblCtrl;
			rxType=CTRL;
			tableName=TABLE_CTRL;
			break;
		}
		String []tableNames={TABLE_EXA,TABLE_OP,TABLE_MED,TABLE_CHN,TABLE_CTRL};
		odo.getOpdOrder().newOrder(rxType, rxNo);
		table.setDSValue();
		getTableInit(tableName, tableNames);
//		odo.getOpdOrder().retrieve();//===pangben 2013-5-13���»�����ݿ����ݣ����ص��������
		getChangeOrderTab();
	}
	/**
	 * ��ʼ������
	 * @param tableName
	 */
	private void getTableInit(String tableName,String [] tableNames){
		for (int i = 0; i < tableNames.length; i++) {
			if (tableName.equals(tableNames[i])) {
				continue;
			}
			this.setTableInit(tableNames[i], false);
		}
	}
	/**
	 * ҽ�ƿ��ݴ����
	 * 
	 * @param ektOrderParm
	 */
	private void onTempSave(TParm ektOrderParm) {
		// System.out.println("HHHFHFGFGFGFG");
		if (!canSave()) {
			this.messageBox_("�ѳ�������ʱ�䲻���޸�");
			//isEKTFee = false;// ҽ�ƿ��շ�ʹ��
			return;
		}
		if (!isMyPat()) { // �жϽ���
			saveSubjrec();
			this.messageBox("E0193");
			//isEKTFee = false;// ҽ�ƿ��շ�ʹ��
			return;
		}
		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag < 0) {
			this.messageBox("E0065");
			return;
		}
		acceptForSave();
		saveSubjrec();
		// ��ô˴�ҽ�ƿ�����������Ҫִ�е�ҽ��
		// ��Ҫ������ҽ�� ��ɾ�� ����ӡ� �޸�
		TParm ektSumExeParm = odo.getOpdOrder().getEktParam(ektOrderParm);
		TParm sendParm=odo.getOpdOrder().getSendParam(ektOrderParm);//========pangben 2014-3-19
		if(!getTempSaveParm())
			return;
		setTempSaveOdo();
		saveRegInfo();
		// �鿴�˾��ﲡ���Ƿ���ҽ�ƿ�����
		// ���ҽ�� �շ�
		//====zhangp 20131202
		if(EKTIO.getInstance().ektAyhSwitch() && !(null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0)){
			TParm ayhParm = EKTpreDebtTool.getInstance().checkMasterForOdo(odo);
			if(ayhParm.getErrCode()<0){
				if(messageBox("�Ƿ��������", ayhParm.getErrText()+",��������?", 0) != 0){
					return;
				}
			}
		}else{
			if (!odo.isModified()) {
				onEktSave(ektOrderParm, ektSumExeParm); // �������ã�ҽ�ƿ�����ʧ�ܻس���
				return;
			}
		}
		if (!odo.checkSave()) {
			// $$ modified by lx ������ʾ���ҩ
			if (odo.getErrText().indexOf("��治��") != -1) {
				if (Operator.getLockFlg().equals("Y")) {
//					this.messageBox(odo.getErrText());
				}else{
					String orderCode = odo.getErrText().split(";")[1];
					TParm inParm = new TParm();
					inParm.setData("orderCode", orderCode);
					this.openDialog("%ROOT%\\config\\pha\\PHAREDrugMsg.x", inParm);
				}
			} else {
//				this.messageBox(odo.getErrText());
			}

			this.messageBox(odo.getErrText()+", ����ʧ��");
			//isEKTFee = false;// ҽ�ƿ��շ�ʹ��
			return;
		}
		// //�õ����뵥�����
		// OrderUtil.getInstance().getOrderPasEMRAll(odo.getOpdOrder(),"ODO");
		TParm exaParm = new TParm();
		TParm opParm = new TParm();
//		TParm orderParm = new TParm();===��ҩ����ǩ����ӡ
		TParm ctrlParm = new TParm();
		if (odo.getOpdOrder().isModified()) {
			exaParm = odo.getOpdOrder().getModifiedExaRx();
			opParm = odo.getOpdOrder().getModifiedOpRx();
			ctrlParm = odo.getOpdOrder().getModifiedCtrlRx();//����ҩƷ
		}
//		if (odo.getOpdOrder().isModified()) {
//			orderParm = odo.getOpdOrder().getModifiedOrderRx();
//			if (orderParm.getCount() > 0) {
//				if (!odo.getOpdOrder().isOrgAvalible(phaCode)) {
//					this.messageBox("E0117");
//					return;
//				}
//			}
//		}
		TParm ektOrderParmone = new TParm();
		if (null == ektOrderParm) {//����ݴ水ťektOrderParm ���Ϊnull
			TParm parmOne = new TParm();
			parmOne.setData("CASE_NO", reg.caseNo());
			// ��ô˴β���ҽ�ƿ����е�ҽ�� ��ִ��ɾ������ҽ��ʱʹ��
			ektOrderParmone = OrderTool.getInstance().selDataForOPBEKT(parmOne);
			if (ektOrderParmone.getErrCode() < 0) {
				return;
			}
		}
		//=======yanjing Ԥ�����У�� start
     	//System.out.println("�����ɣ�rxType is����"+rxType);
		TParm preDateParm = new TParm();
		if (rxType.equals("5")) {
			//�����޸ĵĴ���ǩ�Ų�ѯԤִ��ʱ��
			String rxNo = "";
			if(exaParm.getCount()>0){//�����޸ĵļ����鴦��ǩ
				for(int i = 0;i < exaParm.getCount();i++){
					//����ȡ������ǩ��
				    rxNo += "'"+exaParm.getValue("RX_NO", i)+"'"+",";
				    if(i == exaParm.getCount()-1){
				    	rxNo += "'"+exaParm.getValue("RX_NO", i)+"'";
				    }
				}
				String selectSql = "SELECT DISTINCT PRE_DATE,CASE_NO FROM OPD_ORDER WHERE " +
						"RX_NO IN("+rxNo+") ORDER BY PRE_DATE";
				preDateParm = new TParm(TJDODBTool.getInstance().select(selectSql));
			}
//			if (!preOrder()) {// Ԥ�����У��yanjing 20140101
			if (preOrder().equals("NO")) {// Ԥ�����У��yanjing 20140101
				this.messageBox("ͬһ�Ŵ���ǩ��Ԥִ��ʱ�������ͬ��");
				return;
			}else if(preOrder().equals("OVER")){
				this.messageBox("Ԥ��ʱ�䲻��С�ڵ�ǰʱ�䡣");
				return;
			}
		}
	   //=======yanjing Ԥ�����У�� end
			
		//��������
		if (!odo.onSave()) {
			this.messageBox(odo.getErrText());
			this.messageBox("E0005");
			//isEKTFee = false;// ҽ�ƿ��շ�ʹ��
			return;
		}
		
		if(rxType.equals("5")){
			String preDate = "";
			TParm caseNoParm = new TParm();
			//��ѯԤ��ʱ���Ƿ�ȫ���޸�
			if(preDateParm.getCount()>0){
				for(int j = 0 ;j<preDateParm.getCount();j++){
					String selectCaseNo = "";
					String sPreDate = preDateParm.getValue("PRE_DATE", j).toString();
					if(!"".equals(sPreDate)&&!sPreDate.equals(null)){
						preDate = sPreDate.substring(0, 10).replace("/", "").replace("-", "");
						selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER " +
				        "WHERE MR_NO = '"+odo.getMrNo()+"' " +
						"AND PRE_DATE = TO_DATE("+preDate+",'YYYYMMDD')" +
								"ORDER BY CASE_NO";
					}else{
						selectCaseNo = "SELECT DISTINCT CASE_NO FROM OPD_ORDER " +
				        "WHERE MR_NO = '"+odo.getMrNo()+"' " +
						"AND IS_PRE_ORDER = 'Y' AND PRE_DATE IS NULL" +
								"ORDER BY CASE_NO";
					}
			caseNoParm = new TParm(TJDODBTool.getInstance().select(selectCaseNo));
			if(caseNoParm.getCount() <= 0){//ɾ����  ��ǰ�ĹҺ���Ϣ
//					for(int m = 0;m<preDateParm.getCount();m++){
						String caseNoReg = preDateParm.getValue("CASE_NO", j);
						String deleteReg = "DELETE REG_PATADM WHERE CASE_NO = '"+caseNoReg+"'";
						TJDODBTool.getInstance().update(deleteReg);
//					}
			} 
				}
			}
			
		}
		onSendInw(sendParm,false);
		//this.messageBox("P0005");
		opb = OPB.onQueryByCaseNo(reg);// ִ��ҽ�ƿ��շ�ʱʹ��===============pangben
		// 20111010
		//isFee = true;// ִ�гɹ��Ժ�����շ�===============pangben 20111010
		// //ҽ�ƿ��������
		if (null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0) {
		} else {
			// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
			// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
			if (null == ektOrderParm) {
				//����ݴ水ť ������շѵ�ҽ���޸Ľ�� ִ�� �޸�ҽ���ۿ���˿����
				if(updateOrderUnConcle(ektOrderParmone, ektSumExeParm))
					ektOrderParm=ektOrderParmone;
			}
		}
		// ɾ��\�޸�ҽ���շ�
		if (null!=ektOrderParm){
			//====zhangp 20131202
			if(EKTIO.getInstance().ektAyhSwitch() && !(null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0)){
				
			}else{
				onEktSave(ektOrderParm, ektSumExeParm);//=======pangben 2013-3-19 �޸Ľ���Ҳ��ӡ����ǩ
			}
		}
		if (exaParm.getCount("RX_NO") > 0) {
			// ��ӡ������ҽ����
			onPrintExa(exaParm);
			getRun(1);
		}
		// =======pangben 2012-6-29 ��ҩ����ǩ��ӡ
//		if (orderParm.getCount("RX_NO") > 0) {
//			onPrintOrder(orderParm, MED);
//		}
		if (opParm.getCount("RX_NO") > 0) {//���ô�ӡ����ǩ
			onPrintOp(opParm);
		}
		if (ctrlParm.getCount("RX_NO") > 0) {//����ҩƷ��ӡ����ǩ
			onPrintOrder(ctrlParm, CTRL);
		}
		getRun(4);
		if (ektExeConcel) {// ҽ�ƿ� ɾ��ҽ������ ��ѡȡ����ť ִ�г���ɾ��ҽ������
			TParm parm = new TParm();
			parm.setData("MR_NO", ektReadParm.getValue("MR_NO"));
			TParm regParm = OPDAbnormalRegTool.getInstance().selectRegForOPD(
					parm);
			for (int i = 0; i < regParm.getCount("CASE_NO"); i++) {
				if (regParm.getValue("CASE_NO", i).equals(reg.caseNo())) {
					// wc = "W"; // Ĭ��Ϊ��ҽ
					this.initOpd(regParm, 0);// ��ʼ��
					ektExeConcel = false;
					break;
				}
			}
		}
	}
	/**
	 * ������Ϣ
	 * =====pangben 2013-12-18 �������汾 ҩ��������Ϣ
	 * @param ektSumExeParm
	 */
	private void onSendInw(TParm ektSumExeParm, boolean flg) {
		System.out.println("PHA_RX_NO::::::"+ektSumExeParm.getValue("PHA_RX_NO"));
		if (ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
			// ==pangben 2013-5-21 ���Ԥ����
			if (flg) {
				TParm spcParm = new TParm();
				spcParm.setData("RX_NO", ektSumExeParm.getValue("PHA_RX_NO"));
				spcParm.setData("CASE_NO", reg.caseNo());
				spcParm.setData("CAT1_TYPE", "PHA");
				spcParm.setData("RX_TYPE", "7");
				// ��������ô˴β�����ҽ����ͨ������ǩ���
				TParm spcResult = OrderTool.getInstance().getSumOpdOrderByRxNo(
						spcParm);
				if (spcResult.getErrCode() < 0) {
					this.messageBox("������������ҽ����ѯ���ִ���");
				} else {

					spcResult.setData("SUM_RX_NO", ektSumExeParm
							.getValue("PHA_RX_NO"));
					spcResult = TIOM_AppServer.executeAction(
							"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
							spcResult);
					if (spcResult.getErrCode() < 0) {
						System.out.println("����������:" + spcResult.getErrText());
						this.messageBox("������������ҽ����ӳ��ִ���,"
								+ spcResult.getErrText());
					} else {
						phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15���ҩ����ҩ��ʾ���������
						sendMedMessages();
					}
				}
			} else {
				phaRxNo = ektSumExeParm.getValue("PHA_RX_NO");// =pangben2013-5-15���ҩ����ҩ��ʾ���������
				sendMedMessages();
			}
		}
	}
	/**
	 * ��Ⱦ�����濨
	 */
	private void getDiagrec(){
		int count = odo.getDiagrec().rowCount();
		for (int i = 0; i < count; i++) {
			if (odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odo.getMrNo());
				can.setData("CASE_NO", odo.getCaseNo());
				can.setData("ICD_CODE", odo.getDiagrec().getItemString(i,
						"ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				can.setData("ADM_TYPE", odo.getAdmType());//add by wanglong 20140307
				this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
			}
		}
	}
	/**
	 * 
	 * @param type 1:�������뵥 2: 4:��Ⱦ�����濨
	 */
	public void getRun(final int type){
	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					switch (type){
					case 1:
						onEmr();
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						getDiagrec();
						break;
					}
					
				} catch (Exception e) {
				}
			}
		});
	}
	/**
	 * �ݴ����� 
	 * ��ֵ����
	 */
	private void setTempSaveOdo(){
		String admStatus = odo.getRegPatAdm().getItemString(0, "ADM_STATUS");
		if (!("6".equalsIgnoreCase(admStatus) || "9"
				.equalsIgnoreCase(admStatus))) {
			odo.getRegPatAdm().setItem(0, "ADM_STATUS", "2");
		}
		String SEE_DR_FLG = (String) odo.getRegPatAdm().getItemData(0,
				"SEE_DR_FLG");
		if (!StringUtil.isNullString(SEE_DR_FLG) && !SEE_DR_FLG.equals("Y")) {
			odo.getRegPatAdm().setItem(0, "SEE_DR_FLG", "T");
		}
		// �ж�reg������SEEN_DR_TIME�Ƿ������� ���Ϊ�ռ�¼��ǰʱ��
		if (odo.getRegPatAdm().getItemData(0, "SEEN_DR_TIME") == null) {
			odo.getRegPatAdm().setItem(0, "SEEN_DR_TIME",
					SystemTool.getInstance().getDate());
		}
	}
	/**
	 * �ݴ���� У������
	 * @return
	 */
	private boolean getTempSaveParm(){
		// �¼ӵ�����
		int newRow[] = odo.getOpdOrder().getNewRows();
		TParm ordParm = new TParm();
		// ������ҩ���
		if (!checkDrugAuto()) {
			TTabbedPane tabPanel = (TTabbedPane) this
					.getComponent("TTABPANELORDER");
			switch (tabPanel.getSelectedIndex()) {
			case 2:
				// ��ҩ
				odo.getOpdOrder().setFilter(
								"RX_TYPE='"+ MED+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			case 3:
				// ��ҩ
				odo.getOpdOrder().setFilter("RX_TYPE='"+ CHN
										+ "' AND ORDER_CODE <>'' AND #NEW#='Y'  AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			case 4:
				// ����ҩ
				odo.getOpdOrder().setFilter("RX_TYPE='"+ CTRL
										+ "' AND ORDER_CODE <>'' AND #NEW#='Y' AND #ACTIVE#='Y'");
				odo.getOpdOrder().filter();
				break;
			}
			String buff = odo.getOpdOrder().isFilter() ? odo.getOpdOrder().FILTER
					: odo.getOpdOrder().PRIMARY;
			int newDRow[] = odo.getOpdOrder().getNewRows(buff);
			for (int i : newDRow) {
				ordParm = odo.getOpdOrder().getRowParm(0);
				String order_code = ordParm.getValue("ORDER_CODE");
				if (!order_code.equals("")) {
					this.deleteorderAuto(0);
				}
			}
			return false;
		}
		for (int i : newRow) {
			ordParm = odo.getOpdOrder().getRowParm(i);
			String order_code = ordParm.getValue("ORDER_CODE");
			// ����ҽ��ܿؽӿ�
			if (getctrflg(order_code).equals("Y")) {
				TParm crtParm = new TParm();
				crtParm.setData("ADM_TYPE", admType);
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
						if (this.messageBox("��ʾ��Ϣ/Tip",
								 CTRPanelTool.getInstance()
												.selCTRPanel(crtParm).getValue(
														"MESSAGE") + ",��������?",
								0) != 0) {
							this.deleteorderAuto(i);
							return false;
						} else {
							this.messageBox(CTRPanelTool.getInstance()
									.selCTRPanel(crtParm).getValue("MESSAGE"));
						}
					} else {
						this.messageBox(CTRPanelTool.getInstance().selCTRPanel(
								crtParm).getValue("MESSAGE"));
						this.deleteorderAuto(i);
						return false;
					}

				}
			}
		}
		return true;
	}
	/**
	 * hl7������Ӳ�������
	 * 
	 * @param checkParm
	 *            TParm
	 */
	public void hl7Temp(TParm checkParm) {
		int count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (!checkParm.getValue("CAT1_TYPE", i).equals("RIS")
					|| !checkParm.getValue("CAT1_TYPE", i).equals("LIS")
					|| checkParm.getValue("ORDER_CODE", i).length() <= 0) {
				continue;
			}
			if (checkParm.getBoolean("SETMAIN_FLG", i)) {// ����ҽ������
				String orderCode = checkParm.getValue("ORDER_CODE", i);// ҽ������
				String orderSetGroupNo = checkParm.getValue(
						"ORDERSET_GROUP_NO", i);// ����ҽ����ţ���������ͬʱ����2����ͬ�ļ���ҽ����
				String rxNo = checkParm.getValue("RX_NO", i);// ����ǩ��
				for (int j = count - 1; j >= 0; j--) {
					// ����Ǽ���ҽ��ϸ��ɾ��
					if (!checkParm.getBoolean("SETMAIN_FLG", j)
							&& orderCode.equals(checkParm.getValue(
									"ORDERSET_CODE", j))
							&& orderSetGroupNo.equals(checkParm.getValue(
									"ORDERSET_GROUP_NO", j))
							&& rxNo.equals(checkParm.getValue("RX_NO", j))) {
						checkParm.removeRow(j);
					}
				}
			}
		}
		// System.out.println("checkParm:::::"+checkParm);
		TParm hl7ParmEnd = new TParm();
		// double sum = 0.0;
		int hl7Count = checkParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			if (checkParm.getData("ORDERSET_CODE", i).equals(
					checkParm.getValue("ORDER_CODE", i))
					&& checkParm.getValue("HIDE_FLG", i).equals("N")) {
				hl7ParmEnd.addData("ORDER_CAT1_CODE", checkParm.getData(
						"ORDER_CAT1_CODE", i));
				hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
						"TEMPORARY_FLG", i));
				hl7ParmEnd
						.addData("ADM_TYPE", checkParm.getData("ADM_TYPE", i));
				hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
				hl7ParmEnd.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
				hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
						"MED_APPLY_NO", i));
				hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData("CAT1_TYPE",
						i));
				hl7ParmEnd.addData("BILL_FLG", checkParm.getData("BILL_FLG", i));
			}
		}
		// �õ��շ���Ŀ
		sendHL7Parm = hl7ParmEnd;
	}
	/**
	 * ����ҽ������ϸ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm tableShow(TParm parm) {
		// ҽ������
		String orderCode = "";
		// ҽ�����
		int groupNo = -1;
		// ���㼯��ҽ�����ܷ���
		double fee = 0.0;
		// ҽ������
		int count = parm.getCount("ORDER_CODE");
		// ==================pangben modify 20110804 ɾ����ť��ʾ
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
					// ����Ҫɾ������
					removeRow[removeRowCount] = j;
					// �Լ�
					removeRowCount++;
				}
			}
			// ϸ����ð�����
			parm.setData("AR_AMT", i, fee);
		}
		// ɾ������ҽ��ϸ��=====pangben modify 20110801 ����ȥҽ��վ����ֱ�ӿ��Կ���ҽ���Ʒ�
		if (removeRowCount > 0) {
			for (int i = removeRowCount - 1; i >= 0; i--) {
				parm.removeRow(removeRow[i]);
			}
			// parm.setCount(parm.getCount() - removeRowCount);
		}
		// parm.setCount(parm.getCount() - removeRowCount);
		// ����table��ֵ����
		return parm;

	}

	/**
	 * ����HL7
	 */
	private void sendHL7Mes() {
		/**
		 * ����HL7��Ϣ
		 * 
		 * @param admType
		 *            String �ż�ס��
		 * @param catType
		 *            ҽ�����
		 * @param patName
		 *            ��������
		 * @param caseNo
		 *            String �����
		 * @param applictionNo
		 *            String �����
		 * @param flg
		 *            String ״̬(0,����1,ȡ��)
		 */
		int count = 0;
		if (null == sendHL7Parm||sendHL7Parm.getCount("ADM_TYPE")<=0) {
			return;
		} else {
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		}
		List list = new ArrayList();
		String patName = getValue("PAT_NAME").toString();
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			// System.out.println("�շ���Ŀ:"+temp);
			if (temp.getValue("TEMPORARY_FLG").length() == 0)
				continue;
			String admType = temp.getValue("ADM_TYPE");
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			if (temp.getValue("BILL_FLG").equals("N")) {// �˷�
				parm.setData("FLG", 1);
			}else{
				parm.setData("FLG", 0);
			}
			parm.setData("CASE_NO", opb.getReg().caseNo());
			parm.setData("LAB_NO", temp.getValue("MED_APPLY_NO"));
			parm.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
		}
		// System.out.println("���ͽӿ���Ŀ:"+list);
		if (list.size() <= 0) {
			return;
		}
		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
	}

	/**
	 * �Ƿ����ڱ���������ס����
	 * 
	 * @return boolean
	 */
	public boolean isMyPat() {
		if (odo == null) {
			// System.out.println("------------"+odo.getMrNo());
			return false;
		}
		TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
		// System.out.println("----------------------" + parm);
		String odo_type = "ODO";
		if ("E".equals(admType)) {
			odo_type = "ODE";
		}
		if (!odo_type.equals(parm.getValue("PRG_ID", 0))
				|| !(Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
				|| !(Operator.getID().equals(parm.getValue("OPT_USER", 0)))) {
			// this.messageBox(PatTool.getInstance().getLockParmString(pat.getMrNo()));
			return false;
		}
		return true;
	}
	
	/**
	 * ��ҽ�ƿ� =====pangben 2011-12-16 flg ��false û�����ִ�е�ҽ�� true ����ӻ�ɾ����ҽ��
	 * orderParm ��Ҫ������ҽ�� ��ɾ�� ����ӡ� �޸�
	 * 
	 * @return TParm
	 */
	public TParm onOpenCard(TParm orderOldParm, TParm orderParm) {
		if (odo == null) {
			return null;
		}
		TParm unParm = new TParm();
		if (orderOldParm == null) {
			this.messageBox("û����Ҫ������ҽ��");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		if(orderParm.getValue("OP_FLG").length()>0 && orderParm.getInt("OP_FLG")==5){
			this.messageBox("û����Ҫ������ҽ��");
			unParm.setData("OP_TYPE", 5);
			return unParm;
		}
		// ׼������ҽ�ƿ��ӿڵ�����
		// �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
		// ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
		TParm updateParm=orderParm.getParm("updateParm");
		boolean unFlg = updateOrderParm(updateParm, orderOldParm, unParm);
		TParm parm = new TParm();
		boolean isDelOrder = false;// ִ��ɾ��ҽ��
		//boolean exeDelOrder = false;// ִ��ɾ��ҽ��
		String delFlg=orderParm.getValue("DEL_FLG");
		// �����������ҽ��ɾ��Ҳ�����IS_NEW = false ״̬ ������Ҫ��ִ�з���ʱ�Ȳ�ѯ��ǰ����ҽ��
		// У���Ƿ���ɾ��������ӿ�
		if(delFlg.equals("Y")){
			isDelOrder = true;
		}
		orderParm.setData("BUSINESS_TYPE", "ODO");
		parm.setData("CASE_NO",reg.caseNo());
		orderParm.setData("REGION_CODE", Operator.getRegion());
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("IDNO", pat.getIdNo());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		orderParm.setData("INS_FLG", "N");// ҽ��������
		orderParm.setData("UN_FLG", unFlg ? "Y" : "N");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		orderParm.setData("unParm", unParm.getData());// ���ִ���޸ĵ�ҽ��
		if (null != orderOldParm.getValue("OPBEKTFEE_FLG")
				&& orderOldParm.getValue("OPBEKTFEE_FLG").equals("Y")) {
			orderParm.setData("OPBEKTFEE_FLG", "Y");
		}
		//ֱ���շѲ���������޸ĵ��շ�ҽ�� ����ִ��ȡ������
		if(null == orderOldParm.getValue("OPBEKTFEE_FLG")
				|| orderOldParm.getValue("OPBEKTFEE_FLG").length()<=0){
			if(unFlg)
				orderParm.setData("OPBEKTFEE_FLG", "Y");
		}

		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (null == ektReadParm || ektReadParm.getErrCode() < 0
				|| null == ektReadParm.getValue("MR_NO")) {
			parm.setData("OP_TYPE", 5);
			this.messageBox("ҽ�ƿ���������");
			this.setValue("LBL_EKT_MESSAGE", "δ����");//====pangben 2013-5-3��Ӷ���
			ekt_lable.setForeground(red);//======yanjing 2013-06-14���ö�����ɫ
			return parm;
		}else{
			this.setValue("LBL_EKT_MESSAGE", "�Ѷ���");//====pangben 2013-5-3��Ӷ���
			ekt_lable.setForeground(green);//======yanjing 2013-06-14���ö�����ɫ
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
					+ ektReadParm.getValue("PAT_NAME"));
			ektReadParm = null;
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		int type=0;
		//parm.setData("BILL_FLG", "Y");
		orderParm.setData("ektParm", ektReadParm.getData()); // ҽ�ƿ�����
		try {
			parm = EKTNewIO.getInstance().onOPDAccntClient(orderParm, odo.getCaseNo(),
					this);
		} catch (Exception e) {
			System.out.println("ҽ��վ�շѳ�������::::"+e.getMessage());
		}finally{
			type = parm.getInt("OP_TYPE");
			TParm delExeParm=orderParm.getParm("delExeParm");//�����շ�ɾ����ҽ��
			if (parm == null || type == 3 || type == -1 || type == 5) {
				if(delExeParm.getCount("ORDER_CODE")>0){
					concelDeleteOrder(delExeParm, orderOldParm, isDelOrder);// ɾ��ҽ��ѡ��ȡ������
					ektExeConcel=true;//ɾ����������
				}
			}
		}
		
		if (type == 6) {
			this.messageBox("û����Ҫ������ҽ��");
			parm.setData("OP_TYPE", 5);
			return parm;
		}
		if(null!=parm.getValue("OPD_UN_FLG") && parm.getValue("OPD_UN_FLG").equals("Y")){//�޸�ҽ������,���� �����շѽ��׺ŵ�����ҽ���˻���ȥ�����δ�շ�״̬
			orderParm.setData("newParm",parm.getParm("unParm").getData());
		}
		
		// �õ��շ���Ŀ
		sendHL7Parm = orderParm.getParm("hl7Parm");
		//hl7Temp(checkParm);
		// ɾ�����ݲ���ʱ��ʹ��ֻ���޸�ҽ������ʱʹ��
		parm.setData("orderParm", orderParm.getData());// ��Ҫ������ҽ��
		return parm;
	}

	/**
	 * ҽ�ƿ���������ɾ����ҽ�� =====pangben 2012-01-06
	 */
	private void concelDeleteOrder(TParm orderParm, TParm oldOrderParm,
			boolean exeDelOrder) {
		TParm tempParm = new TParm();
		int count = 0;
		// if (!exeDelOrder) {// ҽ�����ݲ�ͬ ����ȫ��ɾ����orderParmҽ�� û����ô������
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < oldOrderParm.getCount("ORDER_CODE"); j++) {
				if (oldOrderParm.getValue("SEQ_NO", j).equals(
						orderParm.getValue("SEQ_NO", i))
						&& oldOrderParm.getValue("RX_NO", j).equals(
								orderParm.getValue("RX_NO", i))
						&& !orderParm.getBoolean("BILL_FLG", i)) {
					tempParm.setRowData(count, oldOrderParm, j);
					count++;
				}
			}
		}
		orderParm = tempParm;
		if (orderParm.getCount("ORDER_CODE") > 0) {
			// �ж��Ƿ�����޸ĵ�ҽ����Ϣ
			for (int i = orderParm.getCount("ORDER_CODE") - 1; i >= 0; i--) {
				if (!orderParm.getBoolean("BILL_FLG", i)) {
					orderParm.setData("BILL_FLG", i, "Y");
					orderParm.setData("BILL_DATE", i, SystemTool.getInstance()
							.getDate());
					orderParm.setData("BILL_USER", i, Operator.getID());
				}
			}
			ektDeleteChackOut(orderParm);
			// System.out.println("concleDeleteorder�������Parm:::::"+orderParm);
			orderParm.setData("MED_FLG", "Y");// ҽ�ƿ�����������ִ�����MED_APPLY ������
			TParm result = TIOM_AppServer.executeAction("action.opd.ODOAction",
					"concleDeleteOrder", orderParm);
			if (result.getErrCode() < 0) {
				System.out.println("ҽ�ƿ�����ҽ������ʧ��");
			}
		} else {
			System.out.println("û��Ҫִ�е�ҽ������");
		}
	}

	/**
	 * ���ݶ����ֵ�ı��¼�
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            Object
	 */
	public void onSetItemEvent(String columnName, Object value) {
		if (!"MEDI_QTY".equalsIgnoreCase(columnName)
				&& !"TAKE_DAYS".equalsIgnoreCase(columnName)
				&& !"FREQ_CODE".equalsIgnoreCase(columnName))
			return;
		int rxType = odo.getOpdOrder().getItemInt(0, "RX_TYPE");
		String tableName = "", tagName = "";
		switch (rxType) {
		case 1:
			tableName = TABLE_MED;
			tagName = "MED_AMT";
			break;
		case 2:
			tableName = TABLE_CTRL;
			tagName = "CTRL_AMT";
			break;
		case 3:
			tableName = TABLE_CHN;
			tagName = "CHN_AMT";
			break;
		case 4:
			tableName = TABLE_OP;
			tagName = "OP_AMT";
			break;

		}
		this.calculateCash(tableName, tagName);

	}

	/**
	 * ��ӡ����
	 * 
	 * @return Object
	 */
	public Object onPrintCase() {
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("MR", "TEXT", "�����ţ�" + odo.getMrNo());
		if (isEng) {
			parm.setData("HOSP_NAME", "TEXT", Operator
							.getHospitalENGFullName());
		} else {
			parm.setData("HOSP_NAME", "TEXT", Operator
							.getHospitalCHNFullName());
		}
		parm.setData("DR_NAME", "TEXT", "ҽʦǩ��:"
				+ OpdRxSheetTool.getInstance().GetRealRegDr(odo.getCaseNo()));
		parm.setData("REALDEPT_CODE", this.realDeptCode);
		Object obj = new Object();
		if ("O".equals(admType)) {
			obj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010.jhw", parm,
					false);
			// ����EMR���� beign
			this.saveEMR(obj, "���ﲡ����¼", "EMR020001", "EMR02000106");
			// ����EMR���� end
		} else if ("E".equals(admType)) {
			//==========pangben 2013-7-22 ���ﲡ����ӡ������ʱ������Ϊ��ǰʱ�䣬����ʹ��
			parm.setData("DATE", StringTool.getString(TypeTool.getTimestamp(SystemTool.getInstance().getDate()),"yyyy/MM/dd"));
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG.jhw",
					parm, false);

		}

		return obj;
	}

	/**
	 * �ݴ棬��ӡ���á�������֪ͨ��
	 */
	public void onTempSave() {
		onTempSave(null);
	}

	/**
	 * ����󼴴�ӡ�����Ͳ�����¼
	 * 
	 * @param parm
	 *            TParm
	 * @param rxType
	 *            String
	 */
	public void onPrintOrder(TParm parm, String rxType) {
		if (parm == null || parm.getCount("RX_NO") < 1) {
			return;
		}
		int count = parm.getCount("RX_NO");
		if (CHN.equalsIgnoreCase(rxType)) {
			for (int i = 0; i < count; i++) {
				// ��ӡ����ǩ
				TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
						realDeptCode, rxType, odo, parm.getValue("RX_NO", i),
						"");
				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw",
						inParam, true);
				// ����EMR
				String rxNo = parm.getValue("RX_NO", i);
				// �ļ������봦��ǩ��
				this.saveEMR(obj, "��ҩ����ǩ_" + rxNo, "EMR030002", "EMR03000201");
			}
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		String filterString = order.getFilter();

		List<ArrayList<Object>> saveInfo = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < count; i++) {
			ArrayList<Object> data = new ArrayList<Object>();

			String rxNo = parm.getValue("RX_NO", i);
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
					realDeptCode, rxType, odo, rxNo,
					order.getItemString(0, "PSY_FLG"));
			if (CTRL.equalsIgnoreCase(rxType)) {
				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdDrugSheet.jhw", inParam,
						true);

				// ����EMR
				// �ļ������봦��ǩ��
				String fileName = "����ҩƷ����ǩ_" + rxNo;
				// this.saveEMR(obj, fileName, "EMR030001", "EMR03000102");

				data.add(obj);
				data.add(fileName);
				data.add("EMR030001");
				data.add("EMR03000102");
				saveInfo.add(data);
			} else {
				// =============modify by lim begin
				String rxNo2 = rxNo;
				String caseNo2 = this.caseNo;

				String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '��' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
						+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "
						+ " OPD_ORDER.SPECIFICATION cc, "
						+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN 'Ƥ��' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
						+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
						+ " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"
						+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "
						+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,"
						//modify by wanglong 20121226
						+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '�Ա�  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE,OPD_ORDER.DISCOUNT_RATE "
						//modify end
						+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "
						+ " WHERE       CASE_NO = '"
						+ caseNo2
						+ "'"
						+ "  AND RX_NO = '"
						+ rxNo2
						+ "'"
						+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
						+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
						+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
						+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
						+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
						+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
				TParm westResult = new TParm(TJDODBTool.getInstance().select(
						westsql));
				if (westResult.getErrCode() < 0) {
					this.messageBox("E0001");
					return;
				}
				if (westResult.getCount() < 0) {
					this.messageBox("û�д���ǩ����.");
					return;
				}

				TParm westParm = new TParm();
				double pageAmt2 = 0;
				DecimalFormat df2 = new DecimalFormat("############0.00");
				for (int j = 0; j < westResult.getCount(); j++) {
					westParm.addData("AA", westResult.getData("AA", j));
					westParm.addData("BB", westResult.getData("BB", j));
					westParm.addData("CC", westResult.getData("CC", j));
					westParm.addData("DD", westResult.getData("DD", j));
					westParm.addData("EE", westResult.getData("EE", j));
					westParm.addData("FF", westResult.getData("FF", j));
					westParm.addData("ER", westResult.getData("ER", j));
					westParm.addData("GG", westResult.getData("GG", j));
					pageAmt2 += (westResult.getDouble("DOSAGE_QTY", j)
							* westResult.getDouble("OWN_PRICE", j) 
							* westResult.getDouble("DISCOUNT_RATE", j));// modify by wanglong 20121226
					pageAmt2 = StringTool.round(pageAmt2, 2);// add by wanglong 20121226
					if ((j != 0 && (j + 1) % 5 == 0)
							|| j == westResult.getCount() - 1) {
						westParm.addData("AA", "");
						westParm.addData("BB", "");
						westParm.addData("CC", "");
						westParm.addData("DD", "");
						westParm.addData("EE", "");
						westParm.addData("FF", "�������(��):");
						westParm.addData("ER", df2.format(pageAmt2));
						westParm.addData("GG", "");
						pageAmt2 = 0;
					}
				}
				westParm.setCount(westParm.getCount("AA"));
				westParm.addData("SYSTEM", "COLUMNS", "AA");
				westParm.addData("SYSTEM", "COLUMNS", "BB");
				westParm.addData("SYSTEM", "COLUMNS", "CC");
				westParm.addData("SYSTEM", "COLUMNS", "DD");
				westParm.addData("SYSTEM", "COLUMNS", "EE");
				westParm.addData("SYSTEM", "COLUMNS", "FF");
				westParm.addData("SYSTEM", "COLUMNS", "ER");
				westParm.addData("SYSTEM", "COLUMNS", "GG");

				inParam.setData("ORDER_TABLE", westParm.getData());
				// =============modify by lim end
				Object obj = this.openPrintDialog(
						"%ROOT%\\config\\prt\\OPD\\OpdOrderSheet.jhw", inParam,
						true);
				// // ����EMR
				// // �ļ������봦��ǩ��
				String fileName = "��ҩ����ǩ_" + rxNo;
				// this.saveEMR(obj, fileName, "EMR030001", "EMR03000101");
				data.add(obj);
				data.add(fileName);
				data.add("EMR030001");
				data.add("EMR03000101");
				saveInfo.add(data);
			}
		}

		order.setFilter(filterString);
		order.filter();
		for (Iterator iterator = saveInfo.iterator(); iterator.hasNext();) {
			ArrayList<Object> arrayList = (ArrayList<Object>) iterator.next();
			this.saveEMR(arrayList.get(0), (String) arrayList.get(1),
					(String) arrayList.get(2), (String) arrayList.get(3));
		}
	}

	/**
	 * �ݴ��ӡ���á�������֪ͨ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onPrintExa(TParm parm) {
		TParm inParam = new TParm();
		inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
				realDeptCode, EXA, odo);

		// modify by lim 2012/02/23 begin
		String rxNo = inParam.getValue("RX_NO");
		String caseNo = this.caseNo;
		//modify by wanglong 20121226
		String sql = "SELECT  A.MED_APPLY_NO,F.OWN_PRICE,A.ORDER_CODE,CASE WHEN A.BILL_FLG='Y' THEN '��' ELSE '' END AS BILL_FLG,  C.DEPT_CHN_DESC,F.AR_AMT, "////caowl 20131104	ADD OWN_PRICE	 	    
			+" A.ORDER_DESC, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '��' ELSE '' END AS URGENT_FLG,C.DESCRIPTION ,A.DISCOUNT_RATE"  //huangtt 20121127 ��A.DR_NOTE AS DESCRIPTION��Ϊc.DESCRIPTION
		//modify end
			    + " FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C, (SELECT   RX_NO, ORDERSET_GROUP_NO, SUM (AR_AMT) AS AR_AMT,SUM(OWN_PRICE) AS OWN_PRICE, CASE_NO "//caowl 20131104	ADD OWN_PRICE
                + "FROM OPD_ORDER "
                + "WHERE CASE_NO = '"+caseNo+"' "
                + "AND RX_NO IN ("+rxNo+") "
                + "AND SETMAIN_FLG = 'N' "
                + "AND ORDERSET_GROUP_NO IN ( "
                + "  SELECT A.ORDERSET_GROUP_NO "
                + "    FROM OPD_ORDER A, SYS_FEE B, SYS_DEPT C "
                + "    WHERE A.CASE_NO = '"+caseNo+"' "
                + "      AND RX_NO IN ("+rxNo+") "
                + "       AND A.ORDER_CODE = B.ORDER_CODE "
                + "       AND C.DEPT_CODE = A.EXEC_DEPT_CODE "
                + "       AND A.CAT1_TYPE = 'LIS') "
                + " GROUP BY RX_NO, ORDERSET_GROUP_NO, CASE_NO) F"
				+ " WHERE A.CASE_NO='"+ caseNo+ "' AND A.RX_NO IN ("+ rxNo
				+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.CASE_NO = F.CASE_NO AND A.RX_NO = F.RX_NO AND A.ORDERSET_GROUP_NO = F.ORDERSET_GROUP_NO AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND A.CAT1_TYPE='LIS'";
		//System.out.println("������֪ͨ��sql:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		//===pangben 2013-8-8 ��鲻��ӡ
//		String sql2 = sql.replace("'LIS'", "'RIS'");
//
//		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result.getErrCode() < 0) {
			return;
		}
		if (result.getCount() < 0) {
			return;
		}
//		if (result.getErrCode() < 0 || result2.getErrCode() < 0) {
//			return;
//		}
//		if (result.getCount() < 0 && result2.getCount() < 0) {
//			return;
//		}
		TParm data2 = new TParm();
		boolean flg = false;
		int blankRow = 0;
		double pageAmt = 0;
		DecimalFormat df = new DecimalFormat("############0.00");
		if (result.getCount() > 0) {
			int pageCount = 11;
			data2.addData("BILL_FLG", "");
			data2.addData("DEPT_CHN_DESC", "����֪ͨ��");
			data2.addData("ORDER_DESC", "");
			data2.addData("MEDI_QTY", "");
			data2.addData("URGENT_FLG", "");
			data2.addData("DESCRIPTION", "");
			data2.addData("MED_APPLY_NO", "");
			for (int i = 0; i < result.getCount(); i++) {
				data2.addData("BILL_FLG", result.getData("BILL_FLG", i));
				data2.addData("DEPT_CHN_DESC", result.getData("DEPT_CHN_DESC",
						i));
				data2.addData("ORDER_DESC", result.getData("ORDER_DESC", i));
				double own_price = StringTool.round(result.getDouble("OWN_PRICE", i), 3);
				DecimalFormat df3 = new DecimalFormat("############0.000"); 
				//add by huangtt 20121127
				String mediQty = result.getValue("MEDI_QTY", i).substring(0, result.getValue("MEDI_QTY", i).length()-2);
				String ownPrice = df3.format(own_price);
//				StringTool.fillLeft(mediQty, 3-mediQty.length(), " ");  9  4
//				StringTool.fillLeft(ownPrice, 8-ownPrice.length(), " ")
				data2.addData("MEDI_QTY", StringTool.fillLeft(mediQty, 4-mediQty.length(), " ")+StringTool.fill(" ", 14-ownPrice.length())+ownPrice);//caowl 20131104
				data2.addData("URGENT_FLG", result.getData("URGENT_FLG", i));
				data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i));
				data2
						.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO",
								i));
				// �ۼ�
				
//				pageAmt += (result.getDouble("MEDI_QTY", i)
//						* this.getEveryAmt(result.getValue("ORDER_CODE", i)) 
//						* result.getDouble("DISCOUNT_RATE", i));// modify by wanglong 20121226
				pageAmt += StringTool.round(result.getDouble("AR_AMT", i), 2);// pangben 2013-4-17
				// TODO:��###########������Ҫ��ÿҳ����Ľ�����.�û�õ�����*getEveryAmt(ORDERCODE)�����ÿ����¼�Ľ�
				int num = i + blankRow + 1 + 1;// ������i��+
				// �հ���(blankRow)+��һ�м���֪ͨ��(1)+1
				if (!flg) {// ��һҳ
					if (i == 8 || i == (result.getCount() - 1)) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						flg = true;
						blankRow++;
						pageAmt = 0;
					}

				} else {// ����ҳ.//��11����ʾ���
					if (i == result.getCount() - 1) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						blankRow++;
						pageAmt = 0;
					} else if (i != result.getCount() - 1
							&& ((num % 11) + 1) == 11) {
						data2.addData("BILL_FLG", "");
						data2.addData("DEPT_CHN_DESC", "");
						data2.addData("ORDER_DESC", "");
						data2.addData("MEDI_QTY", "");
						data2.addData("URGENT_FLG", "");
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						blankRow++;
						pageAmt = 0;
					}
				}
			}
			int resultLen1 = result.getCount() + 1 + blankRow;
			int len = (resultLen1 <= pageCount) ? (pageCount - resultLen1)
					: ((resultLen1 % pageCount == 0) ? 0
							: (((resultLen1 / pageCount) + 1) * pageCount - resultLen1));

			for (int i = 1; i <= len; i++) {
				data2.addData("BILL_FLG", "");
				data2.addData("DEPT_CHN_DESC", "");
				data2.addData("ORDER_DESC", "");
				data2.addData("MEDI_QTY", "");
				data2.addData("URGENT_FLG", "");
				data2.addData("DESCRIPTION", "");
				data2.addData("MED_APPLY_NO", "");
			}
		}
//		if (result2.getCount() > 0) {
//			data2.addData("BILL_FLG", "");
//			data2.addData("DEPT_CHN_DESC", "���֪ͨ��");
//			data2.addData("ORDER_DESC", "");
//			data2.addData("MEDI_QTY", "");
//			data2.addData("URGENT_FLG", "");
//			data2.addData("DESCRIPTION", "");
//			data2.addData("MED_APPLY_NO", "");
//		}
		blankRow = 0;
		flg = false;
//		for (int i = 0; i < result2.getCount(); i++) {
//			data2.addData("BILL_FLG", result2.getData("BILL_FLG", i));
//			data2.addData("DEPT_CHN_DESC", result2.getData("DEPT_CHN_DESC", i));
//			data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i));
//			data2.addData("MEDI_QTY", result2.getData("MEDI_QTY", i));
//			data2.addData("URGENT_FLG", result2.getData("URGENT_FLG", i));
//			data2.addData("DESCRIPTION", result2.getData("DESCRIPTION", i));
//			data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i));
////			pageAmt += (result2.getDouble("MEDI_QTY", i)
////					* this.getEveryAmt(result2.getValue("ORDER_CODE", i)) 
////					* result2.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
//			pageAmt += StringTool.round(result2.getDouble("AR_AMT", i), 2);//add by wanglong 20121226
//			int num = i + blankRow + 1 + 1;// ������i��+ �հ���(blankRow)+��һ�м���֪ͨ��(1)+1
//			if (!flg) {// ��һҳ
//				if (i == 6 || i == (result2.getCount() - 1)) {
//					data2.addData("BILL_FLG", "");
//					data2.addData("DEPT_CHN_DESC", "");
//					data2.addData("ORDER_DESC", "");
//					data2.addData("MEDI_QTY", "");
//					data2.addData("URGENT_FLG", "");
//					data2.addData("DESCRIPTION", "�������:");
//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
//					flg = true;
//					blankRow++;
//					pageAmt = 0;
//				}
//
//			} else {// ����ҳ.//��9����ʾ���
//				if (i == result2.getCount() - 1) {
//					data2.addData("BILL_FLG", "");
//					data2.addData("DEPT_CHN_DESC", "");
//					data2.addData("ORDER_DESC", "");
//					data2.addData("MEDI_QTY", "");
//					data2.addData("URGENT_FLG", "");
//					data2.addData("DESCRIPTION", "�������:");
//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
//					blankRow++;
//					pageAmt = 0;
//				} else if (i != result2.getCount() - 1 && ((num % 9) + 1) == 9) {
//					data2.addData("BILL_FLG", "");
//					data2.addData("DEPT_CHN_DESC", "");
//					data2.addData("ORDER_DESC", "");
//					data2.addData("MEDI_QTY", "");
//					data2.addData("URGENT_FLG", "");
//					data2.addData("DESCRIPTION", "�������:");
//					data2.addData("MED_APPLY_NO", df.format(pageAmt));
//					blankRow++;
//					pageAmt = 0;
//				}
//			}
//		}
		data2.setCount(data2.getCount("ORDER_DESC"));
		data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
		inParam.setData("ORDER_TABLE", data2.getData());
		// modify by lim 2012/02/23 begin
		DText text = (DText) this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\opdddddd.jhw", inParam, true);
		String rx_no = inParam.getValue("RX_NO");
		// ���봦��ǩ��
		String fileName = "������֪ͨ��_" + rx_no;
		this.saveEMR(text, fileName, "EMR040001", "EMR04000142");
	}

	private double getEveryAmt(String opdOrderCode) {
		String sql = "SELECT SUM(O.DOSAGE_QTY * F.OWN_PRICE) AS AMT FROM SYS_ORDERSETDETAIL O,SYS_FEE F WHERE O.ORDER_CODE = F.ORDER_CODE AND O.ORDERSET_CODE = '"
				+ opdOrderCode + "'";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
		if (result2.getErrCode() < 0) {
			messageBox("��ҳ�������������.");
			return 0;
		}
		if (result2.getCount() <= 0) {
			return 0;
		}
		return result2.getDouble("AMT", 0);
	}

	/**
	 * �ݴ��ӡ���á�������֪ͨ��
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onPrintOp(TParm parm) {
		TParm inParam = new TParm();
		inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
				realDeptCode, OP, odo);
		// modify by liming 2012/02/23 begin
		String rxNo1 = inParam.getValue("RX_NO");
		String caseNo1 = this.caseNo;

		TParm dataParm = new TParm();
		if (rxNo1 != null && rxNo1.trim().length() > 0) {
			//modify by wanglong 20121226
			String sql = "SELECT  A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '��' ELSE '' END ,  C.DEPT_CHN_DESC,  A.ORDER_DESC||CASE WHEN A.DR_NOTE IS NOT NULL THEN '/'||A.DR_NOTE ELSE '' END, A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '��' ELSE '' END,B.DESCRIPTION,A.DISCOUNT_RATE"
			//modify end
			    	+ "   FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C"
					+ "	WHERE A.CASE_NO='"
					+ caseNo1
					+ "' AND A.RX_NO IN ("
					+ rxNo1
					+ ") AND A.SETMAIN_FLG='Y' AND A.ORDER_CODE=B.ORDER_CODE AND C.DEPT_CODE=A.EXEC_DEPT_CODE";
			if ("5".equalsIgnoreCase(rxType)) {
				dataParm = new TParm(TJDODBTool.getInstance().select(sql));
			} else {
				//modify by wanglong 20121226
				sql = "SELECT   A.ORDER_CODE,A.DOSAGE_QTY,A.OWN_PRICE,CASE WHEN A.BILL_FLG='Y' THEN '��' ELSE '' END AS BILL_FLG, C.DEPT_CHN_DESC,  A.ORDER_DESC||CASE WHEN A.SPECIFICATION IS NOT NULL THEN '/'||A.SPECIFICATION ELSE '' END AS ORDER_DESC,  A.MEDI_QTY,CASE WHEN A.URGENT_FLG='Y' THEN '��' ELSE '' END AS URGENT_FLG,B.DESCRIPTION,A.DISCOUNT_RATE"
				//modify end
				    	+ "   FROM  OPD_ORDER A,SYS_FEE B,SYS_DEPT C"
						+ "	WHERE A.CASE_NO='"
						+ caseNo1
						+ "' AND A.RX_NO IN ("
						+ rxNo1
						+ ") AND A.ORDER_CODE=B.ORDER_CODE AND C.DEPT_CODE=A.EXEC_DEPT_CODE AND (A.ORDERSET_CODE IS NULL OR A.SETMAIN_FLG ='Y')";
				dataParm = new TParm(TJDODBTool.getInstance().select(sql));
			}
		}
		if (dataParm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		if (dataParm.getCount() < 0) {
			this.messageBox("û�д���֪ͨ������.");
			return;
		}

		TParm myParm = new TParm();
		boolean flg1 = false;
		int blankRow1 = 0;
		double pageAmt1 = 0;
		DecimalFormat df1 = new DecimalFormat("############0.00");
		for (int i = 0; i < dataParm.getCount(); i++) {
			String orderDesc = dataParm.getValue("ORDER_DESC", i);
			if (orderDesc.length() <= 29) {
				StringBuilder temp = new StringBuilder();
				for (int j = 1; j <= 58 - orderDesc.length(); j++) {
					temp.append(" ");
				}
				orderDesc = dataParm.getValue("ORDER_DESC", i)
						+ temp.toString();
			}
			myParm.addData("BILL_FLG", dataParm.getData("BILL_FLG", i));
			myParm.addData("DEPT_CHN_DESC", dataParm
					.getData("DEPT_CHN_DESC", i));
			myParm.addData("ORDER_DESC", orderDesc);
			myParm.addData("MEDI_QTY", dataParm.getData("MEDI_QTY", i));
			myParm.addData("URGENT_FLG", dataParm.getData("URGENT_FLG", i));
			myParm.addData("DESCRIPTION", dataParm.getData("DESCRIPTION", i));
			// �ۼ�
			pageAmt1 += (dataParm.getDouble("DOSAGE_QTY", i)
					* dataParm.getDouble("OWN_PRICE", i) 
					* dataParm.getDouble("DISCOUNT_RATE", i));//modify by wanglong 20121226
			pageAmt1 = StringTool.round(pageAmt1, 2);//add by wanglong 20121226
			// TODO:��###########������Ҫ��ÿҳ����Ľ�����.�û�õ�����*getEveryAmt(ORDERCODE)�����ÿ����¼�Ľ�
			int num = i + blankRow1 + 1;// ������i��+ �հ���(blankRow)+1
			if (!flg1) {// ��һҳ
				if (i == 4 || i == (dataParm.getCount() - 1)) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "�������:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					flg1 = true;
					blankRow1++;
					pageAmt1 = 0;
				}

			} else {// ����ҳ.//��5����ʾ���
				if (i == dataParm.getCount() - 1) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "�������:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					blankRow1++;
					pageAmt1 = 0;
				} else if (i != dataParm.getCount() - 1 && ((num % 6) + 1) == 6) {
					myParm.addData("BILL_FLG", "");
					myParm.addData("DEPT_CHN_DESC", "");
					myParm.addData("ORDER_DESC", "");
					myParm.addData("MEDI_QTY", "");
					myParm.addData("URGENT_FLG", "�������:");
					myParm.addData("DESCRIPTION", df1.format(pageAmt1));
					blankRow1++;
					pageAmt1 = 0;
				}
			}
		}
		myParm.setCount(myParm.getCount("ORDER_DESC"));
		myParm.addData("SYSTEM", "COLUMNS", "BILL_FLG");
		myParm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		myParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		myParm.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		myParm.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
		myParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		inParam.setData("ORDER_TABLE", myParm.getData());
		// modify by liming 2012/02/23 end

		DText text = (DText) this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\OpdNewHandleSheet.jhw", inParam,
				true);
		// ���봦��ǩ��
		String fileName = "����֪ͨ��_" + rxNo1;
		this.saveEMR(text, fileName, "EMR040002", "EMR04000203");
	}

	/**
	 * ���ò������
	 */
	public void onCaseSheet() {
		TParm inParm = new TParm();
		if (odo == null || pat == null)
			return;
		inParm.setData("MR_NO", odo.getMrNo());
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.setData("DEPT_CODE", realDeptCode);
		if ("en".equals(this.getLanguage())) // �ж��Ƿ���Ӣ�Ľ���
			inParm.setData("PAT_NAME", pat.getName1());
		else
			inParm.setData("PAT_NAME", pat.getName());
		inParm.setData("OPD_ORDER", odo.getOpdOrder());
		inParm.setData("ADM_DATE", reg.getAdmDate());
		inParm.setData("ODO", odo);
		int[] mainDiag = new int[1];
		if (odo.getDiagrec().haveMainDiag(mainDiag)) {
			String icdCode = odo.getDiagrec().getItemString(mainDiag[0],
					"ICD_CODE");
			inParm.setData("ICD_CODE", icdCode);
			inParm.setData("ICD_DESC", odo.getDiagrec().getIcdDesc(icdCode,
					this.getLanguage()));
		}
		this.openDialog("%ROOT%\\config\\opd\\ODOCaseSheet.x", inParm, false);
	}

	/**
	 * �������߿���
	 */
	public void saveSubjrec() {
		odo.getSubjrec().setActive(0, true);
		odo.getSubjrec().setItem(0, "SUBJ_TEXT", word.getCaptureValue("SUB"));
		odo.getSubjrec().setItem(0, "OBJ_TEXT", word.getCaptureValue("OBJ"));
		odo.getSubjrec()
				.setItem(0, "PHYSEXAM_REC", word.getCaptureValue("PHY"));
		odo.getSubjrec().setItem(0, "PROPOSAL",
				word.getCaptureValue("PROPOSAL"));
		odo.getSubjrec().setItem(0, "EXA_RESULT",
				word.getCaptureValue("EXA_RESULT"));
		if ("N".equalsIgnoreCase(odo.getRegPatAdm().getItemString(0,
				"SEE_DR_FLG"))) {
			TParm parm = EmrUtil.getInstance().saveGSFile(odo.getMrNo(),
					odo.getCaseNo(), saveFiles[2], saveFiles[1]);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			String path = parm.getValue("PATH");
			String fileName = parm.getValue("FILENAME");
			word.setMessageBoxSwitch(false);
			word.onSaveAs(path, fileName, 3);
			saveFiles = EmrUtil.getInstance().getGSFile(odo.getCaseNo());
		} else {
			if (this.saveFiles != null) {
				word.setMessageBoxSwitch(false);
				word.onSaveAs(saveFiles[0], saveFiles[1], 3);
			}
			word.setCanEdit(true);
		}

	}

	/**
	 * ����reg����
	 */
	public void saveRegInfo() {
		PatInfo patInfo = odo.getPatInfo();
		RegPatAdm regPatAdm = odo.getRegPatAdm();
		patInfo.setActive(0, true);
		regPatAdm.setActive(0, true);
		patInfo.setItem(0, "PAT1_CODE", this.getValue("PAT1_CODE"));
		patInfo.setItem(0, "PAT2_CODE", this.getValue("PAT2_CODE"));
		patInfo.setItem(0, "PAT3_CODE", this.getValue("PAT3_CODE"));
		patInfo.setItem(0, "PREMATURE_FLG", this.getValue("PREMATURE_FLG"));
		patInfo.setItem(0, "HANDICAP_FLG", this.getValue("HANDICAP_FLG"));
		patInfo.setItem(0, "LMP_DATE", this.getValue("LMP_DATE"));
		patInfo.setItem(0, "BREASTFEED_STARTDATE", this
				.getValue("BREASTFEED_STARTDATE"));
		patInfo.setItem(0, "BREASTFEED_ENDDATE", this
				.getValue("BREASTFEED_ENDDATE"));
		String nowValue = familyWord.getCaptureValue("FAMILY_HISTORY");
		String oldValue = patInfo.getItemString(0, "FAMILY_HISTORY");
		if (!nowValue.equalsIgnoreCase(oldValue)) {
			// 2:δ�����
			if ("2".equalsIgnoreCase(this.familyHisFiles[2])) {
				familyWord.setMessageBoxSwitch(false);
				TParm familyParm = EmrUtil.getInstance()
						.getFamilyHistorySavePath(odo.getCaseNo(),
								odo.getMrNo(), familyHisFiles[3],
								familyHisFiles[1]);
				familyWord.onSaveAs(familyParm.getValue("PATH"), familyParm
						.getValue("FILENAME"), 3);
				familyWord.setCanEdit(true);
			}
			// 3:�����
			else {
				familyWord.setMessageBoxSwitch(false);
				familyWord.onSaveAs(familyHisFiles[0], familyHisFiles[1], 3);
				familyWord.setCanEdit(true);
			}
		}
		patInfo.setItem(0, "FAMILY_HISTORY", this.familyWord
				.getCaptureValue("FAMILY_HISTORY"));
		regPatAdm.setItem(0, "CTZ1_CODE", this.getValue("CTZ1_CODE"));
		regPatAdm.setItem(0, "CTZ2_CODE", this.getValue("CTZ2_CODE"));
		regPatAdm.setItem(0, "CTZ3_CODE", this.getValue("CTZ3_CODE"));
		regPatAdm.setItem(0, "WEIGHT", TypeTool.getDouble(this
				.getValue("WEIGHT")));
		//zhangp 20131206
		String regionCode = Operator.getRegion();
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		if((!aheadFlg) && (EKTIO.getInstance().ektAyhSwitch() && !(null == ektReadParm || ektReadParm.getValue("MR_NO").length() <= 0))){
			TParm result = EKTpreDebtTool.getInstance().insertOpdClinicFee(caseNo, regionCode, optUser, optTerm);
			if(result.getErrCode()<0){
				messageBox("���Ѳ���ʧ��");
			}
		}
	}

	/**
	 * 
	 * @param rxKind
	 *            String ��������
	 * @param tableName
	 *            String TABLE��
	 */
	public void onEditAll(String rxKind, String tableName) {
		TParm msg = (TParm) this
				.openDialog("%ROOT%\\config\\opd\\OPDEditAll.x");
		if (msg == null) {
			return;
		}
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		if (null == ektReadParm) {
			ektReadParm = EKTIO.getInstance().TXreadEKT();
			if (ektReadParm.getErrCode() < 0) {
				this.messageBox("δȷ�����,����ɾ��ҽ��");
				return;
			}
		}
		String freqCode = msg.getValue("FREQ_CODE");
		String routeCode = msg.getValue("ROUTE_CODE");
		double mediQty = msg.getDouble("MEDI_QTY");
		double takeDays = msg.getDouble("TAKE_DAYS");
		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = (OpdOrder) table.getDataStore();
		if (rxKind.equals("MED_RX")) {//====pangben 2014-1-15 
			for (int i = 0; i < order.rowCount(); i++) {
				if (checkPha(order, i, "MED",true))//����ҽ��У�鴦��ǩ�Ƿ���Կ���
					return ;
			}
		}
		// order.setFilter("RX_NO='" +rxNo+
		// "'");
		// order.filter();

		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_CODE"))) {
				continue;
			}
			if ("E".equals(order.getItemString(i, "BILL_TYPE"))
					&& (StringTool.getBoolean(order
							.getItemString(i, "EXEC_FLG"))
							|| StringTool.getBoolean(order.getItemString(i,
									"PRINT_FLG")) || StringTool
							.getBoolean(order.getItemString(i, "BILL_FLG")))) {
				this.messageBox("E0055"); // �ѼƷ�ҽ������ɾ��
				return;
			} else {
				if (StringTool.getBoolean(order.getItemString(i, "BILL_FLG"))
						&& !"E".equals(order.getItemString(i, "BILL_TYPE"))) {
					this.messageBox("E0055"); // �ѼƷ�ҽ������ɾ��
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
	 * ������ҩ��ť
	 */
	public void onResonablemed() {
		if (!passIsReady) {
			this.messageBox("E0067");
			return;
		}
		if (!initReasonbledMed()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		if (odo == null) {
			this.messageBox("E0024");
			return;
		}
		if (((TTabbedPane) this.getComponent("TTABPANELORDER"))
				.getSelectedIndex() == 0
				|| ((TTabbedPane) this.getComponent("TTABPANELORDER"))
						.getSelectedIndex() == 1) {
			this.messageBox("�����ú�����ҩ����!");
			return;
		}
		TParm parm = setopdRecipeInfo();
		if (parm.getCount("ERR") > 0) {
			this.messageBox("E0068");
			return;
		}
		if (parm.getCount("ORDER_CODE") < 0) {
			this.messageBox("δ��⵽ҩƷ��");
			return;
		}
		// ����ʹ��
		PassDriver.PassDoCommand(3);
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			PassDriver.PassGetWarn1(parm.getValue("SEQ", i) + "");
		}
	}

	/**
	 * 
	 * �Զ���������ҩ
	 * 
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!initReasonbledMed()) {
			return true;
		}
		long time = System.currentTimeMillis();
		PassTool.getInstance().setPatientInfo(odo.getCaseNo());
		PassTool.getInstance().setAllergenInfo(odo.getMrNo());
		PassTool.getInstance().setMedCond(odo.getCaseNo());
		TParm parm = setopdRecipeInfoAuto();
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "��ҩƷʹ�ò�����,�Ƿ�浵?", "��Ϣ",
				JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * ����ҽ�����ҩƷ�Զ�
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfoAuto() {
		TParm parm = setopdRecipeInfo();
		PassDriver.PassDoCommand(33);
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			result.addData("FLG", PassDriver.PassGetWarn1(parm.getValue("SEQ",
					i)));
		}
		return result;

	}

	/**
	 * ��������ҽ��ҩƷ��Ϣ
	 * 
	 * @return TParm
	 */
	public TParm setopdRecipeInfo() {
		TParm parm = new TParm();
		int j;
		OpdOrder order = odo.getOpdOrder();
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		case 2:
			// ��ҩ
			order.setFilter("RX_TYPE='" + MED + "'");
			order.filter();
			break;
		case 3:

			// ��ҩ
			order.setFilter("RX_TYPE='" + CHN + "'");
			order.filter();
			break;
		case 4:

			// ����ҩ
			order.setFilter("RX_TYPE='" + CTRL + "'");
			order.filter();
			break;
		default:
			break;
		}
		String[] orderInfo;
		ReasonableMedUtil resonableMed = new ReasonableMedUtil();
		for (int i = 0; i < order.rowCount(); i++) {
			if (StringUtil.isNullString(order.getItemString(i, "ORDER_DESC"))) {
				continue;
			}
			orderInfo = resonableMed.getOrderInfo(order, i);
			j = PassDriver.PassSetRecipeInfo(orderInfo[0], orderInfo[1],
					orderInfo[2], orderInfo[3], orderInfo[4], orderInfo[5],
					orderInfo[6], orderInfo[7], orderInfo[8], orderInfo[9],
					orderInfo[10], orderInfo[11]);
			if (j != 1) {
				parm.addData("ERR", orderInfo[0]);
				break;
			} else {
				parm.addData("SEQ", orderInfo[0]);
				parm.addData("ORDER_CODE", orderInfo[1]);
			}
		}
		return parm;
	}

	/**
	 * ɾ��������
	 * 
	 * @param row
	 */
	private void deleteorderAuto(int row) {
		TTable table = null;
		OpdOrder order = odo.getOpdOrder();
		String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
		String rx_no = "";
		TTabbedPane tabPanel = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		switch (tabPanel.getSelectedIndex()) {
		// ������
		case 0:
			table = (TTable) this.getComponent("TABLEEXA");
			rx_no = this.getValueString("EXA_RX");
			order.deleteRow(row, buff);
			table.setFilter("RX_TYPE='" + EXA
					+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
			table.filter();
			table.setDSValue();
			// ����
		case 1:
			table = (TTable) this.getComponent("TABLEOP");
			rx_no = this.getValueString("OP_RX");
			order.deleteRow(row, buff);
			table.setFilter("RX_TYPE='" + OP + "' AND HIDE_FLG='N' AND RX_NO='"
					+ rx_no + "'");
			table.filter();
			table.setDSValue();
		case 2:

			// ��ҩ
			table = (TTable) this.getComponent("TABLEMED");
			rx_no = this.getValueString("MED_RX");
			order.deleteRow(row);
			order.setFilter("RX_TYPE='" + MED
					+ "' AND HIDE_FLG='N' AND RX_NO='" + rx_no + "'");
			table.filter();
			table.setDSValue();
			break;
		case 3:

			// ��ҩ
			table = (TTable) this.getComponent("TABLECHN");
			rx_no = this.getValueString("CHN_RX");
			order.setFilter("RX_TYPE='" + CHN + "'");
			order.filter();
			order.deleteRow(row);
			order.newOrder(CHN, rx_no);
			int totRow = order.rowCount();
			if (!StringUtil.isNullString(order.getItemString(totRow - 1,
					"ORDER_CODE"))
					|| totRow % 4 != 0 || totRow < 1) {
				for (int a = 0; a < 4 - totRow % 4; a++) {
					order.setItem(a, "PHA_TYPE", "G");
				}
			}
			TParm parm = odo.getOpdOrder().getBuffer(order.PRIMARY);
			TParm tableParm = new TParm();
			for (int j = 0; j < parm.getCount(); j++) {
				int idx = j % 4 + 1;
				tableParm.setData("ORDER_DESC" + idx, 1, parm.getValue(
						"ORDER_DESC", j));
				tableParm.setData("MEDI_QTY" + idx, 1, parm.getDouble(
						"MEDI_QTY", j));
				tableParm.setData("DCTEXCEP_CODE" + idx, 1, parm.getValue(
						"DCTEXCEP_CODE", j));
			}
			tableParm.setCount(1);
			callFunction("UI|TABLECHN|setParmValue", tableParm);
			break;
		case 4:

			// ����ҩ
			table = (TTable) this.getComponent("TABLECTRL");
			rx_no = this.getValueString("CTRL_RX");
			order.setFilter("RX_TYPE='" + CTRL + "'");
			order.deleteRow(row, buff);
			table.filter();
			table.setDSValue();
			break;
		default:
			break;
		}
		return;
	}

	/**
	 * isWarn
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean isWarn(TParm parm) {
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
	 * getWarn
	 * 
	 * @param flg
	 *            int
	 * @return boolean
	 */
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
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 *            String
	 */
	public void showPopMenu(String tableName) {
        TTable table = (TTable) this.getComponent(tableName);
        if (TABLE_EXA.equalsIgnoreCase(tableName)) {
            table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��|Show LIS/RIS Detail Items,onOrderSetShow");
            return;
        }
        if (TABLE_OP.equalsIgnoreCase(tableName)) {
            table.setPopupMenuSyntax("��ʾ������Ŀϸ��|Show Treatment Detail Items,onOpShow");
            return;
        }
        if (TABLE_MED.equalsIgnoreCase(tableName) || TABLE_CTRL.equalsIgnoreCase(tableName)) {// modify by wanglong 20130522
            table.setPopupMenuSyntax("��ʾҩ����Ϣ|Show Med Info,onSysFeeShow;��ʾ������ҩ��Ϣ|Show Rational Drug Use,onQueryRationalDrugUse;ʹ��˵��|Show Drug Use Desc,useDrugMenu");
            this.tableName = tableName;
            return;
        }
        if (TABLE_CHN.equalsIgnoreCase(tableName)) {// add by wanglong 20130522
            table.setPopupMenuSyntax("��ʾ������ҩ��Ϣ|Show Rational Drug Use,onQueryRationalDrugUse");
            return;
        }
	}
	/**
	 * �һ�MENU��ʾʹ��˵��-duzhw 20131209
	 */
	public void useDrugMenu(){

		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = odo.getOpdOrder();
		String caseNo = odo.getCaseNo();
		String mrNo = odo.getMrNo();
		
		String orderCode = order.getItemString(table.getSelectedRow(),
				"ORDER_CODE");
		String seqNo = order.getItemString(table.getSelectedRow(),
				"SEQ_NO");
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("SEQ_NO", seqNo);
		TParm reParm = (TParm)this.openDialog(
				"%ROOT%\\config\\opd\\OPDOrderUseDesc.x", parm);
		if(reParm.getValue("OPER").equals("CONFIRM")){
			String pasdrCode = reParm.getValue("PASDR_CODE");
			String pasdrNote = reParm.getValue("PASDR_NOTE");
			table.setItem(table.getSelectedRow(), "PASDR_CODE", pasdrCode);
			table.setItem(table.getSelectedRow(), "PASDR_NOTE", pasdrNote);
		}
		
	}

	/**
	 * �һ�MENU��ʾ����ҽ���¼�
	 */
	public void onOrderSetShow() {
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		OpdOrder order = odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
	}

	/**
	 * �һ�MENU��ʾ������Ŀϸ���¼�
	 */
	public void onOpShow() {
		TTable table = (TTable) this.getComponent(TABLE_OP);
		OpdOrder order = odo.getOpdOrder();
		int row = table.getSelectedRow();
		String orderCode = order.getItemString(row, "ORDER_CODE");
		int groupNo = order.getItemInt(row, "ORDERSET_GROUP_NO");
		TParm parm = order.getOrderSetDetails(groupNo, orderCode);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);

	}

	/**
	 * �һ�MENU��ʾSYS_FEE�¼�
	 */
	public void onSysFeeShow() {
		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = odo.getOpdOrder();
		String orderCode = order.getItemString(table.getSelectedRow(),
				"ORDER_CODE");
		TParm parm = new TParm();
		parm.setData("FLG", "OPD");
		parm.setData("ORDER_CODE", orderCode);
		this.openDialog("%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_PHA.x", parm);
	}

	/**
	 * ���ò�����ϸ��Ϣ����
	 */
	public void onPatDetail() {
		TParm parm = new TParm();
		// System.out.println("����Ȩ��" + this.getPopedem("DEPT_POPEDEM"));
		// �жϿ���Ȩ��
		if (this.getPopedem("DEPT_POPEDEM"))
			parm.setData("OPD", "ONW");
		else
			parm.setData("OPD", "OPD");
		parm.setData("MR_NO", reg.getPat().getMrNo());
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfo.x", parm);
	}

	/**
	 * ����ҽʦ����ҽ��
	 */
	public void onDrOrder() {
		TParm parm = new TParm();
		parm.setData("DEPT_DR", "2");
		parm.setData("DEPTORDR_CODE", Operator.getID());
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = "";
		String tableName = "";
		String tag = "";
		String rxTag = "";
		// ѡ���ĸ�ҳǩ
		switch (index) {
		// ������
		case 0:
			parm.setData("RX_TYPE", "5");
			orderCat1Type = "EXA";
			tableName = TABLE_EXA;
			tag = "EXA_AMT";
			rxTag = EXA_RX;
			break;
		// ����
		case 1:
			parm.setData("RX_TYPE", "4");
			orderCat1Type = "TRT";
			tableName = TABLE_OP;
			tag = "OP_AMT";
			rxTag = OP_RX;
			break;
		// ��ҩ
		case 2:
			parm.setData("ADM_TYPE",admType);//yanjing,�ż������֣�20130716
			parm.setData("RX_TYPE", "1");
			orderCat1Type = "PHA_W";
			tableName = TABLE_MED;
			tag = "MED_AMT";
			rxTag = MED_RX;
			break;
		// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			orderCat1Type = "PHA_G";
			tableName = TABLE_CHN;
			tag = "CHN_AMT";
			rxTag = CHN_RX;
			if (StringUtil.isNullString(this
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				this.messageBox("E0053");
				return;
			}
			break;
		// ����ҩƷ
		case 4:
			parm.setData("RX_TYPE", "2");
			orderCat1Type = "PHA_W";
			tableName = TABLE_CTRL;
			tag = "CTRL_AMT";
			rxTag = CTRL_RX;
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		if (!getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		if (CHN_RX.equalsIgnoreCase(rxTag)) {
			// ������ҽҽ��
			if (!checkPhaIsSave(CHN_RX,"CHN")) {
				return;
			}
			insertChnOrder(result);
		} else if (!EXA_RX.equalsIgnoreCase(rxTag)) {
			// ����ҽ��
			if (!checkPhaIsSave(MED_RX,"MED")) {
				return;
			}
			insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				this.insertExaPack(result.getRow(i));
			}

		}
	}

	/**
	 * ���ÿ��ҳ���ҽ��
	 */
	public void onDeptOrder() {
		TParm parm = new TParm();
		
		parm.setData("DEPT_DR", "1");
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("FIT", "OPD_FIT_FLG");
		} else {
			parm.setData("FIT", "EMG_FIT_FLG");
		}
		TTabbedPane tabPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		int index = tabPane.getSelectedIndex();
		String orderCat1Type = "";
		String tableName = "";
		String tag = "";
		String rxTag = "";
		// ѡ���ĸ�ҳǩ
		switch (index) {
		// ������
		case 0:
			parm.setData("RX_TYPE", "5");
			orderCat1Type = "EXA";
			tableName = TABLE_EXA;
			tag = "EXA_AMT";
			rxTag = EXA_RX;
			break;
		// ����
		case 1:
			parm.setData("RX_TYPE", "4");
			orderCat1Type = "TRT";
			tableName = TABLE_OP;
			tag = "OP_AMT";
			rxTag = OP_RX;
			break;
		// ��ҩ
		case 2:
			parm.setData("ADM_TYPE",admType);//yanjing,�ż������֣�20130716
			parm.setData("RX_TYPE", "1");
			orderCat1Type = "PHA_W";
			tableName = TABLE_MED;
			tag = "MED_AMT";
			rxTag = MED_RX;
			break;
		// ��ҩ
		case 3:
			parm.setData("RX_TYPE", "3");
			orderCat1Type = "PHA_G";
			tableName = TABLE_CHN;
			tag = "CHN_AMT";
			rxTag = CHN_RX;
			if (StringUtil.isNullString(this
					.getValueString("CHN_EXEC_DEPT_CODE"))) {
				this.messageBox("E0053");
				return;
			}
			break;
		// ����ҩƷ
		case 4:
			parm.setData("RX_TYPE", "2");
			orderCat1Type = "PHA_W";
			tableName = TABLE_CTRL;
			tag = "CTRL_AMT";
			rxTag = CTRL_RX;
			break;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\CommonOrderQuote.x", parm);
		if (result == null || result.getCount("ORDER_CODE") < 1) {
			return;
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		if (!getCheckRxNo()) {
			return;
		}
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		if (CHN_RX.equalsIgnoreCase(rxTag)) {
			// ������ҽҽ��
			if (!checkPhaIsSave(CHN_RX,"CHN")) {
				return;
			}
			insertChnOrder(result);
		} else if (!EXA_RX.equalsIgnoreCase(rxTag)) {
			// ����ҽ��
			if (!checkPhaIsSave(MED_RX,"MED")) {
				return;
			}
			insertOrder(tableName, tag, orderCat1Type, result, rxTag);
		} else {
			int count = result.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				this.insertExaPack(result.getRow(i));
			}

		}
	}

	/**
	 * У���Ƿ���Կ���ͬһ������ǩ�� ===========pangben 2012-7-12 ��ӹܿ�
	 * ��ǰ����ǩ�ܿ�
	 * @return
	 */
	private boolean getCheckRxNo() {
		OpdOrder order = odo.getOpdOrder();
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "�Ѿ��շѵĴ���ǩ���������ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
				return false;

			}
		}
		
		return true;
	}
	/**
	 * У���Ƿ���Կ���ͬһ������ǩ�� ===========pangben 2012-7-12 ��ӹܿ�
	 * ����ҳǩ��ǰ�Ĵ���ǩ�ܿ�
	 * 
	 * @return false ������ִ��  true ����ִ�� 
	 */
	private String getCheckRxNoSum(TParm parm){
		// ������
		TParm exaResult = ((TParm) parm.getData("EXA"));
		OpdOrder order = odo.getOpdOrder();
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			TComboBox rxExa = (TComboBox) getComponent(EXA_RX);
			String rxNo = rxExa.getSelectedID();
			if (null==rxNo || rxNo.length()<=0) {
				return "������û�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ�ļ����鴦��ǩ���������ҽ��";
			}
		}
		TParm opResult = ((TParm) parm.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(OP_RX); // ����ǩ��
			if (null==rxNo || rxNo.length()<=0) {
				return "������Ŀû�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ��������Ŀ����ǩ���������ҽ��";
			}
		}
		// ҩƷ
		TParm orderResult = ((TParm) parm.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(MED_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "����ҩû�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ������ҩ����ǩ���������ҽ��";
			}
		}
		// ����ҩƷ
		orderResult = ((TParm) parm.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			String rxNo = this.getValueString(CTRL_RX);
			if (null==rxNo || rxNo.length()<=0) {
				return "����ҩƷû�г�ʼ������ǩ";
			}
			if (!order.isExecutePrint(odo.getCaseNo(), rxNo)) {
				return "�Ѿ���Ʊ�Ĺ���ҩƷ����ǩ���������ҽ��";
			}
		}
		return null;
	}
	/**
	 * ����ҽ�������ײ���ҽ��
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
	private void insertOrder(String tableName, String tag,
			String orderCat1Type, TParm orderParm, String rxTag) {

		TTable table = (TTable) this.getComponent(tableName);
		OpdOrder order = odo.getOpdOrder();
		String rxType = order.getItemString(0, "RX_TYPE");
		String rxNo = order.getItemString(0, "RX_NO");
		insertOrderData(order, orderParm, orderCat1Type, rxTag);
		if (TABLE_CHN.equalsIgnoreCase(tableName)) {
			initChnTable(rxNo);
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
		this.calculateCash(tableName, tag);
	}

	/**
	 * ����ҽ��������ҽҽ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertChnOrder(TParm parm) {
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString("CHN_RX");

		String deptCode = this.getValueString("CHN_EXEC_DEPT_CODE");
		int count = order.rowCount();
		if (count <= 0) {
			return;
		}
		for (int i = count - 1; i > -1; i--) {
			order.deleteRow(i);
		}

		if (StringUtil.isNullString(rxNo)) {
			this.messageBox("E0069");
			return;
		}
		if (parm == null) {
			this.messageBox("E0070");
			return;
		}
		count = parm.getCount("ORDER_CODE");
		if (count < 1) {
			this.messageBox("E0070");
			return;
		}
		int row = -1;
		for (int i = 0; i < count; i++) {
			row = order.newOrder(CHN, rxNo);
			String orderCode = parm.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			this.initOrder(order, row, parm.getRow(i), phaBase);

			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));

			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", "PHA_G");
			order.setItem(row, "CAT1_TYPE", "PHA");

			order.setItem(row, "DCT_TAKE_QTY", this
					.getValueDouble("DCT_TAKE_QTY"));
			order.setItem(row, "TAKE_DAYS", this
					.getValueDouble("DCT_TAKE_DAYS"));
			order.setItem(row, "FREQ_CODE", this.getValue("CHN_FREQ_CODE"));
			order.setItem(row, "ROUTE_CODE", this.getValue("CHN_ROUTE_CODE"));
			order.setItem(row, "DCTAGENT_CODE", this.getValue("DCTAGENT_CODE"));
			order.setItem(row, "DR_NOTE", this.getValue("DR_NOTE"));
			order.setActive(row, true);
		}
		calculateChnCash(rxNo);
		if ((row + 1) % 4 == 0) {
			addChnRow(rxNo, row / 4);
		}
		this.initChnTable(rxNo);

	}

	/**
	 * ѭ����������
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
			String orderCat1Type, String rxTag) {
		int count = orderParm.getCount("ORDER_CODE");
		String rxNo = order.getItemString(0, "RX_NO");
		String rxType = order.getItemString(0, "RX_TYPE");
		this.rxType = rxType;
		String deptCode = "";
		if (MED.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("MED_RBORDER_DEPT_CODE");
		} else if (CTRL.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("CTRL_RBORDER_DEPT_CODE");
		} else if (CHN.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("CHN_EXEC_DEPT_CODE");
		} else if (OP.equalsIgnoreCase(rxType)) {
			deptCode = this.getValueString("OP_EXEC_DEPT");
		}
		int row = order.rowCount() - 1;
		if (EXA.equalsIgnoreCase(rxType)) {
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
				if (this.messageBox(
								"��ʾ��Ϣ/Tip",
								"��ҽ���Ѿ��������Ƿ������\r\n/This order exist,Do you give it again?",
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
			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", orderParm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", orderParm.getValue("MEDI_UNIT", i));
			order.setItem(row, "FREQ_CODE", orderParm.getValue("FREQ_CODE", i));
			order.setItem(row, "ROUTE_CODE", orderParm
					.getValue("ROUTE_CODE", i));

			order.setItem(row, "TAKE_DAYS", orderParm.getValue("TAKE_DAYS", i));
			order.setItem(row, "PRESRT_NO", orderParm.getValue("PRESRT_NO", i));
			price = OdoUtil.getPrice(order.getItemString(row, "ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			order.itemNow = false;
			order.setItem(row, "GIVEBOX_FLG", orderParm.getValue("GIVEBOX_FLG",
					i));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);
			order.setItem(row, "EXEC_DEPT_CODE", deptCode);
			order.setItem(row, "ORDER_CAT1_CODE", orderCat1Type);
		}
	}

	/**
	 * ����ҽʦ�������
	 */
	public void onDrDiag() {
		String param = "2," + Operator.getID();
		if (StringTool.getBoolean(this.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * ���ÿƳ������
	 */
	public void onDeptDiag() {

		String param = "1," + Operator.getDept();
		if (StringTool.getBoolean(this.getValueString("W_FLG"))) {
			param += ",W";
		} else {
			param += ",C";
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\ODOCommonIcdQuote.x", param);
		// System.out.println("result="+result);
		if (result == null || result.getCount("ICD_CODE") < 1) {
			return;
		}
		insertDiag(result);
	}

	/**
	 * ����ҽ�������ײ������
	 * 
	 * @param diagParm
	 *            TParm��ϼ���
	 */
	private void insertDiag(TParm diagParm) {
		int count = diagParm.getCount("ICD_CODE");
		Diagrec diag = odo.getDiagrec();
		int row = diag.rowCount() - 1;
		TTable table = (TTable) this.getComponent(TABLEDIAGNOSIS);
		for (int i = 0; i < count; i++) {

			if (!StringUtil.isNullString(diag.getItemString(
					diag.rowCount() - 1, "ICD_CODE"))) {
				row = diag.insertRow(-1);
			}
			if (diag.isHaveSameDiag(diagParm.getValue("ICD_CODE", i))) {
				this.messageBox("E0041");
				continue;
			}
			if (!OdoUtil.isAllowDiag(diagParm.getRow(i), Operator.getDept(),
					pat.getSexCode(), pat.getBirthday(), (Timestamp) this
							.getValue("ADM_DATE"))) {
				this.messageBox("E0042"); // liudy
				diag.deleteRow(row);
				table.acceptText();
				table.getTable().grabFocus();
				table.setSelectedRow(0);
				table.setSelectedColumn(1);
				row = table.addRow();
				table.setDSValue();
				continue;
			}

			diag.setItem(row, "ICD_TYPE", wc);
			diag.setItem(row, "ICD_CODE", diagParm.getValue("ICD_CODE", i));
			diag.setItem(row, "ORDER_DATE", diag.getDBTime());
			diag.setActive(row, true);
			if (!diag.haveMainDiag(new int[1])) {
				if ("C".equalsIgnoreCase(wc)
						&& !OdoUtil.isAllowChnDiag(diagParm.getRow(i))) {
					this.messageBox("E0018");
					diag.deleteRow(row);
					// odo.getDiagrec().insertRow();
					table.acceptText();
					table.getTable().grabFocus();
					table.setSelectedRow(0);
					table.setSelectedColumn(1);
					table.addRow();
					table.setDSValue();
					return;
				}

				diag.setItem(row, "MAIN_DIAG_FLG", "Y");
			} else {
				diag.setItem(row, "MAIN_DIAG_FLG", "N");
			}

		}
		if (!StringUtil.isNullString(diag.getItemString(diag.rowCount() - 1,
				"ICD_CODE")))
			row = diag.insertRow(diag.rowCount());
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(row);
		table.setSelectedColumn(1);
	}

	/**
	 * ���ÿƳ���ģ��
	 */
	public void onDeptPack() {
		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "1");
		parm.setData("DEPTORDR_CODE", Operator.getDept());
		parm.setData("ADM_TYPE",admType);//yanjing,�ż������֣�20130614
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackQuote.x", parm, false);
		if(null==result){
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//pangben 2013-5-17���������У���Ƿ�ǰ����ǩ�Ѿ����
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}else{
			if (!checkPhaIsSave(MED_RX,"MED")) {
				return;
			}
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		String resultString=getCheckRxNoSum(result);
		if (null!=resultString) {		
			this.messageBox(resultString);
			return;
		}
		insertPack(result);
	}

	/**
	 * ����ҽʦ����ģ��
	 */
	public void onDrPack() {
		TParm parm = new TParm();
		parm.setData("DEPT_OR_DR", "2");
		parm.setData("ADM_TYPE",admType);//yanjing,�ż������֣�20130614
		parm.setData("DEPTORDR_CODE", Operator.getID());
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackQuote.x", parm, false);
		if(null==result){
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}else{
			if (!checkPhaIsSave(MED_RX,"MED")) {
				return;
			}
		}
		// ============pangben 2012-7-12 ��ӹܿ�
		String resultString=getCheckRxNoSum(result);
		if (null!=resultString) {
			this.messageBox(resultString);
			return;
		}
		insertPack(result);
	}
	
	/**
	 * ȡ��ģ������ҽ��վ�������ϲ�������
	 * 
	 * @param result
	 *            TParm
	 */
	private void insertPack(TParm result) {
		// System.out.println("==result=="+result);
		if (result == null)
			return;
		// =======pangben 2012-6-15
		//deleteLisPosc = false;// ������ɾ���ܿ� HL7�˷Ѳ����޸�ע��
		String subjText = this.getValueString("SUBJ_TEXT")
				+ result.getValue("SUBJ_TEXT");
		String objText = this.getValueString("OBJ_TEXT")
				+ result.getValue("OBJ_TEXT");
		String psyText = this.getValueString("PHYSEXAM_REC")
				+ result.getValue("PHYSEXAM_REC");
		/**
		 * System.out.println("==subjText=="+subjText);
		 * System.out.println("==objText=="+objText);
		 * System.out.println("==psyText=="+psyText);
		 **/

		// zhangyong20110311
		String filter1 = ((TTable) this.getComponent(TABLE_EXA)).getDataStore()
				.getFilter();
		String filter2 = ((TTable) this.getComponent(TABLE_OP)).getDataStore()
				.getFilter();
		String filter3 = ((TTable) this.getComponent(TABLE_MED)).getDataStore()
				.getFilter();
		String filter4 = ((TTable) this.getComponent(TABLE_CHN)).getDataStore()
				.getFilter();
		String filter5 = ((TTable) this.getComponent(TABLE_CTRL))
				.getDataStore().getFilter();
		if (word != null) {
			if (subjText != null && !subjText.equals("")) {
				word.clearCapture("SUB");
				word.pasteString(subjText);
			}
			if (objText != null && !objText.equals("")) {
				word.clearCapture("OBJ");
				word.pasteString(objText);
			}
			if (psyText != null && !psyText.equals("")) {
				word.clearCapture("PHY");
				word.pasteString(psyText);
			}
		}
		// $$=======Modified by lx 2012-06-10 ��ģ�崫��ʱ�����ֲ�ʷ����� end======$$//
		// ��ģ���е����߿����ֲ�ʷ��ֵ���ṹ��������

		// ȡ�����
		TParm diagResult = ((TParm) result.getData("DIAG"));
		// ============xueyf modify 20120312
		boolean isHasDiagFlg = false;
		int rowMainDiag = odo.getDiagrec().getMainDiag();
		if (rowMainDiag >= 0) {
			isHasDiagFlg = true;
		}
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				this.popDiagReturn("", diagResult.getRow(i));
				Diagrec diagRec = odo.getDiagrec();
				int row = tblDiag.getSelectedRow() - 1;
				// ============xueyf modify 20120312 
				String MAIN_DIAG_FLG = diagResult.getData("MAIN_DIAG_FLG", i)
						.toString();
				if (MAIN_DIAG_FLG.equals("Y") && isHasDiagFlg) {
					this.messageBox("���������!\n����Ҫ�������ֶ�ѡ������ϡ�");
					MAIN_DIAG_FLG = "N";
				}
				diagRec.setItem(row, "MAIN_DIAG_FLG", MAIN_DIAG_FLG);
				tblDiag.setDSValue();
			}
		}
		// ������
		TParm exaResult = ((TParm) result.getData("EXA"));
		// System.out.println("exaResult----"+exaResult);
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				insertExaPack(exaResult.getRow(i));
			}
		}
		// // ����
		TParm opResult = ((TParm) result.getData("OP"));
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_OP;
			// orderCat1Type = "TRT";
			// tag = "OP_AMT";
			insertOpPack(opResult);
		}
		// ҩƷ
		TParm orderResult = ((TParm) result.getData("ORDER"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
			// orderCat1Type = "PHA_W";
			// tag = "MED_AMT";
//			System.out.println("111orderResult is :"+orderResult);
			insertOrdPack(orderResult,false);
		}
		// for (int i = 0; i < odo.getOpdOrder().rowCount(); i++) {
		// System.out.println(i + "--ҩƷ--" + odo.getOpdOrder().getRowParm(i));
		// }

		// ����ҩƷ
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_CTRL;
			// orderCat1Type = "PHA_W";
			// tag = "CTRL_AMT";
			insertCtrlPack(orderResult);
		}
		((TTable) this.getComponent(TABLE_EXA)).setFilter(filter1);
		((TTable) this.getComponent(TABLE_EXA)).filter();
		((TTable) this.getComponent(TABLE_EXA)).setDSValue();
		((TTable) this.getComponent(TABLE_OP)).setFilter(filter2);
		((TTable) this.getComponent(TABLE_OP)).filter();
		((TTable) this.getComponent(TABLE_OP)).setDSValue();
		((TTable) this.getComponent(TABLE_MED)).setFilter(filter3);
		((TTable) this.getComponent(TABLE_MED)).filter();
		((TTable) this.getComponent(TABLE_MED)).setDSValue();
		((TTable) this.getComponent(TABLE_CTRL)).setFilter(filter5);
		((TTable) this.getComponent(TABLE_CTRL)).filter();
		((TTable) this.getComponent(TABLE_CTRL)).setDSValue();

		// ��ҩ
		// System.out.println("chn======"+result.getData("CHN"));
		Map chnMap = (Map) result.getData("CHN");
		if (chnMap == null || chnMap.size() <= 0) {
			return;
		}
		Iterator it = chnMap.values().iterator();
		while (it.hasNext()) {
			tableName = TABLE_CHN;
			// orderCat1Type = "PHA_G";
			orderResult = (TParm) it.next();
			insertChnPack(orderResult);
		}
		((TTable) this.getComponent(TABLE_CHN)).setFilter(filter4);
		((TTable) this.getComponent(TABLE_CHN)).filter();
		((TTable) this.getComponent(TABLE_CHN)).setDSValue();

	}
	/**
	 * ��ʼ��parm
	 * 20130428 yanjing 
	 * @param parm
	 * @param i
	 */
	private TParm initParmBase(TParm parm,int i) {
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
		return parmBase;
	}

	/**
	 * ����ҩƷģ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOrdPack(TParm parm,boolean onFechFlg) {
		int count = parm.getCount("ORDER_CODE");
		if (count < 0) {
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		String tableName = "TABLEMED";
		rxType = MED;
		tableName = TABLE_MED;

		String rxNo = this.getValueString(MED_RX);
		String execDept = this.getValueString("MED_RBORDER_DEPT_CODE");
		if (StringUtil.isNullString(rxNo)) {
			initRx(MED_RX, MED);
		}
		rxNo = this.getValueString(MED_RX);

		order.setFilter("RX_NO='" + rxNo + "'");
		if (!order.filter()) {
			return;
		}
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
				: (order.newOrder(MED, rxNo));			
		for (int i = 0; i < count; i++) {
			//yanjing20130428 ����У�飬����
			TParm parmBase = new TParm();
			if (onFechFlg) {
				parmBase = initParmBase(parm, i);
			}else {
				parmBase = PhaBaseTool.getInstance().selectByOrder(
						parm.getValue("ORDER_CODE", i));
			}	
			TParm parmRow = parm.getRow(i);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
			sysFee = sysFee.getRow(0);
			sysFee.setData("EXEC_DEPT_CODE", execDept);
			sysFee.setData("CAT1_TYPE", "PHA");
			this.initOrder(order, row, sysFee, parmBase);
			order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
			order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
			order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
			order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));
			order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
			order
					.setItem(row, "LINKMAIN_FLG", parmRow
							.getValue("LINKMAIN_FLG"));
			order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));
			order.setActive(row, true);
			row = order.newOrder(MED, rxNo);
		}
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			order.newOrder(MED, rxNo);
		}
		initNoSetTable(rxNo, tableName, false);
		this.calculateCash(tableName, "MED_AMT");
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		tblMed.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * �������ҩƷģ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertCtrlPack(TParm parm) {
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(CTRL_RX);
		if (StringUtil.isNullString(rxNo)) {
			initRx(CTRL_RX, CTRL);
		}
		rxNo = this.getValueString(CTRL_RX);
		String exec = this.getValueString("CTRL_RBORDER_DEPT_CODE");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		int row = StringUtil.isNullString(order.getItemString(
				order.rowCount() - 1, "ORDER_CODE")) ? order.rowCount() - 1
				: (order.newOrder(CTRL, rxNo));
		int count = parm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			TParm parmBase = PhaBaseTool.getInstance().selectByOrder(
					parm.getValue("ORDER_CODE", i));
			TParm parmRow = parm.getRow(i);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(parmRow.getValue("ORDER_CODE"))));
			sysFee = sysFee.getRow(0);
			// �ж�ģ�崫�ص���Ϣ���Ƿ��� ִ�п���
			// �����ִ�п��� ��ô��ģ���е�ִ�п���
			String execDept = "";
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// ���ģ����û��ִ�п��� ��ôʹ��sys_fee�е�ִ�п���
			else if (!StringUtil
					.isNullString(sysFee.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFee.getValue("EXEC_DEPT_CODE");
			} else { // ���sys_fee��Ҳû��ִ�п��� ��ôʹ�õ�ǰ�û������ڿ���
				execDept = Operator.getDept();
			}
			sysFee.setData("EXEC_DEPT_CODE", execDept);
			initOrder(order, row, sysFee, parmBase);
			order.setItem(row, "MEDI_QTY", parmRow.getDouble("MEDI_QTY"));
			order.setItem(row, "MEDI_UNIT", parmRow.getValue("MEDI_UNIT"));
			order.setItem(row, "FREQ_CODE", parmRow.getValue("FREQ_CODE"));
			order.setItem(row, "TAKE_DAYS", parmRow.getInt("TAKE_DAYS"));
			order.setItem(row, "ROUTE_CODE", parmRow.getValue("ROUTE_CODE"));
			order
					.setItem(row, "LINKMAIN_FLG", parmRow
							.getValue("LINKMAIN_FLG"));
			order.setItem(row, "LINK_NO", parmRow.getValue("LINK_NO"));
			order.setActive(row, true);
			row = order.newOrder(CTRL, rxNo);
		}
		if (!StringUtil.isNullString(order.getItemString(order.rowCount() - 1,
				"ORDER_CODE"))) {
			order.newOrder(CTRL, rxNo);
		}
		initNoSetTable(rxNo, tableName, false);
		calculateCash(tableName, "CTRL_AMT");
		order.itemNow = false;
		Map inscolor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		Map ctrlcolor = OdoUtil.getCtrlColor(inscolor, odo.getOpdOrder());
		tblCtrl.setRowTextColorMap(ctrlcolor);
	}

	/**
	 * ������������
	 * 
	 * @param parmExa
	 *            TParm
	 *            
	 *            caowl  20131116  maybe
	 */
	private void insertExaPack(TParm parmExa) {
		rxType = EXA;
		OpdOrder order = odo.getOpdOrder();
		TComboBox rxExa = (TComboBox) getComponent(EXA_RX);
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		String rxNo = rxExa.getSelectedID();
		// this.messageBox_(rxNo);
		String orderCode = parmExa.getValue("ORDER_CODE");
		// System.out.println("orderCode---"+orderCode);
		TParm sysFee = new TParm(TJDODBTool.getInstance().select(
				SYSSQL.getSYSFee(orderCode)));
		if (sysFee == null || sysFee.getErrCode() != 0) {
			this.messageBox("E0034");
			return;
		}
		// �����������
		sysFee = sysFee.getRow(0);
		int row = table.getShowParmValue().getCount() - 1;
		order.setFilter("RX_NO='" + rxNo
				+ "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
		order.filter();
		// order.showDebug();
		order.setItem(row, "RX_NO", rxNo);
		initOrder(order, row, sysFee, null);
		// System.out.println("111111-----"+order.getRowParm(row));
		order.setItem(row, "DR_NOTE", parmExa.getValue("DESCRIPTION"));
		String labNo = order.getLabNo(row, odo);
		if (StringUtil.isNullString(labNo)) {
			this.messageBox("E0049");
			order.deleteRow(row);
			order.newOrder(EXA, rxNo);
			table.setDSValue();
			return;
		}
		order.setItem(row, "MED_APPLY_NO", labNo);
		order.setActive(row, true);
		String execDept = sysFee.getValue("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(execDept)) {
			execDept = Operator.getDept();
		}
		order.setItem(row, "ORDERSET_CODE", orderCode);
		order.setItem(row, "SETMAIN_FLG", "Y");
		order.setItem(row, "HIDE_FLG", "N");
		int groupNo = order.getMaxGroupNo();
		order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
		order.setItem(row, "OWN_PRICE", 0.0);
		order.setActive(row, true);

		// ��ѯϸ�����ݣ����ϸ��
		TParm parmDetail = SYSOrderSetDetailTool.getInstance()
				.selectByOrderSetCode(orderCode);
		if (parmDetail.getErrCode() != 0) {
			this.messageBox("E0050");
			return;
		}
		int count = parmDetail.getCount();
		for (int i = 0; i < count; i++) {
			row = order.newOrder(EXA, rxNo);
			// System.out.println("row----"+row);
			initOrder(order, row, parmDetail.getRow(i), null);
			order.setItem(row, "MED_APPLY_NO", labNo);
			order.setItem(row, "OPTITEM_CODE", parmDetail.getValue(
					"OPTITEM_CODE", i));
			order
					.setItem(row, "CAT1_TYPE", parmDetail.getValue("CAT1_TYPE",
							i));
			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			order.setItem(row, "INSPAY_TYPE", parmDetail.getValue(
					"INSPAY_TYPE", i));
			order.setItem(row, "RPTTYPE_CODE", parmDetail.getValue(
					"RPTTYPE_CODE", i));
			order.setItem(row, "DEGREE_CODE", parmDetail.getValue(
					"DEGREE_CODE", i));
			order.setItem(row, "CHARGE_HOSP_CODE", parmDetail.getValue(
					"CHARGE_HOSP_CODE", i));
			order.setItem(row, "HIDE_FLG", parmDetail.getValue("HIDE_FLG", i));
			order.setItem(row, "ORDERSET_CODE", orderCode);
			order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
			double ownPrice = parmDetail.getDouble("OWN_PRICE", i);
			order.setItem(row, "OWN_PRICE", ownPrice);
			double qty = parmDetail.getDouble("TOTQTY", i);
			order.setItem(row, "DOSAGE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "MEDI_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "DISPENSE_QTY", qty);
			order.itemNow = true;
			order.setItem(row, "TAKE_DAYS", 1);
			order
					.setItem(row, "MEDI_UNIT", parmDetail.getValue("UNIT_CODE",
							i));
			order.setItem(row, "DOSAGE_UNIT", parmDetail.getValue("UNIT_CODE",
					i));
			order.setItem(row, "DISPENSE_UNIT", parmDetail.getValue(
					"UNIT_CODE", i));

			order.setItem(row, "AR_AMT", roundAmt(BIL.chargeTotCTZ(order
					.getItemString(row, "CTZ1_CODE"), order.getItemString(row,
					"CTZ2_CODE"), order.getItemString(row, "CTZ3_CODE"), order
					.getItemString(row, "ORDER_CODE"), order.getItemDouble(row,
					"DOSAGE_QTY"), serviceLevel)));
			order.setItem(row, "IS_PRE_ORDER", order.getItemData(row, "IS_PRE_ORDER"));//caowl 20131117
			order.setItem(row, "PRE_DATE", order.getItemData(row, "PRE_DATE"));//caowl 20131117
			order.setActive(row, true);
		}
		order.newOrder(EXA, rxNo);
		initSetTable(TABLE_EXA, true);
		order.itemNow = false;
		// tblExa.getTable().grabFocus();
		// tblExa.setSelectedRow(0);
		// tblExa.setSelectedColumn(tblExa.getColumnIndex("EXEC_DEPT_CODE"));
	}

	/**
	 * ���봦��ģ��
	 * 
	 * @param parm
	 *            TParm
	 */
	private void insertOpPack(TParm parm) {
		TTable table = (TTable) this.getComponent(TABLE_OP);
		OpdOrder order = odo.getOpdOrder();
		String rxNo = this.getValueString(OP_RX); // ����ǩ��
		if (StringUtil.isNullString(rxNo)) {
			initRx(OP_RX, OP);
		}
		rxNo = this.getValueString("OP_RX");
		order.setFilter("RX_NO='" + rxNo + "'");
		order.filter();
		// ��ȡ�ش�ֵ�ĸ���
		int count = parm.getCount("ORDER_CODE");
		// ѭ������ÿ�� ������Ŀ
		for (int i = 0; i < count; i++) {
			int row = -1;
			if (!StringUtil.isNullString(order.getItemString(
					order.rowCount() - 1, "ORDER_CODE"))) {
				row = order.newOrder(OP, rxNo);
			} else {
				row = order.rowCount() - 1;
			}
			order.itemNow = true;
			order.sysFee.setFilter("ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "'");
			order.sysFee.filter();
			TParm sysFeeParm = order.sysFee.getRowParm(0);
			String orderCode = sysFeeParm.getValue("ORDER_CODE");
			// �½�һ�� ������Ŀ
			order.newOpOrder(rxNo, orderCode, ctz, row);
			// �ж�ģ�崫�ص���Ϣ���Ƿ��� ִ�п���
			// �����ִ�п��� ��ô��ģ���е�ִ�п���
			String execDept = "";
			if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parm.getValue("EXEC_DEPT_CODE", i);
			}
			// ���ģ����û��ִ�п��� ��ôʹ��sys_fee�е�ִ�п���
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // ���sys_fee��Ҳû��ִ�п��� ��ôʹ�õ�ǰ�û������ڿ���
				execDept = Operator.getDept();
			}
			order.setItem(row, "EXEC_DEPT_CODE", execDept); // ִ�п���
			order.setItem(row, "ORDER_DESC", sysFeeParm.getValue("ORDER_DESC")
					.replaceFirst(
							"(" + sysFeeParm.getValue("SPECIFICATION") + ")",
							"")); // ҽ������
			order.setItem(row, "CTZ1_CODE", ctz[0]);
			order.setItem(row, "CTZ2_CODE", ctz[1]);
			order.setItem(row, "CTZ3_CODE", ctz[2]);
			order.itemNow = false; // ������������
			order.setItem(row, "MEDI_QTY", parm.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			order.setItem(row, "TAKE_DAYS", parm.getValue("TAKE_DAYS", i));
			order
					.setItem(row, "LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",
							i));
			order.setItem(row, "LINK_NO", parm.getValue("LINK_NO", i));
			order.setActive(row, true);
		}
		order.newOrder(OP, rxNo);
		this.initNoSetTable(rxNo, TABLE_OP, false);
		// table.getTable().grabFocus();
		Map insColor = OdoUtil.getInsColor(ctz, odo.getOpdOrder(),
				whetherCallInsItf);
		table.setRowTextColorMap(insColor);
		this.calculateCash(TABLE_OP, "OP_AMT");
	}

	/**
	 * ������ҽ��ģ������
	 * 
	 * @param parmChn
	 *            TParm
	 */
	private void insertChnPack(TParm parmChn) {
		if (parmChn == null) {
			return;
		}
		OpdOrder order = odo.getOpdOrder();
		// ��ȡ����ǩ��
		String rxNo = this.getValueString(CHN_RX);
		if (StringUtil.isNullString(rxNo)) {
			initRx(CHN_RX, CHN);
		}
		rxNo = this.getValueString(CHN_RX);
		rxType = CHN;
		int count = parmChn.getCount();
		for (int i = 0; i < count; i++) {
			String orderCode = parmChn.getValue("ORDER_CODE", i);
			TParm phaBase = PhaBaseTool.getInstance().selectByOrder(orderCode);
			TParm sysFee = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFee(orderCode)));
			TParm sysFeeParm = sysFee.getRow(0);
			// �ж�ģ�崫�ص���Ϣ���Ƿ��� ִ�п���
			// �����ִ�п��� ��ô��ģ���е�ִ�п���
			String execDept = "";
			if (!StringUtil.isNullString(parmChn.getValue("EXEC_DEPT_CODE", i))) {
				execDept = parmChn.getValue("EXEC_DEPT_CODE", i);
			}
			// ���ģ����û��ִ�п��� ��ôʹ��sys_fee�е�ִ�п���
			else if (!StringUtil.isNullString(sysFeeParm
					.getValue("EXEC_DEPT_CODE"))) {
				execDept = sysFeeParm.getValue("EXEC_DEPT_CODE");
			} else { // ���sys_fee��Ҳû��ִ�п��� ��ôʹ�õ�ǰ�û������ڿ���
				execDept = Operator.getDept();
			}
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			int row = 0;
			for (int h = 0; h < order.rowCount(); h++) {
				if (!StringUtil.isNullString(order.getItemString(h,
						"ORDER_CODE"))) {
					row = h + 1;
				}
			}
			sysFeeParm.setData("EXEC_DEPT_CODE", execDept);
			this.initOrder(order, row, sysFeeParm, phaBase);
			TParm price = OdoUtil.getPrice(order.getItemString(row,
					"ORDER_CODE"));
			order.setItem(row, "OWN_PRICE", price.getDouble("OWN_PRICE"));
			order.setItem(row, "CHARGE_HOSP_CODE", price
					.getValue("CHARGE_HOSP_CODE"));
			double ownAmt = roundAmt(TypeTool.getDouble(price
					.getData("OWN_PRICE"))
					* order.getItemDouble(row, "DOSAGE_QTY"));
			double arAmt = roundAmt(BIL.chargeTotCTZ(ctz[0], ctz[1], ctz[2],
					orderCode, order.getItemDouble(row, "DOSAGE_QTY"),
					serviceLevel));
			order.setItem(row, "DISCN_RATE", BIL.getOwnRate(ctz[0], ctz[1],
					ctz[2], price.getValue("CHARGE_HOSP_CODE")));
			order.setItem(row, "OWN_AMT", ownAmt);
			order.setItem(row, "AR_AMT", arAmt);
			order.setItem(row, "PAYAMOUNT", ownAmt - arAmt);

			order.setItem(row, "EXEC_DEPT_CODE", execDept);
			order.setItem(row, "ORDER_CAT1_CODE", "PHA_G");
			order.setItem(row, "DCT_TAKE_QTY", parmChn.getValue("DCT_TAKE_QTY",
					i));
			order.itemNow = false;
			order.setItem(row, "TAKE_DAYS", parmChn.getValue("TAKE_DAYS", i));
			order.itemNow = false;
			order.setItem(row, "FREQ_CODE", parmChn.getValue("FREQ_CODE", i));
			order.itemNow = false;
			order.setItem(row, "MEDI_QTY", parmChn.getValue("MEDI_QTY", i));
			order.setItem(row, "MEDI_UNIT", parmChn.getValue("MEDI_UNIT", i));
			order.setItem(row, "ROUTE_CODE", parmChn.getValue("ROUTE_CODE", i));
			order.setItem(row, "DCTAGENT_CODE", parmChn.getValue(
					"DCTAGENT_CODE", i));
			order.setItem(row, "DR_NOTE", parmChn.getValue("DR_NOTE", i));
			order.setActive(row, true);
			order.newOrder(CHN, rxNo);
		}
		initChnTable(rxNo);
	}

	/**
	 * �������߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	 * 
	 * @param focusTag
	 *            String
	 */
	public void setFocusTag(String focusTag) {
		this.focusTag = focusTag;
	}

	/**
	 * �õ����߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	 * 
	 * @return focusTag String
	 */
	public String getFocusTag() {
		return this.focusTag;
	}

	/**
	 * ���߿����ֲ�ʷ�ĵ���¼������õ�ǰ�������ؼ���ý���Ŀؼ���Ϊ��Ƭ�ﷵ��ֵʹ��
	 * 
	 * @param tag
	 *            String
	 */
	public void onClick(String tag) {
		this.setFocusTag(tag);
	}

	/**
	 * Ϊ�˱���ģ�幫���ķ���
	 * 
	 * @return odo ODO
	 */
	public ODO getOdo() {
		return this.odo;
	}

	/**
	 * ��ģ��
	 */
	public void onSaveTemplate() {
		TParm parm = new TParm();
		parm.setData("ODO", odo);
		Object re = this.openDialog(
				"%ROOT%\\config\\opd\\OPDComPackEnterName.x", parm, false);
		if (TypeTool.getBoolean(re)) {
			this.messageBox("P0005");
		}
	}

	/**
	 * ��ʾ���ñ�����
	 */
	public void onShowQuoteSheet() {
		TTabbedPane orderPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex() != 0) {
			//======pangben 2013-4-25 ���Ĭ��ѡ�м�����ҳǩ�����������ҳǩʱ����ʾ��Ϣ
			orderPane.setSelectedIndex(0);//===Ĭ����ҩҳǩ
			onChangeOrderTab();//ҳǩ�л�
		}
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\sys\\SysExaSheetTree.x", this, true);
		window.setVisible(true);
	}

	/**
	 * �������ñ����汻�ý�����õ����Ӽ�����ķ���
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onQuoteSheet(Object obj) {
		//=======pangben 2013-1-11 ���У��
		TTabbedPane orderPane = (TTabbedPane) this
				.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex() != 0) {
			this.messageBox("E0072");
			return false;
		}
		String rxNo = this.getValueString(EXA_RX);
		if (StringUtil.isNullString(rxNo)) {
			return false;
		}
		if (!(obj instanceof TParm))
			return false;
		TParm sysFee = (TParm) obj;
		TTable table = (TTable) this.getComponent(TABLE_EXA);
		// ============pangben 2012-7-12 ��ӹܿ�
		if (!getCheckRxNo()) {
			return false;
		}
		insertExa(sysFee, table.getRowCount() - 1, 0);
		return true;
	}

	/**
	 * �������뵥
	 */
	public void onEmr() {
		if (odo == null)
			return;
		// �õ����뵥�����
		TParm emrParm = new TParm();
		TParm actionParm = new TParm();
		if (admType.equals("O")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAll(
					odo.getOpdOrder(), "ODO");
			actionParm.setData("SYSTEM_CODE", "ODO");
		} else if (admType.equals("E")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAll(
					odo.getOpdOrder(), "EMG");
			actionParm.setData("SYSTEM_CODE", "EMG");
		}
		if (emrParm.getInt("ACTION", "COUNT") > 0) {
			actionParm.setData("ADM_TYPE", admType);
			actionParm.setData("MR_NO", this.pat.getMrNo());
			if ("en".equals(this.getLanguage())) // �����Ӣ�� ��ôȡӢ������
				actionParm.setData("PAT_NAME", this.pat.getName1());
			else
				actionParm.setData("PAT_NAME", this.pat.getName());
			actionParm.setData("CASE_NO", odo.getCaseNo());
			actionParm.setData("IPD_NO", "");
			actionParm.setData("ADM_DATE", odo.getRegPatAdm().getItemData(0,
					"ADM_DATE"));
			actionParm.setData("DEPT_CODE", Operator.getDept());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			actionParm.setData("EMR_DATA_LIST", emrParm);
			// System.out.println("for Emr parm"+actionParm);
			// this.openDialog("%ROOT%\\config\\emr\\EMRSingleUI.x",actionParm);
			this.openWindow("%ROOT%\\config\\emr\\EMRSingleUI.x", actionParm);
		}
	}

	/**
	 * ����ҩTABLE���ҩƷ���Ƶ���������ҩ��ѯ����
	 */
	public void onQueryRsDrug() {
		TTable table = (TTable) this.getComponent(TABLE_MED);
		int row = table.getSelectedRow();
		String columnName = table.getParmMap(table.getSelectedColumn());

		// ============xueyf modify 20120309
		if (!("ORDER_DESC_SPECIFICATION".equalsIgnoreCase(columnName) || "ORDER_ENG_DESC"
				.equalsIgnoreCase(columnName)))
			return;
		// String orderDesc=odo.getOpdOrder().getItemString(row, "ORDER_DESC");
		// ("2C010001","25%�����������","",""));
		PassDriver.PassSetQueryDrug(odo.getOpdOrder().getItemString(row,
				"ORDER_CODE"), odo.getOpdOrder().getItemString(row,
				"ORDER_DESC"), "", "");
		PassDriver.PassDoCommand(401);
	}
	/**
	 * У��ҩƷ�Ƿ���Ա��������ʵ��ʵʱ���䷢����
	 * @return
	 */
	private boolean checkPhaIsSave(String rxNo,String type){
		OpdOrder order=odo.getOpdOrder();
		order.setFilter("RX_NO='" + this.getValueString(rxNo) + "'");
		if (!order.filter()) {//��ǰҳǩ����
			return false;
		}
		for (int i = 0; i < order.rowCount(); i++) {
			if (checkPha(order, i,type,true))//����ҽ��У�鴦��ǩ�Ƿ���Կ���
				return false;
		}
		return true;
	}
	/**
	 * סԺԤԼ
	 */
	public void onPreDate() {
		if (odo == null) {
			return;
		}
		//���ݹҺŲ�������Ч����У���Ƿ���Ծ�����ҽ������===========pangben 2013-4-28
		if(!OPBTool.getInstance().canEdit(reg, regSysEFFParm)){
			this.messageBox("������ǰ����ʱ��");
			this.onClear();
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("ADM_TYPE_ZYZ",admType);//yanj,20130816,�ż���ʱסԺ֤��ӡ���Զ��ر�
		if ("O".equalsIgnoreCase(admType)) {
			parm.setData("ADM_SOURCE", "01");
		} else if ("E".equalsIgnoreCase(admType)) {
			parm.setData("ADM_SOURCE", "02");
			parm.setData("CASE_NO", caseNo);//add by wanglong 20121025
		}
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		int mainDiag = odo.getDiagrec().getMainDiag();
		if (mainDiag >= 0) {
			//ѭ��ȡ����������������������� duzhw
			TParm diagParm = getDiagParm();
			//System.out.println("otherDiagParm="+diagParm);
			String description = getDesc(diagParm);
			//System.out.println("description="+description);
			parm.setData("ICD_CODE", odo.getDiagrec().getItemString(mainDiag,
					"ICD_CODE"));
			parm.setData("DESCRIPTION", description + odo.getDiagrec().getItemString(
					mainDiag, "DIAG_NOTE"));
		} else {
			parm.setData("ICD_CODE", "");
			parm.setData("DESCRIPTION", "");
		}
		parm.setData("ADM_EXE_FLG","Y");//=====pangben 2013-4-26 ҽ��վִ�в������ٲ���
		this.openWindow("%ROOT%\\config\\adm\\ADMResv.x", parm);
	}
	/**
	 * ȡ��������ϵĺ��������(�ż���תסԺ-סԺ֤��ʾ)-duzhw add 20140319
	 * @return
	 */
	public TParm getDiagParm() {
		TParm parm = new TParm();
		int num = 0;
		for (int i = 0; i < tblDiag.getRowCount()-1; i++) {
			if(num<2){
				//tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				//int row = tblDiag.getSelectedRow() - 1;
				String MAIN_DIAG_FLG = odo.getDiagrec().getItemString(i,"MAIN_DIAG_FLG");
				if(MAIN_DIAG_FLG.equals("Y"))
					continue;
				String diagCode = odo.getDiagrec().getItemString(i,"ICD_CODE");
				String diagName = odo.getDiagrec().getIcdDesc(diagCode, "");
				//System.out.println("i="+i+" MAIN_DIAG_FLG="+MAIN_DIAG_FLG);
				parm.setData("ICD_CODE", num, diagCode);
				parm.setData("ICD_NAME", num, diagName);
				parm.setCount(num+1);
				num++;
			}
		}
		return parm;
	}
	/**
	 * ��ȡƴ�ӱ�ע
	 */
	public String getDesc(TParm diagParm){
		String desc = "";
		for (int i = 0; i < diagParm.getCount(); i++) {
			String description = diagParm.getValue("ICD_NAME", i);
			if(i==diagParm.getCount()-1){
				desc+=description+" ";
			}else{
				desc+=description+",";
			}
			
		}
		return desc;
	}

	/**
	 * ��������
	 */
	public void onErd() {
		if (odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
		parm.setData("FLG", "OPD");
		this.openDialog("%ROOT%\\config\\erd\\ERDDynamicRcd.x", parm);
	}

	/**
	 * ����ǰʹÿ��TABLE��û�б༭״̬
	 */
	private void acceptForSave() {
		TTable table = (TTable) getComponent(TABLE_CHN);
		table.acceptText();
		table = (TTable) getComponent(TABLE_CTRL);
		table.acceptText();
		table = (TTable) getComponent(TABLE_EXA);
		table.acceptText();
		table = (TTable) getComponent(TABLE_MED);
		table.acceptText();
		table = (TTable) getComponent(TABLE_OP);
		table.acceptText();
		table = (TTable) getComponent(TABLEALLERGY);
		table.acceptText();
		table = (TTable) getComponent(TABLEDIAGNOSIS);
		table.acceptText();
		table = (TTable) getComponent(TABLEMEDHISTORY);
		table.acceptText();
	}

	/**
	 * �����¼
	 */
	public void onCaseHistory() {
		if (odo == null) {
			return;
		}
		//======pangben 2012-7-24 ���Ĭ��ѡ����ҩҳǩ������ڼ�����ҳǩʱ��ҩƷҳǩ���ּ�������������
		TTabbedPane orderPane = (TTabbedPane) this.getComponent("TTABPANELORDER");
		if (orderPane.getSelectedIndex()!=2) {
			orderPane.setSelectedIndex(2);//===Ĭ����ҩҳǩ
			onChangeOrderTab();//ҳǩ�л�	
		}
		Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",
				odo.getMrNo());
		if (obj == null) {
			return;
		}
		if (!(obj instanceof TParm)) {
			return;
		}
		if (Operator.getSpcFlg().equals("Y")) {//���������У���Ƿ�ǰ����ǩ�Ѿ����
			if (!checkSpcPha(odo.getOpdOrder())) {
				return;
			}
		}else{//=======pangben 2014-1-15 
			if (!checkPhaIsSave(MED_RX,"MED")) {
				return;
			}
		}
		//=============pangben 2012-7-24 ��ӹܿ�
//		if (!getCheckRxNo()) {
//			return;
//		}
		//deleteLisPosc = false;// =============pangben 2012-6-15
		TParm result = (TParm) obj;
		String sub = result.getValue("SUB");
		String objStr = result.getValue("OBJ");
		String phy = result.getValue("PHY");
		String exaR = result.getValue("EXA_R");// =========pangben 2012-6-28 ��Ӽ����\����ش�ֵ��ʾ
		String pro = result.getValue("PRO");
		// ======xueyf start �����ֲ�ʷ�ش�����
		if (word != null) {
			if (!StringUtil.isNullString(sub)) {
				word.clearCapture("SUB");
				word.pasteString(sub);
			}
			if (!StringUtil.isNullString(objStr)) {
				word.clearCapture("OBJ");
				word.pasteString(objStr);
			}
			if (!StringUtil.isNullString(phy)) {
				word.clearCapture("PHY");
				word.pasteString(phy);
			}
			// �����
			if (!StringUtil.isNullString(exaR)) {
				word.clearCapture("EXA_RESULT");
				word.pasteString(exaR);
			}
			// ����
			if (!StringUtil.isNullString(pro)) {
				word.clearCapture("PROPOSAL");
				word.pasteString(pro);
			}
		}
		// ======xueyf stop
		// ȡ�����
		TParm diagResult = ((TParm) result.getData("DIAG"));
		if (diagResult != null) {
			int count = diagResult.getCount("ICD_CODE");
			for (int i = 0; i < count; i++) {
				tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				this.popDiagReturn("", diagResult.getRow(i));
			}
		}
		
		// ������
		TParm exaResult = ((TParm) result.getData("EXA"));
		if (exaResult != null && exaResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_EXA;
			int count = exaResult.getCount("ORDER_CODE");
			for (int i = 0; i < count; i++) {
				insertExaPack(exaResult.getRow(i));
			}
		}
		// ����
		TParm opResult = ((TParm) result.getData("OP"));
		// System.out.println("opResult="+opResult);
		if (opResult != null && opResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_OP;
			insertOpPack(opResult);
		}
		// ҩƷ
		TParm orderResult = ((TParm) result.getData("MED"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_MED;
//			System.out.println("222orderResult is :"+orderResult);
			insertOrdPack(orderResult,true);
		}
		// ����ҩƷ
		orderResult = ((TParm) result.getData("CTRL"));
		if (orderResult != null && orderResult.getCount("ORDER_CODE") > 0) {
			tableName = TABLE_CTRL;
			insertCtrlPack(orderResult);
		}
		Map chnResult = ((Map) result.getData("CHN"));
		if (chnResult != null) {
			Iterator it = chnResult.values().iterator();
			while (it.hasNext()) {
				TParm chn = (TParm) it.next();
				// System.out.println("chn="+chn);
				insertChnPack(chn);
			}
		}
	}

	/**
	 * ���鱨��
	 */
	public void onLisReport() {
		if (odo == null) {
			return;
		}
		SystemTool.getInstance().OpenLisWeb(odo.getMrNo());
	}

	/**
	 * ��鱨��
	 */
	public void onRisReport() {
		// ���ü��ӿ�
		if (odo == null) {
			return;
		}
		SystemTool.getInstance().OpenRisWeb(odo.getMrNo());
	}

	/**
	 * ���ô�Ⱦ�����濨
	 */
	public void onContagionReport() {
		if (this.tblDiag == null || odo == null) {
			return;
		}
		int row = this.tblDiag.getSelectedRow();
		if (row < 0) {
			return;
		}
		Diagrec diag = odo.getDiagrec();
		if (diag.isContagion(row)) {
			TParm can = new TParm();
			can.setData("MR_NO", odo.getMrNo());
			can.setData("CASE_NO", odo.getCaseNo());
			can.setData("ICD_CODE", diag.getItemString(row, "ICD_CODE"));
			can.setData("DEPT_CODE", Operator.getDept());
			can.setData("USER_NAME", Operator.getName());
			can.setData("ADM_TYPE", odo.getAdmType());//modify by wanglong 20140307
			this.openDialog("%ROOT%/config/mro/MROInfect.x", can);
		}
	}

	/**
	 * �������µ�
	 */
	public void onBodyTemp() {
		if (odo == null) {
			return;
		}
		String sql = " SELECT B.CHN_DESC,A.BED_DESC FROM ERD_BED A,SYS_DICTIONARY B "
				+ " WHERE B.GROUP_ID='ERD_REGION' "
				+ " AND B.ID=A.ERD_REGION_CODE "
				+ " AND A.CASE_NO='"
				+ odo.getCaseNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		TParm sumParm = new TParm();

		sumParm.setData("SUM", "CASE_NO", odo.getCaseNo());
		sumParm.setData("SUM", "MR_NO", odo.getMrNo());
		sumParm.setData("SUM", "IPD_NO", "");
		sumParm.setData("SUM", "STATION_CODE", result.getData("CHN_DESC", 0));
		sumParm.setData("SUM", "BED_NO", result.getData("BED_DESC", 0));
		sumParm.setData("SUM", "ADM_TYPE", "E");

		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm);
	}

	/**
	 * �������۲���
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE_ZYZ", admType);//yanj,20130820
		if ("O".equalsIgnoreCase(this.admType)) {
			parm.setData("SYSTEM_TYPE", "ODO");
			parm.setData("ADM_TYPE", "O");
		} else {
			parm.setData("SYSTEM_TYPE", "EMG");
			parm.setData("ADM_TYPE", "E");
		}

		parm.setData("CASE_NO", odo.getCaseNo());
		parm.setData("PAT_NAME", odo.getPatInfo().getItemString(0, "PAT_NAME"));
		parm.setData("MR_NO", odo.getMrNo());
		parm.setData("IPD_NO", "");
		parm.setData("ADM_DATE", odo.getRegPatAdm().getItemData(0, "ADM_DATE"));
		parm.setData("DEPT_CODE", Operator.getDept());
		// parm.setData("STYLETYPE","1");
		parm.setData("RULETYPE", "2");
		parm.setData("EMR_DATA_LIST", new TParm());
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	}

	/**
	 * �ṹ����������Ƭ��
	 */
	public void onInsertPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
		word.grabFocus();
	}

	/**
	 * �ṹ���������ü�������
	 */
	public void onInsertResult() {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * �ṹ����������Ƭ��
	 */
	public void onInsertFamilyPY() {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onFamilyReturn");
		TWindow window = (TWindow) this.openWindow(
				"%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
		window.setVisible(true);
		this.familyWord.grabFocus();

	}

	/**
	 * Ƭ���¼����ش�ֵ
	 * 
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) {
		String str = "";
		if (word.focusInCaptue("SUB")) {
			str = "SUB";
		} else if (word.focusInCaptue("OBJ")) {
			str = "OBJ";
		} else if (word.focusInCaptue("PHY")) {
			str = "PHY";
		} else if (word.focusInCaptue("EXA_RESULT")) {
			str = "EXA_RESULT";
		} else if (word.focusInCaptue("PROPOSAL")) {
			str = "PROPOSAL";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.word.pasteString(value)) {
			this.messageBox("E0035");
		}
	}

	/**
	 * Ƭ���¼����ش�ֵ
	 * 
	 * @param value
	 *            String
	 */
	public void onFamilyReturn(String value) {

		String str = "";
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.familyWord.pasteString(value)) {
			this.messageBox("E0035");
		}

	}

	/**
	 * ����ҽ����
	 */
	public void onOrderSheet() {
		TParm parm = new TParm();
		parm.setData("INW", "CASE_NO", odo.getCaseNo());
		this.openDialog("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x",
				parm);
	}

	/**
	 * ��������
	 */
	public void onOpApply() {
		TParm parm = new TParm();
		parm.setData("MR_NO", odo.getPatInfo().getItemString(0, "MR_NO"));
		// CASE_NO
		parm.setData("CASE_NO", odo.getCaseNo());
		// ADM_TYPE
		parm.setData("ADM_TYPE", odo.getRegPatAdm()
				.getItemString(0, "ADM_TYPE"));
		// BOOK_DEPT_CODE
		parm.setData("BOOK_DEPT_CODE", Operator.getDept());
		// STATION_CODE
		parm.setData("STATION_CODE", Operator.getStation());
		// BOOK_DR_CODE
		parm.setData("BOOK_DR_CODE", Operator.getID());
		// ICD_CODE
		int i = odo.getDiagrec().getMainDiag();
		String icdCode = "";
		if (i >= 0) {
			icdCode = odo.getDiagrec().getItemString(i, "ICD_CODE");
		}
		parm.setData("ICD_CODE", icdCode);
		this.openWindow("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
	}

	/**
	 * ������¼
	 */
	public void onOpRecord() {
		if (odo == null) {
			return;
		}
		if (StringUtil.isNullString(odo.getCaseNo())) {
			return;
		}
		TParm parmR = new TParm();
		parmR.setData("SYSTEM", "OPD"); // ����ϵͳ���
		parmR.setData("ADM_TYPE", admType);
		parmR.setData("CASE_NO", odo.getCaseNo());
		parmR.setData("MR_NO", odo.getMrNo());
		this.openDialog("%ROOT%\\config\\ope\\OPEOpDetail.x", parmR);
	}

	/**
	 * ��������
	 * 
	 * @param language
	 *            String
	 */
	public void onChangeLanguage(String language) {
		isEng = "en".equalsIgnoreCase(language);
	}

	public static void main(String[] args) {
		com.javahis.util.JavaHisDebug.TBuilder();
		Operator.setData("D001", "HIS", "127.0.0.1", "20803", "001");
	}

	/**
	 * �Ŷӽк�
	 */
	public void onCallNo() {
		if (tblPat == null) {
			return;
		}
		int row = tblPat.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm parm = tblPat.getParmValue();
		if (parm == null || parm.getCount() <= 0) {
			return;
		}
		CallNo callUtil = new CallNo();
		// System.out.println();
		if (!callUtil.init()) {
			return;
		}
		//�к�ͬ��
		String caseNo = parm.getValue("CASE_NO", row);
		String patName = parm.getValue("PAT_NAME", row);
		String drID = Operator.getID();
		String ip = Operator.getIP();
		if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(patName)
				|| StringUtil.isNullString(drID) || StringUtil.isNullString(ip)) {
			this.messageBox("E0133");
			return;
		}
		String result = callUtil.CallClinicMaster("", caseNo, "", "", patName,
				"", "", drID, ip);
		if ("true".equalsIgnoreCase(result)) {

			this.messageBox("P0119");
			return;
		}
		this.messageBox("E0133");
	}

	/**
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		if (odo == null) {
			return true;
		}
		unLockPat();
		SynLogin("0"); // �˳��кŵ�½
		return true;
	}

	/**
	 * ԤԼ�Һ�
	 */
	public void onReg() {
		if (odo == null)
			return;
		String MR_NO = odo.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", MR_NO);
		parm.setData("NHI_NO", pat.getNhiNo());// ҽ������
		this.openWindow("%ROOT%\\config\\reg\\REGAdmForDr.x", parm);
	}

	/**
	 * �кŵ�½����
	 * 
	 * @param type
	 *            String 1��½,0�˳�
	 */
	private void SynLogin(String type) {
		CallNo callUtil = new CallNo();
		if (!callUtil.init()) {
			return;
		}
	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		isReadEKT = true;
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		boolean isMrNoNull = StringUtil
				.isNullString((String) getValue("MR_NO"));
		if (null == caseNo || isMrNoNull) {
			this.messageBox("��ѡ��һ������");
			return;
		}

		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (ektReadParm.getErrCode() < 0) {
			this.messageBox("ҽ�ƿ���������");
			return;
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
					+ ektReadParm.getValue("PAT_NAME"));
			// // �������в��˵�ʱ�� ��ҽ�ƿ�ֻ�������Ա� ��Ƭ�Ƿ����ڸò���
			ektReadParm.setData("SEX",
					ektReadParm.getValue("SEX_CODE").equals("1") ? "��" : "Ů");
			this.openDialog("%ROOT%\\config\\ekt\\EKTInfoUI.x", ektReadParm);
			ektReadParm = null;
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		// parm.setData("MR_NO", getValue("MR_NO"));
		TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
		if (reg.getCount("CASE_NO") > 1) {
			TParm re = (TParm) this.openDialog(
					"%ROOT%\\config\\opd\\OPDRegChoose.x", reg);
			if (re == null)
				return;
			TParm result = new TParm();
			result.setRowData(0, re);
			wc = "W"; // Ĭ��Ϊ��ҽ
			// ============xueyf modify 20120227 start
			if (isMrNoNull) {
				this.initOpd(result, 0);
			}
		} else if (reg.getCount("CASE_NO") == 1 && isMrNoNull) {
			wc = "W"; // Ĭ��Ϊ��ҽ
			this.initOpd(reg, 0);
			// ============xueyf modify 20120227 stop
		}
		this.setValue("LBL_EKT_MESSAGE", "�Ѷ���");//====pangben 2013-3-19 ��Ӷ���
		ekt_lable.setForeground(green);//======yanjing 2013-06-14���ö�����ɫ
		//isEKTFee = true;
		// }
		// }
		// txReadEKT();
	}
	/**
	 * ��������
	 */
	public void unLockPat() {
		if (pat == null)
			return;
		String odo_type = "ODO";
		if ("E".equals(admType)) {
			odo_type = "ODE";
		}
		// �ж��Ƿ����
		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
			TParm parm = PatTool.getInstance().getLockPat(pat.getMrNo());
			if (odo_type.equals(parm.getValue("PRG_ID", 0))
					&& (Operator.getIP().equals(parm.getValue("OPT_TERM", 0)))
					&& (Operator.getID().equals(parm.getValue("OPT_USER", 0))))
				PatTool.getInstance().unLockPat(pat.getMrNo());
		}
		pat = null;
	}

	// add by zhangk
	/**
	 * ����HL7�ӿڵĲ��� ���������� �� ȡ���� �������б�
	 * 
	 * @param HL7
	 *            List
	 * @param flg
	 *            String 0:���� 1:ȡ��
	 * @return List
	 */
	public List getHL7List(List HL7, String flg) {
		List HL7List = new ArrayList();
		Iterator it = HL7.iterator();
		while (it.hasNext()) {
			TParm re = (TParm) it.next();
			if (flg.equals(re.getValue("FLG"))) {
				HL7List.add(re);
			}
		}
		return HL7List;
	}

	/**
	 * �������� ������λС��
	 * 
	 * @param value
	 *            double
	 * @return double
	 */
	public double roundAmt(double value) {
		double result = 0;
		if (value > 0)
			result = ((int) (value * 100.0 + 0.5)) / 100.0;
		else if (value < 0)
			result = ((int) (value * 100.0 - 0.5)) / 100.0;
		return result;
	}

	/**
	 * �ж��Ƿ�����޸�ҽ�� ���ADM_DATE�ĵ���23��59��59���Ժ󲻿����ٶԲ�����ҽ�������޸ģ�����ɾ����
	 * ���ADM_DATE��������23��59��59���Ժ󲻿����ٶԲ�����ҽ�������޸ģ�����ɾ����
	 * 
	 * @return boolean
	 */
	private boolean canEdit() {
		Timestamp admDate = reg.getAdmDate(); // �Һ�����
		Timestamp now = SystemTool.getInstance().getDate(); // ��ǰʱ��
		if ("O".equals(admType)) {
			// ��ȡ�Һŵ���23��59��59���ʱ�䣨�����޸�ҽ�����޶�ʱ�䣩
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					admDate, "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// ��ǰʱ�������޸�ҽ�����޶�ʱ�� �򲻿����޸�
			if (now.getTime() > time.getTime()) {
				return false;
			}
		} else if ("E".equals(admType)) {
			// ���� ��ȡҽ��վ�������С��޶�������������23��59��59���ʱ�䣨�����޸�ҽ�����޶�ʱ�䣩
			// ������� ��������� ����3/5 ���ϹҺţ�һֱ�� 3/6 ȫ�춼��Ϊ��Ч
			int OPDDay = OPDSysParmTool.getInstance().getEDays();
			String sessionCode = this.getValueString("SESSION_CODE");
			if (sessionCode.equalsIgnoreCase("8") && OPDDay == 0) { // �������
				OPDDay++;
			}
			Timestamp time = StringTool.getTimestamp(StringTool.getString(
					StringTool.rollDate(admDate, OPDDay), "yyyyMMdd")
					+ "235959", "yyyyMMddHHmmss");
			// ��ǰʱ�������޸�ҽ�����޶�ʱ�� �򲻿����޸�
			if (now.getTime() > time.getTime()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж��Ƿ���Ա���
	 * 
	 * @return boolean
	 */
	private boolean canSave() {
		// �����涨����ʱ�� �������� �����޸�
		if (!canEdit()) {
			OpdOrder order = odo.getOpdOrder();
			int[] newRows = order.getNewRows();
			if (newRows != null && newRows.length > 0) {
				return false; // ��������
			}
			int[] updateRows = order.getModifiedRows();
			if (updateRows != null && updateRows.length > 0) {
				return false; // ���޸���
			}
		}
		return true;
	}

	/**
	 * �ǳ�̬����
	 */
	public void onAbnormalReg() {
		Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDAbnormalReg.x");
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj; // �ǳ�̬����Һ���Ϣ
		wc = "W"; // Ĭ��Ϊ��ҽ
		this.initOpd(parm, 0); // ��ʼ��ҽ��վ��Ϣ
	}

	/**
	 * Ȩ�޳�ʼ�� �Ƿ���Դ���
	 */
	private void initInstradCombo() {
		Object obj = this.getPopedemParm();
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		for (int i = 0; i < parm.getCount(); i++) {
			if ("INSTEAD".equals(parm.getValue("ID", i))) {
				this.callFunction("UI|INSTEAD_DEPT|setEnabled", true);
			}
		}
	}

	/**
	 * �ж��Ƿ��˿��
	 * 
	 * @param orderCode
	 *            String
	 * @return boolean
	 */
	private boolean isCheckKC(String orderCode) {
		String sql = SYSSQL.getSYSFee(orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getBoolean("IS_REMARK", 0)) // �����ҩƷ��ע��ô�Ͳ���˿��
			return false;
		else
			// ����ҩƷ��ע�� Ҫ��˿��
			return true;
	}

	/**
	 * �ṹ�������Ҽ�����
	 */
	public void onMouseRightPressed() {
		if (word.focusInCaptue("EXA_RESULT")) {
			word.popupMenu("Ƭ��,onInsertPY;|;�����,onInsertResult", this);
		}
	}

	/**
	 * ���ҩƷ���ͷ��� �Ƿ���Կ���ͬһ����ǩ��
	 * 
	 * @param order
	 *            OpdOrder
	 * @param DOSE_TYPE
	 *            String
	 * @return boolean
	 */
	public boolean checkDOSE_TYPE(OpdOrder order, String DOSE_TYPE) {
		boolean flg = true;
		// ORDER_CODEΪ�ձ�ʾ��һ���ǿ�ҽ�� ���´���ǩ�Ŀ�ʼ ���Է���true
		if (order.getItemString(0, "ORDER_CODE").length() <= 0) {
			return flg;
		}
		// ============pangben 2012-2-29 ��ӹܿ�
		int count = order.rowCount();
		if (count <= 0) {
			return false;
		}
		for (int i = count - 1; i > -1; i--) {
			String tempCode = order.getItemString(i, "ORDER_CODE");
			if (StringUtil.isNullString(tempCode))
				continue;
			if (!deleteOrder(order, i, "�Ѿ���Ʊ�Ĵ���ǩ���������ҽ��","��ҽ���Ѿ��Ǽ�,����ɾ��")) {
				return false;

			}
		}
		// ============pangben 2012-2-29 stop
		// �ڷ�����ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("O")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		// �������δ���ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("I")
				|| order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("F")) {
			if ("E".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		// ����ҩ����ǩ
		if (order.getItemString(0, "DOSE_TYPE").equalsIgnoreCase("E")) {
			if ("I".equalsIgnoreCase(DOSE_TYPE)
					|| "F".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
			if ("O".equalsIgnoreCase(DOSE_TYPE)) {
				this.messageBox("E0194"); // ��ͬ���ͷ����ҩƷ���ɿ�����ͬһ�Ŵ���ǩ��
				flg = false;
			}
		}
		return flg;
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
		if (!"ODO".equalsIgnoreCase(prgId) && !"ODE".equalsIgnoreCase(prgId)) {
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
			String odo_type = "ODO";
			if ("E".equals(admType)) {
				odo_type = "ODE";
			}
			// �ж��Ƿ����
			if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
				TParm parmPat = PatTool.getInstance().getLockPat(pat.getMrNo());
				if (odo_type.equals(parmPat.getValue("PRG_ID", 0))
						&& (Operator.getIP().equals(parmPat.getValue(
								"OPT_TERM", 0)))
						&& (Operator.getID().equals(parmPat.getValue(
								"OPT_USER", 0))))
					PatTool.getInstance().unLockPat(pat.getMrNo());
			}
			this.messageBox("�˻��߱������������������ֲ�ʷ���Ա��棬����ҽ�����ɱ���");
			// this.closeWindow();
			return "OK";
		}

		return "";
	}

	/**
	 * �������ʷ
	 */
	public void onImportMedHistory() {
		TTable table = (TTable) this.getComponent(this.TABLEDIAGNOSIS);
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ���������ʷ����ϣ�");
		} else {
			TParm parm = table.getDataStore()
					.getRowParm(table.getSelectedRow());
			if (parm == null || "".equals(parm.getValue("ICD_CODE"))) {
				this.messageBox("�������������ʷ����ϣ�");
			} else {
				TTable tableMedHistory = (TTable) this
						.getComponent(TABLEMEDHISTORY);
				tableMedHistory.acceptText();
				int rowNo = tableMedHistory.getRowCount() - 1;
				// �ж��Ƿ��Ѿ���������ʱ��
				if (!canEdit()) {
					tableMedHistory.setDSValue(rowNo);
					this.messageBox_("�ѳ�������ʱ�䲻���޸�");
					return;
				}
				if (odo.getMedHistory().isSameICD(parm.getValue("ICD_CODE"))) {
					this.messageBox("E0043"); // ���������ظ����
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				String oldCode = odo.getMedHistory().getItemString(rowNo,
						"ICD_CODE");
				if (!StringUtil.isNullString(oldCode)) {
					this.messageBox("E0040"); // ������������ݣ�������������ɾ��������`
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				odo.getMedHistory().setActive(rowNo, true);

				odo.getMedHistory().setItem(rowNo, "ICD_CODE",
						parm.getValue("ICD_CODE"));
				odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
						parm.getValue("ICD_TYPE"));
				odo.getMedHistory().setItem(rowNo, "SEQ_NO",
						odo.getMedHistory().getMaxSEQ(odo.getMrNo()));
				if (rowNo == tableMedHistory.getRowCount() - 1)
					odo.getMedHistory().insertRow();
				tableMedHistory.setDSValue();
				tableMedHistory.getTable().grabFocus();
				tableMedHistory
						.setSelectedRow(odo.getMedHistory().rowCount() - 1);
				tableMedHistory.setSelectedColumn(2);
			}
		}
	}

	// /**
	// * Ϊ������ҩ�ӿ�׼������������Ϣ
	// * @param patInfo
	// * @param regPatAdm
	// * @return
	// */
	// public String[] getPatInfo(ODO odo){
	// /**
	// * PatientID String ���˲�����ţ����봫ֵ��
	// VisitID String ��ǰ������������봫ֵ��
	// Name String �������� �����봫ֵ��
	// Sex String �����Ա� �����봫ֵ���磺�С�Ů������ֵ����δ֪��
	// Birthday String �������� �����봫ֵ����ʽ��2005-09-20
	// Weight String ���� �����Բ���ֵ����λ��KG
	// Height String ��� �����Բ���ֵ����λ��CM
	// DepartmentName String ҽ������ID/ҽ���������� �����Բ���ֵ��
	// Doctor String ����ҽ��ID/����ҽ������ �����Բ���ֵ��
	// LeaveHospitalDate String ��Ժ���� �����Բ���ֵ��
	// */
	// String[] result=new String[10];
	// result[0]=odo.getMrNo();//MR_NO
	// result[1]="1";//��ǰ�������
	// String sexDesc=TJDODbTool.
	// result[2]="";
	// return result;
	// }

	// zhangyong20110616
	/**
	 * ȡ�óɱ�����
	 * 
	 * @param dept_code
	 *            String
	 * @return String
	 */
	private String getCostCenter(String dept_code) {
		return DeptTool.getInstance().getCostCenter(dept_code, "");
	}

	/**
	 * �ϴ�EMR
	 * 
	 * @param obj
	 *            Object
	 * @param fileName
	 *            String
	 * @param classCode
	 *            String
	 * @param subClassCode
	 *            String
	 */
	private void saveEMR(Object obj, String fileName, String classCode,
			String subClassCode) {
		EMRTool emrTool = new EMRTool(odo.getCaseNo(), odo.getMrNo(), this);
		emrTool.saveEMR(obj, fileName, classCode, subClassCode);
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ������� ==========pangben modify 20110706
	 */
	public void onShow() {
		TTable table = ((TTable) this.getComponent(TABLEPAT));
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// �õ���ǰʹ�õ�ip��ַ
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("EMRWebInitServlet?Mr_No=");
			// ������ҳ����
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("MR_NO"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @param FLG
	 *            String ִ�пۿ�
	 * @param FLG
	 *            double �ܷ���
	 * @return boolean ====================pangben 20110915 flg ��false û�����ִ�е�ҽ��
	 *         true ����ӻ�ɾ����ҽ�� boolean flg ҽ�������޸�ҽ��ʹ�� true ɾ��ҽ���շѲ��� false ����޸�ҽ��
	 *         �շ�
	 */
	private boolean onEktSave(TParm orderParm, TParm ektSumExeParm) {
		int type = 0;
		// ��ִ���ݴ�ҽ���� ���ҽ�ƿ�����ʷ��¼�жϴ˴β���ҽ���Ƿ��޸�ҽ��
		TParm parm = new TParm();
		// TParm detailParm = null;
		// ���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
		if (EKTIO.getInstance().ektSwitch()) { // ҽ�ƿ����أ���¼�ں�̨config�ļ���
			parm = onOpenCard(orderParm, ektSumExeParm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == -1) {
				this.messageBox("��������!");
				return false;
			}
			if (type == 5) {
				return false;
			}
			odo.setTredeNo(parm.getValue("TRADE_NO"));
			tredeNo = parm.getValue("TRADE_NO");
			// System.out.println("ҽ�ƿ��������" + parm);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0005");
				return false;
			}
			if (Operator.getSpcFlg().equals("Y")
					&& ektSumExeParm.getValue("PHA_RX_NO").length() > 0) {
				onSendInw(ektSumExeParm,true);//==pangben 2013-12-18
			}
			if (null != parm.getValue("OPD_UN_FLG")
					&& parm.getValue("OPD_UN_FLG").equals("Y")) {
				TParm tempParm = new TParm();
				tempParm.setData("CASE_NO", caseNo);
				// parm.setData("MR_NO", getValue("MR_NO"));
				TParm reg = OPDAbnormalRegTool.getInstance().selectRegForOPD(
						tempParm);
				wc = "W"; // Ĭ��Ϊ��ҽ
				this.initOpd(reg, 0);
			} else {
				// ����HL7
				try {
					sendHL7Mes();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			String re = EKTIO.getInstance().check(tredeNo, reg.caseNo());
			if (re != null && re.length() > 0) {
				this.messageBox_(re);
				this.messageBox_("����������Ϣ������ϵ");
				// deleteLisPosc = false;
				onExeFee();
				return false;
			}

		} else {
			this.messageBox_("ҽ�ƿ��ӿ�δ����");
			// deleteLisPosc = false;
			return false;
		}
		// �շѳɹ�����ˢ�µ�ǰ����
		// onClear();
		//ektDeleteOrder = false;// ɾ������ִ��
		//isFee = false;// ִ���շ��Ժ󲻿����ٴ�ִ���շ�
		//deleteLisPosc = false;
		onExeFee();
		return true;

	}

	/**
	 * ���ҽ��ܿر�ʶ
	 */
	public String getctrflg(String order_code) {
		String ctr_flg;
		TParm flg = SYSFeeTool.getInstance().getCtrFlg(order_code);
		ctr_flg = flg.getValue("CRT_FLG", 0);
		return ctr_flg;
	}

	/**
	 * �����������ʹ��
	 */
	public void onSpecialCase() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// ��ѯ�Ƿ���Դ����������:�Һ�ҽ�����ز�������ʹ��
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "��" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("�˾��ﲡ��������ҽ������");
			return;
		}
		TParm spcParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\InsSpcMemoDiag.x", result.getData());
		if (null == spcParm || null == spcParm.getValue("SPECIAL_CASE")) {
			return;
		}
		parm.setData("SPC_MEMO", spcParm.getValue("SPECIAL_CASE"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		result = INSMZConfirmTool.getInstance().updateInsMZConfirmSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "��" + result.getErrText());
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005");
	}

	/**
	 * ҽ��������ѯ
	 */
	public void onINSDrQueryList() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CASE_NO", reg.caseNo());
		// �Ƿ���ڲ���
		TParm result = INSMZConfirmTool.getInstance().selectSpcMemo(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + "��" + result.getErrText());
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("�˾��ﲡ��������ҽ������");
			return;
		}
		this.openDialog("%ROOT%\\config\\ins\\INSDrQueryList.x", result
				.getData());
	}

	/**
	 * У��ҽ�ƿ�ɾ������ rxFlg false : ɾ�����Ŵ���ǩ����ʹ�� true :ɾ����������qi
	 */
	public boolean ektDelete(OpdOrder order, int row) {
		// ִ��ҽ�ƿ��������ж��Ƿ��Ѿ�����ʹ��ҽ�ƿ�
		if (!order.isRemovable(row, false)) {// FALSE : �Ѿ��շ� Ҫִ�� onFee() ����
			// TRUE : δ�շ� ��ִ��onFee() ����
			//ektDeleteOrder = false;
			return false;
		} 
		return true;
	}
	/**
	 * У�� �������Ѿ��Ǽǵ����ݲ���ɾ������
	 * STATUS=2 �ѵǼ�
	 * @param row
	 * @return
	 * ===========pangben 2013-1-29
	 */
	public boolean medAppyCheckDate(OpdOrder order, int row){
		if(!order.isRemoveMedAppCheckDate(row)){
			return false;
		}
		return true;
	}
	private boolean readEKT() {
		ektReadParm = EKTIO.getInstance().TXreadEKT();
		if (ektReadParm.getErrCode() < 0) {
			this.messageBox("ҽ�ƿ���������");
			return false;
		}
		if (!ektReadParm.getValue("MR_NO").equals(getValue("MR_NO"))) {
			this.messageBox("������Ϣ����,��ҽ�ƿ���������Ϊ:"
					+ ektReadParm.getValue("PAT_NAME"));
			ektReadParm = null;
			return false;
		}
		if (null == ektReadParm) {
			this.messageBox("δȷ����ݣ����ҽ�ƿ�");
			return false;
		}
		return true;
	}

	// $$=================add by lx 2011/02/12
	// Start���нӿ�=============================$$//
	/**
	 * �ؽ�
	 */
	public void onReCallNo() {
		// ����ҽʦ���� Varchar 6 000234
		String drCode = Operator.getID() + "|";
		// IP��ַ Varchar 15 172.201.132.123
		String ip = Operator.getIP();

		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doReCall",
				inParm);

	}

	/**
	 * ��һ��
	 */
	public void onNextCallNo() {
		String drCode = Operator.getID() + "|";
		// IP��ַ Varchar 15 172.201.132.123
		String ip = Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", drCode + ip);
		TIOM_AppServer.executeAction("action.device.CallNoAction",
				"doNextCall", inParm);

	}

	// $$=================add by lx 2011/02/12
	// ���нӿ�=============================$$//
	/**
	 * У��ҽ�ƿ� ɾ��ҽ�� ��ӻس� OPD_ORDER ����
	 * 
	 * @param parm
	 */
	private void ektDeleteChackOut(TParm parm) {
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			for (int j = 0; j < OPBTool.getInstance().orderName.length; j++) {
				if (null == parm.getValue(OPBTool.getInstance().orderName[j], i)
						|| parm.getValue(OPBTool.getInstance().orderName[j], i).length() <= 0) {
					parm.setData(OPBTool.getInstance().orderName[j], i, "");
				}
			}

		}
	}

	public void onMTRegister() {
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CASE_NO", reg.caseNo());
		this.openDialog("%ROOT%\\config\\ins\\INSMTReg.x", parm);
	}

	/**
	 * �Ƿ��Ǽ���ҽ��
	 * 
	 * @param row
	 *            int
	 * @param buff
	 *            String
	 * @return boolean
	 */
	public boolean isOrderSet(TParm orderParm) {
		boolean falg = false;
		if (orderParm.getBoolean("SETMAIN_FLG")) {
			falg = true;
		}
		return falg;
	}

	/**
	 * ���ϵͳ������
	 */
	public void onClearMenu() {
		CopyOperator.clearComList();
	}
	/**
	 * �жϴ˴β�����ҽ�������ݿ����Ƿ��Ѿ����ڣ��������,��ִ���շѲ���ʱ���ж�ҽ�ƿ��н���Ƿ����
	 * ������㣬ִ�д˴���ǩ���շѵ�ҽ���˻�ҽ�ƿ���
	 */
	private boolean updateOrderParm(TParm orderParm ,TParm orderOldParm,TParm unParm){
		boolean unFlg = false;

		int unCount = 0;
		// System.out.println("orderParm��������������" + orderParm);
		for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
			if (orderParm.getValue("CAT1_TYPE",i).equals("RIS")
					|| orderParm.getValue("CAT1_TYPE",i).equals("LIS")
					|| orderParm.getValue("ORDER_CODE",i).length() <= 0) {
				continue;
			}
			for (int j = 0; j < orderOldParm.getCount("ORDER_CODE"); j++) {
				if (orderParm.getValue("RX_NO",i).equals(
						orderOldParm.getValue("RX_NO", j))
						&& orderParm.getValue("SEQ_NO",i).equals(
								orderOldParm.getValue("SEQ_NO", j))) {
					if (orderParm.getDouble("AMT",i) == orderOldParm
							.getDouble("AR_AMT", j)) {
						break;
					}
					unParm.setRowData(unCount, orderOldParm, j);// ���ִ���޸ĵ�ҽ��
					unCount++;
					unFlg = true;// �ж��Ƿ����ҽ��
					break;
				}
			}
		}
		unParm.setCount(unCount);
		return unFlg;
	}
	
	/**
	 * ������׼��
	 */
	public void onSingleDise() {//add by wanglong 20121025
		TParm action=new TParm();
		action.setData("ADM_TYPE", "E");//����
		if(this.getValue("MR_NO").equals("")){//û�в������ܽ���
			return;
		}
		action.setData("CASE_NO", caseNo);//�����
		TParm result = (TParm) this.openDialog("%ROOT%\\config\\clp\\CLPSingleDise.x",// �������������ѯCASE_NO
				action);
		String diseCode=result.getValue("DISE_CODE");
		for (int row = 0; row < parmpat.getCount(); row++) {
			if(parmpat.getValue("CASE_NO",row).equals(caseNo)){
				parmpat.setData("DISE_CODE", row, diseCode);//���Ĳ��������еĵ�������Ϣ
			}
			
		}
		this.callFunction("UI|TABLEPAT|setParmValue", parmpat);
	}
	
	/**
	 * ���ҩƷ�Ƿ��Ѿ����䷢ �Ƿ������ҩ
	 * 
	 * @param type
	 *            String "EXA":��ҩ "CHN":��ҩ
	 * @param row
	 *            int
	 *            flg =true���鵱ǰ�������޸�ҽ������ɾ��ҽ�����޸�ҽ��������ҩ�������޸ĵ��ǿ���ɾ��
	 * @return boolean
	 */
	public boolean checkDrugCanUpdate(OpdOrder order, String type, int row,
			boolean flg,TParm spcParm) {
		boolean needExamineFlg = false;
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("MED".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		}
		// �������ҩ ��˻���ҩ��Ͳ������ٽ����޸Ļ���ɾ��
		if ("CHN".equals(type)) {
			// �ж��Ƿ����
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		}
		TParm spcReturn=new TParm();
		if (null==spcParm) {
			String caseNo = order.getCaseNo();
			String rxNo = order.getRowParm(row).getValue("RX_NO");
			String seqNo = order.getRowParm(row).getValue("SEQ_NO");
			spcParm = new TParm();
			spcParm.setData("CASE_NO", caseNo);
			spcParm.setData("RX_NO", rxNo);
			spcParm.setData("SEQ_NO", seqNo);
			spcReturn = TIOM_AppServer.executeAction(
					"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
		}else{
			spcReturn=spcParm;
		}
//		PHADosageWsImplService_Client phaDosageWsImplServiceClient = new PHADosageWsImplService_Client();
//		SpcOpdOrderReturnDto spcOpdOrderReturnDto = phaDosageWsImplServiceClient.getPhaStateReturn(caseNo, rxNo, seqNo);
//		if(spcOpdOrderReturnDto == null){
//			return true;
//		}
		if(spcReturn.getErrCode()==-2){
			return true;
		}
		// ������������ ��ô�ж����ҽʦ�Ƿ�Ϊ��
		if (needExamineFlg) {
			// ��������Ա���� ��������ҩ��Ա ��ô��ʾҩƷ����� ���������޸�
			if(flg){//============pangben 2013-4-17 ����޸�ҽ������
//				if (spcOpdOrderReturnDto.getPhaCheckCode().length() > 0) {
//					return false;
//				}
				if (spcReturn.getValue("PhaCheckCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaCheckCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;
				}
			}
		} else {// û��������� ֱ����ҩ
			// �ж��Ƿ�����ҩҩʦ
			if (flg) {// ============pangben 2013-4-17 ����޸�ҽ������
				if (spcReturn.getValue("PhaDosageCode").length() > 0) {
					return false;
				}
			} else {
				if (spcReturn.getValue("PhaDosageCode").length() > 0
						&& spcReturn.getValue("PhaRetnCode").length() == 0) {
					return false;// �Ѿ���ҩ���������޸�
				}
			}
		}
		return true;
	}
	/**
	 * ��ѯ���ﲡ�����
	 * ======pangben 2013-3-28
	 */
	public void onMrSearchFee(){
		if (null == caseNo || caseNo.length() <= 0)
			return;
		// �鿴�˾��ﲡ���Ƿ���ҽ�ƿ�����
		if(!readEKT()){
			return ;
		}
		if(!EKTIO.getInstance().ektAyhSwitch()){
			ektReadParm.setData("CASE_NO",caseNo);
			ektReadParm.setData("EKT_TYPE_FLG",1);//1.��ʾ�������ξ����� 2.�ۿ������ʾ�ۿ����
			TParm result = (TParm) this.openDialog("%ROOT%\\config\\opd\\OPDOrderPreviewAmt.x",// �������������ѯCASE_NO
					ektReadParm);
		}
		TParm result = EKTpreDebtTool.getInstance().getMasterAndFee(odo);
		result = (TParm) this.openDialog("%ROOT%\\config\\opd\\OPDOrderPreviewAmtForPre.x",// �������������ѯCASE_NO
				result);
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
		if (client1 == null)
			return;
		client1.close();
	}
	/**
	 * У���������Ƿ��Ѿ����
	 * @param order
	 * @return
	 */
	private boolean checkSpcPha(OpdOrder order){
		for (int i = 0; i < order.rowCount(); i++) {
			if (i - 1 >= 0) {// �Ѿ����䷢�Ĵ���ǩ�����������ҽ��
				//===pangben 2013-7-23 �޸���������ʾ��Ϣ
				String caseNo = order.getCaseNo();
				String rxNo = order.getRowParm(i - 1).getValue("RX_NO");
				String seqNo = order.getRowParm(i - 1).getValue("SEQ_NO");
				TParm spcParm = new TParm();
				spcParm.setData("CASE_NO", caseNo);
				spcParm.setData("RX_NO", rxNo);
				spcParm.setData("SEQ_NO", seqNo);
				TParm spcReturn = TIOM_AppServer.executeAction(
						"action.opb.OPBSPCAction", "getPhaStateReturn", spcParm);
				if (!this.checkDrugCanUpdate(order, "MED", i - 1, true,spcReturn)) { // �ж��Ƿ��������ҽ��
					if(spcReturn.getValue("PhaRetnCode").length()>0)
						this.messageBox("�Ѿ���ҩ,��ɾ������ǩ����");
					else
						this.messageBox("�˴����Ѿ����,�����޸Ĳ���");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
     * ������ҩ--ҩƷ��Ϣ��ѯ
     */
    public void onQueryRationalDrugUse() {// add by wanglong 20130522
        if (!passIsReady) {
            messageBox("������ҩδ����");
            return;
        }
        if (!PassTool.getInstance().init()) {
            this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
            return;
        }
        int tabbedIndex = ((TTabbedPane) this.getComponent("TTABPANELORDER")).getSelectedIndex();
        int row = -1;
        String orderCode = "";
        switch (tabbedIndex) {
            case 2:
                row = tblMed.getSelectedRow();
                orderCode = tblMed.getDataStore().getItemString(row, "ORDER_CODE");
                break;
            case 3:
                row = tblChn.getSelectedRow();
                orderCode = tblChn.getDataStore().getItemString(row, "ORDER_CODE");
                break;
            case 4:
                row = tblCtrl.getSelectedRow();
                orderCode = tblCtrl.getDataStore().getItemString(row, "ORDER_CODE");
                break;
        }
        if (row < 0) {
            return;
        }
        String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
        if (value == null || value.length() == 0) {
            return;
        }
        int conmmand = Integer.parseInt(value);
        if (conmmand != 6) {
            PassTool.getInstance().setQueryDrug(orderCode, conmmand);
        } else {
            PassTool.getInstance().setWarnDrug2("", "");
        }
    }
    
    /**
     * ��ʾҽ���Ƿ�Ԥ�����
     * yanjing 20131226
     */
    public void onPreOrder(){
    	if(this.getValueString("PREORDER").equals("Y")){
    		//�����Ƿ���Ԥ������״̬
    		odo.getOpdOrder().setPreOrder(true);
    	}else if(this.getValueString("PREORDER").equals("N")){
    		odo.getOpdOrder().setPreOrder(false);
    	}
    	if (!odo.onQuery()) {
			this.messageBox("E0024"); // ��ʼ������ʧ��
			//deleteLisPosc = false;
			return;
		}
    	if (!initSetTable(TABLE_EXA, true))
			this.messageBox("E0026"); // ��ʼ��������ʧ��
    	  initPanel();
    }
  
    
    public void viewClinicFee(){
    	double clinicFee = EKTpreDebtTool.getInstance().getClinicFee(caseNo);
    	setValue("CLINIC_FEE", StringTool.round(clinicFee,2));
    }
}
