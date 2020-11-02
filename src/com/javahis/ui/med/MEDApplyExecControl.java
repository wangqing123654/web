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
 * <p> Title: 采样执行</p>
 * 
 * <p> Description: 采样执行 </p>
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
	 * 动作类名称
	 */
	private String actionName = "action.med.MedAction";

	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	/**
	 * 门急住别
	 */
	private String admType;
	/**
	 * 科室
	 */
	private String deptCode;
	/**
	 * 门诊看诊日期住院为当前日期
	 */
	private Timestamp admDate;
	/**
	 * 就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo;
	/**
	 * 病患姓名
	 */
	private String patName;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 床号
	 */
	private String bedNo;
	/**
	 * 病区
	 */
	private String stationCode;
	/**
	 * 诊区
	 */
	private String clinicareaCode;
	/**
	 * 诊室
	 */
	private String clinicroomNo;
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";
	/**
	 * 团体代码、合同代码
	 */
	private String companyCode, contractCode;// add by wanglong 20121214

    /**
	 * 合同对象
	 */
	private HRMContractD contractD;// add by wanglong 20121214
	/**
	 * 合同TTextFormat
	 */
	private TTextFormat contract;// add by wanglong 20121214
	private String execBarCode = "";//记录采样的条码号   yanjing 20140919
	

	public void onInit() {
		super.onInit();
		
		contractD = new HRMContractD();// add by wanglong 20121214
		contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
		/**
		 * REG_CLINICAREA诊区 REG_CLINICROOM诊室 (住院COMBO权限)(门诊权限)(门急住别权限)
		 */
		// ================pangben modify 20110405 start 区域锁定
		setValue("REGION_CODE", Operator.getRegion());
		// ================pangben modify 20110405 stop
		// ========pangben modify 20110421 start 权限添加
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
					// 一般权限
					if ("1".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("NORMAL", true);
						this.setPopedem("SYSOPERATOR", false);
						this.setPopedem("SYSDBA", false);
					}
					// 角色权限
					if ("2".equals(parm.getValue("POPEDEM"))) {
						this.setPopedem("SYSOPERATOR", true);
						this.setPopedem("NORMAL", false);
						this.setPopedem("SYSDBA", false);
					}
					// 最高权限
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
			if (getRadioButton("O").isSelected()) {//门诊
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
				
				
	        }else if(getRadioButton("E").isSelected()){//门诊
	        	this.setAdmType("E");
	        }
//	        else if(getRadioButton("I").isSelected()){//住院
//	        	this.setAdmType("I");
//	        }
	        else if(getRadioButton("H").isSelected()){//健检
	        	this.setAdmType("H");
	        }
			String date = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyyMMdd")
					+ "000000";
			this.setAdmDate(StringTool.getTimestamp(date,
							"yyyyMMddHHmmss"));
			
		}
		/**
		 * 初始化权限
		 */
		onInitPopeDem();
		/**
		 * 初始化页面
		 */
		initPage();
		/**
		 * 初始化事件
		 */
		initEvent();
	}
	 /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

	/**
	 * 初始化事件
	 */
	public void initEvent() {
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		// 排序监听
		addListener(getTTable(TABLE));
	}

	/**
	 * 初始化参数-duzhw
	 */
	// public void onInitParameter(){
	// /**
	// * 1、一般权限(NORMAL)
	// * 2、角色权限(SYSOPERATOR)
	// * 2、最高权限(SYSDBA)
	// */
	// //一般权限
	// // this.setPopedem("NORMAL",true);
	// //角色权限
	// // this.setPopedem("SYSOPERATOR",true);
	// //最高权限
	// // this.setPopedem("SYSDBA",true);
	// // this.setParameter("H");
	// }
	//选中一条时条码号相同的会同时勾选
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
	 * 选择事件
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
							"选,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,50;条码号,100;报告类别,80,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");
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
							"选,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		if ("H".equals("" + obj)) {//健检
			
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
							"选,30,boolean;急,30,boolean;序号,50;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");//caowl 20130320 增加员工序号
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");//caowl 20130320 增加员工序号
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
	 * 初始化权限
	 */
	public void onInitPopeDem() {
		if (this.getPopedem("NORMAL")) {
			if ("O".equals(this.getAdmType())) {
				this.getTRadioButton("O").setSelected(true);
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
				// 其他设置灰色
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
	 * 初始化页面
	 */
	public void initPage() {
		//获取初始时间，与结束时间
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
							"选,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,70;条码号,100;报告类别,80,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");
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
							"选,30,boolean;印,30,boolean;急,30,boolean;医嘱名称,160;启用时间,140;160;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");
			this
					.getTTable(TABLE)
					.setParmMap(
							"FLG;PRINT_FLG;URGENT_FLG;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");
		}
		
		if ("H".equals(this.getAdmType())) {

//			/** Yuanxm add 开始时间 与结束时间初始化 begin */
//			Date d = new Date(sysDate.getTime());
//			String begin = (d.getYear() + 1900) + "/" + (d.getMonth()) + "/"
//					+ d.getDate() + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
////			String end = (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/"
////					+ d.getDate() + " 23:59:59";
//
//			// 初始化时间
//			this.setValue("START_DATE", getTimestamp(begin));
//			//this.setValue("END_DATE", getTimestamp(end));
//			/** Yuanxm add 开始时间 与结束时间初始化 end */

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
					.setHeader("选,30,boolean;印,30,boolean;急,30,boolean;序号,50;医嘱名称,160;启用时间,140;科室,100,DEPT_CODE;诊区,100,CLINICAREA_CODE;诊室,140,CLINICROOM_CODE;姓名,100;条码号,100;报告类别,120,RPTTYPE_CODE;检体部位,120,ITEM_CODE;仪器代码,100,DEV_CODE;病案号,100;采样人员,70,USER_CODE;采样时间,140");//caowl 20130320 增加序号一列
			this
					.getTTable(TABLE)
					.setParmMap("FLG;PRINT_FLG;URGENT_FLG;NO;ORDER_DESC;ORDER_DATE;DEPT_CODE;CLINICAREA_CODE;CLINICROOM_NO;PAT_NAME;APPLICATION_NO;RPTTYPE_CODE;OPTITEM_CODE;DEV_CODE;MR_NO;BLOOD_USER;BLOOD_DATE;CASE_NO;CAT1_TYPE;APPLICATION_NO;ORDER_NO;SEQ_NO");//caowl 20130320 增加序号一列
		}
		// 查询
		//this.onQuery();
	}

	public static Timestamp getTimestamp(String time) {
		Date date = new Date();
		// 注意format的格式要与日期String的格式相匹配
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
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 拿到TTextField
	 * 
	 * @return TTextFormat
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 返回TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}

	/**
	 * 拿到TTextFormat
	 * 
	 * @return TTextFormat
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	/**
	 * 保存
	 */
	public void onSave(){
		boolean have = false;
		boolean success = false;
		String user_id = Operator.getID();
		String now = SystemTool.getInstance().getDate().toString().substring(0, 19);
		String sql = "";
		int rowCount = this.getTTable(TABLE).getRowCount();
		if(rowCount < 0){
			this.messageBox("没有数据！");
			return;
		}
		// 密码判断
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
				//更新表med_apply
				sql = updateSql(mrNo, caseNo, cat1_type,application_no,order_no,seq_no, user_id, now);
				TParm resultParm = new TParm(TJDODBTool.getInstance().update(sql));
				have = true;
				success = true;
			}
		}
		if(!have){
			this.messageBox("没有选中数据！");
			return;
		}
		if(success){
			this.messageBox("保存成功！");
			//刷新页面
			onQuery();
			execBarCode = "";
			return;
		}else{
			this.messageBox("保存失败！");
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
	 * 查询
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
		//System.out.println("查询sql="+sql);
		TParm action = new TParm(this.getDBTool().select(sql));
//		if(action.getCount()<0){
//        	this.messageBox("查无数据！");
//        	return;
//        }
		this.getTTable(TABLE).setParmValue(action);
		// 每次查询清空全选
		this.setValue("ALLCHECK", "N");

		
	}

	/**
	 * 全选
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
	 * 得到TCheckBox
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}
	
	/**
	 * 返回查询病患结果
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
	 * 得到查询语句
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
        
        //执行状态-未执行或已执行
        if("N".equals(getConfirmRadioValue())){//未执行
        	sql += " AND BLOOD_USER IS NULL AND BLOOD_DATE IS NULL ";
        }else if("Y".equals(getConfirmRadioValue())){//已执行
        	sql += " AND BLOOD_USER IS NOT NULL AND BLOOD_DATE IS NOT NULL ";
        }
        //医嘱-条形码
        if(this.getValueString("BAR_CODE").length() != 0){
        	sql += " AND APPLICATION_NO IN ("+execBarCode+")  ";
        }
        
        if (count > 0) sql += " AND ";
        if ("H".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS' AND STATUS <> 9 ORDER BY CAT1_TYPE,CASE_NO";
        else if ("I".equals(getAdmTypeRadioValue())) sql +=
                " CAT1_TYPE='LIS'  ORDER BY CAT1_TYPE,CASE_NO,BED_NO";
        else sql += " CAT1_TYPE='LIS'  ORDER BY CAT1_TYPE,CASE_NO,BED_NO";
        
//        System.out.println("普通sql:"+sql);
        return sql;
    }
    /**
     * 激发补充计价窗口
     */
    public void onCharge(Object isDbClick) {
    	boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
    	TParm ibsParm = new TParm();
    	// 先选中再调用界面
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
     * 激发补充计价窗口
     */
    /**
    public void onCharge(Object isDbClick) {
        // this.messageBox("come in.");
        boolean dbClickFlg = TypeTool.getBoolean(isDbClick);
        // 得到选中的行数
        
         * int selRow = masterTbl.getSelectedRow(); if (selRow < 0) { //
         * messageBox("请选择病患医嘱"); // return; outsideParm.setData("INWEXE",
         * "CASE_NO", getCaseNo()); outsideParm.setData("INWEXE", "ORDER_NO",
         * ""); outsideParm.setData("INWEXE", "ORDER_SEQ", ""); } else { TParm
         * tableParm = masterTbl.getParmValue(); // 取得需要传给IBS的参数 String caseNo =
         * tableParm.getValue("CASE_NO", selRow); String orderNo =
         * tableParm.getValue("ORDER_NO", selRow); String orderSeq =
         * tableParm.getValue("ORDER_SEQ", selRow); // 传给IBS的数据
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
            // 取得需要传给SUM的参数
            caseNo_ = tableParm.getValue("CASE_NO", selRow);
            mrNo_ = tableParm.getValue("MR_NO", selRow);
            ipdNo_ = tableParm.getValue("IPD_NO", selRow);
            station_ = tableParm.getValue("STATION_CODE", selRow);
            bedNo_ = tableParm.getValue("BED_NO", selRow);
            execDeptCode_ = tableParm.getValue("ORDER_DEPT_CODE", selRow);//zhangp 成本中心修改为科室，补充计费不会再错
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

        // 先选中再调用界面
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
            
             * //激发MovePane的双击效果 mp1.onDoubleClicked(isCharge); if (!isCharge) {
             * 
             * getTPanel("ChargePanel").addItem("ChargePanel_",
             * "%ROOT%\\config\\ibs\\IBSOrderm.x", outsideParm, false); }
             * isCharge = isCharge ? false : true; //隐藏右边的按钮
             * mp2.onDoubleClicked(true); mp3.onDoubleClicked(true);
             
        } else { // 界面打开着，双击调用 outsideParm
            this.callFunction("UI|ChargePanel_|getINWData", ibsParm);
        }
    }
	
	**/
	
	/**
	 * 得到查询参数
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
	 * 得到健检的查询语句
	 * 
	 * @return String
	 */
	public String getHRMQuerySQL() {// add by wanglong 20121214
		String sql = "SELECT 'N' AS FLG, A.PRINT_FLG,  A.DEPT_CODE, A.STATION_CODE, A.CLINICAREA_CODE,"
				+ "          A.CLINICROOM_NO, A.PAT_NAME, A.APPLICATION_NO, A.RPTTYPE_CODE, A.OPTITEM_CODE, "
				+ "          A.DEV_CODE, A.MR_NO,A.IPD_NO, A.ORDER_DESC, A.CAT1_TYPE,A.OPTITEM_CHN_DESC, "
				+ "          TO_CHAR (A.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') AS ORDER_DATE,A.DR_NOTE,"
				+ "          A.EXEC_DEPT_CODE, A.URGENT_FLG,A.SEX_CODE, A.BIRTH_DATE,A.ORDER_NO, A.SEQ_NO, "
				+ "          A.TEL, A.ADDRESS,A.CASE_NO,A.ORDER_CODE, A.ADM_TYPE, A.PRINT_DATE,C.SEQ_NO AS NO "//caowl 20130320 增加员工序号
				+ "     FROM MED_APPLY A, HRM_ORDER B, HRM_CONTRACTD C"
				+ "    WHERE A.APPLICATION_NO = B.MED_APPLY_NO "
				+ "      AND B.CONTRACT_CODE = C.CONTRACT_CODE "
				+ "      AND A.ORDER_NO = B.CASE_NO "
				+ "       AND A.SEQ_NO = B.SEQ_NO  "
				+ "      AND B.MR_NO = C.MR_NO ";
        sql += " AND A.ADM_TYPE = 'H' AND B.SETMAIN_FLG='Y' ";// 门级别
        String srartDate =
                StringTool.getString((Timestamp) this.getValue("START_DATE"), "yyyyMMddHHmmss");// 开始时间
        String endDate =
                StringTool.getString((Timestamp) this.getValue("END_DATE"), "yyyyMMddHHmmss");// 结束时间
        sql +=
                " AND A.START_DTTM BETWEEN TO_DATE('" + srartDate + "','YYYYMMDDHH24MISS') "
                        + "                     AND TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
        if (this.getValueString("REGION_CODE").length() != 0) {// 区域
            sql += " AND A.REGION_CODE = '" + this.getValueString("REGION_CODE") + "'";
        }
        if (this.getValueString("DEPT_CODEMED").length() != 0) {// 科室
            sql += " AND A.DEPT_CODE = '" + this.getValueString("DEPT_CODEMED") + "'";
        }
//        if (getPrintStatus().length() != 0) {// 打印状态（未打印、已打印、全部）
//            sql += " AND A.PRINT_FLG = '" + getPrintStatus() + "'";
//        }
        if (this.getValueString("MR_NO").length() != 0) {// 病案号
            sql += " AND A.MR_NO = '" + this.getValueString("MR_NO") + "'";
        }
        if (this.getValueString("COMPANY_CODE").length() != 0) {// 团体号
            sql += " AND C.COMPANY_CODE = '" + this.getValueString("COMPANY_CODE") + "'";
        }
        if (this.getValueString("CONTRACT_CODE").length() != 0) {// 合同号
            sql += " AND C.CONTRACT_CODE = '" + this.getValueString("CONTRACT_CODE") + "'";
        }
        if (this.getValueString("START_SEQ_NO").length() != 0) {// 员工序号开始
            sql += " AND C.SEQ_NO >= '" + this.getValueString("START_SEQ_NO") + "'";
        }
        if (this.getValueString("END_SEQ_NO").length() != 0) {// 员工序号结束
            sql += " AND C.SEQ_NO <= '" + this.getValueString("END_SEQ_NO") + "'";
        }
        
        //执行状态-未执行或已执行
        if("N".equals(getConfirmRadioValue())){//未执行
        	sql += " AND A.BLOOD_USER IS NULL AND A.BLOOD_DATE IS NULL ";
        }else if("Y".equals(getConfirmRadioValue())){//已执行
        	sql += " A.AND BLOOD_USER IS NOT NULL AND A.BLOOD_DATE IS NOT NULL ";
        }
        
		sql += " AND A.CAT1_TYPE='LIS' AND A.STATUS <> 9 ORDER BY C.SEQ_NO ASC ,A.CAT1_TYPE ASC,A.START_DTTM DESC, A.CASE_NO ASC";// caowl
																																	// 20130305
																																	// 按日期升序排列
		// System.out.println("健康检查sql:"+sql);
		return sql;
	}
	
	/**
	 * 调用密码验证
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
	 * 得到门急住别值
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
	 * 得到执行状态
	 * 
	 * @return String
	 */
	public String getConfirmRadioValue() {
		if (this.getTRadioButton("CONFIRM_N").isSelected())
			return "N";//未执行
		if (this.getTRadioButton("CONFIRM_Y").isSelected())
			return "Y";//已执行
		return "N";
	}

	/**
	 * 得到打印状态
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
	 * 根据病患生日和传入的截至日期，计算病人年龄并根据是否为儿童以不同的形式显示年龄，如是儿童则显示X岁X月X日，如成人则显示x岁
	 * 
	 * @param odo
	 * @return String 界面显示的年龄
	 */
	public static String showAge(Timestamp birth, Timestamp sysdate) {
		String age = "";
		String[] res;
		res = StringTool.CountAgeByTimestamp(birth, sysdate);
		if (OPDSysParmTool.getInstance().isChild(birth)) {
			age = res[0] + "岁" + res[1] + "月";
		} else {
			age = (Integer.parseInt(res[0]) == 0 ? 1 : res[0]) + "岁";
		}
		// System.out.println("age" + age);
		return age;
	}

	

	/**
	 * 查询条码类别
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
	 * 得到检验条码急做标记
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
	 * 拿到仪器类别
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
	 * 拿到字典信息
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
	 * 拿到科室
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
	 * 拿到科室
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
	 * 清空
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
                this.setValue("START_DATE", getTimestamp(begin));
                this.setValue("END_DATE", getTimestamp(end));
                clearValue("IPD_NO;MR_NO;PAT_NAME;BED_NO;ALLCHECK;COMPANY_CODE;CONTRACT_CODE;START_SEQ_NO;END_SEQ_NO;BAR_CODE");// caowl
                                                                                                                       // 增加COMPANY_CODE;CONTRACT_CODE
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
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
                                + " 00:00:00";// caowl 20130305 健检的默认日期为前一个月
                String end =
                        (d.getYear() + 1900) + "/" + (d.getMonth() + 1) + "/" + d.getDate()
                                + " 23:59:59";
                // 初始化时间
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
     * 读卡
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

	// $$===================add by lx 2011/02/13 加入泰心叫号接口
	// start===========================$$//
	

	// $$===================add by lx 2011/02/13
	// 加入泰心叫号接口END===========================$$//

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
	 * 右键
	 */
	public void showPopMenu() {
		TTable table = (TTable) this.getComponent("TABLE");
		table
				.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu|TABLE");
	}

	/**
	 * 细项
	 */
	public void openRigthPopMenu(String tableName) {
		TTable table = (TTable) this.getComponent(tableName);
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		// System.out.println("选中行:"+parm);
		TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
		this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
	}

	/**
	 * 返回集合医嘱细相的TParm形式
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
			// 计算总价格
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
	 * 团体代码选择事件
	 */
	public void onCompanyChoose() {// add by wanglong 20121213
		companyCode = this.getValueString("COMPANY_CODE");
		if (StringUtil.isNullString(companyCode)) {
			return;
		}
		// 根据团体代码查得该团体的合同主项
		TParm contractParm = contractD.onQueryByCompany(companyCode);
		if (contractParm.getErrCode() != 0) {
			this.messageBox_("没有数据");
		}
		// 构造一个TTextFormat,将合同主项赋值给这个控件，取得最后一个合同代码赋值给这个控件初始值
		contract.setPopupMenuData(contractParm);
		contract.setComboSelectRow();
		contract.popupMenuShowData();
		contractCode = contractParm.getValue("ID", 0);
		if (StringUtil.isNullString(contractCode)) {
			this.messageBox_("查询失败");
			return;
		}
		contract.setValue(contractCode);

	}

	/**
	 * 合同代码选择事件
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				 //System.out.println("+i+"+i);
				 //System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				 //System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				//System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
}
