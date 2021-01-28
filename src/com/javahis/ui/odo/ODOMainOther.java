package com.javahis.ui.odo;

import java.awt.Component;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import jdo.hl7.Hl7Communications;
import jdo.odo.Diagrec;
import jdo.odo.DrugAllergy;
import jdo.odo.MedHistory;
import jdo.odo.ODO;
import jdo.odo.OpdOrder;
import jdo.odo.OpdRxSheetTool;
import jdo.opb.OPBTool;
import jdo.opd.ODOTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TRootPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.EMRTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.OrderUtil;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 *
 * Title: ����ҽ������վ��������
 * </p>
 *
 * <p>
 * Description:����ҽ������վ��������
 * </p>
 *
 * <p>
 * Company:Bluecore
 * </p>
 *
 * @author zhangp 2013.08.20
 * @version 3.0
 */
public class ODOMainOther implements DMessageIO {

	public OdoMainControl odoMainControl;
	public ODOMainReg odoMainReg;
	public ODOMainOpdOrder odoMainOpdOrder;
	public ODOMainPat odoMainPat;
	public ODOMainTmplt odoMainTmplt;
	// �����ϵĽṹ������
	public TWord word, familyWord,mWord,allegyWord;
	// �����ϵ�TABLE
	public TTable tblMedHistory, tblAllergy, tblDiag;
	public final static String TABLEDIAGNOSIS = "TABLEDIAGNOSIS";
	public final static String TABLEMEDHISTORY = "TABLEMEDHISTORY";
	public static final String TABLEALLERGY = "TABLEALLERGY";
	public static final String TAGTWORD = "TWORD";
	private static final String TAGDIAGNOSISPANEL = "DIAGNOSISPANEL";
	private static final String TAGTMOVEPANE_1 = "tMovePane_1";
	private static final String TAGTMOVEPANE_0 = "tMovePane_0";
	private static final String TAGCOMM_MENU = "COMM_MENU";
	private static final String TAGW_FLG = "W_FLG";
	private static final String TAGC_FLG = "C_FLG";
	private static final String TAGCHN_DSNAME = "CHN_DSNAME";
	private static final String TAGFAMILY_WORD = "FAMILY_WORD";
	private static final String TAGTABLEDIAGNOSIS = "TABLEDIAGNOSIS";
	private static final String TAGTTABPANELDIAG = "TTABPANELDIAG";
	private static final String TAGPREMATURE_FLG = "PREMATURE_FLG";
	private static final String HIGHRISKMATERNAL_FLG = "HIGHRISKMATERNAL_FLG";
	private static final String TAGHANDICAP_FLG = "HANDICAP_FLG";
	private static final String TAGORDER_ALLERGY = "ORDER_ALLERGY";
	private static final String DIAGCHNENGTEXT_E = "Chinese version";
	private static final String DIAGCHNENGHEADER_E = "Main,30,boolean;Code,150;Notes,130;Order Dr,100,DR_CODE;Order Time,120,timestamp,yyyy/MM/dd HH:mm";
	private static final String DIAGCHNENGPARMMAP_E = "MAIN_DIAG_FLG;ICD_ENG_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE";
	private static final String DIAGCHNENGTEXT_C = "Ӣ�Ĳ���";
	private static final String DIAGCHNENGHEADER_C = "��,30,boolean;����,150;��ע,130;����ҽ��,100,DR_CODE;����ʱ��,120,timestamp,yyyy/MM/dd HH:mm";
	private static final String DIAGCHNENGPARMMAP_C = "MAIN_DIAG_FLG;ICD_DESC;DIAG_NOTE;DR_CODE;ORDER_DATE";

	private static final String NULLSTR = "";

	// ���Ѿ�����Ĳ����Ľṹ����������Ҫ�Ĵ洢·��
	public String[] saveFiles, familyHisFiles;
	private String allergyType = "B";// ��ʼĬ�ϵĹ�������
	public TParm sendHL7Parm;
	// �������߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	private String focusTag;
	private static final String MEGOVERTIME = "�ѳ�������ʱ�䲻���޸�";
	private static final String URLSYSICDPOPUP = "%ROOT%\\config\\sys\\SYSICDPopup.x";
	private static final String URLCLPSINGLEDISE = "%ROOT%\\config\\clp\\CLPSingleDise.x";
	private static final String URLODOEMRTEMPLET = "%ROOT%\\config\\opd\\ODOEmrTemplet.x";
	private static final String URLADMRESV = "%ROOT%\\config\\adm\\ADMResv.x";
	private static final String URLERDDYNAMICRCD = "%ROOT%\\config\\erd\\ERDDynamicRcd.x";
	private static final String URLMROINFECT = "%ROOT%/config/mro/MROInfect.x";
	private static final String URLSUMVITALSIGN = "%ROOT%\\config\\sum\\SUMVitalSign.x";
	private static final String URLTEMRWORDUI = "%ROOT%\\config\\emr\\TEmrWordUI.x";
	private static final String URLEMRCOMPHRASEQUOTE = "%ROOT%\\config\\emr\\EMRComPhraseQuote.x";
	private static final String URLEMRMEDDATAUI = "%ROOT%\\config\\emr\\EMRMEDDataUI.x";
	private static final String URLINWORDERSHEETPRTANDPREVIEW = "%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x";
	private static final String URLOPEOPBOOK = "%ROOT%\\config\\ope\\OPEOpBook.x";
	private static final String URLOPEOPDETAIL = "%ROOT%\\config\\ope\\OPEOpDetail.x";
	private static final String URLOPDCOMPACKENTERNAME = "%ROOT%\\config\\opd\\OPDComPackEnterName.x";
	private static final String URLSYSFEEPOPUP = "%ROOT%\\config\\sys\\SYSFeePopup.x";
	private static final String URLSYSALLERGY = "%ROOT%\\config\\sys\\SysAllergy.x";
	private static final String URLOPDCHNORDERSHEET = "%ROOT%\\config\\prt\\OPD\\OpdChnOrderSheet.jhw";
	private static final String URLOPDDRUGSHEET = "%ROOT%\\config\\prt\\OPD\\OpdDrugSheet_V45.jhw";
	private static final String URLOPDORDERSHEET = "%ROOT%\\config\\prt\\OPD\\OpdOrderSheet_V45.jhw";
	private static final String URLOPDNEWEXASHEET = "%ROOT%\\config\\prt\\OPD\\OpdNewExaSheet.jhw";
	private static final String URLOPDNEWEXASHEET_1 = "%ROOT%\\config\\prt\\OPD\\OPDApplicAtion.jhw";
	private static final String URLOPDCASESHEET1010 = "%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010_V45.jhw";
	private static final String URLEMG = "%ROOT%\\config\\prt\\OPD\\EMG_V45.jhw";
	private static final String URLOPDNEWHANDLESHEET = "OpdNewHandleSheet_V45.jhw";//
	private static final String URLODOCASESHEET = "%ROOT%\\config\\opd\\ODOCaseSheet.x";
	private static final String URLEMRSINGLEUI = "%ROOT%\\config\\emr\\EMRSingleUI.x";
	private static final String URLBOTTLELABEL = "OpdBottleLabelOrder_V45.jhw";
	private static final String URLBOTTLELABEL_I = "OpdBottleLabelOrder_I_V45.jhw";
	public ODOMainOther(OdoMainControl odoMainControl) {
		this.odoMainControl = odoMainControl;
	}

	public void onInit() throws Exception {
		this.odoMainReg = odoMainControl.odoMainReg;
		this.odoMainOpdOrder = odoMainControl.odoMainOpdOrder;
		this.odoMainPat = odoMainControl.odoMainPat;	
		onInitEvent();
	}

	/**
	 * ע��ؼ����¼�
	 */
	public void onInitEvent() throws Exception {
		// ��ϵ��¼�
		tblDiag = (TTable) odoMainControl.getComponent(TABLEDIAGNOSIS);
		tblDiag.addEventListener(TABLEDIAGNOSIS + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onDiagTableChangeValue");
		tblDiag.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tblDiag.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");
		// ����ʷTABLE
		tblMedHistory = (TTable) odoMainControl.getComponent(TABLEMEDHISTORY);
		tblMedHistory.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onMedHistoryCreateEditComponent");
		tblMedHistory.addEventListener(TABLEMEDHISTORY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onMedHistoryChangeValue");
		// ����ʷTABLE
		tblAllergy = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		tblAllergy.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onAllergyCreateEditComponent");
		tblAllergy.addEventListener(TABLEALLERGY + "->"
				+ TTableEvent.CHANGE_VALUE, this, "onAllergyChangeValue");
		// �ṹ������
		word = (TWord) odoMainControl.getComponent(TAGTWORD);
		allegyWord = (TWord) odoMainControl.getComponent("ALLEGY_WORD");//��ȡ�����ɼ�ģ��
		this.setmWord(word);
		
		familyWord = (TWord) odoMainControl.getComponent(TAGFAMILY_WORD);

		// ��Ӳ����û�
		word.getPM().setUser(Operator.getID(),
				this.getOperatorName(Operator.getID()));
	}
	
	
	
	

	public TWord getmWord() {
		return mWord;
	}

	public void setmWord(TWord mWord) {
		this.mWord = mWord;
	}

	/**
	 * �õ��û���
	 *
	 * @param userID
	 *            String
	 * @return String
	 */
	private String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
						+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * ���������
	 */
	public void onOtherClear() throws Exception {
		// ������
		odoMainControl.setValue(TAGCHN_DSNAME, false);// �����Ӣ��
		odoMainControl.setValue(TAGW_FLG, true);// �������ҽ
		odoMainControl.setValue(TAGPREMATURE_FLG, false);// �����
		odoMainControl.setValue(TAGHANDICAP_FLG, false);// �м�
		odoMainControl.setValue(HIGHRISKMATERNAL_FLG, false);// ��Σ����
		odoMainControl.setValue(TAGORDER_ALLERGY, true);// ҩƷ����
		tblMedHistory.removeRowAll();
		tblAllergy.removeRowAll();
		tblDiag.removeRowAll();
		word.onNewFile();
		word.update();
		familyWord.onNewFile();
		familyWord.update();
	}

	/**
	 * ��ʼ������panel
	 */
	public void initPanel() throws Exception {
		initSubject();// ���߿���
		initDiag();// ���
		initMedHistory();// ����ʷ
		initAllergy();// ����ʷ
		onDiagPnChange();// ���ҳǩ��ѡ���������м���ʷ�����ʷҳǩͷ�ͱ�ɫ
	}

	/**
	 * ����ROOT_PANEL�Ŀ��
	 */
	public void setRootPanelWidth() throws Exception {
		TPanel diag = (TPanel) odoMainControl.getComponent(TAGDIAGNOSISPANEL);
		diag.setWidth(418);
		TMovePane upDown = (TMovePane) odoMainControl
				.getComponent(TAGTMOVEPANE_1);
		upDown.setWidth(1088);
		TTabbedPane order = (TTabbedPane) odoMainControl
				.getComponent(ODOMainOpdOrder.TAGTTABPANELORDER);
		order.setWidth(1088);
		TMovePane leftRight = (TMovePane) odoMainControl
				.getComponent(TAGTMOVEPANE_0);
		leftRight.setX(1100);
		TRootPanel menu = (TRootPanel) odoMainControl
				.getComponent(TAGCOMM_MENU);
		menu.setX(1120);
		menu.setWidth(120);
	}

	/**
	 * ��ʼ�����߿���
	 */
	public void initSubject() throws Exception {
		TParm parm = new TParm();
		parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		parm.setData("TYPE", "ZS");
		// TParm microParm=new TParm();
		TParm allParm = new TParm();
		if ("N".equalsIgnoreCase(odoMainControl.odo.getRegPatAdm()
				.getItemString(0, "SEE_DR_FLG"))) {
			//����һ����ֹ�����emr_file_index�Ѵ��ڱ������߲����ļ������
			saveFiles= EmrUtil.getInstance().getGSFile(
					odoMainControl.odo.getCaseNo());
			//1.����SEE_DR_FLGĳЩ����²�׼ȷ������EMR�����Ƿ��Ѿ��������߲���
			if(!saveFiles[1].equals("")){
				word.onOpen(saveFiles[0], saveFiles[1], 3, false);
				allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
				allParm.addListener("onMouseRightPressed", this,
						"onMouseRightPressed");
				word.setWordParameter(allParm);
				word.setCanEdit(true);
				// �����Ƿ��ӡ
				this.doProcessModifyNode(saveFiles[3]);
			//2.ȷ����δ��������������߲����������
			}else{
				// zhangyong20110427
				saveFiles = EmrUtil.getInstance().getGSTemplet(
						odoMainReg.realDeptCode, Operator.getID(),
						odoMainReg.admType);
	
				word.onOpen(saveFiles[0], saveFiles[1], 2, false);
				allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
				allParm.addListener("onMouseRightPressed", this,
						"onMouseRightPressed");
				word.setWordParameter(allParm);
				word.setCanEdit(true);
	
				// ����δ��ӡ
				this.doProcessModifyNode( null );
			}
		} else {
			saveFiles = EmrUtil.getInstance().getGSFile(
					odoMainControl.odo.getCaseNo());

			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
			allParm.addListener("onMouseRightPressed", this,
					"onMouseRightPressed");
			word.setWordParameter(allParm);
			word.setCanEdit(true);
			// �����Ƿ��ӡ
			this.doProcessModifyNode( saveFiles[3]);
		}
		// $$=========== add by lx 2012/02/24/ ����
		word.fixedTryReset(odoMainControl.odo.getMrNo(), odoMainControl.odo
				.getCaseNo());
		// $$=========== add by lx 2012/02/24/ ���� ˢ��ץȡ����
		familyHisFiles = EmrUtil.getInstance().getFamilyHistoryPathByCaseNo(
				odoMainControl.odo.getCaseNo(), odoMainReg.realDeptCode,
				odoMainReg.admType);
		familyWord.onOpen(familyHisFiles[0], familyHisFiles[1], Integer
				.parseInt(familyHisFiles[2]), false);
		TParm familyParm = new TParm();
		familyParm
				.addListener("onDoubleClicked", this, "onFamilyDoubleClicked");
		familyWord.setWordParameter(familyParm);
		// add by wangb ����򿪼���ʷģ�壬�����ݴ���ģ��
		if ("2".equals(familyHisFiles[2])) {
			EmrUtil.getInstance().setCaptureValue(familyWord, "FAMILY_HISTORY", odoMainPat.pat.getFamilyHistory());
			EmrUtil.getInstance().setCaptureValue(familyWord, "PAST_HISTORY", odoMainPat.pat.getPastHistory());
			EmrUtil.getInstance().setCaptureValue(familyWord, "OP_BLOOD_HISTORY", odoMainPat.pat.getOpBloodHistory());
		}
	}
	
	/**
	 *
	 * @param printuser
	 */
	private void doProcessModifyNode(String printuser){

		if ( !StringUtil.isNullString(printuser) ) {
			word.getPM().getModifyNodeManager().setIndex(1);
		} else {
			word.getPM().getModifyNodeManager().setIndex(-1);
		}
	}

	/**
	 * ��ʼ�����
	 */
	public void initDiag() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEDIAGNOSIS);
		odoMainControl.odo.setIcdType(odoMainOpdOrder.wc);

		TRadioButton w = (TRadioButton) odoMainControl.getComponent(TAGW_FLG);
		TRadioButton c = (TRadioButton) odoMainControl.getComponent(TAGC_FLG);
		if (ODOMainOpdOrder.W.equalsIgnoreCase(odoMainOpdOrder.wc)) {
			w.setValue("Y");
		} else {
			c.setValue("Y");
		}
		w = null;
		c = null;
		table.setDataStore(odoMainControl.odo.getDiagrec());
		table.setDSValue();
	}

