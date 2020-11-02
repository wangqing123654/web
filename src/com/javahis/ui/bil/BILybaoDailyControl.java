package com.javahis.ui.bil;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: 医保普通病人结算日报明细表
* </p>
* 
* <p>
* Description:医保普通病人结算日报明细表
* </p>
* 
* <p>
* Copyright: Copyright (c) 2014
* </p>
* 
* <p>
* Company:
* </p>
* 
* @author liling 2014-04-22
* 
*/
public class BILybaoDailyControl extends TControl{
	private TTable table;
	 public BILybaoDailyControl() {

		}
		/*
		 * 初始化
		 */
		public void onInit() {
			initPage();
		}

		private void initPage() {
			table = (TTable) this.getComponent("TABLE");
			String now = StringTool.getString(SystemTool.getInstance().getDate(),
					"yyyyMMdd");
			this.setValue("START_DATE", StringTool.getTimestamp(now + "000000",
					"yyyyMMddHHmmss"));// 开始时间
			this.setValue("END_DATE", StringTool.getTimestamp(now + "235959",
					"yyyyMMddHHmmss"));// 结束时间
		//	this.setValue("USER_ID", Operator.getID());

		}
		/**
	     * 汇出Excel
	     */
	    public void onExport() {
	        if (table.getRowCount() > 0)
	            ExportExcelUtil.getInstance().exportExcel(table, "北京市爱育华妇儿医院普通病人结算日报表明细表");
	    }
	    /**
	     * 清空
	     */
	    public void onClear(){
	    	initPage();
	    	table.removeAll();
	    }
	    /**
		 * 查询操作
		 */
		public void onQuery() {
			if (this.getValueString("START_DATE").length() == 0) {
				messageBox("开始时间不正确!");
				return;
			}
			if (this.getValueString("END_DATE").length() == 0) {
				messageBox("结束时间不正确!");
				return;
			}
//			String startDate = StringTool.getString(TypeTool
//					.getTimestamp(getValue("START_DATE")), "yyyyMMddHH24mmss");
//			String endDate = StringTool.getString(TypeTool
//					.getTimestamp(getValue("END_DATE")), "yyyyMMddHH24mmss");
			String startDate = this.getValueString("START_DATE") ;
			String endDate = this.getValueString("END_DATE") ;
			String sql = 
				"Select B.MR_NO,B.IPD_NO, D.PAT_NAME, C.DEPT_CHN_DESC AS  DEPT,B.RECEIPT_NO,B.PAY_BILPAY,B.AR_AMT,"+
				"B.DISCNT_AMT,0.00 AS CASH_IN,B.PAY_CHECK,0.00 AS CASH_OUT,B.PAY_BANK_CARD,0.00 AS REDUCE_AMT,B.TJINS01,B.CHARGE_DATE,B.PAY_CASH "+   
				"from  ADM_INP A,BIL_IBS_RECPM B,SYS_PATINFO D,SYS_DEPT C "+ 
				"where B.MR_NO=D.MR_NO  "+
				"AND B.MR_NO=A.MR_NO "+
				"AND A.DEPT_CODE=C.DEPT_CODE ";
			StringBuilder sbuilder = new StringBuilder(sql) ;
			String sql1="";
			if(startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate)){
	          sql1=" AND B.CHARGE_DATE BETWEEN TO_DATE ('" + SystemTool.getInstance().getDateReplace(startDate, true)+ "'," +
				" 'YYYYMMDDHH24MISS' )" +
				" AND TO_DATE ('" +SystemTool.getInstance().getDateReplace(endDate.substring(0,10), false) + "'," +
				" 'YYYYMMDDHH24MISS' ) order by B.CHARGE_DATE ";				
			}
			sbuilder.append(sql1);
			TParm returnParm = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
//			String time=returnParm.getValue("CHARGE_DATE", returnParm.getCount()-1).substring(0,19);//结账时间 CHARGE_DATE最大值
//			this.setValue("E_DATE", time);
			double payBil=0;double arAmt=0;double discnt=0;double cashIN=0;
			double payCheck=0;double cashOUT=0;double payBank=0;double reduce=0;
			double tjins=0;//待定
			if (returnParm.getCount() <= 0) {
				this.messageBox("没有需要查询的数据");
				table.removeRowAll();
				return;
			}else{/*MR_NO;IPD_NO;PAT_NAME;DEPT;RECEIPT_NO;*/
				/*PAY_BILPAY;AR_AMT;DISCNT_AMT;CASH_IN;PAY_CHECK;CASH_OUT;PAY_BANK_CARD;REDUCE_AMT;TJINS01*/				
				for(int i=0;i<returnParm.getCount();i++){
					double cash=returnParm.getDouble("PAY_CASH", i);
					if(cash<0){
						returnParm.setData("CASH_OUT", i, Math.abs(cash));	
					}else{
						returnParm.setData("CASH_IN", i, Math.abs(cash));
					}
					payBil += returnParm.getDouble("PAY_BILPAY", i);//预交金
					arAmt += returnParm.getDouble("AR_AMT", i);//应缴金额
					discnt += returnParm.getDouble("DISCNT_AMT", i);//折扣金额
					cashIN += returnParm.getDouble("CASH_IN", i);//现金收款
					payCheck += returnParm.getDouble("PAY_CHECK", i);//支票退款
					cashOUT += returnParm.getDouble("CASH_OUT", i);//现金退款
					payBank  += returnParm.getDouble("PAY_BANK_CARD", i);//银行卡退款
					reduce +=returnParm.getDouble("REDUCE_AMT", i);//减免金额
					tjins +=returnParm.getDouble("TJINS01", i);//待定
				}
			}
//			String sql2="Select sum(PAY_BILPAY) AS PAY_BILPAY,sum(AR_AMT) AS AR_AMT,sum(DISCNT_AMT) AS DISCN_AMT,"+
//			"sum(PAY_CHECK) AS PAY_CHECK,sum(PAY_BANK_CARD) AS PAY_BANK_CARD   from  BIL_IBS_RECPM  ";
//			returnParm.addRowData(inParm, inParmRow); //(double)Math.round(dou*100)/100
			int row=returnParm.insertRow();//returnParm新增一行总计行
			returnParm.setData("MR_NO", row, " ");
			returnParm.setData("IPD_NO", row, " ");
			returnParm.setData("PAT_NAME", row, "合计：");
			returnParm.setData("DEPT", row, " ");
			returnParm.setData("RECEIPT_NO", row, " ");
			returnParm.setData("PAY_BILPAY", row, ""+(double)Math.round(payBil*100)/100);
			returnParm.setData("AR_AMT", row, ""+(double)Math.round(arAmt*100)/100);
			returnParm.setData("DISCNT_AMT", row, ""+(double)Math.round(discnt*100)/100);
			returnParm.setData("CASH_IN", row, ""+(double)Math.round(cashIN*100)/100);
			returnParm.setData("PAY_CHECK", row, ""+(double)Math.round(payCheck*100)/100);
			returnParm.setData("CASH_OUT", row, ""+(double)Math.round(cashOUT*100)/100);
			returnParm.setData("PAY_BANK_CARD", row, ""+(double)Math.round(payBank*100)/100);
			returnParm.setData("REDUCE_AMT", row, ""+(double)Math.round(reduce*100)/100);
			returnParm.setData("TJINS01", row, ""+(double)Math.round(tjins*100)/100);
			returnParm.setCount(returnParm.getCount()+1);
			table.setParmValue(returnParm);
		}
		
	    /**
		 * 打印
		 */
		public void onPrint() {
			TTable table = (TTable) this.getComponent("TABLE");
			TParm tableParm = table.getParmValue();
			if (tableParm.getCount() <= 0) {
				this.messageBox("没有需要打印的数据");
				return;
			}
			String sysDate = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyy-MM-dd HH:mm:ss");
		//	String time=tableParm.getValue("CHARGE_DATE", tableParm.getCount()-1);
			String sDate = StringTool.getString(TypeTool
					.getTimestamp(getValue("START_DATE")), "yyyy-MM-dd HH:mm:ss");
			String eDate = StringTool.getString(TypeTool
					.getTimestamp(getValue("END_DATE")), "yyyy-MM-dd HH:mm:ss");
//			String eDate = StringTool.getString(TypeTool
//					.getTimestamp(getValue("E_DATE")), "yyyy-MM-dd HH:mm:ss");//结账时间 CHARGE_DATE最大值
			TParm parm = new TParm();
			tableParm.addData("SYSTEM", "COLUMNS", "MR_NO"); 
			tableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
			tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			tableParm.addData("SYSTEM", "COLUMNS", "DEPT");
			tableParm.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
			tableParm.addData("SYSTEM", "COLUMNS", "PAY_BILPAY");
			tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT");
			/*DISCNT_AMT;CASH_IN;PAY_CHECK;CASH_OUT;PAY_BANK_CARD;REDUCE_AMT*/
			tableParm.addData("SYSTEM", "COLUMNS", "DISCNT_AMT");
			tableParm.addData("SYSTEM", "COLUMNS", "CASH_IN");
			tableParm.addData("SYSTEM", "COLUMNS", "PAY_CHECK");
			tableParm.addData("SYSTEM", "COLUMNS", "CASH_OUT");
			tableParm.addData("SYSTEM", "COLUMNS", "PAY_BANK_CARD");
			tableParm.addData("SYSTEM", "COLUMNS", "REDUCE_AMT");
			tableParm.addData("SYSTEM", "COLUMNS", "TJINS01");
			//parm.setData("TITLE", "TEXT", "北京市爱育华妇儿医院普通病人结算日报表明细表");			
			parm.setData("CASHER", "TEXT", "");//收费人
			parm.setData("ACCOUNTER", "TEXT", Operator.getName());//结算员
			parm.setData("END_DATE", "TEXT", sDate+" ~ "+eDate);//结账时间
			parm.setData("DATE", "TEXT", sysDate);//打印时间
			parm.setData("TABLE", tableParm.getData());
			this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILybaoDaily.jhw",parm);
		}
	 
}
