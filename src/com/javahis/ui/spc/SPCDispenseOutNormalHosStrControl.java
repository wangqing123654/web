package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.label.Constant;
import jdo.spc.SPCDispenseOutAssginNormalTool;
import jdo.spc.SPCSQL;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:סԺҩ����ҩ-�Ǿ����¼�control
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ-�Ǿ����¼�control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liyh 20121110
 * @version 1.0
 */
public class SPCDispenseOutNormalHosStrControl extends TControl {
	TTable table_N;
	TTable table_Y;

	TPanel N_PANEL;
	TPanel Y_PANEL;

	/** ��ʼʱ�� */
	TTextFormat start_date;

	/** ����ʱ�� */
	TTextFormat end_date;

	/** ���� */
	// TTextFormat station_id;

	/** ͳҩ��table */
	TTable table_order;

	/** ͳҩ���ſؼ� */
	TTextField order_id;

	/** ͳҩ���� ��ѯ�ؼ� */
	TTextField order_id_query;

	String boxEslId;// ��ת��
	TParm parm1 = new TParm();
	int k = 0;

	/** ��ʼ������ */
	public void onInit() {
		super.init();
		table_N = this.getTable("TABLE_N");
		table_Y = this.getTable("TABLE_Y");
		N_PANEL = (TPanel) getComponent("PANEL_N");
		Y_PANEL = (TPanel) getComponent("PANEL_Y");

		start_date = (TTextFormat) getComponent("START_DATE");
		end_date = (TTextFormat) getComponent("END_DATE");
		// station_id = (TTextFormat) getComponent("STATION_ID");
		table_order = this.getTable("TABLE_ORDER");
		order_id = (TTextField) getComponent("INTGMED_NO");
		order_id_query = (TTextField) getComponent("INTGMED_NO_QUERY");

		// ��ʼ����ѯ����
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				'-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		onQuery();
	}

	/** ��ѯ���� */
	public void onQuery() {
		TParm parm = new TParm();

		parm.setData("START_DATE", start_date.getValue());
		parm.setData("END_DATE", end_date.getValue());
		// parm.setData("STATION_ID", start_date.getValue());
		parm.setData("STATION_ID", this.getValueString("STATION_ID"));
		parm.setData("INTGMED_NO", order_id_query.getValue());

		TParm result = SPCDispenseOutAssginNormalTool.getInstance().query(parm);
		table_order.setParmValue(result);
	}

	/** table_order �����¼� */
	public void tableOrderClicked() {
		int row_m = table_order.getSelectedRow();
		if (row_m != -1) {
			order_id.setValue((String) table_order.getItemData(row_m,
					"INTGMED_NO"));
			onIntgmEdNo();
		}

	}

	/**
	 * ͳҩ���Ļس��¼�
	 * */
	public void onIntgmEdNo() {
		String intgmedNo = (String) getValue("INTGMED_NO");// ͳҩ����
		if (StringUtils.isNotBlank(intgmedNo)) {
			TParm parm = new TParm();
			if (N_PANEL.isShowing()) {
				// System.out
				// .println("------nnn-------sql : "
				// + SPCSQL
				// .getStaOrderCodeNormalNoRebinedNoActIng(intgmedNo));
				parm = new TParm(
						TJDODBTool
								.getInstance()
								.select(
										SPCSQL
												.getStaOrderCodeNormalNoRebinedNoActIng(intgmedNo)));
				table_N.setParmValue(parm);
			} else if (Y_PANEL.isShowing()) {
				// System.out
				// .println("-------yyy------sql : "
				// + SPCSQL
				// .getStaOrderCodeNormalNoRebinedNoActEd(intgmedNo));
				parm = new TParm(
						TJDODBTool
								.getInstance()
								.select(
										SPCSQL
												.getStaOrderCodeNormalNoRebinedNoActEd(intgmedNo)));
				table_Y.setParmValue(parm);
			}
			// System.out.println("-----------parm : " + parm);
			if (null != parm && parm.getCount() > 0) {
				String orgChnDesc = (String) parm.getData("STATION_DESC", 0);
				setValue("STATION_DESC", orgChnDesc);
				this.getTextField("STATION_DESC").setEditable(false);
				this.getTextField("INTGMED_NO").setEditable(false);
				this.getTextField("ELETAG_CODE").grabFocus();
			}
		}
	}

	/** panel �����¼� */
	public void onTPanlClicked() {
		onIntgmEdNo();
	}

	public void onAgain() {
		this.clearValue("BOX_ESL_ID");
		this.getTextField("BOX_ESL_ID").grabFocus();
	}

