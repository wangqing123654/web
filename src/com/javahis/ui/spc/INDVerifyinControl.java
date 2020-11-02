package com.javahis.ui.spc;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndAgentTool;
import jdo.spc.IndPurorderMTool;
import jdo.spc.IndStockDTool;
import jdo.spc.IndSysParmTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
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
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndVerifyinObserver;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import com.dongyang.manager.TIOM_Database;

/**
 * <p>  
 * Title: �������Control 
 * </p>
 * 
 * <p>
 * Description: �������Control
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
 * @author liyh 2009.05.14
 * @version 1.0
 */

public class INDVerifyinControl extends TControl {

	// �������
	private String action = "insert";
	// ϸ�����
	private int seq;

	private TParm resultParm;

	private Map map;

	// ����Ȩ��
	private boolean gift_flg = true;

	// ȫ������Ȩ��
	private boolean dept_flg = true;

	// ������Ȩ��
	private boolean check_flg = true;

	// ��ӡ�����ʽ
	java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
	java.text.DecimalFormat df3 = new java.text.DecimalFormat("##########0.000");
	java.text.DecimalFormat df4 = new java.text.DecimalFormat("##########0.0000");
	// luhai 2012-2-28 ����һλ�ĸ�ʽ��
	java.text.DecimalFormat df5 = new java.text.DecimalFormat("##########0");

	public INDVerifyinControl() {
		super();
	}

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
		// this.messageBox(getValueString("SUP_CODE"));
		if (!"".equals(getValueString("SUP_CODE"))) {
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		}
		if (!"".equals(getValueString("VERIFYIN_NO"))) {
			parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
		}
		if (!"".equals(getValueString("START_DATE")) && !"".equals(getValueString("END_DATE"))) {
			parm.setData("START_DATE", getValue("START_DATE"));
			parm.setData("END_DATE", getValue("END_DATE"));
		}
		String drugSql = " ";
		if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
			parm.setData("DRUG_CATEGORY", "2");
			drugSql = " AND DRUG_CATEGORY='2' ";
		} else {
			drugSql = " AND DRUG_CATEGORY='1' ";
			parm.setData("DRUG_CATEGORY", "1");
		}
		String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		if (!"".equals(startDate) && startDate.length()>10) {
			startDate = startDate.substring(0, 10) + " 00:00:00";
		}
		if (!"".equals(endDate) && endDate.length()>10) {
			endDate = endDate.substring(0, 10) + " 23:59:59";
		}
		// zhangyong20110517
		parm.setData("REGION_CODE", Operator.getRegion());
		if (parm == null) {
			return;
		}
		// ���ؽ����
		TParm result = new TParm();
//		result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onQueryM", parm);
	    String sql = " SELECT VERIFYIN_NO, VERIFYIN_DATE, VERIFYIN_USER, ORG_CODE, SUP_CODE,  CHECK_USER, " +
	    			 " CHECK_DATE, DESCRIPTION, REASON_CHN_DESC, BILL_DATE, BILLPRINT_FLG, " +
	    			 " PLAN_NO,OPT_USER, OPT_DATE, OPT_TERM, DRUG_CATEGORY " +
	    			 " FROM IND_VERIFYINM A " +
	    			 " WHERE REGION_CODE='" + Operator.getRegion() + "'  " + drugSql +
	    			 " AND VERIFYIN_DATE BETWEEN TO_DATE('" + startDate + "','yyyy-MM-dd hh24:mi:ss') " + 
	    			 " AND TO_DATE('" + endDate + "','yyyy-MM-dd hh24:mi:ss') "
	    			 ;
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {// �����
			if (getRadioButton("TOXIC_DRUG").isSelected()) {//�龫
				sql += "  AND  A.CHECK_USER IS NOT NULL ";
			}else {//���龫
				sql += "  AND  A.VERIFYIN_NO IN ( SELECT B.VERIFYIN_NO FROM IND_VERIFYIND B WHERE B.PUTAWAY_DATE IS NOT  NULL )";
			}
		} else {//δ���
			if (getRadioButton("TOXIC_DRUG").isSelected()) {
				sql += "  AND  A.CHECK_USER IS  NULL ";
			}else {
				sql += "  AND  A.VERIFYIN_NO IN ( SELECT B.VERIFYIN_NO FROM IND_VERIFYIND B WHERE B.PUTAWAY_DATE IS   NULL )";
			}
			
		}
//		System.out.println("-------sql : "+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result == null || result.getCount("VERIFYIN_NO") <= 0) {
			getTable("TABLE_M").removeRowAll();
			getTable("TABLE_D").removeRowAll();
			this.messageBox("û�в�ѯ����");
			return;
		}
		// �����TABLE_M
		TTable table_M = getTable("TABLE_M");
		table_M.setParmValue(result);
		((TMenuItem) getComponent("save")).setEnabled(true);		
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		getRadioButton("UPDATE_FLG_B").setSelected(true);
		String clearString = "START_DATE;END_DATE;VERIFYIN_DATE;ORG_CODE;VERIFYIN_NO;" + "PLAN_NO;REASON_CHN_DESC;CHECK_FLG;DESCRIPTION;"
				+ "SELECT_ALL;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
		this.clearValue(clearString);
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("VERIFYIN_DATE", date);
		// ����״̬
		((TMenuItem) getComponent("save")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
//		((TMenuItem) getComponent("export")).setEnabled(true);
		getComboBox("ORG_CODE").setEnabled(true);
		getTextFormat("SUP_CODE").setEnabled(true);
		// getCheckBox("CHECK_FLG").setEnabled(false);
		getTextField("VERIFYIN_NO").setEnabled(true);
		getTable("TABLE_M").setSelectionMode(0);
		getTable("TABLE_M").removeRowAll();
		getTable("TABLE_D").setSelectionMode(0);
		getTable("TABLE_D").removeRowAll();
		action = "insert";
		this.setCheckFlgStatus(action);
		resultParm = null;
		TTable table_D = getTable("TABLE_D");
		TDS tds = new TDS();
		tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
		tds.retrieve();
		// �۲���
		IndVerifyinObserver obser = new IndVerifyinObserver();
		tds.addObserver(obser);
		table_D.setDataStore(tds);
		table_D.setDSValue();
		getTextFormat("SUP_CODE").setText("");
		getTextFormat("SUP_CODE").setValue("");
		getRadioButton("GEN_DRUG").setEnabled(true);
		getRadioButton("TOXIC_DRUG").setEnabled(true);
//		((TMenuItem) getComponent("export")).setEnabled(true);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		map = new HashMap();
		// ���������
		TParm parm = new TParm();
		// ���ⵥ��
		String dispenseNo = "";
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DRUG_CATEGORY", "1");
		} else {// �龫
			parm.setData("DRUG_CATEGORY", "2");
		}
		// ���ؽ����
		TParm result = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		if ("insert".equals(action)) {
			if (!CheckDataD()) {
				return;
			}
			// ������Ϣ
			// ��������
			parm.setData("VERIFYIN_DATE", getValue("VERIFYIN_DATE"));
			// ������Ա
			parm.setData("VERIFYIN_USER", Operator.getID());
			// ���ղ���
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
			// ��Ӧ����
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			// ��ע
			parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
			// �ݻ�ԭ��
			parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
			// �ƻ�����
			parm.setData("PLAN_NO", getValueString("PLAN_NO"));
			// OPT_USER,OPT_DATE,OPT_TERM
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", date);
			parm.setData("OPT_TERM", Operator.getIP());
//			parm.setData("SPC_BOX_BARCODE", getValueString("SPC_BOX_BARCODE"));
			parm.setData("SPC_BOX_BARCODE", "0");
			// zhangyong20110517
			parm.setData("REGION_CODE", Operator.getRegion());
			// ���
			boolean check_flg = false;
			if ("Y".equals(getValueString("CHECK_FLG"))) {
				// �����Ա
				parm.setData("CHECK_USER", Operator.getID());
				// ���ʱ��
				parm.setData("CHECK_DATE", date);
				check_flg = true;
			} else {
				parm.setData("CHECK_USER", "");
				parm.setData("CHECK_DATE", tnull);
				check_flg = false;
			}
			// ϸ����
			TTable table_D = getTable("TABLE_D");
			table_D.acceptText();
			TDataStore dataStore = table_D.getDataStore();

			// ����Ƿ���춯״̬�ж�
			String org_code = parm.getValue("ORG_CODE");
			if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
				this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
				return;
			}

			// ϸ����Ϣ
			// ������ⵥ��
			String verifyin_no = "";
			// ����������
			int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
			// ������
			double qty = 0.00;
			// ������
			double g_qty = 0.00;
			// ������
			double pur_qty = 0.00;
			// �����
			double stock_qty = 0.00;
			// ״̬
			String update_flg = "0";
			// ҩƷ����
			String order_code = "";
			// ���������ת����
			TParm getTRA = new TParm();
			// �����Ч��������
			TParm getSEQ = new TParm();
			// ����ת����
			double s_qty = 0.00;
			// ���ת����
			double d_qty = 0.00;
			// ����
			String batch_no = "";
			// �������
			int batch_seq = 1;
			// ��Ч��
			String valid_date = "";
			// ���»�����IND_STOCK��FLG->U:����;I,����
			String flg = "";
			// ���ۼ۸�
			double retail = 0.00;
			// ���յ���
			double verprice = 0.00;
			// �ۼ������ۿ���
			double deduct_atm = 0.00;
			// ���ƽ����
			double stock_price = 0.00;
			// ��������
			String pur_no = "";
			// �����������
			int pur_no_seq = 0;
			// �õ�IND_SYSPARM��Ϣ
			result = IndSysParmTool.getInstance().onQuery();
			//��ת��
			String spcBoxCode = "";
			// ���뵥���Զ�ά��
			boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
			if (reuprice_flg) {
				// �ж����յ����Ƿ����Լ������ͬ
				boolean agent_flg = false;
				for (int i = 0; i < newrows.length; i++) {
					// �жϸ��е�����״̬
					if (!dataStore.isActive(newrows[i])) {
						continue;
					}
					order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
					verprice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE");
					String sup_code = this.getValueString("SUP_CODE");
					TParm inparm = new TParm();
					inparm.setData("SUP_CODE", sup_code);
					inparm.setData("ORDER_CODE", order_code);
					TParm agent_parm = IndAgentTool.getInstance().onQuery(inparm);
					if (agent_parm.getDouble("CONTRACT_PRICE", 0) != verprice) {
						agent_flg = true;
					}
				}
				if (agent_flg) {
					if (this.messageBox("��ʾ", "�Ƿ��Զ�ά�����뵥��", 2) == 0) {
						parm.setData("REUPRICE_FLG", "Y");
					} else {
						parm.setData("REUPRICE_FLG", "N");
					}
				} else {
					parm.setData("REUPRICE_FLG", "N");
				}
			} else {
				parm.setData("REUPRICE_FLG", "N");
			}

			// �ж��Ƿ��Զ������һ��������ⵥ��ά����ҩƷ��������������
			parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");

			// ϸ����Ϣ
			int count = 0;
			// ������ⵥ��
			verifyin_no = SystemTool.getInstance().getNo("ALL", "IND", "IND_VERIFYIN", "No");
			seq = 1;
			String material_loc_code = "";
			for (int i = 0; i < newrows.length; i++) {
				// �жϸ��е�����״̬
				if (!dataStore.isActive(newrows[i])) {
					continue;
				}
				// // ���ݲ�ͬ�ķ�Ʊ�����ɲ�ͬ����ⵥ��
				// verifyin_no =
				// getVerifyinNO(dataStore.getItemString(newrows[i],
				// "INVOICE_NO"));
				// ��ⵥ�ż���
				parm.setData("VERIFYIN", count, verifyin_no);
				// ҩƷ���뼯��
				//this.messageBox(dataStore.getItemString(newrows[i], "BATCH_NO"));	
				String prc = resultParm.getValue("PRC",i);		
				String sup_order_code=dataStore.getItemString(newrows[i], "SUP_ORDER_CODE");
				parm.setData("SUP_ORDER_CODE", count, sup_order_code);
				order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
				parm.setData("ORDER_CODE", count, order_code);
				TParm supParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderCodeBySup(getValueString("SUP_CODE"), sup_order_code)));
				if(supParm.getCount()<=0 ||supParm.getErrCode()<0) {
			  
				}
				int conversionTraio = supParm.getInt("CONVERSION_RATIO", 0);
				conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
				double prc1 = Double.valueOf(prc)/conversionTraio;
				parm.setData("PRC", count, prc1);					
				// ���������ת����
				getTRA = INDTool.getInstance().getTransunitByCode(order_code);
				if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
					this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
					return;
				}
				// ����ת���ʼ���
				s_qty = getTRA.getDouble("STOCK_QTY", 0);
				parm.setData("STOCK_QTY", count, s_qty);
				// ���ת���ʼ���
				d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
				parm.setData("DOSAGE_QTY", count, d_qty);
				// �����
				stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
				parm.setData("QTY", count, stock_qty);
				// ���ż���
				batch_no = dataStore.getItemString(newrows[i], "BATCH_NO");
				parm.setData("BATCH_NO", count, batch_no);
				// ��Ч��
				valid_date = StringTool.getString(dataStore.getItemTimestamp(newrows[i], "VALID_DATE"), "yyyy/MM/dd");
				parm.setData("VALID_DATE", count, StringTool.getTimestamp(valid_date, "yyyy/MM/dd"));
				// �����Ч��������
				getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date);
				if (getSEQ.getErrCode() < 0) {
					this.messageBox("ҩƷ" + order_code + "������Ŵ���");
					return;
				}
				if (getSEQ.getCount("BATCH_SEQ") > 0) {
					flg = "U";
					// ������ҩƷ����
					batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
				} else {
					flg = "I";
					// ������ҩƷ������,ץȡ�ÿ��ҩƷ���+1�����������
					getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
					if (getSEQ.getErrCode() < 0) {
						this.messageBox("ҩƷ" + order_code + "������Ŵ���");
						return;
					}
					// ���+1�������
					batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
					material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
					// this.messageBox(material_loc_code);
				}
				// �����Ч�������߼���
				parm.setData("BATCH_SEQ", count, batch_seq);
				// ��λ
				parm.setData("MATERIAL_LOC_CODE", count, material_loc_code);
				// ���»�����IND_STOCK��FLG����
				parm.setData("UI_FLG", count, flg);
				// ����������
				qty = dataStore.getItemDouble(newrows[i], "VERIFYIN_QTY");
				parm.setData("VERIFYIN_QTY", count, qty);
				// ����������
				g_qty = dataStore.getItemDouble(newrows[i], "GIFT_QTY");
				parm.setData("GIFT_QTY", count, g_qty);
				// �ƻ�������
				pur_qty = resultParm.getDouble("PURORDER_QTY", newrows[i]);
				parm.setData("PURORDER_QTY", count, pur_qty);
				// ���ۼ۸񼯺�
				retail = dataStore.getItemDouble(newrows[i], "RETAIL_PRICE");
				parm.setData("RETAIL_PRICE", count, retail);
				// ���ռ۸񼯺�
				// zhangyong20091014 begin �޸����ռ۸�ĵ�λΪ��ҩ��λ
				verprice = dataStore.getItemDouble(newrows[i], "VERIFYIN_PRICE") / d_qty;
