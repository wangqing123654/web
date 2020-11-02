package com.javahis.ui.clp;

import com.dongyang.control.*;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TMenuItem;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.dongyang.ui.event.TTableEvent;
import jdo.clp.BscInfoTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.clp.ThrpyschdmTool;
import jdo.clp.PackTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.dongyang.util.TypeTool;
import jdo.opd.TotQtyTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.Vector;

import com.javahis.system.textFormat.TextFormatSYSPhaFreq;
import jdo.clp.ClpchkTypeTool;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.util.Compare;

import jdo.odo.Diagrec;
import jdo.sys.PatTool;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: ���������
 * </p>
 * 
 * <p>
 * Description: �ٴ�·����׼�趨
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Zhang jianguo 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangjg 2011.04.21
 * @version 1.0
 */
public class CLPBscInfoControl_bak extends TControl {
	/**
	 * ���췽��
	 */
	public CLPBscInfoControl_bak() {
	}

	/**
	 * ѡ��ؼ�
	 */
	// �ϴε�ѡ��ҳǩ����
	private int lastSelectedIndex = 0;
	private TTabbedPane tTabbedPane_0;
	// ����===pangben 2012-6-22
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	// ѡ���ҳǩ�ؼ�
	// private TPanel CLP01;
	// private TPanel CLP02;
	// private TPanel CLP03;
	// private TPanel CLP04;
	// private TPanel CLP05;
	// Ժ��/����
	private TTextField REGION_CODE;
	/**
	 * ��һ��ҳǩ�Ŀؼ� �ٴ�·�����
	 */
	// ¬��������Ϻ��������table
	private TTable diagnoseTable;
	private TTable operatorDiagnoseTable;
	// ·������
	private TTextField CLNCPATH_CODE;
	// ����˵��
	private TTextField CLNCPATH_CHN_DESC;
	// Ӣ��˵��
	private TTextField CLNCPATH_ENG_DESC;
	// ƴ��
	private TTextField PY1;
	// ������
	private TTextField PY2;
	// �����Ʊ�
	private TextFormatDept DEPT_CODE;
	// �ƶ�����
	private TTextFormat FRSTVRSN_DATE;
	// ����޶���
	private TTextFormat LASTVRSN_DATE;
	// �޶�����
	private TTextField MODIFY_TIMES;
	// �汾
	private TTextField VERSION;
	// ����·������
	private TTextArea ACPT_CODE;
	// ���·������
	private TTextArea EXIT_CODE;
	// ��׼סԺ����
	private TTextField STAYHOSP_DAYS;
	// ƽ��ҽ�Ʒ���
	private TTextField AVERAGECOST;
	// ��ע
	private TTextField DESCRIPTION;
	// �Ƿ�����
	private TCheckBox ACTIVE_FLG;
	// �ٴ�·����� CLP_BSCINFO
	private TTable CLP_BSCINFO;
	/**
	 * �ڶ���ҳǩ�Ŀؼ� ����ʱ��
	 */
	// ·����Ŀ
	private TTextFormat CLNCPATH_CODE01;
	// ʱ�̴���
	private TextFormatCLPDuration SCHD_CODE;
	// ��������
	private TTextField SCHD_DAY;
	// ��Ժ�ڼ���
	private TTextField SUSTAINED_DAYS;
	// ����ʱ�� CLP_THRPYSCHDM
	private TTable CLP_THRPYSCHDM;
	/**
	 * ������ҳǩ�Ŀؼ� ҽ���ײ�
	 */
	// ·����Ŀ
	private TTextFormat CLNCPATH_CODE02;
	// ʱ�̴���
	private TTextFormat SCHD_CODE01;
	// �ܽ��
	private TTextField FEES;
	// �汾
	private TTextField VERSION01;
	// ҽ���ײ� CLP_PACK
	private TTable CLP_PACK01;
	/**
	 * ���ĸ�ҳǩ�Ŀؼ� �ؼ������ײ�
	 */
	// ·����Ŀ
	private TTextFormat CLNCPATH_CODE03;
	// ʱ�̴���
	private TTextFormat SCHD_CODE02;
	// �汾
	private TTextField VERSION02;
	// �ؼ������װ� CLP_PACK
	private TTable CLP_PACK02;
	// ���ICD desc
	private TTextField diagnose_desc;
	// ����ICD desc
	private TTextField operation_diagnose_desc;
	// ���ICD
	private TTextField diagnose;
	// ����ICD desc
	private TTextField operation_diagnose;

	/**
	 * �����ҳǩ�Ŀؼ� ����ƻ�
	 */
	// ·����Ŀ
	private TTextFormat CLNCPATH_CODE04;
	// ʱ�̴���
	private TTextFormat SCHD_CODE03;
	// �汾
	private TTextField VERSION03;
	// ����ƻ� CLP_PACK
	private TTable CLP_PACK03;
	/** �������ڸ�ʽ������� */
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	/** CLP_BSCINFO */
	public static final String CLP_BSCINFO_PARMMAP = "REGION_CODE;CLNCPATH_CODE;CLNCPATH_CHN_DESC;CLNCPATH_ENG_DESC;PY1;ICD_DESC;OPE_DESC;PY2;DEPT_CODE;FRSTVRSN_DATE;LASTVRSN_DATE;MODIFY_TIMES;VERSION;ACPT_CODE;EXIT_CODE;STAYHOSP_DAYS;AVERAGECOST;DESCRIPTION;ACTIVE_FLG;VERSION";
	/** CLP_THRPYSCHDM */
	public static final String CLP_THRPYSCHDM_PARMMAP = "SCHD_DESC;SUSTAINED_DAYS;SCHD_DAY;SCHD_CODE";
	/** ն��table���� */
	public static final String DIAGNOSE_TABLE = "diagnoseTable";
	public static final String OPERATOR_DIAGNOSE_TABLE = "operatorDiagnoseTable";
	// ������¼
	private String regionCode = Operator.getRegion();
	private boolean udFlg = false;// Ƶ�δ����¼��ع���Ϣ���� ���ı�״̬

	/**
	 * ��ʼ������ ��ʼ����ѯȫ��
	 */
	@Override
	public void init() {
		super.init();
		// ��ȡȫ�������ؼ�
		getAllComponent();
		// ע������¼�
		initControler();
		// ��ӡ��ť������
		TMenuItem print = (TMenuItem) this.getComponent("print");
		print.setEnabled(false);
		// �汾���ɱ༭
		VERSION.setEditable(false);
		VERSION01.setEditable(false);
		VERSION02.setEditable(false);
		VERSION03.setEditable(false);
		// �Ƿ����ñ�ʶ���ɱ༭
		ACTIVE_FLG.setEnabled(false);
		// �����ٴ�·����� Ĭ�ϵ���
		if (CLP_BSCINFO.getSelectedRow() < 0) {
			FRSTVRSN_DATE.setValue(dateFormat.format(SystemTool.getInstance()
					.getDate()));
		}
		// ��ѯȫ���ٴ�·�����
		// ¬���޸�begin
		TParm slelectParm = new TParm();
		this.putBasicSysInfoIntoParm(slelectParm);
		TParm allBscInfos = BscInfoTool.getInstance().getAllBscInfos(
				slelectParm);
		// ¬���޸� end
		// ��ʾ��ǰ̨����
		CLP_BSCINFO.setParmValue(allBscInfos);
		// ����Ĭ���޸�����
		this.setValue("LASTVRSN_DATE", getDateStr("yyyy/MM/dd"));
		// ��Ϻ�������ϵ�Table��ʼ����ע������¼�
		initDiagTables();
	}

	/**
	 * ��Ϻ�������ϵ�Table��ʼ����ע������¼�
	 */
	private void initDiagTables() {
		TParm diagTableParm = new TParm();
		TParm operatorDiagTableParm = new TParm();
		this.diagnoseTable.setParmValue(diagTableParm);
		this.operatorDiagnoseTable.setParmValue(operatorDiagTableParm);
		// ����Ĭ����
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		// �󶨼����¼�
		diagnoseTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onDiagCreateEditComponent");
		this.operatorDiagnoseTable.addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onOperateDiagCreateEditComponent");
	}

	/**
	 * ����Ĭ����
	 * 
	 * @param tableName
	 *            String
	 */
	private void addDefaultRowForDiag(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (this.DIAGNOSE_TABLE.equals(tableName)) {
			if (tableParm.getCount("diagnose_desc_begin") > 0
					&& "".equals(tableParm.getValue("diagnose_desc_begin",
							(tableParm.getCount("diagnose_desc_begin") - 1))
							.trim())) {
				return;
			}
			// Ĭ�ϼ���һ��������
			tableParm.addData("diagnose_desc_begin", "");
			tableParm.addData("diagnose_desc_end", "");
			tableParm.addData("diagnose_icd_begin", "");
			tableParm.addData("diagnose_icd_end", "");
			tableParm.addData("diagnose_icd_type_begin", "");
			tableParm.addData("diagnose_icd_type_end", "");

		} else if (this.OPERATOR_DIAGNOSE_TABLE.equals(tableName)) {
			if (tableParm.getCount("operator_diagnose_desc_begin") > 0
					&& ""
							.equals(tableParm
									.getValue(
											"operator_diagnose_desc_begin",
											(tableParm
													.getCount("operator_diagnose_desc_begin") - 1)))) {
				return;
			}
			// ����ICD
			tableParm.addData("operator_diagnose_desc_begin", "");
			tableParm.addData("operator_diagnose_desc_end", "");
			tableParm.addData("operator_diagnose_icd_begin", "");
			tableParm.addData("operator_diagnose_icd_end", "");
		}
		table.setParmValue(tableParm);
	}

	/**
	 * �����ϵ�������
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onDiagCreateEditComponent(Component com, int row, int column) {
		if (column == 0 || column == 1) {
			TTextField textfield = (TTextField) com;
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			TParm parm = new TParm();
			// parm.setData("ICD_TYPE", wc);
			textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			String returmMethodName = "";
			if (column == 0) {
				returmMethodName = "popDiagTableReturnBegin";
			} else if (column == 1) {
				returmMethodName = "popDiagTableReturnEnd";
			}
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					returmMethodName);
		}
	}

	/**
	 * �������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagTableReturnBegin(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		String icdType = parm.getValue("ICD_TYPE");
		tableParm.setData("diagnose_desc_begin", selectedRow, icdDesc);
		tableParm.setData("diagnose_icd_begin", selectedRow, icdCode);
		tableParm.setData("diagnose_icd_type_begin", selectedRow, icdType);
		tableDiag.setParmValue(tableParm);
		// ����Ĭ����
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);

	}

	/**
	 * �������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagTableReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		String icdType = parm.getValue("ICD_TYPE");
		tableParm.setData("diagnose_desc_end", selectedRow, icdDesc);
		tableParm.setData("diagnose_icd_end", selectedRow, icdCode);
		tableParm.setData("diagnose_icd_type_end", selectedRow, icdType);
		tableDiag.setParmValue(tableParm);
		// ����Ĭ����
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
	}

	/**
	 * ���������ϵ�������
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onOperateDiagCreateEditComponent(Component com, int row,
			int column) {
		if (column == 0 || column == 1) {
			TTextField textfield = (TTextField) com;
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			TParm parm = new TParm();
			// parm.setData("ICD_TYPE", wc);
			textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSOpICD.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			String returmMethodName = "";
			if (column == 0) {
				returmMethodName = "popOperatorDiagTableReturnBegin";
			} else if (column == 1) {
				returmMethodName = "popOperatorDiagTableReturnEnd";
			}
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					returmMethodName);
		}
	}

	/**
	 * �������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagTableReturnBegin(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("OPERATION_ICD");
		String icdDesc = parm.getValue("OPT_CHN_DESC");
		tableParm.setData("operator_diagnose_desc_begin", selectedRow, icdDesc);
		tableParm.setData("operator_diagnose_icd_begin", selectedRow, icdCode);
		tableDiag.setParmValue(tableParm);
		// ����Ĭ����
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	/**
	 * �������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagTableReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		TTable tableDiag = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		TParm tableParm = tableDiag.getParmValue();
		int selectedRow = tableDiag.getSelectedRow();
		tableDiag.acceptText();
		// int rowNo = tableDiag.getSelectedRow();
		String icdCode = parm.getValue("OPERATION_ICD");
		String icdDesc = parm.getValue("OPT_CHN_DESC");
		tableParm.setData("operator_diagnose_desc_end", selectedRow, icdDesc);
		tableParm.setData("operator_diagnose_icd_end", selectedRow, icdCode);
		tableDiag.setParmValue(tableParm);
		// ����Ĭ����
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	/**
	 * ��ȡȫ�������ؼ�
	 */
	public void getAllComponent() {
		/**
		 * ѡ��ؼ�
		 */
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("tTabbedPane_0");
		// ѡ���ҳǩ�ؼ�
		// this.CLP01 = (TPanel)this.getComponent("CLP01");
		// this.CLP02 = (TPanel)this.getComponent("CLP02");
		// this.CLP03 = (TPanel)this.getComponent("CLP03");
		// this.CLP04 = (TPanel)this.getComponent("CLP04");
		// this.CLP05 = (TPanel)this.getComponent("CLP05");
		// ����/Ժ��
		this.REGION_CODE = (TTextField) this.getComponent("REGION_CODE");
		/**
		 * ��һ��ҳǩ�Ŀؼ� �ٴ�·����� CLP_BSCINFO
		 */
		// ����������Ϻ����
		this.diagnoseTable = (TTable) this.getComponent(this.DIAGNOSE_TABLE);
		this.operatorDiagnoseTable = (TTable) this
				.getComponent(this.OPERATOR_DIAGNOSE_TABLE);
		this.CLNCPATH_CODE = (TTextField) this.getComponent("CLNCPATH_CODE");
		this.CLNCPATH_CHN_DESC = (TTextField) this
				.getComponent("CLNCPATH_CHN_DESC");
		this.CLNCPATH_ENG_DESC = (TTextField) this
				.getComponent("CLNCPATH_ENG_DESC");
		this.PY1 = (TTextField) this.getComponent("PY1");
		this.PY2 = (TTextField) this.getComponent("PY2");
		this.DEPT_CODE = (TextFormatDept) this.getComponent("DEPT_CODE");
		this.FRSTVRSN_DATE = (TTextFormat) this.getComponent("FRSTVRSN_DATE");
		this.LASTVRSN_DATE = (TTextFormat) this.getComponent("LASTVRSN_DATE");
		this.MODIFY_TIMES = (TTextField) this.getComponent("MODIFY_TIMES");
		this.ACPT_CODE = (TTextArea) this.getComponent("ACPT_CODE");
		this.EXIT_CODE = (TTextArea) this.getComponent("EXIT_CODE");
		this.STAYHOSP_DAYS = (TTextField) this.getComponent("STAYHOSP_DAYS");
		this.AVERAGECOST = (TTextField) this.getComponent("AVERAGECOST");
		this.VERSION = (TTextField) this.getComponent("VERSION");
		this.DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		this.ACTIVE_FLG = (TCheckBox) this.getComponent("ACTIVE_FLG");
		this.CLP_BSCINFO = (TTable) this.getComponent("CLP_BSCINFO");
		this.diagnose = (TTextField) this.getComponent("diagnose");
		this.diagnose_desc = (TTextField) this.getComponent("diagnose_desc");
		this.operation_diagnose = (TTextField) this
				.getComponent("operation_diagnose");
		this.operation_diagnose_desc = (TTextField) this
				.getComponent("operation_diagnose_desc");
		/**
		 * �ڶ���ҳǩ�Ŀؼ� ����ʱ�� CLP_THRPYSCHDM
		 */
		this.SCHD_CODE = (TextFormatCLPDuration) this.getComponent("SCHD_CODE");
		this.CLNCPATH_CODE01 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE01");
		this.SCHD_DAY = (TTextField) this.getComponent("SCHD_DAY");
		this.SUSTAINED_DAYS = (TTextField) this.getComponent("SUSTAINED_DAYS");
		this.CLP_THRPYSCHDM = (TTable) this.getComponent("CLP_THRPYSCHDM");
		/**
		 * ������ҳǩ�Ŀؼ� ҽ���ײ� CLP_PACK
		 */
		this.CLNCPATH_CODE02 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE02");
		this.SCHD_CODE01 = (TTextFormat) this.getComponent("SCHD_CODE01");
		this.FEES = (TTextField) this.getComponent("FEES");
		this.VERSION01 = (TTextField) this.getComponent("VERSION01");
		this.CLP_PACK01 = (TTable) this.getComponent("CLP_PACK01");
		/**
		 * ���ĸ�ҳǩ�Ŀؼ� �ؼ������ײ� CLP_PACK
		 */
		this.CLNCPATH_CODE03 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE03");
		this.SCHD_CODE02 = (TTextFormat) this.getComponent("SCHD_CODE02");
		this.VERSION02 = (TTextField) this.getComponent("VERSION02");
		this.CLP_PACK02 = (TTable) this.getComponent("CLP_PACK02");
		/**
		 * �����ҳǩ�Ŀؼ� ����ƻ� CLP_PACK
		 */
		this.CLNCPATH_CODE04 = (TTextFormat) this
				.getComponent("CLNCPATH_CODE04");
		this.SCHD_CODE03 = (TTextFormat) this.getComponent("SCHD_CODE03");
		this.VERSION03 = (TTextField) this.getComponent("VERSION03");
		this.CLP_PACK03 = (TTable) this.getComponent("CLP_PACK03");
	}

