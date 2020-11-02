package com.javahis.ui.udd;

import java.math.BigDecimal;
import java.sql.Timestamp;

import jdo.sys.Operator;
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
 * Title: ����ҩ��ʹ�ò���΢����걾�ͼ���ϸͳ��
 * </p>
 * 
 * <p>
 * Description: ����ҩ��ʹ�ò���΢����걾�ͼ���ϸͳ��
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author pangben 2013.11.18
 * @version
 */
public class UDDRateStatisticsDetailControl extends TControl{
	private TTable table_z;
	private TTable table_m;

	public UDDRateStatisticsDetailControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */

	public void onInit() {
		initPage();
		this.onClear();
	}

	private void initPage() {
		table_z = (TTable) this.getComponent("TABLE_Z");
		table_m = (TTable) this.getComponent("TABLE_M");

		String now = StringTool.getString(SystemTool.getInstance().getDate(),
				"yyyyMMdd");
		this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
				"yyyyMMddHHmmss"));// ��ʼʱ��
		this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
				"yyyyMMddHHmmss"));// ����ʱ��
		// ����������
		// this.setValue("USER_ID", "000623a");
		this.setValue("USER_NAME", Operator.getID());
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("PATIENT_STATE", "2");
		this.callFunction("UI|PATIENT_STATE|setEnabled", false);
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		if (this.getValueString("START_DATE").length() == 0) {
			messageBox("��ʼʱ�䲻��ȷ!");
			return;
		}
		if (this.getValueString("END_DATE").length() == 0) {
			messageBox("����ʱ�䲻��ȷ!");
			return;
		}
		String regionCode = getValueString("REGION_CODE"); // ����
		if (regionCode.length() == 0) {
			messageBox("������Ϊ��!");
			return;
		}
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMddHHmmss");
		String patientState = getValueString("PATIENT_STATE"); // סԺ״̬
		String deptCode = getValueString("DEPT_CODE"); // ����
		String drCode1 = getValueString("DR_CODE1"); // ҽ��-סԺҳǩѡ��
		String drCode2 = getValueString("DR_CODE2"); // ҽ��-�ż���ҳǩѡ��
		String caseCode = getValueString("CASE_CODE"); // �ż������

		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() == 0) { // ҳǩһ��סԺGRID
			getQueryParm1(startTime, endTime, patientState, deptCode, drCode1);
		}
		if (tab.getSelectedIndex() == 1) { // ҳǩ�����ż���
			getQueryParm2(startTime, endTime, drCode2, caseCode, deptCode);
		}
	}
	private void getShowParm(TParm sumParm ,String regionCode,String dsType,String statis){
		sumParm.addData("IN_STATION_CODE", regionCode);
		sumParm.addData("DS_TYPE", dsType);
		sumParm.addData("DEPT_CHN_DESC", "");
		sumParm.addData("IN_DATE", "");
		sumParm.addData("DEPT_CHN_DESC", "");
		sumParm.addData("IPD_NO", "");
		sumParm.addData("CASE_NO", "");
		sumParm.addData("USER_NAME", "");
		sumParm.addData("ORDER_CODE", "");
		sumParm.addData("STATION_DESC", "");
		sumParm.addData("MR_NO", "");
		sumParm.addData("ORDER_DESC", statis);
	}
	/**
	 * ��ѯ��һ��ҳǩ����
	 */
	public void getQueryParm1(String startTime, String endTime,
			String patientState, String deptCode, String drCode1) {
		// *********סԺ -��ѯΪ01������--����ѯ���ݷ�װ��returnParm��*********
		// *********סԺ -��ѯΪ01������--����ѯ���ݷ�װ��returnParm��*********
		String sumSql="SELECT A.REGION_CODE,D.REGION_CHN_ABN AS IN_STATION_CODE,A.MR_NO,E.PAT_NAME,CASE WHEN A.DS_DATE IS NULL THEN  '��Ժ' ELSE  '��Ժ' END DS_TYPE," +
				"A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO,F.DEPT_CHN_DESC,A.IN_DATE,A.IPD_NO,G.STATION_DESC,H.USER_NAME,C.ORDER_CODE,C.ORDER_DESC " +
				" FROM ADM_INP A, ODI_ORDER B,SYS_FEE C,SYS_REGION D,SYS_PATINFO E, SYS_DEPT F,SYS_STATION G,SYS_OPERATOR H," +
				"(SELECT A.CASE_NO FROM  ADM_INP A, ODI_ORDER B, SYS_FEE C ,SYS_DEPT D, SYS_OPERATOR E, SYS_REGION Z  " +
				"WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.REGION_CODE=Z.REGION_CODE"
						+ " AND A.VS_DR_CODE = E.USER_ID AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY='02' AND B.ANTIBIOTIC_CODE IS NOT NULL "
						+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DS_DATE BETWEEN TO_DATE('"
						+ startTime
						+ "', 'YYYYMMDDHH24MISS') AND"
						+ " TO_DATE('"
						+ endTime
						+ "', 'YYYYMMDDHH24MISS')";
		if ("1".equals(patientState)) {// ��Ժ
			//sql1_1.append(" AND A.DS_DATE IS NULL");
			sumSql+=" AND A.DS_DATE IS NULL";
		} else if ("2".equals(patientState)) {// ��Ժ
			//sql1_1.append(" AND A.DS_DATE IS NOT NULL");
			sumSql+=" AND A.DS_DATE IS NOT NULL";
		}
		
		if (deptCode.toString().length() > 0) {// ����
			sumSql+=" AND D.DEPT_CODE = '" + deptCode + "'";
		}
		if (drCode1.toString().length() > 0) {// ҽ��
			sumSql+=" AND A.VS_DR_CODE = '" + drCode1 + "'";
		}
		// ȥ�� returnParm2����ͬ���������
		sumSql+=" GROUP BY A.CASE_NO ) S WHERE A.CASE_NO=S.CASE_NO AND A.CASE_NO=B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE" +
				" AND A.REGION_CODE=D.REGION_CODE AND A.MR_NO=E.MR_NO AND A.DEPT_CODE=F.DEPT_CODE AND A.STATION_CODE=G.STATION_CODE " +
				" AND A.VS_DR_CODE = H.USER_ID AND C.ORD_SUPERVISION = '01' " +
				" GROUP BY A.MR_NO,E.PAT_NAME,A.DS_DATE,A.REGION_CODE,D.REGION_CHN_ABN,A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO,F.DEPT_CHN_DESC,A.IN_DATE,A.IPD_NO,G.STATION_DESC,H.USER_NAME,C.ORDER_CODE,C.ORDER_DESC "+
		" ORDER BY A.REGION_CODE,DS_TYPE, A.DEPT_CODE, A.VS_DR_CODE,A.CASE_NO";
		TParm returnParm1 = new TParm(TJDODBTool.getInstance().select(sumSql));
		// *********סԺ-�����סԺ���еĲ������ݲ���ͬ������*********
		
		// ȥ�� returnParm2����ͬ���������
		if (returnParm1.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table_z.setParmValue(new TParm());
			return;
		}
		int sum=0;
		String caseNo=returnParm1.getValue("CASE_NO",0);
		for (int i = 0; i < returnParm1.getCount(); i++) {
			if (i==0) {
				sum++;
			}
			if (caseNo.equals(returnParm1.getValue("CASE_NO",i))) {
				continue;
			}else{
				sum++;
				caseNo=returnParm1.getValue("CASE_NO",i);
			}
		}
		getShowParm(returnParm1, "�ܼ�", "��Ժ", sum+"");
//		sumParm.setCount(sumParm.getCount("IN_STATION_CODE"));
		this.table_z.setParmValue(returnParm1);
	}

	/**
	 * ��ѯ�ڶ���ҳǩ����
	 */
	public void getQueryParm2(String startTime, String endTime, String drCode2,
			String caseCode, String deptCode) {

		// *********sql2_2-����-����ҩ���ͼ�����*********
		StringBuffer sql2_2 = new StringBuffer();

		sql2_2.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE, A.CASE_NO,"
						+ " DECODE(A.ADM_TYPE, 'O', '����', 'E', '����') ADM_TYPE,DECODE(A.ADM_TYPE, 'O',A.ADM_DATE, 'E', A.REG_DATE) AS ADM_DATE, "
						+ " D.USER_NAME,D.USER_ID,A.MR_NO,G.PAT_NAME,C.ORDER_CODE,C.ORDER_DESC "
						+ " FROM REG_PATADM A, OPD_ORDER B, SYS_FEE C,SYS_OPERATOR D,SYS_PATINFO G, SYS_REGION Z"
						+ " WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND A.REGION_CODE=Z.REGION_CODE "
						+ " AND A.DR_CODE = D.USER_ID AND A.MR_NO=G.MR_NO "
						+ " AND A.REGCAN_USER IS NULL AND C.ORD_SUPERVISION = '01'"
						+ " AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND B.CAT1_TYPE = 'LIS' AND B.ORDER_CODE = B.ORDERSET_CODE"
						+ " AND B.SETMAIN_FLG = 'Y' AND B.BILL_FLG = 'Y'");
		sql2_2.append(" AND (A.ADM_DATE BETWEEN TO_DATE('" + startTime
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
				+ "', 'YYYYMMDDHH24MISS'))");
		if ("O".equals(caseCode)) {// ����-�ж�����Ϊ ADM_DATE
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		} else if ("E".equals(caseCode)) {// ����-�ж�����Ϊ REG_DATE
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		}
		if (drCode2.toString().length() > 0) {// ҽ��
			sql2_2.append(" and A.DR_CODE = '" + drCode2 + "'");
		}
//		if (deptCode.toString().length() > 0) {// ����
//			sql2_2.append(" AND A.DEPT_CODE = '" + deptCode + "'");
//		}
		sql2_2
				.append(" GROUP BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.USER_NAME,D.USER_ID,A.ADM_DATE,A.REG_DATE,A.CASE_NO,C.ORDER_CODE,C.ORDER_DESC,A.MR_NO,G.PAT_NAME ");
		sql2_2
				.append(" ORDER BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.USER_ID,A.CASE_NO ");
		TParm returnParm2 = new TParm(TJDODBTool.getInstance().select(
				sql2_2.toString()));
		if (returnParm2.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (returnParm2.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table_m.removeAll();
			return;
		}
		String caseNo=returnParm2.getValue("CASE_NO",0);
		int sum=0;
		for (int i = 0; i < returnParm2.getCount(); i++) {
//			if (i==0) {
//				sum++;
//			}
			if (caseNo.equals(returnParm2.getValue("CASE_NO",i))) {
				continue;
			}else{
				sum++;
				caseNo=returnParm2.getValue("CASE_NO",i);
			}
		}
		// ��ӡ��ܼơ�
		returnParm2.addData("IN_STATION_CODE", "�ܼ�:");
		returnParm2.addData("ADM_TYPE", "");
		returnParm2.addData("USER_NAME", "");
		returnParm2.addData("ADM_DATE", "");
		returnParm2.addData("CASE_NO", "");
		returnParm2.addData("ORDER_CODE", "");
		returnParm2.addData("ORDER_DESC", sum);
		returnParm2.addData("MR_NO", "");
		returnParm2.addData("PAT_NAME", "");
		returnParm2.setCount(returnParm2.getCount()+1);
		table_m.setParmValue(returnParm2);

	}

	/**
	 * ����EXCEL
	 */
	public void onExport() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		if (tab.getSelectedIndex() == 0) { // ҳǩһ��סԺGRID
			onExport1();
		}
		if (tab.getSelectedIndex() == 1) { // ҳǩ�����ż���
			onExport2();
		}

	}

	public void onExport1() {// סԺ
		TTable table_z = getTable("TABLE_Z");
		if (table_z.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_z,
					"����ҩ��ʹ�ò���΢����걾��ϸͳ��-סԺ");
		} else {
			this.messageBox("û�л������");
			return;
		}
	}

	public void onExport2() {// ����
		TTable table_m = getTable("TABLE_M");
		if (table_m.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_m,
					"����ҩ��ʹ�ò���΢����걾��ϸͳ��-����");
		} else {
			this.messageBox("û�л������");
			return;
		}

	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		TTabbedPane tab = (TTabbedPane) this.getComponent("TABLEPANE");
		TParm clearParm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		// ����ʱ��ؼ���ֵ
		this.setValue("START_DATE", date.toString().substring(0, 10).replace(
				"-", "/")
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		// �������е�����
		if (tab.getSelectedIndex() == 0) {
			table_z.setParmValue(clearParm);
		} else if (tab.getSelectedIndex() == 1) {
			table_m.setParmValue(clearParm);
		}
		// ��������ؼ���ֵ
		this.clearValue("CASE_CODE;DR_CODE2;DR_CODE1;PATIENT_STATE;DEPT_CODE");
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("PATIENT_STATE", "2");
	}
}
