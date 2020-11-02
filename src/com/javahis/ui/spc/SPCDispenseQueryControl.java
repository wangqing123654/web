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
 * Title: 住院药品销售统计表
 * </p>
 * 
 * <p>
 * Description: 住院药品销售统计表
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


	// 排序
	private BILComparator compare = new BILComparator();

	private boolean ascending = false;
	private int sortColumn = -1;

	public SPCDispenseQueryControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {

		((TTabbedPane) this.getComponent("tTabbedPane_0")).setEnabledAt(2,
				false);

		table_m = this.getTable("TABLE_M");
		table_d = this.getTable("TABLE_D");
		table_ad = this.getTable("TABLE_AD");

		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// ========pangben modify 20110511 start 权限添加
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));

		addListener(getTTable("TABLE_M"));

	}

	/**
	 * 查询方法
	 */
	public void onQuery() {

		// 汇总查询
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
				this.messageBox("供应商不能为空");
				return ;
			}
			String 	where = "";
			// 判断药品编号是否为空，拼接sql
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
			// by liyh 20120723 修改在stock里没有验收价格
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
			 
			System.out.println("住院药品销售 主项----------sql---------" + sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
				this.messageBox("没有查询数据");
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
				
				//标志是否在供应商对应的商品代码里，true是
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
 
			newParm.addData("REGION_CHN_DESC", "总计:");
			newParm.addData("ORDER_CODE", "");
			newParm.addData("DEPT_CHN_DESC", "");
			newParm.addData("ORDER_DESC", "");
			newParm.addData("SPECIFICATION", (count-1)+"条记录");
			newParm.addData("DOSAGE_QTY", "");
			newParm.addData("UNIT_CHN_DESC", "");
			newParm.addData("OWN_AMT", StringTool.round(sum_amt, 2));
			newParm.addData("STOCK_AMT", StringTool.round(stock_amt, 2));
			table_m.setParmValue(newParm);
			// ***************************************************************************
			// luhai modify 2012-05-07 begin 加入管制药品等级和抗生素等级查询条件 begin
			// ***************************************************************************

		} else if (getPanel("tPanel_6").isShowing()) {
			// 所有细项
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
			String sql = " SELECT F.MR_NO,B.PAT_NAME,CASE   M.RX_KIND  WHEN 'ST' THEN '临时' WHEN 'UD' THEN '长期' WHEN 'DS' THEN '出院带药' ELSE '退药' END   AS DSPN_KIND, E.FREQ_CHN_DESC,  "
					+ "  A.DOSAGE_QTY, D.UNIT_CHN_DESC,C.ORDER_CODE,C.ORDER_DESC, "
					+ " A.DOSAGE_QTY * C.OWN_PRICE AS OWM_AMT "
					+ " FROM ODI_DSPNM A, SYS_PATINFO B, SYS_FEE_HISTORY C,SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M,PHA_BASE G  "
					+ " WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND C.UNIT_CODE = D.UNIT_CODE AND A.FREQ_CODE = E.FREQ_CODE AND A.ORDER_CODE=G.ORDER_CODE  "
					+ " AND A.CAT1_TYPE = 'PHA' AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  ";
			// 判断药品编号是否为空，拼接sql
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				sql += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
			}
			// 判断药房
			if (!"".equals(this.getValueString("ORG_CODE"))) {
				sql += " AND A.EXEC_DEPT_CODE = '" + getValueString("ORG_CODE")
						+ "' ";
			}
			// 管制药品等级
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
			// 抗生素等级
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

			// 判断药品编号是否为空，拼接sql
			if (!"".equals(this.getValueString("ORDER_CODE"))) {
				sql += " AND A.ORDER_CODE = '" + getValueString("ORDER_CODE")
						+ "' ";
			}
			// 判断药房
			if (!"".equals(this.getValueString("ORG_CODE"))) {
				sql += " AND A.EXE_DEPT_CODE = '" + getValueString("ORG_CODE")
						+ "' ";
			}
			// 管制药品等级
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
			// 抗生素等级
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
			// System.out.println("住院药品销售 所有细项---------sql：" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("没有查询数据");
				table_ad.removeRowAll();
				return;
			}
			table_ad.setParmValue(result);
		}
		// /lirui 2012-7-5 加入所有细项信息页签，根据条件限制查询指定的细项信息 end
		else {
			this.messageBox("请选择主项信息");
		}
		// 药库查询改用ibs_ordd 进行查询 end
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
			
			
			// 管制药品等级
			if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
				String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
				sql += " AND F.CTRLDRUGCLASS_CODE IS NOT NULL ";
				if (!"".equals(ctrlClass) && ctrlClass != null) {
					sql += " AND F.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
				} else {
					sql += " ";
				}
			}
			// 抗生素等级
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
		// 判断药房
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
		// 管制药品等级
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// 抗生素等级
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
		// 判断药房
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
		// 管制药品等级
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// 抗生素等级
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
	 * 清空方法
	 */
	public void onClear() {
		String clearStr = "ORG_CODE;ORDER_CODE;ORDER_DESC";
		this.clearValue(clearStr);
		callFunction("UI|setSysStatus", "" + " : " + "");
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
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
	 * 打印方法
	 */
	public void onPrint() {

	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (getPanel("tPanel_3").isShowing()) {
			if (table_m.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_m,
					"住院药品销售统计表(主项信息)");
		} else {
			if (table_d.getRowCount() <= 0) {
				this.messageBox("没有汇出数据");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(table_d,
					"住院药品销售统计表(细项信息)");
		}
	}
	
	/**
	 * 导出XML给国药结算
	 */
	public void onExportXml(){
		
	}

	/**
	 * 变更属性页
	 */
	public void onChangeTTabbedPane() {

		long starttime = System.currentTimeMillis();
		// LIRUI modify 2012-06-29 改用ibs_ordd 查询
		if (getPanel("tPanel_5").isShowing() && table_m.getSelectedRow() < 0) {
			this.messageBox("请选择主项信息");
			((TTabbedPane) this.getComponent("tTabbedPane_0"))
					.setSelectedIndex(0);
			return;
		}
		
		// *************单一细项信息*******************
		else if (getPanel("tPanel_5").isShowing()
				&& table_m.getSelectedRow() >= 0) {
			
			String orgCode = getValueString("ORG_CODE"); 
			if(orgCode== null || orgCode.equals("")){
				this.messageBox("请选择药房");
				return ;
			}
			TParm parm = table_m.getParmValue()
					.getRow(table_m.getSelectedRow());
			// 执行科室
			//String dept_code = getValueString("ORG_CODE"); // by liyh 20120710
			// 药品编号
			if(orgCode != null && !orgCode.equals("040103")){
				this.messageBox("没有查询数据");
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
					     +"     WHEN 'ST' THEN '临时' "
					     +"     WHEN 'UD' THEN '长期' "
					     +"     WHEN 'DS' THEN '出院带药' "
					     +"     WHEN 'F'  THEN '首日量' "
					     +"     ELSE '退药' "
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
					     +"	SELECT '退药'  AS DSPN_KIND, "
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
			
			//System.out.println("明细-------------sql--" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("没有查询数据");
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
			
			 
			result.addData("DSPN_KIND", "总计:");
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
		// lirui 2012-07-05 增加所有细项信息页签， 根据主项信息查询所有的细项信息 strart
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
			String sql = " SELECT F.MR_NO,B.PAT_NAME,CASE   M.RX_KIND  WHEN 'ST' THEN '临时' WHEN 'UD' THEN '长期' WHEN 'DS' THEN '出院带药' ELSE '退药' END   AS DSPN_KIND, E.FREQ_CHN_DESC,  "
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
					+ " SELECT F.MR_NO,B.PAT_NAME,'退药' AS DSPN_KIND, '' AS FREQ_CHN_DESC, DOSAGE_QTY  "
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
			//System.out.println("细项sql-all-2--" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result == null || result.getCount("DSPN_KIND") <= 0) {
				this.messageBox("没有查询数据");
				table_ad.removeRowAll();
				return;
			}
			long endtime = System.currentTimeMillis();
			// System.out.println("------变更页属性---------time: "+(endtime-starttime));
			table_ad.setParmValue(result);
		}
		// lirui 2012-07-05 增加所有细项信息页签， 根据主项信息查询所有的细项信息 end
	}

	// lirui 20120606 变更复选框 start
	/**
	 * 变更复选框
	 */
	public void onChangeCheckBox() {
		// 管制药品等级
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			getComBox("CTRLDRUGCLASS_CODE").setEnabled(true);

		} else {
			getComBox("CTRLDRUGCLASS_CODE").setEnabled(false);
			this.clearValue("CTRLDRUGCLASS_CODE");
		}

		// 抗生素等级
		if (getCheckBox("ANTIBIOTIC").isSelected()) {
			getComBox("ANTIBIOTIC_CODE").setEnabled(true);
		} else {
			getComBox("ANTIBIOTIC_CODE").setEnabled(false);
			this.clearValue("ANTIBIOTIC_CODE");
		}
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @return TCheckBox
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 得到combox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 点击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked() {
		// //得到选择的行
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

	// lirui 20120606 变更复选框 end

	/**
	 * 接受返回值方法
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
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TPanel对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TPanel getPanel(String tagName) {
		return (TPanel) getComponent(tagName);
	}

	/**
	 * 得到TTable
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 * @author liyh
	 * @date 20120710
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// TParm tableData = getTTable("TABLE_M").getParmValue();
		// System.out.println("===tableDate排序前==="+tableData);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				/*
				 * System.out.println("----+i:"+i);
				 * System.out.println("----+i:"+j);
				 */
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				// ==========modify-begin (by wanglong 20120716)===============
				TParm tableData = getTTable("TABLE_M").getParmValue();
				// TParm tableData = getTTable("TABLE_M").getShowParmValue();
				// System.out.println("tableData:"+tableData);
				tableData.removeGroupData("SYSTEM");
				tableData.removeData("Data", "DEPT_CHN_DESC");
				tableData.addData("ORDER_CODE", "");
				// “总计”行 不参与排序
				TParm totRowParm = new TParm();// 记录“总计”行
				totRowParm.addRowData(table.getShowParmValue(), tableData
						.getCount());
				int rowCount = tableData.getCount();// 数据的总行数（包括小计行和总计行）
				tableData.removeRow(tableData.getCount());// 去除最后一行(总计行)
				// ==========modify-end========================================
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE_M").getParmMap(
						sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				// ==========modify-begin (by wanglong 20120716)===============
				// cloneVectoryParam(vct, new TParm(), strNames);
				TParm lastResultParm = new TParm();// 记录最终结果
				lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
				lastResultParm.addRowData(totRowParm, 0);// 加入总计行
				lastResultParm.setCount(rowCount);
				table.setParmValue(lastResultParm);
				// ==========modify-end========================================

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 加入排序功能
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
	 * vectory转成param
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
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
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
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 根据orgCode查询对应的子库
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
	 * 得到调配的SQL
	 * @param start_date
	 * @param end_date
	 * @param isDrug   1:普药  2：麻精  空全部
	 * @return
	 */
	public String getOdiDspnmSql(String start_date,String end_date,String isDrug){
	 
		String sql = "    SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
				 	  "	  FROM ODI_DSPNM A  "+
				      "	  WHERE A.PHA_DOSAGE_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS') "+
				      "         AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') " ;
		// 管制药品等级
		if (getCheckBox("CTRLDRUGCLASS").isSelected()) {
			String ctrlClass = this.getValueString("CTRLDRUGCLASS_CODE") + "";
			sql += " AND A.CTRLDRUGCLASS_CODE IS NOT NULL ";
			if (!"".equals(ctrlClass) && ctrlClass != null) {
				sql += " AND A.CTRLDRUGCLASS_CODE='" + ctrlClass + "' ";
			} else {
				sql += " ";
			}
		}
		// 抗生素等级
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
	 * 手术室与介入室SQL (普药、麻精)
	 * @param startDate
	 * @param endDate
	 * @param isDrug   1:普药  2：麻精  空全部
	 * @param parm     子部门集合
	 * @return
	 */
	public String getDispenseDSql(String startDate,String endDate,String isDrug,TParm parm){
		//子库组合
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
	 * 调配退药SQL
	 * @param startDate
	 * @param endDate
	 * @param isDrug   1:普药  2：麻精  空全部
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
