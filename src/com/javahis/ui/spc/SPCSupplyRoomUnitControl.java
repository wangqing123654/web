package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class SPCSupplyRoomUnitControl extends TControl {

	private TTable table;
	private TTextFormat startDate;
	private TTextFormat endDate;
	// 日期格式化
	private SimpleDateFormat formateDate = new SimpleDateFormat("yyyy/MM/dd");

	// private SimpleDateFormat sdf = new
	// SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
	public SPCSupplyRoomUnitControl() {

	}

	/**
	 * 初始化
	 */
	public void onInit() {
		this.initPage();
		// this.onQuery();
	}

	/**
	 * 初始化页面
	 */
	public void initPage() {
		this.startDate = this.getTextFormat("DATE_START");
		this.endDate = this.getTextFormat("DATE_END");
		Calendar cd = Calendar.getInstance();
		Calendar cdto = Calendar.getInstance();
		cd.add(Calendar.MONTH, -1);
		this.startDate.setValue(formateDate.format(cd.getTime()));
		this.endDate.setValue(formateDate.format(cdto.getTime()));
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		this.table = this.getTable("TABLE");
		this.startDate = this.getTextFormat("DATE_START");
		this.endDate = this.getTextFormat("DATE_END");
		this.table.removeRowAll();
		TParm parm = new TParm();
		String SUP_CODE = this.getValueString("SUP_CODE");
		String ORG_CODE = this.getValueString("ORG_CODE");
		// this.startDate.getValue();
		String start_date = this.formateDate.format(this.startDate.getValue());
		String end_date = this.formateDate.format(this.endDate.getValue());
		// String start1 = this.sdf.format(this.startDate.getValue());
		// String end1 = this.sdf.format(this.endDate.getValue());
		long l1 = 0;
		long l2 = 0;
		try {
			Date start = formateDate.parse(start_date);
			l1 = start.getTime();
			Date end = formateDate.parse(end_date);
			l2 = end.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (l1 > l2) {
			this.messageBox("开始时间不能大于结束时间！");
			return;
		}
		// String date_start[] = start_date.split("/");
		// String date_end[] = end_date.split("/");
//		String sql = "SELECT H.SUP_ABS_DESC ,"
//				+
//				// 西药
//				"SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 )) WESTERN_MEDICINE,"
//				+
//				// 中成药
//				"SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 )) CTPM,"
//				+
//				// 消毒剂
//				"SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 )) DISINFECTANT,"
//				+
//				// 制剂
////				"SUM(DECODE( H.TYPE_CODE , '6' , H.VER_AMT , 0.0 )) REAGENT, "
////				+
//				// 合计1
//				"(SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 ))+"
//				+ "SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 ))+"
//				+ "SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 ))"
////				+ "SUM(DECODE( H.TYPE_CODE , '6' , H.VER_AMT , 0.0 ))"
//				+ ") AS ALL_TOT1,"
//				+
//				// 退货
//				"SUM(H.BACK_AMT*1) AS BACK_AMT, "
//				+
//				//CASE WHEN  W.BACK_AMT IS NULL THEN  0  ELSE
//				// 合计2
//				"(SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 ))+"
//				+ "SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 ))+"
//				+ "SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 ))-"
////				+ "SUM(DECODE( H.TYPE_CODE , '6' , H.VER_AMT , 0.0 ))-"
//				+ "SUM(DECODE( H.BACK_AMT , NULL ,0.0  , H.BACK_AMT )) ) AS ALL_TOT2, "
//				+ " SUM(H.CTPM_RETN) AS CTPM_RETN,SUM(H.WEST_RETN) AS WEST_RETN "//20150430 wangjingchun add
//				+ "FROM (" + "SELECT "
//				+ "S.SUP_ABS_DESC,D.ORDER_DESC AS ORDER_DESC,"           
//				+ "D.SPECIFICATION,E.UNIT_CHN_DESC,"      
//				+ "SUM (B.VERIFYIN_QTY) AS QTY," + "B.VERIFYIN_PRICE,"
//				+ "SUM (B.VERIFYIN_QTY * B.VERIFYIN_PRICE) AS VER_AMT,"    
//				+ "B.RETAIL_PRICE AS OWN_PRICE,"
//				+ "SUM (B.VERIFYIN_QTY * B.RETAIL_PRICE) AS OWN_AMT,"  
//				+ "SUM (B.VERIFYIN_QTY * B.RETAIL_PRICE)"
//				+ "- SUM (B.VERIFYIN_QTY * B.VERIFYIN_PRICE)"
//				+ "AS DIFF_AMT,D.MAN_CODE AS MAN_NAME,"
//				+ "G.TYPE_CODE, 0 AS BACK_AMT, "
//				+ " 0 AS WEST_RETN,0 AS CTPM_RETN " //20150430 wangjingchun add
//				+ "FROM IND_VERIFYINM A,"
//				+ "IND_VERIFYIND B," + "SYS_FEE D," + "SYS_UNIT E,"
//				+ "PHA_BASE G,SYS_SUPPLIER S "
//				+ " WHERE     A.SUP_CODE = S.SUP_CODE "  
//				+ "AND A.VERIFYIN_NO = B.VERIFYIN_NO "
//				+ "AND B.ORDER_CODE = D.ORDER_CODE "
//				+ "AND B.BILL_UNIT = E.UNIT_CODE "
//				+ "AND B.UPDATE_FLG IN ('1', '3') "  
//				+ "AND B.ORDER_CODE = G.ORDER_CODE "
//				+ "AND D.ORDER_CODE = G.ORDER_CODE "
//				+ "AND A.CHECK_DATE BETWEEN TO_DATE ('" + start_date
//				+ " 00:00:00','YYYY-MM-DD HH24:MI:SS') AND TO_DATE ('"
//				+ end_date + " 23:59:59','YYYY-MM-DD HH24:MI:SS') " +
//
//				"";
		String sql = "SELECT H.SUP_ABS_DESC ,"
			+
			// 西药
			"SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 )) WESTERN_MEDICINE,"
			+
			// 中成药
			"SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 )) CTPM,"
			+
			// 消毒剂
			"SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 )) DISINFECTANT,"
			+
			// 中草药
			"SUM(DECODE( H.TYPE_CODE , '3' , H.VER_AMT , 0.0 )) REAGENT, "
			+
			// 合计1
			" (SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '3' , H.VER_AMT , 0.0 ))"
			+ ") AS ALL_TOT1,"
			+
			// 退货
			"SUM(H.BACK_AMT*1) AS BACK_AMT, "
			+
			//CASE WHEN  W.BACK_AMT IS NULL THEN  0  ELSE
			// 合计2
			"(SUM(DECODE( H.TYPE_CODE , '1' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '2' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '4' , H.VER_AMT , 0.0 ))+"
			+ "SUM(DECODE( H.TYPE_CODE , '3' , H.VER_AMT , 0.0 ))-"
			+ "SUM(DECODE( H.BACK_AMT , NULL ,0.0  , H.BACK_AMT )) ) AS ALL_TOT2, "
			+ " SUM(H.CTPM_RETN) AS CTPM_RETN,SUM(H.WEST_RETN) AS WEST_RETN,SUM (H.ZCC_RETN) AS ZCC_RETN "//20150430 wangjingchun add
			+ "FROM (" + "SELECT "
			+ "S.SUP_ABS_DESC,D.ORDER_DESC AS ORDER_DESC,"           
			+ "D.SPECIFICATION,E.UNIT_CHN_DESC,"      
			+ "SUM (B.VERIFYIN_QTY) AS QTY," + "B.VERIFYIN_PRICE,"         
			+ "SUM (B.VERIFYIN_QTY * B.VERIFYIN_PRICE) AS VER_AMT,"    
			+ "B.RETAIL_PRICE AS OWN_PRICE,"
			+ "SUM (B.VERIFYIN_QTY * B.RETAIL_PRICE) AS OWN_AMT,"    
			+ "SUM (B.VERIFYIN_QTY * B.RETAIL_PRICE)"
			+ "- SUM (B.VERIFYIN_QTY * B.VERIFYIN_PRICE)"
			+ "AS DIFF_AMT,D.MAN_CODE AS MAN_NAME,"  
			+ "G.TYPE_CODE, 0 AS BACK_AMT, "
			+ " 0 AS WEST_RETN,0 AS CTPM_RETN,0 AS ZCC_RETN " //20150430 wangjingchun add
			+ "FROM IND_VERIFYINM A,"
			+ "IND_VERIFYIND B," + "SYS_FEE D," + "SYS_UNIT E,"
			+ "PHA_BASE G,SYS_SUPPLIER S "
			+ " WHERE     A.SUP_CODE = S.SUP_CODE "  
			+ "AND A.VERIFYIN_NO = B.VERIFYIN_NO "
			+ "AND B.ORDER_CODE = D.ORDER_CODE "
			+ "AND B.BILL_UNIT = E.UNIT_CODE "
			+ "AND B.UPDATE_FLG IN ('1', '3') "  
			+ "AND B.ORDER_CODE = G.ORDER_CODE "
			+ "AND D.ORDER_CODE = G.ORDER_CODE "
			+ "AND A.CHECK_DATE BETWEEN TO_DATE ('" + start_date
			+ " 00:00:00','YYYY-MM-DD HH24:MI:SS') AND TO_DATE ('"
			+ end_date + " 23:59:59','YYYY-MM-DD HH24:MI:SS') " +

			"";
		String sql_Query = "";
		if (!SUP_CODE.equals("")) {
			sql_Query += " AND S.SUP_CODE = '" + SUP_CODE + "' ";
		}
		if (!ORG_CODE.equals("")) {
			sql_Query += " AND A.ORG_CODE = '" + ORG_CODE + "' ";
		}

		String sql_end = " GROUP BY D.GOODS_DESC," + "D.ORDER_DESC,"
				+ "D.SPECIFICATION," + "E.UNIT_CHN_DESC," + "B.VERIFYIN_PRICE,"
				+ "B.RETAIL_PRICE," + "B.ORDER_CODE," + "D.MAN_CODE,"
				+ "G.TYPE_CODE,S.SUP_ABS_DESC "
				+ "  UNION ALL SELECT S.SUP_ABS_DESC, "
				+ " D.ORDER_DESC AS ORDER_DESC, " + " D.SPECIFICATION,"
				+ " E.UNIT_CHN_DESC," + " SUM (B.QTY) AS QTY,"
				+ "  B.VERIFYIN_PRICE,0 AS  VER_AMT," + "  B.RETAIL_PRICE AS OWN_PRICE,0 AS OWN_AMT,0 AS DIFF_AMT, "
				+ "   D.MAN_CODE AS MAN_NAME," + "   G.TYPE_CODE, SUM (B.QTY * B.UNIT_PRICE) AS BACK_AMT, "
				//20150430 wangjingchun add start
				+ " SUM(DECODE( G.TYPE_CODE , '1' , B.QTY * B.UNIT_PRICE , 0.0 )) AS WEST_RETN, "
				+ " SUM(DECODE( G.TYPE_CODE , '2' , B.QTY * B.UNIT_PRICE , 0.0 )) AS CTPM_RETN, "
				//20190711 fux modify
				+ " SUM(DECODE( G.TYPE_CODE , '3' , B.QTY * B.UNIT_PRICE , 0.0 )) AS ZCC_RETN "
				//20150430 wangjingchun add end
				+ "  FROM IND_REGRESSGOODSM A," + "       IND_REGRESSGOODSD B,"
				+ "        SYS_FEE D," + "         SYS_UNIT E,"
				+ "         PHA_BASE G," + "         SYS_SUPPLIER S"
				+ "    WHERE     A.SUP_CODE = S.SUP_CODE"
				+ "        AND A.REGRESSGOODS_NO = B.REGRESSGOODS_NO" 
				+ "        AND B.ORDER_CODE = D.ORDER_CODE" 
				+ "         AND B.BILL_UNIT = E.UNIT_CODE"
				+ "         AND B.UPDATE_FLG IN ('1', '3')"
				+ "         AND B.ORDER_CODE = G.ORDER_CODE"
				+ "         AND D.ORDER_CODE = G.ORDER_CODE"  
				+ "          AND A.CHECK_USER IS NOT NULL"
				+ "         AND A.CHECK_DATE BETWEEN TO_DATE ('" + start_date
				+ " 00:00:00','YYYY-MM-DD HH24:MI:SS')"
				+ "                               AND TO_DATE ('" + end_date  
				+ " 23:59:59','YYYY-MM-DD HH24:MI:SS')" + sql_Query  
				+ "   GROUP BY D.GOODS_DESC," + "            D.ORDER_DESC,"
				+ "            D.SPECIFICATION,"
				+ "            E.UNIT_CHN_DESC,"
				+ "            B.VERIFYIN_PRICE,"  
				+ "             B.RETAIL_PRICE,"
				+ "              B.ORDER_CODE," + "              D.MAN_CODE,"
				+ "            G.TYPE_CODE," + "                S.SUP_ABS_DESC"
				+ "  )H  ";
  
		// StringBuffer querySql = new StringBuffer();
		// querySql.append(sql);
		if (!SUP_CODE.equals("")) {
			sql += " AND S.SUP_CODE = '" + SUP_CODE + "' ";
		}
		if (!ORG_CODE.equals("")) {
			sql += " AND A.ORG_CODE = '" + ORG_CODE + "' ";
		} else {
			this.messageBox("请选择入库部门！");
			return;    
		}
		sql += sql_end;
		sql += "GROUP BY H.SUP_ABS_DESC " + "ORDER BY H.SUP_ABS_DESC";  
		// querySql.append(sql_end);  
		System.out.println("供应单位sql20190715:::::::::::::::::::::::::"+sql);    
		parm = new TParm(TJDODBTool.getInstance().select(sql));
		// sql =
		// " select '合计' as SUP_ABS_DESC, sum(WESTERN_MEDICINE) as WESTERN_MEDICINE,"+
		// "sum(CTPM) as CTPM,sum(DISINFECTANT) as DISINFECTANT,sum(REAGENT) "+
		// "as REAGENT,sum(WESTERN_MEDICINE+CTPM+DISINFECTANT+REAGENT) as ALL_TOT from("+
		// sql+")";
		if (parm.getCount() <= 0) {
			this.messageBox("未查询到相应数据！");
			return;                                                
		}              
		// 各类药物统计  
		double WESTERN_MEDICINE_TOT = 0;
		double CTPM_TOT = 0;
		double DISINFECTANT_TOT = 0;
		double REAGENT_TOT = 0;
		double ALL_TOT1_TOT = 0;
		//20150430 wangjingchun add start
		double CTPM_RETN_TOT = 0;
		double WEST_RETN_TOT = 0;
		double ZCC_RETN_TOT = 0;
		//20150430 wangjingchun add end
		double BACK_AMT_TOT = 0;
		double ALL_TOT2_TOT = 0;
		for (int i = 0; i < parm.getCount("WESTERN_MEDICINE"); i++) {
			WESTERN_MEDICINE_TOT += StringTool.getDouble(parm.getValue(
					"WESTERN_MEDICINE", i));
			CTPM_TOT += StringTool.getDouble(parm.getValue("CTPM", i));
			DISINFECTANT_TOT += StringTool.getDouble(parm.getValue(
					"DISINFECTANT", i));
			REAGENT_TOT += StringTool.getDouble(parm.getValue("REAGENT", i));
			ALL_TOT1_TOT += StringTool.getDouble(parm.getValue("ALL_TOT1", i));
			//20150430 wangjingchun add start
			ZCC_RETN_TOT += StringTool.getDouble(parm.getValue("ZCC_RETN", i));
			CTPM_RETN_TOT += StringTool.getDouble(parm.getValue("CTPM_RETN", i));
			WEST_RETN_TOT += StringTool.getDouble(parm.getValue("WEST_RETN", i));
			//20150430 wangjingchun add end
			BACK_AMT_TOT += StringTool.getDouble(parm.getValue("BACK_AMT", i));
			ALL_TOT2_TOT += StringTool.getDouble(parm.getValue("ALL_TOT2", i));
		}
		// TParm tot_parm = new TParm(TJDODBTool.getInstance().select(sql));
		// System.out.println(tot_parm);
		// parm.addParm(tot_parm);
		parm.addData("SUP_ABS_DESC", "合计");
		parm.addData("WESTERN_MEDICINE", WESTERN_MEDICINE_TOT);// 西药合计
		parm.addData("CTPM", CTPM_TOT);// 中成药合计
		parm.addData("DISINFECTANT", DISINFECTANT_TOT);// 消毒剂合计
		parm.addData("REAGENT", REAGENT_TOT);// 试剂合计
		parm.addData("ALL_TOT1", ALL_TOT1_TOT);// 合计1合计
		//20150430 wangjingchun add start
		parm.addData("ZCC_RETN", ZCC_RETN_TOT);// 中草药(退)合计
		parm.addData("CTPM_RETN", CTPM_RETN_TOT);// 中药(退)合计
		parm.addData("WEST_RETN", WEST_RETN_TOT);// 西药(退)合计
		//20150430 wangjingchun add end
		parm.addData("BACK_AMT", BACK_AMT_TOT);// 退货合计
		parm.addData("ALL_TOT2", ALL_TOT2_TOT);// 合计2合计                                         
		// 处理数字数据，防止出现科学计数显示
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm date_parm = new TParm();
		for (int i = 0; i < parm.getCount("WESTERN_MEDICINE"); i++) {
			date_parm.addData("SUP_ABS_DESC", parm.getValue("SUP_ABS_DESC", i));
			date_parm.addData("WESTERN_MEDICINE", df.format(parm.getDouble(
					"WESTERN_MEDICINE", i)));
			date_parm.addData("CTPM", df.format(parm.getDouble("CTPM", i)));
			date_parm.addData("DISINFECTANT", df.format(parm.getDouble(
					"DISINFECTANT", i)));
			date_parm.addData("REAGENT", df
					.format(parm.getDouble("REAGENT", i)));
			date_parm.addData("ALL_TOT1", df.format(parm.getDouble("ALL_TOT1",
					i)));
			//20150430 wangjingchun add start
			date_parm.addData("ZCC_RETN", df.format(parm.getDouble("ZCC_RETN",
					i)));
			date_parm.addData("CTPM_RETN", df.format(parm.getDouble("CTPM_RETN",
					i)));
			date_parm.addData("WEST_RETN", df.format(parm.getDouble("WEST_RETN",
					i)));
			//20150430 wangjingchun add end
			date_parm.addData("BACK_AMT", df.format(parm.getDouble("BACK_AMT",
					i)));
			date_parm.addData("ALL_TOT2", df.format(parm.getDouble("ALL_TOT2",
					i)));

		}
		this.table.setParmValue(date_parm);
	}  

	/**
	 * 清空控件
	 */
	public void onClear() {
		String clearNames = "ORG_CODE;SUP_CODE";
		// 清空文本框内容
		this.clearValue(clearNames);
		this.initPage();
		this.table.removeRowAll();
		// this.onQuery();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "供应商应付药品明细表");
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		this.table = this.getTable("TABLE");
		if (this.table.getRowCount() > 0) {
			// 表头数据
			TParm date = new TParm();
			String start_date = this.formateDate.format(this.startDate
					.getValue());
			String end_date = this.formateDate.format(this.endDate.getValue());
			// 制表时间
			date.setData("DATA", "TEXT", "统计区间: " + start_date + " 00:00:00～"
					+ end_date + " 23:59:59");
			// 制表人
			date.setData("USER", "TEXT", Operator.getName());
			// 表格数据
			TParm parm = new TParm();
			for (int i = 0; i < this.table.getRowCount(); i++) {
				parm.addData("SUP_ABS_DESC", this.table.getItemString(i,
						"SUP_ABS_DESC"));
				parm.addData("WESTERN_MEDICINE", this.table.getItemString(i,
						"WESTERN_MEDICINE"));
				parm.addData("CTPM", this.table.getItemString(i, "CTPM"));
				parm.addData("DISINFECTANT", this.table.getItemString(i,
						"DISINFECTANT"));
//				parm.addData("REAGENT", this.table.getItemString(i, "REAGENT"));
				parm.addData("ALL_TOT1", this.table
						.getItemString(i, "ALL_TOT1"));
				parm.addData("CTPM_RETN", this.table
						.getItemString(i, "CTPM_RETN"));
				parm.addData("WEST_RETN", this.table
						.getItemString(i, "WEST_RETN"));
				parm.addData("BACK_AMT", this.table
						.getItemString(i, "BACK_AMT"));
				parm.addData("ALL_TOT2", this.table
						.getItemString(i, "ALL_TOT2"));
			}
			parm.setCount(parm.getCount("SUP_ABS_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "SUP_ABS_DESC");
			parm.addData("SYSTEM", "COLUMNS", "WESTERN_MEDICINE");
			parm.addData("SYSTEM", "COLUMNS", "CTPM");
			parm.addData("SYSTEM", "COLUMNS", "DISINFECTANT");
//			parm.addData("SYSTEM", "COLUMNS", "REAGENT");
			parm.addData("SYSTEM", "COLUMNS", "ALL_TOT1");
			parm.addData("SYSTEM", "COLUMNS", "CTPM_RETN");
			parm.addData("SYSTEM", "COLUMNS", "WEST_RETN");
			parm.addData("SYSTEM", "COLUMNS", "BACK_AMT");
			parm.addData("SYSTEM", "COLUMNS", "ALL_TOT2");
			date.setData("TABLE", parm.getData());
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\spc\\SPCSupplyRoomUnit.jhw", date);
		} else {
			this.messageBox("没有打印数据");
			return;
		}
	}

	/**
	 * 获得Table对象
	 * 
	 * @param tag
	 *            Table的tag
	 * @return
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 获得TextField对象
	 * 
	 * @param tag
	 *            TextField的tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * 获得TextForma对象
	 * 
	 * @param tag
	 *            TextForma的tag
	 * @return
	 */
	public TTextFormat getTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}
}
