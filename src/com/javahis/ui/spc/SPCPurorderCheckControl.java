package com.javahis.ui.spc;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndPurorderDTool;
import jdo.spc.IndStockMTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.dom4j.Document;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
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
import com.javahis.manager.IndPurOrderObserver;
import com.javahis.ui.spc.ExportXmlUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��������Control
 * </p>
 * 
 * <p>
 * Description: ��������Control
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */

public class SPCPurorderCheckControl extends TControl {

	/**
	 * ������ǣ�1.insertM�����������ƻ������� 2.updateM�����¶����ƻ�������
	 */
	private String action = "insertM";

	// ϸ�����
	private int seq;

	// ����Ȩ��
	private boolean gift_flg = true;

	// ȫ������Ȩ��
	private boolean dept_flg = true;

	public SPCPurorderCheckControl() {
		super();
	}

	// ��ʽ��������
	java.text.DecimalFormat dfInt = new java.text.DecimalFormat("##########0");

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��������
		initPage();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ���������
		TParm parm = new TParm();
		if (!"".equals(getValueString("ORG_CODE"))) {
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		} else {
			// û��ȫ������Ȩ��
			if (!dept_flg) {
				this.messageBox("��ѡ���ѯ����");
				return;
			}
		}
		if (!"".equals(getValueString("SUP_CODE"))) {
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		}
		if (!"".equals(getValueString("PURORDER_NO"))) {
			parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
		}
		if (!"".equals(getValueString("START_DATE")) && !"".equals(getValueString("END_DATE"))) {
			parm.setData("START_DATE", getValue("START_DATE"));
			parm.setData("END_DATE", getValue("END_DATE"));
		}
	
		if (getRadioButton("STATIC_NO_B").isSelected()) {// ������Դ - �ֹ�
			parm.setData("APPLY_TYPE1", "1");
		} else if (getRadioButton("STATIC_NO_C").isSelected()) {// ������Դ - �ƻ�����
			parm.setData("APPLY_TYPE2", "2");
		} else if (getRadioButton("STATIC_NO_D").isSelected()) {// ������Դ - �Զ�����
			parm.setData("APPLY_TYPE3", "3");
		}

