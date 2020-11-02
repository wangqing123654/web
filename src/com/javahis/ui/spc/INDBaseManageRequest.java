package com.javahis.ui.spc;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;  
import jdo.spc.IndStockMTool; 
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;      
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;  
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndRequestObserver;
import com.javahis.util.StringUtil;

/**   
 * <p>
 * Title: ����ҩ����Control
 * </p>
 *   
 * <p>
 * Description: ����ҩ����Control  
 * </p>
 *     
 * <p>
 * Copyright: Copyright (c) 2008   
 * </p>
 * 
 * <p>                   
 * Company: bluecore  
 * </p>
 * 
 * @author fux 20124.11.25 
 * @version 1.0
 */  
//
public class INDBaseManageRequest extends TControl {

	// ������� 
	private String action;
	// ������
	private TTable table_m;

	// ϸ����
	private TTable table_d;

	// ������ѡ����
	private int row_m;    
  
	// ϸ����ѡ����   
	private int row_d;
  
	// ҳ���Ϸ������б�
	private String[] pageItem = { "REQUEST_DATE", "BASEMANAGE_NO", "APP_ORG_CODE", "REQTYPE_CODE", "TO_ORG_CODE", 
			"REASON_CHN_DESC", "DESCRIPTION", "URGENT_FLG",
			"UNIT_TYPE" };                                 

	// ϸ�����                    
	private int seq;

	// ���뵥��  
	private String BASEMANAGE_NO;     
  
	// ��������
	private String request_type;

	// ȫ������Ȩ��
	private boolean dept_flg = true;

	public INDBaseManageRequest() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ע�ἤ��SYS_FEE�������¼�
		getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponentUD");
		// ��������¼�
		addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, "onTableDChangeValue");
		// ��ʼ����������  
		initPage();                        
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ҩƷ����  
//		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
//		String is_separate_req = result.getValue("IS_SEPARATE_REQ",0);   
//		if("Y".equals(is_separate_req)){
//			((TLabel)getComponent("tLabel_6")).setVisible(false);
//			((TRadioButton)getComponent("GEN_DRUG")).setVisible(false);  
//			((TRadioButton)getComponent("TOXIC_DRUG")).setVisible(false);
//			((TRadioButton)getComponent("NOT_DRUG")).setVisible(false);
//			
//			((TRadioButton)getComponent("NOT_DRUG")).setSelected(true);  
//		}else if ("N".equals(is_separate_req)) {
//			((TRadioButton)getComponent("NOT_DRUG")).setVisible(false);
//		}
		getRadioButton("UPDATE_FLG_A").setSelected(true);
		String clearString = "START_DATE;END_DATE;REQUEST_DATE;BASEMANAGE_NO;REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
				+ "URGENT_FLG;SUM_RETAIL_PRICE";  
		this.clearValue(clearString);
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("REQUEST_DATE", date);
		// getComboBox("REQTYPE_CODE").setSelectedIndex(0);
//		onChangeRequestType();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ����״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
		((TMenuItem) getComponent("save")).setEnabled(true);
		getComboBox("REQTYPE_CODE").setEnabled(true);
		getTextField("BASEMANAGE_NO").setEnabled(true);  
		getComboBox("APP_ORG_CODE").setEnabled(true);    
		getComboBox("TO_ORG_CODE").setEnabled(true);                
		table_m.setSelectionMode(0);       
//		table_m.setParmValue(new TParm());
		table_m.setParmValue(new TParm());        
		table_d.setSelectionMode(0);                             
