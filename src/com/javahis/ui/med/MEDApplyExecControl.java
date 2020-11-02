package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.TableModel;

import jdo.adm.ADMTool;
import jdo.device.CallNo;
import jdo.device.LAB_Service;
import jdo.device.LAB_Service.DynamecMassage;
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
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import com.javahis.xml.Item;
import com.javahis.xml.Job;

/**
 * <p> Title: ����ִ��</p>
 * 
 * <p> Description: ����ִ�� </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: Javahis </p>
 * 
 * @author duzhw
 * @version 1.0
 */
public class MEDApplyExecControl extends TControl {
	
	/**
	 * ����������
	 */
	private String actionName = "action.med.MedAction";

	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
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
	private String execBarCode = "";//��¼�����������   yanjing 20140919
	

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
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		Object obj = this.getParameter();
		// this.messageBox(""+obj);

		if ((obj != null)&&(!"".equals(obj))) {
			if (obj instanceof TParm) {
				TParm parm = (TParm) obj;
				this.setAdmType(parm.getValue("ADM_TYPE"));
				this.setDeptCode(parm.getValue("DEPT_CODE"));
				this.setCaseNo(parm.getValue("CASE_NO"));
				this.setMrNo(parm.getValue("MR_NO"));
				this.setPatName(parm.getValue("PAT_NAME"));
				this.setAdmDate(parm.getTimestamp("ADM_DATE"));
				if(!StringUtil.isNullString(parm.getValue("COMPANY_CODE")) ){//add by wanglong 20130726
				    this.setCompanyCode(parm.getValue("COMPANY_CODE"));
				}
				if(!StringUtil.isNullString(parm.getValue("CONTRACT_CODE"))){//add by wanglong 20130726
                    this.setContractCode(parm.getValue("CONTRACT_CODE"));
                }
				if ("I".equals(this.getAdmType())) {
					this.setIpdNo(parm.getValue("IPD_NO"));
					this.setStationCode(parm.getValue("STATION_CODE"));
					this.setBedNo(parm.getValue("BED_NO"));
					this.setStationCode(Operator.getStation());
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
				// this.messageBox(""+obj);
				// this.messageBox(""+obj.toString());
				this.setAdmType("" + obj);
				// this.setPopedem("SYSOPERATOR",true);
				String date = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyyMMdd")
						+ "000000";
				this
						.setAdmDate(StringTool.getTimestamp(date,
								"yyyyMMddHHmmss"));
			}
			this.grabFocus("BAR_CODE");

		} else {
			if (getRadioButton("O").isSelected()) {//����
				this.setAdmType("O");
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
				getTTextField("MR_NO").setEnabled(true);
				getTTextFormat("COMPANY_CODE").setEnabled(false);// 
				getTTextFormat("CONTRACT_CODE").setEnabled(false);
				getTTextField("START_SEQ_NO").setEnabled(false);
				getTTextField("END_SEQ_NO").setEnabled(false);//
				this.grabFocus("BAR_CODE");
				
				
	        }else if(getRadioButton("E").isSelected()){//����
	        	this.setAdmType("E");
	        }
//	        else if(getRadioButton("I").isSelected()){//סԺ
//	        	this.setAdmType("I");
//	        }
	        else if(getRadioButton("H").isSelected()){//����
	        	this.setAdmType("H");
	        }
			String date = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyyMMdd")
					+ "000000";
			this.setAdmDate(StringTool.getTimestamp(date,
							"yyyyMMddHHmmss"));
			
		}
		/**
		 * ��ʼ��Ȩ��
		 */
		onInitPopeDem();
		/**
		 * ��ʼ��ҳ��
		 */
		initPage();
		/**
		 * ��ʼ���¼�
		 */
		initEvent();
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
	 * ��ʼ���¼�
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		// �������
		addListener(getTTable(TABLE));
	}

