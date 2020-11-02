package jdo.reg;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.SystemTool;
import jdo.util.FtpClient;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: �����Ű�
 * </p>
 * 
 * <p>
 * Description: �����Ű�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: blueocre
 * </p>
 * 
 * @author zhangp 2012-9-02
 * @version 4.0
 */
public class REGCcbSchtool extends TJDODBTool {

	private static REGCcbSchtool instanceObject;
	private String tomReg = "";
	private String Reg14 = "";
	private String deptStr = "";
	private String drStr = "";
	private FtpClient ftpClient;

	/**
	 * �õ�ʵ��
	 * 
	 * @return REGCCBtool
	 */
	public static REGCcbSchtool getInstance() {
		if (instanceObject == null)
			instanceObject = new REGCcbSchtool();
		return instanceObject;
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
		}
		return config;
	}

	/**
	 * �õ�REGSCHDAY·��
	 * 
	 * @return
	 */
	public String getPath() {
		String path = "";
		path = getProp().getString("", "REGSCHDAY.PATH");
		if (path == null || path.trim().length() <= 0) {
		}
		return path;
	}

	/**
	 * �õ�REGSCHDAY·��
	 * 
	 * @return
	 */
	public String getCount() {
		String path = "";
		path = getProp().getString("", "REGSCHDAY.COUNT");
		if (path == null || path.trim().length() <= 0) {
		}
		return path;
	}

	/**
	 * �����ļ�
	 * 
	 * @param data
	 * @param name
	 * @param newdate
	 * @param dataStr
	 * @return
	 */
	public boolean sendFile(String data, String name, String newdate,
			String dataStr) {
		try {
			File file = new File("C:\\JavaHis\\");
			if (!file.exists()) {
				file.mkdir();
			}
			String filename = "C:\\JavaHis\\" + name + "000551_" + newdate
					+ "_" + dataStr;
			file = new File(filename);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
				}
			} else {
				file.delete();
				file = new File(filename);
				file.createNewFile();
			}
			FileWriter fr = new FileWriter(filename);
			BufferedWriter bw = new BufferedWriter(fr);
			bw.write(data);
			bw.flush();
			bw.close();
			ftpClient = new FtpClient();
			FileInputStream in = null;
			try {
				in = new FileInputStream(new File(filename));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (ftpClient
					.uploadFile(getPath(), 21, "javahis", "javahis", "",
							"upload\\" + name + "000551_" + newdate + "_"
									+ dataStr, in)) {

			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void copy(String filename) {
		try {
			File oldFile = new File(filename);

			String newPath = this.getPath();
			File fnewpath = new File(newPath);
			if (!fnewpath.exists()) {
				fnewpath.mkdirs();
			}

			File fnew = new File(newPath + oldFile.getName());

			if (fnew.isFile()) {
				String filePath = newPath + oldFile.getName();
				File myDelFile = new File(filePath);
				myDelFile.delete();
			}

			oldFile.renameTo(fnew);
		} catch (Exception e) {
		}

	}

	/**
	 * ��ѯ�Ű���Ϣ
	 * 
	 * @param fig
	 * @param date
	 * @return
	 */
	public TParm onExecuteQuery(String fig, String date) {
		String clinicroom_desc = "";
		if (fig.equals("2")) {
			clinicroom_desc = ",B.CLINICROOM_DESC ";
		}
		String SQL = "";
		SQL = "SELECT A.REALDEPT_CODE, H.DEPT_CHN_DESC DEPT_NAME, I.USER_NAME, A.REALDR_CODE,"
				+ " CASE"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '01'"
				+ " THEN"
				+ " 2"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '02'"
				+ " THEN"
				+ " 1"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '03'"
				+ " THEN"
				+ " 0"
				+ " END CLINICTYPE_CODE,SUBSTR (A.ADM_DATE, 0, 4)|| '-'"
				+ " || SUBSTR (A.ADM_DATE, 5, 2)"
				+ " || '-'"
				+ " || SUBSTR (A.ADM_DATE, 7, 2) ADM_DATE,"
				+ " CASE"
				+ " WHEN A.SESSION_CODE = '01'"
				+ " THEN '����'"
				+ " WHEN A.SESSION_CODE = '02'"
				+ " THEN '����'"
				+ " END"
				+ " || CASE"
				+ " WHEN G.START_TIME IS NOT NULL"
				+ " THEN    ' '"
				+ " || SUBSTR (G.START_TIME, 0, 2)"
				+ " || ':'"
				+ " || SUBSTR (G.START_TIME, 3, 2)"
				+ " ELSE ''"
				+ " END ADM_DATE2,"
				+ " CASE "
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 2"
				+ " THEN '��һ'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 3"
				+ " THEN '�ܶ�'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 4"
				+ " THEN '����'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 5"
				+ " THEN '����'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 6"
				+ " THEN '����'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 7"
				+ " THEN '����'"
				+ "WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 1"
				+ " THEN '����'"
				+ "END DAY ,"
				+ "D.REG_FEE, D.CLINIC_FEE, '' OTHER_FEE "
				+ clinicroom_desc
				+ " FROM REG_SCHDAY A,"
				+ " REG_CLINICROOM B,"
				+ "(SELECT A.CLINICTYPE_CODE, A.RECEIPT_TYPE, A.ORDER_CODE,"
				+ "   C.OWN_PRICE REG_FEE, D.OWN_PRICE CLINIC_FEE, B.RECEIPT_TYPE,"
				+ "    B.ORDER_CODE"
				+ " FROM REG_CLINICTYPE_FEE A,"
				+ "     REG_CLINICTYPE_FEE B,"
				+ "     SYS_FEE C,"
				+ "      SYS_FEE D"
				+ " WHERE A.RECEIPT_TYPE = 'REG_FEE'"
				+ "  	AND B.RECEIPT_TYPE = 'CLINIC_FEE'"
				+ "     AND A.CLINICTYPE_CODE = B.CLINICTYPE_CODE"
				+ "     AND A.ADM_TYPE = 'O'"
				+ "     AND A.ADM_TYPE = B.ADM_TYPE"
				+ "     AND A.ORDER_CODE = C.ORDER_CODE"
				+ "     AND B.ORDER_CODE = D.ORDER_CODE) D ,"
				+ " REG_CLINICQUE G,"
				+ " SYS_DEPT H,"
				+ " SYS_OPERATOR I "
				+ "WHERE "
				+ " A.ADM_TYPE = G.ADM_TYPE"
				+ " AND A.ADM_DATE = G.ADM_DATE"
				+ " AND A.SESSION_CODE = G.SESSION_CODE"
				+ " AND A.CLINICROOM_NO = G.CLINICROOM_NO"
				+ " AND A.ADM_TYPE = 'O'"
				+ " AND A.ADM_DATE = '"
				+ date
				+ "'"
				+ " AND G.REGMETHOD_CODE = 'B' "
				+ " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE "
				+ " AND A.CLINICROOM_NO = B.CLINICROOM_NO "
				+ " AND A.DEPT_CODE = H.DEPT_CODE AND"
				+ " A.REALDR_CODE = I.USER_ID "
				+ " AND A.DEPT_CODE NOT IN ('020103','020104','020101','020102') "
				+ " UNION ALL"
				+ " SELECT A.REALDEPT_CODE, H.DEPT_CHN_DESC DEPT_NAME, I.USER_NAME, A.REALDR_CODE,"
				+ " CASE"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '01'"
				+ " THEN"
				+ " 2"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '02'"
				+ " THEN"
				+ " 1"
				+ " WHEN"
				+ " A.CLINICTYPE_CODE = '03'"
				+ " THEN"
				+ " 0"
				+ " END CLINICTYPE_CODE,SUBSTR (A.ADM_DATE, 0, 4)|| '-'"
				+ " || SUBSTR (A.ADM_DATE, 5, 2)"
				+ " || '-'"
				+ " || SUBSTR (A.ADM_DATE, 7, 2) ADM_DATE,"
				+ " CASE"
				+ " WHEN A.SESSION_CODE = '01'"
				+ " THEN '����'"
				+ " WHEN A.SESSION_CODE = '02'"
				+ " THEN '����'"
				+ " END ADM_DATE2,  CASE "
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 2"
				+ " THEN '��һ'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 3"
				+ " THEN '�ܶ�'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 4"
				+ " THEN '����'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 5"
				+ " THEN '����'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 6"
				+ " THEN '����'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 7"
				+ " THEN '����'"
				+ " WHEN TO_CHAR (TO_DATE (A.ADM_DATE, 'YYYYMMDD'), 'D') = 1"
				+ " THEN '����'"
				+ " END DAY ,"
				+ " D.REG_FEE, D.CLINIC_FEE, '' OTHER_FEE "
				+ clinicroom_desc
				+ "FROM REG_SCHDAY A,REG_CLINICROOM B,"
				+ " (SELECT A.CLINICTYPE_CODE, A.RECEIPT_TYPE, A.ORDER_CODE,"
				+ " C.OWN_PRICE REG_FEE, D.OWN_PRICE CLINIC_FEE, B.RECEIPT_TYPE,"
				+ " B.ORDER_CODE"
				+ " FROM REG_CLINICTYPE_FEE A,"
				+ " REG_CLINICTYPE_FEE B,"
				+ " SYS_FEE C,"
				+ " SYS_FEE D"
				+ " WHERE A.RECEIPT_TYPE = 'REG_FEE'"
				+ " AND B.RECEIPT_TYPE = 'CLINIC_FEE'"
				+ " AND A.CLINICTYPE_CODE = B.CLINICTYPE_CODE"
				+ " AND A.ADM_TYPE = 'O'"
				+ " AND A.ADM_TYPE = B.ADM_TYPE"
				+ " AND A.ORDER_CODE = C.ORDER_CODE"
				+ " AND B.ORDER_CODE = D.ORDER_CODE) D, "
				+ " SYS_DEPT H,"
				+ " SYS_OPERATOR I  "
				+ " WHERE "
				+ " A.ADM_TYPE = 'O'"
				+ " AND A.ADM_DATE = '"
				+ date
				+ "'"
				+ " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE "
				+ " AND A.CLINICROOM_NO = B.CLINICROOM_NO "
				+ " AND A.DEPT_CODE = H.DEPT_CODE AND"
				+ " A.REALDR_CODE = I.USER_ID "
				+ " AND A.CLINICTYPE_CODE <> '01'"
				+ " AND A.DEPT_CODE NOT IN ('020103','020104','020101','020102') ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

	/**
	 * ��ѯ���հ����Ϣ
	 * 
	 * @param dataStr
	 * @return
	 */
	public TParm query(String dataStr) {
		tomReg = "";
		String yesterday = SystemTool.getInstance().getDate().toString();
		String newdate = yesterday.substring(0, 4) + yesterday.substring(5, 7)
				+ yesterday.substring(8, 10);
		// String newdate = "20120504";
		String bool = "Y";
		TParm returnParm = new TParm();
		TParm parm = this.onExecuteQuery("2", newdate);
		TParm result = this.queryCount();
		parm.setData("COUNT", result.getData("COUNT"));
		for (int i = 0; i < parm.getCount(); i++) {
			tomReg += parm.getValue("REALDEPT_CODE", i) + "\t"
					+ parm.getValue("DEPT_NAME", i) + "\t"
					+ parm.getValue("USER_NAME", i) + "\t"
					+ parm.getValue("REALDR_CODE", i) + "\t"
					+ parm.getValue("CLINICTYPE_CODE", i) + "\t"
					+ parm.getValue("ADM_DATE", i) + "\t"
					+ parm.getValue("DAY", i) + "\t"
					+ parm.getValue("ADM_DATE2", i) + "\t"
					+ parm.getValue("REG_FEE", i) + "\t"
					+ parm.getValue("CLINIC_FEE", i) + "\t"
					+ parm.getValue("OTHER_FEE", i) + "\t"
					+ parm.getValue("CLINICROOM_DESC", i) + "\t";// ��ʾCLINICROOM_DESC
			if (parm.getValue("CLINICTYPE_CODE", i).equals("2")) {
				tomReg += 1 + "\r\n";
			} else {
				tomReg += getCount() + "\r\n";
			}

		}
		if (!sendFile(tomReg, "DoctorRegToday_", newdate, dataStr)) {
			bool = "N";
		}
		returnParm.setData("BOOL", bool);
		returnParm.setData("DATE", newdate);
		return returnParm;
	}

	public TParm queryByDate(String dataStr) {
		Reg14 = "";
		String yesterday = StringTool.rollDate(
				SystemTool.getInstance().getDate(), 1).toString();
		String newdate = yesterday.substring(0, 4) + yesterday.substring(5, 7)
				+ yesterday.substring(8, 10);
		Date data = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		String bool = "Y";
		TParm returnParm = new TParm();
		TParm parm = this.onExecuteQuery("1", newdate);
		TParm result = this.queryCount();
		parm.setData("COUNT", result.getData("COUNT"));
		for (int i = 0; i < parm.getCount(); i++) {
			Reg14 += parm.getValue("REALDEPT_CODE", i) + "\t"
					+ parm.getValue("DEPT_NAME", i) + "\t"
					+ parm.getValue("USER_NAME", i) + "\t"
					+ parm.getValue("REALDR_CODE", i) + "\t"
					+ parm.getValue("CLINICTYPE_CODE", i) + "\t"
					+ parm.getValue("ADM_DATE", i) + "\t"
					+ parm.getValue("DAY", i) + "\t"
					+ parm.getValue("ADM_DATE2", i) + "\t"
					+ parm.getValue("REG_FEE", i) + "\t"
					+ parm.getValue("CLINIC_FEE", i) + "\t"
					+ parm.getValue("OTHER_FEE", i) + "\t";
			if (parm.getValue("CLINICTYPE_CODE", i).equals("2")) {
				Reg14 += 1 + "\r\n";
			} else {
				Reg14 += getCount() + "\r\n";
			}
		}
		if (!sendFile(Reg14, "DoctorReg_", sf.format(data), dataStr)) {
			bool = "N";
		}
		returnParm.setData("BOOL", bool);
		returnParm.setData("DATE", newdate);
		return returnParm;
	}

	/**
	 * ��ѯ����
	 * 
	 * @return
	 */
	public TParm queryCount() {
		String sql = "SELECT COUNT(*) AS COUNT FROM REG_QUEMETHOD WHERE QUEGROUP_CODE='01' AND REGMETHOD_CODE='B'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ������Ϣ
	 * 
	 * @param dataStr
	 * @return
	 */
	public TParm queryDept(String dataStr) {
		deptStr = "";
		String yesterday = SystemTool.getInstance().getDate().toString();
		String newdate = yesterday.substring(0, 4) + yesterday.substring(5, 7)
				+ yesterday.substring(8, 10);
		String bool = "Y";
		TParm returnParm = new TParm();
		TParm parm = this.dept();
		for (int i = 0; i < parm.getCount(); i++) {
			deptStr += parm.getValue("DEPT_CODE", i) + "\t"
					+ parm.getValue("DEPT_CHN_DESC", i) + "\t"
					+ parm.getValue("DEPT_PATH", i) + "\t"
					+ parm.getValue("REG_TYPE", i) + "\t\r\n";
		}
		if (!sendFile(deptStr, "Item_", newdate, dataStr)) {
			bool = "N";
		}
		returnParm.setData("BOOL", bool);
		returnParm.setData("DATE", newdate);
		return returnParm;
	}

	/**
	 * ��ѯҽʦ��Ϣ
	 * 
	 * @param dataStr
	 * @return
	 */
	public TParm queryDr(String dataStr) {
		drStr = "";
		String yesterday = SystemTool.getInstance().getDate().toString();
		String newdate = yesterday.substring(0, 4) + yesterday.substring(5, 7)
				+ yesterday.substring(8, 10);
		String bool = "Y";
		TParm returnParm = new TParm();
		TParm parm = this.dr();
		for (int i = 0; i < parm.getCount(); i++) {
			drStr += parm.getValue("DEPT_CODE", i) + "\t"
					+ parm.getValue("DEPT_CHN_DESC", i) + "\t"
					+ parm.getValue("USER_NAME", i) + "\t"
					+ parm.getValue("USER_ID", i) + "\t"
					+ parm.getValue("POS_CHN_DESC", i) + "\t"
					+ parm.getValue("TALENT", i) + "\t"
					+ parm.getValue("SDESC", i) + "\t\r\n";
		}
		if (!sendFile(drStr, "Doctor_", newdate, dataStr)) {
			bool = "N";
		}
		returnParm.setData("BOOL", bool);
		returnParm.setData("DATE", newdate);
		return returnParm;
	}

	public TParm dept() {
		String sql = " SELECT DEPT_CODE, DEPT_CHN_DESC, '' DEPT_PATH, '' REG_TYPE"
				+ " FROM SYS_DEPT"
				+ " WHERE ACTIVE_FLG = 'Y' AND OPD_FIT_FLG = 'Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

	public TParm dr() {
		String sql = " SELECT C.DEPT_CODE, C.DEPT_CHN_DESC, A.USER_NAME, A.USER_ID, D.POS_CHN_DESC,"
				+ " '' TALENT, '' SDESC"
				+ " FROM SYS_OPERATOR A, SYS_OPERATOR_DEPT B, SYS_DEPT C, SYS_POSITION D"
				+ " WHERE ROLE_ID IN ('ODO', 'OIDR')"
				+ " AND A.USER_ID = B.USER_ID"
				+ " AND B.MAIN_FLG = 'Y'"
				+ " AND B.DEPT_CODE = C.DEPT_CODE"
				+ " AND A.POS_CODE = D.POS_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			return parm;
		}
		return parm;
	}

}
