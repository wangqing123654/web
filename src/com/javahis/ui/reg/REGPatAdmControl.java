package com.javahis.ui.reg;

import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BIL;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import jdo.ekt.EKTpreDebtTool;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.mem.MEMSQL;
import jdo.mem.MEMTool;
import jdo.mro.MROBorrowTool;
import jdo.mro.MROQueueTool;
import jdo.opd.OrderTool;
import jdo.reg.PanelRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGCcbReTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.REGTool;
import jdo.reg.Reg;
import jdo.reg.RegMethodTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sid.IdCardO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.EktDriver;
import com.javahis.device.NJCityInwDriver;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.system.combo.TComboSysCtz;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import com.javahis.ui.ekt.EKTReceiptPrintControl;
import com.javahis.ui.opb.Objects;
import com.javahis.util.DateUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * 
 * <p>
 * Title:�Һ�����������
 * </p>
 * 
 * <p>
 * Description:�Һ�����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author wangl 2008.09.22
 * @version 1.0
 */
public class REGPatAdmControl extends TControl {
	// ��������
	private Pat pat;
	private Pat crmPat;
	private TParm mem = new TParm(); // ��Ա���� huangtt
	private TParm memTrade = new TParm(); // ��Ա���ױ���� huangtt
	// �ҺŶ���
	private Reg reg;
	// �ż���
	private String admType = "O";
	// ԤԼʱ��
	String startTime;
	// ҽ�ƿ�����
	String ektCard;
	int selectRow = -1;
	String tredeNo;
	String businessNo; // �Һų������⳷������
	String tradeNoT;
	String endInvNo;
	private TParm p3; // ҽ��������
	private boolean feeShow = false; // =====pangben 20110815 ҽ�����Ļ�÷��ùܿ�
	private boolean txEKT = false; // ̩��ҽ�ƿ�����ִ��ֱ��д������=====pangben 20110916
	private String ektOldSum; // ҽ�ƿ�����ʧ�ܻ�д���
	private String ektNewSum; // �ۿ��Ժ�Ľ��
	// ������Ϣ���

	private TParm insParm; // ҽ�����Σ�U ���� A ��������

	private boolean tjINS = false; // ���ҽ���ܿأ��ж��Ƿ�ִ����ҽ�ƿ�����

	private boolean insFlg = false; // ҽ���������ɹ��ܿ�
	// private String caseNo; // ҽ������ˢ��ʱ��Ҫ�����
	private TParm regionParm; // ���ҽ���������
	// zhangp 20111227
	private TParm parmSum; // ִ�г�ֵ��������
	private boolean printBil = false; // ��ӡƱ��ʱʹ��
	private TParm reSetEktParm; // ҽ�ƿ��˷�ʹ���ж��Ƿ�ִ��ҽ�ƿ��˷Ѳ���
	private String confirmNo; // ҽ��������ţ��˹�ʱʱʹ��
	private String reSetCaseNo; // �˹�ʹ�þ������
	private String insType; // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ����� �˹�ʹ��
	private boolean tableFlg = false; // ��һ��ҳǩ��ȫ������� ��ý���ܿ�
	private double ins_amt = 0.00; // ҽ�����
	private boolean ins_exe = false; // �ж��Ƿ�ҽ��ִ�� ������ִ�в���������ʱʵ����;״̬
	private TParm greenParm = null;// //��ɫͨ��ʹ�ý��
	private double accountamtforreg = 0.00;// �����˻�
	private String preFlg = "N";// �Ƿ���Ԥ����� caowl 20131117
	private String preCaseNo;// Ԥ����鴫�صľ���� caowl 20131117
	private String preAdmDate = "";// Ԥ����鴫�ص�Ԥ��ʱ�� yanjing 20131212
	private boolean isPreFlg = false;// �Ƿ���Ԥ����鴫��ע�� yanjing 20131212
	private boolean aheadFlg = true;

	private String triageFlg = "N"; // ������˵ȼ� huangtt 20140310

	private boolean singleFlg = true; // �Һ��Ƿ���ﵥ add by huangtt 20140409
	private boolean ticketFlg = true; // �Һ��Ƿ��Ʊ add by huangtt 20131231
	private String memCode; // ��Ա������
	private TParm acceptData = new TParm(); // �Ӳ�
	private boolean crmFlg = true; // crm�ӿڿ���
	private String regCtz = ""; // �Һ����
	boolean saveFlg = false;
	private String zfCtz = ""; // �Է����
	private boolean crmRegFlg = false; // CRMԤԼ��� add by wangbin 20140807
	private String crmId = ""; // CRMԤԼ��  add by wangbin 20140807
	private SocketLink client; // Socket���Ͳ����ҹ���  add by wangbin 20140916
	private String mroRegNo;
	private String clinictypeCode = ""; //�ű𣭣�CRM�ӿ��õ� add by huangtt 20141205
	private String crmTime=""; //CRMԤԼʱ��

