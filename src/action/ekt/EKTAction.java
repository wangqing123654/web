package action.ekt;

//import java.sql.Timestamp;
//import java.util.Map;

import com.dongyang.action.TAction;
//import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

import jdo.ekt.EKTNewIO;
//import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.jdo.TJDODBTool;
//import com.dongyang.manager.TIOM_AppServer;

import jdo.ekt.EKTIO;
//import jdo.bil.BILGreenPathTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.mem.MEMTool;
import jdo.opb.OPBTool;
//import jdo.opd.OrderTool;
import jdo.reg.PatAdmTool;
//import jdo.reg.REGTool;
//import jdo.sys.Operator;
//import jdo.sys.PatTool;
//import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ҽ�ƿ�����
 * </p>
 * 
 * <p>
 * Description:ҽ�ƿ�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111007
 * @version 2.0
 */
public class EKTAction extends TAction {
	public EKTAction() {
	}

	/**
	 * ҽ�ƿ�����д��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm TXEKTRenewCard(TParm parm) {
		TConnection connection = getConnection();
		// �޸�ҽ�ƿ�������:����ǰ����������ʧ��״̬
		TParm result = EKTTool.getInstance()
				.updateEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ���ҽ�ƿ�������
		result = EKTTool.getInstance().insertEKTIssuelog(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ҽ�ƿ�������
		result = EKTIO.getInstance().createCard(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ҽ�ƿ���ϸ����������
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BUSINESS_AMT", 0);
		businessParm.setData("GREEN_BALANCE", 0);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		// zhangp 20111222 ҽ�ƿ���ֵ�˿��������
		TParm bilParm = parm.getParm("bilParm");
		// =======zhangp 20120227 modify start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		bilParm.setData("BIL_BUSINESS_NO", businessNo);
		// bilParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		// =======zhangp 20120227 modify end
		// System.out.println("д��bilParm==="+bilParm);
		result = EKTTool.getInstance().insertEKTBilPay(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ҽ�ƿ���ֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ===============pangben 20111007
	 */
	public TParm TXEKTonFee(TParm parm) {
		TConnection connection = getConnection();
		// �������
		TParm result = null;
		//======20130509 yanjing modify��ҽ�ƿ�EKT-MASER��д��һ��������
//		 if (parm.getBoolean("FLG")) {
//			 // �½�ҽ�ƿ���Ϣ
		 result = EKTIO.getInstance().createCard(parm, connection);
//		 } else {
		 // ����ҽ�ƿ���Ϣ
//		 result = EKTTool.getInstance().updateEKTMaster(parm, connection);
//		 }
		 if (result.getErrCode() < 0) {
		 connection.close();
		 return result;
		 }
		// ҽ�ƿ���ϸ����������
		String businessNo = EKTTool.getInstance().getBusinessNo();
		TParm businessParm = parm.getParm("businessParm");
		businessParm.setData("BUSINESS_NO", businessNo);
		businessParm.setData("GREEN_BALANCE", 0.00);
		businessParm.setData("GREEN_BUSINESS_AMT", 0.00);
		businessParm.setData("BUSINESS_TYPE", "");
		result = EKTTool.getInstance()
				.insertEKTDetail(businessParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		TParm billParm = parm.getParm("billParm");
		// ҽ�ƿ���ֵ��
		// ===zhangp 20120314 start
		// String billBusinessNo = EKTTool.getInstance().getBillBusinessNo();
		// billParm.setData("BIL_BUSINESS_NO", billBusinessNo);
		billParm.setData("DESCRIPTION", parm.getValue("DESCRIPTION"));//add by sunqy 20140715 ���п���
		billParm.setData("CARD_TYPE", parm.getValue("CARD_TYPE"));//==liling 20140725 add Card_type ������
		billParm.setData("PRINT_NO", parm.getValue("PRINT_NO"));
		billParm.setData("BIL_BUSINESS_NO", businessNo);
		// ҽ�ƿ���ֵ����KET_BIL_PAY
		result = EKTTool.getInstance().insertEKTBilPay(billParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();

		// result.setData("BIL_BUSINESS_NO", billBusinessNo);// ��ֵ�վݺ�
		result.setData("BIL_BUSINESS_NO", businessNo);// ��ֵ�վݺ�
		// ===zhangp 20120314 end
		return result;
	}

	/**
	 * ����������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().insertdata(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		// ���¹Һ�����������ɫͨ�����
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ���¹Һ�����������ɫͨ�����
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateEKTGreen(TParm parm, TConnection conn) {
		// ���¹Һ�����������ɫͨ�����
		TParm result = PatAdmTool.getInstance().updateEKTGreen(
				ektParmTemp(parm), conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// ��ô˾��ﲡ����ɫͨ�����
		TParm check = PatAdmTool.getInstance().selEKTByMrNo(ektParm);
		double GREEN_BALANCE = check.getDouble("GREEN_BALANCE", 0);
		double GREEN_PATH_TOTAL = check.getDouble("GREEN_PATH_TOTAL", 0);
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_BALANCE);
		ektParm.setData("GREEN_PATH_TOTAL", StringTool.round(parm
				.getDouble("APPROVE_AMT"), 2)
				+ GREEN_PATH_TOTAL);
		return ektParm;
	}

	/**
	 * ����һ����ɫͨ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm cancelGreenPath(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTGreenPathTool.getInstance().cancleGreenPath(parm,
				conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		parm.setData("APPROVE_AMT", -parm.getDouble("APPROVE_AMT"));
		// ���¹Һ�����������ɫͨ�����
		result = updateEKTGreen(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ɾ���Һ�ʧ�ܳ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm deleteRegOldData(TParm parm) {
		TConnection conn = this.getConnection();
		TParm result = EKTTool.getInstance().deleteTrade(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		result = EKTTool.getInstance().deleteDetail(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * ҽ�ƿ��ս���� ==============zhangp
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onEKTAccount(TParm parm) {
		TConnection connection = this.getConnection();
		TParm result = new TParm();
		result = EKTTool.getInstance().executeEKTAccount(parm, connection);//�ս�ҽ�ƿ�������
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().updateMemTradeData(parm, connection);//�ս��Ա��������
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().updateMemPackageData(parm, connection);//�ս��ײ͵�����
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		result = MEMTool.getInstance().updateMemGiftCardData(parm, connection);//�ս���Ʒ��������
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ִ��ҽ��������� ҽ�ƿ����ױ� EKT_TREDE �� EKT_ACCNTDETAIL ���ҽ����������
	 * 
	 * @param parm
	 *            AMT:���β������ BUSINESS_TYPE :���β������� CASE_NO:�������
	 * @param type
	 *            ��9, ҽ���ۿ� 0,ҽ���س�
	 * @return
	 */
	public TParm exeInsSave(TParm parm) {
		// ��ֹ�ڷ������˵���
		TParm result = new TParm();
		TParm p = null;
		TConnection connection = getConnection();
		/**
		 * �����շ�/�˷�ʹ��
		 */
		if (null != parm.getValue("EXE_FLG")
				&& parm.getValue("EXE_FLG").equals("Y")) {
			parm.setData("ID_NO",parm.getValue("ID_NO"));
			// parm.setData("CASE_NO", caseNo);
			parm.setData("NAME", parm.getValue("PAT_NAME"));
			parm.setData("CREAT_USER", parm.getValue("OPT_USER"));
			parm.setData("CURRENT_BALANCE", parm
					.getDouble("CURRENT_BALANCE")
					- parm.getDouble("AMT"));
			result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			result = EKTTool.getInstance().insertEKTMaster(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + result.getErrCode()
						+ result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			//ҽ��ҽ�ƿ���Ʊ����
			p =EKTNewIO.getInstance().unInsOpbReceiptNo(parm, connection);
			if (p.getErrCode() != 0) {
				err("ERR:EKTIO.createCard " + p.getErrCode()
						+ p.getErrText());
				connection.rollback();
				connection.close();
				return p;
			}
			//ҽ���˷Ѳ��� ���޸�OPD_ORDER �����ڲ����׺���
//			parm.setData("BUSINESS_NO",p.getValue("TRADE_NO"));
//			result=EKTNewTool.getInstance().updateOpdOrderBusinessNo(parm, connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:EKTIO.createCard " + result.getErrCode()
//						+ result.getErrText());
//				connection.rollback();
//				connection.close();
//				return result;
//			}
		}
		// �����շ�ҽ�ƿ�����,ҽ���ָ��� ����INSFEEPrintControl�����
		if (null != parm.getValue("INS_EXE_FLG")
				&& parm.getValue("INS_EXE_FLG").equals("Y")) {
			TParm orderParm = parm.getParm("orderParm");
			TParm readCard = parm.getParm("readCard");// ̩��ҽ�ƿ���������
			// TParm cp=new TParm();
			// ��ѯ�˾��ﲡ���������ݻ��ܽ��
			TParm cp = new TParm();
			cp.setData("EKT_USE", - parm.getDouble("INS_AMT"));// ҽ�ƿ��ۿ���=û�д�Ʊ���ܽ��-ҽ�����
			cp.setData("EKT_OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));// ҽ�ƿ����ڲ���֮ǰ�Ľ��
			// �����˴ζ����Ĵ���ǩ�����н��
			// �س�
			// ��õ�ǰ
			// ҽ�ƿ��Ľ�
			cp.setData("GREEN_BALANCE", 0.00);// ������ۿ���
			cp.setData("CARD_NO", readCard.getValue("PK_CARD_NO"));// ��������
			cp.setData("MR_NO", readCard.getValue("MR_NO"));// ������
			cp.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
			cp.setData("PAT_NAME", readCard.getValue("PAT_NAME"));// ��������
			// cp.setData("OLD_AMT", readCard.getDouble("CURRENT_BALANCE"));//
			// ҽ�ƿ�ԭ�н��
			cp.setData("BUSINESS_TYPE", "OPBT");// ����
			cp.setData("GREEN_PATH_TOTAL", 0);// �������������
			cp.setData("GREEN_USE", 0);// ������˴οۿ���
			cp.setData("OPT_USER", parm.getValue("OPT_USER"));// ������
			cp.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			cp.setData("IDNO", readCard.getValue("IDNO"));// ���֤��
			cp.setData("billAmt", parm.getDouble("billAmt"));//δ�շѽ��
			//cp.setData("TRADE_SUM_NO",orderParm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
			// // CURRENT_BALANCE:ҽ�ƿ����ڽ��
			double ektAmt = readCard.getDouble("CURRENT_BALANCE")
					+ parm.getDouble("INS_AMT") - parm.getDouble("billAmt");
			cp.setData("EKT_AMT", ektAmt);// ҽ�ƿ���ǰ���
			
			//TParm billParm = orderParm.getParm("parmBill");//δ�շ�ҽ������
			// ̩��ҽԺ�ۿ����
			p = new TParm(EKTNewIO.getInstance().onNewSaveInsFee(cp.getData()));
			if (p.getErrCode() < 0) {
				connection.close();
				return p;
			}
			//TParm newParm = orderParm.getParm("parmSum");// ����ҽ��
			orderParm.setData("TRADE_NO",p.getValue("TRADE_NO"));
			orderParm.setData("OPT_USER", parm.getValue("OPT_USER"));// ������
			orderParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));// IP
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));// �����
			//orderParm.setData("billParm", orderParm.getParm("parmBill").getData());//δ�շ�ҽ������
			result = OPBTool.getInstance().onHl7ExeBillFlg(orderParm, connection);
			if (result.getErrCode() < 0) {
				// result = ektCancel(parm);
				if (result.getErrCode() < 0) {
					System.out.println("ҽ�ƿ��ع���Ϣ����ʧ��");
				}
				connection.close();
				return result;
			}
			parm.setData("EXE_USER", parm.getValue("OPT_USER"));
			parm.setData("EXE_TERM", parm.getValue("OPT_TERM"));
			parm.setData("EXE_TYPE", parm.getValue("RECP_TYPE"));//=====pangben 2013-3-13 �޸Ĵ�ǰ̨¼��
			result = INSRunTool.getInstance().deleteInsRun(parm, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
			result = INSTJFlow.getInstance().updateInsAmtFlgPrint(parm,
					parm.getValue("RECP_TYPE"), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return p;
	}
	
	/**
	 * ҽ�ƿ�֧��
	 * =====zhangp
	 * @param parm
	 * @return
	 */
	public TParm ektPay(TParm parm){
		String mrNo = parm.getValue("MR_NO");
		String inCardNo = parm.getValue("CARD_NO");
		double amt = parm.getDouble("AMT");
		String optUser = parm.getValue("OPT_USER");
		String optTerm = parm.getValue("OPT_TERM");
		double currentBalance = 0;
		double newBalance = 0;
		String cardNo = "";
		String tradeNo = "";
		String name = "";		
		TParm result;
		String sql = 
			" SELECT A.CARD_NO, B.CURRENT_BALANCE, B.NAME" +
			" FROM EKT_ISSUELOG A, EKT_MASTER B" +
			" WHERE     A.MR_NO = '" + mrNo + "'" +
			" AND A.WRITE_FLG = 'Y'" +
			" AND A.CARD_NO = B.CARD_NO";
		TParm cardParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(cardParm.getCount() < 0){
			cardParm.setErr(-1, "ҽ�ƿ����0");
			return cardParm;
		}
		cardNo = cardParm.getValue("CARD_NO", 0);
		if(!inCardNo.equals(cardNo)){
			result = new TParm();
			result.setErr(-1, "�˿���Ч");
			return result;
		}
		name = cardParm.getValue("NAME", 0);
		currentBalance = cardParm.getDouble("CURRENT_BALANCE", 0);
		newBalance = currentBalance - amt;
		sql = 
			" UPDATE EKT_MASTER" +
			" SET CURRENT_BALANCE = " + newBalance + "" +
			" WHERE CARD_NO = '" + cardNo + "'";
		TConnection connection = getConnection();
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if(result.getErrCode() < 0 ){
			connection.rollback();
			connection.close();
			return result;
		}
		tradeNo = EKTTool.getInstance().getTradeNo();
		sql =
			" INSERT INTO EKT_TRADE" +
			" (TRADE_NO, CARD_NO, MR_NO, CASE_NO, PAT_NAME, " +
			" OLD_AMT, AMT, STATE, BUSINESS_TYPE, OPT_USER, " +
			" OPT_DATE, OPT_TERM, GREEN_BALANCE, GREEN_BUSINESS_AMT, PAY_OTHER3, " +
			" PAY_OTHER4)" +
			" VALUES" +
			" ('" + tradeNo + "', '" + cardNo + "', '" + mrNo + "', '', '" + name + "', " +
			" " + currentBalance + ", " + amt + ", '1', 'PAY', '" + optUser + "', " +
			" SYSDATE, '" + optTerm + "', 0, 0, 0, " +
			" 0)";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if(result.getErrCode() < 0 ){
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
}
