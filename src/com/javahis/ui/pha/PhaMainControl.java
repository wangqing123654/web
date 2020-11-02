package com.javahis.ui.pha;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JOptionPane;

import jdo.device.CallNo;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.opd.ODOTool;
import jdo.opd.Order;
import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.pha.PHACHMTool;
import jdo.pha.PHARxSheetTool;
import jdo.pha.PassTool;
import jdo.pha.Pha;
import jdo.pha.PhaSQL;
import jdo.pha.PhaSysParmTool;
import jdo.pha.TXNewATCTool;
import jdo.reg.PatAdmTool;
import jdo.spc.INDTool;
import jdo.spc.SPCSQL;
import jdo.spc.SPCTool;
import jdo.spc.bsm.ConsisServiceSoap_ConsisServiceSoap_Client;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import jdo.util.Medicine;
import jdo.util.Personal;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.bsm.XmlUtils;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.ui.sys.LEDMEDUI;
import com.javahis.util.DateUtil;
import com.javahis.util.OdoUtil;

/**
 * 
 * <p>
 * Title: ҩ�����䷢������
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2008.09.28
 * 
 * @version 1.0
 */

public class PhaMainControl extends TControl {

	// �����panel
	private static String PANEL_HEAD = "PanelHead";
	// �м��panel
	private static String PANEL_MIDDLE = "PanelMiddle";

	private static String PANEL_HEAD_NAME = "PHAMainHeader";
	private static String PANEL_MIDDLE_NAME = "PHAMainMiddle";
	// ���䷢��
	private String type;
	// ���г�/��ҩ��Ƭ
	private String orderType;
	// pha����
	private Pha pha;
	// pat����
	private Pat pat;
	// ����table������ǩ�������е��к�
	private int selectUPRow = -1;
	// ����table��ҽ���������е��к�
	private int selectDownRow = -1;
	// ��ѡ�ֵĵ�ǰorderlist����
	private OrderList nowOrdList;

	// ��ѯ�Ľ�����ݣ�װ��OrderList��
	private TParm data;

	// ���Ƿ���Ҫ��ˡ����
	private boolean needExamineFlg;
	// ����ҩ����ҩģʽ�����
	private boolean dispEqualSendFlg;

	private String startDateUI, endDateUI;

	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;

	// ���ӱ�ǩ���³ɹ���־
	private static final String UPDATE_FLAG_TRUE = "1";
	// ���ӱ�ǩ����ʧ�ܱ�־
	private static final String UPDATE_FLAG_FLASE = "-1";

	// ���ӱ�ǩ������ɱ�־
	public UUID uuid;

	// ��¼ ��� ����ʱ��case_no Ϊ������ӱ�ǩ�Ͳ��˹�ϵʱ ��
	public String caseNo = "";
	private String WAY = "202";

	/**
	 * �����==pangben 2013-5-9
	 */
	private LEDMEDUI ledMedUi;
	/**
	 * ����Ʋ���
	 */
	private TParm ledParm;

	TTable tableDown;

	public PhaMainControl() {
	}