	// ,�����ֽ��շ�ʱû����;״̬�ж�
	/**
	 * ��ʼ������
	 */
	public void onInitParameter() {
		// add by huangtt 20140211
		// ��������

		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			admType = acceptData.getData("ADM_TYPE").toString();
		} else {

			String parmAdmType = (String) this.getParameter();
			if (parmAdmType != null && parmAdmType.length() > 0)
				admType = parmAdmType;
		}
		setValue("ADM_TYPE", admType);
		callFunction("UI|SESSION_CODE|setAdmType", admType);
		callFunction("UI|CLINICTYPE_CODE|setAdmType", admType);
		callFunction("UI|VIP_SESSION_CODE|setAdmType", admType);
		callFunction("UI|setTitle", "O".equals(admType) ? "����ҺŴ���" : "����ҺŴ���");
		callFunction("UI|ERD_LEVEL_TITLE|setVisible", false);
		callFunction("UI|ERD_LEVEL|setVisible", false);

		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
			TParm selTriageFlg = REGSysParmTool.getInstance().selectdata();
			triageFlg = selTriageFlg.getValue("TRIAGE_FLG", 0);
			if ("N".equals(triageFlg))
				callFunction("UI|ERD_LEVEL|setEnabled", false);
			setValue("ADM_DATE", SystemTool.getInstance().getDate());
			String sessionCode = initSessionCode();
			Timestamp admDate = TJDODBTool.getInstance().getDBTime();
			// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
			if (!StringUtil.isNullString(sessionCode)
					&& !StringUtil.isNullString(admType)) {
				admDate = SessionTool.getInstance().getDateForSession(admType,
						sessionCode, Operator.getRegion());
				this.setValue("ADM_DATE", admDate);
			}
			// add by huangtt 20140504
			TComboBox ctz1 = new TComboSysCtz();
			callFunction("UI|CTZ1_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ1_CODE|setEmgFitFlg", "Y");
			ctz1.onQuery();
			TComboBox ctz2 = new TComboSysCtz();
			callFunction("UI|CTZ2_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ2_CODE|setEmgFitFlg", "Y");
			ctz2.onQuery();
			TComboBox ctz3 = new TComboSysCtz();
			callFunction("UI|CTZ3_CODE|setOpdFitFlg", "");
			callFunction("UI|CTZ3_CODE|setEmgFitFlg", "Y");
			ctz3.onQuery();
			TTextFormat ctzReg1 = new TextFormatSYSCtz();
			callFunction("UI|REG_CTZ1|setOpdFitFlg", "");
			callFunction("UI|REG_CTZ1|setEmgFitFlg", "Y");
			ctzReg1.onQuery();
			TComboBox ctzReg2 = new TComboSysCtz();
			callFunction("UI|REG_CTZ2|setOpdFitFlg", "");
			callFunction("UI|REG_CTZ2|setEmgFitFlg", "Y");
			ctzReg2.onQuery();

		}
		// ��ʼ������Combo
		callFunction("UI|DEPT_CODE|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		// ��ʼ������(��ͨ��sort)Combo
		callFunction("UI|DEPT_CODE_SORT|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������

	}

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();

		// ��ʼ��ʱ��Combo,ȡ��Ĭ��ʱ��
		initSession();
		setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110421 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		// ============huangtt modify 20131106 start ֤������Ĭ����ʾ���֤
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE CHN_DESC = '���֤' AND GROUP_ID = 'SYS_IDTYPE'";
		TParm typeParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ID_TYPE", typeParm.getValue("ID", 0));
		sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE CTZ_DESC='�Է�'";
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(sql));
		zfCtz = ctzParm.getValue("CTZ_CODE", 0);
		// ============huangtt modify 20131106 end
		
		// ��ʼ��Ĭ��(�ֳ�)�Һŷ�ʽ
		setValue("REGMETHOD_CODE", "A");
		// ��ʼ��ID�������
		// onClickRadioButton();
		this.onClear();
		initSchDay();
		// ��ʼ��ԤԼ��Ϣ��ʼʱ��
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// ��ʼ��VIP���Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// ���˹�,������ťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|NHI_NO|setEnabled", false); // ҽ�������ɱ༭
		// // ��ʼ��������
		// TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		// if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
		// setValue("VISIT_CODE_F", "Y");
		// callFunction("UI|MR_NO|setEnabled", true);
		// }

		// ===zhangp 20120306 modify end
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		// this.onClear();
		// ======zhangp 20120224 modify start
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// ======zhangp 20120224 modify end
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();

		ticketFlg = REGSysParmTool.getInstance().selTicketFlg();
		singleFlg = REGSysParmTool.getInstance().selSingleFlg();
		crmFlg = StringTool.getBoolean(TConfig.getSystemValue("crm.switch"));
		// if(ticketFlg){
		// // ��ʼ����һƱ��
		// BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// endInvNo = invoice.getEndInvno();
		// // ===zhangp 20120306 modify start
		// if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
		// Operator.getRegion(), invoice.getUpdateNo())) {
		// setValue("NEXT_NO", invoice.getUpdateNo());
		// } else {
		// messageBox("Ʊ��������");
		// }
		// }

		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			acceptData = (TParm) obj;
			String mrNo = acceptData.getData("MR_NO").toString();
			this.setValue("MR_NO", mrNo);
			this.onQueryNO();
		}
	}

	/**
	 * ��ʼ�����
	 */
	public void initSchDay() {
		new Thread() {
			// �߳�,Ϊ��ʡʱ����ߴ򿪹Һ�������Ч��
			public void run() {
				// ��ʼ��Ĭ��֧����ʽ
				// ===zhangp ���޸�֧����ʽ 20130517
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// ��ʼ������ҽʦ�Ű�
				onQueryDrTable();

				// ��ʼ������VIP���
				onQueryVipDrTable();
			}
		}.start();

	}

	/**
	 * ���Ӷ�Table1�ļ���
	 */
	public void onTable1Clicked() {
		if(pat == null){
			this.messageBox("�޲�����Ϣ");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|Table1|getClickedRow");
		if (row < 0)
			return;
		// =====20130507 yanjing ��Ӳ�ѯ�հ�����ݿ��ж϶�Ӧ��Ϣ�Ƿ����
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		String admDate = parm.getValue("ADM_DATE").substring(0, 4)
				+ parm.getValue("ADM_DATE").substring(5, 7)
				+ parm.getValue("ADM_DATE").substring(8, 10);
		parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO", row));
		clinictypeCode = tableParm.getValue("CLINICTYPE_CODE", row); //add by huangtt 20141205
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount() <= 0) {
			callFunction("UI|SAVE_REG|setEnabled", false);// �շѰ�ť���ɱ༭=====yanjing
			this.messageBox("���ڡ�ҽʦ��������Ϣ��һ�£���ˢ�½��棡");
			return;
		}
		// =======20130507 yanjing end
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 ɾ���˴���������ش���
		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}
		getCTZ();
		// ����Ʊ����
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			// zhangp
			if (aheadFlg) {
				onClickClinicType(true);
			} else {
				onClickClinicType(false);
			}
		}
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		// ���˹Ұ�ť���ɱ༭
		callFunction("UI|unreg|setEnabled", false);
		// �ò�ӡ��ť���ɱ༭
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // ��һ��ҳǩ�ܿ�
		this.grabFocus("FeeS");

	}
	
	/**
	 * ���Ӷ�Table1�ļ���
	 */
	public void onTable1Clicked(int row) {
		if(pat == null){
			this.messageBox("�޲�����Ϣ");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		
		// =====20130507 yanjing ��Ӳ�ѯ�հ�����ݿ��ж϶�Ӧ��Ϣ�Ƿ����
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		String admDate = parm.getValue("ADM_DATE").substring(0, 4)
				+ parm.getValue("ADM_DATE").substring(5, 7)
				+ parm.getValue("ADM_DATE").substring(8, 10);
		parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO", tableParm.getValue("CLINICROOM_NO", row));
		clinictypeCode = tableParm.getValue("CLINICTYPE_CODE", row); //add by huangtt 20141205
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount() <= 0) {
			callFunction("UI|SAVE_REG|setEnabled", false);// �շѰ�ť���ɱ༭=====yanjing
			this.messageBox("���ڡ�ҽʦ��������Ϣ��һ�£���ˢ�½��棡");
			return;
		}
		// =======20130507 yanjing end
		// add by wangbin 20140807 ���������жϵ�ǰ�����Ƿ���CRMԤԼ�ı�� START
		// �Զ�����ĺű�������ǿգ���˵����CRMԤԼ�Ĳ���
		if (!"".equals(this.getValueString("CLINICTYPE_CODE"))) {
			crmRegFlg = true;
		}
		// add by wangbin 20140807 ���������жϵ�ǰ�����Ƿ���CRMԤԼ�ı�� END
//		clinictypeCode = parm.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 ɾ���˴���������ش���
		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}
		getCTZ();
		// ����Ʊ����
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			// zhangp
			if (aheadFlg) {
				onClickClinicType(true);
			} else {
				onClickClinicType(false);
			}
		}
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		// ���˹Ұ�ť���ɱ༭
		callFunction("UI|unreg|setEnabled", false);
		// �ò�ӡ��ť���ɱ༭
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // ��һ��ҳǩ�ܿ�
		this.grabFocus("FeeS");

	}
	
	

	/**
	 * ���Ӷ�Talbe2�ļ����¼�
	 */
	public void onTable2Clicked() {
		if(pat == null){
			this.messageBox("�޲�����Ϣ");
			return;
		}
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		int row = (Integer) callFunction("UI|Table2|getClickedRow");
		if (row < 0)
			return;
		// �õ�table�ؼ�
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("��ռ��!");
			callFunction("UI|table2|clearSelection");
			return;
		}
		// =====�ѹ���pangben 2012-3-26 start

		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// ϵͳ��ǰʱ��
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// ϵͳ��ǰ����
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// ��ǰ�Һ�����
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("�Ѿ����ﲻ���ԹҺ�");
			callFunction("UI|table2|clearSelection");
			return;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("�Ѿ����ﲻ���ԹҺ�");
				callFunction("UI|table2|clearSelection");
				return;
			}
		}
		// =====�ѹ���pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);
		clinictypeCode = data.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		getCTZ();
		onClickClinicType(true);

		this.grabFocus("FeeS");
		// //add by huangtt 20140303
		// if(!this.getValueString("MR_NO").equals("")){
		// double clinicFee = this.getValueDouble("FeeY");
		// double currentBalance =
		// EKTpreDebtTool.getInstance().getEkeMaster(this.getValueString("MR_NO"));
		// String sqlCtz =
		// "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"+this.getValueString("REG_CTZ1")+"'";
		// TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
		// double overdraft = 0;
		// if(parmCtz.getCount()>0){
		// overdraft = parmCtz.getDouble("OVERDRAFT", 0);
		// }
		// // double currentBalance =2;
		// if(currentBalance+overdraft<clinicFee){
		// this.messageBox("ҽ�ƿ����С������");
		// }
		// }
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
	}

	/**
	 * ���Ӷ�Talbe2�ļ����¼�
	 */
	public boolean onTable2Clicked(int row) {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		// int row = (Integer) callFunction("UI|Table2|getClickedRow");
		// if (row < 0)
		// return;
		// �õ�table�ؼ�
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("�ú���ԤԼռ�ţ��뱨��!");
			callFunction("UI|table2|clearSelection");
			return false;
		}
		// =====�ѹ���pangben 2012-3-26 start

		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// ϵͳ��ǰʱ��
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// ϵͳ��ǰ����
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// ��ǰ�Һ�����
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("�Ѿ����ﲻ���ԹҺ�");
			callFunction("UI|table2|clearSelection");
			return true;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("�Ѿ����ﲻ���ԹҺ�");
				callFunction("UI|table2|clearSelection");
				return true;
			}
		}
		
		// =====�ѹ���pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);
		
		// add by wangbin 20140807 ���������жϵ�ǰ�����Ƿ���CRMԤԼ�ı�� START
		// �Զ�����ĺű�������ǿգ���˵����CRMԤԼ�Ĳ���
		if (!"".equals(this.getValueString("CLINICTYPE_CODE"))) {
			crmRegFlg = true;
		}
		// add by wangbin 20140807 ���������жϵ�ǰ�����Ƿ���CRMԤԼ�ı�� END
		clinictypeCode = data.getValue("CLINICTYPE_CODE", row);  //add by huangtt 20141205
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		getCTZ();
		onClickClinicType(true);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", true);
		setControlEnabled(false);
		this.grabFocus("FeeS");
		return true;
		// //add by huangtt 20140303
		// if(!this.getValueString("MR_NO").equals("")){
		// double clinicFee = this.getValueDouble("FeeY");
		// double currentBalance =
		// EKTpreDebtTool.getInstance().getEkeMaster(this.getValueString("MR_NO"));
		// String sqlCtz =
		// "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"+this.getValueString("REG_CTZ1")+"'";
		// TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
		// double overdraft = 0;
		// if(parmCtz.getCount()>0){
		// overdraft = parmCtz.getDouble("OVERDRAFT", 0);
		// }
		// // double currentBalance =2;
		// if(currentBalance+overdraft<clinicFee){
		// this.messageBox("ҽ�ƿ����С������");
		// }
		// }
		
	}

	/**
	 * ���Ӷ�Talbe3�ļ����¼�
	 */
	public void onTable3Clicked() {
		int row = (Integer) callFunction("UI|Table3|getSelectedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		TParm parm = table3.getParmValue();
		// System.out.println("�˹���Ϣ" + parm);
		// parm.getValue("ARRIVE_FLG",row);������ȡ��
//		String arriveFlg = (String) table3.getValueAt(row, 7);
		String arriveFlg = parm.getValue("ARRIVE_FLG", row);
		
		String isPreOrder = parm.getValue("IS_PRE_ORDER", row);
		String crmFlg = parm.getValue("CRM_FLG", row);// CRM���
		if (crmFlg.equals("Y")) {
			this.messageBox("������ΪCRMԤԼ��Ϣ");
			callFunction("UI|unreg|setEnabled", false);
			callFunction("UI|arrive|setEnabled", false);
			callFunction("UI|print|setEnabled", false);
			return;
		}
		if (isPreOrder.equals("Y")) {// Ԥ������ԤԼ��
			String admdate = "";// yanjing 20131212
			admdate = parm.getValue("ADM_DATE", row);
			if (admdate.equals(null) || "".equals(admdate)) {
				admdate = "";
			} else {
				admdate = admdate.substring(0, 10);
				String date = SystemTool.getInstance().getDate().toString();
				date = date.substring(0, 10);
				if (!admdate.equals(date)) {
					messageBox("�ǵ��գ����ܱ�����");
					return;
				}
			}
			this.preFlg = "Y";
			this.preCaseNo = parm.getValue("CASE_NO", row);
			this.preAdmDate = parm.getValue("ADM_DATE", row);
			setValueForParm("ADM_DATE;CONTRACT_CODE;REGMETHOD_CODE", parm, row);
			// �ñ�����ť���ɱ༭
			callFunction("UI|arrive|setEnabled", false);
			callFunction("UI|unreg|setEnabled", true);// ���˹Ұ�ť�ɱ༭
		} else {
			// �ж��Ƿ�ԤԼ�Һ�
			if ("N".equals(arriveFlg)) {
				if(parm.getValue("PAT_PACKAGE",row)!=null && !parm.getValue("PAT_PACKAGE",row).equals("")){
					TParm packageParm = new TParm(TJDODBTool.getInstance().select("SELECT PACKAGE_DESC FROM MEM_PACKAGE WHERE PACKAGE_CODE='"+parm.getValue("PAT_PACKAGE",row)+"'"));
					this.setValue("PAT_PACKAGE", packageParm.getValue("PACKAGE_DESC",0).toString());
				}
				setValueForParm(
						"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE;INSURE_INFO;PAT_PACKAGE;REASSURE_FLG",
						parm, row);

				// setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
				setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
				setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
				getCTZ();
				onClickClinicType(true);
				// onDateReg();
				callFunction("UI|CLINICROOM_NO|onQuery");
				// �ñ�����ť�ɱ༭
				callFunction("UI|arrive|setEnabled", true);
				// setValue("FeeY", parm.getValue("ARRIVE_FLG", row));
				// setValue("FeeS", parm.getValue("ARRIVE_FLG", row));
				this.messageBox(getValue("PAT_NAME") + "��ԤԼ��Ϣ");
				// ���շѰ�ť���ɱ༭
				// ===zhangp 20120306 modify start
				callFunction("UI|SAVE_REG|setEnabled", false);
				// ===zhangp 20120306 modify end
				// ���˹Ұ�ťΪ��
				callFunction("UI|unreg|setEnabled", true);

			} else {
				// System.out.println("�ѹ���Ϣ:::"+parm);
				// this.messageBox_("�ѹ���Ϣ"+parm);
				if(parm.getValue("PAT_PACKAGE",row)!=null && !parm.getValue("PAT_PACKAGE",row).equals("")){
					TParm packageParm = new TParm(TJDODBTool.getInstance().select("SELECT PACKAGE_DESC FROM MEM_PACKAGE WHERE PACKAGE_CODE='"+parm.getValue("PAT_PACKAGE",row)+"'"));
					this.setValue("PAT_PACKAGE", packageParm.getValue("PACKAGE_DESC",0).toString());
				}
				setValueForParm(
						"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE;INSURE_INFO;REASSURE_FLG",
						parm, row);
				
				setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
				setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
				setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
				setValue("REQUIREMENT", parm.getValue("REQUIREMENT", row)); // add
				// by
				// huangtt
				// 20131121
				callFunction("UI|DEPT_CODE|onQuery");
				callFunction("UI|DR_CODE|onQuery");
				callFunction("UI|CLINICROOM_NO|onQuery");
				callFunction("UI|CLINICTYPE_CODE|onQuery");
				// onClickClinicType( -1);
				// ==================pangben modify 20110815 �޸Ļ��Ʊ�ݱ��еļ۸���ʾ������
				unregFeeShow(parm.getValue("CASE_NO", row));
				setValueForParm(
						"CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
						parm, row);
				// �ñ�����ť���ɱ༭
				callFunction("UI|arrive|setEnabled", false);
				// ���շѰ�ť���ɱ༭
				callFunction("UI|SAVE_REG|setEnabled", false);
				// ���˹Ұ�ť�ɱ༭
				callFunction("UI|unreg|setEnabled", true);
				// �ò�ӡ��ť�ɱ༭
				callFunction("UI|print|setEnabled", true);
			}
		}
		setControlEnabled(false);
		callFunction("UI|INSURE_INFO|setEnabled", false);
		callFunction("UI|PAT_PACKAGE|setEnabled", false);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
	}

	/**
	 * �����ע���¼�
	 */
	public void onSelForeieignerFlg() {
		if (this.getValue("FOREIGNER_FLG").equals("Y"))
			this.grabFocus("BIRTH_DATE");
		if (this.getValue("FOREIGNER_FLG").equals("N"))
			this.grabFocus("IDNO");
	}

	/**
	 * ������״̬
	 */
	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
			callFunction("UI|MR_NO|setEnabled", false);
			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		this.onClear();
	}

	/**
	 * ���没����Ϣ
	 */
	public void onSavePat() {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		// ���������ֵ
		//if(this.getValueString("MR_NO").equals("")){//����ʱ���������ڲ����޸�moidfy by huangjw 20150721
			if (getValue("BIRTH_DATE") == null) {
				this.messageBox("�������ڲ���Ϊ��!");
				return;
			}
		//}
		// add by huangtt 20140320
		if (getValueString("PAT_NAME").equals("")) {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
				if (this.messageBox("������ֵ", "�Ƿ�firstName��lastName�ϲ���ֵ��������", 0) != 0) {
					this.messageBox("��������Ϊ��!");
					return;
				}
				this.setValue("PAT_NAME", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));

			} else if (getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				//if(this.getValueString("MR_NO").equals("")){//����ʱ�����������޸�moidfy by huangjw 20150721
					this.messageBox("��������Ϊ��!");
					this.grabFocus("PAT_NAME");
					return;
				//}
			}
		} else {
			if (getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.messageBox("������firstName!");
				this.grabFocus("FIRST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& getValueString("LAST_NAME").equals("")) {
				this.messageBox("������lastName!");
				this.grabFocus("LAST_NAME");
				return;
			} else if (!getValueString("FIRST_NAME").equals("")
					&& !getValueString("LAST_NAME").equals("")) {
				this.setValue("PAT_NAME1", getValueString("FIRST_NAME") + " "
						+ getValueString("LAST_NAME"));
			}
		}
		//if(this.getValueString("MR_NO").equals("")){//����ʱ���Ա����޸�moidfy by huangjw 20150721
			if (getValue("SEX_CODE") == null
					|| getValue("SEX_CODE").toString().length() <= 0) {
				this.messageBox("�Ա���Ϊ��!");
				this.grabFocus("SEX_CODE");
				return;
			}
		//}
		if(!getValue("ID_TYPE").toString().equals("99")){
        	if (getValue("IDNO") == null || getValue("IDNO").toString().length()<=0) {
                this.messageBox("֤���Ų���Ϊ��!");
                this.grabFocus("IDNO");
                return ;
            }
        }
		
		if (getValue("CTZ1_CODE") == null
				|| getValue("CTZ1_CODE").toString().length() <= 0) {
			this.messageBox("���һ����Ϊ��!");
			this.grabFocus("CTZ1_CODE");
			return;
		}

//		if (!this.emptyTextCheck("SEX_CODE,CTZ1_CODE,ID_TYPE"))
//			return;

		pat = new Pat();
		// ��������
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// Ӣ����
		pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
		// ����ƴ��
		pat.setPy1(TypeTool.getString(getValue("PY1")));
		// ֤������
		pat.setIdType(TypeTool.getString(getValue("ID_TYPE"))); // add by
		// huangtt
		// 20131106
		// ���֤��
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// �����ע��
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
		// ��������
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// �Ա�
		pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
		// �绰
		// pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
		pat.setCellPhone(TypeTool.getString(getValue("CELL_PHONE")));
		// �ʱ�
		pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
		// ��ַ
		pat.setResidAddress(TypeTool.getString(getValue("RESID_ADDRESS")));
		// ��סַ
		pat.setCurrentAddress(TypeTool.getString(getValue("CURRENT_ADDRESS"))); // add
		// by
		// huangtt
		// 20131106
		pat.setAddress(TypeTool.getString(getValue("CURRENT_ADDRESS"))); // add
		// by
		// huangtt
		// 20131106
		// ���1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// ���2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// ���3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// ҽ��������
		pat.setNhiNo(TypeTool.getString("")); // =============pangben
		// ��ע
		pat.setRemarks(TypeTool.getString(getValue("REMARKS"))); // add by
		// huangtt
		// 20131106
		pat.setNationCode(TypeTool.getString(getValue("NATION_CODE")));
		pat.setSpeciesCode(TypeTool.getString(getValue("SPECIES_CODE")));
		pat.setMarriageCode(TypeTool.getString(getValue("MARRIAGE_CODE")));
		pat.setFirstName(TypeTool.getString(getValue("FIRST_NAME")));
		pat.setLastName(TypeTool.getString(getValue("LAST_NAME")));
		// modify
		// 20110808
		if (this.messageBox("������Ϣ", "�Ƿ񱣴�", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
		patParm.setData("LAST_NAME", getValue("LAST_NAME"));
		patParm.setData("FIRST_NAME", getValue("FIRST_NAME"));
		patParm.setData("PY1", getValue("PY1"));
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("CELL_PHONE", getValue("CELL_PHONE"));
		patParm.setData("SEX_CODE", getValue("SEX_CODE"));
		patParm.setData("POST_CODE", getValue("POST_CODE"));
		patParm.setData("RESID_ADDRESS", getValue("RESID_ADDRESS"));
		patParm.setData("ADDRESS", getValue("CURRENT_ADDRESS"));
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO", ""); // =============pangben
		patParm.setData("ID_TYPE", getValue("ID_TYPE")); // add by huangtt
		// 20131106
		patParm.setData("CURRENT_ADDRESS", getValue("CURRENT_ADDRESS")); // add
		// by
		// huangtt
		// 20131106
		patParm.setData("REMARKS", getValue("REMARKS")); // add by huangtt
		// 20131106
		patParm.setData("NATION_CODE", getValueString("NATION_CODE"));
		patParm.setData("SPECIES_CODE", getValueString("SPECIES_CODE"));
		patParm.setData("MARRIAGE_CODE", getValueString("MARRIAGE_CODE"));

		// modify 20110808
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME1").toString())) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("LAST_NAME").toString())) {
			patParm.setData("LAST_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("FIRST_NAME").toString())) {
			patParm.setData("FIRST_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PY1").toString())) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString("" + getValue("CELL_PHONE"))) {
			patParm.setData("CELL_PHONE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX_CODE").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("POST_CODE").toString())) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("RESID_ADDRESS").toString())) {
			patParm.setData("RESID_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		// =============pangben modify 20110808
		// if (StringUtil.isNullString(getValue("NHI_NO").toString())) {
		// patParm.setData("NHI_NO", new TNull(String.class));
		// }
		// ====huangtt 20131106 start
		if (StringUtil.isNullString(getValue("ID_TYPE").toString())) {
			patParm.setData("ID_TYPE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CURRENT_ADDRESS").toString())) {
			patParm.setData("CURRENT_ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CURRENT_ADDRESS").toString())) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("REMARKS").toString())) {
			patParm.setData("REMARKS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("NATION_CODE").toString())) {
			patParm.setData("NATION_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SPECIES_CODE").toString())) {
			patParm.setData("SPECIES_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("MARRIAGE_CODE").toString())) {
			patParm.setData("MARRIAGE_CODE", new TNull(String.class));
		}
		// ====huangtt 20131106 end
		TParm result = new TParm();
		// ===zhangp 20120613 start
		// if ("Y".equals(getValue("VISIT_CODE_F"))) {
		if (!"".equals(getValueString("MR_NO"))) {
			// ===zhangp 20120613 end
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("���ȼ���������");
				return;
			}
			// ���²���
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());

			TParm parmMem = new TParm();
			parmMem.setData("MR_NO", pat.getMrNo());
			parmMem.setData("CUSTOMER_SOURCE", this
					.getValueString("CUSTOMER_SOURCE"));
			parmMem.setData("GUARDIAN1_NAME", this
					.getValueString("GUARDIAN_NAME"));
			parmMem.setData("GUARDIAN1_RELATION", this
					.getValueString("GUARDIAN_RELATION"));
			if (mem.getCount() > 0) {
				result = MEMTool.getInstance()
						.updateMemPatInfoGuardian(parmMem);
			} else {
				result = MEMTool.getInstance().insertMemPatInfo(parmMem);
			}

			if (crmFlg) {
				// System.out.println("patParm==="+patParm);
				// add by huangtt 20140401 CRM----start
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", "<TNULL>".equals(patParm
						.getValue("PAT_NAME")) ? "" : patParm
						.getValue("PAT_NAME"));
				parm.setData("PY1",
						"<TNULL>".equals(patParm.getValue("PY1")) ? ""
								: patParm.getValue("PY1"));
				parm.setData("FIRST_NAME", "<TNULL>".equals(patParm
						.getValue("FIRST_NAME")) ? "" : patParm
						.getValue("FIRST_NAME"));
				parm.setData("LAST_NAME", "<TNULL>".equals(patParm
						.getValue("LAST_NAME")) ? "" : patParm
						.getValue("LAST_NAME"));
				parm.setData("OLDNAME", crmPat.getOldName());
				parm.setData("ID_TYPE", "<TNULL>".equals(patParm
						.getValue("ID_TYPE")) ? "" : patParm
						.getValue("ID_TYPE"));
				parm.setData("IDNO",
						"<TNULL>".equals(patParm.getValue("IDNO")) ? ""
								: patParm.getValue("IDNO"));
				parm.setData("SEX_CODE", "<TNULL>".equals(patParm
						.getValue("SEX_CODE")) ? "" : patParm
						.getValue("SEX_CODE"));
				parm.setData("BIRTH_DATE", "<TNULL>".equals(patParm
						.getValue("BIRTH_DATE")) ? "" : patParm
						.getValue("BIRTH_DATE"));
				parm.setData("NATION_CODE", "<TNULL>".equals(patParm
						.getValue("NATION_CODE")) ? "" : patParm
						.getValue("NATION_CODE"));
				parm.setData("NATION_CODE2", "<TNULL>".equals(patParm
						.getValue("SPECIES_CODE")) ? "" : patParm
						.getValue("SPECIES_CODE"));
				parm.setData("RELIGION", crmPat.getReligionCode());
				parm.setData("MARRIAGE", "<TNULL>".equals(patParm
						.getValue("MARRIAGE_CODE")) ? "" : patParm
						.getValue("MARRIAGE_CODE"));
				parm.setData("RESID_POST_CODE", crmPat.getResidPostCode());
				parm.setData("RESID_ADDRESS", "<TNULL>".equals(patParm
						.getValue("RESID_ADDRESS")) ? "" : patParm
						.getValue("RESID_ADDRESS"));
				parm.setData("POST_CODE", "<TNULL>".equals(patParm
						.getValue("POST_CODE")) ? "" : patParm
						.getValue("POST_CODE"));
				parm.setData("CURRENT_ADDRESS", "<TNULL>".equals(patParm
						.getValue("CURRENT_ADDRESS")) ? "" : patParm
						.getValue("CURRENT_ADDRESS"));
				parm.setData("HOMEPLACE_CODE", crmPat.gethomePlaceCode());
				parm.setData("BIRTH_HOSPITAL", mem
						.getValue("BIRTH_HOSPITAL", 0));
				parm.setData("SCHOOL_NAME", mem.getValue("SCHOOL_NAME", 0));
				parm.setData("SCHOOL_TEL", mem.getValue("SCHOOL_TEL", 0));
				parm.setData("SOURCE", mem.getValue("SOURCE", 0));
				parm.setData("INSURANCE_COMPANY1_CODE", mem.getValue(
						"INSURANCE_COMPANY1_CODE", 0));
				parm.setData("INSURANCE_COMPANY2_CODE", mem.getValue(
						"INSURANCE_COMPANY2_CODE", 0));
				parm.setData("INSURANCE_NUMBER1", mem.getValue(
						"INSURANCE_NUMBER1", 0));
				parm.setData("INSURANCE_NUMBER2", mem.getValue(
						"INSURANCE_NUMBER2", 0));
				parm.setData("GUARDIAN1_NAME", mem
						.getValue("GUARDIAN1_NAME", 0));
				parm.setData("GUARDIAN1_RELATION", mem.getValue(
						"GUARDIAN1_RELATION", 0));
				parm.setData("GUARDIAN1_TEL", mem.getValue("GUARDIAN1_TEL", 0));
				parm.setData("GUARDIAN1_PHONE", mem.getValue("GUARDIAN1_PHONE",
						0));
				parm.setData("GUARDIAN1_COM", mem.getValue("GUARDIAN1_COM", 0));
				parm.setData("GUARDIAN1_ID_TYPE", mem.getValue(
						"GUARDIAN1_ID_TYPE", 0));
				parm.setData("GUARDIAN1_ID_CODE", mem.getValue(
						"GUARDIAN1_ID_CODE", 0));
				parm.setData("GUARDIAN1_EMAIL", mem.getValue("GUARDIAN1_EMAIL",
						0));
				parm.setData("GUARDIAN2_NAME", mem
						.getValue("GUARDIAN2_NAME", 0));
				parm.setData("GUARDIAN2_RELATION", mem.getValue(
						"GUARDIAN2_RELATION", 0));
				parm.setData("GUARDIAN2_TEL", mem.getValue("GUARDIAN2_TEL", 0));
				parm.setData("GUARDIAN2_PHONE", mem.getValue("GUARDIAN2_PHONE",
						0));
				parm.setData("GUARDIAN2_COM", mem.getValue("GUARDIAN2_COM", 0));
				parm.setData("GUARDIAN2_ID_TYPE", mem.getValue(
						"GUARDIAN2_ID_TYPE", 0));
				parm.setData("GUARDIAN2_ID_CODE", mem.getValue(
						"GUARDIAN2_ID_CODE", 0));
				parm.setData("GUARDIAN2_EMAIL", mem.getValue("GUARDIAN2_EMAIL",
						0));
				parm.setData("REG_CTZ1_CODE", mem.getValue("REG_CTZ1_CODE", 0));
				parm.setData("REG_CTZ2_CODE", mem.getValue("REG_CTZ2_CODE", 0));
				parm.setData("FAMILY_DOCTOR", mem.getValue("FAMILY_DOCTOR", 0));
				parm.setData("ACCOUNT_MANAGER_CODE", mem.getValue(
						"ACCOUNT_MANAGER_CODE", 0));
				parm.setData("MEM_TYPE", mem.getValue("MEM_CODE", 0));
				parm.setData("START_DATE", "".equals(mem.getValue("START_DATE",
						0)) ? "" : mem.getValue("START_DATE", 0).substring(0,
						10));
				parm.setData("END_DATE",
						"".equals(mem.getValue("END_DATE", 0)) ? "" : mem
								.getValue("END_DATE", 0).substring(0, 10));
				String sDate = mem.getValue("START_DATE", 0);
				String eDate = mem.getValue("END_DATE", 0);
				Timestamp date = SystemTool.getInstance().getDate();
				if (sDate.length() > 0 && eDate.length() > 0) {
					// ���㹺������
					int buyMonthAge = getBuyMonth(sDate.substring(0, 10)
							.replaceAll("-", ""), eDate.substring(0, 10)
							.replaceAll("-", ""));

					// ��������
					int currMonthAge = getBuyMonth(sDate.substring(0, 10)
							.replaceAll("-", ""), date.toString().substring(0,
							10).replaceAll("-", ""));

					parm.setData("BUY_MONTH_AGE", String.valueOf(buyMonthAge));
					parm.setData("HAPPEN_MONTH_AGE", String
							.valueOf(currMonthAge));
				} else {
					parm.setData("BUY_MONTH_AGE", "");
					parm.setData("HAPPEN_MONTH_AGE", "");
				}
				parm.setData("MEM_CODE", memTrade.getValue("MEM_CODE", 0));
				parm.setData("REASON", memTrade.getValue("REASON", 0));
				parm.setData("START_DATE_TRADE", "".equals(memTrade.getValue(
						"START_DATE", 0)) ? "" : memTrade.getValue(
						"START_DATE", 0).substring(0, 10));
				parm.setData("END_DATE_TRADE", "".equals(memTrade.getValue(
						"END_DATE", 0)) ? "" : memTrade.getValue("END_DATE", 0)
						.substring(0, 10));
				parm.setData("MEM_FEE", memTrade.getValue("MEM_FEE", 0));
				parm
						.setData("INTRODUCER1", memTrade.getValue(
								"INTRODUCER1", 0));
				parm
						.setData("INTRODUCER2", memTrade.getValue(
								"INTRODUCER2", 0));
				parm
						.setData("INTRODUCER3", memTrade.getValue(
								"INTRODUCER3", 0));
				parm
						.setData("DESCRIPTION", memTrade.getValue(
								"DESCRIPTION", 0));
				parm.setData("CTZ1_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ1_CODE")) ? "" : patParm
						.getValue("CTZ1_CODE"));
				parm.setData("CTZ2_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ2_CODE")) ? "" : patParm
						.getValue("CTZ2_CODE"));
				parm.setData("CTZ3_CODE", "<TNULL>".equals(patParm
						.getValue("CTZ3_CODE")) ? "" : patParm
						.getValue("CTZ3_CODE"));
				parm.setData("SPECIAL_DIET", crmPat.getSpecialDiet().getValue());
				parm.setData("E_MAIL", crmPat.getEmail());
				parm.setData("TEL_HOME", crmPat.getTelHome());
				parm.setData("CELL_PHONE", "<TNULL>".equals(patParm
						.getValue("CELL_PHONE")) ? "" : patParm
						.getValue("CELL_PHONE"));

				System.out.println("CRM��Ϣ����ͬ��===" + parm);
				TParm parmCRM = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "updateMemberByMrNo1", parm);

				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
				}
				// add by huangtt 20140401 CRM----end
			}

		} else {
			// ��������
			// pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
			saveFlg = true;
			// add by huangtt 20140210
			if (!this.getValueString("GUARDIAN_NAME").equals("")
					|| !this.getValueString("GUARDIAN_RELATION").equals("")
					|| !this.getValueString("CUSTOMER_SOURCE").equals("")) {
				TParm parmMem = new TParm();
				parmMem.setData("MR_NO", pat.getMrNo());
				parmMem.setData("CUSTOMER_SOURCE", this
						.getValueString("CUSTOMER_SOURCE"));
				parmMem.setData("GUARDIAN1_NAME", this
						.getValueString("GUARDIAN_NAME"));
				parmMem.setData("GUARDIAN1_RELATION", this
						.getValueString("GUARDIAN_RELATION"));
				// System.out.println("parmMEM==="+parmMem);
				result = MEMTool.getInstance().insertMemPatInfo(parmMem);

			}
			this.setValue("VISIT_CODE_C", true);

			// add by huangtt 20140401 CRM----start
			if (crmFlg) {
				TParm parm = new TParm();
				parm.setData("MR_NO", pat.getMrNo());
				parm.setData("PAT_NAME", pat.getName());
				parm.setData("PY1", pat.getPy1());
				parm.setData("FIRST_NAME", pat.getFirstName());
				parm.setData("LAST_NAME", pat.getLastName());
				parm.setData("OLDNAME", "");
				parm.setData("ID_TYPE", pat.getIdType());
				parm.setData("IDNO", pat.getIdNo());
				parm.setData("SEX_CODE", pat.getSexCode());
				parm.setData("BIRTH_DATE", pat.getBirthday());
				parm.setData("NATION_CODE", pat.getNationCode());
				parm.setData("NATION_CODE2", pat.getSpeciesCode());
				parm.setData("MARRIAGE", pat.getMarriageCode());
				parm.setData("RESID_POST_CODE", "");
				parm.setData("RESID_ADDRESS", pat.getResidAddress());
				parm.setData("POST_CODE", pat.getPostCode());
				parm.setData("CURRENT_ADDRESS", pat.getCurrentAddress());
				parm.setData("CELL_PHONE", pat.getCellPhone());
				parm.setData("SPECIAL_DIET", "");
				parm.setData("E_MAIL", "");
				parm.setData("TEL_HOME", "");
				parm.setData("CTZ1_CODE", pat.getCtz1Code());
				parm.setData("CTZ2_CODE", pat.getCtz2Code());
				parm.setData("CTZ3_CODE", pat.getCtz3Code());
				parm.setData("HOMEPLACE_CODE", "");
				parm.setData("RELIGION", "");
				parm.setData("BIRTH_HOSPITAL", "");
				parm.setData("SCHOOL_NAME", "");
				parm.setData("SCHOOL_TEL", "");
				parm.setData("SOURCE", "");
				parm.setData("INSURANCE_COMPANY1_CODE", "");
				parm.setData("INSURANCE_COMPANY2_CODE", "");
				parm.setData("INSURANCE_NUMBER1", "");
				parm.setData("INSURANCE_NUMBER2", "");
				parm.setData("GUARDIAN1_NAME", this
						.getValueString("GUARDIAN_NAME"));
				parm.setData("GUARDIAN1_RELATION", this
						.getValueString("GUARDIAN_RELATION"));
				parm.setData("GUARDIAN1_TEL", "");
				parm.setData("GUARDIAN1_PHONE", "");
				parm.setData("GUARDIAN1_COM", "");
				parm.setData("GUARDIAN1_ID_TYPE", "");
				parm.setData("GUARDIAN1_ID_CODE", "");
				parm.setData("GUARDIAN1_EMAIL", "");
				parm.setData("GUARDIAN2_NAME", "");
				parm.setData("GUARDIAN2_RELATION", "");
				parm.setData("GUARDIAN2_TEL", "");
				parm.setData("GUARDIAN2_PHONE", "");
				parm.setData("GUARDIAN2_COM", "");
				parm.setData("GUARDIAN2_ID_TYPE", "");
				parm.setData("GUARDIAN2_ID_CODE", "");
				parm.setData("GUARDIAN2_EMAIL", "");
				parm.setData("REG_CTZ1_CODE", "");
				parm.setData("REG_CTZ2_CODE", "");
				parm.setData("FAMILY_DOCTOR", "");
				parm.setData("ACCOUNT_MANAGER_CODE", "");
				parm.setData("MEM_TYPE", "");
				parm.setData("START_DATE", "");
				parm.setData("END_DATE", "");
				parm.setData("BUY_MONTH_AGE", "");
				parm.setData("HAPPEN_MONTH_AGE", "");
				parm.setData("MEM_CODE", "");
				parm.setData("REASON", "");
				parm.setData("START_DATE_TRADE", "");
				parm.setData("END_DATE_TRADE", "");
				parm.setData("MEM_FEE", "");
				parm.setData("INTRODUCER1", "");
				parm.setData("INTRODUCER2", "");
				parm.setData("INTRODUCER3", "");
				parm.setData("DESCRIPTION", "");
				// System.out.println("CRM��Ϣ����ͬ��==="+parm);
				TParm parmCRM = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "createMember1", parm);
				if (!parmCRM.getBoolean("flg", 0)) {
					this.messageBox("CRM��Ϣ����ͬ��ʧ�ܣ�");
				}
			}

			// add by huangtt 20140401 CRM----end

		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 20120112 zhangp ����֮�󽨿�
		// ===zhangp 20120309 modify start
		// if (getValueBoolean("VISIT_CODE_C")) {
		if (saveFlg) {
			ektCard();
		}
		// ===������ start
		if (Operator.getSpcFlg().equals("Y")) {
			// SYSPatinfoClientTool sysPatinfoClientTool = new
			// SYSPatinfoClientTool(
			// this.getValue("MR_NO").toString());
			// SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client
			// serviceSpcPatInfoServiceImplPortClient = new
			// SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
			// String msg = serviceSpcPatInfoServiceImplPortClient
			// .onSaveSpcPatInfo(syspat);
			// if (!msg.equals("OK")) {
			// System.out.println(msg);
			// }
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
					"action.sys.SYSSPCPatAction", "getPatName", spcParm);
		}
		// ===������ end
		this.onClear();
	}

	/**
	 * Ԥ������ѯ yanj 20131216
	 * */
	public void onPreOrder() {
		String mr_no = this.getValueString("MR_NO");
		if (mr_no == null || mr_no.equals("")) {
			this.messageBox("�����벡���ţ�");
			return;
		}
		String sql = "SELECT MR_NO,PAT_NAME,SEX_CODE,BIRTH_DATE FROM SYS_PATINFO WHERE MR_NO = '"
				+ mr_no + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount() < 0) {
			this.messageBox("���޲�����Ϣ");
			return;
		}
		String pat_name = selParm.getData("PAT_NAME", 0).toString();
		String sex_code = selParm.getData("SEX_CODE", 0).toString();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String birthDate = selParm.getData("BIRTH_DATE", 0).toString()
				.substring(0, 19).replace("-", "/");
		Timestamp birth_date = new Timestamp(Date.parse(birthDate));
		Timestamp temp = birth_date == null ? sysDate : birth_date;

		// ��������
		String age = "0";
		if (birth_date != null)
			age = OdiUtil.getInstance().showAge(temp, sysDate);
		else
			age = "";
		TParm parm = new TParm();
		parm.setData("MR_NO", mr_no);
		parm.setData("PAT_NAME", pat_name);
		parm.setData("SEX_CODE", sex_code);
		parm.setData("AGE", age);
		Object obj = openDialog("%ROOT%\\config\\opd\\OPDPatPreOrderChoose.x",
				parm);
		TParm patParm = new TParm();
		if (obj != null) {
			patParm = (TParm) obj;
			this.preFlg = "Y";
			this.preCaseNo = patParm.getValue("CASE_NO");
			this.preAdmDate = patParm.getValue("PRE_DATE").toString();
			// reg.setCaseNo(case_no);
			return;
		}
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @throws ParseException
	 */
	public void onQueryNO() {
		onClearRefresh();
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		insFlg = false; // ��ʼ��
		insType = null;// ��ʼ��
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			this.setValue("MR_NO", "");
			return;
		}
		// add by huangtt 20140410 start
		crmPat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));

		// add by huangtt 20140410 end

		// add by huangtt 20140211
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}

		setValue("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		setValue("PAT_NAME", pat.getName().trim());
		setValue("PAT_NAME1", pat.getName1());
		setValue("FIRST_NAME", pat.getFirstName());
		setValue("LAST_NAME", pat.getLastName());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("ID_TYPE", pat.getIdType()); // add by huangtt 20131106
		setValue("REMARKS", pat.getRemarks()); // add by huangtt 20131106
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress()); // add by huangtt
		// 20131106
		// setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		// onPast();
		setValue("SEX_CODE", pat.getSexCode());
		setValue("CELL_PHONE", pat.getCellPhone());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("RESID_ADDRESS", pat.getResidAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		regCtz = getValueString("CTZ1_CODE");
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		setValue("NATION_CODE", pat.getNationCode());
		setValue("SPECIES_CODE", pat.getSpeciesCode());
		setValue("MARRIAGE_CODE", pat.getMarriageCode());

		// add by huangtt 20140114 start
		TParm memParm = new TParm();
		memParm.setData("MR_NO", getValue("MR_NO"));
		TParm memInfo = MEMTool.getInstance().selectMemPatInfo(memParm);
		mem = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
		memTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
		if (mem.getCount() > 0) {
			setValue("GUARDIAN_NAME", mem.getValue("GUARDIAN1_NAME", 0));
			setValue("GUARDIAN_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
			setValue("CUSTOMER_SOURCE", mem.getValue("CUSTOMER_SOURCE", 0));
		}

		if (memInfo.getCount() > 0) {
			memCode = memInfo.getValue("MEM_CODE", 0);
		} else {
			memCode = "";
		}
		// add by huangtt 20140114 end

//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
//		// ��������Ϣ
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
//			// this.messageBox_("�����ɹ�!");//����ר��
			selPatInfoTable();
		// =======20120216 zhangp modify start
		String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
				+ pat.getMrNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
		}
		if (result.getCount() < 0) { // ���δ������ݣ�δ�ƿ������ƿ�
			if (messageBox("��ʾ", "�ò���δ����ҽ�ƿ�,�Ƿ����ҽ�ƿ�", 0) == 0) {
				ektCard(); // �ƿ�
				// ====zhangp 20120227 modify start
				this.onClear();
			}
		}
		// =======20120216 zhangp modify end
		this.grabFocus("CLINICROOM_NO");

		// add by huangtt 20140401 CRM-------start
		
		
		
		if (crmFlg) {
			if (admType.equals("O")) {
				TParm parmCRM = new TParm();
				parmCRM.setData("MR_NO", this.getValueString("MR_NO"));
				TParm order = TIOM_AppServer.executeAction(
						"action.reg.REGCRMAction", "orderInfo", parmCRM);
//				 System.out.println("CRMԤԼ��Ϣ==="+order);
				Timestamp date = SystemTool.getInstance().getDate();
				String today = date.toString().replace("-", "")
						.replace("/", "").substring(0, 8);
				TParm orderToday = new TParm();// �����ԤԼ��Ϣ
				TParm orderTomorrow = new TParm(); // ������֮���ԤԼ��Ϣ
				String startTime = StringTool.getString(TypeTool
						.getTimestamp(getValue("YY_START_DATE")), "yyyy/MM/dd");
				String endTime = StringTool.getString(TypeTool
						.getTimestamp(getValue("YY_END_DATE")), "yyyy/MM/dd");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				for (int i = 0; i < order.getCount(); i++) {
					String admDate1 = order.getValue("ADM_DATE", i).replace(
							"-", "").replace("/", "").substring(0, 8);
					String admDate2 = order.getValue("ADM_DATE", i).replace(
							"-", "/").substring(0, 10);
					if (admDate1.equals(today)) {
						orderToday.addData("CLINICTYPE_CODE", order.getValue("CLINICTYPE_CODE", i));
						orderToday.addData("CLINICTYPE_DESC", order.getValue("CLINICTYPE_DESC", i));
						orderToday.addData("DEPT_CODE", order.getValue("DEPT_CODE", i));
						orderToday.addData("DEPT_DESC", order.getValue("DEPT_DESC", i));
						orderToday.addData("DR_CODE", order.getValue("DR_CODE",i));
						orderToday.addData("DR_DESC", order.getValue("DR_DESC",i));
						orderToday.addData("ADM_DATE", order.getValue("ADM_DATE", i));
						orderToday.addData("START_TIME", order.getValue("START_TIME", i));
						// add by wangbin 2015/1/8 ����CRMID
						orderToday.addData("CRM_ID", order.getValue("CRM_ID", i));
					} else {
						try {
							if (sdf.parse(startTime).before(sdf.parse(admDate2))
									&& sdf.parse(admDate2).before(sdf.parse(endTime))) {
								orderTomorrow.addData("CLINICTYPE_CODE", order.getValue("CLINICTYPE_CODE", i));
								orderTomorrow.addData("REALDEPT_CODE", order.getValue("DEPT_CODE", i));
								orderTomorrow.addData("REALDR_CODE", order.getValue("DR_CODE", i));
								orderTomorrow.addData("ADM_DATE", order.getValue("ADM_DATE", i).replace("-","/"));
								orderTomorrow.addData("CRM_FLG", "Y");
							}
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				orderToday.setCount(orderToday.getCount("DEPT_CODE"));
				orderTomorrow.setCount(orderTomorrow.getCount("REALDEPT_CODE"));
				if (orderTomorrow.getCount() > 0) {
					TTable table3 = (TTable) callFunction("UI|table3|getThis");
					TParm parm = table3.getParmValue();
					for (int i = 0; i < orderTomorrow.getCount(); i++) {
						parm.addData("CASE_NO", "");
						parm.addData("ADM_DATE", orderTomorrow.getValue("ADM_DATE", i));
						parm.addData("SESSION_CODE", "");
						parm.addData("REALDEPT_CODE", orderTomorrow.getValue("REALDEPT_CODE", i));
						parm.addData("REALDR_CODE", orderTomorrow.getValue("REALDR_CODE", i));
						parm.addData("ADM_STATUS", "");
						parm.addData("ARRIVE_FLG", "");
						parm.addData("IS_PRE_ORDER", "");
						parm.addData("CRM_FLG", orderTomorrow.getValue("CRM_FLG", i));
						parm.addData("CONFIRM_NO", "");
						parm.addData("INS_PAT_TYPE", "");
						parm.addData("REGMETHOD_CODE", "");
						parm.addData("OLD_CASE_NO", "");
						parm.addData("TRANHOSP_CODE", "");
						parm.addData("DRG_CODE", "");
						parm.addData("CONTRACT_CODE", "");
						parm.addData("REGCAN_USER", "");
						parm.addData("PREVENT_SCH_CODE", "");
						parm.addData("CTZ2_CODE", "");
						parm.addData("HEIGHT", 0.0);
						parm.addData("REPORT_STATUS", "");
						parm.addData("CLINICROOM_NO", "");
						parm.addData("CTZ3_CODE", "");
						parm.addData("DR_CODE", "");
						parm.addData("REG_DATE", "");
						parm.addData("ADM_REGION", "");
						parm.addData("WEIGHT", "");
						parm.addData("APPT_CODE", "N");
						parm.addData("REG_ADM_TIME", "");
						parm.addData("REQUIREMENT", "");
						parm.addData("ADM_TYPE", "");
						parm.addData("DEPT_CODE", "");
						parm.addData("OPT_TERM", "");
						parm.addData("OPT_DATE", "");
						parm.addData("CLINICAREA_CODE", "");
						parm.addData("QUE_NO", "");
						parm.addData("MR_NO", "");
						parm.addData("TRIAGE_NO", "");
						parm.addData("OPT_USER", "");
						parm.addData("CLINICTYPE_CODE", orderTomorrow.getValue("CLINICTYPE_CODE", i));
						parm.addData("VIP_FLG", "");
						parm.addData("REGION_CODE", "");
						parm.addData("CTZ1_CODE", "");
						parm.addData("HEAT_FLG", "");
						parm.addData("SERVICE_LEVEL", "");
						parm.addData("VISIT_CODE", "");
						parm.addData("REGCAN_DATE", "");
						parm.addData("REASSURE_FLG", "");
					}
					table3.setParmValue(parm);
				}
				if (orderToday.getCount() > 0) {
					//add by huangtt 20141229 start CRMԤԼ�Һ�֮���ų���������ʾ����
					TTable table3 = (TTable) callFunction("UI|table3|getThis");
					TParm parm3 = table3.getParmValue();
					for (int j = 0; j < orderToday.getCount(); j++) {
						String admDate = orderToday.getValue("ADM_DATE", j).replace("-", "").replace("/", "");
						String deptCode = orderToday.getValue("DEPT_CODE", j);
						String drCode = orderToday.getValue("DR_CODE", j);
						String clinictypeCode = orderToday.getValue("CLINICTYPE_CODE", j);
						String orderTime = orderToday.getValue("START_TIME", j).replace(":", "").substring(0, 4);
						for (int j2 = 0; j2 < parm3.getCount("ADM_DATE") ; j2++) {
							if(admDate.equals(parm3.getValue("ADM_DATE", j2).replace("-", "").replace("/", "").subSequence(0, 8)) &&
									deptCode.equals(parm3.getValue("REALDEPT_CODE", j2)) && 
										drCode.equals(parm3.getValue("REALDR_CODE", j2)) && 
											clinictypeCode.equals(parm3.getValue("CLINICTYPE_CODE", j2)) && 
												orderTime.equals(parm3.getValue("REG_ADM_TIME", j2)) && 
													!parm3.getBoolean("APPT_CODE", j2)){
								orderToday.removeRow(j);
							}
						}
					}

					if(orderToday.getCount("ADM_DATE") <= 0){
						return;
					}
					//add by huangtt 20141229 end
					
					TParm orderParm = (TParm) this.openDialog(
							"%ROOT%\\config\\reg\\REGOrderInfo.x", orderToday,
							false);
					// System.out.println("���ص�ԤԼ��Ϣ��==="+orderParm);
					if (orderParm != null) {
						TTabbedPane tabbedPane = (TTabbedPane) getComponent("tTabbedPane_0");
//						tabbedPane.setSelectedIndex(1);
						tabbedPane.setSelectedIndex(0);
						this.setValue("DEPT_CODE_SORT", orderParm.getValue("deptCode"));
						this.setValue("VIP_DEPT_CODE", orderParm.getValue("deptCode"));
						this.setValue("DR_CODE_SORT", orderParm.getValue("deCode"));
						this.setValue("VIP_DR_CODE", orderParm.getValue("deCode"));
						onQueryVipDrTable();
//						TTable table = (TTable) this.getComponent("Table2");
//						table.acceptText();						
//						TParm tableParm = table.getParmValue();
						
						TTable table1 = (TTable) this.getComponent("Table1");
						table1.acceptText();
						TParm table1Parm = table1.getParmValue();
						crmTime = orderParm.getValue("time");
						
						for (int j = 0; j < parm3.getCount("CASE_NO"); j++) {
							if (parm3.getValue("ADM_DATE", j).length() > 0) {
								String admDate1 = parm3.getValue("ADM_DATE", j).replace("-","").replace("/", "").substring(0, 8);
								String admDate2 = orderParm.getValue("admDate").replace("-", "").replace("/", "").substring(0, 8);
								if (admDate1.equals(admDate2)
										&& parm3.getValue("CLINICTYPE_CODE", j).equals(orderParm.getValue("clinictypeCode"))
										&& parm3.getValue("REALDEPT_CODE", j).equals(orderParm.getValue("deptCode"))
										&& parm3.getValue("REALDR_CODE",j).equals(orderParm.getValue("deCode"))
										&& parm3.getValue("REG_ADM_TIME", j).equals(crmTime)
										&& parm3.getBoolean("APPT_CODE", j)){
									table3.setSelectedRow(j);
									onTable3Clicked();												
									return;
								}
							}
						}
						
						
						for (int i = 0; i < table1Parm.getCount("DEPT_CODE"); i++) {
//							if (tableParm.getValue("START_TIME", i).equals(time) 
//									&& tableParm.getValue("CLINICTYPE_CODE", i).equals(orderParm.getValue("clinictypeCode"))) {
//								table.setSelectedRow(i);
							if (table1Parm.getValue("DEPT_CODE", i).equals(orderParm.getValue("deptCode")) 
									&& table1Parm.getValue("CLINICTYPE_CODE", i).equals(orderParm.getValue("clinictypeCode"))
									&& table1Parm.getValue("DR_CODE", i).equals(orderParm.getValue("deCode"))) {
								table1.setSelectedRow(i);
								
								// add by wangbin 2015/1/8
								// ȡ��CRMԤԼ��
								this.crmId = orderParm.getValue("crmId");								
								onTable1Clicked(i);
								
								return;
							}
						}
						this.messageBox("�ò��˵Ŀ���ʱ��Ϊ" + crmTime.substring(0, 2)+":"+crmTime.substring(2, 4) + ",��ʱ�䲻�ڿ���ʱ���ڣ�");
						
						
					}
				}
			}

		}

		// add by huangtt 20140401 CRM-------end

		// ===zhangp 20120413 start
		// ��ʼ����һƱ��
		// ===huangtt 20131212
		// if(ticketFlg){
		// BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// endInvNo = invoice.getEndInvno();
		// if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
		// Operator.getRegion(), invoice.getUpdateNo())) {
		// setValue("NEXT_NO", invoice.getUpdateNo());
		// } else {
		// messageBox("Ʊ��������");
		// }
		// }

		// ===zhangp 20120413 end
		// yanjing ������ʱˢ���հ��
		// if (admType.equals("E")) {
		// initSchDay();
		// }
		TButton btton=(TButton) this.getComponent("SAVE_PAT");
		btton.setEnabled(false);
		callFunction("UI|INSURE_INFO|setEnabled", true);//add by huangjw 20150731
		callFunction("UI|PAT_PACKAGE|setEnabled", true);//add by huangjw 20150731
		// 
		this.checkInsures();
		this.checkCtz1Code();
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param mrNo
	 *            String
	 * @throws ParseException
	 */
	public void onQueryNO(String mrNo) {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			this.setValue("MR_NO", "");
			return;
		}
		// add by huangtt 20140211
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}

		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		// setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("CELL_PHONE", pat.getCellPhone());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("RESID_ADDRESS", pat.getResidAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		regCtz = getValueString("CTZ1_CODE");
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// add by huangtt 20131106 start
		setValue("ID_TYPE", pat.getIdType());
		setValue("REMARKS", pat.getRemarks());
		setValue("CURRENT_ADDRESS", pat.getCurrentAddress());
		// add by huangtt 20131106 end
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
		setValue("NATION_CODE", pat.getNationCode());
		setValue("SPECIES_CODE", pat.getSpeciesCode());
		setValue("MARRIAGE_CODE", pat.getMarriageCode());

		// add by huangtt 20140114 start
		mem = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemInfoAll(getValueString("MR_NO"))));
		memTrade = new TParm(TJDODBTool.getInstance().select(
				MEMSQL.getMemTradeCrm(getValueString("MR_NO"))));
		TParm memParm = new TParm();
		memParm.setData("MR_NO", PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO"))));
		TParm memInfo = MEMTool.getInstance().selectMemPatInfo(memParm);
		if (mem.getCount() > 0) {
			setValue("GUARDIAN_NAME", mem.getValue("GUARDIAN1_NAME", 0));
			setValue("GUARDIAN_RELATION", mem.getValue("GUARDIAN1_RELATION", 0));
			setValue("CUSTOMER_SOURCE", mem.getValue("CUSTOMER_SOURCE", 0));
		}
		if (memInfo.getCount() > 0) {
			memCode = memInfo.getValue("MEM_CODE", 0);
			// regCtz = getValueString("CTZ1_CODE");
			// this.setValue("REG_CTZ1", getValue("CTZ1_CODE"));
			// this.setValue("REG_CTZ2", getValue("CTZ2_CODE"));

		} else {
			memCode = "";
		}
		// if(this.getValueString("REG_CTZ1").equals("")){
		// setValue("REG_CTZ1", zfCtz);
		// regCtz=zfCtz;
		// }
		// if(this.getValueString("REG_CTZ2").equals("")){
		// setValue("REG_CTZ2", "");
		// }
		// add by huangtt 20140114 end
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// �ж��Ƿ����
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("�Ƿ����", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " ǿ�ƽ���[" + aa
//										+ " �����ţ�" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
//		// ��������Ϣ
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
//			// this.messageBox_("�����ɹ�!");//����ר��
			selPatInfoTable();
		this.grabFocus("CLINICROOM_NO");
	}

	/**
	 * Ӧ�ս���ý���
	 * 
	 * @param e
	 *            FocusEvent
	 */
	public void onFocusLostAction(FocusEvent e) {
		onFee();

	}

	/**
	 * ����REG����
	 * 
	 * @throws ParseException
	 */
	public void onSaveReg() {
		// =====yanj 20130502 ���ʱ��У��
		if (admType.equals("E")) {
			String admNowTime1 = StringTool.getString(SystemTool.getInstance()
					.getDate(), "HH:mm:ss");// ϵͳ��ǰʱ��
			String sessionCode = (String) this.getValue("SESSION_CODE");
			String startTime = SessionTool.getInstance().getStartTime(admType,
					sessionCode);
			String endTime = SessionTool.getInstance().getEndTime(admType,
					sessionCode);
			if (startTime.compareTo(endTime) < 0) {
				if (!(admNowTime1.compareTo(startTime) > 0 && (admNowTime1
						.compareTo(endTime) < 0))) {
					this.messageBox("��ˢ�½��棡");
					return;
				}
			} else {
				if (admNowTime1.compareTo(startTime) < 0
						&& admNowTime1.compareTo(endTime) > 0) {
					this.messageBox("��ˢ�½��棡");
					return;
				}
			}
		}
		
		
		// ====zhangp 20120724 start
		if (!this.emptyTextCheck("CTZ1_CODE")) {
			messageBox("��ѡ�����");
			return;
		}
		// ====zhangp 20120724 end

		// System.out.println("����REG����");
		DecimalFormat df = new DecimalFormat("##########0.00");
		// �ֳ��Һ�
		if (this.getValue("REGMETHOD_CODE").equals("A") && aheadFlg) {
			// ������У��
			if (TypeTool.getDouble(df.format(getValue("FeeS"))) < TypeTool
					.getDouble(df.format(getValue("FeeY")))) {
				this.messageBox("����");
				return;
			}

		}
		// ���������ֵ
		// if (!this.emptyTextCheck("DEPT_CODE,CLINICTYPE_CODE,PAY_WAY"))
		// return;
		if (this.getValue("DEPT_CODE") == null
				|| this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox("���Ҳ���Ϊ��");
			return;
		}
		if (this.getValue("CLINICTYPE_CODE") == null
				|| this.getValueString("CLINICTYPE_CODE").length() == 0) {
			this.messageBox("�ű���Ϊ��");
			return;
		}
		//delete by huangtt 20150310
//		if (admType.endsWith("E") && triageFlg.equals("Y")) {
//			if (!this.emptyTextCheck("ERD_LEVEL"))
//				return;
//		}

		// add by huangtt 20140303 start
		if (!this.getValueString("MR_NO").equals("")) {
			double clinicFee = this.getValueDouble("FeeY");
			double currentBalance = EKTpreDebtTool.getInstance().getEkeMaster(
					this.getValueString("MR_NO"));
			String sqlCtz = "SELECT OVERDRAFT FROM SYS_CTZ WHERE CTZ_CODE='"
					+ this.getValueString("REG_CTZ1") + "'";
			TParm parmCtz = new TParm(TJDODBTool.getInstance().select(sqlCtz));
			double overdraft = 0;
			if (parmCtz.getCount() > 0) {
				overdraft = parmCtz.getDouble("OVERDRAFT", 0);
			}
			if (currentBalance + overdraft < clinicFee) {
				this.messageBox("ҽ�ƿ����С������");
			}
			
			//add by huangtt 20150225 start 
			TParm memPatParm = new TParm(TJDODBTool.getInstance().select(MEMSQL.getMemInfoAll(this.getValueString("MR_NO"))));
			if(memPatParm.getCount() > 0){
				String endDate = memPatParm.getValue("END_DATE", 0);
				if(endDate.length() > 0){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
							Locale.CHINA);
					endDate = endDate.replace("-", "/").substring(0, 10);
					String today = SystemTool.getInstance().getDate().toString()
							.replace("-", "/").substring(0, 10);
					try {
						if (sdf.parse(endDate).before(sdf.parse(today))) {
							String startDate = memPatParm.getValue("START_DATE", 0);	
							this.messageBox(memPatParm.getValue("MEM_DESC", 0)
									+ "��������" + startDate.substring(0, 4) + "��"
									+ startDate.substring(5, 7) + "��"
									+ startDate.substring(8, 10) + "�գ�ʧЧ����"
									+ endDate.substring(0, 4) + "��"
									+ endDate.substring(5, 7) + "��"
									+ endDate.substring(8, 10) + "��");

						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			//add by huangtt 20150225 end
			
		}
		// add by huangtt 20140303 end
		
		
		reg = new Reg();

		reg.createReceipt();
		// reg.getRegReceipt().createBilInvoice();
		// if(ticketFlg){ //=============== huangtt 20131212
		// if (RegMethodTool.getInstance().selPrintFlg(
		// this.getValueString("REGMETHOD_CODE"))) {
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null) {
		// this.messageBox("��δ����");
		// return;
		// }
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo().compareTo(
		// reg.getRegReceipt().getBilInvoice().getEndInvno()) > 0) {
		// this.messageBox("Ʊ��������!");
		// return;
		// }
		// }
		// }

		// ����
		if (pat == null) {
			this.messageBox("�޲�����Ϣ");
			return;
		}
		//add by huangtt 20140710
		if(pat.isDeptFlg()){
			if (JOptionPane.showConfirmDialog(null, "�û����б���Ƿ�ѣ��Ƿ������", "��Ϣ",
					JOptionPane.YES_NO_OPTION) == 1){
				return;
			}
		}
		
		if(pat.isOpbArreagrage()){
			if (JOptionPane.showConfirmDialog(null, "��δ������Ŀ���Ƿ������", "��Ϣ",
					JOptionPane.YES_NO_OPTION) == 1){
				this.openDialog("%ROOT%\\config\\bil\\BILPatOweQuery.x",pat.getMrNo());//����Ƿ�ѻ��߲�ѯ����add by huangjw 20150129
				return;
			}
			
		}
		// �ж��Ƿ�Ϊ����������
		if (pat.getBlackFlg())
			this.messageBox("��ע��,��Ϊ����������!");
		pat.setNhiNo(this.getValueString("NHI_NO"));
		// System.out.println("pat::" + pat.getNhiNo());
		reg.setPat(pat);
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("�����Ų���Ϊ��");
			return;
		}
		// �Һ�����,REG����
		// 2�ż���
		if (!onSaveRegParm(true))
			return;
		// ҽ��ҽ�Ʋ��� ���ò���
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����

		reg.setTredeNo(tredeNo);
		String regmethodCode = this.getValueString("REGMETHOD_CODE"); // �Һŷ�ʽ
		// ��ùҺŷ�ʽִ���ж��Ƿ��Ʊ����
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ regmethodCode + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // ����Ƿ���Դ�Ʊע��
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("�Һ�ʧ��");
			return;
		}

		// ================
		// this.messageBox("preFlg is ::"+preFlg);
		if (this.preFlg.equals("Y")) {// yanjing 20131216
			// ���ݾ����ɾ��֮ǰ¼��ĹҺ���Ϣ
			// this.messageBox("preCaseNo is ::"+preCaseNo);
			reg.setCaseNo(preCaseNo);// �����Ԥ�����
			reg.setPreFlg(preFlg);
			String selectOldCaseNo = "SELECT ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,REG_DATE,"
					+ "IS_PRE_ORDER,OLD_CASE_NO FROM REG_PATADM "
					+ "WHERE CASE_NO = '" + preCaseNo + "'";
			// System.out.println("��ѯselectOldCaseNo is����"+selectOldCaseNo);
			TParm oldCaseNoResult = new TParm(TJDODBTool.getInstance().select(
					selectOldCaseNo));
			String oldCaseNo = "";
			if (oldCaseNoResult.getCount() > 0) {
				oldCaseNo = oldCaseNoResult.getValue("OLD_CASE_NO", 0);
			}
			reg.setOldCaseNo(oldCaseNo);
			String regSql = "DELETE REG_PATADM WHERE CASE_NO = '" + preCaseNo
					+ "'";
			TParm deleteParm = new TParm(TJDODBTool.getInstance()
					.update(regSql));
		} else {
			reg.setPreFlg(preFlg);
		}
		// ��Ԥ����ԤԼ��ͬһ�죩�ϲ������ yanjing 20130328
		this.mergePreCaseNo();

		// ==================
		if (null != regMethodParm.getValue("PRINT_FLG", 0)
				&& regMethodParm.getValue("PRINT_FLG", 0).equals("Y")) {
			// ��Ʊ
			reg.setApptCode("N");
//			reg.setRegAdmTime("");  //delete by huangtt 20140910
		} else if (null == regMethodParm.getValue("PRINT_FLG", 0)
				|| regMethodParm.getValue("PRINT_FLG", 0).length() <= 0
				|| regMethodParm.getValue("PRINT_FLG", 0).equals("N")) {
			// ����Ʊ����
			reg.setApptCode("Y");
			// 12ԤԼʱ��
			reg.setRegAdmTime(startTime);
		}
		// ��õ�һ��ҳǩ����
		if (tableFlg) {
//			System.out.println("2222222222222");
			// �ж��Ƿ�VIP����
			TTable table1 = (TTable) this.getComponent("Table1");
			TParm parm = table1.getParmValue();
			TParm temp = parm.getRow(selectRow); // ��õ�һ��ҳǩ����
			String type = temp.getValue("TYPE"); // VIP ��һ��
			if (type.equals("VIP")) {
				// UPDATE REG_CLINICQUE &
				temp.setData("ADM_TYPE", admType); // �Һ�����
				temp.setData("SESSION_CODE", reg.getSessionCode()); // ʱ��
				temp.setData("ADM_DATE", StringTool.getString(
						(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
				temp.setData("START_TIME", StringTool.getString(SystemTool
						.getInstance().getDate(), "HHmm"));// ϵͳ��ǰʱ��
				String admNowDate = StringTool.getString(SystemTool
						.getInstance().getDate(), "yyyyMMdd");// ϵͳ��ǰ����
				// String startTime = "";
				if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
					this.messageBox("�Ѿ����ﲻ���ԹҺ�");
					callFunction("UI|table2|clearSelection");
					return;
				}
				// // ���vip�����  modigy by huangtt 20140926
				if(!queryQueNo(temp)){
					return; 
				}
				
				if(crmTime.length() > 0){
					reg.setRegAdmTime(crmTime);
				}	
				// ===zhangp 20120629 end
				reg.setVipFlg(true); // vip����
				TParm regParm = reg.getParm();
				String admDate = StringTool.getString(reg.getAdmDate(),
						"yyyyMMdd");
				regParm.setData("ADM_DATE", admDate);
				// =========pangben 2012-7-1 start�غ�����
				if (!onSaveQueNo(regParm)) {
					messageBox("ȡ�þ����ʧ��");
					return;
				}
				// =========pangben 2012-7-1 stop
				if ("N".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(true); // ����
				} else if ("Y".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(false); // ������
				}
//				startTime = temp.getValue("START_TIME", 0);
//				reg.setRegAdmTime(startTime);
			} else if (getValueString("REGMETHOD_CODE").equals("D")) {
				messageBox("���VIP��");
				return;
			}
		}
		// =====zhangp 20120301 modify start
//		if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
		if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
			if (!onInsEkt(payWay, null)) {
				// ===========pangben 2012-7-1 ����ʧ�ܻع�VIP�������
				TParm regParm = reg.getParm();
				if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
					this.messageBox("����VIP�������ʧ��,����ϵ��Ϣ����");
				}
				return;
			}
		}
		// =====zhangp 20120301 modify end

		if (!onSaveRegOne(payWay, ins_exe)) {
			// ===========pangben 2012-7-1 ����ʧ�ܻع�VIP�������
			TParm regParm = reg.getParm();
			if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
				this.messageBox("����VIP�������ʧ��,����ϵ��Ϣ����");
			}
			return;
		}
		if (ins_exe) { // ҽ��������ִ�гɹ� ,ɾ����;״̬���� �޸�Ʊ�ݺ�
			if (!updateINSPrintNo(reg.caseNo(), "REG"))
				return;
		}
		// ��Ʊ����
		TParm result = onPrintParm();
		if ("Y".endsWith(reg.getApptCode())) {
			if (this.preFlg.equals("Y")) {// yanjing Ԥ����飬ֱ�ӱ�����20131230
				if (!onArrivePre())
					return;
			} else {
				this.messageBox("ԤԼ�ɹ�!");

			}
			// �����Ŷӽк�
			/**
			 * if (!"true".equals(callNo("REG", ""))) { this.messageBox("�к�ʧ��");
			 * }
			 **/
			this.onClear();
			return;
		}
		// ================pangben modify 20110817 ���˵�λ������ִ�д�Ʊ
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			// �жϵ��ﲡ����Ʊ
			if ("N".endsWith(reg.getApptCode())) {
				// ҽ�ƿ���Ʊ
				if (ticketFlg) {
					onPrint(result);
				}
				// ====huangtt 20131212
				// BilInvoice invoice = new BilInvoice();
				// invoice = invoice.initBilInvoice("REG");
				// // ��ʼ����һƱ��
				// // ===zhangp 20120306 modify start
				// if(ticketFlg){
				// if (BILTool.getInstance().compareUpdateNo("REG",
				// Operator.getID(), Operator.getRegion(),
				// invoice.getUpdateNo())) {
				// setValue("NEXT_NO", invoice.getUpdateNo());
				// } else {
				// messageBox("Ʊ��������");
				// clearValue("NEXT_NO");
				// }
				// }

				// ===zhangp 20120306 modify end
				// �����Ŷӽк�
				if (!"true".equals(callNo("REG", reg.caseNo()))) {
					this.messageBox("�к�ʧ��");
				}

			}
			// ����Ʊִ�м��˲���
		} else {
			// =================pangben 20110817
			TParm parm = new TParm();
			parm.setData("RECEIPT_NO", reg.getRegReceipt().getReceiptNo()); // �վݺ�
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")); // ���˵�λ
			parm.setData("ADM_TYPE", reg.getRegReceipt().getAdmType()); // �ż�ס��
			parm.setData("REGION_CODE", Operator.getRegion()); // Ժ��
			parm.setData("CASHIER_CODE", Operator.getID()); // �շ���Ա
			parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // �շ�����ʱ��
			parm.setData("RECEIPT_TYPE", "REG"); // Ʊ�����ͣ�REG ��OPB
			parm.setData("DATA_TYPE", "REG"); // �ۿ���Դ REG OPB HRM
			parm.setData("CASE_NO", reg.caseNo()); // �����
			parm.setData("MR_NO", reg.getPat().getMrNo());
			parm.setData("AR_AMT", reg.getRegReceipt().getArAmt()); // Ӧ�ɽ��
			parm.setData("BIL_STATUS", "1"); // ����״̬1 ���� 2 �������д�� =1
			// caowl 20130307 start
			// String sqls = "SELECT * FROM BIL_CONTRACTD WHERE MR_NO = '"
			// + reg.getPat().getMrNo() + "' AND CONTRACT_CODE = '"
			// + this.getValue("CONTRACT_CODE") + "'";
			// // System.out.println("��������" + sqls);
			// TParm parms = new TParm(TJDODBTool.getInstance().select(sqls));
			// if (parms.getCount() <= 0) {
			// this.messageBox("�˲��˲����ڸú�ͬ��λ����ȷ�ϣ�");
			// return;
			// }
			// caowl 20130307 end
			// ���˵�λ�ɷ�ʱ��
			// update =2
			parm.setData("RECEIPT_FLG", "1"); // ״̬��1 �շ� 2 �˷�
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result1 = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "insertRecode", parm);
			if (result1.getErrCode() < 0) {
				err(result1.getErrCode() + " " + result1.getErrText());
				this.messageBox("�Һ�ʧ��");
			} else
				this.messageBox("�Һųɹ�,�Ѿ�����");
		}
		
		// add by wangbin 20140812 �Һ�ʱ���벡���������� START
		this.insertMroMrvData();
        // add by wangbin 20140812 �Һ�ʱ���벡���������� END
		
		// add by wangbin 20140805 �����CRMԤԼ�Һ����ݴ�����ʱ�� START
		this.insertMroRegData();
		// add by wangbin 20140805 �����CRMԤԼ�Һ����ݴ�����ʱ�� END
		
		// add by wangbin 20140915 �Һź��򲡰��ҷ�����Ϣ START
		this.onSendMROMessages();
		// add by wangbin 20140915 �Һź��򲡰��ҷ�����Ϣ END
		
		if (singleFlg) {
			onPrintReg(reg.caseNo(), ""); // add by huangtt 20140331
		}
		
		if (crmFlg && "O".equals(reg.getAdmType())) {
			// System.out.println("ʱ��="+reg.getAdmDate());
			// System.out.println("�ż���="+reg.getAdmType());
			// System.out.println("ҽ������"+reg.getDrCode());
			// System.out.println("���ң���"+reg.getDeptCode());
			// System.out.println("��䣽��"+reg.getClinicroomNo());
			// System.out.println("ʱ�Σ���"+reg.getSessionCode());
//			String sqlQueNo = "SELECT QUE_NO-1 QUE_NO FROM REG_SCHDAY"
//					+ " WHERE VIP_FLG = 'Y' "
//					+ " AND DEPT_CODE = '"
//					+ reg.getDeptCode()
//					+ "'"
//					+ " AND ADM_TYPE = '"
//					+ reg.getAdmType()
//					+ "'"
//					+ " AND ADM_DATE = '"
//					+ reg.getAdmDate().toString().replace("/", "").replace("-",
//							"").substring(0, 8) + "'"
//					+ " AND CLINICROOM_NO = '" + reg.getClinicroomNo() + "'";
//					if(reg.getDrCode().trim().length() == 0){
//						sqlQueNo = sqlQueNo +  " AND DR_CODE IS NULL ";
//					}else{
//						sqlQueNo = sqlQueNo + " AND DR_CODE = '" + reg.getDrCode() + "'";
//					}
//
//			sqlQueNo = sqlQueNo+ " AND CLINICTYPE_CODE='" + clinictypeCode + "'";
			String sqlQueNo = "SELECT COUNT(QUE_NO) QUE_NO FROM REG_CLINICQUE " +
					" WHERE ADM_DATE = '"+ reg.getAdmDate().toString().replace("/", "").replace("-",
							"").substring(0, 8)+"'" +
					" AND QUE_STATUS='Y'" +
					" AND CLINICROOM_NO = '" + reg.getClinicroomNo() + "' " +
					"AND ADM_TYPE = 'O'";
//			System.out.println("sqlQueNo="+sqlQueNo);
			TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
					sqlQueNo));
			if(parmQueNo.getCount()>0){
				int queNo=parmQueNo.getInt("QUE_NO", 0);
//				for (int i = 0; i < parmQueNo.getCount(); i++) {
//					queNo += parmQueNo.getInt("QUE_NO", i);
//				}
				TParm orderCount = new TParm();
				orderCount.setData("date", reg.getAdmDate().toString().replace("/",
						"").replace("-", "").substring(0, 8));
				orderCount.setData("deptCode", reg.getDeptCode());
				orderCount.setData("drCode", reg.getDrCode());
				orderCount.setData("quegroup", clinictypeCode);   //add by huangtt 20141106        
				orderCount.setData("count", queNo);
				System.out.println("orderCount=="+orderCount);
				result = TIOM_AppServer.executeAction("action.reg.REGCRMAction",
						"orderCount", orderCount);
				if (!result.getBoolean("flg")) {
					this.messageBox("����Һ�����ʧ�ܣ�");
//					return;
				}
			}
			

		}
	//if(this.getValue("INSURE_INFO")!=null && !this.getValue("INSURE_INFO").equals("")){//�ҺŰ󶨱�����Ϣ
		TParm insureInfo = new TParm(TJDODBTool.getInstance().update("UPDATE REG_PATADM SET INSURE_INFO='"+this.getValue("INSURE_INFO")+"', " +
				" PAT_PACKAGE = '"+this.getValue("PAT_PACKAGE")+"' WHERE CASE_NO='"+reg.caseNo()+"'"));
	//}
		//		// �������� ��Ϣ