		// �龫��ʾ Ĭ��1 ���龫by liyh 20121025
		String drugCateory = "1";
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DRUG_CATEGORY", "1");
		} else if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
			parm.setData("DRUG_CATEGORY", "2");
		} else if (getRadioButton("NOT_DRUG").isSelected()) {// ����
			parm.setData("DRUG_CATEGORY", "3");
		}
		//���״̬
		String checkState = "1";
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {// δ���
			parm.setData("CHECK_DATE_A", "1");
		} else if (getRadioButton("UPDATE_FLG_B").isSelected()) {// �����
			parm.setData("CHECK_DATE_B", "1");
			if (!"".equals(getValueString("CHECK_DATE_START")) && !"".equals(getValueString("CHECK_DATE_END"))) {//��������� �������ʱ���ѯ
				parm.setData("CHECK_DATE_START", getValue("CHECK_DATE_START"));
				parm.setData("CHECK_DATE_END", getValue("CHECK_DATE_END"));
			}				
		}		
		// zhangyong20110517
		parm.setData("REGION_CODE", Operator.getRegion());
		if (parm == null) {
			return;
		}
		// ���ؽ����
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.spc.INDPurorderAction", "onQueryM", parm);

		
		/*
		 * // ������Դ if (getRadioButton("STATIC_NO_B").isSelected()) { // �ֹ����� for
		 * (int i = result.getCount() - 1; i >= 0; i--) { if
		 * (!"".equals(result.getValue("PLAN_NO", i))) { result.removeRow(i); }
		 * } } else if (getRadioButton("STATIC_NO_C").isSelected()) { // �ƻ��ƻ�
		 * for (int i = result.getCount() - 1; i >= 0; i--) { if
		 * ("".equals(result.getValue("PLAN_NO", i))) { result.removeRow(i); } }
		 * }
		 */
		// ��ѯϸ���״̬
		boolean flg = false;
		for (int i = result.getCount() - 1; i >= 0; i--) {
			String checkDate = result.getValue("CHECK_DATE",i);
			if(null == checkDate || "".equals(checkDate)){
				flg = false;
			}else {
				flg = true;
			}
			// ����״̬
			if (getRadioButton("UPDATE_FLG_A").isSelected()) {
				// δ���
				if (flg) {
					result.removeRow(i);
				}
			} else {
				// ���
				if (!flg) {
					result.removeRow(i);
				}
			}
		}

		if (result == null || result.getCount("PURORDER_NO") == 0 || result.getCount() <= 0) {
			getTable("TABLE_M").removeRowAll();
			getTable("TABLE_D").removeRowAll();
			this.messageBox("û�в�ѯ����");
			return;
		}

		// �����TABLE_M
		TTable table_M = getTable("TABLE_M");
		table_M.setParmValue(result);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ҩƷ����
		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		String is_separate_req = result.getValue("IS_SEPARATE_REQ",0);
		if("Y".equals(is_separate_req)){
			getRadioButton("GEN_DRUG").setSelected(true);
		}else if ("N".equals(is_separate_req)) {
			getRadioButton("NOT_DRUG").setSelected(true);
		}
		getRadioButton("STATIC_NO_A").setSelected(true);
		getRadioButton("UPDATE_FLG_A").setSelected(true);
		getRadioButton("GEN_DRUG").setSelected(true);
		String clearString = "START_DATE;END_DATE;PURORDER_DATE;ORG_CODE;PURORDER_NO;" + "RES_DELIVERY_DATE;REASON_CODE;DESCRIPTION;CHECK_USER";
		this.clearValue(clearString);
		getTextFormat("SUP_CODE").setValue(null);
		getTextFormat("SUP_CODE").setText("");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ��ʼ����ѯ����
		this.setValue("CHECK_DATE_END", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("CHECK_DATE_START", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ����״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
//		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("PURORDER_NO").setEnabled(true);
		getTextFormat("SUP_CODE").setEnabled(true);
		getRadioButton("IS_CHECK").setSelected(true);
		getRadioButton("NO_CHECK").setSelected(false);
		getTable("TABLE_M").removeRowAll();
		getTable("TABLE_D").removeRowAll();
		getTable("TABLE_M").setSelectionMode(0);
		action = "insertM";

	}
	
	/**
	 * ��շ���2
	 */
	public void onChangeRaido() {
		String clearString = "PURORDER_DATE;ORG_CODE;PURORDER_NO;" + "RES_DELIVERY_DATE;REASON_CODE;DESCRIPTION;CHECK_USER";
		this.clearValue(clearString);
		getTextFormat("SUP_CODE").setValue(null);
		getTextFormat("SUP_CODE").setText("");
		// ����״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
//		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("PURORDER_NO").setEnabled(true);
		getTextFormat("SUP_CODE").setEnabled(true);
		getRadioButton("IS_CHECK").setSelected(true);
		getRadioButton("NO_CHECK").setSelected(false);
		getTable("TABLE_M").removeRowAll();
		getTable("TABLE_D").removeRowAll();
		getTable("TABLE_M").setSelectionMode(0);
		action = "insertM";

	}	
	
	/**
	 * �Ƿ����
	 */
	public void onSaveCheck() {
		if(getRadioButton("IS_CHECK").isSelected()){
			((TMenuItem) getComponent("save")).setEnabled(true);
		}else {
			((TMenuItem) getComponent("save")).setEnabled(false);
		}
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ���ؽ����
		TParm result = new TParm();
		String optUser = getValueString("CHECK_USER").trim();
		if(null == optUser || "".equals(optUser) || optUser.length()< 1){
			optUser = Operator.getName();
		}
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("CHECK_USER",optUser );
		if (getRadioButton("IS_CHECK").isSelected()) {// �Ƿ�����ƻ�����
			String purOrderNo = getValueString("PURORDER_NO");
			if (null == purOrderNo || "".equals(purOrderNo)) {
				this.messageBox("��ѡ��һ����¼");
				return;
			}
	
			// ���������
			TParm parm = new TParm();
			parm.setData("OPT_USER", optUser);
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("PURORDER_NO", getValue("PURORDER_NO"));

			// ִ�����ݸ���״̬
			result = new TParm(TJDODBTool.getInstance().update(SPCSQL.upDateStatusINDPourOrderM(parm)));
			// ������ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
				return;
			}else{
				// ִ�����ݸ���״̬
				result = new TParm(TJDODBTool.getInstance().update(SPCSQL.upDateStatusINDPourOrderD(parm)));
				if ("updateD".equals(action)) {
					// ϸ����
					if (!CheckDataD()) {
						return;
					}
					TTable table_D = getTable("TABLE_D");
					table_D.acceptText();
					TDataStore dataStore = table_D.getDataStore();
					// ���ȫ���Ķ����к�
					int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
					// ���ȫ����������
					int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);

					TParm inParm = new TParm();
			
					TParm getTRA = new TParm();
					TParm getQTY = new TParm();
					String order_code = "";
					String org_code = getValueString("ORG_CODE");
					double s_qty = 0;
					double d_qty = 0;
					for (int i = 0; i < rows.length; i++) {
						if (getRadioButton("STATIC_NO_C").isSelected()) {
						}
						dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
						dataStore.setItem(rows[i], "OPT_DATE", date);
						dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
						order_code = dataStore.getItemString(rows[i], "ORDER_CODE");
						inParm.setData("ORDER_CODE", i, order_code);
						inParm.setData("PURORDER_QTY", i, dataStore.getItemDouble(rows[i], "PURORDER_QTY"));
						// ���������ת����
						getTRA = INDTool.getInstance().getTransunitByCode(order_code);
						if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
							this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
							return;
						}
						s_qty = getTRA.getDouble("STOCK_QTY", 0);
						inParm.setData("STOCK_QTY", i, s_qty);
						d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
						inParm.setData("DOSAGE_QTY", i, d_qty);
						// ��������趨�ж�
						TParm qtyParm = new TParm();
						qtyParm.setData("ORG_CODE", org_code);
						qtyParm.setData("ORDER_CODE", order_code);
						// System.out.println("ORG_CODE" + org_code);
						// System.out.println("ORDER_CODE" + order_code);
/*						getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
						// System.out.println("getQTY" + getQTY);
						if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
							this.messageBox("ҩƷ" + order_code + "δ�趨���������Ϣ");
							return;
						}*/
					}
					inParm.setData("ORG_CODE", org_code);
					inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
					result = TIOM_AppServer.executeAction("action.spc.INDPurorderAction", "onSavePurOrder", inParm);
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("E0001");
						return;
					}
				}
			}

			// ��ӡ����
//			onPrint();
			this.messageBox("P0001");
			onExportXml();
			onClear();
			onQuery();
		}
	}



	/**
	 * �����ɹ���ΪXML�ļ�
	 */
	public void onExportXml() {
		if (getTable("TABLE_M").getSelectedRow() > -1) {

			// ��������
			String pur_order = getValueString("PURORDER_NO");

			// �õ���Ӧ�̴���
			String supCode = getValueString("SUP_CODE");
			
			// Ҫ��������ϸ������
			TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPurOrderDSqlByNoNew(pur_order)));
			List list = new ArrayList();

			TParm xmlParm = new TParm();
			for (int i = 0; i < parm.getCount(); i++) {
				TParm t = (TParm) parm.getRow(i);
				// System.out.println("Parm:"+parm);
				Map<String, String> map = new LinkedHashMap();
				map.put("warehouse", "K932290201");
				// <------- identity by shendr 20131119  ץȡ�ͻ���ַ����
				TParm descParm = new TParm(TJDODBTool.getInstance().select(INDSQL.querySupplierDescription(supCode)));;
				String description = descParm.getValue("DESCRIPTION",0);
//				map.put("deliverycode", "922268901");
				map.put("deliverycode", description);
				// --------->
				map.put("cstcode", t.getValue("SUP_CODE"));
				// <-------- identity by shendr 20131119 ץȡ��Ӧ��ҩƷ����
//				map.put("goods", t.getValue("SPC_MEDICINE_CODE"));
				String order_code = t.getValue("ORDER_CODE");
				TParm supOrderCodeParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getSupplyUnit(supCode,order_code)));;
				String sup_order_code = supOrderCodeParm.getValue("SUP_ORDER_CODE",0);
				map.put("goods", sup_order_code);