//				System.out.println(order_code+ "--------verprice:"+verprice);
				verprice = StringTool.round(verprice, 4);
				parm.setData("VERIFYIN_PRICE", count, StringTool.round(verprice, 4));
				// zhangyong20091014 end
				// ������ż���
				parm.setData("SEQ_NO", count, seq + count);
				// �ۼ������ۿ����
				deduct_atm = dataStore.getItemDouble(newrows[i], "QUALITY_DEDUCT_AMT");
				parm.setData("QUALITY_DEDUCT_AMT", count, deduct_atm);
				// �������ż���
				pur_no = dataStore.getItemString(newrows[i], "PURORDER_NO");
				parm.setData("PURORDER_NO", count, pur_no);
				// ����������ż���
				pur_no_seq = dataStore.getItemInt(newrows[i], "PURSEQ_NO");
				parm.setData("PURSEQ_NO", count, pur_no_seq);
				// ���ƽ���ۼ���
				stock_price = resultParm.getDouble("STOCK_PRICE", newrows[i]);
				parm.setData("STOCK_PRICE", count, stock_price);
				// �ۼ�������
				parm.setData("ACTUAL_QTY", count, resultParm.getDouble("ACTUAL_QTY", newrows[i]));

				// ���յ���
				dataStore.setItem(newrows[i], "VERIFYIN_NO", verifyin_no);
				// ���
				dataStore.setItem(newrows[i], "SEQ_NO", seq + count);

				// ״̬
				if (check_flg && qty == pur_qty) {
					update_flg = "3";
				} else if (check_flg && qty < pur_qty) {
					update_flg = "1";
				} else if (!check_flg) {
					update_flg = "0";
				}
				dataStore.setItem(newrows[i], "UPDATE_FLG", update_flg);
				// System.out.println("I_UPDATE_FLG:" + update_flg);
				// OPT_USER,OPT_DATE,OPT_TERM
				dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(newrows[i], "OPT_DATE", date);
				dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
				dataStore.setItem(newrows[i], "BATCH_SEQ", batch_seq);// add by
																		// liyh
																		// 20120613
				//by liyh 20121128 ��ת��
				spcBoxCode = dataStore.getItemString(newrows[i], "SPC_BOX_BARCODE");
//				System.out.println("---------spcBoxCode: "+spcBoxCode);
				parm.setData("SPC_BOX_BARCODE", count,spcBoxCode);
				dataStore.setItem(newrows[i], "SPC_BOX_BARCODE", spcBoxCode);
				count++;
			}
			// �õ�dataStore�ĸ���SQL
			String updateSql[] = dataStore.getUpdateSQL();
		
			 for (int i = 0; i < updateSql.length; i++) { 
				 System.out.println(i + "::::::" + updateSql[i]); 
			}
			
			parm.setData("UPDATE_SQL", updateSql);

			// ִ����������(�������������ϸ��)
			result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onInsert", parm);

			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");

			this.setValue("VERIFYIN_NO", verifyin_no);

			// �������յ����ж�����������������״̬
			updateIndVerifinDUpdateFlg(verifyin_no);

		} else if ("updateM".equals(action)) {
			if (getRadioButton("GEN_DRUG").isSelected()) {//��ҩ�����
				return;
			}
			// ����������Ϣ
			if (!CheckDataM()) {
				return;
			}
			// ����ֵȡ��
			parm.setData("ORG_CODE", getValueString("ORG_CODE"));
			parm.setData("SUP_CODE", getValueString("SUP_CODE"));
			parm.setData("PLAN_NO", getValue("PLAN_NO"));
			parm.setData("REASON_CHN_DESC", getValueString("REASON_CODE"));
			parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
			// System.out.println("------------------------");
			String check_flg = getValueString("CHECK_FLG");
			//�龫��ⲿ��
			String drugToOrgCode = getValueString("DRUG_TO_ORG_CODE");
			// �õ�ҩ�Ⲧ������
			TParm sysParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
			//ϵͳ�������Ƿ��Զ��������쵥 1:�Զ���0��
			String isAtuoDrug = sysParm.getValue("IS_AUTO_DRUG", 0);			
			if ("Y".equals(check_flg)) {
			    if (StringUtils.isEmpty(drugToOrgCode) && drugToOrgCode.length()<1 && "1".equals(isAtuoDrug)) {
					this.messageBox("��ѡ���龫��ⲿ��");
					return;
				}
				// �����Ա
				parm.setData("CHECK_USER", Operator.getID());
				// ���ʱ��
				parm.setData("CHECK_DATE", date);
				// ϸ����
				TTable table_D = getTable("TABLE_D");
				table_D.acceptText();
				TDataStore dataStore = table_D.getDataStore();

				// ����Ƿ���춯״̬�ж�
				String org_code = parm.getValue("ORG_CODE");
				if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
					this.messageBox("ҩ�����ι���������ʾ�����ֶ����ս�");
					return;
				}

				// ������
				double qty = 0.00;
				// ������
				double g_qty = 0.00;
				// ������
				double pur_qty = 0.00;
				// �����
				double stock_qty = 0.00;
				// ҩƷ����
				String order_code = "";
				// ���������ת����
				TParm getTRA = new TParm();
				// �����Ч��������
				TParm getSEQ = new TParm();
				// ����ת����
				double s_qty = 0.00;
				// ���ת����
				double d_qty = 0.00;
				// ����
				String batch_no = "";
				// �������
				int batch_seq = 1;
				// ��Ч��
				String valid_date = "";
				// ���»�����IND_STOCK��FLG->U:����;I,����
				String flg = "";
				// ���ۼ۸�
				double retail = 0.00;
				// ���յ���
				double verprice = 0.00;
				// �ۼ������ۿ���
				double deduct_atm = 0.00;
				// ���ƽ����
				double stock_price = 0.00;
				// ��������
				String pur_no = "";
				// �����������
				int pur_no_seq = 0;
				//��ת��
				String spcBoxCode = "";
				String supOrderCode = "";
				String prc = "";
				String verifyin_no = this.getValueString("VERIFYIN_NO");

				// �õ�IND_SYSPARM��Ϣ
				result = IndSysParmTool.getInstance().onQuery();
				// ���뵥���Զ�ά��
				boolean reuprice_flg = result.getBoolean("REUPRICE_FLG", 0);
				if (reuprice_flg) {
					// �ж����յ����Ƿ����Լ������ͬ
					boolean agent_flg = false;
					for (int i = 0; i < table_D.getRowCount(); i++) {
						// �жϸ��е�����״̬
						if (!dataStore.isActive(i)) {
							continue;
						}
						order_code = dataStore.getItemString(i, "ORDER_CODE");
						verprice = dataStore.getItemDouble(i, "VERIFYIN_PRICE");
						String sup_code = this.getValueString("SUP_CODE");
						TParm inparm = new TParm();
						inparm.setData("SUP_CODE", sup_code);
						inparm.setData("ORDER_CODE", order_code);
						TParm agent_parm = IndAgentTool.getInstance().onQuery(inparm);
						if (agent_parm.getDouble("CONTRACT_PRICE", 0) != verprice) {
							agent_flg = true;
						}
					}
					if (agent_flg) {
						if (this.messageBox("��ʾ", "�Ƿ��Զ�ά�����뵥��", 2) == 0) {
							parm.setData("REUPRICE_FLG", "Y");
						} else {
							parm.setData("REUPRICE_FLG", "N");
						}
					} else {
						parm.setData("REUPRICE_FLG", "N");
					}
				} else {
					parm.setData("REUPRICE_FLG", "N");
				}

				// �ж��Ƿ��Զ������һ��������ⵥ��ά����ҩƷ��������������
				parm.setData("UPDATE_TRADE_PRICE", reuprice_flg ? "Y" : "N");

				List<String> list = new ArrayList<String>();
				// ϸ����Ϣ
				String material_loc_code = "";
				for (int i = 0; i < table_D.getRowCount(); i++) {
					// ��ⵥ�ż���
					parm.setData("VERIFYIN", i, verifyin_no);
					// ҩƷ���뼯��
					order_code = dataStore.getItemString(i, "ORDER_CODE");
					parm.setData("ORDER_CODE", i, order_code);
					// ���������ת����
					getTRA = INDTool.getInstance().getTransunitByCode(order_code);
					if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
						this.messageBox("ҩƷ" + order_code + "ת���ʴ���");
						return;
					}
					// ����ת���ʼ���
					s_qty = getTRA.getDouble("STOCK_QTY", 0);
					parm.setData("STOCK_QTY", i, s_qty);
					// ���ת���ʼ���
					d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
					parm.setData("DOSAGE_QTY", i, d_qty);
					// �����
					stock_qty = INDTool.getInstance().getStockQTY(org_code, order_code);
					parm.setData("QTY", i, stock_qty);
					// ���ż���
					batch_no = dataStore.getItemString(i, "BATCH_NO");
					parm.setData("BATCH_NO", i, batch_no);
					// ��Ч��
					valid_date = StringTool.getString(dataStore.getItemTimestamp(i, "VALID_DATE"), "yyyy/MM/dd");
					// System.out.println("0000" + valid_date);
					parm.setData("VALID_DATE", i, valid_date);
					//20121128 liyh�����ת��
					spcBoxCode = dataStore.getItemString(i, "SPC_BOX_BARCODE");
					parm.setData("SPC_BOX_BARCODE", i, spcBoxCode);
					// *************************************************************
					// luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����begin
					// *************************************************************
					// �����Ч��������
					// getSEQ =
					// IndStockDTool.getInstance().onQueryStockBatchSeq(
					// org_code, order_code, batch_no, valid_date);
					// add by liyh 20120614 ��IND_STOCK
					// ���ѯҩƷ���ռ۸�ʱ��Ӧ����Ƭ/֧�ļ۸���С��λ�ļ۸� start
					double verpriceD = dataStore.getItemDouble(i, "VERIFYIN_PRICE") / d_qty;
					verpriceD = StringTool.round(verpriceD, 4);
					String verpriceDStr = String.valueOf(verpriceD);
					// String verifyInPrice = dataStore.getItemString(i,
					// "VERIFYIN_PRICE");
					// add by liyh 20120614 ��IND_STOCK
					// ���ѯҩƷ���ռ۸�ʱ��Ӧ����Ƭ/֧�ļ۸���С��λ�ļ۸� end
					// �����Ч��������
					getSEQ = IndStockDTool.getInstance().onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date, verpriceDStr);//
					// *************************************************************
					// luahi 2012-01-10 add �����������ѡȡ�����������ռ۸�����end
					// *************************************************************
					if (getSEQ.getErrCode() < 0) {
						this.messageBox("ҩƷ" + order_code + "������Ŵ���");
						return;
					}
					if (getSEQ.getCount("BATCH_SEQ") > 0) {
						flg = "U";
						// ������ҩƷ����
						batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
					} else {
						flg = "I";
						// ������ҩƷ������,ץȡ�ÿ��ҩƷ���+1�����������
						getSEQ = IndStockDTool.getInstance().onQueryStockMaxBatchSeq(org_code, order_code);
						if (getSEQ.getErrCode() < 0) {
							this.messageBox("ҩƷ" + order_code + "������Ŵ���");
							return;
						}
						// ���+1�������
						// System.out.println("SEQ:"
						// + getSEQ.getInt("BATCH_SEQ", 0));
						// System.out.println("getSEQ:" + getSEQ);
						batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
						material_loc_code = getSEQ.getValue("MATERIAL_LOC_CODE", 0);
					}
					String listsString = order_code+batch_seq;
