package jdo.sta;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ҽ�Ƽ��ָ�깤����
 * </p>
 * 
 * <p>
 * Description: ҽ�Ƽ��ָ�깤����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl 2012-12-03
 * @version 1.0
 */
public class StaGenMroDataTool extends TJDOTool {
	public StaGenMroDataTool() {
		drList = getDrList();
		getDICTIONARY();
		onInit();
	}

	TDataStore ICD_DATA = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	TDataStore DICTIONARY;
	Map drList;
	SimpleDateFormat Df = new SimpleDateFormat();
	/**
	 * ʵ��
	 */
	public static StaGenMroDataTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static StaGenMroDataTool getInstance() {
		if (instanceObject == null)
			instanceObject = new StaGenMroDataTool();
		return instanceObject;
	}

	/**
	 * ��ȡ���ֵ�����
	 */
	private void getDICTIONARY() {
		if (DICTIONARY == null) {
			String sql = "SELECT GROUP_ID,ID,CHN_DESC FROM SYS_DICTIONARY ";
			DICTIONARY = new TDataStore();
			DICTIONARY.setSQL(sql);
			DICTIONARY.retrieve();
		}
	}

	String DictionaryID;
	String DictionaryGroup;

	/**
	 * ��ȡ���ֵ��е�����
	 * 
	 * @param groupId
	 *            String
	 * @param Id
	 *            String
	 * @return String
	 */
	private String getDictionaryDesc(String groupId, String Id) {
		if (DICTIONARY == null) {
			return "";
		}
		DictionaryGroup = groupId;
		DictionaryID = Id;
		DICTIONARY.filterObject(this, "filterDictionary");
		return DICTIONARY.getItemString(0, "CHN_DESC");
	}

	/**
	 * ��ȡҽʦ�����б�
	 * 
	 * @return Map
	 */
	private Map getDrList() {
		String sql = "SELECT USER_ID,USER_NAME FROM SYS_OPERATOR";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map list = new HashMap();
		for (int i = 0; i < result.getCount(); i++) {
			list.put(result.getValue("USER_ID", i), result.getValue(
					"USER_NAME", i));
		}
		return list;
	}

