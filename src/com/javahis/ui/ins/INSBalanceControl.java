package com.javahis.ui.ins;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJAdm;
import jdo.ins.INSTJTool;
import jdo.mro.MRORecordTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;



/**
 * 
 * <p>
 * Title:סԺ���÷ָ�
 * </p>
 * 
 * <p>
 * Description:סԺ���÷ָ� �ϲ������ַָ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2011-12-01
 * @version 2.0
 */
public class INSBalanceControl extends TControl {
	// ����
	private Compare compare = new Compare();
	// ����
	private boolean ascending = false;
	// ����
	private int sortColumn = -1;
	// ҽ�����
	String nhiCode = "";
	private TTable tableInfo; // ����������Ϣ�б�
	private TTable oldTable; // ���÷ָ�ǰ����
	private TTable newTable; // ���÷ָ������
	private TTabbedPane tabbedPane; // ҳǩ
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	DateFormat df1 = new SimpleDateFormat("yyyy");
	private TParm regionParm; // ҽ���������
	int index = 0; // ���÷ָ� �ۼ���Ҫ������ݸ���
	int selectNewRow; // ���÷ָ����ϸ���ݻ�õ�ǰѡ����
	String type; // TYPE: SINGLE�����ֽ�����ʾ
	//Color red = new Color(255, 0, 0); // ��ϸ���ز�ͬ������ɫ
	private String showValue = "IDNO;IN_DATE;STATION_CODE;BED_NO;UPLOAD_FLG;"
			+ "DRG_FLG;DIAG_CODE;DIAG_DESC2;DIAG_DESC;SOURCE_CODE;HOMEDIAG_DESC"; // �����޸ĵ�����
	// �ڶ���ҳǩ������ҳǩ
	private String pageTwo = "CONFIRM_NO;CASE_NO;YEAR_MON;REGION_CODE;BIRTH_DATE;ADM_SEQ;"
			+ "CONFIRM_SRC;HOSP_NHI_NO;INSBRANCH_CODE;CTZ1_CODE;ADM_CATEGORY;"
			+ "DEPT_DESC;PAT_CLASS;COMPANY_TYPE;SPECIAL_PAT_CODE;"
			+ "DEPT_CODE;BASEMED_BALANCE;INS_BALANCE;"
			+ "ADM_PRJ;SPEDRS_CODE;NHI_NUM;DS_DATE;"
			+ "STATUS;RECEIPT_USER;INS_UNIT;HOSP_CLS_CODE;INP_TIME;"
			+ "HOMEBED_TIME;HOMEBED_DAYS;TRANHOSP_RESTANDARD_AMT;TRANHOSP_DESC;TRAN_CLASS;"
			+ "SEX_CODE;UNIT_CODE;UNIT_DESC;PAT_AGE;NEWADM_SEQ;ADM_DAYS;"
			+ "REFUSE_TOTAL_AMT;AUDIT_TOTAL_AMT;NHI_PAY;NHI_COMMENT;OPT_USER;OPT_DATE;OPT_TERM;"
			+ "NHI_PAY_REAL;ACCOUNT_PAY_AMT;BASICMED_ADD_RATE;MEDAI_ADD_RATE;"
			+ "OVERFLOWLIMIT_ADD_RATE;BASICMED_ADD_AMT;MEDAI_ADD_AMT;OVERFLOWLIMIT_ADD_AMT;ARMYAI_AMT;"
			+ "PUBMANAI_AMT;TOT_PUBMANADD_AMT;PERSON_ACCOUNT_AMT;UNIT_DESC1;FP_NOTE;DS_SUMMARY;SINGLE_NHI_AMT;"
			+ "SINGLE_STANDARD_AMT;SINGLE_SUPPLYING_AMT;SINGLE_STANDARD_AMT_T;START_STANDARD_AMT";