	public void onInit() {
		super.onInit();
		// ��ò�ͬ����ĳ�ʼ������
		gainInitParm();
		// ���ݲ�����ʼ����Ӧ�Ľ���
		initUI();
		// System.out.println("==================" + type);
		// ������tableע�ᵥ���¼�����
		this.callFunction("UI|Table_UP|addEventListener", "Table_UP->" + TTableEvent.CLICKED, this, "onTableUPClicked");

		this.callFunction("UI|Table_DOWN|addEventListener", "Table_DOWN->" + TTableEvent.CLICKED, this,
				"onTableDOWNClicked");

		// �������tableע��CHECK_BOX_CLICKED��������¼�
		this.callFunction("UI|Table_DOWN|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
				"onDownTableCheckBoxChangeValue");

		// �������tableע��change��������¼�
		TTable tableDown = (TTable) getComponent("Table_DOWN");
		tableDown.addEventListener("Table_DOWN->" + TTableEvent.CHANGE_VALUE, this, "onDownTableChangeValue");

		// �������tableע��CHECK_BOX_CLICKED��������¼�
		this.callFunction("UI|Table_UP|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
				"onUpTableCheckBoxChangeValue");

		// ȡ��ҩ��ϵͳ����
		// ��á��Ƿ���Ҫ��ˡ�������ҩ����ҩģʽ���ı��
		if ("WD".equals(orderType))
			needExamineFlg = PhaSysParmTool.getInstance().needExamine();
		else if ("DD".equals(orderType))
			needExamineFlg = PhaSysParmTool.getInstance().needExamineD();
		dispEqualSendFlg = PhaSysParmTool.getInstance().dispEqualSend();
		initUIData();

		// ������ҩ��ʶ
		passIsReady = SYSNewRegionTool.getInstance().isOREASONABLEMED(Operator.getRegion());
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		// ========pangben modify 20110421 start Ȩ�����
		this.callFunction("UI|REGION_CODE|setEnabled",
				SYSRegionTool.getInstance().getRegionIsEnabled(this.getValueString("REGION_CODE")));
		// TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
		// cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
		// getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop
		// ��ʼ��������ҩ
		if (passIsReady && "Examine".equals(type)) {
			if (!PassTool.getInstance().init()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
		((TTextField) getComponent("RX_PRESRT_NO")).grabFocus();// by liyh
																// 20120914 ��ʼ��
		// ����ǩ�õ�����
		this.grabFocus("RX_PRESRT_NO");

		// ���ι���
		setOdoAutEnabled(false);
	}

	/**
	 * �õ���ʼ������ WD��ҩ/�г�ҩ DD��ҩ��Ƭ
	 */
	public void gainInitParm() {
		String s = (String) this.getParameter();
		if (s == null)
			s = this.getConfigString("DEFAULT_PARAMETER"); // �������г�/��Ƭ ��
		// ���䷢�˵�MENU
		// eg:Examine|DD
		String s1[] = StringTool.parseLine(s, "|");
		type = s1[0]; // ���Ƹı䣺���䷢�˵�MENU����/�����tableͷ
		orderType = s1[1]; // ���г�/��Ƭ
		// orderType = "WD";
		// orderType = "DD";
		// type = "Examine";
		// type = "Dispense";
		// type = "Send";
		// type = "Return";
		if ("WD".equals(orderType)) {
			startDateUI = "from_ORDER_DATE";
			endDateUI = "to_ORDER_DATE";
		} else {
			startDateUI = "start_ORDER_DATE";
			endDateUI = "end_ORDER_DATE";
		}
	}

	/**
	 * ���ݳ�ʼ��������ʼ����Ӧ���� ����Ĭ��״̬
	 */
	public void initUI() {
		// messageBox(type);
		if (type.equals("History")) {
			onInitUIHistory();
			if (orderType.equals("WD") && !needExamineFlg) {
				callFunction("UI|CHECKBUTTON|setVisible", false);
				setValue("DOSAGEBUTTON", "Y");
			} else {
				callFunction("UI|CHECKBUTTON|setVisible", true);
				setValue("CHECKBUTTON", "Y");
			}
			callFunction("UI|DOSAGEBUTTON|setVisible", true);
			callFunction("UI|DISPENSEBUTTON|setVisible", true);
			callFunction("UI|RETURNBUTTON|setVisible", true);
			callFunction("UI|tLabel_9|setVisible", false);
			callFunction("UI|FINISH|setVisible", false);
			callFunction("UI|UNFINISH|setVisible", false);

			// ��ʷ��ѯ���治��ʾ�����Ϣ
			callFunction("UI|MAIN_DIAG_LABEL|setVisible", false);
			callFunction("UI|MAIN_DIAG|setVisible", false);
			callFunction("UI|SECONDARY_DIAG_LABEL|setVisible", false);
			callFunction("UI|SECONDARY_DIAG|setVisible", false);
			return;
		}
		if ("DD".equals(orderType) && ("Dispense".equals(type) || "Examine".equals(type)))
			this.callFunction("UI|setMenuConfig", getConfigString("MENU." + type + "." + orderType)); // �˵�����
		else
			this.callFunction("UI|setMenuConfig", getConfigString("MENU." + type)); // �˵�����

		// ��̬���ز˵������ʹ�õĳ�ʼ��һ��
		this.callFunction("UI|onInitMenu");
		// �������䷢�����ò�ͬ�Ĵ���title
		if (type.equals("Examine")) {
			this.callFunction("UI|setTitle", "ҩ����˽���"); // ��
			if (orderType.equals("WD")) {// &&"Y".equals(Operator.getSpcFlg()))
											// {//
											// ��ҩ��˽�����ʾ����ƣ�������ҩ��˲���=====pangben
				// 2013-7-18
				// openLEDMEDUI();//modify by wangjc 20180103��������Ӧϵͳ����ע������ƹ���
			}
		} else if (type.equals("Dispense")) {
			this.callFunction("UI|setTitle", "ҩ���������"); // ��
		} else if (type.equals("Send")) {
			this.callFunction("UI|setTitle", "ҩ����ҩ����"); // ��
		} else {
			this.callFunction("UI|setTitle", "ҩ����ҩ����"); // ��
		}

		// ��������PANEL
		this.callFunction("UI|" + PANEL_HEAD + "|addItem", PANEL_HEAD_NAME, getConfigString("PANEL_HEAD." + orderType),
				null, false);
		this.callFunction("UI|" + PANEL_MIDDLE + "|addItem", PANEL_MIDDLE_NAME,
				getConfigString("PANEL_MIDDLE." + orderType), null, false);

		if ("WD".equalsIgnoreCase(orderType)) { // ��������table
			this.callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));

			// �������䷢����ס��ͬ��������
			if (type.equals("Examine")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left"); // �����е����Ҷ�Ӧλ��

			} else if (type.equals("Dispense")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // �����е����Ҷ�Ӧλ��
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // ��������ס��һ�У�
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // �����е����Ҷ�Ӧλ��
			} else {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // �����е����Ҷ�Ӧλ��
			}

			// �����table
			this.callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			/**
			 * PS��ֻ�С���ҩ����ʱ��Ԥ�����ʲ�������
			 */
			// ������ҩ�����ʱ��ɲ��Դ���ǩΪ��λ�����Ե�����ĳ��ҩ������orderΪ��λȷ�ϣ�
			if (type.equals("Dispense")) { // �ſ���һ��
				this.callFunction("UI|Table_DOWN|setLockColumns", "2,3,4,5,6,7,8,9,10,11,12,13,14,16");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"4,left;5,right;6,left;7,right;8,right;9,right;10,left;11,left;12,left;13,right;14,left;15,left;16,left");
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"4,left;5,right;6,left;7,right;8,right;9,right;10,left;11,left;12,left;13,right;14,left;15,left;16,left");
			}
			// fux modify 20150825
			else if (type.equals("Examine")) { // ��
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"6,left;7,right;8,left;9,right;10,right;11,right;12,left;13,left;14,left;15,right;16,left;17,left;18,left");

			} else { // �󣬷����˶��Դ���ǩΪ��λȷ��
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"5,left;6,right;7,left;8,right;9,right;10,right;11,left;12,left;13,left;14,right;15,left;16,left;17,left");

			}

			// ��ʼ����Ӧ��
			myInitWD();
		} else { // ����DD����Ƭʱ����Ҫ�ı���/���˵�tableͷ

			this.callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));

			// �������䷢����ס��ͬ��������
			if (type.equals("Examine")) {
				this.callFunction("UI|Table_UP|setLockColumns", "1,2,3,4,5,6,7,8,9,10,11,12,13,14");
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left"); // �����е����Ҷ�Ӧλ��
			} else if (type.equals("Dispense")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // �����е����Ҷ�Ӧλ��
			} else if (type.equals("Send")) {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;3,left;4,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left"); // �����е����Ҷ�Ӧλ��
			} else {
				this.callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15"); // ��
				this.callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
						"1,left;2,left;4,right;5,left;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left"); // �����е����Ҷ�Ӧλ��
			}
			// �����table
			this.callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			if (type.equals("Examine")) {// 20190228 add by wangjc �����Ա�ҩ�����ʾ
				this.callFunction("UI|Table_DOWN|setLockColumns", "ALL");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"0,left;1,right;2,left;4,right;5,left;6,left;8,left;9,left;10,right;12,left;13,right;14,left"); // �����е����Ҷ�Ӧλ��
			} else {
				this.callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11");
				this.callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
						"0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left"); // �����е����Ҷ�Ӧλ��
			}

			// ��ʼ����Ӧ��
			myInitDD();
			// �����Ͱ�ҩ����ť������ҽ����ʱ���ɵ��
			if ("DD".equalsIgnoreCase(orderType) && "Dispense".equalsIgnoreCase(type)) {
				((TMenuItem) getComponent("ATC")).setEnabled(false);
			}
		}
		if (!type.equals("History")) {
			callFunction("UI|CHECKBUTTON|setVisible", false);
			callFunction("UI|DOSAGEBUTTON|setVisible", false);
			callFunction("UI|DISPENSEBUTTON|setVisible", false);
			callFunction("UI|RETURNBUTTON|setVisible", false);
		}
		// ���Ը�����Ա��ʼ������
		// System.out.println("=>" + Operator.getDept());
		// System.out.println("=>" + Operator.getName());
		// System.out.println("=>" + Operator.getID());
		// initByOptUser();

		// ��ҩ���治��ʾ�����Ϣ
		if (type.equals("Return")) {
			this.callFunction("UI|MAIN_DIAG_LABEL|setVisible", false);
			this.callFunction("UI|MAIN_DIAG|setVisible", false);
			this.callFunction("UI|SECONDARY_DIAG_LABEL|setVisible", false);
			this.callFunction("UI|SECONDARY_DIAG|setVisible", false);
		}

		// �ڳ�����ǿ�ƴ����Textarea���óɲ��ɱ༭
		((TTextArea) this.getComponent("SECONDARY_DIAG")).getTextArea().setEnabled(false);
	}

	/**
	 * ��ʼ�� ���ݵ�½��Ա��(Ȩ��)��Ϣ��ʼ�� ���õ���½��Ա����Ϣ��
	 */
	public void initByOptUser() {
		String dept = Operator.getDept();
		// this.messageBox(dept);
		this.callFunction("UI|EXEC_DEPT_CODE|setValue", dept);
		// �жϸõ�½��Ա�Ƿ������Ȩ��
		if (this.getPopedem("maxAUT")) {
			// System.out.println("���Ȩ�޵�½");
			// �����Ȩ��,ִ�п��ҿ�������
			this.callFunction("UI|EXEC_DEPT_CODE|setEnabled", true);
		} else {
			// System.out.println("����Ȩ�޵�½");
			// �������Ȩ�޵���ֻ����ʾ��½��Ա��������
			this.callFunction("UI|EXEC_DEPT_CODE|setEnabled", false);
		}

	}

	/**
	 * WD(��ҩ/�г�ҩ) �Զ����ʼ��״̬ ���ʱ��Ҳ����
	 */
	public void myInitWD() {

		selectUPRow = -1;

		if (type.equals("Send")) {// ��ҩ ���ִ�п���EXEC_DEPT_CODE
			// ����ϲ���������
			this.clearValue(
					"EXEC_DEPT_CODE;DRUGTYPE;RX_NO;RX_PRESRT_NO;PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
							+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;ATC_MACHINENO;ATC_TYPE;COUNTER_NO;MAIN_DIAG;SECONDARY_DIAG");
		} else {
			// ����ϲ���������
			this.clearValue("DRUGTYPE;RX_NO;PRESRT_NO;RX_PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
					+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;COUNTER_NO;ATC_MACHINENO;ATC_TYPE;COUNTER_NO;MAIN_DIAG;SECONDARY_DIAG;DRUG");

		}
		callFunction("UI|SAVE|setEnabled", false);
		// ����Ĭ�ϵİ�ť״̬
		this.callFunction("UI|UNFINISH|setEnabled", true);
		// ������ҩҳ���ʱ����ʾ���Ƿ��Ͱ�ҩ�����ؼ�
		if (type.equals("Dispense")) {
			this.callFunction("UI|ATC_NO_L|setVisible", true);

			this.callFunction("UI|ATC_MACHINENO|setVisible", true);

			this.callFunction("UI|ATC_TYPE_L|setVisible", true);

			this.callFunction("UI|ATC_TYPE|setVisible", true);

			this.callFunction("UI|PACKAGEDRUG|setVisible", true);
			// ��ѡ��
			this.callFunction("UI|PACKAGEDRUG|setSelected", false);
			// ������
			this.callFunction("UI|PACKAGEDRUG|setEnabled", false);

		}
		this.callFunction("UI|ALLEXECUTE|setEnabled", true);
		this.callFunction("UI|ALLEXECUTE|setSelected", true);
		// ɾ����/��table��ֵ
		this.callFunction("UI|Table_UP|removeRowAll");
		this.callFunction("UI|Table_DOWN|removeRowAll");
	}

	/**
	 * DD(��ҩ) �Զ����ʼ��״̬ ���ʱ��Ҳ����
	 */
	public void myInitDD() {

		selectUPRow = -1;
		// �����������
		this.clearValue("RX_NO;PRESRT_NO;RX_PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;"
				+ "to_PRESCRIPT_NO;AGENCY_ORG_CODE;DECOCT_CODE;"
				+ "TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE;COUNTER_NO");
		this.callFunction("UI|SAVE|setEnabled", false);
		// ����Ĭ�ϵİ�ť״̬
		this.callFunction("UI|UNFINISH|setEnabled", true);

		// ɾ����/��table��ֵ
		this.callFunction("UI|Table_UP|removeRowAll");
		this.callFunction("UI|Table_DOWN|removeRowAll");
	}

	/**
	 * ͨ�������ò�ѯ����
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
		this.setText("MR_NO", pat.getMrNo());
		((TTextField) getComponent("BASKET_ID")).grabFocus();
		onQuery();

	}

	/**
	 * ��ն���
	 */
	public void onClear() {
		// �ص���ʼ��״̬
		if (orderType.equals("WD")) {
			myInitWD();
		} else {
			myInitDD();
		}
		// ========pangben modify 20110517 start
		this.setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110517 stop
		this.setValue("BASKET_ID", "");// by liyh 20120914
		((TTextField) getComponent("RX_PRESRT_NO")).grabFocus();// by liyh
																// 20120914 ��ʼ��
		// ����ǩ�õ�����

		setOdoAutEnabled(false);
	}

	/**
	 * ��������table��ĳһ�м������¼� �������table���г�ϸ��
	 * 
	 * @param row
	 *            int
	 */
	public void onTableUPClicked(int row) {
		if (type.equals("History")) {
			onUpTableClick();
			return;
		}
		// ѡ���к�
		selectUPRow = row;
		// ��UI�����õ�����һ��ֵ�����statusΪ��Y��Ϊ��ѯ��ɵģ�Ϊ��N��Ϊ��ѯδ��ɵ�
		String status = this.getValueString("FINISH");
		// ����水Ťtype.equals("Examine") || type.equals("Dispense") ||
		// type.equals("Send") || type.equals("Return")
		if ("Y".equals(status) && (type.equals("Dispense") || type.equals("Send") || type.equals("Return"))) {
			this.callFunction("UI|SAVE|setEnabled", false);
		} else {
			this.callFunction("UI|SAVE|setEnabled", true);
		}

		setOdoAutEnabled(false);

		// �洢���OrderList������orders��װ�ص������table�ϣ�

		tableDown = getTable("Table_DOWN");

		pat = Pat.onQueryByMrNo(data.getValue("MR_NO", selectUPRow).trim());

		// add by wangbin 20140731 ��ѯ�����Ϣ
		String rxNo = data.getValue("RX_NO", selectUPRow).trim();
		String sql = "SELECT B.ICD_CODE, C.ICD_CHN_DESC,B.MAIN_DIAG_FLG,B.DIAG_NOTE "
				+ " FROM OPD_ORDER A, OPD_DIAGREC B, SYS_DIAGNOSIS C "
				+ " WHERE A.CASE_NO = B.CASE_NO AND B.ICD_CODE = C.ICD_CODE AND A.RX_NO = '" + rxNo
				+ "' ORDER BY B.MAIN_DIAG_FLG DESC, A.ORDER_DATE DESC";

		TParm diagParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (diagParm.getErrCode() < 0) {
			err("ERR:" + diagParm.getErrCode() + diagParm.getErrText() + diagParm.getErrName());
			this.messageBox("��ѯ�����Ϣ���ִ���");
			return;
		}

		// ����ʷ��Ϣ
		if ("Examine".equals(type)) {
			// ���ó�ʼ����ʷ��Ϣ��Ϊ���ɼ�
			this.callFunction("UI|GMS|setVisible", false);
			this.callFunction("UI|DRUGORINGRD_CODE|setVisible", false);
			String mrNo = getTableSelectRowData("MR_NO", "Table_UP");
			String drugoringdSql = "SELECT ORDER_DESC FROM ( "
					+ "SELECT (CASE WHEN OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN '' ELSE (SELECT CHN_DESC"
					+ " FROM SYS_DICTIONARY" + " WHERE     GROUP_ID = 'SYS_ALLERGY'"
					+ " AND ID = OPD_DRUGALLERGY.DRUG_TYPE) END)" + " AS DRUG_TYPE,(SELECT ORDER_DESC FROM SYS_FEE"
					+ " WHERE     ORDER_CODE = OPD_DRUGALLERGY.DRUGORINGRD_CODE"
					+ " AND OPD_DRUGALLERGY.drug_type = 'B')"
					+ " || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'PHA_INGREDIENT'"
					+ " AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'A')"
					+ " || (SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_ALLERGYTYPE'"
					+ " AND ID = OPD_DRUGALLERGY.DRUGORINGRD_CODE AND OPD_DRUGALLERGY.drug_type = 'C')"
					+ " || (CASE WHEN  OPD_DRUGALLERGY.DRUG_TYPE = 'D' THEN DRUGORINGRD_CODE END )"
					+ " AS ORDER_DESC, OPD_DRUGALLERGY.ALLERGY_NOTE FROM OPD_DRUGALLERGY, SYS_DEPT, SYS_OPERATOR"
					+ " WHERE OPD_DRUGALLERGY.MR_NO = '" + mrNo + "'"
					+ " AND OPD_DRUGALLERGY.DEPT_CODE = SYS_DEPT.DEPT_CODE"
					+ " AND OPD_DRUGALLERGY.DR_CODE = SYS_OPERATOR.USER_ID" + " ) WHERE TRIM(ORDER_DESC) <> '-'";
			TParm drugorTparm = new TParm(TJDODBTool.getInstance().select(drugoringdSql));
			// �ж��Ƿ��л�ȡ������,�޵Ļ���ֱ�����ù���ʷ����ʾ
			if (drugorTparm.getCount() <= 0) {
				this.callFunction("UI|GMS|setVisible", false);
				this.callFunction("UI|DRUGORINGRD_CODE|setVisible", false);
			} else {
				// ����һ������ַ�������ʱ����
				String temp = "";
				for (int i = 0; i < drugorTparm.getCount(); i++) {
					// �ж��ֶ���ϢΪ����ʱ����ֵΪ��
					if (drugorTparm.getValue("ORDER_DESC", i).equals("��")) {
						temp = temp + "";
					} else {
						temp = temp + drugorTparm.getValue("ORDER_DESC", i) + ",";
					}
				}
				if (temp.length() > 0) {
					// ȥ��ĩβ�Ķ���
					temp = temp.substring(0, temp.length() - 1);
					this.setValue("DRUGORINGRD_CODE", temp);
					this.callFunction("UI|GMS|setVisible", true);
					this.callFunction("UI|DRUGORINGRD_CODE|setVisible", true);
				}
			}

		}

		// �����
		String mainDiag = diagParm.getValue("ICD_CHN_DESC", 0).trim().length() > 0 ? diagParm.getValue("ICD_CHN_DESC", 0):diagParm.getValue("DIAG_NOTE", 0);
		int parmLen = diagParm.getCount();
		// �����
		String secondaryDiag = "";
		List<String> secondaryDiagCodeList = new ArrayList<String>();

		for (int i = 1; i < parmLen; i++) {
			if (!StringUtils.equals(diagParm.getValue("MAIN_DIAG_FLG", i), "Y")) {
				// �����ȥ��
				if (!secondaryDiagCodeList.contains(diagParm.getValue("ICD_CODE", i))) {
					secondaryDiagCodeList.add(diagParm.getValue("ICD_CODE", i));
					secondaryDiag = secondaryDiag + (diagParm.getValue("ICD_CHN_DESC", i).trim().length() > 0 ? diagParm.getValue("ICD_CHN_DESC", i):diagParm.getValue("DIAG_NOTE", i));
					if (i < parmLen - 1) {
						secondaryDiag = secondaryDiag + ";\n";
					}
				}
			}
		}

		// ������ҳ���ϸ�ֵ�����TParm
		TParm showDiagParm = new TParm();
		showDiagParm.setData("MAIN_DIAG", mainDiag);
		showDiagParm.setData("SECONDARY_DIAG", secondaryDiag);
		showDiagParm.setData("DRUG", this.getAllerg(pat.getMrNo())); // add by
																		// huangtt
																		// 20180205
																		// ����ʷ
		TParm orders = new TParm();

		String deptSql = " SELECT ORG_CODE,ORG_CHN_DESC FROM IND_ORG ";
		TParm deptParn = new TParm(TJDODBTool.getInstance().select(deptSql));
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < deptParn.getCount("ORG_CODE"); i++) {
			map.put(deptParn.getValue("ORG_CODE", i), deptParn.getValue("ORG_CHN_DESC", i));
		}

		// ���ݲ�ͬ��ҩƷ����
		if (orderType.equals("WD")) { // ��ҩ/�г�ҩ
			// ѡ��table�е�ĳ��
			if (selectUPRow >= 0) {
				// ���ϲ��Ŀؼ���ֵ
				setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO", data, selectUPRow);
				this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add by
																					// huangtt
																					// 20150120
																					// PRESRT_NO
				this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																							// huangtt
																							// 20150120
																							// PRESRT_NO
				setValueForParm("MAIN_DIAG;SECONDARY_DIAG;DRUG", showDiagParm);
				String counterNo = getTableSelectRowData("COUNTER_NO", "Table_UP");
				String orgCode = getTableSelectRowData("EXEC_DEPT_CODE", "Table_UP");
				TParm atcParm = this.getPHAcounterNoData(orgCode, counterNo);
				String atctype = atcParm.getValue("ATC_TYPE", 0);
				String machineNo = atcParm.getValue("MACHINENO", 0);
				this.setValue("ATC_MACHINENO", machineNo);
				this.setValue("ATC_TYPE", atctype);
				// ���õ�ǰѡ�е�ĳ��orderList�к�
				pha.setOrdListRow(selectUPRow);

				// ȡ��ѡ���е�OrderList
				nowOrdList = pha.getCertainOrdListByRow(row);

				// RX_TYPEΪ��2����ʱ���ǹ���ҩƷ
				if ("2".equals(nowOrdList.getRxType())) {

					// ���ΪΪ����Ȩ�ޣ����Ϊ��û��Ȩ����ʹ�����桯��ť������
					if (!isCtlDrugPopedom()) {
						this.callFunction("UI|SAVE|setEnabled", false);
					}
				}

				// ȡ�ø�OrderList�к��е�orders(������TParm)--PRIMARY�������е�ֵ
				orders = nowOrdList.getParm(nowOrdList.PRIMARY);

				// fux modify 20180928 �޸��㵥����ʾ id��6121
				System.out.println("COUNT:" + orders.getCount("ORDER_CODE"));
				for (int k = 0; k < orders.getCount("ORDER_CODE"); k++) {
					String sqlPhatransunit = " SELECT ORDER_CODE,DOSAGE_QTY,STOCK_QTY,DOSAGE_UNIT,STOCK_UNIT "
							+ " FROM PHA_TRANSUNIT WHERE ORDER_CODE = '" + orders.getValue("ORDER_CODE", k) + "' ";
					// System.out.println("sqlPhatransunit::"+sqlPhatransunit);
					TParm parmPhatransunit = new TParm(TJDODBTool.getInstance().select(sqlPhatransunit));
					String stockUnit = parmPhatransunit.getValue("STOCK_UNIT", 0);
					String unitCode = orders.getValue("DISPENSE_UNIT", k);
					if (stockUnit.equals(unitCode)) {
						double dosageQty = parmPhatransunit.getDouble("DOSAGE_QTY", 0);
						double stockQty = parmPhatransunit.getDouble("STOCK_QTY", 0);
						double ownPriceOld = orders.getDouble("OWN_PRICE", k);
						BigDecimal ownPriceNewDec = new BigDecimal(Double.toString(ownPriceOld * dosageQty / stockQty));
						// ����2λС��
						double ownPriceNew = ownPriceNewDec.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						// System.out.println("ownPriceNew:::"+ownPriceNew);
						orders.setData("OWN_PRICE", k, ownPriceNew);
					}
				}

				// ȡ��case_no Ϊ������ӱ�ǩ��ϵʹ��
				caseNo = orders.getValue("CASE_NO", 0);
				// ȡ��TParm������
				int count = orders.getCount();
				// ��Ϊ�������TParm��addData�ĵ�һ����eg:EXE_FLG�ĸ������жϷŵ�table�е�����������Ӧ��ѭ�������һ�β�

				for (int i = 0; i < count; i++) {
					orders.addData("EXE_FLG", "Y"); // Ĭ��ÿ��ORDER�ϻ���
					// ��������ִ��Ĭ��Ϊ��
					nowOrdList.getOrder(i).setExeFlg(true);

				}

				/**
				 * ���ʱ
				 */
				if ("Dispense".equals(type)) {
					for (int i = 0; i < count; i++) {
						String orderCode = orders.getValue("ORDER_CODE", i);
						String execDeptCode = orders.getValue("EXEC_DEPT_CODE", i);
						double dosageQty = orders.getDouble("DOSAGE_QTY", i);

						TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);
						String routeCode = orders.getValue("ROUTE_CODE", i);
						String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);

						String batchNo = orders.getValue("BATCH_NO", i);
						if ("<TNULL>".equals(batchNo)) {
							batchNo = "";
						}

						if ("Y".equals(skintestFlg)) {
							if ("PS".equals(routeCode)) {
								if (status.equals("Y")) {

								} else {
									if (batchNo == null || batchNo.equals("")) {

										TParm result = getIndStockBatchNoBySkintest(orderCode, execDeptCode, dosageQty);

										batchNo = result.getValue("BATCH_NO", 0);
									}
									tableDown.setLockCell(i, 15, false);
								}
							} else {

								if (batchNo == null || batchNo.equals("")) {
									TParm result = getIndStockBatchNoBySkintest(orderCode, execDeptCode, dosageQty);
									batchNo = result.getValue("BATCH_NO", 0);
								}
								tableDown.setLockCell(i, 15, true);
							}
						} else {
							tableDown.setLockCell(i, 15, true);
						}

						orders.setData("BATCH_NO", i, batchNo);
						nowOrdList.getOrder(i).setBatchNo(batchNo);
					}
				}

				// ��ѡ��
				this.callFunction("UI|PACKAGEDRUG|setSelected", true);
				// ������
				this.callFunction("UI|PACKAGEDRUG|setEnabled", true);

				for (int j = 0; j < orders.getCount("EXE_FLG"); j++) {
					orders.setData("EXEC_DEPT", j, map.get(orders.getValue("EXEC_DEPT_CODE", j)));
				}

				// ��order���������table
				// fux modify 20150825 ����bill_flg(����ж��˷ѡ�����) ���� �������� ����һ��
				if ("Examine".equals(type)) {
					for (int k = 0; k < count; k++) {
						if (!"".equals(orders.getValue("PHA_RETN_CODE", k))) {
							orders.setData("BILL_FLG", k, "N");
						}
					}
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;BILL_FLG;RELEASE_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");// 20150803
																																																											// wangjc
																																																											// modify
																																																											// ����RELEASE_FLG
				} else if ("Dispense".equals(type)) {// add by wangjc
					for (int k = 0; k < count; k++) {
						if (!"".equals(orders.getValue("PHA_RETN_CODE", k))) {
							orders.setData("BILL_FLG", k, "N");
						}
					}
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;BILL_FLG;LINKMAIN_FLG;RELEASE_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");
				} else {
					this.callFunction("UI|Table_DOWN|setParmValue", orders,
							"EXE_FLG;ATC_FLG;LINKMAIN_FLG;RELEASE_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;DR_NOTE;BATCH_NO;SKINTEST_FLG;EXEC_DEPT");// 20150803
																																																									// wangjc
																																																									// modify
																																																									// ����RELEASE_FLG
				}
				// "EXE_FLG;ATC_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;REMARK;BATCH_NO"
				// �ð�ҩ��״̬
				this.onDoATC();

				// messageBox("orders:"+orders);
				// fux modify 20150821
				// ��ҩ���״̬���
				if ("Y".equals(status) && type.equals("Return")) {

					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						/** ��ɫ */
						this.getTable("Table_DOWN").setRowTextColor(i, new Color(255, 0, 0));
					}
				}
				// ����ҩ
				else if (!type.equals("Return")) {
					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						String orderCode = orders.getValue("ORDER_CODE", i);
						// ��ΣҩƷ���
						if (this.getColorByHighRiskOrderCode(orderCode)) {
							this.getTable("Table_DOWN").setRowTextColor(i, Color.RED);// ��ΣҩƷ����ʾΪ��ɫ
							// �Ǹ�Σ����
						} else {
							this.getTable("Table_DOWN").setRowTextColor(i, new Color(0, 0, 0));
						}
					}
				}
				// ��ҩ δ��� ����
				else if ("N".equals(status) && type.equals("Return")) {
					for (int i = 0; i < orders.getCount("ORDER_DESC"); i++) {
						this.getTable("Table_DOWN").setRowTextColor(i, new Color(0, 0, 0));
					}
				}

				((TTextField) getComponent("BASKET_ID")).grabFocus();

				return;
			}
		} else { // ��ҩ��Ƭ
			if (selectUPRow >= 0) { // ѡ��table�е�ĳ��
				// ���ϲ��Ŀؼ���ֵ
				setValueForParm(
						"EXEC_DEPT_CODE;RX_NO;RX_PRESRT_NO;PRESRT_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO", data,
						selectUPRow);
				this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));
				this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));
				setValueForParm("MAIN_DIAG;SECONDARY_DIAG;DRUG", showDiagParm);
				// ���в��Ŀؼ���ֵ
				setValueForParm("TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE",
						data, selectUPRow);

				// ���õ�ǰѡ�е�ĳ��orderList�к�
				pha.setOrdListRow(selectUPRow);

				// ȡ��ѡ���е�OrderList
				nowOrdList = pha.getCertainOrdListByRow(row);
				// ȡ�ø�OrderList�к��е�orders(������TParm)
				orders = nowOrdList.getParm(nowOrdList.PRIMARY);

				// ��ҩ----������������ҩorders(OPD�ṩ�ķ���)
				OdoUtil odrUtil = new OdoUtil();
				TParm reOrders = odrUtil.chnMedicReArrange(orders);
				// ȡ��case_no Ϊ������ӱ�ǩ��ϵʹ��
				caseNo = reOrders.getValue("CASE_NO", 0);
				// ������ˡ���ҩ-������TAKE_QTY��/��ҩ����ҩ-������TOT_QTY��
				// 20190228 modify by wangjc ���ӱ�ҩ�����ʾ
				if (type.equals("Examine")) {
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TAKE_QTY1;DCTEXCEP_CODE1;RELEASE_FLG1;ORDER_CODE2;TAKE_QTY2;DCTEXCEP_CODE2;RELEASE_FLG2;ORDER_CODE3;TAKE_QTY3;DCTEXCEP_CODE3;RELEASE_FLG3;ORDER_CODE4;TAKE_QTY4;DCTEXCEP_CODE4;RELEASE_FLG4");
				} else if (type.equals("Send")) {
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TAKE_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TAKE_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TAKE_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TAKE_QTY4;DCTEXCEP_CODE4");
				} else { // Dispense,Return
					this.callFunction("UI|Table_DOWN|setParmValue", reOrders,
							"ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4");
				}
				setTotTakeDays();

				return;
			}

		}
		// �жϸò����Ƿ����ڲ������ι���
		if ("Dispense".equalsIgnoreCase(type) && !checkOrgBatch()) {
			this.messageBox("��ҩ���������ι���\n������ִ�пۿ⣡");
			this.callFunction("UI|SAVE|setEnabled", false);
		}

	}

	/**
	 * �ж��Ƿ�Ϊ��ΣҩƷ
	 * 
	 * @param orderCode
	 * @return 20150731 wangjc add
	 */
	public boolean getColorByHighRiskOrderCode(String orderCode) {
		boolean a = false;
		String sql = "SELECT HIGH_RISK_FLG FROM PHA_BASE WHERE ORDER_CODE='" + orderCode + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getValue("HIGH_RISK_FLG", 0).equals("Y")) {
			a = true;
		}
		return a;
	}

	/**
	 * ��������table��ĳһ�м������¼�
	 * 
	 * @param row
	 *            int �к�
	 */
	public void onTableDOWNClicked(int row) {
		// �õ���ǰ��ѡ����
		selectDownRow = row;
		if (orderType.equals("WD")) {
			// �����Ͱ�ҩ��ѡ��
			// callFunction("UI|PACKAGEDRUG|setEnabled", true);
		} else { // DD
			// ��ҩֻ�������Ŵ���ǩ
		}
		return;
	}

	/**
	 * ��������table���޸������¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDownTableCheckBoxChangeValue(Object obj) {

		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���/��
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();

		// ���ѡ�е��ǵ�һ�оͼ���ִ�ж���--ִ��
		if (col == 0) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TCM_Transform.getBoolean(tableDown.getValueAt(row, col));
			// ����ִ�б��
			onExeFlg(exeFlg, row);
		}
		// ��������table�ϵ��
		// ���ѡ�е��ǵڶ��оͼ���ִ�ж���--�Ͱ�ҩ��
		if (col == 1) {
			String ATCFlg = getATCFlgFromSYSFee(nowOrdList.getOrder(row).getOrderCode());
			String boxFlg = nowOrdList.getOrder(row).getGiveboxFlg();
			if (tableDown.getValueAt(row, col).equals("Y") && (ATCFlg.length() == 0 || ATCFlg.equals("N"))) {
				callFunction("UI|Table_DOWN|setValueAt", "N", row, 1);
				tableDown.acceptText();
				onATCFlg("N", row);
				messageBox("��ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			if (tableDown.getValueAt(row, col).equals("Y") && boxFlg.equals("Y")) {
				callFunction("UI|Table_DOWN|setValueAt", "N", row, 1);
				tableDown.acceptText();
				onATCFlg("N", row);
				messageBox("�м�ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			// ��õ��ʱ��ֵ
			ATCFlg = TCM_Transform.getString(tableDown.getValueAt(row, col));
			// ����ִ�б��
			onATCFlg(ATCFlg, row);
		}
	}

	public void onExeFlg(boolean exeFlg, int row) {
		// this.messageBox_("ִ�С�����������");
		// ȡ���������ó���
		Order nowOrder = nowOrdList.getOrder(row);
		// �����ѡ�ĸ�checkBox���Ͱ�order��ExeFlg������Ϊ�棬�Ա���pha������onExecute������ʹ��
		nowOrder.setExeFlg(exeFlg);
	}

	public void onATCFlg(String ATCFlg, int row) {
		// ȡ���������ó���
		Order nowOrder = nowOrdList.getOrder(row);
		// �����ѡ�ĸ�checkBox���Ͱ�order��ATCFlg������Ϊ��
		nowOrder.setAtcFlg(ATCFlg);
	}

	public void onUpTableCheckBoxChangeValue(Object obj) {
		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���/��
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		if (col != 0)
			return;
		for (int i = 0; i < nowOrdList.size(); i++) {
			nowOrdList.getOrder(i).modifyDctagentFlg("" + tableDown.getValueAt(row, col));
		}
	}

	/**
	 * ����
	 */
	public void onSave() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("1======================="+sdf.format(SystemTool.getInstance().getDate()));
		String rxNoString = getValueString("RX_NO");
		String rxPresrtNoString = this.getValueString("RX_PRESRT_NO");
		// System.out.println("rxNoString:"+rxNoString);
		// System.out.println("rxPresrtNoString:"+rxPresrtNoString);
		// System.out.println("PresrtNoString:"+rxPresrtNoString.replace(rxNoString,
		// ""));
		String presrtNoString = rxPresrtNoString.replace(rxNoString, "");
		// zhangp
		if (EKTIO.getInstance().ektAyhSwitch() && type.equals("Dispense")) {
			TTable downTable = (TTable) getComponent("Table_DOWN");
			downTable.acceptText();
			TParm downParm = downTable.getParmValue();
			TParm inParm = new TParm();
			for (int i = 0; i < downParm.getCount("EXE_FLG"); i++) {
				// EXE_FLG
				if (downParm.getBoolean("EXE_FLG", i)) {
					inParm.addData("SEQ_NO", downParm.getValue("SEQ_NO", i));
					inParm.addData("RX_NO", downParm.getValue("RX_NO", i));
				}
			}
			inParm.setData("CASE_NO", downParm.getValue("CASE_NO", 0));
			inParm.setData("MR_NO", downParm.getValue("MR_NO", 0));
			TParm preParm = EKTpreDebtTool.getInstance().checkMasterForExe(inParm);
			if (preParm.getErrCode() < 0) {
				messageBox(preParm.getErrText());
				return;
			}
		}
		// System.out.println("2======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Examine") && StringUtils.isNotEmpty(rxNoString) && rxNoString.length() > 5
				&& "Y".equals(Operator.getSpcFlg())) {
			Timestamp startDate = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
			Timestamp endDate = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();

			synOPdOrderSave(startDate.toString(), endDate.toString(), getValueString("RX_NO"), getValueString("MR_NO"));
		}
		if (type.equals("Examine") && !checkDrugAuto())
			return;
		TParm result = new TParm();
		// �ж�ִ��ҩ�����������ҩ����ͬ
		if (this.getValueString("EXEC_DEPT_CODE").length() > 0 && this.getValueString("AGENCY_ORG_CODE").length() > 0
				&& this.getValue("EXEC_DEPT_CODE").equals(this.getValue("AGENCY_ORG_CODE"))) {
			this.messageBox("ִ��ҩ�����������ͬ��");
			return;
		}

		// System.out.println("3======================="+sdf.format(SystemTool.getInstance().getDate()));

		// ������ ��ҩ���ӱ�ǩ��˸��
		String mr_No = this.getValueString("MR_NO");

		// ����ǩ��
		String rxNo = this.getValueString("RX_NO");
		// ���ӱ�ǩ
		String basketId = this.getValueString("BASKET_ID");

		String rxPresertNo = this.getValueString("RX_PRESRT_NO"); // add by
																	// huangtt
																	// 20150120

		// ����ѡ��ҩƷ�Ƿ�����������ɲ���
		if ("Y".equals((String) this.getValueString("FINISH")))// ֻ�������ɲż��ɲ�����
			if (!checkIfRve())
				return;

		// ����pha��ҳ���������
		pha.setType(type);
		// ����pha��ҳ��״̬����
		pha.setFinishFlag((String) this.getValueString("FINISH"));

		pha.setLockFlg(Operator.getLockFlg());
		// ================================================================
		// 202��203�������Ⱥ�����ж�
		// MODIFY BY CEHNXI
		if (type.equals("Send")) {
			WAY = "203";
			boolean check = this.onSendBoxMachine();// 203��������ҩ����
			WAY = "202";
			if (!check)
				return;
		}
		// System.out.println("4======================="+sdf.format(SystemTool.getInstance().getDate()));
		// ==pangben 2013-7-19 ���������У�������ҽ�������ݿ��Ƿ�ͬ��
		if ((this.orderType.equals("WD")) && (this.type.equals("Examine"))) {// &&"Y".equals(Operator.getSpcFlg()))
																				// {//
																				// ��ҩ��˽������

			nowOrdList = pha.getCertainOrdListByRow(selectUPRow);

			// ȡ�ø�OrderList�к��е�orders(������TParm)--PRIMARY�������е�ֵ
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);

			System.out.println("ordersordersordersorders:" + orders);

			;// �����Ҫ������ҽ����������
			String sql = "SELECT A.SEQ_NO,A.DISPENSE_QTY FROM OPD_ORDER A WHERE A.CASE_NO='" + caseNo
			// + "' AND A.RX_NO||A.PRESRT_NO='" + rxPresertNo+ "' "
			// ;//add by huangtt 20150120
					+ "' AND A.RX_NO = '" + rxNoString + "' AND A.PRESRT_NO='" + presrtNoString + "' ";// modify by
																										// wangjc
																										// 20190109
																										// ��������Ӧҩ���������

			if (EKTIO.getInstance().ektAyhSwitch()) {
				// sql += "AND BILL_FLG='N'";
			} else {
				sql += "AND A.BILL_FLG='Y'";
			}
			// System.out.println(sql);
			TParm opdOrderParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (opdOrderParm.getCount() <= 0) {
				this.messageBox("��ѯ�˴���ǩҽ����������");
				return;
			}
			if (orders.getCount("SEQ_NO") != opdOrderParm.getCount()) {
				this.messageBox("�˴���ǩ���ݳ�������,��ִ�в�ѯ����");
				return;
			}
			for (int i = 0; i < orders.getCount("SEQ_NO"); i++) {
				for (int j = 0; j < opdOrderParm.getCount(); j++) {
					if (orders.getDouble("SEQ_NO", i) == opdOrderParm.getDouble("SEQ_NO", j)) {// �Ƚ�˳���
						if (orders.getDouble("DISPENSE_QTY", i) == opdOrderParm.getDouble("DISPENSE_QTY", j)) {// �Ƚ������Ƿ���ͬ
							break;
						} else {
							this.messageBox("�˴���ǩ���ݳ�������,��ִ�в�ѯ����");
							return;
						}
					}
				}
			}
		}
		// =========================== ����֮ǰ��֤����������Ƿ񱣴��

		// System.out.println("5======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Examine") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 У��ͬ������
			String checkSqlBySynchronizedExamine = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_CHECK_CODE IS NOT NULL " + "  AND PHA_CHECK_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// ��������Ӧҩ���������
			TParm checkParmBySynchronizedExamine = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedExamine));
			if (checkParmBySynchronizedExamine.getCount() > 0) {
				this.messageBox("��������� �����²�ѯ!");
				onArrangeAfterSave();
				return;
			}
		}

		if (type.equals("Send") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 У��ͬ������
			String checkSqlBySynchronizedSend = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_DISPENSE_CODE IS NOT NULL " + "  AND PHA_DISPENSE_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// ��������Ӧҩ���������
			TParm checkParmBySynchronizedSend = new TParm(TJDODBTool.getInstance().select(checkSqlBySynchronizedSend));
			if (checkParmBySynchronizedSend.getCount() > 0) {
				this.messageBox("�����ѷ�ҩ �����²�ѯ!");
				onArrangeAfterSave();
				return;
			}
		}

		if (type.equals("Return") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// fux modify 20160804 У��ͬ������
			String checkSqlBySynchronizedReturn = " SELECT RX_NO FROM OPD_ORDER " + " WHERE PHA_RETN_CODE IS NOT NULL "
					+ "  AND PHA_RETN_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// ��������Ӧҩ���������
			TParm checkParmBySynchronizedReturn = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedReturn));
			if (checkParmBySynchronizedReturn.getCount() > 0) {
				this.messageBox("��������ҩ �����²�ѯ!");
				onArrangeAfterSave();
				return;
			}
		}

		// System.out.println("6======================="+sdf.format(SystemTool.getInstance().getDate()));
		if (type.equals("Dispense") && !"Y".equals((String) this.getValueString("FINISH"))) {
			// String checkSql = " SELECT RX_NO FROM OPD_ORDER "
			// + " WHERE BILL_FLG = 'Y' "
			// + " AND PHA_DOSAGE_DATE IS NOT NULL"
			// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;//add by huangtt
			// 20150120
			// TParm checkParm = new TParm(TJDODBTool.getInstance().select(
			// checkSql));
			// if (checkParm.getCount() > 0) {
			// this.messageBox("�������ѱ�����");
			// onArrangeAfterSave();
			// return;
			// }

			// fux modify 20160804 У��ͬ������
			String checkSqlBySynchronizedDispense = " SELECT RX_NO FROM OPD_ORDER "
					+ " WHERE PHA_DOSAGE_CODE IS NOT NULL " + "  AND PHA_DOSAGE_DATE IS NOT NULL"
					// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// ��������Ӧҩ���������
			TParm checkParmBySynchronizedDispense = new TParm(
					TJDODBTool.getInstance().select(checkSqlBySynchronizedDispense));
			if (checkParmBySynchronizedDispense.getCount() > 0) {
				this.messageBox("��������ҩ �����²�ѯ!");
				onArrangeAfterSave();
				return;
			}

			// System.out.println("7======================="+sdf.format(SystemTool.getInstance().getDate()));
			nowOrdList = pha.getCertainOrdListByRow(selectUPRow);
			// ȡ�ø�OrderList�к��е�orders(������TParm)--PRIMARY�������е�ֵ
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);

			String msg = "";
			String skingMsg = "";
			for (int i = 0; i < orders.getCount("ORDER_CODE"); i++) {

				String orderCode = orders.getValue("ORDER_CODE", i);
				String orderDesc = orders.getValue("ORDER_DESC", i);
				TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);
				String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);
				String batchNo = orders.getValue("BATCH_NO", i);
				String orgCode = orders.getValue("EXEC_DEPT_CODE", i);
				double dosageQty = orders.getDouble("DOSAGE_QTY", i);
				if ("Y".equals(skintestFlg)) {
					if (batchNo == null || batchNo.equals("")) {
						msg += orderDesc + "���Ų���Ϊ��\n";
					} else {// У��ͬ�����Ƿ��п��
						boolean checkResult = false;
						/** �������ż�� */
						checkResult = INDTool.getInstance().inspectIndStock(orgCode, orderCode, batchNo, dosageQty);
						if (!checkResult) {
							if (this.messageBox("��ʾ", "�����ſ�治�㣬�Ƿ����?", 2) == 0) {

							} else {
								return;
							}
						}
					}
				} else {
					if ("<TNULL>".equals(batchNo) || "null".equals(batchNo)) {
						batchNo = "";
					}
					if (batchNo != null && !batchNo.equals("")) {
						msg += orderDesc + "������д����" + batchNo + "\n";
					}
				}

				String skingFlg = orders.getValue("SKINTEST_FLG", i);

				if ("1".equals(skingFlg)) {
					skingMsg += orderDesc + " ���ţ�" + batchNo + "��Ƥ�Խ��Ϊ������+��\n";
				}
			}

			if (msg.length() > 0) {
				this.messageBox(msg);
				return;
			}

			if (skingMsg.length() > 0) {
				if (this.messageBox("��ʾ", skingMsg + "\n�Ƿ�ȷ����ҩ��?", 2) == 0) {

				} else {
					return;
				}
			}
		}

		// ���㲻����ҩ
		if ("Return".equals(type)) {
			String checkSql = " SELECT RX_NO FROM OPD_ORDER " + " WHERE  BILL_FLG = 'Y' "
			// + " AND RX_NO||PRESRT_NO='" + rxPresertNo+ "' " ;//add by
			// huangtt 20150120
					+ " AND RX_NO = '" + rxNoString + "' AND PRESRT_NO='" + presrtNoString + "' ";// modify by wangjc
																									// 20190109
																									// ��������Ӧҩ���������
			TParm checkParm = new TParm(TJDODBTool.getInstance().select(checkSql));
			if (checkParm.getCount() > 0) {
				this.messageBox("�Ѿ�������ɲ�����ҩ");
				return;
			}
		}

		// System.out.println("8======================="+sdf.format(SystemTool.getInstance().getDate()));
		// ����pha����ı��淽��
		result = pha.onSave();
		// System.out.println("9======================="+sdf.format(SystemTool.getInstance().getDate()));
		// ��֤����
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			this.messageBox("����ʧ�ܣ�");
			return;
		} else {
			this.messageBox("����ɹ���");
			if ("WD".equals(orderType)) {
				if (type.equals("Examine")) {// ��˲���������Ƴ�����
					// getLedMedRemoveRxNo();// ====pangben 2013-5-27//modify by
					// wangjc 20180103��������Ӧϵͳ����ע������ƹ���
				} else if (type.equals("Dispense") && !"Y".equals((String) this.getValueString("FINISH"))) {
					// onPrint(nowOrdList.getRxNo());
				}
			} else {
				if (type.equals("Examine") && !"Y".equals((String) this.getValueString("FINISH"))) {
					onPrintTCM();
				}
			}
			// ==========================ȡ���к� ======chenxi modify
			// 20130618======================
			if (type.equals("Send") && "N".equals(getValueString("FINISH")) && "WD".equals(orderType)) {
				this.onArrive();
			}
			// ============================================= chenxi modify
			// ��װ��ҩ������
			if (type.equals("Dispense") && "N".equals(getValueString("FINISH")) && "WD".equals(orderType)) {
				// lx test
				// this.onDispenseBoxMachine();
			}
			// //============================================= chenxi modify
			// ��װ��ҩ������
			// luhai modify 2012-2-22 ɾ����ҩ�ŵĴ�ӡ���� begin
			// onCode();
			// luhai modify 2012-2-22 ɾ����ҩ�ŵĴ�ӡ���� end
		}
		// 2019-3-25 chenyl�в�ҩ����ʱ�����в�ҩ�ӿ� START
		// System.out.println(orderType.equals("DD"));
		// System.out.println(type.equals("Dispense"));
		// System.out.println(!"Y".equals(this.getValueString("FINISH")));
		if (orderType.equals("DD") && type.equals("Dispense") && !"Y".equals(this.getValueString("FINISH"))) {
			// �в�ҩ����
			if (StringUtils.equals("Y", TConfig.getSystemValue("CHM.Switch"))) {
				// ȡ�ø�OrderList�к��е�orders(������TParm)--PRIMARY�������е�ֵ
				TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);
				String seqNo = orders.getValue("SEQ_NO").replaceAll("\\[", "").replaceAll("\\]", "");
				TParm parm = new TParm();
				parm.setData("CASE_NO", caseNo);
				parm.setData("RX_NO", this.getValueString("RX_NO"));
				parm.setData("SEQ_NO", seqNo);
				// ��ѯҽ����������
				TParm orderInfoResult = PHACHMTool.getInstance().queryOrderInfo(parm);
				if (orderInfoResult.getErrCode() < 0) {
					this.messageBox("������ҩ������ʧ��");
					err("��ѯҽ����������ERR:" + orderInfoResult.getErrCode() + orderInfoResult.getErrText()
							+ orderInfoResult.getErrName());
					return;
				}
				// ��װ�в�ҩ��������
				TParm insertResult = PHACHMTool.getInstance().getChmInsertData(orderInfoResult);
				insertResult = TIOM_AppServer.executeAction("action.pha.PHACHMAction", "insertIntoDataPrescription",
						insertResult);
				if (insertResult.getErrCode() < 0) {
					this.messageBox("������ҩ������ʧ��");
					err("�����в�ҩ�ӿ�ʧ��ERR:" + insertResult.getErrCode() + insertResult.getErrText()
							+ insertResult.getErrName());
					return;
				}
			}
		}

		// ********************************************************************************************
		// luhai modify 2012-3-12 begin //���б��е�ѡ���в���һ�У�����գ�ɾ��ѡ�е��в���ʣ�µ���ѡ�� begin
		// ********************************************************************************************
		// delete begin
		// // ��������Ķ���
		// onArrangeAfterSave();
		// delete end
		TTable table = (TTable) this.getComponent("Table_UP");
		// �жϸ�ѡ�в����Ƿ����һ�Ŵ���ǩ�����ж����´�ˢ�»���ʾ�ò�������Ϣ
		/*
		 * String mrNo = table.getParmValue().getValue("MR_NO", selectUPRow); int
		 * lastCount = 0; for (int j = 0; j < table.getParmValue().getCount("MR_NO");
		 * j++) { if (table.getParmValue().getValue("MR_NO", j).equals(mrNo)) {
		 * lastCount++; } }
		 */
		// �õ��ò����Ĵ���ǩ�󣬼�ȥ�Ѿ���˵��Ǹ�����ǩ
		// lastCount--;
		// ���ʣ������>0 �Ҹò�����δ������ǩ��>0ʱ��ˢ���Ǳ���mr_no ���ݸò�����������
		/*
		 * if (table.getParmValue().getCount("MR_NO") > 1 && lastCount > 0) { //
		 * �������ѡ�е��� this.callFunction("UI|Table_UP|removeRow", selectUPRow); // ����������е���
		 * this.callFunction("UI|Table_DOWN|removeRowAll"); // this.onClear(); //
		 * COUNTER_NO RX_NO this.setValue("COUNTER_NO", ""); this.setValue("RX_NO", "");
		 * // ���luhai 2012-03-28 begin // ����ϲ����� this
		 * .clearValue("DRUGTYPE;DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;" +
		 * "to_PRESCRIPT_NO;AGENCY_ORG_CODE;RETURNREASON;COUNTER_NO;ATC_MACHINENO;ATC_TYPE;COUNTER_NO"
		 * ); // ���luhai 2012-03-28 end this.onQuery(); } else {
		 */
		// ��������Ķ���
		onArrangeAfterSave();
		// System.out.println("10======================="+sdf.format(SystemTool.getInstance().getDate()));
		// }
		// luhai modify 2012-3-12 end
		// ********************************************************************************************
		// luhai modify 2012-3-12 begin //���б��е�ѡ���в���һ�У�����գ�ɾ��ѡ�е��в���ʣ�µ���ѡ�� end
		// ********************************************************************************************
		// cancelCallNoList();

		// ֻ����ҩʱ��ӡ����Ҫǩ��ӡ by yhb 20120914
		if (type.equals("Dispense")) {
			// ����ִ�п���
			updateExecDeptCode();
			// System.out.println("11======================="+sdf.format(SystemTool.getInstance().getDate()));
			// ��ҩ����ӡ
			onPrintAuto(rxNoString, rxPresrtNoString);
			// System.out.println("16======================="+sdf.format(SystemTool.getInstance().getDate()));
		}
	}

	/**
	 * ����ִ�п���
	 */
	public void updateExecDeptCode() {
		TParm parm = nowOrdList.getParm();
		System.out.println("boolean: " + (null != nowOrdList && nowOrdList.size() > 0));
		if (null != nowOrdList && nowOrdList.size() > 0) {
			// System.out.println("---------ATC_FLG:"+nowOrdList.getOrder(0).getAtcFlg());
			int count = nowOrdList.size();
			for (int i = 0; i < count; i++) {
				String atcFlg = nowOrdList.getOrder(i).getAtcFlg();
				if (StringUtils.equals("Y", atcFlg)) {// Y��ʾ�Ͱ�ҩ��
					// ��ѯ����Ӧ�İ�ҩ��
					TParm atcParm = SPCTool.getInstance().getOrgCodeOfAtc(nowOrdList.getOrder(i).getExecDeptCode());
					String caseNo = nowOrdList.getOrder(i).getCaseNo();
					String rxNo = nowOrdList.getOrder(i).getRxNo();
					String seqNo = nowOrdList.getOrder(i).getSeqNo() + "";
					String orgCode = atcParm.getValue("ORG_CODE", 0);
					if (orgCode.length() <= 0)
						orgCode = nowOrdList.getOrder(i).getExecDeptCode();
					// System.out.println("caseNo:"+caseNo+",rxNo="+rxNo+",seqNo:"+seqNo);
					// �޸�ִ�п���
					TParm result = SPCTool.getInstance().updateExecDeptCode(caseNo, rxNo, seqNo, orgCode);
					// System.out.println(i+",result:"+result);
				}
			}
		}
	}

	/**
	 * ȡ���к��б�
	 */
	public void cancelCallNoList() {
		if (!"Send".equals(type))
			return;
		if (nowOrdList.size() <= 0)
			return;
		CallNo call = new CallNo();
		if (!call.init())
			return;
		call.SyncDrug("", nowOrdList.getOrder(0).getCaseNo(), "", "", "", "", "", "", "", "", "", "1");
	}

	/**
	 * ���ô���ǩ�Ƿ����ִ�������
	 * 
	 * @return boolean
	 */
	private boolean checkIfRve() {

		String rxNo = this.getValueString("RX_NO");
		String presrtNo = this.getValueString("PRESRT_NO"); // add by huangtt
															// 20150121
		// String mrNo = this.getValueString("MR_NO");
		// ͨ���ô���ǩ�Ų�ѯ
		String checkSql = getChechSql(rxNo, presrtNo);

//		System.out.println("---��������Ƿ��ִ��--->" + checkSql);

		// ִ��sql���
		TJDODBTool tool = TJDODBTool.getInstance();
		TParm result = new TParm();
		result = new TParm(tool.select(checkSql));
		// �õ����ص�����(PS:��TJDODBTool���غ��װ��TParmȡ���������ⷽ��)
		String flg = "";
		if ("Examine".equalsIgnoreCase(type))
			flg = result.getValue("PHA_DOSAGE_CODE", 0).toString();
		if ("Dispense".equalsIgnoreCase(type))
			flg = result.getValue("PHA_DISPENSE_CODE", 0).toString();
		if ("Send".equalsIgnoreCase(type))
			flg = result.getValue("PHA_RETN_CODE", 0).toString();
		// ������صı��Ϊ������������
		if (flg == null || flg.length() <= 0 || flg.equalsIgnoreCase("null")) {
			return true;
		}

		// ������ʾ��Ϣ
		String msg = "";
		if ("Examine".equalsIgnoreCase(type))
			msg = "����ҩ����ȡ�����";
		if ("Dispense".equalsIgnoreCase(type))
			msg = "�ѷ�ҩ����ȡ����ҩ";
		if ("Send".equalsIgnoreCase(type))
			msg = "����ҩ����ȡ����ҩ";

		this.messageBox("�ô���ǩ\n" + msg);
		return false;
	}

	private String getChechSql(String rxNo, String presrtNo) {

		String checkSql = "";
		// ���-��ҩ����ҩ-��ҩ����ҩ-��ҩ
		if ("Examine".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_DOSAGE_CODE FROM opd_order WHERE " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo + "' ";// add
																														// by
																														// huangtt
																														// 20150120
		if ("Dispense".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_DISPENSE_CODE FROM opd_order WHERE " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo
					+ "' ";// add by
							// huangtt
							// 20150120
		if ("Send".equalsIgnoreCase(type))
			checkSql = "SELECT PHA_RETN_CODE FROM opd_order WHERE  " + " RX_NO||PRESRT_NO='" + rxNo + presrtNo + "' ";// add
																														// by
																														// huangtt
																														// 20150120

		return checkSql;
	}

	/**
	 * ����֮�������������
	 */
	public void onArrangeAfterSave() {
		// �������ѡ�е���
		this.callFunction("UI|Table_UP|removeRow", selectUPRow);
		// ����������е���
		this.callFunction("UI|Table_DOWN|removeRowAll");
		this.onClear();
		this.onQuery();

	}

	/**
	 * ��ѯ ����������ѯorderList װ�ص������table
	 */
	public void onQuery() {
		if (type.equals("History")) {
			onQueryHistory();
			return;
		}
		TParm parm = new TParm();
		// ��UI�����õ�����һ��ֵ�����statusΪ��Y��Ϊ��ѯ��ɵģ�Ϊ��N��Ϊ��ѯδ��ɵ�
		String status = this.getValueString("FINISH");
		// �õ�����ʱ��
		Timestamp startDate = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
		Timestamp endDate = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();
		// ���ݲ�ͬ�Ľ���ȡ�ø�UI�ϵ����в�ѯorderLiset����--����ҩ/��Ƭ
		if (orderType.equals("WD")) { // WD:����ҩ
			// ����ȫ��ִ��Ϊ��
			this.setValue("ALLEXECUTE", "Y");
			parm = this.getParmForTag("MR_NO;EXEC_DEPT_CODE;RX_NO;DEPT_CODE;"
					+ "DR_CODE;from_PRESCRIPT_NO;to_PRESCRIPT_NO;" + "AGENCY_ORG_CODE;COUNTER_NO", true);
			// ��������ҩ�����ʱ�������Ͳ�,�Ҳ�ѯ������
			parm.setData("MEDIC", "Y");

		} else { // DD:��Ƭ
			parm = this.getParmForTag("MR_NO;EXEC_DEPT_CODE;ORDER_DATE;MR_NO;"
					+ "DEPT_CODE;DR_CODE;from_PRESCRIPT_NO;to_PRESCRIPT_NO;" + "AGENCY_ORG_CODE;DECOCT_CODE;COUNTER_NO",
					true);
			// ������Ƭ�����ʱ�������Ͳ�,�Ҳ�ѯ������
			parm.setData("CHNMEDIC", "Y");
		}
		parm.setData("from_ORDER_DATE", startDate);
		parm.setData("to_ORDER_DATE", endDate);
		String rxNoString = getValueString("RX_NO");
		if (type.equals("Examine") && StringUtils.isNotEmpty(rxNoString) && rxNoString.length() > 5
				&& "Y".equals(Operator.getSpcFlg())) {
			synOPdOrder(startDate.toString(), endDate.toString(), getValueString("RX_NO"), getValueString("MR_NO"));
		}
		// --------------------�ر�ܿ�------start--------------------------------
		/**
		 * ���ݹҲ�ͬ���䷢�����̽��в�ͬ�Ĺܿ� ��̨module8�����isCheck(ed) isDGT(ed) isDlvry(ed) isReturn(ed)
		 */
		// �����"��"�Ľ���
		// ����ѡ�С���ɡ�������롰���ʱ�䡱in not null��̨��ѯ����
		if (type.equals("Examine")) {
			if (status.equals("Y")) {
				parm.setData("isCheckedForExamine", "Y"); // ���
			} else
				parm.setData("isCheck", "Y"); // δ���

		}

		// �ж��ڡ��䡱��ҳ��ʱ
		// �����������̣���̨��ѯʱ������Ӧ���ǡ����ʱ�䲻Ϊ�ա�
		if (type.equals("Dispense")) {
			// ������Ҫ��˺���ҩ
			if (needExamineFlg) {
				parm.setData("isChecked", "Y"); // ������
			}
			// �������ҩ���沢��ѡ�С���ɡ�������롰��ҩʱ�䡱in not null��̨��ѯ����
			if (status.equals("Y"))
				parm.setData("isDGTed", "Y"); // ���
			else
				parm.setData("isDGT", "Y"); // δ���
		}

		// �ж��ڡ�������ҳ��ʱ
		// �������ҩ���̣���̨��ѯʱ������Ӧ���ǡ���ҩʱ�䲻Ϊ�ա�
		if (type.equals("Send")) {
			parm.setData("isDGTed", "Y"); // ��ҩ���
			if (this.onBoxFlg(this.getValueString("EXEC_DEPT_CODE"))) {
				parm.setData("isPicked", "Y");// ��װ��ҩ����ҩ��� chenx
			}

			// ����Ƿ�ҩ���沢��ѡ�С���ɡ�������롰��ҩʱ�䡱in not null��̨��ѯ����
			if (status.equals("Y"))
				parm.setData("isDlvryed", "Y"); // ���
			else
				parm.setData("isDlvry", "Y"); // δ���
		}

		// ע�⣺����ҩ�����У�����з�ҩ������ֻ�ܡ���ҩʱ�䡱�����û�з�ҩ����--����ҩ����ҩģʽ����ֻ�ܡ���ҩʱ�䡱���������̣�
		// �ж��ڡ��ˡ���ҳ��ʱ
		// ������ǡ���ҩ����ҩģʽ��--�з�ҩ���̣���̨��ѯʱ������Ӧ���ǡ���ҩʱ�䡱in not null
		if (type.equals("Return") && !dispEqualSendFlg) {
			// �������ҩ���沢��ѡ�С���ɡ�������롰��ҩʱ�䡱in not null��̨��ѯ����
			if (status.equals("Y")) {
				parm.setData("isReturned", "Y"); // ���
			} else {
				// PS����ɷ�ҩ(����ҩ��ʱ��ֻ��ѯ��ҩʱ�䲻Ϊ��)
				parm.setData("isDlvryed", "Y");
				parm.setData("isReturn", "Y"); // δ���
			}

		}

		// ���ڡ���ҩ����ҩģʽ��--û�з�ҩʱ�䣬���Կ�����ҩʱ�䡱in not null
		if (type.equals("Return") && dispEqualSendFlg) {
			// ����Ƿ�ҩ���沢��ѡ�С���ɡ�������롰��ҩʱ�䡱in not null��̨��ѯ����
			if (status.equals("Y"))
				parm.setData("isReturned", "Y"); // ���
			else {
				// PS����ҩ���(����ҩ��ʱ��ֻ��ѯ��ҩʱ�䲻Ϊ��)
				parm.setData("isDGTed", "Y");
				parm.setData("isReturn", "Y"); // δ���
			}
		}
		// --------------------�ر�ܿ�------end----------------------------------

		// ���Ӳ�ѯ<OPD_ORDER��>����--ֻ����ʾ�ѽ��ѵģ�BILL_FLG��
		if (EKTIO.getInstance().ektAyhSwitch()) {
			// parm.setData("BILL_FLG", "N");
		} else {
			parm.setData("BILL_FLG", "Y");
		}
		parm.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify 20110628
		// ���ص�uѯPHA���
		pha = Pha.onQueryByTParm(parm); // ���صĲ�ѯPHA���
		if (pha == null) {
			// �����������
			onClear();
			messageBox("û�з��ϲ�ѯ����������");
			return;
		}
		// ����PHA����ķ������ظò��˵����д���ǩ����һ��TParm���ͱ�����
		data = pha.getAllOrderListParm();
		// System.out.println("data==="+data);
		// ��֤��ѯ�Ľ��
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
		}

		// �õ���ѯ���������
		int count = data.getCount();

		// ��Ϊ�������TParm��addData������һֵ��eg:EXE_FLG���ĸ������жϷŵ�table�е�����������Ӧ��ѭ�������һ�β�
		for (int i = 0; i < count; i++) {
			// ����ǹ���ҩƷ��һ������Ϊ��Y��-->����ҩƷ����һ��
			// ����ǩҽ������(0������Ƽ� 1������ҩ 2������ҩƷ 3����ҩ��Ƭ 4��������Ŀ)
			if ("2".equals(data.getData("RX_TYPE", i))) {
				data.addData("CTL_FLG", "Y");
				/**
				 * J-K:��������ж�û�з��Ź���ҩƷ����Ա�Ͳ��ܿ�������ҩƷ�� ��ô�ڴ˿����޳�����(�жϸò����߶Թ���ҩƷ����Ȩ��)
				 * data.removeRow(i);
				 */
			} else {
				data.addData("CTL_FLG", "N");
			}

		}

		// �ж�������ҩ/����Ƭ,����tableֵ
		if (orderType.equals("WD")) { // ����ҩ
			// ���ݲ�ͬ�Ĺ���ҳ�浱�е�table���еĲ�ͬ�ᶯ̬��������
			if (type.equals("Examine")) { // ��
				this.setColor(data, this.getTable("Table_UP"));
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Dispense")) { // ��
				this.setColor(data, this.getTable("Table_UP"));
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Send")) { // ��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else { // Return-��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"CTL_FLG;RX_PRESRT_NO;PAT_NAME;VARIETY;SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			}
		} else { // ����Ƭ

			// ���ݲ�ͬ�Ĺ���ҳ�浱�е�table���еĲ�ͬ�ᶯ̬��������
			if (type.equals("Examine")) { // ��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Dispense")) { // ��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else if (type.equals("Send")) { // ��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;AGE;WEIGHT;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			} else { // Return-��
				// ��table�����ֵ
				this.callFunction("UI|Table_UP|setParmValue", data,
						"DCTAGENT_FLG;RX_PRESRT_NO;PAT_NAME;VARIETY;SUM_FEE;DECOCT_CODE;PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;COUNTER_NO");
			}

		}

		if ("Y".equals(status) && type.equals("Return")) {

			for (int i = 0; i < data.getCount("RX_NO"); i++) {
				this.getTable("Table_UP").setRowTextColor(i, new Color(255, 0, 0));
			}
		} else {
			for (int i = 0; i < data.getCount("RX_NO"); i++) {
				this.getTable("Table_UP").setRowTextColor(i, new Color(0, 0, 0));
			}
		}
	}

	/**
	 * ���շѵ���ʾΪ��ɫ
	 * 
	 * @param parm
	 * @param table
	 * 
	 *            add by wangjc 20181211
	 */
	private void setColor(TParm parm, TTable table) {
		String sql = "";
		TParm result = null;
		for (int i = 0; i < parm.getCount(); i++) {
			sql = "SELECT COUNT(*) AS COUNT FROM OPD_ORDER WHERE RX_NO = '" + parm.getValue("RX_NO", i)
					+ "' AND PRESRT_NO = '" + parm.getValue("PRESRT_NO", i) + "' AND BILL_FLG = 'Y'";
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getInt("COUNT", 0) > 0) {
				table.setRowColor(i, Color.YELLOW);
			} else {
				table.setRowColor(i, null);
			}
		}
	}

	// ���÷�������ѯ���û��Թ���ҩƷ�Ƿ���Ȩ��
	public boolean isCtlDrugPopedom() {
		// ������ID
		String optId = Operator.getID();
		Personal checkCtlFlg = new Personal();
		TParm result = new TParm(checkCtlFlg.getSYSOperator(optId));
		String CtlFlg = (String) result.getValue("CTRL_FLG", 0);
		// this.messageBox(CtlFlg);
		if (!"Y".equalsIgnoreCase(CtlFlg)) {
			this.messageBox("��û�жԹ���ҩƷ����Ȩ�ޣ�");
			return false;
		}
		return true;
	}

	// ȫ��ִ��
	public void onDoEXE() {
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this.callFunction("UI|ALLEXECUTE|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|Table_DOWN|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, i, 0);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
			onExeFlg(nowFlag, i);
		}
	}

	// ִ���Ͱ�ҩ������
	public void onDoATC() {
		/*
		 * //this.messageBox_("�Ͱ�ҩ��������������"); //�õ���ǰ���Ͱ�ҩ��������״̬ boolean nowFlag =
		 * (Boolean)this.callFunction( "UI|PACKAGEDRUG|isSelected"); //ѭ��ȡ���Թ�������
		 * this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, selectDownRow, 1);
		 * //ȡ���������ó��� Order nowOrder = nowOrdList.getOrder(selectDownRow);
		 * //�����ѡ�ĸ�checkBox���Ͱ�order��ExeFlg������Ϊ�棬�Ա���pha������onExecute������ʹ��
		 * nowOrder.setAtcFlg(TCM_Transform.getString(nowFlag));
		 */

		((TTextField) getComponent("BASKET_ID")).grabFocus();// by liyh 20120914
		// ��ʼ�� ����ǩ�õ�����
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this.callFunction("UI|PACKAGEDRUG|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|Table_DOWN|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			String ATCFlg = getATCFlgFromSYSFee(nowOrdList.getOrder(i).getOrderCode());
			String boxFlg = nowOrdList.getOrder(i).getGiveboxFlg();
			if (ATCFlg.length() == 0 || ATCFlg.equals("N") || boxFlg.equals("Y"))
				continue;
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|Table_DOWN|setValueAt", nowFlag, i, 1);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
			onATCFlg(TCM_Transform.getString(nowFlag), i);
		}
	}

	private String getATCFlgFromSYSFee(String orderCode) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT ATC_FLG " + " FROM SYS_FEE" + " WHERE ORDER_CODE='" + orderCode + "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("ATC_FLG", 0);
	}

	/**
	 * @return boolean true ����ִ�б��� false �������ι��˲����Ա���
	 */
	public boolean checkOrgBatch() {

		// ��IND����ִ�пۿ�Ŀ��Ҵ���
		String orgCode = (String) this.getValue("EXEC_DEPT_CODE");
		TParm orgCodeForInd = new TParm();
		orgCodeForInd.setData("org_code", orgCode);
		// IND�ӿ�
		Medicine subInd = new Medicine();

		return subInd.checkIndOrgBatch(orgCodeForInd);

	}

	/**
	 * ��ӡ��ҩ��
	 */
	public void onCode() {
		// this.messageBox("��ӡ��ҩ����ҩ��");
		if (!"Examine".equals(type))
			return;
		TTable table = (TTable) this.getComponent("Table_UP");
		if (table.getRowCount() <= 0 || table.getSelectedRow() < 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}

		TParm printData = new TParm();
		printData.setData("NUMBER", "TEXT", SystemTool.getInstance().getNo("ALL", "PHA", "CLAIM_NO", "No"));
		String name = (String) table.getValueAt(table.getSelectedRow(), 2);
		printData.setData("NAME", "TEXT", name);
		this.openPrintDialog("%ROOT%\\config\\prt\\pha\\PHAGetMedicineNum.jhw", printData);

	}

	/**
	 * ��ӡ
	 */
	public void onPrint() {
		// this.messageBox("��ӡ������");
		String status = this.getValueString("FINISH");
		// ֻ����ҩ&&���״̬�ſ���ִ�д�ӡ
		if ("Return".equals(type) && !"Y".equals(status)) {
			this.messageBox("��ҩ�Ժ�ſ��Դ�ӡ��");
			return;
		}

		if (((TTable) this.getComponent("Table_UP")).getRowCount() <= 0
				|| ((TTable) this.getComponent("Table_DOWN")).getRowCount() <= 0) {
			this.messageBox("û����ҩ���ݣ�");
			return;
		}

		// ��Ҫ��ӡ������
		TParm printData = new TParm();
		printData = getReturnDrug();

		// this.messageBox_(printData);

		// ��ҩ����--��ҩ��
		if ("Return".equals(type))
			this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHARetnMedSta.jhw", printData);

	}

	/**
	 * �����ҩ��ӡ������
	 * 
	 * @return TParm
	 */
	public TParm getReturnDrug() {

		String prtTime = StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd HH:mm:ss");
		TParm data = new TParm();
		data.setData("proName", "TEXT", "��PhaMainControl��");
		data.setData("prtTime", "TEXT", prtTime);
		data.setData("HospName", "TEXT", Manager.getOrganization().getHospitalCHNShortName(Operator.getPosition()));
		data.setData("staName", "TEXT", "�� ҩ ȷ �� ��");
		// ͳ��ʱ��
		Timestamp startDateT = (Timestamp) ((TTextFormat) this.getComponent(startDateUI)).getValue();
		Timestamp endDateT = (Timestamp) ((TTextFormat) this.getComponent(endDateUI)).getValue();
		String startDate = ("" + startDateT).substring(0, 10);
		String endDate = ("" + endDateT).substring(0, 10);
		// String
		// startDate=this.getValueString("from_ORDER_DATE").substring(0,10);
		// String endDate=this.getValueString("to_ORDER_DATE").substring(0,10);
		data.setData("staSection", "TEXT", "ͳ������: " + startDate + " �� " + endDate);
		// �Ʊ�ʱ��
		data.setData("prtDate", "TEXT", "�Ʊ�ʱ��: " + prtTime);
		// ִ��ҩ��
		String exeDept = ((TComboBox) this.getComponent("EXEC_DEPT_CODE")).getSelectedName();
		data.setData("durgDept", "TEXT", exeDept);
		// ����
		Pat prtPat = Pat.onQueryByMrNo(this.getValueString("MR_NO").trim());
		String name = prtPat.getName();
		data.setData("name", "TEXT", name);
		// �Ա�
		String sex = prtPat.getSexString();
		data.setData("sex", "TEXT", sex);
		// ����
		String age = "43";
		data.setData("age", "TEXT", age);
		// ������
		String mrNo = prtPat.getMrNo();
		data.setData("mrNo", "TEXT", mrNo);

		TTable downTable = (TTable) getComponent("Table_DOWN");
		downTable.acceptText();
		TParm downParm = downTable.getParmValue();

		String caseNo = downParm.getValue("CASE_NO", 0);

		// �����
		// String caseNo = getPatCaseNo(mrNo);
		// �õ�ʵ�ʿ����Ŀ���
		String deptSql = " SELECT   A.REALDEPT_CODE, B.DEPT_CHN_DESC " + " FROM   REG_PATADM A, SYS_DEPT B "
				+ " WHERE   A.CASE_NO = '" + caseNo + "' AND A.REALDEPT_CODE = B.DEPT_CODE";
		TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
		String deptDesc = (String) deptParm.getData("DEPT_CHN_DESC", 0);
		data.setData("dept", "TEXT", deptDesc);

		/**
		 * // ͨ��CASE_NO�õ������ String icdSql = " SELECT A.ICD_CODE, B.ICD_CHN_DESC " + "
		 * FROM OPD_DIAGREC A, SYS_DIAGNOSIS B " + " WHERE A.CASE_NO = '" + caseNo + "'
		 * AND A.ICD_CODE = B.ICD_CODE AND A.MAIN_DIAG_FLG = 'Y'"; TParm icdParm = new
		 * TParm(TJDODBTool.getInstance().select(icdSql));
		 * 
		 * String icdCode = "ICD"; String icdDesc = "����ICD"; // ��������ϵ�ʱ�� if
		 * (icdParm.getCount() > 0) { icdCode = (String) icdParm.getData("ICD_CODE", 0);
		 * icdDesc = (String) icdParm.getData("ICD_CHN_DESC", 0); }
		 * data.setData("icdCode", "TEXT", icdCode); data.setData("icdValue", "TEXT",
		 * icdDesc);
		 */
		data.setData("OEIType", "TEXT", "��");

		TParm mainData = getMainData();
		data.setData("TABLE", mainData.getData());

		return data;
	}

	/**
	 * ��������ݴ�table��
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public TParm getMainData() {
		TParm result = new TParm();

		String rx_No = this.getValueString("RX_NO");
		String presrt_No = this.getValueString("PRESRT_NO"); // add by huangtt
																// 20150121

		String prtSql = " SELECT  A.LINK_NO, A.ORDER_DESC || CASE WHEN TRIM (A.GOODS_DESC) IS NOT NULL OR TRIM (A.GOODS_DESC) <> '' THEN '(' || A.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC,"
				+ " A.SPECIFICATION,D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,A.MEDI_QTY || ' ' || B.UNIT_CHN_DESC   DOSAGE_QTY, "
				+ " A.TAKE_DAYS,A.DISPENSE_QTY,A.OWN_AMT "
				+ " FROM OPD_ORDER A ,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D  "
				+ " WHERE   A.MEDI_UNIT = B.UNIT_CODE  " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.RX_NO||A.PRESRT_NO = '" + rx_No + presrt_No
				+ "'" + " AND A.PHA_RETN_CODE IS NOT NULL ";

		// System.out.println("prtSql-----:"+prtSql);
		result = new TParm(TJDODBTool.getInstance().select(prtSql));
		result.setCount(result.getCount());
		result.addData("SYSTEM", "COLUMNS", "LINK_NO");
		result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		result.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		result.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		result.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		result.addData("SYSTEM", "COLUMNS", "TAKE_DAYS");
		result.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
		result.addData("SYSTEM", "COLUMNS", "OWN_AMT");

		return result;
	}

	/**
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public String getPatCaseNo(String mrNo) {

		return "";
	}

	/**
	 * ��ӡ���Ӳ���
	 */
	public void onElecCaseHistory() {
		// modify by wangbin 20140730 �ż���ҩ�����䷢ҳ����Ӳ��������ʱ�䵹��
		TParm opdParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM REG_PATADM WHERE MR_NO='"
				+ pat.getMrNo() + "' AND ADM_TYPE='O' ORDER BY REG_DATE DESC"));
		if (opdParm.getCount() < 0) {
			this.messageBox("�˲���û�����ﲡ����");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);

	}

	/**
	 * ��ҩ������
	 */
	public void onGenATCFile() {
		String type = this.getValueString("ATC_TYPE");
		String machineNo = this.getValueString("ATC_MACHINENO");
		if (type.equals("1")) {
			this.onOldATCFile();
		} else if (type.equals("2")) {
			if (machineNo.equals("")) {
				this.messageBox("��ҩ��̨�Ų���Ϊ��");
				return;
			}
			this.onNewATCInsert(machineNo);
		}
	}

	/**
	 * �����Ͱ�ҩ����txt�ļ�
	 */
	public void onOldATCFile() {
		if (getTable("Table_UP").getSelectedRow() < 0) {
			messageBox("��ѡ�񴦷���Ϣ");
			return;
		}
		TParm parm = new TParm();
		// �ż���
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		parm.setData("ADM_TYPE", regInfo.getValue("ADM_TYPE", 0));
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		int count = 0;
		for (int i = 0; i < nowOrdList.size(); i++) {
			Order nowOrder = nowOrdList.getOrder(i);
			// �Ͱ�ҩ��ע��
			if (nowOrder.getAtcFlg().equals("N"))
				continue;
			TParm desc = getOrderData(nowOrder.getOrderCode());
			TParm ransRate = getPHAOrderTransRate(nowOrder.getOrderCode());
			// ��ҩ��
			drugListParm.addData("PRESCRIPT_NO", nowOrder.getPrescriptNo());
			// ����
			drugListParm.addData("PAT_NAME", getTableSelectRowData("PAT_NAME", "Table_UP"));
			// ������
			drugListParm.addData("MR_NO", getTableSelectRowData("MR_NO", "Table_UP"));
			// �Ͱ�ҩ��ʱ��
			drugListParm.addData("DATE", ("" + SystemTool.getInstance().getDate()).substring(0, 19));
			// ҩƷ�б����
			drugListParm.addData("SEQ", i + 1);
			// ҩƷ����
			/*************** ������ ��ҩתHIS���� by liyh 20130104 staert *************/
			// drugListParm.addData("ORDER_CODE", nowOrder.getOrderCode());
			String hisOrderCode = SPCTool.getInstance().getHisOrderCodeBySpcOrderCode(nowOrder.getOrderCode(),
					Operator.getRegion());
			// System.out.println(nowOrder.getOrderCode()+"-------spc to his
			// ordercode-------"+hisOrderCode);
			drugListParm.addData("ORDER_CODE", hisOrderCode);
			/*************** ������ ��ҩתHIS���� by liyh 20130104 end *************/
			// ҩƷ��Ʒ��
			drugListParm.addData("ORDER_GOODS_DESC", desc.getData("TRADE_ENG_DESC", 0));
			int time = getFreqData(nowOrder.getFreqCode()).getInt("FREQ_TIMES", 0);
			double qty = nowOrder.getDispenseQty() / nowOrder.getTakeDays();
			double Minqty = (double) (qty) / time;
			// ҩƷ����
			drugListParm.addData("QTY", Minqty);
			// ҩƷƵ��
			drugListParm.addData("FREQ", nowOrder.getFreqCode());
			// ��ҩ����
			drugListParm.addData("DAY", nowOrder.getTakeDays());
			// �ײ�ʱ�䴫���ֵ
			drugListParm.addData("START_DTTM", "000000000000");
			// �Ͱ�ע�Ǵ����ֵ
			drugListParm.addData("FLG", "N");
			count++;
		}
		if (count > 0) {
			if (drugListParm.getCount("SEQ") <= 0)
				return;
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "1");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction", "onATCO", parm);
			if (parm.getErrCode() < 0) {
				messageBox("�Ͱ�ҩ��ʧ��");
				return;
			}
			messageBox("�Ͱ�ҩ���ɹ�");
		}
	}

	/**
	 * ��ҩ�����ݲ���
	 */
	public void onNewATCInsert(String machineNo) {
		if (getTable("Table_UP").getSelectedRow() < 0) {
			messageBox("��ѡ�񴦷���Ϣ");
			return;
		}
		TParm parm = new TParm();
		pat = new Pat();
		// �ż���
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		parm.setData("ADM_TYPE", regInfo.getValue("ADM_TYPE", 0));
		int count = 0;
		Map<String, String> map = new HashMap<String, String>();
		int seq = 0;
		String preNo = "";
		String preStr1 = "";
		String preStr2 = "";
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		for (int i = 0; i < nowOrdList.size(); i++) {
			Order nowOrder = nowOrdList.getOrder(i);
			// �Ͱ�ҩ��ע��
			if (nowOrder.getAtcFlg().equals("N"))
				continue;
			count++;
			TParm desc = getOrderData(nowOrder.getOrderCode());
			if (map.get(nowOrder.getCaseNo()) == null) {
				preNo = nowOrder.getRxNo();
			}
			map.put(nowOrder.getCaseNo(), nowOrder.getCaseNo());
			// ������ 1
			drugListParm.addData("PRESCRIPTIONNO", preNo);
			preStr1 = preNo;
			// ��ͬ����ǩ��ȡ˳���
			if (!preStr1.equals(preStr2)) {
				seq = 1;
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
				preStr2 = preStr1;
			} else {
				seq++;
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
			}
			// ��ţ�Ĭ�ϣ�3
			drugListParm.addData("GROUP_NO", 2);
			// �����ţ�ҩ����̨���ã� 4
			drugListParm.addData("MACHINENO", TypeTool.getInt(machineNo));
			// ����״̬��Ĭ�ϣ� 5
			drugListParm.addData("PROCFLG", 0);
			pat = Pat.onQueryByMrNo(nowOrder.getMrNo());
			// ����ID 6
			drugListParm.addData("PATIENTID", pat.getMrNo());
			// �������� 7
			drugListParm.addData("PATIENTNAME", pat.getName());
			// ��������ƴ��8
			drugListParm.addData("ENGLISHNAME", "");
			// �������� 9
			drugListParm.addData("BIRTHDAY", pat.getBirthday());
			// �Ա� 10
			drugListParm.addData("SEX", pat.getSexCode());
			// ��� ���� �� 1:���� 2:סԺ[����] 3:סԺ[��ʱ] �� 11
			drugListParm.addData("IOFLG", "1");
			// �Һſ��ұ��� 12
			drugListParm.addData("WARDCD", nowOrder.getExecDeptCode());
			// �Һſ������� 13
			drugListParm.addData("WARDNAME", getDeptDesc(nowOrder.getExecDeptCode()));
			// �����ţ������ޣ� 14
			drugListParm.addData("ROOMNO", "");
			// ������ (������) 15
			drugListParm.addData("BEDNO", "");
			// ҽʦ���� 16
			drugListParm.addData("DOCTORCD",
					nowOrder.getDrCode().getBytes().length > 7 ? new String(nowOrder.getDrCode().getBytes(), 0, 7)
							: nowOrder.getDrCode());
			// ҽʦ���� 17
			drugListParm.addData("DOCTORNAME", getDrDesc(nowOrder.getDrCode()));
			// ����ʱ�� 18
			drugListParm.addData("PRESCRIPTIONDATE", nowOrder.getOrderDate());
			String today = ("" + SystemTool.getInstance().getDate()).substring(0, 10).replaceAll("-", "");
			// ��һ����ҩʱ��(����Ϊ��) 19
			drugListParm.addData("TAKEDATE", SystemTool.getInstance().getDate());
			// ��ʼ���õ�ʱ�����(����Ϊ��) 20
			drugListParm.addData("TAKETIME", "");
			// �����õ�ʱ�����(����Ϊ��) 21
			drugListParm.addData("LASTTIME", "");
			// �������Ĭ��Ϊ1�� 22
			drugListParm.addData("PRESC_CLASS", 0);
			// ҩƷ���� 23
			String hisOrderCode = SPCTool.getInstance().getHisOrderCodeBySpcOrderCode(nowOrder.getOrderCode(),
					Operator.getRegion());
			drugListParm.addData("DRUGCD", hisOrderCode);
			// ҩƷ�� 24
			drugListParm.addData("DRUGNAME", desc.getData("ORDER_DESC", 0) + "" + desc.getData("SPECIFICATION", 0));
			// ҩƷ����(Ĭ��Ϊ��) 25
			drugListParm.addData("DRUGSHAPE", "");
			// ��ҩ���� 26
			drugListParm.addData("PRESCRIPTIONDOSE", nowOrder.getMediQty());
			// ��ҩ��λ 27
			drugListParm.addData("PRESCRIPTIONUNIT", getUnitDesc(nowOrder.getMediUnit()));
			double dispenQty = nowOrder.getDispenseQty();
			int time = getFreqData(nowOrder.getFreqCode()).getInt("FREQ_TIMES", 0);
			int day = nowOrder.getTakeDays();
			double qty = (double) (dispenQty / day) / time;
			BigDecimal sf = new BigDecimal(String.valueOf(qty));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			// ��ҩ���� 28
			drugListParm.addData("DISPENSEDDOSE", data.doubleValue());
			// ��ҩ������ 29
			drugListParm.addData("DISPENSEDTOTALDOSE", dispenQty);
			// ��ҩ��λ 30
			drugListParm.addData("DISPENSEDUNIT", getUnitDesc(nowOrder.getDosageUnit()));
			// ��Ƭ�����ļ��� 31
			drugListParm.addData("AMOUNT_PER_PACKAGE",
					this.getPHAOrderTransRate(nowOrder.getOrderCode()).getDouble("MEDI_QTY", 0));
			String manDesc = this.getManDesc(desc.getValue("MAN_CODE", 0));
			// ������ 32
			drugListParm.addData("FIRM_ID",
					manDesc.getBytes().length > 20 ? new String(manDesc.getBytes(), 0, 20) : manDesc);
			// �������� 33
			drugListParm.addData("DISPENSE_DAYS", day);
			// Ƶ�� 34
			drugListParm.addData("FREQ_DESC_CODE", "");
			// Ƶ������ 35
			drugListParm.addData("FREQ_DESC", getFreqData(nowOrder.getFreqCode()).getValue("FREQ_CHN_DESC", 0));
			// һ����ô������գ� 36
			drugListParm.addData("FREQ_COUNTER", "");
			String timeCode = TXNewATCTool.getTimeLine(nowOrder.getFreqCode());
			// ����ʱ����� 37
			drugListParm.addData("FREQ_DESC_DETAIL_CODE", timeCode);
			String timeDetail = TXNewATCTool.getTimeDetail(nowOrder.getFreqCode());
			// ����ʱ����ϸ 38
			drugListParm.addData("FREQ_DESC_DETAIL", timeDetail);
			// ��ҩ˵������ 39
			drugListParm.addData("EXPLANATION_CODE", "");
			// ��ҩ˵�� 40
			drugListParm.addData("EXPLANATION", "");
			// ��ҩ;�� 41
			drugListParm.addData("ADMINISTRATION_NAME", this.getRouteDesc(nowOrder.getRouteCode()));
			// ��ע 42
			drugListParm.addData("DOCTORCOMMENT", "");
			// ��ҩ˳�� 43
			drugListParm.addData("BAGORDERBY", "");
			// ����ʱ�� 44
			drugListParm.addData("MAKERECTIME", ("" + SystemTool.getInstance().getDate()).substring(0, 19));
			// �Է�����ʱ�� 45
			drugListParm.addData("UPDATERECTIME", "");
			// Ԥ�� 46
			drugListParm.addData("FILLER", "");
			// ҽ���� 47
			drugListParm.addData("ORDER_NO", Long.parseLong(nowOrder.getRxNo()));
			// ˳��� 48
			drugListParm.addData("ORDER_SUB_NO", nowOrder.getSeqNo());
			// ������ʱ����
			// �����ӡ��ʽ 49
			drugListParm.addData("BAGPRINTFMT", "");
			// �ߴ� 50
			drugListParm.addData("BAGLEN", "");
			// ��ҩ�� 51
			drugListParm.addData("TICKETNO", "");
			// ҩ����ӡ�ò����� 52
			drugListParm.addData("BAGPRINTPATIENTNM", "");
			// Ԥ���ô�ӡ���ݣ��������� 53
			drugListParm.addData("FREEPRINTITEM_PRESC1", "");
			// Ԥ���ô�ӡ���ݣ�����2�� 54
			drugListParm.addData("FREEPRINTITEM_PRESC2", "");
			// Ԥ���ô�ӡ���ݣ�����3�� 55
			drugListParm.addData("FREEPRINTITEM_PRESC3", "");
			// Ԥ���ô�ӡ���ݣ�����4�� 56
			drugListParm.addData("FREEPRINTITEM_PRESC4", "");
			// Ԥ���ô�ӡ���ݣ�����5�� 57
			drugListParm.addData("FREEPRINTITEM_PRESC5", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ1�� 58
			drugListParm.addData("FREEPRINTITEM_DRUG1", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ2�� 59
			drugListParm.addData("FREEPRINTITEM_DRUG2", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ3�� 60
			drugListParm.addData("FREEPRINTITEM_DRUG3", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ4�� 61
			drugListParm.addData("FREEPRINTITEM_DRUG4", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ5�� 62
			drugListParm.addData("FREEPRINTITEM_DRUG5", "");
			// �ۺϰ�ҩ�ñ�־λ(���й���ʹ�� 63
			drugListParm.addData("SYNTHETICFLG", "");
			// 0:����ֽ��1:�ڴ˴�����׷��һ������ֽ 64
			drugListParm.addData("CUTFLG", "");
			// ����ʱ�䣨number�͡��޷����뺺�֣� 65
			drugListParm.addData("PHARMACYTIME", "");
			// ҩƷ�ϵĿ�ӡ 66
			drugListParm.addData("CARVEDSEAL", "");
			// ҩƷ��ӡ��� 67
			drugListParm.addData("CARVEDSEALABB", "");
			// ������Ϣ�����룱 68
			drugListParm.addData("PREBARCODE1", "");
			// ������Ϣ�����룲 69
			drugListParm.addData("PREBARCODE2", "");
			// ҩƷ��Ϣ������ 70
			drugListParm.addData("PREDRUGBARCODE", "");
			// �������ʽ 71
			drugListParm.addData("PREBARCODEFMT", "");
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "2");
		}
		parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction", "onATCO", parm);
		if (parm.getErrCode() < 0) {
			messageBox("�Ͱ�ҩ��ʧ��");
			return;
		}
		if (count > 0)
			messageBox("�Ͱ�ҩ���ɹ�");
	}

	/**
	 * ��ҩ������
	 * 
	 * @param orgCode
	 * @param counterNo
	 * @return
	 */
	public TParm getPHAcounterNoData(String orgCode, String counterNo) {
		return new TParm(getDBTool().select(" SELECT MACHINENO,ATC_TYPE " + " FROM PHA_COUNTERNO" + " WHERE ORG_CODE='"
				+ orgCode + "' AND COUNTER_NO='" + counterNo + "'"));
	}

	/**
	 * ȡ��ҩƷ��ҩ��λ�Ϳ�浥λת����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getPHAOrderTransRate(String orderCode) {
		return new TParm(getDBTool().select(" SELECT DOSAGE_QTY/STOCK_QTY TRANS_RATE,MEDI_QTY " + " FROM PHA_TRANSUNIT "
				+ " WHERE ORDER_CODE='" + orderCode + "'"));
	}

	/**
	 * ȡ��ҩƷ����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderData(String orderCode) {
		return new TParm(getDBTool()
				.select(" SELECT ORDER_DESC,GOODS_DESC,ALIAS_DESC,TRADE_ENG_DESC,DESCRIPTION,MAN_CODE,SPECIFICATION"
						+ " FROM SYS_FEE" + " WHERE ORDER_CODE='" + orderCode + "'"));
	}

	/**
	 * ȡ�ÿ�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT DEPT_CHN_DESC" + " FROM SYS_DEPT " + " WHERE DEPT_CODE='" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ȡ����Ա����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getDrDesc(String userId) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT USER_NAME " + " FROM SYS_OPERATOR " + " WHERE USER_ID='" + userId + "'"));
		String userName = "";
		if (parm.getCount() > 0)
			userName = parm.getValue("USER_NAME", 0);
		return userName;
	}

	/**
	 * ȡ�õ�λ����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getUnitDesc(String unitCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT " + " WHERE UNIT_CODE='" + unitCode + "'"));
		String unitDesc = "";
		if (parm.getCount() > 0)
			unitDesc = parm.getValue("UNIT_CHN_DESC", 0);
		return unitDesc;
	}

	/**
	 * ȡ����ҩ;������
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getRouteDesc(String routeCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE " + " WHERE ROUTE_CODE='" + routeCode + "'"));
		String routeDesc = "";
		if (parm.getCount() > 0)
			routeDesc = parm.getValue("ROUTE_CHN_DESC", 0);
		return routeDesc;
	}

	/**
	 * ȡ��Ƶ������
	 * 
	 * @param freqCode
	 * @return
	 */
	public TParm getFreqData(String freqCode) {
		TParm parm = new TParm(getDBTool().select(" SELECT FREQ_CHN_DESC,FREQ_TIMES,DESCRIPTION " + " FROM SYS_PHAFREQ "
				+ " WHERE FREQ_CODE='" + freqCode + "'"));
		return parm;
	}

	/**
	 * ȡ��������������
	 * 
	 * @param freqCode
	 * @return
	 */
	public String getManDesc(String manCode) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT MAN_CHN_DESC " + " FROM SYS_MANUFACTURER " + " WHERE MAN_CODE='" + manCode + "'"));
		String manDesc = "";
		if (parm.getCount() > 0)
			manDesc = parm.getValue("MAN_CHN_DESC", 0);
		return manDesc;
	}

	// =============================== chenxi modify 20130520
	/**
	 * ȡ���������
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getCtzDesc(String ctzCode) {
		TParm parm = new TParm(
				getDBTool().select(" SELECT CTZ_DESC " + " FROM SYS_CTZ " + " WHERE CTZ_CODE='" + ctzCode + "'"));
		String ctzDesc = "";
		if (parm.getCount() > 0)
			ctzDesc = parm.getValue("CTZ_DESC", 0);
		return ctzDesc;
	}

	// ========================= chenxi modify 20130603
	/**
	 * ȡ��ҩƷһƬ�ļ���
	 */
	public String getMediQty(String orderCode) {
		TParm parm = new TParm(
				getDBTool().select("SELECT MEDI_QTY FROM PHA_TRANSUNIT " + " WHERE ORDER_CODE = '" + orderCode + "'"));
		String qty = "";
		if (parm.getCount() > 0)
			qty = parm.getValue("MEDI_QTY", 0);
		return qty;
	}

	/**
	 * �õ�his����
	 * 
	 * @param ctzCode
	 * @return
	 */
	public String getBarCode(String order_code) {
		TParm parm = new TParm(getDBTool()
				.select(" SELECT HIS_ORDER_CODE " + " FROM SYS_FEE_SPC " + " WHERE ORDER_CODE='" + order_code + "'"));
		String hisCode = "";
		if (parm.getCount() > 0)
			hisCode = parm.getValue("HIS_ORDER_CODE", 0);
		return hisCode;
	}

	/**
	 * �õ����ݿ����Tool
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ȡ�ñ��ѡ��������
	 * 
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableSelectRowData(String rowName, String tableName) {
		return getTableRowData(getTable(tableName).getSelectedRow(), rowName, tableName);
	}

	/**
	 * ���к�ȡ�ø�������
	 * 
	 * @param row
	 *            int
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableRowData(int row, String rowName, String tableName) {
		return getTable(tableName).getParmValue().getValue(rowName, row);
	}

	/**
	 * ȡ�ñ��ؼ�
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	/**
	 * ҩ���к�
	 */
	public void onCall() {
		/**
		 * if (getValueString("EXEC_DEPT_CODE").length() == 0 ||
		 * getValueString("COUNTER_NO").length() == 0) { messageBox("��ѡ��һ��ҩ������ҩ����");
		 * return; } if (this.getValueString("MR_NO").length() == 0) {
		 * messageBox("�����벡����"); return; }
		 * 
		 * TParm parm = new TParm(); parm.setData("COUNTER_NO",
		 * getValueString("COUNTER_NO")); parm.setData("EXEC_DEPT_CODE",
		 * getDeptDesc(getValueString("EXEC_DEPT_CODE")));
		 * 
		 * parm.setData("MR_NO", this.getValueString("MR_NO"));
		 * openDialog("%ROOT%\\config\\pha\\PHACallNoDiag.x", parm);
		 **/
		// $$================add by lx 2012/02/23 start==================$$//
		if (this.getValueString("MR_NO").length() == 0) {
			messageBox("�����벡����");
			return;
		}
		// ��ѯ��Ӧ����;
		String sql = "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
		sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "'";
		sql += " AND a.SEX_CODE=b.ID";
		TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
		String patName = patParm.getValue("PAT_NAME", 0);
		if (patName.equals("")) {
			this.messageBox("�޴˲���");
			return;
		}
		String strSend = this.getValueString("MR_NO") + "|";
		strSend += patParm.getValue("PAT_NAME", 0) + "|";
		strSend += patParm.getValue("SEX", 0) + "|";
		strSend += patParm.getValue("BIRTH_DATE", 0) + "|";
		strSend += Operator.getIP();
		// System.out.println("========sendString=======" + strSend);
		TParm inParm = new TParm();
		inParm.setData("msg", strSend);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doPHACallNo", inParm);

		// $$================add by lx 2012/02/23 end====================$$//

	}

	/**
	 * ����ҩ
	 */
	public void onArrive() {
		if (this.getValueString("MR_NO").length() == 0) {
			messageBox("�����벡����");
			return;
		}
		// ��ѯ��Ӧ����;
		String sql = "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
		sql += " WHERE MR_NO='" + this.getValueString("MR_NO") + "'";
		sql += " AND a.SEX_CODE=b.ID";
		TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
		String patName = patParm.getValue("PAT_NAME", 0);
		if (patName.equals("")) {
			this.messageBox("�޴˲���");
			return;
		}
		String strSend = this.getValueString("MR_NO") + "|";
		strSend += patParm.getValue("PAT_NAME", 0) + "|";
		strSend += patParm.getValue("SEX", 0) + "|";
		strSend += patParm.getValue("BIRTH_DATE", 0) + "|";
		strSend += Operator.getIP();
		// System.out.println("========sendString=======" + strSend);
		TParm inParm = new TParm();
		inParm.setData("msg", strSend);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doPHAArriveCallNo", inParm);

	}

	/**
	 * ȡ�ÿ�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getCounterNoDesc(String orgCode, String counterNo) {
		TParm parm = new TParm(getDBTool().select(" SELECT COUNTER_DESC" + " FROM PHA_COUNTERNO" + " WHERE ORG_CODE='"
				+ orgCode + "'" + " AND   COUNTER_NO = '" + counterNo + "'"));
		return parm.getValue("COUNTER_DESC", 0);
	}

	/**
	 * ��ʼ����������
	 */
	private void initUIData() {
		setValue("EXEC_DEPT_CODE", Operator.getDept());
		setValue("COUNTER_NO", getCounterNoByUser());
	}

	/**
	 * �õ���ҩ���ں�
	 * 
	 * @return String
	 */
	public String getCounterNoByUser() {
		String user = "";
		if (type.equals("Dispense"))
			user = "A.DOSAGE_USER";
		else if (type.equals("Send") || type.equals("Return"))
			user = "A.DISPENSE_USER";
		else
			return "";
		String sql = "  SELECT A.COUNTER_NO" + "    FROM PHA_COUNTERNO A ,IND_ORG B "
				+ "   WHERE B.ORG_CODE=A.ORG_CODE " + "     AND A.CHOSEN_FLG = 'Y' " + "     AND B.REGION_CODE ='"
				+ Operator.getRegion() + "' " + "     AND A.ORG_CODE='" + Operator.getDept() + "'" + "     AND   "
				+ user + " = '" + Operator.getID() + "'";
		TParm parm = new TParm(getDBTool().select(sql));
		if (parm.getCount("COUNTER_NO") <= 0)
			return "";
		return parm.getValue("COUNTER_NO", 0);
	}

	public void onPaster() {
		// fux modify 20180906 �Ա�ҩ����ʾ
		/*
		 * if (((TTable) this.getComponent("Table_UP")).getSelectedRow() < 0) {
		 * messageBox("δѡ�д���"); return; }
		 */
		if ("WD".equals(orderType))
			// onPrint(nowOrdList.getRxNo(),(nowOrdList.getPresrtNo()==0?"":nowOrdList.getPresrtNo()+""));
			// //modify by huangtt 20150121 nowOrdList.getPresrtNo()
			onPrint(nowOrdList.getRxNo(), nowOrdList.getRxPresertNo()); // modify
																		// by
																		// huangtt
																		// 20150121
																		// nowOrdList.getPresrtNo()
		else
			onPrintTCM();
	}

	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		// �޸Ķ�ҽ�ƿ����� begin luhai 2012-2-27
		// TParm patParm = EKTIO.getInstance().getPat();
		// if (patParm.getErrCode() < 0) {
		// this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
		// return;
		// }
		// setValue("MR_NO", patParm.getValue("MR_NO"));
		// onMrNo();
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("MR_NO", parm.getValue("MR_NO"));
		onMrNo();
		// �޸Ķ�ҽ�ƿ����� end luhai 2012-2-27
	}

	/**
	 * ����ǩ�س��¼�
	 * 
	 * @author liyh
	 * @date 20120914
	 */
	public void onRxNo() {
		onQuery();
	}

	/**
	 * ���没���ź�ҩ����ӱ�ǩID��ϵ
	 * 
	 * @author liyh
	 * @date 20120830
	 */
	public boolean onSaveMedBasket(String bastkId) {

		// ��ѯ���ӱ�ǩ״̬
		String flag = "1";// getEleTagStatus(bastkId);
		if (UPDATE_FLAG_TRUE.equals(flag)) {// ����ҩ��ɹ������没�˺�ҩ����ӱ�ǩ��ϵ
			String ip = Operator.getIP();
			String opdUser = Operator.getName();

			TParm parm = new TParm();
			parm.setData("MR_NO", getValueString("MR_NO"));
			TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
			TParm medParm = new TParm();
			medParm.setData("MR_NO", getValueString("MR_NO"));
			medParm.setData("BASKET_ID", getValueString("BASKET_ID"));
			medParm.setData("RX_NO", getValueString("RX_NO"));
			medParm.setData("CASE_NO", caseNo == null ? "-1" : caseNo);
			medParm.setData("PAT_NAME", pationParm.getValue("PAT_NAME", 0));
			medParm.setData("SEX_TYPE", pationParm.getValue("SEX_NAME", 0));
			medParm.setData("AGE", pationParm.getValue("AGE", 0));
			medParm.setData("OPT_USER", opdUser);
			medParm.setData("OPT_TERM", ip);

			// ���没���ź�ҩ��ID��ϵ
			// System.out.println("----update medbasktet
			// sql:"+PhaSQL.savBasketInfo(medParm));
			TParm result = new TParm(TJDODBTool.getInstance().update(PhaSQL.savBasketInfo(medParm)));
			if (result.getErrCode() < 0) {
				return false;
			}
		} else {
			return false;
		}
		return true;

	}

	/**
	 * �鿴���ӱ�ǩ״̬
	 * 
	 * @param basketId
	 * @return 1:�ɹ���-1���ɹ�
	 */
	public String getEleTagStatus(String basketId) {
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("id", basketId + uuid.toString());
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		try {
			Map<String, Object> map = eti.getLable(mp);
			if (null != map) {
				String resultStatus = (String) map.get("Status");
				if (!"10000".equals(resultStatus)) {// �ɹ�״̬ Ϊ10000
					return UPDATE_FLAG_TRUE;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return UPDATE_FLAG_TRUE;
		}

		return UPDATE_FLAG_TRUE;// UPDATE_FLAG_FLASE;
	}

	/**
	 * ���÷�ҩ����ӱ�ǩ���½ӿ�
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120903
	 */
	public void onBasketId() {
		if ("Dispense".equalsIgnoreCase(type)) {// ֻ����ҩ �Ŵ����¼�
			// ������
			String mrNo = getValueString("MR_NO");
			// ҩ��ĵ��ӱ�ǩid
			String bastkId = getValueString("BASKET_ID");
			// ����ǩ��
			String rxNoStr = getValueString("RX_NO");
			// System.out.println("parm==="+parm);
			if (null == mrNo || mrNo.length() <= 0) {
				this.messageBox("�����Ų���Ϊ��,����ѡ��һ������ǩ��¼");
				return;
			}

			if (null != bastkId && bastkId.length() > 0) {
				bastkId = bastkId.toUpperCase();
				TParm parm = new TParm();
				parm.setData("MR_NO", mrNo);
				// ͨ�������Ų�ѯ������Ϣ
				// System.out.println("------------ͨ�������Ų�ѯ������Ϣ--sql:"+
				// PhaSQL.getPationInfo(parm));
				TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
				/************** ��ѯ���ӱ�ǩ�������� by liyh 20130520 start ******************/
				TParm orgParm = new TParm();
				/*
				 * orgParm.setData("ORG_CODE", this.getValue("EXEC_DEPT_CODE")); TParm
				 * orgResultParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode
				 * (orgParm);
				 */
				/************** ��ѯ���ӱ�ǩ�������� by liyh 20130520 end ******************/
				if (null != pationParm && pationParm.getCount() > 0) {// �����Ϊ��
					// ��ϸ���ӱ�ǩ
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					// ��½���ӱ�ǩ
					// login();
					String nameInfo = "";
					// ����
					String age = pationParm.getValue("AGE", 0);
					// ����
					String patName = pationParm.getValue("PAT_NAME", 0);
					// �Ա�
					String sex = pationParm.getValue("SEX_NAME", 0);
					// ƴ��һ����ʾ 7����
					if (age.indexOf("��") != -1) {// <1�� ��ʾn�£�����Ϊ2-3��
						// �������Ȳ��ܴ���4λ
						patName = patName.length() > 4 ? patName.substring(0, 4) : patName;
						nameInfo = patName + age;
					} else {// >1�꣬����Ϊ1-2
						// �������Ȳ��ܴ���5λ
						patName = patName.length() > 5 ? patName.substring(0, 4) : patName;
						nameInfo = patName + age;
					}
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					uuid = UUID.randomUUID();

					m.put("ObjectId", uuid.toString());
					m.put("ObjectType", 3);
					m.put("ObjectName", "medBasket");
					// m.put("LabelNo", "01048A");
					// ���ӱ�ǩid
					m.put("LabelNo", bastkId);
					// ��վid
					m.put("StationID", "2");
					// ��һ�� ��ʾ�û���������
					m.put("ProductName", nameInfo);
					// �ڶ��� ��ʾ������ ���Ա�
					m.put("Spec", mrNo + " " + sex);
					// ������ ���ӱ�ǩ��ά���뺬��
					m.put("ShelfNo", "SN1000");
					// ��˸����
					m.put("Light", 20);
					// �Ƿ����ƣ�true:��
					m.put("Enabled", true);

					Iterator it = m.entrySet().iterator();
					// System.out.println("-------------��-���ӱ�ǩ��������---------start----------");
					ElectronicTagsInf eti = new ElectronicTagsImpl();
					// ���õ��ӱ�ǩ�ӿ�
					Map<String, Object> map = eti.cargoUpdate(m);
					it = map.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
						// System.out.println(entry.getKey() + "==============="
						// + entry.getValue());
					}
					// System.out.println("------------��--���ӱ�ǩ��������---------end----------");
					if (null != map) {
						String status = (String) map.get("Status");
						if (null != status && "10000".equals(status)) {// ���µ��ӱ�ǩ״̬�ɹ�
							/*
							 * boolean flag = onSaveMedBasketNew(bastkId,patName,age,sex); if(!flag){
							 * this.messageBox("���没�˺͵��ӱ�ǩ��ϵʧ��"); return; }
							 */

							return;
						} else {// ����ֵ ״̬ ����ȷ
							if (this.messageBox("��ʾ", "����ҩ����ӱ�ǩʧ�ܣ��Ƿ��������", 2) == 0) {// ��
								this.messageBox("��ɨ����ӱ�ǩ");
								((TTextField) getComponent("BASKET_ID")).grabFocus();
								return;
							}
						}
					} else {// û�з���ֵ
						if (this.messageBox("��ʾ", "����ҩ����ӱ�ǩʧ�ܣ��Ƿ��������", 2) == 0) {// ��
							this.messageBox("��ɨ����ӱ�ǩ");
							((TTextField) getComponent("BASKET_ID")).grabFocus();
							return;
						}
					}
					/*
					 * Map<String, Object> map = new LinkedHashMap<String, Object> ();
					 * map.put("ProductName", nameInfo); map.put("SPECIFICATION", mrNo +" "+sex );
					 * map.put("TagNo", bastkId); map.put("Light", 50); map.put("APRegion"
					 * ,orgResultParm.getValue("onQueryLabelByOrgCode", 0)); list.add(map); try{
					 * String url = Constant.LABELDATA_URL ;
					 * EleTagControl.getInstance().sendNewEleTag(list, url); }catch (Exception e) {
					 * // TODO: handle exception e.printStackTrace();
					 * System.out.println("���õ��ӱ�ǩ����ʧ��"); }
					 */
				} else {
					this.messageBox("��ѯ������Ϣʧ��");
					return;
				}
			}
		}

	}

	/**
	 * ���µ��ӱ�ǩ
	 * 
	 * @param bastkId
	 * @param nameInfo
	 * @param sex
	 * @param mrNo
	 * @return
	 * @author liyh
	 * @date 20120919
	 */
	public boolean sendEleTag(String bastkId, String nameInfo, String age, String mrNo, String sex) {
		Map<String, Object> m = new LinkedHashMap<String, Object>();
		if (null == uuid)
			uuid = UUID.randomUUID();
		int lightCount = 20;
		if (null == nameInfo || "".equals(nameInfo) || nameInfo.trim().length() < 1) {// name Ϊ��ʱ ��ҩ ��յ��ӱ�ǩ ��˸1�Σ�
			lightCount = 1;
		}
		m.put("ObjectId", uuid.toString());
		m.put("ObjectType", 3);
		m.put("ObjectName", "medBasket");
		// m.put("LabelNo", "01048A");
		// ���ӱ�ǩid
		m.put("LabelNo", bastkId);
		// ��վid
		m.put("StationID", "2");
		// ��һ�� ��ʾ�û���������
		m.put("ProductName", nameInfo + age);
		// �ڶ��� ��ʾ������ ���Ա�
		m.put("Spec", mrNo + " " + sex);
		// ������ ���ӱ�ǩ��ά���뺬��
		if (null == nameInfo || "".equals(nameInfo) || nameInfo.trim().length() < 1) {// name Ϊ��ʱ ��ҩ ��յ��ӱ�ǩ ��˸1�Σ�
			m.put("ShelfNo", "");
		} else {
			m.put("ShelfNo", bastkId);
		}
		// ��˸����
		m.put("Light", 20);
		// �Ƿ����ƣ�true:��
		m.put("Enabled", true);

		Iterator it = m.entrySet().iterator();
		// System.out.println("------------��ҩ--���ӱ�ǩ��������---------start----------");
		ElectronicTagsInf eti = new ElectronicTagsImpl();
		// ���õ��ӱ�ǩ�ӿ�
		Map<String, Object> map = eti.cargoUpdate(m);
		if (null != map) {
			it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
				// System.out.println(entry.getKey() + "===============" +
				// entry.getValue());
			}
			String status = (String) map.get("Status");
			if (null != status && "10000".equals(status)) {// ���µ��ӱ�ǩ״̬�ɹ�
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/**
	 * ��ѯ������Ϣ
	 */
	public boolean queryPationInfo(String bastkId, String mrNo, String rxNo) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		// ͨ�������Ų�ѯ������Ϣ
		// System.out.println("------------ͨ�������Ų�ѯ������Ϣ--sql:"+
		// PhaSQL.getPationInfo(parm));
		TParm pationParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getPationInfo(parm)));
		if (null != pationParm && pationParm.getCount() > 0) {// �����Ϊ�� ��ϸ���ӱ�ǩ
			String nameInfo = "";
			// ����
			String age = pationParm.getValue("AGE", 0);
			// ����
			String patName = pationParm.getValue("PAT_NAME", 0);
			// �Ա�
			String sex = pationParm.getValue("SEX_NAME", 0);
			// ƴ��һ����ʾ 7����
			if (age.indexOf("��") != -1) {// <1�� ��ʾn�£�����Ϊ2-3��
				// �������Ȳ��ܴ���4λ
				patName = patName.length() > 4 ? patName.substring(0, 4) : patName;
				nameInfo = patName + age;
			} else {// >1�꣬����Ϊ1-2
				// �������Ȳ��ܴ���5λ
				patName = patName.length() > 5 ? patName.substring(0, 4) : patName;
				nameInfo = patName + age;
			}
			// ���汣�没���ź�ҩ����ӱ�ǩID��ϵ
			onSaveMedBasketNew(bastkId, patName, age, sex, mrNo, rxNo);
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ���没���ź�ҩ����ӱ�ǩID��ϵ
	 * 
	 * @author liyh
	 * @date 20120830
	 */
	public boolean onSaveMedBasketNew(String bastkId, String name, String age, String sex, String mrNO, String rxNO) {

		String ip = Operator.getIP();
		String opdUser = Operator.getName();
		TParm medParm = new TParm();
		medParm.setData("MR_NO", mrNO);
		medParm.setData("BASKET_ID", bastkId);
		medParm.setData("RX_NO", rxNO);
		medParm.setData("CASE_NO", caseNo == null ? "-1" : caseNo);
		medParm.setData("PAT_NAME", name);
		medParm.setData("SEX_TYPE", sex);
		medParm.setData("AGE", age);
		medParm.setData("OPT_USER", opdUser);
		medParm.setData("OPT_TERM", ip);

		// ���没���ź�ҩ��ID��ϵ
		// System.out.println("----update medbasktet
		// sql:"+PhaSQL.savBasketInfo(medParm));
		TParm result = new TParm(TJDODBTool.getInstance().update(PhaSQL.savBasketInfo(medParm)));
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;

	}

	public void onPrint(String rxNo, String rxPresrtNo) {
		TParm parm = onQuery(rxNo, rxPresrtNo);
		String Date = StringTool.getString(parm.getTimestamp("ORDER_DATE", 0), "yyyy/MM/dd"); // parm.getValue("ORDER_DATE",0);
		String INDT = parm.getValue("COUNTER_NO", 0);
		String INDNO = parm.getValue("PRINT_NO", 0);
		String Name = parm.getValue("PAT_NAME", 0);
		String Sex = parm.getValue("CHN_DESC", 0);
		// String Age = parm.getValue("AGE", 0) + "��";
		Timestamp sysDate = TJDODBTool.getInstance().getDBTime();// add by sunqy
																	// 20140701
																	// ��������
		String Age = com.javahis.util.DateUtil.showAge((Timestamp) parm.getData("BIRTH_AGE", 0), sysDate);// add by
																											// sunqy
																											// 20140701
																											// ��������
		String kb = parm.getValue("DEPT_CHN_DESC", 0);
		String Room = parm.getValue("USER_NAME", 0);
		String MR_NO = parm.getValue("MR_NO", 0);
		String checkuUser = parm.getValue("CHECK_USER", 0);
		String orderCat1 = "";
		if (orderType.equals("WD")) {
			orderCat1 = "'PHA_W','PHA_C'";
		} else
			orderCat1 = "'PHA_G'";
		TParm tparmPHA = getPHAParm(rxNo, orderCat1, rxPresrtNo);
		if (tparmPHA.getCount() <= 0) {
			messageBox("��ҩȷ�ϵ��޴�ӡ����");
			return;
		}

		// ���� E ����
		// F �������
		// I ���
		// O �ڷ�

		TParm oParm = new TParm();
		// ==liling 20140825 modify start ======
		/*
		 * //==liling 20140825 ����start ====== TParm eParm = new TParm(); TParm fiParm =
		 * new TParm(); int ei = 0 ; int fii = 0 ; int oi = 0 ; for(int i = 0 ; i <
		 * tparmPHA.getCount("GOODS_DESC");i++){ TParm rowParm = tparmPHA.getRow(i);
		 * String doseType = tparmPHA.getValue("DOSE_TYPE",i); if("F".equals(doseType)||
		 * "I".equals(doseType)){//�������/��� fiParm.addData("MIAN_FLG",
		 * rowParm.getValue("MIAN_FLG")); fiParm.addData("LINK_NO",
		 * rowParm.getValue("LINK_NO")); fiParm.addData("GOODS_DESC",
		 * rowParm.getValue("GOODS_DESC")); fiParm.addData("SPECIFICATION",
		 * rowParm.getValue("SPECIFICATION")); fiParm.addData("ROUTE_CODE",
		 * rowParm.getValue("ROUTE_CODE")); fiParm.addData("FREQ_CODE",
		 * rowParm.getValue("FREQ_CODE")); fiParm.addData("BATCH_NO",
		 * rowParm.getValue("BATCH_NO")); fiParm.addData("QTY",
		 * rowParm.getValue("QTY")); fii = rowParm.getInt("DOSAGE_PRN_TOT"); }else if
		 * ("E".equals(doseType)){//���� eParm.addData("MIAN_FLG",
		 * rowParm.getValue("MIAN_FLG")); eParm.addData("LINK_NO",
		 * rowParm.getValue("LINK_NO")); eParm.addData("GOODS_DESC",
		 * rowParm.getValue("GOODS_DESC")); eParm.addData("SPECIFICATION",
		 * rowParm.getValue("SPECIFICATION")); eParm.addData("ROUTE_CODE",
		 * rowParm.getValue("ROUTE_CODE")); eParm.addData("FREQ_CODE",
		 * rowParm.getValue("FREQ_CODE")); eParm.addData("BATCH_NO",
		 * rowParm.getValue("BATCH_NO")); eParm.addData("QTY", rowParm.getValue("QTY"));
		 * ei = rowParm.getInt("DOSAGE_PRN_TOT"); }else if("O".equals(doseType)){//�ڷ�
		 * oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
		 * oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
		 * oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
		 * oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
		 * oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
		 * oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
		 * oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO")); oParm.addData("QTY",
		 * rowParm.getValue("QTY")); oi = rowParm.getInt("DOSAGE_PRN_TOT"); } if (parm
		 * != null) { TParm date = null;
		 * 
		 * if(fiParm.getCount("GOODS_DESC")> 0){ date = getPringData(Date, INDT, INDNO,
		 * Name, Sex, Age, kb, Room, MR_NO, checkuUser, fiParm); // ���ô�ӡ���� //
		 * this.messageBox("date" + date); date.setData("Title", "TEXT",
		 * "��(��)����ҩȷ�ϵ�");//==liling 20140825 add this.openPrintWindow(IReportTool
		 * .getInstance().getReportPath("phaInd_V45.jhw"), IReportTool.getInstance
		 * ().getReportParm("phaInd_V45.class",date),false); }
		 * if(eParm.getCount("GOODS_DESC")> 0){ date = getPringData(Date, INDT, INDNO,
		 * Name, Sex, Age, kb, Room, MR_NO, checkuUser, eParm); // ���ô�ӡ���� //
		 * this.messageBox("date" + date); date.setData("Title", "TEXT",
		 * "��(��)����ҩȷ�ϵ�");//==liling 20140825 add this.openPrintWindow(IReportTool
		 * .getInstance().getReportPath("phaInd_V45.jhw"), IReportTool.getInstance
		 * ().getReportParm("phaInd_V45.class",date),false); }
		 */// ==liling 20140825 ���� end ======
		for (int i = 0; i < tparmPHA.getCount("GOODS_DESC"); i++) {
			TParm rowParm = tparmPHA.getRow(i);
			oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
			oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
			oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
			oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
			oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
			oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
			oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
			oParm.addData("QTY", rowParm.getValue("QTY"));
		}
		if (parm != null) {
			TParm date = null;
			if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");
				String previewSwitch = IReportTool.getInstance().getPrintSwitch("phaInd_V45.previewSwitch");
				if (previewSwitch.equals(IReportTool.OFF)) {// Ԥ�����ع��� ֱ�Ӵ�ӡ
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
				} else {// Ԥ�����ؿ���û��Ԥ������ ��Ĭ��Ԥ��
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), false);
				}
				date = null;
			}
			if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ����ס�");// ==liling
																// 20140825 add
				this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
						IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
				date = null;
			}
			// ==liling 20140825 modify end ======
		} else {
			this.messageBox("E0010"); // ������ʾ�Ի��򣨡�û��ӡ�����ݡ���
			return;
		}
	}

	public void onPrintAuto(String rxNo, String rxPresrtNo) {
		// this.messageBox("666666");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// System.out.println("12======================="+sdf.format(SystemTool.getInstance().getDate()));
		TParm parm = onQuery(rxNo, rxPresrtNo);
		String Date = StringTool.getString(parm.getTimestamp("ORDER_DATE", 0), "yyyy/MM/dd"); // parm.getValue("ORDER_DATE",0);
		String INDT = parm.getValue("COUNTER_NO", 0);
		String INDNO = parm.getValue("PRINT_NO", 0);
		String Name = parm.getValue("PAT_NAME", 0);
		String Sex = parm.getValue("CHN_DESC", 0);
		String Age = parm.getValue("AGE", 0) + "��";
		String kb = parm.getValue("DEPT_CHN_DESC", 0);
		String Room = parm.getValue("USER_NAME", 0);
		String MR_NO = parm.getValue("MR_NO", 0);
		String checkuUser = parm.getValue("CHECK_USER", 0);
		String orderCat1 = "";
		if (orderType.equals("WD")) {
			orderCat1 = "'PHA_W','PHA_C'";
		} else
			orderCat1 = "'PHA_G'";

		// System.out.println("13======================="+sdf.format(SystemTool.getInstance().getDate()));
		TParm tparmPHA = getPHAParm(rxNo, orderCat1, rxPresrtNo);
		if (tparmPHA.getCount() <= 0) {
			messageBox("��ҩȷ�ϵ��޴�ӡ����");
			return;
		}

		// System.out.println("14======================="+sdf.format(SystemTool.getInstance().getDate()));
		// ���� E ����
		// F �������
		// I ���
		// O �ڷ�
		TParm eParm = new TParm();
		TParm fiParm = new TParm();
		TParm oParm = new TParm();
		int ei = 0;
		int fii = 0;
		int oi = 0;
		for (int i = 0; i < tparmPHA.getCount("GOODS_DESC"); i++) {
			TParm rowParm = tparmPHA.getRow(i);
			String doseType = tparmPHA.getValue("DOSE_TYPE", i);
			if ("F".equals(doseType) || "I".equals(doseType)) {
				fiParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				fiParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				fiParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				fiParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				fiParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				fiParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				fiParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				fiParm.addData("QTY", rowParm.getValue("QTY"));
				if (fii == 0) {
					fii = rowParm.getInt("DOSAGE_PRN_TOT");
				}

			} else if ("E".equals(doseType)) {
				eParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				eParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				eParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				eParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				eParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				eParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				eParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				eParm.addData("QTY", rowParm.getValue("QTY"));
				if (ei == 0) {
					ei = rowParm.getInt("DOSAGE_PRN_TOT");
				}
			} else if ("O".equals(doseType)) {
				oParm.addData("MIAN_FLG", rowParm.getValue("MIAN_FLG"));
				oParm.addData("LINK_NO", rowParm.getValue("LINK_NO"));
				oParm.addData("GOODS_DESC", rowParm.getValue("GOODS_DESC"));
				oParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
				oParm.addData("ROUTE_CODE", rowParm.getValue("ROUTE_CODE"));
				oParm.addData("FREQ_CODE", rowParm.getValue("FREQ_CODE"));
				oParm.addData("BATCH_NO", rowParm.getValue("BATCH_NO"));
				oParm.addData("QTY", rowParm.getValue("QTY"));
				if (oi == 0) {
					oi = rowParm.getInt("DOSAGE_PRN_TOT");
				}
			}
		}
		// System.out.println("15======================="+sdf.format(SystemTool.getInstance().getDate()));

		if (parm != null) {
			TParm date = null;
			if (fiParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, fiParm);
				date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");
				// ���ô�ӡ����
				// this.messageBox("date" + date);
				for (int i = 0; i < fii; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, fiParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, fiParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ����ס�");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			} else if (oParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, oParm);
				date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");
				// ���ô�ӡ����
				// this.messageBox("date" + date);
				for (int i = 0; i < oi; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, oParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, oParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ����ס�");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			} else if (eParm.getCount("GOODS_DESC") > 0) {
				date = getPringData(Date, INDT, INDNO, Name, Sex, Age, kb, Room, MR_NO, checkuUser, eParm);
				date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");
				for (int i = 0; i < ei; i++) {
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, eParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");//==liling
					// 20140826 modify
					this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
							IReportTool.getInstance().getReportParm("phaInd_V45.class", date), true);
					// date = null;//==liling 20140826 modify
					// date = getPringData(Date, INDT, INDNO, Name, Sex, Age,
					// kb,
					// Room, MR_NO, checkuUser, eParm);//==liling 20140826
					// modify
					// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ����ס�");//==liling
					// 20140826 modify
					// this.openPrintWindow(IReportTool.getInstance().getReportPath("phaInd_V45.jhw"),
					// IReportTool.getInstance().getReportParm("phaInd_V45.class",date),true);//==liling
					// 20140826 modify
					// date = null;//==liling 20140826 modify
				}
			}

		} else {
			this.messageBox("E0010"); // ������ʾ�Ի��򣨡�û��ӡ�����ݡ���
			return;
		}
	}

	private TParm getPringData(String Date, String INDT, String INDNO, String Name, String Sex, String Age, String kb,
			String Room, String MR_NO, String checkuUser, TParm tparmPHA) {
		TParm date = new TParm();
		tparmPHA.addData("SYSTEM", "COLUMNS", "MIAN_FLG");
		tparmPHA.addData("SYSTEM", "COLUMNS", "LINK_NO");
		tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
		tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
		tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
		tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
		tparmPHA.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		tparmPHA.setCount(tparmPHA.getCount("GOODS_DESC"));
		// ��ͷ���� MR_NO
		// date.setData("Title", "TEXT", "��(��)����ҩȷ�ϵ�");//==liling 20140825 add
		date.setData("INDT", "TEXT", "��ҩ̨��" + INDT);
		date.setData("INDNO", "TEXT", "��ҩ�ţ�" + INDNO);
		date.setData("Name", "TEXT", Name);
		date.setData("Sex", "TEXT", Sex);
		date.setData("Age", "TEXT", Age);
		date.setData("kb", "TEXT", kb);
		date.setData("Room", "TEXT", Room);
		date.setData("Date", "TEXT", Date);
		date.setData("MR_NO", "TEXT", MR_NO);
		date.setData("CHECK_USER", "TEXT", checkuUser);
		date.setData("table", tparmPHA.getData());
		return date;
	}

	public TParm onQuery(String rxNo, String rxPresrtNo) {
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = " SELECT A.CASE_NO AS CASE_NO,  A.COUNTER_NO AS COUNTER_NO,A.PRINT_NO AS PRINT_NO,B.Pat_Name AS Pat_Name,C.CHN_DESC  ,"
				+ "   FLOOR (MONTHS_BETWEEN (SYSDATE, B.BIRTH_DATE) / 12) AS AGE, E.DEPT_CHN_DESC AS  DEPT_CHN_DESC,D.USER_NAME AS USER_NAME, "
				+ "   A.ORDER_DATE AS ORDER_DATE,A.MR_NO AS MR_NO,F.USER_NAME AS CHECK_USER, "
				+ "   B.BIRTH_DATE AS BIRTH_AGE "// add by sunqy20140701 ȡ�ó�������
				+ "  FROM  OPD_ORDER A ,SYS_PATINFO B,SYS_DICTIONARY C,SYS_OPERATOR D,SYS_DEPT E,SYS_OPERATOR F"
				// + " WHERE A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'" //add by
				// huangtt 20150121
				+ "  WHERE A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' " // modify by wangjc 20190109
																							// ��������Ӧҩ���������
				+ "  AND B.MR_NO = A.MR_NO  AND C.ID = B.SEX_CODE" + "  AND C.GROUP_ID = 'SYS_SEX'"
				+ "  AND D.USER_ID = A.DR_CODE" + "  AND E.DEPT_CODE = A.DEPT_CODE"
				+ "  AND A.PHA_CHECK_CODE=F.USER_ID(+) " + "  AND A.ORDER_CAT1_CODE LIKE 'PHA%' "
				+ "  GROUP BY   A.COUNTER_NO, A.PRINT_NO, B.Pat_Name, C.CHN_DESC, B.BIRTH_DATE,"
				+ "  A.DEPT_CODE,E.DEPT_CHN_DESC,D.USER_NAME,A.ORDER_DATE,A.CASE_NO,A.MR_NO,F.USER_NAME ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println("��ҩ��:"+sql);
		return parm;
	}

	public TParm getPHAParm(String rxNo, String orderCat1, String rxPresrtNo) {
		String releaseFlg = "";
		if ("Dispense".equals(type)) {
			releaseFlg = " AND RELEASE_FLG = 'N' ";
		}
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = "SELECT CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '��' ELSE '' END MIAN_FLG, "
				+ "       A.LINK_NO, A.ORDER_DESC AS GOODS_DESC   , A.SPECIFICATION, "
				+ "       D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,REGEXP_REPLACE(A.DISPENSE_QTY,'^\\D','0.') || ' ' || B.UNIT_CHN_DESC QTY, "
				+ "       A.BATCH_NO  , CASE WHEN A.ATC_FLG = 'Y' THEN '��' ELSE ' ' END ATC_FLG ,A.DOSE_TYPE,D.DOSAGE_PRN_TOT  "
				+ "       ,A.RELEASE_FLG "// add by wangjc 20171107
				+ "FROM OPD_ORDER A,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D "
				+ "WHERE     A.DISPENSE_UNIT = B.UNIT_CODE " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.ORDER_CAT1_CODE IN (" + orderCat1 + ") "
				// + " AND A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'" //add by
				// huangtt 20150121
				+ "       AND A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' " // modify by wangjc 20190109
																							// ��������Ӧҩ���������
				+ releaseFlg // add by wangjc 20171107 ������ҩ������ӡ�Ա�ҩ
				+ "       ORDER BY LINK_NO, SEQ_NO";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm getPHAInfusion(String rxNo, String rxPresrtNo) {
		String presrtNo = rxPresrtNo.replace(rxNo, "");
		String sql = "SELECT CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '��' ELSE '' END MIAN_FLG, "
				+ "       A.LINK_NO, A.ORDER_DESC AS GOODS_DESC   , A.SPECIFICATION, "
				// modify by wangbin 20140806 ���bug��ҩΪ0.6g�Ŀڷ�ҩ��ӡҩǩ��ʾ.6
				+ "       D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,REGEXP_REPLACE(A.MEDI_QTY,'^\\D','0.') || ' ' || B.UNIT_CHN_DESC QTY,"
				+ " A.DISPENSE_QTY || ' ' || N.UNIT_CHN_DESC AS  DISPENSE_QTY, "
				+ "       A.BATCH_NO  , A.DOSE_TYPE,D.DOSAGE_PRN_TOT,A.MR_NO,E.USER_NAME DOSAGE_NAME ,F.USER_NAME DR_NAME ,"
				// ==liling 20140703 ���� ������ù��÷������� start==
				// + " A.MEDI_QTY || B.UNIT_CHN_DESC QQ,"//��ӿ�ҩ��add by
				// huangjw 20150309
				// +
				// " FLOOR(MONTHS_BETWEEN (SYSDATE, BIRTH_DATE)/12)||'��'|| "
				// +
				// " ABS(TO_NUMBER(EXTRACT(MONTH FROM SYSDATE)-EXTRACT(MONTH FROM
				// BIRTH_DATE)))||'��'|| "
				// +
				// " ABS(TO_NUMBER(EXTRACT(DAY FROM SYSDATE)-EXTRACT(DAY FROM BIRTH_DATE)))||'��'
				// AS AGE,"
				// ==liling 20140703 ���� ������ù��÷������� end==
				+ "		  G.PAT_NAME AS PAT_NAME ,H.CHN_DESC , I.DEPT_CHN_DESC,  "
				// +
				// " A.DOSAGE_QTY || ' ' || N.UNIT_CHN_DESC AS
				// DOSAGE_QTY,A.TAKE_DAYS,K.MATERIAL_LOC_CODE,M.ADM_DATE "
				+ "     A.DOSAGE_QTY/P.DOSAGE_QTY || ' ' || O.UNIT_CHN_DESC "
				+ " AS DOSAGE_QTY,A.TAKE_DAYS,K.MATERIAL_LOC_CODE,M.ADM_DATE,"
				+ " REGEXP_REPLACE(A.MEDI_QTY,'^\\D','0.')/REGEXP_REPLACE(P.MEDI_QTY,'^\\D','0.') AS DOSAGE_QTYNEW,"
				+ " N.UNIT_CHN_DESC  AS UNIT_CHN_DESCNEW ,MOD(A.DOSAGE_QTY,P.DOSAGE_QTY) AS MOD_DOSAGE"
				+ " ,MOD(A.MEDI_QTY,P.MEDI_QTY) AS MOD_MEDI,A.DOSAGE_QTY AS DOSAGE1,P.DOSAGE_QTY AS DOSAGE2,"
				+ " A.MEDI_QTY AS MEDI1,P.MEDI_QTY AS MEDI2,O.UNIT_CHN_DESC AS UNIT_STOCK,A.LINKMAIN_FLG ,S.DRUG_NOTES_PATIENT,A.INFLUTION_RATE "
				+ "FROM OPD_ORDER A,SYS_UNIT B,SYS_UNIT N,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_OPERATOR E,SYS_OPERATOR F ,SYS_PATINFO G,"
				+ " SYS_DICTIONARY H ,SYS_DEPT I, IND_STOCKM K ,REG_PATADM M,SYS_UNIT O,PHA_TRANSUNIT P,PHA_BASE S "
				+ "WHERE     A.MEDI_UNIT = B.UNIT_CODE " + "       AND A.DOSAGE_UNIT = N.UNIT_CODE "
				// fux modify 20151208
				+ "       AND P.STOCK_UNIT = O.UNIT_CODE " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.PHA_DOSAGE_CODE=E.USER_ID(+)"// cyl 2019.3.5
				+ "       AND A.DR_CODE=F.USER_ID " + "       AND A.CAT1_TYPE='PHA' "
				+ "       AND  G.MR_NO = A.MR_NO  " + "       AND H.ID = G.SEX_CODE" +
				// fux modify 20151008 ������λ
				" AND A.ORDER_CODE = K.ORDER_CODE(+)" + " AND A.EXEC_DEPT_CODE = K.ORG_CODE(+)"
				+ " AND A.CASE_NO = M.CASE_NO(+)  " + "       AND H.GROUP_ID = 'SYS_SEX'" +
				// yanmm 20170825 ����ҩƷ�ֵ䲡����ҩע�ⱸע
				"       AND A.ORDER_CODE = S.ORDER_CODE " + "      AND I.DEPT_CODE = A.DEPT_CODE"
				// fux modify 20151208
				+ "     AND A.ORDER_CODE = P.ORDER_CODE "
				// + " AND A.RX_NO||A.PRESRT_NO='" + rxPresrtNo+"'"; //add
				// by huangtt 20150121
				+ " AND A.RX_NO = '" + rxNo + "' AND A.PRESRT_NO='" + presrtNo + "' ";// modify by wangjc 20190109
																						// ��������Ӧҩ���������
		System.out.println("��ѯSQL����������������������������������������������������������������" + sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public void onPrintTCM() {
		String SQL = " SELECT CASE_NO,ADM_TYPE,MR_NO" + " FROM   OPD_ORDER" + " WHERE RX_NO = '" + nowOrdList.getRxNo()
				+ "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		if (parm.getCount() < 0) {
			messageBox("��ҩȷ�ϵ��޴�ӡ����");
			return;
		}
		// RegPatAdm regPatAdm = new RegPatAdm();
		// regPatAdm.setCaseNo(parm.getValue("CASE_NO",0));
		// regPatAdm.onQuery();
		// OpdOrder opdOrder = new OpdOrder();
		// opdOrder.setCaseNo(regPatAdm.getCaseNo());
		// opdOrder.setMrNo(regPatAdm.getMrNo());
		// opdOrder.setDeptCode(getTableSelectRowData("DEPT_CODE","Table_UP"));
		// opdOrder.setDrCode(getTableSelectRowData("DR_CODE","Table_UP"));
		// opdOrder.setAdmType(parm.getValue("ADM_TYPE",0));
		// opdOrder.onQuery();
		//
		// PatInfo patInfo = new PatInfo();
		// patInfo.setMrNo(regPatAdm.getMrNo());
		// patInfo.onQuery();
		// ---------------

		ODO odo = new ODO(parm.getValue("CASE_NO", 0), parm.getValue("MR_NO", 0),
				getTableSelectRowData("DEPT_CODE", "Table_UP"), getTableSelectRowData("DR_CODE", "Table_UP"),
				parm.getValue("ADM_TYPE", 0));

		odo.getOpdOrder().addEventListener(odo.getOpdOrder().ACTION_SET_ITEM, this, "onSetItemEvent");
		odo.onQuery();

		// ---------------
		/*
		 * TParm inParam=PHARxSheetTool.getInstance().getOrderPrintParm(
		 * getTableSelectRowData("DEPT_CODE","Table_UP"), "3", odo.getOpdOrder(),
		 * nowOrdList.getRxNo(),"",odo.getRegPatAdm(),odo.getPatInfo());
		 */
		TParm inParam = PHARxSheetTool.getInstance().getOrderPrintParm(getTableSelectRowData("DEPT_CODE", "Table_UP"),
				"3", odo, nowOrdList.getRxNo(), "");
		openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHAChnOrderSheet.jhw", inParam, true);
	}

	public void onInitUIHistory() {
		callFunction("UI|setMenuConfig", getConfigString("MENU." + type));
		callFunction("UI|onInitMenu");
		callFunction("UI|setTitle", "ҩ����ʷ��Ϣ��ѯ");
		callFunction("UI|" + PANEL_HEAD + "|addItem", PANEL_HEAD_NAME, getConfigString("PANEL_HEAD." + orderType), null,
				false);
		if (orderType.equals("DD"))
			callFunction("UI|" + PANEL_MIDDLE + "|addItem", PANEL_MIDDLE_NAME,
					getConfigString("PANEL_MIDDLE." + orderType), null, false);

		if ("WD".equalsIgnoreCase(orderType)) {
			TTable ta = ((TTable) this.getComponent("Table_UP"));
			callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));
			// ====================pangben modify 20110417 start ���������
			callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
			callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
					"1,left;2,left;3,left;4,right;5,right;6,right;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left");
			// ====================pangben modify 20110417 stop
			callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));

			callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14");
			callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
					"3,left;4,right;5,left;6,right;7,right;8,right;9,left;10,left;11,left;12,left;13,right;14,left");
		} else {
			callFunction("UI|Table_UP|setHeader", getConfigString("TABLE_UP." + orderType + "." + type));
			// ====================pangben modify 20110417 start ���������
			callFunction("UI|Table_UP|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
			callFunction("UI|Table_UP|setColumnHorizontalAlignmentData",
					"1,left;2,left;4,right;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left");
			// ====================pangben modify 20110417 stop
			callFunction("UI|Table_DOWN|setHeader", getConfigString("TABLE_DOWN." + orderType + "." + type));
			callFunction("UI|Table_DOWN|setLockColumns", "0,1,2,3,4,5,6,7,8,9,10,11");
			callFunction("UI|Table_DOWN|setColumnHorizontalAlignmentData",
					"0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left");
		}
		callFunction("UI|Table_UP|removeRowAll");
		callFunction("UI|Table_DOWN|removeRowAll");
	}

	public void onQueryHistory() {
		getTable("Table_UP").removeRowAll();
		getTable("Table_DOWN").removeRowAll();
		if (orderType.equals("WD"))
			onQueryHistoryWD();
		else
			onQueryHistoryDD();
	}

	public void onQueryHistoryWD() {
		String dateSQL = "";
		if (getValue("from_ORDER_DATE") != null && getValue("to_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQL = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQL = " AND TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                                               AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String dateSQLOPD = "";
		if (getValue("from_ORDER_DATE") != null && getValue("to_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_RETN_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("from_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                           AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("to_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String exeDeptSQL = "";
		if (getValueString("EXEC_DEPT_CODE").length() != 0)
			exeDeptSQL = " AND EXEC_DEPT_CODE = '" + getValueString("EXEC_DEPT_CODE") + "'";
		String rxNoSQL = "";
		if (getValueString("RX_NO").length() != 0)
			rxNoSQL = " AND RX_NO = '" + getValueString("RX_NO") + "'";
		String mrNoSQL = "";
		if (getValueString("MR_NO").length() != 0)
			mrNoSQL = " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		String patNameSQL = "";
		if (getValueString("PAT_NAME").length() != 0)
			patNameSQL = " AND PAT_NAME LIKE '%" + getValueString("PAT_NAME") + "%'";
		String deptSQL = "";
		if (getValueString("DEPT_CODE").length() != 0)
			deptSQL = " AND DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		String drSQL = "";
		if (getValueString("DR_CODE").length() != 0)
			drSQL = " AND DR_CODE = '" + getValueString("DR_CODE") + "'";
		String agencyOrgCodeSQL = "";
		if (getValueString("AGENCY_ORG_CODE").length() != 0)
			agencyOrgCodeSQL = " AND AGENCY_ORG_CODE = '" + getValueString("AGENCY_ORG_CODE") + "'";
		String prescriptNoSQL = "";
		if (getValueString("from_PRESCRIPT_NO").length() != 0 && getValueString("to_PRESCRIPT_NO").length() != 0)
			prescriptNoSQL = " AND PRINT_NO BETWEEN TO_NUMBER('" + getValueString("from_PRESCRIPT_NO") + "')"
					+ "              AND     TO_NUMBER('" + getValueString("to_PRESCRIPT_NO") + "')";
		String countNoSQL = "";
		if (getValueString("COUNTER_NO").length() != 0)
			countNoSQL = " AND COUNTER_NO = '" + getValueString("COUNTER_NO") + "'";
		String checkSQL = "";
		if (getValueString("CHECKBUTTON").equals("Y"))
			checkSQL = " AND PHA_CHECK_DATE IS NOT NULL";
		String dosgeSQL = "";
		if (getValueString("DOSAGEBUTTON").equals("Y"))
			dosgeSQL = " AND PHA_DOSAGE_DATE IS NOT NULL";
		String dispenseSQL = "";
		if (getValueString("DISPENSEBUTTON").equals("Y"))
			dispenseSQL = " AND PHA_DISPENSE_DATE IS NOT NULL";
		String returnSQL = "";
		if (getValueString("RETURNBUTTON").equals("Y"))
			returnSQL = " AND PHA_RETN_DATE IS NOT NULL";
		// ==================pangben modify 20110517 start
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (A.REGION_CODE= '" + this.getValueString("REGION_CODE")
					+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE='' ) ";
		// =======================pangben modify 20110517 stop
		String SQL = " SELECT CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END CTL_FLG," +
		// ======pangben modify 20110418
				"    REGION_CHN_DESC, RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ "        PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " FROM  PHA_ORDER_HISTORY A,SYS_PATINFO B, SYS_REGION C"
				+ " WHERE A.REGION_CODE = C.REGION_CODE AND A.MR_NO = B.MR_NO" + // ======pangben modify 20110418
																					// =======================pangben
																					// modify 20110405 start
																					// ��������ѯ����
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE IN ('W','C')" + dateSQL + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + prescriptNoSQL + countNoSQL
				+ " GROUP BY CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END,"
				+ "        REGION_CHN_DESC,  RX_NO,PAT_NAME,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,"
				+ "          PHA_RETN_DATE,PHA_DISPENSE_DATE,PHA_DOSAGE_DATE,"
				+ "          PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE" + " UNION"
				+ " SELECT CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END CTL_FLG,"
				+ "     REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE," + // ==pangben
																											// modify
																											// 20110418
				"        PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " FROM  OPD_ORDER A,SYS_PATINFO B ,SYS_REGION C"
				+ " WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // =====pangben modify 20110418
																				// =======================pangben modify
																				// 2011040 start
																				// ��������ѯ����
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE IN ('W','C')" + dateSQLOPD + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL
				+ drSQL + agencyOrgCodeSQL + prescriptNoSQL + countNoSQL + checkSQL + dosgeSQL + dispenseSQL + returnSQL
				+ " GROUP BY REGION_CHN_DESC,CASE WHEN RX_TYPE = '2' THEN 'Y' ELSE 'N' END,"
				+ "          RX_NO,PAT_NAME,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,"
				+ "          PHA_RETN_DATE,PHA_DISPENSE_DATE,PHA_DOSAGE_DATE,"
				+ "          PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,A.REGION_CODE"
				+ " ORDER BY REGION_CHN_DESC,RX_NO";
		// System.out.println("SQL:"+SQL);
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		clearNull(parm);
		// ==========pangben modify 20110418
		callFunction("UI|Table_UP|setParmValue", parm,
				"CTL_FLG;REGION_CHN_DESC;RX_NO;PAT_NAME;VARIETY;" + "SUM_FEE;PRINT_NO;MR_NO;DEPT_CODE;"
						+ "DR_CODE;PHA_RETN_DATE;PHA_DISPENSE_DATE;" + "PHA_DOSAGE_DATE;PHA_CHECK_DATE;EXEC_DEPT_CODE;"
						+ "COUNTER_NO;REGION_CODE");
	}

	public void onQueryHistoryDD() {
		String dateSQL = "";
		if (getValue("start_ORDER_DATE") != null && getValue("end_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQL = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQL = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQL = " AND TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                                               AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String dateSQLOPD = "";
		if (getValue("start_ORDER_DATE") != null && getValue("end_ORDER_DATE") != null) {
			if (getValueString("CHECKBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_CHECK_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                    AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DOSAGEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                     AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("DISPENSEBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_DISPENSE_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                       AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
			if (getValueString("RETURNBUTTON").equals("Y"))
				dateSQLOPD = " AND PHA_RETN_DATE BETWEEN TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("start_ORDER_DATE"), "yyyyMMddHHmmss")
						+ "','YYYYMMDDHH24MISS')" + "                           AND     TO_DATE('"
						+ StringTool.getString((Timestamp) getValue("end_ORDER_DATE"), "yyyyMMddHHmmss").substring(0, 8)
						+ "235959','YYYYMMDDHH24MISS')";
		}
		String exeDeptSQL = "";
		if (getValueString("EXEC_DEPT_CODE").length() != 0)
			exeDeptSQL = " AND EXEC_DEPT_CODE = '" + getValueString("EXEC_DEPT_CODE") + "'";
		String rxNoSQL = "";
		if (getValueString("RX_NO").length() != 0)
			rxNoSQL = " AND RX_NO = '" + getValueString("RX_NO") + "'";
		String mrNoSQL = "";
		if (getValueString("MR_NO").length() != 0)
			mrNoSQL = " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
		String patNameSQL = "";
		if (getValueString("PAT_NAME").length() != 0)
			patNameSQL = " AND PAT_NAME LIKE '%" + getValueString("PAT_NAME") + "%'";
		String deptSQL = "";
		if (getValueString("DEPT_CODE").length() != 0)
			deptSQL = " AND DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		String drSQL = "";
		if (getValueString("DR_CODE").length() != 0)
			drSQL = " AND DR_CODE = '" + getValueString("DR_CODE") + "'";
		String agencyOrgCodeSQL = "";
		if (getValueString("AGENCY_ORG_CODE").length() != 0)
			agencyOrgCodeSQL = " AND AGENCY_ORG_CODE = '" + getValueString("AGENCY_ORG_CODE") + "'";
		String decoctCodeSQL = "";
		if (getValueString("DECOCT_CODE").length() != 0)
			decoctCodeSQL = " AND DECOCT_CODE = '" + getValueString("DECOCT_CODE") + "'";
		String prescriptNoSQL = "";
		if (getValueString("from_PRESCRIPT_NO").length() != 0 && getValueString("to_PRESCRIPT_NO").length() != 0)
			prescriptNoSQL = " AND PRINT_NO BETWEEN TO_NUMBER('" + getValueString("from_PRESCRIPT_NO") + "')"
					+ "              AND     TO_NUMBER('" + getValueString("to_PRESCRIPT_NO") + "')";
		String countNoSQL = "";
		if (getValueString("COUNTER_NO").length() != 0)
			countNoSQL = " AND COUNTER_NO = '" + getValueString("COUNTER_NO") + "'";
		String checkSQL = "";
		if (getValueString("CHECKBUTTON").equals("Y"))
			checkSQL = " AND PHA_CHECK_DATE IS NOT NULL";
		String dosgeSQL = "";
		if (getValueString("DOSAGEBUTTON").equals("Y"))
			dosgeSQL = " AND PHA_DOSAGE_DATE IS NOT NULL";
		String dispenseSQL = "";
		if (getValueString("DISPENSEBUTTON").equals("Y"))
			dispenseSQL = " AND PHA_DISPENSE_DATE IS NOT NULL";
		String returnSQL = "";
		if (getValueString("RETURNBUTTON").equals("Y"))
			returnSQL = " AND PHA_RETN_DATE IS NOT NULL";
		// =======================pangben modify 20110517 start ��������ѯ����
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (A.REGION_CODE= '" + this.getValueString("REGION_CODE")
					+ "' OR A.REGION_CODE IS NULL OR A.REGION_CODE='' )";
		String SQL = " SELECT DCTAGENT_FLG,REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ // =======================pangben modify 20110418
				"        DECOCT_CODE,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,TO_DATE(PHA_RETN_DATE,'YYYYMMDDHH24MISS') PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "        TAKE_DAYS,SUM(MEDI_QTY) TOT_GRAM,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE REMARK,A.REGION_CODE"
				+ " FROM  PHA_ORDER_HISTORY A,SYS_PATINFO B,SYS_REGION C" + // =======================pangben
				// modify
				// 20110418
				" WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // =======================pangben modify
																				// 20110418
																				// =======================pangben modify
																				// 20110405 start
																				// ��������ѯ����
				region
				// =======================pangben modify 20110405 stop
				+ " AND PHA_TYPE = 'G'" + dateSQL + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + decoctCodeSQL + prescriptNoSQL + countNoSQL
				+ " GROUP BY REGION_CHN_DESC,DCTAGENT_FLG,RX_NO,PAT_NAME,DECOCT_CODE,PRINT_NO," + // ==========pangben
																									// modify 20110418
				"          A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "          PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "          TAKE_DAYS,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE,A.REGION_CODE" + " UNION"
				+ " SELECT DCTAGENT_FLG,REGION_CHN_DESC,RX_NO,PAT_NAME,COUNT(ORDER_CODE) VARIETY,SUM(OWN_AMT) SUM_FEE,"
				+ "        DECOCT_CODE,PRINT_NO,A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "        PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "        TAKE_DAYS,SUM(MEDI_QTY) TOT_GRAM,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE REMARK,A.REGION_CODE"
				+ " FROM  OPD_ORDER A,SYS_PATINFO B,SYS_REGION C" + // ==========pangben modify 20110418
				" WHERE A.REGION_CODE=C.REGION_CODE AND A.MR_NO = B.MR_NO" + // ==========pangben modify 20110418
																				// =======================pangben modify
																				// 20110405 start
																				// ��������ѯ����
				region +
				// =======================pangben modify 20110405 stop
				" AND   PHA_TYPE = 'G'" + dateSQLOPD + exeDeptSQL + rxNoSQL + mrNoSQL + patNameSQL + deptSQL + drSQL
				+ agencyOrgCodeSQL + decoctCodeSQL + prescriptNoSQL + countNoSQL + checkSQL + dosgeSQL + dispenseSQL
				+ returnSQL + " GROUP BY REGION_CHN_DESC,DCTAGENT_FLG,RX_NO,PAT_NAME,DECOCT_CODE,PRINT_NO,"
				+ "          A.MR_NO,DEPT_CODE,DR_CODE,PHA_RETN_DATE,PHA_DISPENSE_DATE,"
				+ "          PHA_DOSAGE_DATE,PHA_CHECK_DATE,EXEC_DEPT_CODE,COUNTER_NO,"
				+ "          TAKE_DAYS,DCT_TAKE_QTY,FREQ_CODE,ROUTE_CODE,DCTAGENT_CODE,DR_NOTE,A.REGION_CODE"
				+ " ORDER BY REGION_CHN_DESC,RX_NO";
		// System.out.println("SQL?::::::::::::::::::::::::"+SQL);
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		clearNull(parm);
		// =======================pangben modify 20110418
		callFunction("UI|Table_UP|setParmValue", parm,
				"DCTAGENT_FLG;REGION_CHN_DESC;RX_NO;PAT_NAME;" + "VARIETY;SUM_FEE;DECOCT_CODE;"
						+ "PRINT_NO;MR_NO;DEPT_CODE;DR_CODE;" + "PHA_RETN_DATE;PHA_DISPENSE_DATE;"
						+ "PHA_DOSAGE_DATE;PHA_CHECK_DATE;" + "EXEC_DEPT_CODE;COUNTER_NO;REGION_CODE");
	}

	private void clearNull(TParm parm) {
		String names[] = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < parm.getCount(names[i]); j++) {
				if (parm.getData(names[i], j) == null || parm.getValue(names[i], j).equalsIgnoreCase("null"))
					parm.setData(names[i], j, "");
			}
		}
	}

	public TParm getDownTableData() {
		String rxNo = getTable("Table_UP").getParmValue().getValue("RX_NO", getTable("Table_UP").getSelectedRow());
		// =======pangben modify 20110418 start ����±߱����������ͨ������ϱ߱����Ϣ��̬��������
		String regionCode = getTable("Table_UP").getParmValue().getValue("REGION_CODE",
				getTable("Table_UP").getSelectedRow());
		TParm parm = new TParm();
		String SQL = "";
		// =======================pangben modify 20110517 start ��������ѯ����
		String region = "";
		if (this.getValueString("REGION_CODE").length() > 0)
			region = " AND (REGION_CODE= '" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE ='') ";
		// =======================pangben modify 20110517 stop

		if (orderType.equals("WD")) {
			SQL = " SELECT ATC_FLG,LINKMAIN_FLG,''||LINK_NO LINK_NO,ORDER_DESC,"
					+ "        DISPENSE_QTY,DISPENSE_UNIT,OWN_PRICE,OWN_AMT,MEDI_QTY,"
					+ "        MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DR_NOTE REMARK,SEQ_NO"
					+ " FROM   PHA_ORDER_HISTORY" + " WHERE  RX_NO = '" + rxNo + "' " +
					// =======================pangben modify 20110405 start
					// ��������ѯ����
					region +
					// =======================pangben modify 20110405 stop
					" UNION" + " SELECT ATC_FLG,LINKMAIN_FLG,LINK_NO,ORDER_DESC ||"
					+ "                                     CASE WHEN TRIM(GOODS_DESC) IS NOT NULL OR TRIM(GOODS_DESC) <>''"
					+ "                                     THEN '(' || GOODS_DESC || ')'"
					+ "                                     ELSE '' END || "
					+ "                                     CASE WHEN TRIM(SPECIFICATION) IS NOT NULL OR TRIM(SPECIFICATION) <>''"
					+ "                                     THEN '(' || SPECIFICATION || ')'"
					+ "                                     ELSE ''"
					+ "                                     END ORDER_DESC,"
					+ "        DISPENSE_QTY,DISPENSE_UNIT,OWN_PRICE,OWN_AMT,MEDI_QTY,"
					+ "        MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,DR_NOTE REMARK,SEQ_NO" + " FROM   OPD_ORDER"
					+ " WHERE  RX_NO = '" + rxNo + "'" +
					// =======================pangben modify 20110405 start
					// ��������ѯ����
					region +
					// =======================pangben modify 20110405 stop
					" ORDER BY SEQ_NO";
		} else {
			SQL = " SELECT ORDER_DESC ORDER_CODE," + "        DOSAGE_QTY TOT_QTY,DCTEXCEP_CODE,SEQ_NO"
					+ (type.equals("Examine") ? ",RELEASE_FLG" : "")// add by
																	// wangjc
																	// ���ӱ�ҩ�����ʾ
					+ " FROM   PHA_ORDER_HISTORY" + " WHERE  RX_NO = '" + rxNo + "' " +
					// =======================pangben modify 20110405 start
					// ��������ѯ����
					region +
					// =======================pangben modify 20110405 stop
					" UNION" + " SELECT ORDER_DESC ||"
					+ "        CASE WHEN TRIM(GOODS_DESC) IS NOT NULL OR TRIM(GOODS_DESC) <>''"
					+ "        THEN '(' || GOODS_DESC || ')'" + "        ELSE '' END || "
					+ "        CASE WHEN TRIM(SPECIFICATION) IS NOT NULL OR TRIM(SPECIFICATION) <>''"
					+ "        THEN '(' || SPECIFICATION || ')'" + "        ELSE ''" + "        END ORDER_CODE,"
					+ "        DOSAGE_QTY TOT_QTY,DCTEXCEP_CODE,SEQ_NO" + (type.equals("Examine") ? ",RELEASE_FLG" : "")// add
																														// by
																														// wangjc
																														// ���ӱ�ҩ�����ʾ
					+ " FROM   OPD_ORDER" + " WHERE  RX_NO = '" + rxNo + "'" +
					// =======================pangben modify 20110405 start
					// ��������ѯ����
					region +
					// =======================pangben modify 20110405 stop
					" ORDER BY SEQ_NO";
		}
		System.out.println("SQL?:11111:::::::::::::::::::::::" + SQL);
		parm = new TParm(TJDODBTool.getInstance().select(SQL));
		if (orderType.equals("DD")) {
			TParm parmFormat = new TParm();
			for (int i = 0; i < parm.getCount(); i = i + 4) {
				if (i < parm.getCount()) {
					parmFormat.addData("ORDER_CODE1", parm.getValue("ORDER_CODE", i));
					parmFormat.addData("TOT_QTY1", parm.getValue("TOT_QTY", i));
					parmFormat.addData("DCTEXCEP_CODE1", parm.getValue("DCTEXCEP_CODE", i));
					parmFormat.addData("RELEASE_FLG1", parm.getValue("RELEASE_FLG", i));// add by wangjc
																						// ���ӱ�ҩ�����ʾ
				} else {
					parmFormat.addData("ORDER_CODE1", "");
					parmFormat.addData("TOT_QTY1", "");
					parmFormat.addData("DCTEXCEP_CODE1", "");
					parmFormat.addData("RELEASE_FLG1", "");// add by wangjc
															// ���ӱ�ҩ�����ʾ
				}
				if (i + 1 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE2", parm.getValue("ORDER_CODE", i + 1));
					parmFormat.addData("TOT_QTY2", parm.getValue("TOT_QTY", i + 1));
					parmFormat.addData("DCTEXCEP_CODE2", parm.getValue("DCTEXCEP_CODE", i + 1));
					parmFormat.addData("RELEASE_FLG2", parm.getValue("RELEASE_FLG", i + 1));// add by
																							// wangjc
																							// ���ӱ�ҩ�����ʾ
				} else {
					parmFormat.addData("ORDER_CODE2", "");
					parmFormat.addData("TOT_QTY2", "");
					parmFormat.addData("DCTEXCEP_CODE2", "");
					parmFormat.addData("RELEASE_FLG2", "");// add by wangjc
															// ���ӱ�ҩ�����ʾ
				}
				if (i + 2 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE3", parm.getValue("ORDER_CODE", i + 2));
					parmFormat.addData("TOT_QTY3", parm.getValue("TOT_QTY", i + 2));
					parmFormat.addData("DCTEXCEP_CODE3", parm.getValue("DCTEXCEP_CODE", i + 2));
					parmFormat.addData("RELEASE_FLG3", parm.getValue("RELEASE_FLG", i + 2));// add by
																							// wangjc
																							// ���ӱ�ҩ�����ʾ
				} else {
					parmFormat.addData("ORDER_CODE3", "");
					parmFormat.addData("TOT_QTY3", "");
					parmFormat.addData("DCTEXCEP_CODE3", "");
					parmFormat.addData("RELEASE_FLG3", "");// add by wangjc
															// ���ӱ�ҩ�����ʾ
				}
				if (i + 3 < parm.getCount()) {
					parmFormat.addData("ORDER_CODE4", parm.getValue("ORDER_CODE", i + 3));
					parmFormat.addData("TOT_QTY4", parm.getValue("TOT_QTY", i + 3));
					parmFormat.addData("DCTEXCEP_CODE4", parm.getValue("DCTEXCEP_CODE", i + 3));
					parmFormat.addData("RELEASE_FLG4", parm.getValue("RELEASE_FLG", i + 3));// add by
																							// wangjc
																							// ���ӱ�ҩ�����ʾ
				} else {
					parmFormat.addData("ORDER_CODE4", "");
					parmFormat.addData("TOT_QTY4", "");
					parmFormat.addData("DCTEXCEP_CODE4", "");
					parmFormat.addData("RELEASE_FLG4", "");// add by wangjc
															// ���ӱ�ҩ�����ʾ
				}
			}
			parm = parmFormat;
		}
		System.out.println(parm);
		return parm;
	}

	public void onUpTableClick() {
		if (getTable("Table_UP").getSelectedRow() < 0)
			return;
		TParm orders = getDownTableData();
		TParm order = getTable("Table_UP").getParmValue();
		if (orderType.equals("WD")) {
			setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO;REGION_CODE", // =====pangben
					// modify
					// 20110418
					order, getTable("Table_UP").getSelectedRow());
			this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add
																				// by
																				// huangtt
																				// 20150120
																				// PRESRT_NO
			this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																						// huangtt
																						// 20150120
																						// PRESRT_NO
			callFunction("UI|Table_DOWN|setParmValue", orders,
					"ATC_FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;DISPENSE_QTY;DISPENSE_UNIT;OWN_PRICE;OWN_AMT;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;TAKE_DAYS;REMARK");
		} else {
			setValueForParm("EXEC_DEPT_CODE;RX_NO;MR_NO;PAT_NAME;DEPT_CODE;DR_CODE;COUNTER_NO;REGION_CODE", // =====pangben
					// modify
					// 20110418
					order, getTable("Table_UP").getSelectedRow());
			this.setValue("PRESRT_NO", data.getValue("PRESRT_NO", selectUPRow));// add
																				// by
																				// huangtt
																				// 20150120
																				// PRESRT_NO
			this.setValue("RX_PRESRT_NO", data.getValue("RX_PRESRT_NO", selectUPRow));// add by
																						// huangtt
																						// 20150120
																						// PRESRT_NO
			setValueForParm("TAKE_DAYS;TOT_GRAM;DCT_TAKE_QTY;FREQ_CODE;ROUTE_CODE;DCTAGENT_CODE;REMARK;SUM_FEE", order,
					getTable("Table_UP").getSelectedRow());
			callFunction("UI|Table_DOWN|setParmValue", orders,
					// "ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4");
					"ORDER_CODE1;TOT_QTY1;DCTEXCEP_CODE1;RELEASE_FLG1;ORDER_CODE2;TOT_QTY2;DCTEXCEP_CODE2;RELEASE_FLG2;ORDER_CODE3;TOT_QTY3;DCTEXCEP_CODE3;RELEASE_FLG3;ORDER_CODE4;TOT_QTY4;DCTEXCEP_CODE4;RELEASE_FLG4");
		}
	}

	private void setTotTakeDays() {
		if (getTable("Table_UP").getSelectedRow() < 0)
			return;
		TParm order = getTable("Table_UP").getParmValue();
		TParm orderRow = order.getRow(getTable("Table_UP").getSelectedRow());
		TParm takeDaysTPatm = new TParm(TJDODBTool.getInstance()
				.select("SELECT TAKE_DAYS FROM OPD_ORDER WHERE RX_NO = '" + orderRow.getValue("RX_NO") + "'"));
		setValue("TAKE_DAYS", takeDaysTPatm.getValue("TAKE_DAYS", 0));
	}

	/**
	 * ������ҩ�Զ����
	 * 
	 * @return boolean
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!PassTool.getInstance().init()) {
			return true;
		}
		String opdOrdercat = "";
		if (orderType.equals("WD")) {
			opdOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		} else {
			opdOrdercat = "A.ORDER_CAT1_CODE='PHA_G'";
			;
		}
		PassTool.getInstance().setPatientInfo(nowOrdList.getOrder(0).getCaseNo());
		PassTool.getInstance().setAllergenInfo(nowOrdList.getMrNo());
		PassTool.getInstance().setMedCond(nowOrdList.getOrder(0).getCaseNo());
		TParm parm = PassTool.getInstance().setRecipeInfoAuto(nowOrdList.getOrder(0).getCaseNo(), opdOrdercat);
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "��ҩƷʹ�ò�����,�Ƿ�浵?", "��Ϣ", JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	/**
	 * ���漶��
	 * 
	 * @param parm
	 *            TParm
	 * @return boolean
	 */
	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("RX_NO"); i++) {
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
	 * ��ѯҩƷ��Ϣ
	 */
	public void queryDrug() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		if (orderType.equals("WD")) {
			int row = getTable("Table_DOWN").getSelectedRow();
			if (row < 0) {
				return;
			}
			String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
			if (value == null || value.length() == 0) {
				return;
			}
			int conmmand = Integer.parseInt(value);
			if (conmmand != 6) {
				PassTool.getInstance().setQueryDrug(nowOrdList.getOrder(row).getOrderCode(), conmmand);

			} else {
				PassTool.getInstance().setWarnDrug1(nowOrdList.getRxNo(), "" + nowOrdList.getOrder(row).getSeqNo());
			}
		} else {
			int column = getTable("Table_DOWN").getSelectedColumn();
			int number = 0;
			if (column < 0) {
				return;
			}
			TParm parm = getTable("Table_DOWN").getParmValue();
			String ordercode;
			switch (column / 3) {
			case 0:
				ordercode = parm.getValue("ORDER_CODE1", 0);
				if (!ordercode.equals("")) {
					number = 0;
				}
				break;
			case 1:
				ordercode = parm.getValue("ORDER_CODE2", 0);
				if (!ordercode.equals("")) {
					number = 1;
				}
				break;
			case 2:
				ordercode = parm.getValue("ORDER_CODE3", 0);
				if (!ordercode.equals("")) {
					number = 2;
				}
				break;
			case 3:
				ordercode = parm.getValue("ORDER_CODE4", 0);
				if (!ordercode.equals("")) {
					number = 3;
				}
				break;
			default:
				break;
			}
			String value = (String) this.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
			if (value == null || value.length() == 0) {
				return;
			}
			int conmmand = Integer.parseInt(value);
			if (conmmand != 6) {
				PassTool.getInstance().setQueryDrug(nowOrdList.getOrder(number).getOrderCode(), conmmand);
			} else {
				PassTool.getInstance().setWarnDrug1(nowOrdList.getRxNo(), "" + nowOrdList.getOrder(number).getSeqNo());
			}

		}
	}

	/**
	 * �ֶ���������ҩ
	 */
	public void checkDrugHand() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		String opdOrdercat = "";
		if (orderType.equals("WD")) {
			opdOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		} else {
			opdOrdercat = "A.ORDER_CAT1_CODE='PHA_G'";
		}
		if (nowOrdList == null) {
			return;
		}
		PassTool.getInstance().setPatientInfo(nowOrdList.getOrder(0).getCaseNo());
		PassTool.getInstance().setAllergenInfo(nowOrdList.getMrNo());
		PassTool.getInstance().setMedCond(nowOrdList.getOrder(0).getCaseNo());
		TParm parm = PassTool.getInstance().setRecipeInfoHand(nowOrdList.getOrder(0).getCaseNo(), opdOrdercat);
		isWarn(parm);
	}

	public void onPasterSwab() {
		onPasterSwab("");
	}

	/**
	 * ��ӡҩǩ-������
	 * 
	 * @author yuhaibao
	 */
	public void onPasterSwab(String save) {
		if (((TTable) this.getComponent("Table_UP")).getSelectedRow() < 0) {
			messageBox("δѡ�д���!");
			return;
		}
		// ȡ�����ݣ����浽һ��P����

		// �������ҩ
		if ("WD".equals(orderType))
			// onPrintSwab(nowOrdList.getRxNo(),
			// save,(nowOrdList.getPresrtNo()==0?"":nowOrdList.getPresrtNo()+""));
			onPrintSwab(nowOrdList.getRxNo(), save, nowOrdList.getRxPresertNo());
		// else
		// onPrintTCM();
	}

	/**
	 * ��ӡ��ҩҩǩ-������
	 * 
	 * @author yuhaibao
	 * @param rxNo
	 */
	public void onPrintSwab(String rxNo, String save, String rxPresrtNo) {
		/*
		 * r TParm parm = onQuery(rxNo);
		 * 
		 * String orderCat1 = ""; if (orderType.equals("WD")) { orderCat1 =
		 * "'PHA_W','PHA_C'"; }
		 */
		if (caseNo == null) {
			TParm orders = nowOrdList.getParm(nowOrdList.PRIMARY);
			caseNo = orders.getValue("CASE_NO", 0);
		}
		// ��ѯ���ݣ�����У�飬 �����ֵ ���ܴ�ӡ��ûֵ ������
		TParm tparmPHA = getPHAInfusion(rxNo, rxPresrtNo);

		if (tparmPHA.getCount() <= 0) {
			messageBox("ҩǩ�޴�ӡ����");
			return;
		}
		// У�飬���tparmPHA.getCount() <= 0���� ��������δ��ɵ�״̬
		// ��֮ǰ��ȡֵ��P���ݣ��ù���������������Ҫ��ֵ���� һһ��Ӧ��
		String linkNo = tparmPHA.getValue("LINK_NO", 0);
		String mrNo = tparmPHA.getValue("MR_NO", 0);
		String dosageName = tparmPHA.getValue("DOSAGE_NAME", 0);
		String drName = tparmPHA.getValue("DR_NAME", 0);
		String sexCode = tparmPHA.getValue("CHN_DESC", 0);
		String patName = getValueString("PAT_NAME");
		// String age = tparmPHA.getValue("AGE",0);
		// Timestamp day = SystemTool.getInstance().getDate();
		// String age = DateUtil.showAge(pat.getBirthday(), day);//==liling
		// 20140703 modify
		String birthdate = pat.getBirthday().toString();
		if (null != birthdate && birthdate.length() >= 10) {
			birthdate = birthdate.substring(0, 4) + "��" + birthdate.substring(5, 7) + "��" + birthdate.substring(8, 10)
					+ "��";
		}
		System.out.println("shengri=============" + birthdate);
		String deptName = tparmPHA.getValue("DEPT_CHN_DESC", 0);
		// fux modify 20151008

		boolean flg = false;
		for (int i = 0; i < tparmPHA.getCount(); i++) {
			String doseType = tparmPHA.getValue("DOSE_TYPE", i);
			if ("O".equals(doseType) || "E".equals(doseType)) {
				flg = true;
				break;
			}

		}
		if (flg) {

			for (int i = 0; i < tparmPHA.getCount(); i++) {
				TParm date = new TParm();

				String doseType = tparmPHA.getValue("DOSE_TYPE", i);
				String doseTypeChn = "";
				String doseChn = "����";
				// ROUTE_CODE
				doseType = tparmPHA.getValue("ROUTE_CODE", i);
				// if("O".equals(doseType)){
				// doseTypeChn = "�ڷ�";
				//
				// }else{
				// doseTypeChn = "����";
				// }
				doseTypeChn = doseType;
				// TParm newTParm = new TParm();
				// fux modify 20151009
				// ��λ GOODS_DESC
				// ���
				// ���� ����ҩ����
				// �÷� �ڷ������ã� ҩ

				// date.addData("GOODS_DESC", tparmPHA.getData("GOODS_DESC",
				// i)+" "+tparmPHA.getData("SPECIFICATION",i));
				// date.addData("QTY", "");
				// date.addData("GOODS_DESC",
				// "�÷�: "+tparmPHA.getData("FREQ_CODE",
				// i)+" "+"ÿ��"+tparmPHA.getData("ROUTE_CODE",
				// i)+" "+tparmPHA.getData("QTY", i));
				// date.addData("QTY","");
				// newTParm.setCount(2);
				// newTParm.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
				// newTParm.addData("SYSTEM", "COLUMNS", "QTY");
				// date.setData("TABLE", newTParm.getData());

				// ��ͷ���� MR_NO
				date.setData("LINK_NO", "TEXT", linkNo);
				date.setData("DEPT_TYPE", "TEXT", "��" + doseTypeChn + "��");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);
				date.setData("BED_CODE", "TEXT", "");
				date.setData("DOSECHN", "TEXT", doseChn);
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT", sexCode);
				// date.setData("AGE", "TEXT",age);
				// fux modify 20151008
				date.setData("REGION", "TEXT", Operator.getHospitalCHNShortName());
				// yanmm 20170824
				// date.setData("REMARK","TEXT","��ҩǰ���Ķ�ҩƷ˵����");
				date.setData("BIRTHDATE", "TEXT", birthdate);
				date.setData("ADM_DATE", "TEXT", tparmPHA.getValue("ADM_DATE", i).substring(0, 10));
				date.setData("SEND_CODE", "TEXT", dosageName);
				date.setData("DOCTOR_CODE", "TEXT", drName);
				date.setData("MATERIAL_LOC_CODE", "TEXT", tparmPHA.getValue("MATERIAL_LOC_CODE", i).toString());
				date.setData("GOODS_DESC", "TEXT", tparmPHA.getData("GOODS_DESC", i).toString());
				date.setData("SPECIFICATION", "TEXT", "���: " + tparmPHA.getData("SPECIFICATION", i).toString());
				date.setData("DRUG_NOTES_PATIENT", "TEXT",
						"��ע: " + tparmPHA.getData("DRUG_NOTES_PATIENT", i).toString() + "");
				// System.out.println("����:"+tparmPHA.getData("MOD_DOSAGE",i).toString());
				// System.out.println("�÷�:"+tparmPHA.getData("MOD_MEDI",i).toString());
				if (tparmPHA.getData("MOD_DOSAGE", i).toString().equals("0")) {

					date.setData("DISPENSE_QTY", "TEXT", "����: " + tparmPHA.getData("DOSAGE_QTY", i).toString());
				} else {
					date.setData("DISPENSE_QTY", "TEXT", "����: " + tparmPHA.getData("DOSAGE1", i).toString() + "/"
							+ tparmPHA.getData("DOSAGE2", i).toString() + tparmPHA.getData("UNIT_STOCK", i).toString());
				}

				if (tparmPHA.getData("MOD_MEDI", i).toString().equals("0")) {
					date.setData("FREQ_CODE", "TEXT",
							"�÷�: " + tparmPHA.getData("FREQ_CODE", i).toString() + "  " + "" + "ÿ�� "
									+ tparmPHA.getData("QTY", i).toString() + " "
									+ tparmPHA.getData("TAKE_DAYS", i).toString() + "��");
				} else {
					date.setData("FREQ_CODE", "TEXT",
							"�÷�: " + tparmPHA.getData("FREQ_CODE", i).toString() + "  " + "" + "ÿ�� "
									+ tparmPHA.getData("QTY", i).toString() + " "
									+ tparmPHA.getData("TAKE_DAYS", i).toString() + "��");

				}

				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\PHAPersralnew.jhw", date, true);
			}
		} else {
			this.messageBox("�ǿڷ�/����ҩ���ܴ�ӡҩǩ!");
		}

	}

	/**
	 * 
	 */
	public void inflution(String rxNo) {
		String sql = "SELECT INFLUTION_RATE from opd_oder where  MIAN_FLG ='Y' and RX_NO='" + rxNo + "' and  ";

	}

	/**
	 * ��Һǩ
	 */
	public void onPrintInfusion() {
		// messageBox("tttt");
		// System.out.println("======================== print");
		String rxNo = getValueString("RX_NO");
		String rxPresrtNo = this.getValueString("RX_PRESRT_NO"); // add by
																	// huangtt
																	// 20150121
		TParm parm = getPHAInfusion(rxNo, rxPresrtNo);
		String linkNo = parm.getValue("LINK_NO", 0);
		String mrNo = parm.getValue("MR_NO", 0);
		String dosageName = parm.getValue("DOSAGE_NAME", 0);
		String drName = parm.getValue("DR_NAME", 0);
		String sexCode = parm.getValue("CHN_DESC", 0);
		String patName = getValueString("PAT_NAME");
		// String age = parm.getValue("AGE",0);
		Timestamp day = SystemTool.getInstance().getDate();
		String age = DateUtil.showAge(pat.getBirthday(), day);// ==liling
																// 20140703
																// modify
		String deptName = parm.getValue("DEPT_CHN_DESC", 0);
		// ��������
		String birthdate = pat.getBirthday().toString();
		if (null != birthdate && birthdate.length() >= 10) {
			birthdate = birthdate.substring(0, 4) + "��" + birthdate.substring(5, 7) + "��" + birthdate.substring(8, 10)
					+ "��";
		}

		boolean isInfusion = false;
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < parm.getCount(); i++) {
			linkNo = parm.getValue("LINK_NO", i);
			set.add(linkNo);
		}

		for (int i = 0; i < parm.getCount(); i++) {
			String doseType = parm.getValue("DOSE_TYPE", 0);
			if ("F".equals(doseType) || "I".equals(doseType)) {
				isInfusion = true;
				break;
			}
		}
		if (isInfusion) {
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				linkNo = (String) it.next();
				TParm tparmPHA = new TParm();
				TParm date = new TParm();
				int count = 0;
				// DecimalFormat df = new DecimalFormat("#####0.000");
				double rate = 0;
				boolean flg = true;
				for (int i = 0; i < parm.getCount(); i++) {
					String linkNoNew = parm.getValue("LINK_NO", i);
					if ("".equals(linkNoNew) && flg) {// ������
						tparmPHA.addData("GOODS_DESC",
								parm.getData("GOODS_DESC", i) + " " + parm.getData("SPECIFICATION", i));
						// tparmPHA.addData("SPECIFICATION",
						// parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("QTY", i));
						// tparmPHA.addData("INFLUTION_RATE",
						// parm.getDouble("INFLUTION_RATE", i));
						// tparmPHA.addData("ROUTE_CODE",
						// parm.getData("ROUTE_CODE", i));
						// tparmPHA.addData("FREQ_CODE",
						// parm.getData("FREQ_CODE", i));
						rate = parm.getDouble("INFLUTION_RATE", i);
						count++;
					} else if (linkNo.equals(linkNoNew)) {// ����
						tparmPHA.addData("GOODS_DESC",
								parm.getData("GOODS_DESC", i) + " " + parm.getData("SPECIFICATION", i));
						// tparmPHA.addData("SPECIFICATION",
						// parm.getData("SPECIFICATION",i));
						tparmPHA.addData("QTY", parm.getData("QTY", i));
						// if(parm.getBoolean("LINKMAIN_FLG", i)){
						// tparmPHA.addData("INFLUTION_RATE",
						// df.format(parm.getDouble("INFLUTION_RATE", i)));
						rate = parm.getDouble("INFLUTION_RATE", i);
						// }
						// tparmPHA.addData("ROUTE_CODE",
						// parm.getData("ROUTE_CODE", i));
						// tparmPHA.addData("FREQ_CODE",
						// parm.getData("FREQ_CODE", i));
						flg = false;
						count++;
					}
				}
				tparmPHA.addData("GOODS_DESC", "");
				tparmPHA.addData("QTY", "");
				// tparmPHA.addData("INFLUTION_RATE", "");

				tparmPHA.addData("GOODS_DESC", "��Һ���ʣ�" + rate + "ml/h");
				tparmPHA.addData("QTY", "");
				// tparmPHA.addData("INFLUTION_RATE", "");
				count += 2;
				tparmPHA.setCount(count);
				tparmPHA.addData("SYSTEM", "COLUMNS", "GOODS_DESC");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
				tparmPHA.addData("SYSTEM", "COLUMNS", "QTY");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "INFLUTION_RATE");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
				// tparmPHA.addData("SYSTEM", "COLUMNS", "FREQ_CODE");

				date.setData("TABLE", tparmPHA.getData());
				// ��ͷ���� MR_NO
				date.setData("LINK_NO", "TEXT", linkNo);
				date.setData("DEPT_TYPE", "TEXT", "���ע�䡿ҩ");
				date.setData("MR_NO", "TEXT", mrNo);
				date.setData("DEPT_CODE", "TEXT", deptName);
				date.setData("BED_CODE", "TEXT", "");
				date.setData("PAT_CODE", "TEXT", patName);
				date.setData("SEX_CODE", "TEXT", sexCode);
				date.setData("AGE", "TEXT", age);
				date.setData("ROUTE_CODE", "TEXT", parm.getData("ROUTE_CODE", 0));
				date.setData("FREQ_CODE", "TEXT", parm.getData("FREQ_CODE", 0));
				date.setData("BAR_CODE", "TEXT", getBarCode(rxNo, linkNo));

				date.setData("SEND_CODE", "TEXT", dosageName);
				date.setData("DOCTOR_CODE", "TEXT", drName);
				// add by wangb 2017/08/11 ��Һƿǩ���ӳ���������ʾ
				date.setData("BIRTHDATE", "TEXT", birthdate);

				this.openPrintWindow("%ROOT%\\config\\prt\\PHA\\phaInfusion.jhw", date, true);
			}
		} else {
			this.messageBox("����ҺҩƷ���ܴ�ӡ��Һ��ǩ!");
		}

	}

	/**
	 * ��ȡ����
	 * 
	 * @param orderNo
	 * @param linkNo
	 * @return
	 */
	private String getBarCode(String orderNo, String linkNo) {
		if ("".equals(linkNo)) {
			return "";
		}
		linkNo = "00".substring(0, 2 - linkNo.length()) + linkNo.trim();
		return orderNo + linkNo;
	}

	// =============== chenxi modify 20121009��Ӵ�ӡ��������
	/**
	 * ��ӡ����
	 * 
	 * @return Object
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		TParm regInfo = PatAdmTool.getInstance().getInfoForCaseNo(nowOrdList.getOrder(0).getCaseNo());
		TParm tableParm = table.getParmValue();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
		parm.setData("MR", "TEXT", "�����ţ�" + tableParm.getValue("MR_NO", selRow));
		// if (isEng) {
		// parm
		// .setData("HOSP_NAME", "TEXT", Operator
		// .getHospitalENGFullName());
		// } else {
		parm.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		// }
		parm.setData("DR_NAME", "TEXT", "ҽʦǩ��:" + OpdRxSheetTool.getInstance().GetRealRegDr(caseNo));
		parm.setData("REALDEPT_CODE", tableParm.getValue("DEPT_CODE", selRow));

		// 20171114 add by wangjc
		// ����ʷ
		if (OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)).equals("")) {// �жϼ���ʷ�Ƿ�Ϊ��
			parm.setData("familyHistory", "");
		} else {
			parm.setData("familyHistory",
					OpdRxSheetTool.getInstance().getRelatedHistory(tableParm.getValue("MR_NO", selRow)));
		}
		// ����ʷ
		if (OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)).equals("")) {// �жϼ���ʷ�Ƿ�Ϊ��
			parm.setData("pastHistory", "");
		} else {
			parm.setData("pastHistory",
					OpdRxSheetTool.getInstance().getPastHistory(tableParm.getValue("MR_NO", selRow)));
		}
		// ������Ѫʷ
		String opBloodHistory = OpdRxSheetTool.getInstance().getOpBloodHistory(tableParm.getValue("MR_NO", selRow));
		if (StringUtils.isEmpty(opBloodHistory)) {
			parm.setData("opBloodHistory", "");
		} else {
			parm.setData("opBloodHistory", opBloodHistory);
		}
		// ��ҩ���
		String medication = OpdRxSheetTool.getInstance().getMedication(caseNo);
		if (StringUtils.isEmpty(medication)) {
			parm.setData("medication", "");
		} else {
			parm.setData("medication", medication);
		}

		Object obj = new Object();
		if ("O".equals(regInfo.getValue("ADM_TYPE", 0))) {
			// obj = this.openPrintDialog(
			// "%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010.jhw", parm,
			// false);
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHACaseSheet.jhw", parm, false);
			// ����EMR���� beign
			// this.saveEMR(obj, "���ﲡ����¼", "EMR020001", "EMR02000106");
			// ����EMR���� end
		} else if ("E".equals(regInfo.getValue("ADM_TYPE", 0))) {
			// obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG.jhw",
			// parm, false);
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\PHA\\PHAEMG.jhw", parm, false);

		}

	}

	/**
	 * ҩǩ-������
	 * 
	 * @author yuhaibao
	 * @param rxNo
	 * @param orderCat1
	 * @return
	 */
	public TParm getPHAParmSwab(String rxNo, String caseNo, String save) {
		boolean nowFlag = (Boolean) this.callFunction("UI|PACKAGEDRUG|isSelected");
		String atc_flg = "";
		if (nowFlag) {
			atc_flg = "N";
		} else {
			atc_flg = "Y";
		}
		if (save.equals("SAVE")) {
			atc_flg = "N";
		}
		String SQL = " SELECT A.ORDER_DESC  ||'\r\n'||  " + " 			B.FREQ_CHN_DESC  " + " 	    || ' ' || 'ÿ��' ||  "
				+ " CASE" + " WHEN SUBSTR (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99'),"
				+ " LENGTH (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99'))," + " 1" + " ) = '.'"
				+ " THEN SUBSTR (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')," + " 1,"
				+ " LENGTH (TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')) - 1" + " )"
				+ " ELSE TO_CHAR (A.MEDI_QTY/E.MEDI_QTY, 'FM9999990.99')" + " END" + " || D.UNIT_CHN_DESC "
				+ "        AS SWABDATA " + " FROM OPD_ORDER A,SYS_PHAFREQ B,SYS_UNIT C ,SYS_UNIT D ,PHA_TRANSUNIT E "
				+ " WHERE A.CASE_NO='" + caseNo + "'  AND A.RX_NO ='" + rxNo + "' "
				+ " AND A.CAT1_TYPE='PHA'  AND A.FREQ_CODE=B.FREQ_CODE(+) AND A.MEDI_UNIT=C.UNIT_CODE(+)  "
				+ " AND A.ORDER_CODE=E.ORDER_CODE AND E.DOSAGE_UNIT=D.UNIT_CODE ";
		// System.out.println("---------ҩǩ-������:---sql: "+SQL);
		return new TParm(TJDODBTool.getInstance().select(SQL));
	}

	/**
	 * ��ѯ������Ϣ�Ƿ��HISͬ������
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @param mrNo
	 *            ������
	 * @return
	 */
	private boolean isHaveInfoFromHis(String startDate, String endDate, String rxNo, String mrNo) {
		// ��־λ��false û������ͬ����������Ҫ���ýӿڣ�TRUE��ѯ������ֱ��
		boolean isHave = false;
		// System.out.println("3928----------------sql:
		// "+SPCSQL.getCountOPdOrder(startDate,
		// endDate, rxNo, mrNo));
		TParm parm = new TParm(
				TJDODBTool.getInstance().select(SPCSQL.getCountOPdOrder(startDate, endDate, rxNo, mrNo)));
		if (null != parm || parm.getCount() > 0) {
			if (parm.getInt("COUNT", 0) > 0) {
				isHave = true;
			}
		}
		return isHave;
	}

	/**
	 * ͬ��HIS��ҩ��Ϣ
	 * 
	 * @param startDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��������
	 * @param rxNo
	 *            ������
	 * @param mrNo
	 *            ������
	 * @return
	 */
	private void synOPdOrder(String startDate, String endDate, String rxNo, String mrNo) {
		startDate = startDate.length() > 19 ? startDate.substring(0, 19) : startDate;
		endDate = endDate.length() > 10 ? endDate.substring(0, 10) + " 23:59:59" : endDate;
		// ��ѯ������Ϣ�Ƿ��HISͬ������ false û������ͬ����������Ҫ���ýӿڣ�TRUE��ѯ������ֱ��
		boolean flag = isHaveInfoFromHis(startDate, endDate, rxNo, mrNo);
		if (flag) {
			return;
		} else {
			// ���ú�̨��action
			TParm rxParm = new TParm();
			rxParm.setData("RX_NO", rxNo);
			rxParm.setData("START_DATE", startDate);
			rxParm.setData("END_DATE", endDate);
			TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction", "onSaveSpcRequest", rxParm);
			// SpcOpdOrderDtos dtos =
			// PHADosageWsImplService_Client.onSaveSpcRequest(rxNo,
			// startDate,endDate);
			// System.out.println("size:=" + dtos.getSpcOpdOrderDtos().size());
			/*
			 * System.out.println("getRxNo:=" + dtos.getSpcOpdOrderDtos().get(0).getRxNo());
			 * System.out.println("tSpcOpdOrderDtos:=" +
			 * dtos.getSpcOpdOrderDtos().get(0).getPatName());
			 * System.out.println("st__retur:=" +
			 * dtos.getSpcOpdOrderDtos().get(0).getSeqNo());
			 * System.out.println("onSaveSpcRequest.result=" + dtos);
			 */
			// SpcDaoImpl.getInstance().onSavePhaOrder(dtos);
		}
	}

	/**
	 * ���µ��ӱ�ǩ���
	 */
	private void updateStockQtyOfEleTag() {
		if (null != nowOrdList && nowOrdList.size() > 0) {
			for (int i = 0; i < nowOrdList.size(); i++) {
				Order nowOrder = nowOrdList.getOrder(i);
				String orderCode = nowOrder.getOrderCode();
				String orgCode = nowOrder.getExecDeptCode();
				String atcFlg = nowOrder.getAtcFlg();
				// System.out.println("-----���µ��ӱ�ǩ���-----orgCode:"+orgCode+",orderCode:"+orderCode+",atcFlg;"+atcFlg);
				if (true) {// if(StringUtils.equals("N", atcFlg)){//���ǰ�ҩʱ
					// �Ÿ���ҩƷ�Ŀ��,
					SPCTool.getInstance().sendEleTag(orgCode, orderCode, 1);
				}
			}
		}
	}

	// ======================================== chenxi 20130515 �ͺ�װ��ҩ�� ����201 ����
	/**
	 * chenxi 20130515 �ͺ�װ��ҩ�� ����201 ����
	 */
	public void onDispenseBoxMachine() {
		TParm result = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selectRow = table.getSelectedRow();
		if (selectRow < 0) {
			this.messageBox("ѡ�񴦷�");
			return;
		}
		// ============================== EXEC_DEPT_CODE ��װ��ҩ������
		if (onBoxFlg(table.getItemString(selectRow, "EXEC_DEPT_CODE")) && "Y".equals(Operator.getSpcFlg())) {

			// ============================= ��װ��ҩ������
			TParm parmMain = data.getRow(selectRow); // ѡ���еĴ�������
			parmMain.setData("DR_NAME", this.getDrDesc(parmMain.getValue("DR_CODE")));
			parmMain.setData("DEPT_DESC", this.getDeptDesc(parmMain.getValue("DEPT_CODE")));
			nowOrdList = pha.getCertainOrdListByRow(selectRow);
			TParm parmDetail = nowOrdList.getParm(nowOrdList.PRIMARY); // ���������Ӧ�Ĵ�����ϸ
			for (int i = 0; i < parmDetail.getCount(); i++) {
				TParm desc = getOrderData(parmDetail.getValue("ORDER_CODE", i)); // ȡ��ҩƷ����
				parmDetail.setData("ORDER_DESC", i,
						desc.getData("ORDER_DESC", 0) + "" + desc.getData("SPECIFICATION", 0));
				parmDetail.setData("MEDI_UNIT", i, this.getUnitDesc(parmDetail.getValue("MEDI_UNIT", i)));
				parmDetail.setData("DISPENSE_UNIT", i, this.getUnitDesc(parmDetail.getValue("DISPENSE_UNIT", i)));
				parmDetail.setData("ROUTE_CODE", i, this.getRouteDesc(parmDetail.getValue("ROUTE_CODE", i)));
				parmDetail.addData("FREQ_DESC",
						this.getFreqData(parmDetail.getValue("FREQ_CODE", i)).getValue("FREQ_CHN_DESC", 0));
				parmDetail.addData("FIRM_ID", this.getManDesc(desc.getValue("MAN_CODE", 0)));
				parmDetail.setData("CTZ1_CODE", i, this.getCtzDesc(parmDetail.getValue("CTZ1_CODE", i)));
				String boxFlg = nowOrdList.getOrder(i).getGiveboxFlg();
				parmDetail.addData("FLG", boxFlg);
				String timeCode = TXNewATCTool.getTimeLine(parmDetail.getValue("FREQ_CODE", i)); // ����ʱ����� 37
				parmDetail.addData("FREQ_DESC_DETAIL_CODE", timeCode);
				String timeDetail = TXNewATCTool.getTimeDetail(parmDetail.getValue("FREQ_CODE", i));// ����ʱ����ϸ 38
				parmDetail.addData("FREQ_DESC_DETAIL", timeDetail);
				parmDetail.addData("QTY_BUONE", getMediQty(parmDetail.getValue("ORDER_CODE", i)));// һƬ�ļ���
																									// (��ֵ)
				parmDetail.addData("BAR_CODE", getBarCode(parmDetail.getValue("ORDER_CODE", i)));// �õ�����������
			}
			// ���ñ�ͷ����
			TParm parmTitle = new TParm();
			parmTitle.setData("OPT_USER", Operator.getName());
			parmTitle.setData("OPT_CODE", Operator.getID());
			parmTitle.setData("OPT_TERM", Operator.getIP());
			parmTitle.setData("OPWINID", table.getItemString(selectRow, "COUNTER_NO")); // ���ں�
			parmTitle.setData("WAY", "201"); // 201 ����
			result.setData("TITLE", parmTitle);
			result.setData("DETAIL", parmDetail);
			result.setData("MAIN", parmMain);
			String inxml = XmlUtils.onCreateXmlDispense(result).toString();
			// System.out.println("xml�����Һ���201�������Ĳ���========"+inxml);
			ConsisServiceSoap_ConsisServiceSoap_Client client = new ConsisServiceSoap_ConsisServiceSoap_Client();
			String outxml = client.onTransConsisData(inxml); // ���ص�xml
			// System.out.println("out====���ص�xml��==201===="+outxml);
			if (outxml.equals("err")) {
				this.messageBox("webservices ���Ӵ���");
				return;
			}
			TParm returnParm = XmlUtils.createXmltoParm(outxml); // �����ص�xmlת��Ϊparm��0����ʧ�ܣ�1����ɹ�
			if (returnParm.getErrCode() == 1) {
				String sql = "UPDATE OPD_ORDER SET COUNTER_NO = '" + returnParm.getValue("MESSAGE") + "' "
						+ "     WHERE RX_NO  ='" + table.getItemString(selectRow, "RX_NO") + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					this.messageBox("���䴰�ں�ʧ��");
					return;
				}
				this.messageBox("�Ͱ�ҩ���ɹ�");
			} else {
				this.messageBox("�Ͱ�ҩ��ʧ��");
				return;
			}
		}
	}

	/**
	 * ֪ͨ��ҩ����ҩ������202����(��ҩ��ɣ�����203����)
	 */
	public boolean onSendBoxMachine() {
		TParm result = new TParm();
		TTable table = (TTable) this.getComponent("Table_UP");
		int selectRow = table.getSelectedRow();
		if (onBoxFlg(table.getItemString(selectRow, "EXEC_DEPT_CODE")) && "Y".equals(Operator.getSpcFlg())) {
			// ���ñ�ͷ����
			TParm parmTitle = new TParm();
			parmTitle.setData("OPT_USER", Operator.getName());
			parmTitle.setData("OPT_CODE", Operator.getID());
			parmTitle.setData("OPT_TERM", Operator.getIP());
			parmTitle.setData("OPWINID", table.getItemString(selectRow, "COUNTER_NO")); // ���ں�
			parmTitle.setData("WAY", WAY); // 202,����203 ��������ʱ�����������߼���ʱ���ڴ�������
			result.setData("TITLE", parmTitle);
			result.setData("MAIN", data.getRow(selectRow));
			String inxml = XmlUtils.onCreateXmlSend(result).toString();
			// System.out.println("xml�����Һ���202.203�������Ĳ���===11222====="+inxml);
			ConsisServiceSoap_ConsisServiceSoap_Client client = new ConsisServiceSoap_ConsisServiceSoap_Client();
			String outxml = client.onTransConsisData(inxml); // ���ص�xml
			// System.out.println("out====���ص�xml��=="+WAY+"===="+outxml);
			if (outxml.equals("err")) {
				this.messageBox("webservices ���Ӵ���");
				return false;
			}
			TParm returnParm = XmlUtils.createXmltoParm(outxml); // �����ص�xmlת��Ϊparm��0����ʧ�ܣ�1����ɹ�
			if (returnParm.getErrCode() == 1) {
				if (WAY.equals("202")) {
					// this.onPasterSwab("SAVE") ;
					this.messageBox("ִ�гɹ�");
					return true;
				}

				return true;
			} else {
				this.messageBox("�Ͱ�ҩ��ʧ��,ȷ���Ƿ�ҩ");
				return false;
			}
		} else if ("Y".equals(Operator.getSpcFlg()))
			this.messageBox("��ҩ���������ͺ�װ��ҩ��"); // shibl modify 2013/12/18 ע��
		return true;
	}

	// ��װ��ҩ������
	public boolean onBoxFlg(String deptCode) {
		String sql = "SELECT BOX_FLG FROM IND_ORG WHERE ORG_CODE = '" + deptCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return false;
		}
		if (result.getValue("BOX_FLG", 0).equals("N"))
			return false;
		return true;
	}

	/**
	 * ���ҩƷ���������˫������
	 * 
	 * @param rxNo
	 *            ===pangben 2013-5-15
	 */
	public void openInwCheckWindow(String rxNo) {
		// this.setValue("RX_NO", rxNo);
		onQuery();
		TTable table = (TTable) this.getComponent("Table_UP");// ������������ݣ����û������ֱ���Ƴ�����Ƶ�ǰ����ǩ
		if (table.getParmValue().getCount() <= 0) {
			this.messageBox("û�л������");
			// getLedMedRemoveRxNo();//modify by wangjc 20180103��������Ӧϵͳ����ע������ƹ���
		}

	}

	/**
	 * �������ʾ
	 */
	public void openLEDMEDUI() {
		Component com = (Component) getComponent();
		TParm parm = new TParm();
		// "PHAMAIN"
		//
		// System.out.println("----Operator.getDept()-----"+Operator.getDept());
		// �������ߵģ���¼���Ž���Ϣ
		parm.setData("PHA_CODE", Operator.getDept());
		//
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while ((com != null) && (!(com instanceof Frame)))
			com = com.getParent();
		this.ledMedUi = new LEDMEDUI((Frame) com, this, parm);
		this.ledMedUi.openWindow();
	}

	public boolean onClosing() {
		if ((this.orderType.equals("WD")) && (this.type.equals("Examine"))) {// &&"Y".equals(Operator.getSpcFlg()))
																				// {
																				// this.ledMedUi.close();//modify
																				// by
																				// wangjc
																				// 20180103��������Ӧϵͳ����ע������ƹ���
		}
		return true;
	}

	public void onSelStationListenerLed(TParm parm) {
		this.ledParm = parm;
	}

	public void onSel() {
		this.ledParm.runListener("onListenerLed", new Object[] { "PHAMAIN" });
	}

	/**
	 * ==pangben 2013-5-27
	 */
	public void getLedMedRemoveRxNo() {
		// if("Y".equals(Operator.getSpcFlg())) {
		TParm caseNoParm = new TParm();// =====pangben 2013-5-15 ������Ƴ�����
		// caseNoParm.setData("RX_NO", this.getValue("RX_NO"));
		if (ledMedUi != null) {
			ledMedUi.removeMessage(caseNoParm);
		}
		// }
	}

	// ====================== chenxi ����ͬ����Ϣ
	private void synOPdOrderSave(String startDate, String endDate, String rxNo, String mrNo) {
		startDate = startDate.length() > 19 ? startDate.substring(0, 19) : startDate;
		endDate = endDate.length() > 10 ? endDate.substring(0, 10) + " 23:59:59" : endDate;
		// ���ú�̨��action
		TParm rxParm = new TParm();
		rxParm.setData("RX_NO", rxNo);
		rxParm.setData("START_DATE", startDate);
		rxParm.setData("END_DATE", endDate);
		TParm result = TIOM_AppServer.executeAction("action.pha.PHAAction", "onSaveSpcRequest", rxParm);

	}

	private TParm getIndStockBatchNoBySkintest(String orderCode, String execDeptCode, double dosageQty) {
		TParm parm = new TParm();
		parm.setData("ORG_CODE", execDeptCode);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("DOSAGE_QTY", dosageQty);
		TParm result = INDTool.getInstance().onQueryStockQtyBySkintest(parm);
		return result;
	}

	/**
	 * ����ҽ��վ�ҽ��棬ҩƷ״̬��ѯ���� �������桢��ӡ���кš����Ӳ���������������ʹ�� ==========pangben 2014-3-13
	 * 
	 * @param flg
	 */
	private void setOdoAutEnabled(boolean flg) {
		if (type.equals("Return")) {
			if (this.getPopedem("odoAUT")) {// ҽ��Ȩ��
				this.callFunction("UI|save|setEnabled", flg);
				this.callFunction("UI|call|setEnabled", flg);
				this.callFunction("UI|elecCaseHistory|setEnabled", flg);
				this.callFunction("UI|returnDrugList|setEnabled", flg);
				this.setValue("DEPT_CODE", Operator.getDept());
				this.setValue("DR_CODE", Operator.getID());
				this.callFunction("UI|DEPT_CODE|setEnabled", flg);
				this.callFunction("UI|DR_CODE|setEnabled", flg);
				this.setTitle("ҩƷ״̬��ѯ");
			}
		}

	}

	public boolean onDownTableChangeValue(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();

		String colName = tNode.getTable().getParmMap(column);

		TParm parm = tNode.getTable().getParmValue();
		TTable tableDown = getTable("Table_DOWN");
		String orderCode = parm.getValue("ORDER_CODE", row);

		if (colName.equals("BATCH_NO")) {
			TParm skinOrder = OrderTool.getInstance().onQuerySkintestPhaBase(orderCode);

			String skintestFlg = skinOrder.getValue("SKINTEST_FLG", 0);

			if ("Y".equals(skintestFlg)) {
				nowOrdList = pha.getCertainOrdListByRow(selectUPRow);
				String value = (String) tNode.getValue();

				// this.messageBox(nowOrdList.getOrder(row).getOrderDesc()+"");
				nowOrdList.getOrder(row).setBatchNo(value);

			} else {
				this.messageBox("����Ƥ��ҩƷ��������д���ţ�");
				tableDown.setItem(row, "BATCH_NO", "");
				return true;
			}

		}
		return false;
	}

	public String getAllerg(String mrNo) {
		boolean aflag = false;
		TParm Aresult = ODOTool.getInstance().getAllergyData(mrNo);// ����ҩƷ
		StringBuffer buf = new StringBuffer();
		String allerg = "";
		if (Aresult.getCount() > 0) {
			for (int j = 0; j < Aresult.getCount(); j++) {
				buf.append(",").append(Aresult.getValue("ORDER_DESC", j)).append(" ")
						.append(Aresult.getValue("ALLERGY_NOTE", j));
			}
			aflag = true;
		}
		if (aflag) {
			allerg = buf.toString();
			allerg = "����ʷ:" + allerg.substring(1, allerg.length());
		}
		return allerg;
	}

	public static void main(String[] args) {

		String prtSql = " SELECT  A.LINK_NO, A.ORDER_DESC || CASE WHEN TRIM (A.GOODS_DESC) IS NOT NULL OR TRIM (A.GOODS_DESC) <> '' THEN '(' || A.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC,"
				+ " A.SPECIFICATION,D.ROUTE_CHN_DESC AS ROUTE_CODE,C.FREQ_CHN_DESC AS FREQ_CODE,A.DOSAGE_QTY || ' ' || B.UNIT_CHN_DESC DOSAGE_QTY, "
				+ " A.TAKE_DAYS,A.DISPENSE_QTY,A.OWN_AMT "
				+ " FROM OPD_ORDER A ,SYS_UNIT B,SYS_PHAFREQ C,SYS_PHAROUTE D  "
				+ " WHERE   A.DISPENSE_UNIT = B.UNIT_CODE  " + "       AND A.FREQ_CODE = C.FREQ_CODE "
				+ "       AND A.ROUTE_CODE = D.ROUTE_CODE " + "       AND A.RX_NO = '1'"
				+ " AND A.PHA_RETN_CODE IS NOT NULL ";
		// System.out.println("-------:"+prtSql);
	}

}
