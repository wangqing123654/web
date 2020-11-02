package com.javahis.ui.bms;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.bms.BMSBloodTool;
import jdo.bms.BMSSQL;
import jdo.bms.BMSTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatMedLisResult;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��Ѫ����
 * </p>
 * 
 * <p>
 * Description: ��Ѫ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSApplyControl extends TControl {

	// �ⲿ���ô���
	private TParm parm;

	private String from_flg;

	private String apply_no;
	
	private String sign;//add by sunqy 20140819 �����Ƿ�����д"��Ѫ֪��ͬ����"��ֵ

	// �ż�ס��
	private String adm_type;

	private String mr_no;

	private Timestamp use_date;

	private String dept_code;

	private String dr_code;

	private String icd_code_1 = "";

	private String icd_desc_1 = "";

	private String icd_code_2 = "";

	private String icd_desc_2 = "";

	private String icd_code_3 = "";

	private String icd_desc_3 = "";

	private String case_no;

	private String ipd_no;

	private String action = "insert";

	// ��½����
	private String language = "";

	public BMSApplyControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			from_flg = parm.getValue("FROM_FLG");
			if ("1".equals(from_flg)) {
				// �½���Ѫ��
				adm_type = parm.getValue("ADM_TYPE");
				mr_no = parm.getValue("MR_NO");
				use_date = parm.getTimestamp("USE_DATE");
				dept_code = parm.getValue("DEPT_CODE");
				dr_code = parm.getValue("DR_CODE");
				icd_code_1 = parm.getValue("ICD_CODE");
				icd_desc_1 = parm.getValue("ICD_DESC");
				case_no = parm.getValue("CASE_NO");
			} else if ("2".equals(from_flg)) {
				// ���ڱ�Ѫ��
				apply_no = parm.getValue("APPLY_NO");
				sign = parm.getValue("SIGN");//add by sunqy 20140819 �Ƿ�����д"��Ѫ֪��ͬ����"
			}
			this.getTextField("MR_NO").setEnabled(false);
		} else {
			from_flg = "";
			this.getTextField("MR_NO").setEnabled(true);
		}

		// ��ʼ����������
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		if (!checkData()) {
			return;
		}

		TParm inparm = new TParm();
		inparm.setData("ADM_TYPE", adm_type);
		inparm.setData("CASE_NO", this.getValue("CASE_NO"));
		inparm.setData("MR_NO", this.getValue("MR_NO"));
		inparm.setData("IPD_NO", this.getValue("IPD_NO"));
		inparm.setData("APPLY_TYPE", "");
		inparm.setData("PRE_DATE", this.getValue("PRE_DATE"));
		inparm.setData("END_DAYS", this.getValue("END_DAYS"));
		inparm.setData("USE_DATE", this.getValue("USE_DATE"));
		//
		inparm.setData("HBSAG", this.getValueString("HBSAG"));
		inparm.setData("ANTI_HCV", this.getValueString("ANTI_HCV"));
		inparm.setData("ANTI_HIV", this.getValueString("ANTI_HIV"));
		inparm.setData("SY", this.getValueString("SY"));
		inparm.setData("RBC", this.getValueString("RBC"));
		inparm.setData("HB", this.getValueString("HB"));
		inparm.setData("HCT", this.getValueString("HCT"));
		inparm.setData("WBC", this.getValueString("WBC"));
		inparm.setData("PLT", this.getValueString("PLT"));
		inparm.setData("RPR", this.getValueString("RPR"));//add by sunqy 20140805
		//
		inparm.setData("URG_FLG",
				this.getRadioButton("URG_FLG_Y").isSelected() ? "Y" : "N");
		inparm.setData("BED_NO", this.getValue("BED_NO"));
		inparm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		inparm.setData("DR_CODE", this.getValue("DR_CODE"));
		inparm.setData("TRANRSN_CODE1", this.getValue("TRANRSN_CODE1"));
		inparm.setData("TRANRSN_CODE2", this.getValue("TRANRSN_CODE2"));
		inparm.setData("TRANRSN_CODE3", this.getValue("TRANRSN_CODE3"));
		inparm.setData("DIAG_CODE1", icd_code_1);
		inparm.setData("DIAG_CODE2", icd_code_2);
		inparm.setData("DIAG_CODE3", icd_code_3);
		inparm.setData("CLS_FLG", "N");
		inparm.setData("TEST_BLD", this.getValue("TEST_BLD"));
		inparm.setData("BLD_SIFT_FLG", "N");
		inparm.setData("SIFT_FLG", "N");
		inparm.setData("APPLY_USER", Operator.getID());
		Timestamp date = SystemTool.getInstance().getDate();
		inparm.setData("APPLY_DATE", date);
		// =====================shibl add 20140626
		// ����������============================================
		inparm.setData("TRANS_HISTORY", this.getValueString("TRANS_HISTORY"));// ��Ѫ����ʷ
		inparm.setData("PAT_OTH", this.getValueString("PAT_OTH"));// ����
		inparm.setData("PREGNANCY", this.getValueString("PREGNANCY"));// ��
		inparm.setData("INFANT", this.getValueString("INFANT"));// ��
		inparm.setData("ALT", this.getValueString("ALT"));
		inparm.setData("ANTI_HBS", this.getValueString("ANTI_HBS"));
		inparm.setData("HBEAG", this.getValueString("HBEAG"));
		inparm.setData("ANTI_HBE", this.getValueString("ANTI_HBE"));
		inparm.setData("ANTI_HBC", this.getValueString("ANTI_HBC"));
		inparm.setData("BLOOD_TEST", this.getValueString("BLOOD_TEST"));// ������
		inparm.setData("BLOOD_AIM", this.getValueString("BLOOD_AIM"));// ��ѪĿ��
		// ================================end
		// ====================================================================
		
		//ADD BY YANGJJ 20151014
		inparm.setData("TRANS_ADVERSE",this.getValueString("TRANS_ADVERSE"));//��Ѫ������Ӧ
		inparm.setData("MED_ALLERGY",this.getValueString("MED_ALLERGY"));//ҩ�����ʷ
		inparm.setData("PREGNANT_TIMES",this.getValueString("PREGNANT_TIMES"));//���д���
		inparm.setData("BORN_TIMES",this.getValueString("BORN_TIMES"));//��������
		inparm.setData("ABORTION_TIMES",this.getValueString("ABORTION_TIMES"));//��������
		inparm.setData("SERVIVAL_NO",this.getValueString("SERVIVAL_NO"));//�����
		
		
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("REMARK", this.getValue("REMARK"));
		this.getTable("TABLE").acceptText();
		TParm parmD = new TParm();
		for (int i = 0; i < this.getTable("TABLE").getRowCount(); i++) {
			parmD.addData("BLD_CODE", this.getTable("TABLE").getItemData(i,
					"BLD_CODE"));
			parmD.addData("APPLY_QTY", this.getTable("TABLE").getItemData(i,
					"APPLY_QTY"));
			parmD.addData("UNIT_CODE", this.getTable("TABLE").getItemString(i,
					"UNIT_CODE"));
			
			//add by yangjj 20150323��������ѪҺ��Ѫ�ͺ�RH������
			parmD.addData("APPLY_BLD", this.getTable("TABLE").getItemData(i,
					"APPLY_BLD"));
			parmD.addData("APPLY_RH_TYPE", this.getTable("TABLE").getItemData(i,
					"APPLY_RH_TYPE"));
			
			//add by yangjj 20150523���ӷ�������
			parmD.addData("IRRADIATION", this.getTable("TABLE").getItemData(i, "IRRADIATION"));
		}
		
		inparm.setData("BMS_APPLYD", parmD.getData());
		TCheckBox sign = (TCheckBox)getComponent("SIGN");//add by sunqy 20140819 ������������д����Ѫ֪��ͬ���顱
		inparm.setData("SIGN", sign.isSelected() ? "Y" : "N");
		System.out.println("control inparm:"+inparm);
		TParm result = new TParm();
		if ("insert".equals(action)) {
			apply_no = SystemTool.getInstance().getNo("ALL", "BMS",
					"BMS_APPLY", "No");
			inparm.setData("APPLY_NO", apply_no);
			result = TIOM_AppServer.executeAction("action.bms.BMSApplyAction",
					"onInsertBMSApply", inparm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
			action = "update";
			this.setValue("APPLY_NO", apply_no);
			onPrint();
		} else {
			inparm.setData("APPLY_NO", this.getValue("APPLY_NO"));

			result = TIOM_AppServer.executeAction("action.bms.BMSApplyAction",
					"onUpdateBMSApply", inparm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
			action = "update";
			// this.setValue("APPLY_NO", apply_no);
		}

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if ("".equals(this.getValueString("APPLY_NO"))) {
			this.messageBox("E0135");
			return;
		}
		TParm parm = new TParm();
		parm.setData("APPLY_NO", this.getValueString("APPLY_NO"));
		TParm result = BMSTool.getInstance().onQueryBMSApply(parm);
		// System.out.println("result--" + result);
		TParm resultM = result.getParm("BMS_APPLYM");
		TParm resultD = result.getParm("BMS_APPLYD");
		if (resultM == null || resultM.getCount("APPLY_NO") == 0
				|| resultM.getCount() == 0) {
			this.messageBox("E0116");
			return;
		}
		// ������Ϣ
		adm_type = resultM.getValue("ADM_TYPE", 0);
		if ("O".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "����");
			} else {
				this.setValue("ADM_TYPE", "O");
			}
		} else if ("E".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "����");
			} else {
				this.setValue("ADM_TYPE", "E");
			}
		} else if ("I".equals(adm_type)) {
			if (!"en".equals(language)) {
				this.setValue("ADM_TYPE", "סԺ");
			} else {
				this.setValue("ADM_TYPE", "I");
			}
		}

		this.setValue("MR_NO", resultM.getData("MR_NO", 0));
		this.setValue("IPD_NO", resultM.getData("IPD_NO", 0));
		this.setValue("CASE_NO", resultM.getData("CASE_NO", 0));
		this.setValue("PRE_DATE", resultM.getData("PRE_DATE", 0));
		this.setValue("END_DAYS", resultM.getData("END_DAYS", 0));
		this.setValue("USE_DATE", resultM.getData("USE_DATE", 0));
		//
		this.setValue("HBSAG", resultM.getData("HBSAG", 0));
		this.setValue("ANTI_HCV", resultM.getData("ANTI_HCV", 0));
		this.setValue("ANTI_HIV", resultM.getData("ANTI_HIV", 0));
		this.setValue("SY", resultM.getData("SY", 0));
		this.setValue("RBC", resultM.getData("RBC", 0));
		this.setValue("HB", resultM.getData("HB", 0));
		this.setValue("HCT", resultM.getData("HCT", 0));
		this.setValue("WBC", resultM.getData("WBC", 0));
		this.setValue("PLT", resultM.getData("PLT", 0));
		this.setValue("RPR", resultM.getData("RPR", 0));//add by sunqy 20140805
		//
		if ("Y".equals(resultM.getValue("URG_FLG", 0))) {
			this.getRadioButton("URG_FLG_Y").setSelected(true);
		} else {
			this.getRadioButton("URG_FLG_N").setSelected(true);
		}
		this.setValue("DEPT_CODE", resultM.getData("DEPT_CODE", 0));
		this.setValue("BED_NO", resultM.getData("BED_NO", 0));
		this.setValue("DR_CODE", resultM.getData("DR_CODE", 0));
		this.setValue("TRANRSN_CODE1", resultM.getData("TRANRSN_CODE1", 0));
		this.setValue("TRANRSN_CODE2", resultM.getData("TRANRSN_CODE2", 0));
		this.setValue("TRANRSN_CODE3", resultM.getData("TRANRSN_CODE3", 0));

		if ("".equals(resultM.getValue("DIAG_CODE1", 0))) {
			this.setValue("DIAG_CODE1", "");
		} else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSIcdByCode(resultM.getValue("DIAG_CODE1", 0))));
			this.setValue("DIAG_CODE1", icdParm.getValue("ICD_CHN_DESC", 0));
		}
		if ("".equals(resultM.getValue("DIAG_CODE2", 0))) {
			this.setValue("DIAG_CODE2", "");
		} else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSIcdByCode(resultM.getValue("DIAG_CODE2", 0))));
			this.setValue("DIAG_CODE2", icdParm.getValue("ICD_CHN_DESC", 0));
		}
		if ("".equals(resultM.getValue("DIAG_CODE3", 0))) {
			this.setValue("DIAG_CODE3", "");
		} else {
			TParm icdParm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSIcdByCode(resultM.getValue("DIAG_CODE3", 0))));
			this.setValue("DIAG_CODE3", icdParm.getValue("ICD_CHN_DESC", 0));
		}

		this.setValue("REMARK", resultM.getData("REMARK", 0));

		Pat pat = Pat.onQueryByMrNo(resultM.getValue("MR_NO", 0));
		if (!"en".equals(language)) {
			this.setValue("PAT_NAME", pat.getName());
		} else {
			this.setValue("PAT_NAME", pat.getName1());
		}
		this.setValue("SEX_CODE", pat.getSexCode());
		Timestamp date = SystemTool.getInstance().getDate();
		if (!"en".equals(language)) {
			this.setValue("AGE", DateUtil.showAge(pat.getBirthday(), date));
		} else {
			String[] args = StringTool.CountAgeByTimestamp(pat.getBirthday(),
					date);
			this.setValue("AGE", args[0] + "Y");
		}
		this.setValue("IDNO", pat.getIdNo());

		TParm parmPat = new TParm();
		parmPat.setData("MR_NO", resultM.getData("MR_NO", 0));
		parmPat.setData("IO_TYPE", adm_type);
		TParm resultPat = BMSTool.getInstance().onQueryPat(parmPat);
		this.setValue("CTZ1_CODE", resultPat.getValue("CTZ1_CODE", 0));
		if (resultPat.getValue("BLOOD_RH_TYPE", 0).equals("+")) {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
		} else if (resultPat.getValue("BLOOD_RH_TYPE", 0).equals("-")) {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
		} else {
			this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
			this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
		}
		this.setValue("TEST_BLD", resultPat.getValue("BLOOD_TYPE", 0));
		this.setValue("DEPT", resultPat.getValue("DEPT_CODE", 0));
		String bed_no = resultPat.getValue("BED_NO", 0);
		if (!"".equals(bed_no)) {
			TParm bedParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '"
							+ bed_no + "'"));
			this.setValue("STATION_CODE", bedParm.getValue("STATION_CODE", 0));
		}

		// =====================shibl add 20140626
		// ����������============================================
		this.setValue("TRANS_HISTORY", resultM.getValue("TRANS_HISTORY", 0));// ��Ѫ����ʷ
		this.setValue("PAT_OTH", resultM.getValue("PAT_OTH", 0));// ����
		this.setValue("PREGNANCY", resultM.getValue("PREGNANCY", 0));// ��
		this.setValue("INFANT", resultM.getValue("INFANT", 0));// ��
		this.setValue("ALT", resultM.getValue("ALT", 0));
		this.setValue("ANTI_HBS", resultM.getValue("ANTI_HBS", 0));
		this.setValue("HBEAG", resultM.getValue("HBEAG", 0));
		this.setValue("ANTI_HBE", resultM.getValue("ANTI_HBE", 0));
		this.setValue("ANTI_HBC", resultM.getValue("ANTI_HBC", 0));
		this.setValue("BLOOD_TEST", resultM.getValue("BLOOD_TEST", 0));// ������
		this.setValue("BLOOD_AIM", resultM.getValue("BLOOD_AIM", 0));// ��ѪĿ��
		// ================================end
		// ====================================================================

		
		//ADD BY YANGJJ 20151014
		this.setValue("TRANS_ADVERSE",resultM.getValue("TRANS_ADVERSE",0));//��Ѫ������Ӧ
		this.setValue("MED_ALLERGY",resultM.getValue("MED_ALLERGY",0));//ҩ�����ʷ
		this.setValue("PREGNANT_TIMES",resultM.getValue("PREGNANT_TIMES",0));//���д���
		this.setValue("BORN_TIMES",resultM.getValue("BORN_TIMES",0));//��������
		this.setValue("ABORTION_TIMES",resultM.getValue("ABORTION_TIMES",0));//��������
		this.setValue("SERVIVAL_NO",resultM.getValue("SERVIVAL_NO",0));//�����
		
		
		
		// ϸ����Ϣ
		TTable table = this.getTable("TABLE");
		if (table.getRowCount() > 0)
			table.removeRowAll();
		for (int i = 0; i < resultD.getCount("BLD_CODE"); i++) {
			int row = table.addRow();
			// BLD_CODE;APPLY_QTY;UNIT_CODE
			table.setItem(row, "BLD_CODE", resultD.getData("BLD_CODE", i));
			table.setItem(row, "APPLY_QTY", resultD.getData("APPLY_QTY", i));
			table.setItem(row, "UNIT_CODE", resultD.getData("UNIT_CODE", i));
			
			//add by yangjj 20150323 ��������ѪҺ��Ѫ�ͺ�RH
			table.setItem(row, "APPLY_BLD", resultD.getData("APPLY_BLD", i));
			table.setItem(row, "APPLY_RH_TYPE", resultD.getData("APPLY_RH_TYPE", i));
			
			//add by yangjj 20150522���ӷ���
			table.setItem(row, "IRRADIATION", resultD.getData("IRRADIATION", i));
		}
		onShowTakeNO();
		action = "update";
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		if (!"".equals(this.getValueString("APPLY_NO"))
				&& "update".equals(action)) {
			TParm parm = new TParm();
			parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
			TParm result = BMSBloodTool.getInstance().onQuery(parm);
			if (result.getCount() > 0) {
				this.messageBox("E0136");
				return;
			}

			result = TIOM_AppServer.executeAction("action.bms.BMSApplyAction",
					"onDeleteBMSApply", parm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0003");
				return;
			}
			this.messageBox("P0003");
			this.onClear();
		} else {
			this.messageBox("E0116");
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		action = "insert";
		ClearPatInfo();
		ClearApplyMInfo();
		ClearApplyDInfo();
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		if ("".equals(this.getValueString("APPLY_NO"))) {
			this.messageBox("E0137");
			return;
		}
		TParm parmApply = new TParm(TJDODBTool.getInstance().select(
				BMSSQL.getBMSApplyPrtData(this.getValueString("APPLY_NO"))));
		if (parmApply.getCount() <= 0) {
			this.messageBox("E0137");
			return;
		}
		Map map = this.initMap();
		// ��ӡ����
		TParm date = new TParm();
		date.setData("TITLE", "TEXT", "�ٴ���Ѫ�������뵥");
		date.setData("PRE_DATE", "TEXT", "��������:"
				+ this.getValueString("PRE_DATE").substring(0, 19).replaceAll(
						"-", "/"));
		date.setData("USE_DATE", "TEXT", "Ԥ����Ѫ����:"
				+ this.getValueString("USE_DATE").substring(0, 19).replaceAll(
						"-", "/"));
		date.setData("APPLY_NO", "TEXT", "���뵥��:"
				+ this.getValueString("APPLY_NO"));
		date.setData("PAT_NAME", "TEXT", "��Ѫ������:"
				+ this.getValueString("PAT_NAME"));
		date.setData("SEX", "TEXT", "�Ա�:"
				+ getComboBox("SEX_CODE").getSelectedName());
		Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		date.setData("AGE", "TEXT", "����:"
				+ DateUtil.showAge(pat.getBirthday(), SystemTool.getInstance()
						.getDate()));
		date.setData("MR_NO", "TEXT", "������:" + this.getValueString("MR_NO"));
		date.setData("STATION_CODE", "TEXT", "����:"
				+ ((TTextFormat) this.getComponent("STATION_CODE")).getText());
		date.setData("IPD_NO", "TEXT", " סԺ��:" + this.getValueString("IPD_NO"));
		date.setData("DEPT_CODE", "TEXT", "�Ʊ�:"
				+ getComboBox("DEPT").getSelectedName());
		date.setData("BED_NO", "TEXT", "����:"
				+ getComboBox("BED_NO").getSelectedName());// duzhw add
		date.setData("DIAG_CODE", "TEXT", "�ٴ����:"
				+ getValueString("DIAG_CODE1") + " "
				+ getValueString("DIAG_CODE2") + " "
				+ getValueString("DIAG_CODE3"));
		date.setData("TRANRSN_CODE", "TEXT", "��Ѫԭ��:"
				+ getComboBox("TRANRSN_CODE1").getSelectedName() + " "
				+ getComboBox("TRANRSN_CODE2").getSelectedName() + " "
				+ getComboBox("TRANRSN_CODE3").getSelectedName());
		date.setData("REMARK", "TEXT", "��ע:" + getValueString("REMARK"));
		date.setData("TEST_BLD", "TEXT", "ABOѪ��:"
				+ getComboBox("TEST_BLD").getSelectedName());
		date.setData("RH_TYPE", "TEXT", "Y"
				.equals(getValueString("BLOOD_RH_TYPE_A")) ? "RH(D):����"
				: "RH(D):����");
		//
		date.setData("HBSAG", "TEXT", "HBSAG:"
				+ (getValueString("HBSAG").equals("") ? "-" : map
						.get(getValueString("HBSAG"))));
		date.setData("ANTI_HCV", "TEXT", "Anti-HCV��"
				+ (getValueString("ANTI_HCV").equals("") ? "-" : map
						.get(getValueString("ANTI_HCV"))));
		date.setData("ANTI_HIV", "TEXT", "Anti-HIV��"
				+ (getValueString("ANTI_HIV").equals("") ? "-" : map
						.get(getValueString("ANTI_HIV"))));
		date.setData("SY", "TEXT", "s-TP��"
				+ (getValueString("SY").equals("") ? "-" : map
						.get(getValueString("SY"))));
		date.setData("RPR", "TEXT", "RPR��"
				+ (getValueString("RPR").equals("") ? "-" : map
						.get(getValueString("RPR"))));//add by sunqy 20140805
		date.setData("RBC", "TEXT", "RBC:" + (("".equals(getValueDouble("RBC")) || getValueDouble("RBC")==0) ? "-" : getValueDouble("RBC"))
				+ " ��10^12/L");
		date.setData("HB", "TEXT", "HB:" + (("".equals(getValueDouble("HB")) || getValueDouble("HB")==0) ? "-" : getValueDouble("HB")) + " g/L");
		date.setData("HCT", "TEXT", "HCT:" + (("".equals(getValueDouble("HCT")) || getValueDouble("HCT")==0) ? "-" : getValueDouble("HCT")) + " %");
		date.setData("WBC", "TEXT", "WBC:" + (("".equals(getValueDouble("WBC")) || getValueDouble("WBC")==0) ? "-" : getValueDouble("WBC"))
						+ " ��10^9/L");
		date.setData("PLT", "TEXT", "PLT:" + (("".equals(getValueDouble("PLT")) || getValueDouble("PLT")==0) ? "-" : getValueDouble("PLT"))
						+ " ��10^9/L");
		date.setData("BAR_CODE", "TEXT", getValueString("APPLY_NO"));
		
		
		//ADD BY YANGJJ 20151015
		String strTransAdverse = "";
		strTransAdverse = "��Ѫ������Ӧ��"+getValueString("TRANS_ADVERSE");
		date.setData("TRANS_ADVERSE", "TEXT", strTransAdverse);
		
		String strBorn = "����ʷ������"
							+getValueString("PREGNANT_TIMES")+"�Σ�����"
							+getValueString("BORN_TIMES")+"�Σ�����"
							+getValueString("ABORTION_TIMES")+"�Σ����"
							+getValueString("SERVIVAL_NO")+"��";
		date.setData("BORN", "TEXT", strBorn);
		
		String strMedAllergy = "ҩ�����ʷ��"+getValueString("MED_ALLERGY");
		date.setData("MED_ALLERGY", "TEXT", strMedAllergy);
		
		
		
		
		// �������
		TParm parm = new TParm();
		TTable table = this.getTable("TABLE");
		String bldCode = "";//add by sunqy 20140805
		String boolean_code = "";
		for (int i = 0; i < table.getRowCount(); i++) {
			boolean_code = table.getItemString(i, "BLD_CODE");
			bldCode += boolean_code + "','";//add by sunqy 20140805
			TParm inparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSBldCodeInfo(boolean_code)));
			/*TParm unitparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSUnit(boolean_code))); // shil modify 2014/03/31*/
			
			//modify by yangjj 20150324
			TParm unitparm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSUnitFromBLDCODE(boolean_code)));
			
			if (inparm == null || inparm.getErrCode() < 0) {
				this.messageBox("E0034");
				return;
			}
			
			parm.addData("BLDCODE_DESC", inparm.getValue("BLDCODE_DESC", 0));
			
			//add by yangjj
			parm.addData("APPLY_BLD", table.getItemString(i, "APPLY_BLD"));
			
			
			
			String rh1 = table.getItemString(i, "APPLY_RH_TYPE");
			String r = "";
			if("+".equals(rh1)){
				r = "����";
			}else if("-".equals(rh1)){
				r = "����";
			}
			parm.addData("APPLY_RH_TYPE",r);
			
			//add by yangjj 20150522
			String str = table.getItemString(i, "IRRADIATION");
			String irr = "";
			if("Y".equals(str)){
				irr = "��";
			}else if("N".equals(str)){
				irr = "��";
			}
			parm.addData("IRRADIATION",irr);
			
			//add by yangjj 20150710
			double appQty = table.getItemDouble(i, "APPLY_QTY");
			if(appQty % 1.0 == 0){
				parm.addData("QTY", (long)appQty);
			}else{
				parm.addData("QTY", appQty);
			}
			
			//parm.addData("QTY", table.getItemDouble(i, "APPLY_QTY"));
			parm.addData("UNIT", unitparm.getValue("UNIT_CHN_DESC", 0));
		}
		//add by sunqy 20140805=============
		bldCode = bldCode.substring(0, bldCode.length()-3);
		TParm inparmExcept = new TParm(TJDODBTool.getInstance().select(BMSSQL.getBMSBldCodeInfoExcept(bldCode)));
		for (int i = 0; i < inparmExcept.getCount(); i++) {
			
			
			parm.addData("BLDCODE_DESC", inparmExcept.getValue("BLDCODE_DESC", i));
			
			//add by yangjj
			parm.addData("APPLY_BLD", "");
			parm.addData("APPLY_RH_TYPE","");
			parm.addData("IRRADIATION","");
			
			parm.addData("QTY", 0);
			parm.addData("UNIT", inparmExcept.getValue("UNIT_CHN_DESC", i));
		}
		//add by sunqy 20140805==============