	/**
	 * ע������¼�
	 */
	public void initControler() {
		// �ٴ�·�����
		callFunction("UI|CLP_BSCINFO|addEventListener", "CLP_BSCINFO->"
				+ TTableEvent.CLICKED, this, "onTableClickedForBscInfo");
		// ����ʱ��
		callFunction("UI|CLP_THRPYSCHDM|addEventListener", "CLP_THRPYSCHDM->"
				+ TTableEvent.CLICKED, this, "onTableClickedForThrpyschdm");
		// ҽ���ײ�
		callFunction("UI|CLP_PACK01|addEventListener", "CLP_PACK01->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack01");
		// �ؼ������ײ�
		callFunction("UI|CLP_PACK02|addEventListener", "CLP_PACK02->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack02");
		// ����ƻ�
		callFunction("UI|CLP_PACK03|addEventListener", "CLP_PACK03->"
				+ TTableEvent.CLICKED, this, "onTableClickedForPack03");
		// ���ICD
		getTextField("diagnose_desc")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// �������ICD���ܷ���ֵ����
		getTextField("diagnose_desc").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popDiagReturn");
		// ���ICD����
		getTextField("diagnose_desc_end")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// �������ICD�������ܷ���ֵ����
		getTextField("diagnose_desc_end").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popDiagReturnEnd");
		// ����ICD
		getTextField("operation_diagnose_desc")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ��������ICD���ܷ���ֵ����
		getTextField("operation_diagnose_desc").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popOperatorDiagReturn");
		// ����ICD end
		getTextField("operation_diagnose_desc_end")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ��������ICD end ���ܷ���ֵ����
		getTextField("operation_diagnose_desc_end").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popOperatorDiagReturnEnd");
		// //���ICD
		// callFunction("UI|diagnose_desc|addEventListener",
		// "diagnose_desc->" + TTextFieldEvent.KEY_RELEASED, this,
		// "popoDiagnose");
		// //����ICD
		// callFunction("UI|operation_diagnose_desc|addEventListener",
		// "operation_diagnose_desc->" + TTextFieldEvent.KEY_RELEASED, this,
		// "popoOperatorDiagnose");
		// ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
		CLP_PACK01.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK01");
		// ҽ���ײ� ���� ��λ �շ� �д���
		this.addEventListener("CLP_PACK01->" + TTableEvent.CHANGE_VALUE, this,
				"onTableChangeValueForPack01");
		// �ؼ�������Ŀ �ٴ�·����Ŀ ҽ������ �д���
		CLP_PACK02.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK02");
		// ����ƻ� �ٴ�·����Ŀ �������� �д���
		CLP_PACK03.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreatePACK03");
		// ¬������20110621 begin
		// ����TABLEֵ�ı����
		addEventListener("CLP_PACK01->" + TTableEvent.CHANGE_VALUE, this,
				"onChangeTableCLPPACK01Value");
		// ¬������20110621 end
		// ====pangben 2012-6-22
		addListener(CLP_BSCINFO);
		addListenerOne(CLP_PACK01);
	}

	/**
	 * �����ܼ�
	 */
	public void onCountFees() {
		double fees = 0.0;
		for (int i = 0; i < CLP_PACK01.getRowCount(); i++) {
			String fee = String.valueOf(CLP_PACK01.getItemData(i, "FEES"));
			if (checkInputString(fee)) {
				fees = fees + Double.parseDouble(fee);
			}
		}
		TTextField FEES = (TTextField) this.getComponent("FEES");
		FEES.setValue(String.valueOf(StringTool.round(fees, 2)));
	}

	/**
	 * ���Ӷ�CLP_BSCINFO�ļ���
	 * 
	 * @param row
	 *            �к�
	 */
	public void onTableClickedForBscInfo(int row) {
		if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		CLP_BSCINFO.acceptText();
		// ·�����벻�ɱ༭
		CLNCPATH_CODE.setEditable(false);
		// �汾���ɱ༭
		VERSION.setEditable(false);
		// �Ƿ����ñ�ʶ���ɱ༭
		ACTIVE_FLG.setEnabled(false);
		// ��׼סԺ�������ɱ༭
		STAYHOSP_DAYS.setEditable(false);
		/*
		 * ��ȡѡ�е����ݣ�����Щ�������õ���������ؼ���
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getParmValue().getData("REGION_CODE", row))));
		CLNCPATH_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CODE"))));
		// ============pangben 2012-6-12 start
		SCHD_CODE.setSqlFlg("Y");
		SCHD_CODE.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
		SCHD_CODE.onQuery();
		CLNCPATH_CHN_DESC.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CHN_DESC"))));
		CLNCPATH_ENG_DESC.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_ENG_DESC"))));
		PY1.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(row,
				"PY1"))));
		PY2.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(row,
				"PY2"))));
		DEPT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "DEPT_CODE"))));
		FRSTVRSN_DATE.setValue(nullToString(String.valueOf(dateFormat
				.format(CLP_BSCINFO.getItemData(row, "FRSTVRSN_DATE")))));
		LASTVRSN_DATE.setValue(nullToString(String.valueOf(dateFormat
				.format(CLP_BSCINFO.getItemData(row, "LASTVRSN_DATE")))));
		MODIFY_TIMES.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "MODIFY_TIMES"))));
		ACPT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "ACPT_CODE"))));
		EXIT_CODE.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "EXIT_CODE"))));
		STAYHOSP_DAYS.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "STAYHOSP_DAYS"))));
		AVERAGECOST.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "AVERAGECOST"))));
		VERSION.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "VERSION"))));
		DESCRIPTION.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "DESCRIPTION"))));
		ACTIVE_FLG.setValue(CLP_BSCINFO.getItemData(row, "ACTIVE_FLG"));
		diagnose.setValue(nullToString(String.valueOf(CLP_BSCINFO.getItemData(
				row, "ICD_CODE"))));
		this.diagnose_desc.setValue(nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "ICD_DESC"))));
		this.operation_diagnose.setValue(nullToString(String
				.valueOf(CLP_BSCINFO.getItemData(row, "OPE_ICD_CODE"))));
		this.operation_diagnose_desc.setValue(nullToString(String
				.valueOf(CLP_BSCINFO.getItemData(row, "OPE_DESC"))));
		// ���
		String clncPathCode = nullToString(String.valueOf(CLP_BSCINFO
				.getItemData(row, "CLNCPATH_CODE")));
		TParm selectTparm = new TParm();
		selectTparm.setData("CLNCPATH_CODE", clncPathCode);
		TParm resulticd = BscInfoTool.getInstance().selectDiagICDList(
				selectTparm);
		TParm newresulticd = new TParm();
		for (int i = 0; i < resulticd.getCount(); i++) {
			String[] names = resulticd.getNames();
			for (int j = 0; j < names.length; j++) {
				newresulticd.addData(names[j].toLowerCase(), resulticd
						.getValue(names[j], i));
			}
		}
		this.diagnoseTable.setParmValue(newresulticd);
		TParm resultopticd = BscInfoTool.getInstance().selectOptICDList(
				selectTparm);
		TParm newresultopticd = new TParm();
		for (int i = 0; i < resultopticd.getCount(); i++) {
			String[] names = resultopticd.getNames();
			for (int j = 0; j < names.length; j++) {
				newresultopticd.addData(names[j].toLowerCase(), resultopticd
						.getValue(names[j], i));
			}
		}
		this.operatorDiagnoseTable.setParmValue(newresultopticd);
		// ����Ĭ����
		addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
	}

	public void onClncpathCodeEdit() {
		String s1 = CLNCPATH_CODE.getValue();
		if (checkInputString(s1)) {
			s1 = s1.toUpperCase();
			CLNCPATH_CODE.setValue(s1);
			for (int i = 0; i < CLP_BSCINFO.getRowCount(); i++) {
				String s2 = String.valueOf(CLP_BSCINFO.getItemData(i,
						"CLNCPATH_CODE"));
				if (s1.equalsIgnoreCase(s2)) {
					CLP_BSCINFO.setSelectedRow(i);
					onTableClickedForBscInfo(i);
				}
			}
			// �����ٴ�·����� Ĭ�ϵ���
			if (CLP_BSCINFO.getSelectedRow() < 0) {
				FRSTVRSN_DATE.setValue(dateFormat.format(SystemTool
						.getInstance().getDate()));
			}
		}
	}

	/**
	 * ���Ӷ�CLP_THRPYSCHDM�ļ���
	 * 
	 * @param row
	 *            �к�
	 */
	public void onTableClickedForThrpyschdm(int row) {
		if (row < 0) {
			return;
		}
		// ��ȡ���ڱ༭״̬������
		CLP_THRPYSCHDM.acceptText();
		// ·�����벻�ɱ༭
		CLNCPATH_CODE01.setEnabled(false);
		// ʱ�̴��벻�ɱ༭
		SCHD_CODE.setEnabled(false);
		/*
		 * ��ȡѡ�е����ݣ�����Щ�������õ���������ؼ���
		 */
		String schdCode = CLP_THRPYSCHDM.getParmValue().getValue("SCHD_CODE",
				row);
		SCHD_CODE.setValue(nullToString(schdCode));
		SCHD_DAY.setValue(nullToString(String.valueOf(CLP_THRPYSCHDM
				.getItemData(row, "SCHD_DAY"))));
		SUSTAINED_DAYS.setValue(nullToString(String.valueOf(CLP_THRPYSCHDM
				.getItemData(row, "SUSTAINED_DAYS"))));
	}

	/**
	 * �����б����¼� �жϵ���ĵ�Ԫ���Ƿ���Ա༭
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack01(int row) {
		if (row < 0) {
			return;
		}
		// ���ð汾
		// VERSION01.setValue(String.valueOf(CLP_PACK01.getItemData(row,
		// "VERSION")));
		int column = CLP_PACK01.getSelectedColumn();
		String version = String.valueOf(CLP_PACK01.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** �������� **/
			// �ٴ�·����Ŀ��ҽ����ϢУ��
			String orderCode = String.valueOf(CLP_PACK01.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK01.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �в��ɱ༭
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// ҽ����� �ٴ�·����� ҽ������ �˲����ɱ༭
				if (column == 2 || column == 3 || column == 4 || column == 18) {
					setTableEnabled(CLP_PACK01, row, column);
				}
				// ������Ŀ���ɱ༭
				else {
					return;
				}
			} else {
				// ¬��ɾ��
				// column == 1 ||
				if (column == 7 || column == 12 || column == 13 || column == 14
						|| column == 22) {
					// ҽ����� ��λ ���� ���� �ܽ�� �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK01, row, column);
				}
			}
		} else {
			/** �޸����� **/
			// �ٴ�·����Ŀ��ҽ����ϢУ��
			String orderCode = String.valueOf(CLP_PACK01.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK01.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �п��Ա༭
			if (column == 0) {
				setTableEnabled(CLP_PACK01, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// column == 1 || ¬��ɾ��
				if (column == 3 || column == 4 || column == 18) {
					// ҽ����� �ٴ�·����� ҽ������ �˲����ɱ༭
					setTableEnabled(CLP_PACK01, row, column);
				} else {
					// ������Ŀ���ɱ༭
					return;
				}
			} else {
				// ¬��ɾ��column == 1 ||
				if (column == 3 || column == 4 || column == 5 || column == 7
						|| column == 12 || column == 13 || column == 14
						|| column == 22) {
					// ҽ����� �ٴ�·����� ҽ������ ��λ ���� �ܽ�� �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK01, row, column);
				}
			}
		}
	}

	/**
	 * �����¼�
	 */
	public void onTableClicked() {
		TParm parm = CLP_PACK01.getParmValue();
		String orderCode = parm.getValue("ORDER_CODE", CLP_PACK01
				.getSelectedRow());
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// System.out.println("=====sqlparm======"+sqlparm);
		// ״̬����ʾ
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR")); // chexi modified
		// DRUG_NOTES_DR
	}

	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK01(Component com, int row, int column) {
		if (row < 0) {
			return;
		}
		// ����ҽ������趨Ƶ��begin
		// setFreqSelectType(row);
		// ����ҽ������趨Ƶ��end
		// System.out.println(
		// "--------------------�༭��Ԫ��-----------------------------------");
		if (column == 3) {
			// �����ٴ�·����Ŀ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "Y");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeSysFeePopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack01");
		}
		if (column == 4) {
			// ����ҽ������ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// �ٴ�·����Ŀ
			String ordTypeCode = CLP_PACK01.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "Y");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			parm.setData("REGION_CODE", Operator.getRegion());
			//this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPSysFeePopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack01");
		}
	}

	// /**
	// * ����ҽ������ɸѡ��ͬ��Ƶ��-
	// * @param selectedRow int
	// */
	// private void setFreqSelectType(int selectedRow){
	// TTable tablePack1 = (TTable)this.getComponent("CLP_PACK01");
	// tablePack1.acceptText();
	// TParm tableParm = tablePack1.getParmValue();
	// TParm rowParm = tableParm.getRow(selectedRow);
	// String orderType = rowParm.getValue("ORDER_TYPE");
	// TextFormatSYSPhaFreq freq =
	// (TextFormatSYSPhaFreq)this.getComponent("FREQ_CODE");
	// String statFlg="Y";
	// if("ST".equals(orderType)){
	// statFlg="Y";
	// }else{
	// statFlg="N";
	// }
	// System.out.println("����ֵ��------------"+statFlg+"orderTYe--"+orderType);
	// freq.setStatFlg(statFlg);
	// freq.onQuery();
	// }
	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack01(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		//System.out.println("result:::::::::"+result);
		// double ownPriceSingle = result.getDouble("OWN_PRICE");// �Էѽ��
		// ״̬�����ҽ����Ϣ
		callFunction("UI|setSysStatus", result.getValue("ORDER_CODE")
				+ result.getValue("ORDER_DESC") + result.getValue("GOODS_DESC")
				+ result.getValue("DESCRIPTION")
				+ result.getValue("SPECIFICATION"));
		// ===========pangben 2012-6-25 start ����ҽ��������
		if ("Y".equals(result.getValue("ORDERSET_FLG"))) {
			double allOwnAmt = 0.00;
			TParm parmDetail = SYSOrderSetDetailTool.getInstance()
					.selectByOrderSetCode(result.getValue("ORDER_CODE"));
			for (int j = 0; j < parmDetail.getCount(); j++) {
				allOwnAmt = allOwnAmt + parmDetail.getDouble("OWN_PRICE", j)
						* parmDetail.getDouble("TOTQTY", j);
			}
			result.setData("OWN_PRICE", allOwnAmt);
		}
		// ===========pangben 2012-6-25 stop
		// �ٴ�·����Ŀ ����
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("TYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "Y", "").getCount();
			if (count <= 0) {
				this.messageBox("���ٴ�·����Ŀû��ҽ����Ϣ��������ѡ��!");
				// ����ٴ�·����Ŀ
				int row = CLP_PACK01.getSelectedRow();
				int column = CLP_PACK01.getSelectedColumn();
				CLP_PACK01.setItem(row, column, "");
				CLP_PACK01.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK01(result);
		}
		// ҽ������ ����
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "Y")
					.getCount();
			if (count <= 0) {
				this.messageBox("��ҽ���������κ�һ���ٴ�·����Ŀ��������ѡ��");
				// ���ҽ������
				int row = CLP_PACK01.getSelectedRow();
				int column = CLP_PACK01.getSelectedColumn();
				CLP_PACK01.setItem(row, column, "");
				CLP_PACK01.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK01(result);
		}

	}