//					System.out.println(i+"---"+listsString);
					if(list.contains(listsString)){
						int size = list.size();
						batch_seq++;
						batch_seq +=size;
						listsString = order_code+batch_seq;
						list.add(listsString);
					}else {
						list.add(listsString);
					}
				
					// �����Ч�������߼���
					parm.setData("BATCH_SEQ", i, batch_seq);
					// ��λ
					parm.setData("MATERIAL_LOC_CODE", i, material_loc_code);
					// ���»�����IND_STOCK��FLG����
					parm.setData("UI_FLG", i, flg);
					// ����������
					qty = dataStore.getItemDouble(i, "VERIFYIN_QTY");
					parm.setData("VERIFYIN_QTY", i, qty);
					// ����������
					g_qty = dataStore.getItemDouble(i, "GIFT_QTY");
					parm.setData("GIFT_QTY", i, g_qty);

					// ���ۼ۸񼯺�
					retail = dataStore.getItemDouble(i, "RETAIL_PRICE");
					parm.setData("RETAIL_PRICE", i, retail);
					// ���ռ۸񼯺�
					// zhangyong20091014 begin �޸����ռ۸�ĵ�λΪ��ҩ��λ
					verprice = dataStore.getItemDouble(i, "VERIFYIN_PRICE") / d_qty;
					parm.setData("VERIFYIN_PRICE", i, StringTool.round(verprice, 4));
					//��ӿ��۸� ���浽Ind_stock��
//					System.out.println("----111-----VERIFYIN_PRICE:"+dataStore.getItemDouble(i, "VERIFYIN_PRICE"));
					parm.setData("INVENT_PRICE", i, StringTool.round(dataStore.getItemDouble(i, "VERIFYIN_PRICE"), 2));
					// zhangyong20091014 end
					// ������ż���
					int seq_no = dataStore.getItemInt(i, "SEQ_NO");
					parm.setData("SEQ_NO", i, seq_no);
					// �ۼ������ۿ����
					deduct_atm = dataStore.getItemDouble(i, "QUALITY_DEDUCT_AMT");
					parm.setData("QUALITY_DEDUCT_AMT", i, deduct_atm);
					// �������ż���
					pur_no = dataStore.getItemString(i, "PURORDER_NO");
					parm.setData("PURORDER_NO", i, pur_no);
					// ����������ż���
					pur_no_seq = dataStore.getItemInt(i, "PURSEQ_NO");
					parm.setData("PURSEQ_NO", i, pur_no_seq);
					
					supOrderCode =  dataStore.getItemString(i, "SUP_ORDER_CODE");
					