//		if (PatTool.getInstance().unLockPat(pat.getMrNo()))
		this.onClear();
		initSession();
		pat = null;
		// ���CRMԤԼFLG
		crmRegFlg = false;
	}
	
	/**
	 * ��ӡ���ﵥ
	 */
	@SuppressWarnings("deprecation")
	public void onPrintReg(String caseNo, String copy) {
		// String sql = "SELECT MAX_QUE," +
		// "MAX_QUE-(SELECT MAX_QUE FROM REG_QUEGROUP WHERE QUEGROUP_CODE = REG_SCHDAY.QUEGROUP_CODE) AS ADD_COUNT "
		// +
		// " FROM REG_SCHDAY " +
		// " WHERE REGION_CODE = '"+reg.getRegion()+"'" +
		// " AND ADM_TYPE = '"+reg.getAdmType()+"'" +
		// " AND ADM_DATE = '"+reg.getAdmDate().toString().replace("-",
		// "").replace("/", "").substring(0, 8)+"'" +
		// " AND SESSION_CODE = '"+reg.getSessionCode()+"'" +
		// " AND REALDEPT_CODE = '"+reg.getRealdeptCode()+"'" +
		// " AND REALDR_CODE = '"+reg.getRealdrCode()+"'" +
		// " AND CLINICROOM_NO='"+reg.getClinicroomNo()+"'" ;
		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// int maxQue = parm.getInt("MAX_QUE", 0);
		// int addCount = parm.getInt("ADD_COUNT", 0);
		String sql = "SELECT * FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm regParm = new TParm(TJDODBTool.getInstance().select(sql));
//		int que = regParm.getInt("QUE_NO", 0);
		// if(que>(maxQue-addCount)){
		// add = " ���Ӻţ�";
		// }
//		String sql1 = "SELECT B.CHN_DESC NAME"
//				+ " FROM REG_CLINICROOM A, SYS_DICTIONARY B"
//				+ " WHERE A.LOCATION = B.ID AND B.GROUP_ID = 'SYS_LOCATION'"
//				+ "  AND A.CLINICROOM_NO='"
//				// + this.getValueString("CLINICROOM_NO") + "'";
//				+ regParm.getValue("CLINICROOM_NO", 0) + "'";
//		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
//		String dept = "";
//		if (!parm1.getValue("NAME", 0).equals("")) {
//			dept = "(" + parm1.getValue("NAME", 0) + ")";
//		}
		TParm data = new TParm();
		data.setData("REQUIREMENT", "TEXT", regParm.getValue("REQUIREMENT", 0));
		data.setData("COPY", "TEXT", copy);
		data.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("HOSP_EN", "TEXT", Operator.getHospitalENGFullName());
		data.setData("TITLE", "TEXT", "���ﵥ");
		data.setData("MR_NO", this.getValueString("MR_NO"));
		data.setData("BAR_CODE", "TEXT", this.getValueString("MR_NO"));
		data.setData("NAME", this.getValueString("PAT_NAME"));
		data.setData("REG_CTZ", this.getText("REG_CTZ1"));
		data.setData("DEPT", this.getText("DEPT_CODE"));// ==liling
//		TTextFormat sex = (TTextFormat) this.getComponent("SEX_CODE");
		data.setData("SEX", this.getText("SEX_CODE"));
		String birthday = pat.getBirthday().toString().replace("-", "")
				.replace("/", "").substring(0, 8);

		Timestamp birthDay = (Timestamp) pat.getBirthday();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = birthDay == null ? sysDate : birthDay;
		String age = "0";
		age = DateUtil.showAge(temp, sysDate);
		
		data.setData("BIRTH", birthday.substring(0, 4) + "��"
				+ birthday.substring(4, 6) + "��" + birthday.substring(6, 8)
				+ "��/"+age);// ==liling

		data.setData("DR", this.getText("DR_CODE"));
		DecimalFormat df = new DecimalFormat("0.00");
		data.setData("FEE", df.format(Math.abs(this.getValueDouble("FeeY"))));// ====liling
		
		sql = "SELECT CLINIC_DESC FROM REG_CLINICAREA WHERE CLINICAREA_CODE='"+regParm.getValue("CLINICAREA_CODE", 0)+"'";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sql));
		
		data.setData("CLINICROOM_NO", parmC.getValue("CLINIC_DESC", 0));
		data.setData("TIME", regParm.getValue("REG_DATE", 0).replace("-", "/")
				.substring(0, regParm.getValue("REG_DATE", 0).length() - 5));
		data.setData("OPERATOR", Operator.getID());
		String admDate = this.getValueString("ADM_DATE").replace("-", "/")
				.substring(0, 10);
