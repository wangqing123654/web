package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jdo.device.CallNo;
import jdo.device.LAB_Service;
import jdo.device.LAB_Service.DynamecMassage;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTpreDebtTool;
import jdo.hrm.HRMContractD;
import jdo.opd.OPDSysParmTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatRegClinicArea;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;
import com.javahis.xml.Item;
import com.javahis.xml.Job;

/**
 * <p>
 * Title: ���������ӡ
 * </p>
 * 
 * <p>
 * Description: ���������ӡ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class MEDApplyControl extends TControl {
	/**
	 * ����������
	 */
	private String actionName = "action.med.MedAction";

	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	/**
	 * �ż�ס��
	 */
	private String admType;
	/**
	 * ����
	 */
	private String deptCode;
	/**
	 * ���￴������סԺΪ��ǰ����
	 */
	private Timestamp admDate;
	/**
	 * �����
	 */
	private String caseNo = "";
	/**
	 * ������
	 */
	private String mrNo;
	/**
	 * ��������
	 */
	private String patName;
	/**
	 * סԺ��
	 */
	private String ipdNo;
	/**
	 * ����
	 */
	private String bedNo;
	/**
	 * ����
	 */
	private String stationCode;
	/**
	 * ����
	 */
	private String clinicareaCode;
	/**
	 * ����
	 */
	private String clinicroomNo;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	/**
	 * ������롢��ͬ����
	 */
	private String companyCode, contractCode;// add by wanglong 20121214

	/**
	 * ��ͬ����
	 */
	private HRMContractD contractD;// add by wanglong 20121214
	/**
	 * ��ͬTTextFormat
	 */
	private TTextFormat contract;// add by wanglong 20121214

	/**
	 * ���������
	 */
	private StringBuffer printText = new StringBuffer();// wanglong add 20140610
	/**
	 * �ҺŲ���������Ч������ѯsql liling 20140825 add
	 */
	private String effectDaySql = "SELECT  EFFECT_DAYS  FROM REG_SYSPARM";
	private int effectDay = 0;// liling 20140825 add
	private String printCode = "";// ��¼��ӡ����� yanjing 20140919
	private String execBarCode = "";// ��¼����������� yanjing 20140919
	TTextFormat clinicAreaCode;// ����add by yanjing 20151104

	public void onInit() {
		super.onInit();
		contractD = new HRMContractD();// add by wanglong 20121214
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		/**
		 * REG_CLINICAREA���� REG_CLINICROOM���� (סԺCOMBOȨ��)(����Ȩ��)(�ż�ס��Ȩ��)
		 */
		// ================pangben modify 20110405 start ��������
		setValue("REGION_CODE", Operator.getRegion());
		// ================pangben modify 20110405 stop
		// ========pangben modify 20110421 start Ȩ�����
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(effectDaySql));// liling
																				// 20140825
																				// add
		if (parm1.getCount() > 0) {
			effectDay = parm1.getInt("EFFECT_DAYS", 0);// liling 20140825 add
		}
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setDeptCode(parm.getValue("DEPT_CODE"));
				this.setCaseNo(parm.getValue("CASE_NO"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setPatName(parm.getValue("PAT_NAME"));
				this.setAdmDate(parm.getTimestamp("ADM_DATE"));
				if (!StringUtil.isNullString(parm.getValue("COMPANY_CODE"))) {// add
																				// by
																				// wanglong
																				// 20130726
					this.setCompanyCode(parm.getValue("COMPANY_CODE"));
				}
				if (!StringUtil.isNullString(parm.getValue("CONTRACT_CODE"))) {// add
																				// by
																				// wanglong
																				// 20130726
					this.setContractCode(parm.getValue("CONTRACT_CODE"));
				}
				if ("I".equals(this.getAdmType())) {
					this.setIpdNo(parm.getValue("IPD_NO"));
					this.setStationCode(parm.getValue("STATION_CODE"));
					this.setBedNo(parm.getValue("BED_NO"));
				} else {
					this.setClinicareaCode(parm.getValue("CLINICAREA_CODE"));
					this.setClinicroomNo(parm.getValue("CLINICROOM_NO"));
				}
				if (parm.getValue("POPEDEM").length() != 0) {
					// һ��Ȩ��
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// ��ɫȨ��
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// ���Ȩ��
					if ("3".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSDBA", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSOPERATOR", false);
					}
				}
			} else {
				this.setAdmType("" + obj);
				this.setDeptCode(Operator.getDept());
				this.setStationCode(Operator.getStation());
				this.setClinicareaCode(Operator.getStation());

				// ==liling 20140717 add start ��������Ϊ����-3 ������===
				Date d = new Date(SystemTool.getInstance().getDate().getTime());
				String date = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + (d.getDate() - effectDay)
						+ " 00:00:00";//
				this.setAdmDate(getTimestamp(date));
				// ==liling 20140717 add end ��������Ϊ����-3 ������===3�滻ΪeffectDay
				this.setDeptCode(Operator.getDept());
				this.setClinicareaCode(Operator.getStation());
			}

		} else {

		}
		/**
		 * ��ʼ��Ȩ��
		 */
		onInitPopeDem();
		/**
		 * ��ʼ��ҳ��
		 */
		onApplyCheck();
		/**
		 * ��ʼ���¼�
		 */
		initEvent();
	}

	/**
	 * ��ʼ���¼�
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxValue");
		// �������
		addListener(getTTable(TABLE));
	}

	/**
	 * ��ʼ������
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("APPLICATION_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("APPLICATION_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N" : "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * ѡ���¼�
	 */
	public void onSelRadioButton(Object obj) {
		boolean applyFlg = false;// ����ִ�а�ť�Ƿ�ѡ
		if (this.getValueBoolean("APPLY_FLG")) {
			applyFlg = true;
		} else {
			applyFlg = false;
		}
		this.onClear();
		if (!applyFlg) {
			if ("O".equals("" + obj)) {
				callFunction("UI|print|setEnabled", true);
				callFunction("UI|send|setEnabled", true);
				callFunction("UI|apply|setEnabled", true);

				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
				this.getTTable(TABLE).setHeader(
						"ѡ,30,boolean;��,30,boolean;����,70;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
				this.getTTable(TABLE).setParmMap(
						"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
			}
			if ("E".equals("" + obj)) {
				callFunction("UI|print|setEnabled", true);
				callFunction("UI|send|setEnabled", true);
				callFunction("UI|apply|setEnabled", true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
				this.getTTable(TABLE).setHeader(
						"ѡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
				this.getTTable(TABLE).setParmMap(
						"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
			}
			if ("I".equals("" + obj)) {
				callFunction("UI|print|setEnabled", true);
				callFunction("UI|send|setEnabled", true);
				callFunction("UI|apply|setEnabled", false);
				//
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(true);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(true);
				getTTextField("BED_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121214
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
				this.getTTable(TABLE).setHeader(
						"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;ҽʦ��ע,100;����ҽ��,70,ORDER_DR_CODE;����,100,STATION_CODE;����,100,BED_NO;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ��,140");
				this.getTTable(TABLE).setParmMap(
						"FLG;PRINT_FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;DR_NOTE;ORDER_DR_CODE;STATION_CODE;BED_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");
			}
			if ("H".equals("" + obj)) {
				callFunction("UI|print|setEnabled", true);
				callFunction("UI|send|setEnabled", true);
				callFunction("UI|apply|setEnabled", true);

				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121214
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);// add-end
				this.getTTable(TABLE).setHeader(
						"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;���,50;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70,ORDER_DR_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ�䣬140");// caowl
				// 20130320
				// ����Ա�����
				this.getTTable(TABLE).setParmMap(
						"FLG;PRINT_FLG;URGENT_FLG;NO;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR_CODE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");// caowl
				// 20130320
				// ����Ա�����
			}
			this.onQuery();
		} else {
			this.initApplyPage();
		}
	}

	/**
	 * ��ʼ��Ȩ��
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("I").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(false);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
		}
		if (this.getPopedem("SYSOPERATOR")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("I").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(true);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(true);
				getTTextField("BED_NO").setEnabled(true);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				// �������û�ɫ
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);// add-end
			}
		}
		if (this.getPopedem("SYSDBA")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("E".equals(this.getAdmType())) {
				this.getTRadioButton("E").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("I".equals(this.getAdmType())) {
				this.getTRadioButton("I").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(true);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(true);
				getTTextField("BED_NO").setEnabled(true);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);// add-end
			}
			if ("H".equals(this.getAdmType())) {
				this.getTRadioButton("H").setSelected(true);
				this.getTRadioButton("H").setEnabled(true);
				this.getTRadioButton("O").setEnabled(true);
				this.getTRadioButton("E").setEnabled(true);
				this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(true);// add-by-wanglong-20121213
				getTTextFormat("CONTRACT_CODE").setEnabled(true);
				getTTextField("START_SEQ_NO").setEnabled(true);
				getTTextField("END_SEQ_NO").setEnabled(true);// add-end
			}
		}
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String endDay = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd") + " 23:59:59";// ==liling
																												// 20140825
																												// add
		this.setValue("START_DATE", this.getAdmDate());
		this.setValue("END_DATE", getTimestamp(endDay));
		if ("O".equals(this.getAdmType())) {// ���//===liling 20140717 add
											// ����ҽ��,70,ORDER_DR_CODE;����������ǰ�棬�����뱨������еĿ�ȱ�С��ҽ���зŴ�
			this.callFunction("UI|execute|setVisible", true);// wanglong add
																// 20141212
			this.callFunction("UI|cancel|setVisible", true);
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;����,40,boolean;ӡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70,ORDER_DR_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ��,140");// yanjing
			// 20140319
			this.getTTable(TABLE).setParmMap(
					"FLG;EXEC_FLG;PRINT_FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR_CODE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");
		}
		if ("E".equals(this.getAdmType())) {// ===liling 20140717 add
											// ����ҽ��,70,ORDER_DR_CODE;
			this.callFunction("UI|execute|setVisible", true);// wanglong add
																// 20141212
			this.callFunction("UI|cancel|setVisible", true);
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;����,40,boolean;ӡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70,ORDER_DR_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ��,140");
			this.getTTable(TABLE).setParmMap(
					"FLG;EXEC_FLG;PRINT_FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR_CODE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");
		}
		if ("I".equals(this.getAdmType())) {// ===liling 20140717 add
											// ����ҽ��,70,ORDER_DR_CODE;
			this.callFunction("UI|execute|setVisible", false);// wanglong add
																// 20141212
			this.callFunction("UI|cancel|setVisible", false);
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("IPD_NO", this.getIpdNo());
			this.setValue("BED_NO", this.getBedNo());
			if (this.getMrNo() != null) {
				this.setValue("DEPT_CODEMED", this.getDeptCode());
				this.setValue("STATION_CODEMED", this.getStationCode());
			} else {
				if (this.getPopedem("NORMAL")) {
					this.setValue("USER_ID", Operator.getID());
					this.setValue("STATION_CODEMED", Operator.getStation());
					callFunction("UI|USER_ID|onQuery");
					callFunction("UI|STATION_CODEMED|onQuery");
				}
			}
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;ҽʦ��ע,100;����ҽ��,70,ORDER_DR_CODE;����,100,STATION_CODE;����,100,BED_NO;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ��,140");
			this.getTTable(TABLE).setParmMap(
					"FLG;PRINT_FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;DR_NOTE;ORDER_DR_CODE;STATION_CODE;BED_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");
		}
		if ("H".equals(this.getAdmType())) {// ===liling 20140717 add
											// ����ҽ��,70,ORDER_DR_CODE;
			this.callFunction("UI|execute|setVisible", false);// wanglong add
																// 20141212
			this.callFunction("UI|cancel|setVisible", false);
			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate() + " 00:00:00";// caowl
																											// 20130305
																											// �����Ĭ������Ϊǰһ����
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate() + " 23:59:59";

			// ��ʼ��ʱ��
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� end */

			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.setValue("COMPANY_CODE", this.getCompanyCode());// add by
																	// wanglong
																	// 20130726
			if (!StringUtil.isNullString(this.getCompanyCode())) {
				TParm contractParm = contractD.onQueryByCompany(this.getCompanyCode());// add-by-wanglong-20130821
				contract.setPopupMenuData(contractParm);
				contract.setComboSelectRow();
				contract.popupMenuShowData();
				contract.setValue(this.getContractCode()); // add-end
			}
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;���,50;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70,ORDER_DR_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,160,DEV_CODE;סԺ��,100;ʱ��,80;��ӡʱ��,140");// caowl
																																																																												// 20130320
																																																																												// ����Ա�����
			this.getTTable(TABLE).setParmMap(
					"FLG;PRINT_FLG;URGENT_FLG;NO;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR_CODE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;IPD_NO;TIME_LIMIT;PRINT_DATE");// caowl
																																																											// 20130320
																																																											// ����Ա�����
		}
	}

	public static Timestamp getTimestamp(String time) {
		Date date = new Date();
		// ע��format�ĸ�ʽҪ������String�ĸ�ʽ��ƥ��
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			date = sdf.parse(time);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �õ�TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * ����TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * �õ�TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
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
	 * ��ѯ
	 */
	public void onQuery() {
		boolean queryFlg = false;
		queryFlg = this.getValueBoolean("APPLY_FLG");
		if (queryFlg) {
			this.applyQuery();// �������빴ѡ
		} else {
			this.printQuery();// ��������δ��ѡ
		}
	}

	/**
	 * �����ӡ��ѯ����
	 */
	public void printQuery() {

		if (this.getValueString("MR_NO").trim().length() != 0) {

			this.setValue("MR_NO", PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
			this.setValue("PAT_NAME", PatTool.getInstance()
					.getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
			this.setMrNo(this.getValueString("MR_NO"));
			this.setPatName(this.getValueString("PAT_NAME"));
			this.setClinicareaCode(this.getValueString("CLINICAREA_CODEMED"));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "O");
				// System.out.println("+++++====++++ patInfParm patInfParm is ::"+patInfParm);
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());

				}
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());

				}
			}

			if ("I".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "I");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setValue("STATION_CODEMED", patInfParm.getValue("STATION_CODE"));
				this.setValue("IPD_NO", patInfParm.getValue("IPD_NO"));
				this.setValue("BED_NO", patInfParm.getValue("BED_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				// this.setValue("DEPT_CODEMED",
				// patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getQuerySQL();
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			culmulateAge(action);// ����ļ���
			this.getTTable(TABLE).setParmValue(action);
			return;
		}
		if (this.getValueString("IPD_NO").trim().length() != 0) {
			this.setValue("IPD_NO", PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO")));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this.getValueString("IPD_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this.getValueString("IPD_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("I".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this.getValueString("IPD_NO"), "I");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setValue("STATION_CODEMED", patInfParm.getValue("STATION_CODE"));
				this.setValue("IPD_NO", patInfParm.getValue("IPD_NO"));
				this.setValue("BED_NO", patInfParm.getValue("BED_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("IPD_NO", this.getValueString("IPD_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getQuerySQL();
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			culmulateAge(action);// ����ļ���
			this.getTTable(TABLE).setParmValue(action);
			return;
		}
		String sql = getQuerySQL();
		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
			sql = getHRMQuerySQL();
		}
		TParm action = new TParm(this.getDBTool().select(sql));
		// System.out.println("sql sql sql is ::"+sql);
		culmulateAge(action);// ����ļ���
		this.getTTable(TABLE).setParmValue(action);
		// ÿ�β�ѯ���ȫѡ
		this.setValue("ALLCHECK", "N");
	}

	/**
	 * ��������ķ��� yanjing 20140404
	 * 
	 */
	private void culmulateAge(TParm action) {
		// ���ݳ������ں͵�ǰ�����ڼ�������
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < action.getCount(); i++) {
			TParm action1 = action.getRow(i);
			String age = "";
			if (!"".equals(action1.getTimestamp("BIRTH_DATE").toString())
					&& !action1.getTimestamp("BIRTH_DATE").toString().equals(null)) {
				age = DateUtil.showAge(action1.getTimestamp("BIRTH_DATE"), sysDate);
			}
			action.setData("AGE", i, age);
		}
	}

	/**
	 * ȫѡ
	 */
	public void onSelAll() {
		TParm parm = this.getTTable(TABLE).getParmValue();
		int rowCount = parm.getCount();
		for (int i = 0; i < rowCount; i++) {
			if (this.getTCheckBox("ALLCHECK").isSelected())
				parm.setData("FLG", i, "Y");
			else
				parm.setData("FLG", i, "N");
		}
		this.getTTable(TABLE).setParmValue(parm);
	}

	/**
	 * �õ�TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ���ز�ѯ�������
	 * 
	 * @param columnName
	 *            String
	 * @param value
	 *            String
	 * @return TParm
	 */
	public TParm getPatInfo(String columnName, String value, String admType) {
		TParm result = new TParm();
		if ("O".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm oParm = new TParm(this.getDBTool()
						.select("SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				// System.out.println("998877caseNO is ::"+this.getCaseNo());
				// System.out.println("---------uuuuuu------ is ::"+"SELECT
				// ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE
				// CASE_NO='"
				// + this.getCaseNo() + "'");
				result.setData("CASE_NO", oParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", oParm.getData("CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", oParm.getData("CLINICROOM_NO", 0));
				TParm oIparm = new TParm(
						this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value + "'"));
				result.setData("PAT_NAME", oIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool()
					.select("SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE " + columnName
							+ "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "O");
				Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData("CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData("CLINICROOM_NO", 0));
			}

		}
		if ("E".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm eParm = new TParm(this.getDBTool()
						.select("SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", eParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", eParm.getData("CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", eParm.getData("CLINICROOM_NO", 0));
				TParm eIparm = new TParm(
						this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value + "'"));
				result.setData("PAT_NAME", eIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool()
					.select("SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE " + columnName
							+ "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "E");
				Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData("CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData("CLINICROOM_NO", 0));
			}
		}
		if ("I".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm iParm = new TParm(this.getDBTool().select(
						"SELECT IN_DATE AS ADM_DATE,CASE_NO,DEPT_CODE,STATION_CODE,IPD_NO,BED_NO FROM ADM_INP WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", iParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", iParm.getData("DEPT_CODE", 0));
				result.setData("STATION_CODE", iParm.getData("STATION_CODE", 0));
				result.setData("IPD_NO", iParm.getData("IPD_NO", 0));
				result.setData("BED_NO", iParm.getData("BED_NO", 0));
				TParm iIparm = new TParm(
						this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value + "'"));
				result.setData("PAT_NAME", iIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT IN_DATE AS ADM_DATE,CASE_NO,DEPT_CODE,STATION_CODE,IPD_NO,BED_NO FROM ADM_INP WHERE "
							+ columnName + "='" + value + "' AND DS_DATE IS NULL AND BED_NO IS NOT NULL"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "I");
				Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
					result.setData("STATION_CODE", temp.getData("STATION_CODE"));
					result.setData("IPD_NO", temp.getData("IPD_NO"));
					result.setData("BED_NO", temp.getData("BED_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
				result.setData("STATION_CODE", queryParm.getData("STATION_CODE", 0));
				result.setData("IPD_NO", queryParm.getData("IPD_NO", 0));
				result.setData("BED_NO", queryParm.getData("BED_NO", 0));
			}
		}
		if ("H".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm hParm = new TParm(this.getDBTool()
						.select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE CASE_NO='"
								+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", hParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", hParm.getData("DEPT_CODE", 0));
				TParm hIparm = new TParm(
						this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value + "'"));
				result.setData("PAT_NAME", hIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(
					this.getDBTool().select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE "
							+ columnName + "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "H");
				Object obj = this.openDialog("%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("DEPT_CODE", queryParm.getData("DEPT_CODE", 0));
			}
		}
		TParm parm = new TParm(
				this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='" + value + "'"));
		result.setData("PAT_NAME", parm.getData("PAT_NAME", 0));
		return result;
	}

	/**
	 * �õ���ѯ���
	 * 
	 * @return String
	 */
	public String getQuerySQL() {

		String sql = "SELECT ";
		if (this.getValueString("BAR_CODE_PRINT").length() > 0) {
			sql += " 'Y' AS FLG, ";
		} else {
			sql += " 'N' AS FLG, ";
		}
		if ("O".equals(getAdmTypeRadioValue()) || "E".equals(getAdmTypeRadioValue())) {// wanglong add 20141212
			sql += " (SELECT C.EXEC_FLG FROM OPD_ORDER C " + " WHERE A.CASE_NO = C.CASE_NO "
					+ " AND A.APPLICATION_NO = C.MED_APPLY_NO " + " AND A.ORDER_NO = C.RX_NO "
					+ " AND A.ORDER_CODE = C.ORDER_CODE " + " AND A.SEQ_NO = C.SEQ_NO) AS EXEC_FLG, ";
		}
		sql += // wanglong modify 20140423 BED_NO��Ϊ��ʾBED_NO_DESC//==liling
				// 20140717 add ORDER_DR_CODE ����ҽ��
				"A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO, C.PAT_NAME,A.APPLICATION_NO,"
						+ "A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,A.MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,"
						+ "TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE, C.BIRTH_DATE,A.ORDER_NO,"
						+ "A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,(SELECT  O.USER_NAME  FROM  SYS_OPERATOR  O  WHERE O.USER_ID=A.ORDER_DR_CODE ) ORDER_DR_CODE,A.ADM_TYPE,"
						+ "TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE, B.TUBE_TYPE,B.TIME_LIMIT FROM MED_APPLY A,SYS_FEE B, SYS_PATINFO C ";
		TParm queryParm = this.getParmQuery();
		String columnName[] = queryParm.getNames();
		if (columnName.length > 0)
			sql += " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.MR_NO = C.MR_NO(+) AND ";
		int count = 0;
		for (String temp : columnName) {
			if (temp.equals("END_DATE"))
				continue;
			if (temp.equals("START_DATE")) {
				if (count > 0)
					sql += " AND ";
				sql += " A.START_DTTM BETWEEN TO_DATE('" + queryParm.getValue("START_DATE")
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + queryParm.getValue("END_DATE")
						+ "','YYYYMMDDHH24MISS')";
				count++;
				continue;
			}

			if (count > 0)
				sql += " AND ";
			sql += "A." + temp + "='" + queryParm.getValue(temp) + "' ";
			count++;
		}
		if (count > 0 && this.getValueString("BAR_CODE_PRINT").length() != 0) {
			sql += " AND A.APPLICATION_NO IN (" + printCode + ") ";
		}
		if (count > 0)
			sql += " AND ";
		if ("H".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY A.CAT1_TYPE,A.CASE_NO";
		else if ("I".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,A.BED_NO";
		else if (!EKTIO.getInstance().ektAyhSwitch())
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,A.BED_NO";
		else
			sql += " A.CAT1_TYPE='LIS' ORDER BY A.CAT1_TYPE,A.CASE_NO,A.BED_NO";

		// add by huangtt 20160315 EXEC_FLGΪ��ʱ��˵��med_apply���������ݣ�
		// opd_order����û�����ݣ���ʱ�������ݽ�����ʾ����
		if ("O".equals(getAdmTypeRadioValue()) || "E".equals(getAdmTypeRadioValue())) {

			sql = "SELECT * FROM (" + sql + ") WHERE EXEC_FLG IS NOT NULL";
		}

		// System.out.println("11��ͨsql:"+sql);
		return sql;
	}

	/**
	 * �õ���ѯ����
	 * 
	 * @return TParm
	 */
	public TParm getParmQuery() {
		TParm result = new TParm();
		result.setData("ADM_TYPE", this.getAdmTypeRadioValue());
		result.setData("START_DATE", StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss"));
		result.setData("END_DATE", StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss"));
		if (this.getAdmType().equals("I") && this.getValueString("DEPT_CODEMED").length() != 0) {
			result.setData("DEPT_CODE", this.getValueString("DEPT_CODEMED"));
		}
		if (this.getValueString("STATION_CODEMED").length() != 0) {
			result.setData("STATION_CODE", this.getValueString("STATION_CODEMED"));
		}
		if (this.getValueString("CLINICAREA_CODEMED").length() != 0) {
			result.setData("CLINICAREA_CODE", this.getValueString("CLINICAREA_CODEMED"));
		}
		if (this.getValueString("CLINICROOM_CODEMED").length() != 0) {
			result.setData("CLINICROOM_NO", this.getValueString("CLINICROOM_CODEMED"));
		}
		if (getPrintStatus().length() != 0 && !this.getValueBoolean("APPLY_FLG")) {
			result.setData("PRINT_FLG", getPrintStatus());
		} else if (getPrintStatus().length() != 0 && this.getValueBoolean("APPLY_FLG")) {
			result.setData("PRINT_FLG", "Y");
		}
		if (this.getValueString("MR_NO").length() != 0) {
			result.setData("MR_NO", this.getValueString("MR_NO"));
		}
		if (this.getValueString("IPD_NO").length() != 0) {
			result.setData("IPD_NO", this.getValueString("IPD_NO"));
		}
		// ==================pangben modify 20110406 start
		if (this.getValueString("REGION_CODE").length() != 0) {
			result.setData("REGION_CODE", this.getValueString("REGION_CODE"));
		}
		// ==================pangben modify 20110406 stop
		return result;
	}

	/**
	 * �õ�����Ĳ�ѯ���
	 * 
	 * @return String
	 */
	public String getHRMQuerySQL() {// add by wanglong 20121214//==liling
									// 20140717 add ORDER_DR_CODE ����ҽ��
		String sql = "SELECT 'N' AS FLG, ";
		if ("O".equals(getAdmTypeRadioValue()) || "E".equals(getAdmTypeRadioValue())) {// wanglong add 20141212
			sql += " B.EXEC_FLG, ";
		}
		sql += " A.PRINT_FLG, A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, D.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,"
				+ "(SELECT  O.USER_NAME  FROM  SYS_OPERATOR  O  WHERE O.USER_ID=A.ORDER_DR_CODE ) ORDER_DR_CODE, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, D.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE,C.SEQ_NO AS NO "// caowl
																												// 20130320
																												// ����Ա�����
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C, SYS_PATINFO D "
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO " + "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO " + "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
		sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// �ż���

		sql += " AND A.MR_NO = D.MR_NO(+) ";

		String srartDate = StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// ��ʼʱ��
		String endDate = StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");// ����ʱ��
		sql += " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate + "','YYYYMMDDHH24MISS') "
				+ "                     AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
		if (this.getValueString("REGION_CODE").length() != 0) {// ����
			sql += " AND A.REGION_CODE = '" + this.getValueString("REGION_CODE") + "'";
		}
		if (this.getValueString("DEPT_CODEMED").length() != 0) {// ����
			sql += " AND A.DEPT_CODE = '" + this.getValueString("DEPT_CODEMED") + "'";
		}
		if (getPrintStatus().length() != 0) {// ��ӡ״̬��δ��ӡ���Ѵ�ӡ��ȫ����
			sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
		}
		if (this.getValueString("MR_NO").length() != 0) {// ������
			sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
		}
		if (this.getValueString("COMPANY_CODE").length() != 0) {// �����
			sql += " AND C.COMPANY_CODE = '" + this.getValueString("COMPANY_CODE") + "'";
		}
		if (this.getValueString("CONTRACT_CODE").length() != 0) {// ��ͬ��
			sql += " AND C.CONTRACT_CODE = '" + this.getValueString("CONTRACT_CODE") + "'";
		}
		if (this.getValueString("START_SEQ_NO").length() != 0) {// Ա����ſ�ʼ
			sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO") + "'";
		}
		if (this.getValueString("END_SEQ_NO").length() != 0) {// Ա����Ž���
			sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO") + "'";
		}
		sql += " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO ASC ,A.CAT1_TYPE ASC,A.START_DTTM DESC, A.CASE_NO ASC";// caowl
																																	// 20130305
																																	// ��������������
																																	// System.out.println("�������sql:"+sql);
		return sql;
	}

	/**
	 * �õ��ż�ס��ֵ
	 * 
	 * @return String
	 */
	public String getAdmTypeRadioValue() {
		if (this.getTRadioButton("O").isSelected())
			return "O";
		if (this.getTRadioButton("E").isSelected())
			return "E";
		if (this.getTRadioButton("I").isSelected())
			return "I";
		if (this.getTRadioButton("H").isSelected())
			return "H";
		return "O";
	}

	/**
	 * �õ���ӡ״̬
	 * 
	 * @return String
	 */
	public String getPrintStatus() {
		if (this.getTRadioButton("ALL").isSelected())
			return "";
		if (this.getTRadioButton("ONPRINT").isSelected())
			return "N";
		if (this.getTRadioButton("YESPRINT").isSelected())
			return "Y";
		return "";
	}

	/**
	 * ���ݲ������պʹ���Ľ������ڣ����㲡�����䲢�����Ƿ�Ϊ��ͯ�Բ�ͬ����ʽ��ʾ���䣬���Ƕ�ͯ����ʾX��X��X�գ����������ʾx��
	 * 
	 * @param odo
	 * @return String ������ʾ������
	 */
	public static String showAge(Timestamp birth, Timestamp sysdate) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birth, sysdate);
		if (OPDSysParmTool.getInstance().isChild(birth)) {
			age = res[0] + "��" + res[1] + "��";
		} else {
			age = (Integer.parseInt(res[0]) == 0 ? 1 : res[0]) + "��";
		}
		// System.out.println("age" + age);
		return age;
	}

	/**
	 * ���������
	 */
	public void onSend() {
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		Set caseNoset = new HashSet();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			caseNoset.add(temp.getValue("CASE_NO"));
		}
		if (caseNoset.size() > 0) {
			Iterator caseNoIter = caseNoset.iterator();
			while (caseNoIter.hasNext()) {
				String caseNo = "" + caseNoIter.next();
				Set applicationNo = new HashSet();
				for (int i = 0; i < rowCount; i++) {
					TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
					if (!temp.getBoolean("FLG"))
						continue;
					if (!caseNo.equals(temp.getValue("CASE_NO")))
						continue;
					applicationNo.add(temp.getValue("APPLICATION_NO"));
				}
				if (applicationNo.size() > 0) {
					Job job = new Job(applicationNo.size());
					List printSize = new ArrayList();
					Iterator appNo = applicationNo.iterator();
					int itemCount = 0;
					while (appNo.hasNext()) {
						String appNoStr = "" + appNo.next();
						StringBuffer orderDesc = new StringBuffer();
						// item
						Item item = job.getLabels().getItem(itemCount);
						for (int i = 0; i < rowCount; i++) {
							TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
							if (!appNoStr.equals(temp.getValue("APPLICATION_NO")))
								continue;
							job.setSurname(temp.getValue("PAT_NAME"));
							job.setName("");
							job.setSex(this.getDictionary("SYS_SEX", temp.getValue("SEX_CODE")));
							job.setWard(this.getDeptDesc(temp.getValue("DEPT_CODE")));
							job.setBdate(this.showAge(temp.getTimestamp("BIRTH_DATE"), sysDate));
							job.setAdate(StringTool.getString(temp.getTimestamp("ORDER_DATE"), "yyyy/MM/dd HH:mm:ss"));
							job.setPhone(temp.getValue("TEL"));
							job.setAddress(temp.getValue("ADDRESS"));
							job.setSpecimenid(caseNo);
							job.setPatientid(temp.getValue("MR_NO"));
							job.setRequestid(temp.getValue("ORDER_NO"));
							job.setNotes(temp.getValue("ADM_TYPE"));
							// �Թ����
							item.setTubeconvert(getTubeType(temp.getValue("ORDER_CODE")));
							// ��ע
							item.setNotelabel(geturGentFlg(appNoStr).equals("Y") ? "(��)" : "");
							// ����
							item.setBarcodelabel(appNoStr);
							// �������
							item.setVariouslabel(temp.getValue("SEQ_NO"));
							// ʱ��
							item.setPdate(StringTool.getString(sysDate, "yyyy/MM/dd HH:mm:ss"));
							if (appNoStr.equals(temp.getValue("APPLICATION_NO"))) {
								orderDesc.append(temp.getValue("ORDER_DESC"));
							}
						}
						item.setMnemotests(orderDesc.toString());
						itemCount++;
					}
					printSize.add(job);
					boolean falg = true;
					int listRowCount = printSize.size();
					for (int i = 0; i < listRowCount; i++) {
						Job pR = (Job) printSize.get(i);
						LAB_Service callLab = new LAB_Service();
						if (!callLab.init())
							continue;
						String host = "127.0.0.1";
						if ("O".equals(job.getNotes())) {
							host = callLab.getHost();
						}
						if ("H".equals(job.getNotes())) {
							host = callLab.getHostH();
						}
						if ("E".equals(job.getNotes())) {
							host = callLab.getHostE();
						}
						if ("I".equals(job.getNotes())) {
							host = callLab.getHostI();
						}
						new DynamecMassage(host, callLab.getPort(), pR.toString().getBytes());
						// �к�
						/**
						 * CallNo call = new CallNo(); if (!call.init()) { falg = false; }
						 **/
						String dateJH = StringTool.getString(sysDate, "yyyy-MM-dd HH:mm:ss");
						int count = pR.getLabels().getItem().length;
						for (int k = 0; k < count; k++) {
							String[] sqlMedApply = new String[] {
									"UPDATE MED_APPLY SET PRINT_FLG='Y',OPT_DATE=SYSDATE,OPT_USER='" + Operator.getID()
											+ "',OPT_TERM='" + Operator.getIP() + "' WHERE APPLICATION_NO='"
											+ pR.getLabels().getItem(k).getBarcodelabel() + "'" };
							TParm sqlParm = new TParm();
							sqlParm.setData("SQL", sqlMedApply);
							TParm actionParm = TIOM_AppServer.executeAction(actionName, "saveMedApply", sqlParm);
							if (actionParm.getErrCode() < 0) {
								this.messageBox("����" + pR.getSurname() + pR.getName() + "ҽ����ӡ״̬ʧ�ܣ�");
								continue;
							}
							/**
							 * if(falg){ String s = call.SyncLisMaster(pR.getSpecimenid(),
							 * pR.getPatientid(), pR.getLabels().getItem(k).getMnemotests(), pR.getSurname()
							 * + pR.getName(), pR.getSex(), pR.getBdate(), pR.getNotes(), dateJH, "", "0",
							 * "2"); }
							 **/
						}
					}
				}
			}
		} else {
			this.messageBox("û����Ҫ��ӡ����Ŀ��");
			return;
		}
		this.messageBox("���ͳɹ���");
	}

	/**
	 * ��ѯ�������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public String getTubeType(String orderCode) {
		// "SYS_TUBECONVERT"
		String sqlSys = "SELECT TUBE_TYPE FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sqlSys));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("TUBE_TYPE", 0);
	}

	/**
	 * ��ѯ����ҽ��ִ��״̬
	 * 
	 * @param CaseNo
	 * @param OrderNo
	 * @param Seq
	 * @return
	 */
	public boolean checkDspnm(TParm parm) {
		String CaseNo = parm.getValue("CASE_NO");
		String OrderNo = parm.getValue("ORDER_NO");
		String Seq = parm.getValue("SEQ_NO");
		String sql = "SELECT NS_EXEC_CODE,NS_EXEC_DATE FROM ODI_DSPNM " + " WHERE CASE_NO='" + CaseNo
				+ "' AND ORDER_NO='" + OrderNo + "' AND ORDER_SEQ=" + Seq;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0 || result.getValue("NS_EXEC_CODE", 0).equals("")) {
			// =========pangben 2015-8-21 �������ҽ��У��ִ����ʾ���ܣ���ѯҽ���Ƿ�Ϊ����ҽ��
			sql = "SELECT OPBOOK_SEQ FROM ODI_ORDER  WHERE CASE_NO='" + CaseNo + "' AND ORDER_NO='" + OrderNo
					+ "' AND ORDER_SEQ=" + Seq;
			result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() <= 0 || result.getValue("OPBOOK_SEQ", 0).equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �����ӡ
	 */
	public void onPrint() {
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		Set applicationNo = new LinkedHashSet();
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ==liling 20140626 �Թ���ɫ start===
		String tubeTypeSql = " SELECT CHN_DESC ,ID FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_TUBECONVERT'";
		TParm tubeParm = new TParm(TJDODBTool.getInstance().select(tubeTypeSql));
		// System.out.println("tubeParm====="+tubeParm);
		// ==liling 20140626 �Թ���ɫ end ===
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			if ("I".equals(getAdmTypeRadioValue()) && !checkDspnm(temp)) {
				this.messageBox(temp.getValue("ORDER_DESC") + "δִ��,���ܴ�ӡ����");
				continue;
			}
			applicationNo.add(temp.getValue("APPLICATION_NO"));
		}
		if (applicationNo.size() > 0 && EKTIO.getInstance().ektAyhSwitch()
				&& ("O".equals(getAdmTypeRadioValue()) || "E".equals(getAdmTypeRadioValue()))) {
			String caseNo1 = this.getTTable(TABLE).getParmValue().getRow(0).getValue("CASE_NO");
			TParm appParm = new TParm();
			String applyNos = "";
			Iterator appNo = applicationNo.iterator();
			while (appNo.hasNext()) {
				String appNoStr = "" + appNo.next();
				appParm.addData("APPLICATION_NO", appNoStr);
				applyNos += "'" + appNoStr + "',";
			}
			applyNos = applyNos.substring(0, applyNos.length() - 1);
			String caseNoSql = "SELECT DISTINCT CASE_NO FROM OPD_ORDER WHERE MED_APPLY_NO IN (" + applyNos + ")";
			TParm caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
			if (caseNoParm.getCount() > 1) {
				messageBox("��ѡ��һ����");
				return;
			}
			caseNoSql = "SELECT CASE_NO,MR_NO FROM REG_PATADM WHERE CASE_NO = '" + caseNoParm.getValue("CASE_NO", 0)
					+ "'";
			caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
			TParm orderPreParm = EKTpreDebtTool.getInstance().getMedOpdOrder(appParm);
			orderPreParm.setData("CASE_NO", caseNoParm.getData("CASE_NO", 0));
			orderPreParm.setData("MR_NO", caseNoParm.getData("MR_NO", 0));
			TParm preParm = EKTpreDebtTool.getInstance().checkMasterForExe(orderPreParm);
			if (preParm.getErrCode() < 0) {
				messageBox(preParm.getErrText());
				return;
			}
		}
		if (applicationNo.size() > 0) {
			List printSize = new ArrayList();
			Iterator appNo = applicationNo.iterator();
			while (appNo.hasNext()) {
				String appNoStr = "" + appNo.next();
				StringBuffer orderDesc = new StringBuffer();
				String patName = "";
				String deptExCode = "";
				String orderDate = "";
				String stationCode = "";
				String optItemDesc = "";
				String deptCode = "";
				String urgentFlg = "";
				String mrNo = "";
				String sexDesc = "";
				String age = "";
				String devdesc = "";
				String applyNo = ""; // chenxi �����
				String drNote = ""; // chenxi ҽʦ��ע
				String tubeType = ""; // yanj �Թ���ɫ����
				String tubeID = "";// ==liling ���ֵ����Թ���ɫID
				String tubColor = "";// liling �Թ���ɫ
				String birthdate = ""; // ��������
				for (int i = 0; i < rowCount; i++) {
					TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
					if (!appNoStr.equals(temp.getValue("APPLICATION_NO")))
						continue;
					tubeType = temp.getValue("TUBE_TYPE");// yanjing 20140319
															// �Թ���ɫ
					// ==liling 20140626 �Թ���ɫ start===
					for (int k = 0; k < tubeParm.getCount(); k++) {
						tubeID = tubeParm.getValue("ID", k);
						if (tubeType.equals(tubeID)) {
							tubColor = tubeParm.getValue("CHN_DESC", k);
						}
					}
					// ==liling 20140626 �Թ���ɫ end===
					patName = temp.getValue("PAT_NAME");
					deptExCode = temp.getValue("EXEC_DEPT_CODE");
					// ==liling ���������ӡ�������� סԺ��ӡ����start ==//20140709 ����
					// ȥ������+this.getText("CLINICROOM_CODEMED")
					if ("O".equals(getAdmTypeRadioValue()) || "E".equals(getAdmTypeRadioValue())) {
						this.getTTextFormat("CLINICAREA_CODEMED").setValue(temp.getValue("CLINICAREA_CODE"));
						this.getTTextFormat("CLINICROOM_CODEMED").setValue(temp.getValue("CLINICROOM_NO"));
						deptCode = this.getText("CLINICAREA_CODEMED");
					} else {
						this.getTTextFormat("STATION_CODEMED").setValue(temp.getValue("STATION_CODE"));
						deptCode = this.getText("STATION_CODEMED");
					}
					// ==liling ���������ӡ�������� סԺ��ӡ���� end ==
					stationCode = temp.getValue("STATION_CODE");
					// =============== chenxi modify 20120709
					applyNo = temp.getValue("APPLICATION_NO");
					drNote = temp.getValue("DR_NOTE");
					String sql = "SELECT B.BED_NO_DESC FROM MED_APPLY A,SYS_BED B WHERE A.CAT1_TYPE='LIS' AND A.APPLICATION_NO ='"
							+ applyNo + "'" + "AND A.BED_NO=B.BED_NO ";// wanglong
																		// modify
																		// 20140610
					TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
					bedNo = selParm.getValue("BED_NO_DESC", 0);
					// ================== chenxi modify 20120709
					// devdesc=temp.getValue("DEV_CODE");
					urgentFlg = geturGentFlg(appNoStr).equals("Y") ? "(��)" : "";
					orderDate = String.valueOf(sysDate).substring(0, 19).replaceAll("-", "/");
					optItemDesc = temp.getValue("OPTITEM_CHN_DESC");
					mrNo = temp.getValue("MR_NO");
					sexDesc = this.getDictionary("SYS_SEX", temp.getValue("SEX_CODE"));
					// �������䣬��ȷ�� �꣬�£��� =======huangjunwen modify 20140611
					Timestamp date = SystemTool.getInstance().getDate();
					Timestamp test = temp.getTimestamp("BIRTH_DATE") == null ? date : temp.getTimestamp("BIRTH_DATE");
					age = DateUtil.showAge(test, date);
					birthdate = temp.getValue("BIRTH_DATE");
					// ===============================huangjunwen modify
					// 20140611
					// age = StringTool.CountAgeByTimestamp(temp
					// .getTimestamp("BIRTH_DATE"), sysDate)[0];
					if (appNoStr.equals(temp.getValue("APPLICATION_NO"))) {
						orderDesc.append(temp.getValue("ORDER_DESC"));
					}
				}
				TParm printParm = new TParm();
				printParm.setData("APPLICATION_NO", "TEXT", appNoStr);
				printParm.setData("PAT_NAME", "TEXT", patName);
				printParm.setData("DEPT_CODE", "TEXT", deptCode);
				printParm.setData("STATION_CODE", "TEXT", stationCode);
				printParm.setData("URGENT_FLG", "TEXT", urgentFlg);
				printParm.setData("EXEC_DEPT_CODE", "TEXT", deptExCode);
				printParm.setData("ORDER_DATE", "TEXT", orderDate);
				printParm.setData("OPTITEM_CHN_DESC", "TEXT", optItemDesc);
				printParm.setData("ORDER_DESC", "TEXT", orderDesc.toString());
				printParm.setData("MR_NO", "TEXT", mrNo);
				printParm.setData("SEX_DESC", "TEXT", sexDesc);
				//
				// printParm.setData("TUBE_TYPE", "TEXT", tubeType);//yanjing
				// 20140319�Թ���ɫ
				printParm.setData("TUBE_TYPE", "TEXT", tubColor);// ===liling
																	// �Թ���ɫ
				// =============== chenxi
				printParm.setData("BED_NO", "TEXT", bedNo);
				printParm.setData("DR_NOTE", "TEXT", drNote);
				// ============== chenxi
				// add by wangb 2017/08/10 ���ӳ���������ʾ
				if (birthdate != null && birthdate.length() >= 10) {
					birthdate = birthdate.substring(0, 4) + "��" + birthdate.substring(5, 7) + "��"
							+ birthdate.substring(8, 10) + "��";
				}
				// printParm.setData("BIRTHDATE", "TEXT", birthdate);
				printParm.setData("AGE", "TEXT", birthdate);
				printSize.add(printParm);
			}
			int listRowCount = printSize.size();
			String paramSql = "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM='#'".replaceFirst("#",
					Operator.getIP());
			TParm printParam = new TParm(TJDODBTool.getInstance().select(paramSql)); // wanglong
																						// modify
																						// 20140610
			if (printParam.getErrCode() < 0) {
				this.messageBox("��ȡ��ӡ��������");
				return;
			}
			String printerPort = printParam.getValue("PRINTER_PORT", 0);
			boolean zebraFlg = printParam.getBoolean("ZEBRA_FLG", 0) && !printerPort.equals("");
			for (int i = 0; i < listRowCount; i++) {
				TParm pR = (TParm) printSize.get(i);
				pR.setData("EXEC_DEPT_CODE", "TEXT", getDeptDesc(pR.getValue("EXEC_DEPT_CODE", "TEXT")));
				// ===liling start====
				// if("I".equals(getAdmTypeRadioValue())){//סԺ��ʾ����
				// pR.setData("DEPT_CODE", "TEXT",
				// getDeptDesc(pR.getValue("DEPT_CODE", "TEXT")) + "("
				// + getStationDesc(pR.getValue("STATION_CODE", "TEXT")) + ")");
				// }else{
				// pR.setData("DEPT_CODE", "TEXT",
				// getDeptDesc(pR.getValue("DEPT_CODE", "TEXT"))
				// + getStationDesc(pR.getValue("STATION_CODE", "TEXT")));
				// }
				// ===liling end====
				// this.messageBox_(pR);
				if (!zebraFlg) {// wanglong modify 20140610
					this.openPrintDialog("%ROOT%\\config\\prt\\MED\\Med_ApplyPrint_V45.jhw", pR, true);
				} else {
					this.printText = new StringBuffer();// wanglong add 20140610
					this.addBarcode(101, 0, 2, 3.0, 115, pR.getValue("APPLICATION_NO", "TEXT"));
					this.addText(330, 110, pR.getValue("URGENT_FLG", "TEXT"));// (��)
					// ======��һ��
					this.addText(2, 137, "I");
					this.addText(10, 137, "D");
					this.addText(23, 137, ":");
					this.addText(31, 137, pR.getValue("MR_NO", "TEXT"));
					this.addText(155, 137, pR.getValue("PAT_NAME", "TEXT"));
					this.addText(155 + pR.getValue("PAT_NAME", "TEXT").length() * 25 - 3, 137,
							pR.getValue("SEX_DESC", "TEXT"));
					this.addText(155 + pR.getValue("PAT_NAME", "TEXT").length() * 25 + 25, 137,
							pR.getValue("AGE", "TEXT"));
					// =======�ڶ���
					this.addText(2, 163, pR.getValue("DEPT_CODE", "TEXT").replaceAll("��", "(").replaceAll("��", ")"));
					this.addText(270, 163, "����:" + pR.getValue("BED_NO", "TEXT"));
					// =======������
					this.addText(2, 189, pR.getValue("TUBE_TYPE", "TEXT"));
					this.addText(180, 189, "ִ�п���:" + pR.getValue("EXEC_DEPT_CODE", "TEXT"));
					// =======������
					this.addText(2, 215, "������Ŀ:" + pR.getValue("ORDER_DESC", "TEXT"));
					if (!printBarCode(printerPort)) {// �������ӡ���� wanglong add
														// 20140610
						return;
					}
				}
				String[] sqlMedApply = null;
				if (this.getAdmTypeRadioValue().equals("O") || this.getAdmTypeRadioValue().equals("E")) {
					sqlMedApply = new String[2];
					sqlMedApply[0] = "UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE,OPT_DATE=SYSDATE,OPT_USER='"
							+ Operator.getID() + "',OPT_TERM='" + Operator.getIP()
							+ "' WHERE CAT1_TYPE = 'LIS' AND APPLICATION_NO='"// wanglong
																				// modify
																				// 20140610
							+ pR.getValue("APPLICATION_NO", "TEXT") + "'";
					sqlMedApply[1] = getMedOEexe(pR.getValue("APPLICATION_NO", "TEXT"), "Y");
				} else {
					sqlMedApply = new String[] {
							"UPDATE MED_APPLY SET PRINT_FLG='Y',PRINT_DATE=SYSDATE,OPT_DATE=SYSDATE,OPT_USER='"
									+ Operator.getID() + "',OPT_TERM='" + Operator.getIP()
									+ "' WHERE CAT1_TYPE = 'LIS' AND APPLICATION_NO='"// wanglong
																						// modify
																						// 20140610
									+ pR.getValue("APPLICATION_NO", "TEXT") + "'" };
				}
				TParm sqlParm = new TParm();
				sqlParm.setData("SQL", sqlMedApply);
				TParm actionParm = TIOM_AppServer.executeAction(actionName, "saveMedApply", sqlParm);
				if (actionParm.getErrCode() < 0) {
					this.messageBox("����" + pR.getValue("PAT_NAME") + "ҽ����ӡ״̬ʧ�ܣ�");
					return;
				}
				// �к�
				CallNo call = new CallNo();
				if (!call.init()) {
					continue;
				}
				String dateJH = StringTool.getString(sysDate, "yyyy-MM-dd HH:mm:ss");
				String s = call.SyncLisMaster(pR.getValue("APPLICATION_NO", "TEXT"), pR.getValue("MR_NO", "TEXT"),
						pR.getValue("ORDER_DESC", "TEXT"), pR.getValue("PAT_NAME", "TEXT"),
						pR.getValue("SEX_DESC", "TEXT"), pR.getValue("AGE", "TEXT"), pR.getValue("URGENT_FLG", "TEXT"),
						dateJH, "", "0", "2");
			}
		} else {
			this.messageBox("û����Ҫ��ӡ����Ŀ��");
			return;
		}
		this.printQuery();
		this.onInit();
	}

	/**
	 * �ż���ִ��sql
	 * 
	 * @param applyNo
	 * @return
	 */
	public String getMedOEexe(String applyNo, String flg) {
		String now = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMddHHmmss");
		String execDr = "";
		String execDate = "";
		if (flg.equals("Y")) {
			execDr = Operator.getID();
			// wanglong modify 20141212 ����Ѿ��Ϲ��ʣ��򲻸���EXEC_DATE���������ֶθ���
			execDate = ",EXEC_DATE=CASE WHEN EXEC_DATE IS NOT NULL THEN EXEC_DATE ELSE TO_DATE('" + now
					+ "','YYYYMMDDHH24MISS') END";
		} else {
			execDate = ",EXEC_DATE=NULL";
		}
		String sqlMedApply = "UPDATE OPD_ORDER SET EXEC_FLG='" + flg + "',EXEC_DR_CODE='" + execDr + "'" + execDate
				+ ",OPT_DATE=SYSDATE,OPT_USER='" + Operator.getID() + "',OPT_TERM='" + Operator.getIP()
				+ "' WHERE (CASE_NO,MED_APPLY_NO,CAT1_TYPE) IN (SELECT CASE_NO,APPLICATION_NO,CAT1_TYPE "// wanglong
																											// modify
																											// 20141212����CASE_NO
				+ "FROM MED_APPLY WHERE CAT1_TYPE='LIS' AND APPLICATION_NO='" + applyNo + "')";
		return sqlMedApply;
	}

	/**
	 * �޸Ĵ�ӡ���Ϊ��N�� YANJING 20140409
	 */
	public String getMedApplyPrtFlg(String applyNo, String flg) {
		String sqlMedApply = "UPDATE MED_APPLY SET PRINT_FLG='" + flg + "',PRINT_DATE="
				+ (flg.equals("Y") ? "SYSDATE" : "''") + ",OPT_DATE=SYSDATE,OPT_USER='" + Operator.getID()
				+ "',OPT_TERM='" + Operator.getIP() + "' WHERE APPLICATION_NO='" + applyNo + "' AND CAT1_TYPE='LIS'";
		return sqlMedApply;
	}

	/**
	 * ���˲���
	 */
	public void onMedExe() {// wanglong add 20141212
		Map map = new HashMap();
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		TParm parmValue = this.getTTable(TABLE).getParmValue();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parmValue.getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			// if (onCheckLisStatus(temp).getErrCode() < 0) {
			// this.messageBox(onCheckLisStatus(temp).getErrText());
			// return;
			// }
			if (map.get(temp.getValue("APPLICATION_NO")) == null) {
				map.put(temp.getValue("APPLICATION_NO"), temp.getValue("APPLICATION_NO"));
			}
		}
		if (map.size() == 0) {
			this.messageBox("����ѡ��ҽ��");
			return;
		}
		String[] sqlMedApply = new String[map.size()];
		String[] sqlMedApplyPrtFlg = new String[map.size()];
		int count = 0;
		Iterator e = map.values().iterator();
		while (e.hasNext()) {
			String applyNo = (String) e.next();
			sqlMedApply[count] = this.getMedOEexe(applyNo, "Y");
			// sqlMedApplyPrtFlg[count] = this.getMedApplyPrtFlg(applyNo, "Y");
			count++;
		}
		sqlMedApplyPrtFlg = new String[0];
		TParm sqlParm = new TParm();
		sqlParm.setData("SQL", sqlMedApply);
		sqlParm.setData("PRTFLGSQL", sqlMedApplyPrtFlg);
		TParm actionParm = TIOM_AppServer.executeAction(actionName, "saveMedApply1", sqlParm);
		if (actionParm.getErrCode() < 0) {
			this.messageBox("����ʧ�ܣ�");
			return;
		}
		this.onQuery();
		this.messageBox("�����ɹ���");
		printCode = "";
	}

	/**
	 * ȡ�����˲���
	 */
	public void onCancleMedExe() {
		Map map = new HashMap();
		this.getTTable(TABLE).acceptText();
		int rowCount = this.getTTable(TABLE).getRowCount();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (!temp.getBoolean("FLG"))
				continue;
			if (onCheckLisStatus(temp).getErrCode() < 0) {
				this.messageBox(onCheckLisStatus(temp).getErrText());
				return;
			}
			if (map.get(temp.getValue("APPLICATION_NO")) == null) {
				map.put(temp.getValue("APPLICATION_NO"), temp.getValue("APPLICATION_NO"));
			}
		}
		String[] sqlMedApply = new String[map.size()];
		String[] sqlMedApplyPrtFlg = new String[map.size()];
		int count = 0;
		Iterator e = map.values().iterator();
		while (e.hasNext()) {
			String applyNo = (String) e.next();
			sqlMedApply[count] = this.getMedOEexe(applyNo, "N");
			sqlMedApplyPrtFlg[count] = this.getMedApplyPrtFlg(applyNo, "N");
			count++;
		}
		if (count == 0) {
			this.messageBox("��ȡ��ҽ��");
			return;
		}
		TParm sqlParm = new TParm();
		sqlParm.setData("SQL", sqlMedApply);
		sqlParm.setData("PRTFLGSQL", sqlMedApplyPrtFlg);
		TParm actionParm = TIOM_AppServer.executeAction(actionName, "saveMedApply1", sqlParm);
		if (actionParm.getErrCode() < 0) {
			this.messageBox("����ʧ�ܣ�");
			return;
		}
		this.onQuery();
		this.messageBox("�����ɹ���");
		printCode = "";
	}

	/**
	 * �ж�״̬
	 * 
	 * @return
	 */
	public TParm onCheckLisStatus(TParm parm) {
		TParm statusParm = new TParm();
		String applyNo = parm.getValue("APPLICATION_NO");
		String orderNo = parm.getValue("ORDER_NO");
		String seqNo = parm.getValue("SEQ_NO");
		String orderDesc = parm.getValue("ORDER_DESC");
		String sql = "SELECT STATUS FROM MED_APPLY WHERE CAT1_TYPE='LIS' AND " + "APPLICATION_NO='" + applyNo
				+ "' AND ORDER_NO='" + orderNo + "' AND SEQ_NO='" + seqNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() <= 0) {
			statusParm.setErr(-1, orderDesc + "δ��ѯ��״̬,������ȷ��");
			return statusParm;
		}
		if (result.getValue("STATUS", 0).equals("11")) {
			statusParm.setErr(-1, orderDesc + "��ǩ��,����ȡ��");
			return statusParm;
		}
		if (result.getValue("STATUS", 0).equals("4")) {
			statusParm.setErr(-1, orderDesc + "�Ѻ���,����ȡ��");
			return statusParm;
		}
		if (result.getValue("STATUS", 0).equals("6")) {
			statusParm.setErr(-1, orderDesc + "�����,����ȡ��");
			return statusParm;
		}
		if (result.getValue("STATUS", 0).equals("7")) {
			statusParm.setErr(-1, orderDesc + "�ѱ���,����ȡ��");
			return statusParm;
		}
		if (result.getValue("STATUS", 0).equals("8")) {
			statusParm.setErr(-1, orderDesc + "�����,����ȡ��");
			return statusParm;
		}
		return statusParm;
	}

	/**
	 * �õ��������뼱�����
	 */
	public String geturGentFlg(String appNoStr) {
		String flg = "";
		if (appNoStr.equals(""))
			return flg;
		String medsql = "SELECT CASE_NO,ORDER_NO,SEQ_NO,ADM_TYPE FROM MED_APPLY " + " WHERE APPLICATION_NO='" + appNoStr
				+ "' AND CAT1_TYPE='LIS'";
		TParm parm = new TParm(this.getDBTool().select(medsql));
		if (parm.getErrCode() < 0)
			return flg;
		if (parm.getCount() <= 0)
			return flg;
		String admType = "";
		String caseNo = "";
		String orderNo = "";
		String seqNo = "";
		if (parm.getCount() > 0) {
			String orderSql = "";
			admType = parm.getValue("ADM_TYPE", 0);
			caseNo = parm.getValue("CASE_NO", 0);
			orderNo = parm.getValue("ORDER_NO", 0);
			seqNo = String.valueOf(parm.getInt("SEQ_NO", 0));
			if (admType.equals("O") || admType.equals("E")) {
				orderSql = " SELECT URGENT_FLG FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "'" + " AND RX_NO='" + orderNo
						+ "' AND SEQ_NO='" + seqNo + "'";
			}
			if (admType.equals("I")) {
				orderSql = " SELECT URGENT_FLG FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "'" + " AND ORDER_NO='"
						+ orderNo + "' AND ORDER_SEQ='" + seqNo + "'";
			}
			if (admType.equals("H")) {
				orderSql = " SELECT URGENT_FLG FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "'" + " AND SEQ_NO='" + seqNo
						+ "'";
			}
			TParm result = new TParm(this.getDBTool().select(orderSql));
			flg = result.getValue("URGENT_FLG", 0);
		}
		return flg;
	}

	/**
	 * �õ��������
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getCategory(String categoryCode) {
		String result = "";
		TParm parm = new TParm(this.getDBTool()
				.select("SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE CATEGORY_CODE='" + categoryCode + "'"));
		result = parm.getValue("CATEGORY_CHN_DESC", 0);
		return result;
	}

	/**
	 * �õ��ֵ���Ϣ
	 * 
	 * @param groupId
	 *            String
	 * @param id
	 *            String
	 * @return String
	 */
	public String getDictionary(String groupId, String id) {
		String result = "";
		TParm parm = new TParm(this.getDBTool()
				.select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='" + groupId + "' AND ID='" + id + "'"));
		result = parm.getValue("CHN_DESC", 0);
		return result;
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(this.getDBTool()
				.select("SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(
				this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		TRadioButton h = (TRadioButton) this.getComponent("H");
		// ==liling 20140717 add start ��������Ϊ����-3 ������===
		String endDay = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd") + " 23:59:59";// ==liling
																												// 20140825
																												// add
		String dateH = StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd") + "000000";
		Timestamp sysDateH = StringTool.getTimestamp(dateH, "yyyyMMddHHmmss");
		// Timestamp sysDate = StringTool.getTimestamp(date, "yyyyMMddHHmmss");
		// ==liling 20140717 add end ��������Ϊ����-3 ������===
		// System.out.println(sysDate);
		this.setValue("CONFIRM_N", "Y");
		this.setValue("APPLY_FLG", "N");
		callFunction("UI|print|setEnabled", true);
		callFunction("UI|send|setEnabled", true);
		this.onInit();
		if (this.getPopedem("NORMAL")) {
			this.onInit();
			if (h.isSelected()) {
				Date d = new Date(sysDateH.getTime());
				String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate() + " 00:00:00";// caowl
																												// 20130305
																												// �����Ĭ������Ϊǰһ����
				String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate() + " 23:59:59";
				// ��ʼ��ʱ��
				this.setValue("START_DATE", getTimestamp(begin));
				this.setValue("END_DATE", getTimestamp(end));
				clearValue(
						"IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE;BAR_CODE_PRINT");// caowl
				// ����COMPANY_CODE;CONTRACT_CODE
			} else {
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK");
				this.setValue("START_DATE", this.getAdmDate());
				this.setValue("END_DATE", getTimestamp(endDay));
			}
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
		}
		if (this.getPopedem("SYSOPERATOR")) {
			this.onInit();
			if (h.isSelected()) {
				Date d = new Date(sysDateH.getTime());
				String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate() + " 00:00:00";// caowl
																												// 20130305
																												// �����Ĭ������Ϊǰһ����
				String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate() + " 23:59:59";
				// ��ʼ��ʱ��
				this.setValue("START_DATE", getTimestamp(begin));
				this.setValue("END_DATE", getTimestamp(end));
				clearValue(
						"IPD_NO;MR_NO;PAT_NAME;BED_NO;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE;BAR_CODE_PRINT");

			} else {
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;BAR_CODE;BAR_CODE_PRINT");
				this.setValue("START_DATE", this.getAdmDate());
				this.setValue("END_DATE", getTimestamp(endDay));
			}

			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
		}
		if (this.getPopedem("SYSDBA")) {
			if (h.isSelected()) {
				Date d = new Date(sysDateH.getTime());
				String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate() + " 00:00:00";// caowl
																												// 20130305
																												// �����Ĭ������Ϊǰһ����
				String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate() + " 23:59:59";
				// ��ʼ��ʱ��
				this.setValue("START_DATE", getTimestamp(begin));
				this.setValue("END_DATE", getTimestamp(end));
				clearValue(
						"IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE;BAR_CODE_PRINT");

			} else {
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;BAR_CODE;BAR_CODE_PRINT");
				this.setValue("START_DATE", this.getAdmDate());
				// this.messageBox("444"+endDay);
				this.setValue("END_DATE", getTimestamp(endDay));
			}
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
		}
	}

	/**
	 * ����
	 */
	public void onRead() {
		this.onClear(); // add by huangtt 20150629
		// TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
		TParm patParm = jdo.ekt.EKTIO.getInstance().TXreadEKT();
		if (patParm.getErrCode() < 0) {
			this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
			return;
		}
		this.setValue("DEPT_CODEMED", "");
		this.setValue("CLINICAREA_CODEMED", "");
		this.setValue("CLINICROOM_CODEMED", "");
		this.setValue("STATION_CODEMED", "");
		this.setValue("MR_NO", patParm.getValue("MR_NO"));
		this.setMrNo(patParm.getValue("MR_NO"));

		// System.out.println("===jjjj is ::"+this.getMrNo());
		this.onQuery();
	}

	// $$===================add by lx 2011/02/13 ����̩�ĽкŽӿ�
	// start===========================$$//
	/**
	 * ���нӿ�-�Ŷ�
	 */
	public void onQueueCall() {
		// �����Ų���Ϊ��
		if (this.getValue("MR_NO").equals("")) {
			this.messageBox("�����벡���ţ�");
			return;
		}
		// TABLE���Ŷ���Ϣ����
		if (this.getTTable(TABLE).getRowCount() == 0) {
			this.messageBox("���Ŷ���Ϣ���ϣ�");
			return;
		}
		String msg = "";
		String mrNo = (String) this.getValue("MR_NO");
		msg += mrNo + "|";
		String sql = "SELECT PAT_NAME,b.CHN_DESC SEX,to_char(BIRTH_DATE,'yyyy-MM-dd') BIRTH_DATE FROM SYS_PATINFO a,(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX') b";
		sql += " WHERE MR_NO='" + mrNo + "'";
		sql += " AND a.SEX_CODE=b.ID";
		// ͨ��mrNoȡ�Ա�����
		TParm patParm = new TParm(TJDODBTool.getInstance().select(sql));
		msg += patParm.getValue("PAT_NAME", 0) + "|";
		msg += patParm.getValue("SEX", 0) + "|";
		msg += patParm.getValue("BIRTH_DATE", 0) + "|";
		msg += Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doLabQueueCall", inParm);
		this.messageBox("�����Ŷ���Ϣ�ɹ���");
	}

	/**
	 * ���нӿ�-��һ��
	 */
	public void onNextCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doLabNextCall", inParm);

		this.messageBox("�кųɹ���");
	}

	/**
	 * ���нӿ�-�ؽ�
	 */
	public void onReCall() {
		String msg = "" + "|" + Operator.getIP();
		TParm inParm = new TParm();
		inParm.setData("msg", msg);
		TIOM_AppServer.executeAction("action.device.CallNoAction", "doLabReCall", inParm);

		this.messageBox("�ؽгɹ���");

	}

	// $$===================add by lx 2011/02/13
	// ����̩�ĽкŽӿ�END===========================$$//

	public String getAdmType() {
		return admType;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public Timestamp getAdmDate() {
		return admDate;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public String getClinicareaCode() {
		return clinicareaCode;
	}

	public String getClinicroomNo() {
		return clinicroomNo;
	}

	public String getIpdNo() {
		return ipdNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public String getPatName() {
		return patName;
	}

	public String getStationCode() {
		return stationCode;
	}

	public String getBedNo() {
		return bedNo;
	}

	public void setAdmType(String admType) {
		this.admType = admType;
	}

	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public void setClinicareaCode(String clinicareaCode) {
		this.clinicareaCode = clinicareaCode;
	}

	public void setClinicroomNo(String clinicroomNo) {
		this.clinicroomNo = clinicroomNo;
	}

	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	public void setPatName(String patName) {
		this.patName = patName;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public void setBedNo(String bedNo) {
		this.bedNo = bedNo;
	}

	// ========add by wanglong 20130726
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	// ========add end
	/**
	 * �Ҽ�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu|TABLE");
	}

	/**
	 * ϸ��
	 */
	public void openRigthPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		// System.out.println("ѡ����:"+parm);
		TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * ���ؼ���ҽ��ϸ���TParm��ʽ
	 * 
	 * @return result TParm
	 */
	public TParm getOrderSetDetails(String orderCode) {
		TParm result = new TParm();
		String sql = "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B  WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			result.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
			result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
			result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
			// �����ܼ۸�
			double ownPrice = parm.getDouble("OWN_PRICE", i) * parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
			result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
			result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
		}
		return result;
	}

	/**
	 * �������ѡ���¼�
	 */
	public void onCompanyChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			return;
		}
		// ������������ø�����ĺ�ͬ����
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm.getErrCode() != 0) {
			this.messageBox_("û������");
		}
		// ����һ��TTextFormat,����ͬ���ֵ������ؼ���ȡ�����һ����ͬ���븳ֵ������ؼ���ʼֵ
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("��ѯʧ��");
			return;
		}
		contract.setValue(contractCode);

	}

	/**
	 * ��ͬ����ѡ���¼�
	 */
	public void onContractChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			this.messageBox_(companyCode);
			return;
		}
		contractCode = this.getValueString("CONTRACT_CODE");

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
				TParm tableData = getTTable(TABLE).getParmValue();
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
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
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
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
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
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

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
	 * ����ִ�� yanjing 20140319
	 */
	public void onApply() {
		boolean have = false;
		boolean success = false;
		String user_id = Operator.getID();
		String now = SystemTool.getInstance().getDate().toString().substring(0, 19);
		String sql = "";
		int rowCount = this.getTTable(TABLE).getRowCount();
		if (rowCount <= 0) {
			this.messageBox("û�����ݣ�");
			return;
		}
		// �����ж�
		if (!checkPW()) {
			return;
		}
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (temp.getBoolean("FLG")) {
				String mrNo = temp.getValue("MR_NO");
				String caseNo = temp.getValue("CASE_NO");
				String cat1_type = temp.getValue("CAT1_TYPE");
				String application_no = temp.getValue("APPLICATION_NO");
				String order_no = temp.getValue("ORDER_NO");
				String seq_no = temp.getValue("SEQ_NO");
				// ���±�med_apply
				sql = updateSql(mrNo, caseNo, cat1_type, application_no, order_no, seq_no, user_id, now);
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(sql));
				have = true;
				success = true;
			}
		}
		if (!have) {
			this.messageBox("û��ѡ�����ݣ�");
			return;
		}
		if (success) {
			this.messageBox("����ɹ���");
			// ˢ��ҳ��
			onQuery();
			execBarCode = "";
			return;
		} else {
			this.messageBox("����ʧ�ܣ�");
			execBarCode = "";
			return;
		}
	}

	/**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "inwCheck";
		String value = (String) this.openDialog("%ROOT%\\config\\inw\\passWordCheck.x", inwCheck);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initApplyPage() {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String endDay = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd") + " 23:59:59";// ==liling
																												// 20140825
																												// add
		this.setValue("START_DATE", this.getAdmDate());
		this.setValue("END_DATE", getTimestamp(endDay));
		// System.out.println("----0000-----mr_no is ::"+this.getMrNo());
		if ("O".equals(this.getAdmType())) {// ��ӡ�����ҽ������ORDER_DR����������ǰ�棬�����뱨������еĿ�ȱ�С��ҽ���зŴ�
											// ǰ2������20 ҽ����40
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;��,30,boolean;����,70;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
			this.getTTable(TABLE).setParmMap(
					"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("E".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;��,30,boolean;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,200;ҽ������ʱ��,140;����ҽ��,70;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;�����,100;�������,60,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");
			this.getTTable(TABLE).setParmMap(
					"FLG;URGENT_FLG;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;ORDER_DR;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}

		if ("H".equals(this.getAdmType())) {

			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� begin */
			Date d = new Date(sysDate.getTime());
			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate() + " 00:00:00";// caowl
																											// 20130305
																											// �����Ĭ������Ϊǰһ����
			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate() + " 23:59:59";
			// ��ʼ��ʱ��
			this.setValue("START_DATE", getTimestamp(begin));
			this.setValue("END_DATE", getTimestamp(end));
			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� end */

			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.setValue("COMPANY_CODE", this.getCompanyCode());// add by
																	// wanglong
																	// 20130726
			if (!StringUtil.isNullString(this.getCompanyCode())) {
				TParm contractParm = contractD.onQueryByCompany(this.getCompanyCode());// add-by-wanglong-20130821
				contract.setPopupMenuData(contractParm);
				contract.setComboSelectRow();
				contract.popupMenuShowData();
				contract.setValue(this.getContractCode()); // add-end
			}
			// this.setValue("CONTRACT_CODE", this.getContractCode());
			this.getTTable(TABLE).setHeader(
					"ѡ,30,boolean;��,30,boolean;���,50;����,100;������,100;����,90,DEPT_CODE;����,80;�Թ���ɫ,120,TUBE_TYPE;ҽ������,160;ҽ������ʱ��,140;����,90,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140;����ִ��״̬,100;��ӡʱ��,140");// caowl
																																																																																	// 20130320
																																																																																	// �������һ��
			this.getTTable(TABLE).setParmMap(
					"FLG;URGENT_FLG;NO;PAT_NAME;MR_NO;DEPT_CODE;AGE;TUBE_TYPE;ORDER_DESC;ORDER_DATE;CLINICAREA_CODE;CLINICROOM_NO;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;BLOOD_USER;BLOOD_DATE;BLOOD_STATE;PRINT_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");// caowl
																																																																					// 20130320
																																																																					// �������һ��
		}
		// ��ѯ
		// this.onQuery();
	}

	/**
	 * ����ִ�пؼ��¼�
	 */
	public void onApplyCheck() {
		if (this.getValueBoolean("APPLY_FLG")) {
			callFunction("UI|print|setEnabled", false);
			callFunction("UI|send|setEnabled", false);
			callFunction("UI|execute|setEnabled", false);// wanglong add
															// 20141212
			callFunction("UI|cancel|setEnabled", false);
			callFunction("UI|apply|setEnabled", true);
			this.initApplyPage();
		} else {
			callFunction("UI|apply|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
			callFunction("UI|send|setEnabled", true);
			callFunction("UI|execute|setEnabled", true);// wanglong add 20141212
			callFunction("UI|cancel|setEnabled", true);
			this.initPage();
		}
		if ("O".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", true);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", false);
		} else if ("E".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", true);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", false);
		} else if ("I".equals(this.getAdmType())) {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", true);
			callFunction("UI|H|setEnabled", false);

			// add by yangjj 20150319 ���ز���ȷ��panel
			TPanel panel = (TPanel) this.getComponent("tPanel_0");
			panel.setVisible(false);

			// add by yangjj 20150319���ز���ִ��menu
			callFunction("UI|apply|setVisible", false);

		} else {
			callFunction("UI|O|setEnabled", false);
			callFunction("UI|E|setEnabled", false);
			callFunction("UI|I|setEnabled", false);
			callFunction("UI|H|setEnabled", true);
		}
		clinicAreaCode = (TTextFormat) this.getComponent("CLINICAREA_CODEMED");// ִ�п���
		TextFormatRegClinicArea combo_clinicarea = (TextFormatRegClinicArea) this.getComponent("CLINICAREA_CODEMED");
		combo_clinicarea.setDrCode(Operator.getID());
		combo_clinicarea.onQuery();
	}

	/**
	 * ��ѯ
	 */
	public void applyQuery() {
		if (this.getValueString("MR_NO").trim().length() != 0) {
			this.setValue("MR_NO", PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
			this.setValue("PAT_NAME", PatTool.getInstance()
					.getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
			this.setMrNo(this.getValueString("MR_NO"));
			this.setPatName(this.getValueString("PAT_NAME"));
			this.setClinicareaCode(this.getValueString("CLINICAREA_CODEMED"));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());

				}
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
				if (patInfParm.getValue("CLINICAREA_CODE").equals(null)
						|| "".equals(patInfParm.getValue("CLINICAREA_CODE"))) {
					this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());

				}
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				// this.setValue("DEPT_CODEMED",
				// patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getApplyQuerySQL();
			// System.out.println("MR-SQL="+sql);
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			culmulateAge(action);// ����ļ���
			this.getTTable(TABLE).setParmValue(action);

			return;
		}
		String sql = getApplyQuerySQL();
		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
			sql = getHRMQuerySQL();
		}
		TParm action = new TParm(this.getDBTool().select(sql));
		// ���ݳ������ں͵�ǰ�����ڼ�������
		culmulateAge(action);// ����ļ���
		// if(action.getCount()<0){
		// this.messageBox("�������ݣ�");
		// return;
		// }
		this.getTTable(TABLE).setParmValue(action);
		// ÿ�β�ѯ���ȫѡ
		this.setValue("ALLCHECK", "N");

	}

	/**
	 * �õ���ѯ���
	 * 
	 * @return String
	 */
	public String getApplyQuerySQL() {
		String sql = "SELECT ";
		if (this.getValueString("BAR_CODE").length() > 0) {
			sql += "'Y' AS FLG, ";
		} else {
			sql += "'N' AS FLG, ";
		}
		// ִ��״̬-δִ�л���ִ��
		if ("N".equals(getConfirmRadioValue())) {// δִ��
			sql += // wanglong modify 20140423 BED_NO��Ϊ��ʾBED_NO_DESC
					" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,C.PAT_NAME,C.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
							+ "A.DEV_CODE,MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
							+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
							+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'δȷ��' AS BLOOD_STATE,B.TUBE_TYPE,B.TIME_LIMIT  FROM MED_APPLY A,SYS_FEE B, SYS_PATINFO C ";
		} else if ("Y".equals(getConfirmRadioValue())) {// ��ִ��
			sql += // wanglong modify 20140423 BED_NO��Ϊ��ʾBED_NO_DESC
					" A.PRINT_FLG,A.DEPT_CODE,A.STATION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,(SELECT S.BED_NO_DESC FROM SYS_BED S WHERE S.BED_NO = A.BED_NO) BED_NO,C.PAT_NAME,C.BIRTH_DATE,A.APPLICATION_NO,A.RPTTYPE_CODE,A.OPTITEM_CODE,"
							+ "A.DEV_CODE,A.MR_NO,A.IPD_NO,A.ORDER_DESC,A.CAT1_TYPE,A.OPTITEM_CHN_DESC,TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,"
							+ "A.DR_NOTE,A.EXEC_DEPT_CODE,A.URGENT_FLG,A.SEX_CODE,A.ORDER_NO,A.SEQ_NO,A.TEL,A.ADDRESS,A.CASE_NO,A.ORDER_CODE,A.ADM_TYPE,TO_CHAR(A.PRINT_DATE,'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE,"
							+ "A.BLOOD_USER,TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE,'��ȷ��' AS BLOOD_STATE, B.TUBE_TYPE,B.TIME_LIMIT FROM MED_APPLY A,SYS_FEE B, SYS_PATINFO C ";
		}
		// sql +=
		// "
		// PRINT_FLG,DEPT_CODE,STATION_CODE,CLINICAREA_CODE,CLINICROOM_NO,BED_NO,PAT_NAME,APPLICATION_NO,RPTTYPE_CODE,OPTITEM_CODE,"
		// +
		// "DEV_CODE,MR_NO,IPD_NO,ORDER_DESC,CAT1_TYPE,OPTITEM_CHN_DESC,TO_CHAR(ORDER_DATE,'YYYY/MM/DD
		// HH24:MI:SS') AS ORDER_DATE,"
		// +
		// "DR_NOTE,EXEC_DEPT_CODE,URGENT_FLG,SEX_CODE,BIRTH_DATE,ORDER_NO,SEQ_NO,TEL,ADDRESS,CASE_NO,ORDER_CODE,ADM_TYPE,PRINT_DATE,"
		// +
		// "BLOOD_USER,TO_CHAR(BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE, # FROM
		// MED_APPLY";
		TParm queryParm = this.getParmQuery();
		String columnName[] = queryParm.getNames();
		if (columnName.length > 0)
			sql += " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.MR_NO = C.MR_NO(+) AND ";
		int count = 0;

		for (String temp : columnName) {
			if (temp.equals("END_DATE"))
				continue;
			if (temp.equals("START_DATE")) {
				if (count > 0)
					sql += " AND ";
				sql += " A.START_DTTM BETWEEN TO_DATE('" + queryParm.getValue("START_DATE")
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + queryParm.getValue("END_DATE")
						+ "','YYYYMMDDHH24MISS')";
				count++;
				continue;
			}
			if (count > 0)
				sql += " AND ";
			sql += " A." + temp + "='" + queryParm.getValue(temp) + "' ";
			count++;
		}

		// ִ��״̬-δִ�л���ִ��
		if ("N".equals(getConfirmRadioValue())) {// δִ��
			sql += " AND A.BLOOD_USER IS NULL AND A.BLOOD_DATE IS NULL ";
		} else if ("Y".equals(getConfirmRadioValue())) {// ��ִ��
			sql += " AND A.BLOOD_USER IS NOT NULL AND A.BLOOD_DATE IS NOT NULL ";
		}
		// ҽ��-������
		if (this.getValueString("BAR_CODE").length() != 0) {
			sql += " AND A.APPLICATION_NO IN (" + execBarCode + ") ";
		}

		if (count > 0)
			sql += " AND ";
		if ("H".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY A.CAT1_TYPE,A.CASE_NO";
		else if ("I".equals(getAdmTypeRadioValue()))
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,A.BED_NO";
		else
			sql += " A.CAT1_TYPE='LIS'  ORDER BY A.CAT1_TYPE,A.CASE_NO,BED_NO";// BIL_FLG
																				// =
																				// 'Y'

		// System.out.println("��ͨsql:"+sql);
		return sql;
	}

	/**
	 * �õ�ִ��״̬
	 * 
	 * @return String
	 */
	public String getConfirmRadioValue() {
		if (this.getTRadioButton("CONFIRM_N").isSelected())
			return "N";// δִ��
		if (this.getTRadioButton("CONFIRM_Y").isSelected())
			return "Y";// ��ִ��
		return "N";
	}

	public String updateSql(String mr_no, String case_no, String cat1_type, String application_no, String order_no,
			String seq_no, String user_id, String now) {
		String sql = "update MED_APPLY set blood_user = '" + user_id + "'" + " ,blood_date = TO_DATE('" + now
				+ "','YYYY/MM/DD HH24:MI:SS') " + " where mr_no = '" + mr_no + "' and case_no = '" + case_no + "' "
				+ " and cat1_type = '" + cat1_type + "' and application_no = '" + application_no + "' "
				+ " and order_no = '" + order_no + "' and seq_no = '" + seq_no + "' ";

		return sql;
	}

	public void onQueryBar() {
		if (execBarCode.length() == 0) {
			execBarCode += "'" + this.getValueString("BAR_CODE") + "'";
		} else {
			execBarCode += "," + "'" + this.getValueString("BAR_CODE") + "'";
		}
		if (!this.getValueBoolean("APPLY_FLG")) {
			this.setValue("APPLY_FLG", "Y");// ����ִ�пؼ��Զ���ѡ
		}
		applyQuery();
		this.setValue("BAR_CODE", "");
		this.grabFocus("BAR_CODE");

	}

	public void onQueryPrintBar() {
		if (printCode.length() == 0) {
			printCode += "'" + this.getValueString("BAR_CODE_PRINT") + "'";
		} else {
			printCode += "," + "'" + this.getValueString("BAR_CODE_PRINT") + "'";
		}
		if (this.getValueBoolean("APPLY_FLG")) {
			this.setValue("APPLY_FLG", "N");// ����ִ�пؼ��Զ���ѡ
		}
		printQuery();
		// this.afterPrint();
		// onApplyCheck();
		this.setValue("BAR_CODE_PRINT", "");
		this.grabFocus("BAR_CODE_PRINT");

	}

	/**
	 * ��ӡ������
	 * 
	 * @param port
	 *            LPT�˿ں�
	 * @return
	 */
	public boolean printBarCode(String port) {// wanglong add 20140610
		this.printText.insert(0, "^XA");
		this.printText.append("^XZ");
		synchronized (this.printText) { // ͬ�� �� ��ӡ��
			FileWriter fw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter(port); // ������LPT3
				out = new PrintWriter(fw);
				out.print(this.printText.toString());
				return true;
			} catch (IOException e) {
				this.messageBox("��ӡ�����Ҳ���ʹ��" + port + "�˿ڵĴ�ӡ��");
				e.printStackTrace();
				return false;
			} finally {
				out.close();
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��������Ŀ�����
	 * 
	 * @param x
	 *            X����
	 * @param y
	 *            Y����
	 * @param W
	 *            ���
	 * @param R
	 *            Ratio
	 * @param H
	 *            �߶�
	 * @param barCode
	 *            ����
	 */
	public void addBarcode(int x, int y, int W, double R, int H, String barCode) {// wanglong
																					// add
																					// 20140610
		// ^XA
		// ^FO35,5^BY2,3.0,50
		// ^BC
		// ^FD>;1404090017501^FS
		// ^XZ
		this.printText.append("^FO" + x + "," + y + "^BY" + W + "," + R + "," + H + "^BC^FD>;" + barCode + "^FS");
	}

	/**
	 * ��������
	 * 
	 * @param x
	 * @param y
	 * @param str
	 */
	public void addText(int x, int y, String str) {// wanglong add 20140610
		addText(x, y, 24, str);
	}

	/**
	 * ��������
	 * 
	 * @param x
	 * @param y
	 * @param fontSize
	 *            �����С
	 * @param str
	 */
	public void addText(int x, int y, int fontSize, String str) {// fontSizeĬ��24
		this.printText.append(getTextCode(x, y, fontSize, str));
	}

	/**
	 * �������ֵĿ�����
	 * 
	 * @param x
	 * @param y
	 * @param fontSize
	 * @param str
	 * @return
	 */
	public static String getTextCode(int x, int y, int fontSize, String str) {// wanglong
																				// add
																				// 20140610
		StringBuffer temp = new StringBuffer();
		try {
			for (int i = 0; i < str.length(); i++) {
				String s = str.substring(i, i + 1);
				byte[] ba = s.getBytes("GBK");
				if (ba.length == 1) {
					temp.append(
							"^CI0^FO" + x + "," + (y + 4) + "^A0N," + fontSize + "," + fontSize + "^FD" + s + "^FS");
					x += fontSize / 2;
				} else if (ba.length == 2) {
					StringBuffer inTmp = new StringBuffer();
					for (int j = 0; j < 2; j++) {
						String hexStr = Integer.toHexString(ba[j] - 128);
						hexStr = hexStr.substring(hexStr.length() - 2);
						inTmp.append(hexStr + ")");
					}
					temp.append("^CI14^FO" + x + "," + y + "^AJN," + fontSize + "," + fontSize + "^FH)^FD)" + inTmp
							+ "^FS");
					x += fontSize;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return temp.toString();
	}
}