/*					TParm inparm = new TParm();
					inparm.setData("PURORDER_NO", pur_no);
					inparm.setData("SEQ_NO", pur_no_seq);
					result = IndPurorderMTool.getInstance().onQueryDone(inparm);
					if (result.getCount() == 0 || result.getErrCode() < 0) {
						return;
					}*/
					// ����������
					pur_qty = result.getDouble("PURORDER_QTY", 0);
					parm.setData("PURORDER_QTY", i, pur_qty);
					// ���ƽ���ۼ���
					stock_price = result.getDouble("STOCK_PRICE", 0);
					parm.setData("STOCK_PRICE", i, stock_price);
					// �ۼ�������
					parm.setData("ACTUAL_QTY", i, result.getDouble("ACTUAL_QTY", i));
					// ״̬
					String update_flg = "0";
					if ("Y".equals(check_flg) && qty == pur_qty) {
						update_flg = "3";
					} else if ("Y".equals(check_flg) && qty < pur_qty) {
						update_flg = "1";
					} else if (!"Y".equals(check_flg)) {
						update_flg = "0";
					}
					// System.out.println("U_UPDATE_FLG:" + update_flg);
					dataStore.setItem(i, "UPDATE_FLG", update_flg);
					// OPT_USER,OPT_DATE,OPT_TERM
					dataStore.setItem(i, "OPT_USER", Operator.getID());
					dataStore.setItem(i, "OPT_DATE", date);
					dataStore.setItem(i, "OPT_TERM", Operator.getIP());
					// luhai 2012-01-10 modify ����������ʱ��¼BATCH_SEQ begin
					dataStore.setItem(i, "BATCH_SEQ", batch_seq);
					// luhai 2012-01-10 modify ����������ʱ��¼BATCH_SEQ end
				}
				parm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
			} else {
				parm.setData("CHECK_USER", "");
				parm.setData("CHECK_DATE", tnull);
			}
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_DATE", date);
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("REGION_CODE", Operator.getRegion());
			parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
			// ִ�����ݸ���
			result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onUpdate", parm);
			// �����ж�
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.messageBox("P0001");
//			System.out.println("-------------isAuto:"+isAtuoDrug);
			if ("2".equals(parm.getValue("DRUG_CATEGORY")) && "Y".equals(getValueString("CHECK_FLG")) && "1".equals(isAtuoDrug)) {// ��������  ��  ���龫  ���Զ��������쵥�ͳ��ⵥ
				dispenseNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_DISPENSE", "No");
				parm.setData("DISPENSE_NO", dispenseNo);
				parm.setData("DRUG_TO_ORG_CODE", drugToOrgCode);
				// ִ�����ݸ���
				result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onSaveReqeustAndDispense", parm);
			}
			// �������յ����ж�����������������״̬
			updateIndVerifinDUpdateFlg(getValueString("VERIFYIN_NO"));
			// ������ҩƷΪ��ҩʱ�ж��Ƿ������������
			updateManCode(getValueString("VERIFYIN_NO"));
			// �ж��в�ҩ����޸����ۼ�
			// updateGrpricePrice(getValueString("VERIFYIN_NO"));

			
			if ("Y".equals(getValueString("CHECK_FLG"))) {// ��������
				// ��ӡ������ⵥ
				this.onPrint();
				if("2".equals(parm.getValue("DRUG_CATEGORY"))){
					this.onPrintDispendseOut(dispenseNo);
				}
			}
		} else if ("updateD".equals(action)) {
			// ������ϸ��Ϣ
			TTable table_D = getTable("TABLE_D");
			table_D.acceptText();
			TDataStore dataStore = table_D.getDataStore();
			// ���resultParm
			TParm inparm = new TParm();
			inparm.setData("PURORDER_NO", dataStore.getItemString(0, "PURORDER_NO"));
			inparm.setData("SEQ_NO", dataStore.getItemInt(0, "PURSEQ_NO"));
			resultParm = IndPurorderMTool.getInstance().onQueryDone(inparm);
			// ϸ����
			// if (!CheckDataD()) {
			// return;
			// }
			// ���ȫ���Ķ����к�
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			// ���̶�����������
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			// ϸ����ж�
			if (!table_D.update()) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			table_D.setDSValue();
			return;
		}
		this.onClear();
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
			this.messageBox("��������ɲ���ɾ��");
		} else {
			if (getTable("TABLE_M").getSelectedRow() > -1) {
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ�����յ�", 2) == 0) {
					TTable table_D = getTable("TABLE_D");
					TParm parm = new TParm();
					// ϸ����Ϣ
					if (table_D.getRowCount() > 0) {
						table_D.removeRowAll();
						String deleteSql[] = table_D.getDataStore().getUpdateSQL();
						parm.setData("DELETE_SQL", deleteSql);
					}
					// ������Ϣ
					parm.setData("VERIFYIN_NO", getValueString("VERIFYIN_NO"));
					TParm result = new TParm();
					result = TIOM_AppServer.executeAction("action.spc.INDVerifyinAction", "onDeleteM", parm);
					// ɾ���ж�
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("ɾ��ʧ��");
						return;
					}
					getTable("TABLE_M").removeRow(getTable("TABLE_M").getSelectedRow());
					this.messageBox("ɾ���ɹ�");
				}
			} else {
				if (this.messageBox("ɾ��", "ȷ���Ƿ�ɾ������ϸ��", 2) == 0) {
					TTable table_D = getTable("TABLE_D");
					int row = table_D.getSelectedRow();
					table_D.removeRow(row);
					// ϸ����ж�
					if (!table_D.update()) {
						messageBox("E0001");
						return;
					}
					messageBox("P0001");
					table_D.setDSValue();
				}
			}
		}
	}

	/**
	 * ��δ������ϸ
	 */
	public void onExport() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		Object result = openDialog("%ROOT%\\config\\spc\\INDUnVerifyin.x", parm);
		if (result != null) {
			TParm addParm = (TParm) result;
			if (addParm == null) {
				return;
			}
			resultParm = (TParm) result;
			// System.out.println("RESULT" + resultParm);
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			TNull tnull = new TNull(Timestamp.class);
			// ��Ӧ����
			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// �ƻ�����
			this.setValue("PLAN_NO", addParm.getValue("PLAN_NO", 0));
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				int row = table.addRow();
				// ���DATESTORE
				// ORDER_CODE
				table.getDataStore().setItem(i, "ORDER_CODE", addParm.getValue("ORDER_CODE", i));
				// ���TABLE_D����
				// ������
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				actual_qty = addParm.getDouble("ACTUAL_QTY", i);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty - actual_qty);
				// ������
				table.setItem(row, "GIFT_QTY", addParm.getData("GIFT_QTY", i));
				// ������λ
				table.setItem(row, "BILL_UNIT", addParm.getValue("BILL_UNIT", i));
				// ���յ���
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
				table.setItem(row, "VERIFYIN_PRICE", puroder_price);
				// �������
				table.setItem(row, "INVOICE_AMT", StringTool.round(puroder_price * (purorder_qty - actual_qty), 2));
				// ���ۼ�
				retail_price = addParm.getDouble("RETAIL_PRICE", i);
				table.setItem(row, "RETAIL_PRICE", retail_price);
				// ��������
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// �����������
				table.setItem(row, "PURSEQ_NO", addParm.getData("SEQ_NO", i));
				// �ۼ�������
				table.setItem(row, "ACTUAL", addParm.getData("ACTUAL_QTY", i));
				// ��Ʊ����
				table.setItem(row, "INVOICE_DATE", tnull);
				// �ж�ҩƷ����:�вݣ��гɣ���ҩ
				TParm pha_parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHAInfoByOrder(addParm.getValue("ORDER_CODE", i))));
				// ����ҩƷ���͸���Ч��
				if ("G".equals(pha_parm.getValue("PHA_TYPE", 0))) {
					table.setItem(row, "VALID_DATE", "9999/12/31");
				} else {
					table.setItem(row, "VALID_DATE", tnull);
				}
				// ��������
				table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			// zhangyong20110517
//			((TMenuItem) getComponent("export")).setEnabled(false);
		}
	}

	/**
	 * �����ҩ������
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpXML() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DROG_TYPE", "N");
		} else {// �龫
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
			FileUtils fileUtils=new  FileUtils();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			FileParseExcel fileParseExcel= new FileParseExcel();
			try {
				addParm = (TParm) fileParseExcel.readXls(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultParm = (TParm) addParm;
//			System.out.println("---------------in   addpamr: "+addParm);
			TTable table = getTable("TABLE_D");
//			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			// ��Ӧ����
			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// �ƻ�����
			this.setValue("PLAN_NO", "");

			getRadioButton("GEN_DRUG").setEnabled(false);
			getRadioButton("TOXIC_DRUG").setEnabled(false);
//			System.out.println("--count: "+addParm.getCount("ORDER_CODE"));
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				int row = table.addRow();

				TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(addParm.getValue("ORDER_CODE", i))));
				// System.out.println("--phaParm:"+phaParm);
				// ���DATESTORE
				resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
				// ORDER_CODE
				table.getDataStore().setItem(i, "ORDER_CODE", addParm.getValue("ORDER_CODE", i));
				// ���TABLE_D����
				// ������
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty);
				// ������
				table.setItem(row, "GIFT_QTY", 0);
				// ������λ
				// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
				// 0));
				table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));
				// ���յ���
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
				table.setItem(row, "VERIFYIN_PRICE", puroder_price);
				// �������
				table.setItem(row, "INVOICE_AMT", StringTool.round(puroder_price * purorder_qty, 2));
				// ���ۼ�
				retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
				table.setItem(row, "RETAIL_PRICE", retail_price);
				// ��������
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// �����������
				table.setItem(row, "PURSEQ_NO", addParm.getData("PURSEQ_NO", i));
				// �ۼ�������
				table.setItem(row, "ACTUAL", 0);

				String time1 = addParm.getData("INVOICE_DATE", i) + "";
				time1 = time1.replaceAll("-", "/");
				// ��Ʊ����
				table.setItem(row, "INVOICE_DATE", time1);
				String validDate = addParm.getData("VALID_DATE", i) + "";
				validDate = validDate.replaceAll("-", "/");
				table.setItem(row, "REASON_CHN_DESC", "VER01");
				// Ч��
				table.setItem(row, "VALID_DATE", validDate);
				// ��������
				table.setItem(row, "MAN_CODE", phaParm.getData("MAN_CODE", 0));
				// ��Ʊ��
				table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
				// ����
				table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
				// װ�䵥��
				String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
				table.setItem(row, "SPC_BOX_BARCODE",boxCode);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			getCheckBox("SELECT_ALL").setSelected(true);
			onCheckSelectAll();
		}
	}
	
	/**
	 * �����ҩ-��Ʊ��Ϣ
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpInvoiceFromXML() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DROG_TYPE", "N");
		} else {// �龫
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
				addParm = (TParm) FileUtils.readExcelFile(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("---------------in   addpamr: "+addParm);
			TTable table = getTable("TABLE_D");
//			System.out.println("11--------"+table.getItemString(0, "ORDER_CODE"));
//			System.out.println("--------"+table.getItemString(1, "ORDER_CODE"));
//			System.out.println(""+table.getRowChangeAction());
			for (int i = 0; i < table.getRowCount(); i++) {//BILLNO
                String orderCodeD =table.getItemString(i, "ORDER_CODE");
                //��ҩ���۵���
                String purOrderNoD = table.getItemString(i, "PURORDER_NO");
                String verifyinNOD =  table.getItemString(i, "VERIFYIN_NO");
//                System.out.println(i+"---purOrderNoD:"+purOrderNoD);
//                System.out.println(i+"---verifyinNOD:"+verifyinNOD);
//                System.out.println(i+"---orderCodeD:"+orderCodeD);
                for (int j = 0; j < addParm.getCount("ORDER_CODE") ;j++) {
					String orderCodeA = addParm.getValue("ORDER_CODE",j);
//					System.out.println(j+"---orderCodeA:"+orderCodeA);
	                //��ҩ���۵���
	                String purOrderNoA = addParm.getValue("PURORDER_NO", j);
//	                System.out.println(j+"---purOrderNoA:"+purOrderNoA);
	                if(orderCodeD.equals(orderCodeA) && purOrderNoD.equals(purOrderNoA)){
	                	String invoiceNo = addParm.getValue("INVOICE_NO", j);
	                	String invoiceDate = addParm.getValue("INVOICE_DATE", j);
	    				table.setItem(i, "INVOICE_NO", invoiceNo);
	    				table.setItem(i, "INVOICE_DATE", invoiceDate.replaceAll("-", "/"));
//	    				System.out.println("------1160-------sql:"+SPCSQL.updateVerifyin( invoiceNo,invoiceDate , verifyinNOD, orderCodeD, purOrderNoD));
	    				TJDODBTool.getInstance().update(SPCSQL.updateVerifyin( invoiceNo,invoiceDate , verifyinNOD, orderCodeD, purOrderNoD));
	                }
				}
			}
			table.setDSValue();
		}
	}	


	/**
	 * �����ҩ������
	 * 
	 * @date 20120828
	 * @author liyh
	 */
	public void onImpExcel() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("��Ӧ���̲���Ϊ��");
			return;
		}		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		String supCode = getValueString("SUP_CODE");
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DROG_TYPE", "N");
		} else {// �龫
			parm.setData("DROG_TYPE", "Y");
		}
		Object result = openDialog("%ROOT%\\config\\spc\\INDVerifyinImpXML.x", parm);

		if (result != null) {
//			FileParseExcel fileParseExcel=new  FileParseExcel();
			TParm fileParm = (TParm) result;
			if (fileParm == null) {
				return;
			}
			@SuppressWarnings("unused")
			String filePath = (String) fileParm.getData("PATH", 0);
			TParm addParm = new TParm();
			try {
//				addParm = (TParm) FileUtils.readXMLFileP(filePath);
				addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultParm = (TParm) addParm;
			TTable table = getTable("TABLE_D");
			table.removeRowAll();
			double purorder_qty = 0;
			double actual_qty = 0;
			double puroder_price = 0;
			double retail_price = 0;
			// ��Ӧ����
//			getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
			// �ƻ�����
			this.setValue("PLAN_NO", "");

			getRadioButton("GEN_DRUG").setEnabled(false);
			getRadioButton("TOXIC_DRUG").setEnabled(false);
			int rowCount = 0 ;
			//���ҩƷ�Ƿ��й�Ӧ����Ϣ
			/*String message = checkOrderCodeInAgent(supCode,addParm);
			if (null != message && message.length()>0) {//���û�����ֶ�ά��
				this.messageBox("����ҩƷ����ҩƷ����ͣ�ã�"+message);
				return;
			}*/
			for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
				String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
				//���ж�ERP_ID�Ƿ��Ѿ����� true����
				boolean flg = isImpERPInfo("","","",erpId);
				if(flg){//������� ������һ��ѭ��
					continue;
				}
				int row = table.addRow();
				
				String supOrderCode = addParm.getValue("ORDER_CODE", i);
				TParm supParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderCodeBySup(supCode, supOrderCode)));
				if(supParm.getCount()<=0 ||supParm.getErrCode()<0) {
					table.getDataStore().setItem(row, "ORDER_CODE",supOrderCode);
					continue;			
				}
				String orderCode = supParm.getValue("ORDER_CODE",0);
				// ORDER_CODE
				TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
