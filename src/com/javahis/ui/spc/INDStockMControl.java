package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndStockMTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҩ��������
 * </p>
 * 
 * <p>
 * Description: ҩ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author liyh
 * @version 4.0
 */
public class INDStockMControl extends TControl {

	private String action = "insertM";

	// ������
	private TTable table_m;

	// ϸ����
	private TTable table_d;

	public INDStockMControl() {

	}
	//liuyalin add 20170602
	double percent = 0.0;
	double mm_use_qty = 0.0;
	double dd_use_qty = 0.0;
	//liuyalin add end 20170602

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// ��ʼ��TABLE
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ���������
		TParm parm = new TParm();
		// ���ؽ����
		TParm result = new TParm();
		String region_code = Operator.getRegion();
		Timestamp date = SystemTool.getInstance().getDate();
		parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
		parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		parm.setData("REGION_CODE", region_code);
		parm.setData("MATERIAL_LOC_CODE", this
				.getValueString("MATERIAL_LOC_CODE"));
		parm.setData("MATERIAL_LOC_DESC", this
				.getValueString("MATERIAL_LOC_DESC"));
		parm.setData("ELETAG_CODE", this.getValueString("ELETAG_CODE"));
		parm.setData("DISPENSE_FLG", this.getValueString("DISPENSE_FLG"));
		parm.setData("DISPENSE_ORG_CODE", this
				.getValueString("DISPENSE_ORG_CODE"));
		parm.setData("QTY_TYPE", this.getValueString("QTY_TYPE"));
		// liuyalin 20170601 add ����ת����
		TParm parmA = new TParm(TJDODBTool.getInstance().select(INDSQL.getPhaTransUnit(parm.getValue("ORDER_CODE"))));
//		percent = parmA.getDouble("STOCK_QTY", 0) / parmA.getDouble("DOSAGE_QTY", 0);
//		System.out.println("percent��������" + percent);
		//modify by wangjc 20171222
		percent = parmA.getDouble("DOSAGE_QTY", 0);
		parm.setData("MM_USE_QTY", this.getValueDouble("MM_USE_QTY")*percent);
		parm.setData("DD_USE_QTY", this.getValueDouble("DD_USE_QTY")*percent);
//		parm.setData("MM_USE_QTY", this.getValueDouble("MM_USE_QTY"));
//		parm.setData("DD_USE_QTY", this.getValueDouble("DD_USE_QTY"));
		// liuyalin 20170601 add end
		parm.setData("MAX_QTY", this.getValueDouble("MAX_QTY"));
		parm.setData("SAFE_QTY", this.getValueDouble("SAFE_QTY"));
		parm.setData("MIN_QTY", this.getValueDouble("MIN_QTY"));
		parm.setData("ECONOMICBUY_QTY", this.getValueDouble("ECONOMICBUY_QTY"));
		parm.setData("BUY_UNRECEIVE_QTY", this
				.getValueDouble("BUY_UNRECEIVE_QTY"));
		parm.setData("STANDING_QTY", this.getValueDouble("STANDING_QTY"));
		parm.setData("LOCK_QTY", getValueDouble("LOCK_QTY"));
		// <---------- identity by shendr 20131104
		// String active_flg = this.getValueString("ACTIVE_FLG");
		// if("N".equals(active_flg))
		// active_flg="Y";
		// else
		// active_flg="N";
		// parm.setData("ACTIVE_FLG", active_flg);
		// ---------->
		parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
		parm.setData("MATERIAL_LOC_SEQ", this
				.getValueString("MATERIAL_LOC_SEQ"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));

