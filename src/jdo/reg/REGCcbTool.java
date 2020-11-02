package jdo.reg;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.device.CallNoTx;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.opb.OPB;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:���нӿ�tool
 * 
 * <p>
 * Description:���нӿ�tool
 * </p>
 * 
 * Copyright: Copyright (c) 2008 </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author fuwj attributable
 * @version 1.0
 */
public class REGCcbTool extends TJDOTool {

	public static REGCcbTool instanceObject;

	public static String ISSCUESS = "0"; // �ӿڵ��óɹ���ʾ

	public static String ISERROR = "1"; // �ӿڵ���ʧ�ܱ�ʾ

	public static String TRANTYPEYY = "5"; // ����ȷ��ԤԼ�Һ�

	public static String TRANTYPEDR = "1"; // ����ȷ�ϹҺ�

	public static String ISVIP = "Y"; // VIP��

	public static String YJZ = "3"; // ��ѯ�Ѿ���Һ�����

	public static String GQ = "2"; // ��ѯ�ѹ��ڹҺ�����

	public static String ZC = "0"; // ��ѯ�����ĹҺ�����

	public static String YYGH = "1"; // ԤԼ�ҺŽ���ȷ��

	public static String BD = "2"; // ҽ����������ȷ��

	public static String GRGH = "3"; // ���չҺŽ���ȷ��

	public static String INSPASSWORD = "111111"; // ҽ����Ĭ������

	public static String PRINTNO = "111111"; // ����ҽ��Ʊ�ݺţ�д��

	public static String CZ = "1"; // ��ֻ֧�ֳ�ְ

	/**
	 * �õ�ʵ��
	 * 
	 * @return REGCcbTool
	 */
	public static REGCcbTool getInstance() {
		if (instanceObject == null)
			instanceObject = new REGCcbTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public REGCcbTool() {
		setModuleName("reg\\REGCcbModule.x");
		onInit();
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
			return null;
		}
		return config;
	}

	/**
	 * �õ������ļ���ȡ·��
	 * 
	 * @return
	 */
	public String getPath() {
		String path = "";
		path = getProp().getString("", "CHECKACCOUNTFTP.PATH");
		if (path == null || path.trim().length() <= 0) {
			return null;
		}
		return path;
	}

