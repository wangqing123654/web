package action.opb;

import java.util.List;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;

import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TIOM_AppServer;
import jdo.opb.OPBTool;
import jdo.ekt.EKTTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title: �����շѶ�����
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author lzk 2008.9.26
 * @version 1.0
 */
public class OPBAction extends TAction {
	/**
	 * ��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		TParm result = new TParm();
		// �õ������
		String caseNo = parm.getValue("CASE_NO");
		if (caseNo.length() == 0)
			return err(-1, "CASE_NO is null");
		// �õ�����order
		TParm orderresult = OrderTool.getInstance().query(caseNo);
		// System.out.println("orderresult"+orderresult);
		if (orderresult.getErrCode() != 0) {
			return err(orderresult);
		}
		// ����order
		result.setData("ORDER", orderresult.getData());
		return result;
	}
	public TParm reduceBackFee(TParm inParm){
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().reduceBackFee(inParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
		
	}

	/**
	 * �ż��ﱣ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().opbCharge(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ��ż��ﱣ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ============pangben 20111025
	 */
	public TParm onSaveEKT(TParm parm) {
		// ִ�����opd_order ������ �շ�״̬ ����״̬receipt_no is null and bill_flg ='Y'
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().opbCharge(parm, connection);
		// result.setErr(-1,"����");
		TParm result1 = null;
		if (result.getErrCode() < 0) {
			result1 = ektCancel(parm);
			if (result1.getErrCode() < 0) {
				System.out.println("ҽ�ƿ��ع���Ϣ����ʧ��");
			}
			connection.rollback();
			connection.close();
			return result;
		}
		TParm orderParm =parm.getParm("newParm");//����ҽ��
		result = OPBTool.getInstance().updateOpdOrderEkt(parm, orderParm, connection);
		if (result.getErrCode() < 0) {
			result = ektCancel(parm);
			if (result.getErrCode() < 0) {
				System.out.println("ҽ�ƿ��ع���Ϣ����ʧ��");
			}
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	

	/**
	 * ҽ�ƿ�ִ���շѳ��ִ���ع���Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm ektCancel(TParm parm) {
		// System.out.println("ektCancel ���� parm:::" + parm);
		TParm result = null;
		TParm orderParm = parm.getParm("orderParm");
		TConnection connection = getConnection();
		// �ۿ����
		if ("Y".equals(parm.getValue("FLG"))) {
			result = ektTempCancel(parm, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		} else {
			result = ektTempCancel(parm, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// �˿����
			if (null != parm.getValue("CANCLE_TREDE")
					&& parm.getValue("CANCLE_TREDE").length() > 0) {
				result = EKTTool.getInstance().consumeCancelOne(parm,
						parm.getValue("CANCLE_TREDE"), 0);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ�����ʧ�ܹ�������
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	private TParm ektTempCancel(TParm parm, TConnection connection) {
		TParm result = null;
		result = EKTTool.getInstance().deleteTrade(parm, connection);
		if (result.getErrCode() < 0) {
			// connection.close();
			return result;
		}
		return result;
	}

	/**
	 * �����˷�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backReceipt(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backReceipt(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �����շѲ�ӡ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOPBRePrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().saveOPBRePrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �����շѲ�ӡ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveOPBEKTRePrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance()
				.saveOPBEKTRePrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * �ײʹ�Ʊ
	 */
	public TParm onOPBMemPrint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().onOpbMemPackPrint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * ҽ�ƿ���Ʊ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOPBEktprint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().onOPBEktprint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �ֽ��Ʊ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOPBCashprint(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().onOPBCashprint(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * �ֽ��˷�����Ʊ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backOPBRecp(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backOPBRecp(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ��˷�����Ʊ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm backEKTOPBRecp(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance().backEKTOPBRecp(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ��˷�����Ʊ��:���˲�����û��ִ�н����Ʊ�ݣ���ִ���޸�BIL_INVRCP��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ==================pangben modify 20110822
	 */
	public TParm backOPBRecpStatus(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OPBTool.getInstance()
				.backOPBRecpStatus(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;

	}

	/**
	 * ����ҽ��վҽ�ƿ��շѲ����޸�ҽ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 201109015
	 */
	public TParm updateBillSets(TParm parm) {
		TParm result = new TParm();

		TParm orderParm = parm.getParm("orderParm"); // ��Ҫ������ҽ��
		TConnection connection = getConnection();
		result = OPBTool.getInstance().updateOpdOrderEkt(parm, orderParm, connection);
		if (result.getErrCode() < 0) {
			result = ektCancel(parm);
			if (result.getErrCode() < 0) {
				System.out.println("ҽ�ƿ��ع���Ϣ����ʧ��");
			}
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;

	}

	/*
	 * @param args String[]
	 */
	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		TIOM_AppServer.resetAction();

		//
	}

	/**
	 * ɾ��ҽ��(�����շѽ���) ====pangben 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBCharge(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OrderTool.getInstance()
				.deleteOPBCharge(parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ɾ��ҽ��(�����շѽ���) ====pangben 20120414
	 * 
	 * @param order
	 * @return
	 */
	public TParm deleteOPBChargeSet(TParm parm) {
		TConnection connection = getConnection();
		TParm result = OrderTool.getInstance().deleteOPBChargeSet(parm,
				connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

}