//		table_d.removeRowAll();              
		table_d.setParmValue(new TParm());                  
		   
		action = "insertM";
		getRadioButton("STATIC_NO_A").setSelected(true);
		getRadioButton("GEN_DRUG").setSelected(true);  
		table_d.setLockCellColumn(0	, false);
	}

	/**    
	 * ��ѯ����
	 */
	public void onQuery() {           
		table_m.setSelectionMode(0);         
		//table_m.setParmValue(new TParm());
		table_m.setParmValue(new TParm());    
		table_d.setSelectionMode(0);         
		//table_d.setParmValue(new TParm());
		table_d.setParmValue(new TParm()); 
		if ("".equals(getValueString("APP_ORG_CODE"))) {
			this.messageBox("��ҩ���Ų���Ϊ��");
			return;  
		}   
		// ������ѯ
		TParm result = TIOM_AppServer.executeAction("action.spc.INDBaseManageAction", "onQueryM", onQueryParm());
		// ����״̬��������  
		result = onFilterQueryByStatus(result);
		if (result == null || result.getCount() <= 0) {
			this.messageBox("�޲�ѯ���");
		} else {
			table_m.setParmValue(result);
		}
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ���������
		TParm parm = new TParm();
		// ���ؽ����        
		TParm result = new TParm();  
		if (!"updateD".equals(action)) {
			if (!CheckDataM()) {
				return; 
			}            
			// ���������Ϣ
			parm = getRequestMParm(parm);
			if ("insertM".equals(action)) {
				//fux  1 
				// ��ҩ����    
				BASEMANAGE_NO = SystemTool.getInstance().getNo("ALL", "IND", "BASEMANAGE_NO", "No");
				parm.setData("BASEMANAGE_NO", BASEMANAGE_NO);                                        
				// ִ����������                                                                                 
				result = TIOM_AppServer.executeAction("action.spc.INDBaseManageAction", "onInsertM", parm);
			} else {                                         
				parm.setData("BASEMANAGE_NO", BASEMANAGE_NO); 
				// ִ�����ݸ���  
				result = TIOM_AppServer.executeAction("action.spc.INDBaseManageAction", "onUpdateM", parm);
			}
			// ������ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			setValue("BASEMANAGE_NO", BASEMANAGE_NO);    
			this.messageBox("P0001");   
			onQuery();                         
		} else {
			if (!CheckDataD()) {
				return;
			}
			// ��Ӧ���ſ�漰ҩƷ�������ж�
			if (!getOrgStockCheck()) {
				return;
			}

			Timestamp date = SystemTool.getInstance().getDate();
			table_d.acceptText();
			TDataStore dataStore = table_d.getDataStore();
			// ���ȫ���Ķ����к�
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			// ���ȫ����������
			int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
			// ���̶�����������
			Vector vct = new Vector();
			for (int i = 0; i < newrows.length; i++) {
				dataStore.setItem(newrows[i], "BASEMANAGE_NO", getValueString("BASEMANAGE_NO"));
				dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
				//fux modify 20141126Ĭ��Ϊ3 �����״̬
				dataStore.setItem(newrows[i], "UPDATE_FLG", "3");  
				vct.add(dataStore.getItemString(newrows[i], "ORDER_CODE"));  
			}
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}

			TParm inParm = new TParm();
			inParm.setData("ORG_CODE", this.getValueString("APP_ORG_CODE"));
			inParm.setData("ORDER_CODE_LIST", vct);
			inParm.setData("OPT_USER", Operator.getID());
			inParm.setData("OPT_DATE", date);
			inParm.setData("OPT_TERM", Operator.getIP());
			inParm.setData("REGION_CODE", Operator.getRegion());
			inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
			inParm.setData("REGION_CODE", Operator.getRegion());// ========pangben
																// modify
																// 20110621
			String[] sqlStrings = dataStore.getUpdateSQL();
			// ִ����������
			result = TIOM_AppServer.executeAction("action.spc.INDBaseManageAction", "onUpdateD", inParm);
			// ������ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			messageBox("P0001");
			table_d.setDSValue();
			// ���ô�ӡ����
			if (getRadioButton("UPDATE_FLG_A").isSelected()) {
				this.onPrint();
			}
			return;
		}
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		if (getRadioButton("UPDATE_FLG_B").isSelected()) {
			this.messageBox("��������ɲ���ɾ��");
		} else {
			if (row_m > -1) {
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥", 2) == 0) {
					TParm parm = new TParm();
					//��Ҫ����Ƿ��г����ϸ��û�У��в���ȫ��ɾ��
					if(checkIsDispense(BASEMANAGE_NO)){
						this.messageBox("���쵥����ҩƷ���⣬����ɾ��!\n�����Ҫɾ��������ȡ������");
						return ;
					}
					
					// ϸ����Ϣ
					if (table_d.getRowCount() > 0) {
						table_d.setParmValue(new TParm());
						String deleteSql[] = table_d.getDataStore().getUpdateSQL();
						parm.setData("DELETE_SQL", deleteSql);
					}
					// ������Ϣ
					parm.setData("BASEMANAGE_NO", BASEMANAGE_NO);
					TParm result = new TParm();
					result = TIOM_AppServer.executeAction("action.spc.INDBaseManageAction", "onDeleteM", parm);
					// ɾ���ж�
					if (result.getErrCode() < 0) {
						this.messageBox("ɾ��ʧ��");
						return;
					}
					table_m.removeRow(row_m);
					this.messageBox("ɾ���ɹ�");
					onClear();
				}
			} else {
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����뵥ϸ��", 2) == 0) {
					// luhai modify 2012-05-11 begin
					// �Ѿ������ϸ�����ɾ��
					String requestNo = table_d.getDataStore().getItemString(row_d, "BASEMANAGE_NO");
					String seqNo = table_d.getDataStore().getItemString(row_d, "SEQ_NO");
					if (!"".equals(requestNo)) {
						if (checkIsDispense(requestNo, seqNo)) {
							this.messageBox("������ϸ���Ѿ����⣬�޷�ɾ����");
							return;
						}
					}
					// luhai modify 2012-05-11 end

					if ("".equals(table_d.getDataStore().getItemString(row_d, "BASEMANAGE_NO"))) {
						table_d.removeRow(row_d);
						return;
					}
					table_d.removeRow(row_d);
					// ϸ����ж�
					if (!table_d.update()) {
						messageBox("E0001");
						return;
					}
					messageBox("P0001");
					table_d.setDSValue();
					// onQuery();
				}
			}
		}
	}

	/**
	 * �ж�ϸ���Ƿ����
	 * 
	 * @return
	 */
	private boolean checkIsDispense(String requestNo, String seqNo) {
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append(" SELECT  * ");
		sqlbf.append(" FROM IND_DISPENSEM M,IND_DISPENSED D  ");
		sqlbf.append(" WHERE M.DISPENSE_NO=D.DISPENSE_NO  ");
		sqlbf.append(" AND M.BASEMANAGE_NO='" + requestNo + "' ");
		sqlbf.append(" AND D.REQUEST_SEQ=" + seqNo);
		Map select = TJDODBTool.getInstance().select(sqlbf.toString());
		TParm resultParm = new TParm(select);
		if (resultParm.getCount() <= 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * �ж�ϸ���Ƿ����
	 * 
	 * @return
	 */
	private boolean checkIsDispense(String requestNo) {
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append(" SELECT DISTINCT M.DISPENSE_NO,M.UPDATE_FLG ");  
		sqlbf.append(" FROM IND_DISPENSEM M,IND_DISPENSED D  ");
		sqlbf.append(" WHERE M.DISPENSE_NO=D.DISPENSE_NO  ");
		sqlbf.append(" AND M.BASEMANAGE_NO='" + requestNo + "' ");
		Map select = TJDODBTool.getInstance().select(sqlbf.toString());
		TParm resultParm = new TParm(select);  
		if (resultParm.getCount() <= 0) {
			return false;
		} else {
			String updateFlg = "";
			if(resultParm.getCount() > 1 ){
				for(int i = 0 ; i < resultParm.getCount("UPDATE_FLG");i++){
					updateFlg = resultParm.getValue("UPDATE_FLG",i);
					if(!updateFlg.equals("2")){
						return true;
					}
				}
			}else{
				updateFlg = resultParm.getValue("UPDATE_FLG",0);
				if(updateFlg != null && updateFlg.equals("2")){
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		Timestamp datetime = SystemTool.getInstance().getDate();
		TTable table_d = getTable("TABLE_D");
		if ("".equals(this.getValueString("BASEMANAGE_NO"))) {
			this.messageBox("���������뵥");
			return;
		}          
		if (table_d.getRowCount() > 0) {  
			// ��ӡ����  
			TParm date = new TParm();
			// ��ͷ����                                       
			date.setData("TITLE", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion())
					+"����ҩ���쵥");  
			//fux    
			date.setData("ORG_CODE_OUT", "TEXT", "��������:" + this.getComboBox("TO_ORG_CODE").getSelectedName()); 
			date.setData("ORG_CODE_IN", "TEXT", "���벿��:" + this.getComboBox("APP_ORG_CODE").getSelectedName());  
			date.setData("REQ_NO", "TEXT", "���뵥��: " + this.getValueString("BASEMANAGE_NO"));      
			date.setData("REQ_TYPE", "TEXT", "�������:" + "��������");      
			date.setData("DATE", "TEXT", "�Ʊ�����: " + datetime.toString().substring(0, 10).replace('-', '/'));
			// ����ע��, �����뵥Ϊ����ʱ,�����쵥����ʾ������ע��(URGENT) modify ZhenQin 2011-06-01
			TCheckBox URGENT_FLG = (TCheckBox) this.getComponent("URGENT_FLG");
			date.setData("URGENT", "TEXT", URGENT_FLG.isSelected() ? "��" : "");  

			// �������
			TParm parm = new TParm();  
			String order_code = "";
			String unit_type = "0";  
			String order_desc = "";
			if ("TEC".equals(getValueString("REQTYPE_CODE")))
				unit_type = IndSysParmTool.getInstance().onQuery().getValue("UNIT_TYPE", 0);
			else
				unit_type = "1";

			for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
				if (!table_d.getDataStore().isActive(i)) {
					continue;
				}
				order_code = table_d.getDataStore().getItemString(i, "ORDER_CODE");
				TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderInfoByCode(order_code, unit_type)));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("ҩƷ��Ϣ����");
					return;
				}
				if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
					order_desc = inparm.getValue("ORDER_DESC", 0);
				} else {
					order_desc = inparm.getValue("ORDER_DESC", 0) + "(" + inparm.getValue("GOODS_DESC", 0) + ")";
				}
				parm.addData("ORDER_DESC", order_desc);
				parm.addData("SPECIFICATION", inparm.getValue("SPECIFICATION", 0));
				parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
				parm.addData("UNIT_PRICE", table_d.getItemDouble(i, "RETAIL_PRICE"));
				parm.addData("QTY", table_d.getItemDouble(i, "QTY"));
				parm.addData("AMT", StringTool.round(table_d.getItemDouble(i, "RETAIL_PRICE") * table_d.getItemDouble(i, "QTY"), 2));
			}
			if (parm.getCount("ORDER_DESC") == 0) {
				this.messageBox("û�д�ӡ����");
				return;
			}

			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT");
			date.setData("TABLE", parm.getData());

			// ��β����
			date.setData("TOT", "TEXT", "�ϼ�: " + StringTool.round(Double.parseDouble(this.getValueString("SUM_RETAIL_PRICE")), 2));

			// luhai 2012-2-11 add �ϼƴ�д begin
			date.setData("TOT_DESC", "TEXT", ""
					+ StringUtil.getInstance().numberToWord(StringTool.round(Double.parseDouble(this.getValueString("SUM_RETAIL_PRICE")), 2)));
			// luhai 2012-2-11 add �ϼƴ�д end
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\RequestBaseTec.jhw", date);  
		} else {
			this.messageBox("û�д�ӡ����");
			return;
		}
	}

	/**
	 * ����������¼�
	 */
	public void onChangeRequestType() {/*
		TTextFormat app_org_code = (TTextFormat) getComponent("APP_ORG_CODE");
		TTextFormat to_org_code = (TTextFormat) this.getComponent("TO_ORG_CODE");
		app_org_code.setValue("");
		to_org_code.setValue("");
		request_type = this.getValueString("REQTYPE_CODE");
		// ȫ������Ȩ��
		if (dept_flg) {
			// ������ѡ��������趨���벿�źͽ��ܲ���
			if ("DEP".equals(request_type)) {
				getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.initTextFormatIndOrg("B", Operator.getRegion()));
				to_org_code.setOrgType("A");
			} else if ("TEC".equals(request_type)) {
				getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.initTextFormatIndOrg("C", Operator.getRegion()));
				to_org_code.setOrgType("B");
			} else if ("EXM".equals(request_type)) {
				getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.initTextFormatIndOrg("C", "Y"));
				to_org_code.setOrgType("B");
			}
			to_org_code.setOrgFlg("Y");
			app_org_code.onQuery();
			to_org_code.onQuery();  
		} else {  
			if ("DEP".equals(request_type)) {
				app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' "));
				to_org_code.setOrgType("A");
			} else if ("TEC".equals(request_type)) {
				app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'C' "));
				to_org_code.setOrgType("B");
			} else if ("EXM".equals(request_type)) {
				app_org_code.setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' "));
				to_org_code.setOrgType("B");
			}
			app_org_code.onQuery();
			to_org_code.setOrgFlg("Y");
			to_org_code.onQuery();
		}*/
	}

	/**
	 * ���뵥״̬����¼�
	 */
	public void onChangeRequestStatus() {
		String clearString = "BASEMANAGE_NO;REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;" + "DESCRIPTION;URGENT_FLG;SUM_RETAIL_PRICE";
		this.clearValue(clearString);
		table_m.setSelectionMode(0);
		table_m.setParmValue(new TParm());   
		table_d.setSelectionMode(0);  
		table_d.setParmValue(new TParm());   
    
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			action = "insertM";
		} else {  
			((TMenuItem) getComponent("save")).setEnabled(false);
		}                                                        
		
	}     

	/**   
	 * ������(TABLE_M)�����¼�  
	 */
	public void onTableMClicked() {
		row_m = table_m.getSelectedRow();                      
		if (row_m != -1) {    
			// ������ѡ���������Ϸ�   
			getTableSelectValue(table_m, row_m, pageItem);
			// �趨ҳ��״̬  
			getTextField("BASEMANAGE_NO").setEnabled(false);      
			//getTextFormat("APP_ORG_CODE").setEnabled(false);
			//getComboBox("REQTYPE_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true); 
			action = "updateM";                   
			// �������  
			request_type = getValueString("REQTYPE_CODE");
			// ���뵥��
			BASEMANAGE_NO = getValueString("BASEMANAGE_NO");
			// ��ϸ��Ϣ
			getTableDInfo(BASEMANAGE_NO);
			// ���һ��ϸ��
			onAddRow();  
			table_d.setSelectionMode(0);
			table_d.setLockCellColumn(0	, false);
		}
	}

	/**
	 * ��ϸ���(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {
		row_d = table_d.getSelectedRow();
		if (row_d != -1) {
			action = "updateD";
			table_m.setSelectionMode(0);
			row_m = -1;
			// ȡ��SYS_FEE��Ϣ��������״̬����
			/*
			 * String order_code = table_d.getDataStore().getItemString(table_d.
			 * getSelectedRow(), "ORDER_CODE"); if (!"".equals(order_code)) {
			 * this.setSysStatus(order_code); } else {
			 * callFunction("UI|setSysStatus", ""); }
			 */
		}
	}

	/**
	 * ��TABLE�����༭�ؼ�ʱ����
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {
		if (column != 0)
			return;
		if (!(com instanceof TTextField))
			return;
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag  
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;    
		table_d.acceptText();  
		String order_code = parm.getValue("ORDER_CODE");
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_code))  
			table_d.getDataStore().setItem(table_d.getSelectedRow(), "ORDER_CODE", order_code);
		   //�龫��ʾfuxin 20141030 
//        TParm isDrugParm = new TParm(TJDODBTool.getInstance().select(INDSQL.isDrug(order_code)));
		TParm isDrugParm = new TParm(TJDODBTool.getInstance().select(INDSQL.GMisDrug(order_code)));
        if(getRadioButton("GEN_DRUG").isSelected()&&getRadioButton("GEN_DRUG").isVisible()){//���龫
        	if( null != isDrugParm && isDrugParm.getCount() > 0){       
        		//��ѯ�������龫  
        		this.messageBox("��ҩƷ����ѡ���ҩƷ����(��ҩ)����,����������");
        		int old_row = table_d.getSelectedRow();  
        		table_d.getTable().grabFocus();          
        		table_d.setSelectedRow(old_row);                    
        		table_d.setSelectedColumn(0);     
        		return;    
        	}
        }else if(getRadioButton("TOXIC_DRUG").isSelected()&&getRadioButton("TOXIC_DRUG").isVisible()){//�龫
        	   //û��ѯ��������ҩ
        	if( null == isDrugParm && isDrugParm.getCount() < 1){              
        		this.messageBox("��ҩƷ����ѡ���ҩƷ����(�龫)����,����������");
        		int old_row = table_d.getSelectedRow();
        		table_d.getTable().grabFocus();  
        		table_d.setSelectedRow(old_row);
        		table_d.setSelectedColumn(0);
        		return;
        	}
        }
		// ��鹩Ӧ�����Ƿ���ڸ�ҩƷ
		TParm qtyParm = new TParm();
		qtyParm.setData("ORG_CODE", this.getValueString("TO_ORG_CODE"));
		qtyParm.setData("ORDER_CODE", order_code);
		TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
		if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
			this.messageBox("��Ӧ���Ų�����ҩƷ:" + order_desc + "(" + order_code + ")");
			table_d.removeRow(table_d.getSelectedRow());
			onAddRow();
			return;
		}                 

		// ����Ƿ�����ظ�ҩƷ     
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!table_d.getDataStore().isActive(i)) {
				continue;
			}
			if (table_d.getSelectedRow() == i) {  
				continue;                        
			}
			if (order_code.equals(table_d.getDataStore().getItemString(i, "ORDER_CODE"))) {
				this.messageBox("ҩƷ�����ظ�");
				return;
			}
		}

		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(order_code)));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			this.messageBox("ҩƷ��Ϣ����");
			return;
		}
		double stock_price = 0;
		double retail_price = 0;  
		//fux ��ʱ��Ϊ��浥λ
		table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result.getValue("STOCK_UNIT", 0));
		stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
		retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//		if ("0".equals(getValueString("UNIT_TYPE"))) {
//			// ��浥λ
//			table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result.getValue("STOCK_UNIT", 0));
//			stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//			retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//		} else {
//			// ��ҩ��λ
//			table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result.getValue("DOSAGE_UNIT", 0));
//			stock_price = result.getDouble("STOCK_PRICE", 0);
//			retail_price = result.getDouble("RETAIL_PRICE", 0);  
//		}
		// �ɱ���  
		table_d.getDataStore().setItem(table_d.getSelectedRow(), "STOCK_PRICE", stock_price);
		
		TParm agentresult = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndAgent(order_code)));
		if (agentresult.getErrCode() < 0) {
			err("ERR:" + agentresult.getErrCode() + agentresult.getErrText() + agentresult.getErrName());
			this.messageBox("ҩƷ��Ϣ����");
			return;
		}  
		table_d.getDataStore().setItem(table_d.getSelectedRow(), "VERIFYIN_PRICE", agentresult.getData("CONTRACT_PRICE",0));		
		
		// ���ۼ�
		table_d.setItem(table_d.getSelectedRow(), "RETAIL_PRICE", retail_price);
		// �趨ѡ���е���Ч��
		table_d.getDataStore().setActive(table_d.getSelectedRow(), true);
		int old_row = table_d.getSelectedRow();
		onAddRow();
		table_d.getTable().grabFocus();
		table_d.setSelectedRow(old_row);
		table_d.setSelectedColumn(2);
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public boolean popReturn(String orderCode, String orderDesc, int row, double qty, double amtPrice) {
		table_d.acceptText();  
		String order_code = orderCode;
		String order_desc = orderDesc; 
		if (!StringUtil.isNullString(order_code))
			table_d.getDataStore().setItem(row, "ORDER_CODE", order_code);

		// ��鹩Ӧ�����Ƿ���ڸ�ҩƷ
		TParm qtyParm = new TParm();
		qtyParm.setData("ORG_CODE", this.getValueString("TO_ORG_CODE"));
		qtyParm.setData("ORDER_CODE", order_code);
		TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
		if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
			this.messageBox("��Ӧ���Ų�����ҩƷ:" + order_desc + "(" + order_code + ")");
			table_d.removeRow(table_d.getSelectedRow());
			onAddRow();
			return true;
		}

		// ����Ƿ�����ظ�ҩƷ
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!table_d.getDataStore().isActive(i)) {
				continue;
			}
			if (table_d.getSelectedRow() == i) {
				continue;
			}
			if (order_code.equals(table_d.getDataStore().getItemString(i, "ORDER_CODE"))) {
				this.messageBox("ҩƷ�����ظ�");
				return true;
			}
		}

		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(order_code)));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			this.messageBox("ҩƷ��Ϣ����");
			return true;
		}
		double stock_price = 0;    
		double retail_price = 0;
		if ("0".equals(getValueString("UNIT_TYPE"))) {
			// ��浥λ
			String a = result.getValue("STOCK_UNIT", 0);
			table_d.setItem(row, "UNIT_CODE", a);
			stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
			retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
		} else {
			// ��ҩ��λ
			table_d.setItem(row, "UNIT_CODE", result.getValue("DOSAGE_UNIT", 0));
			stock_price = result.getDouble("STOCK_PRICE", 0);
			retail_price = result.getDouble("RETAIL_PRICE", 0);
		}
		// �ɱ���
		table_d.getDataStore().setItem(row, "STOCK_PRICE", stock_price);
		table_d.getDataStore().setItem(row, "QTY", qty);
		table_d.getDataStore().setItem(row, "RETAIL_PRICE", amtPrice);
		
		TParm agentresult = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndAgent(order_code)));
		if (agentresult.getErrCode() < 0) {
			err("ERR:" + agentresult.getErrCode() + agentresult.getErrText() + agentresult.getErrName());
			this.messageBox("ҩƷ��Ϣ����");
			return true;   
		}  
		table_d.getDataStore().setItem(row, "VERIFYIN_PRICE", agentresult.getData("CONTRACT_PRICE",0));		

		
		// ���ۼ�
		table_d.setItem(row, "RETAIL_PRICE", retail_price);
		// �趨ѡ���е���Ч��
		table_d.getDataStore().setActive(row, true);
		// int old_row = table_d.getSelectedRow();
		onAddRow();
		/*
		 * table_d.getTable().grabFocus(); table_d.setSelectedRow(old_row);
		 * table_d.setSelectedColumn(2);
		 */
		return true;
	}

	/**
	 * ���ֵ�ı��¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableDChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// �ж����ݸı�
		if (node.getValue().equals(node.getOldValue()))
			return true;  
		// Table������                                             
		TTable table = node.getTable();
		String columnName = table.getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
//			if (qty <= 0) {
//				this.messageBox("������������С�ڻ����0");
//				return true;
//			}     
			table.getDataStore().setItem(row, "QTY", qty);
			this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
			return false;  
		}
		return true;
	}

//	/**
//	 * �ƻ�����
//	 * 
//	 * @author liyh
//	 * @date 20121026
//	 */
//	public void onSuggest() {
//		action = "updateD";
//		String orgCode = getValueString("APP_ORG_CODE");
//		// �õ�ҩ��ϵͳ��������
//		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
//		// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
//		String fixedAmountFlag = parm.getValue("FIXEDAMOUNT_FLG", 0);
//		if ("".equals(fixedAmountFlag)) {
//			fixedAmountFlag = "1";
//		}
//		// �Զ������� :2�����������������1����������
//		String autoFillType = parm.getValue("AUTO_FILL_TYPE", 0);
//		if ("".equals(autoFillType)) {
//			autoFillType = "1";
//		}
//		
//		//�õ�ҩ��ҩ��������Զ���������
//		TParm orgParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDORG(orgCode, Operator.getRegion())));
//		if(null != orgParm && orgParm.getCount() > 0){//���ҩ��ҩ�������� �Զ��������� �� ȡ����ֵ
//			// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
//			String fixedAmountFlagNew = orgParm.getValue("FIXEDAMOUNT_FLG", 0);
//			if (!"".equals(fixedAmountFlagNew)) {
//				fixedAmountFlag = fixedAmountFlagNew;  
//			}
//			// �Զ������� :2�����������������1����������
//			String autoFillTypeNew = orgParm.getValue("AUTO_FILL_TYPE", 0);  
//			if (!"".equals(autoFillTypeNew)) {
//				autoFillType = autoFillTypeNew;
//			}
//		} 
//		  
//		// �龫��ʾ Ĭ��1 ���龫by liyh 20121025  
//		//<------ identity by shendr 20131104    
//		TParm parmD = new TParm();    
//		//fux modify 20141029     
//		if (getRadioButton("GEN_DRUG").isSelected()|| getRadioButton("NOT_DRUG").isSelected()) {// ���龫
//			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMOfSuggest(orgCode, fixedAmountFlag)));
//		} else if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
//			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrugOfSuggest(orgCode, fixedAmountFlag)));
//		} else{
//			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrugOfSuggestAll(orgCode, fixedAmountFlag)));
//		}      
//		// ------>  
//		if (parmD == null) {  
//			return;
//		}
//		if (parmD != null) {
//			// System.out.println("addParm:" + addParm);
//			TTable table = getTable("TABLE_D");
//			double allAmt = 0.0;
//			TParm result = new TParm();  
//			int rowNumb = 0;
//			for (int i = 0; i < parmD.getCount(); i++) {
//				String orderCode = parmD.getValue("ORDER_CODE", i);
//				String orderDesc = parmD.getValue("ORDER_DESC", i);
//				//�õ�ҩƷ�۸�
//				TParm priceParm = getOrderCodePrice(orderCode);   
//				//���ݲ�����ʽ�Ͳ������� ���� ��������
//				double qty = getOrderCodePrice(parmD, i, fixedAmountFlag, autoFillType,priceParm.getDouble("DOSAGE_QTY", 0));
//				if(qty<1){//if qty =0 ,no show
//					continue;
//				}
//				//�����������п�浥λ תΪ������λ
////				qty = INDTool.getInstance().getStockQtyInt(qty,priceParm.getDouble("DOSAGE_QTY", 0));
//				double amtPrice = StringTool.round(priceParm.getDouble("STOCK_PRICE", 0) * qty, 2);
//				boolean flag = popReturn(orderCode, orderDesc, rowNumb, qty, amtPrice);
//				rowNumb++;
//			}
//			action = "updateD";
//		}
//		table_d.setLockCellColumn(0	, true);
//
//	}
//
//	/**
//	 * �ƻ�����
//	 * 
//	 * @author liyh
//	 * @date 20121026
//	 */
//	public void onSuggest2() {
//		String orgCode = getValueString("APP_ORG_CODE");
//		TParm stockMParm = new TParm(TJDODBTool.getInstance().select(INDSQL.queryOrgCodeAuto()));
//		// �õ�ҩ�Ⲧ������
//		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
//		// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
//		String fixedAmountFlag = parm.getValue("FIXEDAMOUNT_FLG", 0);
//		if (null == fixedAmountFlag || "".equals(fixedAmountFlag)) {
//			fixedAmountFlag = "0";
//		}
//		// �Զ������� :2�����������������1����������
//		String autoFillType = parm.getValue("AUTO_FILL_TYPE", 0);
//		if (null == autoFillType || "".equals(autoFillType)) {
//			autoFillType = "1";
//		}
//		// �龫��ʾ Ĭ��1 ���龫by liyh 20121025
//		TParm parmD = new TParm();
//		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
//			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockM(orgCode, fixedAmountFlag)));
//		} else if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
//			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrug(orgCode, fixedAmountFlag)));
//		}
//		if (parmD == null) {
//			return;
//		}
//		if (parmD != null) {
//			// System.out.println("addParm:" + addParm);
//			TTable table = getTable("TABLE_D");
//			table.removeRow(0);
//			double allAmt = 0.0;
//			TParm result = new TParm();
//			for (int i = 0; i < parmD.getCount(); i++) {
//				String orderCode = parmD.getValue("ORDER_CODE", i);
//				int row = table.addRow();
//				TParm priceParm = getOrderCodePrice(orderCode);
//				// ���DATESTORE
//				// ORDER_CODE
//				// table.getDataStore().setItem(i, "ORDER","2N031012");
//				String speString = priceParm.getValue("SPECIFICATION", 0);
//				// table.setItem(i, "ORDER",orderCode);
//				table_d.getDataStore().setItem(i, "ORDER", parmD.getValue("ORDER_DESC", i));
//				table_d.getDataStore().setItem(i, "ORDER_ORDER", orderCode);
//				table_d.getDataStore().setItem(i, "SPECIFICATION", speString);
//				// ������λ
//				String unit = priceParm.getValue("UNIT_CODE", 0);
//				table_d.getDataStore().setItem(i, "UNIT_CODE", unit);
//				double qty = getOrderCodePrice(parmD, i, autoFillType, fixedAmountFlag,priceParm.getDouble("DOSAGE_QTY", 0));
//				// ������
//				table.setItem(i, "QTY", qty);
//				// ��������
//				table_d.getDataStore().setItem(i, "RETAIL_PRICE", priceParm.getValue("STOCK_PRICE", 0));
//				// �������
//				double amtPrice = StringTool.round(priceParm.getDouble("STOCK_PRICE", 0) * qty, 2);
//				allAmt += amtPrice;
//				table_d.getDataStore().setItem(i, "SUM_RETAIL_PRICE", amtPrice);
//				table_d.getDataStore().setItem(i, "ACTUAL_QTY", 0);
//				table_d.getDataStore().setItem(i, "QUALITY_DEDUCT_AMT", 0);
//
//				/*
//				 * result.setData("SUM_RETAIL_PRICE", i, amtPrice);
//				 * result.setData("UNIT_CODE", i, unit); result.setData("QTY",
//				 * i, qty); result.setData("ORDER", i,
//				 * priceParm.getValue("ORDER_DESC",0));
//				 * result.setData("RETAIL_PRICE", i,
//				 * priceParm.getValue("STOCK_PRICE",0));
//				 * result.setData("ORDER_ORDER", i, orderCode);
//				 * result.setData("SPECIFICATION", i, speString);
//				 * result.setData("QUALITY_DEDUCT_AMT", i, 0);
//				 * result.setData("ACTUAL_QTY", i, 0);
//				 */
//				result.setData("count", i);
//			}
//			result.setData("count", "1");
//			table.setDSValue();
//			// �����ܼ�Ǯ
//			this.setValue("SUM_RETAIL_PRICE", StringTool.round(allAmt, 2));
//			TParm a = table.getParmValue();
//			// System.out.println("---------result: "+result);
//			// table.setParmValue(result);
//			// zhangyong20110517
//			((TMenuItem) getComponent("import")).setEnabled(false);
//			action = "updateD";
//		}
//
//	}
//
//	/**
//	 * ���ҩƷ�ļ۸�
//	 * 
//	 * @param parm
//	 * @return
//	 * @author liyh
//	 * @date 20121022
//	 */
//	public TParm getOrderCodePrice(String orgCode) {
//		TParm parm = new TParm();
//		double pur_price = 0;
//		double stock_price = 0;
//		double retail_price = 0;
//		// ��ԃˎƷ��λ�̓r��
//		String sql = INDSQL.getPHAInfoByOrderSpc(orgCode);
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		// ������λ
//		pur_price = StringTool.round(result.getDouble("A.PURCH_UNIT", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//		// ��浥λ
//		stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//		// ��ҩ��λ
//		retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
//		// ��浥λ
//		parm.addData("UNIT_CODE", result.getValue("STOCK_UNIT", 0));
//		parm.addData("PURCH_PRICE", String.valueOf(pur_price));
//		parm.addData("STOCK_PRICE", String.valueOf(stock_price));
//		parm.addData("RETAIL_PRICE", String.valueOf(retail_price));
//		parm.addData("SPECIFICATION", result.getValue("SPECIFICATION", 0));
//		parm.addData("ORDER_DESC", result.getValue("ORDER_DESC", 0));
//		parm.addData("DOSAGE_QTY", result.getValue("DOSAGE_QTY", 0));
//		return parm;
//	}
//
//	/**
//	 * ���ҩƷ��������
//	 * 
//	 * @param fixedType
//	 *            ������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
//	 * @param autoFillType
//	 *            �Զ�����������1:������2�������������
//	 * @return
//	 * @author liyh
//	 * @date 20121022
//	 */
//	public double getOrderCodePrice(TParm parmD, int row, String fixedType, String autoFillType,double dosageQty) {
//		// ��߿����
//		double maxQty = parmD.getDouble("MAX_QTY", row);
//		// ��Ϳ����
//		double minQty = parmD.getDouble("MIN_QTY", row);
//		// ��ȫ�����
//		double safeQty = parmD.getDouble("SAFE_QTY", row);
//		// ���ò�����
//		double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", row);
//		// ��ǰ�����
//		double stockQty = parmD.getDouble("STOCK_QTY", row);
//		stockQty = INDTool.getInstance().getStockQtyInt(stockQty, dosageQty);
//		// ��;��
//		//fux modify 20141028 
//		double buyQty = 0.0;  
//		//buyQty =  parmD.getDouble("BUY_UNRECEIVE_QTY", row);
//		double fixedQty = 0.0;       
//		
//		if ("2".equals(autoFillType)) {// �Զ�����������1:������2�������������
//			fixedQty = maxQty - stockQty - buyQty;
//		} else {
//			fixedQty = ecoBuyQty;
//		}
//		return fixedQty;
//	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		/**
		 * Ȩ�޿��� Ȩ��1:һ�����ֻ��ʾ������������ Ȩ��9:���Ȩ����ʾȫԺҩ�ⲿ��
		 */
		// ��ʾȫԺҩ�ⲿ��
/*		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' ")));
			getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'B' "));
			if (parm.getCount("NAME") > 0) {
				getTextFormat("APP_ORG_CODE").setValue(parm.getValue("ID", 0));
			}
			dept_flg = false;
		} else {
			getTextFormat("APP_ORG_CODE").setPopupMenuSQL(INDSQL.initTextFormatIndOrg("B", Operator.getRegion()));
		}
		getTextFormat("APP_ORG_CODE").onQuery();*/
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ��ʼ������ʱ��
		setValue("REQUEST_DATE", date);
		// ��ʼ�����뵥����   
		// getComboBox("REQTYPE_CODE").setSelectedIndex(0);
		// ��ʼ������״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
		// ��ʼ��TABLE
		table_m = getTable("TABLE_M");  
		table_d = getTable("TABLE_D");  
		row_m = -1;
		row_d = -1;  
		seq = 0;
		action = "insertM";
		setValue("APP_ORG_CODE",Operator.getDept());
//		((TMenuItem) getComponent("import")).setEnabled(false);
		// ҩƷ����
		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		String is_separate_req = result.getValue("IS_SEPARATE_REQ",0);
		if("N".equals(is_separate_req)){
			((TLabel)getComponent("tLabel_6")).setVisible(false);
			((TRadioButton)getComponent("GEN_DRUG")).setVisible(false);
			((TRadioButton)getComponent("TOXIC_DRUG")).setVisible(false);
			((TRadioButton)getComponent("NOT_DRUG")).setVisible(false);
			
			((TRadioButton)getComponent("NOT_DRUG")).setSelected(true);
		}else if ("Y".equals(is_separate_req)) {
			((TRadioButton)getComponent("NOT_DRUG")).setVisible(false);
		}
	}

	/**
	 * ��ѯ��������
	 * 
	 * @return
	 */
	private TParm onQueryParm() {
		TParm parm = new TParm();
		if (!"".equals(getValueString("APP_ORG_CODE"))) {
			parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
		}
		if (!"".equals(getValueString("BASEMANAGE_NO"))) {
			parm.setData("BASEMANAGE_NO", getValueString("BASEMANAGE_NO"));
		}
		if (!"".equals(getValueString("REQTYPE_CODE"))) {
			parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
		} else {
			parm.setData("TYPE_DEPT_TEC", "");
		}
		if (!"".equals(getValueString("START_DATE")) && !"".equals(getValueString("END_DATE"))) {
			parm.setData("START_DATE", getValue("START_DATE"));
			parm.setData("END_DATE", getValue("END_DATE"));
		}
        if (getRadioButton("STATIC_NO_B").isSelected()) {//������Դ   - �ƻ�����
         	parm.setData("APPLY_TYPE", "1");
         }else if(getRadioButton("STATIC_NO_C").isSelected()) {//������Դ   - �ƻ�����
         	parm.setData("APPLY_TYPE", "2");
         }else if (getRadioButton("STATIC_NO_D").isSelected()) {//������Դ   - �Զ�����
         	parm.setData("APPLY_TYPE", "3");
         } 		
        if(getRadioButton("GEN_DRUG").isSelected()){//���龫
        	parm.setData("DRUG_CATEGORY", "1");
        }else if(getRadioButton("TOXIC_DRUG").isSelected()){//�龫
        	parm.setData("DRUG_CATEGORY", "2");
        }else {//����
        	parm.setData("DRUG_CATEGORY", "3");
        }          
		// zhangyong20110517
		parm.setData("REGION_CODE", Operator.getRegion());

		if (parm == null) {
			return parm;
		}
		return parm;
	}

	/**
	 * ���ѡ���������Ϸ�
	 * 
	 * @param table
	 * @param row
	 * @param args
	 */
	private void getTableSelectValue(TTable table, int row, String[] args) {
		for (int i = 0; i < args.length; i++) {
			setValue(args[i], table.getItemData(row, args[i]));
		}
	}

	/**
	 * �������뵥��ȡ��ϸ����Ϣ����ʾ��ϸ������
	 * 
	 * @param req_no
	 */
	private void getTableDInfo(String req_no) {
		// ��ϸ��Ϣ  
		table_d.setParmValue(new TParm());  
		table_d.setSelectionMode(0);
		TDS tds = new TDS();
		String sql = "SELECT BASEMANAGE_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, "
			+ "QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, "
			+ "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "
			+ "FROM IND_BASEMANAGED WHERE BASEMANAGE_NO='" + req_no + "' "
			+ "ORDER BY SEQ_NO";
		tds.setSQL(sql);    
		tds.retrieve();
		if (tds.rowCount() == 0) {
			seq = 1;  
		} else {            
			seq = getMaxSeq(tds, "SEQ_NO");
		}

		// �۲���
		IndRequestObserver obser = new IndRequestObserver();
		obser.setOrgCode(getValueString("APP_ORG_CODE"));
		tds.addObserver(obser);
		table_d.setDataStore(tds);
		table_d.setDSValue();
		this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
	}

	/**
	 * ���һ��ϸ��
	 */
	private void onAddRow() {
		// ��δ�༭��ʱ����  
		if (!this.isNewRow())
			return;  
		int row = table_d.addRow(); 
		TParm parm = new TParm();   
		parm.setData("ACTIVE", false);
		table_d.getDataStore().setActive(row, false);
	}

	/**
	 * �Ƿ���δ�༭��
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean falg = false;
		TParm parmBuff = table_d.getDataStore().getBuffer(table_d.getDataStore().PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			falg = true;
		}
		return falg;
	}

	/**
	 * ���ݶ���״̬���˲�ѯ���
	 * 
	 * @param parm
	 * @return
	 */
	private TParm onFilterQueryByStatus(TParm parm) {
		String update_flg = "0";
		boolean flg = false;
		TDataStore ds = new TDataStore();
		for (int i = parm.getCount("BASEMANAGE_NO") - 1; i >= 0; i--) {
			
			String sql = "SELECT BASEMANAGE_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, "
				+ "QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, "
				+ "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "
				+ "FROM IND_BASEMANAGED WHERE BASEMANAGE_NO='" + parm.getValue("BASEMANAGE_NO", i) + "' "
				+ "ORDER BY SEQ_NO";
			ds.setSQL(sql);
			ds.retrieve(); 
			if (ds.rowCount() == 0) {  
				flg = false;
			} else {
				flg = true;
				for (int j = 0; j < ds.rowCount(); j++) {
					update_flg = ds.getItemString(j, "UPDATE_FLG");
					if ("0".equals(update_flg) || "1".equals(update_flg)) {
						// δ���
						flg = false;  
						break;
					}  
				}
			}
			// ����״̬
			if (getRadioButton("UPDATE_FLG_A").isSelected()) {
				// δ���
				if (flg) {
					parm.removeRow(i);
				}
			} else {
				// ���
				if (!flg) {  
					parm.removeRow(i);
				}
			}
		}
		// û��ȫ������Ȩ��
		if (!dept_flg) {
			TParm optOrg = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
			for (int i = parm.getCount("APP_ORG_CODE") - 1; i >= 0; i--) {
				boolean check_flg = true;
				for (int j = 0; j < optOrg.getCount("ID"); j++) {
					if (parm.getValue("APP_ORG_CODE", i).equals(optOrg.getValue("ID", j))) {
						check_flg = false;
						break;
					} else {
						continue;
					}
				}
				if (check_flg) {
					parm.removeRow(i);
				}
			}
		}
		return parm;
	}

	/**
	 * ����������������PARM
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getRequestMParm(TParm parm) {
		Timestamp date = SystemTool.getInstance().getDate();
		parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
		parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
		parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
		parm.setData("REQUEST_USER", Operator.getID());
		parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
		parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
		String unit_type = "0";
		if ("TEC".equals(getValueString("REQTYPE_CODE")))
			// ���տ�浥λ����
			unit_type = IndSysParmTool.getInstance().onQuery().getValue("UNIT_TYPE", 0);
		else
			// 1 �ǿۿⵥλ����ҩ��
			unit_type = "1";
		parm.setData("UNIT_TYPE", unit_type);
		parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
		// zhangyong20110517
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
        if(getRadioButton("GEN_DRUG").isSelected()){//���龫
        	parm.setData("DRUG_CATEGORY", "1");
        }else if(getRadioButton("TOXIC_DRUG").isSelected()){//�龫
        	parm.setData("DRUG_CATEGORY", "2");
        }else {//����
        	parm.setData("DRUG_CATEGORY", "3");
        }
        parm.setData("APPLY_TYPE", "1");
       if (getRadioButton("STATIC_NO_C").isSelected()) {//������Դ   - �ƻ�����
        	parm.setData("APPLY_TYPE", "2");
        }else if (getRadioButton("STATIC_NO_D").isSelected()) {//������Դ   - �Զ�����
        	parm.setData("APPLY_TYPE", "3");
        }         
		return parm;  
	}

	/**
	 * ���������ܽ��
	 * 
	 * @return
	 */
	private double getSumRetailPrice() {
		table_d.acceptText();
		double sum = 0;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!table_d.getDataStore().isActive(i)) {
				continue;
			}
			double amount1 = table_d.getItemDouble(i, "QTY");
			sum += table_d.getItemDouble(i, "RETAIL_PRICE") * amount1;
		}
		return StringTool.round(sum, 2);
	}
  
	/**
	 * ���ݼ���  
	 * 
	 * @return
	 */
	private boolean CheckDataM() {  
		if ("".equals(getValueString("APP_ORG_CODE"))) {
			this.messageBox("���첿�Ų���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("REQTYPE_CODE"))) {
			this.messageBox("���������Ϊ��");
			return false;
		}
/*		TextFormatINDOrg to_org_code = (TextFormatINDOrg) this.getComponent("TO_ORG_CODE");
		if (to_org_code.isEnabled()) {  
			if ("".equals(getValueString("TO_ORG_CODE"))) {
				this.messageBox("��Ӧ���Ų���Ϊ��");
				return false;
			}
		}*/
		if ("".equals(getValueString("TO_ORG_CODE"))) {
			this.messageBox("��Ӧ���Ų���Ϊ��");
			return false;
		}		
		return true;
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataD() {
		table_d.acceptText();
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!table_d.getDataStore().isActive(i)) {
				continue;
			}  
//			if (table_d.getItemDouble(i, "QTY") <= 0) {
//				this.messageBox("����ҩ������������С�ڻ����0");
//				return false;
//			}   
		}  
		return true;
	}

	/**
	 * ���ⲿ�ſ�漰ҩƷ�������ж�
	 */
	private boolean getOrgStockCheck() {
		/** ��Ӧ����ҩƷ¼��ܿ� */
		String org_code = getValueString("TO_ORG_CODE");
		String order_code = "";
		double stock_qty = 0;
		double qty = 0;                    

		for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
			if (!table_d.getDataStore().isActive(i)) {
				continue;
			}
			order_code = table_d.getDataStore().getItemString(i, "ORDER_CODE");
			qty = table_d.getDataStore().getItemDouble(i, "QTY");
			TParm resultParm = new TParm();
			if ("0".equals(getValueString("UNIT_TYPE"))) {
				String sql = INDSQL.getPHAInfoByOrder(order_code);
				resultParm = new TParm(TJDODBTool.getInstance().select(sql));
				if (resultParm.getErrCode() < 0) {
					this.messageBox("ҩƷ��Ϣ����");
					return false;
				}  
				qty = StringTool.round(qty * resultParm.getDouble("DOSAGE_QTY", 0), 2);
			}
			TParm inparm = new TParm();
			inparm.setData("ORG_CODE", org_code);
			inparm.setData("ORDER_CODE", order_code);
			TParm resultQTY = INDTool.getInstance().getStockQTY(inparm);
			stock_qty = resultQTY.getDouble("QTY", 0);
			if (stock_qty < 0) {
				stock_qty = 0;
			}
			if (qty > stock_qty) {
				TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
				if (this.messageBox("��ʾ", "ҩƷ:" + order.getValue("ORDER_DESC") + "(" + order_code + ") �����������,��ǰ�����Ϊ" + stock_qty + ",�Ƿ����", 2) == 0) {
					return true;
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * ���콨�� �¼�
	 */
	public void onRequestSugget(){
		if (getRadioButton("STATIC_NO_A").isSelected()) {//������Դ   - ȫ��
			((TMenuItem) getComponent("import")).setEnabled(false);
         }else if (getRadioButton("STATIC_NO_B").isSelected()) {//������Դ   - �ֶ�
        	 ((TMenuItem) getComponent("import")).setEnabled(false);
         }else if(getRadioButton("STATIC_NO_C").isSelected()) {//������Դ   - �ƻ�����
        	 ((TMenuItem) getComponent("import")).setEnabled(true);
         }else if (getRadioButton("STATIC_NO_D").isSelected()) {//������Դ   - �Զ�����
        	 ((TMenuItem) getComponent("import")).setEnabled(false);
         } 
	}
	/**
	 * �õ����ı��
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	private int getMaxSeq(TDataStore dataStore, String columnName) {
		if (dataStore == null)
			return 0;
		// ����������
		int count = dataStore.rowCount();
		// ��������
		int s = 0;
		for (int i = 0; i < count; i++) {
			int value = dataStore.getItemInt(i, columnName);
			// �������ֵ
			if (s < value) {
				s = value;
				continue;
			}
		}
		// ���ż�1
		s++;
		return s;
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
	 * �õ�TextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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
	 * ȡ��SYS_FEE��Ϣ��������״̬����   
	 * 
	 * @param order_code
	 *            String
	 */
	private void setSysStatus(String order_code) {
		TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
		String status_desc = "ҩƷ����:" + order.getValue("ORDER_CODE") + " ҩƷ����:" + order.getValue("ORDER_DESC") + " ��Ʒ��:" + order.getValue("GOODS_DESC") + " ���:"
				+ order.getValue("SPECIFICATION");
		callFunction("UI|setSysStatus", status_desc);
	}

}
