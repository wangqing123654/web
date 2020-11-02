package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import java.math.BigDecimal;

/**
 * <p>
 * Title: ҽԺסԺ������̬����Ч����
 * </p>
 * 
 * <p>
 * Description: ҽԺסԺ������̬����Ч����
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
 * @author zhangk 2009-6-19
 * @version 1.0
 */
public class STAIn_03Tool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static STAIn_03Tool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static STAIn_03Tool getInstance() {
		if (instanceObject == null)
			instanceObject = new STAIn_03Tool();
		return instanceObject;
	}

	public STAIn_03Tool() {
		setModuleName("sta\\STAIn_03Module.x");
		onInit();
	}

	/**
	 * ��ѯSTA_DAILY_02����Ϣ(����Ϣ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm ==============pangben modify 20110523 �������
	 */
	public TParm selectSTA_DAILY_02(String sta_date, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", sta_date);
		// ==============pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ==============pangben modify 20110523 stop
		TParm result = this.query("selectSTA_DAILY_02", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯSTA_DAILY_02����Ϣ(���ڶβ�ѯ)
	 * 
	 * @param parm
	 *            TParm �������:DATE_S ��ʼ���ڣ�DATE_E ��ֹ���� ��ѡ����:DEPT_CODE
	 * @return TParm
	 */
	public TParm selectSTA_DAILY_02(TParm parm) {
		TParm result = this.query("selectSTA_DAILY_02_DAYS", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯSTA_DAILY_02����Ϣ(����Ϣ)
	 * 
	 * @param sta_date
	 *            String
	 * @return TParm ==============pangben modify 20110523 ����������
	 */
	public TParm selectSTA_DAILY_02Day(String sta_date, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", sta_date);
		// ==============pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ==============pangben modify 20110523 stop
		TParm result = this.query("selectSTA_DAILY_02Day", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ѯSTA_DAILY_02����Ϣ(����Ϣ)
	 * 
	 * @param sta_date
	 *            String
	 * @return TParm ==============pangben modify 20110523 ����������
	 */
	public TParm selectSTA_DAILY_02Day(String sta_date, String dept_code,
			String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", sta_date);
		// ==============pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ==============pangben modify 20110523 stop
		// �������CODE����
		if (dept_code.trim().length() > 0)
			parm.setData("DEPT_CODE", dept_code);
		TParm result = this.query("selectSTA_DAILY_02Day", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������(��4������Ϊ��λ)
	 * 
	 * @param sta_date
	 *            String
	 * @return TParm ==============pangben modify 20110523 ����������
	 */
	public TParm selectData(String sta_date, String regionCode) {
		TParm result = new TParm();
		if (sta_date.trim().length() <= 0) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		// ==============pangben modify 20110523
		TParm DAILY_M = this.selectSTA_DAILY_02(sta_date, regionCode); // ��ѯSTA_DAILY_02������Ϣ
		if (DAILY_M.getErrCode() < 0) {
			err("ERR:" + DAILY_M.getErrCode() + DAILY_M.getErrText()
					+ DAILY_M.getErrName());
			return DAILY_M;
		}
		String StartDate = sta_date + "01"; // ÿ�µ�һ��
		// ��ȡ���·ݵ����һ��
		String EndDate = StringTool.getString(STATool.getInstance()
				.getLastDayOfMonth(sta_date), "yyyyMMdd");
		TParm DAILY_D_S = this.selectSTA_DAILY_02Day(StartDate, regionCode); // ��ѯSTA_DAILY_02������Ϣ(ÿ�µ�һ��)
		if (DAILY_D_S.getErrCode() < 0) {
			err("ERR:" + DAILY_D_S.getErrCode() + DAILY_D_S.getErrText()
					+ DAILY_D_S.getErrName());
			return DAILY_D_S;
		}

		TParm DAILY_D_E = this.selectSTA_DAILY_02Day(EndDate, regionCode); // ��ѯSTA_DAILY_02������Ϣ(ÿ�����һ��)
		if (DAILY_D_E.getErrCode() < 0) {
			err("ERR:" + DAILY_D_E.getErrCode() + DAILY_D_E.getErrText()
					+ DAILY_D_E.getErrName());
			return DAILY_D_E;
		}
		// ��ѯ�����ļ�����
		TParm deptList = STATool.getInstance().getDeptByLevel(
				new String[] { "4" }, regionCode);// ========pangben modify
													// 20110523
		String deptCode = ""; // ��¼����CODE
		String IPD_DEPT_CODE = ""; // ��¼סԺ����CODE
//		String STATION_CODE = ""; // ��¼����CODE========pangben modify 20110523
		for (int i = 0; i < deptList.getCount(); i++) {
			deptCode = deptList.getValue("DEPT_CODE", i);
			IPD_DEPT_CODE = deptList.getValue("IPD_DEPT_CODE", i); // סԺ����CODE
//			STATION_CODE = deptList.getValue("STATION_CODE", i);// ��¼����CODE
			if (IPD_DEPT_CODE.trim().length() <= 0) { // �ж��Ƿ���סԺ���� ������Ǿͽ�����һ��ѭ��
				continue;
			}
			// ��������ֶεı���
			int DATA_01 = 0;
			int DATA_02 = 0;
			int DATA_03 = 0;
			int DATA_04 = 0;
			int DATA_05 = 0;
			int DATA_06 = 0;
			int DATA_07 = 0;
			int DATA_08 = 0;
			int DATA_09 = 0;
			int DATA_10 = 0;
			int DATA_11 = 0;
			int DATA_12 = 0;
			int DATA_13 = 0;
			int DATA_14 = 0;
			int DATA_15 = 0;
			int DATA_16 = 0;
			double DATA_17 = 0;
			double DATA_18 = 0;
			double DATA_19 = 0;
			double DATA_20 = 0;
			double DATA_21 = 0;
			double DATA_22 = 0;
			double DATA_23 = 0;
			double DATA_24 = 0;
			double DATA_25 = 0;
			int DATA_26 = 0;
			int DATA_27 = 0;
			String stationCode = ""; // ��¼����CODE
			// ѭ��ͳ��STA_DAILY_02��ÿ�µ�һ�죩����ֶε�����
			for (int j = 0; j < DAILY_D_S.getCount(); j++) {
				if (DAILY_D_S.getValue("DEPT_CODE", j).equals(deptCode)) {
					DATA_02 = DAILY_D_S.getInt("DATA_07", j); // �ڳ�ԭ����
				}
			}
			// ѭ��ͳ��STA_DAILY_02��ÿ�����һ�죩����ֶε�����
			for (int j = 0; j < DAILY_D_E.getCount(); j++) {
				if (DAILY_D_E.getValue("DEPT_CODE", j).equals(deptCode)) {
					DATA_01 = DAILY_D_E.getInt("DATA_17", j); // ��ĩʵ�д�
					DATA_13 = DAILY_D_E.getInt("DATA_16", j); // ��ĩ������
				}
			}
			// ѭ��ͳ��STA_DAILY_02�������ݣ�����ֶε�����
			for (int j = 0; j < DAILY_M.getCount(); j++) {
				if (DAILY_M.getValue("DEPT_CODE", j).equals(deptCode)) {
					stationCode = DAILY_M.getValue("STATION_CODE", j); // Ժ��
					DATA_03 = DAILY_M.getInt("DATA_08", j); // ������Ժ��
					DATA_04 = DAILY_M.getInt("DATA_08_1", j); // ����ת����
					DATA_05 = DAILY_M.getInt("DATA_09", j); // ��Ժ�ϼ�
					DATA_06 = DAILY_M.getInt("DATA_10", j); // ��Ժ����������
					DATA_07 = DAILY_M.getInt("DATA_11", j); // ����
					DATA_08 = DAILY_M.getInt("DATA_12", j); // ��ת
					DATA_09 = DAILY_M.getInt("DATA_13", j); // δ��
					DATA_10 = DAILY_M.getInt("DATA_14", j); // ����
					DATA_11 = DAILY_M.getInt("DATA_15", j); // ����
					DATA_12 = DAILY_M.getInt("DATA_15_1", j); // ת������
					DATA_14 = DAILY_M.getInt("DATA_21", j); // ��Ժ��ռ���ܴ�����
					DATA_15 = DAILY_M.getInt("DATA_18", j); // ʵ�ʿ����ܴ�����
					DATA_16 = DAILY_M.getInt("DATA_20", j); // ʵ��ռ���ܴ�����
					DATA_26 = STATool.getInstance().getDaysOfMonth(sta_date); // ��׼����������
																				// ��������
					DATA_27 = DAILY_M.getInt("DATA_17", j); // ƽ�����Ų�����
				}
				if (DATA_15 != 0) {
					DATA_17 = (double) DATA_16 / (double) DATA_15 * 100; // ����ʹ����
				}
				if (DATA_06 != 0) {
					DATA_19 = (double) DATA_07 / (double) DATA_06 * 100; // ������
					DATA_20 = (double) DATA_08 / (double) DATA_06 * 100; // ��ת��
					DATA_21 = (double) DATA_09 / (double) DATA_06 * 100; // δ����
					DATA_22 = (double) DATA_10 / (double) DATA_06 * 100; // ������
				}
				if (DATA_05 != 0) {
					DATA_24 = (double) DATA_14 / (double) DATA_05; // ��Ժ��ƽ��סԺ��
				}
				if (DATA_26 != 0) {
					DATA_23 = new BigDecimal((double) DATA_16
							/ (double) DATA_26).setScale(0,
							BigDecimal.ROUND_HALF_UP).intValue();// ƽ��סԺ����
				}
				if (DATA_27 != 0) {
					DATA_18 = (double) (DATA_07 + DATA_08 + DATA_09 + DATA_10
							+ DATA_11 + DATA_12)
							/ (double) DATA_27;// ������ת
					DATA_25 = (double) DATA_16 / (double) DATA_27;
				}
			}
			// �������
			result.addData("STA_DATE", sta_date);
			result.addData("DEPT_CODE", deptCode);
			result.addData("REGION_CODE", regionCode);
			result.addData(
					"STATION_CODE",
					null == stationCode || stationCode.length() == 0 ? IPD_DEPT_CODE
							: stationCode);// ==========pangben modify 20110523
			result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
			result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
			result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
			result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
			result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
			result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
			result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
			result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
			result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
			result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
			result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
			result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
			result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
			result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
			result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
			result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
			result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
			result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
			result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
			result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
			result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
			result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
			result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
			result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
			result.addData("DATA_25", DATA_25 == 0 ? "" : Math.round(DATA_25));
			result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
			result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
			result.addData("CONFIRM_FLG", "N");
			result.addData("CONFIRM_USER", "");
			result.addData("CONFIRM_DATE", "");
			result.addData("OPT_USER", "");
			result.addData("OPT_TERM", "");
		}
		return result;
	}

	/**
	 * ��������(��4������Ϊ��λ)
	 * 
	 * @param sta_date
	 *            String
	 * @return TParm
	 */
	public TParm selectData(TParm p) {
		TParm result = new TParm();
		if (p == null) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		TParm DAILY_M = this.selectSTA_DAILY_02(p); // ��ѯSTA_DAILY_02���ڶ���Ϣ
		// System.out.println(" DAILY_M ---------"+DAILY_M);
		if (DAILY_M.getErrCode() < 0) {
			err("ERR:" + DAILY_M.getErrCode() + DAILY_M.getErrText()
					+ DAILY_M.getErrName());
			return DAILY_M;
		}
		String StartDate = p.getValue("DATE_S"); // ��ʼ����
		String EndDate = p.getValue("DATE_E"); // ��ֹ����
		// =============pangben modify 20110523 ����������
		TParm DAILY_D_S = this.selectSTA_DAILY_02Day(StartDate,
				p.getValue("DEPT_CODE"), p.getValue("REGION_CODE")); // ��ѯSTA_DAILY_02������Ϣ(��ʼ����)
		// System.out.println(" DAILY_D_S ---------"+DAILY_D_S);
		if (DAILY_D_S.getErrCode() < 0) {
			err("ERR:" + DAILY_D_S.getErrCode() + DAILY_D_S.getErrText()
					+ DAILY_D_S.getErrName());
			return DAILY_D_S;
		}
		// ======================pangben modify 20110523 ����������
		TParm DAILY_D_E = this.selectSTA_DAILY_02Day(EndDate,
				p.getValue("DEPT_CODE"), p.getValue("REGION_CODE")); // ��ѯSTA_DAILY_02������Ϣ(��ֹ����)
		// System.out.println("DAILY_D_E-=---------------------"+DAILY_D_E);
		if (DAILY_D_E.getErrCode() < 0) {
			err("ERR:" + DAILY_D_E.getErrCode() + DAILY_D_E.getErrText()
					+ DAILY_D_E.getErrName());
			return DAILY_D_E;
		}
		// �жϲ�ѯ�������Ƿ�������ţ����ָ���˲�����ôֻͳ�Ƹò��ŵ���Ϣ �������ź���
		TParm deptList = new TParm();
		if (p.getValue("DEPT_CODE").trim().length() <= 0) {
			// ��ѯ�����ļ�����
			deptList = STATool.getInstance().getDeptByLevel(
					new String[] { "4" }, p.getValue("REGION_CODE"));// ==============pangben
																		// modify
																		// 20110523
		} else {
			// ��ѯָ���Ĳ���
			deptList = STADeptListTool.getInstance().selectNewIPDDeptCode(
					p.getValue("DEPT_CODE"), p.getValue("REGION_CODE"));// =========pangben
																		// modify
																		// 20110523
		}
		// System.out.println("----------deptList---------------------"+deptList);
		String deptCode = ""; // ��¼����CODE
		String IPD_DEPT_CODE = ""; // ��¼סԺ����CODE
//		String STATION_CODE = "";// ��¼����CODE=========pangben modify 20110523
		int TDATA_01 = 0;
		int TDATA_02 = 0;
		int TDATA_03 = 0;
		int TDATA_04 = 0;
		int TDATA_05 = 0;
		int TDATA_06 = 0;
		int TDATA_07 = 0;
		int TDATA_08 = 0;
		int TDATA_09 = 0;
		int TDATA_10 = 0;
		int TDATA_11 = 0;
		int TDATA_12 = 0;
		int TDATA_13 = 0;
		int TDATA_14 = 0;
		int TDATA_15 = 0;
		int TDATA_16 = 0;
		double TDATA_17 = 0;
		double TDATA_18 = 0;
		double TDATA_19 = 0;
		double TDATA_20 = 0;
		double TDATA_21 = 0;
		double TDATA_22 = 0;
		double TDATA_23 = 0;
		double TDATA_24 = 0;
		double TDATA_25 = 0;
		double TDATA_26 = 0;
		double TDATA_27 = 0;
		for (int i = 0; i < deptList.getCount(); i++) {
			deptCode = deptList.getValue("DEPT_CODE", i);
			IPD_DEPT_CODE = deptList.getValue("IPD_DEPT_CODE", i); // סԺ����CODE
			if (IPD_DEPT_CODE.trim().length() <= 0) { // �ж��Ƿ���סԺ���� ������Ǿͽ�����һ��ѭ��
				continue;
			}
			// ��������ֶεı���
			int DATA_01 = 0;
			int DATA_02 = 0;
			int DATA_03 = 0;
			int DATA_04 = 0;
			int DATA_05 = 0;
			int DATA_06 = 0;
			int DATA_07 = 0;
			int DATA_08 = 0;
			int DATA_09 = 0;
			int DATA_10 = 0;
			int DATA_11 = 0;
			int DATA_12 = 0;
			int DATA_13 = 0;
			int DATA_14 = 0;
			int DATA_15 = 0;
			int DATA_16 = 0;
			double DATA_17 = 0;
			double DATA_18 = 0;
			double DATA_19 = 0;
			double DATA_20 = 0;
			double DATA_21 = 0;
			double DATA_22 = 0;
			double DATA_23 = 0;
			double DATA_24 = 0;
			double DATA_25 = 0;
			int DATA_26 = 0;
			int DATA_27 = 0;
			String stationCode = ""; // ��¼����CODE
			// ѭ��ͳ��STA_DAILY_02����ʼ���ڣ�����ֶε�����
			for (int j = 0; j < DAILY_D_S.getCount(); j++) {
				if (DAILY_D_S.getValue("DEPT_CODE", j).equals(deptCode)) {
					DATA_02 = DAILY_D_S.getInt("DATA_07", j); // �ڳ�ԭ����
				}
			}
			// ѭ��ͳ��STA_DAILY_02����ֹ���ڣ�����ֶε�����
			for (int j = 0; j < DAILY_D_E.getCount(); j++) {
				if (DAILY_D_E.getValue("DEPT_CODE", j).equals(deptCode)) {
					DATA_01 = DAILY_D_E.getInt("DATA_17", j); // ��ĩʵ�д�
					DATA_13 = DAILY_D_E.getInt("DATA_16", j); // ��ĩ������
				}
			}
			// ѭ��ͳ��STA_DAILY_02�������ݣ�����ֶε�����
			for (int j = 0; j < DAILY_M.getCount(); j++) {
				if (DAILY_M.getValue("DEPT_CODE", j).equals(deptCode)) {
					stationCode = DAILY_M.getValue("STATION_CODE", j); // Ժ��
					DATA_03 = DAILY_M.getInt("DATA_08", j); // ������Ժ��
					DATA_04 = DAILY_M.getInt("DATA_08_1", j); // ����ת����
					DATA_05 = DAILY_M.getInt("DATA_09", j); // ��Ժ�ϼ�
					DATA_06 = DAILY_M.getInt("DATA_10", j); // ��Ժ����������
					DATA_07 = DAILY_M.getInt("DATA_11", j); // ����
					DATA_08 = DAILY_M.getInt("DATA_12", j); // ��ת
					DATA_09 = DAILY_M.getInt("DATA_13", j); // δ��
					DATA_10 = DAILY_M.getInt("DATA_14", j); // ����
					DATA_11 = DAILY_M.getInt("DATA_15", j); // ����
					DATA_12 = DAILY_M.getInt("DATA_15_1", j); // ת������
					DATA_14 = DAILY_M.getInt("DATA_21", j); // ��Ժ��ռ���ܴ�����
					DATA_15 = DAILY_M.getInt("DATA_18", j); // ʵ�ʿ����ܴ�����
					DATA_16 = DAILY_M.getInt("DATA_20", j); // ʵ��ռ���ܴ�����
					DATA_26 = StringTool.getDateDiffer(
							StringTool.getTimestamp(EndDate, "yyyyMMdd"),
							StringTool.getTimestamp(StartDate, "yyyyMMdd")); // ��׼����������
																				// ��ʼ���ڼ�ȥ��ֹ����
					DATA_27 = DAILY_M.getInt("DATA_17", j); // ƽ�����Ų�����
				}
				if (DATA_15 != 0) {
					DATA_17 = (double) DATA_16 / (double) DATA_15 * 100; // ����ʹ����
				}
				if (DATA_06 != 0) {
					DATA_19 = (double) DATA_07 / (double) DATA_06 * 100; // ������
					DATA_20 = (double) DATA_08 / (double) DATA_06 * 100; // ��ת��
					DATA_21 = (double) DATA_09 / (double) DATA_06 * 100; // δ����
					DATA_22 = (double) DATA_10 / (double) DATA_06 * 100; // ������
				}
				if (DATA_05 != 0) {
					DATA_24 = (double) DATA_14 / (double) DATA_05; // ��Ժ��ƽ��סԺ��
				}
				if (DATA_26 != 0) {
					DATA_23 = new BigDecimal((double) DATA_16
							/ (double) DATA_26).setScale(0,
							BigDecimal.ROUND_HALF_UP).intValue();// ƽ��סԺ����
				}
				if (DATA_27 != 0) {
					DATA_18 = (double) (DATA_07 + DATA_08 + DATA_09 + DATA_10
							+ DATA_11 + DATA_12)
							/ (double) DATA_27;// ������ת
					DATA_25 = (double) DATA_16 / (double) DATA_27;
				}
			}
			// �������
			result.addData("STA_DATE", StartDate + "-" + EndDate);
			result.addData("DEPT_CODE", deptCode);
			result.addData(
					"STATION_CODE",
					null == stationCode || stationCode.length() == 0 ? deptCode
							: stationCode);// ===========pangben modify 20110523
			result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
			result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
			result.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
			result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
			result.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
			result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
			result.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
			result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
			result.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
			result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
			result.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
			result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
			result.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
			result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
			result.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
			result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
			result.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
			result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
			result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
			result.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
			result.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
			result.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
			result.addData("DATA_23", DATA_23 == 0 ? "" : DATA_23);
			result.addData("DATA_24", DATA_24 == 0 ? "" : DATA_24);
			result.addData("DATA_25", DATA_25 == 0 ? "" : Math.round(DATA_25));
			result.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
			result.addData("DATA_27", DATA_27 == 0 ? "" : DATA_27);
			result.addData("CONFIRM_FLG", "N");
			result.addData("CONFIRM_USER", "");
			result.addData("CONFIRM_DATE", "");
			result.addData("OPT_USER", "");
			result.addData("OPT_TERM", "");
			TDATA_01 += DATA_01;
			TDATA_02 += DATA_02;
			TDATA_03 += DATA_03;
			TDATA_04 += DATA_04;
			TDATA_05 += DATA_05;
			TDATA_06 += DATA_06;
			TDATA_07 += DATA_07;
			TDATA_08 += DATA_08;
			TDATA_09 += DATA_09;
			TDATA_10 += DATA_10;
			TDATA_11 += DATA_11;
			TDATA_12 += DATA_12;
			TDATA_13 += DATA_13;
			TDATA_14 += DATA_14;
			TDATA_15 += DATA_15;
			TDATA_16 += DATA_16;
			TDATA_17 += DATA_17;
			TDATA_18 += DATA_18;
			TDATA_19 += DATA_19;
			TDATA_20 += DATA_20;
			TDATA_25 += DATA_25;
			TDATA_26 = DATA_26;
		}
		if (TDATA_15 != 0) {
			TDATA_17 = (double) TDATA_16 / (double) TDATA_15 * 100; // ����ʹ����
		}
		if (TDATA_06 != 0) {
			TDATA_19 = (double) TDATA_07 / (double) TDATA_06 * 100; // ������
			TDATA_20 = (double) TDATA_08 / (double) TDATA_06 * 100; // ��ת��
			TDATA_21 = (double) TDATA_09 / (double) TDATA_06 * 100; // δ����
			TDATA_22 = (double) TDATA_10 / (double) TDATA_06 * 100; // ������
		}
		if (TDATA_05 != 0) {
			TDATA_24 = (double) TDATA_14 / (double) TDATA_05; // ��Ժ��ƽ��סԺ��
		}
		if (TDATA_26 != 0) {
			TDATA_23 = new BigDecimal((double) TDATA_16 / (double) TDATA_26)
					.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();// ƽ��סԺ����
		}
		if (TDATA_27 != 0) {
			TDATA_18 = (double) (TDATA_07 + TDATA_08 + TDATA_09 + TDATA_10
					+ TDATA_11 + TDATA_12)
					/ (double) TDATA_27;// ������ת
			TDATA_25 = (double) TDATA_16 / (double) TDATA_27;
		}
		result.addData("DEPT_CODE", "�ϼ�:");
		result.addData("DATA_01", TDATA_02 == 0 ? "" : TDATA_01);
		result.addData("DATA_02", TDATA_02 == 0 ? "" : TDATA_02);
		result.addData("DATA_03", TDATA_03 == 0 ? "" : TDATA_03);
		result.addData("DATA_04", TDATA_04 == 0 ? "" : TDATA_04);
		result.addData("DATA_05", TDATA_05 == 0 ? "" : TDATA_05);
		result.addData("DATA_06", TDATA_06 == 0 ? "" : TDATA_06);
		result.addData("DATA_07", TDATA_07 == 0 ? "" : TDATA_07);
		result.addData("DATA_08", TDATA_08 == 0 ? "" : TDATA_08);
		result.addData("DATA_09", TDATA_09 == 0 ? "" : TDATA_09);
		result.addData("DATA_10", TDATA_10 == 0 ? "" : TDATA_10);
		result.addData("DATA_11", TDATA_11 == 0 ? "" : TDATA_11);
		result.addData("DATA_12", TDATA_12 == 0 ? "" : TDATA_12);
		result.addData("DATA_13", TDATA_13 == 0 ? "" : TDATA_13);
		result.addData("DATA_14", TDATA_14 == 0 ? "" : TDATA_14);
		result.addData("DATA_15", TDATA_15 == 0 ? "" : TDATA_15);
		result.addData("DATA_16", TDATA_16 == 0 ? "" : TDATA_16);
		result.addData("DATA_17", TDATA_17 == 0 ? "" : TDATA_17);
		result.addData("DATA_18", TDATA_18 == 0 ? "" : TDATA_18);
		result.addData("DATA_19", TDATA_19 == 0 ? "" : TDATA_19);
		result.addData("DATA_20", TDATA_20 == 0 ? "" : TDATA_20);
		result.addData("DATA_21", TDATA_21 == 0 ? "" : TDATA_21);
		result.addData("DATA_22", TDATA_22 == 0 ? "" : TDATA_22);
		result.addData("DATA_23", TDATA_23 == 0 ? "" : TDATA_23);
		result.addData("DATA_24", TDATA_24 == 0 ? "" : TDATA_24);
		result.addData("DATA_25", TDATA_25 == 0 ? "" : Math.round(TDATA_25));
		result.addData("DATA_26", TDATA_26 == 0 ? "" : TDATA_26);
		result.addData("DATA_27", TDATA_27 == 0 ? "" : TDATA_27);
		result.addData("STA_DATE", "");
        result.addData("STATION_CODE", "");
        result.addData("REGION_CODE", "");
		return result;
	}

	/**
	 * ɾ����STA_IN_03����
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ============pangben modify 20110523 ����������
	 */
	public TParm deleteSTA_IN_03(String STA_DATE, String regionCode,
			TConnection conn) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// ============pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110523 stop
		TParm result = this.update("deleteSTA_IN_03", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����STA_IN_03����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertSTA_IN_03(TParm parm, TConnection conn) {
		TParm result = this.update("insertSTA_IN_03", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����STA_IN_03����Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm.getCount("STA_DATE") <= 0) {
			result.setErr(-1, "û�пɲ��������");
			return result;
		}
		String STA_DATE = parm.getValue("STA_DATE", 0);
		// =============pangben modify 20110523 start
		String regionCode = parm.getValue("REGION_CODE", 0);
		// =============pangben modify 20110523 stop
		if (STA_DATE.trim().length() <= 0) {
			result.setErr(-1, "STA_DATE����Ϊ��");
			return result;
		}
		// =============pangben modify 20110523
		result = this.deleteSTA_IN_03(STA_DATE, regionCode, conn); // ɾ�������ڵ�����
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm insert = null;
		for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
			insert = new TParm();
			insert.setData("STA_DATE", parm.getValue("STA_DATE", i));
			insert.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			insert.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
			insert.setData("DATA_01", parm.getValue("DATA_01", i));
			insert.setData("DATA_02", parm.getValue("DATA_02", i));
			insert.setData("DATA_03", parm.getValue("DATA_03", i));
			insert.setData("DATA_04", parm.getValue("DATA_04", i));
			insert.setData("DATA_05", parm.getValue("DATA_05", i));
			insert.setData("DATA_06", parm.getValue("DATA_06", i));
			insert.setData("DATA_07", parm.getValue("DATA_07", i));
			insert.setData("DATA_08", parm.getValue("DATA_08", i));
			insert.setData("DATA_09", parm.getValue("DATA_09", i));
			insert.setData("DATA_10", parm.getValue("DATA_10", i));
			insert.setData("DATA_11", parm.getValue("DATA_11", i));
			insert.setData("DATA_12", parm.getValue("DATA_12", i));
			insert.setData("DATA_13", parm.getValue("DATA_13", i));
			insert.setData("DATA_14", parm.getValue("DATA_14", i));
			insert.setData("DATA_15", parm.getValue("DATA_15", i));
			insert.setData("DATA_16", parm.getValue("DATA_16", i));
			insert.setData("DATA_17", parm.getValue("DATA_17", i));
			insert.setData("DATA_18", parm.getValue("DATA_18", i));
			insert.setData("DATA_19", parm.getValue("DATA_19", i));
			insert.setData("DATA_20", parm.getValue("DATA_20", i));
			insert.setData("DATA_21", parm.getValue("DATA_21", i));
			insert.setData("DATA_22", parm.getValue("DATA_22", i));
			insert.setData("DATA_23", parm.getValue("DATA_23", i));
			insert.setData("DATA_24", parm.getValue("DATA_24", i));
			insert.setData("DATA_25", parm.getValue("DATA_25", i));
			insert.setData("DATA_26", parm.getValue("DATA_26", i));
			insert.setData("DATA_27", parm.getValue("DATA_27", i));
			insert.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
			insert.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
			insert.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", i));
			insert.setData("OPT_USER", parm.getValue("OPT_USER", i));
			insert.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// =============panben modify 20110523 start
			insert.setData("REGION_CODE", parm.getValue("REGION_CODE", i));
			// =============panben modify 20110523 stop
			result = this.insertSTA_IN_03(insert, conn); // ����������
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ��ѯSTA_IN_03������
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ============pangben modify 20110524 ����������
	 */
	public TParm selectSTA_IN_03(String STA_DATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// ============pangben modify 20110524 start
		parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110524 stop
		TParm result = this.query("selectSTA_IN_03", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ͳ����������ת���������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������ ����Ҫ�����3������
	 * ���²�ѯת����
	 * 
	 * @param dept
	 *            String[] ����
	 * @return int
	 */
	public int getDept_TranOutNum(String[] dept, String DATE_S, String DATE_E) {
		if (dept.length <= 0) {
			return 0;
		}
		String sql = "SELECT COUNT(A.MR_NO) AS NUM "
				+ " FROM ADM_CHG A,ADM_CHG B " + " WHERE A.MR_NO = B.MR_NO "
				+ " AND A.CASE_NO=B.CASE_NO " + " AND A.CHG_DATE=B.CHG_DATE "
				+ " AND A.PSF_KIND='INPR' " + " AND B.PSF_KIND='OUPR' "
				+ " AND B.DEPT_CODE IN (#) " + " AND A.DEPT_CODE NOT IN (#) "
				+ " AND B.CHG_DATE BETWEEN TO_DATE('" + DATE_S
				+ "','YYYYMMDD') AND TO_DATE('" + DATE_E
				+ "'||'235959','YYYYMMDDHH24MISS')";
		String deptList = "";
		for (int i = 0; i < dept.length; i++) {
			deptList += "'" + dept[i] + "',";
		}
		sql = sql.replace("#", deptList.substring(0, deptList.length() - 1));
		// System.out.println("sqlOut:"+sql);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return 0;
		}
		return result.getInt("NUM", 0);
	}

	/**
	 * ͳ����������ת������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������ ����Ҫ�����3������
	 * ���²�ѯת����
	 * 
	 * @param dept
	 *            String[] ����
	 * @return int
	 */
	public int getDept_TranInNum(String[] dept, String DATE_S, String DATE_E) {
		if (dept.length <= 0) {
			return 0;
		}
		String sql = "SELECT COUNT(A.MR_NO) AS NUM "
				+ " FROM ADM_CHG A,ADM_CHG B " + " WHERE A.MR_NO = B.MR_NO "
				+ " AND A.CASE_NO=B.CASE_NO " + " AND A.CHG_DATE=B.CHG_DATE "
				+ " AND A.PSF_KIND='OUPR' " + " AND B.PSF_KIND='INPR' "
				+ " AND B.DEPT_CODE IN (#) " + " AND A.DEPT_CODE NOT IN (#) "
				+ " AND B.CHG_DATE BETWEEN TO_DATE('" + DATE_S
				+ "','YYYYMMDD') AND TO_DATE('" + DATE_E
				+ "'||'235959','YYYYMMDDHH24MISS')";
		String deptList = "";
		for (int i = 0; i < dept.length; i++) {
			deptList += "'" + dept[i] + "',";
		}
		sql = sql.replace("#", deptList.substring(0, deptList.length() - 1));
		TParm result = new TParm();
		// System.out.println("sqlIn:" + sql);
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return 0;
		}
		return result.getInt("NUM", 0);
	}

	/**
	 * ����2�����ҵ�CODE��ѯ��2�����ҵ�ת������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������
	 * ����2������Ҫ��3�����ҵ�ת������Ϊ��С��λ�����м���
	 * 
	 * @param DEPT_CODE_2
	 *            String
	 * @param DATE_S
	 *            String
	 * @param DATE_E
	 *            String
	 * @return int =================pangben modify 20110523 ����������
	 */
	public int getTranInNumForDEPT_2(String DEPT_CODE_2, String DATE_S,
			String DATE_E, String regionCode) {
		TParm dept3 = STATool.getInstance().getDeptByLevel(
				new String[] { "3" }, regionCode);// ��ѯ����3������======pangben
													// modify 20110523 ����������
		TParm dept4 = STATool.getInstance().getDeptByLevel(
				new String[] { "4" }, regionCode);// ��ѯ����4������======pangben
													// modify 20110523 ����������
		// ��ѯ�������ڸ�2�����ҵ�3������
		TParm deptList3 = new TParm();
		for (int i = 0; i < dept3.getCount(); i++) {
			if (DEPT_CODE_2.equals(dept3.getValue("DEPT_CODE", i).substring(0,
					3))) {
				deptList3.addData("DEPT_CODE", dept3.getValue("DEPT_CODE", i));
				deptList3.addData("DEPT_DESC", dept3.getValue("DEPT_DESC", i));
			}
		}
		// ��¼ÿ�����������µ��ļ������б� �Զ��ŷָ�
		String deptList4 = "";
		int tranNum = 0;// �ۼӼ�¼ת�ƴ���
		for (int i = 0; i < deptList3.getCount("DEPT_CODE"); i++) {
			deptList4 = "";// ��¼ÿ�����������µ������ļ�����
			for (int j = 0; j < dept4.getCount(); j++) {
				if (deptList3.getValue("DEPT_CODE", i).equals(
						dept4.getValue("DEPT_CODE", j).substring(0, 5))) {
					deptList4 += dept4.getValue("IPD_DEPT_CODE", j) + ",";
				}
			}
			if (deptList4.length() > 0) {
				tranNum += this.getDept_TranInNum(
						deptList4.substring(0, deptList4.length() - 1).split(
								","), DATE_S, DATE_E);
			}
		}
		return tranNum;
	}

	/**
	 * ����2�����ҵ�CODE��ѯ��2�����ҵ�ת������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������
	 * ����2������Ҫ��3�����ҵ�ת������Ϊ��С��λ�����м���
	 * 
	 * @param DEPT_CODE_2
	 *            String
	 * @param DATE_S
	 *            String
	 * @param DATE_E
	 *            String
	 * @return int =================pangben modify 20110523 ����������
	 */
	public int getTranOutNumForDEPT_2(String DEPT_CODE_2, String DATE_S,
			String DATE_E, String regionCode) {
		TParm dept3 = STATool.getInstance().getDeptByLevel(
				new String[] { "3" }, regionCode); // ��ѯ����3������======pangben
													// modify 20110523 ����������
		TParm dept4 = STATool.getInstance().getDeptByLevel(
				new String[] { "4" }, regionCode); // ��ѯ����4������======pangben
													// modify 20110523 ����������
		// ��ѯ�������ڸ�2�����ҵ�3������
		TParm deptList3 = new TParm();
		for (int i = 0; i < dept3.getCount(); i++) {
			if (DEPT_CODE_2.equals(dept3.getValue("DEPT_CODE", i).substring(0,
					3))) {
				deptList3.addData("DEPT_CODE", dept3.getValue("DEPT_CODE", i));
				deptList3.addData("DEPT_DESC", dept3.getValue("DEPT_DESC", i));
			}
		}
		// ��¼ÿ�����������µ��ļ������б� �Զ��ŷָ�
		String deptList4 = "";
		int tranNum = 0;// �ۼӼ�¼ת�ƴ���
		for (int i = 0; i < deptList3.getCount("DEPT_CODE"); i++) {
			deptList4 = "";// ��¼ÿ�����������µ������ļ�����
			for (int j = 0; j < dept4.getCount(); j++) {
				if (deptList3.getValue("DEPT_CODE", i).equals(
						dept4.getValue("DEPT_CODE", j).substring(0, 5))) {
					deptList4 += dept4.getValue("IPD_DEPT_CODE", j) + ",";
				}
			}
			if (deptList4.length() > 0) {
				tranNum += this.getDept_TranOutNum(
						deptList4.substring(0, deptList4.length() - 1).split(
								","), DATE_S, DATE_E);
			}
		}
		return tranNum;
	}

	/**
	 * ����1�����ҵ�CODE��ѯ��1�����ҵ�ת������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������
	 * ����1������Ҫ��3�����ҵ�ת������Ϊ��С��λ�����м���
	 * 
	 * @param DEPT_CODE_1
	 *            String һ������CODE
	 * @param DATE_S
	 *            String
	 * @param DATE_E
	 *            String
	 * @return int ======pangben modify 20110524 ����������
	 */
	public int getTranInNumForDEPT_1(String DEPT_CODE_1, String DATE_S,
			String DATE_E, String regionCode) {
		TParm dept2 = STATool.getInstance().getDeptByLevel(
				new String[] { "2" }, regionCode);// ��ѯ����2������======pangben
													// modify 20110524 ����������
		int tranNum = 0;
		for (int i = 0; i < dept2.getCount(); i++) {
			if (DEPT_CODE_1.equals(dept2.getValue("DEPT_CODE", i).substring(0,
					1))) {
				tranNum += getTranInNumForDEPT_2(
						dept2.getValue("DEPT_CODE", i), DATE_S, DATE_E,
						regionCode);// ======pangben modify 20110524 ����������
			}
		}
		return tranNum;
	}

	/**
	 * ����1�����ҵ�CODE��ѯ��1�����ҵ�ת������� ���ݽ����Ժ������ͳ�Ʊ����� ����ͬһ�������ҵ��ļ�����֮���ת�Ʋ�����3�����ҵ�ת������
	 * ����1������Ҫ��3�����ҵ�ת������Ϊ��С��λ�����м���
	 * 
	 * @param DEPT_CODE_1
	 *            String һ������CODE
	 * @param DATE_S
	 *            String
	 * @param DATE_E
	 *            String
	 * @return int ======pangben modify 20110524 ����������
	 */
	public int getTranOutNumForDEPT_1(String DEPT_CODE_1, String DATE_S,
			String DATE_E, String regionCode) {
		TParm dept2 = STATool.getInstance().getDeptByLevel(
				new String[] { "2" }, regionCode);// ��ѯ����2������======pangben
													// modify 20110524 ����������
		int tranNum = 0;
		for (int i = 0; i < dept2.getCount(); i++) {
			if (DEPT_CODE_1.equals(dept2.getValue("DEPT_CODE", i).substring(0,
					1))) {
				tranNum += getTranOutNumForDEPT_2(
						dept2.getValue("DEPT_CODE", i), DATE_S, DATE_E,
						regionCode);// ======pangben modify 20110524 ����������
			}
		}
		return tranNum;
	}
	/**
     * �޸�STA_IN_03����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateSTA_IN_03(TParm parm, TConnection conn) {
        TParm  result = this.update("updateSTA_IN_03", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

	/**
	 * ��ѯ��Ժ����סԺ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectMRO_DAYS(TParm parm) {
		TParm result = this.query("selectMRO_DAYS", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
