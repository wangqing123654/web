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
 * Title: ����ҩ��ʹ�ò���΢����걾�ͼ���ͳ��
 * </p>
 * 
 * <p>
 * Description: ����ҩ��ʹ�ò���΢����걾�ͼ���ͳ��
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author duzhw 2013.08.06
 * @version
 */

public class UDDRateStatisticsControl extends TControl {

	private TTable table_z;
	private TTable table_m;

	public UDDRateStatisticsControl() {
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
	private void getShowParm(TParm sumParm ,String regionCode,String dsType,String vsDrCode,
			double allNum,String dept_code,double sjNum,String statis){
		sumParm.addData("IN_STATION_CODE", regionCode);
		sumParm.addData("DS_TYPE", dsType);
		sumParm.addData("DEPT_CHN_DESC", dept_code);
		sumParm.addData("USER_NAME", vsDrCode);
		sumParm.addData("ALLNUM", allNum);
		sumParm.addData("SJ_NUM", sjNum);
		sumParm.addData("STATIS", statis);
	}
	/**
	 * ��ѯ��һ��ҳǩ����
	 */
	public void getQueryParm1(String startTime, String endTime,
			String patientState, String deptCode, String drCode1) {
		// *********סԺ -��ѯΪ01������--����ѯ���ݷ�װ��returnParm��*********
		String sumSql="SELECT SS.REGION_CODE ,SS.DEPT_CODE,SS.VS_DR_CODE,SS.DS_TYPE, COUNT(SS.CASE_NO) ALLNUM FROM ( SELECT A.REGION_CODE,CASE WHEN A.DS_DATE IS NULL THEN  '��Ժ' ELSE  '��Ժ' END DS_TYPE," +
				"A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO FROM ADM_INP A, ODI_ORDER B,SYS_FEE C, (SELECT A.CASE_NO FROM  ADM_INP A, ODI_ORDER B, SYS_FEE C ,SYS_DEPT D, SYS_OPERATOR E, SYS_REGION Z  " +
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
		//String sql=" GROUP BY A.CASE_NO ) ";
		// *********סԺ-�����סԺ���еĲ������ݲ���ͬ������*********
		StringBuffer sql1_2 = new StringBuffer();
		sql1_2.append("SELECT S.IN_STATION_CODE,S.DS_TYPE,S.DEPT_CODE,S.DEPT_CHN_DESC, S.VS_DR_CODE,S.REGION_CODE, S.USER_NAME,COUNT(S.CASE_NO) ALLNUM,0 SJ_NUM,'0%' STATIS FROM (")
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE,CASE WHEN A.DS_DATE IS NULL THEN  '��Ժ' ELSE  '��Ժ' END DS_TYPE,"
						+ " A.DEPT_CODE,D.DEPT_CHN_DESC, A.VS_DR_CODE, E.USER_NAME,A.CASE_NO,Z.REGION_CODE "
						+ " FROM ADM_INP A, ODI_ORDER B, SYS_FEE C, SYS_DEPT D, SYS_OPERATOR E, SYS_REGION Z"
						+ " WHERE A.CASE_NO = B.CASE_NO AND A.DEPT_CODE = D.DEPT_CODE AND A.REGION_CODE=Z.REGION_CODE"
						+ " AND A.VS_DR_CODE = E.USER_ID AND B.ORDER_CODE = C.ORDER_CODE AND B.ANTIBIOTIC_WAY='02' AND B.ANTIBIOTIC_CODE IS NOT NULL "
						+ " AND A.CANCEL_FLG <> 'Y' AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DS_DATE BETWEEN TO_DATE('"
						+ startTime
						+ "', 'YYYYMMDDHH24MISS') AND"
						+ " TO_DATE('"
						+ endTime
						+ "', 'YYYYMMDDHH24MISS')");
		if ("1".equals(patientState)) {// ��Ժ
			sql1_2.append(" AND A.DS_DATE IS NULL");
			//sql1_1.append(" AND A.DS_DATE IS NULL");
			sumSql+=" AND A.DS_DATE IS NULL";
		} else if ("2".equals(patientState)) {// ��Ժ
			sql1_2.append(" AND A.DS_DATE IS NOT NULL");
			//sql1_1.append(" AND A.DS_DATE IS NOT NULL");
			sumSql+=" AND A.DS_DATE IS NOT NULL";
		}
		
		if (deptCode.toString().length() > 0) {// ����
			sql1_2.append(" AND D.DEPT_CODE = '" + deptCode + "'");
			sumSql+=" AND D.DEPT_CODE = '" + deptCode + "'";
		}
		if (drCode1.toString().length() > 0) {// ҽ��
			sql1_2.append(" AND A.VS_DR_CODE = '" + drCode1 + "'");
			sumSql+=" AND A.VS_DR_CODE = '" + drCode1 + "'";
		}
		sql1_2.append(" GROUP BY Z.REGION_CHN_ABN, Z.REGION_CODE,A.DS_DATE,A.DEPT_CODE, D.DEPT_CHN_DESC, A.VS_DR_CODE,E.USER_NAME,A.CASE_NO ) S ")
						.append("GROUP BY S.REGION_CODE,S.IN_STATION_CODE,S.DS_TYPE,S.DEPT_CODE,S.DEPT_CHN_DESC, S.VS_DR_CODE, S.USER_NAME ")
				.append(" ORDER BY S.REGION_CODE,S.DS_TYPE,S.DEPT_CODE, S.VS_DR_CODE ");
		TParm returnParm2 = new TParm(TJDODBTool.getInstance().select(
				sql1_2.toString()));
		// ȥ�� returnParm2����ͬ���������
		sumSql+=" GROUP BY A.CASE_NO ) S WHERE A.CASE_NO=S.CASE_NO AND A.CASE_NO=B.CASE_NO AND B.ORDER_CODE=C.ORDER_CODE AND C.ORD_SUPERVISION = '01' " +
				" GROUP BY A.REGION_CODE,A.DS_DATE,A.DEPT_CODE ,A.VS_DR_CODE,A.CASE_NO ) SS " +
		"GROUP BY SS.REGION_CODE, SS.DS_TYPE, SS.DEPT_CODE, SS.VS_DR_CODE"+		
		" ORDER BY SS.REGION_CODE, SS.DS_TYPE, SS.DEPT_CODE, SS.VS_DR_CODE";
		TParm returnParm1 = new TParm(TJDODBTool.getInstance().select(sumSql));
		/**
		 * ͳ�� returnParm2�����ݷŵ���parm�� ��ͬԺ����סԺ״̬�����ҡ�ҽ����CASE_NO ��ͬ ord_supervision
		 * = '01'�ĸ���Ϊ ͳ�ƣ�����������Ϊord_supervision ������
		 * ͳ�ƣ�������ҩ�ﲡ���ͼ�������ord_supervision = '01'������ ���㣺������ҩ���ͼ��� =
		 * ����ҩ�ﲡ���ͼ�����/�������� ��ʾ����Ժ��(IN_STATION_CODE) סԺ״̬(DS_TYPE)
		 * ����DEPT_CHN_DESC(DEPT_CODE) ҽ��USER_NAME(DR_CODE) ������(ALLNUM)
		 * ����ҩ�ﲡ���ͼ�����(SJ_NUM) ����ҩ���ͼ���(STATIS)��
		 */
		String regionCode="";//����
		String dsType="";//״̬
		String dept_code="";//����
		String drCode="";//ҽ��
		if (returnParm2.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table_z.setParmValue(new TParm());
			return;
		} 
	//	String sql1Temp=null;
		//����ҩƷ�ۼƺϲ�
		for (int i = 0; i < returnParm2.getCount(); i++) {
			
			regionCode=returnParm2.getValue("REGION_CODE",i);
			dsType=returnParm2.getValue("DS_TYPE",i);
			dept_code=returnParm2.getValue("DEPT_CODE",i);
			drCode=returnParm2.getValue("VS_DR_CODE",i);
			for (int j = 0; j < returnParm1.getCount(); j++) {
				if (regionCode.equals(returnParm1.getValue("REGION_CODE",j))
						&&dsType.equals(returnParm1.getValue("DS_TYPE",j))
								&&dept_code.equals(returnParm1.getValue("DEPT_CODE",j))&&
										drCode.equals(returnParm1.getValue("VS_DR_CODE",j))) {
					returnParm2.setData("SJ_NUM", i, returnParm1.getInt("ALLNUM",j));
					returnParm2.setData("STATIS", i,StringTool.round(returnParm1.getDouble("ALLNUM",j)/returnParm2.getDouble("ALLNUM",i)*100, 2) +"%");
					break;
				}
			}
		}
		regionCode=returnParm2.getValue("REGION_CODE",0);
		dsType=returnParm2.getValue("DS_TYPE",0);
		dept_code=returnParm2.getValue("DEPT_CODE",0);
		drCode=returnParm2.getValue("VS_DR_CODE",0);
		//String userName="";
		String deptDesc="";
		double sum=0;//С��
		double codeSum=0;//С�ƿ���
		double numSum=0;//�ܼ�
		double codeNumSum=0;//�ܼƿ���
		TParm sumParm=new TParm();
		//�����ۼƺϼ�
		for (int i = 0; i < returnParm2.getCount(); i++) {
			if (regionCode.equals(returnParm2.getValue("REGION_CODE",i))
					&&dsType.equals(returnParm2.getValue("DS_TYPE",i))
							&&dept_code.equals(returnParm2.getValue("DEPT_CODE",i))) {
				getShowParm(sumParm, returnParm2.getValue("IN_STATION_CODE",i), dsType, returnParm2.getValue("USER_NAME",i), 
						returnParm2.getInt("ALLNUM",i),returnParm2.getValue("DEPT_CHN_DESC",i), 
						returnParm2.getInt("SJ_NUM",i), returnParm2.getValue("STATIS",i));
				sum+=returnParm2.getInt("ALLNUM",i);//С��
				codeSum+=returnParm2.getInt("SJ_NUM",i);//����ҩƷС��
				numSum+=returnParm2.getInt("ALLNUM",i);//�ܼ�
				codeNumSum+=returnParm2.getInt("SJ_NUM",i);//����ҩƷ�ܼ�
				//userName=returnParm2.getValue("USER_NAME",i);
				deptDesc=returnParm2.getValue("DEPT_CHN_DESC",i);
			}else{
				getShowParm(sumParm, "С��", dsType, "", 
						sum, deptDesc, 
						codeSum,StringTool.round(codeSum/sum *100,2)+"%");
				regionCode=returnParm2.getValue("REGION_CODE",i);
				dsType=returnParm2.getValue("DS_TYPE",i);
				dept_code=returnParm2.getValue("DEPT_CODE",i);
				drCode=returnParm2.getValue("VS_DR_CODE",i);
				//userName=returnParm2.getValue("USER_NAME",i);
				deptDesc=returnParm2.getValue("DEPT_CHN_DESC",i);
				sum=returnParm2.getInt("ALLNUM",i);//С��
				codeSum=returnParm2.getInt("SJ_NUM",i);//����ҩƷС��
				numSum+=returnParm2.getInt("ALLNUM",i);//�ܼ�
				codeNumSum+=returnParm2.getInt("SJ_NUM",i);//����ҩƷ�ܼ�
				getShowParm(sumParm, returnParm2.getValue("IN_STATION_CODE",i), dsType, returnParm2.getValue("USER_NAME",i), 
						returnParm2.getInt("ALLNUM",i), returnParm2.getValue("DEPT_CHN_DESC",i), 
						returnParm2.getInt("SJ_NUM",i), returnParm2.getValue("STATIS",i));
			}
			if (i==returnParm2.getCount()-1) {
				getShowParm(sumParm, "С��", dsType, "", 
						sum, deptDesc, 
						codeSum,StringTool.round(codeSum/sum *100,2)+"%");
			}
		}
		getShowParm(sumParm, "�ܼ�", "", "", 
				numSum, "", 
				codeNumSum,StringTool.round(codeNumSum/numSum *100,2)+"%");
		sumParm.setCount(sumParm.getCount("IN_STATION_CODE"));
		this.table_z.setParmValue(sumParm);
	}