//		if (regParm.getBoolean("VIP_FLG", 0)
//				&& !"".equals(regParm.getValue("REG_ADM_TIME", 0))  ) {
//			String vipSql = "SELECT START_TIME FROM REG_CLINICQUE "
//					+ "WHERE ADM_TYPE='"
//					+ regParm.getValue("ADM_TYPE", 0)
//					+ "' AND ADM_DATE='"
//					+ regParm.getValue("ADM_DATE", 0).replaceAll("-", "")
//							.substring(0, 8) + "'" + " AND SESSION_CODE='"
//					+ regParm.getValue("SESSION_CODE", 0)
//					+ "' AND CLINICROOM_NO='"
//					+ regParm.getValue("CLINICROOM_NO", 0) 
//					+ "' ORDER BY START_TIME";
//			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
//			SimpleDateFormat sdf = new SimpleDateFormat("mmss");
//			String t1 = result.getValue("START_TIME", 0);
//			String t2 = result.getValue("START_TIME", 1);
//			long minute = 0;
//			try {
//				minute = (sdf.parse(t2).getTime() - sdf.parse(t1).getTime()) / 1000;
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			StringBuffer time = new StringBuffer("");
//			time.append(regParm.getValue("REG_ADM_TIME", 0).substring(0, 2));
//			time.append(":");
//			time.append(regParm.getValue("REG_ADM_TIME", 0).substring(2, 4));
//			Calendar cal = Calendar.getInstance();
//			cal.set(Integer.parseInt(admDate.substring(0, 4)), Integer
//					.parseInt(admDate.substring(5, 7)), Integer
//					.parseInt(admDate.substring(8, 10)),
//					Integer.parseInt(regParm.getValue("REG_ADM_TIME", 0)
//							.substring(0, 2)), Integer.parseInt(regParm
//							.getValue("REG_ADM_TIME", 0).substring(2, 4)));
//		
//		
//			cal.add(Calendar.MINUTE, Integer.parseInt(minute+""));
//
//			time.append(" - ");
//			time.append(cal.getTime().getHours());
//			time.append(":");
//			if(cal.getTime().getMinutes() < 10){
//				time.append("0");
//			}
//			time.append(cal.getTime().getMinutes());
//			
//			data.setData("REG_ADM_TIME", admDate + " " + time.toString());
//		} else {
//			data.setData("REG_ADM_TIME", "");
//		}
		data.setData("REG_ADM_TIME", "");
		if(regParm.getValue("REG_ADM_TIME", 0).length()==4){
			data.setData("REG_ADM_TIME", regParm.getValue("REG_ADM_TIME", 0).substring(0, 2)+":"+regParm.getValue("REG_ADM_TIME", 0).substring(2, 4));
		}
		
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REG.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
			this.openPrintDialog(IReportTool.getInstance().getReportPath(
					"REG.jhw"), IReportTool.getInstance().getReportParm("REG.class",data), true);
		}
//		this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REG.jhw", data, true);

	}

	/**
	 * ��Ʊ����
	 * 
	 * @return TParm
	 */
	private TParm onPrintParm() {
		// �����Ʊ����
		TParm result = PatAdmTool.getInstance().getRegPringDate(reg.caseNo(),
				"");
		// zhangp 20120206
		result.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		result.setData("PAY_WAY", this.getValue("PAY_WAY")); // ֧����ʽ
		result.setData("INS_SUMAMT", ins_amt);
		result.setData("ACCOUNT_AMT_FORREG", accountamtforreg);// �����˻�
		return result;
	}

	/**
	 * ҽ��ҽ�Ʋ��� ���ò���
	 * 
	 * @param payWay
	 *            String
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	private boolean onInsEkt(String payWay, String caseNo) {

		// ҽ�ƿ�֧��
		if (payWay.equals("EKT")) {
			// ����CASE_NO ��Ϊҽ�ƿ���ҪCASE_NO ��������ҽ�ƿ�֧����ʱ��������CASE_NO
			if ("N".endsWith(reg.getApptCode())) {
				// System.out.println("222222222222222222");
				if (null != caseNo && caseNo.length() > 0) {
					reg.setCaseNo(caseNo);
				} else {
					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
							"CASE_NO", "CASE_NO"));
				}
				// ����ҽ�ƿ�
				if (!this.onEktSave("Y")) {
					System.out.println("!!!!!!!!!!!ҽ�ƿ��������");
					return false;
				}
				if (null != greenParm
						&& null != greenParm.getValue("GREEN_FLG")
						&& greenParm.getValue("GREEN_FLG").equals("Y")) {
					// ʹ����ɫͨ�����
					reg.getRegReceipt().setPayMedicalCard(
							TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
					reg.getRegReceipt().setOtherFee1(
							greenParm.getDouble("GREEN_USE"));
				}
			}
		}
		if (payWay.equals("INS")) {
			TParm result = null;
			// ҽ����֧��
			result = onSaveRegTwo(payWay, ins_exe, caseNo);
			if (null == result) {
				return false;
			}
			ins_exe = result.getBoolean("INS_EXE");
			ins_amt = result.getDouble("INS_AMT");
			accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
		}

		if (ins_exe) {
			// ִ��ҽ�� �ж���;״̬
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", reg.caseNo());
			runParm.setData("EXE_USER", Operator.getID());
			runParm.setData("EXE_TERM", Operator.getIP());
			runParm.setData("EXE_TYPE", "REG");
			runParm = INSRunTool.getInstance().queryInsRun(runParm);
			if (runParm.getErrCode() < 0) {
				return false;
			}
			if (runParm.getCount("CASE_NO") <= 0) {
				// û�в�ѯ�����ݣ�˵����;״̬������
				return false;
			} else {
				if (runParm.getInt("STUTS", 0) != 1) { // STUTS :1.��; 2.�ɹ�
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ִ�б���REG_PATADM BIL_REG_RECP BIL_INVRCP �����(ҽ��ִ�в���)
	 * 
	 * @param payWay
	 *            String
	 * @param ins_exe
	 *            boolean
	 * @return boolean
	 * 
	 */
	private boolean onSaveRegOne(String payWay, boolean ins_exe) {
		TParm result = new TParm();
		if (!reg.onNew()) {
			this.messageBox("�Һ�ʧ��");
			if (payWay.equals("EKT")) { // ҽ�ƿ�֧��
				result = new TParm();
				result.setData("CURRENT_BALANCE", ektOldSum);
				result.setData("MR_NO", p3.getValue("MR_NO"));
				result.setData("SEQ", p3.getValue("SEQ"));
				try {
					result = EKTIO.getInstance().TXwriteEKTATM(result,
							reg.getPat().getMrNo());
					// ��дҽ�ƿ����
					if (result.getErrCode() < 0)
						System.out.println("err:" + result.getErrText());
					// ҽ�ƿ��Һų������⳷������
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("err:ҽ�ƿ�д������ʧ��");
				}

				cancleEKTData();
			}
			if (payWay.equals("INS")) { // ҽ����֧��
				if (!ins_exe) { // ҽ�������� ,ɾ����;״̬����
					return false;
				}
				result = new TParm();
				insParm.setData("EXE_TYPE", "REG");
				// ִ�г�������----��Ҫʵ��
				if (tjINS) { // ҽ�ƿ�����
					result.setData("CURRENT_BALANCE", ektOldSum);
					result.setData("MR_NO", p3.getValue("MR_NO"));
					result.setData("SEQ", p3.getValue("SEQ"));
					try {
						result = EKTIO.getInstance().TXwriteEKTATM(result,
								p3.getValue("MR_NO"));
						// ��дҽ�ƿ����
						if (result.getErrCode() < 0)
							System.out.println("err:" + result.getErrText());
						// ҽ�ƿ��Һų������⳷������
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("err:ҽ�ƿ�д������ʧ��");
						e.printStackTrace();
					}
					cancleEKTData();
				}

			}
			// EKTIO.getInstance().unConsume(tredeNo, this);
			return false;
		}
		return true;
	}

	/**
	 * ִ�б��� ҽ�������ݲ���
	 * 
	 * @param payWay
	 *            String
	 * @param ins_amt
	 *            double
	 * @param ins_exe
	 *            boolean
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
		double ins_amtTemp = 0.00;// ҽ�����
		TParm result = new TParm();
		if (payWay.equals("INS")) {
			// ��ѯ�Ƿ�������������
			if (null == caseNo || caseNo.length() <= 0) {
				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
						"CASE_NO", "CASE_NO"); // ��þ����
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
			if (parm.getErrCode() < 0) {
				this.messageBox("E0005");
				return null;
			}

			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
				this.messageBox("�˾��ﲡ��ʹ��������,������ʹ��ҽ������");
				return null;
			}
			if (this.getValue("REG_CTZ1").toString().length() <= 0) {
				this.messageBox("��ѡ��ҽ������������");
				return null;
			}
			// ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ
			// 2.��ְ���� 3.�Ǿ�����
			// ҽ�����Һ�
			// ��ùҺŷ��ô��룬���ý�����
			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
					+ admType
					+ "'"
					+ " AND A.CLINICTYPE_CODE='"
					+ getValue("CLINICTYPE_CODE") + "'";

			// �Һŷ�
			double reg_fee = BIL.getRegDetialFee(admType, TypeTool
					.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
							.getString(getValue("REG_CTZ2")), TypeTool
							.getString(getValue("CTZ3_CODE")), this
							.getValueString("SERVICE_LEVEL") == null ? ""
							: this.getValueString("SERVICE_LEVEL"));
			// ���� �����ۿ�
			double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
					.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
							.getString(getValue("REG_CTZ2")), TypeTool
							.getString(getValue("CTZ3_CODE")), this
							.getValueString("SERVICE_LEVEL") == null ? ""
							: this.getValueString("SERVICE_LEVEL"));

			// System.out.println("regFeesql:::::" + regFeesql);
			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
					regFeesql));
			if (regFeeParm.getErrCode() < 0) {
				err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
				this.messageBox("ҽ��ִ�в���ʧ��");
				return null;
			}
			for (int i = 0; i < regFeeParm.getCount(); i++) {
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
					regFeeParm.setData("AR_AMT", i, reg_fee);
				}
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
					regFeeParm.setData("AR_AMT", i, clinic_fee);
				}
			}
			// System.out.println("regFeesql::" + regFeesql);
			result = TXsaveINSCard(regFeeParm, caseNo); // ִ�в���
			// System.out.println("RESULT::::" + result);
			if (null == result)
				return null;
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("ҽ��ִ�в���ʧ��");
				return null;
			}
			// 24ҽ����֧��(REG_RECEIPT)
			if (null != result.getValue("MESSAGE_FLG")
					&& result.getValue("MESSAGE_FLG").equals("Y")) {
				System.out.println("ҽ�������ִ����ֽ���ȡ");
			} else {
				// ҽ��֧��
				ins_amtTemp = tjInsPay(result, regFeeParm);
				ins_exe = true; // ҽ��ִ�в��� ��Ҫ�ж���;״̬
				reg.setInsPatType(insParm.getValue("INS_TYPE")); // ����ҽ������
				reg.setConfirmNo(insParm.getValue("CONFIRM_NO")); // ҽ�������
				// CONFIRM_NO
			}

		}
		result.setData("INS_AMT", ins_amtTemp);
		result.setData("INS_EXE", ins_exe);

		return result;
	}

	/**
	 * ������� ��������
	 * 
	 * @param payWay
	 *            ֧�����
	 */
	private void onSaveRegParm(String payWay) {
		// 20�ֽ�֧��(REG_RECEIPT)
		if (payWay.equals("C0")) {
			reg.getRegReceipt()
					.setPayCash(TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);

		}
		// 21���п�֧��(REG_RECEIPT)
		if (payWay.equals("C1")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
		}
		// 22֧Ʊ֧��(REG_RECEIPT)
		if (payWay.equals("T0")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			// 24��ע(д֧Ʊ��)(REG_RECEIPT)
			reg.getRegReceipt().setRemark(
					TypeTool.getString(getValue("REMARK")));

		}
		// 22����֧��(REG_RECEIPT)
		if (payWay.equals("C4")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			reg.getRegReceipt().setPayDebit(
					TypeTool.getDouble(getValue("FeeY")));
		}
		// 23ҽ�ƿ�֧��(REG_RECEIPT)
		if (payWay.equals("EKT")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY")));
		}
	}

	/**
	 * ������� ��������
	 * 
	 * @return boolean =======pangben 2012-7-1��Ӳ��� ���ֱ������� flg=false ���� ִ�� UPDATE
	 *         QUE_NO ����
	 */
	private boolean onSaveRegParm(boolean flg) {
		String regionCode = TypeTool.getString(getValue("REGION_CODE")); // ����
		String ctz1Code = TypeTool.getString(getValue("REG_CTZ1")); // ���1
		String ctz2Code = TypeTool.getString(getValue("REG_CTZ2")); // ���2
//		String ctz3Code = TypeTool.getString(getValue("CTZ3_CODE")); // ���3
		String ctz3Code = ""; // ���3
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����

		reg.setReassureFlg(this.getValueString("REASSURE_FLG"));//���ļ�
		
		reg.setRequirement(this.getValueString("REQUIREMENT")); // add by
		// huangtt
		// 20131106
		// ��ӱ�ע�ֶ�
		reg.setAdmType(admType);
		// 4����
		reg.setRegion(regionCode);
		// 5��������
		reg.setAdmDate(TypeTool.getTimestamp(getValue("ADM_DATE")));
		// 6�ҺŲ�������
		reg.setRegDate(SystemTool.getInstance().getDate());
		// 7ʱ��
		reg.setSessionCode(TypeTool.getString(getValue("SESSION_CODE")));
		// 8����
		reg.setClinicareaCode((PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(getValue("CLINICROOM_NO"))))
				.getValue("CLINICAREA_CODE", 0));
		// 9����
		reg.setClinicroomNo(TypeTool.getString(getValue("CLINICROOM_NO")));
		// 10�ű�
		reg.setClinictypeCode(TypeTool.getString(getValue("CLINICTYPE_CODE")));
		// System.out.println("��������"+reg.getAdmDate());
		// System.out.println("��ǰ����"+SystemTool.getInstance().getDate());
		// 19�Һŷ�ʽ
		reg.setRegmethodCode(TypeTool.getString(getValue("REGMETHOD_CODE")));
		String admDate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		// 17ԤԼ����
		// =========pangben 2012-7-1 ���ֱ��� �͵��չҺ��߼�
		if (flg) {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("ԤԼ");
//				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
				if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
					this.messageBox("��ѡ������ֳ��Һ�,�������ڱ���Ϊ����!");
					return false;
				}
				reg.setApptCode("Y");
				// 12ԤԼʱ��
				reg.setRegAdmTime(startTime);
			} else {
				// System.out.println("����");
				reg.setApptCode("N");
			}
			// 18������
			if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C")))
				// ����
				reg.setVisitCode("0");
			else {
				// ����
				reg.setVisitCode("1");
			}
			// 11����VIPȡֵ���õ������
			if (!tableFlg) {
				if (!onSaveParm(admDate))
					return false;
			} else {
				// ===========pangben 2012-7-1 �޸� UPDATE ���que_no ֻ����һ�����Ӿ������
				TTable table1 = (TTable) this.getComponent("Table1");
				TParm parm = table1.getParmValue();
				TParm temp = parm.getRow(selectRow); // ��õ�һ��ҳǩ����
				String type = temp.getValue("TYPE"); // VIP ��һ��
				if (type.equals("VIP")) {
					// VIP��Һ�
				} else {
					// ��ͨ��Һ�
					int queNo = SchDayTool.getInstance().selectqueno(
							reg.getRegion(),
							reg.getAdmType(),
							TypeTool.getString(reg.getAdmDate()).replaceAll(
									"-", "").substring(0, 8),
							reg.getSessionCode(), reg.getClinicroomNo());
					if (queNo == 0) {
						this.messageBox("���޾����!");
						return false;
					}
					reg.setQueNo(queNo);
					if (reg.getQueNo() == -1) {
						// ���޺Ų��ܹҺ�
						this.messageBox("E0017");
						return false;
					}
					// ==========pangben 2012-6-18 �޸��غ�����
					TParm regParm = reg.getParm();
					regParm.setData("ADM_DATE", admDate);
					if (onSaveQueNo(regParm)) {
						// return true;
					} else {
						return false;
					}
				}
			}
		} else {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("ԤԼ");
//				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
				if (RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE"))) {
					this.messageBox("��ѡ������ֳ��Һ�,�������ڱ���Ϊ����!");
					return false;
				}
			} else {
				reg.setApptCode("N");
			}
		}

		// 13����
		reg.setDeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 14ҽʦ
		reg.setDrCode(TypeTool.getString(getValue("DR_CODE")));
		// 15ʵ���Ʊ�(Ĭ�Ͽ���)
		reg.setRealdeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 16ʵ��ҽʦ(Ĭ��ҽʦ)
		reg.setRealdrCode(TypeTool.getString(getValue("DR_CODE")));
		// 20����ۿ�1
		reg.setCtz1Code(ctz1Code);
		// 21����ۿ�2
		reg.setCtz2Code(ctz2Code);
		// 22����ۿ�3
		reg.setCtz3Code(ctz3Code);

		// 23ת��Ժ��
		reg.setTranhospCode("");
		// 24���˺�
		reg.setTriageNo("");
		// 25���˵�λ
		reg.setContractCode(TCM_Transform.getString(getValue("CONTRACT_CODE")));
		// 26����ע��    getValue("REGMETHOD_CODE").equals("O") add by huangtt 20160407  ���114�Һŷ�ʽ
		
		if(RegMethodTool.getInstance().selSiteNumFlg(getValueString("REGMETHOD_CODE")))
		
