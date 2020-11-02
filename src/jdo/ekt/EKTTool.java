package jdo.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

import jdo.mem.MEMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ҽ�ƿ�������
 * </p>
 * 
 * <p>
 * Description: ҽ�ƿ�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author sundx
 * @version 1.0
 */
public class EKTTool extends TJDOTool {

	/**
	 * ������
	 */
	
	public EKTTool() {
		setModuleName("ekt\\EKTModule.x");
		onInit();
	}

	/**
	 * ʵ��
	 */
	private static EKTTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTTool
	 */
	public static EKTTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTTool();
		return instanceObject;
	}

	/**
	 * �õ����׺�
	 * 
	 * @return String
	 */
	public String getBusinessNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO",
				"BUSINESS_NO");
	}

	/**
	 * �õ�ҽ�ƿ���ֵ��
	 * 
	 * @return String =================pangben 20111008
	 */
	public String getBillBusinessNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "BIL_BUSINESS_NO",
				"BIL_BUSINESS_NO");
	}

	/**
	 * �õ�ҽ�ƿ��ⲿ���׺�
	 * 
	 * @return String ================pangben 20110927
	 */
	public String getTradeNo() {
		return SystemTool.getInstance().getNo("ALL", "EKT", "TRADE_NO",
				"TRADE_NO");
	}

	/**
	 * д��ҽ�ƿ����ױ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insetTrade(TParm parm, TConnection connection) {
		// =====zhangp 20120224 modify start
		parm.setData("BANK_FLG", "N");
		// =====zhangp 20120224 modify end
		TParm result = update("insetTrade", parm, connection);
		return result;
	}

	/**
	 * д��ҽ�ƿ���ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("insertEKTDetail", parm, connection);
		return result;
	}

	/**
	 * ȷ�Ͻ���
	 * 
	 * @param TradeNo
	 *            String
	 * @param connection
	 *            TConnection
	 * @return boolean
	 */