//				System.out.println("phaParm:"+phaParm);
				// ���DATESTORE
				resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
				table.getDataStore().setItem(row, "ORDER_CODE",orderCode);
				table.getDataStore().setItem(row, "ORDER_DESC", phaParm.getValue("ORDER_DESC", 0));
				// ���������ת����
				TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
				if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
					this.messageBox("ҩƷ" + orderCode + "ת���ʴ���");
					return;
				}
				// ���TABLE_D����
				// ������
				purorder_qty = addParm.getDouble("PURORDER_QTY", i);
				int stockQty = getTRA.getInt("STOCK_QTY", 0);
				//�а�װ
				int conversionTraio = phaParm.getInt("CONVERSION_RATIO", 0);
				conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
//				System.out.println("--------stockQty: "+stockQty+",conversionTraio:"+conversionTraio+",--purorder_qty:"+purorder_qty);
				purorder_qty = purorder_qty * conversionTraio;               
//				System.out.println("--------purorder_qty: "+purorder_qty);
				table.setItem(row, "VERIFYIN_QTY", purorder_qty);			
				// ������
				table.setItem(row, "GIFT_QTY", 0);
				// ������λ
				// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
				// 0));
				table.setItem(row, "BILL_UNIT", phaParm.getValue("PURCH_UNIT", 0));

				// ���յ���
				puroder_price = addParm.getDouble("PURORDER_PRICE", i);
//				System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
				/******************���ռ۸� ȡ ind_agent ��Ϊpha_base  by liyh 20130313 start*****************************/
				//��ѯ ��Ӧ�̵ļ۸�
				TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
				if(null != agentParm && agentParm.getCount()>0 ){
					double verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
					// �������
					table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
		
				}
				/*				else{//�����̵�ҩƷ��Ϣ�����Զ�ά��
					//����ι�Ӧ��û�д������ҩƷ�򱣴�
					onSaveAgentInfo(orderCode,StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4),supCode);
					table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
					// �������
					table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));
				}
				table.setItem(row, "VERIFYIN_PRICE", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
				// �������
				table.setItem(row, "INVOICE_AMT", StringTool.round(phaParm.getDouble("STOCK_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0)*purorder_qty,2));*/
				/******************���ռ۸� ȡ ind_agent ��Ϊpha_base  by liyh 20130313  end *****************************/
				// ���ۼ�
				retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
				table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
					
				// ��������
				table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
				// �����������
				table.setItem(row, "PURSEQ_NO", i);
				// �ۼ�������
//				table.setItem(row, "ACTUAL", purorder_qty);

				String invoiceDate = addParm.getData("INVOICE_DATE", i) + "";
				invoiceDate = invoiceDate.replaceAll("-", "/");
				// ��Ʊ���� 
				table.setItem(row, "INVOICE_DATE", invoiceDate);
				String validDate = addParm.getData("VALID_DATE", i) + "";
				validDate = validDate.replaceAll("-", "/");
				table.setItem(row, "REASON_CHN_DESC", "VER01");
				// Ч��
				table.setItem(row, "VALID_DATE", validDate);
				// ��������
				table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
				// ��Ʊ��
				table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
				// ����
				table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
				// ����
				table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
				// װ�䵥��
				table.getDataStore().setItem(row, "SUP_ORDER_CODE", supOrderCode);
				String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
				table.setItem(row, "SPC_BOX_BARCODE",boxCode);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				table.getDataStore().setActive(row, false);
			}
			table.setDSValue();
			getComboBox("ORG_CODE").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			action = "insert";
			this.setCheckFlgStatus(action);
			getCheckBox("SELECT_ALL").setSelected(true);
			onCheckSelectAll();
		}
	}
	
	/**
	 * ���ҩƷ�Ƿ�����Ӧ��
	 * @param supCode
	 * @param parm
	 * @return
	 */
	public static String checkOrderCodeInAgent(String supCode,TParm parm ){
		int count = parm.getCount("ORDER_CODE");
		String msg = "";
		if(count>0){
			for(int i = 0 ;i < count ; i++){
				String orderCode = parm.getValue("ORDER_CODE", i);
				TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
				if(null != agentParm && agentParm.getCount()>0 ){
					continue;
				}else {
//					TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
					msg += parm.getValue("ORDER_DESC", i)+"("+parm.getValue("ORDER_CODE", i) +"),";
				}
			}
		}
		return msg;
	}
	
	/**
	 * ���ҩƷ�����Ƿ�ƥ��
	 * @param supCode
	 * @param parm
	 * @return
	 */
	public static String checkOrderType(boolean isDrugFlag,TParm parm ){
		int count = parm.getCount("ORDER_CODE");
		String msg = "";
		if(count>0){
			for(int i = 0 ;i < count ; i++){
				String orderCode = parm.getValue("ORDER_CODE", i);
				TParm resultParm = new TParm(TJDODBTool.getInstance().select(INDSQL.isDrug(orderCode)));
				System.out.println("------------INDSQL.isDrug(orderCode):"+INDSQL.isDrug(orderCode));
				if (isDrugFlag && null == resultParm && resultParm.getCount()<1 ) {
					msg = "�����ҩƷ��ѡ���ҩƷ����(�龫)������";
				}else if( !isDrugFlag && null != resultParm && resultParm.getCount()>0 ){
					msg = "�����ҩƷ��ѡ���ҩƷ����(��ҩ)������";
				}
			}
		}
		System.out.println("-------------checkMsg:"+msg);
		return msg;
	}	
	
	/**
	 * ���湩Ӧ�̴����ҩƷ
	 * @param orderCode
	 * @param price
	 */
	public static void onSaveAgentInfo(String orderCode,double price,String supCode){
        TParm parm = new TParm();
        parm.setData("SUP_CODE", supCode);
        parm.setData("ORDER_CODE", orderCode);
        parm.setData("MAIN_FLG", "Y");
        parm.setData("CONTRACT_NO", "");
        parm.setData("CONTRACT_PRICE",price);
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDAgentAction",
                                                  "onInsert", parm);
	}
	
    /**
     * ��ѯ��ҩ�������Ƿ��Ѿ����뵽������-������
     * @param orderCode ҩƷ����
     * @param boxCode   ��������
     * @param billNo    ���۵���-�����ձ����ֶ���PURORDER_NO
     * @param erpId     ��ҩ������-ID
     * @return String
     */
	public static boolean isImpERPInfo(String orderCode,String boxCode,String billNo,String erpId){
		//false ��ʾ δ���룬true��ʾ�Ѿ�����
		boolean flag = false;
//		System.out.println("------------SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId):"+SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getErpIdInfo(orderCode, boxCode, billNo, erpId)));
		if(null != parm && parm.getCount() > 0 && parm.getInt("COUNT_NUM", 0)> 0){
			flag = true;
		}
//		System.out.println("-------------flag: "+flag);
		return flag;
	}
	
	/**
	 * ��ӡ������ⵥ
	 */
	public void onPrint() {
		Timestamp datetime = SystemTool.getInstance().getDate();
		TTable table_d = getTable("TABLE_D");
		if ("".equals(this.getValueString("VERIFYIN_NO"))) {
			this.messageBox("���������յ�");
			return;
		}
		if (table_d.getRowCount() > 0) {
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT","ҩƷ������ⵥ");
			date.setData("VER_NO", "TEXT", "��ⵥ��: " + this.getValueString("VERIFYIN_NO"));
			date.setData("INVOICE_NO", "TEXT","��Ʊ��: "+table_d.getItemString(0, "INVOICE_NO"));
			date.setData("SUP_CODE", "TEXT", "��������: " + this.getTextFormat("SUP_CODE").getText());
			date.setData("ORG_CODE", "TEXT", "���ղ���: " + this.getComboBox("ORG_CODE").getSelectedName());
			String verdate = this.getValueString("VERIFYIN_DATE").substring(0,10);
			date.setData("DATE", "TEXT", "��������: " +verdate);
			// �������
			TParm parm = new TParm();
			String order_code = "";
			double ver_sum = 0;
			double own_sum = 0;
			double diff_sum = 0;
			for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
				if (!table_d.getDataStore().isActive(i)) {
					continue;
				}
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				/*parm.addData("INVOICE_NO", table_d.getItemString(i, "INVOICE_NO"));
				String invoiceDate =  table_d.getItemString(i, "INVOICE_DATE");
				if(invoiceDate.length()>=10){
					invoiceDate=invoiceDate.substring(0, 10).replace('-', '/');
				}
				parm.addData("INVOICE_DATE", invoiceDate);*/
				order_code = table_d.getDataStore().getItemString(i, "ORDER_CODE");
				TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderInfoByCode(order_code, this.getValueString("SUP_CODE"), "VER")));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("ҩƷ��Ϣ����");
					return;
				}
				parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
				parm.addData("SPECIFICATION", inparm.getValue("SPECIFICATION", 0));
				parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
				// luhai 2012-2-28 modify ��������ȡ1λ begin
				// parm.addData("VERIFYIN_QTY",
				// df3.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
				parm.addData("VERIFYIN_QTY", df5.format(table_d.getItemDouble(i, "VERIFYIN_QTY")));
				// luhai 2012-2-28 modify ��������ȡ1λ end
				parm.addData("VERIFYIN_PRICE", df4.format(StringTool.round(table_d.getItemDouble(i, "VERIFYIN_PRICE"), 4)));
				double ver_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") * table_d.getItemDouble(i, "VERIFYIN_PRICE");
				parm.addData("VER_AMT", df2.format(StringTool.round(ver_atm, 2)));
				ver_sum += ver_atm;
				parm.addData("OWN_PRICE", df4.format(StringTool.round(table_d.getItemDouble(i, "RETAIL_PRICE"), 4)));
				double own_atm = table_d.getItemDouble(i, "VERIFYIN_QTY") * table_d.getItemDouble(i, "RETAIL_PRICE");
				parm.addData("OWN_AMT", df2.format(StringTool.round(own_atm, 2)));
				own_sum += own_atm;
				parm.addData("DIFF_AMT", df2.format(StringTool.round(own_atm - ver_atm, 2)));
				diff_sum += own_atm - ver_atm;
				String batchNo = table_d.getItemString(i, "BATCH_NO");
				if(batchNo.length()>8) {
					batchNo = batchNo.substring(0, 7);										
				}
				parm.addData("BATCH_NO", batchNo);
				parm.addData("VALID_DATE", table_d.getItemString(i, "VALID_DATE").substring(0, 10).replace('-', '/'));
				String pha_type = inparm.getValue("PHA_TYPE", 0);
				if ("W".equals(pha_type)) {
					parm.addData("PHA_TYPE", "��ҩ");
				} else if ("C".equals(pha_type)) {
					parm.addData("PHA_TYPE", "�г�ҩ");
				} else if ("G".equals(pha_type)) {
					parm.addData("PHA_TYPE", "�в�ҩ");
				}
				// ��������
			/*	TParm manparm = new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSManufacturer(table_d.getItemString(i, "MAN_CODE"))));
				// System.out.println(""+SYSSQL.getSYSManufacturer(table_d.getItemString(i,
				// "MAN_CODE")));
				parm.addData("MAN_CODE", manparm.getValue("MAN_CHN_DESC", 0));*/
			 /*  String manCode = table_d.getItemString(i,
	                "MAN_CODE");	
	                if(manCode.length()>8) {									
	                	manCode = manCode.substring(0, 7)+"...";
	                }   */  
	            parm.addData("MAN_CODE","�ϸ�");	
				if ("Y".equals(inparm.getValue("BID_FLG", 0))) {
					parm.addData("BID_FLG", "��");
				} else {
					parm.addData("BID_FLG", "��");
				}
			}
			if (parm.getCount("ORDER_DESC") == 0) {
				this.messageBox("û�д�ӡ����");
				return;
			}
			int row = parm.getCount("ORDER_DESC");


			// luhai 2012-3-7 ɾ���ϼƲ��ָ���ͼ��ʵ�� begin
			parm.setCount(parm.getCount("ORDER_DESC"));
		//	parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");
			// luhai delete 2012-2-11 begin
			// parm.addData("SYSTEM", "COLUMNS", "INVOICE_DATE");
			// luhai delete 2012-2-11 end
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");		
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_QTY");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "VER_AMT");
			parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
			parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
			
			parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");

			date.setData("TABLE", parm.getData());
			date.setData("BAR_CODE", "TEXT", this.getValueString("VERIFYIN_NO"));
			// ��β����
		//	date.setData("USER", "TEXT", "�˶���: " + Operator.getName());
			date.setData("USER", "TEXT", "�˶���: ");
			// luhai 2012-3-7 begin ����ϼƲ���
			date.setData("VER_AMT", "TEXT", df2.format(StringTool.round(ver_sum, 2)));// ���ռ۸�
			//date.setData("OWN_AMT", "TEXT", df2.format(StringTool.round(own_sum, 2)));// ���ۼ۸�
			/*date
					.setData("VER_AMT_CHN", "TEXT", "�ϼƣ���д����"
							+ StringUtil.getInstance().numberToWord(Double.parseDouble(df2.format(StringTool.round(ver_sum, 2)))));// ���մ�д
*/			// luhai 2012-3-7 end
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\VerifyIn.jhw", date);
		} else {
			this.messageBox("û�д�ӡ����");
			return;
		}
	}
	
	
	/**
	 * ����װ�䵥
	 */
	public void onInsertPatByExl() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
			return;
		}
		if ("".equals(getValueString("SUP_CODE"))) {
			this.messageBox("��Ӧ���̲���Ϊ��");
			return;
		}		
		TParm parm = new TParm();
		parm.setData("ORG_CODE", getValueString("ORG_CODE"));
		String supCode = getValueString("SUP_CODE");
		if (getRadioButton("GEN_DRUG").isSelected()) {// ���龫
			parm.setData("DROG_TYPE", "N");
		} else {// �龫
			parm.setData("DROG_TYPE", "Y");
		}
		
 		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
	
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			String filePath = file.getPath();
//			System.out.println("----------filePaht:"+filePath);
			if (filePath != null) {
				TParm addParm = new TParm();
				try {
//					addParm = (TParm) FileUtils.readXMLFileP(filePath);
					addParm = (TParm) FileParseExcel.getInstance().readXls(filePath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				resultParm = (TParm) addParm;
				TTable table = getTable("TABLE_D");
				table.removeRowAll();
				double purorder_qty = 0;
				double actual_qty = 0;
				double puroder_price = 0;
				double retail_price = 0;
				// ��Ӧ����
//				getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
				// �ƻ�����
				this.setValue("PLAN_NO", "");

				getRadioButton("GEN_DRUG").setEnabled(false);
				getRadioButton("TOXIC_DRUG").setEnabled(false);
				int rowCount = 0 ;
/*				//*********update by liyh 20130607  �ж�ҩƷ���� start**********//*
				boolean isDrugFlag = getRadioButton("TOXIC_DRUG").isSelected();
				String checkMsg = checkOrderType(isDrugFlag,addParm);
				if(null != checkMsg && checkMsg.length()>0){
					this.messageBox(checkMsg);
					return ;
				}
				//*********update by liyh 20130607  �ж�ҩƷ���� end**********//*
*/				//���ҩƷ�Ƿ��й�Ӧ����Ϣ
				/*String message = checkOrderCodeInAgent(supCode,addParm);
				if (null != message && message.length()>0) {//���û�����ֶ�ά��
					this.messageBox("����ҩƷ����ҩƷ����ͣ�ã�"+message);
					return;
				}*/
				for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
					String erpId = addParm.getInt("ERP_PACKING_ID", i)+"";
					//���ж�ERP_ID�Ƿ��Ѿ����� true����
					boolean flg = isImpERPInfo("","","",erpId);
					if(flg){//������� ������һ��ѭ��
						continue;
					} 
					int row = table.addRow();			
					// ORDER_CODE
					String supOrderCode = addParm.getValue("ORDER_CODE", i);
					String prc = addParm.getValue("PRC", i);		
					TParm supParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getOrderCodeBySup(supCode, supOrderCode)));
					if(supParm.getCount()<=0 ||supParm.getErrCode()<0) {
						table.getDataStore().setItem(row, "ORDER_CODE",supOrderCode);
						continue;			  
					}
					String orderCode = supParm.getValue("ORDER_CODE",0);
					TParm phaParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getPHABaseInfo(orderCode)));
