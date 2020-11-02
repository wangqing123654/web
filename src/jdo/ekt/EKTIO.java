package jdo.ekt;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.util.Map;
import com.dongyang.db.TConnection;

import java.sql.Timestamp;
import com.dongyang.data.TNull;
import com.javahis.util.JavaHisDebug;
import java.util.HashMap;
import java.util.Iterator;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.config.TConfig;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import java.util.List;
import java.util.ArrayList;
import com.javahis.device.EktDriver;


import com.javahis.device.NJSMCardDriver;
import com.javahis.device.ReadCardService;
import com.javahis.device.NJSMCardYYDriver;

import jdo.reg.PatAdmTool;

public class EKTIO extends TJDODBTool {
	
	com.javahis.device.card.CardDTO cardDto;
	/**
	 * ʵ��
	 */
	private static EKTIO instanceObject;
	private String COM = getEktPort();// ̩��ҽ�ƿ�������Ĭ�ϲ����ż�ֵ����
	
	
	/**
	 * �õ�ʵ��
	 * 
	 * @return EKTIO
	 */
	public static EKTIO getInstance() {
		if (instanceObject == null)
			instanceObject = new EKTIO();
		return instanceObject;
	}

	/**
	 * ekt����
	 * 
	 * @return boolean
	 */
	public boolean ektSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.switch"));
	}

	/**
	 * ҽ�ƿ�����
	 * 
	 * @return boolean
	 */
	public boolean EKTDialogSwitch() {
		return StringTool.getBoolean(TConfig
				.getSystemValue("ekt.opd.EKTDialogSwitch"));
	}
	
	/**
	 * ������ekt����
	 * 
	 * @return boolean
	 */
	public boolean ektAyhSwitch() {
		return StringTool.getBoolean(TConfig.getSystemValue("ekt.ayhSwitch"));
	}

	/**
	 * ������
	 * 
	 * @param cardNo
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param balance
	 *            double
	 * @return boolean
	 */
	public boolean createCard(String cardNo, String mrNo, double balance) {
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("ID_NO", "none");
		parm.setData("MR_NO", mrNo);
		// parm.setData("CASE_NO", caseNo);
		parm.setData("NAME", PatTool.getInstance().getNameForMrno(mrNo));
		parm.setData("CURRENT_BALANCE", balance);
		parm.setData("CREAT_USER", Operator.getID());
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", getDBTime());
		parm.setData("OPT_TERM", Operator.getIP());
		return createCard(parm.getData());
	}
	public boolean unConsume(String tredeNo, TControl control) {
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			if (control != null)
				control.messageBox(result);
			return false;
		}
		String sdatetime = StringTool.getString(TJDODBTool.getInstance()
				.getDBTime(), "yyyy-MM-dd HH:mm:ss");
		result = EktDriver
				.unConsume(1000, Operator.getID(), tredeNo, sdatetime);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			if (control != null)
				control.messageBox(result);
			return false;
		}
		EktDriver.close();
		beep(1);
		consumeCancel(tredeNo);
		return true;
	}

	/**
	 * �жϿ�Ƭ�Ƿ����;��
	 * 
	 * @param cardNo
	 *            String
	 * @return String
	 */
	public String queryTredeNoCancel(String cardNo) {
		if (isClientlink())
			return (String) callServerMethod(cardNo);
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		TParm result = EKTTool.getInstance().queryTradeNoCancel(parm);
		if (result.getCount() <= 0)
			return "";
		return result.getValue("TREDE_NO", 0);
	}

	/**
	 * ȡ������
	 * 
	 * @param tredeNo
	 *            String
	 * @return boolean
	 */
	public boolean consumeCancel(String tredeNo) {
		if (isClientlink())
			return (Boolean) callServerMethod(tredeNo);
		return EKTTool.getInstance().consumeCancel(tredeNo);
	}

	/**
	 * �۷�
	 * 
	 * @param p
	 *            TParm
	 * @return TParm
	 */
	public TParm consume(TParm p) {
		TParm parm = new TParm();
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		String sdatetime = StringTool.getString(p.getTimestamp("OPT_DATE"),
				"yyyy-MM-dd HH:mm:ss");
		double value = p.getDouble("AMT");
		result = EktDriver.consume(1000, Operator.getID(), value, sdatetime);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		TConnection connection = getConnection();
		String tredeNo = result.substring(3);
		p.setData("TREDE_NO", tredeNo);
		p.setData("AMT", value);
		p.setData("STATE", 0);
		p.setData("OPT_USER", Operator.getID());
		p.setData("OPT_TERM", Operator.getIP());
		p.setData("BANK_FLG", "N");// ���п�����
		p.setData("INS_FLG", "N");// ҽ��������
		parm.setData("TREDE_NO", tredeNo);
		if (!insetTrede(p.getData())) {
			parm.setErr(-1, "ҽ�ƿ�д���׵�����!");
		}
		connection.commit();
		connection.close();
		EktDriver.close();
		return parm;
	}

	/**
	 * �۷Ѳ���
	 * 
	 * @param parm
	 *            TParm
	 * @param flg
	 *            int 1��̩��ҽԺҽ�ƿ��۷ֲ���
	 * @return TParm ==================pangben 20110910
	 */
	public TParm consume(TParm parm, int flg) {
		TParm result = null;

		switch (flg) {

		case 1:
			parm.setData("CURRENT_BALANCE", parm.getDouble("EKT_AMT"));
			try {
				result = TXwriteEKTATM(parm, parm.getValue("MR_NO"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result.getErrCode() < 0) {
				System.out.println("ҽ�ƿ��ۿ�ʧ��");
				return result;
			}
			String tredeNo = EKTTool.getInstance().getTradeNo();// �õ�ҽ�ƿ��ⲿ���׺�
			result.setData("TREDE_NO", tredeNo);
			parm.setData("TREDE_NO", tredeNo);
			// parm.setData("AMT", parm.getDouble("AMT"));
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("STATE", 1);// �ѿۿ�״̬
			// }
			if (!insetTrede(parm.getData())) {
				TParm writeParm = new TParm();
				writeParm.setData("CURRENT_BALANCE", parm.getValue("OLD_AMT"));
				writeParm.setData("SEQ", parm.getValue("SEQ"));
				writeParm.setData("MR_NO", parm.getValue("MR_NO"));
				try {
					writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
							parm.getValue("MR_NO"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // ��дҽ�ƿ����
				if (writeParm.getErrCode() < 0)
					System.out.println("err:" + writeParm.getErrText());
				parm.setErr(-1, "ҽ�ƿ�д���׵�����!");
			}
			break;
		}

		return parm;
	}

	/**
	 * ����ҽ�ƿ���Ϣ
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean insetTrede(Map map) {
		if (isClientlink())
			return (Boolean) callServerMethod(map);
		TParm parm = new TParm(map);
		// =====20120225 zhangp modify start
		parm.setData("BANK_FLG", "N");
		// =====20120225 zhangp modify end
		TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrede " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		// ������ɫͨ�����
		if (parm.getValue("FLG").equals("Y")) {
			// ���¹Һ�����������ɫͨ�����
			result = PatAdmTool.getInstance().updateEKTGreen1(
					ektParmTemp(parm), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return false;
			}
		}
		connection.commit();
		connection.close();
		return true;
	}

	/**
	 * ����ҽ�ƿ���Ϣ ̩��ҽԺҽ�ƿ�����
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean insetTrede(Map map, TConnection connection) {
		if (isClientlink())
			return (Boolean) callServerMethod(map, connection);
		TParm parm = new TParm(map);
		// TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().insetTrade(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.insetTrede " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		// ������ɫͨ�����
		if (parm.getValue("FLG").equals("Y")) {
			// ���¹Һ�����������ɫͨ�����
			result = PatAdmTool.getInstance().updateEKTGreen1(
					ektParmTemp(parm), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return false;
			}
		}

		return true;
	}

	/**
	 * ҽ�ƿ� ��ɫͨ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =============pangben 20111009
	 */
	private TParm ektParmTemp(TParm parm) {
		TParm ektParm = new TParm();
		ektParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		// ��ô˾��ﲡ����ɫͨ�����ۿ���
		ektParm.setData("GREEN_BALANCE", StringTool.round(parm
				.getDouble("GREEN_BALANCE"), 2));
		return ektParm;
	}

	/**
	 * ���没����
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @return boolean
	 */
	public boolean saveMRNO(String mrNo, TControl control) {
		return saveMRNO(mrNo, control, true);
	}

	/**
	 * ���没����
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @param beep
	 *            boolean true ����
	 * @return boolean
	 */
	public boolean saveMRNO(String mrNo, TControl control, boolean beep) {
		if (EktDriver.init() != 1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox("�޿�");
			return false;
		}
		result = EktDriver.loadKey("FFFFFFFFFFFF");
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		String data = StringTool.fill0(mrNo, 32);
		result = EktDriver.writeCard(48, data);
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			control.messageBox(result);
			return false;
		}
		EktDriver.close();
		if (beep)
			beep(1);
		return true;
	}

	/**
	 * �Ͼ�����ҽԺҽ�����忨���� ===============pangben modify 20110808
	 * 
	 * @param mrNo
	 *            String
	 * @param control
	 *            TControl
	 * @param beep
	 *            boolean
	 * @return boolean
	 */
	public boolean saveMRNO1(TParm parm, TControl control, boolean beep) {
		// ====����dll======
		if (NJSMCardDriver.init() != 1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		if (NJSMCardYYDriver.init() != -1) {
			control.messageBox("EKTDLL init err!");
			return false;
		}
		// ====�������=====
		if (ReadCardService.LinkReaderPro() != 0) {
			NJSMCardDriver.close();
			control.messageBox("����ʧ��");
			return false;
		}

		String data = StringTool.fill0(parm.getValue("MR_NO"), 32);
		NJSMCardYYDriver.close();
		return true;
		// ReadCardService.WriteCardInfo(data);
		// if (!result.substring(0, 2).equals("00")) {
		// EktDriver.close();
		// control.messageBox(result);
		// return false;
		// }
		// EktDriver.close();

		// return true;
	}

	/**
	 * �Ͼ�����ҽԺд��ҽ���� ===============pangben modify 20110808
	 * 
	 * @param parm
	 *            TParm
	 * @return int
	 */
	public boolean writeEKT(TParm parm, boolean beep) {
		int i = ReadCardService.WriteCardInfo(parm);
		if (i == 0) {
			return true;
		} else
			return false;

	}

	/**
	 * ��ȡ������
	 * 
	 * @return String
	 */
	public String readMRNO() {
		if (EktDriver.init() != 1) {
			System.out.println("EktDriver.init()->EKTDLL init err!");
			return "";
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.open()->" + result);
			return "";
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.hasCard()->�޿�");
			return "";
		}
		result = EktDriver.loadKey("FFFFFFFFFFFF");
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			System.out.println("EktDriver.loadKey()->" + result);
			return "";
		}
		result = EktDriver.readCard(48);
		EktDriver.close();
		if (result.length() > 12)
			result = result.substring(result.length() - 12);
		if ("000000000000".equals(result))
			return "";

		return result;
	}

	/**
	 * ��ȡ����Ϣ
	 * 
	 * @return TParm
	 */
	public TParm getPat() {
		return getPat(true);
	}

	/**
	 * ��ȡ����Ϣ
	 * 
	 * @param beep
	 *            boolean true ����
	 * @return TParm
	 */
	public TParm getPat(boolean beep) {
		TParm parm = new TParm();
		if (EktDriver.init() != 1) {
			parm.setErr(-1, "EKTDLL init err!");
			parm.setData("ERRCode", 1);
			return parm;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setData("ERRCode", "ҽ�ƿ���������");
			parm.setErr(-1, result);
			return parm;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			EktDriver.close();
			parm.setData("ERRCode", "�޿�");
			parm.setErr(-1, "�޿�!");
			return parm;
		}
		result = EktDriver.readUser();
		String s[] = StringTool.parseLine(result, "|");
		if (!s[0].equals("00")) {
			parm.setData("ERRCode", result);
			EktDriver.close();
			parm.setErr(-1, result);
			return parm;
		}
		EktDriver.close();

		// �����Ƿ�����;����
		String tredeNo = queryTredeNoCancel(s[1]);
		if (tredeNo != null && tredeNo.length() > 0) {
			unConsume(tredeNo, null);
			EktDriver.open();
			result = EktDriver.readUser();
			s = StringTool.parseLine(result, "|");
			EktDriver.close();
		}

		parm.setData("CARD_NO", s[1]);
		parm.setData("CARD_TYPE", s[2]);
		parm.setData("CTZ_CODE", s[3]);
		try {
			parm.setData("CURRENT_BALANCE", Double.parseDouble(s[4]));
		} catch (Exception e) {
		}
		parm.setData("NAME", s[5]);
		parm.setData("SEX", s[6]);
		Timestamp birth = StringTool.getTimestamp(s[7], "yyyy-MM-dd");
		parm.setData("BIRTH_DATE", birth);
		parm.setData("ID", s[8]);
		parm.setData("TEL_NO", s[9]);
		if (s.length < 11)
			parm.setData("ADDRESS", "");
		else
			parm.setData("ADDRESS", s[10]);
		parm.setData("MR_NO", readMRNO());
		if (beep)
			beep(1);
		return parm;
	}

	/**
	 * ��ȡ������
	 * 
	 * @param cardNo
	 *            String
	 * @return String
	 */
	public String getMrno(String cardNo) {
		TParm parm = new TParm(
				select("SELECT MR_NO FROM EKT_MASTER WHERE CARD_NO='" + cardNo
						+ "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("MR_NO", 0);
	}

	/**
	 * ����ҽ�ƿ���Ϣ
	 * 
	 * @param map
	 *            Map
	 * @return boolean
	 */
	public boolean createCard(Map map) {
		if (isClientlink())
			return (Boolean) callServerMethod(map);
		TParm parm = new TParm(map);
		TConnection connection = getConnection();
		TParm result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		result = EKTTool.getInstance().insertEKTMaster(parm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		connection.commit();
		connection.close();

		return true;
	}

	/**
	 * ����ҽ�ƿ���Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param control
	 *            TControl
	 * @return TParm ===============pangben 20111007
	 */
	public TParm createCard(TParm parm, TConnection connection) {
		TParm result = EKTTool.getInstance().deleteEKTMaster(parm, connection);
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
		return result;
	}

	/**
	 * ���п���ҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateEKTAndBank(TParm parm) {
		TParm result = EKTTool.getInstance().updateEKTAndBank(parm);
		if (result.getErrCode() != 0) {
			err("ERR:EKTIO.createCard " + result.getErrCode()
					+ result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * ̩��ҽԺ�۷ѽӿ�
	 * 
	 * @param parm
	 *            TParm
	 * @param caseNo
	 *            String �����
	 * @param control
	 *            TControl
	 * @return TParm [RX_NO,ORDER_CODE,SEQ_NO,AMT,EXEC_FLG,RECEIPT_FLG,BILL_FLG]
	 *         OP_TYPE 0 ҽ�ƿ����عر� OP_TYPE 1 �۷� OP_TYPE 2 �˷� OP_TYPE 3 ȡ�����涯��
	 *         OP_TYPE 4 �޷��ò��� OP_TYPE 5 �޿�������ҽ�� OP_TYPE -1 ���ò���������߲����ں�̨����ҽ�ƿ�
	 *         RX_LIST �����б�
	 */
	public TParm onOPDAccntClient(TParm parm, String caseNo, TControl control) {
		TParm result = new TParm();
		// ��ֹ�ڷ������˵���
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onOPDAccntClient �������˽�ֹ���ñ�����");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// ҽ�ƿ������Ƿ�����
		if (!ektSwitch()) {
			result.setData("OP_TYPE", 0);
			return result;
		}
		if (parm == null) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient TParm ����Ϊ��");
			result.setData("OP_TYPE", -1);
			return result;
		}
		if (caseNo == null || caseNo.length() == 0) {
			//System.out.println("ERR:EKTIO.onOPDAccntClient caseNo ����Ϊ��");
			result.setData("OP_TYPE", -1);
			return result;
		}
		// =========pangben 20110919 �޸�ҽ�ƿ���������
		// TParm readCard = getPat(false);
		TParm readCard = parm.getParm("ektParm");// ̩��ҽ�ƿ���������
		if (parm.getBoolean("IS_NEW") && readCard.getErrCode() == -1) {
			if ("�޿�".equals(readCard.getValue("ERRCode")))
				control.messageBox("�޿�,����ҽ������δ�շѣ�");
			else
				control
						.messageBox(readCard.getValue("ERRCode")
								+ ",����ҽ������δ�շѣ�");
			result.setData("OP_TYPE", 5);
			return result;

		}

		if (readCard.getErrCode() != 0) {
			result.setData("OP_TYPE", -1);
			control.messageBox("ҽ�ƿ�����,��;ʧ��");
			return result;
		}
		//parm.setData("CASE_NO", caseNo);
		// ̩��ҽԺ��ÿ���
		String cardNo = readCard.getValue("MR_NO") + readCard.getValue("SEQ");
		double oldAmt = readCard.getDouble("CURRENT_BALANCE");//ҽ�ƿ����
		String mrNo = parm.getValue("MR_NO");
		String businessType = parm.getValue("BUSINESS_TYPE");
		//��ѯ���ͻ������Һ�REG,REGT���շ�OPB,OPBT,ODO,ODOT
		String ektTradeType = parm.getValue("EKT_TRADE_TYPE");
		String type_flg = parm.getValue("TYPE_FLG");// �˷Ѳ���
		String ins_flg = parm.getValue("INS_FLG");// ҽ����ע��
		double insAmt = parm.getDouble("INS_AMT");// ҽ�����
		String unFlg = parm.getValue("UN_FLG");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		String tradeNo = "";
		if (!mrNo.equals(readCard.getValue("MR_NO"))) {
			if (parm.getBoolean("IS_NEW")) {
				control.messageBox("�˿�Ƭ�����ڸû���,����ҽ������δ�շѣ�");
				result.setData("OP_TYPE", 5);
				return result;
			}
			control.messageBox("�˿�Ƭ�����ڸû���!");
			result.setData("OP_TYPE", 3);
			return result;
		}
		int type = 1;
		double ektAMT = 0.00;// ҽ�ƿ�ִ���Ժ���
		double ektOldAMT = 0.00;// ҽ�ƿ�ԭ��������ʧ��ʱʹ��
		String cancelTrede = null;// �ۿ����ʧ�ܻع�ҽ�ƿ���������
		String opbektFeeFlg = parm.getValue("OPBEKTFEE_FLG");// ҽ�����س������
		double opbAmt = 0.00;// ����ҽ��վ�����Ľ��
		double greenBalance = 0.00;// ��ɫͨ���ܿۿ���
		double greenPathTotal = 0.00;// ��ɫͨ���������
		//zhangp
		double payOther3 = parm.getDouble("PAY_OTHER3");
		double payOther4 = parm.getDouble("PAY_OTHER4");

		String greenFlg = null;// �ж��Ƿ������ɫͨ�������BIL_OPB_RECP
		// ��PAY_MEDICAL_CARD����ʱ��Ҫ�ж�Ϊ0ʱ�Ĳ���
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control ����Ϊ��");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			//zhangp
			p.setData("PAY_OTHER3", payOther3);
			p.setData("PAY_OTHER4", payOther4);
			
			// �ۿ���
			double amt = 0.00;
			// ��ѯ�˾��ﲡ���������ݻ��ܽ��
			//TParm orderSumParm=parm.getParm("orderSumParm");
			//��ѯ�˲������շ�δ��Ʊ���������ݻ��ܽ��
			//TParm ektSumParm =parm.getParm("ektSumParm");
			amt=parm.getDouble("SHOW_AMT");//δ�շѵ��ܽ�� ��ʾ���
			if (amt == 0) {
				result.setData("OP_TYPE", 6);// û����Ҫ����������
				return result;
			}
			if (amt != 0) {
				opbAmt = amt;
				p.setData("AMT", amt);
				p.setData("EXE_AMT", parm.getDouble("EXE_AMT"));//ִ�н��(EKT_TRADE �д˴� �����Ľ��)
				//δ��Ʊ�����ܽ��
				readCard.setData("NAME", parm.getValue("NAME"));
				readCard.setData("SEX", parm.getValue("SEX"));
				p.setData("READ_CARD", readCard.getData());
				p.setData("BUSINESS_TYPE", businessType);// �ۿ��ֶ�
				p.setData("EKT_TRADE_TYPE", ektTradeType);
				p.setData("TYPE_FLG", type_flg);// �˷�ע��
				p.setData("INS_FLG", ins_flg);// ҽ��ע��
				p.setData("INS_AMT", insAmt);// ҽ�����
				p.setData("OPBEKTFEE_FLG", opbektFeeFlg);
				p.setData("TRADE_SUM_NO", parm.getValue("TRADE_SUM_NO"));////UPDATE EKT_TRADE �帺����,ҽ�ƿ��ۿ��ڲ����׺���,��ʽ'xxx','xxx'
				//p.setData("newParm", parm.getParm("newParm").getData());//����ҽ��վҽ������Ҫ������ҽ��(��ɾ�����ݼ���)
				TParm r = null;
				// ��ѯ��ɫͨ��ʹ�ý��
				TParm tempParm=new TParm();
				tempParm.setData("CASE_NO",caseNo);
				TParm greenParm = PatAdmTool.getInstance().selEKTByMrNo(tempParm);
				if (greenParm.getErrCode() < 0) {
					result.setData("OP_TYPE", -1);
					return result;
				}
				if (amt > (oldAmt + greenParm.getDouble("GREEN_BALANCE", 0))) {// ҽ�ƿ��н��С�ڴ˴��շѵĽ��
					if (null != unFlg && unFlg.equals("Y")) {// ҽ���޸�ҽ������
						parm.setData("OPD_UN_FLG", "Y");
						p.setData("GREEN_BALANCE", greenParm.getDouble(
								"GREEN_BALANCE", 0));// ��ɫͨ��ʣ����
						p.setData("GREEN_PATH_TOTAL", greenParm.getDouble(
								"GREEN_PATH_TOTAL", 0));// ��ɫͨ���������
						p.setData("unParm", parm.getParm("unParm").getData());
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTOpdChageUI.x", p);
						result.setData("unParm", r.getData());
					} else {
						r = (TParm) control.openDialog(
								"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
					}
				} else {
					r = (TParm) control.openDialog(
							"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
				}
				System.out.println("������ִ�н��洫�صĲ��� rrrrrrr is::"+r);
				if (r == null) {
					// System.out.println("asdadasd");
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					result.setData("OP_TYPE", 3);
					return result;
				}
				type = r.getInt("OP_TYPE");
				// ����
				if (type == 2) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (null == unFlg
						|| unFlg.equals("N")
						|| amt <= (oldAmt + greenParm.getDouble(
								"GREEN_BALANCE", 0))) {
					// cardNo = r.getValue("CARD_NO");
					// =========pangben 20111024 start
					ektAMT = r.getDouble("EKTNEW_AMT");// ����ҽ�ƿ��еĽ��
					ektOldAMT = r.getDouble("OLD_AMT");
					if (null != r.getValue("AMT")
							&& r.getValue("AMT").length() > 0) {
						result.setData("AMT", r.getValue("AMT")); // �շѽ��
						result.setData("EKT_USE", r.getDouble("EKT_USE")); // ��ҽ�ƿ����
						// greenUseAmt=r.getDouble("GREEN_USE");//��ɫͨ��ʹ�ý��
						// ektUseAmt= r.getDouble("EKT_USE");//ҽ�ƿ�ʹ�ý��
						result.setData("GREEN_USE", r.getDouble("GREEN_USE")); // ����ɫͨ�����
					}
					greenBalance = r.getDouble("GREEN_BALANCE"); // ��ɫͨ��δ�ۿ���
					greenPathTotal = r.getDouble("GREEN_PATH_TOTAL"); // ��ɫͨ���ܽ��
					// =========pangben 20111024 stop
					
					tradeNo = r.getValue("TRADE_NO");
					cancelTrede = r.getValue("CANCLE_TREDE");
					greenFlg = r.getValue("GREEN_FLG");
				}
			} 
		}
		double currentBalance = readCard.getDouble("CURRENT_BALANCE");
		List list = onOPDAccnt(parm.getData(), cardNo, caseNo,
				Operator.getID(), Operator.getIP(), type, currentBalance,
				ektAMT, greenBalance, greenPathTotal,businessType);
		result.setData("EKTNEW_AMT", ektAMT);
		result.setData("OLD_AMT", ektOldAMT); // ҽ�ƿ��Ѿ������Ժ�Ľ��
		if (list == null) {
			result.setData("OP_TYPE", 4);
			return result;
		}
		result.setData("OP_TYPE", type);
		result.setData("RX_LIST", list);
		result.setData("TRADE_NO", tradeNo);
		result.setData("CARD_NO", cardNo);
		result.setData("OPD_UN_FLG",parm.getValue("OPD_UN_FLG"));// ҽ���޸�ҽ������
		result.setData("CANCLE_TREDE", cancelTrede);// ����ʹ��
		result.setData("OPB_AMT", opbAmt);// ����ҽ��վ�������
		result.setData("GREEN_FLG", greenFlg);// ���BIL_OPB_RECP
		result.setData("GREEN_BALANCE", greenBalance); // ��ɫͨ��δ�ۿ���
		result.setData("GREEN_PATH_TOTAL", greenPathTotal); // ��ɫͨ���ܽ��
		// ������ʱʹ�������ܿ��Ƿ������ɫͨ��
		return result;
	}
	/**
	 * ҽ���˷Ѳ���
	 * @return
	 */
	public TParm insUnFee(TParm p, TControl control){
		TParm readCard = TXreadEKT();// ̩��ҽ�ƿ���������
		if (null==readCard || null==readCard.getValue("MR_NO")) {
			p.setErr(-1,"���ҽ�ƿ���Ϣʧ��");
			return p;
		}
		
		p.setData("CURRENT_BALANCE",readCard.getDouble("CURRENT_BALANCE") - p.getDouble("AMT"));
		p.setData("SEQ",readCard.getValue("SEQ"));
		if (!readCard.getValue("MR_NO").equals(p.getValue("MR_NO"))) {
			p.setErr(-1,"������Ϣ����");
			return p;
		}
		TParm returnParm = new TParm();
		try {
			returnParm = EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		TParm r = (TParm)control.openDialog(
//				"%ROOT%\\config\\ekt\\EKTOpdChageUI.x", p);
		return returnParm;
	}
	/**
	 * 
	 * ����ҽ�ƿ��Ʒѽӿ�
	 * 
	 * @param data
	 *            Map ORDER_CODE,RX_NO,SEQ_NO,AMT
	 * @param businessNo
	 *            String
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param cashierCode
	 *            String ������Ա
	 * @param term
	 *            String
	 * @param type
	 *            int
	 * @param currentBalance
	 *            double ektAmt ҽ�ƿ����Ѿ��ƷѵĽ��
	 * @param greenUseAmt
	 *            ��ɫͨ��ʹ�ý��
	 * @param ektUseAmt
	 *            ҽ�ƿ�ʹ�ý��
	 * @param greenBalance
	 *            double//��ɫͨ���ܿۿ���
	 * @param greenPathTotal
	 *            double//��ɫͨ���������
	 * @return List
	 */
	public List onOPDAccnt(Map data, String cardNo,
			String caseNo, String cashierCode, String term, int type,
			double currentBalance, double ektAmt, double greenBalance,
			double greenPathTotal,String businessType) {
		if (cardNo == null || cardNo.length() == 0)
			return null;
		if (isClientlink())
			return (List) callServerMethod(data, cardNo, caseNo,
					cashierCode, term, type, currentBalance, ektAmt,
					greenBalance, greenPathTotal,businessType);

		TParm ioparm = new TParm(data);
		// ����ҽ�ƿ���ϸ��EKT_ACCNTDETAIL
		// ��Ʊ���޸�
		String mrNo = ioparm.getValue("MR_NO");
		if (mrNo == null || mrNo.length() == 0) {
			System.out.println("ERR:EKTIO.onOPDAccnt �����ڵ�CaseNo " + caseNo);
			return null;
		}
		String opdFlg = ioparm.getValue("OPD_UN_FLG");// ҽ���޸ĵ�ҽ������ҽ�ƿ����ִ�еĲ���
		if (null != opdFlg && opdFlg.equals("Y")) {
			TParm unParm = ioparm.getParm("unParm");
			ioparm = unParm;
		}
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");
		List list = new ArrayList();
		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			TParm parm = (TParm) map.get(rxNo);
			if (parm == null) {
				parm = new TParm();
				map.put(rxNo, parm);
			}
			list.add(rxNo);
			parm.addData("ORDER_CODE", ioparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", ioparm.getData("SEQ_NO", i));
			parm.addData("AMT", ioparm.getData("AMT", i));
			parm.addData("BILL_FLG", ioparm.getData("BILL_FLG", i));
		}
		// list.add(pv[3]);
		return list;
	}

	private double getDoubleSum(TParm parm, String name) {
		double amt = 0;
		int count = parm.getCount(name);
		for (int i = 0; i < count; i++) {
			if (parm.getBoolean("BILL_FLG", i))
				amt += parm.getDouble(name, i);
		}
		return amt;
	}

	/**
	 * �õ�������
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getMrNo(String caseNo) {
		if (isClientlink())
			return (String) callServerMethod(caseNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = EKTTool.getInstance().getMrNo(parm);
		return result.getValue("MR_NO", 0);
	}

	/**
	 * �õ�����ҽ�ƿ����
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxBalance(String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxBalance(parm);
		return result.getDouble("CURRENT_BALANCE", 0);
	}

	/**
	 * �õ�����ҽ�ƿ�ԭ��
	 * 
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxOriginal(String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxOriginal(parm);
		return result.getDouble("ORIGINAL_BALANCE", 0);
	}

	/**
	 * �õ���ǰ���
	 * 
	 * @param cardNo
	 *            String
	 * @return TParm
	 */
	public TParm getCurrentBalance(String cardNo) {
		TParm p = new TParm();
		p.setData("CARD_NO", cardNo);
		return EKTTool.getInstance().getEKTCurrentBalance(p);
	}

	/**
	 * �õ���Ƭ����
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	// zhangp 20120106 ע��
	// public String getCardNo(String caseNo) {
	// if (isClientlink())
	// return (String) callServerMethod(caseNo);
	// TParm parm = new TParm();
	// parm.setData("CASE_NO", caseNo);
	// TParm result = EKTTool.getInstance().getCardNo(parm);
	// return result.getValue("CARD_NO", 0);
	// }

	/**
	 * �õ���������
	 * 
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param rxNo
	 *            String
	 * @return double
	 */
	public double getRxAmt(String cardNo, String caseNo, String rxNo) {
		if (isClientlink())
			return (Double) callServerMethod(cardNo, caseNo, rxNo);
		TParm parm = new TParm();
		parm.setData("CARD_NO", cardNo);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RX_NO", rxNo);
		TParm result = EKTTool.getInstance().getRxAmt(parm);
		return result.getDouble("AMT", 0);
	}

	public static void main(String args[]) {
		JavaHisDebug.initClient();
		TParm parm = new TParm();
		double[] d = new double[] { 17.5, 21, 16.8, 12.25, 13.13, 39.38, 2.52,
				13.13 };
		for (int i = 0; i < d.length; i++) {
			parm.addData("ORDER_CODE", "1");
			parm.addData("RX_NO", "101007000011");
			parm.addData("AMT", d[i]);
			parm.addData("EXEC_FLG", false);
			parm.addData("RECEIPT_FLG", false);
			parm.addData("BILL_FLG", true);
		}

		// System.out.println(EKTIO.getInstance().getAmt(parm.getData(),"0792D24D","101007000006"));
	}

	/**
	 * �õ������շѷ����ܶ�
	 * 
	 * @param data
	 *            Map
	 * @param cardNo
	 *            String
	 * @param caseNo
	 *            String
	 * @return double
	 */
	public double getAmt(Map data, String cardNo, String caseNo) {
		if (isClientlink())
			return (Double) callServerMethod(data, cardNo, caseNo);
		TParm ioparm = new TParm(data);
		Map map = new HashMap();
		int count = ioparm.getCount("ORDER_CODE");
		// System.out.println("ioParm::" + ioparm);
		for (int i = 0; i < count; i++) {
			String rxNo = ioparm.getValue("RX_NO", i);
			double amt = ioparm.getDouble("AMT", i);
			if (amt > 0)
				amt = ((int) (amt * 100.0 + 0.5)) / 100.0;
			else if (amt < 0)
				amt = ((int) (amt * 100.0 - 0.5)) / 100.0;
			if (!ioparm.getBoolean("BILL_FLG", i))
				amt = 0;
			// System.out.println("����" + rxNo + " code " +
			// ioparm.getData("ORDER_CODE",i) + " ��� " + amt);
			Object value = map.get(rxNo);
			if (value == null)
				map.put(rxNo, amt);
			else
				map.put(rxNo, (Double) value + amt);
		}
		// System.out.println("�ۼ����� " + map);
		Iterator iterator = map.keySet().iterator();
		double sumamt = 0;
		while (iterator.hasNext()) {
			String rxNo = (String) iterator.next();
			sumamt += (Double) map.get(rxNo);
			double d = getRxAmt(cardNo, caseNo, rxNo);
			// System.out.println("���� " + rxNo + " �ܽ��" + d);
			sumamt -= d;
		}
		if (sumamt > 0)
			sumamt = ((int) (sumamt * 100.0 + 0.5)) / 100.0;
		else if (sumamt < 0)
			sumamt = ((int) (sumamt * 100.0 - 0.5)) / 100.0;
		// System.out.println("�ܷ��� " + sumamt);
		return sumamt;
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
			Object[] pv, Timestamp date, TConnection connection, int type,
			double greenBalance, double greenPathTotAl,String businessType) {

		String businessNo = (String) pv[0];
		int index = (Integer) pv[1];
		double originalBalance = (Double) pv[2];// ҽ�ƿ��еĽ��δ�ۿ�ǰ�Ľ��
		double dB = (Double) pv[3];
		TParm p = new TParm();
		p.setData("CASE_NO", caseNo);
		TParm r = new TParm();
		p.setData("RX_NO", rxNo);
		r = EKTTool.getInstance().getDetail(p);
		int count = r.getCount("ORDER_CODE");
		// ҽ��������������˿�
		for (int i = 0; i < count; i++) {
			index++;
			TParm parm = new TParm();
			parm.setData("BUSINESS_NO", businessNo);
			parm.setData("BUSINESS_SEQ", index);
			parm.setData("CARD_NO", cardNo);
			parm.setData("MR_NO", mrNo);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_CODE", r.getValue("ORDER_CODE", i));
			parm.setData("RX_NO", rxNo);
			parm.setData("SEQ_NO", r.getInt("SEQ_NO", i));
			double amt = -r.getDouble("BUSINESS_AMT", i);// ��һ��ҽ����Ҫ�ۿ�Ľ��
			parm.setData("CHARGE_FLG", "2");// ״̬(1,�ۿ�;2,�˿�;3,ҽ�ƿ���ֵ,4,�ƿ�,5,����,6,����)
			parm.setData("ORIGINAL_BALANCE", originalBalance);// �շ�ǰ���
			parm.setData("BUSINESS_AMT", amt);
			double greenTempAmt = 0.00;// ʹ����ɫͨ���Ľ��
			dB -= amt;
			if (greenBalance < greenPathTotAl) {
				greenTempAmt -= amt;// ʹ����ɫͨ���Ľ��
				if (greenBalance + greenTempAmt >= greenPathTotAl) {// �жϻ��˽���Ƿ񳬹��������
					originalBalance += greenBalance + greenTempAmt
							- greenPathTotAl;
					greenBalance = greenPathTotAl;
					greenTempAmt = greenPathTotAl - greenBalance;// ������
				} else {// ����ʹ����ɫͨ�����
					greenBalance += greenTempAmt;// ��ɫͨ��ʣ����
					originalBalance = 0;
				}
			} else {
				originalBalance -= amt;
				greenTempAmt = 0;
			}
			parm.setData("CURRENT_BALANCE", originalBalance);
			parm.setData("GREEN_BALANCE", greenBalance);// ��ɫͨ��ʣ����=��ǰʣ����-ÿһ�ʲ������
			parm.setData("GREEN_BUSINESS_AMT", greenTempAmt);// ��ɫͨ��ÿһ��ʹ�ý��
			parm.setData("CASHIER_CODE", cashierCode);
			parm.setData("BUSINESS_DATE", date);
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
			parm.setData("OPT_DATE", date);
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
		// }
		// if (null != uEktFlg && uEktFlg.equals("Y")) {// ҽ�ƿ��˷Ѳ���
		// // ��ϸ����޸Ĳ���ͨ��Ʊ�ݸ�������
		// r = EKTTool.getInstance().detailCancelPrintNo(p, connection);
		// }else{
		r = EKTTool.getInstance().detailCancel(p, connection);
		// }
		if (r.getErrCode() != 0) {
			err("ERR:EKTIO.onOPDAccnt.detailCancel " + r.getErrCode()
					+ r.getErrText());
			connection.rollback();
			connection.close();
			return false;
		}
		if (type == 1) {
			count = ioparm.getCount("ORDER_CODE");
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
				double greenTempAmt = 0.00;
				if (amt > originalBalance) {

					greenTempAmt = amt - originalBalance;// ʹ����ɫͨ���Ľ��
					greenBalance -= greenTempAmt;// ��ɫͨ��ʣ����
					originalBalance = 0;
				} else {
					originalBalance -= amt;// ����ҽ�ƿ��ۿ�
				}

				parm.setData("GREEN_BALANCE", greenBalance);// ��ɫͨ��ʣ����=��ǰʣ����-ÿһ�ʲ������
				parm.setData("GREEN_BUSINESS_AMT", greenTempAmt);// ��ɫͨ��ÿһ��ʹ�ý��
				// if (insFlg) {
				// originalBalance += amt;// ҽ���˿����
				// } else {
				// }
				parm.setData("CURRENT_BALANCE", originalBalance);
				parm.setData("CASHIER_CODE", cashierCode);
				parm.setData("BUSINESS_DATE", date);
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
				parm.setData("OPT_DATE", date);
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
		}
		pv[0] = businessNo;
		pv[1] = index;
		pv[2] = originalBalance;
		pv[3] = dB;
		return true;
	}

	/**
	 * ���Խ���
	 * 
	 * @param tredeNo
	 *            String
	 * @return String
	 */
	public String check(String tredeNo, String caseNo,double reduceAmt) {
		if (isClientlink())
			return (String) callServerMethod(tredeNo, caseNo,reduceAmt);
		return EKTTool.getInstance().check(tredeNo, caseNo,reduceAmt);
	}

	/**
	 * ����
	 * 
	 * @param times
	 *            int
	 * @return String
	 */
	public static String beep(int times) {
		if (true)
			return "1";
		if (EktDriver.init() != 1) {
			//System.out.println("EktDriver.init() err");
			return "";
		}
		EktDriver.open();
		String value = EktDriver.beep(times);
		EktDriver.close();
		return value;
	}

	/*
	 * public static void main(String args[]) { JavaHisDebug.initClient(); TParm
	 * parm = new TParm(); //MR_NO,CASE_NO,ORDER_CODE,RX_NO,SEQ_NO,AMT for(int j
	 * = 0;j < 2;j++) for(int i = 0;i < 3;i++) { parm.addData("RX_NO",j);
	 * parm.addData("ORDER_CODE", "ORDER_CODE" + i); parm.addData("SEQ_NO", i);
	 * parm.addData("AMT", 10); }
	 * //EKTIO.getInstance().onOPDAccnt(parm.getData()
	 * ,"0001","100113000006","cashierCode","term",1); }
	 */
	/**
	 * �ۿ����
	 * 
	 * @param parm
	 *            TParm
	 * @param control
	 *            TControl
	 * @return TParm
	 */
	public TParm onREGAccntClient(TParm parm, TControl control) {
		TParm result = new TParm();
		// ��ֹ�ڷ������˵���
		if (!isClientlink()) {
			System.out.println("ERR:EKTIO.onREGAccntClient �������˽�ֹ���ñ�����");
			result.setData("REG_TYPE", -1);
			return result;
		}

		// ������������
		if (null == parm || null == parm.getValue("CASE_NO")
				|| parm.getValue("CASE_NO").length() <= 0) {
			//System.out.println("ERR:EKTIO.onREGAccntClient TParm ����Ϊ��");
			result.setData("REG_TYPE", -1);
			return result;
		}
		result = TXreadEKT();
		// ��ȡҽ�ƿ��ش�����������
		if (result.getErrCode() < 0 || null == result.getValue("MR_NO")
				|| result.getValue("MR_NO").length() <= 0) {
			//System.out.println("ERR:EKTIO.onREGAccntClient �������ִ���");
			result.setData("REG_TYPE", -1);
			return result;
		}
		// ���Ǳ��˿�
		if (!result.getValue("MR_NO").equals(parm.getValue("MR_NO"))) {
			control.messageBox("�˿�Ƭ�����ڸû���!");
			result.setData("REG_TYPE", 3);
			return result;
		}
		String tredeNo = null;
		// ����
		String cardNo = result.getValue("CARD_NO");
		String caseNo = parm.getValue("CASE_NO");
		// ���
		double oldAmt = result.getDouble("CURRENT_BALANCE");
		// ������
		String mrNo = result.getValue("MR_NO");
		// ����
		String businessType = parm.getValue("BUSINESS_TYPE");
		int type = 1;
		// ���׺�
		String businessNo = EKTTool.getInstance().getBusinessNo();
		if (EKTDialogSwitch()) {
			if (control == null) {
				//System.out.println("ERR:EKTIO.onOPDAccntClient control ����Ϊ��");
				result.setData("OP_TYPE", -1);
				return result;
			}
			TParm p = new TParm();
			p.setData("CASE_NO", caseNo);
			p.setData("CARD_NO", cardNo);
			p.setData("MR_NO", mrNo);
			// ҽ�ƿ�������0
			if (oldAmt != 0) {
				p.setData("AMT", oldAmt);
				p.setData("READ_CARD", parm.getData());
				p.setData("BUSINESS_NO", businessNo);
				p.setData("BUSINESS_TYPE", businessType);
				beep(1);
				TParm r = (TParm) control.openDialog(
						"%ROOT%\\config\\ekt\\EKTChageUI.x", p);
				if (r == null) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				if (r.getErrCode() < 0) {
					control.messageBox(r.getErrText());
					result.setData("OP_TYPE", 3);
					return result;
				}
				// cardNo = r.getValue("CARD_NO");
				type = r.getInt("OP_TYPE");
				// ����
				if (type == 2) {
					result.setData("OP_TYPE", 3);
					return result;
				}
				tredeNo = r.getValue("TREDE_NO");
			} else {
				tredeNo = "�޽���" + businessNo;
				//System.out.println("tredeNo:::" + tredeNo);
				TParm wparm = new TParm();
				wparm.setData("TREDE_NO", tredeNo);
				wparm.setData("CARD_NO", cardNo);
				wparm.setData("MR_NO", mrNo);
				wparm.setData("CASE_NO", caseNo);
				wparm.setData("BUSINESS_NO", businessNo);
				wparm.setData("PAT_NAME", PatTool.getInstance().getNameForMrno(
						mrNo));
				wparm.setData("OLD_AMT", oldAmt);
				wparm.setData("AMT", 0);
				wparm.setData("STATE", 1);
				wparm.setData("BUSINESS_TYPE", businessType);
				wparm.setData("OPT_USER", Operator.getID());
				wparm.setData("OPT_TERM", Operator.getIP());
				wparm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
				if (!insetTrede(wparm.getData())) {
					control.messageBox("ҽ�ƿ�д���׵�����!");
					result.setErr(-1, "ҽ�ƿ�д���׵�����!");
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * ̩��ҽԺ��ҽ�ƿ�����
	 * 
	 * @param commPort
	 *            String Ĭ�ϲ����ǡ�COM1�� ���ز�����
	 * @return TParm
	 */
	public TParm readEkt() throws Exception {

//		CardRW cardRW = new CardRW();
//		int i = CardRW.init();
//		// д��
//		/*
//		 * TParm param=cardRW.writeEKT("COM1", "123456789987001100.12----");
//		 * if(param.getErrCode()==-1){
//		 * System.out.println("=========д����������.============");
//		 * System.out.println(param.getErrParm().getErrText()); }
//		 */
//
//		// ����
//		TParm param1 = cardRW.readEKT(COM);
////		System.out.println("param1 is:"+param1);
//		if (param1.getErrCode() == -1) {
////			System.out.println("=========������������.============");
////			System.out.println(param1.getErrParm().getErrText());
//		}
//		CardRW.free();
		//add by huangtt 20131225
		TParm param1= new TParm();
		VRTool vrTool = new VRTool();
		if(vrTool.getVrSwitch()){
			com.javahis.device.card.impl.VReadCard cardRW = new com.javahis.device.card.impl.VReadCard();
			cardDto = cardRW.readMedicalCard();
		}else{
			com.javahis.device.card.impl.MCSCardRWUtil cardRW = new com.javahis.device.card.impl.MCSCardRWUtil();
			cardDto = cardRW.readMedicalCard();
		}
		param1.setData("MR_NO", cardDto.getMrno());
		param1.setData("SEQ", cardDto.getSeq());
		return param1;
	}

	/**
	 ** ̩��ҽԺ��ҽ�ƿ�����
	 * 
	 * 
	 * ====zhangp 20120105 �ж�ҽ�ƿ��Ƿ���Ч
	 * 
	 * @param commPort
	 *            String Ĭ�ϲ����ǡ�COM1�� ���ز�����
	 * @return TParm zhangp 20120105 �ж�ҽ�ƿ��Ƿ���Ч
	 */
	public TParm TXreadEKT() {
		TParm parm=new TParm();
		TParm result =  new TParm();
		try {
			parm = readEkt();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			result.setErr(-1, "����ʧ�ܣ�");
			e.printStackTrace();
			return result;
		}
		String mrNo = parm.getValue("MR_NO");
		String seq = parm.getValue("SEQ");
		String sql = "SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO," +
				"A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE,A.CARD_TYPE"
				+ " FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C WHERE A.MR_NO = '"
				+ mrNo
				+ "' AND A.CARD_SEQ = '"
				+ seq
				+ "' AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
//		System.out.println(sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			result.setErr(-1, "�������");
			return result;
		}
		if (result.getCount() <= 0) {
			result.setErr(-1, "��ҽ�ƿ���Ч");
			return result;
		}
		return result.getRow(0);
	}
	/**
	 * ̩��ҽԺдҽ�ƿ�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void TXwriteEKT(TParm parm) throws Exception{
//		CardRW cardRW = new CardRW();
//		CardRW.init();
//		String currentBalance = parm.getValue("CURRENT_BALANCE");
		String inputData = parm.getValue("MR_NO");
//		TParm result = cardRW.writeEKT(COM, inputData, parm.getValue("SEQ"),
//				currentBalance);
//		CardRW.free();
//		com.javahis.device.card.ICardRW cardRW;
		VRTool vrTool = new VRTool();
		if(vrTool.getVrSwitch()){
			com.javahis.device.card.impl.VReadCard cardRW = new com.javahis.device.card.impl.VReadCard();
			cardRW.writeMedicalCard(parm.getValue("SEQ"), inputData, parm.getValue("TYPE"), parm.getDouble("CURRENT_BALANCE"));
		}else{
			com.javahis.device.card.impl.MCSCardRWUtil cardRW = new com.javahis.device.card.impl.MCSCardRWUtil();
			cardRW.writeMedicalCard(parm.getValue("SEQ"), inputData, parm.getValue("TYPE"), parm.getDouble("CURRENT_BALANCE"));
		}
		
//		return result;
	}

	/**
	 * ̩��ҽԺҽ�ƿ��ۿ����
	 * 
	 * 
	 * ======zhangp 20120106 modify
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm TXwriteEKTATM(TParm parm, String mrNo) throws Exception{
		//TParm readParm = readEkt();
		// ======zhangp 20120225 modify start ��ʱ����
		// String card_no = readParm.getValue("MR_NO") +
		// readParm.getValue("SEQ");
		// String sql =
		// "SELECT CURRENT_BALANCE FROM EKT_MASTER WHERE CARD_NO = '"
		// + card_no + "' AND CURRENT_BALANCE = '"
		// + readParm.getValue("CURRENT_BALANCE") + "'";
		// TParm readBalance = new TParm(TJDODBTool.getInstance().select(sql));
		// if (readBalance.getErrCode() < 0) {
		// readBalance.setErr(-1, "������");
		// return readBalance;
		// }
		// if (readBalance.getCount() <= 0) {
		// readBalance.setErr(-1, "��ҽ�ƿ���Ч");
		// return readBalance;
		// }
		// ======zhangp 20120225 modify end ��ʱ����
//		CardRW cardRW = new CardRW();
//		CardRW.init();
//		// ����ҽ�ƿ������ֲ���ʮλ�����ֵĲ���
		String currentBalance = parm.getValue("CURRENT_BALANCE");
		double balance = Double.parseDouble(currentBalance);
//		TParm result = cardRW.writeBalance(COM, currentBalance);
//		CardRW.free();
//		com.javahis.device.card.ICardRW cardRW;
		VRTool vrTool = new VRTool();
		if(vrTool.getVrSwitch()){
			com.javahis.device.card.impl.VReadCard cardRW = new com.javahis.device.card.impl.VReadCard();
		}else{
			com.javahis.device.card.impl.MCSCardRWUtil cardRW = new com.javahis.device.card.impl.MCSCardRWUtil();
			cardRW.writeMedicalCardBalance(balance);
		}
		String cardNo = parm.getValue("MR_NO") + parm.getValue("SEQ");
		TParm result=new TParm();
		if(!createCard(cardNo, mrNo, balance)){
			result.setErr(-1,"ҽ�ƿ�д������ʧ��");
			return result;
		}
		return result;
	}
	/**
	 * �õ�debug���
	 * 
	 * @return
	 */
	public String getEktPort() {
		String com = "";
		com = getProp().getString("", "ekt.port");
		if (com == null || com.trim().length() <= 0) {
			//System.out.println("�����ļ�ҽ�ƿ�com��Ǵ���");
		}
		return com;
	}

	/**
	 * ��ȡ TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			//System.out.println("TConfig.x �ļ�û���ҵ���");
		}
		return config;
	}
	
}