//				map.put("goods", t.getValue("SPC_MEDICINE_CODE"));
				// --------->
				map.put("goodname", t.getValue("ORDER_DESC"));
				map.put("spec", t.getValue("SPECIFICATION"));
				map.put("msunitno", t.getValue("UNIT_CHN_DESC"));
				map.put("producer", t.getValue("MAN_CODE"));
				map.put("billqty", t.getValue("PURORDER_QTY"));
				map.put("prc", t.getValue("PURORDER_PRICE"));
				map.put("ordercode", t.getValue("SUP_ORDER_CODE"));			
				// ��������
				String purorder_no = t.getValue("PURORDER_NO");
				String seq = t.getValue("SEQ_NO");
				map.put("purchaseid", purorder_no + "-" + seq);
				list.add(map);
			}
			
			 Document doc = ExportXmlUtil.createXml(list);
			 ExportXmlUtil.exeSaveXml(doc, pur_order+".xml");
			 

		} else {
			this.messageBox("��ѡ��Ҫ���ɵĲɹ���!");
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
			if (getTable("TABLE_M").getSelectedRow() > -1) {
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������", 2) == 0) {
					TTable table_D = getTable("TABLE_D");
					table_D.removeRowAll();
					TParm parm = new TParm();
					String pur_order = getValueString("PURORDER_NO");
					parm.setData("PURORDER_NO", pur_order);
					String org_code = getValueString("ORG_CODE");
					parm.setData("ORG_CODE", org_code);
					// ��ѯ����ϸ��
					TParm result = new TParm();
					result = IndPurorderDTool.getInstance().onQuery(parm);
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("ɾ��ʧ��");
						return;
					}
					if (result.getCount() > 0) {
						// ɾ�����ϸ��
						String order_code = "";
						parm.setData("DELETE_SQL", table_D.getDataStore().getUpdateSQL());
						TParm getTRA = new TParm();
						for (int i = 0; i < result.getCount(); i++) {
							// ���������ת����
							order_code = result.getValue("ORDER_CODE", i);
							getTRA = INDTool.getInstance().getTransunitByCode(order_code);
							if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
								this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
								return;
							}
							parm.setData("ORDER_CODE", i, order_code);
							parm.setData("PURORDER_QTY", i, result.getDouble("PURORDER_QTY", i));
							double s_qty = getTRA.getDouble("STOCK_QTY", 0);
							parm.setData("STOCK_QTY", i, s_qty);
							double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
							parm.setData("DOSAGE_QTY", i, d_qty);
						}
					}
					result = TIOM_AppServer.executeAction("action.spc.INDPurorderAction", "onDeleteM", parm);
					// ɾ���ж�
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("ɾ��ʧ��");
						return;
					}
					getTable("TABLE_M").removeRow(getTable("TABLE_M").getSelectedRow());
					this.messageBox("ɾ���ɹ�");
					this.onClear();
				}
			} else {
				TParm inparm = new TParm();
				TTable table_D = getTable("TABLE_D");
				int row = table_D.getSelectedRow();
				String purorder_no = getValueString("PURORDER_NO");
				String org_code = getValueString("ORG_CODE");
				String order_code = table_D.getItemString(row, "ORDER_CODE");
				double seq_no = table_D.getDataStore().getItemDouble(row, "SEQ_NO");
				inparm.setData("PURORDER_NO", purorder_no);
				inparm.setData("ORDER_CODE", order_code);
				inparm.setData("SEQ_NO", seq_no);
				// ��ѯϸ��
				TParm result = IndPurorderDTool.getInstance().onQuery(inparm);
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("ɾ��ʧ��");
					return;
				}
				if (result.getCount() == 0) {
					table_D.removeRow(table_D.getSelectedRow());
				} else {
					if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
						double pur_qty = result.getDouble("PURORDER_QTY", 0);
						inparm.setData("PURORDER_QTY", pur_qty);
						inparm.setData("ORG_CODE", org_code);
						table_D.removeRow(table_D.getSelectedRow());
						String[] sql = table_D.getDataStore().getUpdateSQL();
						inparm.setData("UPDATE_SQL", sql);
						// ���������ת����
						TParm getTRA = INDTool.getInstance().getTransunitByCode(order_code);
						if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
							this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
							return;
						}
						double s_qty = getTRA.getDouble("STOCK_QTY", 0);
						inparm.setData("STOCK_QTY", s_qty);
						double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
						inparm.setData("DOSAGE_QTY", d_qty);
						result = TIOM_AppServer.executeAction("action.spc.INDPurorderAction", "onDeleteD", inparm);
						// ɾ���ж�
						if (result == null || result.getErrCode() < 0) {
							this.messageBox("ɾ��ʧ��");
							return;
						}
						this.messageBox("ɾ���ɹ�");
					}
				}
			}
		}
	}

	/**
	 * ��ӡ������
	 */
	public void onPrint() {
		TTable table_d = getTable("TABLE_D");
		if ("".equals(this.getValueString("PURORDER_NO"))) {
			this.messageBox("�����ڶ�����");
			return;
		}
		if (table_d.getRowCount() > 0) {
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()) + "�ƻ����뵥");
			date.setData("SUP_CODE", "TEXT", "��Ӧ����: " + this.getTextFormat("SUP_CODE").getText());
			date.setData("PUR_NO", "TEXT", "���뵥��: " + this.getValueString("PURORDER_NO"));
			date.setData("DATE", "TEXT", "��������: " + this.getValueString("PURORDER_DATE").substring(0, 10).replace('-', '/'));
			// �������
			TParm parm = new TParm();
			String order_code = "";
			double sum_money = 0;
			double amt = 0;
			for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
				if (!table_d.getDataStore().isActive(i)) {
					continue;
				}
				if ("Y".equals(table_d.getItemString(i, "END_FLG"))) {
					continue;
				}
				order_code = table_d.getDataStore().getItemString(i, "ORDER_CODE");
				TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderInfoByCode(order_code, this.getValueString("SUP_CODE"), "PUR")));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("ҩƷ��Ϣ����");
					return;
				}
				parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
				parm.addData("SPECIFICATION", inparm.getValue("SPECIFICATION", 0));
				parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
				parm.addData("MAN_CODE", inparm.getValue("MAN_CHN_DESC", 0));
				// parm.addData("QTY",
				// StringTool.round(table_d.getItemDouble(i, "PURORDER_QTY"),
				// 3));
				// luhai modify 2012-3-7 begin
				// this.messageBox(dfInt.format(table_d.getItemDouble(i,
				// "PURORDER_QTY"))+"");
				parm.addData("QTY", dfInt.format(table_d.getItemDouble(i, "PURORDER_QTY")));
				// luhai modify 2012-3-7 end
				parm.addData("PRICE", StringTool.round(table_d.getItemDouble(i, "PURORDER_PRICE"), 4));
				amt = table_d.getItemDouble(i, "PURORDER_QTY") * table_d.getItemDouble(i, "PURORDER_PRICE");
				parm.addData("AMT", StringTool.round(amt, 2));
				sum_money += amt;
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
			parm.addData("SYSTEM", "COLUMNS", "PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT");
			parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
			// System.out.println("PARM---" + parm);
			date.setData("TABLE", parm.getData());
			// ��β����
			date.setData("CHECK", "TEXT", "��ˣ� ");
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			date.setData("TOT", "TEXT", "�ܽ�" + StringTool.round(sum_money, 2));

			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\Purorder.jhw", date);
		} else {
			this.messageBox("û�д�ӡ����");
			return;
		}
	}

	/**
	 * ������(TABLE_M)�����¼�
	 */
	public void onTableMClicked() {
		TTable table = getTable("TABLE_M");
		int row = table.getSelectedRow();

		TParm parm = table.getParmValue();
		if (row != -1) {
			// ������Ϣ(TABLE��ȡ��)
			setValue("PURORDER_DATE", table.getItemTimestamp(row, "PURORDER_DATE"));
			setValue("PURORDER_NO", table.getItemString(row, "PURORDER_NO"));
			setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
			setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
			setValue("RES_DELIVERY_DATE", table.getItemTimestamp(row, "RES_DELIVERY_DATE"));
			setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
			setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
			// �趨ҳ��״̬
			getTextField("PURORDER_NO").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);

			setValue("CHECK_USER", parm.getValue("CHECK_USER", row));
			String isDrug = parm.getValue("DRUG_CATEGORY", row);
/*			if ("1".equals(isDrug)) {// �ж�ҩƷ���� ��ť����ͨ1���龫2
				getRadioButton("GEN_DRUG").setSelected(true);
				getRadioButton("TOXIC_DRUG").setSelected(false);
			} else {
				getRadioButton("GEN_DRUG").setSelected(false);
				getRadioButton("TOXIC_DRUG").setSelected(true);
			}*/
			action = "updateM";

			// ��ϸ��Ϣ
			TTable table_D = getTable("TABLE_D");
			table_D.removeRowAll();
			table_D.setSelectionMode(0);
			TDS tds = new TDS();
			tds.setSQL(INDSQL.getPurOrderDByNo(getValueString("PURORDER_NO")));
			tds.retrieve();
			if (tds.rowCount() == 0) {
				// this.messageBox("û�ж�����ϸ");
				seq = 1;
			} else {
				seq = getMaxSeq(tds, "SEQ_NO");
			}

			// �۲���
			IndPurOrderObserver obser = new IndPurOrderObserver();
			obser.setOrgCode(table.getItemString(row, "ORG_CODE"));
			tds.addObserver(obser);
			table_D.setDataStore(tds);
			table_D.setDSValue();

			// �����ܼ�Ǯ
			double sum = getSumMoney();
			this.setValue("SUM_MONEY", sum);

			// ���һ��ϸ��
//			onAddRow();
		}
	}

	/**
	 * ��ϸ���(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {
		TTable table = getTable("TABLE_D");
		int row = table.getSelectedRow();
		if (row != -1) {
			action = "updateD";
			// ������Ϣ
			TTable table_M = getTable("TABLE_M");
			table_M.setSelectionMode(0);
			// ȡ��SYS_FEE��Ϣ��������״̬����
			/*
			 * String order_code = table.getDataStore().getItemString(table.
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
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TParm parm = new TParm();
		parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// ���õ����˵�
		textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig("%ROOT%\\config\\spc\\INDSupOrder.x"), parm);
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
		TTable table = getTable("TABLE_D");
		table.acceptText();
		String order_code = parm.getValue("ORDER_CODE");
		/*************** ����ҳ�� �Ƿ����龫���ж�ҩƷ ѡ���Ƿ���ȷ start by liyh 20121025 **************/
		// �龫��ʾ Ĭ��1 ���龫by liyh 20121025
		TParm isDrugParm = new TParm(TJDODBTool.getInstance().select(INDSQL.isDrug(order_code)));
		System.out.println("------------INDSQL.isDrug(" + order_code + "): " + INDSQL.isDrug(order_code));
		String drugCateory = "1";
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			if (null != isDrugParm && isDrugParm.getCount() > 0) {
				this.messageBox("��ҩ���龫ҩƷ������ѡ��");
				return;
			}
		} else if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
			if (null == isDrugParm || isDrugParm.getCount() < 1) {
				this.messageBox("��ҩ�Ƿ��龫ҩƷ������ѡ��");
				return;
			}
		}

		/*************** ����ҳ�� �Ƿ����龫���ж�ҩƷ ѡ���Ƿ���ȷ end by liyh 20121025 **************/
		if (!StringUtil.isNullString(order_code))
			table.getDataStore().setItem(table.getSelectedRow(), "ORDER_CODE", order_code);
		String specification = parm.getValue("SPECIFICATION");
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(specification))
			table.setItem(table.getSelectedRow(), "SPECIFICATION", specification);
		String purch_unit = parm.getValue("PURCH_UNIT");
		if (!StringUtil.isNullString(purch_unit))
			table.setItem(table.getSelectedRow(), "BILL_UNIT", purch_unit);
		double price = parm.getDouble("CONTRACT_PRICE");
		table.setItem(table.getSelectedRow(), "PURORDER_PRICE", price);
		table.getDataStore().setActive(table.getSelectedRow(), true);
		// ���������
		TParm qtyParm = new TParm();
		qtyParm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
		qtyParm.setData("ORDER_CODE", order_code);
		TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
		// System.out.println("getQTY" + getQTY);