	/***
	 * Ժ�ڲ�����Ϣ����
	 *
	 * @param parm
	 * @param tConnection 
	 * @return
	 */
	public TParm signTran(TParm parm, TConnection connection) {
		// ʵ�������ز�������returnParm
		TParm returnParm = new TParm();
		String ccbId = (String) parm.getData("IDNO");
		if(ccbId.length()==18) {
			ccbId = ccbId.substring(0, 6)+ccbId.substring(8,17);
		}				
		parm.setData("CCB_ID",ccbId);								
		TParm result = query("getPatInfoByidNo", parm);
		returnParm.setData("EXECUTE_FLAG", ISERROR);
		if (result.getErrCode() < 0) {
			err("ERR:signTran" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("PATIENT_ID", "");
			returnParm.setData("EXECUTE_MESSAGE", "������Ϣ��ѯʧ��");
			return returnParm;
		}
		// ��ȡ������
		String mrNo = (String) result.getData("MR_NO", 0);
		if (isNull(mrNo)) {
			parm.setData("MR_NO", mrNo);
			// ���²�����Ϣ
			result = this.updatePatInfo(parm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:updatePatInfo" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("PATIENT_ID", "");
				returnParm.setData("EXECUTE_MESSAGE", "���²�����Ϣʧ��");
				return returnParm;
			}
		} else {
			returnParm.setData("PATIENT_ID", "");
			returnParm.setData("EXECUTE_MESSAGE", "�����Ϣ��ƥ��,������̨�˹�����");
			return returnParm;
		}
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("EXECUTE_MESSAGE", "�����ɹ�");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		return returnParm;
	}

	/***
	 * ���²�����Ϣ
	 * 
	 * @param parm
	 * @param tConnection
	 * @return
	 */
	public TParm updatePatInfo(TParm parm, TConnection connection) {
		String name = (String) parm.getData("PAT_NAME"); // ����
		String idNo = (String) parm.getData("IDNO"); // ֤����
		String mrNo = (String) parm.getData("MR_NO"); // ������
		String sql = "UPDATE SYS_PATINFO set PAT_NAME='" + name + "'";
		if (parm.getData("CCB_PERSON_NO") != null) {
			String personNo = (String) parm.getData("CCB_PERSON_NO");
			sql = sql + ",CCB_PERSON_NO='" + personNo + "'";
		}
		if (parm.getData("TEL_HOME") != null) {
			String tel = (String) parm.getData("TEL_HOME");
			sql = sql + ",CELL_PHONE='" + tel + "'";
		}
		if (parm.getData("ADDRESS") != null) {
			String address = (String) parm.getData("ADDRESS");
			sql = sql + ",RESID_ADDRESS='" + address + "'";
		}
		sql = sql + " WHERE MR_NO='" + mrNo + "'";

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}

	/**
	 * ���浱�ջ�ԤԼ�Һ�����
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm saveRegister(TParm parm, TConnection connection) {

		// ���ص�parm����
		TParm returnParm = new TParm();
		// ת��VIP�Һ�starttime��ʽ
		String establish_time = (String) parm.getData("ESTABLISH_TIME");
		String start_time = this.getStartTime((String) establish_time);
		parm.setData("START_TIME", start_time);
		parm.setData("CCB_PERSON_NO", (String) parm.getData("PERSON_NO"));
		// ��ѯ�Ƿ���ڲ�����
		TParm result = query("getPatInfoByPersonNo", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err(result.getErrCode() + " " + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޴��˲�����Ϣ");
			return returnParm;
		}
		String mr_no = (String) result.getData("MR_NO", 0);
		String pat_name = (String) result.getData("PAT_NAME", 0);
		parm.setData("PAT_NAME", pat_name);
		parm.setData("MR_NO", mr_no);
		String session_code = getSessionCode(establish_time);
		parm.setData("SESSION_CODE", (String) session_code);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		// ��ѯ�Ƿ��и��Ű����Ϣ
		result = this.getRegAchDay(parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޴��Ű���Ϣ");
			return returnParm;
		}
		String case_no = SystemTool.getInstance().getNo("ALL", "REG",
				"CASE_NO", "CASE_NO");
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", "O");
		String region_code = (String) result.getData("REGION_CODE", 0);
		parm.setData("REGION_CODE", region_code);
		parm.setData("REG_DATE", SystemTool.getInstance().getDate());
		// ת��sessioncode��ʽ������:01 ����:02
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		parm.setData("CLINICAREA_CODE", (PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(clinicroomNo))).getValue(
				"CLINICAREA_CODE", 0));
		parm.setData("CLINICROOM_NO", clinicroomNo);
		String vip_flg = (String) result.getData("VIP_FLG", 0);
		parm.setData("VIP_FLG", vip_flg);
		String clinictype_code = (String) result.getData("CLINICTYPE_CODE", 0);
		parm.setData("CLINICTYPE_CODE", clinictype_code);
		String reg_adm_time = this.getStartTime((String) establish_time);
		parm.setData("REG_ADM_TIME", "");

		String realdept_code = (String) result.getData("REALDEPT_CODE", 0);
		parm.setData("REALDEPT_CODE", realdept_code);
		String realdr_code = (String) result.getData("REALDR_CODE", 0);
		parm.setData("REALDR_CODE", realdr_code);
		parm.setData("APPT_CODE", "Y");
		parm.setData("VISIT_CODE", "1");
		parm.setData("REGMETHOD_CODE", "B");
		parm.setData("CTZ1_CODE", "99");
		parm.setData("HEAT_FLG", "N");
		parm.setData("ADM_STATUS", "1");
		parm.setData("REPORT_STATUS", "1");
		parm.setData("WEIGHT", 0);
		parm.setData("HEIGHT", 0);
		parm.setData("ARRIVE_FLG", "N");
		// �α�����
		parm.setData("INS_PAT_TYPE","");
		if (parm.getData("SS_TYPE") != null
				&& parm.getData("SS_TYPE").toString().length() > 0) {
			String ssType = (String) parm.getData("SS_TYPE");
			if ("0".equals(ssType)) {
				parm.setData("INS_PAT_TYPE", "1");
			}
		}
		// ��ȷ��
		parm.setData("ADM_REGION", region_code);
		String admType = (String) parm.getData("ADM_TYPE");
		// �Һŷ�
		double reg_fee = BIL.getRegDetialFee(admType, (String) parm
				.getData("CLINICTYPE_CODE"), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// ����
		double clinic_fee = BIL.getRegDetialFee(admType, (String) parm
				.getData("CLINICTYPE_CODE"), "CLINIC_FEE", "99", "", "", "");
		parm.setData("CLINIC_FEE", clinic_fee);

		// �ж��Ƿ�VIP
		String isVip = (String) result.getData("VIP_FLG", 0);
		// VIP��
		if (ISVIP.equals(isVip)) {
			// ����vip��sessionCode
			result = this.getVipQueNo(parm);
			if (result.getErrCode() != 0 || result.getCount() <= 0) {
				err("ERR:getVipQueNo" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "VIP����Ѿ����ڻ�ռ��");
				return returnParm;
			}
			parm.setData("REG_ADM_TIME", result.getData("START_TIME", 0));
			Long vip_que_no = (Long) result.getData("QUE_NO", 0);
			parm.setData("QUE_NO", vip_que_no);
			// ����vip���
			result = this.updateVipqueno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.updateVipqueno " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����VIP���ʧ��");
				return returnParm;
			}
			// ��������ˮ���
			result = this.updatequeno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:updatequeno" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "�������ʧ��");
				return returnParm;
			}
		} else {
			// ��ͨ���
			Long que_no = (Long) this.getRegAchDay(parm).getData("QUE_NO", 0);
			if (que_no == null) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ȡ���ʧ��");
				return returnParm;
			}
			parm.setData("QUE_NO", que_no);
			result = this.updatequeno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "�������ʧ��");
				return returnParm;
			}
		}
		Date adm_date = StringTool.getDate((String) parm.getData("DATE"),
				"yyyy-MM-dd");
		parm.setData("ADM_DATE", adm_date);

		boolean iscurrent = (Boolean) parm.getData("iscurrent");
		TParm insParm = new TParm(); // ��ҽ�������ز���
		TParm insFeeParm = new TParm();
		// �ж��Ƿ��ǵ��չҺţ�����Ǽ����ߵ��չҺ�����
		if (iscurrent) {
			// �ж�ҽ�������Ƿ�Ϊ�գ��粻Ϊ������ҽ��
			if (parm.getData("SSCARD_NO") != null
					&& !"".equals((String) parm.getData("SSCARD_NO"))) {
				TParm regionParm = null;// ���ҽ���������
				regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
				parm.setData("STATE", "9"); // 9��ҽ�����۷�
				parm.setData("CARD_TYPE", 2); // 9������
				parm.setData("TEXT", ";"+(String) parm.getData("SSCARD_NO")+"=");
				parm.setData("NHI_REGION_CODE", regionParm
						.getValue("NHI_NO", 0));
				parm.setData("NHI_NO", (String) parm.getData("SSCARD_NO"));		
				// ��ȡҽ������Ϣ
				insParm = this.readCard(parm);
				// ҽ��ȷ������
				if (null == insParm || insParm.getErrCode() < 0
						|| null == insParm.getValue("CARD_NO")
						|| insParm.getValue("CARD_NO").length() <= 0) {
					err("ERR:readCard" + result.getErrCode()
							+ result.getErrText() + result.getErrName());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "ҽ������ȡʧ��");
					return returnParm;
				}
				String crowdType = (String) insParm.getData("CROWD_TYPE");
				if (!CZ.equals(crowdType)) {
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "ִ��ҽ��ʧ�ܣ���ֻ֧�ֳ�ְ");
					return returnParm;
				}

				String confirmNo = (String) insParm.getData("CONFIRM_NO");
				parm.setData("CONFIRM_NO", confirmNo); // ����ҽ��ȷ�Ϻ�
				parm.setData("CTZ1_CODE", insParm.getData("CTZ_CODE")); // �������
				// �ָ���ǰ����
				insFeeParm = this.saveCardBefore(parm, insParm);
				if (result.getErrCode() != 0) {
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "ҽ������ȡʧ��");
					return returnParm;
				}
				// �ָ���
				result = this.insExeFee(true, insFeeParm, parm);
				if (result == null || result.getErrCode() < 0) {
					err("ERR:insExeFee" + result.getErrCode()
							+ result.getErrText() + result.getErrName());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "ҽ���ָ�ʧ��");
					return returnParm;
				}
				returnParm.setData("PER_PAY_AMOUNT", result // ҽ�����
						.getDouble("UACCOUNT_AMT"));
				returnParm.setData("S_ACCOUNT_AMOUNT", result // �Էѽ��
						.getDouble("ACCOUNT_AMT"));
				returnParm.setData("S_GOC_AMOUNT", 0.0);
			} else {
				// ���ղ���ҽ��
				parm.setData("CONFIRM_NO", ""); // ����ҽ����ҽ��ȷ�Ϻ�Ϊ��
				parm.setData("NHI_NO", "");
				returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);// ����ҽ����ҽ������Ϊ��
				returnParm.setData("S_ACCOUNT_AMOUNT", 0.0);
				returnParm.setData("S_GOC_AMOUNT", 0.0);
			}
			parm.setData("APPT_CODE", "N");
			result = this.insertRegPatAdm(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertRegPatAdm " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӹҺ�����ʧ��");
				return returnParm;
			}
		} else {
			parm.setData("CONFIRM_NO", ""); // ԤԼ����ҽ����ҽ��ȷ�Ϻ�Ϊ��
			parm.setData("NHI_NO", "");
			result = this.insertRegPatAdm(parm, connection); // ����Ʊ�ݱ�
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertRegPatAdm " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӹҺ�����ʧ��");
				return returnParm;
			}
			// ԤԼ�Һ���ӽ��ױ��漰ҽ��
			parm.setData("BUSINESS_TYPE", "REG");
			parm.setData("AMT", reg_fee + clinic_fee);
			parm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
					"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
			parm.setData("STATE", "1");
			parm.setData("RECEIPT_NO", "");
			result = this.insertCcbTrade(parm);
			if (result.getErrCode() < 0) {
				err("ERR:insertCcbTrade" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
				return returnParm;
			}
			// ԤԼ���ֲ��漰ҽ��
			returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0.0);
			returnParm.setData("S_GOC_AMOUNT", 0.0);
		}
		returnParm = this.setReturnParm(parm, returnParm);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����Һ����ݳɹ�");
		return returnParm;
	}

	/***
	 * ��ѯ�Ű���Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getRegAchDay(TParm parm) {
		TParm result = query("getRegAchDay", parm);
		if (result.getErrCode() < 0) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ��ȡVip��queno
	 * 
	 * @param parm
	 * @param tConnection
	 * @return
	 */
	public TParm getVipQueNo(TParm parm) {
		TParm result = query("getVipQueNo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:getVipQueNo" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ���¾����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatequeno(TParm parm, TConnection connection) {
		TParm result = update("updatequeno", parm, connection);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ����VIP�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateVipqueno(TParm parm, TConnection connction) {
		TParm result = update("updateVipqueno", parm, connction);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ���¹Һ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertRegPatAdm(TParm parm, TConnection connection) {

		TParm result = this.update("insertInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ת��sessioncode��ʽVIP��
	 */
	public String getSessionCodeVip(String code) {
		String session_code = "";
		int flagNum = code.indexOf(" ");
		String subString = code.substring(0, flagNum);
		if ("����".equals(subString)) {
			session_code = "01";
		} else if ("����".equals(subString)) {
			session_code = "02";
		}
		return session_code;
	}

	/**
	 * ת��sessioncode��ʽ��ͨ��
	 */
	public String getSessionCode(String code) {
		String session_code = "";
		if (code.contains("����")) {
			session_code = "01";
		} else if (code.contains("����")) {
			session_code = "02";
		}
		return session_code;
	}

	/**
	 * ת��starttime��ʽ
	 * 
	 * @param code
	 * @return
	 */
	public String getStartTime(String code) {
		int flagNum = code.indexOf(" ");
		String subString = code.substring(flagNum + 1, code.length());
		return subString.replace(":", "");
	}

	/***
	 * ��ӽ��н��׷���
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertCcbTrade(TParm parm) {
		parm.setData("BANK_FLG", "Y");
		TParm result = this.update("insertCcbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * ����ɷ���ӽ��ױ�
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertOpbCcbTrade(TParm parm) {
		TParm result = this.update("insertCcbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ����ԤԼ�Һŷ���ֵ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm setReturnParm(TParm parm, TParm returnParm) {
		returnParm.setData("PATIENT_ID", parm.getData("MR_NO"));
		returnParm.setData("TRAN_NO", parm.getData("CASE_NO"));
		returnParm.setData("SEQ", parm.getData("QUE_NO"));
		return returnParm;
	}

	/***
	 * ���浱�չҺ���Ϣ
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm saveCurrnetDay(TParm parm, TConnection connection) {
		TParm reslut = this.saveRegister(parm, connection);
		return reslut;
	}

	/***
	 * ��������վ�
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertBilRegRecp(TParm parm, TConnection connection) {
		// ��invrcp���receipt_no����
		parm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		parm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		parm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		parm.setData("PAY_CASH", 0);
		TParm result = this.update("insertBilRegRecp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ��������վ�
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertBilOpbRecp(TParm parm, TConnection connection) {
		// ��invrcp���receipt_no����
		parm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		parm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		parm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		parm.setData("PAY_CASH", 0);
		TParm result = this.update("insertBilOpbRecp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ���Ʊ������
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertInvrcp(TParm parm, TConnection connection) {
		parm.setData("RECP_TYPE", "REG");
		parm.setData("INV_NO", parm.getData("PRINT_NO"));
		parm.setData("CANCEL_FLG", "0");
		TParm result = this.update("insertInvrcp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * �������Ʊ��
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertOpbInvrcp(TParm parm, TConnection connection) {
		parm.setData("RECP_TYPE", "OPB");
		parm.setData("STATUS", "0");
		parm.setData("INV_NO", parm.getData("PRINT_NO"));
		parm.setData("CANCEL_FLG", "0");
		TParm result = this.update("insertInvrcp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * ��ѯԤԼ�Һ���Ϣ
	 * 
	 * @param parmu
	 * @param connection
	 * @return
	 */
	public TParm bankQueryRegister(TParm parm, TConnection connection) {
		TParm result = this.getPatInfoByPersonNo(parm, connection);
		return result;

	}

	/***
	 * ����person_no��ѯ������Ϣ
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getPatInfoByPersonNo(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String ccbPersonNo = (String) parm.getData("PERSON_NO");
		String startDate = (String) parm.getData("START_DATE");
		String endDate = (String) parm.getData("END_DATE");
		String cardNo = (String) parm.getData("CARD_NO");
		String mrNo = (String) parm.getData("PATIENT_ID");
		String type = (String) parm.getData("QUERY_TYPE");
		String sql = "SELECT  "
				+ "B.CASE_NO AS TRAN_NO,"
				+ "D.DEPT_CODE AS DEPT_NO,"
				+ "D.DEPT_CHN_DESC AS DEPT_NAME,"
				+ "B.CLINICROOM_NO AS DEPT_STATION,"
				+ "(CASE WHEN B.CLINICTYPE_CODE='03' THEN '0' WHEN B.CLINICTYPE_CODE='01' THEN '1' ELSE '2' END) "
				+ "AS ESTABLISH_TYPE,"
				+ "E.USER_NAME AS DOCTOR_NAME,"
				+ "E.USER_ID AS DOCTOR_NO,"
				+ "(SELECT POS_CHN_DESC FROM SYS_POSITION P WHERE E.POS_CODE=P.POS_CODE AND E.USER_ID=B.DR_CODE) "
				+ "AS DOCTOR_RANK,"
				+ "A.PAT_NAME AS NAME,"
				+ "TO_CHAR(B.ADM_DATE,'yyyy-MM-dd') AS ADM_DATE,"
				+ "'' AS WEEK,"
				+ "B.REG_ADM_TIME AS ESTABLISH_TIME,"
				+ "B.QUE_NO,"
				+ "B.CLINICTYPE_CODE,A.MR_NO AS PATIENT_ID,1 AS COUNT,"
				+ "TO_CHAR(B.REGCAN_DATE,'yyyy-MM-dd') AS CANCEL_DATE,TO_CHAR(B.OPT_DATE,'yyyy-MM-dd') "
				+ "AS OPT_DATE,B.ARRIVE_FLG AS ARRIVE_FLG,"
				+ "(CASE WHEN B.ADM_DATE<TO_DATE(TO_CHAR(sysdate,'YYYY-MM-DD'),'YYYY-MM-DD') THEN 'Y' ELSE 'N' END) AS DATE_FLG,"
				+ "B.ADM_STATUS AS STATUS,A.CCB_PERSON_NO FROM SYS_PATINFO A,REG_PATADM B,SYS_DEPT D,SYS_OPERATOR E "
				+ "WHERE B.REGCAN_USER IS NULL AND B.REGCAN_DATE IS NULL AND"
				+ " D.DEPT_CODE=B.DEPT_CODE AND B.DR_CODE = E.USER_ID AND A.MR_NO=B.MR_NO";
		if (ccbPersonNo != null && !"".equals(ccbPersonNo)) {
			sql = sql + " AND A.CCB_PERSON_NO='" + ccbPersonNo + "'";
		}
		if (mrNo != null && !"".equals(mrNo)) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
		if (startDate != null && endDate != null && !"".equals(startDate)
				&& !"".equals(endDate)) {
			sql = sql + " AND B.ADM_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYY-MM-DD') " + "AND TO_DATE('" + endDate
					+ "','YYYY-MM-DD') ";
		}
		// ����
		if (ZC.equals(type)) {
			sql = sql
					+ " AND B.ADM_DATE>=to_date(to_char(sysdate,'YYYY-MM-DD'),'YYYY-MM-DD') AND B.ARRIVE_FLG='N'";
		}
		// δ����Ա���,���չҺţ�
		if ("1".equals(type)) {
			sql = sql + " AND B.ARRIVE_FLG='Y'";
		}
		TParm returnResult = new TParm(TJDODBTool.getInstance().select(sql));
		if (returnResult.getErrCode() < 0 || returnResult.getCount() <= 0) {
			err("ERR:" + returnResult.getErrCode() + returnResult.getErrText()
					+ returnResult.getErrName());
			returnResult.setData("EXECUTE_FLAG", ISERROR);
			returnResult.setData("EXECUTE_MESSAGE", "û�в�ѯ����ؼ�¼");
			return returnResult;
		}
		List list = (List) returnResult.getData("CLINICTYPE_CODE");
		for (int i = 0; i < list.size(); i++) {
			String clinicTypeCode = (String) list.get(i);
			double reg_fee = BIL.getRegDetialFee("O", clinicTypeCode,
					"REG_FEE", "99", "", "", "");
			double clinic_fee = BIL.getRegDetialFee("O", clinicTypeCode,
					"CLINIC_FEE", "99", "", "", "");
			double totalFee = reg_fee + clinic_fee;
			returnResult.addData("AMOUNT", reg_fee);
			returnResult.addData("CLINIC_FEE", clinic_fee);
			returnResult.addData("OTHER_FEE", 0);
			returnResult.addData("TOTAL_FEE", reg_fee + clinic_fee);
		}
		returnResult.setData("EXECUTE_FLAG", ISSCUESS);
		return returnResult;
	}

	/**
	 * ��ȡ������Ϣ��¼
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getCcbTrade(TParm parm, TConnection connection) {
		TParm result = this.query("getCcbTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/***
	 * �˹Ҹ��¹Һŵ�����
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm cancelPatadm(TParm parm, TConnection connection) {

		TParm result = this.update("cancelPatadm", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * �ж��Ƿ�Ϊ��
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNull(String str) {
		if (!"".equals(str) && str != null) {
			return true;
		}
		return false;
	}

	/***
	 * �ж��Ա�
	 * 
	 * @param sex
	 * @return
	 */
	public String isSex(String sex) {
		if (sex == null) {
			return "";
		}
		if ("M".equals(sex)) {
			return "1";
		} else {
			return "2";
		}
	}

	/**
	 * ҽ����������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm readCard(TParm parm) {
		// ��ҽ��
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		TParm readParm = new TParm();
		TParm skParm = new TParm();
		skParm.setData("MR_NO", parm.getData("MR_NO"));
		// �����������ͣ�1��������2���Һţ�3���շѣ�4��סԺ,5 :���صǼǣ�
		skParm.setData("CARD_TYPE", parm.getData("CARD_TYPE"));
		skParm = SYSRegionTool.getInstance().selectdata("H01");
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		skParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		skParm.setData("TEXT", parm.getData("TEXT"));
		skParm.setData("OPT_USER", parm.getData("OPT_USER"));
		skParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		skParm.setData("PASSWORD", INSPASSWORD); // ����Ĭ��

		readParm = new TParm(INSTJReg.getInstance().readINSCard(
				skParm.getData()));// ��������
		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			return null;
		}
		readParm.setData("RETURN_TYPE", 1);// ����ִ��״̬
		readParm.setData("PASSWORD", INSPASSWORD);
		int insType = 0;
		if (parm.getData("SS_TYPE") == null) {
			return null;
		}
		String ssType = (String) parm.getData("SS_TYPE");
		TParm testParm = new TParm();
		if ("0".equals(ssType)) {
			insType = 1;
			testParm = INSTJFlow.getInstance().insIdentificationChZPt(readParm);
			String ctzCode = testParm.getValue("CTZ_CODE");
			TParm ctzParm = sysCtzParm(1, ctzCode);
			if (ctzParm.getErrCode() < 0) {
				return null;
			}
			readParm.setData("CTZ_CODE", ctzParm.getValue("CTZ_CODE", 0));// ��Ա���
			if (testParm.getErrCode() < 0) {
				readParm.setData("RETURN_TYPE", 0);// ����ִ��״̬
				return null;
			}
		}
		if (null == testParm || null == testParm.getValue("CONFIRM_NO")) {
			return null;
		}
		// testParm.setData("CTZ1_CODE", testParm.getValue("CTZ_CODE"));
		testParm.setData("BED_FEE", regionParm.getValue("TOP_BEDFEE", 0));// ��λ��
		testParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������
		readParm.setData("opbReadCardParm", testParm.getData());// �ʸ�ȷ�������
		readParm.setData("CONFIRM_NO", testParm.getValue("CONFIRM_NO"));// ���ؾ�ҽ˳���
		readParm.setData("INS_TYPE", insType);
		readParm.setData("RETURN_TYPE", 1);// �������� 1.�ɹ� 2.ʧ��
		readParm.setData("INS_PAT_TYPE", 1);// 1.��ͨ
		readParm.setData("DISEASE_CODE", "");// ���ز���
		// 2.����
		readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// ҽ���������

		return readParm;
	}

	private TParm sysCtzParm(int type, String ctzCode) {
		String ctzSql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE NHI_NO='" + ctzCode
				+ "' AND NHI_CTZ_FLG='Y'";
		if (type == 1) {
			ctzSql += " AND CTZ_CODE IN('11','12','13')";
		}
		if (type == 2) {
			ctzSql += " AND CTZ_CODE IN('21','22','23')";
		}
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm.getErrCode() < 0) {
			return ctzParm;
		}
		return ctzParm;
	}

	/***
	 * ִ��ҽ��ǰ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveCardBefore(TParm parm, TParm returnParm) {
		TParm tparm = new TParm();
		tparm = PatAdmTool.getInstance().selEKTByMrNo(parm);

		if (tparm.getErrCode() < 0) {
			return null;
		}

		if (tparm.getDouble("GREEN_BALANCE", 0) > 0) {
			return null;
		}
		String clinictypeCode = (String) parm.getData("CLINICTYPE_CODE");
		String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
				+ "B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
				+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
				+ "'0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
				+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='O'"
				+ " AND A.CLINICTYPE_CODE='" + clinictypeCode + "'";
		// �Һŷ�
		double reg_fee = parm.getDouble("REG_FEE");
		// ���� �����ۿ�
		double clinic_fee = parm.getDouble("CLINIC_FEE");
		TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(regFeesql));
		if (regFeeParm.getErrCode() < 0) {
			err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
			return null;
		}
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
				regFeeParm.setData("AR_AMT", i, reg_fee);
			}
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
				regFeeParm.setData("AR_AMT", i, clinic_fee);
			}
		}
		returnParm.setData("REG_PARM", regFeeParm.getData()); // ҽ����Ϣ
		returnParm.setData("DEPT_CODE", parm.getData("DEPT_CODE")); // ���Ҵ���
		returnParm.setData("MR_NO", parm.getData("MR_NO")); // ������

		returnParm.setData("RECP_TYPE", "REG"); // ���ͣ�REG / OPB
		returnParm.setData("CASE_NO", parm.getData("CASE_NO"));
		returnParm.setData("REG_TYPE", "1"); // �Һű�־:1 �Һ�0 �ǹҺ�
		returnParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		if (parm.getData("OPT_NAME") == null) {
			returnParm.setData("OPT_USER", parm.getData("OPT_USER"));
		}
		returnParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		if (parm.getData("OPT_IP") == null) {
			returnParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		}
		returnParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		returnParm.setData("DR_CODE", parm.getData("DR_CODE"));// ҽ������
		returnParm.setData("EREG_FLG", "0"); // ��ͨ
		returnParm.setData("PRINT_NO", PRINTNO); // Ʊ��
		returnParm.setData("QUE_NO", parm.getData("QUE_NO"));
		returnParm.setData("FeeY", reg_fee + clinic_fee);
		return returnParm;
	}

	/**
	 * ҽ����ִ�з�����ʾ���� flg �Ƿ�ִ���˹� false�� ִ���˹� true�� �����̲���
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(boolean flg, TParm insParm, TParm parm) {
		TParm insFeeParm = new TParm();
		if (flg) {
			insFeeParm.setData("insParm", insParm.getData()); // ҽ����Ϣ
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // ҽ����ҽ���
		} else {
			insFeeParm.setData("INS_TYPE", insParm.getData("INS_TYPE")); // �˹�ʹ��
			insFeeParm.setData("RECP_TYPE", "REG"); // �˹�ʹ��
		}
		insFeeParm.setData("CONFIRM_NO", insParm.getData("CONFIRM_NO"));
		insFeeParm.setData("CASE_NO", insParm.getData("CASE_NO"));
		insFeeParm.setData("NAME", parm.getData("PAT_NAME"));
		insFeeParm.setData("MR_NO", parm.getData("MR_NO")); // ������

		insFeeParm.setData("FeeY", insParm.getData("FeeY")); // Ӧ�ս��
		insFeeParm.setData("PAY_TYPE", false); // ֧����ʽ
		TParm regionParm = new TParm();// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // �������
		insFeeParm.setData("FEE_FLG", flg); // �жϴ˴β�����ִ���˷ѻ����շ� ��true �շ� false �˷�
		TParm returnParm = new TParm();
		if (flg) { // ������
			returnParm = this.saveCard(flg, insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")) {
				// || returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// �˷�����
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// ҽ�����
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// ��ְ
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - returnIns
							.getDouble("UNACCOUNT_PAY_AMT")), 2);
				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// �Ǿ�
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ARMY_AI_AMT");// �ֽ���
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);
				}
				returnParm.setData("RETURN_TYPE", 1); // ִ�гɹ�
				returnParm.setData("ACCOUNT_AMT", accountAmt);// ҽ�����
			}

		}
		return returnParm;
	}

	/***
	 * ҽ�������÷ָ�
	 * 
	 * @param flag
	 * @param parm
	 * @return
	 */
	public TParm saveCard(boolean flag, TParm inparm) {
		boolean exeError = false;
		boolean exeSplit = false;
		TParm insParm = inparm.getParm("insParm");
		if (flag) {
			if (null == insParm
					|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
				return null;
			}
		}
		insParm.setData("NEW_REGION_CODE", "H01");// �������
		insParm.setData("FeeY", inparm.getDouble("FeeY"));
		insParm.setData("PRINT_NO", PRINTNO); // Ʊ�ݺ�
		TParm result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(insParm);// ���÷ָ�
		exeError = true;// �����ۼ�
		// ����ҽ��
		if (result.getErrCode() < 0) {
			exeSplit = false;
			return null;
		} else {
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				exeSplit = false;
			} else {
				exeSplit = true;// ִ�з��÷ָ����
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// ���ý������
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(inparm);
		TParm accountParm = getAmt(inparm.getInt("INS_TYPE"), parm);
		return accountParm;
	}

	/**
	 * ��ȡ���
	 * 
	 * @param insType
	 * @param returnParm
	 * @return
	 */
	public TParm getAmt(int insType, TParm returnParm) {
		// ȡ��ҽ��ר�����֧�����
		double sOTOT_Amt = 0.00;
		// ȡ���ֽ�֧�����
		double sUnaccount_pay_amt = 0.00;

		if (insType == 1) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // �ܽ��
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // ���˻�֧��
					returnParm.getDouble("UNREIM_AMT", 0);// ����δ����
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);// �ֽ�֧�����
		}
		// ��ְ����
		if (insType == 2) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- returnParm.getDouble("UNREIM_AMT", 0);
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		// �Ǿ�����
		if (insType == 3) {
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						- returnParm.getDouble("UNREIM_AMT", 0);

			} else {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0);

			}

			// �ֽ�֧��
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)
					- returnParm.getDouble("FLG_AGENT_AMT", 0)
					- returnParm.getDouble("ARMY_AI_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);
		return parm;
	}

	/**
	 * ҽ���˹Ҳ���
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo, TParm tparm) {
		TParm reSetInsParm = new TParm();
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		if (null != tparm.getData("CONFIRM_NO")
				&& ((String) tparm.getData("CONFIRM_NO")).length() > 0) {
			tparm.setData("INV_NO", invNo);
			tparm.setData("RECP_TYPE", "REG");
			TParm parm = insExeFee(false, null, tparm);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // ȡ��
				return false;
			}
			reSetInsParm.setData("CASE_NO", (String) tparm.getData("CASE_NO")); // �����
			reSetInsParm.setData("CONFIRM_NO", (String) tparm
					.getData("CONFIRM_NO")); // ҽ�������
			reSetInsParm.setData("INS_TYPE", tparm.getData("INS_TYPE")); // ҽ�������
			reSetInsParm.setData("RECP_TYPE", "REG"); // �շ�����
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // �˷�����
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // ҽ���������
			reSetInsParm
					.setData("PAT_TYPE", (String) tparm.getData("REG_CTZ1")); // ���
			reSetInsParm.setData("INV_NO", invNo); // Ʊ�ݺ�
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ҽ��ִ���˷Ѳ���
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * ���ձ���
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmReg(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		parm.setData("CASE_NO", (String) parm.getData("CASE_NO"));
		TParm result = query("selectConfirmReg", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err(result.getErrName() + " " + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޴�ԤԼ��Ϣ");
			return returnParm;
		}
		parm.setData("OPT_USER", parm.getData("OPT_NAME"));
		Long seq = (Long) result.getData("QUE_NO", 0);
		String mrNo = (String) result.getData("MR_NO", 0);
		parm.setData("ADM_TYPE", "O");
		String admDate = result.getData("ADM_DATE", 0).toString();
		parm.setData("ADM_DATE", admDate.replace("-", ""));
		parm.setData("DR_CODE", result.getData("DR_CODE", 0));
		String caseNo = (String) parm.getData("CASE_NO");
		parm.setData("CLINICTYPE_CODE", result.getData("CLINICTYPE_CODE", 0));
		String date = SystemTool.getInstance().getDate().toString();
		date = date.substring(0, 10);
		if (!date.equals(admDate)) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�ǵ���,���ܱ���");
			return returnParm;
		}
		double reg_fee = BIL.getRegDetialFee("O", (String) parm
				.getData("CLINICTYPE_CODE"), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// ����
		double clinic_fee = BIL.getRegDetialFee("O", (String) parm
				.getData("CLINICTYPE_CODE"), "CLINIC_FEE", "99", "", "", "");
		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("AMT", reg_fee + clinic_fee);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("SESSION_CODE", result.getData("SESSION_CODE", 0));
		result = this.getRegAchDay(parm);
		if (result.getErrCode() != 0 || result == null) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "��ȡ�����Ϣʧ��");
			return returnParm;
		}
		// returnParm.setData("SEQ", result.getData("QUE_NO", 0));
		String region_code = (String) result.getData("REGION_CODE", 0);
		parm.setData("REGION_CODE", region_code);
		TParm insParm = new TParm();
		TParm insFeeParm = new TParm();
		// ��ҽ��
		if (parm.getData("SSCARD_NO") != null
				&& parm.getData("SSCARD_NO").toString().length() > 0) {
			parm.setData("TEXT", ";"+(String) parm.getData("SSCARD_NO")+"=");
			parm.setData("SS_TYPE", "0");
			insParm = this.readCard(parm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ִ��ҽ��ʧ�ܣ���ֻ֧�ֳ�ְ");
				return returnParm;
			}
			parm.setData("CTZ1_CODE", insParm.getData("CTZ_CODE"));
			// ҽ��ȷ������
			String confirmNo = (String) insParm.getData("CONFIRM_NO");
			parm.setData("CONFIRM_NO", confirmNo);
			parm.setData("NHI_NO", ";"+(String) parm.getData("SSCARD_NO")+"=");
			if (null == insParm || insParm.getErrCode() < 0
					|| null == insParm.getValue("CARD_NO")
					|| insParm.getValue("CARD_NO").length() <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ������ȡʧ��");
				return returnParm;
			}
			insFeeParm = this.saveCardBefore(parm, insParm);
			if (insFeeParm.getErrCode() != 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ������ȡʧ��");
				return returnParm;
			}
			// ҽ���ָ�
			result = this.insExeFee(true, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ���ָ�ʧ��");
				return returnParm;
			}
			// ����ҽ�����ָ���ҽ���ţ�ҽ��ȷ�Ϻ�
			String sql = "UPDATE REG_PATADM SET ";
			String nhiNo = (String) parm.getData("NHI_NO");
			if (parm.getData("SSCARD_NO") != null
					&& parm.getData("SSCARD_NO").toString().length() > 0) {
				sql = sql + "CONFIRM_NO='" + confirmNo + "',NHI_NO='" + nhiNo
						+ "'";
			}
			if (parm.getData("CTZ1_CODE") != null
					&& parm.getData("CTZ1_CODE").toString().length() > 0) {
				sql = sql + ",CTZ1_CODE='" + (String) parm.getData("CTZ1_CODE")
						+ "'";
			}
			sql = sql + " WHERE CASE_NO='" + caseNo + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql,
					connection));
			if (updateParm.getErrCode() < 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "���¹Һ�����ʧ��");
				return returnParm;
			}
			returnParm
					.setData("PER_PAY_AMOUNT", result.getData("UACCOUNT_AMT"));
			returnParm.setData("S_ACCOUNT_AMOUNT", result
					.getData("ACCOUNT_AMT"));
			returnParm.setData("S_GOC_AMOUNT", result.getData("S_GOC_AMOUNT"));
		} else {
			// ��ҽ���ԷѲ���
			TParm bilParm = new TParm();
			bilParm.setData("RECEIPT_NO", SystemTool.getInstance().getNo("ALL",
					"BIL", "CCB_RECEIPT_NO", "CCB_RECEIPT_NO"));
			bilParm.setData("CASE_NO", caseNo);
			bilParm.setData("ADM_TYPE", "O");
			bilParm.setData("REGION_CODE", region_code);
			bilParm.setData("MR_NO", mrNo);
			bilParm.setData("PRINT_NO", SystemTool.getInstance().getNo("ALL",
					"BIL", "CCB_INV_NO", "CCB_INV_NO"));
			bilParm.setData("REG_FEE", reg_fee);
			bilParm.setData("CLINIC_FEE", clinic_fee);
			bilParm.setData("REG_FEE_REAL", reg_fee);
			bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
			bilParm.setData("AR_AMT", reg_fee + clinic_fee);
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
			bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
			bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
			bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
			bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
			bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));
			// ԤԼ������ҽ��ֱ�Ӵ�Ʊ
			result = this.insertBilRegRecp(bilParm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertBilRegRecp " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "���Ʊ�ݵ�ʧ��");
				return returnParm;
			}
			result = this.insertInvrcp(bilParm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertInvrcp " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӹҺ�����ʧ��");
				return returnParm;
			}
			// ԤԼ������ҽ��ֱ���޸ı���״̬
			String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
					+ caseNo + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql,
					connection));
			if (updateParm.getErrCode() < 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "���¹Һ�����ʧ��");
				return returnParm;
			}
			callNo("REG", caseNo);
			returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0);
			returnParm.setData("S_GOC_AMOUNT", 0);
		}
		returnParm.setData("SEQ", seq);
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "�����ɹ�");
		return returnParm;
	}

	/**
	 * ԤԼ�ҺŽ���ȷ�� ���ͣ�1
	 * 
	 * @param parm
	 * @return
	 */
	public TParm confirmTranYy(TParm parm) {
		TParm returnParm = new TParm();
		if (parm.getData("TRAN_NO") == null
				|| parm.getData("TRAN_NO").toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
		}
		String caseNo = (String)parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		if (result.getData("CASE_NO", 0) == null
				|| result.getData("CASE_NO", 0).toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
		}
		sql = "SELECT CASE_NO FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "���׳ɹ�");
		return returnParm;
	}

	/**
	 * �Էѱ�������ȷ�� ���ͣ�2
	 * 
	 * @param parm
	 * @return
	 */
	public TParm confirmBd(TParm parm) {
		TParm returnParm = new TParm();
		if (parm.getData("TRAN_NO") == null
				|| parm.getData("TRAN_NO").toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
		}
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		if (result.getData("CASE_NO", 0) == null
				|| result.getData("CASE_NO", 0).toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
		}
		sql = "SELECT CASE_NO FROM REG_PATADM WHERE CASE_NO='" + caseNo
				+ "' AND ARRIVE_FLG='Y'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "���׳ɹ�");
		return returnParm;
	}

	/**
	 * ���չҺŽ���ȷ�� ���ͣ�3
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmTran(TParm parm, TConnection connection) {
		TParm insParm = null;
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String tranType = (String) parm.getData("TRAN_TYPE");
		String streamNo = (String) parm.getData("STREAM_NO");
		parm.setData("CASE_NO", caseNo);
		// ��ѯ�Һ�������Ϣ
		TParm result = query("selectTradeByCaseno", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		// �Һŷ�
		double reg_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// ����
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		double clinic_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "CLINIC_FEE", "99", "", "", "");
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("ADM_TYPE", "O");
		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String invNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		bilParm.setData("PRINT_NO", invNo);
		bilParm.setData("REG_FEE", reg_fee);
		bilParm.setData("CLINIC_FEE", clinic_fee);
		bilParm.setData("AR_AMT", reg_fee + clinic_fee);
		bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("REG_FEE_REAL", reg_fee);
		bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));

		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("BUSINESS_NO", (String) parm.getData("STREAM_NO"));
		parm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		parm.setData("BUSINESS_TYPE", "REG");
		parm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));
		parm.setData("BANK_FLG", "Y");

		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		// �ж��Ƿ���ҽ��
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm regionParm = null;// ���ҽ���������
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
			parm.setData("STATE", "9");
			parm.setData("CONFIRM_NO", confirmNo);
			String regionCode = (String) result.getData("REGION_CODE", 0);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "REG");
			// ��ȡҽ������
			result = query("getInsOpd", parm);
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0);
			parm.setData("AMT", -insPay);
			bilParm.setData("PAY_INS_CARD", insPay);
			bilParm.setData("OTHER_FEE2", personPay);
			TParm opbReadCardParm = new TParm();		
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + "" + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��");
				return returnParm;
			}
			String sql = "UPDATE INS_OPD SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR:getRegAchDay" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			parm.setData("RECEIPT_NO", receiptNo);
			parm.setData("TRADE_NO", parm.getData("tradeNOINS"));
			parm.setData("BUSINESS_TYPE", "REG");
			result = this.insertCcbTrade(parm);
			if (result.getErrCode() < 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
				return returnParm;
			}
		} else {
			// ����ҽ����ҽ��֧��Ϊ0,����֧�����з���
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
			parm.setData("RECEIPT_NO", receiptNo);
		}
		result = this.insertBilRegRecp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertBilRegRecp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "���Ʊ�ݵ�ʧ��");
			return returnParm;
		}
		result = this.insertInvrcp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertInvrcp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "���Ʊ������ʧ��");
			return returnParm;
		}
		// ��ӽ��ױ��漰ҽ��
		parm.setData("AMT", reg_fee + clinic_fee);
		parm.setData("TRADE_NO", parm.getData("tradeNOCCB"));
		parm.setData("STATE", "1");
		parm.setData("BUSINESS_TYPE", "REG");
		result = this.insertCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
			return returnParm;
		}
		// ����ȷ���޸ĹҺ�����Ϊ����״̬
		String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
				+ caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		callNo("REG", caseNo);
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "���׳ɹ�");
		return returnParm;
	}

	/**
	 * ��������ȷ�� ���ͣ�5
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmRegTran(TParm parm, TConnection connection) {
		TParm insParm = null;
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String tranType = (String) parm.getData("TRAN_TYPE");
		String streamNo = (String) parm.getData("STREAM_NO");
		String resetStreamNo = (String) parm.getData("RET_STREAM_NO");
		parm.setData("CASE_NO", caseNo);
		if ("".equals(streamNo) || "".equals(resetStreamNo)) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		// ��ѯ�Һ�������Ϣ
		TParm result = query("selectTradeByCaseno", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		// �Һŷ�
		double reg_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// ����
		double clinic_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "CLINIC_FEE", "99", "", "", "");
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("ADM_TYPE", "O");
		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String invNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		bilParm.setData("PRINT_NO", invNo);
		bilParm.setData("REG_FEE", reg_fee);
		bilParm.setData("CLINIC_FEE", clinic_fee);
		bilParm.setData("AR_AMT", reg_fee + clinic_fee);
		bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("REG_FEE_REAL", reg_fee);
		bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));

		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("BUSINESS_NO", streamNo);
		parm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		parm.setData("BUSINESS_TYPE", "REG");
		parm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		parm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));
		parm.setData("BANK_FLG", "Y");
		parm.setData("RECEIPT_NO", receiptNo);
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		// �ж��Ƿ���ҽ��
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm regionParm = null;// ���ҽ���������
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
			parm.setData("STATE", "9");
			parm.setData("CONFIRM_NO", confirmNo);
			String regionCode = (String) result.getData("REGION_CODE", 0);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "REG");
			// ��ȡҽ������
			result = query("getInsOpd", parm);
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0);
			parm.setData("AMT", -insPay);
			bilParm.setData("PAY_INS_CARD", insPay);
			bilParm.setData("OTHER_FEE2", personPay);
			TParm opbReadCardParm = new TParm();
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��");
				return returnParm;
			}
			String sql = "UPDATE INS_OPD SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			// ��ӽ��ױ���ҽ������
			parm.setData("TRADE_NO", parm.getData("tradeNOINS"));
			result = this.insertCcbTrade(parm);
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
				return returnParm;
			}
		} else {
			// ����ҽ����ҽ��֧��Ϊ0,����֧�����з���
			// bilParm.setData("PAY_INS_CARD", 0);
			// bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ʧ��,ȱ��ҽ����");
			return returnParm;
		}
		result = this.insertBilRegRecp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertBilRegRecp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "���Ʊ�ݵ�ʧ��");
			return returnParm;
		}
		result = this.insertInvrcp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertInvrcp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "��ӹҺ�����ʧ��");
			return returnParm;
		}
		// ����ȷ���޸ĹҺ�����Ϊ����״̬
		String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
				+ caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		parm.setData("AMT", (reg_fee + clinic_fee));
		parm.setData("BUSINESS_NO", streamNo);
		parm.setData("TRADE_NO", parm.getData("tradeNOCCB"));
		parm.setData("STATE", "1");
		parm.setData("BUSINESS_TYPE", "REG");
		result = this.insertCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
			return returnParm;
		}
		// ��ӽ��ױ��漰ҽ��
		parm.setData("AMT", -(reg_fee + clinic_fee));
		parm.setData("BUSINESS_NO", resetStreamNo);
		parm.setData("TRADE_NO", parm.getData("tradeResetCCB"));
		parm.setData("STATE", "2");
		parm.setData("BUSINESS_TYPE", "REGT");
		result = this.insertCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "��ӽ�����Ϣʧ��");
			return returnParm;
		}
		callNo("REG", caseNo);
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "���׳ɹ�");
		return returnParm;
	}

	/**
	 * ȡ��ɾ��ҽ������
	 * 
	 * @param insParm
	 */
	public TParm deleteINSOpd(TParm insParm) {
		TParm result = INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
		if (result.getErrCode() < 0) {
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * �ɷ���Ϣ��ѯ
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm getCharge(TParm tparm, TConnection connection) {
		String queryType = (String) tparm.getData("QUERY_TYPE");
		String caseNo = (String) tparm.getData("TRAN_NO");
		TParm returnParm = new TParm();
		// ��ѯ�Ƿ���ڵ��չҺ���Ϣ
		String sql = "SELECT CASE_NO,MR_NO,ADM_DATE "
				+ "FROM REG_PATADM "
				+ "WHERE ADM_DATE=TO_DATE(TO_CHAR(SYSDATE,'yyyy-mm-dd'),'yyyy-mm-dd')"
				+ " AND CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޵��չҺ���Ϣ");
			return returnParm;
		}
		// ��ѯ�ɷ���Ϣ
		String opdSql = "SELECT B.MR_NO AS PATIENT_ID,A.CASE_NO AS TRAN_NO,B.PRESRT_NO AS CLINIC_COST_ITEM_NO,"
				+ "B.ORDER_CODE AS ITEM_CODE,B.ORDER_DESC AS ITEM_NAME,B.SPECIFICATION AS ITEM_SPEC,"
				+ "B.DOSAGE_QTY AS AMOUNT,B.DISPENSE_UNIT AS UNITS,B.OWN_PRICE AS PRICES,B.EXEC_DEPT_CODE AS PERFORMED_BY,"
				+ "(SELECT C.DEPT_CHN_DESC FROM SYS_DEPT C WHERE C.DEPT_CODE=B.DEPT_CODE ) AS ORDERED_BY,"
				+ "(SELECT D.USER_NAME FROM SYS_OPERATOR D WHERE D.USER_ID =B.DR_CODE ) AS ORDERED_DOCTOR,"
				+ "B.RECEIPT_NO AS PAY_SEQ,"
				+ "B.AR_AMT AS COSTS,B.OWN_AMT AS CHARGES,B.ORDER_CAT1_CODE AS REQ_CLASS "
				+ "FROM REG_PATADM A,OPD_ORDER B "
				+ "WHERE A.CASE_NO='"
				+ caseNo
				+ "' "
				+ " AND TO_CHAR(B.ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd') "
				+ " AND A.CASE_NO=B.CASE_NO ";
		if ("2".equals(queryType)) { // δ�ɷ�
			opdSql = opdSql + " AND B.BILL_FLG='N'";
		} else if ("1".equals("queryType")) {
			opdSql = opdSql + " AND B.BILL_FLG='Y'"; // �ѽɷ�
		}
		result = new TParm(TJDODBTool.getInstance().select(opdSql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޽ɷ���Ϣ");
			return returnParm;
		}
		returnParm.setData(result.getData());
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "��ѯ�ɹ�");
		return returnParm;
	}

	// ����ɷ���Ϣ
	public TParm saveCharge(TParm tparm, TConnection connection) {

		TParm returnParm = new TParm();
		String caseNo = (String) tparm.getData("CASE_NO");
		String sql = "SELECT MR_NO,NHI_NO,REALDR_CODE FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo + "' AND ARRIVE_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޵��չҺ���Ϣ");
			return returnParm;
		}
		// String nhiNo = (String) result.getData("NHI_NO", 0);
		String nhiNo = (String) tparm.getData("SSCARD_NO");
		String mrNo = (String) result.getData("MR_NO", 0);
		String drCode = (String) result.getData("REALDR_CODE", 0);
		// ��ѯ�Ƿ���ڵ��վ�����Ϣ
		sql = "SELECT SUM(AR_AMT) AS AMTCOUNT FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL AND RELEASE_FLG='N' "
				+ " AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd') ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getDouble("AMTCOUNT", 0) <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޵��վ�����Ϣ");
			return returnParm;
		}
		Double amt = result.getDouble("AMTCOUNT", 0);
		// ��ҽ��
		TParm insParm = new TParm();
		if (!"".equals(nhiNo) && nhiNo.length() > 0) {
			nhiNo = ";"+(String) tparm.getData("SSCARD_NO")+"=";
			TParm skParm = new TParm();
			skParm.setData("MR_NO", mrNo);
			skParm.setData("CASE_NO", caseNo);
			skParm.setData("INS_TYPE", "1");
			skParm.setData("CARD_TYPE", 3);
			skParm.setData("SS_TYPE", "0");
			skParm.setData("TEXT", nhiNo);
			// ��ȡҽ����
			insParm = this.readCard(skParm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ִ��ҽ��ʧ�ܣ���ֻ֧�ֳ�ְ");
				return returnParm;
			}
			if (null == insParm || insParm.getErrCode() < 0
					|| null == insParm.getValue("CARD_NO")
					|| insParm.getValue("CARD_NO").length() <= 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ������ȡʧ��");
				return returnParm;
			}
			Pat pat = Pat.onQueryByMrNo(mrNo);
			Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
			// CCBOPB opb = CCBOPB.onQueryByCaseNo(reg);
			OPB opb = new OPB();
			// TParm orderParm1 = OrderTool.getInstance().getOrder(caseNo);
			TParm selectParm = this.getOrder(caseNo);
			if (selectParm.getErrCode() < 0 || selectParm.getCount() <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "�޵��չҺž�����Ϣ");
				return returnParm;
			}
			TParm orderParm = opb.getInsCcbParm(true, selectParm);
			if (orderParm.getCount("ORDER_CODE") <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "û��Ҫִ�е�����");
				return returnParm;
			}
			insParm.setData("ID_NO", pat.getIdNo());
			insParm.setData("NAME", pat.getName());
			insParm.setData("REG_PARM", orderParm.getData()); // ����Ҫ�ָ��ҽ��
			insParm.setData("MR_NO", pat.getMrNo()); // ������
			insParm.setData("PAY_KIND", "11"); // 4 ֧�����:11���ҩ��21סԺ//֧�����12
			insParm.setData("CASE_NO", reg.caseNo()); // �����
			insParm.setData("RECP_TYPE", "OPB"); // �������			
			insParm.setData("OPT_USER", (String) tparm.getData("OPT_NAME")); // �������
			// insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // ���Ҵ���
			insParm.setData("REG_TYPE", "0"); // �Һű�־:1 �Һ�0 �ǹҺ�
			insParm.setData("OPT_TERM", (String) tparm.getData("OPT_IP"));
			insParm.setData("OPBEKTFEE_FLG", "Y"); // ����ҽ�ƿ��շ�ע��----�ۿ��Ʊʱʹ����������
			// �վݱ�
			// BIL_OPB_RECP ҽ������޸�
			// insParm.setData("PRINT_NO", this.getValue("UPDATE_NO")); // Ʊ��
			// insParm.setData("DR_CODE", this.getValue("DR_CODE")); // ҽ������
			if (reg.getAdmType().equals("E")) {
				insParm.setData("EREG_FLG", "1"); // ����
			} else {
				insParm.setData("EREG_FLG", "0"); // ��ͨ
			}
			// ���÷ָ�����
			TParm insFeeParm = new TParm();
			insParm.setData("RECP_TYPE", "OPB"); // �շ�ʹ��
			insParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // ҽ�������
			// insFeeParm.setData("insParm", insParm.getData());
			insParm.setData("INS_TYPE", insParm.getValue("INS_TYPE"));
			insParm.setData("FeeY", orderParm.getDouble("sumAmt")); // δ�շ�ҽ�����
			insParm.setData("parmSum", orderParm.getParm("parmSum").getData());// ���е�ҽ��
			insParm.setData("billAmt", orderParm.getDouble("billAmt"));// ����ҽ�����
			// �����շ�δ�շ�
			insParm.setData("orderParm", orderParm.getData());// ��Ҫ�շѵ�ҽ��
			insParm.setData("ADM_TYPE", "O");
			insParm.setData("PRINT_NO", PRINTNO);// Ʊ����ʱд��
			insParm.setData("DR_CODE",drCode);              
			// �ָ���										
			result = this.insExeFee(true, insParm, tparm);
			if (null == result || result.getErrCode() != 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ���ָ�ʧ��");
				return returnParm;
			}
			returnParm.setData("PER_PAY_AMOUNT", StringTool.round(result
					.getDouble("UACCOUNT_AMT"),2));
			returnParm.setData("S_ACCOUNT_AMOUNT", StringTool.round(result
					.getDouble("ACCOUNT_AMT"),2)); 
			returnParm
					.setData("S_GOC_AMOUNT", StringTool.round(result.getDouble("S_GOC_AMOUNT"),2));
		} else {
			returnParm.setData("PER_PAY_AMOUNT", amt);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0);
			returnParm.setData("S_GOC_AMOUNT", 0); 
		}
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("TRAN_NO", caseNo);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ɹ�");
		return returnParm;
	}

	/**
	 * ����ɷѽ���ȷ�� ���ͣ�4
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm confirmTranOpb(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String cardNo = (String) parm.getData("CARD_NO");
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		// ��ѯ�Ƿ���ڵ��չҺ���Ϣ
		String sql = "SELECT A.CASE_NO,A.MR_NO,A.ADM_DATE,A.NHI_NO,A.CONFIRM_NO,A.REGION_CODE "
				+ "FROM REG_PATADM A " + "WHERE" + " CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�޵��չҺž�����Ϣ");
			return returnParm;
		}
		parm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		parm.setData("CASE_NO", (String) result.getData("CASE_NO", 0));
		parm.setData("BUSINESS_NO", parm.getData("STREAM_NO"));
		parm.setData("BUSINESS_TYPE", "OPB");
		parm.setData("BANK_FLG", "Y");
		parm.setData("OPT_USER", parm.getData("OPT_NAME"));
		parm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));

		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String mrNo = (String) result.getData("MR_NO", 0);
		sql = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		String parName = (String) result.getData("PAT_NAME", 0);
		parm.setData("PAT_NAME", parName);
		sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1' AND RECP_TYPE='OPB'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);

		// String receiptNo = (String) parm.getData("tradeNOCCB");
		// String receiptInsNO = (String) parm.getData("tradeNOINS");
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("ADM_TYPE", "O");
		String printNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		parm.setData("PRINT_NO", printNo);
		bilParm.setData("PRINT_NO", printNo);
		// �ж��Ƿ���ҽ��
		TParm insParm = new TParm();
		TParm selOpdParm = new TParm();
		TParm opdParm = new TParm();
		sql = "SELECT CASE_NO,REXP_CODE,AR_AMT,ORDER_CODE,ORDER_CAT1_CODE,TEMPORARY_FLG,"
				+ "ADM_TYPE,RX_NO,SEQ_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL AND RELEASE_FLG='N' "
				+ "AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd')";
		opdParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (opdParm.getErrCode() < 0 || opdParm.getCount() <= 0) {
			err("ERR:" + opdParm.getErrCode() + opdParm.getErrText()
					+ opdParm.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		for (int i = 0; i <opdParm.getCount("ORDER_CODE"); i++) {
			if (!opdParm.getValue("CAT1_TYPE", i).equals("RIS")
					&& !opdParm.getValue("CAT1_TYPE", i).equals("LIS")) {
				continue;
			}
			if(opdParm.getData("MED_APPLY_NO",i)!=null) {
				String medApplyNo = opdParm.getValue("MED_APPLY_NO",i);
				String rxNo = opdParm.getValue("RX_NO",i);
				int seqNo = opdParm.getInt("SEQ_NO",i);
				String cat1Type = opdParm.getValue("CAT1_TYPE",i);
				String updateSql ="UPDATE MED_APPLY SET BILL_FLG='Y' WHERE APPLICATION_NO='"+medApplyNo+"'" +
						" AND ORDER_NO='"+rxNo+"' AND SEQ_NO="+seqNo+" AND CAT1_TYPE='"+cat1Type+"'";
				result = new TParm(TJDODBTool.getInstance().update(updateSql, connection));
				if (result.getErrCode() < 0) {         
					err("ERR: " + result.getErrCode() + result.getErrText());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
					return returnParm;
				}  
		//		applyParm.setData("MED_APPLY_NO",opdParm.getValue("MED_APPLY_NO",0));
		//		applyParm.setData("RX_NO",opdParm.getValue("RX_NO",0));
		//		applyParm.setData("SEQ_NO",opdParm.getValue("SEQ_NO",0));
		//		applyParm.setData("CAT1_TYPE",opdParm.getValue("CAT1_TYPE",0));
			}
		}
		TParm hl7ParmEnd = new TParm();
		int hl7Count = opdParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			hl7ParmEnd.addData("ORDER_CAT1_CODE", opdParm.getData(
					"ORDER_CAT1_CODE", i));
			hl7ParmEnd.addData("TEMPORARY_FLG", opdParm.getData(
					"TEMPORARY_FLG", i));
			hl7ParmEnd.addData("ADM_TYPE", opdParm.getData("ADM_TYPE", i));
			hl7ParmEnd.addData("RX_NO", opdParm.getData("RX_NO", i));
			hl7ParmEnd.addData("SEQ_NO", opdParm.getData("SEQ_NO", i));
		}
		hl7ParmEnd.setData("PAT_NAME", parName);
		hl7ParmEnd.setData("CASE_NO", caseNo);

		double[] chargeDouble = new double[30];
		double[] sumAmt = chargeDouble(opdParm, chargeDouble, 2);
		int index = 1;
		// д������
		for (int i = 0; i < chargeDouble.length; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				bilParm.setData(chargeTemp + "0" + index, chargeDouble[i]);
			} else {
				bilParm.setData(chargeTemp + index, chargeDouble[i]);
			}
			index++;
		}
		if (!"".equals(confirmNo) && confirmNo != null) {
			TParm regionParm = null;// ���ҽ���������
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
			parm.setData("CONFIRM_NO", confirmNo);
			parm.setData("CASE_NO", caseNo);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "OPB");
			// ��ȡҽ������
			result = query("getInsOpd", parm);		
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��,����ϵ��Ϣ����ִ���Զ�����");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0); 
				bilParm.setData("PAY_INS_CARD", insPay);        
			bilParm.setData("PAY_OTHER2", personPay);
			bilParm.setData("AR_AMT", result.getDouble("INS_TOT_AMT", 0)); // ��ҽ�����
			bilParm.setData("TOT_AMT", result.getDouble("INS_TOT_AMT", 0));

			parm.setData("AMT", -insPay);
			TParm opbReadCardParm = new TParm();
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + "" + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ��֧��ʧ��");
				return returnParm;
			}
			// ����ҽ��Ʊ�ݺ�
			sql = "UPDATE INS_OPD SET INV_NO='" + printNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + printNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// ��ӽ��ױ���ҽ������
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
				return returnParm;
			}
			parm.setData("AMT", -insPay);
			parm.setData("TRADE_NO", (String) parm.getData("tradeNOINS"));
			parm.setData("STATE", "9");
			parm.setData("RECEIPT_NO", receiptNo);
			result = this.insertOpbCcbTrade(parm);
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "���뽻�ױ�ʧ��");
				return returnParm;
			}
		} else {
			parm.setData("TRADE_NO", (String) parm.getData("tradeNOCCB"));
			parm.setData("STATE", "1");
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("PAY_OTHER2", sumAmt[0]);
			bilParm.setData("AR_AMT", sumAmt[0]); // ��ҽ�����
			bilParm.setData("TOT_AMT", sumAmt[0]);
			parm.setData("AMT", sumAmt[0]);
			parm.setData("AR_AMT", sumAmt[0]);
		}
		bilParm.setData("REDUCE_AMT", 0);
		bilParm.setData("PAY_CASH", 0);
		bilParm.setData("PAY_MEDICAL_CARD", 0);
		bilParm.setData("PAY_BANK_CARD", 0);
		bilParm.setData("PAY_CHECK", 0);
		bilParm.setData("PAY_DEBIT", 0);
		bilParm.setData("PAY_BILPAY", 0);
		bilParm.setData("PAY_INS", 0);
		bilParm.setData("PAY_OTHER1", 0);
		bilParm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		bilParm.setData("OPT_TERM", (String) parm.getData("OPT_IP"));
		bilParm.setData("OPT_DATE", (Date) parm.getData("OPT_DATE"));
		bilParm.setData("CASHIER_CODE", (String) parm.getData("OPT_NAME"));
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));
		// ���Ʊ�ݵ�
		result = this.insertBilOpbRecp(bilParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����Ʊ�ݱ�ʧ��");
			return returnParm;
		}
		result = this.insertOpbInvrcp(bilParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����Ʊ�ݱ�ʧ��");
			return returnParm;
		}
		parm.setData("STATE", "1");
		parm.setData("AMT", sumAmt[0]);
		parm.setData("TRADE_NO", (String) parm.getData("tradeNOCCB"));
		parm.setData("RECEIPT_NO", receiptNo);
		result = this.insertOpbCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "���뽻�ױ�ʧ��");
			return returnParm;
		}
		String optUser = (String) parm.getData("OPT_NAME");
		sql = "UPDATE OPD_ORDER SET PRINT_FLG='Y', BILL_FLG='Y',RECEIPT_NO='"
				+ receiptNo
				+ "',BILL_DATE=SYSDATE,BILL_USER='"
				+ optUser
				+ "' WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd')";
		result = result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + "" + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȷ��ʧ��");
			return returnParm;
		}
		this.sendHL7Mes(hl7ParmEnd);
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "���׳ɹ�");
		return returnParm;
	}

	/**
	 * ��ý���������
	 * 
	 * @param opdParm
	 *            TParm
	 * @param chargeDouble
	 *            double[]
	 * @param type
	 *            int 1 .ҽ�ƿ����� 2.�ֽ����
	 * @return double
	 */
	private double[] chargeDouble(TParm opdParm, double[] chargeDouble, int type) {
		int opdCount = opdParm.getCount("CASE_NO");
		String rexpCode = "";
		double arAmt = 0.00;
		double[] sumAmt = new double[3]; // ����ܽ�� ҽ�ƿ���� ��ɫͨ�����
		double allArAmt = 0.00;
		String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
		TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		int chargeCount = sysChargeParm.getCount("ID");
		String[] chargeName = new String[30];
		int index = 1;
		for (int i = 0; i < 30; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				chargeName[i] = bilRecpParm
						.getData(chargeTemp + "0" + index, 0).toString();
			} else {
				chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0)
						.toString();
			}
			index++;
		}
		TParm p = new TParm();
		for (int i = 0; i < chargeCount; i++) {
			String sysChargeId = sysChargeParm.getData("ID", i).toString();
			for (int j = 0; j < chargeName.length; j++) {
				if (sysChargeId.equals(chargeName[j])) {
					p.setData("CHARGE", i, j);
					p.setData("ID", i, sysChargeParm.getData("ID", i));
					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
							i));
					break;
				}
			}
		}
		String idCharge = "";
		int charge = 0;
		double greenAmt = 0.00; // ��ɫͨ�����
		double ektAmt = 0.00;
		double tempAmt = 0.00;
		for (int i = 0; i < opdCount; i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			arAmt = opdParm.getDouble("AR_AMT", i);
			allArAmt = allArAmt + arAmt;
			for (int j = 0; j < p.getCount("ID"); j++) {
				idCharge = p.getData("ID", j).toString();
				charge = p.getInt("CHARGE", j);
				if (rexpCode.equals(idCharge))
					chargeDouble[charge] = chargeDouble[charge] + arAmt;
				// ========20120220 zhangp modify end
			}
		}
		sumAmt[0] = allArAmt;
		// sumAmt[1] = ektAmt; // ҽ�ƿ����
		// sumAmt[2] = greenAmt; // ��ɫͨ�����
		return sumAmt;
	}

	/**
	 * ��ӻس�Ʊ�ݽ�����Ϣ
	 * 
	 * @param caseNo
	 * @param connection
	 * @return
	 */
	public TParm backFlush(TParm parm, TConnection connection) {
		// ��Ӹ���Ʊ��
		TParm bilparm = new TParm();
		String caseNo = (String) parm.getData("caseNo");
		String cancelUser = (String) parm.getData("OPT_NAME");
		Date cancelDate = (Date) parm.getData("OPT_DATE");
		bilparm.setData("CASE_NO", caseNo);
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		bilparm.setData("RECEIPT_NO", receiptNo);
		bilparm.setData("ADM_TYPE", "O");
		String sql = "SELECT RECEIPT_NO,PRINT_NO,REGION_CODE,MR_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,"
				+ "REG_FEE,CLINIC_FEE,OTHER_FEE1,OTHER_FEE2,AR_AMT,PAY_CASH,PAY_INS_CARD"
				+ " FROM BIL_REG_RECP WHERE CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		bilparm.setData("REGION_CODE", result.getData("REGION_CODE", 0));
		bilparm.setData("MR_NO", result.getData("MR_NO", 0));
		bilparm.setData("PRINT_NO", result.getData("PRINT_NO", 0));
		// bilparm.setData("BILL_DATE",result.getData("BILL_DATE",0));
		// bilparm.setData("CHARGE_DATE",result.getData("CHARGE_DATE",0));
		// bilparm.setData("PRINT_DATE",result.getData("PRINT_DATE",0));
		bilparm.setData("REG_FEE", -(Double) (result.getData("REG_FEE", 0)));
		bilparm.setData("CLINIC_FEE", -(Double) (result
				.getData("CLINIC_FEE", 0)));
		bilparm.setData("OTHER_FEE1", result.getData("OTHER_FEE1", 0));
		bilparm.setData("OTHER_FEE2", result.getData("OTHER_FEE2", 0));
		bilparm.setData("AR_AMT", -(Double) (result.getData("AR_AMT", 0)));
		// bilparm.setData("PAY_CASH",result.getData("PAY_CASH",0));
		// bilparm.setData("PAY_INS_CARD",result.getData("PAY_INS_CARD",0));
		String oldRecepitNo = (String) result.getData("RECEIPT_NO", 0);
		// ����һ����������
		result = this.insertBilRegRecp(bilparm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// ����RESET_RECEIPT_NO
		sql = "UPDATE BIL_REG_RECP SET RESET_RECEIPT_NO='" + receiptNo
				+ "' WHERE CASE_NO='" + caseNo + "' " + "AND RECEIPT_NO='"
				+ oldRecepitNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		// ȡ��Ʊ��
		sql = "UPDATE BIL_INVRCP SET CANCEL_FLG='1',CANCEL_USER='" + cancelUser
				+ "',CANCEL_DATE=" + cancelDate + " WHERE RECEIPT_NO='"
				+ oldRecepitNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		sql = "SELECT CARD_NO,MR_NO,BUSINESS_NO,PAT_NAME,AMT,STATE,BUSINESS_TYPE,BANK_FLG "
				+ "FROM EKT_CCB_TRADE "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BUSINESS_TYPE='REG' AND STATE='1'";
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		// ���ױ��˿�
		TParm tradeParm = new TParm();
		tradeParm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
		tradeParm.setData("CARD_NO", result.getData("CARD_NO", 0));
		tradeParm.setData("MR_NO", result.getData("MR_NO", 0));
		tradeParm.setData("CASE_NO", caseNo);
		tradeParm.setData("BUSINESS_NO", result.getData("BUSINESS_NO", 0));
		tradeParm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		tradeParm.setData("AMT", -(Double) result.getData("AMT", 0));
		tradeParm.setData("STATE", result.getData("STATE", 0));
		// tradeParm.setData("BUSINESS_TYPE",result.getData("BUSINESS_TYPE",0));
		result = this.insertCcbTrade(tradeParm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		return result;
	}

	/**
	 * ����ȡ������ҽ����ȡ���ӿ�1
	 * 
	 * @param parm
	 * @return
	 */
	public TParm backIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String guid = (String) parm.getData("GUID");
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		}
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// ȡ�����ý������
		if (result.getErrCode() < 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		// �˹Ҳ���
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * ���չҺŽ���ȡ��
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCurrent(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String guid = (String) parm.getData("GUID");
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO,CLINICROOM_NO,SESSION_CODE,QUE_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE FROM REG_PATADM WHERE APPT_CODE='N' AND ARRIVE_FLG='N' AND CASE_NO='"
				+ caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));    
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
			+ admDate
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ clinicroomNo
			+ "' AND "
			+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "���չҺŽ���ȡ��ʧ��");
			return returnParm;
		}
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * ���չҺ�ҽ������ȡ�� ȡ������:4
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCurrentIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT MR_NO,NHI_NO,CONFIRM_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE,SESSION_CODE,CLINICROOM_NO,"
				+ "QUE_NO,APPT_CODE,"
				+ "ADM_TYPE,CLINICTYPE_CODE"
				+ " FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ " AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����ȡ��ʧ��");  
			return returnParm;
		}
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		//�ͷ�VIP��
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
			+ admDate
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ clinicroomNo
			+ "' AND "
			+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		}
		sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		}
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
			+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
			+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));        
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// ȡ�����ý������
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * ҽ���ɷ�ȡ��������6
	 * 
	 * @param parm
	 * @return
	 */
	public TParm bankCancelChargeIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		}
		TParm regionParm = null;// ���ҽ���������
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// ���ҽ���������
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// ȡ�����ý������
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// ɾ������
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * �Էѽɷѽ���ȡ��
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCharge(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * ԤԼ�Һų��� ���ͣ�8
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankReturnTran(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String guid = (String) parm.getData("O_GUID");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT CASE_NO,MR_NO FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL AND ARRIVE_FLG='N' "
				+ "AND APPT_CODE='Y' AND ADM_STATUS='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CASE_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		String mrNo = (String) result.getData("MR_NO", 0);
		caseNo = (String) result.getData("CASE_NO", 0);
		sql = "SELECT CCB_PERSON_NO FROM SYS_PATINFO WHERE MR_NO='" + mrNo
				+ "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		String personNo = (String) result.getData("CCB_PERSON_NO", 0);
		sql = "SELECT BUSINESS_NO,AMT,OPT_DATE,CARD_NO FROM EKT_CCB_TRADE "
				+ "WHERE CASE_NO='" + caseNo
				+ "' AND STATE='1' AND BUSINESS_TYPE='REG'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		String cardNo = (String) result.getData("CARD_NO", 0);
		String streamNo = (String) result.getData("BUSINESS_NO", 0);
		double amt = result.getDouble("AMT", 0);
		Date optDate = (Date) result.getData("OPT_DATE", 0);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		returnParm.setData("OPT_ID", Operator.getID());
		returnParm.setData("OPT_IP", Operator.getIP());
		returnParm.setData("OPT_NAME", Operator.getName());
		returnParm.setData("HIS_CODE", "000551");
		returnParm.setData("CARD_NO", cardNo);
		returnParm.setData("PERSON_NO", personNo);			
		returnParm.setData("O_TRAN_NO", caseNo);
		returnParm.setData("O_STREAM_NO", streamNo);
		returnParm.setData("O_PAY_SEQ", ""); // ԤԼ�ҺŲ�����Ʊ��
		returnParm.setData("TRAN_NO", caseNo);
		returnParm.setData("AMOUNT", String.valueOf(amt));
		returnParm.setData("DATE", sf.format(optDate));
		returnParm.setData("RETURN_TYPE", "1"); // ԤԼ�Һ��˷�
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		return returnParm;
	}

	/**
	 * ԤԼ�Һų�����Ǯ
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm cancelReg(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_TERM");
		String guid = (String) parm.getData("GUID");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT CASE_NO,MR_NO FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL AND ARRIVE_FLG='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CASE_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		sql = "SELECT CARD_NO,MR_NO,BUSINESS_NO,PAT_NAME,AMT,STATE,BUSINESS_TYPE,BANK_FLG "
				+ "FROM EKT_CCB_TRADE "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BUSINESS_TYPE='REG' AND STATE='1'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		// ���ױ��˿�
		TParm tradeParm = new TParm();
		tradeParm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
		tradeParm.setData("CARD_NO", result.getData("CARD_NO", 0));
		tradeParm.setData("MR_NO", result.getData("MR_NO", 0));
		tradeParm.setData("CASE_NO", caseNo);
		tradeParm.setData("BUSINESS_NO", parm.getData("BUSINESS_NO"));
		tradeParm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		tradeParm.setData("AMT", -(Double) result.getData("AMT", 0));
		tradeParm.setData("STATE", "2");
		tradeParm.setData("BUSINESS_TYPE", "REGT");
		tradeParm.setData("RECEIPT_NO", "");
		tradeParm.setData("OPT_USER", parm.getData("OPT_USER"));
		tradeParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		tradeParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		tradeParm.setData("TOKEN", parm.getData("TOKEN"));
		tradeParm.setData("GUID", parm.getData("GUID"));
		result = this.insertCcbTrade(tradeParm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "�˷�ʧ��");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "�˷ѳɹ�");
		return returnParm;
	}

	/**
	 * �Һ�ȡ��������
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelTranCurrent(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT MR_NO,NHI_NO,CONFIRM_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS "
				+ "ADM_DATE,SESSION_CODE,CLINICROOM_NO,"
				+ "QUE_NO,APPT_CODE,"
				+ "ADM_TYPE,CLINICTYPE_CODE"
				+ "FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			result.setData("EXECUTE_FLAG", ISERROR);
			result.setData("EXECUTE _MESSAGE", "����ȡ��ʧ��");
			return returnParm;
		}
		String mrNo = (String) result.getData("MR_NO");
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		String nhiNo = (String) result.getData("NHI_NO", 0);
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		// ���vip�Ļ��ͷ�VIP��
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
				+ admDate
				+ "' AND "
				+ "SESSION_CODE = '"
				+ sessionCode
				+ "' AND "
				+ "CLINICROOM_NO = '"
				+ clinicroomNo
				+ "' AND "
				+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		// �˹Ҳ���
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		// �س�Ʊ�ݱ�,���ױ��˷�
		result = this.backFlush(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "����Һ���Ϣ����ȡ��ʧ��");
			return returnParm;
		}
		// ��ҽ��ȡ��ҽ��
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm insParm = new TParm();
			TParm readCardParm = new TParm();
			readCardParm.setData("STATE", "9"); // 9��ҽ�����۷�
			readCardParm.setData("CARD_TYPE", 2); // 9������
			readCardParm.setData("SSCARD_NO", (String) parm
					.getData("SSCARD_NO"));
			readCardParm.setData("MR_NO", mrNo);
			readCardParm.setData("OPT_USER", parm.getData("OPT_NAME"));
			readCardParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			// ��ȡҽ������Ϣ
			insParm = this.readCard(readCardParm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ִ��ҽ��ʧ�ܣ���ֻ֧�ֳ�ְ");
				return returnParm;
			}
			// �ָ���
			TParm insFeeParm = new TParm();
			result = this.insExeFee(false, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ���˿�ʧ��");
				return returnParm;
			}
			// �ָ���
			result = this.insExeFee(true, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "ҽ���˿�ʧ��");
				return returnParm;
			}

		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "����ȡ���ɹ�");
		return returnParm;
	}

	/**
	 * ��ȡFTP�����ļ�
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm readFile(TParm tparm) {
		String fileName = (String) tparm.getData("filName");
		String fileUrl = this.getPath() + fileName;
		TParm parm = new TParm();
		Map map = new HashMap();
		List<String> cardList = new ArrayList();
		List<String> caseList = new ArrayList();
		List<String> receiptList = new ArrayList();
		List<String> businessList = new ArrayList();
		List<String> amtList = new ArrayList();
		List<String> dateList = new ArrayList();
		List<String> ipList = new ArrayList();
		List<String> userList = new ArrayList();
		List<String> guidList = new ArrayList();
		List<String> typeList = new ArrayList();
		try {
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();
			DataInputStream input = new DataInputStream(conn.getInputStream());
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] str = line.split("	");
				cardList.add(str[0]);
				caseList.add(str[1]);
				receiptList.add(str[2]);
				businessList.add(str[3]);
				amtList.add(str[4]);
				dateList.add(str[5]);
				ipList.add(str[6]);
				userList.add(str[7]);
				guidList.add(str[8]);
				typeList.add(str[9]);
			}
			map.put("CARD_NO", cardList);
			map.put("CASE_NO", caseList);
			map.put("RECEIPT_NO", receiptList);
			map.put("BUSINESS_NO", businessList);
			map.put("AMT", amtList);
			map.put("OPT_DATE", dateList);
			map.put("OPT_TERM", ipList);
			map.put("OPT_USER", userList);
			map.put("GUID", guidList);
			map.put("BUSINESS_TYPE", typeList);
			input.close();
		} catch (FileNotFoundException e) {
			parm.setErr(-1, "��ȡ�ļ�ʧ��");
		} catch (IOException e) {
			parm.setErr(-1, "��ȡ�ļ�ʧ��");
		}
		return new TParm(map);
	}

	/**
	 * ����HL7
	 */
	private void sendHL7Mes(TParm sendHL7Parm) {
		/**
		 * ����HL7��Ϣ
		 * 
		 * @param admType
		 *            String �ż�ס��
		 * @param catType
		 *            ҽ�����
		 * @param patName
		 *            ��������
		 * @param caseNo
		 *            String �����
		 * @param applictionNo
		 *            String �����
		 * @param flg
		 *            String ״̬(0,����1,ȡ��)
		 */
		int count = 0;
		if (null != sendHL7Parm && null != sendHL7Parm.getData("ADM_TYPE"))
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		List list = new ArrayList();
		String patName = (String) sendHL7Parm.getData(("PAT_NAME"));
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			if (temp.getValue("TEMPORARY_FLG").length() == 0) {
				continue;
			}
			String cat1Type = temp.getValue("ORDER_CAT1_CODE");
			TParm cat1TypeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"
							+ cat1Type + "'"));
			if (!cat1TypeParm.getValue("CAT1_TYPE", 0).equals("RIS")
					&& !cat1TypeParm.getValue("CAT1_TYPE", 0).equals("LIS")) {
				continue;
			}
			String admType = temp.getValue("ADM_TYPE");
			String caseNo = (String) sendHL7Parm.getData("CASE_NO");
			String sql = " SELECT CASE_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
					+ "  WHERE CASE_NO ='"
					+ caseNo
					+ "' "
					+ "    AND RX_NO='"
					+ temp.getValue("RX_NO")
					+ "' "
					+ "    AND ORDERSET_CODE IS NOT NULL "
					+ "    AND ORDERSET_CODE = ORDER_CODE "
					+ "    AND SEQ_NO='"
					+ temp.getValue("SEQ_NO")
					+ "' AND ADM_TYPE='" + admType + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			parm.setData("FLG", 0);
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
		}
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		if (resultParm.getErrCode() < 0) {

		}
	}

	/**
	 * ̩�ĹҺ��Ŷӽк�
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// �Һ�����
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// �������
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("DR_CODE", 0) + "|";
		// ҽʦ����
		sendString += result.getValue("USER_NAME", 0) + "|";
		// �ű�
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// ���
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// ���߲�����
		sendString += result.getValue("MR_NO", 0) + "|";

		// ��������
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// �����Ա�

		sendString += result.getValue("SEX", 0) + "|";
		// ��������
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// �������
		sendString += result.getValue("QUE_NO", 0) + "|";

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// �޹�����
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// �ѹ�����
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";

		// ʱ���
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));

		// �˹ҽк�
		if ("UNREG".equals(type)) {

			// ԤԼ����
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				sendString += "|" + startTimeParm.getValue("START_TIME", 0)
						+ "00";
			}

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);

		} else if ("REG".equals(type)) {
			sendString += "|";
			// ԤԼ����
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|�ڷ��ڴ�л��|000875|�����|����ҽʦ|06����|000000001009|������|Ů|1936-01-05|2|60|2|����|
			inParm.setData("msg", sendString);
			inParm.setData("msg", sendString);
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TIOM_AppServer.executeAction(socket,"action.device.CallNoAction",
					"doReg", inParm);
										
		}

		return "true";

	}

	/**
	 * ����ɷ��˷��õ�
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getTradeInfo(TParm parm) {
		TParm returnParm = new TParm();
		String receiptNo = (String) parm.getData("RECEIPT_NO");
		String caseNo = (String) parm.getData("CASE_NO");
		String sql = "SELECT CARD_NO,"
				+ "OPT_DATE,"
				+ "GUID,MR_NO,PAT_NAME,RECEIPT_NO,"
				+ "(select CCB_PERSON_NO FROM SYS_PATINFO B WHERE  A.MR_NO=B.MR_NO) AS PERSON_NO, "
				+ "BUSINESS_NO " + "FROM EKT_CCB_TRADE A "
				+ "WHERE RECEIPT_NO='" + receiptNo
				+ "' AND STATE='1' AND CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡҽ����Ϣ
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getOrder(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm data = query("query", parm);  
		if (data.getErrCode() < 0) {
			err("Order+ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		return data;  
	}

}
