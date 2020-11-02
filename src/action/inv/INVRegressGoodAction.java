package action.inv;

import jdo.inv.INVRegressGoodTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class INVRegressGoodAction extends TAction {

	/**
	 * 保存
	 * */
	public TParm onSave(TParm parms) {
		TParm result = new TParm();
		TParm parm = parms.getParm("INVRegressGood");
		if (parm == null)
			return result.newErrParm(-1, "参数为空");
		TConnection connection = getConnection();
		// 1.将grid的数据写入INV_RETURNHIGH表
		int count = parm.getCount("RETURN_NO");
		for (int i = 0; i < count; i++) {
			result = INVRegressGoodTool.getInstance().insertData(
					parm.getRow(i), connection);
			if (result == null || result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}

		// 2.更新STOCKM STOCKD STOCKDD三张表的数据
		TParm invParm =  parms.getParm("INVStock");
		result = this.onSaveINV(invParm, connection);
		if (result == null || result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	/**
	 * 扣库方法
	 * */
	public TParm onSaveINV(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm.getCount("INV_CODE") < 0) {
			return result;
		}
		// 更新 INV_STOCKM INV_STOCKD INV_STOCKDD表数据
		for (int i = 0; i < parm.getCount("INV_CODE"); i++) {
			if (parm.getData("FLG", i).toString().equals("HIGH")) {
				String inv_code = parm.getData("INV_CODE", i).toString();
				String orgCode = parm.getData("ORG_CODE", i).toString();
				String opt_user = parm.getData("OPT_USER", i).toString();
				String opt_term = parm.getData("OPT_TERM", i).toString();
//				int qty = parm.getInt("QTY", i);
				String qty = parm.getValue("QTY", i);
				
				// step1.根据ORG_CODE,INV_CODE查询INV_STOCKM ORG_CODE 
				String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY "
						+ " FROM INV_STOCKM " + " WHERE ORG_CODE = '" + orgCode + "'"
						+ " AND INV_CODE = '" + inv_code + "'";
				// step2.更新 INV_STOCKM表的字段 stock_qty(-m)
				result = new TParm(TJDODBTool.getInstance().select(selectInvStockM));
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				String sql1 = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"
						+ inv_code + "'";
				TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
				String inv_chn_desc = parm1.getData("INV_CHN_DESC", 0).toString();
				String sql2 = "SELECT ORG_DESC FROM INV_ORG WHERE ORG_CODE = '"
						+ orgCode + "'";
				TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
				String dept_chn_desc = parm2.getData("ORG_DESC", 0).toString();
				if (result.getCount("ORG_CODE") < 0) {
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc + "查无此物资");
					connection.rollback();
					connection.close();
					return result;
					// 查无此物资
				}

				if (Double.parseDouble(result.getData("STOCK_QTY", 0).toString()) < Double.parseDouble(qty)) {
					// 库存不足
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc + "库存不足");
					connection.rollback();
					connection.close();
					return result;
				}

				TParm updInvStockMParm = new TParm();
				updInvStockMParm.setData("ORG_CODE", orgCode);
				updInvStockMParm.setData("INV_CODE", inv_code);
				updInvStockMParm.setData("QTY", qty);
				updInvStockMParm.setData("OPT_USER", opt_user);
				updInvStockMParm.setData("OPT_TERM", opt_term);
				result = INVRegressGoodTool.getInstance().updInvStockM(
						updInvStockMParm, connection);

				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				// step3.根据 ORG_CODE,INV_CODE查询INV_STOCKD 根据VALID_DATE升序排序
				String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"
						+ inv_code
						+ "' AND ORG_CODE = '"
						+ orgCode
						+ "' ORDER BY VALID_DATE";
				result = new TParm(TJDODBTool.getInstance().select(selectInvStockD));
				if (result.getErrCode() < 0) {
					result.setErrText("查询INV_STOCKD表失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("ORG_CODE") < 0) {
					// 查无数据
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc + "查无此物资");
					connection.rollback();
					connection.close();
					return result;
				}
				result = saveInvStockD(result, Double.parseDouble(qty), connection, opt_user, opt_term);
				// step5.查询INV_STOCKDD 根据RFID查询 ，只有一条数据
				String selectInvStockDD = "SELECT INV_CODE,INVSEQ_NO,RFID,STOCK_QTY FROM INV_STOCKDD WHERE INV_CODE='"
						+ inv_code
						+ "' AND RFID = '"
						+ parm.getData("RFID", i)
						+ "'";
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockDD));
				if (result.getErrCode() < 0) {
					result.setErrText("查询INV_STOCKDD表失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("INV_CODE") < 0) {
					result.setErrText("查无条码号：" + parm.getData("RFID", i));
					// 查无数据
					connection.rollback();
					connection.close();
					return result;
				}
				TParm updInvStockDDParm = new TParm();
				updInvStockDDParm.setData("RFID", parm.getData("RFID", i));
				// step6.根据INV_CODE 扣库 更新如下字段
				INVRegressGoodTool.getInstance().updInvStockDD(updInvStockDDParm,
						connection);
				// -------------------------低值-----------------------
			} else if (parm.getData("FLG", i).toString().equals("LOW")) {
				String inv_code = parm.getData("INV_CODE", i).toString();
				String orgCode = parm.getData("ORG_CODE", i).toString();
				String opt_user = parm.getData("OPT_USER", i).toString();
				String opt_term = parm.getData("OPT_TERM", i).toString();
				Double qty = parm.getDouble("QTY", i);// 扣库数量
				// step1.根据ORG_CODE,INV_CODE查询INV_STOCKM ORG_CODE 高值为介入室
				String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY "
						+ " FROM INV_STOCKM " + " WHERE ORG_CODE = '" + orgCode
						+ "' AND INV_CODE = '" + inv_code + "' ";// ORG_CODE暂时写死
				// step2.更新 INV_STOCKM表的字段 stock_qty(-m)
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockM));
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
				}
				String sql1 = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"
						+ inv_code + "'";
				TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
				String inv_chn_desc = parm1.getData("INV_CHN_DESC", 0)
						.toString();
				String sql2 = "SELECT ORG_DESC FROM INV_ORG WHERE ORG_CODE = '"
						+ orgCode + "'";
				TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
				String dept_chn_desc = parm2.getData("ORG_DESC", 0)
						.toString();
				if (result.getCount("ORG_CODE") < 0) {
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "查无此物资");
					connection.rollback();
					connection.close();
					return result;
					// 查无此物资
				}
				if (Double.parseDouble(result.getData("STOCK_QTY", 0)
						.toString()) < qty) {
					// 库存不足
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "库存不足");
					connection.rollback();
					connection.close();
					return result;
				}
				TParm updInvStockMParm = new TParm();
				updInvStockMParm.setData("ORG_CODE", orgCode);
				updInvStockMParm.setData("INV_CODE", inv_code);
				updInvStockMParm.setData("QTY", qty);
				updInvStockMParm.setData("OPT_USER", opt_user);
				updInvStockMParm.setData("OPT_TERM", opt_term);
				result = INVRegressGoodTool.getInstance().updInvStockM(
						updInvStockMParm, connection);
				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				// step3.根据 ORG_CODE,INV_CODE查询INV_STOCKD 根据VALID_DATE升序排序
				String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"
						+ inv_code
						+ "' AND ORG_CODE = '"
						+ orgCode
						+ "' ORDER BY VALID_DATE";
				result = new TParm(TJDODBTool.getInstance().select(
						selectInvStockD));
				if (result.getErrCode() < 0) {
					result.setErrText("保存失败！");
					connection.rollback();
					connection.close();
					return result;
				}
				if (result.getCount("ORG_CODE") < 0) {
					// 查无数据
					result.setErrCode(-2);
					result.setErrText(dept_chn_desc + "的" + inv_chn_desc
							+ "查无此物资");
					connection.rollback();
					connection.close();
					return result;
				}
				result = saveInvStockD(result, qty, connection, opt_user,
						opt_term);
			}
		}

		return result;

	}

	/**
	 * 循环扣inv_stockd d 为要扣的数量
	 * */
	public TParm saveInvStockD(TParm invSockDParm, double d,
			TConnection connection, String opt_user, String opt_term) {

		double stockQty = invSockDParm.getDouble("STOCK_QTY", 0);
		if (stockQty >= d) {//库存量大于扣库量
			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), d, connection,
					opt_user, opt_term, d);
		} else {
			//例如 输入100  实际库存60

			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0),
					invSockDParm.getValue("INV_CODE", 0), invSockDParm
							.getValue("BATCH_SEQ", 0), stockQty, connection,
					opt_user, opt_term, stockQty);
			d = d - stockQty;
			if (invSockDParm.getCount("ORG_CODE") > 0) {
				invSockDParm.removeRow(0);
				this.saveInvStockD(invSockDParm, d, connection, opt_user,
						opt_term);
			} else {
				return invSockDParm;
			}
		}
		return invSockDParm;
	}

	/**
	 * 扣库方法 org inv batch_seq qty
	 * */
	public TParm updateInvStockD(String org, String inv, String batch_seq,
			double qty, TConnection connection, String opt_user,
			String opt_term, Double d) {
		TParm result = new TParm();
		// String updInvStockD =
		// "UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY - "+qty+" WHERE ORG_CODE = '"+org+"' AND INV_CODE = '"+inv+"' AND BATCH_SEQ = '"+batch_seq+"'";
		// System.out.println("updateInvStockD执行。。。。");
		TParm updInvStockDParm = new TParm();
//		updInvStockDParm.setData("STOCK_QTY", d);
		updInvStockDParm.setData("QTY", qty);
		updInvStockDParm.setData("ORG_CODE", org);
		updInvStockDParm.setData("INV_CODE", inv);
		updInvStockDParm.setData("BATCH_SEQ", batch_seq);
		updInvStockDParm.setData("OPT_USER", opt_user);
		updInvStockDParm.setData("OPT_TERM", opt_term);
		result = INVRegressGoodTool.getInstance().updInvStockD(updInvStockDParm,
				connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
}