//					System.out.println("phaParm:"+phaParm);
					// ���DATESTORE
					resultParm.setData("STOCK_PRICE", i, phaParm.getDouble("STOCK_PRICE", 0));
					table.getDataStore().setItem(row, "ORDER_CODE",orderCode);
					table.getDataStore().setItem(row, "ORDER_DESC", phaParm.getValue("ORDER_DESC", 0));
					// ���������ת����
					TParm getTRA = INDTool.getInstance().getTransunitByCode(orderCode);
					if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
						this.messageBox("ҩƷ" + orderCode + "ת���ʴ���");
						return;
					}
					// ���TABLE_D����
					// ������
					purorder_qty = addParm.getDouble("PURORDER_QTY", i);
					int stockQty = getTRA.getInt("STOCK_QTY", 0);
					//�а�װ
					int conversionTraio = supParm.getInt("CONVERSION_RATIO", 0);
					conversionTraio = conversionTraio == 0 ? 1 : conversionTraio;
					table.getDataStore().setItem(row, "PRC", Double.valueOf(prc) /conversionTraio);	
//					System.out.println("--------stockQty: "+stockQty+",conversionTraio:"+conversionTraio+",--purorder_qty:"+purorder_qty);
					purorder_qty = purorder_qty  * conversionTraio;
//					System.out.println("--------purorder_qty: "+purorder_qty);
					table.setItem(row, "VERIFYIN_QTY", purorder_qty);
					// ������
					table.setItem(row, "GIFT_QTY", 0);   
					// ������λ
					// System.out.println("BILL_UNIT:"+phaParm.getValue("PURCH_UNIT",
					// 0));
					table.setItem(row, "BILL_UNIT", phaParm.getValue("STOCK_UNIT", 0));

					// ���յ���
					puroder_price = addParm.getDouble("PURORDER_PRICE", i);
//					System.out.println(i+"--------------SPCSQL.getPriceOfSupCode: "+SPCSQL.getPriceOfSupCode("18", orderCode));
					/******************���ռ۸� ȡ ind_agent ��Ϊpha_base  by liyh 20130313 start*****************************/
					//��ѯ ��Ӧ�̵ļ۸�
					TParm agentParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getPriceOfSupCode(supCode, orderCode)));
					if(null != agentParm && agentParm.getCount()>0 ){
						double verifyPrice =  agentParm.getDouble("LAST_VERIFY_PRICE", 0);
						verifyPrice =  agentParm.getDouble("CONTRACT_PRICE", 0);
						table.setItem(row, "VERIFYIN_PRICE", StringTool.round(verifyPrice,4));
						// �������
						table.setItem(row, "INVOICE_AMT", StringTool.round(verifyPrice*purorder_qty,2));
			
					}
					// ���ۼ�
					retail_price = phaParm.getDouble("RETAIL_PRICE", 0);
					table.setItem(row, "RETAIL_PRICE", StringTool.round(phaParm.getDouble("RETAIL_PRICE", 0)*getTRA.getInt("DOSAGE_QTY", 0),4));
						
					// ��������
					table.setItem(row, "PURORDER_NO", addParm.getData("PURORDER_NO", i));
					// �����������
					table.setItem(row, "PURSEQ_NO",i);
					// �ۼ�������
					//table.setItem(row, "ACTUAL", 0);

					String invoiceDate = addParm.getData("INVOICE_DATE", i) + "";
					invoiceDate = invoiceDate.replaceAll("-", "/");
					// ��Ʊ����
					table.setItem(row, "INVOICE_DATE", invoiceDate);
					String validDate = addParm.getData("VALID_DATE", i) + "";
					validDate = validDate.replaceAll("-", "/");
					table.setItem(row, "REASON_CHN_DESC", "VER01");
					// Ч��
					table.setItem(row, "VALID_DATE", validDate);
					// ��������
					table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
					// ��Ʊ��
					table.setItem(row, "INVOICE_NO", addParm.getData("INVOICE_NO", i));
					// ����
					table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
					// ����
					table.setItem(row, "ERP_PACKING_ID", addParm.getData("ERP_PACKING_ID", i));				
					// װ�䵥��
					String boxCode = addParm.getValue("SPC_BOX_BARCODE", i);
					table.setItem(row, "SPC_BOX_BARCODE",boxCode);
					//��Ӧ��ҩƷ����
			//		resultParm.setData("",i,addParm.getData("PRC", i));
					table.getDataStore().setItem(row, "SUP_ORDER_CODE", supOrderCode);
				
					table.getDataStore().setItem(i, "UPDATE_FLG", "0");
					table.getDataStore().setActive(row, false);
				}
				table.setDSValue();
				getComboBox("ORG_CODE").setEnabled(false);
				getTextFormat("SUP_CODE").setEnabled(false);
				action = "insert";
				this.setCheckFlgStatus(action);
				getCheckBox("SELECT_ALL").setSelected(true);
				onCheckSelectAll();
			}
		}
		//onPackage();

	}	

	/**
	 * ��ӡ���ⵥ
	 */
	public void onPrintDispendseOut(String dispsenseNo) {
		Timestamp datetime = SystemTool.getInstance().getDate();
		if ("".equals(dispsenseNo)) {
			return;
		}
		TParm dispenseMParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getDispenseByDisNo(dispsenseNo)));
