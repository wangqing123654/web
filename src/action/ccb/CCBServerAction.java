package action.ccb;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.reg.REGCcbReTool;
import jdo.reg.REGCcbTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.DateTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҽ��ͨ��Ŀ�����е��ýӿ�
 * </p>
 * 
 * <p>
 * Description:ҽ��ͨ��Ŀ�����е��ýӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author fuwj 20120805
 * @version 1.0
 */
public class CCBServerAction extends TAction {

	/**
	 * ������
	 */
	public CCBServerAction() {

	}

	/**
	 * Ժ�ڲ�����Ϣ�����ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm SignTran(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");
		// ������д������
		TParm parm = new TParm();
		parm.setData("CCB_PERSON_NO",
				CommitData.get("PERSON_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("PERSON_NO")).trim());
		parm.setData("PAT_NAME", CommitData.get("NAME") == null ? new TNull(
				String.class) : ((String) CommitData.get("NAME")).trim());
		parm.setData("IDNO", CommitData.get("ID_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("ID_NO")).trim());
		String sex = REGCcbTool.getInstance().isSex(
				(String) CommitData.get("SEX"));
		parm.setData("SEX_CODE", CommitData.get("SEX"));
		parm.setData("BIRTH_DATE", CommitData.get("BIRTHDATE"));
		parm.setData("TEL_HOME", CommitData.get("TEL"));
		parm.setData("ADDRESS", CommitData.get("ADDRESS"));
		// OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("OPT_DATE", OperateInfo.get("OPT_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));

		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		resultData = REGCcbTool.getInstance().signTran(parm, connection);
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();

		Map<String, Object> rCommitData = new HashMap<String, Object>();

		Map<String, Object> rReturnData = new HashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "SignTran");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		rResult.put("ACCOUNT", "");
 
		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);
  
		TParm returnParm = new TParm();
		returnParm.setData(returnMap);
		return returnParm;

	} 

	/**
	 * ���浱�ջ�ԤԼ�Һ���Ϣ�ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm SaveRegister(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");
		// ������д������
		TParm parm = new TParm();
		parm.setData("HIS_CODE",
				CommitData.get("HIS_CODE") == null ? new TNull(String.class)
						: ((String) CommitData.get("HIS_CODE")).trim());
		parm.setData("DEPT_CODE",
				CommitData.get("DEPT_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("DEPT_NO")).trim());
		parm.setData("ESTABLISH_TYPE",
				CommitData.get("ESTABLISH_TYPE") == null ? new TNull(
						String.class) : ((String) CommitData
						.get("ESTABLISH_TYPE")).trim());
		parm.setData("SS_TYPE", CommitData.get("SS_TYPE") == null ? new TNull(
				String.class) : ((String) CommitData.get("SS_TYPE")).trim());
		parm.setData("DOCTOR_NAME",
				CommitData.get("DOCTOR_NAME") == null ? new TNull(String.class)
						: ((String) CommitData.get("DOCTOR_NAME")).trim());
		parm.setData("DR_CODE",
				CommitData.get("DOCTOR_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("DOCTOR_NO")).trim());
		parm.setData("PATIENT_ID",
				CommitData.get("PATIENT_ID") == null ? new TNull(String.class)
						: ((String) CommitData.get("PATIENT_ID")).trim());
		parm.setData("CARD_NO", CommitData.get("CARD_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("CARD_NO")).trim());
		parm.setData("PAT_NAME", CommitData.get("NAME") == null ? new TNull(
				String.class) : ((String) CommitData.get("NAME")).trim());
		parm.setData("IDNO", CommitData.get("ID_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("ID_NO")).trim());
		parm.setData("PERSON_NO",
				CommitData.get("PERSON_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("PERSON_NO")).trim());
		parm.setData("DATE", CommitData.get("DATE"));
		parm.setData("WEEK", CommitData.get("WEEK"));
		parm.setData("ESTABLISH_TIME", CommitData.get("ESTABLISH_TIME"));
		parm.setData("AMOUNT", CommitData.get("AMOUNT"));
		parm.setData("CLINIC_FEE", CommitData.get("CLINIC_FEE"));
		parm.setData("OTHER_FEE", CommitData.get("OTHER_FEE"));
		parm.setData("TOTAL_FEE", CommitData.get("TOTAL_FEE"));
		parm.setData("TRAN_TYPE", CommitData.get("TRAN_TYPE"));
		parm.setData("BUSINESS_NO",
				CommitData.get("STREAM_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("STREAM_NO")).trim());
		parm.setData("TRADE_CHANNEL", CommitData.get("TRADE_CHANNEL"));
		parm.setData("SSCARD_NO",
				CommitData.get("SSCARD_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("SSCARD_NO")).trim());
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_ID")).trim());
		parm.setData("OPT_USER",
				OperateInfo.get("OPT_NAME") == null ? new TNull(String.class)
						: ((String) OperateInfo.get("OPT_NAME")).trim());
		parm.setData("OPT_TERM", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		String optDate = (String) OperateInfo.get("OPT_DATE");
		Timestamp CreateDate = Timestamp.valueOf(optDate);
		parm.setData("OPT_DATE", CreateDate);

		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());

		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());
		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		if (CommitData.get("TRAN_TYPE").toString().equals("0")) {
			parm.setData("iscurrent", false); // ԤԼ�Һű�ʶ
			resultData = REGCcbTool.getInstance()
					.saveRegister(parm, connection);
		} else {
			parm.setData("iscurrent", true); // ���չҺű�ʶ
			resultData = REGCcbTool.getInstance().saveCurrnetDay(parm,
					connection);
		}
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();

		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));
		rReturnData.put("TRAN_NO", resultData.getValue("TRAN_NO"));
		rReturnData.put("SEQ", resultData.getValue("SEQ"));
		rReturnData
				.put("PER_PAY_AMOUNT", resultData.getValue("PER_PAY_AMOUNT"));
		rReturnData.put("S_ACCOUNT_AMOUNT", resultData
				.getValue("S_ACCOUNT_AMOUNT"));
		rReturnData.put("S_GOC_AMOUNT", resultData.getValue("S_GOC_AMOUNT"));

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "SaveRegister");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		Date account = SystemTool.getInstance().getDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		rResult.put("ACCOUNT", sf.format(account));

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}

	/**
	 * ԤԼ�Һ���Ϣ��ѯ�ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm BankQueryRegister(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");

		// ������д������
		// print(map);

		TParm parm = new TParm();

		parm.setData("HIS_CODE", CommitData.get("HIS_CODE"));
		parm.setData("CARD_NO", CommitData.get("CARD_NO"));
		parm.setData("PERSON_NO", CommitData.get("PERSON_NO"));
		parm.setData("PATIENT_ID", CommitData.get("PATIENT_ID"));
		parm.setData("START_DATE", CommitData.get("START_DATE"));
		parm.setData("END_DATE", CommitData.get("END_DATE"));
		parm.setData("QUERY_TYPE", CommitData.get("QUERY_TYPE"));
		// OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("OPT_DATE", OperateInfo.get("OPT_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));
		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		resultData = REGCcbTool.getInstance().bankQueryRegister(parm,
				connection);
		int count = resultData.getCount();
		if (count < 0) {
			count = 0;
		}
		String exeflg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(exeflg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();
		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID", 0));
		rReturnData.put("COUNT", count);

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "BankQueryRegister");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		Date account = SystemTool.getInstance().getDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		rResult.put("ACCOUNT", sf.format(account));

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);
		Map linkMap;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (count <= 0) { // �������󣬲�ѯ������¼����һ���ռ�¼
			linkMap = new LinkedHashMap();
			linkMap.put("TRAN_NO", "");
			linkMap.put("DEPT_NO", "");
			linkMap.put("DEPT_NAME", "");
			linkMap.put("DEPT_STATION", "");
			linkMap.put("ESTABLISH_TYPE", "");
			linkMap.put("DOCTOR_NAME", "");
			linkMap.put("DOCTOR_NO", "");
			linkMap.put("DOCTOR_RANK", "");
			linkMap.put("NAME", "");
			linkMap.put("DATE", "");
			linkMap.put("WEEK", "");
			linkMap.put("ESTABLISH_TIME", "");
			linkMap.put("SEQ", "");
			linkMap.put("AMOUNT", "");
			linkMap.put("CLINIC_FEE", "");
			linkMap.put("OTHER_FEE", "");
			linkMap.put("TOTAL_FEE", "");
			linkMap.put("PAY_DATE", "");
			linkMap.put("CANCEL_DATE", "");
			linkMap.put("TYPE", "");
			list.add(linkMap);
		}
		resultData.removeData("PATIENT_ID");
		resultData.removeData("COUNT");
		for (int i = 0; i < count; i++) {
			TParm tp = (TParm) resultData.getRow(i);
			Map m = tp.getData();
			Map dataMap = (Map) m.get("Data");

			linkMap = new LinkedHashMap();
			linkMap.put("TRAN_NO", dataMap.get("TRAN_NO"));
			linkMap.put("DEPT_NO", dataMap.get("DEPT_NO"));
			linkMap.put("DEPT_NAME", dataMap.get("DEPT_NAME"));
			linkMap.put("DEPT_STATION", dataMap.get("DEPT_STATION"));
			linkMap.put("ESTABLISH_TYPE", dataMap.get("ESTABLISH_TYPE"));
			linkMap.put("DOCTOR_NAME", dataMap.get("DOCTOR_NAME"));
			linkMap.put("DOCTOR_NO", dataMap.get("DOCTOR_NO"));
			linkMap.put("DOCTOR_RANK", dataMap.get("DOCTOR_RANK"));
			linkMap.put("NAME", dataMap.get("NAME"));
			linkMap.put("DATE", dataMap.get("ADM_DATE"));
			linkMap.put("WEEK", dataMap.get("WEEK"));
			linkMap.put("ESTABLISH_TIME", dataMap.get("ESTABLISH_TIME"));
			linkMap.put("SEQ", dataMap.get("QUE_NO"));
			linkMap.put("AMOUNT", dataMap.get("AMOUNT"));
			linkMap.put("CLINIC_FEE", dataMap.get("CLINIC_FEE"));
			linkMap.put("OTHER_FEE", dataMap.get("OTHER_FEE"));
			linkMap.put("TOTAL_FEE", dataMap.get("TOTAL_FEE"));
			linkMap.put("PAY_DATE", dataMap.get("OPT_DATE"));
			linkMap.put("CANCEL_DATE", dataMap.get("CANCEL_DATE"));
			String status = "";
			String admStatus = (String) dataMap.get("STATUS");
			String flg = (String) dataMap.get("ARRIVE_FLG");
			String dateFlg = (String) dataMap.get("DATE_FLG");
			if ("N".equals(flg) && "N".equals(dateFlg)) { // δ����δ����
				status = "0";
			}
			if ("N".equals(flg) && "Y".equals(dateFlg)) { // δ�����ѹ���
				status = "2";
			}
			if ("Y".equals(flg) && "1".equals(admStatus)) { // δ���ԤԼ�ѱ��������չҺţ�
				status = "1";
			}
			if (!"1".equals(admStatus)) { // �Ѿ��ԤԼ�ѱ��������չҺţ�
				status = "3";
			}
			linkMap.put("TYPE", status);
			list.add(linkMap);
		}

		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("return", returnMap);
		map5.put("list", list);
		return new TParm(map5);

	}

	/**
	 * ����ȡ���ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm BankCancelTran(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");
		Map<String, Object> Result = (HashMap) map.get("Result");
		// ������д������
		TParm parm = new TParm();
		parm.setData("HIS_CODE",
				CommitData.get("HIS_CODE") == null ? new TNull(String.class)
						: ((String) CommitData.get("HIS_CODE")).trim());
		parm.setData("TRAN_NO", CommitData.get("TRAN_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("TRAN_NO")).trim());
		parm.setData("TRAN_TYPE",
				CommitData.get("TRAN_TYPE") == null ? new TNull(String.class)
						: ((String) CommitData.get("TRAN_TYPE")).trim());
		// OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_ID")).trim());
		parm.setData("OPT_USER",
				OperateInfo.get("OPT_NAME") == null ? new TNull(String.class)
						: ((String) OperateInfo.get("OPT_NAME")).trim());
		parm.setData("OPT_TERM", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		String optDate = (String) OperateInfo.get("OPT_DATE");
		Timestamp CreateDate = Timestamp.valueOf(optDate);
		parm.setData("OPT_DATE", CreateDate);
		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());
		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());
		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		if (CommitData.get("TRAN_TYPE").toString().equals("1")) { // ԤԼ����ҽ������ȡ��
			resultData = REGCcbTool.getInstance().backIns(parm, connection);
		}
		if (CommitData.get("TRAN_TYPE").toString().equals("3")) { // �ԷѹҺ�ȡ��
			resultData = REGCcbTool.getInstance().bankCancelCurrent(parm,
					connection);
		}
		if (CommitData.get("TRAN_TYPE").toString().equals("4")) { // ���չҺ�ҽ������ȡ��
			resultData = REGCcbTool.getInstance().bankCancelCurrentIns(parm,
					connection);
		}
		if (CommitData.get("TRAN_TYPE").toString().equals("6")) { // ҽ���ɷ�ȡ��
			resultData = REGCcbTool.getInstance().bankCancelChargeIns(parm,
					connection);
		}
		if (CommitData.get("TRAN_TYPE").toString().equals("8")) { // ԤԼ�Һų���
			resultData = REGCcbTool.getInstance().bankReturnTran(parm,
					connection);
			resultData.setData("OPT_ID",OperateInfo.get("OPT_ID") == null ? new TNull(
					String.class) : ((String) OperateInfo.get("OPT_ID")).trim());
			resultData.setData("OPT_IP",OperateInfo.get("OPT_IP") == null ? new TNull(
					String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
			String flg = (String) resultData.getData("EXECUTE_FLAG");
			if ("0".equals(flg)) {
				TSocket socket = new TSocket("127.0.0.1", 8080, "web");
System.out.println("���������=========================="+resultData);				
				resultData = TIOM_AppServer.executeAction(socket,				
						"action.ccb.CBCClientAction", "DoReturn", resultData);
				Map<String, Object> m = (HashMap) resultData.getData();
				Map<String, Object> r = (HashMap) m.get("Result");
				Map<String, Object> returndate = (HashMap) m.get("ReturnData");
				Map<String, Object> operate = (HashMap) m.get("OperateInfo");
				flg = (String) r.get("EXECUTE_FLAG");
				String streamNo = (String) returndate.get("STREAM_NO");
				String guid = (String) operate.get("GUID");
				String date = (String) operate.get("OPT_DATE");
				Timestamp returnDate = Timestamp.valueOf(date);
				parm.setData("OPT_DATE", returnDate);
				parm.setData("BUSINESS_NO", streamNo);
				parm.setData("GUID", guid);
				if ("0".equals(flg)) {
					resultData = REGCcbTool.getInstance().cancelReg(parm,
							connection);
				}
			}

		}
		if (CommitData.get("TRAN_TYPE").toString().equals("5")) { // ԤԼ�Һų���
			resultData = REGCcbTool.getInstance().bankCancelCharge(parm,
					connection); // �Էѽɷ�ȡ��
		}
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();
		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "BankCancelTran");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		rResult.put("ACCOUNT", SystemTool.getInstance().getDate());

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}

	/**
	 * ����ȷ�Ͻӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm ConfirmTran(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");

		// ������д������
		TParm parm = new TParm();
		parm.setData("HIS_CODE",
				CommitData.get("HIS_CODE") == null ? new TNull(String.class)
						: ((String) CommitData.get("HIS_CODE")).trim());
		parm.setData("PATIENT_ID",
				CommitData.get("PATIENT_ID") == null ? new TNull(String.class)
						: ((String) CommitData.get("PATIENT_ID")).trim());
		parm.setData("CARD_NO", CommitData.get("CARD_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("CARD_NO")).trim());
		parm.setData("PERSON_NO",
				CommitData.get("PERSON_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("PERSON_NO")).trim());
		parm.setData("TRAN_NO", CommitData.get("TRAN_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("TRAN_NO")).trim());
		parm.setData("H_GUID", CommitData.get("H_GUID") == null ? new TNull(
				String.class) : ((String) CommitData.get("H_GUID")).trim());
		parm.setData("PAY_SEQ", CommitData.get("PAY_SEQ") == null ? new TNull(
				String.class) : ((String) CommitData.get("PAY_SEQ")).trim());
		parm.setData("STREAM_NO",
				CommitData.get("STREAM_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("STREAM_NO")).trim());
		parm.setData("AMOUNT", CommitData.get("AMOUNT") == null ? new TNull(
				String.class) : ((String) CommitData.get("AMOUNT")).trim());
		parm.setData("TRAN_TYPE",
				CommitData.get("TRAN_TYPE") == null ? new TNull(String.class)
						: ((String) CommitData.get("TRAN_TYPE")).trim());
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_ID")).trim());
		parm.setData("OPT_NAME",
				OperateInfo.get("OPT_NAME") == null ? new TNull(String.class)
						: ((String) OperateInfo.get("OPT_NAME")).trim());
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		String optDate = (String) OperateInfo.get("OPT_DATE");
		Timestamp CreateDate = Timestamp.valueOf(optDate);
		parm.setData("OPT_DATE", CreateDate);
		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());
		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());
		String tradeNOINS = SystemTool.getInstance().getNo("ALL", "EKT",
				"CCB_TRADE_NO", "CCB_TRADE_NO");
		parm.setData("tradeNOINS", tradeNOINS); // ҽ�����ױ�����
		String tradeNOCCB = SystemTool.getInstance().getNo("ALL", "EKT",
				"CCB_TRADE_NO", "CCB_TRADE_NO");
		parm.setData("tradeNOCCB", tradeNOCCB); // �Էѽ��ױ�����
		String tradeResetCCB = SystemTool.getInstance().getNo("ALL", "EKT",
				"CCB_TRADE_NO", "CCB_TRADE_NO");
		parm.setData("tradeResetCCB", tradeResetCCB); // �Էѽ��ױ�����

		String tranType = (String) CommitData.get("TRAN_TYPE");
		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		if ("1".equals(tranType)) {
			resultData = REGCcbTool.getInstance().confirmTranYy(parm);           //ԤԼ�ҺŽ���ȷ��
		} else if ("2".equals(tranType)) {
			resultData = REGCcbTool.getInstance().confirmBd(parm);               //�Էѱ�������ȷ��
		} else if ("3".equals(tranType)) {
			resultData = REGCcbTool.getInstance().confirmTran(parm, connection);  //ҽ����������ȷ��
		} else if ("4".equals(tranType)) {
			resultData = REGCcbTool.getInstance().confirmTranOpb(parm,            //�ɷѽ���ȷ��
					connection);
		} else if ("5".equals(tranType)) {
			parm.setData("RET_STREAM_NO",
					CommitData.get("RET_STREAM_NO") == null ? new TNull(
							String.class) : ((String) CommitData
							.get("RET_STREAM_NO")).trim());
			resultData = REGCcbTool.getInstance().confirmRegTran(parm,
					connection);
		}
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();

		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData
				.put("BANK_STREAM_NO", resultData.getValue("BANK_STREAM_NO"));
		rReturnData.put("BANK_RET_STREAM_NO", resultData
				.getValue("BANK_RET_STREAM_NO"));
		rReturnData.put("PAY_SEQ", resultData.getValue("PAY_SEQ"));
		rReturnData.put("TRADE_STATUS", resultData.getValue("TRADE_STATUS"));

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "ConfirmTran");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		Date account = SystemTool.getInstance().getDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		rResult.put("ACCOUNT", sf.format(account));

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}

	/**
	 * ԤԼ�����ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm ConfirmReg(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");
		// ������д������
		TParm parm = new TParm();
		parm.setData("PERSON_NO",
				CommitData.get("PERSON_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("PERSON_NO")).trim());
		parm.setData("CARD_NO", CommitData.get("CARD_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("CARD_NO")).trim());
		parm.setData("CASE_NO", CommitData.get("TRAN_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("TRAN_NO")).trim());
		parm.setData("TRAN_TYPE",
				CommitData.get("TRAN_TYPE") == null ? new TNull(String.class)
						: ((String) CommitData.get("TRAN_TYPE")).trim());
		parm.setData("SSCARD_NO",
				CommitData.get("SSCARD_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("SSCARD_NO")).trim());
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_ID")).trim());

		parm.setData("OPT_NAME",
				OperateInfo.get("OPT_NAME") == null ? new TNull(String.class)
						: ((String) OperateInfo.get("OPT_NAME")).trim());
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		String optDate = (String) OperateInfo.get("OPT_DATE");
		Timestamp CreateDate = Timestamp.valueOf(optDate);
		parm.setData("OPT_DATE", CreateDate);
		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());
		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());
		String tradeNOINS = SystemTool.getInstance().getNo("ALL", "EKT", // ҽ�������������ʽ�������
				"CCB_TRADE_NO", "CCB_TRADE_NO");
		parm.setData("tradeNOINS", tradeNOINS);
		String tradeNOCCB = SystemTool.getInstance().getNo("ALL", "EKT",
				"CCB_TRADE_NO", "CCB_TRADE_NO");
		parm.setData("tradeNOCCB", tradeNOCCB);
		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		resultData = REGCcbTool.getInstance().confirmReg(parm, connection);
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();

		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));
		rReturnData.put("SEQ", resultData.getValue("SEQ"));
		rReturnData
				.put("PER_PAY_AMOUNT", resultData.getValue("PER_PAY_AMOUNT"));
		rReturnData.put("S_ACCOUNT_AMOUNT", resultData
				.getValue("S_ACCOUNT_AMOUNT"));
		rReturnData.put("S_GOC_AMOUNT", resultData.getValue("S_GOC_AMOUNT"));

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "ConfirmReg");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		Date account = SystemTool.getInstance().getDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		rResult.put("ACCOUNT", sf.format(account));

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}

	/**
	 * ��ȡ�ɷ���Ϣ�ӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm GetCharge(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");

		// ������д������
		TParm parm = new TParm();
		// parm.setData(CommitData);
		parm.setData("PERSON_NO", CommitData.get("PERSON_NO"));
		parm.setData("CARD_NO", CommitData.get("CARD_NO"));
		parm.setData("TRAN_NO", CommitData.get("TRAN_NO"));
		parm.setData("CLINIC_ID", CommitData.get("CLINIC_ID"));
		parm.setData("QUERY_TYPE", CommitData.get("QUERY_TYPE"));
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());
		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());

		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		resultData = REGCcbTool.getInstance().getCharge(parm, connection);
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();
		int count = resultData.getCount();
		if (count < 0) {
			count = 0;
		}
		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID", 0));
		rReturnData.put("COUNT", count);

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "GetCharge");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		Date account = SystemTool.getInstance().getDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		rResult.put("ACCOUNT", sf.format(account));

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		Map linkMap;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		resultData.removeData("PATIENT_ID");
		resultData.removeData("COUNT");
		if (count <= 0) {
			linkMap = new LinkedHashMap();
			linkMap.put("TRAN_NO", "");
			linkMap.put("CLINIC_COST_ITEM_NO", "");
			linkMap.put("ITEM_CODE", "");
			linkMap.put("ITEM_NAME", "");
			linkMap.put("ITEM_SPEC", "");
			linkMap.put("AMOUNT", "");
			linkMap.put("PRICES", "");
			linkMap.put("UNITS", "");
			linkMap.put("ORDERED_BY", "");
			linkMap.put("ORDERED_DOCTOR", "");
			linkMap.put("COSTS", "");
			linkMap.put("CHARGES", "");
			linkMap.put("REQ_CLASS", "");
			linkMap.put("PAY_SEQ", "");
			linkMap.put("PERFORMED_BY", "");
			list.add(linkMap);
		}
		for (int i = 0; i < count; i++) {
			TParm tp = (TParm) resultData.getRow(i);
			Map m = tp.getData();
			Map dataMap = (Map) m.get("Data");
			linkMap = new LinkedHashMap();
			linkMap.put("TRAN_NO", dataMap.get("TRAN_NO"));
			linkMap.put("CLINIC_COST_ITEM_NO", dataMap
					.get("CLINIC_COST_ITEM_NO") == null ? new TNull(
					String.class) : ((String) dataMap
					.get("CLINIC_COST_ITEM_NO")).trim());
			linkMap.put("ITEM_CODE", dataMap.get("ITEM_CODE"));
			linkMap.put("ITEM_NAME", dataMap.get("ITEM_NAME"));
			linkMap.put("ITEM_SPEC", dataMap.get("ITEM_SPEC"));
			linkMap.put("AMOUNT", dataMap.get("AMOUNT"));
			linkMap.put("PRICES", dataMap.get("PRICES"));
			linkMap.put("UNITS", dataMap.get("UNITS"));
			linkMap.put("ORDERED_BY",
					dataMap.get("ORDERED_BY") == null ? new TNull(String.class)
							: ((String) dataMap.get("ORDERED_BY")).trim());
			linkMap.put("ORDERED_DOCTOR", dataMap.get("ORDERED_DOCTOR"));
			linkMap.put("COSTS", dataMap.get("COSTS"));
			linkMap.put("CHARGES", dataMap.get("CHARGES"));
			linkMap.put("REQ_CLASS", dataMap.get("REQ_CLASS"));
			linkMap.put("PAY_SEQ", dataMap.get("PAY_SEQ"));
			linkMap.put("PERFORMED_BY", dataMap.get("PERFORMED_BY"));
			list.add(linkMap);
		}
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("return", returnMap);
		map5.put("list", list);
		return new TParm(map5);

	}

	/**
	 * ����ɷ����ݽӿ�
	 * 
	 * @param p
	 * @return
	 */
	public TParm SaveCharge(TParm p) {
		Map<String, Object> map = (HashMap) p.getData();
		Map<String, Object> CommitData = (HashMap) map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap) map.get("OperateInfo");
		// ������д������
		TParm parm = new TParm();
		parm.setData("PAY_SEQ", CommitData.get("PAY_SEQ") == null ? new TNull(
				String.class) : ((String) CommitData.get("PAY_SEQ")).trim());
		parm.setData("PATIENT_ID",
				CommitData.get("PATIENT_ID") == null ? new TNull(String.class)
						: ((String) CommitData.get("PATIENT_ID")).trim());
		parm.setData("CARD_NO", CommitData.get("CARD_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("CARD_NO")).trim());
		parm.setData("PAY_AMT", CommitData.get("PAY_AMT") == null ? new TNull(
				String.class) : ((String) CommitData.get("PAY_AMT")).trim());
		parm.setData("CASE_NO", CommitData.get("TRAN_NO") == null ? new TNull(
				String.class) : ((String) CommitData.get("TRAN_NO")).trim());
		parm.setData("STREAM_NO",
				CommitData.get("STREAM_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("STREAM_NO")).trim());
		parm.setData("SSCARD_NO",
				CommitData.get("SSCARD_NO") == null ? new TNull(String.class)
						: ((String) CommitData.get("SSCARD_NO")).trim());
		// OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_ID")).trim());
		parm.setData("OPT_NAME",
				OperateInfo.get("OPT_NAME") == null ? new TNull(String.class)
						: ((String) OperateInfo.get("OPT_NAME")).trim());
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("OPT_IP")).trim());
		parm.setData("OPT_DATE", OperateInfo.get("HIS_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("GUID")).trim());
		parm.setData("TOKEN", OperateInfo.get("TOKEN") == null ? new TNull(
				String.class) : ((String) OperateInfo.get("TOKEN")).trim());

		// ����JAVAHISҵ���߼�
		TConnection connection = getConnection();
		TParm resultData = new TParm();
		resultData = REGCcbTool.getInstance().saveCharge(parm, connection);
		String flg = resultData.getValue("EXECUTE_FLAG");
		if ("1".equals(flg)) {
			connection.rollback();
		}
		connection.commit();
		connection.close();

		Map<String, Object> rCommitData = new LinkedHashMap<String, Object>();

		Map<String, Object> rReturnData = new LinkedHashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));
		rReturnData.put("TRAN_NO", resultData.getValue("TRAN_NO"));
		rReturnData.put("PAY_SEQ", resultData.getValue("PAY_SEQ"));
		rReturnData
				.put("PER_PAY_AMOUNT", resultData.getValue("PER_PAY_AMOUNT"));
		rReturnData.put("S_ACCOUNT_AMOUNT", resultData
				.getValue("S_ACCOUNT_AMOUNT"));
		rReturnData.put("S_GOC_AMOUNT", resultData.getValue("S_GOC_AMOUNT"));

		Map<String, Object> rOperateInfo = new LinkedHashMap<String, Object>();
		rOperateInfo.put("METHOD", "SaveCharge");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("OPT_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new LinkedHashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));
		rResult.put("ACCOUNT", "");

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm();
		returnParm.setData(returnMap);
		return returnParm;
	}

	/**
	 * ��ȡ�����ļ�
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm readFile(TParm tparm) {
		TParm result = REGCcbTool.getInstance().readFile(tparm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;

	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm getCcbRe(TParm tparm) {
		TParm result = REGCcbReTool.getInstance().getCcbRe(tparm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;

	}

}
