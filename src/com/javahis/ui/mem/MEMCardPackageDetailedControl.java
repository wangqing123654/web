package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * 
 * <p>
 * Title:会员卡套餐销售明细
 * </p>
 * 
 * <p>
 * Description: 会员卡套餐销售明细
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) /p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author huangtt 20141204
 * @version 1.0
 */
public class MEMCardPackageDetailedControl extends TControl {

	private static TTable tableCard;
	private static TTable tablePackage;

	public void onInit() {
		// 初始化查询时间
		onInitDate();

		tableCard = (TTable) this.getComponent("TABLE_CARD");
		tablePackage = (TTable) this.getComponent("TABLE_PACKAGE");

	}

	/**
	 * 初始化时间
	 */
	public void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", date.toString().replace("-", "/")
				.substring(0, 10)
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().replace("-", "/").substring(
				0, 10)
				+ " 23:59:59");
	}

	public void onQuery() {
		String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		if (startDate.length() > 0) {
			startDate = startDate.substring(0, startDate.lastIndexOf("."))
					.replace(":", "").replace("-", "").replace(" ", "");
		}
		if (endDate.length() > 0) {
			endDate = endDate.substring(0, endDate.lastIndexOf(".")).replace(
					":", "").replace("-", "").replace(" ", "");
		}

		TParm cardParm = getCardDetailed(startDate, endDate);
		tableCard.setParmValue(cardParm);
		TParm packageParm = getPackageDetailed(startDate, endDate);
		tablePackage.setParmValue(packageParm);
	}

	public TParm getTable(TParm parm) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm result = new TParm();
		double amtSum = 0.0;
		int countSum = 0;
		double pt1Sum = 0.0;
		double pt6Sum = 0.0;
		double pt2Sum = 0.0;
		double pt5Sum = 0.0;
		double pt7Sum = 0.0;
		double pt9Sum = 0.0;
		double pt10Sum = 0.0;
		for (int i = 0; i < parm.getCount("NAME_DESC"); i++) {
			result.addData("NAME_DESC", parm.getValue("NAME_DESC", i));
			// result.addData(".TableRowLineShow", false);
			result.addData("STYLE", "收入");
			result.addData("AMT", df.format(parm.getDouble("AMT", i)));
			result.addData("COUNT", parm.getInt("COUNT", i));
			result.addData("PT1", df.format(parm.getDouble("PT1", i)));
			result.addData("PT6", df.format(parm.getDouble("PT6", i)));
			result.addData("PT2", df.format(parm.getDouble("PT2", i)));
			result.addData("PT5", df.format(parm.getDouble("PT5", i)));
			result.addData("PT7", df.format(parm.getDouble("PT7", i)));
			result.addData("PT9", df.format(parm.getDouble("PT9", i)));
			result.addData("PT10", df.format(parm.getDouble("PT10", i)));
			result.addData("NAME_DESC", "");
			// result.addData(".TableRowLineShow", false);
			result.addData("STYLE", "退费");
			result.addData("AMT", df.format(parm.getDouble("R_AMT", i)));
			result.addData("COUNT", parm.getInt("R_COUNT", i));
			result.addData("PT1", df.format(parm.getDouble("R_PT1", i)));
			result.addData("PT6", df.format(parm.getDouble("R_PT6", i)));
			result.addData("PT2", df.format(parm.getDouble("R_PT2", i)));
			result.addData("PT5", df.format(parm.getDouble("R_PT5", i)));
			result.addData("PT7", df.format(parm.getDouble("R_PT7", i)));
			result.addData("PT9", df.format(parm.getDouble("R_PT9", i)));
			result.addData("PT10", df.format(parm.getDouble("R_PT10", i)));
			result.addData("NAME_DESC", "");
			// result.addData(".TableRowLineShow", true);
			result.addData("STYLE", "合计");
			result.addData("AMT", df.format(parm.getDouble("AMT", i)
					- parm.getDouble("R_AMT", i)));
			result.addData("COUNT", parm.getInt("COUNT", i)
					+ parm.getInt("R_COUNT", i));
			result.addData("PT1", df.format(parm.getDouble("PT1", i)
					- parm.getDouble("R_PT1", i)));
			result.addData("PT6", df.format(parm.getDouble("PT6", i)
					- parm.getDouble("R_PT6", i)));
			result.addData("PT2", df.format(parm.getDouble("PT2", i)
					- parm.getDouble("R_PT2", i)));
			result.addData("PT5", df.format(parm.getDouble("PT5", i)
					- parm.getDouble("R_PT5", i)));
			result.addData("PT7", df.format(parm.getDouble("PT7", i)
					- parm.getDouble("R_PT7", i)));
			result.addData("PT9", df.format(parm.getDouble("PT9", i)
					- parm.getDouble("R_PT9", i)));
			result.addData("PT10", df.format(parm.getDouble("PT10", i)
					- parm.getDouble("R_PT10", i)));
			amtSum = amtSum + parm.getDouble("AMT", i)
					- parm.getDouble("R_AMT", i);
			countSum = countSum + parm.getInt("COUNT", i)
					+ parm.getInt("R_COUNT", i);
			pt1Sum = pt1Sum + parm.getDouble("PT1", i)
					- parm.getDouble("R_PT1", i);
			pt6Sum = pt6Sum + parm.getDouble("PT6", i)
					- parm.getDouble("R_PT6", i);
			pt2Sum = pt2Sum + parm.getDouble("PT2", i)
					- parm.getDouble("R_PT2", i);
			pt5Sum = pt5Sum + parm.getDouble("PT5", i)
					- parm.getDouble("R_PT5", i);
			pt7Sum = pt7Sum + parm.getDouble("PT7", i)
					- parm.getDouble("R_PT7", i);
			pt9Sum = pt9Sum + parm.getDouble("PT9", i)
					- parm.getDouble("R_PT9", i);
			pt10Sum = pt10Sum + parm.getDouble("PT10", i)
					- parm.getDouble("R_PT10", i);
		}
		result.addData("NAME_DESC", "总计");
		result.addData("STYLE", "");
		result.addData("AMT", df.format(amtSum));
		result.addData("COUNT", countSum);
		result.addData("PT1", df.format(pt1Sum));
		result.addData("PT6", df.format(pt6Sum));
		result.addData("PT2", df.format(pt2Sum));
		result.addData("PT5", df.format(pt5Sum));
		result.addData("PT7", df.format(pt7Sum));
		result.addData("PT9", df.format(pt9Sum));
		result.addData("PT10", df.format(pt10Sum));
		result.setCount(result.getCount("NAME_DESC"));
		result.addData("SYSTEM", "COLUMNS", "NAME_DESC");
		result.addData("SYSTEM", "COLUMNS", "STYLE");
		result.addData("SYSTEM", "COLUMNS", "COUNT");
		result.addData("SYSTEM", "COLUMNS", "AMT");
		result.addData("SYSTEM", "COLUMNS", "PT1");
		result.addData("SYSTEM", "COLUMNS", "PT6");
		result.addData("SYSTEM", "COLUMNS", "PT2");
		result.addData("SYSTEM", "COLUMNS", "PT5");
		result.addData("SYSTEM", "COLUMNS", "PT7");
		result.addData("SYSTEM", "COLUMNS", "PT9");
		result.addData("SYSTEM", "COLUMNS", "PT10");

		return result;

	}

	public void onPrint() {
		tableCard.acceptText();
		tablePackage.acceptText();
		TParm parmCard = tableCard.getParmValue();
		TParm parmPackage = tablePackage.getParmValue();
		TParm card = new TParm();
		TParm pack = new TParm();
		TParm packTitle = new TParm();
		if (parmCard.getCount("NAME_DESC") > 0
				|| parmPackage.getCount("NAME_DESC") > 0) {
			card = this.getTable(parmCard);
			pack = this.getTable(parmPackage);
			// packTitle.addData("TITLE", "套餐销售日结明细");
			// packTitle.setCount(packTitle.getCount("TITLE"));
			// packTitle.addData("SYSTEM", "COLUMNS", "TITLE");
		} else {
			this.messageBox("没有要打印的数据！");
			return;
		}
		TParm printParm = new TParm();
		printParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		printParm.setData("TITLE2", "TEXT", "门诊预收款日结-附表");
		printParm.setData("CARD_TITLE", "TEXT", "会员卡销售日结明细");
		Timestamp date = SystemTool.getInstance().getDate();
		printParm.setData("date1", "TEXT", "打印日期："
				+ date.toString().replace("-", "/").substring(0,
						date.toString().length() - 2));
		String startDate = getValueString("START_DATE");
		String endDate = getValueString("END_DATE");
		startDate = startDate.substring(0, startDate.lastIndexOf(".")).replace(
				"-", "/");
		endDate = endDate.substring(0, endDate.lastIndexOf(".")).replace("-",
				"/");
		printParm.setData("date2", "TEXT", "结算日期：" + startDate + " ~ "
				+ endDate);
		printParm.setData("tableCard", card.getData());
		// printParm.setData("packTitle", packTitle.getData());
		printParm.setData("tablePackage", pack.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\MEM\\MEMCardPackageDetailed.jhw",
				printParm, false);

	}

	public void onClear() {
		this.clearValue("START_DATE;END_DATE");
		onInitDate();
		tableCard.removeRowAll();
		tablePackage.removeRowAll();
	}

	/**
	 * 得到套餐销售明细
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getPackageDetailed(String sDate, String eDate) {
		// String
		// sql="SELECT CASE WHEN A.PACKAGE_DESC IS NULL THEN B.PACKAGE_DESC ELSE A.PACKAGE_DESC END AS NAME_DESC, "
		// +
		// "  A.AMT, A.COUNT, " +
		// " A.PT1, A.PT6, A.PT2, A.PT5, A.PT7, B.AMT R_AMT," +
		// " B.COUNT R_COUNT, B.PT1 R_PT1, B.PT6 R_PT6, " +
		// " B.PT2 R_PT2, B.PT5 R_PT5, B.PT7 R_PT7" +
		// " FROM (SELECT   A.PACKAGE_CODE, A.PACKAGE_DESC, SUM (AR_AMT) AMT," +
		// " COUNT (1) COUNT, SUM (PAY_TYPE01) PT1, SUM (PAY_TYPE06) PT6," +
		// " SUM (PAY_TYPE02) PT2, SUM (PAY_TYPE05) PT5, SUM (PAY_TYPE07) PT7" +
		// " FROM (SELECT DISTINCT C.PACKAGE_CODE, A.TRADE_NO, C.PACKAGE_DESC" +
		// " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C"
		// +
		// " WHERE A.TRADE_NO = B.TRADE_NO" +
		// " AND A.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')" +
		// " AND TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS' )" +
		// " AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
		// " AND A.REST_FLAG = 'N') A," +
		// " MEM_PACKAGE_TRADE_M B" +
		// " WHERE A.TRADE_NO = B.TRADE_NO" +
		// " GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC) A" +
		// " FULL OUTER JOIN" +
		// " (SELECT   A.PACKAGE_CODE, A.PACKAGE_DESC, ABS(SUM (AR_AMT)) AMT," +
		// " COUNT (1) COUNT, ABS(SUM (AR_AMT)) PT1, ABS(SUM (PAY_TYPE06)) PT6,"
		// +
		// " ABS(SUM (PAY_TYPE02)) PT2, ABS(SUM (PAY_TYPE05)) PT5, ABS(SUM (PAY_TYPE07)) PT7"
		// +
		// " FROM (SELECT DISTINCT C.PACKAGE_CODE, A.TRADE_NO, C.PACKAGE_DESC" +
		// " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C"
		// +
		// " WHERE A.TRADE_NO = B.REST_TRADE_NO" +
		// " AND A.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS' )" +
		// " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" +
		// " AND B.PACKAGE_CODE = C.PACKAGE_CODE" +
		// " AND A.REST_FLAG = 'Y') A," +
		// " MEM_PACKAGE_TRADE_M B" +
		// " WHERE A.TRADE_NO = B.TRADE_NO" +
		// " GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC) B" +
		// " ON A.PACKAGE_CODE = B.PACKAGE_CODE";
		// System.out.println(sql);
		// TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String sql = "SELECT   A.PACKAGE_CODE, A.PACKAGE_DESC NAME_DESC, SUM (AR_AMT) AMT,"
				+ " COUNT (1) COUNT, SUM (PAY_TYPE01) PT1, SUM (PAY_TYPE06) PT6,"
				+ " SUM (PAY_TYPE02) PT2, SUM (PAY_TYPE05) PT5, SUM (PAY_TYPE07) PT7,"
				+ "SUM (PAY_TYPE09) PT9,SUM (PAY_TYPE10) PT10,"
				+ " '' R_AMT,0 R_COUNT, '' R_PT1, '' R_PT6, "
				+ " '' R_PT2, '' R_PT5, '' R_PT7,'' R_PT9,'' R_PT10"
				+ " FROM (SELECT DISTINCT C.PACKAGE_CODE, A.TRADE_NO, C.PACKAGE_DESC"
				+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C"
				+ " WHERE A.TRADE_NO = B.TRADE_NO"
				+
				// " AND A.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')"
				// +
				// " AND TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS' )" +
				"  AND A.ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ FROM EKT_ACCOUNT"
				+ " WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ sDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ eDate
				+ "', 'YYYYMMDDHH24MISS'))"
				+ " AND B.PACKAGE_CODE = C.PACKAGE_CODE"
				+ " AND A.REST_FLAG = 'N') A,"
				+ " MEM_PACKAGE_TRADE_M B"
				+ " WHERE A.TRADE_NO = B.TRADE_NO"
				+ " GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC";

		String r_sql = "SELECT   A.PACKAGE_CODE, A.PACKAGE_DESC NAME_DESC, ABS(SUM (AR_AMT)) AMT,"
				+ " COUNT (1) COUNT, ABS(SUM (PAY_TYPE01)) PT1, ABS(SUM (PAY_TYPE06)) PT6,"
				+ " ABS(SUM (PAY_TYPE02)) PT2, ABS(SUM (PAY_TYPE05)) PT5, ABS(SUM (PAY_TYPE07)) PT7, "
				+ "ABS(SUM (PAY_TYPE09)) PT9,ABS(SUM (PAY_TYPE10)) PT10"
				+ " FROM (SELECT DISTINCT C.PACKAGE_CODE, A.TRADE_NO, C.PACKAGE_DESC"
				+ " FROM MEM_PACKAGE_TRADE_M A, MEM_PAT_PACKAGE_SECTION B, MEM_PACKAGE C"
				+ " WHERE A.TRADE_NO = B.REST_TRADE_NO"
				+
				// " AND A.OPT_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS' )"
				// +
				// " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" +
				" AND A.ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ FROM EKT_ACCOUNT"
				+ " WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ sDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ eDate
				+ "', 'YYYYMMDDHH24MISS'))"
				+ " AND B.PACKAGE_CODE = C.PACKAGE_CODE"
				+ " AND A.REST_FLAG = 'Y') A,"
				+ " MEM_PACKAGE_TRADE_M B"
				+ " WHERE A.TRADE_NO = B.TRADE_NO"
				+ " GROUP BY A.PACKAGE_CODE, A.PACKAGE_DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm r_parm = new TParm(TJDODBTool.getInstance().select(r_sql));
		for (int i = 0; i < parm.getCount(); i++) {
			int row = -1;
			for (int j = 0; j < r_parm.getCount(); j++) {
				if (parm.getValue("PACKAGE_CODE", i).equalsIgnoreCase(
						r_parm.getValue("PACKAGE_CODE", j))) {
					row = j;
				}
			}
			if (row > -1) {
				parm.setData("R_AMT", i, r_parm.getData("AMT", row));
				parm.setData("R_COUNT", i, r_parm.getData("COUNT", row));
				parm.setData("R_PT1", i, r_parm.getData("PT1", row));
				parm.setData("R_PT6", i, r_parm.getData("PT6", row));
				parm.setData("R_PT2", i, r_parm.getData("PT2", row));
				parm.setData("R_PT5", i, r_parm.getData("PT5", row));
				parm.setData("R_PT7", i, r_parm.getData("PT7", row));
				parm.setData("R_PT9", i, r_parm.getData("PT9", row));
				parm.setData("R_PT10", i, r_parm.getData("PT10", row));
				r_parm.removeRow(row);
			}
		}
		// System.out.println("r_parm--"+r_parm);
		// System.out.println("parm=="+parm);
		for (int i = 0; i < r_parm.getCount(); i++) {
			parm.addData("PACKAGE_CODE", r_parm.getData("PACKAGE_CODE", i));
			parm.addData("NAME_DESC", r_parm.getData("NAME_DESC", i));
			parm.addData("AMT", "");
			parm.addData("COUNT", "");
			parm.addData("PT1", "");
			parm.addData("PT6", "");
			parm.addData("PT2", "");
			parm.addData("PT5", "");
			parm.addData("PT7", "");
			parm.addData("PT9", "");
			parm.addData("PT10", "");
			parm.addData("R_AMT", r_parm.getData("AMT", i));
			parm.addData("R_COUNT", r_parm.getData("COUNT", i));
			parm.addData("R_PT1", r_parm.getData("PT1", i));
			parm.addData("R_PT6", r_parm.getData("PT6", i));
			parm.addData("R_PT2", r_parm.getData("PT2", i));
			parm.addData("R_PT5", r_parm.getData("PT5", i));
			parm.addData("R_PT7", r_parm.getData("PT7", i));
			parm.addData("R_PT9", r_parm.getData("PT9", i));
			parm.addData("R_PT10", r_parm.getData("PT10", i));
		}
		parm.setCount(parm.getCount("NAME_DESC"));
		return parm;
	}

	/**
	 * 得到会员卡销售明细
	 * 
	 * @param sDate
	 * @param eDate
	 * @return
	 */
	public TParm getCardDetailed(String sDate, String eDate) {
		String sql = "SELECT CASE WHEN A.MEM_CARD_DESC IS NULL THEN B.MEM_CARD_DESC ELSE A.MEM_CARD_DESC END AS NAME_DESC, "
				+ " A.AMT, A.COUNT, A.PT1, A.PT6, A.PT2, A.PT5,"
				+ " A.PT7,A.PT9,A.PT10, B.MEM_CARD_DESC, B.AMT R_AMT, B.COUNT R_COUNT, B.PT1 R_PT1,"
				+ " B.PT6 R_PT6, B.PT2 R_PT2, B.PT5 R_PT5, B.PT7 R_PT7, B.PT9 R_PT9, B.PT10 R_PT10"
				+ " FROM (SELECT   SUM (MEM_FEE) AMT, COUNT (1) COUNT, SUM (PAY_TYPE01) PT1,"
				+ " SUM (PAY_TYPE06) PT6, SUM (PAY_TYPE02) PT2,"
				+ " SUM (PAY_TYPE05) PT5, SUM (PAY_TYPE07) PT7,SUM (PAY_TYPE09) PT9,"
				+ "SUM (PAY_TYPE10) PT10, MEM_CARD,MEM_CARD_DESC"
				+ " FROM (SELECT B.MEM_CARD, B.MEM_DESC MEM_CARD_DESC, B.DESCRIPTION, A.*"
				+ "  FROM MEM_TRADE A, MEM_MEMBERSHIP_INFO B"
				+ "  WHERE A.MEM_CODE = B.MEM_CODE AND A.STATUS = '1'"
				+
				// "  AND A.SALE_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')"
				// +
				// "  AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS'))" +
				"  AND A.ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ FROM EKT_ACCOUNT"
				+ " WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ sDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ eDate
				+ "', 'YYYYMMDDHH24MISS'))"
				+ " ) GROUP BY MEM_CARD, MEM_CARD_DESC) A"
				+ " FULL OUTER JOIN"
				+ " (SELECT  ABS(SUM (MEM_FEE)) AMT, COUNT (1) COUNT, ABS(SUM (PAY_TYPE01)) AS PT1,"
				+ " ABS(SUM (PAY_TYPE06)) PT6, ABS(SUM (PAY_TYPE02)) PT2,"
				+ " ABS(SUM (PAY_TYPE05)) PT5, ABS(SUM (PAY_TYPE07)) PT7,ABS(SUM (PAY_TYPE09)) PT9,"
				+ "ABS(SUM (PAY_TYPE10)) PT10, MEM_CARD,MEM_CARD_DESC"
				+ " FROM (SELECT B.MEM_CARD, B.MEM_DESC MEM_CARD_DESC, B.DESCRIPTION,"
				+ " A.STATUS, A.MEM_FEE,"
				+ "  A.PAY_TYPE01,"
				+ " A.PAY_TYPE02, A.PAY_TYPE03, A.PAY_TYPE04,"
				+ " A.PAY_TYPE05, A.PAY_TYPE06, A.PAY_TYPE07,"
				+ " A.PAY_TYPE08, A.PAY_TYPE09, A.PAY_TYPE10"
				+ " FROM MEM_TRADE A, MEM_MEMBERSHIP_INFO B"
				+ " WHERE A.MEM_CODE = B.MEM_CODE"
				+ " AND A.STATUS IN ('2', '3')"
				+ " AND A.REMOVE_FLG = 'N'"
				+
				// " AND A.OPT_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')"
				// +
				// " AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS'))" +
				"  AND A.ACCOUNT_SEQ IN (SELECT ACCOUNT_SEQ FROM EKT_ACCOUNT"
				+ " WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"
				+ sDate
				+ "', 'YYYYMMDDHH24MISS')"
				+ " AND TO_DATE ('"
				+ eDate
				+ "', 'YYYYMMDDHH24MISS'))"
				+ " ) GROUP BY MEM_CARD, MEM_CARD_DESC) B ON A.MEM_CARD = B.MEM_CARD";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}

}