	/**
	 * �����������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSavedata(TParm parm, TConnection conn) {
		TParm result = new TParm();
		StringBuffer reBuffer = new StringBuffer();
		int count = 0;
		for (int i = 0; i < parm.getCount(); i++) {
			TParm comParm = getComParm(parm.getRow(i));
			if (comParm.getErrCode() < 0) {
				reBuffer.append("������:" + parm.getRow(i).getValue("MR_NO")
						+ "\r\n" + comParm.getErrText());
				count++;
				continue;
			}
			STAValidator vali = new STAValidator();
			TParm valiParm = vali.checkUI(comParm);
			if (valiParm.getErrCode() < 0) {
				reBuffer.append("������:" + comParm.getValue("P3") + "\r\n"
						+ valiParm.getErrText());
				count++;
				continue;
			}
			result = this.DeleteData(parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			result = this.updateSTAFlg(parm.getRow(i), "", "1", conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			result = this.insertData(comParm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
			conn.commit();
		}
		if (count > 0) {
			result.setErrCode(-100);
			result.setData("ERRORLOG", reBuffer.toString());
			result.setData("ERRORCOUNT", count);
		}
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param comParm
	 * @param conn
	 * @return
	 */
	public TParm DeleteData(TParm comParm, TConnection conn) {
		String p3 = comParm.getValue("MR_NO");// ������
		String p2 = comParm.getValue("IN_COUNT");// סԺ����
		String sql = "DELETE FROM  STA_MRO_DAILY WHERE P3='" + p3 + "' AND P2="
				+ p2;
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ������������
	 * 
	 * @param parm
	 * @return
	 */
	public Map groupByPatParm(TParm parm) {
		Map result = new HashMap();
		if (parm == null) {
			return null;
		}
		int count = parm.getCount();
		if (count < 1) {
			return null;
		}
		TParm temp = new TParm();
		String[] names = parm.getNames();
		if (names == null) {
			return null;
		}
		if (names.length < 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(";");
		}
		try {
			sb.replace(sb.lastIndexOf(";"), sb.length(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TParm tranParm = new TParm();
		for (int i = 0; i < count; i++) {
			String orderNo = parm.getValue("GROUP_ID", i);
			if (result.get(orderNo) == null) {
				temp = new TParm();
				temp.addRowData(parm, i, sb.toString());
				result.put(orderNo, temp);
			} else {
				tranParm = (TParm) result.get(orderNo);
				tranParm.addRowData(parm, i, sb.toString());
				result.put(orderNo, tranParm);
			}
		}
		return result;
	}

	/**
	 * ���ÿ����Ҫ�����м�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getComParm(TParm parm) {
		TParm DataParm = new TParm();
		String caseNo = parm.getValue("CASE_NO");
		String mrNo = parm.getValue("MR_NO");
		TParm reCordParm = this.getMroReCordData(parm);
		if (reCordParm.getErrCode() < 0) {
			DataParm.setErrCode(-100);
			DataParm.setErrText("��ѯMroReCord���ݳ���");
			return DataParm;
		}
		DataParm.setData("P900",
				reCordParm.getValue("HOSP_ID", 0).equals("") ? "" : reCordParm
						.getData("HOSP_ID", 0));// 1 ҽ�ƻ�������
		TParm hisParm = SYSRegionTool.getInstance().selectdata(
				reCordParm.getValue("REGION_CODE", 0));
		DataParm.setData("P6891", hisParm.getValue("REGION_CHN_DESC", 0)
				.equals("") ? "" : hisParm.getValue("REGION_CHN_DESC", 0));// 2
		// ��������
		DataParm.setData("P686",
				reCordParm.getValue("NHI_NO", 0).equals("") ? "" : reCordParm
						.getData("NHI_NO", 0)); // 3 ҽ�Ʊ����ֲᣨ������
		DataParm.setData("P800", reCordParm.getValue("NHI_CARDNO", 0)
				.equals("") ? "" : reCordParm.getData("NHI_CARDNO", 0));// 4
		// ��������
		DataParm.setData("P1",
				reCordParm.getValue("MRO_CTZ", 0).equals("") ? "" : reCordParm
						.getData("MRO_CTZ", 0));// 5 ҽ�Ƹ��ʽ
		DataParm.setData("P2",
				reCordParm.getValue("IN_COUNT", 0).equals("") ? 0 : reCordParm
						.getData("IN_COUNT", 0));// 6 סԺ����
		DataParm.setData("P3", reCordParm.getValue("MR_NO", 0).equals("") ? ""
				: reCordParm.getData("MR_NO", 0)); // 7 ������
		// ��ȡ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(reCordParm.getValue("MR_NO", 0));
		DataParm.setData("P4",
				reCordParm.getValue("PAT_NAME", 0).equals("") ? "" : reCordParm
						.getData("PAT_NAME", 0));// 8 ����
		DataParm.setData("P5", reCordParm.getValue("SEX", 0).equals("") ? ""
				: reCordParm.getData("SEX", 0)); // 9 �Ա�
		DataParm.setData("P6",
				reCordParm.getValue("BIRTH_DATE", 0).equals("") ? ""
						: getFormateDate(reCordParm.getTimestamp("BIRTH_DATE",
								0), "yyyyMMdd"));// 10 ��������
		if (reCordParm.getTimestamp("BIRTH_DATE", 0) == null) {
			DataParm.setErrCode(-101);
			DataParm.setErrText("��ѯ��������Ϊ��");
			return DataParm;
		}
		String[] res;
		res = StringTool
				.CountAgeByTimestamp(reCordParm.getTimestamp("BIRTH_DATE", 0),
						StringTool.getTimestamp(StringTool.getString(reCordParm
								.getTimestamp("IN_DATE", 0), "yyyyMMdd"),
								"yyyyMMdd"));
		int NbAge = 0;// ��λ����
		// ����С��1����
		if (TypeTool.getInt(res[0]) < 1) {
			NbAge = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool
					.getString(reCordParm.getTimestamp("IN_DATE", 0),
							"yyyyMMdd"), "yyyyMMdd"), reCordParm.getTimestamp(
					"BIRTH_DATE", 0));
		} else if (TypeTool.getInt(res[0]) >= 1) {
			DataParm.setData("P7", res[0].equals("") ? 0 : res[0]);// 11 ����
		} else {
			DataParm.setData("P7", "");
		}
		DataParm.setData("P8", getReCode("SYS_DICTIONARY", "SYS_MARRIAGE",
				"STA1_CODE", "ID", reCordParm.getValue("MARRIGE", 0))
				.equals("") ? "9" : getReCode("SYS_DICTIONARY", "SYS_MARRIAGE",
				"STA1_CODE", "ID", reCordParm.getValue("MARRIGE", 0)));// 12
		// ����״̬
		// Ϊ��ʱĬ��д��������
		DataParm.setData("P9", getReCode("SYS_DICTIONARY", "SYS_OCCUPATION",
				"STA1_CODE", "ID", reCordParm.getValue("OCCUPATION", 0))
				.equals("") ? "" : getReCode("SYS_DICTIONARY",
				"SYS_OCCUPATION", "STA2_CODE", "ID", reCordParm.getValue(
						"OCCUPATION", 0)));// 13 ְҵ
		DataParm.setData("P101", "");// 14 ����ʡ��(����)
		DataParm.setData("P102", "");// 15 ��������(����)
		DataParm.setData("P103", "");// 16 ��������(����)
		DataParm.setData("P11", getDictionaryDesc("SYS_SPECIES",
				reCordParm.getValue("FOLK", 0)).equals("") ? ""
				: getDictionaryDesc("SYS_SPECIES", reCordParm.getValue("FOLK",
						0)));// 17 ����
		DataParm.setData("P12", getDictionaryDesc("SYS_NATION",
				reCordParm.getValue("NATION", 0)).equals("") ? ""
				: getDictionaryDesc("SYS_NATION", reCordParm.getValue("NATION",
						0)));// 18 ����
		String foreignerFlg = reCordParm.getValue("FOREIGNER_FLG", 0);// �Ƿ������
		if (foreignerFlg.equals("N")) {
			DataParm.setData("P13",
					reCordParm.getValue("IDNO", 0).equals("") ? "" : reCordParm
							.getData("IDNO", 0));// 19 ���֤��
		} else {
			DataParm.setData("P13", "");
		}
		DataParm.setData("P801",
				reCordParm.getValue("ADDRESS", 0).equals("") ? "" : reCordParm
						.getData("ADDRESS", 0));// 20 ��סַ
		DataParm.setData("P802", reCordParm.getValue("TEL", 0).equals("") ? ""
				: reCordParm.getData("TEL", 0));// 21 ��סլ�绰
		DataParm.setData("P803",
				reCordParm.getValue("POST_NO", 0).equals("") ? "" : reCordParm
						.getData("POST_NO", 0));// 22 ��סַ��������
		String offStr = reCordParm.getValue("OFFICE", 0) + " "
				+ reCordParm.getValue("O_ADDRESS", 0);
		DataParm.setData("P14", offStr.equals("") ? "" : offStr);// 23 ������λ����ַ
		DataParm.setData("P15", reCordParm.getValue("O_TEL", 0).equals("") ? ""
				: reCordParm.getData("O_TEL", 0));// 24 �绰
		DataParm.setData("P16",
				reCordParm.getValue("O_POSTNO", 0).equals("") ? "" : reCordParm
						.getData("O_POSTNO", 0));// 25 ������λ��������
		DataParm.setData("P17",
				reCordParm.getValue("H_ADDRESS", 0).equals("") ? ""
						: reCordParm.getData("H_ADDRESS", 0));// 26 ���ڵ�ַ
		DataParm.setData("P171",
				reCordParm.getValue("H_POSTNO", 0).equals("") ? "" : reCordParm
						.getData("H_POSTNO", 0));// 27 �������ڵ���������
		DataParm.setData("P18",
				reCordParm.getValue("CONTACTER", 0).equals("") ? ""
						: reCordParm.getData("CONTACTER", 0));// 28 ��ϵ������
		DataParm.setData("P19", getDictionaryDesc("SYS_RELATIONSHIP",
				reCordParm.getValue("RELATIONSHIP", 0)).equals("") ? ""
				: getReCode("SYS_DICTIONARY", "SYS_RELATIONSHIP", "STA2_CODE",
						"ID", reCordParm.getValue("RELATIONSHIP", 0)));// 29 ��ϵ
		DataParm.setData("P20", reCordParm.getValue("CONT_ADDRESS", 0).equals(
				"") ? "" : reCordParm.getData("CONT_ADDRESS", 0));// 30
		// ��ϵ��ַ
		DataParm.setData("P804", getReCode("SYS_DICTIONARY", "ADM_SOURCE",
				"STA1_CODE", "ID", reCordParm.getValue("ADM_SOURCE", 0))
				.equals("") ? "" : getReCode("SYS_DICTIONARY", "ADM_SOURCE",
				"STA1_CODE", "ID", reCordParm.getValue("ADM_SOURCE", 0)));// 31
		// ��Ժ;��
		DataParm.setData("P21",
				reCordParm.getValue("CONT_TEL", 0).equals("") ? "" : reCordParm
						.getData("CONT_TEL", 0));// 32��ϵ�˵绰
		DataParm.setData("P22",
				reCordParm.getValue("IN_DATE", 0).equals("") ? "" : StringTool.getTimestamp(StringTool
						.getString(reCordParm.getTimestamp("IN_DATE", 0),
						"yyyyMMdd"), "yyyyMMdd"));// 33��Ժ����   ������ɫͨ������ʱ��С����Ժʱ�� �ݾ�ȷ�촦��
		DataParm.setData("P23", getReCode("SYS_DEPT", "", "STA1_CODE",
				"DEPT_CODE", reCordParm.getValue("IN_DEPT", 0)).equals("") ? ""
				: getReCode("SYS_DEPT", "", "STA1_CODE", "DEPT_CODE",
						reCordParm.getValue("IN_DEPT", 0)));// 34��Ժ����
		DataParm.setData("P231",
				getReCode("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
						reCordParm.getValue("IN_ROOM_NO", 0)).equals("") ? ""
						: getReCode("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
								reCordParm.getValue("IN_ROOM_NO", 0)));// 35
		// ��Ժ����
		DataParm.setData("P24", "");// 36 ת�ƿƱ�(����)
		DataParm.setData("P25",
				reCordParm.getValue("OUT_DATE", 0).equals("") ? "" : reCordParm
						.getTimestamp("OUT_DATE", 0));// 37 ��Ժ����

		DataParm.setData("P26",
				getReCode("SYS_DEPT", "", "STA1_CODE", "DEPT_CODE",
						reCordParm.getValue("OUT_DEPT", 0)).equals("") ? ""
						: getReCode("SYS_DEPT", "", "STA1_CODE", "DEPT_CODE",
								reCordParm.getValue("OUT_DEPT", 0)));// 38 ��Ժ�Ʊ�
		DataParm.setData("P261",
				getReCode("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
						reCordParm.getValue("OUT_ROOM_NO", 0)).equals("") ? ""
						: getReCode("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
								reCordParm.getValue("OUT_ROOM_NO", 0)));// 39
		TParm IDiagParm = getMroDiagData(parm, "I", "Y");
		if (IDiagParm.getErrCode() < 0) {
			DataParm.setErrCode(IDiagParm.getErrCode());
			DataParm.setErrText(IDiagParm.getErrText());
			return DataParm;
		}
		// ��Ժ����
		DataParm.setData("P27", reCordParm.getValue("REAL_STAY_DAYS", 0)
				.equals("") ? 0 : reCordParm.getData("REAL_STAY_DAYS", 0));// 40ʵ��סԺ����
		DataParm.setData("P28", IDiagParm.getData("ICD_CODE").equals("") ? ""
				: IDiagParm.getData("ICD_CODE"));// 41 �ţ���������ϱ���
		DataParm.setData("P281", IDiagParm.getData("ICD_CODE").equals("") ? ""
				: IDiagParm.getData("ICD_DESC"));// 42 �ţ��������������
		DataParm.setData("P29", getReCode("SYS_DICTIONARY", "ADM_CONDITION",
				"STA1_CODE", "ID", reCordParm.getValue("IN_CONDITION", 0))
				.equals("") ? "" : getReCode("SYS_DICTIONARY", "ADM_CONDITION",
				"STA1_CODE", "ID", reCordParm.getValue("IN_CONDITION", 0)));// 43
		// ��Ժʱ���
		TParm MDiagParm = getMroDiagData(parm, "M", "Y");
		if (MDiagParm.getErrCode() < 0) {
			DataParm.setErrCode(MDiagParm.getErrCode());
			DataParm.setErrText(MDiagParm.getErrText());
			return DataParm;
		}
		TParm ODiagParm = getMroDiagData(parm, "O", "Y");
		if (ODiagParm.getErrCode() < 0) {
			DataParm.setErrCode(ODiagParm.getErrCode());
			DataParm.setErrText(ODiagParm.getErrText());
			return DataParm;
		}
		DataParm.setData("P30", MDiagParm.getValue("ICD_CODE").equals("") ?ODiagParm.getValue("ICD_CODE")
				: MDiagParm.getData("ICD_CODE"));// 44 ��Ժ��ϱ���
		DataParm.setData("P301", MDiagParm.getValue("ICD_CODE").equals("") ?ODiagParm.getData("ICD_DESC")
				: MDiagParm.getData("ICD_DESC"));// 45 ��Ժ�������
		DataParm.setData("P31", reCordParm.getValue("CONFIRM_DATE", 0).equals(
				"") ? "" : reCordParm.getTimestamp("CONFIRM_DATE", 0));// 46
		// ��Ժ��ȷ������
		DataParm.setData("P321", ODiagParm.getValue("ICD_CODE").equals("") ? ""
				: ODiagParm.getData("ICD_CODE"));// 47 ��Ժ�����
		DataParm.setData("P322", ODiagParm.getValue("ICD_CODE").equals("") ? ""
				: ODiagParm.getData("ICD_DESC"));// 48 ��Ժ���������
		DataParm.setData("P805", getReCode("SYS_DICTIONARY",
				"ADM_IN_PAT_CONDITION", "STA1_CODE", "ID",
				ODiagParm.getValue("IN_PAT_CONDITION")).equals("") ? ""
				: getReCode("SYS_DICTIONARY", "ADM_IN_PAT_CONDITION",
						"STA1_CODE", "ID", ODiagParm
								.getValue("IN_PAT_CONDITION")));// 49 ��Ҫ�����Ժ����
		DataParm.setData("P323",
				getReCode("SYS_DICTIONARY", "ADM_RETURN", "STA1_CODE", "ID",
						ODiagParm.getValue("ICD_STATUS")).equals("") ? ""
						: getReCode("SYS_DICTIONARY", "ADM_RETURN",
								"STA1_CODE", "ID", ODiagParm
										.getValue("ICD_STATUS")));// 50 ��Ҫ��ϳ�Ժ���
		// �����Ϣ
		TParm othDiagParm = this.getMroOthDiagData(parm, "O", "N");
		if (othDiagParm.getErrCode() < 0) {
			DataParm.setErrCode(othDiagParm.getErrCode());
			DataParm.setErrText(othDiagParm.getErrText());
			return DataParm;
		}
		DataParm.setData("P324",
				othDiagParm.getValue("DIAG_CODE1").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE1"));// 51������ϱ���1
		DataParm.setData("P325", othDiagParm.getValue("DIAG1").equals("") ? ""
				: othDiagParm.getData("DIAG1"));// 52 ������ϼ�������1
		DataParm.setData("P806", othDiagParm.getValue("IN_PAT_CONDITION1")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION1"));// 53���������Ժ����1
		DataParm.setData("P326",
				othDiagParm.getValue("ICD_STATUS1").equals("") ? ""
						: othDiagParm.getData("ICD_STATUS1"));// 54 ������ϳ�Ժ���1
		DataParm.setData("P327",
				othDiagParm.getValue("DIAG_CODE2").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE2"));// 55 ������ϱ���2
		DataParm.setData("P328", othDiagParm.getValue("DIAG2").equals("") ? ""
				: othDiagParm.getData("DIAG2"));// 56 ������ϼ�������2
		DataParm.setData("P807", othDiagParm.getValue("IN_PAT_CONDITION2")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION2"));// 57
		// ���������Ժ����2
		DataParm.setData("P329",
				othDiagParm.getValue("ICD_STATUS2").equals("") ? ""
						: othDiagParm.getData("ICD_STATUS2"));// 58 ������ϳ�Ժ���2
		DataParm.setData("P3291",
				othDiagParm.getValue("DIAG_CODE3").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE3"));// 59 ������ϱ���3
		DataParm.setData("P3292", othDiagParm.getValue("DIAG3").equals("") ? ""
				: othDiagParm.getData("DIAG3"));// 60 ������ϼ�������3
		DataParm.setData("P808", othDiagParm.getValue("IN_PAT_CONDITION3")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION3"));// 61
		// ���������Ժ����3
		DataParm.setData("P3293", othDiagParm.getValue("ICD_STATUS3")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS3"));// 62
		// ������ϳ�Ժ���3
		DataParm.setData("P3294",
				othDiagParm.getValue("DIAG_CODE4").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE4"));// 63 ������ϱ���4
		DataParm.setData("P3295", othDiagParm.getData("DIAG4").equals("") ? ""
				: othDiagParm.getData("DIAG4"));// 64 ������ϼ�������4
		DataParm.setData("P809", othDiagParm.getValue("IN_PAT_CONDITION4")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION4"));// 65
		// ���������Ժ����4
		DataParm.setData("P3296", othDiagParm.getValue("ICD_STATUS4")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS4"));// 66
		// ������ϳ�Ժ���4
		DataParm.setData("P3297",
				othDiagParm.getValue("DIAG_CODE5").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE5"));// 67 ������ϱ���5
		DataParm.setData("P3298", othDiagParm.getValue("DIAG5").equals("") ? ""
				: othDiagParm.getData("DIAG5"));// 68 ������ϼ�������5
		DataParm.setData("P810", othDiagParm.getValue("IN_PAT_CONDITION5")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION5"));// 69
		// ���������Ժ����5
		DataParm.setData("P3299", othDiagParm.getValue("ICD_STATUS5")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS5"));// 70
		// ������ϳ�Ժ���5
		DataParm.setData("P3281",
				othDiagParm.getValue("DIAG_CODE6").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE6"));// 71 ������ϱ���6
		DataParm.setData("P3282", othDiagParm.getValue("DIAG6").equals("") ? ""
				: othDiagParm.getData("DIAG6"));// 72 ������ϼ�������6
		DataParm.setData("P811", othDiagParm.getValue("IN_PAT_CONDITION6")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION6"));// 73
		// ���������Ժ����6
		DataParm.setData("P3283", othDiagParm.getValue("ICD_STATUS6")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS6"));// 74
		// ������ϳ�Ժ���6
		DataParm.setData("P3284",
				othDiagParm.getValue("DIAG_CODE7").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE7"));// 75 ������ϱ���7
		DataParm.setData("P3285", othDiagParm.getValue("DIAG7").equals("") ? ""
				: othDiagParm.getData("DIAG7"));// 76 ������ϼ�������7
		DataParm.setData("P812", othDiagParm.getValue("IN_PAT_CONDITION7")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION7"));// 77
		// ���������Ժ����7
		DataParm.setData("P3286", othDiagParm.getValue("ICD_STATUS7")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS7"));// 78
		// ������ϳ�Ժ���7
		DataParm.setData("P3287",
				othDiagParm.getValue("DIAG_CODE8").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE8"));// 79 ������ϱ���8
		DataParm.setData("P3288", othDiagParm.getValue("DIAG8").equals("") ? ""
				: othDiagParm.getData("DIAG8"));// 80 ������ϼ�������8
		DataParm.setData("P813", othDiagParm.getValue("IN_PAT_CONDITION8")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION8"));// 81
		// ���������Ժ����8
		DataParm.setData("P3289", othDiagParm.getValue("ICD_STATUS8")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS8"));// 82
		// ������ϳ�Ժ���8
		DataParm.setData("P3271",
				othDiagParm.getValue("DIAG_CODE9").equals("") ? ""
						: othDiagParm.getData("DIAG_CODE9"));// 83 ������ϱ���9
		DataParm.setData("P3272", othDiagParm.getData("DIAG9").equals("") ? ""
				: othDiagParm.getData("DIAG9"));// 84 ������ϼ�������9
		DataParm.setData("P814", othDiagParm.getValue("IN_PAT_CONDITION9")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION9"));// 85
		// ���������Ժ����9
		DataParm.setData("P3273", othDiagParm.getValue("ICD_STATUS9")
				.equals("") ? "" : othDiagParm.getData("ICD_STATUS9"));// 86
		// ������ϳ�Ժ���9
		DataParm.setData("P3274", othDiagParm.getValue("DIAG_CODE10")
				.equals("") ? "" : othDiagParm.getData("DIAG_CODE10"));// 87
		// ������ϱ���10
		DataParm.setData("P3275",
				othDiagParm.getValue("DIAG10").equals("") ? "" : othDiagParm
						.getData("DIAG10"));// 88 ������ϼ�������10
		DataParm.setData("P815", othDiagParm.getValue("IN_PAT_CONDITION10")
				.equals("") ? "" : othDiagParm.getData("IN_PAT_CONDITION10"));// 89
		// ���������Ժ����10
		DataParm.setData("P3276", othDiagParm.getValue("ICD_STATUS10").equals(
				"") ? "" : othDiagParm.getData("ICD_STATUS10"));// 90
		// ������ϳ�Ժ���10
		DataParm.setData("P689", reCordParm.getValue("INFECT_COUNT", 0).equals(
				"") ? "" : reCordParm.getData("INFECT_COUNT", 0));// 91 ҽԺ��Ⱦ�ܴ���
		DataParm.setData("P351", reCordParm.getValue("PATHOLOGY_DIAG", 0)
				.equals("") ? "" : reCordParm.getData("PATHOLOGY_DIAG", 0));// 92
		// ������ϱ���1
		DataParm.setData("P352", reCordParm.getValue("PATHOLOGY_DIAG", 0)
				.equals("") ? "" : getICD_DESC(reCordParm.getValue(
				"PATHOLOGY_DIAG", 0)));// 93 ������ϱ�������1
		DataParm.setData("P816", reCordParm.getValue("PATHOLOGY_NO", 0).equals(
				"") ? "" : reCordParm.getData("PATHOLOGY_NO", 0));// 94 �����1
		DataParm.setData("P353", reCordParm.getValue("PATHOLOGY_DIAG2", 0)
				.equals("") ? "" : reCordParm.getData("PATHOLOGY_DIAG2", 0));// 95
		// �������2
		DataParm.setData("P354", reCordParm.getValue("PATHOLOGY_DIAG2", 0)
				.equals("") ? "" : getICD_DESC(reCordParm.getValue(
				"PATHOLOGY_DIAG2", 0)));// 96 �����������2
		DataParm.setData("P817", reCordParm.getValue("PATHOLOGY_NO2", 0)
				.equals("") ? "" : reCordParm.getData("PATHOLOGY_NO2", 0));// 97
		// �����2
		DataParm.setData("P355", reCordParm.getValue("PATHOLOGY_DIAG3", 0)
				.equals("") ? "" : reCordParm.getData("PATHOLOGY_DIAG3", 0));// 98
		// �������3
		DataParm.setData("P356", reCordParm.getValue("PATHOLOGY_DIAG3", 0)
				.equals("") ? "" : getICD_DESC(reCordParm.getValue(
				"PATHOLOGY_DIAG3", 0)));// 99 �����������3
		DataParm.setData("P818", reCordParm.getValue("PATHOLOGY_NO3", 0)
				.equals("") ? "" : reCordParm.getData("PATHOLOGY_NO3", 0));// 100
		// �����3
		DataParm.setData("P361",
				reCordParm.getValue("EX_RSN", 0).equals("") ? "" : reCordParm
						.getData("EX_RSN", 0));// 101 ����1
		DataParm.setData("P362",
				reCordParm.getValue("EX_RSN", 0).equals("") ? ""
						: getICD_DESC(reCordParm.getValue("EX_RSN", 0)));// 102��������1
		DataParm.setData("P363",
				reCordParm.getValue("EX_RSN2", 0).equals("") ? "" : reCordParm
						.getData("EX_RSN2", 0));// 103 ����2
		DataParm.setData("P364",
				reCordParm.getValue("EX_RSN2", 0).equals("") ? ""
						: getICD_DESC(reCordParm.getValue("EX_RSN2", 0)));// 104
		// ��������2
		DataParm.setData("P365",
				reCordParm.getValue("EX_RSN3", 0).equals("") ? "" : reCordParm
						.getData("EX_RSN3", 0));// 105 ����3
		DataParm.setData("P366",
				reCordParm.getValue("EX_RSN3", 0).equals("") ? ""
						: getICD_DESC(reCordParm.getValue("EX_RSN3", 0)));// 106
		// ������3
		DataParm.setData("P371", "");// 107 ����ԭ��������
		DataParm.setData("P372",
				reCordParm.getValue("ALLEGIC", 0).equals("") ? "" : reCordParm
						.getData("ALLEGIC", 0));// 108 ����ҩ������
		DataParm.setData("P38", reCordParm.getValue("HBSAG", 0).equals("") ? ""
				: reCordParm.getData("HBSAG", 0));// 109 HBsAg
		DataParm.setData("P39",
				reCordParm.getValue("HCV_AB", 0).equals("") ? "" : reCordParm
						.getData("HCV_AB", 0));// 110 HCV-Ab
		DataParm.setData("P40",
				reCordParm.getValue("HIV_AB", 0).equals("") ? "" : reCordParm
						.getData("HIV_AB", 0));// 111 HIV-Ab
		if (!reCordParm.getValue("QUYCHK_OI", 0).equals("1")
				&& !reCordParm.getValue("QUYCHK_OI", 0).equals("2")) {
			DataParm.setData("P411", "9");
		} else {
			DataParm.setData("P411", reCordParm.getValue("QUYCHK_OI", 0)
					.equals("") ? "" : reCordParm.getData("QUYCHK_OI", 0));// 112
			// �������Ժ��Ϸ������
		}
		if (!reCordParm.getValue("QUYCHK_INOUT", 0).equals("1")
				&& !reCordParm.getValue("QUYCHK_INOUT", 0).equals("2")) {
			DataParm.setData("P412", "9");
		} else {
			DataParm.setData("P412", reCordParm.getValue("QUYCHK_INOUT", 0)
					.equals("") ? "" : reCordParm.getData("QUYCHK_INOUT", 0));// 113��Ժ���Ժ��Ϸ������
		}
		if (!reCordParm.getValue("QUYCHK_OPBFAF", 0).equals("1")
				&& !reCordParm.getValue("QUYCHK_OPBFAF", 0).equals("2")) {
			DataParm.setData("P413", "9");
		} else {
			DataParm.setData("P413", reCordParm.getValue("QUYCHK_OPBFAF", 0)
					.equals("") ? "" : reCordParm.getData("QUYCHK_OPBFAF", 0));// 114
			// ��ǰ��������Ϸ������
		}
		if (!reCordParm.getValue("QUYCHK_CLPA", 0).equals("1")
				&& !reCordParm.getValue("QUYCHK_CLPA", 0).equals("2")) {
			DataParm.setData("P414", "9");
		} else {
			DataParm.setData("P414", reCordParm.getValue("QUYCHK_CLPA", 0)
					.equals("") ? "" : reCordParm.getData("QUYCHK_CLPA", 0));// 115
			// �ٴ��벡����Ϸ������
		}
		if (!reCordParm.getValue("QUYCHK_RAPA", 0).equals("1")
				&& !reCordParm.getValue("QUYCHK_RAPA", 0).equals("2")) {
			DataParm.setData("P415", "9");
		} else {
			DataParm.setData("P415", reCordParm.getValue("QUYCHK_RAPA", 0)
					.equals("") ? "" : reCordParm.getData("QUYCHK_RAPA", 0));// 116
			// �����벡����Ϸ������
		}
		DataParm.setData("P421",
				reCordParm.getValue("GET_TIMES", 0).equals("") ? ""
						: reCordParm.getData("GET_TIMES", 0));// 117 ���ȴ���
		DataParm.setData("P422", reCordParm.getValue("SUCCESS_TIMES", 0)
				.equals("") ? "" : reCordParm.getData("SUCCESS_TIMES", 0));// 118
		// ���ȳɹ�����
		DataParm.setData("P687", reCordParm.getValue("MDIAG_BASIS", 0).equals(
				"") ? "" : reCordParm.getData("MDIAG_BASIS", 0));// 119
		// ����������
		DataParm.setData("P688", reCordParm.getValue("DIF_DEGREE", 0)
				.equals("") ? "" : reCordParm.getData("DIF_DEGREE", 0));// 120
		// �ֻ��̶�
		DataParm.setData("P431", reCordParm.getValue("DIRECTOR_DR_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"DIRECTOR_DR_CODE", 0)));// 121 ������
		DataParm.setData("P432", reCordParm.getValue("PROF_DR_CODE", 0).equals(
				"") ? "" : drList.get(reCordParm.getValue("PROF_DR_CODE", 0)));// 122
		// ������ҽʦ
		DataParm.setData("P433", reCordParm.getValue("ATTEND_DR_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"ATTEND_DR_CODE", 0)));// 123 ����ҽʦ
		DataParm.setData("P434", reCordParm.getValue("VS_DR_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue("VS_DR_CODE",
				0)));// 124 ����ҽʦ
		DataParm.setData("P819", reCordParm.getValue("VS_NURSE_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"VS_NURSE_CODE", 0)));// 125 ���λ�ʿ
		DataParm.setData("P435", reCordParm.getValue("INDUCATION_DR_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"INDUCATION_DR_CODE", 0)));// 126 ����ҽʦ
		DataParm.setData("P436", reCordParm.getValue("GRADUATE_INTERN_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"GRADUATE_INTERN_CODE", 0)));// 127 �о���ʵϰҽʦ
		DataParm.setData("P437", reCordParm.getValue("INTERN_DR_CODE", 0)
				.equals("") ? "" : drList.get(reCordParm.getValue(
				"INTERN_DR_CODE", 0)));// 128 ʵϰҽʦ
		DataParm.setData("P438",
				reCordParm.getValue("ENCODER", 0).equals("") ? "" : drList
						.get(reCordParm.getValue("ENCODER", 0)));// 129 ����Ա
		DataParm.setData("P44",
				reCordParm.getValue("QUALITY", 0).equals("") ? "" : reCordParm
						.getData("QUALITY", 0));// 130 ��������
		DataParm.setData("P45",
				reCordParm.getValue("CTRL_DR", 0).equals("") ? "" : drList
						.get(reCordParm.getValue("CTRL_DR", 0)));// 131 �ʿ�ҽʦ
		DataParm.setData("P46",
				reCordParm.getValue("CTRL_NURSE", 0).equals("") ? "" : drList
						.get(reCordParm.getValue("CTRL_NURSE", 0)));// 132 �ʿػ�ʿ
		DataParm.setData("P47",
				reCordParm.getValue("CTRL_DATE", 0).equals("") ? ""
						: reCordParm.getTimestamp("CTRL_DATE", 0));// 133
		// �ʿ�����
		// ������Ϣ
		TParm opeParm = this.getMroReCordOpData(parm);
		if (opeParm.getErrCode() < 0) {
			DataParm.setErrCode(opeParm.getErrCode());
			DataParm.setErrText(opeParm.getErrText());
			return DataParm;
		}
		// System.out.println("������Ϣ  " + opeParm);
		DataParm.setData("P490", opeParm.getData("OP_CODE1"));// 134 ������������1
		DataParm.setData("P491", opeParm.getValue("OP_DATE1").equals("") ? null
				: opeParm.getTimestamp("OP_DATE1"));// 135 ������������1
		DataParm.setData("P820", opeParm.getData("OP_LEVEL1"));// 136 ��������1
		DataParm.setData("P492", opeParm.getValue("OP_DESC1"));// 137 ������������1
		DataParm.setData("P493", opeParm.getData("OPE_SITE1"));// 138 ����������λ1
		DataParm.setData("P494", opeParm.getData("OP_TIME1"));// 139 ��������ʱ��1
		DataParm.setData("P495", opeParm.getData("MAIN_SUGEON1"));// 140 ����1
		DataParm.setData("P496", opeParm.getData("AST_DR11"));// 141 ����1
		DataParm.setData("P497", opeParm.getData("AST_DR21"));// 142 ����1
		DataParm.setData("P498", opeParm.getData("ANA_WAY1"));// 143 ����ʽ1
		DataParm.setData("P4981", opeParm.getData("ANA_LEVEL1"));// 144 ����ּ�1
		DataParm.setData("P499", opeParm.getData("HEALTH_LEVEL1"));// 145
		// �п����ϵȼ�1
		DataParm.setData("P4910", opeParm.getData("ANA_DR1"));// 146 ����ҽʦ1
		DataParm.setData("P4911", opeParm.getData("OP_CODE2"));// 147 ������������2
		DataParm.setData("P4912",
				opeParm.getValue("OP_DATE2").equals("") ? null : opeParm
						.getTimestamp("OP_DATE2"));// 148 ������������2
		DataParm.setData("P821", opeParm.getData("OP_LEVEL2"));// 149 ��������2
		DataParm.setData("P4913", opeParm.getValue("OP_DESC2"));// 150 ������������2
		DataParm.setData("P4914", opeParm.getData("OPE_SITE2"));// 151 ����������λ2
		DataParm.setData("P4915", opeParm.getData("OP_TIME2"));// 152 ��������ʱ��2
		DataParm.setData("P4916", opeParm.getData("MAIN_SUGEON2"));// 153 ����2
		DataParm.setData("P4917", opeParm.getData("AST_DR12"));// 154 ����2
		DataParm.setData("P4918", opeParm.getData("AST_DR22"));// 155 ����2
		DataParm.setData("P4919", opeParm.getData("ANA_WAY2"));// 156 ����ʽ2
		DataParm.setData("P4982", opeParm.getData("ANA_LEVEL2"));// 157 ����ּ�2
		DataParm.setData("P4920", opeParm.getData("HEALTH_LEVEL2"));// 158�п����ϵȼ�2
		DataParm.setData("P4921", opeParm.getData("ANA_DR2"));// 159 ����ҽʦ2
		DataParm.setData("P4922", opeParm.getData("OP_CODE3"));// 160 ������������3
		DataParm.setData("P4923",
				opeParm.getValue("OP_DATE3").equals("") ? null : opeParm
						.getTimestamp("OP_DATE3"));// 161 ������������3
		DataParm.setData("P822", opeParm.getData("OP_LEVEL3"));// 162 ��������3
		DataParm.setData("P4924", opeParm.getValue("OP_DESC3"));// 163 ������������3
		DataParm.setData("P4925", opeParm.getData("OPE_SITE3"));// 164 ����������λ3
		DataParm.setData("P4926", opeParm.getData("OP_TIME3"));// 165 ��������ʱ��3
		DataParm.setData("P4927", opeParm.getData("MAIN_SUGEON3"));// 166 ����3
		DataParm.setData("P4928", opeParm.getData("AST_DR13"));// 167 ����3
		DataParm.setData("P4929", opeParm.getData("AST_DR23"));// 168 ����3
		DataParm.setData("P4930", opeParm.getData("ANA_WAY3"));// 169 ����ʽ3
		DataParm.setData("P4983", opeParm.getData("ANA_LEVEL3"));// 170 ����ּ�3
		DataParm.setData("P4531", opeParm.getData("HEALTH_LEVEL3"));// 171
		// �п����ϵȼ�3
		DataParm.setData("P4532", opeParm.getData("ANA_DR3"));// 172 ����ҽʦ3
		DataParm.setData("P4533", opeParm.getData("OP_CODE4"));// 173������������4
		DataParm.setData("P4534",
				opeParm.getValue("OP_DATE4").equals("") ? null : opeParm
						.getTimestamp("OP_DATE4"));// 174 ������������4
		DataParm.setData("P823", opeParm.getData("OP_LEVEL4"));// 175 ��������4
		DataParm.setData("P4535", opeParm.getValue("OP_DESC4"));// 176 ������������4
		DataParm.setData("P4536", opeParm.getData("OPE_SITE4"));// 177 ����������λ4
		DataParm.setData("P4537", opeParm.getData("OP_TIME4"));// 178 ��������ʱ��4
		DataParm.setData("P4538", opeParm.getData("MAIN_SUGEON4"));// 179 ����4
		DataParm.setData("P4539", opeParm.getData("AST_DR14"));// 180 ����4
		DataParm.setData("P4540", opeParm.getData("AST_DR24"));// 181 ����4
		DataParm.setData("P4541", opeParm.getData("ANA_WAY4"));// 182 ����ʽ4
		DataParm.setData("P4984", opeParm.getData("ANA_LEVEL4"));// 183 ����ּ�4
		DataParm.setData("P4542", opeParm.getData("HEALTH_LEVEL4"));// 184
		// �п����ϵȼ�4
		DataParm.setData("P4543", opeParm.getData("ANA_DR4"));// 185 ����ҽʦ4
		DataParm.setData("P4544", opeParm.getData("OP_CODE5"));// 186 ������������5
		DataParm.setData("P4545",
				opeParm.getValue("OP_DATE5").equals("") ? null : opeParm
						.getTimestamp("OP_DATE5"));// 187 ������������5
		DataParm.setData("P824", opeParm.getData("OP_LEVEL5"));// 188 ��������5
		DataParm.setData("P4546", opeParm.getValue("OP_DESC5"));// 189 ������������5
		DataParm.setData("P4547", opeParm.getData("OPE_SITE5"));// 190 ����������λ5
		DataParm.setData("P4548", opeParm.getData("OP_TIME5"));// 191 ��������ʱ��5
		DataParm.setData("P4549", opeParm.getData("MAIN_SUGEON5"));// 192 ����5
		DataParm.setData("P4550", opeParm.getData("AST_DR15"));// 193 ����5
		DataParm.setData("P4551", opeParm.getData("AST_DR25"));// 194 ����5
		DataParm.setData("P4552", opeParm.getData("ANA_WAY5"));// 195 ����ʽ5
		DataParm.setData("P4985", opeParm.getData("ANA_LEVEL5"));// 196 ����ּ�5
		DataParm.setData("P4553", opeParm.getData("HEALTH_LEVEL5"));// 197
		// �п����ϵȼ�5
		DataParm.setData("P4554", opeParm.getData("ANA_DR5"));// 198 ����ҽʦ5
		DataParm.setData("P45002", opeParm.getData("OP_CODE6"));// 199 ������������6
		DataParm.setData("P45003",
				opeParm.getValue("OP_DATE6").equals("") ? null : opeParm
						.getTimestamp("OP_DATE6"));// 200 ������������6
		DataParm.setData("P825", opeParm.getData("OP_LEVEL6"));// 201 ��������6
		DataParm.setData("P45004", opeParm.getValue("OP_DESC6"));// 202 ������������6
		DataParm.setData("P45005", opeParm.getData("OPE_SITE6"));// 203 ����������λ6
		DataParm.setData("P45006", opeParm.getData("OP_TIME6"));// 204 ��������ʱ��6
		DataParm.setData("P45007", opeParm.getData("MAIN_SUGEON6"));// 205 ����6
		DataParm.setData("P45008", opeParm.getData("AST_DR16"));// 206 ����6
		DataParm.setData("P45009", opeParm.getData("AST_DR26"));// 207 ����6
		DataParm.setData("P45010", opeParm.getData("ANA_WAY6"));// 208 ����ʽ6
		DataParm.setData("P45011", opeParm.getData("ANA_LEVEL6"));// 209 ����ּ�6
		DataParm.setData("P45012", opeParm.getData("HEALTH_LEVEL6"));// 210
		// �п����ϵȼ�6
		DataParm.setData("P45013", opeParm.getData("ANA_DR6"));// 211 ����ҽʦ6
		DataParm.setData("P45014", opeParm.getData("OP_CODE7"));// 212 ������������7
		DataParm.setData("P45015",
				opeParm.getValue("OP_DATE7").equals("") ? null : opeParm
						.getTimestamp("OP_DATE7"));// 213 ������������7
		DataParm.setData("P826", opeParm.getData("OP_LEVEL7"));// 214 ��������7
		DataParm.setData("P45016", opeParm.getValue("OP_DESC7"));// 215 ������������7
		DataParm.setData("P45017", opeParm.getData("OPE_SITE7"));// 216 ����������λ7
		DataParm.setData("P45018", opeParm.getData("OP_TIME7"));// 217 ��������ʱ��7
		DataParm.setData("P45019", opeParm.getData("MAIN_SUGEON7"));// 218 ����7
		DataParm.setData("P45020", opeParm.getData("AST_DR17"));// 219 ����7
		DataParm.setData("P45021", opeParm.getData("AST_DR27"));// 220 ����7
		DataParm.setData("P45022", opeParm.getData("ANA_WAY7"));// 221 ����ʽ7
		DataParm.setData("P45023", opeParm.getData("ANA_LEVEL7"));// 222 ����ּ�7
		DataParm.setData("P45024", opeParm.getData("HEALTH_LEVEL7"));// 223
		// �п����ϵȼ�7
		DataParm.setData("P45025", opeParm.getData("ANA_DR7"));// 224 ����ҽʦ7
		DataParm.setData("P45026", opeParm.getData("OP_CODE8"));// 225 ������������8
		DataParm.setData("P45027",
				opeParm.getValue("OP_DATE8").equals("") ? null : opeParm
						.getTimestamp("OP_DATE8"));// 226 ������������8
		DataParm.setData("P827", opeParm.getData("OP_LEVEL8"));// 227 ��������8
		DataParm.setData("P45028", opeParm.getValue("OP_DESC8"));// 228 ������������8
		DataParm.setData("P45029", opeParm.getData("OPE_SITE8"));// 229 ����������λ8
		DataParm.setData("P45030", opeParm.getData("OP_TIME8"));// 230 ��������ʱ��8
		DataParm.setData("P45031", opeParm.getData("MAIN_SUGEON8"));// 231 ����8
		DataParm.setData("P45032", opeParm.getData("AST_DR18"));// 232 ����8
		DataParm.setData("P45033", opeParm.getData("AST_DR28"));// 233 ����8
		DataParm.setData("P45034", opeParm.getData("ANA_WAY8"));// 234 ����ʽ8
		DataParm.setData("P45035", opeParm.getData("ANA_LEVEL8"));// 235 ����ּ�8
		DataParm.setData("P45036", opeParm.getData("HEALTH_LEVEL8"));// 236
		// �п����ϵȼ�8
		DataParm.setData("P45037", opeParm.getData("ANA_DR8"));// 237 ����ҽʦ8
		DataParm.setData("P45038", opeParm.getData("OP_CODE9"));// 238 ������������9
		DataParm.setData("P45039",
				opeParm.getValue("OP_DATE9").equals("") ? null : opeParm
						.getTimestamp("OP_DATE9"));// 239 ������������9
		DataParm.setData("P828", opeParm.getData("OP_LEVEL9"));// 240 ��������9
		DataParm.setData("P45040", opeParm.getValue("OP_DESC9"));// 241 ������������9
		DataParm.setData("P45041", opeParm.getData("OPE_SITE9"));// 242 ����������λ9
		DataParm.setData("P45042", opeParm.getData("OP_TIME9"));// 243 ��������ʱ��9
		DataParm.setData("P45043", opeParm.getData("MAIN_SUGEON9"));// 244 ����9
		DataParm.setData("P45044", opeParm.getData("AST_DR19"));// 245 ����9
		DataParm.setData("P45045", opeParm.getData("AST_DR29"));// 246 ����9
		DataParm.setData("P45046", opeParm.getData("ANA_WAY9"));// 247 ����ʽ9
		DataParm.setData("P45047", opeParm.getData("ANA_LEVEL9"));// 248 ����ּ�9
		DataParm.setData("P45048", opeParm.getData("HEALTH_LEVEL9"));// 249
		// �п����ϵȼ�9
		DataParm.setData("P45049", opeParm.getData("ANA_DR9"));// 250 ����ҽʦ9
		DataParm.setData("P45050", opeParm.getData("OP_CODE10"));// 251 ������������10
		DataParm.setData("P45051",
				opeParm.getValue("OP_DATE10").equals("") ? null : opeParm
						.getTimestamp("OP_DATE10"));// 252 ������������10
		DataParm.setData("P829", opeParm.getData("OP_LEVEL10"));// 253 ��������10
		DataParm.setData("P45052", opeParm.getValue("OP_DESC10"));// 254
		// ������������10
		DataParm.setData("P45053", opeParm.getData("OPE_SITE10"));// 255
		// ����������λ10
		DataParm.setData("P45054", opeParm.getData("OP_TIME10"));// 256 ��������ʱ��10
		DataParm.setData("P45055", opeParm.getData("MAIN_SUGEON10"));// 257 ����10
		DataParm.setData("P45056", opeParm.getData("AST_DR110"));// 258 ����10
		DataParm.setData("P45057", opeParm.getData("AST_DR210"));// 259 ����10
		DataParm.setData("P45058", opeParm.getData("ANA_WAY10"));// 260 ����ʽ10
		DataParm.setData("P45059", opeParm.getData("ANA_LEVEL10"));// 261 ����ּ�10
		DataParm.setData("P45060", opeParm.getData("HEALTH_LEVEL10"));// 262
		// �п����ϵȼ�10
		DataParm.setData("P45061", opeParm.getData("ANA_DR10"));// 263 ����ҽʦ10

		DataParm.setData("P561", reCordParm.getValue("SPENURS_DAYS", 0).equals(
				"") ? "" : reCordParm.getData("SPENURS_DAYS", 0));// 264 �ؼ���������
		DataParm.setData("P562", reCordParm.getValue("FIRNURS_DAYS", 0).equals(
				"") ? "" : reCordParm.getData("FIRNURS_DAYS", 0));// 265 �ؼ���������
		DataParm.setData("P563", reCordParm.getValue("SECNURS_DAYS", 0).equals(
				"") ? "" : reCordParm.getData("SECNURS_DAYS", 0));// 266 �ؼ���������
		DataParm.setData("P564", reCordParm.getValue("THRNURS_DAYS", 0).equals(
				"") ? "" : reCordParm.getData("THRNURS_DAYS", 0));// 267 �ؼ���������
		DataParm.setData("P6911", reCordParm.getValue("ICU_ROOM1", 0)
				.equals("") ? "" : getReCode("SYS_DEPT", "", "ICU_TYPE",
				"DEPT_CODE", reCordParm.getValue("ICU_ROOM1", 0)));// 268
		// ��֢�໤������1
		DataParm.setData("P6912", reCordParm.getValue("ICU_IN_DATE1", 0)
						.equals("") ? null : reCordParm.getTimestamp(
						"ICU_IN_DATE1", 0));// 269 ����ʱ��1
		DataParm.setData("P6913", reCordParm.getValue("ICU_OUT_DATE1", 0)
				.equals("") ? null : reCordParm
				.getTimestamp("ICU_OUT_DATE1", 0));// 270 �˳�ʱ��1
		DataParm.setData("P6914", reCordParm.getValue("ICU_ROOM2", 0)
				.equals("") ? "" : getReCode("SYS_DEPT", "", "ICU_TYPE",
				"DEPT_CODE", reCordParm.getValue("ICU_ROOM2", 0)));// 271
		// ��֢�໤������2
		DataParm.setData("P6915", reCordParm.getValue("ICU_IN_DATE2", 0)
						.equals("") ? null : reCordParm.getTimestamp(
						"ICU_IN_DATE2", 0));// 272 ����ʱ��2
		DataParm.setData("P6916", reCordParm.getValue("ICU_OUT_DATE2", 0)
				.equals("") ? null : reCordParm
				.getTimestamp("ICU_OUT_DATE2", 0));// 273 �˳�ʱ��2
		DataParm.setData("P6917", reCordParm.getValue("ICU_ROOM3", 0)
				.equals("") ? "" : getReCode("SYS_DEPT", "", "ICU_TYPE",
				"DEPT_CODE", reCordParm.getValue("ICU_ROOM3", 0)));// 274
		// ��֢�໤������3
		DataParm.setData("P6918", reCordParm.getValue("ICU_IN_DATE3", 0)
						.equals("") ? null : reCordParm.getTimestamp(
						"ICU_IN_DATE3", 0));// 275 ����ʱ��3
		DataParm.setData("P6919", reCordParm.getValue("ICU_OUT_DATE3", 0)
				.equals("") ? null : reCordParm
				.getTimestamp("ICU_OUT_DATE3", 0));// 276 �˳�ʱ��3
		DataParm.setData("P6920", reCordParm.getValue("ICU_ROOM4", 0)
				.equals("") ? "" : getReCode("SYS_DEPT", "", "ICU_TYPE",
				"DEPT_CODE", reCordParm.getValue("ICU_ROOM4", 0)));// 277
		// ��֢�໤������4
		DataParm
				.setData("P6921", reCordParm.getValue("ICU_IN_DATE4", 0)
						.equals("") ? null : reCordParm.getTimestamp(
						"ICU_IN_DATE4", 0));// 278 ����ʱ��4
		DataParm.setData("P6922", reCordParm.getValue("ICU_OUT_DATE4", 0)
				.equals("") ? null : reCordParm
				.getTimestamp("ICU_OUT_DATE4", 0));// 279 �˳�ʱ��4
		DataParm.setData("P6923", reCordParm.getValue("ICU_ROOM5", 0)
				.equals("") ? "" : getReCode("SYS_DEPT", "", "ICU_TYPE",
				"DEPT_CODE", reCordParm.getValue("ICU_ROOM5", 0)));// 280
		// ��֢�໤������5
		DataParm
				.setData("P6924", reCordParm.getValue("ICU_IN_DATE5", 0)
						.equals("") ? null : reCordParm.getTimestamp(
						"ICU_IN_DATE5", 0));// 281 ����ʱ��5
		DataParm.setData("P6925", reCordParm.getValue("ICU_OUT_DATE5", 0)
				.equals("") ? null : reCordParm
				.getTimestamp("ICU_OUT_DATE5", 0));// 282 �˳�ʱ��5

		DataParm.setData("P57",
				reCordParm.getValue("BODY_CHECK", 0).equals("") ? ""
						: reCordParm.getData("BODY_CHECK", 0));// 283 ��������ʬ��
		DataParm.setData("P58",
				reCordParm.getValue("FIRST_CASE", 0).equals("") ? ""
						: reCordParm.getData("FIRST_CASE", 0));// 284 ���������ơ���顢��
		// ��Ϊ��Ժ��һ��
		DataParm.setData("P581", reCordParm.getValue("OPE_TYPE_CODE", 0)
				.equals("") ? "" : reCordParm.getData("OPE_TYPE_CODE", 0));// 285
		// ������������
		String flg = reCordParm.getValue("ACCOMP_DATE", 0).equals("") ? "1"
				: "2";
		DataParm.setData("P60", flg);// 286 ����
		DataParm.setData("P611", reCordParm.getValue("ACCOMPANY_WEEK", 0)
				.equals("") ? "" : reCordParm.getData("ACCOMPANY_WEEK", 0));// 287
		// ��������
		DataParm.setData("P612", reCordParm.getValue("ACCOMPANY_MONTH", 0)
				.equals("") ? "" : reCordParm.getData("ACCOMPANY_MONTH", 0));// 288
		// ��������
		DataParm.setData("P613", reCordParm.getValue("ACCOMPANY_YEAR", 0)
				.length() < 2 ? "" : reCordParm.getValue("ACCOMPANY_YEAR", 0)
				.substring(0, 2)); // 289
		// ��������
		DataParm.setData("P59",
				reCordParm.getValue("SAMPLE_FLG", 0).equals("") ? ""
						: reCordParm.getData("SAMPLE_FLG", 0));// 290 ʾ�̲���
		DataParm.setData("P62",
				reCordParm.getValue("BLOOD_TYPE", 0).equals("") ? ""
						: reCordParm.getData("BLOOD_TYPE", 0));// 291 ABOѪ��
		DataParm.setData("P63",
				reCordParm.getValue("RH_TYPE", 0).equals("") ? "" : reCordParm
						.getData("RH_TYPE", 0));// 292 RhѪ��
		DataParm.setData("P64", reCordParm.getValue("TRANS_REACTION", 0)
				.equals("") ? "" : reCordParm.getData("TRANS_REACTION", 0));// 293
		// ��Ѫ��Ӧ
		DataParm.setData("P651", reCordParm.getValue("RBC", 0).equals("") ? ""
				: reCordParm.getData("RBC", 0));// 294 ��ϸ��
		DataParm.setData("P652",
				reCordParm.getValue("PLATE", 0).equals("") ? "" : reCordParm
						.getData("PLATE", 0));// 295 ѪС��
		DataParm.setData("P653",
				reCordParm.getValue("PLASMA", 0).equals("") ? "" : reCordParm
						.getData("PLASMA", 0));// 296 ABOѪ��
		DataParm.setData("P654", reCordParm.getValue("WHOLE_BLOOD", 0).equals(
				"") ? "" : reCordParm.getData("WHOLE_BLOOD", 0));// 297 ȫѪ
		DataParm.setData("P655", reCordParm.getValue("BANKED_BLOOD", 0).equals(
				"") ? "" : reCordParm.getData("BANKED_BLOOD", 0));// 298 �������
		DataParm.setData("P656",
				reCordParm.getValue("OTH_BLOOD", 0).equals("") ? ""
						: reCordParm.getData("OTH_BLOOD", 0));// 299 ��ϸ��
		double nsAge = StringTool.round(((double) NbAge) / 30, 2);
		DataParm.setData("P66", nsAge > 0 ? nsAge : "");// 300��Ӥ�׶�������
		DataParm.setData("P681",
				reCordParm.getValue("NB_WEIGHT", 0).equals("") ? ""
						: reCordParm.getInt("NB_WEIGHT", 0));// 301 ��������������1
		DataParm.setData("P682", reCordParm.getValue("NB_WEIGHT2", 0)
				.equals("") ? "" : reCordParm.getData("NB_WEIGHT2", 0));// 302
		// ��������������2
		DataParm.setData("P683", reCordParm.getValue("NB_WEIGHT3", 0)
				.equals("") ? "" : reCordParm.getData("NB_WEIGHT3", 0));// 303
		// ��������������3
		DataParm.setData("P684", reCordParm.getValue("NB_WEIGHT4", 0)
				.equals("") ? "" : reCordParm.getData("NB_WEIGHT4", 0));// 304
		// ��������������4
		DataParm.setData("P685", reCordParm.getValue("NB_WEIGHT5", 0)
				.equals("") ? "" : reCordParm.getData("NB_WEIGHT5", 0));// 305
		// ��������������5
		DataParm.setData("P67", reCordParm.getValue("NB_ADM_WEIGHT", 0).equals(
				"") ? "" : reCordParm.getData("NB_ADM_WEIGHT", 0));// 306
		// ��������Ժ����

		String beComaTime = reCordParm.getValue("BE_COMA_TIME", 0);
		String afComaTime = reCordParm.getValue("AF_COMA_TIME", 0);
		String p731 = "";
		String p732 = "";
		String p733 = "";
		String p734 = "";
		if (!beComaTime.equals("")) {
			p731 = beComaTime.substring(2, 4).startsWith("0") ? beComaTime
					.substring(3, 4) : beComaTime.substring(2, 4);// �м䲿��
			p732 = beComaTime.substring(4, 6).startsWith("0") ? beComaTime
					.substring(5, 6) : beComaTime.substring(4, 6);// ���沿��
		}
		if (!afComaTime.equals("")) {
			p733 = afComaTime.substring(2, 4).startsWith("0") ? afComaTime
					.substring(3, 4) : afComaTime.substring(2, 4);// �м䲿��
			p734 = afComaTime.substring(4, 6).startsWith("0") ? afComaTime
					.substring(5, 6) : afComaTime.substring(4, 6);// ���沿��
		}
		DataParm.setData("P731", p731.equals("") ? "" : Integer.parseInt(p731));// 307
		// ��Ժǰ����Сʱ
		DataParm.setData("P732", p732.equals("") ? "" : Integer.parseInt(p732));// 308��Ժǰ���ٷ���
		DataParm.setData("P733", p733.equals("") ? "" : Integer.parseInt(p733));// 309
		// ��Ժ�����Сʱ
		DataParm.setData("P734", p734.equals("") ? "" : Integer.parseInt(p734));// 310
		// ��Ժ����ٷ���
		DataParm.setData("P72",
				reCordParm.getData("VENTI_TIME", 0).equals("") ? "" : Integer
						.parseInt(reCordParm.getValue("VENTI_TIME", 0)));// 311
		// ������ʹ��ʱ��

		String AgnPlanFlg = reCordParm.getValue("AGN_PLAN_FLG", 0);
		if (AgnPlanFlg.equals("Y")) {
			DataParm.setData("P830", "2");
			DataParm.setData("P831", reCordParm.getValue("AGN_PLAN_INTENTION",
					0));
		} else {
			DataParm.setData("P830", "1");// 312 �Ƿ��г�Ժ31������סԺ�ƻ�
			DataParm.setData("P831", "");// 323 ��Ժ31����סԺ�ƻ�Ŀ��
		}
		String outType = reCordParm.getValue("OUT_TYPE", 0);
		DataParm.setData("P741", outType);// 314 ��Ժ��ʽ
		if (outType.equals("2")) {
			DataParm.setData("P742", reCordParm.getValue("TRAN_HOSP", 0)
					.equals("999999") ? reCordParm.getValue("TRAN_HOSP_OTHER",
					0) : getReCode("SYS_TRN_HOSP", "", "HOSP_DESC",
					"HOSP_CODE", reCordParm.getValue("TRAN_HOSP", 0)));
			DataParm.setData("P743", "");
		}
		if (outType.equals("3")) {
			DataParm.setData("P742", "");
			DataParm.setData("P743", reCordParm.getValue("TRAN_HOSP", 0)
					.equals("999999") ? reCordParm.getValue("TRAN_HOSP_OTHER",
					0) : getReCode("SYS_TRN_HOSP", "", "HOSP_DESC",
					"HOSP_CODE", reCordParm.getValue("TRAN_HOSP", 0)));
		} else {
			DataParm.setData("P742", "");// 315 ת��ҽԺ����
			DataParm.setData("P743", "");// 316ת�������������/��������Ժ����
		}
		DecimalFormat df = new DecimalFormat("0.00");
		DataParm.setData("P782",
				reCordParm.getValue("SUM_TOT", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("SUM_TOT", 0)));// 317
		// סԺ�ܷ���
		DataParm.setData("P751",
				reCordParm.getValue("OWN_TOT", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("OWN_TOT", 0)));// 318
		// סԺ�ܷ��������Ը����
		DataParm.setData("P752",
				reCordParm.getValue("CHARGE_01", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_01", 0)));// 319һ��ҽ�Ʒ����
		DataParm.setData("P754",
				reCordParm.getValue("CHARGE_02", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_02", 0)));// 320һ�����Ʋ�����
		DataParm.setData("P755",
				reCordParm.getValue("CHARGE_03", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_03", 0)));// 321
		// �����
		DataParm.setData("P756",
				reCordParm.getValue("CHARGE_04", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_04", 0)));// 322�ۺ�ҽ�Ʒ�������������
		DataParm.setData("P757",
				reCordParm.getValue("CHARGE_05", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_05", 0)));// 323������Ϸ�
		DataParm.setData("P758",
				reCordParm.getValue("CHARGE_06", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_06", 0)));// 324ʵ������Ϸ�
		DataParm.setData("P759",
				reCordParm.getValue("CHARGE_07", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_07", 0)));// 325Ӱ��ѧ��Ϸ�
		DataParm.setData("P760",
				reCordParm.getValue("CHARGE_08", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_08", 0)));// 326�ٴ������Ŀ��
		DataParm.setData("P761",
				(reCordParm.getValue("CHARGE_09", 0).equals("") && reCordParm
						.getData("CHARGE_10", 0).equals("")) ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_09", 0)
								+ reCordParm.getDouble("CHARGE_10", 0)));// 327������������Ŀ��
		DataParm.setData("P762",
				reCordParm.getValue("CHARGE_09", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_09", 0)));// 328�ٴ��������Ʒ�
		DataParm.setData("P763", (reCordParm.getValue("CHARGE_11", 0)
				.equals("")
				&& reCordParm.getValue("CHARGE_12", 0).equals("") && reCordParm
				.getValue("CHARGE_13", 0).equals("")) ? 0.00 : df
				.format(reCordParm.getDouble("CHARGE_11", 0)
						+ reCordParm.getDouble("CHARGE_12", 0)
						+ reCordParm.getDouble("CHARGE_13", 0)));// 329�������Ʒ�
		DataParm.setData("P764",
				reCordParm.getValue("CHARGE_11", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_11", 0)));// 330�����
		DataParm.setData("P765",
				reCordParm.getValue("CHARGE_12", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_12", 0)));// 331������
		DataParm.setData("P767",
				reCordParm.getValue("CHARGE_14", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_14", 0)));// 332������
		DataParm.setData("P768",
				reCordParm.getValue("CHARGE_15", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_15", 0)));// 333��ҽ���Ʒ�
		DataParm.setData("P769",
				(reCordParm.getValue("CHARGE_16", 0).equals("") && reCordParm
						.getValue("CHARGE_17", 0).equals("")) ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_16", 0)
								+ reCordParm.getDouble("CHARGE_17", 0)));// 334��ҩ��
		DataParm.setData("P770",
				reCordParm.getValue("CHARGE_16", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_16", 0)));// 335����ҩ�����
		DataParm.setData("P771",
				reCordParm.getValue("CHARGE_18", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_18", 0)));// 336�г�ҩ��
		DataParm.setData("P772",
				reCordParm.getValue("CHARGE_19", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_19", 0)));// 337�в�ҩ��
		DataParm.setData("P773",
				reCordParm.getValue("CHARGE_20", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_20", 0)));// 338Ѫ��
		DataParm.setData("P774",
				reCordParm.getValue("CHARGE_21", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_21", 0)));// 339�׵�������Ʒ��
		DataParm.setData("P775",
				reCordParm.getValue("CHARGE_22", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_22", 0)));// 340�򵰰�����Ʒ��
		DataParm.setData("P776",
				reCordParm.getValue("CHARGE_23", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_23", 0)));// 341��Ѫ��������Ʒ��
		DataParm.setData("P777",
				reCordParm.getValue("CHARGE_24", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_24", 0)));// 342ϸ����������Ʒ��
		DataParm.setData("P778",
				reCordParm.getValue("CHARGE_25", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_25", 0)));// 343�����һ����ҽ�ò��Ϸ�
		DataParm.setData("P779",
				reCordParm.getValue("CHARGE_26", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_26", 0)));// 344������һ����ҽ�ò��Ϸ�
		DataParm.setData("P780",
				reCordParm.getValue("CHARGE_27", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_27", 0)));// 345������һ����ҽ�ò��Ϸ�
		DataParm.setData("P781",
				reCordParm.getValue("CHARGE_28", 0).equals("") ? 0.00 : df
						.format(reCordParm.getDouble("CHARGE_28", 0)));// 346������
		return DataParm;
	}

	private String filterICD;

	/**
	 * ��ȡ�������
	 * 
	 * @param ICD
	 *            String �����
	 * @return String
	 */
	private String getICD_DESC(String ICD) {
		filterICD = ICD;
		ICD_DATA.filterObject(this, "filterICD");
		return ICD_DATA.getItemString(0, "ICD_CHN_DESC");
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
	public boolean filterICD(TParm parm, int row) {
		return filterICD.equals(parm.getValue("ICD_CODE", row));
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
	public boolean filterDictionary(TParm parm, int row) {
		return DictionaryGroup.equals(parm.getValue("GROUP_ID", row))
				&& DictionaryID.equals(parm.getValue("ID", row));
	}

	/**
	 * �滻����
	 * 
	 * @param TableName
	 *            String ����
	 * @param groupID
	 *            String ����
	 * @param StaCode
	 *            String ��Ӧ����
	 * @param codeColunm
	 *            String code����
	 * @param code
	 *            String ����
	 * @return String
	 */
	public String getReCode(String TableName, String groupID, String StaCode,
			String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + StaCode + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0 && !groupID.equals("")) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (StaCode.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code + "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code + "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(StaCode, 0);
	}

	/**
	 * �õ�MroReCord����
	 * 
	 * @return
	 */
	public TParm getMroReCordData(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String sql = "SELECT *  FROM MRO_RECORD WHERE CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �õ�MRO_RECORD_DIAG����
	 * 
	 * @return
	 */
	public TParm getMroDiagData(TParm parm, String IoType, String MainFlg) {
		String caseNo = parm.getValue("CASE_NO");
		TParm Reparm = new TParm();
		Reparm.setData("ICD_CODE", "");
		Reparm.setData("ICD_DESC", "");
		Reparm.setData("IN_PAT_CONDITION", "");
		Reparm.setData("ICD_STATUS", "");
		String sql = "SELECT *  FROM MRO_RECORD_DIAG WHERE CASE_NO='" + caseNo
				+ "'";
		if (!IoType.equals("")) {
			sql += " AND IO_TYPE='" + IoType + "'";
		}
		if (!MainFlg.equals("")) {
			sql += " AND MAIN_FLG='" + MainFlg + "'";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			System.out.println("�õ�MRO_RECORD_DIAG��������ݴ���sql:" + sql);
			result.setErrCode(-104);
			result.setErrText("��������ݲ�ѯ����");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return Reparm;
		}
		if (result.getCount() > 0) {
			Reparm.setData("ICD_CODE", result.getValue("ICD_CODE", 0));
			Reparm.setData("ICD_DESC", result.getValue("ICD_DESC", 0));
			Reparm.setData("IN_PAT_CONDITION", result.getValue(
					"IN_PAT_CONDITION", 0));
			Reparm.setData("ICD_STATUS", result.getData("ICD_STATUS", 0)
					.equals("") ? "" : getReCode("SYS_DICTIONARY",
					"ADM_RETURN", "STA1_CODE", "ID", result.getValue(
							"ICD_STATUS", 0)));
		}
		return Reparm;
	}

	/**
	 * �õ�MRO_RECORD_DIAG�����������
	 * 
	 * @param parm
	 * @param IoType
	 * @param MainFlg
	 * @return
	 */
	public TParm getMroOthDiagData(TParm parm, String IoType, String MainFlg) {
		String caseNo = parm.getValue("CASE_NO");
		String sql = "SELECT *  FROM MRO_RECORD_DIAG WHERE CASE_NO='" + caseNo
				+ "'";
		if (!IoType.equals("")) {
			sql += " AND IO_TYPE='" + IoType + "'";
		}
		if (!MainFlg.equals("")) {
			sql += " AND MAIN_FLG='" + MainFlg + "'";
		}
		int index = 1;
		TParm reParm = new TParm();
		for (int i = 0; i < 10; i++) {
			reParm.setData("DIAG_CODE" + index, ""); // ��ϼ�������
			reParm.setData("DIAG" + index, ""); // ��ϼ�������
			reParm.setData("IN_PAT_CONDITION" + index, ""); // ��ϼ�������
			reParm.setData("ICD_STATUS" + index, ""); // �����Ժ����
			index++;
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			System.out.println("�õ�MRO_RECORD_DIAG����������ݴ���sql:" + sql);
			result.setErrCode(-104);
			result.setErrText("����������ݲ�ѯ����");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return reParm;
		}
		if (result.getCount() <= 0) {
			return reParm;
		}
		int Tindex = 1;
		for (int i = 0; i < result.getCount(); i++) {
			reParm.setData("DIAG_CODE" + Tindex, result.getData("ICD_CODE", i)); // ��ϼ�������
			reParm.setData("DIAG" + Tindex, result.getData("ICD_DESC", i)); // ��ϼ�������
			reParm.setData("IN_PAT_CONDITION" + Tindex, result.getData(
					"IN_PAT_CONDITION", i)); // ��ϼ�������
			reParm.setData("ICD_STATUS" + Tindex, result.getData("ICD_STATUS",
					i).equals("") ? "" : getReCode("SYS_DICTIONARY",
					"ADM_RETURN", "STA1_CODE", "ID", result.getValue(
							"ICD_STATUS", i))); // �����Ժ����
			Tindex++;
		}
		return reParm;
	}

	/**
	 * �õ�MRO_RECORD_OP����
	 * 
	 * @return
	 */
	public TParm getMroReCordOpData(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String sql = "SELECT A.OP_CODE,A.OP_DATE,A.OP_LEVEL,A.OP_DESC,"
				+ "A.OPE_SITE,A.OPE_TIME,A.MAIN_SUGEON,A.AST_DR1,A.AST_DR2,"
				+ "A.ANA_WAY,A.ANA_LEVEL,A.HEALTH_LEVEL,A.ANA_DR,B.STA1_CODE,B.STA1_DESC"
				+ " FROM MRO_RECORD_OP A,SYS_OPERATIONICD B WHERE CASE_NO='"
				+ caseNo
				+ "'AND A.OP_CODE=B.OPERATION_ICD ORDER BY OP_DATE ASC";
		int index = 1;
		TParm reParm = new TParm();
		for (int i = 0; i < 10; i++) {
			reParm.setData("OP_CODE" + index, ""); // ������������
			reParm.setData("OP_DATE" + index, null); // ������������
			reParm.setData("OP_LEVEL" + index, ""); // ��������
			reParm.setData("OP_DESC" + index, ""); // ������������
			reParm.setData("OPE_SITE" + index, ""); // ����������λ
			reParm.setData("OP_TIME" + index, null); // ��������ʱ��
			reParm.setData("MAIN_SUGEON" + index, ""); // ����
			reParm.setData("AST_DR1" + index, ""); // ����
			reParm.setData("AST_DR2" + index, ""); // ����
			reParm.setData("ANA_WAY" + index, ""); // ����ʽ
			reParm.setData("ANA_LEVEL" + index, ""); // ����ּ�
			reParm.setData("HEALTH_LEVEL" + index, ""); // �п����ϵȼ�
			reParm.setData("ANA_DR" + index, ""); // ����ҽʦ
			index++;
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			System.out.println("�õ�MRO_RECORD_OP�������ݴ���sql:" + sql);
			result.setErrCode(-104);
			result.setErrText("�������ݲ�ѯ����");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		int Tindex = 1;
		StringBuffer opestr = new StringBuffer();
		for (int i = 0; i < result.getCount(); i++) {
			if (result.getValue("STA1_CODE", i).equals("")) {
				opestr.append("��������:" + result.getValue("OP_CODE", i)
						+ "�޶�Ӧ���� \r\n");
				continue;
			}
			reParm.setData("OP_CODE" + Tindex, result.getValue("STA1_CODE", i)
					.equals("") ? "NA" : result.getValue("STA1_CODE", i)); // ������������
			reParm.setData("OP_DATE" + Tindex, result
					.getTimestamp("OP_DATE", i));// ������������
			reParm.setData("OP_LEVEL" + Tindex, result.getValue("OP_LEVEL", i)); // ��������
			reParm.setData("OP_DESC" + Tindex, result.getValue("STA1_DESC", i)); // ������������
			reParm.setData("OPE_SITE" + Tindex, result.getValue("OPE_SITE", i));// ����������λ
			reParm.setData("OP_TIME" + Tindex, result.getValue("OPE_TIME", i)); // ��������ʱ��
			reParm.setData("MAIN_SUGEON" + Tindex, result.getValue(
					"MAIN_SUGEON", i).equals("") ? "" : drList.get(result
					.getValue("MAIN_SUGEON", i))); // ����
			reParm.setData("AST_DR1" + Tindex, result.getValue("AST_DR1", i)
					.equals("") ? "" : drList
					.get(result.getValue("AST_DR1", i))); // ����
			reParm.setData("AST_DR2" + Tindex, result.getValue("AST_DR2", i)
					.equals("") ? "" : drList
					.get(result.getValue("AST_DR2", i))); // ����
			reParm.setData("ANA_WAY" + Tindex, result.getValue("ANA_WAY", i)
					.equals("") ? "" : getReCode("SYS_DICTIONARY",
					"OPE_ANAMETHOD", "STA1_CODE", "ID", result.getValue(
							"ANA_WAY", i))); // ����ʽ
			reParm.setData("ANA_LEVEL" + Tindex, result
					.getValue("ANA_LEVEL", i)); // ����ּ�
			reParm.setData("HEALTH_LEVEL" + Tindex, result.getValue(
					"HEALTH_LEVEL", i).equals("") ? "" : getReCode(
					"SYS_DICTIONARY", "MRO_HEALTHLEVEL", "STA1_CODE", "ID",
					result.getValue("HEALTH_LEVEL", i))); // �п����ϵȼ�
			reParm
					.setData("ANA_DR" + Tindex, result.getValue("ANA_DR", i)
							.equals("") ? "" : drList.get(result.getValue(
							"ANA_DR", i))); // ����ҽʦ
			Tindex++;
		}
		if (opestr.toString().length() > 0) {
			reParm.setErrCode(-103);
			reParm.setErrText(opestr.toString());
		}
		return reParm;
	}

	/**
	 * ����STA_MRO_DAILY����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertData(TParm parm, TConnection conn) {
		String sql = this.getsql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			System.out.println("����STA_MRO_DAILY���ݴ���:" + sql);
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private Timestamp getFormateDate(Timestamp a, String b) {
		if (a == null) {
			return a;
		}
		Df = new SimpleDateFormat(b);
		String dfStr = Df.format(a);
		Timestamp redate = StringTool.getTimestamp(dfStr, b);
		return redate;
	}

	/**
	 * �޸�MRO-record���
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm updateSTAFlg(TParm parm, String zipTime, String flg,
			TConnection conn) {
		TParm result = new TParm();
		TParm inparm = new TParm();
		String caseNo = parm.getValue("CASE_NO");
		String nowSql = "";
		if (!zipTime.equals("")) {
			nowSql = ",STA_DATE=TO_DATE('" + zipTime + "','YYYYMMDDHH24MISS') ";
		}
		if (flg == "0") {
			String sqlsel = "SELECT * FROM MRO_RECORD WHERE  CASE_NO='"
					+ caseNo + "'";
			inparm = new TParm(TJDODBTool.getInstance().select(sqlsel));
			if (inparm.getValue("STA_FLG", 0).equals("2"))
				flg = "3";
			String sql = " UPDATE MRO_RECORD SET STA_FLG='" + flg + "'"
					+ nowSql + " WHERE CASE_NO='" + caseNo + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			if (flg.equals("0")) {
				result = this.DeleteData(inparm.getRow(0), conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		} else {
			String sql = " UPDATE MRO_RECORD SET STA_FLG='" + flg + "'"
					+ nowSql + " WHERE CASE_NO='" + caseNo + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * �޸�MRO-record���
	 * 
	 * @param parm
	 *            TParm
	 */
	public TParm updateSTASendFlg(String zipTime, String sendTime, String flg,
			TConnection conn) {
		String nowSql = "";
		TParm parm = new TParm();
		if (!sendTime.equals("")) {
			nowSql = ",STASEND_DATE=TO_DATE('" + sendTime
					+ "','YYYYMMDDHH24MISS') ";
		}
		String sql = " UPDATE MRO_RECORD SET STA_FLG='" + flg + "'" + nowSql
				+ " WHERE STA_DATE=TO_DATE('" + zipTime
				+ "','YYYYMMDDHH24MISS') AND STA_FLG='2'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		String sqlsel = "SELECT * FROM MRO_RECORD WHERE  STA_DATE=TO_DATE('"
				+ zipTime + "','YYYYMMDDHH24MISS') AND STA_FLG='3'";
		parm = new TParm(TJDODBTool.getInstance().select(sqlsel));
		if (parm.getErrCode() < 0) {
			err("ERR:" + parm.getErrCode() + parm.getErrText()
					+ parm.getErrName());
			return parm;
		}
		if (parm.getCount() > 0) {
			String sqlzip = " UPDATE MRO_RECORD SET STA_FLG='0'" + nowSql
					+ " WHERE STA_DATE=TO_DATE('" + zipTime
					+ "','YYYYMMDDHH24MISS') AND STA_FLG='3'";
			result = new TParm(TJDODBTool.getInstance().update(sqlzip, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			result = this.DeleteData(parm.getRow(0), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * sql
	 * 
	 * @param parm
	 * @return
	 */
	public String getsql(TParm parm) {
		StringBuffer str = new StringBuffer();
		str.append("INSERT INTO STA_MRO_DAILY ");
		str.append("(P900, P6891, P686, P800, P1, ");
		str.append("P2, P3, P4, P5, P6, ");
		str.append("P7, P8, P9, P101, P102, ");
		str.append("P103, P11, P12, P13, P801,");
		str.append("P802, P803, P14, P15, P16, ");
		str.append("P17, P171, P18, P19, P20, ");
		str.append("P804, P21, P22, P23, P231, ");
		str.append("P24, P25, P26, P261, P27, ");
		str.append("P28, P281, P29, P30, P301, ");
		str.append("P31, P321, P322, P805, P323, ");
		str.append("P324, P325, P806, P326, P327, ");
		str.append("P328, P807, P329, P3291, P3292, ");
		str.append("P808, P3293, P3294, P3295, P809, ");
		str.append("P3296, P3297, P3298, P810, P3299, ");
		str.append("P3281, P3282, P811, P3283, P3284, ");
		str.append("P3285, P812, P3286, P3287, P3288, ");
		str.append("P813, P3289, P3271, P3272, P814, ");
		str.append("P3273, P3274, P3275, P815, P3276, ");
		str.append("P689, P351, P352, P816, P353, ");
		str.append("P354, P817, P355, P356, P818, ");
		str.append("P361, P362, P363, P364, P365, ");
		str.append("P366, P371, P372, P38, P39,");
		str.append("P40, P411, P412, P413, P414, ");
		str.append("P415, P421, P422, P687, P688, ");
		str.append("P431, P432, P433, P434, P819, ");
		str.append("P435, P436, P437, P438, P44, ");
		str.append("P45, P46, P47, P490, P491, ");
		str.append("P820, P492, P493, P494, P495, ");
		str.append("P496, P497, P498, P4981, P499,");
		str.append("P4910, P4911, P4912, P821, P4913, ");
		str.append("P4914, P4915, P4916, P4917, P4918, ");
		str.append("P4919, P4982, P4920, P4921, P4922, ");
		str.append("P4923, P822, P4924, P4925, P4526, ");
		str.append("P4527, P4528, P4529, P4530, P4983, ");
		str.append("P4531, P4532, P4533, P4534, P823, ");
		str.append("P4535, P4536, P4537, P4538, P4539, ");
		str.append("P4540, P4541, P4984, P4542, P4543, ");
		str.append("P4544, P4545, P824, P4546, P4547, ");
		str.append("P4548, P4549, P4550, P4551, P4552, ");
		str.append("P4985, P4553, P4554, P45002, P45003, ");
		str.append("P825, P45004, P45005, P45006, P45007, ");
		str.append("P45008, P45009, P45010, P45011, P45012, ");
		str.append("P45013, P45014, P45015, P826, P45016, ");
		str.append("P45017, P45018, P45019, P45020, P45021, ");
		str.append("P45022, P45023, P45024, P45025, P45026,");
		str.append("P45027, P827, P45028, P45029, P45030,");
		str.append("P45031, P45032, P45033, P45034, P45035,");
		str.append("P45036, P45037, P45038, P45039, P828, ");
		str.append("P45040, P45041, P45042, P45043, P45044,");
		str.append("P45045, P45046, P45047, P45048, P45049,");
		str.append("P45050, P45051, P829, P45052, P45053,");
		str.append("P45054, P45055, P45056, P45057, P45058,");
		str.append("P45059, P45060, P45061, P561, P562, ");
		str.append("P563, P564, P6911, P6912, P6913,");
		str.append("P6914, P6915, P6916, P6917, P6918,");
		str.append("P6919, P6920, P6921, P6922, P6923,");
		str.append("P6924, P6925, P57, P58, P581,");
		str.append("P60, P611, P612, P613, P59,");
		str.append("P62, P63, P64, P651, P652,");
		str.append("P653, P654, P655, P656, P66,");
		str.append("P681, P682, P683, P684, P685,");
		str.append("P67, P731, P732, P733, P734,");
		str.append("P72, P830, P831, P741, P742,");
		str.append("P743, P782, P751, P752, P754,");
		str.append("P755, P756, P757, P758, P759,");
		str.append("P760, P761, P762, P763, P764,");
		str.append("P765, P767, P768, P769, P770,");
		str.append("P771, P772, P773, P774, P775,");
		str.append("P776, P777, P778, P779, P780,");
		str.append("P781) Values (");
		str.append("'" + parm.getValue("P900") + "', '"
				+ parm.getValue("P6891") + "', '" + parm.getValue("P686")
				+ "', '" + parm.getValue("P800") + "', '" + parm.getValue("P1")
				+ "', ");// 1-5
		str
				.append(parm.getInt("P2") + ", '" + parm.getValue("P3")
						+ "', '" + parm.getValue("P4") + "'," + "'"
						+ parm.getValue("P5") + "',");// 6-9
		str.append(parm.getValue("P6").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')"); // 10
		str.append("," + parm.getInt("P7") + ", '" + parm.getValue("P8")
				+ "', '" + parm.getValue("P9") + "', '" + parm.getValue("P101")
				+ "', '" + parm.getValue("P102") + "',");// 11-15
		str.append("'" + parm.getValue("P103") + "', '" + parm.getValue("P11")
				+ "', '" + parm.getValue("P12") + "', '" + parm.getValue("P13")
				+ "', '" + parm.getData("P801") + "',");// 16-20
		str.append("'" + parm.getValue("P802") + "', '" + parm.getValue("P803")
				+ "', '" + parm.getValue("P14") + "', '" + parm.getValue("P15")
				+ "', '" + parm.getValue("P16") + "',");// 21-25
		str.append("'" + parm.getValue("P17") + "', '" + parm.getValue("P171")
				+ "', '" + parm.getValue("P18") + "', '" + parm.getValue("P19")
				+ "', '" + parm.getValue("P20") + "',");// 26-30
		str.append("'" + parm.getValue("P804") + "', '" + parm.getValue("P21")
				+ "',");// 31-32
		str.append(parm.getValue("P22").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P22"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 33
		str.append(",'" + parm.getValue("P23") + "', '" + parm.getValue("P231")
				+ "',");// 34-35
		str.append("'" + parm.getValue("P24") + "',");// 36
		str.append(parm.getValue("P25").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P25"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 37
		str.append(",'" + parm.getValue("P26") + "','" + parm.getValue("P261")
				+ "'," + parm.getInt("P27") + ",");// 38-40
		str.append("'" + parm.getValue("P28") + "','" + parm.getValue("P281")
				+ "','" + parm.getValue("P29") + "','" + parm.getValue("P30")
				+ "','" + parm.getValue("P301") + "',");// 41-45
		str.append(parm.getValue("31").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("31"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 46
		str.append(",'" + parm.getValue("P321") + "','" + parm.getValue("P322")
				+ "','" + parm.getValue("P805") + "','" + parm.getValue("P323")
				+ "',");// 47-50
		str.append("'" + parm.getValue("P324") + "','" + parm.getValue("P325")
				+ "','" + parm.getValue("P806") + "','" + parm.getValue("P326")
				+ "','" + parm.getValue("P327") + "',");// 51-55
		str.append("'" + parm.getValue("P328") + "','" + parm.getValue("P807")
				+ "','" + parm.getValue("P329") + "','"
				+ parm.getValue("P3291") + "','" + parm.getValue("P3292")
				+ "',");// 56-60
		str.append("'" + parm.getValue("P808") + "','"
						+ parm.getValue("P3293") + "','"
						+ parm.getValue("P3294") + "','"
						+ parm.getValue("P3295") + "','"
						+ parm.getValue("P809") + "',");// 61-65
		str.append("'" + parm.getValue("P3296") + "','"
				+ parm.getValue("P3297") + "','" + parm.getValue("P3298")
				+ "','" + parm.getValue("P810") + "','"
				+ parm.getValue("P3299") + "',");// 66-70
		str.append("'" + parm.getValue("P3281") + "','"
				+ parm.getValue("P3282") + "','" + parm.getValue("P811")
				+ "','" + parm.getValue("P3283") + "','"
				+ parm.getValue("P3284") + "',");// 71-75
		str.append("'" + parm.getValue("P3285") + "','" + parm.getValue("P812")
				+ "','" + parm.getValue("P3286") + "','"
				+ parm.getValue("P3287") + "','" + parm.getValue("P3288")
				+ "',");// 76-80
		str.append("'" + parm.getValue("P813") + "','"
						+ parm.getValue("P3289") + "','"
						+ parm.getValue("P3271") + "','"
						+ parm.getValue("P3272") + "','"
						+ parm.getValue("P814") + "',");// 81-85
		str.append("'" + parm.getValue("P3273") + "','"
				+ parm.getValue("P3274") + "','" + parm.getValue("P3275")
				+ "','" + parm.getValue("P815") + "','"
				+ parm.getValue("P3276") + "',");// 86-90
		str.append(parm.getInt("P689") + ",'" + parm.getValue("P351") + "','"
				+ parm.getValue("P352") + "','" + parm.getValue("P816") + "','"
				+ parm.getValue("P353") + "',");// 91-95
		str.append("'" + parm.getValue("P354") + "','" + parm.getValue("P817")
				+ "','" + parm.getValue("P355") + "','" + parm.getValue("P356")
				+ "','" + parm.getValue("P818") + "',");// 96-100
		str.append("'" + parm.getValue("P361") + "','" + parm.getValue("P362")
				+ "','" + parm.getValue("P363") + "','" + parm.getValue("P364")
				+ "','" + parm.getValue("P365") + "',");// 101-105
		str.append("'" + parm.getValue("P366") + "','" + parm.getValue("P371")
				+ "','" + parm.getValue("P372") + "','" + parm.getValue("P38")
				+ "','" + parm.getValue("P39") + "',"); // 106-110
		str.append("'" + parm.getValue("P40") + "','" + parm.getValue("P411")
				+ "','" + parm.getValue("P412") + "','" + parm.getValue("P413")
				+ "','" + parm.getValue("P414") + "',");// 111-115
		str.append("'" + parm.getValue("P415") + "'," + parm.getInt("P421")
				+ "," + parm.getInt("P422") + ",'" + parm.getData("P687")
				+ "','" + parm.getData("P688") + "',");// 116-120
		str.append("'" + parm.getValue("P431") + "','" + parm.getValue("P432")
				+ "','" + parm.getValue("P433") + "','" + parm.getValue("P434")
				+ "','" + parm.getValue("P819") + "',");// 121-125
		str.append("'" + parm.getValue("P435") + "','" + parm.getValue("P436")
				+ "','" + parm.getValue("P437") + "','" + parm.getValue("P438")
				+ "','" + parm.getValue("P44") + "',");// 126-130
		str.append("'" + parm.getValue("P45") + "','" + parm.getValue("P46")
				+ "',");// 131-132
		str.append(parm.getValue("P47").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P47"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 133
		str.append(",'" + parm.getValue("P490") + "',");// 134
		str.append(parm.getValue("P491").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P491"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 135
		str.append(",'" + parm.getValue("P820") + "','" + parm.getValue("P492")
				+ "','" + parm.getValue("P493") + "'," + parm.getInt("P494")
				+ ",'" + parm.getValue("P495") + "',");// 136-140
		str.append("'" + parm.getValue("P496") + "','"
						+ parm.getValue("P497") + "','" + parm.getValue("P498")
						+ "','" + parm.getValue("P4981") + "','"
						+ parm.getValue("P499") + "',"); // 141-145
		str.append("'" + parm.getValue("P4910") + "','"
				+ parm.getValue("P4911") + "',");// 146-147
		str.append(parm.getValue("P4912").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P4912"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 148
		str.append(",'" + parm.getValue("P821") + "','"
				+ parm.getValue("P4913") + "',");// 149-150
		str.append("'" + parm.getValue("P4914") + "'," + parm.getInt("P4915")
				+ ",'" + parm.getValue("P4916") + "','"
				+ parm.getValue("P4917") + "','" + parm.getValue("P4918")
				+ "',");// 150-155
		str.append("'" + parm.getValue("P4919") + "','"
				+ parm.getValue("P4982") + "','" + parm.getValue("P4920")
				+ "','" + parm.getValue("P4921") + "','"
				+ parm.getValue("P4922") + "',");// 156-160
		str.append(parm.getValue("P4923").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P4923"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 161
		str.append(",'" + parm.getValue("P822") + "','"
				+ parm.getValue("P4924") + "','" + parm.getValue("P4925")
				+ "'," + parm.getInt("P4526") + ",");// 162-165
		str.append("'" + parm.getValue("P4527") + "','"
				+ parm.getValue("P4528") + "','" + parm.getValue("P4529")
				+ "','" + parm.getValue("P4530") + "','"
				+ parm.getValue("P4983") + "',");// 166-170
		str.append("'" + parm.getValue("P4531") + "','"
				+ parm.getValue("P4532") + "','" + parm.getValue("P4533")
				+ "',");// 171-173
		str.append(parm.getValue("P4534").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P4534"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 174
		str.append(",'" + parm.getValue("P823") + "',");// 175
		str.append("'" + parm.getValue("P4535") + "','"
				+ parm.getValue("P4536") + "'," + parm.getInt("P4537") + ",'"
				+ parm.getValue("P4538") + "','" + parm.getValue("P4539")
				+ "',");// 176-180
		str.append("'" + parm.getValue("P4540") + "','"
				+ parm.getValue("P4541") + "','" + parm.getValue("P4984")
				+ "','" + parm.getValue("P4542") + "','"
				+ parm.getValue("P4543") + "',");// 181-185
		str.append("'" + parm.getValue("P4544") + "',");// 186
		str.append(parm.getValue("P4545").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P4545"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 187
		str.append(",'" + parm.getValue("P824") + "','"
				+ parm.getValue("P4546") + "','" + parm.getValue("P4547")
				+ "',");// 188-190
		str.append(parm.getInt("P4548") + ",'" + parm.getValue("P4549") + "','"
				+ parm.getValue("P4550") + "','" + parm.getValue("P4551")
				+ "','" + parm.getValue("P4552") + "',");// 191-195
		str.append("'" + parm.getValue("P4985") + "','"
				+ parm.getValue("P4553") + "','" + parm.getValue("P4554")
				+ "','" + parm.getValue("P45002") + "',");// 196-199
		str.append(parm.getValue("P45003").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P45003"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 200
		str.append(",'" + parm.getValue("P825") + "','"
				+ parm.getValue("P45004") + "','" + parm.getValue("P45005")
				+ "'," + parm.getInt("P45006") + ",'" + parm.getValue("P45007")
				+ "',");// 201-205
		str.append("'" + parm.getValue("P45008") + "','"
				+ parm.getValue("P45009") + "','" + parm.getValue("P45010")
				+ "','" + parm.getValue("P45011") + "','"
				+ parm.getValue("P45012") + "',");// 206-210
		str.append("'" + parm.getValue("P45013") + "','"
				+ parm.getValue("P45014") + "',");// 211-212
		str.append(parm.getValue("P45015").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P45015"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 213
		str.append(",'" + parm.getValue("P826") + "','"
				+ parm.getValue("P45016") + "',");// 214-215
		str.append("'" + parm.getValue("P45017") + "'," + parm.getInt("P45018")
				+ ",'" + parm.getValue("P45019") + "','"
				+ parm.getValue("P45020") + "','" + parm.getValue("P45021")
				+ "',");// 216-220
		str.append("'" + parm.getValue("P45022") + "','"
				+ parm.getValue("P45023") + "','" + parm.getValue("P45024")
				+ "','" + parm.getValue("P45025") + "','"
				+ parm.getValue("P45026") + "',"); // 221-225
		str.append(parm.getValue("P45027").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P45027"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 226
		str.append(",'" + parm.getValue("P827") + "','"
				+ parm.getValue("P45028") + "','" + parm.getValue("P45029")
				+ "'," + parm.getInt("P45030") + ","); // 227-230
		str.append("'" + parm.getValue("P45031") + "','"
				+ parm.getValue("P45032") + "','" + parm.getValue("P45033")
				+ "','" + parm.getValue("P45034") + "','"
				+ parm.getValue("P45035") + "',"); // 231-235
		str.append("'" + parm.getValue("P45036") + "','"
				+ parm.getValue("P45037") + "','" + parm.getValue("P45038")
				+ "',");// 236-238
		str.append(parm.getValue("P45039").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P45039"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')" + ",");// 239
		str.append(",'" + parm.getValue("P828") + "',");// 240
		str.append("'" + parm.getValue("P45040") + "','"
				+ parm.getValue("P45041") + "'," + parm.getInt("P45042") + ",'"
				+ parm.getValue("P45043") + "','" + parm.getValue("P45044")
				+ "',");// 241-245
		str.append("'" + parm.getValue("P45045") + "','"
				+ parm.getValue("P45046") + "','" + parm.getValue("P45047")
				+ "','" + parm.getValue("P45048") + "','"
				+ parm.getValue("P45049") + "',");// 246-250
		str.append("'" + parm.getValue("P45050") + "',");// 251
		str.append(parm.getValue("P45051").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P45051"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 252
		str.append(",'" + parm.getValue("P829") + "','"
				+ parm.getValue("P45052") + "','" + parm.getValue("P45053")
				+ "',");// 253-255
		str.append(parm.getInt("P45054") + ",'" + parm.getValue("P45055")
				+ "','" + parm.getValue("P45056") + "','"
				+ parm.getValue("P45057") + "','" + parm.getValue("P45058")
				+ "',"); // 256-260
		str.append("'" + parm.getValue("P45059") + "','"
				+ parm.getValue("P45060") + "','" + parm.getValue("P45061")
				+ "'," + parm.getInt("P561") + "," + parm.getInt("P562") + ",");// 261-265
		str.append(parm.getInt("P563") + "," + parm.getInt("P564") + ",'"
				+ parm.getValue("P6911") + "',");// 266-268
		str.append(parm.getValue("P6912").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6912"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 269
		str.append(",");
		str.append(parm.getValue("P6913").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6913"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 270
		str.append(",'" + parm.getValue("P6914") + "',");// 271
		str.append(parm.getValue("P6915").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6915"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 272
		str.append(",");
		str.append(parm.getValue("P6916").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6916"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 273
		str.append(",'" + parm.getValue("P6917") + "',");// 274
		str.append(parm.getValue("P6918").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6918"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 275
		str.append(",");
		str.append(parm.getValue("P6919").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6919"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 276
		str.append(",'" + parm.getValue("P6920") + "',");// 277
		str.append(parm.getValue("P6921").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6921"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 278
		str.append(",");
		str.append(parm.getValue("P6922").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6922"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 279
		str.append(",'" + parm.getValue("P6923") + "',"); // 280
		str.append(parm.getValue("P6924").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6924"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 281
		str.append(",");
		str.append(parm.getValue("P6925").equals("") ? "NULL" : "TO_DATE('"
				+ StringTool.getString(parm.getTimestamp("P6925"),
						"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')");// 282
		str.append(",'" + parm.getValue("P57") + "','" + parm.getValue("P58")
				+ "','" + parm.getValue("P581") + "',"); // 283-285
		str.append("'" + parm.getValue("P60") + "'," + parm.getInt("P611")
				+ "," + parm.getInt("P612") + "," + parm.getInt("P613") + ",'"
				+ parm.getValue("P59") + "',"); // 286-290
		str.append("'" + parm.getValue("P62") + "','" + parm.getValue("P63")
				+ "','" + parm.getValue("P64") + "'," + parm.getInt("P651")
				+ "," + parm.getInt("P652") + ",");// 291-295
		str.append(parm.getInt("P653") + "," + parm.getInt("P654") + ","
				+ parm.getInt("P655") + "," + parm.getInt("P656") + ","
				+ parm.getDouble("P66") + ","); // 296-300
		str.append(parm.getInt("P681") + "," + parm.getInt("P682") + ","
				+ parm.getInt("P683") + "," + parm.getInt("P684") + ","
				+ parm.getInt("P685") + ","); // 301-305
		str.append(parm.getInt("P67") + "," + parm.getInt("P731") + ","
				+ parm.getInt("P732") + "," + parm.getInt("P733") + ","
				+ parm.getInt("P734") + ",");// 306-310
		str.append(parm.getInt("P72") + ",'" + parm.getValue("P830") + "','"
				+ parm.getValue("P831") + "','" + parm.getValue("P741") + "','"
				+ parm.getValue("P742") + "',");// 311-315
		str.append("'" + parm.getValue("P743") + "',"
				+ StringTool.round(parm.getDouble("P782"), 2) + ","
				+ StringTool.round(parm.getDouble("P751"), 2) + ","
				+ StringTool.round(parm.getDouble("P752"), 2) + ","
				+ StringTool.round(parm.getDouble("P754"), 2) + ","); // 316-320
		str.append(StringTool.round(parm.getDouble("P755"), 2) + ","
				+ StringTool.round(parm.getDouble("P756"), 2) + ","
				+ StringTool.round(parm.getDouble("P757"), 2) + ","
				+ StringTool.round(parm.getDouble("P758"), 2) + ","
				+ StringTool.round(parm.getDouble("P759"), 2) + ",");// 321-325
		str.append(StringTool.round(parm.getDouble("P760"), 2) + ","
				+ StringTool.round(parm.getDouble("P761"), 2) + ","
				+ StringTool.round(parm.getDouble("P762"), 2) + ","
				+ StringTool.round(parm.getDouble("P763"), 2) + ","
				+ StringTool.round(parm.getDouble("P764"), 2) + ",");// 326-330
		str.append(StringTool.round(parm.getDouble("P765"), 2) + ","
				+ StringTool.round(parm.getDouble("P767"), 2) + ","
				+ StringTool.round(parm.getDouble("P768"), 2) + ","
				+ StringTool.round(parm.getDouble("P769"), 2) + ","
				+ StringTool.round(parm.getDouble("P770"), 2) + ",");// 331-335
		str.append(StringTool.round(parm.getDouble("P771"), 2) + ","
				+ StringTool.round(parm.getDouble("P772"), 2) + ","
				+ StringTool.round(parm.getDouble("P773"), 2) + ","
				+ StringTool.round(parm.getDouble("P774"), 2) + ","
				+ StringTool.round(parm.getDouble("P775"), 2) + ",");// 336-340
		str.append(StringTool.round(parm.getDouble("P776"), 2) + ","
				+ StringTool.round(parm.getDouble("P777"), 2) + ","
				+ StringTool.round(parm.getDouble("P778"), 2) + ","
				+ StringTool.round(parm.getDouble("P779"), 2) + ","
				+ StringTool.round(parm.getDouble("P780"), 2) + ",");// 341-345
		str.append(StringTool.round(parm.getDouble("P781"), 2) + ")");// 346
		return str.toString();
	}
}
