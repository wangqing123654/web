package com.javahis.ui.udd;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: ����ҩ���ֵ�ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Description: ����ҩ���ֵ�ͳ�Ʊ���
 * </p>
 * 
 * <p>
 * Copyright: Bluecore
 * </p>
 * 
 * <p>
 * Company:Bluecore
 * </p>
 * 
 * @author yanjing
 * @version 1.0
 */
public class UDDEssentialDrugsControl extends TControl {
	private TTable dtable;// ���ܱ��
	private TTable mtable;// ��ϸ���
	private int pageFlg = 0;// ҳǩ��ǣ�0�����ܣ�1����ϸ��

	public void onInit() {
		// ����ؼ���ֵ
		dtable = this.getTable("TABLED");
		mtable = this.getTable("TABLEM");
		this.setValue("ADM_TYPE", '1');
		// ��ʼ����ѯ����ʱ
		Timestamp rollDay = SystemTool.getInstance().getDate();
		String date = StringTool.getString(rollDay, "yyyy/MM/dd 00:00:00");
		String end_day = StringTool.getString(rollDay, "yyyy/MM/dd 23:59:59");
		this.setValue("S_DATE", date);
		this.setValue("E_DATE", end_day);

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if ("".equals(this.getValue("S_DATE"))
				|| this.getValue("S_DATE") == null) {
			this.messageBox("��ʼʱ�䲻��Ϊ�գ�");
			return;
		} else if ("".equals(this.getValue("E_DATE"))
				|| this.getValue("E_DATE") == null) {
			this.messageBox("����ʱ�䲻��Ϊ�գ�");
			return;
		}
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")),
				"yyyy/MM/dd HH:mm:ss").replace("/", "").replace(" ", "")
				.replace(":", "").replace("-", "");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")),
				"yyyy/MM/dd HH:mm:ss").replace("/", "").replace(" ", "")
				.replace(":", "").replace("-", "");
		TParm result = new TParm();
		TParm resultAll = new TParm();
		String sqlAll = "";// ��ѯȫ��ҩƷ��sql
		String sql = "";// ��ѯ����ҩƷ��sql
		String dept_code = "";// ��ѯ�������Ƿ��п�������
		String sumResultAll="";
		String sumResult="";
		TParm resultSum=new TParm();
		TParm resultSumParm=new TParm();
		if (this.getValueString("DEPT_CODE") != null
				&& !this.getValueString("DEPT_CODE").equals("")) {
			dept_code = "  AND A.DEPT_CODE = '"
					+ this.getValueString("DEPT_CODE") + "'";
		}
		if (pageFlg == 0) {// ����
			//���ҩƷ�����ѯ����
			String pha_type = "";// ��ѯ�������Ƿ���ҩƷ��������
			if (this.getValueString("PHA_TYPE").equals("1")) {
				pha_type = "  AND A.ORDER_CAT1_CODE = 'PHA_W'";//��ҩ
			}else if(this.getValueString("PHA_TYPE").equals("2")){
				pha_type = "  AND A.ORDER_CAT1_CODE = 'PHA_C'";//�г�ҩ
			}
			if (this.getValueString("ADM_TYPE").equals("1")) {// �ż������
				// ��ѯ����ҩƷ
				sql = "SELECT '1' AS ADM_TYPE ,"
						+ "A.DEPT_CODE,SUM(A.AR_AMT) AS CLASS_GRUG_AMT,"
						+ "COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM  "
						+ "FROM OPD_ORDER A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')" + dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('" + startTime
						+ "','yyyymmddhh24miss') " + "  AND TO_DATE ('"
						+ endTime + "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE ";
				// System.out.println("=======sql is:::"+sql);
				// ��ѯȫ��ҩƷ
				sqlAll = "SELECT '1' AS ADM_TYPE ,A.DEPT_CODE, COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,"
						+ "SUM(A.AR_AMT) AS TOT_AMT  "
						+ "FROM OPD_ORDER A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ " AND A.ADM_TYPE IN ('O', 'E') "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') " + "GROUP BY A.DEPT_CODE ";
//				 System.out.println("123=======sqlAll is:::"+sqlAll);
				sumResultAll="SELECT COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,SUM(A.AR_AMT) AS TOT_AMT " +
						"FROM OPD_ORDER A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE  AND A.ADM_TYPE IN ('O', 'E') " 
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "AND TO_DATE ('"
						+ endTime 
						+ "','yyyymmddhh24miss') ";
			    sumResult="SELECT SUM(A.AR_AMT) AS CLASS_GRUG_AMT,COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM  FROM OPD_ORDER A,SYS_FEE B "
				    	+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')" + dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('" + startTime
						+ "','yyyymmddhh24miss') " + "  AND TO_DATE ('"
						+ endTime + "','yyyymmddhh24miss') ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				resultAll = new TParm(TJDODBTool.getInstance().select(sqlAll));
			} else if (this.getValueString("ADM_TYPE").equals("2")) {// סԺ����
				// ��ѯ����ҩƷ
				sql = "SELECT '2' AS ADM_TYPE, "
						+ "A.DEPT_CODE,COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM,"
						+ "SUM(A.TOT_AMT) AS CLASS_GRUG_AMT "
						+ "FROM IBS_ORDD A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ "AND B.SYS_GRUG_CLASS = '1' " + dept_code
						+ " AND A.CAT1_TYPE = 'PHA' " 
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('" + startTime
						+ "','yyyymmddhh24miss') " + "  AND TO_DATE ('"
						+ endTime + "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE ORDER BY DEPT_CODE";
//				 System.out.println("09098=======sql is:::"+sql);
				// ��ѯȫ��ҩƷ
				sqlAll = "SELECT '2' AS ADM_TYPE,A.DEPT_CODE,COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,"
						+ "SUM(A.TOT_AMT) AS TOT_AMT "
						+ "FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE ORDER BY DEPT_CODE";
				
				sumResultAll = "SELECT COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,SUM(A.TOT_AMT) AS TOT_AMT "
						+ "FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE  AND A.REXP_CODE IN ('022.01', '022.02') "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') ";
				sumResult = "SELECT SUM(A.TOT_AMT) AS CLASS_GRUG_AMT,COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM  " +
						"FROM IBS_ORDD A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime + "','yyyymmddhh24miss') ";
//				 System.out.println("סԺ09090123=======sqlAll is:::"+sqlAll);
//				 System.out.println("סԺ09090123=======sqlAll is:::"+sumResultAll);
//				 System.out.println("סԺ09090123=======sqlAll is:::"+sumResult);
				result = new TParm(TJDODBTool.getInstance().select(sql));
				resultAll = new TParm(TJDODBTool.getInstance().select(sqlAll));

			} else {// ȫԺ
				// ��ѯ����ҩƷ
				sql = "SELECT DD.ADM_TYPE,DD.DEPT_CODE,DD.CLASS_GRUG_AMT,DD.CLASS_GRUG_NUM" 
						+ " FROM(SELECT '1' AS ADM_TYPE,"
						+ "A.DEPT_CODE,SUM(A.AR_AMT) AS CLASS_GRUG_AMT,"
						+ "COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM  "
						+ "FROM OPD_ORDER A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')"
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE"
						+ "  UNION ALL "
						+ "(SELECT '2' AS ADM_TYPE, "
						+ "A.DEPT_CODE,SUM(A.TOT_AMT) AS CLASS_GRUG_AMT,"
						+ "COUNT(distinct(A.ORDER_CODE)) AS CLASS_GRUG_NUM "
						+"FROM IBS_ORDD A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY  A.DEPT_CODE  )) DD " + "ORDER BY ADM_TYPE";
//				 System.out.println("111111sql is:::"+sql);
				// ��ѯȫ��ҩƷ
				sqlAll = "SELECT DD.ADM_TYPE,DD.DEPT_CODE,DD.TOT_AMT ,DD.TOT_NUM " 
						+"FROM (SELECT '1' AS ADM_TYPE,A.DEPT_CODE, COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,"
						+ "SUM(A.AR_AMT) AS TOT_AMT  "
						+ "FROM OPD_ORDER A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ " AND A.ADM_TYPE IN ('O', 'E') "
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ dept_code
						+ "AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE "
						+ "  UNION ALL "
						+ "(SELECT '2' AS ADM_TYPE,A.DEPT_CODE,COUNT(distinct(A.ORDER_CODE)) AS TOT_NUM,"
						+ "SUM(A.TOT_AMT) AS TOT_AMT "
						+ "FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss')  "
						+ "GROUP BY A.DEPT_CODE ))DD " + "ORDER BY ADM_TYPE";
				// ��ѯ����ҩƷ
				sumResult = "SELECT SUM(DD.CLASS_GRUG_AMT) AS CLASS_GRUG_AMT,COUNT(distinct(DD.ORDER_CODE)) CLASS_GRUG_NUM" 
						+ " FROM(SELECT SUM(A.AR_AMT) AS CLASS_GRUG_AMT,"
						+ "A.ORDER_CODE  "
						+ "FROM OPD_ORDER A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')"
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.ORDER_CODE"
						+ "  UNION ALL "
						+ "(SELECT SUM(A.TOT_AMT) AS CLASS_GRUG_AMT,"
						+ "A.ORDER_CODE "
						+"FROM IBS_ORDD A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY  A.ORDER_CODE )) DD ";
//				 System.out.println("111111sql is:::"+sql);
				// ��ѯȫ��ҩƷ
				sumResultAll = "SELECT SUM(DD.TOT_AMT) AS TOT_AMT ,COUNT(distinct(DD.ORDER_CODE)) AS TOT_NUM " 
						+"FROM (SELECT A.ORDER_CODE,"
						+ "SUM(A.AR_AMT) AS TOT_AMT  "
						+ "FROM OPD_ORDER A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ " AND A.ADM_TYPE IN ('O', 'E') "
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ dept_code
						+ pha_type
						+ "AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') GROUP BY A.ORDER_CODE "
						+ "  UNION ALL "
						+ "(SELECT A.ORDER_CODE,"
						+ "SUM(A.TOT_AMT) AS TOT_AMT "
						+ "FROM IBS_ORDD A,SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ dept_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ pha_type
						+ " AND A.REXP_CODE IN ('022.01', '022.02') "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') GROUP BY A.ORDER_CODE ))DD ";
//				System.out.println("ȫԺ09090123=======sqlAll is:::"+sql);
//				System.out.println("ȫԺ09090123=======sqlAll is:::"+sqlAll);
//				 System.out.println("ȫԺ09090123=======sqlAll is:::"+sumResultAll);
//				 System.out.println("ȫԺ09090123=======sqlAll is:::"+sumResult);
				result = new TParm(TJDODBTool.getInstance().select(sql));
				resultAll = new TParm(TJDODBTool.getInstance().select(sqlAll));
//				 System.out.println("222222sqlAll is:::"+sqlAll);
			}

			
			// ���ֵ
			if (result.getCount() <= 0) {
				this.messageBox("�������ݡ�");
				// this.mtable.setParmValue(result);
				// return;
			} else {
				int m = 0;
				for (int i = 0; i < result.getCount(); i++) {
					String dept_code1 = result.getValue("DEPT_CODE", i);
					String adm_type1 = result.getValue("ADM_TYPE", i);
					for (int j = 0; j < resultAll.getCount(); j++) {
						String dept_code2 = resultAll.getValue("DEPT_CODE", j);
						String adm_type2 = resultAll.getValue("ADM_TYPE", j);
						if (adm_type1.equals(adm_type2)
								&& dept_code1.equals(dept_code2)) {
							result.setData("TOT_NUM", i, resultAll.getValue(
									"TOT_NUM", j));
							result.setData("TOT_AMT", i, resultAll.getValue(
									"TOT_AMT", j));
						}
					}
//					all_num_count += result.getDouble("TOT_NUM", i);
//					class_num_count += result.getDouble("CLASS_GRUG_NUM", i);
//					all_amt_count += result.getDouble("TOT_AMT", i);
//					class_amt_count += result.getDouble("CLASS_GRUG_AMT", i);

					// ����ÿһ�еİٷֱ�
					double tot_amt = result.getDouble("TOT_AMT", i);
					double tot_num = result.getDouble("TOT_NUM", i);
					double class_grug_num = result.getDouble("CLASS_GRUG_NUM",
							i);
					double class_grug_amt = result.getDouble("CLASS_GRUG_AMT",
							i);
					double amt_rate = 0.0000;
					String amt_real_rate = "";
					double num_rate = 0.0000;
					String num_real_rate = "";
					if (class_grug_num <= 0) {
						num_real_rate = num_rate + "%" + "";
						result.setData("NUM_RATE", i, num_real_rate);
					}
					if (class_grug_amt <= 0) {
						amt_real_rate = amt_rate + "%" + "";
						result.setData("AMT_RATE", i, amt_real_rate);
					}
					if (class_grug_num > 0 && class_grug_amt > 0) {
						// ����
						num_rate = class_grug_num / tot_num;
						BigDecimal bdi = new BigDecimal(num_rate);
						bdi = bdi.setScale(4, 4);
						BigDecimal parmMuli = new BigDecimal(100);
						parmMuli = parmMuli.setScale(0);
						num_rate = bdi.multiply(parmMuli).doubleValue();
						num_real_rate = num_rate + "%" + "";
						result.setData("NUM_RATE", i, num_real_rate);
						// ���
						amt_rate = class_grug_amt / tot_amt;
						BigDecimal bdi1 = new BigDecimal(amt_rate);
						bdi = bdi1.setScale(4, 4);
						BigDecimal parmMuli1 = new BigDecimal(100);
						parmMuli = parmMuli1.setScale(0);
						amt_rate = bdi.multiply(parmMuli1).doubleValue();
						amt_real_rate = amt_rate + "%" + "";
						result.setData("AMT_RATE", i, amt_real_rate);
					}
					m++;
				}
				// �ܼƵļ���
				// System.out.println("==========all_num_count::"+all_num_count);
				// ADM_TYPE;DEPT_CODE;CLASS_GRUG_AMT;CLASS_GRUG_NUM;TOT_AMT;TOT_NUM;AMT_RATE;NUM_RATE
				resultSum=new TParm(TJDODBTool.getInstance().select(sumResult));
				resultSumParm=new TParm(TJDODBTool.getInstance().select(sumResultAll));
				result.setData("ADM_TYPE", m, "�ܼ�:");
				result.setData("DEPT_CODE", m, "");
				result.setData("CLASS_GRUG_AMT", m, resultSum.getDouble("CLASS_GRUG_AMT",0));
				result.setData("CLASS_GRUG_NUM", m, resultSum.getInt("CLASS_GRUG_NUM",0));
				result.setData("TOT_AMT", m, resultSumParm.getDouble("TOT_AMT",0));
				result.setData("TOT_NUM", m, resultSumParm.getInt("TOT_NUM",0));
				result.setData("AMT_RATE", m, StringTool.round(resultSum.getDouble("CLASS_GRUG_AMT",0)/
						resultSumParm.getDouble("TOT_AMT",0)*100, 2)+"%");
				result.setData("NUM_RATE", m, StringTool.round(resultSum.getDouble("CLASS_GRUG_NUM",0)/
						resultSumParm.getDouble("TOT_NUM",0)*100, 2)+"%");
				// this.mtable.setParmValue(result);
			}
			// System.out.println("��� �ǣ���"+result);
			mtable.setParmValue(result);
		} else if (pageFlg == 1) {// ��ϸ
			String dept_code1 = "";// ��ѯ�������Ƿ��п�������
			if (this.getValueString("DEPT_CODE1") != null
					&& !this.getValueString("DEPT_CODE1").equals("")) {
				dept_code1 = "  AND A.DEPT_CODE = '"
						+ this.getValueString("DEPT_CODE1") + "'";
			}
			String dr_code = "";// ��ѯ�������Ƿ���ҽ������
			if (this.getValueString("DR_CODE") != null
					&& !this.getValueString("DR_CODE").equals("")) {
				dr_code = "  AND A.DR_CODE = '"
						+ this.getValueString("DR_CODE") + "'";
			}
			if (this.getValueString("ADM_TYPE").equals("1")) {// �ż���
				// ��ѯ����ҩƷ
				sql = "SELECT '1' AS ADM_TYPE,"
						+ "A.DEPT_CODE,A.DR_CODE,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) AS SUM_QTY,"
						+ "A.OWN_PRICE,SUM(A.OWN_PRICE * A.DOSAGE_QTY) AS SUM_AMT  "
						+ "FROM OPD_ORDER A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')"
						+ dept_code1
						+ dr_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE,A.ADM_TYPE,A.DR_CODE,A.ORDER_CODE,A.ORDER_DESC,"
						+ "A.SPECIFICATION,A.DOSAGE_UNIT,A.OWN_PRICE"
						+ " ORDER BY A.ADM_TYPE";
				// System.out.println("GFDSA=======sql is:::"+sql);
			} else if (this.getValueString("ADM_TYPE").equals("2")) {// סԺ
				// ��ѯ����ҩƷ
				sql = "SELECT '2' AS ADM_TYPE, "
						+ "A.DEPT_CODE,A.DR_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC AS ORDER_DESC,"
						+ "B.SPECIFICATION,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) AS SUM_QTY,"
						+ "A.OWN_PRICE,SUM(A.OWN_PRICE * A.DOSAGE_QTY) AS SUM_AMT  "
						+ "FROM IBS_ORDD A,SYS_FEE B   "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+
						// "AND A.CASE_NO = C.CASE_NO AND " +
						// "C.CANCEL_FLG <> 'Y' AND A.DEPT_CODE = C.DEPT_CODE "+
						"AND B.SYS_GRUG_CLASS = '1' "
						+ dept_code1
						+ dr_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE,A.DS_FLG,A.DR_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC,"
						+ "B.SPECIFICATION,A.DOSAGE_UNIT,A.OWN_PRICE"
						+ " ORDER BY ADM_TYPE";
//				 System.out.println("09098=======sql is:::"+sql);

			} else {// ȫԺ
				sql = "SELECT '1' AS ADM_TYPE,"
						+ "A.DEPT_CODE,A.DR_CODE,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) AS SUM_QTY,"
						+ "A.OWN_PRICE,SUM(A.OWN_PRICE * A.DOSAGE_QTY) AS SUM_AMT  "
						+ "FROM OPD_ORDER A,SYS_FEE B "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE  "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ "AND A.ADM_TYPE IN ('O', 'E')"
						+ dept_code1
						+ dr_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE,A.ADM_TYPE,A.DR_CODE,A.ORDER_CODE,A.ORDER_DESC,"
						+ "A.SPECIFICATION,A.DOSAGE_UNIT,A.OWN_PRICE"
						+
						// " ORDER BY A.DEPT_CODE,A.ORDER_CODE" +
						" UNION ALL "
						+ "(SELECT '2' AS ADM_TYPE, "
						+ "A.DEPT_CODE,A.DR_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC AS ORDER_DESC,"
						+ "B.SPECIFICATION,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) AS SUM_QTY,"
						+ "A.OWN_PRICE,SUM(A.OWN_PRICE * A.DOSAGE_QTY) AS SUM_AMT  "
						+ "FROM IBS_ORDD A,SYS_FEE B   "
						+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
						+ "AND B.SYS_GRUG_CLASS = '1' "
						+ dept_code1
						+ dr_code
						+ " AND A.CAT1_TYPE = 'PHA' "
						+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
						+ startTime
						+ "','yyyymmddhh24miss') "
						+ "  AND TO_DATE ('"
						+ endTime
						+ "','yyyymmddhh24miss') "
						+ "GROUP BY A.DEPT_CODE,A.DS_FLG,A.DR_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC,"
						+ "B.SPECIFICATION,A.DOSAGE_UNIT,A.OWN_PRICE)"
						+ "ORDER BY ADM_TYPE";
			}
			result = new TParm(TJDODBTool.getInstance().select(sql));
			// ���ֵ
			if (result.getCount() <= 0) {
				this.messageBox("�������ݡ�");
			} else {
				int m = 0;
				double all_num_count = 0;// ���㡰�ܼơ���������
				double all_amt_count = 0;// ���㡰�ܼơ����ܽ��
				for (int i = 0; i < result.getCount(); i++) {
					all_num_count += result.getDouble("SUM_QTY", i);
					all_amt_count += result.getDouble("SUM_AMT", i);
					m++;
				}
				result.setData("ADM_TYPE", m, "�ܼ�:");
				result.setData("DEPT_CODE", m, "");
				result.setData("DR_CODE", m, "");
				result.setData("ORDER_CODE", m, "");
				result.setData("ORDER_DESC", m, "");
				result.setData("SPECIFICATION", m, "");
				result.setData("DOSAGE_UNTI", m, "");
				result.setData("SUM_QTY", m, all_num_count);
				result.setData("OWN_PRICE", m, "");
				result.setData("SUM_AMT", m, all_amt_count);

			}
			dtable.setParmValue(result);
		}
	}

	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ((TTabbedPane) this.getComponent("PAGETAB"));
		if (tabbedPane.getSelectedIndex() == 0) {
			pageFlg = 0;// ����
		} else {
			pageFlg = 1;// ��ϸ
		}
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = null;
		try {
			table = pageFlg == 0 ? table = (TTable) callFunction("UI|TABLEM|getThis")
					: (TTable) callFunction("UI|TABLED|getThis");
			if (pageFlg == 0) {
				ExportExcelUtil.getInstance().exportExcel(table,
						"����ҩ���ֵ�ͳ�Ʊ������ܣ�");
			} else if (pageFlg == 1) {
				ExportExcelUtil.getInstance().exportExcel(table,
						"����ҩ���ֵ�ͳ�Ʊ�����ϸ��");
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("û�пɵ��������ݣ�");
			return;
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		// ����ʱ��ؼ���ֵ
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// �������е�����
		if (pageFlg == 0) {
			mtable.removeRowAll();
		} else if (pageFlg == 1) {
			dtable.removeRowAll();
		}
		// ��������ؼ���ֵ
		this.clearValue("DEPT_CODE;DEPT_CODE1;DR_OE_CODE");
		this.setValue("ADM_TYPE", '1');
	}

	/**
	 * ��ȡTABLE����
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

}
