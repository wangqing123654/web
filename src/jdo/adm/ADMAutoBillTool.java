package jdo.adm;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import java.sql.Timestamp;
import jdo.sys.SYSBedTool;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.util.StringTool;
import jdo.ibs.IBSTool;
import jdo.sys.SYSBedFeeTool;
import jdo.sys.SystemTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SYSOrderSetDetailTool;
import jdo.sys.SYSFeeTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSSQL;
import jdo.ibs.IBSOrdermTool;

/**
 * <p>
 * Title:
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMAutoBillTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static ADMAutoBillTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static ADMAutoBillTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ADMAutoBillTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public ADMAutoBillTool() {
		setModuleName("adm\\ADMAutoBillModule.x");
		onInit();
	}

	/**
	 * ��ѯȫ����
	 * 
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm queryData(TParm parm) {
		TParm result = new TParm();
		result = query("querydata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}

		return result;

	}

	/**
	 * ��ѯȫ����
	 * 
	 * @return TParm
	 */
	public TParm queryData() {
		TParm result = new TParm();
		result = query("querydata");
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm insertdata(TParm parm) {
		TParm result = new TParm();
		String orderCode = parm.getValue("ORDER_CODE");
		if (existsORDER(orderCode)) {
			result.setErr(-1, "�շ���Ŀ " + " �Ѿ�����!");
			return result;
		}
		result = update("insertdata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ����
	 * 
	 * @param parm TParm
	 * @return TParm
	 */
	public TParm updatedata(TParm parm) {
		TParm result = update("update", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ������
	 * 
	 * @param orderCode String
	 * @return TParm
	 */
	public TParm deletedata(String orderCode) {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		TParm result = update("deletedata", parm);
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �ж��Ƿ�����Զ��Ʒ���Ŀ
	 * 
	 * @param orderCode String
	 * @return boolean
	 */
	public boolean existsORDER(String orderCode) {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		return getResultInt(query("existsORDER", parm), "COUNT") > 0;
	}

	/**
	 * �̶������Զ�����
	 * 
	 * @param parm TParm ��������� STATION_CODE������code�� DATE�����ڣ� OPT_USER��������Ա��
	 *             OPT_TERM������IP
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm postAutoBill(TParm parm, TConnection conn) {
		String StationCode = parm.getValue("STATION_CODE");
		Timestamp date = StringTool.rollDate(parm.getTimestamp("DATE"), -1);
		String OPT_USER = parm.getValue("OPT_USER");
		String OPT_TERM = parm.getValue("OPT_TERM");
		String dateString = StringTool.getString(date, "yyyyMMdd");
		double PATCH_AMT = 0;// ɾ��IBS_ORDD����ܽ��
		TParm result = new TParm();
		// ��ѯ�ò��������в���
		TParm bed = new TParm();
		bed.setData("STATION_CODE", StationCode);
		TParm bedList = SYSBedTool.getInstance().queryAll(bed);
//        System.out.println("��ѯ�ò��������в���"+bedList);
		if (bedList.getErrCode() < 0) {
			err("ERR:" + bedList.getErrCode() + bedList.getErrText() + bedList.getErrName());
			return bedList;
		}
		// ȡ adm_autobil �е�order_code����
		TParm autoBill = this.queryData();// ��ѯȫ���Զ��Ʒѵ���Ŀ
		// ѭ��ÿ�Ų������м�� ע��Ҫ�����������������ü�����һ���˵���
		for (int i = 0; i < bedList.getCount(); i++) {
			// �������ס�в���
			String CASE_NO = "";
			// ���������� ����ǰ��� ��ô���Ŵ�����ʵ��ס�в��˵Ĵ�λ�����Ǹô�λ�Ĺ̶�����Ҫ�����ڰ����Ĳ����˵���
			if (!bedList.getValue("BED_OCCU_FLG", i).equals("Y"))
				CASE_NO = bedList.getValue("CASE_NO", i);
			// ��ѯ�ò����Ƿ����
			TParm BED_OCCU_CODE = new TParm();// ��¼�ò�����ռ�õĴ�λ����Ϣ
			for (int j = 0; j < bedList.getCount(); j++) {
				// CASE_NO��ͬ���Ұ������ΪY ��ʾ�ò�������
				if (CASE_NO.equals(bedList.getValue("CASE_NO", j)) && "Y".equals(bedList.getValue("BED_OCCU_FLG", j))) {
//                    System.out.println("�ǰ�����Ϣ========"+bedList);
					BED_OCCU_CODE.addRowData(bedList, j);// ���벡����������Ϣ
				}
			}
			// ���벡��ʵ�����ڴ�λ����Ϣ
			BED_OCCU_CODE.addRowData(bedList, i);
			if (CASE_NO.length() > 0) {
				String dateS = StringTool.getString(parm.getTimestamp("DATE"), "yyyyMMdd");
				String EXEsql = " SELECT B.PAT_NAME,A.STATION_CODE, A.BED_NO,A.IPD_NO,A.MR_NO,A.CASE_NO "
						+ "   FROM ADM_INP A,SYS_PATINFO B" + "  WHERE DS_DATE IS NULL"
						+ "    AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)" + "    AND A.IN_DATE < TO_DATE('" + dateS
						+ "','YYYYMMDD') " + "    AND A.BED_NO IS NOT NULL " + "    AND A.MR_NO = B.MR_NO"
						+ "    AND A.CASE_NO = '" + CASE_NO + "' " + "  ORDER BY A.MR_NO ";
//                System.out.println("У��sql"+EXEsql);
				TParm caseParm = new TParm(TJDODBTool.getInstance().select(EXEsql));
				if (caseParm.getErrCode() < 0) {
					err("ERR:" + caseParm.getErrCode() + caseParm.getErrText() + caseParm.getErrName());
					return caseParm;
				}
				if (caseParm.getCount() <= 0)
					continue;
				// ��˴��յ��Ƿ��Ѿ��м�¼
				TParm dailyRecParm = new TParm();
				dailyRecParm.setData("CASE_NO", CASE_NO);
				dailyRecParm.setData("POST_DATE", dateString);// �յ�����
				TParm daulyRec = ADMDailyRecTool.getInstance().selectDailyRec(dailyRecParm);
				// ������յ�������Ϣ��ɾ�����յ���Ϣ���Ѿ����ʵĲ�����Ϣ(ibs_ordd,ibs_ordm)
				if (daulyRec.getCount() > 0) {
					TParm delRec = new TParm();
					delRec.setData("CASE_NO", CASE_NO);
					delRec.setData("POST_DATE", dateString);// �յ�����
					result = ADMDailyRecTool.getInstance().delDailRec(delRec, conn);// ɾ�������Ĵ��յ�
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						return result;
					}
					// ɾ��IBS�ķ��ü�¼ ����IBSTool�ṩ�Ľӿ�
//                    System.out.println("date:"+StringTool.getString(date, "yyyyMMdd"));
					result = IBSTool.getInstance().deleteOrderForPatch(CASE_NO, StringTool.getString(date, "yyyyMMdd"),
							conn);
					PATCH_AMT = result.getDouble("PATCH_AMT");
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
						return result;
					}
				}
				// ��ѯADM_INP����סԺ��Ϣ
				TParm inp = new TParm();
				inp.setData("CASE_NO", CASE_NO);
				TParm admInp = ADMInpTool.getInstance().selectall(inp);
				if (admInp.getErrCode() < 0) {
					err("ERR:" + admInp.getErrCode() + admInp.getErrText() + admInp.getErrName());
					return admInp;
				}
				if (admInp.getCount() <= 0) {
					admInp.setErr(-1, "���޸ò���סԺ��Ϣ");
					return admInp;
				}
				// ===zhangp 20120828 start ����ibs_orddʱִ�п���Ϊ�ɱ����Ĵ���
				String cccsql = " SELECT COST_CENTER_CODE" + " FROM SYS_DEPT" + " WHERE DEPT_CODE = '"
						+ admInp.getValue("DEPT_CODE", 0) + "'";
				TParm cccParm = new TParm(TJDODBTool.getInstance().select(cccsql));
				if (cccParm.getErrCode() < 0) {
					err("ERR:" + cccParm.getErrCode() + cccParm.getErrText() + cccParm.getErrName());
					String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
							+ admInp.getValue("STATION_CODE", 0) + "'";
					TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
					if (stParm.getErrCode() < 0) {
						err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
						stParm.addData("COST_CENTER_CODE", "");
					}
					if (stParm.getCount() < 0) {
						stParm.addData("COST_CENTER_CODE", "");
					}
					cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
				}
				if (cccParm.getCount() <= 0) {
					String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
							+ admInp.getValue("STATION_CODE", 0) + "'";
					TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
					if (stParm.getErrCode() < 0) {
						err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
						stParm.addData("COST_CENTER_CODE", "");
					}
					if (stParm.getCount() < 0) {
						stParm.addData("COST_CENTER_CODE", "");
					}
					cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
				}
				// ====zhangp 20130819 start
				String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
						+ admInp.getValue("STATION_CODE", 0) + "'";
				TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
				if (stParm.getErrCode() < 0) {
					err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
					stParm.addData("COST_CENTER_CODE", "");
				}
				if (stParm.getCount() < 0) {
					stParm.addData("COST_CENTER_CODE", "");
				}
				// ====zhangp 20130819 end
				// ===zhangp 20120828 end
				// ���봲�յ�
				TParm insertRec = new TParm();
				insertRec.setData("POST_DATE", dateString);
				insertRec.setData("CASE_NO", CASE_NO);
				insertRec.setData("MR_NO", admInp.getValue("MR_NO", 0));
				insertRec.setData("IPD_NO", admInp.getValue("IPD_NO", 0));
				insertRec.setData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
				insertRec.setData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
				insertRec.setData("BED_NO", admInp.getValue("BED_NO", 0));
				insertRec.setData("VS_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
				insertRec.setData("NURSING_CLASS", admInp.getValue("NURSING_CLASS", 0));// ����ȼ�
				insertRec.setData("PATIENT_CONDITITION", admInp.getValue("PATIENT_CONDITION", 0));// ����״̬
				insertRec.setData("CARE_NUM", admInp.getInt("CARE_NUM", 0));// �㻤����
				insertRec.setData("OPT_USER", OPT_USER);
				insertRec.setData("OPT_TERM", OPT_TERM);
				result = ADMDailyRecTool.getInstance().insertDailyRec(insertRec, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
				}
				// ����IBS����
				TParm orderList = new TParm();
				for (int h = 0; h < autoBill.getCount(); h++) {
					// �������д�λ�ķ��ü���
					for (int k = 0; k < BED_OCCU_CODE.getCount("BED_NO"); k++) {
						// �жϴ˴�λ�Ƿ��ǲ��˵İ���
						if (BED_OCCU_CODE.getBoolean("BED_OCCU_FLG", k)) {// �ǰ���
							// �жϴ�������Ƿ�԰�������
							if (!autoBill.getBoolean("OCCUFEE_FLG", h)) {
								// ������԰������м��� ��ô��������ѭ��
								continue;
							}
						}
						// �жϸò����Ƿ���Ӥ����
						if (BED_OCCU_CODE.getBoolean("BABY_BED_FLG", k)) {// ��Ӥ����
							// �жϴ�������Ƿ��Ӥ��������
							if (!autoBill.getBoolean("BABY_FLG", h)) {
								// �������Ӥ�������м��� ��ô��������ѭ��
								continue;
							}
						}
						Timestamp now = SystemTool.getInstance().getDate();
						String year = StringTool.getString(now, "yyyy");
						int nextYear = Integer.valueOf(year) + 1;
						String start = autoBill.getValue("START_DATE", h);
						String end = autoBill.getValue("END_DATE", h);
						long tiemNow = TypeTool.getLong(StringTool.getString(now, "yyyyMMddHHmmss"));// ��ǰʱ��
						// �����ʼ�·� ���� ��ֹ�·� ��ô ��ʼʱ��ͽ�ֹʱ����ͬһ��
						if (start.compareTo(end) < 0) {
							start = year + start + "000000";
							end = year + end + "235959";
						} else {
							start = year + start + "000000";
							end = nextYear + end + "235959";
						}
						// �жϵ�ǰʱ���Ƿ��� �̶����õļ��㷶Χ��
						if (tiemNow < TypeTool.getLong(start) || tiemNow > TypeTool.getLong(end)) {
							continue;
						}
						TParm sysFee = this.getSysFee(autoBill.getValue("ORDER_CODE", h));
						orderList.addData("CASE_NO", CASE_NO);
						// ���� �������������������ڣ����԰�������
						orderList.addData("DOSAGE_QTY", autoBill.getInt("DOSEAGE_QTY", h));
						orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
						orderList.addData("ORDER_CODE", autoBill.getValue("ORDER_CODE", h));
						orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
						orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
						orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
						// =====zhangp 20130819 start
						if (autoBill.getBoolean("DEPT_FLG", h)) {
							orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
						} else {
							orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
						}
						// =====zhangp 20130819 end
						orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
						orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
						orderList.addData("DISPENSE_EFF_DATE", now);
						orderList.addData("DISPENSE_END_DATE", now);
						orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); // ��ҩ����
						orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
						orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ1
						orderList.addData("OPT_USER", OPT_USER);
						orderList.addData("OPT_TERM", OPT_TERM);
						orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
						orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
						orderList.addData("HIDE_FLG", "N");// ����ҽ��ϸ�����ر��
						orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
						orderList.addData("BILL_FLG", "Y");
						orderList.addData("REQUEST_FLG", "N");// ����ע��
						TParm parmDetail = SYSOrderSetDetailTool.getInstance()
								.selectByOrderSetCode(autoBill.getValue("ORDER_CODE", h));
						if (parmDetail.getCount() > 0) {
							orderList.addData("ORDERSET_GROUP_NO", h);// ����ҽ�����
							orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));// ����ҽ������CODE
						} else {
							orderList.addData("ORDERSET_GROUP_NO", "");// ����ҽ�����
							orderList.addData("ORDERSET_CODE", "");// ����ҽ������CODE
						}
						for (int x = 0; x < parmDetail.getCount(); x++) {
							TParm sysFeeD = this.getSysFee(parmDetail.getValue("ORDER_CODE", x));
							orderList.addData("CASE_NO", CASE_NO);
							// ���� �������������������ڣ����԰�������
							orderList.addData("DOSAGE_QTY", autoBill.getInt("DOSEAGE_QTY", h));
							orderList.addData("DOSAGE_UNIT", sysFeeD.getValue("UNIT_CODE"));
							orderList.addData("ORDER_CODE", parmDetail.getValue("ORDER_CODE", x));
							orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
							orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
							orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
							orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
							orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
							if (autoBill.getBoolean("DEPT_FLG", h)) {
								orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
							} else {
								orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
							}
							orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
							orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
							orderList.addData("DISPENSE_EFF_DATE", now);
							orderList.addData("DISPENSE_END_DATE", now);
							orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); // ��ҩ����
							orderList.addData("MEDI_UNIT", sysFeeD.getValue("UNIT_CODE"));
							orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ0
							orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));
							orderList.addData("OPT_USER", OPT_USER);
							orderList.addData("OPT_TERM", OPT_TERM);
							orderList.addData("ORDER_CAT1_CODE", sysFeeD.getValue("ORDER_CAT1_CODE"));
							orderList.addData("CAT1_TYPE", sysFeeD.getValue("CAT1_TYPE"));
							orderList.addData("ORDERSET_GROUP_NO", h);// ����ҽ�����
							orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));// ����ҽ������CODE
							orderList.addData("HIDE_FLG", "Y");// ����ҽ��ϸ�����ر��
							orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
							orderList.addData("BILL_FLG", "Y");
							orderList.addData("REQUEST_FLG", "N");// ����ע��
						}
					}
				}
				// ���ݴ�λ�ȼ���ѯ sys_bedfee ��λ�����ֵ� ����ô�λ�ķ���
				for (int k = 0; k < BED_OCCU_CODE.getCount("BED_NO"); k++) {
					TParm bedBill = SYSBedFeeTool.getInstance().getSYSBedFeeOccu(
							BED_OCCU_CODE.getValue("BED_CLASS_CODE", k), BED_OCCU_CODE.getValue("BED_OCCU_FLG", k));
//                    System.out.println("��λ�ȼ���"+BED_OCCU_CODE.getValue("BED_CLASS_CODE", k));
//                    System.out.println("��λ���ã�"+bedBill);
					Timestamp now = SystemTool.getInstance().getDate();
					for (int h = 0; h < bedBill.getCount(); h++) {
						TParm sysFee = this.getSysFee(bedBill.getValue("ORDER_CODE", h));
						orderList.addData("CASE_NO", CASE_NO);
						orderList.addData("DOSAGE_QTY", "1"); // ��λ����Ĭ������Ϊ1
						orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
						orderList.addData("ORDER_CODE", bedBill.getValue("ORDER_CODE", h));
						orderList.addData("IPD_NO", BED_OCCU_CODE.getValue("IPD_NO", k));
						orderList.addData("MR_NO", BED_OCCU_CODE.getValue("MR_NO", k));
						orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
						orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
						orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
						orderList.addData("BED_NO", BED_OCCU_CODE.getValue("BED_NO", k));
						String start = autoBill.getValue("START_DATE", h);
						String end = autoBill.getValue("END_DATE", h);
						String year = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy");
						// �����ʼ�·� ���� ��ֹ�·� ��ô ��ʼʱ��ͽ�ֹʱ����ͬһ��
						if (start.compareTo(end) < 0) {
							start = year + start + "000000";
							end = year + end + "235959";
						} else {
							start = year + start + "000000";
							int nextYear = Integer.valueOf(year) + 1;
							end = nextYear + end + "235959";
						}
						orderList.addData("DISPENSE_EFF_DATE", now);
						orderList.addData("DISPENSE_END_DATE", now);
						orderList.addData("MEDI_QTY", 1); // ��ҩ����Ĭ��Ϊ1
						orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
						orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ1
						orderList.addData("OPT_USER", OPT_USER);
						orderList.addData("OPT_TERM", OPT_TERM);
						orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
						orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
						orderList.addData("ORDERSET_GROUP_NO", "");// ����ҽ�����
						orderList.addData("ORDERSET_CODE", "");// ����ҽ������CODE
						orderList.addData("HIDE_FLG", "N");// ����ҽ��ϸ�����ر��
						orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
						orderList.addData("BILL_FLG", "Y");
						orderList.addData("REQUEST_FLG", "N");// ����ע��
					}
				}
				TParm ibsParm = new TParm();
				ibsParm.setData("M", orderList.getData());
				ibsParm.setData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
				ibsParm.setData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
				ibsParm.setData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
				ibsParm.setData("FLG", "ADD");
				// ȡ�����ڲ����IBS����
				TParm IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);
				TParm ibsIN = new TParm();
				ibsIN.setData("M", IBSInsert.getData());
				ibsIN.setData("DATA_TYPE", "0");
				ibsIN.setData("FLG", "ADD");
				ibsIN.setData("BILL_DATE", StringTool.getTimestamp(dateString + "235959", "yyyyMMddHHmmss"));
				ibsIN.setData("PATCH_AMT", PATCH_AMT);
//                System.out.println("�Զ��������===="+ibsIN);
				result = IBSTool.getInstance().insertIBSOrder(ibsIN, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * ���̶������Զ����ˣ��Ը���Ϊ��λ���ʣ�
	 * 
	 * @param parm TParm ���������CASE_NO:����ţ� DATE:���ڣ� OPT_USER��������Ա�� OPT_TERM������IP
	 * @param conn TConnection
	 * @return TParm
	 */
	public TParm postAutoBillOfMen(TParm parm, TConnection conn) {
//        System.out.println("�Զ��Ʒ���Σ�"+parm);
		String CASE_NO = parm.getValue("CASE_NO");
		Timestamp date = StringTool.rollDate(parm.getTimestamp("DATE"), -1);
		String OPT_USER = parm.getValue("OPT_USER");
		String OPT_TERM = parm.getValue("OPT_TERM");
		String dateString = StringTool.getString(date, "yyyyMMdd");
		double PATCH_AMT = 0;// ɾ��IBS_ORDD����ܽ��
		TParm result = new TParm();
		// ��ѯ�ò��������в���
		TParm bed = new TParm();
		bed.setData("CASE_NO", CASE_NO);
		TParm bedList = SYSBedTool.getInstance().queryAll(bed);
		if (bedList.getErrCode() < 0) {
			err("ERR:" + bedList.getErrCode() + bedList.getErrText() + bedList.getErrName());
			return bedList;
		}
		if (bedList.getCount("CASE_NO") <= 0)
			return bedList;
//        System.out.println("��ѯ�ò��������в�����"+bedList);
//        System.out.println("CASE_NO��"+CASE_NO);
		if (CASE_NO.length() > 0) {
			// ��˴��յ��Ƿ��Ѿ��м�¼
			TParm dailyRecParm = new TParm();
			dailyRecParm.setData("CASE_NO", CASE_NO);
			dailyRecParm.setData("POST_DATE", dateString); // �յ�����
			TParm daulyRec = ADMDailyRecTool.getInstance().selectDailyRec(dailyRecParm);
			// ������յ�������Ϣ��ɾ�����յ���Ϣ���Ѿ����ʵĲ�����Ϣ(ibs_ordd,ibs_ordm)
			if (daulyRec.getCount() > 0) {
				TParm delRec = new TParm();
				delRec.setData("CASE_NO", CASE_NO);
				delRec.setData("POST_DATE", dateString); // �յ�����
				result = ADMDailyRecTool.getInstance().delDailRec(delRec, conn); // ɾ�������Ĵ��յ�
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
				}
				// ɾ��IBS�ķ��ü�¼ ����IBSTool�ṩ�Ľ��
//                System.out.println("ɾ�������ʱ�䣺"+StringTool.getString(date, "yyyyMMdd"));
				result = IBSTool.getInstance().deleteOrderForPatch(CASE_NO, StringTool.getString(date, "yyyyMMdd"),
						conn);
				PATCH_AMT = result.getDouble("PATCH_AMT");
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
					return result;
				}
			}
			// ��ѯADM_INP����סԺ��Ϣ
			TParm inp = new TParm();
			inp.setData("CASE_NO", CASE_NO);
			TParm admInp = ADMInpTool.getInstance().selectall(inp);
			if (admInp.getErrCode() < 0) {
				err("ERR:" + admInp.getErrCode() + admInp.getErrText() + admInp.getErrName());
				return admInp;
			}
			if (admInp.getCount() <= 0) {
				admInp.setErr(-1, "���޸ò���סԺ��Ϣ");
				return admInp;
			}
			// ===zhangp 20120828 start ����ibs_orddʱִ�п���Ϊ�ɱ����Ĵ���
			String cccsql = " SELECT COST_CENTER_CODE" + " FROM SYS_DEPT" + " WHERE DEPT_CODE = '"
					+ admInp.getValue("DEPT_CODE", 0) + "'";
			TParm cccParm = new TParm(TJDODBTool.getInstance().select(cccsql));
			if (cccParm.getErrCode() < 0) {
				err("ERR:" + cccParm.getErrCode() + cccParm.getErrText() + cccParm.getErrName());
				String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
						+ admInp.getValue("STATION_CODE", 0) + "'";
				TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
				if (stParm.getErrCode() < 0) {
					err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
					stParm.addData("COST_CENTER_CODE", "");
				}
				if (stParm.getCount() < 0) {
					stParm.addData("COST_CENTER_CODE", "");
				}
				cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
			}
			if (cccParm.getCount() <= 0) {
				String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
						+ admInp.getValue("STATION_CODE", 0) + "'";
				TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
				if (stParm.getErrCode() < 0) {
					err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
					stParm.addData("COST_CENTER_CODE", "");
				}
				if (stParm.getCount() < 0) {
					stParm.addData("COST_CENTER_CODE", "");
				}
				cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
			}
			// ====zhangp 20130819 start
			String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
					+ admInp.getValue("STATION_CODE", 0) + "'";
			TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
			if (stParm.getErrCode() < 0) {
				err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
				stParm.addData("COST_CENTER_CODE", "");
			}
			if (stParm.getCount() < 0) {
				stParm.addData("COST_CENTER_CODE", "");
			}
			// ====zhangp 20130819 end
			// ===zhangp 20120828 end
			// ���봲�յ�
			TParm insertRec = new TParm();
			insertRec.setData("POST_DATE", dateString);
			insertRec.setData("CASE_NO", CASE_NO);
			insertRec.setData("MR_NO", admInp.getValue("MR_NO", 0));
			insertRec.setData("IPD_NO", admInp.getValue("IPD_NO", 0));
			insertRec.setData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
			insertRec.setData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
			insertRec.setData("BED_NO", admInp.getValue("BED_NO", 0));
			insertRec.setData("VS_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
			insertRec.setData("NURSING_CLASS", admInp.getValue("NURSING_CLASS", 0)); // ����ȼ�
			insertRec.setData("PATIENT_CONDITITION", admInp.getValue("PATIENT_CONDITION", 0)); // ����״̬
			insertRec.setData("CARE_NUM", admInp.getInt("CARE_NUM", 0));// �㻤����
			insertRec.setData("OPT_USER", OPT_USER);
			insertRec.setData("OPT_TERM", OPT_TERM);
			result = ADMDailyRecTool.getInstance().insertDailyRec(insertRec, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result;
			}
			// ����IBS����
			TParm orderList = new TParm();
			// ȡ adm_autobil �е�order_code����
			TParm autoBill = this.queryData(); // ��ѯȫ���Զ��Ʒѵ���Ŀ
			for (int h = 0; h < autoBill.getCount(); h++) {
				// �������д�λ�ķ��ü���
				for (int k = 0; k < bedList.getCount("BED_NO"); k++) {
					// �жϴ˴�λ�Ƿ��ǲ��˵İ���
					if (bedList.getBoolean("BED_OCCU_FLG", k)) {// �ǰ���
						// �жϴ�������Ƿ�԰�������
						if (!autoBill.getBoolean("OCCUFEE_FLG", h)) {
							// ������԰������м��� ��ô��������ѭ��
							continue;
						}
					}
					// �жϸò����Ƿ���Ӥ����
					if (bedList.getBoolean("BABY_BED_FLG", k)) {// ��Ӥ����
						// �жϴ�������Ƿ��Ӥ��������
						if (!autoBill.getBoolean("BABY_FLG", h)) {
							// �������Ӥ�������м��� ��ô��������ѭ��
							continue;
						}
					}
					Timestamp now = SystemTool.getInstance().getDate();
					String year = StringTool.getString(now, "yyyy");
					int nextYear = Integer.valueOf(year) + 1;
					String start = autoBill.getValue("START_DATE", h);
					String end = autoBill.getValue("END_DATE", h);
					long tiemNow = TypeTool.getLong(StringTool.getString(now, "yyyyMMddHHmmss"));// ��ǰʱ��
					// �����ʼ�·� ���� ��ֹ�·� ��ô ��ʼʱ��ͽ�ֹʱ����ͬһ��
					if (start.compareTo(end) < 0) {
						start = year + start + "000000";
						end = year + end + "235959";
					} else {
						start = year + start + "000000";
						end = nextYear + end + "235959";
					}
					// �жϵ�ǰʱ���Ƿ��� �̶����õļ��㷶Χ��
					if (tiemNow < TypeTool.getLong(start) || tiemNow > TypeTool.getLong(end)) {
						continue;
					}
					TParm sysFee = this.getSysFee(autoBill.getValue("ORDER_CODE", h));
					orderList.addData("CASE_NO", CASE_NO);
					// ����
					orderList.addData("DOSAGE_QTY", autoBill.getInt("DOSEAGE_QTY", h));
					orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
					orderList.addData("ORDER_CODE", autoBill.getValue("ORDER_CODE", h));
					orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
					orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
					orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
					orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
					orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
					// ===zhangp 20120828 start ����ibs_orddʱִ�п���Ϊ�ɱ����Ĵ���
//                    orderList.addData("EXEC_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
					// =====zhangp 20130819 start
					if (autoBill.getBoolean("DEPT_FLG", h)) {
						orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
					} else {
						orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
					}
					// =====zhangp 20130819 end
					// ===zhangp 20120828 end
					orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
					orderList.addData("BED_NO", bedList.getValue("BED_NO", k));
					orderList.addData("DISPENSE_EFF_DATE", now);
					orderList.addData("DISPENSE_END_DATE", now);
					orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); // ��ҩ����Ĭ��Ϊ0
					orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
					orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ0
					orderList.addData("OPT_USER", OPT_USER);
					orderList.addData("OPT_TERM", OPT_TERM);
					orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
					orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
					orderList.addData("HIDE_FLG", "N");// ����ҽ��ϸ�����ر��
					orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
					orderList.addData("BILL_FLG", "Y");
					orderList.addData("REQUEST_FLG", "N");// ����ע��
					TParm parmDetail = SYSOrderSetDetailTool.getInstance()
							.selectByOrderSetCode(autoBill.getValue("ORDER_CODE", h));
					if (parmDetail.getCount() > 0) {
						orderList.addData("ORDERSET_GROUP_NO", h);// ����ҽ�����
						orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));// ����ҽ������CODE
					} else {
						orderList.addData("ORDERSET_GROUP_NO", "");// ����ҽ�����
						orderList.addData("ORDERSET_CODE", "");
					}
					for (int x = 0; x < parmDetail.getCount(); x++) {
						TParm sysFeeD = this.getSysFee(parmDetail.getValue("ORDER_CODE", x));
						orderList.addData("CASE_NO", CASE_NO);
						// ���� �������������������ڣ����԰�������
						orderList.addData("DOSAGE_QTY", autoBill.getInt("DOSEAGE_QTY", h));
						orderList.addData("DOSAGE_UNIT", sysFeeD.getValue("UNIT_CODE"));
						orderList.addData("ORDER_CODE", parmDetail.getValue("ORDER_CODE", x));
						orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
						orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
						orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
						orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
						if (autoBill.getBoolean("DEPT_FLG", h)) {
							orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
						} else {
							orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
						}
						orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
						orderList.addData("BED_NO", bedList.getValue("BED_NO", k));

						orderList.addData("DISPENSE_EFF_DATE", now);
						orderList.addData("DISPENSE_END_DATE", now);
						orderList.addData("MEDI_QTY", autoBill.getInt("DOSEAGE_QTY", h)); // ��ҩ����
						orderList.addData("MEDI_UNIT", sysFeeD.getValue("UNIT_CODE"));
						orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ1
						orderList.addData("OPT_USER", OPT_USER);
						orderList.addData("OPT_TERM", OPT_TERM);
						orderList.addData("ORDER_CAT1_CODE", sysFeeD.getValue("ORDER_CAT1_CODE"));
						orderList.addData("CAT1_TYPE", sysFeeD.getValue("CAT1_TYPE"));
						orderList.addData("ORDERSET_GROUP_NO", h);// ����ҽ�����
						orderList.addData("ORDERSET_CODE", autoBill.getValue("ORDER_CODE", h));// ����ҽ������CODE
						orderList.addData("HIDE_FLG", "Y");// ����ҽ��ϸ�����ر��
						orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
						orderList.addData("BILL_FLG", "Y");
						orderList.addData("REQUEST_FLG", "N");// ����ע��
					}
				}
			}
			// ���ݴ�λ�ȼ���ѯ sys_bedfee ��λ�����ֵ� ����ô�λ�ķ���
//            System.out.println("bedList~~~~~~~~~~~~~:"+bedList);
//            System.out.println("������Ϣ��"+admInp);
			for (int k = 0; k < bedList.getCount("BED_NO"); k++) {
//                System.out.println("BED_CLASS_CODE~~~~~~~~~:"+bedList.getValue("BED_CLASS_CODE", k));
				TParm bedBill = SYSBedFeeTool.getInstance().getSYSBedFeeOccu(bedList.getValue("BED_CLASS_CODE", k),
						bedList.getValue("BED_OCCU_FLG", k));
				Timestamp now = SystemTool.getInstance().getDate();
//                System.out.println("bedBill==============:"+bedBill);
				for (int h = 0; h < bedBill.getCount(); h++) {
					TParm sysFee = this.getSysFee(bedBill.getValue("ORDER_CODE", h));
					orderList.addData("CASE_NO", CASE_NO);
					orderList.addData("DOSAGE_QTY", "1"); // ��λ����Ĭ������Ϊ1
					orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
					orderList.addData("ORDER_CODE", bedBill.getValue("ORDER_CODE", h));
					orderList.addData("IPD_NO", bedList.getValue("IPD_NO", k));
					orderList.addData("MR_NO", bedList.getValue("MR_NO", k));
					orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
					orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
					orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
					orderList.addData("EXEC_DEPT_CODE", stParm.getValue("COST_CENTER_CODE", 0));
					orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
					orderList.addData("BED_NO", bedList.getValue("BED_NO", k));
					String start = bedBill.getValue("START_DATE", h);
					String end = bedBill.getValue("END_DATE", h);
					if (start.length() > 0)
						start = start.replace("/", "") + "000000";
					if (end.length() > 0)
						end = end.replace("/", "") + "235959";
					orderList.addData("DISPENSE_EFF_DATE", now);
					orderList.addData("DISPENSE_END_DATE", now);
					orderList.addData("MEDI_QTY", 1); // ��ҩ����Ĭ��Ϊ1
					orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
					orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ1
					orderList.addData("OPT_USER", OPT_USER);
					orderList.addData("OPT_TERM", OPT_TERM);
					orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
					orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
					orderList.addData("ORDERSET_GROUP_NO", "");// ����ҽ�����
					orderList.addData("ORDERSET_CODE", "");// ����ҽ������CODE
					orderList.addData("HIDE_FLG", "N");// ����ҽ��ϸ�����ر��
					orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
					orderList.addData("BILL_FLG", "Y");
					orderList.addData("REQUEST_FLG", "N");// ����ע��
				}
			}
			TParm ibsParm = new TParm();
			ibsParm.setData("M", orderList.getData());
			ibsParm.setData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
			ibsParm.setData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
			ibsParm.setData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
			ibsParm.setData("FLG", "ADD");
//            System.out.println("ibsParm:"+ibsParm);
			// ȡ�����ڲ����IBS����
			TParm IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);
			TParm ibsIN = new TParm();
			ibsIN.setData("M", IBSInsert.getData());
			ibsIN.setData("DATA_TYPE", "0");
			ibsIN.setData("FLG", "ADD");
			// fux modify 20140219 start
			Timestamp dateNew = StringTool.rollDate(parm.getTimestamp("DATE"), 0);
			String dateStringNew = StringTool.getString(dateNew, "yyyyMMdd");
			String today = parm.getValue("TODAY");
			if ("Y".equals(today)) {

				// modify by yangjj 20160408 #3067
				ibsIN.setData("BILL_DATE", dateNew);
			} else {
				ibsIN.setData("BILL_DATE", StringTool.getTimestamp(dateString + "235959", "yyyyMMddHHmmss"));
			}
			// fux modify 20140219 end
			// ibsIN.setData("BILL_DATE",StringTool.getTimestamp(dateString+"235959","yyyyMMddHHmmss"));
			ibsIN.setData("PATCH_AMT", PATCH_AMT);
//            System.out.println("�Զ�������Ρ�������"+ibsIN);
			result = IBSTool.getInstance().insertIBSOrder(ibsIN, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ��ѯsys_fee��Ϣ
	 * 
	 * @param orderCode String
	 * @return TParm
	 */
	public TParm getSysFee(String orderCode) {
		TParm result = new TParm(TJDODBTool.getInstance().select(SYSSQL.getSYSFee(orderCode)));
		return result.getRow(0);
	}

	/**
	 * סԺҩ�·����
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm phaServiceFee(TParm parm, TConnection conn) {
//      System.out.println("�Զ��Ʒ���Σ�"+parm);
		String CASE_NO = parm.getValue("CASE_NO");
		String OPT_USER = parm.getValue("OPT_USER");
		String OPT_TERM = parm.getValue("OPT_TERM");
		Timestamp now = SystemTool.getInstance().getDate();
		//
		TParm result = new TParm();
		// ��ѯADM_INP����סԺ��Ϣ
		TParm inp = new TParm();
		inp.setData("CASE_NO", CASE_NO);
		TParm admInp = ADMInpTool.getInstance().selectall(inp);
		if (admInp.getErrCode() < 0) {
			err("ERR:" + admInp.getErrCode() + admInp.getErrText() + admInp.getErrName());
			return admInp;
		}
		if (admInp.getCount() <= 0) {
			admInp.setErr(-1, "���޸ò���סԺ��Ϣ");
			return admInp;
		}
		// �����ظ���ȡҩ�·����
		TParm delP = this.delPhaServiceFee(conn, CASE_NO, admInp.getDouble("TOTAL_AMT", 0),
				admInp.getDouble("CUR_AMT", 0));
		if (delP.getErrCode() < 0) {
			return delP;
		}
		// �ײͻ��߲���ȡҩ�·����
		String lumpworkCode=admInp.getValue("LUMPWORK_CODE", 0);//�ײʹ���
		if (null != lumpworkCode && lumpworkCode.length() > 0) {
			System.out.println("===�ײͻ��߲���ȡҩ�·����===");
			return new TParm();
		}
		// 89 [Ա��]2019 92 ����[Ա��]2019 ����������������� סԺ������ҩ�·����
		if ("89".equals(admInp.getValue("CTZ1_CODE", 0)) || "92".equals(admInp.getValue("CTZ1_CODE", 0))) {
			return new TParm();
		}
		// ����ҩ�·��������
		String sql = "SELECT\r\n" + "	TO_CHAR( BILL_DATE, 'YYYY-MM-DD' ) DAY,\r\n" + "	SUM( TOT_AMT ) TOT_AMT \r\n"
				+ "FROM\r\n" + "	IBS_ORDD \r\n" + "WHERE\r\n" + "	CASE_NO = '@' \r\n"
				+ "	AND CAT1_TYPE = 'PHA' \r\n" + "	AND INCLUDE_FLG = 'Y' \r\n" + "GROUP BY\r\n"
				+ "	TO_CHAR( BILL_DATE, 'YYYY-MM-DD' ) \r\n" + "ORDER BY\r\n"
				+ "	TO_CHAR( BILL_DATE, 'YYYY-MM-DD' ) DESC";
		sql = sql.replace("@", CASE_NO);
		TParm ttt = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("====sql:::"+sql);
		int qty = ttt.getCount("TOT_AMT") < 0 ? 0 : ttt.getCount("TOT_AMT");
		if (qty == 0) {
			return new TParm();
		}
		//
		// ===zhangp 20120828 start ����ibs_orddʱִ�п���Ϊ�ɱ����Ĵ���
		String cccsql = " SELECT COST_CENTER_CODE" + " FROM SYS_DEPT" + " WHERE DEPT_CODE = '"
				+ admInp.getValue("DEPT_CODE", 0) + "'";
		TParm cccParm = new TParm(TJDODBTool.getInstance().select(cccsql));
		if (cccParm.getErrCode() < 0) {
			err("ERR:" + cccParm.getErrCode() + cccParm.getErrText() + cccParm.getErrName());
			String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
					+ admInp.getValue("STATION_CODE", 0) + "'";
			TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
			if (stParm.getErrCode() < 0) {
				err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
				stParm.addData("COST_CENTER_CODE", "");
			}
			if (stParm.getCount() < 0) {
				stParm.addData("COST_CENTER_CODE", "");
			}
			cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
		}
		if (cccParm.getCount() <= 0) {
			String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
					+ admInp.getValue("STATION_CODE", 0) + "'";
			TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
			if (stParm.getErrCode() < 0) {
				err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
				stParm.addData("COST_CENTER_CODE", "");
			}
			if (stParm.getCount() < 0) {
				stParm.addData("COST_CENTER_CODE", "");
			}
			cccParm.addData("COST_CENTER_CODE", stParm.getValue("COST_CENTER_CODE", 0));// ��dept_code��
		}
		// ====zhangp 20130819 start
		String stsql = " SELECT COST_CENTER_CODE" + " FROM SYS_STATION" + " WHERE STATION_CODE = '"
				+ admInp.getValue("STATION_CODE", 0) + "'";
		TParm stParm = new TParm(TJDODBTool.getInstance().select(stsql));
		if (stParm.getErrCode() < 0) {
			err("ERR:" + stParm.getErrCode() + stParm.getErrText() + stParm.getErrName());
			stParm.addData("COST_CENTER_CODE", "");
		}
		if (stParm.getCount() < 0) {
			stParm.addData("COST_CENTER_CODE", "");
		}

		// ����IBS����
		TParm orderList = new TParm();
		String orderCode = TConfig.getSystemValue("PHA_SERVICE_FEE");
		TParm sysFee = this.getSysFee(orderCode);
		orderList.addData("CASE_NO", CASE_NO);
		// ����
		orderList.addData("DOSAGE_QTY", qty);
		orderList.addData("DOSAGE_UNIT", sysFee.getValue("UNIT_CODE"));
		orderList.addData("ORDER_CODE", orderCode);
		orderList.addData("IPD_NO", admInp.getValue("IPD_NO", 0));
		orderList.addData("MR_NO", admInp.getValue("MR_NO", 0));
		orderList.addData("DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
		orderList.addData("ORDER_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
		orderList.addData("STATION_CODE", admInp.getValue("STATION_CODE", 0));
		// ===zhangp 20120828 start ����ibs_orddʱִ�п���Ϊ�ɱ����Ĵ���
//         orderList.addData("EXEC_DEPT_CODE", admInp.getValue("DEPT_CODE", 0));
		// =====zhangp 20130819 start
		orderList.addData("EXEC_DEPT_CODE", cccParm.getValue("COST_CENTER_CODE", 0));
		// =====zhangp 20130819 end
		// ===zhangp 20120828 end
		orderList.addData("EXEC_STATION_CODE", admInp.getValue("STATION_CODE", 0));
		orderList.addData("BED_NO", admInp.getValue("BED_NO", 0));
		orderList.addData("DISPENSE_EFF_DATE", now);
		orderList.addData("DISPENSE_END_DATE", now);
		orderList.addData("MEDI_QTY", qty); // ��ҩ����Ĭ��Ϊ0
		orderList.addData("MEDI_UNIT", sysFee.getValue("UNIT_CODE"));
		orderList.addData("TAKE_DAYS", 1); // ��ҩ����Ĭ��Ϊ0
		orderList.addData("OPT_USER", OPT_USER);
		orderList.addData("OPT_TERM", OPT_TERM);
		orderList.addData("ORDER_CAT1_CODE", sysFee.getValue("ORDER_CAT1_CODE"));
		orderList.addData("CAT1_TYPE", sysFee.getValue("CAT1_TYPE"));
		orderList.addData("HIDE_FLG", "N");// ����ҽ��ϸ�����ر��
		orderList.addData("ORDER_DR_CODE", admInp.getValue("VS_DR_CODE", 0));
		orderList.addData("BILL_FLG", "Y");
		orderList.addData("REQUEST_FLG", "N");// ����ע��
		orderList.addData("ORDERSET_GROUP_NO", "");// ����ҽ�����
		orderList.addData("ORDERSET_CODE", "");
		//
		TParm ibsParm = new TParm();
		ibsParm.setData("M", orderList.getData());
		ibsParm.setData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
		ibsParm.setData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
		ibsParm.setData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
		ibsParm.setData("FLG", "ADD");
//      System.out.println("ibsParm:"+ibsParm);
		// ȡ�����ڲ����IBS����
		TParm IBSInsert = IBSTool.getInstance().getIBSOrderData(ibsParm);
//		System.out.println("===IBSInsert:::"+IBSInsert);
		//
		TParm ibsIN = new TParm();
		ibsIN.setData("M", IBSInsert.getData());
		ibsIN.setData("DATA_TYPE", "0");
		ibsIN.setData("FLG", "ADD");
		ibsIN.setData("BILL_DATE", now);
		result = IBSTool.getInstance().insertIBSOrder(ibsIN, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * ɾ��ҩ�·����
	 * 
	 * @param conn
	 * @param caseNo
	 * @param totalAmt
	 * @param curAmt
	 * @return
	 */
	private TParm delPhaServiceFee(TConnection conn, String caseNo, double totalAmt, double curAmt) {
		String orderCode = TConfig.getSystemValue("PHA_SERVICE_FEE");
		String sql = "select CASE_NO, CASE_NO_SEQ, TOT_AMT from ibs_ordd where order_code = '@'";
		sql = sql.replace("@", orderCode);
		TParm t = new TParm(TJDODBTool.getInstance().select(sql));
		if (t.getCount("CASE_NO") <= 0) {
			return new TParm();
		} else {
			double amt = 0;
			String sql1 = null;
			TParm t1 = null;
			for (int i = 0; i < t.getCount("CASE_NO"); i++) {
				amt += t.getDouble("TOT_AMT", i);
				// del ibs_ordm
				sql1 = "DELETE FROM IBS_ORDM WHERE CASE_NO='@1' AND CASE_NO_SEQ = @2";
				sql1 = sql1.replace("@1", caseNo).replace("@2", t.getValue("CASE_NO_SEQ", i));
				t1 = new TParm(TJDODBTool.getInstance().update(sql1, conn));
				if (t1.getErrCode() < 0) {
					return t1;
				}
			}
			// del ibs_ordd
			sql1 = "delete from ibs_ordd where order_code = '@'";
			sql1 = sql1.replace("@", orderCode);
			t1 = new TParm(TJDODBTool.getInstance().update(sql1, conn));
			if (t1.getErrCode() < 0) {
				return t1;
			}
			// System.out.println("�˷�ҽ���ܽ��"+(totalAmt+totalAmtForADM));
			t1 = ADMTool.getInstance().updateTOTAL_AMT("" + (totalAmt - amt), caseNo, conn); // ����ADM��ҽ���ܽ��
			if (t1.getErrCode() < 0) {
				return t1;
			}
//            System.out.println("�˷�Ŀǰ���"+(curAmt + totalAmtForADM));
			t1 = ADMTool.getInstance().updateCUR_AMT("" + (curAmt + amt), caseNo, conn); // ����ADM��Ŀǰ���
			if (t1.getErrCode() < 0) {
				return t1;
			}
		}
		return new TParm();
	}
	
}
