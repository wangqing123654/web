package jdo.ind;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jdo.ibs.IBSOrderdTool;
import jdo.opd.OrderTool;
import jdo.pha.PhaBaseTool;
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
 * Title: 药库管理系统内部公用方法及对外接口方法
 * </p>
 * 
 * <p>
 * Description:药库管理系统内部公用方法及对外接口方法
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
	 * 实例
	 */
	public static INDTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static INDTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INDTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INDTool() {
		onInit();
	}

	/**
	 * 查询采购计划主档及订购单的生成
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryPurPlan(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 查询数据
		TParm result = IndPurPlanMTool.getInstance().onQuery(parm);
		return result;
	}

	/**
	 * 采购计划引用查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onQueryExcerptByOrder(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 采购计划引用查询
		parm.setData("CHECK_SAFE", "Y");
		//System.out.println("parm:::"+parm);
		String con = "";
		String con1 = "";
		if(parm.getValue("ORG_CODE")!=null&&!"".equals(parm.getValue("ORG_CODE"))){
			con = con + " AND A.ORG_CODE = '"+parm.getValue("ORG_CODE")+"'";
			con1= con1+ " AND ORG_CODE = '"+parm.getValue("ORG_CODE")+"' ";
		}
		if(parm.getValue("ORDER_CODE")!=null&&!"".equals(parm.getValue("ORDER_CODE"))){
			con = con + " AND A.ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"'";
			con1= con1+ " AND ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"' ";
		}
		if(parm.getValue("TYPE_CODE")!=null&&!"".equals(parm.getValue("TYPE_CODE"))){
			con = con + " AND C.TYPE_CODE = '"+parm.getValue("TYPE_CODE")+"'";
		}
		if(parm.getValue("MATERIAL_LOC_CODE")!=null&&!"".equals(parm.getValue("MATERIAL_LOC_CODE"))){
			con = con + " AND A.MATERIAL_LOC_CODE = '"+parm.getValue("MATERIAL_LOC_CODE")+"'";
		}
		if(parm.getValue("SUP_CODE")!=null&&!"".equals(parm.getValue("SUP_CODE"))){
			con = con + " AND A.SUP_CODE = '"+parm.getValue("SUP_CODE")+"'";
		}
		
		//E_QTY = A.ECONOMICBUY_QTY - STOCK_QTY
		String sql = " SELECT K.SELECT_FLG,K.ORDER_CODE,K.PURCH_UNIT,K.BUY_QTY,K.SELL_QTY, "+
		" K.MAIN_QTY,K.MIDD_QTY,K.STOCK_QTY,K.PLAN_QTY,K.CONTRACT_PRICE,K.TOT_MONEY, "+
		" K.SUP_CODE,K.SAFE_QTY,K.MAX_QTY,K.BUY_UNRECEIVE_QTY,K.ORDER_DESC,K.SPECIFICATION,K.MAN_CODE  FROM  "+
			     "(SELECT   'N' AS SELECT_FLG, A.ORDER_CODE, C.PURCH_UNIT, "+
				 "0 AS BUY_QTY,  "+
				 "0 AS SELL_QTY, "+  
				 "0 AS MAIN_QTY, "+
				 "0 AS MIDD_QTY, "+
				 "SUM (B.STOCK_QTY) / D.DOSAGE_QTY / D.STOCK_QTY AS STOCK_QTY,  "+
				 "(A.ECONOMICBUY_QTY-SUM (B.STOCK_QTY)/ D.DOSAGE_QTY / D.STOCK_QTY) AS PLAN_QTY,  "+
				 "E.CONTRACT_PRICE,  "+ 
				 "(A.ECONOMICBUY_QTY-SUM (B.STOCK_QTY) / D.DOSAGE_QTY / D.STOCK_QTY)  "+ 
				 "* E.CONTRACT_PRICE AS TOT_MONEY,  "+
				 "E.SUP_CODE, A.SAFE_QTY AS SAFE_QTY,  "+  
				 "A.MAX_QTY  AS MAX_QTY,  "+
				 "A.BUY_UNRECEIVE_QTY "+
				 "AS BUY_UNRECEIVE_QTY,  "+
				 "CASE WHEN F.GOODS_DESC IS NULL THEN C.ORDER_DESC ELSE C.ORDER_DESC || '(' || F.GOODS_DESC || ')' END AS ORDER_DESC,  "+
				 "F.SPECIFICATION, F.MAN_CODE  "+
			     " FROM IND_STOCKM A,(SELECT ORDER_CODE,ORG_CODE,SUM(STOCK_QTY)AS STOCK_QTY,ACTIVE_FLG FROM  IND_STOCK" +
			     " WHERE 1=1 AND ACTIVE_FLG = 'Y' " +
			     con1 +
			     " GROUP BY ORDER_CODE,ORG_CODE,ACTIVE_FLG) B, PHA_BASE C, PHA_TRANSUNIT D, IND_AGENT E, SYS_FEE F  "+
			     "WHERE A.ORG_CODE = B.ORG_CODE  "+
			     "AND A.ORDER_CODE = B.ORDER_CODE  "+
			     "AND A.ACTIVE_FLG = 'N'  "+
			     "AND B.ACTIVE_FLG = 'Y'  "+
			     "AND A.ORDER_CODE = C.ORDER_CODE  "+
			     "AND A.ORDER_CODE = D.ORDER_CODE "+
			     "AND A.SUP_CODE= E.SUP_CODE  "+
			     "AND A.ORDER_CODE = E.ORDER_CODE  "+
			     "AND A.ORDER_CODE = F.ORDER_CODE  "+
			     con +
			     "GROUP BY A.ORDER_CODE,  "+
				 "C.PURCH_UNIT,  "+
				 "D.DOSAGE_QTY,  "+
				 "D.STOCK_QTY,  "+
				 "A.ECONOMICBUY_QTY,  "+
				 "E.CONTRACT_PRICE,  "+
				 "E.SUP_CODE,  "+
				 "A.SAFE_QTY,  "+
				 "A.MAX_QTY, "+
				 "A.BUY_UNRECEIVE_QTY,  "+
				 "C.ORDER_DESC,  "+
				 "F.SPECIFICATION,  "+
				 "F.MAN_CODE,  "+
				 "F.GOODS_DESC) K WHERE K.PLAN_QTY >0 ";
		//System.out.println("sql::::::::::"+sql);
//		TParm result = IndPurPlanMTool.getInstance()
//				.onQueryExcerptByOrder(parm);
		TParm result =  new TParm(TJDODBTool.getInstance().select(sql));
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
			// 查询上期采购量
			buy = IndPurPlanMTool.getInstance().onQueryBuyQty(order);
			// System.out.println("buy---"+buy);
			if (buy != null && !"".equals(buy)) {
				result.setData("BUY_QTY", i, buy.getDouble("BUY_QTY", 0));
			} else {
				result.setData("BUY_QTY", i, 0);
			}
			// 查询上期销售量
			sell = IndPurPlanMTool.getInstance().onQuerySellQty(order);
//			 System.out.println("sell---"+sell);
			if (sell != null && !"".equals(sell)) {
				result.setData("SELL_QTY", i, sell.getDouble("SELL_QTY", 0));
			} else {
				result.setData("SELL_QTY", i, 0);
			}
			// 查询主库库存
			main = IndPurPlanMTool.getInstance().onQueryMainQty(order);
			if (main != null && !"".equals(main)) {
				result.setData("MAIN_QTY", i, main.getDouble("MAIN_QTY", 0));
			} else {
				result.setData("MAIN_QTY", i, 0);
			}
			// System.out.println("main---"+main);
			// 查询中库库存
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
	 * 查询采购计划生成订购单明细
	 * 
	 * @return
	 */
	public TParm onQueryCreatePurPlanD(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 查询采购计划生成订购单明细
		TParm result = IndPurPlanDTool.getInstance().onQueryCreate(parm);
		String order_code = "";
		String org_code = "";
		for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
			double purorder_qty = 0;
			double stock_qty = 0;
			double create_qty = 0;
			order_code = result.getValue("ORDER_CODE", i);
			org_code = parm.getValue("ORG_CODE");
			// 库存量
			stock_qty = getStockQTY(org_code, order_code);
			if (stock_qty < 0) {
				stock_qty = 0;
			}
			result.setData("QTY", i, stock_qty);
			// 累计生成量
			parm.setData("ORDER_CODE", order_code);
			purorder_qty = IndPurorderDTool.getInstance().onQueryPurOrderQTY(
					parm).getDouble("PURORDER_QTY", 0);
			if (purorder_qty < 0) {
				purorder_qty = 0;
			}
			result.setData("PURORDER_QTY", i, purorder_qty);
			// System.out.println("purorder_qty---" + purorder_qty);
			// 本次生成量
			create_qty = result.getDouble("PLAN_QTY", i) - purorder_qty;
			result.setData("CREATE_QTY", i, create_qty);
			// System.out.println("create_qty---" + create_qty);
			// 生成金额
			result.setData("ATM", i, StringTool.round(result.getDouble(
					"CONTRACT_PRICE", i)
					* create_qty, 2));
		}
		return result;
	}

	/**
	 * 采购计划生成订购单
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onCreatePurOrder(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		TParm parmM = parm.getParm("PUR_M");
		TParm parmD = parm.getParm("PUR_D");
		Map sup = new HashMap();
		Map pur_no = new HashMap();
		// 供应厂商
		String sup_code = "";
		// 订购单号
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
		// 返回结果集
		TParm result = new TParm();
		// 订购单主项
		TParm inparmM = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		// 新增订购单主项
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
		// 订购单细项
		TParm inparmD = new TParm();
		// 新增订购单细项
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

		// 更新采购计划状态
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
	 * 新增验收单
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertVerify(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 供应厂商
		String sup_code = parm.getValue("SUP_CODE");
		// 验收科室
		String org_code = parm.getValue("ORG_CODE");
		// 结果集
		TParm result = new TParm();

		/** 新增验收单主项 */
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

		/** 新增验收单细项,审核入库更新验收明细状态 */
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

		/** 购入单价自动维护 */
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// System.out.println("---购入单价自动维护----------"+parm);
		/**************************** by liyh 20120906 自动维护供应商代理货物信息 start ********************************/
		if (null != parm && parm.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				String orderCodeS = parm.getValue("ORDER_CODE", i);
				String purOrderNoS = parm.getValue("PURORDER_NO", i);
				String sql = INDSQL.getPurOrderDByNoAndOrder(purOrderNoS,
						orderCodeS);
				// System.out.println("---------sql :"+sql);
				TParm purTParm = new TParm(TJDODBTool.getInstance().select(sql));
				// 订购时间
				String purDate = "";
				// 订购单价
				double purPrice = 0;
				// 订购数量
				double purQty = 0;
				if (null != purTParm && purTParm.getCount() > 0) {
					purDate = purTParm.getValue("OPT_DATE", 0);
					purPrice = purTParm.getDouble("PURORDER_PRICE", 0);
					purQty = purTParm.getDouble("PURORDER_QTY", 0);
				} else {
					purDate = SystemTool.getInstance().getDate().toString();

				}
				if (null != purDate && purDate.length() > 19) {
					purDate = purDate.substring(0, 19);
				}
				inparm.setData("LAST_ORDER_DATE", purDate);
				inparm.setData("LAST_ORDER_QTY", purQty);
				inparm.setData("LAST_ORDER_PRICE", purPrice
						* parm.getDouble("DOSAGE_QTY", i)
						* parm.getDouble("STOCK_QTY", i));
				// 最新价格 改为 配药价格
				inparm.setData("LAST_VERIFY_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
				String lastOrderNo = parm.getValue("PURORDER_NO", i);
				lastOrderNo = lastOrderNo == null ? "-1" : lastOrderNo;
				inparm
						.setData("LAST_ORDER_NO", parm.getValue("PURORDER_NO",
								i));
				// 合同价格 为库存价格
				inparm.setData("CONTRACT_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i)
						* parm.getDouble("DOSAGE_QTY", i)
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
				/**************************** by liyh 20120906 自动维护供应商代理货物信息 end ********************************/
				result = new TParm(TJDODBTool.getInstance().update(updateSql,
						conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		// 审核标记
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** 依照所对应入库部门、入库数量及效期批号进行库存新增或更新 */
			String ui_flg = "I";
			// 验收入库总量(配药单位)
			double in_qty = 0;
			// 验收数量(进货单位)
			double v_qty = 0;
			// 赠与数量(进货单位)
			double g_qty = 0;
			// 进货转换率
			double s_qty = 0;
			// 库存转换率
			double d_qty = 0;
			// 零售价格
			double retail = 0.0000;
			// 验收价格
			double verifyin = 0.0000;
			// 入库金额((验收入库量+ 赠与量) * 零售价)
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
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				inparm.setData("IN_QTY", in_qty);
				retail = parm.getDouble("RETAIL_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
				inparm.setData("IN_AMT", in_amt);
				inparm.setData("STOCK_QTY", in_qty);
				inparm.setData("VERIFYIN_QTY", in_qty);
				inparm.setData("VERIFYIN_AMT", in_amt);
				inparm.setData("VERIFYIN_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
				inparm.setData("FAVOR_QTY", StringTool.round(g_qty * s_qty
						* d_qty, 2));
				if ("U".equals(ui_flg)) {
					// 更新指定库存量
					result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
							conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				} else {
					// 新增库存量
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
					result = IndStockDTool.getInstance().onInsert(inparm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}

			/** 累计验收数、累计品质扣款更新 */
			// 累计验收数
			double actual = 0;
			// 计划订购量
			double purorder_qty = 0;
			// 累计品质扣款
			double deduct_atm = 0;
			for (int i = 0; i < parm.getCount("PURORDER_NO"); i++) {
				TParm inparm = new TParm();
				// 订购单号
				inparm.setData("PURORDER_NO", parm.getValue("PURORDER_NO", i));
				// 订购单序号
				inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i));
				// 验收入库量
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				inparm.setData("VERIFYIN_QTY", v_qty);
				// 累计质量扣款金额
				deduct_atm = parm.getDouble("QUALITY_DEDUCT_AMT", i);
				inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
				// 累计验收数
				actual = parm.getDouble("ACTUAL_QTY", i);
				// 计划订购量
				purorder_qty = parm.getDouble("PURORDER_QTY", i);
				// 状态
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

			/** 移动加权平均成本 */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				// zhangyong20091015 修改为验收单价
				verifyin = parm.getDouble("VERIFYIN_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * s_qty * d_qty
						* verifyin, 2);
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				double stock_price = getPhaBaseStockPrice(parm.getValue(
						"ORDER_CODE", i), in_amt, in_qty);
				// System.out.println("移动加权平均成本:" + stock_price);
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

			/** 批发价更新 */
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

			/** 在途量更新 */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				inparm.setData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
				// 验收量
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round(v_qty * s_qty * (-1.00), 2);
				inparm.setData("BUY_UNRECEIVE_QTY", in_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}

		/** 计划单号更新 */

		return result;
	}

	/**
	 * 更新验收单
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateVerify(TParm parm, TConnection conn) {
		// System.out.println("VER_PARM: " + parm);
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 验收科室
		String org_code = parm.getValue("ORG_CODE");
		// 更新主项信息
		result = IndVerifyinMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 判断审核状态
		// 审核标记
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** 依照所对应入库部门、入库数量及效期批号进行库存新增或更新 */
			String ui_flg = "I";
			// 验收入库总量(配药单位)
			double in_qty = 0;
			// 验收数量(进货单位)
			double v_qty = 0;
			// 赠与数量(进货单位)
			double g_qty = 0;
			// 进货转换率
			double s_qty = 0;
			// 库存转换率
			double d_qty = 0;
			// 零售价格
			double retail = 0.00;
			// 验收价格
			double verifyin = 0.0000;
			// 入库金额((验收入库量+ 赠与量) * 零售价)
			double in_amt = 0.00;
			String order_code = "";
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
				// zhangyong20091015 去掉d_qty
				in_amt = StringTool.round((v_qty + g_qty) * retail, 2);
				inparm.setData("IN_AMT", in_amt);
				inparm.setData("STOCK_QTY", in_qty);
				inparm.setData("VERIFYIN_QTY", in_qty);
				inparm.setData("VERIFYIN_AMT", in_amt);
				inparm.setData("VERIFYIN_PRICE", parm.getDouble(
						"VERIFYIN_PRICE", i));
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
					// 更新指定库存量
					result = IndStockDTool.getInstance().onUpdateQtyVer(inparm,
							conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				} else {
					// 新增库存量
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
					result = IndStockDTool.getInstance().onInsert(inparm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}

			/** 累计验收数、累计品质扣款更新 */
			// 累计验收数
			double actual = 0;
			// 计划订购量
			double purorder_qty = 0;
			// 累计品质扣款
			double deduct_atm = 0;
			for (int i = 0; i < parm.getCount("PURORDER_NO"); i++) {
				TParm inparm = new TParm();
				// 订购单号
				inparm.setData("PURORDER_NO", parm.getValue("PURORDER_NO", i));
				// 订购单序号
				inparm.setData("SEQ_NO", parm.getValue("PURSEQ_NO", i));
				// 验收入库量
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				inparm.setData("VERIFYIN_QTY", v_qty);
				// 累计质量扣款金额
				deduct_atm = parm.getDouble("QUALITY_DEDUCT_AMT", i);
				inparm.setData("QUALITY_DEDUCT_AMT", deduct_atm);
				// 累计验收数
				actual = parm.getDouble("ACTUAL_QTY", i);
				// 计划订购量
				purorder_qty = parm.getDouble("PURORDER_QTY", i);
				// 状态
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

			/** 移动加权平均成本 */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				g_qty = parm.getDouble("GIFT_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				// zhangyong20091015 修改为验收单价
				verifyin = parm.getDouble("VERIFYIN_PRICE", i);
				in_amt = StringTool.round((v_qty + g_qty) * s_qty * d_qty
						* verifyin, 2);
				in_qty = StringTool.round((v_qty + g_qty) * s_qty * d_qty, 2);
				double stock_price = getPhaBaseStockPrice(parm.getValue(
						"ORDER_CODE", i), in_amt, in_qty);
				// System.out.println("移动加权平均成本:" + stock_price);
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

			/** 批发价更新 */
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

			/** 在途量更新 */
			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				TParm inparm = new TParm();
				inparm.setData("ORG_CODE", org_code);
				order_code = parm.getValue("ORDER_CODE", i);
				inparm.setData("ORDER_CODE", order_code);
				// 验收量
				v_qty = parm.getDouble("VERIFYIN_QTY", i);
				s_qty = parm.getDouble("STOCK_QTY", i);
				d_qty = parm.getDouble("DOSAGE_QTY", i);
				in_qty = StringTool.round(v_qty * s_qty * (-1.00), 2);
				inparm.setData("BUY_UNRECEIVE_QTY", in_qty);
				result = IndStockMTool.getInstance().onUpdateBuyUnreceiveQty(
						inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 审核入库更新验收明细状态 */
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
	 * 新增退货主项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertRegressgoodsM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		/** 新增退货单主项 */
		result = IndRegressgoodsMTool.getInstance().onInsert(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 新增退货细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertRegressgoodsD(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		/** 新增退货单细项,审核出库更新退货明细状态 */
		Object object = parm.getData("UPDATE_SQL");
		if (object == null) {
			return null;
		}
		String[] updateSQL = (String[]) object;
		for (int i = 0; i < updateSQL.length - 1; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSQL[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		return result;
	}

	/**
	 * 新增退货单
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertRegressgoods(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 供应厂商
		String sup_code = parm.getValue("SUP_CODE");
		// 退货科室
		String org_code = parm.getValue("ORG_CODE");

		/** 新增退货单主项 */
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

		/** 新增退货单细项,审核出库更新退货明细状态 */
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

		// 审核标记
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** 依照所对应出库部门、出库数量及效期批号进行库存更新 */
			// 退货出库总量(配药单位)
			double out_qty = 0;
			// 退货数量(进货单位)
			double v_qty = 0;
			// 出货转换率
			double s_qty = 0;
			// 库存转换率
			double d_qty = 0;
			// 零售价格
			double retail = 0.00;
			// 出库金额(退货出库量 * 零售价)
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
				// 更新指定库存量
				result = IndStockDTool.getInstance().onUpdateQtyReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 累计验收数、累计品质扣款更新 */
			for (int i = 0; i < parm.getCount("VERIFYIN_NO"); i++) {
				TParm inparm = new TParm();
				// 验收单号
				inparm.setData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO", i));
				// 验收单序号
				inparm.setData("SEQ_NO", parm.getValue("VERSEQ_NO", i));
				// 退货出库量
				v_qty = parm.getDouble("QTY", i);
				inparm.setData("QTY", v_qty);
				// OPT_USER,OPT_DATE,OPT_TERM
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				// 累计退货数更新
				result = IndVerifyinDTool.getInstance().onUpdateReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 更新退货单
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateRegressgoods(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 验收科室
		String org_code = parm.getValue("ORG_CODE");
		// 更新主项信息
		result = IndRegressgoodsMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		// 审核标记
		String check = parm.getValue("CHECK_USER");
		if (!"".equals(check)) {
			/** 依照所对应出库部门、出库数量及效期批号进行库存更新 */
			// 退货出库总量(配药单位)
			double out_qty = 0;
			// 退货数量(进货单位)
			double v_qty = 0;
			// 出货转换率
			double s_qty = 0;
			// 库存转换率
			double d_qty = 0;
			// 零售价格
			double retail = 0.00;
			// 出库金额(退货出库量 * 零售价)
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
				// 更新指定库存量
				result = IndStockDTool.getInstance().onUpdateQtyReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 累计验收数、累计品质扣款更新 */
			for (int i = 0; i < parm.getCount("VERIFYIN_NO"); i++) {
				TParm inparm = new TParm();
				// 验收单号
				inparm.setData("VERIFYIN_NO", parm.getValue("VERIFYIN_NO", i));
				// 验收单序号
				inparm.setData("SEQ_NO", parm.getValue("VERSEQ_NO", i));
				// 退货出库量
				v_qty = parm.getDouble("QTY", i);
				inparm.setData("QTY", v_qty);
				// OPT_USER,OPT_DATE,OPT_TERM
				inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE"));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
				// 累计退货数更新
				result = IndVerifyinDTool.getInstance().onUpdateReg(inparm,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}

			/** 审核出库更新退货明细状态 */
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
	 * 根据药库编号及药品代码查询药库库存量
	 * 
	 * @param org_code
	 *            药库编号
	 * @param order_code
	 *            药品代码
	 * @return QTY 库存量
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
		return result.getDouble("QTY", 0);
	}

	/**
	 * 根据药库编号,药品代码,批号,效期查询药库库存量. <br>
	 * 当有org_code时,添加region_code无影响,为了匹配Control层调用,这里传入的region_code没有使用
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
	 * 根据药库编号及药品代码查询药库库存量
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
	 * 根据用量判断库存
	 * 
	 * @param org_code
	 *            药房代码
	 * @param order_code
	 *            药品代码
	 * @param dosage_qty
	 *            用量
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
	 * 校验库存，爱育华锁库存方法
	 * 查询IND_STOCK表中当前库存，判断是否可以扣库操作
	 * ===pangben 2013-11-4
	 * @param orderCode 当前操作医嘱代码
	 * @param dosageQty  操作的数量
	 * @param feeFlg  true: 扣库   false: 退库
	 * @return
	 */
	public boolean inspectIndStock(String caseNo,String orderCode,String rxNo,String seqNo,
			String orgCode,double dosageQty,double oldDosageQty,
			boolean feeFlg){
		// 正流程扣库操作
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("ORG_CODE", orgCode);
		if (!feeFlg) {//修改医嘱校验库存
			parm.setData("EXEC_DEPT_CODE", orgCode);
			parm.setData("CAT1_TYPE", "PHA");
			parm.setData("CASE_NO", caseNo);
			parm.setData("RX_NO", rxNo);
			TParm orderParm=OrderTool.getInstance().selectLockQtyCheckSumQty(parm);
			if (orderParm.getCount("QTY")>0) {//数据库存在数据，扣除库存=当前操作的数量-数据库里的数量
				dosageQty=dosageQty-orderParm.getDouble("QTY",0);
			}
		}else{//新增数据直接扣除库存
			dosageQty=dosageQty-oldDosageQty;
		}
		TParm result = IndStockDTool.getInstance().onQueryLockQTY(parm);
		if (result.getErrCode() < 0) {
			return false;
		}
		if (result.getCount("QTY") <= 0) {
			return false;
		}
		if (result.getDouble("QTY", 0)-dosageQty<0) {// 当前锁库存量小于使用库存量
			return false;
		}
		return true;
	}
	/**
	 * <药房>配药扣库
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
	 *            String 服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm reduceIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		// ***********************************************************************
		// luhai 2012-1-26 modify 将传回的orderList中加入batchSeq和orderCode 信息 begin
		// ***********************************************************************
		// TParm result = new TParm();
		// TParm resultParm = new TParm();
		// // 根据药库编号及药品代码查询药品的批次序号、库存和零售价
		// TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
		// org_code, order_code, "");
		// //System.out.println("parm-----"+parm);
		// if (parm.getErrCode() < 0) {
		// return parm;
		// }
		// // 库存量
		// double qty = 0;
		// // 批次序号
		// int batch_seq = 0;
		// // 出库金额
		// double out_amt = 0;
		// // 成本金额
		// double cost_amt = 0;
		// // 根据服务等级选择对应的零售金额列
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
		// //成本金额
		// cost_amt = 0;
		// for (int i = 0; i < parm.getCount(); i++) {
		// qty = parm.getDouble("QTY", i);
		// //System.out.println("qty-------------"+qty);
		// batch_seq = parm.getInt("BATCH_SEQ", i);
		// if (qty >= dosage_qty) {
		// // 药品可以一次扣库
		// out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
		// * dosage_qty, 2);
		// //计算成本金额
		// cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;
		//
		// // 更新库存(扣库)
		// resultParm = IndStockDTool.getInstance().onUpdateQtyOut(org_code,
		// order_code, batch_seq, dosage_qty, out_amt, opt_user,
		// opt_date, opt_term, conn);
		//
		// //回传成本金额
		// result.addData("ORDER_CODE", order_code);
		// result.addData("ORG_CODE", org_code);
		// result.addData("QTY", dosage_qty);
		// result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
		// result.setData("COST_AMT", StringTool.round(cost_amt, 2));
		// return result;
		// }
		// else {
		// // 药品不可一次扣库
		// out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
		// * qty, 2);
		// //计算成本金额
		// cost_amt +=parm.getDouble("VERIFYIN_PRICE", i) * qty;
		//
		// // 更新库存(扣库)
		// resultParm = IndStockDTool.getInstance().onUpdateQtyOut(org_code,
		// order_code, batch_seq, qty, out_amt, opt_user, opt_date,
		// opt_term, conn);
		// // 更新扣库量
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
		// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
		TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
				org_code, order_code, "");
		// System.out.println("parm-----"+parm);
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// 库存量
		double qty = 0;
		// 批次序号
		int batch_seq = 0;
		// 出库金额
		double out_amt = 0;
		// 成本金额
		double cost_amt = 0;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}
		// System.out.println("DOSAGE_QTY-------------"+dosage_qty);
		// 成本金额
		cost_amt = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			// System.out.println("qty-------------"+qty);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// 药品可以一次扣库
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// 计算成本金额
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;

				// 更新库存(扣库)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);

				// 回传成本金额
				result.addData("ORDER_CODE", order_code);
				result.addData("ORG_CODE", org_code);
				result.addData("QTY", dosage_qty);
				result.addData("PRICE", parm.getDouble("VERIFYIN_PRICE", i));
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				// add batchSeq
				result.addData("BATCH_SEQ", parm.getValue("BATCH_SEQ", i));
				return result;
			} else {
				// 药品不可一次扣库
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// 计算成本金额
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;

				// 更新库存(扣库)
				resultParm = IndStockDTool.getInstance().onUpdateQtyOut(
						org_code, order_code, batch_seq, qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// 更新扣库量
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
		// luhai 2012-1-26 modify 将传回的orderList中加入batchSeq和orderCode 信息 begin
		// ***********************************************************************
	}

	/**
	 * <药房>配药扣库(入口)
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
	 * <药房>取消配药入库
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
	 *            服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm unReduceIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		TParm result = new TParm();
		// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
		TParm parm = IndStockDTool.getInstance().onQueryStockQty(org_code,
				order_code, "DESC");

		if (parm.getErrCode() != 0) {
			return parm;
		}
		// 批次序号
		int batch_seq = parm.getInt("BATCH_SEQ", 0);
		// 入库数量
		double in_qty = dosage_qty;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// 入库金额
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// 更新库存(取消配药)
		result = IndStockDTool.getInstance().onUpdateQtyIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}

	/**
	 * <药房>取消配药入库(入口)
	 * 
	 * @param parm
	 * @param service_level
	 *            服务等级
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
	 * <药房>退药入库
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
	 *            服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm regressIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		// 更新库存
		TParm result = new TParm();
		// 根据药库编号及药品代码查询药品的批次序号、库存和零售价 by liyh 2010806
		TParm parm = IndStockDTool.getInstance().onQueryStockQty(org_code,
					order_code, "ASC");
		
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// 批次序号
		int batch_seq = parm.getInt("BATCH_SEQ", 0);
		// 入库数量
		double in_qty = dosage_qty;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// 入库金额
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// 更新库存(退药)
		result = IndStockDTool.getInstance().onUpdateQtyRegIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}
	
	/**
	 * <药房>退药入库
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
	 *            服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm regressIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level,int batchSeq ,TConnection conn) {
		// 更新库存
		TParm result = new TParm();
		// 根据药库编号及药品代码查询药品的批次序号、库存和零售价 by liyh 2010806
		TParm parm = IndStockDTool.getInstance().onQueryStockQty(org_code,
					order_code,batchSeq , "ASC"); 
		
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// 批次序号
		int batch_seq = batchSeq;
		// 入库数量
		double in_qty = dosage_qty;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";  
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {  
			OWN_PRICE = "RETAIL_PRICE";  
		}

		// 入库金额
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// 更新库存(退药)
		result = IndStockDTool.getInstance().onUpdateQtyRegIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}

	/**
	 * <药房>退药根据SEQ入库 luhai 2012-1-30 add 根据BATCH_SEQ 进行退库操作
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
	 *            服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm regressIndStockWithBatchSeq(String org_code,
			String order_code, int batchSeq, double dosage_qty,
			String opt_user, Timestamp opt_date, String opt_term,
			String service_level, TConnection conn) {
		// 批次序号
		int batch_seq = batchSeq;
		TParm parm = IndStockDTool.getInstance().onQueryStockWithBatchSeq(
				org_code, order_code, batchSeq);
		// 入库数量
		double in_qty = dosage_qty;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// 入库金额
		double in_amt = StringTool.round(parm.getDouble(OWN_PRICE, 0) * in_qty,
				2);
		// 更新库存(退药)
		TParm result = IndStockDTool.getInstance().onUpdateQtyRegIn(org_code,
				order_code, batch_seq, in_qty, in_amt, opt_user, opt_date,
				opt_term, conn);
		return result;
	}

	/**
	 * <药房>退药入库(入口)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX(String), ORDER_CODE=XXX(String),
	 *            DOSAGE_QTY=XXX(double), OPT_USER=XXX(String),
	 *            OPT_DATE=XXX(Timestamp), OPT_TERM=XXX(String)
	 * @param service_level
	 *            服务等级
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
		int batch_seq = 0;
		batch_seq  = parm.getInt("BATCH_SEQ");
		//System.out.println("batch_seq:"+batch_seq);
		result = regressIndStock(org_code, order_code, dosage_qty, opt_user,
				opt_date, opt_term, service_level,batch_seq, conn);
		if (result.getErrCode() != 0)
			return result;
		return result;
	}

	/**
	 * <药房>取消退药扣库
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
	 *            服务等级
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm unRegressIndStock(String org_code, String order_code,
			double dosage_qty, String opt_user, Timestamp opt_date,
			String opt_term, String service_level, TConnection conn) {
		TParm result = new TParm();
		// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
		TParm parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(
				org_code, order_code, "");
		if (parm.getErrCode() < 0) {
			return parm;
		}
		// 库存量
		double qty = 0;
		// 批次序号
		int batch_seq = 0;
		// 出库金额
		double out_amt = 0;
		// 成本金额
		double cost_amt = 0;
		// 根据服务等级选择对应的零售金额列
		String OWN_PRICE = "RETAIL_PRICE";
		if ("2".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE2";
		} else if ("3".equals(service_level)) {
			OWN_PRICE = "OWN_PRICE3";
		} else {
			OWN_PRICE = "RETAIL_PRICE";
		}

		// 成本金额
		cost_amt = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			qty = parm.getDouble("QTY", i);
			batch_seq = parm.getInt("BATCH_SEQ", i);
			if (qty >= dosage_qty) {
				// 药品可以一次扣库
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i)
						* dosage_qty, 2);
				// 计算成本金额
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * dosage_qty;
				// 更新库存(取消退药扣库)
				result = IndStockDTool.getInstance().onUpdateQtyRegOut(
						org_code, order_code, batch_seq, dosage_qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// 回传成本金额
				result.setData("COST_AMT", StringTool.round(cost_amt, 2));
				return result;
			} else {
				// 药品不可一次扣库
				out_amt = StringTool.round(parm.getDouble(OWN_PRICE, i) * qty,
						2);
				// 计算成本金额
				cost_amt += parm.getDouble("VERIFYIN_PRICE", i) * qty;
				// 更新库存(取消退药扣库)
				result = IndStockDTool.getInstance().onUpdateQtyRegOut(
						org_code, order_code, batch_seq, qty, out_amt,
						opt_user, opt_date, opt_term, conn);
				// 更新用量
				dosage_qty = dosage_qty - qty;
			}
		}
		return result;
	}

	/**
	 * <药房>取消退药扣库(入口)
	 * 
	 * @param parm
	 *            :ORG_CODE=XXX(String), ORDER_CODE=XXX(String),
	 *            DOSAGE_QTY=XXX(double), OPT_USER=XXX(String),
	 *            OPT_DATE=XXX(Timestamp), OPT_TERM=XXX(String)
	 * @param service_level
	 *            服务等级
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
	 * 判断部门是否批次过帐中
	 * 
	 * @param org_code
	 *            药房代码
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
	 * 判断是否该药品正在进行调价
	 * 
	 * @param org_code
	 *            药房代码
	 * @param order_code
	 *            药品代码
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
	 * 删除采购计划单主项和细项
	 * 
	 * @return
	 */
	public TParm onDeletePurPlanM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		Object delete = parm.getData("DELETE_SQL");
		TParm result = new TParm();
		if (delete != null) {
			String[] deleteSql;
			if (delete instanceof String[]) {
				deleteSql = (String[]) delete;
				// 删除细项
				for (int i = 0; i < deleteSql.length; i++) {
					result = new TParm(TJDODBTool.getInstance().update(
							deleteSql[i], conn));
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			}
		}
		// 删除主项
		result = IndPurPlanMTool.getInstance().onDelete(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 删除订购细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeletePurorderD(TParm parm, TConnection conn) {
		// 数据检核
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
		// 更新在途量
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
		// 删除细项
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
	 * 删除订购主档及细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeletePurorderM(TParm parm, TConnection conn) {
		// 数据检核
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
		// 判断是否删除细项
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
			// 更新在途量
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
			// 删除细项
			for (int i = 0; i < deleteSql.length; i++) {
				result = new TParm(TJDODBTool.getInstance().update(
						deleteSql[i], conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// 删除主项
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
	 * 删除验收单主档及细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteVerifyinM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		TParm result = new TParm();
		// 判断是否删除细项
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
			// 删除细项
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
		// 删除主项
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
	 * 删除退货单主档及细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteRegressgoodsM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		TParm result = new TParm();
		// 判断是否删除细项
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
			// 删除细项
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
		// 删除主项
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
	 * 删除申请单主档及细项
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onDeleteRequestM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		TParm result = new TParm();
		// 判断是否删除细项
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
			// 删除细项
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
		// 删除主项
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
	 * 出入库数量转换率
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
	 * 保存订购单
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSavePurOrder(TParm parm, TConnection conn) {
		// 数据检核
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
		// 保存订购单细项
		for (int i = 0; i < updateSql.length; i++) {
			result = new TParm(TJDODBTool.getInstance().update(updateSql[i],
					conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// 更新在途量
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

		// 更新订购单中采购计划单号
		Object plan_no = parm.getData("PLAN_NO");
		if (plan_no != null) {
			TParm inparm = new TParm();
			inparm.setData("PURORDER_NO", parm.getValue("PURORDER_NO"));
			inparm.setData("PLAN_NO", parm.getValue("PLAN_NO"));
			result = IndPurorderMTool.getInstance()
					.onUpdatePlanNo(inparm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 新增出库作业(在途) / 耗损、其它出库作业、卫耗材、科室备药(出库即入库)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutOn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 申请单类型
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");
		// 单位类型
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// System.out.println("parmD---"+parmD);
		// 药品出库(申请单出库)
		result = onIndDispenseOut(parmD, org_code, unit_type, request_type,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新申请单状态及实际出入库数量
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 药品出库(申请单出库)
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
		int seq = 1;
		double actual_qty = 0;
		String batch_no = "";
		String valid_date = "";
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// 实际出库数量
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// 不指定批号和效期
			if ("DEP".equals(request_type) || "TEC".equals(request_type)
					|| "GIF".equals(request_type) || "COS".equals(request_type)
					|| "EXM".equals(request_type)) {
				// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
					if (qty >= actual_qty) {
						// 药品可以一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j)
								* actual_qty, 2);
						// 更新库存(申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type, org_code,
										order_code, batch_seq, actual_qty,
										out_amt, parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						if (result.getErrCode() < 0) {
							return result;
						}

						// 新增出库单细项
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
						// 修改取得转换率-发药单位的数量 by liyh 20120801
						double tranUnitQty = getDosageQty(order_code);
						/*
						 * double tranUnitQty = getPhaTransUnitQty(order_code,
						 * "2") * getPhaTransUnitQty(order_code, "1");
						 */
						if ("0".equals(unit_type)) {
							qty_out = actual_qty / tranUnitQty;
						} else {
							qty_out = actual_qty;
						}
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));
						// luhai modify 20120503 begin TEC，EXM，COS 零售价用发药单位
						// begin
						if ("TEC".equals(request_type)
								|| "EXM".equals(request_type)
								|| "COS".equals(request_type)) {// 备药生成时为最小单位不*转换率
																// //luhai
																// 20120503 add
																// EXM
							inparm.setData("RETAIL_PRICE", StringTool.round(
									stock_parm.getDouble("STOCK_RETAIL_PRICE",
											j), 4));
						} else {
							inparm.setData("RETAIL_PRICE", StringTool.round(
									stock_parm.getDouble("STOCK_RETAIL_PRICE",
											j)
											* tranUnitQty, 4));
						}
						// luhai modify 20120503 begin TEC，EXM，COS 零售价用发药单位 end
						double stock_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j)
								* tranUnitQty;
						inparm.setData("STOCK_PRICE", StringTool.round(
								stock_price, 4));
						// luhai 2012-01-13 add verifyin_price begin
						double verifyin_price = 0;
						if ("TEC".equals(request_type)
								|| "EXM".equals(request_type)
								|| "COS".equals(request_type)) {// 备药生成时为最小单位不*转换率
																// //luhai
																// 20120503 add
																// EXM
							verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j);
						} else {
							verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j)
									* tranUnitQty;
							// luhai modify 20120503 add
							// 验收价格的调整，若出库为库存单位则取值时从实际的验收表中进行取值 begin
							int batchSeq = stock_parm.getInt("BATCH_SEQ", j);
							String orderCode = parm.getValue("ORDER_CODE", i);
							double tmpVerifyPrice = getVerifyInStockPrice(
									org_code, orderCode, batchSeq);// 修改取得验收价格的方法
																	// by liyh
																	// 20120801
																	// 由验收表转为ind_stock表
							verifyin_price = tmpVerifyPrice > 0 ? tmpVerifyPrice
									: verifyin_price;
							// luhai modify 20120503 add
							// 验收价格的调整，若出库为库存单位则取值时从实际的验收表中进行取值 end
						}
						inparm.setData("VERIFYIN_PRICE", StringTool.round(
								verifyin_price, 4));
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// 药品不可一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"STOCK_RETAIL_PRICE", j)
								* qty, 2);
						// 更新库存(申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type, org_code,
										order_code, batch_seq, qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						if (result.getErrCode() < 0) {
							return result;
						}

						// 新增出库单细项
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
						// 修改取得转换率-发药单位的数量 by liyh 20120801
						double tranUnitQty = getDosageQty(order_code);
						/*
						 * double tranUnitQty = getPhaTransUnitQty(order_code,
						 * "2") * getPhaTransUnitQty(order_code, "1");
						 */
						if ("0".equals(unit_type)) {
							qty_out = qty / tranUnitQty;
						} else {
							qty_out = qty;
						}
						inparm.setData("QTY", qty_out);
						inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE",
								i));
						inparm.setData("RETAIL_PRICE", StringTool.round(
								stock_parm.getDouble("STOCK_RETAIL_PRICE", j)
										* tranUnitQty, 4));
						double stock_price = stock_parm.getDouble(
								"VERIFYIN_PRICE", j)
								* tranUnitQty;
						inparm.setData("STOCK_PRICE", StringTool.round(
								stock_price, 4));
						// luhai modify 20120503 add 加入TEC的处理 begin
						// luhai 2012-01-13 add verifyin_price begin
						double verifyin_price = 0;
						if ("TEC".equals(request_type)
								|| "EXM".equals(request_type)
								|| "C0S".equals(request_type)) {// 备药生成时为最小单位不*转换率
																// //luhai
																// 20120503 add
																// EXM
							verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j);
						} else {
							verifyin_price = stock_parm.getDouble(
									"VERIFYIN_PRICE", j)
									* tranUnitQty;
							// luhai modify 20120503 add
							// 验收价格的调整，若出库为库存单位则取值时从实际的验收表中进行取值 begin
							int batchSeq = stock_parm.getInt("BATCH_SEQ", j);
							String orderCode = parm.getValue("ORDER_CODE", i);
							double tmpVerifyPrice = getVerifyInStockPrice(
									org_code, orderCode, batchSeq);// 修改取得验收价格的方法
																	// by liyh
																	// 20120801
																	// 由验收表转为ind_stock表
							verifyin_price = tmpVerifyPrice > 0 ? tmpVerifyPrice
									: verifyin_price;
							// luhai modify 20120503 add
							// 验收价格的调整，若出库为库存单位则取值时从实际的验收表中进行取值 end
						}
						// luhai 2012-01-13 add verifyin_price begin
						// double verifyin_price = stock_parm.getDouble(
						// "VERIFYIN_PRICE", j) * tranUnitQty;
						inparm.setData("VERIFYIN_PRICE", StringTool.round(
								verifyin_price, 4));
						// luhai 2012-01-13 add verifyin_price end
						// luhai modify 20120503 add 加入TEC的处理 end
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// 更新出库量
						actual_qty = actual_qty - qty;
						// 更新实际出库数量
						qty_out = qty_out + qty;
					}
				}
			}
			// 指定批号和效期
			else if ("RET".equals(request_type) || "WAS".equals(request_type)
					|| "THO".equals(request_type)) {
				// *****************************************************
				// luhai 2012-01-12 药品流转采用batchSeq进行流转
				// *****************************************************
				// 根据药库编号、药品代码、药品的批号和效期查询批次序号
				// batch_no = parm.getValue("BATCH_NO", i);
				// valid_date = parm.getValue("VALID_DATE", i).substring(0, 10);
				// TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
				// INDSQL.getIndStockBatchSeq(org_code, order_code, batch_no,
				// valid_date)));
				// if (inParmSeq.getErrCode() < 0) {
				// return inParmSeq;
				// }
				// int batch_seq = inParmSeq.getInt("BATCH_SEQ", 0);
				// double out_amt = StringTool.round(inParmSeq.getDouble(
				// "STOCK_RETAIL_PRICE", 0) * actual_qty, 2);
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
				// luhai 2012-01-12 药品流转采用batchSeq进行流转 end
				// *****************************************************
				// 更新库存(申请单出库)
				result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
						request_type, org_code, order_code, batch_seq,
						actual_qty, out_amt, parm.getValue("OPT_USER", i),
						parm.getTimestamp("OPT_DATE", i),
						parm.getValue("OPT_TERM", i), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				// 新增出库单细项
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
				// 修改取得转换率-发药单位的数量 by liyh 20120801
				double tranUnitQty = getDosageQty(order_code);
				/*
				 * double tranUnitQty = getPhaTransUnitQty(order_code, "2") *
				 * getPhaTransUnitQty(order_code, "1");
				 */
				if ("0".equals(unit_type)) {
					qty_out = actual_qty / tranUnitQty;
				} else {
					qty_out = actual_qty;
				}
				inparm.setData("QTY", qty_out);
				inparm.setData("UNIT_CODE", parm.getValue("UNIT_CODE", i));
				inparm.setData("RETAIL_PRICE", StringTool.round(inParmSeq
						.getDouble("STOCK_RETAIL_PRICE", 0)
						* tranUnitQty, 4));
				// 原有4.0实现的0差价是将stockprice
				// 作为验收价格，保持原有逻辑不动，加入verfyinprice，故此逻辑不改动
				double stock_price = inParmSeq.getDouble("VERIFYIN_PRICE", 0)
						* tranUnitQty;
				double verifyinPrice = inParmSeq.getDouble("VERIFYIN_PRICE", 0)
						* tranUnitQty;
				// luhai 2012-01-13 add verfiyin_price begin
				// luhai modify 2012-05-03 add 验收价格若为库存单位从验收表中取值，不从发药单位根据转换率进行计算
				// begin
				String orderCode = parm.getValue("ORDER_CODE", i);
				double tmpVerifyPrice = getVerifyInStockPrice(org_code,
						orderCode, batch_seq);// 修改取得验收价格的方法 by liyh 20120801
												// 由验收表转为ind_stock表
				verifyinPrice = tmpVerifyPrice > 0 ? tmpVerifyPrice
						: verifyinPrice;
				// luhai modify 2012-05-03 add 验收价格若为库存单位从验收表中取值，不从发药单位根据转换率进行计算
				// end
				inparm.setData("VERIFYIN_PRICE", StringTool.round(
						verifyinPrice, 4));
				// luhai 2012-01-13 add verfiyin_price end
				inparm.setData("STOCK_PRICE", StringTool.round(stock_price, 4));
				inparm.setData("ACTUAL_QTY", qty_out);
				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
				// luhai 2012-01-12 加入batch_seq
				inparm.setData("BATCH_SEQ", batch_seq);
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
	 * 新增出库作业(出库即入库)
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseOutIn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 申请单类型
		String request_type = parm.getValue("REQTYPE_CODE");
		String out_org_code = parm.getValue("OUT_ORG_CODE");
		String in_org_code = parm.getValue("IN_ORG_CODE");
		// 单位类型
		String unit_type = parm.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// 入库注记
		boolean in_flg = parm.getBoolean("IN_FLG");
		// 判断是否自动将成本价存回批发价
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		// 药品出库(申请单出库入库)
		result = onIndDispenseOutIn(parmD, out_org_code, in_org_code,
				unit_type, request_type, in_flg, reuprice_flg, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新申请单状态及实际出入库数量
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 药品出库入库(申请单出库入库)
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
		// 入库信息
		TParm stock_in = new TParm();
		TParm result = new TParm();
		// 出库作业
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			// 实际出库数量
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// 不指定批号和效期
			if ("DEP".equals(request_type) || "TEC".equals(request_type)
					|| "GIF".equals(request_type) || "COS".equals(request_type)
					|| "EXM".equals(request_type)) {
				// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(out_org_code, order_code, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
					// System.out.println("order_code===" + order_code);
					// System.out.println("actual_qty===" + actual_qty);
					// System.out.println("qty===" + qty);
					if (qty >= actual_qty) {
						// 药品可以一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", j)
								* actual_qty, 2);
						// 更新库存(申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type,
										out_org_code, order_code, batch_seq,
										actual_qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);
						// 填入入库信息
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

						// 新增出库单细项
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
						inparm.setData("RETAIL_PRICE", parm.getDouble(
								"RETAIL_PRICE", i));
						double stock_price = parm.getDouble("STOCK_PRICE", i);
						// luhai 2012-01-13 加入验收价格 add
						String orgCode = out_org_code;
						TParm stockParm = IndStockDTool.getInstance()
								.onQueryStockBatchSeq(orgCode, order_code,
										batch_no, valid_date);
						String verifyinPrice = "0.0";
						if (stockParm.getCount("VERIFYIN_PRICE") > 0) {
							verifyinPrice = stockParm.getValue(
									"VERIFYIN_PRICE", 0);
						}
						inparm.setData("VERIFYIN_PRICE", verifyinPrice);
						// luhai 2012-01-13 加入验收价格 end
						inparm.setData("STOCK_PRICE", stock_price);
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// 药品不可一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", i)
								* qty, 2);
						// 更新库存(申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut(request_type,
										out_org_code, order_code, batch_seq,
										qty, out_amt,
										parm.getValue("OPT_USER", i),
										parm.getTimestamp("OPT_DATE", i),
										parm.getValue("OPT_TERM", i), conn);

						// 填入入库信息
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

						// 新增出库单细项
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
						inparm.setData("RETAIL_PRICE", parm.getDouble(
								"RETAIL_PRICE", i));
						double stock_price = parm.getDouble("STOCK_PRICE", i);
						inparm.setData("STOCK_PRICE", stock_price);
						// luhai 2012-01-13 加入验收价格 add
						String orgCode = out_org_code;
						TParm stockParm = IndStockDTool.getInstance()
								.onQueryStockBatchSeq(orgCode, order_code,
										batch_no, valid_date);
						String verifyinPrice = "0.0";
						if (stockParm.getCount("VERIFYIN_PRICE") > 0) {
							verifyinPrice = stockParm.getValue(
									"VERIFYIN_PRICE", 0);
						}
						inparm.setData("VERIFYIN_PRICE", verifyinPrice);
						// luhai 2012-01-13 加入验收价格 end
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// 更新出库量
						actual_qty = actual_qty - qty;
						// 更新实际出库数量
						qty_out = qty_out + qty;
					}
				}
			}
			// 指定批号和效期
			else if ("RET".equals(request_type) || "WAS".equals(request_type)
					|| "THO".equals(request_type)) {
				// luhai 2012-01-13 modify 直接采用batch_seq 进行处理 begin
				// 根据药库编号、药品代码、药品的批号和效期查询批次序号
				// batch_no = parm.getValue("BATCH_NO", i);
				// valid_date = parm.getValue("VALID_DATE", i).substring(0, 10);
				// TParm inParmSeq = new TParm(TJDODBTool.getInstance().select(
				// INDSQL.getIndStockBatchSeq(out_org_code, order_code,
				// batch_no,
				// valid_date)));
				// if (inParmSeq.getErrCode() < 0) {
				// return inParmSeq;
				// }
				// int batch_seq = inParmSeq.getInt("BATCH_SEQ", 0);

				int batch_seq = parm.getInt("BATCH_SEQ", i);
				// luhai 2012-01-13 modify 直接采用batch_seq 进行处理 end
				double out_amt = StringTool.round(parm.getDouble(
						"RETAIL_PRICE", i)
						* actual_qty, 2);
				// 更新库存(申请单出库)
				result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
						request_type, out_org_code, order_code, batch_seq,
						actual_qty, out_amt, parm.getValue("OPT_USER", i),
						parm.getTimestamp("OPT_DATE", i),
						parm.getValue("OPT_TERM", i), conn);

				// 填入入库信息
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

				// 新增出库单细项
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
				result = IndDispenseDTool.getInstance().onInsertD(inparm, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				seq++;
			}
		}

		// 入库作业
		if (in_flg) {
			// 根据批号和效期查询药品,存在的话更新库存量,不存在的话取最大批次序号进行新增
			List list = new ArrayList();
			double dosage_qty = 0;
			int batch_seq = 0;
			double retail_price = 0;
			for (int i = 0; i < stock_in.getCount("ORDER_CODE"); i++) {
				order_code = stock_in.getValue("ORDER_CODE", i);
				// 添加入列表
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

			/** 移动加权平均成本 */
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

			/** 批发价更新 */
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
	 * 其他入库(申请单入库)
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
			// 实际入库数量
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			if ("0".equals(unit_type)) {
				actual_qty = parm.getDouble("ACTUAL_QTY", i)
						* getPhaTransUnitQty(order_code, "2");
			} else {
				actual_qty = parm.getDouble("ACTUAL_QTY", i);
			}
			// 指定批号和效期
			if ("THI".equals(request_type)) {
				// luhai modify 2012-01-13 改成根据batchSeq 唯一标示药品 begin
				// 根据药库编号、药品代码、药品的批号和效期查询批次序号
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
				// luhai modify 2012-01-13 改成根据batchSeq 唯一标示药品 end
				double in_amt = StringTool.round(parm.getDouble("RETAIL_PRICE",
						i)
						* actual_qty, 2);
				// 新增入库单细项
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
				if (batchSeqCount > 0) {
					// 1得到的该采购价格没有*转换率
					verifyinPrice = indStockResult.getDouble("VERIFYIN_PRICE",
							0);
					// 2将采购价加入转换率处理
					double tranUnitQty = getPhaTransUnitQty(order_code, "2")
							* getPhaTransUnitQty(order_code, "1");
					verifyinPrice = verifyinPrice * tranUnitQty;
				}
				inparm.setData("VERIFYIN_PRICE", verifyinPrice);
				// luhai 2012-01-13 add verifyinPrice end
				inparm.setData("ACTUAL_QTY", qty_out);
				inparm.setData("PHA_TYPE", parm.getValue("PHA_TYPE", i));
				inparm.setData("OPT_USER", parm.getValue("OPT_USER", i));
				inparm.setData("OPT_DATE", parm.getTimestamp("OPT_DATE", i));
				inparm.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
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
	 * 新增入库作业
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onInsertDispenseIn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 购入单价自动维护
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		TParm parmM = parm.getParm("OUT_M");
		// 申请单类型
		String request_type = parmM.getValue("REQTYPE_CODE");
		// 更新入库主项信息
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
			inparm.setData("OPT_USER", parmD.getValue("OPT_USER", i));
			inparm.setData("OPT_DATE", parmD.getTimestamp("OPT_DATE", i));
			inparm.setData("OPT_TERM", parmD.getValue("OPT_TERM", i));
			// 更新入库细项信息
			result = IndDispenseDTool.getInstance().onUpdateD(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}

		// 入库作业
		if (parm.existData("IN_ORG")) {
			// 入库部门
			String in_org = parm.getValue("IN_ORG");
			// 是否入库
			boolean flg = parm.getBoolean("IN_FLG");
			// 药品代码
			String order_code = "";
			// 单位类型
			String unit_type = parmM.getValue("UNIT_TYPE");
			// 扣库量
			double dosage_qty = 0;
			// 零售价
			double retail_price = 0;
			// 批次序号
			int batch_seq = 0;
			List<String> seqList = new ArrayList<String>();
			Map<String, Integer> batchSeqMap = new HashMap<String, Integer>();
			if (flg) {
				List list = new ArrayList();
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					order_code = parmD.getValue("ORDER_CODE", i);
					// 添加入列表
					list.add(order_code);
					if ("0".equals(unit_type)) {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i)
								* parmD.getDouble("DOSAGE_QTY", i)
								* parmD.getDouble("STOCK_QTY", i);
					} else {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i);
					}
					// 指定批号和效期(入库作业未查出指定效期批号,抓取最大批次序号+1新增)
					TParm batchSeq = new TParm(TJDODBTool.getInstance().select(
							INDSQL.getIndStockMaxBatchSeq(in_org, order_code)));
					batch_seq = batchSeq.getInt("BATCH_SEQ", 0);
					// batch_seq = parmD.getInt("BATCH_SEQ", i);
					retail_price = parmD.getDouble("RETAIL_PRICE", i)
							/ parmD.getDouble("DOSAGE_QTY", i)
							/ parmD.getDouble("STOCK_QTY", i);
					String valid_date = parmD.getValue("VALID_DATE", i)
							.replace('/', '-').substring(0, 10);
					String batch_no = parmD.getValue("BATCH_NO", i);
					String opt_user = parmD.getValue("OPT_USER", i);
					Timestamp opt_date = parmD.getTimestamp("OPT_DATE", i);
					String opt_term = parmD.getValue("OPT_TERM", i);
					String orderCodeBatchSeq = order_code + "-" + batch_seq;
					// System.out.println("---00------ordercodeseq: "+orderCodeBatchSeq);
					/*
					 * if(seqList.contains(orderCodeBatchSeq)){ int seqSize =
					 * seqList.size(); batch_seq += seqSize; batch_seq++;
					 * orderCodeBatchSeq = order_code+batch_seq;
					 * seqList.add(orderCodeBatchSeq); }else{
					 * seqList.add(orderCodeBatchSeq); }
					 */
					// 解决一个单子有多个同样的药品时，batch_seq重复的问题
					Set<String> keySet = batchSeqMap.keySet();
					if (keySet.contains(order_code)) {
						Integer batchSeqValue = batchSeqMap.get(order_code);
						batch_seq = batchSeqValue + 1;
						batchSeqMap.put(order_code, batch_seq);
					} else {
						batchSeqMap.put(order_code, batch_seq);
					}
					// System.out.println("---11------ordercodeseq: "+orderCodeBatchSeq);
					result = IndStockDTool.getInstance()
							.onUpdateStockByBatchVaildIn(request_type, in_org,
									order_code, batch_seq, valid_date,
									batch_no, dosage_qty, retail_price,
									opt_user, opt_date, opt_term, list, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}

				/** 移动加权平均成本 */
				double in_qty = 0;
				double d_qty = 0;
				double stock_price = 0;
				double in_amt = 0;
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					in_qty = parmD.getDouble("ACTUAL_QTY", i);
					d_qty = parmD.getDouble("DOSAGE_QTY", i);
					stock_price = parmD.getDouble("STOCK_PRICE", i);
					in_amt = StringTool.round(in_qty * stock_price, 2);
					in_qty = StringTool.round(in_qty * d_qty
							* parmD.getDouble("STOCK_QTY", i), 2);
					stock_price = getPhaBaseStockPrice(parm.getValue(
							"ORDER_CODE", i), in_amt, in_qty);
					TParm stock_price_parm = new TParm();
					stock_price_parm.setData("ORDER_CODE", parmD.getValue(
							"ORDER_CODE", i));
					stock_price_parm.setData("STOCK_PRICE", stock_price);
					result = PhaBaseTool.getInstance().onUpdateStockPrice(
							stock_price_parm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}

				/** 批发价更新 */
				if ("Y".equals(reuprice_flg)) {
					for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
						d_qty = parmD.getDouble("DOSAGE_QTY", i);
						stock_price = StringTool.round(parm.getDouble(
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
	 * 新增其它入库
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertDispenseOtherIn(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 购入单价自动维护
		String reuprice_flg = parm.getValue("REUPRICE_FLG");
		TParm parmM = parm.getParm("OUT_M");
		result = IndDispenseMTool.getInstance().onInsertM(parmM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 申请单类型
		String request_type = parm.getValue("REQTYPE_CODE");
		String org_code = parm.getValue("ORG_CODE");
		// 单位类型
		String unit_type = parmM.getValue("UNIT_TYPE");
		TParm parmD = parm.getParm("OUT_D");
		// 其他入库(申请单入库)
		result = onIndDispenseOtherIn(parmD, org_code, unit_type, request_type,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}

		// // 中草药自动维护收费及调价损益
		// if (parm.existData("PARM_C")) {
		// TParm parmC = parm.getParm("PARM_C");
		// // /////////////////
		// }

		// 入库作业
		if (parm.existData("IN_ORG")) {
			// 入库部门
			String in_org = parm.getValue("IN_ORG");
			// 是否入库
			boolean flg = parm.getBoolean("IN_FLG");
			// 批次和有效期
			String batchvalid = parm.getValue("BATCH");
			// 药品代码
			String order_code = "";
			// 扣库量
			double dosage_qty = 0;
			// 零售价
			double retail_price = 0;
			// 批次序号
			int batch_seq = 0;
			if (flg) {
				List list = new ArrayList();
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					order_code = parmD.getValue("ORDER_CODE", i);
					list.add(order_code);
					if ("0".equals(unit_type)) {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i)
								* parmD.getDouble("DOSAGE_QTY", i)
								* parmD.getDouble("STOCK_QTY", i);
					} else {
						dosage_qty = parmD.getDouble("ACTUAL_QTY", i);
					}
					// 指定批号和效期(入库作业未查出指定效期批号,抓取最大批次序号+1新增)
					batch_seq = parmD.getInt("BATCH_SEQ", i);
					retail_price = parmD.getDouble("RETAIL_PRICE", i)
							/ parmD.getDouble("DOSAGE_QTY", i)
							/ parmD.getDouble("STOCK_QTY", i);
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

				/** 移动加权平均成本 */
				double in_qty = 0;
				double d_qty = 0;
				double stock_price = 0;
				double in_amt = 0;
				for (int i = 0; i < parmD.getCount("ORDER_CODE"); i++) {
					in_qty = parmD.getDouble("ACTUAL_QTY", i);
					d_qty = parmD.getDouble("DOSAGE_QTY", i);
					stock_price = parmD.getDouble("STOCK_PRICE", i);
					in_amt = StringTool.round(in_qty * stock_price, 2);
					in_qty = StringTool.round(in_qty * d_qty, 2);
					stock_price = getPhaBaseStockPrice(parm.getValue(
							"ORDER_CODE", i), in_amt, in_qty);
					TParm stock_price_parm = new TParm();
					stock_price_parm.setData("ORDER_CODE", parmD.getValue(
							"ORDER_CODE", i));
					stock_price_parm.setData("STOCK_PRICE", stock_price);
					result = PhaBaseTool.getInstance().onUpdateStockPrice(
							stock_price_parm, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}

				/** 批发价更新 */
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
		// 更新申请单状态及实际出入库数量
		result = onUpdateRequestFlgAndActual(parmM.getValue("REQUEST_NO"),
				parmD, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 取得药品转换率
	 * 
	 * @param order_code
	 *            药品代码
	 * @param qty_type
	 *            转换率类型 1:进货/库存 2:库存/配药 3:配药/开药 (大单位-->小单位)
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
	 * 计算库存平均价 (加权平均成本)
	 * 
	 * @param order_code
	 *            药品代码
	 * @param verifyin_atm
	 *            验收金额
	 * @param in_qty
	 *            入库量(配药单位)
	 * @return 加权平均成本
	 */
	public double getPhaBaseStockPrice(String order_code, double verifyin_atm,
			double in_qty) {
		TParm result = new TParm();
		// 取得库存量
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		result = IndStockDTool.getInstance().onQueryStockQTY(parm);
		double qty = result.getDouble("QTY", 0);
		// System.out.println("库存量" + qty);
		// 取得原加成平均成本
		result = PhaBaseTool.getInstance().selectByOrder(order_code);
		double stock_price = result.getDouble("STOCK_PRICE", 0);
		// System.out.println("原加成平均成本" + stock_price);
		// System.out.println("验收金额" + verifyin_atm);
		// System.out.println("入库量" + in_qty);
		// 计算加权平均成本
		double new_price = (qty * stock_price + verifyin_atm) / (qty + in_qty);
		new_price = StringTool.round(new_price, 4);
		return new_price;
	}

	/**
	 * 更新申请单的状态和实际出入库数量
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
			double qty = parm.getDouble("QTY", i);
			double actual_qty = parm.getDouble("ACTUAL_QTY", i);
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
		// 更新申请单状态
		TParm parmT = new TParm(TJDODBTool.getInstance().update(
				INDSQL.onUpdateRequestFlg(request_no), conn));
		if (parmT.getErrCode() < 0) {
			return parmT;
		}
		return result;
	}

	/**
	 * 盘点冻结
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertQtyCheck(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
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
			// 得到验收价格 luhai 2012-1-23 begin
			String orderCode = parm_Order.getData("ORDER_CODE", i) + "";
			String batchSeq = parm_Order.getData("BATCH_SEQ", i) + "";
			String orgCode = parm.getValue("ORG_CODE");
			TParm indStokParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDStock(orgCode, orderCode, Integer
							.parseInt(batchSeq))));
			String verifyinPrice = indStokParm.getValue("VERIFYIN_PRICE", 0);
			parm.setData("VERIFYIN_PRICE", verifyinPrice);
			// 得到验收价格 luhai 2012-1-23 end
			// 盘点冻结新增
			result = IndQtyCheckTool.getInstance().onInsert(parm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 冻结药品
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
	 * 盘点保存数据
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateQtyCheck(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 将本次所有冻结药品的实际数量和调整量全部置为0和0-库存量
		// 注释掉置为0 by liyh 20120726
		/*
		 * result = IndQtyCheckTool.getInstance().onUpdate(parm, conn); if
		 * (result.getErrCode() < 0) { return result; }
		 */
		// 循环保存盘点数据
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
			// 判断是否存在批次序号，不存在的取最大批次序号
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
	 * 解除冻结
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateUnLock(TParm parm, TConnection conn) {
		// luhai modify 2012-04-25 begin
		// // 数据检核
		// if (parm == null)
		// return null;
		// // 结果集
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
		// // 更新库存表数据 IND_STOCK
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
		// // 更新库存表数据 IND_STOCK
		// result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
		// parmIn, conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// // 更新盘点表数据 IND_QTYCHECK
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
		// // 更新库存表数据 IND_STOCK
		// result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
		// parmIn, conn);
		// if (result.getErrCode() < 0) {
		// return result;
		// }
		// // 更新盘点表数据 IND_QTYCHECK
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
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
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
					// 更新库存表数据 IND_STOCK
					TParm parmIn = new TParm();
					parmIn.setData("STOCK_FLG", "N");
					parmIn.setData("CHECKMODI_QTY", parmY.getDouble("MODI_QTY",
							j));
					parmIn.setData("CHECKMODI_AMT", parmY.getDouble("MODI_ATM",
							j));
					parmIn.setData("FROZEN_DATE", frozen_date);
					parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
					parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
					parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
					parmIn.setData("ORG_CODE", org_code);
					parmIn.setData("ORDER_CODE", order_code);
					parmIn.setData("BATCH_SEQ", parmY.getInt("BATCH_SEQ", j));
					// 更新库存表数据 IND_STOCK
					result = IndStockDTool.getInstance()
							.onUpdateUnLockQtyCheck(parmIn, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
					// 更新盘点表数据 IND_QTYCHECK
					parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
					parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
					result = IndQtyCheckTool.getInstance().onUpdateUnLock(
							parmIn, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
				}
			} else {
				// System.out.println("1--------------------" + parm);
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
				parmIn.setData("CHECKMODI_QTY", parmY.getDouble("MODI_QTY", 0));
				parmIn.setData("CHECKMODI_AMT", parmY.getDouble("MODI_ATM", 0));
				parmIn.setData("FROZEN_DATE", frozen_date);
				parmIn.setData("OPT_USER", parm.getData("OPT_USER"));
				parmIn.setData("OPT_DATE", parm.getData("OPT_DATE"));
				parmIn.setData("OPT_TERM", parm.getData("OPT_TERM"));
				// 得到库存
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
				// 库存不足
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
								System.out.println("处理后的modiQty============="
										+ qtymodi);
								orderMap.put(key, 0);
							}
						}
					}
				}

				// 更新库存表数据 IND_STOCK
				result = IndStockDTool.getInstance().onUpdateUnLockQtyCheck(
						parmIn, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
				// 更新盘点表数据 IND_QTYCHECK
				parmIn.setData("UNFREEZE_USER", parm.getData("OPT_USER"));
				parmIn.setData("UNFREEZE_DATE", parm.getData("OPT_DATE"));
				result = IndQtyCheckTool.getInstance().onUpdateUnLock(parmIn,
						conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
		// end
	}

	/**
	 * 卫耗材出库
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertCosOut(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		String request_no = parm.getValue("REQUEST_NO");
		String check_flg = parm.getValue("CHECK_FLG");
		if ("Y".equals(check_flg)) {
			// 1.新增出库单主项
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
			// 取得卫耗材申请单细项
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
				// 实际出库数量
				double qty_out = 0;
				order_code = requestD.getValue("ORDER_CODE", i);
				actual_qty = requestD.getDouble("QTY", i);
				// 不指定批号和效期
				TParm stock_parm = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				for (int j = 0; j < stock_parm.getCount(); j++) {
					double qty = stock_parm.getDouble("QTY", j);
					int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
					if (qty >= actual_qty) {
						// 药品可以一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", j)
								* actual_qty, 2);
						// 2.更新库存(卫耗材申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut("COS", org_code,
										order_code, batch_seq, actual_qty,
										out_amt, parm.getValue("OPT_USER"),
										parm.getTimestamp("OPT_DATE"),
										parm.getValue("OPT_TERM"), conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						// 3.新增出库单细项
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						break;
					} else {
						// 药品不可一次扣库
						double out_amt = StringTool.round(stock_parm.getDouble(
								"RETAIL_PRICE", i)
								* qty, 2);
						// 2.更新库存(卫耗材申请单出库)
						result = IndStockDTool.getInstance()
								.onUpdateQtyRequestOut("COS", org_code,
										order_code, batch_seq, qty, out_amt,
										parm.getValue("OPT_USER"),
										parm.getTimestamp("OPT_DATE"),
										parm.getValue("OPT_TERM"), conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						// 3.新增出库单细项
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
						result = IndDispenseDTool.getInstance().onInsertD(
								inparm, conn);
						if (result.getErrCode() < 0) {
							return result;
						}
						seq++;
						// 更新出库量
						actual_qty = actual_qty - qty;
						// 更新实际出库数量
						qty_out = qty_out + qty;
					}
				}
			}
			// 4.更新申请单状态
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
		// 5.更新卫耗材申请单主项
		result = IndRequestMTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 科室备药生成查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDeptExm(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		String type = parm.getValue("TYPE");
		TParm resultM = new TParm();
		TParm resultD = new TParm();

		if ("OPD".equals(type)) {
			// 汇总resultM
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
			}

			// 明细resultD
			resultD = IndRequestMTool.getInstance().onQueryOPDDeptExmD(parm);
			if (resultD.getErrCode() < 0) {
				return resultD;
			}
			result.addData("RESULT_M", resultM.getData());
			result.addData("RESULT_D", resultD.getData());
		} else if ("IBS".equals(type)) {
			// 汇总
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
			// luhai modify 明细数据抓取改成请领时间 begin
			if (!"N".equals(parm.getValue("REQUEST_FLG_B"))) {// 已申请
				// 明细
				resultD = IndRequestMTool.getInstance()
						.onQueryIBSDeptExmDFinish(parm);
			} else {
				// 明细
				resultD = IndRequestMTool.getInstance()
						.onQueryIBSDeptExmD(parm);
			}
			// //明细
			// resultD = IndRequestMTool.getInstance().onQueryIBSDeptExmD(parm);
			// luhai modify 明细数据抓取改成请领时间 end
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
	 * 科室备药生成查询-病区备药
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDeptFromOdiDspnm(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// 已经申请
		if ("Y".equals(parm.getValue("REQUEST_FLG_A"))) {
			// 汇总
			resultM = IndRequestMTool.getInstance().onQueryExm(parm);
			if (resultM.getErrCode() < 0) {
				return resultM;
			}
			if (null != parm.getValue("REQUEST_NO")
					&& parm.getValue("REQUEST_NO").length() > 3) {
				// 明细
				resultD = IndRequestMTool.getInstance()
						.queryOdiDspnmExmEd(parm);
				if (resultD.getErrCode() < 0) {
					return resultD;
				}
			}

		} else {// 未申请
			// 汇总
			resultM = IndRequestMTool.getInstance().queryOdiDsnpmExmM(parm);
			// System.out.println("resultM---"+resultM);
			if (resultM.getErrCode() < 0) {
				return resultM;
			}
			// 明细
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
	 * 生成请领单
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onCreateDeptExmRequest(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();

		// 新增申请单主项
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("111111111111");

		// 新增请领单细项
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

		// 更新门急住状态
		String type = parm.getValue("TYPE");
		TParm update = parm.getParm("UPDATE");
		if ("I".equals(type)) {
			// 更新住院状态
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
			// 更新门急诊状态
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
		} else if ("H".equals(type)) {// add by wanglong 20130324
			String caseNo = "";
			String rxNo = "";
			String orderCode = "";
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			String sql = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				rxNo = update.getValue("RX_NO", i);
				orderCode = update.getValue("ORDER_CODE", i);// add by wanglong
																// 20130324
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				sql = "UPDATE HRM_ORDER SET REQUEST_NO='" + requestNo
						+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
						+ caseNo +
						// "' AND RX_NO='" + rxNo +
						"' AND ORDER_CODE='" + orderCode + // add by wanglong
															// 20130324
						"' AND SEQ_NO=" + seqNo;
				// System.out.println("=======================sql============" +
				// sql);
				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 生成请领单
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onCreateDeptOdiRequestSpc(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();

		// 新增申请单主项
		TParm requestM = parm.getParm("REQUEST_M");
		// System.out.println("indtool--------------requestM"+requestM);
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("111111111111");

		// 新增请领单细项
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
		 * // 更新ODI_DSPNM住状态 TParm update = parm.getParm("UPDATE");
		 * System.out.println("--------- ---update:"+update); //更新病区-住院状态 String
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
	 * 门急诊生成请领单
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
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 新增申请单主项
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 新增请领单细项
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
	 * 手术介入麻精备药申请生成请领单
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
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 新增申请单主项
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 新增请领单细项
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
	 * 药品库存日结批次作业
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onIndStockBatch(TParm parm, TConnection conn) {
		// 数据检核
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
	 * 指定部门药品库存日结批次作业
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onIndStockBatchByOrgCode(TParm parm, TConnection conn) {
		// 结果集
		TParm result1 = new TParm();
		// 数据检核
		if (parm == null)
			return result1;
		TParm resutlParm = new TParm();
		// 1.更新该过账部门的IND_ORG.BATCH_FLG为"Y"
		result1 = IndOrgTool.getInstance().onUpdateBatchFlg(parm, conn);
		if (result1.getErrCode() < 0) {
			return result1;
		}
		// System.out.println("1----");
		// 2.药品库存日结批次作业
		// 2.1查询日库存交易档
		TParm result = IndStockDTool.getInstance().onQuery(parm);
		if (result.getErrCode() < 0) {
			return result;
		}
		// System.out.println("2--result--"+result);
		// 2.2日结批次作业
		String order_code = "";
		Timestamp date = parm.getTimestamp("TRANDATE");
		for (int i = 0; i < result.getCount("ORG_CODE"); i++) {
			// 当前库存
			double stock_qty = result.getDouble("STOCK_QTY", i);
			// 昨日库存
			double last_stock_qty = result.getDouble("LAST_TOTSTOCK_QTY", i);
			// 本日入
			double in_qty = result.getDouble("IN_QTY", i);
			// 本日出
			double out_qty = result.getDouble("OUT_QTY", i);
			// 本日盘差
			double modi_qty = result.getDouble("CHECKMODI_QTY", i);
			if (stock_qty == 0 && last_stock_qty == 0 && in_qty == 0
					&& out_qty == 0 && modi_qty == 0) {
				continue;
			}
			// 计算日使用量 = 今日出库 - 盘差调整
			double UseQty = out_qty - modi_qty;
			// 计算日库存量 = 昨日库存 + 今日入库 - 今日出库 + 盘差调整
			double TotQty = stock_qty;// last_stock_qty + in_qty - out_qty +
										// modi_qty;;;;update by liyh
										// stock_qty是实时值，不用计算
			if (TotQty != (last_stock_qty + in_qty - out_qty + modi_qty)) {// 不等于
																			// 写日志
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
			// 计算日支出金额
			double UseAmt = UseQty * result.getDouble("RETAIL_PRICE", i);
			// 计算日库存金额
			double TotAmt = TotQty * result.getDouble("RETAIL_PRICE", i);
			// 计算昨日库存金额
			double TotLastStockAmt = last_stock_qty
					* result.getDouble("RETAIL_PRICE", i);

			// 是否有调价计划
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

			// 写入药品库存日结作业
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
			// System.out.println("inparm---" + inparm);
			// 删除插入ddstock 的功能，有rebatch 批次插入 begin
			resutlParm = IndDDStockTool.getInstance().onInsert(inparm, conn);
			if (resutlParm.getErrCode() < 0) {
				return resutlParm;
			}
			// 删除插入ddstock 的功能，有rebatch 批次插入 end
			// System.out.println("4----");

			// 各项出库纪录归零
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
		// 3.更新该过账部门的IND_ORG.BATCH_FLG为"N"
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
	 * 日月结查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryDay(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 1.药库日结中查询药房可发药的药品分类
		String org_code = parm.getValue("ORG_CODE");
		TParm type_parm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getIndDDStockOrderTypeCode(org_code)));
		if (type_parm.getErrCode() < 0) {
			return type_parm;
		}
		// System.out.println("type_parm==" + type_parm);
		// 2.每种药品的统计查询
		for (int i = 0; i < type_parm.getCount("TYPE_CODE"); i++) {
			result.addData("TYPE_CODE", type_parm.getValue("TYPE_CODE", i));
			result.addData("TYPE_DESC", type_parm.getValue("CHN_DESC", i));
			// 根据药品分类查询药品个数
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndDDStockOrderTypeCode(org_code, type_parm
							.getValue("TYPE_CODE", i))));
			// System.out.println("orderParm==" + orderParm);
			if (orderParm.getErrCode() < 0) {
				return orderParm;
			}
			// 本期交易档成本金额
			double stock_price = 0;
			// 上期结存成本金额
			double last_stock_amt = 0;
			// 上期结存零售金额
			double last_own_amt = 0;
			// 本期结存成本金额
			double stock_amt = 0;
			// 本期结存零售金额
			double own_amt = 0;
			for (int j = 0; j < orderParm.getCount("ORDER_CODE"); j++) {
				// 本期交易档成成本价查询(查询统计区间最后一天的所有药品和药品成本价动作)
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
				TParm last_stock_amtParm = new TParm(TJDODBTool.getInstance()
						.select(
								INDSQL
										.getIndDDStockLastStockAMT(parm
												.getValue("START_DATE"), parm
												.getValue("ORG_CODE"),
												orderParm.getValue(
														"ORDER_CODE", j),
												stock_price)));

				if (last_stock_amtParm.getErrCode() < 0) {
					return last_stock_amtParm;
				}
				// 上期结存成本金额
				last_stock_amt = last_stock_amtParm.getDouble("LAST_STOCK_AMT",
						0);
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
				if ("A".equals(parm.getValue("ORG_TYPE"))) {
					// 药库交易档成本
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
				} else {
					// 药房交易档成本
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
						result.setData("LAST_STOCK_AMT", i, result.getDouble(
								"LAST_STOCK_AMT", i)
								+ last_stock_amt);
					}
				}
				// 上期结存零售金额
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

				// 本期结存成本金额和零售金额
				TParm stock_ownParm = new TParm();
				TParm stock_ownInParm = new TParm();
				stock_ownInParm
						.setData("START_DATE", parm.getValue("END_DATE"));
				stock_ownInParm.setData("END_DATE", parm.getValue("END_DATE"));
				stock_ownInParm.setData("ORG_CODE", parm.getValue("ORG_CODE"));
				stock_ownInParm.setData("ORDER_CODE", orderParm.getValue(
						"ORDER_CODE", j));
				resultParm = IndDDStockTool.getInstance()
						.onQueryDDStockDayStockOwn(stock_ownInParm);
				if (resultParm.getErrCode() < 0) {
					return resultParm;
				}
				stock_amt = resultParm.getDouble("STOCK_AMT", 0);
				own_amt = resultParm.getDouble("OWN_AMT", 0);

				stock_ownInParm.setData("START_DATE", parm
						.getValue("START_DATE"));
				if ("A".equals(parm.getValue("ORG_TYPE"))) {
					// 药库本期交易档零售金额
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
					// 药房本期交易档零售金额
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
	 * 取消出库作业
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateDipenseCancel(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 更新申请单累计完成量和状态
		TParm parm_D = parm.getParm("PARM_D");
		for (int i = 0; i < parm_D.getCount("ORDER_CODE"); i++) {
			TParm inParm = new TParm();
			inParm.setData("REQUEST_NO", parm.getValue("REQUEST_NO"));
			// inParm.setData("SEQ_NO", parm_D.getInt("REQUEST_SEQ", i)); //by
			// liyanhui on 20120625 取消出库 按整张申请单出库
			inParm.setData("ACTUAL_QTY", "0");
			inParm.setData("UPDATE_FLG", "0");// by liyanhui on
												// 20120618,取消出后还可以再次出库-修改请领表状态
			inParm.setData("OPT_USER", parm.getData("OPT_USER"));
			inParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			inParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = IndRequestDTool.getInstance().onUpdateActualQtyCancel(
					inParm, conn);
			System.out.println("4914-------------------errorcode:"
					+ result.getErrCode());
			if (result.getErrCode() < 0) {
				return result;
			}
			// 根据药品代码、效期、批号和部门查询药品批次序号
			String sql = INDSQL.getIndStockBatchSeq(parm.getValue("ORG_CODE"),
					parm_D.getValue("ORDER_CODE", i), parm_D.getValue(
							"BATCH_NO", i), parm_D.getValue("VALID_DATE", i)
							.substring(0, 10));
			TParm batch_seq_Parm = new TParm(TJDODBTool.getInstance().select(
					sql));
			if (batch_seq_Parm.getErrCode() < 0) {
				return batch_seq_Parm;
			}
			// 修改出库部门库存量
			// System.out.println(parm_D);
			// luhai 2012-4-5 根据出库单位判断是否需要进行单位转换 begin
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
			String orderCode = parm_D.getValue("ORDER_CODE", i);
			String unitCode = parm_D.getValue("UNIT_CODE", i);
			String stockUnit = getStockUnit(orderCode);
			// 药品发药时和库存单位相同，需要进行转换率处理，indstock 存发药单位
			if (unitCode.equals(stockUnit)) {
				actualQty = getDosageQty(orderCode) * actualQty;
			} else {
				// 出库单据就是发药单位不需要处理
				actualQty = actualQty;
			}
			result = IndStockDTool.getInstance().onUpdateQtyRequestOut(
					parm.getValue("REQTYPE_CODE"), parm.getValue("ORG_CODE"),
					parm_D.getValue("ORDER_CODE", i),
					batch_seq_Parm.getInt("BATCH_SEQ", 0), -actualQty,
					-parm_D.getDouble("RETAIL_ATM", i),
					parm.getValue("OPT_USER"), parm.getTimestamp("OPT_DATE"),
					parm.getValue("OPT_TERM"), conn);
			// luhai 2012-4-5 根据出库单位判断是否需要进行单位转换 end
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// 更新出库单主项状态
		result = IndDispenseMTool.getInstance().onUpdateMFlg(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 得到药品的库存单位
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
	 * 得到药品的发药-库存转换率
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
	 * 草药自动维护收费标准
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateGrpricePrice(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
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
			// 1.更新SYS_FEE_HISTORY的历史数据
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
			// 2.新增SYS_FEE_HISTORY的最新数据
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
			// 3.更新SYS_FEE数据(从SYS_FEE_HISTORY中取得最新数据更新SYS_FEE)
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
			// 4.更新PHA_BASE中的零售价和批发价
			TParm phaParm = new TParm();
			phaParm.setData("ORDER_CODE", order_code);
			phaParm.setData("RETAIL_PRICE", own_price);
			phaParm.setData("TRADE_PRICE", own_price);
			result = PhaBaseTool.getInstance().onUpdateRetailPrice(phaParm,
					conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			// 5.调价损益
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
	 * 给PARM赋值
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
	 * 拿到SYS_FEE简称
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
	 * 过滤方法
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
	 * IND_STCOKM主档更新
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateIndStockM(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
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
		return result;
	}

	/**
	 * 按病区整批配药(住院药房全病区整批配药)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm luhai add 加入返回值中加入扣库的的order信息
	 */
	public TParm reduceIndStockByStation(TParm parm, TConnection conn) {
		// luhai add 2012-1-28 begin
		TParm returnParm = new TParm();
		// luhai add 2012-1-28 end
		// 数据检核
		if (parm == null) {
			return null;
		}
		// System.out.println("parm===" + parm);
		// 结果集
		TParm result = new TParm();
		// 从Config文件中取得自动循环次数
		// int count =
		// Integer.parseInt(TConfig.getSystemValue("CYCLE_TIMES_FOR_ERR"))
		int count = 1;
		String org_code = "";
		String order_code = "";
		String batch_no ="";
		double dosage_qty = 0;
		double own_price = 0;
		// i: 控制自动循环次数
		for (int i = 0; i < count; i++) {
			// 查询药品配药信息,拼组更新语句组
			// 更新语句
			String update_sql = "";
			List list = new ArrayList(); // 用于存储SQL语句数组
			List order_list = new ArrayList(); // 用于存储ORDER_CODE
			// j: 药品种类个数
			int parmCount = parm.getCount("ORDER_CODE");
			// System.out.println("parmCount=" + parmCount);
			for (int j = 0; j < parmCount; j++) {
				org_code = parm.getValue("ORG_CODE", j);
				order_code = parm.getValue("ORDER_CODE", j);
				dosage_qty = parm.getDouble("DOSAGE_QTY", j);
				own_price = parm.getDouble("OWN_PRICE", j);
				batch_no = parm.getValue("BATCH_NO",j);
				// zhangyong20101119 begin
				// 检核库存
				
				
				
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

				// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
				TParm parmOrder = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				// System.out.println("parmOrder=" + parmOrder);
				if (parmOrder.getErrCode() != 0) {
					return parmOrder;
				}

				// 库存量
				double qty = 0;
				// 批次序号
				int batch_seq = 0;
				// 出库数量
				double out_qty = dosage_qty;
				// 出库金额
				double out_amt = 0;
				// 验收价格
				double verifyinPrice = 0;
				int parmOrderCount = parmOrder.getCount();
				// **************************************************************
				// luhai modify 2012-1-28 将扣除的order信息记录到TParm 中 begin
				// **************************************************************
				// for (int k = 0; k < parmOrderCount; k++) {
				// qty = parmOrder.getDouble("QTY", k);
				// batch_seq = parmOrder.getInt("BATCH_SEQ", k);
				// if (qty >= dosage_qty) {
				// // 药品可以一次扣库
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
				// // 药品不可一次扣库
				// out_amt = StringTool.round(own_price * qty, 2);
				// update_sql = INDSQL.getUpdateReduceIndStockSql(org_code,
				// order_code, batch_seq, qty, out_amt,
				// parm.getValue("OPT_USER", j),
				// parm.getValue("OPT_DATE", j),
				// parm.getValue("OPT_TERM", j));
				// //System.out.println("qty<dosage_qty----" + update_sql);
				// list.add(update_sql);
				// order_list.add(order_code);
				// // 更新用量
				// dosage_qty = dosage_qty - qty;
				// }
				// }
				// }
				if(batch_no!=null&&!"".equals(batch_no)) {				
					parmOrder = IndStockDTool.getInstance()
					.onQueryStockBatchNo(org_code, order_code, "",batch_no);
					if (parmOrder.getErrCode() != 0) {
						return parmOrder;			
					}				
				}
				for (int k = 0; k < parmOrderCount; k++) {
					qty = parmOrder.getDouble("QTY", k);
					batch_seq = parmOrder.getInt("BATCH_SEQ", k);
					verifyinPrice = parmOrder.getDouble("VERIFYIN_PRICE", k);
					
//					System.out.println("dosage_qty:"+dosage_qty);
//					System.out.println("qty:"+qty);
					
					if (qty >= dosage_qty) {  
						// 药品可以一次扣库
						out_amt = StringTool.round(own_price * dosage_qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, dosage_qty,
								out_amt, parm.getValue("OPT_USER", j), parm
										.getValue("OPT_DATE", j), parm
										.getValue("OPT_TERM", j));
						// System.out.println("qty>=dosage_qty----" +
						// update_sql);
						// 记录扣库药品
						returnParm.addData("ORDER_CODE", order_code);
						returnParm.addData("DISPENSE_QTY", dosage_qty);
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						break;
					} else {
						// 药品不可一次扣库
						out_amt = StringTool.round(own_price * qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, qty, out_amt,
								parm.getValue("OPT_USER", j), parm.getValue(
										"OPT_DATE", j), parm.getValue(
										"OPT_TERM", j));
						// System.out.println("qty<dosage_qty----" +
						// update_sql);
						// 记录扣库药品  
						returnParm.addData("ORDER_CODE", order_code);
						//fux modify 20160318 修改需扣库 数量
						returnParm.addData("DISPENSE_QTY", qty); 
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						// 更新用量
						dosage_qty = dosage_qty - qty;
					}
				}
			}
			// ********************************************************
			// luhai modify 2012-1-28 将扣除的order信息记录到TParm 中 end
			// ******************************************************
			// 执行拼组后的更新SQL语句组
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
		// 若程序未出异常，则返回扣库的药品Parm
		return returnParm;
	}

	/**
	 * 按病区整批配药缺药信息(住院药房全病区整批配药)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm defectIndStockQTY(TParm parm) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		String order_code = "";
		String org_code = "";
		String batchNo="";
		double dosage_qty = 0;
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			org_code = parm.getValue("ORG_CODE", i);
			order_code = parm.getValue("ORDER_CODE", i);
			batchNo = parm.getValue("BATCH_NO",i);
			dosage_qty = parm.getDouble("DOSAGE_QTY", i);
			if(batchNo!=null&&!"".equals(batchNo)) {		
				String search = "SELECT A.STOCK_QTY FROM IND_STOCK A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND SYSDATE < A.VALID_DATE AND B.ANTIBIOTIC_CODE IS NOT NULL AND SKINTEST_FLG='Y' AND A.ORG_CODE='"+org_code+"' AND A.ORDER_CODE='"+order_code+"' AND A.BATCH_NO='"+batchNo+"' ORDER BY A.VALID_DATE ASC";
				TParm batchParm = new TParm(TJDODBTool.getInstance().select(search));
				if(batchParm.getCount()>0&&dosage_qty>batchParm.getDouble("STOCK_QTY", 0)) {
					result.addData("ORG_CODE", org_code);
					result.addData("ORDER_CODE", order_code);
					result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
					result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
					result.addData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
					result.addData("STOCK_QTY", batchParm.getDouble("STOCK_QTY", 0));
					result.addData("DIFF_QTY", dosage_qty
							- batchParm.getDouble("STOCK_QTY", 0));
							result.addData("BATCH_NO", batchNo);
				}
			}
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
			if (dosage_qty > parmOrder.getDouble("QTY", 0)&&"".equals(batchNo)) {
				result.addData("ORG_CODE", org_code);
				result.addData("ORDER_CODE", order_code);
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
				result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
				result.addData("DOSAGE_UNIT", parm.getValue("DOSAGE_UNIT", i));
				result.addData("STOCK_QTY", parmOrder.getDouble("QTY", 0));
				result.addData("DIFF_QTY", dosage_qty
						- parmOrder.getDouble("QTY", 0));
				result.addData("BATCH_NO", "");
			}
		}
		return result;
	}

	/**
	 * 取得药品的信息,格式： 药品名称 + (商品名)
	 * 
	 * @return String
	 */
	public String getOrderDescAndGoodsDesc(String order_code,
			String order_desc, String goods_desc) {
		// 药品代码为空
		if (!"".equals(order_code)) {
			TParm parm = getSysFeeOrder(order_code);
			return getOrderDescAndGoodsDesc("", parm.getValue("ORDER_DESC"),
					parm.getValue("GOODS_DESC"));
		} else {
			// 商品名为空
			if ("".equals(goods_desc)) {
				return order_desc;
			}
			// 商品名不为空
			else {
				return order_desc + "(" + goods_desc + ")";
			}
		}
	}

	/**
	 * 根据药品的batchseq ordercode 从ind_verifyin中得到验收时的价格 luhai add 2012-05-03
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
	 * 根据药品的batchseq ordercode 从ind_stock中得到验收时的价格 luhai add 2012-05-03
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
	 * 物联网-HIS 验收时自动生成请领和出库单
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
		// 得到药库拨补参数
		// TParm sysParm = new
		// TParm(TJDODBTool.getInstance().select(INDSQL.getINDSysParm()));
		String appOrgCode = parm.getValue("APP_ORG_CODE");// sysParm.getValue("TOXIC_STORAGE_ORG",
															// 0);
		// 得到请领单号
		String requestNo = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		// 得到请领主档PARM
		TParm requestM = getRequestMAutoOfDrugInfo(parm, requestNo, appOrgCode);
		// System.out.println("------------requestM: " + requestM);
		// 生成请领主档
		// System.out.println("------------requestM--sql : " +
		// SPCSQL.saveRequestMAutoOfDrug(requestM));
		result = new TParm(TJDODBTool.getInstance().update(
				INDSQL.saveRequestMAutoOfDrug(requestM), conn));
		if (result.getErrCode() < 0) {
			return result;
		}
		int seqNo = 1;
		// 生成请领明细档
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			String orderCode = parm.getValue("ORDER_CODE", i);
			double qty = parm.getDouble("VERIFYIN_QTY", i);
			seqNo = seqNo + i;
			// 得到请领明细档PARM
			TParm requestD = getRequestDAutoOfDrugInfo(requestNo, seqNo + "",
					orderCode, qty, optUser, optTerm, region);
			// System.out.println("------------requestD: " + requestD);
			// 保存
			// System.out.println("------------requestD--sql : " +
			// SPCSQL.saveRequestDAutoOfDrug(requestD));
			result = new TParm(TJDODBTool.getInstance().update(
					INDSQL.saveRequestDAutoOfDrug(requestD), conn));
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		/*
		 * // 生成出库主档 String dispenseNo = parm.getValue("DISPENSE_NO"); TParm
		 * dispsenseM = geDispsenseMAutoOfDrugInfo(parm, dispenseNo, requestNo,
		 * appOrgCode); // System.out.println("------------dispsenseM: " +
		 * dispsenseM); // 保存 //
		 * System.out.println("------------dispsenseM sql: " +
		 * SPCSQL.saveDispsenseMAutoOfDrug(dispsenseM)); result = new
		 * TParm(TJDODBTool
		 * .getInstance().update(SPCSQL.saveDispsenseMAutoOfDrug(dispsenseM),
		 * conn)); if (result.getErrCode() < 0) { return result; }
		 */
		/*
		 * // 生成出库明细档 for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
		 * String orderCode = parm.getValue("ORDER_CODE", i); double qty =
		 * parm.getDouble("VERIFYIN_QTY", i); String batchSeq =
		 * parm.getValue("BATCH_SEQ", i); seqNo = seqNo + i; // 得到出库明细档PARM
		 * TParm requestD = getDispsenseDAutoOfDrugInfo(dispenseNo, seqNo + "",
		 * orgCode, orderCode, qty, batchSeq, parm); //
		 * System.out.println("------------requestD: " + requestD); // 保存 //
		 * System.out.println("------------requestD sql:  " +
		 * SPCSQL.saveDispsenseDAutoOfDrug(requestD)); result = new
		 * TParm(TJDODBTool
		 * .getInstance().update(SPCSQL.saveDispsenseDAutoOfDrug(requestD),
		 * conn)); if (result.getErrCode() < 0) { return result; } }
		 * 
		 * // 生成出库明细档 for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
		 * String orderCode = parm.getValue("ORDER_CODE", i); double qty =
		 * parm.getDouble("VERIFYIN_QTY", i); String batchSeq =
		 * parm.getValue("BATCH_SEQ", i); seqNo = seqNo + i; // 得到出库明细档PARM
		 * TParm stockM = getStockDParm(orgCode, orderCode, qty, batchSeq); //
		 * System.out.println("------------requestD: " + stockM); // 保存 //
		 * System.out.println("------------requestD sql:  " +
		 * SPCSQL.updateStockByRequest(stockM)); //更新库存 result = new
		 * TParm(TJDODBTool
		 * .getInstance().update(SPCSQL.updateStockByRequest(stockM))); if
		 * (result.getErrCode() < 0) { return result; } }
		 */

		return result;
	}

	/**
	 * 组装麻精自动生成请领单主档的parm
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
	 * 组装麻精自动出库单主档
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
		// 得到药品的价格
		TParm priceParm = getOrderCodePrice(orderCode);
		parm.setData("REQUEST_NO", requestNo);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("QTY", fixQty);
		// 1库存单位；2配药单位
		parm.setData("UNIT_CODE", priceParm.getValue("UNIT_CODE", 0));
		parm.setData("RETAIL_PRICE", priceParm.getValue("RETAIL_PRICE", 0));
		parm.setData("STOCK_PRICE", priceParm.getValue("STOCK_PRICE", 0));
		// 单据状态 0:已申请 1:部份接收 2:已终止 3:完全接收
		parm.setData("UPDATE_FLG", "0");
		parm.setData("OPT_USER", optUser);
		parm.setData("OPT_TERM", optTerm);
		parm.setData("REGION_CODE", region);
		return parm;
	}

	/**
	 * 获得药品的价格
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
		// 查詢藥品單位和價格
		String sql = INDSQL.getPHAInfoByOrder(orgCode);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// 订购单位
		pur_price = StringTool.round(result.getDouble("A.PURCH_UNIT", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// 库存单位
		stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// 发药单位
		retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
				* result.getDouble("DOSAGE_QTY", 0), 2);
		// 库存单位
		parm.addData("UNIT_CODE", result.getValue("STOCK_UNIT", 0));
		parm.addData("PURCH_PRICE", String.valueOf(pur_price));
		parm.addData("STOCK_PRICE", String.valueOf(stock_price));
		parm.addData("RETAIL_PRICE", String.valueOf(retail_price));
		return parm;
	}

	/**
	 * 查询药品信息
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public String getPhaBaseInfo(String orderCode) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getOrderDescByOrderCode(orderCode)));
		if (result.getErrCode() < 0) {
			return "";
		}
		return result.getValue("ORDER_DESC", 0);
	}

	/**
	 * 根据物联网结算数据生成请领单
	 * 
	 * @param closeDate
	 * @param optUser
	 * @param optTerm
	 * @param regionCode
	 * @param conn
	 * @return
	 */
	public TParm createIndRequsestAuto(String closeDate, String optUser,
			String optTerm, String regionCode) {
		// System.out.println("------sql: "+sql);
		// 查询物联网结算数据-部门
		TParm parm = queryOrgCodeInIndAccout(closeDate);
		// 得到请领主档PARM
		int count = parm.getCount();
		TParm result = new TParm();
		if (null != parm && count > 0) {
			for (int i = 0; i < count; i++) {
				String appOrgCode = parm.getValue("ORG_CODE", i);
				String toOrgCode = "040101";
				// 得到请领单号
				String requestNo = SystemTool.getInstance().getNo("ALL", "IND",
						"IND_REQUEST", "No");
				// 创建请领主档
				TParm requestM = createRequestM(toOrgCode, requestNo,
						appOrgCode, optUser, optTerm, regionCode, "1", "1");
				// 生成请领主档
				// System.out.println("---------saveRequestMAutoOfDrug"+INDSQL.saveRequestMAutoOfDrug(requestM));
				result = new TParm(TJDODBTool.getInstance().update(
						INDSQL.createRequestM(requestM)));
				if (result.getErrCode() < 0) {
					return result;
				}
				int seqNo = 1;
				// 查询物联网结算数据
				TParm parmD = queryIndAccout(closeDate, appOrgCode, "");
				// 生成请领明细档
				for (int j = 0; j < parmD.getCount("ORDER_CODE"); j++) {
					String orderCode = parmD.getValue("ORDER_CODE", j);
					double qty = parmD.getDouble("OUT_QTY", j);
					if (qty == 0) {
						continue;
					}
					seqNo = seqNo + j;
					// 得到请领明细档PARM
					TParm requestD = getRequestDAutoOfDrugInfo(requestNo, seqNo
							+ "", orderCode, qty, optUser, optTerm, regionCode);
					// 保存
					// System.out.println("---------saveRequestDAutoOfDrug"+INDSQL.saveRequestDAutoOfDrug(requestD));
					result = new TParm(TJDODBTool.getInstance().update(
							INDSQL.saveRequestDAutoOfDrug(requestD)));
					if (result.getErrCode() < 0) {
						System.out.println(INDSQL
								.saveRequestDAutoOfDrug(requestD));
						return result;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 查询物联网结算数据
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
	 * 查询物联网结算数据-部门
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
	 * 自动创建请领单主档的parm
	 * 
	 * @param toOrgCode
	 * @param requestNo
	 * @param appOrgCode
	 * @param optUser
	 * @param optTerm
	 * @param regionCode
	 * @param drugCategory
	 *            麻精标记 1：普药，
	 * @param applyType
	 *            申请方式 1：手动
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
		result.setData("REASON_CHN_DESC", "自动");
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

	public TParm onSaveIndAccoun() {

		return null;
	}

	/**
	 * 批量扣库
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm onUpdateStockQty(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String opt_user = parm.getValue("OPT_USER");
		String opt_term = parm.getValue("OPT_TERM");
		TParm dataParm = parm.getParm("data");
		if (null != dataParm && dataParm.getCount() > 0) {
			int count = dataParm.getCount();
			for (int i = 0; i < count; i++) {
				String org_code = "";
				double dosage_qty = 0;
				String order_code = "";
				String closeDate = "";
				org_code = dataParm.getValue("ORG_CODE", i);
				order_code = dataParm.getValue("ORDER_CODE", i);
				dosage_qty = dataParm.getDouble("OUT_QTY", i);
				closeDate = dataParm.getValue("CLOSE_DATE", i);
				Timestamp opt_date = SystemTool.getInstance().getDate();
				// System.out.println("------org_code: "+org_code+"--ordercode:"+order_code);
				// 扣库存
				result = reduceIndStock(org_code, order_code, dosage_qty,
						opt_user, opt_date, opt_term, "", connection);
				if (result.getErrCode() != 0) {
					return result;
				}
				// 扣库成功 改变状态
				String sql = INDSQL.updateIndAccount(closeDate, org_code,
						order_code);
				// System.out.println("-------update sql:"+sql);
				result = new TParm(TJDODBTool.getInstance().update(sql,
						connection));
				if (result.getErrCode() != 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 按病区整批配药(住院药房全病区整批配药)-麻精
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm luhai add 加入返回值中加入扣库的的order信息
	 */
	public TParm reduceIndStockByStationOfDrug(TParm parm, TConnection conn) {
		// System.out.println(">>>reduceIndStockByStationOfDrug--2");
		// luhai add 2012-1-28 begin
		TParm returnParm = new TParm();
		// luhai add 2012-1-28 end
		// 数据检核
		if (parm == null) {
			return null;
		}
		// System.out.println("3--------------------parm===" + parm);
		// 结果集
		TParm result = new TParm();
		// 从Config文件中取得自动循环次数
		// int count =
		// Integer.parseInt(TConfig.getSystemValue("CYCLE_TIMES_FOR_ERR"))
		int count = 1;
		String org_code = "";
		String order_code = "";
		double dosage_qty = 0;
		double own_price = 0;
		// i: 控制自动循环次数
		for (int i = 0; i < count; i++) {
			// 查询药品配药信息,拼组更新语句组
			// 更新语句
			String update_sql = "";
			List list = new ArrayList(); // 用于存储SQL语句数组
			List order_list = new ArrayList(); // 用于存储ORDER_CODE
			// j: 药品种类个数
			int parmCount = parm.getCount("ORDER_CODE");
			for (int j = 0; j < parmCount; j++) {
				org_code = parm.getValue("ORG_CODE", j);
				order_code = parm.getValue("ORDER_CODE", j);
				dosage_qty = parm.getDouble("DOSAGE_QTY", j);
				own_price = parm.getDouble("OWN_PRICE", j);
				// 判断是否是麻精-ture 是，false不是
				boolean flg = isDrug(order_code);
				// System.out.println("4---------------------flg:"+flg);
				if (!flg) {
					continue;
				}
				// System.out.println("5----------------------开始扣库------------");
				// zhangyong20101119 begin
				// 检核库存
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

				// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
				TParm parmOrder = IndStockDTool.getInstance()
						.onQueryStockBatchAndQty(org_code, order_code, "");
				// System.out.println("parmOrder=" + parmOrder);
				if (parmOrder.getErrCode() != 0) {
					return parmOrder;
				}

				// 库存量
				double qty = 0;
				// 批次序号
				int batch_seq = 0;
				// 出库数量
				double out_qty = dosage_qty;
				// 出库金额
				double out_amt = 0;
				// 验收价格
				double verifyinPrice = 0;
				int parmOrderCount = parmOrder.getCount();
				for (int k = 0; k < parmOrderCount; k++) {
					qty = parmOrder.getDouble("QTY", k);
					batch_seq = parmOrder.getInt("BATCH_SEQ", k);
					verifyinPrice = parmOrder.getDouble("VERIFYIN_PRICE", k);
					if (qty >= dosage_qty) {
						// 药品可以一次扣库
						out_amt = StringTool.round(own_price * dosage_qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, dosage_qty,
								out_amt, parm.getValue("OPT_USER", j), parm
										.getValue("OPT_DATE", j), parm
										.getValue("OPT_TERM", j));
						// System.out.println("qty>=dosage_qty----" +
						// update_sql);
						// 记录扣库药品
						returnParm.addData("ORDER_CODE", order_code);
						returnParm.addData("DISPENSE_QTY", dosage_qty);
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						break;
					} else {
						// 药品不可一次扣库
						out_amt = StringTool.round(own_price * qty, 2);
						update_sql = INDSQL.getUpdateReduceIndStockSql(
								org_code, order_code, batch_seq, qty, out_amt,
								parm.getValue("OPT_USER", j), parm.getValue(
										"OPT_DATE", j), parm.getValue(
										"OPT_TERM", j));
						// System.out.println("qty<dosage_qty----" +
						// update_sql);
						// 记录扣库药品
						returnParm.addData("ORDER_CODE", order_code);
						returnParm.addData("DISPENSE_QTY", dosage_qty);
						returnParm.addData("BATCH_SEQ", batch_seq);
						returnParm.addData("ORG_CODE", org_code);
						returnParm.addData("VERIFYIN_PRICE", verifyinPrice);
						list.add(update_sql);
						order_list.add(order_code);
						// 更新用量
						dosage_qty = dosage_qty - qty;
					}
				}
			}
			// ********************************************************
			// luhai modify 2012-1-28 将扣除的order信息记录到TParm 中 end
			// ******************************************************
			// 执行拼组后的更新SQL语句组
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
				// System.out.println("------------------------sql["+l+"]:"+sql[l]);
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
		// 若程序未出异常，则返回扣库的药品Parm
		return returnParm;
	}

	/**
	 * 判断是否是麻精
	 * 
	 * @param orderCode
	 * @return true 是，false 否
	 */
	public boolean isDrug(String orderCode) {
		// false 不是麻精
		boolean flg = false;
		String sql = INDSQL.getCountByMj("'" + orderCode + "'");
		// System.out.println("---------isDrug-sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null != parm && parm.getCount() > 0) {
			flg = true;
		}
		return flg;
	}
}