/*		if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
			this.messageBox("ҩƷ:" + order_desc + "(" + order_code + ") δ�趨���������Ϣ");
			onAddRow();
			table.removeRow(table.getSelectedRow());
			return;
		}*/
		// �ж��Ƿ����ظ�����
		for (int i = 0; i < table.getDataStore().rowCount(); i++) {
			if (i == table.getSelectedRow()) {
				continue;
			}
			if (order_code.equals(table.getDataStore().getItemData(i, "ORDER_CODE"))) {
				this.messageBox("ҩƷ:" + order_desc + "(" + order_code + ") �Ѵ���");
				table.removeRow(table.getSelectedRow());
				return;
			}
		}
	}

	/**
	 * ȫѡ��ѡ��ѡ���¼�
	 */
	public void onCheckSelectAll() {
		TTable table = getTable("TABLE_D");
		table.acceptText();
		if (table.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < table.getRowCount(); i++) {
				if (!table.getDataStore().isActive(i)) {
					continue;
				}
				table.getDataStore().setItem(i, "UPDATE_FLG", "2");
				this.setValue("SUM_MONEY", 0);
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				if (!table.getDataStore().isActive(i)) {
					continue;
				}
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				this.setValue("SUM_MONEY", getSumMoney());
			}
		}
		table.setDSValue();
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
		String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("PURORDER_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("������������С�ڻ����0");
				return true;
			} else {
				// �����ܼ�Ǯ
				if (!"2".equals(node.getTable().getDataStore().getItemString(row, "UPDATE_FLG"))) {
					node.getTable().getDataStore().setItem(row, "PURORDER_QTY", qty);
					this.setValue("SUM_MONEY", getSumMoney());
				}
			}
		}
		if ("GIFT_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("������������С��0");
				return true;
			}
		}
		if ("PURORDER_PRICE".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("���۲���С�ڻ����0");
				return true;
			} else {
				// �����ܼ�Ǯ
				if (!"2".equals(node.getTable().getDataStore().getItemString(row, "UPDATE_FLG"))) {
					node.getTable().getDataStore().setItem(row, "PURORDER_PRICE", qty);
					this.setValue("SUM_MONEY", getSumMoney());
				}
			}
		}
		return false;
	}

	/**
	 * ���(TABLE)��ѡ��ı��¼�
	 * 
	 * @param obj
	 */
	public void onTableCheckBoxClicked(Object obj) {
		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���
		int column = tableDown.getSelectedColumn();
		if (column == 0) {
			// this.messageBox("");
			String flg = tableDown.getDataStore().getItemString(tableDown.getSelectedRow(), "UPDATE_FLG");
			double amt = tableDown.getItemDouble(tableDown.getSelectedRow(), "PURORDER_QTY")
					* tableDown.getItemDouble(tableDown.getSelectedRow(), "PURORDER_PRICE");
			double sum_amt = this.getValueDouble("SUM_MONEY");
			if ("2".equals(flg)) {
				sum_amt = sum_amt - amt;
			} else {
				sum_amt = sum_amt + amt;
			}
			this.setValue("SUM_MONEY", sum_amt);
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {

		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		// ��ʼ����ѯ����
		this.setValue("CHECK_DATE_END", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("CHECK_DATE_START", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");		
		((TMenuItem) getComponent("delete")).setEnabled(false);
//		((TMenuItem) getComponent("export")).setEnabled(false);
//		((TMenuItem) getComponent("save")).setEnabled(false);
		action = "insertM";

		// ע�ἤ��INDSupOrder�������¼�
		getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponentUD");
		addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, "onTableDChangeValue");
		// ��TABLEDEPT�е�CHECKBOX��������¼�
		callFunction("UI|TABLE_D|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
		// ҩƷ����
		TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		String is_separate_req = result.getValue("IS_SEPARATE_REQ",0);
		if("Y".equals(is_separate_req)){
			getRadioButton("GEN_DRUG").setSelected(true);
		}else if ("N".equals(is_separate_req)) {
			getRadioButton("NOT_DRUG").setSelected(true);
		}
	}



	

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataD() {
		TTable table_D = getTable("TABLE_D");
		table_D.acceptText();
		for (int i = 0; i < table_D.getRowCount(); i++) {
			if (!table_D.getDataStore().isActive(i)) {
				continue;
			}
			if (table_D.getItemDouble(i, "PURORDER_QTY") <= 0) {
				this.messageBox("������������С�ڻ����0");
				return false;
			}
		}
		return true;
	}

	/**
	 * �����ܽ��
	 * 
	 * @return
	 */
	private double getSumMoney() {
		TTable table = getTable("TABLE_D");
		TDataStore ds = table.getDataStore();
		table.acceptText();
		double sum = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!"2".equals(ds.getItemString(i, "UPDATE_FLG"))) {
				sum += table.getItemDouble(i, "PURORDER_QTY") * table.getItemDouble(i, "PURORDER_PRICE");
			}
		}
		return StringTool.round(sum, 2);
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
	 * �ƻ�����
	 * 
	 * @author liyh
	 * @date 20121026
	 */
	public void onSuggest() {
		String orgCode = getValueString("ORG_CODE");
		// �õ�ҩ�Ⲧ������
		TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		// �Զ�������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
		String fixedAmountFlag = parm.getValue("FIXEDAMOUNT_FLG", 0);
		if (null == fixedAmountFlag || "".equals(fixedAmountFlag)) {
			fixedAmountFlag = "0";
		}
		// �Զ������� :2�����������������1����������
		String autoFillType = parm.getValue("AUTO_FILL_TYPE", 0);
		if (null == autoFillType || "".equals(autoFillType)) {
			autoFillType = "1";
		}
		// �龫��ʾ Ĭ��1 ���龫by liyh 20121025
		TParm parmD = new TParm();
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockM(orgCode, fixedAmountFlag)));
		} else if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
			parmD = new TParm(TJDODBTool.getInstance().select(INDSQL.queryStockMDrug(orgCode, fixedAmountFlag)));
		}
		if (parmD == null) {
			return;
		}
		if (parmD != null) {
			// System.out.println("addParm:" + addParm);
			TTable table = getTable("TABLE_D");
			table.removeRow(0);
			double allAmt = 0.0;
			for (int i = 0; i < parmD.getCount(); i++) {
				String orderCode = parmD.getValue("ORDER_CODE", i);
				int row = table.addRow();
				TParm priceParm = getOrderCodePrice(orderCode);
				// ���DATESTORE
				// ORDER_CODE
				table.getDataStore().setItem(i, "ORDER_CODE", orderCode);
				// ������λ
				String unit = priceParm.getValue("UNIT_CODE", 0);
				table.setItem(i, "BILL_UNIT", unit);
				double qty = getOrderCodePrice(parmD, i, autoFillType, fixedAmountFlag);
				// ������
				table.setItem(i, "PURORDER_QTY", qty);
				// ������
				table.setItem(i, "GIFT_QTY", 0);
				// ��������
				table.setItem(i, "PURORDER_PRICE", priceParm.getValue("STOCK_PRICE", 0));
				// �������
				double amtPrice = StringTool.round(priceParm.getDouble("STOCK_PRICE", 0) * qty, 2);
				allAmt += amtPrice;
				table.setItem(i, "TOT_MONEY", amtPrice);
				table.setItem(i, "ACTUAL_QTY", 0);
				table.setItem(i, "QUALITY_DEDUCT_AMT", 0);
			}
			table.setDSValue();
			// getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			// �����ܼ�Ǯ
			this.setValue("SUM_MONEY", StringTool.round(allAmt, 2));
			// zhangyong20110517
//			((TMenuItem) getComponent("export")).setEnabled(false);
		}

	}

	/**
	 * ���ҩƷ�ļ۸�
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public TParm getOrderCodePrice(String orgCode) {
		TParm parm = new TParm();
		double pur_price = 0;
		double stock_price = 0;
		double retail_price = 0;
		// ��ԃˎƷ��λ�̓r��
		String sql = INDSQL.getPHAInfoByOrder(orgCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ������λ
		pur_price = StringTool.round(result.getDouble("A.PURCH_UNIT", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
		// ��浥λ
		stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
		// ��ҩ��λ
		retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0) * result.getDouble("DOSAGE_QTY", 0), 2);
		// ��浥λ
		parm.addData("UNIT_CODE", result.getValue("STOCK_UNIT", 0));
		parm.addData("PURCH_PRICE", String.valueOf(pur_price));
		parm.addData("STOCK_PRICE", String.valueOf(stock_price));
		parm.addData("RETAIL_PRICE", String.valueOf(retail_price));
		return parm;
	}

	/**
	 * ���ҩƷ��������
	 * 
	 * @param fixedType
	 *            ������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
	 * @param autoFillType
	 *            �Զ�����������1:������2�������������
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public double getOrderCodePrice(TParm parmD, int row, String fixedType, String autoFillType) {
		// ��߿����
		double maxQty = parmD.getDouble("MAX_QTY", row);
		// ��Ϳ����
		double minQty = parmD.getDouble("MIN_QTY", row);
		// ��ȫ�����
		double safeQty = parmD.getDouble("SAFE_QTY", row);
		// ���ò�����
		double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", row);
		// ��ǰ�����
		double stockQty = parmD.getDouble("STOCK_QTY", row);
		// ��;��
		double buyQty = parmD.getDouble("BUY_UNRECEIVE_QTY", row);
		double fixedQty = 0.0;
		if ("0".equals(fixedType)) {// 0�����Ƕ�������
			fixedQty = ecoBuyQty;
		} else {
			if ("2".equals(autoFillType)) {// �Զ�����������1:������2�������������
				fixedQty = maxQty - stockQty - buyQty;
			} else {
				fixedQty = ecoBuyQty;
			}
		}
		return fixedQty;
	}

	/**
	 * ��ö���/�ƻ����������PARM
	 * 
	 * @param purOrderNo
	 * @param orgCode
	 * @param SUP_CODE
	 * @param isDrug
	 *            �Ƿ�Ϊ�龫 Y�ǣ�N����
	 * @return
	 * @author liyh
	 * @date 20121026
	 */
	private TParm getPurOrderMParm(String purOrderNo, String orgCode, String SUP_CODE, String fixedType, String autoFillType, String isDrug) {
		TParm parm = new TParm();
		parm.setData("PURORDER_NO", purOrderNo);
		parm.setData("ORG_CODE", orgCode);
		parm.setData("SUP_CODE", SUP_CODE);
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("REGION_CODE", "H01");
		// �Զ�������ʽ
		parm.setData("FIXEDAMOUNT_FLG", fixedType);
		// �Զ���������
		parm.setData("AUTO_FILL_TYPE", autoFillType);
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
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
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
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