	/**
	 * ��ѯ�ڶ���ҳǩ����
	 */
	public void getQueryParm2(String startTime, String endTime, String drCode2,
			String caseCode, String deptCode) {

		// *********sql2_1-����-ͳ�������� ���� ҽ��*********
		StringBuffer sql2_1 = new StringBuffer();

		sql2_1
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE, COUNT(DISTINCT(A.CASE_NO)) ALLNUM,"
						+ " DECODE(A.ADM_TYPE, 'O', '����', 'E', '����') ADM_TYPE,"
						+ " C.USER_NAME,C.USER_ID,D.DEPT_CHN_DESC,D.DEPT_CODE"
						+ " FROM REG_PATADM A, OPD_ORDER B, SYS_OPERATOR C, SYS_DEPT D, SYS_REGION Z"
						+ " WHERE REGCAN_USER IS NULL AND A.CASE_NO = B.CASE_NO AND A.REGION_CODE=Z.REGIN_CODE "
						+ " AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND A.DEPT_CODE = D.DEPT_CODE AND A.DR_CODE = C.USER_ID  ");

		if ("O".equals(caseCode)) {// ����-�ж�����Ϊ ADM_DATE
			sql2_1.append(" AND (A.ADM_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_1.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		} else if ("E".equals(caseCode)) {// ����-�ж�����Ϊ REG_DATE
			sql2_1.append(" AND (A.REG_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_1.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		}

		if (drCode2.toString().length() > 0) {// ҽ��
			sql2_1.append(" AND A.DR_CODE = '" + drCode2 + "'");
		}
		if (deptCode.toString().length() > 0) {// ����
			sql2_1.append(" AND A.DEPT_CODE = '" + deptCode + "'");
		}
		sql2_1
				.append(" GROUP BY Z.REGION_CHN_ABN , A.ADM_TYPE,C.USER_NAME, C.USER_ID, D.DEPT_CHN_DESC, D.DEPT_CODE ");
		sql2_1
				.append(" ORDER BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.DEPT_CODE, C.USER_ID ");
		TParm returnParm1 = new TParm(TJDODBTool.getInstance().select(
				sql2_1.toString()));
		// *********sql2_2-����-����ҩ���ͼ�����*********
		StringBuffer sql2_2 = new StringBuffer();

		sql2_2
				.append("SELECT Z.REGION_CHN_ABN AS IN_STATION_CODE, COUNT(DISTINCT(A.CASE_NO)) SJ_NUM,"
						+ " DECODE(A.ADM_TYPE, 'O', '����', 'E', '����') ADM_TYPE,"
						+ " D.USER_NAME,D.USER_ID,B.DEPT_CODE,E.DEPT_CHN_DESC"
						+ " FROM REG_PATADM A, OPD_ORDER B, SYS_FEE C,SYS_OPERATOR D,SYS_DEPT E, SYS_REGION Z"
						+ " WHERE A.CASE_NO = B.CASE_NO AND B.ORDER_CODE = C.ORDER_CODE AND A.REGION_CODE=Z.REGION_CODE "
						+ " AND A.DR_CODE = D.USER_ID AND E.DEPT_CODE = B.DEPT_CODE"
						+ " AND A.REGCAN_USER IS NULL AND C.ORD_SUPERVISION = '01'"
						+ " AND Z.REGION_CODE = '"
						+ this.getValueString("REGION_CODE")
						+ "'"
						+ " AND B.CAT1_TYPE = 'LIS' AND B.ORDER_CODE = B.ORDERSET_CODE"
						+ " AND B.SETMAIN_FLG = 'Y' AND B.BILL_FLG = 'Y'");

		if ("O".equals(caseCode)) {// ����-�ж�����Ϊ ADM_DATE
			sql2_2.append(" AND (A.ADM_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		} else if ("E".equals(caseCode)) {// ����-�ж�����Ϊ REG_DATE
			sql2_2.append(" AND (A.REG_DATE BETWEEN TO_DATE('" + startTime
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + endTime
					+ "', 'YYYYMMDDHH24MISS'))");
			sql2_2.append(" AND A.ADM_TYPE = '" + caseCode + "'");
		}
		if (drCode2.toString().length() > 0) {// ҽ��
			sql2_2.append(" and A.DR_CODE = '" + drCode2 + "'");
		}
		if (deptCode.toString().length() > 0) {// ����
			sql2_2.append(" AND A.DEPT_CODE = '" + deptCode + "'");
		}
		sql2_2
				.append(" GROUP BY Z.REGION_CHN_ABN , A.ADM_TYPE,D.USER_NAME,D.USER_ID,B.DEPT_CODE,E.DEPT_CHN_DESC ");
		sql2_2
				.append(" ORDER BY Z.REGION_CHN_ABN , A.ADM_TYPE,B.DEPT_CODE,D.USER_ID ");
		TParm returnParm2 = new TParm(TJDODBTool.getInstance().select(
				sql2_2.toString()));
		/**
		 * �ϲ�returnParm1��returnParm2 Ϊ��TParm �ϲ���TParm�洢��ʽ�� Ժ��(IN_STATION_CODE)
		 * �ż���(ADM_TYPE) ����DEPT_CHN_DESC(DEPT_CODE) ҽ��USER_NAME(DR_CODE)
		 * ������(ALLNUM) ����ҩ�ﲡ���ͼ�����(SJ_NUM) ����ҩ���ͼ���(STATIS) 1��ѭ������returnParm1
		 */
		// TParm returnParm3 = new TParm();
		for (int j = 0; j < returnParm1.getCount(); j++) {
			// ȡ��returnParm1�е��ż��𡢿��ҡ�ҽ��
			String adm_type1 = returnParm1.getValue("ADM_TYPE", j);
			String dept_code1 = returnParm1.getValue("DEPT_CODE", j);
			String dr_code1 = returnParm1.getValue("USER_ID", j);
			String adm_type2 = "";
			String dept_code2 = "";
			String dr_code2 = "";
			double sj_num = 0.00;
			for (int i = 0; i < returnParm2.getCount(); i++) {
				// ȡ��returnParm2�е��ż��𡢿��ҡ�ҽ��
				adm_type2 = returnParm2.getValue("ADM_TYPE", i);
				dept_code2 = returnParm2.getValue("DEPT_CODE", i);
				dr_code2 = returnParm2.getValue("USER_ID", i);
				if (adm_type1.equals(adm_type2)
						&& dept_code1.equals(dept_code2)
						&& dr_code1.equals(dr_code2)) {
					sj_num = returnParm2.getDouble("SJ_NUM", i);
				}
			}
			returnParm1.setData("IN_STATION_CODE", j, returnParm2.getValue(
					"IN_STATION_CODE", j));
			returnParm1.setData("SJ_NUM", j, sj_num);// �ͼ�����
		}

		// С��
		int all_count = 0;
		int all_sj_count = 0;
		int j = 0;// ��¼���롰С�ơ��͡��ܼơ���parm������
		// ���㡰�ܼơ�
		double tot_sj_count = 0.0;
		double tot_count = 0.0;
		TParm newParm = new TParm();
		for (int i = 0; i < returnParm1.getCount(); i++) {
			double rx_no = returnParm1.getDouble("ALLNUM", i);
			double rx_sj_no = returnParm1.getDouble("SJ_NUM", i);
			// �����ܼ�
			double rx_sj_rate = 0;
			tot_count += rx_no;
			tot_sj_count += rx_sj_no;
			String real_rate = "";
			if (rx_sj_no <= 0) {
				real_rate = rx_sj_rate + "%" + "";
				returnParm1.setData("STATIS", i, real_rate);
			} else {
				rx_sj_rate = rx_sj_no / rx_no;
				BigDecimal bdi = new BigDecimal(rx_sj_rate);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				rx_sj_rate = bdi.multiply(parmMuli).doubleValue();
				real_rate = rx_sj_rate + "%" + "";
				returnParm1.setData("STATIS", i, real_rate);
			}
			String dept_chn_code = returnParm1.getValue("DEPT_CHN_DESC", i);
			String d_code = returnParm1.getValue("DEPT_CODE", i);
			String next_dr_code = returnParm1.getValue("DEPT_CODE", i + 1);
			if (i == 0 || (d_code.equals(next_dr_code))) {
				all_count += rx_no;
				all_sj_count += rx_sj_no;
				newParm.addRowData(returnParm1, i);
				j++;
			} else {
				all_count += rx_no;
				all_sj_count += rx_sj_no;
				newParm.addRowData(returnParm1, i);
				double rate = (double) all_sj_count / (double) all_count;
				BigDecimal bdi1 = new BigDecimal(rate);
				bdi1 = bdi1.setScale(4, 4);
				BigDecimal parmMuli1 = new BigDecimal(100);
				parmMuli1 = parmMuli1.setScale(0);
				rate = bdi1.multiply(parmMuli1).doubleValue();
				real_rate = rate + "%" + "";
				returnParm1.addData("STATIS", real_rate);
				newParm.setData("IN_STATION_CODE", ++j, "С��:");
				newParm.setData("ADM_TYPE", ++j, "");
				newParm.setData("DEPT_CHN_DESC", ++j, dept_chn_code);
				newParm.setData("USER_NAME", ++j, "");
				newParm
						.setData("ALLNUM", ++j, all_count == 0 ? "0"
								: all_count);
				newParm.setData("SJ_NUM", ++j, all_sj_count == 0 ? "0"
						: all_sj_count);
				newParm.setData("STATIS", ++j, real_rate);
				j++;
				all_count = 0;
				all_sj_count = 0;
			}

		}
		// �����ܼ����ͼ���
		String real_rate1 = "0%";
		if (tot_count != 0) {
			double rate3 = tot_sj_count / tot_count;
			BigDecimal bdi1 = new BigDecimal(rate3);
			bdi1 = bdi1.setScale(4, 4);
			BigDecimal parmMuli1 = new BigDecimal(100);
			parmMuli1 = parmMuli1.setScale(0);
			rate3 = bdi1.multiply(parmMuli1).doubleValue();
			real_rate1 = rate3 + "%" + "";
		}

		// ��ӡ��ܼơ�
		newParm.setData("IN_STATION_CODE", j, "�ܼ�:");
		newParm.setData("ADM_TYPE", j, "");
		newParm.setData("DEPT_CHN_DESC", j, "");
		newParm.setData("USER_NAME", j, "");
		newParm.setData("ALLNUM", j, tot_count);
		newParm.setData("SJ_NUM", j, tot_sj_count);
		newParm.setData("STATIS", j, real_rate1);
		if (newParm.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (newParm.getCount() <= 0) {
			this.messageBox("û����Ҫ��ѯ������");
			table_m.removeRowAll();
			return;
		}
		table_m.setParmValue(newParm);

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
					"����ҩ��ʹ�ò���΢����걾�ͼ���ͳ��-סԺ");
		} else {
			this.messageBox("û�л������");
			return;
		}
	}

	public void onExport2() {// ����
		TTable table_m = getTable("TABLE_M");
		if (table_m.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table_m,
					"����ҩ��ʹ�ò���΢����걾�ͼ���ͳ��-����");
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
		this.callFunction("UI|PATIENT_STATE|setEnabled", false);
	}
}