	/**
	 * ��ʼ������-duzhw
	 */
	// public void onInitParameter(){
	// /**
	// * 1��һ��Ȩ��(NORMAL)
	// * 2����ɫȨ��(SYSOPERATOR)
	// * 2�����Ȩ��(SYSDBA)
	// */
	// //һ��Ȩ��
	// // this.setPopedem("NORMAL",true);
	// //��ɫȨ��
	// // this.setPopedem("SYSOPERATOR",true);
	// //���Ȩ��
	// // this.setPopedem("SYSDBA",true);
	// // this.setParameter("H");
	// }
	//ѡ��һ��ʱ�������ͬ�Ļ�ͬʱ��ѡ
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
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * ѡ���¼�
	 */
	public void onSelRadioButton(Object obj) {
		this.onClear();
		if ("O".equals("" + obj)) {
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
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,50;�����,100;�������,80,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;CASE_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("E".equals("" + obj)) {
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
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("H".equals("" + obj)) {//����
			
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
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;���,50;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");//caowl 20130320 ����Ա�����
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");//caowl 20130320 ����Ա�����
		}
		this.onQuery();
	}
	/**
	 * 
	 */
	public void onConRadioButton(Object obj){
		if ("Y".equals("" + obj)){
			callFunction("UI|save|setEnabled", false);
		}else if("N".equals("" + obj)){
			callFunction("UI|save|setEnabled", true);
		}
		this.onQuery();
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
				//this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(true);
				this.getTRadioButton("E").setEnabled(false);
				this.getTRadioButton("O").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(false);
				this.getTRadioButton("H").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				//getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(false);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				//getTTextField("IPD_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(false);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(true);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(true);
				//getTTextField("IPD_NO").setEnabled(false);
				getTTextField("BED_NO").setEnabled(false);
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
				//this.getTRadioButton("I").setEnabled(true);
				getTTextFormat("DEPT_CODEMED").setEnabled(true);
				getTTextFormat("STATION_CODEMED").setEnabled(false);
				getTTextFormat("CLINICAREA_CODEMED").setEnabled(false);
				getTTextFormat("CLINICROOM_CODEMED").setEnabled(false);
				//getTTextField("IPD_NO").setEnabled(false);
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
		//��ȡ��ʼʱ�䣬�����ʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();
		this.setValue("START_DATE",sysDate.toString().substring(0,10).replace('-','/')+" 00:00:00");
		this.setValue("END_DATE", sysDate.toString().substring(0,10).replace('-','/')+" 23:59:59");
		
		if ("O".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,70;�����,100;�������,80,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("E".equals(this.getAdmType())) {
			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this
					.getTTable(TABLE)
					.setHeader(
							"ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;ҽ������,160;����ʱ��,140;160;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		
		if ("H".equals(this.getAdmType())) {

//			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� begin */
//			Date d = new Date(sysDate.getTime());
//			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
//					+ d.getDate() + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
////			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
////					+ d.getDate() + " 23:59:59";
//
//			// ��ʼ��ʱ��
//			this.setValue("START_DATE", getTimestamp(begin));
//			//this.setValue("END_DATE", getTimestamp(end));
//			/** Yuanxm add ��ʼʱ�� �����ʱ���ʼ�� end */

			this.setValue("MR_NO", this.getMrNo());
			this.setValue("PAT_NAME", this.getPatName());
			this.setValue("DEPT_CODEMED", this.getDeptCode());
			this.setValue("CLINICAREA_CODEMED", this.getClinicareaCode());
			this.setValue("CLINICROOM_CODEMED", this.getClinicroomNo());
			this.setValue("COMPANY_CODE", this.getCompanyCode());//add by wanglong 20130726
            if (!StringUtil.isNullString(this.getCompanyCode())) {
	            TParm contractParm = contractD.onQueryByCompany(this.getCompanyCode());//add-by-wanglong-20130821
	            contract.setPopupMenuData(contractParm);
	            contract.setComboSelectRow();
	            contract.popupMenuShowData();
	            contract.setValue(this.getContractCode()); //add-end
			}
//			this.setValue("CONTRACT_CODE", this.getContractCode());
			this
					.getTTable(TABLE)
					.setHeader("ѡ,30,boolean;ӡ,30,boolean;��,30,boolean;���,50;ҽ������,160;����ʱ��,140;����,100,DEPT_CODE;����,100,CLINICAREA_CODE;����,140,CLINICROOM_CODE;����,100;�����,100;�������,120,RPTTYPE_CODE;���岿λ,120,ITEM_CODE;��������,100,DEV_CODE;������,100;������Ա,70,USER_CODE;����ʱ��,140");//caowl 20130320 �������һ��
			this
					.getTTable(TABLE)
					.setParmMap("FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");//caowl 20130320 �������һ��
		}
		// ��ѯ
		//this.onQuery();
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
	 * ����
	 */
	public void onSave(){
		boolean have = false;
		boolean success = false;
		String user_id = Operator.getID();
		String now = SystemTool.getInstance().getDate().toString().substring(0, 19);
		String sql = "";
		int rowCount = this.getTTable(TABLE).getRowCount();
		if(rowCount < 0){
			this.messageBox("û�����ݣ�");
			return;
		}
		// �����ж�
		if (!checkPW()) {
			return;
		}
		for (int i = 0; i < rowCount; i++) {
			TParm temp = this.getTTable(TABLE).getParmValue().getRow(i);
			if (temp.getBoolean("FLG")){
				String mrNo = temp.getValue("MR_NO");
				String caseNo = temp.getValue("CASE_NO");
				String cat1_type = temp.getValue("CAT1_TYPE");
				String application_no = temp.getValue("APPLICATION_NO");
				String order_no = temp.getValue("ORDER_NO");
				String seq_no = temp.getValue("SEQ_NO");
				//���±�med_apply
				sql = updateSql(mrNo, caseNo, cat1_type,application_no,order_no,seq_no, user_id, now);
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(sql));
				have = true;
				success = true;
			}
		}
		if(!have){
			this.messageBox("û��ѡ�����ݣ�");
			return;
		}
		if(success){
			this.messageBox("����ɹ���");
			//ˢ��ҳ��
			onQuery();
			execBarCode = "";
			return;
		}else{
			this.messageBox("����ʧ�ܣ�");
			execBarCode = "";
			return;
		}
	}
	public String updateSql(String mr_no, String case_no,String cat1_type,String application_no,
			String order_no,String seq_no, String user_id, String now){
		String sql = "update MED_APPLY set blood_user = '"+user_id+"'" +
				" ,blood_date = TO_DATE('"+now+"','YYYY/MM/DD HH24:MI:SS') " +
				" where mr_no = '"+mr_no+"' and case_no = '"+case_no+"' " +
				" and cat1_type = '"+cat1_type+"' and application_no = '"+application_no+"' " +
				" and order_no = '"+order_no+"' and seq_no = '"+seq_no+"' ";
		
		return sql;
	}
	public void onQueryBar(){
		if(execBarCode.length()==0){
			execBarCode += "'"+this.getValueString("BAR_CODE")+"'";
		}else{
			execBarCode += ","+"'"+this.getValueString("BAR_CODE")+"'";
		}
		onQuery();
		this.setValue("BAR_CODE", "");
		this.grabFocus("BAR_CODE");
		
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		
		if (this.getValueString("MR_NO").trim().length() != 0) {

			this.setValue("MR_NO", PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO")));
			this.setValue("PAT_NAME", PatTool.getInstance().getNameForMrno(
					PatTool.getInstance().checkMrno(
							this.getValueString("MR_NO"))));
			if ("O".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "O");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("E".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "E");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
				this.setValue("CLINICAREA_CODEMED", patInfParm
						.getValue("CLINICAREA_CODE"));
				this.setValue("CLINICROOM_CODEMED", patInfParm
						.getValue("CLINICROOM_NO"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			if ("H".equals(getAdmTypeRadioValue())) {
				TParm patInfParm = getPatInfo("MR_NO", this
						.getValueString("MR_NO"), "H");
				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
				this.setCaseNo(patInfParm.getValue("CASE_NO"));
			}
			String sql = getQuerySQL();
			//System.out.println("MR-SQL="+sql);
			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
				sql = getHRMQuerySQL();
			}
			TParm action = new TParm(this.getDBTool().select(sql));
			this.getTTable(TABLE).setParmValue(action);
		
			return;
		}
//		if (this.getValueString("IPD_NO").trim().length() != 0) {
//			this.setValue("IPD_NO", PatTool.getInstance().checkIpdno(
//					this.getValueString("IPD_NO")));
//			if ("O".equals(getAdmTypeRadioValue())) {
//				TParm patInfParm = getPatInfo("IPD_NO", this
//						.getValueString("IPD_NO"), "O");
//				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("CLINICAREA_CODEMED", patInfParm
//						.getValue("CLINICAREA_CODE"));
//				this.setValue("CLINICROOM_CODEMED", patInfParm
//						.getValue("CLINICROOM_NO"));
//				this.setCaseNo(patInfParm.getValue("CASE_NO"));
//			}
//			if ("E".equals(getAdmTypeRadioValue())) {
//				TParm patInfParm = getPatInfo("IPD_NO", this
//						.getValueString("IPD_NO"), "E");
//				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("CLINICAREA_CODEMED", patInfParm
//						.getValue("CLINICAREA_CODE"));
//				this.setValue("CLINICROOM_CODEMED", patInfParm
//						.getValue("CLINICROOM_NO"));
//				this.setCaseNo(patInfParm.getValue("CASE_NO"));
//			}
//			if ("I".equals(getAdmTypeRadioValue())) {
//				TParm patInfParm = getPatInfo("IPD_NO", this
//						.getValueString("IPD_NO"), "I");
//				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
//				this.setValue("STATION_CODEMED", patInfParm
//						.getValue("STATION_CODE"));
//				this.setValue("IPD_NO", patInfParm.getValue("IPD_NO"));
//				this.setValue("BED_NO", patInfParm.getValue("BED_NO"));
//				this.setCaseNo(patInfParm.getValue("CASE_NO"));
//			}
//			if ("H".equals(getAdmTypeRadioValue())) {
//				TParm patInfParm = getPatInfo("IPD_NO", this
//						.getValueString("IPD_NO"), "H");
//				this.setValue("PAT_NAME", patInfParm.getValue("PAT_NAME"));
//				this.setValue("DEPT_CODEMED", patInfParm.getValue("DEPT_CODE"));
//				this.setCaseNo(patInfParm.getValue("CASE_NO"));
//			}
//			String sql = getQuerySQL();
//			if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
//				sql = getHRMQuerySQL();
//			}
//			TParm action = new TParm(this.getDBTool().select(sql));
//			this.getTTable(TABLE).setParmValue(action);
//			return;
//		}
		String sql = getQuerySQL();
		if ("H".equals(getAdmTypeRadioValue())) {// add by wanglong 20121214
			sql = getHRMQuerySQL();
		}
		//System.out.println("��ѯsql="+sql);
		TParm action = new TParm(this.getDBTool().select(sql));
//		if(action.getCount()<0){
//        	this.messageBox("�������ݣ�");
//        	return;
//        }
		this.getTTable(TABLE).setParmValue(action);
		// ÿ�β�ѯ���ȫѡ
		this.setValue("ALLCHECK", "N");

		
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
				TParm oParm = new TParm(
						this
								.getDBTool()
								.select(
										"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
												+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", oParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", oParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", oParm.getData("CLINICROOM_NO",
						0));
				TParm oIparm = new TParm(this.getDBTool().select(
						"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
								+ value + "'"));
				result.setData("PAT_NAME", oIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE "
							+ columnName + "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "O");
				Object obj = this.openDialog(
						"%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp
							.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp
							.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData(
						"CLINICROOM_NO", 0));
			}

		}
		if ("E".equals(admType)) {
			if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
				TParm eParm = new TParm(
						this
								.getDBTool()
								.select(
										"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE CASE_NO='"
												+ this.getCaseNo() + "'"));
				result.setData("CASE_NO", eParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", eParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", eParm.getData("CLINICROOM_NO",
						0));
				TParm eIparm = new TParm(this.getDBTool().select(
						"SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
								+ value + "'"));
				result.setData("PAT_NAME", eIparm.getData("PAT_NAME", 0));
				return result;
			}
			TParm queryParm = new TParm(this.getDBTool().select(
					"SELECT ADM_DATE,CASE_NO,CLINICAREA_CODE,CLINICROOM_NO FROM REG_PATADM WHERE "
							+ columnName + "='" + value + "'"));
			if (queryParm.getCount() > 1) {
				queryParm.setData("ADM_TYPE", "E");
				Object obj = this.openDialog(
						"%ROOT%\\config\\med\\MEDPatInfo.x", queryParm);
				if (obj != null) {
					TParm temp = (TParm) obj;
					result.setData("CASE_NO", temp.getData("CASE_NO"));
					result.setData("CLINICAREA_CODE", temp
							.getData("CLINICAREA_CODE"));
					result.setData("CLINICROOM_NO", temp
							.getData("CLINICROOM_NO"));
				}
			} else {
				result.setData("CASE_NO", queryParm.getData("CASE_NO", 0));
				result.setData("CLINICAREA_CODE", queryParm.getData(
						"CLINICAREA_CODE", 0));
				result.setData("CLINICROOM_NO", queryParm.getData(
						"CLINICROOM_NO", 0));
			}
        }
        if ("H".equals(admType)) {
            if (this.getCaseNo() != null || this.getCaseNo().length() != 0) {
                TParm hParm =
                        new TParm(
                                this.getDBTool()
                                        .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE CASE_NO='"
                                                        + this.getCaseNo() + "'"));
                result.setData("CASE_NO", hParm.getData("CASE_NO", 0));
                result.setData("DEPT_CODE", hParm.getData("DEPT_CODE", 0));
                TParm hIparm =
                        new TParm(this.getDBTool()
                                .select("SELECT * FROM SYS_PATINFO WHERE " + columnName + "='"
                                                + value + "'"));
                result.setData("PAT_NAME", hIparm.getData("PAT_NAME", 0));
                return result;
            }
            TParm queryParm =
                    new TParm(
                            this.getDBTool()
                                    .select("SELECT REPORT_DATE AS ADM_DATE,CASE_NO,DEPT_CODE FROM HRM_PATADM WHERE "
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
        TParm parm =
                new TParm(this.getDBTool().select("SELECT * FROM SYS_PATINFO WHERE " + columnName
                                                          + "='" + value + "'"));
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
    	if(this.getValueString("BAR_CODE").length() > 0){
    		sql += "'Y' AS FLG, ";
    	}else{
    		sql += "'N' AS FLG, ";
    	}
        sql +=
                " PRINT_FLG,DEPT_CODE,STATION_CODE,CLINICAREA_CODE,CLINICROOM_NO,BED_NO,PAT_NAME,APPLICATION_NO,RPTTYPE_CODE,OPTITEM_CODE,DEV_CODE,MR_NO,IPD_NO,ORDER_DESC,CAT1_TYPE,OPTITEM_CHN_DESC,TO_CHAR(ORDER_DATE,'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,DR_NOTE,EXEC_DEPT_CODE,URGENT_FLG,SEX_CODE,BIRTH_DATE,ORDER_NO,SEQ_NO,TEL,ADDRESS,CASE_NO,ORDER_CODE,ADM_TYPE,PRINT_DATE,BLOOD_USER,TO_CHAR(BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS BLOOD_DATE FROM MED_APPLY";
        TParm queryParm = this.getParmQuery();
        String columnName[] = queryParm.getNames();
        if (columnName.length > 0) sql += " WHERE ";
        int count = 0;
        
        for (String temp : columnName) {
            if (temp.equals("END_DATE")) continue;
            if (temp.equals("START_DATE")) {
                if (count > 0) sql += " AND ";
                sql +=
                        " START_DTTM BETWEEN TO_DATE('" + queryParm.getValue("START_DATE")
                                + "','YYYYMMDDHH24MISS') AND TO_DATE('"
                                + queryParm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')";
                count++;
                continue;
            }
            if (count > 0) sql += " AND ";
            sql += temp + "='" + queryParm.getValue(temp) + "' ";
            count++;
        }
        
        //ִ��״̬-δִ�л���ִ��
        if("N".equals(getConfirmRadioValue())){//δִ��
        	sql += " AND BLOOD_USER IS NULL AND BLOOD_DATE IS NULL ";
        }else if("Y".equals(getConfirmRadioValue())){//��ִ��
        	sql += " AND BLOOD_USER IS NOT NULL AND BLOOD_DATE IS NOT NULL ";
        }
        //ҽ��-������
        if(this.getValueString("BAR_CODE").length() != 0){
        	sql += " AND APPLICATION_NO IN ("+execBarCode+")  ";
        }
        
        if (count > 0) sql += " AND ";
        if ("H".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS' AND STATUS <> 9 ORDER BY CAT1_TYPE,CASE_NO";
        else if ("I".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS'  ORDER BY CAT1_TYPE,CASE_NO,BED_NO";
        else sql += " CAT1_TYPE='LIS'  ORDER BY CAT1_TYPE,CASE_NO,BED_NO";
        
//        System.out.println("��ͨsql:"+sql);
        return sql;
    }
    /**
     * ��������Ƽ۴���
     */
    public void onCharge(Object isDbClick) {
    	boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
    	TParm ibsParm = new TParm();
    	// ��ѡ���ٵ��ý���
        if (!dbClickFlg) {
        	String mrNo = this.getValueString("MR_NO");
        	System.out.println("mrNo="+mrNo);
        	if(mrNo.length()>0){
        		int row=getTTable(TABLE).getSelectedRow();
        		TParm  parm=getTTable(TABLE).getParmValue().getRow(row);
        		ibsParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        		ibsParm.setData("MR_NO", mrNo);
				ibsParm.setData("SYSTEM", "ONW");
                ibsParm.setData("ONW_TYPE", getAdmTypeRadioValue());
            	//openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", ibsParm);
            	TParm result = (TParm)this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x", ibsParm);
        	}
        	
        }
    	
    	
    }
    
    /**
     * ��������Ƽ۴���
     */
    /**
    public void onCharge(Object isDbClick) {
        // this.messageBox("come in.");
        boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
        // �õ�ѡ�е�����
        
         * int selRow = masterTbl.getSelectedRow(); if (selRow < 0) { //
         * messageBox("��ѡ�񲡻�ҽ��"); // return; outsideParm.setData("INWEXE",
         * "CASE_NO", getCaseNo()); outsideParm.setData("INWEXE", "ORDER_NO",
         * ""); outsideParm.setData("INWEXE", "ORDER_SEQ", ""); } else { TParm
         * tableParm = masterTbl.getParmValue(); // ȡ����Ҫ����IBS�Ĳ��� String caseNo =
         * tableParm.getValue("CASE_NO", selRow); String orderNo =
         * tableParm.getValue("ORDER_NO", selRow); String orderSeq =
         * tableParm.getValue("ORDER_SEQ", selRow); // ����IBS������
         * outsideParm.setData("INWEXE", "CASE_NO", caseNo);
         * outsideParm.setData("INWEXE", "ORDER_NO", orderNo);
         * outsideParm.setData("INWEXE", "ORDER_SEQ", orderSeq); }
         
        TParm ibsParm = new TParm();
        int selRow = this.getTTable(TABLE).getSelectedRow();
        String caseNo_ = "";
        String mrNo_ = "";
        String ipdNo_ = "";
        String station_ = "";
        String bedNo_ = "";
        String execDeptCode_ = "";
        String vsDrCode_ = ""; 
        
        String clpCode_ = "";

        if (selRow != -1) {
            TParm tableParm = this.getTTable(TABLE).getParmValue();
            // System.out.println("==tableParm=="+tableParm);
            // ȡ����Ҫ����SUM�Ĳ���
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
            execDeptCode_ = tableParm.getValue("ORDER_DEPT_CODE", selRow);//zhangp �ɱ������޸�Ϊ���ң�����ƷѲ����ٴ�
            // System.out.println("===execDeptCode_==="+execDeptCode_);
            vsDrCode_ = tableParm.getValue("VS_DR_CODE", selRow);
            // System.out.println("===vsDrCode_==="+vsDrCode_);

        } else {
            caseNo_ = this.getCaseNo();
            mrNo_ = this.getMrNo();
            ipdNo_ = this.getIpdNo();
            station_ = this.getStationCode();
            execDeptCode_ = Operator.getDept();
            // this.getValue("");
            vsDrCode_ = this.getValueString("VC_CODE");
            // System.out.println("===vsDrCode_==="+vsDrCode_);
            // outsideParm.getValue("")
            // this.getValue("");
            TParm parm = new TParm();
            parm.setData("CASE_NO", caseNo_);
            parm = ADMTool.getInstance().getADM_INFO(parm);
            bedNo_ = parm.getValue("BED_NO", 0);
            // System.out.println("===bedNo_==="+bedNo_);
            // outsideParm.getValue(name)

        }

        // ��ѡ���ٵ��ý���
        if (!dbClickFlg) {
            // outsideParm.setData("IBS", "TYPE", "INW");
            ibsParm.setData("IBS", "CASE_NO", caseNo_);
            ibsParm.setData("IBS", "IPD_NO", ipdNo_);
            ibsParm.setData("IBS", "MR_NO", mrNo_);
            ibsParm.setData("IBS", "BED_NO", bedNo_);
            ibsParm.setData("IBS", "DEPT_CODE", execDeptCode_);
            ibsParm.setData("IBS", "STATION_CODE", station_);
            ibsParm.setData("IBS", "VS_DR_CODE", vsDrCode_);
            ibsParm.setData("IBS", "TYPE", "INW");
            ibsParm.setData("IBS", "CLNCPATH_CODE", clpCode_);

            openDialog("%ROOT%\\config\\ibs\\IBSOrderm.x", ibsParm);
            
             * //����MovePane��˫��Ч�� mp1.onDoubleClicked(isCharge); if (!isCharge) {
             * 
             * getTPanel("ChargePanel").addItem("ChargePanel_",
             * "%ROOT%\\config\\ibs\\IBSOrderm.x", outsideParm, false); }
             * isCharge = isCharge ? false : true; //�����ұߵİ�ť
             * mp2.onDoubleClicked(true); mp3.onDoubleClicked(true);
             
        } else { // ������ţ�˫������ outsideParm
            this.callFunction("UI|ChargePanel_|getINWData", ibsParm);
        }
    }
	
	**/
	
	/**
	 * �õ���ѯ����
	 * 
	 * @return TParm
	 */
    public TParm getParmQuery() {
        TParm result = new TParm();
        result.setData("ADM_TYPE", this.getAdmTypeRadioValue());
        result.setData("START_DATE", StringTool.getString((Timestamp) this.getValue("START_DATE"),
                                                          "yyyyMMddHHmmss"));
        result.setData("END_DATE", StringTool.getString((Timestamp) this.getValue("END_DATE"),
                                                        "yyyyMMddHHmmss"));
        if (this.getValueString("DEPT_CODEMED").length() != 0) {
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
//        if (getPrintStatus().length() != 0) {
//            result.setData("PRINT_FLG", getPrintStatus());
//        }
        if (this.getValueString("MR_NO").length() != 0) {
            result.setData("MR_NO", this.getValueString("MR_NO"));
        }
        if (this.getValueString("IPD_NO").length() != 0) {
            result.setData("IPD_NO", this.getValueString("IPD_NO"));
        }
        // if (this.getValueString("BED_NO").length() != 0) {
        // result.setData("BED_NO", this.getValueString("BED_NO"));
        // }
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
	public String getHRMQuerySQL() {// add by wanglong 20121214
		String sql = "SELECT 'N' AS FLG, A.PRINT_FLG,  A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, A.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC, A.CAT1_TYPE,A.OPTITEM_CHN_DESC, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, A.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE,C.SEQ_NO AS NO "//caowl 20130320 ����Ա�����
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C"
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO "
				+ "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO "
				+ "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
        sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// �ż���
        String srartDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// ��ʼʱ��
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");// ����ʱ��
        sql +=
                " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate + "','YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
        if (this.getValueString("REGION_CODE").length() != 0) {// ����
            sql += " AND A.REGION_CODE = '" + this.getValueString("REGION_CODE") + "'";
        }
        if (this.getValueString("DEPT_CODEMED").length() != 0) {// ����
            sql += " AND A.DEPT_CODE = '" + this.getValueString("DEPT_CODEMED") + "'";
        }
//        if (getPrintStatus().length() != 0) {// ��ӡ״̬��δ��ӡ���Ѵ�ӡ��ȫ����
//            sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
//        }
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
        
        //ִ��״̬-δִ�л���ִ��
        if("N".equals(getConfirmRadioValue())){//δִ��
        	sql += " AND A.BLOOD_USER IS NULL AND A.BLOOD_DATE IS NULL ";
        }else if("Y".equals(getConfirmRadioValue())){//��ִ��
        	sql += " A.AND BLOOD_USER IS NOT NULL AND A.BLOOD_DATE IS NOT NULL ";
        }
        
		sql += " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO ASC ,A.CAT1_TYPE ASC,A.START_DTTM DESC, A.CASE_NO ASC";// caowl
																																	// 20130305
																																	// ��������������
		// System.out.println("�������sql:"+sql);
		return sql;
	}
	
	/**
	 * ����������֤
	 * 
	 * @return boolean
	 */
	public boolean checkPW() {
		String inwCheck = "inwCheck";
		String value = (String) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", inwCheck);
		if (value == null) {
			return false;
		}
		return value.equals("OK");
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
//		if (this.getTRadioButton("I").isSelected())
//			return "I";
		if (this.getTRadioButton("H").isSelected())
			return "H";
		return "O";
	}
	/**
	 * �õ�ִ��״̬
	 * 
	 * @return String
	 */
	public String getConfirmRadioValue() {
		if (this.getTRadioButton("CONFIRM_N").isSelected())
			return "N";//δִ��
		if (this.getTRadioButton("CONFIRM_Y").isSelected())
			return "Y";//��ִ��
		return "N";
	}

	/**
	 * �õ���ӡ״̬
	 * 
	 * @return String
	 */
//	public String getPrintStatus() {
//		if (this.getTRadioButton("ALL").isSelected())
//			return "";
//		if (this.getTRadioButton("ONPRINT").isSelected())
//			return "N";
//		if (this.getTRadioButton("YESPRINT").isSelected())
//			return "Y";
//		return "";
//	}

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
	 * ��ѯ�������
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public String getTubeType(String orderCode) {
		// "SYS_TUBECONVERT"
		String sqlSys = "SELECT TUBE_TYPE FROM SYS_FEE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sqlSys));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("TUBE_TYPE", 0);
	}

	/**
	 * �õ��������뼱�����
	 */
    public String geturGentFlg(String appNoStr) {
        String flg = "";
        if (appNoStr.equals("")) return flg;
        String medsql =
                "SELECT CASE_NO,ORDER_NO,SEQ_NO,ADM_TYPE FROM MED_APPLY "
                        + " WHERE APPLICATION_NO='" + appNoStr + "' AND CAT1_TYPE='LIS'";
        TParm parm = new TParm(this.getDBTool().select(medsql));
        if (parm.getErrCode() < 0) return flg;
        if (parm.getCount() <= 0) return flg;
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
                orderSql =
                        " SELECT URGENT_FLG FROM OPD_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND RX_NO='" + orderNo + "' AND SEQ_NO='" + seqNo + "'";
            }
            if (admType.equals("I")) {
                orderSql =
                        " SELECT URGENT_FLG FROM ODI_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND ORDER_NO='" + orderNo + "' AND ORDER_SEQ='" + seqNo + "'";
            }
            if (admType.equals("H")) {
                orderSql =
                        " SELECT URGENT_FLG FROM HRM_ORDER WHERE CASE_NO='" + caseNo + "'"
                                + " AND SEQ_NO='" + seqNo + "'";
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
        TParm parm =
                new TParm(this.getDBTool()
                        .select("SELECT CATEGORY_CHN_DESC FROM SYS_CATEGORY WHERE CATEGORY_CODE='"
                                        + categoryCode + "'"));
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
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "' AND ID='" + id + "'"));
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
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT STATION_DESC FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
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
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ���
	 */
    public void onClear() {
        TRadioButton h = (TRadioButton) this.getComponent("H");
        String date =
                StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd") + "000000";
        String endDate =StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd") + "235959";
        Timestamp sysDate = StringTool.getTimestamp(date, "yyyyMMddHHmmss");
        Timestamp endTime = StringTool.getTimestamp(endDate, "yyyyMMddHHmmss");
        if (this.getPopedem("NORMAL")) {
            this.onInit();
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE");// caowl
                                                                                                                       // ����COMPANY_CODE;CONTRACT_CODE
            } else {
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK");
                this.setValue("START_DATE", sysDate);
                this.setValue("END_DATE", endTime);
            }
            this.getTRadioButton("ONPRINT").setSelected(true);
            this.getTTable(TABLE).removeRowAll();
        }
        else if (this.getPopedem("SYSOPERATOR")) {
            this.onInit();
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE");

			}else{
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED");
				this.setValue("START_DATE", sysDate);
				this.setValue("END_DATE", endTime);
			}
			
			this.getTRadioButton("ONPRINT").setSelected(true);
			this.getTTable(TABLE).removeRowAll();
        }
        else if (this.getPopedem("SYSDBA")) {
            if (h.isSelected()) {
                Date d = new Date(sysDate.getTime());
                String begin =
                        (d.getYear() + 1900) + "/" + (d.getMonth()) + "/" + d.getDate()
                                + " 00:00:00";// caowl 20130305 �����Ĭ������Ϊǰһ����
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // ��ʼ��ʱ��
				this.setValue("START_DATE", getTimestamp(begin));
				this.setValue("END_DATE", getTimestamp(end));
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE");

			}else{
				clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;BAR_CODE");
				this.getTRadioButton("ONPRINT").setSelected(true);
				this.setValue("START_DATE", sysDate);
			}
			
			this.setValue("END_DATE", endTime);
			this.getTTable(TABLE).removeRowAll();
		}else{
			clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;DEPT_CODEMED;STATION_CODEMED;CLINICAREA_CODEMED;CLINICROOM_CODEMED;BAR_CODE");
			this.setValue("START_DATE", sysDate);
			this.setValue("END_DATE", endTime);
			this.getTTable(TABLE).removeRowAll();
		}
      
	}

	/**
     * ����
     */
    public void onRead() {
        // TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
        TParm patParm = jdo.ekt.EKTIO.getInstance().TXreadEKT();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        this.setValue("MR_NO", patParm.getValue("MR_NO"));
        this.grabFocus("BAR_CODE");
        this.onQuery();
    }

	// $$===================add by lx 2011/02/13 ����̩�ĽкŽӿ�
	// start===========================$$//
	

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
	//========add by wanglong 20130726
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
    //========add end
	/**
	 * �Ҽ�
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table
				.setPopupMenuSyntax("��ʾ����ҽ��ϸ�� \n Display collection details with your doctor,openRigthPopMenu|TABLE");
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
			double ownPrice = parm.getDouble("OWN_PRICE", i)
					* parm.getDouble("DOSAGE_QTY", i);
			result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
			result.addData("OWN_AMT", ownPrice);
			result
					.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE",
							i));
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
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
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
				 //System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

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
}
