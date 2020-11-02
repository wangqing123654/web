package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSMTRegisterTool;
import jdo.ins.INSTJReg;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.opd.DiagRecTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * 
 * Title: ���صǼ� \���
 * 
 * Description:���صǼ� \���:���صǼǿ��߻����� \��˲�ѯ
 * 
 * Copyright: BlueCore (c) 2011
 * 
 * @author pangben 2012-1-12
 * @version 2.0
 */
public class INSMTRegisterControl extends TControl {
	private TTable table;// ���ز����б�
	private TTable tableAudit;// ����б�
	private TParm insParm;// ��ҽ��������
	private TTabbedPane tabPanel;// ҳǩ
	private String cardNo;// ҽ������
	private TParm regionParm;// �������
	// DateFormat df1 = new SimpleDateFormat("yyyyMMddhhmmss");
	private int selectRow = -1;// ѡ������
	// ��������
	private Pat pat;
	// �ҺŶ���
	private Reg reg;
	// �����
	private String caseNo;
	private String statusType;// �������״̬ ����ʱʹ��
	public static final String SEPARATED = "@";// �����ṹ������

	/**
	 * 
	 * @param str
	 * @param suff
	 * @return
	 */
	private String[] getArray(String str, String suff) {
		String arr[] = str.split(suff);

		return arr;

	}

	// �ڶ���ҳǩ����
	private String insMZMTRegister = "MR_NO;PAT_NAME;REG_DATE;CLINICROOM_NO;REALDR_CODE;CASE_NO;REGISTER_NO;OWN_NO;CASE_NO;BRANCH_CODE;SEX_CODE;BIRTH_DATE;PAT_AGE;PAT_TYPE;COMPANY_NO;COMPANY_CODE;COMPANY_DESC;"
			+ "MED_HELP_COMPANY;ENTERPRISE_TYPE;REGISTER_SERIAL_NO;PAY_KIND;DISEASE_CODE;DIAG_HOSP_CODE;"
			+ "REGISTER_DR_CODE1;REGISTER_DR_CODE2;PAT_PHONE;PAT_ZIP_CODE;"
			+ "PAT_ADDRESS;REGISTER_TYPE;DISEASE_HISTORY;ASSISTANT_EXAMINE;DIAG_CODE;"
			+ "HOSP_CODE_LEVEL1;HOSP_CODE_LEVEL2;HOSP_CODE_LEVEL3;HOSP_CODE_LEVEL3_PRO;"
			+ "DRUGSTORE_CODE;EFFECT_FLG;DATA_SOURCE;REGISTER_DATE;"
			+ "REGISTER_RESPONSIBLE;REGISTER_USER;STATUS_TYPE;AUDIT_CENTER_USER;"
			+ "INS_CROWD_TYPE;UNPASS_REASON;BEGIN_DATE;DIAG_DESC";
	// ������ҳǩ
	private String threePage = "REGISTER_NO1;PAT_NAME1;REGISTER_NO1;BEGIN_DATE1;SEX_CODE1;DISEASE_CODE1;REGISTER_USER1;"
			+ "REGISTER_SERIAL_NO;HOSP_CODE_LEVEL3_1;HOSP_CODE_LEVEL2_1;HOSP_CODE_LEVEL1_1;HOSP_CODE_LEVEL3_PRO1;DRUGSTORE_CODE1";