	/**
	 * ��ʼ������ʷ
	 */
	public void initMedHistory() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEMEDHISTORY);
		table.setDataStore(odoMainControl.odo.getMedHistory());
		table.setDSValue();
	}

	/**
	 * ��ʼ������ʷ
	 */
	public void initAllergy() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		DrugAllergy all = odoMainControl.odo.getDrugAllergy();
		table.setDataStore(all);
		all.setFilter("DRUG_TYPE='B'");
		all.filter();
		table.setDSValue();
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
	public void onDiagCreateEditComponent(Component com, int row, int column)
			throws Exception {
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		// tempIcd = odo.getDiagrec().getItemString(row, "ICD_CODE");
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����sys_fee��������
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", odoMainOpdOrder.wc);
		parm.setData("DIAG_DEPT_CODE", odoMainOpdOrder.diagDeptCode);	//add by huangtt 20150302	
		textfield.setPopupMenuParameter("ICD", odoMainControl.getConfigParm()
				.newConfig(URLSYSICDPOPUP), parm);
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
			int column) throws Exception {
		if (column != 2) {
			return;
		}
		if (!(com instanceof TTextField)) {
			return;
		}
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		String wcMed = odoMainControl.odo.getMedHistory().getItemString(row,
				"ICD_TYPE");
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", wcMed);
		// ��table�ϵ���text����sys_fee��������
		textfield.setPopupMenuParameter("ICD", odoMainControl.getConfigParm()
				.newConfig(URLSYSICDPOPUP), parm);
		// ����text���ӽ���sys_fee�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popMedHistoryReturn");

	}

	/**
	 * ����ʷ���
	 *
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popAllergyReturn(String tag, Object obj) throws Exception {
		TParm parm = (TParm) obj;
		if (StringUtil.isNullString(TABLEALLERGY)) {
			odoMainControl.messageBox("E0034"); // ȡ�����ݴ���
			return;
		}
		TTable table = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		table.acceptText();
		int row = table.getSelectedRow();
		String desc;
		String oldCode = odoMainControl.odo.getDrugAllergy().getItemString(row,
				"DRUGORINGRD_CODE");
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!odoMainReg.canEdit()) {
			table.setDSValue(row);
			odoMainControl.messageBox_(MEGOVERTIME);
			return;
		}
		if (!StringUtil.isNullString(oldCode)) {
			odoMainControl.messageBox("E0040"); // ������������ݣ�������������ɾ��������
			table.setDSValue(row);
			return;
		}
		if (StringTool.getBoolean(odoMainControl
				.getValueString(TAGORDER_ALLERGY))) {
			odoMainControl.odo.getDrugAllergy().setItem(row,
					"DRUGORINGRD_CODE", parm.getValue("ORDER_CODE"));
			odoMainControl.odo.getDrugAllergy().setItem(row, "DRUG_TYPE",
					allergyType);
			desc = "B";
		} else if (StringTool.getBoolean(odoMainControl
				.getValueString("INGDT_ALLERGY"))) {
			odoMainControl.odo.getDrugAllergy().setItem(row,
					"DRUGORINGRD_CODE", parm.getValue("ID"));
			odoMainControl.odo.getDrugAllergy().setItem(row, "DRUG_TYPE",
					allergyType);
			desc = "A";
		} else {
			odoMainControl.odo.getDrugAllergy().setItem(row,
					"DRUGORINGRD_CODE", parm.getValue("ID"));
			odoMainControl.odo.getDrugAllergy().setItem(row, "DRUG_TYPE",
					allergyType);
			desc = "C";
		}
		odoMainControl.odo.getDrugAllergy().setActive(table.getSelectedRow(),
				true);
		int newRow = 0;
		if (table.getSelectedRow() == table.getRowCount() - 1) {
			newRow = odoMainControl.odo.getDrugAllergy().insertRow();
			odoMainControl.odo.getDrugAllergy().setItem(newRow, "DRUG_TYPE",
					desc);
		}
		table.setDSValue();
		table.getTable().grabFocus();
		table.setSelectedRow(newRow);
		table.setSelectedColumn(2);
	}

	/**
	 * ������׼��
	 */
	public void onSingleDise() throws Exception {// add by wanglong 20121025
		TParm action = new TParm();
		action.setData("ADM_TYPE", ODOMainReg.E);// ����
		if (odoMainControl.getValue("MR_NO").equals(NULLSTR)) {// û�в������ܽ���
			return;
		}
		action.setData("CASE_NO", odoMainControl.caseNo);// �����
		TParm result = (TParm) odoMainControl.openDialog(URLCLPSINGLEDISE,// �������������ѯCASE_NO
				action);
		String diseCode = result.getValue("DISE_CODE");
		for (int row = 0; row < odoMainReg.parmpat.getCount(); row++) {
			if (odoMainReg.parmpat.getValue("CASE_NO", row).equals(
					odoMainControl.caseNo)) {
				odoMainReg.parmpat.setData("DISE_CODE", row, diseCode);// ���Ĳ��������еĵ�������Ϣ
			}
		}
		odoMainControl.callFunction("UI|TABLEPAT|setParmValue",
				odoMainReg.parmpat);
	}

	/**
	 * �������ʷ
	 */
	public void onImportMedHistory() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEDIAGNOSIS);
		if (table.getSelectedRow() < 0) {
			odoMainControl.messageBox("��ѡ���������ʷ����ϣ�");
		} else {
			TParm parm = table.getDataStore()
					.getRowParm(table.getSelectedRow());
			if (parm == null || NULLSTR.equals(parm.getValue("ICD_CODE"))) {
				odoMainControl.messageBox("�������������ʷ����ϣ�");
			} else {
				TTable tableMedHistory = (TTable) odoMainControl
						.getComponent(TABLEMEDHISTORY);
				tableMedHistory.acceptText();
				int rowNo = tableMedHistory.getRowCount() - 1;
				// �ж��Ƿ��Ѿ���������ʱ��
				if (!odoMainReg.canEdit()) {
					tableMedHistory.setDSValue(rowNo);
					odoMainControl.messageBox_("�ѳ�������ʱ�䲻���޸�");
					return;
				}
				if (odoMainControl.odo.getMedHistory().isSameICD(
						parm.getValue("ICD_CODE"))) {
					odoMainControl.messageBox("E0043"); // ���������ظ����
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				String oldCode = odoMainControl.odo.getMedHistory()
						.getItemString(rowNo, "ICD_CODE");
				if (!StringUtil.isNullString(oldCode)) {
					odoMainControl.messageBox("E0040"); // ������������ݣ�������������ɾ��������`
					tableMedHistory.setDSValue(rowNo);
					return;
				}
				odoMainControl.odo.getMedHistory().setActive(rowNo, true);
				odoMainControl.odo.getMedHistory().setItem(rowNo, "ICD_CODE",
						parm.getValue("ICD_CODE"));
				odoMainControl.odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
						parm.getValue("ICD_TYPE"));
				odoMainControl.odo.getMedHistory().setItem(
						rowNo,
						"SEQ_NO",
						odoMainControl.odo.getMedHistory().getMaxSEQ(
								odoMainControl.odo.getMrNo()));
				if (rowNo == tableMedHistory.getRowCount() - 1)
					odoMainControl.odo.getMedHistory().insertRow();
				tableMedHistory.setDSValue();
				tableMedHistory.getTable().grabFocus();
				tableMedHistory.setSelectedRow(odoMainControl.odo
						.getMedHistory().rowCount() - 1);
				tableMedHistory.setSelectedColumn(2);
			}
		}
	}

	/**
	 * ����ϵ�ѡ�¼����ж��Ƿ���������
	 *
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onDiagMain(Object obj) throws Exception {
		TTable table = (TTable) obj;
		table.acceptText();
		return false;
	}

	/**
	 * ����ʷ�ı��¼�
	 *
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onMedHistoryChangeValue(TTableNode tNode) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEMEDHISTORY);
		int row = table.getSelectedRow();
		if (row < 0)
			return true;
		int column = tNode.getColumn();
		String columnName = table.getParmMap(column);
		if ("ICD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return false;
		}
		return false;
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
	public void onDoubleClicked(int pageIndex, int x, int y) throws Exception {
		//this.odoMainControl.messageBox("---����---");
		EComponent com = word.getFocusManager().getFocus();
		if (com != null && (com instanceof EFixed)) {
			onMarkTextProperty();
		}else{
			// �������û�������ߡ����ߡ���������ʲô������.
			String str = NULLSTR;
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
			} else if (word.focusInCaptue("ASSESSMENT")) {
				str = "ASSESSMENT";
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
	public void onFamilyDoubleClicked(int pageIndex, int x, int y)
			throws Exception {
		// �������û�������ߡ����ߡ���������ʲô������.
		String str = NULLSTR;
		//String str1 = NULLSTR;
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (this.familyWord.focusInCaptue("PAST_HISTORY")) {//����ʷ add by huangjw 20140818
			str = "PAST_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		onInsertFamilyPY();
	}

	/**
	 * �����Ӣ����ʾ
	 */
	public void onChnEng() throws Exception {
		TCheckBox chnEng = (TCheckBox) odoMainControl
				.getComponent(TAGCHN_DSNAME);
		TTable tableDiag = (TTable) odoMainControl
				.getComponent(TAGTABLEDIAGNOSIS);
		if (chnEng.isSelected()) {
			chnEng.setText(DIAGCHNENGTEXT_E);
			// ============xueyf modify 20120220
			tableDiag.setHeader(DIAGCHNENGHEADER_E);
			tableDiag.setParmMap(DIAGCHNENGPARMMAP_E);
			tableDiag.setDSValue();
		} else {
			chnEng.setText(DIAGCHNENGTEXT_C);
			// ============xueyf modify 20120220
			tableDiag.setHeader(DIAGCHNENGHEADER_C);
			tableDiag.setParmMap(DIAGCHNENGPARMMAP_C);
			tableDiag.setDSValue();
		}
	}

	/**
	 * ���ҳǩ��ѡ���������м���ʷ�����ʷҳǩͷ�ͱ�ɫ
	 */
	public void onDiagPnChange() throws Exception {
		if (odoMainControl.odo == null)
			return;
		MedHistory medHistory = odoMainControl.odo.getMedHistory();
		TTabbedPane p = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELDIAG);
		if (medHistory.rowCount() > 1) {
			p.setTabColor(1, OdoMainControl.RED);
		} else {
			p.setTabColor(1, null);
		}
		
		//modify by huangtt 20150629 start
		String filter = odoMainControl.odo.getDrugAllergy().getFilter();
		odoMainControl.odo.getDrugAllergy().setFilter("");
		odoMainControl.odo.getDrugAllergy().filter();
		
		if (odoMainControl.odo.getDrugAllergy().rowCount() > 1) {
		//ADM_DATE_FORMAT;DRUG_TYPE;DRUGORINGRD_DESC;ALLERGY_NOTE;DEPT_CODE;DR_CODE;ADM_TYPE;CASE_NO
			boolean flg = false;
			for (int i = 0; i < odoMainControl.odo.getDrugAllergy().rowCount(); i++) {
				
//				System.out.println(i+"===="+odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUG_TYPE"));
//				System.out.println(i+"===="+odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE"));
				
				if(odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE").toString().length() == 0){
					continue;
				}
				String drugType = odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUG_TYPE").toString();
				if(!("00".equals(odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE")) 
						&& ("A".equals(drugType) || "C".equals(drugType) ) )
						&& !("001".equals(odoMainControl.odo.getDrugAllergy().getItemData(i, "DRUGORINGRD_CODE")) 
								&& "B".equals(drugType))
						){
					flg = true;
				}
			}
			if(flg){
				p.setTabColor(4, OdoMainControl.RED);
			}else{
				p.setTabColor(4, null);
			}
				
			
			
		} else {
			p.setTabColor(4, null);
		}
		
		odoMainControl.odo.getDrugAllergy().setFilter(filter);
		odoMainControl.odo.getDrugAllergy().filter();
		//modify by huangtt 20150629 end
	}

	/**
	 * ���ҳǩ˫���¼����ṹ������ѡ��ģ��
	 */
	public void onChangeTemplate() throws Exception {
		TTabbedPane panel = (TTabbedPane) odoMainControl
				.getComponent(TAGTTABPANELDIAG);
		if (panel.getSelectedIndex() != 0) {
			return;
		}
		if (odoMainControl.odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("SYSTEM_TYPE", "ODO");
		parm.setData("ADM_TYPE", odoMainReg.admType);
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		// zhangyong20110427 begin
		Object obj = odoMainControl.openDialog(URLODOEMRTEMPLET, parm);
		// zhangyong20110427 end
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm action = (TParm) obj;
		String templetPath = action.getValue("TEMPLET_PATH");
		String templetName = action.getValue("EMT_FILENAME");
		word.onOpen(templetPath, templetName, 2, false);
		if (!"N".equalsIgnoreCase(odoMainControl.odo.getRegPatAdm()
				.getItemString(0, "SEE_DR_FLG"))) {
			saveFiles = EmrUtil.getInstance().getGSFile(
					odoMainControl.odo.getCaseNo());
		}
		word.setCanEdit(true);
	}

	/**
	 * LMP����¼������㻳������
	 */
	public void onLmp() throws Exception {
		Timestamp LMP = (Timestamp) odoMainControl.getValue("LMP_DATE");
		String week = "";
		if(odoMainControl.odo.getRegPatAdm().getItemData(
				0, "ADM_DATE") != null){
			 week = OdoUtil
				.getPreWeekNew((Timestamp)odoMainControl.odo.getRegPatAdm().getItemData(
						0, "ADM_DATE"), LMP);
		}else{
			 week = OdoUtil
				.getPreWeekNew(TJDODBTool.getInstance().getDBTime(), LMP);
		}
		
		/*int week = OdoUtil
				.getPreWeek(TJDODBTool.getInstance().getDBTime(), LMP);*/
		odoMainControl.setValue("PRE_WEEK", week);
		//����LMP_DATE����Ԥ����   add by huangjw 20140928 start
		Date date=LMP;
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		odoMainControl.setValue("EDC_DATE", preDate(gc));
		//����LMP_DATE����Ԥ����   add by huangjw 20140928 end
	}
	/**
	 * ����Ԥ���� add by huangjw 20140928
	 * @param Calendar
	 * @return
	 */
	public String preDate(Calendar gc){
//		
//		gc.add(1, 1);
//		gc.add(2, -3);
//		gc.add(5, 7);
		gc.add(Calendar.DATE, 279);// EDC�ļ��㹫ʽ��LMPֱ�Ӽ�279�����õ�EDC
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString=formatter.format(gc.getTime());
		return dateString;
	}
	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastStartDate() throws Exception {
		Timestamp t2 = (Timestamp) odoMainControl
				.getValue("BREASTFEED_ENDDATE");
		if (t2 == null)
			return;
		Timestamp t1 = (Timestamp) odoMainControl
				.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			odoMainControl.messageBox("E0063");
			odoMainControl.setValue("BREASTFEED_STARTDATE", NULLSTR);
			return;
		}
	}

	/**
	 * �����ڵ���¼����������ڲ����ڽ�������
	 */
	public void onBreastEndDate() throws Exception {
		Timestamp t2 = (Timestamp) odoMainControl
				.getValue("BREASTFEED_ENDDATE");
		Timestamp t1 = (Timestamp) odoMainControl
				.getValue("BREASTFEED_STARTDATE");
		if (t1 == null) {
			odoMainControl.messageBox("E0064");
			odoMainControl.setValue("BREASTFEED_ENDDATE", NULLSTR);
			return;
		}
		if (t2 == null) {
			return;
		}
		if (StringTool.getDateDiffer(t1, t2) > 0) {
			odoMainControl.messageBox("E0063");
			odoMainControl.setValue("BREASTFEED_ENDDATE", NULLSTR);
			return;
		}
	}

	/**
	 * ���ߵ���¼�
	 *
	 * @param tag
	 *            String
	 */
	public void onSubjText(String tag) throws Exception {
		// TODO
		odoMainControl.messageBox("���ߵ���¼�û�з���");
	}

	/**
	 * ���߿����ֲ�ʷ�ĵ���¼������õ�ǰ�������ؼ���ý���Ŀؼ���Ϊ��Ƭ�ﷵ��ֵʹ��
	 *
	 * @param tag
	 *            String
	 */
	public void onClick(String tag) throws Exception {
		odoMainControl.messageBox("onClickû�з���");
	}

	/**
	 * סԺԤԼ
	 */
	public void onPreDate() throws Exception {
		if (odoMainControl.odo == null) {
			return;
		}
		// ���ݹҺŲ�������Ч����У���Ƿ���Ծ�����ҽ������===========pangben 2013-4-28
		if (!OPBTool.getInstance().canEdit(odoMainReg.reg,
				odoMainReg.regSysEFFParm)) {
			odoMainControl.messageBox("������ǰ����ʱ��");
			odoMainControl.onClear();
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", odoMainControl.odo.getMrNo());
		parm.setData("ADM_TYPE_ZYZ", odoMainReg.admType);// yanj,20130816,�ż���ʱסԺ֤��ӡ���Զ��ر�
		if (ODOMainReg.O.equalsIgnoreCase(odoMainReg.admType)) {
			parm.setData("ADM_SOURCE", "01");
		} else if (ODOMainReg.E.equalsIgnoreCase(odoMainReg.admType)) {
			parm.setData("ADM_SOURCE", "02");
			parm.setData("CASE_NO", odoMainControl.caseNo);// add by wanglong
															// 20121025
		}
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("DR_CODE", Operator.getID());
		int mainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
		if (mainDiag >= 0) {
			// ѭ��ȡ����������������������� duzhw
			TParm diagParm = getDiagParm();
			// System.out.println("otherDiagParm="+diagParm);
			String description = getDesc(diagParm);
			// System.out.println("description="+description);
			parm.setData("ICD_CODE", odoMainControl.odo.getDiagrec()
					.getItemString(mainDiag, "ICD_CODE"));
			parm.setData("DESCRIPTION", description
					+ odoMainControl.odo.getDiagrec().getItemString(mainDiag,
							"DIAG_NOTE"));
		} else {
			parm.setData("ICD_CODE", NULLSTR);
			parm.setData("DESCRIPTION", NULLSTR);
		}
		parm.setData("ADM_EXE_FLG", "Y");// =====pangben 2013-4-26 ҽ��վִ�в������ٲ���
		odoMainControl.openWindow(URLADMRESV, parm);
	}

	/**
	 * ȡ��������ϵĺ��������(�ż���תסԺ-סԺ֤��ʾ)-duzhw add 20140319
	 *
	 * @return
	 */
	public TParm getDiagParm() {
		TParm parm = new TParm();
		int num = 0;
		for (int i = 0; i < tblDiag.getRowCount() - 1; i++) {
			if (num < 2) {
				// tblDiag.setSelectedRow(tblDiag.getRowCount() - 1);
				// int row = tblDiag.getSelectedRow() - 1;
				String MAIN_DIAG_FLG = odoMainControl.odo.getDiagrec()
						.getItemString(i, "MAIN_DIAG_FLG");
				if (MAIN_DIAG_FLG.equals("Y"))
					continue;
				String diagCode = odoMainControl.odo.getDiagrec()
						.getItemString(i, "ICD_CODE");
				String diagName = odoMainControl.odo.getDiagrec().getIcdDesc(
						diagCode, "");
				// System.out.println("i="+i+" MAIN_DIAG_FLG="+MAIN_DIAG_FLG);
				parm.setData("ICD_CODE", num, diagCode);
				parm.setData("ICD_NAME", num, diagName);
				parm.setCount(num + 1);
				num++;
			}
		}
		return parm;
	}

	/**
	 * ��ȡƴ�ӱ�ע
	 */
	public String getDesc(TParm diagParm) {
		String desc = "";
		for (int i = 0; i < diagParm.getCount(); i++) {
			String description = diagParm.getValue("ICD_NAME", i);
			if (i == diagParm.getCount() - 1) {
				desc += description + " ";
			} else {
				desc += description + ",";
			}

		}
		return desc;
	}

	/**
	 * ��������
	 */
	public void onErd() throws Exception {
		if (odoMainControl.odo == null) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		parm.setData("MR_NO", odoMainControl.odo.getMrNo());
		parm.setData("PAT_NAME", odoMainControl.odo.getPatInfo().getItemString(
				0, "PAT_NAME"));
		parm.setData("FLG", "OPD");
		odoMainControl.openDialog(URLERDDYNAMICRCD, parm);
	}

	/**
	 * ���鱨��
	 */
	public void onLisReport() throws Exception {
		if (odoMainControl.odo == null) {
			return;
		}
		SystemTool.getInstance().OpenLisWeb(odoMainControl.odo.getMrNo());
	}

	/**
	 * ��鱨��
	 */
	public void onRisReport() throws Exception {
		// ���ü��ӿ�
		if (odoMainControl.odo == null) {
			return;
		}
		SystemTool.getInstance().OpenRisWeb(odoMainControl.odo.getMrNo());
	}

	/**
	 * ���ô�Ⱦ�����濨
	 */
	public void onContagionReport() throws Exception {
		if (this.tblDiag == null || odoMainControl.odo == null) {
			return;
		}
		int row = this.tblDiag.getSelectedRow();
		if (row < 0) {
			return;
		}
		Diagrec diag = odoMainControl.odo.getDiagrec();
		if (diag.isContagion(row)) {
			TParm can = new TParm();
			can.setData("MR_NO", odoMainControl.odo.getMrNo());
			can.setData("CASE_NO", odoMainControl.odo.getCaseNo());
			can.setData("ICD_CODE", diag.getItemString(row, "ICD_CODE"));
			can.setData("DEPT_CODE", Operator.getDept());
			can.setData("USER_NAME", Operator.getName());
			can.setData("ADM_TYPE", odoMainControl.odo.getAdmType());// modify
																		// by
																		// wanglong
																		// 20140307
			odoMainControl.openDialog(URLMROINFECT, can);
		}
	}

	/**
	 * �������µ�
	 */
	public void onBodyTemp() throws Exception {
		if (odoMainControl.odo == null) {
			return;
		}
		TParm result = ODOTool.getInstance().getErdBedByCaseNo(
				odoMainControl.odo.getCaseNo());
		TParm sumParm = new TParm();
		sumParm.setData("SUM", "CASE_NO", odoMainControl.odo.getCaseNo());
		sumParm.setData("SUM", "MR_NO", odoMainControl.odo.getMrNo());
		sumParm.setData("SUM", "IPD_NO", NULLSTR);
		sumParm.setData("SUM", "STATION_CODE", result.getData("CHN_DESC", 0));
		sumParm.setData("SUM", "BED_NO", result.getData("BED_DESC", 0));
		sumParm.setData("SUM", "ADM_TYPE", ODOMainReg.E);
		odoMainControl.openDialog(URLSUMVITALSIGN, sumParm);
	}

	/**
	 * �������۲���
	 */
	public void onErdSheet() throws Exception {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE_ZYZ", odoMainReg.admType);// yanj,20130820
		if (ODOMainReg.O.equalsIgnoreCase(odoMainReg.admType)) {
			parm.setData("SYSTEM_TYPE", "ODO");
			parm.setData("ADM_TYPE", ODOMainReg.O);
		} else {
			parm.setData("SYSTEM_TYPE", "EMG");
			parm.setData("ADM_TYPE", ODOMainReg.E);
		}
		parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		parm.setData("PAT_NAME", odoMainControl.odo.getPatInfo().getItemString(
				0, "PAT_NAME"));
		parm.setData("MR_NO", odoMainControl.odo.getMrNo());
		parm.setData("IPD_NO", NULLSTR);
		parm.setData("ADM_DATE", odoMainControl.odo.getRegPatAdm().getItemData(
				0, "ADM_DATE"));
		parm.setData("DEPT_CODE", Operator.getDept());
		parm.setData("RULETYPE", "2");
		parm.setData("EMR_DATA_LIST", new TParm());
		odoMainControl.openWindow(URLTEMRWORDUI, parm);
	}

	/**
	 * �ṹ����������Ƭ��
	 */
	public void onInsertPY() throws Exception {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) odoMainControl.openWindow(
				URLEMRCOMPHRASEQUOTE, inParm, true);
		window.setVisible(true);
		word.grabFocus();
	}

	/**
	 * �ṹ���������ü�������
	 */
	public void onInsertResult() throws Exception {
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		inParm.addListener("onReturnContent", this, "onReturnContent");
		TWindow window = (TWindow) odoMainControl.openWindow(URLEMRMEDDATAUI,
				inParm, true);
		window.setX(ImageTool.getScreenWidth() - window.getWidth());
		window.setY(0);
		window.setVisible(true);
	}

	/**
	 * �ṹ����������Ƭ��
	 */
	public void onInsertFamilyPY() throws Exception {
		TParm inParm = new TParm();
		inParm.setData("TYPE", "2");
		inParm.setData("ROLE", "1");
		inParm.setData("DR_CODE", Operator.getID());
		inParm.setData("DEPT_CODE", Operator.getDept());
		inParm.addListener("onReturnContent", this, "onFamilyReturn");
		TWindow window = (TWindow) odoMainControl.openWindow(
				URLEMRCOMPHRASEQUOTE, inParm, true);
		window.setVisible(true);
		this.familyWord.grabFocus();
	}

	/**
	 * Ƭ���¼����ش�ֵ
	 *
	 * @param value
	 *            String
	 */
	public void onReturnContent(String value) throws Exception {
		String str = NULLSTR;
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
		}else if (word.focusInCaptue("ASSESSMENT")) {
			str = "ASSESSMENT";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.word.pasteString(value)) {
			odoMainControl.messageBox("E0035");
		}
	}

	/**
	 * Ƭ���¼����ش�ֵ
	 *
	 * @param value
	 *            String
	 */
	public void onFamilyReturn(String value) throws Exception {
		String str = NULLSTR;
		//String str1 = NULLSTR;
		if (this.familyWord.focusInCaptue("FAMILY_HISTORY")) {
			str = "FAMILY_HISTORY";
		}
		if (this.familyWord.focusInCaptue("PAST_HISTORY")) {//����ʷ add by huangjw 20140818
			str = "PAST_HISTORY";
		}
		if (StringUtil.isNullString(str)) {
			return;
		}
		if (!this.familyWord.pasteString(value)) {
			odoMainControl.messageBox("E0035");
		}
	}

	/**
	 * ����ҽ����
	 */
	public void onOrderSheet() throws Exception {
		TParm parm = new TParm();
		parm.setData("INW", "CASE_NO", odoMainControl.odo.getCaseNo());
		odoMainControl.openDialog(URLINWORDERSHEETPRTANDPREVIEW, parm);
	}

	/**
	 * ��������
	 */
	public void onOpApply() throws Exception {
		TParm parm = new TParm();
		parm.setData("MR_NO", odoMainControl.odo.getPatInfo().getItemString(0,
				"MR_NO"));
		// CASE_NO
		parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		// ADM_TYPE
		parm.setData("ADM_TYPE", odoMainControl.odo.getRegPatAdm()
				.getItemString(0, "ADM_TYPE"));
		// BOOK_DEPT_CODE
		parm.setData("BOOK_DEPT_CODE", Operator.getDept());
		// STATION_CODE
		parm.setData("STATION_CODE", Operator.getStation());
		// BOOK_DR_CODE
		parm.setData("BOOK_DR_CODE", Operator.getID());
		// ICD_CODE
		int i = odoMainControl.odo.getDiagrec().getMainDiag();
		String icdCode = NULLSTR;
		if (i >= 0) {
			icdCode = odoMainControl.odo.getDiagrec().getItemString(i,
					"ICD_CODE");
		}
		parm.setData("ICD_CODE", icdCode);
		odoMainControl.openWindow(URLOPEOPBOOK, parm);
	}

	/**
	 * ������¼
	 */
	public void onOpRecord() throws Exception {
		if (odoMainControl.odo == null) {
			return;
		}
		if (StringUtil.isNullString(odoMainControl.odo.getCaseNo())) {
			return;
		}
		TParm parmR = new TParm();
		parmR.setData("SYSTEM", "OPD"); // ����ϵͳ���
		parmR.setData("ADM_TYPE", odoMainReg.admType);
		parmR.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		parmR.setData("MR_NO", odoMainControl.odo.getMrNo());
		odoMainControl.openDialog(URLOPEOPDETAIL, parmR);
	}

	/**
	 * �ṹ�������Ҽ�����
	 */
	public void onMouseRightPressed() throws Exception {
		if (word.focusInCaptue("EXA_RESULT")) {
			word.popupMenu("Ƭ��,onInsertPY;|;�����,onInsertResult", this);
		}
		//
		EComponent e = this.word.getFocusManager().getFocus();
		if (e == null) {
			return;
		}
		if (!this.word.canEdit()) {
			return;
		}
		// ��
		/*if (e instanceof EFixed) {
			//EFixed fixed = (EFixed) e;
			word.popupMenu("���±��޸�,onMarkTextProperty",this);
		}*/
	}

	/**
	 * ��ģ��
	 */
	public void onSaveTemplate() throws Exception {
		TParm parm = new TParm();
		parm.setData("ODO", odoMainControl.odo);
		Object re = odoMainControl.openDialog(URLOPDCOMPACKENTERNAME, parm,
				false);
		if (TypeTool.getBoolean(re)) {
			odoMainControl.messageBox("P0005");
		}
	}

	/**
	 * ����ʷֵ�ı��¼�
	 *
	 * @param tNode
	 *            TTableNode
	 * @return boolean
	 */
	public boolean onAllergyChangeValue(TTableNode tNode) throws Exception {
		int column = tNode.getColumn();
		int row = tNode.getRow();
		TTable table = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		String columnName = table.getParmMap(column);
		if ("DRUGORINGRD_DESC".equalsIgnoreCase(columnName)) {
			tNode.setValue(NULLSTR);
			return false;
		}
		if (!"ALLERGY_NOTE".equalsIgnoreCase(columnName)) {
			return true;
		}
		if (StringUtil.isNullString(odoMainControl.odo.getDrugAllergy()
				.getItemString(row, "DRUGORINGRD_CODE"))) {
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
	public boolean onDiagTableChangeValue(TTableNode tNode) throws Exception {
		int column = tNode.getColumn();
		String colName = tNode.getTable().getParmMap(column);

		if ("ICD_DESC".equalsIgnoreCase(colName)) {
			tNode.setValue(NULLSTR);
		}
		int row = tNode.getRow();
		if ("MAIN_DIAG_FLG".equalsIgnoreCase(colName)) {
			if (StringUtil.isNullString(odoMainControl.odo.getDiagrec()
					.getItemString(row, "ICD_CODE"))) {
				odoMainControl.odo.getDiagrec().setItem(row, "MAIN_DIAG_FLG",
						"N");
				return true;
			}
			int[] oldMain = new int[1];
			boolean isHavingMain = odoMainControl.odo.getDiagrec()
					.haveMainDiag(oldMain);
			if (!isHavingMain) {
				if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)
						&& odoMainControl.odo.getDiagrec().isSyndromFlg(
								odoMainControl.odo.getDiagrec().getItemString(
										row, "ICD_CODE"))) {
					odoMainControl.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ��UP

					return true;
				} else if (ODOMainOpdOrder.W
						.equalsIgnoreCase(odoMainOpdOrder.wc)) {
					String icdCode = odoMainControl.odo.getDiagrec()
							.getItemString(row, "ICD_CODE");
					if (!odoMainControl.odo.getDiagrec().isMainFlg(icdCode)) {
						odoMainControl.messageBox("E0132"); // ����ϲ�����Ϊ�����
						return true;
					}
				}

			} else {
				if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)
						&& odoMainControl.odo.getDiagrec().isSyndromFlg(
								odoMainControl.odo.getDiagrec().getItemString(
										row, "ICD_CODE"))) {
					odoMainControl.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ��UP
					return true;
				} else if (ODOMainOpdOrder.W
						.equalsIgnoreCase(odoMainOpdOrder.wc)) {
					String icdCode = odoMainControl.odo.getDiagrec()
							.getItemString(row, "ICD_CODE");
					if (!odoMainControl.odo.getDiagrec().isMainFlg(icdCode)) {
						return true;
					}
					odoMainControl.odo.getDiagrec().setItem(oldMain[0],
							"MAIN_DIAG_FLG", "N");
					tNode.getTable().setDSValue(oldMain[0]);
				}
			}
		}
		return false;
	}

	/**
	 * �������͸ı�
	 *
	 * @param type
	 *            String
	 */
	public void onAllg(String type) throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		table.acceptText();
		//table.setVisible(true);
		if (TCM_Transform.getBoolean(odoMainControl.getValue("OTHER_ALLERGY"))) {
			allergyType = "C";
			table.setFilter(" DRUG_TYPE='C' ");
			table.filter();
			table.setDSValue();
		} else if (TCM_Transform.getBoolean(odoMainControl
				.getValue("INGDT_ALLERGY"))) {
			allergyType = "A";
			table.setFilter(" DRUG_TYPE='A' ");
			table.filter();
			table.setDSValue();
		} else if(TCM_Transform.getBoolean(odoMainControl
				.getValue("ORDER_ALLERGY"))){
			allergyType = "B";
			table.setFilter(" DRUG_TYPE='B' ");
			table.filter();
			table.setDSValue();
		} else {
			//table.setVisible(false);
			getAllegyByCaseNo();
		}
		
	}
	
	/**
	 * ��ȡ��ʿ¼��Ĺ���ʷ add by huangjw 20150401
	 * @throws Exception 
	 */
	public void getAllegyByCaseNo() throws Exception{
		String subclassCode = TConfig.getSystemValue("ONWEmrMRCODE");
		String sql = " SELECT FILE_PATH,FILE_NAME FROM EMR_FILE_INDEX WHERE SUBCLASS_CODE='"+subclassCode+"' AND CASE_NO='"+odoMainControl.odo.getCaseNo()+"' ORDER BY OPT_DATE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<=0){
			return;
		}
		TParm allegyParm = new TParm();
		String allegyHistory="";
		String filePath="";
		String fileName="";
		for(int i=0;i<parm.getCount();i++){
			 filePath=parm.getValue("FILE_PATH",i);
			 fileName=parm.getValue("FILE_NAME",i);
			allegyWord.onOpen(filePath, fileName, 3, false);
			if(allegyWord.getCaptureValue("AllegyHistory").trim().length()>0){
				allegyHistory=allegyWord.getCaptureValue("AllegyHistory");
			}
			
			
		}
		//add by huangtt 20160812 start   �Զ������ʷ��ӵ����ݿ���
		allegyParm.setData("allegyHistory", allegyHistory);
		allegyParm.setData("filePath", filePath);
		allegyParm.setData("fileName", fileName);
		allegyParm.setData("mrNo", odoMainControl.getValue("MR_NO"));
		allegyParm.setData("caseNo", odoMainControl.caseNo);
		allegyParm.setData("admType", odoMainReg.admType);
		//add by huangtt 20160812 end
		
		odoMainControl.openDialog("%ROOT%\\config\\emr\\EMRAllegyHistory.x",allegyParm);
		this.getRadioButton("ORDER_ALLERGY").setSelected(true);
		onAllg("B");
	}
	/**
	 * add by huangjw 20150402
	 * @param tag
	 * @return
	 */
	public TRadioButton getRadioButton(String tag){
		return (TRadioButton)odoMainControl.getComponent(tag);
	} 
	/**
	 * hl7������Ӳ�������
	 *
	 * @param checkParm
	 *            TParm
	 */
	public void hl7Temp(TParm checkParm) throws Exception {
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
				hl7ParmEnd
						.addData("BILL_FLG", checkParm.getData("BILL_FLG", i));
			}
		}
		// �õ��շ���Ŀ
		sendHL7Parm = hl7ParmEnd;
	}

	/**
	 * ����HL7
	 */
	public void sendHL7Mes() throws Exception {
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
		if (null == sendHL7Parm || sendHL7Parm.getCount("ADM_TYPE") <= 0) {
			return;
		} else {
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		}
		List list = new ArrayList();
		String patName = odoMainControl.getValue("PAT_NAME").toString();
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
			} else {
				parm.setData("FLG", 0);
			}
			parm.setData("CASE_NO", odoMainControl.opb.getReg().caseNo());
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
			odoMainControl.messageBox(resultParm.getErrText());
	}

	/**
	 * ����HL7�ӿڵĲ��� ���������� �� ȡ���� �������б�
	 *
	 * @param HL7
	 *            List
	 * @param flg
	 *            String 0:���� 1:ȡ��
	 * @return List
	 */
	public List getHL7List(List HL7, String flg) throws Exception {
		odoMainControl.messageBox("getHL7List�޷���");
		return HL7;
	}

	/**
	 * �������߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	 *
	 * @param focusTag
	 *            String
	 */
	public void setFocusTag(String focusTag) throws Exception {
		this.focusTag = focusTag;
	}

	/**
	 * Ϊ�˱���ģ�幫���ķ���
	 *
	 * @return odo ODO
	 */
	public ODO getOdo() throws Exception {
		return odoMainControl.odo;
	}

	/**
	 * �õ����߿����ֲ�ʷ���ʱ�Ļ�ÿؼ���TAB
	 *
	 * @return focusTag String
	 */
	public String getFocusTag() throws Exception {
		return this.focusTag;
	}

	/**
	 * �������
	 *
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popDiagReturn(String tag, Object obj) throws Exception {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) odoMainControl.getComponent(TABLEDIAGNOSIS);
		tableDiag.acceptText();
		Diagrec diagRec = odoMainControl.odo.getDiagrec();
		int rowNo = tableDiag.getSelectedRow();
		String icdTemp = parm.getValue("ICD_CODE");
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!odoMainReg.canEdit()) {
			tableDiag.setDSValue(rowNo);
			odoMainControl.messageBox_("�ѳ�������ʱ�䲻���޸�");
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
			odoMainControl.messageBox("E0041"); // ������ѿ���
			return;
		}
		if (!OdoUtil.isAllowDiag(parm, Operator.getDept(), odoMainPat.pat
				.getSexCode(), odoMainPat.pat.getBirthday(),
				(Timestamp) odoMainControl.getValue("ADM_DATE"))) {
			odoMainControl.messageBox("E0042"); // ��ϲ������ڸò��ˣ������¿���
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
			if (ODOMainOpdOrder.C.equalsIgnoreCase(odoMainOpdOrder.wc)
					&& !OdoUtil.isAllowChnDiag(parm)) {
				odoMainControl.messageBox("E0018"); // �����Ϊ֢�򣬲�����Ϊ���
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
			// mainFlg = true;
		} else {
			diagRec.setItem(rowNo, "MAIN_DIAG_FLG", "N");
			// mainFlg=false;
		}
		diagRec.setActive(rowNo, true);
		diagRec.setItem(rowNo, "ICD_TYPE", odoMainOpdOrder.wc);
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
		tableDiag.setItem(rowNo, "FILE_NO", rowNo+1);// add caoy 2014/5/29�������
		tableDiag.setDSValue();
		if (rowNo == tableDiag.getRowCount() - 1) {
			rowNo = tableDiag.addRow();
		}
		tableDiag.getTable().grabFocus();
		tableDiag.setSelectedRow(rowNo);
		tableDiag.setSelectedColumn(1);
	}

	/**
	 * ��������ʷ
	 *
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popMedHistoryReturn(String tag, Object obj) throws Exception {
		TParm parm = (TParm) obj;
		TTable tableMedHistory = (TTable) odoMainControl
				.getComponent(TABLEMEDHISTORY);
		tableMedHistory.acceptText();
		int rowNo = tableMedHistory.getSelectedRow();
		// �ж��Ƿ��Ѿ���������ʱ��
		if (!odoMainReg.canEdit()) {
			tableMedHistory.setDSValue(rowNo);
			odoMainControl.messageBox_("�ѳ�������ʱ�䲻���޸�");
			return;
		}
		if (odoMainControl.odo.getMedHistory().isSameICD(
				parm.getValue("ICD_CODE"))) {
			odoMainControl.messageBox("E0043"); // ���������ظ����
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		String oldCode = odoMainControl.odo.getMedHistory().getItemString(
				rowNo, "ICD_CODE");
		if (!StringUtil.isNullString(oldCode)) {
			odoMainControl.messageBox("E0040"); // ������������ݣ�������������ɾ��������`
			tableMedHistory.setDSValue(rowNo);
			return;
		}
		odoMainControl.odo.getMedHistory().setActive(rowNo, true);

		odoMainControl.odo.getMedHistory().setItem(rowNo, "ICD_CODE",
				parm.getValue("ICD_CODE"));
		odoMainControl.odo.getMedHistory().setItem(rowNo, "ICD_TYPE",
				parm.getValue("ICD_TYPE"));
		odoMainControl.odo.getMedHistory().setItem(
				rowNo,
				"SEQ_NO",
				odoMainControl.odo.getMedHistory().getMaxSEQ(
						odoMainControl.odo.getMrNo()));
		if (rowNo == tableMedHistory.getRowCount() - 1)
			odoMainControl.odo.getMedHistory().insertRow();
		tableMedHistory.setDSValue();
		tableMedHistory.getTable().grabFocus();
		tableMedHistory.setSelectedRow(odoMainControl.odo.getMedHistory()
				.rowCount() - 1);
		tableMedHistory.setSelectedColumn(2);
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
	public void onAllergyCreateEditComponent(Component com, int row, int column)
			throws Exception {
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		if (TCM_Transform.getBoolean(odoMainControl.getValue(TAGORDER_ALLERGY))) {
			TParm parm = new TParm();
			parm.setData("ALLERGY_TYPE", "allergyType");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", odoMainControl
					.getConfigParm().newConfig(URLSYSFEEPOPUP), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");
		} else if (TCM_Transform.getBoolean(odoMainControl
				.getValue("INGDT_ALLERGY"))) {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "A");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("INGDT", odoMainControl
					.getConfigParm().newConfig(URLSYSALLERGY), parm);

			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");
		} else {
			TParm parm = new TParm();
			parm.addData("ALLERGY_TYPE", "C");
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("OTHER", odoMainControl
					.getConfigParm().newConfig(URLSYSALLERGY), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popAllergyReturn");
		}
	}

	/**
	 * �������߿���
	 */
	public void saveSubjrec() throws Exception {
		odoMainControl.odo.getSubjrec().setActive(0, true);;
		odoMainControl.odo.getSubjrec().setItem(0, "SUBJ_TEXT",
				word.getCaptureValueNoDel("SUB").replaceAll("'", "''"));
		odoMainControl.odo.getSubjrec().setItem(0, "OBJ_TEXT",
				word.getCaptureValueNoDel("OBJ").replaceAll("'", "''"));
		odoMainControl.odo.getSubjrec().setItem(0, "PHYSEXAM_REC",
				word.getCaptureValueNoDel("PHY").replaceAll("'", "''"));
		odoMainControl.odo.getSubjrec().setItem(0, "PROPOSAL",
				word.getCaptureValueNoDel("PROPOSAL").replaceAll("'", "''"));
		odoMainControl.odo.getSubjrec().setItem(0, "EXA_RESULT",
				word.getCaptureValueNoDel("EXA_RESULT").replaceAll("'", "''"));
		//add by huangtt ��������2017
		odoMainControl.odo.getSubjrec().setItem(0, "ASSESSMENT",
				word.getCaptureValueNoDel("ASSESSMENT").replaceAll("'", "''"));
		// add by wangb ������ҩ��� 2017/10/13
		odoMainControl.odo.getSubjrec().setItem(0, "MEDICATION",
				word.getCaptureValueNoDel("MEDICATION").replaceAll("'", "''"));
		if ("N".equalsIgnoreCase(odoMainControl.odo.getRegPatAdm()
				.getItemString(0, "SEE_DR_FLG"))) {
			//�����Ѵ��������ֲ�ʷ����
			final String emrFiles[]= EmrUtil.getInstance().getGSFile(
					odoMainControl.odo.getCaseNo());
			if (!emrFiles[1].equals("")){
				if (this.saveFiles != null) {
					word.setMessageBoxSwitch(false);
					word.onSaveAs(saveFiles[0], saveFiles[1], 3);
				}
				word.setCanEdit(true);
			//δ�������û�����߲�ʷ����
			}else{				
				TParm parm = EmrUtil.getInstance().saveGSFile(
						odoMainControl.odo.getMrNo(),
						odoMainControl.odo.getCaseNo(), saveFiles[2], saveFiles[1]);
				if (parm.getErrCode() < 0) {
					odoMainControl.messageBox("E0066");
					return;
				}
				String path = parm.getValue("PATH");
				String fileName = parm.getValue("FILENAME");
				word.setMessageBoxSwitch(false);
				word.onSaveAs(path, fileName, 3);
				saveFiles = EmrUtil.getInstance().getGSFile(
						odoMainControl.odo.getCaseNo());
			}
		} else {
			if (this.saveFiles != null) {
				word.setMessageBoxSwitch(false);
				word.onSaveAs(saveFiles[0], saveFiles[1], 3);
			}
			word.setCanEdit(true);
		}
//		odoMainControl.messageBox("P0001"); 
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
			String subClassCode) throws Exception {
		EMRTool emrTool = new EMRTool(odoMainControl.odo.getCaseNo(),
				odoMainControl.odo.getMrNo(), odoMainControl);
		emrTool.saveEMR(obj, fileName, classCode, subClassCode);
	}
	
	/**
	 * 
	 * @param obj
	 * @param fileName
	 * @param classCode
	 * @param subClassCode
	 * @throws Exception
	 */
	private void saveOnlyOneEMR(Object obj, String fileName, String classCode,
			String subClassCode) throws Exception {
		EMRTool emrTool = new EMRTool(odoMainControl.odo.getCaseNo(),
				odoMainControl.odo.getMrNo(), odoMainControl);
		emrTool.saveOnlyOneEMR(obj, fileName, classCode, subClassCode);
	}

	/**
	 * ��Ⱦ�����濨
	 */
	private void getDiagrec() throws Exception {
		int count = odoMainControl.odo.getDiagrec().rowCount();
		for (int i = 0; i < count; i++) {
			if (odoMainControl.odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odoMainControl.odo.getMrNo());
				can.setData("CASE_NO", odoMainControl.odo.getCaseNo());
				can.setData("ICD_CODE", odoMainControl.odo.getDiagrec()
						.getItemString(i, "ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				can.setData("ADM_TYPE", odoMainControl.odo.getAdmType());// add
																			// by
																			// wanglong
																			// 20140307
				odoMainControl.openDialog(URLMROINFECT, can);
			}
		}
	}

	/**
	 * �����������
	 */
	public void onConsApply()throws Exception {
		String flg = "ODO";
		TParm parm = new TParm();
		if(odoMainControl.odo.getAdmType().equals("O")){
			odoMainControl.messageBox("�����޻���Ȩ��");
			return;
		}
		parm.setData("INW", "CASE_NO", odoMainControl.odo.getCaseNo());
		parm.setData("INW", "PAT_NAME",  odoMainPat.pat.getName1());
		parm.setData("INW", "ADM_DATE", odoMainControl.odo.getRegPatAdm());
		parm.setData("INW", "MR_NO", odoMainControl.odo.getMrNo());
		parm.setData("INW", "ADM_TYPE", odoMainControl.odo.getAdmType());
		parm.setData("INW", "ODI_FLG", flg);
		parm.setData("INW", "KIND_CODE","03");
		odoMainControl.openDialog("%ROOT%\\config\\inp\\INPConsApplication.x",parm);
	}

	/**
	 *
	 * @param type
	 *            1:�������뵥 2: 4:��Ⱦ�����濨
	 */
	public void getRun(final int type) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					switch (type) {
					case 1:
						onEmrM();
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
	 * ֻ�� �м��顢������뵥ʱ��̯��������� ===========caoy ��Ӽ������뵥����
	 */
	public void onEmrM() throws Exception {

		if (odoMainControl.odo == null)
			return;
		//==liling 20140718 add ���뵥Ԥ������ start===
		String preSwitch=IReportTool.getInstance().getPreviewSwitch("EMRSingle_V45.previewSwitch");
		if(!preSwitch.equals(IReportTool.ON)){
			return;
		}
		// �õ����뵥�����
		TParm emrParm = new TParm();
		TParm actionParm = new TParm();
		if (odoMainReg.admType.equals("O")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAllM(
					odoMainControl.odo.getOpdOrder(), "ODO");
			actionParm.setData("SYSTEM_CODE", "ODO");
		} else if (odoMainReg.admType.equals("E")) {
			emrParm = OrderUtil.getInstance().getOrderPasEMRAllM(
					odoMainControl.odo.getOpdOrder(), "EMG");
			actionParm.setData("SYSTEM_CODE", "EMG");
		}
		TParm result = ODOTool.getInstance().getPrintExaDataSum(
				odoMainControl.odo.getCaseNo(), true);
		if (result.getInt("ACTION", "COUNT") > 0||emrParm.getInt("ACTION", "COUNT")>0) {

			actionParm.setData("ADM_TYPE", odoMainReg.admType);
			actionParm.setData("MR_NO", odoMainPat.pat.getMrNo());
			if ("en".equals(odoMainControl.getLanguage())) // �����Ӣ�� ��ôȡӢ������
				actionParm.setData("PAT_NAME", odoMainPat.pat.getName1());
			else
				actionParm.setData("PAT_NAME", odoMainPat.pat.getName());
			actionParm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
			actionParm.setData("IPD_NO", "");
			actionParm.setData("ADM_DATE", odoMainControl.odo.getRegPatAdm()
					.getItemData(0, "ADM_DATE"));
			actionParm.setData("DEPT_CODE", Operator.getDept());
			actionParm.setData("STYLETYPE", "1");
			actionParm.setData("RULETYPE", "2");
			if (result.getCount() > 0) {
				actionParm.setData("CParm", result);
			}
			if (emrParm.getCount() > 0) {
				actionParm.setData("EMR_DATA_LIST", emrParm);
			}
			odoMainControl.openWindow(URLEMRSINGLEUI, actionParm);
		}
		// System.out.println("for Emr parm"+actionParm);
		// this.openDialog("%ROOT%\\config\\emr\\EMRSingleUI.x",actionParm);

	}

	/**
	 * ������� ==========pangben modify 20110706
	 */
	public void onShow() throws Exception {
		TTable table = ((TTable) odoMainControl
				.getComponent(ODOMainReg.TABLEPAT));
		if (table.getSelectedRow() < 0) {
			odoMainControl.messageBox("��ѡ��һ������");
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
	 * ����󼴴�ӡ�����Ͳ�����¼
	 *
	 * @param parm
	 *            TParm
	 * @param rxType
	 *            String
	 */
	public void onPrintOrder(TParm parm, String rxType) throws Exception {
		if (parm == null || parm.getCount("RX_NO") < 1) {
			return;
		}
		int count = parm.getCount("RX_NO");
		Object obj1=null;
		String side="";
		if (ODORxChnMed.CHN.equalsIgnoreCase(rxType)) {
			for (int i = 0; i < count; i++) {
				// ��ӡ����ǩ
				TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
						odoMainReg.realDeptCode, rxType, odoMainControl.odo,
						parm.getValue("RX_NO", i), NULLSTR);
				//==liling 20140718 add ��ҩ������ӡ���� start===
				String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdChnOrderSheet.prtSwitch");
				if(prtSwitch.equals(IReportTool.ON)){
				//==liling 20140718 add ��ҩ������ӡ���� end===
				Object obj = odoMainControl.openPrintDialog(
						URLOPDCHNORDERSHEET, inParam, true);
				// ����EMR
				String rxNo = parm.getValue("RX_NO", i);
				// �ļ������봦��ǩ��
				this.saveEMR(obj, "��ҩ����ǩ_" + rxNo, "EMR030002", "EMR03000201");
				}
			}
			return;
		}
		OpdOrder order = odoMainControl.odo.getOpdOrder();
		String filterString = order.getFilter();

		List<ArrayList<Object>> saveInfo = new ArrayList<ArrayList<Object>>();

		// add caoyong 20140321 ����ҩƷ start
		String psdesc = "Ƥ�Խ��(       )����_____________ ";// Ƥ��
		String PS = "Ƥ��";// Ƥ��
		String mrno = odoMainControl.odo.getMrNo();
		TParm Aresult = ODOTool.getInstance().getAllergyData(mrno);// ����ҩƷ
		StringBuffer buf = new StringBuffer();
		boolean flag = false;// �Ƿ��й���ҩ
		boolean pflag = false;// �ж��Ƿ��Ѿ�����Ƥ��
		boolean dosflg=false;//Ƥ���÷���������ʾ
		boolean newpsflg=true;
		DecimalFormat df2 = new DecimalFormat("############0.00");
		if (Aresult.getCount() > 0) {
			for (int j = 0; j < Aresult.getCount(); j++) {
				buf.append(",").append(Aresult.getValue("ORDER_DESC", j))
						.append(" ")
						.append(Aresult.getValue("ALLERGY_NOTE", j));
			}
			flag = true;
		}
		String allerg = buf.toString();
		// add caoyong 20140321 ����ҩƷ end

		for (int i = 0; i < count; i++) {
			ArrayList<Object> data = new ArrayList<Object>();
			String rxNo = parm.getValue("RX_NO", i);
			String seqNo = parm.getValue("SEQ_NO", i);
			order.setFilter("RX_NO='" + rxNo + "'");
			order.filter();
			TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
					odoMainReg.realDeptCode, rxType, odoMainControl.odo, rxNo,
					order.getItemString(0, "PSY_FLG"));
			if (ODORxCtrl.CTRL.equalsIgnoreCase(rxType)) {
				if (flag) {
					inParam.setData("ALLERGY", "TEXT", "����ʷ:"
							+ allerg.substring(1, allerg.length()));
				}
				//modify by huangjw 20140729 ����ҩƷ����׷�  start
				for(int j=0;j<2;j++){
					String side_x="";
					if(j==0)
						side_x="������";
					else
						side_x="�����㡾�׷���";
				//modify by huangjw 20140729 ����ҩƷ����׷�  end
//					this.getDrugParm(df2, rxNo, inParam,side_x);//����ҩƷ caoy
					 
					//==liling 20140718 add ����ҩƷ������ӡ���� start===
					String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdDrugSheet.prtSwitch");
					if(prtSwitch.equals(IReportTool.ON)){
					//==liling 20140718 add ����ҩƷ������ӡ���� end===
					 Object obj = odoMainControl.openPrintDialog(URLOPDDRUGSHEET,
							inParam, true);
					// ����EMR
					// �ļ������봦��ǩ��
					String fileName = "����ҩƷ����ǩ_" + rxNo;
					// this.saveEMR(obj, fileName, "EMR030001", "EMR03000102");
	
					data.add(obj);
					data.add(fileName);
					data.add("EMR030001");
					data.add("EMR03000102");
					saveInfo.add(data);
					}
				}
			} else {
				String rxNo2 = rxNo;
				String caseNo2 = odoMainControl.caseNo;
				for(int g=0;g<2;g++){//��ӡ����
					if(g==0){
						side="�����㡾�׷���";
					}else{
						side="������";
					}
					
				// =============modify by lim begin
			

				TParm westResult = ODOTool.getInstance().getPrintOrderData(
						caseNo2, rxNo2);

				if (westResult.getErrCode() < 0) {
					odoMainControl.messageBox("E0001");
					return;
				}
				if (westResult.getCount() < 0) {
					order.setFilter(filterString);
					order.filter();
					odoMainControl.messageBox("û�д���ǩ����.");
					return;
				}

				TParm westParm = new TParm();
				double pageAmt2 = 0;
				
				 //add caoyong 20140322 Ƥ�Խ��,����--- start
				StringBuffer bufB = new StringBuffer();
				StringBuffer bufM = new StringBuffer();
				String psQty="";
				for (int j = 0; j < westResult.getCount(); j++) {
					if (PS.equals(westResult.getData("DD", j))) {// �Ƿ���Ƥ����ҩ
						psdesc = "";
						dosflg=true;
						newpsflg=false;
					}else{
						dosflg=false;
					}
					//if (PS.equals(westResult.getData("DD", j))
							//&& !"".equals(westResult.getData("BATCH_NO", j))) {// ��Ƥ��ҩ������Ƥ������
					if (!"".equals(westResult.getData("BATCH_NO", j))){
						bufB.append(",").append(
								westResult.getData("BATCH_NO", j));
						bufM.append(",").append(
								westResult.getData("SKINTEST_FLG", j));
						pflag = true;
					}
            			westParm.addData("AA", "");
    					westParm.addData("BB", westResult.getData("BB", j)+"  "+westResult.getData("ER", j));
    					westParm.addData("CC", NULLSTR);
    					
						westParm.addData("AA", NULLSTR);
						if(dosflg){
							westParm.addData("BB","                    "+westResult.getData("FF", j)+"  "+westResult.getData("DD", j));
						}else{
							//westParm.addData("BB", "    "+"�÷���ÿ��"+westResult.getData("HH", j)+"  "+westResult.getData("FF", j)+"  "+westResult.getData("DD", j)+"  "+westResult.getData("DR", j));
							//ȥ��ҽʦ��ע modify by huangjw 20141222
							westParm.addData("BB", "    "+"�÷���ÿ��"+westResult.getData("HH", j)+"  "+westResult.getData("FF", j)+"  "+westResult.getData("DD", j));
						}
						westParm.addData("CC", NULLSTR);
					// add caoyong 20140322 Ƥ�Խ��,����--- start
				
					pageAmt2 += (westResult.getDouble("DOSAGE_QTY", j)
							* westResult.getDouble("OWN_PRICE", j) * westResult
							.getDouble("DISCOUNT_RATE", j));// modify by
															// wanglong 20121226
					pageAmt2 = StringTool.round(pageAmt2, 2);// add by wanglong
																// 20121226
					//if ((j != 0 && (j + 1) % 6 == 0)
							//|| j == westResult.getCount() - 1) {
					//	pageAmt2 = 0;
					//}
				}
				westParm.setCount(westParm.getCount("AA"));
				westParm.addData("SYSTEM", "COLUMNS","");
				westParm.addData("SYSTEM", "COLUMNS", "BB");
				westParm.addData("SYSTEM", "COLUMNS", "CC");
				westParm.addData("SYSTEM", "COLUMNS", "DR");
				inParam.setData("ORDER_TABLE", westParm.getData());
				// add caoyong ��Ӵ���ǩ���Ƥ�Ժ͹������� 2014��3��21 start
				
					
				inParam.setData("SKINTEST", "TEXT", psdesc);
				inParam.setData("TOT_AMT", "TEXT", df2.format(pageAmt2));
				inParam.setData("SIDE", "TEXT", side);
				if (newpsflg&&pflag) {
					//inParam.setData("SKINTEST", "TEXT", psdesc);
					inParam.setData("SKINTESTM", "TEXT", bufB.toString()
									.substring(1, bufB.toString().length()));
					inParam.setData("SKINTESTB", "TEXT", bufM.toString()
								.substring(1, bufM.toString().length()));
				}
				if (flag) {
					inParam.setData("ALLERGY", "TEXT", "����ʷ:"
							+ allerg.substring(1, allerg.length()));
				}
				// add caoyong ��Ӵ���ǩ���Ƥ�Ժ͹������� 2014��3��21 end
				// =============modify by lim end
				//==liling 20140718 add ��ҩ������ӡ���� start===
				String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdOrderSheet.prtSwitch");
				if(prtSwitch.equals(IReportTool.ON)){
				//==liling 20140718 add ��ҩ������ӡ���� end===
				 obj1 = odoMainControl.openPrintDialog(URLOPDORDERSHEET,
						inParam, true);
				// // ����EMR
					// // �ļ������봦��ǩ��
					String fileName = "��ҩ����ǩ_" + rxNo;
					// this.saveEMR(obj, fileName, "EMR030001", "EMR03000101");
					data.add(obj1);
					data.add(fileName);
					data.add("EMR030001");
					data.add("EMR03000101");
					saveInfo.add(data);
				}
				 
				}
				TParm bottParm=ODOTool.getInstance().getNewBottleLabel(odoMainControl.caseNo, rxNo,"");
				TParm newbottParm_I=new TParm();//ע��
				TParm newbottParm_E=new TParm();//����
				TParm newbottParm_O=new TParm();//�ڷ�
				TParm newbottParm=new TParm();//���
				String newName="";
				if(bottParm.getCount()>0){
					for(int k=0;k<bottParm.getCount();k++){
						if("I".equals(bottParm.getValue("CLASSIFY_TYPE", k))){//ע��
							newbottParm_I.addRowData(bottParm, k);
						}else if("F".equals(bottParm.getValue("CLASSIFY_TYPE", k))){//���
							newbottParm.addRowData(bottParm, k);
						}else if("E".equals(bottParm.getValue("CLASSIFY_TYPE", k))){//����
							newbottParm_E.addRowData(bottParm, k);
						}else if("O".equals(bottParm.getValue("CLASSIFY_TYPE", k))){//�ڷ�
							newbottParm_O.addRowData(bottParm, k);
						}
						
					}
					if(newbottParm_E.getCount()>0){
						newName="�ţ�������ҩ������ִ�е�";
						this.getPrintBottleLabel_I(rxNo, inParam,newbottParm_E,newName);//����ִ�е�
					}
					if(newbottParm_I.getCount()>0){
						newName="�ţ�������ҩ������ִ�е�";
						this.getPrintBottleLabel_I(rxNo, inParam,newbottParm_I,newName);//ע��ִ�е�
					}
					if(newbottParm.getCount()>0){
						newName="�ţ�������ҩ������ִ�е�";
						this.getPrintBottleLabel_I(rxNo, inParam,newbottParm,newName);//��Һִ�е�
					}
					if(newbottParm_O.getCount()>0){
						newName="�ţ�������ҩ������ִ�е�";
						this.getPrintBottleLabel_I(rxNo, inParam,newbottParm_O,newName);//�ڷ�ִ�е�
					}
				}
				//��Һ�� add caoy
				
				
//				// // ����EMR
//				// // �ļ������봦��ǩ��
//				String fileName = "��ҩ����ǩ_" + rxNo;
//				// this.saveEMR(obj, fileName, "EMR030001", "EMR03000101");
//				data.add(obj1);
//				data.add(fileName);
//				data.add("EMR030001");
//				data.add("EMR03000101");
//				saveInfo.add(data);
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
	public void onPrintExa(TParm parm) throws Exception {
		TParm inParam = new TParm();
		
//		System.out.println("======�ݴ��ӡ���á�������֪ͨ��parm parm  is ::"+parm);
		inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
				odoMainReg.realDeptCode, ODORxExa.EXA, odoMainControl.odo);
		// modify by lim 2012/02/23 begin
		String rxNo = inParam.getValue("RX_NO");
		String caseNo = odoMainControl.caseNo;
		
		//=====����rx_no��mr_no��ѯcase_no   yanjing 20140610  start
		String caseNoSql = "SELECT CASE_NO FROM OPD_ORDER WHERE MR_NO = '"+odoMainControl.odo.getMrNo()+"'  " +
				"AND RX_NO = "+rxNo+" ";
		TParm caseNoParm = new TParm (TJDODBTool.getInstance().select(caseNoSql));
		if(caseNoParm.getCount()>0){
			caseNo = caseNoParm.getValue("CASE_NO", 0);
		}
		//=====����rx_no��mr_no��ѯcase_no   yanjing 20140610 end
		
		// modify by wanglong 20121226
		TParm result = ODOTool.getInstance()
				.getPrintExaData(caseNo, rxNo, true);
		// modify by wanglong 20121226
		// TParm result2 = ODOTool.getInstance().getPrintExaData(caseNo, rxNo,
		// false);
		int blankPage=0;// add caoy 2014/5/21

		if (result.getErrCode() < 0) {
			return;
		}
		if (result.getCount() < 0) {
			return;
		}
		TParm data2 = new TParm();
		boolean flg = false;
		int blankRow = 0;
		double pageAmt = 0;
		//List listArray = new ArrayList();// ===caoy 2014-3-21 ���뵥���
		DecimalFormat df = new DecimalFormat("############0.00");
		if (result.getCount() > 0) {
			int pageCount = 11;
			data2.addData("BILL_FLG", NULLSTR);
			data2.addData("DEPT_CHN_DESC", "����֪ͨ��");
			data2.addData("ORDER_DESC", NULLSTR);
			data2.addData("MEDI_QTY", NULLSTR);
			data2.addData("URGENT_FLG", NULLSTR);
			data2.addData("DESCRIPTION", NULLSTR);
			data2.addData("MED_APPLY_NO", NULLSTR);
			// ======pangben 2014-3-20
			// data2.addData("TIME_LIMIT", NULLSTR) ;//===ʱ��====liling 20140331
			// ȥ��ʱ��
			data2.addData("CHN_DESC", NULLSTR);// ִ�еص�
			for (int i = 0; i < result.getCount(); i++) {
				data2.addData("BILL_FLG", result.getData("BILL_FLG", i));
				data2.addData("DEPT_CHN_DESC", result.getData("DEPT_CHN_DESC",
						i));
				data2.addData("ORDER_DESC", result.getData("ORDER_DESC", i));
				double own_price = StringTool.round(result.getDouble(
						"OWN_PRICE", i), 3);
				DecimalFormat df3 = new DecimalFormat("############0.000");
				// add by huangtt 20121127
				String mediQty = result.getValue("MEDI_QTY", i).substring(0,
						result.getValue("MEDI_QTY", i).length() - 2);
				String ownPrice = df3.format(own_price);
				data2.addData("MEDI_QTY", StringTool.fillLeft(mediQty,
						4 - mediQty.length(), " ")
						+ StringTool.fill(" ", 14 - ownPrice.length())
						+ ownPrice);// caowl 20131104
				data2.addData("URGENT_FLG", result.getData("URGENT_FLG", i));
				data2.addData("DESCRIPTION", result.getData("DESCRIPTION", i));
				data2
						.addData("MED_APPLY_NO", result.getData("MED_APPLY_NO",
								i));
				// ======pangben 2014-3-20
				// data2.addData("TIME_LIMIT", result.getData("TIME_LIMIT", i))
				// ;//===ʱ��====liling 20140331 ȥ��ʱ��
				data2.addData("CHN_DESC", result.getData("CHN_DESC", i));// ִ�еص�

				//$------ҽ�����Ƴ������11�ʹ�ӡ�հ�ҳ -------start
				if(result.getData("ORDER_DESC", i).toString().length()>=11){
					blankPage++;
				}
				//$------ҽ�����Ƴ������11�ʹ�ӡ�հ�ҳ -------end


				// / add caoyong 20140321-------------start
				/*if ("LIS".equals(result.getValue("ORDER_CAT1_CODE", i))) {
					if (!listArray.contains(result.getData("MED_APPLY_NO", i))) {

						listArray.add(result.getData("MED_APPLY_NO", i));
					}
				}*/
				// / add caoyong 20140321-------------end
				// �ۼ�

				// pageAmt += (result.getDouble("MEDI_QTY", i)
				// * this.getEveryAmt(result.getValue("ORDER_CODE", i))
				// * result.getDouble("DISCOUNT_RATE", i));// modify by wanglong
				// 20121226
				pageAmt += StringTool.round(result.getDouble("AR_AMT", i), 2);// pangben
																				// 2013-4-17
				// TODO:��###########������Ҫ��ÿҳ����Ľ�����.�û�õ�����*getEveryAmt(ORDERCODE)�����ÿ����¼�Ľ�
				int num = i + blankRow + 1 + 1;// ������i��+
				// �հ���(blankRow)+��һ�м���֪ͨ��(1)+1
				if (!flg) {// ��һҳ
					//if (i == 8 || i == (result.getCount() - 1)) {
					if (i+blankPage == 10 || i == (result.getCount() - 1)) {
						data2.addData("BILL_FLG", NULLSTR);
						data2.addData("DEPT_CHN_DESC", NULLSTR);
						data2.addData("ORDER_DESC", NULLSTR);
						data2.addData("MEDI_QTY", NULLSTR);
						data2.addData("URGENT_FLG", NULLSTR);
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						// ======pangben 2014-3-20
						// data2.addData("TIME_LIMIT", NULLSTR)
						// ;//===ʱ��====liling 20140331 ȥ��ʱ��
						data2.addData("CHN_DESC", NULLSTR);// ִ�еص�
						flg = true;
						blankRow++;
						pageAmt = 0;
					}
				} else {// ����ҳ.//��11����ʾ���
					if (i == result.getCount() - 1) {
						data2.addData("BILL_FLG", NULLSTR);
						data2.addData("DEPT_CHN_DESC", NULLSTR);
						data2.addData("ORDER_DESC", NULLSTR);
						data2.addData("MEDI_QTY", NULLSTR);
						data2.addData("URGENT_FLG", NULLSTR);
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						// ======pangben 2014-3-20
						// data2.addData("TIME_LIMIT", NULLSTR)
						// ;//===ʱ��====liling 20140331 ȥ��ʱ��
						data2.addData("CHN_DESC", NULLSTR);// ִ�еص�
						blankRow++;
						pageAmt = 0;
					} else if (i != result.getCount() - 1
							&& ((num % 11) + 1) == 11) {
						data2.addData("BILL_FLG", NULLSTR);
						data2.addData("DEPT_CHN_DESC", NULLSTR);
						data2.addData("ORDER_DESC", NULLSTR);
						data2.addData("MEDI_QTY", NULLSTR);
						data2.addData("URGENT_FLG", NULLSTR);
						data2.addData("DESCRIPTION", "�������:");
						data2.addData("MED_APPLY_NO", df.format(pageAmt));
						// ======pangben 2014-3-20
						// data2.addData("TIME_LIMIT", NULLSTR)
						// ;//===ʱ��====liling 20140331 ȥ��ʱ��
						data2.addData("CHN_DESC", NULLSTR);// ִ�еص�
						blankRow++;
						pageAmt = 0;
					}
				}
			}
			int resultLen1 = result.getCount() + 1 + blankRow;
			int len = (resultLen1 <= pageCount) ? (pageCount - resultLen1)
					: ((resultLen1 % pageCount == 0) ? 0
							: (((resultLen1 / pageCount) + 1) * pageCount - resultLen1));
			for (int i = 1; i <= len-blankPage; i++) {//modify caoyong ���-blankPage Ϊ�˱���������һ��ҽ�����ƹ������ֿհ�ҳ����
				data2.addData("BILL_FLG", NULLSTR);
				data2.addData("DEPT_CHN_DESC", NULLSTR);
				data2.addData("ORDER_DESC", NULLSTR);
				data2.addData("MEDI_QTY", NULLSTR);
				data2.addData("URGENT_FLG", NULLSTR);
				data2.addData("DESCRIPTION", NULLSTR);
				data2.addData("MED_APPLY_NO", NULLSTR);
				// ======pangben 2014-3-20
				// data2.addData("TIME_LIMIT", NULLSTR) ;//===ʱ��====liling
				// 20140331 ȥ��ʱ��
				data2.addData("CHN_DESC", NULLSTR);// ִ�еص�
			}
		}
		// if (result2.getCount() > 0) {
		// data2.addData("BILL_FLG", "");
		// data2.addData("DEPT_CHN_DESC", "���֪ͨ��");
		// data2.addData("ORDER_DESC", "");
		// data2.addData("MEDI_QTY", "");
		// data2.addData("URGENT_FLG", "");
		// data2.addData("DESCRIPTION", "");
		// data2.addData("MED_APPLY_NO", "");
		// }
		blankRow = 0;
		flg = false;
		// // for (int i = 0; i < result2.getCount(); i++) {
		// data2.addData("BILL_FLG", result2.getData("BILL_FLG", i));
		// data2.addData("DEPT_CHN_DESC", result2.getData("DEPT_CHN_DESC", i));
		// data2.addData("ORDER_DESC", result2.getData("ORDER_DESC", i));
		// data2.addData("MEDI_QTY", result2.getData("MEDI_QTY", i));
		// data2.addData("URGENT_FLG", result2.getData("URGENT_FLG", i));
		// data2.addData("DESCRIPTION", result2.getData("DESCRIPTION", i));
		// data2.addData("MED_APPLY_NO", result2.getData("MED_APPLY_NO", i));
		// pageAmt += StringTool.round(result2.getDouble("AR_AMT", i), 2);//add
		// by wanglong 20121226
		// int num = i + blankRow + 1 + 1;// ������i��+ �հ���(blankRow)+��һ�м���֪ͨ��(1)+1
		// if (!flg) {// ��һҳ
		// if (i == 6 || i == (result2.getCount() - 1)) {
		// data2.addData("BILL_FLG", "");
		// data2.addData("DEPT_CHN_DESC", "");
		// data2.addData("ORDER_DESC", "");
		// data2.addData("MEDI_QTY", "");
		// data2.addData("URGENT_FLG", "");
		// data2.addData("DESCRIPTION", "�������:");
		// data2.addData("MED_APPLY_NO", df.format(pageAmt));
		// flg = true;
		// blankRow++;
		// pageAmt = 0;
		// }
		// } else {// ����ҳ.//��9����ʾ���
		// if (i == result2.getCount() - 1) {
		// data2.addData("BILL_FLG", "");
		// data2.addData("DEPT_CHN_DESC", "");
		// data2.addData("ORDER_DESC", "");
		// data2.addData("MEDI_QTY", "");
		// data2.addData("URGENT_FLG", "");
		// data2.addData("DESCRIPTION", "�������:");
		// data2.addData("MED_APPLY_NO", df.format(pageAmt));
		// blankRow++;
		// pageAmt = 0;
		// } else if (i != result2.getCount() - 1 && ((num % 9) + 1) == 9) {
		// data2.addData("BILL_FLG", "");
		// data2.addData("DEPT_CHN_DESC", "");
		// data2.addData("ORDER_DESC", "");
		// data2.addData("MEDI_QTY", "");
		// data2.addData("URGENT_FLG", "");
		// data2.addData("DESCRIPTION", "�������:");
		// data2.addData("MED_APPLY_NO", df.format(pageAmt));
		// blankRow++;
		// pageAmt = 0;
		// }
		// }
		// }
		data2.setCount(data2.getCount("ORDER_DESC"));
		data2.addData("SYSTEM", "COLUMNS", "BILL_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		data2.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		data2.addData("SYSTEM", "COLUMNS", "MEDI_QTY");
		data2.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
		data2.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		data2.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
		// data2.addData("SYSTEM", "COLUMNS", "TIME_LIMIT");//====liling
		// 20140331 ȥ��ʱ��
		data2.addData("SYSTEM", "COLUMNS", "CHN_DESC");
		inParam.setData("ORDER_TABLE", data2.getData());
		// modify by lim 2012/02/23 begin
		DText text = (DText) odoMainControl.openPrintDialog(URLOPDNEWEXASHEET,
				inParam, true);

		// / add caoyong 20140321-------------start
		/*TParm sresult = new TParm();
		TParm spram = new TParm();
		TParm sinParam = new TParm();

		Iterator iter = listArray.iterator();
		while (iter.hasNext()) {
			String mapplyno = (String) iter.next();
			sinParam = OpdRxSheetTool.getInstance().getExaPrintParmR(parm,
					odoMainReg.realDeptCode, ODORxExa.EXA, odoMainControl.odo,
					mapplyno);
			for (int g = 0; g < result.getCount(); g++) {
				if ("LIS".equals(result.getValue("ORDER_CAT1_CODE", g))) {
					if (mapplyno.equals(result.getData("MED_APPLY_NO", g))) {
						sresult.addRowData(result, g);
					}
				}
			}
			for (int k = 0; k < sresult.getCount(); k++) {
				spram.addData("ORDER_DESC", sresult.getValue("ORDER_DESC", k));

			}
			TParm psresulParm = ODOTool.getInstance().getSyscate(
					sresult.getValue("ORDER_CODE", 0).substring(0, 5));
			spram.setCount(sresult.getCount("ORDER_DESC"));
			spram.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			spram.addData("SYSTEM", "COLUMNS", "");
			spram.addData("SYSTEM", "COLUMNS", "");
			spram.addData("SYSTEM", "COLUMNS", "");
			sinParam.setData("ORDER_TABLE", spram.getData());
			sinParam
					.setData("SAMPLE_SOURCE", "TEXT", sresult.getValue("BF", 0));
			sinParam.setData("PRINT_TIME", "TEXT", sresult.getValue("OPT_DATE",
					0));
			sinParam.setData("TITLE", "TEXT", psresulParm.getValue(
					"CATEGORY_CHN_DESC", 0)
					+ " ����������뵥");

			odoMainControl.openPrintDialog(URLOPDNEWEXASHEET_1, sinParam, true);
			spram = new TParm();
			sresult = new TParm();
			sinParam = new TParm();
			// / add caoyong 20140321-------------end
		}*/
		String rx_no = inParam.getValue("RX_NO");
		// ���봦��ǩ��
		String fileName = "������֪ͨ��_" + rx_no;//���򴦷� ������
		this.saveEMR(text, fileName, "EMR040001", "EMR04000142");
		// odoMainTmplt.onEmrM(result);
	}

	/**
	 * ��Ѫ����
	 */
	public void onBXResult() {
		TParm inparm = new TParm();
		inparm.setData("MR_NO", odoMainControl.odo.getMrNo());
		inparm.setData("ADM_TYPE", odoMainControl.odo.getAdmType());
		inparm.setData("USE_DATE", SystemTool.getInstance().getDate());
		inparm.setData("DEPT_CODE", odoMainControl.odo.getDeptCode());
		inparm.setData("DR_CODE", odoMainControl.odo.getDrCode());
		String sql = "SELECT * FROM OPD_DIAGREC WHERE CASE_NO = '"
				+ odoMainControl.odo.getCaseNo() + "' AND  MAIN_DIAG_FLG='Y' ";
		TParm diagRec = new TParm(odoMainControl.odo.getDBTool().select(sql));
		inparm.setData("ICD_CODE", diagRec.getCount() > 0 ? diagRec.getValue(
				"ICD_CODE", 0) : "");
		inparm.setData("ICD_DESC", diagRec.getCount() > 0 ? odoMainControl.odo
				.getDiagrec()
				.getIcdDesc(diagRec.getValue("ICD_CODE", 0), "chn") : "");
		inparm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		// û����ʷ��¼
		odoMainControl.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", inparm);
	}

	/**
	 * ��ӡ����
	 *
	 * @return Object
	 */
	public Object onPrintCase() throws Exception {
		TParm parm = new TParm();

		// ��� SUBCLASSCODE ���ߵ�
		parm.setData("SUBCLASSCODE_ZHUSU", TConfig
				.getSystemValue("ODOEmrTempletZSSUBCLASSCODE"));
		//
		//word.findFixed("OPDSubject").tryReset();
		//
		parm.setData("WORD_ZHUSU", word);
		//
		parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		parm.setData("MR_NO", odoMainControl.odo.getMrNo());
		parm.setData("MR", "TEXT", "�����ţ�" + odoMainControl.odo.getMrNo());
		//����ҳü�ϵĲ�����Ϣ  huangjunwen add 2014611
		Pat pat = Pat.onQueryByMrNo(odoMainControl.odo.getMrNo());
	    parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT",odoMainControl.odo.getMrNo());
	    parm.setData("filePatName","TEXT",pat.getName());
	    parm.setData("fileSex","TEXT",pat.getSexString());
	    String birthdate;
	    if(!"".equals(pat.getBirthday()))
	    	birthdate=pat.getBirthday().toString().substring(0,10).replace('-', '/');
	    else
	    	birthdate="";
	    parm.setData("fileBirthday","TEXT",birthdate);
	    parm.setData("FILE_HEAD_TITLE_IPD_NO","TEXT",pat.getIpdNo());
	    //===����ʷ
	    if(getAllegeParm(odoMainControl.odo.getMrNo())<=0){
	    	parm.setData("ALLEGE","TEXT","-");
	    }
	    //===�жϼ�������Ƿ�Ϊ��
//	    if(getcount(odoMainControl.odo.getMrNo())<=0){
//	    	parm.setData("PARAM1","TEXT","-");
//	    }
	    
	    if(this.getDataCount("5",odoMainControl.odo.getCaseNo())<=0){
	    	parm.setData("data1","-");//�жϼ������Ƿ�Ϊ�� add by huangjw 20150114
	    }
	    if(this.getDataCount("4",odoMainControl.odo.getCaseNo())<=0){
	    	parm.setData("data2","-");//�жϴ����Ƿ�Ϊ�� add by huangjw 20150114
	    }
	    if((this.getDataCount("1",odoMainControl.odo.getCaseNo())+this.getDataCount("2",odoMainControl.odo.getCaseNo()))<=0){
	    	parm.setData("data3","-");//�ж�ҩƷʱ��Ϊ�� add by huangjw 20150114
	    }
	    if(OpdRxSheetTool.getInstance().getPastHistory(odoMainControl.odo.getMrNo()).equals("")){//�жϼ���ʷ�Ƿ�Ϊ��
	    	parm.setData("pastHistory","-");
	    }else{
	    	parm.setData("pastHistory",OpdRxSheetTool.getInstance().getPastHistory(odoMainControl.odo.getMrNo()));
	    }
	    if(OpdRxSheetTool.getInstance().getRelatedHistory(odoMainControl.odo.getMrNo()).equals("")){//�жϼ���ʷ�Ƿ�Ϊ��
	    	parm.setData("familyHistory","-");
	    }else{
	    	parm.setData("familyHistory",OpdRxSheetTool.getInstance().getRelatedHistory(odoMainControl.odo.getMrNo()));
	    }
	    // add by wangb 2017/10/13 ����������������Ѫʷ
		String opBloodHistory = OpdRxSheetTool.getInstance().getOpBloodHistory(
				odoMainControl.odo.getMrNo());
		if (StringUtils.isEmpty(opBloodHistory)) {
			parm.setData("opBloodHistory", "-");
		} else {
			parm.setData("opBloodHistory", opBloodHistory);
		}
	    if(getDiagcount(odoMainControl.odo.getCaseNo())<=0){//�ж�����Ƿ�Ϊ��
	    	parm.setData("data4","-");
	    }
	    //����ҳü�ϵĲ�����Ϣ  huangjunwen add 2014611
		if (odoMainControl.isEng) {
			parm
					.setData("HOSP_NAME", "TEXT", Operator
							.getHospitalENGFullName());
		} else {
			parm
					.setData("HOSP_NAME", "TEXT", Operator
							.getHospitalCHNFullName());
		}
		parm.setData("DR_NAME", "TEXT", "ҽʦǩ��:"
				+ OpdRxSheetTool.getInstance().GetRealRegDr(
						odoMainControl.odo.getCaseNo()));
		parm.setData("REALDEPT_CODE", odoMainReg.realDeptCode);
		Object obj = new Object();
		if (ODOMainReg.O.equals(odoMainReg.admType)) {
			obj = odoMainControl.openPrintDialog(URLOPDCASESHEET1010, parm,
					false);

			// ����EMR���� beign
			//this.saveEMR(obj, "���ﲡ����¼", "EMR020001", "EMR02000106");
			//
			saveOnlyOneEMR(obj, "���ﲡ����¼", "EMR020001", "EMR02000106");
		
			// ����EMR���� end
		} else if (ODOMainReg.E.equals(odoMainReg.admType)) {
			// ==========pangben 2013-7-22 ���ﲡ����ӡ������ʱ������Ϊ��ǰʱ�䣬����ʹ��
			parm.setData("DATE", StringTool.getString(TypeTool
					.getTimestamp(SystemTool.getInstance().getDate()),
					"yyyy/MM/dd HH:mm:ss"));
			obj = odoMainControl.openPrintDialog(URLEMG, parm, false);
		}
		return obj;
	}
	/**
	 * ����ʷ����
	 * @param mrno
	 * @return
	 */
	public int getAllegeParm(String mrno){
		String sql="SELECT * FROM ( " +
				" SELECT    SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 0, 4)|| '/'" +
				"|| SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 5, 2)" +
				"|| '/'" +
				"|| SUBSTR (OPD_DRUGALLERGY.ADM_DATE, 7, 2) AS ADM_DATE," +
				" (CASE WHEN OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN '' ELSE (SELECT CHN_DESC" +
				" FROM SYS_DICTIONARY" +
				" WHERE     GROUP_ID = 'SYS_ALLERGY'" +
				" AND ID = OPD_DRUGALLERGY.DRUG_TYPE) END)" +
				" AS DRUG_TYPE,(SELECT ORDER_DESC FROM SYS_FEE" +
				" WHERE     ORDER_CODE = OPD_DRUGALLERGY.DRUGORINGRD_CODE" +
				" AND OPD_DRUGALLERGY.drug_type = 'B')" +
				" || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'PHA_INGREDIENT'" +
				" AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'A')" +
				" || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_ALLERGYTYPE'" +
				" AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'C')" +
				" || (CASE WHEN  OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN DRUGORINGRD_CODE END )" +
				" AS ORDER_DESC, OPD_DRUGALLERGY.ALLERGY_NOTE FROM OPD_DRUGALLERGY, SYS_DEPT, SYS_OPERATOR" +
				" WHERE OPD_DRUGALLERGY.MR_NO = '"+mrno+"'" +
				" AND OPD_DRUGALLERGY.DEPT_CODE = SYS_DEPT.DEPT_CODE" +
				" AND OPD_DRUGALLERGY.DR_CODE = SYS_OPERATOR.USER_ID" +
				" ) WHERE TRIM(ORDER_DESC) <> '-'";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		int count=parm.getCount();
		return count;
	}
	/**
	 * ҽ������
	 * @param rx_type
	 * @param caseno
	 * @return
	 */
	public  int getDataCount(String rx_type,String caseno){
		int i=0;
		String sql="SELECT OPD_ORDER.ORDER_DESC || '  ' || SYS_FEE.TRADE_ENG_DESC, "+
                   "TO_CHAR (OPD_ORDER.ORDER_DATE, 'mm/dd hh24:mi')|| ' '|| SYS_OPERATOR.USER_NAME "+
                   "FROM OPD_ORDER, SYS_FEE, SYS_OPERATOR "+
                   "WHERE     CASE_NO = '"+caseno+"' "+
                   "AND RX_TYPE = '"+rx_type+"' "+
                   "AND SYS_FEE.ORDER_CODE = OPD_ORDER.ORDER_CODE "+
                   "AND SYS_OPERATOR.USER_ID = OPD_ORDER.DR_CODE "+
                   "AND (   OPD_ORDER.ORDERSET_CODE IS NULL OR OPD_ORDER.SETMAIN_FLG = 'Y') "+
                   "ORDER BY rx_no, link_no, seq_no ";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			i=parm.getCount();
		}
		return i;
	}
	/**
	 * ��� ����
	 * @param caseNo
	 * @return
	 */
	public int getDiagcount(String caseNo){
		int i=0;
		String sql="SELECT CASE WHEN OPD_DIAGREC.MAIN_DIAG_FLG = 'Y' THEN '��' ELSE '' END || '',"+
			" SYS_DIAGNOSIS.ICD_CHN_DESC || '  ' || OPD_DIAGREC.DIAG_NOTE || '   '"+
			" FROM OPD_DIAGREC, SYS_DIAGNOSIS"+
			" WHERE     CASE_NO = '"+caseNo+"'"+
			" AND SYS_DIAGNOSIS.ICD_CODE = OPD_DIAGREC.ICD_CODE";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			i=parm.getCount();
		}
		return i;
	}
	/**
	 * ���ﲡ�� add by huangjw 20150112
	 */
	public void onRePrintCase(){
		TParm parm = new TParm();
        TParm emrParm = new TParm();
        String caseNo = odoMainControl.odo.getCaseNo();
        emrParm.setData("MR_CODE", TConfig.getSystemValue("OEEmrMRCODE"));
        emrParm.setData("CASE_NO", caseNo);
        emrParm.setData("DEPT_CODE", odoMainControl.odo.getDeptCode());
        TParm result = new TParm();
        String subClassCode=TConfig.getSystemValue("OEEmrMRCODE");
     // 1.ȡ��ҽ�����Ƶ�ģ��(����)
        String sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
            + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
            + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
            + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
            + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '6' "
            + " AND DEPTORDR_CODE = '" + Operator.getID() +
            "' AND B.OPD_FLG = 'Y' "
            +" AND B.SUBCLASS_CODE = '" + subClassCode+"'"
            +" AND A.MAIN_FLG = 'Y'"
            + " ORDER BY MAIN_FLG DESC ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result == null || result.getCount() <= 0) {
            // 2.ȡ�ÿ����ƶ���ģ��(����)
            sql = " SELECT B.CLASS_CODE,B.SUBCLASS_CODE,B.SUBCLASS_DESC, "
                + " B.TEMPLET_PATH,B.SEQ,A.DEPTORDR_CODE AS DEPT_CODE,"
                + " B.EMT_FILENAME FROM OPD_COMTEMPLET A, EMR_TEMPLET B "
                + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                + " AND A.SEQ = B.SEQ AND A.DEPT_OR_DR = '5' "
                + " AND DEPTORDR_CODE = '" + odoMainControl.odo.getDeptCode() +
                "' AND B.OPD_FLG = 'Y' "
                +" AND B.SUBCLASS_CODE = '" + subClassCode+"'"
                +" AND A.MAIN_FLG = 'Y'"
                + " ORDER BY MAIN_FLG DESC ";
            result = new TParm(TJDODBTool.getInstance().select(sql));
        }
        if (result!=null&&result.getCount()> 0) {
        	emrParm.setData("SEQ", result.getValue("SEQ", 0));
        }
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        if (odoMainReg.admType.equals("O")) {
        	parm.setData("SYSTEM_TYPE", "ODO");
        }else if(odoMainReg.admType.equals("E")){
        	parm.setData("SYSTEM_TYPE", "EMG");
        }
        parm.setData("ADM_TYPE", odoMainControl.odo.getAdmType());
        parm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
        parm.setData("PAT_NAME", odoMainControl.getValue("PAT_NAME"));
        parm.setData("MR_NO", odoMainControl.odo.getMrNo());
        parm.setData("ADM_DATE", odoMainControl.odo.getRegPatAdm().getItemData(0, "ADM_DATE"));
        parm.setData("DEPT_CODE", odoMainControl.odo.getDeptCode());
        parm.setData("EMR_FILE_DATA", emrParm);
        parm.setData("RULETYPE","2");//�޸�Ȩ��
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
        parm.setData("SEEN_DR",OpdRxSheetTool.getInstance().GetRegDr(caseNo));//����ҽ��
        parm.setData("SEEN_DR_TIME",OpdRxSheetTool.getInstance().getSeenDrDate(caseNo));//����ʱ��
        odoMainControl.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
        
	}
	/**
	 * ��ӡ����֪ͨ��
	 *
	 * @param parm
	 *            TParm
	 */
	public void onPrintOp(TParm parm) throws Exception {
		if(JOptionPane.showConfirmDialog(null, "�Ƿ��ӡ���ƴ��õ���", "��Ϣ",
    				JOptionPane.YES_NO_OPTION)==0){//������ʾ��Ϣ add by  huangjw 20150511
			TParm inParam = new TParm();
			inParam = OpdRxSheetTool.getInstance().getExaPrintParm(parm,
					odoMainReg.realDeptCode, ODORxOp.OP, odoMainControl.odo);
			// modify by liming 2012/02/23 begin
			String rxNo1 = inParam.getValue("RX_NO");
			String caseNo1 = odoMainControl.caseNo;
			TParm dataParm = new TParm();
			if (rxNo1 != null && rxNo1.trim().length() > 0) {
				dataParm = ODOTool.getInstance().getPrintOpData(caseNo1, rxNo1,
						odoMainOpdOrder.rxType);
			}
			if (dataParm.getErrCode() < 0) {
				odoMainControl.messageBox("E0001");
				return;
			}
			if (dataParm.getCount() < 0) {
//				odoMainControl.messageBox("û�д���֪ͨ������.");//==liling 20140717����
				return;
			}
			TParm myParm = new TParm();
			boolean flg1 = false;
			int blankRow1 = 0;
			double pageAmt1 = 0;
			DecimalFormat df1 = new DecimalFormat("############0.00");
			for (int i = 0; i < dataParm.getCount(); i++) {
				String orderDesc = dataParm.getValue("ORDER_DESC", i);
				/*if (orderDesc.length() <= 29) {
					StringBuilder temp = new StringBuilder();
					for (int j = 1; j <= 58 - orderDesc.length(); j++) {
						temp.append(" ");
					}
					orderDesc = dataParm.getValue("ORDER_DESC", i)
							+ temp.toString();
				}*/
				myParm.addData("BILL_FLG", dataParm.getData("BILL_FLG", i));
				myParm.addData("DEPT_CHN_DESC", dataParm
						.getData("DEPT_CHN_DESC", i));
				myParm.addData("ORDER_DESC", orderDesc);
				myParm.addData("DOSAGE_QTY", dataParm.getData("DOSAGE_QTY", i));// ��MEDI_QTY��ΪDOSAGE_QTY
																				// wanglong
																				// modify
																				// 20140421
				myParm.addData("URGENT_FLG", dataParm.getData("URGENT_FLG", i));
				myParm.addData("DESCRIPTION", dataParm.getData("DESCRIPTION", i));
				// �ۼ�
				pageAmt1 += (dataParm.getDouble("DOSAGE_QTY", i)
						* dataParm.getDouble("OWN_PRICE", i) * dataParm.getDouble(
						"DISCOUNT_RATE", i));// modify by wanglong 20121226
				pageAmt1 = StringTool.round(pageAmt1, 2);// add by wanglong 20121226
				// TODO:��###########������Ҫ��ÿҳ����Ľ�����.�û�õ�����*getEveryAmt(ORDERCODE)�����ÿ����¼�Ľ�
				int num = i + blankRow1 + 1;// ������i��+ �հ���(blankRow)+1
				if (!flg1) {// ��һҳ
					if (i == 4 || i == (dataParm.getCount() - 1)) {
						myParm.addData("BILL_FLG", NULLSTR);
						myParm.addData("DEPT_CHN_DESC", NULLSTR);
						myParm.addData("ORDER_DESC", NULLSTR);
						myParm.addData("DOSAGE_QTY", NULLSTR);
						myParm.addData("URGENT_FLG", "�������:");
						myParm.addData("DESCRIPTION", df1.format(pageAmt1));
						flg1 = true;
						blankRow1++;
						pageAmt1 = 0;
					}
				} else {// ����ҳ.//��5����ʾ���
					if (i == dataParm.getCount() - 1) {
						myParm.addData("BILL_FLG", NULLSTR);
						myParm.addData("DEPT_CHN_DESC", NULLSTR);
						myParm.addData("ORDER_DESC", NULLSTR);
						myParm.addData("DOSAGE_QTY", NULLSTR);
						myParm.addData("URGENT_FLG", "�������:");
						myParm.addData("DESCRIPTION", df1.format(pageAmt1));
						blankRow1++;
						pageAmt1 = 0;
					} else if (i != dataParm.getCount() - 1 && ((num % 10) + 1) == 10) {
						myParm.addData("BILL_FLG", NULLSTR);
						myParm.addData("DEPT_CHN_DESC", NULLSTR);
						myParm.addData("ORDER_DESC", NULLSTR);
						myParm.addData("DOSAGE_QTY", NULLSTR);
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
			myParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			myParm.addData("SYSTEM", "COLUMNS", "URGENT_FLG");
			myParm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
			inParam.setData("ORDER_TABLE", myParm.getData());
			// modify by liming 2012/02/23 end
			//==liling 20140718 add ���ô�ӡ���� start====
			String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdNewHandleSheet_V45.prtSwitch");
			if(prtSwitch.equals(IReportTool.ON)){
				//==liling 20140718 add ���ô�ӡ���� end====
			//modify by huangjw20140717 start
			DText text = (DText) odoMainControl.openPrintDialog(IReportTool.getInstance().getReportPath(
					URLOPDNEWHANDLESHEET), IReportTool.getInstance().getReportParm("OpdNewHandleSheet_V45.class",inParam), true);
			//modify by huangjw20140717 end
			// ���봦��ǩ��
			String fileName = "����֪ͨ��_" + rxNo1;
			this.saveEMR(text, fileName, "EMR040002", "EMR04000203");
			}
		}
		
	}

	/**
	 * ���ò������
	 */
	public void onCaseSheet(String type) throws Exception {
		TParm inParm = new TParm();
		if (odoMainControl.odo == null || odoMainPat.pat == null)
			return;
		inParm.setData("MR_NO", odoMainControl.odo.getMrNo());
		inParm.setData("CASE_NO", odoMainControl.odo.getCaseNo());
		inParm.setData("DEPT_CODE", odoMainReg.realDeptCode);
		if ("en".equals(odoMainControl.getLanguage())) // �ж��Ƿ���Ӣ�Ľ���
			inParm.setData("PAT_NAME", odoMainPat.pat.getName1());
		else
			inParm.setData("PAT_NAME", odoMainPat.pat.getName());
		inParm.setData("OPD_ORDER", odoMainControl.odo.getOpdOrder());
		inParm.setData("ADM_DATE", odoMainReg.reg.getAdmDate());
		inParm.setData("ODO", odoMainControl.odo);
		int[] mainDiag = new int[1];
		if (odoMainControl.odo.getDiagrec().haveMainDiag(mainDiag)) {
			String icdCode = odoMainControl.odo.getDiagrec().getItemString(
					mainDiag[0], "ICD_CODE");
			inParm.setData("ICD_CODE", icdCode);
			inParm.setData("ICD_DESC", odoMainControl.odo.getDiagrec()
					.getIcdDesc(icdCode, odoMainControl.getLanguage()));
		}
		inParm.setData("TYPE", type);
		odoMainControl.openDialog(URLODOCASESHEET, inParm, false);
	}

	/**
	 * ��ҩ������ҩƷ�����õ�checkBox�¼�
	 *
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onCheckBox(Object obj) throws Exception {
		TTable table = (TTable) obj;
		table.acceptText();
		table.setDSValue();
		return false;
	}

	// @Override
	public Object callMessage(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ����ǰʹÿ��TABLE��û�б༭״̬
	 */
	public void acceptOtherForSave() throws Exception {
		TTable table = (TTable) odoMainControl.getComponent(TABLEALLERGY);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(TABLEDIAGNOSIS);
		table.acceptText();
		table = (TTable) odoMainControl.getComponent(TABLEMEDHISTORY);
		table.acceptText();
	}

	// /**
	// * ����
	// * @throws Exception
	// */
	// public void onSave(int step) throws Exception{
	// TParm orderParm = new TParm();
	// TParm ctrlParm = new TParm();
	// TParm chnParm = new TParm();
	// TParm exaParm = new TParm();
	// TParm opParm = new TParm();
	// switch (step) {
	// case 1:
	// if (!odoMainPat.isMyPat()) {
	// saveSubjrec();
	// odoMainControl.messageBox("E0193");
	// return;
	// }
	// int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
	// if (rowMainDiag < 0) {
	// odoMainControl.messageBox("E0065");
	// return;
	// }
	// saveSubjrec();
	// break;
	// case 2:
	// if (odoMainControl.odo.getOpdOrder().isModified()) {
	// orderParm = odoMainControl.odo.getOpdOrder().getModifiedOrderRx();
	// ctrlParm = odoMainControl.odo.getOpdOrder().getModifiedCtrlRx();
	// chnParm = odoMainControl.odo.getOpdOrder().getModifiedChnRx();
	// exaParm = odoMainControl.odo.getOpdOrder().getModifiedExaRx();
	// odoMainControl.odo.getOpdOrder().updateMED(odoMainControl.odo);
	// opParm = odoMainControl.odo.getOpdOrder().getModifiedOpRx();
	// if (orderParm.getCount() > 0) {
	// if
	// (!odoMainControl.odo.getOpdOrder().isOrgAvalible(odoMainOpdOrder.phaCode))
	// {
	// odoMainControl.messageBox("E0117");
	// }
	// }
	// if (orderParm.getCount("RX_NO") > 0) {
	// onPrintOrder(orderParm, ODORxMed.MED);
	// }
	// if (ctrlParm.getCount("RX_NO") > 0) {
	// onPrintOrder(ctrlParm, ODORxCtrl.CTRL);
	// }
	// if (chnParm.getCount("RX_NO") > 0) {
	// onPrintOrder(chnParm, ODORxChnMed.CHN);
	// }
	// if (exaParm.getCount("RX_NO") > 0) {
	// // ��ӡ������ҽ����
	// onPrintExa(exaParm);
	// SwingUtilities.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// odoMainTmplt.onEmr();
	// } catch (Exception e) {
	// }
	// }
	// });
	// }
	// if (opParm.getCount("RX_NO") > 0) {
	// onPrintOp(opParm);
	// }
	// }
	// break;
	// case 3:
	// int countPreOrder = odoMainControl.odo.getDiagrec().rowCount();
	// for (int i = 0; i < countPreOrder; i++) {
	// if (odoMainControl.odo.getDiagrec().isContagion(i)) {
	// TParm can = new TParm();
	// can.setData("MR_NO", odoMainControl.odo.getMrNo());
	// can.setData("CASE_NO", odoMainControl.odo.getCaseNo());
	// can.setData("ICD_CODE", odoMainControl.odo.getDiagrec().getItemString(i,
	// "ICD_CODE"));
	// can.setData("DEPT_CODE", Operator.getDept());
	// can.setData("USER_NAME", Operator.getName());
	// odoMainControl.openDialog(URLMROINFECT, can);
	// }
	// }
	// onContagionReport();// ��Ⱦ����������
	// break;
	// default:
	// break;
	// }
	// }

	/**
	 * ����
	 *
	 * @throws Exception
	 */
	public void onSave(TParm orderParm, TParm ctrlParm, TParm chnParm,
			TParm exaParm, TParm opParm) throws Exception { 
		if (orderParm.getCount("RX_NO") > 0) {
			onPrintOrder(orderParm, ODORxMed.MED);
			onCaseSheet(ODORxMed.MED);//add  by huangjw 20160429 ������ҩʱ�����������
		}
		if (ctrlParm.getCount("RX_NO") > 0) {
			onPrintOrder(ctrlParm, ODORxCtrl.CTRL);
			onCaseSheet(ODORxCtrl.CTRL);
		}
		if (chnParm.getCount("RX_NO") > 0) {
//			onPrintOrder(chnParm, ODORxChnMed.CHN);
			onCaseSheet(ODORxChnMed.CHN); 
		}
		if (exaParm.getCount("RX_NO") > 0) {
			// ��ӡ������ҽ����
			//onPrintExa(exaParm);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						onEmrM();
					} catch (Exception e) {
					}
				}
			});
		}
		if (opParm.getCount("RX_NO") > 0) {
			onPrintOp(opParm);
		}
		int countPreOrder = odoMainControl.odo.getDiagrec().rowCount();
		for (int i = 0; i < countPreOrder; i++) {
			if (odoMainControl.odo.getDiagrec().isContagion(i)) {
				TParm can = new TParm();
				can.setData("MR_NO", odoMainControl.odo.getMrNo());
				can.setData("CASE_NO", odoMainControl.odo.getCaseNo());
				can.setData("ICD_CODE", odoMainControl.odo.getDiagrec()
						.getItemString(i, "ICD_CODE"));
				can.setData("DEPT_CODE", Operator.getDept());
				can.setData("USER_NAME", Operator.getName());
				can.setData("ADM_TYPE", odoMainControl.odo.getAdmType());// add
																			// by
																			// wanglong
																			// 20140307
				odoMainControl.openDialog(URLMROINFECT, can);
			}
		}
		onContagionReport();// ��Ⱦ����������
	}

	/**
	 * �ݴ�
	 *
	 * @throws Exception
	 */
	public void onTempSave(TParm orderParm,TParm exaParm, TParm opParm, TParm ctrlParm,TParm  chnParm)
			throws Exception {
		if (exaParm.getCount("RX_NO") > 0) {
			// ��ӡ������ҽ����
			//onPrintExa(exaParm);
			getRun(1);
		}
		if (orderParm.getCount("RX_NO") > 0) {
			 onPrintOrder(orderParm, ODORxMed.MED);//��ҩ��ӡ����ǩ
			 onCaseSheet(ODORxMed.MED);//add  by huangjw 20160429 ������ҩʱ�����������
		}
		// ==========yanjing 2014-04-18 ɾ�����ô���ǩ��Ҫ����ʾ
		 if (opParm.getCount("RX_NO") > 0) {//���ô�ӡ����ǩ
		 onPrintOp(opParm);
		 }
		 if(chnParm.getCount("RX_NO") > 0){
//			 onPrintOrder(chnParm,ODORxChnMed.CHN);//��ҩ��ӡ����ǩ
			 onCaseSheet(ODORxChnMed.CHN);
		 }
		if (ctrlParm.getCount("RX_NO") > 0) {// ����ҩƷ��ӡ����ǩ
			onPrintOrder(ctrlParm, ODORxCtrl.CTRL);
			onCaseSheet(ODORxCtrl.CTRL);
		}
		
		getRun(4);
	}
	
	public void saveAllegyHistory(){
		//add by huangtt 20171228�����ݿ���Զ������
		String subclassCode = TConfig.getSystemValue("ONWEmrMRCODE");
		String sql = " SELECT FILE_PATH,FILE_NAME FROM EMR_FILE_INDEX WHERE SUBCLASS_CODE='"+subclassCode+"' AND CASE_NO='"+odoMainControl.odo.getCaseNo()+"' ORDER BY OPT_DATE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()> 0){
			String allegyHistory="";
			String filePath="";
			String fileName="";
			for(int i=0;i<parm.getCount();i++){
				 filePath=parm.getValue("FILE_PATH",i);
				 fileName=parm.getValue("FILE_NAME",i);
				allegyWord.onOpen(filePath, fileName, 3, false);
				if(allegyWord.getCaptureValue("AllegyHistory").trim().length()>0){
					allegyHistory=allegyWord.getCaptureValue("AllegyHistory");
				}
	
			}
			
			

			String mrNo = odoMainPat.pat.getMrNo();
			String caseNo = odoMainReg.reg.caseNo();
			sql = "SELECT * FROM OPD_DRUGALLERGY WHERE MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
			TParm parmA = new TParm(TJDODBTool.getInstance().select(sql));
			TParm result = new TParm();
			if(parmA.getCount()>0){
				
				if(allegyHistory.trim().length() == 0 || allegyHistory.trim().equals("-")){
					String sqlD = "DELETE FROM OPD_DRUGALLERGY WHERE MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
					result = new TParm(TJDODBTool.getInstance().update(sqlD));
				}else{
					//����
					String sqlU = "UPDATE OPD_DRUGALLERGY SET DRUGORINGRD_CODE ='"+allegyHistory+"',CASE_NO='"+caseNo+"' WHERE MR_NO='"+mrNo+"' AND DRUG_TYPE='D'";
					result = new TParm(TJDODBTool.getInstance().update(sqlU));
					
				}
				
			}else{
				
				if(allegyHistory.trim().length() > 0 && !allegyHistory.trim().equals("-")){
					//����
					String admType = odoMainReg.reg.getAdmType();
					String date = SystemTool.getInstance().getDate().toString();
					date = date.replaceAll("-", "").replaceAll("/", "").replaceAll(":", "").replaceAll(" ", "").substring(0, 14);
					
					String sqlI = "INSERT INTO OPD_DRUGALLERGY" +
							"(MR_NO, ADM_DATE, DRUG_TYPE, DRUGORINGRD_CODE, ADM_TYPE, " +
							" CASE_NO, DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, " +
							" OPT_TERM)" +
							" VALUES" +
							" ('"+mrNo+"', '"+date+"', 'D', '"+allegyHistory+"', '"+admType+"', " +
							" '"+caseNo+"', '"+Operator.getDept()+"', '"+Operator.getID()+"', '"+Operator.getID()+"', SYSDATE, " +
							" '"+Operator.getIP()+"')";
					result = new TParm(TJDODBTool.getInstance().update(sqlI));
				}

			}
			
			if(result.getErrCode() < 0){
				
				return;
			}
			
		
			
			
		}

		
		
	}

	/**
	 * ��������
	 *
	 * @return
	 * @throws Exception
	 */
	public boolean onSaveSubjrec() throws Exception {
		if (!odoMainPat.isMyPat()) { // �жϽ���
			saveSubjrec();
			odoMainControl.messageBox("E0193");
			return true;
		}
		//modify by huangtt 20141126
		if(word.getCaptureValueNoDel("SUB").trim().length() == 0 || odoMainControl.odo.getOpdOrder().isModified()){
			int rowMainDiag = odoMainControl.odo.getDiagrec().getMainDiag();
			if (rowMainDiag < 0) {
				odoMainControl.messageBox("E0065");
				return true;
			}
		}

		saveSubjrec();
		saveAllegyHistory();
		
		return false;
	}
	/**
	 * ����ҩƷ add caoy
	 * @param df2
	 * @param rxNo
	 * @param inParam
	 */
		public void getDrugParm(DecimalFormat df2, String rxNo, TParm inParam,String side_x) {
			TParm drugResult = ODOTool.getInstance().getPrintOrderData(
					odoMainControl.caseNo, rxNo);
			TParm drugParm=new TParm();
			
			
			double totAmt=0;
			for (int j = 0; j < drugResult.getCount(); j++) {

				drugParm.addData("AA", "");
				drugParm.addData("BB", drugResult.getData("BB", j));
				drugParm.addData("CC", drugResult.getData("ER", j));
				drugParm.addData("AA", NULLSTR);
				drugParm.addData("BB", "   " + "�÷���ÿ��"
						+ drugResult.getData("HH", j));
				drugParm.addData("CC", drugResult.getData("FF", j) + "  "
						+ drugResult.getData("DD", j));

				totAmt += (drugResult.getDouble("DOSAGE_QTY", j)
						* drugResult.getDouble("OWN_PRICE", j) * drugResult
						.getDouble("DISCOUNT_RATE", j));
				totAmt = StringTool.round(totAmt, 2);
			}
			drugParm.setCount(drugParm.getCount("AA"));
			drugParm.addData("SYSTEM", "COLUMNS", "AA");
			drugParm.addData("SYSTEM", "COLUMNS", "BB");
			drugParm.addData("SYSTEM", "COLUMNS", "CC");
			inParam.setData("ORDER_TABLE", drugParm.getData());
			inParam.setData("TOT_AMT", "TEXT", df2.format(totAmt));
			inParam.setData("SIDE", "TEXT", side_x);
			
			
		}
	/**
	 * add caoy ��ӡƿ��ǩ
	 * @param rxNo
	 * @param inParam
	 */
		public void getPrintBottleLabel(String rxNo, TParm inParam,TParm bottParm,String opdNewName) {
			TParm parmData=new TParm();
			Timestamp today = SystemTool.getInstance().getDate();
			SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
				String flg="";
				for(int k=0;k<bottParm.getCount();k++){
					if(!"".equals(bottParm.getValue("FLG", k))){
						flg=bottParm.getValue("FLG", k);
					}else{
						flg="  ";
					}
					parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
					parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));
			        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k));
			        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
			        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
			        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//���ҽʦ��ע add by huangjw 20141222
			        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
			        parmData.addData("EXEC_DATE",bottParm.getValue("EXEC_DATE", k));
			        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