	/**
	 * ҽ���ײ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK01(TParm parm) {
		/************************* ȫ����Ϣ *************************/
		CLP_PACK01.acceptText();
		// ��ȡ�к�
		int selRow = CLP_PACK01.getSelectedRow();
		// Ժ������
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// ������Ϣ
		TParm result = new TParm();
		// ҽ�������
		String cat1Type = parm.getValue("CAT1_TYPE");
		String ordTypeCode = parm.getValue("TYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* ȫ����Ϣ *************************/

		/************************* Ԥ����Ϣ *************************/
		// ѡ
		result.setData("SEL_FLG", false);
		// ҽ�����
		// String orderType = String.valueOf(CLP_PACK01.getItemData(selRow, 1));
		String orderType = "ST";// =====pangben 2012-6-19
		result.setData("ORDER_TYPE", orderType);
		/************************* Ԥ����Ϣ *************************/

		/************************* Ĭ����Ϣ *************************/
		// ���յ��ٴ�·����Ŀ��ҽ�����루��ѡ���ٴ�·����Ŀ������Ĭ��ҽ�������Σ�
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ҽ������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// ���� Ƶ�� ��λ ;�����÷���
			TParm phaResult = PackTool.getInstance()
					.getDefaultPHABaseInfo(parm);
			String dose = phaResult.getValue("MEDI_QTY", 0);// old:DEFAULT_TOTQTY
			String doseUnit = phaResult.getValue("MEDI_UNIT", 0);// old:DOSAGE_UNIT
			String freqCode = phaResult.getValue("FREQ_CODE", 0);
			String routeCode = phaResult.getValue("ROUTE_CODE", 0);
			String takeDays = phaResult.getValue("TAKE_DAYS", 0);
			if ("PHA".equals(cat1Type)) {
				/** ҩƷ��ҽ�� */
				// ����
				result.setData("DOSE", dose);
				// ��λ
				result.setData("DOSE_UNIT", doseUnit);
				// Ƶ��
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", freqCode);
				}
				// ;�����÷���
				result.setData("ROUT_CODE", routeCode);
				// �շ�
				if ("ST".equals(orderType)) {
					result.setData("DOSE_DAYS", "1");
				} else if ("UD".equals(orderType)) {
					result.setData("DOSE_DAYS", takeDays);
				}
				result.setData("MEDI_QTY", phaResult.getDouble("MEDI_QTY", 0));//��ҩ����
			} else {
				/** ��ҩƷ��ҽ���������� */
				// ����
				result.setData("DOSE", "1");
				// ��λ
				result.setData("DOSE_UNIT", parm.getValue("UNIT_CODE"));
				// Ƶ��
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", ".");
				}
				// ;�����÷���
				result.setData("ROUT_CODE", "");
				// �շ�
				result.setData("DOSE_DAYS", "1");
				result.setData("MEDI_QTY", "1");//��ҩ����
			}
			// �ڼ��� ====pangben 2012-6-19
			result.setData("START_DAY", "1");
			// ����
			result.setData("TOTAL", "1");
			// �Էѵ���
			result.setData("OWN_PRICE", parm.getValue("OWN_PRICE"));
			// �ܽ���ǰ��¼��
			result.setData("FEES", "1");
			// ҽ����ע �ֶ�����
			result.setData("NOTE", "");
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// ִ�п���
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// �˲����
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// ����
			result.setData("URGENT_FLG", "");
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", this.getValue("VERSION01"));
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			
			result.setData("DISPENSE_UNIT", phaResult.getDouble(
					"DISPENSE_UNIT", 0));// ��ҩ��λ
			result.setData("MEDI_UNIT", phaResult.getValue("MEDI_UNIT", 0));// 
			result.setData("TAKE_DAYS", result.getDouble("DOSE_DAYS"));// 
			result.setData("DOSAGE_QTY", phaResult.getDouble("DOSAGE_QTY", 0));// 
			result.setData("ACTIVE_FLG", "Y");// ����״̬
			/************************* ������Ϣ *************************/
			// ¬������
			// �����������ܼ�
			// =============pangben 2012-6-21 start ��ʾ���� ���
			addTotalAndTotalPrice(result);
			this.addExeDept(result);
			/************************* �������� *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			if (selRow >= CLP_PACK01.getRowCount() - 1) {
				insertNewRow(CLP_PACK01, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ�ҽ�����룬û�н��ܵ��ٴ�·����Ŀ���루��ѡ��ҽ�����ƣ�����ѯ�ٴ�·����Ŀ��
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// ����ҽ�������ѯ�ٴ�·����Ŀ����
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "Y");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ҽ������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// ���� Ƶ�� ��λ ;�����÷���
			TParm phaResult = PackTool.getInstance()
					.getDefaultPHABaseInfo(parm);
			String dose = phaResult.getValue("MEDI_QTY", 0);// DEFAULT_TOTQTY
			String doseUnit = phaResult.getValue("MEDI_UNIT", 0);// DOSAGE_UNIT
			String freqCode = phaResult.getValue("FREQ_CODE", 0);
			String routeCode = phaResult.getValue("ROUTE_CODE", 0);
			String takeDays = phaResult.getValue("TAKE_DAYS", 0);
			if ("PHA".equals(cat1Type)) {
				/** ҩƷ��ҽ�� */
				// ����
				result.setData("DOSE", dose);
				// ��λ
				result.setData("DOSE_UNIT", doseUnit);
				// Ƶ��
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", freqCode);
				}
				// ;�����÷���
				result.setData("ROUT_CODE", routeCode);
				// �շ�
				if ("ST".equals(orderType)) {
					result.setData("DOSE_DAYS", "1");
				} else if ("UD".equals(orderType)) {
					result.setData("DOSE_DAYS", takeDays);
				}
				result.setData("MEDI_QTY", phaResult.getDouble("MEDI_QTY", 0));// ��ҩ����
			} else {
				/** ��ҩƷ��ҽ���������� */
				// ����
				result.setData("DOSE", "1");
				// ��λ
				result.setData("DOSE_UNIT", parm.getValue("UNIT_CODE"));
				// Ƶ��
				if ("ST".equals(orderType)) {
					result.setData("FREQ_CODE", "STAT");
				} else if ("UD".equals(orderType)) {
					result.setData("FREQ_CODE", ".");
				}
				// ;�����÷���
				result.setData("ROUT_CODE", "");
				// �շ�
				result.setData("DOSE_DAYS", "1");
				result.setData("MEDI_QTY", "1");// ��ҩ����
			}
			// �ڼ��� ====pangben 2012-6-19
			result.setData("START_DAY", "1");
			// ����
			result.setData("TOTAL", "1");
			// �Էѵ���
			result.setData("OWN_PRICE", parm.getValue("OWN_PRICE"));
			// �ܽ���ǰ��¼��
			result.setData("FEES", parm.getValue("OWN_PRICE"));
			// ҽ����ע �ֶ�����
			result.setData("NOTE", "");
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// ִ�п���
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// �˲����
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// ����
			result.setData("URGENT_FLG", "");
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", this.getValue("VERSION01"));
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			
			result.setData("DISPENSE_UNIT", phaResult.getDouble(
					"DISPENSE_UNIT", 0));// ��ҩ��λ
			result.setData("MEDI_UNIT", phaResult.getValue("MEDI_UNIT", 0));// ��ҩ��λ
			result.setData("TAKE_DAYS", result.getDouble("DOSE_DAYS"));// �շ�
			result.setData("DOSAGE_QTY", phaResult.getDouble("DOSAGE_QTY", 0));// 
			result.setData("ACTIVE_FLG", "Y");// ����״̬
			// ¬������
			addTotalAndTotalPrice(result);
			addExeDept(result);
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			if (selRow >= CLP_PACK01.getRowCount() - 1) {
				insertNewRow(CLP_PACK01, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ��ٴ�·����Ŀ���룬û�н��յ�ҽ�����루��ѡ���ٴ�·����Ŀ��������Ĭ��ҽ�������Σ�
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// CAT1_TYPE
			result.setData("CAT1_TYPE", cat1Type);
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK01.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
			/************************* �������� *************************/

		}
		/************************* Ĭ����Ϣ *************************/
		// ¬������-20110630 begin
		TParm tableParm = CLP_PACK01.getParmValue();
		// ɾ��
		// tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		CLP_PACK01.setParmValue(tableParm, CLP_PACK01.getParmMap());
		// ¬������-20110630 end
	}

	/**
	 * �����ܼۺ��ܽ��
	 * 
	 * @param result
	 *            TParm
	 */
	private void addTotalAndTotalPrice(TParm result) {
		boolean isvalid = false;
		if (this.checkNullAndEmpty(result.getData("DOSE") + "")
				&& this.checkNullAndEmpty(result.getData("DOSE_UNIT") + "")
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("FREQ_CODE")))
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("DOSE_DAYS")))
				&& this.checkNullAndEmpty(String.valueOf(result
						.getData("ORDER_CODE")))) {
			isvalid = true;
		}
		if (!isvalid) {
			return;
		}
		// ==============pangben 2012-6-21 start ҩƷ�������� ���
		TParm totParm = TotQtyTool.getInstance().getTotQty(result);
		// result.setData("DOSE",totParm.getDouble("QTY"));
		// if (null != result.getValue("CAT1_TYPE")
		// && "PHA".equals(result.getValue("CAT1_TYPE"))) {
		// // result.setData("DOSE",result.getDouble("DOSAGE_QTY"));
		// result.setData("DOSE", result.getDouble("DOSE")
		// / result.getDouble("MEDI_QTY"));
		// }