//	public boolean consumeConfirmation(String TradeNo, TConnection connection) {
//		TParm parm = new TParm();
//		parm.setData("Trade_NO", TradeNo);
//		parm.setData("STATE", 1);
//		TParm result = update("updateTrade", parm, connection);
//		if (result.getErrCode() != 0) {
//			System.out.println("EKTTool.consumeConfirmation(" + TradeNo + ")->"
//					+ result.getErrText());
//			return false;
//		}
//		return true;
//	}

	public TParm queryTradeNoCancel(TParm parm) {
		return query("queryTradeNoCancel", parm);
	}

	/**
	 * �˻�;�н���
	 * 
	 * @param cardNo
	 *            String
	 * @return boolean
	 */
	public boolean checkCancel(String cardNo) {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		TParm result = queryTradeNoCancel(parm);
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			String TradeNo = result.getValue("Trade_NO", i);
			consumeCancel(TradeNo);
		}
		return true;
	}

	public String check(String TradeNo, String caseNo,double reduceAmt) {
		TParm parm = new TParm();
		// parm.setData("Trade_NO", TradeNo);
		parm.setData("CASE_NO", caseNo);
		TParm result = queryTrade(parm);
//		DecimalFormat sfd= new DecimalFormat("######0.00");
		double amt = result.getDouble("AMT", 0)+reduceAmt;
		amt = StringTool.round(amt, 2);
//		double amt = result.getDouble("AMT", 0);
		TParm parm3 = query("sumOpbdetailCaseNo", parm);
		double d2 = parm3.getDouble("AR_AMT", 0);
		if (amt != d2) {
			String s = StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyy/MM/dd HH:mm:ss");
			s = s + "ҽ�ƿ����״��󣺽��׺�" + TradeNo + " ҽ�ƿ����׽�����ҽ���ܽ��!" + amt + " "
					+ d2;
			//System.out.println(s);
			return s;
		}
		
		parm3=queryOpdOrderError(parm);//====pangben 2013-4-27 
		if(parm3.getCount()>0){
			String s = StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyy/MM/dd HH:mm:ss");
			s = s + ",ҽ�ƿ����׺����������!";
			//System.out.println(s);
			return s;
		}
		return "";
	}

	public double ss(double amt) {
		if (amt > 0)
			amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
		else if (amt < 0)
			amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
		return amt;
	}

	/**
	 * ȡ������
	 * 
	 * @param TradeNo
	 *            String
	 * @return boolean
	 */
	public boolean consumeCancel(String TradeNo) {
		TParm parm = new TParm();
		parm.setData("Trade_NO", TradeNo);
		parm.setData("STATE", 2);
		TConnection connection = getConnection();
		TParm result = update("updateTrade", parm, connection);
		if (result.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return false;
		}
		result = queryTrade(parm);
		if (result.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ").queryTrade->" + result.getErrText());
			return false;
		}
		String businessNo = result.getValue("BUSINESS_NO", 0);
		parm.setData("BUSINESS_NO", businessNo);
		parm = queryAccntdetail(parm);
		if (parm.getErrCode() != 0) {
			connection.rollback();
			connection.close();
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ").queryTrade->" + parm.getErrText());
			return false;
		}
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			TParm p = new TParm();
			p.setData("BUSINESS_SEQ", parm.getData("BUSINESS_SEQ", i));
			p.setData("BUSINESS_NO", businessNo);
			p.setData("CHARGE_FLG",
					"1".equals(parm.getValue("CHARGE_FLG", i)) ? 2 : 1);
			result = updateAccntdetail(p, connection);
			if (result.getErrCode() != 0) {
				connection.rollback();
				connection.close();
				// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
				// ").for" + i + "->" + result.getErrText());
				return false;
			}
		}
		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * ҽ�ƿ��˷Ѳ���
	 * 
	 * @param TradeNo
	 * @param connection
	 * @return
	 */
	public TParm consumeCancelOne(TParm parm, String TradeNo, int type) {
		String sql = "UPDATE EKT_Trade SET STATE = " + type + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE=SYSDATE,OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' WHERE Trade_NO in (" + TradeNo
				+ ")";
		// System.out.println("consumeCancelOne ���� sql::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return result;
		}
		// result = queryTrade(parm);
		// if(result.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
		// ").queryTrade->" + result.getErrText());
		// return result;
		// }
		// String businessNo = result.getValue("BUSINESS_NO",0);
		// parm.setData("BUSINESS_NO",businessNo);
		// parm = queryAccntdetail(parm);
		// if(parm.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
		// ").queryTrade->" + parm.getErrText());
		// return result;
		// }
		// int count = parm.getCount();
		// for(int i = 0;i < count;i++)
		// {
		// TParm p = new TParm();
		// p.setData("BUSINESS_SEQ",parm.getData("BUSINESS_SEQ",i));
		// p.setData("BUSINESS_NO",businessNo);
		// p.setData("CHARGE_FLG","1".equals(parm.getValue("CHARGE_FLG",i))?2:1);
		// result = updateAccntdetail(p,connection);
		// if(result.getErrCode() != 0)
		// {
		// System.out.println("EKTTool.consumeConfirmation(" + TradeNo + ").for"
		// + i +
		// "->" + result.getErrText());
		// return result;
		// }
		// }
		return result;
	}

	/**
	 * ҽ�ƿ��˷Ѳ���
	 * 
	 * @param TradeNo
	 * @param connection
	 * @return
	 */
	public TParm consumeCancelOne(TParm parm, String TradeNo, int type,
			TConnection connection) {
		String sql = "UPDATE EKT_Trade SET STATE = " + type + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "',OPT_DATE=SYSDATE,OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "' WHERE Trade_NO in (" + TradeNo
				+ ")";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() != 0) {
			// System.out.println("EKTTool.consumeConfirmation(" + TradeNo +
			// ")->" + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ����CASE_NO ��� Trade_NO
	 * 
	 * @param parm
	 * @return =========pangben 2011-11-17
	 */
	public TParm selectTradeNo(TParm parm) {
		return query("selectTradeNo", parm);
	}

	/**
	 * ��ѯ������ϸ״̬����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryAccntdetail(TParm parm) {
		return query("queryAccntdetail", parm);
	}

	/**
	 * ��ѯ�ڲ����׺�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryTrade(TParm parm) {
		return query("queryTrade", parm);
	}

	/**
	 * ���½�����ϸ״̬����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateAccntdetail(TParm parm, TConnection connection) {
		return update("updateAccntdetail", parm, connection);
	}

	/**
	 * д��ҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("insertEKTMaster", parm, connection);
		return result;
	}

	/**
	 * ҽ�ƿ������п�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTAndBank(TParm parm) {
		TParm result = update("updateEKTAndBank", parm);
		return result;
	}

	/**
	 * ҽ�ƿ��շ�������Ʊ���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTradePrint(TParm parm, TConnection connection) {
		TParm result = update("updateTradePrint", parm, connection);
		return result;
	}

	/**
	 * ɾ��ҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("deleteEKTMaster", parm, connection);
		return result;
	}

	/**
	 * ����ҽ�ƿ���ϸ����˱��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("updateEKTDetail", parm, connection);
		return result;
	}

	/**
	 * ��ѯ��ϸ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getDetail(TParm parm) {
		return query("getDetail", parm);
	}

	/**
	 * ȡ���շ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm detailCancel(TParm parm, TConnection connection) {
		TParm result = update("detailCancel", parm, connection);
		return result;
	}

	/**
	 * ����ʧ��ɾ��ҽ�ƿ���ϸ�����
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDetail(TParm parm, TConnection connection) {
		TParm result = update("deleteDetail", parm, connection);
		return result;
	}

	/**
	 * ����ҽ�ƿ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTMaster(TParm parm, TConnection connection) {
		TParm result = update("updateEKTMaster", parm, connection);
		return result;
	}

	/**
	 * ����ҽ�ƿ�����״̬���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm confirmEKTDetail(TParm parm, TConnection connection) {
		TParm result = update("confirmEKTDetail", parm, connection);
		return result;
	}

	/**
	 * ȡ��ҽ�ƿ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getEKTCurrentBalance(TParm parm) {
		TParm result = query("getEKTCurrentBalance", parm);
		return result;
	}

	/**
	 * �õ�����ҽ�ƿ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxBalance(TParm parm) {
		TParm result = query("getRxBalance", parm);
		return result;
	}

	/**
	 * �õ�����ҽ�ƿ�ԭ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxOriginal(TParm parm) {
		TParm result = query("getRxOriginal", parm);
		return result;
	}

	/**
	 * ȡ�ù涨ʱ�����ҽ�ƿ����׽��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getEKTBusinessAmt(TParm parm) {
		TParm result = query("getEKTBusinessAmt", parm);
		return result;
	}

	/**
	 * �õ�MrNo
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMrNo(TParm parm) {
		return query("getMrNo", parm);
	}

	/**
	 * �õ���Ƭ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// zhangp 20120106 ע��
	// public TParm getCardNo(TParm parm)
	// {
	// return query("getCardNo",parm);
	// }
	/**
	 * �õ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getRxAmt(TParm parm) {
		return query("getRxAmt", parm);
	}

	/**
	 * ҽ�ƿ����ѽӿڲ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm setEKTPayParm(TParm parm) {
		if (parm == null || parm.getCount("MR_NO") <= 0)
			return null;
		TParm result = new TParm();
		TParm eKTParmD = new TParm();
		TParm eKTParmM = new TParm();
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			eKTParmD.addData("REGION_CODE", parm.getValue("REGION_CODE", i));
			eKTParmD.addData("MR_NO", parm.getValue("MR_NO", i));
			eKTParmD.addData("CASE_NO", parm.getValue("CASE_NO", i));
			eKTParmD.addData("BUSINESS_SEQ", "" + (i + 1));
			eKTParmD.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			eKTParmD.addData("RX_NO", parm.getValue("RX_NO", i));
			eKTParmD.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			eKTParmD.addData("CHARGE_FLG", "1");
			eKTParmD.addData("BUSINESS_AMT", parm.getValue("OWN_AMT", i));
			eKTParmD.addData("CASHIER_CODE", parm.getValue("OPT_USER", i));
			eKTParmD.addData("BUSINESS_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("ACCNT_STATUS", "1");
			eKTParmD.addData("ACCNT_USER", "");
			eKTParmD.addData("ACCNT_DATE", "");
			eKTParmD.addData("OPT_USER", parm.getValue("OPT_USER", i));
			eKTParmD.addData("OPT_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// ����
			eKTParmD.addData("CARD_NO", "001");
			// ������ˮ��
			eKTParmD.addData("BUSINESS_NO", "001");
			// ����ǰ���
			eKTParmD.addData("ORIGINAL_BALANCE", "66");
			// ���׺����
			eKTParmD.addData("CURRENT_BALANCE", "66");
			// ����״̬
			eKTParmD.addData("BUSINESS_STATUS", "2");
		}
		// ����
		eKTParmM.setData("CARD_NO", "001");
		// �������֤��
		eKTParmM.setData("ID_NO", "120102189311083334");
		// ��������
		eKTParmM.setData("NAME", "����");
		// ���׺����
		eKTParmM.setData("CURRENT_BALANCE", "66");
		eKTParmM.setData("CREAT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_DATE", parm.getData("OPT_DATE", 0));
		eKTParmM.setData("OPT_TERM", parm.getValue("OPT_TERM", 0));
		result.setData("EKT_MASTER", eKTParmM.getData());
		result.setData("EKT_ACCNTDETAIL", eKTParmD.getData());
		return result;
	}

	/**
	 * ҽ�ƿ����ѽӿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTPay(TParm parm, TConnection connection) {
		TParm eKTParm = setEKTPayParm(parm);
		TParm result = new TParm();
		if (eKTParm == null) {
			result.setErr(-1, "��������Ϊ�գ�");
			return result;
		}
		// �õ�����ҽ�ƿ���������
		TParm eKTParmM = eKTParm.getParm("EKT_MASTER");
		// ȡ��ҽ�ƿ����
		result = EKTTool.getInstance().getEKTCurrentBalance(eKTParmM);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ��˴˿��Ƿ��Ѿ����ѹ�(�������Ƿ������ݺ�ҽ�ƿ�)
		if (result.getCount("CURRENT_BALANCE") <= 0) {
			// д��ҽ�ƿ�����
			result = EKTTool.getInstance()
					.insertEKTMaster(eKTParmM, connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		} else {
			// д��ҽ�ƿ�����
			result = EKTTool.getInstance()
					.updateEKTMaster(eKTParmM, connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// �õ�����ҽ�ƿ���ϸ������
		TParm eKTParmD = eKTParm.getParm("EKT_ACCNTDETAIL");
		// д��ҽ�ƿ���ϸ��
		for (int i = 0; i < eKTParmD.getCount("MR_NO"); i++) {
			result = EKTTool.getInstance().insertEKTDetail(eKTParmD.getRow(i),
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ҽ�ƿ��˷ѷѽӿڲ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm setEKTRefundParm(TParm parm) {
		if (parm == null || parm.getCount("MR_NO") <= 0)
			return null;
		TParm result = new TParm();
		TParm eKTParmD = new TParm();
		TParm eKTParmM = new TParm();
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			eKTParmD.addData("REGION_CODE", parm.getValue("REGION_CODE", i));
			eKTParmD.addData("MR_NO", parm.getValue("MR_NO", i));
			eKTParmD.addData("CASE_NO", parm.getValue("CASE_NO", i));
			eKTParmD.addData("BUSINESS_SEQ", "" + (i + 1));
			eKTParmD.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
			eKTParmD.addData("RX_NO", parm.getValue("RX_NO", i));
			eKTParmD.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
			eKTParmD.addData("CHARGE_FLG", "2");
			eKTParmD.addData("BUSINESS_AMT", parm.getValue("OWN_AMT", i));
			eKTParmD.addData("CASHIER_CODE", parm.getValue("OPT_USER", i));
			eKTParmD.addData("BUSINESS_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("ACCNT_STATUS", "1");
			eKTParmD.addData("ACCNT_USER", "");
			eKTParmD.addData("ACCNT_DATE", "");
			eKTParmD.addData("OPT_USER", parm.getValue("OPT_USER", i));
			eKTParmD.addData("OPT_DATE", parm.getData("OPT_DATE", i));
			eKTParmD.addData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// ����
			eKTParmD.addData("CARD_NO", "001");
			// ������ˮ��
			eKTParmD.addData("BUSINESS_NO", "001");
			// ����ǰ���
			eKTParmD.addData("ORIGINAL_BALANCE", "66");
			// ���׺����
			eKTParmD.addData("CURRENT_BALANCE", "66");
			// ����״̬
			eKTParmD.addData("BUSINESS_STATUS", "2");
		}
		// ����
		eKTParmM.setData("CARD_NO", "001");
		// ���׺����
		eKTParmM.setData("CURRENT_BALANCE", "123");
		eKTParmM.setData("OPT_USER", parm.getValue("OPT_USER", 0));
		eKTParmM.setData("OPT_DATE", parm.getData("OPT_DATE", 0));
		eKTParmM.setData("OPT_TERM", parm.getValue("OPT_TERM", 0));
		result.setData("EKT_MASTER", eKTParmM.getData());
		result.setData("EKT_ACCNTDETAIL", eKTParmD.getData());
		return result;
	}

	/**
	 * ҽ�ƿ��˷ѷ���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTRefund(TParm parm, TConnection connection) {
		TParm eKTParm = setEKTRefundParm(parm);
		TParm result = new TParm();
		if (eKTParm == null) {
			result.setErr(-1, "��������Ϊ�գ�");
			return result;
		}
		// �õ�����ҽ�ƿ���������
		TParm eKTParmM = eKTParm.getParm("EKT_MASTER");
		// д��ҽ�ƿ�����
		result = EKTTool.getInstance().updateEKTMaster(eKTParmM, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// �õ�����ҽ�ƿ���ϸ������
		TParm eKTParmD = eKTParm.getParm("EKT_ACCNTDETAIL");
		// д��ҽ�ƿ���ϸ��
		for (int i = 0; i < eKTParmD.getCount("MR_NO"); i++) {
			result = EKTTool.getInstance().insertEKTDetail(eKTParmD.getRow(i),
					connection);
			if (result.getErrCode() < 0) {
				err("ERR:M " + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ҽ�ƿ�����ȷ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTConfirm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������Ϊ�գ�");
			return result;
		}
		// д��ҽ�ƿ���ϸ��
		result = EKTTool.getInstance().confirmEKTDetail(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ҽ�ƿ����˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm eKTAccnt(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������Ϊ�գ�");
			return result;
		}
		// д��ҽ�ƿ���ϸ��
		parm.setData("ACCNT_USER", parm.getData("OPT_USER"));
		parm.setData("ACCNT_DATE", parm.getData("OPT_DATE"));
		result = EKTTool.getInstance().updateEKTDetail(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���ҽ�ƿ���ֵ�˿
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTBilPay(TParm parm, TConnection connection) {
		if(null==parm.getValue("PRINT_NO")||parm.getValue("PRINT_NO").length()<=0){
			parm.setData("PRINT_NO","");
		}
		TParm result = update("insertEKTBilPay", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ���ҽ�ƿ�������¼��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertEKTIssuelog(TParm parm, TConnection connection) {
		// System.out.println("parm==="+parm);
		TParm result = update("insertEKTIssuelog", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ҽ�ƿ��޸�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateEKTPWD(TParm parm) {
		TParm result = update("updateEKTPWD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * ��ȡҽ�ƿ���Ϣ
	 * 
	 * @return TParm
	 */
	public TParm selectEKTIssuelog(TParm parm) {
		TParm result = query("selectEKTIssuelog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * �޸�ҽ�ƿ������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateEKTIssuelog(TParm parm, TConnection connection) {
		TParm result = update("updateEKTIssuelog", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ɾ�������������շѳ��ִ���ʹ��
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteTrade(TParm parm, TConnection connection) {
		TParm result = update("deleteTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ҽ�ƿ�����ȷ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPWDEKTIssuelog(TParm parm) {
		TParm result = query("selectPWDEKTIssuelog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����ҽ��վ��EKT_Trade ���е�������������״̬ͨ�������ɾ��
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteOpbTrade(TParm parm) {
		TParm result = update("deleteOpbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ҽ�ƿ����׼�¼�ܽ�� zhangp 20121219
	 */
	public TParm selectEKTTradeTotal(TParm parm) {
		TParm result = query("selectEKTTradeTotal", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ҽ�ƿ����׼�¼��ϸ��� zhangp 20121219
	 */
	public TParm selectEKTTradeDetail(TParm parm) {
		TParm result = query("selectEKTTradeDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ҽ�ƿ��ս� ====zhangp 20111229
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm executeEKTAccount(TParm parm, TConnection connection) {
		TParm resultAll = new TParm();
		TParm result = new TParm();
		// double amt = 0.00;
		TParm selDateAccountInParm = new TParm();
		selDateAccountInParm.setData("BUSINESS_DATE", parm
				.getData("BUSINESS_DATE"));
		selDateAccountInParm.setData("OPT_USER", parm.getData("OPT_USER"));
		// =======zhangp 20120226 modify start
		selDateAccountInParm
				.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		// ========zhangp 20120226 modify end

		// ��������
		selDateAccountInParm.setData("CHARGE_FLG", "4");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("BUY_QTY", result.getData("COUNT", 0));
		int buy_qty = result.getInt("COUNT", 0);
		// double buy_amt =result.getDouble("SUM", 0);

		// ��������
		selDateAccountInParm.setData("CHARGE_FLG", "8");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("CHANGE_QTY", result.getData("COUNT", 0));
		int chang_qty = result.getInt("COUNT", 0);
		// ====zhangp 20120226 modify start
		String sql = "SELECT FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '8'";
		TParm singleFact = new TParm(TJDODBTool.getInstance().select(sql));
		if (singleFact.getCount() < 0) {
			// System.out.println("����ԭ�������Ѳ�����");
			return singleFact;
		}
		double chang_amt = chang_qty * singleFact.getDouble("FACTORAGE_FEE", 0);
		// =====zhangp 20120226 modify end
		// ��������
		selDateAccountInParm.setData("CHARGE_FLG", "5");
		result = query("selectEktaccntDetailByChargeFlag",
				selDateAccountInParm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
		}
		resultAll.addData("ADD_QTY", result.getData("COUNT", 0));
		// double add_amt =result.getDouble("SUM", 0);
		int add_qty = result.getInt("COUNT", 0);

		// ��������
		int sent_count = buy_qty + chang_qty + add_qty;
		resultAll.addData("SENT_COUNT", sent_count);

		// ��������
		// ===zhangp 20120314 start
		// int factorage_qty = sent_count;
		int factorage_qty = chang_qty;
		// ===zhangp 20120314 end
		// =======zhangp 20120226 modify start
		double factorage_amt = chang_amt;
		// ========zhangp 20120226 modify end
		resultAll.addData("FACTORAGE_QTY", factorage_qty);
		resultAll.addData("FACTORAGE_AMT", factorage_amt);

		// �ֽ����
		selDateAccountInParm.setData("CHARGE_FLG", "3");
		result = query("selectEktaccntDetailSum", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
			result.setData("SUM", 0, 0);
		}
		resultAll.addData("PAY_MEDICAL_QTY", result.getData("COUNT", 0));
		Integer pay_medical_qty = new Integer(result.getData("COUNT", 0)
				.toString());
		double pay_medical_atm = StringTool.getDouble(result.getData("SUM", 0)
				.toString());
		// =======zhangp 20120226 modify start
		// selDateAccountInParm.setData("CHARGE_FLG", "3");
		// result = query("selectEktaccntDetailSum",
		// selDateAccountInParm,connection);
		// if (result.getErrCode() < 0) {
		// err(result.getErrName() + " " + result.getErrText());
		// return result;
		// }
		// if (result.getCount() < 1) {
		// result.setData("COUNT", 0 , 0);
		// result.setData("SUM", 0 , 0);
		// }
		// pay_medical_qty = pay_medical_qty + result.getInt("COUNT", 0);
		// pay_medical_atm = pay_medical_atm + result.getDouble("SUM",0);
		// =======zhangp 20120226 modify end
		resultAll.addData("PAY_MEDICAL_ATM", pay_medical_atm);
		// System.out.println(""+pay_medical_atm+result);
		// �˿������
		selDateAccountInParm.setData("CHARGE_FLG", "7");
		result = query("selectEktaccntDetailSum", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		if (result.getCount() < 1) {
			result.setData("COUNT", 0, 0);
			result.setData("SUM", 0, 0);
		}
		int npay_medical_qty = StringTool.getInt(result.getData("COUNT", 0)
				.toString());
		double npay_medical_amt = StringTool.getDouble(result.getData("SUM", 0)
				.toString());
		resultAll.addData("NPAY_MEDICAL_QTY", npay_medical_qty);
		resultAll.addData("NPAY_MEDICAL_AMT", npay_medical_amt);

		// ʵ�ս��
		// ===zhangp 20120314 start
		// double ar_amt = pay_medical_atm - npay_medical_amt;
		double ar_amt = pay_medical_atm - npay_medical_amt + factorage_amt;
		// ===zhangp 20120314 end
		TParm upREGRecp = new TParm();
		upREGRecp.setData("ACCOUNT_SEQ", parm.getData("ACCOUNT_SEQ"));
		upREGRecp.setData("ACCOUNT_USER", parm.getData("ACCOUNT_USER"));
		upREGRecp.setData("ACCOUNT_DATE", parm.getData("ACCOUNT_DATE"));
		upREGRecp.setData("FACTORAGE_AMT", factorage_amt);
		upREGRecp.setData("FACTORAGE_QTY", factorage_qty);
		upREGRecp.setData("ACCOUNT_TYPE", parm.getData("ACCOUNT_TYPE"));
		upREGRecp.setData("BUY_QTY", buy_qty);
		upREGRecp.setData("CHANGE_QTY", chang_qty);
		upREGRecp.setData("ADD_QTY", add_qty);
		upREGRecp.setData("SENT_COUNT", sent_count);
		upREGRecp.setData("FACTORAGE_QTY", factorage_qty);
		upREGRecp.setData("FACTORAGE_AMT", factorage_amt);
		upREGRecp.setData("PAY_MEDICAL_ATM", pay_medical_atm);
		upREGRecp.setData("PAY_MEDICAL_QTY", pay_medical_qty);
		upREGRecp.setData("NPAY_MEDICAL_QTY", npay_medical_qty);
		upREGRecp.setData("NPAY_MEDICAL_AMT", npay_medical_amt);
		upREGRecp.setData("AR_AMT", ar_amt);
		upREGRecp.setData("STATUS", "0");
		upREGRecp.setData("REGION_CODE", parm.getData("REGION_CODE"));
		upREGRecp.setData("OPT_USER", parm.getData("OPT_USER"));
		upREGRecp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		upREGRecp.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		//====��ѯ���޻�Ա���ݣ�yanjing 20140528 start
		TParm memTradeResult = new TParm();
		TParm memPakageResult = new TParm();
		TParm memGiftResult = new TParm();
		memTradeResult = MEMTool.getInstance().selectMemTradeData(parm);//��Ա��
		memPakageResult = MEMTool.getInstance().selectMemPackageData(parm);//�ײ�
		memGiftResult = MEMTool.getInstance().selectMemGiftCardData(parm);//��Ʒ��
		//====��ѯ���޻�Ա���ݣ�yanjing 20140528 end
		// ���ȫΪ0 �����������ս� 20120131 zhangp
		if (factorage_amt == 0 && factorage_qty == 0 && buy_qty == 0
				&& chang_qty == 0 && add_qty == 0 && sent_count == 0
				&& factorage_qty == 0 && factorage_amt == 0
				&& pay_medical_atm == 0 && pay_medical_qty == 0
				&& npay_medical_qty == 0 && npay_medical_amt == 0
				&& ar_amt == 0&&memTradeResult.getCount()<=0
				&& memPakageResult.getCount()<=0&&memGiftResult.getCount()<=0) {//======20140528  yanjing ע
			result = new TParm();
			result.setErrCode(-1);
			result.setErrText("���ս�����");
			return result;
		}
		selDateAccountInParm.setData("CASHIER_CODE", parm
				.getData("ACCOUNT_USER"));
		result = update("updateEktAccntDetailStatus", selDateAccountInParm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		//=====yanjing 20140513 ��ѯ�����ս������Ƿ����
		
		String ektAccountSql = "SELECT ACCOUNT_USER FROM EKT_ACCOUNT WHERE ACCOUNT_SEQ = '"+upREGRecp.getValue("ACCOUNT_SEQ")+"' " +
				"AND  ACCOUNT_TYPE = '"+upREGRecp.getValue("ACCOUNT_TYPE")+"'";
		TParm ektResult = new TParm(TJDODBTool.getInstance().select(ektAccountSql));
		if(ektResult.getCount()<= 0){
			result = update("insertEKTAccount", upREGRecp, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				return result;
			}
		}
		
		
		return resultAll;
	}

	/**
	 * ��ѯ�ս����� ======zhangp 20120105
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm accountQuery(TParm parm) {
		TParm result = new TParm();
		result = this.query("accountQuery", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ������ =====zhangp 20120201
	 * 
	 * @return
	 */
	public TParm factageFee(String id) {
		String sql = "SELECT ISSUERSN_DESC,FACTORAGE_FEE FROM EKT_ISSUERSN WHERE ISSUERSN_CODE = '"
				+ id + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ����ԭ��Ĭ�� ===zhangp 20120201
	 * 
	 * @return
	 */
	public TParm sendCause() {
		String sql = "SELECT ISSUERSN_CODE AS ID,ISSUERSN_DESC AS NAME,FLG FROM EKT_ISSUERSN WHERE FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ����Ʊ�ݣ�ҽ�ƿ��˷�ʱ�޸�EKT_ACCNTDETAIL����CHARGE_FLG�ֶ�ʹ��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryGroupAccntdetail(TParm parm) {
		return query("queryGroupAccntdetail", parm);
	}

	/**
	 * ȡ��֧����ʽĬ��ֵ =======zhangp 20120224
	 * 
	 * @return
	 */
	public String getPayTypeDefault() {
		String sql = "SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='GATHER_TYPE' ORDER BY SEQ,ID ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		if (result.getCount() < 0) {
			return "";
		}
		String id = result.getData("ID", 0).toString();
		return id;
	}

	/**
	 * ҽ�ƿ��շ�
	 * 
	 * @param ioparm
	 *            �շѵ�ҽ��
	 * @param ektParm
	 *            ҽ�ƿ���Ϣ
	 * @param caseNo
	 *            �������
	 * @param type
	 *            ִ�пۿ�
	 * @param businessType
	 *            ����
	 * @return ����ǩ����
	 */
	public TParm onOPDAccntClient(TParm sumParm, TParm ioparm, TParm ektParm,
			String caseNo, String businessType, double ektAmt,
			TConnection connection) {
		String businessNo = EKTTool.getInstance().getBusinessNo();// �ڲ����׺���
		String cardNo = ektParm.getValue("PK_CARD_NO");// ����
		String mrNo = ektParm.getValue("MR_NO");
		String cashierCode = sumParm.getValue("OPT_USER");
		double originalBalance = ektParm.getDouble("NEW_CURRENT_BALANCE");// ִ��ҽ��������ҽ�ƿ����=
																			// ҽ���ָ��Ľ��+ҽ�ƿ����
		String term = sumParm.getValue("OPT_TERM");
		TParm tempParm = new TParm();
		String TradeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
		tempParm.setData("CURRENT_BALANCE", ektAmt);//����ҽ�ƿ���ʣ��Ľ��
		tempParm.setData("Trade_NO", TradeNo);// �ⲿ���׺�
		tempParm.setData("CARD_NO", cardNo);// ����
		tempParm.setData("MR_NO", mrNo);// ������
		tempParm.setData("BUSINESS_NO", businessNo);// �ڲ����׺�
		tempParm.setData("PAT_NAME", ektParm.getValue("PAT_NAME"));// ��������
		tempParm.setData("OLD_AMT", originalBalance);// ԭ�����
		tempParm.setData("AMT", sumParm.getDouble("billAmt"));// ������δ�շѽ��
		tempParm.setData("STATE", 1);// �ۿ�
		tempParm.setData("BUSINESS_TYPE", businessType);// �շ����� OPB /REG
		tempParm.setData("OPT_USER", cashierCode);
		tempParm.setData("OPT_TERM", term);
		tempParm.setData("ID_NO", "none");
		tempParm.setData("NAME", ektParm.getValue("PAT_NAME"));
		tempParm.setData("CREAT_USER", cashierCode);
		tempParm.setData("OPT_USER", cashierCode);
		tempParm.setData("CASE_NO", caseNo);
		tempParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		tempParm.setData("OPT_TERM", term);
		TParm result = createCard(tempParm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");

		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			TParm parm = (TParm) map.get(rxNo);
			if (parm == null) {
				parm = new TParm();
				map.put(rxNo, parm);
			}
			parm.addData("ORDER_CODE", ioparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", ioparm.getData("SEQ_NO", i));
			parm.addData("AMT", ioparm.getData("AMT", i));
			parm.addData("BILL_FLG", ioparm.getData("BILL_FLG", i));
		}
		// }
		Iterator iterator = map.keySet().iterator();
		List list = new ArrayList();
		int[] pv = { 0 };
		while (iterator.hasNext()) {
			String rxNo = (String) iterator.next();
			TParm parm = (TParm) map.get(rxNo);
			// ͬһ���������ͬ���޸Ĳ�ͬҩƷ
			// if(getDoubleSum(parm,"AMT") == getRxAmt(cardNo,caseNo,rxNo))
			// continue;
			if (!accnt(parm, cardNo, mrNo, caseNo, rxNo, cashierCode, term,
					businessNo, originalBalance, connection, businessType, pv))
				return null;
			list.add(rxNo);
		}
		// �������
		// TParm p = new TParm();
		// p.setData("CARD_NO", cardNo);
		// p.setData("CURRENT_BALANCE", ektAmt);
		// p.setData("OPT_USER", cashierCode);
		// p.setData("OPT_DATE", SystemTool.getInstance().getDate());
		// p.setData("OPT_TERM", term);
		// TParm result = EKTTool.getInstance().updateEKTMaster(p, connection);
		// if (result.getErrCode() != 0) {
		// err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
		// + result.getErrText());
		// connection.rollback();
		// connection.close();
		// return null;
		// }
		
		return tempParm;
	}

	/**
	 * ����
	 * 
	 * @param ioparm
	 *            TParm
	 * @param cardNo
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @param cashierCode
	 *            String
	 * @param term
	 *            String
	 * @param pv
	 *            Object[]
	 * @param date
	 *            Timestamp
	 * @param connection
	 *            TConnection
	 * @param type
	 *            int
	 * @param greenUseAmt
	 *            double ��ɫͨ��ʹ�ý��
	 * @param ektUseAmt
	 *            double ҽ�ƿ�ʹ�ý��
	 * @param greenBalance
	 *            double //��ɫͨ���ۿ�ʣ���ܽ��
	 * @param greenPathTotAl
	 *            double //��ɫͨ���������
	 * @return boolean
	 */
	private boolean accnt(TParm ioparm, String cardNo, String mrNo,
			String caseNo, String rxNo, String cashierCode, String term,
			String businessNo, double originalBalance, TConnection connection,
			String businessType, int[] pv) {
		TParm p = new TParm();
		p.setData("CASE_NO", caseNo);
		TParm r = new TParm();
		p.setData("RX_NO", rxNo);
		//r = EKTTool.getInstance().getDetail(p);
		int index = pv[0];
		//int count = r.getCount("ORDER_CODE");
		// ҽ��������������˿�
//		for (int i = 0; i < count; i++) {
//			index++;
//			TParm parm = new TParm();
//			parm.setData("BUSINESS_NO", businessNo);
//			parm.setData("BUSINESS_SEQ", index);
//			parm.setData("CARD_NO", cardNo);
//			parm.setData("MR_NO", mrNo);
//			parm.setData("CASE_NO", caseNo);
//			parm.setData("ORDER_CODE", r.getValue("ORDER_CODE", i));
//			parm.setData("RX_NO", rxNo);
//			parm.setData("SEQ_NO", r.getInt("SEQ_NO", i));
//			double amt = -r.getDouble("BUSINESS_AMT", i);// ��һ��ҽ����Ҫ�ۿ�Ľ��
//			parm.setData("CHARGE_FLG", "2");// ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����)
//			parm.setData("ORIGINAL_BALANCE", originalBalance);// �շ�ǰ���
//			parm.setData("BUSINESS_AMT", amt);
//			originalBalance -= amt;
//			parm.setData("CURRENT_BALANCE", originalBalance);
//			parm.setData("GREEN_BALANCE", 0.00);// ��ɫͨ��ʣ����=��ǰʣ����-ÿһ�ʲ������
//			parm.setData("GREEN_BUSINESS_AMT", 0.00);// ��ɫͨ��ÿһ��ʹ�ý��
//			parm.setData("CASHIER_CODE", cashierCode);
//			parm.setData("BUSINESS_DATE", SystemTool.getInstance().getDate());
//			// 1������ִ�����
//			// 2��˫��ȷ�����
//			parm.setData("BUSINESS_STATUS", "1");
//			// 1��δ����
//			// 2�����˳ɹ�
//			// 3������ʧ��
//			parm.setData("ACCNT_STATUS", "1");
//			parm.setData("ACCNT_USER", new TNull(String.class));
//			parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
//			parm.setData("OPT_USER", cashierCode);
//			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//			parm.setData("OPT_TERM", term);
//			parm.setData("BUSINESS_TYPE", businessType);
//			TParm result = EKTTool.getInstance().insertEKTDetail(parm,
//					connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
//						+ result.getErrText());
//				connection.rollback();
//				connection.close();
//				return false;
//			}
//		}
		r = detailCancel(p, connection);
		// }
		if (r.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt.detailCancel " + r.getErrCode()
					+ r.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		int count = ioparm.getCount("ORDER_CODE");
		for (int i = 0; i < count; i++) {
			if (!ioparm.getBoolean("BILL_FLG", i))
				continue;
			index++;
			TParm parm = new TParm();
			parm.setData("BUSINESS_NO", businessNo);
			parm.setData("BUSINESS_SEQ", index);
			parm.setData("CARD_NO", cardNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_CODE", ioparm.getValue("ORDER_CODE", i));
			parm.setData("RX_NO", rxNo);
			parm.setData("SEQ_NO", ioparm.getInt("SEQ_NO", i));

			double amt = ioparm.getDouble("AMT", i);
			if (amt > 0)
				amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
			else if (amt < 0)
				amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
			// if (insFlg) {// ҽ���˿�
			// parm.setData("CHARGE_FLG", "2"); //
			// ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����)
			// parm.setData("BUSINESS_AMT", -amt);
			// } else {
			parm.setData("CHARGE_FLG", "1"); // ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����)
			parm.setData("BUSINESS_AMT", amt);
			// }
			parm.setData("ORIGINAL_BALANCE", originalBalance); // �շ�ǰ���
			originalBalance -= amt;// ����ҽ�ƿ��ۿ�
			parm.setData("GREEN_BALANCE", 0.00);// ��ɫͨ��ʣ����=��ǰʣ����-ÿһ�ʲ������
			parm.setData("GREEN_BUSINESS_AMT", 0.00);// ��ɫͨ��ÿһ��ʹ�ý��
			// if (insFlg) {
			// originalBalance += amt;// ҽ���˿����
			// } else {
			// }
			parm.setData("CURRENT_BALANCE", originalBalance);
			parm.setData("CASHIER_CODE", cashierCode);
			parm.setData("BUSINESS_DATE", SystemTool.getInstance().getDate());
			// 1������ִ�����
			// 2��˫��ȷ�����

			parm.setData("BUSINESS_STATUS", "1");
			// 1��δ����
			// 2�����˳ɹ�
			// 3������ʧ��
			parm.setData("ACCNT_STATUS", "1");
			parm.setData("ACCNT_USER", new TNull(String.class));
			parm.setData("ACCNT_DATE", new TNull(Timestamp.class));
			parm.setData("OPT_USER", cashierCode);
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.setData("OPT_TERM", term);
			parm.setData("BUSINESS_TYPE", businessType);
			TParm result = EKTTool.getInstance().insertEKTDetail(parm,
					connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return false;
			}
		}

		pv[0] = index;
		return true;
	}

	/**
	 * ҽ�ƿ��ۿ� �޸�ҽ�ƿ����
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param control
	 *            TControl
	 * @return TParm ===============pangben 20111007
	 */
	private TParm createCard(TParm parm, TConnection connection) {
		TParm result = insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrade " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return result;
		}
		result = deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		result = insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * ��ò�������ҽ������
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryCCBTrade(TParm parm) {
		TParm result = new TParm();
		String optUser = parm.getValue("OPT_USER") ;
		String startDate = parm.getValue("START_DATE").split(" ")[0];
		String endDate = parm.getValue("END_DATE").split(" ")[0];
    	String sql =
    	      "SELECT A.OPT_DATE, C.USER_NAME,"+
    		         "CASE WHEN STATE = '1' THEN 'ԤԼ�շ�' ELSE 'ԤԼ�˷�' END STATE,"+
    		         "A.BUSINESS_NO, A.MR_NO, A.CARD_NO, D.PAT_NAME,"+
    		         "A.AMT, 0 OTHER "+
    		    "FROM EKT_CCB_TRADE A, REG_PATADM B, SYS_OPERATOR C, SYS_PATINFO D "+
    		   "WHERE (A.BUSINESS_TYPE = 'REG' OR A.BUSINESS_TYPE='REGT') "+
    		     "AND A.BANK_FLG = 'Y' "+
    		     (optUser.trim().equals("")?"":"AND A.OPT_USER = '"+optUser+"' ")+
    		     "AND A.OPT_DATE BETWEEN TO_DATE ('" + startDate + " 00:00:00', 'YYYY/MM/DD hh24:mi:ss') "+
    	                            "AND TO_DATE ('" + endDate + " 23:59:59', 'YYYY/MM/DD hh24:mi:ss') "+
    		     "AND A.CASE_NO = B.CASE_NO "+
    		     "AND A.OPT_USER = C.USER_ID "+	 
    		     "AND A.MR_NO = D.MR_NO "+
    		     "AND B.APPT_CODE = 'Y' "+
    		     "AND ( (A.STATE = 2 AND A.RECEIPT_NO IS NOT NULL) "+
    		        "OR (A.STATE IN (1, 2) AND A.RECEIPT_NO IS NULL) ) "+
    		"ORDER BY A.OPT_USER, A.OPT_DATE";
        result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	/**
	 * ��ѯOPD_ORDER ���շ��Ժ� BUSINESS_NO Ϊ�յ����� ====pangben 2013-4-27
	 * @param parm
	 * @return
	 */
	public TParm queryOpdOrderError(TParm parm){
		TParm result = new TParm();
		result = this.query("queryOpdOrderError", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * ����ҽ�ƿ��Ļش�ֵ��дҽ������Ӧ���ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @param parm
	 *            orderParm ҽ�ƿ���Ҫ�շѵ�ҽ�� ������� OPD_ORDER_HISTORY ������ ����ҽ���޸�
	 *            ��Ҫ�޸ĵ�ҽ���ǣ���ҩ��ҩ �������ҽ��
	 * @return boolean
	 * ===============pangben 2013-7-17 ����ҽ��վʹ��
	 */
	public TParm getBillSets(TParm parm,TParm ektSumExeParm) {
		//int type = parm.getInt("OP_TYPE");
		// �ɹ�
		TParm result = new TParm();
		if (parm.getValue("OPD_UN_FLG").length()<=0 || parm.getValue("OPD_UN_FLG").equals("N")) {
			result.setData("EXEC_FLG", "N");
			result.setData("RECEIPT_FLG", "N");
			//result.setData("BILL_TYPE", "E");
			//result.setData("BILL_FLG", "Y");
			result.setData("BILL_USER", parm.getValue("OPT_USER"));
			result.setData("BILL_DATE", SystemTool.getInstance().getDate());
		}
		// ֻ�˷ѣ��ѻش��Ĵ������շ�ע����ΪN��
		else {
			result.setData("EXEC_FLG", "N");
			result.setData("RECEIPT_FLG", "N");
			//result.setData("BILL_TYPE", "C");
			//result.setData("BILL_FLG", "N");
			result.setData("BILL_USER", "");
			result.setData("BILL_DATE", new TNull(String.class));
		}
		result.setData("CASE_NO", parm.getValue("CASE_NO"));//�����===��ѯ�Ƿ�������
		// ������ʱʹ�������ܿ��Ƿ������ɫͨ��
		result.setData("REGION_CODE", Operator.getRegion());
		result.setData("OPT_DATE", SystemTool.getInstance().getDate());
		result.setData("OPT_USER", parm.getValue("OPT_USER"));
		result.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		result.setData("MR_NO", parm.getValue("MR_NO"));//������ ===��ѯ�Ƿ�������
		result.setData("OPB_AMT", parm.getDouble("OPB_AMT"));
		result.setData("EKTNEW_AMT", parm.getDouble("EKTNEW_AMT"));// ҽ�ƿ��н��
		result.setData("OLD_AMT", parm.getDouble("OLD_AMT"));// ҽ�ƿ��ۿ�֮ǰ���
		result.setData("TRADE_NO", parm.getValue("TRADE_NO"));
		//result.setData("orderSumParm", parm.getParm("orderParm").getData());// ��Ҫ�޸ĵ�ҽ��
		result.setData("orderParm", ektSumExeParm.getParm("newParm").getData());// ��Ҫ�޸ĵ�ҽ��
		result.setData("hl7Parm",ektSumExeParm.getParm("hl7Parm").getData());//HL7���ͽӿڼ���
		return result;
	}
	
	/**
	 * �����������Ա� ��֤���Ų�ѯ������Ϣ      huangtt 20140924
	 * @param parm
	 * @return
	 */
	public TParm getPatInfo(TParm parm){
		TParm result = new TParm();
		String patName = parm.getValue("PAT_NAME");
		String sexCode = parm.getValue("SEX_CODE");
		String idNo = parm.getValue("IDNO");
		String sql = "SELECT MR_NO, IPD_NO, DELETE_FLG, MERGE_FLG, MOTHER_MRNO, PAT_NAME, PAT_NAME1," +
				" PY1, PY2, FOREIGNER_FLG, IDNO, BIRTH_DATE, CTZ1_CODE, CTZ2_CODE," +
				" CTZ3_CODE, TEL_COMPANY, TEL_HOME, CELL_PHONE, COMPANY_DESC, E_MAIL," +
				" BLOOD_TYPE, BLOOD_RH_TYPE, SEX_CODE, MARRIAGE_CODE, POST_CODE, ADDRESS," +
				" RESID_POST_CODE, RESID_ADDRESS, CONTACTS_NAME, RELATION_CODE," +
				" CONTACTS_TEL, CONTACTS_ADDRESS, SPOUSE_IDNO, FATHER_IDNO, MOTHER_IDNO," +
				" RELIGION_CODE, EDUCATION_CODE, OCC_CODE, NATION_CODE, SPECIES_CODE," +
				" FIRST_ADM_DATE, RCNT_OPD_DATE, RCNT_OPD_DEPT, RCNT_IPD_DATE," +
				" RCNT_IPD_DEPT, RCNT_EMG_DATE, RCNT_EMG_DEPT, RCNT_MISS_DATE," +
				" RCNT_MISS_DEPT, KID_EXAM_RCNT_DATE, KID_INJ_RCNT_DATE, ADULT_EXAM_DATE," +
				" SMEAR_RCNT_DATE, DEAD_DATE, HEIGHT, WEIGHT, DESCRIPTION, BORNIN_FLG," +
				" NEWBORN_SEQ, PREMATURE_FLG, HANDICAP_FLG, BLACK_FLG," +
				" NAME_INVISIBLE_FLG, LAW_PROTECT_FLG, LMP_DATE, PREGNANT_DATE," +
				" BREASTFEED_STARTDATE, BREASTFEED_ENDDATE, PAT1_CODE, PAT2_CODE," +
				" PAT3_CODE, MERGE_TOMRNO, OPT_USER, OPT_DATE AS REPORT_DATE, OPT_TERM, HOMEPLACE_CODE," +
				" FAMILY_HISTORY, HANDLE_FLG, NHI_NO, ADDRESS_COMPANY, POST_COMPANY," +
				" BIRTHPLACE, CCB_PERSON_NO, NHICARD_NO, PAT_BELONG, REMARKS, ID_TYPE," +
				" CURRENT_ADDRESS, SPECIAL_DIET, FIRST_NAME, LAST_NAME," +
				" GESTATIONAL_WEEKS, OLD_NAME, NEW_BODY_WEIGHT, TESTBLD_DR, BOOK_BUILD," +
				" DEPT_FLG, OPB_ARREAGRAGE, PAST_HISTORY" +
				" FROM SYS_PATINFO" +
				" WHERE 1 = 1";
		if(patName.length()>0 || sexCode.length()>0){
			sql += " AND PAT_NAME='"+patName+"' AND SEX_CODE='"+sexCode+"'";
		}
		if(idNo.length() > 0){
			sql += " AND IDNO='"+idNo+"'";
		}
		sql += "ORDER BY MR_NO DESC";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
			err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	public TParm updataEktData(TParm parm,TConnection connection ){
		TParm result = new TParm();
		
		return result;
		
	}
}