		if ("insertM".equals(action)) {
			if (!CheckDataM()) {
				return;
			}
			result = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDStockM(this.getValueString("ORG_CODE"), this
							.getValueString("ORDER_CODE"))));
			if (result.getCount() > 0) {
				this.messageBox("��ҩƷ�Ѵ���");
				return;
			}
			result = IndStockMTool.getInstance().onInsertIndStockM(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			// 20150302 wangjingchun add start
			// ����������Ϊ����ʱ�����п�ͬʱ����
			String org_type_sql = "SELECT * FROM IND_ORG WHERE ORG_CODE = '"
					+ parm.getValue("ORG_CODE") + "' ";
			TParm org_type_parm = new TParm(TJDODBTool.getInstance().select(
					org_type_sql));
			if (org_type_parm.getValue("ORG_TYPE", 0).equals("A")) {
				String mes = "";
				String org_sql = "SELECT * FROM IND_ORG WHERE ORG_TYPE = 'B'";
				TParm org_parm = new TParm(TJDODBTool.getInstance().select(
						org_sql));
				for (int i = 0; i < org_parm.getCount("ORG_CODE"); i++) {
					TParm b_parm = new TParm(TJDODBTool.getInstance().select(
							INDSQL.getINDStockM(org_parm
									.getValue("ORG_CODE", i), this
									.getValueString("ORDER_CODE"))));
					if (b_parm.getCount() > 0) {
						mes += " " + b_parm.getValue("ORG_CODE", 0);
						continue;
					}
					parm.setData("ORG_CODE", org_parm.getValue("ORG_CODE", i));
					b_parm = IndStockMTool.getInstance()
							.onInsertIndStockM(parm);
					if (b_parm.getErrCode() < 0) {
						this.messageBox("E0001");
						continue;
					}
				}
				this.messageBox(mes + "�и�ҩƷ�Ѵ���");
			}
			// 20150302 wangjingchun add end
			this.messageBox("P0001");
		} else if ("updateM".equals(action)) {
			if (!CheckDataM()) {
				return;
			}
			// ִ����������
			result = TIOM_AppServer.executeAction("action.spc.INDStockMAction",
					"onUpdate", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
		} else if ("updateD".equals(action)) {
			table_d.acceptText();
			int rows = table_d.getRowCount();
			TParm outParm = table_d.getParmValue();
			String org_code = this.getValueString("ORG_CODE");
			String order_code = this.getValueString("ORDER_CODE");
			String active_flg = "";
			String batch_seq = "";
			for (int i = 0; i < rows; i++) {
				active_flg = table_d.getParmValue().getRow(i).getValue(
						"ACTIVE_FLG");
				if ("Y".equals(outParm.getValue("ACTIVE_FLG", i)))
					active_flg = "N";
				else
					active_flg = "Y";
				batch_seq = outParm.getValue("BATCH_SEQ", i);
				result = new TParm(TJDODBTool.getInstance().update(
						INDSQL.updateIndStock(org_code, order_code, active_flg,
								batch_seq)));
			}
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			messageBox("P0001");
		} else {
			TDataStore dataStore = table_d.getDataStore();
			// ���ȫ���Ķ����к�
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table_d.update()) {
				messageBox("E0001");
				return;
			}
			table_d.setDSValue();
			messageBox("P0001");
		}
		onQuery();
	}

	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table_m.removeRowAll();
		table_d.removeRowAll();
		String org_code = getValueString("ORG_CODE");
		if ("".equals(org_code)) {
			this.messageBox("���Ŵ��벻��Ϊ��");
			return;
		}
		if ("Y".equals(this.getValueString("SAFE_FLG"))
				&& "Y".equals(this.getValueString("MAX_FLG"))) {
			this.messageBox("����ͬʱѡ����ڰ�ȫ������͸�����ߴ���");
			return;
		}
		String sql = INDSQL.getIndStockMInfo(org_code,
				getValueString("ORDER_CODE"), "");
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("---" + sql);
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("û�в�ѯ����");
			return;
		}
		TParm result = new TParm();
		String qty = "0.0";
		for (int i = 0; i < parm.getCount(); i++) {
			// liuyalin 20170601 add ����ת����
			TParm parmA = new TParm(TJDODBTool.getInstance().select(INDSQL.getPhaTransUnit(parm.getValue("ORDER_CODE",i))));
//			percent = parmA.getDouble("DOSAGE_QTY", 0) / parmA.getDouble("STOCK_QTY", 0);
//			mm_use_qty = parm.getDouble("MM_USE_QTY",i);
//		    dd_use_qty = parm.getDouble("DD_USE_QTY",i);
//			parm.setData("MM_USE_QTY", i, mm_use_qty*percent);
//			parm.setData("DD_USE_QTY", i, dd_use_qty*percent);
			//modify by wangjc
			percent = parmA.getDouble("DOSAGE_QTY", 0);
			mm_use_qty = parm.getDouble("MM_USE_QTY",i);
		    dd_use_qty = parm.getDouble("DD_USE_QTY",i);
			parm.setData("MM_USE_QTY", i, mm_use_qty/percent);
			parm.setData("DD_USE_QTY", i, dd_use_qty/percent);
			// liuyalin 20170601 add end
			TParm stock_qty = new TParm();
			stock_qty.setData("ORG_CODE", this.getValueString("ORG_CODE"));
			stock_qty.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			// String qtyS = INDTool.getInstance().getCurrentStockQty(org_code,
			// parm.getValue("ORDER_CODE", i));//
			// IndStockDTool.getInstance().onQueryStockQTY(stock_qty);
			// <--- �����޸� by shendr
			sql = INDSQL.getStockQtyOfStockM(this.getValueString("ORG_CODE"),
					parm.getData("ORDER_CODE", i).toString());
			result = new TParm(TJDODBTool.getInstance().select(sql));
			// qty = result.getDouble("QTY", 0);
			qty = result.getValue("STOCK_QTY");
			if (qty == "" || qty == "0.0") {
				qty = "0��";
			} else {
				qty = qty.substring(qty.indexOf("[") + 1);
				qty = qty.substring(0, qty.lastIndexOf("]"));
			}
			// --->
			// System.out.println(parm.getValue("ORDER_CODE",
			// i)+"----qtyS:"+qtyS);
			/*
			 * qty = result.getDouble("QTY", 0); if (qty < 0) { qty = 0; }
			 */

			// parm.setData("STOCK_QTY", i, qtyS);
			parm.setData("STOCK_QTY", i, qty);
			String sql1 = INDSQL.getDeptDesc(parm.getValue("DISPENSE_ORG_CODE",
					i));
			TParm orgDesc = new TParm(TJDODBTool.getInstance().select(sql1));
			parm.setData("DISPENSE_ORG_CODE", i, orgDesc.getData(
					"ORG_CHN_DESC", 0));
		}

		/*
		 * if ("Y".equals(getValueString("SAFE_FLG"))) { for (int i =
		 * parm.getCount() - 1; i >= 0; i--) { if (parm.getDouble("STOCK_QTY",
		 * i) < parm.getDouble("SAFE_QTY", i)) { parm.removeRow(i); } } }
		 */
		/*
		 * if ("Y".equals(getValueString("MAX_FLG"))) { for (int i =
		 * parm.getCount() - 1; i >= 0; i--) { if (parm.getDouble("STOCK_QTY",
		 * i) > parm.getDouble("MAX_QTY", i)) { parm.removeRow(i); } } }
		 */
		// for (int i = 0; i < parm.getCount(); i++) {
		// if ("N".equals(parm.getValue("ACTIVE_FLG",i))) {
		// parm.setData("ACTIVE_FLG", i, "Y");
		// }else {
		// parm.setData("ACTIVE_FLG", i, "N");
		// }
		// }
