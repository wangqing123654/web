package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title: ҽ�������걨��ѯ
 * </p>
 * 
 * <p>
 * Description:ҽ�������걨��ѯ
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.02.06
 * @version 1.0
 */
public class INSSuffererReportTool extends TJDODBTool {

	/**
	 * ʵ��
	 */
	public static INSSuffererReportTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSNoticeTool
	 */
	public static INSSuffererReportTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSSuffererReportTool();
		return instanceObject;
	}

	/**
	 * ҽ�������걨��ѯ
	 * 
	 * @param selectCaseNoTparm
	 *            TParm ��ѯ����
	 * @return TParm
	 */
	public TParm selectdata(TParm queryTParm) {
		int searchStatus = Integer
				.parseInt(queryTParm.getValue("searchStatus"));
		String where = "";
		String whereDsDate = "";
		String whereInsStatus = "";
		switch (searchStatus) {
		case 0:// ��Ժ
			break;
		case 1:// ��Ժδ�걨
			where = " and f.upload_date is null";
			break;
		case 2:// ��Ժ���걨
			where = " and f.upload_date is not null";
			break;
		case 3:// �ѳ�Ժ[��Ժ֪ͨ��]��δתҽ����ϸ
			whereDsDate = " and A.ds_date is not null";
			whereInsStatus = " and g.in_status = '0'";
			break;
		case 4:// �ѽ���ҽ������δ�ϴ�
			whereInsStatus = " and g.in_status = '1'";
			break;
		case 5:// �Ѿ�תҽ����ϸ�������ٻز���
			whereDsDate = " and A.ds_date is null";
			whereInsStatus = " and g.in_status = '0'";
			break;
		case 6:// �Ѿ�תҽ����ϸ�������ٻز���

			break;
		}

		String SQL = this
				.getSql(where, whereDsDate, whereInsStatus, queryTParm);
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));

		return result;
	}

	private String getSql(String where, String whereDsDate,
			String whereInsStatus, TParm queryTParm) {
		int searchStatus = Integer
				.parseInt(queryTParm.getValue("searchStatus"));
		// ��Ժ
		String SQLzy = " SELECT a.mr_no, a.case_no, b.pat_name, B.sex_code, TO_CHAR (b.birth_date, 'yyyy/mm/dd') birth_date, s.station_desc, TO_CHAR (a.in_date, 'yyyy/mm/dd') in_date, TO_CHAR (a.ds_date, 'yyyy/mm/dd') ds_date, '' FROM adm_inp a LEFT JOIN sys_patinfo b ON a.mr_no = b.mr_no , sys_dictionary x, sys_bed d, sys_station s, sys_ctz e WHERE a.region_code = '"
				+ queryTParm.getValue("region_code")
				+ "' AND a.ctz1_code = e.ctz_code AND e.nhi_ctz_flg = 'Y' AND SUBSTR (e.ctz_code, 0, 1) = 1 AND a.bed_no = d.bed_no AND d.station_code = '"
				+ queryTParm.getValue("endemicArea")
				+ "' AND d.station_code = s.station_code AND x.GROUP_ID = 'sys_sex' and A.ds_date is null";
		// ��Ժδ�걨/���걨
		String SQLcysp = "Select  A.mr_no,A.case_no,B.pat_name,B.sex_code,to_char(B.birth_date,'yyyy/mm/dd') birth_date,s.station_desc, to_char(A.in_date,'yyyy/mm/dd') in_date,to_char(A.ds_date,'yyyy/mm/dd') ds_date,to_char(f.upload_date,'yyyy/mm/dd') upload_date from ADM_INP A left join SYS_PATINFO B on A.mr_no = B.mr_no ,sys_dictionary  X, SYS_BED D, SYS_STATION S,INS_IBS f,SYS_CTZ E where A.region_code = '"
				+ queryTParm.getValue("region_code")
				+ "'  AND  E.NHI_CTZ_FLG = 'Y' AND SUBSTR (e.ctz_code, 0, 1) = 1 and A.ds_date is not null and A.bill_date  between  to_date('"
				+ queryTParm.getValue("startDate")
				+ "','yyyy/mm/dd')  and  to_date('"
				+ queryTParm.getValue("endDate")
				+ "','yyyy/mm/dd') and f.upload_date is null "
				+ where
				+ " and f.case_no=a.case_no and A.BED_NO=D.BED_NO and D.Station_code='"
				+ queryTParm.getValue("endemicArea")
				+ "' and D.Station_code=S.Station_code  AND x.GROUP_ID = 'sys_sex'";
		// �ѳ�Ժ
		String SQLcycy = "Select  A.mr_no,A.case_no,B.pat_name,B.sex_code,to_char(B.birth_date,'yyyy/mm/dd') birth_date,s.station_desc, to_char(A.in_date,'yyyy/mm/dd') in_date,to_char(A.ds_date,'yyyy/mm/dd') ds_date,'' from ADM_INP A left join SYS_PATINFO B on A.mr_no = B.mr_no ,sys_dictionary  X, SYS_BED D, SYS_STATION S,ins_adm_confirm g,SYS_CTZ E where A.region_code = '"
				+ queryTParm.getValue("region_code")
				+ "'    AND  E.NHI_CTZ_FLG = 'Y' AND SUBSTR (e.ctz_code, 0, 1) = 1 and A.ds_date is not null "
				+ whereDsDate
				+ " and A.bill_date  between  to_date('"
				+ queryTParm.getValue("startDate")
				+ "','yyyy/mm/dd')  and  to_date('"
				+ queryTParm.getValue("endDate")
				+ "','yyyy/mm/dd') and g.case_no=a.case_no "
				+ whereInsStatus
				+ " and g.in_status = '0' and A.BED_NO=D.BED_NO and D.Station_code='"
				+ queryTParm.getValue("endemicArea")
				+ "' and D.Station_code=S.Station_code  AND x.GROUP_ID = 'sys_sex'";
		// ���ϴ���δ��ӡסԺ�վ�
		String SQLcysc = "SELECT a.mr_no, a.case_no, b.pat_name, B.sex_code, TO_CHAR (b.birth_date, 'yyyy/mm/dd') birth_date, s.station_desc, TO_CHAR (a.in_date, 'yyyy/mm/dd') in_date, TO_CHAR (a.ds_date, 'yyyy/mm/dd') ds_date, TO_CHAR (f.upload_date, 'yyyy/mm/dd')upload_date FROM adm_inp a LEFT JOIN sys_patinfo b ON a.mr_no = b.mr_no , sys_dictionary x, sys_bed d, sys_station s, ins_ibs f, ins_adm_confirm g, sys_ctz e WHERE a.region_code = '"
				+ queryTParm.getValue("region_code")
				+ "'  AND a.ctz1_code = e.ctz_code AND e.nhi_ctz_flg = 'Y' AND SUBSTR (e.ctz_code, 0, 1) = 1 AND a.bill_date BETWEEN TO_DATE ('"
				+ queryTParm.getValue("startDate")
				+ "','yyyy/mm/dd') AND TO_DATE ('"
				+ queryTParm.getValue("endDate")
				+ "','yyyy/mm/dd') AND f.case_no = a.case_no AND f.confirm_no = g.confirm_no AND g.in_status = '2' AND a.bed_no = d.bed_no AND d.station_code = '"
				+ queryTParm.getValue("endemicArea")
				+ "' AND d.station_code = s.station_code AND x.GROUP_ID = 'sys_sex'";
		switch (searchStatus) {
		case 0:// ��Ժ
			return SQLzy;
		case 1:
		case 2:
			return SQLcysp;
		case 3:
		case 4:
		case 5:
			return SQLcycy;
		case 6:// ��Ժ
			return SQLcysc;
		}
		// System.out.println(SQL);
		return null;
	}
}