//		if (getValue("REGMETHOD_CODE").equals("A") || getValue("REGMETHOD_CODE").equals("O"))
			reg.setArriveFlg(true);
		else
			reg.setArriveFlg(false);
		// 27�˹���Ա
		// reg.setRegcanUser();
		// 28�˹�����
		// reg.setRegcanDate();
		// 29�Һ�Ժ��
		reg.setAdmRegion(regionCode);
		// 30Ԥ������ʱ��(�ƻ�����)
		// reg.setPreventSchCode();
		// 31DRG��
		// reg.setDrgCode();
		// 32����ע��
		// reg.setHeatFlg();
		// 33�������
		reg.setAdmStatus("1");
		// 34����״̬
		reg.setReportStatus("1");
		// 35����
		// reg.setWeight();
		// 36���
		// reg.setHeight();
		if (admType.equals("E"))
			reg.setErdLevel(Objects.toString(getValue("ERD_LEVEL"),""));

		// �ż����վ�(For bill),REG_RECEIPT����
		// reg.createReceipt();
		// 3�ż�ס��(REG_RECEIPT)
		reg.getRegReceipt().setAdmType(admType);
		// 4����(REG_RECEIPT)
		reg.getRegReceipt().setRegion(regionCode);
		// 5ID��(REG_RECEIPT)
		reg.getRegReceipt().setMrNo(TypeTool.getString(getValue("MR_NO")));
		// 6�����վݺ�(REG_RECEIPT)
		// reg.getRegReceipt().setResetReceiptNo("");
		// 8��������(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9�շ�����(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10�վݴ�ӡ����(REG_RECEIPT)
		// ===================pangben modify 20110818 ���˱�ǣ�PRINT_DATE ��λΪ��ʱ�����м���
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			reg.getRegReceipt()
					.setPrintDate(SystemTool.getInstance().getDate());
			// 7�վ�ӡˢ��(REG_RECEIPT)
			// reg.getRegReceipt().setPrintNo(
			// reg.getRegReceipt().getBilInvoice().getUpdateNo()); //modify by
			// huangtt

		}

		// 11�Һŷ�(REG_RECEIPT)
		// ======================pangben modify 20110815
		onSaveParm(ctz1Code, ctz2Code, ctz3Code);
		// 12�ۿ�ǰ�Һŷ�(REG_RECEIPT)
		reg.getRegReceipt().setRegFeeReal(
				TypeTool.getDouble(getValue("REG_FEE")));

		// 14�ۿ�ǰ����(REG_RECEIPT)
		reg.getRegReceipt().setClinicFeeReal(
				TypeTool.getDouble(getValue("CLINIC_FEE")));
		// 15���ӷ�(REG_RECEIPT)
		// reg.getRegReceipt().setSpcFee(0.00);
		// 16��������1(REG_RECEIPT)
		// reg.getRegReceipt().setOtherFee1(0.00);
		// 17��������2(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee2(0.00);
		// 18��������3(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee3(0.00);
		// 19Ӧ�ս��(REG_RECEIPT)
		reg.getRegReceipt().setArAmt(TypeTool.getDouble(getValue("FeeY")));
		onSaveRegParm(payWay);
		// 24ҽ����֧��(REG_RECEIPT)
		// reg.getRegReceipt().setPayInsCard(0.00);
		// 26�ż����������(REG_RECEIPT)
		// reg.getRegReceipt().setPayIns(0.00);
		// 28�տ�Ա����(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		// 29���ʱ�־(REG_RECEIPT)
		// reg.getRegReceipt().setAccountFlg("");
		// 30�սᱨ���(REG_RECEIPT)
		// reg.getRegReceipt().setAccountSeq("");
		// 31�ս���Ա(REG_RECEIPT)
		// reg.getRegReceipt().setAccountUser(Operator.getName());
		// 32��������(REG_RECEIPT)
		// reg.getRegReceipt().setAccountDate(SystemTool.getInstance().getDate());
		// ����ȼ�
		reg.setServiceLevel(this.getValueString("SERVICE_LEVEL"));
		// Ʊ������BilInvoice(For bil),BIL_INVOICE����
		// reg.getRegReceipt().createBilInvoice();
		// reg.getRegReceipt().getBilInvoice().getParm(); //modify by huangtt
		// reg.getRegReceipt().getBilInvoice().setCashierCode(Operator.getID());
		// //������Ա
		// reg.getRegReceipt().getBilInvoice().setStartValidDate();
		// reg.getRegReceipt().getBilInvoice().setEndValidDate();
		// reg.getRegReceipt().getBilInvoice().setStatus("1");

		// Ʊ����ϸ��BILInvrcpt(For bil),BIL_INVRCP����
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1Ʊ������(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setInvNo(
		// reg.getRegReceipt().getBilInvoice().getUpdateNo()); //
		// //2��Ʊ����(BIL_INVRCP) modify by huangtt

		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // ������Ա(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // �ܽ��(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setCancelFlg();
		// reg.getRegReceipt().getBilInvrcpt().setCancelUser();
		// reg.getRegReceipt().getBilInvrcpt().setCancelDate();
		// �жϳ�ʼ��Ʊ��
		// ===huangtt 20131212
		// if(ticketFlg){
		// reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// if (RegMethodTool.getInstance().selPrintFlg(
		// this.getValueString("REGMETHOD_CODE"))) {
		// // ��ʾ��һƱ��
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo()
		// .length() == 0) {
		// this.messageBox("��δ����");
		// return false;
		// }
		// }
		// }

		if ("Y".equals(reg.getApptCode())) {
			if (this.getPopedem("LEADER")) {
				this.messageBox("���鳤����ԤԼ!");
				return false;
			}
		}
		return true;
	}

	/**
	 * �������ͳ������
	 * 
	 * @param admDate
	 *            String
	 * @return boolean flg =false ����������ִ�� UPDATE QUE_NO ����
	 */
	private boolean onSaveParm(String admDate) {
		if (SchDayTool.getInstance().isVipflg(reg.getRegion(),
				reg.getAdmType(), admDate, reg.getSessionCode(),
				reg.getClinicroomNo())) {
			int row = (Integer) callFunction("UI|Table2|getSelectedRow");
			if (row < 0)
				return false;
			// �õ�table�ؼ�
			TTable table2 = (TTable) callFunction("UI|table2|getThis");
			TParm data = table2.getParmValue();
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					data, row);
			// 20090217 �·��� -------end---------
			// =======pangben 2012-7-31 �޸Ĳ�ѯ�Ƿ��Ѿ�vipռ��
			String regAdmTime =TypeTool.getString(table2.getValueAt(row, table2.getColumnIndex("START_TIME"))); //add by huangtt 20140910
			int queNoVIP = TypeTool.getInt(table2.getValueAt(row, table2
					.getColumnIndex("QUE_NO")));
			String vipSql = "SELECT QUE_NO,QUE_STATUS FROM REG_CLINICQUE "
					+ "WHERE ADM_TYPE='"
					+ reg.getAdmType()
					+ "' AND ADM_DATE='"
					+ TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8) + "'" + " AND SESSION_CODE='"
					+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
					+ reg.getClinicroomNo() + "' AND  QUE_NO='" + queNoVIP
					+ "' AND QUE_STATUS='N'";
			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				this.messageBox("��ռ��!");
				// ��ʼ������VIP���
				onQueryVipDrTable();
				return false;
			}
			if (queNoVIP == 0) {
				this.messageBox("����VIP�����!");
				return false;
			}
			reg.setQueNo(queNoVIP);
			reg.setRegAdmTime(regAdmTime);  //add by huangtt 20140910
			reg.setVipFlg(true);
			if (reg.getQueNo() == -1) {
				this.messageBox("E0017");
				return false;
			}

		} else {
			int queNo = SchDayTool.getInstance().selectqueno(
					reg.getRegion(),
					reg.getAdmType(),
					TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8), reg.getSessionCode(),
					reg.getClinicroomNo());
			if (queNo == 0) {
				this.messageBox("���޾����!");
				return false;
			}
			reg.setQueNo(queNo);
			if (reg.getQueNo() == -1) {
				// ���޺Ų��ܹҺ�
				this.messageBox("E0017");
				return false;
			}
		}
		// =========pangben 2012-6-18
		TParm regParm = reg.getParm();
		regParm.setData("ADM_DATE", admDate);
		if (onSaveQueNo(regParm)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * �������ͳ������
	 * 
	 * @param ctz1Code
	 *            String
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 */
	private void onSaveParm(String ctz1Code, String ctz2Code, String ctz3Code) {
		if (!feeShow) { // �ж��Ƿ���ҽ�����Ļ�õķ���====�Ͼ�ҽ��ʹ�ã�����feeShow=false ����ִ��
			// feeShow=true
			reg.getRegReceipt().setRegFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
							ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));
			// 13����(REG_RECEIPT)
			reg.getRegReceipt().setClinicFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")),
							"CLINIC_FEE", ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));

		} else {
			reg.getRegReceipt().setRegFee(
					TypeTool.getDouble(getValue("REG_FEE")));
			reg.getRegReceipt().setClinicFee(
					TypeTool.getDouble(getValue("CLINIC_FEE")));
		}
	}

	/**
	 * ҽ�ƿ��Һų������⳷������
	 */
	private void cancleEKTData() {
		// ҽ�ƿ��Һų������⳷������
		TParm oldParm = new TParm();
		oldParm.setData("BUSINESS_NO", businessNo);
		oldParm.setData("TREDE_NO", tredeNo);
		TParm result = TIOM_AppServer.executeAction("action.ins.EKTAction",
				"deleteRegOldData", oldParm);
		// if (result.getErrCode() < 0)
		// System.out.println("err:" + result.getErrText());
	}

	/**
	 * ̩�ĹҺ��Ŷӽк�
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		// System.out.println("========caseNo=========="+caseNo);
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// �Һ�����
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// �������
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("DR_CODE", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("USER_NAME", 0) + "|";
		// �ű�
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// ���
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// ���߲�����
		sendString += result.getValue("MR_NO", 0) + "|";

		// ��������
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// �����Ա�

		sendString += result.getValue("SEX", 0) + "|";
		// ��������
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// �������
		sendString += result.getValue("QUE_NO", 0) + "|";

		// System.out.println("==adm date=="+result.getValue("ADM_DATE",0));

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// System.out.println("===noSql=="+noSql);
		// �޹�����
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// �ѹ����� noParm.getValue("QUE_NO", 0)+ "|";
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";
		// this.messageBox("SESSION_CODE"+((TComboBox)
		// this.getComponent("SESSION_CODE")).getSelectedText());
		// ʱ���
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));
		// System.out.println("===timeSql==="+timeSql);

		// �˹ҽк�
		if ("UNREG".equals(type)) {
			// ԤԼ����

			inParm.setData("msg", sendString);
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + Que_No.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 **/
			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);
			// this.messageBox("�˹ҽк�!");

		} else if ("REG".equals(type)) {
			// System.out.println("adm time===="+this.reg.getRegAdmTime());
			sendString += "|";
			// ԤԼ����
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				// sendString += result.getValue("ADM_DATE", 0).replaceAll("-",
				// "").substring(
				// 0, 8)+startTimeParm.getValue("START_TIME", 0) + "00";
				// System.out.println("========ԤԼsendString=========="+sendString);
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|�ڷ��ڴ�л��|000875|�����|����ҽʦ|06����|000000001009|������|Ů|1936-01-05|2|60|2|����|
			inParm.setData("msg", sendString);
			// this.messageBox("�ҺŽк�!");
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + QueNo.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 * System.out.println("Reg_sendString--->" + sendString);
			 **/

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction", "doReg",
					inParm);

		}

		return "true";

	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 * 
	 * @param flg
	 *            boolean
	 */
	public void onClickClinicType(boolean flg) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// �Һŷ�
		this.setValue("REG_FEE", reg_fee1);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		// ����ǰ��
		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// ����
		this.setValue("CLINIC_FEE", celinic_fee1);
		// //Ӧ�շ���
		// if (pat != null) {
		setValue("FeeY", reg_fee + clinic_fee);
		// add by huangtt 20140115
		if (aheadFlg) {
			if (flg) { // ԤԼ�ҺŲ���ʾӦ�ս��
				setValue("FeeS", reg_fee + clinic_fee);
			}
		}

		// }
	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 */
	public void onClickClinicType() {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// �Һŷ�
		this.setValue("REG_FEE", reg_fee1);

		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// ����
		this.setValue("CLINIC_FEE", celinic_fee1);
		// //Ӧ�շ���
		setValue("FeeY", reg_fee + clinic_fee);

		if (aheadFlg) {

			setValue("FeeS", reg_fee + clinic_fee);

		}
	}

	/**
	 * �ű�Comboֵ�ı��¼�
	 * 
	 * @param fee
	 *            int
	 */
	public void onClickClinicType(int fee) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		double reg_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// �Һŷ�
		this.setValue("REG_FEE", reg_fee1);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), "", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		double celinic_fee1 = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// ����
		this.setValue("CLINIC_FEE", celinic_fee1);
		// Ӧ�շ���
		double feeY = reg_fee + clinic_fee;

		// if (pat != null)
		setValue("FeeY", feeY * fee);
		if (aheadFlg) {
			setValue("FeeS", feeY * fee);
		}

	}

	/**
	 * ֧����ʽ�ı��¼�
	 */
	public void onSelPayWay() {
		if (getValue("PAY_WAY").equals("PAY_CHECK"))
			callFunction("UI|REMARK|setEnabled", true);
		else
			callFunction("UI|REMARK|setEnabled", false);
		if (getValue("PAY_WAY").equals("PAY_DEBIT"))
			callFunction("UI|CONTRACT_CODE|setEnabled", true);
		else
			callFunction("UI|CONTRACT_CODE|setEnabled", false);

	}

	/**
	 * ��ѯҽʦ�Ű�(һ��)
	 * 
	 */
	public void onQueryDrTable() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// ɸѡ����ר�����ͨ��
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		// ���ǹ���Ȩ��
		if (this.getPopedem("deptFilter"))
			parm.setData("DEPT_CODE_SORT", "1101020101");
		TParm data = SchDayTool.getInstance().selectDrTable(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
		TTable table = (TTable) this.getComponent("Table1");
		int selRow = table.getSelectedRow();
		if (selRow < 0)
			return;
		String drCode = table.getItemString(selRow, 4);
		String clinicroomNo = table.getItemString(selRow, 3);
		String sql = "SELECT SEE_DR_FLG FROM REG_PATADM" + "  WHERE DR_CODE='"
				+ drCode + "' " + "AND  CLINICROOM_NO ='" + clinicroomNo + "'"
				+ "AND SEE_DR_FLG='N'";
		// System.out.println("sql===="+sql);
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = selparm.getCount();
		this.setValue("COUNT", count + "");
	}

	/**
	 * ��ѯҽʦ�Ű�(VIP)
	 */
	public void onQueryVipDrTable() {
		TTable table2 = new TTable();
		table2.removeAll();
		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
				true);
		parm.setData("ADM_TYPE", admType);
		parm.setData("VIP_ADM_DATE", StringTool.getString(
				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
		if (data2.getErrCode() < 0) {
			messageBox(data2.getErrText());
			return;
		}
		this.callFunction("UI|Table2|setParmValue", data2);
	}

	/**
	 * ��ѯ�����Һ���Ϣ
	 */
	// CASE_NO;ADM_DATE;SESSION_CODE;DEPT_CODE;DR_CODE;QUE_NO;ADM_STATUS;ARRIVE_FLG;CONFIRM_NO;INS_PAT_TYPE;REGMETHOD_CODE
	public void selPatInfoTable() {
		TParm parm = new TParm();
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_START_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_END_DATE")), "yyyyMMdd");
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("YY_START_DATE", startTime);
		parm.setData("YY_END_DATE", endTime);
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		TParm data = PatAdmTool.getInstance().selPatInfoForREG(parm);
		// System.out.println("table3��ʾ����" + data);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table3|setParmValue", data);

	}

	/**
	 * ���ݿ��������б���ѯҽʦ�Ű�(һ��)
	 */
	public void onQueryDrTableByDrCombo() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		parm.setDataN("DEPT_CODE_SORT", TypeTool
				.getString(getValue("DEPT_CODE_SORT")));
		parm.setDataN("DR_CODE_SORT", TypeTool
				.getString(getValue("DR_CODE_SORT"))); // add by huangtt
		// 20140227
		// ɸѡ����ר�����ͨ��
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}

			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		TParm data = SchDayTool.getInstance().selectDrTable(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
	}

	/**
	 * ��������
	 */
	public void onFee() {
		DecimalFormat df = new DecimalFormat("##########0.00");
		// ������
		setValue("FeeZ", TypeTool.getDouble(df.format(getValue("FeeS")))
				- TypeTool.getDouble(df.format(getValue("FeeY"))));
		// �õ�����
		this.grabFocus("SAVE_REG");
	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		// TParm forPrtParm = new TParm();
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		String caseNo = (String) table3.getValueAt(row, 0);

		if (ticketFlg) {
			String confirmNo = (String) table3.getParmValue().getValue(
					"CONFIRM_NO", row);
			// if (this.getValueString("NEXT_NO").length() <= 0
			// || this.getValueString("NEXT_NO").compareTo(endInvNo) > 0) {
			// this.messageBox("Ʊ��������!");
			// return;
			// }
			TParm temp = new TParm();
			temp.setData("RECEIPT_TYPE", "REG");
			temp.setData("CASE_NO", caseNo);
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				temp.setData("REGION_CODE", Operator.getRegion());
			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					temp);
			if (null != result && result.getValue("BIL_STATUS", 0).equals("1")) {
				this.messageBox("���˹Һŷ���û��ִ�н������,�����Դ�Ʊ");
				return;
			}

			TParm onREGReprintParm = new TParm();
			onREGReprintParm.setData("CASE_NO", caseNo);
			onREGReprintParm.setData("OPT_USER", Operator.getID());
			onREGReprintParm.setData("OPT_TERM", Operator.getIP());
			onREGReprintParm.setData("ADM_TYPE", admType);
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onREGReprint", onREGReprintParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��ӡ����ʧ��");
				return;
			}
			result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
			result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("CONFIRM_NO", confirmNo);
			TParm mzConfirmParm = INSMZConfirmTool.getInstance()
					.queryMZConfirm(parm); // �жϴ˴β����Ƿ���ҽ������
			if (mzConfirmParm.getErrCode() < 0) {
				return;
			}
			TParm printParm = null;
			if (mzConfirmParm.getCount() > 0) {
				printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
				insFlg = true;
			}
			onRePrint(result, mzConfirmParm, printParm);
		}
		if (singleFlg) {

			// " WHERE REGION_CODE = '"+reg.getRegion()+"'" +
			// " AND ADM_TYPE = '"+reg.getAdmType()+"'" +
			// " AND ADM_DATE = '"+reg.getAdmDate().toString().replace("-",
			// "").replace("/", "").substring(0, 8)+"'" +
			// " AND SESSION_CODE = '"+reg.getSessionCode()+"'" +
			// " AND REALDEPT_CODE = '"+reg.getRealdeptCode()+"'" +
			// " AND REALDR_CODE = '"+reg.getRealdrCode()+"'" +
			// " AND CLINICROOM_NO='"+reg.getClinicroomNo()+"'" ;

			TParm parm = table3.getParmValue();
			onPrintReg(caseNo, "(copy)"); // add by huangtt 20140331

		}

		this.onInit();
	}

	/**
	 * ��ӡ
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
		// ��ʾ��ʽ:����(����)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "��)"); // �ű�
		// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		parm.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		DecimalFormat df = new DecimalFormat("########0.00");
		if (tjINS) {
			ektNewSum = df.format(p3.getDouble("CURRENT_BALANCE"));
		}
		parm.setData("CURRENT_BALANCE", "TEXT", "�� "
				+ df.format(Double.parseDouble(ektNewSum == null
						|| "".equals(ektNewSum) ? "0.00" : ektNewSum))); // ҽ�ƿ�ʣ����

		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_CASH", "TEXT", "�ֽ�:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // �ֽ�
			// �����˻�
			String sqlamt = " SELECT ACCOUNT_PAY_AMT  FROM INS_OPD "
					+ " WHERE CASE_NO ='"
					+ mzConfirmParm.getValue("CASE_NO", 0) + "'"
					+ " AND CONFIRM_NO ='"
					+ mzConfirmParm.getValue("CONFIRM_NO", 0) + "'";
			TParm insaccountamtParm = new TParm(TJDODBTool.getInstance()
					.select(sqlamt));
			if (insaccountamtParm.getErrCode() < 0) {

			} else {
				parm.setData("PAY_ACCOUNT", "TEXT", "�˻�:"
						+ StringTool.round(insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0), 2));
				parm.setData("PAY_DEBIT", "TEXT", "ҽ��:"
						+ StringTool.round((printParm.getDouble("PAY_INS_CARD",
								0) - insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0)), 2)); // ҽ��֧��
			}
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// ҽ��������Ա�����ʾ
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		// ===zhangp 20120313 start
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			// parm.setData("Cost_class", "TEXT", "��ͳ");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			// parm.setData("Cost_class", "TEXT", "����");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}
		// ===zhangp 20120313 end
		String caseNo = parm.getValue("CASE_NO", "TEXT");// add by wanglong
		// 20121217
		TParm oldDataRecpParm = BILREGRecpTool.getInstance().selForRePrint(
				caseNo);// add by wanglong 20121217
		parm.setData("RECEIPT_NO", "TEXT", oldDataRecpParm.getData(
				"RECEIPT_NO", 0));// add by wanglong 20121217
		// this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
		// parm, true);
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REGRECPPrint.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
		this.openPrintDialog(IReportTool.getInstance().getReportPath(
				"REGRECPPrint.jhw"), IReportTool.getInstance().getReportParm(
				"REGRECPPrint.class", parm), true);// ����ϲ�modify by wanglong
		}
		// 20130730
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.initReg();
		clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;RESID_ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;NHI_NO;EKT_CURRENT_BALANCE;COUNT"
				+ " ;REMARKS;CURRENT_ADDRESS;REQUIREMENT"
				+ " ;MEM_CODE;START_DATE;END_DATE;GUARDIAN_NAME;GUARDIAN_RELATION;GUARDIAN_TEL;GUARDIAN_PHONE;NATION_CODE;SPECIES_CODE;MARRIAGE_CODE"
				+ ";REG_CTZ2;DEPT_CODE_SORT;DR_CODE_SORT;INSURE_INFO;PAT_PACKAGE;REASSURE_FLG"); // ====add by
		// huangtt 20131106
		clearValue("FIRST_NAME;LAST_NAME;CELL_PHONE;CUSTOMER_SOURCE");
		// ============huangtt modify 20131106 start ֤������Ĭ����ʾ���֤
		String sql = "SELECT ID FROM SYS_DICTIONARY WHERE CHN_DESC = '���֤' AND GROUP_ID = 'SYS_IDTYPE'";
		TParm typeParm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("ID_TYPE", typeParm.getValue("ID", 0));
		// ============huangtt modify 20131106 end
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		callFunction("UI|FOREIGNER_FLG|setEnabled", true); // ����֤���ɱ༭======pangben
		// modify 20110808
		// if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
		// callFunction("UI|MR_NO|setEnabled", false);
		// this.grabFocus("PAT_NAME");
		// }
		// if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
		// callFunction("UI|MR_NO|setEnabled", true);
		// this.grabFocus("MR_NO");
		// }
		// callFunction("UI|MR_NO|setEnabled", true); //����֤���ɱ༭======pangben
		// modify 20110808
		callFunction("UI|CONTRACT_CODE|setEnabled", true); // ���˵�λ�ɱ༭
		callFunction("UI|SAVE_REG|setEnabled", true);// �շѰ�ť�ɱ༭
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// feeIstrue = false;
		ins_amt = 0.00; // ҽ�����
		accountamtforreg = 0.00;// �����˻�
		feeShow = false; // �Ͼ�ҽ�����Ļ�÷�����ʾ
		txEKT = false; // ̩��ҽ�ƿ�д���ܿ�
		p3 = null; // ҽ�ƿ�����parm
		insFlg = false; // ҽ������������
		tjINS = false; // ҽ�ƿ�������ɲ���
		reSetEktParm = null; // ҽ�ƿ��˷�ʹ���ж��Ƿ�ִ��ҽ�ƿ��˷Ѳ���
		confirmNo = null; // ҽ��������ţ��˹�ʱʱʹ��
		reSetCaseNo = null; // �˹�ʹ�þ������
		insType = null;
		tableFlg = false; // �ж�ѡ�е�һ��ҳǩ�������
		ektNewSum = "0.00"; // ҽ�ƿ��ۿ����
		// ===zhangp 20120427 start
		greenParm = null;// //��ɫͨ��ʹ�ý��
		aheadFlg = REGSysParmTool.getInstance().selAeadFlg();
		ticketFlg = REGSysParmTool.getInstance().selTicketFlg();
		singleFlg = REGSysParmTool.getInstance().selSingleFlg();
		
//		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		initSchDay();
		callFunction("UI|SAVE_REG|setEnabled", true);
		pat = null;
		// reg=null;
		ins_exe = false;
		preFlg = "N";// yanjing 20131212
		
		// add by huangtt 20140211
		// ��ʼ��������
		TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
			setValue("VISIT_CODE_F", "Y");
		}

		TTextFormat dept_code_sort = (TTextFormat) this
				.getComponent("DEPT_CODE_SORT");
		dept_code_sort.onQuery();
		TTextFormat dr_code_sort = (TTextFormat) this
				.getComponent("DR_CODE_SORT");
		dr_code_sort.onQuery();
		//add by huangtt 20140915 start
		TTextFormat dept_code = (TTextFormat) this.getComponent("DEPT_CODE");
		dept_code.onQuery();
		TTextFormat dr_code = (TTextFormat) this.getComponent("DR_CODE");
		dr_code.onQuery();
		TTextFormat clinicroom_no = (TTextFormat) this.getComponent("CLINICROOM_NO");
		clinicroom_no.onQuery();
		//add by huangtt 20140915 end
		saveFlg = false;
		clinictypeCode="";
		
		TButton btton=(TButton) this.getComponent("SAVE_PAT");
		btton.setEnabled(true);
		callFunction("UI|INSURE_INFO|setEnabled", false);//add by huangjw 20150731
		callFunction("UI|PAT_PACKAGE|setEnabled", false);//add by huangjw 20150731
	}

	/**
	 * �Ƿ�رմ���
	 * 
	 * @return boolean true �ر� false ���ر�
	 */
	public boolean onClosing() {
		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		return true;
	}

	/**
	 * ��ʼ��ʱ��
	 */
	public void initSession() {
		// ��ʼ��ʱ��Combo,ȡ��Ĭ��ʱ��
		String defSession = SessionTool.getInstance().getDefSessionNow_New(admType,
				Operator.getRegion());
		setValue("SESSION_CODE", defSession);
		setValue("VIP_SESSION_CODE", defSession);
	}

	/**
	 * Ϊ��պ��ʼ��
	 */
	public void initReg() {
		// ����Ĭ�����
		setValue("CTZ1_CODE", "99");
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// ��������
		combo_ctz.setNhiFlg("");
		combo_ctz.onQuery();
		setValue("REG_CTZ1", "99");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("ADM_DATE", SystemTool.getInstance().getDate());
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// ����ʱ���ж�Ӧ����ʾ�����ڣ����������0������⣬���0������Ӧ����ʾǰһ������ڣ�
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
			this.setValue("ADM_DATE", admDate);
		}
		// ��ʼ��Ĭ��(�ֳ�)�Һŷ�ʽ
		setValue("REGMETHOD_CODE", "A");

		// ��ʼ��ԤԼ��Ϣ��ʼʱ��
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// ��ʼ��VIP���Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// ���˹�,����,��ӡ��ťΪ��
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// ���շѰ�ť�ɱ༭
		callFunction("UI|SAVE_REG|setEnabled", true);
		// �ùҺ���Ϣ����ؼ��ɱ༭
		setControlEnabled(true);
		setRegion();
	}

	/**
	 * ͨ���ʱ�ĵõ�ʡ��
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
		if (parm.getErrCode() != 0 || parm.getCount() == 0) {
			return;
		}
		setValue("STATE", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("CITY", parm.getData("POST_CODE", 0).toString());
		this.grabFocus("MARRIAGE_CODE");
	}

	/**
	 * ���������Ƿ��������
	 */
	public void setRegion() {
		if (!REGSysParmTool.getInstance().selOthHospRegFlg())
			callFunction("UI|REGION_CODE|setEnabled", false);
	}

	/**
	 * ͨ�����д�����������
	 */
	public void selectCode() {
		this.setValue("POST_CODE", this.getValue("CITY"));
	}

	/**
	 * ��ⲡ����ͬ����
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		// setPatName1();
		if (StringUtil.isNullString(patName)) {
			return;
		}
		// add by huangtt 20131126
		String sexCode = this.getValueString("SEX_CODE");
		if (StringUtil.isNullString(sexCode)) {
			this.grabFocus("PY1");
			return;
		}
		try {
			
			String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS "
					+ " , ID_TYPE, CURRENT_ADDRESS" // add by huangtt 20131106
					+ " FROM SYS_PATINFO A " // del ,EKT_ISSUELOG B
					+ " WHERE PAT_NAME = '"
					+ patName
					+ "' AND  SEX_CODE='"
					+ sexCode // add by huangtt 20131126
					+ "' "
					// + " AND A.MR_NO = B.MR_NO (+) "
					+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}

			// ѡ�񲡻���Ϣ
			if (same.getCount("MR_NO") > 0) {
				int sameCount = this.messageBox("��ʾ��Ϣ",
						"������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
				if (sameCount != 1) {
					this.grabFocus("ID_TYPE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		this.grabFocus("ID_TYPE");

	}

	/**
	 * ��ⲡ����ͬ���֤��
	 */
	public void onIDNo() {
		try {
			String idNo = this.getValueString("IDNO");
			String idType = this.getValueString("ID_TYPE"); // add by huangtt
			// 20131106

			// if (StringUtil.isNullString(idType)) { //add by huangtt 20131106
			// return;
			// }
			if (StringUtil.isNullString(idNo)) {
				return;
			}

			if (idType.equals("01")) {// ֤������Ϊ���֤
				if (!isCard(idNo)) {
					this.messageBox("¼������֤�Ų�����Ҫ��");
					this.grabFocus("IDNO");
					return;
				} else {
					if (idNo.length() > 14) {
						String time = idNo.substring(6, 14);
						String time2 = time.substring(0, 4) + "/"
								+ time.substring(4, 6) + "/"
								+ time.substring(6, 8);
						// System.out.println("time2="+time2);
						// ��ʾ��������
						this.setValue("BIRTH_DATE", time2);
						String sexCode = idNo.substring(idNo.length() - 2);
						sexCode = sexCode.substring(0, 1);
						// System.out.println("�Ա�"+sexCode);
						// �����Ա�
						setSexCode(sexCode);
						// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS

					}
				}
			}
			String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
					+ " POST_CODE, RESID_ADDRESS, A.MR_NO"
					// +",B.EKT_CARD_NO "
					+ " , ID_TYPE, CURRENT_ADDRESS" // add by huangtt
					// 20131106
					+ " FROM SYS_PATINFO A"
					// +",EKT_ISSUELOG B "
					+ " WHERE A.IDNO = '" + idNo + "'  "
					// + " AND A.ID_TYPE = '"+idType+"'" //add by
					// huangtt 20131106
					// + " AND A.MR_NO = B.MR_NO (+) "
					+ " ORDER BY A.OPT_DATE";
			// ===zhangp 20120319 end
			TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
			if (same.getErrCode() != 0) {
				this.messageBox_(same.getErrText());
			}
			// ѡ�񲡻���Ϣ
			if (same.getCount("MR_NO") > 0) {
				int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ������Ϣ,�Ƿ�������������Ϣ",
						0);
				if (sameCount != 1) {
					this.grabFocus("BIRTH_DATE");
					return;
				}
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						same);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					onQueryNO(patParm.getValue("MR_NO"));
					return;
				}
			}