//		TParm parm = getTotalAndFees(totParm.getDouble("QTY"), result
//				.getValue("DOSE_UNIT"), result.getValue("FREQ_CODE"), result
//				.getInt("DOSE_DAYS"),
//				getOwnPrice(result.getValue("ORDER_CODE")));
		// ���õ�ǰ��¼���������ܽ��
		result.setData("TOTAL", totParm.getDouble("QTY"));
		result.setData("FEES", totParm.getDouble("QTY")*result.getDouble("OWN_PRICE"));
	}

	/**
	 * ����ִ�п���
	 * 
	 * @param result
	 *            TParm
	 */
	private void addExeDept(TParm result) {
		boolean isvalid = false;
		if (this
				.checkNullAndEmpty(String.valueOf(result.getData("ORDER_CODE")))) {
			isvalid = true;
		}
		if (!isvalid) {
			return;
		}
		TParm selectParm = new TParm();
		selectParm.setData("ORDER_CODE", result.getValue("ORDER_CODE"));
		TParm resultData = PackTool.getInstance().getExecDeptWithOrderCode(
				selectParm);
		// ����ִ�п�����Ϣ
		String deptCode = resultData.getValue("EXEC_DEPT_CODE", 0);
		if (deptCode != null) {
			result.setData("RBORDER_DEPT_CODE", deptCode);
		}
	}

	/**
	 * �õ�Ĭ�ϲ������
	 * 
	 * @return String
	 */
	private String getDefaultCheckTypeCode() {
		TParm selectParm = new TParm();
		selectParm.setData("REGION_CODE", Operator.getRegion());
		TParm result = ClpchkTypeTool.getInstance().selectAllCheckCode(
				selectParm);
		if (result.getCount() > 0) {
			return result.getValue("CHKTYPE_CODE", 0);
		} else {
			return "";
		}

	}

	/**
	 * �����б����¼� �жϵ���ĵ�Ԫ���Ƿ���Ա༭
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack02(int row) {
		if (row < 0) {
			return;
		}
		// ���ð汾
		VERSION02.setValue(String.valueOf(CLP_PACK02
				.getItemData(row, "VERSION")));
		int column = CLP_PACK02.getSelectedColumn();
		String version = String.valueOf(CLP_PACK02.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** �������� **/
			// �ٴ�·����Ŀ���ؼ�������Ŀ��ϢУ��
			String orderCode = String.valueOf(CLP_PACK02.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK02.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �в��ɱ༭
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// ҽ����� �ٴ�·����� ҽ������ �˲����ɱ༭
				if (column == 1 || column == 2 || column == 3 || column == 8) {
					setTableEnabled(CLP_PACK02, row, column);
				}
				// ������Ŀ���ɱ༭
				else {
					return;
				}
			} else {
				// ¬��ɾ�� column == 1 ||
				if (column == 11) {
					// ҽ����� �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK02, row, column);
				}
			}
		} else {
			/** �޸����� **/
			// �ٴ�·����Ŀ��ҽ����ϢУ��
			String orderCode = String.valueOf(CLP_PACK02.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK02.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �п��Ա༭
			if (column == 0) {
				setTableEnabled(CLP_PACK02, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// ¬��ɾ�� column == 1 ||
				if (column == 2 || column == 3 || column == 8) {
					// ҽ����� �ٴ�·����� ҽ������ �˲����ɱ༭
					setTableEnabled(CLP_PACK02, row, column);
				} else {
					// ������Ŀ���ɱ༭
					return;
				}
			} else {
				// ¬��ɾ�� column == 1 ||
				if (column == 2 || column == 3 || column == 8 || column == 11) {
					// ҽ����� �ٴ�·����� ҽ������ �˲���� �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK02, row, column);
				}
			}
		}
	}

	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK02(Component com, int row, int column) {
		// int rowCount = CLP_PACK02.getRowCount();
		if (row < 0) {
			return;
		}
		if (column == 2) {
			// �����ٴ�·����Ŀ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "N");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeChkItemPopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack02");
		}
		if (column == 3) {
			// ����ҽ������ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// �ٴ�·����Ŀ
			String ordTypeCode = CLP_PACK02.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "N");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPChkItemPopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack02");
		}

	}

	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack02(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		// �ٴ�·����Ŀ ����
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("ORDTYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "N", "").getCount();
			if (count <= 0) {
				this.messageBox("���ٴ�·����Ŀû�йؼ�������Ŀ��Ϣ��������ѡ��!");
				// ����ٴ�·����Ŀ
				int row = CLP_PACK02.getSelectedRow();
				int column = CLP_PACK02.getSelectedColumn();
				CLP_PACK02.setItem(row, column, "");
				CLP_PACK02.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK02(result);
		}
		// ҽ������ ����
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "N")
					.getCount();
			if (count <= 0) {
				this.messageBox("�˹ؼ�������Ŀ�������κ�һ���ٴ�·����Ŀ��������ѡ��");
				// ���ҽ������
				int row = CLP_PACK02.getSelectedRow();
				int column = CLP_PACK02.getSelectedColumn();
				CLP_PACK02.setItem(row, column, "");
				CLP_PACK02.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK02(result);
		}
	}

	/**
	 * ҽ���ײ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK02(TParm parm) {
		/************************* ȫ����Ϣ *************************/
		CLP_PACK02.acceptText();
		// ��ȡ�к�
		int selRow = CLP_PACK02.getSelectedRow();
		// Ժ������
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// ������Ϣ
		TParm result = new TParm();
		String ordTypeCode = parm.getValue("ORDTYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* ȫ����Ϣ *************************/

		/************************* Ԥ����Ϣ *************************/
		// ѡ
		result.setData("SEL_FLG", false);
		// ҽ�����
		String orderType = String.valueOf(CLP_PACK02.getItemData(selRow, 1));
		result.setData("ORDER_TYPE", orderType);
		/************************* Ԥ����Ϣ *************************/
		/************************* Ĭ����Ϣ *************************/
		// ���յ��ٴ�·����Ŀ��ҽ�����루��ѡ���ٴ�·����Ŀ������Ĭ��ҽ�������Σ�
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ҽ������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			TParm basicResult = PackTool.getInstance().getCheckItemBasicInfo(
					orderCode);
			// ����
			// result.setData("DOSE", "1");
			result.setData("DOSE", basicResult.getValue("CLP_QTY", 0));
			// ��λ
			// result.setData("DOSE_UNIT", "");
			result.setData("DOSE_UNIT", basicResult.getValue("CLP_UNIT", 0));
			// Ƶ��
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", ".");
			}
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// �˲����
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			// result.setData("CHKTYPE_CODE", chkTypeCode);
			result
					.setData(
							"CHKTYPE_CODE",
							null == parm.getValue("CHKTYPE_CODE")
									|| parm.getValue("CHKTYPE_CODE").length() <= 0 ? chkTypeCode
									: parm.getValue("CHKTYPE_CODE"));
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", "");
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			if (selRow >= CLP_PACK02.getRowCount() - 1) {
				insertNewRow(CLP_PACK02, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ�ҽ�����룬û�н��ܵ��ٴ�·����Ŀ���루��ѡ��ҽ�����ƣ�����ѯ�ٴ�·����Ŀ��
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// ����ҽ�������ѯ�ٴ�·����Ŀ����
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "N");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ҽ������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			TParm basicResult = PackTool.getInstance().getCheckItemBasicInfo(
					orderCode);
			// ����
			// result.setData("DOSE", "1");
			result.setData("DOSE", basicResult.getValue("CLP_QTY", 0));
			// ��λ
			// result.setData("DOSE_UNIT", "");
			result.setData("DOSE_UNIT", basicResult.getValue("CLP_UNIT", 0));
			// Ƶ��
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", ".");
			}
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// �˲����
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			// result.setData("CHKTYPE_CODE", chkTypeCode);
			result
					.setData(
							"CHKTYPE_CODE",
							null == parm.getValue("CHKTYPE_CODE")
									|| parm.getValue("CHKTYPE_CODE").length() <= 0 ? chkTypeCode
									: parm.getValue("CHKTYPE_CODE"));
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", "");
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			if (selRow >= CLP_PACK02.getRowCount() - 1) {
				insertNewRow(CLP_PACK02, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ��ٴ�·����Ŀ���룬û�н��յ�ҽ�����루��ѡ���ٴ�·����Ŀ��������Ĭ��ҽ�������Σ�
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			// NOTE
			result.setData("NOTE", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK02.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
			/************************* �������� *************************/
		}
		/************************* Ĭ����Ϣ *************************/
		// ¬������-20110630 begin
		// TParm tableParm = CLP_PACK02.getParmValue();
		// tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		// CLP_PACK02.setParmValue(tableParm, CLP_PACK02.getParmMap());
		// //luhai 20110705 �������������ȷ��������begin
		// result.setData("CHKTYPE_CODE", parm.getValue("CHKTYPE_CODE"));
		// //luhai 20110705 �������������ȷ��������end

		// ¬������-20110630 end
	}

	/**
	 * �����б����¼� �жϵ���ĵ�Ԫ���Ƿ���Ա༭
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClickedForPack03(int row) {
		if (row < 0) {
			return;
		}
		// ���ð汾
		VERSION03.setValue(String.valueOf(CLP_PACK03
				.getItemData(row, "VERSION")));
		int column = CLP_PACK03.getSelectedColumn();
		String version = String.valueOf(CLP_PACK03.getItemData(row, "VERSION"));
		if (version == null || "".equals(version.trim())) {
			/** �������� **/
			// �ٴ�·����Ŀ��������ϢУ��
			String orderCode = String.valueOf(CLP_PACK03.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK03.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �в��ɱ༭
			if (column == 0) {
				return;
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				// ������� �ٴ�·����� �������� �˲����ɱ༭
				if (column == 1 || column == 2 || column == 3 || column == 9) {
					setTableEnabled(CLP_PACK03, row, column);
				}
				// ������Ŀ���ɱ༭
				else {
					return;
				}
			} else {
				if (column == 1 || column == 5 || column == 13) {
					// ������� ��λ �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK03, row, column);
				}
			}
		} else {
			/** �޸����� **/
			// �ٴ�·����Ŀ��������ϢУ��
			String orderCode = String.valueOf(CLP_PACK03.getItemData(row,
					"ORDER_CHN_DESC"));
			String ordTypeCode = String.valueOf(CLP_PACK03.getItemData(row,
					"TYPE_CHN_DESC"));
			// ѡ �п��Ա༭
			if (column == 0) {
				setTableEnabled(CLP_PACK03, row, column);
			} else if (!checkOrderCode(orderCode)
					|| !checkOrdTypeCode(ordTypeCode)) {
				if (column == 1 || column == 2 || column == 3 || column == 9) {
					// ������� �ٴ�·����� �������� �˲����ɱ༭
					setTableEnabled(CLP_PACK03, row, column);
				} else {
					// ������Ŀ���ɱ༭
					return;
				}
			} else {
				if (column == 1 || column == 2 || column == 3 || column == 9
						|| column == 5 || column == 12) {
					// ҽ����� �ٴ�·����� ҽ������ �˲���� ��λ �汾���ɱ༭
					return;
				} else {
					// ������Ŀ�ɱ༭
					setTableEnabled(CLP_PACK03, row, column);
				}
			}
		}
	}

	/**
	 * ҽ��ҳ��ı��䶯�¼���������
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onChangeTableCLPPACK01Value(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// �ж�Ƶ���Ƿ����Ҫ��
		int selectedColumn = node.getTable().getSelectedColumn();
		String freqCode = (String) node.getValue();
		boolean freqFlag = true;
		if (selectedColumn == 8) {
			TTable tablePack1 = (TTable) this.getComponent("CLP_PACK01");
			TParm tableParm = tablePack1.getParmValue();
			int selectedRow = node.getRow();
			TParm selectRowParm = tableParm.getRow(selectedRow);
			String orderType = selectRowParm.getValue("ORDER_TYPE");
			TParm parm = new TParm();
			parm.setData("FREQ_CODE", freqCode);
			parm.setData("STAT_FLG", orderType.equalsIgnoreCase("ST") ? "Y"
					: "N");
			TParm result = BscInfoTool.getInstance().checkFreq(parm);
			if (result.getCount() <= 0) {
				freqFlag = false;
			} else {
				freqFlag = true;
			}
			if (!freqFlag) {
				this.messageBox("Ƶ�β���Ӧ���ڸ�ҽ����");
				udFlg = false;// �ع� ������
			} else {
				udFlg = true;// ���ع�
			}

		}
		if (freqFlag) {
			return false; // ���ع�
		} else {
			return true; // �ع�
		}
	}

	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreatePACK03(Component com, int row, int column) {
		// int rowCount = CLP_PACK03.getRowCount();
		if (row < 0) {
			return;
		}
		if (column == 2) {
			// �����ٴ�·����Ŀ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "O");
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("2", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPOrdTypeNursOrderPopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack03");
		}
		if (column == 3) {
			// ����ҽ������ ��ȡҽ��Ĭ����Ϣ
			if (!(com instanceof TTextField)) {
				return;
			}
			TTextField textFilter = (TTextField) com;
			textFilter.onInit();
			// �ٴ�·����Ŀ
			String ordTypeCode = CLP_PACK03.getParmValue().getValue(
					"ORDTYPE_CODE", row);
			TParm parm = new TParm();
			parm.setData("ORDER_FLG", "O");
			parm.setData("ORDTYPE_CODE", ordTypeCode);
			this.putBasicSysInfoIntoParm(parm);
			textFilter.setPopupMenuParameter("3", getConfigParm().newConfig(
					"%ROOT%\\config\\clp\\CLPNursOrderPopup.x"), parm);
			// ������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popReturnForPack03");
		}
	}

	/**
	 * ҽ���ײ� �ٴ�·����Ŀ ҽ������ �д���
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturnForPack03(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// ����ת����TParm
		TParm result = (TParm) obj;
		// �ٴ�·����Ŀ ����
		if ("2".equals(tag)) {
			String ordTypeCode = result.getValue("ORDTYPE_CODE");
			int count = getOrderByOrdTypeCode(ordTypeCode, "O", "").getCount();
			if (count <= 0) {
				this.messageBox("���ٴ�·����Ŀû�л�����Ϣ��������ѡ��!");
				// ����ٴ�·����Ŀ
				int row = CLP_PACK03.getSelectedRow();
				int column = CLP_PACK03.getSelectedColumn();
				CLP_PACK03.setItem(row, column, "");
				CLP_PACK03.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK03(result);
		}
		// ҽ������ ����
		if ("3".equals(tag)) {
			int count = getOrdTypeCode(result.getValue("ORDER_CODE"), "O")
					.getCount();
			if (count <= 0) {
				this.messageBox("�˻����������κ�һ���ٴ�·����Ŀ��������ѡ��");
				// ���ҽ������
				int row = CLP_PACK03.getSelectedRow();
				int column = CLP_PACK03.getSelectedColumn();
				CLP_PACK03.setItem(row, column, "");
				CLP_PACK03.setValueAt("", row, column);
				return;
			}
			// ��������������ҽ����Ϣ
			setCLP_PACK03(result);
		}
	}

	/**
	 * ҽ���ײ�
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setCLP_PACK03(TParm parm) {
		/************************* ȫ����Ϣ *************************/
		CLP_PACK03.acceptText();
		// ��ȡ�к�
		int selRow = CLP_PACK03.getSelectedRow();
		// Ժ������
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		// ������Ϣ
		TParm result = new TParm();
		String ordTypeCode = parm.getValue("ORDTYPE_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String typeChnDesc = parm.getValue("TYPE_CHN_DESC");
		/************************* ȫ����Ϣ *************************/

		/************************* Ԥ����Ϣ *************************/
		// ѡ
		result.setData("SEL_FLG", false);
		// ҽ�����
		String orderType = String.valueOf(CLP_PACK03.getItemData(selRow, 1));
		result.setData("ORDER_TYPE", orderType);
		/************************* Ԥ����Ϣ *************************/

		/************************* Ĭ����Ϣ *************************/
		// ���յ��ٴ�·����Ŀ��ҽ�����루��ѡ���ٴ�·����Ŀ������Ĭ��ҽ�������Σ�
		if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ��������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// ����
			result.setData("DOSE", parm.getValue("AMOUNT"));
			// ��λ
			result.setData("DOSE_UNIT", parm.getValue("UNIT"));
			// Ƶ��
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", parm.getValue("FREQ"));
			}
			// ������ע �ֶ�����
			result.setData("NOTE", "");
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// ִ�п���
			result
					.setData("RBORDER_DEPT_CODE", parm
							.getValue("EXEC_DEPT_CODE"));
			// �˲����
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", "");
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			if (selRow >= CLP_PACK03.getRowCount() - 1) {
				insertNewRow(CLP_PACK03, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ�ҽ�����룬û�н��ܵ��ٴ�·����Ŀ���루��ѡ��ҽ�����ƣ�����ѯ�ٴ�·����Ŀ��
		else if ((!checkInputString(ordTypeCode) || !checkInputString(typeChnDesc))
				&& checkInputString(orderCode)) {
			// ����ҽ�������ѯ�ٴ�·����Ŀ����
			TParm ordTypeCodeResult = getOrdTypeCode(orderCode, "O");
			ordTypeCode = ordTypeCodeResult.getValue("ORDTYPE_CODE", 0);
			typeChnDesc = ordTypeCodeResult.getValue("TYPE_CHN_DESC", 0);
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			// ��������
			String orderDesc = parm.getValue("ORDER_CHN_DESC");
			result.setData("ORDER_CHN_DESC", orderDesc);
			// ����
			result.setData("DOSE", parm.getValue("AMOUNT"));
			// ��λ
			result.setData("DOSE_UNIT", parm.getValue("UNIT"));
			// Ƶ��
			if ("ST".equals(orderType)) {
				result.setData("FREQ_CODE", "STAT");
			} else if ("UD".equals(orderType)) {
				result.setData("FREQ_CODE", parm.getValue("FREQ"));
			}
			// ������ע �ֶ�����
			result.setData("NOTE", "");
			// ִ����Ա
			result.setData("CHKUSER_CODE", "001");
			// �˲����
			parm.setData("ORDER_CAT1_CODE", parm.getValue("CHKTYPE_CODE"));
			TParm chkTypeResult = PackTool.getInstance()
					.getDefaultChkTypeByOrderCat1(parm);
			String chkTypeCode = chkTypeResult.getValue("CHKTYPE_CODE", 0);
			result.setData("CHKTYPE_CODE", chkTypeCode);
			// ��ִ
			result.setData("EXEC_FLG", "");
			// �����׼��
			result.setData("STANDARD", "");
			// �汾
			result.setData("VERSION", "");
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			if (selRow >= CLP_PACK03.getRowCount() - 1) {
				insertNewRow(CLP_PACK03, selRow);
			}
			/************************* �������� *************************/
		}
		// ���յ��ٴ�·����Ŀ���룬û�н��յ�ҽ�����루��ѡ���ٴ�·����Ŀ��������Ĭ��ҽ�������Σ�
		else if ((checkInputString(ordTypeCode) && checkInputString(typeChnDesc))
				&& !checkInputString(orderCode)) {
			// �ٴ�·����Ŀ
			result.setData("TYPE_CHN_DESC", typeChnDesc);
			/************************* ������Ϣ *************************/
			// ORDER_SEQ_NO
			result.setData("ORDER_SEQ_NO", "");
			// SEQ
			result.setData("SEQ", "");
			// ORDER_CODE
			result.setData("ORDER_CODE", orderCode);
			// ORDTYPE_CODE
			result.setData("ORDTYPE_CODE", ordTypeCode);
			// ROUT_CODE
			result.setData("ROUT_CODE", "");
			// DOSE_DAYS
			result.setData("DOSE_DAYS", "");
			// RBORDER_DEPT_CODE
			result.setData("RBORDER_DEPT_CODE", "");
			// URGENT_FLG
			result.setData("URGENT_FLG", "");
			/************************* ������Ϣ *************************/

			/************************* �������� *************************/
			TParm tableParm = CLP_PACK03.getParmValue();
			tableParm.setRowData(selRow, result);
			CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
			/************************* �������� *************************/
		}
		/************************* Ĭ����Ϣ *************************/
		// ¬������-20110630 begin
		TParm tableParm = CLP_PACK03.getParmValue();
		tableParm.setData("CHKTYPE_CODE", selRow, getDefaultCheckTypeCode());
		CLP_PACK03.setParmValue(tableParm, CLP_PACK03.getParmMap());
		// ¬������-20110630 end

	}

	/**
	 * ���� ��λ �շ� �д���
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableChangeValueForPack01(Object obj) {
		CLP_PACK01.acceptText();
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		int row = node.getRow();
		int column = node.getColumn();
		// ���� Ƶ�� �շ� �д���
		if (column == 6 || column == 8 || column == 11) {
			double dose = 0.0;
			TParm tableParm = CLP_PACK01.getParmValue();

			if (column == 6) {
				// =========pangben 2012-6-21 �������ĵ��ó�Ժ��ҩ������������
				TParm parm = new TParm();
				parm = tableParm.getRow(row);
				parm.setData("MEDI_QTY", node.getValue());// ��ҩ��λ
				// CLP_PACK01.setItem(row, "DOSE", node.getValue());
				// CLP_PACK01.setItem(row, "MEDI_QTY", node.getValue());
				CLP_PACK01.setItem(row, "MEDI_QTY", node.getValue());// ��ҩ��λ
				//CLP_PACK01.setItem(row, "DOSAGE_QTY", node.getValue());// ��ҩ��λ
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
//				if (null != parm.getValue("CAT1_TYPE")
//						&& ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS"
//								.equals(parm.getValue("CAT1_TYPE")))) {
//					dose = totParm.getDouble("QTY");
//				} else {
//					dose = totParm.getDouble("QTY") * parm.getInt("DOSE_DAYS");
//				}
				dose = totParm.getDouble("QTY");
			}
			// else {
			// TParm totParm = TotQtyTool.getInstance().getTotQty(
			// tableParm.getRow(row));
			// dose = totParm.getDouble("QTY");
			// // dose = Double.parseDouble(String.valueOf(CLP_PACK01
			// // .getItemData(row, "DOSE")));
			// }
			// String doseUnit = String.valueOf(CLP_PACK01.getItemData(row,
			// "DOSE_UNIT"));
			String freqCode = "";
			if (column == 8) {
				if (!udFlg) {
					return;
				}
				freqCode = String.valueOf(node.getValue());
				// =========pangben 2012-6-21 ��������
				TParm parm = tableParm.getRow(row);
				parm.setData("FREQ_CODE", freqCode);// Ƶ��
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
				dose = totParm.getDouble("QTY");
				dose = dose * gettime(freqCode);
				// CLP_PACK01.setItem(row, "FREQ_CODE", freqCode);
			}
			// else {
			// freqCode = String.valueOf(CLP_PACK01.getItemData(row,
			// "FREQ_CODE"));
			// }
			int doseDays = 0;
			if (column == 11) {
				doseDays = Integer.parseInt(String.valueOf(node.getValue()));
				// =========pangben 2012-6-21 ��������

				TParm parm = tableParm.getRow(row);
				parm.setData("TAKE_DAYS", doseDays);// ����
				TParm totParm = TotQtyTool.getInstance().getTotQty(parm);
				dose = totParm.getDouble("QTY");
				CLP_PACK01.setItem(row, "TAKE_DAYS", doseDays);
			}
			// else {
			// doseDays = Integer.parseInt(String.valueOf(CLP_PACK01
			// .getItemData(row, "DOSE_DAYS")));
			// System.out.println("doseDays:::"+doseDays);
			// }
			// ���㵱ǰ��¼���������ܽ��
			double ownPrice = CLP_PACK01.getParmValue().getDouble("OWN_PRICE",
					row);
			// TParm parm=getTotalAndFees(dose, doseUnit, freqCode, doseDays,
			// getOwnPrice(orderCode));
			// ���õ�ǰ��¼���������ܽ��
			CLP_PACK01.setItem(row, "TOTAL", dose);
			CLP_PACK01.setItem(row, "FEES", dose * ownPrice);
		}
	}

	/**
	 * ���Ƶ������
	 * 
	 * @param freqCode
	 * @return
	 */
	private int gettime(String freqCode) {
		TParm parmTrn = new TParm(TJDODBTool.getInstance().select(
				"SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
						+ freqCode + "'"));
		int times = parmTrn.getInt("FREQ_TIMES", 0);
		return times;
	}

	public TParm getTotalAndFees(TParm parm, int row) {
		//double dose = Double.parseDouble(parm.getValue("DOSE", row));
		// String doseUnit = parm.getValue("DOSE_UNIT", row);// ��λ
		// String freqCode = parm.getValue("FREQ_CODE", row);// Ƶ��
		// int doseDays = Integer.parseInt(parm.getValue("DOSE_DAYS", row));//
		// �շ�
		// ���㵱ǰ��¼���������ܽ��
		// String orderCode = parm.getValue("ORDER_CODE", row);
		// double dosageQty = parm.getDouble("DOSAGE_QTY", row);// ��ҩ��λ
		// =============pangben 2012-6-21 start ��ʾ���� ���
		TParm totParm = TotQtyTool.getInstance().getTotQty(parm.getRow(row));
		double dose = totParm.getDouble("QTY");// ����
		// TParm totalAndFees = getTotalAndFees(dose, doseUnit, freqCode,
		// doseDays, parm.getRow(row).getDouble("OWN_PRICE"));
		// if (null!=parm.getValue("ROUT_CODE", row)
		// &&("IVD".equals(parm.getValue("ROUT_CODE",
		// row))||"IVP".equals(parm.getValue("ROUT_CODE", row)))) {
		// //DOSE_UNIT=9 ���� ML ��������
		// if (null!=parm.getValue("DOSE_UNIT",
		// row)&&"9".equals(parm.getValue("DOSE_UNIT", row))) {
		// //����
		// totalAndFees.setData("TOTAL",StringTool.round(totalAndFees.getDouble("TOTAL")/parm.getDouble("DOSE",
		// row),2));
		// //�ܽ��
		// totalAndFees.setData("FEES",StringTool.round(totalAndFees.getDouble("FEES")/parm.getDouble("DOSE",
		// row),2));
		// }
		// }
		// ���õ�ǰ��¼���������ܽ��
		parm.setData("TOTAL", row, dose);
		// ===========pangben 2012-6-25
		parm.setData("OWN_PRICE", row, parm.getRow(row).getDouble("OWN_PRICE"));
		parm.setData("FEES", row,  
				StringTool.round(dose * parm.getRow(row).getDouble("OWN_PRICE"), 2));//modify by wanglong 20120924
		
		return parm;
	}

	/**
	 * ��ձ�������
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 */
	public void clearRowData(TTable table, int row) {
		TParm clearParm = new TParm();
		clearParm.setData("ORDER_TYPE", table.getItemData(row, "ORDER_TYPE"));
		TParm tableParm = table.getParmValue();
		tableParm.setRowData(row, clearParm);
		table.setParmValue(tableParm, table.getParmMap());
	}

	/**
	 * ��ѯ�ٴ�·����Ŀ
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrdTypeCode(String orderCode, String orderFlg) {
		String ordTypeCodeSql = " SELECT A.*, B.TYPE_CHN_DESC FROM CLP_ORDERTYPE A, CLP_ORDTYPE B WHERE "
				+ " A.ORDTYPE_CODE = B.TYPE_CODE"
				+ " AND A.ORDER_CODE = '"
				+ orderCode + "'" + " AND A.ORDER_FLG = '" + orderFlg + "'";
		TParm ordTypeCodeResult = new TParm(TJDODBTool.getInstance().select(
				ordTypeCodeSql));
		return ordTypeCodeResult;
	}

	/**
	 * �����ٴ�·����Ŀ�����ȡ��ҽ����Ϣ
	 * 
	 * @param ordTypeCode
	 *            String
	 * @param orderFlg
	 *            String
	 * @return TParm
	 */
	public TParm getOrderByOrdTypeCode(String ordTypeCode, String orderFlg,
			String defaultFlg) {
		TParm parm = new TParm();
		parm.setData("ORDTYPE_CODE", ordTypeCode);
		parm.setData("ORDER_FLG", orderFlg);
		parm.setData("DEFAULT_FLG", defaultFlg);
		return PackTool.getInstance().getOrderByOrdTypeCode(parm);
	}

	/**
	 * ��ȡ�Էѵ���
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @return double
	 */
	public double getOwnPrice(String orderCode) {
		// ��ȡ����
		String sql = " SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE = '"
				+ orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getDouble("OWN_PRICE", 0);
	}

	/**
	 * ����У�飺ҽ������
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public boolean checkOrderCode(String orderDesc) {
		if (orderDesc == null || "".equals(orderDesc.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ����У�飺�ٴ�·����Ŀ
	 * 
	 * @param ordTypeCode
	 *            String
	 * @return boolean
	 */
	public boolean checkOrdTypeCode(String ordTypeCode) {
		if (ordTypeCode == null || "".equals(ordTypeCode.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ����һ��
	 * 
	 * @param table
	 *            TTable
	 */
	public void insertNewRow(TTable table, int row) {
		int rowID = table.addRow(row + 1);
		table.setItem(rowID, "ORDER_TYPE", "ST");
	}

	/**
	 * O �����б�ָ����Ԫ��ɱ༭
	 * 
	 * @param table
	 *            TTable
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void setTableEnabled(TTable table, int row, int column) {
		int rowCount = table.getRowCount();
		String lockRows = "";
		int columnCount = table.getColumnCount();
		String lockColumns = "";
		if (row < 0 || row >= rowCount) {
			return;
		}
		if (column < 0 || column >= columnCount) {
			return;
		}
		for (int i = 0; i < rowCount; i++) {
			if (i != row) {
				lockRows = lockRows + i + ",";
			}
		}
		for (int i = 0; i < columnCount; i++) {
			if (i != column) {
				lockColumns = lockColumns + i + ",";
			}
		}
		if (lockRows.endsWith(",")) {
			lockRows = lockRows.substring(0, lockRows.length() - 1);
		}
		table.setLockRows(lockRows);
		if (lockColumns.endsWith(",")) {
			lockColumns = lockColumns.substring(0, lockColumns.length() - 1);
		}
		table.setLockColumns(lockColumns);
		table.setSelectedColumn(column);
		table.setSelectedRow(row);
	}

	/**
	 * ҽ���ײ� ���㵱ǰ��¼���������ܽ��
	 */
	public TParm getTotalAndFees(double dose, String doseUnit, String freqCode,
			int doseDays, double ownPrice) {
		TParm parm = new TParm();
		// ��������
		TParm totalAndFeesParm = TotQtyTool.getIbsQty(dose, doseUnit, freqCode,
				doseDays);
		double total = totalAndFeesParm.getDouble("DOSAGE_QTY");
		parm.setData("TOTAL", total);
		// �����ܽ��
		double fees = total * ownPrice;
		parm.setData("FEES", fees);
		return parm;
	}

	/**
	 * ҽ���ײ� �����ܽ�ȫ���б�
	 */
	public void setALLFEES() {
		double totFee = 0.0;
		int rows = CLP_PACK01.getRowCount();
		for (int i = 0; i < rows; i++) {
			totFee += TypeTool.getDouble(CLP_PACK01.getValueAt(i, 16));
		}
		FEES.setValue(String.valueOf(totFee));
	}

	/**
	 * ��nullת��Ϊ���ַ���
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}

	/**
	 * ģ����ѯ
	 */
	public void onQuery() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// �ٴ�·�����
			TParm parm = new TParm();
			// ��ȡ��ѯ��������װ��������
			// ¬���޸�begin regionCode
			parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", regionCode);
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("PY1", PY1.getValue());
			parm.setData("PY2", PY2.getValue());
			parm.setData("CLNCPATH_CHN_DESC", CLNCPATH_CHN_DESC.getValue());
			parm.setData("CLNCPATH_ENG_DESC", CLNCPATH_ENG_DESC.getValue());
			parm.setData("DEPT_CODE", DEPT_CODE.getValue());
			parm.setData("FRSTVRSN_DATE", FRSTVRSN_DATE.getValue());
			parm.setData("LASTVRSN_DATE", LASTVRSN_DATE.getValue());
			parm.setData("MODIFY_TIMES", MODIFY_TIMES.getValue());
			parm.setData("ACPT_CODE", ACPT_CODE.getValue());
			parm.setData("EXIT_CODE", EXIT_CODE.getValue());
			parm.setData("STAYHOSP_DAYS", STAYHOSP_DAYS.getValue());
			parm.setData("AVERAGECOST", AVERAGECOST.getValue());
			parm.setData("DESCRIPTION", DESCRIPTION.getValue());
			parm.setData("ACTIVE_FLG", ACTIVE_FLG.getValue());
			// ģ����ѯ�ٴ�·�����
			TParm bscInfoList = BscInfoTool.getInstance().getBscInfoList(parm);
			// ��ʾ��ǰ̨����
			CLP_BSCINFO.setParmValue(bscInfoList);
		} else if (selectedIndex == 1) {
			// ����ʱ��
			TParm parm = new TParm();
			// ��ȡ��ѯ��������װ��������
			parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			// ģ����ѯ����ʱ��
			TParm thrpyschdmList = ThrpyschdmTool.getInstance()
					.getThrpyschdmList(parm);
			// ��ʾ��ǰ̨����
			CLP_THRPYSCHDM.setParmValue(thrpyschdmList);
		} else if (selectedIndex == 2) {
			// ҽ���ײ�
			TParm parm = new TParm();
			// ��ȡ��ѯ��������װ��������
			// luhai modify begin
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode);
			// luhai modify end
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "Y");
			// ģ����ѯҽ���ײ�
			TParm pack01List = PackTool.getInstance().getPack01List(parm);
			for (int i = 0; i < pack01List.getCount(); i++) {
				// ============pangben 2012-6-25 ����ҽ��ϸ����ʾ��� ORDERSET_FLG=Y Ϊ����ҽ������
				TParm result = pack01List.getRow(i);
				if (null != result.getValue("CAT1_TYPE")&&"Y".equals(result.getValue("ORDERSET_FLG"))) {
					double allOwnAmt = 0.00;
					TParm parmDetail = SYSOrderSetDetailTool
							.getInstance()
							.selectByOrderSetCode(result.getValue("ORDER_CODE"));
					for (int j = 0; j < parmDetail.getCount(); j++) {
						allOwnAmt = allOwnAmt
								+ parmDetail.getDouble("OWN_PRICE", j)
								* parmDetail.getDouble("TOTQTY", j);
					}
					pack01List.setData("OWN_PRICE", i, allOwnAmt);
					// result.setData("OWN_PRICE",allOwnAmt);
				}
				getTotalAndFees(pack01List, i);
			}
			// ��ʾ��ǰ̨����
			CLP_PACK01.setParmValue(pack01List);
			insertNewRow(CLP_PACK01, CLP_PACK01.getParmValue().getCount() - 1);
		} else if (selectedIndex == 3) {
			// �ؼ�������Ŀ
			TParm parm = new TParm();
			// ��ȡ��ѯ��������װ��������
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode); // luhai modify
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "N");
			// ģ����ѯ�ؼ�������Ŀ
			TParm pack02List = PackTool.getInstance().getPack02List(parm);
			// ��ʾ��ǰ̨����
			CLP_PACK02.setParmValue(pack02List, CLP_PACK02.getParmMap());
			insertNewRow(CLP_PACK02, CLP_PACK02.getParmValue().getCount() - 1);
		} else if (selectedIndex == 4) {
			// ����ƻ�
			TParm parm = new TParm();
			// ��ȡ��ѯ��������װ��������
			// parm.setData("REGION_CODE", REGION_CODE.getValue());
			parm.setData("REGION_CODE", this.regionCode); // luhai modify
			parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
			parm.setData("SCHD_CODE", SCHD_CODE.getValue());
			parm.setData("ORDER_FLG", "O");
			// ģ����ѯ����ƻ�
			TParm pack03List = PackTool.getInstance().getPack03List(parm);
			// ��ʾ��ǰ̨����
			CLP_PACK03.setParmValue(pack03List, CLP_PACK03.getParmMap());
			insertNewRow(CLP_PACK03, CLP_PACK03.getParmValue().getCount() - 1);
		}
	}

	public void onClear() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// �ٴ�·�����
			CLNCPATH_CODE.setEditable(true);
			CLNCPATH_CODE.setValue("");
			PY1.setValue("");
			PY2.setValue("");
			CLNCPATH_CHN_DESC.setValue("");
			CLNCPATH_ENG_DESC.setValue("");
			DEPT_CODE.setValue("");
			FRSTVRSN_DATE.setValue("");
			LASTVRSN_DATE.setValue("");
			MODIFY_TIMES.setValue("");
			ACPT_CODE.setValue("");
			EXIT_CODE.setValue("");
			STAYHOSP_DAYS.setValue("");
			STAYHOSP_DAYS.setEditable(true);
			AVERAGECOST.setValue("");
			VERSION.setValue("");
			DESCRIPTION.setValue("");
			ACTIVE_FLG.setValue(false);
			CLP_BSCINFO.clearSelection();
			this.diagnose_desc.setValue("");
			this.diagnose.setValue("");
			this.operation_diagnose.setValue("");
			this.operation_diagnose_desc.setValue("");
			this.operatorDiagnoseTable.setParmValue(new TParm());
			this.diagnoseTable.setParmValue(new TParm());
			// ����Ĭ����
			addDefaultRowForDiag(this.OPERATOR_DIAGNOSE_TABLE);
			addDefaultRowForDiag(this.DIAGNOSE_TABLE);
		} else if (selectedIndex == 1) {
			// ����ʱ��
			SCHD_CODE.setEnabled(true);
			SCHD_CODE.setValue("");
			SCHD_DAY.setValue("");
			SUSTAINED_DAYS.setValue("");
			CLP_THRPYSCHDM.clearSelection();
		} else if (selectedIndex == 2) {
			// ҽ���ײ�
			VERSION01.setValue("");
			CLP_PACK01.clearSelection();
		} else if (selectedIndex == 3) {
			// �ؼ������ײ�
			VERSION02.setValue("");
			CLP_PACK02.clearSelection();
		} else if (selectedIndex == 4) {
			// ����ƻ�
			VERSION03.setValue("");
			CLP_PACK03.clearSelection();
		}

	}

	public void menuControl() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// ��ȡ��ѯ�˵�
		TMenuItem query = (TMenuItem) this.getComponent("query");
		TMenuItem use = (TMenuItem) this.getComponent("use");
		TMenuItem save = (TMenuItem) this.getComponent("save");
		TMenuItem delete = (TMenuItem) this.getComponent("delete");
		// ��selectedIndex=0ʱ����ѯ�����ò˵����ã����򣬲�ѯ�����ò˵�������
		if (selectedIndex <= 0) {
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(true);
			use.setEnabled(true);
		}
		// ����ʱ�̣��汾��Ϊ1��ʱ������޸�
		else if (selectedIndex == 1) {
//			if (!"1".equals(VERSION.getValue())) {
//				save.setEnabled(false);
//				delete.setEnabled(false);
//			} else {
//				save.setEnabled(true);
//				delete.setEnabled(true);
//			}
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(false);
			use.setEnabled(false);
		} else {
			save.setEnabled(true);
			delete.setEnabled(true);
			query.setEnabled(false);
			use.setEnabled(false);
		}
	}

	/**
	 * ��ѡ��л�ʱ ���ô˷���
	 */
	public void onChange() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// ��ѯ�˵�����
		menuControl();
		// ����ǰ��ѡ��ҳǩ����Ϊ0���ٴ�·����𣩣������κδ���ֱ�ӷ���
		if (selectedIndex <= 0) {
			// �汾���ɱ༭
			VERSION.setEditable(false);
			// �Ƿ����ñ�ʶ���ɱ༭
			ACTIVE_FLG.setEnabled(false);
			// ��¼�˴ε�ѡ��ҳǩ����
			lastSelectedIndex = selectedIndex;
			return;
		}
		// ����ǰ��ѡ��ҳǩ����Ϊ1������ʱ�̣�������֤�Ƿ�ѡ�����ٴ�·�����
		if (selectedIndex == 1) {
			if (CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ٴ�·�����");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			// ·����Ŀ���ɱ༭
			CLNCPATH_CODE01.setEnabled(false);
			CLNCPATH_CODE01.setValue(CLNCPATH_CODE.getValue());
			// ��һ�����ڵ�ѡ�����Ϊ0���ٴ�·����𣩣��������ǰ��Ϣ�����²�ѯʱ����Ϣ
			if (lastSelectedIndex == 0) {
				// �����ǰ��Ϣ
				onClear();
				// ģ����ѯʱ��
				onQuery();
			}
		}
		// ����ǰ��ѡ��ҳǩ����Ϊ����������֤�Ƿ�ѡ�����ٴ�·����������ʱ��
		if (selectedIndex == 2) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ٴ�·�����");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("��ѡ������ʱ�̣�");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// �汾���ɱ༭
			VERSION01.setEditable(false);

			// ·����Ŀ���ɱ༭
			CLNCPATH_CODE02.setEnabled(false);
			CLNCPATH_CODE02.setValue(CLNCPATH_CODE.getValue());
			// ʱ�̴��벻�ɱ༭
			SCHD_CODE01.setEnabled(false);
			SCHD_CODE01.setValue(SCHD_CODE.getValue());
			// �ܽ��ɱ༭
			FEES.setEditable(false);
			// �����ǰ��Ϣ
			onClear();
			// ģ����ѯʱ��
			onQuery();
			// ==============pangben 2012-6-19 start
			this.setValue("VERSION01", VERSION.getValue());
			// ==============pangben 2012-6-19 stop
			// ===============pangben 2012-5-18
			// TParm clpParm = CLP_BSCINFO.getParmValue();
			// ===============pangben stop
			// this.setValue("VERSION01", clpParm.getValue("VERSION", 0));
		}
		if (selectedIndex == 3) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ٴ�·�����");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("��ѡ������ʱ�̣�");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// �汾���ɱ༭
			VERSION02.setEditable(false);
			// ·����Ŀ���ɱ༭
			CLNCPATH_CODE03.setEnabled(false);
			CLNCPATH_CODE03.setValue(CLNCPATH_CODE.getValue());
			// ʱ�̴��벻�ɱ༭
			SCHD_CODE02.setEnabled(false);
			SCHD_CODE02.setValue(SCHD_CODE.getValue());
			// �����ǰ��Ϣ
			onClear();
			// ģ����ѯʱ��
			onQuery();
			this.setValue("VERSION02", VERSION.getValue());
		}
		if (selectedIndex == 4) {
			if (lastSelectedIndex == 0 && CLP_BSCINFO.getSelectedRow() < 0) {
				this.messageBox("��ѡ���ٴ�·�����");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
			if (lastSelectedIndex == 0
					|| (lastSelectedIndex == 1 && CLP_THRPYSCHDM
							.getSelectedRow() < 0)) {
				this.messageBox("��ѡ������ʱ�̣�");
				tTabbedPane_0.setSelectedIndex(1);
				return;
			}
			// �汾���ɱ༭
			VERSION03.setEditable(false);
			// ·����Ŀ���ɱ༭
			CLNCPATH_CODE04.setEnabled(false);
			CLNCPATH_CODE04.setValue(CLNCPATH_CODE.getValue());
			// ʱ�̴��벻�ɱ༭
			SCHD_CODE03.setEnabled(false);
			SCHD_CODE03.setValue(SCHD_CODE.getValue());
			// �����ǰ��Ϣ
			onClear();
			// ģ����ѯʱ��
			onQuery();
			this.setValue("VERSION03", VERSION.getValue());
		}
		// ��¼�˴ε�ѡ��ҳǩ����
		lastSelectedIndex = selectedIndex;
	}

	/**
	 * ��װ����TParm CLP_BSCINFO
	 */
	public TParm parmFormatForBscInfo() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue().toUpperCase());
		parm.setData("CLNCPATH_CHN_DESC", nullToString(CLNCPATH_CHN_DESC
				.getValue()));
		parm.setData("CLNCPATH_ENG_DESC", nullToString(CLNCPATH_ENG_DESC
				.getValue()));
		parm.setData("PY1", nullToString(PY1.getValue()));
		parm.setData("PY2", nullToString(PY2.getValue()));
		parm.setData("DEPT_CODE", nullToString(String.valueOf(DEPT_CODE
				.getValue())));
		parm.setData("FRSTVRSN_DATE", dateFormat.format(FRSTVRSN_DATE
				.getValue()));
		if (!this.checkNullAndEmpty(LASTVRSN_DATE.getValue() + "")) {
			parm.setData("LASTVRSN_DATE", dateFormat.format(LASTVRSN_DATE
					.getValue()));
		}
		parm.setData("MODIFY_TIMES", MODIFY_TIMES.getValue());
		parm.setData("ACPT_CODE", nullToString(ACPT_CODE.getValue()));
		parm.setData("EXIT_CODE", nullToString(EXIT_CODE.getValue()));
		parm.setData("STAYHOSP_DAYS", STAYHOSP_DAYS.getValue());
		parm.setData("AVERAGECOST", AVERAGECOST.getValue());
		parm.setData("VERSION", VERSION.getValue());
		parm.setData("DESCRIPTION", DESCRIPTION.getValue());
		parm.setData("ACTIVE_FLG", ACTIVE_FLG.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ICD_CODE", this.diagnose.getValue());
		parm.setData("OPE_ICD_CODE", this.operation_diagnose.getValue());
		return parm;
	}

	/**
	 * ��װ����TParm CLP_THRPYSCHDM
	 */
	public TParm parmFormatForThrpyschdm() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue().toUpperCase());
		parm.setData("SCHD_CODE", String.valueOf(SCHD_CODE.getValue())
				.toUpperCase());
		parm.setData("SCHD_DAY", SCHD_DAY.getValue());
		parm.setData("SUSTAINED_DAYS", SUSTAINED_DAYS.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 * ��װ����TParm CLP_PACK
	 * 
	 * @param String
	 *            ORDER_FLG Y:ҽ���ײ� N:�ؼ�������Ŀ O:����ƻ�
	 */
	public TParm parmFormatForPack(String ORDER_FLG) {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
		parm.setData("SCHD_CODE", SCHD_CODE.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ORDER_FLG", ORDER_FLG);
		return parm;
	}

	public boolean checkBscInfo() {
		if (CLNCPATH_CODE.getValue() == null
				|| "".equals(CLNCPATH_CODE.getValue().trim())) {
			this.messageBox("������·�����룡");
			return false;
		}
		if (FRSTVRSN_DATE.getValue() == null
				|| "".equals(FRSTVRSN_DATE.getValue())) {
			this.messageBox("�������ƶ����ڣ�");
			return false;
		}
		// luhai delete 20110630 begin
		// if (LASTVRSN_DATE.getValue() == null ||
		// "".equals(LASTVRSN_DATE.getValue())) {
		// this.messageBox("����������޶��գ�");
		// return false;
		// }
		// if (MODIFY_TIMES.getValue() == null ||
		// "".equals(MODIFY_TIMES.getValue())) {
		// this.messageBox("�������޸Ĵ�����");
		// return false;
		// }
		// luhai delete 20110630 end
		if (STAYHOSP_DAYS.getValue() == null
				|| "".equals(STAYHOSP_DAYS.getValue())) {
			this.messageBox("�������׼סԺ������");
			return false;
		}
		if (AVERAGECOST.getValue() == null || "".equals(AVERAGECOST.getValue())) {
			this.messageBox("������ƽ��ҽ�Ʒ��ã�");
			return false;
		}
		return true;
	}

	/**
	 * �ٴ�·�����
	 */
	public boolean saveBscInfo(TParm parm) {
		CLP_BSCINFO.acceptText();
		// ��ѯ�������ݣ��ٴ�·�����
		int count = BscInfoTool.getInstance().getBscInfoObject(parm).getCount();
		// �жϱ������������ݿ����Ƿ����
		if (count <= 0) {
			// ��������ڣ�����в������
			TParm seqParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT NVL(MAX(SEQ) + 1, 0) AS SEQ FROM CLP_BSCINFO"));
			parm.setData("SEQ", seqParm.getInt("SEQ", 0));
			// TParm result = BscInfoTool.getInstance().insert(parm);
			TParm saveParm = new TParm();
			saveParm.setData("bscinfoParm", parm.getData());
			saveParm.setData("icdParm", this.diagnoseTable.getParmValue()
					.getData());
			saveParm.setData("optParm", this.operatorDiagnoseTable
					.getParmValue().getData());
			saveParm.setData("basicInfo", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPBscInfoAction", "insertBSCInfo", saveParm);

			if (result.getErrCode() >= 0) {
				this.messageBox("����ɹ���");
				return true;
			} else {
				this.messageBox("����ʧ�ܣ�");
				return false;
			}
		} else {
			// ������ڣ�����и��²���
			// TParm result = BscInfoTool.getInstance().update(parm);
			TParm saveParm = new TParm();
			saveParm.setData("bscinfoParm", parm.getData());
			saveParm.setData("icdParm", this.diagnoseTable.getParmValue()
					.getData());
			saveParm.setData("optParm", this.operatorDiagnoseTable
					.getParmValue().getData());
			saveParm.setData("basicInfo", this.getBasicOperatorMap());
			TParm result = TIOM_AppServer.executeAction(
					"action.clp.CLPBscInfoAction", "updateBSCInfo", saveParm);
			if (result.getErrCode() >= 0) {
				this.messageBox("����ɹ���");
				return true;
			} else {
				this.messageBox("����ʧ�ܣ�");
				return false;
			}
		}
	}

	public boolean checkThrpyschdm() {
		if (CLNCPATH_CODE.getValue() == null
				|| "".equals(CLNCPATH_CODE.getValue().trim())) {
			this.messageBox("������·�����룡");
			return false;
		}
		if (SCHD_CODE.getValue() == null || "".equals(SCHD_CODE.getValue())) {
			this.messageBox("������ʱ�̴��룡");
			return false;
		}
		if (SCHD_DAY.getValue() == null
				|| "".equals(SCHD_DAY.getValue().trim())) {
			this.messageBox("������ʱ��������");
			return false;
		}
		int schdDay = Integer.parseInt(SCHD_DAY.getValue());
		if (schdDay < 0) {
			this.messageBox("ʱ����������Ϊ����");
			return false;
		}
		schdDay = getTotalSchDay();
		int stayHospDays = Integer.parseInt(STAYHOSP_DAYS.getValue());
		// System.out.println("��׼סԺ������" + stayHospDays);
		if (schdDay > stayHospDays) {
			this.messageBox("ʱ��������������׼סԺ������");
			return false;
		}
		return true;
	}

	/**
	 * �õ���ʵ��ʱ����
	 * 
	 * @return int
	 */
	private int getTotalSchDay() {
		int schdDay = 0;
		TTable durationTable = (TTable) this.getComponent("CLP_THRPYSCHDM");
		TParm tableParm = durationTable.getParmValue();
		// ʵ�ʿ�ʼʱ��
		String realsustainedDay = this.getValueString("SUSTAINED_DAYS");
		if (!this.validNumber(realsustainedDay)) {
			realsustainedDay = "0";
		}
		// ʵ������
		String realSchdDay = String.valueOf(SCHD_DAY.getValue());
		String realSchdCode = String.valueOf(SCHD_CODE.getValue());
		int realEndDay = this.getIntValue(realsustainedDay)
				+ this.getIntValue(realSchdDay) - 1;
		boolean isedit = false;
		List<Map> durationList = new ArrayList<Map>();
		for (int i = 0; i < tableParm.getCount(); i++) {
			String currentSchdCode = String.valueOf(tableParm.getValue(
					"SCHD_CODE", i));
			String currentSchdDay = String.valueOf(tableParm.getValue(
					"SCHD_DAY", i));
			String currentStartDay = tableParm.getValue("SUSTAINED_DAYS", i);
			if (realSchdCode.equalsIgnoreCase(currentSchdCode)) {
				currentSchdDay = realSchdDay; // �����Ǳ༭��ֵ��ʹ�ñ༭��ֵ
				currentStartDay = realsustainedDay;
				isedit = true;
			}
			// ��ʼ��������
			int currentSchdDayIntValue = this.getIntValue(currentSchdDay); // ��������
			int currentStartDayIntValue = this.getIntValue(currentStartDay); // ��ʼ����
			int currentEndDayIntValue = currentSchdDayIntValue
					+ currentStartDayIntValue - 1;
			HashMap<String, Integer> durationMap = new HashMap<String, Integer>();
			durationMap.put("currentSchdDay", currentSchdDayIntValue);
			durationMap.put("currentStartDay", currentStartDayIntValue);
			durationMap.put("currentEndDay", currentEndDayIntValue);
			durationList.add(durationMap);
		}
		// ������¼����ֵҲҪ�����ۼ�
		if (!isedit) {
			HashMap<String, Integer> durationMap = new HashMap<String, Integer>();
			durationMap.put("currentSchdDay", this.getIntValue(realSchdDay));
			durationMap.put("currentStartDay", this
					.getIntValue(realsustainedDay));
			durationMap.put("currentEndDay", realEndDay);
			durationList.add(durationMap);
		}
		Collections.sort(durationList, new comparatorDuration());
		for (Map obj : durationList) {
			// System.out.println("-----------------------��ʼ����---------" +
			// obj.get("currentStartDay") + "ִ��ʱ�䣺" +
			// obj.get("currentSchdDay") + "����ʱ�䣺" +
			// obj.get("currentEndDay"));
		}
		for (int i = 0; i < durationList.size(); i++) {
			Map<String, Integer> mapObj = (Map) durationList.get(i);
			Map<String, Integer> mapObjPre = null;
			if (i > 0) {
				mapObjPre = (Map) durationList.get(i - 1);
			}
			if (mapObjPre == null) {
				schdDay += mapObj.get("currentSchdDay");
			} else {
				int currentSchdDay = mapObj.get("currentSchdDay");
				int currentStartDay = mapObj.get("currentStartDay");
				int preEndDay = mapObjPre.get("currentEndDay");
				if (currentStartDay <= preEndDay) {
					schdDay += currentSchdDay
							- (preEndDay - currentStartDay + 1);
				} else {
					schdDay += currentSchdDay;
				}
			}
		}
		// System.out.println("�ܵ�ִ��������" + schdDay);
		return schdDay;
	}
	/**
	 * ����ʱ��
	 */
	public boolean saveThrpyschdm(TParm parm) {
		CLP_THRPYSCHDM.acceptText();
		// ��ѯ�������ݣ�����ʱ�̣�
		int count = ThrpyschdmTool.getInstance().getThrpyschdmObject(parm)
				.getCount();
		// �жϱ������������ݿ����Ƿ����
		if (count <= 0) {
			// ��������ڣ�����в������
			int seq = 0;

			TParm seqParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT MAX(SEQ) AS SEQ FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE = '"
							+ parm.getValue("CLNCPATH_CODE") + "'"));
			if (null != seqParm.getValue("SEQ", 0)) {
				seq = seqParm.getInt("SEQ", 0);
				seq++;
			}
			parm.setData("SEQ", seq);
			TParm result = ThrpyschdmTool.getInstance().insert(parm);
			if (result.getErrCode() >= 0) {
				this.messageBox("����ɹ���");
				return true;
			} else {
				this.messageBox("����ʧ�ܣ�");
				return false;
			}

		} else {
			// ������ڣ�����и��²���
			TParm result = ThrpyschdmTool.getInstance().update(parm);
			if (result.getErrCode() >= 0) {
				this.messageBox("����ɹ���");
				return true;
			} else {
				this.messageBox("����ʧ�ܣ�");
				return false;
			}
		}
	}

	/**
	 * �ַ����ǿ���֤
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public boolean checkInputString(String str) {
		if (str == null) {
			return false;
		} else if ("".equals(str.trim())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ҽ���ײ�����У��
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack01(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("ҽ�������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("�˲������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("�ٴ�·����Ŀ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("ҽ�����Ʋ���Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("��������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("������λ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("Ƶ�β���Ϊ�գ�");
			return false;
		}
		// ����֤;��20110707
		// if ("PHA".equals(detail.getValue("CAT1_TYPE"))) {
		// if (!checkInputString(detail.getValue("ROUT_CODE"))) {
		// this.messageBox("ҩƷ��ҽ��;������Ϊ�գ�");
		// return false;
		// }
		// }
		if (!checkInputString(detail.getValue("DOSE_DAYS"))) {
			this.messageBox("�շݲ���Ϊ�գ�");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("�����׼�����벻��ȷ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * �ؼ�������Ŀ����У��
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack02(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("ҽ�������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("�˲������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("�ٴ�·����Ŀ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("ҽ�����Ʋ���Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("��������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("������λ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("Ƶ�β���Ϊ�գ�");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("�����׼�����벻��ȷ��");
				return false;
			}
		}
		return true;
	}

	/**
	 * ����ƻ�����У��
	 * 
	 * @param detail
	 *            TParm
	 * @return boolean
	 */
	public boolean checkPack03(TParm detail) {
		if (!checkInputString(detail.getValue("ORDER_TYPE"))) {
			this.messageBox("���������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("CHKTYPE_CODE"))) {
			this.messageBox("�˲������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("TYPE_CHN_DESC"))) {
			this.messageBox("�ٴ�·����Ŀ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("ORDER_CHN_DESC"))) {
			this.messageBox("�������Ʋ���Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE"))) {
			this.messageBox("��������Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("DOSE_UNIT"))) {
			this.messageBox("������λ����Ϊ�գ�");
			return false;
		}
		if (!checkInputString(detail.getValue("FREQ_CODE"))) {
			this.messageBox("Ƶ�β���Ϊ�գ�");
			return false;
		}
		if (checkInputString(detail.getValue("STANDARD"))) {
			if (detail.getValue("STANDARD").length() > 3) {
				this.messageBox("�����׼�����벻��ȷ��");
				return false;
			}
		}
		return true;
	}

	public boolean checkPack(TTable table, TParm parm) {
		table.acceptText();
		// System.out.println("table:"+table.getParmValue());
		for (int i = 0; i < table.getRowCount() - 1; i++) {
			TParm detail = parmFormatPackDetail(table, i, parm);
			// ҽ���ײ�У��
			if ("CLP_PACK01".equals(table.getTag())) {
				if (!checkPack01(detail)) {
					return false;
				}
			}
			// �ؼ�������Ŀ
			if ("CLP_PACK02".equals(table.getTag())) {
				if (!checkPack02(detail)) {
					return false;
				}
			}
			// ����ƻ�
			if ("CLP_PACK03".equals(table.getTag())) {
				if (!checkPack03(detail)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * �ٴ�·��
	 * 
	 * @param tableParm
	 *            TParm �б���ȫ������
	 * @param row
	 *            int ��ǰ�к�
	 * @param parm
	 *            TParm ���б�����
	 * @return TParm result
	 */
	public TParm parmFormatPackDetail(TTable table, int row, TParm parm) {
		TParm detail = new TParm();
		TParm data = table.getParmValue();
		// ���б�����
		detail.setData("CLNCPATH_CODE", parm.getValue("CLNCPATH_CODE"));
		detail.setData("SCHD_CODE", parm.getValue("SCHD_CODE"));
		detail.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		detail.setData("OPT_USER", parm.getValue("OPT_USER"));
		detail.setData("OPT_DATE", parm.getValue("OPT_DATE"));
		detail.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		detail.setData("ORDER_FLG", parm.getValue("ORDER_FLG"));
		// �б���ʾ��Ŀ
		detail.setData("ORDER_TYPE", data.getValue("ORDER_TYPE", row));
		detail.setData("ORDER_CHN_DESC", data.getValue("ORDER_CHN_DESC", row));
		detail.setData("TYPE_CHN_DESC", data.getValue("TYPE_CHN_DESC", row));
		detail.setData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE", row));
		detail.setData("DOSE", data.getValue("DOSE", row));
		detail.setData("DOSE_UNIT", data.getValue("DOSE_UNIT", row));
		detail.setData("FREQ_CODE", data.getValue("FREQ_CODE", row));
		detail.setData("ROUT_CODE", data.getValue("ROUT_CODE", row));
		detail.setData("DOSE_DAYS", data.getValue("DOSE_DAYS", row));
		detail.setData("NOTE", data.getValue("NOTE", row));
		detail.setData("RBORDER_DEPT_CODE", data.getValue("RBORDER_DEPT_CODE",
				row));
		detail.setData("CHKUSER_CODE", data.getValue("CHKUSER_CODE", row));
		detail.setData("URGENT_FLG", data.getValue("URGENT_FLG", row));
		detail.setData("EXEC_FLG", data.getValue("EXEC_FLG", row));
		detail.setData("STANDARD", data.getValue("STANDARD", row));
		detail.setData("VERSION", data.getValue("VERSION", row));
		detail.setData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO", row));
		detail.setData("SEQ", data.getValue("SEQ", row));
		detail.setData("ORDER_CODE", data.getValue("ORDER_CODE", row));
		detail.setData("ORDTYPE_CODE", data.getValue("ORDTYPE_CODE", row));
		detail.setData("CAT1_TYPE", data.getValue("CAT1_TYPE", row));
		return detail;
	}

	/**
	 * ����
	 */
	public void onSave() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// �ٴ�·�����
		if (selectedIndex == 0) {
			if (!checkBscInfo()) {
				return;
			}
			TParm parm = parmFormatForBscInfo();
			// ��������޶����ں��޶����� luhai Modify 20110630
			if (!this.checkNullAndEmpty(parm.getValue("LASTVRSN_DATE"))) {
				parm.setData("LASTVRSN_DATE", parm.getData("FRSTVRSN_DATE"));
			}
			if (!this.checkNullAndEmpty(parm.getValue("MODIFY_TIMES"))) {
				parm.setData("MODIFY_TIMES", "1");
			}
			// luhai Modify 20110630 end
			if (saveBscInfo(parm)) {
				// ��ѯȫ���ٴ�·����Ϣ
				TParm selectParm = new TParm();
				this.putBasicSysInfoIntoParm(selectParm);
				TParm bscInfoList = BscInfoTool.getInstance().getAllBscInfos(
						selectParm);
				// ��ʾ��ǰ̨����
				CLP_BSCINFO.setParmValue(bscInfoList, CLP_BSCINFO_PARMMAP);
				// ��ȡ�˴���ӻ��߱༭���������ڵ��к�
				int selectedRow = -1;
				TParm data = CLP_BSCINFO.getParmValue();
				for (int i = 0; i < CLP_BSCINFO.getRowCount(); i++) {
					if (CLNCPATH_CODE.getValue().equals(
							data.getValue("CLNCPATH_CODE", i))) {
						selectedRow = i;
					}
				}
				// ���ô���Ϊѡ��״̬
				CLP_BSCINFO.setSelectedRow(selectedRow);
				// �����е�ֵ��䵽��Ӧ���������
				onTableClickedForBscInfo(selectedRow);
			} else {
				return;
			}
		}
		// ����ʱ��
		if (selectedIndex == 1) {
			TParm parm = parmFormatForThrpyschdm();
			if (!checkThrpyschdm()) {
				return;
			}
			if (saveThrpyschdm(parm)) {
				// ��ѯȫ������ʱ����Ϣ
				TParm thrpyschdmList = ThrpyschdmTool.getInstance()
						.getThrpyschdmList(parm);
				// System.out.println("---------------ȫ������ʱ��:" +
				// thrpyschdmList);
				// ��ʾ��ǰ̨����
				CLP_THRPYSCHDM.setParmValue(thrpyschdmList);
				// ��ȡ�˴���ӻ��߱༭���������ڵ��к�
				int selectedRow = -1;
				TParm data = CLP_THRPYSCHDM.getParmValue();
				for (int i = 0; i < CLP_THRPYSCHDM.getRowCount(); i++) {
					if (SCHD_CODE.getValue().equals(
							data.getValue("SCHD_CODE", i))) {
						selectedRow = i;
					}
				}
				// ���ô���Ϊѡ��״̬
				CLP_THRPYSCHDM.setSelectedRow(selectedRow);
				// �����е�ֵ��䵽��Ӧ���������
				onTableClickedForThrpyschdm(selectedRow);
			} else {
				return;
			}
		}
		// ҽ���ײ�
		if (selectedIndex == 2) {
			if (CLP_PACK01.getRowCount() <= 1) {
				this.messageBox("û����Ҫ��������ݣ�");
				return;
			}
			TParm parm = parmFormatForPack("Y");
			// ҽ���ײ�У��
			if (!checkPack(CLP_PACK01, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK01.getParmValue().getData());
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// ����״̬ΪYʱ����ҽ���ײͲ�����ʷ����ǰҽ���ײͺ��ٴ�·���汾�ż�1��ͬʱ���õ�ǰ�ٴ�·��
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// ����״̬ΪNʱ��ֱ�Ӹ��µ�ǰҽ���ײ�
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("����ʧ�ܣ�");
				return;
			} else {
				this.messageBox("����ɹ���");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK01, "Y");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}
		}
		// �ؼ�������Ŀ
		if (selectedIndex == 3) {
			if (CLP_PACK02.getRowCount() <= 1) {
				this.messageBox("û����Ҫ��������ݣ�");
				return;
			}
			TParm parm = parmFormatForPack("N");
			// ҽ���ײ�У��
			if (!checkPack(CLP_PACK02, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK02.getParmValue().getData());
			// System.out.println("-------parm:"+parm);
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// ����״̬ΪYʱ����ҽ���ײͲ�����ʷ����ǰҽ���ײͺ��ٴ�·���汾�ż�1��ͬʱ���õ�ǰ�ٴ�·��
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// ����״̬ΪNʱ��ֱ�Ӹ��µ�ǰҽ���ײ�
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("����ʧ�ܣ�");
				return;
			} else {
				this.messageBox("����ɹ���");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK02, "Y");
				return;
			}
		}
		// ����ƻ�
		if (selectedIndex == 4) {
			if (CLP_PACK03.getRowCount() <= 1) {
				this.messageBox("û����Ҫ��������ݣ�");
				return;
			}
			TParm parm = parmFormatForPack("O");
			// ҽ���ײ�У��
			if (!checkPack(CLP_PACK03, parm)) {
				return;
			}
			parm.setData("CLP_PACK", CLP_PACK03.getParmValue().getData());
			TParm result = new TParm();
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				// ����״̬ΪYʱ����ҽ���ײͲ�����ʷ����ǰҽ���ײͺ��ٴ�·���汾�ż�1��ͬʱ���õ�ǰ�ٴ�·��
				parm.setData("CLP_BSCINFO", getUseParm("Y").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			} else {
				// ����״̬ΪNʱ��ֱ�Ӹ��µ�ǰҽ���ײ�
				parm.setData("CLP_BSCINFO", getUseParm("N").getData());
				result = TIOM_AppServer.executeAction(
						"action.clp.CLPBscInfoAction", "savePack", parm);
			}
			// �жϴ���ֵ
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				this.messageBox("����ʧ�ܣ�");
				return;
			} else {
				this.messageBox("����ɹ���");
				onQuery();
				resetVersionAndActiveFlg(CLP_PACK03, "Y");
				return;
			}
		}
	}

	/**
	 * �������ù�����Ϣ
	 * 
	 * @param table
	 *            TTable
	 * @param activeFlg
	 *            String
	 */
	public void resetVersionAndActiveFlg(TTable table, String activeFlg) {
		int selectedRow = CLP_BSCINFO.getSelectedRow();
		// �������ð汾
		String version = String.valueOf(table.getItemData(0, "VERSION"));
		VERSION.setValue(version);
		CLP_BSCINFO.setItem(selectedRow, "VERSION", version);
		// �����������ñ�ʶ
		ACTIVE_FLG.setValue(activeFlg);
		CLP_BSCINFO.setItem(selectedRow, "ACTIVE_FLG", activeFlg);
		// ���һ��ѡ��״̬
		if (table.getRowCount() > 0) {
			table.setSelectedRow(table.getRowCount() - 1);
		}
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// �ٴ�·�����
			int selectedRow = CLP_BSCINFO.getSelectedRow();
			if (CLP_BSCINFO.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
					TParm parm = new TParm();
					parm.setData("REGION_CODE", REGION_CODE.getValue());
					parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deleteBscInfo",
							parm);
					// �жϴ���ֵ
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("ɾ��ʧ�ܣ�");
						return;
					} else {
						this.messageBox("ɾ���ɹ���");
						CLP_BSCINFO.removeRow(selectedRow);
						/**
						 * ��ɾ����������Ϊ�㣬�������ǰ��Ϣ
						 * ��ɾ���������һ�У��򽹵�ת�Ƶ���ɾ���е���һ�У���ɾ��������һ�У�
						 * ��ɾ���Ĳ������һ�У��򽹵�ת�Ƶ���ɾ���е���һ�У��ȵ�ǰ�кţ�
						 */
						if (CLP_BSCINFO.getRowCount() <= 0) {
							onClear();
						} else if (selectedRow >= CLP_BSCINFO.getRowCount()) {
							// ����ѡ����
							CLP_BSCINFO.setSelectedRow(CLP_BSCINFO
									.getRowCount() - 1);
							// �����е�ֵ��䵽��Ӧ���������
							onTableClickedForBscInfo(CLP_BSCINFO.getRowCount() - 1);
						} else {
							// ���ô���Ϊѡ��״̬
							CLP_BSCINFO.setSelectedRow(selectedRow);
							// �����е�ֵ��䵽��Ӧ���������
							onTableClickedForBscInfo(selectedRow);
						}
						return;
					}
				}
			}
		} else if (selectedIndex == 1) {
			// ����ʱ��
			int selectedRow = CLP_THRPYSCHDM.getSelectedRow();
			if (CLP_THRPYSCHDM.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
					TParm parm = new TParm();
					parm.setData("REGION_CODE", REGION_CODE.getValue());
					parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.setData("SCHD_CODE", SCHD_CODE.getValue());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deleteThrpyschdm",
							parm);
					// �жϴ���ֵ
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("ɾ��ʧ�ܣ�");
						return;
					} else {
						this.messageBox("ɾ���ɹ���");
						CLP_THRPYSCHDM.removeRow(selectedRow);
						/**
						 * ��ɾ����������Ϊ�㣬�������ǰ��Ϣ
						 * ��ɾ���������һ�У��򽹵�ת�Ƶ���ɾ���е���һ�У���ɾ��������һ�У�
						 * ��ɾ���Ĳ������һ�У��򽹵�ת�Ƶ���ɾ���е���һ�У��ȵ�ǰ�кţ�
						 */
						if (CLP_THRPYSCHDM.getRowCount() <= 0) {
							onClear();
						} else if (selectedRow >= CLP_THRPYSCHDM.getRowCount()) {
							// ����ѡ����
							CLP_THRPYSCHDM.setSelectedRow(CLP_THRPYSCHDM
									.getRowCount() - 1);
							// �����е�ֵ��䵽��Ӧ���������
							onTableClickedForThrpyschdm(CLP_THRPYSCHDM
									.getRowCount() - 1);
						} else {
							// ���ô���Ϊѡ��״̬
							CLP_THRPYSCHDM.setSelectedRow(selectedRow);
							// �����е�ֵ��䵽��Ӧ���������
							onTableClickedForThrpyschdm(selectedRow);
						}
					}
				}
			}
		} else if (selectedIndex == 2) {
			// ҽ���ײ�
			int selectedRow = CLP_PACK01.getSelectedRow();
			CLP_PACK01.acceptText();
			TParm data = CLP_PACK01.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK01.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK01.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
					// ����parm
					TParm parmsend = parmFormatForPack("Y");// �ò���û�����ã��ɺ��ԣ���ʹ�ø�parm�е�������Ϣ
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// �жϴ���ֵ
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("ɾ��ʧ�ܣ�");
						return;
					} else {
						this.messageBox("ɾ���ɹ���");
						onQuery();
						// һ��Ҫ��ѯ���ٸ���״̬�������ڵ���ǰ��onQuery
						resetVersionAndActiveFlg(CLP_PACK01, "N");
						if (CLP_PACK01.getRowCount() > 0) {
							CLP_PACK01
									.setSelectedRow(CLP_PACK01.getRowCount() - 1);
						}
					}
				}
			}
		} else if (selectedIndex == 3) {
			// �ؼ������ײ�
			int selectedRow = CLP_PACK02.getSelectedRow();
			CLP_PACK02.acceptText();
			TParm data = CLP_PACK02.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK02.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK02.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
					// ����parm
					TParm parmsend = parmFormatForPack("N"); // �ò���û�����ã��ɺ��ԣ���ʹ�ø�parm�е�������Ϣ
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// �жϴ���ֵ
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("ɾ��ʧ�ܣ�");
						return;
					} else {
						this.messageBox("ɾ���ɹ���");
						onQuery();
						resetVersionAndActiveFlg(CLP_PACK02, "N");
						if (CLP_PACK02.getRowCount() > 0) {
							CLP_PACK02
									.setSelectedRow(CLP_PACK02.getRowCount() - 1);
						}
					}
				}
			}
		} else if (selectedIndex == 4) {
			// ����ƻ�
			int selectedRow = CLP_PACK03.getSelectedRow();
			CLP_PACK03.acceptText();
			TParm data = CLP_PACK03.getParmValue();
			TParm parm = new TParm();
			for (int i = 0; i < CLP_PACK03.getRowCount(); i++) {
				String selFlg = data.getValue("SEL_FLG", i);
				if ("Y".equals(selFlg)) {
					parm.addData("REGION_CODE", REGION_CODE.getValue());
					parm.addData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
					parm.addData("SCHD_CODE", SCHD_CODE.getValue());
					parm.addData("ORDER_TYPE", data.getValue("ORDER_TYPE", i));
					parm.addData("ORDER_CODE", data.getValue("ORDER_CODE", i));
					parm.addData("CHKTYPE_CODE", data.getValue("CHKTYPE_CODE",
							i));
					parm.addData("ORDER_SEQ_NO", data.getValue("ORDER_SEQ_NO",
							i));
				}
			}
			if (parm.getCount("REGION_CODE") <= 0) {
				return;
			}
			if (CLP_PACK03.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ�ɾ����", 2) == 0) {
					// ����parm
					TParm parmsend = parmFormatForPack("O");// �ò���û�����ã��ɺ��ԣ���ʹ�ø�parm�е�������Ϣ
					if ("Y".equals(ACTIVE_FLG.getValue())) {
						parmsend.setData("CLP_BSCINFO", getUseParm("Y")
								.getData());
					} else {
						parmsend.setData("CLP_BSCINFO", getUseParm("N")
								.getData());
					}
					parmsend.setData("saveMap", parm.getData());
					TParm result = TIOM_AppServer.executeAction(
							"action.clp.CLPBscInfoAction", "deletePack",
							parmsend);
					// �жϴ���ֵ
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						this.messageBox("ɾ��ʧ�ܣ�");
						return;
					} else {
						this.messageBox("ɾ���ɹ���");
						onQuery();
						resetVersionAndActiveFlg(CLP_PACK03, "N");
						if (CLP_PACK03.getRowCount() > 0) {
							CLP_PACK03
									.setSelectedRow(CLP_PACK03.getRowCount() - 1);
						}
					}
				}
			}
		}
	}

	/**
	 * �������ñ�ʶ
	 * 
	 * @param ACTIVE_FLG
	 *            String Y:���� N:����
	 * @return TParm
	 */
	public TParm getUseParm(String activeFlg) {
		TParm parm = new TParm();
		parm.setData("REGION_CODE", REGION_CODE.getValue());
		parm.setData("CLNCPATH_CODE", CLNCPATH_CODE.getValue());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", dateFormat.format(SystemTool.getInstance()
				.getDate()));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("ACTIVE_FLG", activeFlg);
		parm.setData("VERSION", VERSION.getValue());
		return parm;
	}

	/**
	 * ����ʱ�������������<=��׼סԺ����
	 * 
	 * @param clncPathCode
	 *            String
	 * @return boolean
	 */
	public boolean checkStayHospDays(String clncPathCode) {
		int schdDay = 0;
		// String sql =
		// " SELECT SUM(SCHD_DAY) AS SCHD_DAY FROM CLP_THRPYSCHDM WHERE"
		// + " CLNCPATH_CODE = '" + clncPathCode + "'";
		// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// schdDay = result.getInt("SCHD_DAY", 0);
		// luhai modify begin 20110629
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append("SELECT SCHD_DAY,SUSTAINED_DAYS FROM CLP_THRPYSCHDM  ");
		sqlbf.append(" WHERE  CLNCPATH_CODE='" + clncPathCode + "'");
		sqlbf.append("ORDER BY SUSTAINED_DAYS");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sqlbf.toString()));
		for (int i = 0; i < result.getCount(); i++) {
			TParm rowParm = result.getRow(i);
			if (i == 0) {
				schdDay += rowParm.getInt("SCHD_DAY");
				continue;
			}
			// �ǵ�һ������
			TParm beforeRowParm = result.getRow(i - 1);
			int nowSusTainedDay = rowParm.getInt("SUSTAINED_DAYS");
			int beforeSusTainedDay = beforeRowParm.getInt("SUSTAINED_DAYS");
			schdDay += rowParm.getInt("SCHD_DAY")
					+ ((nowSusTainedDay - beforeSusTainedDay) <= 0 ? (nowSusTainedDay
							- beforeSusTainedDay - 1)
							: 0);

		}
		// luhai modify end 20110629
		int stayHospDays = Integer.parseInt(STAYHOSP_DAYS.getValue());
		if (schdDay != stayHospDays) {
			this.messageBox("ʱ���������ͱ�׼סԺ������һ�£�");
			return false;
		}
		return true;
	}

	/**
	 * ����
	 */
	public void onUse() {
		// ��ȡҳǩ����
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		if (selectedIndex == 0) {
			// ���ٴ�·���Ѿ�����
			if ("Y".equals(ACTIVE_FLG.getValue())) {
				return;
			}
			// ʱ�����������ڱ�׼סԺ����
			if (!checkStayHospDays(CLNCPATH_CODE.getValue())) {
				return;
			}
			// �ٴ�·�����
			int selectedRow = CLP_BSCINFO.getSelectedRow();
			if (CLP_BSCINFO.getRowCount() > 0 && selectedRow >= 0) {
				if (this.messageBox("ѯ��", "�Ƿ����ã�", 2) == 0) {
					TParm result = BscInfoTool.getInstance().use(
							getUseParm("Y"));
					if (result.getErrCode() >= 0) {
						// �����ǰ��Ϣ
						onClear();
						// ģ����ѯʱ��
						onQuery();
						this.messageBox("���óɹ���");
						// ���ô���Ϊѡ��״̬
						CLP_BSCINFO.setSelectedRow(selectedRow);
						// �����е�ֵ��䵽��Ӧ���������
						onTableClickedForBscInfo(selectedRow);
					} else {
						this.messageBox("����ʧ�ܣ�");
					}
				}
			}
		}
	}

	// /**
	// * ��Ͽ�popo
	// */
	// public void popoDiagnose(){
	// TParm parm = new TParm();
	// TTextField textfield = (TTextField) this.getComponent("diagnose_desc");
	// textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
	// "%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
	// // ����text���ӽ���sys_fee�������ڵĻش�ֵ
	// textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
	// "popDiagReturn");
	// }

	/**
	 * ���
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("diagnose", icdCode);
		this.setValue("diagnose_desc", icdDesc);
	}

	/**
	 * ���end
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popDiagReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("diagnose_end", icdCode);
		this.setValue("diagnose_desc_end", icdDesc);
	}

	// /**
	// * �������popo
	// */
	// public void popoOperatorDiagnose() {
	// TParm parm = new TParm();
	// TTextField textfield =
	// (TTextField)this.getComponent("operation_diagnose_desc");
	// textfield.setPopupMenuParameter("ICD", getConfigParm().newConfig(
	// "%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
	// // ����text���ӽ���sys_fee�������ڵĻش�ֵ
	// textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
	// "popOperatorDiagReturn");
	// }

	/**
	 * �����������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("operation_diagnose", icdCode);
		this.setValue("operation_diagnose_desc", icdDesc);
	}

	/**
	 * �����������end
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popOperatorDiagReturnEnd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		this.setValue("operation_diagnose_end", icdCode);
		this.setValue("operation_diagnose_desc_end", icdDesc);
	}

	/**
	 * ��TParm�м���ϵͳĬ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm) {
		int total = parm.getCount();
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", Operator.getIP());
	}

	/**
	 * �õ���ǰʱ��
	 * 
	 * @param dataForatStr
	 *            String
	 * @return String
	 */
	private String getDateStr(String dataForatStr) {
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, dataForatStr);
		return datestr;
	}

	/**
	 * �õ���ǰʱ�䣨Ĭ�Ϸ�����������Ϣ��
	 * 
	 * @return String
	 */
	private String getDateStr() {
		return getDateStr("yyyyMMdd");
	}

	/**
	 * ������֤����
	 * 
	 * @param validData
	 *            String
	 * @return boolean
	 */
	private boolean validNumber(String validData) {
		Pattern p = Pattern.compile("[0-9]{1,}");
		Matcher match = p.matcher(validData);
		if (!match.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * �õ�int��ֵ
	 * 
	 * @param str
	 *            String
	 * @return int
	 */
	private int getIntValue(String str) {
		int tmp = 0;
		try {
			tmp = Integer.parseInt(str);
		} catch (Exception e) {
			tmp = 0;
		}
		return tmp;
	}

	class comparatorDuration implements Comparator {
		public int compare(Object obj1, Object obj2) {
			int returnvalue = 0;
			Map<String, Integer> objMap1 = (HashMap<String, Integer>) obj1;
			Integer startDay1 = objMap1.get("currentStartDay");
			Map<String, Integer> objMap2 = (HashMap<String, Integer>) obj2;
			Integer startDay2 = objMap2.get("currentStartDay");
			return startDay1.compareTo(startDay2);
		}

	}

	/**
	 * �õ�ָ��table��ѡ����
	 * 
	 * @param tableName
	 *            String
	 * @return int
	 */
	private int getSelectedRow(String tableName) {
		int selectedIndex = -1;
		if (tableName == null || tableName.length() <= 0) {
			return -1;
		}
		Object componentObj = this.getComponent(tableName);
		if (!(componentObj instanceof TTable)) {
			return -1;
		}
		TTable table = (TTable) componentObj;
		selectedIndex = table.getSelectedRow();
		return selectedIndex;
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * ȡ�ý���TTextField
	 * 
	 * @param tag
	 *            String
	 * @return TTextField
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * ����Operator�õ�map
	 * 
	 * @return Map
	 */
	private Map getBasicOperatorMap() {
		Map map = new HashMap();
		map.put("REGION_CODE", Operator.getRegion());
		map.put("OPT_USER", Operator.getID());
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		map.put("OPT_DATE", datestr);
		map.put("OPT_TERM", Operator.getIP());
		return map;
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
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
		CLP_BSCINFO.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
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
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = CLP_BSCINFO.getParmValue();
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
				String tblColumnName = CLP_BSCINFO.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �һ�MENU�����¼�
	 * 
	 * @param tableName
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("CLP_PACK01");
		TTabbedPane tab = (TTabbedPane) this.getComponent("tTabbedPane_0");
		int selectIndex = tab.getSelectedIndex() + 1;
		if (selectIndex != 3)
			return;
		TParm action = this.CLP_PACK01.getParmValue().getRow(
				CLP_PACK01.getSelectedRow());
		if (null != action.getValue("CAT1_TYPE")
				&& ("LIS".equals(action.getValue("CAT1_TYPE")) || "RIS"
						.equals(action.getValue("CAT1_TYPE")))) {
			table.setPopupMenuSyntax("��ʾ����ҽ��ϸ��,openRigthPopMenu");
		} else {
			table.setPopupMenuSyntax("");
			return;
		}

	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �򿪼���ҽ��ϸ���ѯ
	 */
	public void openRigthPopMenu() {

		TParm action = this.CLP_PACK01.getParmValue().getRow(
				CLP_PACK01.getSelectedRow());
		String orderCode = action.getValue("ORDER_CODE");
		if (StringUtil.isNullString(orderCode)) {
			System.out
					.println("OpdOrder->getOrderSetDetails->orderSetCode is invalie");
			return;
		}
		String sql = " SELECT A.ORDERSET_CODE,A.ORDER_CODE,B.ORDER_DESC,B.OWN_PRICE,B.SPECIFICATION,A.DOSAGE_QTY,B.UNIT_CODE"
				+ " FROM SYS_ORDERSETDETAIL A,SYS_FEE B WHERE A.ORDER_CODE =B.ORDER_CODE AND A.ORDERSET_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = parm.getCount();
		if (count <= 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return;
		}
		TParm result = getOrderSetDetails(parm);
		// this.messageBox_("����ҽ��ϸ��"+parm);
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(TParm parm) {
		TParm result = new TParm();
		int count = parm.getCount();
		if (count < 0) {
			// System.out.println("OpdOrder->getOrderSetDetails->count <  0");
			return result;
		}
		// TDS ds = odiObject.getDS("ODI_ORDER");
		// String buff = ds.isFilter() ? ds.FILTER : ds.PRIMARY;
		// TParm parm = ds.getBuffer(buff);

		// System.out.println("groupNo=-============" + groupNo);
		// System.out.println("orderSetCode===========" + orderSetCode);
		// System.out.println("count===============" + count);
		// temperrϸ��۸�
		for (int i = 0; i < count; i++) {
			// tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
			// System.out.println("tempCode==========" + tempCode);
			// System.out.println("tempNO============" + tempNo);
			// System.out.println("setmain_flg========" +
			// parm.getBoolean("SETMAIN_FLG", i));
			// ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
			result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
			result.addData("MEDI_UNIT", parm.getValue("UNIT_CODE", i));
			// ��ѯ����
			// TParm ownPriceParm = new TParm(this.getDBTool().select(
			// "SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"
			// + parm.getValue("ORDER_CODE", i) + "'"));
			// this.messageBox_(ownPriceParm);
			// �����ܼ۸�
			double ownPrice = parm.getDouble("OWN_PRICE", i)
					* parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result.addData("EXEC_DEPT_CODE", "");
			result.addData("OPTITEM_CODE", "");
			result.addData("INSPAY_TYPE", "");
		}
		return result;
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
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
		CLP_PACK01.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
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
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
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
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = CLP_PACK01.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = CLP_PACK01.getParmMap(sortColumnOne);
				// ת��parm�е���
				int col = tranParmColIndexOne(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// ��������vectorת��parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * ����EXCEL
	 */
	public void onExport() {
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		switch (selectedIndex){
		case 0:		
			ExportExcelUtil.getInstance().exportExcel(CLP_BSCINFO,
			"�ٴ�·�����");
			break;
		case 1:
			ExportExcelUtil.getInstance().exportExcel(CLP_THRPYSCHDM,
			"����ʱ��");
			break;
		case 2:
			ExportExcelUtil.getInstance().exportExcel(CLP_PACK01,
			"ҽ���ײ�-���:"+this.getText("CLNCPATH_CODE02")+" ʱ��:"+this.getText("SCHD_CODE01"));
			break;
		}
	}
}