//			        System.out.println("111������linkNo is����"+bottParm.getValue("LINK_NO",k));
//			        System.out.println("222������linkNo is����"+bottParm.getValue("LINK_NO",k+1));
			        if(k!=bottParm.getCount()-1){
			        	if("".equals(bottParm.getValue("LINK_NO",k))){
			        		parmData.addData(".TableRowLineShow", true);
			        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
			        		parmData.addData(".TableRowLineShow", true);
			        	}else{
			        		parmData.addData(".TableRowLineShow", false);
			        	}
			        	
			        }
			}
				parmData.setCount(bottParm.getCount());
				parmData.addData("SYSTEM", "COLUMNS", "FLG");
				parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
				parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
				parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
				parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
				parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
				parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//���ҽʦ��ע add by huangjw 20141222
				parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
				parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
//				parmData.addData("SYSTEM", "COLUMNS", "NS_NOTE");
				inParam.setData("TABLE1", parmData.getData());
				inParam.setData("DATE", "TEXT", format.format(today));
				inParam.setData("SIDE", "TEXT", "������");
				inParam.setData("OpdNewExaName", "TEXT", opdNewName);
				String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdBottleLabelOrder_V45.prtSwitch");
				if(prtSwitch.equals(IReportTool.ON)){
					Object obj = odoMainControl.openPrintDialog(IReportTool.getInstance().getReportPath(URLBOTTLELABEL),
							IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_V45.class",inParam), true);
				}
		}
		/**
		 * ע��ִ�е�
		 * @param rxNo
		 * @param inParam
		 */
			public void getPrintBottleLabel_I(String rxNo, TParm inParam,TParm bottParm,String opdNewName) {
				TParm parmData=new TParm();
				Timestamp today = SystemTool.getInstance().getDate();
				SimpleDateFormat format=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				
					String flg="";
					for(int k=0;k<bottParm.getCount();k++){
						if(!"".equals(bottParm.getValue("FLG", k))){
							flg=bottParm.getValue("FLG", k);
						}else{
							flg="  ";
						}
						parmData.addData("FLG", flg+" "+bottParm.getValue("LINK_NO",k));
						parmData.addData("ORDER_DESC", bottParm.getValue("ORDER_DESC", k));
				        parmData.addData("DISPENSE_QTY",bottParm.getValue("DISPENSE_QTY", k));
				        parmData.addData("FREQ_CHN_DESC",bottParm.getValue("FREQ_CHN_DESC", k));
				        parmData.addData("ROUTE_CHN_DESC", bottParm.getValue("ROUTE_CHN_DESC", k));
				        parmData.addData("DR_NOTE", bottParm.getValue("DR_NOTE",k));//���ҽʦ��ע add by huangjw 20141222
				        parmData.addData("EXEC_DR_DESC",bottParm.getValue("EXEC_DR_DESC", k));
				        parmData.addData("EXEC_END_DATE","");
				        parmData.addData("EXEC_DATE","");//bottParm.getValue("EXEC_DATE", k)
				        parmData.addData("TAKE_DAYS",bottParm.getValue("TAKE_DAYS", k));
//				        System.out.println("111������linkNo is����"+bottParm.getValue("LINK_NO",k));
//				        System.out.println("222������linkNo is����"+bottParm.getValue("LINK_NO",k+1));
				        if(k!=bottParm.getCount()-1){
				        	if("".equals(bottParm.getValue("LINK_NO",k))){
				        		parmData.addData(".TableRowLineShow", true);
				        	}else if(!(bottParm.getValue("LINK_NO",k).equals(bottParm.getValue("LINK_NO",k+1)))){
				        		parmData.addData(".TableRowLineShow", true);
				        	}else{
				        		parmData.addData(".TableRowLineShow", false);
				        	}
				        	
				        }
				}
					parmData.setCount(bottParm.getCount());
					parmData.addData("SYSTEM", "COLUMNS", "FLG");
					parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
					parmData.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
					parmData.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
					parmData.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
					parmData.addData("SYSTEM", "COLUMNS", "ROUTE_CHN_DESC");
//					parmData.addData("SYSTEM", "COLUMNS", "DR_NOTE");//���ҽʦ��ע add by huangjw 20141222
					parmData.addData("SYSTEM", "COLUMNS", "EXEC_DATE");//yanjing ����ִ�л�ʿ��ִ��ʱ�� 20150226
					parmData.addData("SYSTEM", "COLUMNS", "EXEC_END_DATE");
					parmData.addData("SYSTEM", "COLUMNS", "EXEC_DR_DESC");
					
//					parmData.addData("SYSTEM", "COLUMNS", "NS_NOTE");
					inParam.setData("TABLE1", parmData.getData());
					inParam.setData("DATE", "TEXT", format.format(today));
					inParam.setData("SIDE", "TEXT", "������");
					inParam.setData("OpdNewExaName_I", "TEXT", opdNewName);
					String prtSwitch=IReportTool.getInstance().getPrintSwitch("OpdBottleLabelOrder_I_V45.prtSwitch");
					if(prtSwitch.equals(IReportTool.ON)){
						Object obj = odoMainControl.openPrintDialog(IReportTool.getInstance().getReportPath(URLBOTTLELABEL_I),
								IReportTool.getInstance().getReportParm("OpdBottleLabelOrder_I_V45.class",inParam), true);
					}
			}
		
		
	
		/**
		 * ���¸�ֵ�����
		 */
		public void onPHY(){ 
			ECapture capture = word.findCapture("PHY");		
			List aa = capture.getElements();
			for (int i = 0; i < aa.size(); i++) {				
				if(aa.get(i).toString().indexOf("EFixed") > 0){
					EFixed fixed = (EFixed) aa.get(i);
					if("�̶��ı�".equals(fixed.getName())){
//						System.out.println(aa.get(i));
//						System.out.println(fixed.getTryFileName());
//						System.out.println(fixed.getTryName());
						if(fixed.getTryFileName().length()>0 && fixed.getTryName().length() > 0){
							fixed.tryReset();
						}

						
					}
				}
				
				
			}	

			

			

		}
		
	/**
	 * �������±깦��
	 */
	public void onInsertMarkText() {

		word.insertFixed();
		word.onOpenMarkProperty();
		word.update();

	}

	/**
	 * ���±��ı�����
	 */
	public void onMarkTextProperty() {
		EComponent com = word.getFocusManager().getFocus();
		if (com != null && (com instanceof EFixed)) {
		 word.onOpenMarkProperty();
		 word.update();
		}
	}
    
	/**
	 * Ƭ���¼
	 *
	 * @param value
	 *            String
	 */