//			this.grabFocus("BIRTH_DATE");
		} catch (Exception e) {
			// TODO: handle exception
		} 
		this.grabFocus("BIRTH_DATE");

	}

	// =====huangtt 20131106 start
	/**
	 * ��ⲡ����ͬ��������
	 */
	public void onBirthDate() {
		String birthDate = this.getValueString("BIRTH_DATE");
		if (StringUtil.isNullString(birthDate)) {
			return;
		}
		birthDate = birthDate.substring(0, 10).replace(":", "")
				.replace("-", "").replace(" ", "");
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " , ID_TYPE, CURRENT_ADDRESS"
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE BIRTH_DATE = "
				+ "TO_DATE ('"
				+ birthDate
				+ "', 'YYYYMMDD')"
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("SEX_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("SEX_CODE");
	}

	/**
	 * ��ⲡ����ͬ��סַ
	 */
	public void onCurrentAddress() {
		String currentAddress = this.getValueString("CURRENT_ADDRESS");
		if (StringUtil.isNullString(currentAddress)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " , ID_TYPE, CURRENT_ADDRESS"
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE CURRENT_ADDRESS = '"
				+ currentAddress
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";

		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}

		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ����������Ϣ,�Ƿ�������������Ϣ", 0);
			if (sameCount != 1) {
				this.grabFocus("REMARKS");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("NATION_CODE");
	}

	// =====huangtt 20131106 end

	/**
	 * ��ⲡ����ͬ���֤��
	 */
	public void onTelHome() {
		String telHome = this.getValueString("TEL_HOME");
		if (StringUtil.isNullString(telHome)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat =
		// ===zhangp 20120319 start
		"SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.TEL_HOME = '" + telHome + "'  "
				+ " AND A.MR_NO = B.MR_NO (+) " + " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// ѡ�񲡻���Ϣ
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("��ʾ��Ϣ", "������ͬ�绰���벡����Ϣ,�Ƿ�������������Ϣ",
					0);
			if (sameCount != 1) {
				this.grabFocus("POST_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("POST_CODE");
	}

	/**
	 * ����
	 */
	public void onArrive() {
		String caseNo = "";// yanjing 20131212
		String admdate = "";// yanjing 20131212
		// if(preFlg.equals("N")){
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		admdate = table3Parm.getValue("ADM_DATE", row);
		if (admdate.equals(null) || "".equals(admdate)) {
			admdate = "";
		} else {
			// admdate = table3Parm.getData("ADM_DATE", row).toString();
			admdate = admdate.substring(0, 10);
			String date = SystemTool.getInstance().getDate().toString();
			date = date.substring(0, 10);
			if (!admdate.equals(date)) {
				messageBox("�ǵ��գ����ܱ�����");
				return;
			}
		}
		// ====zhangp 20120306 modify end
		caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);

		// // ����ҽ�ƿ�
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("�����Ų���Ϊ��");
			return;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// �Һ�����,REG����
		// 2�ż���
		if (!onSaveRegParm(false))
			return;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8��������(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9�շ�����(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10�վݴ�ӡ����(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28�տ�Ա����(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // �Һ��վ�(REG_RECEIPT)
		// Ʊ������BilInvoice(For bil),BIL_INVOICE����
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// Ʊ����ϸ��BILInvrcpt(For bil),BIL_INVRCP����
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // Ʊ����ϸ���վݺ�(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1Ʊ������(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2��Ʊ����(BIL_INVRCP)
		// 7�վ�ӡˢ��(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // ������Ա(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // �ܽ��(BIL_INVRCP)
		// �жϳ�ʼ��Ʊ��
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// // ��ʾ��һƱ��
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
		// this.messageBox("��δ����");
		// return;
		// }
		reg.getRegReceipt().getBilInvoice().getParm();
		// �ż�������
		TParm saveParm = new TParm();

		// Ʊ������
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// Ʊ����ϸ��
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// ҽ��ҽ�Ʋ��� ���ò���
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����
		if (!onInsEkt(payWay, caseNo)) {
			return;
		}
		saveParm.setData("REG", regParm.getData());
		// �����վ�
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// ����ҽ������ִ���޸�REG_PADADM
			// ����INS_PAT_TYPE ��
			// COMFIRM_NO �ֶ�
		}
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// ҽ�ƿ�������д���
			if (payWay.equals("EKT")) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", ektOldSum);
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
			}
			return;
		}

		// �����Ʊ����
		result = onPrintParm();
		if (ticketFlg) {
			onPrint(result);
		}
		if (singleFlg) {
			onPrintReg(caseNo, ""); // add by huangtt 20140331
		}
		this.onClear();
		// ���������Ŷӽк�
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("�к�ʧ��");
		}
		BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// // ��ʼ����һƱ��
		// setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
	}

	/**
	 * ���� yanjing Ԥ����鱨��
	 */
	public boolean onArrivePre() {
		String caseNo = "";// yanjing 20131212
		String admdate = "";// yanjing 20131212
		// if(preFlg.equals("N")){
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		admdate = table3Parm.getValue("ADM_DATE", row);
		if (admdate.equals(null) || "".equals(admdate)) {
			admdate = "";
		} else {
			// admdate = table3Parm.getData("ADM_DATE", row).toString();
			admdate = admdate.substring(0, 10);
			String date = SystemTool.getInstance().getDate().toString();
			date = date.substring(0, 10);
			if (!admdate.equals(date)) {
				messageBox("�ǵ��գ����ܱ�����");
				return false;
			}
		}
		// ====zhangp 20120306 modify end
		caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);

		// // ����ҽ�ƿ�
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("�����Ų���Ϊ��");
			return false;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// �Һ�����,REG����
		// 2�ż���
		if (!onSaveRegParm(false))
			return false;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8��������(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9�շ�����(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10�վݴ�ӡ����(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28�տ�Ա����(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // �Һ��վ�(REG_RECEIPT)
		// Ʊ������BilInvoice(For bil),BIL_INVOICE����
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// Ʊ����ϸ��BILInvrcpt(For bil),BIL_INVRCP����
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // Ʊ����ϸ���վݺ�(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1Ʊ������(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2��Ʊ����(BIL_INVRCP)
		// 7�վ�ӡˢ��(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // ������Ա(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // �ܽ��(BIL_INVRCP)
		// �жϳ�ʼ��Ʊ��
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// ��ʾ��һƱ��
		// if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
		// || reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
		// this.messageBox("��δ����");
		// return false;
		// }
		reg.getRegReceipt().getBilInvoice().getParm();
		// �ż�������
		TParm saveParm = new TParm();

		// Ʊ������
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// Ʊ����ϸ��
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// ҽ��ҽ�Ʋ��� ���ò���
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // ֧�����
		if (!onInsEkt(payWay, caseNo)) {
			return false;
		}
		saveParm.setData("REG", regParm.getData());
		// �����վ�
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// ����ҽ������ִ���޸�REG_PADADM
			// ����INS_PAT_TYPE ��
			// COMFIRM_NO �ֶ�
		}
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// ҽ�ƿ�������д���
			if (payWay.equals("EKT")) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", ektOldSum);
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
			}
			return false;
		}

		// �����Ʊ����
		result = onPrintParm();
		if (ticketFlg) {
			onPrint(result);
		}
		if (singleFlg) {
			onPrintReg(caseNo, ""); // add by huangtt 20140331
		}

		this.onClear();
		// ���������Ŷӽк�
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("�к�ʧ��");
		}
		BilInvoice invoice = new BilInvoice();
		// invoice = invoice.initBilInvoice("REG");
		// // ��ʼ����һƱ��
		// setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
		return true;
	}

	/**
	 * ����Ӣ����
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * �˹Ҳ���
	 */
	public void onUnReg() {
		// =====zhangp 20120301 modify start
		if (this.messageBox("ѯ��", "�Ƿ��˹�", 2) == 0) {
			this.callFunction("UI|unreg|setEnabled", false);
			if (!this.getPopedem("LEADER")) {
				this.messageBox("���鳤�����˹�!");
				return;
			}
			TTable table3 = (TTable) callFunction("UI|table3|getThis");
			int row = table3.getSelectedRow();
			if (row < 0) {
				this.messageBox("��ѡ��Ҫ�˹ҵ�����");
				return;
			}
			
			//�ж��Ƿ�������Ʒ�
			String caseNoUnreg = table3.getParmValue().getValue(
							"CASE_NO", row);
			String sql2 = "SELECT RX_NO,AR_AMT  FROM OPD_ORDER " + 
					"		     WHERE CASE_NO = '"+caseNoUnreg+"' " + 
					"		       AND RX_NO = 'CLINIC_FEE' ";
			
			TParm updateParm2 = new TParm(TJDODBTool.getInstance().select(sql2));
			System.out.println("updateParm2::::::"+updateParm2);
			if (updateParm2.getData("RX_NO")!=null || updateParm2.getDouble("AR_AMT")>0) {
				messageBox("�ѿ����޷��˺ţ�");
				return;
			}
			
			// ===zhangp 20120316 start
			String arriveFlg = (String) table3.getValueAt(row, 7);
			// �ж��Ƿ�ԤԼ�Һ�
			if ("N".equals(arriveFlg)) {
				table3.getParmValue().getRow(row);
				String sql = "UPDATE REG_PATADM SET REGCAN_USER = '"
						+ Operator.getID()
						+ "',REGCAN_DATE = SYSDATE,OPT_USER = '"
						+ Operator.getID() + "',"
						+ "OPT_DATE = SYSDATE,OPT_TERM = '" + Operator.getIP()
						+ "' " + "WHERE CASE_NO = '"
						+ table3.getParmValue().getValue("CASE_NO", row) + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("�˹�ʧ��");
					return;
				}
				String admDate = table3.getParmValue()
						.getValue("ADM_DATE", row);
				admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
						+ admDate.substring(8, 10);
				sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
						+ table3.getParmValue().getValue("ADM_TYPE", row)
						+ "'AND ADM_DATE = '"
						+ admDate
						+ "' AND "
						+ "SESSION_CODE = '"
						+ table3.getParmValue().getValue("SESSION_CODE", row)
						+ "' AND "
						+ "CLINICROOM_NO = '"
						+ table3.getParmValue().getValue("CLINICROOM_NO", row)
						+ "' AND "
						+ "QUE_NO = '"
						+ table3.getParmValue().getValue("QUE_NO", row) + "'";
				updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("�˹�ʧ��");
					return;
				}
				// �õ�Ԥ�������
				String isPre = table3.getParmValue().getValue("IS_PRE_ORDER",
						row);
				if (isPre.equals("Y")) {
					// Ԥ����飬�˹ҵ�ͬʱɾ��opd_order�е�Ԥ�����ҩ��
					String preCaseNo = table3.getParmValue().getValue(
							"CASE_NO", row);
					// ����case_no��opd_order��õ�rx_no
					String selRxNo = "SELECT RX_NO FROM OPD_ORDER WHERE CASE_NO = '"
							+ preCaseNo + "'";
					TParm selRxNoParm = new TParm(TJDODBTool.getInstance()
							.select(selRxNo));
					if (selRxNoParm.getCount() > 0) {// ɾ��opd_order�е�ҽ��
						String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '"
								+ preCaseNo + "' AND IS_PRE_ORDER = 'Y'";
						TParm delPreParm = new TParm(TJDODBTool.getInstance()
								.update(delPreSql));
						if (delPreParm.getErrCode() < 0) {
							messageBox("ԤԼ��Ϣ�˹�ʧ��");
							return;
						}
					}
				}

				messageBox("ԤԼȡ���ɹ�");
				// �����Ŷӽк�
				if (!"true".equals(callNo("UNREG", table3.getParmValue()
						.getValue("CASE_NO", row)))) {
					this.messageBox("�к�ʧ��");
				}
				
				// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ START
				this.cancelMroRegAppointment(table3.getParmValue().getValue(
						"CASE_NO", row));
				// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ END
				
				this.onClear();
				return;
			}
			// ===zhangp 20120316 end

			// �õ�Ԥ�������
			String isPre = table3.getParmValue().getValue("IS_PRE_ORDER", row);
			if (isPre.equals("Y")) {
				// Ԥ����飬�˹ҵ�ͬʱɾ��opd_order�е�Ԥ�����ҩ��
				String preCaseNo = table3.getParmValue().getValue("CASE_NO",
						row);
				// ����case_no��opd_order��õ�rx_no
				String selRxNo = "SELECT RX_NO FROM OPD_ORDER WHERE CASE_NO = '"
						+ preCaseNo + "'";
				TParm selRxNoParm = new TParm(TJDODBTool.getInstance().select(
						selRxNo));
				if (selRxNoParm.getCount() > 0) {// ɾ��opd_order�е�ҽ��
					String delPreSql = "DELETE OPD_ORDER WHERE CASE_NO = '"
							+ preCaseNo + "' AND IS_PRE_ORDER = 'Y'";
					TParm delPreParm = new TParm(TJDODBTool.getInstance()
							.update(delPreSql));
					if (delPreParm.getErrCode() < 0) {
						messageBox("ԤԼ��Ϣ�˹�ʧ��");
						return;
					}
				}
			}

			String caseNo = (String) table3.getValueAt(row, 0);
			TParm tredeParm = new TParm(); // ��ѯ�˴��˹Ҳ����Ƿ���ҽ�ƿ��˹�
			tredeParm.setData("CASE_NO", caseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // ����
			tredeParm.setData("STATE", "1"); // ״̬�� 0 �ۿ� 1 �ۿ��Ʊ 2�˹� 3 ����
			confirmNo = table3.getParmValue().getValue("CONFIRM_NO", row); // ҽ�������
			reSetCaseNo = table3.getParmValue().getValue("CASE_NO", row); // ҽ���˹�ʹ��
			insType = table3.getParmValue().getValue("INS_PAT_TYPE", row); // ҽ����������1.��ְ��ͨ2.��ְ����
			// 3.�Ǿ�����
			if (null != confirmNo && confirmNo.length() > 0) {
				// ִ��ҽ������
				// System.out.println("ҽ�����˷�");
			} else {
				reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // ҽ�ƿ��˷Ѳ�ѯ
				if (reSetEktParm.getErrCode() < 0) {
					this.messageBox("�˹�ִ������");
					return;
				}
				if (reSetEktParm.getCount() > 0) { // ������ڵ���û�л��ҽ�ƿ���Ϣ����ʾ==pangb
					// 2011-11-29
					String payWay = this.getValueString("PAY_WAY");
					if (!"EKT".equals(payWay)) {
						this.messageBox("���ȡҽ�ƿ���Ϣ");
						return;
					}
				}
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("RECEIPT_TYPE", "REG");
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				parm.setData("REGION_CODE", Operator.getRegion());

			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					parm);
			// ��ѯ�Ƿ��м�����Ϣ
			if (null != result && result.getCount() > 0) {
				// �Ѿ�������ɵĹҺŷ�
				if ("2".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes2(caseNo, true);
				} else if ("1".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes1(caseNo);
				}
				// �����˹�
			} else {
				onUnRegNo(caseNo, false);
			}
			
			// add by wangbin 20180206 �˹ҳɹ��������ʱ���ȡ��״̬ START
			this.cancelMroRegAppointment(caseNo);
			// add by wangbin 20180206 �˹ҳɹ��������ʱ���ȡ��״̬ END
			
			this.onClear();
		} else
			return;

	}

	/**
	 * �������֤
	 */
	public void idnoInfo() {
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfoFromID.x");
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();

	}

	/**
	 * ̩��ҽ�ƿ��ۿ����
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	private boolean onTXEktSave(String FLG, TParm insParm) {
		int type = 0;
		TParm parm = new TParm();
		// ���ʹ��ҽ�ƿ������ҿۿ�ʧ�ܣ��򷵻ز�����
		if (EKTIO.getInstance().ektSwitch()) { // ҽ�ƿ����أ���¼�ں�̨config�ļ���
			if (null == insParm)
				parm = onOpenCard(FLG);
			else
				parm = onOpenCard(FLG, insParm);
			// System.out.println("��ҽ�ƿ�parm=" + parm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			// System.out.println("type===" + type);
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == 2) {
				return false;
			}
			if (type == -1) {
				this.messageBox("��������!");
				return false;
			}
			tredeNo = parm.getValue("TREDE_NO");
			businessNo = parm.getValue("BUSINESS_NO"); // //����ҽ�ƿ��ۿ��������ʹ��
			ektOldSum = parm.getValue("OLD_AMT"); // ִ��ʧ�ܳ����Ľ��
			ektNewSum = parm.getValue("EKTNEW_AMT"); // �ۿ��Ժ�Ľ��
			// �ж��Ƿ������ɫͨ��
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				greenParm = parm;
			}
			// System.out.println("ektNewSum======"+ektNewSum);
		} else {
			this.messageBox_("ҽ�ƿ��ӿ�δ����");
			return false;
		}
		return true;

	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @param FLG
	 *            String
	 * @return boolean
	 */
	public boolean onEktSave(String FLG) {
		if (aheadFlg) {
			return onTXEktSave(FLG, null);
		}
		return true;
	}

	/**
	 * ��ҽ�ƿ�
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG) {
		if (reg == null) {
			return null;
		}
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("INS_FLG", "N");
		// ҽ�����������ֽ���ȡ
		reg.setInsPatType(""); // ����ҽ������ ��Ҫ���浽REG_PATADM���ݿ����1.��ְ��ͨ 2.��ְ����
		// 3.�Ǿ�����
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))); // ҽ�ƿ��Ѿ��շѵ�����
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);

		return parm;
	}

	/**
	 * ���ҽ��
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG, TParm insParm) {
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
		orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // ҽ�����ԷѲ��ֽ��
		orderParm.setData("INS_FLG", "Y"); // ҽ����ע��
		orderParm.setData("OPBEKTFEE_FLG", true);// ȡ����ť
		orderParm.setData("RECP_TYPE", "REG"); // ���EKT_ACCNTDETAIL ������ʹ��
		orderParm.setData("comminuteFeeParm", insParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�ز���
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // �˲��������շ�ҽ�������Ѿ���Ʊ��
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // ��ʾ���
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);
		return parm;
	}

	/**
	 * ҽ�ƿ����
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // д�̶�ֵ
		orderParm.addData("ORDER_CODE", "REG"); // д�̶�ֵ
		orderParm.addData("SEQ_NO", "1"); // д�̶�ֵ
		orderParm.addData("EXEC_FLG", "N"); // д�̶�ֵ
		orderParm.addData("RECEIPT_FLG", "N"); // д�̶�ֵ
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}

	/**
	 * �˹Ҳ���ҽ�ƿ��˷Ѳ���
	 * 
	 * @param caseNo
	 * @param type
	 *            1.����ҽ�ƿ��˷� 2.ҽ�����˷�
	 * @return
	 */
	public TParm onOpenCardR(String caseNo) {
		// ׼������ҽ�ƿ��ӿڵ�����
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // д�̶�ֵ
		orderParm.addData("ORDER_CODE", "REG"); // д�̶�ֵ
		orderParm.addData("SEQ_NO", "1"); // д�̶�ֵ
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.addData("EXEC_FLG", "N"); // д�̶�ֵ
		orderParm.addData("RECEIPT_FLG", "N"); // д�̶�ֵ
		orderParm.addData("BILL_FLG", "N");
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "��" : "Ů");
		orderParm.setData("BUSINESS_TYPE", "REGT");
		orderParm.setData("TYPE_FLG", "Y");
		if (null != confirmNo && confirmNo.length() > 0) {
			orderParm.setData("OPBEKTFEE_FLG", true);
		}
		orderParm.setData("ektParm", p3.getData());
		// ��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
		TParm parm = new TParm();
		parm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		parm.setData("CASE_NO", caseNo);
		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(parm);
		orderParm.setData("EXE_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0)); // ҽ�ƿ��Ѿ��շѵ�����
		orderParm.setData("SHOW_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0));
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// System.out.println("MR_NO" + pat.getMrNo());
		// System.out.println("�˹Ҵ�����"+TypeTool.getDouble(getValue("FeeY")));
		// ��ҽ�ƿ�������ҽ�ƿ��Ļش�ֵ
		parm = EKTIO.getInstance().onOPDAccntClient(orderParm, caseNo, this);
		return parm;
	}

	/**
	 * ̩��ҽ�ƿ����� =========================pangben modify 20110808
	 * 
	 * @throws ParseException
	 */
	public void onEKT() throws ParseException {
		// �Ͼ�ҽ������������
		// ̩��ҽ�ƿ�����
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.�ͷŶ����豸
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.ע��TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // ҽ�ƿ�����
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // �Ͼ�ҽ����
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // ̩��ҽ�ƿ�
			this.setValue("PAY_WAY", "EKT"); // ֧����ʽ�޸�
			this.setValue("CONTRACT_CODE", "");
			// callFunction("UI|CONTRACT_CODE|setEnabled", false); // ���˵�λ���ɱ༭
		}
		// ͨ�����֤�Ų����Ƿ���ڴ˲�����Ϣ
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//����֤�����ɱ༭
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// �Ͼ�ҽ��������
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// add by huangtt 20140115 start
		this.setValue("EKTMR_NO", p3.getValue("MR_NO"));
		String EKTCARD_CODE = p3.getData("CARD_NO").toString();
		this.setValue("EKTCARD_CODE", EKTCARD_CODE);
		this.setValue("CURRENT_BALANCE", p3.getValue("CURRENT_BALANCE"));
		// add by huangtt 20140115 end

		// ===zhangp 20120318 end

		// //add by huangtt 20140109 ���û�Ա������ =======start=======
		// String cardTypeSql =
		// "SELECT CTZ_CODE FROM SYS_CTZ WHERE MEM_CODE = '"+p3.getValue("CARD_TYPE")+"' AND DEPT_CODE IS NULL";
		// TParm cardParm = new
		// TParm(TJDODBTool.getInstance().select(cardTypeSql));
		// if(cardParm.getCount()>0){
		// setValue("REG_CTZ1", cardParm.getValue("CTZ_CODE", 0));
		// }
		// //add by huangtt 20140109 ���û�Ա������ ========end========
		//		
	}
	
	/**
     * ���֤�����ش�ֵadd by huangjw 20141110
     * @param parm
     */
    public void setValueByQueryIdNo(TParm parm){
    	if(this.getValueString("MR_NO").equals(parm.getValue("MR_NO"))){
    		//TParm p = this.getParmForTag("MR_NO;PY1");
    		clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
    		this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
    	}else{
    		//this.setValue("MR_NO",parm.getValue("MR_NO"));
    		onQueryNO(parm.getValue("MR_NO", 0));
    		clearValue("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS");
    		this.setValueForParm("PAT_NAME;SEX_CODE;BRITHPLACE;BIRTH_DATE;IDNO;RESID_ADDRESS", parm);
    	}
		
	}
	
	/**
	 * ������������� ==============pangben 2013-3-18
	 */
	public void onReadIdCard() {
		if (JOptionPane.showConfirmDialog(null, "�Ƿ񸲸ǣ�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {// �����֤ʱ������ʾ��Ϣ��add by huangjw 20141119
			TParm idParm = IdCardO.getInstance().readIdCard();
			if (idParm.getErrCode() < 0) {
				this.messageBox(idParm.getErrText());
				return;
			}
			if (idParm.getCount() > 0) {// ����������ʾ
				if (idParm.getCount() == 1) {// pangben 2013-8-8 ֻ����һ������
					// onQueryNO(idParm.getValue("MR_NO", 0));
					setValueByQueryIdNo(idParm);// modify by huangjw 20141110
					onAddress(); // add by huangtt 20131122
									// �ж���סַ�Ƿ�Ϊ�գ���Ϊ�գ���ͬ����ϸ��ַ
				} else {
					Object obj = openDialog(
							"%ROOT%\\config\\sys\\SYSPatChoose.x", idParm);
					TParm patParm = new TParm();
					if (obj != null) {
						patParm = (TParm) obj;
						setValueByQueryIdNo(patParm);// modify by huangjw 20141110
						// onQueryNO(patParm.getValue("MR_NO"));
					} else {
						return;
					}
				}
				setValue("VISIT_CODE_F", "Y"); // ����
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
				setPatName1();// ����Ӣ��
			} else {
				String sql = "SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
						+ idParm.getValue("PAT_NAME") + "%'";
				TParm infoParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (infoParm.getCount() <= 0) {
					this.messageBox(idParm.getValue("MESSAGE"));
					setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
					callFunction("UI|MR_NO|setEnabled", false); // �����Ų��ɱ༭--�������
				} else {
					this.messageBox("������ͬ�����Ĳ�����Ϣ");
					this.grabFocus("PAT_NAME");// Ĭ��ѡ��
				}
				this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
				this.setValue("IDNO", idParm.getValue("IDNO"));
				this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
				this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
				this
						.setValue("RESID_ADDRESS", idParm
								.getValue("RESID_ADDRESS"));// ��ַ
				this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
						TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
				setPatName1();// ����Ӣ��
			}
		}
	}

	/**
	 * ҽ�ƿ���������
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 * @throws ParseException
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) throws ParseException {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// ͨ�����֤�Ų����Ƿ���ڴβ���
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // ���ڽ���������ʾ
			onQueryNO(); // ִ�и�ֵ����
			setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			// modify
			// 20110808
			tjINS = true; // ���ҽ��ʹ�ã��ж��Ƿ�ִ����ҽ�ƿ�����
			callFunction("UI|PAY_WAY|setEnabled", false); // ֧�����
		} else {
			this.messageBox("��ҽ�ƿ���Ч"); // ��������ʾ�����ϵ���Ϣ�����֤�š����ơ�ҽ����
			switch (typeEKT) {
			// �Ͼ�ҽ���� û�д˲�����Ϣʱִ�и�ֵ����
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // ���֤��
				this.setValue("NHI_NO", p3.getValue("siNO")); // ҽ����
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // ����
				break;
			// ̩��ҽ�ƿ�û�д˲�����Ϣʱִ�и�ֵ����
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // ̩��ҽ�ƿ�д�������ܿ�
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // �����Ų��ɱ༭
			this.grabFocus("PAT_NAME");
			setValue("VISIT_CODE_C", "Y"); // Ĭ�ϳ���
		}
	}

	/**
	 * ҽ�ƿ�����
	 */
	public void TXonEKTR() {
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		if (null != p && p.getValue("MR_NO").length() > 0) {
			// zhangp 20111231 �޸�ҽ�ƿ���
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		// zhangp 20111227
		clearEKTValue();
	}

	/**
	 * ��ֵ����
	 * 
	 * @throws ParseException
	 */
	public void TXonEKTW() throws ParseException {
		if (this.getValueDouble("TOP_UPFEE") <= 0) {
			this.messageBox("��ֵ����ȷ");
			return;
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
			this.messageBox("֧����ʽ������Ϊ��ֵ");
			return;
		}
		if("WX".equals(this.getValue("GATHER_TYPE"))||"ZFB".equals(this.getValue("GATHER_TYPE"))){
			if(this.getValueString("EKT_PRINT_NO").trim().length()<=0){
				this.messageBox("΢�Ż�֧������Ҫ��Ʊ�ݺ���ӽ��׺��룡");
				return;
			}
		}
		
		
		// add by huangtt 20140228
		if (this.messageBox("��ֵ", "�Ƿ��ֵ" + this.getValueDouble("TOP_UPFEE")
				+ "Ԫ", 0) != 0) {
			return;
		}
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("��ҽ�ƿ���Ч");
			return;
		}
		// zhangp 20111227
		pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		TParm parm = new TParm();
		parm.setData("SEQ", p.getValue("SEQ")); // ���
		parm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2)); // ���
		parm.setData("MR_NO", p.getValue("MR_NO")); // ������

		if (null != p && p.getValue("MR_NO").length() > 0) {
			// result.setData("CURRENT_BALANCE",
			// this.getValue("CURRENT_BALANCE"));
			// yanjing ע
			// TParm result = EKTIO.getInstance().TXwriteEKTATM(parm,
			// p.getValue("MR_NO"));
			// if (result.getErrCode() < 0) {
			// this.messageBox_("ҽ�ƿ���ֵ����ʧ��");
			// return;
			// }
			insbilPay(parm, p);
		} else {
			this.messageBox("��ҽ�ƿ���Ч");
		}
		clearEKTValue();
		// =====zhangp 20120403 start
		onEKT();
	}

	/**
	 * ҽ�ƿ���ֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @param p
	 *            TParm
	 */
	private void insbilPay(TParm parm, TParm p) {
		// zhangp 20111227
		TParm result = new TParm();
		parmSum = new TParm();
		parmSum.setData("CARD_NO", pat.getMrNo() + p.getValue("SEQ"));
		parmSum.setData("CURRENT_BALANCE", parm.getValue("CURRENT_BALANCE"));
		parmSum.setData("CASE_NO", "none");
		parmSum.setData("NAME", pat.getName());
		parmSum.setData("MR_NO", pat.getMrNo());
		parmSum.setData("ID_NO", null != pat.getIdNo()
				&& pat.getIdNo().length() > 0 ? pat.getIdNo() : "none");
		parmSum.setData("OPT_USER", Operator.getID());
		parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmSum.setData("OPT_TERM", Operator.getIP());
		parmSum.setData("FLG", false);
		parmSum.setData("ISSUERSN_CODE", "��ֵ"); // ����ԭ��
		parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); // ֧����ʽ
		parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); // ֧����ʽ����
		parmSum.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // ��ֵ���
		parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); // �Ա�
		parmSum.setData("CARD_TYPE", this.getValue("CARD_TYPE")); // ��ע
		parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); // ��ע
		parmSum.setData("PRINT_NO", this.getValue("EKT_PRINT_NO")); // ҽ�ƿ�Ʊ�ݺ�
		parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); // Ʊ�ݺ�
		parmSum.setData("CREAT_USER", Operator.getID()); // ִ����Ա//=====yanjing
		// ��ϸ�����
		TParm feeParm = new TParm();
		feeParm.setData("ORIGINAL_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)); // ԭ���
		feeParm.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // ��ֵ���
		feeParm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		parmSum.setData("TRADE_NO",this.getValue("DESCRIPTION"));
		// EKT_ACCNTDETAIL ����
		parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm)
				.getData());
		// zhangp 20120112 EKT_BIL_PAY ���ֶ�
		parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime()); // �ۿ�����ʱ��
		parmSum.setData("PROCEDURE_AMT", 0.00); // PROCEDURE_AMT
		// bil_pay ��ֵ������
		parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
		// �������
		result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"TXEKTonFee", parmSum); //
		callFunction("UI|tButton_5|setEnabled", false);// ��ֵ��ť�����������������===pangben
		// 2013-7-1
		if (result.getErrCode() < 0) {
			this.messageBox("ҽ�ƿ���ֵʧ��");
			callFunction("UI|tButton_5|setEnabled", true);// ��ֵ��ť�����������������===pangben
			// 2013-7-1
			// parm = EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
			// if (parm.getErrCode() < 0) {
			// System.out.println("�س�ҽ�ƿ����ʧ��");
			// }
		} else {
			printBil = true;
			this.messageBox("ҽ�ƿ���ֵ�ɹ�");
			callFunction("UI|tButton_5|setEnabled", true);// ��ֵ��ť�����������������===pangben
			// 2013-7-1
			String bil_business_no = result.getValue("BIL_BUSINESS_NO"); // �վݺ�
			try {
				onPrint(bil_business_no, "");
				this.clearValue("EKT_PRINT_NO;EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;DESCRIPTION");
				this.setValue("TOP_UPFEE", 0.00);
				this.setValue("CURRENT_BALANCE", 0.00);
				this.setValue("SUM_EKTFEE", 0.00);
			} catch (Exception e) {
				this.messageBox("��ӡ��������,��ִ�в�ӡ����");
				// TODO: handle exception
			}
		}
	}

	/**
	 * дҽ�ƿ�
	 */
	public void writeCard() {
	}

	/**
	 * ָ��������Ϣ
	 */
	public void queryConusmeByID() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("�޿�");

			return;
		}
		result = EktDriver.queryConusmeByID("1008250000000021");
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * ��֤
	 */
	public void unConsume() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("�޿�");

			return;
		}
		result = EktDriver.unConsume(1000, "sys", "1008250000000021",
				StringTool.getString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKTBarcode() {
		TParm printParm = new TParm();
		if ((ektCard != null || ektCard.length() != 0)
				&& this.getValueString("MR_NO") != null) {
			printParm.setData("mrNo", "TEXT", this.getValueString("MR_NO")); // ������
			printParm.setData("patName", "TEXT", this
					.getValueString("PAT_NAME")); // ��������
			printParm.setData("barCode", "TEXT", ektCard); // �����
			this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGEktCard.jhw",
					printParm);
		} else {
			this.messageBox("���ȶ�ҽ�ƿ�");
		}

	}

	/**
	 * ����SESSION combo���ż����ԣ������ص�ǰ��SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// Ϊ�˽����SESSION_CODE��ʾ�ż������𣬷���һ������ʾ��TEXTFIELD��
		String sessionCode = SessionTool.getInstance().getDefSessionNow_New(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * �忨 ===================pangben modify 20110808
	 */
	public void clearCard() {
		// EKTIO.getInstance().saveMRNO1(parm, this,true);
		if (null == p3) {
			this.messageBox("û����Ҫ�忨������");
			return;
		}

		p3.setData("identifyNO", this.getValue("IDNO"));
		p3.setData("siNO", this.getValue("NHI_NO"));
		p3.setData("patientName", this.getValue("PAT_NAME"));
		boolean temp = EKTIO.getInstance().writeEKT(p3, true);
		if (temp) {
			// �޸Ľ��˲���ҽ���������
			StringBuffer sql = new StringBuffer();
			sql
					.append("UPDATE SYS_PATINFO SET NHI_NO='',OPT_DATE=SYSDATE WHERE MR_NO='"
							+ this.getValueString("MR_NO").trim() + "'");
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sql.toString()));
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				this.messageBox("�忨ʧ��");
				return;
			}
			this.messageBox("�忨�ɹ�");
		}
	}

	public void onClearRefresh() {
		this.initReg();
		clearValue(" PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;RESID_ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;INSURE_INFO;PAT_PACKAGE");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		// ����Ĭ�Ϸ���ȼ�
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		crmTime="";
		// ��������
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
	}

	/**
	 * �ؼ��ɱ༭����
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setControlEnabled(boolean flg) {
		// callFunction("UI|REGMETHOD_CODE|setEnabled", flg);
		callFunction("UI|ADM_DATE|setEnabled", flg);
		callFunction("UI|SESSION_CODE|setEnabled", flg);
		callFunction("UI|DEPT_CODE|setEnabled", flg);
		callFunction("UI|DR_CODE|setEnabled", flg);
		callFunction("UI|CLINICROOM_NO|setEnabled", flg);
		if(flg){
			callFunction("UI|CLINICTYPE_CODE|setEnabled", flg);
		}
		callFunction("UI|REG_FEE|setEnabled", flg);
		callFunction("UI|CLINIC_FEE|setEnabled", flg);
		
	}

	/**
	 * ��÷���
	 */
	public void showXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		feeShow = true;
		// String
		// mr_no=parm.getValue("TBR").trim().substring(1,parm.getValue("TBR").trim().indexOf("]"));

		// System.out.println("parm:::"+parm);
		// if(this.getValueString("MR_NO").trim().equals(mr_no)){
		if (null == parm)
			return;
		// feeIstrue = true;
		this.setValue("FeeY", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]"))); // �շ�
		this.setValue("REG_FEE", parm.getValue("GHF").substring(1,
				parm.getValue("GHF").indexOf("]"))); // �Һŷ�
		this.setValue("CLINIC_FEE", parm.getValue("ZLF").substring(1,
				parm.getValue("ZLF").indexOf("]"))); // ����
		this.setValue("FeeS", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]")));
		// }
	}

	/**
	 * �˹ҽ����ʾ ҽ�����Ļ�õļ۸���ʾ =====================pangben modify 20110815
	 * 
	 * @param caseNo
	 *            String
	 */
	private void unregFeeShow(String caseNo) {
		int feeunred = -1;
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT REG_FEE,CLINIC_FEE,AR_AMT FROM BIL_REG_RECP WHERE CASE_NO='"
						+ caseNo + "'"); // ����˹ҵĽ��
		// System.out.println("sql:::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		this.setValue("FeeY", result.getDouble("AR_AMT", 0) * feeunred); // �ܷ���
		this.setValue("REG_FEE", result.getDouble("REG_FEE", 0) * feeunred); // �Һ�
		this.setValue("CLINIC_FEE", result.getDouble("CLINIC_FEE", 0)
				* feeunred); // ����
		// add by huangtt 20140115
		if (aheadFlg) {
			this.setValue("FeeS", result.getDouble("AR_AMT", 0) * feeunred); // ��ȡ����
		}

	}

	/**
	 * ��������û�м��˵Ĳ��� flg �ж��Ƿ��Ǽ�������
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegNo(String caseNo, boolean flg) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm unRegParm = new TParm();

		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", optUser);

		// ��ѯ��ǰ�����Ƿ��������
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		inInvRcpParm.setData("RECP_TYPE", "REG");// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			//��ѯ�ùҺ������Ƿ��Ѿ��˹��� add by huangtt 20151224 start
			boolean isDedug=true; //add by huangtt 20160505 ��־���
			String sql = "SELECT REGCAN_USER FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
			TParm unParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(unParm.getValue("REGCAN_USER", 0).length() > 0){
				this.messageBox("�þ����¼���˹ң�");
				return;
			}
			if(isDedug){
				if(unParm.getErrCode() < 0){
					System.out.println(" come in class: REGPatAdmControl.class ��method ��onUnRegNo");
					System.out.println("err:"+unParm);

				}
			}
			//add by huangtt 20151224 end  
			reSetReg(unRegParm, caseNo, flg, "onUnRegForEKT", "onUnReg", "Y");
		} else {
			this.messageBox("�Ѳ�������,�����˹�!");
			return;
		}
	}

	/**
	 * �����˹Ҳ���:BIL_STATUS=2 �Ѿ������˹Ҳ���
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegYes2(String caseNo, boolean flg) {
		onUnRegNo(caseNo, flg);
	}

	/**
	 * �����˹Ҳ���:BIL_STATUS=1 �ж��Ƿ�������ã����û�в�������ֱ����ӡ��޸Ĳ���BIL_REG_RECP ����Ѿ��������ò������˹�
	 * 
	 * @param caseNo
	 *            String
	 */
	private void onUnRegYes1(String caseNo) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", Operator.getID());
		TParm unRegParm = new TParm();
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("RECP_TYPE", "REG");
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		unRegParm.setData("RECEIPT_NO", recpNo);
		unRegParm.setData("OPT_NAME", Operator.getName());
		// ��ѯ��ǰ�����Ƿ��������
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			// û��ִ�н���ķ��ò����˹�
			this.messageBox("û��ִ�н���,�����˷�");
			// ֱ����ӡ��޸Ĳ���BIL_REG_RECP
			// �ֽ��˹Ҷ���
			reSetReg(unRegParm, caseNo, false, "onUnRegForStatusEKT",
					"onUnRegStatus", "Y");
		} else {
			// �Ѳ�������
			this.messageBox("�Ѳ�������,�����˹�!");
		}

	}

	/**
	 * ����
	 */
	public void readINSCard() {
		String payWay = this.getValueString("PAY_WAY");// ֧����ʽ
		// ���ҽ��������
		tjReadINSCard(payWay);
	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean onSaveINSData() {
		boolean result = false;
		return result;
	}

	/**
	 * ���ҽ�ƿ���Ϣ
	 */
	public void ektOnClear() {
		clearValue("EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;TOP_UPFEE;SUM_EKTFEE;DESCRIPTION;CARD_TYPE");
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
	}

	/**
	 * ����Һ��վݴ�ӡ
	 * 
	 * @param parm
	 *            TParm
	 * 
	 */
	private void onPrint(TParm parm) {
		// //����С��
		// sOTOT_Amt = ""+ TiMath.round( Double.parseDouble(sOTOT_Amt),2);

		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // ������������
		// ��ʾ��ʽ:����(����)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "��)"); // �ű�
		// ��ʾ��ʽ:�ű�(���)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		parm.setData("BALANCE_NAME", "TEXT", "�� ��"); // �������
		DecimalFormat df = new DecimalFormat("########0.00");
		// parm.setData("CURRENT_BALANCE", "TEXT", "�� "
		// + df.format(Double.parseDouble(ektNewSum == null
		// || "".equals(ektNewSum) ? "0.00" : ektNewSum))); // ҽ�ƿ�ʣ����
		parm
				.setData(
						"CURRENT_BALANCE",
						"TEXT",
						"�� "
								+ df
										.format(Double
												.parseDouble(ektNewSum == null
														|| "".equals(ektNewSum) ? ""
														+ df
																.format((Double
																		.parseDouble(getValueString(
																				"EKT_CURRENT_BALANCE")
																				.equals(
																						"") ? "0"
																				: getValueString("EKT_CURRENT_BALANCE"))
																		- parm
																				.getDouble(
																						"TEXT",
																						"REGFEE") - parm
																		.getDouble(
																				"TEXT",
																				"CLINICFEE")))
														: ektNewSum))); // ҽ�ƿ�ʣ����
		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_DEBIT", "TEXT", "ҽ��:"
					+ StringTool.round((parm.getDouble("INS_SUMAMT") - parm
							.getDouble("ACCOUNT_AMT_FORREG")), 2)); // ҽ��֧��
			parm.setData("PAY_CASH", "TEXT", "�ֽ�:"
					+ StringTool.round((parm.getDouble("TOTAL", "TEXT") - parm
							.getDouble("INS_SUMAMT")), 2)); // �ֽ�
			parm
					.setData("PAY_ACCOUNT", "TEXT", "�˻�:"
							+ StringTool.round(parm
									.getDouble("ACCOUNT_AMT_FORREG"), 2)); // �˻�
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ insParm.getParm("opbReadCardParm").getValue(
							"SP_PRESON_TYPE") + "'";// ҽ��������Ա�����ʾ
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // ����
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		// ===zhangp 20120313 start
		if ("1".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "�Ŵ������ѽ���");
			// parm.setData("Cost_class", "TEXT", "��ͳ");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		} else if ("2".equals(insType) || "3".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			// parm.setData("Cost_class", "TEXT", "����");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "���������ѽ���");
			}
		}
		// ===zhangp 20120313 end
		parm.setData("RECEIPT_NO", "TEXT", reg.getRegReceipt().getReceiptNo());// add
		// by
		// wanglong
		// 20121217
		// this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
		// parm, true);
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("REGRECPPrint.prtSwitch");
		if(IReportTool.ON.equals(prtSwitch)){
		this.openPrintDialog(IReportTool.getInstance().getReportPath(
				"REGRECPPrint.jhw"), IReportTool.getInstance().getReportParm(
				"REGRECPPrint.class", parm), true);// ����ϲ�modify by wanglong
		}
		// 20130730
	}

	/**
	 * ���ҽ������������
	 * 
	 * @param payWay
	 *            String
	 */
	private void tjReadINSCard(String payWay) {
		// yanjing ɾ����SERVICE_LEVEL����� 20130807
		clearValue("REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ ");
		initSchDay();
		if (null == pat && !this.getValueBoolean("VISIT_CODE_C")) {
			this.messageBox("���Ȼ�ò�����Ϣ");
			return;
		}

		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ,5 :���صǼǣ�
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm) {
			this.setValue("PAY_WAY", payWay); // ֧����ʽ�޸�
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // ��ȡ״̬ 1.�ɹ� 2.ʧ��
		if (returnType == 0 || returnType == 2) {
			this.messageBox("��ȡҽ����ʧ��");
			this.setValue("PAY_WAY", payWay); // ֧����ʽ�޸�
			return;
		}

		int crowdType = insParm.getInt("CROWD_TYPE"); // ҽ����ҽ��� 1.��ְ 2.�Ǿ�
		insType = insParm.getValue("INS_TYPE"); // ҽ����������: 1.��ְ��ͨ 2.��ְ���� 3.�Ǿ�����
		// ============pangben 2012-4-8 ��ѯ�����Ƿ����ҽ��У��
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (this.getValueBoolean("VISIT_CODE_C")
				&& this.getValue("MR_NO").toString().trim().length() <= 0) {// ������ҽ������
			this.setValue("PAT_NAME", name);
			this.setValue("IDNO", opbReadCardParm.getValue("SID").trim());
			this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // ҽ������
			// ========pangben 2013-3-5 ��ӳ��ﲡ�˲�����Ϣ����
			setPatName1();
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// ��ƴ
			// ÿ��ˢ����Ҫ����������ϵͳ���ݡ����صǼǽ���ʱ�䡱�뵱ǰʱ����бȽ�
			if (!insType.equals("1")) {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTH_DATE") ? opbReadCardParm.getValue(
						"BIRTH_DATE").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(6, 8) : "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX_CODE"));
			} else {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTHDAY") ? opbReadCardParm.getValue(
						"BIRTHDAY").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(6,
								opbReadCardParm.getValue("BIRTHDAY").length())
						: "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX"));
			}
			return;
		}
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
		// ===================pangben 2012-04-09ҽ���ܿ����
		// ÿ��ˢ����Ҫ����������ϵͳ���ݡ����صǼǽ���ʱ�䡱�뵱ǰʱ����бȽ�
		if (!insType.equals("1")) {

			// �������صǼ���Ч����X��X��X�գ����ڴ�ʱ��ǰ2�����ڵ����򲡼������İ������϶�
			String mtEndDate = opbReadCardParm.getValue("MT_END_DATE");// ���صǼǽ���ʱ��
			this.messageBox("�������صǼ���Ч����" + mtEndDate
					+ "�����ڴ�ʱ��ǰ2�����ڵ����򲡼������İ������϶�");
		}
		// ============pangben 2012-4-9 stop
		// �ж���Ⱥ���
		// ������ۿ۶��ո�ֵ
		// 11����ְ��ͨ ,11:ҽ����\ 12����ְ����,21:ҽ���� \13����ְ����,51:ҽ����
		// 21:�Ǿ������� ,11:ҽ����\22:�Ǿ�ѧ����ͯ 12:ҽ���� \23���Ǿӳ������,13:ҽ����
		this.setValue("REG_CTZ1", insParm.getValue("CTZ_CODE"));
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// ��������
		combo_ctz.setNhiFlg(crowdType + "");
		combo_ctz.onQuery();
		insFlg = true; // ҽ������ȡ�ɹ�
		callFunction("UI|REG_CTZ1|setEnabled", true); // ������
		callFunction("UI|PAY_WAY|setEnabled", false); // ֧�����
		this.setValue("PAY_WAY", "INS"); // ֧����ʽ�޸�
		this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // ҽ������
		this.grabFocus("FeeS");
	}

	/**
	 * ̩��ҽԺҽ�����������
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm TXsaveINSCard(TParm parm, String caseNo) {
		// û�л��ҽ�ƿ���Ϣ �ж��Ƿ�ִ���ֽ��շ�
		if (!tjINS && !insFlg) {
			if (this.messageBox("��ʾ", "û�л��ҽ�ƿ���Ϣ,ִ���ֽ��շ��Ƿ����", 2) != 0) {
				return null;
			}
		}
		if (tjINS) { // ҽ�ƿ�����
			if (p3.getDouble("CURRENT_BALANCE") < this.getValueDouble("FeeY")) {
				this.messageBox("ҽ�ƿ�����,���ֵ");
				return null;
			}
		}
		TParm result = new TParm();
		insParm.setData("REG_PARM", parm.getData()); // ҽ����Ϣ
		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // ���Ҵ���
		insParm.setData("MR_NO", pat.getMrNo()); // ������

		reg.setCaseNo(caseNo);
		insParm.setData("RECP_TYPE", "REG"); // ���ͣ�REG / OPB
		insParm.setData("CASE_NO", reg.caseNo());
		insParm.setData("REG_TYPE", "1"); // �Һű�־:1 �Һ�0 �ǹҺ�
		insParm.setData("OPT_USER", Operator.getID());
		insParm.setData("OPT_TERM", Operator.getIP());
		insParm.setData("DR_CODE", this.getValue("DR_CODE"));// ҽ������
		// insParm.setData("PAY_KIND", "11");// 4 ֧�����:11���ҩ��21סԺ//֧�����12��
		if (this.getValueString("ERD_LEVEL").length() > 0) {
			insParm.setData("EREG_FLG", "1"); // ����
		} else {
			insParm.setData("EREG_FLG", "0"); // ��ͨ
		}

		insParm.setData("PRINT_NO", this.getValue("NEXT_NO")); // Ʊ��
		insParm.setData("QUE_NO", reg.getQueNo());

		TParm returnParm = insExeFee(true);
		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
			return null;
		}
		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.ʧ�� 1. �ɹ�
		if (returnType == 0 || returnType == -1) { // ȡ������
			return null;
		}

		insParm.setData("comminuteFeeParm", returnParm.getParm(
				"comminuteFeeParm").getData()); // ���÷ָ�����
		insParm.setData("settlementDetailsParm", returnParm.getParm(
				"settlementDetailsParm").getData()); // ���ý���

		// System.out.println("insParm:::::::"+insParm);
		result = INSTJReg.getInstance().insCommFunction(insParm.getData());

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			// this.messageBox("ҽ��ִ�в���ʧ��");
			return result;
		}
		// System.out.println("ҽ����������:" + insParm);
		// boolean messageFlg = false; // ҽ��������� ִ���ֽ��տ�
		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
		result.setData("ACCOUNT_AMT_FORREG", returnParm
				.getDouble("ACCOUNT_AMT_FORREG")); // �˻����
		insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // ҽ�����
		if (tjINS) { // ҽ�ƿ�����
			// TParm insExeParm = insExe(returnParm.getDouble("ACCOUNT_AMT"),
			// p3,
			// reg.caseNo(), "REG", 9);
			// if (insExeParm.getErrCode() < 0) {
			// return insExeParm;
			// }
			// ִ��ҽ�ƿ��ۿ��������Ҫ�ж�ҽ�������ҽ�ƿ����
			if (!onTXEktSave("Y", result)) {
				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
						"deleteOldData", insParm);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					result.setErr(-1, "ҽ����ִ�в���ʧ��");
					// return result;
				}
				result.setErr(-1, "ҽ�ƿ�ִ�в���ʧ��");
				return result;
			}
			// result = new TParm();// ִ���������REG_PATADM
		}
		return result;
	}

	/**
	 * ҽ����ִ�з�����ʾ���� flg �Ƿ�ִ���˹� false�� ִ���˹� true�� �����̲���
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(boolean flg) {
		TParm insFeeParm = new TParm();
		if (flg) {
			insFeeParm.setData("insParm", insParm.getData()); // ҽ����Ϣ
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		} else {
			insFeeParm.setData("CASE_NO", reSetCaseNo); // �˹�ʹ��
			insFeeParm.setData("INS_TYPE", insType); // �˹�ʹ��
			insFeeParm.setData("RECP_TYPE", "REG"); // �˹�ʹ��
			insFeeParm.setData("CONFIRM_NO", confirmNo); // �˹�ʹ��
		}
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // ������

		insFeeParm.setData("FeeY", this.getValueDouble("FeeY")); // Ӧ�ս��
		insFeeParm.setData("PAY_TYPE", tjINS); // ֧����ʽ
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // �������
		insFeeParm.setData("FEE_FLG", flg); // �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
		TParm returnParm = new TParm();
		if (flg) { // ������
			// returnParm=INSTJReg.getInstance().onInsFee(insFeeParm, this);
			returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
					insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")
					|| returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// �˷�����
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// ҽ�����
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// ��ְ
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - returnIns
							.getDouble("UNACCOUNT_PAY_AMT")), 2);
					this.messageBox("ҽ���˷ѽ��:"
							+ accountAmt
							+ " �ֽ��˷ѽ��:"
							+ StringTool.round(returnIns
									.getDouble("UNACCOUNT_PAY_AMT"), 2));

				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// �Ǿ�
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ARMY_AI_AMT");// �ֽ���
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);

					this.messageBox("ҽ���˷ѽ��:" + accountAmt + " �ֽ��˷ѽ��:"
							+ StringTool.round(payAmt, 2));
				}

				returnParm.setData("RETURN_TYPE", 1); // ִ�гɹ�
				returnParm.setData("ACCOUNT_AMT", accountAmt);// ҽ�����
			}

		}
		return returnParm;
	}

	/**
	 * ҽ��ִ���˷Ѳ���
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * ���˲�����֧����ʽ���ü���
	 */
	public void contractSelect() {

		if (this.getValue("CONTRACT_CODE").toString().length() > 0) {
			this.setValue("PAY_WAY", "C4"); // ����

		} else {
			this.setValue("PAY_WAY", "C0"); // �ֽ�
		}
	}

	/**
	 * �����ҽ�ƿ� zhangp 20121216
	 */
	public void ektCard() {
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", sendParm);
	}

	/**
	 * ҽ�ƿ���ϸ���������==============zhangp 20111227
	 * 
	 * @param p
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBusinessParm(TParm p, TParm feeParm) {
		// ��ϸ������
		TParm bilParm = new TParm();
		bilParm.setData("BUSINESS_SEQ", 0);
		bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
		bilParm.setData("MR_NO", pat.getMrNo());
		bilParm.setData("CASE_NO", "none");
		bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("SEQ_NO", 0);
		bilParm.setData("CHARGE_FLG", "3"); // ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����)
		bilParm.setData("ORIGINAL_BALANCE", feeParm
				.getValue("ORIGINAL_BALANCE")); // �շ�ǰ���
		bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
		bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
		bilParm.setData("CASHIER_CODE", Operator.getID());
		bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
		// 1������ִ�����
		// 2��˫��ȷ�����
		bilParm.setData("BUSINESS_STATUS", "1");
		// 1��δ����
		// 2�����˳ɹ�
		// 3������ʧ��
		bilParm.setData("ACCNT_STATUS", "1");
		bilParm.setData("ACCNT_USER", new TNull(String.class));
		bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
		bilParm.setData("OPT_USER", Operator.getID());
		bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		bilParm.setData("OPT_TERM", Operator.getIP());
		// p.setData("bilParm",bilParm.getData());
		return bilParm;
	}

	/**
	 * ��ֵ��������ݲ���==============zhangp 20111227
	 * 
	 * @param parm
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBillParm(TParm parm, TParm feeParm) {
		TParm billParm = new TParm();
		billParm.setData("CARD_NO", parm.getValue("CARD_NO")); // ����
		billParm.setData("CURT_CARDSEQ", 0); // ���
		billParm.setData("ACCNT_TYPE", "4"); // ��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
		billParm.setData("MR_NO", parm.getValue("MR_NO")); // ������
		billParm.setData("ID_NO", parm.getValue("ID_NO")); // ���֤��
		billParm.setData("NAME", parm.getValue("NAME")); // ��������
		billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); // ��ֵ���
		billParm.setData("CREAT_USER", Operator.getID()); // ִ����Ա
		billParm.setData("OPT_USER", Operator.getID()); // ������Ա
		billParm.setData("OPT_TERM", Operator.getIP()); // ִ��ip
		billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); // ֧����ʽ
		// 20120112 zhangp ���ֶ�
		billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
		billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
		return billParm;
	}

	/**
	 * ��ֵ��ӡ==============zhangp 20111227
	 * 
	 * @param bil_business_no
	 *            String
	 * @param copy
	 *            String
	 */
	private void onPrint(String bil_business_no, String copy) {
		if (!printBil) {
			this.messageBox("����ҽ�ƿ���ֵ�����ſ��Դ�ӡ");
			return;
		}
		boolean flg = false;
		TTable table = new TTable();
		parmSum.setData("TITLE", "�����ֵ�վ�");
		parmSum.setData("UnFeeFLG", "N");
		parmSum.setData("ACOUNT_NO", "");
		parmSum.setData("BIL_BUSINESS_NO", bil_business_no);
		EKTReceiptPrintControl.getInstance().onPrint(table, parmSum, copy, -2,
				pat, flg, this);
		// TParm parm = new TParm();
		// parm.setData("TITLE", "TEXT", (Operator.getRegion() != null
		// && Operator.getRegion().length() > 0 ? Operator
		// .getHospitalCHNFullName() : "����ҽԺ"));
		// parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // ������
		// parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // ����
		// parm.setData("GATHER_TYPE", "TEXT", parmSum
		// .getValue("GATHER_TYPE_NAME")); // �տʽ
		// parm.setData("AMT", "TEXT", StringTool.round(parmSum
		// .getDouble("BUSINESS_AMT"), 2)); // ���
		// // ====zhangp 20120525 start
		// // parm.setData("GATHER_NAME", "TEXT", "�� ��"); //�տʽ
		// parm.setData("GATHER_NAME", "TEXT", ""); // �տʽ
		// // ====zhangp 20120525 end
		// parm.setData("TYPE", "TEXT", "Ԥ ��"); // �ı�Ԥ�ս��
		// parm.setData("SEX_TYPE", "TEXT", pat.getSexCode().equals("1") ? "��"
		// : "Ů"); // �Ա�
		// parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(
		// parmSum.getDouble("BUSINESS_AMT"))); // ��д���
		// parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); //
		// ̨ͷһ
		// String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "yyyyMMdd"); // ������
		// String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "hhmmss"); // ʱ����
		// parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); // ̨ͷ��
		// yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "yyyy/MM/dd"); // ������
		// hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
		// .getInstance().getDBTime()), "HH:mm"); // ʱ����
		// parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION"));
		// // ��ע
		// parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); // Ʊ�ݺ�
		// if (null == bil_business_no)
		// bil_business_no = EKTTool.getInstance().getBillBusinessNo(); // ��ӡ����
		// parm.setData("ONFEE_NO", "TEXT", bil_business_no); // �վݺ�
		// parm.setData("PRINT_DATE", "TEXT", yMd); // ��ӡʱ��
		// parm.setData("DATE", "TEXT", yMd + "    " + hms); // ����
		// parm.setData("USER_NAME", "TEXT", Operator.getID()); // �տ���
		// parm.setData("COPY", "TEXT", copy); // �տ���
		// // ===zhangp 20120525 start
		// parm.setData("O", "TEXT", "");
		// // this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw",
		// // parm,true);
		// this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm,
		// true);
		// ===zhangp 20120525 end

	}

	/**
	 * ��ֵ�ı���س��¼�======zhangp 20111227
	 */
	public void addFee() {
		if (this.getValueDouble("TOP_UPFEE") < 0) {
			this.messageBox("��ֵ������Ϊ��ֵ");
			return;
		}
		this.setValue("SUM_EKTFEE", this.getValueDouble("TOP_UPFEE")
				+ this.getValueDouble("CURRENT_BALANCE"));
	}

	/**
	 * ���ҽ�ƿ�ҳǩ============zhangp 20111227
	 */
	public void clearEKTValue() {
		ektOnClear();
		// clearValue("DESCRIPTION;TOP_UPFEE;SUM_EKTFEE");
	}

	/**
	 * ɾ��ҽ����;״̬
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo, String exeType) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
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
	 * ҽ��֧����ֵ
	 * 
	 * @param result
	 *            ҽ�����صĲ���
	 * @param regFeeParm
	 *            ҽ���ָ��ҽ���Ľ��
	 * @return ����ҽ��֧���ܽ��
	 */
	private double tjInsPay(TParm result, TParm regFeeParm) {
		reg.getRegReceipt().setPayBankCard(0.00);
		reg.getRegReceipt().setPayCheck(0.00);
		reg.getRegReceipt().setPayDebit(0.00);
		reg.getRegReceipt().setPayInsCard(result.getDouble("INS_SUMAMT")); // ҽ�����
		double ins_amt = result.getDouble("INS_SUMAMT");
		if (!tjINS) { // �ֽ��շ�
			reg.getRegReceipt().setPayCash(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT"));
			reg.getRegReceipt().setPayMedicalCard(0.00); // ҽ�ƿ����
		} else { // ҽ�ƿ��շ�
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT")); // ҽ�ƿ����
		}
		TParm comminuteFeeParm = result.getParm("comminuteFeeParm"); // ���÷ָ�
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			for (int j = 0; j < comminuteFeeParm.getCount("ORDER_CODE"); j++) {
				if (regFeeParm.getValue("ORDER_CODE", i).equals(
						comminuteFeeParm.getValue("ORDER_CODE", j))) {
					if (comminuteFeeParm.getValue("RECEIPT_TYPE", j).equals(
							"REG_FEE")) {
						reg.getRegReceipt().setRegFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 12�ۿ�ǰ�Һŷ�(REG_RECEIPT)
						reg.getRegReceipt().setRegFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					} else {
						reg.getRegReceipt().setClinicFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 14�ۿ�ǰ����(REG_RECEIPT)
						reg.getRegReceipt().setClinicFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					}
					break;
				}
			}
		}
		return ins_amt;
	}

	/**
	 * �˹Ҳ���ʹ��
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 * @param ektName
	 *            String
	 * @param cashName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetReg(TParm unRegParm, String caseNo, boolean flg,
			String ektName, String cashName, String stutsFlg) {
		// TParm reSetInsParm=new TParm();
		if (!reSetInsSave(unRegParm.getValue("INV_NO")))
			return;
		if ("EKT".equals(this.getValueString("PAY_WAY"))) {
			// ��ӽ��п��˹ҷ�֧====pangben 2012-12-07
			TParm ccbParm = checkCcbReSet(caseNo);// �ж��Ƿ�ִ�н��п�����
			if (null == ccbParm || ccbParm.getCount() <= 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				// ���в���
				// TParm ccbp=checkCcbReSet(caseNo);
				unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));// ���н��
				reSetCcbSave(unRegParm, caseNo, stutsFlg);
			}
		} else if ("C0".equals(this.getValueString("PAY_WAY"))) { // �ֽ�
			reSetCashSave(unRegParm, stutsFlg, flg, cashName);
		} else if ("INS".equals(this.getValueString("PAY_WAY"))) { // ҽ����
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				TParm ccbParm = checkCcbReSet(caseNo);// �ж��Ƿ�ִ�н��п�����
				if (null == ccbParm || ccbParm.getCount() <= 0)
					reSetCashSave(unRegParm, stutsFlg, flg, cashName);
				else {
					// ���в���
					unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));
					reSetCcbSave(unRegParm, caseNo, stutsFlg);
				}
			}
		}
		// ҽ��ɾ����;״̬
		if (null != confirmNo && confirmNo.length() > 0) {
			if (!deleteInsRun(reSetCaseNo, "REGT"))
				return;
		}
	}

	/**
	 * ҽ���˹Ҳ���
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo) {
		TParm reSetInsParm = new TParm();
		if (null != confirmNo && confirmNo.length() > 0) {
			// ҽ�����˷� ��Ҫ�޸�ҽ�ƿ�����
			if (null == reSetCaseNo && reSetCaseNo.length() <= 0) {
				return false;
			}
			TParm tredeParm = new TParm(); // ��ѯ�˴��˹Ҳ����Ƿ���ҽ�ƿ��˹�
			tredeParm.setData("CASE_NO", reSetCaseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // ����
			tredeParm.setData("STATE", "1"); // ״̬�� 0 �ۿ� 1 �ۿ��Ʊ 2�˹� 3 ����
			TParm reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // ҽ�ƿ��˷Ѳ�ѯ
			if (reSetEktParm.getErrCode() < 0) {
				return false;
			}
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {// ҽ�ƿ��˹Ҳ���
				if (p3 == null || null == p3.getValue("MR_NO")
						|| p3.getValue("MR_NO").length() <= 0) {
					this.messageBox("ҽ�ƿ��˷�,��ִ�ж�������");
					return false;
				}
			}
			TParm parm = insExeFee(false);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // ȡ��
				return false;
			}
			reSetInsParm.setData("CASE_NO", reSetCaseNo); // �����
			reSetInsParm.setData("CONFIRM_NO", confirmNo); // ҽ�������
			reSetInsParm.setData("INS_TYPE", insType); // ҽ�������
			reSetInsParm.setData("RECP_TYPE", "REG"); // �շ�����
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // �˷�����
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // ҽ���������
			reSetInsParm.setData("PAT_TYPE", this.getValue("REG_CTZ1")); // ���
			reSetInsParm.setData("INV_NO", invNo); // Ʊ�ݺ�
			// System.out.println("reSetInsParm::::::" + reSetInsParm);
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				this.messageBox("ҽ���˹�ʧ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * У���Ƿ��п��˹Ҳ���
	 * 
	 * @return
	 */
	private TParm checkCcbReSet(String reSetCaseNo) {
		String sql = "SELECT CASE_NO,SUM(AMT) AS AMT FROM EKT_CCB_TRADE WHERE CASE_NO='"
				+ reSetCaseNo + "' AND BUSINESS_TYPE='REG' group by case_no";
		TParm reSetParm = new TParm(TJDODBTool.getInstance().select(sql));
		return reSetParm;
	}

	/**
	 * ���п��˷Ѳ��� =====pangben 2012-12-07
	 */
	private void reSetCcbSave(TParm unRegParm, String caseNo, String stutsFlg) {
		// ���ý��нӿ��˷�����
		unRegParm.setData("NHI_NO", regionParm.getValue("NHI_NO", 0));
		unRegParm.setData("RECEIPT_NO", unRegParm.getParm("RECP_PARM")
				.getValue("RECEIPT_NO", 0));
		// ���нӿڲ���
		// TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ccb.CCBServerAction", "getCcbRe", unRegParm);
		if (result.getErrCode() < 0) {
			this.messageBox("���нӿڵ��ó�������,����ϵ��Ϣ����");
			return;
		}
		unRegParm.setData("FLG", "N");
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("OPT_USER", Operator.getID());
		result.setData("BUSINESS_TYPE", "REGT");
		result = REGCcbReTool.getInstance().saveEktCcbTrede(result);
		if (result.getErrCode() < 0) {
			this.messageBox("�����˹�ʧ��");
			return;
		}
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onUnReg", unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// �����Ŷӽк�
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("�к�ʧ��");
		}
		if (stutsFlg.equals("Y")) {
			// add by huangtt 20131231 start
			String message = "���п��˹ҳɹ�!";
			// if(ticketFlg){
			// message = message + "Ʊ�ݺ�:"+unRegParm.getValue("INV_NO");
			// }
			this.messageBox(message);
			// this.messageBox("���п��˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
			// add by huangtt 20131231 end

			// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ START
			this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
			// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ END
		}
	}

	/**
	 * ҽ�ƿ��˷Ѳ���
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param ektName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetEktSave(TParm unRegParm, String caseNo, String ektName,
			String stutsFlg) {
		// ҽ�ƿ�
		TParm result = new TParm();
		if (EKTIO.getInstance().ektSwitch()) {
			if (aheadFlg) {
				result = onOpenCardR(caseNo);
				int type = 0;
				if (result == null) {
					this.messageBox("E0115");
					return;
				}
				type = result.getInt("OP_TYPE");
				if (type == 3 || type == -1) {
					this.messageBox("E0115");
					return;
				}
				if (type == 2) {
					return;
				}
				tradeNoT = result.getValue("TRADE_NO");
				unRegParm.setData("TRADE_NO", tradeNoT);
			} else {
				result = EKTpreDebtTool.getInstance().unRegForPre(caseNo);
				if (result.getErrCode() < 0) {
					messageBox(result.getErrText());
					return;
				}
			}
			// ҽ�ƿ��˹�
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					ektName, unRegParm);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				EKTIO.getInstance().unConsume(tradeNoT, this);
				return;
			}
			if (stutsFlg.equals("Y")) {
				// add by huangtt 20131231 start
				String message = "�˹ҳɹ�!";
				// if(ticketFlg){
				// message = message + "Ʊ�ݺ�:"+unRegParm.getValue("INV_NO");
				// }
				this.messageBox(message);
				// this.messageBox("�˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
				// add by huangtt 20131231 end
				
				// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ START
				this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
				// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ END
			}
			// �����Ŷӽк�
			if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
				this.messageBox("�к�ʧ��");
			}

		}
	}

	/**
	 * �ֽ��˷Ѳ���
	 * 
	 * @param unRegParm
	 *            �˹�����
	 * @param flg
	 *            �ֽ��˹ҹܿ�
	 * @param cashName
	 *            �ֽ����ACTION��ӿڷ�������
	 * @param stutsFlg
	 *            �ж��Ƿ�ִ����ʾ��Ϣ��
	 */
	private void reSetCashSave(TParm unRegParm, String stutsFlg, boolean flg,
			String cashName) {
		TParm result = new TParm();
		//add by huangtt 20141110  �˹�ʱ�ж��Ƿ�����ҽ��
		result = EKTpreDebtTool.getInstance().unRegForPre(unRegParm.getValue("CASE_NO"));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		
		
		if (stutsFlg.equals("Y")) {
			unRegParm.setData("FLG", flg);
		}
		// �ֽ��˹Ҷ���
		result = TIOM_AppServer.executeAction("action.reg.REGAction", cashName,
				unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// �����Ŷӽк�
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("�к�ʧ��");
		}
		if (stutsFlg.equals("Y")) {
			// add by huangtt 20131231 start
			String message = "�˹ҳɹ�!";
			// if(ticketFlg){
			// message = message + "Ʊ�ݺ�:"+unRegParm.getValue("INV_NO");
			// }
			this.messageBox(message);
			// this.messageBox("�˹ҳɹ�!Ʊ�ݺ�:" + unRegParm.getValue("INV_NO"));
			// add by huangtt 20131231 end

			// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ START
			this.cancelMroRegAppointment(unRegParm.getValue("CASE_NO"));
			// add by wangbin 20150806 �˹ҳɹ��������ʱ���ȡ��״̬ END
		}
	}

	/**
	 * VIP ������Һ�������ͬ
	 */
	public void onDateReg() {
		this.setValue("VIP_ADM_DATE", this.getValue("ADM_DATE"));
		onQueryVipDrTable();
	}

	public void onPast() {
		if (this.getValueString("BIRTH_DATE").length() > 0
				&& this.getValueString("BIRTH_DATE") != null)
			this.grabFocus("POST_CODE");
	}

	/**
	 * ����QUE_NO ����� ����غ����� ��ԭ�������һ�������ֳ����ȱ��������߼� ===============pangben
	 * 2012-6-18
	 */
	private boolean onSaveQueNo(TParm regParm) {
		// ����ű�
		TParm result = null;
		if (regParm.getBoolean("VIP_FLG")) {
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onSaveQueNo", regParm);
		} else {
			// ��ͨ��
			result = SchDayTool.getInstance().updatequeno(regParm);
		}
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * ����������ռ�� ====zhangp 20120629
	 * 
	 * @param temp
	 */
	private boolean queryQueNo(TParm temp) {
		String vipSql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE "
				+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
				+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
				+ temp.getValue("CLINICROOM_NO") + "' AND  QUE_STATUS='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (result.getErrCode() < 0) {
			messageBox("���ʧ��");
			return false;
		}
//		if (result.getCount() <= 0) {
//			messageBox("�޾����");
//			return;
//		}
		int queNo = result.getInt("QUE_NO", 0);
		//add by huangtt 20140926 start
		if(queNo == 0){
			messageBox("�޾����");
			return false;
		}
		//add by huangtt 20140926 end
		reg.setQueNo(queNo);
		
//		String sql = "SELECT START_TIME FROM REG_CLINICQUE "
//			+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
//			+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
//			+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
//			+ temp.getValue("CLINICROOM_NO") + "' AND QUE_NO = "+queNo;
//		TParm parmT = new TParm(TJDODBTool.getInstance().select(sql));
//		if(parmT.getCount() > 0)
//			reg.setRegAdmTime(parmT.getValue("START_TIME", 0));
		
		return true;
	}

	/**
	 * �ж���סַ�Ƿ���д����û��д������ϸ��ַһ�� ====add by huangtt 20131121
	 */
	public void onAddress() {
		String currentAddress = this.getValueString("CURRENT_ADDRESS");
		if (currentAddress.equals("")) {
			this.setValue("CURRENT_ADDRESS", this
					.getValueString("RESID_ADDRESS"));
		}
		this.grabFocus("SPECIES_CODE");
	}

	public void getCTZ() {
		String dept = this.getValueString("DEPT_CODE");
		// String ctz=p3.getValue("CARD_TYPE");
		String ctz = memCode;
		if (!ctz.equals("")) {
			String sql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE MAIN_CTZ_FLG='Y' AND CTZ_DEPT_FLG='Y' AND MEM_CODE = '"
					+ ctz + "' AND DEPT_CODE ='" + dept + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm.getCount() > 0) {
				this.setValue("REG_CTZ1", parm.getValue("CTZ_CODE", 0));
			} else {
				this.setValue("REG_CTZ1", this.getValue("CTZ1_CODE"));
			}
		}
		getCtzValid();
		getCtzValid2();

	}

	public void getCtzValid() {
		String ctz = this.getValueString("REG_CTZ1");
		String sql = "SELECT START_DATE,END_DATE,MEM_CODE,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			if (parm.getBoolean("USE_FLG", 0)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				String endDate = parm.getValue("END_DATE", 0).replace("-", "/")
						.substring(0, 10);
				String today = SystemTool.getInstance().getDate().toString()
						.replace("-", "/").substring(0, 10);
				try {
					if (sdf.parse(endDate).before(sdf.parse(today))) {
						this.messageBox("�ùҺ����һ�ѹ��ڣ�������ѡ��");
						setValue("REG_CTZ1", zfCtz);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!parm.getValue("MEM_CODE", 0).equals("")) {
				sql = "SELECT MR_NO FROM MEM_PATINFO  WHERE END_DATE > SYSDATE AND MEM_CODE='"
						+ parm.getValue("MEM_CODE", 0)
						+ "' AND MR_NO='"
						+ this.getValueString("MR_NO") + "'";
				// System.out.println(sql);
				TParm parmMem = new TParm(TJDODBTool.getInstance().select(sql));
				if (parmMem.getCount() < 0) {
					this.messageBox("�ùҺ����һ��Ӧ���ڸû��ߣ�������ѡ��");
					setValue("REG_CTZ1", zfCtz);
				}
			}
			onClickClinicType(true);
		}
	}

	public void getCtzValid2() {
		String ctz = this.getValueString("REG_CTZ2");
		String sql = "SELECT START_DATE,END_DATE,MEM_CODE,USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() > 0) {
			if (parm.getBoolean("USE_FLG", 0)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd",
						Locale.CHINA);
				String endDate = parm.getValue("END_DATE", 0).replace("-", "/")
						.substring(0, 10);
				String today = SystemTool.getInstance().getDate().toString()
						.replace("-", "/").substring(0, 10);
				try {
					if (sdf.parse(endDate).before(sdf.parse(today))) {
						this.messageBox("�ùҺ���ݶ��ѹ��ڣ�������ѡ��");
						setValue("REG_CTZ2", "");

					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (!parm.getValue("MEM_CODE", 0).equals("")) {
				sql = "SELECT MR_NO FROM MEM_PATINFO WHERE END_DATE > SYSDATE AND MEM_CODE='"
						+ parm.getValue("MEM_CODE", 0)
						+ "' AND MR_NO='"
						+ this.getValueString("MR_NO") + "'";
				TParm parmMem = new TParm(TJDODBTool.getInstance().select(sql));
				if (parmMem.getCount() < 0) {
					this.messageBox("�ùҺ���ݶ���Ӧ���ڸû��ߣ�������ѡ��");

					setValue("REG_CTZ2", "");
				}
			}

			onClickClinicType(true);
		}
	}

	public void mergePreCaseNo() {
		// ��Ԥ����ԤԼʱ��Ԥ������ԤԼ�Һźϲ������
		String admDatePre = this.getValue("ADM_DATE").toString().substring(0,
				10).replace("/", "").replace("-", "").replace(" ", "");
		String preSelectSql = "SELECT CASE_NO,OLD_CASE_NO FROM REG_PATADM WHERE ADM_DATE = TO_DATE('"
				+ admDatePre
				+ "','YYYYMMDD') AND IS_PRE_ORDER ='Y' AND MR_NO = '"
				+ this.getValue("MR_NO").toString() + "' ";
		TParm preSelectResult = new TParm(TJDODBTool.getInstance().select(
				preSelectSql));
		if (preSelectResult.getCount() > 0) {
			reg.setCaseNo(preSelectResult.getValue("CASE_NO", 0));
			reg.setOldCaseNo(preSelectResult.getValue("OLD_CASE_NO", 0));
			reg.setPreFlg("Y");
			// ɾ���Һű��Ԥ���Һ���Ϣ
			String delPreSql = "DELETE REG_PATADM WHERE CASE_NO = '"
					+ reg.caseNo() + "'";
			TParm delPreResult = new TParm(TJDODBTool.getInstance().update(
					delPreSql));
		}

	}

	/**
	 * ��ȡ��������
	 */
	public int getBuyMonth(String s, String s1) {
		// Date m=new Date();
		Date d = null;
		Date d1 = null;
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		try {
			d = df.parse(s);
			d1 = df.parse(s1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		c.setTime(d1);
		int year1 = c.get(Calendar.YEAR);
		int month1 = c.get(Calendar.MONTH);
		int result;
		if (year == year1) {
			result = month1 - month;// �������������£����·ݲ�
		} else {
			result = 12 * (year1 - year) + month1 - month;// �������������£����·ݲ�
		}
		return result;
	}

	public void onWrist() {
		if (getValueString("MR_NO").length() == 0
				|| getValueString("PAT_NAME").length() == 0
				|| getValueString("SEX_CODE").length() == 0
				|| getValueString("BIRTH_DATE").length() == 0) {
			messageBox("������ѡ��Ϊ��,��������д!");
			return;
		}
		String a_number = getValueString("MR_NO");
		String a_name = getValueString("PAT_NAME");
		String a_gender = getValueString("SEX_CODE");
		if (a_gender.equals("1"))
			a_gender = "��";
		if (a_gender.equals("2"))
			a_gender = "Ů";
		if (a_gender.equals("9"))
			a_gender = "δ˵��";
		if (a_gender.equals("0"))
			a_gender = "δ֪";
		String a_time = getValueString("BIRTH_DATE");
		TParm print = new TParm();
		print.setData("MR_NO", "TEXT", a_number);
		print.setData("PAT_NAME", "TEXT", a_name);
		print.setData("SEX_CODE", "TEXT", a_gender);
		print.setData("BIRTH_DATE", "TEXT", a_time.substring(0, 10).replace(
				"-", "/"));
		openPrintDialog("%ROOT%\\config\\prt\\REG\\REGPatAdm.jhw", print);
	}

	/**
	 * ���֤��У��
	 */
	public boolean isCard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches(
				"(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}

	/**
	 * �����Ա�
	 */
	public void setSexCode(String sexCode) {
		try {
			int a = Integer.parseInt(sexCode);
			int b = a % 2;
			if (b == 0) {// Ů
				this.setValue("SEX_CODE", 2);
			} else if (b == 1) {// ��
				this.setValue("SEX_CODE", 1);
			}
			this.grabFocus("NATION_CODE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * ������ʱ����
	 * 
	 * @author wangbin
	 */
	private void insertMroRegData() {
		// modify by wangbin 2016/1/18 Ϊ�����û�������ȡԤԼ���ݣ��ֳ��ҺŲ���ԤԼ���ֻҪ��ʱ�������ݶ�����
		// modify by wangbin 2015/1/8 ԤԼ�Һŵĳ��ﲡ���Լ��ֳ��ҺŵĲ��ˣ��Һ�ʱ������ʱ��
		// modify by wangbin 2015/1/15 ȥ�������жϣ��ż��ﶼ�������ﴦ��
		TParm mroRegParm = new TParm();

		// ԤԼ�ҺŵĲ���
		if (crmRegFlg && StringUtils.isNotEmpty(crmId)) {
			TParm queryParm = new TParm();
			queryParm.setData("BOOK_ID", crmId);
			// ��ѯ�Һ������Ƿ��Ѿ�����
			queryParm = MROBorrowTool.getInstance().queryMroRegAppointment(
					queryParm);

			if (queryParm.getErrCode() < 0) {
				err("ERR:" + queryParm.getErrCode() + queryParm.getErrName()
						+ queryParm.getErrText());
				return;
			}

			// ԤԼ��������Ѿ����ڣ�˵���Ƿǳ����ԤԼ���ݣ���ȡʱ�Ѳ�����ʱ��
			if (queryParm.getCount() > 0) {
				// modify by wangb 2017/11/24 CRM��ͬԤԼ�Ŵ����ظ����õ����
				if (queryParm.getValue("MR_NO", 0).equals(pat.getMrNo())) {
					if (StringUtils.isEmpty(queryParm.getValue("CASE_NO", 0))) {
						TParm updateParm = new TParm();
						updateParm.setData("MRO_REGNO", queryParm.getValue("MRO_REGNO", 0));
						updateParm.setData("BOOK_ID", crmId);
						updateParm.setData("MR_NO", queryParm.getValue("MR_NO", 0));
						updateParm.setData("ADM_TYPE", "O");
						updateParm.setData("CASE_NO", reg.caseNo());
						// �ظ���ʱ������
						updateParm = MROBorrowTool.getInstance().updateCaseNoForMroReg(updateParm);
						
						if (updateParm.getErrCode() < 0) {
							err("ERR:" + updateParm.getErrCode() + updateParm.getErrName()
									+ updateParm.getErrText());
							return;
						}
					}
					return;
				} else {
					// ����������е����ݵĲ������뵱ǰ�ҺŵĲ�һ�£��򽫿������ݵ�ȡ��
					TParm parm = new TParm();
					parm.setData("BOOK_ID", queryParm.getValue("BOOK_ID", 0));
					parm.setData("MR_NO", queryParm.getValue("MR_NO", 0));
					parm.setData("ADM_TYPE", "O");
					parm.setData("OPT_USER", Operator.getID());
					parm.setData("OPT_TERM", Operator.getIP());

					TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(parm, null);

					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
					}
				}
			}
		}

		// ȡ��ԭ��
		mroRegParm.setData("MRO_REGNO", SystemTool.getInstance().getNo("ALL",
				"MRO", "MRO_REGNO", "MRO_REGNO"));

		this.mroRegNo = mroRegParm.getValue("MRO_REGNO");

		mroRegParm.setData("SEQ", 1);
		// ԤԼ��
		mroRegParm.setData("BOOK_ID", crmId == null ? "" : crmId);
		// 0_ԤԼ�Һ�(APP), 1_�ֳ��Һ�(LOC), 2_סԺ�Ǽ�
		mroRegParm.setData("ORIGIN_TYPE", "1");
		// �ż�ס��ʶ
		mroRegParm.setData("ADM_TYPE", "O");
		// ������
		mroRegParm.setData("MR_NO", pat.getMrNo());
		// �����
		mroRegParm.setData("CASE_NO", reg.caseNo());
		// ����
		mroRegParm.setData("ADM_AREA_CODE", reg.getClinicareaCode());
		// �Һ�ʱ��
		mroRegParm.setData("ADM_DATE", StringTool.getString(
				(Timestamp) getValue("ADM_DATE"), "yyyy/MM/dd"));
		// �Һ�ʱ��
		mroRegParm.setData("SESSION_CODE", reg.getSessionCode());
		// �������
		mroRegParm.setData("QUE_NO", reg.getQueNo());
		mroRegParm.setData("PAT_NAME", pat.getName());
		mroRegParm.setData("SEX_CODE", pat.getSexCode());
		mroRegParm.setData("BIRTH_DATE", StringTool.getString(
				pat.getBirthday(), "yyyy/MM/dd"));
		mroRegParm.setData("CELL_PHONE", pat.getCellPhone());
		mroRegParm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		mroRegParm.setData("DR_CODE", this.getValue("DR_CODE"));
		// ������ȷ��״̬
		mroRegParm.setData("CONFIRM_STATUS", "0");
		// ȡ��ע��(Y_ȡ��,N_δȡ��)
		mroRegParm.setData("CANCEL_FLG", "N");
		mroRegParm.setData("OPT_DATE", StringTool.getString(SystemTool
				.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss"));
		mroRegParm.setData("OPT_USER", Operator.getID());
		mroRegParm.setData("OPT_TERM", Operator.getIP());

		TParm mroRegResult = MROBorrowTool.getInstance().insertMroRegByLoc(
				mroRegParm);

		if (mroRegResult.getErrCode() < 0) {
			System.out.println("������ʱ��ʧ�ܣ�" + mroRegResult.getErrText());
			err("ERR:" + mroRegResult.getErrCode() + " "
					+ mroRegResult.getErrText());
		}
		
		// ���ԤԼ��
		crmId = "";
	}
	
	/**
	 * �����ݲ��벡����������
	 * 
	 * @author wangbin
	 */
	private void insertMroMrvData() {
		// modify by wangbin 2015/1/15 ȥ�������жϣ��ż��ﶼ�������ﴦ��
		TParm queryParm = new TParm();
		// ������
		queryParm.setData("MR_NO", pat.getMrNo());
		// �ż�ס��ʶ
		queryParm.setData("ADM_TYPE", "O");
		// ��ѯ���������������Ƿ���ڸò���������
		queryParm = MROQueueTool.getInstance().selectMRO_MRV(queryParm);

		if (queryParm.getCount() <= 0) {
			TParm mroMrv = new TParm();
			String region = Operator.getRegion();
			mroMrv.setData("MR_NO", pat.getMrNo());
			mroMrv.setData("SEQ", 1);
			mroMrv.setData("ADM_TYPE", "O");
			mroMrv.setData("IPD_NO", "");
			mroMrv.setData("CREATE_HOSP", region);
			mroMrv.setData("IN_FLG", "0");
			mroMrv.setData("CURT_HOSP", region);
			mroMrv.setData("CURT_LOCATION", "");
			mroMrv.setData("TRAN_HOSP", region);
			mroMrv.setData("BOX_CODE", "");
			mroMrv.setData("BOOK_NO", "");
			mroMrv.setData("OPT_USER", Operator.getID());
			mroMrv.setData("OPT_TERM", Operator.getIP());
			// ���ӵ�ǰ������Ŀ��Һͽ�����
			mroMrv.setData("CURT_LEND_DEPT_CODE", "");
			mroMrv.setData("CURT_LEND_DR_CODE", "");

			TParm result = MROQueueTool.getInstance().insertMRO_MRV(mroMrv);
			if (result.getErrCode() < 0) {
				System.out.println("���벡����������:" + result.getErrText());
				err("ERR:" + result.getErrCode() + " " + result.getErrText());
			}
		}
	}
	
	/**
	 * ����������ȡ��
	 * 
	 * @param caseNo �����
	 * @author wangbin 2014/09/05
	 */
	private void cancelMroRegAppointment(String caseNo) {
		TParm parm = new TParm();
		if (StringUtils.isNotEmpty(crmId)) {
			parm.setData("BOOK_ID", crmId);
		}
		if (StringUtils.isNotEmpty(pat.getMrNo())) {
			parm.setData("MR_NO", pat.getMrNo());
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("ADM_TYPE", "O");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());

		// �������ȡ��ԤԼ���������ʱ��(MRO_REG)ȡ��ע��
		TParm result = MROBorrowTool.getInstance().cancelMroRegAppointment(parm, null);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
		
		// �������ȡ���Һţ�����½��ı��ȡ��״̬
		result = MROBorrowTool.getInstance().updateMroQueueCanFlg(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
		}
	}
	
	/**
	 * �򲡰��ҷ�����Ϣ
	 * 
	 * @author wangbin 20140915
	 */
	private void onSendMROMessages() {
		client = SocketLink
				.running("","ODO", "ODO");
		if (client.isClose()) {
			out(client.getErrText());
			return;
		}
		
		client.sendMessage("ODO", "MRO_REGNO:" + this.mroRegNo
				+ "|MR_NO:" + pat.getMrNo() + "|PAT_NAME:" + pat.getName());
		
		if (client == null) {
			return;
		}
		client.close();
	}
	/**
	 * 
	 */
	public void checkInsure(){
		if(this.getValue("INSURE_INFO") == null || this.getValue("INSURE_INFO").toString().length() <= 0){
			return;
		}
		String sql = "SELECT VALID_FLG FROM MEM_INSURE_INFO " +
				" WHERE MR_NO = '"+this.getValueString("MR_NO")+"' AND CONTRACTOR_CODE = '"+this.getValue("INSURE_INFO")+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(!"Y".equals(parm.getValue("VALID_FLG",0))){
			this.messageBox("�ñ�����Ϣ��Ч��������ѡ��");
			this.setValue("INSURE_INFO", "");
			return;
		}
		sql = "SELECT CONTRACTOR_CODE FROM MEM_INSURE_INFO " +
		" WHERE MR_NO = '"+pat.getMrNo()+"' AND VALID_FLG = 'Y'" +
		" AND START_DATE <= TRUNC (SYSDATE, 'dd') AND END_DATE >= TRUNC (SYSDATE, 'dd')";
		System.out.println(""+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			if(JOptionPane.showConfirmDialog(null, "�ò������ղ�����Ч���ڣ��Ƿ����", "��Ϣ",
    				JOptionPane.YES_NO_OPTION) == 0){
				
			}else{
				this.setValue("INSURE_INFO", "");
			}
		}
	}
	
	public void getServiceLevel(){
		String ctz = this.getValueString("REG_CTZ1");
		String sql = "SELECT SERVICE_LEVEL FROM SYS_CTZ WHERE CTZ_CODE = '"
				+ ctz + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", 0));
	}
	
	/**
	 * ���ݱ�����Ч�����ż���Һ�ҳ��������Ӧ��ʾ
	 * ���Һ����ڴ�������һ��������Ч�ڵĽ�ֹ����ʱ���ż���ҺŽ��������Ӧ���չ�˾�ĵ�������
	 */
	private void checkInsures() {
		String sql = "SELECT A.MR_NO, A.CONTRACTOR_CODE, A.INSURANCE_NUMBER, A.VALID_FLG, A.START_DATE, A.END_DATE, B.CONTRACTOR_DESC FROM MEM_INSURE_INFO A LEFT JOIN MEM_CONTRACTOR B ON A.CONTRACTOR_CODE = B.CONTRACTOR_CODE "
				+ " WHERE MR_NO = '" + this.getValueString("MR_NO") + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount("MR_NO");
		if (count > 0) {
			Date endDate;
			Date now = SystemTool.instanceObject.getDate();
			String contractorDesc;
			String insuranceNumber;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < count; i++) {
				contractorDesc = parm.getValue("CONTRACTOR_DESC", i);
				insuranceNumber = parm.getValue("INSURANCE_NUMBER", i);
				endDate = parm.getTimestamp("END_DATE", i);
				if (endDate.before(now)) {
					sb.append("���չ�˾<"+contractorDesc + ">�����յ���<"+insuranceNumber+">����ͬ���ڣ�" + "\n\r");
				}
			}
			if (sb.toString().length() > 0) {
				this.messageBox(sb.toString());
			}
		}
	}
	
	/**
	 * �ż���Һ�ʱ����ҽ�ƿ������䲡���ź�������������ͣ�ã�
	 * 
	 * 1�������ʾ���û���ԭ��ݡ�99 ����[�Է�]����ͣ�ã����ڡ�����ע��ҳ�桿�����趨��
	 * 
	 * 2������ż���ҺŽ����еġ����һ���͡��Һ����һ���е�ֵ
	 */
	private void checkCtz1Code() {
		String ctz1Code = this.getValueString("CTZ1_CODE");
		String sql = "SELECT CTZ_CODE, CTZ_DESC, USE_FLG FROM SYS_CTZ WHERE CTZ_CODE = '" + ctz1Code + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (!parm.getBoolean("USE_FLG", 0)) {
			String ctzDesc = parm.getValue("CTZ_DESC", 0);
			this.messageBox("�û���ԭ��ݡ�" + ctzDesc + "����ͣ�ã����ڡ�����ע��ҳ�桿�����趨");
			this.setValue("CTZ1_CODE", "");
			this.setValue("REG_CTZ1", "");
		}
	}
}
