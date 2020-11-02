package com.javahis.ui.udd;

import java.math.BigDecimal;
import java.sql.Timestamp; //import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable; //import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat; //import com.dongyang.ui.event.TPopupMenuEvent;
//import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

//import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��/��/ס�ﻼ�߿���ҩ�ﴦ������ͳ��
 * </p>
 * 
 * <p>
 * Description: ��/��/ס�ﻼ�߿���ҩ�ﴦ������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Bluecore
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author yanjing
 * @version 1.0
 */
public class UDDpatAntiPrestatisticsControl extends TControl {
	private TTable table_i;// סԺ��
	private TTable table_oe;// �ż����
	private TTable table_oes;// �ż����---����
	private int pageFlg = 0;// ҳǩ��ǣ�0��סԺ��1���ż��

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ��ʼ��ʱ��ؼ�
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.table_i = this.getTable("TABLE_I");// �õ�סԺ���
		this.table_oe = this.getTable("TABLE_OE");// �õ��ż�����
		table_oes = this.getTable("TABLE_OES");// �õ��ż�����---����
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		setValue("OPT_USER", Operator.getID());// ��ʼ��������
		this.clearValue("ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
		this.callFunction("UI|ODI_TYPE|setEnabled", false);
	}

	/**
	 * ��ȡ������
	 * 
	 * @param tagName
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) this.getComponent(tagName);
	}

	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTTabbedPane() {
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) {
			pageFlg = 0;// סԺ
		} else if (tabbedPane.getSelectedIndex() == 1) {
			pageFlg = 1;// �ż���
		} else {
			pageFlg = 2;// �ż���--����
		}
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String dr_code = "";// ��ѯ�������Ƿ���ҽ��
		String adm_type = "";// ��ѯ�������Ƿ����ż�������
		String adm_types = "";
		String dr_codes ="";
		if (this.getValueString("ADM_TYPE") != null
				&& !this.getValueString("ADM_TYPE").equals("")) {
			adm_type = "  AND A.ADM_TYPE = '" + this.getValueString("ADM_TYPE")
					+ "'";
		}// sql������ż�������
		if (this.getValueString("ADM_TYPES") != null
				&& !this.getValueString("ADM_TYPES").equals("")) {
			adm_types = "  AND A.ADM_TYPE = '" + this.getValueString("ADM_TYPES")
					+ "'";
		}// sql������ż�������
		if (this.getValueString("DR_OE_CODE") != null
				&& !this.getValueString("DR_OE_CODE").equals("")) {
			dr_code = "  AND A.DR_CODE = '" + this.getValueString("DR_OE_CODE")
					+ "'";
		}// sql�����ҽ����ѯ����
		
		if (this.getValueString("DR_OE_CODES") != null
				&& !this.getValueString("DR_OE_CODES").equals("")) {
			dr_codes = "  AND A.DR_CODE = '" + this.getValueString("DR_OE_CODES")
					+ "'";
		}// sql�����ҽ����ѯ����
		String region_code = "";// ��������ѯ�������ż���
		if (this.getValueString("REGION_CODE") != null
				&& !this.getValueString("REGION_CODE").equals("")) {
			region_code = "  AND A.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
		}
		String region_code1 = "";// ��������ѯ����,סԺ
		if (this.getValueString("REGION_CODE") != null
				&& !this.getValueString("REGION_CODE").equals("")) {
			region_code1 = "  AND M.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
		}
		String odi_dr_code = "";// סԺҽ��
		String odi_type = "";// סԺ״̬
		String odi_dept_code = "";// סԺ����
		if (this.getValueString("DR_CODE") != null
				&& !this.getValueString("DR_CODE").equals("")) {
			odi_dr_code = "  AND M.VS_DR_CODE = '"
					+ this.getValueString("DR_CODE") + "'";
		}// sql�м�ҽ����ѯ����
		if (this.getValueString("ODI_TYPE") != null
				&& !this.getValueString("ODI_TYPE").equals("")) {
			if (this.getValueString("ODI_TYPE").equals("1")) {
				odi_type = " AND M.DS_DATE IS NULL ";
			} else {
				odi_type = " AND M.DS_DATE IS NOT NULL ";
			}
		}// sql�м�סԺ״̬��ѯ����
		if (this.getValueString("DEPT_CODE") != null
				&& !this.getValueString("DEPT_CODE").equals("")) {
			odi_dept_code = "  AND M.DEPT_CODE = '"
					+ this.getValueString("DEPT_CODE") + "'";
		}// sql�мӿ��Ҳ�ѯ����
		TTextFormat startDateComp = (TTextFormat) this.getComponent("S_DATE");// ��ʼʱ��
		TTextFormat endDateComp = (TTextFormat) this.getComponent("E_DATE");// ����ʱ��
		// ����ʱ��ĸ�ʽ
		String startDate = StringTool.getString((Timestamp) startDateComp
				.getValue(), "yyyyMMddHHmmss");
		String endDate = StringTool.getString((Timestamp) endDateComp
				.getValue(), "yyyyMMddHHmmss");
		// �����Ų�ѯ�����TParm
		TParm result = new TParm();
		TParm result1 = new TParm();
		TTabbedPane tabbedPane = ((TTabbedPane) this
				.getComponent("tTabbedPane_2"));
		if (tabbedPane.getSelectedIndex() == 0) { // ����סԺҳǩʱ
			// ��ѯ���е�סԺ����
			String SQL_I = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE, "
					+ "F.USER_NAME AS DR_CODE,S.ANTIBIOTIC_CODE,M.CASE_NO,"
					+ "CASE WHEN M.DS_DATE IS NOT NULL THEN '��Ժ' ELSE '��Ժ' END AS ODI_TYPE "
					+ "FROM ADM_INP M,SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F,(SELECT A.CASE_NO,A.DEPT_CODE, A.VS_DR_CODE,"
					+ "CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL  THEN '01' ELSE '' END ANTIBIOTIC_CODE "
					+ "FROM ADM_INP A,ODI_ORDER B, PHA_BASE C  "
					+ "WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE(+)"
					+ " AND A.DS_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "GROUP BY A.DEPT_CODE, A.VS_DR_CODE, C.ANTIBIOTIC_CODE,A.CASE_NO) S "
					+ "WHERE M.CASE_NO = S.CASE_NO AND M.CANCEL_FLG <> 'Y' "
					+ "AND M.REGION_CODE = D.REGION_CODE AND "
					+ " M.DEPT_CODE = E.DEPT_CODE AND M.VS_DR_CODE = F.USER_ID "
					+ region_code1
					+ odi_dr_code
					+ odi_type
					+ odi_dept_code
					+ "GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,M.DS_DATE,S.ANTIBIOTIC_CODE,M.CASE_NO "
					+ "ORDER BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,ODI_TYPE,M.CASE_NO";
			result1 = new TParm(TJDODBTool.getInstance().select(SQL_I));
			// �����ѯ���,��CASE_NO�ظ�ʱֻ����һ������
			TParm result2 = new TParm();
			for (int i = 0; i < result1.getCount(); i++) {
				String case_no1 = result1.getValue("CASE_NO", i);
				String case_no2 = result1.getValue("CASE_NO", i + 1);
				if (case_no1.equals(case_no2)) {
					result2.addRowData(result1, i);
					i++;
				} else {
					result2.addRowData(result1, i);
				}
			}
			// �����ѯ���������Ժ������Ժ״̬�����ҡ�ҽ����ͬ������
			TParm result3 = new TParm();
			double tot_num = 1.0;
			for (int i = 0; i < result2.getCount(); i++) {
				String dept_code1 = result2.getValue("DEPT_CODE", i);
				String dr_code1 = result2.getValue("DR_CODE", i);
				String odi_type1 = result2.getValue("ODI_TYPE", i);
				String dept_code2 = result2.getValue("DEPT_CODE", i + 1);
				String dr_code2 = result2.getValue("DR_CODE", i + 1);
				String odi_type2 = result2.getValue("ODI_TYPE", i + 1);
				// double tot_num = 1.0;
				if (dept_code1.equals(dept_code2) && dr_code1.equals(dr_code2)
						&& odi_type1.equals(odi_type2)) {
					tot_num++;
				} else {
					result3.addRowData(result2, i);
					result3.setData("TOT_NUM", i, tot_num);
					tot_num = 1.0;
				}

			}
			// ��ѯ����ʹ�ÿ���ҩ������
			TParm result11 = new TParm();
			String sql_i = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE, "
					+ "F.USER_NAME AS DR_CODE,S.ANTIBIOTIC_CODE,M.CASE_NO,"
					+ "CASE WHEN M.DS_DATE IS NOT NULL THEN '��Ժ' ELSE '��Ժ' END AS ODI_TYPE "
					+ "FROM ADM_INP M,SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F,(SELECT A.CASE_NO,A.DEPT_CODE, A.VS_DR_CODE,"
					+ "CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL  THEN '01' ELSE '' END ANTIBIOTIC_CODE "
					+ "FROM ADM_INP A,ODI_ORDER B, PHA_BASE C  "
					+ "WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE"
					+ " AND A.DS_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "GROUP BY A.DEPT_CODE, A.VS_DR_CODE, C.ANTIBIOTIC_CODE,A.CASE_NO) S "
					+ "WHERE M.CASE_NO = S.CASE_NO AND M.CANCEL_FLG <> 'Y' "
					+ "AND M.REGION_CODE = D.REGION_CODE AND S.ANTIBIOTIC_CODE IS NOT NULL "
					+ " AND M.DEPT_CODE = E.DEPT_CODE AND M.VS_DR_CODE = F.USER_ID "
					+ region_code1
					+ odi_dr_code
					+ odi_type
					+ odi_dept_code
					+ "GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,M.DS_DATE,S.ANTIBIOTIC_CODE,M.CASE_NO "
					+ "ORDER BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC, F.USER_NAME,ODI_TYPE,M.CASE_NO ";
			result11 = new TParm(TJDODBTool.getInstance().select(sql_i));
			// �����ѯ���,��CASE_NO�ظ�ʱֻ����һ������
			TParm result12 = new TParm();
			for (int i = 0; i < result11.getCount(); i++) {
				String case_no1 = result11.getValue("CASE_NO", i);
				String case_no2 = result11.getValue("CASE_NO", i + 1);
				if (case_no1.equals(case_no2)) {
					result12.addRowData(result11, i);
					i++;
				} else {
					result12.addRowData(result11, i);
				}
			}
			// �����ѯ���������Ժ������Ժ״̬�����ҡ�ҽ����ͬ������
			TParm result13 = new TParm();
			double anti_num = 1.0;
			for (int i = 0; i < result12.getCount(); i++) {
				String dept_code1 = result12.getValue("DEPT_CODE", i);
				String dr_code1 = result12.getValue("DR_CODE", i);
				String odi_type1 = result12.getValue("ODI_TYPE", i);
				String dept_code2 = result12.getValue("DEPT_CODE", i + 1);
				String dr_code2 = result12.getValue("DR_CODE", i + 1);
				String odi_type2 = result12.getValue("ODI_TYPE", i + 1);
				// double tot_num = 1.0;
				if (dept_code1.equals(dept_code2) && dr_code1.equals(dr_code2)
						&& odi_type1.equals(odi_type2)) {
					anti_num++;
				} else {
					result13.addRowData(result12, i);
					result13.setData("ANTI_NUM", i, anti_num);
					anti_num = 1.0;
				}

			}
			// ��result3��result13���������ϵ�result��
			for (int i = 0; i < result3.getCount(); i++) {
				String dept_code1 = result3.getValue("DEPT_CODE", i);
				String dr_code1 = result3.getValue("DR_CODE", i);
				String odi_type1 = result3.getValue("ODI_TYPE", i);
				double anti_num2 = 0;
				for (int j = 0; j < result13.getCount(); j++) {
					String dept_code2 = result13.getValue("DEPT_CODE", j);
					String dr_code2 = result13.getValue("DR_CODE", j);
					String odi_type2 = result13.getValue("ODI_TYPE", j);
					if (dept_code1.equals(dept_code2)
							&& dr_code1.equals(dr_code2)
							&& odi_type1.equals(odi_type2)) {
						anti_num2 = result13.getDouble("ANTI_NUM", j);
					}
				}
				result.addRowData(result3, i);
				result.setData("ANTI_NUM", i, anti_num2);
			}

			if (result.getCount("REGION_CODE") < 0) {
				messageBox("��������");
				TParm resultparm = new TParm();
				this.table_i.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();
				int j = 0;
				double count_tot = 0;
				double count_anti = 0;
				double all_count = 0;// ���㡰�ܼơ���������
				double all_anti_count = 0;// ���㡰�ܼơ���ʹ�ÿ���ҩ������
				for (int i = 0; i < result.getCount("REGION_CODE"); i++) {
					double rx_no = result.getDouble("TOT_NUM", i);
					double rx_anti_no = result.getDouble("ANTI_NUM", i);
					all_count += rx_no;
					all_anti_count += rx_anti_no;
					double rx_anti_rate = 0.0000;
					String real_rate = "";
					if (rx_anti_no <= 0) {
						real_rate = rx_anti_rate + "%" + "";
						result.setData("ANTI_RATE", i, real_rate);
					} else {
						// ����ÿһ�еİٷֱ�
						rx_anti_rate = rx_anti_no / rx_no;
						BigDecimal bdi = new BigDecimal(rx_anti_rate);
						bdi = bdi.setScale(4, 4);
						BigDecimal parmMuli = new BigDecimal(100);
						parmMuli = parmMuli.setScale(0);
						rx_anti_rate = bdi.multiply(parmMuli).doubleValue();
						real_rate = rx_anti_rate + "%" + "";
						result.setData("ANTI_RATE", i, real_rate);
					}
					String d_code = result.getValue("DEPT_CODE", i);
					String next_dr_code = result.getValue("DEPT_CODE", i + 1);
					if (i == 0 || (d_code.equals(next_dr_code))) {
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						j++;
					} else {
						// ���㡰С�ơ��еİٷֱ�
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						double rate = count_anti / count_tot;
						BigDecimal bdi1 = new BigDecimal(rate);
						bdi1 = bdi1.setScale(4, 4);
						BigDecimal parmMuli1 = new BigDecimal(100);
						parmMuli1 = parmMuli1.setScale(0);
						rate = bdi1.multiply(parmMuli1).doubleValue();
						real_rate = rate + "%" + "";
						result.addData("RX_ANTI_RATE", real_rate);
						resultParm.setData("REGION_CODE", ++j, "С��:");
						resultParm.setData("ODI_TYPE", ++j, "");
						resultParm.setData("DEPT_CODE", ++j, d_code);
						resultParm.setData("DR_CODE", ++j, "");
						resultParm.setData("TOT_NUM", ++j, count_tot);
						resultParm.setData("ANTI_NUM", ++j, count_anti);
						resultParm.setData("ANTI_RATE", ++j, real_rate);
						j++;
						count_tot = 0;
						count_anti = 0;

					}

				}
				// �����ܼ��еİٷֱ�
				double last_rate = all_anti_count / all_count;
				BigDecimal bdi = new BigDecimal(last_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				last_rate = bdi.multiply(parmMuli).doubleValue();
				String real_rate1 = last_rate + "%" + "";
				resultParm.setData("REGION_CODE", j, "�ܼ�:");
				resultParm.setData("ODI_TYPE", j, "");
				resultParm.setData("DEPT_CODE", j, "");
				resultParm.setData("DR_CODE", j, "");
				resultParm.setData("TOT_NUM", j, all_count);
				resultParm.setData("ANTI_NUM", j, all_anti_count);
				resultParm.setData("ANTI_RATE", j, real_rate1);
				this.table_i.setParmValue(resultParm);
			}
		} else if (tabbedPane.getSelectedIndex() == 1) {
			// ��ѯ���еĴ���ǩ��
			String SQL = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE,"
					+ " CASE A.ADM_TYPE WHEN 'O' THEN '����' ELSE '����' END AS ADM_TYPE,F.USER_NAME AS DR_CODE,"
					+ " COUNT(DISTINCT (B.RX_NO)) AS RX_NUM  "
					+
					// " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(B.RX_NO) END AS RX_ANT_NUM "
					// +
					" FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C , SYS_REGION D,SYS_DEPT E,"
					+ " SYS_OPERATOR F "
					+ " WHERE A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') AND A.CASE_NO = B.CASE_NO AND "
					+ " A.REGCAN_USER IS NULL AND B.ORDER_CODE = C.ORDER_CODE AND B.REGION_CODE = D.REGION_CODE AND "
					+ " B.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID  AND B.BILL_FLG='Y' "
					+ region_code
					+ dr_code
					+ adm_type
					+ " GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC,A.ADM_TYPE,F.USER_NAME"
					+ " ORDER BY E.DEPT_CHN_DESC,F.USER_NAME";
			result = new TParm(TJDODBTool.getInstance().select(SQL));
			// ��ѯ����ҩ�ﴦ��ǩ��
			String sql = "SELECT E.DEPT_CHN_DESC AS DEPT_CODE , F.USER_NAME AS DR_CODE,"
					+ " CASE B.ADM_TYPE WHEN 'O' THEN '����' ELSE '����' END AS ADM_TYPE,"
					+ " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(DISTINCT (B.RX_NO)) END AS RX_ANT_NUM"
					+ " FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C, SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F "
					+ " WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND "
					+ " A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') AND A.REGION_CODE = B.REGION_CODE "
					+ " AND B.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID  AND B.BILL_FLG='Y' "
					+ region_code
					+ dr_code
					+ adm_type
					+ " GROUP BY C.ANTIBIOTIC_CODE,B.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME"
					+ " ORDER BY E.DEPT_CHN_DESC,F.USER_NAME";
			result1 = new TParm(TJDODBTool.getInstance().select(sql));
			for (int j = 0; j < result.getCount(); j++) {
				// ȡ��result�е��ż��𡢿��ҡ�ҽ��
				String adm_type0 = result.getValue("ADM_TYPE", j);
				String dept_code0 = result.getValue("DEPT_CODE", j);
				String dr_code0 = result.getValue("DR_CODE", j);
				// ���������У����̵��ż��𡢿��Һ�ҽ���Ƿ���ͬ
				String adm_type1 = "";
				String adm_type2 = "";
				String dept_code1 = "";
				String dept_code2 = "";
				String dr_code1 = "";
				String dr_code2 = "";
				double rx_ant_num1 = 0;
				for (int i = 0; i < result1.getCount(); i++) {
					// ȡ��result�е��ż��𡢿��ҡ�ҽ��
					adm_type1 = result1.getValue("ADM_TYPE", i);
					dept_code1 = result1.getValue("DEPT_CODE", i);
					dr_code1 = result1.getValue("DR_CODE", i);
					if (adm_type0.equals(adm_type1)
							&& dept_code0.equals(dept_code1)
							&& dr_code0.equals(dr_code1)) {
						adm_type2 = result1.getValue("ADM_TYPE", i + 1);
						dept_code2 = result1.getValue("DEPT_CODE", i + 1);
						dr_code2 = result1.getValue("DR_CODE", i + 1);
						if (adm_type1.equals(adm_type2)
								&& dept_code1.equals(dept_code2)
								&& dr_code1.equals(dr_code2)) {
							// ȡ��result1 �е�RX_ANT_NUMֵ
							double temp = result1.getDouble("RX_ANT_NUM", i);
							rx_ant_num1 += temp;
						}
					}
				}
				result.setData("RX_ANT_NUM", j, rx_ant_num1);
			}
			if (result.getCount() < 0) {
				messageBox("��������");
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();// ����д�������
				int j = 0;// ��¼resultParm������
				double count_tot = 0;// С�����ܴ�����
				double count_anti = 0;// С����ʹ�ÿ����صĴ�����
				double all_count = 0;// �ܼ����ܴ�����
				double all_anti_count = 0;// �ܼ���ʹ�ÿ����صĴ�����
				for (int i = 0; i < result.getCount(); i++) {
					double rx_no = result.getDouble("RX_NUM", i);
					double rx_anti_no = result.getDouble("RX_ANT_NUM", i);
					all_count += rx_no;
					all_anti_count += rx_anti_no;
					double rx_anti_rate = 0.0000;
					String real_rate = "";
					if (rx_anti_no <= 0) {
						real_rate = rx_anti_rate + "%" + "";
						result.setData("RX_ANTI_RATE", i, real_rate);
					} else {
						// ����ÿһ�еĿ���ҩ�ﴦ������ start
						rx_anti_rate = rx_anti_no / rx_no;
						BigDecimal bdi = new BigDecimal(rx_anti_rate);
						bdi = bdi.setScale(4, 4);
						BigDecimal parmMuli = new BigDecimal(100);
						parmMuli = parmMuli.setScale(0);
						rx_anti_rate = bdi.multiply(parmMuli).doubleValue();
						real_rate = rx_anti_rate + "%" + "";
						result.setData("RX_ANTI_RATE", i, real_rate);
						// ����ÿһ�еĿ���ҩ�ﴦ������ end
					}
					String d_code = result.getValue("DEPT_CODE", i);// ȥ�������Ŀ���
					String next_dr_code = result.getValue("DEPT_CODE", i + 1);
					if ((d_code.equals(next_dr_code))) {// ����̵Ŀ�����ͬ
						count_tot += rx_no;
						count_anti += rx_anti_no;
						resultParm.addRowData(result, i);
						j++;
					} else {// ����̵Ŀ��Ҳ�ͬ
						count_tot += rx_no;// ����ÿ����С�ơ���������
						count_anti += rx_anti_no;// ����ÿ����С�ơ���ʹ�ÿ����ص�����
						resultParm.addRowData(result, i);// resultParm
															// �����ó���������ʹ�ÿ���������������֮���ֵ
						// ���㡰С�ơ��е�ʹ����
						double rate = count_anti / count_tot;
						BigDecimal bdi1 = new BigDecimal(rate);
						bdi1 = bdi1.setScale(4, 4);
						BigDecimal parmMuli1 = new BigDecimal(100);
						parmMuli1 = parmMuli1.setScale(0);
						rate = bdi1.multiply(parmMuli1).doubleValue();
						real_rate = rate + "%" + "";
						result.addData("RX_ANTI_RATE", real_rate);
						// ���á�С�ơ��е�ֵ
						resultParm.setData("REGION_CODE", ++j, "С��:");
						resultParm.setData("ADM_TYPE", ++j, "");
						resultParm.setData("DEPT_CODE", ++j, d_code);
						resultParm.setData("DR_CODE", ++j, "");
						resultParm.setData("RX_NUM", ++j, count_tot);
						resultParm.setData("RX_ANT_NUM", ++j, count_anti);
						resultParm.setData("RX_ANTI_RATE", ++j, real_rate);
						j++;
						count_tot = 0;// "С��"��ֵ���������������������
						count_anti = 0;// "С��"��ֵ��������ʹ�ÿ����ص������������
					}
				}
				// ���á��ܼơ��е�ֵ
				// ���㡰�ܼơ��аٷֱȣ�start
				double last_rate = all_anti_count / all_count;
				BigDecimal bdi = new BigDecimal(last_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				last_rate = bdi.multiply(parmMuli).doubleValue();
				String real_rate1 = last_rate + "%" + "";
				// ���㡰�ܼơ��аٷֱȣ�end
				resultParm.setData("REGION_CODE", j, "�ܼ�:");
				resultParm.setData("ADM_TYPE", j, "");
				resultParm.setData("DEPT_CODE", j, "");
				resultParm.setData("DR_CODE", j, "");
				resultParm.setData("RX_NUM", j, all_count);
				resultParm.setData("RX_ANT_NUM", j, all_anti_count);
				resultParm.setData("RX_ANTI_RATE", j, real_rate1);
				this.table_oe.setParmValue(resultParm);// Ϊ���ֵ
			}
		}else{//=========pangben 2013-11-21
			// ��ѯ���е�����
			String SQL = "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE,"
					+ " CASE A.ADM_TYPE WHEN 'O' THEN '����' ELSE '����' END AS ADM_TYPE,F.USER_NAME AS DR_CODE,"
					+ " COUNT(DISTINCT (A.CASE_NO)) AS RX_NUM ,0 RX_ANT_NUM,'0.0%' AS RX_ANTI_RATE,B.PHA_NUM "
					+
					// " CASE WHEN C.ANTIBIOTIC_CODE IS NOT NULL THEN COUNT(B.RX_NO) END AS RX_ANT_NUM "
					// +
					" FROM REG_PATADM A, SYS_REGION D,SYS_DEPT E,"
					+ " SYS_OPERATOR F,(SELECT A.DEPT_CODE,A.REGION_CODE,A.DR_CODE ,COUNT(DISTINCT (A.CASE_NO)) PHA_NUM FROM REG_PATADM A,OPD_ORDER B " 
					+ "WHERE A.CASE_NO=B.CASE_NO AND B.CAT1_TYPE='PHA' AND A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ region_code
					+ dr_codes
					+ adm_types+" GROUP BY A.DEPT_CODE,A.REGION_CODE,A.DR_CODE) B"
					+ " WHERE A.ADM_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','yyyymmddhh24miss')"
					+ " AND TO_DATE('"
					+ endDate
					+ "','yyyymmddhh24miss') AND "
					+ " A.REGCAN_USER IS NULL AND A.REGION_CODE = D.REGION_CODE AND "
					+ " A.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID AND "
					+ " A.REGION_CODE=B.REGION_CODE(+) AND A.DR_CODE=B.DR_CODE(+) AND"
					+ " A.DEPT_CODE=B.DEPT_CODE(+) "
					+ region_code
					+ dr_codes
					+ adm_types
					+ " GROUP BY D.REGION_CHN_ABN,E.DEPT_CHN_DESC,A.ADM_TYPE,F.USER_NAME,B.PHA_NUM "
					+ " ORDER BY D.REGION_CHN_ABN,A.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME";
			//System.out.println("SQL:sdfasdfs::::"+SQL);
			result = new TParm(TJDODBTool.getInstance().select(SQL));
			// ��ѯ����ҩ�ﴦ��ǩ��
			String sql =" SELECT   S.REGION_CODE,S.DEPT_CODE , S.DR_CODE,ADM_TYPE,COUNT(S.CASE_NO) AS RX_ANT_NUM FROM ("+
                        "SELECT D.REGION_CHN_ABN AS REGION_CODE,E.DEPT_CHN_DESC AS DEPT_CODE , F.USER_NAME AS DR_CODE,"+
                        "CASE A.ADM_TYPE WHEN 'O' THEN '����' ELSE '����' END AS ADM_TYPE,A.CASE_NO "+
                        "FROM REG_PATADM A,OPD_ORDER B,PHA_BASE C, SYS_REGION D,SYS_DEPT E,SYS_OPERATOR F "+
                    " WHERE A.REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND "+
                    " A.ADM_DATE BETWEEN TO_DATE('"+startDate+"','yyyymmddhh24miss') "+
                    " AND TO_DATE('"+endDate+"','yyyymmddhh24miss') AND A.REGION_CODE = D.REGION_CODE "+
                    " AND A.DEPT_CODE = E.DEPT_CODE AND A.DR_CODE = F.USER_ID "+
                    " AND C.ANTIBIOTIC_CODE IS NOT NULL AND B.BILL_FLG='Y' "
                    + region_code
					+ dr_codes
					+ adm_types
					+
                    " GROUP BY D.REGION_CHN_ABN,C.ANTIBIOTIC_CODE,A.ADM_TYPE,E.DEPT_CHN_DESC,F.USER_NAME,A.CASE_NO )S"+
                    " GROUP BY S.REGION_CODE,S.DEPT_CODE , S.DR_CODE,ADM_TYPE"+
                    " ORDER BY S.REGION_CODE,S.ADM_TYPE,S.DEPT_CODE,S.DR_CODE";
			result1 = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount()<=0) {
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				this.messageBox("û�в�ѯ������");
				return;
			}
			String adm_type0="";
			String dept_code0="";
			String dr_code0="";
			// ���������У����̵��ż��𡢿��Һ�ҽ���Ƿ���ͬ
			for (int j = 0; j < result.getCount(); j++) {
				// ȡ��result�е��ż��𡢿��ҡ�ҽ��
				adm_type0 = result.getValue("ADM_TYPE", j);
				dept_code0 = result.getValue("DEPT_CODE", j);
				dr_code0 = result.getValue("DR_CODE", j);
				for (int i = 0; i < result1.getCount(); i++) {
					// ȡ��result�е��ż��𡢿��ҡ�ҽ��
					if (adm_type0.equals(result1.getValue("ADM_TYPE", i))
							&& dept_code0.equals(result1.getValue("DEPT_CODE", i))
							&& dr_code0.equals(result1.getValue("DR_CODE", i))) {
						// ȡ��result1 �е�RX_ANT_NUMֵ
						result.setData("RX_ANT_NUM", j, result1.getDouble("RX_ANT_NUM", i));
						result.setData("RX_ANTI_RATE", j, StringTool.round(result1.getDouble("RX_ANT_NUM", i)/result.getDouble("RX_NUM",j)*100, 2)+"%");
						break;
					}
				}
			}
			if (result.getCount() < 0) {
				messageBox("��������");
				TParm resultparm = new TParm();
				this.table_oe.setParmValue(resultparm);
				return;
			} else {
				TParm resultParm = new TParm();// ����д�������
				double count_tot = 0;// С����������
				double count_anti = 0;// С����ʹ�ÿ����ص�����
				double all_count = 0;// �ܼ���������
				double all_anti_count = 0;// �ܼ���ʹ�ÿ����ص�����
				double count_pha=0;//С������ȡҩ����
				double all_pha_coount=0;//�ܼ���ʹ��ҩƷ����
				adm_type0 =result.getValue("ADM_TYPE", 0);
				dept_code0 =result.getValue("DEPT_CODE", 0);
				//dr_code0 = result.getValue("DR_CODE", 0);
				String regionCode =result.getValue("REGION_CODE", 0);
				for (int i = 0; i < result.getCount(); i++) {
					if (adm_type0.equals(result.getValue("ADM_TYPE", i))
							&&dept_code0.equals(result.getValue("DEPT_CODE", i))) {
						count_tot+=result.getDouble("RX_NUM", i);
						count_pha+=result.getDouble("PHA_NUM", i);
						count_anti+=result.getDouble("RX_ANT_NUM", i);
						getParmValue(resultParm, regionCode, dept_code0, adm_type0, result.getValue("DR_CODE", i), result.getDouble("RX_NUM", i), 
								result.getDouble("RX_ANT_NUM", i), result.getValue("RX_ANTI_RATE",i),result.getDouble("PHA_NUM", i));
					}else{
						String antiRate="0.0%";
						if (count_tot!=0) {
							antiRate=StringTool.round(count_anti/count_tot*100,2)+"%";
						}
						getParmValue(resultParm, "С��:", dept_code0, adm_type0, "", count_tot, count_anti,
								antiRate,count_pha);
						adm_type0=result.getValue("ADM_TYPE", i);
						dept_code0=result.getValue("DEPT_CODE", i);
						regionCode=result.getValue("REGION_CODE", i);
						getParmValue(resultParm, regionCode, dept_code0, adm_type0, result.getValue("DR_CODE", i), result.getDouble("RX_NUM", i), 
								result.getDouble("RX_ANT_NUM", i), result.getValue("RX_ANTI_RATE",i),result.getDouble("PHA_NUM", i));
						count_tot=0;
						count_anti=0;
						count_pha=0;
					}
					all_count += result.getDouble("RX_NUM", i);
					all_pha_coount += result.getDouble("PHA_NUM", i);
					all_anti_count += result.getDouble("RX_ANT_NUM", i);
					if (i==result.getCount()-1) {
						getParmValue(resultParm, "С��:", dept_code0, adm_type0, "", count_tot, count_anti,
								StringTool.round(count_anti/count_tot*100,2)+"%",count_pha);
					}
				}
				getParmValue(resultParm, "�ܼ�:", "", "", "", all_count, all_anti_count,
						StringTool.round(all_anti_count/all_count*100,2)+"%",all_pha_coount);
				resultParm.setCount(resultParm.getCount("REGION_CODE"));
				this.table_oes.setParmValue(resultParm);// Ϊ���ֵ
			}
		}

	}
	private void getParmValue(TParm resultParm,String regionCode,String deptCode,
			String admType,String userName,double count_tot,double count_anti,String antiRate,double count_pha){
		resultParm.addData("REGION_CODE",regionCode);
		resultParm.addData("ADM_TYPE", admType);
		resultParm.addData("DEPT_CODE", deptCode);
		resultParm.addData("DR_CODE",userName);
		resultParm.addData("RX_NUM", count_tot);
		resultParm.addData("RX_ANT_NUM",count_anti);
		resultParm.addData("RX_ANTI_RATE",antiRate);
		resultParm.addData("PHA_NUM",count_pha);
	}
	/**
	 * ���Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		try {
			TTabbedPane tabbedPane = ((TTabbedPane) this
					.getComponent("tTabbedPane_2"));
			if (tabbedPane.getSelectedIndex() == 0) {
				if (table_i.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_i,
						"סԺ���߿���ҩ�ﴦ������ͳ��");
			} else if (tabbedPane.getSelectedIndex() == 1) {
				if (table_oe.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oe,
						"�ż��ﻼ�߿���ҩ�ﴦ������ͳ��-����");
			}else{
				if (table_oes.getParmValue().getCount()<=0) {
					messageBox("û�пɵ�������ݣ�");
					return;
				}
				ExportExcelUtil.getInstance().exportExcel(table_oes,
				"�ż��ﻼ�߿���ҩ�ﴦ������ͳ��-����");
			}

		} catch (NullPointerException e) {
			// TODO: handle exception
			messageBox("û�пɵ�������ݣ�");
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
		table_i.setParmValue(new TParm());
		table_oe.setParmValue(new TParm());
		table_oes.setParmValue(new TParm());
		// ��������ؼ���ֵ
		this.clearValue("OPT_USER;ODI_TYPE;DEPT_CODE;DR_CODE;ADM_TYPE;DR_OE_CODE");
		this.setValue("ODI_TYPE", "2");
	}
}
