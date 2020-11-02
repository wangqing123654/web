package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatEMRTemplet;

public class OPDCommTempletCombineControl extends TControl{

	//调试开关
	private boolean isDebug = true;

	private String action = "save";
	// 主项表格
	private TTable table;

	private String type = "2";

	private String m_param;

	private String templatecat;

	private TextFormatEMRTemplet textFormatemr;
	
	private TextFormatEMRTemplet textFormatemr_q;
	
	
	public OPDCommTempletCombineControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		
		initPage();
		onClickCheckBoxEvent();
		
	}
	
	/**
	 * 初始画面数据
	 */
	private void initPage() {
		m_param = (String) this.getParameter();
		type = m_param.split(";")[0];
		templatecat = m_param.split(";")[1];
		if (isDebug) {
			System.out.println("---type--" + type);
			System.out.println("---m_templatecat--" + templatecat);
		}

		// 初始化Table
		table = getTable("TABLE");// tTable_0 TABLE
		textFormatemr = (TextFormatEMRTemplet) this
				.getComponent("SUBCLASS_CODE");
		textFormatemr_q = (TextFormatEMRTemplet) this
		.getComponent("SUBCLASS_CODE1");
		String subClassCode1 = "";
		subClassCode1 = TConfig.getSystemValue("ONWEmrMRCODE");
		textFormatemr_q.setSubClassCode(subClassCode1);
		//this.getCheckBox("MAIN_FLG").setSelected(true);
		//this.setValue("MAIN_FLG", "Y");
		// SUBCLASS_CODE||'_'||SEQ
		String sql = "SELECT SUBCLASS_CODE||'_'||SEQ SUBCLASS_CODE,"
				+ " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM,DEPT_OR_DR, DEPTORDR_CODE "
				+ " FROM OPD_COMTEMPLET " + " WHERE  ";
		// + " WHERE DEPT_OR_DR = '2' ";

		// SUBCLASS_CODE;SEQ;MAIN_FLG;OPT_USER;OPT_DATE;OPT_TERM;DEPT	_OR_DR;DEPTORDR_CODE
		String where = "";
		// 1：科室模板；2： 医生模板
		if ("1".equals(type)) {
			if(!this.getCheckBox("tCheckBox_1").isSelected()){
				type="'1','3'";
			}else{
				type="'5'";
			}
			where = " DEPT_OR_DR IN ("+type+") AND DEPTORDR_CODE = '" + Operator.getDept() + "'";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(true);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(false);
			this.setValue("DEPT_CODE", Operator.getDept());
			this.setValue("LAB_TEXT", "科室：");
			type="1";//还原type
		} else if ("2".equals(type)) {
			this.getLabel("tLabel_0").setVisible(false);
			this.getTextFormat("SUBCLASS_CODE1").setVisible(false);
			//this.getCheckBox("tCheckBox_1").setVisible(false);
			//this.getCheckBox("tCheckBox_1").setSelected(true);
			if(this.getCheckBox("tCheckBox_1").isSelected()){
				type="6";
			}
			where = " DEPT_OR_DR = '" + type+ "' AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
			((TTextFormat) this.getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) this.getComponent("DR_CODE")).setVisible(true);
			this.setValue("DR_CODE", Operator.getID());
			this.setValue("LAB_TEXT", "医生：");
			type="2";
		} 

		//
