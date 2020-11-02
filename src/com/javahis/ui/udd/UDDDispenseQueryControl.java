package com.javahis.ui.udd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
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
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangy 2010.12.27
 * @version 1.0
 */
public class UDDDispenseQueryControl extends TControl {

	public TTable table_m;

	public TTable table_d;

	public TTable table_ad;

	private TTextFormat ORG_CODE;

	// $$=============add by liyh 2012-07-10 ����������start==================$$//
	// =====modify-begin (by wanglong 20120716)===============================
	// �ɶԱ��������⣬��д
	// private Compare compare = new Compare();
	private BILComparator compare = new BILComparator();
	// ======modify-end========================================================
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by liyh 20120710 ����������end==================$$//

	DecimalFormat   sf  =   new  DecimalFormat("##0.00"); 
	DecimalFormat   af  =   new  DecimalFormat("##0.0000"); 
	
	public UDDDispenseQueryControl() {
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// lirui 2012-7-5 ��������ϸ����Ϣҳǩ start
		((TTabbedPane) this.getComponent("tTabbedPane_0")).setEnabledAt(2,
				false);
		// lirui 2012-7-5 ��������ϸ����Ϣҳǩ end
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
		// ===========pangben modify 20110511 stop
		// $$=====add by liyh 20120710�������򷽷�start============$$//
		addListener(getTTable("TABLE_M"));
		// $$=====add by liyh 20120710 �������򷽷�end============$$//
		ORG_CODE = (TTextFormat) this.getComponent("ORG_CODE");
		ORG_CODE.setValue(Operator.getDept());
		this.setValue("ORG_CODE", Operator.getDept());

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {

		// lirui 2012-06-29 start
		if (getPanel("tPanel_3").isShowing()) {// �����ѯ
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

			String where = "";
			// �ж�ҩƷ����Ƿ�Ϊ�գ�ƴ��sql  
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				where += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
			}
			// ==========modify-begin (by wanglong 20120717) =============
			// String
			// sql=" SELECT AA.*, BB.VERIFYIN_PRICE * AA.DOSAGE_QTY AS STOCK_AMT "
			// +
			String sql = " SELECT AA.*,ROUND(BB.STOCK_PRICE,4) AS  STOCK_PRICE , ROUND (BB.STOCK_PRICE* AA.DOSAGE_QTY, 2) AS STOCK_AMT  "
					+ // by liyh 20120723 �޸���stock��û�����ռ۸�
					// ==========modify-end========================================  
					" FROM ( "                
					+
					// " SELECT  A.EXE_DEPT_CODE,D.REGION_CHN_ABN AS REGION_CHN_DESC,E.DEPT_CHN_DESC,A.ORDER_CODE,A.DEPT_CODE, "
					// +// ע�͵� by liyh 20120710
					// " B.ORDER_DESC AS ORDER_DESC,B.SPECIFICATION AS SPECIFICATION, C.UNIT_CHN_DESC AS UNIT_CHN_DESC, "
					// +// ע�͵� by liyh 20120710
					// " SUM (A.DOSAGE_QTY) AS DOSAGE_QTY,SUM (A.OWN_AMT) AS OWN_AMT "
					// + //lirui 2012-7-6 ���ü���// ע�͵� by liyh 20120710
					" SELECT D.REGION_CHN_ABN AS REGION_CHN_DESC, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC,B.SPECIFICATION AS SPECIFICATION, "
					+ // update by liyh 20120710
					" C.UNIT_CHN_DESC AS UNIT_CHN_DESC,  SUM(A.DOSAGE_QTY) AS DOSAGE_QTY, A.OWN_PRICE ,SUM(A.OWN_AMT) AS OWN_AMT "
					+ // update by liyh 20120710
					" FROM IBS_ORDD A, SYS_FEE B, SYS_UNIT C,SYS_REGION  D, PHA_BASE F "
					+ " WHERE A.CAT1_TYPE = 'PHA'  AND B.REGION_CODE = D.REGION_CODE   "
					+ " AND A.ORDER_CODE = B.ORDER_CODE AND A.DOSAGE_UNIT = C.UNIT_CODE "
					+ " AND A.BILL_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ where
					+ "  AND A.ORDER_CODE=F.ORDER_CODE ";

			// �ж�ҩ��
			if (!"".equals(this.getValueString("ORG_CODE"))) {
				sql += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE")
						+ "' ";
			} else {
				sql += " AND A.EXE_DEPT_CODE IN ( '040103','030503','0306' ) ";
			}
			// ����ҩƷ�ȼ�
			if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
				String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE")
						+ "";
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

			sql +=
			// "  GROUP BY  A.EXE_DEPT_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,C.UNIT_CHN_DESC,A.OWN_PRICE,D.REGION_CHN_ABN,E.DEPT_CHN_DESC,A.DEPT_CODE "
			// +// update by liyh 20120710
			"  GROUP BY  D.REGION_CHN_ABN ,  A.ORDER_CODE,  B.ORDER_DESC  ,  B.SPECIFICATION   ,   C.UNIT_CHN_DESC, A.OWN_PRICE  "
					+ // update by liyh 20120710
					"  ORDER BY ORDER_CODE "
					+ "  ) AA, PHA_BASE BB  "
					+ "  WHERE AA.ORDER_CODE = BB.ORDER_CODE ";// by liyh
																// 20120723
																// �޸���stock��û�����ռ۸�
		    //System.out.println("סԺҩƷ���� ����----------sql---------"+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			// System.out.println("---parm----"+parm);
			if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_m.setParmValue(new TParm());
				return;
			}

			double sum_amt = 0;
			for (int i = 0; i < parm.getCount("OWN_AMT"); i++) {
				sum_amt += parm.getDouble("OWN_AMT", i);
			}
			double stock_amt = 0;
			for (int i = 0; i < parm.getCount("STOCK_AMT"); i++) {
				stock_amt += parm.getDouble("STOCK_AMT", i);
			}
			parm.addData("REGION_CHN_DESC", "�ܼ�:");
			parm.addData("DEPT_CHN_DESC", "");
			parm.addData("ORDER_DESC", "");
			parm.addData("SPECIFICATION", "");
			parm.addData("DOSAGE_QTY", "");
			parm.addData("UNIT_CHN_DESC", "");
			parm.addData("OWN_AMT", StringTool.round(sum_amt, 2));
			parm.addData("STOCK_AMT", StringTool.round(stock_amt, 2));  
			table_m.setParmValue(parm);
			// ***************************************************************************
			// luhai modify 2012-05-07 begin �������ҩƷ�ȼ��Ϳ����صȼ���ѯ���� begin
			// ***************************************************************************

		}
		// /lirui 2012-7-5 ��������ϸ����Ϣҳǩ�������������Ʋ�ѯָ����ϸ����Ϣ start
		else if (getPanel("tPanel_6").isShowing()) {// ����ϸ��

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
					+ " FROM IBS_ORDD A, SYS_PATINFO B, SYS_FEE_HISTORY C,SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M,PHA_BASE G  "
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
				sql += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE")
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

			sql += " AND A.BILL_DATE IS NOT NULL "
					+ "  AND A.BILL_DATE BETWEEN "
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

	/**
	 * ��շ���
	 */
	public void onClear() {
		String clearStr = "ORDER_CODE;ORDER_DESC";
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
		//fux modify ���� �����ӡ �� ϸ���ӡ����
		if (getPanel("tPanel_3").isShowing()) {
			if (table_m.getRowCount() <= 0) {
				this.messageBox("û�д�ӡ����");  
				return;  
			} 
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", "סԺҩƷ����ͳ�Ʊ�(����)");
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");
			//messageBox("start_date:"+start_date);  
			date.setData("DATE_AREA", "TEXT", "ͳ������: "
					+ start_date.substring(0, 4) + "/"
					+ start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " "
					+ start_date.substring(11, 13) + ":"
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"
					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "�Ʊ�����: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE"));
			date.setData("ORG_CODE", "TEXT", "����: " +orgDesc );
			// �������
			TParm parm = new TParm();
			TParm tableParm = table_m.getParmValue();
			//REGION_CHN_DESC;ORDER_CODE;ORDER_DESC;SPECIFICATION;DOSAGE_QTY;UNIT_CHN_DESC;OWN_PRICE;OWN_AMT;STOCK_PRICE;STOCK_AMT
			for (int i = 0; i < table_m.getRowCount(); i++) {

				parm.addData("REGION_CHN_DESC", tableParm.getValue("REGION_CHN_DESC", i));
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue(
						"ORDER_DESC", i));  
				parm.addData("SPECIFICATION", tableParm.getValue(
						"SPECIFICATION", i));
				parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));  
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(  
						"UNIT_CHN_DESC", i));
				parm.addData("OWN_PRICE", af.format(tableParm.getDouble("OWN_PRICE", i)));
				parm.addData("OWN_AMT", sf.format(tableParm.getDouble("OWN_AMT", i)));
				parm.addData("STOCK_PRICE", af.format(tableParm.getDouble("STOCK_PRICE", i)));  
				parm.addData("STOCK_AMT", sf.format(tableParm.getDouble("STOCK_AMT", i)));

			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");    
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");  
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
			date.setData("TABLE", parm.getData());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UDDDispenseM.jhw",
					date);
			// ExportExcelUtil.getInstance().exportExcel(table_m,
			// "סԺҩƷ����ͳ�Ʊ�(������Ϣ)");
		} else if (getPanel("tPanel_5").isShowing()) {
			if (table_d.getRowCount() <= 0) {  
				this.messageBox("û�д�ӡ����");
				return;
			}   
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", "סԺҩƷ����ͳ�Ʊ�(��һ��ϸ)");
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");
			date.setData("DATE_AREA", "TEXT", "ͳ������: "
					+ start_date.substring(0, 4) + "/"  
					+ start_date.substring(5, 7) + "/"
					+ start_date.substring(8, 10) + " "
					+ start_date.substring(11, 13) + ":"
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"  
					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "�Ʊ�����: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			String orgDesc = getOrg(this.getValueString("ORG_CODE"));
			date.setData("ORG_CODE", "TEXT", "����: " +orgDesc );
			// �������
			TParm parm = new TParm();
			TParm tableParm = table_d.getParmValue();
			// DSPN_KIND;FREQ_CHN_DESC;MR_NO;PAT_NAME;DOSAGE_QTY;UNIT_CHN_DESC;OWM_AMT
			for (int i = 0; i < table_d.getRowCount(); i++) {

				parm.addData("DSPN_KIND", tableParm.getValue("DSPN_KIND", i));
				parm.addData("FREQ_CHN_DESC", tableParm.getValue("FREQ_CHN_DESC", i));
				parm.addData("MR_NO", tableParm.getValue(
						"MR_NO", i));
				parm.addData("PAT_NAME", tableParm.getValue(
						"PAT_NAME", i));
				parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue(
						"UNIT_CHN_DESC", i));
				parm.addData("OWM_AMT", sf.format(tableParm.getDouble("OWM_AMT", i)));

			}
			parm.setCount(parm.getCount("DSPN_KIND"));
			parm.addData("SYSTEM", "COLUMNS", "DSPN_KIND");
			parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "OWM_AMT");
			date.setData("TABLE", parm.getData());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UDDDispenseD.jhw",
					date);

			// ExportExcelUtil.getInstance().exportExcel(table_d,
			// "סԺҩƷ����ͳ�Ʊ�(ϸ����Ϣ)");
		}
		else if (getPanel("tPanel_6").isShowing()) {
			if (table_ad.getRowCount() <= 0) {
				this.messageBox("û�д�ӡ����");
				return;
			}
			// ��ӡ����
			TParm date = new TParm();
			// ��ͷ����
			date.setData("TITLE", "TEXT", "סԺҩƷ����ͳ�Ʊ�(������ϸ)");
			String start_date = getValueString("START_DATE");
			String end_date = getValueString("END_DATE");    
			date.setData("DATE_AREA", "TEXT", "ͳ������: "
					+ start_date.substring(0, 4) + "/"
					+ start_date.substring(5, 7) + "/"  
					+ start_date.substring(8, 10) + " "    
					+ start_date.substring(11, 13) + ":"  
					+ start_date.substring(14, 16) + ":"
					+ start_date.substring(17, 19) + " ~ "
					+ end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
					+ "/" + end_date.substring(8, 10) + " "
					+ end_date.substring(11, 13) + ":"
					+ end_date.substring(14, 16) + ":"
					+ end_date.substring(17, 19));
			date.setData("DATE", "TEXT", "�Ʊ�����: "
					+ SystemTool.getInstance().getDate().toString().substring(
							0, 10).replace('-', '/'));
			date.setData("USER", "TEXT", "�Ʊ���: " + Operator.getName());
			// �������
			TParm parm = new TParm();
			TParm tableParm = table_ad.getParmValue();
			// MR_NO;PAT_NAME;ORDER_CODE;ORDER_DESC;DSPN_KIND;FREQ_CHN_DESC;DOSAGE_QTY;UNIT_CHN_DESC;OWM_AMT
			for (int i = 0; i < table_ad.getRowCount(); i++) {

				parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				parm.addData("ORDER_CODE", tableParm.getValue(
						"ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue(
						"ORDER_DESC", i));
				parm.addData("DSPN_KIND", tableParm.getValue("DSPN_KIND", i));
				parm.addData("FREQ_CHN_DESC", tableParm.getValue(
						"FREQ_CHN_DESC", i));
				parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
				parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
				parm.addData("OWM_AMT", sf.format(tableParm.getDouble("OWM_AMT",
						i)));

			}
			parm.setCount(parm.getCount("MR_NO"));
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");   
			parm.addData("SYSTEM", "COLUMNS", "DSPN_KIND");  
			parm.addData("SYSTEM", "COLUMNS", "FREQ_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");   
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "OWM_AMT");
			date.setData("TABLE", parm.getData());
			// ���ô�ӡ����
			this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UDDDispenseAD.jhw",
					date);

			// ExportExcelUtil.getInstance().exportExcel(table_d,
			// "סԺҩƷ����ͳ�Ʊ�(ϸ����Ϣ)");
		}  
	}
	  
	private String getOrg(String valueString) {
		String sql = " SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"+valueString+"' ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ORG_CHN_DESC",0);
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
			TParm parm = table_m.getParmValue()
					.getRow(table_m.getSelectedRow());
			// ִ�п���
			String dept_code = getValueString("ORG_CODE"); // by liyh 20120710
			// ҩƷ���
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
			String sql = " SELECT CASE M.RX_KIND   "
					+ "    WHEN 'ST' THEN '��ʱ' " + "     WHEN 'UD' THEN '����' "
					+ "     WHEN 'DS' THEN '��Ժ��ҩ' "
					+ "     WHEN 'F'  THEN '������' " + "     ELSE '��ҩ'  "
					+ "  END " + "     AS DSPN_KIND," + "   E.FREQ_CHN_DESC,"
					+ "   B.PAT_NAME," + "   B.MR_NO," + "   A.DOSAGE_QTY ,"
					+ "   D.UNIT_CHN_DESC," + "   A.OWN_AMT AS OWM_AMT ,    "
					+ "   C.STOCK_PRICE  " + "  FROM IBS_ORDD A, "
					+ "   SYS_PATINFO B, " + "  PHA_BASE C," + "  SYS_UNIT D,"
					+ "  SYS_PHAFREQ E," + "  ODI_ORDER M " + " WHERE    "
					+ "    A.CAT1_TYPE = 'PHA'  " + " AND A.DOSAGE_QTY > 0  "
					+ " AND A.ORDER_NO IS NOT NULL "
					+ " AND A.BILL_DATE IS NOT NULL "
					+ " AND A.BILL_DATE BETWEEN TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ "                    AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND A.ORDER_CODE = '"
					+ order_code
					+ "' "
					+ "  AND A.EXE_DEPT_CODE='"
					+ dept_code
					+ "' "
					+ "  AND A.CASE_NO = M.CASE_NO "
					+ " AND A.ORDER_SEQ = M.ORDER_SEQ "
					+ " AND A.ORDER_NO = M.ORDER_NO "
					+ " AND M.MR_NO=B.MR_NO "
					+ " AND A.ORDER_CODE=C.ORDER_CODE "
					+ " AND C.DOSAGE_UNIT=D.UNIT_CODE  "
					+ " AND A.FREQ_CODE=E.FREQ_CODE  "

					+ "  UNION ALL  "

					+ "  SELECT '��ҩ' AS DSPN_KIND,  "
					+ "     '' AS FREQ_CHN_DESC,"
					+ "     B.PAT_NAME, "
					+ "     F.MR_NO,   "
					+ "      DOSAGE_QTY AS DOSAGE_QTY, "
					+ "     D.UNIT_CHN_DESC, "
					+ "      A.OWN_AMT AS OWM_AMT, "
					+ "      C.STOCK_PRICE "
					+ " FROM IBS_ORDD A, "
					+ "      SYS_PATINFO B, "
					+ "      PHA_BASE C, "
					+ "      SYS_UNIT D, "
					+ "      ADM_INP F "
					+ " WHERE     A.CASE_NO = F.CASE_NO "
					+ "      AND F.MR_NO = B.MR_NO "
					+ "      AND A.ORDER_CODE = C.ORDER_CODE "
					+ "      AND C.DOSAGE_UNIT = D.UNIT_CODE "
					+ "      AND A.CAT1_TYPE = 'PHA'  "
					+ "      AND A.EXE_DEPT_CODE='"
					+ dept_code
					+ "' "
					+ "      AND ORDER_NO IS NOT NULL "
					+ "      AND DOSAGE_QTY < 0  "
					+ "      AND A.BILL_DATE BETWEEN TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ "                          AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ "      AND A.ORDER_CODE = '"
					+ order_code
					+ "'"
					+ " UNION ALL "
					+ "  SELECT '����Ʒ�' "
					+ "        AS DSPN_KIND, "
					+ "     E.FREQ_CHN_DESC, "
					+ "      B.PAT_NAME, "
					+ "     B.MR_NO, "
					+ "     A.DOSAGE_QTY, "
					+ "     D.UNIT_CHN_DESC, "
					+ "      A.OWN_AMT AS OWM_AMT, "
					+ "      C.STOCK_PRICE "
					+ "  FROM IBS_ORDD A, "
					+ "      SYS_PATINFO B, "
					+ "     PHA_BASE C, "
					+ "      SYS_UNIT D, "
					+ "      SYS_PHAFREQ E, "
					+ "      IBS_ORDM F "
					+ " WHERE     A.CAT1_TYPE = 'PHA' "
					+ "      AND A.DOSAGE_QTY > 0 "
					+ "      AND A.BILL_DATE IS NOT NULL "
					+ "      AND A.BILL_DATE BETWEEN TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ "                          AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ "      AND A.ORDER_CODE = '"
					+ order_code
					+ "' "
					+ "      AND A.EXE_DEPT_CODE = '"
					+ dept_code
					+ "' "
					+ "      AND F.CASE_NO=A.CASE_NO "
					+ "      AND F.CASE_NO_SEQ=A.CASE_NO_SEQ "
					+ "      AND A.ORDER_CODE = C.ORDER_CODE "
					+ "      AND C.DOSAGE_UNIT = D.UNIT_CODE "
					+ "      AND A.FREQ_CODE = E.FREQ_CODE "
					+ "      AND F.MR_NO=B.MR_NO  "
					+ "      AND F.DATA_TYPE='1' ";

			System.out.println("sql----:" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("û�в�ѯ����");
				table_d.setParmValue(new TParm());
				return;
			}
			double sum_amt = 0;
			// double verifyAmt = 0;
			double dosageQty = 0;
			for (int i = 0; i < result.getCount("OWM_AMT"); i++) {
				sum_amt += result.getDouble("OWM_AMT", i);
				// verifyAmt += result.getDouble("VERIFY_AMT", i);
				dosageQty += result.getDouble("DOSAGE_QTY", i);
			}

			result.addData("DSPN_KIND", "�ܼ�:");
			result.addData("FREQ_CHN_DESC", "");
			result.addData("MR_NO", "");
			result.addData("PAT_NAME", "");
			result.addData("DOSAGE_QTY", dosageQty);
			result.addData("UNIT_CHN_DESC", "");
			result.addData("OWM_AMT", StringTool.round(sum_amt, 2));

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
					+ " FROM IBS_ORDD A, SYS_PATINFO B, SYS_FEE_HISTORY C, "
					+ " SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M  "
					+ " WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND C.UNIT_CODE = D.UNIT_CODE AND A.FREQ_CODE = E.FREQ_CODE"
					+ " AND A.CAT1_TYPE = 'PHA' AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  "
					+ " AND A.BILL_DATE IS NOT NULL "
					+ "  AND A.BILL_DATE BETWEEN "
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
					+ " FROM IBS_ORDD A, SYS_PATINFO B, PHA_BASE C, "
					+ " SYS_UNIT D,ADM_INP F WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO "
					+ " AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND C.DOSAGE_UNIT = D.UNIT_CODE "
					+ " AND A.CAT1_TYPE = 'PHA' AND ORDER_NO IS NOT NULL AND DOSAGE_QTY < 0  "
					+ "  AND A.BILL_DATE "
					+ " BETWEEN TO_DATE ('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') "
					+ " AND TO_DATE ('"
					+ end_date
					+ "', 'YYYYMMDDHH24MISS')";
			// System.out.println("ϸ��sql-all-2--" + sql);
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
}