	// ������ҳǩ
	private String pageThree = "PHA_AMT;PHA_OWN_AMT;PHA_ADD_AMT;"
			+ "PHA_NHI_AMT;EXM_AMT;EXM_OWN_AMT;EXM_ADD_AMT;EXM_NHI_AMT;TREAT_AMT;TREAT_OWN_AMT;TREAT_ADD_AMT;"
			+ "TREAT_NHI_AMT;OP_AMT;OP_OWN_AMT;OP_ADD_AMT;OP_NHI_AMT;BED_AMT;BED_OWN_AMT;BED_ADD_AMT;BED_NHI_AMT;"
			+ "MATERIAL_AMT;MATERIAL_OWN_AMT;MATERIAL_ADD_AMT;MATERIAL_NHI_AMT;OTHER_AMT;OTHER_OWN_AMT;"
			+ "OTHER_ADD_AMT;OTHER_NHI_AMT;BLOODALL_AMT;BLOODALL_OWN_AMT;BLOODALL_ADD_AMT;BLOODALL_NHI_AMT;"
			+ "BLOOD_AMT;BLOOD_OWN_AMT;BLOOD_ADD_AMT;BLOOD_NHI_AMT;OWN_RATE;DECREASE_RATE;REALOWN_RATE;"
			+ "INSOWN_RATE;RESTART_STANDARD_AMT;STARTPAY_OWN_AMT;OWN_AMT;PERCOPAYMENT_RATE_AMT;ADD_AMT;"
			+ "INS_HIGHLIMIT_AMT;APPLY_AMT;TRANBLOOD_OWN_AMT;HOSP_APPLY_AMT;"
			+ "TOT_ADD_AMT;TOT_NHI_AMT;SUM_TOT_AMT;TOT_AMT;TOT_OWN_AMT;"
			//������
			+ "QFBZ_AMT_S;TC_OWN_AMT_S;JZ_OWN_AMT_S;TX_OWN_AMT_S;ZGXE_AMT_S;TOTAL_AMT_S";
	private String singleName = "SPECIAL_PAT_CODE;COMPANY_TYPE;PAT_CLASS;PROGRESS;LBL_SPECIAL_PAT_CODE;LBL_COMPANY_TYPE;LBL_PAT_CLASS;LBL_PROGRESS"; // �����ֲ�������ʾ�Ŀؼ�
	// ������ҳǩ ������ҳ����
	private String mroRecordName = "CASE_NO1;MR_NO1;MARRIGE;OCCUPATION;FOLK;NATION;OFFICE;O_ADDRESS;O_TEL;O_POSTNO;"
			+ "H_ADDRESS;H_TEL;H_POSTNO;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL;"
			+ "IN_DEPT;IN_STATION;IN_ROOM_NO;TRANS_DEPT;OUT_DEPT;OUT_STATION;"
			+ "OUT_ROOM_NO;REAL_STAY_DAYS;OE_DIAG_CODE;IN_CONDITION;CONFIRM_DATE;"
			+ "OUT_DIAG_CODE1;CODE1_REMARK;CODE1_STATUS;OUT_DIAG_CODE2;CODE2_REMARK;CODE2_STATUS;"
			+ "OUT_DIAG_CODE3;CODE3_REMARK;CODE3_STATUS;OUT_DIAG_CODE4;CODE4_REMARK;CODE4_STATUS;"
			+ "OUT_DIAG_CODE5;CODE5_REMARK;CODE5_STATUS;OUT_DIAG_CODE6;CODE6_REMARK;CODE6_STATUS;"
			+ "INTE_DIAG_CODE;PATHOLOGY_DIAG;PATHOLOGY_REMARK;EX_RSN;ALLEGIC;HBSAG;HCV_AB;HIV_AB;"
			+ "QUYCHK_OI;QUYCHK_INOUT;QUYCHK_OPBFAF;QUYCHK_CLPA;QUYCHK_RAPA;GET_TIMES;SUCCESS_TIMES;"
			+ "DIRECTOR_DR_CODE;PROF_DR_CODE;ATTEND_DR_CODE;VS_DR_CODE;VS_DR_CODE1;INDUCATION_DR_CODE;"
			+ "GRADUATE_INTERN_CODE;INTERN_DR_CODE;ENCODER;QUALITY;CTRL_DR;CTRL_NURSE;CTRL_DATE;"
			+ "INFECT_REPORT;OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL;HEAL_LV;DIS_REPORT;BODY_CHECK;"
			+ "FIRST_CASE;ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE;SAMPLE_FLG;"
			+ "BLOOD_TYPE;RH_TYPE;TRANS_REACTION;RBC;PLATE;PLASMA;WHOLE_BLOOD;OTH_BLOOD;STATUS;"
			+ "PG_OWNER;DRPG_OWNER;FNALPG_OWNER;ADMCHK_FLG;DIAGCHK_FLG;BILCHK_FLG;QTYCHK_FLG;"
			+ "IN_COUNT;HOMEPLACE_CODE;MRO_CHAT_FLG;ADDITIONAL_CODE1;ADDITIONAL_CODE2;ADDITIONAL_CODE3;"
			+ "ADDITIONAL_CODE4;ADDITIONAL_CODE5;ADDITIONAL_CODE6;OE_DIAG_CODE2;OE_DIAG_CODE3;"
			+ "INTE_DIAG_STATUS;DISEASES_CODE;TEST_EMR;TEACH_EMR;IN_DIAG_CODE;INS_DR_CODE;"
			+ "CLNCPATH_CODE;REGION_CODE;TYPERESULT;SUMSCODE;OUT_ICD_DESC1;OUT_ICD_DESC2;OUT_ICD_DESC3;"
			+ "OUT_ICD_DESC4;OUT_ICD_DESC5;OUT_ICD_CODE1;OUT_ICD_CODE2;OUT_ICD_CODE3;OUT_ICD_CODE4;OUT_ICD_CODE5";
	// ������ҳǩ�б��水ť����
	private String pageSix = "L_TIMES;M_TIMES;S_TIMES";
	// ͷ��
	private String pageHead = "CONFIRM_NO;CASE_NO;MR_NO;PAT_NAME";
	// �����������ʾ
	private String[] nameAmt = { "_AMT", "_OWN_AMT", "_ADD_AMT", "_NHI_AMT" };
	private String[] nameType = { "PHA", "EXM", "TREAT", "OP", "BED",
			"MATERIAL", "OTHER", "BLOODALL", "BLOOD" }; // �շѽ������
	// ҽ���շѽ��
	private String[] insAmt = { "RESTART_STANDARD_AMT",
			"PERCOPAYMENT_RATE_AMT", "STARTPAY_OWN_AMT", "OWN_AMT",
			"TRANBLOOD_OWN_AMT", "ADD_AMT", "INS_HIGHLIMIT_AMT" };
	// ���÷ָ�ǰ�������
	private String[] pageFour = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	// ���÷ָ��������
	private String[] pageFive = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"DOSE_CODE", "STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE", "NHI_FEE_DESC",
			"OWN_PRICE", "CHARGE_DATE" };
	private TParm newParm; // ���÷ָ�������ݷ���������¼���ʹ��
	// �ۼƉ���һ���Բ���
	double addFee = 0.00;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initParm();
		// �������
		addListener(newTable);
	}

	/**
	 * ��ʼ������
	 */
	private void initParm() {
		type = (String) getParameter(); // TYPE: SINGLE ������
		tableInfo = (TTable) this.getComponent("TABLEINFO"); // ����������Ϣ�б�
		oldTable = (TTable) this.getComponent("OLD_TABLE"); // ���÷ָ�ǰ����
		newTable = (TTable) this.getComponent("NEW_TABLE"); // ���÷ָ������
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // ҳǩ
		this.setValue("START_DATE", SystemTool.getInstance().getDate()); // ��Ժ��ʼʱ��
		this.setValue("END_DATE", SystemTool.getInstance().getDate()); // ��Ժ����ʱ��
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
		isEnable(pageTwo + ";" + pageThree + ";" + mroRecordName, false);
		//��Ժ����
		callFunction("UI|DS_DATE|setEnabled",true);
		// ֻ��text���������������ICD10������
		callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");

		// textfield���ܻش�ֵ
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// �����ֲ�������
		if (null != type && type.equals("SINGLE")) {
			String[] singles = singleName.split(";");
			this.setTitle("�����ַ��÷ָ�");
			for (int i = 0; i < singles.length; i++) {
				callFunction("UI|" + singles[i] + "|setVisible", false);
			}
			callFunction("UI|tPanel_6|setVisible", false);
			callFunction("UI|tPanel_6|setEnabled", false);
			callFunction("UI|tPanel_13|setVisible", true);
			callFunction("UI|tPanel_13|setEnabled", true);
		} else {
			// ������ҳҳǩ����ʾ������ť
			callFunction("UI|OP_BTN|setVisible", false);
			callFunction("UI|MRO_BTN|setVisible", false);
		}

	}

	/**
	 * ���÷ָ�����а�ť�û�
	 * 
	 * @param enAble
	 *            boolean
	 */
	private void feePartitionEnable(boolean enAble) {
		callFunction("UI|save|setEnabled", enAble);
		callFunction("UI|new|setEnabled", enAble);
		callFunction("UI|delete|setEnabled", enAble);
		callFunction("UI|query|setEnabled", enAble);
		callFunction("UI|changeInfo|setEnabled", enAble);
		callFunction("UI|apply|setEnabled", enAble);
		callFunction("UI|onSave|setEnabled", enAble);
		for (int i = 1; i < 11; i++) {
			callFunction("UI|NEW_RDO_" + i + "|setEnabled", enAble);
		}
	}

	/**
	 * �ۼ�����
	 */
	private void update1() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// System.out.println("�ۼƉ������"+parm);
		String insAdmSql = " SELECT ADM_SEQ FROM INS_ADM_CONFIRM WHERE CONFIRM_NO = '"
				+ parm.getValue("CONFIRM_NO") + "' ";
		TParm insAdmParm = new TParm(TJDODBTool.getInstance().select(insAdmSql));
		// ҽ������˳���
		String admSeq = insAdmParm.getValue("ADM_SEQ", 0);
		String upLoadSql = " SELECT SUM (A.TOTAL_AMT) AS TOTAL_AMT, SUM (A.ADDPAY_AMT) AS ADDPAY_AMT,"
				+ "        SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"
				+ "        MAX (CHARGE_DATE) AS CHARGE_DATE "
				+ "   FROM INS_IBS_UPLOAD A "
				+ "  WHERE ADM_SEQ = '"
				+ admSeq
				+ "' "
				+ "    AND A.NHI_ORDER_CODE NOT LIKE '***%' "
				+ "    AND A.ADDPAY_FLG = 'Y' ";
		// ��ְ ���� ��ѯ������Ӧ�����ۼ�����ΪY������
		// TParm result =
		// INSIbsUpLoadTool.getInstance().querySumIbsUpLoad(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(upLoadSql));
		if (result.getErrCode() < 0) {
			return;
		}
		addFee = result.getDouble("TOTAL_AMT", 0);
		TParm splitParm = new TParm();
		TParm splitCParm = new TParm();
		splitParm.setData("ADDPAY_ADD", result.getDouble("TOTAL_AMT", 0));
		//��ʼʱ��
		String startDate = parm.getValue("START_DATE");
		//System.out.println("startDate:"+startDate.length());
		if(startDate.length() > 8)
			startDate =startDate.substring(0,8); 
		splitParm.setData("HOSP_START_DATE", startDate);
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.��ְ 2.�Ǿ�
			// System.out.println("��ְ�������"+splitCParm);
			// ��ְ�ۼ�����
			splitCParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
			// System.out.println("��ְ��������"+splitCParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			// System.out.println("�ǾӉ������"+splitCParm);
			// �Ǿ� סԺ�ۼ���������
			splitCParm = INSTJTool.getInstance().DataDown_sp1_H(splitParm);
			// System.out.println("�ǾӉ�������"+splitCParm);
		}
		if (!INSTJTool.getInstance().getErrParm(splitCParm)) {
			this.messageBox(splitCParm.getErrText());
			return;
		}
		TParm exeParm = new TParm();
		exeParm.setData("NHI_AMT", splitCParm.getDouble("NHI_AMT")); // �걨���
		exeParm.setData("TOTAL_AMT", result.getDouble("TOTAL_AMT", 0)); // �������
		exeParm.setData("TOTAL_NHI_AMT", splitCParm.getDouble("NHI_AMT")); // ҽ�����
		exeParm.setData("ADD_AMT", splitCParm.getDouble("ADDPAY_AMT")); // �ۼ��������
		exeParm.setData("ADDPAY_AMT", splitCParm.getDouble("ADDPAY_AMT")); // �ۼ��������
		exeParm.setData("OWN_AMT", splitCParm.getDouble("OWN_AMT")); // �Էѽ��
		exeParm.setData("CASE_NO", parm.getValue("CASE_NO")); // �������
		exeParm.setData("REGION_CODE", Operator.getRegion()); // ����
		// ��ѯ���SEQ_NO
		TParm maxSeqParm = INSIbsUpLoadTool.getInstance().queryMaxIbsUpLoad(
				parm);
		if (maxSeqParm.getErrCode() < 0) {
			return;
		}
		exeParm.setData("SEQ_NO", maxSeqParm.getInt("SEQ_NO", 0) + 1); // ˳���
		exeParm.setData("DOSE_CODE", ""); // ����
		exeParm.setData("STANDARD", ""); // ���
		exeParm.setData("PRICE", 0); // ����
		exeParm.setData("QTY", 0); // ����
		exeParm.setData("ADM_SEQ", maxSeqParm.getValue("ADM_SEQ", 0)); // ҽ�������
		exeParm.setData("OPT_USER", Operator.getID()); // ID
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("HYGIENE_TRADE_CODE", ""); // ��׼�ĺ�
		exeParm.setData("ORDER_CODE", "***018"); // ҽ������
		exeParm.setData("NHI_ORDER_CODE", "***018"); // ҽ��ҽ������
		exeParm.setData("ORDER_DESC", "һ���Բ����ۼ�����");
		exeParm.setData("ADDPAY_FLG", "Y"); // �ۼ�������־��Y���ۼ�������N�����ۼ�������
		exeParm.setData("PHAADD_FLG", "N"); // ����ҩƷ
		exeParm.setData("CARRY_FLG", "N"); // ��Ժ��ҩ
		exeParm.setData("OPT_TERM", Operator.getIP()); //
		exeParm.setData("NHI_ORD_CLASS_CODE", "06"); // ͳ�ƴ���
		exeParm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("CHARGE_DATE", 0), true)); // ��ϸ¼��ʱ��
		exeParm.setData("YEAR_MON", parm.getValue("YEAR_MON")); // �ں�
		result = TIOM_AppServer.executeAction("action.ins.INSBalanceAction",
				"onAdd", exeParm);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ���ۼ�����ʧ��");
			return;
		}
	}

	/**
	 * ׼���ϴ�ҽ�� �ǾӲ�����Ҫ�ж��Ƿ� ȡ��ҽ���Ƿ��Ƕ�ͯ��ҩ���ͯ������Ŀ
	 * 
	 * �����ֲ��� INS_IBS�޸Ĵ�λ���������ҽ�ò��Ϸ�������
	 */
	private void updateRun() {
		TParm commParm = getTableSeleted();
		if (null == commParm) {

			return;
		}
		TParm parmValue = newTable.getParmValue(); // ��÷��÷ָ��������
		double bedFee = regionParm.getDouble("TOP_BEDFEE", 0);
		boolean flg = false; // �����Ϣ��ܿ� �ж��Ƿ�ָ�ɹ�
		TParm tableParm = null;
		TParm newParm = new TParm(); // �ۼ�����
		// TParm ctzParm = null;
		TParm tempParm = new TParm();
		if (null == nhiCode || nhiCode.length() <= 0) {
			String sql = "SELECT CTZ1_CODE FROM INS_IBS WHERE YEAR_MON='"
					+ commParm.getValue("YEAR_MON") + "' AND CASE_NO='"
					+ this.getValueString("CASE_NO") + "'";
			tempParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (tempParm.getErrCode() < 0) {
				this.messageBox("��ò���ҽ�����ʧ��");
				return;
			}
			if (tempParm.getCount("CTZ1_CODE") <= 0) {
				this.messageBox("û���ҵ�����ҽ�����");
				return;
			}
			nhiCode = tempParm.getValue("CTZ1_CODE", 0);
		}
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			// �ۼ���������ʱ�����ݿ�����һ��ҽ��Ϊ***018������
			if ("***018".equals(nhiOrderCode) || nhiOrderCode.equals("")) { // ҽ������
				continue;
			}
			if (nhiOrderCode.length() > 4) {
				String billdate = tableParm.getValue("CHARGE_DATE").replace(
						"/", ""); // ��ϸ������ʱ��
				TParm parm = new TParm();
				
				parm.setData("CTZ1_CODE", nhiCode); // ���
				parm.setData("QTY", tableParm.getValue("QTY")); // ����
				parm.setData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT")); // �ܽ��
				parm.setData("TIPTOP_BED_AMT", bedFee); // ��ߴ�λ��
				parm.setData("PHAADD_FLG", null != tableParm
						.getValue("PHAADD_FLG")
						&& tableParm.getValue("PHAADD_FLG").equals("Y") ? "1"
						: "0"); // ҩƷ����ע��
				parm.setData("FULL_OWN_FLG", null != tableParm
						.getValue("FULL_OWN_FLG")
						&& tableParm.getValue("FULL_OWN_FLG").equals("Y") ? "0"
						: "1"); // ȫ�Էѱ�־
				parm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // ҽ���������
				parm.setData("NHI_ORDER_CODE", nhiOrderCode);//ҽ����
				parm.setData("CHARGE_DATE", billdate); // ���÷���ʱ��
				TParm splitParm = new TParm();		
				//pangben 2012-9-6
				if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.��ְ 2.�Ǿ�
					// System.out.println("��ְҽ���ָ�ǰ�������"+parm);
					// סԺ������ϸ�ָ�
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
					// סԺ������ϸ�ָ�
					splitParm = INSTJTool.getInstance().DataDown_sp1_G(parm);
				}
				if (!INSTJTool.getInstance().getErrParm(splitParm)) {
					flg = true;
					this.messageBox(parmValue.getValue("SEQ_NO", i) + "��ʧ��");
					break;
				}
				// �ۼ����ݲ���
				setIbsUpLoadParm(tableParm, splitParm, newParm);
			} else {
				this.messageBox("����" + parmValue.getValue("SEQ_NO", i)
						+ "��ҽ������"); // ���
			}

		}
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("TYPE", type); // �ж�ִ������ ��SINGLE:�����ֲ���
		newParm.setData("CASE_NO", commParm.getValue("CASE_NO")); // �����ֲ���ʹ��
		newParm.setData("YEAR_MON", commParm.getValue("YEAR_MON")); // �ںŵ����ֲ���ʹ��
		// ִ���޸�INS_IBS_UPLOAD�����
		// System.out.println("ִ���޸�INS_IBS_UPLOAD��������"+newParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onSaveInsUpLoad", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			this.messageBox("�ָ�ʧ��");
		} else {
			this.messageBox("�ָ�ɹ�");
		}
	}

	/**
	 * ���÷ָ� �ۼ����� ���INS_IBS_UPLOAD �����
	 * 
	 * @param tableParm
	 *            TParm
	 * @param splitParm
	 *            TParm
	 * @param newParm
	 *            TParm
	 */
	private void setIbsUpLoadParm(TParm tableParm, TParm splitParm,
			TParm newParm) {
		newParm.addData("ADM_SEQ", tableParm.getValue("ADM_SEQ")); // ����˳���
		newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO")); // ���
		newParm.addData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				tableParm.getValue("CHARGE_DATE"), true)); // ��ϸ������ʱ��
		newParm.addData("ADDPAY_AMT", splitParm.getValue("ADDPAY_AMT")); // �������
		newParm.addData("TOTAL_NHI_AMT", splitParm.getValue("NHI_AMT")); // �걨���
		newParm.addData("OWN_AMT", splitParm.getValue("OWN_AMT")); // ȫ�Էѽ��
		newParm.addData("OWN_RATE", splitParm.getValue("OWN_RATE")); // �Ը�����
		newParm.addData("NHI_ORD_CLASS_CODE", splitParm
				.getValue("NHI_ORD_CLASS_CODE")); // ͳ�ƴ���
		newParm.addData("ADDPAY_FLG", null != splitParm.getValue("ADDPAY_FLG")
				&& splitParm.getValue("ADDPAY_FLG").equals("1") ? "Y" : "N"); // �ۼ�������־

	}

	/**
	 * ��ñ�����
	 * 
	 * @param name
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String name) {
		return (TTable) this.getComponent(name);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "��Ժ��ʼʱ�䲻����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "��Ժ����ʱ�䲻����Ϊ��");
			return;
		}

		if (((Timestamp) this.getValue("START_DATE")).after(((Timestamp) this
				.getValue("END_DATE")))) {
			this.messageBox("��ʼʱ�䲻���Դ��ڽ���ʱ��");
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		// 21.��ְ��ͨ 22.��ְ���� 23.�Ǿ����� SYS_CTZ ����ҽ������
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // ��ְ
			parm.setData("INS_CROWD_TYPE", "'11','12','13'");
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) { // �Ǿ�
			parm.setData("INS_CROWD_TYPE", "'21','22','23'");
		}
		if (null != this.getValue("MR_NO")
				&& this.getValue("MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("MR_NO"));
		}
		//==============pangben 2012-6-18 start
		if (null != this.getValue("CASE_NO")
				&& this.getValue("CASE_NO").toString().length() > 0) {
			parm.setData("CASE_NO", this.getValue("CASE_NO"));
		}
		//==============pangben 2012-6-18 stop
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		parm.setData("START_DATE", df.format(this.getValue("START_DATE"))); // ��Ժʱ��
		parm.setData("END_DATE", df.format(this.getValue("END_DATE"))); // ��Ժ����ʱ��
		parm.setData("TYPE", type); // TYPE:SINGLE �����ֲ���
		TParm result = INSADMConfirmTool.getInstance().INS_Adm_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}

	/**
	 * У��Ϊ�շ���
	 * 
	 * @param name
	 *            String
	 * @param message
	 *            String
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}

	/**
	 * ת������������
	 */
	public void onQueryInfo() {
		onExe("M");
	}

	/**
	 * ת�걨
	 */
	public void onApply() {
		//===========pangben 2012-7-18 start û�г�Ժ���ڲ�����ִ��ת�걨����
		if (null==this.getValue("DS_DATE") || this.getValue("DS_DATE").toString().length()<=0) {
			this.messageBox("û�л�ó�Ժ���ڲ�����ִ��");
			return;
		}
		//===========pangben 2012-7-18 stop
		onExe("H");
	}

	/**
	 * ִ��ת����ת�걨����
	 * 
	 * @param type
	 *            ��M :ת������Ϣ���� ,H :ת�걨���� A : �Զ���
	 */
	private void onExe(String type) {
		TParm parm = getTableSeleted();
		//System.out.println("parm:"+parm);
		if (null == parm) {
			return;
		}
		parm.setData("TYPE", type); // M :ת������Ϣ���� ,H :ת�걨���� A : �Զ�
		parm.setData("REGION_CODE", Operator.getRegion()); // ҽԺ����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String endDate = "";
		String tempDate = df1.format(SystemTool.getInstance().getDate());
		
		// �ж��Ƿ����������� ��ý���ʱ��
		String startDate = parm.getValue("START_DATE");
		if(startDate.length() > 8)
			startDate =startDate.substring(0,8); 
		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) {
			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231";
		} else {
			endDate = df.format(SystemTool.getInstance().getDate());
		}
		parm.setData("END_DATE", endDate); // ϵͳʱ��
		parm.setData("START_DATE", startDate); // ϵͳʱ��
		//System.out.println("parm:"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExe", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��:"+result.getErrText());
			return;
		} 
		String Msg = "ת�����\n" + "�ɹ�����:" + result.getValue("SUCCESS_INDEX")
				+ "\n" + "ʧ�ܱ���:" + result.getValue("ERROR_INDEX");
		this.messageBox(Msg);
		if ("M".equals(type)) {
			this.setValueForParm(pageHead + ";" + pageTwo + ";" + showValue+";REALOWN_RATE",
					result.getRow(0));//pangben 2013-4-1���ʵ��֧������,�ǾӲ��˽������ʧ�ܣ�֧����������ȷ����ֵ����
			int days = StringTool.getDateDiffer((Timestamp) this
					.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
			int rollDate = days == 0 ? 1 : days;
			this.setValue("ADM_DAYS", rollDate);
			this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
			tabbedPane.setSelectedIndex(1);
		}
	}

	/**
	 * ��ô����
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	private String getDiagDesc(String caseNo) {
		String sql = "SELECT ICD_CODE,ICD_DESC AS ICD_CHN_DESC FROM MRO_RECORD_DIAG  WHERE CASE_NO='"
				+ caseNo + "' AND IO_TYPE='O' AND MAIN_FLG='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		String diagDesc = "";
		for (int i = 0; i < result.getCount(); i++) {
			diagDesc += result.getValue("ICD_CHN_DESC", i) + ",";
		}
		if (diagDesc.length() > 0) {
			diagDesc = diagDesc.substring(0, diagDesc.lastIndexOf(","));
		}
		return diagDesc;
	}

	/**
	 * ������ҳǩ���ܽ�����ݸ�ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void getTotAmtValue(TParm result) {
		// ���úϼ�
		for (int i = 0; i < nameAmt.length; i++) {
			double sum = 0.00;
			for (int j = 0; j < nameType.length; j++) {
				sum += result.getRow(0).getDouble(nameType[j] + nameAmt[i]);
				this.setValue("TOT" + nameAmt[i], sum);
			}
		}
		double sum = 0.00;
		// ҽ�����ϼ�
		for (int i = 0; i < insAmt.length; i++) {
			sum += this.getValueDouble(insAmt[i]);
		}
		this.setValue("SUM_TOT_AMT", sum); // �ܼ�
	}

	/**
	 * ���÷ָ�ִ�в���
	 */
	public void onUpdate() {
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		String sql = "SELECT SOURCE_CODE,ADM_PRJ FROM INS_IBS WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND YEAR_MON='"
				+ parm.getValue("YEAR_MON") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ����������");
			return;
		}
		if (result.getCount("SOURCE_CODE") <= 0
				|| null == result.getValue("SOURCE_CODE", 0)
				|| result.getValue("SOURCE_CODE", 0).length() <= 0) {

			this.messageBox("��Ժ���û�б���,������ִ�зָ����");
			tabbedPane.setSelectedIndex(1);
			return;
		}
		if (result.getCount("ADM_PRJ") <= 0
				|| null == result.getValue("ADM_PRJ", 0)
				|| result.getValue("ADM_PRJ", 0).length() <= 0) {
			this.messageBox("��ҽ��Ŀû�б���,��ִ��ת��������");
			tabbedPane.setSelectedIndex(1);
			return;
		}
		if (!this.CheckTotAmt()) {
		} else {
			feePartitionEnable(false);
			updateRun(); // ׼���ϴ�ҽ��
			update1(); // �ۼ�����
			feePartitionEnable(true);
		}

	}

	/**
	 * ���÷ָ�ִ���Ժ����ݱȽ�
	 * 
	 * @return boolean
	 */
	public boolean CheckTotAmt() {
			TParm parm = getTableSeleted();
			if (null != parm) {
				 String sql =" SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT" +
	     		" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B" +
	     		" WHERE A.ADM_SEQ = B.ADM_SEQ" +
	     		" AND B.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	     		" AND A.NHI_ORDER_CODE  NOT LIKE '***%'";       
		TParm ibsUpLoadParm = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("ibsUpLoadParm===" + ibsUpLoadParm);
		if (ibsUpLoadParm.getErrCode() < 0) {
			return false;
		}
		String sql1 =" SELECT SUM(TOT_AMT) AS TOT_AMT" +
				     " FROM IBS_ORDD" +
				     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";
		TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (ibsOrddParm.getErrCode() < 0) {
			return false;
		}
//		System.out.println("ibsOrddParm===" + ibsOrddParm);
		if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) != ibsOrddParm
				.getDouble("TOT_AMT", 0)){
			messageBox("���÷ָ�����������");
			return false; 
		} else {
			return true; 
		}
	