//		System.out.println("-----dispenseMParm: "+dispenseMParm);
		if (dispenseMParm.getCount() > 0) {
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()) + "���ⵥ");
			// ===============pangben modify 20110607 ��Ӽ���ע��
			String urGentFlg = dispenseMParm.getValue("URGENT_FLG", 0);
			if (null != urGentFlg && urGentFlg.equals("Y"))
				date.setData("URGENT", "TEXT", "��");
			else
				date.setData("URGENT", "TEXT", "");
			date.setData("DISP_NO", "TEXT", "���ⵥ��: " + dispsenseNo);
			date.setData("REQ_NO", "TEXT", "���뵥��: " + dispenseMParm.getValue("REQUEST_NO",0));
			String dispenseDate = dispenseMParm.getValue("DISPENSE_DATE",0);
			if(null != dispenseDate && dispenseDate.length() > 10){
				dispenseDate = dispenseDate.substring(0, 10).replace('-', '/');
			}
			date.setData("OUT_DATE", "TEXT", "��������: " + dispenseDate);
			date.setData("REQ_TYPE", "TEXT", "�������: " + "�龫����");
			TParm appOrgCodeParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgDescByOrgCode(dispenseMParm.getValue("APP_ORG_CODE",0))));
			TParm toOrgCodeParm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getOrgDescByOrgCode(dispenseMParm.getValue("TO_ORG_CODE",0))));
			date.setData("ORG_CODE_APP", "TEXT", "���벿��: " +appOrgCodeParm.getValue("ORG_CHN_DESC",0));
			date.setData("ORG_CODE_FROM", "TEXT", "���ܲ���: " +toOrgCodeParm.getValue("ORG_CHN_DESC",0));
			date.setData("DATE", "TEXT", "�Ʊ�����: " + datetime.toString().substring(0, 10).replace('-', '/'));

			// �������
			TParm parm = new TParm();
			String order_code = "";
			String order_desc = "";
			double qty = 0;
			double sum_retail_price = 0;
			// luhai 2012-1-22 ����ɹ����ܺ�
			double sum_verifyin_price = 0;

			String sql = "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL " + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  " + "  END AS ORDER_DESC, "
					+ " B.SPECIFICATION, C.UNIT_CHN_DESC, A.RETAIL_PRICE, "
					+ " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,A.VERIFYIN_PRICE "// ADD
					+ " FROM IND_DISPENSED A, SYS_FEE B, SYS_UNIT C " + " WHERE A.ORDER_CODE = B.ORDER_CODE " + " AND A.UNIT_CODE = C.UNIT_CODE "
					+ " AND A.DISPENSE_NO = '" + dispsenseNo + "' " + " ORDER BY A.SEQ_NO";
			// luhai modify ���ⵥɾ����Ʒ�� begin
			TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
			for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
				parm.addData("ORDER_DESC", printData.getValue("ORDER_DESC", i));
				parm.addData("SPECIFICATION", printData.getValue("SPECIFICATION", i));
				parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
				parm.addData("UNIT_PRICE", df4.format(printData.getDouble("RETAIL_PRICE", i)));
				parm.addData("VERIFYIN_PRICE", df4.format(printData.getDouble("VERIFYIN_PRICE", i)));
				qty = printData.getDouble("OUT_QTY", i);
				parm.addData("QTY", qty);
				parm.addData("AMT", StringTool.round(printData.getDouble("RETAIL_PRICE", i) * qty, 2));
				parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble("VERIFYIN_PRICE", i) * qty, 2));
				sum_retail_price += printData.getDouble("RETAIL_PRICE", i) * qty;
				sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) * qty;
				parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
				parm.addData("VALID_DATE", StringTool.getString(printData.getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
			}

			if (parm.getCount("ORDER_DESC") <= 0) {
				this.messageBox("û�д�ӡ����");
				return;
			}
			// luhai 2012-1-22 modify ����ɹ����ɹ����� begin
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "AMT");
			parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
			date.setData("TABLE", parm.getData());

			// ��β����
			String atm = StringTool.round(sum_retail_price, 2) + "";
			String verifyinAtm = StringTool.round(sum_verifyin_price, 2) + "";
			// "�ϼ�(��д): " + StringUtil.getInstance().numberToWord(atm));
			date.setData("ATM", "TEXT", atm);
			date.setData("VERIFYIN_ATM", "TEXT", verifyinAtm);
			date.setData("VERIFYIN_ATM_DESC", "TEXT", StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
			date.setData("TOT", "TEXT", "�ϼ�: ");
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			date.setData("BAR_CODE", "TEXT", dispsenseNo);
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", date);
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
		if (row != -1) {
			// ������Ϣ(TABLE��ȡ��)
			setValue("VERIFYIN_DATE", table.getItemTimestamp(row, "VERIFYIN_DATE"));
			setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
			setValue("VERIFYIN_NO", table.getItemString(row, "VERIFYIN_NO"));
			setValue("PLAN_NO", table.getItemString(row, "PLAN_NO"));
			setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
			setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
			setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
			if (getRadioButton("UPDATE_FLG_B").isSelected()) {
				setValue("CHECK_FLG", "Y");
			} else {
				setValue("CHECK_FLG", "N");
			}
			// �趨ҳ��״̬
			getTextField("VERIFYIN_NO").setEnabled(false);
			getTextFormat("SUP_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
//			((TMenuItem) getComponent("export")).setEnabled(false);
			action = "updateM";
			this.setCheckFlgStatus(action);

			// ��ѯѡ�����յ�������Ӧ��ϸ��
			TTable table_D = getTable("TABLE_D");
			table_D.removeRowAll();
			table_D.setSelectionMode(0);
			TDS tds = new TDS();
			tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
			tds.retrieve();
			if (tds.rowCount() == 0) {
				this.messageBox("û�ж�����ϸ");
			}
			// �۲���
			IndVerifyinObserver obser = new IndVerifyinObserver();
			tds.addObserver(obser);
			table_D.setDataStore(tds);
			table_D.setDSValue();

			this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
			this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			this.setValue("PRICE_DIFFERENCE", getSumRetailMoney() - getSumVerifinMoney());
			if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫
				// ����״̬
				((TMenuItem) getComponent("save")).setEnabled(true);
			} else {//��ҩ
				// ����״̬
				((TMenuItem) getComponent("save")).setEnabled(false);
			}			
		}
	}

	/**
	 * ��ϸ���(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {
		TTable table = getTable("TABLE_D");
		int row = table.getSelectedRow();
		if (row != -1) {
			// ������Ϣ
			TTable table_M = getTable("TABLE_M");
			table_M.setSelectionMode(0);
			if (getTextField("VERIFYIN_NO").isEnabled()) {
				action = "insert";
			} else {
				action = "updateD";
			}
			this.setCheckFlgStatus(action);

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
		if (node.getValue() != null && node.getOldValue() != null && node.getValue().equals(node.getOldValue()))
			return true;
		// Table������
		TTable table = node.getTable();
		String columnName = table.getDataStoreColumnName(node.getColumn());
		int row = node.getRow();
		if ("VERIFYIN_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("������������С�ڻ����0");
				return true;
			}
			double amt = StringTool.round(qty * table.getItemDouble(row, "VERIFYIN_PRICE"), 2);
			table.setItem(row, "INVOICE_AMT", amt);
			return false;
		}
		if ("GIFT_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("����������С��0");
				return true;
			}
			return false;
		}
		if ("VERIFYIN_PRICE".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("���յ��۲���С�ڻ����0");
				return true;
			}
			double amt = StringTool.round(qty * table.getItemDouble(row, "VERIFYIN_QTY"), 2);
			table.setItem(row, "INVOICE_AMT", amt);
			return false;
		}
		if ("QUALITY_DEDUCT_AMT".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("Ʒ�ʿۿ��С��0");
				return true;
			}
		}
		// ��Ʊ����
		if ("INVOICE_NO".equals(columnName) && row == 0) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "INVOICE_NO", node.getValue());
			}
		}
		// ��Ʊ����
		if ("INVOICE_DATE".equals(columnName) && row == 0) {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.setItem(i, "INVOICE_DATE", node.getValue());
			}
			return false;
		}
		String order_code = table.getItemString(row, "ORDER_CODE");
		String sql = "SELECT PHA_TYPE FROM PHA_BASE WHERE ORDER_CODE = '" + order_code + "' ";
		// System.out.println("sql---"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String pha_type = parm.getValue("PHA_TYPE", 0);
		// ��������
		if ("MAN_CODE".equals(columnName)) {
			if (!"G".equals(pha_type)) {
				this.messageBox("��ҩƷ���в�ҩ�������޸���������!");
				return true;
			}
		}
		// ���ۼ�
		if ("RETAIL_PRICE".equals(columnName)) {
			if (!"G".equals(pha_type)) {
				this.messageBox("��ҩƷ���в�ҩ�������޸����ۼ۸�!");
				return true;
			} else {
				double own_price = TypeTool.getDouble(node.getValue());
				if (own_price <= 0) {
					this.messageBox("���۵��۲���С�ڻ����0");
					return true;
				}
				double amt = StringTool.round(own_price * (table.getItemDouble(row, "VERIFYIN_QTY") + table.getItemDouble(row, "GIFT_QTY")), 2);
				table.setItem(row, "SELL_SUM", amt);
				return false;
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
		// ���ѡ�е���
		int row = tableDown.getSelectedRow();
		if (column == 0) {
			if (tableDown.getDataStore().isActive(row)) {
				// ���������ܽ��
				double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
				double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
				double sum = tableDown.getItemDouble(row, "RETAIL_PRICE") * (amount1 + amount2);
				double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
				this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum + sum_atm, 2));
				// ��������ܽ��
				double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
				double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
				this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(ver_amt + amt, 2));
			} else {
				// ���������ܽ��
				double amount1 = tableDown.getItemDouble(row, "VERIFYIN_QTY");
				double amount2 = tableDown.getItemDouble(row, "GIFT_QTY");
				double sum = tableDown.getItemDouble(row, "RETAIL_PRICE") * (amount1 + amount2);
				double sum_atm = this.getValueDouble("SUM_RETAIL_PRICE");
				if (sum_atm - sum < 0) {
					this.setValue("SUM_RETAIL_PRICE", 0);
				} else {
					this.setValue("SUM_RETAIL_PRICE", StringTool.round(sum_atm - sum, 2));
				}
				// ��������ܽ��
				double ver_amt = this.getValueDouble("SUM_VERIFYIN_PRICE");
				double amt = tableDown.getItemDouble(row, "INVOICE_AMT");
				if (ver_amt - amt < 0) {
					this.setValue("SUM_VERIFYIN_PRICE", 0);
				} else {
					this.setValue("SUM_VERIFYIN_PRICE", StringTool.round(ver_amt - amt, 2));
				}
			}
			// �������
			this.setValue("PRICE_DIFFERENCE", StringTool.round(getValueDouble("SUM_RETAIL_PRICE") - getValueDouble("SUM_VERIFYIN_PRICE"), 2));
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
				table.getDataStore().setActive(i, true);
				table.getDataStore().setItem(i, "UPDATE_FLG", "0");
				this.setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
				this.setValue("SUM_VERIFYIN_PRICE", getSumVerifinMoney());
				this.setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney() - getSumVerifinMoney(), 2));
			}
		} else {
			for (int i = 0; i < table.getRowCount(); i++) {
				table.getDataStore().setActive(i, false);
				table.getDataStore().setItem(i, "UPDATE_FLG", "");
				this.setValue("SUM_RETAIL_PRICE", 0);
				this.setValue("SUM_VERIFYIN_PRICE", 0);
				this.setValue("PRICE_DIFFERENCE", 0);
			}
		}
		table.setDSValue();
	}

	/**
	 * ��ѡ��ı��¼�
	 */
	public void onChangeRadioButton() {
		if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
//			((TMenuItem) getComponent("export")).setEnabled(false);
		} else {
			((TMenuItem) getComponent("save")).setEnabled(true);
//			((TMenuItem) getComponent("export")).setEnabled(true);
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		/**
		 * Ȩ�޿��� Ȩ��1:һ�����������Ȩ��,ֻ��ʾ������������;������¼�빦�� Ȩ��2:һ���������Ȩ��,ֻ��ʾ������������;��������¼�빦��
		 * Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ�Ű�������¼�빦��
		 */
		// ����Ȩ��
		if (!this.getPopedem("giftEnabled")) {
			TTable table_d = getTable("TABLE_D");
			table_d.setLockColumns("1,2,4,5,7,9,10,18,19,20");
			gift_flg = false;
		}
		// ��ʾȫԺҩ�ⲿ��
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.getIndOrgByUserId(Operator.getID(), " AND B.ORG_TYPE = 'A' ")));
			getComboBox("ORG_CODE").setParmValue(parm);
			if (parm.getCount("NAME") > 0) {
				getComboBox("ORG_CODE").setSelectedIndex(1);
			}
			dept_flg = false;
		}
		// ������Ȩ��
		if (!this.getPopedem("checkEnabled")) {
			getCheckBox("CHECK_FLG").setEnabled(false);
			check_flg = false;
		}

		// ��������¼�
		addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, "onTableDChangeValue");
		// ��TABLEDEPT�е�CHECKBOX��������¼�
		callFunction("UI|TABLE_D|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
		// ��ʼ������ʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
		setValue("VERIFYIN_DATE", date);
		// ��ʼ������״̬
		((TMenuItem) getComponent("delete")).setEnabled(false);
		// ��ʼ��TABLE_D
		TTable table_D = getTable("TABLE_D");
		table_D.removeRowAll();
		table_D.setSelectionMode(0);
		TDS tds = new TDS();
		tds.setSQL(INDSQL.getVerifyinDByNo(getValueString("VERIFYIN_NO")));
		tds.retrieve();
		if (tds.rowCount() == 0) {
			seq = 1;
		} else {
			seq = getMaxSeq(tds, "SEQ_NO");
		}
		IndVerifyinObserver obser = new IndVerifyinObserver();
		tds.addObserver(obser);
		table_D.setDataStore(tds);
		table_D.setDSValue();
		// getComboBox("ORG_CODE").setSelectedIndex(1);
		action = "insert";
		this.setCheckFlgStatus(action);
		resultParm = new TParm();
/*		if (getRadioButton("TOXIC_DRUG").isSelected()) {// �龫q
			// ����״̬
			((TMenuItem) getComponent("save")).setEnabled(true);
		} else {//��ҩ
			// ����״̬
			((TMenuItem) getComponent("save")).setEnabled(false);
		}	*/
	}

	/**
	 * �趨�������״̬
	 * 
	 * @param action
	 *            String
	 */
	private void setCheckFlgStatus(String action) {
		if ("insert".equals(action)) {
			if (check_flg) {
				TTable table_M = getTable("TABLE_M");
				if (table_M.getSelectedRow() >= 0) {
					getCheckBox("CHECK_FLG").setEnabled(true);
				} else {
					getCheckBox("CHECK_FLG").setEnabled(false);
				}
			} else {
				getCheckBox("CHECK_FLG").setEnabled(false);
			}
		} else if ("updateM".equals(action)) {
			if (check_flg) {
				getCheckBox("CHECK_FLG").setEnabled(true);
			} else {
				getCheckBox("CHECK_FLG").setEnabled(false);
			}
		} else if ("updateD".equals(action)) {
			getCheckBox("CHECK_FLG").setEnabled(false);
		}
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("���ղ��Ų���Ϊ��");
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
		// �ж�ϸ���Ƿ��б�ѡ����
		TTable table = getTable("TABLE_D");
		table.acceptText();
		int count = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (table.getDataStore().isActive(i)) {
				count++;
				// �ж�������ȷ��
				double qty = table.getItemDouble(i, "VERIFYIN_QTY");
				if (qty <= 0) {
					this.messageBox("������������С�ڻ����0");
					return false;
				}
				double pur_qty = resultParm.getDouble("PURORDER_QTY", i);
/*				if (qty > pur_qty) {
					this.messageBox("�����������ܴ��ڶ�����");
					return false;
				}*/
				qty = table.getItemDouble(i, "GIFT_QTY");
				if (qty < 0) {
					this.messageBox("����������С��0");
					return false;
				}
				qty = table.getItemDouble(i, "VERIFYIN_PRICE");
				if (qty <= 0) {
					this.messageBox("���յ��۲���С�ڻ����0");
					return false;
				}
				// ��Ʊ����
				/*
				 * if ("".equals(table.getItemString(i, "INVOICE_NO"))) {
				 * this.messageBox("��Ʊ���벻��Ϊ��"); return false; }
				 */
				// ��Ʊ����
				/*
				 * if (table.getItemTimestamp(i, "INVOICE_DATE") ==
				 * null||table.getItemTimestamp(i, "INVOICE_DATE").equals(new
				 * TNull(Timestamp.class))) { this.messageBox("��Ʊ���ڲ���Ϊ��");
				 * return false; }
				 */
				// ����
				if ("".equals(table.getItemString(i, "BATCH_NO"))) {
					this.messageBox("���Ų���Ϊ��");
					return false;
				}
				// Ч��
				if (table.getItemTimestamp(i, "VALID_DATE") == null || table.getItemTimestamp(i, "VALID_DATE").equals(new TNull(Timestamp.class))) {
					this.messageBox("Ч�ڲ���Ϊ��");
					return false;
				}
				// Ʒ�ʿۿ�
				qty = table.getItemDouble(i, "QUALITY_DEDUCT_AMT");
				if (qty < 0) {
					this.messageBox("Ʒ�ʿۿ��С��0");
					return false;
				}
			}
		}
		if (count == 0) {
			this.messageBox("û��ִ������");
			return false;
		}
		return true;
	}

	/**
	 * ȡ�����յ���
	 * 
	 * @return String
	 */
	private String getVerifyinNO(String invoiceNo) {
		String verifyinNo = "";
		if (map.containsKey(invoiceNo)) {
			verifyinNo = (String) map.get(invoiceNo);
			TDS tds = new TDS();
			tds.setSQL(INDSQL.getVerifyinDByNo(verifyinNo));
			tds.retrieve();
			seq++;
		} else {
			verifyinNo = SystemTool.getInstance().getNo("ALL", "IND", "IND_VERIFYIN", "No");
			map.put(invoiceNo, verifyinNo);
			seq = 1;
		}
		return verifyinNo;
	}

	/**
	 * ���������ܽ��
	 * 
	 * @return
	 */
	private double getSumRetailMoney() {
		TTable table = getTable("TABLE_D");
		TDataStore ds = table.getDataStore();
		table.acceptText();
		double sum = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
				double amount1 = table.getItemDouble(i, "VERIFYIN_QTY");
				double amount2 = table.getItemDouble(i, "GIFT_QTY");
				sum += table.getItemDouble(i, "RETAIL_PRICE") * (amount1 + amount2);
			}
		}
		return StringTool.round(sum, 2);
	}

	/**
	 * ��������ܽ��
	 * 
	 * @return
	 */
	private double getSumVerifinMoney() {
		TTable table = getTable("TABLE_D");
		TDataStore ds = table.getDataStore();
		table.acceptText();
		double sum = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
				sum += table.getItemDouble(i, "INVOICE_AMT");
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

	// �������յ����ж�����������������״̬
	private void updateIndVerifinDUpdateFlg(String verifyin_no) {
		String sql = "SELECT A.PURORDER_QTY + A.GIFT_QTY AS QTY, " + " B.VERIFYIN_QTY, B.VERIFYIN_NO, B.SEQ_NO "
				+ " FROM IND_PURORDERD A, IND_VERIFYIND B, IND_VERIFYINM C " + " WHERE A.PURORDER_NO = B.PURORDER_NO " + " AND A.SEQ_NO = B.PURSEQ_NO "
				+ " AND B.VERIFYIN_NO = C.VERIFYIN_NO " + " AND A.PURORDER_QTY = B.VERIFYIN_QTY " + " AND C.CHECK_DATE IS NOT NULL " + " AND B.VERIFYIN_NO = '"
				+ verifyin_no + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String update_sql = "UPDATE IND_VERIFYIND SET UPDATE_FLG = '";
		String update_where = "' WHERE VERIFYIN_NO = '" + verifyin_no + "' AND SEQ_NO = ";
		String update_flg = "1";
		String sql_list = "";
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("SEQ_NO"); i++) {
			if (parm.getDouble("VERIFYIN_QTY", i) >= parm.getDouble("QTY", i)) {
				update_flg = "3";
			} else {
				update_flg = "1";
			}
			sql_list = update_sql + update_flg + update_where + parm.getInt("SEQ_NO", i);
			result = new TParm(TJDODBTool.getInstance().update(sql_list));
		}
	}

	/**
	 * ������ҩƷΪ��ҩʱ�ж��Ƿ������������
	 * 
	 * @param verifyin_no
	 *            String
	 */
	private void updateManCode(String verifyin_no) {
		String sql = " SELECT A.ORDER_CODE, A.MAN_CODE , B.MAN_CHN_DESC " + " FROM IND_VERIFYIND A, PHA_BASE B " + " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND B.PHA_TYPE = 'G' AND A.VERIFYIN_NO = '" + verifyin_no + "' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
			return;
		}
		List list = new ArrayList();
		String man_code = "";
		String order_code = "";
		String sql_1 = "";
		String sql_2 = "";
		String sql_3 = "";
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			if (parm.getValue("MAN_CHN_DESC").equals(parm.getValue("MAN_CODE", i))) {
				continue;
			} else {
				order_code = parm.getValue("ORDER_CODE", i);
				man_code = parm.getValue("MAN_CODE", i);
				sql_1 = "UPDATE PHA_BASE SET MAN_CHN_DESC = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' ";
				sql_2 = "UPDATE SYS_FEE SET MAN_CODE = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' ";
				sql_3 = "UPDATE SYS_FEE_HISTORY SET MAN_CODE = '" + man_code + "' WHERE ORDER_CODE = '" + order_code + "' AND ACTIVE_FLG = 'Y'";
				list.add(sql_1);
				list.add(sql_2);
				list.add(sql_3);
			}
		}
		if (list == null || list.size() <= 0) {
			return;
		} else {
			String[] sql_list = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				sql_list[i] = list.get(i).toString();
			}
			TParm result = new TParm(TJDODBTool.getInstance().update(sql_list));
			if (result == null || result.getErrCode() < 0) {
				return;
			} else {
				TIOM_Database.logTableAction("SYS_FEE");
			}
		}
	}
}
