package com.javahis.ui.udd;

//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.sql.Timestamp; //import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//import jdo.sys.Operator;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat; //import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: Ժ������������ҩ��ʹ������ʹ�ý������
 * </p>
 * 
 * <p>
 * Description: Ժ������������ҩ��ʹ������ʹ�ý������
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
public class UDDAntiUsageRankingControl extends TControl {
	private TTable table;

	// private Compare compare = new Compare();
	// private int sortColumn = -1;
	// private boolean ascending = false;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		this.setValue("ADM_TYPE", '2');// 2:סԺ
		// ��ʼ��Ժ��
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		this.setValue("NOW_NUM", "100");// ����Ĭ�ϵ�����
		this.table = this.getTable("TABLE");
		// Ϊ����Ӽ�������Ϊ������׼����
		// addListener(table);
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
	 * ��ѯ
	 */
	public void onQuery() {
		String dr_code = "";// ��ѯ�������Ƿ���ҽ��
		String dept_code = "";// ��ѯ�������Ƿ����ż�������
		String mro_deptCode="";
		String mro_dr_code="";
		if (this.getValueString("DEPT_CODE") != null
				&& !this.getValueString("DEPT_CODE").equals("")) {
			dept_code = "  AND A.DEPT_CODE = '"
					+ this.getValueString("DEPT_CODE") + "'";
			mro_deptCode= "  AND A.OUT_DEPT = '"
				+ this.getValueString("DEPT_CODE") + "' ";
		}
		if (this.getValueString("DR_CODE") != null
				&& !this.getValueString("DR_CODE").equals("")) {
			dr_code = "  AND A.DR_CODE = '" + this.getValueString("DR_CODE")
					+ "'";
			mro_dr_code = "  AND A.VS_DR_CODE = '" + this.getValueString("DR_CODE")
			+ "' ";
		}
		String ds_flg = "";//��ѯ�������Ƿ��г�Ժ��ҩ
		if (this.getValueString("DS_FLG").equals("N")) {
			ds_flg = "  AND A.DS_FLG = 'N' ";
		}
		String region_code = "";// ��������ѯ����
		String mro_region_code="";
		if (this.getValueString("REGION_CODE") != null
				&& !this.getValueString("REGION_CODE").equals("")) {
			region_code = "  AND B.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "'";
			mro_region_code = "  AND A.REGION_CODE = '"
				+ this.getValueString("REGION_CODE") + "' ";
		}
		// �ж����������
		String order_by = "";
		if (this.getValueString("DDD").equals("Y")) {
			order_by = "  ORDER BY DDD DESC";
		} else if (this.getValueString("TOT_QTY").equals("Y")) {
			order_by = "  ORDER BY TOT_QTY";
		} else if (this.getValue("TOT_AMT").equals("Y")) {
			order_by = "  ORDER BY TOT_ATM";

		} else {
			order_by = "  ORDER BY ORDER_CODE";
		}
		TTextFormat startDateComp = (TTextFormat) this.getComponent("S_DATE");// ��ʼʱ��
		TTextFormat endDateComp = (TTextFormat) this.getComponent("E_DATE");// ����ʱ��
		// TParm parm = new TParm();//�洫��Ĳ���
		String startDate = StringTool.getString((Timestamp) startDateComp
				.getValue(), "yyyyMMddHHmmss");
		String endDate = StringTool.getString((Timestamp) endDateComp
				.getValue(), "yyyyMMddHHmmss");
		TParm result = new TParm();
		String sql = "";
		TParm sumParm = new TParm();
		int days=0;
		if (this.getValueString("ADM_TYPE").equals("1")) {// �ż���
			sql = "SELECT F.REGION_CHN_ABN AS REGION_CODE,A.ORDER_CODE,A.ORDER_DESC,B.SPECIFICATION,E.UNIT_CHN_DESC AS MEDI_UNIT,C.OWN_PRICE,"
					+ "  SUM(A.DOSAGE_QTY) AS TOT_QTY ,C.DDD,SUM(A.AR_AMT) AS TOT_ATM,G.MEDI_QTY ,G.DOSAGE_QTY,H.UNIT_CHN_DESC AS DOSAGE_UNIT"
					+ "  FROM OPD_ORDER A,PHA_BASE B,SYS_FEE C,SYS_UNIT E ,SYS_REGION F,PHA_TRANSUNIT G ,SYS_UNIT H "
					+ "  WHERE A.ORDER_CODE=B.ORDER_CODE AND B. ANTIBIOTIC_CODE IS NOT NULL"
					+ "   AND B.REGION_CODE = F.REGION_CODE AND A.DOSAGE_UNIT = H.UNIT_CODE AND G.ORDER_CODE = A.ORDER_CODE "
					+ region_code
					+ "  AND A.BILL_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "  AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "  AND A.ORDER_CODE = C.ORDER_CODE AND A.CAT1_TYPE = 'PHA' AND B.MEDI_UNIT = E.UNIT_CODE "
					+ dr_code
					+ dept_code
					+ "  GROUP BY A.ORDER_CODE,A.ORDER_DESC,B.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC,C.OWN_PRICE,F.REGION_CHN_ABN,C.DDD,H.UNIT_CHN_DESC,G.MEDI_QTY,G.DOSAGE_QTY   "
					+
					// "  ORDER BY A.ORDER_CODE";
					order_by;

		} else if (this.getValueString("ADM_TYPE").equals("2")) {// סԺ
			sql = "SELECT F.REGION_CHN_ABN AS REGION_CODE,A.ORDER_CODE,B.ORDER_DESC,"
					+ "  H.UNIT_CHN_DESC AS DOSAGE_UNIT,G.MEDI_QTY,G.DOSAGE_QTY,"
					+ "  B.SPECIFICATION,E.UNIT_CHN_DESC AS MEDI_UNIT,C.OWN_PRICE,"
					+ "  SUM(A.DOSAGE_QTY) AS TOT_QTY ,C.DDD ,SUM(A.TOT_AMT) AS TOT_ATM "
					+ "  FROM IBS_ORDD A,PHA_BASE B,SYS_FEE C ,SYS_UNIT E,SYS_REGION F,PHA_TRANSUNIT G ,SYS_UNIT H,ADM_INP D "
					+ "  WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ANTIBIOTIC_CODE IS NOT NULL AND A.ORDER_CODE = G.ORDER_CODE "
					+ "  AND B.DOSAGE_UNIT = H.UNIT_CODE AND A.CASE_NO=D.CASE_NO AND D.DS_DATE IS NOT NULL "
					+ "  AND D.DS_DATE BETWEEN TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ "  AND TO_DATE ('"
					+ endDate
					+ "','yyyymmddhh24miss') "
					+ "  AND B.REGION_CODE = F.REGION_CODE "
					+ region_code
					+ "  AND A.ORDER_CODE = C.ORDER_CODE AND A.CAT1_TYPE = 'PHA' AND B.MEDI_UNIT = E.UNIT_CODE "
					+ dr_code
					+ dept_code
					+ ds_flg
					+ "  GROUP BY A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,H.UNIT_CHN_DESC,"
					+ " E.UNIT_CHN_DESC,C.OWN_PRICE,F.REGION_CHN_ABN,C.DDD,H.UNIT_CHN_DESC,G.MEDI_QTY ,G.DOSAGE_QTY "
					+ order_by;
			// "  ORDER BY A.ORDER_CODE";
//			String admSql = "SELECT SUM(trunc(S.ds_date-S.in_date)) DAYS FROM (SELECT B.ds_date,B.in_date,B.CASE_NO " +
//					"FROM ADM_INP B ,IBS_ORDD A WHERE B.CASE_NO=A.CASE_NO "+region_code+dept_code+dr_code
//					+ " AND B.DS_DATE IS NOT NULL AND B.DS_DATE BETWEEN TO_DATE ('"
//					+ startDate
//					+ "','yyyymmddhh24miss') "
//					+ "  AND TO_DATE ('"
//					+ endDate + "','yyyymmddhh24miss')  GROUP BY B.ds_date,B.in_date,B.CASE_NO)S";
			
			String admSql = "SELECT IN_DATE,OUT_DATE "
					+ "FROM MRO_RECORD A,SYS_DICTIONARY B,SYS_DEPT C,SYS_STATION D "
					+ " WHERE B.GROUP_ID='SYS_SEX' "+mro_region_code+mro_deptCode+mro_dr_code
					+ " AND A.SEX=B.ID  AND A.OUT_DEPT=C.DEPT_CODE(+)"
					+ " AND A.OUT_STATION=D.STATION_CODE(+)  AND a.admchk_flg = 'Y' "
                    + " AND a.diagchk_flg = 'Y' "
                    + " AND a.bilchk_flg = 'Y' "
                    + " AND a.qtychk_flg = 'Y' AND A.OUT_DATE IS NOT NULL and A.OUT_DATE BETWEEN  TO_DATE ('"
					+ startDate
					+ "','yyyymmddhh24miss') "
					+ " AND TO_DATE ('"
					+ endDate + "','yyyymmddhh24miss')";
//			String admSql = "SELECT COUNT (B.CASE_NO), SUM (TRUNC (B.OUT_DATE - B.IN_DATE)) DAYS"    
//                    +" FROM MRO_RECORD B WHERE B.OUT_DATE IS NOT NULL"
//                    + " AND B.OUT_DATE BETWEEN TO_DATE ('"+startDate+"','yyyymmddhh24miss') "
//                 +" AND TO_DATE ('"+endDate+"','yyyymmddhh24miss')"  
//                 +" AND B.ADMCHK_FLG = 'Y' "
//                 +" AND B.DIAGCHK_FLG = 'Y' "
//                 +" AND B.BILCHK_FLG = 'Y' "
//                 +" AND B.QTYCHK_FLG = 'Y' "
//                 +" AND B.REGION_CODE = 'H01'";
//			System.out.println("Ժ������ admSql is������"+admSql);

			sumParm = new TParm(TJDODBTool.getInstance().select(admSql));

			for (int i = 0; i < sumParm.getCount(); i++) {
				int inDays = 0;
				// �жϲ����Ƿ��Ժ
				if (sumParm.getValue("OUT_DATE", i).length() > 0) {
					// ��������Ѿ���Ժ
					// סԺ���� = ��Ժʱ�� - סԺ����
					inDays = StringTool.getDateDiffer(StringTool.getTimestamp(StringTool.getString(sumParm.getTimestamp("OUT_DATE", i), "yyyyMMdd"),"yyyyMMdd"),
							StringTool.getTimestamp(StringTool.getString(sumParm.getTimestamp("IN_DATE", i),"yyyyMMdd"), "yyyyMMdd"));
					// �����Ժ����
					// �����Ժ����Ϊ�� ���Զ���һ
					if (inDays == 0) {
						inDays = 1;
					}
					days+=inDays;
				} 
			}
//			sys
		}
//		System.out.println("Ժ������ sql is������"+sql);
		
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// ����DDD��
		for (int j = 0; j < result.getCount(); j++) {
			double ddd_in = result.getDouble("DDD", j);
			if (ddd_in != 0.0) {
				double ddds = result.getDouble("TOT_QTY", j)
						* result.getDouble("MEDI_QTY", j) / ddd_in;
				result.setData("DDD", j, ddds); 
			}
		}
		if (result.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			table.removeRowAll();
			return;
		}
		// ���Ҫ��ʾ������
		TParm parm = new TParm();
		// Ҫ��ʵ�����ݵ�����
		int rows = this.getValueInt("NOW_NUM");
		int count = result.getCount("ORDER_CODE");
		if (rows <= count) {
			count = rows;
		}
		// �����ܼ�
		double totalAmt = 0.0;// ���
		double ddd = 0.0;// DDD���ܼ�

		// ѭ���ۼ�
		for (int i = 0; i < count; i++) {
			parm.addRowData(result, i);
			// ����������
			double temp = result.getDouble("TOT_ATM", i);
			totalAmt += temp;
			double temp1 = result.getDouble("DDD", i);
			ddd += temp1;
		}
		parm.setData("REGION_CODE", count, "�ܼ�:");
		parm.setData("ORDER_CODE", count, "");
		parm.setData("ORDER_DESC", count, "");
		parm.setData("SPECIFICATION", count, "");
		parm.setData("MEDI_QTY", count, "");
		parm.setData("MEDI_UNIT", count, "");
		parm.setData("OWN_PRICE", count, "");
		parm.setData("DDD", count, ddd);
		parm.setData("TOT_QTY", count, "");
		parm.setData("MEDI_QTY", count, "");
		parm.setData("TOT_ATM", count, totalAmt);
		if (parm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ�����ݣ�");
			TParm resultparm = new TParm();
			this.table.setParmValue(resultparm);
			return;
		} else {

			for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
				parm.setData("NO", i, i + 1);
			}
			table.acceptText();
			this.table.setParmValue(parm);
			if (this.getValueString("ADM_TYPE").equals("2")) {
				this.setValue("AUD", StringTool.round(ddd/ days * 100, 2));
				this.setValue("TOT_NUM", days);
			}else{
				this.setValue("AUD",StringTool.round(ddd*100,2));
//				this.setValue("TOT_NUM", "0");
			}
		}
	}

	/**
	 * ���Excel
	 */
	public void onExport() {

		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		TTable table = null;
		try {
			table = (TTable) callFunction("UI|TABLE|getThis");
			ExportExcelUtil.getInstance().exportExcel(table,
					"Ժ������������ҩ��ʹ������ʹ�ý��ͳ�Ʊ���");

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
		this.setValue("S_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("E_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
		table.removeRowAll();
		this.setValue("DDD", "N");
		this.setValue("TOT_QTY", "N");
		this.setValue("TOT_AMT", "N");
		this.setValue("NOW_NUM", "100");
		this.clearValue("DEPT_CODE;DR_CODE;ADM_TYPE;AUD");
		this.closeWindow();
	}
}
