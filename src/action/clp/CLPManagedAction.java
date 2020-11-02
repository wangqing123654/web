package action.clp;

import com.dongyang.action.TAction;
import jdo.clp.CLPManagedTool;
import jdo.clp.CLPManagemTool;

import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;

import jdo.opd.TotQtyTool;
import java.util.Map;

/**
 * <p>
 * Title: �ٴ�·��չ��
 * </p>
 * 
 * <p>
 * Description: �ٴ�·��չ��
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
 * @author ¬��
 * @version 1.0
 */
public class CLPManagedAction extends TAction {
	public CLPManagedAction() {
	}

	/**
	 * ��ԭʵ��
	 * 
	 * @param patientTParm
	 *            TParm
	 * @return TParm
	 */
	public TParm returnPractice(TParm basicParm) {
		Map operatorMap = (Map) basicParm.getData("operator");
		TParm patientTParm = new TParm((Map) basicParm.getData("patientTParm"));
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			TParm parmToDel = new TParm();
			parmToDel.setData("CASE_NO", case_no);
			parmToDel.setData("CLNCPATH_CODE", clncpath_code);
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			this.putBasicSysInfoIntoParm(parmToDel, operatorMap);
			result = CLPManagedTool.getInstance()
					.deleteCLPManagedWithPatientInfo(parmToDel, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			CLPManagedTool.getInstance().updateCLPManagedWithPatientInfo(
					parmToDel, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// չ����������
			conn.commit();
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
		}

		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * չ����׼
	 * 
	 * @param inputParm
	 *            TParm
	 * @return TParm
	 */
	public TParm deployStandard(TParm inputParm) {
		TParm patientTParm = new TParm((Map) inputParm.getData("patientTParm"));
		String deployDate = inputParm.getValue("deployDate");
		Map operatorMap = (Map) inputParm.getData("operator");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		if (bilRecpParm.getCount() <= 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			String version = patientTParm.getValue("VERSION", i);// ========pangben
			// 2012-05-22
			// �汾��
			String deptCode = patientTParm.getValue("DEPT_CODE", i);// ���Ҵ���
			boolean isdelflag = deleteManagedHistory(regionCode, case_no,
					clncpath_code, conn, true);
			if (!isdelflag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
			// ��CLP_PACK ���е����ݲ���
			boolean flag = insertManagedFromCLPPack(regionCode, case_no,
					clncpath_code, deployDate, version, deptCode, conn,
					operatorMap);
			if (!flag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
			// չ����������
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
		}
		conn.close();
		return result;
	}

	/**
	 * չ��ʵ��
	 * 
	 * @param patientTParm
	 *            TParm
	 * @return TParm
	 */
	public TParm deployPractice(TParm inputParm) {
		TParm patientTParm = inputParm.getParm("patientTParm");
		String deployDate = inputParm.getValue("deployDate");
		Map operatorMap = (Map) inputParm.getData("operator");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			// ===========pangben 2012-05-22 ��Ӱ汾���Ż�sql
			String version = patientTParm.getValue("VERSION", i);// �汾��
			String deptCode = patientTParm.getValue("DEPT_CODE", i);// ���Ҵ���
			// ===========pangben 2012-05-22 stop
			boolean isdelflag = deleteManagedHistory(regionCode, case_no,
					clncpath_code, conn);
			if (!isdelflag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// 20110907 ¬��ɾ�� begin
			// conn = this.getConnection();
			// 20110907 ¬��ɾ�� end
			// ��CLP_PACK ���е����ݲ���
			boolean flaginsert = insertManagedFromCLPPack(regionCode, case_no,
					clncpath_code, deployDate, version, deptCode, conn,
					operatorMap);
			if (!flaginsert) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// 2011-09-07 ¬��ɾ�� begin
			// conn = this.getConnection();
			// 2011-09-07 ¬��ɾ�� end
			// ��IBS_ORDD ���е����ݲ���
			boolean flaginsertibs = insertManagedFromIBSORDD(regionCode,
					case_no, clncpath_code, deployDate, conn, operatorMap);
			if (!flaginsertibs) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// չ����������
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
		}
		conn.commit();
		conn.close();
		return result;
	}

	private boolean deployBill(TParm bilRecpParm, String regionCode,
			String case_no, String clncpath_code, TConnection conn,
			Map operatorMap) {
		boolean flag = true;

		TParm selectParm = new TParm();
		selectParm.setData("REGION_CODE", regionCode);
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance()
				.selectCLPManagedWithCondition(selectParm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return false;
		}
		// =============pangben 2012-6-1 start
		String[] billName = { "", "" };// ʱ�̱Ƚ���ͬ���ۼӽ��ʹ��
		double[] sum = { 0.00, 0.00 };// ʱ���ۼӵĽ��
		String[] sqlName = { "", "" };// sql �����Ҫ��õķ�����Ŀ
		String[] sqlTotName = { "", "" };// sql �����Ҫ��õķ�����Ŀ���
		sqlName[0] = "";
		sqlTotName[0] = "";
		sum[0] = 0.00;
		sqlName[1] = "";
		sqlTotName[1] = "";
		sum[1] = 0.00;
		// =============pangben 2012-6-1 stop
		if (null != result && result.getValue("SCHD_CODE", 0).length() > 0) {
			billName[0] = result.getValue("SCHD_CODE", 0);
			billName[1] = result.getValue("SCHD_CODE", 0);
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm rowParm = result.getRow(i);
			flag = deployDuringBill(bilRecpParm, rowParm, conn, operatorMap,
					billName, sum, sqlName, sqlTotName, result, i);
			if (!flag) {
				return flag;
			}
		}

		return flag;
	}

	/**
	 * billName//ʱ�̱Ƚ���ͬ���ۼӽ��ʹ�� sum//ʱ���ۼӵĽ�� sqlName//sql �����Ҫ��õķ�����Ŀ
	 * sqlTotName//sql �����Ҫ��õķ�����Ŀ��� ���õ���ʱ����Ϣ��ӻ���µ�CLP_BLL�� =========pangben
	 * 2012-5-24 ��Ӳ��� �Ż�����
	 */
	private boolean deployDuringBill(TParm bilRecpParm, TParm managedParm,
			TConnection conn, Map operatorMap, String[] billName, double[] sum,
			String[] sqlName, String[] sqlTotName, TParm sumParm, int index) {
		boolean flag = true;
		managedParm.setData("OPT_USER", operatorMap.get("OPT_USER"));
		managedParm.setData("OPT_DATE", operatorMap.get("OPT_DATE"));
		managedParm.setData("OPT_TERM", operatorMap.get("OPT_TERM"));
		managedParm.setData("REGION_CODE", operatorMap.get("REGION_CODE"));
		// ��¼��׼��clp_bill
		flag = recordCLPBill(bilRecpParm, managedParm, conn, "1", billName,
				sum, sqlName, sqlTotName, sumParm, index);
		if (!flag) {
			return false;
		}
		// ��¼ʵ�ʵ�clp_bill
		flag = recordCLPBill(bilRecpParm, managedParm, conn, "2", billName,
				sum, sqlName, sqlTotName, sumParm, index);
		if (!flag) {
			return false;
		}
		return flag;
	}

	/**
	 * 
	 * @param bilRecpParm
	 * @param managedParm
	 * @param conn
	 *            billName//ʱ�̱Ƚ���ͬ���ۼӽ��ʹ�� sum//ʱ���ۼӵĽ�� sqlName//sql �����Ҫ��õķ�����Ŀ
	 *            sqlTotName//sql �����Ҫ��õķ�����Ŀ���
	 * @return =================pangben 2012-5-24 ��Ӳ��� �Ż�����
	 */
	private boolean recordCLPBill(TParm bilRecpParm, TParm managedParm,
			TConnection conn, String schdType, String[] billName, double[] sum,
			String[] sqlName, String[] sqlTotName, TParm sumParm, int index) {
		boolean flag = true;
		// �����ʼ����
		managedParm.setData("SCHD_TYPE", schdType);
		if ("1".equals(schdType)) {
			managedParm.setData("CHARGE", managedParm.getDouble("TOT_AMT"));
		} else {
			managedParm.setData("CHARGE", managedParm.getDouble("MAIN_AMT"));
		}
		// TParm stardardCLPBill = this.cloneTParm(managedParm);
		TParm existResult = CLPManagedTool.getInstance().checkCLPBillExist(
				managedParm);
		TParm result = null; // ִ�н��parm
		// String columnName = getColumnNameWithCondition(stardardCLPBill
		// .getValue("ORDER_CODE"));
		String columnName = "";
		// ============pangben 2012-5-24 start ����Ż�����
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (managedParm.getValue("IPD_CHARGE_CODE").equals(
						bilRecpParm.getValue("CHARGE0" + i, 0))) {
					columnName = "REXP_0" + i;
					break;
				}
			} else {
				if (managedParm.getValue("IPD_CHARGE_CODE").equals(
						bilRecpParm.getValue("CHARGE" + i, 0))) {
					columnName = "REXP_" + i;
					break;
				}
			}
		}
		String billNameTemp = "";
		if ("1".equals(schdType)) {
			billNameTemp = billName[0];
		} else {
			billNameTemp = billName[1];
		}
		// ========�ۼƷ�����Ŀ���Ա���Ŀ��ͬ�ۼ�
		if (managedParm.getValue("SCHD_CODE").equals(billNameTemp)) {

			if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
				// ============pangben 2012-6-26 start
				if (columnName.length() > 0) {
					if ("1".equals(schdType)) {

						// ������Ӧ�ķ����в����ۼ�����
						if (!sqlName[0].contains(columnName)) {
							sqlName[0] += " , " + columnName + "="
									+ managedParm.getDouble("CHARGE");
							sum[0] += managedParm.getDouble("CHARGE");
						}
					} else {
						// ������Ӧ�ķ����в����ۼ�����
						if (!sqlName[1].contains(columnName)) {
							sqlName[1] += " , " + columnName + "="
									+ managedParm.getDouble("CHARGE");
							sum[1] += managedParm.getDouble("CHARGE");
						}
					}
				}

			} else {
				if (columnName.length() > 0) {
					if ("1".equals(schdType)) {
						// ������Ӧ�ķ����в����ۼ�����
						if (!sqlName[0].contains(columnName)) {
							sum[0] += managedParm.getDouble("CHARGE");
							sqlName[0] += columnName + ",";
							sqlTotName[0] += managedParm.getDouble("CHARGE")
									+ ",";
						}
					} else {
						// ������Ӧ�ķ����в����ۼ�����
						if (!sqlName[1].contains(columnName)) {
							sum[1] += managedParm.getDouble("CHARGE");
							sqlName[1] += columnName + ",";
							sqlTotName[1] += managedParm.getDouble("CHARGE")
									+ ",";
						}
					}
				}
			}
			// ============pangben 2012-6-26 stop
		} else {
			// ============pangben 2012-5-24 stop

			String sqlNameTemp = "";
			if ("1".equals(schdType)) {
				sqlNameTemp = sqlName[0];
			} else {
				sqlNameTemp = sqlName[1];
			}
			if (this.checkNullAndEmpty(sqlNameTemp)) {
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					result = CLPManagedTool.getInstance().updateCLPBill(
							managedParm, conn, sum, sqlName, schdType,
							billNameTemp);
				} else {
					result = CLPManagedTool.getInstance().saveCLPBill(
							managedParm, conn, sum, sqlName, sqlTotName,
							schdType, billNameTemp);
				}
				if (result.getErrCode() < 0) {
					flag = false;
				}
			}
			// ��׼��ʵ�� �������CLP_BILL ͨ����ͬ���շ���Ŀ��ò�ͬ�Ľ��
			if ("1".equals(schdType)) {
				billName[0] = managedParm.getValue("SCHD_CODE");
				sum[0] = managedParm.getDouble("CHARGE");
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					sqlName[0] = columnName + "="
							+ managedParm.getDouble("CHARGE");
				} else {
					sqlName[0] = columnName + ",";
					sqlTotName[0] = managedParm.getDouble("CHARGE") + ",";
				}
			} else {
				billName[1] = managedParm.getValue("SCHD_CODE");
				sum[1] = managedParm.getDouble("CHARGE");
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					sqlName[1] = columnName + "="
							+ managedParm.getDouble("CHARGE");
				} else {
					sqlName[1] = columnName + ",";
					sqlTotName[1] = managedParm.getDouble("CHARGE") + ",";
				}
			}

		}
		// ���һ��ʱ�̽�����
		if (sumParm.getCount() - 1 == index) {
			if (this.checkNullAndEmpty(managedParm.getValue("SCHD_CODE"))) {
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					result = CLPManagedTool.getInstance().updateCLPBill(
							managedParm, conn, sum, sqlName, schdType,
							managedParm.getValue("SCHD_CODE"));
				} else {
					result = CLPManagedTool.getInstance().saveCLPBill(
							managedParm, conn, sum, sqlName, sqlTotName,
							schdType, managedParm.getValue("SCHD_CODE"));
				}
				if (result.getErrCode() < 0) {
					flag = false;
				}
			}
		}
		return flag;
	}

	private String getColumnNameWithCondition(String orderCode) {
		String columnName = "";
		TParm selectParm = new TParm();
		selectParm.setData("ORDER_CODE", orderCode);
		TParm result = CLPManagedTool.getInstance().getCLPBillColumName(
				selectParm);
		if (result.getCount() > 0) {
			columnName = result.getValue("COLUMNNAME", 0);
		}
		return columnName;
	}

	/**
	 * ɾ����չ����������
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @return type 1:��׼ 2��ʵ�� 3��ȫ��
	 * @return boolean
	 * 
	 */
	private boolean deleteCLPBill(String regionCode, String case_no,
			String clncpath_code, TConnection conn, int type) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		if (1 == type) {
			parmDelete.setData("SCHD_TYPE", 1);
		} else if (2 == type) {
			parmDelete.setData("SCHD_TYPE", 2);
		}
		TParm result = CLPManagedTool.getInstance().deleteCLPBill(parmDelete,
				conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * ɾ���Ѿ�չ������ʷ����
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param mr_no
	 *            String
	 * @return boolean
	 */
	private boolean deleteManagedHistory(String regionCode, String case_no,
			String clncpath_code, TConnection conn) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance().deleteCLPManaged(
				parmDelete, conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * ɾ��CLPPack��Ϣ����standardFlag
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @param conn
	 *            TConnection
	 * @param isFlag
	 *            boolean
	 * @return boolean
	 */
	private boolean deleteManagedHistory(String regionCode, String case_no,
			String clncpath_code, TConnection conn, boolean standardFlag) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		parmDelete.setData("STANDARD_FLG", standardFlag ? "Y" : "N");
		TParm result = CLPManagedTool.getInstance().deleteCLPManaged(
				parmDelete, conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * ����CLPManaged From CLPPack
	 * 
	 * @return boolean ============pangben 20120522 ��Ӱ汾�š����Ҵ������
	 * 
	 */
	private boolean insertManagedFromCLPPack(String regionCode, String case_no,
			String clncpath_code, String deployDate, String version,
			String deptCode, TConnection conn, Map operatorMap) {
		boolean flag = true;
		TParm clpPackTParm = new TParm();
		clpPackTParm.setData("CLNCPATH_CODE", clncpath_code);
		clpPackTParm.setData("REGION_CODE", regionCode);
		clpPackTParm.setData("CASE_NO", case_no);
		clpPackTParm.setData("VERSION", version);

		// չ��ʱ��׼������Ҫ����version ���д���
		// TParm clpresult = CLPManagedTool.getInstance().
		// selectManagedInfoFromCLPPack(clpPackTParm);
		TParm clpresult = CLPManagedTool.getInstance()
				.selectManagedInfoFromCLPPackWithVersion(clpPackTParm);
		// pangben 2012-05-22 start ���������
		String sql = "SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM CLP_MANAGED "
				+ " WHERE CASE_NO='" + case_no + "' AND CLNCPATH_CODE='"
				+ clncpath_code + "'";
		TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
		int seq = 0;
		if (null != maxSeqParm.getValue("ORDER_SEQ", 0)) {
			seq = maxSeqParm.getInt("ORDER_SEQ", 0);
		}
		// pangben 2012-05-22 stop
		for (int i = 0; i < clpresult.getCount(); i++) {
			TParm parm = clpresult.getRow(i);
			this.putBasicSysInfoIntoParm(parm, operatorMap);
			parm.setData("CASE_NO", case_no);
			parm.setData("CLNCPATH_CODE", clncpath_code);
			parm.setData("STANDING_DTTM", deployDate);
			// ����EXEC_FLG
			if (parm.getData("EXEC_FLG") == null) {
				TNull tnull = new TNull(String.class);
				parm.setData("EXEC_FLG", tnull);
			}
			// ��������
			// ��������λ��Ƶ�Σ��շ�
			// System.out.println("��ѯCLPPACK������");
			double dose = parm.getDouble("DOSE");
			int doseDays = parm.getInt("DOSE_DAYS");
			double freqTimes = parm.getDouble("FREQ_TIMES");// ======pangben
			double dosageQty =0.00; 
			//�жϴ�ҽ���Ƿ���ҩƷ
			if (null!=parm.getValue("MEDI_QTY") &&null!=parm.getValue("DOSAGE_UNIT")&&parm.getValue("DOSAGE_UNIT").length()>0) {
				String tranSql = "SELECT MEDI_QTY FROM PHA_TRANSUNIT "
					+ " WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
				TParm tranParm = new TParm(TJDODBTool.getInstance().select(tranSql));//����ֵ�����===pangben 2015-7-8
				double mediQty=tranParm.getDouble("MEDI_QTY",0);//��ҩ��׼��
				if (mediQty==0) {
					dosageQty=0.00;
				}else{
					dosageQty=StringTool.round(dose/mediQty * freqTimes * doseDays, 4);
				}
				parm.setData("DOSE_UNIT",parm.getValue("DOSAGE_UNIT"));//ҩƷ��ҩ��λ
			}else{
				//����ҽ������
				parm.setData("DOSE_UNIT",parm.getValue("UNIT_CODE"));//����ҽ����λ
				dosageQty=StringTool.round(dose * freqTimes * doseDays, 4);
			}
//			if (null!=parm.getValue("ORDERSET_FLG")&&parm.getValue("ORDERSET_FLG").equals("Y")) {
//				
//			}else{
//				
//			}
			// 2012-05-22Ƶ��
			// System.out.println("��ѯ��������:" + dose);
			// System.out.println("��λ:" + doseUnit);
			// System.out.println("Ƶ��" + freqCode);
			// System.out.println("��ѯ�����շ�:" + doseDays);
			// TParm totalAndFeesParm = TotQtyTool.getIbsQty(dose, doseUnit,
			// freqCode,
			// doseDays);
			// System.out.println("�����������:" +
			// totalAndFeesParm.getDouble("DOSAGE_QTY"));
			// parm.setData("TOT", totalAndFeesParm.getDouble("DOSAGE_QTY"));
			// TParm ownPriceParm = CLPManagedTool.getInstance().selectOwnPrice(
			// this.cloneTParm(parm), conn);//---

			
			double ownPrice = parm.getDouble("OWN_PRICE");// ======pangben
			// 2012-05-22 �޸�
			double totAmt = ownPrice * dosageQty;
			totAmt = StringTool.round(totAmt, 2);
			parm.setData("TOT", dosageQty);
			parm.setData("TOT_AMT", totAmt);
			// ����ORDER_NO
			parm.setData("ORDER_NO", SystemTool.getInstance().getNo("ALL",
					"CLP", "CLP", "ORDERNO"));
			parm.setData("ORDER_SEQ", seq);// ====pangben 2012-05-22 ������
			parm.setData("DEPT_CODE", deptCode);// ====pangben 2012-05-22 ���Ҵ���
			TParm result = CLPManagedTool.getInstance()
					.inserttCLPManagedWithCLPPack(parm, conn);// �Ķ�
			if (result.getErrCode() < 0) {
				flag = false;
			}
			seq++;
		}
		return flag;
	}

	/**
	 * ���� IBSORDD From CLPPack
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @return boolean
	 */
	private boolean insertManagedFromIBSORDD(String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean flag = true;
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance()
				.selectManagedInfoFromIBSOrdd(parm);
		// ��һ�α���ѭ��������ѯ������Ҫд��ʵ����Ŀ�е����ݽ�����ȫƥ�䣬ֻ��orderCode=mainOrderCode�����ݲ��ܽ��кϲ�
		for (int i = 0; i < result.getCount(); i++) {
			TParm ibsParm = result.getRow(i);
			insertIntoManagedWithIBSOrdddSelectDataWithSameData(ibsParm,
					regionCode, case_no, clncpath_code, deployDate, conn,
					operatorMap);
		}
		// �ڶ���ѭ��������һ��orderCode=mainOrderCode
		// ��û��ƥ���ϵ����ݽ��д����ҳ�����ͬһ���͵����ݽ��кϲ�����ʵ�����������
		// ��׼����ƥ������ϲ���ѯ���ĵ�һ������
		conn.commit();
//		result = CLPManagedTool.getInstance()
//				.selectCLPManaedFromIBSOrddTwo(parm);
		result = CLPManagedTool.getInstance()
				.selectManagedInfoFromIBSOrdd(parm);
//		if (result.getCount("CASE_NO")>0) {
//			
//		}else{
//			return true;
//		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm ibsParm = result.getRow(i);
			insertIntoManagedWithIBSOrdddSelectDataWithNotSameData(ibsParm,
					regionCode, case_no, clncpath_code, deployDate, conn,
					operatorMap);
		}

		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * ����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpManaged�� ��ȫʹ��orderCode=mainCoderCode ����ƥ��
	 * 
	 * @param selectData
	 *            TParm
	 */
	private boolean insertIntoManagedWithIBSOrdddSelectDataWithSameData(
			TParm selectData, String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean isExist = CLPManagedTool.getInstance()
				.checkIBSOrddExistInCLPManaged(case_no, clncpath_code,
						selectData.getValue("SCHD_CODE"),
						selectData.getValue("ORDER_CODE"),
						selectData.getValue("DOSAGE_UNIT"),
						selectData.getValue("QTY"));
		// ��������begin
		selectData.setData("CASE_NO", case_no);
		selectData.setData("CLNCPATH_CODE", clncpath_code);
		selectData.setData("STANDING_DTTM", deployDate);
		this.putBasicSysInfoIntoParm(selectData, operatorMap);
		putTNull(selectData, "EXEC_USER", String.class);
		putTNull(selectData, "PROGRESS_CODE", String.class);
		putTNull(selectData, "MEDICAL_NOTE", String.class);
		// ��������end
		TParm result = null;
		if (isExist) {
			// ���ҳ���Ҫ���µĲ�����Ϣ
			TParm patientParm = new TParm();
			patientParm.setData("CASE_NO", case_no);
			patientParm.setData("CLNCPATH_CODE", clncpath_code);
			patientParm.setData("SCHD_CODE", selectData.getValue("SCHD_CODE"));
			patientParm
					.setData("ORDER_CODE", selectData.getValue("ORDER_CODE"));
			patientParm.setData("MEDI_UNIT", selectData.getValue("DOSAGE_UNIT"));
			TParm updateData = CLPManagedTool.getInstance()
					.getUpdateDataInCLPManagedWithPatienInfo(patientParm);
			// System.out.println("��Ҫ���µ�����" + updateData);
			// ����ʵ�����ݱ�����׼��ordertype���ϵ�������ordertype������ordercode���ϵĻ����ȸ���
			// orderType��orderCode����ͬ������
			for (int i = 0; i < updateData.getCount(); i++) {
				TParm updateDatatmp = updateData.getRow(i);
				// ��������¼��orderCode��ͬ������orderERTYPE���Ӧ��QTY��UNIT
				// Ҳ��һ���ġ��ڱȶ���ͬorderCode���кϲ�ʱ�������ж�
				// QTY��UNIT �ıȶ���Ҫ������������ͬһ�ٴ�·����Ŀ�Ĳ�ͬorder��ıȶԣ�����ҪorderCode����ͬһ����Ŀ
				// ����������orderCode���ٴ�·����λ���ٴ�·������ҲҪ��ͬ��
				if (updateDatatmp.getValue("ORDER_CODE").equals(
						selectData.getValue("ORDER_CODE"))) {
					String mainCode = updateDatatmp.getValue("MAINORD_CODE");
					// �Ѿ�����ʵ�����ݵ�managed��ļ�¼�����и���
					if (this.checkNullAndEmpty(mainCode)
							|| selectData.getValue("ORDER_CODE").equals(
									mainCode)) {
						continue;
					}
					// ������Ҫ���µ�����
					selectData.setData("ORDER_NO", updateDatatmp
							.getData("ORDER_NO"));
					selectData.setData("ORDER_SEQ", updateDatatmp
							.getData("ORDER_SEQ"));
					//����ҽ��û��ϸ�� ����ʾ���� ����
					//==========pangben 2012-7-6 
					if (null!=updateDatatmp.getValue("ORDERSET_FLG") && "Y".equals(updateDatatmp.getValue("ORDERSET_FLG"))) {
						result = CLPManagedTool.getInstance()
						.updateCLPManagedWithIBSOrddOrderSet(selectData, conn);
					}else{
						result = CLPManagedTool.getInstance()
						.updateCLPManagedWithIBSOrdd(selectData, conn);
					}
					if (result.getErrCode() < 0) {
						return false;
					}
				}
				// conn.commit(); // �ύ�Ա��´��ж��ܹ������¼����ֵ
			}
		}else{
			
		}
		if (result == null) {
			return true;
		}
		if (result.getErrCode() < 0) {
			// System.out.println("����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpmanagedʧ��");
			return false;
		} else {
			// System.out.println("����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpmanaged�ɹ�");
			return true;
		}
	}

	/**
	 * ����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpManaged��(����OrderCode����ʱ�����ٴ�·������з���)
	 * ��orderCode=mainOrderCode û��ƥ������ݽ��ж���ƥ�䣬ƥ������ΪmainOrderCode
	 * ��orderCodeͬ����һ��type
	 * 
	 * @param selectData
	 *            TParm
	 */
	private boolean insertIntoManagedWithIBSOrdddSelectDataWithNotSameData(
			TParm selectData, String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean isExist = CLPManagedTool.getInstance()
				.checkIBSOrddExistInCLPManaged(case_no, clncpath_code,
						selectData.getValue("SCHD_CODE"),
						selectData.getValue("ORDER_CODE"),
						selectData.getValue("DOSAGE_UNIT"),
						selectData.getValue("QTY"));
		// ��������begin
		selectData.setData("CASE_NO", case_no);
		selectData.setData("CLNCPATH_CODE", clncpath_code);
		selectData.setData("STANDING_DTTM", deployDate);
		this.putBasicSysInfoIntoParm(selectData, operatorMap);
		putTNull(selectData, "EXEC_USER", String.class);
		putTNull(selectData, "PROGRESS_CODE", String.class);
		putTNull(selectData, "MEDICAL_NOTE", String.class);
		// ��������end
		TParm result = null;
		if (isExist) {
			// ���ҳ���Ҫ���µĲ�����Ϣ
			TParm patientParm = new TParm();
			patientParm.setData("CASE_NO", case_no);
			patientParm.setData("CLNCPATH_CODE", clncpath_code);
			patientParm.setData("SCHD_CODE", selectData.getValue("SCHD_CODE"));
			patientParm
					.setData("ORDER_CODE", selectData.getValue("ORDER_CODE"));
			patientParm.setData("MEDI_UNIT", selectData.getValue("DOSAGE_UNIT"));
			TParm updateData = CLPManagedTool.getInstance()
					.getUpdateDataInCLPManagedWithPatienInfo(patientParm);
			// System.out.println("��Ҫ���µ�����" + updateData);
			// ����ʵ�����ݱ�����׼��ordertype���ϵ�������ordertype������ordercode���ϵĻ����ȸ���
			// orderType��orderCode����ͬ������
			boolean ishasSave = false;
			// �ж������Ƿ��Ѿ�����һ�ε�ѭ���д��������������򲻽��д���
			for (int i = 0; i < updateData.getCount(); i++) {
				TParm updateDatatmp = updateData.getRow(i);
				String mainCode = updateDatatmp.getValue("MAINORD_CODE");
				// ���Ը��µı�׼�����е�mainOrderCode�����ʵ�ʵ����ݵ�orderCode��ͬʱ˵���ļ�¼�Ѿ�����һ��
				// չ��ʱչ���������д���������ʶ��¼Ϊtrue��
				if (selectData.getValue("ORDER_CODE")
						.equalsIgnoreCase(mainCode)) {
					ishasSave = true;
					continue;
				}
			}
			if (!ishasSave) {
				for (int i = 0; i < updateData.getCount(); i++) {
					TParm updateDatatmp = updateData.getRow(i);
					// ¬�� 2011-07-20 �޸� ������i==0 �Ķ���ishasSave ==false
					// ��ֻ��û�и��µ����ݣ��Ž��и��£����º󽫱�ʾishasSave���ó�true
					// ��ֹ������i=0 ����һ����¼mainCode����ֵ�����
					if (ishasSave == false) {
						String mainCode = updateDatatmp
								.getValue("MAINORD_CODE");
						// ����׼�������Ѿ�����mainCode ����µ�ʵ����������֮ƥ�䵫������׼�����Ѿ���¼��ϣ������и��²���
						if (this.checkNullAndEmpty(mainCode)) {
							continue;
						}
						// ¬�� 2011-07-20 �����ж� begin
						// ����orderCode ����ȫƥ��ʱ������Ҫ��֤�滻��orderCode��ԭorderCode
						// �Ƿ�����ͬһ���ٴ�·����Ŀ
						// ���滻orderCode���ٴ�·����λ���ٴ�·������ҲҪ�����ж�
						String orderCode = updateDatatmp.getValue("ORDER_CODE");
						String mainOrderCode = selectData
								.getValue("ORDER_CODE");
						boolean isSame = CLPManagedTool.getInstance()
								.checkOrderIsValid(orderCode, mainOrderCode);
						if (!isSame) {
							continue;
						}
						// ¬�� 2011-07-20 �����ж� end
						// ������Ҫ���µ�����
						selectData.setData("ORDER_NO", updateDatatmp
								.getData("ORDER_NO"));
						selectData.setData("ORDER_SEQ", updateDatatmp
								.getData("ORDER_SEQ"));
						//==========pangben 2012-7-6 
						if (null!=updateDatatmp.getValue("ORDERSET_FLG") && "Y".equals(updateDatatmp.getValue("ORDERSET_FLG"))) {
							result = CLPManagedTool.getInstance()
							.updateCLPManagedWithIBSOrddOrderSet(selectData, conn);
						}else{
							result = CLPManagedTool.getInstance()
							.updateCLPManagedWithIBSOrdd(selectData, conn);
						}
						ishasSave = true;
					} else {
						// �������������һ��ʵ�ʼ�¼��Ӧ������׼���ݵ����������ʵ�����ݺϲ���
						// ��ѯ�ĵ�һ�������У�����ı�׼��������
					}
					conn.commit(); // �ύ�Ա��´��ж��ܹ������¼����ֵ
				}
			}
			// ������ͬorderCode���и��º�orderCode��ͬʱ���¶�û��ƥ�������£����������ӵ�����
			// ����������
			if (!ishasSave) {
				// System.out.println(
				// "������ͬorderCode���и��º�orderCode��ͬʱ���¶�û��ƥ�������£����������ӵ����ݽ���������");
				// ����ORDER_NO
				selectData.setData("ORDER_NO", SystemTool.getInstance().getNo(
						"ALL", "CLP", "CLP", "ORDERNO"));
				// ===========pangben 2012-5-30 �������
				TParm maxSeqParm = maxSEQ(case_no, clncpath_code);
				selectData.setData("TOT_AMT", 0.00);// �ܽ��
				//selectData.setData("TOT_AMT", selectData.getDouble("MAIN_AMT"));// �ܽ��

				if (maxSeqParm.getErrCode() < 0) {
					return false;
				}
				selectData.setData("ORDER_SEQ", maxSeqParm.getInt("ORDER_SEQ",
						0) + 1);
				result = CLPManagedTool.getInstance()
						.insertCLPManagedWithIBSOrdd(selectData, conn);
				conn.commit(); // �ύ�Ա��´��ж��ܹ������¼����ֵ
			}
		} else {
			// System.out.println("managed�����ݲ����ڲ���");
			// ����ORDER_NO
			selectData.setData("ORDER_NO", SystemTool.getInstance().getNo(
					"ALL", "CLP", "CLP", "ORDERNO"));
			// ===========pangben 2012-5-30 �������
			TParm maxSeqParm = maxSEQ(case_no, clncpath_code);
			if (maxSeqParm.getErrCode() < 0) {
				return false;
			}
			selectData.setData("TOT_AMT", 0.00);// �ܽ��
			//selectData.setData("TOT_AMT", selectData.getDouble("MAIN_AMT"));
			selectData.setData("ORDER_SEQ",
					maxSeqParm.getInt("ORDER_SEQ", 0) + 1);
			result = CLPManagedTool.getInstance().insertCLPManagedWithIBSOrdd(
					selectData, conn);
		}
		if (result == null) {
			return true;
		}
		if (result.getErrCode() < 0) {
			// System.out.println("����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpmanagedʧ��");
			return false;
		} else {
			// System.out.println("����IBSOrdd��ѯ�������ݸ��ݲ�ͬ��������뵽clpmanaged�ɹ�");
			return true;
		}
	}

	/**
	 * �������
	 * 
	 * @param case_no
	 * @param clncpath_code
	 * @return ===========pangben 2012-5-30
	 */
	private TParm maxSEQ(String case_no, String clncpath_code) {
		String sql = "SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM CLP_MANAGED "
				+ " WHERE CASE_NO='" + case_no + "' AND CLNCPATH_CODE='"
				+ clncpath_code + "'";
		TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
		return maxSeqParm;
	}

	/**
	 * ��TParm�м���ϵͳĬ����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm, Map OperatorMap) {
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", OperatorMap.get("REGION_CODE"));
		parm.setData("OPT_USER", OperatorMap.get("OPT_USER"));
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", OperatorMap.get("OPT_TERM"));
	}

	/**
	 * ����TParm ���null�ķ���
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNullVector(TParm parm, String keyStr, Class type) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getData(keyStr, i) == null) {
				// System.out.println("����Ϊ�����");
				TNull tnull = new TNull(type);
				parm.setData(keyStr, i, tnull);
			}
		}
	}

	/**
	 * ����TParm ��nullֵ����
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNull(TParm parm, String keyStr, Class type) {
		if (parm.getData(keyStr) == null) {
			// System.out.println("����Ϊ�����");
			TNull tnull = new TNull(type);
			parm.setData(keyStr, tnull);
		}
	}

	/**
	 * ����Ƿ�Ϊ�ջ�մ�
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * ��¡����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// private TParm cloneTParm(TParm from) {
	// TParm returnTParm = new TParm();
	// for (int i = 0; i < from.getNames().length; i++) {
	// returnTParm.setData(from.getNames()[i], from.getData(from
	// .getNames()[i]));
	// }
	// return returnTParm;
	// }

}