//			TParm ibsUpLoadParm = INSIbsUpLoadTool.getInstance()
//					.queryCheckSumIbsUpLoad(parm);		
//			if (ibsUpLoadParm.getErrCode() < 0) {
//				return false;
//			}
//			TParm ibsOrderParm = INSIbsOrderTool.getInstance()
//					.queryCheckSumIbsOrder(parm);
//			if (ibsOrderParm.getErrCode() < 0) {
//				return false;
//			}
//			if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) == ibsOrderParm
//					.getDouble("TOTAL_AMT", 0)) {
//				return true;
//			} else {
//				if (this.messageBox("��Ϣ", "�˲����շѽ����ָ�����,�Ƿ����", 2) == 0) {
//					return true;
//				}
//				return false;
//			}
		}
		return true;
	}

	private void onSplitOld(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				// this.messageBox("û�в�ѯ������");
				return;
			}
		} else {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				return;
			}
		}
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������
		double totalNhiAmt = 0.00; // �걨���
		double ownAmt = 0.00; // �Էѽ��
		double addPayAmt = 0.00; // �������
		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
			totalNhiAmt += result.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += result.getDouble("OWN_AMT", i);
			addPayAmt += result.getDouble("ADDPAY_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFour.length; i++) {
			if (i == 0) {
				result.addData(pageFour[i], "�ϼ�:");
				continue;
			}
			result.addData(pageFour[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.addData("TOTAL_NHI_AMT", totalNhiAmt);
		result.addData("OWN_AMT", ownAmt);
		result.addData("ADDPAY_AMT", addPayAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
		this.setValue("SUM_AMT", totalAmt); // ����ܽ��
	}

	/**
	 * ���÷ָ�ǰ����
	 */
	public void onSplitOld() {
		onSplitOld(true);

	}

	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // �ں�
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // �������
		parm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // �ʸ�ȷ������
		parm.setData("START_DATE", parm.getValue("IN_DATE").replace("/", "")); // ��ʼʱ��
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("PAT_AGE", parm.getValue("PAT_AGE")); // ����
		return parm;
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {

		switch (tabbedPane.getSelectedIndex()) {
		// 3 :���÷ָ�ǰҳǩ 4�����÷ָ��ҳǩ
		case 3:
			onSplitOld();
			break;
		case 4:
			onSplitNew();
			break;
		}
	}

	/**
	 * ���÷ָ������
	 */
	public void onSplitNew() {
		onSplitNew(true);
	}

	private void onSplitNew(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"NEW_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm upLoadParmOne = INSIbsUpLoadTool.getInstance()
				.queryNewSplit(parm);
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				// this.messageBox("û�в�ѯ������");
				callFunction("UI|upload|setEnabled", false); // û�����ݲ�����ִ�зָ����
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false); // û�����ݲ�����ִ�зָ����
				return;
			}
		}

		if (null == upLoadParmOne) {
			upLoadParmOne = new TParm();
		}
		// �ϲ�����
		if (upLoadParmTwo.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < upLoadParmTwo.getCount(); i++) {
				upLoadParmOne.setRowData(upLoadParmOne.getCount() + 1,
						upLoadParmTwo, i);
			}
			upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		}
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������
		double totalNhiAmt = 0.00; // �걨���
		double ownAmt = 0.00; // �Էѽ��
		double addPayAmt = 0.00; // �������
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			totalNhiAmt += upLoadParmOne.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += upLoadParmOne.getDouble("OWN_AMT", i);
			addPayAmt += upLoadParmOne.getDouble("ADDPAY_AMT", i);
			if (upLoadParmOne.getValue("ORDER_CODE", i).equals("***018")) { // �ϴ�ҽ���������ۼƽ��
				continue;
			}
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFive.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pageFive[i], "�ϼ�:");
				continue;
			}
			upLoadParmOne.addData(pageFive[i], "");
		}
		upLoadParmOne.addData("QTY", 0);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("TOTAL_NHI_AMT", totalNhiAmt);
		upLoadParmOne.addData("OWN_AMT", ownAmt);
		upLoadParmOne.addData("ADDPAY_AMT", addPayAmt);
		upLoadParmOne.addData("ADM_SEQ", ""); // ����˳��� ����
		upLoadParmOne.addData("FLG", ""); // ��������
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", ""); // ����׼��
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// ��Ӻϼ�
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totalAmt); // �ܽ����ʾ
		callFunction("UI|upload|setEnabled", true);
	}

	/**
	 * ������Ϣ��񵥻��¼�
	 */
	public void onTableClick() {
		onSplitOld(false);
		onSplitNew(false);
		int row = tableInfo.getSelectedRow();
		TParm parm = tableInfo.getParmValue().getRow(row);
		this.setValueForParm(pageHead, parm);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // �ں�
		TParm result = INSIbsTool.getInstance().queryIbsSum(parm); // ��ѯ���ݸ����渳ֵ
		nhiCode = result.getValue("NHI_CODE", 0);
		setSumValue(result, parm);
	}

	/**
	 * �������ı���س��¼�
	 */
	public void onMrNo() {
		// TParm parm = getTableSeleted();
		// if (null == parm) {
		// return;
		// }
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("MR_NO", pat.getMrNo());
		TParm parm = new TParm();
		//=============pangben 2012-6-18 start ���סԺ��ϢУ�� �ж�case_NO
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MR_NO,CASE_NO FROM ADM_INP WHERE CANCEL_FLG = 'N' ");
		String temp = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
			temp = " AND  REGION_CODE='" + Operator.getRegion() + "'";
		}
		parm.setData("MR_NO", pat.getMrNo());
		sql.append(" AND MR_NO='" + pat.getMrNo() + "'" + temp);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount()<=0) {
			this.messageBox("�˲���û��סԺ��Ϣ");
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
			this.setValue("CASE_NO", "");
			return;
		}
		parm.setData("FLG","Y");
		if (result.getCount("MR_NO") > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
			this.setValue("CASE_NO", result.getValue("CASE_NO"));
		} else {
			this.setValue("CASE_NO", result.getValue("CASE_NO", 0));
		}
		//=============pangben 2012-6-18 STOP
		// TParm result = INSIbsTool.getInstance().queryIbsSum(parm);//
		// ��ѯ���ݸ����渳ֵ
		// setSumValue(result, parm);
	}

	/**
	 * ���÷ָ����ϸ���ݱ������
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		parm.setData("OPT_USER", Operator.getID()); // id
		parm.setData("OPT_TERM", Operator.getIP()); // Ip
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		// ִ�����INS_IBS_UPLOAD�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoad", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew(false);
		}
	}

	/**
	 * ���÷ָ����ϸ�����½�����
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT", "TOTAL_NHI_AMT",
				"OWN_AMT", "ADDPAY_AMT" };
		TParm parm = newTable.getParmValue();
		TParm result = new TParm();
		// ���һ��������
		for (int i = 0; i < pageFive.length; i++) {
			result.setData(pageFive[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}

		result.setData("FLG", "Y"); // ��������
		if (parm.getCount() > 0) {
			// ��úϼ�����
			result.setData("ADM_SEQ", parm.getValue("ADM_SEQ", 0)); // ����˳��� ����
			result.setData("HYGIENE_TRADE_CODE", parm.getValue(
					"HYGIENE_TRADE_CODE", 0)); // ����׼��
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // �Ƴ��ϼ�
			int seqNo = -1; // ������˳�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // ˳���
			parm.setRowData(parm.getCount(), result, -1); // ����½�������
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // ���ϼ����·���
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("û�����ݲ������½�����");
			return;
			// result.setData("ADM_SEQ",parm.getValue("ADM_SEQ",0));//����˳��� ����
			// parm.setRowData(parm.getCount(),result,-1);
			// parm.setCount(parm.getCount()+1);
		}
		newTable.setParmValue(parm);
	}

	/**
	 * ���÷ָ����ϸ����ɾ������
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("������ɾ���ϼ�����");
			return;
		}
		TParm result = INSIbsUpLoadTool.getInstance().deleteINSIbsUploadSeq(
				parm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		this.messageBox("P0005"); // ִ�гɹ�
		onSplitNew(false);
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
		selectNewRow = row;
		// �����ǰ�к�
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// ҽ�� �� ��������
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("QTY".equalsIgnoreCase(columnName)) { // �����ϼ�����

			// TNumberTextField numberField= (TNumberTextField) com;

			newParm = newTable.getParmValue();
			double sum = newParm.getDouble("QTY", selectNewRow)
					* newParm.getDouble("PRICE", selectNewRow);
			newParm.setData("TOTAL_AMT", selectNewRow, sum); // �������
			// numberField.setValue(sum);
			newTable.setParmValue(newParm);
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // ������ CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}

	}

	/**
	 * ���¸�ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		newTable.acceptText();
		TParm newParm = newTable.getParmValue();
		newParm
				.setData("ORDER_CODE", selectNewRow, parm
						.getValue("ORDER_CODE")); // ҽ����
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // ҽ������
		newParm.setData("NHI_FEE_DESC", selectNewRow, parm
				.getValue("NHI_FEE_DESC")); // ҽ������
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // ����
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // ҽ�����ô���
		newTable.setParmValue(newParm);
	}

	/**
	 * �������
	 */
	public void onSettlement() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		if (null == this.getValue("DS_DATE")
				|| this.getValue("DS_DATE").toString().length() <= 0) {
			this.messageBox("��Ժʱ�䲻����Ϊ��");
			this.grabFocus("DS_DATE");
			return;
		}
		// �����ֲ���У������
		if (null != type && type.equals("SINGLE")) {

			if (null == this.getValue("ADM_DAYS")
					|| this.getValue("ADM_DAYS").toString().length() <= 0) {
				this.messageBox("סԺ��������Ϊ��");
				this.grabFocus("ADM_DAYS");
				tabbedPane.setSelectedIndex(1);
				return;
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // ����
		parm.setData("REALOWN_RATE", this.getValue("REALOWN_RATE")); // ����ʵ���Ը�����
		parm.setData("INS_CROWD_TYPE", this.getValueInt("INS_CROWD_TYPE")); // ��Ⱥ���
		parm.setData("TYPE", type); // type:SINGLE �����ֲ���ʹ��

		parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("DS_DATE"), true)); // ��Ժʱ�� ������

		parm.setData("ADM_DAYS", this.getValueInt("ADM_DAYS")); // סԺ����
		String[] name = showValue.split(";"); // ������޸����ݻ��
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValue(name[i])); // �����޸ĵ�����
		}
		parm.setData("CHEMICAL_DESC", this.getText("CHEMICAL_DESC")); // ����˵��
		parm.setData("ADDAMT", addFee);
		// System.out.println("�������parm:::::"+parm);
		// �������
		TParm result = new TParm(INSTJAdm.getInstance().onSettlement(
				parm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		this.messageBox("P0005"); // ִ�гɹ�
		result = INSIbsTool.getInstance().queryIbsSum(parm); // ��ѯ���ݸ����渳ֵ
		setSumValue(result, parm);
		// isEnable(pageThree, false);
		tabbedPane.setSelectedIndex(2);
	}

	/**
	 * �������ݸ�ֵ
	 * 
	 * @param result
	 *            TParm
	 * @param parm
	 *            TParm
	 */
	private void setSumValue(TParm result, TParm parm) {
		this.setValueForParm(pageTwo + ";" + pageThree + ";" + showValue,
				result.getRow(0));
		this.setText("CHEMICAL_DESC", result.getValue("CHEMICAL_DESC", 0)); // ����֤��
		int days = StringTool.getDateDiffer((Timestamp) this
				.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
		int rollDate = days == 0 ? 1 : days;
		this.setValue("ADM_DAYS", rollDate);
		this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
		// �����ֲ���ִ��
		if (null != type && type.equals("SINGLE")) {
			//������Ϣ
			TParm mroParm = MRORecordTool.getInstance().getInHospInfo(parm);
			//��Ժ�����Ϣ
			parm.setData("IO_TYPE","O");
			TParm outDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//��Ժ��Ϣ
			parm.setData("IO_TYPE","M");
			TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//�ż������
			parm.setData("IO_TYPE","I");
			TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			
			//Ժ����Ϣ
			
			setValueForParm(mroRecordName, mroParm.getRow(0));
			setValueForParm(pageSix, result.getRow(0));
			
			//ʵ���𸶱�׼��� (�����ָ���û���𸶱�׼��)
			setValue("QFBZ_AMT_S", "0.00");
			//ͳ������Ը���׼���
			setValue("TC_OWN_AMT_S", result.getRow(0).getDouble("STARTPAY_OWN_AMT"));
			//ҽ�ƾ����Ը���׼���
			setValue("JZ_OWN_AMT_S", result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT"));
			//�����Էѽ��
			double txAmt  =  result.getRow(0).getDouble("BED_SINGLE_AMT")+result.getRow(0).getDouble("MATERIAL_SINGLE_AMT");
			
			setValue("TX_OWN_AMT_S", txAmt);
			//����޶����Ͻ��
			setValue("ZGXE_AMT_S", result.getRow(0).getDouble("INS_HIGHLIMIT_AMT"));
			//�ϼ�
			double totAmt = txAmt + result.getRow(0).getDouble("INS_HIGHLIMIT_AMT")
                            + result.getRow(0).getDouble("STARTPAY_OWN_AMT")
                            + result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT");
			
			setValue("TOTAL_AMT_S", totAmt);
			
			//�״β��̼�¼
			setValue("FP_NOTE", result.getRow(0).getValue("FP_NOTE")); 
			//��ԺС��
            setValue("DS_SUMMARY", result.getRow(0).getValue("DS_SUMMARY")); 
            //סԺҽʦ
            setValue("VS_DR_CODE1", mroParm.getRow(0).getValue("VS_DR_CODE")); 
            //��Ժ���
            for (int i = 0; i < outDiagParm.getCount(); i++) 
            { 	
              //��Ժ���
              String icdCode = "" + outDiagParm.getData("ICD_CODE", i);
			  String icdDesc = "" + outDiagParm.getData("ICD_DESC", i);
			  String icdStatus =  "" +  outDiagParm.getData("ICD_STATUS", i);
			  setValue("OUT_ICD_CODE"+(i+1), icdCode);
			  setValue("OUT_ICD_DESC"+(i+1), icdDesc);
			  setValue("ADDITIONAL_CODE"+(i+1), icdStatus);
			}
            //�ż������
            String oeDiag = "";
            for(int i = 0; i<oeDiagParm.getCount();i++)
            {
            	oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));	
            }
            setValue("OE_DIAG_CODE", oeDiag);
            // ��Ժ���
    		String inDiag = "";
    		for (int i = 0; i < inDiagParm.getCount(); i++) 
    		{
           	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
    		}           
    		setValue("IN_DIAG_CODE", inDiag);
		}
		getTotAmtValue(result);
	}

	/**
	 * ���
	 */
	public void onClear() {
		// isEnable(pageThree, true);
		// ͷ��
		clearValue(pageHead + ";INS_CROWD_TYPE");
		// ҳǩ
		clearValue(pageTwo + ";" + pageThree
				+ ";CHEMICAL_DESC;FP_NOTE;DS_SUMMARY;" + showValue
				+ ";" + mroRecordName + ";" + pageSix);
		// �Ƴ�����
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // ��һ��ҳǩ
		clearValue("SUM_AMT;NEW_SUM_AMT");
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
	}

	/**
	 * ִ�б༭״̬
	 * 
	 * @param name
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void isEnable(String name, boolean flg) {
		String[] pageName = name.split(";");
		for (int i = 0; i < pageName.length; i++) {
			callFunction("UI|" + pageName[i] + "|setEnabled", flg);
		}
	}

	/**
	 * �ڶ���ҳǩ�������
	 */
	public void onSaveIbs() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String[] ibsName = showValue.split(";");
		for (int i = 0; i < ibsName.length; i++) {
			parm.setData(ibsName[i], this.getValue(ibsName[i]));
		}
		// ============pangben ȥ���س���
		String chemical = this.getText("CHEMICAL_DESC");
		parm.setData("CHEMICAL_DESC", chemical.replace("\n", "")); // ����˵��
		parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("DS_DATE"), true));
		// System.out.println("parmparmparm:::"+parm);
		TParm result = INSIbsTool.getInstance().updateIbsOther(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	}

	/**
	 * ������ ������¼��ѯ����
	 */
	public void onOp() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSOperator.x", parm);
	}

	/**
	 * �����ַ��÷ָ� ������ҳ �б������
	 */
	public void onMroSave() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String[] name = pageSix.split(";");
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValueInt(name[i]));
		}
		parm.setData("FP_NOTE", this.getText("FP_NOTE"));
		parm.setData("DS_SUMMARY", this.getText("DS_SUMMARY"));
		TParm restult = INSIbsTool.getInstance().updateInsIbsMro(parm);
		if (restult.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	}

	/**
	 * ������� Ϊ��ʱ ��������Ĳ���ʾ
	 */
	public void onDiagLost() {
		if (this.getValueString("DIAG_CODE").trim().length() <= 0) {
			this.setValue("DIAG_DESC", "");
		}
	}

	/**
	 * ����¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			this.setValue("DIAG_CODE", "");
			this.setValue("DIAG_DESC", "");
		} else {
			this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
			this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
		}
	}

	boolean sortClicked = false;

	/**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = newTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = newTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		newTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
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
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * ͨ��ҽ�����β�ѯ������Ա������
	 * 
	 * @param crowType
	 *            int
	 * @param exeParm
	 *            TParm
	 * @return String
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("CTZ_CODE");
			sql = " AND CTZ_CODE IN('11','12','13')";
		} else if (crowType == 2) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('21','22','23')";
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_CODE,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_CODE", 0);
	}

}
