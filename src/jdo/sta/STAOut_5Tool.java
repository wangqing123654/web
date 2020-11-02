package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ��������ҽԺ���ֲ���סԺҽ�Ʒ����걨����ͳ5��3��
 * </p>
 * 
 * <p>
 * Description: ��������ҽԺ���ֲ���סԺҽ�Ʒ����걨����ͳ5��3��
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
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_5Tool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static STAOut_5Tool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static STAOut_5Tool getInstance() {
		if (instanceObject == null)
			instanceObject = new STAOut_5Tool();
		return instanceObject;
	}

	public STAOut_5Tool() {
		setModuleName("sta\\STAOut_5Module.x");
		onInit();
	}

	/**
	 * ��������������Ҫ��30����ͳ��SQL���
	 * 
	 * @param Condition
	 *            String
	 * @return String ==========pangben modify 20110523 ����������
	 */
	private String getSQL(String Condition, String StartDate, String EndDate,
			String regionCode) {
		String sql = "";
		// ====pangben modify 20110523 start
		String region = "";
		if (null != regionCode && regionCode.length() > 0)
			region = " AND C.REGION_CODE='" + regionCode + "' ";
		// ====pangben modify 20110523 stop

		if (Condition.trim().length() > 0) {
			sql = "SELECT A.IPD_CHARGE_CODE, SUM (NVL (B.TOT_AMT, 0)) TOT_AMT,SUM (NVL (B.OWN_AMT, 0)) OWN_AMT "+
            "FROM SYS_CHARGE_HOSP A, "+
            "(SELECT   HEXP_CODE, SUM (TOT_AMT) TOT_AMT, SUM (OWN_AMT) OWN_AMT "+
           "FROM MRO_RECORD C,IBS_ORDD D "+
              "WHERE C.CASE_NO = D.CASE_NO "+
             " AND  C.OUT_DATE BETWEEN TO_DATE('"
					+ StartDate
					+ "','YYYYMMDD') AND TO_DATE('"
					+ EndDate
					+ "235959','YYYYMMDDHH24MISS') " + region+" AND (" + Condition+")"+
            " GROUP BY D.HEXP_CODE) B  "+
            " WHERE A.CHARGE_HOSP_CODE = B.HEXP_CODE(+) "+
            "  GROUP BY A.IPD_CHARGE_CODE "+
            "  ORDER BY A.IPD_CHARGE_CODE ";
		}
		return sql;
	}

	/**
	 * ���ݴ����SQL��� ��ѯMRO_RECORD��ͼ
	 * 
	 * @param sql
	 *            String
	 * @return TParm ==========pangben modify 20110523 ����������
	 */
	public TParm getRecordSum(String Condition, String StartDate,
			String EndDate, String regionCode) {
		TParm result = new TParm();
		String sql = this.getSQL(Condition, StartDate, EndDate, regionCode);
		if (sql.trim().length() <= 0) {
			result.setErr(-1, "SQL��䲻��Ϊ��");
			return result;
		}
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��ȡ30���ֵ�ͳ����Ϣ
	 * 
	 * @param parm
	 *            TParm ���������DATE_S:��ʼ���ڣ� DATE_E:��ֹ����
	 * @return TParm
	 */
	public TParm selectDiseaseSum(TParm parm) {
		TParm result = new TParm();
		String StartDate = parm.getValue("DATE_S");// ��ʼ����
		String EndDate = parm.getValue("DATE_E");// ��ֹ����
		String regionCode = parm.getValue("REGION_CODE");// =========pangben
															// modify 20110523
		TParm Disease = STA30ListTool.getInstance().selectData(new TParm());// ��ȡ30�����б�
		int DATA1 = 0;
		int DATA2 = 0;
		int DATA3 = 0;
		double DATA4 = 0;
		double DATA5 = 0;
		double DATA6 = 0;
		double DATA7 = 0;
		double DATA8 = 0;
		// ����30���ֽ���ѭ����������
		TParm RecordSum = null;
		String str="";
		for (int i = 0; i < Disease.getCount("SEQ"); i++) {
			String CONDITION = Disease.getValue("CONDITION", i);
			if(CONDITION.equals(""))
				continue;
			if(str.length()>0)
				str+=" OR "+CONDITION;
			else
				str=CONDITION;
			// =========pangben modify 20110523 start
			RecordSum = getRecordSum(CONDITION, StartDate, EndDate, regionCode);// ��ȡһ�ֲ��ֵ�ֵ
			// =========pangben modify 20110523 stop
			// ��һ�ֲ��ֵ�ֵת��Ϊһ������
			TParm row = this.getRowParm(RecordSum, "",
					Disease.getValue("SEQ", i),
					Disease.getValue("ICD_DESC", i),
					Disease.getValue("DEPT_DESC", i));
			result.setRowData(
					i,
					row,
					0,
					"STA_DATE;SEQ;ICD_DESC;DEPT_DESC;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;CONFIRM_FLG;CONFIRM_USER;CONFIRM_DATE;OPT_USER;OPT_TERM");
		}
		RecordSum = getRecordSum(str, StartDate, EndDate, regionCode);// �ϼ�
		TParm row = this.getRowParm(RecordSum, "",
				"99",
				"�ܼ�:",
				"�ܼ�:");
		result.setRowData(
				Disease.getCount("SEQ"),
				row,
				0,
				"STA_DATE;SEQ;ICD_DESC;DEPT_DESC;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;CONFIRM_FLG;CONFIRM_USER;CONFIRM_DATE;OPT_USER;OPT_TERM");
		return result;
	}

	/**
	 * ���ݲ�ѯ����MroRecord������ȡÿ�ֲ��ֵľ�������
	 * 
	 * @param RecordSum
	 *            TParm
	 * @return TParm
	 */
	public TParm getRowParm(TParm RecordSum, String STA_DATE, String SEQ,
			String ICD_DESC, String DEPT_DESC) {
		TParm result = new TParm();
		// ��ʼ����
		result.addData("STA_DATE", STA_DATE);// ����
		result.addData("SEQ", SEQ);// ���
		result.addData("ICD_DESC", ICD_DESC);
		result.addData("DEPT_DESC", DEPT_DESC);
		result.addData("DATA_01", RecordSum.getInt("NUM", 0));// ��Ժ������
		result.addData("DATA_02", RecordSum.getInt("SUMDAYS", 0));// ��Ժ����ռ���ܴ�����
		result.addData("DATA_03",
				StringTool.round(RecordSum.getDouble("AVGDAYS", 0), 1)); // ��Ժ����ƽ��סԺ��
		// ��������ܼ�
		double chargeSum = RecordSum.getDouble("CHARGE_01", 0)
				+ RecordSum.getDouble("CHARGE_02", 0)
				+ RecordSum.getDouble("CHARGE_03", 0)
				+ RecordSum.getDouble("CHARGE_04", 0)
				+ RecordSum.getDouble("CHARGE_05", 0)
				+ RecordSum.getDouble("CHARGE_06", 0)
				+ RecordSum.getDouble("CHARGE_07", 0)
				+ RecordSum.getDouble("CHARGE_08", 0)
				+ RecordSum.getDouble("CHARGE_09", 0)
				+ RecordSum.getDouble("CHARGE_10", 0)
				+ RecordSum.getDouble("CHARGE_11", 0)
				+ RecordSum.getDouble("CHARGE_12", 0)
				+ RecordSum.getDouble("CHARGE_13", 0)
				+ RecordSum.getDouble("CHARGE_14", 0)
				+ RecordSum.getDouble("CHARGE_15", 0)
				+ RecordSum.getDouble("CHARGE_16", 0)
				+ RecordSum.getDouble("CHARGE_17", 0)
				+ RecordSum.getDouble("CHARGE_18", 0)
				+ RecordSum.getDouble("CHARGE_19", 0)
				+ RecordSum.getDouble("CHARGE_20", 0)
				+ RecordSum.getDouble("CHARGE_21", 0)
				+ RecordSum.getDouble("CHARGE_22", 0)
				+ RecordSum.getDouble("CHARGE_23", 0)
				+ RecordSum.getDouble("CHARGE_24", 0)
				+ RecordSum.getDouble("CHARGE_25", 0)
				+ RecordSum.getDouble("CHARGE_26", 0)
				+ RecordSum.getDouble("CHARGE_27", 0)
				+ RecordSum.getDouble("CHARGE_28", 0)
				+ RecordSum.getDouble("CHARGE_29", 0)
				+ RecordSum.getDouble("CHARGE_30", 0);
		result.addData("DATA_04", chargeSum);
		result.addData("DATA_05", RecordSum.getData("CHARGE_01", 0)); // ��λ��
		// ҩ��
		result.addData("DATA_06", RecordSum.getDouble("CHARGE_03", 0) + // ��ҩ
				RecordSum.getDouble("CHARGE_04", 0) + // �г�ҩ
				RecordSum.getDouble("CHARGE_05", 0)); // �в�ҩ
		result.addData("DATA_07", RecordSum.getData("CHARGE_11", 0));// ������
		// ������Ʒ� = �����+����+���Ʒ�
		result.addData("DATA_08", RecordSum.getDouble("CHARGE_07", 0) + // �����
				RecordSum.getDouble("CHARGE_10", 0) + // ���Ʒ�
				RecordSum.getDouble("CHARGE_13", 0)); // ����
		result.addData("CONFIRM_FLG", "");
		result.addData("CONFIRM_USER", "");
		result.addData("CONFIRM_DATE", "");
		result.addData("OPT_USER", "");
		result.addData("OPT_TERM", "");
		return result;
	}

	/**
	 * ɾ��STA_OUT_05����
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm
	 */
	public TParm deleteSTA_OUT_05(String STA_DATE, String regionCode,
			TConnection conn) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// ============pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110523 stop
		TParm result = update("deleteSTA_OUT_05", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertSTA_OUT_05(TParm parm, TConnection conn) {
		TParm result = update("insertSTA_OUT_05", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �������� ��ɾ��ԭ������ �ٲ���������
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertData(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sta_date = parm.getValue("STA_DATE", 0);
		// =======pangben modify 20110523 start
		String regionCode = parm.getValue("REGION_CODE", 0);
		// =======pangben modify 20110523 stop
		result = this.deleteSTA_OUT_05(sta_date, regionCode, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		TParm insert = null;
		for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
			insert = new TParm();
			insert.setData("STA_DATE", parm.getData("STA_DATE", i));
			insert.setData("SEQ", parm.getData("SEQ", i));
			insert.setData("DATA_01", parm.getData("DATA_01", i) == null ? ""
					: parm.getData("DATA_01", i));
			insert.setData("DATA_02", parm.getData("DATA_02", i) == null ? ""
					: parm.getData("DATA_02", i));
			insert.setData("DATA_03", parm.getData("DATA_03", i) == null ? ""
					: parm.getData("DATA_03", i));
			insert.setData("DATA_04", parm.getData("DATA_04", i) == null ? ""
					: parm.getData("DATA_04", i));
			insert.setData("DATA_05", parm.getData("DATA_05", i) == null ? ""
					: parm.getData("DATA_05", i));
			insert.setData("DATA_06", parm.getData("DATA_06", i) == null ? ""
					: parm.getData("DATA_06", i));
			insert.setData("DATA_07", parm.getData("DATA_07", i) == null ? ""
					: parm.getData("DATA_07", i));
			insert.setData("DATA_08", parm.getData("DATA_08", i) == null ? ""
					: parm.getData("DATA_08", i));
			insert.setData(
					"CONFIRM_FLG",
					parm.getData("CONFIRM_FLG", i) == null ? "" : parm.getData(
							"CONFIRM_FLG", i));
			insert.setData(
					"CONFIRM_USER",
					parm.getData("CONFIRM_USER", i) == null ? "" : parm
							.getData("CONFIRM_USER", i));
			insert.setData(
					"CONFIRM_DATE",
					parm.getData("CONFIRM_DATE", i) == null ? "" : parm
							.getData("CONFIRM_DATE", i));
			insert.setData("OPT_USER", parm.getData("OPT_USER", i) == null ? ""
					: parm.getData("OPT_USER", i));
			insert.setData("OPT_TERM", parm.getData("OPT_TERM", i) == null ? ""
					: parm.getData("OPT_TERM", i));
			// ========pangben modify 20110523 start
			insert.setData(
					"REGION_CODE",
					parm.getData("REGION_CODE", i) == null ? "" : parm.getData(
							"REGION_CODE", i));
			// ========pangben modify 20110523 stop
			result = this.insertSTA_OUT_05(insert, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ��ѯ��ӡ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPrint(String STA_DATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// ========pangben modify 20110523 start
		parm.setData("REGION_CODE", regionCode);
		// ========pangben modify 20110523 stop
		TParm result = this.query("selectPrint", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

}