/*	public void onReturnContent1(String value) {
		if (!word.pasteString(value)) {
			// ִ��ʧ��
			odoMainControl.messageBox("E0005");
		}
	}*/
    
	public boolean diagCanSave(){
		boolean re = true;
		int f =0;
		boolean isEmptyFlg = false;//�ж��Զ�������Ƿ�Ϊ�� add  by huangjw
		for (int i = 0; i < tblDiag.getDataStore().rowCount(); i++) {
			if("Y".equals(tblDiag.getItemData(i, "MAIN_DIAG_FLG"))){
				f++;
			}
		}
		if(f > 1){
			odoMainControl.messageBox("ֻ����һ�������");
			return true;
		}
		if( f == 1){
			re=false;
		}
		
		if( f == 0){
			odoMainControl.messageBox("û�������");
			return true;//add  by huangjw
		}
		for (int i = 0; i < tblDiag.getRowCount() - 1; i++) {
			String diagCode = odoMainControl.odo.getDiagrec()
			.getItemString(i, "ICD_CODE");
			String diagName = odoMainControl.odo.getDiagrec().getIcdDesc(
					diagCode, "");
			String diagNote = odoMainControl.odo.getDiagrec()
			.getItemString(i, "DIAG_NOTE");
			if((diagName == null || "".equals(diagName)) && (diagNote == null || "".equals(diagNote))){
				isEmptyFlg = true;
			}
		}
		if(isEmptyFlg){//add  by huangjw
			odoMainControl.messageBox("��ɾ������ϻ���������");
			return true;
		}
		return re;
	}
	
	public void onMaternalAction(){
		String flg = odoMainControl.getValueString("HIGHRISKMATERNAL_FLG");
		if("Y".equals(flg)){
			odoMainControl.setValue("MATERNAL_MESSAGE", "��Σ����");
		}else{
			odoMainControl.setValue("MATERNAL_MESSAGE", "");
		}
	}
		
}
