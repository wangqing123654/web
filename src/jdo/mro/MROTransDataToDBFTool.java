package jdo.mro;

import java.sql.*;

import javax.swing.JOptionPane;

import java.io.FileWriter;
import java.io.IOException;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: ����DBF��������ҳ�����סԺ
 * </p>
 * 
 * <p>
 * Description: ����DBF��������ҳ�����סԺ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20111101
 * @version 1.0
 */
public class MROTransDataToDBFTool extends TJDOTool {
	DateFormat df;
	DateFormat df1;

	public MROTransDataToDBFTool() {
		df = new SimpleDateFormat("yyyy-MM-dd");
		df1 = new SimpleDateFormat("HH");
	}

	/**
	 * ʵ��
	 */
	public static MROTransDataToDBFTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static MROTransDataToDBFTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROTransDataToDBFTool();
		return instanceObject;
	}

	// ����������Ϣ��
	private String[] MROInsertData = { "baza00", "baza01", "baza02", "baza03",
			"baza04", "baza05", "baza06", "bazaa1", "baza07", "baza08",
			"baza09", "baza10", "bazaa2", "baza11", "bazaa3", "bazaa4",
			"baza12", "baza13", "bazaa5", "baza14", "baza15", "baza16",
			"baza17", "baza18", "baza19", "baza20", "baza21", "bazaba",
			"baza22", "bazabb", "baza23", "bazabc", "baza24", "baza25",
			"baza26", "baza27", "baza28", "baza29", "baza30", "baza31",
			"bazab1", "baza32", "baza33", "baza34", "baza35", "bazab2",
			"bazab3", "bazab4", "baza41", "baza43", "baza44", "bazac1",
			"bazac2", "bazac3", "baza45", "bazac5", "bazac6", "bazac7",
			"bazac8", "bazac9", "bazaca", "bazacb", "bazacc", "baza46",
			"baza47", "baza48", "baza49", "baza50", "baza51", "baza52",
			"baza53", "baza55", "baza56", "baza57", "baza59", "baza58",
			"baza61", "baza62", "baza63", "baza64", "baza65", "baza69",
			"baza70", "baza73", "baza74", "baza75", "baza76", "baza81",
			"baza82", "baza83", "baza84", "baza85", "baza86", "baza87",
			"baza88", "baza89", "baza90", "baza91", "baza92", "baza93",
			"baza94", "baza95", "baza96", "baza97", "baza98", "baza99",
			"bazae2", "bazae3", "bazae4", "bazae5", "bazae6" };
	// ������ҹ�����־�ǼǱ�
	private String[] OPDInsertData = { "TJM2RQ", "TJM2KB", "TJM2001",
			"TJM2002", "TJM2003", "TJM2004", "TJM2005", "TJM2006", "TJM2007",
			"TJM2008", "TJM2009", "TJM2010", "TJM2011", "TJM2012", "TJM2013",
			"TJM2014", "TJM2015", "TJM2016" };
	// סԺ����������־�ǼǱ�
	private String[] ODIInsertData = { "TJZ1RQ", "TJZ1KB", "TJZ1001",
			"TJZ1002", "TJZ1003", "TJZ1004", "TJZ1005", "TJZ1007", "TJZ1008",
			"TJZ1009", "TJZ1010", "TJZ1014", "TJZ1012", "TJZ1013", "TJZ1015",
			"TJZ1016", "TJZ1017", "TJZ1018", "TJZ1019", "TJZ1020" };
	// ��Ժ�������Ϣ��
	private String[] outDiagInfo = { "BAZA01", "BAF102", "BAF103", "BAF104",
			"BAF105", "BAF106", "BAF107", "BAF111" };
	// ��ѯ����SQL
	private String SQL = "SELECT CASE_NO, MR_NO, IPD_NO, PAT_NAME, SEX, "
			+ "BIRTH_DATE, AGE, MARRIGE, OCCUPATION,IN_COUNT, "
			+ "FOLK, NATION, IDNO, CTZ1_CODE,HOMEPLACE_CODE, "
			+ "OFFICE, O_ADDRESS, O_TEL, O_POSTNO, H_ADDRESS, "
			+ "H_TEL, H_POSTNO, CONTACTER, RELATIONSHIP, CONT_ADDRESS, "
			+ "CONT_TEL, IN_DATE, IN_DEPT, IN_STATION, IN_ROOM_NO, "
			+ "TRANS_DEPT,OUT_DATE, OUT_DEPT, OUT_STATION, OUT_ROOM_NO, "
			+ "REAL_STAY_DAYS, OE_DIAG_CODE, IN_CONDITION, IN_DIAG_CODE, CONFIRM_DATE,  "
			+ "OUT_DIAG_CODE1, CODE1_REMARK, CODE1_STATUS, OUT_DIAG_CODE2, CODE2_REMARK, "
			+ "CODE2_STATUS, OUT_DIAG_CODE3, CODE3_REMARK,CODE3_STATUS, OUT_DIAG_CODE4, "
			+ "CODE4_REMARK, CODE4_STATUS, OUT_DIAG_CODE5, CODE5_REMARK, CODE5_STATUS, "
			+ "OUT_DIAG_CODE6,CODE6_REMARK, CODE6_STATUS, INTE_DIAG_CODE, PATHOLOGY_DIAG, "
			+ "PATHOLOGY_REMARK, EX_RSN, ALLEGIC, HBSAG, HCV_AB, "
			+ "HIV_AB,QUYCHK_OI, QUYCHK_INOUT, QUYCHK_OPBFAF, QUYCHK_CLPA, "
			+ "QUYCHK_RAPA, GET_TIMES, SUCCESS_TIMES, DIRECTOR_DR_CODE, PROF_DR_CODE,  "
			+ "ATTEND_DR_CODE, VS_DR_CODE, INDUCATION_DR_CODE, GRADUATE_INTERN_CODE, INTERN_DR_CODE, "
			+ "ENCODER, QUALITY, CTRL_DR, CTRL_NURSE,CTRL_DATE, "
			+ "INFECT_REPORT, OP_CODE, OP_DATE, MAIN_SUGEON, OP_LEVEL, "
			+ "HEAL_LV, DIS_REPORT, BODY_CHECK, FIRST_CASE, ACCOMPANY_WEEK,  "
			+ "ACCOMPANY_MONTH, ACCOMPANY_YEAR, ACCOMP_DATE, SAMPLE_FLG, BLOOD_TYPE, "
			+ "RH_TYPE, TRANS_REACTION, RBC, PLATE, PLASMA,  "
			+ "WHOLE_BLOOD, OTH_BLOOD, STATUS, PG_OWNER, DRPG_OWNER, "
			+ "FNALPG_OWNER, ADMCHK_FLG, DIAGCHK_FLG, BILCHK_FLG, QTYCHK_FLG,  "
			+ "CHARGE_01, CHARGE_02, CHARGE_03, CHARGE_04, CHARGE_05, "
			+ "CHARGE_06, CHARGE_07, CHARGE_08, CHARGE_09, CHARGE_10, "
			+ "CHARGE_11,CHARGE_12, CHARGE_13, CHARGE_14, CHARGE_15, "
			+ "CHARGE_16, CHARGE_17, CHARGE_18, CHARGE_19, CHARGE_20, "
			+ "OPT_USER, OPT_DATE,OPT_TERM,MRO_CHAT_FLG,ADDITIONAL_CODE1, "
			+ "ADDITIONAL_CODE2,ADDITIONAL_CODE3,ADDITIONAL_CODE4,ADDITIONAL_CODE5,ADDITIONAL_CODE6, "
			+ "OE_DIAG_CODE2,OE_DIAG_CODE3,IN_DIAG_CODE2,IN_DIAG_CODE3,INTE_DIAG_STATUS,"
			+ "TEST_EMR,TEACH_EMR,CLNCPATH_CODE,DISEASES_CODE "
			+ "FROM MRO_RECORD ";

	/**
	 * ���ɲ�����ҳDBF�ļ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getMRODBF(TParm parm, TControl contorl) {
		// String TabName=parm.getValue("TabName");

		TParm result = new TParm();
		// if (null == TabName || TabName.equals("")) {
		// contorl.messageBox("������DBF���ݱ�����!!", "��Ϣ", JOptionPane.ERROR_MESSAGE);
		// result.setData("DBF_TYPE","1");//����ʧ��
		// return result;
		// }
		// Connection con1=makeConnection(TabName);
		// if (con1 == null) {
		// contorl.messageBox("dbf����ʧ�ܣ�����������!! \n ��ȷ���趨odbc���ݿ�����Ϊ'" + TabName +
		// "'!!", "��Ϣ", JOptionPane.ERROR_MESSAGE);
		// result.setData("DBF_TYPE", "1");
		// return result;
		// }
		StringBuffer caseNoSum = new StringBuffer();
		caseNoSum.append(" WHERE OUT_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS')");
		// for (int i = 0; i < parm.getCount(); i++) {
		// caseNoSum.append(parm.getValue("CASE_NO", i) + ",");
		// }
		// String caseTemp = caseNoSum.toString().substring(0,
		// caseNoSum.toString().
		// lastIndexOf(",")) + ")";
		parm = new TParm(TJDODBTool.getInstance().select(SQL + caseNoSum));
		if (parm.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		if (null == parm || parm.getCount() <= 0) {
			contorl.messageBox("û����Ҫ�������ݣ������Ժ��ѯ�����Ƿ���ȷ!!");
			result.setData("DBF_TYPE", "1");
			return result;
		}
		// String[] InsertData=new String[100];sys
		String TabName = "c:\\Sqlserver"
				+ StringTool.getString(SystemTool.getInstance().getDate(),
						"yyyyMMdd") + ".txt";
		StringBuffer content = new StringBuffer();
		for (int i = 0; i < MROInsertData.length; i++) {
			content.append(MROInsertData[i] + " ");
		}
		content.append(";");
		for (int i = 0; i < parm.getCount(); i++) {
			result = mroUpdateName(parm.getRow(i));
			for (int j = 0; j < MROInsertData.length; j++) {
				content.append(result.getValue(MROInsertData[j]) + " ");
			}
			content.append(";");
		}
		appendLog(TabName, content.toString());
		contorl.messageBox("DBFת���ɹ�");
		return result;
	}

	/**
	 * ������סԺ������־�Ǽǲ���
	 * 
	 * @param parm
	 *            TParm �����õĲ���
	 * @param stringName
	 *            String ��Ҫͨ�������ֵ��ʾ�����ݣ������סԺ
	 * @param contorl
	 *            TControl
	 * @return TParm
	 */
	public TParm getOPDAndODIDBF(TParm parm, String stringName, TControl contorl) {
		TParm result = new TParm();
		if (null == parm || parm.getCount() <= 0) {
			contorl.messageBox("û����Ҫ�������ݣ���ȷ�Ͻ�����������!!", "��Ϣ",
					JOptionPane.ERROR_MESSAGE);
			result.setData("DBF_TYPE", "1");
			return result;
		}

		String TabName = "c:\\Sqlserver"
				+ stringName
				+ StringTool.getString(SystemTool.getInstance().getDate(),
						"yyyyMMdd") + ".txt";
		StringBuffer content = new StringBuffer();
		if ("OPD".equals(stringName)) {
			for (int i = 0; i < OPDInsertData.length; i++) {
				content.append(OPDInsertData[i] + "\t\t");
			}
			content.append("\n");
			for (int i = 0; i < parm.getCount(); i++) {
				result = opdUpdateName(parm.getRow(i));
				for (int j = 0; j < OPDInsertData.length; j++) {
					content.append(result.getValue(OPDInsertData[j]) + "\t\t");
				}
				content.append("\n");
			}

		} else if ("ODI".equals(stringName)) {
			for (int i = 0; i < ODIInsertData.length; i++) {
				content.append(ODIInsertData[i] + "\t\t");
			}
			content.append("\n");
			for (int i = 0; i < parm.getCount(); i++) {
				result = odiUpdateName(parm.getRow(i));
				for (int j = 0; j < ODIInsertData.length; j++) {
					content.append(result.getValue(ODIInsertData[j]) + "\t\t");
				}
				content.append("\n");
			}

		}

		appendLog(TabName, content.toString());
		contorl.messageBox("DBFת���ɹ�");
		return result;
	}

	// /**
	// * ���ݿ�����
	// * @param tabname String
	// * @return Connection
	// */
	// //connect to database
	// private Connection makeConnection(String tabname) {
	// Connection con = null;
	// try {
	// //con =(Connection)DriverManager.getConnection (url,username,password);
	// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	// //con = DriverManager.getConnection("jdbc:odbc:tjza");
	// con = DriverManager.getConnection("jdbc:odbc:" + tabname);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return con;
	// }
	/**
	 * ������ҳDBF����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm mroUpdateName(TParm parm) {
		TParm result = new TParm();
		String mrNo = parm.getValue("MR_NO");
		mrNo = mrNo.substring(4, mrNo.length());
		result.setData("baza00", StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy")
				+ mrNo
				+ (parm.getInt("IN_COUNT") < 10 ? "0" + parm.getInt("IN_COUNT")
						: parm.getInt("IN_COUNT"))); // ����Ψһ�Ա�־������4λ��Ժ���+10�����ţ������ż�����˵����
		result.setData("baza01", resultName(parm.getValue("MR_NO"))); // ������8λ������(����8λ�á�0����ǰ�油λ)+2λסԺ����(�确01��)
		result.setData("baza02", resultName(parm.getValue("PAT_NAME"))); // ������������д��������8������
		// �����Ա�
		String returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_SEX' AND ID='" + parm.getValue("SEX") + "'",
				"STA1_CODE");
		result.setData("baza03", returnString); // �Ա�
												// ����д���ұ�׼����(0δ֪��1�У�2Ů��3Ů���У�4�б�Ů��9δ˵��)
		result.setData("baza04", null == parm.getTimestamp("BIRTH_DATE") ? ""
				: df.format(parm.getTimestamp("BIRTH_DATE"))); // �������� ����д������
																// 2002-01-01
		result.setData("baza05", resultName(parm.getValue("IDNO"))); // ���֤��
																		// ��д������18λ
		result.setData("baza06",
				resultName(parm.getValue("AGE")).equals("") ? "" : parm
						.getValue("AGE").substring(0,
								parm.getValue("AGE").length() - 1)); // ����
																		// ��д��������25
		result.setData("bazaa1", "Y"); // ���䵥λ ��д����(Y�꣬M�£�D��)
		// ���һ���״��
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_MARRIAGE' AND ID='" + parm.getValue("MARRIGE")
						+ "'", "STA1_CODE");
		result.setData("baza07", returnString.equals("") ? "9" : returnString); // ����״��
																				// ��д���ұ�׼����(1δ�飬2�ѻ飬3ɥż��4��飬9����)
		// ����ְҵ
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_OCCUPATION' AND ID='"
						+ parm.getValue("OCCUPATION") + "'", "STA1_CODE");
		result.setData("baza08", returnString); // ְҵ ��д���ұ�׼����(GB6565-1999)
		// ���ҳ�����
		returnString = sqlReturnParm("SYS_HOMEPLACE", "HOMEPLACE_CODE='"
				+ parm.getValue("HOMEPLACE_CODE") + "'", "STA1_CODE");
		result.setData("baza09", returnString); // ������ ��д���ұ�׼����(GB/T2260-84)
		// ��������
		returnString = sqlReturnParm(
				"SYS_DICTIONARY",
				"GROUP_ID='SYS_SPECIES' AND ID='" + parm.getValue("FOLK") + "'",
				"STA1_CODE");
		result.setData("baza10", returnString); // ���� ��д���ұ�׼����(GB/3304-91)
		// ���ҹ���
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_NATION' AND ID='" + parm.getValue("NATION")
						+ "'", "STA1_CODE");
		result.setData("bazaa2", returnString); // ���� ��д���ұ�׼����
		result.setData("baza11", resultName(parm.getValue("O_TEL"))); // ��λ�绰
																		// ��д���ߵ�λ�绰,������16���ַ�
		result.setData("bazaa3", resultName(parm.getValue("O_ADDRESS"))); // ��λ����
																			// ��д���ߵ�λ����,������20���ַ�
		result.setData("bazaa4", resultName(parm.getValue("O_POSTNO"))); // ��λ�ʱ�
		// ���һ��ڵ�ַ��ʡ���أ�
		returnString = sqlReturnParm("SYS_POSTCODE", "POST_CODE='"
				+ parm.getValue("H_POSTNO") + "'", "STA1_CODE");
		result.setData("baza12", returnString); // ���ڵ�ַ��ʡ���أ���д���ұ�׼����
		result.setData("baza13", resultName(parm.getValue("H_ADDRESS"))); // ���ڵ�ַ���ִ壩
																			// ��д��������ִ�,������15���ַ�
		result.setData("bazaa5", resultName(parm.getValue("H_TEL"))); // �绰
		result.setData("baza14", resultName(parm.getValue("H_POSTNO"))); // ��������
		result.setData("baza15", resultName(parm.getValue("CONTACTER"))); // ��ϵ������
		// ���ҹ�ϵ��д����
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_RELATIONSHIP' AND ID='"
						+ parm.getValue("RELATIONSHIP") + "'", "STA1_CODE");
		result.setData("baza16", returnString); // ��ϵ��д����(1-9),�μ����ұ�׼����
		result.setData("baza17", ""); // ��ϵ��ַ��ʡ���أ�//������-----------����NULL
		result.setData("baza18", resultName(parm.getValue("CONT_ADDRESS"))); // ��ϵ��ַ���ִ壩
		result.setData("baza19", resultName(parm.getValue("CONT_TEL"))); // ��ϵ�绰
		// ������Ժ���
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='ADM_CONDITION' AND ID='"
						+ parm.getValue("IN_CONDITION") + "'", "STA1_CODE");
		result.setData("baza20", returnString); // ��Ժ��� ��д����(1Σ��2����3һ��)
		// ������Ժ�Ʊ����
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("IN_DEPT") + "'", "STA1_CODE");
		result.setData("baza21", returnString); // ��Ժ�Ʊ����
		// ������Ժ��������
		// returnString=sqlReturnParm("SYS_STATION","STATION_CODE='"+parm.getValue("IN_STATION")+"'","STA1_CODE");
		result.setData("bazaba", parm.getValue("IN_STATION")); // ��Ժ��������
		// ����ת��Ʊ����
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("TRANS_DEPT") + "'", "STA1_CODE");
		result.setData("baza22", returnString); // ת��Ʊ����
		// ����ת�벡������
		// returnString=sqlReturnParm("SYS_STATION","STATION_CODE='"+parm.getValue("IN_STATION")+"'","STA1_CODE");
		// returnString=sqlReturnParm("ADM_INP","CASE_NO ='"+parm.getValue("CASE_NO")+"'","OPD_DR_CODE");
		result.setData("bazabb", ""); // ת�벡������-------???������ת�벡��:
		// ���ҳ�Ժ�Ʊ����
		returnString = sqlReturnParm("SYS_DEPT", "DEPT_CODE='"
				+ parm.getValue("OUT_DEPT") + "'", "STA1_CODE");
		result.setData("baza23", returnString); // ��Ժ�Ʊ����
		// ������Ժ��������
		returnString = sqlReturnParm("SYS_STATION", "STATION_CODE='"
				+ parm.getValue("OUT_STATION") + "'", "STA1_CODE");
		result.setData("bazabc", returnString); // ��Ժ��������
		result.setData("baza24", null == parm.getTimestamp("IN_DATE") ? "" : df
				.format(parm.getTimestamp("IN_DATE"))); // ��Ժ����
		result.setData("baza25", null == parm.getTimestamp("IN_DATE") ? ""
				: df1.format(parm.getTimestamp("IN_DATE"))); // ��Ժʱ��:��д�ӵ㣬��10
		// ���ҳ�Ժ��ʽ
		// returnString=sqlReturnParm("SYS_DICTIONARY","GROUP_ID='ADM_RETURN' AND ID='"+parm.getValue("CODE1_STATUS")+"'","STA1_CODE");
		result.setData("baza26", ""); // ��Ժ��ʽ ��д����(1���棬2�Զ���3תԺ)-----��ȷ�� ��������NULL
		result.setData("baza27", null == parm.getTimestamp("OUT_DATE") ? ""
				: df.format(parm.getTimestamp("OUT_DATE"))); // ��Ժ����
		result.setData("baza28", null == parm.getTimestamp("OUT_DATE") ? ""
				: df1.format(parm.getTimestamp("OUT_DATE"))); // ��Ժʱ��
		result.setData("baza29", resultName(parm.getValue("REAL_STAY_DAYS"))); // סԺ����
																				// :��д����0������
		result.setData("baza30", null == parm.getTimestamp("CONFIRM_DATE") ? ""
				: df.format(parm.getTimestamp("CONFIRM_DATE"))); // ȷ������
		int day = 0;
		if (null != parm.getTimestamp("CONFIRM_DATE"))
			day = StringTool.getDateDiffer(parm.getTimestamp("CONFIRM_DATE"),
					parm.getTimestamp("IN_DATE"));
		result.setData("baza31", null == parm.getTimestamp("CONFIRM_DATE") ? ""
				: day); // �ڼ���ȷ��
		// ���ҿ�����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("DIRECTOR_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab1", returnString); // ������ ��д����������
		// ����������������ҽʦ ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("PROF_DR_CODE") + "'", "USER_NAME");
		result.setData("baza32", returnString); // ������������ҽʦ ��д����
		// ��������ҽʦ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("ATTEND_DR_CODE") + "'", "USER_NAME");
		result.setData("baza33", returnString); // ����ҽʦ��д����
		// ����סԺҽʦ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("VS_DR_CODE") + "'", "USER_NAME");
		result.setData("baza34", returnString); // סԺҽʦ��д����
		// ��������ҽʦ��д����
		returnString = sqlReturnParm("ADM_INP", "CASE_NO ='"
				+ parm.getValue("CASE_NO") + "'", "OPD_DR_CODE");
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='" + returnString
				+ "'", "USER_NAME");
		result.setData("baza35", returnString); // ����ҽʦ��д������ȷ��û�ҵ�����ҽ������
		// ���ҽ���ҽʦ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("INDUCATION_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab2", returnString); // ����ҽʦ��д����
		// �����о���ʵϰҽʦ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("GRADUATE_INTERN_CODE") + "'", "USER_NAME");
		result.setData("bazab3", returnString); // �о���ʵϰҽʦ��д����
		// ����ʵϰҽʦ��д����
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("INTERN_DR_CODE") + "'", "USER_NAME");
		result.setData("bazab4", returnString); // ʵϰҽʦ��д����
		// ����ת��
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='ADM_RETURN' AND ID='"
						+ parm.getValue("CODE1_STATUS") + "'", "STA1_CODE");
		result.setData("baza41", returnString); // ת�飨��ҽ����ϣ�����д��Ժ�������(1������2��ת��3δ����4������9����)--���Ժ��ʽ��ͬ����ȷ��
		result.setData("baza43", null == parm.getValue("QUYCHK_OI")
				|| parm.getValue("QUYCHK_OI").length() <= 0 ? "0" : "1"); // �ų���Ϸ��ϱ�־:����д����(0δ����1���ϣ�2�����ϣ�3���϶�)
																			// ----(1
																			// ��
																			// 0��2��3
																			// ��)
		result.setData("baza44", null == parm.getValue("QUYCHK_INOUT")
				|| parm.getValue("QUYCHK_INOUT").length() <= 0 ? "0" : "1"); // �����Ϸ��ϱ�־:����д����(0δ����1���ϣ�2�����ϣ�3���϶�)----(1
																				// ��
																				// 0��2��3
																				// ��)
		result.setData("bazac1", null == parm.getValue("QUYCHK_CLPA")
				|| parm.getValue("QUYCHK_CLPA").length() <= 0 ? "0" : "1"); // �ٴ��벡����ϱ�־
																			// ����д����(0δ����1���ϣ�2�����ϣ�3���϶�)----(1
																			// ��
																			// 0��2��3
																			// ��)
		result.setData("bazac2", null == parm.getValue("QUYCHK_RAPA")
				|| parm.getValue("QUYCHK_RAPA").length() <= 0 ? "0" : "1"); // �����벡����ϱ�־
																			// ����д����(0δ����1���ϣ�2�����ϣ�3���϶�)----(1
																			// ��
																			// 0��2��3
																			// ��)
		result.setData("bazac3", ""); // ����Ϊ��Ժ��һ�� ��д����(1�ǣ�2��)----��ȷ��
		// ����Ѫ����д����
		returnString = sqlReturnParm("SYS_DICTIONARY",
				"GROUP_ID='SYS_BLOOD' AND ID='" + parm.getValue("BLOOD_TYPE")
						+ "'", "STA1_CODE");
		result.setData("baza45", returnString); // Ѫ����д����(0δ֪��1 A��2 B��3 AB��4 O��9
												// ����)
		result.setData("bazac5", null == parm.getValue("RH_TYPE")
				|| parm.getValue("RH_TYPE").equals("3") ? "" : parm
				.getValue("RH_TYPE")); // RH ��д����(1����2��)
		result.setData("bazac6", null == parm.getValue("HBSAG") ? "0" : parm
				.getValue("HBSAG")); // HBsAg ��д����(0δ����1���ԣ�2����)
		result.setData("bazac7", null == parm.getValue("HCV_AB") ? "0" : parm
				.getValue("HCV_AB")); // HCV-Ab ��д����(0δ����1���ԣ�2����)
		result.setData("bazac8", null == parm.getValue("HIV_AB") ? "0" : parm
				.getValue("HIV_AB")); // HIV-Ab ��д����(0δ����1���ԣ�2����)
		result.setData("bazac9", ""); // ��Ժ��������־----��ȷ��
		result.setData("bazaca", ""); // ����Ϊ��Ժ��һ�� ��д����(1�ǣ�2��)----��ȷ��
		result.setData("bazacb", ""); // ���Ϊ��Ժ��һ�� ��д����(1�ǣ�2��)----��ȷ��
		result.setData("bazacc", resultName(parm.getValue("FIRST_CASE"))); // ���Ϊ��Ժ��һ��
																			// ��д����(1�ǣ�2��)
		result.setData("baza46", resultName(parm.getValue("GET_TIMES"))); // ���ȴ���
																			// �������������д��С��0������
		result.setData("baza47", resultName(parm.getValue("SUCCESS_TIMES"))); // �ɹ�����
																				// ͬ���ȴ���
		result.setData("baza48", null == parm.getValue("ACCOMP_DATE")
				|| parm.getValue("ACCOMP_DATE").length() <= 0 ? "2" : "1"); // ����
																			// ��д����(1�ǣ�2��)
		StringBuffer accompDate = new StringBuffer();
		accompDate.append(null == parm.getValue("ACCOMPANY_WEEK")
				|| parm.getValue("ACCOMPANY_WEEK").length() <= 0 ? "" : parm
				.getValue("ACCOMPANY_WEEK")
				+ "W");
		accompDate.append(null == parm.getValue("ACCOMPANY_MONTH")
				|| parm.getValue("ACCOMPANY_MONTH").length() <= 0 ? "" : ","
				+ parm.getValue("ACCOMPANY_MONTH") + "M");
		accompDate.append(null == parm.getValue("ACCOMPANY_YEAR")
				|| parm.getValue("ACCOMPANY_YEAR").length() <= 0 ? "" : ","
				+ parm.getValue("ACCOMPANY_YEAR") + "Y");
		result.setData("baza49", accompDate); // �������� ��д��1W��1�ܣ���1M��1�£���1Y��1�꣩
		result.setData("baza50", resultName(parm.getValue("SAMPLE_FLG"))); // ʾ�̲���
																			// ��д����(1�ǣ�2��)
		result.setData("baza51", resultName(parm.getValue("QUALITY"))); // ��������
																		// ��д����(1�ף�2�ң�3��)
		result.setData("baza52", null == parm.getValue("ALLEGIC")
				|| parm.getValue("ALLEGIC").length() <= 0 ? "2" : "1"); // ҩ�������־
																		// ��д����(1�У�2��)
		result.setData("baza53", null == parm.getValue("OP_CODE")
				|| parm.getValue("OP_CODE").length() <= 0 ? "2" : "1"); // ������־
																		// ��д����(1�У�2��)
		result.setData("baza55", "2"); // ������� ��д����(1�У�2����3����ҽ)----��ȷ��:д��'2'
		// ��Ժ;��
		returnString = sqlReturnParm("ADM_INP", "CASE_NO ='"
				+ parm.getValue("CASE_NO") + "'", "ADM_SOURCE");
		result.setData("baza56", null != returnString ? returnString.substring(
				1, returnString.length()) : returnString); // ��Ժ;��
															// ��д����(1���2���3תԺ)
		result.setData("baza57", null == parm.getValue("TRANS_REACTION") ? "0"
				: parm.getValue("TRANS_REACTION")); // ��Ѫ��Ӧ ��д����(0δ�䣬1�У�2��)
		// ���ұ���Ա
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("ENCODER") + "'", "USER_NAME");
		result.setData("baza59", returnString); // ����Ա ��������
		// ������Һ��Ӧ
		// returnString=sqlReturnParm("PHL_ORDER","CASE_NO ='"+parm.getValue("CASE_NO")+"'","CASE_NO");
		result.setData("baza58", ""); // ��Һ��Ӧ
										// ��д����(0δ�䣬1�У�2��)---����Һ��Ӧ����ȷ��:��������NULL
		result.setData("baza61", null == parm.getValue("PATHOLOGY_DIAG")
				|| parm.getValue("PATHOLOGY_DIAG").length() <= 0 ? "2" : "1"); // �����־
																				// ��д����(1�У�2��)
		result.setData("baza62", ""); // ���в��� ��д����(1�ǣ�2��)--��ȷ��
		result.setData("baza63", ""); // ���ȷ��� ��д����(1�У�2����3����ҽ)---����ȷ��:��������NULL
		result.setData("baza64", parm.getValue("IN_DIAG_CODE")); // ��Ժ����ϣ�ICD_10��
		result.setData("baza65", parm.getValue("OUT_DIAG_CODE1")); // ��Ժ����ϣ�ICD_10)

		// �����ʿػ�ʿ
		returnString = sqlReturnParm("SYS_OPERATOR", "USER_ID='"
				+ parm.getValue("CTRL_NURSE") + "'", "USER_NAME");
		result.setData("baza69", returnString); // �ʿػ�ʿ ��д����
		result.setData("baza70", ""); // ������ҩ�Ƽ� ��д���루0δ֪��1�У�2�ޣ�---��ȷ��:��������NULL
		result.setData("baza71", ""); // ��ҽ��ɫ����
		result.setData("baza72", ""); // �Ǳ�Ҫ���
		result.setData("baza73", ""); // ��Ժǰ����Ժ���� ��д����(1�У�2��)---��ȷ��
		result.setData("baza74", ""); // סԺ�ڼ䲡�飺Σ�� ��д����(1�ǣ�2��)--��ȷ��
		// ����ҽ�Ʊ��պ�
		returnString = sqlReturnParm("SYS_PATINFO", "MR_NO='"
				+ parm.getValue("MR_NO") + "'", "NHI_NO");
		result.setData("baza81", returnString); // ҽ�Ʊ��պ� ��дʵ��ҽ�Ʊ��պ� ����ҽ����
		// ����ҽ�Ƹ��ʽ
		returnString = sqlReturnParm("SYS_CTZ", "CTZ_CODE='"
				+ parm.getValue("CTZ1_CODE") + "'", "STA1_CODE");
		result.setData("baza82", returnString); // ҽ�Ƹ��ʽ ����д���룬�μ�������ҳ˵��

		String bilSql = "SELECT SUM(WRT_OFF_AMT) AS WRT_OFF_AMT,REXP_CODE FROM BIL_IBS_RECPM A,BIL_IBS_RECPD B WHERE A.RECEIPT_NO=B.RECEIPT_NO AND A.CASE_NO='"
				+ parm.getValue("CASE_NO") + "' GROUP BY REXP_CODE";

		TParm bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
		if (bilParm.getErrCode() < 0) {
			return bilParm;
		}
		if (bilParm.getCount("WRT_OFF_AMT") <= 0) {
			bilSql = "SELECT SUM(TOT_AMT) AS WRT_OFF_AMT,REXP_CODE FROM IBS_ORDD WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' GROUP BY REXP_CODE";
			bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
		}
		double sum = parm.getDouble("CHARGE_15") + parm.getDouble("CHARGE_02")
				+ parm.getDouble("CHARGE_01") + parm.getDouble("CHARGE_04")
				+ parm.getDouble("CHARGE_12") + parm.getDouble("CHARGE_13")
				+ parm.getDouble("CHARGE_06") + parm.getDouble("CHARGE_11")
				+ parm.getDouble("CHARGE_18") + parm.getDouble("CHARGE_14")
				+ parm.getDouble("CHARGE_10") + parm.getDouble("CHARGE_05")
				+ parm.getDouble("CHARGE_03") + parm.getDouble("CHARGE_07")
				+ parm.getDouble("CHARGE_08") + parm.getDouble("CHARGE_19")
				+ parm.getDouble("CHARGE_17") + parm.getDouble("CHARGE_16");
		result.setData("baza83", sum); // סԺ�ܷ��� ��дʵ�ʷ��ã�2λС������λ��Ԫ���������·����=�ܷ���
		result.setData("baza84", parm.getDouble("CHARGE_01")); // ��λ��
		result.setData("baza85", parm.getDouble("CHARGE_18")); // �г�ҩ��
		result.setData("baza86", parm.getDouble("CHARGE_17")+parm.getDouble("CHARGE_16")); // ��ҩ��
		result.setData("baza87", parm.getDouble("CHARGE_04")); // ����
		result.setData("baza88", parm.getDouble("CHARGE_12")); // Ѫ��
		result.setData("baza89", parm.getDouble("CHARGE_13")); // ����
		result.setData("baza90", parm.getDouble("CHARGE_06")); // ���Ʒѣ���ҽԺ�ޣ�
		result.setData("baza91", parm.getDouble("CHARGE_11")); // ������
		result.setData("baza92", parm.getDouble("CHARGE_18")); // ������
		result.setData("baza93", parm.getDouble("CHARGE_14")); // ������
		result.setData("baza94", parm.getDouble("CHARGE_10")); // ����� CT����
		result.setData("baza95", parm.getDouble("CHARGE_05")); // �����
		result.setData("baza96", parm.getDouble("CHARGE_03")); // �в�ҩ��
		result.setData("baza97", parm.getDouble("CHARGE_07")); // �����
		result.setData("baza98", parm.getDouble("CHARGE_08")); // ���Ʒ�
		result.setData("baza99", 0.00); // ҽ���ܸ���� ��ȷ��
		result.setData("bazae2", 0.00); // ����� ��ȷ��
		result.setData("bazae3", parm.getDouble("CHARGE_19")); // Ӥ����
		result.setData("bazae4", parm.getDouble("CHARGE_17")); // �㴲�� ���໤��
		result.setData("bazae5", parm.getDouble("CHARGE_16")); // ������1:��ů��
		result.setData("bazae6", 0.00); // ������2
		return result;
	}

	private String resultName(String name) {
		if (null == name)
			return "";
		else
			return name;
	}

	public static void appendLog(String fileName, String content) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileName, true);
			fw.write(content);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}

	}

	/**
	 * ���ݵõ������ݲ�����ر���STA1_CODE ֵ
	 * 
	 * @param tableName
	 *            String ������
	 * @param whereName
	 *            String ����
	 * @param resultName
	 *            String ����������
	 * @return String
	 */
	private String sqlReturnParm(String tableName, String whereName,
			String resultName) {
		StringBuffer sql = new StringBuffer();
		if (null == tableName || tableName.length() <= 0) {
			return null;
		}
		if (null == resultName || resultName.length() <= 0) {
			return null;
		}

		sql.append("SELECT " + resultName + " FROM " + tableName);
		if (null != whereName && whereName.length() > 0) {
			sql.append(" WHERE " + whereName);
		}
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		return null != result.getValue(resultName, 0)
				&& result.getValue(resultName, 0).length() > 0 ? result
				.getValue(resultName, 0) : "";
	}

	/**
	 * ������ҹ�����־�Ǽǲ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm opdUpdateName(TParm parm) {
		TParm result = new TParm();
		result.setData("TJM2RQ", "");// ���� ����д���� 2002-01-01
		result.setData("TJM2KB", "");// �Ʊ����
		result.setData("TJM2001", "");// ��������ҽʦ��ʱ
		result.setData("TJM2002", "");// ����ҽʦ��ʱ
		result.setData("TJM2003", "");// ҽʦҽʿ��ʱ
		result.setData("TJM2004", "");// ����ʵϰ��Ա��ʱ
		result.setData("TJM2005", "");// ��������ҽʦ����
		result.setData("TJM2006", "");// ����ҽʦ����
		result.setData("TJM2007", "");// ҽʦҽʿ����
		result.setData("TJM2008", "");// ����ʵϰ��Ա��
		result.setData("TJM2009", "");// �����˴�:��ʵ�������д����ֵ
		result.setData("TJM2010", "");// ר�������˴�
		result.setData("TJM2011", "");// ר�������˴�
		result.setData("TJM2012", "");// �����˴�
		result.setData("TJM2013", "");// �����˴�
		result.setData("TJM2014", "");// �����˴�
		result.setData("TJM2015", "");// ������������
		result.setData("TJM2016", "");// ������������
		return result;
	}

	/**
	 * סԺ����������־�ǼǱ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm odiUpdateName(TParm parm) {
		TParm result = new TParm();
		result.setData("TJZ1RQ", "");// ���� ����д���� 2002-01-01
		result.setData("TJZ1KB", "");// �Ʊ����
		result.setData("TJZ1001", "");// ʵ�ʿ��Ŵ�λ��:��ʵ�������д����ֵ
		result.setData("TJZ1002", "");// ԭ������
		result.setData("TJZ1003", "");// ��Ժ����
		result.setData("TJZ1004", "");// ����ת��
		result.setData("TJZ1005", "");// ��Ժ����
		result.setData("TJZ1007", "");// ����
		result.setData("TJZ1008", "");// ��ת
		result.setData("TJZ1009", "");// δ��
		result.setData("TJZ1010", "");// ����
		result.setData("TJZ1014", "");// ����
		result.setData("TJZ1012", "");// ��������
		result.setData("TJZ1013", "");// �ƻ�����
		result.setData("TJZ1015", "");// ת������
		result.setData("TJZ1016", "");// ��Ժ����
		result.setData("TJZ1017", "");// ��Ժ��ռ���ܴ�����
		result.setData("TJZ1018", "");// Σ�ز�����
		result.setData("TJZ1019", "");// �������
		result.setData("TJZ1020", "");// 24Сʱ����������
		return result;
	}

	/**
	 * ��Ժ�������Ϣ��
	 */
	private void outDiagInfo(TParm parm) {
		TParm result = new TParm();
		result.setData("BAZA01", parm.getValue("MR_NO")); // ������ BAZA01 C 10
		result.setData("BAF102", parm.getValue("MR_NO"));
		// ��Ժ�����˳��� BAF102 N 1
		// ��Ժ����ϣ������룩 BAF103 C 10 ��Ӧ��gb17���е�DM�ֶ�
		// ��Ժ����ϣ�ICD_9�� BAF104 C 5
		// ת�飨����ϣ� BAF105 C 1
		// ��֢��־ BAF106 C 1
		// ��Ⱦ��λ BAF107 C 1
		// ��Ժ����ϣ�ICD_10�� BAF111 C 8 ��Ӧ��gb17���е�DMMC�ֶ�

	}// OUT_DIAG_CODE6
}