	// ҽ�����������
	private String newMzMt = "MED_HISTORY;ASSSISTANT_STUFF;JUDGE_END;JUDGE_CONTER_I;THE_JUDGE_START_DATE;"
			+ "THE_JUDGE_END_DATE";
	private String pageOneNew = "DISEASE_HISTORY;ASSISTANT_EXAMINE;MED_HISTORY;ASSSISTANT_STUFF";
	private TParm userParm;// ���ҽ��ҽ������

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initParm();
	}

	/**
	 * ��ʼ����
	 */
	private void initParm() {
		// ��ѯ��ʼʱ��
		this.callFunction("UI|START_DATE|setValue", SystemTool.getInstance()
				.getDate());
		// ����ʱ��
		this.callFunction("UI|END_DATE|setValue", SystemTool.getInstance()
				.getDate());
		// ��ʼʱ��
		this.callFunction("UI|BEGIN_DATE|setValue", SystemTool.getInstance()
				.getDate());
		this.setValue("INS_CROWD_TYPE", 1);// ��Ⱥ���
		table = (TTable) this.getComponent("TABLE");// ���ز����б�
		tableAudit = (TTable) this.getComponent("TABLE_AUDIT");// ����б�
		// ֻ��text���������������ICD10������
		callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");
		// textfield���ܻش�ֵ
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ������ݸ�ѡ���¼�
		tableAudit.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tabPanel = (TTabbedPane) this.getComponent("TTABPANEL");// ҳǩ
		initParmComponent();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		String sql = " SELECT USER_ID,DR_QUALIFY_CODE "
				+ "FROM SYS_OPERATOR WHERE DR_QUALIFY_CODE IS NOT NULL OR DR_QUALIFY_CODE <>'' ORDER BY USER_ID";
		userParm = new TParm(TJDODBTool.getInstance().select(sql)); // �������ҽ��ҽ��
		this.setValue("BEGIN_DATE1",SystemTool.getInstance().getDate());
	}

	private void initParmComponent() {
		this.setValue("DIAG_HOSP_CODE", "000551");
		this.setValue("REGISTER_DR_CODE1", Operator.getID());// ����ҽ��1
		this.setValue("REGISTER_USER", Operator.getID());// ������
		this.setValue("REGISTER_RESPONSIBLE", Operator.getID());// ������
	}

	/**
	 * ���߱���
	 * 
	 * @return boolean
	 */
	public void onCommandButSave() {
		// ��ȡ������Ϣ
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("���ȶ���");
			return;
		}
		if (!checkOut()) {
			return;
		}
		String[] name = { "REGISTER_USER", "REGISTER_DR_CODE1",
				"REGISTER_DR_CODE2", "REGISTER_RESPONSIBLE",
				"HOSP_CODE_LEVEL1", "HOSP_CODE_LEVEL2", "HOSP_CODE_LEVEL3",
				"HOSP_CODE_LEVEL3_PRO", "DRUGSTORE_CODE" };
		for (int i = 0; i < name.length; i++) {
			if (this.getValueString(name[i]).length() <= 0) {
				this.messageBox(getTTextFormat(name[i]).getName() + "����Ϊ��");
				this.grabFocus(name[i]);
				return;
			}
		}
		// ���ֱ���
		// if (!this.emptyTextCheck("DISEASE_CODE")) {
		// tabPanel.setSelectedIndex(1);// �ڶ���ҳǩ��ʾ
		// return;
		// }
		getMrNoOne(false);
		if (null == pat || null == pat.getMrNo()) {
			return;
		}
		TParm parm = new TParm();
		name = insMZMTRegister.split(";");// ��õڶ���ҳǩ������
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValueString(name[i]));
		}
		// =====================pangben 2012-4-10 start
		// name=pageOneNew.split(";");
		// for (int i = 0; i < name.length; i++) {
		// parm.setData(name[i], this.getValueString(name[i]));
		// };
		parm.setData("MED_HISTORY", this.getText("MED_HISTORY"));// ����ʷ(����)
		parm.setData("ASSSISTANT_STUFF", this.getText("ASSSISTANT_STUFF"));// ��������(����)
		parm.setData("DISEASE_HISTORY", this.getText("DISEASE_HISTORY"));// ����ʷ(����)
		parm.setData("ASSISTANT_EXAMINE", this.getText("ASSISTANT_EXAMINE"));// ��������(����)
		// =====================pangben 2012-4-10 stop
		parm.setData("BEGIN_DATE", this.getValueString("BEGIN_DATE").replace(
				"-", "").substring(0, 8));// ��ʼʱ��
		parm.setData("END_DATE", this.getValueString("END_DATE").replace("-",
				"").substring(0, 8));// ����ʱ��
		parm.setData("CARD_NO", insParm.getValue("CARD_NO").toString());// ����
		parm.setData("PERSONAL_NO", insParm.getValue("PERSONAL_NO").toString());// ���˱���
		parm.setData("OWN_NO", insParm.getValue("PERSONAL_NO").toString());// ���˱���
		parm.setData("REGION_CODE", Operator.getRegion());// �������
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		parm.setData("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));// ���ز���
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGISTER_DR_CODE1", getInsId(this
				.getValueString("REGISTER_DR_CODE1")));
		parm.setData("REGISTER_DR_CODE2", getInsId(this
				.getValueString("REGISTER_DR_CODE2")));
		parm.setData("REGISTER_USER", getInsId(this
				.getValueString("REGISTER_USER")));
		parm.setData("REGISTER_RESPONSIBLE", getInsId(this
				.getValueString("REGISTER_RESPONSIBLE")));
		// System.out.println("INSPARM111111::" + parm);
		TParm result = new TParm(INSTJReg.getInstance().onCommandButSave(
				parm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());// ִ��ʧ��
			return;
		}
		// ==========pangben 2012-4-9 start
		if (null != result.getValue("message")
				&& result.getValue("message").length() > 0) {
			this.messageBox(result.getValue("message"));// ҽ������ ��Ӽ���ʱ��
		}
		if (null != result.getValue("messageEnd")
				&& result.getValue("messageEnd").length() > 0) {
			this.messageBox(result.getValue("messageEnd"));// �ϴμ�������LAST_JUDGE_END:0.���϶�1�����϶�
		}
		// ==========pangben 2012-4-9 stop
		// �����Ϣ
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE"));
			// return;
		} else {
			this.messageBox("P0005");// ִ�гɹ�
		}
		TParm MTRegisterParm = result.getParm("MTRegisterParm");
		TParm commParm = result.getParm("commParm");// ��ò�����������
		if (null != commParm && null != commParm.getValue("PERSONAL_NO")) {
			commParm.setData("MR_NO", MTRegisterParm.getValue("MR_NO"));
			threePageShow(commParm,true);// ������ҳǩ����
		}
		// System.out.println("MTRegisterParm:" + MTRegisterParm);
		this.setValueForParm(insMZMTRegister, MTRegisterParm, 0);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(MTRegisterParm.getValue(userName[i],0)));
		}
		// ==================pangben 2012-4-10 ���ҽ������
		String[] newString = newMzMt.split(";");
		for (int i = 0; i < newString.length; i++) {
			this.setValue(newString[i] + "_OUT", MTRegisterParm.getData(
					newString[i], 0));
		}
		this.setValue("THE_JUDGE_TOT_AMT_OUT", MTRegisterParm.getDouble(
				"THE_JUDGE_TOT_AMT", 0));
		this.setValue("THE_JUDGE_APPLY_AMT_OUT", MTRegisterParm.getDouble(
				"THE_JUDGE_APPLY_AMT", 0));
		this.setValue("NUMBER_PAY_AMT_OUT", MTRegisterParm.getDouble(
				"NUMBER_PAY_AMT", 0));
		// ==================pangben 2012-4-10 stop
		this.setValue("REGISTER_NO", MTRegisterParm.getValue("REGISTER_NO", 0));// ���صǼǱ��
	}
	/**
	 * ���ҽ��ҽ������
	 * @param id
	 * @return
	 */
	private String getInsId(String id) {
		for (int i = 0; i < userParm.getCount(); i++) {
			if (id.equals(userParm.getValue("USER_ID", i))) {
				return userParm.getValue("DR_QUALIFY_CODE", i);
			}
		}
		return "";
	}
	/**
	 * ���ҽԺҽ������
	 * @param id
	 * @return
	 */
	private String getUserId(String id){
		for (int i = 0; i < userParm.getCount(); i++) {
			if (id.equals(userParm.getValue("DR_QUALIFY_CODE", i))) {
				return userParm.getValue("USER_ID", i);
			}
		}
		return "";
	}
	/**
	 * ��������
	 */
	public void onReadCard() {
		// ��Ⱥ���
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		// parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CARD_TYPE", 5);// �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ��5�����صǼǣ�
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// ��Ⱥ���
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm)
			return;
		// System.out.println("��ȡҽ����::" + insParm);
		// System.out.println("���˱��룺����"+insParm.getValue("PERSONAL_NO"));
		int returnType = insParm.getInt("RETURN_TYPE");// ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			return;
		}
		// ��Ⱥ���:1.��ְ 2.�Ǿ�br
		if (insParm.getInt("CROWD_TYPE") == 2) {
			if (!insParm.getValue("PAT_NAME").equals(this.getValue("PAT_NAME"))) {// �ǾӲ����ж��Ƿ���ѡ��Ĳ�����ͬ
				if (this.messageBox("��ʾ", "��Ƭ������("
						+ insParm.getValue("PAT_NAME") + ")������ǰѡ��Ĳ�������("
						+ this.getValue("PAT_NAME") + ")����,�Ƿ����", 2) != 0) {
					tabPanel.setSelectedIndex(0);
					return;
				}
			}
		}
		this.setValue("PAT_NAME", insParm.getValue("PAT_NAME"));// ����
		this.setValue("SEX_CODE", insParm.getValue("SEX_CODE"));// �Ա�
		this.setValue("OWN_NO", insParm.getValue("PERSONAL_NO"));// ���˱���
		this.setValue("OWN_NO1", insParm.getValue("PERSONAL_NO"));// ���˱���
		this.setValue("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));// ���ز��ֱ���
		this.setValue("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE"));
		cardNo = insParm.getValue("CARD_NO");// ҽ������
		tabPanel.setSelectedIndex(1);// �ڶ���ҳǩ��ʾ
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (!this.emptyTextCheck("START_DATE,END_DATE")) {// У��ʱ��
			return;
		}
		TParm parm = new TParm();
		parm.setData("BEGIN_DATE", StringTool.setTime((Timestamp) this
				.getValue("START_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("END_DATE"), "23:59:59"));
		if (this.getValue("SUM_MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("SUM_MR_NO"));// ������
		}
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {// ����
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// ��ѯ����
		TParm result = INSMTRegisterTool.getInstance().queryINSMTRegister(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * ���ﲡ���б�˫���¼�
	 */
	public void onDoubleClick() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm parm = table.getParmValue();
		selectRow = row;// ѡ��������
		statusType = parm.getValue("STATUS_TYPE", row);// �������״̬
		caseNo = parm.getValue("CASE_NO", row);// �����
		this.setValueForParm(insMZMTRegister, parm, row);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(parm.getValue(userName[i],0)));
		}
		TParm parmValue = parm.getRow(row);
		getDrUserIdValue(parmValue);
		threePageShow(parmValue,true);
		tabPanel.setSelectedIndex(1);// �ڶ���ҳǩ��ʾ
	}

	/**
	 * ��Ժ��ҽ��������
	 * 
	 * @param parm
	 */
	private void getDrUserIdValue(TParm parm) {
		String[] drCode = { "REGISTER_DR_CODE1", "REGISTER_DR_CODE2",
				"REGISTER_RESPONSIBLE", "REGISTER_USER" };
		for (int i = 0; i < drCode.length; i++) {
			this.setValue(drCode[i], setCommboxValue(parm.getValue(drCode[i])));
		}
	}

	/**
	 * ������ҳǩ��ʾ����
	 * 
	 * @param parm
	 * @param row
	 */
	private void threePageShow(TParm parm,boolean flg) {
		// ������ҳǩ��ֵ
		this.setValue("MR_NO1", parm.getValue("MR_NO"));// ��������
		this.setValue("PAT_NAME1", parm.getValue("PAT_NAME"));// ��������
		this.setValue("SEX_CODE1", parm.getValue("SEX_CODE"));// �����Ա�
		this.setValue("REGISTER_NO1", parm.getValue("REGISTER_NO"));// ���صǼǱ��
		this.setValue("OWN_NO1", parm.getValue("PERSONAL_NO"));// ���˱���
		this.setValue("DISEASE_CODE1", parm.getValue("DISEASE_CODE"));// ���ز��ֱ���
		String date=parm.getValue("BEGIN_DATE");
		if (!flg) {
			date=date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
			this.setValue("BEGIN_DATE1",date);// ��ʼ����
		}else{
			this.setValue("BEGIN_DATE1",parm.getTimestamp("BEGIN_DATE"));// ��ʼ����
		}
		//Timestamp date=StringTool.getTimestamp(parm.getValue("BEGIN_DATE"), "yyyy/MM/dd");
		
		this.setValue("REGISTER_USER1", setCommboxValue(parm
				.getValue("REGISTER_USER")));// ���������
		this
				.setValue("REGISTER_SERIAL_NO", parm
						.getValue("REGISTER_SERIAL_NO"));// ���صǼ����
		// this.setValue("HOSP_CODE_LEVEL3_1", "111");// ����ҽԺ
		this.setValue("HOSP_CODE_LEVEL3_1", parm.getValue("HOSP_CODE_LEVEL3"));// ����ҽԺ
		this.setValue("HOSP_CODE_LEVEL2_1", parm.getValue("HOSP_CODE_LEVEL2"));// ����ҽԺ
		this.setValue("HOSP_CODE_LEVEL1_1", parm.getValue("HOSP_CODE_LEVEL1"));// һ��ҽԺ
		this.setValue("HOSP_CODE_LEVEL3_PRO1", parm
				.getValue("HOSP_CODE_LEVEL3_PRO"));// ����ר��ҽԺ
		String[] name = { "MED_HISTORY", "ASSSISTANT_STUFF", "JUDGE_CONTER_I",
				"JUDGE_END" };
		for (int i = 0; i < name.length; i++) {
			if (name[i].equals("JUDGE_END")) {
				String judgeName="";
				if (null!= parm.getValue(name[i])) {
					if (parm.getValue(name[i]).equals("0")) {
						judgeName="���϶�";
					}else if (parm.getValue(name[i]).equals("1")){
						judgeName="�����϶�";
					}
				}
				this.setText(name[i] + "_OUT",judgeName);
				this.setText(name[i],judgeName);
			}else{
				this.setText(name[i] + "_OUT", parm.getValue(name[i]));
				this.setText(name[i], parm.getValue(name[i]));
			}
			
		}
		this.setValueForParm(insMZMTRegister, parm);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(parm.getValue(userName[i])));
		}
		this.setValue("DRUGSTORE_CODE1", parm.getValue("DRUGSTORE_CODE"));// ��������ҩ��
	}

	/**
	 * У�飬��������ʹ��
	 * 
	 * @return
	 */
	private boolean checkOut() {

		if (!this
				.emptyTextCheck("INS_CROWD_TYPE,MR_NO,CASE_NO,PAT_NAME,OWN_NO,DIAG_CODE,PAT_PHONE,PAT_ADDRESS")) {
			return false;
		}
		
		if (null == this.getValue("PAY_KIND")
				|| this.getValue("PAY_KIND").toString().length() <= 0) {// ���ز��ֲ�����Ϊ��
			this.messageBox("֧�������Ϊ��");
			this.grabFocus("PAY_KIND");
			return false;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// ���ز��ֲ�����Ϊ��
			this.messageBox("���ز��ֲ�����Ϊ��");
			this.grabFocus("DISEASE_CODE");
			return false;
		}
		if (null == this.getValue("DIAG_HOSP_CODE")
				|| this.getValue("DIAG_HOSP_CODE").toString().length() <= 0) {
			this.messageBox("�������ҽԺ");
			this.grabFocus("DIAG_HOSP_CODE");
			return false;
		}
		if (null == this.getValue("REGISTER_DR_CODE1")
				|| this.getValue("REGISTER_DR_CODE1").toString().length() <= 0) {
			this.messageBox("�Ǽ�ҽ��1������Ϊ��");
			this.grabFocus("REGISTER_DR_CODE1");
			return false;
		}
		if (null == this.getValue("REGISTER_USER")
				|| this.getValue("REGISTER_USER").toString().length() <= 0) {
			this.messageBox("�����˲�����Ϊ��");
			this.grabFocus("REGISTER_USER");
			return false;
		}
		if (null == this.getValue("REGISTER_RESPONSIBLE")
				|| this.getValue("REGISTER_RESPONSIBLE").toString().length() <= 0) {
			this.messageBox("�����˲�����Ϊ��");
			this.grabFocus("REGISTER_RESPONSIBLE");
			return false;
		}
		return true;
	}

	/**
	 * ����
	 */
	public void onLoadDown() {
		// ����
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("���ȶ���");
			return;
		}
		// ���ز��ֱ���
		if (!this.emptyTextCheck("REGISTER_NO,CASE_NO,MR_N0")) {
			return;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// ���ز��ֲ�����Ϊ��
			this.messageBox("���ز��ֲ�����Ϊ��");
			this.grabFocus("DISEASE_CODE");
			return;
		}
		if (null == this.getValue("PAY_KIND")
				|| this.getValue("PAY_KIND").toString().length() <= 0) {// ���ز��ֲ�����Ϊ��
			this.messageBox("֧����𲻿���Ϊ��");
			this.grabFocus("PAY_KIND");
			return;
		}
		insParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));// ���ֱ���
		insParm.setData("REGISTER_NO", this.getValue("REGISTER_NO"));// ���صǼǱ���
		insParm.setData("MR_NO", this.getValue("MR_NO"));// ������
		insParm.setData("CASE_NO", this.getValue("CASE_NO"));// �����
		insParm.setData("REGION_CODE", Operator.getRegion());// ����
		insParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ����������
		insParm.setData("OPT_USER", Operator.getID());// 
		insParm.setData("OPT_TERM", Operator.getIP());// 
		insParm.setData("STATUS_TYPE", statusType==null?"0":statusType);// �������״̬
		// TParm parm = new TParm();
		// String[] name = insMZMTRegister.split(";");// ��õڶ���ҳǩ������
		// for (int i = 0; i < name.length; i++) {
		// parm.setData(name[i], this.getValue(name[i]));
		// }
		String[] name = insMZMTRegister.split(";");// ��õڶ���ҳǩ������
		for (int i = 0; i < name.length; i++) {
			insParm.setData(name[i], this.getValueString(name[i]));
		}
		insParm.setData("BEGIN_DATE", StringTool.getString(TypeTool
				.getTimestamp(this.getValue("BEGIN_DATE")), "yyyyMMdd"));// �������״̬
		TParm result = new TParm(INSTJReg.getInstance().onLoadDown(
				insParm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		} else {
			this.messageBox("���سɹ�");
		}
		getDrUserIdValue(result.getRow(0));
		this.setValueForParm(insMZMTRegister, result, 0);// ���ݸ�ֵ
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(result.getValue(userName[i],0)));
		}
		this.setValue("REGISTER_NO", result.getValue("REGISTER_NO", 0));// ���صǼǱ��
		tabPanel.setSelectedIndex(1);// �ڶ���ҳǩ��ʾ
	}

	/**
	 * ������Ϣ ������ҳǩ����
	 */
	public void onShare() {
		// ����
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("���ȶ���");
			return;
		}
		// ���ز��ֱ���,��Ⱥ���
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// ���ز��ֲ�����Ϊ��
			this.messageBox("���ز��ֲ�����Ϊ��");
			this.grabFocus("DISEASE_CODE");
			return;
		}
		insParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));
		insParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",0));
		TParm result = new TParm(INSTJReg.getInstance().onShare(
				insParm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		} else {
			this.messageBox("P0005");
		}
		threePageShow(result,false);
		
		tabPanel.setSelectedIndex(2);// ������ҳǩ��ʾ
		// ������ҳǩ��ֵ
		// this.
	}

	/**
	 * ��˲�ѯ����
	 */
	public void onQueryAudit() {
		// ��Ⱥ���
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));
		if (this.getValueBoolean("UNRDO_AUDIT")) {// ���״̬
			parm.setData("STATUS_TYPE", 0);// δ���
		} else {
			parm.setData("STATUS_TYPE", 1);// �����
		}
		if (this.getValue("SUM_MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("SUM_MR_NO"));// ������
		}
		TParm result = INSMTRegisterTool.getInstance().queryAudit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			return;
		}
		tableAudit.setParmValue(result);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		tableAudit.removeRowAll();// ����б�
		table.removeRowAll();// ���ز����б�
		insParm = null;// ��ҽ��������
		cardNo = null;// ҽ������
		selectRow = -1;// ѡ������
		pat = null;// ��������
		reg = null;// �ҺŶ���
		caseNo = null;// �����
		statusType = null;// �������״̬ ����ʱʹ��
		this.setValue("INS_CROWD_TYPE", "1");// ��Ⱥ���Ĭ��
		clearValue(insMZMTRegister + ";" + threePage + ";" + newMzMt);
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ���ĸ�ҳǩȫѡ
		// ��ʼʱ��
		this.callFunction("UI|BEGIN_DATE|setValue", SystemTool.getInstance()
				.getDate());
		initParmComponent();
	}

	/**
	 * ͨ��ҽ��ҽ�������ñ�Ժ��ҽ������
	 * 
	 * @param insUserId
	 * @return
	 */
	private String setCommboxValue(String insUserId) {
		for (int i = 0; i < userParm.getCount(); i++) {
			if (insUserId.equals(userParm.getValue("DR_QUALIFY_CODE", i))) {
				return userParm.getValue("USER_ID", i);
			}
		}
		return "";
	}

	/**
	 * �������¼�
	 */
	public void getMrNo() {
		getMrNoOne(true);
	}

	private void getMrNoOne(boolean flg) {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));

		if (null == pat || null == pat.getMrNo()) {
			this.messageBox("�����ڴ˲���");
			this.grabFocus("MR_NO");
			return;
		} else {
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());
			this.setValue("SEX_CODE", pat.getSexCode());
		}
		if (flg) {
			getCaseNo();
		}

	}

	/**
	 * ������� ������ҳǩ����
	 */
	public void onUpdate() {
		if (selectRow < 0 || null == caseNo) {
			this.messageBox("��ѡ��Ҫ���������");
			tabPanel.setSelectedIndex(0);// ������ҳǩ��ʾ
			return;
		}
		// У��
		String[] checkValue = { "REGISTER_USER1", "HOSP_CODE_LEVEL3_1",
				"HOSP_CODE_LEVEL2_1", "HOSP_CODE_LEVEL1_1", "DRUGSTORE_CODE1" };
		for (int i = 0; i < checkValue.length; i++) {
			if (null == this.getValue(checkValue[i])
					|| this.getValue(checkValue[i]).toString().length() <= 0) {
				this.messageBox(getTTextFormat(checkValue[i]).getName()
						+ "����Ϊ��");
				this.grabFocus(checkValue[i]);
				return;
			}
		}
		TParm parm = new TParm();
		parm.setData("REGISTER_NO", this.getValue("REGISTER_NO1"));// ���صǼǱ���
		parm.setData("MR_NO", this.getValue("MR_NO1"));// ������
		parm.setData("CASE_NO", caseNo);// �����
		parm.setData("HOSP_CODE_LEVEL3", this.getValue("HOSP_CODE_LEVEL3_1"));// ����ҽԺ
		parm.setData("HOSP_CODE_LEVEL2", this.getValue("HOSP_CODE_LEVEL2_1"));// ����ҽԺ
		parm.setData("HOSP_CODE_LEVEL1", this.getValue("HOSP_CODE_LEVEL1_1"));// һ��ҽԺ
		parm.setData("HOSP_CODE_LEVEL3_PRO", "");// ����ר��ҽԺ
		parm.setData("DRUGSTORE_CODE", this.getValue("DRUGSTORE_CODE1"));// ��������ҩ��
		parm.setData("REGISTER_USER", getInsId(this
				.getValueString("REGISTER_USER1")));// �����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm parms = new TParm();
		parms.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		parms.addData("REGISTER_NO", this.getValue("REGISTER_NO1"));
		parms.addData("OWN_NO", this.getValueString("OWN_NO"));
		parms.addData("DISEASE_CODE", this.getValue("DISEASE_CODE1"));
		parms
				.addData("REGISTER_SERIAL_NO", this
						.getValue("REGISTER_SERIAL_NO"));
		parms.addData("REGISTER_BEGIN_DATE", this.getValue("BEGIN_DATE1")
				.toString().replace("/", "").replace("-", "").substring(0,8));
		parms
				.addData("HOSP_CODE_LEVEL1_1", this
						.getValue("HOSP_CODE_LEVEL1_1"));
		parms
				.addData("HOSP_CODE_LEVEL2_1", this
						.getValue("HOSP_CODE_LEVEL2_1"));
		parms
				.addData("HOSP_CODE_LEVEL3_1", this
						.getValue("HOSP_CODE_LEVEL3_1"));
		parms
		.addData("HOSP_CODE_LEVEL3_PRO1", this
				.getValue("HOSP_CODE_LEVEL3_PRO1"));
		parms.addData("DRUGSTORE_CODE1", this.getValue("DRUGSTORE_CODE1"));
		parms.addData("UPDATE_DATE", SystemTool.getInstance().getDate());
		parms.addData("UP_USER",
				getInsId(this.getValueString("REGISTER_USER1")));
		
		parms.setData("PIPELINE", "DataDown_cmts");
		parms.setData("PLOT_TYPE", "Q");
		parms.addData("PARM_COUNT", 13);
		TParm result = InsManager.getInstance().safe(parms);
		if (null == result || result.getInt("PROGRAM_STATE") <0) {
			this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			return;
		}
		result = INSMTRegisterTool.getInstance().updateThreePageData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("����ɹ�");
		}

	}

	/**
	 * ���TTextFormat�ؼ�
	 * 
	 * @param format
	 * @return
	 */
	private TTextFormat getTTextFormat(String format) {
		return (TTextFormat) this.getComponent(format);
	}

	/**
	 * ����޸�״̬���� 0:δ��� 1:�����
	 */
	public void onUpdateStutsType() {
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		onExeUpdateStutsType("1");// δ���ִ���Ժ�Ϊ�����״̬

	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * ��������
	 */
	public void onConcelStutsType() {
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parmValue = tableAudit.getParmValue();// ����������
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		if (this.messageBox("��ʾ", "�Ƿ�ִ�г�������", 2) != 0) {
			return;
		}
		TParm result = new TParm();
		TParm tempParm = parmValue.getRow(row);// ִ�е�����
		tempParm.setData("BEGIN_DATE", this.getValue("BEGIN_DATE").toString()
				.replace("-", "").replace("/", ""));
		tempParm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			result = INSTJTool.getInstance().DataDown_mts_P(tempParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_cmts_P(tempParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox(result.getErrText());
			return;
		}
		String sql = "DELETE FROM INS_MZMT_REGISTER WHERE CASE_NO='"
				+ tempParm.getValue("CASE_NO") + "'" + " AND REGISTER_NO='"
				+ tempParm.getValue("REGISTER_NO") + "'";
		TParm deleteParm = new TParm(TJDODBTool.getInstance().update(sql));
		if (deleteParm.getErrCode() < 0) {
			this.messageBox("ɾ��ʧ��");
			return;
		}
		this.messageBox("P0005");

		// STATUS_TYPE
	}

	private void onExeUpdateStutsType(String stutsTypeFlg) {
		TParm parmValue = tableAudit.getParmValue();// ����������
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		TParm tempParm = parmValue.getRow(row);// ִ�е�����
		// �ж��Ƿ���ѡ�е�����
		TParm result = new TParm();
		tempParm.setData("BEGIN_DATE", this.getValue("BEGIN_DATE").toString()
				.replace("-", "").replace("/", ""));
		tempParm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		// ����������ز���
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			result = INSTJTool.getInstance().DataDown_mts_D(tempParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_cmts_D(tempParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox(result.getErrText());
			return;
		}
		String statusType = result.getValue("STATUS_TYPE");
		String registerCenterUser = result.getValue("REGISTER_CENTER_USER");// ���ľ�����
		String unpassReason = result.getValue("UNPASS_REASON");// ��ͨ��ԭ��
		if ("1".equals(statusType))
			this.messageBox("ͨ�����  ����ˣ�" + registerCenterUser);
		else if ("2".equals(statusType)) {
			this.messageBox("δͨ����� ԭ��:" + unpassReason);
		} else {
			statusType = "0";
			messageBox("����δ��ˣ�");
		}
		tempParm.setData("OPT_USER", Operator.getID());
		tempParm.setData("OPT_TERM", Operator.getIP());
		tempParm.setData("STATUS_TYPE", statusType);// ���״̬ 0:δ��� 1:�����
		tempParm.setData("AUDIT_CENTER_USER", registerCenterUser);
		tempParm.setData("UNPASS_REASON", unpassReason);

		result = INSMTRegisterTool.getInstance().updateStutsType(tempParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onQueryAudit();
		}

	}

	// /**
	// * ���ĸ�ҳǩȫѡ
	// */
	// public void onSelectAll() {
	// TParm parmValue = tableAudit.getParmValue();// ����������
	// if (parmValue.getCount() <= 0) {
	// this.messageBox("û��Ҫִ�е�����");
	// return;
	// }
	// boolean flg = false;// �ж��Ƿ�ѡ��
	// if (this.getValueBoolean("SELECT_ALL")) {
	// flg = true;
	// } else {
	// flg = false;
	// }
	// for (int i = 0; i < parmValue.getCount(); i++) {
	// parmValue.setData("FLG", i, flg);
	// }
	// tableAudit.setParmValue(parmValue);
	// }

	/**
	 * ҳǩ����¼�
	 */
	// public void onChangeTab() {
	// ((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);//
	// ���ĸ�ҳǩȫѡ
	// // tableOpd.removeRowAll();
	// }

	/**
	 * �����ղ���
	 */
	public void onClearA() {
		tableAudit.removeRowAll();
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// ���ĸ�ҳǩȫѡ
		((TRadioButton) this.getComponent("UNRDO_AUDIT")).setSelected(false);// δ���Ĭ��
		this.setValue("INS_CROWD_TYPE", "1");// ��Ⱥ���Ĭ��
	}

	/**
	 * ��ѡ��ѡ���¼�
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		// TParm oldTempParm = new TParm();
		// TParm oldParm = table.getParmValue();
		// int index = 0;
		table.acceptText();
		return false;
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

	/**
	 * �������ص�ѡ��ť����
	 */
	public void onClickSave() {
		if (getRadioButton("RO_OPEN").isSelected()) {// ���ߵ�ѡ��ť
			callFunction("UI|REGISTER_NO|setEnabled", false);// ���صǼǱ��
		} else {
			callFunction("UI|REGISTER_NO|setEnabled", true);// ���صǼǱ��
		}
	}

	/**
	 * ��þ������
	 */
	public void getCaseNo() {
		if (pat == null) {
			this.messageBox("�����Ų���Ϊ��");
			this.grabFocus("MR_NO");
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
		this.setValue("CASE_NO", caseNo);
		parm.setData("CASE_NO", caseNo);
		TParm diagRecparm = DiagRecTool.getInstance().queryInsData(parm);
		if (diagRecparm.getErrCode() < 0) {
			return;
		}
		// ====================pangben 2012-4-10 start
		String sql = "SELECT MED_HISTORY,DISEASE_HISTORY,ASSISTANT_EXAMINE,ASSSISTANT_STUFF FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getValue("DISEASE_HISTORY",0).length()>0) {
			String[] newString = pageOneNew.split(";");
			for (int i = 0; i < newString.length; i++) {
				this.setValue(newString[i], sqlParm.getValue(newString[i], 0));
			}
		}
		// ====================pangben 2012-4-10 stop
		this.setValue("DIAG_DESC", diagRecparm.getValue("ICD_CHN_DESC", 0));// �������
		this.setValue("DIAG_CODE", diagRecparm.getValue("ICD_CODE", 0));// ��ϱ���
	}

	/**
	 * ��˴�ӡ
	 */
	public void onExePrint() {
		TParm parmValue = tableAudit.getParmValue();// ����������
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			return;
		}
		TParm tempParm = parmValue.getRow(row);// ִ�е�����
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "����л���ҽ�Ʊ������������ض����ּ�����");
		String companyCode = tempParm.getValue("COMPANY_CODE");// ��λ����
		char[] com = companyCode.toCharArray();
		for (int i = 0; i < com.length; i++) {
			parm.setData("UNIT" + (i + 1), "TEXT", com[i]);
		}
		// LAST_JUDGE_END 1.�״� 2.���� 3.�����״�
		if (tempParm.getInt("REGISTER_SERIAL_NO") == 1) {
			parm.setData("REGISTER_SERIAL_NO1", "TEXT", "��");
		} else if (tempParm.getInt("REGISTER_SERIAL_NO") == 2) {
			parm.setData("REGISTER_SERIAL_NO2", "TEXT", "��");
		} else if (tempParm.getInt("REGISTER_SERIAL_NO") == 3) {
			parm.setData("REGISTER_SERIAL_NO3", "TEXT", "��");
		}
		parm.setData("TABLE_NO", "TEXT", "18_1"); // ���
		parm.setData("PAT_NAME", "TEXT", tempParm.getValue("PAT_NAME"));// ����
		parm.setData("SEX_CODE", "TEXT", null != tempParm
				&& tempParm.getInt("SEX_CODE") == 1 ? "��" : "Ů");// �Ա�
		parm.setData("AGE", "TEXT", tempParm.getValue("PAT_AGE"));// ����
		parm.setData("TEL_P", "TEXT", tempParm.getValue("PAT_PHONE"));// �绰����
		parm.setData("IDNO", "TEXT", tempParm.getValue("IDNO"));// ���֤����
		parm.setData("ADDRESS", "TEXT", tempParm.getValue("ADDRESS"));// ��ַ
		// ��ʷ
		String diseaseHistory = tempParm.getValue("DISEASE_HISTORY");
		setDiseaseHistory(parm, diseaseHistory, "DISEASE_HISTORY_");
		// ����ʷ
		String medHistory = tempParm.getValue("MED_HISTORY");
		setDiseaseHistory(parm, medHistory, "MED_HISTORY_");
		// �������
		String assistantExamine = tempParm.getValue("ASSISTANT_EXAMINE");

		setExamine(parm, assistantExamine, "ASSISTANT_EXAMINE_");
		if (tempParm.getInt("LAST_JUDGE_END") == 1) {
			parm.setData("LAST_JUDGE_END_1", "TEXT", "��");// �ٴ����
		} else {
			parm.setData("LAST_JUDGE_END_1", "TEXT", "��");// �ٴ����
		}
		parm.setData("AUDIT_CENTER_USER", "TEXT", tempParm
				.getValue("AUDIT_CENTER_USER"));// ������Ա
		parm.setData("HOSP_CODE_LEVEL3", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL3")));// ����ҽԺ
		parm.setData("HOSP_CODE_LEVEL2", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL2")));// ����ҽԺ
		parm.setData("HOSP_CODE_LEVEL1", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL1")));// һ��ҽԺ
		parm.setData("DRUGSTORE_CODE", "TEXT", getHospDesc(tempParm
				.getValue("DRUGSTORE_CODE")));// ҩ��
		parm.setData("BEGIN_DATE", "TEXT", tempParm.getValue("BEGIN_DATE")
				.substring(0, 11));// ��ʼʱ��
		parm.setData("END_DATE", "TEXT", tempParm.getValue("END_DATE")
				.substring(0, 11));// ����ʱ��
		// ������

		parm.setData("BRANCH_DESC", "TEXT", getBranch(tempParm
				.getValue("BRANCH_CODE")));
		parm.setData("MED_HELP_COMPANY", "TEXT", tempParm
				.getValue("MED_HELP_COMPANY"));// �α���λ
		parm.setData("JUDGE_CONTER", "TEXT", "");// ��������
		parm.setData("JUDGE_SEQ", "TEXT", tempParm.getValue("JUDGE_SEQ"));

		parm.setData("CTZ_CODE", "TEXT", getCtzCode(tempParm
				.getInt("INS_CROWD_TYPE"), tempParm));
		parm.setData("REGISTER_USER", "TEXT", tempParm
				.getValue("REGISTER_USER"));//
		parm.setData("REGISTER_RESPONSIBLE", "TEXT", tempParm
				.getValue("REGISTER_RESPONSIBLE"));// 
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMTRegister.jhw",
				parm);

	}

	private String getHospDesc(String code) {
		String sql = "SELECT HOSP_DESC FROM INS_HOSP_LIST WHERE HOSP_CODE='"
				+ code + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getErrCode() < 0) {
			return "";
		}
		return sqlParm.getValue("HOSP_DESC", 0);
	}

	/**
	 * �������
	 * 
	 * @return
	 */
	private void setExamine(TParm parm, String strExa, String name) {
		String str[] = getArray(strExa, SEPARATED);
		//
		for (int i = 1; i <= 11; i++) {
			parm.setData(name + i, "TEXT", str[i - 1]);
			// getNumCom("B"+i).setText(str[i-1]);
		}

	}

	/**
	 * ���첡ʷ
	 * 
	 * @return
	 */
	private void setDiseaseHistory(TParm parm, String strDH, String name) {
		String str[] = getArray(strDH, SEPARATED);
		for (int i = 1; i <= 4; i++) {
			if (str[i - 1].equals("1")) {
				parm.setData(name + i, "TEXT", "��");//
			}
		}
		if (str.length > 4 && !name.equals("MED_HISTORY_")) {
			parm.setData("YEAR", "TEXT", str[4]);//
			parm.setData("JIN", "TEXT", str[5]);//
		}
	}

	/**
	 * �����籣����
	 * 
	 * @param code
	 * @return
	 */
	private String getBranch(String code) {
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='INS_FZX' AND ID='"
				+ code + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getErrCode() < 0) {
			return "";
		}
		return sqlParm.getValue("CHN_DESC", 0);
	}

	/**
	 * ͨ��ҽ�����β�ѯ������Ա������
	 * 
	 * @param crowType
	 * @param exeParm
	 * @return
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('11','12','13')";
		} else if (crowType == 2) {// 1 ��ְ 2.�Ǿ�
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('21','22','23')";
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_DESC,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_DESC", 0);
	}

}