	/**
	 * ���ӱ�ǩ�Ļس��¼�
	 * */
	public void onEleTagCode() {
		N_PANEL = (TPanel) getComponent("PANEL_N");
		if (N_PANEL.isShowing()) {
			String eletagCode = this.getValueString("ELETAG_CODE");
			String intgmedNo = (String) getValue("INTGMED_NO");

			if (StringUtil.isNullString(eletagCode)) {
				return;
			}
			if (StringUtil.isNullString(intgmedNo)) {
				this.messageBox("����ɨ��ͳҩ��");
				setValue("ELETAG_CODE", "");
				this.getTextField("INTGMED_NO").grabFocus();
				return;
			}
			table_N.acceptText();

			String labelNo = getValueString("ELETAG_CODE");
			String orgCode = "";
			String orderCode = "";
			String spec = "";
			TParm parm = table_N.getParmValue();
			int count = table_N.getParmValue().getCount();

			boolean b = false;
			for (int i = 0; i < count; i++) {
				TParm rowParm = parm.getRow(i);
				orgCode = rowParm.getValue("EXEC_DEPT_CODE");
				String eleCode = rowParm.getValue("ELETAG_CODE").toUpperCase();
				if (eletagCode.toUpperCase().equals(eleCode)) {
					TParm updateParm = new TParm();
					orderCode = rowParm.getValue("ORDER_CODE");
					updateParm.setData("INTGMED_NO", intgmedNo);
					updateParm.setData("ORDER_CODE", orderCode);
					// System.out.println("-------150-----uupdate sql :"
					// +SPCSQL.updateStateInTg(intgmedNo, orderCode));
					TParm result = new TParm(TJDODBTool.getInstance().update(
							SPCSQL.updateStateInTg(intgmedNo, orderCode)));
					// System.out.println("---update: " + result);
					table_N.removeRow(i);
					b = true;
					break;
				}
			}

			// �ǵģ����õ��ӱ�ǩ�ӿ���ʾ�������˸
			// System.out.println("-------------bbbb: "+b);
			if (b) {
				TParm inParm = new TParm();
				inParm.setData("ORDER_CODE", orderCode);
				inParm.setData("ORG_CODE", orgCode);
				inParm.setData("ELETAG_CODE", eletagCode);
				TParm outParm = new TParm(TJDODBTool.getInstance().select(
						SPCSQL.getStockQty(inParm)));
				spec = outParm.getValue("SPECIFICATION");
				String orderDesc = outParm.getValue("ORDER_DESC", 0);
				String qty = outParm.getValue("QTY", 0);

				/**
				 * ���õ��ӱ�ǩ
				 */
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("ProductName", orderDesc);
				map.put("SPECIFICATION", spec + " " + qty);
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
				try {
					String url = Constant.LABELDATA_URL;
					LabelControl.getInstance().sendLabelDate(list, url);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("���õ��ӱ�ǩ����ʧ��");
				}

			}
			this.setValue("ELETAG_CODE", "");
			return;
		}
	}

	public void onChange() {
		onQuery();
	}

	/**
	 * ��ղ���
	 * */
	public void onClear() {
		String controlName = "INTGMED_NO;STATION_DESC;ELETAG_CODE";
		this.clearValue(controlName);

		this.getTextField("INTGMED_NO").setEditable(true);
		this.getTextField("ELETAG_CODE").setEditable(true);
		this.getTextField("INTGMED_NO").grabFocus();
		table_N.removeRowAll();
		table_Y.removeRowAll();
	}

	/**
	 * ȷ��ͳҩ��� author shendr 2013.07.04
	 */
	public void onSave() {
		// ͳҩ����
		String intgmedNo = (String) getValue("INTGMED_NO");
		String eletagCode = "";
		String orgCode = "";
		String orderCode = "";
		String spec = "";
		// ��ȡδȡҩTABLE��parm
		TParm parm = table_N.getParmValue();
		// ��ȡ��������
		int count = table_N.getParmValue().getCount();
		/**
		 * ���õ��ӱ�ǩ
		 */
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// ���δȡҩPanel
		for (int i = 0; i < count; i++) {
			// �Ķ�Ӧ��IS_INTGֵΪY
			TParm rowParm = parm.getRow(i);
			orderCode = rowParm.getValue("ORDER_CODE");
			eletagCode = rowParm.getValue("ELETAG_CODE");
			orgCode = rowParm.getValue("EXEC_DEPT_CODE");
			TJDODBTool.getInstance().update(
					SPCSQL.updateStateInTg(intgmedNo, orderCode));
			// ���õ��ӱ�ǩ�ӿ���ʾ�������˸
			TParm inParm = new TParm();
			inParm.setData("ORDER_CODE", orderCode);
			inParm.setData("ORG_CODE", orgCode);
			TParm outParm = new TParm(TJDODBTool.getInstance().select(
					SPCSQL.getQty(inParm)));
			String orderDesc = outParm.getValue("ORDER_DESC", 0);
			spec = outParm.getValue("SPECIFICATION");
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
		}
		if (list != null && list.size() > 0) {
			try {
				String url = Constant.LABELDATA_URL;
				LabelControl.getInstance().sendLabelDate(list, url);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("���õ��ӱ�ǩ����ʧ��");
			}
		}
		table_N.removeRowAll();
	}

	/**
	 * ��ѯסԺҩ��AP_REGION
	 */
	private TParm queryApRegion() {
		String sql = "SELECT AP_REGION FROM IND_SYSPARM ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) this.getComponent(tagName);
	}
}
