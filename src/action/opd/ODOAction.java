package action.opd;

import jdo.odo.ODOSaveTool;
import jdo.opd.DiagRecTool;
import jdo.opd.DrugAllergyTool;
import jdo.opd.MedHistoryTool;
import jdo.opd.OPDSubjrecTool;
import jdo.opd.OrderTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.ekt.EKTTool;
import jdo.opb.OPBReceiptTool;
import jdo.spc.IndStockDTool;
import jdo.bil.BILPrintTool;
import com.dongyang.util.StringTool;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import jdo.bil.BILInvoiceTool;

/**
 * 
 * <p>
 * Title: 医生站动作类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui 2008.9.8
 * @version 1.0
 */
public class ODOAction extends TAction {
	/**
	 * 连接
	 */
	TConnection connection;

	/**
	 * 查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		// 得到问诊号
		String caseNo = parm.getValue("CASE_NO");
		if (caseNo.length() == 0)
			return err(-1, "CASE_NO is null");
		// 得到病案号
		String mrNo = parm.getValue("MR_NO");
		if (mrNo.length() == 0)
			return err(-1, "MR_NO is null");
		TParm result = new TParm();
		String admType = parm.getValue("ADM_TYPE");
		// 查询主诉客诉信息
		TParm subjectresult = OPDSubjrecTool.getInstance().query(caseNo,
				admType);

		if (subjectresult.getErrCode() != 0) {

			return err(subjectresult);
		}
		result.setData("SUBJREC", subjectresult.getData());

		// 查询医嘱
		TParm orderresult = OrderTool.getInstance().query(caseNo);
		if (orderresult.getErrCode() != 0) {
			return err(orderresult);
		}
		result.setData("ORDER", orderresult.getData());
		// 查询既往史
		TParm medhistoryparm = new TParm();
		medhistoryparm.setData("MR_NO", mrNo);
		TParm medhistoryresult = MedHistoryTool.getInstance().selectdata(
				medhistoryparm);
		if (medhistoryresult.getErrCode() != 0) {
			return err(medhistoryresult);
		}
		result.setData("MEDHISTORY", medhistoryresult.getData());
		// 查询诊断
		TParm diagrecparm = new TParm();
		diagrecparm.setData("CASE_NO", caseNo);
		TParm diagrecresult = new TParm();
		diagrecresult = DiagRecTool.getInstance().selectdata(diagrecparm);

		if (diagrecresult.getErrCode() != 0) {
			return err(diagrecresult);
		}
		result.setData("DIAGREC", diagrecresult.getData());
		// 查询过敏史
		TParm drugallergyparm = new TParm();
		drugallergyparm.setData("MR_NO", mrNo);
		TParm drugallergyresult = DrugAllergyTool.getInstance().selectdata(
				drugallergyparm);
		if (drugallergyresult.getErrCode() != 0) {
			return err(drugallergyresult);
		}
		result.setData("DRUGALLERGY", drugallergyresult.getData());
		// 查询医嘱历史
		TParm orderhistoryparm = new TParm();
		TParm orderhistoryresult = new TParm();
		// orderhistoryparm.setData("CASE_NO", caseNo);
		// orderhistoryresult = OrderHistoryTool.getInstance().selectdata(
		// orderhistoryparm);
		// if (orderhistoryresult.getErrCode() != 0) {
		// return err(orderhistoryresult);
		// }
		result.setData("ORDERHISTORY", orderhistoryresult.getData());

		return result;
	}

	/**
	 * 门急医生站保存入口
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErrCode(-1);
			result.setErrText("参数错误");
			return result;
		}
		// 取得链接
		TConnection conn = getConnection();
		result = ODOSaveTool.getInstance().onSave(parm, conn);
		if (result.getErrCode() != 0) {
			conn.rollback();
			conn.close();
		}
		conn.commit();
		conn.close();
		ODOSaveTool.getInstance().onCheckOrderLog(parm);
		return result;
	}

	/**
	 * 现金收付：门诊医生站使用 添加收据档数据，如果记账操作添加记账表数据，记账时修改OPD_ORDER 表 PRINT_FLG 和PRINT_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOpbRect(TParm parm) {
		TConnection conn = getConnection();
		String billFlg = parm.getValue("billFlg"); // 是否记账标记
		String contractCode = parm.getValue("CONTRACT_CODE"); // 记账单位
		String cashierCode = parm.getValue("cashierCode"); // 票据创建人
		// 调用存储票据
		TParm result = OPBReceiptTool.getInstance().initReceipt(parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			conn.close();
			return result;
		}
		String recpNo = result.getValue("RECEIPT_NO");
		// 记账操作
		if ("N".equals(billFlg)) {
			parm.setData("RECEIPT_NO", recpNo);
			result = BILPrintTool.getInstance().insertRecode(parm,
					contractCode, conn);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				conn.close();
				return result;

			}

		} else {
			// 不记账执行修改票据数据
			TParm bilInvoice = new TParm();
			bilInvoice.setData("RECP_TYPE", "OPB");
			bilInvoice.setData("STATUS", "0");
			bilInvoice.setData("CASHIER_CODE", cashierCode);
			bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
			String updateNo = StringTool.addString(parm.getValue("PRINT_NO"));

			bilInvoice.setData("UPDATE_NO", updateNo);
			// 调用写票
			result = BILInvoiceTool.getInstance().updateDatePrint(bilInvoice,
					conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}

		}
		// =========此就诊病患，执行记账 OPD_ORDER 表 PRINT_FLG 列更改为N 不执行打票操作，PRINT_NO=""
		TParm upOpdParm = new TParm();
		if ("N".equals(parm.getValue("billFlg"))) {
			upOpdParm.setData("PRINT_FLG", "N");
		} else
			upOpdParm.setData("PRINT_FLG", "Y");

		upOpdParm.setData("RECEIPT_NO", recpNo);
		upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));

		result = OrderTool.getInstance().updateForOPBCash(upOpdParm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}

		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 门诊医生站 医疗卡删除医嘱操作 弹出医疗扣款界面 选择取消按钮撤销医嘱操作
	 * 
	 * @param parm
	 *            =========pangben 2012-01-06
	 * @return
	 */
	public TParm concleDeleteOrder(TParm parm) {
		TConnection conn = getConnection();
		TParm result = OrderTool.getInstance().onInsertForOpbEkt(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 医生站写入时没有rexp_code的后台输出日志
	 * 
	 * @param parm
	 * @return
	 */
	public TParm noRexpCodeLog(TParm parm) {
		System.out.println("REXP_CODE为空:::::::" + parm.getErrText());
		return parm;
	}

	/**
	 * 修改药嘱执行状态
	 * 
	 * @param parm
	 *            yanjing 20130415
	 * @return
	 */
	public TParm updateEXEC_FLG(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空！");
			return result;
		}
		for (int i = 0; i < parm.getCount(); i++) {
			result = OrderTool.getInstance().getFlgUpdateDate(parm.getRow(i),
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 锁库存操作 
	 * ==========pangben 2013-11-7
	 * @param parm
	 * @return
	 */
	public TParm onSaveLockQty(TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
		String sql = "";
		TParm oldOrderParm = parm.getParm("oldOrderParm");// 操作之前数据
		TParm newOrderParm = parm.getParm("newOrderParm");// 执行后数据
		TParm orderParm = new TParm();
		TParm qtyOrderParm =new TParm();
		double newQty = 0.00;//获得当前相同数量
		boolean flg = false;
		if (oldOrderParm.getCount("QTY") > 0) {//数据库中本来存在医嘱
			for (int i = 0; i < oldOrderParm.getCount("QTY"); i++) {
				flg = false;// 比较是否存在
				for (int j = 0; j < newOrderParm.getCount("QTY"); j++) {
					if (oldOrderParm.getValue("EXEC_DEPT_CODE", i).equals(
							newOrderParm.getValue("EXEC_DEPT_CODE", j))
							&& oldOrderParm.getValue("ORDER_CODE", i).equals(
									newOrderParm.getValue("ORDER_CODE", j))) {
						orderParm.addRowData(oldOrderParm, i);// 累计相同的医嘱,之后将操作newOrderParm集合中不相同的数据
						newQty = newOrderParm.getDouble("QTY", j);
						flg = true;
						break;
					}
				}
				if (flg) {// 修改医嘱，新数据中存在LOCK_QTY=LOCK_QTY+原来使用的数量-现在使用的数量
					if (oldOrderParm.getDouble("QTY", i) != newQty) {
//						sql = "UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY+"
//								+ (oldOrderParm.getDouble("QTY", i) - newQty)
//								+ " WHERE ORG_CODE='"
//								+ oldOrderParm.getValue("ORG_CODE", i)
//								+ "' AND ORDER_CODE='"
//								+ oldOrderParm.getValue("ORDER_CODE", i) + "'";
						qtyOrderParm.setData("ORG_CODE", oldOrderParm.getValue("ORG_CODE", i));
						qtyOrderParm.setData("ORDER_CODE", oldOrderParm.getValue("ORDER_CODE", i));
						qtyOrderParm.setData("LOCK_QTY", newQty-oldOrderParm.getDouble("QTY", i));
						result =IndStockDTool.getInstance().updateIndStockLockQty(qtyOrderParm, connection);
//						result = new TParm(TJDODBTool.getInstance().update(sql,
//								connection));
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							connection.rollback();
							connection.close();
							return result;
						}
					}
				} else {// 新数据里不存在此条医嘱 LOCK_QTY=LOCK_QTY+原来使用的数量
//					sql = "UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY+"
//							+ oldOrderParm.getDouble("QTY", i)
//							+ " WHERE ORG_CODE='"
//							+ oldOrderParm.getValue("ORG_CODE", i)
//							+ "' AND ORDER_CODE='"
//							+ oldOrderParm.getValue("ORDER_CODE", i) + "'";
//					result = new TParm(TJDODBTool.getInstance().update(sql,
//							connection));
					qtyOrderParm.setData("ORG_CODE", oldOrderParm.getValue("ORG_CODE", i));
					qtyOrderParm.setData("ORDER_CODE", oldOrderParm.getValue("ORDER_CODE", i));
					qtyOrderParm.setData("LOCK_QTY",-oldOrderParm.getDouble("QTY", i));
					result =IndStockDTool.getInstance().updateIndStockLockQty(qtyOrderParm, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						connection.rollback();
						connection.close();
						return result;
					}
				}
			}
			if (orderParm.getCount("QTY") > 0) {// 存在相同的数据
				for (int i = 0; i < newOrderParm.getCount("QTY"); i++) {
					flg = false;
					for (int j = 0; j < orderParm.getCount("QTY"); j++) {
						if (newOrderParm.getValue("EXEC_DEPT_CODE", i).equals(
								orderParm.getValue("EXEC_DEPT_CODE", j))
								&& newOrderParm.getValue("ORDER_CODE", i)
										.equals(
												orderParm.getValue(
														"ORDER_CODE", j))) {
							flg = true;// 数据相同则不执行任何操作
							break;
						}
					}
					if (!flg) {// 在newOrderParm集合中查找oldOrderParm原来集合中不存在的数据，扣除库存
//						sql = "UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY-"
//								+ newOrderParm.getDouble("QTY", i)
//								+ " WHERE ORG_CODE='"
//								+ newOrderParm.getValue("ORG_CODE", i)
//								+ "' AND ORDER_CODE='"
//								+ newOrderParm.getValue("ORDER_CODE", i) + "'";
//						result = new TParm(TJDODBTool.getInstance().update(sql,
//								connection));
						qtyOrderParm.setData("ORG_CODE", newOrderParm.getValue("ORG_CODE", i));
						qtyOrderParm.setData("ORDER_CODE", newOrderParm.getValue("ORDER_CODE", i));
						qtyOrderParm.setData("LOCK_QTY",newOrderParm.getDouble("QTY", i));
						result =IndStockDTool.getInstance().updateIndStockLockQty(qtyOrderParm, connection);
						if (result.getErrCode() < 0) {
							err("ERR:" + result.getErrCode()
									+ result.getErrText() + result.getErrName());
							connection.rollback();
							connection.close();
							return result;
						}
					}
				}
			} else {// 不存在相同的数据，直接将所有新数据执行扣除库存操作
				for (int i = 0; i < newOrderParm.getCount("QTY"); i++) {
//					sql = "UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY-"
//							+ newOrderParm.getDouble("QTY", i)
//							+ " WHERE ORG_CODE='"
//							+ newOrderParm.getValue("ORG_CODE", i)
//							+ "' AND ORDER_CODE='"
//							+ newOrderParm.getValue("ORDER_CODE", i) + "'";
//					result = new TParm(TJDODBTool.getInstance().update(sql,
//							connection));
					qtyOrderParm.setData("ORG_CODE", newOrderParm.getValue("ORG_CODE", i));
					qtyOrderParm.setData("ORDER_CODE", newOrderParm.getValue("ORDER_CODE", i));
					qtyOrderParm.setData("LOCK_QTY",newOrderParm.getDouble("QTY", i));
					result =IndStockDTool.getInstance().updateIndStockLockQty(qtyOrderParm, connection);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						connection.rollback();
						connection.close();
						return result;
					}
				}
			}
			
		}else{//第一次操作保存医嘱直接扣除库存
			for (int i = 0; i < newOrderParm.getCount("QTY"); i++) {
//				sql = "UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY-"
//						+ newOrderParm.getDouble("QTY", i)
//						+ " WHERE ORG_CODE='"
//						+ newOrderParm.getValue("ORG_CODE", i)
//						+ "' AND ORDER_CODE='"
//						+ newOrderParm.getValue("ORDER_CODE", i) + "'";
//				result = new TParm(TJDODBTool.getInstance().update(sql,
//						connection));
				qtyOrderParm.setData("ORG_CODE", newOrderParm.getValue("ORG_CODE", i));
				qtyOrderParm.setData("ORDER_CODE", newOrderParm.getValue("ORDER_CODE", i));
				qtyOrderParm.setData("LOCK_QTY",newOrderParm.getDouble("QTY", i));
				result =IndStockDTool.getInstance().updateIndStockLockQty(qtyOrderParm, connection);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					connection.rollback();
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
}
