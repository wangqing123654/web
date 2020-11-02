package com.javahis.ui.reg;

import java.text.DecimalFormat;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 客户汇总表
 * </p>
 * 
 * <p>
 * Description: 客户汇总表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author Huzc 2015.12.24
 * @version 1.0
 */
public class REGCustomerCollectControl extends TControl {
	private TTable table_o;
	private TTable table_i;
	private TTextFormat abc;
	boolean isDebug = true;
	public REGCustomerCollectControl() {
	}

	/**
	 * 初始化
	 * 
	 * @author Huzc
	 */
	public void onInit() {
		String date = StringTool.getString(
				TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd");
		this.setValue("MONTH_START", date);
		this.setValue("MONTH_END", date);
		super.onInit();
		table_o = (TTable) this.getComponent("TABLE_O");
		table_i = (TTable) this.getComponent("TABLE_I");
		abc = (TTextFormat) this.getComponent("ABC");
	}

	/**
	 * 查询
	 * 
	 * @author Huzc
	 */
	public void onQuery() {
		try{
			String monthstart = getValueString("MONTH_START").toString().substring(
					0, 10).replaceAll("-", "");
			String monthend = getValueString("MONTH_END").toString()
					.substring(0, 10).replaceAll("-", "");
	
			String bilsql = " SELECT TIME , COUNT(*) AS COUNT1 , SUM(AR_AMT) AS AR_AMT1 , SUM(PAY_TYPE01) AS PAY_CASH1 , SUM(PAY_TYPE02) AS PAY_CARD1 ,SUM(PAY_TYPE03) AS PAY_TYPE03 , SUM(PAY_TYPE04) AS PAY_TYPE04, " +
					"SUM(PAY_TYPE05) AS PAY_TYPE05, SUM(PAY_TYPE06) AS PAY_TYPE06, SUM(PAY_TYPE07) AS PAY_TYPE07, SUM(PAY_TYPE08) AS PAY_TYPE08, SUM(PAY_TYPE09) AS PAY_TYPE09 , SUM(PAY_TYPE10) AS PAY_TYPE10  "
					+ " FROM(SELECT TO_CHAR(BILL_DATE,'YYYY/MM') AS TIME , AR_AMT ,PAY_TYPE01 ,PAY_TYPE02, PAY_TYPE03, PAY_TYPE04, PAY_TYPE05, PAY_TYPE06, PAY_TYPE07, PAY_TYPE08,PAY_TYPE09,PAY_TYPE10 "
					+ " FROM BIL_OPB_RECP "
					+ " WHERE BILL_DATE BETWEEN TO_DATE(' "
					+ monthstart
					+ " 00:00:00','YYYYMMdd HH24:mi:SS') "
					+ " AND TO_DATE(' "
					+ monthend
					+ " 23:59:59','YYYYMMdd HH24:mi:SS') "
					+ " AND ADM_TYPE <> 'H' "
					+ " AND MEM_PACK_FLG = 'N') "
					+ " GROUP BY TIME "
					+ " ORDER BY TIME ";
	
			String memsql = " SELECT TIME , COUNT(*) AS COUNT2 , SUM(AR_AMT) AS AR_AMT2 , SUM(PAY_TYPE01) AS PAY_CASH2 , SUM(PAY_TYPE02) AS PAY_CARD2 , SUM(PAY_TYPE03) AS PAY_TYPE03 , " +
					"SUM(PAY_TYPE04) AS PAY_TYPE04, SUM(PAY_TYPE05) AS PAY_TYPE05, SUM(PAY_TYPE06) AS PAY_TYPE06, SUM(PAY_TYPE07) AS PAY_TYPE07, SUM(PAY_TYPE08) AS PAY_TYPE08, SUM(PAY_TYPE09) AS PAY_TYPE09 , SUM(PAY_TYPE10) AS PAY_TYPE10 "
					+ " FROM(SELECT TO_CHAR(BILL_DATE , 'YYYY/MM') AS TIME, AR_AMT , PAY_TYPE01 ,PAY_TYPE02, PAY_TYPE03, PAY_TYPE04, PAY_TYPE05, PAY_TYPE06, PAY_TYPE07, PAY_TYPE08,PAY_TYPE09,PAY_TYPE10 "
					+ " FROM MEM_PACKAGE_TRADE_M "
					+ " WHERE BILL_DATE BETWEEN TO_DATE(' "
					+ monthstart
					+ " 00:00:00','YYYYMMdd HH24:mi:SS') "
					+ " AND TO_DATE(' "
					+ monthend
					+ " 23:59:59','YYYYMMdd HH24:mi:SS')) " + " GROUP BY TIME " + " ORDER BY TIME ";
			/*String insql = " SELECT TIME, COUNT (*) AS COUNT3, SUM(AR_AMT) AS AR_AMT3 "
		                 + " FROM (SELECT TIME,CASE_NO,SUM(PRE_AMT) AS AR_AMT "
		                 + " FROM (  SELECT TO_CHAR (A.IN_DATE, 'YYYY/MM') AS TIME, "
		                 + " A.CASE_NO, "
		                 + " B.PRE_AMT "
		                 + " FROM ADM_INP A, BIL_PAY B "
		                 + " WHERE     A.IN_DATE BETWEEN TO_DATE (' "
		                 + monthstart
		                 + " 00:00:00','YYYYMMdd HH24:mi:SS') " 
		                 + " AND TO_DATE (' " 
		                 + monthend
		                 + " 23:59:59','YYYYMMdd HH24:mi:SS') "
		                 + " AND A.CASE_NO = B.CASE_NO(+) " 
		                 + " AND A.BILL_STATUS <> '4' "
		                 + " ) GROUP BY TIME,CASE_NO "
		                 + " UNION "
		                 + "  SELECT TIME, CASE_NO, SUM (PAY_BILPAY) AS AR_AMT "
		                 + " FROM (SELECT TO_CHAR (A.IN_DATE, 'YYYY/MM') AS TIME, "
		                 + "  A.CASE_NO, "
		                 + "  B.PAY_BILPAY "
		                 + "  FROM ADM_INP A, BIL_IBS_RECPM B "
		                 + "  WHERE     A.IN_DATE BETWEEN TO_DATE (' "
		                 + monthstart
		                 + " 00:00:00','YYYYMMdd HH24:mi:SS') "
		                 + "  AND TO_DATE (' "
		                 + monthend
		                 + " 23:59:59','YYYYMMdd HH24:mi:SS') "
		                 + "  AND A.CASE_NO = B.CASE_NO "
		                 + "  AND A.BILL_STATUS = '4') "
		                 + "  GROUP BY TIME, CASE_NO) "
		                 + " GROUP BY TIME " 
		                 + " ORDER BY TIME ";*/
			String insql = " WITH AA "+
				" AS (  SELECT TIME, COUNT (TIME) COUNT3 "+
				" FROM (SELECT TO_CHAR (IN_DATE, 'yyyy/MM') AS TIME "+
				" FROM ADM_INP "+
				" WHERE IN_DATE BETWEEN TO_DATE ('"+monthstart+" 00:00:00', "+
				" 'yyyy/MM/dd HH24:mi:ss') "+
				" AND TO_DATE ('"+monthend+" 23:59:59', "+
				" 'yyyyMMdd HH24:mi:ss')) "+
				" GROUP BY TIME "+
				" ORDER BY TIME), "+
				" BB "+
				" AS (  SELECT TIME, SUM (PRE_AMT) AS AR_AMT "+
				" FROM (SELECT TO_CHAR (CHARGE_DATE, 'yyyy/MM') AS TIME, PRE_AMT "+
				" FROM BIL_PAY "+
				" WHERE     CHARGE_DATE BETWEEN TO_DATE ( "+
				" '"+monthstart+" 00:00:00', "+
				" 'yyyyMMdd HH24:mi:ss') "+
				" AND TO_DATE ( "+
				" '"+monthend+" 23:59:59', "+
				" 'yyyyMMdd HH24:mi:ss') "+
				" AND TRANSACT_TYPE IN ('01', '02', '04') "+
				" AND PAY_TYPE <> 'TCJZ') "+
				" GROUP BY TIME "+
				" ORDER BY TIME) "+
				" SELECT AA.*, BB.AR_AMT AR_AMT3 "+
				" FROM AA, BB "+
				" WHERE AA.TIME = BB.TIME ";
			
			/*String outsql =  " SELECT TIME, COUNT (*) COUNT4, SUM(AR_AMT) AS AR_AMT , SUM(PAY_BILPAY) AS PAY_BILPAY , SUM(REDUCE_AMT) AS REDUCE_AMT "  
		                  +  " FROM (  SELECT TIME,CASE_NO,SUM (AR_AMT) AS AR_AMT , SUM (PAY_BILPAY) AS PAY_BILPAY,  SUM (REDUCE_AMT) AS REDUCE_AMT " 
		                  +  "   FROM (SELECT TO_CHAR (A.BILL_DATE, 'YYYY/MM') AS TIME, " 
		                  +  "   A.CASE_NO, " 
		                  +  "   B.AR_AMT, " 
		                  +  "   B.PAY_BILPAY, " 
		                  +  "   B.REDUCE_AMT " 
		                  +  "   FROM ADM_INP A, BIL_IBS_RECPM B " 
		                  +  "   WHERE     A.CASE_NO = B.CASE_NO " 
		                  +  "   AND A.BILL_DATE BETWEEN TO_DATE (' " 
		                  +  monthstart
		                  +  " 00:00:00','YYYYMMdd HH24:mi:SS') " 
		                  +  "   AND TO_DATE (' "
		                  +  monthend
		                  +  " 23:59:59','YYYYMMdd HH24:mi:SS') " 
		                  +  "  AND A.BILL_STATUS = '4' ) "  
		                  +  "   GROUP BY TIME, CASE_NO) " 
		                  +  "   GROUP BY TIME " 
		                  +  "   ORDER BY TIME ";*/
			/*String outsql = " SELECT TIME, "+
							" COUNT (TIME) COUNT4, "+
							" SUM (AR_AMT) AS AR_AMT, "+
							" SUM (PAY_BILPAY) AS PAY_BILPAY, "+
							" SUM (REDUCE_AMT) AS REDUCE_AMT "+
							" FROM (SELECT TO_CHAR (CHARGE_DATE, 'yyyy/MM') AS TIME, "+
							" AR_AMT, "+
							" PAY_BILPAY, "+
							" REDUCE_AMT "+
							" FROM BIL_IBS_RECPM "+
							" WHERE CHARGE_DATE BETWEEN TO_DATE ('"+monthstart+" 00:00:00', "+
							" 'yyyyMMdd HH24:mi:ss') "+
							" AND TO_DATE ('"+monthend+" 23:59:59', "+
							" 'yyyyMMdd HH24:mi:ss')) "+
							" GROUP BY TIME "+
							" ORDER BY TIME";*/
			String outsql = " SELECT TIME," +
					" COUNT (TIME) COUNT4," +
					" SUM(PAY_CASH)+SUM(PAY_CHECK)+SUM(PAY_BANK_CARD)+SUM(PAY_GIFT_CARD)+SUM(PAY_DISCNT_CARD)+SUM(PAY_DEBIT)+SUM(PAY_BXZF) AR_AMT4" +
					" FROM (SELECT TO_CHAR (CHARGE_DATE, 'yyyy/MM') AS TIME," +
					" CASE WHEN PAY_CASH IS NULL THEN 0 ELSE PAY_CASH END PAY_CASH," +
					" CASE WHEN PAY_CHECK IS NULL THEN 0 ELSE PAY_CHECK END PAY_CHECK," +
					" CASE WHEN PAY_BANK_CARD IS NULL THEN 0 ELSE PAY_BANK_CARD END PAY_BANK_CARD," +
					" CASE WHEN PAY_GIFT_CARD IS NULL THEN 0 ELSE PAY_GIFT_CARD END PAY_GIFT_CARD," +
					" CASE WHEN PAY_DISCNT_CARD IS NULL THEN 0 ELSE PAY_DISCNT_CARD END PAY_DISCNT_CARD," +
					" CASE WHEN PAY_DEBIT IS NULL THEN 0 ELSE PAY_DEBIT END PAY_DEBIT," +
					" CASE WHEN PAY_BXZF IS NULL THEN 0 ELSE PAY_BXZF END PAY_BXZF" +
					" FROM BIL_IBS_RECPM" +
					" WHERE CHARGE_DATE BETWEEN TO_DATE ('"+monthstart+" 00:00:00','yyyyMMdd HH24:mi:ss')" +
					" AND TO_DATE ('"+monthend+" 23:59:59','yyyyMMdd HH24:mi:ss')" +
					//" AND RESET_RECEIPT_NO IS NULL" +
					" UNION ALL" +
					" SELECT TO_CHAR (REFUND_DATE, 'yyyy/MM') AS TIME," +
					" CASE WHEN PAY_CASH IS NULL THEN 0 ELSE PAY_CASH END PAY_CASH," +
					" CASE WHEN PAY_CHECK IS NULL THEN 0 ELSE PAY_CHECK END PAY_CHECK," +
					" CASE WHEN PAY_BANK_CARD IS NULL THEN 0 ELSE PAY_BANK_CARD END PAY_BANK_CARD," +
					" CASE WHEN PAY_GIFT_CARD IS NULL THEN 0 ELSE PAY_GIFT_CARD END PAY_GIFT_CARD," +
					" CASE WHEN PAY_DISCNT_CARD IS NULL THEN 0 ELSE PAY_DISCNT_CARD END PAY_DISCNT_CARD," +
					" CASE WHEN PAY_DEBIT IS NULL THEN 0 ELSE PAY_DEBIT END PAY_DEBIT," +
					" CASE WHEN PAY_BXZF IS NULL THEN 0 ELSE PAY_BXZF END PAY_BXZF" +
					" FROM BIL_IBS_RECPM" +
					" WHERE REFUND_DATE BETWEEN TO_DATE ('"+monthstart+" 00:00:00','yyyyMMdd HH24:mi:ss')" +
					" AND TO_DATE ('"+monthend+" 23:59:59','yyyyMMdd HH24:mi:ss')" +
					" AND RESET_RECEIPT_NO IS NOT NULL" +
					" AND CHARGE_DATE < TO_DATE ('"+monthstart+" 00:00:00','yyyyMMdd HH24:mi:ss') )" +
					" GROUP BY TIME" +
					" ORDER BY TIME";
			//System.out.println("insql::"+insql);
			System.out.println("outsql::"+outsql);
			//System.out.println("ssss:"+sql);
			TParm bilparm = new TParm(TJDODBTool.getInstance().select(bilsql));
			TParm memparm = new TParm(TJDODBTool.getInstance().select(memsql));
			TParm inparm = new TParm(TJDODBTool.getInstance().select(insql));
			TParm outparm = new TParm(TJDODBTool.getInstance().select(outsql));
	
			TParm parm = new TParm();
			TParm parm1 = new TParm();
	
			int sum1 = 0;
			double sum2 = 0;
			int sum3 = 0;
			double sum4 = 0;
			double sum5 = 0;
			double sum6 = 0;
			double sum7 = 0;
			double sum03 = 0;
			double sum04 = 0;
			double sum05 = 0;
			double sum06 = 0;
			double sum07 = 0;
			double sum08 = 0;
			double sum09 = 0;
			double sum10 = 0;
			if (bilparm.getCount() < 1 & memparm.getCount() < 1) {
				this.messageBox("门诊查无数据");
			} else {
				DecimalFormat df = new DecimalFormat("0.00");
				for (int i = 0; i < bilparm.getCount("TIME"); i++) {
					parm.addData("TIME", bilparm.getValue("TIME", i));
					parm.addData("COUNT1", bilparm.getInt("COUNT1", i));
					parm.addData("AR_AMT1", bilparm.getDouble("AR_AMT1", i));
					parm.addData("COUNT2", memparm.getInt("COUNT2", i));
					parm.addData("AR_AMT2", memparm.getDouble("AR_AMT2", i));
					parm.addData("PAY_WAY", "现金:"+df.format(bilparm.getDouble("PAY_CASH1", i)+ memparm.getDouble("PAY_CASH2", i))
					                       +" 刷卡:"+df.format(bilparm.getDouble("PAY_CARD1", i)+ memparm.getDouble("PAY_CARD2", i)));
					
				    if(bilparm.getDouble("PAY_TYPE03", i) != 0 || memparm.getDouble("PAY_TYPE03", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 汇票:"+df.format(bilparm.getDouble("PAY_TYPE03", i)+ memparm.getDouble("PAY_TYPE03", i)));
					}
					if(bilparm.getDouble("PAY_TYPE04", i) != 0 || memparm.getDouble("PAY_TYPE04", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 应收款:"+df.format(bilparm.getDouble("PAY_TYPE04", i)+ memparm.getDouble("PAY_TYPE04", i)));
					}
					if(bilparm.getDouble("PAY_TYPE05", i) != 0 || memparm.getDouble("PAY_TYPE05", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 礼品卡:"+df.format(bilparm.getDouble("PAY_TYPE05", i)+ memparm.getDouble("PAY_TYPE05", i)));
					} 
					if(bilparm.getDouble("PAY_TYPE06", i) != 0 || memparm.getDouble("PAY_TYPE06", i) != 0){
						parm.setData("PAY_WAY", i,parm.getValue("PAY_WAY", i) + " 支票:"+df.format(bilparm.getDouble("PAY_TYPE06", i)+ memparm.getDouble("PAY_TYPE06", i)));
					}
					if(bilparm.getDouble("PAY_TYPE07", i) != 0 || memparm.getDouble("PAY_TYPE07", i) != 0){
						parm.setData("PAY_WAY", i,parm.getValue("PAY_WAY", i) + " 代金券:"+df.format(bilparm.getDouble("PAY_TYPE07", i)+ memparm.getDouble("PAY_TYPE07", i)));
					}
					if(bilparm.getDouble("PAY_TYPE08", i) != 0 || memparm.getDouble("PAY_TYPE08", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 保险直付:"+df.format(bilparm.getDouble("PAY_TYPE08", i)+ memparm.getDouble("PAY_TYPE08", i)));
					}
					if(bilparm.getDouble("PAY_TYPE09", i) != 0 || memparm.getDouble("PAY_TYPE09", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 微信:"+df.format(bilparm.getDouble("PAY_TYPE09", i)+ memparm.getDouble("PAY_TYPE09", i)));
					}
					if(bilparm.getDouble("PAY_TYPE10", i) != 0 || memparm.getDouble("PAY_TYPE10", i) != 0){
						parm.setData("PAY_WAY",i, parm.getValue("PAY_WAY", i) + " 支付宝:"+df.format(bilparm.getDouble("PAY_TYPE10", i)+ memparm.getDouble("PAY_TYPE10", i)));
					}
					parm.addData("AMT", bilparm.getDouble("AR_AMT1", i)+ memparm.getDouble("AR_AMT2", i));
	
					int count1 = parm.getInt("COUNT1", i);
					double aramt1 = parm.getDouble("AR_AMT1", i);
					int count2 = parm.getInt("COUNT2", i);
					double aramt2 = parm.getDouble("AR_AMT2", i);
					double paycash = bilparm.getDouble("PAY_CASH1", i)+ memparm.getDouble("PAY_CASH2", i);
					double paycard = bilparm.getDouble("PAY_CARD1", i)+ memparm.getDouble("PAY_CARD2", i);
					double type03 = bilparm.getDouble("PAY_TYPE03", i)+ memparm.getDouble("PAY_TYPE03", i);
					double type04 = bilparm.getDouble("PAY_TYPE04", i)+ memparm.getDouble("PAY_TYPE04", i);
					double type05 = bilparm.getDouble("PAY_TYPE05", i)+ memparm.getDouble("PAY_TYPE05", i);
					double type06 = bilparm.getDouble("PAY_TYPE06", i)+ memparm.getDouble("PAY_TYPE06", i);
					double type07 = bilparm.getDouble("PAY_TYPE07", i)+ memparm.getDouble("PAY_TYPE07", i);
					double type08 = bilparm.getDouble("PAY_TYPE08", i)+ memparm.getDouble("PAY_TYPE08", i);
					double type09 = bilparm.getDouble("PAY_TYPE09", i)+ memparm.getDouble("PAY_TYPE09", i);
					double type10 = bilparm.getDouble("PAY_TYPE10", i)+ memparm.getDouble("PAY_TYPE10", i);
					double amt = parm.getDouble("AMT", i);
	
					sum1 += count1;
					sum2 += aramt1;
					sum3 += count2;
					sum4 += aramt2;
					sum5 += paycash;
					sum6 += paycard;
					sum7 += amt;
					sum03 += type03;
					sum04 += type04;
					sum05 += type05;
					sum06 += type06;
					sum07 += type07;
					sum08 += type08;
					sum09 += type09;
					sum10 += type10;
				}
				parm.addData("TIME", "总计");
				parm.addData("COUNT1", sum1);
				parm.addData("AR_AMT1", sum2);
				parm.addData("COUNT2", sum3);
				parm.addData("AR_AMT2", sum4);
				parm.addData("PAY_WAY", "现金:"+df.format(sum5)+" 刷卡:"+df.format(sum6));
				int countin = bilparm.getCount();
				if(sum03 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 汇票:"+df.format(sum03));
				}
				if(sum04 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 应收款:"+df.format(sum04));
				}
				if(sum05 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 礼品卡:"+df.format(sum05));
				}
				if(sum06 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 支票:"+df.format(sum06));
				}
				if(sum07 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 代金券:"+df.format(sum07));
				}
				if(sum08 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 保险直付:"+df.format(sum08));
				}
				if(sum09 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 微信:"+df.format(sum09));
				}
				if(sum10 != 0){
					parm.setData("PAY_WAY",countin, parm.getValue("PAY_WAY",countin)+" 支付宝:"+df.format(sum10));
				}
				parm.addData("AMT", sum7);
				table_o.setParmValue(parm);
			}
			
			int suma = 0;
			double sumb = 0;
			int sumc = 0;
			double sumd = 0;
			double sume = 0;
			
			if (inparm.getCount() < 1 & outparm.getCount() < 1) {
				this.messageBox("住院查无数据");
				return;
			} else {
				for (int i = 0; i < outparm.getCount("TIME"); i++) {
					parm1.addData("TIME", outparm.getValue("TIME", i));
				    parm1.addData("COUNT3", inparm.getInt("COUNT3", i));
					parm1.addData("AR_AMT3", inparm.getDouble("AR_AMT3", i));
					parm1.addData("COUNT4", outparm.getInt("COUNT4", i));
					parm1.addData("AR_AMT4", outparm.getDouble("AR_AMT4", i));
					parm1.addData("AMT1", parm1.getDouble("AR_AMT3", i)+ parm1.getDouble("AR_AMT4", i));
					
					int count3 = parm1.getInt("COUNT3", i);
					double aramt3 = parm1.getDouble("AR_AMT3", i);
					int count4 = parm1.getInt("COUNT4", i);
					double aramt4 = parm1.getDouble("AR_AMT4", i);
					double amt1 = parm1.getDouble("AMT1", i);
	
					suma += count3;
					sumb += aramt3;
					sumc += count4;
					sumd += aramt4;
					sume += amt1;
				}
				parm1.addData("TIME", "总计");
				parm1.addData("COUNT3", suma);
				parm1.addData("AR_AMT3", sumb);
				parm1.addData("COUNT4", sumc);
				parm1.addData("AR_AMT4", sumd);
				parm1.addData("AMT1", sume);
	
				table_i.setParmValue(parm1);
			}
			DecimalFormat a = new DecimalFormat("0.00");
			abc.setValue(a.format(sum7+sume));
			abc.setEnabled(false);
		} catch (Exception e) {
			if(isDebug){
				System.out.println("come in class: REGCustomerCollectControl ，method ：onQuery");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 汇出
	 * 
	 * @author Huzc
	 */
	public void onExportO() {
		if (table_o.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table_o, "客户汇总表(门诊)");
	}
	
	public void onExportI() {
		if (table_i.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table_o, "客户汇总表(住院)");
	}

	/**
	 * 清空
	 * 
	 * @author Huzc
	 */
	public void onClear() {
		onInit();
		table_o.removeRowAll();
		table_i.removeRowAll();
		abc.setEnabled(true);
		abc.setValue(null);
	}
	
	/**
	 * 打印
	 * 
	 * @author Huzc
	 */
	public void onPrintO(){
		TParm parm = table_o.getShowParmValue();
		if(parm.getCount() < 1 || parm ==null){
			this.messageBox("没有打印数据");
			return;
		}else{
			parm.addData("SYSTEM", "COLUMNS", "TIME");
			parm.addData("SYSTEM", "COLUMNS", "COUNT1");
			parm.addData("SYSTEM", "COLUMNS", "AR_AMT1");
			parm.addData("SYSTEM", "COLUMNS", "COUNT2");
			parm.addData("SYSTEM", "COLUMNS", "AR_AMT2");
			parm.addData("SYSTEM", "COLUMNS", "PAY_WAY");
			parm.addData("SYSTEM", "COLUMNS", "AMT");
			
			TParm printparm = new TParm();
			
			String monthstart = getValueString("MONTH_START").toString().substring(
					0, 10).replaceAll("-", "/");
			String monthend = getValueString("MONTH_END").toString()
					.substring(0, 10).replaceAll("-", "/");
			printparm.setData("QUERYTIME", "TEXT", "查询时间段:"+monthstart+" - "+monthend);
			
			String nowdate = SystemTool.getInstance().getDate().toString().replace("-", "/");
			printparm.setData("PRINTTIME", "TEXT", "打印时间:"+nowdate.substring(0,10));
			
			printparm.setData("TABLE", parm.getData());
			openPrintWindow("%ROOT%\\config\\prt\\REG\\REGCustomerCollectO", printparm);
		}
	}
	
	public void onPrintI(){
		TParm parm = table_i.getShowParmValue();
		if(parm.getCount() < 1 || parm ==null){
			this.messageBox("没有打印数据");
			return;
		}else{
			parm.addData("SYSTEM", "COLUMNS", "TIME");
			parm.addData("SYSTEM", "COLUMNS", "COUNT3");
			parm.addData("SYSTEM", "COLUMNS", "AR_AMT3");
			parm.addData("SYSTEM", "COLUMNS", "COUNT4");
			parm.addData("SYSTEM", "COLUMNS", "AR_AMT4");
			parm.addData("SYSTEM", "COLUMNS", "AMT1");
			
			TParm printparm = new TParm();
			
			String monthstart = getValueString("MONTH_START").toString().substring(
					0, 10).replaceAll("-", "/");
			String monthend = getValueString("MONTH_END").toString()
					.substring(0, 10).replaceAll("-", "/");
			printparm.setData("QUERYTIME", "TEXT", "查询时间段:"+monthstart+" - "+monthend);
			
			String nowdate = SystemTool.getInstance().getDate().toString().replace("-", "/");
			printparm.setData("PRINTTIME", "TEXT", "打印时间:"+nowdate.substring(0,10));
			
			printparm.setData("TABLE", parm.getData());
			openPrintWindow("%ROOT%\\config\\prt\\REG\\REGCustomerCollectI", printparm);
		}
	}
}
