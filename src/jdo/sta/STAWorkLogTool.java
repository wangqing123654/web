package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ��������
 * </p>
 * 
 * <p>
 * Description: ��������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-21
 * @version 1.0
 */
public class STAWorkLogTool extends TJDOTool {

	private String selectDayData = "SELECT "
			+ " SUM(B.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM,SUM(B.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM,SUM(B.OUYCHK_INOUT) AS OUYCHK_INOUT,SUM(B.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF,SUM(B.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, "
			+ " SUM(B.HEAL_LV_BAD) AS HEAL_LV_BAD,SUM(B.GET_TIMES) AS GET_TIMES,SUM(B.SUCCESS_TIMES) AS SUCCESS_TIMES,SUM(A.DATA_01) AS DATA_01, SUM(A.DATA_02) AS DATA_02, SUM(A.DATA_03) AS DATA_03, SUM(A.DATA_04) AS DATA_04, "
			+ " SUM(A.DATA_05) AS DATA_05, SUM(A.DATA_06) AS DATA_06, SUM(A.DATA_06_1) AS DATA_06_1, SUM(A.DATA_07) AS DATA_07, SUM(A.DATA_08) AS DATA_08, "
			+ " SUM(A.DATA_08_1) AS DATA_08_1, SUM(A.DATA_09) AS DATA_09, SUM(A.DATA_10) AS DATA_10, SUM(A.DATA_11) AS DATA_11, SUM(A.DATA_12) AS DATA_12, "
			+ " SUM(A.DATA_13) AS DATA_13, SUM(A.DATA_14) AS DATA_14, SUM(A.DATA_15) AS DATA_15, SUM(A.DATA_15_1) AS DATA_15_1, SUM(A.DATA_16) AS DATA_16,"
			+ " SUM(A.DATA_19) AS DATA_19, SUM(A.DATA_20) AS DATA_20, SUM(A.DATA_21) AS DATA_21, "
			+ " SUM(A.DATA_22) AS DATA_22, A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG"
			+ " FROM STA_DAILY_01 A,STA_STATION_DAILY B "
			+ " WHERE  A.DEPT_CODE=B.DEPT_CODE AND A.STATION_CODE=B.STATION_CODE " + " AND A.STA_DATE=B.STA_DATE";

	private String selectStationMData = "SELECT "
			+ " SUM(B.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM,SUM(B.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM,SUM(B.OUYCHK_INOUT) AS OUYCHK_INOUT,SUM(B.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF,SUM(B.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, "
			+ " SUM(B.HEAL_LV_BAD) AS HEAL_LV_BAD,SUM(B.GET_TIMES) AS GET_TIMES,SUM(B.SUCCESS_TIMES) AS SUCCESS_TIMES,SUM(A.DATA_01) AS DATA_01, SUM(A.DATA_02) AS DATA_02, SUM(A.DATA_03) AS DATA_03, SUM(A.DATA_04) AS DATA_04, "
			+ " SUM(A.DATA_05) AS DATA_05, SUM(A.DATA_06) AS DATA_06, SUM(A.DATA_06_1) AS DATA_06_1, SUM(A.DATA_07) AS DATA_07, SUM(A.DATA_08) AS DATA_08, "
			+ " SUM(A.DATA_08_1) AS DATA_08_1, SUM(A.DATA_09) AS DATA_09, SUM(A.DATA_10) AS DATA_10, SUM(A.DATA_11) AS DATA_11, SUM(A.DATA_12) AS DATA_12, "
			+ " SUM(A.DATA_13) AS DATA_13, SUM(A.DATA_14) AS DATA_14, SUM(A.DATA_15) AS DATA_15, SUM(A.DATA_15_1) AS DATA_15_1, SUM(A.DATA_16) AS DATA_16,"
			+ " SUM(A.DATA_19) AS DATA_19, SUM(A.DATA_20) AS DATA_20, SUM(A.DATA_21) AS DATA_21, "
			+ " SUM(A.DATA_22) AS DATA_22, A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG"
			+ " FROM STA_DAILY_01 A,STA_STATION_DAILY B "
			+ " WHERE  A.DEPT_CODE=B.DEPT_CODE  AND A.STATION_CODE=B.STATION_CODE " 
			+ " AND A.STA_DATE=B.STA_DATE";

	private String selectOpdMData = "SELECT SUM(OUTP_NUM) AS OUTP_NUM,SUM(ERD_NUM) AS ERD_NUM,SUM(ERD_DIED_NUM) AS ERD_DIED_NUM,SUM(OBS_NUM) AS OBS_NUM,SUM(OBS_DIED_NUM) AS OBS_DIED_NUM, "
			+ " DEPT_CODE " + " FROM STA_OPD_DAILY ";
	/**
	 * ʵ��
	 */
	public static STAWorkLogTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static STAWorkLogTool getInstance() {
		if (instanceObject == null)
			instanceObject = new STAWorkLogTool();
		return instanceObject;
	}

	public STAWorkLogTool() {
		setModuleName("sta\\STAWorkLogModule.x");
		onInit();
	}

