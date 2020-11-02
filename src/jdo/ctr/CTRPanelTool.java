package jdo.ctr;

import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import jdo.sys.CTZTool;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import jdo.sys.Operator;

/**
 * <p>
 * Title: ҽ��ܿع�����
 * </p>
 * 
 * <p>
 * Description: ҽ��ܿع�����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shibl 2011.07.05
 * @version 1.0
 */
public class CTRPanelTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static CTRPanelTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return CTRPanelTool
	 */
	public static CTRPanelTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new CTRPanelTool();
		}
		return instanceObject;
	}

	/**
	 * ������
	 */
	public CTRPanelTool() {
		setModuleName("ctr\\CTRPanelModule.x");
		onInit();
	}

	/**
	 * ����
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = update("insert", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯȫ�ֶ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectall(TParm parm) {
		TParm result = new TParm();
		result = query("selectall", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * У�������Ŀ���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selCTRPanel(TParm parm) {
		TParm result = new TParm();
		// �ż���
		String admType = parm.getValue("ADM_TYPE");
		// System.out.println("�ż���" + admType);
		// �������
		String caseNo = parm.getValue("CASE_NO");
		// System.out.println("�������" + caseNo);
		// ������
		String mrNo = parm.getValue("MR_NO");
		// System.out.println("������"+mrNo);
		// ҽ������
		String orderCode = parm.getValue("ORDER_CODE");
		// ��ݴ���
		String ctzCode = parm.getValue("CTZ_CODE");
		// System.out.println("��ݴ���" + ctzCode);
		// ����ʱ��
		Timestamp orderDate;
		TParm inParm = new TParm();
		// ��orס
		String typeCode = "";
		if ("I".equals(admType)) {
			orderDate = SystemTool.getInstance().getDate();
			inParm = parm.getParm("Parm");
		} else {
			orderDate = parm.getTimestamp("ORDER_DATE");
		}
		// System.out.println("����ʱ��" + orderDate);
		// ҽ����ݡ��Է����
		TParm ctzParm = CTZTool.getInstance().getFlgByCtz(ctzCode);
		// ҽ������
		String sqlMaster = "";
		if (ctzParm.getBoolean("NHI_CTZ_FLG", 0)) {
			sqlMaster = " SELECT ORDER_CODE,CONTROL_ID,FORCE_FLG,MESSAGE_TEXT,MESSAGE_TEXT_E,CTRL_COMMENT,SUBCLASS_CODE"
					+ " FROM CTR_MATER WHERE "
					+ " ORDER_CODE='"
					+ orderCode
					+ "' AND NHI_FLG='Y' ";
		} else {
			sqlMaster = " SELECT ORDER_CODE,CONTROL_ID,FORCE_FLG,MESSAGE_TEXT,MESSAGE_TEXT_E,CTRL_COMMENT,SUBCLASS_CODE "
					+ " FROM CTR_MATER WHERE "
					+ " ORDER_CODE='"
					+ orderCode
					+ "' AND OWN_FLG='Y' ";
		}
		TParm masterParm = new TParm(TJDODBTool.getInstance().select(sqlMaster));
		// System.out.println("masterParm=====" + masterParm);
		for (int j = 0; j < masterParm.getCount(); j++) {
			// ҽ��ϸ��
			String sqlDetail = " SELECT A.ORDER_CODE,A.CONTROL_ID,A.SERIAL_NO,A.GROUP_NO,A.RESTRITEM_CODE,"
					+ "        A.LIMIT_TYPE,A.PARAVALUE_1,A.PARADATATYPE_1,A.PARAVALUE_2,A.PARADATATYPE_2,"
					+ "        A.PARAVALUE_3,A.PARADATATYPE_3,A.PERIOD_CODE,A.PERIOD_VALUE,A.LOGICAL_TYPE "
					+ "   FROM CTR_DETAIL A,CTR_MATER B "
					+ "  WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND A.CONTROL_ID=B.CONTROL_ID(+) AND "
					+ "  A.ORDER_CODE = '"
					+ orderCode
					+ "' "
					+ " AND A.CONTROL_ID='"
					+ masterParm.getValue("CONTROL_ID", j)
					+ "'"
					+ " AND B.ACTIVE_FLG = 'Y' " + " ORDER BY A.SERIAL_NO  ";
			// System.out.println("sqlDetail=====" + sqlDetail);
			TParm detailParm = new TParm(TJDODBTool.getInstance().select(
					sqlDetail));
			// System.out.println("detailParm=====" + detailParm);
			int detailCount = detailParm.getCount("RESTRITEM_CODE");
			String restritemCode = "";
			for (int i = 0; i < detailCount; i++) {
				// ������Ŀ
				restritemCode = detailParm.getValue("RESTRITEM_CODE", i);
				// ���Ʊ��
				int controlId = detailParm.getInt("CONTROL_ID", i);
				// ���
				int serialNo = detailParm.getInt("SERIAL_NO", i);
				// �߼�����
				String logicaltype = detailParm.getValue("LOGICAL_TYPE", i);
				int logicalcode = Integer.parseInt(logicaltype);
				String messageStr = "";
				String subClassCode = "";
				if (masterParm.getCount("MESSAGE_TEXT") > 0) {
					messageStr = masterParm.getValue("MESSAGE_TEXT", j);
				}
				if (masterParm.getCount("SUBCLASS_CODE") > 0) {
					subClassCode = masterParm.getValue("SUBCLASS_CODE", j);
				}
				int next = i + 1;
				// ��ֵ����1�����
				if (detailCount > 1 && next < detailCount) {
					// ��Ե���һ��������Ŀ
					String NrestritemCode = detailParm.getValue(
							"RESTRITEM_CODE", next);
					int NcontrolId = detailParm.getInt("CONTROL_ID", next);
					int NserialNo = detailParm.getInt("SERIAL_NO", next);
					// �߼�����ΪAND
					if (logicalcode == 1) {
						// System.out.println("�߼�����ΪAND");
						if ((!restritem(restritemCode, admType, typeCode,
								orderCode, caseNo, controlId, serialNo, mrNo,
								orderDate, inParm) && !restritem(
								NrestritemCode, admType, typeCode, orderCode,
								caseNo, NcontrolId, NserialNo, mrNo, orderDate,
								inParm))) {
							result.setData("FORCE_FLG",
									masterParm.getValue("FORCE_FLG", 0));
							result.setData("MESSAGE", messageStr);
							result.setData("SUBCLASS_CODE", subClassCode);
							result.setErrCode(100);
							return result;
						}
					}
					// �߼�����ΪOR
					if (logicalcode == 2) {
						// System.out.println("�߼�����ΪOR");
						if ((!restritem(restritemCode, admType, typeCode,
								orderCode, caseNo, controlId, serialNo, mrNo,
								orderDate, inParm) || !restritem(
								NrestritemCode, admType, typeCode, orderCode,
								caseNo, NcontrolId, NserialNo, mrNo, orderDate,
								inParm))) {
							result.setData("FORCE_FLG",
									masterParm.getValue("FORCE_FLG", 0));
							result.setData("MESSAGE", messageStr);
							result.setData("SUBCLASS_CODE", subClassCode);
							result.setErrCode(100);
							return result;
						}
					}
					if (logicalcode == 3) {
						break;
					}
				}
				// ��ֵ����1�����
				else if (detailCount == 1) {
					if (!restritem(restritemCode, admType, typeCode, orderCode,
							caseNo, controlId, serialNo, mrNo, orderDate,
							inParm)) {
						result.setData("FORCE_FLG",
								masterParm.getValue("FORCE_FLG", 0));
						result.setData("MESSAGE", messageStr);
						result.setData("SUBCLASS_CODE", subClassCode);
						result.setErrCode(100);
						return result;
					}
				}
			}
		}
		result.setErrCode(99);
		return result;
	}

	/**
	 * ������Ŀ����
	 * 
	 * @param restritemCode
	 *            String
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @param mrNo
	 *            String
	 * @param orderDate
	 *            Timestamp
	 * @return boolean
	 */
	public boolean restritem(String restritemCode, String admType,
			String typeCode, String orderCode, String caseNo, int controlId,
			int serialNo, String mrNo, Timestamp orderDate, TParm parm) {

		// �����
		if ("01".equals(restritemCode)) {
			if (!restritem1(admType, typeCode, orderCode, caseNo, controlId,
					serialNo)) {
				return false;
			}
		}
		// ��һ���
		if ("02".equals(restritemCode)) {
			// System.out.println("��һ���");
			if (!restritem2(admType, typeCode, orderCode, caseNo, controlId,
					serialNo)) {

				return false;
			}
		}
		// ʩ����ĳҽ��֮ǰ
		if ("03".equals(restritemCode)) {
			// System.out.println("ʩ����ĳҽ��֮ǰ");
			if (!restritem3(admType, typeCode, orderCode, caseNo, controlId,
					serialNo, orderDate)) {
				return false;
			}
		}
		// ʩ����ĳҽ��֮��
		if ("04".equals(restritemCode)) {
			// System.out.println("ʩ����ĳҽ��֮��");
			if (!restritem4(admType, typeCode, orderCode, caseNo, controlId,
					serialNo, orderDate, parm)) {

				return false;
			}
		}
		// ����ֵ
		if ("05".equals(restritemCode)) {
			// System.out.println("����ֵ");
			if (!restritem5(admType, typeCode, mrNo, caseNo, orderCode,
					controlId, serialNo, orderDate)) {
				return false;
			}

		}
		// ����
		if ("06".equals(restritemCode)) {
			// System.out.println("����");
			if (!restritem6(mrNo, admType, typeCode)) {
				return false;
			}

		}
		return true;
	}

	/**
	 * �����У��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @return boolean
	 */
	public boolean restritem1(String admType, String typeCode,
			String orderCode, String caseNo, int controlId, int serialNo) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '01' ";
		// System.out.println("sql"+sql);
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀ����ϲ�ѯ���ݴ���!");
		}
		// ���������Ŀ��������
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������Ŀ��������
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������ĿסԺ����
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		String region = "";
		if (!Operator.getRegion().equals("")
				&& Operator.getRegion().length() > 0) {
			region = "AND REGION_CODE='" + Operator.getRegion() + "'";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("")) {
			String limitTypeSql = " SELECT LIMIT_TYPE " + "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '" + orderCode + "' "
					+ "    AND CONTROL_ID = " + controlId + " "
					+ "    AND SERIAL_NO = " + serialNo + " ";
			// System.out.println("limitTypeSql"+limitTypeSql);
			TParm limitTypeParm = new TParm(TJDODBTool.getInstance().select(
					limitTypeSql));
			String limitType = limitTypeParm.getValue("LIMIT_TYPE", 0);
			String paraDetailSql = "";
			TParm paraDetailParm = new TParm();
			String icdSql = "";
			TParm icdParm = new TParm();
			if ("O".equals(typeCode)) {
				icdSql = " SELECT CASE_NO,ICD_TYPE,ICD_CODE "
						+ "   FROM OPD_DIAGREC " + "  WHERE CASE_NO = '"
						+ caseNo + "' " + "    AND MAIN_DIAG_FLG = 'Y'  ";
				// System.out.println("icdSql"+icdSql);
				icdParm = new TParm(TJDODBTool.getInstance().select(icdSql));
			} else {
				icdSql = " SELECT CASE_NO,ICD_TYPE,ICD_CODE "
						+ "   FROM ADM_INPDIAG " + "  WHERE CASE_NO = '"
						+ caseNo + "' " + "    AND MAINDIAG_FLG = 'Y' ";
				// System.out.println("icdSql"+icdSql);
				icdParm = new TParm(TJDODBTool.getInstance().select(icdSql));
			}
			for (int j = 0; j < icdParm.getCount("ICD_CODE"); j++) {
				// �����
				String icdMCode = icdParm.getValue("ICD_CODE", j);
				if ("1".equals(limitType)) {
					// System.out.println("�����������");
					paraDetailSql = " SELECT START_VALUE, END_VALUE FROM CTR_DETAIL "
							+ "  WHERE ORDER_CODE = '"
							+ orderCode
							+ "' "
							+ "    AND CONTROL_ID = "
							+ controlId
							+ " "
							+ "    AND SERIAL_NO = " + serialNo + " ";
					paraDetailParm = new TParm(TJDODBTool.getInstance().select(
							paraDetailSql));
					String start = paraDetailParm.getValue("START_VALUE", 0)
							.toString();
					String end = paraDetailParm.getValue("END_VALUE", 0)
							.toString();
					if (!start.equals("") && !end.equals("")) {
						if (StringTool.compareTo(icdMCode, start.toUpperCase()) >= 0
								&& StringTool.compareTo(end.toUpperCase(),
										icdMCode) >= 0) {
							return false;
						}
					}
				}
				// �ų�
				else {
					paraDetailSql = " SELECT START_VALUE, END_VALUE FROM CTR_DETAIL "
							+ "  WHERE ORDER_CODE = '"
							+ orderCode
							+ "' "
							+ "    AND CONTROL_ID = "
							+ controlId
							+ " "
							+ "    AND SERIAL_NO = " + serialNo + " ";
					// System.out.println("paraDetailSql"+paraDetailSql);
					paraDetailParm = new TParm(TJDODBTool.getInstance().select(
							paraDetailSql));
					String start = paraDetailParm.getValue("START_VALUE", 0);
					String end = paraDetailParm.getValue("END_VALUE", 0);
					if (!start.equals("") && !end.equals("")) {
						if (!(StringTool.compareTo(start.toUpperCase(),
								icdMCode) <= 0 && StringTool.compareTo(
								icdMCode, end.toUpperCase()) <= 0)) {
							return false;
						}
					}
				}

			}
		}
		return true;
	}

	/**
	 * ��һ���У��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @return boolean
	 */
	public boolean restritem2(String admType, String typeCode,
			String orderCode, String caseNo, int controlId, int serialNo) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '02' ";
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀ��һ��ϲ�ѯ���ݴ���!");
		}
		// ���������Ŀ��������
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������Ŀ��������
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������ĿסԺ����
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("")) {
			String limitTypeSql = " SELECT LIMIT_TYPE " + "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '" + orderCode + "' "
					+ "    AND CONTROL_ID = " + controlId + " "
					+ "    AND SERIAL_NO = " + serialNo + " ";
			TParm limitTypeParm = new TParm(TJDODBTool.getInstance().select(
					limitTypeSql));
			// ��������
			String limitType = limitTypeParm.getValue("LIMIT_TYPE", 0);
			String paraDetailSql = "";
			TParm paraDetailParm = new TParm();
			String icdSql = "";
			TParm icdParm = new TParm();
			// ����
			if ("O".equals(typeCode)) {
				icdSql = " SELECT CASE_NO,ICD_TYPE,ICD_CODE "
						+ "   FROM OPD_DIAGREC " + "  WHERE CASE_NO = '"
						+ caseNo + "' ";
				icdParm = new TParm(TJDODBTool.getInstance().select(icdSql));
			}
			// סԺ
			else {
				icdSql = " SELECT CASE_NO,ICD_TYPE,ICD_CODE "
						+ "   FROM ADM_INPDIAG " + "  WHERE CASE_NO = '"
						+ caseNo + "' ";
				icdParm = new TParm(TJDODBTool.getInstance().select(icdSql));
			}
			for (int j = 0; j < icdParm.getCount("ICD_CODE"); j++) {
				// ��һ���
				String icdMCode = icdParm.getValue("ICD_CODE", j);
				if ("1".equals(limitType)) {
					// System.out.println("��һ��ϡ��������ƽ���");
					paraDetailSql = " SELECT START_VALUE,END_VALUE FROM CTR_DETAIL "
							+ "  WHERE ORDER_CODE = '"
							+ orderCode
							+ "' "
							+ "    AND CONTROL_ID = "
							+ controlId
							+ " "
							+ "    AND SERIAL_NO = " + serialNo + " ";
					paraDetailParm = new TParm(TJDODBTool.getInstance().select(
							paraDetailSql));
					String start = paraDetailParm.getValue("START_VALUE", 0)
							.toString();
					String end = paraDetailParm.getValue("END_VALUE", 0)
							.toString();
					if (!start.equals("") && !end.equals("")) {
						if (StringTool.compareTo(icdMCode, start.toUpperCase()) >= 0
								&& StringTool.compareTo(end.toUpperCase(),
										icdMCode) >= 0) {
							return false;
						}
					}
				} else {
					// System.out.println("��һ��ϡ������ų�����");
					paraDetailSql = " SELECT START_VALUE,END_VALUE FROM CTR_DETAIL "
							+ "  WHERE ORDER_CODE = '"
							+ orderCode
							+ "' "
							+ "    AND CONTROL_ID = "
							+ controlId
							+ " "
							+ "    AND SERIAL_NO = " + serialNo + " ";
					paraDetailParm = new TParm(TJDODBTool.getInstance().select(
							paraDetailSql));
					String start = paraDetailParm.getValue("START_VALUE", 0);
					String end = paraDetailParm.getValue("END_VALUE", 0);
					if (!start.equals("") && !end.equals("")) {
						if (!(StringTool.compareTo(start.toUpperCase(),
								icdMCode) <= 0 && StringTool.compareTo(
								icdMCode, end.toUpperCase()) <= 0)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * ʩ����ĳҽ��֮ǰУ��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @param orderDate
	 *            Timestamp
	 * @return boolean
	 */
	public boolean restritem3(String admType, String typeCode,
			String orderCode, String caseNo, int controlId, int serialNo,
			Timestamp orderDate) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '03' ";
		// System.out.println("sql"+sql);
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀʩ��ĳҽ��֮ǰ��ѯ���ݴ���!");
		}
		// ���������Ŀ��������
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������Ŀ��������
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������ĿסԺ����
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		String region = "";
		if (!Operator.getRegion().equals("")
				&& Operator.getRegion().length() > 0) {
			region = "AND REGION_CODE='" + Operator.getRegion() + "'";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("")) {
			String paraValueSql = " SELECT PARAVALUE_1, PARAVALUE_2, PARAVALUE_3 "
					+ "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "' "
					+ "    AND CONTROL_ID = "
					+ controlId
					+ " " + "    AND SERIAL_NO = " + serialNo + " ";
			// System.out.println("paraValueSql"+paraValueSql);
			TParm paraValueParm = new TParm(TJDODBTool.getInstance().select(
					paraValueSql));
			String limitTypeSql = " SELECT LIMIT_TYPE " + "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '" + orderCode + "' "
					+ "    AND CONTROL_ID = " + controlId + " "
					+ "    AND SERIAL_NO = " + serialNo + " ";
			TParm limitTypeParm = new TParm(TJDODBTool.getInstance().select(
					limitTypeSql));
			String limitType = limitTypeParm.getValue("LIMIT_TYPE", 0);
			String value1Sql = "";
			TParm value1Parm = new TParm();
			String value2Sql = "";
			TParm value2Parm = new TParm();
			String value3Sql = "";
			TParm value3Parm = new TParm();
			if ("O".equals(typeCode)) {
				if (paraValueParm.getData("PARAVALUE_1", 0) != null
						&& paraValueParm.getValue("PARAVALUE_1", 0).length() > 0) {
					value1Sql = " SELECT ORDER_DATE FROM OPD_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_1", 0) + "' "
							+ region;
					// System.out.println("value1Sql"+value1Sql);
					value1Parm = new TParm(TJDODBTool.getInstance().select(
							value1Sql));
					if (value1Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value1Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}
				if (paraValueParm.getData("PARAVALUE_2", 0) != null
						&& paraValueParm.getValue("PARAVALUE_2", 0).length() > 0) {

					value2Sql = " SELECT ORDER_DATE FROM OPD_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_2", 0) + "' "
							+ region;
					// System.out.println("value2Sql"+value2Sql);
					value2Parm = new TParm(TJDODBTool.getInstance().select(
							value2Sql));
					if (value2Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value2Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}
				if (paraValueParm.getData("PARAVALUE_3", 0) != null
						&& paraValueParm.getValue("PARAVALUE_3", 0).length() > 0) {

					value3Sql = " SELECT ORDER_DATE FROM OPD_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_3", 0) + "' "
							+ region;
					// System.out.println("value3Sql"+value3Sql);
					value3Parm = new TParm(TJDODBTool.getInstance().select(
							value3Sql));
					if (value3Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value3Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}
			} else {
				if (paraValueParm.getData("PARAVALUE_1", 0) != null
						&& paraValueParm.getValue("PARAVALUE_1", 0).length() > 0) {
					value1Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_1", 0) + "' "
							+ region;
					// System.out.println("value1Sql"+value1Sql);
					value1Parm = new TParm(TJDODBTool.getInstance().select(
							value1Sql));
					if (value1Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value1Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}
				if (paraValueParm.getData("PARAVALUE_2", 0) != null
						&& paraValueParm.getValue("PARAVALUE_2", 0).length() > 0) {

					value2Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_2", 0) + "' "
							+ region;
					// System.out.println("value2Sql"+value2Sql);
					value2Parm = new TParm(TJDODBTool.getInstance().select(
							value2Sql));
					if (value2Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value2Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}
				if (paraValueParm.getData("PARAVALUE_3", 0) != null
						&& paraValueParm.getValue("PARAVALUE_3", 0).length() > 0) {

					value3Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
							+ "  WHERE CASE_NO = '" + caseNo + "' "
							+ "    AND ORDER_CODE = '"
							+ paraValueParm.getValue("PARAVALUE_3", 0) + "' "
							+ region;
					// System.out.println("value3Sql"+value3Sql);
					value3Parm = new TParm(TJDODBTool.getInstance().select(
							value3Sql));
					if (value3Parm.getData("ORDER_DATE", 0) != null
							&& StringTool.getDateDiffer(
									value3Parm.getTimestamp("ORDER_DATE", 0),
									orderDate) <= 0) {
						return false;
					}
				}

			}
		}
		return true;
	}

	/**
	 * ʩ����ĳҽ��֮��У��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param orderCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @param orderDate
	 *            Timestamp
	 * @return boolean
	 */
	public boolean restritem4(String admType, String typeCode,
			String orderCode, String caseNo, int controlId, int serialNo,
			Timestamp orderDate, TParm parm) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '04' ";
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀʩ��ĳҽ��֮���ѯ���ݴ���!");
		}
		// ���������Ŀ��������
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������Ŀ��������
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������ĿסԺ����
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		String region = "";
		if (!Operator.getRegion().equals("")
				&& Operator.getRegion().length() > 0) {
			region = "AND REGION_CODE='" + Operator.getRegion() + "'";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("")) {
			String paraValueSql = " SELECT PARAVALUE_1, PARAVALUE_2, PARAVALUE_3 "
					+ "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '"
					+ orderCode
					+ "' "
					+ "    AND CONTROL_ID = "
					+ controlId
					+ " " + "    AND SERIAL_NO = " + serialNo + " ";
			TParm paraValueParm = new TParm(TJDODBTool.getInstance().select(
					paraValueSql));
			String limitTypeSql = " SELECT LIMIT_TYPE " + "   FROM CTR_DETAIL "
					+ "  WHERE ORDER_CODE = '" + orderCode + "' "
					+ "    AND CONTROL_ID = " + controlId + " "
					+ "    AND SERIAL_NO = " + serialNo + " ";
			TParm limitTypeParm = new TParm(TJDODBTool.getInstance().select(
					limitTypeSql));
			String limitType = limitTypeParm.getValue("LIMIT_TYPE", 0);
			String value1Sql = "";
			TParm value1Parm = new TParm();
			// String value2Sql = "";
			// TParm value2Parm = new TParm();
			// String value3Sql = "";
			// TParm value3Parm = new TParm();
			// �������
			if ("O".equals(typeCode)) {
				// ����
				if ("1".equals(limitType)) {
					if (paraValueParm.getData("PARAVALUE_1", 0) != null
							&& paraValueParm.getValue("PARAVALUE_1", 0)
									.length() > 0) {
						value1Sql = " SELECT ORDER_DATE FROM OPD_ORDER "
								+ "  WHERE CASE_NO = '" + caseNo + "' "
								+ "    AND ORDER_CODE = '"
								+ paraValueParm.getValue("PARAVALUE_1", 0)
								+ "' " + region;
						value1Parm = new TParm(TJDODBTool.getInstance().select(
								value1Sql));
						if (value1Parm.getData("ORDER_DATE", 0) != null
								&& StringTool.getDateDiffer(value1Parm
										.getTimestamp("ORDER_DATE", 0),
										orderDate) <= 0) {
							return false;
						}
					}
				}
				// �ų�
				else {
					if (paraValueParm.getData("PARAVALUE_1", 0) != null
							&& paraValueParm.getValue("PARAVALUE_1", 0)
									.length() > 0) {
						value1Sql = " SELECT ORDER_DATE FROM OPD_ORDER "
								+ "  WHERE CASE_NO = '" + caseNo + "' "
								+ "    AND ORDER_CODE = '"
								+ paraValueParm.getValue("PARAVALUE_1", 0)
								+ "' " + region;
						value1Parm = new TParm(TJDODBTool.getInstance().select(
								value1Sql));
						if (value1Parm.getData("ORDER_DATE", 0) != null
								&& StringTool.getDateDiffer(value1Parm
										.getTimestamp("ORDER_DATE", 0),
										orderDate) <= 0) {

						}
						return false;
					}
				}
			}
			// סԺ
			else {
				// ҽ������
				String rxkind = parm.getValue("RX_KIND");
				// ����
				if ("1".equals(limitType)) {
					if (paraValueParm.getData("PARAVALUE_1", 0) != null
							&& paraValueParm.getValue("PARAVALUE_1", 0)
									.length() > 0) {
						// ��Ժ��ҩ
						if (rxkind.equals("DS")) {
							value1Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
									+ "  WHERE CASE_NO = '" + caseNo
									+ "' AND RX_KIND='" + rxkind
									+ "'  AND ORDER_CODE = '"
									+ paraValueParm.getValue("PARAVALUE_1", 0)
									+ "' " + region;
							value1Parm = new TParm(TJDODBTool.getInstance()
									.select(value1Sql));
							if (value1Parm.getData("ORDER_DATE", 0) != null
									&& StringTool.getDateDiffer(value1Parm
											.getTimestamp("ORDER_DATE", 0),
											orderDate) <= 0) {
								return false;
							}
						} else {
							value1Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
									+ "  WHERE CASE_NO = '"
									+ caseNo
									+ "' AND (RX_KIND='ST' OR RX_KIND='UD' OR RX_KIND='IG') "
									+ "  AND ORDER_CODE = '"
									+ paraValueParm.getValue("PARAVALUE_1", 0)
									+ "' " + region;
							value1Parm = new TParm(TJDODBTool.getInstance()
									.select(value1Sql));
							if (value1Parm.getData("ORDER_DATE", 0) != null
									&& StringTool.getDateDiffer(value1Parm
											.getTimestamp("ORDER_DATE", 0),
											orderDate) <= 0) {
								return false;
							}
						}
					}
				}
				// �ų�
				else {
					if (paraValueParm.getData("PARAVALUE_1", 0) != null
							&& paraValueParm.getValue("PARAVALUE_1", 0)
									.length() > 0) {
						// ��Ժ��ҩ
						if (rxkind.equals("DS")) {
							value1Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
									+ "  WHERE CASE_NO = '" + caseNo + "' "
									+ "' AND RX_KIND='" + rxkind
									+ "    AND ORDER_CODE = '"
									+ paraValueParm.getValue("PARAVALUE_1", 0)
									+ "' " + region;
							value1Parm = new TParm(TJDODBTool.getInstance()
									.select(value1Sql));
							if (value1Parm.getData("ORDER_DATE", 0) != null
									&& StringTool.getDateDiffer(value1Parm
											.getTimestamp("ORDER_DATE", 0),
											orderDate) <= 0) {
							}
							return false;
						}
					} else {
						if (rxkind.equals("DS")) {
							value1Sql = " SELECT ORDER_DATE FROM ODI_ORDER "
									+ "  WHERE CASE_NO = '"
									+ caseNo
									+ "' "
									+ "' AND (RX_KIND='ST' OR RX_KIND='UD' OR RX_KIND='IG') "
									+ "    AND ORDER_CODE = '"
									+ paraValueParm.getValue("PARAVALUE_1", 0)
									+ "' " + region;
							value1Parm = new TParm(TJDODBTool.getInstance()
									.select(value1Sql));
							if (value1Parm.getData("ORDER_DATE", 0) != null
									&& StringTool.getDateDiffer(value1Parm
											.getTimestamp("ORDER_DATE", 0),
											orderDate) <= 0) {
							}
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * ����ֵУ��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param mrNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param orderCode
	 *            String
	 * @param controlId
	 *            int
	 * @param serialNo
	 *            int
	 * @param orderDate
	 *            Timestamp
	 * @return boolean
	 */
	public boolean restritem5(String admType, String typeCode, String mrNo,
			String caseNo, String orderCode, int controlId, int serialNo,
			Timestamp orderDate) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '05' ";
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀ�������ѯ���ݴ���!");
		}
		// ��ѯҽ��ܿ�ϸ���������ԡ�����һ�������ڼ�ֵ���ڼ�ֵ
		String paraValueSql = " SELECT LIMIT_TYPE,PARAVALUE_1, PERIOD_CODE, PERIOD_VALUE "
				+ "   FROM CTR_DETAIL "
				+ "  WHERE ORDER_CODE = '"
				+ orderCode
				+ "' "
				+ "    AND CONTROL_ID = "
				+ controlId
				+ " "
				+ "    AND SERIAL_NO = " + serialNo + " ";
		TParm paraValueParm = new TParm(TJDODBTool.getInstance().select(
				paraValueSql));
		String limitType = paraValueParm.getValue("LIMIT_TYPE", 0);
		String period_code = paraValueParm.getValue("PERIOD_CODE", 0);
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		int period_value = paraValueParm.getInt("PERIOD_VALUE", 0);
		// ��ѯҽ��ܿ�ϸ����ʼֵ�ͽ�ֵֹ
		String value1Sql = " SELECT START_VALUE, END_VALUE FROM CTR_DETAIL "
				+ "  WHERE ORDER_CODE = '" + orderCode + "' "
				+ "    AND CONTROL_ID = " + controlId + " "
				+ "    AND SERIAL_NO = " + serialNo + " ";
		// System.out.println("value1Sql"+value1Sql);
		TParm value1Parm = new TParm(TJDODBTool.getInstance().select(value1Sql));
		String start = value1Parm.getValue("START_VALUE", 0);
		String end = value1Parm.getValue("END_VALUE", 0);
		String region = "";
		if (!Operator.getRegion().equals("")
				&& Operator.getRegion().length() > 0) {
			region = "AND REGION_CODE='" + Operator.getRegion() + "'";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("") && !start.equals("") && !end.equals("")) {
			if (period_code.equals("")) {
				System.out.println("�ڼ�ֵΪ��");
				// ��ѯ����ֵ�еļ��ֵ
				String medApplySql = " SELECT B.TEST_VALUE "
						+ "   FROM MED_APPLY A, MED_LIS_RPT B "
						+ "  WHERE A.CAT1_TYPE = B.CAT1_TYPE "
						+ "    AND A.APPLICATION_NO = B.APPLICATION_NO "
						+ "    AND B.TESTITEM_CODE = '"
						+ paraValueParm.getValue("PARAVALUE_1", 0) + "' "
						+ "    AND A.CASE_NO = '" + caseNo + "' "
						+ "    AND A.ADM_TYPE = '" + typeCode + "'" + region
						+ "  ORDER BY B.APPLICATION_NO DESC ";
				// System.out.println("medApplySql1======" + medApplySql);
				TParm medApplyParm = new TParm(TJDODBTool.getInstance().select(
						medApplySql));
				String test = medApplyParm.getValue("TEST_VALUE", 0);
				if ("1".equals(limitType)) {
					// System.out.println("���ƽ���");
					// �Ƚ�ֵ
					// System.out.println("���ֵ==========" + test);
					String[] str = new String[] { start, end, test };
					int result = lisrisCheck(str);
					// �Ƚ�ֵΪ�����ֻ��ַ�
					if (result == 1) {
						// System.out.println("�����ֻ��ַ�");
						if (start.equals(test) && end.equals(test)) {
							return false;
						}
					}
					// �Ƚ�ֵΪ����
					if (result == 2) {
						// System.out.println("�Ƚ�ֵ��������");
						double startDouble = Double.parseDouble(start);
						double endDouble = Double.parseDouble(end);
						double testDouble = Double.parseDouble(test);
						if (testDouble >= startDouble
								&& endDouble >= testDouble) {
							return false;
						}
					}
				}
				// �ų�����
				else {
					String[] str = new String[] { start, end, test };
					int result = lisrisCheck(str);
					// �Ƚ�ֵΪ�����ֻ��ַ�
					if (result == 1) {
						// System.out.println("�����ֻ��ַ�");
						if (!start.equals(test) && !end.equals(test)) {
							return false;
						}
					}
					// �Ƚ�ֵΪ����
					if (result == 2) {
						// System.out.println("�Ƚ�ֵ��������");
						double startDouble = Double.parseDouble(start);
						double endDouble = Double.parseDouble(end);
						double testDouble = Double.parseDouble(test);
						if (!(testDouble >= startDouble && endDouble >= testDouble)) {
							return false;
						}
					}

				}
			} else {
				// System.out.println("�ڼ�ֵ��Ϊ��");
				// ��ѯ����ֵ�еļ��ֵ
				String medApplySql = " SELECT B.TEST_VALUE,A.ORDER_DATE "
						+ "   FROM MED_APPLY A, MED_LIS_RPT B "
						+ "  WHERE A.CAT1_TYPE = B.CAT1_TYPE "
						+ "    AND A.APPLICATION_NO = B.APPLICATION_NO "
						+ "    AND B.TESTITEM_CODE = '"
						+ paraValueParm.getValue("PARAVALUE_1", 0)
						+ "' "
						+ " AND A.ADM_TYPE = '"
						+ typeCode
						+ "'"
						+ " AND A.MR_NO = '"
						+ mrNo
						+ "' "
						+ " AND A.ORDER_DATE IN (SELECT MAX(A.ORDER_DATE)"
						+ " FROM MED_APPLY A, MED_LIS_RPT B "
						+ " WHERE A.CAT1_TYPE = B.CAT1_TYPE "
						+ "    AND B.TESTITEM_CODE = '"
						+ paraValueParm.getValue("PARAVALUE_1", 0)
						+ "' "
						+ "    AND A.MR_NO = '"
						+ mrNo
						+ "' "
						+ " AND A.APPLICATION_NO = B.APPLICATION_NO AND B.test_value IS NOT NULL)"
						+ "AND A.REGION_CODE='" + region
						+ "  ORDER BY B.APPLICATION_NO DESC ";
				// System.out.println("medApplySql2======" + medApplySql);
				TParm medApplyParm = new TParm(TJDODBTool.getInstance().select(
						medApplySql));
				String test = medApplyParm.getValue("TEST_VALUE", 0);
				Timestamp date = medApplyParm.getTimestamp("ORDER_DATE", 0);
				// ��������
				if ("1".equals(limitType)) {
					// System.out.println("���ƽ���");
					// �Ƚ�ֵ
					// System.out.println("���ֵ==========" + test);
					if ((StringTool.getDateDiffer(orderDate, date)) > period_value) {
						System.out.println("ʱ���ѹ�");
					} else {
						String[] str = new String[] { start, end, test };
						int result = lisrisCheck(str);
						// �Ƚ�ֵΪ�����ֻ��ַ�
						if (result == 1) {
							if (start.equals(test) && end.equals(test)) {
								return false;
							}
						}
						// �Ƚ�ֵΪ����
						if (result == 2) {
							double startDouble = Double.parseDouble(start);
							double endDouble = Double.parseDouble(end);
							double testDouble = Double.parseDouble(test);
							if (testDouble >= startDouble
									&& endDouble >= testDouble) {
								return false;
							}
						}
					}
				}
				// �ų�����
				else {
					if ((StringTool.getDateDiffer(orderDate, date)) > period_value) {
						// System.out.println("ʱ���ѹ�");
					} else {
						String[] str = new String[] { start, end, test };
						int result = lisrisCheck(str);
						// �Ƚ�ֵΪ�����ֻ��ַ�
						if (result == 1) {
							if (!start.equals(test) && !end.equals(test)) {
								return false;
							}
						}
						// �Ƚ�ֵΪ����
						if (result == 2) {
							double startDouble = Double.parseDouble(start);
							double endDouble = Double.parseDouble(end);
							double testDouble = Double.parseDouble(test);
							if (!(testDouble >= startDouble && endDouble >= testDouble)) {
								return false;
							}
						}

					}
				}
			}
		}
		return true;
	}

	/**
	 * У�黳��
	 * 
	 * @param admType
	 *            String
	 * @param typeCode
	 *            String
	 * @param mrNo
	 *            String
	 * @return boolean
	 */
	public boolean restritem6(String mrNo, String admType, String typeCode) {
		String sql = " SELECT OPD_FLG, EMG_FLG, INP_FLG "
				+ "   FROM CTR_PANEL " + "  WHERE RESTRITEM_CODE = '06' ";
		TParm flgParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (flgParm == null || flgParm.getCount() < 0) {
			// System.out.println("������Ŀ���в�ѯ���ݴ���!");
		}
		// ���������Ŀ��������
		if (admType.equals("O") && flgParm.getValue("OPD_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������Ŀ��������
		if (admType.equals("E") && flgParm.getValue("EMG_FLG", 0).equals("Y")) {
			typeCode = "O";
		}
		// ���������ĿסԺ����
		if (admType.equals("I") && flgParm.getValue("INP_FLG", 0).equals("Y")) {
			typeCode = "I";
		}
		// ���������Ŀ���ã��š�����ס��
		if (!typeCode.equals("")) {
			String lmpDateSql = " SELECT LMP_DATE FROM SYS_PATINFO "
					+ "  WHERE MR_NO = '" + mrNo + "' ";
			TParm lmpDateParm = new TParm(TJDODBTool.getInstance().select(
					lmpDateSql));
			// LMD_DATE����
			if (lmpDateParm.getData("LMP_DATE", 0) != null
					&& !(StringTool.getDateDiffer(lmpDateParm.getTimestamp(
							"LMP_DATE", 0), SystemTool.getInstance().getDate()) > 0)) {
				return false;
			}
		}
		return true;

	}

	/**
	 * lisrisCheck ����ֵ���ͼ���
	 * 
	 * @param str
	 *            String[]
	 * @return int
	 */
	public int lisrisCheck(String[] str) {
		// �������͡��ַ�����
		if (isChinese(str) || isCharic(str)) {
			return 1;
		}
		// ����
		else if (isNumeric(str)) {
			return 2;

		}
		return 0;
	}

	/*
	 * �ж�������ַ����Ƿ�Ϊ������
	 * 
	 * @param str ������ַ���
	 * 
	 * @return ����Ǵ����ַ���true,���򷵻�false
	 */

	public static boolean isChinese(String[] str) {
		Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+$");
		for (int i = 0; i < str.length; i++) {
			if (!pattern.matcher(str[i]).matches()) {
				return false;
			}
		}
		return true;

	}

	/**
	 * 
	 * �ж�������ַ����Ƿ�Ϊ����
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isNumeric(String[] str) {
		Pattern pattern = Pattern.compile("[0-9]{1,}([.][0-9]{1,}){0,1}");
		for (int i = 0; i < str.length; i++) {
			if (!pattern.matcher(str[i]).matches()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * �ж�������ַ����Ƿ�Ϊ�ַ�
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean isCharic(String[] str) {
		Pattern pattern = Pattern.compile("[a-z A-Z]{1,}");
		for (int i = 0; i < str.length; i++) {
			if (!pattern.matcher(str[i]).matches()) {
				return false;
			}
		}
		return true;

	}

	/**
	 * 
	 * �ж�������ַ����Ƿ�Ϊ�ַ������ֻ��
	 * 
	 * @param str
	 *            String[]
	 * @return boolean
	 */
	public static boolean ismixic(String[] str) {
		Pattern pattern = Pattern
				.compile("[a-z A-Z]{0,}[0-9]{1,}([.][0-9]{1,}){0,1}");
		for (int i = 0; i < str.length; i++) {
			if (!pattern.matcher(str[i]).matches()) {
				return false;
			}
		}
		return true;

	}
}