//		String code = getValueString("SUBCLASS_CODE");
//		if (code != null && code.length() > 0) {
//
//			where += " AND SUBCLASS_CODE='" + code.split("_")[0] + "'";
//			where += " AND SEQ='" + code.split("_")[1] + "'";
//		}

		String order_by = " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";

		String strSQL = sql + where + order_by;
		/*
		 * TDataStore dataStore = new TDataStore();
		 * 
		 * System.out.println("------sql111------"+sql + where + order_by);
		 * dataStore.setSQL(sql + where + order_by); dataStore.retrieve();
		 * table.setDataStore(dataStore); table.setDSValue();
		 */
		if (isDebug) {
			System.out.println("------SQL------" + strSQL);
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(strSQL));
		TParm data=new TParm();
		TParm data1=new TParm();
		TParm data2=new TParm();
		if(!this.getCheckBox("tCheckBox_1").isSelected() && type.equals("1")){
			for(int i=0;i<result.getCount();i++){
					if(result.getValue("DEPT_OR_DR",i).equals("1")){
						data1=result.getRow(i);
					}
					if(result.getValue("DEPT_OR_DR",i).equals("3")){
						data2=result.getRow(i);
					}
			}
			data.setData("SUBCLASS_CODE",0,data1.getValue("SUBCLASS_CODE"));
			data.setData("SEQ",0,data1.getValue("SEQ"));
			data.setData("MAIN_FLG",0,data1.getValue("MAIN_FLG"));
			data.setData("DEPT_OR_DR",0,data1.getValue("DEPT_OR_DR"));
			data.setData("DEPTORDR_CODE",0,data1.getValue("DEPTORDR_CODE"));
			data.setData("SUBCLASS_CODE1",0,data2.getValue("SUBCLASS_CODE"));
			data.setData("SEQ1",0,data2.getValue("SEQ"));
			data.setData("OPT_USER",0,data1.getValue("OPT_USER"));
			data.setData("OPT_DATE",0,data1.getValue("OPT_DATE"));
			data.setData("OPT_TERM",0,data1.getValue("OPT_TERM"));
			data.setData("DEPT_OR_DR1",0,data2.getValue("DEPT_OR_DR"));
			data.setData("DEPTORDR_CODE1",0,data1.getValue("DEPTORDR_CODE"));
			if(!"".equals(data.getValue("DEPTORDR_CODE",0)) && data.getValue("DEPTORDR_CODE",0) !=null){
				table.setParmValue(data);
			}else{
				table.removeRowAll();
				table.setParmValue(new TParm());
			}
		}else{
			table.setParmValue(result);
		}
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}
	/**
	 * 根据复诊病历是否挑勾，来显示SUBCLASSCODE控件
	 */
	public void viewSubclasscodeContent(){
		String subClassCode = "";
		if (templatecat.equals("SUB")) {
			subClassCode = TConfig
					.getSystemValue("ODOEmrTempletZSSUBCLASSCODE");
			
		} else if (templatecat.equals("PHY")) {
			subClassCode = TConfig.getSystemValue("ONWEmrMRCODE");
		}
		if(this.getCheckBox("tCheckBox_1").isSelected()){
			subClassCode = TConfig.getSystemValue("OEEmrMRCODE");
		}
		if (isDebug) {
			System.out.println("---过滤模版为---" + subClassCode);
		}
		textFormatemr.setSubClassCode(subClassCode);
		
	}
	/**
	 * 复诊病历挑勾事件
	 */
	public void onClickCheckBoxEvent(){
		if(this.getCheckBox("tCheckBox_1").isSelected()){
			String subClassCode = "";
			subClassCode=TConfig.getSystemValue("OEEmrMRCODE");
			viewSubclasscodeContent();
			getLabel("tLabel_1").setText("复诊病历:");
			table.removeRowAll();
			if(type.equals("1"))
				header1();
		}else{
			viewSubclasscodeContent();
			getLabel("tLabel_1").setText("主诉模板:");
			table.removeRowAll();
			if(type.equals("1"))
				header2();
		}
		initPage();
		onClear1();
	}
	/**
	 * 表头1 add by huangjw 20150114
	 */
	public void header1(){
		table.setHeader("模板,200,SUBCLASS_CODE;顺序号,80;主,30,boolean;操作人员,100,DR_CODE;操作时间,150;操作IP,120");
		table.setParmMap("SUBCLASS_CODE;SEQ;MAIN_FLG;OPT_USER;OPT_DATE;OPT_TERM;DEPT_OR_DR;DEPTORDR_CODE");
		this.getTextFormat("SUBCLASS_CODE1").setVisible(false);
		this.getLabel("tLabel_0").setVisible(false);
	}
	/**
	 * 表头2 add by huangjw 20150114
	 */
	public void header2(){
		table.setHeader("模板,200,SUBCLASS_CODE;顺序号,80;主,30,boolean;模板,200,SUBCLASS_CODE;顺序号,80;操作人员,100,DR_CODE;操作时间,150;操作IP,120");
		table.setParmMap("SUBCLASS_CODE;SEQ;MAIN_FLG;SUBCLASS_CODE1;SEQ1;OPT_USER;OPT_DATE;OPT_TERM;DEPT_OR_DR1;DEPTORDR_CODE1;SEQ;DEPT_OR_DR;DEPTORDR_CODE");
		this.getTextFormat("SUBCLASS_CODE1").setVisible(true);
		this.getLabel("tLabel_0").setVisible(true);
	}
	
	/**
	 * 保存方法
	 */
	public void onSave() {
		int row = 0;
		TTextFormat combo = getTextFormat("SUBCLASS_CODE");
		boolean flg = combo.isEnabled();
		if (flg) {
			if (!CheckData())
				return;
			// row = table.addRow();
		} else {
			row = table.getSelectedRow();
			return;
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		String main_sql = "";
		String main_sql1="";//体征sql add by huangjw 20150116
		
		// 科室 主诉
		if ("1".equals(type)) {
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '1' AND DEPTORDR_CODE = '"
					+ getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";
			main_sql1 = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
				+ "WHERE DEPT_OR_DR = '3' AND DEPTORDR_CODE = '"
				+ getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";
			// 医生 主诉
		} else if ("2".equals(type)) {
			if(this.getCheckBox("tCheckBox_1").isSelected()){
				type="6";
			}
			main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET "
					+ "WHERE DEPT_OR_DR = '"+type+"' AND DEPTORDR_CODE = '"
					+ getValueString("DR_CODE") + "' AND MAIN_FLG = 'Y' ";
			type="2";
		}

		if ("save".equals(action)) {
			TParm result = new TParm(TJDODBTool.getInstance().select(main_sql));
			if (result != null && result.getCount() > 0) {
				this.messageBox("已存在主模板，请重新选择！");
				return;
			}

			String deptOrDR = "";
			String deptOrDR1 = "";//add by huangjw 20150116
			String deptOrDRCode = "";
			String strSubClassCode1 = "";
			String strSeq1 = "";
			if ("1".equals(type)) {
				if(!this.getCheckBox("tCheckBox_1").isSelected()){
					deptOrDR = "1";
					deptOrDR1 = "3";//add by huangjw 20150116
					strSubClassCode1 = getValueString("SUBCLASS_CODE1").split("_")[0];//add by huangjw 20150116
					strSeq1 = getValueString("SUBCLASS_CODE1").split("_")[1];//add by huangjw 20150116
				}else{
					deptOrDR = "5";
				}
				
				deptOrDRCode = getValueString("DEPT_CODE");
			} else if ("2".equals(type)) {
				if(!this.getCheckBox("tCheckBox_1").isSelected()){
					deptOrDR = "2";
				}else{
					deptOrDR = "6";
				}
				deptOrDRCode = getValueString("DR_CODE");
			} 
			if (isDebug) {
				System.out.println("++SUBCLASS_CODE++----"
						+ getValueString("SUBCLASS_CODE"));
			}
			String strSubClassCode = getValueString("SUBCLASS_CODE").split("_")[0];
			
			String strSeq = getValueString("SUBCLASS_CODE").split("_")[1];
			
			String strMainFlg = getValueString("MAIN_FLG");
			

			String insSQL = "INSERT INTO OPD_COMTEMPLET VALUES(";
			insSQL += "'" + deptOrDR + "',";
			insSQL += "'" + deptOrDRCode + "',";
			insSQL += "'" + strSubClassCode + "',";
			insSQL += "'" + strSeq + "',";
			insSQL += "'" + Operator.getID() + "',";
			insSQL += "SYSDATE,";
			insSQL += "'" + Operator.getIP() + "',";
			insSQL += "'" + strMainFlg + "'";
			insSQL += ")";
			
			if(!this.getCheckBox("tCheckBox_1").isSelected()&& type.equals("1")){
				String insSQL1="INSERT INTO OPD_COMTEMPLET VALUES(";
				insSQL1 += "'" + deptOrDR1 + "',";
				insSQL1 += "'" + deptOrDRCode + "',";
				insSQL1 += "'" + strSubClassCode1 + "',";
				insSQL1 += "'" + strSeq1 + "',";
				insSQL1 += "'" + Operator.getID() + "',";
				insSQL1 += "SYSDATE,";
				insSQL1 += "'" + Operator.getIP() + "',";
				insSQL1 += "'" + strMainFlg + "'";
				insSQL1 += ")";
				System.out.println("-----insSQL1-----" + insSQL1);
				TParm parm_1=new TParm(TJDODBTool.getInstance().update(insSQL1));
				if (parm_1.getErrCode() != 0) {
					messageBox("E0001");
					return;
				}
			}
			if (isDebug) {
				System.out.println("-----insSQL-----" + insSQL);
				//System.out.println("-----insSQL1-----" + insSQL1);
			}
			TParm parm = new TParm(TJDODBTool.getInstance().update(insSQL));
			
			// 更新模版对应的数据
			if (!this.getCheckBox("tCheckBox_1").isSelected() && "2".equals(type)) {
				String updateSQL = "UPDATE EMR_TEMPLET SET USER_ID='"
						+ deptOrDRCode + "'";
				updateSQL += " WHERE SUBCLASS_CODE='" + strSubClassCode + "'";
				updateSQL += " AND SEQ='" + strSeq + "'";
				if (isDebug) {
					System.out
							.println("+++++++deptUpdateSQL++++++" + updateSQL);
				}
				//
				TParm parm1 = new TParm(TJDODBTool.getInstance().update(
						updateSQL));
				if (parm1.getErrCode() != 0) {
					messageBox("E0001");
					return;
				}
			}
			if (parm.getErrCode() != 0 ) {
				messageBox("E0001");
				return;
			}
			
			messageBox("P0001");
			this.onQuery();
			/*
			 * table.setItem(row, "SUBCLASS_CODE",
			 * getValueString("SUBCLASS_CODE").split("_")[0]); String sql =
			 * "SELECT SEQ FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '" +
			 * getValueString("SUBCLASS_CODE") + "' AND OPD_FLG = 'Y' "; TParm
			 * parm = new TParm(TJDODBTool.getInstance().select(sql));
			 * 
			 * table.setItem(row, "SEQ",
			 * getValueString("SUBCLASS_CODE").split("_"
			 * )[1]);//parm.getInt("SEQ",0) table.setItem(row, "MAIN_FLG",
			 * getValueString("MAIN_FLG")); table.setItem(row, "OPT_USER",
			 * Operator.getID()); table.setItem(row, "OPT_DATE", date);
			 * table.setItem(row, "OPT_TERM", Operator.getIP());
			 */
		}

		/*
		 * TDataStore dataStore = table.getDataStore(); if
		 * (dataStore.isModified()) { table.acceptText(); if (!table.update()) {
		 * messageBox("E0001"); table.removeRow(row); table.setDSValue();
		 * onClear(); return; } table.setDSValue(); } messageBox("P0001");
		 * table.setDSValue();
		 */
	}
	

	/**
	 * TABLE单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String likeNames = "SUBCLASS_CODE;MAIN_FLG;SUBCLASS_CODE1";
			this.setValueForParm(likeNames, parm);
			getTextFormat("SUBCLASS_CODE").setEnabled(false);
			getTextFormat("SUBCLASS_CODE1").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		initPage();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		this.setValue("SUBCLASS_CODE", "");
		this.setValue("SUBCLASS_CODE1", "");
		this.setValue("MAIN_FLG", "N");
		this.setValue("tCheckBox_1", "N");
		table.setSelectionMode(0);
		((TTextFormat) this.getComponent("SUBCLASS_CODE")).setEnabled(true);
		((TTextFormat) this.getComponent("SUBCLASS_CODE1")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
		onInit();
		//onQuery();
	}
	/**
	 * 清空方法1
	 */
	public void onClear1() {
		this.setValue("SUBCLASS_CODE", "");
		this.setValue("SUBCLASS_CODE1", "");
		this.setValue("MAIN_FLG", "N");
		//this.setValue("tCheckBox_1", "N");
		table.setSelectionMode(0);
		((TTextFormat) this.getComponent("SUBCLASS_CODE")).setEnabled(true);
		((TTextFormat) this.getComponent("SUBCLASS_CODE1")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;

		TParm parm = table.getParmValue().getRow(row);
		String delSQL = "DELETE FROM OPD_COMTEMPLET";
		String delSQL1 = "DELETE FROM OPD_COMTEMPLET";
		delSQL += " where DEPT_OR_DR = '" + parm.getValue("DEPT_OR_DR") + "'";
		delSQL += " AND DEPTORDR_CODE='" + parm.getValue("DEPTORDR_CODE") + "'";
		delSQL += " AND SUBCLASS_CODE='"
				+ parm.getValue("SUBCLASS_CODE").split("_")[0] + "'";
		delSQL += " AND SEQ='" + parm.getValue("SEQ") + "'";
		if(!this.getCheckBox("tCheckBox_1").isSelected() && type.equals("1")){
			delSQL1 += " where DEPT_OR_DR = '" + parm.getValue("DEPT_OR_DR1") + "'";
			delSQL1 += " AND DEPTORDR_CODE='" + parm.getValue("DEPTORDR_CODE") + "'";
			delSQL1 += " AND SUBCLASS_CODE='"
					+ parm.getValue("SUBCLASS_CODE1").split("_")[0] + "'";
			delSQL1 += " AND SEQ='" + parm.getValue("SEQ1") + "'";
			TParm parm2 = new TParm(TJDODBTool.getInstance().update(delSQL1));
			if (parm2.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}
		}
		
		if (isDebug) {
			System.out.println("-----delSQL-----" + delSQL);
			System.out.println("-----delSQL-----" + delSQL);
		}
		TParm parm1 = new TParm(TJDODBTool.getInstance().update(delSQL));
		if (parm1.getErrCode() != 0) {
			messageBox("E0001");
			return;
		}
		messageBox("P0001");
		this.onQuery();

		//
		// table.removeRow(row);
		// table.setSelectionMode(0);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "delete";
		onClear();
	}

	/**
	 * 检查数据
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("SUBCLASS_CODE"))) {
			this.messageBox("主诉模板编号不能为空");
			return false;
		}
		if(!this.getCheckBox("tCheckBox_1").isSelected() && type.equals("1")){
			if ("".equals(getValueString("SUBCLASS_CODE1"))) {
				this.messageBox("体征模板编号不能为空");
				return false;
			}
		}
		if(!this.getCheckBox("MAIN_FLG").isSelected()){
			this.messageBox("请勾选主模板");
			return false;
		}
		return true;
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}
	
	/**
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}
	
	/**
	 * 得到TCheckBox对象
	 * @param tagName
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName){
		return (TCheckBox)getComponent(tagName);
	}
	/**
	 * 得到TLabel对象
	 * @param tagName
	 * @return
	 */
	public TLabel getLabel(String tagName){
		return (TLabel)getComponent(tagName);
	}

}