//		// �������
//		TParm parm = new TParm();
//		TTable table = this.getTable("TABLE");
//		String boolean_code = "";
//		for (int i = 0; i < table.getRowCount(); i++) {
//			boolean_code = table.getItemString(i, "BLD_CODE");
//			TParm inparm = new TParm(TJDODBTool.getInstance().select(
//					BMSSQL.getBMSBldCodeInfo(boolean_code)));
//			TParm unitparm = new TParm(TJDODBTool.getInstance().select(
//					BMSSQL.getBMSUnit(boolean_code))); // shil modify 2014/03/31
//			if (inparm == null || inparm.getErrCode() < 0) {
//				this.messageBox("E0034");
//				return;
//			}
//			parm.addData("BLDCODE_DESC", inparm.getValue("BLDCODE_DESC", 0));
//			parm.addData("QTY", table.getItemDouble(i, "APPLY_QTY"));
//			parm.addData("UNIT", unitparm.getValue("UNIT_CHN_DESC", 0));
//		}
		parm.setCount(parm.getCount("BLDCODE_DESC"));
		
		parm.addData("SYSTEM", "COLUMNS", "BLDCODE_DESC");
		parm.addData("SYSTEM", "COLUMNS", "APPLY_BLD");
		parm.addData("SYSTEM", "COLUMNS", "APPLY_RH_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT");
		
		//add by yangjj 20150522
		parm.addData("SYSTEM", "COLUMNS", "IRRADIATION");

		date.setData("TABLE", parm.getData());

		// =========================shibl add
		// 20140627=======================================
		date.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", getValueString("MR_NO"));
		date.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",getValueString("IPD_NO"));
		date.setData("filePatName", "TEXT", getValueString("PAT_NAME"));
		date.setData("fileSex", "TEXT", getComboBox("SEX_CODE")
				.getSelectedName());
		date.setData("fileBirthday", "TEXT", StringTool.getString(pat
				.getBirthday(), "yyyy/MM/dd"));
		TRadioButton urgFlg = (TRadioButton)getComponent("URG_FLG_Y");//add by sunqy 20140808�ж���Ѫ����
		date.setData("URG_FLG", "TEXT", "��Ѫ����:"
				+ (urgFlg.isSelected() ? "����" : "����"));
		date.setData("ID_NO", "TEXT", "��Ѫ�ߣ���ĸ�����֤����:" + pat.getIdNo());
		date.setData("TRANS_HISTORY", "TEXT", "������Ѫʷ:"
				+ ((TTextFormat) this.getComponent("TRANS_HISTORY")).getText());
		date.setData("BLOOD_AIM", "TEXT", "��ѪĿ��:"
				+ ((TTextFormat) this.getComponent("BLOOD_AIM")).getText());
		date.setData("DR_CODE", "TEXT", "����ҽʦ:"
				+ ((TTextFormat) this.getComponent("DR_CODE")).getText());
		date.setData("BLOOD_TEST", "TEXT", "������:"
				+ getValueString("BLOOD_TEST"));
		date.setData("ALT", "TEXT", "ALT:" + (("".equals(getValueDouble("ALT")) || getValueDouble("RBC")==0) ? "-" : getValueDouble("ALT")) + " U/L");
		date.setData("ANTI_HBS", "TEXT", "Anti-HBs��"
				+ (getValueString("ANTI_HBS").equals("") ? "-" : map
						.get(getValueString("ANTI_HBS"))));
		date.setData("HBEAG", "TEXT", "HbeAg�� "
				+ (getValueString("HBEAG").equals("") ? "-" : map
						.get(getValueString("HBEAG"))));
		date.setData("ANTI_HBE", "TEXT", "Anti-HBe��"
				+ (getValueString("ANTI_HBE").equals("") ? "-" : map
						.get(getValueString("ANTI_HBE"))));
		date.setData("ANTI_HBC", "TEXT", "Anti-HBc��"
				+ (getValueString("ANTI_HBC").equals("") ? "-" : map
						.get(getValueString("ANTI_HBC"))));
		TCheckBox sign = (TCheckBox)this.getComponent("SIGN");
		if(sign.isSelected()){
			date.setData("SIGN", "TEXT", "��");//add by sunqy 20140819 ������������д����Ѫ֪��ͬ���顱��
		}else{
			date.setData("SIGN", "TEXT", "");
		}

		
		// ���ô�ӡ����
		this.openPrintDialog("%ROOT%\\config\\prt\\BMS\\ApplyNo_V45.jhw", date);
		// this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\ApplyNo.jhw", date);
	}

	/**
	 * ��ѯ������Ϣ
	 */
	public void onQueryPatInfo() {
		mr_no = this.getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (pat == null) {
			this.messageBox("E0116");
			ClearPatInfo();
			return;
		}
		mr_no = pat.getMrNo();
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		if (!"".equals(adm_type)) {
			parm.setData("IO_TYPE", adm_type);
		}
		TParm result = BMSTool.getInstance().onQueryPat(parm);
		if (result == null || result.getCount("ADM_TYPE") <= 0) {
			this.messageBox("E0116");
			ClearPatInfo();
			return;
		} else {
			result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSQueryPat.x",
					parm);
			if (result == null || result.getCount("ADM_TYPE") == 0) {
				this.messageBox("E0116");
				ClearPatInfo();
				return;
			}
			adm_type = result.getValue("ADM_TYPE");
			mr_no = result.getValue("MR_NO");
			pat = Pat.onQueryByMrNo(mr_no);
			this.setValue("MR_NO", mr_no);
			ipd_no = pat.getIpdNo();
			this.setValue("IPD_NO", ipd_no);
			case_no = result.getValue("CASE_NO");
			this.setValue("CASE_NO", case_no);
			if (!"en".equals(language)) {
				this.setValue("PAT_NAME", pat.getName());
			} else {
				this.setValue("PAT_NAME", pat.getName1());
			}
			this.setValue("SEX_CODE", pat.getSexCode());
			Timestamp date = SystemTool.getInstance().getDate();
			if (!"en".equals(language)) {
				this.setValue("AGE", DateUtil.showAge(pat.getBirthday(), date));
			} else {
				String[] args = StringTool.CountAgeByTimestamp(pat
						.getBirthday(), date);
				this.setValue("AGE", args[0] + "Y");
			}
			this.setValue("IDNO", pat.getIdNo());
			this.setValue("CTZ1_CODE", pat.getCtz1Code());
			if (pat.getBloodRHType().equals("+")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			} else if (pat.getBloodRHType().equals("-")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
			} else {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			}
			// ������Դ
			if ("O".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "����");
				} else {
					this.setValue("ADM_TYPE", "O");
				}
			} else if ("E".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "����");
				} else {
					this.setValue("ADM_TYPE", "E");
				}
			} else if ("I".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "סԺ");
				} else {
					this.setValue("ADM_TYPE", "I");
				}
			}
			this.setValue("DEPT", result.getValue("DEPT_CODE"));
			this.setValue("BED_NO", result.getValue("BED_NO"));

			this.setValue("TEST_BLD", pat.getBloodType());
			this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));

			String bed_no = result.getValue("BED_NO", 0);
			if (!"".equals(bed_no)) {
				TParm bedParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '"
								+ bed_no + "'"));
				this.setValue("STATION_CODE", bedParm.getValue("STATION_CODE",
						0));
			}

			this.onFilterDeptCode();
			((TComboBox) this.getComponent("HBSAG")).grabFocus();
		}
	}

	/**
	 * ������Ѫԭ��_1
	 */
	public void onFilterTranrsnCode1() {
		String tranrsn_code_1 = getValueString("TRANRSN_CODE1");
	}

	/**
	 * ������Ѫԭ��_2
	 */
	public void onFilterTranrsnCode2() {
		String tranrsn_code_1 = getValueString("TRANRSN_CODE1");
		String tranrsn_code_2 = getValueString("TRANRSN_CODE2");
	}

	/**
	 * ����ҽ��
	 */
	public void onFilterDeptCode() {
		String dept_code = this.getValueString("DEPT_CODE");
		TextFormatSYSOperator operator = (TextFormatSYSOperator) this
				.getComponent("DR_CODE");
		operator.setDept(dept_code);
		operator.onQuery();
	}

	/**
	 * ����ѪҺ����
	 */
	public void onAdd() {
		TTable table = this.getTable("TABLE");
		if (this.getComboBox("BL00D_CODE").getSelectedIndex() <= 0) {
			this.messageBox("E0138");
			return;
		}
		if (this.getValueDouble("APPLY_QTY") <= 0) {
			this.messageBox("E0139");
			return;
		}
		
		//add by yangjj ����Ѫ�ͺ�RH������Ϊ��
		TComboBox apply_bld = (TComboBox) this.getComponent("APPLY_BLOOD");
		TTextFormat apply_rh_type = (TTextFormat) this.getComponent("APPLY_RH");
		
		if ("".equals(apply_bld.getValue())){
			this.messageBox("��ѡ��Ѫ��");
			return;
		}
		System.out.println(apply_rh_type.getValue());
		if ("".equals(this.getValueString("APPLY_RH"))){
			this.messageBox("��ѡ��RHѪ��");
			return;
		}
		
		
		
		for (int i = 0; i < table.getRowCount(); i++) {
			if (getValueString("BL00D_CODE").equals(
					table.getItemString(i, "BLD_CODE"))) {
				this.messageBox("E0140");
				return;
			}
		}
		int row = table.addRow();
		table.setItem(row, "BLD_CODE", this.getComboBox("BL00D_CODE")
				.getValue());
		table.setItem(row, "APPLY_QTY", this.getValueDouble("APPLY_QTY"));
		table.setItem(row, "UNIT_CODE", this.getComboBox("UNIT_CODE")
				.getValue());
		
		//add by yangjj 20150323��������ѪҺ��Ѫ�ͺ�RH������
		table.setItem(row, "APPLY_BLD", this.getComboBox("APPLY_BLOOD")
				.getValue());
		table.setItem(row, "APPLY_RH_TYPE", ((TTextFormat)this.getComponent("APPLY_RH"))
				.getValue().toString());
		
		//add by yangjj 20150522���ӷ�������
		TCheckBox irr = (TCheckBox) this.getComponent("IRRADIATION");
		table.setItem(row, "IRRADIATION", irr.isSelected()?"Y":"N");
	}

	/**
	 * ɾ��ѪҺ����
	 */
	public void onRemove() {
		TTable table = this.getTable("TABLE");
		int row = table.getSelectedRow();
		if (row == -1) {
			this.messageBox("E0134");
		}
		// �������뵥
		if ("update".equals(action)) {
			// ��ɾ�����Ƿ�����Ѫ�����
			TParm parm = new TParm();
			parm.setData("APPLY_NO", this.getValue("APPLY_NO"));
			parm.setData("BLD_CODE", table.getItemData(row, "BLD_CODE"));
			TParm result = BMSBloodTool.getInstance().onQuery(parm);
			if (result.getCount() > 0) {
				this.messageBox("E0136");
				return;
			}
		}
		table.removeRow(row);
	}

	/**
	 * ѪҺ����ı��¼�
	 */
	public void onBloodCodeChange() {
		//modify by yangjj 20150324
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				BMSSQL.getBMSUnitFromBLDCODE(this.getComboBox("BL00D_CODE")
						.getSelectedID())));
		this.getComboBox("UNIT_CODE").setValue(parm.getValue("UNIT_CODE", 0));
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_1 = icd_code;
			icd_desc_1 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE1", icd_desc_1);
			} else {
				this.setValue("DIAG_CODE1", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_2 = icd_code;
			icd_desc_2 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE2", icd_desc_2);
			} else {
				this.setValue("DIAG_CODE2", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn3(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icd_code = parm.getValue("ICD_CODE");
		String icd_desc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icd_code)) {
			icd_code_3 = icd_code;
			icd_desc_3 = icd_desc;
			if (!"en".equals(language)) {
				this.setValue("DIAG_CODE3", icd_desc_3);
			} else {
				this.setValue("DIAG_CODE3", parm.getValue("ICD_ENG_DESC"));
			}
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		language = this.getLanguage();

		// ��ʼ��ѪҺ����
		TParm bldParm = new TParm(TJDODBTool.getInstance().select(
				BMSSQL.getBMSBloodCode()));
		this.getComboBox("BL00D_CODE").setParmValue(bldParm);

		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		TParm parm = new TParm();
		String icd_type = "W";
		parm.setData("ICD_TYPE", icd_type);
		if (icd_type.equals("W")) {
			parm.setData("ICD_EXCLUDE", "Y");
			parm.setData("ICD_MIN_EX", "M80000/0");
			parm.setData("ICD_MAX_EX", "M99890/1");
			parm.setData("ICD_START_EX", "V");
			parm.setData("ICD_END_EX", "Y");
			parm.setData("ICD_MIC_FLG", false);// add by wanglong 20140321
			// ������̬ѧ���ע��
		}
		getTextField("DIAG_CODE1").onInit();
		getTextField("DIAG_CODE2").onInit();
		getTextField("DIAG_CODE3").onInit();
		// ��ʼ�����
		// ���õ����˵�
		getTextField("DIAG_CODE1")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("DIAG_CODE1").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn1");
		// ���õ����˵�
		getTextField("DIAG_CODE2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("DIAG_CODE2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn2");
		// ���õ����˵�
		getTextField("DIAG_CODE3")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("DIAG_CODE3").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn3");

		if (from_flg.equals("1")) {
			// ��ʼ����Ѫʱ��
			this.setValue("PRE_DATE", date);
			// ��Ѫ����
			if (tnull.equals(use_date) || use_date == null) {
				this.setValue("USE_DATE", StringTool.rollDate(date, 1));
			} else {
				this.setValue("USE_DATE", use_date);
			}
			// ��Ѫ����
			TParm sysParm = new TParm(TJDODBTool.getInstance().select(
					BMSSQL.getBMSysParm()));
			this.setValue("END_DAYS", sysParm.getInt("END_DAYS", 0));
			// ������
			this.setValue("MR_NO", mr_no);
			if (!"".equals(mr_no)) {
				Pat pat = Pat.onQueryByMrNo(mr_no);
				if (!"en".equals(language)) {
					this.setValue("PAT_NAME", pat.getName());
				} else {
					this.setValue("PAT_NAME", pat.getName1());
				}
				this.setValue("SEX_CODE", pat.getSexCode());
				if (!"en".equals(language)) {
					this.setValue("AGE", DateUtil.showAge(pat.getBirthday(),
							date));
				} else {
					String[] args = StringTool.CountAgeByTimestamp(pat
							.getBirthday(), date);
					this.setValue("AGE", args[0] + "Y");
				}

				this.setValue("IDNO", pat.getIdNo());
			}
			// Ժ�����
			this.setValue("CASE_NO", case_no);
			// ������Դ
			if ("O".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "����");
				} else {
					this.setValue("ADM_TYPE", "O");
				}
			} else if ("E".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "����");
				} else {
					this.setValue("ADM_TYPE", "E");
				}
			} else if ("I".equals(adm_type)) {
				if (!"en".equals(language)) {
					this.setValue("ADM_TYPE", "סԺ");
				} else {
					this.setValue("ADM_TYPE", "I");
				}
			}
			// �������
			this.setValue("DEPT_CODE", dept_code);
			// ����ҽʦ
			this.setValue("DR_CODE", dr_code);

			parm.setData("MR_NO", mr_no);
			parm.setData("IO_TYPE", adm_type);
			parm.setData("CASE_NO", case_no);
			TParm result = BMSTool.getInstance().onQueryPat(parm);
			this.setValue("DEPT", result.getValue("DEPT_CODE", 0));
			this.setValue("BED_NO", result.getValue("BED_NO", 0));
			this.setValue("IPD_NO", result.getValue("IPD_NO", 0));
			this.setValue("CTZ1_CODE", result.getValue("CTZ1_CODE", 0));
			this.setValue("DIAG_CODE1", icd_desc_1);
			if (result.getValue("BLOOD_RH_TYPE", 0).equals("+")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(true);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			} else if (result.getValue("BLOOD_RH_TYPE", 0).equals("-")) {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(true);
			} else {
				this.getRadioButton("BLOOD_RH_TYPE_A").setSelected(false);
				this.getRadioButton("BLOOD_RH_TYPE_B").setSelected(false);
			}
			this.setValue("TEST_BLD", result.getValue("BLOOD_TYPE", 0));
			this.setValue("BLOOD_TEST", result.getValue("TESTBLD_DR", 0));// ������
			String bed_no = result.getValue("BED_NO", 0);
			if (!"".equals(bed_no)) {
				TParm bedParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT STATION_CODE FROM SYS_BED WHERE BED_NO = '"
								+ bed_no + "'"));
				this.setValue("STATION_CODE", bedParm.getValue("STATION_CODE",
						0));
			}
			callFunction("UI|RBC|onQueryData");
			callFunction("UI|HB|onQueryData");
			callFunction("UI|HCT|onQueryData");
			callFunction("UI|PLT|onQueryData");
			callFunction("UI|WBC|onQueryData");
		    //��Ѫʮ���������  סԺȥ��caseNo  ����3���±��
			String[] fields={"ALT","ANTI_HBS","HBEAG","HBSAG","ANTI_HBE","ANTI_HBC","ANTI_HCV","ANTI_HIV","SY","RPR"};
			if("I".equals(adm_type))
			  onOnReSet(fields);
			callFunction("UI|ALT|onQueryData");
			callFunction("UI|ANTI_HBS|onQueryData");
			callFunction("UI|HBEAG|onQueryData");
			callFunction("UI|HBSAG|onQueryData");
			callFunction("UI|ANTI_HBE|onQueryData");
			callFunction("UI|ANTI_HBC|onQueryData");
			callFunction("UI|ANTI_HCV|onQueryData");
			callFunction("UI|ANTI_HIV|onQueryData");
			callFunction("UI|SY|onQueryData");
			callFunction("UI|RPR|onQueryData");//add by sunqy 20140805
		} else if (from_flg.equals("2")) {
			this.setValue("APPLY_NO", apply_no);
			if("Y".equals(sign)){//add by sunqy 20140819 
				TCheckBox sign = (TCheckBox)getComponent("SIGN");
				sign.setSelected(true);
			}
			this.onQuery();
		} else {
			// ��ʼ����Ѫʱ��
			this.setValue("PRE_DATE", date);
		}

	}
    /**
     * 
     * @param med
     */
	private void  onOnReSet(String[] fields){
		for (String field : fields) {
			TextFormatMedLisResult med=(TextFormatMedLisResult)getComponent(field);
			med.setCaseNo("");
			med.setThreeMonths("Y");
		}	
	}
	/**
	 * ���ݼ��
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		// ����Ʊ�
		if (this.getValue("DEPT_CODE").equals("")) {
			this.messageBox("E0141");
			return false;
		}
		// ����ҽʦ
		if (this.getValue("DR_CODE").equals("")) {
			this.messageBox("E0142");
			return false;
		}
//		// ����Ѫ��
//		if (this.getValue("TEST_BLD").equals("")) {
//			this.messageBox("E0143");
//			return false;
//		}
//		// ����RH
//		if (getRadioButton("BLOOD_RH_TYPE_A").getValue().equals("N")
//				&& getRadioButton("BLOOD_RH_TYPE_B").getValue().equals("N")) {
//			this.messageBox("Ѫ��RHΪ����");
//			return false;
//		}
		// �ٴ����
		if (!this.getValue("DIAG_CODE1").equals("")
				&& !this.getValue("DIAG_CODE2").equals("")
				&& !this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE2"))
					|| this.getValue("DIAG_CODE1").equals(
							this.getValue("DIAG_CODE3"))
					|| this.getValue("DIAG_CODE2").equals(
							this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		} else if (!this.getValue("DIAG_CODE1").equals("")
				&& !this.getValue("DIAG_CODE2").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE2"))) {
				this.messageBox("E0144");
				return false;
			}
		} else if (!this.getValue("DIAG_CODE1").equals("")
				&& !this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE1").equals(this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		} else if (!this.getValue("DIAG_CODE2").equals("")
				&& !this.getValue("DIAG_CODE3").equals("")) {
			if (this.getValue("DIAG_CODE2").equals(this.getValue("DIAG_CODE3"))) {
				this.messageBox("E0144");
				return false;
			}
		}

		// ��Ѫԭ��
		if (!this.getValue("TRANRSN_CODE1").equals("")
				&& !this.getValue("TRANRSN_CODE2").equals("")
				&& !this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(
					this.getValue("TRANRSN_CODE2"))
					|| this.getValue("TRANRSN_CODE1").equals(
							this.getValue("TRANRSN_CODE3"))
					|| this.getValue("TRANRSN_CODE2").equals(
							this.getValue("TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		} else if (!this.getValue("TRANRSN_CODE1").equals("")
				&& !this.getValue("TRANRSN_CODE2").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(
					this.getValue("TRANRSN_CODE2"))) {
				this.messageBox("E0145");
				return false;
			}
		} else if (!this.getValue("TRANRSN_CODE1").equals("")
				&& !this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE1").equals(
					this.getValue("TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		} else if (!this.getValue("TRANRSN_CODE2").equals("")
				&& !this.getValue("TRANRSN_CODE3").equals("")) {
			if (this.getValue("TRANRSN_CODE2").equals(
					this.getValue("TRANRSN_CODE3"))) {
				this.messageBox("E0145");
				return false;
			}
		}

		// ����ѪƷ
		if (this.getTable("TABLE").getRowCount() < 1) {
			this.messageBox("E0146");
			return false;
		}
		// =============pangben modify 20110623 start
		if (this.getValueDouble("HB") >= 1000) {
			this.messageBox("Ѫ�쵰�׳��������ֵ");
			return false;
		}
		if (this.getValueDouble("HCT") >= 100) {
			this.messageBox("Ѫϸ��ѹ����ֵ��Ч");
			return false;
		}
		// =============pangben modify 20110623 stop
		if(this.getValue("TRANS_HISTORY") == null || "".equals(this.getValue("TRANS_HISTORY"))){
			this.messageBox("������ѪʷΪ����");
			return false;
		}
		return true;
	}

	/**
	 * ��ղ�����Ϣ
	 */
	private void ClearPatInfo() {
		String clearStr = "MR_NO;IPD_NO;CASE_NO;PAT_NAME;SEX_CODE;AGE;IDNO;"
				+ "ADM_TYPE;CTZ1_CODE;DEPT;BED_NO;STATION_CODE";
		this.clearValue(clearStr);
	}

	/**
	 * ������뵥������Ϣ
	 */
	private void ClearApplyMInfo() {
		String clearStr = "APPLY_NO;PRE_DATE;END_DAYS;USE_DATE;CH_CBD;CH_CSFCH_CTS;"
				+ "CH_CTS;DEPT_CODE;DR_CODE;TRANRSN_CODE1;TRANRSN_CODE2;"
				+ "TRANRSN_CODE3;TEST_BLD;DIAG_CODE1;DIAG_CODE2;DIAG_CODE3;"
				+ "PTT;PT;ALB;HB;HCT;OTHER;OTHER;HBSAG;ANTI_HCV;ANTI_HIV;"
				+ "SY;RPR;RBC;HB;HCT;WBC;PLT;TRANS_HISTORY;PAT_OTH;PREGNANCY;"
				+ "INFANT;ALT;ANTI_HBS;HBEAG;ANTI_HBC;ANTI_HBE;BLOOD_TEST;BLOOD_AIM;TRANS_ADVERSE;MED_ALLERGY;PREGNANT_TIMES;BORN_TIMES;ABORTION_TIMES;SERVIVAL_NO";
		this.clearValue(clearStr);
		this.getRadioButton("URG_FLG_N").setSelected(true);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("PRE_DATE", date);
	}

	/**
	 * ������뵥ϸ����Ϣ
	 */
	private void ClearApplyDInfo() {
		String clearStr = "BL00D_CODE;APPLY_QTY;UNIT_CODE";
		this.clearValue(clearStr);
		this.getTable("TABLE").removeRowAll();
	}

	/**
	 * ��ʼ��������ֵ���ղ���
	 * 
	 * @return
	 */
	public Map<String, String> initMap() {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_YY'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			return map;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm pamrRow = result.getRow(i);
			if (!pamrRow.getValue("ID").equals(""))
				map.put(pamrRow.getValue("ID"), pamrRow.getValue("CHN_DESC"));
		}
		return map;
	}

	/**
	 * ȡѪ��
	 */
	public void onTake() {
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", this.adm_type);
		parm.setData("PAT_NAME", getValueString("PAT_NAME"));
		parm.setData("APPLY_NO", getValueString("APPLY_NO"));
		parm.setData("CASE_NO", getValueString("CASE_NO"));
		parm.setData("IPD_NO", getValueString("IPD_NO"));
		parm.setData("MR_NO", getValueString("MR_NO"));
		parm.setData("BED_NO", getValueString("BED_NO"));
		parm.setData("STATION_CODE", getValueString("STATION_CODE"));
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		parm.setData("BLOOD_TYPE", this.getComboBox("TEST_BLD").getValue());
		if (this.getRadioButton("BLOOD_RH_TYPE_A").isSelected()) {
			parm.setData("BLOOD_RH_TYPE", "+");
		} else {
			parm.setData("BLOOD_RH_TYPE", "-");
		}
		parm.setData("TYPE", "INSERT");
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parm);
		onShowTakeNO();
	}
    /**
     * 
     */
	public void onShowTakeNO() {
		String applyNo=this.getValueString("APPLY_NO");
		String caseNo=this.getValueString("CASE_NO");
		String mrNo=this.getValueString("MR_NO");
		String sql = "SELECT * FROM BMS_BLDTAKEM WHERE " + " MR_NO='" + mrNo
				+ "' AND CASE_NO='" + caseNo + "' AND APPLY_NO='" + applyNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTable("TABLETK").setParmValue(result);
	}
	/**
	 * 
	 */
   public void onTableTKDoubleClick(){
	   int row=this.getTable("TABLETK").getSelectedRow();
	   if(row<=-1){
		   return;
	   }
	   TParm parm=this.getTable("TABLETK").getParmValue().getRow(row);
	   parm.setData("PAT_NAME", getValueString("PAT_NAME"));
	   parm.setData("TYPE", "UPDATE");
	   this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parm);
	   onShowTakeNO();
	   
   }
	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

}