	/**
	 * ���� STA_DAILY_02(��������) ���� ���в���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdate(TParm parm, TConnection conn) {
		TParm result = this.update("insertData", parm, conn);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���� STA_DAILY_02(��������) ���� ���в���
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertDaily(TParm Dailyparm, TConnection conn) {
		String STA_DATE = Dailyparm.getValue("STA_DATE"); // ɾ�����ݲ���
		TParm parm = Dailyparm.getParm("Daily"); // �������ݲ���
		TParm result = new TParm();
		// ==================pangben modify 20110520 ����������
		result = deleteSTA_DAILY_02(STA_DATE,
				Dailyparm.getValue("REGION_CODE"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ��������������״̬
		// =======pangben modify 20110519 start ����������
		int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY",
				STA_DATE, Dailyparm.getValue("REGION_CODE"));
		// =======pangben modify 20110519 stop
		TParm parm1 = new TParm();
		for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
			parm1.setData("STA_DATE", parm.getValue("STA_DATE", i));
			parm1.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			parm1.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
			parm1.setData("DATA_01", parm.getValue("DATA_01", i));
			parm1.setData("DATA_02", parm.getValue("DATA_02", i));
			parm1.setData("DATA_03", parm.getValue("DATA_03", i));
			parm1.setData("DATA_04", parm.getValue("DATA_04", i));
			parm1.setData("DATA_05", parm.getValue("DATA_05", i));
			parm1.setData("DATA_06", parm.getValue("DATA_06", i));
			parm1.setData("DATA_07", parm.getValue("DATA_07", i));
			parm1.setData("DATA_08", parm.getValue("DATA_08", i));
			parm1.setData("DATA_08_1", parm.getValue("DATA_08_1", i));
			parm1.setData("DATA_09", parm.getValue("DATA_09", i));
			parm1.setData("DATA_10", parm.getValue("DATA_10", i));
			parm1.setData("DATA_11", parm.getValue("DATA_11", i));
			parm1.setData("DATA_12", parm.getValue("DATA_12", i));
			parm1.setData("DATA_13", parm.getValue("DATA_13", i));
			parm1.setData("DATA_14", parm.getValue("DATA_14", i));
			parm1.setData("DATA_15", parm.getValue("DATA_15", i));
			parm1.setData("DATA_15_1", parm.getValue("DATA_15_1", i));
			parm1.setData("DATA_16", parm.getValue("DATA_16", i));
			parm1.setData("DATA_17", parm.getValue("DATA_17", i));
			parm1.setData("DATA_18", parm.getValue("DATA_18", i));
			parm1.setData("DATA_19", parm.getValue("DATA_19", i));
			parm1.setData("DATA_20", parm.getValue("DATA_20", i));
			parm1.setData("DATA_21", parm.getValue("DATA_21", i));
			parm1.setData("DATA_22", parm.getValue("DATA_22", i));
			parm1.setData("DATA_23", parm.getValue("DATA_23", i));
			parm1.setData("DATA_24", parm.getValue("DATA_24", i));
			parm1.setData("DATA_25", parm.getValue("DATA_25", i));
			parm1.setData("DATA_26", parm.getValue("DATA_26", i));
			parm1.setData("DATA_27", parm.getValue("DATA_27", i));
			parm1.setData("DATA_28", parm.getValue("DATA_28", i));
			parm1.setData("DATA_29", parm.getValue("DATA_29", i));
			parm1.setData("DATA_30", parm.getValue("DATA_30", i));
			parm1.setData("DATA_31", parm.getValue("DATA_31", i));
			parm1.setData("DATA_32", parm.getValue("DATA_32", i));
			parm1.setData("DATA_33", parm.getValue("DATA_33", i));
			parm1.setData("DATA_34", parm.getValue("DATA_34", i));
			parm1.setData("DATA_35", parm.getValue("DATA_35", i));
			parm1.setData("DATA_36", parm.getValue("DATA_36", i));
			parm1.setData("DATA_37", parm.getValue("DATA_37", i));
			parm1.setData("DATA_38", parm.getValue("DATA_38", i));
			parm1.setData("DATA_39", parm.getValue("DATA_39", i));
			parm1.setData("DATA_40", parm.getValue("DATA_40", i));
			parm1.setData("DATA_41", parm.getValue("DATA_41", i));
			parm1.setData("DATA_41_1", parm.getValue("DATA_41_1", i));
			parm1.setData("DATA_41_2", parm.getValue("DATA_41_2", i));
			parm1.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
			parm1.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
			parm1.setData("OPT_USER", parm.getValue("OPT_USER", i));
			parm1.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// ============pangben modify 20110519 start
			parm1.setData("REGION_CODE", Dailyparm.getValue("REGION_CODE"));
			// ============pangben modify 20110519 stop
			if (reFlg == 2 && parm.getValue("SUBMIT_FLG", i).equals("Y"))// �жϲ�����־��������־���Ѿ��ύ
				parm1.setData("SUBMIT_FLG", "Y");
			else
				// ������һ��δ�ύ�ľͽ��ύ�������ΪN
				parm1.setData("SUBMIT_FLG", "N");
			result = this.insertdate(parm1, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ��ȡ�м�������3������
	 * 
	 * @return TParm ===========pangben modify 20110525 ����������
	 */
	public TParm selectDEPT(String regionCode) {
		// ===========pangben modify 20110525 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===========pangben modify 20110525 stop
		TParm result = this.query("selectDEPT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ�м�������3��4������
	 * 
	 * @return TParm
	 */
	public TParm selectDEPT_4(TParm parm) {
		TParm result = this.query("selectDEPT_4", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����STA_DATE ��ѯ STA_DAILY_02�����Ƿ��Ѿ����ڸ����ڵ�����
	 * 
	 * @param STA_DATE
	 *            String
	 * @param regionCode
	 *            String
	 * @return TParm =====pangben modify 20110519 ����������
	 */
	public TParm checkNum(String STA_DATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// =====pangben modify 20110519 strat
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// =====pangben modify 20110519 stop
		TParm result = this.query("checkNum", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��STA_DAILY_02������
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ========pangben modify 20110520 ����������
	 */
	public TParm deleteSTA_DAILY_02(String STA_DATE, String regionCode,
			TConnection conn) {
		TParm parm = new TParm();
		// ========pangben modify 20110520 start
		parm.setData("STA_DATE", STA_DATE);
		parm.setData("REGION_CODE", regionCode);
		// ========pangben modify 20110520 stop
		TParm result = this.update("deleteSTA_DAILY_02", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ɾ��STA_DAILY_02������
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ========pangben modify 20110520 ����������
	 */
	public TParm updateSTA_DAILY_02(TParm  parm) {
		// ========pangben modify 20110520 stop
		TParm result = this.update("updateSTA_DAILY_02", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}


	/**
	 * ��ѯסԺ�������(�ձ�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStation(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "ȱ�ٱ�Ҫ������STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start ����������
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND B.REGION_CODE='" + parm.getValue("REGION_CODE")
					+ "' ";
		String deptCode = "";
		if (null != parm.getValue("DEPT_CODE")
				&& parm.getValue("DEPT_CODE").length() > 0)
			deptCode = " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE")
					+ "' ";
		// ============pangben modify 20110520 stop

		String sql = this.selectDayData + " AND A.STA_DATE='"
				+ parm.getValue("STA_DATE") + "' " + region
				+ " GROUP BY A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �ϲ���ͬ���Ҳ�ͬ�����Ĳ�������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSetStation(TParm parm) {
		if (parm.getCount() <= 0)
			return parm;
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);
			double BED_RETUEN = countBedReturn(parmRow.getInt("DATA_09"),
					parmRow.getInt("DATA_17")); // ��ת���� ƽ�����Ų�����(���ڵ�����ĩʵ�в�����)
			double i_BED_WORK_DAY = parmRow.getInt("DATA_17");// ����������
																// ÿ��ÿ��Ϊ1�������գ�ÿ�쵼��Ĺ�����Ӧ�õ�����ĩʵ�в�����<17>
			double i_BED_USE_RATE = 0;
			if (parmRow.getInt("DATA_17") != 0)
				i_BED_USE_RATE = (double) parmRow.getInt("DATA_16")
						/ (double) parmRow.getInt("DATA_17") * 100; // ����ʹ����=ʵ��ռ�ò�����/���Ų�����
			else
				i_BED_USE_RATE = 0;
			parm.setData("BED_RETUEN", i, BED_RETUEN);
			parm.setData("BED_WORK_DAY", i, i_BED_WORK_DAY);
			parm.setData("BED_USE_RATE", i, i_BED_USE_RATE);

		}
		return parm;
	}

	/**
	 * ��ѯ�ż����������(�ձ�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPD(TParm parm) {
		TParm result = this.query("selectOPD", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯסԺ�������(�±�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStation_M(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "ȱ�ٱ�Ҫ������STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start ����������
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND B.REGION_CODE='" + parm.getValue("REGION_CODE")
					+ "' ";
		// ============pangben modify 20110520 stop

		String sql = selectStationMData + " AND A.STA_DATE LIKE '"
				+ parm.getValue("STA_DATE") + "%' " + region
				+ " GROUP BY A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�ż����������(�±�)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPD_M(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "ȱ�ٱ�Ҫ������STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start ����������
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND REGION_CODE='" + parm.getValue("REGION_CODE") + "' ";
		// ============pangben modify 20110520 stop
		String sql = selectOpdMData + " WHERE STA_DATE LIKE '"
				+ parm.getValue("STA_DATE") + "%' " + region
				+ " GROUP BY DEPT_CODE";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ�ձ�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectDataDay(TParm parm) {
		TParm reOPD = this.selectOPD(parm); // ��ȡ�����ż����м������
		// System.out.println("�����ż����м������------------"+reOPD);
		if (reOPD.getErrCode() < 0) {
			err("ERR:" + reOPD.getErrCode() + reOPD.getErrText()
					+ reOPD.getErrName());
			return reOPD;
		}
		TParm reStation = onSetStation(selectStation(parm)); // ��ȡ���в�����־����
		// System.out.println("���в�����־����------------"+reStation);
		if (reStation.getErrCode() < 0) {
			err("ERR:" + reStation.getErrCode() + reStation.getErrText()
					+ reStation.getErrName());
			return reStation;
		}
		// ========pangben modify 20110519 start
		TParm result = selectdataParm(reOPD, reStation, parm
				.getValue("REGION_CODE"));
		// System.out.println("-=result---------"+result);
		// ========pangben modify 20110519 stop
		return result;
	}

	/**
	 * ��ѯ�±�������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectDataMonth(TParm parm) {
		TParm reOPD = this.selectOPD_M(parm); // ��ȡ�����ż����м������
		if (reOPD.getErrCode() < 0) {
			err("ERR:" + reOPD.getErrCode() + reOPD.getErrText()
					+ reOPD.getErrName());
			return reOPD;
		}
		TParm reStation = this.selectStation_M(parm); // ��ȡ���в�����־����
		if (reStation.getErrCode() < 0) {
			err("ERR:" + reStation.getErrCode() + reStation.getErrText()
					+ reStation.getErrName());
			return reStation;
		}
		TParm reFirstStation = this.selectStationFirstDay(parm); // ��ѯ�����ڳ�����
		// System.out.println("reFirstStation:" + reFirstStation);
		if (reFirstStation.getErrCode() < 0) {
			err("ERR:" + reFirstStation.getErrCode()
					+ reFirstStation.getErrText() + reFirstStation.getErrName());
			return reFirstStation;
		}
		TParm reLastStation = this.selectStationLastDay(parm); // ��ѯ������ĩ����
		// System.out.println("reLastStation:" + reLastStation);
		if (reLastStation.getErrCode() < 0) {
			err("ERR:" + reLastStation.getErrCode()
					+ reLastStation.getErrText() + reLastStation.getErrName());
			return reLastStation;
		}
		TParm result = selectdataParm(reOPD, reStation, reFirstStation,
				reLastStation, parm);
		return result;
	}

	/**
	 * ��ѯ �սṤ������
	 * 
	 * @param parm
	 *            TParm
	 * @param regionCode
	 *            TParm �������
	 * @return TParm ============pangben modify 20110519 ����������
	 */
	public TParm selectdataParm(TParm reOPD, TParm reStation, String regionCode) {
		TParm result = new TParm();
		// ============pangben modify 20110519 �����������
		TParm DEPT_4Parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			DEPT_4Parm.setData("REGION_CODE", regionCode);
		TParm dept = this.selectDEPT_4(DEPT_4Parm); // ��ȡ����4���м䲿��
		// ============pangben modify 20110519 stop
		// �������в��� ɸѡ�������ŵ�������� ��䵽 TParm ��
		for (int i = 0; i < dept.getCount(); i++) {
			String deptcode = dept.getValue("DEPT_CODE", i);
			String stationcode = "";// ����CODE
			String isSubmit = "Y";// �жϲ�����־�Ƿ��ύ(֮�жϲ����Ŀ���) ֻ���ż���Ŀ��� Ĭ��Ϊ�Ѿ��ύ
			// �жϴ˿����Ƿ���סԺ���ң������ isSubmit Ĭ��ΪN(û���ύ)
			if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
				isSubmit = "N";
			}
			int opdIndex = -1;
			int stationIndex = -1;
			// ���ż����м����ɸѡ�ò��ŵ����� ��¼����
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// �Ӳ�������ɸѡ���ŵ����� ��¼����
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// ����������� �ͽ���Ӧ����Ϣ��䵽Parm��
			if (opdIndex != -1) {
				result.addData("OUTP_NUM", reOPD.getData("OUTP_NUM", opdIndex));
				result.addData("ERD_NUM", reOPD.getData("ERD_NUM", opdIndex));
				result.addData("ERD_DIED_NUM", reOPD.getData("ERD_DIED_NUM",
						opdIndex));
				result.addData("OBS_NUM", reOPD.getData("OBS_NUM", opdIndex));
				result.addData("OBS_DIED_NUM", reOPD.getData("OBS_DIED_NUM",
						opdIndex));
				stationcode = dept.getValue("OE_DEPT_CODE", i); // �ż�����Ҽ�¼����
																// ���Ͽ��ҵ�CODE
			} else {
				result.addData("OUTP_NUM", null);
				result.addData("ERD_NUM", null);
				result.addData("ERD_DIED_NUM", null);
				result.addData("OBS_NUM", null);
				result.addData("OBS_DIED_NUM", null);
			}
			// ����������������ھͽ���Ӧ����Ϣ��䵽Parm��
			if (stationIndex != -1) {
				result.addData("OUYCHK_OI_NUM", reStation.getData(
						"OUYCHK_OI_NUM", stationIndex));
				result.addData("OUYCHK_RAPA_NUM", reStation.getData(
						"OUYCHK_RAPA_NUM", stationIndex));
				result.addData("OUYCHK_INOUT", reStation.getData(
						"OUYCHK_INOUT", stationIndex));
				result.addData("OUYCHK_OPBFAF", reStation.getData(
						"OUYCHK_OPBFAF", stationIndex));
				result.addData("HEAL_LV_I_CASE", reStation.getData(
						"HEAL_LV_I_CASE", stationIndex));
				result.addData("HEAL_LV_BAD", reStation.getData("HEAL_LV_BAD",
						stationIndex));
				result.addData("GET_TIMES", reStation.getData("GET_TIMES",
						stationIndex));
				result.addData("SUCCESS_TIMES", reStation.getData(
						"SUCCESS_TIMES", stationIndex));
				result.addData("BED_RETUEN", reStation.getData("BED_RETUEN",
						stationIndex));
				result.addData("BED_WORK_DAY", reStation.getData(
						"BED_WORK_DAY", stationIndex));
				result.addData("BED_USE_RATE", reStation.getData(
						"BED_USE_RATE", stationIndex));
				result.addData("DATA_01", reStation.getData("DATA_01",
						stationIndex));
				result.addData("DATA_02", reStation.getData("DATA_02",
						stationIndex));
				result.addData("DATA_03", reStation.getData("DATA_03",
						stationIndex));
				result.addData("DATA_04", reStation.getData("DATA_04",
						stationIndex));
				result.addData("DATA_05", reStation.getData("DATA_05",
						stationIndex));
				result.addData("DATA_06", reStation.getData("DATA_06",
						stationIndex));
				result.addData("DATA_06_1", reStation.getData("DATA_06_1",
						stationIndex));
				result.addData("DATA_07", reStation.getData("DATA_07",
						stationIndex));
				result.addData("DATA_08", reStation.getData("DATA_08",
						stationIndex));
				result.addData("DATA_08_1", reStation.getData("DATA_08_1",
						stationIndex));
				result.addData("DATA_09", reStation.getData("DATA_09",
						stationIndex));
				result.addData("DATA_10", reStation.getData("DATA_10",
						stationIndex));
				result.addData("DATA_11", reStation.getData("DATA_11",
						stationIndex));
				result.addData("DATA_12", reStation.getData("DATA_12",
						stationIndex));
				result.addData("DATA_13", reStation.getData("DATA_13",
						stationIndex));
				result.addData("DATA_14", reStation.getData("DATA_14",
						stationIndex));
				result.addData("DATA_15", reStation.getData("DATA_15",
						stationIndex));
				result.addData("DATA_15_1", reStation.getData("DATA_15_1",
						stationIndex));
				result.addData("DATA_16", reStation.getData("DATA_16",
						stationIndex));
				result.addData("DATA_17", reStation.getData("DATA_17",
						stationIndex));
				result.addData("DATA_18", reStation.getData("DATA_18",
						stationIndex));
				result.addData("DATA_19", reStation.getData("DATA_19",
						stationIndex));
				result.addData("DATA_20", reStation.getData("DATA_20",
						stationIndex));
				result.addData("DATA_21", reStation.getData("DATA_21",
						stationIndex));
				result.addData("DATA_22", reStation.getData("DATA_22",
						stationIndex));
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // סԺ���Ҽ�¼�Ĳ���CODE�����ϵĿ���CODE
				if (reStation.getValue("CONFIRM_FLG", stationIndex).trim()
						.length() > 0)// �±��������ύ�ֶ�
					isSubmit = reStation.getValue("CONFIRM_FLG", stationIndex);// ��¼������־�Ƿ��ύ
			} else {
				result.addData("OUYCHK_OI_NUM", null);
				result.addData("OUYCHK_RAPA_NUM", null);
				result.addData("OUYCHK_INOUT", null);
				result.addData("OUYCHK_OPBFAF", null);
				result.addData("HEAL_LV_I_CASE", null);
				result.addData("HEAL_LV_BAD", null);
				result.addData("GET_TIMES", null);
				result.addData("SUCCESS_TIMES", null);
				result.addData("BED_RETUEN", null);
				result.addData("BED_WORK_DAY", null);
				result.addData("BED_USE_RATE", null);
				result.addData("DATA_01", null);
				result.addData("DATA_02", null);
				result.addData("DATA_03", null);
				result.addData("DATA_04", null);
				result.addData("DATA_05", null);
				result.addData("DATA_06", null);
				result.addData("DATA_06_1", null);
				result.addData("DATA_07", null);
				result.addData("DATA_08", null);
				result.addData("DATA_08_1", null);
				result.addData("DATA_09", null);
				result.addData("DATA_10", null);
				result.addData("DATA_11", null);
				result.addData("DATA_12", null);
				result.addData("DATA_13", null);
				result.addData("DATA_14", null);
				result.addData("DATA_15", null);
				result.addData("DATA_15_1", null);
				result.addData("DATA_16", null);
				result.addData("DATA_17", null);
				result.addData("DATA_18", null);
				result.addData("DATA_19", null);
				result.addData("DATA_20", null);
				result.addData("DATA_21", null);
				result.addData("DATA_22", null);
			}
			// ��������ż��������ż������� Ҳ������ סԺ���� ��ô��������Ϣ
			if (opdIndex == -1 && stationIndex == -1) {
				// ����ò�����סԺ���� ��ô StationCode ���סԺ���ŵ�Code
				if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("IPD_DEPT_CODE", i);
				}
				// �������סԺ���� ��ô StationCode ������ﲿ�ŵ�CODE
				else if (dept.getValue("OE_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("OE_DEPT_CODE", i);
				} else {
					stationcode = dept.getValue("DEPT_CODE", i);
				}
			}
			result.addData("DEPT_CODE", deptcode); // ��¼�м����CODE
			result.addData("STATION_CODE", stationcode); // ��¼�м��StationCode
			result.addData("SUBMIT_FLG", isSubmit);// ��¼������־�Ƿ��ύ
		}
		return result;
	}

	/**
	 * ��ѯ �սṤ������(�±���)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdataParm(TParm reOPD, TParm reStation,
			TParm reFirstStation, TParm reLastStation, TParm parm) {
		TParm result = new TParm();
		// ===========pangben modify 20110519 start ��Ӳ��� û��ʹ�ô˷���
		TParm dept = this.selectDEPT_4(parm); // ��ȡ����4���м䲿��
		// ===========pangben modify 20110519 stop
		// �������в��� ɸѡ�������ŵ�������� ��䵽 TParm ��
		for (int i = 0; i < dept.getCount(); i++) {
			String deptcode = dept.getValue("DEPT_CODE", i);
			String stationcode = "";// ����CODE
			String isSubmit = "Y";// �жϲ�����־�Ƿ��ύ(֮�жϲ����Ŀ���) ֻ���ż���Ŀ��� Ĭ��Ϊ�Ѿ��ύ
			// �жϴ˿����Ƿ���סԺ���ң������ isSubmit Ĭ��ΪN(û���ύ)
			if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
				isSubmit = "N";
			}
			int opdIndex = -1;
			int stationIndex = -1;
			int firstIndex = -1;
			int lastIndex = -1;
			// ���ż����м����ɸѡ�ò��ŵ����� ��¼����
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// �Ӳ�������ɸѡ���ŵ����� ��¼����
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// ���ڳ�ֵ��ɸѡ��Ӧ�Ĳ�����Ϣ
			for (int h = 0; h < reFirstStation.getCount(); h++) {
				if (deptcode.equals(reFirstStation.getValue("DEPT_CODE", h))) {
					firstIndex = h;
				}
			}
			// ����ĩֵ��ɸѡ��Ӧ�Ĳ�����Ϣ
			for (int h = 0; h < reLastStation.getCount(); h++) {
				if (deptcode.equals(reFirstStation.getValue("DEPT_CODE", h))) {
					lastIndex = h;
				}
			}
			// System.out.println("firstIndex:"+firstIndex);
			// System.out.println("lastIndex:"+lastIndex);
			// ����������� �ͽ���Ӧ����Ϣ��䵽Parm��
			if (opdIndex != -1) {
				result.addData("OUTP_NUM", reOPD.getData("OUTP_NUM", opdIndex));
				result.addData("ERD_NUM", reOPD.getData("ERD_NUM", opdIndex));
				result.addData("ERD_DIED_NUM", reOPD.getData("ERD_DIED_NUM",
						opdIndex));
				result.addData("OBS_NUM", reOPD.getData("OBS_NUM", opdIndex));
				result.addData("OBS_DIED_NUM", reOPD.getData("OBS_DIED_NUM",
						opdIndex));
				stationcode = dept.getValue("OE_DEPT_CODE", i); // �ż�����Ҽ�¼����
																// ���Ͽ��ҵ�CODE
			} else {
				result.addData("OUTP_NUM", null);
				result.addData("ERD_NUM", null);
				result.addData("ERD_DIED_NUM", null);
				result.addData("OBS_NUM", null);
				result.addData("OBS_DIED_NUM", null);
			}
			// ����������������ھͽ���Ӧ����Ϣ��䵽Parm��
			if (stationIndex != -1) {
				result.addData("OUYCHK_OI_NUM", reStation.getData(
						"OUYCHK_OI_NUM", stationIndex));
				result.addData("OUYCHK_RAPA_NUM", reStation.getData(
						"OUYCHK_RAPA_NUM", stationIndex));
				result.addData("OUYCHK_INOUT", reStation.getData(
						"OUYCHK_INOUT", stationIndex));
				result.addData("OUYCHK_OPBFAF", reStation.getData(
						"OUYCHK_OPBFAF", stationIndex));
				result.addData("HEAL_LV_I_CASE", reStation.getData(
						"HEAL_LV_I_CASE", stationIndex));
				result.addData("HEAL_LV_BAD", reStation.getData("HEAL_LV_BAD",
						stationIndex));
				result.addData("GET_TIMES", reStation.getData("GET_TIMES",
						stationIndex));
				result.addData("SUCCESS_TIMES", reStation.getData(
						"SUCCESS_TIMES", stationIndex));
				result.addData("BED_RETUEN", reStation.getData("BED_RETUEN",
						stationIndex));
				result.addData("BED_WORK_DAY", reStation.getData(
						"BED_WORK_DAY", stationIndex));
				result.addData("BED_USE_RATE", reStation.getData(
						"BED_USE_RATE", stationIndex));
				result.addData("DATA_01", reStation.getData("DATA_01",
						stationIndex));
				result.addData("DATA_02", reStation.getData("DATA_02",
						stationIndex));
				result.addData("DATA_03", reStation.getData("DATA_03",
						stationIndex));
				result.addData("DATA_04", reStation.getData("DATA_04",
						stationIndex));
				result.addData("DATA_05", reStation.getData("DATA_05",
						stationIndex));
				result.addData("DATA_06", reStation.getData("DATA_06",
						stationIndex));
				result.addData("DATA_06_1", reStation.getData("DATA_06_1",
						stationIndex));

				result.addData("DATA_08", reStation.getData("DATA_08",
						stationIndex));
				result.addData("DATA_08_1", reStation.getData("DATA_08_1",
						stationIndex));
				result.addData("DATA_09", reStation.getData("DATA_09",
						stationIndex));
				result.addData("DATA_10", reStation.getData("DATA_10",
						stationIndex));
				result.addData("DATA_11", reStation.getData("DATA_11",
						stationIndex));
				result.addData("DATA_12", reStation.getData("DATA_12",
						stationIndex));
				result.addData("DATA_13", reStation.getData("DATA_13",
						stationIndex));
				result.addData("DATA_14", reStation.getData("DATA_14",
						stationIndex));
				result.addData("DATA_15", reStation.getData("DATA_15",
						stationIndex));
				result.addData("DATA_15_1", reStation.getData("DATA_15_1",
						stationIndex));

				result.addData("DATA_18", reStation.getData("DATA_18",
						stationIndex));
				result.addData("DATA_19", reStation.getData("DATA_19",
						stationIndex));
				result.addData("DATA_20", reStation.getData("DATA_20",
						stationIndex));
				result.addData("DATA_21", reStation.getData("DATA_21",
						stationIndex));
				result.addData("DATA_22", reStation.getData("DATA_22",
						stationIndex));
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // סԺ���Ҽ�¼�Ĳ���CODE�����ϵĿ���CODE
				if (reStation.getValue("CONFIRM_FLG", stationIndex).trim()
						.length() > 0)// �±��������ύ�ֶ�
					isSubmit = reStation.getValue("CONFIRM_FLG", stationIndex);// ��¼������־�Ƿ��ύ
			} else {
				result.addData("OUYCHK_OI_NUM", null);
				result.addData("OUYCHK_RAPA_NUM", null);
				result.addData("OUYCHK_INOUT", null);
				result.addData("OUYCHK_OPBFAF", null);
				result.addData("HEAL_LV_I_CASE", null);
				result.addData("HEAL_LV_BAD", null);
				result.addData("GET_TIMES", null);
				result.addData("SUCCESS_TIMES", null);
				result.addData("BED_RETUEN", null);
				result.addData("BED_WORK_DAY", null);
				result.addData("BED_USE_RATE", null);
				result.addData("DATA_01", null);
				result.addData("DATA_02", null);
				result.addData("DATA_03", null);
				result.addData("DATA_04", null);
				result.addData("DATA_05", null);
				result.addData("DATA_06", null);
				result.addData("DATA_06_1", null);
				result.addData("DATA_08", null);
				result.addData("DATA_08_1", null);
				result.addData("DATA_09", null);
				result.addData("DATA_10", null);
				result.addData("DATA_11", null);
				result.addData("DATA_12", null);
				result.addData("DATA_13", null);
				result.addData("DATA_14", null);
				result.addData("DATA_15", null);
				result.addData("DATA_15_1", null);
				result.addData("DATA_18", null);
				result.addData("DATA_19", null);
				result.addData("DATA_20", null);
				result.addData("DATA_21", null);
				result.addData("DATA_22", null);
			}
			if (firstIndex != -1) {
				result.addData("DATA_07", reFirstStation.getData("DATA_07",
						firstIndex));
			} else
				result.addData("DATA_07", null);
			if (lastIndex != -1) {
				result.addData("DATA_16", reLastStation.getData("DATA_16",
						lastIndex));
				result.addData("DATA_17", reLastStation.getData("DATA_17",
						lastIndex));
			} else {
				result.addData("DATA_16", null);
				result.addData("DATA_17", null);
			}
			// ��������ż��������ż������� Ҳ������ סԺ���� ��ô��������Ϣ
			if (opdIndex == -1 && stationIndex == -1) {
				// ����ò�����סԺ���� ��ô StationCode ���סԺ���ŵ�Code
				if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("IPD_DEPT_CODE", i);
				}
				// �������סԺ���� ��ô StationCode ������ﲿ�ŵ�CODE
				else if (dept.getValue("OE_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("OE_DEPT_CODE", i);
				} else {
					stationcode = dept.getValue("DEPT_CODE", i);
				}
			}
			result.addData("DEPT_CODE", deptcode); // ��¼�м����CODE
			result.addData("STATION_CODE", stationcode); // ��¼�м��StationCode
			result.addData("SUBMIT_FLG", isSubmit);// ��¼������־�Ƿ��ύ
		}
		return result;
	}

	/**
	 * ���м���ұ��в�ѯ����סԺ���ҵ�CODE
	 * 
	 * @return TParm
	 */
	public TParm selectIPD_DEPT(TParm parm) {
		TParm result = this.query("selectIPD_DEPT", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯ STA_DAILY_01�����Ѿ��ύ�Ĳ���CODE
	 * 
	 * @param STADATE
	 *            String
	 * @param regionCode
	 *            String
	 * @return TParm ===========pangben modify 20110520 ����������
	 */
	public TParm selectSubmitDept(String STADATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STADATE);
		// ===========pangben modify 20110520 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===========pangben modify 20110520 stop
		TParm result = this.query("selectSubmitDept", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���STA_DAILY_01���е������Ƿ��Ѿ��ύ
	 * 
	 * @return TParm ===============pangben modify 20110520 ����������
	 */
	public TParm checkSubmit(String STADATE, String regionCode) {
		// ===============pangben modify 20110520 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===============pangben modify 20110520 stop
		TParm dept = this.selectIPD_DEPT(parm); // ��ѯ������ סԺ����
		TParm submit = this.selectSubmitDept(STADATE, regionCode); // ��ѯSTA_DAILY_01�������ύ�Ĳ���
		for (int i = 0; i < submit.getCount(); i++) {
			String dept_code = submit.getValue("DEPT_CODE", i);
			for (int j = dept.getCount() - 1; j >= 0; j--) {
				if (dept_code.equals(dept.getValue("DEPT_CODE", j))) {
					dept.removeRow(j);
				}
			}
		}
		return dept;
	}

	/**
	 * ��ѯĳ3�������µ������¼����ң������������ұ���
	 * 
	 * @param deptList_4
	 *            TParm �������ļ����� TParm
	 * @param dept3_code
	 *            String ��������CODE
	 * @return TParm
	 */
	private TParm checkChildDept(TParm deptList_4, String dept3_code) {
		TParm result = new TParm();
		// ѭ���Ƚ� �ж��ļ����������ĸ���������
		for (int i = 0; i < result.getCount(); i++) {
			// �ļ�����CODE ��ȡǰ��λ���������ҵ�CODE
			if (deptList_4.getValue("DEPT_CODE", i).substring(0, 5).equals(
					dept3_code)) {
				result
						.addData("DEPT_CODE", deptList_4.getValue("DEPT_CODE",
								i));
			}
		}
		return result;
	}

	/**
	 * ��ȡĳһ3�����ţ��Լ���4�����ţ���ͳ������
	 * 
	 * @param reOPD
	 *            TParm �����ŵ���������
	 * @param reStation
	 *            TParm �����ŵ�סԺ����
	 * @param deptcode
	 *            TParm ����code
	 * @param dept
	 *            TParm 3,4������TParm
	 * @return TParm
	 */
	private TParm getDeptData(TParm reOPD, TParm reStation, String deptcode,
			TParm dept) {
		TParm result = new TParm();
		TParm deptList = checkChildDept(dept, deptcode);// ��ѯĳ3�������µ������¼����ң������������ұ���
		String stationcode = ""; // ����CODE
		int opdIndex = -1;// ��¼����ͳ����Ϣ��ָ������
		int stationIndex = -1;// ��¼סԺͳ����Ϣ��ָ������
		int OUTP_NUM = -1;
		int ERD_NUM = -1;
		int OBS_NUM = -1;
		int OBS_DIED_NUM = -1;
		int ERD_DIED_NUM = -1;
		int OUYCHK_OI_NUM = -1;
		int OUYCHK_RAPA_NUM = -1;
		int OUYCHK_INOUT = -1;
		int OUYCHK_OPBFAF = -1;
		int HEAL_LV_I_CASE = -1;
		int HEAL_LV_BAD = -1;
		int GET_TIMES = -1;
		int SUCCESS_TIMES = -1;
		int BED_RETUEN = -1;
		int BED_WORK_DAY = -1;
		int BED_USE_RATE = -1;
		int DATA_01 = -1;
		int DATA_02 = -1;
		int DATA_03 = -1;
		int DATA_04 = -1;
		int DATA_05 = -1;
		int DATA_06 = -1;
		int DATA_06_1 = -1;
		int DATA_07 = -1;
		int DATA_08 = -1;
		int DATA_08_1 = -1;
		int DATA_09 = -1;
		int DATA_10 = -1;
		int DATA_11 = -1;
		int DATA_12 = -1;
		int DATA_13 = -1;
		int DATA_14 = -1;
		int DATA_15 = -1;
		int DATA_15_1 = -1;
		int DATA_16 = -1;
		int DATA_17 = -1;
		int DATA_18 = -1;
		int DATA_19 = -1;
		int DATA_20 = -1;
		int DATA_21 = -1;
		int DATA_22 = -1;
		for (int i = 0; i < deptList.getCount(); i++) {
			// ���ż����м����ɸѡ�ò��ŵ����� ��¼����
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// �Ӳ�������ɸѡ���ŵ����� ��¼����
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// ����������� �ͽ���Ӧ����Ϣ �ۼ�
			if (opdIndex != -1) {
				OUTP_NUM = OUTP_NUM == -1 ? 0 : OUTP_NUM
						+ reOPD.getInt("OUTP_NUM", opdIndex);
				ERD_NUM = ERD_NUM == -1 ? 0 : ERD_NUM
						+ reOPD.getInt("ERD_NUM", opdIndex);
				ERD_DIED_NUM = ERD_DIED_NUM == -1 ? 0 : ERD_DIED_NUM
						+ reOPD.getInt("ERD_DIED_NUM", opdIndex);
				OBS_NUM = OBS_NUM == -1 ? 0 : OBS_NUM
						+ reOPD.getInt("OBS_NUM", opdIndex);
				OBS_DIED_NUM = OBS_DIED_NUM == -1 ? 0 : OBS_DIED_NUM
						+ reOPD.getInt("OBS_DIED_NUM", opdIndex);
				stationcode = dept.getValue("OE_DEPT_CODE", i); // �ż�����Ҽ�¼����
																// ���Ͽ��ҵ�CODE
			}
			// ����������������ھͽ���Ӧ����Ϣ �ۼ�
			if (stationIndex != -1) {
				OUYCHK_OI_NUM = OUYCHK_OI_NUM == -1 ? 0 : OUYCHK_OI_NUM
						+ reStation.getInt("OUYCHK_OI_NUM", stationIndex);
				OUYCHK_RAPA_NUM = OUYCHK_RAPA_NUM == -1 ? 0 : OUYCHK_RAPA_NUM
						+ reStation.getInt("OUYCHK_RAPA_NUM", stationIndex);
				OUYCHK_INOUT = OUYCHK_INOUT == -1 ? 0 : OUYCHK_INOUT
						+ reStation.getInt("OUYCHK_INOUT", stationIndex);
				OUYCHK_OPBFAF = OUYCHK_OPBFAF == -1 ? 0 : OUYCHK_OPBFAF
						+ reStation.getInt("OUYCHK_OPBFAF", stationIndex);
				HEAL_LV_I_CASE = HEAL_LV_I_CASE == -1 ? 0 : HEAL_LV_I_CASE
						+ reStation.getInt("HEAL_LV_I_CASE", stationIndex);
				HEAL_LV_BAD = HEAL_LV_BAD == -1 ? 0 : HEAL_LV_BAD
						+ reStation.getInt("HEAL_LV_BAD", stationIndex);
				GET_TIMES = GET_TIMES == -1 ? 0 : GET_TIMES
						+ reStation.getInt("GET_TIMES", stationIndex);
				SUCCESS_TIMES = SUCCESS_TIMES == -1 ? 0 : SUCCESS_TIMES
						+ reStation.getInt("SUCCESS_TIMES", stationIndex);
				BED_RETUEN = BED_RETUEN == -1 ? 0 : BED_RETUEN
						+ reStation.getInt("BED_RETUEN", stationIndex);
				BED_WORK_DAY = BED_WORK_DAY == -1 ? 0 : BED_WORK_DAY
						+ reStation.getInt("BED_WORK_DAY", stationIndex);
				BED_USE_RATE = BED_USE_RATE == -1 ? 0 : BED_USE_RATE
						+ reStation.getInt("BED_USE_RATE", stationIndex);
				DATA_01 = DATA_01 == -1 ? 0 : DATA_01
						+ reStation.getInt("DATA_01", stationIndex);
				DATA_02 = DATA_02 == -1 ? 0 : DATA_02
						+ reStation.getInt("DATA_02", stationIndex);
				DATA_03 = DATA_03 == -1 ? 0 : DATA_03
						+ reStation.getInt("DATA_03", stationIndex);
				DATA_04 = DATA_04 == -1 ? 0 : DATA_04
						+ reStation.getInt("DATA_04", stationIndex);
				DATA_05 = DATA_05 == -1 ? 0 : DATA_05
						+ reStation.getInt("DATA_05", stationIndex);
				DATA_06 = DATA_06 == -1 ? 0 : DATA_06
						+ reStation.getInt("DATA_06", stationIndex);
				DATA_06_1 = DATA_06_1 == -1 ? 0 : DATA_06_1
						+ reStation.getInt("DATA_06_1", stationIndex);
				DATA_07 = DATA_07 == -1 ? 0 : DATA_07
						+ reStation.getInt("DATA_07", stationIndex);
				DATA_08 = DATA_08 == -1 ? 0 : DATA_08
						+ reStation.getInt("DATA_08", stationIndex);
				DATA_08_1 = DATA_08_1 == -1 ? 0 : DATA_08_1
						+ reStation.getInt("DATA_08_1", stationIndex);
				DATA_09 = DATA_09 == -1 ? 0 : DATA_09
						+ reStation.getInt("DATA_09", stationIndex);
				DATA_10 = DATA_10 == -1 ? 0 : DATA_10
						+ reStation.getInt("DATA_10", stationIndex);
				DATA_11 = DATA_11 == -1 ? 0 : DATA_11
						+ reStation.getInt("DATA_11", stationIndex);
				DATA_12 = DATA_12 == -1 ? 0 : DATA_12
						+ reStation.getInt("DATA_12", stationIndex);
				DATA_13 = DATA_13 == -1 ? 0 : DATA_13
						+ reStation.getInt("DATA_13", stationIndex);
				DATA_14 = DATA_14 == -1 ? 0 : DATA_14
						+ reStation.getInt("DATA_14", stationIndex);
				DATA_15 = DATA_15 == -1 ? 0 : DATA_15
						+ reStation.getInt("DATA_15", stationIndex);
				DATA_15_1 = DATA_15_1 == -1 ? 0 : DATA_15_1
						+ reStation.getInt("DATA_15_1", stationIndex);
				DATA_16 = DATA_16 == -1 ? 0 : DATA_16
						+ reStation.getInt("DATA_16", stationIndex);
				DATA_17 = DATA_17 == -1 ? 0 : DATA_17
						+ reStation.getInt("DATA_17", stationIndex);
				DATA_18 = DATA_18 == -1 ? 0 : DATA_18
						+ reStation.getInt("DATA_18", stationIndex);
				DATA_19 = DATA_19 == -1 ? 0 : DATA_19
						+ reStation.getInt("DATA_19", stationIndex);
				DATA_20 = DATA_20 == -1 ? 0 : DATA_20
						+ reStation.getInt("DATA_20", stationIndex);
				DATA_21 = DATA_21 == -1 ? 0 : DATA_21
						+ reStation.getInt("DATA_21", stationIndex);
				DATA_22 = DATA_22 == -1 ? 0 : DATA_22
						+ reStation.getInt("DATA_22", stationIndex);
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // סԺ���Ҽ�¼�Ĳ���CODE�����ϵĿ���CODE
			}
		}
		result.addData("OUYCHK_OI_NUM", OUYCHK_OI_NUM == -1 ? null
				: OUYCHK_OI_NUM);
		result.addData("OUYCHK_RAPA_NUM", OUYCHK_RAPA_NUM == -1 ? null
				: OUYCHK_RAPA_NUM);
		result
				.addData("OUYCHK_INOUT", OUYCHK_INOUT == -1 ? null
						: OUYCHK_INOUT);
		result.addData("OUYCHK_OPBFAF", OUYCHK_OPBFAF == -1 ? null
				: OUYCHK_OPBFAF);
		result.addData("HEAL_LV_I_CASE", HEAL_LV_I_CASE == -1 ? null
				: HEAL_LV_I_CASE);
		result.addData("HEAL_LV_BAD", HEAL_LV_BAD == -1 ? null : HEAL_LV_BAD);
		result.addData("GET_TIMES", GET_TIMES == -1 ? null : GET_TIMES);
		result.addData("SUCCESS_TIMES", SUCCESS_TIMES == -1 ? null
				: SUCCESS_TIMES);
		result.addData("BED_RETUEN", BED_RETUEN == -1 ? null : BED_RETUEN);
		result
				.addData("BED_WORK_DAY", BED_WORK_DAY == -1 ? null
						: BED_WORK_DAY);
		result
				.addData("BED_USE_RATE", BED_USE_RATE == -1 ? null
						: BED_USE_RATE);
		result.addData("DATA_01", DATA_01 == -1 ? null : DATA_01);
		result.addData("DATA_02", DATA_02 == -1 ? null : DATA_02);
		result.addData("DATA_03", DATA_03 == -1 ? null : DATA_03);
		result.addData("DATA_04", DATA_04 == -1 ? null : DATA_04);
		result.addData("DATA_05", DATA_05 == -1 ? null : DATA_05);
		result.addData("DATA_06", DATA_06 == -1 ? null : DATA_06);
		result.addData("DATA_06_1", DATA_06_1 == -1 ? null : DATA_06_1);
		result.addData("DATA_07", DATA_07 == -1 ? null : DATA_07);
		result.addData("DATA_08", DATA_08 == -1 ? null : DATA_08);
		result.addData("DATA_08_1", DATA_08_1 == -1 ? null : DATA_08_1);
		result.addData("DATA_09", DATA_09 == -1 ? null : DATA_09);
		result.addData("DATA_10", DATA_10 == -1 ? null : DATA_10);
		result.addData("DATA_11", DATA_11 == -1 ? null : DATA_11);
		result.addData("DATA_12", DATA_12 == -1 ? null : DATA_12);
		result.addData("DATA_13", DATA_13 == -1 ? null : DATA_13);
		result.addData("DATA_14", DATA_14 == -1 ? null : DATA_14);
		result.addData("DATA_15", DATA_15 == -1 ? null : DATA_15);
		result.addData("DATA_15_1", DATA_15_1 == -1 ? null : DATA_15_1);
		result.addData("DATA_16", DATA_16 == -1 ? null : DATA_16);
		result.addData("DATA_17", DATA_17 == -1 ? null : DATA_17);
		result.addData("DATA_18", DATA_18 == -1 ? null : DATA_18);
		result.addData("DATA_19", DATA_19 == -1 ? null : DATA_19);
		result.addData("DATA_20", DATA_20 == -1 ? null : DATA_20);
		result.addData("DATA_21", DATA_21 == -1 ? null : DATA_21);
		result.addData("DATA_22", DATA_22 == -1 ? null : DATA_22);
		result.addData("DEPT_CODE", deptcode); // ��¼�м����CODE
		result.addData("STATION_CODE", stationcode); // ��¼�м��StationCode
		return result;
	}

	/**
	 * ��ѯ����1��2��3�������б�
	 * 
	 * @return TParm
	 */
	public TParm selectDeptList() {
		TParm result = this.query("selectDeptList");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯSTA_DAILY_02ĳһ�������
	 * 
	 * @param STADATE
	 *            String ��������
	 * @param regionCode
	 *            String ����
	 * @return TParm ============pangben modify 20110519 ����������
	 */
	public TParm selectSTA_DAILY_02(String STADATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STADATE);
		// ============pangben modify 20110519 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110519 stop
		TParm result = this.query("selectSTA_DAILY_02", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��˴���Ĳ���CODE�ǲ���סԺ���ң������� ture:��סԺ���� false:����סԺ����
	 * 
	 * @param dept_code
	 *            String
	 * @return boolean
	 */
	public boolean checkIPDDept(String dept_code) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", dept_code);
		TParm result = this.query("checkIPDDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		// ���IPD_DEPT_CODE סԺ���Ҵ��벻���� ���� false
		if (result.getValue("IPD_DEPT_CODE", 0).trim().length() <= 0)
			return false;
		else
			return true;
	}

	/**
	 * ���� ������ת���Σ�
	 * 
	 * @param outNum
	 *            int ���ڳ�Ժ����
	 * @param openBedNum
	 *            int ͬ��ƽ�����Ų�����
	 * @return double
	 */
	public double countBedReturn(int outNum, int openBedNum) {
		DecimalFormat df = new DecimalFormat("0.00"); // �趨Double���͸�ʽ
		// ��ʽ��������ת(��) = ���ڳ�Ժ����/ ͬ��ƽ�����Ų�����
		double bedReturn = 0;
		if (openBedNum != 0) {
			bedReturn = (double) outNum / (double) openBedNum;
		}
		return bedReturn;
	}

	/**
	 * ���� ����ʹ����
	 * 
	 * @param real_bed_day_num
	 *            int
	 * @param daysOfMonth
	 *            int
	 * @param END_BED_NUM
	 *            int
	 * @return double
	 */
	public double countBED_USE_RATE(int real_bed_day_num, int daysOfMonth,
			int END_BED_NUM) {
		// ��ʽ������ʹ���� = ʵ��ռ���ܴ����� / ������ / ���ƴ�λ������ĩʵ�в�������
		double BED_USE_RATE = 0;
		if (daysOfMonth != 0 && END_BED_NUM != 0) {
			BED_USE_RATE = (double) real_bed_day_num / (double) daysOfMonth
					/ (double) END_BED_NUM;
		}
		return BED_USE_RATE;
	}

	/**
	 * ��ѯ�±����ڳ�ֵ���µ�һ��ֵ�� ��ѯ�ڳ�ʵ�в�����
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm selectStationFirstDay(TParm parm) {
		String STA_DATE = parm.getValue("STA_DATE");
		String StartDate = STA_DATE + "01";// ÿ�µ�һ��
		TParm p = new TParm();
		p.setData("STA_DATE", StartDate);
		// ============pangben modify 20110520 start
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			p.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		// ============pangben modify 20110520 stop
		TParm result = this.query("selectStationFirstDay", p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 *��ѯ�±�����ĩֵ�������һ��ֵ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStationLastDay(TParm parm) {
		String STA_DATE = parm.getValue("STA_DATE");
		String StartDate = STA_DATE + "01";// ÿ�µ�һ��
		// ��ȡ���·ݵ����һ��
		String EndDate = StringTool.getString(STATool.getInstance()
				.getLastDayOfMonth(STA_DATE), "yyyyMMdd");
		TParm p = new TParm();
		p.setData("DATE_S", StartDate);
		p.setData("DATE_E", EndDate);
		// ============pangben modify 20110520 start
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			p.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		// ============pangben modify 20110520 stop

		TParm result = this.query("selectStationLastDay", p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ������������֮�������
	 * @param STA_START_DATE ��ʼ����
	 * @param STA_END_DATE ��������
	 * @param type �ձ� or �±�
	 *             �ձ����������� -��ʼ���� +1
	 *             �±����ӿ�ʼ�·ݵ������·ݵ��������
	 * @return
	 */
    public int countDays(String STA_START_DATE, String STA_END_DATE, String type) {// wanglong add 20140710
        if (STA_START_DATE.compareTo(STA_END_DATE) > 0) {
            String temp = STA_START_DATE;
            STA_START_DATE = STA_END_DATE;
            STA_END_DATE = temp;
        }
        String daysSql =
                "SELECT ABS(TO_DATE( '#', 'YYYYMMDD') - TO_DATE( '#', 'YYYYMMDD')) + 1 AS DAYS_COUNT FROM DUAL";
        if (type.equals("D")) {// �ձ�
            daysSql = daysSql.replaceFirst("#", STA_END_DATE);
            daysSql = daysSql.replaceFirst("#", STA_START_DATE);
        } else {// �±�
            STA_START_DATE =
                    StringTool.getString(StringTool.getTimestamp(STA_START_DATE, "yyyyMM"),
                                         "yyyyMMdd");
            STA_END_DATE =
                    StringTool
                            .getString(StringTool.rollDate(StringTool.getTimestamp(STATool
                                               .getInstance().rollMonth(STA_END_DATE, 1),// ��һ����
                                                                                   "yyyyMM"), -1),
                                       "yyyyMMdd");
            daysSql = daysSql.replaceFirst("#", STA_END_DATE);
            daysSql = daysSql.replaceFirst("#", STA_END_DATE);
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(daysSql));
        if (result.getErrCode() < 0) {
            return 0;
        }
        return result.getInt("DAYS_COUNT", 0);
    }
    
    /**
     * �����������ѯ�ձ�����
     * @param parm
     * @param type
     */
    public TParm selectDataByPeriod(TParm parm, String type) {// wanglong add 20140710
        if (parm.getValue("STA_START_DATE").compareTo(parm.getValue("STA_END_DATE")) > 0) {
            String temp = parm.getValue("STA_START_DATE");
            parm.setData("STA_START_DATE", parm.getValue("STA_END_DATE"));
            parm.setData("STA_END_DATE", temp);
        }
        String daysSql =
                "SELECT ABS(TO_DATE( '#', 'YYYYMMDD') - TO_DATE( '#', 'YYYYMMDD')) + 1 AS DAYS_COUNT FROM DUAL";
        if (type.equals("D")) {// �ձ�
            daysSql = daysSql.replaceFirst("#", parm.getValue("STA_END_DATE"));
            daysSql = daysSql.replaceFirst("#", parm.getValue("STA_START_DATE"));
        } else {// �±�
            parm.setData("STA_START_DATE", StringTool.getString(StringTool.getTimestamp(parm
                    .getValue("STA_START_DATE"), "yyyyMM"), "yyyyMMdd"));
            parm.setData("STA_END_DATE",
                         StringTool.getString(StringTool.rollDate(StringTool
                                                                          .getTimestamp(STATool.getInstance()
                                                                                                .rollMonth(parm.getValue("STA_END_DATE"),
                                                                                                           1),// ��һ����
                                                                                        "yyyyMM"),
                                                                  -1), "yyyyMMdd"));
            daysSql = daysSql.replaceFirst("#", parm.getValue("STA_END_DATE"));
            daysSql = daysSql.replaceFirst("#", parm.getValue("STA_START_DATE"));
        }
        String sql =
                "WITH A AS (SELECT DISTINCT DEPT_CODE, DEPT_DESC, OE_DEPT_CODE, IPD_DEPT_CODE "
                        + "            FROM STA_OEI_DEPT_LIST "
                        + "           WHERE REGION_CODE = '#' "
                        + "             AND DEPT_LEVEL = '4'), "
                        + "     B AS (SELECT DEPT_CODE, SUM(OUTP_NUM) AS OUTP_NUM, SUM(ERD_NUM) AS ERD_NUM, "
                        + "                 SUM(ERD_DIED_NUM) AS ERD_DIED_NUM, SUM(OBS_NUM) AS OBS_NUM, "
                        + "                 SUM(OBS_DIED_NUM) AS OBS_DIED_NUM "
                        + "            FROM STA_OPD_DAILY "
                        + "           WHERE REGION_CODE = '#' "
                        + "             AND STA_DATE BETWEEN '@' AND '&' �� "// /////
                        + "        GROUP BY DEPT_CODE), "
                        + "     C AS (SELECT A.DEPT_CODE, SUM(B.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM, "
                        + "                 SUM(B.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM, SUM(B.OUYCHK_INOUT) AS OUYCHK_INOUT, "
                        + "                 SUM(B.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF, SUM(B.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, "
                        + "                 SUM(B.HEAL_LV_BAD) AS HEAL_LV_BAD, SUM(B.GET_TIMES) AS GET_TIMES, "
                        + "                 SUM(B.SUCCESS_TIMES) AS SUCCESS_TIMES, SUM(A.DATA_01) AS DATA_01, "
                        + "                 SUM(A.DATA_02) AS DATA_02, SUM(A.DATA_03) AS DATA_03, SUM(A.DATA_04) AS DATA_04, "
                        + "                 SUM(A.DATA_05) AS DATA_05, SUM(A.DATA_06) AS DATA_06, SUM(A.DATA_06_1) AS DATA_06_1, "
                        + "                 SUM(A.DATA_07) AS DATA_07, SUM(A.DATA_08) AS DATA_08, SUM(A.DATA_08_1) AS DATA_08_1, "
                        + "                 SUM(A.DATA_09) AS DATA_09, SUM(A.DATA_10) AS DATA_10, SUM(A.DATA_11) AS DATA_11, "
                        + "                 SUM(A.DATA_12) AS DATA_12, SUM(A.DATA_13) AS DATA_13, SUM(A.DATA_14) AS DATA_14, "
                        + "                 SUM(A.DATA_15) AS DATA_15, SUM(A.DATA_15_1) AS DATA_15_1, SUM(A.DATA_16) AS DATA_16, "
                        + "                 SUM(A.DATA_19) AS DATA_19, SUM(A.DATA_20) AS DATA_20, SUM(A.DATA_21) AS DATA_21, "
                        + "                 SUM(A.DATA_22) AS DATA_22, A.DATA_17, A.DATA_18, A.CONFIRM_FLG "
                        + "            FROM STA_DAILY_01 A, STA_STATION_DAILY B "
                        + "           WHERE A.DEPT_CODE = B.DEPT_CODE "
                        + "             AND A.STATION_CODE = B.STATION_CODE "
                        + "             AND A.STA_DATE = B.STA_DATE "
                        + "             AND A.REGION_CODE = B.REGION_CODE "
                        + "             AND A.REGION_CODE = '#' �� "
                        + "             AND A.STA_DATE BETWEEN '@' AND '&' "// /////////
                        + "        GROUP BY A.DEPT_CODE, A.DATA_17, A.DATA_18, A.CONFIRM_FLG), "
                        + "     D AS (SELECT DEPT_CODE, SUM(DATA_07) AS DATA_07 "
                        + "             FROM STA_DAILY_01 "
                        + "            WHERE REGION_CODE = '#' �� "
                        + "              AND STA_DATE = '@' "// ��һ��
                        + "         GROUP BY DEPT_CODE), "
                        + "     E AS (SELECT DEPT_CODE, SUM(DATA_16) AS DATA_16, DATA_17 "
                        + "             FROM STA_DAILY_01 "
                        + "            WHERE REGION_CODE = '#' �� "
                        + "              AND STA_DATE = '&' "// ���һ��
                        + "         GROUP BY DEPT_CODE, DATA_17), "
                        + "     F AS (!) "// ��������
                        + "SELECT �� A.DEPT_CODE, "
//                        + "       CASE WHEN C.DEPT_CODE IS NOT NULL THEN A.IPD_DEPT_CODE "
//                        + "            WHEN B.DEPT_CODE IS NOT NULL THEN A.OE_DEPT_CODE "
//                        + "            ELSE A.DEPT_CODE "
//                        + "       END AS STATION_CODE, "
                        + "       CASE WHEN B.OUTP_NUM IS NULL OR B.ERD_NUM IS NULL THEN NULL "
                        + "            ELSE B.OUTP_NUM + B.ERD_NUM "
                        + "       END AS DATA_01, "
                        + "       B.OUTP_NUM AS DATA_02, "
                        + "       B.ERD_NUM AS DATA_03, "
                        + "       B.ERD_DIED_NUM AS DATA_04, "
                        + "       B.OBS_NUM AS DATA_05, "
                        + "       B.OBS_DIED_NUM AS DATA_06, "
                        + "       C.DATA_07 AS DATA_07, "
                        + "       C.DATA_08 AS DATA_08, "
                        + "       C.DATA_08_1 AS DATA_08_1, "
                        + "       C.DATA_09 AS DATA_09, "
                        + "       C.DATA_10 AS DATA_10, "
                        + "       C.DATA_11 AS DATA_11, "
                        + "       C.DATA_12 AS DATA_12, "
                        + "       C.DATA_13 AS DATA_13, "
                        + "       C.DATA_14 AS DATA_14, "
                        + "       C.DATA_15 AS DATA_15, "
                        + "       C.DATA_15_1 AS DATA_15_1, "
                        + "       C.DATA_16 AS DATA_16, "
                        + "       C.DATA_17 AS DATA_17, "
                        + "       C.DATA_18 * F.DAYS_COUNT AS DATA_18, "
                        + "       C.DATA_18 AS DATA_19, "
                        + "       C.DATA_16 AS DATA_20, "
                        + "       C.DATA_19 AS DATA_21, "
                        + "       C.OUYCHK_OI_NUM AS DATA_22, "
                        + "       C.OUYCHK_RAPA_NUM AS DATA_23, "
                        + "       C.OUYCHK_INOUT AS DATA_24, "
                        + "       C.OUYCHK_OPBFAF AS DATA_25, "
                        + "       C.HEAL_LV_I_CASE AS DATA_26, "
                        + "       C.HEAL_LV_BAD AS DATA_27, "
                        + "       C.GET_TIMES AS DATA_28, "
                        + "       C.SUCCESS_TIMES AS DATA_29, "
                        + "       C.DATA_22 AS DATA_30, "
                        + "       CASE WHEN (C.DATA_10 + C.DATA_15) IS NULL OR C.DATA_10 + C.DATA_15 = 0 THEN NULL "
                        + "            WHEN C.DATA_11 IS NULL OR C.DATA_11 = 0 THEN 0 "
                        + "            ELSE ROUND( C.DATA_11 / (C.DATA_10 + C.DATA_15), 2)*100 "
                        + "       END AS DATA_31, "
                        + "       CASE WHEN (C.DATA_10 + C.DATA_15) IS NULL OR C.DATA_10 + C.DATA_15 = 0 THEN NULL "
                        + "            WHEN C.DATA_12 IS NULL OR C.DATA_12 = 0 THEN 0 "
                        + "            ELSE ROUND( C.DATA_12 / (C.DATA_10 + C.DATA_15), 2)*100 "
                        + "       END AS DATA_32, "
                        + "       CASE WHEN (C.DATA_10 + C.DATA_15) IS NULL OR C.DATA_10 + C.DATA_15 = 0 THEN NULL "
                        + "            WHEN C.DATA_14 IS NULL OR C.DATA_14 = 0 THEN 0 "
                        + "            ELSE ROUND( C.DATA_14 / (C.DATA_10 + C.DATA_15), 2)*100 "
                        + "       END AS DATA_33, "
                        + "       CASE WHEN C.DATA_18 > 0 AND C.DATA_09 > 0 THEN ROUND( C.DATA_09 / C.DATA_18, 2) "
                        + "            WHEN C.DATA_09 IS NULL OR C.DATA_09 = 0 THEN 0 "
                        + "            ELSE NULL "
                        + "       END AS DATA_34, "
                        + "       NULL AS DATA_35, "//�㷨����
                        + "       CASE WHEN C.DATA_18 > 0 THEN ROUND( C.DATA_16 / F.DAYS_COUNT / C.DATA_18, 2) ELSE 0  "
                        + "       END AS DATA_36, "
                        + "       CASE WHEN C.DATA_09 IS NULL OR C.DATA_09 = 0 THEN NULL "
                        + "            WHEN C.DATA_19 IS NULL OR C.DATA_19 = 0 THEN 0 "
                        + "            ELSE ROUND( C.DATA_19 / C.DATA_09, 2) "
                        + "       END AS DATA_37, "
                        + "       NULL AS DATA_38, "
                        + "       CASE WHEN C.HEAL_LV_I_CASE IS NULL OR C.HEAL_LV_I_CASE = 0 THEN NULL "
                        + "            WHEN C.HEAL_LV_BAD IS NULL OR C.HEAL_LV_BAD = 0 THEN 0 "
                        + "            ELSE ROUND( C.HEAL_LV_BAD / C.HEAL_LV_I_CASE, 2)*100 "
                        + "       END AS DATA_39, "
                        + "       CASE WHEN C.GET_TIMES IS NULL THEN NULL "
                        + "            WHEN C.GET_TIMES = 0 THEN 100 "
                        + "            WHEN C.SUCCESS_TIMES = 0 THEN 0 "
                        + "            ELSE ROUND( C.SUCCESS_TIMES / C.GET_TIMES, 2)*100 "
                        + "       END AS DATA_40, "
                        + "       CASE WHEN C.DATA_16 IS NULL OR C.DATA_16 = 0 THEN NULL "
                        + "            WHEN C.DATA_22 IS NULL OR C.DATA_22 = 0 THEN 0 "
                        + "            ELSE ROUND( C.DATA_22 / C.DATA_16, 2)*100 "
                        + "       END AS DATA_41, "
                        + "       CASE WHEN (C.DATA_10 + C.DATA_15) IS NULL THEN NULL "
                        + "            WHEN C.DATA_10 + C.DATA_15 = 0 THEN 100 "
                        + "            WHEN C.OUYCHK_RAPA_NUM IS NULL OR C.OUYCHK_RAPA_NUM = 0 THEN 0 "
                        + "            ELSE ROUND( C.OUYCHK_RAPA_NUM / (C.DATA_10 + C.DATA_15), 2)*100 "
                        + "       END AS DATA_41_1, "
                        + "       CASE WHEN (C.DATA_10 + C.DATA_15) IS NULL OR C.DATA_10 + C.DATA_15 = 0 THEN NULL "
                        + "            WHEN C.OUYCHK_OPBFAF IS NULL OR C.OUYCHK_OPBFAF = 0 THEN 0 "
                        + "            ELSE ROUND( C.OUYCHK_OPBFAF / (C.DATA_10 + C.DATA_15), 2)*100 "
                        + "       END AS DATA_41_2             "
                        + "  FROM A, B, C, D, E, F             "
                        + " WHERE A.DEPT_CODE = B.DEPT_CODE(+) ~ "
                        + "   AND A.DEPT_CODE = C.DEPT_CODE(+) "
                        + "   AND A.DEPT_CODE = D.DEPT_CODE(+) "
                        + "   AND A.DEPT_CODE = E.DEPT_CODE(+) "
                        + "ORDER BY A.DEPT_CODE ";
//                        + "ORDER BY A.DEPT_CODE, STATION_CODE ";
        sql = sql.replaceFirst("!", daysSql);
        sql = sql.replaceAll("#", parm.getValue("REGION_CODE"));
        sql = sql.replaceAll("@", parm.getValue("STA_START_DATE"));
        sql = sql.replaceAll("&", parm.getValue("STA_END_DATE"));
        if (!parm.getValue("DEPT_CODE").equals("")) {
            sql =
                    sql.replaceFirst("~", " AND A.DEPT_CODE = '#' ".replaceFirst("#", parm
                            .getValue("DEPT_CODE")));
        } else {
            sql = sql.replaceFirst("~", "");
        }
        if (!parm.getValue("CONFIRM_FLG").equals("")) {
            sql =
                    sql.replaceAll("��", " AND CONFIRM_FLG = '#' ".replaceFirst("#", parm
                            .getValue("CONFIRM_FLG")));
            sql =
                    sql.replaceFirst("��", " AND A.CONFIRM_FLG = '#' ".replaceFirst("#", parm
                            .getValue("CONFIRM_FLG")));
            sql =
                    sql.replaceFirst("��", "'#' AS SUBMIT_FLG, ".replaceFirst("#", parm
                            .getValue("CONFIRM_FLG")));
        } else {
            sql = sql.replaceAll("��", "");
            sql = sql.replaceFirst("��", "");
            if (type.equals("D")) {// �ձ�
                sql = sql.replaceFirst("��", "'' AS SUBMIT_FLG, ");
            } else {// �±�
                sql = sql.replaceFirst("��", "");
            }
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
	
}
