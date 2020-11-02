package jdo.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdo.ibs.IBSOrderdTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeHistoryTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ҩ�����ϵͳ�ڲ����÷���������ӿڷ���
 * </p>
 * 
 * <p>
 * Description:ҩ�����ϵͳ�ڲ����÷���������ӿڷ���
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class INDTool extends TJDOTool {

	private String code;

	/**
	 * ʵ��
	 */
	public static INDTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static INDTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INDTool() {
		onInit();
	}

	/**
	 * ��ѯ�ɹ��ƻ�������������������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryPurPlan(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// ��ѯ����
		TParm result = IndPurPlanMTool.getInstance().onQuery(parm);
		return result;
	}

	/**
	 * �ɹ��ƻ����ò�ѯ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryExcerptByOrder(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �ɹ��ƻ����ò�ѯ
		TParm result = IndPurPlanMTool.getInstance()
				.onQueryExcerptByOrder(parm);
		// System.out.println("result---"+result);
		TParm buy = new TParm();
		TParm sell = new TParm();
		TParm main = new TParm();
		TParm midd = new TParm();
		TParm order = new TParm();
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			order.setData("ORDER_CODE", result.getValue("ORDER_CODE", i));
			order.setData("START_DATE", parm.getTimestamp("START_DATE"));
			order.setData("END_DATE", parm.getTimestamp("END_DATE"));
			order.setData("SUP_CODE", result.getValue("SUP_CODE", i));
			order.setData("ORG_CODE", parm.getValue("ORG_CODE"));
			// ��ѯ���ڲɹ���
			buy = IndPurPlanMTool.getInstance().onQueryBuyQty(order);
			// System.out.println("buy---"+buy);
			if (buy != null && !"".equals(buy)) {
				result.setData("BUY_QTY", i, buy.getDouble("BUY_QTY", 0));
			} else {
				result.setData("BUY_QTY", i, 0);
			}
			// ��ѯ����������
			sell = IndPurPlanMTool.getInstance().onQuerySellQty(order);
			// System.out.println("sell---"+sell);
			if (sell != null && !"".equals(sell)) {
				result.setData("SELL_QTY", i, sell.getDouble("SELL_QTY", 0));
			} else {
				result.setData("SELL_QTY", i, 0);
			}
			// ��ѯ������
			main = IndPurPlanMTool.getInstance().onQueryMainQty(order);
			if (main != null && !"".equals(main)) {
				result.setData("MAIN_QTY", i, main.getDouble("MAIN_QTY", 0));
			} else {
				result.setData("MAIN_QTY", i, 0);
			}
			// System.out.println("main---"+main);
			// ��ѯ�п���
			midd = IndPurPlanMTool.getInstance().onQueryMiddQty(order);
			if (midd != null && !"".equals(midd)) {
				result.setData("MIDD_QTY", i, midd.getDouble("MIDD_QTY", 0));
			} else {
				result.setData("MIDD_QTY", i, 0);
			}
			// System.out.println("midd---"+midd);
		}
		if ("Y".equals(parm.getValue("CHECK_SAFE"))) {
			double safe = 0.0;
			double stock = 0.0;
			for (int i = result.getCount() - 1; i >= 0; i--) {
				safe = TypeTool.getDouble(result.getData("SAFE_QTY", i));
				stock = TypeTool.getDouble(result.getData("STOCK_QTY", i));
				if (stock > safe) {
					result.removeRow(i);
				}
			}
		}
		return result;
	}

	/**
	 * ��ѯ�ɹ��ƻ����ɶ�������ϸ
	 * 
	 * @return
	 */
	public TParm onQueryCreatePurPlanD(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// ��ѯ�ɹ��ƻ����ɶ�������ϸ
		TParm result = IndPurPlanDTool.getInstance().onQueryCreate(parm);
		String order_code = "";
		String org_code = "";
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			double purorder_qty = 0;
			double stock_qty = 0;
			double create_qty = 0;
			order_code = result.getValue("ORDER_CODE", i);
			org_code = parm.getValue("ORG_CODE");
			// �����
			stock_qty = getStockQTY(org_code, order_code);
			if (stock_qty < 0) {
				stock_qty = 0;
			}
			result.setData("QTY", i, stock_qty);
			// �ۼ�������
			parm.setData("ORDER_CODE", order_code);
			purorder_qty = IndPurorderDTool.getInstance().onQueryPurOrderQTY(
					parm).getDouble("PURORDER_QTY", 0);
			if (purorder_qty < 0) {
				purorder_qty = 0;
			}
			result.setData("PURORDER_QTY", i, purorder_qty);
			// System.out.println("purorder_qty---" + purorder_qty);
			// ����������
			create_qty = result.getDouble("PLAN_QTY", i) - purorder_qty;
			result.setData("CREATE_QTY", i, create_qty);
			// System.out.println("create_qty---" + create_qty);
			// ���ɽ��
			result.setData("ATM", i, StringTool.round(result.getDouble(
					"CONTRACT_PRICE", i)
					* create_qty, 2));
		}
		return result;
	}

	/**
	 * �ɹ��ƻ����ɶ�����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onCreatePurOrder(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		TParm parmM = parm.getParm("PUR_M");
		TParm parmD = parm.getParm("PUR_D");
		Map sup = new HashMap();
		Map pur_no = new HashMap();
		// ��Ӧ����
		String sup_code = "";
		// ��������
		String purorder_no = "";
		int count = 0;
		for (int i = 0; i < parmD.getCount("SUP_CODE"); i++) {
			sup_code = parmD.getValue("SUP_CODE", i);
			if (sup.isEmpty()) {
				sup.put(count, sup_code);
				purorder_no = SystemTool.getInstance().getNo("ALL", "IND",
						"IND_PURORDER", "No");
				pur_no.put(sup_code, purorder_no);
			} else {
				if (sup.containsValue(sup_code)) {
					continue;
				}
				purorder_no = SystemTool.getInstance().getNo("ALL", "IND",
						"IND_PURORDER", "No");
				sup.put(count, sup_code);
				pur_no.put(sup_code, purorder_no);
			}
			count++;
		}
		// ���ؽ����
		TParm result = new TParm();
		// ����������
		TParm inparmM = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		// ��������������
		for (int i = 0; i < sup.size(); i++) {
			inparmM.setData("PURORDER_NO", pur_no.get(sup.get(i)));
			inparmM.setData("PURORDER_DATE", parmM.getData("PURORDER_DATE"));
			inparmM.setData("ORG_CODE", parmM.getData("ORG_CODE"));
			inparmM.setData("SUP_CODE", sup.get(i));
			inparmM.setData("RES_DELIVERY_DATE", tnull);
			inparmM.setData("REASON_CHN_DESC", "");
			inparmM.setData("DESCRIPTION", "");
			inparmM.setData("PLAN_NO", parmM.getData("PLAN_NO"));
			inparmM.setData("OPT_USER", parmM.getData("OPT_USER"));
			inparmM.setData("OPT_DATE", parmM.getData("OPT_DATE"));
			inparmM.setData("OPT_TERM", parmM.getData("OPT_TERM"));
			inparmM.setData("REGION_CODE", parmM.getData("REGION_CODE"));
			// System.out.println("M--> " + inparmM);
			result = IndPurorderMTool.getInstance().onInsert(inparmM, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// ������ϸ��
		TParm inparmD = new TParm();
		// ����������ϸ��
		int seq = 0;
		for (int i = 0; i < parmD.getCount("SUP_CODE"); i++) {
			if (i > 0) {
				String oldValue = parmD.getValue("SUP_CODE", i - 1);
				String newValue = parmD.getValue("SUP_CODE", i);
				if (oldValue.equals(newValue)) {
					seq++;
				} else {
					seq = 0;
				}
			}
			inparmD.setData("PURORDER_NO", pur_no.get(parmD.getValue(
					"SUP_CODE", i)));
			inparmD.setData("SEQ_NO", seq);
			inparmD.setData("ORDER_CODE", parmD.getValue("ORDER_CODE", i));
			inparmD.setData("PURORDER_QTY", parmD.getValue("CREATE_QTY", i));
			inparmD.setData("GIFT_QTY", 0);
			inparmD.setData("BILL_UNIT", parmD.getValue("PURCH_UNIT", i));
			inparmD.setData("PURORDER_PRICE", parmD.getValue("CONTRACT_PRICE",
					i));
			inparmD.setData("ACTUAL_QTY", 0);
			inparmD.setData("QUALITY_DEDUCT_AMT", 0);
			inparmD.setData("UPDATE_FLG", "0");
			inparmD.setData("OPT_USER", parmM.getData("OPT_USER"));
			inparmD.setData("OPT_DATE", parmM.getData("OPT_DATE"));
			inparmD.setData("OPT_TERM", parmM.getData("OPT_TERM"));
			// System.out.println("D--> " + inparmD);
			result = IndPurorderDTool.getInstance().onInsert(inparmD, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		// ���²ɹ��ƻ�״̬
		TParm plan_parm = new TParm();
		plan_parm.setData("PLAN_NO", parmM.getValue("PLAN_NO"));
		plan_parm.setData("ORG_CODE", parmM.getValue("ORG_CODE"));
		plan_parm.setData("CREATE_FLG", "Y");
		plan_parm.setData("OPT_USER", parmM.getData("OPT_USER"));
		plan_parm.setData("OPT_DATE", parmM.getData("OPT_DATE"));
		plan_parm.setData("OPT_TERM", parmM.getData("OPT_TERM"));
		result = IndPurPlanMTool.getInstance().onUpdateCreateFlg(plan_parm,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �������յ�
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertVerify(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// ��Ӧ����
		String sup_code = parm.getValue("SUP_CODE");
		// ���տ���
		String org_code = parm.getValue("ORG_CODE");
		// �����
		TParm result = new TParm();

		/** �������յ����� */
		Map map = new HashMap();
		for (int i = 0; i < parm.getCount("VERIFYIN"); i++) {
			if (map.containsKey(parm.getData("VERIFYIN", i))
					|| map.containsValue(parm.getData("VERIFYIN", i))) {
				continue;
			}
			parm.setData("VERIFYIN_NO", parm.getData("VERIFYIN", i));
			map.put(parm.getData("VERIFYIN", i), parm.getData("VERIFYIN", i));
			result = IndVerifyinMTool.getInstance().onInsert(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		/** �������յ�ϸ��,���������������ϸ״̬ */
		Object object = parm.getData("UPDATE_SQL");
		if (object == null) {
			return null;
		}
		String[] updateSQL = (String[]) object;
		for (int i = 0; i < updateSQL.length; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSQL[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		/** ���뵥���Զ�ά�� */
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// System.out.println("---���뵥���Զ�ά��----------"+reuprice_flg);
		/**************************** by liyh 20120906 �Զ�ά����Ӧ�̴��������Ϣ start ********************************/
		if (null != parm && parm.getCount("ORDER_CODE") > 0
				&& "Y".equals(reuprice_flg)) {
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				String orderCodeS = parm.getValue("ORDER_CODE", i);
				String purOrderNoS = parm.getValue("PURORDER_NO", i);
				String sql = INDSQL.getPurOrderDByNoAndOrder(purOrderNoS,
						orderCodeS);
				// System.out.println("---------sql :"+sql);
				TParm purTParm = new TParm(TJDODBTool.getInstance().select(sql));
				// ����ʱ��
				String purDate = "";
				// ��������
				double purPrice = 0;
				// ��������
				double purQty = 0;
				if (null != purTParm && purTParm.getCount() > 0) {
					purDate = purTParm.getValue("OPT_DATE", 0);
					if (null != purDate && purDate.length() > 19) {
						purDate = purDate.substring(0, 19);
					}
					purPrice = purTParm.getDouble("PURORDER_PRICE", 0);
					purQty = purTParm.getDouble("PURORDER_QTY", 0);
				}

				inparm.setData("LAST_ORDER_DATE", purDate);
				inparm.setData("LAST_ORDER_QTY", purQty);
				// �ϴζ�������
				inparm.setData("LAST_ORDER_PRICE", purPrice
						/ parm.getDouble("DOSAGE_QTY", i)
						* parm.getDouble("STOCK_QTY", i));
				// �ϴ����յ���
				// inparm.setData("LAST_VERIFY_PRICE",
				// parm.getDouble("VERIFYIN_PRICE", i) *
				// parm.getDouble("DOSAGE_QTY", i) * parm.getDouble("STOCK_QTY",
				// i));
				inparm.setData("LAST_VERIFY_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i)
						/ parm.getDouble("DOSAGE_QTY", i)
						* parm.getDouble("STOCK_QTY", i));
				inparm
						.setData("LAST_ORDER_NO", parm.getValue("PURORDER_NO",
								i));
				// inparm.setData("CONTRACT_PRICE",
				// parm.getDouble("VERIFYIN_PRICE", i) *
				// parm.getDouble("DOSAGE_QTY", i) * parm.getDouble("STOCK_QTY",
				// i));
				inparm.setData("CONTRACT_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i)
						* parm.getDouble("STOCK_QTY", i));
				inparm.setData("SUP_CODE", sup_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				String updateSql = INDSQL.updateAgent(inparm);
				// System.out.println("----------updateSql: "+updateSql);
				// result =
				// IndAgentTool.getInstance().onUpdateContractPrice(inparm,
				// conn);
				/**************************** by liyh 20120906 �Զ�ά����Ӧ�̴��������Ϣ end ********************************/
				result = new TParm(TJDODBTool.getInstance().update(updateSql,
						conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		// fux modify 20141031
		// �鿴ind_stockm�Ƿ�������,����ޣ�����(ȷ���Ƿ����)
		if (null != parm && parm.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				String orderCode = parm.getValue("ORDER_CODE", i);
				String sqlStockM = INDSQL.getVerInStockM(orderCode, org_code);
				TParm StockmParm = new TParm(TJDODBTool.getInstance().select(
						sqlStockM));
				if (null == StockmParm || StockmParm.getCount() < 0) {
					StockmParm.setData("ORDER_CODE", orderCode);
					StockmParm.setData("ORG_CODE", org_code);
					StockmParm.setData("REGION_CODE", "H01");
					StockmParm.setData("OPT_USER", parm.getValue("OPT_USER"));
					StockmParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
					String insertSql = INDSQL.insertStockM(StockmParm);
					result = new TParm(TJDODBTool.getInstance().update(
							insertSql, conn));
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}

		}

		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ��ⲿ�š����������Ч�����Ž��п����������� */
			String ui_flg = "I";
			// �����������(��ҩ��λ)
			double in_qty = 0;
			// ��������(������λ)
			double v_qty = 0;
			// ��������(������λ)
			double g_qty = 0;
			// ����ת����
			double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.0000;
			// ���ռ۸�
			double verifyin = 0.0000;
			// �����((���������+ ������) * ���ۼ�)
			double in_amt = 0.00;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				ui_flg = parm.getValue("UI_FLG", i);
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round((v_qty + g_qty) * d_qty, 2);
				inparm.setData("IN_QTY", in_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
				inparm.setData("IN_AMT", in_amt);
				inparm.setData("STOCK_QTY", in_qty);
				inparm.setData("VERIFYIN_QTY", in_qty);
				inparm.setData("VERIFYIN_AMT", in_amt);
				inparm.setData("VERIFYIN_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
				inparm.setData("FAVOR_QTY", StringTool.round(g_qty * d_qty, 2));
				if ("U".equals(ui_flg)) {
					// ����ָ�������
					result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
							conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				} else {
					// ���������
					inparm.setData("BATCH_NO", parm.getValue("BATCH_NO", i));
					inparm.setData("VALID_DATE", parm.getData("VALID_DATE", i));
					inparm.setData("REGION_CODE", "");
					inparm.setData("MATERIAL_LOC_CODE", parm.getData(
							"MATERIAL_LOC_CODE", i));
					inparm.setData("ACTIVE_FLG", "Y");
					inparm.setData("STOCK_FLG", "N");
					inparm.setData("READJUSTP_FLG", "N");
					inparm.setData("LAST_TOTSTOCK_QTY", 0);
					inparm.setData("LAST_TOTSTOCK_AMT", 0);
					inparm.setData("OUT_QTY", 0);
					inparm.setData("OUT_AMT", 0);
					inparm.setData("CHECKMODI_QTY", 0);
					inparm.setData("CHECKMODI_AMT", 0);
					inparm.setData("REGRESSGOODS_QTY", 0);
					inparm.setData("REGRESSGOODS_AMT", 0);
					inparm.setData("DOSEAGE_QTY", 0);
					inparm.setData("DOSAGE_AMT", 0);
					inparm.setData("REGRESSDRUG_QTY", 0);
					inparm.setData("REGRESSDRUG_AMT", 0);
					inparm.setData("FREEZE_TOT", 0);
					inparm.setData("PROFIT_LOSS_AMT", 0);
					inparm.setData("STOCKIN_QTY", 0);
					inparm.setData("STOCKIN_AMT", 0);
					inparm.setData("STOCKOUT_QTY", 0);
					inparm.setData("STOCKOUT_AMT", 0);
					inparm.setData("REQUEST_IN_QTY", 0);
					inparm.setData("REQUEST_IN_AMT", 0);
					inparm.setData("REQUEST_OUT_QTY", 0);
					inparm.setData("REQUEST_OUT_AMT", 0);
					inparm.setData("GIF_IN_QTY", 0);
					inparm.setData("GIF_IN_AMT", 0);
					inparm.setData("GIF_OUT_QTY", 0);
					inparm.setData("GIF_OUT_AMT", 0);
					inparm.setData("RET_IN_QTY", 0);
					inparm.setData("RET_IN_AMT", 0);
					inparm.setData("RET_OUT_QTY", 0);
					inparm.setData("RET_OUT_AMT", 0);
					inparm.setData("WAS_OUT_QTY", 0);
					inparm.setData("WAS_OUT_AMT", 0);
					inparm.setData("THO_OUT_QTY", 0);
					inparm.setData("THO_OUT_AMT", 0);
					inparm.setData("THI_IN_QTY", 0);
					inparm.setData("THI_IN_AMT", 0);
					inparm.setData("COS_OUT_QTY", 0);
					inparm.setData("COS_OUT_AMT", 0);
					inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
					inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
					inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
					inparm.setData("SUP_CODE", parm.getValue("SUP_CODE", i));
					inparm.setData("SUP_ORDER_CODE", parm.getValue(
							"SUP_ORDER_CODE", i));
					result = IndStockDTool.getInstance().onInsert(inparm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}

			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// �ۼ�������
			double actual = 0;
			// �ƻ�������
			double purorder_qty = 0;
			// �ۼ�Ʒ�ʿۿ�
			double deduct_atm = 0;
			for (int i = 0; i < parm.getCount("PURORDER_NO"); i++) {
				TParm inparm = new TParm();
				// ��������
				inparm.setData("PURORDER_NO", parm.getValue("PURORDER_NO", i));
				// ���������
				inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i));
				// ���������
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				inparm.setData("VERIFYIN_QTY", v_qty);
				// �ۼ������ۿ���
				deduct_atm = parm.getDouble("QUALITY_DEDUCT_AMT", i);
				inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
				// �ۼ�������
				actual = parm.getDouble("ACTUAL_QTY", i);
				// �ƻ�������
				purorder_qty = parm.getDouble("PURORDER_QTY", i);
				// ״̬
				if (purorder_qty == v_qty + actual) {
					inparm.setData("UPDATE_FLG", "3");
				} else {
					inparm.setData("UPDATE_FLG", "1");
				}
				result = IndPurorderDTool.getInstance().onUpdateVer(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** �ƶ���Ȩƽ���ɱ� */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				// zhangyong20091015 �޸�Ϊ���յ���
				verifyin = parm.getDouble("VERIFYIN_PRICE", i);
				in_amt = StringTool
						.round((v_qty + g_qty) * d_qty * verifyin, 2);
				in_qty = StringTool.round((v_qty + g_qty) * d_qty, 2);
				double stock_price = getPhaBaseStockPrice(parm.getValue(
						"ORDER_CODE", i), in_amt, in_qty);
				// System.out.println("�ƶ���Ȩƽ���ɱ�:" + stock_price);
				TParm stock_price_parm = new TParm();
				stock_price_parm.setData("ORDER_CODE", parm.getValue(
						"ORDER_CODE", i));
				stock_price_parm.setData("STOCK_PRICE", stock_price);
				result = PhaBaseTool.getInstance().onUpdateStockPrice(
						stock_price_parm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** �����۸��� */
			if ("Y".equals(parm.getValue("UPDATE_TRADE_PRICE"))) {
				for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
					double verfyin_price = parm.getDouble("VERIFYIN_PRICE", i);
					TParm trade_price_parm = new TParm();
					trade_price_parm.setData("ORDER_CODE", parm.getValue(
							"ORDER_CODE", i));
					trade_price_parm.setData("TRADE_PRICE", verfyin_price);
					result = PhaBaseTool.getInstance().onUpdateTradePrice(
							trade_price_parm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}

			/** ��;������ */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				// ������
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round(v_qty * (-1.00), 2);
				inparm.setData("BUY_UNRECEIVE_QTY", in_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		/** �ƻ����Ÿ��� */

		return result;
	}

	/**
	 * �������յ� HIS
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 * @throws InterruptedException
	 */
	public TParm onUpdateVerifyInd(TParm parm, TConnection conn)
			throws InterruptedException {
		// System.out.println("VER_PARM: " + parm);
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���տ���
		String org_code = parm.getValue("ORG_CODE");
		// ����������Ϣ
		result = IndVerifyinMTool.getInstance().onUpdate(parm, conn);

		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// �ж����״̬
		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ��ⲿ�š����������Ч�����Ž��п����������� */
			String ui_flg = "I";
			// �����������(��ҩ��λ)
			double in_qty = 0;
			// ��������(������λ)
			double v_qty = 0;
			// ��������(������λ)
			double g_qty = 0;
			// ����ת����
			double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.00;
			// ���ռ۸�
			double verifyin = 0.0000;
			// �����((���������+ ������) * ���ۼ�)
			double in_amt = 0.00;
			String order_code = "";
			String supCode = parm.getValue("SUP_CODE");
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				ui_flg = parm.getValue("UI_FLG", i);
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				order_code = parm.getValue("ORDER_CODE", i);
				inparm.setData("ORDER_CODE", order_code);
				inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round((v_qty + g_qty) * d_qty, 2);
				inparm.setData("IN_QTY", in_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				// zhangyong20091015 ȥ��d_qty
				in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
				inparm.setData("IN_AMT", in_amt);
				inparm.setData("STOCK_QTY", in_qty);
				inparm.setData("VERIFYIN_QTY", in_qty);
				inparm.setData("VERIFYIN_AMT", in_amt);
				inparm.setData("VERIFYIN_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
				inparm.setData("INVENT_PRICE", parm
						.getDouble("INVENT_PRICE", i));
				inparm.setData("FAVOR_QTY", StringTool.round(g_qty, 2));
				retail = StringTool.round(retail / d_qty, 4);
				inparm.setData("RETAIL_PRICE", retail);
				double profit_loss_amt = 0;
				if ("U".equals(ui_flg)) {
					String sql = "SELECT STOCK_QTY, RETAIL_PRICE FROM IND_STOCK WHERE ORG_CODE = '"
							+ org_code
							+ "' AND ORDER_CODE = '"
							+ order_code
							+ "' AND BATCH_SEQ = "
							+ parm.getValue("BATCH_SEQ", i);
					TParm retailPriceParm = new TParm(TJDODBTool.getInstance()
							.select(sql));
					profit_loss_amt = (retail - retailPriceParm.getDouble(
							"RETAIL_PRICE", 0))
							* retailPriceParm.getDouble("STOCK_QTY", 0);

					inparm.setData("PROFIT_LOSS_AMT", profit_loss_amt);
					// ����ָ�������
					result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
							conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				} else {
					// ���������
					String batch_no = parm.getValue("BATCH_NO", i);
					inparm.setData("BATCH_NO", batch_no);
					String valid = parm.getValue("VALID_DATE", i);
					inparm.setData("VALID_DATE", StringTool.getTimestamp(valid,
							"yyyy/MM/dd"));
					inparm.setData("MATERIAL_LOC_CODE", parm.getValue(
							"MATERIAL_LOC_CODE", i));
					inparm.setData("ACTIVE_FLG", "Y");
					inparm.setData("STOCK_FLG", "N");
					inparm.setData("READJUSTP_FLG", "N");
					inparm.setData("LAST_TOTSTOCK_QTY", 0);
					inparm.setData("LAST_TOTSTOCK_AMT", 0);
					inparm.setData("OUT_QTY", 0);
					inparm.setData("OUT_AMT", 0);
					inparm.setData("CHECKMODI_QTY", 0);
					inparm.setData("CHECKMODI_AMT", 0);
					inparm.setData("REGRESSGOODS_QTY", 0);
					inparm.setData("REGRESSGOODS_AMT", 0);
					inparm.setData("DOSEAGE_QTY", 0);
					inparm.setData("DOSAGE_AMT", 0);
					inparm.setData("REGRESSDRUG_QTY", 0);
					inparm.setData("REGRESSDRUG_AMT", 0);
					inparm.setData("FREEZE_TOT", 0);
					inparm.setData("PROFIT_LOSS_AMT", 0);
					inparm.setData("STOCKIN_QTY", 0);
					inparm.setData("STOCKIN_AMT", 0);
					inparm.setData("STOCKOUT_QTY", 0);
					inparm.setData("STOCKOUT_AMT", 0);
					inparm.setData("REQUEST_IN_QTY", 0);
					inparm.setData("REQUEST_IN_AMT", 0);
					inparm.setData("REQUEST_OUT_QTY", 0);
					inparm.setData("REQUEST_OUT_AMT", 0);
					inparm.setData("GIF_IN_QTY", 0);
					inparm.setData("GIF_IN_AMT", 0);
					inparm.setData("GIF_OUT_QTY", 0);
					inparm.setData("GIF_OUT_AMT", 0);
					inparm.setData("RET_IN_QTY", 0);
					inparm.setData("RET_IN_AMT", 0);
					inparm.setData("RET_OUT_QTY", 0);
					inparm.setData("RET_OUT_AMT", 0);
					inparm.setData("WAS_OUT_QTY", 0);
					inparm.setData("WAS_OUT_AMT", 0);
					inparm.setData("THO_OUT_QTY", 0);
					inparm.setData("THO_OUT_AMT", 0);
					inparm.setData("THI_IN_QTY", 0);
					inparm.setData("THI_IN_AMT", 0);
					inparm.setData("COS_OUT_QTY", 0);
					inparm.setData("COS_OUT_AMT", 0);
					inparm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
					inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
					inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
					inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
					inparm.setData("RETAIL_PRICE", retail);
					inparm.setData("SUP_CODE", parm.getValue("SUP_CODE"));
					inparm.setData("SUP_ORDER_CODE", parm.getValue(
							"SUP_ORDER_CODE", i));
					result = IndStockDTool.getInstance().onInsert(inparm, conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}

			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// �ۼ������� ע�͵����¶������ԭ���ǹ�ҩ�����պͶ���û�б�Ȼ��ϵ ������ ֻ�ǳ�����һ������ by liyh 20121219
			double actual = 0;
			// �ƻ�������
			double purorder_qty = 0;
			// �ۼ�Ʒ�ʿۿ�
			double deduct_atm = 0;
			for (int i = 0; i < parm.getCount("PURORDER_NO"); i++) {
				TParm inparm = new TParm();
				// ��������
				inparm.setData("PURORDER_NO", parm.getValue("PURORDER_NO", i));
				// ���������
				inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i));
				// ���������
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				inparm.setData("VERIFYIN_QTY", v_qty);
				// �ۼ������ۿ���
				deduct_atm = parm.getDouble("QUALITY_DEDUCT_AMT", i);
				inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
				// �ۼ�������
				actual = parm.getDouble("ACTUAL_QTY", i);
				// �ƻ�������
				purorder_qty = parm.getDouble("PURORDER_QTY", i);
				// ״̬
				if (purorder_qty == v_qty + actual) {
					inparm.setData("UPDATE_FLG", "3");
				} else {
					inparm.setData("UPDATE_FLG", "1");
				}
				result = IndPurorderDTool.getInstance().onUpdateVer(inparm,
						conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}

			/** �ƶ���Ȩƽ���ɱ� */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				// zhangyong20091015 �޸�Ϊ���յ���
				verifyin = parm.getDouble("VERIFYIN_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * s_qty * d_qty
						* verifyin, 2);
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				double stock_price = getPhaBaseStockPrice(parm.getValue(
						"ORDER_CODE", i), in_amt, in_qty);
				// System.out.println("�ƶ���Ȩƽ���ɱ�:" + stock_price);
				TParm stock_price_parm = new TParm();
				stock_price_parm.setData("ORDER_CODE", parm.getValue(
						"ORDER_CODE", i));
				stock_price_parm.setData("STOCK_PRICE", stock_price);
				result = PhaBaseTool.getInstance().onUpdateStockPrice(
						stock_price_parm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}

			/** �����۸��� */
			if ("Y".equals(parm.getValue("UPDATE_TRADE_PRICE"))) {
				for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
					double verfyin_price = parm.getDouble("VERIFYIN_PRICE", i);
					TParm trade_price_parm = new TParm();
					trade_price_parm.setData("ORDER_CODE", parm.getValue(
							"ORDER_CODE", i));
					trade_price_parm.setData("TRADE_PRICE", verfyin_price);
					result = PhaBaseTool.getInstance().onUpdateTradePrice(
							trade_price_parm, conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}

			/** ��;������ */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				order_code = parm.getValue("ORDER_CODE", i);
				inparm.setData("ORDER_CODE", order_code);
				// ������
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round(v_qty * s_qty * (-1.00), 2);
				inparm.setData("BUY_UNRECEIVE_QTY", in_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}

			// //fux modify 20160311
			// try {
			// Thread.sleep(20000);
			// } catch (Exception e) {
			// // TODO: handle exception
			// conn.rollback();
			// conn.close();
			// }

			// ���� 10000ms 10��

			/** ���������������ϸ״̬ */
			Object object = parm.getData("UPDATE_SQL");
			if (object == null) {
				conn.rollback();
				conn.close();
				return null;
			}
			String[] updateSQL = (String[]) object;
			for (int i = 0; i < updateSQL.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						updateSQL[i], conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * �������յ�
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 * @throws InterruptedException
	 */
	public TParm onUpdateVerify(TParm parm, TConnection conn)
			throws InterruptedException {

		// long startTime = System.currentTimeMillis();
		// //dwr.a();
		// long endTime = System.currenTimeMillis();
		// if(endTime - statreTime > 100000){break;}

		// System.out.println("VER_PARM: " + parm);
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���տ���
		String org_code = parm.getValue("ORG_CODE");
		// ����������Ϣ
		result = IndVerifyinMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// �ж����״̬
		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ��ⲿ�š����������Ч�����Ž��п����������� */
			String ui_flg = "I";
			// �����������(��ҩ��λ)
			double in_qty = 0;
			// ��������(������λ)
			double v_qty = 0;
			// ��������(������λ)
			double g_qty = 0;
			// ����ת����
			double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.00;
			// ���ռ۸�
			double verifyin = 0.0000;
			// �����((���������+ ������) * ���ۼ�)
			double in_amt = 0.00;
			String order_code = "";
			String supCode = parm.getValue("SUP_CODE");
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				ui_flg = parm.getValue("UI_FLG", i);
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				order_code = parm.getValue("ORDER_CODE", i);
				inparm.setData("ORDER_CODE", order_code);
				inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				inparm.setData("IN_QTY", in_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				// zhangyong20091015 ȥ��d_qty
				in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
				inparm.setData("IN_AMT", in_amt);
				inparm.setData("STOCK_QTY", in_qty);
				inparm.setData("VERIFYIN_QTY", in_qty);
				inparm.setData("VERIFYIN_AMT", in_amt);
				inparm.setData("VERIFYIN_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
				inparm.setData("INVENT_PRICE", parm
						.getDouble("INVENT_PRICE", i));
				inparm.setData("FAVOR_QTY", StringTool.round(g_qty * s_qty, 2));
				retail = StringTool.round(retail / s_qty / d_qty, 4);
				inparm.setData("RETAIL_PRICE", retail);
				double profit_loss_amt = 0;
				if ("U".equals(ui_flg)) {
					String sql = "SELECT STOCK_QTY, RETAIL_PRICE FROM IND_STOCK WHERE ORG_CODE = '"
							+ org_code
							+ "' AND ORDER_CODE = '"
							+ order_code
							+ "' AND BATCH_SEQ = "
							+ parm.getValue("BATCH_SEQ", i);
					TParm retailPriceParm = new TParm(TJDODBTool.getInstance()
							.select(sql));
					profit_loss_amt = (retail - retailPriceParm.getDouble(
							"RETAIL_PRICE", 0))
							* retailPriceParm.getDouble("STOCK_QTY", 0);
					inparm.setData("PROFIT_LOSS_AMT", profit_loss_amt);
					// ����ָ�������
					result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
							conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				} else {
					// ���������
					String batch_no = parm.getValue("BATCH_NO", i);
					inparm.setData("BATCH_NO", batch_no);
					String valid = parm.getValue("VALID_DATE", i);
					inparm.setData("VALID_DATE", StringTool.getTimestamp(valid,
							"yyyy/MM/dd"));
					inparm.setData("MATERIAL_LOC_CODE", parm.getValue(
							"MATERIAL_LOC_CODE", i));
					inparm.setData("ACTIVE_FLG", "Y");
					inparm.setData("STOCK_FLG", "N");
					inparm.setData("READJUSTP_FLG", "N");
					inparm.setData("LAST_TOTSTOCK_QTY", 0);
					inparm.setData("LAST_TOTSTOCK_AMT", 0);
					inparm.setData("OUT_QTY", 0);
					inparm.setData("OUT_AMT", 0);
					inparm.setData("CHECKMODI_QTY", 0);
					inparm.setData("CHECKMODI_AMT", 0);
					inparm.setData("REGRESSGOODS_QTY", 0);
					inparm.setData("REGRESSGOODS_AMT", 0);
					inparm.setData("DOSEAGE_QTY", 0);
					inparm.setData("DOSAGE_AMT", 0);
					inparm.setData("REGRESSDRUG_QTY", 0);
					inparm.setData("REGRESSDRUG_AMT", 0);
					inparm.setData("FREEZE_TOT", 0);
					inparm.setData("PROFIT_LOSS_AMT", 0);
					inparm.setData("STOCKIN_QTY", 0);
					inparm.setData("STOCKIN_AMT", 0);
					inparm.setData("STOCKOUT_QTY", 0);
					inparm.setData("STOCKOUT_AMT", 0);
					inparm.setData("REQUEST_IN_QTY", 0);
					inparm.setData("REQUEST_IN_AMT", 0);
					inparm.setData("REQUEST_OUT_QTY", 0);
					inparm.setData("REQUEST_OUT_AMT", 0);
					inparm.setData("GIF_IN_QTY", 0);
					inparm.setData("GIF_IN_AMT", 0);
					inparm.setData("GIF_OUT_QTY", 0);
					inparm.setData("GIF_OUT_AMT", 0);
					inparm.setData("RET_IN_QTY", 0);
					inparm.setData("RET_IN_AMT", 0);
					inparm.setData("RET_OUT_QTY", 0);
					inparm.setData("RET_OUT_AMT", 0);
					inparm.setData("WAS_OUT_QTY", 0);
					inparm.setData("WAS_OUT_AMT", 0);
					inparm.setData("THO_OUT_QTY", 0);
					inparm.setData("THO_OUT_AMT", 0);
					inparm.setData("THI_IN_QTY", 0);
					inparm.setData("THI_IN_AMT", 0);
					inparm.setData("COS_OUT_QTY", 0);
					inparm.setData("COS_OUT_AMT", 0);
					inparm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
					inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
					inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
					inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
					inparm.setData("RETAIL_PRICE", retail);
					inparm.setData("SUP_CODE", parm.getValue("SUP_CODE"));
					inparm.setData("SUP_ORDER_CODE", parm.getValue(
							"SUP_ORDER_CODE", i));
					result = IndStockDTool.getInstance().onInsert(inparm, conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}

			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// �ۼ������� ע�͵����¶������ԭ���ǹ�ҩ�����պͶ���û�б�Ȼ��ϵ ������ ֻ�ǳ�����һ������ by liyh 20121219
			/*
			 * double actual = 0; // �ƻ������� double purorder_qty = 0; // �ۼ�Ʒ�ʿۿ�
			 * double deduct_atm = 0; for (int i = 0; i <
			 * parm.getCount("PURORDER_NO"); i++) { TParm inparm = new TParm();
			 * // �������� inparm.setData("PURORDER_NO",
			 * parm.getValue("PURORDER_NO", i)); // ���������
			 * inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i)); // ���������
			 * v_qty = parm.getDouble("VERIFYIN_QTY", i);
			 * inparm.setData("VERIFYIN_QTY", v_qty); // �ۼ������ۿ��� deduct_atm =
			 * parm.getDouble("QUALITY_DEDUCT_AMT", i);
			 * inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm); // �ۼ������� actual
			 * = parm.getDouble("ACTUAL_QTY", i); // �ƻ������� purorder_qty =
			 * parm.getDouble("PURORDER_QTY", i); // ״̬ if (purorder_qty ==
			 * v_qty + actual) { inparm.setData("UPDATE_FLG", "3"); } else {
			 * inparm.setData("UPDATE_FLG", "1"); } result =
			 * IndPurorderDTool.getInstance().onUpdateVer(inparm, conn); if
			 * (result.getErrCode() < 0) { return result; } }
			 */

			/** �ƶ���Ȩƽ���ɱ� */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				// zhangyong20091015 �޸�Ϊ���յ���
				verifyin = parm.getDouble("VERIFYIN_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * s_qty * d_qty
						* verifyin, 2);
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				double stock_price = getPhaBaseStockPrice(parm.getValue(
						"ORDER_CODE", i), in_amt, in_qty);
				// System.out.println("�ƶ���Ȩƽ���ɱ�:" + stock_price);
				TParm stock_price_parm = new TParm();
				stock_price_parm.setData("ORDER_CODE", parm.getValue(
						"ORDER_CODE", i));
				stock_price_parm.setData("STOCK_PRICE", stock_price);
				result = PhaBaseTool.getInstance().onUpdateStockPrice(
						stock_price_parm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}

			/** �����۸��� */
			if ("Y".equals(parm.getValue("UPDATE_TRADE_PRICE"))) {
				for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
					double verfyin_price = parm.getDouble("VERIFYIN_PRICE", i);
					TParm trade_price_parm = new TParm();
					trade_price_parm.setData("ORDER_CODE", parm.getValue(
							"ORDER_CODE", i));
					trade_price_parm.setData("TRADE_PRICE", verfyin_price);
					result = PhaBaseTool.getInstance().onUpdateTradePrice(
							trade_price_parm, conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}

			/** ��;������ */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				order_code = parm.getValue("ORDER_CODE", i);
				inparm.setData("ORDER_CODE", order_code);
				// ������
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round(v_qty * s_qty * (-1.00), 2);
				inparm.setData("BUY_UNRECEIVE_QTY", in_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}

			/** ���������������ϸ״̬ */
			Object object = parm.getData("UPDATE_SQL");
			if (object == null) {
				conn.rollback();
				conn.close();
				return result;
			}
			String[] updateSQL = (String[]) object;
			for (int i = 0; i < updateSQL.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						updateSQL[i], conn));
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		}

		return result;
	}

	/**
	 * ������龫�Զ���������ͳ��ⵥ
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onSaveReqeustAndDispense(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		String region = parm.getValue("REGION_CODE");
		String orgCode = parm.getValue("ORG_CODE");
		Timestamp optDate = parm.getTimestamp("OPT_DATE");
		// �õ�ҩ�Ⲧ������ ���龫��ⲿ�� ����ǰ̨
		// TParm sysParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		// sysParm.getValue("TOXIC_STORAGE_ORG", 0);
		String appOrgCode = parm.getValue("DRUG_TO_ORG_CODE");
		// �õ����쵥��
		String requestNo = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		// �õ���������PARM
		TParm requestM = getRequestMAutoOfDrugInfo(parm, requestNo, appOrgCode);
		// System.out.println("------------requestM: " + requestM);
		// ������������
		// System.out.println("------------requestM--sql : " +
		// SPCSQL.saveRequestMAutoOfDrug(requestM));
		result = new TParm(TJDODBTool.getInstance().update(
				SPCSQL.saveRequestMAutoOfDrug(requestM), conn));
		if (result.getErrCode() < 0) {
			return result;
		}
		int seqNo = 1;
		// ����������ϸ��
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			String orderCode = parm.getValue("ORDER_CODE", i);
			double qty = parm.getDouble("VERIFYIN_QTY", i);
			seqNo = seqNo + i;
			// �õ�������ϸ��PARM
			TParm requestD = getRequestDAutoOfDrugInfo(requestNo, seqNo + "",
					orderCode, qty, optUser, optTerm, region);
			// System.out.println("------------requestD: " + requestD);
			// ����
			// System.out.println("------------requestD--sql : " +
			// SPCSQL.saveRequestDAutoOfDrug(requestD));
			result = new TParm(TJDODBTool.getInstance().update(
					SPCSQL.saveRequestDAutoOfDrug(requestD), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// ���ɳ�������
		String dispenseNo = parm.getValue("DISPENSE_NO");
		TParm dispsenseM = geDispsenseMAutoOfDrugInfo(parm, dispenseNo,
				requestNo, appOrgCode);
		// System.out.println("------------dispsenseM: " + dispsenseM);
		// ����
		// System.out.println("------------dispsenseM sql: " +
		// SPCSQL.saveDispsenseMAutoOfDrug(dispsenseM));
		result = new TParm(TJDODBTool.getInstance().update(
				SPCSQL.saveDispsenseMAutoOfDrug(dispsenseM), conn));
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���ɳ�����ϸ��
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			String orderCode = parm.getValue("ORDER_CODE", i);
			double qty = parm.getDouble("VERIFYIN_QTY", i);
			String batchSeq = parm.getValue("BATCH_SEQ", i);
			String supOrderCode = parm.getValue("SUP_ORDER_CODE", i);
			seqNo = seqNo + i;
			// �õ�������ϸ��PARM
			TParm requestD = getDispsenseDAutoOfDrugInfo(dispenseNo,
					seqNo + "", orgCode, orderCode, qty, batchSeq, parm,
					supOrderCode);
			// System.out.println("------------requestD: " + requestD);
			// ����
			// System.out.println("------------requestD sql:  " +
			// SPCSQL.saveDispsenseDAutoOfDrug(requestD));
			result = new TParm(TJDODBTool.getInstance().update(
					SPCSQL.saveDispsenseDAutoOfDrug(requestD), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		// ���ɳ�����ϸ��
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			String orderCode = parm.getValue("ORDER_CODE", i);
			double qty = parm.getDouble("VERIFYIN_QTY", i)
					* parm.getDouble("DOSAGE_QTY", i);
			String batchSeq = parm.getValue("BATCH_SEQ", i);
			seqNo = seqNo + i;
			// �õ�������ϸ��PARM
			TParm stockM = getStockDParm(orgCode, orderCode, qty, batchSeq);
			// System.out.println("------------requestD: " + stockM);
			// ����
			// System.out.println("------------requestD sql:  " +
			// SPCSQL.updateStockByRequest(stockM));
			// ���¿��
			result = new TParm(TJDODBTool.getInstance().update(
					SPCSQL.updateStockByRequest(stockM)));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// ֱ�����
		String inOrg = parm.getValue("DRUG_TO_ORG_CODE");
		String orderCode = "";
		String unitType = "0";
		double dosageQty = 0;
		double retailPrice = 0;
		double verifyinPrice = 0;
		int batchSeq = 0;
		List list = new ArrayList();
		/*
		 * for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) { orderCode =
		 * parm.getValue("ORDER_CODE",i); list.add(orderCode);
		 * if("0".equals(unitType)) { dosageQty =
		 * parm.getDouble("INSTORE_QTY",i)*parm.getDouble("DOSAGE_QTY",i); }else
		 * { dosageQty = parm.getDouble("INSTORE_QTY",i); } batchSeq =
		 * parm.getInt("BATCH_SEQ",i); retailPrice =
		 * parm.getDouble("RETAIL_PRICE",i)/parm.getDouble("DOSAGE_QTY",i);
		 * verifyinPrice =
		 * parm.getDouble("VERIFYIN_PRICE",i)/parm.getDouble("DOSAGE_QTY",i);
		 * String vailDate = parm.getValue("VALID_DATE",i).replace('/',
		 * '-').substring(0, 10); String batchNo = parm.getValue("BATCH_NO",i);
		 * // String oUser = parm.getValue("OPT_USER",i); // Timestamp optDate =
		 * parm.getTimestamp("OPT_DATE",i); // String oTerm =
		 * parm.getValue("OPT_TERM",i); String supCode =
		 * parm.getValue("SUP_CODE",i); double inventPrice =
		 * parm.getDouble("INVENT_PRICE", i); String supOrderCode =
		 * parm.getValue("SUP_ORDER_CODE",i); result =
		 * IndStockDTool.getInstance().onUpdateStockByBatchVaildInToxic("DEP",
		 * inOrg, orderCode, batchSeq, vailDate, batchNo, dosageQty,
		 * retailPrice,verifyinPrice, optUser, optDate,
		 * optTerm,supCode,supOrderCode,inventPrice, list, conn); if
		 * (result.getErrCode() < 0) { return result; }
		 * 
		 * }
		 */
		return result;
	}

	/**
	 * ��װ�龫�Զ��������쵥������parm
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getRequestMAutoOfDrugInfo(TParm parm, String requestNo,
			String appOrgCode) {
		TParm result = new TParm();
		result.setData("REQUEST_NO", requestNo);
		result.setData("REQTYPE_CODE", "DEP");
		result.setData("TO_ORG_CODE", parm.getValue("ORG_CODE"));
		result.setData("APP_ORG_CODE", appOrgCode);
		result.setData("REQUEST_USER", parm.getValue("OPT_USER"));
		result.setData("REASON_CHN_DESC", "");
		result.setData("UNIT_TYPE", "0");
		result.setData("URGENT_FLG", "N");
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		return result;
	}

	/**
	 * ��װ�龫�Զ����ⵥ����
	 * 
	 * @param requestNo
	 * @param seqNo
	 * @param orderCode
	 * @param fixQty
	 * @return
	 */
	public TParm getRequestDAutoOfDrugInfo(String requestNo, String seqNo,
			String orderCode, double fixQty, String optUser, String optTerm,
			String region) {
		TParm parm = new TParm();
		// �õ�ҩƷ�ļ۸�
		TParm priceParm = getOrderCodePrice(orderCode);
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("QTY", fixQty);
		// 1��浥λ��2��ҩ��λ
		parm.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		parm.setData("RETAIL_PRICE", priceParm.getValue("RETAIL_PRICE", 0));
		parm.setData("STOCK_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		// ����״̬ 0:������ 1:���ݽ��� 2:����ֹ 3:��ȫ����
		parm.setData("UPDATE_FLG", "0");
		parm.setData("OPT_USER", optUser);
		parm.setData("OPT_TERM", optTerm);
		parm.setData("REGION_CODE", region);
		return parm;
	}

	/**
	 * ��װ�龫�Զ����ⵥ����
	 * 
	 * @param requestNo
	 * @param seqNo
	 * @param orderCode
	 * @param fixQty
	 * @return
	 */
	public TParm getRequestDAutoOfDrug(String requestNo, String seqNo,
			String orderCode, double fixQty, String optUser, String optTerm,
			String region) {
		TParm parm = new TParm();
		// �õ�ҩƷ�ļ۸�
		TParm priceParm = getOrderCodePrice(orderCode);
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("QTY", fixQty);
		// 1��浥λ��2��ҩ��λ
		parm.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		parm.setData("RETAIL_PRICE", priceParm.getValue("RETAIL_PRICE", 0));
		parm.setData("STOCK_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		// ����״̬ 0:������ 1:���ݽ��� 2:����ֹ 3:��ȫ����
		parm.setData("UPDATE_FLG", "0");
		parm.setData("OPT_USER", optUser);
		parm.setData("OPT_TERM", optTerm);
		parm.setData("REGION_CODE", region);
		return parm;
	}

	/**
	 * ��װ�龫�Զ����ɳ��ⵥ������parm
	 * 
	 * @param parm
	 * @return
	 */
	public TParm geDispsenseMAutoOfDrugInfo(TParm parm, String dispsenSeNo,
			String requestNo, String appOrgCode) {
		TParm result = new TParm();
		result.setData("DISPENSE_NO", dispsenSeNo);
		result.setData("REQTYPE_CODE", "DEP");
		result.setData("REQUEST_NO", requestNo);
		result.setData("TO_ORG_CODE", parm.getValue("ORG_CODE"));
		result.setData("APP_ORG_CODE", appOrgCode);
		result.setData("REQUEST_USER", parm.getValue("OPT_USER"));
		result.setData("REASON_CHN_DESC", "");
		result.setData("UNIT_TYPE", "0");
		result.setData("URGENT_FLG", "N");
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		return result;
	}

	/**
	 * ��װ�龫�Զ����ɳ��ⵥ��ϸ����parm
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getDispsenseDAutoOfDrugInfo(String dispsenSeNo, String seqNo,
			String orgCode, String orderCode, double qty, String batchSeq,
			TParm parm, String supOrderCode) {
		TParm result = new TParm();
		// System.out.println("----------sql: "+SPCSQL.getIndStock(orgCode,
		// orderCode,batchSeq));
		TParm stockParm = new TParm(TJDODBTool.getInstance().select(
				SPCSQL.getIndStock(orgCode, orderCode, batchSeq)));
		// �õ�ҩƷ�ļ۸�
		TParm priceParm = getOrderCodePrice(orderCode);
		result.setData("DISPENSE_NO", dispsenSeNo);
		result.setData("SEQ_NO", seqNo);
		result.setData("REQUEST_SEQ", seqNo);
		result.setData("ORDER_CODE", orderCode);
		result.setData("BATCH_SEQ", batchSeq);
		result.setData("BATCH_NO", stockParm.getValue("BATCH_NO", 0));
		// fux modify 20141028
		result.setData("VALID_DATE", stockParm.getValue("VALID_DATE", 0)
				.substring(0, 10));
		result.setData("QTY", qty);
		result.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		result.setData("RETAIL_PRICE", priceParm.getValue("RETAIL_PRICE", 0));
		result.setData("VERIFYIN_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		result.setData("INVENT_PRICE", stockParm.getValue("INVENT_PRICE", 0));
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result.setData("SUP_CODE", stockParm.getValue("SUP_CODE", 0));
		result.setData("SUP_ORDER_CODE", supOrderCode);
		return result;
	}

	/**
	 * ��װҩ�⵵��parm
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getStockDParm(String orgCode, String orderCode, double qty,
			String batchSeq) {
		TParm result = new TParm();
		// System.out.println("----------sql: "+SPCSQL.getIndStock(orgCode,
		// orderCode,batchSeq));
		TParm stockParm = new TParm(TJDODBTool.getInstance().select(
				SPCSQL.getIndStock(orgCode, orderCode, batchSeq)));
		result.setData("ORDER_CODE", orderCode);
		result.setData("BATCH_SEQ", batchSeq);
		result.setData("ORG_CODE", orgCode);
		result.setData("STOCK_QTY", qty);
		result.setData("STOCK_AMT", StringTool.round(qty
				* stockParm.getDouble("RETAIL_PRICE", 0), 2));
		return result;
	}

	/**
	 * �����˻���
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertRegressgoods(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ��Ӧ����
		String sup_code = parm.getValue("SUP_CODE");
		// �˻�����
		String org_code = parm.getValue("ORG_CODE");

		/** �����˻������� */
		Map map = new HashMap();
		for (int i = 0; i < parm.getCount("REGRESSGOODS"); i++) {
			if (map.containsKey(parm.getData("REGRESSGOODS", i))
					|| map.containsValue(parm.getData("REGRESSGOODS", i))) {
				continue;
			}
			parm.setData("REGRESSGOODS_NO", parm.getData("REGRESSGOODS", i));
			map.put(parm.getData("REGRESSGOODS", i), parm.getData(
					"REGRESSGOODS", i));
			result = IndRegressgoodsMTool.getInstance().onInsert(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		/** �����˻���ϸ��,��˳�������˻���ϸ״̬ */
		Object object = parm.getData("UPDATE_SQL");
		if (object == null) {
			return null;
		}
		String[] updateSQL = (String[]) object;
		for (int i = 0; i < updateSQL.length; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSQL[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ���ⲿ�š�����������Ч�����Ž��п����� */
			// �˻���������(��ҩ��λ)
			double out_qty = 0;
			// �˻�����(������λ)
			double v_qty = 0;
			// ����ת����
			double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.00;
			// ������(�˻������� * ���ۼ�)
			double out_amt = 0.00;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				v_qty = parm.getDouble("QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				out_qty = StringTool.round(v_qty * s_qty * d_qty, 2);
				inparm.setData("OUT_QTY", out_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				out_amt = StringTool.round(v_qty * s_qty * d_qty * retail, 2);
				inparm.setData("OUT_AMT", out_amt);
				// OPT_USER,OPT_DATE,OPT_TERM
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				// ����ָ�������
				result = IndStockDTool.getInstance().onUpdateQtyReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
			// fux modify 20141027 �������ۼ�������
			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// for (int i = 0; i < parm.getCount("VERIFYIN_NO"); i++) {
			// TParm inparm = new TParm();
			// // ���յ���
			// inparm.setData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO", i));
			// // ���յ����
			// inparm.setData("SEQ_NO", parm.getValue("VERSEQ_NO", i));
			// // �˻�������
			// v_qty = parm.getDouble("QTY", i);
			// inparm.setData("QTY", v_qty);
			// // OPT_USER,OPT_DATE,OPT_TERM
			// inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
			// inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
			// inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			// // �ۼ��˻�������
			// result = IndVerifyinDTool.getInstance().onUpdateReg(inparm,
			// conn);
			// if (result.getErrCode() < 0) {
			// return result;
			// }
			// }
		}
		return result;
	}

	/**
	 * �����˻���
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateRegressgoods(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���տ���
		String org_code = parm.getValue("ORG_CODE");
		// ����������Ϣ
		result = IndRegressgoodsMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ���ⲿ�š�����������Ч�����Ž��п����� */
			// �˻���������(��ҩ��λ)
			double out_qty = 0;
			// �˻�����(������λ)
			double v_qty = 0;
			// ����ת����
			double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.00;
			// ������(�˻������� * ���ۼ�)
			double out_amt = 0.00;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				v_qty = parm.getDouble("QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				out_qty = StringTool.round(v_qty * s_qty * d_qty, 2);
				inparm.setData("OUT_QTY", out_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				out_amt = StringTool.round(v_qty * s_qty * d_qty * retail, 2);
				inparm.setData("OUT_AMT", out_amt);
				// OPT_USER,OPT_DATE,OPT_TERM
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				// ����ָ�������
				result = IndStockDTool.getInstance().onUpdateQtyReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
			// fux modify 20141027 ȥ�������ۼ����޸�
			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// for (int i = 0; i < parm.getCount("VERIFYIN_NO"); i++) {
			// TParm inparm = new TParm();
			// // ���յ���
			// inparm.setData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO", i));
			// // ���յ����
			// inparm.setData("SEQ_NO", parm.getValue("VERSEQ_NO", i));
			// // �˻�������
			// v_qty = parm.getDouble("QTY", i);
			// inparm.setData("QTY", v_qty);
			// // OPT_USER,OPT_DATE,OPT_TERM
			// inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
			// inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
			// inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			// // �ۼ��˻�������
			// result = IndVerifyinDTool.getInstance().onUpdateReg(inparm,
			// conn);
			// if (result.getErrCode() < 0) {
			// return result;
			// }
			// }

			/** ��˳�������˻���ϸ״̬ */
			Object object = parm.getData("UPDATE_SQL");
			if (object == null) {
				return null;
			}
			String[] updateSQL = (String[]) object;
			for (int i = 0; i < updateSQL.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						updateSQL[i], conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * ����ҩ���ż�ҩƷ�����ѯҩ������
	 * 
	 * @param org_code
	 *            ҩ����
	 * @param order_code
	 *            ҩƷ����
	 * @return QTY �����
	 */
	public double getStockQTY(String org_code, String order_code) {
		if ("".equals(org_code) || "".equals(order_code)) {
			return -1;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", org_code);
		parm.setData("ORDER_CODE", order_code);
		TParm result = getStockQTY(parm);
		if (result == null || result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return -1;
		}
		double qty = result.getDouble("QTY", 0);

		// add by huangtt start 20150608
		TParm resultBase = getStockBaseQTY(parm);
		qty = qty - resultBase.getDouble("QTY", 0);
		// add by huangtt end 20150608
		return qty;
	}

	/**
	 * ����ҩ����,ҩƷ����,����,Ч�ڲ�ѯҩ������. <br>
	 * ����org_codeʱ,���region_code��Ӱ��,Ϊ��ƥ��Control�����,���ﴫ���region_codeû��ʹ��
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param batch_no
	 *            String
	 * @param valid_date
	 *            Timestamp
	 * @return double
	 */
	public double getStockQTY(String org_code, String order_code,
			String batch_no, String valid_date, String region_code) {
		String sql = INDSQL.getINDStock(org_code, order_code, batch_no,
				valid_date);
		// System.out.println("sql==" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("STOCK_QTY", 0);
	}

	/**
	 * ����ҩ����,ҩƷ����,����,Ч�ڲ�ѯҩ������. <br>
	 * ����org_codeʱ,���region_code��Ӱ��,Ϊ��ƥ��Control�����,���ﴫ���region_codeû��ʹ��
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param batch_no
	 *            String
	 * @param valid_date
	 *            Timestamp
	 * @return double
	 */
	public double getStockQTYAll(String org_code, String order_code,
			String batch_no, String valid_date, String region_code) {
		String sql = INDSQL.getINDStockAll(org_code, order_code, batch_no,
				valid_date);
		// System.out.println("sql==" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("STOCK_QTY", 0);
	}

	/**
	 * ����ҩ����,ҩƷ����,����,Ч�ڲ�ѯҩ������. (SUM(STOCK_Qty))
	 * ����org_codeʱ,���region_code��Ӱ��,Ϊ��ƥ��Control�����,���ﴫ���region_codeû��ʹ��
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param batch_no
	 *            String
	 * @param valid_date
	 *            Timestamp
	 * @return double
	 */
	public double getStockQTYReturn(String org_code, String order_code,
			String batch_no, String valid_date, String region_code) {
		String sql = INDSQL.getINDStockReturn(org_code, order_code, batch_no,
				valid_date);
		// System.out.println("sql==" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getDouble("STOCK_QTY", 0);
	}

	/**
	 * ����ҩ���ż�ҩƷ�����ѯҩ������ add by huangtt 20150608
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getStockQTY(TParm parm) {
		TParm result = IndStockDTool.getInstance().onQueryStockQTY(parm);
		if (result == null || result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ҩ���ż�ҩƷ�����ѯ���ұ�ҩ�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getStockBaseQTY(TParm parm) {
		TParm result = IndStockDTool.getInstance().onQueryStockBaseQTY(parm);
		if (result == null || result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm unitParm = new TParm(TJDODBTool.getInstance().select(
				SYSSQL.getPhaTransUnit(parm.getValue("ORDER_CODE"))));
		if (unitParm.getValue("STOCK_UNIT", 0).equals(
				unitParm.getValue("DOSAGE_UNIT", 0))
				&& unitParm.getValue("STOCK_UNIT", 0).equals(
						result.getValue("UNIT_CODE", 0))) {
			return result;
		} else {
			result.setData("QTY", 0, result.getInt("QTY", 0)
					* unitParm.getInt("DOSAGE_QTY", 0));
			return result;
		}

	}

	/**
	 * ���������жϿ��
	 * 
	 * @param org_code
	 *            ҩ������
	 * @param order_code
	 *            ҩƷ����
	 * @param dosage_qty
	 *            ����
	 * @return boolean
	 */
	public boolean inspectIndStock(String org_code, String order_code,
			double dosage_qty) {
		if (dosage_qty < 0)
			return false;
		if (getStockQTY(org_code, order_code) >= dosage_qty)
			return true;
		return false;
	}

	/**
	 * <ҩ��>��ҩ�ۿ�
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            String ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm reduceIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		// ***********************************************************************
		// luhai 2012-1-26 modify �����ص�orderList�м���batchSeq��orderCode ��Ϣ begin
		// ***********************************************************************
		// TParm result = new TParm();
		// TParm resultParm = new TParm();
		// // ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		// TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
		// org_code, order_code, "");
		// //System.out.println("parm-----"+parm);
		// if (parm.getErrCode() < 0) {
		// return parm;
		// }
		// // �����
		// double qty = 0;
		// // �������
		// int batch_seq = 0;
		// // ������
		// double out_amt = 0;
		// // �ɱ����
		// double cost_amt = 0;
		// // ���ݷ���ȼ�ѡ���Ӧ�����۽����
		// String OWN_PRICE = "RETAIL_PRICE";
		// if ("2".equals(service_level)) {
		// OWN_PRICE = "OWN_PRICE2";
		// }
		// else if ("3".equals(service_level)) {
		// OWN_PRICE = "OWN_PRICE3";
		// }
		// else {
		// OWN_PRICE = "RETAIL_PRICE";
		// }
		// //System.out.println("DOSAGE_QTY-------------"+dosage_qty);
		// //�ɱ����
		// cost_amt = 0;
		// for (int i = 0; i < parm.getCount(); i++) {
		// qty = parm.getDouble("QTY", i);
		// //System.out.println("qty-------------"+qty);
		// batch_seq = parm.getInt("BATCH_SEQ", i);
		// if (qty >= dosage_qty) {
		// // ҩƷ����һ�οۿ�
		// out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
		// * dosage_qty, 2);
		// //����ɱ����
		// cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;
		//
		// // ���¿��(�ۿ�)
		// resultParm = IndStockDTool.getInstance().onUpdateQtyOut(org_code,
		// order_code, batch_seq, dosage_qty, out_amt, opt_user,
		// opt_date, opt_term, conn);
		//
		// //�ش��ɱ����
		// result.addData("ORDER_CODE", order_code);
		// result.addData("ORG_CODE", org_code);
		// result.addData("QTY", dosage_qty);
		// result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
		// result.setData("COST_AMT", StringTool.round(cost_amt, 2));
		// return result;
		// }
		// else {
		// // ҩƷ����һ�οۿ�
		// out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
		// * qty, 2);
		// //����ɱ����
		// cost_amt +=parm.getDouble("VERIFYIN_PRICE", i) * qty;
		//
		// // ���¿��(�ۿ�)
		// resultParm = IndStockDTool.getInstance().onUpdateQtyOut(org_code,
		// order_code, batch_seq, qty, out_amt, opt_user, opt_date,
		// opt_term, conn);
		// // ���¿ۿ���
		// dosage_qty = dosage_qty - qty;
		// result.addData("ORDER_CODE", order_code);
		// result.addData("ORG_CODE", org_code);
		// result.addData("QTY", qty);
		// result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
		// }
		// }
		// return result;
		TParm result = new TParm();
		TParm resultParm = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
				org_code, order_code, "");
		// System.out.println("parm-----"+parm);
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// �����
		double qty = 0;
		// �������
		int batch_seq = 0;
		// ������
		double out_amt = 0;
		// �ɱ����
		double cost_amt = 0;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽���� <������ĳ����ռ۸񣬳ɱ��ۣ������ݷ���ȼ�������>
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}
		// System.out.println("DOSAGE_QTY-------------"+dosage_qty);
		// �ɱ����
		cost_amt = 0;
		Double tot = 0.0;
		for (int i = 0; i < parm.getCount(); i++) {
			tot += parm.getDouble("QTY", i);
		}
		if(tot < dosage_qty){
			result.setErr( -1, "ҩƷ��治��\n���ɿۿ⣡");
			return result;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			// System.out.println("qty-------------"+qty);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);

				// �ش��ɱ����
				result.addData("ORDER_CODE", order_code);
				result.addData("ORG_CODE", org_code);
				result.addData("QTY", dosage_qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				return result;
			} else {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// ���¿ۿ���
				dosage_qty = dosage_qty - qty;
				result.addData("ORDER_CODE", order_code);
				result.addData("ORG_CODE", org_code);
				result.addData("QTY", qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
			}
		}
		return result;
		// ***********************************************************************
		// luhai 2012-1-26 modify �����ص�orderList�м���batchSeq��orderCode ��Ϣ begin
		// ***********************************************************************
	}

	/**
	 * <ҩ��>��ҩ�ۿ�
	 * 
	 * @param orgCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            String ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm reduceIndStockByBatchNo(String orgCode, String orderCode,
			String batchNo, double dosage_qty, String opt_user,
			Timestamp opt_date, String opt_term, String service_level,
			TConnection conn) {

		TParm result = new TParm();
		TParm resultParm = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		TParm parm = getIndStockQtyByBatchNo(orgCode, orderCode, batchNo, "");
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// �����
		double qty = 0;
		// �������
		int batch_seq = 0;
		// ������
		double out_amt = 0;
		// �ɱ����
		double cost_amt = 0;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽���� <������ĳ����ռ۸񣬳ɱ��ۣ������ݷ���ȼ�������>
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}
		// System.out.println("DOSAGE_QTY-------------"+dosage_qty);
		// �ɱ����
		cost_amt = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			// System.out.println("qty-------------"+qty);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						orgCode, orderCode, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);

				// �ش��ɱ����
				result.addData("ORDER_CODE", orderCode);
				result.addData("ORG_CODE", orgCode);
				result.addData("QTY", dosage_qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				result.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
				result.addData("VALID_DATE", parm.getValue("VALID_DATE", i));
				return result;
			} else {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						orgCode, orderCode, batch_seq, qty, out_amt, opt_user,
						opt_date, opt_term, conn);
				// ���¿ۿ���
				dosage_qty = dosage_qty - qty;
				result.addData("ORDER_CODE", orderCode);
				result.addData("ORG_CODE", orgCode);
				result.addData("QTY", qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				result.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
				result.addData("VALID_DATE", parm.getValue("VALID_DATE", i));
			}
		}
		return result;
		// ***********************************************************************
		// luhai 2012-1-26 modify �����ص�orderList�м���batchSeq��orderCode ��Ϣ begin
		// ***********************************************************************
	}

	/**
	 * <ҩ��>��ҩ�ۿ�
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            String ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm reduceIndStockSpc(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {

		TParm result = new TParm();
		TParm resultParm = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
				org_code, order_code, "");
		// System.out.println("parm-----"+parm);
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// �����
		double qty = 0;
		// �������
		int batch_seq = 0;
		// ������
		double out_amt = 0;
		// �ɱ����
		double cost_amt = 0;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽���� <������ĳ����ռ۸񣬳ɱ��ۣ������ݷ���ȼ�������>
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}
		// System.out.println("DOSAGE_QTY-------------"+dosage_qty);
		// �ɱ����
		cost_amt = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			// System.out.println("qty-------------"+qty);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);

				// �ش��ɱ����
				result.addData("ORDER_CODE", order_code);
				result.addData("ORG_CODE", org_code);
				result.addData("QTY", dosage_qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				result.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
				result.addData("VALID_DATE", parm.getValue("VALID_DATE", i));
				return result;
			} else {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;

				// ���¿��(�ۿ�)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// ���¿ۿ���
				dosage_qty = dosage_qty - qty;
				result.addData("ORDER_CODE", order_code);
				result.addData("ORG_CODE", org_code);
				result.addData("QTY", qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				result.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
				result.addData("VALID_DATE", parm.getValue("VALID_DATE", i));
			}
		}
		return result;
		// ***********************************************************************
		// luhai 2012-1-26 modify �����ص�orderList�м���batchSeq��orderCode ��Ϣ begin
		// ***********************************************************************
	}

	/**
	 * <ҩ��>��ҩ�ۿ�(���)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX,ORDER_CODE=XXX,DOSAGE_QTY=XXX,OPT_USER=XXX,OPT_DATE
	 *            =XXX,OPT_TERM=XXX
	 * @param service_level
	 * @param conn
	 * @return TParm
	 */
	public TParm reduceIndStock(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		org_code = parm.getValue("ORG_CODE");
		order_code = parm.getValue("ORDER_CODE");
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		result = reduceIndStock(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <ҩ��>��ҩ�ۿ�(���)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX,ORDER_CODE=XXX,DOSAGE_QTY=XXX,OPT_USER=XXX,OPT_DATE
	 *            =XXX,OPT_TERM=XXX
	 * @param service_level
	 * @param conn
	 * @return TParm
	 */
	public TParm reduceIndStockSpc(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		org_code = parm.getValue("ORG_CODE");
		order_code = parm.getValue("ORDER_CODE");
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		result = reduceIndStockSpc(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <ҩ��>��ҩ�ۿ�(���)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX,ORDER_CODE=XXX,DOSAGE_QTY=XXX,OPT_USER=XXX,OPT_DATE
	 *            =XXX,OPT_TERM=XXX
	 * @param service_level
	 * @param conn
	 * @return TParm
	 */
	public TParm reduceIndStockByBatchNo(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String orgCode = "";
		String orderCode = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		orgCode = parm.getValue("ORG_CODE");
		orderCode = parm.getValue("ORDER_CODE");
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		String batchNo = parm.getValue("BATCH_NO");
		result = reduceIndStockByBatchNo(orgCode, orderCode, batchNo,
				dosage_qty, opt_user, opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <ҩ��>ȡ����ҩ���
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm unReduceIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		TParm result = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		TParm parm = IndStockDTool.getInstance().onQueryStockQty(org_code,
				order_code, "DESC");

		if (parm.getErrCode() != 0) {
			return parm;
		}
		// �������
		int batch_seq = parm.getInt("BATCH_SEQ", 0);
		// �������
		double in_qty = dosage_qty;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽����
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// �����
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// ���¿��(ȡ����ҩ)
		result = IndStockDTool.getInstance().onUpdateQtyIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}

	/**
	 * <ҩ��>ȡ����ҩ���(���)
	 * 
	 * @param parm
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 * @return
	 */
	public TParm unReduceIndStock(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		org_code = parm.getValue("ORG_CODE");
		// System.out.println("orgCode="+org_code);
		order_code = parm.getValue("ORDER_CODE");
		// System.out.println("orderCode="+order_code);
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		// System.out.println("dosage_qty="+dosage_qty);
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		result = unReduceIndStock(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <ҩ��>��ҩ���
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm regressIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		// ���¿��
		TParm result = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ� by liyh 2010806
		TParm parm = IndStockDTool.getInstance().onQueryStockQtyTwo(org_code,
				order_code, "DESC");
		if (null == parm || parm.getCount() < 1) {
			parm = IndStockDTool.getInstance().onQueryStockQty(org_code,
					order_code, "DESC");
		}
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// �������
		int batch_seq = parm.getInt("BATCH_SEQ", 0);
		// �������
		double in_qty = dosage_qty;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽����
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// �����
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// ���¿��(��ҩ)
		result = IndStockDTool.getInstance().onUpdateQtyRegIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);

		result.setData("BATCH_SEQ", batch_seq);
		result.setData("DOSAGE_QTY", dosage_qty);
		result.setData("VERIFYIN_PRICE", parm.getValue("VERIFYIN_PRICE", 0));
		System.out.println(batch_seq + "-----" + dosage_qty + "------"
				+ parm.getValue("VERIFYIN_PRICE", 0));
		return result;
	}

	/**
	 * <ҩ��>��ҩ����SEQ��� luhai 2012-1-30 add ����BATCH_SEQ �����˿����
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm regressIndStockWithBatchSeq(String org_code,
			String order_code, int batchSeq, double dosage_qty,
			String opt_user, Timestamp opt_date, String opt_term,
			String service_level, TConnection conn) {
		// �������
		int batch_seq = batchSeq;
		TParm parm = IndStockDTool.getInstance().onQueryStockWithBatchSeq(
				org_code, order_code, batchSeq);
		// �������
		double in_qty = dosage_qty;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽����
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// �����
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// ���¿��(��ҩ)
		TParm result = IndStockDTool.getInstance().onUpdateQtyRegIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}

	/**
	 * <ҩ��>��ҩ���(���)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX(String), ORDER_CODE=XXX(String),
	 *            DOSAGE_QTY=XXX(double), OPT_USER=XXX(String),
	 *            OPT_DATE=XXX(Timestamp), OPT_TERM=XXX(String)
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 * @return TParm
	 */
	public TParm regressIndStock(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		org_code = parm.getValue("ORG_CODE");
		order_code = parm.getValue("ORDER_CODE");
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		result = regressIndStock(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <ҩ��>ȡ����ҩ�ۿ�
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param dosage_qty
	 *            double
	 * @param opt_user
	 *            String
	 * @param opt_date
	 *            Timestamp
	 * @param opt_term
	 *            String
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm unRegressIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		TParm result = new TParm();
		// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
		TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
				org_code, order_code, "");
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// �����
		double qty = 0;
		// �������
		int batch_seq = 0;
		// ������
		double out_amt = 0;
		// �ɱ����
		double cost_amt = 0;
		// ���ݷ���ȼ�ѡ���Ӧ�����۽����
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// �ɱ����
		cost_amt = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;
				// ���¿��(ȡ����ҩ�ۿ�)
				result = IndStockDTool.getInstance().onUpdateQtyRegOut(
						org_code, order_code, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// �ش��ɱ����
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				return result;
			} else {
				// ҩƷ����һ�οۿ�
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// ����ɱ����
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;
				// ���¿��(ȡ����ҩ�ۿ�)
				result = IndStockDTool.getInstance().onUpdateQtyRegOut(
						org_code, order_code, batch_seq, qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// ��������
				dosage_qty = dosage_qty - qty;
			}
		}
		return result;
	}

	/**
	 * <ҩ��>ȡ����ҩ�ۿ�(���)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX(String), ORDER_CODE=XXX(String),
	 *            DOSAGE_QTY=XXX(double), OPT_USER=XXX(String),
	 *            OPT_DATE=XXX(Timestamp), OPT_TERM=XXX(String)
	 * @param service_level
	 *            ����ȼ�
	 * @param conn
	 * @return TParm
	 */
	public TParm unRegressIndStock(TParm parm, String service_level,
			TConnection conn) {
		TParm result = new TParm();
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		String opt_user = "";
		String opt_term = "";
		org_code = parm.getValue("ORG_CODE");
		order_code = parm.getValue("ORDER_CODE");
		dosage_qty = parm.getDouble("DOSAGE_QTY");
		opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		opt_term = parm.getValue("OPT_TERM");
		result = unRegressIndStock(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * �жϲ����Ƿ����ι�����
	 * 
	 * @param org_code
	 *            ҩ������
	 * @return boolean
	 */
	public boolean checkIndOrgBatch(String org_code) {
		if ("".equals(org_code))
			return false;
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndOrgBatchFlg(org_code)));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		if ("Y".equals(TypeTool.getString(result.getData("BATCH_FLG", 0))))
			return false;
		return true;
	}

	/**
	 * �ж��Ƿ��ҩƷ���ڽ��е���
	 * 
	 * @param org_code
	 *            ҩ������
	 * @param order_code
	 *            ҩƷ����
	 * @return boolean
	 */
	public boolean checkIndStockReadjustp(String org_code, String order_code) {
		if ("".equals(org_code) || "".equals(order_code))
			return false;
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndStockReadjustpFlg(org_code, order_code)));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		if ("Y".equals(TypeTool.getString(result.getData("READJUSTP_FLG", 0))))
			return false;
		return true;
	}

	/**
	 * ɾ���ɹ��ƻ��������ϸ��
	 * 
	 * @return
	 */
	public TParm onDeletePurPlanM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		Object delete = parm.getData("DELETE_SQL");
		TParm result = new TParm();
		if (delete != null) {
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
				// ɾ��ϸ��
				for (int i = 0; i < deleteSql.length; i++) {
					result = new TParm(TJDODBTool.getInstance().update(
							deleteSql[i], conn));
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		}
		// ɾ������
		result = IndPurPlanMTool.getInstance().onDelete(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ɾ������ϸ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeletePurorderD(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		Object update = parm.getData("UPDATE_SQL");
		Object org = parm.getData("ORG_CODE");
		Object order = parm.getData("ORDER_CODE");
		Object p_qty = parm.getData("PURORDER_QTY");
		Object s_qty = parm.getData("STOCK_QTY");
		Object d_qty = parm.getData("DOSAGE_QTY");
		if (update == null) {
			return null;
		}
		String[] updateSql;
		if (update instanceof String[]) {
			updateSql = (String[]) update;
		} else {
			return null;
		}
		String org_code = (String) org;
		String order_code = (String) order;
		double pur_qty = TypeTool.getDouble(p_qty);
		double stock_qty = TypeTool.getDouble(s_qty);
		double dosage_qty = TypeTool.getDouble(d_qty);
		TParm result = new TParm();
		// ������;��
		TParm inparm = new TParm();
		inparm.setData("ORG_CODE", org_code);
		inparm.setData("ORDER_CODE", order_code);
		pur_qty = pur_qty * stock_qty * dosage_qty * (-1.00);
		inparm.setData("BUY_UNRECEIVE_QTY", StringTool.round(pur_qty, 2));
		result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(inparm,
				conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ɾ��ϸ��
		for (int i = 0; i < updateSql.length; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSql[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * ɾ������������ϸ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeletePurorderM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		Object puroder = parm.getData("PURORDER_NO");
		if (puroder == null) {
			return null;
		}
		String puroderNo;
		if (puroder instanceof String) {
			puroderNo = (String) puroder;
		} else {
			return null;
		}
		TParm result = new TParm();
		// �ж��Ƿ�ɾ��ϸ��
		if (parm.existData("DELETE_SQL")) {
			Object delete = parm.getData("DELETE_SQL");
			Object org = parm.getData("ORG_CODE");
			if (delete == null) {
				return null;
			}
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
			} else {
				return null;
			}
			if (org == null) {
				return null;
			}
			String org_code;
			if (org instanceof String) {
				org_code = (String) org;
			} else {
				return null;
			}
			double pur_qty = 0;
			double stock_qty = 0;
			double dosage_qty = 0;
			// ������;��
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				pur_qty = parm.getDouble("PURORDER_QTY", i);
				stock_qty = parm.getDouble("STOCK_QTY", i);
				dosage_qty = parm.getDouble("DOSAGE_QTY", i);
				pur_qty = pur_qty * stock_qty * dosage_qty * (-1.00);
				inparm.setData("BUY_UNRECEIVE_QTY", StringTool
						.round(pur_qty, 2));
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
			// ɾ��ϸ��
			for (int i = 0; i < deleteSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						deleteSql[i], conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// ɾ������
		TParm inparm = new TParm();
		inparm.setData("PURORDER_NO", puroderNo);
		result = IndPurorderMTool.getInstance().onDelete(inparm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ�����յ�������ϸ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteVerifyinM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		TParm result = new TParm();
		// �ж��Ƿ�ɾ��ϸ��
		if (parm.existData("DELETE_SQL")) {
			Object delete = parm.getData("DELETE_SQL");
			if (delete == null) {
				return null;
			}
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
			} else {
				return null;
			}
			// ɾ��ϸ��
			for (int i = 0; i < deleteSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						deleteSql[i], conn));
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		// ɾ������
		TParm inparm = new TParm();
		String ver_no = parm.getValue("VERIFYIN_NO");
		inparm.setData("VERIFYIN_NO", ver_no);
		result = IndVerifyinMTool.getInstance().onDelete(inparm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ���˻���������ϸ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteRegressgoodsM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		TParm result = new TParm();
		// �ж��Ƿ�ɾ��ϸ��
		if (parm.existData("DELETE_SQL")) {
			Object delete = parm.getData("DELETE_SQL");
			if (delete == null) {
				return null;
			}
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
			} else {
				return null;
			}
			// ɾ��ϸ��
			for (int i = 0; i < deleteSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						deleteSql[i], conn));
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		// ɾ������
		TParm inparm = new TParm();
		String reg_no = parm.getValue("REGRESSGOODS_NO");
		inparm.setData("REGRESSGOODS_NO", reg_no);
		result = IndRegressgoodsMTool.getInstance().onDelete(inparm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ�����뵥������ϸ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteRequestM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		TParm result = new TParm();
		// �ж��Ƿ�ɾ��ϸ��
		if (parm.existData("DELETE_SQL")) {
			Object delete = parm.getData("DELETE_SQL");
			if (delete == null) {
				return null;
			}
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
			} else {
				return null;
			}
			// ɾ��ϸ��
			for (int i = 0; i < deleteSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						deleteSql[i], conn));
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		// ɾ������
		TParm inparm = new TParm();
		String req_no = parm.getValue("REQUEST_NO");
		inparm.setData("REQUEST_NO", req_no);
		result = IndRequestMTool.getInstance().onDelete(inparm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���������ת����
	 * 
	 * @param order_code
	 * @return
	 */
	public TParm getTransunitByCode(String order_code) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getTransunitByCode(order_code)));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���涩����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSavePurOrder(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		Object update = parm.getData("UPDATE_SQL");
		String org_code = parm.getValue("ORG_CODE");
		if (update == null) {
			return null;
		}
		if (org_code == null) {
			return null;
		}
		String[] updateSql;
		if (update instanceof String[]) {
			updateSql = (String[]) update;
		} else {
			return null;
		}
		TParm result = new TParm();
		// ���涩����ϸ��
		for (int i = 0; i < updateSql.length; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSql[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// ������;��
		double qty = 0;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			TParm inparm = new TParm();
			inparm.setData("ORG_CODE", org_code);
			inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			qty = parm.getDouble("PURORDER_QTY", i)
					* parm.getDouble("STOCK_QTY", i)
					* parm.getDouble("DOSAGE_QTY", i);
			inparm.setData("BUY_UNRECEIVE_QTY", StringTool.round(qty, 2));
			result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
					inparm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}

		// ���¶������вɹ��ƻ����� by liyh 201219��ҩ��������������ɹ�����
		/*
		 * Object plan_no = parm.getData("PLAN_NO"); if (plan_no != null) {
		 * TParm inparm = new TParm(); inparm.setData("PURORDER_NO",
		 * parm.getValue("PURORDER_NO")); inparm.setData("PLAN_NO",
		 * parm.getValue("PLAN_NO")); result =
		 * IndPurorderMTool.getInstance().onUpdatePlanNo(inparm, conn); if
		 * (result.getErrCode() < 0) { err("ERR:" + result.getErrCode() +
		 * result.getErrText() + result.getErrName()); return result; } }
		 */
		return result;
	}

	/**
	 * ����������ҵ(��;) / ��������������ҵ�����Ĳġ����ұ�ҩ(���⼴���)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutOn(TParm parm, TConnection conn) {

		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���뵥����
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");

		String toorg_code = parm.getValue("TO_ORG_CODE");

		// ��λ����
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// 20150727 wangjc add start
		// �鿴ind_stockm�Ƿ�������,����ޣ�����(ȷ���Ƿ����)

		if (!"DRT".equals(request_type)) {
			// ҩƷ����(���뵥����)
			result = onIndDispenseOut(parmD, org_code, unit_type, request_type,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// �������뵥״̬��ʵ�ʳ��������
			result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
					parmD, conn);
			if (result.getErrCode() < 0) {
				return result;
			}

			for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
				String orderCode = parmD.getValue("ORDER_CODE", i);
				String sqlStockM = INDSQL.getVerInStockM(orderCode, org_code);
				TParm StockmParm = new TParm(TJDODBTool.getInstance().select(
						sqlStockM));
				if (null == StockmParm || StockmParm.getCount() < 0) {
					StockmParm.setData("ORDER_CODE", orderCode);
					StockmParm.setData("ORG_CODE", org_code);
					StockmParm.setData("REGION_CODE", "H01");
					StockmParm.setData("OPT_USER", parmD
							.getValue("OPT_USER", i));
					StockmParm.setData("OPT_TERM", parmD
							.getValue("OPT_TERM", i));
					String insertSql = INDSQL.insertStockM(StockmParm);
					result = new TParm(TJDODBTool.getInstance().update(
							insertSql, conn));
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		} else {
			// fux modify 20160218 ����ǿ��������� �� У����ǽ��ܿ���(���㰡)

			// ҩƷ����(���뵥����)
			result = onIndDispenseOut(parmD, toorg_code, unit_type,
					request_type, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// �������뵥״̬��ʵ�ʳ��������
			result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
					parmD, conn);
			if (result.getErrCode() < 0) {
				return result;
			}

			for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
				String orderCode = parmD.getValue("ORDER_CODE", i);
				String sqlStockM = INDSQL.getVerInStockM(orderCode, toorg_code);
				TParm StockmParm = new TParm(TJDODBTool.getInstance().select(
						sqlStockM));
				if (null == StockmParm || StockmParm.getCount() < 0) {
					StockmParm.setData("ORDER_CODE", orderCode);
					StockmParm.setData("ORG_CODE", toorg_code);
					StockmParm.setData("REGION_CODE", "H01");
					StockmParm.setData("OPT_USER", parmD
							.getValue("OPT_USER", i));
					StockmParm.setData("OPT_TERM", parmD
							.getValue("OPT_TERM", i));
					String insertSql = INDSQL.insertStockM(StockmParm);
					result = new TParm(TJDODBTool.getInstance().update(
							insertSql, conn));
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		}

		// 20150727 wangjc add end

		return result;
	}

	/**
	 * ҩƷ����(���뵥����)
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	public TParm onIndDispenseOut(TParm parm, String org_code,
			String unit_type, String request_type, TConnection conn) {
		String order_code = "";
		String unit_code = "";
		int seq = 1;
		double actual_qty = 0;
		// DRT ��������
		double drt_qty = 0;
		String batch_no = "";
		String valid_date = "";
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// ʵ�ʳ�������
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			unit_code = parm.getValue("UNIT_CODE", i);
			// fux*
			double un = getPhaTransUnit(order_code, unit_code);
			// System.out.println("un:"+un);
			if ("0".equals(unit_type)) {
				if (un == 3) {
					actual_qty = parm.getDouble("ACTUAL_QTY", i);
				} else {
					actual_qty = parm.getDouble("ACTUAL_QTY", i)
							* getPhaTransUnitQty(order_code, "2");
				}
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}

			if ("0".equals(unit_type)) {
				if (un == 3) {
					drt_qty = parm.getDouble("QTY_1", i);
				} else {
					drt_qty = parm.getDouble("QTY_1", i)
							* getPhaTransUnitQty(order_code, "2");
				}
			} else {
				drt_qty = parm.getDouble("QTY_1", i);
			}

			// actual_qty = parm.getDouble("ACTUAL_QTY", i);
			// 20150722 wangjc add start

			// 20150722 wangjc add end

			// ��ָ�����ź�Ч��
			if ("DEP".equals(request_type) || "TEC".equals(request_type)
					|| "GIF".equals(request_type) || "COS".equals(request_type)
					|| "EXM".equals(request_type)) {

				if (actual_qty == 0) {
					continue;
				}

				// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				System.out.println("stock_parm:::" + stock_parm);
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);

					if (qty >= actual_qty) {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j)
								* actual_qty, 2);
						// ���¿��(���뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type, org_code,
										order_code, batch_seq, actual_qty,
										out_amt, parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						if (result.getErrCode() < 0) {
							return result;
						}

						// �������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", parm.getValue(
								"DISPENSE_NO", i));
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", parm.getInt(
								"REQUEST_SEQ", i));
						inparm.setData("ORDER_CODE", parm.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", stock_parm.getInt(
								"BATCH_SEQ", j));
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
						double tranUnitQty = getDosageQty(order_code);
						/*
						 * double tranUnitQty = getPhaTransUnitQty(order_code,
						 * "2") * getPhaTransUnitQty(order_code, "1");
						 */
						// fux modify
						if ("0".equals(unit_type)) {
							if (un == 3) {
								qty_out = actual_qty;
							} else {
								qty_out = actual_qty / tranUnitQty;
							}
						} else {
							qty_out = actual_qty;
						}
						// System.out.println("1::::qty:"+qty);
						// System.out.println("1:::::::qty_out:"+qty_out);
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));

						// ���ۼ�ȡ��С��λ�۸�
						inparm.setData("RETAIL_PRICE", stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j));

						// luhai modify 20120503 begin TEC��EXM��COS ���ۼ��÷�ҩ��λ end
						double stock_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j)
								* tranUnitQty;
						inparm.setData("STOCK_PRICE", stock_price);
						// luhai 2012-01-13 add verifyin_price begin

						// ��ҩ����ʱΪ��С��λ��*ת����
						// ���ռ۸�
						double verifyin_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j);
						inparm.setData("VERIFYIN_PRICE", verifyin_price);

						// ���м۸�
						double inventPrice = stock_parm.getDouble(
								"INVENT_PRICE", j);
						inparm.setData("INVENT_PRICE", inventPrice);

						// luhai 2012-01-13 add verifyin_price end
						inparm.setData("ACTUAL_QTY", qty_out);
						inparm
								.setData("PHA_TYPE", parm.getValue("PHA_TYPE",
										i));
						inparm
								.setData("OPT_USER", parm.getValue("OPT_USER",
										i));
						inparm.setData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						inparm
								.setData("OPT_TERM", parm.getValue("OPT_TERM",
										i));
						inparm.setData("BOX_ESL_ID", parm.getValue(
								"BOX_ESL_ID", i));
						inparm.setData("BOXED_USER", parm.getValue(
								"BOXED_USER", i));
						inparm
								.setData("IS_BOXED", parm.getValue("IS_BOXED",
										i));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));

						inparm.setData("SUP_ORDER_CODE", stock_parm.getValue(
								"SUP_ORDER_CODE", j));
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j)
								* qty, 2);
						// ���¿��(���뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type, org_code,
										order_code, batch_seq, qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						if (result.getErrCode() < 0) {
							return result;
						}

						// �������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", parm.getValue(
								"DISPENSE_NO", i));
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", parm.getInt(
								"REQUEST_SEQ", i));
						inparm.setData("ORDER_CODE", parm.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", stock_parm.getInt(
								"BATCH_SEQ", j));
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
						double tranUnitQty = getDosageQty(order_code);
						/*
						 * double tranUnitQty = getPhaTransUnitQty(order_code,
						 * "2") * getPhaTransUnitQty(order_code, "1");
						 */
						// fux modify
						if ("0".equals(unit_type)) {
							if (un == 3) {
								qty_out = actual_qty;
							} else {
								qty_out = actual_qty / tranUnitQty;
								qty = qty / tranUnitQty;
							}
						} else {
							qty_out = actual_qty;
						}

						// System.out.println("2::::qty:"+qty);
						// System.out.println("2:::::::qty_out:"+qty_out);
						// fux modify qty_out ->qty
						inparm.setData("QTY", qty);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));

						// ���ۼ�ȡ��С��λ�۸�
						inparm.setData("RETAIL_PRICE", stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j));

						double stock_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j)
								* tranUnitQty;
						inparm.setData("STOCK_PRICE", stock_price);

						double verifyin_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j);
						inparm.setData("VERIFYIN_PRICE", verifyin_price);

						// ���м۸�
						double inventPrice = stock_parm.getDouble(
								"INVENT_PRICE", j);
						inparm.setData("INVENT_PRICE", inventPrice);

						inparm.setData("ACTUAL_QTY", qty);
						inparm
								.setData("PHA_TYPE", parm.getValue("PHA_TYPE",
										i));
						inparm
								.setData("OPT_USER", parm.getValue("OPT_USER",
										i));
						inparm.setData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						inparm
								.setData("OPT_TERM", parm.getValue("OPT_TERM",
										i));
						inparm.setData("BOX_ESL_ID", parm.getValue(
								"BOX_ESL_ID", i));
						inparm.setData("BOXED_USER", parm.getValue(
								"BOXED_USER", i));
						inparm
								.setData("IS_BOXED", parm.getValue("IS_BOXED",
										i));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));
						inparm.setData("SUP_ORDER_CODE", stock_parm.getValue(
								"SUP_ORDER_CODE", j));
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// ���³�����
						if ("0".equals(unit_type)) {
							if (un != 3) {
								qty_out = qty_out * tranUnitQty;
								qty = qty * tranUnitQty;
							}
						}
						actual_qty = actual_qty - qty;
						// ����ʵ�ʳ�������
						// fux modify 20151201
						qty_out = qty_out + qty;
						// System.out.println("���³�����actual_qty:"+actual_qty);
						// System.out.println("����ʵ�ʳ�������qty_out:"+qty_out);
					}
				}
			}
			// 20160107 fuxin modify ������
			else if ("DRT".equals(request_type)) {
				// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
				// TParm stock_parm =
				// IndStockDTool.getInstance().onQueryStockBatchAndQty(org_code,
				// order_code, "");
				// ������sql
				int batch_seq = parm.getInt("BATCH_SEQ", i);
				// System.out.println("batch_seq:" + batch_seq);
				String sqlStock = "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE, A.STOCK_QTY AS QTY,"
						+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3,B.PHA_TYPE,  "
						+ " A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE,A.SUP_CODE,A.INVENT_PRICE, "
						+ " A.SUP_ORDER_CODE "
						+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
						+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ " AND A.ORDER_CODE = C.ORDER_CODE "
						+ " AND B.ORDER_CODE = C.ORDER_CODE "
						+ " AND A.ORG_CODE = '"
						+ org_code
						+ "' AND A.ORDER_CODE = '"
						+ order_code
						+ "' AND A.ACTIVE_FLG='Y' "
						// fux modify 20170301 �������ŵ��ж�
						+ "  AND A.BATCH_SEQ = '" + batch_seq + "'  "
						// + " AND SYSDATE < A.VALID_DATE "
						// + " AND A.STOCK_QTY  > 0 "

						+ " ORDER BY A.VALID_DATE " + " , A.BATCH_SEQ DESC";
				// System.out.println("sqlStock:" + sqlStock);
				TParm stock_parm = new TParm(TJDODBTool.getInstance().select(
						sqlStock));
				// fux modify 20160222
				for (int j = 0; j < stock_parm.getCount(); j++) {
					// double qty = stock_parm.getDouble("QTY",0);
					int batch_seqStock = stock_parm.getInt("BATCH_SEQ", j);
					if (batch_seqStock == batch_seq) {
						// �����˻� һ������һ���Կۻ�
						// if (qty >= actual_qty) {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", 0)
								* drt_qty, 2);
						// ���¿��(���뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type, org_code,
										order_code, batch_seq, drt_qty,
										out_amt, parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						if (result.getErrCode() < 0) {
							return result;
						}

						// �������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", parm.getValue(
								"DISPENSE_NO", i));
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", parm.getInt(
								"REQUEST_SEQ", i));
						inparm.setData("ORDER_CODE", parm.getValue(
								"ORDER_CODE", i));
						// fux modify 20161009 ȷ�� ƥ������Ч�� �� ������Ҫ
						inparm.setData("BATCH_SEQ", stock_parm.getInt(
								"BATCH_SEQ", j));
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
						double tranUnitQty = getDosageQty(order_code);
						/*
						 * double tranUnitQty = getPhaTransUnitQty(order_code,
						 * "2") * getPhaTransUnitQty(order_code, "1");
						 */
						// fux modify
						if ("0".equals(unit_type)) {
							if (un == 3) {
								qty_out = drt_qty;
							} else {
								qty_out = drt_qty / tranUnitQty;
							}
						} else {
							qty_out = drt_qty;
						}
						// System.out.println("1::::qty:"+qty);
						// System.out.println("1:::::::qty_out:"+qty_out);
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));

						// ���ۼ�ȡ��С��λ�۸�
						inparm.setData("RETAIL_PRICE", stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", 0));

						// luhai modify 20120503 begin TEC��EXM��COS ���ۼ��÷�ҩ��λ end
						double stock_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", 0)
								* tranUnitQty;
						inparm.setData("STOCK_PRICE", stock_price);
						// luhai 2012-01-13 add verifyin_price begin

						// ��ҩ����ʱΪ��С��λ��*ת����
						// ���ռ۸�
						double verifyin_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", 0);
						inparm.setData("VERIFYIN_PRICE", verifyin_price);
						System.out.println("invent_price:"
								+ stock_parm.getDouble("INVENT_PRICE", 0));
						// ���м۸�
						double inventPrice = stock_parm.getDouble(
								"INVENT_PRICE", 0);
						inparm.setData("INVENT_PRICE", inventPrice);

						// luhai 2012-01-13 add verifyin_price end
						inparm.setData("ACTUAL_QTY", qty_out);
						inparm
								.setData("PHA_TYPE", parm.getValue("PHA_TYPE",
										i));
						inparm
								.setData("OPT_USER", parm.getValue("OPT_USER",
										i));
						inparm.setData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						inparm
								.setData("OPT_TERM", parm.getValue("OPT_TERM",
										i));
						inparm.setData("BOX_ESL_ID", parm.getValue(
								"BOX_ESL_ID", i));
						inparm.setData("BOXED_USER", parm.getValue(
								"BOXED_USER", i));
						inparm
								.setData("IS_BOXED", parm.getValue("IS_BOXED",
										i));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", 0));

						inparm.setData("SUP_ORDER_CODE", stock_parm.getValue(
								"SUP_ORDER_CODE", 0));
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;

					}

				}
			}
			// ָ�����ź�Ч��
			else if ("RET".equals(request_type) || "WAS".equals(request_type)
					|| "THO".equals(request_type)) {

				if (actual_qty == 0) {
					continue;
				}

				int batch_seq = parm.getInt("BATCH_SEQ", i);
				batch_no = parm.getValue("BATCH_NO", i);
				valid_date = parm.getValue("VALID_DATE", i).substring(0, 10);
				TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
						INDSQL
								.getIndStock(org_code, order_code, batch_seq
										+ "")));
				double out_amt = StringTool.round(inParmSeq.getDouble(
						"STOCK_RETAIL_PRICE", 0)
						* actual_qty, 2);
				// *****************************************************
				// luhai 2012-01-12 ҩƷ��ת����batchSeq������ת end
				// *****************************************************
				// ���¿��(���뵥����)
				result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
						request_type, org_code, order_code, batch_seq,
						actual_qty, out_amt, parm.getValue("OPT_USER", i),
						parm.getTimestamp("OPT_DATE", i),
						parm.getValue("OPT_TERM", i), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				// �������ⵥϸ��
				TParm inparm = new TParm();
				inparm.setData("DISPENSE_NO", parm.getValue("DISPENSE_NO", i));
				inparm.setData("SEQ_NO", seq);
				inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", batch_seq);
				inparm.setData("BATCH_NO", batch_no);
				inparm
						.setData("VALID_DATE", parm.getTimestamp("VALID_DATE",
								i));
				// �޸�ȡ��ת����-��ҩ��λ������ by liyh 20120801
				double tranUnitQty = getDosageQty(order_code);
				/*
				 * double tranUnitQty = getPhaTransUnitQty(order_code, "2") *
				 * getPhaTransUnitQty(order_code, "1");
				 */
				// fux modify
				if ("0".equals(unit_type)) {
					if (un == 3) {
						qty_out = actual_qty;
					} else {
						qty_out = actual_qty / tranUnitQty;
					}
				} else {
					qty_out = actual_qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
				inparm.setData("RETAIL_PRICE", inParmSeq.getDouble(
						"STOCK_RETAIL_PRICE", 0));
				// ԭ��4.0ʵ�ֵ�0����ǽ�stockprice
				// ��Ϊ���ռ۸񣬱���ԭ���߼�����������verfyinprice���ʴ��߼����Ķ�
				double stock_price = inParmSeq.getDouble("VERIFYIN_PRICE", 0);
				double verifyinPrice = inParmSeq.getDouble("VERIFYIN_PRICE", 0);
				// luhai 2012-01-13 add verfiyin_price begin
				// luhai modify 2012-05-03 add ���ռ۸���Ϊ��浥λ�����ձ���ȡֵ�����ӷ�ҩ��λ����ת���ʽ��м���
				// begin
				String orderCode = parm.getValue("ORDER_CODE", i);
				double tmpVerifyPrice = getVerifyInStockPrice(org_code,
						orderCode, batch_seq);
				verifyinPrice = tmpVerifyPrice > 0 ? tmpVerifyPrice
						/ tranUnitQty : verifyinPrice;

				inparm.setData("VERIFYIN_PRICE", verifyinPrice);

				inparm.setData("STOCK_PRICE", stock_price);
				inparm.setData("ACTUAL_QTY", qty_out);

				// ���м۸�

				String sql = SPCSQL.getIndStock(org_code, orderCode, String
						.valueOf(batch_seq));
				TParm validParm = new TParm(TJDODBTool.getInstance()
						.select(sql));
				double inventPrice = 0;
				String supCode = "";
				String supOrderCode = "";
				if (validParm.getCount() > 0) {
					inventPrice = validParm.getRow(0).getDouble("INVENT_PRICE");
					supCode = validParm.getRow(0).getValue("SUP_CODE");
					supOrderCode = validParm.getRow(0).getValue(
							"SUP_ORDER_CODE");
				}
				inparm.setData("INVENT_PRICE", inventPrice);

				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				inparm.setData("BOX_ESL_ID", parm.getValue("BOX_ESL_ID", i));
				inparm.setData("BOXED_USER", parm.getValue("BOXED_USER", i));
				inparm.setData("IS_BOXED", parm.getValue("IS_BOXED", i));
				// luhai 2012-01-12 ����batch_seq
				inparm.setData("SUP_CODE", supCode);

				inparm.setData("SUP_ORDER_CODE", supOrderCode);
				result = IndDispenseDTool.getInstance().onInsertD(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				seq++;
			}

			// ��������������;�� yuanxm���� 2013/5/6����Ҫ�� ,������
			TParm searchParm = new TParm();
			searchParm.setData("ORG_CODE", org_code);
			searchParm.setData("ORDER_CODE", order_code);
			result = IndStockMTool.getInstance().onQuery(searchParm);
			if (result.getCount("ORG_CODE") > 0) {

				/** ��;������ */
				TParm inparm1 = new TParm();
				inparm1.setData("ORG_CODE", org_code);
				inparm1.setData("ORDER_CODE", order_code);
				// ������
				inparm1.setData("BUY_UNRECEIVE_QTY", actual_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm1, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		/**
		 * ���ķ���ȥ������������� for(int i = 0 ; i < newParmD.getCount("ORDER_CODE")
		 * ;i++){ TParm rowParm = newParmD.getRow(i); String orderCode =
		 * rowParm.getValue("ORDER_CODE") ; actual_qty =
		 * rowParm.getDouble("ACTUAL_QTY"); if ("0".equals(unit_type)) {
		 * actual_qty = actual_qty * getPhaTransUnitQty(order_code, "2"); } else
		 * { actual_qty = actual_qty; } TParm updateParm = new TParm();
		 * updateParm.setData("ORDER_CODE",orderCode);
		 * updateParm.setData("ORG_CODE",org_code);
		 * updateParm.setData("LOCK_QTY",actual_qty); result =
		 * IndStockMTool.getInstance().onUpdateMinusLockQty(updateParm, conn);
		 * if (result.getErrCode() < 0) { return result; } }
		 */

		return result;
	}

	/**
	 * ����������ҵ(���⼴���)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutIn(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���뵥����
		String request_type = parm.getValue("REQTYPE_CODE");
		String out_org_code = parm.getValue("OUT_ORG_CODE");
		String in_org_code = parm.getValue("IN_ORG_CODE");
		// ��λ����
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// ���ע��
		boolean in_flg = parm.getBoolean("IN_FLG");
		// �ж��Ƿ��Զ����ɱ��۴��������
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// ҩƷ����(���뵥�������)
		result = onIndDispenseOutIn(parmD, out_org_code, in_org_code,
				unit_type, request_type, in_flg, reuprice_flg, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������뵥״̬��ʵ�ʳ��������
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ҩƷ�������(���뵥�������)
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	public TParm onIndDispenseOutIn(TParm parm, String out_org_code,
			String in_org_code, String unit_type, String request_type,
			boolean in_flg, String reuprice_flg, TConnection conn) {
		String order_code = "";
		int seq = 1;
		double actual_qty = 0;
		String batch_no = "";
		String valid_date = "";
		// �����Ϣ
		TParm stock_in = new TParm();
		TParm result = new TParm();
		// ������ҵ
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// ʵ�ʳ�������
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// ��ָ�����ź�Ч��
			if ("DEP".equals(request_type) || "TEC".equals(request_type)
					|| "GIF".equals(request_type) || "COS".equals(request_type)
					|| "EXM".equals(request_type)) {
				// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(out_org_code, order_code, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);

					if (qty >= actual_qty) {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", j)
								* actual_qty, 2);
						// ���¿��(���뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type,
										out_org_code, order_code, batch_seq,
										actual_qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						// ���������Ϣ
						stock_in.addData("ORDER_CODE", order_code);
						stock_in.addData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						stock_in.addData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						stock_in.addData("IN_QTY", actual_qty);
						stock_in.addData("RETAIL_PRICE", parm.getDouble(
								"RETAIL_PRICE", i));
						stock_in.addData("OPT_USER", parm.getValue("OPT_USER",
								i));
						stock_in.addData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						stock_in.addData("OPT_TERM", parm.getValue("OPT_TERM",
								i));

						if (result.getErrCode() < 0) {
							return result;
						}

						// �������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", parm.getValue(
								"DISPENSE_NO", i));
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", parm.getInt(
								"REQUEST_SEQ", i));
						inparm.setData("ORDER_CODE", parm.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", stock_parm.getInt(
								"BATCH_SEQ", j));
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						if ("0".equals(unit_type)) {
							qty_out = actual_qty
									/ getPhaTransUnitQty(order_code, "2");
						} else {
							qty_out = actual_qty;
						}
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));
						inparm.setData("RETAIL_PRICE", stock_parm.getDouble(
								"RETAIL_PRICE", j));
						String verifyinPrice = stock_parm.getValue(
								"VERIFYIN_PRICE", j);
						inparm.setData("VERIFYIN_PRICE", verifyinPrice);
						inparm.setData("STOCK_PRICE", verifyinPrice);

						inparm.setData("ACTUAL_QTY", qty_out);
						inparm
								.setData("PHA_TYPE", parm.getValue("PHA_TYPE",
										i));
						inparm
								.setData("OPT_USER", parm.getValue("OPT_USER",
										i));
						inparm.setData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						inparm
								.setData("OPT_TERM", parm.getValue("OPT_TERM",
										i));
						inparm.setData("BOX_ESL_ID", parm.getValue(
								"BOX_ESL_ID", i));
						inparm.setData("BOXED_USER", parm.getValue(
								"BOXED_USER", i));
						inparm
								.setData("IS_BOXED", parm.getValue("IS_BOXED",
										i));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));
						inparm.setData("INVENT_PRICE", stock_parm.getValue(
								"INVENT_PRICE", j));
						inparm.setData("SUP_ORDER_CODE", stock_parm.getValue(
								"SUP_ORDER_CODE", j));

						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", i)
								* qty, 2);
						// ���¿��(���뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type,
										out_org_code, order_code, batch_seq,
										qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);

						// ���������Ϣ
						stock_in.addData("ORDER_CODE", order_code);
						stock_in.addData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						stock_in.addData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						stock_in.addData("IN_QTY", qty);
						stock_in.addData("RETAIL_PRICE", parm.getDouble(
								"RETAIL_PRICE", i));
						stock_in.addData("OPT_USER", parm.getValue("OPT_USER",
								i));
						stock_in.addData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						stock_in.addData("OPT_TERM", parm.getValue("OPT_TERM",
								i));

						if (result.getErrCode() < 0) {
							return result;
						}

						// �������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", parm.getValue(
								"DISPENSE_NO", i));
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", parm.getInt(
								"REQUEST_SEQ", i));
						inparm.setData("ORDER_CODE", parm.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", stock_parm.getInt(
								"BATCH_SEQ", j));
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						if ("0".equals(unit_type)) {
							qty_out = qty / getPhaTransUnitQty(order_code, "2");
						} else {
							qty_out = qty;
						}
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));
						inparm.setData("RETAIL_PRICE", stock_parm.getDouble(
								"RETAIL_PRICE", j));
						String verifyinPrice = stock_parm.getValue(
								"VERIFYIN_PRICE", j);
						inparm.setData("VERIFYIN_PRICE", verifyinPrice);
						inparm.setData("STOCK_PRICE", verifyinPrice);

						inparm.setData("ACTUAL_QTY", qty_out);
						inparm
								.setData("PHA_TYPE", parm.getValue("PHA_TYPE",
										i));
						inparm
								.setData("OPT_USER", parm.getValue("OPT_USER",
										i));
						inparm.setData("OPT_DATE", parm.getTimestamp(
								"OPT_DATE", i));
						inparm
								.setData("OPT_TERM", parm.getValue("OPT_TERM",
										i));
						inparm.setData("BOX_ESL_ID", parm.getValue(
								"BOX_ESL_ID", i));
						inparm.setData("BOXED_USER", parm.getValue(
								"BOXED_USER", i));
						inparm
								.setData("IS_BOXED", parm.getValue("IS_BOXED",
										i));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));
						inparm.setData("INVENT_PRICE", stock_parm.getValue(
								"INVENT_PRICE", j));
						inparm.setData("SUP_ORDER_CODE", stock_parm.getValue(
								"SUP_ORDER_CODE", j));

						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// ���³�����
						actual_qty = actual_qty - qty;
						// ����ʵ�ʳ�������
						qty_out = qty_out + qty;
					}
				}
			}
			// ָ�����ź�Ч��
			else if ("RET".equals(request_type) || "WAS".equals(request_type)
					|| "THO".equals(request_type)) {

				int batch_seq = parm.getInt("BATCH_SEQ", i);
				// luhai 2012-01-13 modify ֱ�Ӳ���batch_seq ���д��� end
				double out_amt = StringTool.round(parm.getDouble(
						"RETAIL_PRICE", i)
						* actual_qty, 2);
				// ���¿��(���뵥����)
				result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
						request_type, out_org_code, order_code, batch_seq,
						actual_qty, out_amt, parm.getValue("OPT_USER", i),
						parm.getTimestamp("OPT_DATE", i),
						parm.getValue("OPT_TERM", i), conn);

				// ���������Ϣ
				stock_in.addData("ORDER_CODE", order_code);
				stock_in.addData("BATCH_NO", batch_no);
				stock_in.addData("VALID_DATE", valid_date);
				stock_in.addData("IN_QTY", actual_qty);
				stock_in.addData("RETAIL_PRICE", parm.getDouble("RETAIL_PRICE",
						i));
				stock_in.addData("OPT_USER", parm.getValue("OPT_USER", i));
				stock_in.addData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				stock_in.addData("OPT_TERM", parm.getValue("OPT_TERM", i));

				if (result.getErrCode() < 0) {
					return result;
				}

				// �������ⵥϸ��
				TParm inparm = new TParm();
				inparm.setData("DISPENSE_NO", parm.getValue("DISPENSE_NO", i));
				inparm.setData("SEQ_NO", seq);
				inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", batch_seq);
				inparm.setData("BATCH_NO", batch_no);
				inparm
						.setData("VALID_DATE", parm.getTimestamp("VALID_DATE",
								i));
				if ("0".equals(unit_type)) {
					qty_out = actual_qty / getPhaTransUnitQty(order_code, "2");
				} else {
					qty_out = actual_qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
				inparm.setData("RETAIL_PRICE", parm
						.getDouble("RETAIL_PRICE", i));
				double stock_price = parm.getDouble("STOCK_PRICE", i);
				inparm.setData("STOCK_PRICE", stock_price);
				// luhai 2012-1-13 add verifyin_price begin
				String orgCode = out_org_code;
				TParm stockParm = IndStockDTool.getInstance()
						.onQueryStockBatchSeq(orgCode, order_code, batch_no,
								valid_date);
				String verifyinPrice = "0.0";
				if (stockParm.getCount("VERIFYIN_PRICE") > 0) {
					verifyinPrice = stockParm.getValue("VERIFYIN_PRICE", 0);
				}
				inparm.setData("VERIFYIN_PRICE", verifyinPrice);
				// luhai 2012-1-13 add verifyin_price end
				inparm.setData("ACTUAL_QTY", qty_out);
				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				inparm.setData("BOX_ESL_ID", parm.getValue("BOX_ESL_ID", i));
				inparm.setData("BOXED_USER", parm.getValue("BOXED_USER", i));
				inparm.setData("IS_BOXED", parm.getValue("IS_BOXED", i));
				inparm.setData("SUP_CODE", "");
				inparm.setData("INVENT_PRICE", 0);
				inparm.setData("SUP_ORDER_CODE", "");
				result = IndDispenseDTool.getInstance().onInsertD(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				seq++;
			}
		}

		// �����ҵ
		if (in_flg) {
			// �������ź�Ч�ڲ�ѯҩƷ,���ڵĻ����¿����,�����ڵĻ�ȡ���������Ž�������
			List list = new ArrayList();
			double dosage_qty = 0;
			int batch_seq = 0;
			double retail_price = 0;
			for (int i = 0; i < stock_in.getCount("ORDER_CODE"); i++) {
				order_code = stock_in.getValue("ORDER_CODE", i);
				// ������б�
				list.add(order_code);
				dosage_qty = stock_in.getDouble("IN_QTY", i);
				retail_price = stock_in.getDouble("RETAIL_PRICE", i)
						/ dosage_qty;
				valid_date = stock_in.getValue("VALID_DATE", i).replace('/',
						'-').substring(0, 10);
				batch_no = stock_in.getValue("BATCH_NO", i);
				String opt_user = stock_in.getValue("OPT_USER", i);
				Timestamp opt_date = stock_in.getTimestamp("OPT_DATE", i);
				String opt_term = stock_in.getValue("OPT_TERM", i);
				result = IndStockDTool.getInstance()
						.onUpdateStockByBatchVaildIn(request_type, in_org_code,
								order_code, batch_seq, valid_date, batch_no,
								dosage_qty, retail_price, opt_user, opt_date,
								opt_term, list, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** �ƶ���Ȩƽ���ɱ� */
			double in_qty = 0;
			double d_qty = 0;
			double stock_price = 0;
			double in_amt = 0;
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				in_qty = parm.getDouble("ACTUAL_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				stock_price = parm.getDouble("STOCK_PRICE", i);
				in_amt = StringTool.round(in_qty * stock_price, 2);
				in_qty = StringTool.round(in_qty * d_qty, 2);
				stock_price = getPhaBaseStockPrice(parm.getValue("ORDER_CODE",
						i), in_amt, in_qty);
				TParm stock_price_parm = new TParm();
				stock_price_parm.setData("ORDER_CODE", parm.getValue(
						"ORDER_CODE", i));
				stock_price_parm.setData("STOCK_PRICE", stock_price);
				result = PhaBaseTool.getInstance().onUpdateStockPrice(
						stock_price_parm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** �����۸��� */
			if ("Y".equals(reuprice_flg)) {
				for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
					d_qty = parm.getDouble("DOSAGE_QTY", i);
					stock_price = StringTool.round(parm.getDouble(
							"STOCK_PRICE", i)
							/ d_qty, 4);
					TParm trade_price_parm = new TParm();
					trade_price_parm.setData("ORDER_CODE", parm.getValue(
							"ORDER_CODE", i));
					trade_price_parm.setData("TRADE_PRICE", stock_price);
					result = PhaBaseTool.getInstance().onUpdateTradePrice(
							trade_price_parm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		}

		return result;
	}

	/**
	 * �������(���뵥���)
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	public TParm onIndDispenseOtherIn(TParm parm, String org_code,
			String unit_type, String request_type, TConnection conn) {
		String order_code = "";
		int seq = 1;
		double actual_qty = 0;
		String batch_no = "";
		String valid_date = "";
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// ʵ���������
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// ָ�����ź�Ч��
			if ("THI".equals(request_type)) {
				// luhai modify 2012-01-13 �ĳɸ���batchSeq Ψһ��ʾҩƷ begin
				// ����ҩ���š�ҩƷ���롢ҩƷ�����ź�Ч�ڲ�ѯ�������
				// batch_no = parm.getValue("BATCH_NO", i);
				// valid_date = parm.getValue("VALID_DATE", i).substring(0, 10);
				// TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
				// INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
				// valid_date)));
				// if (inParmSeq.getErrCode() < 0) {
				// return inParmSeq;
				// }
				// int batch_seq = inParmSeq.getInt("BATCH_SEQ", 0);
				int batch_seq = parm.getInt("BATCH_SEQ", i);
				batch_no = parm.getValue("BATCH_NO", i);
				valid_date = parm.getValue("VALID_DATE", i).substring(0, 10);
				// luhai modify 2012-01-13 �ĳɸ���batchSeq Ψһ��ʾҩƷ end
				double in_amt = StringTool.round(parm.getDouble("RETAIL_PRICE",
						i)
						* actual_qty, 2);
				// ������ⵥϸ��
				TParm inparm = new TParm();
				inparm.setData("DISPENSE_NO", parm.getValue("DISPENSE_NO", i));
				inparm.setData("SEQ_NO", seq);
				inparm.setData("REQUEST_SEQ", parm.getInt("REQUEST_SEQ", i));
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				inparm.setData("BATCH_SEQ", batch_seq);
				inparm.setData("BATCH_NO", batch_no);
				inparm
						.setData("VALID_DATE", parm.getTimestamp("VALID_DATE",
								i));
				inparm.setData("QTY", parm.getDouble("QTY", i) - qty_out);
				if ("0".equals(unit_type)) {
					qty_out = actual_qty / getPhaTransUnitQty(order_code, "2");
				} else {
					qty_out = actual_qty;
				}
				inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
				inparm.setData("RETAIL_PRICE", parm
						.getDouble("RETAIL_PRICE", i));
				double stock_price = parm.getDouble("STOCK_PRICE", i);
				inparm.setData("STOCK_PRICE", stock_price);
				// luhai 2012-01-13 add verifyinPrice begin
				TParm indStockResult = new TParm(TJDODBTool.getInstance()
						.select(
								INDSQL.getIndStockBatchSeq(org_code,
										order_code, batch_seq + "")));
				double verifyinPrice = 0.0;
				int batchSeqCount = indStockResult.getCount("BATCH_SEQ");
				double inventPrice = 0;
				String supCode = "";
				String supOrderCode = "";
				if (batchSeqCount > 0) {
					// 1�õ��ĸòɹ��۸�û��*ת����
					verifyinPrice = indStockResult.getDouble("VERIFYIN_PRICE",
							0);
					// 2���ɹ��ۼ���ת���ʴ���
					double tranUnitQty = getPhaTransUnitQty(order_code, "2")
							* getPhaTransUnitQty(order_code, "1");
					verifyinPrice = verifyinPrice * tranUnitQty;
					inventPrice = indStockResult.getDouble("INVENT_PRICE", 0);
					supCode = indStockResult.getValue("SUP_CODE", 0);
					supOrderCode = indStockResult.getValue("SUP_ORDER_CODE", 0);
				}

				inparm.setData("INVENT_PRICE", inventPrice);
				inparm.setData("VERIFYIN_PRICE", verifyinPrice);
				// luhai 2012-01-13 add verifyinPrice end
				inparm.setData("ACTUAL_QTY", qty_out);
				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				Timestamp date = SystemTool.getInstance().getDate();
				inparm.setData("BOX_ESL_ID", "-1");
				inparm.setData("BOXED_DATE", date);
				inparm.setData("IS_BOXED", "Y");
				inparm.setData("SUP_CODE", supCode);
				inparm.setData("BOXED_USER", Operator.getID());
				inparm.setData("SUP_ORDER_CODE", supOrderCode);
				result = IndDispenseDTool.getInstance().onInsertD(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				seq++;
			}
		}
		return result;
	}

	/**
	 * ���������ҵ
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseIn(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���뵥���Զ�ά��
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		TParm parmM = parm.getParm("OUT_M");
		// ���뵥����
		String request_type = parmM.getValue("REQTYPE_CODE");

		// ����Ƿ�ȫ���ϼ����
		String checkPutway = parmM.getValue("CHECK_PUTAWAY");
		if (checkPutway != null && checkPutway.equals("Y")) {
			// System.out.println("---INDTool.java-----checkPutway != null && checkPutway.equals(Y)--------");
			TParm inParm = new TParm();
			inParm.setData("DISPENSE_NO", parmM.getValue("DISPENSE_NO"));
			inParm.setData("IS_PUTAWAY", "N");
			TParm queryParm = IndDispenseDTool.getInstance().onQueryDispenseD(
					inParm);
			int count = queryParm.getCount();
			if (count == 1) {
				// �������������Ϣ
				result = IndDispenseMTool.getInstance().onUpdateM(parmM, conn);
			}
		} else {
			// �������������Ϣ
			result = IndDispenseMTool.getInstance().onUpdateM(parmM, conn);
		}
		if (result.getErrCode() < 0) {
			return result;
		}
		TParm parmD = parm.getParm("OUT_D");
		for (int i = 0; i < parmD.getCount("DISPENSE_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("DISPENSE_NO", parmD.getValue("DISPENSE_NO", i));
			inparm.setData("SEQ_NO", parmD.getInt("SEQ_NO", i));
			double stock_price = parmD.getDouble("STOCK_PRICE", i);
			inparm.setData("STOCK_PRICE", stock_price);
			inparm.setData("ACTUAL_QTY", parmD.getDouble("ACTUAL_QTY", i));
			inparm.setData("OPT_USER", parmD.getValue("OPT_USER", i));
			inparm.setData("OPT_DATE", parmD.getTimestamp("OPT_DATE", i));
			inparm.setData("OPT_TERM", parmD.getValue("OPT_TERM", i));
			inparm.setData("IS_PUTAWAY", parmD.getValue("IS_PUTAWAY", i));
			inparm.setData("PUTAWAY_USER", parmD.getValue("PUTAWAY_USER", i));
			// �������ϸ����Ϣ
			result = IndDispenseDTool.getInstance().onUpdateD(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		// �����ҵ
		if (parm.existData("IN_ORG")) {
			// fux modify 20141107
			// // ��ⲿ��
			// String in_org = parm.getValue("IN_ORG");
			//			
			// //���ⲿ��
			// String out_org = parm.getValue("OUT_ORG");
			// ��ⲿ��
			String in_org = parm.getValue("IN_ORG");

			// ���ⲿ��
			String out_org = parm.getValue("OUT_ORG");
			System.out.println("in_org:" + in_org);
			System.out.println("out_org:" + out_org);
			// �Ƿ����
			boolean flg = parm.getBoolean("IN_FLG");
			// ҩƷ����
			String order_code = "";
			// ��λ����
			String unit_type = parmM.getValue("UNIT_TYPE");
			// �ۿ���
			double dosage_qty = 0;
			// ���ۼ�
			double retail_price = 0;
			// ���ռ�
			double verifyinPrice = 0;
			// �������
			int batch_seq = 0;
			String supCode = "";
			String regionCode = "";
			String supOrderCode = "";
			if (flg) {
				List list = new ArrayList();
				Map<String, Integer> batchSeqMap = new HashMap<String, Integer>();
				System.out.println("parmD:" + parmD);
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					order_code = parmD.getValue("ORDER_CODE", i);

					// ������б�
					list.add(order_code);
					if ("0".equals(unit_type)) {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i)
								* parmD.getDouble("DOSAGE_QTY", i);
					} else {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i);
					}

					TParm searchParm = new TParm();
					/**
					 * IND_STOCKM��ѯ�Ƿ���ڣ����ڣ�������;��������������
					 */
					searchParm.setData("ORG_CODE", in_org);
					searchParm.setData("ORDER_CODE", order_code);
					result = IndStockMTool.getInstance().onQueryAll(searchParm);

					supCode = parmD.getValue("SUP_CODE", i);
					supOrderCode = parmD.getValue("SUP_ORDER_CODE", i);
					regionCode = parmD.getValue("REGION_CODE", i);
					if (result.getCount("ORG_CODE") <= 0) {

						// ��������
						TParm inparm = new TParm();
						inparm.setData("ORG_CODE", in_org);
						inparm.setData("ORDER_CODE", order_code);
						inparm.setData("REGION_CODE", regionCode);
						inparm.setData("OPT_USER", parmD
								.getValue("OPT_USER", i));
						inparm.setData("OPT_DATE", parmD.getTimestamp(
								"OPT_DATE", i));
						inparm.setData("OPT_TERM", parmD
								.getValue("OPT_TERM", i));
						inparm.setData("MATERIAL_LOC_CODE", "");
						inparm.setData("MATERIAL_LOC_DESC", "");
						inparm.setData("ELETAG_CODE", "");
						inparm.setData("MATERIAL_LOC_SEQ", "");
						inparm.setData("SUP_CODE", supCode);
						// fux modify 20141028
						// inparm.setData("LOCK_QTY",0);
						inparm.setData("LOCK_QTY", dosage_qty);
						result = IndStockMTool.getInstance().onInsert(inparm,
								conn);
						if (result.getErrCode() < 0) {
							return result;
						}
					} else {
						TParm inparm1 = new TParm();
						inparm1.setData("ORG_CODE", in_org);
						inparm1.setData("ORDER_CODE", order_code);
						// ������
						inparm1.setData("BUY_UNRECEIVE_QTY", -1 * dosage_qty);
						inparm1.setData("LOCK_QTY", dosage_qty);
						result = IndStockMTool.getInstance()
								.onUpdateBuyUnreceiveQty(inparm1, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
					}

					String batch_no = parmD.getValue("BATCH_NO", i);
					String valid_date = parmD.getValue("VALID_DATE", i)
							.replace('/', '-').substring(0, 10);

					double verprice = StringTool.round(parmD.getDouble(
							"VERIFYIN_PRICE", i), 4);
					String verpriceDStr = String.valueOf(verprice);

					// �õ�����
					TParm getSEQ = IndStockDTool.getInstance()
							.onQueryStockBatchSeqBy(in_org, order_code,
									batch_no, valid_date, verpriceDStr,
									supCode, supOrderCode);
					if (getSEQ.getCount("BATCH_SEQ") > 0) {
						batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
					} else {
						getSEQ = IndStockDTool.getInstance()
								.onQueryStockMaxBatchSeq(in_org, order_code);

						// �ҵ���ǰ��������κż�1 ��û���ҵ�Ĭ��Ϊ1
						if (getSEQ.getCount("BATCH_SEQ") > 0) {
							// ���+1�������
							batch_seq = getSEQ.getInt("BATCH_SEQ", 0) + 1;
						} else {
							batch_seq = 1;
						}
					}

					// ͬһƷ��ҩƷ�ظ���⣬BATCH_SEQ����������
					Set<String> keySet = batchSeqMap.keySet();
					if (keySet.contains(order_code)) {
						Integer batchSeqValue = batchSeqMap.get(order_code);
						batchSeqValue += 1;
						batchSeqMap.put(order_code, batchSeqValue);
						batch_seq = batchSeqValue;
					} else {
						batchSeqMap.put(order_code, batch_seq);
					}

					// ���м۸�
					double inventPrice = parmD.getDouble("INVENT_PRICE", i);

					retail_price = parmD.getDouble("RETAIL_PRICE", i);
					verifyinPrice = parmD.getDouble("VERIFYIN_PRICE", i);

					String opt_user = parmD.getValue("OPT_USER", i);
					Timestamp opt_date = parmD.getTimestamp("OPT_DATE", i);
					String opt_term = parmD.getValue("OPT_TERM", i);

					result = IndStockDTool.getInstance()
							.onUpdateStockByBatchVaildInSpc(request_type,
									in_org, order_code, batch_seq, valid_date,
									batch_no, dosage_qty, retail_price,
									opt_user, opt_date, opt_term, list,
									supCode, inventPrice, verifyinPrice,
									regionCode, supOrderCode, conn);
					if (result.getErrCode() < 0) {
						return result;
					}

				}

				/** �ƶ���Ȩƽ���ɱ� */
				double in_qty = 0;
				double d_qty = 0;
				double stock_price = 0;
				double in_amt = 0;
				/*
				 * for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
				 * in_qty = parmD.getDouble("ACTUAL_QTY", i); d_qty =
				 * parmD.getDouble("DOSAGE_QTY", i); stock_price =
				 * parmD.getDouble("STOCK_PRICE", i); in_amt =
				 * StringTool.round(in_qty * stock_price, 2); in_qty =
				 * StringTool.round(in_qty * d_qty *
				 * parmD.getDouble("STOCK_QTY", i), 2); stock_price =
				 * getPhaBaseStockPrice(parmD.getValue("ORDER_CODE", i), in_amt,
				 * in_qty); TParm stock_price_parm = new TParm();
				 * stock_price_parm.setData("ORDER_CODE",
				 * parmD.getValue("ORDER_CODE", i));
				 * stock_price_parm.setData("STOCK_PRICE", stock_price); result
				 * =
				 * PhaBaseTool.getInstance().onUpdateStockPrice(stock_price_parm
				 * , conn); if (result.getErrCode() < 0) { return result; } }
				 */

				/** �����۸��� */
				if ("Y".equals(reuprice_flg)) {
					for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
						d_qty = parmD.getDouble("DOSAGE_QTY", i);
						stock_price = StringTool.round(parmD.getDouble(
								"STOCK_PRICE", i)
								/ d_qty / parmD.getDouble("STOCK_QTY", i), 4);
						TParm trade_price_parm = new TParm();
						trade_price_parm.setData("ORDER_CODE", parmD.getValue(
								"ORDER_CODE", i));
						trade_price_parm.setData("TRADE_PRICE", stock_price);
						result = PhaBaseTool.getInstance().onUpdateTradePrice(
								trade_price_parm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * �����������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertDispenseOtherIn(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���뵥���Զ�ά��
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ���뵥����
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");
		// ��λ����
		String unit_type = parmM.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// �������(���뵥���)
		result = onIndDispenseOtherIn(parmD, org_code, unit_type, request_type,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		// // �в�ҩ�Զ�ά���շѼ���������
		// if (parm.existData("PARM_C")) {
		// TParm parmC = parm.getParm("PARM_C");
		// // /////////////////
		// }

		// �����ҵ
		if (parm.existData("IN_ORG")) {
			// ��ⲿ��
			String in_org = parm.getValue("IN_ORG");
			// �Ƿ����
			boolean flg = parm.getBoolean("IN_FLG");
			// ���κ���Ч��
			String batchvalid = parm.getValue("BATCH");
			// ҩƷ����
			String order_code = "";
			// �ۿ���
			double dosage_qty = 0;
			// ���ۼ�
			double retail_price = 0;
			// �������
			int batch_seq = 0;
			if (flg) {
				List list = new ArrayList();
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					order_code = parmD.getValue("ORDER_CODE", i);
					list.add(order_code);
					if ("0".equals(unit_type)) {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i)
								* parmD.getDouble("DOSAGE_QTY", i);
					} else {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i);
					}
					// ָ�����ź�Ч��(�����ҵδ���ָ��Ч������,ץȡ����������+1����)
					batch_seq = parmD.getInt("BATCH_SEQ", i);
					retail_price = parmD.getDouble("RETAIL_PRICE", i)
							/ parmD.getDouble("DOSAGE_QTY", i);
					String valid_date = parmD.getValue("VALID_DATE", i)
							.replace('/', '-').substring(0, 10);
					String batch_no = parmD.getValue("BATCH_NO", i);
					String opt_user = parmD.getValue("OPT_USER", i);
					Timestamp opt_date = parmD.getTimestamp("OPT_DATE", i);
					String opt_term = parmD.getValue("OPT_TERM", i);
					result = IndStockDTool.getInstance()
							.onUpdateStockByBatchVaildIn(request_type, in_org,
									order_code, batch_seq, valid_date,
									batch_no, dosage_qty, retail_price,
									opt_user, opt_date, opt_term, list, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}

				/** �ƶ���Ȩƽ���ɱ� */
				double in_qty = 0;
				double d_qty = 0;
				double stock_price = 0;
				double in_amt = 0;
				/*
				 * for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
				 * in_qty = parmD.getDouble("ACTUAL_QTY", i); d_qty =
				 * parmD.getDouble("DOSAGE_QTY", i); stock_price =
				 * parmD.getDouble("STOCK_PRICE", i); in_amt =
				 * StringTool.round(in_qty * stock_price, 2); in_qty =
				 * StringTool.round(in_qty * d_qty, 2); stock_price =
				 * getPhaBaseStockPrice(parm.getValue("ORDER_CODE", i), in_amt,
				 * in_qty); TParm stock_price_parm = new TParm();
				 * stock_price_parm.setData("ORDER_CODE",
				 * parmD.getValue("ORDER_CODE", i));
				 * stock_price_parm.setData("STOCK_PRICE", stock_price); result
				 * =
				 * PhaBaseTool.getInstance().onUpdateStockPrice(stock_price_parm
				 * , conn); if (result.getErrCode() < 0) { return result; } }
				 */

				/** �����۸��� */
				if ("Y".equals(reuprice_flg)) {
					for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
						d_qty = parmD.getDouble("DOSAGE_QTY", i);
						stock_price = StringTool.round(parm.getDouble(
								"STOCK_PRICE", i)
								/ d_qty, 4);
						TParm trade_price_parm = new TParm();
						trade_price_parm.setData("ORDER_CODE", parmD.getValue(
								"ORDER_CODE", i));
						trade_price_parm.setData("TRADE_PRICE", stock_price);
						result = PhaBaseTool.getInstance().onUpdateTradePrice(
								trade_price_parm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
					}
				}
			}
		}
		// �������뵥״̬��ʵ�ʳ��������
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ȡ��ҩƷת����
	 * 
	 * @param order_code
	 *            ҩƷ����
	 * @param qty_type
	 *            ת�������� 1:����/��� 2:���/��ҩ 3:��ҩ/��ҩ (��λ-->С��λ)
	 * @return
	 */
	public double getPhaTransUnitQty(String order_code, String qty_type) {
		TParm parm = new TParm(Manager.getMedicine()
				.getPhaTransUnit(order_code));
		if ("1".equals(qty_type)) {
			return parm.getDouble("STOCK_QTY", 0);
		} else if ("2".equals(qty_type)) {
			return parm.getDouble("DOSAGE_QTY", 0);
		} else if ("3".equals(qty_type)) {
			return parm.getDouble("MEDI_QTY", 0);
		}
		return 1;
	}

	/**
	 * ȡ��ҩƷ��Ӧ��λ
	 * 
	 * @param order_code
	 *            ҩƷ����
	 * @param qty_type
	 *            ת�������� 1:����/��� 2:���/��ҩ 3:��ҩ/��ҩ (��λ-->С��λ)
	 * @return
	 */
	public double getPhaTransUnit(String order_code, String unit) {
		TParm parm = new TParm(Manager.getMedicine()
				.getPhaTransUnit(order_code));
		if (unit.equals(parm.getValue("PURCH_UNIT", 0).toString())) {
			return 1;
		} else if (unit.equals(parm.getValue("STOCK_UNIT", 0).toString())) {
			return 2;
		} else if (unit.equals(parm.getValue("DOSAGE_UNIT", 0).toString())) {
			return 3;
		}
		return 0;
	}

	/**
	 * ������ƽ���� (��Ȩƽ���ɱ�)
	 * 
	 * @param order_code
	 *            ҩƷ����
	 * @param verifyin_atm
	 *            ���ս��
	 * @param in_qty
	 *            �����(��ҩ��λ)
	 * @return ��Ȩƽ���ɱ�
	 */
	public double getPhaBaseStockPrice(String order_code, double verifyin_atm,
			double in_qty) {
		TParm result = new TParm();
		// ȡ�ÿ����
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		result = IndStockDTool.getInstance().onQueryStockQTY(parm);
		double qty = result.getDouble("QTY", 0);
		// System.out.println("�����" + qty);
		// ȡ��ԭ�ӳ�ƽ���ɱ�
		result = PhaBaseTool.getInstance().selectByOrder(order_code);
		double stock_price = result.getDouble("STOCK_PRICE", 0);
		// System.out.println("ԭ�ӳ�ƽ���ɱ�" + stock_price);
		// System.out.println("���ս��" + verifyin_atm);
		// System.out.println("�����" + in_qty);
		// �����Ȩƽ���ɱ�
		double new_price = (qty * stock_price + verifyin_atm) / (qty + in_qty);
		new_price = StringTool.round(new_price, 4);
		return new_price;
	}

	/**
	 * �������뵥��״̬��ʵ�ʳ��������
	 * 
	 * @param request_no
	 *            String
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateRequestFlgAndActual(String request_no, TParm parm,
			TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("REQUEST_SEQ"); i++) {
			String update_flg = "1";
			double qty = parm.getDouble("QTY_1", i);// 20150722 wangjc mdify
			double actual_qty = parm.getDouble("ACTUAL_QTY_1", i);// 20150722
			// wangjc
			// mdify
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", request_no);
			inparm.setData("SEQ_NO", parm.getInt("REQUEST_SEQ", i));
			inparm.setData("ACTUAL_QTY", actual_qty);
			inparm.setData("OPT_USER", parm.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM", i));
			if (qty == actual_qty) {
				update_flg = "3";
			}
			inparm.setData("UPDATE_FLG", update_flg);
			result = IndRequestDTool.getInstance().onUpdateActualQty(inparm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// �������뵥״̬
		TParm parmT = new TParm(TJDODBTool.getInstance().update(
				INDSQL.onUpdateRequestFlg(request_no), conn));
		if (parmT.getErrCode() < 0) {
			return parmT;
		}
		return result;
	}

	/**
	 * �̵㶳��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertQtyCheck(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parm_Order = parm.getParm("ORDER");
		for (int i = 0; i < parm_Order.getCount("ORDER_CODE"); i++) {
			parm.setData("ORDER_CODE", parm_Order.getData("ORDER_CODE", i));
			parm.setData("BATCH_SEQ", parm_Order.getData("BATCH_SEQ", i));
			parm.setData("BATCH_NO", parm_Order.getData("BATCH_NO", i));
			parm.setData("VALID_DATE", parm_Order.getData("VALID_DATE", i));
			parm.setData("DOSAGE_UNIT", parm_Order.getData("DOSAGE_UNIT", i));
			parm.setData("STOCK_UNIT", parm_Order.getData("STOCK_UNIT", i));
			parm.setData("STOCK_PRICE", parm_Order.getData("STOCK_PRICE", i));
			parm.setData("TRADE_PRICE", parm_Order.getData("TRADE_PRICE", i));
			parm.setData("RETAIL_PRICE", parm_Order.getData("RETAIL_PRICE", i));
			parm.setData("STOCK_QTY", parm_Order.getData("STOCK_QTY", i));
			parm
					.setData("ACTUAL_CHECK_QTY", parm_Order.getData(
							"STOCK_QTY", i));
			// �õ����ռ۸� luhai 2012-1-23 begin
			String orderCode = parm_Order.getData("ORDER_CODE", i) + "";
			String batchSeq = parm_Order.getData("BATCH_SEQ", i) + "";
			String orgCode = parm.getValue("ORG_CODE");
			TParm indStokParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDStock(orgCode, orderCode, Integer
							.parseInt(batchSeq))));
			String verifyinPrice = indStokParm.getValue("VERIFYIN_PRICE", 0);
			parm.setData("VERIFYIN_PRICE", verifyinPrice);
			// �õ����ռ۸� luhai 2012-1-23 end
			// �̵㶳������
			result = IndQtyCheckTool.getInstance().onInsert(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// ����ҩƷ
			parm.setData("STOCK_FLG", "Y");
			parm.setData("FREEZE_TOT", parm_Order.getData("STOCK_QTY", i));
			result = IndStockDTool.getInstance().onUpdateQtyCheck(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// System.out.println("check-parm:" + parm);
		}
		return result;
	}

	/**
	 * �̵㱣������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateQtyCheck(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���������ж���ҩƷ��ʵ�������͵�����ȫ����Ϊ0��0-�����
		// ע�͵���Ϊ0 by liyh 20120726
		/*
		 * result = IndQtyCheckTool.getInstance().onUpdate(parm, conn); if
		 * (result.getErrCode() < 0) { return result; }
		 */
		// ѭ�������̵�����
		TParm parmD = parm.getParm("PARM_D");
		for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
			TParm inparm = new TParm();
			inparm.setData("QTY", parmD.getDouble("QTY", i));
			inparm.setData("ACTUAL_CHECKQTY_USER", parm.getData("OPT_USER"));
			inparm.setData("ACTUAL_CHECKQTY_DATE", parm.getData("OPT_DATE"));
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			inparm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			inparm.setData("ORDER_CODE", parmD.getValue("ORDER_CODE", i));
			inparm.setData("FROZEN_DATE", parm.getData("FROZEN_DATE"));
			// �ж��Ƿ����������ţ������ڵ�ȡ����������
			if (parmD.getData("BATCH_SEQ", i) == null
					|| "".equals(parmD.getValue("BATCH_SEQ", i))) {
				TParm batch = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getMaxSeqByQtyCheck(parm.getValue("ORG_CODE"),
								parmD.getValue("ORDER_CODE", i), parm
										.getValue("FROZEN_DATE"))));
				if (batch.getErrCode() < 0) {
					return batch;
				}
				inparm.setData("BATCH_SEQ", batch.getInt("BATCH_SEQ", 0));
			} else {
				inparm.setData("BATCH_SEQ", parmD.getInt("BATCH_SEQ", i));
			}
			result = IndQtyCheckTool.getInstance().onUpdateQtyCheck(inparm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * �������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateUnLock(TParm parm, TConnection conn) {
		// luhai modify 2012-04-25 begin
		// // ���ݼ��
		// if (parm == null)
		// return null;
		// // �����
		// TParm result = new TParm();
		// String org_code = parm.getValue("ORG_CODE");
		// String frozen_date = parm.getValue("FROZEN_DATE");
		// String order_code = "";
		// TParm parm_D = parm.getParm("PARM_D");
		// for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
		// //System.out.println("--------------------");
		// order_code = parm_D.getValue("ORDER_CODE", i);
		// if ("Y".equals(parm.getValue("VALID_FLG"))) {
		// TParm parmY = new TParm(TJDODBTool.getInstance().select(
		// INDSQL.getQtyCheckBatchSeqAndModi(org_code, order_code,
		// frozen_date)));
		// if (parmY.getErrCode() < 0) {
		// return parmY;
		// }
		// for (int j = 0; j < parmY.getCount("ORDER_CODE"); j++) {
		// // ���¿������� IND_STOCK
		// TParm parmIn = new TParm();
		// parmIn.setData("STOCK_FLG", "N");
		// parmIn.setData("CHECKMODI_QTY",
		// parmY.getDouble("MODI_QTY", j));
		// parmIn.setData("CHECKMODI_AMT",
		// parmY.getDouble("MODI_ATM", j));
		// parmIn.setData("FROZEN_DATE", frozen_date);
		// parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
		// parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
		// parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// parmIn.setData("ORG_CODE", org_code);
		// parmIn.setData("ORDER_CODE", order_code);
		// parmIn.setData("BATCH_SEQ", parmY.getInt("BATCH_SEQ", j));
		// // ���¿������� IND_STOCK
		// result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
		// parmIn, conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// // �����̵������ IND_QTYCHECK
		// parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
		// parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
		// result = IndQtyCheckTool.getInstance().onUpdateUnLock(
		// parmIn,
		// conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// }
		// }
		// else {
		// //System.out.println("1--------------------" + parm);
		// TParm parmIn = new TParm();
		// parmIn.setData("ORG_CODE", org_code);
		// parmIn.setData("ORDER_CODE", order_code);
		// parmIn.setData("STOCK_FLG", "N");
		// parmIn.setData("BATCH_SEQ", parm_D.getInt("BATCH_SEQ", i));
		// TParm parmY = new TParm(TJDODBTool.getInstance().select(
		// INDSQL.getQtyCheckBatchSeqAndModi(org_code, order_code,
		// frozen_date, parm_D.getInt("BATCH_SEQ", i))));
		// if (parmY.getErrCode() < 0) {
		// return parmY;
		// }
		// parmIn.setData("CHECKMODI_QTY",
		// parmY.getDouble("MODI_QTY", 0));
		// parmIn.setData("CHECKMODI_AMT",
		// parmY.getDouble("MODI_ATM", 0));
		// parmIn.setData("FROZEN_DATE", frozen_date);
		// parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
		// parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
		// parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
		//
		// // ���¿������� IND_STOCK
		// result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
		// parmIn, conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// // �����̵������ IND_QTYCHECK
		// parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
		// parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
		// result = IndQtyCheckTool.getInstance().onUpdateUnLock(
		// parmIn,
		// conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// }
		// }
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		String org_code = parm.getValue("ORG_CODE");
		String frozen_date = parm.getValue("FROZEN_DATE");
		String order_code = "";
		TParm parm_D = parm.getParm("PARM_D");
		Map orderMap = new HashMap();
		for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
			System.out.println("--------------------");
			order_code = parm_D.getValue("ORDER_CODE", i);
			if ("Y".equals(parm.getValue("VALID_FLG"))) {
				TParm parmY = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getQtyCheckBatchSeqAndModi(org_code, order_code,
								frozen_date)));
				if (parmY.getErrCode() < 0) {
					return parmY;
				}
				for (int j = 0; j < parmY.getCount("ORDER_CODE"); j++) {
					// ���¿������� IND_STOCK
					TParm parmIn = new TParm();
					parmIn.setData("STOCK_FLG", "N");
					parmIn.setData("CHECKMODI_QTY", parmY.getDouble("MODI_QTY",
							j));
					parmIn.setData("CHECKMODI_AMT", StringTool.round(parmY
							.getDouble("MODI_ATM", j), 2));
					parmIn.setData("FROZEN_DATE", frozen_date);
					parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
					parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
					parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
					parmIn.setData("ORG_CODE", org_code);
					parmIn.setData("ORDER_CODE", order_code);
					parmIn.setData("BATCH_SEQ", parmY.getInt("BATCH_SEQ", j));
					// ���¿������� IND_STOCK
					result = IndStockDTool.getInstance()
							.onUpdateUnLockQtyCheck(parmIn, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
					// �����̵������ IND_QTYCHECK
					parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
					parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
					result = IndQtyCheckTool.getInstance().onUpdateUnLock(
							parmIn, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			} else {
				System.out.println("1--------------------" + parm);
				TParm parmIn = new TParm();
				parmIn.setData("ORG_CODE", org_code);
				parmIn.setData("ORDER_CODE", order_code);
				parmIn.setData("STOCK_FLG", "N");
				parmIn.setData("BATCH_SEQ", parm_D.getInt("BATCH_SEQ", i));
				TParm parmY = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getQtyCheckBatchSeqAndModi(org_code, order_code,
								frozen_date, parm_D.getInt("BATCH_SEQ", i))));
				if (parmY.getErrCode() < 0) {
					return parmY;
				}
				System.out.println("sql::::"
						+ INDSQL.getQtyCheckBatchSeqAndModi(org_code,
								order_code, frozen_date, parm_D.getInt(
										"BATCH_SEQ", i)));
				System.out
						.println("MODI_QTY:" + parmY.getDouble("MODI_QTY", 0));
				System.out
						.println("MODI_ATM:" + parmY.getDouble("MODI_ATM", 0));
				parmIn.setData("CHECKMODI_QTY", parmY.getDouble("MODI_QTY", 0));
				parmIn.setData("CHECKMODI_AMT", parmY.getDouble("MODI_ATM", 0));
				parmIn.setData("FROZEN_DATE", frozen_date);
				parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
				parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
				parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
				// �õ����
				StringBuffer sqlbf = new StringBuffer();
				sqlbf
						.append(" SELECT (LAST_TOTSTOCK_QTY+IN_QTY-OUT_QTY+CHECKMODI_QTY)  AS QTY  ");
				sqlbf.append(" FROM IND_STOCK  ");
				sqlbf.append(" WHERE ORDER_CODE='" + order_code + "' ");
				sqlbf.append(" AND  BATCH_SEQ=" + parm_D.getInt("BATCH_SEQ", i)
						+ " ");
				sqlbf.append(" AND ORG_CODE='" + org_code + "' ");
				Map select = TJDODBTool.getInstance().select(sqlbf.toString());
				TParm resultStock = new TParm(select);
				double stock = resultStock.getDouble("QTY", 0);
				// ��治��
				if (stock + parmY.getDouble("MODI_QTY", 0) < 0) {
					orderMap.put(order_code, Math.abs((stock + parmY.getDouble(
							"MODI_QTY", 0))));
					parmIn.setData("CHECKMODI_QTY", parmY.getDouble("MODI_QTY",
							0)
							+ Math
									.abs((stock + parmY
											.getDouble("MODI_QTY", 0))));
				} else {
					Set keySet = orderMap.keySet();
					for (Object key : keySet) {
						// System.out.println("key:"+key);
						if ((key + "").equals(order_code)) {
							if ((stock + parmY.getDouble("MODI_QTY", 0) - Double
									.parseDouble((orderMap.get(key)) + "")) > 0) {
								double qtymodi = parmY.getDouble("MODI_QTY", 0)
										- Double
												.parseDouble((orderMap.get(key))
														+ "");
								parmIn.setData("CHECKMODI_QTY", qtymodi);
								System.out.println("������modiQty============="
										+ qtymodi);
								orderMap.put(key, 0);
							}
						}
					}
				}

				// ���¿������� IND_STOCK
				System.out.println("parmIn:::::::20141211:::::::::" + parmIn);
				result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
						parmIn, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				// �����̵������ IND_QTYCHECK
				parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
				parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
				result = IndQtyCheckTool.getInstance().onUpdateUnLock(parmIn,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			// //fux modify 20141103 ȫ���������
			// TParm inparm = new TParm();
			// inparm.setData("QTY", parm_D.getDouble("QTY", i));
			// inparm.setData("ACTUAL_CHECKQTY_USER", parm.getData("OPT_USER"));
			// inparm.setData("ACTUAL_CHECKQTY_DATE", parm.getData("OPT_DATE"));
			// inparm.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
			// inparm.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
			// inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			// inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			// inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			// inparm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			// inparm.setData("ORDER_CODE", parm_D.getValue("ORDER_CODE", i));
			// inparm.setData("FROZEN_DATE", parm.getData("FROZEN_DATE"));
			// // �ж��Ƿ����������ţ������ڵ�ȡ����������
			// if (parm_D.getData("BATCH_SEQ", i) == null ||
			// "".equals(parm_D.getValue("BATCH_SEQ", i))) {
			// TParm batch = new TParm(TJDODBTool.getInstance().select(
			// INDSQL.getMaxSeqByQtyCheck(parm.getValue("ORG_CODE"),
			// parm_D.getValue("ORDER_CODE", i),
			// parm.getValue("FROZEN_DATE"))));
			// if (batch.getErrCode() < 0) {
			// return batch;
			// }
			// inparm.setData("BATCH_SEQ", batch.getInt("BATCH_SEQ", 0));
			// } else {
			// inparm.setData("BATCH_SEQ", parm_D.getInt("BATCH_SEQ", i));
			// }
			// result = IndQtyCheckTool.getInstance().onUpdateQtyCheck(inparm,
			// conn);
			// if (result.getErrCode() < 0) {
			// return result;
			// }

		}

		return result;
		// end
	}

	/**
	 * ���Ĳĳ���
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertCosOut(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		String request_no = parm.getValue("REQUEST_NO");
		String check_flg = parm.getValue("CHECK_FLG");
		if ("Y".equals(check_flg)) {
			// 1.�������ⵥ����
			String dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
					"IND_DISPENSE", "No");
			TParm dispenseM = new TParm();
			dispenseM.setData("DISPENSE_NO", dispense_no);
			dispenseM.setData("REQTYPE_CODE", "COS");
			dispenseM.setData("REQUEST_NO", request_no);
			dispenseM.setData("REQUEST_DATE", parm.getData("REQUEST_DATE"));
			dispenseM.setData("APP_ORG_CODE", parm.getData("APP_ORG_CODE"));
			dispenseM.setData("TO_ORG_CODE", parm.getData("TO_ORG_CODE"));
			dispenseM.setData("URGENT_FLG", parm.getData("URGENT_FLG"));
			dispenseM.setData("DESCRIPTION", parm.getData("DESCRIPTION"));
			dispenseM.setData("DISPENSE_DATE", parm.getData("OPT_DATE"));
			dispenseM.setData("DISPENSE_USER", parm.getData("OPT_USER"));
			TNull tnull = new TNull(Timestamp.class);
			dispenseM.setData("WAREHOUSING_DATE", tnull);
			dispenseM.setData("WAREHOUSING_USER", "");
			dispenseM.setData("REASON_CHN_DESC", parm
					.getData("REASON_CHN_DESC"));
			dispenseM.setData("UNIT_TYPE", parm.getData("UNIT_TYPE"));
			dispenseM.setData("UPDATE_FLG", "3");
			dispenseM.setData("OPT_USER", parm.getData("OPT_USER"));
			dispenseM.setData("OPT_DATE", parm.getData("OPT_DATE"));
			dispenseM.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = IndDispenseMTool.getInstance().onInsertM(dispenseM, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// ȡ�����Ĳ����뵥ϸ��
			TParm requestD = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getRequestDByNo(request_no)));
			if (requestD.getErrCode() < 0) {
				return requestD;
			}
			String order_code = "";
			int seq = 1;
			double actual_qty = 0;
			String org_code = parm.getValue("TO_ORG_CODE");
			for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
				// ʵ�ʳ�������
				double qty_out = 0;
				order_code = requestD.getValue("ORDER_CODE", i);
				actual_qty = requestD.getDouble("QTY", i);
				// ��ָ�����ź�Ч��
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
					if (qty >= actual_qty) {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", j)
								* actual_qty, 2);
						// 2.���¿��(���Ĳ����뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut("COS", org_code,
										order_code, batch_seq, actual_qty,
										out_amt, parm.getValue("OPT_USER"),
										parm.getTimestamp("OPT_DATE"),
										parm.getValue("OPT_TERM"), conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						// 3.�������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", dispense_no);
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", requestD.getInt("SEQ_NO",
								i));
						inparm.setData("ORDER_CODE", requestD.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", batch_seq);
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						inparm.setData("QTY", requestD.getDouble("QTY", i)
								- qty_out);
						qty_out = actual_qty;
						inparm.setData("UNIT_CODE", requestD.getValue(
								"UNIT_CODE", i));
						inparm.setData("RETAIL_PRICE", requestD.getDouble(
								"RETAIL_PRICE", i));
						double stock_price = requestD.getDouble("STOCK_PRICE",
								i);
						inparm.setData("STOCK_PRICE", stock_price);
						inparm.setData("ACTUAL_QTY", qty_out);
						inparm.setData("PHA_TYPE", stock_parm.getValue(
								"PHA_TYPE", j));
						inparm.setData("OPT_USER", parm.getData("OPT_USER"));
						inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
						inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// ҩƷ����һ�οۿ�
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", i)
								* qty, 2);
						// 2.���¿��(���Ĳ����뵥����)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut("COS", org_code,
										order_code, batch_seq, qty, out_amt,
										parm.getValue("OPT_USER"),
										parm.getTimestamp("OPT_DATE"),
										parm.getValue("OPT_TERM"), conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						// 3.�������ⵥϸ��
						TParm inparm = new TParm();
						inparm.setData("DISPENSE_NO", dispense_no);
						inparm.setData("SEQ_NO", seq);
						inparm.setData("REQUEST_SEQ", requestD.getInt("SEQ_NO",
								i));
						inparm.setData("ORDER_CODE", requestD.getValue(
								"ORDER_CODE", i));
						inparm.setData("BATCH_SEQ", batch_seq);
						inparm.setData("BATCH_NO", stock_parm.getValue(
								"BATCH_NO", j));
						inparm.setData("VALID_DATE", stock_parm.getData(
								"VALID_DATE", j));
						qty_out = qty;
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", requestD.getValue(
								"UNIT_CODE", i));
						inparm.setData("RETAIL_PRICE", requestD.getDouble(
								"RETAIL_PRICE", i));
						double stock_price = requestD.getDouble("STOCK_PRICE",
								i);
						inparm.setData("STOCK_PRICE", stock_price);
						inparm.setData("ACTUAL_QTY", qty_out);
						inparm.setData("PHA_TYPE", stock_parm.getValue(
								"PHA_TYPE", j));
						inparm.setData("OPT_USER", parm.getData("OPT_USER"));
						inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
						inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
						inparm.setData("SUP_CODE", stock_parm.getValue(
								"SUP_CODE", j));
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// ���³�����
						actual_qty = actual_qty - qty;
						// ����ʵ�ʳ�������
						qty_out = qty_out + qty;
					}
				}
			}
			// 4.�������뵥״̬
			for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
				double qty = requestD.getDouble("QTY", i);
				TParm inparm = new TParm();
				inparm.setData("REQUEST_NO", request_no);
				inparm.setData("SEQ_NO", requestD.getInt("SEQ_NO", i));
				inparm.setData("ACTUAL_QTY", qty);
				inparm.setData("OPT_USER", parm.getData("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
				inparm.setData("UPDATE_FLG", "3");
				result = IndRequestDTool.getInstance().onUpdateActualQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// 5.�������Ĳ����뵥����
		result = IndRequestMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ���ұ�ҩ���ɲ�ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDeptExm(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		String type = parm.getValue("TYPE");
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		TParm resultMEM = new TParm();// �����ײ�����
		TParm resultMEMD = new TParm();// �����ײ�����
		if ("OPD".equals(type)) {
			// ����resultM
			if ("Y".equals(parm.getValue("REQUEST_FLG_A"))) {
				resultM = IndRequestMTool.getInstance().onQueryExm(parm);
				if (resultM.getErrCode() < 0) {
					return resultM;
				}

			} else {
				resultM = IndRequestMTool.getInstance()
						.onQueryOPDDeptExmM(parm);
				if (resultM.getErrCode() < 0) {
					return resultM;
				}
				resultMEM = IndRequestMTool.getInstance()
						.onQueryOPDDeptExmMMEM(parm);
				if (resultMEM.getErrCode() < 0) {
					return resultMEM;
				}
			}

			// ��ϸresultD
			resultD = IndRequestMTool.getInstance().onQueryOPDDeptExmD(parm);
			if (resultD.getErrCode() < 0) {
				return resultD;
			}
			resultMEMD = IndRequestMTool.getInstance().onQueryOPDDeptExmDMEM(
					parm);
			if (resultMEMD.getErrCode() < 0) {
				return resultMEMD;
			}
			for (int i = 0; i < resultMEM.getCount(); i++) {
				resultM.addRowData(resultMEM, i);
			}
			for (int i = 0; i < resultMEMD.getCount(); i++) {
				resultD.addRowData(resultMEMD, i);
			}
			result.addData("RESULT_M", resultM.getData());
			result.addData("RESULT_D", resultD.getData());
			/*
			 * result.addData("RESULT_MEM", resultMEM.getData());
			 * result.addData("RESULT_MEMD", resultMEMD.getData());
			 */
		} else if ("IBS".equals(type)) {
			// ����
			if ("Y".equals(parm.getValue("REQUEST_FLG_A"))) {
				resultM = IndRequestMTool.getInstance().onQueryExm(parm);
				if (resultM.getErrCode() < 0) {
					return resultM;
				}
			} else {
				resultM = IndRequestMTool.getInstance()
						.onQueryIBSDeptExmM(parm);
				// System.out.println("resultM---"+resultM);
				if (resultM.getErrCode() < 0) {
					return resultM;
				}
			}
			// luhai modify ��ϸ����ץȡ�ĳ�����ʱ�� begin
			if (!"N".equals(parm.getValue("REQUEST_FLG_B"))) {// ������
				// ��ϸ
				resultD = IndRequestMTool.getInstance()
						.onQueryIBSDeptExmDFinish(parm);
			} else {
				// ��ϸ
				resultD = IndRequestMTool.getInstance()
						.onQueryIBSDeptExmD(parm);
			}
			// //��ϸ
			// resultD = IndRequestMTool.getInstance().onQueryIBSDeptExmD(parm);
			// luhai modify ��ϸ����ץȡ�ĳ�����ʱ�� end
			// System.out.println("resultD---"+resultD);
			if (resultD.getErrCode() < 0) {
				return resultD;
			}
			result.addData("RESULT_M", resultM.getData());
			result.addData("RESULT_D", resultD.getData());
		}
		return result;
	}

	/**
	 * �������쵥
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onCreateDeptExmRequest(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();

		// �������뵥����
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("111111111111");

		// �������쵥ϸ��
		TParm requestD = parm.getParm("REQUEST_D");
		String order_code = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm
					.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE",
							0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// System.out.println("222222222222222");

		// �����ż�ס״̬
		String type = parm.getValue("TYPE");
		TParm update = parm.getParm("UPDATE");
		if ("I".equals(type)) {
			// ����סԺ״̬
			String caseNo = "";
			int caseNoSeq = 0;
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				caseNoSeq = update.getInt("CASE_NO_SEQ", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				result = IBSOrderdTool.getInstance().upForDeptMedic(caseNo,
						caseNoSeq, seqNo, requestFlg, requestNo, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
			// System.out.println("3333333333333333");
		} else if ("O".equals(type) || "E".equals(type)) {
			// �����ż���״̬
			String caseNo = "";
			String rxNo = "";
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			String sql = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				rxNo = update.getValue("RX_NO", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				// result = OpdOrder.updateRequest(caseNo,
				// rxNo, seqNo, requestFlg,
				// requestNo, conn);
				sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo
						+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
						+ caseNo + "' AND RX_NO='" + rxNo + "' AND SEQ_NO="
						+ seqNo;
				// System.out.println("sql---" + sql);
				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
			// System.out.println("4444444444444444");
		}
		return result;
	}

	/**
	 * ҩƷ����ս�������ҵ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onIndStockBatch(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORG_CODE_LIST"); i++) {
			parm.setData("ORG_CODE", parm.getValue("ORG_CODE_LIST", i));
			result = this.onIndStockBatchByOrgCode(parm, conn);
		}
		return result;
	}

	/**
	 * ָ������ҩƷ����ս�������ҵ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onIndStockBatchByOrgCode(TParm parm, TConnection conn) {
		// �����
		TParm result1 = new TParm();
		// ���ݼ��
		if (parm == null)
			return result1;
		TParm resutlParm = new TParm();
		// 1.���¸ù��˲��ŵ�IND_ORG.BATCH_FLGΪ"Y"
		result1 = IndOrgTool.getInstance().onUpdateBatchFlg(parm, conn);
		if (result1.getErrCode() < 0) {
			return result1;
		}
		// System.out.println("1----");
		// 2.ҩƷ����ս�������ҵ
		// 2.1��ѯ�տ�潻�׵�
		TParm result = IndStockDTool.getInstance().onQuery(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("2--result--"+result);
		// 2.2�ս�������ҵ
		String order_code = "";
		Timestamp date = parm.getTimestamp("TRANDATE");
		for (int i = 0; i < result.getCount("ORG_CODE"); i++) {
			// ��ǰ���
			double stock_qty = result.getDouble("STOCK_QTY", i);
			// ���տ��
			double last_stock_qty = result.getDouble("LAST_TOTSTOCK_QTY", i);
			// ������
			double in_qty = result.getDouble("IN_QTY", i);
			// ���ճ�
			double out_qty = result.getDouble("OUT_QTY", i);
			// �����̲�
			double modi_qty = result.getDouble("CHECKMODI_QTY", i);
			/*
			 * if (stock_qty == 0 && last_stock_qty == 0 && in_qty == 0 &&
			 * out_qty == 0 && modi_qty == 0) { continue; }
			 */
			// ������ʹ���� = ���ճ��� - �̲����
			double UseQty = out_qty - modi_qty;
			// �����տ���� = ���տ�� + ������� - ���ճ��� + �̲����
			double TotQty = stock_qty;// last_stock_qty + in_qty - out_qty +
			// modi_qty;;;;update by liyh
			// stock_qty��ʵʱֵ�����ü���
			if (TotQty != (last_stock_qty + in_qty - out_qty + modi_qty)) {// ������
				// д��־
				// add
				// liyh
				// by
				// liudy
				System.out
						.println("-------STOCK_QTY != (last_stock_qty + in_qty - out_qty + modi_qty):::"
								+ "stock:"
								+ TotQty
								+ ",last_stock_qty:"
								+ last_stock_qty
								+ ",in_qty:"
								+ in_qty
								+ ",out_qty:"
								+ out_qty
								+ ",modi_qty:"
								+ modi_qty);
			}
			// ������֧�����
			double UseAmt = UseQty * result.getDouble("RETAIL_PRICE", i);
			// �����տ����
			double TotAmt = TotQty * result.getDouble("RETAIL_PRICE", i);
			// �������տ����
			double TotLastStockAmt = last_stock_qty
					* result.getDouble("RETAIL_PRICE", i);

			// �Ƿ��е��ۼƻ�
			// order_code = result.getValue("ORDER_CODE", i);
			// double adjustAmt = 0;
			// TParm afterPrice = new TParm(TJDODBTool.getInstance().select(
			// INDSQL.getOrderSysFeeHistoryPrice(order_code,
			// date.toString().
			// substring(0,
			// 10).replaceAll("-", ""))));
			// if (afterPrice.getCount("ORDER_CODE") > 0) {
			// TParm beforePrice = new TParm(TJDODBTool.getInstance().
			// select(INDSQL.
			// getOrderSysFeeHistoryPrice(order_code,
			// StringTool.rollDate(date, -1).
			// toString().substring(0, 10).
			// replaceAll("-", ""))));
			// if (beforePrice.getCount("ORDER_CODE") > 0) {
			// adjustAmt = (afterPrice.getDouble("OWN_PRICE", 0) -
			// beforePrice.getDouble("OWN_PRICE", 0)) *
			// last_stock_qty;
			// TotLastStockAmt = last_stock_qty *
			// beforePrice.getDouble("OWN_PRICE", 0);
			// }
			// }
			// System.out.println("3----");

			// д��ҩƷ����ս���ҵ
			TParm inparm = new TParm();
			// 1-TRANDATE
			inparm.setData("TRANDATE", parm.getValue("TRANDATE").substring(0,
					10).replaceAll("-", ""));
			// 2-ORG_CODE
			inparm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			// 3-ORDER_CODE
			inparm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
			// 4-BATCH_SEQ
			inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
			// 5-BATCH_NO
			inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
			// 6-VALID_DATE
			inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
			// 7-REGION_CODE
			inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
			// 8-STOCK_QTY
			inparm.setData("STOCK_QTY", TotQty);
			// 9-STOCK_AMT
			inparm.setData("STOCK_AMT", TotAmt);
			// 10-LAST_TOTSTOCK_QTY
			inparm.setData("LAST_TOTSTOCK_QTY", last_stock_qty);
			// 11-LAST_TOTSTOCK_AMT
			inparm.setData("LAST_TOTSTOCK_AMT", TotLastStockAmt);
			// 12-IN_QTY
			inparm.setData("IN_QTY", result.getDouble("IN_QTY", i));
			// 13-IN_AMT
			inparm.setData("IN_AMT", result.getDouble("IN_AMT", i));
			// 14-OUT_QTY
			inparm.setData("OUT_QTY", result.getDouble("OUT_QTY", i));
			// 15-OUT_AMT
			inparm.setData("OUT_AMT", result.getDouble("OUT_AMT", i));
			// 16-CHECKMODI_QTY
			inparm.setData("CHECKMODI_QTY", result
					.getDouble("CHECKMODI_QTY", i));
			// 17-CHECKMODI_AMT
			inparm.setData("CHECKMODI_AMT", result
					.getDouble("CHECKMODI_AMT", i));
			// 18-VERIFYIN_QTY
			inparm.setData("VERIFYIN_QTY", result.getDouble("VERIFYIN_QTY", i));
			// 19-VERIFYIN_AMT
			inparm.setData("VERIFYIN_AMT", result.getDouble("VERIFYIN_AMT", i));
			// 20-FAVOR_QTY
			inparm.setData("FAVOR_QTY", result.getDouble("FAVOR_QTY", i));
			// 21-REGRESSGOODS_QTY
			inparm.setData("REGRESSGOODS_QTY", result.getDouble(
					"REGRESSGOODS_QTY", i));
			// 22-REGRESSGOODS_AMT
			inparm.setData("REGRESSGOODS_AMT", result.getDouble(
					"REGRESSGOODS_AMT", i));
			// 23-DOSAGE_QTY
			inparm.setData("DOSAGE_QTY", result.getDouble("DOSEAGE_QTY", i));
			// 24-DOSAGE_AMT
			inparm.setData("DOSAGE_AMT", result.getDouble("DOSAGE_AMT", i));
			// 25-REGRESSDRUG_QTY
			inparm.setData("REGRESSDRUG_QTY", result.getDouble(
					"REGRESSDRUG_QTY", i));
			// 26-REGRESSDRUG_AMT
			inparm.setData("REGRESSDRUG_AMT", result.getDouble(
					"REGRESSDRUG_AMT", i));
			// 27-PROFIT_LOSS_AMT
			inparm.setData("PROFIT_LOSS_AMT", // adjustAmt +
					result.getDouble("PROFIT_LOSS_AMT", i));
			// 28-VERIFYIN_PRICE
			inparm.setData("VERIFYIN_PRICE", result.getDouble("VERIFYIN_PRICE",
					i));
			// 29-STOCK_PRICE
			inparm.setData("STOCK_PRICE", result.getDouble("STOCK_PRICE", i));
			// 30-RETAIL_PRICE
			inparm.setData("RETAIL_PRICE", result.getDouble("RETAIL_PRICE", i));
			// 31-TRADE_PRICE
			inparm.setData("TRADE_PRICE", result.getDouble("TRADE_PRICE", i));
			// 32-STOCKIN_QTY
			inparm.setData("STOCKIN_QTY", result.getDouble("STOCKIN_QTY", i));
			// 33-STOCKIN_AMT
			inparm.setData("STOCKIN_AMT", result.getDouble("STOCKIN_AMT", i));
			// 34-STOCKOUT_QTY
			inparm.setData("STOCKOUT_QTY", result.getDouble("STOCKOUT_QTY", i));
			// 35-STOCKOUT_AMT
			inparm.setData("STOCKOUT_AMT", result.getDouble("STOCKOUT_AMT", i));
			// 36-OPT_USER
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			// 37-OPT_DATE
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			// 38-OPT_TERM
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			// 39-REQUEST_IN_QTY
			inparm.setData("REQUEST_IN_QTY", result.getDouble("REQUEST_IN_QTY",
					i));
			// 40-REQUEST_IN_AMT
			inparm.setData("REQUEST_IN_AMT", result.getDouble("REQUEST_IN_AMT",
					i));
			// 41-REQUEST_OUT_QTY
			inparm.setData("REQUEST_OUT_QTY", result.getDouble(
					"REQUEST_OUT_QTY", i));
			// 42-REQUEST_OUT_AMT
			inparm.setData("REQUEST_OUT_AMT", result.getDouble(
					"REQUEST_OUT_AMT", i));
			// 43-GIF_IN_QTY
			inparm.setData("GIF_IN_QTY", result.getDouble("GIF_IN_QTY", i));
			// 44-GIF_IN_AMT
			inparm.setData("GIF_IN_AMT", result.getDouble("GIF_IN_AMT", i));
			// 45-GIF_OUT_QTY
			inparm.setData("GIF_OUT_QTY", result.getDouble("GIF_OUT_QTY", i));
			// 46-GIF_OUT_AMT
			inparm.setData("GIF_OUT_AMT", result.getDouble("GIF_OUT_AMT", i));
			// 47-RET_IN_QTY
			inparm.setData("RET_IN_QTY", result.getDouble("RET_IN_QTY", i));
			// 48-RET_IN_AMT
			inparm.setData("RET_IN_AMT", result.getDouble("RET_IN_AMT", i));
			// 49-RET_OUT_QTY
			inparm.setData("RET_OUT_QTY", result.getDouble("RET_OUT_QTY", i));
			// 50-RET_OUT_AMT
			inparm.setData("RET_OUT_AMT", result.getDouble("RET_OUT_AMT", i));
			// 51-WAS_OUT_QTY
			inparm.setData("WAS_OUT_QTY", result.getDouble("WAS_OUT_QTY", i));
			// 52-WAS_OUT_AMT
			inparm.setData("WAS_OUT_AMT", result.getDouble("WAS_OUT_AMT", i));
			// 53-THO_OUT_QTY
			inparm.setData("THO_OUT_QTY", result.getDouble("THO_OUT_QTY", i));
			// 54-THO_OUT_AMT
			inparm.setData("THO_OUT_AMT", result.getDouble("THO_OUT_AMT", i));
			// 55-THI_IN_QTY
			inparm.setData("THI_IN_QTY", result.getDouble("THI_IN_QTY", i));
			// 56-THI_IN_AMT
			inparm.setData("THI_IN_AMT", result.getDouble("THI_IN_AMT", i));
			// 57-COS_OUT_QTY
			inparm.setData("COS_OUT_QTY", result.getDouble("COS_OUT_QTY", i));
			// 58-COS_OUT_AMT
			inparm.setData("COS_OUT_AMT", result.getDouble("COS_OUT_AMT", i));
			// 59--SUP_CODE
			inparm.setData("SUP_CODE", result.getDouble("SUP_CODE", i));
			inparm.setData("INVENT_PRICE", result.getDouble("INVENT_PRICE", i));

			// 60 SUP_ORDER_CODE
			inparm.setData("SUP_ORDER_CODE", result.getValue("SUP_ORDER_CODE",
					i));
			// System.out.println("inparm---" + inparm);
			// ɾ������ddstock �Ĺ��ܣ���rebatch ���β��� begin
			resutlParm = IndDDStockTool.getInstance().onInsert(inparm, conn);
			if (resutlParm.getErrCode() < 0) {
				return resutlParm;
			}
			/**
			 * ����ind_ddstock��ʷ��¼ resutlParm =
			 * IndDDStockTool.getInstance().onInsertStockHistory(inparm, conn);
			 * if (resutlParm.getErrCode() < 0) { return resutlParm; }
			 */
			// ɾ������ddstock �Ĺ��ܣ���rebatch ���β��� end
			// System.out.println("4----");

			// ��������¼����
			TParm srockParm = new TParm();
			srockParm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			srockParm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
			srockParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
			srockParm.setData("STOCK_QTY", TotQty);
			srockParm.setData("LAST_TOTSTOCK_QTY", TotQty);
			srockParm.setData("LAST_TOTSTOCK_AMT", TotAmt);
			srockParm.setData("OPT_USER", parm.getData("OPT_USER"));
			srockParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			srockParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

			resutlParm = IndStockDTool.getInstance().onUpdateOutQtyToZero(
					srockParm, conn);
			conn.commit();
			if (resutlParm.getErrCode() < 0) {
				return resutlParm;
			}
			// System.out.println("5----");
		}
		// 3.���¸ù��˲��ŵ�IND_ORG.BATCH_FLGΪ"N"
		parm.setData("BATCH_FLG", "N");
		result = IndOrgTool.getInstance().onUpdateBatchFlg(parm, conn);
		conn.commit();
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("6----");
		return result;
	}

	public TParm onSaveIndStockHistory(String trandate, String orgCode) {
		TParm result = new TParm();
		// fux modify 2014
		// WHERE A.ORG_CODE='" + orgCode + "'
		String sqlString = "INSERT INTO IND_STOCK_HISTORY SELECT '" + trandate
				+ "',A.* FROM IND_STOCK A ";
		// String sqlString =
		// "INSERT INTO IND_STOCK_HISTORY(TRANDATE, ORG_CODE, ORDER_CODE, BATCH_SEQ, BATCH_NO, "
		// +
		// "	                          VALID_DATE, REGION_CODE, STOCK_QTY, LAST_TOTSTOCK_QTY, LAST_TOTSTOCK_AMT, "
		// +
		// "	                          IN_QTY, IN_AMT, OUT_QTY, OUT_AMT, CHECKMODI_QTY,  "
		// +
		// "	                          CHECKMODI_AMT, VERIFYIN_QTY, VERIFYIN_AMT, FAVOR_QTY, REGRESSGOODS_QTY,  "
		// +
		// "	                          REGRESSGOODS_AMT, DOSEAGE_QTY, DOSAGE_AMT, REGRESSDRUG_QTY, REGRESSDRUG_AMT,  "
		// +
		// "	                          PROFIT_LOSS_AMT, VERIFYIN_PRICE, RETAIL_PRICE, STOCKIN_QTY, STOCKIN_AMT,  "
		// +
		// "	                          STOCKOUT_QTY, STOCKOUT_AMT, OPT_USER, OPT_DATE, OPT_TERM,  "
		// +
		// "	                          REQUEST_IN_QTY, REQUEST_IN_AMT, REQUEST_OUT_QTY, REQUEST_OUT_AMT, GIF_IN_QTY,  "
		// +
		// "	                          GIF_IN_AMT, GIF_OUT_QTY, GIF_OUT_AMT, RET_IN_QTY, RET_IN_AMT,  "
		// +
		// "	                          RET_OUT_QTY, RET_OUT_AMT, WAS_OUT_QTY, WAS_OUT_AMT, THO_OUT_QTY,  "
		// +
		// "	                          THO_OUT_AMT, THI_IN_QTY, THI_IN_AMT, COS_OUT_QTY, COS_OUT_AMT,  "
		// + "	                          IS_UPDATE ) SELECT '" +trandate + "', "
		// +
		// "                              A.ORG_CODE, A.ORDER_CODE, A.BATCH_SEQ, A.BATCH_NO, "
		// +
		// "	                          A.VALID_DATE, A.REGION_CODE, A.STOCK_QTY, A.LAST_TOTSTOCK_QTY, A.LAST_TOTSTOCK_AMT, "
		// +
		// "	                          A.IN_QTY, A.IN_AMT, A.OUT_QTY, A.OUT_AMT, A.CHECKMODI_QTY,  "
		// +
		// "	                          A.CHECKMODI_AMT, A.VERIFYIN_QTY, A.VERIFYIN_AMT, A.FAVOR_QTY, A.REGRESSGOODS_QTY,  "
		// +
		// "	                          A.REGRESSGOODS_AMT, A.DOSEAGE_QTY, A.DOSAGE_AMT, A.REGRESSDRUG_QTY, A.REGRESSDRUG_AMT,  "
		// +
		// "	                          A.PROFIT_LOSS_AMT, A.VERIFYIN_PRICE, A.RETAIL_PRICE, A.STOCKIN_QTY, A.STOCKIN_AMT,  "
		// +
		// "	                          A.STOCKOUT_QTY, A.STOCKOUT_AMT, A.OPT_USER, A.OPT_DATE, A.OPT_TERM,  "
		// +
		// "	                          A.REQUEST_IN_QTY, A.REQUEST_IN_AMT, A.REQUEST_OUT_QTY, A.REQUEST_OUT_AMT, A.GIF_IN_QTY,  "
		// +
		// "	                          A.GIF_IN_AMT, A.GIF_OUT_QTY, A.GIF_OUT_AMT, A.RET_IN_QTY, A.RET_IN_AMT,  "
		// +
		// "	                          A.RET_OUT_QTY, A.RET_OUT_AMT, A.WAS_OUT_QTY, A.WAS_OUT_AMT, A.THO_OUT_QTY,  "
		// +
		// "	                          A.THO_OUT_AMT, A.THI_IN_QTY, A.THI_IN_AMT, A.COS_OUT_QTY, A.COS_OUT_AMT,  "
		// +
		// "	                          A.IS_UPDATE FROM IND_STOCK A WHERE A.ORG_CODE='"
		// + orgCode + "'";
		System.out.println("---------sql:" + sqlString);
		result = new TParm(TJDODBTool.getInstance().update(sqlString));
		return result;
	}

	/**
	 * auto update ind_stockm is lock_stock_qty
	 * 
	 * @param conn
	 * @return
	 */
	public TParm onUpdateIndStockMLockQty(TConnection conn) {
		TParm result = new TParm();
		String sql = " UPDATE IND_STOCKM A SET A.LOCK_QTY = 0 ";
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}

	/**
	 * ���½��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDay(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// 1.ҩ���ս��в�ѯҩ���ɷ�ҩ��ҩƷ����
		String org_code = parm.getValue("ORG_CODE");
		// ҩƷ����
		TParm type_parm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndDDStockOrderTypeCode(org_code)));
		if (type_parm.getErrCode() < 0) {
			return type_parm;
		}
		// System.out.println("type_parm==" + type_parm);
		// 2.ÿ��ҩƷ��ͳ�Ʋ�ѯ
		for (int i = 0; i < type_parm.getCount("TYPE_CODE"); i++) {
			result.addData("TYPE_CODE", type_parm.getValue("TYPE_CODE", i));
			result.addData("TYPE_DESC", type_parm.getValue("CHN_DESC", i));
			// ����ҩƷ�����ѯҩƷ����
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndDDStockOrderTypeCode(org_code, type_parm
							.getValue("TYPE_CODE", i))));
			// System.out.println("orderParm==" + orderParm);
			if (orderParm.getErrCode() < 0) {
				return orderParm;
			}
			// ���ڽ��׵��ɱ����
			double stock_price = 0;
			// ���ڽ��ɱ����
			double last_stock_amt = 0;
			// ���ڽ�����۽��
			double last_own_amt = 0;
			// ���ڽ��ɱ����
			double stock_amt = 0;
			// ���ڽ�����۽��
			double own_amt = 0;
			for (int j = 0; j < orderParm.getCount("ORDER_CODE"); j++) {
				// ���ڽ��׵��ɳɱ��۲�ѯ(��ѯͳ���������һ�������ҩƷ��ҩƷ�ɱ��۶���)
				TParm stock_priceParm = new TParm();
				TParm stock_priceInParm = new TParm();
				stock_priceInParm.setData("START_DATE", parm
						.getValue("START_DATE"));
				stock_priceInParm
						.setData("END_DATE", parm.getValue("END_DATE"));
				stock_priceInParm
						.setData("ORG_CODE", parm.getValue("ORG_CODE"));
				stock_priceInParm.setData("ORDER_CODE", orderParm.getValue(
						"ORDER_CODE", j));
				stock_priceParm = IndDDStockTool.getInstance().onQuery(
						stock_priceInParm);
				// zhangyong20101027 begin
				if (stock_priceParm == null
						|| stock_priceParm.getCount("TRANDATE") <= 0) {
					continue;
				}
				// parm.setData("START_DATE", parm.getValue("END_DATE"));
				// parm.setData("END_DATE", stock_priceParm.getValue("TRANDATE",
				// 0));
				// zhangyong20101027 end
				if (stock_priceParm.getErrCode() < 0) {
					return stock_priceParm;
				}
				if (stock_priceParm.getCount("STOCK_PRICE") <= 0) {
					continue;
				}
				stock_price = stock_priceParm.getDouble("STOCK_PRICE", 0);

				TParm resultParm = new TParm();
				TParm resultInParm = new TParm();
				resultInParm.setData("STOCK_PRICE", stock_price);
				resultInParm.setData("START_DATE", parm.getValue("START_DATE"));
				resultInParm.setData("END_DATE", parm.getValue("END_DATE"));
				resultInParm.setData("ORG_CODE", parm.getValue("ORG_CODE"));
				resultInParm.setData("ORDER_CODE", orderParm.getValue(
						"ORDER_CODE", j));
				// ////////////////////////////////////////////
				// if (j == 0)
				// System.out.println("resultInParm---" + resultInParm);
				// ////////////////////////////////////////////
				TParm stock_ownInParm = new TParm();
				stock_ownInParm
						.setData("START_DATE", parm.getValue("END_DATE"));
				stock_ownInParm.setData("END_DATE", parm.getValue("END_DATE"));
				stock_ownInParm.setData("ORG_CODE", parm.getValue("ORG_CODE"));
				stock_ownInParm.setData("ORDER_CODE", orderParm.getValue(
						"ORDER_CODE", j));

				if ("A".equals(parm.getValue("ORG_TYPE"))) {

					TParm last_stock_amtParm = new TParm(TJDODBTool
							.getInstance().select(
									INDSQL.getIndDDStockLastStockAMTYK(parm
											.getValue("START_DATE"), parm
											.getValue("ORG_CODE"), orderParm
											.getValue("ORDER_CODE", j),
											stock_price)));

					if (last_stock_amtParm.getErrCode() < 0) {
						return last_stock_amtParm;
					}
					// ���ڽ��ɱ����
					last_stock_amt = last_stock_amtParm.getDouble(
							"LAST_STOCK_AMT", 0);

					// ҩ�⽻�׵��ɱ�
					resultParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayA(resultInParm);
					if (resultParm.getErrCode() < 0) {
						return resultParm;
					}
					if (result == null
							|| result.getCount("VERIFYIN_STOCK_AMT") == 0
							|| result.getCount("VERIFYIN_STOCK_AMT") < i + 1) {
						result.addData("VERIFYIN_STOCK_AMT", resultParm
								.getDouble("VERIFYIN_AMT", 0));
						result.addData("REGRESSGOODS_STOCK_AMT", resultParm
								.getDouble("REGRESSGOODS_AMT", 0));
						result.addData("REQUEST_OUT_STOCK_AMT", resultParm
								.getDouble("REQUEST_OUT_AMT", 0));
						result.addData("RET_IN_STOCK_AMT", resultParm
								.getDouble("RET_IN_AMT", 0));
						result.addData("THO_OUT_STOCK_AMT", resultParm
								.getDouble("THO_OUT_AMT", 0));
						result.addData("THI_IN_STOCK_AMT", resultParm
								.getDouble("THI_IN_AMT", 0));
						result.addData("WAS_OUT_STOCK_AMT", resultParm
								.getDouble("WAS_OUT_AMT", 0));
						result.addData("COS_OUT_STOCK_AMT", resultParm
								.getDouble("COS_OUT_AMT", 0));
						result.addData("CHECKMODI_STOCK_AMT", resultParm
								.getDouble("CHECKMODI_AMT", 0));
						result.addData("LAST_STOCK_AMT", last_stock_amt);
					} else {
						result.setData("VERIFYIN_STOCK_AMT", i, result
								.getDouble("VERIFYIN_STOCK_AMT", i)
								+ resultParm.getDouble("VERIFYIN_AMT", 0));
						result.setData("REGRESSGOODS_STOCK_AMT", i, result
								.getDouble("REGRESSGOODS_STOCK_AMT", i)
								+ resultParm.getDouble("REGRESSGOODS_AMT", 0));
						result.setData("REQUEST_OUT_STOCK_AMT", i, result
								.getDouble("REQUEST_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("REQUEST_OUT_AMT", 0));
						result.setData("RET_IN_STOCK_AMT", i, result.getDouble(
								"RET_IN_STOCK_AMT", i)
								+ resultParm.getDouble("RET_IN_AMT", 0));
						result.setData("THO_OUT_STOCK_AMT", i, result
								.getDouble("THO_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("THO_OUT_AMT", 0));
						result.setData("THI_IN_STOCK_AMT", i, result.getDouble(
								"THI_IN_STOCK_AMT", i)
								+ resultParm.getDouble("THI_IN_AMT", 0));
						result.setData("WAS_OUT_STOCK_AMT", i, result
								.getDouble("WAS_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("WAS_OUT_AMT", 0));
						result.setData("COS_OUT_STOCK_AMT", i, result
								.getDouble("COS_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("COS_OUT_AMT", 0));
						result.setData("CHECKMODI_STOCK_AMT", i, result
								.getDouble("CHECKMODI_STOCK_AMT", i)
								+ resultParm.getDouble("CHECKMODI_AMT", 0));
						result.setData("LAST_STOCK_AMT", i, result.getDouble(
								"LAST_STOCK_AMT", i)
								+ last_stock_amt);
					}
					resultParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayStockOwn(stock_ownInParm);
				} else {

					TParm last_stock_amtParm = new TParm(TJDODBTool
							.getInstance().select(
									INDSQL.getIndDDStockLastStockAMT(parm
											.getValue("START_DATE"), parm
											.getValue("ORG_CODE"), orderParm
											.getValue("ORDER_CODE", j),
											stock_price)));

					if (last_stock_amtParm.getErrCode() < 0) {
						return last_stock_amtParm;
					}
					// ���ڽ��ɱ����
					last_stock_amt = last_stock_amtParm.getDouble(
							"LAST_STOCK_AMT", 0);

					// ҩ�����׵��ɱ�
					resultParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayB(resultInParm);
					if (resultParm.getErrCode() < 0) {
						return resultParm;
					}
					if (result == null
							|| result.getCount("REQUEST_IN_STOCK_AMT") == 0
							|| result.getCount("REQUEST_IN_STOCK_AMT") < i + 1) {
						result.addData("REQUEST_IN_STOCK_AMT", resultParm
								.getDouble("REQUEST_IN_AMT", 0));
						result.addData("RET_OUT_STOCK_AMT", resultParm
								.getDouble("RET_OUT_AMT", 0));
						result.addData("RET_IN_STOCK_AMT", resultParm
								.getDouble("RET_IN_AMT", 0));
						result.addData("REQUEST_OUT_STOCK_AMT", resultParm
								.getDouble("REQUEST_OUT_AMT", 0));
						result.addData("GIF_IN_STOCK_AMT", resultParm
								.getDouble("GIF_IN_AMT", 0));
						result.addData("GIF_OUT_STOCK_AMT", resultParm
								.getDouble("GIF_OUT_AMT", 0));
						result.addData("REGRESSDRUG_STOCK_AMT", resultParm
								.getDouble("REGRESSDRUG_AMT", 0));
						result.addData("DOSAGE_STOCK_AMT", resultParm
								.getDouble("DOSAGE_AMT", 0));
						result.addData("THO_OUT_STOCK_AMT", resultParm
								.getDouble("THO_OUT_AMT", 0));
						result.addData("THI_IN_STOCK_AMT", resultParm
								.getDouble("THI_IN_AMT", 0));
						result.addData("WAS_OUT_STOCK_AMT", resultParm
								.getDouble("WAS_OUT_AMT", 0));
						result.addData("COS_OUT_STOCK_AMT", resultParm
								.getDouble("COS_OUT_AMT", 0));
						result.addData("CHECKMODI_STOCK_AMT", resultParm
								.getDouble("CHECKMODI_AMT", 0));
						// update by fuwj
						result.addData("OUT_STOCK_AMT", resultParm.getDouble(
								"OUT_STOCK_AMT", 0));
						result.addData("IN_STOCK_AMT", resultParm.getDouble(
								"IN_STOCK_AMT", 0));
						// fux modify 20141209
						result.addData("OUT_AMT", resultParm.getDouble(
								"OUT_AMT", 0));
						result.addData("IN_AMT", resultParm.getDouble("IN_AMT",
								0));
						result.addData("LAST_STOCK_AMT", last_stock_amt);
					} else {
						result.setData("REQUEST_IN_STOCK_AMT", i, result
								.getDouble("REQUEST_IN_STOCK_AMT", i)
								+ resultParm.getDouble("REQUEST_IN_AMT", 0));
						result.setData("RET_OUT_STOCK_AMT", i, result
								.getDouble("RET_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("RET_OUT_AMT", 0));
						result.setData("RET_IN_STOCK_AMT", i, result.getDouble(
								"RET_IN_STOCK_AMT", i)
								+ resultParm.getDouble("RET_IN_AMT", 0));
						result.setData("REQUEST_OUT_STOCK_AMT", i, result
								.getDouble("REQUEST_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("REQUEST_OUT_AMT", 0));
						result.setData("GIF_IN_STOCK_AMT", i, result.getDouble(
								"GIF_IN_STOCK_AMT", i)
								+ resultParm.getDouble("GIF_IN_AMT", 0));
						result.setData("GIF_OUT_STOCK_AMT", i, result
								.getDouble("GIF_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("GIF_OUT_AMT", 0));
						result.setData("REGRESSDRUG_STOCK_AMT", i, result
								.getDouble("REGRESSDRUG_STOCK_AMT", i)
								+ resultParm.getDouble("REGRESSDRUG_AMT", 0));
						result.setData("DOSAGE_STOCK_AMT", i, result.getDouble(
								"DOSAGE_STOCK_AMT", i)
								+ resultParm.getDouble("DOSAGE_AMT", 0));
						result.setData("THO_OUT_STOCK_AMT", i, result
								.getDouble("THO_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("THO_OUT_AMT", 0));
						result.setData("THI_IN_STOCK_AMT", i, result.getDouble(
								"THI_IN_STOCK_AMT", i)
								+ resultParm.getDouble("THI_IN_AMT", 0));
						result.setData("WAS_OUT_STOCK_AMT", i, result
								.getDouble("WAS_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("WAS_OUT_AMT", 0));
						result.setData("COS_OUT_STOCK_AMT", i, result
								.getDouble("COS_OUT_STOCK_AMT", i)
								+ resultParm.getDouble("COS_OUT_AMT", 0));
						result.setData("CHECKMODI_STOCK_AMT", i, result
								.getDouble("CHECKMODI_STOCK_AMT", i)
								+ resultParm.getDouble("CHECKMODI_AMT", 0));
						// update by fuwj
						result.setData("OUT_STOCK_AMT", i, result.getDouble(
								"OUT_STOCK_AMT", i)
								+ resultParm.getDouble("OUT_STOCK_AMT", 0));
						result.setData("IN_STOCK_AMT", i, result.getDouble(
								"IN_STOCK_AMT", i)
								+ resultParm.getDouble("IN_STOCK_AMT", 0));
						// fux modify 20141209
						result.addData("OUT_AMT", resultParm.getDouble(
								"OUT_AMT", 0));
						result.addData("IN_AMT", resultParm.getDouble("IN_AMT",
								0));
						result.setData("LAST_STOCK_AMT", i, result.getDouble(
								"LAST_STOCK_AMT", i)
								+ last_stock_amt);
					}
					resultParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayStockOwn2(stock_ownInParm);
				}
				// ���ڽ�����۽��
				TParm last_own_amtParm = new TParm(TJDODBTool.getInstance()
						.select(
								INDSQL.getIndDDStockLastOwnAMT(parm
										.getValue("START_DATE"), parm
										.getValue("ORG_CODE"), orderParm
										.getValue("ORDER_CODE", j))));
				// ////////////////////////////////////////////
				// if (j == 0)
				// System.out.println("-----" +
				// INDSQL.getIndDDStockLastOwnAMT(parm.
				// getValue("LAST_TRANDATE"), parm.getValue("ORG_CODE"),
				// orderParm.getValue("ORDER_CODE", j)));
				// /////////////////////////////////////////////
				if (last_own_amtParm.getErrCode() < 0) {
					return last_own_amtParm;
				}
				last_own_amt = last_own_amtParm.getDouble("LAST_OWN_AMT", 0);

				// ���ڽ��ɱ��������۽��
				TParm stock_ownParm = new TParm();

				if (resultParm.getErrCode() < 0) {
					return resultParm;
				}
				stock_amt = resultParm.getDouble("STOCK_AMT", 0);
				own_amt = resultParm.getDouble("OWN_AMT", 0);

				stock_ownInParm.setData("START_DATE", parm
						.getValue("START_DATE"));
				if ("A".equals(parm.getValue("ORG_TYPE"))) {
					// ҩ�Ȿ�ڽ��׵����۽��
					TParm ownParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayC(stock_ownInParm);
					if (ownParm.getErrCode() < 0) {
						return ownParm;
					}
					if (result == null
							|| result.getCount("VERIFYIN_OWN_AMT") == 0
							|| result.getCount("VERIFYIN_OWN_AMT") < i + 1) {
						result.addData("VERIFYIN_OWN_AMT", ownParm.getDouble(
								"VERIFYIN_AMT", 0));
						result.addData("REGRESSGOODS_OWN_AMT", ownParm
								.getDouble("REGRESSGOODS_AMT", 0));
						result.addData("REQUEST_OUT_OWN_AMT", ownParm
								.getDouble("REQUEST_OUT_AMT", 0));
						result.addData("RET_IN_OWN_AMT", ownParm.getDouble(
								"RET_IN_AMT", 0));
						result.addData("THO_OUT_OWN_AMT", ownParm.getDouble(
								"THO_OUT_AMT", 0));
						result.addData("THI_IN_OWN_AMT", ownParm.getDouble(
								"THI_IN_AMT", 0));
						result.addData("WAS_OUT_OWN_AMT", ownParm.getDouble(
								"WAS_OUT_AMT", 0));
						result.addData("COS_OUT_OWN_AMT", ownParm.getDouble(
								"COS_OUT_AMT", 0));
						result.addData("CHECKMODI_OWN_AMT", resultParm
								.getDouble("CHECKMODI_AMT", 0));
						result.addData("PROFIT_LOSS_AMT", resultParm.getDouble(
								"PROFIT_LOSS_AMT", 0));
						result.addData("LAST_OWN_AMT", last_own_amt);
						result.addData("STOCK_AMT", stock_amt);
						result.addData("OWN_AMT", own_amt);
					} else {
						result.setData("VERIFYIN_OWN_AMT", i, result.getDouble(
								"VERIFYIN_OWN_AMT", i)
								+ ownParm.getDouble("VERIFYIN_AMT", 0));
						result.setData("REGRESSGOODS_OWN_AMT", i, result
								.getDouble("REGRESSGOODS_OWN_AMT", i)
								+ ownParm.getDouble("REGRESSGOODS_AMT", 0));
						result.setData("REQUEST_OUT_OWN_AMT", i, result
								.getDouble("REQUEST_OUT_OWN_AMT", i)
								+ ownParm.getDouble("REQUEST_OUT_AMT", 0));
						result.setData("RET_IN_OWN_AMT", i, result.getDouble(
								"RET_IN_OWN_AMT", i)
								+ ownParm.getDouble("RET_IN_AMT", 0));
						result.setData("THO_OUT_OWN_AMT", i, result.getDouble(
								"THO_OUT_OWN_AMT", i)
								+ ownParm.getDouble("THO_OUT_AMT", 0));
						result.setData("THI_IN_OWN_AMT", i, result.getDouble(
								"THI_IN_OWN_AMT", i)
								+ ownParm.getDouble("THI_IN_AMT", 0));
						result.setData("WAS_OUT_OWN_AMT", i, result.getDouble(
								"WAS_OUT_OWN_AMT", i)
								+ ownParm.getDouble("WAS_OUT_AMT", 0));
						result.setData("COS_OUT_OWN_AMT", i, result.getDouble(
								"COS_OUT_OWN_AMT", i)
								+ ownParm.getDouble("COS_OUT_AMT", 0));
						result.setData("CHECKMODI_OWN_AMT", i, result
								.getDouble("CHECKMODI_OWN_AMT", i)
								+ ownParm.getDouble("CHECKMODI_AMT", 0));
						result.setData("PROFIT_LOSS_AMT", i, result.getDouble(
								"PROFIT_LOSS_AMT", i)
								+ ownParm.getDouble("PROFIT_LOSS_AMT", 0));
						result.setData("LAST_OWN_AMT", i, result.getDouble(
								"LAST_OWN_AMT", i)
								+ last_own_amt);
						result.setData("STOCK_AMT", i, result.getDouble(
								"STOCK_AMT", i)
								+ stock_amt);
						result.setData("OWN_AMT", i, result.getDouble(
								"OWN_AMT", i)
								+ own_amt);
					}
				} else {
					// ҩ�����ڽ��׵����۽��
					TParm ownParm = IndDDStockTool.getInstance()
							.onQueryDDStockDayD(stock_ownInParm);
					if (ownParm.getErrCode() < 0) {
						return ownParm;
					}
					if (result == null
							|| result.getCount("REQUEST_IN_OWN_AMT") == 0
							|| result.getCount("REQUEST_IN_OWN_AMT") < i + 1) {
						result.addData("REQUEST_IN_OWN_AMT", ownParm.getDouble(
								"REQUEST_IN_AMT", 0));
						result.addData("RET_OUT_OWN_AMT", ownParm.getDouble(
								"RET_OUT_AMT", 0));
						result.addData("RET_IN_OWN_AMT", ownParm.getDouble(
								"RET_IN_AMT", 0));
						result.addData("REQUEST_OUT_OWN_AMT", ownParm
								.getDouble("REQUEST_OUT_AMT", 0));
						result.addData("GIF_IN_OWN_AMT", ownParm.getDouble(
								"GIF_IN_AMT", 0));
						result.addData("GIF_OUT_OWN_AMT", ownParm.getDouble(
								"GIF_OUT_AMT", 0));
						result.addData("REGRESSDRUG_OWN_AMT", ownParm
								.getDouble("REGRESSDRUG_AMT", 0));
						result.addData("DOSAGE_OWN_AMT", ownParm.getDouble(
								"DOSAGE_AMT", 0));
						result.addData("THO_OUT_OWN_AMT", ownParm.getDouble(
								"THO_OUT_AMT", 0));
						result.addData("THI_IN_OWN_AMT", ownParm.getDouble(
								"THI_IN_AMT", 0));
						result.addData("WAS_OUT_OWN_AMT", ownParm.getDouble(
								"WAS_OUT_AMT", 0));
						result.addData("COS_OUT_OWN_AMT", ownParm.getDouble(
								"COS_OUT_AMT", 0));
						result.addData("CHECKMODI_OWN_AMT", ownParm.getDouble(
								"CHECKMODI_AMT", 0));
						result.addData("PROFIT_LOSS_AMT", ownParm.getDouble(
								"PROFIT_LOSS_AMT", 0));
						// update by fuwj
						result.addData("OUT_OWN_AMT", ownParm.getDouble(
								"OUT_OWN_AMT", 0));
						result.addData("IN_OWN_AMT", ownParm.getDouble(
								"IN_OWN_AMT", 0));

						result.addData("LAST_OWN_AMT", last_own_amt);
						result.addData("STOCK_AMT", stock_amt);
						result.addData("OWN_AMT", own_amt);
					} else {
						result.setData("REQUEST_IN_OWN_AMT", i, result
								.getDouble("REQUEST_IN_OWN_AMT", i)
								+ ownParm.getDouble("REQUEST_IN_AMT", 0));
						result.setData("RET_OUT_OWN_AMT", i, result.getDouble(
								"RET_OUT_OWN_AMT", i)
								+ ownParm.getDouble("RET_OUT_AMT", 0));
						result.setData("RET_IN_OWN_AMT", i, result.getDouble(
								"RET_IN_OWN_AMT", i)
								+ ownParm.getDouble("RET_IN_AMT", 0));
						result.setData("REQUEST_OUT_OWN_AMT", i, result
								.getDouble("REQUEST_OUT_OWN_AMT", i)
								+ ownParm.getDouble("REQUEST_OUT_AMT", 0));
						result.setData("GIF_IN_OWN_AMT", i, result.getDouble(
								"GIF_IN_OWN_AMT", i)
								+ ownParm.getDouble("GIF_IN_AMT", 0));
						result.setData("GIF_OUT_OWN_AMT", i, result.getDouble(
								"GIF_OUT_OWN_AMT", i)
								+ ownParm.getDouble("GIF_OUT_AMT", 0));
						result.setData("REGRESSDRUG_OWN_AMT", i, result
								.getDouble("REGRESSDRUG_OWN_AMT", i)
								+ ownParm.getDouble("REGRESSDRUG_AMT", 0));
						result.setData("DOSAGE_OWN_AMT", i, result.getDouble(
								"DOSAGE_OWN_AMT", i)
								+ ownParm.getDouble("DOSAGE_AMT", 0));
						result.setData("THO_OUT_OWN_AMT", i, result.getDouble(
								"THO_OUT_OWN_AMT", i)
								+ ownParm.getDouble("THO_OUT_AMT", 0));
						result.setData("THI_IN_OWN_AMT", i, result.getDouble(
								"THI_IN_OWN_AMT", i)
								+ ownParm.getDouble("THI_IN_AMT", 0));
						result.setData("WAS_OUT_OWN_AMT", i, result.getDouble(
								"WAS_OUT_OWN_AMT", i)
								+ ownParm.getDouble("WAS_OUT_AMT", 0));
						result.setData("COS_OUT_OWN_AMT", i, result.getDouble(
								"COS_OUT_OWN_AMT", i)
								+ ownParm.getDouble("COS_OUT_AMT", 0));
						result.setData("CHECKMODI_OWN_AMT", i, result
								.getDouble("CHECKMODI_OWN_AMT", i)
								+ ownParm.getDouble("CHECKMODI_AMT", 0));
						result.setData("PROFIT_LOSS_AMT", i, result.getDouble(
								"PROFIT_LOSS_AMT", i)
								+ ownParm.getDouble("PROFIT_LOSS_AMT", 0));
						// update by fuwj
						result.setData("OUT_OWN_AMT", i, result.getDouble(
								"OUT_OWN_AMT", i)
								+ ownParm.getDouble("OUT_OWN_AMT", 0));
						result.setData("IN_OWN_AMT", i, result.getDouble(
								"IN_OWN_AMT", i)
								+ ownParm.getDouble("IN_OWN_AMT", 0));

						result.setData("LAST_OWN_AMT", i, result.getDouble(
								"LAST_OWN_AMT", i)
								+ last_own_amt);
						result.setData("STOCK_AMT", i, result.getDouble(
								"STOCK_AMT", i)
								+ stock_amt);
						result.setData("OWN_AMT", i, result.getDouble(
								"OWN_AMT", i)
								+ own_amt);
					}
				}
			}
		}
		// System.out.println("result===" + result);
		return result;
	}

	/**
	 * ȡ��������ҵ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateDipenseCancel(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// �������뵥�ۼ��������״̬
		TParm parm_D = parm.getParm("PARM_D");
		for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
			TParm inParm = new TParm();
			inParm.setData("REQUEST_NO", parm.getValue("REQUEST_NO"));
			// inParm.setData("SEQ_NO", parm_D.getInt("REQUEST_SEQ", i)); //by
			// liyanhui on 20120625 ȡ������ ���������뵥����
			inParm.setData("ACTUAL_QTY", "0");
			inParm.setData("UPDATE_FLG", "0");// by liyanhui on
			// 20120618,ȡ�����󻹿����ٴγ���-�޸������״̬
			inParm.setData("OPT_USER", parm.getData("OPT_USER"));
			inParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			inParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = IndRequestDTool.getInstance().onUpdateActualQtyCancel(
					inParm, conn);

			if (result.getErrCode() < 0) {
				return result;
			}

			// ����ҩƷ���롢Ч�ڡ����źͲ��Ų�ѯҩƷ������� ���ģ������BATCH_SEQ���ɹ���һ��ȡ
			// String sql =
			// INDSQL.getIndStockBatchSeq(parm.getValue("ORG_CODE"),
			// parm_D.getValue("ORDER_CODE", i), parm_D.getValue("BATCH_NO", i),
			// parm_D
			// .getValue("VALID_DATE", i).substring(0, 10));

			String orgCode = parm.getValue("ORG_CODE");
			String orderCode = parm_D.getValue("ORDER_CODE", i);
			String batchNo = parm_D.getValue("BATCH_NO", i);
			String validDate = parm_D.getValue("VALID_DATE", i)
					.substring(0, 10);

			double verifyPrice = StringTool.round(parm_D.getDouble(
					"STOCK_PRICE", i), 4);
			String verifyPriceStr = verifyPrice + "";

			String supCode = parm_D.getValue("SUP_CODE", i);
			String sql = INDSQL.getIndStockBatchSeqBy(orgCode, orderCode,
					batchNo, validDate, verifyPriceStr, supCode);

			TParm batch_seq_Parm = new TParm(TJDODBTool.getInstance().select(
					sql));
			if (batch_seq_Parm.getErrCode() < 0) {
				return batch_seq_Parm;
			}
			// �޸ĳ��ⲿ�ſ����
			// System.out.println(parm_D);
			// luhai 2012-4-5 ���ݳ��ⵥλ�ж��Ƿ���Ҫ���е�λת�� begin
			// double actualQty=parm_D.getDouble("ACTUAL_QTY", i);
			// result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
			// parm.getValue("REQTYPE_CODE"), parm.getValue("ORG_CODE"),
			// parm_D.getValue("ORDER_CODE", i),
			// batch_seq_Parm.getInt("BATCH_SEQ", 0),
			// -parm_D.getDouble("ACTUAL_QTY", i),
			// -parm_D.getDouble("RETAIL_ATM", i),
			// parm.getValue("OPT_USER"), parm.getTimestamp("OPT_DATE"),
			// parm.getValue("OPT_TERM"), conn);
			double actualQty = parm_D.getDouble("ACTUAL_QTY", i);
			// String orderCode = parm_D.getValue("ORDER_CODE", i);
			String unitCode = parm_D.getValue("UNIT_CODE", i);
			String stockUnit = getStockUnit(orderCode);
			// ҩƷ��ҩʱ�Ϳ�浥λ��ͬ����Ҫ����ת���ʴ���indstock �淢ҩ��λ
			if (unitCode.equals(stockUnit)) {
				actualQty = getDosageQty(orderCode) * actualQty;
			} else {
				// ���ⵥ�ݾ��Ƿ�ҩ��λ����Ҫ����
				actualQty = actualQty;
			}
			result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
					parm.getValue("REQTYPE_CODE"), parm.getValue("ORG_CODE"),
					parm_D.getValue("ORDER_CODE", i),
					parm_D.getInt("BATCH_SEQ", i), -actualQty,
					-parm_D.getDouble("RETAIL_ATM", i),
					parm.getValue("OPT_USER"), parm.getTimestamp("OPT_DATE"),
					parm.getValue("OPT_TERM"), conn);
			// luhai 2012-4-5 ���ݳ��ⵥλ�ж��Ƿ���Ҫ���е�λת�� end
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// ���³��ⵥ����״̬
		result = IndDispenseMTool.getInstance().onUpdateMFlg(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �õ�ҩƷ�Ŀ�浥λ
	 * 
	 * @param orderCode
	 * @return
	 */
	public String getStockUnit(String orderCode) {
		String stockUnit = "";
		Map select = TJDODBTool.getInstance().select(
				INDSQL.getPHAInfoByOrder(orderCode));
		TParm orderParm = new TParm(select);
		if (orderParm.getCount() <= 0) {
			return null;
		}
		stockUnit = orderParm.getValue("STOCK_UNIT", 0);
		return stockUnit;
	}

	/**
	 * �õ�ҩƷ�ķ�ҩ-���ת����
	 * 
	 * @return
	 */
	public double getDosageQty(String orderCode) {
		double dosageQty = 1;
		Map select = TJDODBTool.getInstance().select(
				INDSQL.getPHAInfoByOrder(orderCode));
		TParm orderParm = new TParm(select);
		if (orderParm.getCount() <= 0) {
			return 1;
		}
		dosageQty = orderParm.getDouble("DOSAGE_QTY", 0);
		return dosageQty;
	}

	/**
	 * ��ҩ�Զ�ά���շѱ�׼
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateGrpricePrice(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parm_D = parm.getParm("DATA");
		String opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		String opt_term = parm.getValue("OPT_TERM");
		String org_code = parm.getValue("ORG_CODE");
		String order_code = "";
		double own_price = 0;
		double old_own_price = 0;
		double diff_price = 0;
		String batch_no = "";
		String valid_date = "";
		Timestamp datetime = SystemTool.getInstance().getDate();
		String date = StringTool.getString(datetime, "yyyyMMddHHmmss");
		TParm historyParm = new TParm();
		for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
			order_code = parm_D.getValue("ORDER_CODE", i);
			own_price = parm_D.getDouble("OWN_PRICE", i);
			historyParm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFeeHistory(order_code, true)));
			// 1.����SYS_FEE_HISTORY����ʷ����
			TParm updateParm = historyParm.getRow(0);
			updateParm.setData("OPT_USER", opt_user);
			updateParm.setData("OPT_DATE", opt_date);
			updateParm.setData("OPT_TERM", opt_term);
			updateParm.setData("END_DATE", date);
			updateParm.setData("RPP_CODE", "");
			updateParm.setData("ACTIVE_FLG", "N");
			result = SYSFeeHistoryTool.getInstance().onUpdate(updateParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 2.����SYS_FEE_HISTORY����������
			updateParm.setData("START_DATE", date);
			updateParm.setData("END_DATE", "99991231235959");
			updateParm.setData("ACTIVE_FLG", "Y");
			updateParm.setData("OWN_PRICE", own_price);
			updateParm.setData("OWN_PRICE2", own_price);
			updateParm.setData("OWN_PRICE3", own_price);
			result = SYSFeeHistoryTool.getInstance().onInsert(updateParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 3.����SYS_FEE����(��SYS_FEE_HISTORY��ȡ���������ݸ���SYS_FEE)
			TParm newParm = new TParm();
			newParm = this.setParmData(newParm, historyParm);
			newParm.setData("OWN_PRICE", own_price);
			newParm.setData("OWN_PRICE2", own_price);
			newParm.setData("OWN_PRICE3", own_price);
			String order_cat1_code = newParm.getValue("ORDER_CAT1_CODE");
			TParm cat1Parm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSysOrderCat1(order_cat1_code)));
			newParm.setData("CAT1_TYPE", cat1Parm.getValue("CAT1_TYPE", 0));
			result = SYSFeeTool.getInstance().onUpdate(newParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 4.����PHA_BASE�е����ۼۺ�������
			TParm phaParm = new TParm();
			phaParm.setData("ORDER_CODE", order_code);
			phaParm.setData("RETAIL_PRICE", own_price);
			phaParm.setData("TRADE_PRICE", own_price);
			result = PhaBaseTool.getInstance().onUpdateRetailPrice(phaParm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 5.��������
			old_own_price = parm_D.getDouble("OLD_OWN_PRICE", i);
			diff_price = own_price - old_own_price;
			batch_no = parm_D.getValue("BATCH_NO", i);
			valid_date = parm_D.getValue("VALID_DATE", i);
			String sql = "UPDATE IND_STOCK SET PROFIT_LOSS_AMT = PROFIT_LOSS_AMT + ("
					+ diff_price
					+ " * STOCK_QTY), OPT_USER= '"
					+ opt_user
					+ "', OPT_DATE = SYSDATE, OPT_TERM = '"
					+ opt_term
					+ "', RETAIL_PRICE = "
					+ own_price
					+ " WHERE ORDER_CODE = '"
					+ order_code
					+ "' AND STOCK_QTY > 0 AND ACTIVE_FLG = 'Y' AND (BATCH_NO <> '"
					+ batch_no
					+ "' OR VALID_DATE <> TO_DATE('"
					+ valid_date
					+ "', 'YYYYMMDD')) AND ORG_CODE = '" + org_code + "'";
			// System.out.println("sql----"+sql);
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	public TParm onUpdateGrpricePriceSpc(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm parm_D = parm.getParm("DATA");
		String opt_user = parm.getValue("OPT_USER");
		Timestamp opt_date = parm.getTimestamp("OPT_DATE");
		String opt_term = parm.getValue("OPT_TERM");
		String org_code = parm.getValue("ORG_CODE");
		String order_code = "";
		double own_price = 0;
		double old_own_price = 0;
		double diff_price = 0;
		String batch_no = "";
		String valid_date = "";
		Timestamp datetime = SystemTool.getInstance().getDate();
		String date = StringTool.getString(datetime, "yyyyMMddHHmmss");
		TParm historyParm = new TParm();
		for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
			order_code = parm_D.getValue("ORDER_CODE", i);
			own_price = parm_D.getDouble("OWN_PRICE", i);
			historyParm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSYSFeeHistory(order_code, true)));
			// 1.����SYS_FEE_HISTORY����ʷ����
			TParm updateParm = historyParm.getRow(0);
			updateParm.setData("OPT_USER", opt_user);
			updateParm.setData("OPT_DATE", opt_date);
			updateParm.setData("OPT_TERM", opt_term);
			updateParm.setData("END_DATE", date);
			updateParm.setData("RPP_CODE", "");
			updateParm.setData("ACTIVE_FLG", "N");
			result = SYSFeeHistoryTool.getInstance().onUpdate(updateParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 2.����SYS_FEE_HISTORY����������
			updateParm.setData("START_DATE", date);
			updateParm.setData("END_DATE", date);
			updateParm.setData("ACTIVE_FLG", "Y");
			updateParm.setData("OWN_PRICE", own_price);
			updateParm.setData("OWN_PRICE2", own_price);
			updateParm.setData("OWN_PRICE3", own_price);
			result = SYSFeeHistoryTool.getInstance().onInsert(updateParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 3.����SYS_FEE����(��SYS_FEE_HISTORY��ȡ���������ݸ���SYS_FEE)

			TParm newParm = new TParm();
			newParm = this.setParmData(newParm, historyParm);
			newParm.setData("OWN_PRICE", own_price);
			newParm.setData("OWN_PRICE2", own_price);
			newParm.setData("OWN_PRICE3", own_price);
			String order_cat1_code = newParm.getValue("ORDER_CAT1_CODE");
			TParm cat1Parm = new TParm(TJDODBTool.getInstance().select(
					SYSSQL.getSysOrderCat1(order_cat1_code)));
			newParm.setData("CAT1_TYPE", cat1Parm.getValue("CAT1_TYPE", 0));

			if (null == newParm.getData("SYS_PHA_CLASS"))
				newParm.setData("SYS_PHA_CLASS", "");
			if (null == newParm.getData("SYS_GRUG_CLASS"))
				newParm.setData("SYS_GRUG_CLASS", "");
			if (null == newParm.getData("NOADDTION_FLG"))
				newParm.setData("NOADDTION_FLG", "");

			result = SYSFeeTool.getInstance().onUpdate(newParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 4.����PHA_BASE�е����ۼۺ�������
			TParm phaParm = new TParm();
			phaParm.setData("ORDER_CODE", order_code);
			phaParm.setData("RETAIL_PRICE", own_price);
			phaParm.setData("TRADE_PRICE", own_price);
			result = PhaBaseTool.getInstance().onUpdateRetailPrice(phaParm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 5.��������
			old_own_price = parm_D.getDouble("OLD_OWN_PRICE", i);
			diff_price = own_price - old_own_price;
			batch_no = parm_D.getValue("BATCH_NO", i);
			valid_date = parm_D.getValue("VALID_DATE", i);
			String sql = "UPDATE IND_STOCK SET PROFIT_LOSS_AMT = PROFIT_LOSS_AMT + ("
					+ diff_price
					+ " * STOCK_QTY), OPT_USER= '"
					+ opt_user
					+ "', OPT_DATE = SYSDATE, OPT_TERM = '"
					+ opt_term
					+ "', RETAIL_PRICE = "
					+ own_price
					+ " WHERE ORDER_CODE = '"
					+ order_code
					+ "' AND STOCK_QTY > 0 AND ACTIVE_FLG = 'Y' AND (BATCH_NO <> '"
					+ batch_no
					+ "' OR VALID_DATE <> TO_DATE('"
					+ valid_date
					+ "', 'YYYYMMDD')) AND ORG_CODE = '" + org_code + "'";
			// System.out.println("sql----"+sql);
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * ��PARM��ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param data
	 *            TParm
	 * @return TParm
	 */
	public TParm setParmData(TParm parm, TParm data) {
		String[] arrays = {
				"ORDER_CODE",
				"START_DATE",
				"END_DATE",
				"ORDER_DESC",
				"ACTIVE_FLG",// 1-5
				"LAST_FLG",
				"PY1",
				"PY2",
				"SEQ",
				"DESCRIPTION",// 6-10
				"TRADE_ENG_DESC",
				"GOODS_DESC",
				"GOODS_PYCODE",
				"ALIAS_DESC",
				"ALIAS_PYCODE",// 11-15
				"SPECIFICATION",
				"NHI_FEE_DESC",
				"HABITAT_TYPE",
				"MAN_CODE",
				"HYGIENE_TRADE_CODE",// 16-20
				"ORDER_CAT1_CODE",
				"CHARGE_HOSP_CODE",
				"OWN_PRICE",
				"NHI_PRICE",
				"GOV_PRICE",// 21-25
				"UNIT_CODE",
				"LET_KEYIN_FLG",
				"DISCOUNT_FLG",
				"EXPENSIVE_FLG",
				"OPD_FIT_FLG",// 26-30
				"EMG_FIT_FLG",
				"IPD_FIT_FLG",
				"HRM_FIT_FLG",
				"DR_ORDER_FLG",
				"INTV_ORDER_FLG",// 31-35
				"LCS_CLASS_CODE",
				"TRANS_OUT_FLG",
				"TRANS_HOSP_CODE",
				"USEDEPT_CODE",
				"EXEC_ORDER_FLG",// 36-40
				"EXEC_DEPT_CODE",
				"INSPAY_TYPE",
				"ADDPAY_RATE",
				"ADDPAY_AMT",
				"NHI_CODE_O",// 41-45
				"NHI_CODE_E", "NHI_CODE_I",
				"CTRL_FLG",
				"CLPGROUP_CODE",
				"ORDERSET_FLG",// 46-50
				"INDV_FLG", "SUB_SYSTEM_CODE",
				"RPTTYPE_CODE",
				"DEV_CODE",// 51-55
				"OPTITEM_CODE", "MR_CODE", "DEGREE_CODE",
				"CIS_FLG",
				"OPT_USER",// 56-56
				"OPT_DATE", "OPT_TERM", "ATC_FLG", "OWN_PRICE2",
				"OWN_PRICE3",// 61-65
				"TUBE_TYPE", "IS_REMARK", "ACTION_CODE", "ATC_FLG_I",
				"CAT1_TYPE", "REGION_CODE" }; // 66-70
		for (int i = 0; i < arrays.length; i++) {
			parm.setData(arrays[i], data.getData(arrays[i], 0));
		}
		return parm;
	}

	/**
	 * �õ�SYS_FEE���
	 * 
	 * @param orderCode
	 *            String
	 * @return String
	 */
	public synchronized TParm getSysFeeOrder(String orderCode) {
		TDataStore d = com.dongyang.manager.TIOM_Database
				.getLocalTable("SYS_FEE");
		this.code = orderCode;
		d.filterObject(this, "filter");
		if (d.rowCount() == 0)
			return null;
		return d.getRowParm(0);
	}

	/**
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filter(TParm parm, int row) {
		return parm.getValue("ORDER_CODE", row).equals(this.code);
	}

	/**
	 * IND_STCOKM��������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateIndStockM(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		result = IndStockMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		String sql = INDSQL.onUpdateIndStcokMaterialLocCode(parm
				.getValue("ORG_CODE"), parm.getValue("ORDER_CODE"), parm
				.getValue("MATERIAL_LOC_CODE"));
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result == null || result.getErrCode() < 0) {
			return result;
		}
		// <------- identity by shendr 20131106 ����ͬ������IND_STOCK.ACTIVE_FLG����
		// result = new TParm(TJDODBTool.getInstance().update(
		// INDSQL.updateIndStock(parm.getValue("ORG_CODE"),
		// parm.getValue("ORDER_CODE"),parm.getValue("ACTIVE_FLG")),conn));
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// -------->
		return result;
	}

	/**
	 * ������������ҩ(סԺҩ��ȫ����������ҩ)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm luhai add ���뷵��ֵ�м���ۿ�ĵ�order��Ϣ
	 */
	public TParm reduceIndStockByStation(TParm parm, TConnection conn) {
		// luhai add 2012-1-28 begin
		TParm returnParm = new TParm();
		// luhai add 2012-1-28 end
		// ���ݼ��
		if (parm == null) {
			return null;
		}
		// System.out.println("parm===" + parm);
		// �����
		TParm result = new TParm();
		// ��Config�ļ���ȡ���Զ�ѭ������
		// int count =
		// Integer.parseInt(TConfig.getSystemValue("CYCLE_TIMES_FOR_ERR"))
		int count = 1;
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		double own_price = 0;
		// i: �����Զ�ѭ������
		for (int i = 0; i < count; i++) {
			// ��ѯҩƷ��ҩ��Ϣ,ƴ����������
			// �������
			String update_sql = "";
			List list = new ArrayList(); // ���ڴ洢SQL�������
			List order_list = new ArrayList(); // ���ڴ洢ORDER_CODE
			// j: ҩƷ�������
			int parmCount = parm.getCount("ORDER_CODE");
			// System.out.println("parmCount=" + parmCount);
			for (int j = 0; j < parmCount; j++) {
				org_code = parm.getValue("ORG_CODE", j);
				order_code = parm.getValue("ORDER_CODE", j);
				dosage_qty = parm.getDouble("DOSAGE_QTY", j);
				own_price = parm.getDouble("OWN_PRICE", j);
				// zhangyong20101119 begin
				// ��˿��
				boolean check_qty = INDTool.getInstance().inspectIndStock(
						org_code, order_code, dosage_qty);
				if (!check_qty) {
					result.setErrCode(-1);
					TParm sysFee = new TParm(TJDODBTool.getInstance().select(
							SYSSQL.getSYSFee(order_code)));
					result.setData("ORDER_CODE", order_code + " "
							+ sysFee.getValue("ORDER_DESC", 0));
					return result;
				}
				// zhangyong20101119 end

				// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
				TParm parmOrder = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				// System.out.println("parmOrder=" + parmOrder);
				if (parmOrder.getErrCode() != 0) {
					return parmOrder;
				}

				// �����
				double qty = 0;
				// �������
				int batch_seq = 0;
				// ��������
				double out_qty = dosage_qty;
				// ������
				double out_amt = 0;
				// ���ռ۸�
				double verifyinPrice = 0;
				int parmOrderCount = parmOrder.getCount();
				// **************************************************************
				// luhai modify 2012-1-28 ���۳���order��Ϣ��¼��TParm �� begin
				// **************************************************************
				// for (int k = 0; k < parmOrderCount; k++) {
				// qty = parmOrder.getDouble("QTY", k);
				// batch_seq = parmOrder.getInt("BATCH_SEQ", k);
				// if (qty >= dosage_qty) {
				// // ҩƷ����һ�οۿ�
				// out_amt = StringTool.round(own_price * dosage_qty, 2);
				// update_sql = INDSQL.getUpdateReduceIndStockSql(org_code,
				// order_code, batch_seq, dosage_qty, out_amt,
				// parm.getValue("OPT_USER", j),
				// parm.getValue("OPT_DATE", j),
				// parm.getValue("OPT_TERM", j));
				// //System.out.println("qty>=dosage_qty----" + update_sql);
				// list.add(update_sql);
				// order_list.add(order_code);
				// break;
				// }
				// else {
				// // ҩƷ����һ�οۿ�
				// out_amt = StringTool.round(own_price * qty, 2);
				// update_sql = INDSQL.getUpdateReduceIndStockSql(org_code,
				// order_code, batch_seq, qty, out_amt,
				// parm.getValue("OPT_USER", j),
				// parm.getValue("OPT_DATE", j),
				// parm.getValue("OPT_TERM", j));
				// //System.out.println("qty<dosage_qty----" + update_sql);
				// list.add(update_sql);
				// order_list.add(order_code);
				// // ��������
				// dosage_qty = dosage_qty - qty;
				// }
				// }
				// }
				for (int k = 0; k < parmOrderCount; k++) {
					qty = parmOrder.getDouble("QTY", k);
					batch_seq = parmOrder.getInt("BATCH_SEQ", k);
					verifyinPrice = parmOrder.getDouble("VERIFYIN_PRICE", k);
					if (qty >= dosage_qty) {
						// ҩƷ����һ�οۿ�
						out_amt = StringTool.round(own_price * dosage_qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, dosage_qty,
								out_amt, parm.getValue("OPT_USER", j), parm
										.getValue("OPT_DATE", j), parm
										.getValue("OPT_TERM", j));
						// System.out.println("qty>=dosage_qty----" +
						// update_sql);
						// ��¼�ۿ�ҩƷ
						returnParm.addData("ORDER_CODE", order_code);
						returnParm.addData("DISPENSE_QTY", dosage_qty);
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						break;
					} else {
						// ҩƷ����һ�οۿ�
						out_amt = StringTool.round(own_price * qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, qty, out_amt,
								parm.getValue("OPT_USER", j), parm.getValue(
										"OPT_DATE", j), parm.getValue(
										"OPT_TERM", j));
						// System.out.println("qty<dosage_qty----" +
						// update_sql);
						// ��¼�ۿ�ҩƷ
						returnParm.addData("ORDER_CODE", order_code);
						returnParm.addData("DISPENSE_QTY", dosage_qty);
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						// ��������
						dosage_qty = dosage_qty - qty;
					}
				}
			}
			// ********************************************************
			// luhai modify 2012-1-28 ���۳���order��Ϣ��¼��TParm �� end
			// ******************************************************
			// ִ��ƴ���ĸ���SQL�����
			String[] sql = new String[list.size()];
			for (int k = 0; k < list.size(); k++) {
				sql[k] = list.get(k).toString();
			}
			if (sql == null) {
				result.setErrCode(-1);
				return result;
			}
			if (sql.length < 1) {
				result.setErrCode(-1);
				return result;
			}

			for (int l = 0; l < sql.length; l++) {
				result = new TParm(TJDODBTool.getInstance()
						.update(sql[l], conn));
				if (result.getErrCode() < 0) {
					result.setErrCode(-1);
					TParm sysFee = new TParm(TJDODBTool.getInstance().select(
							SYSSQL.getSYSFee(order_list.get(l).toString())));
					result.setData("ORDER_CODE", order_list.get(l) + " "
							+ sysFee.getValue("ORDER_DESC", 0));
					return result;
				}
			}
		}

		// return result;
		// ������δ���쳣���򷵻ؿۿ��ҩƷParm
		return returnParm;
	}

	/**
	 * ������������ҩȱҩ��Ϣ(סԺҩ��ȫ����������ҩ)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm defectIndStockQTY(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		String order_code = "";
		String org_code = "";
		double dosage_qty = 0;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			org_code = parm.getValue("ORG_CODE", i);
			order_code = parm.getValue("ORDER_CODE", i);
			dosage_qty = parm.getDouble("DOSAGE_QTY", i);
			// ===zhangp 20120806 start
			String tables = parm.getValue("TABLES");
			String conditions = parm.getValue("CONDITIONS");
			String sql = "SELECT SUM(A.LAST_TOTSTOCK_QTY+ A.IN_QTY- A.OUT_QTY + A.CHECKMODI_QTY) AS QTY "
					+ " FROM IND_STOCK A "
					+ tables
					+ " WHERE A.ACTIVE_FLG='Y' "
					+ " AND SYSDATE < A.VALID_DATE "
					+ " AND A.ORG_CODE = '"
					+ org_code
					+ "' "
					+ " AND A.ORDER_CODE = '"
					+ order_code
					+ "'" + conditions;
			// TParm parmOrder = IndStockDTool.getInstance().
			// onQueryStockQTY(parm.getRow(i));
			TParm parmOrder = new TParm(TJDODBTool.getInstance().select(sql));
			// ===zhangp 20120806 end
			if (parmOrder.getErrCode() != 0 || parmOrder.getCount() <= 0) {
				return null;
			}
			if (dosage_qty > parmOrder.getDouble("QTY", 0)) {
				result.addData("ORG_CODE", org_code);
				result.addData("ORDER_CODE", order_code);
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
				result.addData("STOCK_QTY", parmOrder.getDouble("QTY", 0));
				result.addData("DIFF_QTY", dosage_qty
						- parmOrder.getDouble("QTY", 0));
			}
		}
		return result;
	}

	/**
	 * ȡ��ҩƷ����Ϣ,��ʽ�� ҩƷ���� + (��Ʒ��)
	 * 
	 * @return String
	 */
	public String getOrderDescAndGoodsDesc(String order_code,
			String order_desc, String goods_desc) {
		// ҩƷ����Ϊ��
		if (!"".equals(order_code)) {
			TParm parm = getSysFeeOrder(order_code);
			return getOrderDescAndGoodsDesc("", parm.getValue("ORDER_DESC"),
					parm.getValue("GOODS_DESC"));
		} else {
			// ��Ʒ��Ϊ��
			if ("".equals(goods_desc)) {
				return order_desc;
			}
			// ��Ʒ����Ϊ��
			else {
				return order_desc + "(" + goods_desc + ")";
			}
		}
	}

	/**
	 * ����ҩƷ��batchseq ordercode ��ind_verifyin�еõ�����ʱ�ļ۸� luhai add 2012-05-03
	 * 
	 * @return
	 */
	public double getVerifyInStockPrice(String orderCode, int batchSeq) {
		Map select = TJDODBTool.getInstance().select(
				INDSQL.getIndVerifyInPrice(orderCode, batchSeq));
		TParm result = new TParm(select);
		if (result.getErrCode() < 0) {
			return 0;
		} else if (result.getCount() <= 0) {
			return 0;
		} else {
			return result.getDouble("VERIFYIN_PRICE", 0);
		}
	}

	/**
	 * ����ҩƷ��batchseq ordercode ��ind_stock�еõ�����ʱ�ļ۸� luhai add 2012-05-03
	 * 
	 * @return
	 */
	public double getVerifyInStockPrice(String orgCode, String orderCode,
			int batchSeq) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndVerifyInPrice(orgCode, orderCode, batchSeq)));
		if (result.getErrCode() < 0) {
			return 0;
		} else if (result.getCount() <= 0) {
			return 0;
		} else {
			return result.getDouble("VERIFYIN_PRICE", 0);
		}
	}

	/**
	 * �������յ�
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onSaveRequestMD(TParm parmM, TParm parmD, TConnection conn) {
		// ���ݼ��
		if (parmM == null)
			return null;
		// ������������
		TParm result = this.update("createNewRequestM", parmM, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		// ����������ϸ��
		int count = parmD.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			result = IndRequestDTool.getInstance().onInsert(parmD, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		/** �ƻ����Ÿ��� */

		return result;
	}

	/**
	 * �Զ�����-������������
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public TParm onSaveRequestMAuto(TParm parmM, TParm parmD, TConnection conn) {
		/************************************ ������������ start ****************************/
		TParm result = new TParm();

		String orgCode = (String) parmM.getValue("APP_ORG_CODE");
		String toOrgCode = (String) parmM.getValue("TO_ORG_CODE");
		String requestNo = (String) parmM.getValue("REQUEST_NO");
		String fixedType = (String) parmM.getValue("FIXEDAMOUNT_FLG");
		String autoFillType = (String) parmM.getValue("AUTO_FILL_TYPE");
		String isDrug = (String) parmM.getValue("DRUG_CATEGORY");
		TParm parm = getInsertMParm(requestNo, orgCode, toOrgCode, isDrug);
		// System.out.println("----------saveRequestMAuto" +
		// INDSQL.saveRequestMAuto(parm));
		result = new TParm(TJDODBTool.getInstance().update(
				INDSQL.saveRequestMAuto(parm), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		/************************************ ������������ end ****************************/

		/************************************ ����������ϸ�� start ************************/
		/*
		 * //��ѯ�����Զ�������ҩƷ TParm stockm = new
		 * TParm(TJDODBTool.getInstance().select
		 * (INDSQL.queryStockM(orgCode,fixedType)));
		 * System.out.println("---------��ѯ�����Զ�������ҩƷ: "+stockm); // �ж��Ƿ��Ѿ������� if
		 * (null == stockm || stockm.getCount("ORDER_CODE") < 0) { return
		 * stockm; }
		 */
		for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
			String orderCode = parmD.getValue("ORDER_CODE", i);
			// System.out.println("--------orderCode: " + orderCode);
			// ��߿����
			double maxQty = parmD.getDouble("MAX_QTY", i);
			// ��Ϳ����
			double minQty = parmD.getDouble("MIN_QTY", i);
			// ��ȫ�����
			double safeQty = parmD.getDouble("SAFE_QTY", i);
			// ���ò�����
			double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", i);
			// ��ǰ�����
			double stockQty = parmD.getDouble("STOCK_QTY", i);
			// ��;��
			double buyQty = 0;// parmD.getDouble("BUY_UNRECEIVE_QTY", i);//��ʱ�޸�
			// ��;�� �ݶ�Ϊ0 ԭ���� ���ڿ�������㲻׼ȷ
			double fixQty = getOrderCodePrice(maxQty, minQty, safeQty,
					ecoBuyQty, stockQty, buyQty, fixedType, autoFillType);
			if (fixQty < 1) {
				continue;
			}
			// ��װ��ϸ������
			TParm deParm = getRequestDParm(requestNo, i + "", orderCode, fixQty);
			String sqlD = INDSQL.saveRequestDAuto(deParm);
			// System.out.println("---------��ϸsql: " + sqlD);
			result = new TParm(TJDODBTool.getInstance().update(sqlD, conn));
		}
		/************************************ ����������ϸ�� end ************************/
		return result;
	}

	/**
	 * ���ҩƷ��������
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @param maxQty
	 *            �������
	 * @param mixQty
	 *            ��Ϳ����
	 * @param safeQty
	 *            ��ȫ�����
	 * @param ecoBuyQty
	 *            ���ò�����
	 * @param fixedType
	 *            ������ʽ��0:ÿ��(��)��������, 1:���������(���ڰ�ȫ���ʱ) 2:����С�����(���ڰ�ȫ���ʱ)
	 * @param autoFillType
	 *            �Զ�����������1:������2�������������
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	private double getOrderCodePrice(double maxQty, double mixQty,
			double safeQty, double ecoBuyQty, double stockQty,
			double stockQtyIng, String fixedType, String autoFillType) {
		double fixedQty = 0.0;
		if ("0".equals(fixedType)) {// 0�����Ƕ�������
			fixedQty = ecoBuyQty;
		} else {
			if ("2".equals(autoFillType)) {// �Զ�����������1:������2�������������
				fixedQty = maxQty - stockQty - stockQtyIng;
			} else {
				fixedQty = ecoBuyQty;
			}
		}
		return fixedQty;
	}

	/**
	 * ����������������PARM
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	private TParm getInsertMParm(String requestNo, String orgCode,
			String toOrgCode, String isDrug) {
		TParm parm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("REQTYPE_CODE", "ATO");
		parm.setData("APP_ORG_CODE", orgCode);
		parm.setData("TO_ORG_CODE", toOrgCode);
		parm.setData("REQUEST_DATE", SystemTool.getInstance().getDate());
		parm.setData("REQUEST_USER", "AUTO");
		parm.setData("REASON_CHN_DESC", "");
		parm.setData("DESCRIPTION", "DESCRIPTION");
		parm.setData("REGION_CODE", "H01");
		String unit_type = "1";
		parm.setData("UNIT_TYPE", unit_type);
		parm.setData("URGENT_FLG", "N");
		parm.setData("OPT_USER", "USER");
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", "IP");
		parm.setData("DRUG_CATEGORY", isDrug);
		return parm;
	}

	/**
	 *�������ϸ�������PARM
	 * 
	 * @param requestNo
	 * @param seqNo
	 * @param orderCode
	 * @param fixedType
	 *            ������ʽ
	 * @param fixedQty
	 *            ����������ʽ
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	private TParm getRequestDParm(String requestNo, String seqNo,
			String orderCode, double fixQty) {
		TParm parm = new TParm();
		// �õ�ҩƷ�ļ۸�
		TParm priceParm = getOrderCodePrice(orderCode);
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("QTY", fixQty);
		// 1��浥λ��2��ҩ��λ
		parm.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		parm.setData("RETAIL_PRICE", priceParm.getValue("RETAIL_PRICE", 0));
		parm.setData("STOCK_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		return parm;
	}

	/**
	 *��ö���/�ƻ�ϸ�������PARM
	 * 
	 * @param requestNo
	 * @param seqNo
	 * @param orderCode
	 * @param fixedType
	 *            ������ʽ
	 * @param fixedQty
	 *            ����������ʽ
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	private TParm getPurOrderDParm(String purOrderNo, String seqNo,
			String orderCode, double fixQty) {
		TParm parm = new TParm();
		// �õ�ҩƷ�ļ۸�
		TParm priceParm = getOrderCodePrice(orderCode);
		parm.setData("PURORDER_NO", purOrderNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("PURORDER_QTY", fixQty);
		// 1��浥λ��2��ҩ��λ
		parm.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		parm.setData("PURCH_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		return parm;
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
		pur_price = StringTool.round(result.getDouble("A.PURCH_UNIT", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// ��浥λ
		stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// ��ҩ��λ
		retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// ��浥λ
		parm.addData("UNIT_CODE", result.getValue("STOCK_UNIT", 0));
		parm.addData("PURCH_PRICE", String.valueOf(pur_price));
		parm.addData("STOCK_PRICE", String.valueOf(stock_price));
		parm.addData("RETAIL_PRICE", String.valueOf(retail_price));
		return parm;
	}

	/**
	 * �Զ�����-��������/�ƻ�����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public TParm onSavePurOrderMAuto(TParm parmM, TParm parmD, TConnection conn) {
		/************************************ ���涩��/�ƻ����� start ****************************/
		TParm result = new TParm();

		String purOrderNo = (String) parmM.getValue("PURORDER_NO");
		String fixedType = (String) parmM.getValue("FIXEDAMOUNT_FLG");
		String autoFillType = (String) parmM.getValue("AUTO_FILL_TYPE");
		// System.out.println("----------saveRequestMAuto" +
		// INDSQL.savePurOrderMAuto(parmM));
		result = new TParm(TJDODBTool.getInstance().update(
				INDSQL.savePurOrderMAuto(parmM), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		/************************************ ���涩��/�ƻ����� end ****************************/

		/************************************ ���涩��/�ƻ���ϸ�� start ************************/
		/*
		 * //��ѯ�����Զ�������ҩƷ TParm stockm = new
		 * TParm(TJDODBTool.getInstance().select
		 * (INDSQL.queryStockM(orgCode,fixedType)));
		 * System.out.println("---------��ѯ�����Զ�������ҩƷ: "+stockm); // �ж��Ƿ��Ѿ������� if
		 * (null == stockm || stockm.getCount("ORDER_CODE") < 0) { return
		 * stockm; }
		 */
		for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
			String orderCode = parmD.getValue("ORDER_CODE", i);
			// System.out.println("--------orderCode: " + orderCode);
			// ��߿����
			double maxQty = parmD.getDouble("MAX_QTY", i);
			// ��Ϳ����
			double minQty = parmD.getDouble("MIN_QTY", i);
			// ��ȫ�����
			double safeQty = parmD.getDouble("SAFE_QTY", i);
			// ���ò�����
			double ecoBuyQty = parmD.getDouble("ECONOMICBUY_QTY", i);
			// ��ǰ�����
			double stockQty = parmD.getDouble("STOCK_QTY", i);
			stockQty = getStockQtyOfOrderCodeFromate(orderCode, stockQty);
			double buyQty = parmD.getDouble("BUY_UNRECEIVE_QTY", i);
			// �õ�ҩƷת����

			double fixQty = getOrderCodePrice(maxQty, minQty, safeQty,
					ecoBuyQty, stockQty, buyQty, fixedType, autoFillType);
			// ��װ��ϸ������
			TParm deParm = getPurOrderDParm(purOrderNo, i + "", orderCode,
					fixQty);
			String sqlD = INDSQL.savePurOrderDAuto(deParm);
			// System.out.println("---------��ϸsql: " + sqlD);
			result = new TParm(TJDODBTool.getInstance().update(sqlD, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		/************************************ ���涩��/�ƻ���ϸ�� end ************************/
		return result;
	}

	/**
	 *ҩƷ�������װ��-��dosageQty���stockQty
	 * 
	 * @param orderCode
	 * @param qty
	 * @return
	 */
	public int getStockQtyOfOrderCodeFromate(String orderCode, double qty) {
		int stockQty = (int) qty;
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getPHAInfoByOrder(orderCode)));
		if (null != parm) {
			int dosageQty = parm.getInt("DOSAGE_QTY", 0);
			stockQty = getStockQtyInt(qty, dosageQty);
		}
		return stockQty;
	}

	public int getStockQtyInt(double fixQty, double dosageQty) {
		int result = 0;
		if (dosageQty > 0) {
			result = (int) (fixQty / dosageQty)
					+ (fixQty % dosageQty == 0 ? 0 : 1);
		} else {
			result = (int) fixQty;
		}
		return result;
	}

	/**
	 * ���������ҵ
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseInToxic(TParm parm, TConnection conn) {

		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���뵥���Զ�ά��
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		TParm parmM = parm.getParm("OUT_M");
		// ���뵥����
		String request_type = parmM.getValue("REQTYPE_CODE");
		// �������������Ϣ
		result = IndDispenseMTool.getInstance().onUpdateM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		TParm parmD = parm.getParm("OUT_D");
		for (int i = 0; i < parmD.getCount("DISPENSE_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("DISPENSE_NO", parmD.getValue("DISPENSE_NO", i));
			inparm.setData("SEQ_NO", parmD.getInt("SEQ_NO", i));
			double stock_price = parmD.getDouble("STOCK_PRICE", i);
			inparm.setData("STOCK_PRICE", stock_price);
			inparm.setData("ACTUAL_QTY", parmD.getDouble("ACTUAL_QTY", i));
			inparm.setData("INSTORE_QTY", parmD.getDouble("INSTORE_QTY", i));
			inparm.setData("OPT_USER", parmD.getValue("OPT_USER", i));
			inparm.setData("OPT_DATE", parmD.getTimestamp("OPT_DATE", i));
			inparm.setData("OPT_TERM", parmD.getValue("OPT_TERM", i));
			if (parmD.getDouble("OLD_INSTORE_QTY", i)
					+ parmD.getDouble("INSTORE_QTY", i) >= parmD.getDouble(
					"ACTUAL_QTY", i)) {
				inparm.setData("IS_PUTAWAY", "Y");
			} else {
				inparm.setData("IS_PUTAWAY", "N");
			}
			// �������ϸ����Ϣ
			result = IndDispenseDTool.getInstance()
					.onUpdateDToxic(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// �����ҵ
		if (parm.existData("IN_ORG")) {
			// ��ⲿ��
			String in_org = parm.getValue("IN_ORG");
			// �Ƿ����
			boolean flg = parm.getBoolean("IN_FLG");
			// ҩƷ����
			String order_code = "";
			// ��λ����
			String unit_type = parmM.getValue("UNIT_TYPE");
			// �ۿ���
			double dosage_qty = 0;
			// ���ۼ�
			double retail_price = 0;
			// �������
			int batch_seq = 0;
			if (flg) {
				List list = new ArrayList();
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					order_code = parmD.getValue("ORDER_CODE", i);
					// ������б�
					list.add(order_code);
					if ("0".equals(unit_type)) {
						dosage_qty = parmD.getDouble("INSTORE_QTY", i)
								* parmD.getDouble("DOSAGE_QTY", i);
					} else {
						dosage_qty = parmD.getDouble("INSTORE_QTY", i);
					}
					// ָ�����ź�Ч��(�����ҵδ���ָ��Ч������,ץȡ����������+1����)
					batch_seq = parmD.getInt("BATCH_SEQ", i);
					retail_price = parmD.getDouble("RETAIL_PRICE", i)
							/ parmD.getDouble("DOSAGE_QTY", i);
					String valid_date = parmD.getValue("VALID_DATE", i)
							.replace('/', '-').substring(0, 10);
					String batch_no = parmD.getValue("BATCH_NO", i);
					String opt_user = parmD.getValue("OPT_USER", i);
					Timestamp opt_date = parmD.getTimestamp("OPT_DATE", i);
					String opt_term = parmD.getValue("OPT_TERM", i);
					String supCode = parmD.getValue("SUP_CODE", i);
					double inventPrice = parmD.getDouble("INVENT_PRICE", i);
					double verifyin_price = parmD
							.getDouble("VERIFYIN_PRICE", i);
					String supOrderCode = parmD.getValue("SUP_ORDER_CODE", i);
					result = IndStockDTool.getInstance()
							.onUpdateStockByBatchVaildInToxic(request_type,
									in_org, order_code, batch_seq, valid_date,
									batch_no, dosage_qty, retail_price,
									verifyin_price, opt_user, opt_date,
									opt_term, supCode, supOrderCode,
									inventPrice, list, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}

				/** �ƶ���Ȩƽ���ɱ� */
				double in_qty = 0;
				double d_qty = 0;
				double stock_price = 0;
				double in_amt = 0;
				/*
				 * for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
				 * in_qty = parmD.getDouble("ACTUAL_QTY", i); d_qty =
				 * parmD.getDouble("DOSAGE_QTY", i); stock_price =
				 * parmD.getDouble("STOCK_PRICE", i); in_amt =
				 * StringTool.round(in_qty * stock_price, 2); in_qty =
				 * StringTool.round(in_qty * d_qty *
				 * parmD.getDouble("STOCK_QTY", i), 2); stock_price =
				 * getPhaBaseStockPrice(parm.getValue("ORDER_CODE", i), in_amt,
				 * in_qty); TParm stock_price_parm = new TParm();
				 * stock_price_parm.setData("ORDER_CODE",
				 * parmD.getValue("ORDER_CODE", i));
				 * stock_price_parm.setData("STOCK_PRICE", stock_price); result
				 * =
				 * PhaBaseTool.getInstance().onUpdateStockPrice(stock_price_parm
				 * , conn); if (result.getErrCode() < 0) { return result; } }
				 */

				/** �����۸��� */
				if ("Y".equals(reuprice_flg)) {
					for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
						d_qty = parmD.getDouble("DOSAGE_QTY", i);
						stock_price = StringTool.round(parm.getDouble(
								"STOCK_PRICE", i)
								/ d_qty, 4);
						TParm trade_price_parm = new TParm();
						trade_price_parm.setData("ORDER_CODE", parmD.getValue(
								"ORDER_CODE", i));
						trade_price_parm.setData("TRADE_PRICE", stock_price);
						result = PhaBaseTool.getInstance().onUpdateTradePrice(
								trade_price_parm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * �������յ�
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateVerifySpc(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// ���տ���
		String org_code = parm.getValue("ORG_CODE");

		/**
		 * ���ж������Ƿ���¹����ǲ����£����ձ�����ֻ���ӱ��һ��ҩƷ���ʱ����
		 */
		TParm searchParm = new TParm();
		searchParm.setData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO"));
		result = IndVerifyinMTool.getInstance().onQuery(searchParm);
		String checkuser = "";
		if (result.getErrCode() > 0 && result.getCount("VERIFYIN_NO") > 0) {
			checkuser = result.getRow(0).getValue("CHECK_USER");
		}
		if (checkuser == null || checkuser.equals("")) {
			// ����������Ϣ
			result = IndVerifyinMTool.getInstance().onUpdate(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		/** ���뵥���Զ�ά�� */
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		if ("Y".equals(reuprice_flg)) {

			double prc = parm.getDouble("PRC");

			if (prc > 0) {
				String supCode = parm.getValue("SUP_CODE");
				TParm agentParm = new TParm();
				agentParm.setData("SUP_CODE", supCode);
				agentParm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
				agentParm.setData("CONTRACT_PRICE", parm
						.getDouble("INVENT_PRICE"));
				agentParm.setData("LAST_VERIFY_PRICE", parm
						.getDouble("VERIFYIN_PRICE"));
				agentParm.setData("OPT_USER", parm.getValue("OPT_USER"));
				agentParm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				agentParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				agentParm.setData("CONTRACT_NO", "");
				agentParm.setData("MAIN_FLG", "Y");
				result = IndAgentTool.getInstance().onQuery(agentParm);
				if (result.getCount() > 0) {
					result = IndAgentTool.getInstance().onUpdate(agentParm,
							conn);
				} else {
					result = IndAgentTool.getInstance().onInsert(agentParm,
							conn);
				}
				if (result.getErrCode() < 0) {
					return result;
				}
			}

		}

		// �ж����״̬
		// ��˱��
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** ��������Ӧ��ⲿ�š����������Ч�����Ž��п����������� */
			String ui_flg = "I";
			// �����������(��ҩ��λ)
			double in_qty = 0;
			// ��������(������λ)
			double v_qty = 0;
			// ��������(������λ)
			double g_qty = 0;
			// ����ת����
			// double s_qty = 0;
			// ���ת����
			double d_qty = 0;
			// ���ۼ۸�
			double retail = 0.00;
			// ���ռ۸�
			double verifyin = 0.0000;
			// �����((���������+ ������) * ���ۼ�)
			double in_amt = 0.00;
			String order_code = "";
			ui_flg = parm.getValue("UI_FLG");
			TParm inparm = new TParm();
			inparm.setData("ORG_CODE", org_code);
			order_code = parm.getValue("ORDER_CODE");
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_SEQ", parm.getValue("BATCH_SEQ"));
			v_qty = parm.getDouble("VERIFYIN_QTY");
			g_qty = parm.getDouble("GIFT_QTY");
			// s_qty = parm.getDouble("STOCK_QTY");
			d_qty = parm.getDouble("DOSAGE_QTY");
			in_qty = StringTool.round((v_qty + g_qty) * d_qty, 2);
			inparm.setData("IN_QTY", in_qty);
			retail = parm.getDouble("RETAIL_PRICE");
			// �����
			in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
			inparm.setData("IN_AMT", in_amt);
			inparm.setData("STOCK_QTY", in_qty);
			inparm.setData("VERIFYIN_QTY", in_qty);
			inparm.setData("VERIFYIN_AMT", in_amt);

			inparm.setData("INVENT_PRICE", parm.getDouble("INVENT_PRICE"));

			// ��С��λ���ռ�
			inparm.setData("VERIFYIN_PRICE", parm.getDouble("VERIFYIN_PRICE"));

			// ��������
			inparm.setData("FAVOR_QTY", StringTool.round(g_qty, 2));

			// ���ۼ۸�ȡ����PHA_BASE����Ҫ��ת��(/ s_qty / d_qty)
			// retail = StringTool.round(retail / s_qty / d_qty, 4);
			inparm.setData("RETAIL_PRICE", retail);
			double profit_loss_amt = 0;
			if ("U".equals(ui_flg)) {
				String sql = "SELECT STOCK_QTY, RETAIL_PRICE FROM IND_STOCK WHERE ORG_CODE = '"
						+ org_code
						+ "' AND ORDER_CODE = '"
						+ order_code
						+ "' AND BATCH_SEQ = " + parm.getValue("BATCH_SEQ");
				TParm retailPriceParm = new TParm(TJDODBTool.getInstance()
						.select(sql));
				// ��������
				profit_loss_amt = (retail - retailPriceParm.getDouble(
						"RETAIL_PRICE", 0))
						* retailPriceParm.getDouble("STOCK_QTY", 0);
				inparm.setData("PROFIT_LOSS_AMT", profit_loss_amt);
				// ����ָ�������
				result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			} else {

				// ���������
				String batch_no = parm.getValue("BATCH_NO");
				inparm.setData("BATCH_NO", batch_no);
				String valid = parm.getValue("VALID_DATE");
				inparm.setData("VALID_DATE", StringTool.getTimestamp(valid,
						"yyyy/MM/dd"));
				inparm.setData("MATERIAL_LOC_CODE", parm
						.getValue("MATERIAL_LOC_CODE"));
				inparm.setData("ACTIVE_FLG", "Y");
				inparm.setData("STOCK_FLG", "N");
				inparm.setData("READJUSTP_FLG", "N");
				inparm.setData("LAST_TOTSTOCK_QTY", 0);
				inparm.setData("LAST_TOTSTOCK_AMT", 0);
				inparm.setData("OUT_QTY", 0);
				inparm.setData("OUT_AMT", 0);
				inparm.setData("CHECKMODI_QTY", 0);
				inparm.setData("CHECKMODI_AMT", 0);
				inparm.setData("REGRESSGOODS_QTY", 0);
				inparm.setData("REGRESSGOODS_AMT", 0);
				inparm.setData("DOSEAGE_QTY", 0);
				inparm.setData("DOSAGE_AMT", 0);
				inparm.setData("REGRESSDRUG_QTY", 0);
				inparm.setData("REGRESSDRUG_AMT", 0);
				inparm.setData("FREEZE_TOT", 0);
				inparm.setData("PROFIT_LOSS_AMT", 0);
				inparm.setData("STOCKIN_QTY", 0);
				inparm.setData("STOCKIN_AMT", 0);
				inparm.setData("STOCKOUT_QTY", 0);
				inparm.setData("STOCKOUT_AMT", 0);
				inparm.setData("REQUEST_IN_QTY", 0);
				inparm.setData("REQUEST_IN_AMT", 0);
				inparm.setData("REQUEST_OUT_QTY", 0);
				inparm.setData("REQUEST_OUT_AMT", 0);
				inparm.setData("GIF_IN_QTY", 0);
				inparm.setData("GIF_IN_AMT", 0);
				inparm.setData("GIF_OUT_QTY", 0);
				inparm.setData("GIF_OUT_AMT", 0);
				inparm.setData("RET_IN_QTY", 0);
				inparm.setData("RET_IN_AMT", 0);
				inparm.setData("RET_OUT_QTY", 0);
				inparm.setData("RET_OUT_AMT", 0);
				inparm.setData("WAS_OUT_QTY", 0);
				inparm.setData("WAS_OUT_AMT", 0);
				inparm.setData("THO_OUT_QTY", 0);
				inparm.setData("THO_OUT_AMT", 0);
				inparm.setData("THI_IN_QTY", 0);
				inparm.setData("THI_IN_AMT", 0);
				inparm.setData("COS_OUT_QTY", 0);
				inparm.setData("COS_OUT_AMT", 0);
				inparm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				inparm.setData("RETAIL_PRICE", retail);
				inparm.setData("SUP_CODE", parm.getValue("SUP_CODE"));
				inparm.setData("SUP_ORDER_CODE", parm
						.getValue("SUP_ORDER_CODE"));

				// ÿ��������ʱ���ѯIND_STOCKM������û����Ʒ�֣�û������

				result = IndStockDTool.getInstance().onInsert(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** �ۼ����������ۼ�Ʒ�ʿۿ���� */
			// �ۼ������� ע�͵����¶������ԭ���ǹ�ҩ�����պͶ���û�б�Ȼ��ϵ ������ ֻ�ǳ�����һ������ by liyh 20121219
			/*
			 * double actual = 0; // �ƻ������� double purorder_qty = 0; // �ۼ�Ʒ�ʿۿ�
			 * double deduct_atm = 0; for (int i = 0; i <
			 * parm.getCount("PURORDER_NO"); i++) { TParm inparm = new TParm();
			 * // �������� inparm.setData("PURORDER_NO",
			 * parm.getValue("PURORDER_NO", i)); // ���������
			 * inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i)); // ���������
			 * v_qty = parm.getDouble("VERIFYIN_QTY", i);
			 * inparm.setData("VERIFYIN_QTY", v_qty); // �ۼ������ۿ��� deduct_atm =
			 * parm.getDouble("QUALITY_DEDUCT_AMT", i);
			 * inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm); // �ۼ������� actual
			 * = parm.getDouble("ACTUAL_QTY", i); // �ƻ������� purorder_qty =
			 * parm.getDouble("PURORDER_QTY", i); // ״̬ if (purorder_qty ==
			 * v_qty + actual) { inparm.setData("UPDATE_FLG", "3"); } else {
			 * inparm.setData("UPDATE_FLG", "1"); } result =
			 * IndPurorderDTool.getInstance().onUpdateVer(inparm, conn); if
			 * (result.getErrCode() < 0) { return result; } }
			 */

			/** �ƶ���Ȩƽ���ɱ� */
			v_qty = parm.getDouble("VERIFYIN_QTY");
			g_qty = parm.getDouble("GIFT_QTY");
			// s_qty = parm.getDouble("STOCK_QTY");
			d_qty = parm.getDouble("DOSAGE_QTY");
			// zhangyong20091015 �޸�Ϊ���յ���
			verifyin = parm.getDouble("VERIFYIN_PRICE");
			in_amt = StringTool.round((v_qty + g_qty) * d_qty * verifyin, 2);
			in_qty = StringTool.round((v_qty + g_qty) * d_qty, 2);
			double stock_price = getPhaBaseStockPrice(parm
					.getValue("ORDER_CODE"), in_amt, in_qty);
			// System.out.println("�ƶ���Ȩƽ���ɱ�:" + stock_price);
			TParm stock_price_parm = new TParm();
			stock_price_parm.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			stock_price_parm.setData("STOCK_PRICE", stock_price);
			result = PhaBaseTool.getInstance().onUpdateStockPrice(
					stock_price_parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}

			/** �����۸��� */
			if ("Y".equals(parm.getValue("UPDATE_TRADE_PRICE"))) {
				double verfyin_price = parm.getDouble("VERIFYIN_PRICE");
				TParm trade_price_parm = new TParm();
				trade_price_parm.setData("ORDER_CODE", parm
						.getValue("ORDER_CODE"));
				trade_price_parm.setData("TRADE_PRICE", verfyin_price);
				result = PhaBaseTool.getInstance().onUpdateTradePrice(
						trade_price_parm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/**
			 * IND_STOCKM��ѯ�Ƿ���ڣ����ڣ�������;��������������
			 */
			searchParm.setData("ORG_CODE", org_code);
			searchParm.setData("ORDER_CODE", order_code);
			result = IndStockMTool.getInstance().onQueryAll(searchParm);
			if (result.getCount("ORG_CODE") > 0) {

				/** ��;������ robo 2013/5/6��ҩ�������;��ȥ������ */
				/**
				 * TParm inparm1 = new TParm(); inparm1.setData("ORG_CODE",
				 * org_code); order_code = parm.getValue("ORDER_CODE");
				 * inparm1.setData("ORDER_CODE", order_code); // ������ v_qty =
				 * parm.getDouble("VERIFYIN_QTY"); s_qty =
				 * parm.getDouble("STOCK_QTY"); d_qty =
				 * parm.getDouble("DOSAGE_QTY"); in_qty = StringTool.round(v_qty
				 * * s_qty * d_qty * (-1.00), 2);
				 * inparm1.setData("BUY_UNRECEIVE_QTY", in_qty); result =
				 * IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(inparm1,
				 * conn); if (result.getErrCode() < 0) { return result; }
				 */
			} else {

				inparm.setData("MATERIAL_LOC_CODE", parm
						.getValue("MATERIAL_LOC_CODE"));
				inparm.setData("MATERIAL_LOC_DESC", "");
				inparm.setData("ELETAG_CODE", "");
				inparm.setData("MATERIAL_LOC_SEQ", "");
				inparm.setData("SUP_CODE", parm.getValue("SUP_CODE"));
				inparm.setData("LOCK_QTY", 0);
				result = IndStockMTool.getInstance().onInsert(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** ���������������ϸ״̬ */
			result = SPCGenDrugPutUpTool.getInstance().updateINDverifyindSpc(
					parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			/** ���������������ϸ״̬ */
		}

		return result;
	}

	/**
	 * ָ������ҩƷ����ս�������ҵ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onIndMMStockBatch(TParm parm, TConnection conn) {
		// �����
		TParm result1 = new TParm();
		// ���ݼ��
		if (parm == null)
			return result1;
		TParm resutlParm = new TParm();
		// System.out.println("1----");
		// 2.ҩƷ����ս�������ҵ
		// 2.1��ѯ�տ�潻�׵�
		TParm result = IndStockDTool.getInstance().onQuery(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("2--result--"+result);
		// 2.2�ս�������ҵ
		String order_code = "";
		Timestamp date = parm.getTimestamp("TRANDATE");
		for (int i = 0; i < result.getCount("ORG_CODE"); i++) {
			// ��ǰ���
			double stock_qty = result.getDouble("STOCK_QTY", i);
			// ���տ��
			double last_stock_qty = result.getDouble("LAST_TOTSTOCK_QTY", i);
			// ������
			double in_qty = result.getDouble("IN_QTY", i);
			// ���ճ�
			double out_qty = result.getDouble("OUT_QTY", i);
			// �����̲�
			double modi_qty = result.getDouble("CHECKMODI_QTY", i);
			if (stock_qty == 0 && last_stock_qty == 0 && in_qty == 0
					&& out_qty == 0 && modi_qty == 0) {
				continue;
			}
			// ������ʹ���� = ���ճ��� - �̲����
			double UseQty = out_qty - modi_qty;
			// �����տ���� = ���տ�� + ������� - ���ճ��� + �̲����
			double TotQty = stock_qty;// last_stock_qty + in_qty - out_qty +
			// modi_qty;;;;update by liyh
			// stock_qty��ʵʱֵ�����ü���
			if (TotQty != (last_stock_qty + in_qty - out_qty + modi_qty)) {// ������
				// д��־
				// add
				// liyh
				// by
				// liudy
				System.out
						.println("-------STOCK_QTY != (last_stock_qty + in_qty - out_qty + modi_qty):::"
								+ "stock:"
								+ TotQty
								+ ",last_stock_qty:"
								+ last_stock_qty
								+ ",in_qty:"
								+ in_qty
								+ ",out_qty:"
								+ out_qty
								+ ",modi_qty:"
								+ modi_qty);
			}
			// ������֧�����
			double UseAmt = UseQty * result.getDouble("RETAIL_PRICE", i);
			// �����տ����
			double TotAmt = TotQty * result.getDouble("RETAIL_PRICE", i);
			// �������տ����
			double TotLastStockAmt = last_stock_qty
					* result.getDouble("RETAIL_PRICE", i);

			// �Ƿ��е��ۼƻ�
			// order_code = result.getValue("ORDER_CODE", i);
			// double adjustAmt = 0;
			// TParm afterPrice = new TParm(TJDODBTool.getInstance().select(
			// INDSQL.getOrderSysFeeHistoryPrice(order_code,
			// date.toString().
			// substring(0,
			// 10).replaceAll("-", ""))));
			// if (afterPrice.getCount("ORDER_CODE") > 0) {
			// TParm beforePrice = new TParm(TJDODBTool.getInstance().
			// select(INDSQL.
			// getOrderSysFeeHistoryPrice(order_code,
			// StringTool.rollDate(date, -1).
			// toString().substring(0, 10).
			// replaceAll("-", ""))));
			// if (beforePrice.getCount("ORDER_CODE") > 0) {
			// adjustAmt = (afterPrice.getDouble("OWN_PRICE", 0) -
			// beforePrice.getDouble("OWN_PRICE", 0)) *
			// last_stock_qty;
			// TotLastStockAmt = last_stock_qty *
			// beforePrice.getDouble("OWN_PRICE", 0);
			// }
			// }
			// System.out.println("3----");

			// д��ҩƷ����ս���ҵ
			TParm inparm = new TParm();
			// 1-TRANDATE
			inparm.setData("TRANDATE", parm.getValue("TRANDATE").substring(0,
					10).replaceAll("-", ""));
			// 2-ORG_CODE
			inparm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			// 3-ORDER_CODE
			inparm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
			// 4-BATCH_SEQ
			inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
			// 5-BATCH_NO
			inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
			// 6-VALID_DATE
			inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
			// 7-REGION_CODE
			inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
			// 8-STOCK_QTY
			inparm.setData("STOCK_QTY", TotQty);
			// 9-STOCK_AMT
			inparm.setData("STOCK_AMT", TotAmt);
			// 10-LAST_TOTSTOCK_QTY
			inparm.setData("LAST_TOTSTOCK_QTY", last_stock_qty);
			// 11-LAST_TOTSTOCK_AMT
			inparm.setData("LAST_TOTSTOCK_AMT", TotLastStockAmt);
			// 12-IN_QTY
			inparm.setData("IN_QTY", result.getDouble("IN_QTY", i));
			// 13-IN_AMT
			inparm.setData("IN_AMT", result.getDouble("IN_AMT", i));
			// 14-OUT_QTY
			inparm.setData("OUT_QTY", result.getDouble("OUT_QTY", i));
			// 15-OUT_AMT
			inparm.setData("OUT_AMT", result.getDouble("OUT_AMT", i));
			// 16-CHECKMODI_QTY
			inparm.setData("CHECKMODI_QTY", result
					.getDouble("CHECKMODI_QTY", i));
			// 17-CHECKMODI_AMT
			inparm.setData("CHECKMODI_AMT", result
					.getDouble("CHECKMODI_AMT", i));
			// 18-VERIFYIN_QTY
			inparm.setData("VERIFYIN_QTY", result.getDouble("VERIFYIN_QTY", i));
			// 19-VERIFYIN_AMT
			inparm.setData("VERIFYIN_AMT", result.getDouble("VERIFYIN_AMT", i));
			// 20-FAVOR_QTY
			inparm.setData("FAVOR_QTY", result.getDouble("FAVOR_QTY", i));
			// 21-REGRESSGOODS_QTY
			inparm.setData("REGRESSGOODS_QTY", result.getDouble(
					"REGRESSGOODS_QTY", i));
			// 22-REGRESSGOODS_AMT
			inparm.setData("REGRESSGOODS_AMT", result.getDouble(
					"REGRESSGOODS_AMT", i));
			// 23-DOSAGE_QTY
			inparm.setData("DOSAGE_QTY", result.getDouble("DOSEAGE_QTY", i));
			// 24-DOSAGE_AMT
			inparm.setData("DOSAGE_AMT", result.getDouble("DOSAGE_AMT", i));
			// 25-REGRESSDRUG_QTY
			inparm.setData("REGRESSDRUG_QTY", result.getDouble(
					"REGRESSDRUG_QTY", i));
			// 26-REGRESSDRUG_AMT
			inparm.setData("REGRESSDRUG_AMT", result.getDouble(
					"REGRESSDRUG_AMT", i));
			// 27-PROFIT_LOSS_AMT
			inparm.setData("PROFIT_LOSS_AMT", // adjustAmt +
					result.getDouble("PROFIT_LOSS_AMT", i));
			// 28-VERIFYIN_PRICE
			inparm.setData("VERIFYIN_PRICE", result.getDouble("VERIFYIN_PRICE",
					i));
			// 29-STOCK_PRICE
			inparm.setData("STOCK_PRICE", result.getDouble("STOCK_PRICE", i));
			// 30-RETAIL_PRICE
			inparm.setData("RETAIL_PRICE", result.getDouble("RETAIL_PRICE", i));
			// 31-TRADE_PRICE
			inparm.setData("TRADE_PRICE", result.getDouble("TRADE_PRICE", i));
			// 32-STOCKIN_QTY
			inparm.setData("STOCKIN_QTY", result.getDouble("STOCKIN_QTY", i));
			// 33-STOCKIN_AMT
			inparm.setData("STOCKIN_AMT", result.getDouble("STOCKIN_AMT", i));
			// 34-STOCKOUT_QTY
			inparm.setData("STOCKOUT_QTY", result.getDouble("STOCKOUT_QTY", i));
			// 35-STOCKOUT_AMT
			inparm.setData("STOCKOUT_AMT", result.getDouble("STOCKOUT_AMT", i));
			// 36-OPT_USER
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			// 37-OPT_DATE
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			// 38-OPT_TERM
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			// 39-REQUEST_IN_QTY
			inparm.setData("REQUEST_IN_QTY", result.getDouble("REQUEST_IN_QTY",
					i));
			// 40-REQUEST_IN_AMT
			inparm.setData("REQUEST_IN_AMT", result.getDouble("REQUEST_IN_AMT",
					i));
			// 41-REQUEST_OUT_QTY
			inparm.setData("REQUEST_OUT_QTY", result.getDouble(
					"REQUEST_OUT_QTY", i));
			// 42-REQUEST_OUT_AMT
			inparm.setData("REQUEST_OUT_AMT", result.getDouble(
					"REQUEST_OUT_AMT", i));
			// 43-GIF_IN_QTY
			inparm.setData("GIF_IN_QTY", result.getDouble("GIF_IN_QTY", i));
			// 44-GIF_IN_AMT
			inparm.setData("GIF_IN_AMT", result.getDouble("GIF_IN_AMT", i));
			// 45-GIF_OUT_QTY
			inparm.setData("GIF_OUT_QTY", result.getDouble("GIF_OUT_QTY", i));
			// 46-GIF_OUT_AMT
			inparm.setData("GIF_OUT_AMT", result.getDouble("GIF_OUT_AMT", i));
			// 47-RET_IN_QTY
			inparm.setData("RET_IN_QTY", result.getDouble("RET_IN_QTY", i));
			// 48-RET_IN_AMT
			inparm.setData("RET_IN_AMT", result.getDouble("RET_IN_AMT", i));
			// 49-RET_OUT_QTY
			inparm.setData("RET_OUT_QTY", result.getDouble("RET_OUT_QTY", i));
			// 50-RET_OUT_AMT
			inparm.setData("RET_OUT_AMT", result.getDouble("RET_OUT_AMT", i));
			// 51-WAS_OUT_QTY
			inparm.setData("WAS_OUT_QTY", result.getDouble("WAS_OUT_QTY", i));
			// 52-WAS_OUT_AMT
			inparm.setData("WAS_OUT_AMT", result.getDouble("WAS_OUT_AMT", i));
			// 53-THO_OUT_QTY
			inparm.setData("THO_OUT_QTY", result.getDouble("THO_OUT_QTY", i));
			// 54-THO_OUT_AMT
			inparm.setData("THO_OUT_AMT", result.getDouble("THO_OUT_AMT", i));
			// 55-THI_IN_QTY
			inparm.setData("THI_IN_QTY", result.getDouble("THI_IN_QTY", i));
			// 56-THI_IN_AMT
			inparm.setData("THI_IN_AMT", result.getDouble("THI_IN_AMT", i));
			// 57-COS_OUT_QTY
			inparm.setData("COS_OUT_QTY", result.getDouble("COS_OUT_QTY", i));
			// 58-COS_OUT_AMT
			inparm.setData("COS_OUT_AMT", result.getDouble("COS_OUT_AMT", i));
			// 59--SUP_CODE
			inparm.setData("SUP_CODE", result.getDouble("SUP_CODE", i));
			// System.out.println("inparm---" + inparm);
			// ɾ������ddstock �Ĺ��ܣ���rebatch ���β��� begin
			resutlParm = IndDDStockTool.getInstance().onInsert(inparm, conn);
			if (resutlParm.getErrCode() < 0) {
				return resutlParm;
			}
			// ɾ������ddstock �Ĺ��ܣ���rebatch ���β��� end
			// System.out.println("4----");

			// ��������¼����
			TParm srockParm = new TParm();
			srockParm.setData("ORG_CODE", parm.getData("ORG_CODE"));
			srockParm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
			srockParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
			srockParm.setData("STOCK_QTY", TotQty);
			srockParm.setData("LAST_TOTSTOCK_QTY", TotQty);
			srockParm.setData("LAST_TOTSTOCK_AMT", TotAmt);
			srockParm.setData("OPT_USER", parm.getData("OPT_USER"));
			srockParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			srockParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

			resutlParm = IndStockDTool.getInstance().onUpdateOutQtyToZero(
					srockParm, conn);
			conn.commit();
			if (resutlParm.getErrCode() < 0) {
				return resutlParm;
			}
			// System.out.println("5----");
		}
		// 3.���¸ù��˲��ŵ�IND_ORG.BATCH_FLGΪ"N"
		parm.setData("BATCH_FLG", "N");
		result = IndOrgTool.getInstance().onUpdateBatchFlg(parm, conn);
		conn.commit();
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("6----");
		return result;
	}

	/**
	 * ��ѯ�а�װ
	 * 
	 * @param orderCode
	 * @return
	 */
	public TParm getPackUnit(String orderCode) {
		String sql = "SELECT PACK_UNIT FROM PHA_BASE WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	public TParm getVerifyinPrice(String orgCode, String orderCode) {
		String sql = "select a.verifyin_price as price from ind_stock a where "
				+ "a.org_code='" + orgCode + "' and a.order_code='" + orderCode
				+ "' and a.batch_seq=(select max(batch_seq) from ind_stock b "
				+ "where b.org_code=a.org_code and b.order_code=a.order_code) ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ��װ����
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public TParm getDosageQty(String orgCode, String orderCode) {
		String sql = "SELECT DOSAGE_QTY FROM PHA_TRANSUNIT WHERE ORDER_CODE='"
				+ orderCode + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ��ѯҩƷ��Ϣ
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public TParm getPhaBaseInfo(String orderCode) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getPHABaseInfo(orderCode)));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �õ���ǰҩƷ���
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public String getCurrentStockQty(String orgCode, String orderCode) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				SPCSQL.getStockQty(orgCode, orderCode)));
		if (result.getErrCode() < 0) {
			return "0";
		}
		if (result.getCount() < 1) {
			return "0";
		}
		return result.getValue("QTY", 0);
	}

	/**
	 * ��ѯ��������������-����
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public TParm queryOrgCodeInIndAccout(String closeDate) {
		String sql = INDSQL.getOrgCodeInIndAccount(closeDate);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �Զ��������쵥������parm
	 * 
	 * @param toOrgCode
	 * @param requestNo
	 * @param appOrgCode
	 * @param optUser
	 * @param optTerm
	 * @param regionCode
	 * @param drugCategory
	 *            �龫��� 1����ҩ��
	 * @param applyType
	 *            ���뷽ʽ 1���ֶ�
	 * @return
	 */
	public TParm createRequestM(String toOrgCode, String requestNo,
			String appOrgCode, String optUser, String optTerm,
			String regionCode, String drugCategory, String applyType) {
		TParm result = new TParm();
		result.setData("REQUEST_NO", requestNo);
		result.setData("REQTYPE_CODE", "DEP");
		result.setData("TO_ORG_CODE", toOrgCode);
		result.setData("APP_ORG_CODE", appOrgCode);
		result.setData("REQUEST_USER", optUser);
		result.setData("REASON_CHN_DESC", "�Զ�");
		result.setData("UNIT_TYPE", "0");
		result.setData("URGENT_FLG", "N");
		result.setData("DRUG_CATEGORY", drugCategory);
		result.setData("APPLY_TYPE", applyType);
		result.setData("URGENT_FLG", "N");
		result.setData("OPT_USER", optUser);
		result.setData("OPT_TERM", optTerm);
		result.setData("REGION_CODE", regionCode);
		return result;
	}

	/**
	 * ��ѯ��������������
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public TParm queryIndAccout(String closeDate, String orgCode,
			String orderCode) {
		String sql = INDSQL.getIndAccount(closeDate, orgCode, orderCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ���ұ�ҩ���ɲ�ѯ-������ҩ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDeptFromOdiDspnm(TParm parm) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// �Ѿ�����
		if ("Y".equals(parm.getValue("REQUEST_FLG_A"))) {
			// ����
			resultM = IndRequestMTool.getInstance().onQueryExm(parm);
			if (resultM.getErrCode() < 0) {
				return resultM;
			}
			if (null != parm.getValue("REQUEST_NO")
					&& parm.getValue("REQUEST_NO").length() > 3) {
				// ��ϸ
				resultD = IndRequestMTool.getInstance()
						.queryOdiDspnmExmEd(parm);
				if (resultD.getErrCode() < 0) {
					return resultD;
				}
			}

		} else {// δ����
			// ����
			resultM = IndRequestMTool.getInstance().queryOdiDsnpmExmM(parm);
			// System.out.println("resultM---"+resultM);
			if (resultM.getErrCode() < 0) {
				return resultM;
			}
			// ��ϸ
			resultD = IndRequestMTool.getInstance().queryOdiDspnmExmD(parm);
			if (resultD.getErrCode() < 0) {
				return resultD;
			}
		}

		result.addData("RESULT_M", resultM.getData());
		result.addData("RESULT_D", resultD.getData());
		return result;
	}

	/**
	 * �ż����������쵥
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @author shendr
	 * @date 2013.7.23
	 * @return TParm
	 */
	public TParm onCreateDeptOPDRequestSpc(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// �������뵥����
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������쵥ϸ��
		TParm requestD = parm.getParm("REQUEST_D");
		String order_code = "";
		String startDate = "";
		String endDate = "";
		String execDeptCode = "";
		String requestNo = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm
					.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE",
							0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			requestNo = requestD.getData("REQUEST_NO", i) + "";
			startDate = requestD.getData("START_DATE", i) + "";
			endDate = requestD.getData("END_DATE", i) + "";
			execDeptCode = requestD.getData("EXEC_DEPT_CODE", i) + "";
			result = new TParm(TJDODBTool.getInstance()
					.update(
							INDSQL.updateOpdOrderForRequestByOrderCode(
									requestNo, startDate, endDate,
									execDeptCode, order_code), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * �������쵥
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onCreateDeptOdiRequestSpc(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();

		// �������뵥����
		TParm requestM = parm.getParm("REQUEST_M");
		// System.out.println("indtool--------------requestM"+requestM);
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("111111111111");

		// �������쵥ϸ��
		TParm requestD = parm.getParm("REQUEST_D");
		// System.out.println("indtool--------------requestD"+requestD);
		String order_code = "";
		String startDate = "";
		String endDate = "";
		String stationCode = "";
		String requestNo = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm
					.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE",
							0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			requestNo = requestD.getData("REQUEST_NO", i) + "";
			startDate = requestD.getData("START_DATE", i) + "";
			endDate = requestD.getData("END_DATE", i) + "";
			stationCode = requestD.getData("STATION_CODE", i) + "";
			String sql = INDSQL.updateOdiDspnmForRequestByOrderCode(requestNo,
					startDate, endDate, stationCode, order_code);
			// System.out.println("--updateOdiDspnmForRequestByOrderCode-----------sql:"+sql);
			result = new TParm(TJDODBTool.getInstance().update(
					INDSQL.updateOdiDspnmForRequestByOrderCode(requestNo,
							startDate, endDate, stationCode, order_code), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// System.out.println("222222222222222");

		/*
		 * // ����ODI_DSPNMס״̬ TParm update = parm.getParm("UPDATE");
		 * System.out.println("--------- ---update:"+update); //���²���-סԺ״̬ String
		 * caseNo = ""; String orderNo = ""; int orderSeq = 0; String requestNo
		 * = ""; for (int i = 0; i < update.getCount("CASE_NO"); i++) { caseNo =
		 * update.getValue("CASE_NO", i); orderNo = update.getValue("ORDER_NO",
		 * i); orderSeq = update.getInt("ORDER_SEQ", i); requestNo =
		 * update.getValue("REQUEST_NO", i);
		 * System.out.println("---------INDSQL.updateOdiDspnmForRequest:"
		 * +INDSQL.updateOdiDspnmForRequest(caseNo, orderNo, orderSeq,
		 * requestNo)); result = new
		 * TParm(TJDODBTool.getInstance().update(INDSQL
		 * .updateOdiDspnmForRequest(caseNo, orderNo, orderSeq, requestNo),
		 * conn)); if (result.getErrCode() < 0) { return result; } }
		 */
		// System.out.println("3333333333333333");

		return result;
	}

	/**
	 * ���������龫��ҩ�����������쵥
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @author shendr
	 * @date 2013.7.29
	 * @return TParm
	 */
	public TParm onCreateDeptOpiRequestSpc(TParm parm, TConnection conn) {
		// ���ݼ��
		if (parm == null)
			return null;
		// �����
		TParm result = new TParm();
		// �������뵥����
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// �������쵥ϸ��
		TParm requestD = parm.getParm("REQUEST_D");
		String order_code = "";
		String startDate = "";
		String endDate = "";
		String execDeptCode = "";
		String requestNo = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm
					.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE",
							0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			requestNo = requestD.getData("REQUEST_NO", i) + "";
			startDate = requestD.getData("START_DATE", i) + "";
			endDate = requestD.getData("END_DATE", i) + "";
			execDeptCode = requestD.getData("EXEC_DEPT_CODE", i) + "";
			result = new TParm(TJDODBTool.getInstance()
					.update(
							INDSQL.updateSpcInvRecordForRequestByOrderCode(
									requestNo, startDate, endDate,
									execDeptCode, order_code), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * ҩƷ����(���뵥����)�龫���⡶���ұ�ҩ��
	 * 
	 * @param parm
	 *            TParm
	 * @param org_code
	 *            String
	 * @param unit_type
	 *            String
	 * @param request_type
	 *            String
	 * @return TParm
	 */
	public TParm onIndDispenseOutDrug(TParm parm, String org_code,
			String request_type, TConnection conn) {
		String order_code = "";
		int seq = 1;
		double actual_qty = 0;
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// ʵ�ʳ�������
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);

			actual_qty = parm.getDouble("ACTUAL_QTY", i);

			// ����ҩ���ż�ҩƷ�����ѯҩƷ��������š��������ۼ�
			TParm stock_parm = IndStockDTool.getInstance()
					.onQueryStockBatchAndQty(org_code, order_code, "");
			for (int j = 0; j < stock_parm.getCount(); j++) {
				double qty = stock_parm.getDouble("QTY", j);
				int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
				if (qty >= actual_qty) {
					// ҩƷ����һ�οۿ�
					double out_amt = StringTool.round(stock_parm.getDouble(
							"STOCK_RETAIL_PRICE", j)
							* actual_qty, 2);
					// ���¿��(���뵥����)
					result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
							request_type, org_code, order_code, batch_seq,
							actual_qty, out_amt, parm.getValue("OPT_USER", i),
							parm.getTimestamp("OPT_DATE", i),
							parm.getValue("OPT_TERM", i), conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						return result;
					}
					seq++;
					break;
				} else {
					// ҩƷ����һ�οۿ�
					double out_amt = StringTool.round(stock_parm.getDouble(
							"STOCK_RETAIL_PRICE", j)
							* qty, 2);
					// ���¿��(���뵥����)
					result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
							request_type, org_code, order_code, batch_seq, qty,
							out_amt, parm.getValue("OPT_USER", i),
							parm.getTimestamp("OPT_DATE", i),
							parm.getValue("OPT_TERM", i), conn);
					if (result.getErrCode() < 0) {
						conn.rollback();
						return result;
					}

					seq++;
					// ���³�����
					actual_qty = actual_qty - qty;
					// ����ʵ�ʳ�������
					qty_out = qty_out + qty;
				}
			}

		}
		return result;
	}

	/**
	 * ��ѯҩ�������
	 * 
	 * @return
	 */
	public TParm onQueryINDSysParm() {
		TParm sysParm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		if (sysParm.getCount() > 0) {
			return sysParm.getRow(0);
		}
		return new TParm();

	}

	/**
	 * ��ѯҩ�������
	 * 
	 * @return
	 */
	public TParm getSupCode(TParm parm) {
		String supCode = parm.getValue("SUP_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String sql = "SELECT SUP_ORDER_CODE FROM IND_CODE_MAP WHERE SUP_CODE='"
				+ supCode + "' AND ORDER_CODE='" + orderCode + "'";
		TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sysParm.getErrCode() < 0) {
			return sysParm;
		}
		return sysParm;

	}

	/**
	 * 
	 * @param orderCode
	 *            ҩƷ����
	 * @param orgCode
	 *            �ۿⲿ��
	 * @param dosageQty
	 *            ��ǰ��ҩ����
	 * @param oldDosageQty
	 *            ǰһ����ҩ�������޸�ҽ��ʹ�ã�
	 * @param feeFlg
	 *            true :����/ flase:�޸�
	 * @return true �п��/false: û�п��
	 */
	public boolean inspectIndStockQty(String orderCode, String orgCode,
			double dosageQty, double oldDosageQty, boolean feeFlg) {
		// �����̿ۿ����
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("ORG_CODE", orgCode);
		if (!feeFlg) {// �޸�ҽ��У����
			// ����ȿ���10֧����12֧����������ټ�2���� ��������-2
			if (oldDosageQty > dosageQty) {
				dosageQty = oldDosageQty - dosageQty;
			} else {
				dosageQty = dosageQty - oldDosageQty;
			}
		} else {
			dosageQty = dosageQty - oldDosageQty;
		}
		parm.setData("DOSAGE_QTY", dosageQty);
		parm.setData("ACTIVE_FLG", "Y");
		TParm result = IndStockDTool.getInstance().onQueryLockQTY(parm);
		if (result.getErrCode() < 0) {
			return false;
		}
		if (result.getCount("QTY") <= 0) {
			return false;
		}
		double qty = result.getDouble("QTY", 0);
		// add by huangtt start 20150608
		TParm resultBase = getStockBaseQTY(parm);
		qty = qty - resultBase.getDouble("QTY", 0);
		// add by huangtt end 20150608
		if (qty < 0) {
			return false;
		}
		return true;
	}

	public TParm onQueryStockQtyBySkintest(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		double dosageQty = parm.getDouble("DOSAGE_QTY");
		String sql = " SELECT BATCH_NO FROM IND_STOCK "
				+ " WHERE ORG_CODE='"
				+ orgCode
				+ "' AND ORDER_CODE='"
				+ orderCode
				+ "' AND STOCK_QTY>="
				+ dosageQty
				+ "       AND SYSDATE<VALID_DATE  AND  ACTIVE_FLG='Y'  ORDER BY VALID_DATE  ";
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		return resultParm;
	}

	public boolean inspectIndStock(String orgCode, String orderCode,
			String batchNO, double dosageQty) {

		System.out.println("batchNO----------------" + batchNO);
		if (dosageQty < 0)
			return false;

		if (getIndStockQty(orgCode, orderCode, batchNO) >= dosageQty)
			return true;
		return false;

	}

	/**
	 * �������Ų�
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @param batchNo
	 * @return
	 */
	public double getIndStockQty(String orgCode, String orderCode,
			String batchNo) {
		String sql = " SELECT SUM(STOCK_QTY) AS QTY FROM IND_STOCK  "
				+ " WHERE ACTIVE_FLG='Y' AND SYSDATE < VALID_DATE  "
				+ "  AND ORG_CODE='" + orgCode + "' AND ORDER_CODE='"
				+ orderCode + "' " + "  AND BATCH_NO='" + batchNo + "' ";
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (resultParm.getErrCode() != -1) {
			return resultParm.getDouble("QTY", 0);
		}
		return 0;
	}

	/**
	 * batchNo
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getIndStockQtyByBatchNo(String orgCode, String orderCode,
			String batchNo, String sort) {

		String sql = "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE, A.STOCK_QTY AS QTY,"
				+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3,B.PHA_TYPE,  "
				+ " A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE,A.SUP_CODE,A.INVENT_PRICE, "
				+ " A.SUP_ORDER_CODE "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND A.ORG_CODE = '"
				+ orgCode
				+ "' AND A.ORDER_CODE = '"
				+ orderCode
				+ "' AND A.ACTIVE_FLG='Y' "
				+ " AND SYSDATE < A.VALID_DATE AND A.BATCH_NO='"
				+ batchNo
				+ "'  "
				+ " AND A.STOCK_QTY  > 0 "
				+ " ORDER BY A.VALID_DATE "
				+ sort + " , A.BATCH_SEQ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;

	}

	/**
	 * ��������ҩƷ���ķ�Ʊ�źͷ�Ʊ����
	 * 
	 * @param parm
	 * @return wangjc 20150528
	 */
	public TParm onUpdateInvoiceNo(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			String sql = "UPDATE IND_VERIFYIND SET INVOICE_NO='"
					// fux modify 201700605 id:5283
					+ parm.getValue("INVOICE_NO", i) + "',INVOICE_DATE= TO_DATE('"
					+ parm.getValue("INVOICE_DATE", i) + "','YYYY/MM/DD'),OPT_USER='"
					+ parm.getValue("OPT_USER", i)
					+ "',OPT_DATE=SYSDATE,OPT_TERM='"
					+ parm.getValue("OPT_TERM", i) + "' WHERE VERIFYIN_NO='"
					+ parm.getValue("VERIFYIN_NO", i) + "' AND ORDER_CODE='"
					+ parm.getValue("ORDER_CODE", i) + "' AND BATCH_NO='"
					+ parm.getValue("BATCH_NO", i)
					+ "' AND VALID_DATE=TO_DATE('"
					+ parm.getValue("VALID_DATE", i) + "','YYYY/MM/DD')";
//			System.out.println("sql>>>>>>>>>>>>>>>"+sql);
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0) {
				conn.rollback();
				return result;
			}
		}
		return result;
	}

	/**
	 * ��������ҩƷ���ķ�Ʊ�źͷ�Ʊ����
	 * 
	 * @param parm
	 * @return wangjc 20150528
	 */
	public TParm onUpdateRegoodsInvoiceNo(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			String sql = "UPDATE IND_REGRESSGOODSD SET INVOICE_NO='"
					// fux modify 201700605 id:5283
					+ parm.getValue("INVOICE_NO", i) + "',INVOICE_DATE=TO_DATE('"
					+ parm.getValue("INVOICE_DATE", i) + "','YYYY/MM/DD'),OPT_USER='"
					+ parm.getValue("OPT_USER", i)
					+ "',OPT_DATE=SYSDATE,OPT_TERM='"
					+ parm.getValue("OPT_TERM", i)
					+ "' WHERE REGRESSGOODS_NO='"
					+ parm.getValue("REGRESSGOODS_NO", i)
					+ "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE", i)
					+ "' AND BATCH_NO='" + parm.getValue("BATCH_NO", i)
					+ "' AND VALID_DATE=TO_DATE('"
					+ parm.getValue("VALID_DATE", i) + "','YYYY/MM/DD')";
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0) {
				conn.rollback();
				return result;
			}
		}
		return result;
	}
}
