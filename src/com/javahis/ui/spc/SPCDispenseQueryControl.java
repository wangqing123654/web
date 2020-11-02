package com.javahis.ui.spc;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.spc.SPCSettleAccountsTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: סԺҩƷ����ͳ�Ʊ�
 * </p>
 * 
 * <p>
 * Description: סԺҩƷ����ͳ�Ʊ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>f
 * Company: Javahis
 * </p>
 * 
 * @author
 * @version 1.0
 */
public class SPCDispenseQueryControl extends TControl {

	public TTable table_m;

	public TTable table_d;

	public TTable table_ad;


	// ����
	private BILComparator compare = new BILComparator();

	private boolean ascending = false;
	private int sortColumn = -1;

	public SPCDispenseQueryControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {

		((TTabbedPane) this.getComponent("tTabbedPane_0")).setEnabledAt(2,
				false);

		table_m = this.getTable("TABLE_M");
		table_d = this.getTable("TABLE_D");
		table_ad = this.getTable("TABLE_AD");

		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ========pangben modify 20110511 start Ȩ�����
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));

		addListener(getTTable("TABLE_M"));

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {

		// ���ܲ�ѯ
		if (getPanel("tPanel_3").isShowing()) {
			String start_date = getValueString("START_DATE");
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			String end_date = getValueString("END_DATE");
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);

			String supCode = getValueString("SUP_CODE");
			if(supCode == null || supCode.equals("")){
				this.messageBox("��Ӧ�̲���Ϊ��");
				return ;
			}
			String 	where = "";
			// �ж�ҩƷ����Ƿ�Ϊ�գ�ƴ��sql
			String dispenseWhere = "" ;
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				where += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
				dispenseWhere += " AND B.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' " ;
			}

			String commonSql = getODIDspnmSql(start_date, end_date, where);
			String dispenseSql = getDispenseSql(start_date, end_date, dispenseWhere);
			String returnSql = getODIDspnmReturnSql(start_date, end_date, where);
			// by liyh 20120723 �޸���stock��û�����ռ۸�
			String sql = " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.UNIT_CHN_DESC, "
					+ " SUM(A.DOSAGE_QTY) DOSAGE_QTY,SUM(A.OWN_AMT) OWN_AMT, "
					+ " (SELECT B.LAST_VERIFY_PRICE FROM IND_AGENT B WHERE B.SUP_CODE='"+supCode+"' AND B.ORDER_CODE=A.ORDER_CODE ) AS VERIFY_PRICE, "
					+ " (  SUM (A.DOSAGE_QTY) * (SELECT B.LAST_VERIFY_PRICE FROM IND_AGENT B WHERE B.SUP_CODE='"+supCode+"' AND B.ORDER_CODE=A.ORDER_CODE ) ) AS STOCK_AMT "
					+"  FROM (  "
				    + " SELECT AA.ORDER_CODE,AA.ORDER_DESC,AA.SPECIFICATION,AA.UNIT_CHN_DESC, "
					+ " AA.DOSAGE_QTY,AA.OWN_AMT "
					+ " FROM (  "
					+ commonSql  ;
			if(dispenseSql != null && !dispenseSql.equals("") )
				  sql +=  " UNION ALL " + dispenseSql ;
			sql += " UNION ALL  "+returnSql
					+ "    ORDER BY ORDER_CODE    ) "
					+ "  AA, "
					+ "  PHA_BASE CC ";

			 
			sql += " WHERE  AA.ORDER_CODE=CC.ORDER_CODE  ";// by
			sql += " ) A  GROUP BY A.ORDER_CODE,A.UNIT_CHN_DESC,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION " ;
			sql += " ORDER BY A.ORDER_CODE " ;
			 
			System.out.println("סԺҩƷ���� ����----------sql---------" + sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_m.setParmValue(new TParm());
				return;
			}
			
			TParm newParm = new TParm() ;
			TParm indAgenTParm = SPCSettleAccountsTool.getInstance().onQueryIndStockBySupCode(supCode);
			int count = 1 ;
			double sum_amt = 0;
			double stock_amt = 0;
			for(int i = 0 ; i < parm.getCount() ;i++){
				TParm rowParm = parm.getRow(i);
				
				//��־�Ƿ��ڹ�Ӧ�̶�Ӧ����Ʒ�����true��
				boolean b = false;
				String orderCode = rowParm.getValue("ORDER_CODE") ;
				for(int k = 0 ; k < indAgenTParm.getCount() ; k++ ){
					TParm indRowParm = indAgenTParm.getRow(k) ;
					String agtOrderCode = indRowParm.getValue("ORDER_CODE");
					if(orderCode.equals(agtOrderCode)){
						b = true;
						break ;
					}
				}
				if(b){
					newParm.addData("REGION_CHN_DESC",rowParm.getValue("REGION_CHN_DESC"));
					newParm.addData("ORDER_CODE", rowParm.getValue("ORDER_CODE"));
					newParm.addData("DEPT_CHN_DESC", rowParm.getValue("DEPT_CHN_DESC"));
					newParm.addData("ORDER_DESC", rowParm.getValue("ORDER_DESC"));
					newParm.addData("SPECIFICATION", rowParm.getValue("SPECIFICATION"));
					newParm.addData("DOSAGE_QTY", rowParm.getDouble("DOSAGE_QTY"));
					newParm.addData("UNIT_CHN_DESC", rowParm.getValue("UNIT_CHN_DESC"));
					newParm.addData("OWN_AMT", rowParm.getDouble("OWN_AMT"));
					newParm.addData("STOCK_AMT", rowParm.getDouble("STOCK_AMT"));
					newParm.addData("VERIFY_PRICE", rowParm.getDouble("VERIFY_PRICE"));
					sum_amt += rowParm.getDouble("OWN_AMT");
					stock_amt += rowParm.getDouble("STOCK_AMT");
					count++;
				}
			}
 
			newParm.addData("REGION_CHN_DESC", "�ܼ�:");
			newParm.addData("ORDER_CODE", "");
			newParm.addData("DEPT_CHN_DESC", "");
			newParm.addData("ORDER_DESC", "");
			newParm.addData("SPECIFICATION", (count-1)+"����¼");
			newParm.addData("DOSAGE_QTY", "");
			newParm.addData("UNIT_CHN_DESC", "");
			newParm.addData("OWN_AMT", StringTool.round(sum_amt, 2));
			newParm.addData("STOCK_AMT", StringTool.round(stock_amt, 2));
			table_m.setParmValue(newParm);
			// ***************************************************************************
			// luhai modify 2012-05-07 begin �������ҩƷ�ȼ��Ϳ����صȼ���ѯ���� begin
			// ***************************************************************************

		} else if (getPanel("tPanel_6").isShowing()) {
			// ����ϸ��
			String start_date = getValueString("START_DATE");
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			String end_date = getValueString("END_DATE");
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);
			String sql = " SELECT F.MR_NO,B.PAT_NAME,CASE   M.RX_KIND  WHEN 'ST' THEN '��ʱ' WHEN 'UD' THEN '����' WHEN 'DS' THEN '��Ժ��ҩ' ELSE '��ҩ' END   AS DSPN_KIND, E.FREQ_CHN_DESC,  "
					+ "  A.DOSAGE_QTY, D.UNIT_CHN_DESC,C.ORDER_CODE,C.ORDER_DESC, "
					+ " A.DOSAGE_QTY * C.OWN_PRICE AS OWM_AMT "
					+ " FROM ODI_DSPNM A, SYS_PATINFO B, SYS_FEE_HISTORY C,SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M,PHA_BASE G  "
					+ " WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND C.UNIT_CODE = D.UNIT_CODE AND A.FREQ_CODE = E.FREQ_CODE AND A.ORDER_CODE=G.ORDER_CODE  "
					+ " AND A.CAT1_TYPE = 'PHA' AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  ";
			// �ж�ҩƷ����Ƿ�Ϊ�գ�ƴ��sql
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				sql += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
			}
			// �ж�ҩ��
			if (!"".equals(this.getValueString("ORG_CODE"))) {
				sql += " AND A.EXEC_DEPT_CODE = '" + getValueString("ORG_CODE")
						+ "' ";
			}
			// ����ҩƷ�ȼ�
			if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
				String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE")
						+ "";
				sql += " AND G.CTRLDRUGCLASS_CODE IS NOT NULL ";
				if (!"".equals(ctrlClass) && ctrlClass != null) {
					sql += " AND G.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
				} else {
					sql += " ";
				}
			}
			// �����صȼ�
			if (getCheckBox("ANTIBIOTIC").isSelected()) {
				String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
						+ "";
				sql += " AND G.ANTIBIOTIC_CODE IS NOT NULL";
				if (!"".equals(antiblogticCode) && antiblogticCode != null) {
					sql += " AND G.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
				} else {
					sql += " ";
				}
			}

			sql += " AND A.PHA_DOSAGE_DATE IS NOT NULL "
					+ "  AND A.PHA_DOSAGE_DATE BETWEEN "
					+ " TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+)   "
					+ " UNION ALL "
					+ " SELECT F.MR_NO,B.PAT_NAME,'' AS DSPN_KIND, '' AS FREQ_CHN_DESC,DOSAGE_QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC,"
					+ " C.ORDER_CODE,C.ORDER_DESC, A.OWN_AMT  AS OWN_AMT "
					+ " FROM IBS_ORDD A, SYS_PATINFO B, PHA_BASE C, "
					+ " SYS_UNIT D,ADM_INP F WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO "
					+ " AND A.ORDER_CODE = C.ORDER_CODE ";

			// �ж�ҩƷ����Ƿ�Ϊ�գ�ƴ��sql
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				sql += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
			}
			// �ж�ҩ��
			if (!"".equals(this.getValueString("ORG_CODE"))) {
				sql += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE")
						+ "' ";
			}
			// ����ҩƷ�ȼ�
			if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
				String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE")
						+ "";
				sql += " AND C.CTRLDRUGCLASS_CODE IS NOT NULL ";
				if (!"".equals(ctrlClass) && ctrlClass != null) {
					sql += " AND C.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
				} else {
					sql += " ";
				}
			}
			// �����صȼ�
			if (getCheckBox("ANTIBIOTIC").isSelected()) {
				String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
						+ "";
				sql += " AND C.ANTIBIOTIC_CODE IS NOT NULL";
				if (!"".equals(antiblogticCode) && antiblogticCode != null) {
					sql += " AND C.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
				} else {
					sql += " ";
				}
			}

			sql += " AND C.DOSAGE_UNIT = D.UNIT_CODE "
					+ " AND A.CAT1_TYPE = 'PHA' AND ORDER_NO IS NOT NULL AND DOSAGE_QTY < 0  "
					+ "  AND A.BILL_DATE " + " BETWEEN TO_DATE ('" + start_date
					+ "', 'YYYYMMDDHH24MISS') " + " AND TO_DATE ('" + end_date
					+ "', 'YYYYMMDDHH24MISS')";
			// System.out.println("סԺҩƷ���� ����ϸ��---------sql��" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_ad.removeRowAll();
				return;
			}
			table_ad.setParmValue(result);
		}
		// /lirui 2012-7-5 ��������ϸ����Ϣҳǩ�������������Ʋ�ѯָ����ϸ����Ϣ end
		else {
			this.messageBox("��ѡ��������Ϣ");
		}
		// ҩ���ѯ����ibs_ordd ���в�ѯ end
	}

	public String getDispenseSql(String start_date, String end_date,
			String where) {
		String sql  = "";
		boolean flg = true;
		String orgCode = getValueString("ORG_CODE");
		if(orgCode != null && !orgCode.equals("")) {
			if("040103".equals(orgCode)){
				flg = false;
			}
		}
		if(flg) {
		
			 sql = "SELECT  B.ORDER_CODE, C.ORDER_DESC AS ORDER_DESC,C.SPECIFICATION AS SPECIFICATION,"
				+ "  D.UNIT_CHN_DESC AS UNIT_CHN_DESC,  SUM(B.ACTUAL_QTY) AS DOSAGE_QTY, SUM(B.RETAIL_PRICE) AS OWN_AMT,B.UNIT_CODE "
				+ " FROM IND_DISPENSEM A, IND_DISPENSED B, SYS_FEE C, SYS_UNIT D,PHA_BASE F "
				+ " WHERE A.DISPENSE_NO=B.DISPENSE_NO AND B.ORDER_CODE=C.ORDER_CODE AND B.UNIT_CODE=D.UNIT_CODE "
				+ "    AND B.ORDER_CODE=F.ORDER_CODE  AND ( A.UPDATE_FLG = '3' OR A.UPDATE_FLG = '1' ) AND  A.REQTYPE_CODE IN ('EXM','TEC')  "
				+ " AND A.DISPENSE_DATE BETWEEN TO_DATE('"
				+ start_date
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
				+ end_date
				+ "', 'YYYYMMDDHH24MISS') "
				+ where ;
			 
			
			if(orgCode == null || orgCode.equals("")){
				orgCode = "040103";
				sql += " AND A.TO_ORG_CODE = '"+orgCode+"' ";
			}else {
				sql += " AND A.APP_ORG_CODE = '"+orgCode+"' ";
			}
			
			
			// ����ҩƷ�ȼ�
			if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
				String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
				sql += " AND F.CTRLDRUGCLASS_CODE IS NOT NULL ";
				if (!"".equals(ctrlClass) && ctrlClass != null) {
					sql += " AND F.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
				} else {
					sql += " ";
				}
			}
			// �����صȼ�
			if (getCheckBox("ANTIBIOTIC").isSelected()) {
				String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
						+ "";
				sql += " AND F.ANTIBIOTIC_CODE IS NOT NULL";
				if (!"".equals(antiblogticCode) && antiblogticCode != null) {
					sql += " AND F.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
				} else {
					sql += " ";
				}
			}
		
			sql += " GROUP BY     B.ORDER_CODE,  C.ORDER_DESC  ,  C.SPECIFICATION   ,   D.UNIT_CHN_DESC,B.UNIT_CODE  ";
		}
		return sql;
		
	}
	
	

	public String getODIDspnmSql(String start_date, String end_date,
			String where) {
		String sql = "SELECT  A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC,B.SPECIFICATION AS SPECIFICATION, "
				+ " C.UNIT_CHN_DESC AS UNIT_CHN_DESC,  SUM(A.DOSAGE_QTY) AS DOSAGE_QTY, SUM(A.OWN_AMT) AS OWN_AMT, A.DOSAGE_UNIT AS UNIT_CODE "
				+ " FROM ODI_DSPNM A, SYS_FEE B, SYS_UNIT C  "
				+ " WHERE  "
				+ "  A.ORDER_CODE = B.ORDER_CODE AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "
				+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
				+ start_date
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
				+ end_date
				+ "', 'YYYYMMDDHH24MISS') "
				+ where
				+ " AND A.TAKEMED_ORG='2'   ";
		// �ж�ҩ��
		String orgCode = getValueString("ORG_CODE") ;
		if (!"".equals(orgCode) ){
			if(orgCode.equals("040103")){
				sql += "  AND A.EXEC_DEPT_CODE IN ( '040103','040109') ";
			}else{
				sql += " AND A.EXEC_DEPT_CODE = '" + getValueString("ORG_CODE")
					+ "' ";
			}
		}else {
			sql += "  AND A.EXEC_DEPT_CODE IN ( '040103','040109') ";
		}
		// ����ҩƷ�ȼ�
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// �����صȼ�
		if (getCheckBox("ANTIBIOTIC").isSelected()) {
			String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
					+ "";
			sql += " AND A.ANTIBIOTIC_CODE IS NOT NULL";
			if (!"".equals(antiblogticCode) && antiblogticCode != null) {
				sql += " AND A.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
			} else {
				sql += " ";
			}
		}

		sql += "  GROUP BY   A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,C.UNIT_CHN_DESC,A.DOSAGE_UNIT "
				;
		return sql;
	}
	
	public String getODIDspnmReturnSql(String start_date, String end_date,
			String where) {
		String sql = "SELECT  A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC,B.SPECIFICATION AS SPECIFICATION, "
				+ " C.UNIT_CHN_DESC AS UNIT_CHN_DESC,  SUM(-1 * A.RTN_DOSAGE_QTY) AS DOSAGE_QTY, SUM( A.OWN_PRICE*A.RTN_DOSAGE_QTY*-1 ) AS OWN_AMT, A.DOSAGE_UNIT AS UNIT_CODE "
				+ " FROM ODI_DSPNM A, SYS_FEE B, SYS_UNIT C  "
				+ " WHERE  "
				+ "   A.ORDER_CODE = B.ORDER_CODE AND A.DISPENSE_UNIT = C.UNIT_CODE "
				+ " AND A.DSPN_KIND='RT' "
				+ " AND A.DSPN_DATE BETWEEN TO_DATE('"
				+ start_date
				+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
				+ end_date
				+ "', 'YYYYMMDDHH24MISS') "
				+ where
				+ "   ";
		// �ж�ҩ��
		String orgCode = getValueString("ORG_CODE");
		if (!"".equals(orgCode)) {
			if(orgCode.equals("040103")){
				sql += "  AND A.EXEC_DEPT_CODE IN ( '040103','040109') ";
			}else{
				sql += " AND A.EXEC_DEPT_CODE = '" + getValueString("ORG_CODE")
					+ "' ";
			}
		}else {
			sql += " AND A.EXEC_DEPT_CODE IN ( '040103','040109') ";
		}
		// ����ҩƷ�ȼ�
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// �����صȼ�
		if (getCheckBox("ANTIBIOTIC").isSelected()) {
			String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
					+ "";
			sql += " AND A.ANTIBIOTIC_CODE IS NOT NULL";
			if (!"".equals(antiblogticCode) && antiblogticCode != null) {
				sql += " AND A.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
			} else {
				sql += " ";
			}
		}

		sql += "  GROUP BY  A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,C.UNIT_CHN_DESC,A.DOSAGE_UNIT "
				;
		return sql;
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC";
		this.clearValue(clearStr);
		callFunction("UI|setSysStatus", "" + " : " + "");
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table_m.setParmValue(new TParm());
		table_d.setParmValue(new TParm());
		// (
		// (TTabbedPane)this.getComponent("tTabbedPane_0")).setSelectedIndex(0);
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {

	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (getPanel("tPanel_3").isShowing()) {
			if (table_m.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_m,
					"סԺҩƷ����ͳ�Ʊ�(������Ϣ)");
		} else {
			if (table_d.getRowCount() <= 0) {
				this.messageBox("û�л������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d,
					"סԺҩƷ����ͳ�Ʊ�(ϸ����Ϣ)");
		}
	}
	
	/**
	 * ����XML����ҩ����
	 */
	public void onExportXml(){
		
	}

	/**
	 * �������ҳ
	 */
	public void onChangeTTabbedPane() {

		long starttime = System.currentTimeMillis();
		// LIRUI modify 2012-06-29 ����ibs_ordd ��ѯ
		if (getPanel("tPanel_5").isShowing() && table_m.getSelectedRow() < 0) {
			this.messageBox("��ѡ��������Ϣ");
			((TTabbedPane) this.getComponent("tTabbedPane_0"))
					.setSelectedIndex(0);
			return;
		}
		
		// *************��һϸ����Ϣ*******************
		else if (getPanel("tPanel_5").isShowing()
				&& table_m.getSelectedRow() >= 0) {
			
			String orgCode = getValueString("ORG_CODE"); 
			if(orgCode== null || orgCode.equals("")){
				this.messageBox("��ѡ��ҩ��");
				return ;
			}
			TParm parm = table_m.getParmValue()
					.getRow(table_m.getSelectedRow());
			// ִ�п���
			//String dept_code = getValueString("ORG_CODE"); // by liyh 20120710
			// ҩƷ���
			if(orgCode != null && !orgCode.equals("040103")){
				this.messageBox("û�в�ѯ����");
				table_d.setParmValue(new TParm());
				return ;
			}
			String order_code = parm.getValue("ORDER_CODE");
			String start_date = getValueString("START_DATE");
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			String end_date = getValueString("END_DATE");
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);
			
			String sql =  " SELECT CASE A.DSPN_KIND "
					     +"     WHEN 'ST' THEN '��ʱ' "
					     +"     WHEN 'UD' THEN '����' "
					     +"     WHEN 'DS' THEN '��Ժ��ҩ' "
					     +"     WHEN 'F'  THEN '������' "
					     +"     ELSE '��ҩ' "
					     +"  END "
					     +"     AS DSPN_KIND, "
					     +" D.FREQ_CHN_DESC, "
					     +" A.MR_NO, "
					     +" B.PAT_NAME, "
					     +" A.DOSAGE_QTY, "
					     +" C.UNIT_CHN_DESC, "
					     +" A.OWN_AMT, "
					     +" CASE WHEN A.DOSAGE_UNIT=F.STOCK_UNIT THEN E.CONTRACT_PRICE  ELSE E.LAST_VERIFY_PRICE   END AS VERIFY_PRICE, "
					     +" (CASE WHEN A.DOSAGE_UNIT=F.STOCK_UNIT THEN E.CONTRACT_PRICE  ELSE E.LAST_VERIFY_PRICE   END *A.DOSAGE_QTY ) AS VERIFY_AMT  "
					     +" FROM ODI_DSPNM A, "
					     +"      SYS_PATINFO B, "
					     +"      SYS_UNIT C, "
					     +"      SYS_PHAFREQ D , "
					     +"      IND_AGENT E  , "
					     +"      PHA_BASE F    " 
					     +" WHERE  A.MR_NO=B.MR_NO "	        
					     +"     AND A.DOSAGE_UNIT  = C.UNIT_CODE "
					     +"     AND A.FREQ_CODE = D.FREQ_CODE "
					     +"     AND A.ORDER_CODE=E.ORDER_CODE "
					     +"		AND A.ORDER_CODE=F.ORDER_CODE "
					     +"		AND E.SUP_CODE='"+getValueString("SUP_CODE")+"' "
					     +"		AND A.CAT1_TYPE = 'PHA' "
					     +"		AND A.BILL_FLG ='Y' "
					     +" 	AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('"+start_date+"', "
					     +"                      'YYYYMMDDHH24MISS') "
					     +"        AND TO_DATE ('"+end_date+"', "
					     +"                      'YYYYMMDDHH24MISS') "
					     +" 	AND A.ORDER_CODE = '"+order_code+"' "
					 //    +"     AND A.EXEC_DEPT_CODE='"+orgCode+"' "
					     +"      AND A.TAKEMED_ORG='2'  "
					     +" 	AND A.ORDER_CODE=E.ORDER_CODE "        
					     +" UNION ALL "
					     +"	SELECT '��ҩ'  AS DSPN_KIND, "
					     +" 	'' AS FREQ_CHN_DESC, "
					     +" 	A.MR_NO, "
					     +" 	B.PAT_NAME, "
					     +"		-1 * A.RTN_DOSAGE_QTY AS DOSAGE_QTY , "
					     +" 	C.UNIT_CHN_DESC, "
					     +"  	-1 * A.OWN_PRICE*A.RTN_DOSAGE_QTY AS OWN_AMT, "
					     + " (SELECT B.LAST_VERIFY_PRICE FROM IND_AGENT B WHERE B.SUP_CODE='"+getValueString("SUP_CODE")+"' AND B.ORDER_CODE=A.ORDER_CODE ) AS VERIFY_PRICE, "
						 + " (  A.RTN_DOSAGE_QTY*-1 * (SELECT B.LAST_VERIFY_PRICE FROM IND_AGENT B WHERE B.SUP_CODE='"+getValueString("SUP_CODE")+"' AND B.ORDER_CODE=A.ORDER_CODE ) ) AS VERIFY_AMT "
					     +"  FROM ODI_DSPNM A, "
					     +"  	  SYS_PATINFO B, "
					     +"       SYS_UNIT C, "
					   //  +"       SYS_PHAFREQ D, "
					     +"       IND_AGENT E, "
					     +"       PHA_BASE F      "  
					     +"  WHERE  A.MR_NO=B.MR_NO "     
					     +"      AND A.DISPENSE_UNIT  = C.UNIT_CODE "
					    
					     +" 	 AND A.ORDER_CODE=E.ORDER_CODE "
					     +"      AND A.ORDER_CODE=F.ORDER_CODE "
					     +"      AND E.SUP_CODE='"+getValueString("SUP_CODE")+"' "
					     +"      AND A.CAT1_TYPE = 'PHA' "
					     +"      AND  A.DSPN_KIND='RT'  "
					     +"      AND A.DSPN_DATE BETWEEN TO_DATE ('"+start_date+"', "
					     +"                      'YYYYMMDDHH24MISS') "
					     +"      AND TO_DATE ('"+end_date+"', "
	                     +"                      'YYYYMMDDHH24MISS') "
	                     +"      AND A.ORDER_CODE = '"+order_code+"' ";
	                  //   +"      AND A.EXEC_DEPT_CODE='"+orgCode+"' ";
			
			//System.out.println("��ϸ-------------sql--" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_d.setParmValue(new TParm());
				return;
			}
			
			double sum_amt = 0;
			double verifyAmt = 0;
			double dosageQty =  0 ;
			for (int i = 0; i < result.getCount("OWN_AMT"); i++) {
				sum_amt += result.getDouble("OWN_AMT", i);
				verifyAmt += result.getDouble("VERIFY_AMT", i);
				dosageQty += result.getDouble("DOSAGE_QTY",i);
			}
			
			 
			result.addData("DSPN_KIND", "�ܼ�:");
			result.addData("FREQ_CHN_DESC", "");
			result.addData("MR_NO", "");
			result.addData("PAT_NAME", "");
			result.addData("DOSAGE_QTY", dosageQty);
			result.addData("UNIT_CHN_DESC", "");
			result.addData("OWN_AMT", StringTool.round(sum_amt, 2));
			result.addData("VERIFY_AMT", StringTool.round(verifyAmt, 2));
			result.addData("VERIFY_PRICE", "");
			table_d.setParmValue(result);
		}
		// lirui 2012-07-05 ��������ϸ����Ϣҳǩ�� ����������Ϣ��ѯ���е�ϸ����Ϣ strart
		else if (getPanel("tPanel_6").isShowing()) {

			String start_date = getValueString("START_DATE");
			start_date = start_date.substring(0, 4)
					+ start_date.substring(5, 7) + start_date.substring(8, 10)
					+ start_date.substring(11, 13)
					+ start_date.substring(14, 16)
					+ start_date.substring(17, 19);
			String end_date = getValueString("END_DATE");
			end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
					+ end_date.substring(8, 10) + end_date.substring(11, 13)
					+ end_date.substring(14, 16) + end_date.substring(17, 19);
			String sql = " SELECT F.MR_NO,B.PAT_NAME,CASE   M.RX_KIND  WHEN 'ST' THEN '��ʱ' WHEN 'UD' THEN '����' WHEN 'DS' THEN '��Ժ��ҩ' ELSE '��ҩ' END   AS DSPN_KIND, E.FREQ_CHN_DESC,  "
					+ "  A.DOSAGE_QTY, D.UNIT_CHN_DESC, "
					+ " A.DOSAGE_QTY * C.OWN_PRICE AS OWM_AMT,C.ORDER_CODE,C.ORDER_DESC  "
					+ " FROM ODI_DSPNM A, SYS_PATINFO B, SYS_FEE_HISTORY C, "
					+ " SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M  "
					+ " WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND C.UNIT_CODE = D.UNIT_CODE AND A.FREQ_CODE = E.FREQ_CODE"
					+ " AND A.CAT1_TYPE = 'PHA' AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  "
					+ " AND A.CASHIER_DATE IS NOT NULL "
					+ "  AND A.CASHIER_DATE BETWEEN "
					+ " TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+)   "
					+ " UNION ALL "
					+ " SELECT F.MR_NO,B.PAT_NAME,'��ҩ' AS DSPN_KIND, '' AS FREQ_CHN_DESC, DOSAGE_QTY  "
					+ " AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.OWN_AMT AS OWN_AMT,C.ORDER_CODE,C.ORDER_DESC "
					+ " FROM ODI_DSPNM A, SYS_PATINFO B, PHA_BASE C, "
					+ " SYS_UNIT D,ADM_INP F WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO "
					+ " AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND C.DOSAGE_UNIT = D.UNIT_CODE "
					+ " AND A.CAT1_TYPE = 'PHA' AND ORDER_NO IS NOT NULL AND DOSAGE_QTY < 0  "
					+ "  AND A.CASHIER_DATE "
					+ " BETWEEN TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS')";
			//System.out.println("ϸ��sql-all-2--" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_ad.removeRowAll();
				return;
			}
			long endtime = System.currentTimeMillis();
			// System.out.println("------���ҳ����---------time: "+(endtime-starttime));
			table_ad.setParmValue(result);
		}
		// lirui 2012-07-05 ��������ϸ����Ϣҳǩ�� ����������Ϣ��ѯ���е�ϸ����Ϣ end
	}

	// lirui 20120606 �����ѡ�� start
	/**
	 * �����ѡ��
	 */
	public void onChangeCheckBox() {
		// ����ҩƷ�ȼ�
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			getComBox("CTRLDRUGCLASS_CODE").setEnabled(true);

		} else {
			getComBox("CTRLDRUGCLASS_CODE").setEnabled(false);
			this.clearValue("CTRLDRUGCLASS_CODE");
		}

		// �����صȼ�
		if (getCheckBox("ANTIBIOTIC").isSelected()) {
			getComBox("ANTIBIOTIC_CODE").setEnabled(true);
		} else {
			getComBox("ANTIBIOTIC_CODE").setEnabled(false);
			this.clearValue("ANTIBIOTIC_CODE");
		}
	}

	/**
	 * �õ�CheckBox����
	 * 
	 * @return TCheckBox
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * �õ�combox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * ����¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked() {
		// //�õ�ѡ�����
		// int rowId=table_m.getSelectedRow();
		// this.messageBox("ORDER_CODE----+"+ table_m.getItemData(rowId,
		// "ORDER_CODDE"));
		// callFunction("UI|setSysStatus", table_m.getItemData(rowId,
		// "ORDER_CODDE")+ ":" + table_m.getItemData(rowId, "ORDER_DESC"));

		TParm parm = table_m.getParmValue().getRow(table_m.getSelectedRow());
		// System.out.println("parm----"+parm);
		callFunction("UI|setSysStatus", parm.getValue("ORDER_CODE") + " : "
				+ parm.getValue("ORDER_DESC"));
	}

	// lirui 20120606 �����ѡ�� end

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
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
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�TPanel����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TPanel getPanel(String tagName) {
		return (TPanel) getComponent(tagName);
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 * @author liyh
	 * @date 20120710
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 * @author liyh
	 * @date 20120710
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// TParm tableData = getTTable("TABLE_M").getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableData);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				/*
				 * System.out.println("----+i:"+i);
				 * System.out.println("----+i:"+j);
				 */
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				// ==========modify-begin (by wanglong 20120716)===============
				TParm tableData = getTTable("TABLE_M").getParmValue();
				// TParm tableData = getTTable("TABLE_M").getShowParmValue();
				// System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");
				tableData.removeData("Data", "DEPT_CHN_DESC");
				tableData.addData("ORDER_CODE", "");
				// ���ܼơ��� ����������
				TParm totRowParm = new TParm();// ��¼���ܼơ���
				totRowParm.addRowData(table.getShowParmValue(), tableData
						.getCount());
				int rowCount = tableData.getCount();// ���ݵ�������������С���к��ܼ��У�
				tableData.removeRow(tableData.getCount());// ȥ�����һ��(�ܼ���)
				// ==========modify-end========================================
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable("TABLE_M").getParmMap(
						sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				// ==========modify-begin (by wanglong 20120716)===============
				// cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm = new TParm();// ��¼���ս��
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// �����м�����
				lastResultParm.addRowData(totRowParm, 0);// �����ܼ���
				lastResultParm.setCount(rowCount);
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * ����������
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 * @author liyh
	 * @date 20120710
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 * @author liyh
	 * @date 20120710
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectoryת��param
	 * 
	 * @author liyh
	 * @date 20120710
	 */
	// ==========modify-begin (by wanglong 20120716)===============
	// private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
	// String columnNames) {
	private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ==========modify-end========================================
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		// ==========modify-begin (by wanglong 20120716)===============
		// getTTable("TABLE").setParmValue(parmTable);
		return parmTable;
		// ==========modify-end========================================
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * ����orgCode��ѯ��Ӧ���ӿ�
	 * @param orgCode
	 * @return
	 */
	public TParm getIndOrg(String orgCode){
		String sql = " SELECT ORG_CODE ,SUP_ORG_CODE " +
				     " FROM IND_ORG " +
				     " WHERE SUP_ORG_CODE='"+orgCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * �õ������SQL
	 * @param start_date
	 * @param end_date
	 * @param isDrug   1:��ҩ  2���龫  ��ȫ��
	 * @return
	 */
	public String getOdiDspnmSql(String start_date,String end_date,String isDrug){
	 
		String sql = "    SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
				 	  "	  FROM ODI_DSPNM A  "+
				      "	  WHERE A.PHA_DOSAGE_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS') "+
				      "         AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') " ;
		// ����ҩƷ�ȼ�
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// �����صȼ�
		if (getCheckBox("ANTIBIOTIC").isSelected()) {
			String antiblogticCode = this.getValueString("ANTIBIOTIC_CODE")
					+ "";
			sql += " AND A.ANTIBIOTIC_CODE IS NOT NULL";
			if (!"".equals(antiblogticCode) && antiblogticCode != null) {
				sql += " AND A.ANTIBIOTIC_CODE='" + antiblogticCode + "' ";
			} else {
				sql += " ";
			}
		}

	    return sql;
	}
	
	/**
	 * �������������SQL (��ҩ���龫)
	 * @param startDate
	 * @param endDate
	 * @param isDrug   1:��ҩ  2���龫  ��ȫ��
	 * @param parm     �Ӳ��ż���
	 * @return
	 */
	public String getDispenseDSql(String startDate,String endDate,String isDrug,TParm parm){
		//�ӿ����
		String deptStr = "";
		if(parm.getCount("ORG_CODE") > 0){
			int count = parm.getCount("ORG_CODE");
			for(int i = 0 ; i < count; i++ ){
				TParm rowParm = (TParm)parm.getRow(i);
				if(i < count-1 ){
					deptStr += " '"+rowParm.getValue("ORG_CODE")+"' , ";
				}else {
					deptStr += " '"+rowParm.getValue("ORG_CODE")+"' " ;
				}
			}
		}
		String sql = "";
		sql = "   SELECT B.ORDER_CODE,CASE WHEN B.UNIT_CODE=D.STOCK_UNIT THEN B.ACTUAL_QTY*C.DOSAGE_QTY*C.STOCK_QTY ELSE B.ACTUAL_QTY END AS DOSAGE_QTY "+ 
              "   FROM IND_DISPENSEM A, IND_DISPENSED B,PHA_TRANSUNIT C,PHA_BASE D "+
              "	  WHERE ( A.UPDATE_FLG = '3' OR A.UPDATE_FLG = '1' ) AND "+
              "         A.REQTYPE_CODE IN ('EXM','TEC') AND "+
              "		    A.APP_ORG_CODE  IN ( "+deptStr+" )  AND "+
              "         A.DISPENSE_DATE BETWEEN TO_DATE ('"+startDate+"','YYYYMMDDHH24MISS') AND "+
              "         TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS')  AND "+
              "         A.DISPENSE_NO = B.DISPENSE_NO AND "+
              "         B.ORDER_CODE=C.ORDER_CODE AND "+
              "         B.ORDER_CODE=D.ORDER_CODE  AND "+
              "         ( D.CTRLDRUGCLASS_CODE IS NULL OR  " +
        	  "           D.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) " ;
		
		
		return sql ;
		
	}
	
	/**
	 * ������ҩSQL
	 * @param startDate
	 * @param endDate
	 * @param isDrug   1:��ҩ  2���龫  ��ȫ��
	 * @return
	 */
	public String getOdiRtnSql(String startDate,String endDate,String isDrug){
		String sql = "" ;
	
		sql = "    SELECT A.ORDER_CODE,-1 * A.RTN_DOSAGE_QTY AS DOSAGE_QTY "+
        	  "	   FROM ODI_DSPNM A ,PHA_BASE B "+
        	  "	   WHERE A.DSPN_DATE BETWEEN TO_DATE ( '"+startDate+"' , 'YYYYMMDDHH24MISS')"+
        	  "                    AND TO_DATE ('"+endDate+"','YYYYMMDDHH24MISS')  AND  "+
        	  "          A.DSPN_KIND='RT' AND " +
        	  "          A.ORDER_CODE=B.ORDER_CODE AND " +
        	  "         ( B.CTRLDRUGCLASS_CODE IS NULL OR  " +
        	  "           B.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) " ;
			
		
		return sql;
	}
	
}