//		System.out.println("���ݣ�����"+parm);
		table_m.setParmValue(parm);
	}

	/**
	 * ������λ
	 * 
	 * @author liyh
	 * @date 20121013
	 */
	public void onUpdate() {
		TParm parm = new TParm();
		if (null != parm && parm.getCount() > 0) {
			int count = parm.getCount();
			for (int i = 0; i < count; i++) {
				String orgCode = parm.getValue("ORG_CODE", i);
				String orderCode = parm.getValue("ORDER_CODE", i);
				String eleTagCode = parm.getValue("ELETAG_CODE", i);
				TParm result = new TParm(TJDODBTool.getInstance()
						.update(
								INDSQL.updateEleTagCode(eleTagCode, orgCode,
										orderCode)));
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("����ʧ��");
					return;
				}
			}
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ���VALUE
		String clear = "ORG_CODE;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE;DISPENSE_FLG;"
				+ "DISPENSE_ORG_CODE;MM_USE_QTY;DD_USE_QTY;MAX_QTY;SAFE_QTY;"
				+ "QTY_TYPE;MIN_QTY;ECONOMICBUY_QTY;BUY_UNRECEIVE_QTY;SUP_CODE;"
				+ "STANDING_QTY;ACTIVE_FLG;SAFE_FLG;MAX_FLG;MATERIAL_LOC_DESC;ELETAG_CODE;LOCK_QTY";
		this.clearValue(clear);
		table_m.removeRowAll();
		table_m.setSelectionMode(0);
		table_d.removeRowAll();
		table_d.setSelectionMode(0);
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
		// .setEnabled(false);
		((TCheckBox) this.getComponent("DISPENSE_FLG")).setSelected(true);
		action = "insertM";
	}

	/**
	 * TABLE_M�����¼�
	 */
	public void onTableMClicked() {
		int row = table_m.getSelectedRow();
		TParm parm = table_m.getParmValue();
		if (row != -1) {
			this.setValue("ORDER_CODE", table_m.getItemData(row, "ORDER_CODE"));
			this.setValue("ORDER_DESC", table_m.getItemData(row, "ORDER_DESC"));
			// ��� ��λ �͵��ӱ�ǩ by liyh 20121026
			this.setValue("MATERIAL_LOC_CODE", table_m.getItemData(row,
					"MATERIAL_LOC_CODE"));
			this.setValue("MATERIAL_LOC_DESC", parm.getValue(
					"MATERIAL_LOC_DESC", row));
			// ��Ҫ��Ӧ��
			this.setValue("SUP_CODE", parm.getValue("SUP_CODE", row));
			this.setValue("ELETAG_CODE", table_m
					.getItemData(row, "ELETAG_CODE"));
			this.setValue("DISPENSE_FLG", table_m.getItemData(row,
					"DISPENSE_FLG"));
			// this.setValue("DISPENSE_ORG_CODE", table_m.getItemData(row,
			// "DISPENSE_ORG_CODE"));
			this.setValue("MM_USE_QTY", table_m.getItemData(row, "MM_USE_QTY"));
			this.setValue("DD_USE_QTY", table_m.getItemData(row, "DD_USE_QTY"));
			this.setValue("MAX_QTY", table_m.getItemData(row, "MAX_QTY"));
			this.setValue("SAFE_QTY", table_m.getItemData(row, "SAFE_QTY"));
			this.setValue("MIN_QTY", table_m.getItemData(row, "MIN_QTY"));
			this.setValue("ECONOMICBUY_QTY", table_m.getItemData(row,
					"ECONOMICBUY_QTY"));
			this.setValue("BUY_UNRECEIVE_QTY", table_m.getItemData(row,
					"BUY_UNRECEIVE_QTY"));
			this.setValue("STANDING_QTY", table_m.getItemData(row,
					"STANDING_QTY"));
			this.setValue("QTY_TYPE", table_m.getItemData(row, "QTY_TYPE"));
			this.setValue("ACTIVE_FLG", table_m.getItemData(row, "ACTIVE_FLG"));
			String locSeq = table_m.getItemData(row, "MATERIAL_LOC_SEQ") + "";
			this.setValue("MATERIAL_LOC_SEQ", locSeq);

			setValue("LOCK_QTY", parm.getDouble("LOCK_QTY", row));
			// if ("Y".equals(table_m.getItemData(row, "DISPENSE_FLG"))) {
			// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
			// .setEnabled(true);
			// } else {
			// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
			// .setEnabled(false);
			// }

			setMMDDUseQty(row); // add by wukai on 20161017ץȡ����������Ϣ
			action = "updateM";

			// ��ϸ��Ϣ
			table_d.removeRowAll();
			table_d.setSelectionMode(0);
			TParm parmD = new TParm(TJDODBTool.getInstance().select(
					INDSQL
							.getINDStockAllOfStockM(this
									.getValueString("ORG_CODE"), this
									.getValueString("ORDER_CODE"), Operator
									.getRegion())));
			for (int i = 0; i < parmD.getCount(); i++) {
				if ("N".equals(parmD.getValue("ACTIVE_FLG", i))) {
					parmD.setData("ACTIVE_FLG", i, "Y");
				} else {
					parmD.setData("ACTIVE_FLG", i, "N");
				}
			}
			table_d.setParmValue(parmD);
			/*
			 * tds.setSQL(INDSQL.getINDStockAllOfStockM(this.getValueString("ORG_CODE"
			 * ), this.getValueString("ORDER_CODE"), Operator.getRegion()));
			 * tds.retrieve(); if (tds.rowCount() == 0) {
			 * this.messageBox("û����ϸ��Ϣ"); }
			 * 
			 * // �۲��� IndMainBatchValidObserver obser = new
			 * IndMainBatchValidObserver(); tds.addObserver(obser);
			 * table_d.setDataStore(tds); table_d.setDSValue();
			 */
		}
	}

	/**
	 * �����º�������ƽ���պ����� add by wukai on 20161017 ���������������������� ��ֵ�Զ���������
	 * 
	 * @param row
	 */
	private void setMMDDUseQty(int row) {
		double mmQty = table_m.getItemDouble(row, "MM_USE_QTY");
		// this.messageBox(mmQty + "");
		if (mmQty == 0.0) {
			// this.messageBox("ttt");
			String sql = "SELECT ORG_CODE AS ID, ORG_FLG AS FLG, ORG_TYPE AS TYPE FROM IND_ORG WHERE ORG_CODE = '"
					+ this.getValueString("ORG_CODE") + "'";
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			String flg = (String) parm.getData("TYPE", 0);

			Calendar cal = Calendar.getInstance(Locale.CHINA);
			cal.roll(Calendar.MONTH, -1);
			String year = String.valueOf(cal.get(Calendar.YEAR));
			String mon = String.valueOf(cal.get(Calendar.MONTH) + 1);
			int maxDay = cal.getActualMaximum(Calendar.DATE);
			if (mon.length() == 1) {
				mon = "0" + mon;
			}
			String startDate = year + mon + "01000000";
			String endDate = year + mon + maxDay + "235959";
			String orgCode = this.getValueString("ORG_CODE");
			System.out.println("mon : " + mon + "  ,,,,   maxDay : " + maxDay
					+ "  ,,,,,   flg : " + flg);

			TParm res = null;
			if ("A".equalsIgnoreCase(flg)) { // ҩ��
				String sqlA = INDSQL.getVerUseQTYSQLA(table_m.getItemString(
						row, "ORDER_CODE"), orgCode, startDate, endDate);
				res = new TParm(TJDODBTool.getInstance().select(sqlA));
			} else if ("B".equalsIgnoreCase(flg)) { // ҩ��
				String sqlB = INDSQL.getVerUseQTYSQLB(table_m.getItemString(
						row, "ORDER_CODE"), orgCode, startDate, endDate);
				res = new TParm(TJDODBTool.getInstance().select(sqlB));
			} else if ("C".equalsIgnoreCase(flg)) { // ����
				String sqlC = INDSQL.getVerUseQTYSQLC(table_m.getItemString(
						row, "ORDER_CODE"), orgCode, startDate, endDate);
				res = new TParm(TJDODBTool.getInstance().select(sqlC));
			}
			if (res != null && res.getCount("QTY") > 0) {
				this.setValue("MM_USE_QTY", res.getData("QTY", 0));
				this.setValue("DD_USE_QTY", res.getDouble("QTY", 0) / maxDay);
			}
		}

	}

	/**
	 * ��ϸ���(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {
		int row_d = table_d.getSelectedRow();
		if (row_d != -1) {
			action = "updateD";
			table_m.setSelectionMode(0);
		}
	}

	/**
	 * ���Ÿı��¼�
	 */
	public void onChangeOrgCode() {
		this.setValue("MATERIAL_LOC_CODE", "");
		String org_code = this.getValueString("ORG_CODE");
		// if (!"".equals(org_code)) {
		// // �趨��λ
		// ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
		// setOrgCode(org_code);
		// ( (TComboINDMaterialloc)
		// getComponent("MATERIAL_LOC_CODE")).onQuery();
		// }

		// TParm parm = new TParm(TJDODBTool.getInstance().select(
		// INDSQL.getINDOrgType(org_code)));
		// String org_type = "";
		// this.setValue("DISPENSE_ORG_CODE", "");
		// if ("C".equals(parm.getValue("ORG_TYPE", 0))) {
		// org_type = "B";
		// } else if ("B".equals(parm.getValue("ORG_TYPE", 0))) {
		// org_type = "A";
		// } else {
		// org_type = "-";
		// }
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
		// .setOrgType(org_type);
		// ((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE")).onQuery();
	}

	/**
	 * �����ı��¼�
	 */
	public void onDispenseFlgAction() {
		if ("Y".equals(this.getValueString("DISPENSE_FLG"))) {
			((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
					.setEnabled(true);
		} else {
			((TComboOrgCode) this.getComponent("DISPENSE_ORG_CODE"))
					.setEnabled(false);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * �����ҩ������
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpXML() {
		TParm parm = new TParm();
		Object result = openDialog(
				"%ROOT%\\config\\spc\\INDMateriallocEleTag.x", parm);
		if (null != result) {
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm xmlParm = new TParm();
			try {
				try {
					filePath = filePath.replaceAll("\\\\", "/");
					xmlParm = (TParm) FileUtils
							.readXMLFileOfMateriaLocAndOrderCode(filePath);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int count = xmlParm.getCount("ORDER_CODE");
				if (null != xmlParm && count > 0) {
					TParm resultTParm = new TParm();
					for (int i = 0; i < count; i++) {
						TParm mlTParm = new TParm();
						String orgCode = (String) xmlParm
								.getData("ORG_CODE", i);
						String orderCode = (String) xmlParm.getData(
								"ORDER_CODE", i);
						String materialLocCode = (String) xmlParm.getData(
								"MATERIAL_LOC_CODE", i);
						mlTParm.setData("ORG_CODE", orgCode);
						mlTParm.setData("ORDER_CODE", orderCode);
						mlTParm.setData("REGION_CODE", materialLocCode);
						mlTParm.setData("MATERIAL_LOC_CODE", xmlParm.getData(
								"MATERIAL_LOC_CODE", i));
						mlTParm.setData("MATERIAL_LOC_DESC", xmlParm.getData(
								"MATERIAL_LOC_DESC", i));
						mlTParm.setData("ELETAG_CODE", xmlParm.getData(
								"ELETAG_CODE", i));
						mlTParm.setData("OPT_USER", Operator.getID());
						mlTParm.setData("OPT_TERM", Operator.getIP());
						// �Ȳ�ѯ���ŵ�ҩƷ�Ƿ���ڣ������� INSERT OR UPDATE
						TParm stockParm = new TParm(
								TJDODBTool.getInstance()
										.select(
												INDSQL.getINDStockM(orgCode,
														orderCode)));
						String sql = "";
						if (null == stockParm || stockParm.getCount() < 1) {
							sql = INDSQL.onSaveMatLocStockM(mlTParm);
						} else {
							sql = INDSQL.onUpdateMatLocStockM(mlTParm);
						}
						resultTParm = new TParm(TJDODBTool.getInstance()
								.update(sql));
						if (resultTParm.getErrCode() < 0) {
							err("ERR:" + resultTParm.getErrCode()
									+ resultTParm.getErrText()
									+ resultTParm.getErrName());
							break;
						}
						// �޸�ҩ����λ
						String updateSql = INDSQL
								.onUpdateIndStcokMaterialLocCode(orgCode,
										orderCode, materialLocCode);
						resultTParm = new TParm(TJDODBTool.getInstance()
								.update(updateSql));
						if (resultTParm.getErrCode() < 0) {
							err("ERR:" + resultTParm.getErrCode()
									+ resultTParm.getErrText()
									+ resultTParm.getErrName());
							break;
						}

					}
					if (resultTParm.getErrCode() < 0) {
						err("ERR:" + resultTParm.getErrCode()
								+ resultTParm.getErrText()
								+ resultTParm.getErrName());
						messageBox("����ʧ��");
						return;
					}
					messageBox("����ɹ�");
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("��沿�Ų���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("ҩƷ���Ʋ���Ϊ��");
			return false;
		}

		return true;
	}

	public void onBarcode() {
		TTable t = this.getTable("TABLE_M");
		TParm rowParm = t.getParmValue();
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("��ѡ��Ҫ��ӡ����ļ�¼");
			return;
		}
		String containerId = t.getItemString(row, "MATERIAL_LOC_CODE");
		// String containerName = t.getItemString(row, "CONTAINER_DESC");
		String orderName = t.getItemString(row, "ORDER_DESC");
		String specification = rowParm.getValue("SPECIFICATION", row);
		TParm printData = new TParm();
		printData.setData("BAR_CODE", "TEXT", containerId);
		printData.setData("ORDER_DESC", "TEXT", orderName);
		printData.setData("CONTAINER_CODE", "TEXT", "");
		printData.setData("SPECIFICATION", "TEXT", specification);
		printData.setData("GOODS_DESC", "TEXT", rowParm.getValue("GOODS_DESC",
				row));
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\SPCStockM.jhw",
				printData);
	}

	/**
	 * ���ӱ�ǩˢ�¿��
	 */
	public void onRefresh() {
		int row = table_m.getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ��ҩƷ���ٽ���ˢ�¿�����");
			return;
		}
		String eletagCode = "";
		String orgCode = "";
		String orderCode = "";
		/**
		 * ���õ��ӱ�ǩ
		 */
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		orderCode = this.getValueString("ORDER_CODE");
		eletagCode = this.getValueString("ELETAG_CODE");
		orgCode = this.getValueString("ORG_CODE");
		// ���õ��ӱ�ǩ�ӿ���ʾ�������˸
		TParm inParm = new TParm();
		inParm.setData("ORDER_CODE", orderCode);
		inParm.setData("ORG_CODE", orgCode);
		TParm outParm = new TParm(TJDODBTool.getInstance().select(
				SPCSQL.getQty(inParm)));
		String orderDesc = outParm.getValue("ORDER_DESC", 0);
		String spec = outParm.getValue("SPECIFICATION");
		String qty = outParm.getValue("QTY", 0);
		// ������ӱ�ǩeletagCodeΪ��ʱ�������õ��ӱ�ǩ�ӿ�
		if (eletagCode != null && !eletagCode.equals("")) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("ProductName", orderDesc);
			map.put("SPECIFICATION", spec);
			map.put("NUM", qty);
			map.put("TagNo", eletagCode);
			map.put("Light", 1);
			// ��ind_sysparm�����ݻ�ȡapRegion
			TParm res = queryApRegion();
			if (res.getErrCode() < 0) {
				this.messageBox("��ѯסԺҩ������������");
				return;
			}
			String apRegion = res.getValue("AP_REGION", 0);
			if (apRegion == null || apRegion.equals("")) {
				System.out.println("��ǩ����û�����ò��Ŵ���");
			}
			map.put("APRegion", apRegion);
			list.add(map);
		}
		if (list != null && list.size() > 0) {
			try {
				String url = Constant.LABELDATA_URL;
				System.out.println("URL---:" + url);
				LabelControl.getInstance().sendLabelDate(list, url);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("���õ��ӱ�ǩ����ʧ��");
			}
		}
	}

	/**
	 * ��ѯסԺҩ��AP_REGION
	 */
	private TParm queryApRegion() {
		String org_code = this.getValueString("ORG_CODE");
		String sql = "SELECT AP_REGION FROM IND_LABEL WHERE LABEL_ID='"
				+ org_code + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
